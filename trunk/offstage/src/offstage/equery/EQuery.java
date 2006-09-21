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
import citibob.jschema.*;
import java.sql.*;
import java.io.*;
import com.thoughtworks.xstream.*;
import offstage.db.TestConnPool;

public class EQuery
{

// Info on the query
ArrayList clauses = new ArrayList();

// ============================================
/** Inserts clause before clause #ix */
public void insertClause(int ix, Clause c)
	{ clauses.add(ix, c); }
public void appendClause(Clause c)
	{ clauses.add(c); }
public Clause removeClause(int ix)
	{ return (Clause)clauses.remove(ix); }
public Clause getClause(int n)
{
	return (Clause)clauses.get(n);
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
public void writeSqlQuery(EQuerySchema schema, SqlQuery sql)
{
	String cwhere = "(1=0";
	for (Iterator ii=clauses.iterator(); ii.hasNext(); ) {
		Clause clause = (Clause)ii.next();
		List elements = clause.elements;
		String ewhere = "(1=1";
		for (Iterator jj=elements.iterator() ; jj.hasNext(); ) {
			Element e = (Element)jj.next();
			ColName cn = e.colName;
			Column c = ((EQuerySchema.Col)schema.getCol(cn)).col;
			if (!sql.containsTable(e.colName.getTable())) {
				String joinClause = ((EQuerySchema.Tab)schema.getTab(cn.getTable())).joinClause;
				sql.addWhereClause(joinClause);
				sql.addTable(cn.getTable());
			}
			ewhere = ewhere + " and\n" +
				e.colName.toString() + " " + e.comparator + " " +
				" (" + c.getType().toSql(e.value) + ")";
		}
		ewhere = ewhere + ")";
		String joiner = (clause.type == Clause.ADD ? "or" : "and not");
		cwhere = "(" + cwhere + ") " + joiner + " \n" + ewhere;
	}
	cwhere = cwhere + ")";
	sql.addWhereClause(cwhere);
}
// ------------------------------------------------------
public String getSql(EQuerySchema eqs)
{
	SqlQuery sql = new SqlQuery();
	this.writeSqlQuery(eqs, sql);
	sql.addTable("entities as main");
	sql.addColumn("main.entityid");
	sql.setDistinct(true);
	return sql.getSelectSQL();	
}
// ------------------------------------------------------
//public static void main(String[] args) throws Exception
//{
//	
//	ColName a = new ColName("tab",  "col");
//	HashMap map = new HashMap();
//	map.put(a, a);
//	ColName b = new ColName("tab.col");
//	System.out.println(a.equals(b));
//	System.out.println(map.get(b));
//	System.out.println(map.get(a));
//	if (true) return;
//	
//	Connection db = new TestConnPool().checkout();
//	Statement st = db.createStatement();
//	offstage.schema.OffstageSchemaSet sset = new offstage.schema.OffstageSchemaSet(st, null);
//	EQuerySchema eqs = new EQuerySchema(st, sset);
//	EQuery eq = new EQuery();
//	eq.newClause();
//	eq.addElement("phones", "phone", "=", "617-308-0436 yyy");
//	eq.addElement("phones", "groupid", "=", new Integer(109));
//	eq.newClause();
//	eq.addElement("donations", "groupid", "=", new Integer(169));
//	eq.newClause();
//	eq.addElement("persons", "lastname", "like", "%Fischer%");
//	SqlQuery sql = new SqlQuery();
//	eq.writeSqlQuery(eqs, sql);
//	sql.addTable("entities as main");
//	sql.addColumn("main.entityid");
//	sql.setDistinct(true);
//	String ssql = sql.getSelectSQL();
//	System.out.println(ssql);
//
//	// Serialize using XML
//	FileWriter fout = new FileWriter("test.xml");
//	XStream xs = new XStream();
//	ObjectOutputStream oos = xs.createObjectOutputStream(fout);
//	oos.writeObject(eq);
//	oos.close();
//
//}
}
