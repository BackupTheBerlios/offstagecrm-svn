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
EntitySelector sel;

public void setJType(Swinger swing) {}

public void initRuntime(App app, java.awt.Window root)
{
	this.app = app;
	super.setJType(new SqlInteger(), new EntityIDFormatter(app.getPool()));
	sel = new EntitySelector();
	sel.initRuntime(app);
	super.setPopupWidget(sel);
}

protected void showPopup()
{
	sel.setValue(null);
	super.showPopup();
	sel.requestTextFocus();
}
// =======================================================
static class EntityIDFormatter extends DBFormatter
{

public EntityIDFormatter(ConnPool pool)
{ super(pool); }

public String valueToString(Statement st, Object value)
throws java.sql.SQLException
{
	String s = SQL.readString(st,
		" select " +
			" (case when firstname is null then '' else firstname || ' ' end ||" +
			" case when middlename is null then '' else middlename || ' ' end ||" +
			" case when lastname is null then '' else lastname end" +
//			" case when orgname is null then '' else ' (' || orgname || ')' end" +
			" ) as name" +
		" from entities" +
		" where entityid = " + SqlInteger.sql((Integer)value));
	return s;
}

}

}

