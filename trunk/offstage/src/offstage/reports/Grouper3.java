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
import freemarker.template.*;

/**
 *
 * @author citibob
 */
public class Grouper3 extends Grouper
{

Map<String,Integer> fieldMap;

/** Next versions of these */
TemplateCollectionModel[] gcoll;
TemplateModelIIterator[] giter;

// Leaf model --- current low-level table being iterated
TemplateCollectionModel lcoll;
TemplateModelIIterator liter;
TemplateHashModel rowModel = new TemplateHashModel() {
	public TemplateModel get(java.lang.String key) {
		return new EleModel(model.getVal(fieldMap.get(key)));
	}
	public boolean 	isEmpty() { return model.getColumnCount() == 0; }
};

// ----------------------------------------
// ----------------------------------------
class LeafModel implements TemplateCollectionModel {
public TemplateModelIIterator iterator() {
	return liter;
}}
class LeafIterator implements TemplateModelIIterator {
	public boolean hasNext() {
		if (!super.nextRow()) return false;
		return (super.level == gcols.length);
	}
	public TemplateModel next() {
		return rowModel;
	}
}
// ----------------------------------------
// ============================================================
class RowModel implements TemplateHashModel {
	int row;
	public RowModel(int row) { this.row = row; }
	public TemplateModel get(java.lang.String key) {
		return new EleModel((String)model.getValueAt(row, model.findColumn(key)));
	}
	public boolean 	isEmpty() { return model.getColumnCount() == 0; }
}
// ============================================================
class EleModel implements TemplateScalarModel {
	String val;
	public EleModel(String val) { this.val = val; }
	public String getAsString() { return val; }
}
// ============================================================




Model model;
int[][] gcols;
Object[][] lastVal;	// Value of each grouping level on last row
int lastLevel = -1;
int level = -1;		// min. level at which current row doesn't match last row at this level

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


	if (!model.next()) return false;

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
