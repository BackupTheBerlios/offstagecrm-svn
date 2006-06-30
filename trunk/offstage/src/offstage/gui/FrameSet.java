/*
 * FrameSet.java
 *
 * Created on March 12, 2006, 1:22 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.gui;

import java.sql.*;
import javax.swing.*;
import java.util.prefs.*;
import citibob.swing.prefs.*;
import citibob.jschema.swing.*;
import citibob.gui.*;
import citibob.sql.*;
import offstage.FrontApp;
import offstage.EQueryBrowserApp;
import offstage.config.*;

/**
 *
 * @author citibob
 */
public class FrameSet {

protected OffstageGui offstageGui;
protected ConsoleFrame consoleFrame;

    /** Creates a new instance of FrameSet */
    public FrameSet() throws Exception {
		// Open the Database
		Preferences dbPref = OffstageVersion.prefs.node("db");
		Preferences dbGuiPref = OffstageVersion.prefs.node("db/gui");
		DBPrefsDialog d = new DBPrefsDialog(null, dbPref, dbGuiPref);
		d.setVisible(true);
		if (!d.isOkPressed()) {	// User cancelled DB open
			System.exit(0);
		}
		ConnPool pool = d.newConnPool();
		Connection dbb = pool.checkout();

		// Get database version
		Statement st = dbb.createStatement();
		OffstageVersion.fetchDbVersion(st);
		st.close();
		pool.checkin(dbb);

		consoleFrame = new ConsoleFrame();
		consoleFrame.initRuntime("Java Console", OffstageVersion.guiPrefs.absolutePath() + "/ConsoleFrame");
		
		FrontApp app = new FrontApp(pool, consoleFrame.getDocument());
		offstageGui = new OffstageGui();
		offstageGui.initRuntime(app, this, OffstageVersion.guiPrefs);

	
	    offstageGui.setVisible(true);
    }

	public static void main(String[] args) throws Exception
    {
		new FrameSet();
    }

}
