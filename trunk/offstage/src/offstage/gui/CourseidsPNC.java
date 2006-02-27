/*
 * TermsPanel.java
 *
 * Created on January 23, 2006, 5:01 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.gui;

import offstage.*;
import offstage.db.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;
import citibob.multithread.*;
import citibob.swing.table.*;
import citibob.swing.*;
import citibob.jschema.gui.*;
import citibob.jschema.*;

/**
 *
 * @author citibob
 */
public class CourseidsPNC extends citibob.jschema.gui.StatusPNC
{

	public void initRuntime(CourseidsDbModel dm, ActionRunner runner)
	throws java.sql.SQLException
	{
		//dm.setInstantUpdate(st, true);
		super.initRuntime(dm,
 			new String[] {"Name", "Weekday", "StartTime", "EndTime"},
			new String[] {"name", "dayofweek", "tstart", "tend"},
			runner);
		//String fmt = "HH:mm";
		StatusTable table = getTable();
		//ColPermuteTableModel model = getTableModel();
		KeyedModel dkm = new DayOfWeekKeyedModel();
		table.setRenderEdit("dayofweek", new KeyedRenderEdit(dkm));


		List times = DateRenderer.makeDateList(null, 7,0, 23,0, 15L*60L*1000L);
		DateListRenderEdit dre = new DateListRenderEdit(times, "HH:mm");
		table.setRenderEdit("tstart", dre);
		table.setRenderEdit("tend", dre);
	}

	
//	
//public static void main(String[] args) throws Exception
//{
//	FrontApp app = new FrontApp();
//	ActionRunner runner = new SimpleActionRunner(app.getPool());
//	CourseidsDbModel tm = new CourseidsDbModel(app.getDbChange());//app.createStatement());
//	Connection dbb = app.getPool().checkout();
//	java.sql.Statement st = dbb.createStatement();
//	tm.setKey(3);
//	tm.doSelect(st);
//	st.close();
//	CourseidsPNC panel = new CourseidsPNC();
//	panel.initRuntime(tm, runner);
//	JFrame frame = new JFrame();
//	frame.setSize(500,300);
//	frame.getContentPane().add(panel);
//	frame.show();
//}
}
