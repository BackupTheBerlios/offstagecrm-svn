/*
 * OffstageReports.java
 *
 * Created on October 27, 2007, 8:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import citibob.app.*;
import java.io.*;
import java.awt.Component;
import javax.swing.*;

/**
 *
 * @author citibob
 */
public class OffstageReports extends citibob.reports.Reports
{

public OffstageReports(App app)
{
	this.app = app;
	oofficeExe = app.getProps().getProperty("ooffice.exe");
}

public InputStream openTemplateFile(File dir, String name) throws IOException
{
	if (dir != null) return super.openTemplateFile(dir, name);

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
	return OffstageReports.class.getClassLoader().getResourceAsStream(resourceName);
}

// =============================================================

}
