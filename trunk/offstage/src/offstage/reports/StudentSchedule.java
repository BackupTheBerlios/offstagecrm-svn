/*
 * StudentSchedule.java
 *
 * Created on August 9, 2007, 12:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

//import com.artofsolving.jodconverter.*;
//import com.artofsolving.jodconverter.openoffice.connection.*;
//import com.artofsolving.jodconverter.openoffice.converter.*;
import net.sf.jooreports.templates.*;
import java.io.*;
import java.util.*;
import com.pdfhacks.*;
import citibob.sql.pgsql.*;
import java.sql.*;
import offstage.*;
import citibob.sql.*;
import citibob.swing.table.*;
import citibob.text.*;

/**
 *
 * @author citibob
 */
public class StudentSchedule
{
	
public static String getSql(int termid, int studentid)
{
	return
		" select adult.lastname as alastname, adult.firstname as afirstname,\n" +
		" p.lastname, p.firstname, pr.name as programname,\n" +
		" c.dayofweek, dow.shortname as dayofweek_shortname,\n" +
		" to_char(c.tstart,'HH:MI') as tstart,\n" +
		" to_char(c.tnext,'HH:MI') as tnext,\n" +
		" loc.name as locname,\n" +
		" t.firstdate, t.nextdate-1 as lastdate,\n" +
		" t.firstdate as firstyear, t.nextdate-1 as lastyear\n" +		
		" from termids t\n" +
		" inner join courseids c on (c.termid = t.groupid)\n" +
		" inner join enrollments en on (en.courseid = c.courseid)\n" +
		" inner join persons p on (p.entityid = en.entityid)\n" +
		" inner join termregs tr on (t.groupid = tr.groupid and p.entityid = tr.entityid)" +
		" inner join entities_school ps on (p.entityid = ps.entityid)\n" +
		" inner join entities adult on (ps.adultid = adult.entityid)\n" +
		" inner join daysofweek dow on (dow.javaid = c.dayofweek)\n" +
		" inner join locations loc on (loc.locationid = c.locationid)\n" +
		" left outer join programids pr  on (pr.programid = tr.programid)\n" +
		" where t.groupid=" + SqlInteger.sql(termid) + "\n" +
		(studentid < 0 ? "" : " and p.entityid = " + SqlInteger.sql(studentid)) +
		" order by adult.lastname, adult.firstname, p.lastname, p.firstname, \n" +
		" c.dayofweek, c.tstart\n";

}

//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	Statement st = pool.checkout().createStatement();
//	FrontApp fapp = new FrontApp(pool,null);
//	
//	RSTableModel rsmod = new RSTableModel(fapp.getSqlTypeSet());
//		rsmod.executeQuery(st, getSql(8, 12633));
//		
//	String[] gcols = new String[] {"lastname", "firstname", "programname", "firstdate", "lastdate", "firstyear", "lastyear"};
//	TableModelGrouper group = new TableModelGrouper(rsmod, gcols);
//	String[] sformattercols = new String[] {"firstdate", "firstyear", "lastyear"};
//	SFormatter[] sformatters = {
//		new JDateSFormatter("EEEEE, MMMMM d"),
//		new JDateSFormatter("yyyy"),
//		new JDateSFormatter("yyyy")
//	};
//	
//	JodPdfWriter jout = new JodPdfWriter("ooffice", new FileOutputStream("x.pdf"));
//	JTypeTableModel jtmod;
//	try {
//		while ((jtmod = group.next()) != null) {
//			StringTableModel smod = new StringTableModel(jtmod, fapp.getSFormatterMap());
//			for (int i=0; i<sformattercols.length; ++i) smod.setSFormatter(sformattercols[i], sformatters[i]);
//			TemplateTableModel ttmod = new TemplateTableModel(smod);
//			HashMap data = new HashMap();
//				data.put("rs", ttmod);
//			for (int i=0; i<gcols.length; ++i) {
//				data.put("g0_" + gcols[i], smod.getValueAt(0, smod.findColumn(gcols[i])));
//			}
//			jout.writeReport(ReportOutput.openTemplateFile(fapp, "StudentSchedule.odt"), data);
//		}
//	} finally {
//		jout.close();
//	}
//	
//	Runtime.getRuntime().exec("acroread x.pdf");
//}
////public static void doTest(String oofficeExe) throws Exception
//public static void main(String[] args) throws Exception
//{
//	
//	OutputStream pdfOut = new FileOutputStream(new File(dir, "test1-out.pdf"));
//	JodPdfWriter jout = new JodPdfWriter(oofficeExe, pdfOut);
//	try {
//		jout.writeReport(new FileInputStream(new File(dir, "test1.odt")), data);
//		jout.writeReport(new FileInputStream(new File(dir, "test1.odt")), data);
//	} finally {
//		jout.close();
//	}
//}

}
