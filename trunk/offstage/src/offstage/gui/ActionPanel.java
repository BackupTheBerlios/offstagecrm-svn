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

/**
 *
 * @author citibob
 */
public class ActionPanel
extends ObjHtmlPanel
implements ObjHtmlPanel.Listener
{

FrontApp fapp;
HashMap actionMap = new HashMap();

ActionPanel getThis() { return this; }

/** Creates a new instance of ActionPanel */
public void initRuntime(FrontApp xfapp) throws Exception
{
	this.fapp = xfapp;

	actionMap.put("newperson", new StRunnable() {
	public void run(Statement st) throws Exception {
		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		Wizard wizard = new NewPersonWizard(fapp, st, root);
		wizard.runWizard();
	}});

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

	actionMap.put("editquery", new StRunnable() {
	public void run(Statement st) throws Exception {
		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		Wizard wizard = new offstage.equery.swing.EQueryWizard(fapp, st, root, "listquery");
		wizard.runWizard();
	}});


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
	
	fapp.getGuiRunner().doRun((CBRunnable)actionMap.get(url));

}
}
