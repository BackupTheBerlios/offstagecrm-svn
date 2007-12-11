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
 * JMBT.java
 *
 * Created on July 5, 2005, 7:38 PM
 */

package offstage.school.gui;

import com.sun.org.apache.bcel.internal.generic.DDIV;
import java.sql.*;
import citibob.sql.*;
import java.util.*;
import citibob.sql.pgsql.*;
import java.util.prefs.*;
import offstage.config.*;
import citibob.gui.DBPrefsDialog;

/**
 * A bunch of "stored procedures" for the JMBT database.  This is because
 * PostgreSQL stored procedures are nearly useless.
 * @author citibob
 */
public class TuitionCalc {

static SqlNumeric money = new SqlNumeric(9, 2, true);
static SqlTime time = new SqlTime(true);
static Double getMoney(ResultSet rs, String col) throws SQLException
	{ return (Double)money.get(rs, col); }
// ==========================================================
String termName;

/** When different charges are due throughout the term */
static class DueDate
{
	String name;
	String description;
	java.util.Date duedate;
	public DueDate(ResultSet rs, SqlDate date) throws SQLException
	{
		name = rs.getString("name");
		description = rs.getString("description");
		duedate = date.get(rs, "duedate");
	}
}
Map<String,DueDate> duedates;

/** Info on the payer */
static class Payer
{
	int entityid;
	boolean isorg;
	char billingtype;
	List<Student> students;
	public Payer(ResultSet rs) throws SQLException
	{
		entityid = rs.getInt("entityid");
		String sBillingType = rs.getString("billingtype");
		billingtype = (sBillingType == null ? 'y' : sBillingType.charAt(0));
		isorg = rs.getBoolean("isorg");
		students = new ArrayList(1);
	}
}
Map<Integer,Payer> payers;

/** A record of data for one student -- from termregs */
static class Student implements Comparable<Student>
{
	int entityid;
	int payerid;
	String lastname, firstname;
	double scholarship;
	Double tuitionoverride;
	List<Enrollment> enrollments;	// Courses we're enrolled in

	// Calculated Stuff
	double defaultTuition = 0;
	double tuition = 0;				// Tuition we calculated
	String tuitionDesc;				// Discription of our tuition for account
	
	public Student(ResultSet rs) throws SQLException
	{
		entityid = rs.getInt("entityid");
		payerid = rs.getInt("payerid");
			if (payerid == 0) payerid = entityid;
		lastname = rs.getString("lastname");
		firstname = rs.getString("firstname");
		scholarship = getMoney(rs, "scholarship");
		tuition = getMoney(rs, "tuition");
		tuitionoverride = getMoney(rs, "tuitionoverride");
		enrollments = new ArrayList(1);
	}
	public double getPrice()
	{
		double price = 0;
		for (Enrollment e : enrollments) price += e.getPrice();
		return price;
	}
	public double getSec()
	{
		double sec = 0;
		for (Enrollment e : enrollments) sec += e.getSec();
		return sec;
	}
	public String getName() { return firstname + " " + lastname; }

	public int compareTo(Student o) {
		double d = o.tuition - tuition;		// Sort descending
		if (d > 0) return 1;
		if (d < 0) return -1;
		return 0;
	}
}
Map<Integer,Student> students;

/** One student enrolled in one course */
static class Enrollment
{
	// From enrollments
	int entityid;			// student
	int courseid;
	java.util.Date dstart;	// Custom start date for pro-rating
	java.util.Date dend;

	// From courseids...
	int tstartMS;		// Time of day for start and end of course
	int tnextMS;
	Double price;		// Price of just this one course
	int locationid;		// Where the course is held
	
	public Enrollment(ResultSet rs, SqlDate date) throws SQLException
	{
		entityid = rs.getInt("entityid");
		courseid = rs.getInt("courseid");
		dstart = date.get(rs, "dstart");
		dend = date.get(rs, "dend");
		tstartMS = (int)time.get(rs, "tstart").getTime();
		tnextMS = (int)time.get(rs, "tnext").getTime();
		price = money.get(rs, "price");
		locationid = rs.getInt("locationid");
	}
	
