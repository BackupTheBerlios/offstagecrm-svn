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
 * DonorReport.java
 *
 * Created on February 10, 2007, 9:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import citibob.sql.AdhocOJSqlTableModel;
import citibob.app.*;
import java.sql.*;
import citibob.jschema.*;
import citibob.reports.Reports;
import static citibob.sql.RSTableModel.Col;
import java.util.*;
import citibob.sql.*;
import offstage.db.*;

/**
 
 *
 * @author citibob
 */
public class DonationReport extends MultiTableDbModel
{

//SqlDbModel main;
String idSql;		// Set of IDs for our report

public void doSelect(SqlRunner str)
{
	DB.createIDList(str, idSql, "ids_donor");
	super.doSelect(str);
	str.execSql("drop table ids_donor");
}
	
/** Creates a new instance of DonorReport */
public DonationReport(App app, String idSql)
{
	this.idSql = idSql;

	String sql;
	sql =
		" select p.* from persons p, ids_donor ids where p.entityid = ids.id";
//		int x=5;
	MainSqlTableModel main = new MainSqlTableModel(
		app.getSqlTypeSet(), sql);
	this.add(new SqlDbModel(main));

	// Outer Join the Fiscal Year summaries
	final int[] years = new int[] {1989, 1990, 1991, 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007};
	Col[] cols = new Col[years.length];
//	for (int i=0; i<years.length; ++i) cols[i] = new Col(""+years[i], new JavaJType(Double.class));
	for (int i=0; i<years.length; ++i) cols[i] = new Col(""+years[i], new SqlNumeric(10,2,true));
	sql =
		" select d.entityid, di.fiscalyear, sum(amount) as amount" +
		" from donations d, donationids di, ids_donor ids" +
		" where d.entityid = ids.id" +
		" and d.groupid = di.groupid" +
		" and di.fiscalyear in (1989, 1990, 1991, 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007)" +
		" group by d.entityid, di.fiscalyear";

	SqlDbModel model = new SqlDbModel(new AdhocOJSqlTableModel(
		main, "entityid", "entityid", cols, app.getSqlTypeSet(),
		sql) {
			public void setRow(int row, ResultSet rs) throws SQLException
			{
				int year = rs.getInt("fiscalyear");
				//int col = findColumn(""+year);
				int col = Arrays.binarySearch(years, year); //findColumn(""+year);
System.out.println("(" + row + ", " + col + ") = " + rs.getDouble("amount"));
System.out.println("year = " + year);
				setValueAt(rs.getDouble("amount"), row, col);
			}
		});
	this.add(model);
//	setTableModel();
}

public static void writeCSV(final App app, SqlRunner str, final java.awt.Frame frame, final String title, String sql) throws Exception
{
	final DonationReport report = new DonationReport(app, sql);
	report.doSelect(str);
	str.execUpdate(new UpdRunnable() {
	public void run(SqlRunner str) throws Exception {
		Reports rr = app.getReports();
		rr.writeCSV(rr.format(report.newTableModel()),
			frame, "Save" + title);
//		ReportOutput.saveCSVReport(fapp, frame, "Save" + title, report.newTableModel());	
	}});
}



}
