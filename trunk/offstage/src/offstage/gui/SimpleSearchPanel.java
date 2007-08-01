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
 * SimpleSearchPanel.java
 *
 * Created on June 5, 2005, 5:47 PM
 */

package offstage.gui;
import java.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import citibob.multithread.*;
import offstage.FrontApp;
import offstage.db.FullEntityDbModel;
import offstage.db.*;
import java.awt.event.*;

/**
 *
 * @author  citibob
 */
public class SimpleSearchPanel extends javax.swing.JPanel {
//	Statement st;
	FullEntityDbModel dm;
	EntityListTableModel searchResults;
	//ActionRunner runner;
	citibob.app.App app;
	
	/** Creates new form SimpleSearchPanel */
	public SimpleSearchPanel() {
		initComponents();
		
		// Double-clicking will go to selected person
		searchResultsTable.addMouseListener(new DClickTableMouseListener(searchResultsTable) {
		public void doubleClicked(final int row) {
			app.runGui(SimpleSearchPanel.this, new StRunnable() {
			public void run(Statement st) throws Exception {
				// Make sure it's selected in the GUI
				searchResultsTable.getSelectionModel().setSelectionInterval(row, row);

				// Process the selection
				int entityid = getSelectedEntityID();
				if (entityid < 0) return;
				dm.setKey(entityid);
				dm.doSelect(st);
			}});
		}});
		
		// Pressing ENTER will initiate search.
		searchWord.addKeyListener(new KeyAdapter() {
	    public void keyTyped(KeyEvent e) {
			//System.out.println(e.getKeyChar());
			if (e.getKeyChar() == '\n') runSearch();
		}});
		
	}
	public void initRuntime(FrontApp fapp) //Statement st, FullEntityDbModel dm)
	{
		//this.st = app.createStatement();
		this.app = fapp;
		this.dm = fapp.getFullEntityDm();
		searchResults = fapp.getSimpleSearchResults();
		// searchResults = new EntityListTableModel();
		searchResultsTable.initRuntime(searchResults);
	}

	int getSelectedEntityID()
	{
		int selected = searchResultsTable.getSelectedRow();
		if (selected < 0) return -1;
		int entityID = searchResults.getEntityID(selected);
		return entityID;
	}
	
	private void runSearch() {
		app.runGui(this, new StRunnable() {
		public void run(Statement st) throws Exception {
			String sql = DB.simpleSearchSql(searchWord.getText());
			if (sql != null) searchResults.setRows(st, sql, null);
		}});
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

        jPanel1 = new javax.swing.JPanel();
        searchWord = new javax.swing.JTextField();
        bSearch = new javax.swing.JButton();
        bGo = new javax.swing.JButton();
        FamilyScrollPanel = new javax.swing.JScrollPane();
        searchResultsTable = new offstage.gui.FamilyTable();
        jToolBar1 = new javax.swing.JToolBar();
        bSetFamily = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        searchWord.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                searchWordActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(searchWord, gridBagConstraints);

        bSearch.setText("Search");
        bSearch.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bSearchActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jPanel1.add(bSearch, gridBagConstraints);

        bGo.setText("Go");
        bGo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bGoActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jPanel1.add(bGo, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.SOUTH);

        FamilyScrollPanel.setPreferredSize(new java.awt.Dimension(300, 64));
        searchResultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        FamilyScrollPanel.setViewportView(searchResultsTable);

        add(FamilyScrollPanel, java.awt.BorderLayout.CENTER);

        bSetFamily.setText("Add to Family");
        bSetFamily.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bSetFamilyActionPerformed(evt);
            }
        });

        jToolBar1.add(bSetFamily);

        add(jToolBar1, java.awt.BorderLayout.NORTH);

    }// </editor-fold>//GEN-END:initComponents

	private void searchWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchWordActionPerformed
// TODO add your handling code here:
	}//GEN-LAST:event_searchWordActionPerformed

	private void bSetFamilyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSetFamilyActionPerformed
		app.runGui(this, new StRunnable() {
		public void run(Statement st) throws Exception {
			int entityid = getSelectedEntityID();
			if (entityid < 0) return;
//			dm.getEntitySb().setFamilySameAs(st, entityid);
			dm.getEntity().addToFamily(st, entityid);
//			dm.getEntitySb().addToFamily(st, entityid);	// Adds entityid to the family of person currently in entity editor
			dm.doUpdate(st);
			dm.doSelect(st);

		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bSetFamilyActionPerformed

	private void bGoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bGoActionPerformed
		app.runGui(this, new StRunnable() {
		public void run(Statement st) throws Exception {
			int entityid = getSelectedEntityID();
			if (entityid < 0) return;
			dm.setKey(entityid);
			dm.doSelect(st);
		}});
	}//GEN-LAST:event_bGoActionPerformed

	
	private void bSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSearchActionPerformed
		runSearch();
	}//GEN-LAST:event_bSearchActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane FamilyScrollPanel;
    private javax.swing.JButton bGo;
    private javax.swing.JButton bSearch;
    private javax.swing.JButton bSetFamily;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar jToolBar1;
    private offstage.gui.FamilyTable searchResultsTable;
    private javax.swing.JTextField searchWord;
    // End of variables declaration//GEN-END:variables
	
}
