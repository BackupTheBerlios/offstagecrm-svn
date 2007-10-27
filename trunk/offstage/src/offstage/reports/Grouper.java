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
public abstract class Grouper
{

/** Used as an adapter to a ResultSet or TableModel */
public interface Model {
	/** Moves our cursor to the next row, returns false if EOF */
	public boolean next();

	public Object getVal(int col);

	public JType getJType(int col);
}


///** We have a new group -- called every time stuff changes, including at beginning*/
//abstract void groupChanged(int level);

abstract void groupStart(int level);
abstract void groupEnd(int level);

/** Called after groupChanged() for each row. */
abstract void processRow();

// ----------------------------------------
// ----------------------------------------

Model model;
int[][] gcols;
Object[][] lastVal;	// Value of each grouping level on last row
protected int lastLevel = -1;
protected int level = -1;		// min. level at which current row doesn't match last row at this level

/** Compares for equality, taking null into account */
private boolean equals(Object a, Object b)
{
	if (a == b) return true;
	if (a == null || b == null) return false;
	return a.equals(b);
}

/** groups[i][j]: Group level i involves grouping by column j */
public Grouper(Model model, int[][] gcols)
{
	this.model = model;
	this.gcols = gcols;

	// Allocate memory
	lastVal = new Object[gcols.length][];
	for (int i = 0; i < gcols.length; ++i) lastVal[i] = new Object[gcols[i].length];
}

public boolean nextRow()
{
	if (!model.next()) {
		return false;
		level = -1;
	}

	// See at what level current and last row mis-match
	lastLevel = level;
	match: for (level = 0; level < gcols.length; ++level) {
	for (int j=0; j< gcols[level].length; ++j) {
		if (!equals(lastVal[level][j], model.getVal(gcols[level][j]))) {
			for (int k=level; k<gcols.length; ++k) {
			for (int m=0; m<gcols[k].length; ++m) {
				
			}}
		}
	}}
}
//
//	// Go up and down levels
//	if (level >= 0) for (int i=gcols.length-1; i >= mismatch; --i) groupEnd(i);
//	for (int i=mismatch; i < gcols.length; ++i) groupStart(i);
//	level = mismatch;
//
//
//
//
//	}
//
//	// Finish off all groupings
//	for (int i=gcols.length-1; i >= 0; --i) groupEnd(i);
//
//
//
//
//	// Process...
//	while (model.next()) {
//		// See at what level current and last row mis-match
//		int mismatch;
//		match: for (mismatch = 0; mismatch < gcols.length; ++mismatch) {
//		for (int j=0; j< gcols[mismatch].length; ++j) {
//			if (!equals(last[mismatch][j], model.getVal(gcols[mismatch][j]))) break match;
//		}}
//
//		// Go up and down levels
//		if (level >= 0) for (int i=gcols.length-1; i >= mismatch; --i) groupEnd(i);
//		for (int i=mismatch; i < gcols.length; ++i) groupStart(i);
//		level = mismatch;
//	}
//
//	// Finish off all groupings
//	for (int i=gcols.length-1; i >= 0; --i) groupEnd(i);
//
//}

}
