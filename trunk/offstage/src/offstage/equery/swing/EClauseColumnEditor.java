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
import offstage.equery.EQuery;
import offstage.equery.EQuerySchema;

public class EClauseColumnEditor
extends KeyedTableCellEditor
{
	
	EQuery.Element element;
	
	static KeyedModel newKeyedModel(EQuerySchema schema)
	{
		KeyedModel kmodel = new KeyedModel();
		for (Iterator ii = schema.colIterator(); ii.hasNext();) {
			EQuerySchema.Col col = (EQuerySchema.Col)ii.next();
			String stable = col.table;
			String scol = col.col.getName();
			kmodel.addItem(new EQuery.ColName(stable, scol), col.getViewName());
		}
		return kmodel;
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value,
	boolean isSelected, int row, int column)
	{
		element = (EQuery.Element)value;
		Component ret = super.getTableCellEditorComponent(table, element.colName, isSelected, row, column);
//System.out.println("EClauseColumnEditor.getTableCellEditorComponent: value = " + );
		return ret;
	}
	
	public EClauseColumnEditor(EQuerySchema schema)
	{
		super(newKeyedModel(schema));
	}

	public Object getCellEditorValue()	// Returns EQuery.Element
	{
		Object val = super.getCellEditorValue();
		element.colName = (EQuery.ColName)val;
		return element;
	}

}
