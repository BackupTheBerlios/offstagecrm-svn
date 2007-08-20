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
public class SchoolDB {

// -------------------------------------------------------------------------------
/** Makes a student record for an entity if it doesn't already exist. */
public static void w_student_create(Statement st, int studentid)
{
	try {
		String sql =
			"insert into entities_school (entityid, adultid) values (" +
			SqlInteger.sql(studentid) +
			", (select primaryentityid from entities" +
			"     where entityid = " + SqlInteger.sql(studentid) + "))";
		st.executeUpdate(sql);
	} catch(SQLException e) {
//		System.out.println("hoi");
	}	// ignore if already in DB
}
/** Makes a student record for an entity if it doesn't already exist. */
public static void w_student_register(Statement st, int termid, int studentid, SqlDate sqlDate)
{
	try {
		String sql =
			"insert into termregs (groupid, entityid, dtregistered) values (" +
			SqlInteger.sql(termid) + ", " +
			SqlInteger.sql(studentid) + ", " +
			sqlDate.toSql(new java.util.Date()) + ")";	// Register NOW
		st.executeUpdate(sql);
	} catch(SQLException e) {
	}	// ignore if already in DB
}
public static void w_payer_create(Statement st, int payerid)
{
	try {
		String sql =
			"insert into entities_school (entityid, adultid) values (" +
			SqlInteger.sql(payerid) + ", " + SqlInteger.sql(payerid) + ")";
		st.executeUpdate(sql);
	} catch(SQLException e) {
//		System.out.println("hoi");
	}	// ignore if already in DB
}

///** Recalculate tuition for adultid associated with this student. */
//public static void w_tuitiontrans_calcTuition(Statement st, int termid, int studentid)
//throws SQLException
//{
//	ResultSet rs;
//
//	// Find responsible paying party for this student
//	int adultid = SQL.readInt(st,
//		" select adultid from entities_school" +
//		" where entityid = " + SqlInteger.sql(studentid));
//	
//	// Find old responsible paying party
//	int oldadultid = -1;
//	try {
//		oldadultid = SQL.readInt(st,
//			" select distinct entityid from tuitiontrans where termid = " + SqlInteger.sql(termid) +
//			" and studentid = " + SqlInteger.sql(studentid));
////		w_tuitiontrans_calcTuitionByAdult(st, termid, oldadultid);
//	} catch(SQLException e) {}	// There is no old adultid; it's OK.
//
//	// Recalculate tuition on both accounts
//	if (oldadultid >= 0 && oldadultid != adultid) w_tuitiontrans_calcTuitionByAdult(st, termid, oldadultid);
//	w_tuitiontrans_calcTuitionByAdult(st, termid, adultid);
//
////	// Remove previous tuition invoice records, just for this student
////	st.executeUpdate(
////		" delete from tuitiontrans where termid = " + SqlInteger.sql(termid) +
////		" and studentid = " + SqlInteger.sql(studentid));
//	
//	
//}

private static class TuitionRec implements Comparable<TuitionRec>
{
	public int studentid;
	public String studentName;
	public String sdate;
	public String description;
	public double scholarship;
	public double tuition;
	public int compareTo(TuitionRec o) {
		double d = o.tuition - tuition;		// Sort descending
		if (d > 0) return 1;
		if (d < 0) return -1;
		return 0;
	}
}

public static void w_tuitiontrans_calcTuitionByAdult(Statement st, int termid, int adultid)
throws SQLException
{
	StringBuffer sqlOut = new StringBuffer();
	ResultSet rs;
	String sql;
	
	SqlNumeric money = new SqlNumeric(9, 2);
	SqlTime time = new SqlTime(true);
	
	// Read term due dates
	HashMap<String,String> duedates = new HashMap();
	rs = st.executeQuery(
		" select name,duedate from termduedates" +
		" where termid = " + SqlInteger.sql(termid));
	while (rs.next()) duedates.put(rs.getString("name"), rs.getString("duedate"));
	rs.close();
	
	// Get name of the term
	rs = st.executeQuery(
		" select t.name as termname" +
		" from termids t" +
//		" left outer join entities_school es on (es.termid = t.termid and es.entityid = " + SqlInteger.sql(adultid) + ")" +
//		" select name, billdtime, paymentdue from termids" +
		" where t.groupid = " + SqlInteger.sql(termid));
	rs.next();
	String termName = rs.getString("termname");
	rs.close();
	
	// Get billing type for this payer
	rs = st.executeQuery(
		" select billingtype" +
		" from entities_school es where entityid = " + SqlInteger.sql(adultid));
	rs.next();
	String sBillingType = rs.getString("billingtype");
	char btype = (sBillingType == null ? 'y' : sBillingType.charAt(0));
	rs.close();
	
	
//	String sBillDtime = rs.getString("billdtime");
//	String sPaymentdue = rs.getString("paymentdue");
	rs.close();

	// Get the quarterly and yearly duedates
	
	// Remove previous tuition invoice records
	sqlOut.append(
		" delete from tuitiontrans where termid = " + SqlInteger.sql(termid) +
		" and entityid = " + SqlInteger.sql(adultid) + ";\n");
//		" and studentid in" +
//		" (select studentid from entities_school" +
//		"  where adultid = " + SqlInteger.sql(adultid) + ");\n");
	
	// Recalculate all tuition records for that paying party
	sql =
		" select st.entityid as studentid, p.lastname, p.firstname, tr.scholarship, c.*\n" +
		" from entities_school st, entities p, courseids c, enrollments e, termregs tr\n" +
		" where st.entityid = p.entityid\n" +
		" and tr.entityid = p.entityid and tr.groupid = c.termid" +
		" and st.adultid = " + SqlInteger.sql(adultid) + "\n" +
		" and c.termid = " + SqlInteger.sql(termid) + "\n" +
		" and e.courseid = c.courseid and e.entityid = st.entityid" +
		" and e.courserole = (select courseroleid from courseroles where name = 'student')" +
		" order by st.entityid, dayofweek, c.tstart";
	rs = st.executeQuery(sql);
	
	// Calculate sum of hours in enrolled courses, per student
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
	if (nsiblings > 1) {
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
		st.executeUpdate("update termregs" +
			" set tuition = " + trx.tuition +
			" where entityid = " + trx.studentid +
			" and groupid = " + termid);
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
	
	// Covert to actual billing records that are quarterly or yearly...
	if (btype == 'q') {
		for (TuitionRec trx : tuitions) {
			for (int i=1; i<=4; ++i) {
				TuitionRec tq = new TuitionRec();
				tq.sdate = duedates.get("q" + i);
				tq.description = trx.description + " --- Quarter " + i;
				tq.studentid = trx.studentid;
				tq.tuition = trx.tuition * .25;
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
			"'" + trx.sdate + "', " +
			money.sql(trx.tuition) + ", " + SqlString.sql(trx.description) + ", " +
			SqlInteger.sql(trx.studentid) + ", " + SqlInteger.sql(termid) + ");\n");
	}
	
System.out.println(sqlOut);
	st.executeUpdate(sqlOut.toString());
	
}
// -------------------------------------------------------------------------------
/** Returns true only if ALL IDs are in the entities_school table. */
public static boolean isInSchool(Statement st, int entityid) throws SQLException
{
	ResultSet rs = null;
	try {
		rs = st.executeQuery(
			"select entityid from entities_school where entityid = " +
			SqlInteger.sql(entityid));
		return rs.next();
	} finally {
		rs.close();
	}
}
//public static boolean isInSchool(Statement st, int[] entityids) throws SQLException
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
//	Statement st = pool.checkout().createStatement();
//	w_tuitiontrans_calcTuition(st, 8, 12633);
//
//}
}
