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
 * ActionPanel.java
 *
 * Created on October 22, 2006, 10:08 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.gui;

import citibob.wizard.Wizard;
import citibob.wizard.Wizard;
import offstage.*;
import citibob.swing.html.*;
import java.util.*;
import citibob.multithread.*;
import java.sql.*;
import offstage.wizards.newrecord.*;
import offstage.wizards.modify.*;
import citibob.swing.*;
import javax.swing.*;
import offstage.school.gui.*;
import citibob.jschema.*;
import citibob.jschema.gui.*;

/**
 *
 * @author citibob
 */
public class ActionPanel
extends ObjHtmlPanel
implements ObjHtmlPanel.Listener
{

FrontApp fapp;
HashMap<String,CBTask> actionMap = new HashMap();

ActionPanel getThis() { return this; }

/** Creates a new instance of ActionPanel */
public void initRuntime(FrontApp xfapp) throws Exception
{
	this.fapp = xfapp;

	actionMap.put("newperson", new CBTask("", new StRunnable() {
	public void run(Statement st) throws Exception {
		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		Wizard wizard = new NewPersonWizard(fapp, st, root);
		wizard.runWizard();
	}}));

//	actionMap.put("amend", new StRunnable() {
//	public void run(Statement st) throws Exception {
//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
//		Wizard wizard = new AmendRecordWizard(fapp, st, root);
//		wizard.runWizard();
//	}});

//	actionMap.put("newquery", new StRunnable() {
//	public void run(Statement st) throws Exception {
//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
//		Wizard wizard = new offstage.equery.swing.EQueryWizard(fapp, st, root, "newquery");
//		wizard.runWizard();
//	}});

	actionMap.put("ticketsalesreport", new CBTask("", new StRunnable() {
	public void run(Statement st) throws Exception {
		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		Wizard wizard = new offstage.reports.ReportWizard(fapp, st, root, "ticketparams");
		wizard.runWizard();
	}}));

	actionMap.put("editquery", new CBTask("", new StRunnable() {
	public void run(Statement st) throws Exception {
		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		Wizard wizard = new offstage.equery.swing.EQueryWizard(fapp, st, root, "listquery");
		wizard.runWizard();
	}}));

	actionMap.put("newcategory", new CBTask("", "admin", new StRunnable() {
	public void run(Statement st) throws Exception {
		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		Wizard wizard = new offstage.wizards.newgroupid.NewGroupidWizard(fapp, st, root);
		wizard.runWizard();
	}}));

	actionMap.put("editcourses", new CBTask("", "admin", new StRunnable() {
	public void run(Statement st) throws Exception {
		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		Wizard wizard = new offstage.wizard.editcourses.EditCoursesWizard(fapp, st, root);
		wizard.runWizard();
//		SchemaBufDbModel tm = new SchemaBufDbModel(
//			fapp.getSchema("termids"), fapp.getDbChange());
//		tm.setWhereClause("(firstdate > now() - interval '2 years' or iscurrent)");
//		tm.setOrderClause("firstdate");
//		tm.doSelect(st);
//		
//		StatusPNC panel = new StatusPNC();
//		panel.initRuntime(tm,
// 			new String[] {"Type", "Name", "From", "To (+1)", "Is Current"},
//			new String[] {"termtypeid", "name", "firstdate", "nextdate", "iscurrent"},
//			null, fapp);
////		JFrame frame = new JFrame();
//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(ActionPanel.this);
//		JDialog frame = new JDialog(root, true);
//		frame.setSize(500,300);
//		frame.getContentPane().add(panel);
//		fapp.setUserPrefs(frame, "editterms");
//		frame.setVisible(true);
	}}));

//	actionMap.put("editcourses", new CBTask("", "admin", new StRunnable() {
//	public void run(Statement st) throws Exception {
//		CoursesEditor panel = new CoursesEditor();
//		panel.initRuntime(st, fapp);
//		
//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(ActionPanel.this);
//		JDialog frame = new JDialog(root, true);
//		frame.setSize(500,300);
//		frame.getContentPane().add(panel);
//		fapp.setUserPrefs(frame, "editcourses");
//		frame.setVisible(true);
//	}}));
	
	addListener(this);
	loadHtml();
}

// ===================================================
// ObjHtmlPanel.Listener
public void linkSelected(java.net.URL href, String target)
{
	String url = href.toExternalForm();
	int slash = url.lastIndexOf('/');
	if (slash > 0) url = url.substring(slash+1);
	
	CBTask t = actionMap.get(url);
	fapp.runGui(this, t.getPermissions(), t.getCBRunnable());
}
}

//
//public boolean doTicketSalesReport(String title, int groupid) throws Exception
//{
//	SqlTableModel report = new SqlTableModel(fapp.getSqlTypeSet(),
//		" select p.entityid,p.firstname,p.lastname,p.city,p.state,p.zip," +
//		" t.numberoftickets,t.payment,tt.tickettype\n" +
//		" from persons p, ticketeventsales t, tickettypes tt\n" +
//		" where p.entityid = t.entityid\n" +
//		" and t.tickettypeid = tt.tickettypeid\n" +
//		" and t.groupid in (314,315)\n" +
//		" order by p.lastname,p.firstname\n");
//	report.executeQuery(st);
//	OffstageGuiUtil.saveCSVReport(report.newTableModel(), "Save" + title,
//		fapp, frame);
//	return true;
//}
