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
import offstage.MailingModel;
import offstage.MailingsDbModel;

/**
 *
 * @author  citibob
 */
public class MailingsEditor extends javax.swing.JPanel {

	MailingModel mailing;
	ActionRunner runner;
//	Statement st;
	
	/** Creates new form MailingsEditor */
	public MailingsEditor() {
		initComponents();
	}
	public void initRuntime(Statement st, FrontApp app) throws SQLException
	{
		runner = app.getGuiRunner();
		mailing = app.getMailingModel();
		tMailingIds.initRuntime(mailing);
		ColPermuteTableModel tModel = new ColPermuteTableModel(mailing.getMailingsSb(),
			new String[] {"To", "Address 1", "Address 2", "City", "State", "Zip", "Country"},
			new String[] {"addressto", "address1", "address2", "city", "state", "zip", "country"});
		tMailing.setModel(tModel);
		SchemaRowModel rowModel = mailing.getCurMailingidRm();
		JSchemaWidgetTree.bindToSchemaRow(this, rowModel);
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
        tMailing = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        lName = new citibob.jschema.swing.JSchemaTextField();
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

        jButton1.setText("View Labels");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jToolBar1.add(jButton1);

        jPanel2.add(jToolBar1, java.awt.BorderLayout.SOUTH);

        _tableLayoutInstance = new info.clearthought.layout.TableLayout();
        _tableLayoutInstance.setHGap(0);
        _tableLayoutInstance.setVGap(0);
        _tableLayoutInstance.setColumn(new double[]{info.clearthought.layout.TableLayout.FILL});
        _tableLayoutInstance.setRow(new double[]{info.clearthought.layout.TableLayout.FILL});
        jPanel1.setLayout(_tableLayoutInstance);

        lName.setColName("name");
        lName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lNameActionPerformed(evt);
            }
        });

        jPanel1.add(lName, new info.clearthought.layout.TableLayoutConstraints(0, 0, 0, 0, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

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

    }
    // </editor-fold>//GEN-END:initComponents

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
		runner.doRun(new StRunnable() {
		public void run(Statement st) throws Exception {
			jButton1.requestFocus();
			mailing.makeReport(st);
		}});
		// TODO add your handling code here:
	}//GEN-LAST:event_jButton1ActionPerformed

	private void lNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lNameActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_lNameActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private citibob.jschema.swing.JSchemaTextField lName;
    private javax.swing.JTable tMailing;
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
