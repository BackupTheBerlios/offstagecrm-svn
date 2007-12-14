/*
 * AccountsDB.java
 *
 * Created on December 14, 2007, 12:22 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.accounts.gui;

import citibob.sql.pgsql.SqlInteger;

/**
 *
 * @author citibob
 */
public class AccountsDB
{
	
public static String w_tmp_acct_balance_sql(String idSql, final int actypeid)
{
	if (idSql == null) idSql =
		" select distinct entityid as id from actrans where actypeid = "+ SqlInteger.sql(actypeid) +
		" UNION" +
		" select distinct entityid as id from acbal where actypeid = "+ SqlInteger.sql(actypeid);
	String sql =
		" create temporary table _bal (entityid int, date date, bal numeric(9,2) default 0);\n" +
		" insert into _bal (entityid) " + idSql + ";\n" +
		
		" update _bal" +
		" set bal = acbal.bal" +
		" from acbal" +
		" where _bal.entityid = acbal.entityid\n" +
		" and actypeid = " + SqlInteger.sql(actypeid) + ";\n" +

		" update _bal" +
		" set bal=bal+xx.amount\n" +
		" from (\n" +
		"     select actrans.entityid, sum(actrans.amount) as amount" +
		"     from actrans, _bal" +
		"     where actypeid = " + SqlInteger.sql(actypeid) +
		"     and _bal.entityid = actrans.entityid" +
		"     and (_bal.date is null or actrans.date >= _bal.date)" +
		"     group by actrans.entityid" +
		" \n) xx" +
		" where xx.entityid = _bal.entityid;\n";
		
//		" drop table _bal0";
	
	return sql;
}	
}
