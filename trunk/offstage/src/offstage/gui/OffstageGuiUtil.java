/*
 * GuiUtil.java
 *
 * Created on July 8, 2007, 11:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.gui;

import citibob.swing.typed.*;
import citibob.swing.table.*;
import citibob.app.*;
import citibob.jschema.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;

/**
 *
 * @author citibob
 */
public class OffstageGuiUtil
{
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
	
}
