/*
JSchema: library for GUI-based database applications
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
 * JDate.java
 *
 * Created on May 14, 2003, 8:52 PM
 */

package offstage.gui;

import java.text.DateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import citibob.exception.*;
import citibob.sql.*;
import citibob.swing.typed.*;
import citibob.app.*;
import citibob.sql.pgsql.*;

/**
 *
 * @author  citibob
 */
public class EntityIDLabel extends JTypedEditableLabel
{


public EntityIDLabel() {}

App app;
java.awt.Window root;
EntitySelector panel;

public void setJType(Swinger swing) {}

public void initRuntime(App app, java.awt.Window root)
{
	this.app = app;
	this.root = root;
	super.setJType(new SqlInteger(), new EntityIDFormatter(app.getPool()));


	panel = new EntitySelector();
	panel.initRuntime(app);

}

public Object selectValue()
{
	JDialog frame;

	// Initialize our selector frame to pop up as needed
	if (root instanceof Dialog) frame = new javax.swing.JDialog((Dialog)root);
	else frame = new JDialog((Frame)root);

	frame.getContentPane().add(panel);
	frame.setSize(200,400);
	frame.pack();
	frame.setVisible(true);
	return panel.getValue();
}




// =======================================================
static class EntityIDFormatter extends DBFormatter
{

public EntityIDFormatter(ConnPool pool)
{ super(pool); }

public String valueToString(Statement st, Object value)
throws java.sql.SQLException
{
	return SQL.readString(st,
		" select firstname + ' ' + lastname + " +
		" from entities" +
		" where entityid = " + SqlInteger.sql((Integer)value));
}

}

}

