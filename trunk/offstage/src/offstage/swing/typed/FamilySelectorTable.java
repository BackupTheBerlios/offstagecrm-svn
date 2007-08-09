/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2007 by Robert Fischer

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package offstage.swing.typed;
import java.sql.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import citibob.multithread.*;
import offstage.FrontApp;
import offstage.db.FullEntityDbModel;
import offstage.db.*;
import java.awt.event.*;
import citibob.swing.typed.*;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;

/**
 * Allows users to select other people from within a family.
 * @author  citibob
 */
public class FamilySelectorTable extends IdSqlTable
{

public void initRuntime(citibob.app.App app) //Statement st, FullEntityDbModel dm)
{
	super.initRuntime(app);
}
		
public void setPrimaryEntityID(Statement st, int primaryEntityID)
throws SQLException
{
	executeQuery(st,
		" select pe.entityid from entities pe" +
		" where pe.primaryentityid = " + SqlInteger.sql(primaryEntityID) +
		" and not obsolete",
		"isprimary desc, name");
}
// ===========================================================

	
public static void main(String[] args) throws Exception
{	
	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
	Statement st = pool.checkout().createStatement();
	FrontApp fapp = new FrontApp(pool,null);
	
	javax.swing.JFrame frame = new javax.swing.JFrame();
	FamilySelectorTable table = new FamilySelectorTable();
	table.initRuntime(fapp);
	table.setPreferredSize(new Dimension(400,300));
	JScrollPane pane = new JScrollPane();
		pane.add(table);
	JPanel panel = new JPanel();
		panel.add(pane);
	frame.getContentPane().add(panel);
	frame.pack();
	table.setPrimaryEntityID(st, 12633);
	frame.setVisible(true);
}

	
}
