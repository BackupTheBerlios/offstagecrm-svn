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
package offstage.db;

import citibob.jschema.*;
import citibob.multithread.*;
import citibob.sql.*;
import citibob.sql.pgsql.SqlInteger;
import java.util.*;
import java.sql.*;
import offstage.schema.*;

/** Query one person record and all the stuff related to it. */

public class FullEntityDbModel extends MultiDbModel
{

// -------------------------------------------------------
// Entity types
public static final int NONE=0;
public static final int PERSON=1;
public static final int ORG=2;

// Key field.
private int entityID;

// Reflects what's ACTUALLY loaded, not the key field.
private int entityType;

/** Returns type (Person or Organization) of data currently in the buffers.  If neither, returns NONE. */
private void setEntityType()
{
//System.err.println("setEntityType: " + getPersonSb().getRowCount() + ", " + getOrgSb().getRowCount());
	int oldET = getEntityType();

	if (getPersonSb().getRowCount() > 0) entityType = PERSON;
	else if (getOrgSb().getRowCount() > 0) entityType = ORG;
	else entityType = NONE;

	if (getEntityType() != oldET)
		fireEntityTypeChanged(getEntityType());
}
public int getEntityType()
	{ return entityType; }
// -------------------------------------------------------

EntityDbModel onePerson;
EntityDbModel oneOrg;
IntKeyedDbModel phones;
IntKeyedDbModel donations;
IntKeyedDbModel notes;
IntKeyedDbModel tickets;
IntKeyedDbModel events;
IntKeyedDbModel classes;
IntKeyedDbModel interests;
//FamilyTableModel family;

//SchemaBufRowModel onePersonRm;
//SchemaBufRowModel oneOrgRm;


public void setKey(int entityID)
{
	this.entityID = entityID;

	// First, figure out whether 
	onePerson.setKey(entityID);
	oneOrg.setKey(entityID);
	phones.setKey(entityID);
	donations.setKey(entityID);
	notes.setKey(entityID);
	tickets.setKey(entityID);
	events.setKey(entityID);
	classes.setKey(entityID);
	interests.setKey(entityID);
}
public int getEntityId()
{
	return entityID;
}
// ---------------------------------------------------------
// Return the various SchemaBufs that make up this super-record.

public EntityBuf getPersonSb()
	{ return (EntityBuf)onePerson.getSchemaBuf(); }
public EntityDbModel getPersonDb()
	{ return onePerson; }
//public SchemaBufRowModel getPersonRm()
//	{ return onePersonRm; }
public EntityBuf getOrgSb()
	{ return (EntityBuf)oneOrg.getSchemaBuf(); }
public EntityDbModel getOrgDb()
	{ return oneOrg; }
//public SchemaBufRowModel getOrgRm()
//	{ return oneOrgRm; }
public EntityBuf getEntitySb()
	{ return (EntityBuf)getEntity().getSchemaBuf(); }
public EntityDbModel getEntity()
	{ return (entityID == ORG ? oneOrg : onePerson); }
public SchemaBuf getPhonesSb()
	{ return phones.getSchemaBuf(); }
public SchemaBuf getDonationSb()
	{ return donations.getSchemaBuf(); }
public SchemaBuf getEventsSb()
	{ return events.getSchemaBuf(); }
public SchemaBuf getClassesSb()
	{ return classes.getSchemaBuf(); }
public SchemaBuf getInterestsSb()
	{ return interests.getSchemaBuf(); }
public SchemaBuf getNotesSb()
	{ return notes.getSchemaBuf(); }
public SchemaBuf getTicketsSb()
	{ return tickets.getSchemaBuf(); }
//public FamilyTableModel getFamily()
//	{ return family; }
public FullEntityDbModel(OffstageSchemaSet osset, citibob.app.App app)
{
//	add(onePerson = new EntityDbModel(new EntityBuf(new PersonsSchema()), "entityID", false));
//	add(oneOrg = new IntKeyedDbModel(new EntityBuf(new OrgSchema()), "entityID", false));
	add(onePerson = new EntityDbModel(osset.persons, app));
	add(oneOrg = new EntityDbModel(osset.org, app));
	add(phones = new IntKeyedDbModel(osset.phones, "entityID"));
	add(donations = new IntKeyedDbModel(osset.donations, "entityID"));
	add(notes = new IntKeyedDbModel(osset.notes, "entityID"));
	add(tickets = new IntKeyedDbModel(osset.tickets, "entityID"));
	add(events = new IntKeyedDbModel(osset.events, "entityID"));
	add(classes = new IntKeyedDbModel(osset.classes, "entityID"));
	add(interests = new IntKeyedDbModel(osset.interests, "entityID"));
}

public void insertPhone(int groupTypeID) throws KeyViolationException
{
	getPhonesSb().insertRow(-1, "groupid", new Integer(groupTypeID));
}

public void doSelect(Statement st)
throws java.sql.SQLException
{
	super.doSelect(st);
	setEntityType();
	
//	// Now do the read-only stuff
//	String sql =
//		" select e.entityid from entities e, entities pe" +
//		" where pe.entityid = " + entityID +
//		" and e.primaryentityid = pe.primaryentityid";
//	String orderBy = "isprimary desc, relation, name";
//	
////System.out.println("*****************\n" + sql);
//	// ResultSet rs = st.executeQuery(sql);
//	family.setRows(st, sql, orderBy);
	//rs.close();
}

/** Override standard delete.  Don't actually delete record, just set obsolete bit. */
public void doDelete(Statement st)
throws java.sql.SQLException
{
	// Delete the immediate record
	SchemaBufDbModel dm = getEntity();
	SchemaBuf sb = dm.getSchemaBuf();
	sb.setValueAt(Boolean.TRUE, 0, sb.findColumn("obsolete"));
	dm.doUpdate(st);

	// Reassign any other family members
	st.executeUpdate("update entities set primaryentityid=entityid" +
		" where primaryentityid = " + SqlInteger.sql(this.getEntityId()));
	
	// Stop displaying it
	this.setKey(-1);
	this.doSelect(st);
}

/** Sets up the SchemaBufs for a new person,
which will be inserted into the DB upon doUpdate(). */
public void newEntity(Statement st, int entityType) throws java.sql.SQLException
{
	// Clear all existing data --- including in sub-DBModels.
	doClear();

	// Get a new entityID for this record.
	int entityID = DB.r_nextval(st, "entities_entityid_seq");
	setKey(entityID);

	// Insert a blank record with that entityID
	try {
		SchemaBuf sb = (entityType == PERSON ? getPersonSb() : getOrgSb());
		sb.insertRow(-1, new String[] {"entityid", "primaryentityid", "isorg"},
			new Object[] {new Integer(entityID), new Integer(entityID), Boolean.FALSE});
	} catch(KeyViolationException e) {}	// can't happen, buffer is clear.

	// Switch to person view.
	setEntityType();
}

// ===================================================
public static interface Listener
{
	void entityTypeChanged(int type);
}
public static class Adapter implements Listener
{
	public void entityTypeChanged(int type) {}
}
// ===================================================
// ===================================================
// Listener code
public LinkedList listeners = new LinkedList();
public void addListener(Listener l)
	{ listeners.add(l); }
public void fireEntityTypeChanged(int type)
{
	for (Iterator ii = listeners.iterator(); ii.hasNext(); ) {
		Listener l = (Listener)ii.next();
		l.entityTypeChanged(type);
	}
}
// ===================================================


}
