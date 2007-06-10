/*
 * TermWiz.java
 *
 * Created on June 10, 2007, 4:08 PM
 */

package offstage.wizard.editcourses;

import citibob.app.*;
import citibob.jschema.*;
import citibob.jschema.gui.*;
import citibob.multithread.*;
import java.sql.*;

/**
 *
 * @author  citibob
 */
public class TermsWiz extends citibob.swing.JPanelWiz
{
	
offstage.FrontApp fapp;

	/** Creates new form TermWiz */
	public TermsWiz(offstage.FrontApp fapp, Statement st) throws SQLException
	{
		super("Edit School Terms");
		this.fapp = fapp;
		initComponents();
		SchemaBufDbModel tm = new SchemaBufDbModel(
			fapp.getSchema("termids"), fapp.getDbChange());
		tm.setWhereClause("(firstdate > now() - interval '2 years' or iscurrent)");
		tm.setOrderClause("firstdate");
		tm.doSelect(st);
		
//		termPNC = new StatusPNC();
		termPNC.initRuntime(tm,
 			new String[] {"Type", "Name", "From", "To (+1)", "Is Current"},
			new String[] {"termtypeid", "name", "firstdate", "nextdate", "iscurrent"},
			null, fapp);
		if (tm.getSchemaBuf().getRowCount() > 0) termPNC.getTable().setRowSelectionInterval(0,0);
	}

	/** Override to take action when the "<< Back" button is pressed. */
	public void backPressed() { termPNC.doSave(); }
	public void nextPressed() {
		// Save but don't update; so we can still go to the selected row.
		fapp.runGui(TermsWiz.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			SchemaBufDbModel dbm = termPNC.getDbModel();
			if (dbm.valueChanged()) {
				dbm.doUpdate(st);
			}
		}});
//		termPNC.doSave();
	}
//	public void cancelPressed() {}
	
//void saveCur()
//{
//	fapp.runGui(this, new StRunnable() {
//	public void run(Statement st) throws SQLException {
//		termPNC.getDbModel().doUpdate(st);
//	}});
//}
	
	/** After the Wiz is done running, report its output into a Map. */
	public void getAllValues(java.util.Map map)
	{
		Object termid = termPNC.getTable().getOneSelectedValU("termid");
		map.put("termid", termid);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        termPNC = new citibob.jschema.gui.StatusPNC();

        setLayout(new java.awt.BorderLayout());

        add(termPNC, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private citibob.jschema.gui.StatusPNC termPNC;
    // End of variables declaration//GEN-END:variables
	
}
