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
package offstage.school.gui;

import citibob.jschema.*;
import citibob.multithread.*;
import citibob.sql.*;
import citibob.sql.pgsql.SqlInteger;
import java.util.*;
import java.sql.*;
import offstage.schema.*;
import citibob.jschema.log.*;
import offstage.db.*;

/** Query one person record and all the stuff related to it. */

public class FullStudentDbModel extends MultiDbModel
{

//// Key field.
//private int entityID;

//public int getEntityId()
//{ return entityID; }

public final EntityDbModel personDb;
public final IntKeyedDbModel schoolDb;
public final IntKeyedDbModel notesDb;

//public void setKey(int entityID)
//{
//	this.entityID = entityID;
//
//	personDb.setKey(entityID);
//	schoolDb.setKey(entityID);
//	notesDb.setKey(entityID);
//}
// ---------------------------------------------------------

void logadd(QueryLogger logger, SchemaBufDbModel m)
{
	add(m);
	m.setLogger(logger);
}
public FullStudentDbModel(offstage.FrontApp fapp)
{
	citibob.app.App app = fapp;
	QueryLogger logger = fapp.getLogger();
	logadd(logger, personDb = new EntityDbModel(fapp.getSchema("persons"), app));
	logadd(logger, schoolDb = new IntKeyedDbModel(fapp.getSchema("entities_school"), "entityid", true));
	logadd(logger, notesDb = new IntKeyedDbModel(fapp.getSchema("notes"), "entityid", true));
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
		personDb.getSchemaBuf().insertRow(-1, new String[] {"entityid", "primaryentityid", "isorg"},
			new Object[] {new Integer(entityID), new Integer(entityID), Boolean.FALSE});
	} catch(KeyViolationException e) {}	// can't happen, buffer is clear.
}



}
