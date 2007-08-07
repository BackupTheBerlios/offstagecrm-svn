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
 * NewRecordWiz2.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.crypt.wiz;

import citibob.swing.html.HtmlWiz;
import citibob.swing.html.HtmlWiz;
import java.util.*;
import citibob.swing.typed.*;
import citibob.swing.html.*;
import offstage.types.*;
import javax.swing.*;
import offstage.wizards.*;
import offstage.*;
import offstage.gui.*;
import citibob.wizard.*;
import java.sql.*;

/**
 *
 * @author citibob
 */
public class CCBatchInitial extends HtmlWiz {

/** Should this Wiz screen be cached when "Back" is pressed? */
public boolean getCacheWiz() { return false; }

/**
 * Creates a new instance of NewRecordWiz2 
 */
public CCBatchInitial(java.awt.Frame owner, java.sql.Statement st, FrontApp fapp)
throws org.xml.sax.SAXException, java.io.IOException, java.sql.SQLException
{
	super(owner, "Insert Key", true);

	String sql =
//" update ccpayments set ccbatchid = null;" +
		" select count(*) as npayments from ccpayments" +
		" where ccbatchid is null and ccinfo is not null";
	ResultSet rs = st.executeQuery(sql);
	rs.next();
	JTypedLabel npayments = new JTypedLabel();
//	npayments.setColName("npayments");
//	TypedWidgetBinder.setValueRecursive(npayments, rs, fapp.getSwingerMap(), fapp.getSqlTypeSet());
	npayments.setJType(new JavaJType(Integer.class),
		new FormatFormatter(new java.text.DecimalFormat("#")));
	npayments.setValue(rs.getInt(1));
	rs.close();
	
	addWidget("npayments", npayments);
//	this.setSize(new java.awt.Dimension(750, 550));
	loadHtml();
}


}
