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
import java.sql.*;
import offstage.*;
import citibob.sql.*;
import citibob.swing.table.*;
import citibob.text.*;

/**
 *
 * @author citibob
 */
public class YDPConfirmationLetter
{
	
public static String getSql(int termid)
{
	String idSql =
		" select xx.entityid\n" +
		" from (\n" +
		" 	select distinct s.parentid as entityid\n" +
		" 	from termregs tr, entities_school s\n" +
		" 	where tr.groupid = " + termid + "\n" +
		" 	and tr.entityid = s.entityid\n" +
		" ) xx, persons p\n" +
		" where xx.entityid = p.entityid\n" +
		" order by p.lastname, p.firstname";
	String sql = LabelReport.getSql(idSql, false);
	return sql;
}

public static void doReport(citibob.app.App app, Statement st, int termid)
throws Exception
{
	RSTableModel rsmod = new RSTableModel(app.getSqlTypeSet());
		rsmod.executeQuery(st, getSql(termid));

	String[] gcols = new String[] {"line1", "line2", "line3", "city", "state", "zip", "firstname"};
	TableModelGrouper group = new TableModelGrouper(rsmod, gcols);
//	String[] sformattercols = new String[] {"firstdate", "firstyear", "lastyear"};
//	SFormatter[] sformatters = {
//		new JDateSFormatter("EEEEE, MMMMM d"),
//		new JDateSFormatter("yyyy"),
//		new JDateSFormatter("yyyy")
//	};
//	ReportOutput.saveJodReport(fapp, SchoolPanel.this,
//		"Save Student Schedules",
//		group, sformattercols, sformatters);
	ReportOutput.viewJodReport(app, "YDPConfirmationLetter.odt",
		group, null, null);//sformattercols, sformatters);
}

}
