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
 * SimpleSearchPanel.java
 *
 * Created on June 5, 2005, 5:47 PM
 */

package offstage.gui;
import java.sql.*;
import citibob.jschema.pgsql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import citibob.multithread.*;
import offstage.FrontApp;
import offstage.db.FullEntityDbModel;
import offstage.db.*;

/**
 *
 * @author  citibob
 */
public class SimpleSearchPanel extends javax.swing.JPanel {
//	Statement st;
	FullEntityDbModel dm;
	EntityListTableModel searchResults;
	ActionRunner runner;
	
	/** Creates new form SimpleSearchPanel */
	public SimpleSearchPanel() {
		initComponents();
		
		searchResultsTable.addMouseListener(new DClickTableMouseListener(searchResultsTable) {
		public void doubleClicked(final int row) {
			runner.doRun(new StRunnable() {
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
	}
	public void initRuntime(FrontApp app) //Statement st, FullEntityDbModel dm)
	{
		//this.st = app.createStatement();
		this.runner = app.getGuiRunner();
		this.dm = app.getFullEntityDm();
		searchResults = app.getSimpleSearchResults();
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
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        info.clearthought.layout.TableLayout _tableLayoutInstance;

        jPanel1 = new javax.swing.JPanel();
        searchWord = new javax.swing.JTextField();
        bSearch = new javax.swing.JButton();
        bGo = new javax.swing.JButton();
        FamilyScrollPanel = new javax.swing.JScrollPane();
        searchResultsTable = new offstage.gui.FamilyTable();
        jToolBar1 = new javax.swing.JToolBar();
        bSetFamily = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        _tableLayoutInstance = new info.clearthought.layout.TableLayout();
        _tableLayoutInstance.setHGap(0);
        _tableLayoutInstance.setVGap(0);
        _tableLayoutInstance.setColumn(new double[]{info.clearthought.layout.TableLayout.FILL,info.clearthought.layout.TableLayout.FILL});
        _tableLayoutInstance.setRow(new double[]{info.clearthought.layout.TableLayout.FILL,info.clearthought.layout.TableLayout.FILL});
        jPanel1.setLayout(_tableLayoutInstance);

        jPanel1.add(searchWord, new info.clearthought.layout.TableLayoutConstraints(0, 0, 1, 0, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

        bSearch.setText("Search");
        bSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSearchActionPerformed(evt);
            }
        });

        jPanel1.add(bSearch, new info.clearthought.layout.TableLayoutConstraints(0, 1, 0, 1, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

        bGo.setText("Go");
        bGo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bGoActionPerformed(evt);
            }
        });

        jPanel1.add(bGo, new info.clearthought.layout.TableLayoutConstraints(1, 1, 1, 1, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

        add(jPanel1, java.awt.BorderLayout.SOUTH);

        FamilyScrollPanel.setPreferredSize(new java.awt.Dimension(300, 64));
        searchResultsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        FamilyScrollPanel.setViewportView(searchResultsTable);

        add(FamilyScrollPanel, java.awt.BorderLayout.CENTER);

        bSetFamily.setText("Set Family Same as...");
        bSetFamily.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSetFamilyActionPerformed(evt);
            }
        });

        jToolBar1.add(bSetFamily);

        add(jToolBar1, java.awt.BorderLayout.NORTH);

    }
    // </editor-fold>//GEN-END:initComponents

	private void bSetFamilyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSetFamilyActionPerformed
		runner.doRun(new StRunnable() {
		public void run(Statement st) throws Exception {
			int entityid = getSelectedEntityID();
			if (entityid < 0) return;
			dm.getEntitySb().setFamilySameAs(st, entityid);
			dm.doUpdate(st);
			dm.doSelect(st);

		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bSetFamilyActionPerformed

	private void bGoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bGoActionPerformed
		runner.doRun(new StRunnable() {
		public void run(Statement st) throws Exception {
			// TODO add your handling code here:
			int entityid = getSelectedEntityID();
			if (entityid < 0) return;
			dm.setKey(entityid);
			dm.doSelect(st);
		}});
	}//GEN-LAST:event_bGoActionPerformed

	private void bSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSearchActionPerformed
		runner.doRun(new StRunnable() {
		public void run(Statement st) throws Exception {
			String text = searchWord.getText();
			if (text == null || "".equals(text)) return;		// no query
			String ssearch = SqlString.sql(text, false);
			String sql = "select entityid from persons where firstname ilike '%" + ssearch + "%'" +
				" or lastname ilike '%" + ssearch + "%'" +
				"    union " +
				" select entityid from organizations where name ilike '%" + ssearch + "%'";
System.out.println(sql);
			//ResultSet rs = st.executeQuery(sql);
			searchResults.setRows(st, sql, null);
			//rs.close();
		}});
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
