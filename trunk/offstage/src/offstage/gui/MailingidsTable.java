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

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import citibob.swing.*;
import citibob.swing.table.*;
import citibob.jschema.*;
import java.sql.*;
import offstage.MailingModel;
import citibob.jschema.swing.*;

/**
 *
 * @author citibob
 */
public class MailingidsTable extends SchemaBufTable
{
	
//MailingModel mailing;		// Sets the key on this object.  May be NULL.
//FrontApp fapp;					// Allows us to switch screens.
//Statement st;

public MailingidsTable() {
	setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

//	// Select row on double click
//	addMouseListener(new MouseAdapter()
//	{ public void mouseClicked(MouseEvent me) {
//		if (me.getClickCount() == 2) selectRow();
//	}});

//	// Select row on enter
//	addKeyListener(new KeyAdapter() {
//	public void keyReleased(KeyEvent ke) {
//		if (ke.getKeyCode() == KeyEvent.VK_ENTER) selectRow();
//    }});
}

public void initRuntime(MailingModel mailing)
//Statement st, CitibobTableModel mailingids, MailingsDbModel mailingsDm)
{
	//this.st = fapp.createStatement();
//	MailingModel mailing = fapp.getMailingModel();
//	this.fapp = fapp;
	
	super.setModelU(
		mailing.getMailingidsSb(),
		new String[] {"Name", "Create Date"},
		new String[] {"name", "created"},
		new boolean[] {false, false});
		
	setSelectionModel(mailing.getMailingidsSelectModel());
}
//void selectRow()
//{
//	int selected = getSelectedRow();
//	if (selected < 0) return;
//	int col = mailingids.findCol("groupid");
//	Integer mailingID = (Integer)mailingids.getValueAt(selected, col);
//	mailingsDm.setKey(mailingID.intValue());
//	try {
//		mailingsDm.doSelect(st);
//		fapp.setScreen(FrontApp.MAILINGS_SCREEN);
//	} catch(SQLException e) {
//		e.printStackTrace(System.out);
//	}	
//}
//// ========================================================
//private class DClickListener extends MouseAdapter
//{ public void mouseClicked(MouseEvent me) {
//	if (me.getClickCount() == 2) selectRow();
//}}

}
