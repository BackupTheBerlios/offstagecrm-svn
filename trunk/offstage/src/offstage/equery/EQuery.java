/*
Offstage CRM: Enterprise Database for Arts Organizations
This file Copyright (c) 2006 by Robert Fischer

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package offstage.equery;

import java.util.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import java.sql.*;
import java.io.*;
import com.thoughtworks.xstream.*;
import offstage.db.*;

public class EQuery extends Query
{

// Info on the query
ArrayList clauses = new ArrayList();
java.util.Date lastUpdatedFirst;
java.util.Date lastUpdatedNext;
// ============================================

public void setLastUpdatedFirst(java.util.Date dt)
	{ this.lastUpdatedFirst = dt; }
public void setLastUpdatedNext(java.util.Date dt)
	{ this.lastUpdatedNext = dt; }
public java.util.Date getLastUpdatedFirst()
	{ return this.lastUpdatedFirst; }
public java.util.Date getLastUpdatedNext()
	{ return this.lastUpdatedNext; }


/** Inserts clause before clause #ix */
public void insertClause(int ix, EClause c)
	{ clauses.add(ix, c); }
public void appendClause(EClause c)
	{ clauses.add(c); }
public EClause removeClause(int ix)
	{ return (EClause)clauses.remove(ix); }
public EClause getClause(int n)
{
	return (EClause)clauses.get(n);
}

public int getNumClauses()
	{ return clauses.size(); }

public ArrayList getClauses()
	{ return clauses; }
// -----------------------------------------------
/** Creates a standard SqlQuery out of the data in this query. */
public void writeSqlQuery(QuerySchema schema, SqlQuery sql)
{
	String cwhere = "(1=0";
	for (Iterator ii=clauses.iterator(); ii.hasNext(); ) {
		EClause clause = (EClause)ii.next();
		List elements = clause.elements;
		StringBuffer ewhere = null;
		for (Iterator jj=elements.iterator() ; jj.hasNext(); ) {
			Element e = (Element)jj.next();
			ColName cn = e.colName;
			Column c = (((QuerySchema.Col) schema.getCol(cn)).col);
			addTable(schema, sql, cn);
//			if (!sql.containsTable(e.colName.getTable())) {
//				String joinClause = (((QuerySchema.Tab) schema.getTab(cn.getTable()))).joinClause;
//				sql.addWhereClause(joinClause);
//				sql.addTable(cn.getTable());
//			}
			if (ewhere == null) ewhere = new StringBuffer("(");
			else ewhere.append(" and\n");
			if (clause.type == EClause.SUBTRACT) {
				// subtle outer join semantics issue...
				ewhere.append(e.colName.toString() + " is not null and ");
			}
			ewhere.append(e.colName.toString() + " " + e.comparator + " " +
				" (" + c.getType().toSql(e.value) + ")");
		}
		ewhere.append(")");
		String joiner = (clause.type == EClause.ADD ? "or" : "and not");
		cwhere = "(" + cwhere + ") " + joiner + " \n" + ewhere.toString();
	}
	cwhere = cwhere + ")";
	sql.addWhereClause(cwhere);
	
	// Add where clause for lastupdated date range
	if (lastUpdatedFirst != null) sql.addWhereClause("main.lastupdated >= " + SqlTimestamp.sql(lastUpdatedFirst));
	if (lastUpdatedNext != null) sql.addWhereClause("main.lastupdated < " + SqlTimestamp.sql(lastUpdatedNext));
}
// ------------------------------------------------------
/** Returns the mailing id */
public int makeMailing(Statement st, String queryName, EQuerySchema schema) throws SQLException
{
	EQuery eqy = this;
//	if (eqy == null) return;
	String eqXml = eqy.toXML();
	String eqSql = eqy.getSql(schema);

	String sql;

	// Create the mailing list and insert EntityID records
//	sql = "select w_mailingids_create(" + SqlString.sql(eqXml) + ", " + SqlString.sql(eqSql) + ")";
//	int xmailingID = SQL.readInt(st, sql);
	int xmailingID = DB.w_mailingids_create(st, eqXml, eqSql);
System.out.println("Created Mailing list ID: " + xmailingID);
	sql = "select w_mailings_correctlist(" + SqlInteger.sql(xmailingID) + ", FALSE)";
	st.executeQuery(sql);
	sql = "update mailingids set name = " + SqlString.sql(queryName) + " where groupid = " + xmailingID;
	st.executeUpdate(sql);
	return xmailingID;
}

}
