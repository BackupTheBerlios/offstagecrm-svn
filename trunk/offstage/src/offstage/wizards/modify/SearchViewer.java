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
 * DupCheckPanel.java
 *
 * Created on October 12, 2006, 8:31 PM
 */

package offstage.wizards.modify;

import offstage.db.*;
import offstage.*;
import java.sql.*;
import citibob.swing.*;
import citibob.sql.*;
import offstage.devel.gui.DevelModel;

/**
 *
 * @author  citibob
 */
public class SearchViewer extends javax.swing.JPanel {

DevelModel dmod;
EntityListTableModel dupsModel;

/** Creates new form DupCheckPanel */
public SearchViewer() {
	initComponents();
}

//public void initRuntime(SqlRunner str,
//EntityListTableModel dupsModel, FulldmodModel dmod,
//ActionRunner guiRunner, SwingerMap smap)
//{
//	this.dupsModel = dupsModel;
//	this.dmod = dmod;
//}

public void initRuntime(SqlRunner str,
FrontApp fapp)
//FulldmodModel xdmod,
//String idSql, String orderBy,
//final ActionRunner guiRunner, SwingerMap smap)
throws SQLException
{
	dmod = new DevelModel(fapp);
	searchPanel.initRuntime(fapp, dmod);
	mainEntityPanel.initRuntime(str, fapp, dmod);
//dmod, smap);
//
//	dupsModel = new EntityListTableModel();
//	dupsModel.setRows(st, idSql, orderBy);
//	this.dmod = xdmod;
//	dupsTable.initRuntime(dupsModel);
//	DClickTableMouseListener dclick =
//		new DClickTableMouseListener(dupsTable) {
//		public void doubleClicked(final int row) {
//			guiRunner.doRun(new StRunnable() {
//			public void run(Statement st) throws Exception {
//				// Make sure it's selected in the GUI
//				dupsTable.getSelectionModel().setSelectionInterval(row, row);
//
//				// Process the selection
//				int entityid = getSelectedEntityID(dupsTable);
//				if (entityid < 0) return;
//				dmod.setKey(entityid);
//				dmod.doSelect(st);
//			}});
//		}};
//	
//	// Double-clicking will go to selected person
//	dupsTable.addMouseListener(dclick);
//	dclick.doubleClicked(0);		// There's at least one row here, select it.
//
}

int getSelectedEntityID(CitibobJTable searchResultsTable)
{
	int selected = searchResultsTable.getSelectedRow();
	if (selected < 0) return -1;
	int entityID = dupsModel.getEntityID(selected);
	return entityID;
}

public int getDisplayedEntityID()
{
	return dmod.getEntityId();
}
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jSplitPane2 = new javax.swing.JSplitPane();
        mainEntityPanel = new offstage.devel.gui.PersonPanel();
        searchPanel = new offstage.gui.SimpleSearchPanel();

        setLayout(new java.awt.BorderLayout());

        jSplitPane2.setDividerLocation(200);
        jSplitPane2.setRightComponent(mainEntityPanel);

        jSplitPane2.setLeftComponent(searchPanel);

        add(jSplitPane2, java.awt.BorderLayout.CENTER);

    }
    // </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane jSplitPane2;
    private offstage.devel.gui.PersonPanel mainEntityPanel;
    private offstage.gui.SimpleSearchPanel searchPanel;
    // End of variables declaration//GEN-END:variables

}
