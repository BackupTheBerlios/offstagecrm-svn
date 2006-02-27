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
 * EClauseTableModel.java
 *
 * Created on June 23, 2005, 10:34 PM
 */

package offstage.equery.swing;

import citibob.swing.*;
import citibob.swing.table.*;
import citibob.swing.typed.*;
import java.sql.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import citibob.jschema.*;
import offstage.equery.EQuery;
import offstage.equery.EQuerySchema;

/**
 *
 * @author citibob
 */
public class EClauseTableModel extends AbstractTableModel
implements CitibobTableModel, EClauseTableConst
{

EQuerySchema schema;
EQuery.Clause clause;
/** Creates a new instance of EClauseTableModel */
public EClauseTableModel(EQuerySchema schema, EQuery.Clause clause)
{
	this.schema = schema;
	this.clause = clause;
}
public EClauseTableModel(EQuerySchema schema)
{
	this.schema = schema;
	this.clause = null;
}
public EQuerySchema getSchema()
{
	return schema;
}
public EQuery.Clause getClause()
	{ return clause; }
public void setClause(EQuery.Clause clause)
{
	this.clause = clause;
	this.fireTableDataChanged();
}
public void addElement()
{
	if (clause == null) return;
	clause.elements.add(new EQuery.Element());
	int idx = clause.elements.size() - 1;
	fireTableRowsInserted(idx,idx);
}
public void removeElement(int idx)
{
	if (clause == null) return;
	if (clause.elements.size() <= idx) return;
	if (idx < 0) return;
	clause.elements.remove(idx);
	fireTableRowsDeleted(idx,idx);
}
// ===============================================================
// Implementation of TableModel

// --------------------------------------------------
public String getColumnName(int column) 
{
	  switch(column) {
		  case C_COLUMN : return S_COLUMN;
		  case C_COMPARE : return S_COMPARE;
		  case C_VALUE : return S_VALUE;
	  }
	  return null;	
}
public int findCol(String colName)
{
	if (S_COLUMN.equals(colName)) return C_COLUMN;
	if (S_COMPARE.equals(colName)) return C_COMPARE;
	if (S_VALUE.equals(colName)) return C_VALUE;
	return -1;
}
// --------------------------------------------------
/** Allow editing of all non-key fields. */
public boolean isCellEditable(int rowIndex, int columnIndex)
	{ return (clause != null); }
// --------------------------------------------------
/** Set entire row.  Normally, setValueAt() will be called with a modified
version of the object retrieved from getValueAt(). */
public void setValueAt(Object val, int rowIndex, int colIndex)
{
/*
	EQuery.Element el = (EQuery.Element)clause.get(rowIndex);
	switch(colIndex) {
		case C_COLUMN :
			EQuery.ColName cn = (EQuery.ColName)val;
			el.colName = cn;
		break;
		case C_COMPARE :
			String s = (String)val;
			el.comparator = s;
		break;
		case C_VALUE :
			el.value = val;
		break;
	}
*/
	if (clause == null) return;
	clause.elements.set(rowIndex, val);
	// Redisplay the entire row!
	this.fireTableRowsUpdated(rowIndex,rowIndex);
}
// --------------------------------------------------
	public int getRowCount()
	  { return (clause == null ? 0 : clause.elements.size()); }
	public int getColumnCount()
	  { return 3; }
	public Object getValueAt(int row, int column)
	{
		return (clause == null ? null : clause.elements.get(row));
/*
		EQuery.Element el = (EQuery.Element)clause.get(row);
	  switch(column) {
		  case C_COLUMN : return el.colName;
		  case C_COMPARE : return el.comparator;
		  case C_VALUE : return el.value;
	  }
	  return null;
*/
  }
public Class getColumnClass(int columnIndex) 
{
	return EQuery.Element.class;
/*
	switch(columnIndex) {
		case C_COLUMN : return EQuery.ColName.class;
		case C_COMPARE : return String.class;
		case C_VALUE : return Object.class;
	}
	return null;
*/
}
// ===============================================================
// Implementation of CitibobTableModel (prototype stuff)
java.util.List proto;
public java.util.List getPrototypes()
	{ return proto; }
public void setPrototypes(java.util.List proto)
	{ this.proto = proto; }
public void setPrototypes(Object[] pr)
{
	proto = new ArrayList(pr.length);
	for (int i = 0; i < pr.length; ++i) {
		proto.add(pr[i]);
	}
}
// ===============================================================
public static class ColNameRenderer
extends DefaultTableCellRenderer {
	EQuerySchema schema;
	public ColNameRenderer(EQuerySchema schema)
	{
		this.schema = schema;
	}
	public void setValue(Object o) {
System.out.println("ColNameRenderer");
		if (o instanceof EQuery.Element) {
			EQuery.Element e = (EQuery.Element)o;
			// EQuery.ColName ccn = e.colName;
//System.out.println("colName = " + e.colName);
			EQuerySchema.Col col = schema.getCol(e.colName);
			setText(col == null ? "<none>" : col.getViewName());
		} else {
			setText("<ERROR>");
		}
	}
}
public static class CompareRenderer
extends DefaultTableCellRenderer {
	public void setValue(Object o) {
		if (o instanceof EQuery.Element) {
			EQuery.Element e = (EQuery.Element)o;
			setText(e.comparator == null ? "<none>" : e.comparator);
		} else {
			setText("<ERROR>");
		}
	}
}
public static class ValueRenderer
extends DefaultTableCellRenderer {
	EQuerySchema schema;
	public ValueRenderer(EQuerySchema schema)
		{ this.schema = schema; }
	public void setValue(Object o) {
		if (o instanceof EQuery.Element) {
			EQuery.Element e = (EQuery.Element)o;
			if (e.colName == null) {
				setText("<null>");
			} else {
				EQuerySchema.Col col = schema.getCol(e.colName);
				KeyedModel kmodel = col.kmodel;
				if (kmodel != null) {
					if (e.value == null) {
						setText("<null>");
					} else {
						Object oval = kmodel.get(e.value);
						if (oval == null) setText("<" + e.value.toString() + ">");
						else setText(oval.toString());
					}
				} else {
					setText(e.value == null ? "<null>" : e.value.toString());
				}
			}
		} else {
			setText("<ERROR>");
		}
	}
}
// ===========================================================
}
