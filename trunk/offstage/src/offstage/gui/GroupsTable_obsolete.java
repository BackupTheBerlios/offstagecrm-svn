///*
//OffstageArts: Enterprise Database for Arts Organizations
//This file Copyright (c) 2005-2007 by Robert Fischer
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.
//*/
///*
// * GroupsTable.java
// *
// * Created on March 19, 2005, 12:00 AM
// */
//
//package offstage.gui;
//
//import javax.swing.table.*;
//import javax.swing.*;
//import javax.swing.event.*;
//import java.sql.*;
//import citibob.jschema.*;
//import citibob.swing.table.*;
//import citibob.swing.typed.*;
//import citibob.swing.*;
//import citibob.sql.*;
////import offstage.schema.GroupTypeKeyedModel;
//
///**
// * TODO: MAKE THIs subclass from citibob.jschema.gui.TypedItemTable and TypedItemPanel
// * @author citibob
// */
//public class GroupsTable_obsolete extends CitibobJTable {
//	
////KeyedTableCellRenderer idRenderer;
////TypedWidgetCellEditor idEditor;
////TableCellEditor idEditor;
// 
///** Creates a new instance of GroupsTable */
//public GroupsTable_obsolete() {
//	setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//	
////	this.setDefaultRenderEdit(java.util.Date.class, new DateRenderEdit("yyyy-MM-dd"));
//}
//
//String[] xColNames = new String[] {};
//String[] xSColMap = new String[] {};
//String idTableName;
//public String[] getColNames()
//	{ return xColNames; }
//public String[] getColMap()
//	{ return xSColMap; }
//public void setColNames(String[] s)
//	{ xColNames = s; }
//public void setColMap(String[] s)
//	{ xSColMap = s; }
//public String getIDTableName()
//	{ return idTableName; }
//public void setIDTableName(String s)
//	{ idTableName = s; }
//
////		new String[] {"S", "Type", "Phone"},
////		new String[] {"__status__", "groupid", "phone"});
//
//public void initRuntime(SqlRunner str, SchemaBuf phonesSb)
//throws java.sql.SQLException
//{
//	// Prepend 2 columns to column list
//	String[] colNames = new String[xColNames.length + 2];
//	String[] sColMap = new String[xSColMap.length + 2];
//	colNames[0] = "Status";
//	sColMap[0] = "__status__";
//	colNames[1] = "Type";
//	sColMap[1] = "groupid";
//	for (int i=0; i<xColNames.length; ++i) {
//		colNames[i+2] = xColNames[i];
//		sColMap[i+2] = xSColMap[i];
////		System.out.println("   column " + colNames[i+2]);
//	}
//
//	// Set it up
//	StatusSchemaBuf ssb = new StatusSchemaBuf(phonesSb);
//	ColPermuteTableModel model = new ColPermuteTableModel(
//		ssb, colNames, sColMap, null);
//System.out.println("model = " + model);
//	setModel(model);
//	
////	setRenderEdit(model.findColumnU("groupid"),
////		new KeyedRenderEdit(new GroupTypeKeyedModel(st, idTableName)));
////		new KeyedRenderEdit(new KeyedM
////				GroupTypeKeyedModel(st, idTableName)));
//	setRenderEdit(model.findColumnU("__status__"),
//		new citibob.swing.table.StatusRenderEdit());
//}
//
//	
//}
