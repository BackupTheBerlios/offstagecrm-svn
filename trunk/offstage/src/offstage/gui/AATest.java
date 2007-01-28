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
public class AATest {

protected OffstageGui offstageGui;
protected ConsoleFrame consoleFrame;




    /** Creates a new instance of FrameSet */
    public AATest() throws Exception {
		ConnPool pool = offstage.db.DB.newConnPool();
		pool.checkout();
//		Thread.currentThread().sleep(1000L*60);
		for (int i=0; i < 100; ++i) {
			Connection dbb = pool.checkout();
			System.out.println("Checked out connection " + i);
		}
    }

	public static void main(String[] args) throws Exception
    {
		new AATest();
    }

}
