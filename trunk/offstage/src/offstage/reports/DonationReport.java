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
import offstage.*;
import citibob.app.*;
import java.sql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import citibob.multithread.*;
import static citibob.jschema.JoinedSchemaBufDbModel.TableSpec;
import static citibob.sql.RSTableModel.Col;
import java.util.*;
import citibob.swing.typed.*;
import citibob.sql.*;
import offstage.db.*;
import citibob.sql.pgsql.*;

/**
 
 *
 * @author citibob
 */
public class DonationReport extends MultiTableDbModel
{

//SqlDbModel main;
String idSql;		// Set of IDs for our report

public void doSelect(Statement st)
throws java.sql.SQLException
{
	DB.createIDList(st, idSql, "ids_donor");
	super.doSelect(st);
	st.executeUpdate("drop table ids_donor");
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
		app.getSqlTypeSet(), "entityid", sql);
	this.add(new SqlDbModel(main));

	// Outer Join the Fiscal Year summaries
	final int[] years = new int[] {1989, 1990, 1991, 2000, 2001, 2002, 2003};
	Col[] cols = new Col[years.length];
//	for (int i=0; i<years.length; ++i) cols[i] = new Col(""+years[i], new JavaJType(Double.class));
	for (int i=0; i<years.length; ++i) cols[i] = new Col(""+years[i], new SqlNumeric(10,2,true));
	sql =
		" select d.entityid, di.fiscalyear, sum(amount) as amount" +
		" from donations d, donationids di, ids_donor ids" +
		" where d.entityid = ids.id" +
		" and d.groupid = di.groupid" +
		" and di.fiscalyear in (1989, 1990, 1991, 2000, 2001, 2002, 2003)" +
		" group by d.entityid, di.fiscalyear";

	SqlDbModel model = new SqlDbModel(new AdhocOJSqlTableModel(
		main, "entityid", cols, app.getSqlTypeSet(),
		sql) {
			public void setRow(int row, ResultSet rs) throws SQLException
			{
				int year = rs.getInt("fiscalyear");
				//int col = findColumn(""+year);
				int col = Arrays.binarySearch(years, year); //findColumn(""+year);
				setValueAt(rs.getDouble("amount"), row, col);
			}
		});
	this.add(model);
//	setTableModel();
}
}
