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
import offstage.FrontApp;
import offstage.EQueryBrowserApp;


/**
 *
 * @author citibob
 */
public class FrameSet {

protected OffstageGui offstageGui;
protected ConsoleFrame consoleFrame;

    /** Creates a new instance of FrameSet */
    public FrameSet() throws Exception {
		// JFrame jf = new JFrame();
		DBPrefsDialog d = new DBPrefsDialog(null, "offstage/db", "offstage/db/gui/DBPrefsDialog");
		d.setVisible(true);
		if (!d.isOkPressed()) {	// User cancelled DB open
			System.exit(0);
		}

		consoleFrame = new ConsoleFrame();
		consoleFrame.initRuntime("Java Console", "offstage/gui/ConsoleFrame");
		
		FrontApp app = new FrontApp(d.newConnPool(), consoleFrame.getDocument());
		offstageGui = new OffstageGui();
		offstageGui.initRuntime(app, this);


	    offstageGui.setVisible(true);
    }

	public static void main(String[] args) throws Exception
    {
		new FrameSet();
    }

}
