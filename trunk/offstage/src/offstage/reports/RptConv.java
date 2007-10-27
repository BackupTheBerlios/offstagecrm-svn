/*
 * RptConv.java
 *
 * Created on October 26, 2007, 8:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import citibob.swing.typed.*;
import citibob.swing.table.*;
import citibob.app.*;
import citibob.jschema.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import net.sf.jasperreports.engine.*;
import java.util.*;
import citibob.text.*;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import net.sf.jasperreports.engine.util.*;
import com.Ostermiller.util.*;

public class RptConv {

//public static StringTableModel StringTableModel_TableModel(
//citibob.text.SFormatterMap sfmt, JTypeTableModel jtmod)
//{
//	return new StringTableModel(jtmod, sfmt);
//}
//
//public static StringTableModel StringTableModel_TableModel(
//citibob.app.App app, JTypeTableModel jtmod)
//	{ return StringTableModel_TableModel(app.getSFormatterMap(), jtmod); }

// ===================================================================
public static List<StringTableModel> toStringTableModel(
Grouper group, SFormatterMap sfmap,
String[] scols, SFormatter[] sfmt)
{
	LinkedList<StringTableModel> list = new LinkedList();

	int[] gcols = group.getGcols();
	String[] sgcols = group.getGcolNames();
	JodPdfWriter jout = new JodPdfWriter(app.getProps().getProperty("ooffice.exe"), new FileOutputStream(file), outExt);
	JTypeTableModel jtmod;
	try {
		while ((jtmod = group.next()) != null) {
			StringTableModel smod = new StringTableModel(jtmod, app.getSFormatterMap());
			if (sformattercols != null) for (int i=0; i<sformattercols.length; ++i)
				smod.setSFormatter(sformattercols[i], sformatters[i]);
			TemplateTableModel ttmod = new TemplateTableModel(smod);
			HashMap data = new HashMap();
				data.put("rs", ttmod);
			for (int i=0; i<gcols.length; ++i) {
				data.put("g0_" + sgcols[i], smod.getValueAt(0, gcols[i]));
			}
			InputStream in = ReportOutput.openTemplateFile(app, templateName);
			System.out.println("Formatting report " + jout.getNumReports());
			jout.writeReport(in, ext, data);
			in.close();
		}

}
// ===================================================================
// Final Report Outputs
public static void writeCSV(StringTableModel report, Writer out)
throws IOException
{
	int ncol = report.getColumnCount();

	CSVPrinter pout = new CSVPrinter(out);
	for (int i=0; i<ncol; ++i) {
		pout.print(report.getColumnName(i));
	}
	pout.println();
	
	// Do each row
	for (int j=0; j<report.getRowCount(); ++j) {
		for (int i=0; i<ncol; ++i) {
			pout.print((String)report.getValueAt(j,i));
		}
		pout.println();
	}
	pout.flush();
}

public static void writeCSV(StringTableModel report, File file)
throws IOException
{
	FileWriter out = null;
	try {
		writeCSV(new FileWriter(f), report);
	} finally {
		try { out.close(); } catch(Exception e) {}
	}
}
}
