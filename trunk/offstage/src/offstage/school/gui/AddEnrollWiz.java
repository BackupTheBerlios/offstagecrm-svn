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
/*
 * OrgWiz.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.school.gui;

import citibob.swing.html.*;
import java.util.*;
import citibob.swing.typed.*;
import citibob.swing.html.*;
import offstage.types.*;
import javax.swing.*;
import offstage.wizards.*;
import citibob.wizard.*;
import offstage.schema.*;
import citibob.util.*;
import java.sql.*;

/**
 *
 * @author citibob
 */
public class AddEnrollWiz extends HtmlWiz {
	
/**
 * Creates a new instance of OrgWiz 
 */
public AddEnrollWiz(java.awt.Frame owner, java.sql.Statement st, citibob.app.App app, TypedHashMap v)
throws org.xml.sax.SAXException, java.io.IOException, SQLException
{
	super(owner, "New Org Record", app.getSwingerMap(), true);
	setSize(600,460);
	addWidget("sperson", new JTypedLabel((String)v.get("sperson")));
	addWidget("sterm", new JTypedLabel((String)v.get("sterm")));
	
//	addWidget("courserole", new JKeyedComboBox((KeyedModel)v.get("courseroleModel"));
	addWidget("courserole", new JKeyedComboBox(
		new citibob.sql.DbKeyedModel(st, null,
		"courseroles", "courseroleid", "name", "orderid")));
	
	addWidget("courseid", new JKeyedComboBox( //new JKeyedSelectTable(
		new citibob.sql.DbKeyedModel(st, null,
		"courseids", "courseid", "name", "dayofweek,tstart")));

	loadHtml();
}


//public static void main(String[] args)
//throws Exception
//{
//	JFrame f = new JFrame();
//	f.setVisible(true);
//	OrgWiz wiz = new OrgWiz(f, app);
//	wiz.setVisible(true);
//	System.out.println(wiz.getSubmitName());
//	
//	wiz = new OrgWiz(f, app);
//	wiz.setVisible(true);
//	System.out.println(wiz.getSubmitName());
//	
//	System.exit(0);
//}
}
