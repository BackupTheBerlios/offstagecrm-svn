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
new com.Ostermiller.util.CSVPrinter(System.out);
		ConnPool pool = offstage.db.DB.newConnPool();
		Connection dbb = pool.checkout();

		// Get database version
		Statement st = dbb.createStatement();
		OffstageVersion.fetchDbVersion(st);
		
//ResultSet rs = st.executeQuery("select dtime from querylog");
//rs.next();
//java.util.Date dt = rs.getTimestamp(1);
//String s = rs.getString(1);
//
//
////java.util.Date dt2 = new java.util.Date(ms);
//System.out.println(dt);
//System.out.println(s);



// TODO: This should not be needed.  But run for now until upgrade is in place.
st.execute("update entities set primaryentityid=entityid where primaryentityid is null");
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
