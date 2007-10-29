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
import citibob.reports.*;

/**
 *
 * @author citibob
 */
public class YDPConfirmationLetter
{

public static void doReport(SqlRunner str, final citibob.app.App app, int termid)
throws Exception
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
	
	str.execSql(sql, new RsRunnable() {
	public void run(SqlRunner str, ResultSet rs) throws Exception {
		Reports rr = app.getReports();
		rr.viewJodPdfs(rr.toJodList(rs,
			new String[][] {{"line1", "line2", "line3", "city", "state", "zip", "firstname"}}),
			"YDPConfirmationLetter.odt");
	}});
}

}
