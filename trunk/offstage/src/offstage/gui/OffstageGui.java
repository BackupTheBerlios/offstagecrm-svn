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
 * FrontGui.java
 *
 * Created on July 13, 2005, 10:52 PM
 */

package offstage.gui;
import java.sql.*;
import javax.swing.*;
import java.util.prefs.*;
import citibob.swing.prefs.*;
import citibob.jschema.swing.*;
import citibob.gui.*;
import offstage.FrontApp;
//import offstage.EQueryBrowserApp;

/**
 *
 * @author  citibob
 */
public class OffstageGui extends javax.swing.JFrame {

FrameSet frameSet;
FrontApp app;

	/** Creates new form FrontGui */
	public OffstageGui() {
		initComponents();
	}

	public void initRuntime(final FrontApp app, FrameSet frameSet, Preferences guiPrefs)
//	throws java.sql.SQLException
	throws Exception
	{
		this.app = app;
		this.frameSet = frameSet;
		Connection dbb = null;
		Statement st = null;
		try {
			dbb = app.getPool().checkout();
			st = dbb.createStatement();
			//EQueryBrowserApp eapp = app.getEqueryBrowserApp();
			actions.initRuntime(app);
			people.initRuntime(st, app);
			school.initRuntime(app, st);
//			queries.initRuntime(app);
			mailings.initRuntime(st, app); //st, eapp.getMailingidsSb(), app.getMailingsDm());

			//JSchemaWidgetTree.initWithStatement(this, st);

			app.addListener(new FrontApp.Adapter() {
			public void screenChanged() {
				switch(app.getScreen()) {
					case FrontApp.ACTIONS_SCREEN :
						tabs.setSelectedIndex(0);
					break;
					case FrontApp.PEOPLE_SCREEN :
						tabs.setSelectedIndex(1);
					break;
					case FrontApp.SCHOOL_SCREEN :
						tabs.setSelectedIndex(2);
					break;
					case FrontApp.MAILINGS_SCREEN :
						tabs.setSelectedIndex(3);
					break;
				}
			}});

			// Mess with preferences
//			Preferences prefs = Preferences.userNodeForPackage(this.getClass());
			Preferences prefs = app.userRoot().node("OffstageGui");
			new SwingPrefs().setPrefs(this, "", prefs);
		} finally {
			st.close();
			app.getPool().checkin(dbb);
		}
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        tabs = new javax.swing.JTabbedPane();
        actions = new offstage.gui.ActionPanel();
        people = new offstage.gui.EditorPanel();
        school = new offstage.school.gui.SchoolPanel();
        mailings = new offstage.gui.MailingsEditor();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        miThrowException = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        miQuit = new javax.swing.JMenuItem();
        mWindow = new javax.swing.JMenu();
        miMailPrefs = new javax.swing.JMenuItem();
        Console = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tabs.addTab("Actions", actions);

        tabs.addTab("Development", people);

        school.setPreferredSize(new java.awt.Dimension(836, 600));
        tabs.addTab("School", school);

        tabs.addTab("Mailings", mailings);

        getContentPane().add(tabs, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");
        miThrowException.setText("Throw Exception");
        miThrowException.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miThrowExceptionActionPerformed(evt);
            }
        });

        jMenu1.add(miThrowException);

        jMenu1.add(jSeparator1);

        miQuit.setText("Quit");
        miQuit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miQuitActionPerformed(evt);
            }
        });

        jMenu1.add(miQuit);

        jMenuBar1.add(jMenu1);

        mWindow.setText("Window");
        miMailPrefs.setText("Mail Preferences");
        miMailPrefs.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miMailPrefsActionPerformed(evt);
            }
        });

        mWindow.add(miMailPrefs);

        Console.setText("Java Console");
        Console.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ConsoleActionPerformed(evt);
            }
        });

        mWindow.add(Console);

        jMenuBar1.add(mWindow);

        jMenu2.setText("Help");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void miThrowExceptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miThrowExceptionActionPerformed
	app.runGui(this, new citibob.multithread.StRunnable() {
	public void run(Statement st) throws Exception {
		throw new Exception("Hello");
	}});
}//GEN-LAST:event_miThrowExceptionActionPerformed

	private void miMailPrefsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miMailPrefsActionPerformed
		new citibob.mail.MailPrefsDialog(this).setVisible(true);

// TODO add your handling code here:
	}//GEN-LAST:event_miMailPrefsActionPerformed

private void miQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miQuitActionPerformed
	System.exit(0);
// TODO add your handling code here:
}//GEN-LAST:event_miQuitActionPerformed

private void ConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConsoleActionPerformed
	frameSet.consoleFrame.setVisible(true);
// TODO add your handling code here:
}//GEN-LAST:event_ConsoleActionPerformed
	
	/**
	 * @param args the command line arguments
	 */
//	public static void main(String[] args) throws Exception
//    {
//		JFrame jf = new JFrame();
//
//		DBPrefsDialog d = new DBPrefsDialog(jf, "offstage/db", "offstage/gui/DBPrefsDialog");
//		d.setVisible(true);
//		if (!d.isOkPressed()) return;	// User cancelled DB open
//
//		FrontApp app = new FrontApp(d.newConnPool());
//		OffstageGui gui = new OffstageGui();
//		gui.initRuntime(app, null);
//	    gui.setVisible(true);
//    }
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Console;
    private offstage.gui.ActionPanel actions;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JMenu mWindow;
    private offstage.gui.MailingsEditor mailings;
    private javax.swing.JMenuItem miMailPrefs;
    private javax.swing.JMenuItem miQuit;
    private javax.swing.JMenuItem miThrowException;
    private offstage.gui.EditorPanel people;
    private offstage.school.gui.SchoolPanel school;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables
	
}
