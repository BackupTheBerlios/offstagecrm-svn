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
	
RSTableModel searchResults;
citibob.app.App app;

/** Creates new form SimpleSearchPanel */
public EntitySelector() {
	initComponents();
}


public void initRuntime(citibob.app.App xapp) //Statement st, FullEntityDbModel dm)
{
	this.app = xapp;
	
	searchResults = new RSTableModel(app.getSqlTypeSet()) {
	public void executeQuery(Statement st, String text) throws SQLException {
		// Convert text to a search query for entityid's
		String idSql = DB.simpleSearchSql(text);
		if (idSql == null) return;		// no query

		// Search for appropriate set of columns, given that list of IDs.
		String sql =
			" create temporary table _ids (id int); delete from _ids;\n" +

			" delete from _ids;\n" +

			" insert into _ids (id) " + idSql + ";\n" +

			" select p.entityid," +
			" (case when lastname is null then '' else lastname || ', ' end ||" +
			" case when firstname is null then '' else firstname || ' ' end ||" +
			" case when middlename is null then '' else middlename end ||" +
			" case when orgname is null then '' else ' (' || orgname || ')' end" +
			" ) as name," +
//			" city as tooltip," +
			" ('<html>' ||" +
			" case when city is null then '' else city || ', ' end ||" +
			" case when state is null then '' else state end || '<br>' ||" +
			" case when occupation is null then '' else occupation || '<br>' end ||" +
			" case when email is null then '' else email || '' end ||" +
			" '</html>') as tooltip," +
			" p.entityid = p.primaryentityid as isprimary" +
			" from persons p, _ids" +
			" where p.entityid = _ids.id" +
			" order by name;\n" +

			" drop table _ids";
		ResultSet rs = st.executeQuery(sql);
		setNumRows(0);
		addAllRows(rs);
		rs.close();
//		super.executeQuery(st, sql);
	}};
	// PostgreSQL doesn't properly return data types of headings above, given
	// the computed columns.  So we must set the column types ourselves.
	searchResults.setColHeaders(new RSTableModel.Col[] {
		new RSTableModel.Col("entityid", new SqlInteger(true)),
		new RSTableModel.Col("name", new SqlString(true)),
		new RSTableModel.Col("tooltip", new SqlString(true)),
		new RSTableModel.Col("isprimary", new SqlBool(true))
	});
		
	// Add the model (with tooltips)
	searchResultsTable.setModelU(searchResults,
		new String[] {"Name"},
		new String[] {"name"},
		new String[] {"tooltip"},
		new boolean[] {false},
		app.getSwingerMap(), app.getSFormatterMap());
	searchResultsTable.setValueColU("entityid");
	super.setSubWidget(searchResultsTable);

	
	// Pressing ENTER will initiate search.
	searchWord.addKeyListener(new KeyAdapter() {
	public void keyTyped(KeyEvent e) {
		//System.out.println(e.getKeyChar());
		if (e.getKeyChar() == '\n') runSearch();
	}});
}

	
public void runSearch() {
	app.runGui(this, new StRunnable() {
	public void run(Statement st) throws Exception {
		String text = searchWord.getText();
		searchResults.executeQuery(st, text);
	}});
}

/** Allows others to add a double-click-to-select mouse listener. */
public JTypedSelectTable getSearchTable()
	{ return searchResultsTable; }
// ----------------------------------------------------------------------

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
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        searchWord = new javax.swing.JTextField();
        bSearch = new javax.swing.JButton();
        FamilyScrollPanel = new javax.swing.JScrollPane();
        searchResultsTable = new citibob.swing.typed.JTypedSelectTable();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));

        searchWord.setPreferredSize(new java.awt.Dimension(4, 19));
        searchWord.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                searchWordActionPerformed(evt);
            }
        });

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

	private void searchWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchWordActionPerformed
// TODO add your handling code here:
	}//GEN-LAST:event_searchWordActionPerformed

	
	private void bSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSearchActionPerformed
	app.runGui(this, new StRunnable() {
	public void run(Statement st) throws Exception {
		runSearch();
	}});
	}//GEN-LAST:event_bSearchActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane FamilyScrollPanel;
    private javax.swing.JButton bSearch;
    private javax.swing.JPanel jPanel1;
    private citibob.swing.typed.JTypedSelectTable searchResultsTable;
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