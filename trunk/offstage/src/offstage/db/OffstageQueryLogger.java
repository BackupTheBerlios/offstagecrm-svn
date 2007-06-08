/*
 * OffstageQueryLogger.java
 *
 * Created on June 8, 2007, 1:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.db;

import static citibob.sql.ConsSqlQuery.*;
import static citibob.jschema.log.QueryLogRec.*;
import java.sql.*;
import citibob.sql.*;
import java.util.*;
import citibob.jschema.*;
import citibob.jschema.log.*;
import citibob.multithread.*;
import citibob.sql.pgsql.*;

/**
 * Logs queries into Offstage's querylog and querylogcol tables.  This is pretty
 general, could be put in JSchema with a bit of thought.
 * @author citibob
 */
public class OffstageQueryLogger implements QueryLogger
{

int loginID;
ActionRunner runner;

/** Creates a new instance of OffstageQueryLogger */
public OffstageQueryLogger(ActionRunner runner, int loginID)
{
	this.runner = runner;
	this.loginID = loginID;
}

public void log(final QueryLogRec rec)
{
	runner.doRun(new StRunnable() {
	public void run(java.sql.Statement st) throws Throwable
	{
		char ctype = 'X';
		switch(rec.type) {
			case UPDATE : ctype = 'U'; break;
			case INSERT : ctype = 'I'; break;
			case DELETE : ctype = 'D'; break;
		}
		int id = DB.r_nextval(st, "querylog_queryid_seq");
		StringBuffer sql = new StringBuffer();
		sql.append("insert into querylog (loginid, queryid, type, dbtable, dtime)" +
			" values (" + SqlInteger.sql(loginID) + ", " + SqlInteger.sql(id) + ", " + SqlChar.sql(ctype) +
			", " + SqlString.sql(rec.table) + ", now());\n");
		for (ColUpdate nv : rec.keyCols) {
			sql.append("insert into querylogcols (queryid, iskey, colname, oldval, sqlval) values (" +
				SqlInteger.sql(id) + ", true, " +
				SqlString.sql(nv.name) + ", " + SqlString.sql(nv.oldval) + ", " + SqlString.sql(nv.value) + ");\n");
		}
		for (ColUpdate nv : rec.valCols) {
			sql.append("insert into querylogcols (queryid, iskey, colname, oldval, sqlval) values (" +
				SqlInteger.sql(id) + ", false, " +
				SqlString.sql(nv.name) + ", " + SqlString.sql(nv.oldval) + ", " + SqlString.sql(nv.value) + ");\n");
		}
System.out.println(sql.toString());
		st.execute(sql.toString());
	}});
}

}
