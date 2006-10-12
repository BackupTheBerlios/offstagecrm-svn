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

import citibob.jschema.*;
import citibob.sql.pgsql.*;
import citibob.sql.*;


public class EQueriesSchema extends ConstSchema
{

public EQueriesSchema()
{
	table = "equeries";
	cols = new Column[] {
		new Column(new SqlSerial("equeries_equeryid_seq", false), "equeryid", true),
		new Column(new SqlString(), "equery", false),
		new Column(new SqlTimestamp(), "lastmodified", false),
//		new Column(new SqlTimestamp(), "lastaccessed", false),
		new Column(new SqlString(100), "name", false)
	};
}

}