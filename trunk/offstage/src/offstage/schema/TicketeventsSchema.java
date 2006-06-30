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
package offstage.schema;

import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import java.sql.*;
import citibob.util.KeyedModel;

public class TicketeventsSchema extends ConstSchema
{

public TicketeventsSchema(Statement st, DbChangeModel change)
throws SQLException
{
	super();
	table = "ticketeventsales";
	KeyedModel kmodel = new DbKeyedModel(st, change,
		"ticketeventids", "groupid", "name", "name");
	KeyedModel kTicketTypes = new DbKeyedModel(st, change,
		"tickettypes", "tickettypeid", "tickettype", "tickettype");

	cols = new Column[] {
		new Column(new SqlEnum(kmodel, false), "groupid", true),
		new Column(new SqlInteger(false), "entityid", true),
		new Column(new SqlInteger(true), "numberoftickets", false),
		new Column(new SqlNumeric(9,2,true), "payment", false),
		new Column(new SqlEnum(kTicketTypes, true), "tickettypeid", false)
	};
}

}
