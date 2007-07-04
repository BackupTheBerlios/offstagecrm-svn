package offstage.accounts.gui;
/*
 * NewRecordWizard.java
 *
 * Created on October 8, 2006, 11:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import citibob.sql.pgsql.SqlInteger;
import citibob.swing.html.*;
import citibob.wizard.*;
import javax.swing.*;
import java.sql.*;
import offstage.db.*;
import offstage.wizards.*;
import offstage.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.jschema.log.*;

/**
 *
 * @author citibob
 */
public class TransactionWizard extends OffstageWizard {

	Statement st;		// Datbase connection
	int entityid, actypeid;
	/*
addState(new State("", "", "") {
	public HtmlWiz newWiz()
		{ return new }
	public void process()
	{
		
	}
});
*/

public TransactionWizard(offstage.FrontApp xfapp, Statement xst, java.awt.Frame xframe,
int xentityid, int xactypeid)
{
	super("Transactions", xfapp, xframe, "cashpayment");
	this.st = xst;
	this.entityid = xentityid;
	this.actypeid = xactypeid;
// ---------------------------------------------
//addState(new State("init", "init", "init") {
//	public HtmlWiz newWiz() throws Exception
//		{ return new InitWiz(frame); }
//	public void process() throws Exception
//	{
//		String s = v.getString("type");
//		if (s != null) state = s;
//	}
//});
//// ---------------------------------------------
//addState(new State("person", "init", null) {
addState(new State("cashpayment", null, null) {
	public HtmlWiz newWiz() throws Exception
		{ return new CashpaymentWiz(frame, fapp); }
	public void process() throws Exception
	{
		Schema schema = fapp.getSchema("actrans");
		boolean hasDtime = (v.get("dtime") != null);
		String sql =
			" insert into cashpayments (entityid, actypeid," +
			(hasDtime ? "dtime, " : "") +
			"amount, description) values (" +
			SqlInteger.sql(entityid) + ", " + SqlInteger.sql(actypeid) + ", " +
			(hasDtime ? vsql("dtime", schema) + ", " : "") +
			vsql(new Integer(-((Number)v.get("amount")).intValue()), "amount", schema) + ", " +
			vsql("description", schema) + ")";
		st.executeUpdate(sql);
	}
});
}
// ---------------------------------------------

public static void main(String[] args) throws Exception
{
	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
	Statement st = pool.checkout().createStatement();
	FrontApp fapp = new FrontApp(pool,null);
	Wizard wizard = new TransactionWizard(fapp, st, null, 12633, 1);
	wizard.runWizard();
}

}
