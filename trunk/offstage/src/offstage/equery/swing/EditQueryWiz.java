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
 * EQueryBrowser.java
 *
 * Created on July 3, 2005, 1:31 PM
 */

package offstage.equery.swing;

import java.sql.*;
import java.io.*;
import javax.swing.*;
import citibob.swing.table.*;
import java.util.prefs.*;
import offstage.*;
import offstage.db.FullEntityDbModel;
import offstage.db.EntityListTableModel;
import offstage.equery.*;
import citibob.multithread.*;
import offstage.gui.*;
import citibob.swing.typed.*;
import offstage.db.*;
import citibob.jschema.*;
import citibob.sql.pgsql.*;
import citibob.sql.*;

/**
 *
 * @author  citibob
 */
public class EditQueryWiz extends citibob.swing.JPanelWiz {

SchemaBufDbModel equeryDm;		// Holds our (one) equery
SchemaBufRowModel row;
EntityListTableModel testResults;
//EQuery equery;			// The latest value we're editing
//EQuerySchema equerySchema;

offstage.FrontApp fapp;
int equeryID;


/** Creates new form EQueryBrowser */
public EditQueryWiz() {
	super("Edit Query");
	initComponents();
}

//protected EQueryWiz1 getThis() { return this; }

public EditQueryWiz(SqlRunner str, offstage.FrontApp fapp, int equeryID)
//public void initRuntime(SqlRunner str, citibob.app.App fapp, int equeryID)
throws SQLException
{
	this();
	cacheWiz = false;		// Don't cache this wiz
	this.fapp = fapp;
	
	this.equeryID = equeryID;
	
	EQueryTableModel2 eqModel = new EQueryTableModel2(fapp.getEquerySchema());
//		new EQuerySchema(fapp.getSchemaSet()));
	eQueryEditor.initRuntime(eqModel, fapp.getSwingerMap(), fapp.getTimeZone());

	equeryDm = new SchemaBufDbModel(new SchemaBuf(fapp.getSchema("equeries")));
	row = new SchemaBufRowModel(equeryDm.getSchemaBuf());
	citibob.swing.typed.TypedWidgetBinder.bindRecursive(this, row, fapp.getSwingerMap());
	
	equeryDm.setWhereClause("equeryid = " + equeryID);
	equeryDm.doSelect(str);
	
	testResults = new EntityListTableModel(fapp.getSqlTypeSet());
	tTestResults.initRuntime(testResults);
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

        jSplitPane4 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tTestResults = new offstage.gui.FamilyTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lQuerySize = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        bApply = new javax.swing.JButton();
        editorPanel = new javax.swing.JPanel();
        eQueryEditor = new offstage.equery.swing.EQueryEditor();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTypedTextField1 = new citibob.swing.typed.JTypedTextField();
        jToolBar1 = new javax.swing.JToolBar();
        bDeleteQuery = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        tTestResults.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tTestResults);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setText("Number of Records Selected:");
        jPanel2.add(jLabel1);

        lQuerySize.setText("0");
        jPanel2.add(lQuerySize);

        jPanel2.add(jPanel4);

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        bApply.setText("Apply");
        bApply.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bApplyActionPerformed(evt);
            }
        });

        jPanel5.add(bApply);

        jPanel1.add(jPanel5, java.awt.BorderLayout.SOUTH);

        jSplitPane4.setRightComponent(jPanel1);

        editorPanel.setLayout(new java.awt.BorderLayout());

        eQueryEditor.setColName("equery");
        editorPanel.add(eQueryEditor, java.awt.BorderLayout.CENTER);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Query Name: ");
        jPanel6.add(jLabel2, new java.awt.GridBagConstraints());

        jTypedTextField1.setText("jTypedTextField1");
        jTypedTextField1.setColName("name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(jTypedTextField1, gridBagConstraints);

        editorPanel.add(jPanel6, java.awt.BorderLayout.NORTH);

        bDeleteQuery.setText("Delete Query");
        bDeleteQuery.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bDeleteQueryActionPerformed(evt);
            }
        });

        jToolBar1.add(bDeleteQuery);

        editorPanel.add(jToolBar1, java.awt.BorderLayout.SOUTH);

        jSplitPane4.setTopComponent(editorPanel);

        add(jSplitPane4, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents

	private void bDeleteQueryActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDeleteQueryActionPerformed
	{//GEN-HEADEREND:event_bDeleteQueryActionPerformed
//		fapp.runGui(EditQueryWiz.this, new StRunnable() {
//		public void run(SqlRunner str) throws Exception {
			
			if (JOptionPane.showConfirmDialog(EditQueryWiz.this,
				"Are you sure you wish to permanently\n" +
				"delete the current query?",
				"Delete Confirmation",
			JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
			this.wrapper.doSubmit("deletequery");
//		}});

// TODO add your handling code here:
	}//GEN-LAST:event_bDeleteQueryActionPerformed

	private void bApplyActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bApplyActionPerformed
	{//GEN-HEADEREND:event_bApplyActionPerformed
		fapp.runGui(this, new BatchRunnable() {
		public void run(SqlRunner str) throws SQLException {
			eQueryEditor.commitValue();
			EQuery eqy = eQueryEditor.getEQuery();
			
			// Main table
			String sql0 = eqy.getSql(fapp.getEquerySchema());
			testResults.setRows(str, sql0, null);
			
			// Summary counts
			String sql =
				DB.sqlCountIDList(sql0) + ";\n" +
				DB.sqlCountIDList(DB.removeDupsIDSql(sql0));
			str.execSql(sql, new RssRunnable() {
			public void run(SqlRunner str, ResultSet[] rss) throws SQLException {
				rss[0].next();
				int fullSize = rss[0].getInt(1);
				rss[1].next();
				int nodupSize = rss[1].getInt(1);
				lQuerySize.setText(""+fullSize + " (" + nodupSize + " merged households)");
			}});
		}});
	}//GEN-LAST:event_bApplyActionPerformed

public void backPressed()
	{ saveCurQuery(); }
public void nextPressed()
	{ saveCurQuery(); }

void saveCurQuery()
{
	fapp.runGui(this, new BatchRunnable() {
	public void run(SqlRunner str) throws SQLException {
		eQueryEditor.commitValue();
		equeryDm.doUpdate(str);
//		equery = eQueryEditor.getEQuery();
//		String equery = eQueryEditor.getValue();
//		String sql = "update equeries set lastmodified=now(), equery = "
//			+ SqlString.sql(equery)
//			+ " where equeryid = " + equeryID;
//		st.executeUpdate(sql);
	}});
}
public void getAllValues(java.util.Map map)
{
	map.put("equery", eQueryEditor.getEQuery());
	map.put("equeryname", jTypedTextField1.getValue());
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bApply;
    private javax.swing.JButton bDeleteQuery;
    private offstage.equery.swing.EQueryEditor eQueryEditor;
    private javax.swing.JPanel editorPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JToolBar jToolBar1;
    private citibob.swing.typed.JTypedTextField jTypedTextField1;
    private javax.swing.JLabel lQuerySize;
    private offstage.gui.FamilyTable tTestResults;
    // End of variables declaration//GEN-END:variables
// ================================================================

}
