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
 * StudentSchedule.java
 *
 * Created on August 9, 2007, 12:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import citibob.app.App;
import citibob.sql.pgsql.*;
import java.sql.*;
import citibob.sql.*;
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


public static void viewStudentSchedules(final App app, SqlRunner str, int termid, int entityid)
throws Exception
{
	String sql = offstage.reports.StudentSchedule.getSql(termid, entityid);
	str.execSql(sql, new RsRunnable() {
	public void run(SqlRunner str, ResultSet rs) throws Exception {
		citibob.reports.Reports reports = app.getReports();
		
		java.util.List models = reports.toJodList(rs,
			new String[][] {{"lastname", "firstname", "programname", "firstdate", "lastdate", "firstyear", "lastyear", "afirstname", "alastname"}},
			new String[] {"firstdate", "firstyear", "lastyear"},
			new SFormat[] {
				new DateSFormat("EEEEE, MMMMM d", "", app.getTimeZone()),
				new DateSFormat("yyyy", "", app.getTimeZone()),
				new DateSFormat("yyyy", "", app.getTimeZone())
		});
		reports.viewJodPdfs(models, null, "StudentSchedule.odt");
	}});
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
//	SFormat[] sformatters = {
//		new JDateSFormat("EEEEE, MMMMM d"),
//		new JDateSFormat("yyyy"),
//		new JDateSFormat("yyyy")
//	};
//	
//	JodPdfWriter jout = new JodPdfWriter("ooffice", new FileOutputStream("x.pdf"));
//	JTypeTableModel jtmod;
//	try {
//		while ((jtmod = group.next()) != null) {
//			StringTableModel smod = new StringTableModel(jtmod, fapp.getSFormatMap());
//			for (int i=0; i<sformattercols.length; ++i) smod.setSFormat(sformattercols[i], sformatters[i]);
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
