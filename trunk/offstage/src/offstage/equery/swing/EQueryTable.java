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
 * EQueryTable.java
 *
 * Created on July 2, 2005, 12:21 PM
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



/**
 *
 * @author citibob
 */
public class EQueryTable extends JTable {

EQueryTableModel model;

public EQueryTable()
{
	super();
	setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	ListSelectionModel sm = getSelectionModel();
	sm.addListSelectionListener(new ListSelectionListener() {
	public void valueChanged(ListSelectionEvent e) {
		//Ignore extra messages.
		if (e.getValueIsAdjusting()) return;

		ListSelectionModel lsm = (ListSelectionModel)e.getSource();
		if (lsm.isSelectionEmpty()) {
			model.setCurRow(-1);
		} else {
			int selectedRow = lsm.getMinSelectionIndex();
			model.setCurRow(selectedRow);
		}
	}
	});
}

public void initRuntime(EQueryTableModel model)
{
	super.setModel(model);
	this.model = model;
}




}
