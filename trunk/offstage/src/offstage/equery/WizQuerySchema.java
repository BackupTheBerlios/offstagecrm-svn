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
import offstage.schema.OffstageSchemaSet;
import citibob.util.KeyedModel;

public class WizQuerySchema extends QuerySchema
{

// --------------------------------------------------
public WizQuerySchema(Statement st, OffstageSchemaSet sset) throws SQLException
{
	super(st);
	addSchema(sset.events,
		"events.entityid = main.entityid");
	addSchema(sset.donations,
		"donations.entityid = main.entityid");
	addSchema(sset.classes,
		"classes.entityid = main.entityid");
	addSchema(sset.interests,
		"interests.entityid = main.entityid");
	addSchema(sset.tickets,
		"ticketeventsales.entityid = main.entityid");
	doAlias(alias);
}
// --------------------------------------------------------------------
private static final String[] alias = {
	"events.groupid", "events",
	"donations.groupid", "donations",
	"classes.groupid", "classes",
	"interests.groupid", "interests",
	"tickets.groupid", "tickets"
};

}