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
	File f = new File(app.getConfigDir().getPath() + File.separatorChar + "reports" + name);
	if (f.exists()) return new FileInputStream(f);

	// File doesn't exist; read from inside JAR file instead.
//	Class klass = offstage.config.OffstageVersion.class;
//	String resourceName = klass.getPackage().getName().replace('.', '/') + "/" + name;
//	return klass.getClassLoader().getResourceAsStream(resourceName);
	String resourceName = "offstage/reports/" + name;
	return ReportOutput.class.getClassLoader().getResourceAsStream(resourceName);
}

public static void viewJasperReport(App app, String templateName, JRDataSource jrdata, Map params)
throws JRException, IOException
{	
	InputStream reportIn = openTemplateFile(app, templateName);
	params = (params == null ? new HashMap() : params);
	JasperPrint jprint = net.sf.jasperreports.engine.JasperFillManager.fillReport(reportIn, params, jrdata);
	offstage.reports.PrintersTest.checkAvailablePrinters();		// Java/CUPS/JasperReports bug workaround for Mac OS X
	net.sf.jasperreports.view.JasperViewer.viewReport(jprint, false);
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

	CSVReportOutput csv = new CSVReportOutput(report, null, null, app.getSFormatterMap());
	csv.writeReport(file);
	return file;
}

public static File saveJodReport(App app, Component parent,
String title, TableModelGrouper group,
String[] sformattercols, SFormatter[] sformatters) throws Exception
{
	File file = chooseReportOutput(app, parent,
		"pdfreport", ".pdf", title);
	
	int[] gcols = group.getGcols();
	String[] sgcols = group.getGcolNames();
	JodPdfWriter jout = new JodPdfWriter(app.getProps().getProperty("ooffice.exe"), new FileOutputStream(file));
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
			jout.writeReport(ReportOutput.openTemplateFile(app, "StudentSchedule.odt"), data);
		}
	} finally {
		jout.close();
	}

	citibob.gui.BareBonesPdf.view(file);
//	Runtime.getRuntime().exec("acroread " + file.getPath());
	return file;
}
// =========================================================================




}
