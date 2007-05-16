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
import offstage.schema.*;
import citibob.sql.*;
import offstage.db.*;
import java.sql.*;

/**
 *
 * @author citibob
 */
public class EntityBuf extends SchemaBuf
{
	
/** Creates a new instance of EntitySchemaBuf.  schema is either PersonSchema or OrgSchema */
public EntityBuf(Schema schema) {
	super(schema);
}
	
/** Automatically set lastupdated every time row saved to DB. */
public void getUpdateCols(int row, ConsSqlQuery q, boolean updateUnchanged)
{
	super.getUpdateCols(row, q, updateUnchanged);
	q.addColumn("lastupdated", "now()");
}

/** Automatically set lastupdated every time row saved to DB. */
public void getInsertCols(int row, ConsSqlQuery q, boolean insertUnchanged)
{
	super.getInsertCols(row, q, insertUnchanged);
	q.addColumn("lastupdated", "now()");
	q.addColumn("created", "now()");
}

// ==================================================================
// Custom edits to data in the buffer...
public void setFamilySameAs(Statement st, int entityid)
throws SQLException
{
	int peid = DB.getPrimaryEntityID(st, entityid);
	setValueAt(new Integer(peid), 0, findColumn("primaryentityid"));
}
		
public void clearFamily()
{
	setValueAt(getValueAt(0, "entityid"), 0, findColumn("primaryentityid"));
}

}
