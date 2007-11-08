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
package offstage.schema;

import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import java.sql.*;
import citibob.types.*;
import java.util.*;

public class TicketeventsSchema extends ConstSchema
{

public TicketeventsSchema(citibob.sql.SqlRunner str, DbChangeModel change, TimeZone tz)
throws SQLException
{
	super();
	table = "ticketeventsales";
	KeyedModel kmodel = new DbKeyedModel(str, change,
		"ticketeventids", "groupid", "name", "name");
	KeyedModel kTicketTypes = new DbKeyedModel(str, change,
		"tickettypes", "tickettypeid", "name", "name");
	KeyedModel kVenues = new DbKeyedModel(str, change,
		"venueids", "venueid", "name", "name");
	KeyedModel kOfferCodes = new DbKeyedModel(str, change,
		"offercodeids", "offercodeid", "name", "name");
	KeyedModel kPerfTypes = new DbKeyedModel(str, change,
		"perftypeids", "perftypeid", "name", "name");
	cols = new Column[] {
		new Column(new SqlEnum(kmodel, false), "groupid", true),
		new Column(new SqlInteger(false), "entityid", true),
		new Column(new SqlInteger(true), "numberoftickets", false),
		new Column(new SqlNumeric(9,2,true), "payment", false),
		new Column(new SqlEnum(kTicketTypes, true), "tickettypeid", false),
		new Column(new SqlEnum(kVenues, true), "venueid", false),
		new Column(new SqlEnum(kOfferCodes, "<none>"), "offercodeid", false),
		new Column(new SqlEnum(kPerfTypes, "<unknown>"), "perftypeid", false),
		new Column(new SqlDate(tz, true), "date", false)
	};
}

}
