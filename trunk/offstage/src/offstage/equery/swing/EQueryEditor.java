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
 * EQueryEditor.java
 *
 * Created on July 2, 2005, 11:41 AM
 */

package offstage.equery.swing;

import citibob.swing.*;
import citibob.swing.table.*;
import citibob.swing.typed.*;
import java.sql.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import citibob.jschema.*;
import offstage.equery.*;
import java.io.*;
import java.text.*;

/**
 *
 * @author  citibob
 */
public class EQueryEditor extends javax.swing.JPanel
implements TypedWidget
{

	EQueryTableModel2 model;

    /** Creates new form EQueryEditor */
    public EQueryEditor() {
        initComponents();
		
		eClauseScrollPane.setRowHeaderView(new TableRowHeader(eClauseScrollPane, eQueryTable, 15));
    }

	public void initRuntime(EQueryTableModel2 qm, SwingerMap smap)
	throws SQLException
	{
		this.model = qm;
//		eQueryTable.setModel(new EQueryTableModel2());
		eQueryTable.setModel(qm);
		eQueryTable.setSwingerMap(smap);
		
		JDateType jt = new citibob.swing.typed.JDate(true);
		DateFormat fmt = new SimpleDateFormat("MM-dd-yyyy");
		Swinger swinger = new JDateSwinger(jt, fmt);
		dtFirst.setJType(swinger);
		dtNext.setJType(swinger);
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        eClauseScrollPane = new javax.swing.JScrollPane();
        eQueryTable = new offstage.equery.swing.EQueryTable();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        bAddClause = new javax.swing.JButton();
        bAddElement = new javax.swing.JButton();
        bRemoveRow = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        dtFirst = new citibob.swing.typed.JTypedDateChooser();
        jLabel2 = new javax.swing.JLabel();
        dtNext = new citibob.swing.typed.JTypedDateChooser();
        jLabel3 = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        eQueryTable.setModel(new javax.swing.table.DefaultTableModel(
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
        eClauseScrollPane.setViewportView(eQueryTable);

        add(eClauseScrollPane, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.BorderLayout());

        bAddClause.setText("+Clause");
        bAddClause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddClauseActionPerformed(evt);
            }
        });

        jToolBar1.add(bAddClause);

        bAddElement.setText("+Element");
        bAddElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddElementActionPerformed(evt);
            }
        });

        jToolBar1.add(bAddElement);

        bRemoveRow.setText("-");
        bRemoveRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRemoveRowActionPerformed(evt);
            }
        });

        jToolBar1.add(bRemoveRow);

        jPanel1.add(jToolBar1, java.awt.BorderLayout.WEST);

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setText("Last Modified from");
        jPanel2.add(jLabel1);

        jPanel2.add(dtFirst);

        jLabel2.setText("-");
        jPanel2.add(jLabel2);

        jPanel2.add(dtNext);

        jLabel3.setText("(not inclusive)");
        jPanel2.add(jLabel3);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.SOUTH);

    }
    // </editor-fold>//GEN-END:initComponents

public int getSelectedRow()
{
	ListSelectionModel lsm = eQueryTable.getSelectionModel();
	if (lsm.isSelectionEmpty()) {
		return -1;
	} else {
		int selectedRow = lsm.getMinSelectionIndex();
		return selectedRow;
	}
//	put this in jtypetable?
}

	private void bAddClauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddClauseActionPerformed
		int row = getSelectedRow();
		if (row < 0) row = eQueryTable.getModel().getRowCount() - 1;
		model.insertClause(row, new EClause());
		model.insertElement(getSelectedRow(), new Element());
	}//GEN-LAST:event_bAddClauseActionPerformed

	private void bRemoveRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRemoveRowActionPerformed
	model.removeRow(getSelectedRow());
	}//GEN-LAST:event_bRemoveRowActionPerformed

private void bAddElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddElementActionPerformed
	model.insertElement(getSelectedRow(), new Element());
}//GEN-LAST:event_bAddElementActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAddClause;
    private javax.swing.JButton bAddElement;
    private javax.swing.JButton bRemoveRow;
    private citibob.swing.typed.JTypedDateChooser dtFirst;
    private citibob.swing.typed.JTypedDateChooser dtNext;
    private javax.swing.JScrollPane eClauseScrollPane;
    private offstage.equery.swing.EQueryTable eQueryTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

	
public void setEnabled(boolean e)
{
	eQueryTable.setEnabled(e);
	jToolBar1.setEnabled(e);
}

	
// ================================================================
/** Gets the EQuery value out */
public EQuery getEQuery()
{
	EQuery q = model.getQuery();
	if (q != null) {
		q.setLastUpdatedFirst((java.util.Date)dtFirst.getValue());
		q.setLastUpdatedNext((java.util.Date)dtNext.getValue());
	}
	return q;
}
/** Returns last legal value of the widget.  Same as method in JFormattedTextField */
public String getValue()
{
	getEQuery();
	return model.getSQuery();
}


/** Sets the value.  Same as method in JFormattedTextField.  Fires a
 * propertyChangeEvent("value") when calling setValue() changes the value. */
public void setValue(Object o)
{
	Object oldValue = getValue();
	model.setSQuery((String)o);
	EQuery q = model.getQuery();
	dtFirst.setValue(q.getLastUpdatedFirst());
	dtNext.setValue(q.getLastUpdatedNext());
	this.firePropertyChange("value", oldValue, getValue());
}

public void commitValue()
{
	firePropertyChange("value", null, getValue());
}
///** fire a propertyChange event on the current value. */
//public void commitValue()
//{
//	Object oldValue = getValue();
//	model.setSQuery((String)o);
//	this.firePropertyChange("value", oldValue, getValue());	
//}

/** From TableCellEditor (in case this is being used in a TableCellEditor):
 * Tells the editor to stop editing and accept any partially edited value
 * as the value of the editor. The editor returns false if editing was not
 * stopped; this is useful for editors that validate and can not accept
 * invalid entries. */
public boolean stopEditing()
	{ return true; }

/** Is this object an instance of the class available for this widget?
 * If so, then setValue() will work.  See SqlType.. */
public boolean isInstance(Object o)
	{ return o instanceof String; }

/** Set up widget to edit a specific SqlType.  Note that this widget does not
 have to be able to edit ALL SqlTypes... it can throw a ClassCastException
 if asked to edit a SqlType it doesn't like. */
public void setJType(citibob.swing.typed.Swinger f) throws ClassCastException
{
 }

String colName;
/** Row (if any) in a RowModel we will bind this to at runtime. */
public String getColName()
	{ return colName; }
/** Row (if any) in a RowModel we will bind this to at runtime. */
public void setColName(String col)
	{ this.colName = col; }


//// =====================================================
//// Methods implemented in java.awt.Component
///** Implemented in java.awt.Component */
//public void setEnabled(boolean enabled);
//
///** Implemented in java.awt.Component --- property will be "value" */
//public void addPropertyChangeListener(String property, java.beans.PropertyChangeListener listener);
///** Implemented in java.awt.Component --- property will be "value"  */
//public void removePropertyChangeListener(String property, java.beans.PropertyChangeListener listener);
// =====================================================

	
	
}
