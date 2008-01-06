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
 * SchoolAccounts.java
 *
 * Created on November 25, 2007, 4:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import citibob.sql.*;
import java.sql.*;
import java.util.*;
import citibob.sql.pgsql.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import javax.swing.table.*;

/**
 * To be written out to XLS, so we don't need to include JType information
 * @author citibob
 */
public class SchoolAccounts
{
	
// =================================================================
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
// =================================================================
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
	
	/** @param termid current term */
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
// =================================================================
TimeZone tz;
SqlDate sqlDate;		// Used for reading date from database
int lateDays;					// Apply late fee after # days late
java.util.Date asOfDate;		// Apply late fee as of this date.
//java.util.Date transCutoff;		// Don't accept payments after this date.  null: accept all payments

/** Report result */
public Map<String,Object> model;
DefaultTableModel table;
int unpaidLateCol;
int entityidCol;
// =================================================================
/** @param tz TimeZone used to read from database.
 @param xasOfDate Calculate late fees as of this date.  Don't list transactions after this date.  Any hours,min or sec are cleared.
 @param xtransCutoff Don't list transactions after this date.  If null, list all transactions.
 @param lateDays Something is late if it was billed before xlateAsOfDate - lateDays and is not yet paid.
 */
public SchoolAccounts(SqlRunner str, TimeZone tz, final int termid,
java.util.Date xasOfDate, int lateDays)
//int xlateDays, java.util.Date xlateAsOf)
{
	this.lateDays = lateDays;
	this.tz = tz;

	// Calculate when a bill becomes late
	Calendar cal = Calendar.getInstance(tz);
		cal.setTime(xasOfDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	this.asOfDate = cal.getTime();
	cal.add(Calendar.DAY_OF_MONTH, -lateDays);
	final java.util.Date lateCutoff = cal.getTime();
	
	sqlDate = new SqlDate(tz, false);
	
	String sql =
		// rss[0]
		// Non-tuition transactions
		" select ac.tableoid,klass.relname,ac.entityid, e.lastname, e.firstname," +
		" ac.date,ac.amount,ac.description,ac.actransid,-1 as termid" +
		" from actrans ac, pg_class klass, actypes, entities e" +
		" where klass.oid = ac.tableoid" +
		" and e.entityid = ac.entityid and not e.obsolete" +
		" and klass.relname <> 'tuitiontrans'" +
		" and ac.actypeid = actypes.actypeid and actypes.name = 'school'\n" +
		(asOfDate == null ? "" : " and ac.date <= " + sqlDate.toSql(asOfDate) + "\n") +
		"   UNION" +
		// Tuition transactions
		" select 0,'tuitiontrans',ac.entityid,e.lastname, e.firstname, ac.date," +
		" ac.amount,ac.description,ac.actransid,ac.termid" +
		" from tuitiontrans ac, entities e" +
		" where e.entityid = ac.entityid and not e.obsolete" +
		(asOfDate == null ? "" : " and ac.date <= " + sqlDate.toSql(asOfDate) + "\n") +
		" order by lastname,firstname,entityid,date,amount desc;\n" +
		
		// rss[1]
		" select name from termids where groupid=" + termid + ";\n";
	str.execSql(sql, new RssRunnable() {
	public void run(SqlRunner str, ResultSet[] rss) throws Exception {
		model = new TreeMap();
		table = new DefaultTableModel(
			new String[] {"entityid", "lastname", "firstname", "totalbilled_term",
				"(regfees_term)","(paid+adj)_term","scholarships_term","unpaid_term",
				"unpaid_all","unpaid_late","overpay"},
//			new JType[] {integer, string, string, money, money, money, money, money, money, money},
			0);
		unpaidLateCol = table.findColumn("unpaid_late");
		entityidCol = table.findColumn("entityid");
		model.put("rs", table);

		List<Acct> accts = new ArrayList();
		int lastEntityid = -1;
		Acct acct = null;
		ResultSet rs = rss[0];
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
		
//		System.out.println("entityid,lastname,firstname,totalbilled_term,(regfees_term)," +
//			"(paid+adj)_term,scholarships_term,unpaid_term,unpaid_all,overpay");
		for (Acct ac : accts) {
			double unpaid_term = 0;
			double unpaid_all = 0;
			double unpaid_late = 0;
			for (Bill b : ac.unpaid) {
				if (b.termid == termid) unpaid_term += b.amountUnpaid;
				unpaid_all += b.amountUnpaid;
				if (b.dt.getTime() < lateCutoff.getTime()) unpaid_late += b.amountUnpaid;
			}
			table.addRow(new Object[] {
				ac.entityid, ac.lastname, ac.firstname,
				ac.totalbilled_term,
				ac.regfees_term,
				(ac.totalbilled_term - unpaid_term - ac.rebates_term),
				//(ac.rebates_term - ac.scholarship_term) + ", " +		// adj_term
				ac.scholarship_term,
				unpaid_term, unpaid_all, unpaid_late, ac.overpay});
		}
		
		// Add miscellaneous stuff
		if (rss[1].next()) {
			model.put("sterm", rss[1].getString("name"));
			model.put("date", new java.util.Date());
			model.put("lateasofdate", new java.util.Date());
			model.put("regfee", new Double(25));
		} else {
			// No extra model things needed, we're just doing
			// this for internal late fees, not a report.
		}
	}});
}

public void applyLateFees(SqlRunner str, double multiplier)
{
	DateFormat dfmt = new SimpleDateFormat("MMM dd, yyyy");
	NumberFormat nfmt = NumberFormat.getCurrencyInstance();
	StringBuffer sql = new StringBuffer();
	int nrow = table.getRowCount();
	for (int i=0; i<nrow; ++i) {
		int entityid = (Integer)table.getValueAt(i, entityidCol);
		double unpaidLate = (Double)table.getValueAt(i, unpaidLateCol);
		if (unpaidLate < 1.0D) continue;
		double lateFee = unpaidLate * multiplier;
		String description = "Late fee on " + lateDays + "-day overdue balance of " + nfmt.format(unpaidLate);
		sql.append(
			" insert into actrans (entityid, actypeid, date, amount, description, datecreated) values (" +
			entityid + ",(select actypeid from actypes where name = 'school')," +
			sqlDate.toSql(asOfDate) + "," + lateFee + "," +
			SqlString.sql(description) + ",now());\n");
	}
	str.execSql(sql.toString());
}




//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	offstage.FrontApp fapp = new offstage.FrontApp(pool,null);
//
//	SqlBatch str = new SqlBatch();
//	int termid = 346;
//	SchoolAccounts sa = new SchoolAccounts(fapp);
//	sa.findUnpaid(str, termid);
//	str.exec(pool);
//
//	Map<String,Object> map = new TreeMap();
//	map.put("rs", sa);
//	fapp.getReports().writeXls(map, "StudentAccounts.xls", new java.io.File("x2.xls"));
//}

}

