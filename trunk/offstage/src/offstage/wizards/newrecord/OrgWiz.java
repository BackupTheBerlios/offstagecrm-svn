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

package offstage.wizards.newrecord;

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
import citibob.sql.*;
import citibob.types.*;

/**
 *
 * @author citibob
 */
public class OrgWiz extends HtmlWiz {
	
/**
 * Creates a new instance of OrgWiz 
 */
public OrgWiz(java.awt.Frame owner, SqlRunner str, citibob.app.App app)
throws org.xml.sax.SAXException, java.io.IOException, SQLException
{
	super(owner, "New Org Record", app.getSwingerMap(), true);
	setSize(600,460);
//	TypedWidgetMap map = new TypedWidgetMap();
	addTextField("firstname", new JStringSwinger());
	addTextField("middlename", new JStringSwinger());
	addTextField("lastname", new JStringSwinger());
	addTextField("address1", new JStringSwinger());
	addTextField("address2", new JStringSwinger());
	addTextField("city", new JStringSwinger());
	addTextField("state", new JStringSwinger());
	addTextField("zip", new JStringSwinger());
	addTextField("phone", new PhoneSwinger());
	addTextField("occupation", new JStringSwinger());
	addTextField("title", new JStringSwinger());
	addTextField("orgname", new JStringSwinger());
	addTextField("email", new JStringSwinger());
	addTextField("url", new JStringSwinger());
	
//	addWidget("interestid", "groupid", app.getSchema("interests"));
	
	KeyedModel kmodel = new citibob.sql.DbKeyedModel(str, null,
		"interestids", "groupid", "name", "name");
	kmodel.addItem(null, "<No Interest Specified>");
	JKeyedComboBox interests = new JKeyedComboBox(kmodel);
	interests.setValue(null);
	addWidget("interestid", interests);

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
