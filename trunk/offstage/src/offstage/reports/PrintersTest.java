package offstage.reports;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;


/** 
 See discussion at:
 http://www.jasperforge.org/index.php?option=com_joomlaboard&Itemid=215&func=view&id=20901&catid=8
 
 JasperReports doesn't print with Linux (CUPS).  This is due to a known bug
 in Sun's Java print system that makes it not work with CUPS:

http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4900425

Consider the program at the bottom of this post (which is code taken out of
 JPPrintServiceExporter.java). When run on Linux (Fedora Core 6), it prints
 false twice, which means that it will never find the (properly installed) printers.
 When run on Mac OS X Tiger (which also uses CUPS), it prints false the first time,
 but true the second.

Workarounds:
1. On Mac OS X, run the code snipped down below once at the start of your
 application. This should work, but I haven't tried it yet.
2. For Linux, modify JPPrintServiceExporter.checkAvailablePrinters() to
 always return true on Linux. This will required using a modified version
 of JasperReports, of course.
*/

public class PrintersTest
{
	// artf1936
	public static boolean checkAvailablePrinters() 
	{
		PrintService[] ss = java.awt.print.PrinterJob.lookupPrintServices();
		for (int i=0;i<ss.length;i++) {
			Attribute[] att = ss[i].getAttributes().toArray();
			for (int j=0;j<att.length;j++) {
				if (att[j].equals(PrinterIsAcceptingJobs.ACCEPTING_JOBS)) {
					return true;
				}
			}
		}
		return false;
	}
	public static void main(String[] args)
	{
		System.out.println(checkAvailablePrinters());
		System.out.println(checkAvailablePrinters());
	}
}
