/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2007 by Robert Fischer

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
 * GuiUtil.java
 *
 * Created on July 8, 2007, 11:18 PM
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

/**
 *
 * @author citibob
 */
public class ReportOutput
{

public static InputStream openTemplateFile(App app, String name) throws IOException
{
	// First: try loading external file
//	File dir = new File(System.getProperty("user.dir"), "config");
	File f = new File(app.getConfigDir().getPath() + File.separatorChar + "reports" + File.separatorChar + name);
	if (f.exists()) {
System.out.println("Loading template from filesystem: " + f);
		return new FileInputStream(f);
	}

	// File doesn't exist; read from inside JAR file instead.
//	Class klass = offstage.config.OffstageVersion.class;
//	String resourceName = klass.getPackage().getName().replace('.', '/') + "/" + name;
//	return klass.getClassLoader().getResourceAsStream(resourceName);
	String resourceName = "offstage/reports/" + name;
System.out.println("Loading template as resource: " + resourceName);
	return ReportOutput.class.getClassLoader().getResourceAsStream(resourceName);
}

public static void viewJasperReport(App app, String templateName, JRDataSource jrdata, Map params)
throws JRException, IOException
{	
	InputStream reportIn = openTemplateFile(app, templateName);
	JasperReport jasperReport = (templateName.endsWith(".jrxml") ?
		JasperCompileManager.compileReport(reportIn) :
		(JasperReport)JRLoader.loadObject(reportIn));
	params = (params == null ? new HashMap() : params);
	JasperPrint jprint = net.sf.jasperreports.engine.JasperFillManager.fillReport(jasperReport, params, jrdata);
	offstage.reports.PrintersTest.checkAvailablePrinters();		// Java/CUPS/JasperReports bug workaround for Mac OS X
	net.sf.jasperreports.view.JasperViewer.viewReport(jprint, false);
	reportIn.close();
}


/** @param reportName Name of report to be used in preferences node pathname.
 @param ext Filename extension (WITH the dot) to use on report.
 @param title Title to display in chooser dialog.
 */
public static File chooseReportOutput(App app, Component parent,
String reportName, String ext, String title) throws Exception
{
	java.util.prefs.Preferences pref = app.userRoot().node("reports");
	final String dotExt = ext;
	final String starExt = "*" + ext;
	String dir = pref.get(reportName, null);
	JFileChooser chooser = new JFileChooser(dir);
	chooser.setDialogTitle("Save " + title);
	chooser.addChoosableFileFilter(
		new javax.swing.filechooser.FileFilter() {
		public boolean accept(File file) {
			String filename = file.getName();
			return filename.endsWith(dotExt);
		}
		public String getDescription() {
			return starExt;
		}
	});
	String path = null;
	File file;
	for (;;) {
		chooser.showSaveDialog(parent);

		path = chooser.getCurrentDirectory().getAbsolutePath();
		if (chooser.getSelectedFile() == null) return null;
		String fname = chooser.getSelectedFile().getPath();
		if (!fname.endsWith(dotExt)) fname = fname + dotExt;
		file = new File(fname);
		if (!file.exists()) break;
		if (JOptionPane.showConfirmDialog(
			parent, "The file " + file.getName() + " already exists.\nWould you like to ovewrite it?",
			"Overwrite File?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) break;
	}
	pref.put("saveReportDir", path);
	return file;
}
public static File saveCSVReport(App app, Component parent,
String title, JTypeTableModel report) throws Exception
{
	File file = chooseReportOutput(app, parent,
		"csvreport", ".csv", title);
	if (file == null) return null;

	CSVReportOutput csv = new CSVReportOutput(report, null, null, app.getSFormatterMap());
	csv.writeReport(file);
	return file;
}
//// ====================================================================
//public static File saveJodReport(App app, Component parent,
//String title, TableModelGrouper group,
//String[] sformattercols, SFormatter[] sformatters) throws Exception
//{
//	File file = chooseReportOutput(app, parent,
//		"pdfreport", ".pdf", title);
//	
//	int[] gcols = group.getGcols();
//	String[] sgcols = group.getGcolNames();
//	JodPdfWriter jout = new JodPdfWriter(app.getProps().getProperty("ooffice.exe"), new FileOutputStream(file));
//	JTypeTableModel jtmod;
//	try {
//		while ((jtmod = group.next()) != null) {
//			StringTableModel smod = new StringTableModel(jtmod, app.getSFormatterMap());
//			if (sformattercols != null) for (int i=0; i<sformattercols.length; ++i)
//				smod.setSFormatter(sformattercols[i], sformatters[i]);
//			TemplateTableModel ttmod = new TemplateTableModel(smod);
//			HashMap data = new HashMap();
//				data.put("rs", ttmod);
//			for (int i=0; i<gcols.length; ++i) {
//				data.put("g0_" + sgcols[i], smod.getValueAt(0, gcols[i]));
//			}
//			jout.writeReport(ReportOutput.openTemplateFile(app, "StudentSchedule.odt"), data);
//		}
//	} finally {
//		jout.close();
//	}
//
//	citibob.gui.BareBonesPdf.view(file);
////	Runtime.getRuntime().exec("acroread " + file.getPath());
//	return file;
//}
//
//public static File saveJodReport(App app, Component parent,
//String title, HashMap jodModel) throws Exception
//{
//	File file = chooseReportOutput(app, parent,
//		"pdfreport", ".pdf", title);
//	
//	JodPdfWriter jout = new JodPdfWriter(app.getProps().getProperty("ooffice.exe"), new FileOutputStream(file));
//	try {
//			jout.writeReport(ReportOutput.openTemplateFile(app, "AcctStatement.odt"), jodModel);
//	} finally {
//		jout.close();
//	}
//
//	citibob.gui.BareBonesPdf.view(file);
////	Runtime.getRuntime().exec("acroread " + file.getPath());
//	return file;
//}
// =========================================================================
public static File viewJodReport(App app, String templateName,
TableModelGrouper group,
String[] sformattercols, SFormatter[] sformatters) throws Exception
{
	int dot = templateName.lastIndexOf('.');
	String outBase = templateName.substring(0, dot);
	String ext = templateName.substring(dot+1);
	String outExt = "pdf";
	File file = File.createTempFile(outBase, "." + outExt);
	file.deleteOnExit();

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
	} finally {
		jout.close();
	}

	if (jout.getNumReports() == 0) {
		javax.swing.JOptionPane.showMessageDialog(null,
			"The report has no pages.");
		return null;
	} else {
		citibob.gui.BareBonesPdf.view(file);
		return file;
	}
//	Runtime.getRuntime().exec("acroread " + file.getPath());
}

public static File viewJodReport(App app, String templateName,
java.util.List models) throws Exception
{
	int dot = templateName.lastIndexOf('.');
	String outBase = templateName.substring(0, dot);
	String ext = templateName.substring(dot+1);
	String outExt = "pdf";
	File file = File.createTempFile(outBase, "." + outExt);
	file.deleteOnExit();
	
	JodPdfWriter jout = new JodPdfWriter(app.getProps().getProperty("ooffice.exe"),
		new FileOutputStream(file), outExt);
	try {
		int i=0;
		for (Object model : models) {
			System.out.println("Formatting report " + i++ + " of " + models.size());
			InputStream in = ReportOutput.openTemplateFile(app, templateName);
			jout.writeReport(in, ext, model);
			in.close();
		}
	} finally {
		jout.close();
	}

	if (jout.getNumReports() == 0) {
		javax.swing.JOptionPane.showMessageDialog(null,
			"The report has no pages.");
		return null;
	} else {
		citibob.gui.BareBonesPdf.view(file);
		return file;
	}
//	citibob.gui.BareBonesPdf.view(file);
//	return file;
}

public static File viewJodReport(App app, String templateName,
HashMap jodModel) throws Exception
{
	int dot = templateName.lastIndexOf('.');
	String ext = templateName.substring(dot+1);
	String outBase = templateName.substring(0, dot);
	String outExt = "pdf";
	File file = File.createTempFile(outBase, "." + outExt);
	file.deleteOnExit();
	
	JodPdfWriter jout = new JodPdfWriter(app.getProps().getProperty("ooffice.exe"),
		new FileOutputStream(file), outExt);
	try {
			InputStream in = ReportOutput.openTemplateFile(app, templateName);
			jout.writeReport(in, ext, jodModel);
			in.close();
	} finally {
		jout.close();
	}

	citibob.gui.BareBonesPdf.view(file);
//	Runtime.getRuntime().exec("acroread " + file.getPath());
	return file;
}
// =========================================================================




}
