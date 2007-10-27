/*
JSchema: library for GUI-based database applications
This file Copyright (c) 2006-2007 by Robert Fischer

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
 * CSVReportOutput.java
 *
 * Created on February 14, 2007, 11:35 PM
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

/**
 * TODO: Change this to use StringTableModel
 * @author citibob
 */
public class CSVReportOutput {

//SFormatter[] formatters;
//ColPermuteTableModel mod;
//SFormatterMap sfmap;

///** @param colNames Name of each column in finished report --- Null if use underlying column names
// @param sColMap Name of each column in underlying uModel  --- Null if wish to use all underlying columns */
//public CSVReportOutput(JTypeTableModel uModel,
//String[] colNames, String[] sColMap,
//SFormatterMap sfmap)
//{
//
//	mod = new ColPermuteTableModel(uModel, colNames, sColMap, null);
//
//	// Set up formatter for each column
//	int ncol = mod.getColumnCount();
//	formatters = new SFormatter[ncol];
//	for (int i=0; i<ncol; ++i) {
//		int ucol = mod.getColMap(i);
//		JType type = uModel.getJType(0, ucol);
//System.out.println(ucol + " " + uModel.getColumnName(ucol) + " " + type);
//		formatters[i] = sfmap.newSFormatter(type);
//	}
//}

///** Used to set a special (non-default) formatter for a particular column. */
//public void setSFormatterU(String uname, SFormatter fmt)
//{
//	int col = mod.findColumnU(uname);
//	formatters[col] = fmt;
//}


/** Creates a new instance of CSVReportOutput */
public void writeReport(File f) throws IOException, java.text.ParseException
{
	FileWriter out = null;
	try {
		writeReport(new FileWriter(f));
	} finally {
		try { out.close(); } catch(Exception e) {}
	}
}

/** Creates a new instance of CSVReportOutput */
public void writeReport(Writer out) throws IOException, java.text.ParseException
{
	int ncol = mod.getColumnCount();

//	// Start the output
//	PrintWriter pout = new PrintWriter(out);
//	for (int i=0; i<ncol; ++i) {
//		if (i>0) pout.print(",");
//		pout.print(mod.getColumnName(i));
//	}
//	pout.println();

	CSVPrinter pout = new com.Ostermiller.util.CSVPrinter(out);
	for (int i=0; i<ncol; ++i) {
		pout.print(mod.getColumnName(i));
	}
	pout.println();
	
	// Do each row
	for (int j=0; j<mod.getRowCount(); ++j) {
		for (int i=0; i<ncol; ++i) {
			Object o = mod.getValueAt(j,i);
//System.out.println("Column " + i + ": Formatting " + o + " with formatter: " + formatters[i]);
			String s = (o == null ? "" : formatters[i].valueToString(o));
//			if (s.indexOf('"') >= 0) {
//				// Quote only if needed
//				s = '"' + s.replace("\"", "\\\"") + '"';
//			}
			pout.print(s);
		}
		pout.println();
	}
	pout.flush();
}

//public static void main(String[] args) throws Exception
//{
//	String ss = "\"hoi\"";
//	System.out.println(ss.replace("\"", "\\\""));
//
//
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	Statement st = pool.checkout().createStatement();
//	citibob.text.SFormatterMap sfmap = new offstage.types.OffstageSFormatterMap();
//	citibob.sql.SqlTypeSet tset = new citibob.sql.pgsql.PgsqlTypeSet();
//
//	String sql =
//		" select * from mailings where groupid=295";
//	RSTableModel rst = new RSTableModel(tset);
//	rst.executeQuery(st, sql);
//
//	CSVReportOutput rpt = new CSVReportOutput(rst, null, null, sfmap);
//	rpt.setSFormatterU("phone", new offstage.types.PhoneFormatter());
//	rpt.writeReport(new File("/export/home/citibob/x.csv"));
//	
////	ResultSet rs = st.executeQuery(sql);
////	ResultSetMetaData meta = rs.getMetaData();
////	int ncol = meta.getColumnCount();
////	SFormatter[] formats = new SFormatter[ncol];
////	for (int i=0; i<ncol; ++i) {
////		if (i>1) System.out.print(",");
////		System.out.print(meta.getColumnLabel(i+1));
////		formats[i] = sfmap.newSFormatter(tset.getSqlType(meta, i));
////	}
////	System.out.println();
////	while (rs.next()) {
////		for (int i = 0; i < ncol; ++i) {
////			Object o = rs.getObject(i+1);
////			String s = (o == null ? "" : formats[i].valueToString(o));
////			if (s.indexOf('"') >= 0) {
////				// Quote only if needed
////				s = '"' + s.replace("\"", "\\\"") + '"';
////			}
////			if (i > 0) System.out.print(",");
////			System.out.print(s);
////		}
////		System.out.println();
////	}
//}

}
