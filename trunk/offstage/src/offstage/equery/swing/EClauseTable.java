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
 * GroupsTable.java
 *
 * Created on March 19, 2005, 12:00 AM
 */

package offstage.equery.swing;

import javax.swing.table.*;
import javax.swing.*;
import javax.swing.event.*;
import java.sql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import citibob.swing.typed.*;
import citibob.swing.*;
import java.util.*;
import offstage.equery.EQuerySchema;


/**
 *
 * @author citibob
 */


public class EClauseTable extends citibob.swing.CitibobJTable {
	

EQuerySchema schema;

KeyedTableCellRenderer idRenderer;
//TypedWidgetCellEditor idEditor;
TableCellEditor idEditor;
 
/** Creates a new instance of GroupsTable */
public EClauseTable() {
	setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
}


public void initRuntime(EClauseTableModel model)
throws java.sql.SQLException
{
	setModel(model);
	EQuerySchema schema = model.getSchema();

	TableColumn c;
	TableCellRenderer re;
	TableCellEditor ed;
	TableColumnModel cols = getColumnModel();

System.out.println("Setting column renderers");
	// Set up Column 0
	c = cols.getColumn(EClauseTableModel.C_COLUMN);
	re = new EClauseTableModel.ColNameRenderer(schema);
	c.setCellRenderer(re);
	ed = new EClauseColumnEditor(schema);
	c.setCellEditor(ed);

	// Set up Column 1
	c = cols.getColumn(EClauseTableModel.C_COMPARE);
	re = new EClauseTableModel.CompareRenderer();
	c.setCellRenderer(re);
	ed = new EClauseCompareEditor(schema);
	c.setCellEditor(ed);

	// Set up Column 2
	c = cols.getColumn(EClauseTableModel.C_VALUE);
	re = new EClauseTableModel.ValueRenderer(schema);
	c.setCellRenderer(re);
	ed = new EClauseValueEditor(schema);
	c.setCellEditor(ed);
}


// ==========================================================

// ==========================================================
// ==========================================================
// ==========================================================


/*
String[] xColNames = new String[] {};
String[] xSColMap = new String[] {};
String idTableName;
public String[] getColNames()
	{ return xColNames; }
public String[] getColMap()
	{ return xSColMap; }
public void setColNames(String[] s)
	{ xColNames = s; }
public void setColMap(String[] s)
	{ xSColMap = s; }
public String getIDTableName()
	{ return idTableName; }
public void setIDTableName(String s)
	{ idTableName = s; }
*/
// ------------------------------------------------------


	
}
