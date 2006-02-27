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

package offstage.db;

import java.sql.*;
import citibob.sql.*;
import citibob.jschema.pgsql.*;
import java.util.*;

/**
 * A bunch of "stored procedures" for the JMBT database.  This is because
 * PostgreSQL stored procedures are nearly useless.
 * @author citibob
 */
public class DB {


// -------------------------------------------------------------------------------
/** Gets the next value from a sequence. */
public static int r_nextval(Statement st, String sequence) throws SQLException
{
	return SQL.readInt(st, "select nextval('" + sequence + "')");
}
// -------------------------------------------------------------------------------
public static int w_mailingids_create(Statement st, String eqXml, String eqSql)
throws SQLException
{
	int groupID = r_nextval(st, "groupids_groupid_seq");
	//SQL.readInt(st, "select nextval('groupids_groupid_seq')");
	try { st.executeUpdate("drop table _ids"); } catch(SQLException sqe) {}
	String sql;
	sql =
		// Create Mailing List 
		" insert into mailingids" + 
		" (groupid, name, created, equery) values" + 
		" (" + groupID + ", 'Mailing', now(),\n" + SqlString.sql(eqXml) + "\n);" + 

		// Create temporary table of IDs for this mailing list
		" create temporary table _ids (entityid int);" + 
		" delete from _ids;" + 
		" insert into _ids (entityid) " + eqSql + ";" +

		// Insert into Mailing List
		" insert into mailings (groupid, entityid) select " + groupID + ", entityid from _ids;" + 
		" drop table _ids";
System.out.println(sql);
	st.executeUpdate(sql);
	return groupID;
}
// -------------------------------------------------------------------------------
public static void w_mailings_makereport(Statement st, int mailingID)
throws SQLException
{
	String sql;
	st.executeUpdate(
		" update mailings set line1=trim(addressto), line2=trim(address1), line3=trim(address2)" +
		" where groupid = " + mailingID +
		" and address1 is not null and address2 is not null");
	st.executeUpdate(
		" update mailings set line1=null, line2=trim(addressto), line3=trim(address2)" +
		" where groupid = " + mailingID +
		" and address1 is null and address2 is not null");
	st.executeUpdate(
		" update mailings set line1=null, line2=trim(addressto), line3=trim(address1)" +
		" where groupid = " + mailingID +
		" and address1 is not null and address2 is null");
	st.executeUpdate(
		" update mailings set isgood = true where groupid = " + mailingID);
	st.executeUpdate(
		" update mailings set isgood = false where" +
		" groupid = " + mailingID + " and (" +
		" addressto is null" +
		" or (address1 is null and address2 is null)" +
		" or (zip is null and trim(country) = 'USA')" +
		" or city is null" +
		" or state is null" +
		")");
}
// -------------------------------------------------------------------------------
public static int getPrimaryEntityID(Statement st, int eid)
throws SQLException
{
	ResultSet rs = st.executeQuery(
		"select primaryentityid from entities where entityid = " + eid);
	rs.next();
	int peid = rs.getInt(1);
	rs.close();
	return peid;
}

public static int getTransitivePrimaryEntityID(Statement st, int eid)
throws SQLException
{
	int origID = eid;
	TreeSet ids = new TreeSet();
	for (;;) {
		ResultSet rs = st.executeQuery(
			"select primaryentityid from entities where entityid = " + eid);
		rs.next();
		int neid = rs.getInt(1);
		rs.close();
		if (neid == 0) return eid;	// null in SQL table
		if (neid == eid) return eid;	// We've found the root
		Integer Neid = new Integer(neid);
		if (ids.contains(Neid)) return origID;	// Cycle found :-(
		ids.add(Neid);
		eid = neid;
	}
}
// -------------------------------------------------------------------------------
public static ResultSet rs_entities_namesByIDList(Statement st, String idSql, String orderBy)
throws SQLException
{
	if (orderBy == null) orderBy = "relation, name";
//orderBy = "relation, name";
	// Construct sql query to covert IDs to names
	String sql =
		" create temporary table _ids (entityid int); delete from _ids;\n" +
		" delete from _ids;\n" +
		" insert into _ids (entityid) " + idSql + ";\n" +
		" (select o.entityid, 'organizations' as relation, name as name" +
		" , o.entityid = o.primaryentityid as isprimary" +
		" from organizations o, _ids" +
		" where o.entityid = _ids.entityid\n" +
		"   union\n" +
		" select p.entityid, 'persons' as relation," +
		" (case when lastname is null then '' else lastname || ', ' end ||" +
		" case when firstname is null then '' else firstname || ' ' end ||" +
		" case when middlename is null then '' else middlename end) as name" +
		" , p.entityid = p.primaryentityid as isprimary" +
		" from persons p, _ids" +
		" where p.entityid = _ids.entityid)" +
		" order by " + orderBy + ";\n" +
		" drop table _ids";

	/*
	String sql =
		" (select entityid, 'organizations' as relation, name as name" +
		" from organizations" +
		" where entityid in (" + ids + ")" +
		"   union" +
		" select entityid, 'persons' as relation," +
		" case when lastname is null then '' else lastname || ', ' end ||" +
		" case when firstname is null then '' else firstname || ' ' end ||" +
		" case when middlename is null then '' else middlename end" +
		" from persons" +
		" where entityid in (" + ids + "))" +
		" order by relation, name";
	 */
System.out.println(sql);
	ResultSet rs = st.executeQuery(sql);
	return rs;
//	super.addAllRows(rs);
//	rs.close();
}
// --------------------------------------------------
}
