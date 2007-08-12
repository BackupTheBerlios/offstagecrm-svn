/*
 * TMGrouper.java
 *
 * Created on August 11, 2007, 11:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import java.io.*;
import java.util.*;
import com.pdfhacks.*;
import citibob.sql.pgsql.*;
import java.sql.*;
import offstage.*;
import citibob.sql.*;
import citibob.swing.table.*;

/**
 *
 * @author citibob
 */
public class TableModelGrouper
{
	
int[] gcols;	// The columns to group by
String[] sGroupCols;
int startrow = 0;
JTypeTableModel mod;

public TableModelGrouper(JTypeTableModel mod, String[] sGroupCols)
{
	this.mod = mod;
	this.sGroupCols = sGroupCols;
	gcols = new int[sGroupCols.length];
	for (int i=0; i<sGroupCols.length; ++i) {
		gcols[i] = mod.findColumn(sGroupCols[i]);
		if (gcols[i] < 0) throw new ArrayIndexOutOfBoundsException("Unknown column name: " + sGroupCols[i]);
	}
}
/** Creates a new instance of TMGrouper */
public TableModelGrouper(JTypeTableModel mod, int[] groupCols)
{
	this.mod = mod;
	this.gcols = groupCols;
}

public int[] getGcols() { return gcols; }
public String[] getGcolNames() { return sGroupCols; }

boolean isEqual(Object a, Object b)
{
	if (a==b) return true;	// both null
	if (a==null || b==null) return false;	// one null, one non-null
	return a.equals(b);	// both non-null
}

/** Sees if row is a break from row-1 */
boolean isBreak(int row)
{
	for (int i=0; i<gcols.length; ++i) {
		if (!isEqual(mod.getValueAt(row, gcols[i]), mod.getValueAt(row-1,gcols[i]))) return true;
	}
	return false;
}

public JTypeTableModel next()
{
	if (startrow >= mod.getRowCount()) return null;		// eof
	for (int row = startrow+1; ; ++row) {
		if (row >= mod.getRowCount() || isBreak(row)) {
			int ostart = startrow;
			startrow = row;
			return new SubrowTableModel(mod, ostart, row);
		}
	}
}

}
