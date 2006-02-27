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

/**
 *
 * @author citibob
 */
public class EQueryTableModel extends AbstractTableModel
implements CitibobTableModel
{

EQuery query;
EClauseTableModel clauseModel;		// Allows us to control current clause.
int curRow;							// Index of current row in our model --- the clause we're currently editing

/** Creates a new instance of EClauseTableModel */
public EQueryTableModel(EClauseTableModel clauseModel)
{
	this.query = null;
	this.clauseModel = clauseModel;
	curRow = -1;
}
public EQuery getQuery()
	{ return query; }
public void setCurRow(int curRow)
{
	this.curRow = curRow;
	EQuery.Clause c;
	if (curRow == -1 || query == null) {
		c = null;
	} else {
		c = query.getClause(curRow);
	}
	clauseModel.setClause(c);
}
public void setQuery(EQuery query)
{
	this.query = query;
	this.fireTableDataChanged();
	setCurRow(getRowCount() > 0 ? 0 : -1);
}
public void addClause()
{
	if (query == null) return;
	query.getClauses().add(new EQuery.Clause());
	int idx = query.getClauses().size() - 1;
	fireTableRowsInserted(idx,idx);
}
public void removeClause(int idx)
{
	if (query == null) return;
	if (query.getClauses().size() <= idx) return;
	if (idx < 0) return;
	query.getClauses().remove(idx);
	if (curRow == idx) setCurRow(-1);
	fireTableRowsDeleted(idx,idx);
}
// ===============================================================
// Implementation of TableModel

// --------------------------------------------------
public String getColumnName(int column) 
{
	return "Name";
}
public int findCol(String colName)
{
	if ("Name".equals(colName)) return 0;
	return -1;
}
// --------------------------------------------------
/** Allow editing of all non-key fields. */
public boolean isCellEditable(int rowIndex, int columnIndex)
	{ return (query != null); }
// --------------------------------------------------
/** Set entire row.  Normally, setValueAt() will be called with a modified
version of the object retrieved from getValueAt(). */
public void setValueAt(Object val, int rowIndex, int colIndex)
{
	if (query == null) return;
	EQuery.Clause c = query.getClause(rowIndex);
	c.name = (String)val;
	// Redisplay the entire row!
	this.fireTableCellUpdated(rowIndex, colIndex);
}
// --------------------------------------------------
	public int getRowCount()
	  { return (query == null ? 0 : query.getNumClauses()); }
	public int getColumnCount()
	  { return 1; }
	public Object getValueAt(int row, int column)
		{ return query.getClause(row).name; }
public Class getColumnClass(int columnIndex) 
{
	return String.class;
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
}
