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

import citibob.jschema.*;
import citibob.sql.pgsql.*;
import citibob.sql.*;
import citibob.util.KeyedModel;
import java.sql.*;


public class CheckpaymentsSchema extends ActransSchema
{

public CheckpaymentsSchema(Statement st, DbChangeModel change, java.util.TimeZone tz)
throws SQLException
{
	super(st, change,tz);
	table = "checkpayments";
	appendCols(new Column[] {
		new Column(new SqlString(50), "name"),
		new Column(new SqlString(15), "checknumber"),
		new Column(new SqlString(30), "phone")
	});

}

}
