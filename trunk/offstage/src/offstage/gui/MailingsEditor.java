/*
Offstage CRM: Enterprise Database for Arts Organizations
This file Copyright (c) 2006 by Robert Fischer

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
/*
 * MailingsEditor.java
 *
 * Created on July 10, 2005, 10:31 PM
 */

package offstage.gui;
import citibob.jschema.*;
import citibob.jschema.swing.*;
import java.sql.*;
import javax.swing.*;
import citibob.swing.*;
import citibob.multithread.*;
import citibob.swing.table.*;
import offstage.FrontApp;
import offstage.MailingModel2;
//import offstage.MailingsDbModel;
import citibob.swing.typed.*;
import java.awt.Cursor;

/**
 *
 * @author  citibob
 */
public class MailingsEditor extends javax.swing.JPanel {

	MailingModel2 mailing;
	ActionRunner runner;
//	Statement st;
	
	/** Creates new form MailingsEditor */
	public MailingsEditor() {
		initComponents();
	}
	public MailingsEditor getThis() { return this; }
	public void initRuntime(Statement st, FrontApp app) throws SQLException
	{
		runner = app.getGuiRunner();
		mailing = app.getMailingModel();
//		tMailingIds.initRuntime(mailing);
		tMailingIds.setModelU(
			mailing.getMailingidsDb(),
			new String[] {"Name", "Create Date"},
			new String[] {"name", "created"},
			new boolean[] {false, false}, app.getSwingerMap());
		tMailingIds.setRenderU("created", new javax.swing.table.DefaultTableCellRenderer());
//		tMailingIds.setSelectionModel(mailing.getMailingidsDb()SelectModel());
		mailing.getMailingidsDb().setInstantUpdate(app.getAppRunner(), true);
		
		tMailingIds.addMouseListener(new DClickTableMouseListener(tMailingIds) {
		public void doubleClicked(final int row) {
			citibob.swing.SwingUtil.setCursor(getThis(), Cursor.WAIT_CURSOR);
			runner.doRun(new StRunnable() {
			public void run(Statement st) throws Exception {
				// Make sure it's selected in the GUI
				tMailingIds.getSelectionModel().setSelectionInterval(row, row);

				// Process the selection
				Integer Mailingid = (Integer)mailing.getMailingidsDb().getSchemaBuf().getValueAt(row, "groupid");
				if (Mailingid == null) return;
				mailing.setKey(Mailingid.intValue());
				mailing.doSelect(st);
			}});
			citibob.swing.SwingUtil.setCursor(getThis(), Cursor.DEFAULT_CURSOR);
		}});
		
		tMailing.setModelU(mailing.getMailingsDb().getSchemaBuf(),
			new String[] {"Name", "Address1", "Address2", "City", "State", "Zip", "Country"},
			new String[] {"addressto", "address1", "address2", "city", "state", "zip", "country"},
			null, app.getSwingerMap());
//			new boolean[] {false, false, false, false, false, false, false});
		
//		SchemaBufRowModel rowModel = new SchemaBufRowModel(mailing.getOneMailingidsDb().getSchemaBuf());
//		TypedWidgetBinder.bindRecursive(this, rowModel, app.getSwingerMap());
	}
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        info.clearthought.layout.TableLayout _tableLayoutInstance;

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tMailing = new citibob.jschema.swing.StatusTable();
        jToolBar1 = new javax.swing.JToolBar();
        bSave = new javax.swing.JButton();
        bUndo = new javax.swing.JButton();
        bInsert = new javax.swing.JButton();
        bDelete = new javax.swing.JButton();
        bViewLabels = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        lName = new citibob.swing.typed.JTypedTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tMailingIds = new offstage.gui.MailingidsTable();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        tMailing.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tMailing);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        bSave.setText("Save");
        bSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSaveActionPerformed(evt);
            }
        });

        jToolBar1.add(bSave);

        bUndo.setText("Undo");
        bUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bUndoActionPerformed(evt);
            }
        });

        jToolBar1.add(bUndo);

        bInsert.setText("Insert");
        bInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bInsertActionPerformed(evt);
            }
        });

        jToolBar1.add(bInsert);

        bDelete.setText("Delete");
        bDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDeleteActionPerformed(evt);
            }
        });

        jToolBar1.add(bDelete);

        bViewLabels.setText("Save & View Labels");
        bViewLabels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bViewLabelsActionPerformed(evt);
            }
        });

        jToolBar1.add(bViewLabels);

        jPanel2.add(jToolBar1, java.awt.BorderLayout.SOUTH);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));

        lName.setColName("name");
        lName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lNameActionPerformed(evt);
            }
        });

        jPanel1.add(lName);

        jPanel2.add(jPanel1, java.awt.BorderLayout.NORTH);

        jSplitPane1.setRightComponent(jPanel2);

        tMailingIds.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tMailingIds);

        jSplitPane1.setLeftComponent(jScrollPane2);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents

private void bInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bInsertActionPerformed
	runner.doRun(new StRunnable() {
	public void run(Statement st) throws Exception {
		mailing.newAddress();
	}});
}//GEN-LAST:event_bInsertActionPerformed

	private void bUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bUndoActionPerformed
		runner.doRun(new StRunnable() {
		public void run(Statement st) throws Exception {
			bUndo.requestFocus();
			mailing.doSelect(st);
		}});
	}//GEN-LAST:event_bUndoActionPerformed

	private void bSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSaveActionPerformed
		runner.doRun(new StRunnable() {
		public void run(Statement st) throws Exception {
			bSave.requestFocus();
			mailing.doUpdate(st);
			mailing.getMailingidsDb().doSelect(st);
		}});
	}//GEN-LAST:event_bSaveActionPerformed

	private void bDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeleteActionPerformed
			int selected = tMailing.getSelectedRow();
			if (selected != -1) {
				mailing.getMailingsDb().getSchemaBuf().deleteRow(selected);
			}
	}//GEN-LAST:event_bDeleteActionPerformed

	private void bViewLabelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bViewLabelsActionPerformed
		citibob.swing.SwingUtil.setCursor(getThis(), Cursor.WAIT_CURSOR);
		runner.doRun(new StRunnable() {
		public void run(Statement st) throws Exception {
//			throw new Exception("Bobs Exception");
			bViewLabels.requestFocus();
			mailing.doUpdate(st);
System.out.println("MailingsEditor: done with doUpdate");
			mailing.makeReport(st);
		}});
		citibob.swing.SwingUtil.setCursor(getThis(), Cursor.DEFAULT_CURSOR);
		// TODO add your handling code here:
	}//GEN-LAST:event_bViewLabelsActionPerformed

	private void lNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lNameActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_lNameActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bDelete;
    private javax.swing.JButton bInsert;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bUndo;
    private javax.swing.JButton bViewLabels;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private citibob.swing.typed.JTypedTextField lName;
    private citibob.jschema.swing.StatusTable tMailing;
    private offstage.gui.MailingidsTable tMailingIds;
    // End of variables declaration//GEN-END:variables

	
//	public static void main(String[] args) throws Exception
//    {
//		FrontApp app = new FrontApp();
//		Statement st = app.createStatement();
//		MailingsDbModel dm = new MailingsDbModel(st);
//		
//		dm.setKey(250);
//		dm.doSelect(st);
//
//		MailingsEditor mp = new MailingsEditor();
//		//mp.initRuntime(st, dm);//personRM, dm.getPhonesSb());
//
//		
//		
//	    JFrame frame = new JFrame();
//	    frame.getContentPane().add(mp);
//		frame.pack();
//	    frame.setVisible(true);
//		System.out.println("Done");
//    }
	
}
