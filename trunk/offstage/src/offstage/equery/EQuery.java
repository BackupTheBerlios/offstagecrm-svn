/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2007 by Robert Fischer

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
public void writeSqlQuery(QuerySchema schema, ConsSqlQuery sql)
{
	String cwhere = "(1=0";
	for (Iterator ii=clauses.iterator(); ii.hasNext(); ) {
		EClause clause = (EClause)ii.next();
		List elements = clause.elements;
		if (elements.size() == 0) continue;
		StringBuffer ewhere = null;
		for (Iterator jj=elements.iterator() ; jj.hasNext(); ) {
			Element e = (Element)jj.next();
			ColName cn = e.colName;
System.out.println("cn = " + cn);
			QuerySchema.Col qsc = (QuerySchema.Col) schema.getCol(cn);
System.out.println("qsc = " + qsc);
			Column c = qsc.col;
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
	if (lastUpdatedFirst != null) sql.addWhereClause(
		"main.lastupdated >= " + SqlTimestamp.gmt(lastUpdatedFirst));
	if (lastUpdatedNext != null) sql.addWhereClause(
		"main.lastupdated < " + SqlTimestamp.gmt(lastUpdatedNext));
}
// ------------------------------------------------------
/** Returns the mailing id */
public void makeMailing(SqlRunner str, String queryName, EQuerySchema schema,
final UpdRunnable rr) throws SQLException
{
	String eqXml = toXML();
	String eqSql = getSql(schema);

	// Create the mailing list and insert EntityID records
	DB.w_mailingids_create(str, queryName, eqXml, eqSql, rr);
}

}
