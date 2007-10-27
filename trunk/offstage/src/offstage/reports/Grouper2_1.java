/*
 * TMGrouper.java
 *
 * Created on August 11, 2007, 11:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import com.sun.crypto.provider.RSACipher;
import java.io.*;
import java.util.*;
import com.pdfhacks.*;
import citibob.sql.pgsql.*;
import java.sql.*;
import offstage.*;
import citibob.sql.*;
import citibob.swing.table.*;
import citibob.swing.typed.*;

/**
 *
 * @author citibob
 */
public abstract class Grouper2_1
{

List[] lists;		// Currently active list for each grouper level

int curRow;			// Current row # we're scanning'
int start;			// Starting row of current lowest-level grouping

int[][] gcols;
JTypeTableModel model;

int level = -1;		// min. level at which current row doesn't match last row at this level
Object[][] last;	// Value of each grouping level on last row


public Grouper2_1(JTypeTableModel model, int[][] gcols)
{
	this.model = model;
	this.gcols = gcols;
	curRow = 0;

	// Allocate memory
	lists = new List[gcols.length];
	last = new Object[gcols.length][];
	for (int i = 0; i < gcols.length; ++i) last[i] = new Object[gcols[i].length];

	// Process...
	while (next()) {
		// See at what level current and last row mis-match
		int mismatch;
		match: for (mismatch = 0; mismatch < gcols.length; ++mismatch) {
		for (int j=0; j< gcols[mismatch].length; ++j) {
			Object curVal = model.getValueAt(curRow, gcols[mismatch][j]);
			Object lastVal = last[mismatch][j];
			if (!equals(lastVal, curVal)) break match;
		}}

		// Go up and down levels
		if (level >= 0) for (int i=gcols.length-1; i >= mismatch; --i) groupEnd(i);
		for (int i=mismatch; i < gcols.length; ++i) groupStart(i);
		level = mismatch;
	}

	// Finish off all groupings
	for (int i=gcols.length-1; i >= 0; --i) groupEnd(i);
}

boolean next()
{
	return ((++curRow) >= model.getRowCount());
}

void groupStart(int level)
{
	List ll = new LinkedList();
	lists[level] = ll;
	if (level > 0) lists[level-1].add(ll);
	start = curRow;
}
void groupEnd(int level)
{
	if (level == gcols.length - 1) {
		lists[level].add(newTableModel(start,curRow));
		start = curRow;
	}
}

Object newTableModel(int firstrow, int nextrow)
{
	return new SubrowTableModel(model, firstrow, nextrow);
}

///** Called after groupChanged() for each row. */
//void processRow() {}

// ----------------------------------------
// ----------------------------------------

/** Compares for equality, taking null into account */
private boolean equals(Object a, Object b)
{
	if (a == null) return (b == null);
	if (b == null) return false;
	return a.equals(b);
}


}

