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

/**
 *
 * @author citibob
 */
public class ReportOutput
{

public static void viewJasperReport(String reportName, JRDataSource jrdata, Map params)
throws JRException
{	
	InputStream in = Object.class.getResourceAsStream("/offstage/reports/" + reportName);
	viewJasperReport(in, jrdata, params);
}
public static void viewJasperReport(InputStream reportIn, JRDataSource jrdata, Map params)
throws JRException
{	
	// Convert ccinfo to full table representation
	params = (params == null ? new HashMap() : params);
	params.put("doggie", "Doggone WOrld");
//	InputStream in = Object.class.getResourceAsStream("/offstage/reports/CCPayments.jasper");
	JasperPrint jprint = net.sf.jasperreports.engine.JasperFillManager.fillReport(reportIn, params, jrdata);
	offstage.reports.PrintersTest.checkAvailablePrinters();		// Java/CUPS/JasperReports bug workaround for Mac OS X
	net.sf.jasperreports.view.JasperViewer.viewReport(jprint, false);
}
	
public static String saveCSVReport(JTypeTableModel report, String title, App app, Component parent) throws Exception
{
	String dir = app.userRoot().get("saveReportDir", null);
	JFileChooser chooser = new JFileChooser(dir);
	chooser.setDialogTitle("Save " + title);
	chooser.addChoosableFileFilter(
		new javax.swing.filechooser.FileFilter() {
		public boolean accept(File file) {
			String filename = file.getName();
			return filename.endsWith(".csv");
		}
		public String getDescription() {
			return "*.csv";
		}
	});
	String path = null;
	String fname = null;
	for (;;) {
		chooser.showSaveDialog(parent);

		path = chooser.getCurrentDirectory().getAbsolutePath();
		if (chooser.getSelectedFile() == null) return null;
		fname = chooser.getSelectedFile().getPath();
		if (!fname.endsWith(".csv")) fname = fname + ".csv";
		File f = new File(fname);
		if (!f.exists()) break;
		if (JOptionPane.showConfirmDialog(
			parent, "The file " + f.getName() + " already exists.\nWould you like to ovewrite it?",
			"Overwrite File?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) break;
	}
	app.userRoot().put("saveReportDir", path);

	CSVReportOutput csv = new CSVReportOutput(report, null, null, app.getSFormatterMap());
	csv.writeReport(new File(fname));
	return fname;
}
// =========================================================================




}
