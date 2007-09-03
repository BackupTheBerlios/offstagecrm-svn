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

package offstage.swing.typed;
import java.sql.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import citibob.multithread.*;
import offstage.FrontApp;
import offstage.db.FullEntityDbModel;
import offstage.db.*;
import java.awt.event.*;
import citibob.swing.typed.*;
import javax.swing.table.*;
import java.awt.*;

/**
 *
 * @author  citibob
 */
public class EntitySelector extends citibob.swing.typed.JTypedPanel {
	
// searchResultsTable is the main "sub widget", whose value change events get reported in JTypedPanel
citibob.app.App app;

/** Creates new form SimpleSearchPanel */
public EntitySelector() {
	initComponents();
}


public void initRuntime(citibob.app.App xapp) //Statement st, FullEntityDbModel dm)
{
	this.app = xapp;
	searchResultsTable.initRuntime(app);
	super.setSubWidget(searchResultsTable);
	
	// Pressing ENTER will initiate search.
	searchWord.addKeyListener(new KeyAdapter() {
	public void keyTyped(KeyEvent e) {
		//System.out.println(e.getKeyChar());
		if (e.getKeyChar() == '\n') runSearch();
	}});
}

public void setSearch(SqlRunner str, String text)
throws SQLException
{
		String idSql = DB.simpleSearchSql(text);
		searchResultsTable.executeQuery(str, idSql, null);
		str.execUpdate(new UpdRunnable() {
		public void run(SqlRunner str) throws Exception {
			if (searchResultsTable.getModel().getRowCount() == 1) {
				searchResultsTable.setRowSelectionInterval(0,0);	// Should fire an event...
			}
		}});
}

void runSearch() {
	app.runGui(this, new BatchRunnable() {
	public void run(SqlRunner str) throws Exception {
		String text = searchWord.getText();
		setSearch(str, text);
	}});
}


///** Allows others to add a double-click-to-select mouse listener. */
//public JTypedSelectTable getSearchTable()
//	{ return searchResultsTable; }
// ----------------------------------------------------------------------

/** Used when this widget is in a popup; put the focus immediately on the search field. */
public void requestTextFocus()
{
	searchWord.setText("");
	searchWord.requestFocus();
}

/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        jPanel1 = new javax.swing.JPanel();
        searchWord = new javax.swing.JTextField();
        bSearch = new javax.swing.JButton();
        FamilyScrollPanel = new javax.swing.JScrollPane();
        searchResultsTable = new offstage.swing.typed.IdSqlTable();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));

        jPanel1.add(searchWord);

        bSearch.setText("Search");
        bSearch.setPreferredSize(new java.awt.Dimension(84, 25));
        bSearch.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bSearchActionPerformed(evt);
            }
        });

        jPanel1.add(bSearch);

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

    }// </editor-fold>//GEN-END:initComponents

	
	private void bSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSearchActionPerformed
//	app.runGui(this, new StRunnable() {
//	public void run(Statement st) throws Exception {
		runSearch();
//	}});
	}//GEN-LAST:event_bSearchActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane FamilyScrollPanel;
    private javax.swing.JButton bSearch;
    private javax.swing.JPanel jPanel1;
    private offstage.swing.typed.IdSqlTable searchResultsTable;
    private javax.swing.JTextField searchWord;
    // End of variables declaration//GEN-END:variables
// ===========================================================
///** Pass along change in value from underlying typed widget --- but only
// if new value is non-null.   This widget can onl*/
//public void propertyChange(java.beans.PropertyChangeEvent evt) {
//	Object newval = evt.getNewValue();
//	firePropertyChange("value", evt.getOldValue(), newval);
//}	
// ===========================================================

	
public static void main(String[] args) throws Exception
{	
	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
	Statement st = pool.checkout().createStatement();
	FrontApp fapp = new FrontApp(pool,null);
	
	javax.swing.JFrame frame = new javax.swing.JFrame();
	EntitySelector panel = new EntitySelector();
	panel.initRuntime(fapp);
	frame.getContentPane().add(panel);
	frame.pack();
	frame.setVisible(true);
}

	
}
