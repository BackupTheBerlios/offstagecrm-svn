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

import citibob.util.*;
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
import offstage.equery.*;

/**
 *
 * @author citibob
 */
public class EQueryTableModel2 extends AbstractTableModel
implements JTypeTableModel, EClauseTableConst
{

EQuery query;
static JType[] jtypesQuery;
static {
	jtypesQuery = new JType[] {
		new JEnum(new KeyedModel(
			new Object[] {new Integer(Clause.ADD), new Integer(Clause.SUBTRACT)},
			new Object[] {"+", "-"})),
		new JavaJType(String.class)};
}
// ------------------------------------------------------
static class RowSpec {
//	public Clause clause;
//	public Element element;
	public int cix;
	public int eix;
	public RowSpec(Clause c, Element e) {
		clause = c;
		element = e;
	}
}
ArrayList rows;		// Description of what goes in each row
RowSpec getRow(int row)
	{ return (RowSpec)rows.get(row); }
void makeRowSpecs()
{
	for (Iterator ci=query.getClauses().iterator(); ci.hasNext(); ) {
		Clause c = (Clause)ci.next();
		rows.add(new RowSpec(c, null));
		for (Iterator ei=c.elements.iterator(); ei.hasNext(); ) {
			Element e = (Element)ei.next();
			rows.add(new RowSpec(c, e));
		}
	}
}
// ------------------------------------------------------
int baseRow(int row)
{
	return row - (getRow(row).eix + 1);
}
/** Inserts clause before the row'th row of the overall table.  row = rows.size() if we wish to append to end... */
public void insertClause(int row, Clause clause)
{
	row = baseRow(row);

	// Add to underlying query
	int cix = (row < rows.size() ? getRow(row).cix : query.getClauses().size());
	query.insertClause(cix, clause);

	// Add new rows to table model and shift old rows...
	int nele = clause.getElements().size();
	ArrayListUtil.setSize(rows, rows.size() + nele + 1);
	ArrayListUtil.shift(rows, row, nele+1);

	// Insert new RowSpecs
	rows.set(row, new RowSpec(cix, -1));
	for (int i=0; i<nele; ++i) rows.set(row + i + 1, new RowSpec(cix, i));

	// Modify indices in all others
	for (int i=row + nele + 1; i<rows.size(); ++i) ++getRow(i).cix;

	this.fireTableRowsInserted(row, row+nele+1-1);
}
// ------------------------------------------------------
public void removeClause(int row)
{
	row = baseRow(row);
	int cix = getRow(row).cix;
	Clause clause = query.getClause(cix);

	query.removeClause(cix);

	// Remove new rows to table model and shift old rows...
	int nele = clause.getElements().size();
	ArrayListUtil.shift(rows, row + nele+1, -(nele+1));
	ArrayListUtil.setSize(rows, rows.size() - nele - 1);

	// Modify indices in all others
	for (int i=row; i<rows.size(); ++i) --getRow(i).cix;

	this.fireTableRowsDeleted(row, row+nele+1-1);
}
// ------------------------------------------------------
public void insertElement(int row, Element ele)
{
	if (row == 0) return;		// Cannot insert an element here....
	int brow = baseRow(row - 1);
	int cix = getRow(brow).cix;
//	int prow = row-1;		// Now on row to APPEND after, will always be legal
//	RowSpec prs = getRow(prow);

	Clause clause = query.getClause(prs.cix);
	clause.insert(prs.eix+1);		// Header rows have eix == -1, this is OK

	rows.insert(row, new RowSpec(prs.cix, prs.eix+1));
	for (int i=prs.eix+2; i<clause.getElements().size(); ++i) ++getRow(i).eix;

	this.fireTableRowsInserted(row, row);
}
// ------------------------------------------------------

public EQuery getQuery()
	{ return query; }
public void setQuery(EQuery query)
{
	this.query = query;
	makeRowSpecs();
	this.fireTableDataChanged();
}

/** Inserts a new clause before the row'th row in the table. */
public void newClause(int row)
{
	if (row < 0) {
		// Append at end
		int idx = query.newClause();
		Clause c = query.
	} else {
		RowSpec rs = getRow(row);
	}
}


public void addClause(int row)
{
	RowSpec rs = getRow(row);
	rs.
	if (query == null) return;
	query.getClauses().add(new Clause());
	int idx = query.getClauses().size() - 1;
	fireTableRowsInserted(idx,idx);
}
public void addClause(int row)
{
	RowSpec rs = getRow(row);
	rs.
	if (query == null) return;
	query.getClauses().add(new Clause());
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
	switch(column) {
		case C_ADDSUB : return S_ADDSUB;
		case C_NAME : return S_NAME;
	}
	return null;	
}
//public int findCol(String colName)
//{
//	if ("Name".equals(colName)) return 0;
//	return -1;
//}
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
	switch(colIndex) {
		case C_ADDSUB : c.type = ((Integer)val).intValue(); break;
		case C_NAME : c.name = (String)val; break;
	}
	// Redisplay the entire row!
	this.fireTableCellUpdated(rowIndex, colIndex);
}
// --------------------------------------------------
	public int getRowCount()
	  { return (query == null ? 0 : query.getNumClauses()); }
	public int getColumnCount()
	  { return 2; }
public Object getValueAt(int row, int column)
{
	EQuery.Clause c = query.getClause(row);
	switch(column) {
		case C_ADDSUB : return new Integer(c.type);
		case C_NAME : return c.name;
	}
	return null;
}
public Class getColumnClass(int column) 
{
	switch(column) {
		case C_ADDSUB : return Integer.class;
		case C_NAME : return String.class;
	}
	return String.class;
}
// ===============================================================
// Implementation of CitibobTableModel (prototype stuff)
// ===============================================================
// Implementation of JTypeTableModel (prototype stuff)
/** Return JType for a cell --- used to set up renderers and editors */
JType getJTypeClause(int row, int colIndex)
{
	if (colIndex == C_COLUMN) return schema.getColsJType();
	
	EQuery.Element el = getElement(row);
	if (el == null) return null;
	EQuerySchema.Col col = schema.getCol(el.colName);
	if (col == null) return null;
	if (colIndex == C_COMPARE) return col.comparators;
	if (col.col == null) return null;
	if (colIndex == C_VALUE) return col.col.getType();
	return null;
}
public JType getJType(int row, int column)
{
	if (isClauseRow(row)) return getJTypeClause(row, column);
	return jtypesQuery[column];
}

///** Return JType for a cell --- used to set up renderers and editors */
//public JType getJType(int row, int colIndex)
//{ return null; }
// ===============================================================
}
