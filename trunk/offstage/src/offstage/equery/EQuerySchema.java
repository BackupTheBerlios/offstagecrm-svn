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
package offstage.equery;

import java.util.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.swing.typed.*;
import java.sql.*;
import citibob.util.KeyedModel;

public class EQuerySchema extends QuerySchema
{

// --------------------------------------------------
public EQuerySchema(SchemaSet sset) throws SQLException
{
	super();
	addSchema(sset.get("entities"),
		"entities.entityid = main.entityid");
	addSchema(sset.get("org"),
		"organizations.entityid = main.entityid");
	addSchema(sset.get("persons"),
		"persons.entityid = main.entityid");
	addSchema(sset.get("events"),
		"events.entityid = main.entityid");
	addSchema(sset.get("donations"),
		"donations.entityid = main.entityid");
	addSchema(sset.get("notes"),
		"notes.entityid = main.entityid");
	addSchema(sset.get("phones"),
		"phones.entityid = main.entityid");
	addSchema(sset.get("classes"),
		"classes.entityid = main.entityid");
	addSchema(sset.get("interests"),
		"interests.entityid = main.entityid");
	addSchema(sset.get("tickets"),
		"ticketeventsales.entityid = main.entityid");
	doAlias(alias);
}

// --------------------------------------------------------------------
private static final String[] alias = {
	"persons.isorg", "isorg",
	"persons.firstname", "firstname",
	"persons.middlename", "middlename",
	"persons.lastname", "lastname",
	"persons.gender", "gender",
	"persons.email", "email",
	"persons.occupation", "occupation",
	"persons.orgname", "orgname",
	"entities.address1", "address1",
	"entities.address2", "address2",
	"entities.city", "city",
	"entities.state", "state",
	"entities.zip", "zip",
	"entities.country", "country",
	"entities.lastupdated", "lastupdated",
	"entities.sendmail", "sendmail",
//	"organizations.name", "org-name",
	"events.groupid", "event-type",
//	"events.role", "event-role",
	"donations.groupid", "donation-type",
	"donations.date", "donation-date",
	"donations.amount", "donation-amount",
	"notes.groupid", "note-type",
	"notes.date", "note-date",
	"notes.note", "note",
	"phones.groupid", "phone-type",
	"phones.phone", "phone",
	"classes.groupid", "classes",
	"interests.groupid", "interests",
	"ticketeventsales.groupid", "tickets",
	"entities.entityid", "entityid",
	"entities.obsolete", "obsolete",
};

}
