/*
 * StudentSchedule.java
 *
 * Created on August 9, 2007, 12:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import java.io.*;
import java.sql.*;
import citibob.sql.*;
import citibob.reports.*;
import offstage.FrontApp;
import offstage.equery.EQuery;

/**
 *
 * @author citibob
 */
public class MailMerge
{

public static void viewReport(SqlRunner str, final FrontApp fapp,
EQuery equery, final File templateFile)
throws Exception
{
	
	String idSql = equery.getSql(fapp.getEquerySchema(), true);
	String sql = LabelReport.getSql(idSql);
	
	str.execSql(sql, new RsRunnable() {
	public void run(SqlRunner str, ResultSet rs) throws Exception {
		Reports rr = fapp.getReports();
//		Map map = new HashMap();
//		rr.viewJodPdfs(rr.toJodList(rs, null), templateFile.getParentFile(), templateFile.getName());
		rr.viewJodPdfs(rr.toJodList(rs,
//			new String[][] {{"entityid"}}),
			new String[][] {{"line1", "line2", "line3", "city", "state", "zip", "firstname"}}),
			templateFile.getParentFile(), templateFile.getName());
	}});
}

}
