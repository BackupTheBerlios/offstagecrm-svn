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

import citibob.swing.CitibobJTable;
import java.sql.*;
import java.io.*;
import javax.swing.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import java.util.prefs.*;
import offstage.*;
import citibob.multithread.*;
import offstage.gui.*;
import citibob.swing.typed.*;
import offstage.db.*;

/**
 *
 * @author  citibob
 */
public class ListQueryWiz extends citibob.swing.JPanelWiz {

citibob.app.App fapp;
//EQueryModel2 app;
EntityListTableModel testResults;
//Statement st;
ActionRunner runner;

SchemaBufDbModel equeriesDm;
public boolean ok = false;		// Did user exit by pressing OK?
//public int equeryID;			// Query ID selected by user

/** Creates new form EQueryBrowser */
public ListQueryWiz(citibob.app.App fapp) {
	super("Select Query");
	this.fapp = fapp;
	initComponents();
}
public ListQueryWiz(Statement st, final citibob.app.App fapp)
//public void initRuntime(Statement st, final citibob.app.App fapp)
throws SQLException
{
	this(fapp);
	
	// Create model and bind it to the JTable
	equeriesDm = tQueries.setSubSchemaDm(fapp.getSchema("equeries"),
		new String[] {"ID", "Name", "Modified"},
		new String[] {"equeryid", "name", "lastmodified"},
		new boolean[] {false, false, false},
		fapp.getSwingerMap());
	
	// Read the data
	equeriesDm.setOrderClause("lastmodified desc");
	equeriesDm.doSelect(st);
	
	tQueries.addMouseListener(new DClickTableMouseListener(tQueries) {
	public void doubleClicked(final int row) {
		fapp.runGui(ListQueryWiz.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			tQueries.getSelectionModel().setSelectionInterval(row, row);
			wrapper.doSubmit("next");
		}});
	}});
	
}

//void finishDialog(boolean b)
//{
//	int row = tQueries.getSelectionModel().getMinSelectionIndex();
//	if (row < 0 && b) return;	// Must select something first.
//	equeryID = (Integer)equeriesDm.getSchemaBuf().getValueAt(row, 0);
//	ok = b;
//	setVisible(false);
//}
//
int getEqueryID() {
	int row = tQueries.getSelectionModel().getMinSelectionIndex();
	int equeryID = (row < 0 ? -1 :
		(Integer)equeriesDm.getSchemaBuf().getValueAt(row, 0));
	return equeryID;
}
public void getAllValues(java.util.Map m)
{
	m.put("equeryid", getEqueryID());
//	m.put("submit", ok);
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

        jScrollPane3 = new javax.swing.JScrollPane();
        tQueries = new citibob.jschema.swing.SchemaBufTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        bDeleteQuery = new javax.swing.JButton();
        bNewQuery = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        tQueries.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tQueries);

        add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jLabel1.setText("Please select the query below you wish to edit.");
        add(jLabel1, java.awt.BorderLayout.NORTH);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Please select the query you wish to edit, or click on \"New Query\"");
        jPanel3.add(jLabel2, new java.awt.GridBagConstraints());

        add(jPanel3, java.awt.BorderLayout.NORTH);

        bDeleteQuery.setText("Delete");
        bDeleteQuery.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bDeleteQueryActionPerformed(evt);
            }
        });

        jToolBar1.add(bDeleteQuery);

        bNewQuery.setText("New");
        bNewQuery.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bNewQueryActionPerformed(evt);
            }
        });

        jToolBar1.add(bNewQuery);

        add(jToolBar1, java.awt.BorderLayout.SOUTH);

    }// </editor-fold>//GEN-END:initComponents

	private void bDeleteQueryActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDeleteQueryActionPerformed
	{//GEN-HEADEREND:event_bDeleteQueryActionPerformed
//System.out.println("hoi");
		fapp.runGui(ListQueryWiz.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			final int row = tQueries.getSelectionModel().getMinSelectionIndex();
			if (row < 0) return;
			SchemaBuf sb = equeriesDm.getSchemaBuf();
			final int equeryID = (Integer)sb.getValueAt(row, 0);
			final String equeryName = (String)sb.getValueAt(row, 1);
			if (JOptionPane.showConfirmDialog(ListQueryWiz.this,
				"Are you sure you wish to permanently\n" +
				"delete the selected query \"" + equeryName + "\"?",
				"Delete Confirmation",
			JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
			sb.deleteRow(row);
			equeriesDm.doUpdate(st);
//			equeriesDm.doDelete(st);
			equeriesDm.doSelect(st);
		}});
	}//GEN-LAST:event_bDeleteQueryActionPerformed

	private void bNewQueryActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bNewQueryActionPerformed
	{//GEN-HEADEREND:event_bNewQueryActionPerformed
		wrapper.doSubmit("newquery");
	}//GEN-LAST:event_bNewQueryActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bDeleteQuery;
    private javax.swing.JButton bNewQuery;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar jToolBar1;
    private citibob.jschema.swing.SchemaBufTable tQueries;
    // End of variables declaration//GEN-END:variables
// ================================================================

}
