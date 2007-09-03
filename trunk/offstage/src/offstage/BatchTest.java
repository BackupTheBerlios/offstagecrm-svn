/*
 * BatchTest.java
 *
 * Created on August 21, 2007, 1:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage;

import java.io.*;
import java.util.*;
import citibob.sql.pgsql.*;
import java.sql.*;
import citibob.sql.*;
import citibob.text.*;
import citibob.app.*;
import java.text.*;

/**
 *
 * @author citibob
 */
public class BatchTest {

public static void processResults(ResultSet[] rss) throws SQLException
{
	System.out.println("=============================");
	for (int i=0; i<rss.length; ++i) {
		ResultSet rs = rss[i];
		// Do your thing
		System.out.println("Reult Set: " + i);
		while (rs.next()) {
			System.out.println("    " + rs.getObject(1));
		}
		rs.close();
	}
}

public static void main(String[] args) throws Exception
{
	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
	Statement st = pool.checkout().createStatement();
	FrontApp fapp = new FrontApp(pool,null);

	if (true) {
		String sql =
			" select lastname from entities where lastname = 'Fischer';\n" +
			" update entities set lastname='Fischer' where entityid=12633;\n" +
			" select name from courseids;\n" +
			" select 'hello' as __divider__;\n" +
			" select 'hello' as __divider__;\n" +
			" update entities set lastname='Fischer' where entityid=12633;\n";
		st.execute(sql);

		int nextCurrent = Statement.CLOSE_ALL_RESULTS;
		List<ResultSet> xrss = new ArrayList();
		ResultSet rs;
		for (int n=0;; ++n,st.getMoreResults(nextCurrent)) {
			nextCurrent = Statement.KEEP_CURRENT_RESULT;
			rs = st.getResultSet();
			if (rs == null) {
				// Not an update or a select --- we're done with all result sets
				if (st.getUpdateCount() == -1) break;
				// It was an update --- go on to next result set
				continue;
			}

			// See if this is a divider
			ResultSetMetaData meta = rs.getMetaData();
			if (meta.getColumnCount() > 0 && "__divider__".equals(meta.getColumnName(1))) {
				rs.close();

				// It was --- process all buffered result sets
				System.out.println("DIVIDER");
				nextCurrent = Statement.CLOSE_ALL_RESULTS;
				ResultSet[] rss = new ResultSet[xrss.size()];
				xrss.toArray(rss);
				processResults(rss);
				xrss.clear();
			} else {
				// Just a regular ResultSet --- buffer it
				xrss.add(rs);
			}

		}
	}

	if (false) {
		st.clearBatch();
		st.addBatch(" select lastname from entities where lastname = 'Fischer';\n");
		st.addBatch(" select lastname from entities where lastname = 'Fischer';\n");
		st.executeBatch();
	}


}

    protected void finalize() throws Exception {
    }
}
