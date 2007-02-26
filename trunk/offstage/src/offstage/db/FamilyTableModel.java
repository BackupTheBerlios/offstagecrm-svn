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

package offstage.db;

import citibob.jschema.*;
import citibob.swing.*;
import java.sql.*;
import citibob.sql.*;
import citibob.multithread.*;
import citibob.app.*;

/**
 *
 * @author citibob
 */
public class FamilyTableModel extends EntityListTableModel
implements RowModel.ColListener //, SchemaRowModelBindable
{

/** Creates a new instance of FamilyTableModel
 @param runner should be a raw runner (for db, not app). */
public FamilyTableModel(ActionRunner runner, SqlTypeSet tset) {
	super(tset);
	this.runner = runner;
}

int primaryCol;
SchemaRowModel bufRow;
ActionRunner runner;
// --------------------------------------------------------------
public void setValue(Statement st, int primaryEntityID)
throws SQLException
{
	// Now do the read-only stuff
	String sql =
		" select pe.entityid from entities pe" +
		" where pe.primaryentityid = " + primaryEntityID;
	String orderBy = "isprimary desc, relation, name";
	
//System.out.println("*****************\n" + sql);
	// ResultSet rs = st.executeQuery(sql);
	setRows(st, sql, orderBy);
}
// ===============================================================
/** Binds this widget to listen/edit a particular column in a RowModel, using the type for that column derived from the associated Schema.  NOTE: This requires a correspondence in the numbering of columns in the Schema and in the RowModel.  No permutions inbetween are allowed!  This should not be a problem, just make sure the TableRowModel binds DIRECTLY to the source SchemaBuf, not to some permutation thereof. */
public void bind(SchemaRowModel bufRow)
{
	primaryCol = bufRow.getSchema().findCol("primaryentityid");

	/** Bind as a listener to the RowModel (which fronts a SchemaBuf)... */
	this.bufRow = bufRow;
	bufRow.addColListener(primaryCol, this);
}
// ===============================================================
// Implementation of RowModel.Listener

/** Propagate data from underlying model to widget. */
public void valueChanged(final int col)
{
	runner.doRun(new StRunnable() {
	public void run(Statement st) throws Exception {
		Integer Primaryid = (Integer)bufRow.get(primaryCol);
		if (Primaryid == null) {
			setRowCount(0);		// Just clear it out...
		} else {
			int primaryid = Primaryid.intValue();
System.out.println("FamilyTableModel: value changed to: " + primaryid);
			setValue(st, primaryid);
		}
	}});
}
public void curRowChanged(int col)
{
	int row = bufRow.getCurRow();
	if (row == MultiRowModel.NOROW) setRowCount(0);
	else valueChanged(col);
}
	
}
