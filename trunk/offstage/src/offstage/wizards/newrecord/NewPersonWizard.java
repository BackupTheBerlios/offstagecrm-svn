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
package offstage.wizards.newrecord;
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
public class NewPersonWizard extends OffstageWizard {

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

public NewPersonWizard(offstage.FrontApp xfapp, Statement xst, java.awt.Frame xframe)
{
	super("New Record", xfapp, xframe, "person");
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
addState(new State("person", null, null) {
	public HtmlWiz newWiz() throws Exception
		{ return new PersonWiz(frame, st, fapp); }
	public void process() throws Exception
	{
		if (state == null) {
			// First: do a simple check of data entry
			if (!isValid()) {
				JOptionPane.showMessageDialog((JDialog)wiz,
					"Invalid input.\nPlease fill in all required (starred) fields!");
				state = "person";
			} else {
				String idSql = offstage.db.DupCheck.checkDups(st, v, 3, 20);
				v.put("idsql", idSql);
				System.out.println("DupCheck sql: " + idSql);
				int ndups = DB.countIDList(st, idSql);
				if (ndups == 0) {
					createPerson(false);
					state = null; //"finished";
				} else {
					state = "checkdups";
				}
				//state = (ndups == 0 ? "finished" : "checkdups");
			}
		}
	}
});
// ---------------------------------------------
// Duplicates were found; double-check.
addState(new State("checkdups", null, null) {
	public HtmlWiz newWiz() throws Exception
		{ return new DupsWiz(frame, st, fapp, v.getString("idsql")); }
	public void process() throws Exception
	{
		String submit = v.getString("submit");
		if ("dontadd".equals(submit)) state = null;
		if ("addanyway".equals(submit)) {
			createPerson(false);
			state = "finished";
System.out.println("Add anyway!");
		}
	}
});
// ---------------------------------------------
addState(new State("org", null, null) {
	public HtmlWiz newWiz() throws Exception
		{ return new OrgWiz(frame, st, fapp); }
	public void process() throws Exception
	{
		if (state == null) {
			// First: do a simple check of data entry
			if (!isValidOrg()) {
				JOptionPane.showMessageDialog((JDialog)wiz,
					"Invalid input.\nPlease fill in all required (starred) fields!");
				state = "org";
			} else {
				String idSql = offstage.db.DupCheck.checkDups(st, v, 3, 20);
				v.put("idsql", idSql);
				System.out.println("DupCheck sql: " + idSql);
				int ndups = DB.countIDList(st, idSql);
				if (ndups == 0) {
					createPerson(true);
					state = null;// "finished";
				} else {
					state = "checkdups";
				}
				//state = (ndups == 0 ? "finished" : "checkdups");
			}
		}
	}
});
// ---------------------------------------------
// Duplicates were found; double-check.
addState(new State("finished", null, null) {
	public HtmlWiz newWiz() throws Exception
		{ return new FinishedWiz(frame); }
	public void process() throws Exception
		{}
});
// ---------------------------------------------

}

// ====================================================
private void addSCol(ConsSqlQuery q, String col)
{
	String val = v.getString(col);
	if (val != null) q.addColumn(col, SqlString.sql(val));
}
void createPerson(boolean isorg) throws SQLException
{
	// Make main record
	int id = DB.r_nextval(st, "entities_entityid_seq");
	v.put("entityid", new Integer(id));
	ConsSqlQuery q = new ConsSqlQuery("persons", ConsSqlQuery.INSERT);
	q.addColumn("entityid", SqlInteger.sql(id));
	q.addColumn("primaryentityid", SqlInteger.sql(id));
	addSCol(q, "lastname");
	addSCol(q, "middlename");
	addSCol(q, "firstname");
	addSCol(q, "address1");
	addSCol(q, "address2");
	addSCol(q, "city");
	addSCol(q, "state");
	addSCol(q, "zip");
	addSCol(q, "occupation");
	addSCol(q, "title");
	addSCol(q, "orgname");
	addSCol(q, "email");
	addSCol(q, "url");
	q.addColumn("isorg", SqlBool.sql(isorg));
	String sql = q.getSql();
System.out.println(sql);
	st.execute(sql);
	fapp.getLogger().log(new QueryLogRec(q, fapp.getSchemaSet().get("persons")));
	
	// Make phone record --- first dig for keyed model...
	String phone = v.getString("phone");
	if (phone != null) {
		String phoneType = (isorg ? "work" : "home");
		q = new ConsSqlQuery("phones", ConsSqlQuery.INSERT);
		q.addColumn("entityid", SqlInteger.sql(id));
		q.addColumn("groupid", "(select groupid from phoneids where name = " + SqlString.sql(phoneType) + ")");
		q.addColumn("phone", SqlString.sql(phone));
		sql = q.getSql();
System.out.println(sql);
		st.execute(sql);
		
		fapp.getLogger().log(new QueryLogRec(q, fapp.getSchemaSet().get("phones")));
	}

	// Do interests
	Integer interestid = v.getInteger("interestid");
	if (interestid != null) {
		q = new ConsSqlQuery("interests", ConsSqlQuery.INSERT);
		q.addColumn("entityid", SqlInteger.sql(id));
		q.addColumn("groupid", SqlInteger.sql(interestid));
		sql = q.getSql();
System.out.println(sql);
		st.execute(sql);
		fapp.getLogger().log(new QueryLogRec(q, fapp.getSchemaSet().get("phones")));
	}
}

boolean notnull(String field)
{
	return (v.getString(field) != null);
}
/** Initial check on validity of info inputted. */
boolean isValid()
{
	return notnull("lastname");
}

/** Initial check on validity of info inputted. */
boolean isValidOrg()
{
	return notnull("orgname");
}

public static void main(String[] args) throws Exception
{
	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
	Statement st = pool.checkout().createStatement();
	FrontApp fapp = new FrontApp(pool,null);
	Wizard wizard = new NewPersonWizard(fapp, st, null);
	wizard.runWizard();
}

}
