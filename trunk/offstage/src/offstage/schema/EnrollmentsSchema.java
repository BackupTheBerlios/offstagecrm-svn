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
import java.sql.*;
import citibob.types.*;

public class EnrollmentsSchema extends ConstSchema
{

public EnrollmentsSchema(citibob.sql.SqlRunner str, DbChangeModel change, java.util.TimeZone tz)
throws SQLException
{
	super();
	table = "enrollments";
	KeyedModel kmodel = new DbKeyedModel(str, change,
		"courseroles", "courseroleid", "name", "orderid,name");
	cols = new Column[] {
		new Column(new SqlInteger(false), "courseid", true),
		new Column(new SqlInteger(false), "entityid", true),
		new Column(new SqlEnum(kmodel, "<none>"), "courserole"),
		new Column(new SqlDate(tz, true), "dstart"),
		new Column(new SqlDate(tz, true), "dend"),
		new Column(new SqlInteger(true), "pplanid"),
		new Column(new SqlTimestamp("GMT"), "dtapproved"),
		new Column(new SqlTimestamp("GMT"), "dtenrolled")
	};
}

}
