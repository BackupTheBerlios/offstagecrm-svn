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
import citibob.jschema.pgsql.*;
import offstage.schema.OrgSchema;
import offstage.schema.NotesSchema;
import offstage.schema.PersonsSchema;
import offstage.schema.PhonesSchema;
import offstage.schema.EventsSchema;
import offstage.schema.DonationsSchema;
import offstage.schema.GroupTypeKeyedModel;
import offstage.schema.EntitiesSchema;

public class EQuerySchema
{

// Info on the fields we can process; set up by initializer
HashMap cols = new HashMap();			// Maps "table.col" -> Col
LinkedList colList = new LinkedList();	// Holds same columns as cols, in order of insertion
HashMap tabs = new HashMap();	// Maps "table" -> String
HashMap typeComparators = new HashMap();	// Maps SqlType --> list of comparators
// -----------------------------------------------
public static class Col
{
	public String table;
	public Column col;
	private String viewName = null;					// Name user knows this column by
	public KeyedModel kmodel = null;		// For enumerated column types; null if not used
	public String[] comparators;			// List of Strings, which can be used in SQL to compare this field.
	void setViewName(String s)
		{ this.viewName = s; }
	boolean hasViewName()
		{ return viewName != null; }
	public String getViewName()
	{
		if (viewName == null) return "---" + table + "." + col.getName();
		return viewName;
	}
}
public static class Tab
{
	public Schema schema;
	public String table;
	public String joinClause;
}
// -----------------------------------------------
private String colKey(String table, String col)
	{ return table + "." + col; }
public Tab getTab(String table)
	{ return (Tab)tabs.get(table); }
public Col getCol(String table, String scol)
	{ return (Col)cols.get(colKey(table, scol)); }
public Col getCol(EQuery.ColName cname)
{
	if (cname == null) return null;
	return (Col)cols.get(colKey(cname.stable, cname.scol));
}
public Iterator colIterator()
	{ return colList.iterator(); }
public void setKeyedModel(String table, String scol, KeyedModel kmodel)
{
	Col col = getCol(table,scol);
	col.kmodel = kmodel;
	col.comparators = new String[] {"=", "<>"};
}
public EQuerySchema(Statement st) throws SQLException
{
	typeComparators.put(SqlBool.class, new String[] {"="});
	typeComparators.put(SqlDate.class, new String[] {"=", ">", "<", ">=", "<=", "<>"});
	typeComparators.put(SqlInteger.class, new String[] {"=", ">", "<", ">=", "<=", "<>"});
	typeComparators.put(SqlString.class, new String[] {"=", "<>", "ilike", "not ilike", "similar to", "not similar to"});
	typeComparators.put(SqlTimestamp.class, new String[] {"=", ">", "<", ">=", "<=", "<>"});
	cols = new HashMap();
	addSchema(new EntitiesSchema(),
		"entities.entityid = main.entityid");
	addSchema(new OrgSchema(),
		"organizations.entityid = main.entityid");
	addSchema(new PersonsSchema(),
		"persons.entityid = main.entityid");
	addSchema(new EventsSchema(),
		"events.entityid = main.entityid");
	setKeyedModel("events", "groupid", new GroupTypeKeyedModel(st, "eventids"));
	addSchema(new DonationsSchema(),
		"donations.entityid = main.entityid");
	setKeyedModel("donations", "groupid", new GroupTypeKeyedModel(st, "donationids"));
	addSchema(new NotesSchema(),
		"notes.entityid = main.entityid");
	setKeyedModel("notes", "groupid", new GroupTypeKeyedModel(st, "noteids"));
	addSchema(new PhonesSchema(),
		"phones.entityid = main.entityid");
	setKeyedModel("phones", "groupid", new GroupTypeKeyedModel(st, "phoneids"));
	doAlias();
}
private void addSchema(Schema sc, String joinClause, String table)
{
	if (table == null) table = sc.getDefaultTable();
	Tab tab = new Tab();
	tab.schema = sc;
	tab.joinClause = joinClause;
	tab.table = table;
	tabs.put(table, tab);
	for (int i=0; i<sc.getColCount(); ++i) {
		Col col = new Col();
		col.col = sc.getCol(i);
		Class colClass = col.col.getType().getClass();
		col.comparators = (String[]) typeComparators.get(colClass);
		col.table = table;
System.out.println("Adding to Schema: " + colKey(col.table, col.col.getName()));
		cols.put(colKey(col.table, col.col.getName()), col);
		colList.add(col);
	}
}
private void addSchema(Schema sc, String joinClause)
	{ addSchema(sc,joinClause,null); }

// --------------------------------------------------------------------
static final String[] alias = {
	"persons.firstname", "firstname",
	"persons.middlename", "middlename",
	"persons.lastname", "lastname",
	"persons.gender", "gender",
	"persons.email", "email",
	"persons.occupation", "occupation",
	"entities.address1", "address1",
	"entities.address2", "address2",
	"entities.city", "city",
	"entities.state", "state",
	"entities.zip", "zip",
	"entities.country", "country",
	"entities.lastupdated", "lastupdated",
	"entities.sendmail", "sendmail",
	"organizations.name", "org-name",
	"events.groupid", "event-type",
	"events.role", "event-role",
	"donations.groupid", "donation-type",
	"donations.date", "donation-date",
	"donations.amount", "donation-amount",
	"notes.groupid", "note-type",
	"notes.date", "note-date",
	"notes.note", "note",
	"phones.groupid", "phone-type",
	"phones.phone", "phone",
	"entities.entityid", "entityid",
	"entities.obsolete", "obsolete",
};

/** Sets up column name aliases, and removes all non-aliased coumns. */
void doAlias()
{
	// Set aliases
	HashMap newCols = new HashMap();
	colList.clear();
	for (int i=0; i<alias.length; i+=2) {
		String cname = alias[i];
		String vname = alias[i+1];
		Col col = (Col)cols.get(cname);
		if (col == null) continue;
		col.setViewName(vname);
		newCols.put(colKey(col.table, col.col.getName()), col);
		colList.add(col);
	}

	// Remove non-aliased columns
	cols = newCols;
//	for (Iterator ii=cols.values().iterator(); ii.hasNext(); ) {
//		Col col = (Col)ii.next();
//		if (!col.hasViewName()) ii.remove();
//	}
}
}
