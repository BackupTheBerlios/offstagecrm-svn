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
 * EditorPanel.java
 *
 * Created on June 5, 2005, 10:33 AM
 */

package offstage.devel.gui;

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
import offstage.devel.gui.DevelModel;
import citibob.multithread.*;
import offstage.school.gui.*;
import citibob.sql.*;

/**
 *
 * @author  citibob
 */
public class DevelPanel extends javax.swing.JPanel {

DevelModel dmod;
FrontApp app;

	/** Creates new form EditorPanel */
	public DevelPanel() {
		initComponents();
	}
	public void initRuntime(SqlRunner str, FrontApp fapp, DevelModel dmod)
	{
		this.app = fapp;
		this.dmod = dmod;
		entityPanel.initRuntime(str, fapp, dmod);
		simpleSearch.initRuntime(fapp, dmod);
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

        entityPanel = new offstage.devel.gui.EntityPanel();
        simpleSearch = new offstage.gui.SimpleSearchPanel();
        jToolBar1 = new javax.swing.JToolBar();
        bSave = new javax.swing.JButton();
        bUndo = new javax.swing.JButton();
        bDelete = new javax.swing.JButton();
        bNewPerson = new javax.swing.JButton();
        bNewOrg = new javax.swing.JButton();

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

        bNewPerson.setText("New Person");
        bNewPerson.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bNewPersonActionPerformed(evt);
            }
        });

        jToolBar1.add(bNewPerson);

        bNewOrg.setText("New Org");
        bNewOrg.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bNewOrgActionPerformed(evt);
            }
        });

        jToolBar1.add(bNewOrg);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jToolBar1, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

	private void bNewOrgActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bNewOrgActionPerformed
	{//GEN-HEADEREND:event_bNewOrgActionPerformed
	app.runGui(this, new BatchRunnable() {
	public void run(SqlRunner str) throws Exception {
//		model.newEntity(st, FullEntityDbModel.PERSON);
		JFrame root = (javax.swing.JFrame)citibob.swing.WidgetTree.getRoot(DevelPanel.this);
		citibob.wizard.Wizard wizard = new offstage.wizards.newrecord.NewPersonWizard(app, root);
		wizard.runWizard("org");
		Integer EntityID = (Integer)wizard.getVal("entityid");
		if (EntityID != null) {
			offstage.devel.gui.DevelPanel.this.dmod.setKey(EntityID);
			offstage.devel.gui.DevelPanel.this.dmod.doSelect(str);
		}
	}});// TODO add your handling code here:
	}//GEN-LAST:event_bNewOrgActionPerformed

	private void bUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bUndoActionPerformed
	app.runGui(this, new BatchRunnable() {
	public void run(SqlRunner str) throws Exception {
		offstage.devel.gui.DevelPanel.this.dmod.doSelect(str);
	}});
	}//GEN-LAST:event_bUndoActionPerformed

private void bDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeleteActionPerformed
	app.runGui(this, "admin", new BatchRunnable() {
	public void run(SqlRunner str) throws Exception {
		if (JOptionPane.showConfirmDialog(DevelPanel.this,
			"Are you sure you wish to permanently delete this record?",
			"Delete Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				offstage.devel.gui.DevelPanel.this.dmod.doDelete(str);
				// Stop displaying it
				offstage.devel.gui.DevelPanel.this.dmod.setKey(-1);
				offstage.devel.gui.DevelPanel.this.dmod.doSelect(str);
		}
//		this.simpleSearch.runSearch();
	}});
}//GEN-LAST:event_bDeleteActionPerformed

private void bNewPersonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNewPersonActionPerformed
	app.runGui(this, new BatchRunnable() {
	public void run(SqlRunner str) throws Exception {
//		model.newEntity(st, FullEntityDbModel.PERSON);
		JFrame root = (javax.swing.JFrame)citibob.swing.WidgetTree.getRoot(DevelPanel.this);
		citibob.wizard.Wizard wizard = new offstage.wizards.newrecord.NewPersonWizard(app, root);
		wizard.runWizard("person");
		Integer EntityID = (Integer)wizard.getVal("entityid");
		if (EntityID != null) {
			offstage.devel.gui.DevelPanel.this.dmod.setKey(EntityID);
			offstage.devel.gui.DevelPanel.this.dmod.doSelect(str);
		}
	}});
}//GEN-LAST:event_bNewPersonActionPerformed

private void bSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSaveActionPerformed
	app.runGui(this, new BatchRunnable() {
	public void run(SqlRunner str) throws Exception {
		offstage.devel.gui.DevelPanel.this.dmod.doUpdate(str);
		offstage.devel.gui.DevelPanel.this.dmod.doSelect(str);
	}});
}//GEN-LAST:event_bSaveActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bDelete;
    private javax.swing.JButton bNewOrg;
    private javax.swing.JButton bNewPerson;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bUndo;
    private offstage.devel.gui.EntityPanel entityPanel;
    private javax.swing.JToolBar jToolBar1;
    private offstage.gui.SimpleSearchPanel simpleSearch;
    // End of variables declaration//GEN-END:variables

//	public static void main(String[] args) throws Exception
//    {
//
//
//		FrontApp app = new FrontApp();
//		FullEntityDbModel dm = app.getFullEntityDm();
//		//SqlRunner str = app.createStatement();
//		SqlRunner str = app.getPool().checkout().createStatement();
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