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
import offstage.reports.*;
import java.io.*;

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
	
public EQueryWizard(offstage.FrontApp xfapp, Statement xst, javax.swing.JFrame xframe, String startState)
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
		int equeryID = DB.r_nextval(st, "equeries_equeryid_seq");
		String sql = "insert into equeries (equeryid, name, equery, lastmodified) values (" +
			SqlInteger.sql(equeryID) + ", " +
			SqlString.sql(v.getString("queryname")) +
			", '', now())";
		st.executeUpdate(sql);
		v.put("equeryid", equeryID);
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
		if ("deletequery".equals(v.get("submit"))) {
//			equeryDm.doDelete(st);
			st.executeUpdate("delete from equeries where equeryid = " + SqlInteger.sql(v.getInt("equeryid")));
			state = stateRec.back;
		}

	}
});
// ---------------------------------------------
addState(new OState("reporttype", "editquery", null) {
	public Wiz newWiz() throws Exception
		{ return new ReportTypeWiz(frame); }
	public void process() throws Exception
	{
//		citibob.swing.SwingUtil.setCursor(frame, java.awt.Cursor.WAIT_CURSOR);
		String submit = v.getString("submit");
		EQuery equery = (EQuery)v.get("equery");
		String equeryName = v.getString("equeryname");
		if ("mailinglabels".equals(submit)) {
			int mailingID = equery.makeMailing(st, equeryName, fapp.getEquerySchema());
			fapp.getMailingModel().setKey(mailingID);
			fapp.getMailingModel().doSelect(st);
			fapp.setScreen(FrontApp.MAILINGS_SCREEN);
			state = stateRec.next;
		} else if ("peopletab".equals(submit)) {
			EntityListTableModel res = fapp.getSimpleSearchResults();
			String sql = equery.getSql(fapp.getEquerySchema());
			res.setRows(st, sql, null);
			fapp.setScreen(FrontApp.PEOPLE_SCREEN);
			state = stateRec.next;
		} else if ("donationreport".equals(submit)) {
			String sql = equery.getSql(fapp.getEquerySchema());
			state = (doDonationReport("Donation Report", sql) ? stateRec.next : stateRec.name);
		} else if ("donationreport_nodup".equals(submit)) {
			String sql = equery.getSql(fapp.getEquerySchema());
			sql = DB.removeDupsIDSql(sql);
			state = (doDonationReport("Donation Report (One per Household)", sql) ? stateRec.next : stateRec.name);
		}
		
//		// Go on no matter what we chose...
//		if (!"back".equals(submit)) state = stateRec.next;
//		citibob.swing.SwingUtil.setCursor(frame, java.awt.Cursor.DEFAULT_CURSOR);
	}
});
// ---------------------------------------------
}
// ==================================================================
public boolean doDonationReport(String title, String sql) throws Exception
{
	DonationReport report = new DonationReport(fapp, sql);
	report.doSelect(st);
	String dir = fapp.userRoot().get("saveReportDir", null);
	JFileChooser chooser = new JFileChooser(dir);
	chooser.setDialogTitle("Save " + title);
	chooser.addChoosableFileFilter(
		new javax.swing.filechooser.FileFilter() {
		public boolean accept(File file) {
			String filename = file.getName();
			return filename.endsWith(".csv");
		}
		public String getDescription() {
			return "*.csv";
		}
	});
	String path = null;
	String fname = null;
	for (;;) {
		chooser.showSaveDialog(frame);

		path = chooser.getCurrentDirectory().getAbsolutePath();
		if (chooser.getSelectedFile() == null) return false;
		fname = chooser.getSelectedFile().getPath();
		if (!fname.endsWith(".csv")) fname = fname + ".csv";
		File f = new File(fname);
		if (!f.exists()) break;
		if (JOptionPane.showConfirmDialog(
			frame, "The file " + f.getName() + " already exists.\nWould you like to ovewrite it?",
			"Overwrite File?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) break;
	}
	fapp.userRoot().put("saveReportDir", path);

	CSVReportOutput csv = new CSVReportOutput(report.newTableModel(), null, null,
		fapp.getSFormatterMap());
	csv.writeReport(new File(fname));
	return true;
}
// ==================================================================

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
