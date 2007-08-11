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
package offstage.school.gui;
/*
 * NewRecordWizard.java
 *
 * Created on October 8, 2006, 11:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import citibob.sql.pgsql.SqlInteger;
import citibob.swing.html.*;
import citibob.wizard.*;
import javax.swing.*;
import java.sql.*;
import offstage.db.*;
import offstage.wizards.*;
import offstage.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.jschema.log.*;

/**
 *
 * @author citibob
 */
public class EnrollWizard extends OffstageWizard {

	Statement st;		// Datbase connection
	/*
addState(new State("", "", "") {
	public HtmlWiz newWiz()
		{ return new }
	public void process()
	{
		
	}
});
*/

//public static class Params {
//	public int termid;
//	public int entityid;
//	public String sterm;
//	public String sperson;
//}
//Params prm;

public EnrollWizard(offstage.FrontApp xfapp, Statement xst, java.awt.Frame xframe)
{
	super("Enrollments", xfapp, xframe, "person");
	this.st = xst;
// ---------------------------------------------
//addState(new State("init", "init", "init") {
//	public HtmlWiz newWiz() throws Exception
//		{ return new InitWiz(frame); }
//	public void process() throws Exception
//	{
//		String s = v.getString("type");
//		if (s != null) state = s;
//	}
//});
//// ---------------------------------------------
//addState(new State("person", "init", null) {
addState(new State("add", null, null) {
	public HtmlWiz newWiz() throws Exception
		{ return new AddEnrollWiz(frame, st, fapp, v); }
	public void process() throws Exception
	{
		st.executeUpdate(newInsertQuery("enrollments", v).getSql());
	}
});

}



}
