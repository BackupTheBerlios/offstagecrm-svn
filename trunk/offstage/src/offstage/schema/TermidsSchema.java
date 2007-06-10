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



public class TermidsSchema extends ConstSchema
{

public TermidsSchema(Statement st, DbChangeModel change, java.util.TimeZone tz)
throws SQLException{
	table = "termids";
	KeyedModel kmodel = new DbKeyedModel(st, change,
		"termtypes", "termtypeid", "name", "orderid");
	cols = new Column[] {
		new Column(new SqlSerial("termids_termid_seq", false), "termid", true),
		new Column(new SqlEnum(kmodel, false), "termtypeid", false),
		new Column(new SqlString(), "name", false),
//		new Column(new SqlDate(), "firstdate", false),
//		new Column(new SqlDate(), "nextdate", false)
		new ColumnDefaultNow(new SqlDate(tz, false), "firstdate", false),
		new ColumnDefaultNow(new SqlDate(tz, false), "nextdate", false),
		new Column(new SqlBool(false), "iscurrent", false)
	};
}

}
