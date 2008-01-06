/*
 * OffstageFrameSet.java
 *
 * Created on January 5, 2008, 9:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.gui;

import citibob.gui.ConsoleFrame;
import citibob.gui.FrameSet.Maker;
import citibob.sql.SqlRunner;
import citibob.sql.UpdRunnable;
import javax.swing.*;
import offstage.FrontApp;
import offstage.cleanse.CleansePanel;
import offstage.devel.gui.DevelFrame;
import offstage.school.gui.SchoolFrame;

/**
 *
 * @author citibob
 */
public class OffstageFrameSet extends citibob.gui.FrameSet {

FrontApp fapp;
WindowMenu wmenu;

public JFrame newFrame(FrameRec rec) throws Exception
{
	JFrame frame = super.newFrame(rec);
	wmenu.setWindowMenu(frame);
	if ("maintenance".equals(rec.name)) {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	return frame;
}


/** Creates a new instance of OffstageFrameSet */
public OffstageFrameSet(FrontApp xfapp) {
	super(xfapp.getSwingPrefs(), xfapp.userRoot());
	wmenu = new WindowMenu(xfapp);
	this.fapp = xfapp;

addMaker("devel", new Maker() {
public JFrame newFrame() throws Exception {
	SqlRunner str = fapp.getBatchSet();
	final DevelFrame f = new DevelFrame();
	f.initRuntime(fapp.getBatchSet(), fapp);
	str.flush();
//	str.execUpdate(new UpdRunnable() {
//	public void run(SqlRunner str) throws Exception {
//		f.pack();
//	}});
	return f;
}});
// ----------------------------------------
addMaker("school", new Maker() {
public JFrame newFrame() throws Exception {
	final SchoolFrame f = new SchoolFrame();
	SqlRunner str = fapp.getBatchSet();
	f.initRuntime(str, fapp);
	str.flush();
//	str.execUpdate(new UpdRunnable() {
//	public void run(SqlRunner str) throws Exception {
//		f.pack();
//	}});
	return f;
}});
// ----------------------------------------
addMaker("dups", new Maker() {
public JFrame newFrame() throws Exception {
	SqlRunner str = fapp.getBatchSet();
	final offstage.cleanse.CleansePanel panel = new CleansePanel();
	panel.initRuntime(str, fapp, "n");
	final JFrame frame = new JFrame("Duplicate Names");
	frame.getContentPane().add(panel);
	str.flush();
	return frame;
}});
// ----------------------------------------
addMaker("console", new Maker() {
public JFrame newFrame() {
	ConsoleFrame consoleFrame = new ConsoleFrame();
	consoleFrame.initRuntime("Java Console", fapp.userRoot().node("ConsoleFrame"));
	return consoleFrame;
}});
addMaker("maintenance", new Maker() {
public JFrame newFrame() throws Exception {
	OffstageGui offstageGui = new OffstageGui();
	offstageGui.initRuntime(fapp);
	return offstageGui;
}});
// ----------------------------------------
//addMaker("mailprefs", new Maker() {
//public JFrame newFrame() {
//	return new citibob.mail.MailPrefsDialog(this);
//}});


}
}
