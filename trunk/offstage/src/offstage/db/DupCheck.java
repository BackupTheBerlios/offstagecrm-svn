/*
 * DupCheck.java
 *
 * Created on October 9, 2006, 12:07 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.db;

import citibob.wizard.*;
import citibob.sql.pgsql.*;
import java.sql.*;
import java.util.*;

/**
 * Try to find possible duplicate records, given the data from one record
 * (or prospective record)
 * @author citibob
 */
public class DupCheck {

TypedHashMap v;
// Results of trying to parse


// Parsed Data
String streetName;
String zip;
String lastName;
String firstName;
String phone;
String email;

/** Higher score means more likely to be a duplicate. */
static class Score
{
	int entityID;
	int score;
}
HashMap scores;
// -------------------------------------------------------------
String getString(String name)
{
	String addr1 = v.getString(name);
	if (addr1 == null) addr1 = "";
	else addr1 = addr1.trim();
	return addr1;
}
String getStringNull(String name)
{
	String addr1 = v.getString(name);
	if (addr1 == null) return null;
	addr1 = addr1.trim();
	if ("".equals(addr1)) return null;
	return addr1;
}

String[] splitWords(String s)
{
	s = s.replace("\t", " ");
	s = s.replace("\n", " ");
	String[] tok = s.split(" ");
	return tok;
}

static boolean containsNumeric(String s)
{
	for (int i=0; i<s.length(); ++i) {
		char c = s.charAt(i);
		if (c >= '0' && c <= '9') return true;
	}
	return false;
}

static boolean isNumeric(String s)
{
	for (int i=0; i<s.length(); ++i) {
		char c = s.charAt(i);
		if (c >= '0' && c <= '9') continue;
		return false;
	}
	return true;
}

void parse()
//throws java.text.ParseException
{
	// Try to parse out the street name
	streetName = null;
	String addr1 = getString("addr1");
	String addr2 = getString("addr2");
	String addr = "".equals(addr2) ? addr1 : addr2;
	if (!"".equals(addr)) {
		String[] tok = splitWords(addr);
		if (!(isNumeric(tok[0]))) {
			// Forget about address; probably a PO Box
		} else {
			// First word not containing number is probably street name
			for (int i=0; i<tok.length; ++i) {
				if (!containsNumeric(tok[i])) {
					streetName = tok[i];
					break;
				}
			}
		}
		if ("".equals(streetName)) streetName = null;
	}
	
	// Parse out 5-digit zip code
	zip = null;
	String z = getString("zip");
	if (z.length() >= 5) {
		z = z.substring(0,5);
		if (isNumeric(z)) zip = z;
	}
	
	// Get first and last name --- easy!
	lastName = getStringNull("lastname");
	firstName = getStringNull("firstname");
	
	// Parse out phone number
//	phone = offstage.types.PhoneFormatter.unformat(getStringNull("phone"));
	phone = getStringNull("phone");
	email = getStringNull("email");
}
// --------------------------------------------------------------
public void addScore(Integer EntityID)
{
	Score s = (Score)scores.get(EntityID);
	if (s == null) {
		s = new Score();
		s.entityID = EntityID.intValue();
		s.score = 1;
		scores.put(EntityID, s);
	} else {
		++s.score;
	}
}

void addScores(Statement st, String table, String whereClause)
throws SQLException
{
	String sql = "select distinct entityid from " + table + " where " + whereClause;
System.out.println("DupCheck: " + sql);
	ResultSet rs = st.executeQuery(sql);
	while (rs.next()) {
		Integer EntityID = (Integer)rs.getObject(1);
		addScore(EntityID);
	}
	rs.close();
}
void addScores(Statement st, String whereClause)
throws SQLException
{
	addScores(st, "entities", "not obsolete and " + whereClause);
}
/** Returns entityid of possible dups */
void scoreDups(Statement st)
throws SQLException
{
	scores = new HashMap();
	String sql;

	if (firstName != null)
		addScores(st, "firstname ilike " + SqlString.sql("%" + firstName + "%"));
	if (lastName != null)
		addScores(st, "lastname ilike " + SqlString.sql("%" + lastName + "%"));
	if (streetName != null)
		addScores(st, "address1 ilike " + SqlString.sql("%" + streetName + "%") +
			" or address2 ilike " + SqlString.sql("%" + streetName + "%"));
	if (zip != null)
		addScores(st, "zip ilike " + SqlString.sql("%" + zip + "%"));
	if (phone != null)
		addScores(st, "phones", "phone = " + SqlString.sql(phone));
	if (email != null)
		addScores(st, "persons", "email ilike " + SqlString.sql(email));
	
}
// --------------------------------------------------------------------
/** Creates a query that selects out all the dups of score >= minScore.
 Returns null if >maxDups entities fit the bill.  This will be used
 in EntityListTableModel. */
String getIDSql(int minScore, int maxDups)
{
	StringBuffer sql = new StringBuffer(
		"select entityid as id from entities where entityid in (-1");
	int nid = 0;
	for (Iterator ii=scores.entrySet().iterator(); ii.hasNext(); ) {
		Map.Entry e = (Map.Entry)ii.next();
		Score s = (Score)e.getValue();
		if (s.score >= minScore) {
			if (++nid > maxDups) return null;
			sql.append(",");
			sql.append(e.getKey());
		}
	}
	sql.append(")");
	return sql.toString();
}
// --------------------------------------------------------------
/** Creates a new instance of DupCheck */
public DupCheck(Statement st, TypedHashMap v)
throws SQLException
{
	this.v = v;
	parse();
	scoreDups(st);
}
// --------------------------------------------------------------
/** @param v a set of (name,value) pairs corresponding to wizard screen or database row. */
public static String checkDups(Statement st, TypedHashMap v, int minScore, int maxDups)
throws SQLException
{
	DupCheck dc = new DupCheck(st, v);
	return dc.getIDSql(minScore, maxDups);
}
}
