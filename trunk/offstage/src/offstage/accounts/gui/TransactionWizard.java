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

import citibob.app.App;
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
import offstage.crypt.*;
import offstage.swing.typed.*;

/**
 *
 * @author citibob
 */
public class TransactionWizard extends OffstageWizard {

	Statement st;		// Datbase connection
	int entityid, actypeid;
	SqlDate sqlDate;
/*
addState(new State("", "", "") {
	public HtmlWiz newWiz()
		{ return new }
	public void process()
	{
		
	}
});
*/

/** Does an insert, using all the field names in v automatically. */
void vInsert(String table, TypedHashMap v) throws SQLException
{
	ConsSqlQuery sql = newInsertQuery(table, v);
	sql.addColumn("entityid", SqlInteger.sql(entityid));
	sql.addColumn("actypeid", SqlInteger.sql(actypeid));
//	sql.addColumn("date", sqlDate.toSql(new java.util.Date()));		// Store day that it is in home timezone
	st.executeUpdate(sql.getSql());
}

public TransactionWizard(offstage.FrontApp xfapp, Statement xst, java.awt.Frame xframe,
int xentityid, int xactypeid)
{
	super("Transactions", xfapp, xframe, "ccpayment");
	this.st = xst;
	this.entityid = xentityid;
	this.actypeid = xactypeid;
	sqlDate = new SqlDate(fapp.getTimeZone(), false);
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
// ---------------------------------------------
addState(new State("cashpayment", null, null) {
	public HtmlWiz newWiz() throws Exception
		{ return new CashpaymentWiz(frame, fapp); }
	public void process() throws Exception
	{
		double namount = ((Number)v.get("namount")).doubleValue();
		v.put("amount", new Double(-namount));
		vInsert("cashpayments", v);
	}
});
addState(new State("adjpayment", null, null) {
	public HtmlWiz newWiz() throws Exception
		{ return new AdjpaymentWiz(frame, fapp); }
	public void process() throws Exception
	{
		double namount = ((Number)v.get("namount")).doubleValue();
		v.put("amount", new Double(-namount));
		vInsert("adjpayments", v);
	}
});
addState(new State("checkpayment", null, null) {
	public HtmlWiz newWiz() throws Exception
		{ return new CheckpaymentWiz(frame, fapp); }
	public void process() throws Exception
	{
		double namount = ((Number)v.get("namount")).doubleValue();
		v.put("amount", new Double(-namount));
		vInsert("checkpayments", v);
	}
});
addState(new State("ccpayment", null, null) {
	public HtmlWiz newWiz() throws Exception
		{ return new CcpaymentWiz(frame, st, entityid, fapp); }
	public void process() throws Exception
	{
		CcpaymentWiz cwiz = (CcpaymentWiz)wiz;
//		cwiz.getWidget("ccchooser")
		CCChooser cc = (CCChooser)cwiz.getWidget("ccchooser");
		// TODO: Log change to credit card # on file (or should I?)
		cc.saveNewCardIfNeeded(st);
		
		ConsSqlQuery sql = new ConsSqlQuery("ccpayments", ConsSqlQuery.INSERT);
		cc.getCard(sql);
		sql.addColumn("amount", SqlDouble.sql(-((Number)v.get("namount")).doubleValue()));
		sql.addColumn("description", SqlString.sql((String)v.get("description")));
		sql.addColumn("entityid", SqlInteger.sql(entityid));
		sql.addColumn("actypeid", SqlInteger.sql(actypeid));
		sql.addColumn("date", sqlDate.toSql(new java.util.Date()));		// Store day that it is in home timezone
		st.executeUpdate(sql.getSql());
	}
});
// ---------------------------------------------
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