	public double getPrice()
	{
		if (price == null) return 0;
		return price.doubleValue();
	}
	public double getSec()
	{
		if (price == null) return (tnextMS - tstartMS) / 1000;
		return 0;
	}
}

/** A line in the account */
private static class TuitionRec implements Comparable<TuitionRec>
{
	Student student;
	double defaultTuition;		// Calculated tuition
	double tuition;			// Actual tuition (could be from override)
	String description;		// How we describe this in account
	public int compareTo(TuitionRec o) {
		double d = o.tuition - tuition;		// Sort descending
		if (d > 0) return 1;
		if (d < 0) return -1;
		return 0;
	}
}

// ==========================================================================
App app;
SqlRunner str;
int termid;
String payerIdSql;

/** @param payerIdSql IdSql that selects the payers for which we want to recalc tuition. */
public TuitionCalc(App app, SqlRunner str, int termid, String payerIdSql)
{
	this.app = app;
	this.str = str;
	this.termid = termid;
	this.payerIdSql = payerIdSql;
}

void readTuitionData()
throws SQLException
{
	String sql =
		// rss[0]: Name of Term
		" select t.name as termname" +
		" from termids t" +
		" where t.groupid = " + SqlInteger.sql(termid) + ";\n" +
		
		// rss[1]: DueDate
		" select name,duedate from termduedates" +
		" where termid = " + SqlInteger.sql(termid) + ";\n" +

		// Make temporary tables for below
		" create temporary table _payers (entityid int);\n" +
		" insert into _payers " + payerIdSql + ";\n" +
		
		" create temporary table _students (entityid int);\n" +
		" insert into _students\n" +
		" select distinct entityid\n" +
		" from entities_school es, _payers\n" +
		" where es.adultid = _payers.entityid;\n" +
		
		// Delete previous tuition records in account
		" delete from tuitiontrans using _payers" +
		" where tuitiontrans.entityid = _payers.entityid\n" +
		" and tuitiontrans.termid = " + SqlInteger.sql(termid) + ";\n" +

		// Zero out previous tuitions
		" update termregs from _students" +
		" set termregs.tuition = 0" +
		" where termregs.entityid = _students.entityid;\n" +
		
		// rss[2]: Payers
		" select e.isorg,\n" +
		" (case when es.billingtype is null then 'y' else es.billingtype) as billingtype\n" +
		" from entities e left outer join entities_school es, _payers" +
		" where _payers.entityid = e.entityid;\n" +

		// rss[3]: Students
		" select _students.entityid, es.adultid as payerid, e.lastname, e.firstname," +
		" tr.scholarship, tr.tuition, tr.tuitionoverride" +
		" from _students," +
		"     entities e left outer join entities_school es on (e.entityid = es.entityid)," +
		"     termregs tr" +
		" where tr.termid = " + SqlInteger.sql(termid) + ";\n" +
		" and _students.entityid = tr.entityid\n" +
		" and _students.entityid = e.entityid;\n" +

		// rss[4]: Enrollments
		" select _students.entityid, c.*\n" +
		" from _students, enrollments en, courseids c, courseroles cr" +
		" where _students.entityid = en.entityid" +
		" and en.termid = c.termid and en.courseid = c.courseid" +
		" and c.termid = " + SqlInteger.sql(termid) +
		" and en.courserole = cr.courseroleid and cr.name = 'student'\n" +
		" order by st.entityid, c.dayofweek, c.tstart;\n" + 
		
		// Drop temporary tables
		" drop table _payers;" +
		" drop table _students;";
	str.execSql(sql, new RssRunnable() {
	public void run(citibob.sql.SqlRunner str, java.sql.ResultSet[] rss) throws Exception {
		SqlDate date = new SqlDate(app.getTimeZone(), true);
		ResultSet rs;
		
		// rss[0]: Name of term
		rs = rss[0];
		rs.next();
		termName = rs.getString("name");
		
		// rss[1]: DueDate
		rs = rss[1];
		duedates = new TreeMap();
		while (rs.next()) {
			DueDate dd = new DueDate(rs, date);
			duedates.put(dd.name, dd);
		}
		
		// rss[2]: Payers
		rs = rss[2];
		payers = new TreeMap();
		while (rs.next()) {
			Payer pp = new Payer(rs);
			payers.put(pp.entityid, pp);
		}
		
		// rss[3]: Students
		rs = rss[3];
		students = new TreeMap();
		while (rs.next()) {
			Student ss = new Student(rs);
			Payer pp = payers.get(ss.payerid);
			if (pp != null) pp.students.add(ss);
			students.put(ss.entityid, ss);
		}
		
		// rss[4]: Enrollments
		rs = rss[4];
		while (rs.next()) {
			Enrollment ee = new Enrollment(rs, date);
			Student ss = students.get(ee.entityid);
			if (ss != null) ss.enrollments.add(ee);
		}
	}});
}

/** @return SQL to update tuition records */
String calcTuitionSql(StringBuffer sql)
{
//	StringBuffer sql = new StringBuffer();
	
	// Go through family by family
	for (Payer payer : payers.values()) {
		calcTuition(payer);
	}
	
	// Produce the SQL to store this tuition calculation
	for (Payer pp : payers.values()) {
		for (Student ss : pp.students) {
			// Main tuition in student record
			sql.append(
				" update termregs" +
				" set defaulttuition=" + money.sql(ss.defaultTuition) + "," +
				" tuition=" + money.sql(ss.tuition) +
				" where termid = " + SqlInteger.sql(termid) +
				" and entityid = " + SqlInteger.sql(ss.entityid) +
				";\n");

			// Don't mess with accounts if there's no tuition to be charged
			if (ss.tuition == 0) continue;
			
			// Registration fee
			DueDate reg = duedates.get("r");
			if (reg != null) {
				insertTransaction(sql, pp.entityid, reg.duedate,
					getRegistrationFee(),
					"Registration Fee for " + ss.getName(),
					ss.entityid);
			}

			// Main fees
			switch(pp.billingtype) {
				case 'q' : {
					for (int i=1; i<=4; ++i) {
						String ssegment = " --- Quarter " + i;
						java.util.Date duedate = duedates.get("q" + i).duedate;
						
						// Main tuition
						insertTransaction(sql, pp.entityid, duedate,
							ss.tuition * .25,
							ss.tuitionDesc + ssegment,
							ss.entityid);
						
						// Scholarships
						if (ss.scholarship > 0) {
							insertTransaction(sql, pp.entityid, duedate,
								-ss.scholarship * .25,
								termName + ": Scholarship for " + ss.getName() + ssegment,
								ss.entityid);
						}
					}
				} break;
				case 'y' : {
					String ssegment = " --- Full Year";
					java.util.Date duedate = duedates.get("y").duedate;
					
					// Main tuition
					insertTransaction(sql, pp.entityid, duedate,
						ss.tuition,
						ss.tuitionDesc + " --- Full Year",
						ss.entityid);
					
					// Scholarships
					if (ss.scholarship > 0) {
						insertTransaction(sql, pp.entityid, duedate,
							-ss.scholarship,
							termName + ": Scholarship for " + ss.getName() + ssegment,
							ss.entityid);
					}
				} break;
			}
		}
	}
}

void insertTransaction(StringBuffer sql, int entityid,
java.util.Date duedate, double amount, String description, int studentid)		
{
	sql.append(
		" insert into tuitiontrans " +
		" (entityid, actypeid, date, amount, description, studentid, termid)" +
		" values (" + SqlInteger.sql(entityid) + ", " +
		" (select actypeid from actypes where name = 'school'), " +
		date.toSql(duedate) + ", " + money.toSql(amount) + "," +
		SqlString.sql(description) + ", " + SqlInteger.sql(studentid) + ", " +
		SqlInteger.sql(termid) + ");\n");
	
}		
double getRegistrationFee() { return 25; }
	
/** Changes the student records inside payer.students */
void calcTuition(Payer payer)
{
	List<TuitionRec> trs = new ArrayList(payer.students.size());
	int n = 0;
	for (Student ss : payer.students) {
		// Get the tuition...
		ss.defaultTuition = calcTuition(ss);
		if (ss.tuitionoverride != null) {
			// Manual override of tuition --- just set it
			ss.tuition = ss.tuitionoverride;
		} else {
			// No discount, first (or only) child
			ss.tuition = ss.defaultTuition;
		}
		ss.tuitionDesc = termName + ": Tuition for " + ss.getName();
		++n;
	}

	if (payer.isorg) return;	// No sibling discounts for organizational payers
	Collections.sort(payer.students);
	for (Student ss : payer.students) {
		if (n++ == 0) continue;		// Don't apply to first child
		if (ss.tuitionoverride != null) continue;		// Don't apply if we've manually set tuition
		
		// Apply discount, we're on a sibling
		ss.defaultTuition *= .9;
		ss.tuition = ss.defaultTuition;
		ss.tuitionDesc += " (w/ sibling discount)";
	}

}

/** Returns the (one) tuition number for a particular student. */
double calcTuition(Student ss)
{	
	int sec = ss.getSec();			// Price goes by time
	double price = ss.getPrice();		// Non-timed items
	
	double tuition = getPrice(sec) + price;
	
	// Apply pro-rating here
}

double getPrice(int sec)
{
	return TuitionRate.getRateY(weeklyS);
}





public static void w_tuitiontrans_recalcAllTuitions(SqlRunner str, final int termid)
throws SQLException
{
	ResultSet rs;

	if (termid < 0) return;
	
	String sql = "select distinct adultid from entities_school union select entityid from entities_school";
	str.execSql(sql, new RsRunnable() {
	public void run(SqlRunner str, ResultSet rs) throws Exception {
		final List<Integer> payers = new ArrayList();
		while (rs.next()) payers.add(rs.getInt(1));
		
		for (Integer payerid : payers) {
			System.out.println("Recalculating tuition for " + payerid);
			w_tuitiontrans_calcTuitionByAdult(str, termid, payerid, null);
		}
	}});
}

static SqlMoney nullableMoney = new SqlMoney(true);
public static void w_tuitiontrans_calcTuitionByAdult(SqlRunner str,
final int termid, final int adultid, final UpdRunnable rr)
throws SQLException
{
	String sql =
		// rss[0]
		" select name,duedate from termduedates" +
		" where termid = " + SqlInteger.sql(termid) + ";\n" +

		// rss[1]
		" select t.name as termname" +
		" from termids t" +
		" where t.groupid = " + SqlInteger.sql(termid) + ";\n" +

		// rss[2] --- NOP... make sure next ResultSet has data
		"select w_student_create(" + SqlInteger.sql(adultid) + ");\n" +

		// rss[3]
		" select es.billingtype, e.isorg\n" +
		" from entities_school es, entities e\n" +
		" where es.entityid = " + SqlInteger.sql(adultid) + "\n" +
		" and es.entityid = e.entityid;" +

		// Speed up rss[4+5] queries below
		" create temporary table _ids (entityid int);\n" +
		" insert into _ids select entityid from entities_school" +
		"    where adultid = " + SqlInteger.sql(adultid) + ";\n" +
		// Use _ids to clear tuition, in case no enrollments down below
		" update termregs set tuition = 0" +
		" from _ids st where termregs.entityid = st.entityid" +
		" and termregs.groupid = " + SqlInteger.sql(termid) + ";\n" +
		
		// rss[4]
		" select st.entityid as studentid, tr.tuitionoverride\n" +
		" from _ids st, termregs tr\n" +
		" where st.entityid = tr.entityid\n" +
		" and tr.groupid = " + SqlInteger.sql(termid) + "\n" +
		" and tr.tuitionoverride is not null;\n" +

		// rss[5]
		" select st.entityid as studentid, p.lastname, p.firstname, tr.scholarship, tr.tuitionoverride, c.*\n" +
		" from _ids st, entities p, courseids c, enrollments e, termregs tr\n" +
		" where st.entityid = p.entityid\n" +
		" and tr.entityid = p.entityid and tr.groupid = c.termid" +
		" and c.termid = " + SqlInteger.sql(termid) + "\n" +
		" and e.courseid = c.courseid and e.entityid = st.entityid" +
		" and e.courserole = (select courseroleid from courseroles where name = 'student')" +
		" order by st.entityid, dayofweek, c.tstart;" +
		
		" drop table _ids;";

	str.execSql(sql, new RssRunnable() {
	public void run(SqlRunner str, ResultSet[] rss) throws Exception {
System.out.println("Processing results, adultid = " + adultid);
		ResultSet rs;
		HashMap<String,String> duedates = new HashMap();
		final StringBuffer sqlOut = new StringBuffer();	// SQL to store results
		SqlNumeric money = new SqlNumeric(9, 2);
		SqlTime time = new SqlTime(true);
		
		// rss[0]
		// Read term due dates: rss[0]
		rs = rss[0];
		while (rs.next()) duedates.put(rs.getString("name"), rs.getString("duedate"));
		rs.close();
		
		// Get name of the term: rss[1]
		rs = rss[1];
		rs.next();
		String termName = rs.getString("termname");
	
		// Get billing type for this payer
		rs = rss[3];
		rs.next();
		String sBillingType = rs.getString("billingtype");
		char btype = (sBillingType == null ? 'y' : sBillingType.charAt(0));
		boolean isorg = rs.getBoolean("isorg");
		rs.close();

		// Remove previous tuition invoice records
		sqlOut.append(
			" delete from tuitiontrans where termid = " + SqlInteger.sql(termid) +
			" and entityid = " + SqlInteger.sql(adultid) + ";\n");
	
		// Get tuition overrides, even for people who have dropped all courses
		Map<Integer,Double> tuitionOverrides = new TreeMap();
		rs = rss[5];
		while (rs.next()) {
			tuitionOverrides.put(rs.getInt("studentid"),
				(Double)nullableMoney.get(rs, "tuitionoverride"));
		}
		
		// Calculate sum of hours in enrolled courses, per student
		rs = rss[5];
		TuitionRec tr = null;
		double scholarship = 0;
		int nsiblings = 0;				// Total number of siblings
		int weeklyS = 0;
		ArrayList<TuitionRec> tuitions = new ArrayList();
		for (;;) {
			boolean hasNext = rs.next();
			if (!hasNext || tr == null || (tr.studentid != rs.getInt("studentid"))) {
				if (tr != null) {
					tr.tuition += TuitionRate.getRateY(weeklyS);
					tuitions.add(tr);
					++nsiblings;

					// Apply the scholarship
					if (scholarship > 0) {
						TuitionRec trs = new TuitionRec();
						trs.studentid = tr.studentid;
						trs.studentName = tr.studentName;
						trs.description = termName + ": Scholarship for " + tr.studentName;
						tuitions.add(trs);
						trs.tuition = -scholarship;
					}
				}

				// Make up description for this next student record.
				if (hasNext) {
					tr = new TuitionRec();
					tr.studentid = rs.getInt("studentid");
					tr.studentName = rs.getString("firstname") + " " + rs.getString("lastname");
					tr.description = termName + ": Tuition for " + tr.studentName;
					tr.tuition = 0;
					tr.tuitionOverride = (Double)nullableMoney.get(rs, "tuitionoverride");
					tuitionOverrides.remove(tr.studentid);
					scholarship = rs.getDouble("scholarship");
					weeklyS = 0;
				}
			}
			if (!hasNext) break;
	System.out.println(rs.getString("studentid") + " " + rs.getString("name"));
			// Add the price of this course to the tuition
			String Price = rs.getString("price");
			if (Price != null) {
				tr.tuition += Double.parseDouble(Price);
			} else {
				long tstart = time.get(rs, "tstart").getTime();
				long tnext = time.get(rs, "tnext").getTime();
				int lengthS = (int)(tnext - tstart) / 1000;
				weeklyS += lengthS;
			}
		}
		rs.close();

		// Give sibling discounts
		if (nsiblings > 1 && (!isorg)) {
			Collections.sort(tuitions);		// Sorted by tuition; scholarships are at end
			Iterator<TuitionRec> ii = tuitions.iterator();
			ii.next();
			while (ii.hasNext()) {
				TuitionRec trx = ii.next();
				if (trx.tuition < 0) break;		// Scholarships
				trx.tuition *= .9;
				trx.description += " (w/ sibling discount)";
			}
		}

	
		for (TuitionRec trx : tuitions) {
			if (trx.tuition < 0) continue;		// Scholarships
			// Store in termregs table
			sqlOut.append("update termregs" +
				" set tuition = " + trx.tuition +
				" where entityid = " + trx.studentid +
				" and groupid = " + termid + ";\n");
		}
	
		// ====================================================
		// Make up final list of bills to account
		ArrayList<TuitionRec> t2 = new ArrayList();
	
		// Add registration fees
		for (TuitionRec trx : tuitions) {
			if (trx.tuition < 0) continue;		// Ignore scholarhips
			TuitionRec tq = new TuitionRec();
			tq.sdate = duedates.get("r");
			tq.description = "Registration Fee for " + trx.studentName;
			tq.studentid = trx.studentid;
			tq.tuition = 25;
			t2.add(tq);
		}
	
		// Convert to actual billing records that are quarterly or yearly...
		if (btype == 'q') {
			for (TuitionRec trx : tuitions) {
				for (int i=1; i<=4; ++i) {
					TuitionRec tq = new TuitionRec();
					tq.sdate = duedates.get("q" + i);
					tq.description = trx.description + " --- Quarter " + i;
					tq.studentid = trx.studentid;
					tq.tuition = trx.tuition * .25;
					if (trx.tuitionOverride != null) tq.tuitionOverride = new Double(trx.tuitionOverride.doubleValue() * .25);
					t2.add(tq);
				}
			}
		} else {	// Bill Yearly
			for (TuitionRec trx : tuitions) {
				trx.sdate = duedates.get("y");
				trx.description += " --- Full Year";
				t2.add(trx);		
			}
		}
	
		// Write them out
		for (TuitionRec trx : t2) {
			// This student's records have ended; write out
			sqlOut.append(" insert into tuitiontrans" +
				" (entityid, actypeid, date, amount, description, studentid, termid)" +
				" values (" + SqlInteger.sql(adultid) + ", " +
				" (select actypeid from actypes where name = 'school'), " +
				(trx.sdate == null ? "null" : "'" + trx.sdate + "'") + ", " +
				money.sql(trx.tuitionOverride != null ? trx.tuitionOverride : trx.tuition) + ", " +
				SqlString.sql(trx.description) + ", " +
				SqlInteger.sql(trx.studentid) + ", " + SqlInteger.sql(termid) + ");\n");
		}
	
System.out.println(sqlOut);
		str.execSql(sqlOut.toString());
		str.execUpdate(rr);
	}});
}
// -------------------------------------------------------------------------------
///** Returns true only if ALL IDs are in the entities_school table. */
//public static boolean isInSchool(SqlRunner str, int entityid) throws SQLException
//{
//	ResultSet rs = null;
//	try {
//		rs = st.executeQuery(
//			"select entityid from entities_school where entityid = " +
//			SqlInteger.sql(entityid));
//		return rs.next();
//	} finally {
//		rs.close();
//	}
//}
//public static boolean isInSchool(SqlRunner str, int[] entityids) throws SQLException
//{
//	ResultSet rs = null;
//	return SQL.readInt(st,
//		"select count(*) from entities_school where entityid in " +
//		SQL.intList(entityids)) == entityids.length;
//}
// -------------------------------------------------------------------------------
//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	SqlRunner str = pool.checkout().createStatement();
//	w_tuitiontrans_calcTuition(st, 8, 12633);
//
//}
}
