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
import citibob.multithread.*;
import citibob.swing.table.*;
import citibob.swing.*;
import citibob.jschema.gui.*;

/**
 *
 * @author citibob
 */
public class TermsPNC extends citibob.jschema.gui.StatusPNC
{

	public void initRuntime(TermsDbModel dm, ActionRunner runner)
	throws java.sql.SQLException
	{
		//dm.setInstantUpdate(st, true);
		super.initRuntime(dm,
 			new String[] {"Type", "Name", "From", "To (+1)"},
			new String[] {"termtypeid", "name", "firstdate", "nextdate"},
			runner);
		String fmt = "yyyy-MM-dd";
		StatusTable table = getTable();
		//ColPermuteTableModel model = getTableModel();
		table.setRenderEdit("termtypeid", new KeyedRenderEdit(dm.getTypeKeyedModel()));
		table.setRenderEdit("firstdate", new DateRenderEdit(fmt));
		table.setRenderEdit("nextdate", new DateRenderEdit(fmt));
	}

	
	
public static void main(String[] args) throws Exception
{
	FrontApp app = new FrontApp(null);
	Statement st = app.getPool().checkout().createStatement();
	TermsDbModel tm = new TermsDbModel(st, app.getDbChange());
//	Statement st = app.createStatement();
	tm.doSelect(st);
	TermsPNC panel = new TermsPNC();
	ActionRunner runner = new SimpleDbActionRunner(app.getPool());
	panel.initRuntime(tm, runner);
	JFrame frame = new JFrame();
	frame.setSize(500,300);
	frame.getContentPane().add(panel);
	frame.show();
}
}
