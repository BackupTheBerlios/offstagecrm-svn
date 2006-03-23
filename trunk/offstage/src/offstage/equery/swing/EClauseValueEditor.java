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
import citibob.sql.pgsql.*;
import offstage.equery.EQuery;
import offstage.equery.EQuerySchema;
import citibob.sql.SqlType;
import citibob.sql.KeyedModel;
import citibob.swing.pgsql.*;

public class EClauseValueEditor
extends MultiTableCellEditor
{
	HashMap editors;		// (KeyedModel | SqlType) --> TableCellEditor
	EQuerySchema schema;
	EQuery.Element curElement;	// Current table row we're editing
//	SwingerMap smap;

	SqlSwinger swing = new SqlStringSwinger(new SqlString(true));
	TableCellEditor defaultEditor = new DefaultEClauseCellEditor(
		new JTypedTextField(swing));

//	TableCellEditor defaultEditor = new DefaultEClauseCellEditor(
//		new JTypedTextField(new SqlString().getTextConverter()));
//	TableCellEditor integerEditor = new DefaultEClauseCellEditor(
//		new JTypedTextField(SqlInteger.textConverter));

	private void addTypedTFEditor(SqlSwinger swing)
	{
		editors.put(swing.getSqlType().getClass(),
			new DefaultEClauseCellEditor(
			new JTypedTextField(swing))); //type.getTextConverter())));
	}
//	public EClauseValueEditor(EQuerySchema schema, SwingerMap smap)
	public EClauseValueEditor(EQuerySchema schema)
	{
//		this.smap = smap
		this.schema = schema;

		// Set up editors for each basic SQL type
		editors = new HashMap();
// TODO: this needs fixing up...
		addTypedTFEditor(new SqlIntegerSwinger(new SqlInteger(false)));
		addTypedTFEditor(new SqlDateSwinger(new SqlDate(), null, "MM/dd/yyyy"));
		addTypedTFEditor(new SqlStringSwinger(new SqlString(false)));
		addTypedTFEditor(new SqlTimestampSwinger(new SqlTimestamp(false), null, "MM/dd/yyyy HH:mm"));
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
//		return super.getTableCellEditorComponent(table, e.value, isSelected, row, column);
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
