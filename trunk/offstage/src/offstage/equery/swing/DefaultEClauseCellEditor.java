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
 * DefaultColEditor.java
 *
 * Created on July 1, 2005, 11:43 PM
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
 * Makes a default table cell editor for our funny items, in which each column
 * gets the entire row to "edit".
 * @author citibob
 */
public class DefaultEClauseCellEditor
extends TypedWidgetCellEditor
implements EClauseTableConst
{

EQuery.Element element;
int column;

public DefaultEClauseCellEditor(Component c)
	{ super(c); }

public Component getTableCellEditorComponent(JTable table, Object value,
	boolean isSelected, int row, int column)
{
	element = (EQuery.Element)value;
	this.column = column;
	Object val = null;
	switch(column) {
		case C_COLUMN :
			val = element.colName;
		break;
		case C_COMPARE :
			val = element.comparator;
		break;
		case C_VALUE :
			val = element.value;
		break;
	}
	return super.getTableCellEditorComponent(table, val,
		isSelected, row, column);
}
public Object getCellEditorValue()
{
	Object val = super.getCellEditorValue();
System.out.println("DefaultEClauseCellEditor: val = " + val);
	switch(column) {
		case C_COLUMN :
			element.colName = (EQuery.ColName)val;
		break;
		case C_COMPARE :
			element.comparator = (String)val;
		break;
		case C_VALUE :
			element.value = val;
		break;
	}
	return element;
}



}
