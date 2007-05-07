///*
// * CSVReport.java
// *
// * Created on February 14, 2007, 11:35 PM
// *
// * To change this template, choose Tools | Template Manager
// * and open the template in the editor.
// */
//
//package offstage.reports;
//
//import java.sql.*;
//import citibob.text.*;
//import citibob.swing.table.*;
//import java.io.*;
//
///**
// *
// * @author citibob
// */
//public class CSVReport {
//
//SFormatter[] formatters;
//ColPermuteTableModel mod;
//SFormatterMap sfmap;
//
//public CSVReport(JTypeTableModel uModel,
//String[] colNames, String[] sColMap,
//SFormatterMap sfmap)
//{
//	int ncol = mod.getColumnCount();
//
//	mod = new ColPermuteTableModel(uModel, colNames, sColMap, null);
//
//	// Set up formatter for each column
//	formatters = new SFormatter[ncol];
//	for (int i=0; i<ncol; ++i)
//		formatters[i] = sfmap.newSFormatter(mod.getJType(0, i));
//}
//public void setSFormatterU(String uname, SFormatter fmt)
//{
//	int col = mod.findColumnU(uname);
//	formatters[col] = fmt;
//}
//
//
///** Creates a new instance of CSVReport */
//public void writeReport(JTypeTableModel mod, File fout, SFormatterMap sfmap){}
//
///** Creates a new instance of CSVReport */
//public void writeReport(JTypeTableModel mod, File fout, SFormatterMap sfmap)
//{
//	int ncol = mod.getColumnCount();
//
//	// Set up formatter for each column
//	formatters = new SFormatter[ncol];
//	for (int i=0; i<ncol; ++i)
//		formatters[i] = sfmap.newSFormatter(mod.getJType(0, i));
//
//	// Start the output
//	PrintWriter out = new PrintWriter(new FileWriter(fout));
//	for (int i=0; i<ncol; ++i) {
//		if (i>0) out.print(",");
//		out.print(meta.getColumnLabel(i+1));
//	}
//	out.println();
//
//	// Do each row
//	for (int j=0; j<mod.getRowCount(); ++j) {
//		for (int i=0; i<ncol; ++i) {
//			Object o = mod.getValueAt(i,j);
//			String s = (o == null ? "" : formatters[i].valueToString(o));
//			if (s.indexOf('"') >= 0) {
//				// Quote only if needed
//				s = '"' + s.replace("\"", "\\\"") + '"';
//			}
//			out.print(s);
//		}
//		out.println();
//	}
//	out.close();
//}
//
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
//	ResultSet rs = st.executeQuery(sql);
//	ResultSetMetaData meta = rs.getMetaData();
//	int ncol = meta.getColumnCount();
//	SFormatter[] formats = new SFormatter[ncol];
//	for (int i=0; i<ncol; ++i) {
//		if (i>1) System.out.print(",");
//		System.out.print(meta.getColumnLabel(i+1));
//		formats[i] = sfmap.newSFormatter(tset.getSqlType(meta, i));
//	}
//	System.out.println();
//	while (rs.next()) {
//		for (int i = 0; i < ncol; ++i) {
//			Object o = rs.getObject(i+1);
//			String s = (o == null ? "" : formats[i].valueToString(o));
//			if (s.indexOf('"') >= 0) {
//				// Quote only if needed
//				s = '"' + s.replace("\"", "\\\"") + '"';
//			}
//			if (i > 0) System.out.print(",");
//			System.out.print(s);
//		}
//		System.out.println();
//	}
//}
//
//}
