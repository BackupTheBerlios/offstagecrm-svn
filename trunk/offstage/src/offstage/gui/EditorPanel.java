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
 * EditorPanel.java
 *
 * Created on June 5, 2005, 10:33 AM
 */

package offstage.gui;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import citibob.jschema.*;
import citibob.jschema.swing.*;
//import citibob.jschema.swing.JSchemaWidgetTree;
import citibob.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import offstage.FrontApp;
import offstage.db.FullEntityDbModel;
import citibob.multithread.*;
import offstage.school.gui.*;

/**
 *
 * @author  citibob
 */
public class EditorPanel extends javax.swing.JPanel {

FullEntityDbModel model;
//ActionRunner runner;
//citibob.app.App app;
FrontApp app;

	/** Creates new form EditorPanel */
	public EditorPanel() {
		initComponents();
	}
	public void initRuntime(Statement st, FrontApp fapp)
//ActionRunner guiRunner, FullEntityDbModel)
	throws java.sql.SQLException
	{
		this.app = fapp;
//		this.runner = fapp.getGuiRunner();
		this.model = fapp.getFullEntityDm();
		//JSchemaWidgetTree.bindToPool(this, fapp.getPool());
		entityPanel.initRuntime(st, app, model);
		simpleSearch.initRuntime(fapp);
	}
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        entityPanel = new offstage.gui.EntityPanel();
        simpleSearch = new offstage.gui.SimpleSearchPanel();
        jToolBar1 = new javax.swing.JToolBar();
        bSave = new javax.swing.JButton();
        bUndo = new javax.swing.JButton();
        bDelete = new javax.swing.JButton();
        bNewPerson = new javax.swing.JButton();
        bSchool = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        entityPanel.setPreferredSize(new java.awt.Dimension(550, 586));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        add(entityPanel, gridBagConstraints);

        simpleSearch.setPreferredSize(new java.awt.Dimension(150, 400));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        add(simpleSearch, gridBagConstraints);

        bSave.setText("Save");
        bSave.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bSaveActionPerformed(evt);
            }
        });

        jToolBar1.add(bSave);

        bUndo.setText("Undo");
        bUndo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bUndoActionPerformed(evt);
            }
        });

        jToolBar1.add(bUndo);

        bDelete.setText("Delete");
        bDelete.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bDeleteActionPerformed(evt);
            }
        });

        jToolBar1.add(bDelete);

        bNewPerson.setText("New");
        bNewPerson.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bNewPersonActionPerformed(evt);
            }
        });

        jToolBar1.add(bNewPerson);

        bSchool.setText("School");
        bSchool.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bSchoolActionPerformed(evt);
            }
        });

        jToolBar1.add(bSchool);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jToolBar1, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

	private void bSchoolActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSchoolActionPerformed
	{//GEN-HEADEREND:event_bSchoolActionPerformed
	app.runGui(this, new StRunnable() {
	public void run(Statement st) throws Exception {
		PersonSchool ps = new PersonSchool();
//		java.awt.Window root = (java.awt.Window)citibob.swing.WidgetTree.getRoot(EditorPanel.this);
		ps.initRuntime(app, st, model.getEntityId());
		citibob.gui.GuiUtil.showJPanel(EditorPanel.this, ps, app,
			"School Info for Person", "personschool", true);
	}});
// TODO add your handling code here:
	}//GEN-LAST:event_bSchoolActionPerformed

	private void bUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bUndoActionPerformed
	app.runGui(this, new StRunnable() {
	public void run(Statement st) throws Exception {
		model.doSelect(st);
	}});
	}//GEN-LAST:event_bUndoActionPerformed

private void bDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeleteActionPerformed
	app.runGui(this, "admin", new StRunnable() {
	public void run(Statement st) throws Exception {
		if (JOptionPane.showConfirmDialog(EditorPanel.this,
			"Are you sure you wish to permanently delete this record?",
			"Delete Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				model.doDelete(st);
		}
//		this.simpleSearch.runSearch();
	}});
}//GEN-LAST:event_bDeleteActionPerformed

private void bNewPersonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNewPersonActionPerformed
	app.runGui(this, new StRunnable() {
	public void run(Statement st) throws Exception {
		model.newEntity(st, FullEntityDbModel.PERSON);
	}});
}//GEN-LAST:event_bNewPersonActionPerformed

private void bSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSaveActionPerformed
	app.runGui(this, new StRunnable() {
	public void run(Statement st) throws Exception {
		model.doUpdate(st);
		model.doSelect(st);
	}});
}//GEN-LAST:event_bSaveActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bDelete;
    private javax.swing.JButton bNewPerson;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bSchool;
    private javax.swing.JButton bUndo;
    private offstage.gui.EntityPanel entityPanel;
    private javax.swing.JToolBar jToolBar1;
    private offstage.gui.SimpleSearchPanel simpleSearch;
    // End of variables declaration//GEN-END:variables

//	public static void main(String[] args) throws Exception
//    {
//
//
//		FrontApp app = new FrontApp();
//		FullEntityDbModel dm = app.getFullEntityDm();
//		//Statement st = app.createStatement();
//		Statement st = app.getPool().checkout().createStatement();
//
//		dm.setKey(139208);
//		dm.doSelect(st);
//
//		EditorPanel personPanel = new EditorPanel();
//		personPanel.initRuntime(new SimpleDbActionRunner(app.getPool()), app);//personRM, dm.getPhonesSb());
//
//		
//		
//	    JFrame frame = new JFrame();
//	    frame.getContentPane().add(personPanel);
//		frame.pack();
//	    frame.setVisible(true);
//		System.out.println("Done");
//    }

}
