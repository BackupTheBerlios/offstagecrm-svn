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
/*
 * MailingDbModel.java
 *
 * Created on July 11, 2005, 10:31 PM
 */

package offstage.equery.swing;
import citibob.jschema.*;
import java.sql.*;
import java.sql.*;
import java.io.*;
import net.sf.jasperreports.engine.*;
import java.util.*;
import javax.swing.event.*;
import offstage.db.DB;
import offstage.schema.EQueriesSchema;
import offstage.schema.MailingidsSchema;
import citibob.multithread.*;
import offstage.schema.*;
import offstage.*;
import offstage.equery.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;

/**
 *
 * @author citibob
 */
public class EQueryModel2
{

SchemaBufDbModel equeries;		// Set to entire equery info
//IntKeyedDbModel oneQuery;		// One specific query...

//protected SchemaBufDbModel oneQuery;		// Row currently being edited...
protected SchemaBufRowModel oneQueryRow;		// Row currently being edited...
protected EQueryTableModel2 oneQueryTableModel;	// Query editor model for row currently being edited...
protected EQuerySchema schema;
protected String xmlBlankQuery;			// XML for a blank query to insert...
MailingModel2 mailingModel;

ActionRunner runner;

//public SchemaBufDbModel getEQueriesDb()
//	{ return equeries; }

/** Creates a new instance of MailingDbModel */
public EQueryModel2(final Statement st, MailingModel2 mailingModel, OffstageSchemaSet sset)
throws SQLException
{
	this.mailingModel = mailingModel;
	this.runner = runner;
	
	// Init XML for a blank query
	Clause c = new Clause();
	c.elements.add(new Element());
	EQuery q = new EQuery();
	q.appendClause(c);
	xmlBlankQuery = q.toXML();
			
	schema = new EQuerySchema(st, sset);
	SchemaBuf sb = new SchemaBuf(sset.equeries) {
		/** Automatically set lastupdated every time row saved to DB. */
		public void getUpdateCols(int row, SqlQuery q, boolean updateUnchanged)
		{
			super.getUpdateCols(row, q, updateUnchanged);
			q.addColumn("lastmodified", "now()");
		}

		/** Automatically set lastupdated every time row saved to DB. */
		public void getInsertCols(int row, SqlQuery q, boolean insertUnchanged)
		{
			super.getInsertCols(row, q, insertUnchanged);
			q.addColumn("lastmodified", "now()");
		}
	};
	equeries = new SchemaBufDbModel(sb, null);
	equeries.setInsertBlankRow(true);
	equeries.setUpdateBufOnUpdate(true);
//	oneQuery = new IntKeyedDbModel(new SchemaBuf(sset.equeries), "equeries", false);
	oneQueryRow = new SchemaBufRowModel(equeries.getSchemaBuf());
	oneQueryTableModel = new EQueryTableModel2(new EQuerySchema(st, sset));
	//equeries.setWhereClause("created >= now() - interval '30 days'");
	equeries.setOrderClause("name");
	equeries.doSelect(st);
}

public void saveCurQuery(Statement st) throws SQLException
{
	// Make sure it's selected in the GUI
	int oldRow = oneQueryRow.getCurRow();
	if (oldRow < 0) return;
	
	// Save any changes...
	equeries.doUpdate(st, oldRow);
}

public void undoCurQuery()
{
	int col = oneQueryRow.findColumn("equery");
	oneQueryRow.resetValue(col);
}

public void makeMailing(Statement st, EQuery eqy, String queryName) throws SQLException
{
	if (eqy == null) return;
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
	
	mailingModel.getMailingidsDb().doSelect(st);
//	mailingModel.setMailingID(xmailingID);
}
// ===================================================
}
