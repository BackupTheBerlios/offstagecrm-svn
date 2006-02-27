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
import citibob.jschema.pgsql.*;
import offstage.equery.EQuery;
import offstage.equery.EQuerySchema;

public class EClauseCompareEditor
extends MultiTableCellEditor
{
	HashMap editors;		// KeyedModel --> TableCellEditor
	EQuerySchema schema;
	EQuery.Element curElement;	// Current table row we're editing

	public EClauseCompareEditor(EQuerySchema schema)
	{
		this.schema = schema;
		editors = new HashMap();
	}

	TableCellEditor defaultEditor = new DefaultEClauseCellEditor(
		new JTypedTextField(new SqlString().getTextConverter()));
	public Component getTableCellEditorComponent(JTable table, Object value,
		boolean isSelected, int row, int column)
	{
		// Set up the right editor
		EQuery.Element e = (EQuery.Element)value;
		curElement = e;
		EQuerySchema.Col col = schema.getCol(e.colName);
		String[] comparators = col.comparators;
		if (comparators == null) setCur(defaultEditor);
		else {
			TableCellEditor ed = (TableCellEditor)editors.get(comparators);
			if (ed == null) {
				ed = new DefaultCellEditor(new JComboBox(comparators));
				editors.put(comparators, ed);
			}
			setCur(ed);
		}
		// Delegate...
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}
	public Object getCellEditorValue()
	{
		Object value = super.getCellEditorValue();
		curElement.comparator = (String)value;
		return curElement;
	}
}
