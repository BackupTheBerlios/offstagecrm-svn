package offstage.equery.swing;
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
import citibob.swing.*;
import citibob.wizard.*;
import javax.swing.*;
import java.sql.*;
import offstage.db.*;
import offstage.wizards.*;
import offstage.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import offstage.equery.*;

/**
 *
 * @author citibob
 */
public class EQueryWizard extends OffstageWizard {

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
	
public EQueryWizard(offstage.FrontApp xfapp, Statement xst, java.awt.Frame xframe, String startState)
{
	super("Query Wizard", xfapp, xframe, startState);
	this.st = xst;
// ---------------------------------------------
addState(new OState("listquery", null, "editquery") {
	public Wiz newWiz() throws Exception
		{ return new JPanelWizWrapper(frame, false, true,
			  new ListQueryWiz(st, fapp)); }
	public void process() throws Exception
	{
		if ("newquery".equals(v.get("submit"))) state = "newquery";
	}
});
// ---------------------------------------------
addState(new OState("newquery", null, "editquery") {
	public Wiz newWiz() throws Exception
	{
		NewQueryWiz w = new NewQueryWiz(frame);
		return w;
	}
	public void process() throws Exception
	{
		String sql = "insert into equeries (name, lastmodified) values ("
			+ citibob.sql.pgsql.SqlString.sql(v.getString("queryname"))
			+ ", now())";
		st.executeUpdate(sql);
//		System.out.println(sql);
	}
});
// ---------------------------------------------
addState(new OState("editquery", "listquery", "reporttype") {
	public Wiz newWiz() throws Exception {
		EditQueryWiz eqw = new EditQueryWiz(st, fapp, v.getInt("equeryid"));
		return new JPanelWizWrapper(frame, true, true, eqw);
	}
	public void process() throws Exception
	{
	}
});
// ---------------------------------------------
addState(new OState("reporttype", "editquery", null) {
	public Wiz newWiz() throws Exception
		{ return new ReportTypeWiz(frame); }
	public void process() throws Exception
	{
		citibob.swing.SwingUtil.setCursor(frame, java.awt.Cursor.WAIT_CURSOR);
		String submit = v.getString("submit");
		EQuery equery = (EQuery)v.get("equery");
		String equeryName = v.getString("equeryname");
		if ("mailinglabels".equals(submit)) {
			int mailingID = equery.makeMailing(st, equeryName, fapp.getEquerySchema());
			fapp.getMailingModel().setKey(mailingID);
			fapp.getMailingModel().doSelect(st);
			fapp.setScreen(FrontApp.MAILINGS_SCREEN);
		} else if ("peopletab".equals(submit)) {
			EntityListTableModel res = fapp.getSimpleSearchResults();
			String sql = equery.getSql(fapp.getEquerySchema());
			res.setRows(st, sql, null);
			fapp.setScreen(FrontApp.PEOPLE_SCREEN);
		}
		
		// Go on no matter what we chose...
		if (!"back".equals(submit)) state = stateRec.next;
		citibob.swing.SwingUtil.setCursor(frame, java.awt.Cursor.DEFAULT_CURSOR);
	}
});
// ---------------------------------------------
}

public static void main(String[] args) throws Exception
{
	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
	Statement st = pool.checkout().createStatement();
	FrontApp fapp = new FrontApp(pool,null);
	Wizard wizard = new EQueryWizard(fapp, st, null, "listquery");
	wizard.runWizard();
	System.exit(0);
}

}
