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
public abstract class Grouper2
{

/** Moves our cursor to the next row, returns false if EOF */
public boolean next();

public Object getVal(int col);


///** We have a new group -- called every time stuff changes, including at beginning*/
//abstract void groupChanged(int level);

abstract void groupStart(int level);
abstract void groupEnd(int level);

/** Called after groupChanged() for each row. */
abstract void processRow();

// ----------------------------------------
// ----------------------------------------

int level = -1;		// min. level at which current row doesn't match last row at this level
Object[][] last;	// Value of each grouping level on last row

/** Compares for equality, taking null into account */
private boolean equals(Object a, Object b)
{
	if (a == null) return (b == null);
	if (b == null) return false;
	return a.equals(b);
}

/** groups[i][j]: Group level i involves grouping by column j */
public void group(int[][] gcols)
{
	// Allocate memory
	last = new Object[gcols.length][];
	for (int i = 0; i < gcols.length; ++i) last[i] = new Object[gcols[i].length];

	// Process...
	while (next()) {
		// See at what level current and last row mis-match
		int mismatch;
		match: for (mismatch = 0; mismatch < gcols.length; ++mismatch) {
		for (int j=0; j< gcols[mismatch].length; ++j) {
			if (!equals(last[mismatch][j], getVal(gcols[mismatch][j]))) break match;
		}}

		// Go up and down levels
		if (level >= 0) for (int i=gcols.length-1; i >= mismatch; --i) groupEnd(i);
		for (int i=mismatch; i < gcols.length; ++i) groupStart(i);
		level = mismatch;
	}

	// Finish off all groupings
	for (int i=gcols.length-1; i >= 0; --i) groupEnd(i);

}

}
