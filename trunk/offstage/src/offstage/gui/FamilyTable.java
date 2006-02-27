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
 * FamilyTable.java
 *
 * Created on March 19, 2005, 12:00 AM
 */

package offstage.gui;

import javax.swing.*;
import javax.swing.table.*;
import citibob.swing.*;
import citibob.swing.table.*;

/**
 *
 * @author citibob
 */
public class FamilyTable extends CitibobJTable {

/** Creates a new instance of FamilyTable */
public FamilyTable() {
	setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
}

public void initRuntime(CitibobTableModel family)
{
	ColPermuteTableModel model = new ColPermuteTableModel(
		family,
		new String[] {"Name"},
		new String[] {"name"});
	setModel(model);
}
	
}
