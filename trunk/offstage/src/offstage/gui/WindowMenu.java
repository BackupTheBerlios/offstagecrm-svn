/*
 * WIndowMenu.java
 *
 * Created on January 6, 2008, 12:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.gui;

import citibob.app.App;
import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author citibob
 */
public class WindowMenu {

App app;
//JFrame frame;

public WindowMenu(App app)
{
	this.app = app;
}

void setWindowMenu(JFrame f)
{
	// Find the Window menu
//	this.frame = f;
	JMenuBar mbar = (JMenuBar)f.getJMenuBar();
	if (mbar == null) return;
	JMenu menu;
	for (int i=0; ; ) {
		menu = mbar.getMenu(i);
		if ("Window".equals(menu.getText())) break;
		++i;
		if (i == mbar.getMenuCount()) return;		// Couldn't find it
	}

	// Add our menu items...
	JMenuItem mi;

	addFrameMenuItem(menu, "Development", "devel");
	addFrameMenuItem(menu, "School", "school");
	menu.add(new JSeparator());
	addFrameMenuItem(menu, "Duplicates", "dups");
	addFrameMenuItem(menu, "Console", "console");

}

void addFrameMenuItem(JMenu menu, String text, String frameName)
{
	JMenuItem mi = new JMenuItem(text);
	mi.addActionListener(new OpenFrameListener(frameName));
	menu.add(mi);
}


class OpenFrameListener implements ActionListener
{
	String frameName;
	public OpenFrameListener(String frameName) {
		this.frameName = frameName;
	}
	public void actionPerformed(java.awt.event.ActionEvent evt) {
		app.runGui((Component)evt.getSource(), new citibob.multithread.ERunnable() {
		public void run() throws Exception {
			app.getFrameSet().openFrame(frameName);
		}});
	}
}


}
