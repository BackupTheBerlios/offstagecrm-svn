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

import citibob.swing.html.*;
import javax.swing.*;
import java.sql.*;
import offstage.db.*;
import offstage.wizards.*;
import offstage.*;

/**
 *
 * @author citibob
 */
public class NewRecordWizard extends OffstageWizard {

	Statement st;		// Datbase connection
	/*
addState(new State("", "", "") {
	public HtmlDialog newWiz()
		{ return new }
	public void process()
	{
		
	}
});
*/
	
public NewRecordWizard(offstage.FrontApp xfapp, Statement xst, java.awt.Frame xframe)
{
	super(xfapp, xframe, "init");
	this.st = xst;
// ---------------------------------------------
addState(new State("init", "init", "init") {
	public HtmlDialog newWiz() throws Exception
		{ return new InitWiz(frame); }
	public void process() throws Exception
	{
		String s = v.getString("type");
		if (s != null) state = s;
	}
});
// ---------------------------------------------
addState(new State("person", "init", "finished") {
	public HtmlDialog newWiz() throws Exception
		{ return new PersonWiz(frame); }
	public void process() throws Exception
	{
//		if (!checkFieldsFilledIn()) return;
		String idSql = offstage.db.DupCheck.checkDups(st, v, 3, 20);
//		String idSql = offstage.db.DupCheck.checkDups(st, v, 3, 20);
		v.put("idsql", idSql);
		System.out.println("DupCheck sql: " + idSql);
		int ndups = DB.countIDList(st, idSql);
		if (ndups != 0) state = "checkperson";
	}
});
// ---------------------------------------------
// Duplicates were found; double-check.
addState(new State("checkperson", "person", null) {
	public HtmlDialog newWiz() throws Exception
		{ return new DupsWiz(frame, st, fapp, v.getString("idsql")); }
	public void process() throws Exception
	{
		String submit = v.getString("submit");
		if ("dontadd".equals(submit)) state = null;
		if ("addanyway".equals(submit)) {
			state = null;
System.out.println("Add anyway!");
		}
	}
});
// ---------------------------------------------

}

public static void main(String[] args) throws Exception
{
	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
	Statement st = pool.checkout().createStatement();
	FrontApp fapp = new FrontApp(pool,null);
	Wizard wizard = new NewRecordWizard(fapp, st, null);
	wizard.runWizard();
}

}
