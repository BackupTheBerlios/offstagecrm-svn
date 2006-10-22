/*
 * DupCheckPanel.java
 *
 * Created on October 12, 2006, 8:31 PM
 */

package offstage.wizards.newrecord;

import offstage.db.*;
import offstage.*;
import citibob.multithread.*;
import citibob.swing.typed.*;
import java.sql.*;


/**
 *
 * @author  citibob
 */
public class IDListViewer extends javax.swing.JPanel {

FullEntityDbModel entityDb;
EntityListTableModel dupsModel;

/** Creates new form DupCheckPanel */
public IDListViewer() {
	initComponents();
}

//public void initRuntime(Statement st,
//EntityListTableModel dupsModel, FullEntityDbModel entityDb,
//ActionRunner guiRunner, SwingerMap smap)
//{
//	this.dupsModel = dupsModel;
//	this.entityDb = entityDb;
//}

public void initRuntime(Statement st,
FullEntityDbModel entityDb,
String idSql, String orderBy,
ActionRunner guiRunner, SwingerMap smap)
throws SQLException
{
	dupsModel = new EntityListTableModel();
	dupsModel.setRows(st, idSql, orderBy);
	this.entityDb = entityDb;
	dupsTable.setModel(dupsModel);
	mainEntityPanel.initRuntime(st, guiRunner, entityDb, smap);
}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        info.clearthought.layout.TableLayout _tableLayoutInstance;

        dupsTable = new citibob.swing.CitibobJTable();
        mainEntityPanel = new offstage.gui.MainEntityPanel();

        _tableLayoutInstance = new info.clearthought.layout.TableLayout();
        _tableLayoutInstance.setHGap(0);
        _tableLayoutInstance.setVGap(0);
        _tableLayoutInstance.setColumn(new double[]{info.clearthought.layout.TableLayout.MINIMUM,info.clearthought.layout.TableLayout.MINIMUM});
        _tableLayoutInstance.setRow(new double[]{info.clearthought.layout.TableLayout.FILL});
        setLayout(_tableLayoutInstance);

        dupsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        add(dupsTable, new info.clearthought.layout.TableLayoutConstraints(0, 0, 0, 0, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

        add(mainEntityPanel, new info.clearthought.layout.TableLayoutConstraints(1, 0, 1, 0, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

    }
    // </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private citibob.swing.CitibobJTable dupsTable;
    private offstage.gui.MainEntityPanel mainEntityPanel;
    // End of variables declaration//GEN-END:variables

}
