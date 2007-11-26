/*
 * SchoolAccounts.java
 *
 * Created on November 25, 2007, 4:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import citibob.sql.*;
import citibob.util.*;
import java.sql.*;
import java.util.*;
import citibob.app.*;
import citibob.types.*;
import citibob.sql.pgsql.*;

/**
 *
 * @author citibob
 */
public class SchoolAccounts
{
	
static class Bill
{
	int termid;
	java.util.Date dt;
	String description;
	double amount;
	double amountUnpaid;
	public Bill(int termid, java.util.Date dt, String description, double amount)
	{
		this.termid = termid;
		this.dt = dt;
		this.description = description;
		this.amount = amount;
		this.amountUnpaid = amount;
	}
}
static class Acct
{
	int entityid;
	String lastname, firstname;
	double overpay;		// Anything overpaid at this point
	double totalbilled_term;	// Total for the "chosen term"
	double regfees_term;	// Total of just registration fees for the "chosen term"
	double rebates_term;	// Rebates over the term
	double scholarship_term;	// Scholarships over the term
	List<Bill> unpaid = new LinkedList();	// Unpaid bills
	
	public Acct(int entityid, String lastname, String firstname) {
		this.entityid = entityid;
		this.lastname = lastname;
		this.firstname = firstname;
	}
	
	public void addBill(Bill b, int termid)
	{
		if (b.termid == termid) {
			totalbilled_term += b.amount;
			if (b.description != null && b.description.contains("Registr")) {
				regfees_term += b.amount;
			}
		}
		unpaid.add(b);
	}
	
	/** Record a payment of the oldest bills on record.
	 @param amt amt must be positive (abs) */
	void payPayment(double amt)
	{
		overpay += amt;
		for (ListIterator<Bill> ii=unpaid.listIterator(); ii.hasNext();) {
			Bill b = ii.next();
			double x = Math.min(overpay, b.amountUnpaid);
			b.amountUnpaid -= x;
			overpay -= x;
			if (Math.abs(b.amountUnpaid) < 1e-7) {
				// Remove bill if fully paid up.
				ii.remove();
			}
			if (Math.abs(overpay) < 1e-7) {
				overpay = 0;
				return;		// We're done distributing payment
			}
		}
		// Still some payment left --- leave for later.
	}

	// TODO: Should not use same "overpay" as payPayment()
	void payRebate(double xamt, String description, boolean isChosenTerm)
	{
		double amt = xamt;
		overpay += amt;
		for (ListIterator<Bill> ii=unpaid.listIterator(unpaid.size()); ii.hasPrevious();) {
			Bill b = ii.previous();
			double x = Math.min(overpay, b.amountUnpaid);
			b.amountUnpaid -= x;
			overpay -= x;
			if (Math.abs(overpay) < 1e-7) {
				overpay = 0;
				break;		// We're done distributing payment
			}
		}
		// Still some payment left --- leave for later.
		
		
		// Count up rebates and scholarships
		if (isChosenTerm) {
			rebates_term += xamt;
			if (description != null && description.contains("Scholarship")) {
				scholarship_term += xamt;
			}
		}
	}

}


SqlDate sqlDate;
public SchoolAccounts(App app)
{
	sqlDate = new SqlDate(app.getTimeZone(), false);
}

public void findUnpaid(SqlRunner str, final int termid)
{
	String sql =
		" select ac.tableoid,klass.relname,ac.entityid, e.lastname, e.firstname, ac.date,ac.amount,ac.description,ac.actransid,-1 as termid" +
		" from actrans ac, pg_class klass, actypes, entities e" +
		" where klass.oid = ac.tableoid" +
		" and e.entityid = ac.entityid and not e.obsolete" +
		" and ac.tableoid not in (select oid from pg_class where relname in ('tuitiontrans'))" +
		" and ac.actypeid = actypes.actypeid and actypes.name = 'school'" +
		"   UNION" +
		" select 0,'tuitiontrans',ac.entityid,e.lastname, e.firstname, ac.date,ac.amount,ac.description,ac.actransid,ac.termid" +
		" from tuitiontrans ac, entities e" +
		" where e.entityid = ac.entityid and not e.obsolete" +
		" order by lastname,firstname,entityid,date,amount desc";
	str.execSql(sql, new RsRunnable() {
	public void run(SqlRunner str, ResultSet rs) throws Exception {
		List<Acct> accts = new ArrayList();
		int lastEntityid = -1;
		Acct acct = null;
		while (rs.next()) {
			// Change over entityid
			int entityid = rs.getInt("entityid");
			if (entityid != lastEntityid) {
				if (acct != null) {
					acct.payPayment(0);		// Use up any overpay that we can.
					accts.add(acct);
				}
				acct = new Acct(entityid, rs.getString("lastname"), rs.getString("firstname"));
				lastEntityid = entityid;
			}
			
			// Decide what kind of transaction this is.
			double amount = rs.getDouble("amount");
			if (amount > 0) {
				// Another bill --- must be paid off.
				Bill b = new Bill(rs.getInt("termid"), sqlDate.get(rs, "date"), rs.getString("description"), amount);
				acct.addBill(b, termid);
			} else {
				// It's a payment
				String relname = rs.getString("relname");
				if ("tuitiontrans".equals(relname) || "adjpayments".equals(relname)) {
					// Rebates pay off the newest charges.
					acct.payRebate(-amount, rs.getString("description"), rs.getInt("termid") == termid);
				} else {
					// Payments pay off the oldest charges.
					acct.payPayment(-amount);
				}
			}
		}
		
		System.out.println("entityid,lastname,firstname,totalbilled_term,(regfees_term)," +
			"(paid+adj)_term,scholarships_term,unpaid_term,unpaid_all,overpay");
		for (Acct ac : accts) {
			double unpaid_term = 0;
			double unpaid_all = 0;
			for (Bill b : ac.unpaid) {
				if (b.termid == termid) unpaid_term += b.amountUnpaid;
				unpaid_all += b.amountUnpaid;
			}
			System.out.println(ac.entityid + ", " + ac.lastname + ", " + ac.firstname + ", " +
				ac.totalbilled_term + ", " +
				ac.regfees_term + ", " +
				(ac.totalbilled_term - unpaid_term - ac.rebates_term) + ", " +
				//(ac.rebates_term - ac.scholarship_term) + ", " +		// adj_term
				ac.scholarship_term + ", " +
				unpaid_term + ", " + unpaid_all + ", " + ac.overpay);
		}
		
		
	}});
}

public static void main(String[] args) throws Exception
{
	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
	offstage.FrontApp fapp = new offstage.FrontApp(pool,null);

	SqlBatch str = new SqlBatch();
	int termid = 346;
	SchoolAccounts sa = new SchoolAccounts(fapp);
	sa.findUnpaid(str, termid);
	str.exec(pool);
}

}

