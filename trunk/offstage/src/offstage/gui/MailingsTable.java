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
 * MailingidsTable.java
 *
 * Created on July 4, 2005, 1:53 PM
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
public class MailingsTable extends CitibobJTable
{
public MailingsTable() {
	setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
}

public void initRuntime(CitibobTableModel mailings)
{
	ColPermuteTableModel model = new ColPermuteTableModel(
		mailings,
		new String[] {"Name", "Address1", "Address2", "City", "State", "Zip", "Country"},
		new String[] {"addressto", "address1", "address2", "city", "state", "zip", "country"});
	model.setEditable(new boolean[] {false, false});
	setModel(model);
}
// ========================================================
}
