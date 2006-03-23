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

import citibob.gui.*;
import citibob.sql.*;
import offstage.schema.*;
import offstage.*;
import offstage.db.*;
import javax.swing.*;
import java.sql.*;
import citibob.multithread.*;
import citibob.swing.table.*;
import citibob.swing.*;
import citibob.swing.typed.*;
import citibob.jschema.gui.*;
import citibob.jschema.swing.StatusTable;
import citibob.sql.*;

/**
 *
 * @author citibob
 */
public class TermsPNC extends citibob.jschema.gui.StatusPNC
{

	public void initRuntime(TermsDbModel dm, SwingerMap swingers, ActionRunner runner)
	throws java.sql.SQLException
	{
		//dm.setInstantUpdate(st, true);
		super.initRuntime(dm,
 			new String[] {"Type", "Name", "From", "To (+1)"},
			new String[] {"termtypeid", "name", "firstdate", "nextdate"},
			swingers, runner);
//		String fmt = "yyyy-MM-dd";
//		StatusTable table = getTable();
//		//ColPermuteTableModel model = getTableModel();
//		table.setRenderEditU("termtypeid", new KeyedRenderEdit(dm.getTypeKeyedModel()));
//		table.setRenderEditU("firstdate", new DateRenderEdit(fmt));
//		table.setRenderEditU("nextdate", new DateRenderEdit(fmt));
	}

	
	
public static void main(String[] args) throws Exception
{
	DBPrefsDialog d = new DBPrefsDialog(null, "offstage/db", "offstage/gui/DBPrefsDialog");
		d.setVisible(true);
		if (!d.isOkPressed()) {	// User cancelled DB open
			System.exit(0);
		}
	ConnPool pool = d.newConnPool();
	DbChangeModel change = new DbChangeModel();
	SwingerMap swingers = new citibob.sql.pgsql.DefaultSwingerMap();
	
	//FrontApp app = new FrontApp(d.newConnPool());
	Statement st = pool.checkout().createStatement();
	OffstageSchemaSet sset = new OffstageSchemaSet(st, change);
	TermsDbModel tm = new offstage.TermsDbModel(st, change, sset.termids);
//	Statement st = app.createStatement();
	tm.doSelect(st);
	TermsPNC panel = new TermsPNC();
	ActionRunner runner = new SimpleDbActionRunner(pool);
	panel.initRuntime(tm, swingers, runner);
	JFrame frame = new JFrame();
	frame.setSize(500,300);
	frame.getContentPane().add(panel);
	frame.show();
}
}
