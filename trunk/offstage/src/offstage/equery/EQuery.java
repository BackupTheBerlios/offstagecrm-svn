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
import offstage.db.TestConnPool;

public class EQuery
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

///** Convenience: add an element to the last clause */
//public void addElement(Element e)
//{
//	Clause clause = (Clause)clauses.get(clauses.size() - 1);
//	clause.addElement(e);
//}
///** Convenience: add an element to the last clause */
//public void addElement(String table, String col, String comparator, Object value)
//{
//	Element e = new Element(new ColName(table,col),comparator,value);
//	addElement(e);
//}
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
		String ewhere = "(1=1";
		for (Iterator jj=elements.iterator() ; jj.hasNext(); ) {
			Element e = (Element)jj.next();
			ColName cn = e.colName;
			Column c = (((QuerySchema.Col) schema.getCol(cn)).col);
			if (!sql.containsTable(e.colName.getTable())) {
				String joinClause = (((QuerySchema.Tab) schema.getTab(cn.getTable()))).joinClause;
				sql.addWhereClause(joinClause);
				sql.addTable(cn.getTable());
			}
			ewhere = ewhere + " and\n" +
				e.colName.toString() + " " + e.comparator + " " +
				" (" + c.getType().toSql(e.value) + ")";
		}
		ewhere = ewhere + ")";
		String joiner = (clause.type == EClause.ADD ? "or" : "and not");
		cwhere = "(" + cwhere + ") " + joiner + " \n" + ewhere;
	}
	cwhere = cwhere + ")";
	sql.addWhereClause(cwhere);
	
	// Add where clause for lastupdated date range
	if (lastUpdatedFirst != null) sql.addWhereClause("main.lastupdated >= " + SqlTimestamp.sql(lastUpdatedFirst));
	if (lastUpdatedNext != null) sql.addWhereClause("main.lastupdated < " + SqlTimestamp.sql(lastUpdatedNext));
}
// ------------------------------------------------------
public String getSql(EQuerySchema qs)
{
	SqlQuery sql = new SqlQuery();
	this.writeSqlQuery(qs, sql);
	sql.addTable("entities as main");
	sql.addColumn("main.entityid");
	sql.setDistinct(true);
	return sql.getSelectSQL();	
}
// ------------------------------------------------------
/** Sets the value.  Same as method in JFormattedTextField.  Fires a
 * propertyChangeEvent("value") when calling setValue() changes the value. */
public static EQuery fromXML(String squery)
{
	if (squery == null) return null;
	
	Object obj = null;
	try {
		StringReader fin = new StringReader(squery);
		EQueryXStream xs = new EQueryXStream();
		ObjectInputStream ois = xs.createObjectInputStream(fin);
		obj = ois.readObject();
	} catch(ClassNotFoundException e) {
		return null;
//		throw new IOException("Class Not Found in Serialized File");
	} catch(com.thoughtworks.xstream.io.StreamException se) {
		return null;
//		throw new IOException("Error reading serialized file");
	} catch(IOException e) {}	// won't happen
	
	if (obj == null) {
		return null;
	} else if (!(obj instanceof EQuery)) {
		return null;
//		throw new IOException("Wrong object of class " + obj.getClass() + " found in EQuery file");
	} else {
		return (EQuery)obj;
	}
}

public String toXML()
{
	// Serialize using XML
	StringWriter fout = new StringWriter();
	EQueryXStream xs = new EQueryXStream();
	try {
		ObjectOutputStream oos = xs.createObjectOutputStream(fout);
		oos.writeObject(this);
		oos.close();
	} catch(IOException e) {}	// won't happen
	return fout.getBuffer().toString();
}

}
