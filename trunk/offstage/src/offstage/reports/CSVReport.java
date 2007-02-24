/*
 * CSVReport.java
 *
 * Created on February 14, 2007, 11:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import java.sql.*;

/**
 *
 * @author citibob
 */
public class CSVReport {

    /** Creates a new instance of CSVReport */
    public CSVReport() {
    }

public static void main(String[] args) throws Exception
{
	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
	Statement st = pool.checkout().createStatement();

	String sql =
		" select * from mailings where groupid=295";
	ResultSet rs = st.executeQuery(sql);
	ResultSetMetaData meta = rs.getMetaData();
	int ncol = meta.getColumnCount();
	for (int i=0; i<ncol; ++i) {
		System.out.print(meta.getColumnLabel(i+1) + ",");
	}
	System.out.println("");
	while (rs.next()) {
		for (int i = 0; i < ncol; ++i) {
			Object o = rs.getObject(i+1);
			System.out.print("\"" + (o == null ? "" : o) + "\",");
		}
		System.out.println();
	}
}

}
