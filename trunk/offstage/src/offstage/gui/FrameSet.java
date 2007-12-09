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
//import com.jgoodies.looks.plastic.theme.*;
//import com.jgoodies.looks.plastic.*;

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

//System.out.println(System.getProperty("os.name"));
		ConnPool pool = offstage.db.DB.newConnPool();
//		Connection dbb = pool.checkout();
//
//		// Get database version
//		Statement st = dbb.createStatement();
//		OffstageVersion.fetchDbVersion(st);
//		
//// TODO: This should not be needed.  But run for now until upgrade is in place.
//st.execute("update entities set primaryentityid=entityid where primaryentityid is null");
//		st.close();
//		pool.checkin(dbb);

//		SqlBatchSet str = new SqlBatchSet();
		
		consoleFrame = new ConsoleFrame();
		consoleFrame.initRuntime("Java Console", OffstageVersion.guiPrefs.absolutePath() + "/ConsoleFrame");
		
		FrontApp app = new FrontApp(pool, consoleFrame.getDocument());
		offstageGui = new OffstageGui();
		offstageGui.initRuntime(app.getBatchSet(), app, this, OffstageVersion.guiPrefs);

app.getFullEntityDm().setKey(12633);	// Go to Bob's record (for debuggin)'
app.getFullEntityDm().doSelect(app.getBatchSet());
		app.getBatchSet().runBatches();
		
		offstageGui.pack();
	    offstageGui.setVisible(true);
    }


	
	public static void main(String[] args) throws Exception
    {
		System.setProperty("swing.metalTheme", "ocean");
		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		
	 new FrameSet();
    }

}
