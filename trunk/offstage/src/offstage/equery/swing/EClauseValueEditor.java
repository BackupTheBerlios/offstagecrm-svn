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

public class EClauseValueEditor
extends MultiTableCellEditor
{
	HashMap editors;		// (KeyedModel | SqlType) --> TableCellEditor
	EQuerySchema schema;
	EQuery.Element curElement;	// Current table row we're editing


	TableCellEditor defaultEditor = new DefaultEClauseCellEditor(
		new JTypedTextField(new SqlString().getTextConverter()));
//	TableCellEditor integerEditor = new DefaultEClauseCellEditor(
//		new JTypedTextField(SqlInteger.textConverter));

	private void addTypedTFEditor(SqlType type)
	{
		editors.put(type.getClass(),
			new DefaultEClauseCellEditor(
			new JTypedTextField(type.getTextConverter())));
	}
	public EClauseValueEditor(EQuerySchema schema)
	{
		this.schema = schema;

		// Set up editors for each basic SQL type
		editors = new HashMap();
		addTypedTFEditor(new SqlInteger());
		addTypedTFEditor(new SqlDate());
		addTypedTFEditor(new SqlString());
		addTypedTFEditor(new SqlTimestamp());
		editors.put(SqlBool.class,
			new DefaultEClauseCellEditor(
			new JBoolButton()));
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
		boolean isSelected, int row, int column)
	{
System.out.println("EC: getTableCellEditorComponent");
		// Set up a specialized editor for this column, if needed...
		EQuery.Element e = (EQuery.Element)value;
		curElement = e;
		EQuerySchema.Col col = schema.getCol(e.colName);
		KeyedModel kmodel = col.kmodel;
		if (kmodel == null) {
			// Use a default editor for the given data type
			SqlType type = col.col.getType();
			TableCellEditor ed = (TableCellEditor)editors.get(type.getClass());
			if (ed == null) ed = (TableCellEditor)editors.get(SqlString.class);
			setCur(ed);
		} else {
			TableCellEditor ed = (TableCellEditor)editors.get(kmodel);
			if (ed == null) {
				//ed = new DefaultCellEditor(new JKeyedComboBox(kmodel));
				ed = new DefaultEClauseCellEditor(new JKeyedComboBox(kmodel));
				editors.put(kmodel, ed);
			}
			setCur(ed);
		}
		// Delegate...
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}
	public Object getCellEditorValue()
	{
System.out.println("EC: getCellEditorValue()");
		Object value = super.getCellEditorValue();	// Get value from currently used Editor.
//		curElement.value = value;
//		return curElement;
return value;
	}
}
