/*
 * StudentSchedule.java
 *
 * Created on August 9, 2007, 12:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import com.artofsolving.jodconverter.*;
import com.artofsolving.jodconverter.openoffice.connection.*;
import com.artofsolving.jodconverter.openoffice.converter.*;
import net.sf.jooreports.templates.*;
import java.io.*;
import java.util.*;
import com.pdfhacks.*;
import citibob.sql.pgsql.*;

/**
 *
 * @author citibob
 */
public class StudentSchedule
{
	
public static String getSql(int termid)
{
	return
		" select adult.lastname as alastname, adult.firstname as afirstname,\n" +
		" p.lastname, p.firstname, pr.name as programname,\n" +
		" c.dayofweek, dow.shortname,\n" +
		" to_char(c.tstart,'HH:MI') as tstart,\n" +
		" to_char(c.tnext,'HH:MI') as tnext,\n" +
		" loc.name as locname\n" +
		" from termids t\n" +
		" inner join courseids c on (c.termid = t.termid)\n" +
		" inner join enrollments en on (en.courseid = c.courseid)\n" +
		" inner join persons p on (p.entityid = en.entityid)\n" +
		" inner join entities_school ps on (p.entityid = ps.entityid)\n" +
		" inner join entities adult on (ps.adultid = adult.entityid)\n" +
		" inner join daysofweek dow on (dow.javaid = c.dayofweek)\n" +
		" inner join locations loc on (loc.locationid = c.locationid)\n" +
		" left outer join programids pr  on (pr.programid = ps.programid)\n" +
		" where t.termid=" + SqlInteger.sql(termid) + "\n" +
		" order by adult.lastname, adult.firstname, p.lastname, p.firstname, \n" +
		" c.dayofweek, c.tstart\n";

}
	
//public static void doTest(String oofficeExe) throws Exception
public static void main(String[] args) throws Exception
{
	
	OutputStream pdfOut = new FileOutputStream(new File(dir, "test1-out.pdf"));
	JodPdfWriter jout = new JodPdfWriter(oofficeExe, pdfOut);
	try {
		jout.writeReport(new FileInputStream(new File(dir, "test1.odt")), data);
		jout.writeReport(new FileInputStream(new File(dir, "test1.odt")), data);
	} finally {
		jout.close();
	}
}

}
