/*
 * GrouperTableModel.java
 *
 * Created on August 11, 2007, 10:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import java.sql.*;
import citibob.text.*;
import citibob.swing.table.*;
import citibob.sql.*;
import java.io.*;
import citibob.swing.typed.JType;
import com.Ostermiller.util.*;
import javax.swing.table.*;

/**
 * A TableModel that represents just a subrange of rows of a parent TableModel.
Used for grouping the parent TableModel using TableModelGrouper.
 * @author citibob
 */
public class SubrowTableModel extends AbstractJTypeTableModel
{

JTypeTableModel mod;
int firstrow;
int nextrow;

/** Creates a new instance of GrouperTableModel */
public SubrowTableModel(JTypeTableModel mod, int firstrow, int nextrow)
{
	this.mod = mod;
	this.firstrow = firstrow;
	this.nextrow = nextrow;
}

/** Return SqlType for a cell.  If type depends only on col, ignores the row argument. */
public JType getJType(int row, int col) { return mod.getJType(row,col); }


public int getRowCount() { return nextrow - firstrow; }
public int getColumnCount() { return mod.getColumnCount(); }
public String 	getColumnName(int column) { return mod.getColumnName(column); }
public Object getValueAt(int row, int col) { return mod.getValueAt(row + firstrow,col); }
public Class 	getColumnClass(int columnIndex) { return mod.getColumnClass(columnIndex); }

public void setValueAt(Object obj, int row, int col) {mod.setValueAt(obj, row + firstrow, col); }


}
