/*
Offstage CRM: Enterprise Database for Arts Organizations
This file Copyright (c) 2006 by Robert Fischer

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
public static void w_students_create(Statement st, int studentid)
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

/** Recalculate tuition for adultid associated with this student. */
public static void w_tuitiontrans_calcTuition(Statement st, int termid, int studentid)
throws SQLException
{
	ResultSet rs;
	
	// Find responsible paying party for this student
	int adultid = SQL.readInt(st,
		" select adultid from entities_school" +
		" where entityid = " + SqlInteger.sql(studentid));
	
	w_tuitiontrans_calcTuitionByAdult(st, termid, adultid);
}

public static void w_tuitiontrans_calcTuitionByAdult(Statement st, int termid, int adultid)
throws SQLException
{
	StringBuffer sqlOut = new StringBuffer();
	ResultSet rs;
	String sql;
	
	SqlNumeric money = new SqlNumeric(9, 2);
	SqlTime time = new SqlTime(true);
	
	// Get name of the term
	rs = st.executeQuery(
		" select name, paymentdue from termids" +
		" where termid = " + SqlInteger.sql(termid));
	rs.next();
	String termName = rs.getString("name");
	String sPaymentdue = rs.getString("paymentdue");
	rs.close();
	
	// Remove previous tuition invoice records
	sqlOut.append(
		" delete from tuitiontrans where termid = " + SqlInteger.sql(termid) +
		" and studentid in" +
		" (select studentid from entities_school" +
		"  where adultid = " + SqlInteger.sql(adultid) + ");\n");
	
	// Recalculate all tuition records for that paying party
	sql =
		" select st.entityid as studentid, p.lastname, p.firstname, c.*\n" +
		" from entities_school st, entities p, courseids c, enrollments e\n" +
		" where st.entityid = p.entityid\n" +
		" and st.adultid = " + SqlInteger.sql(adultid) + "\n" +
		" and c.termid = " + SqlInteger.sql(termid) + "\n" +
		" and e.courseid = c.courseid and e.entityid = st.entityid" +
		" and e.courserole = (select courseroleid from courseroles where name = 'student')" +
		" order by st.entityid, dayofweek, c.tstart";
	rs = st.executeQuery(sql);
	
	// Calculate sum of hours in enrolled courses, per student
	int studentid = -1;
	String description = null;
	double tuition = 0;		// Tuition for one student
	for (;;) {
		boolean hasNext = rs.next();
		if (!hasNext || (studentid != rs.getInt("studentid"))) {
			if (studentid != -1) {
				// This student's records have ended; write out
				sqlOut.append(" insert into tuitiontrans" +
					" (entityid, actypeid, amount, description, ddue, studentid, termid)" +
					" values (" + SqlInteger.sql(adultid) + ", " +
					" (select actypeid from actypes where name = 'school'), " +
					money.sql(tuition) + ", " + SqlString.sql(description) + ", " +
					"'" + sPaymentdue + "', " +
					SqlInteger.sql(studentid) + ", " + SqlInteger.sql(termid) + ");\n");
			}
			
			// Make up description for this next student record.
			studentid = rs.getInt("studentid");
			description = termName + ": Tuition for " +
				rs.getString("firstname") + " " + rs.getString("lastname");
			tuition = 0;
		}
		if (!hasNext) break;
System.out.println(rs.getString("studentid") + " " + rs.getString("name"));
		// Add the price of this course to the tuition
		double price;
		String Price = rs.getString("price");
		if (Price != null) price = Double.parseDouble(Price);
		else {
			long tstart = time.get(rs, "tstart").getTime();
			long tnext = time.get(rs, "tnext").getTime();
			int lengthS = (int)(tnext - tstart) / 1000;
			price = (double)lengthS * (100.0 / 3600.0);	// $100/hr
		}
		tuition += price;
	}
	rs.close();
	
System.out.println(sqlOut);
	st.executeUpdate(sqlOut.toString());
}
// -------------------------------------------------------------------------------
public static void main(String[] args) throws Exception
{
	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
	Statement st = pool.checkout().createStatement();
	w_tuitiontrans_calcTuition(st, 8, 12633);

}
}