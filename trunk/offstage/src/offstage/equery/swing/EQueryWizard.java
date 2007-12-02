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

import citibob.reports.*;
import offstage.reports.*;
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
import offstage.gui.*;

/**
 *
 * @author citibob
 */
public class EQueryWizard extends OffstageWizard {

	/*
addState(new State("", "", "") {
	public HtmlWiz newWiz(WizState.Context con)
		{ return new }
	public void process(citibob.sql.SqlRunner str)
	{
		
	}
});
*/
	
public EQueryWizard(offstage.FrontApp xfapp, javax.swing.JFrame xframe, String startState)
{
	super("Query Wizard", xfapp, xframe, startState);
// ---------------------------------------------
addState(new AbstractWizState("listquery", null, "editquery") {
	public Wiz newWiz(WizState.Context con) throws Exception
		{ return new JPanelWizWrapper(frame, null, "",
			  new ListQueryWiz(con.str, fapp)); }
	public void process(WizState.Context con) throws Exception
	{
		if ("newquery".equals(con.v.get("submit"))) stateName = "newquery";
	}
});
// ---------------------------------------------
addState(new AbstractWizState("newquery", null, "editquery") {
	public Wiz newWiz(WizState.Context con) throws Exception
	{
		NewQueryWiz w = new NewQueryWiz(frame);
		return w;
	}
	public void process(final WizState.Context con) throws Exception
	{
		SqlSerial.getNextVal(con.str, "equeries_equeryid_seq");
		con.str.execUpdate(new UpdRunnable() {
		public void run(SqlRunner str) {
			int equeryID = (Integer)con.str.get("equeries_equeryid_seq");
			con.v.put("equeryid", equeryID);
			String sql = "insert into equeries (equeryid, name, equery, lastmodified) values (" +
				SqlInteger.sql(equeryID) + ", " +
				SqlString.sql(con.v.getString("queryname")) +
				", '', now())";
			con.str.next().execSql(sql);
		}});
	}
});
// ---------------------------------------------
addState(new AbstractWizState("editquery", "listquery", "reporttype") {
	public Wiz newWiz(WizState.Context con) throws Exception {
		EditQueryWiz eqw = new EditQueryWiz(con.str, fapp, con.v.getInt("equeryid"));
		return new JPanelWizWrapper(frame, "", "", eqw);
	}
	public void process(WizState.Context con) throws Exception
	{
		if ("deletequery".equals(con.v.get("submit"))) {
//			equeryDm.doDelete(st);
			con.str.execSql("delete from equeries where equeryid = " + SqlInteger.sql(con.v.getInt("equeryid")));
			stateName = stateRec.getBack();
		}

	}
});
// ---------------------------------------------
addState(new AbstractWizState("reporttype", "editquery", null) {
	public Wiz newWiz(WizState.Context con) throws Exception
		{ return new ReportTypeWiz(frame); }
	public void process(final WizState.Context con) throws Exception
	{
//		citibob.swing.SwingUtil.setCursor(frame, java.awt.Cursor.WAIT_CURSOR);
		String submit = con.v.getString("submit");
		EQuery equery = (EQuery)con.v.get("equery");
		String equeryName = con.v.getString("equeryname");
		if ("mailinglabels".equals(submit)) {
			equery.makeMailing(con.str, equeryName, fapp.getEquerySchema(), null);
			con.str.execUpdate(new UpdRunnable() {
			public void run(SqlRunner str) {
				final int mailingID = (Integer)con.str.get("groupids_groupid_seq");
				fapp.getMailingModel().setKey(mailingID);
				fapp.getMailingModel().doSelect(con.str.next());
				fapp.setScreen(FrontApp.MAILINGS_SCREEN);
			}});
			stateName = stateRec.getNext();
		} else if ("peopletab".equals(submit)) {
			EntityListTableModel res = fapp.getSimpleSearchResults();
			String sql = equery.getSql(fapp.getEquerySchema(), false);
System.out.println("EQueryWizard sql: " + sql);
			res.setRows(con.str, sql, null);
			fapp.setScreen(FrontApp.PEOPLE_SCREEN);
			stateName = stateRec.getNext();
		} else if ("donationreport".equals(submit)) {
			String sql = equery.getSql(fapp.getEquerySchema(), false);
			stateName = (doDonationReport(con.str, "Donation Report", sql) ? stateRec.getNext() : stateRec.getName());
		} else if ("donationreport_nodup".equals(submit)) {
			String sql = equery.getSql(fapp.getEquerySchema(), true);
//			sql = DB.removeDupsIDSql(sql);
			stateName = (doDonationReport(con.str, "Donation Report (One per Household)", sql) ? stateRec.getNext() : stateRec.getName());
		} else if ("spreadsheet".equals(submit)) {
			String sql = equery.getSql(fapp.getEquerySchema(), false);
			stateName = (doSpreadsheetReport(con.str, "Donation Report", sql) ? stateRec.getNext() : stateRec.getName());
		}
		
//		// Go on no matter what we chose...
//		if (!"back".equals(submit)) state = stateRec.getNext();
//		citibob.swing.SwingUtil.setCursor(frame, java.awt.Cursor.DEFAULT_CURSOR);
	}
});
// ---------------------------------------------






}
// ==================================================================
public boolean doDonationReport(SqlRunner str, final String title, String sql) throws Exception
{
	final DonationReport report = new DonationReport(fapp, sql);
	report.doSelect(str);
	str.execUpdate(new UpdRunnable() {
	public void run(SqlRunner str) throws Exception {
		Reports rr = fapp.getReports();
		rr.writeCSV(rr.format(report.newTableModel()),
			frame, "Save" + title);
//		ReportOutput.saveCSVReport(fapp, frame, "Save" + title, report.newTableModel());	
	}});
	return true;
}
public boolean doSpreadsheetReport(SqlRunner str, final String title, String idSql) throws Exception
{
	final SpreadsheetReport report = new SpreadsheetReport(str, fapp.getSqlTypeSet(), idSql);
	str.execUpdate(new UpdRunnable() {
	public void run(SqlRunner str) throws Exception {
		Reports rr = fapp.getReports();
		rr.writeCSV(rr.format(report), frame, "Save"+title);
//		ReportOutput.saveCSVReport(fapp, frame, "Save" + title, report);	
	}});
	return true;
}
//		
//	DonationReport report = new DonationReport(fapp, sql);
//	report.doSelect(st);	
//	String dir = fapp.userRoot().get("saveReportDir", null);
//	JFileChooser chooser = new JFileChooser(dir);
//	chooser.setDialogTitle("Save " + title);
//	chooser.addChoosableFileFilter(
//		new javax.swing.filechooser.FileFilter() {
//		public boolean accept(File file) {
//			String filename = file.getName();
//			return filename.endsWith(".csv");
//		}
//		public String getDescription() {
//			return "*.csv";
//		}
//	});
//	String path = null;
//	String fname = null;
//	for (;;) {
//		chooser.showSaveDialog(frame);
//
//		path = chooser.getCurrentDirectory().getAbsolutePath();
//		if (chooser.getSelectedFile() == null) return false;
//		fname = chooser.getSelectedFile().getPath();
//		if (!fname.endsWith(".csv")) fname = fname + ".csv";
//		File f = new File(fname);
//		if (!f.exists()) break;
//		if (JOptionPane.showConfirmDialog(
//			frame, "The file " + f.getName() + " already exists.\nWould you like to ovewrite it?",
//			"Overwrite File?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) break;
//	}
//	fapp.userRoot().put("saveReportDir", path);
//
//	CSVReportOutput csv = new CSVReportOutput(report.newTableModel(), null, null,
//		fapp.getSFormatMap());
//	cscon.v.writeReport(new File(fname));
//	return true;
//}
// ==================================================================


}
