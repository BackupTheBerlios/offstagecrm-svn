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

public class EntitiesSchoolSchema extends ConstSchema
{

public EntitiesSchoolSchema(Statement st, DbChangeModel change)
throws SQLException
{
	table = "entities_school";

//	KeyedModel kmodel = new DbKeyedModel(st, change,
//		"relprimarytypes", "relprimarytypeid", "name", "name");
	cols = new Column[] {
			new Column(new SqlInteger(false), "entityid", true),
			new Column(new SqlInteger(), "adultid", false)
	};
}	
// ------------------------------------------
//// Singleton stuff
//private static EntitiesSchema instance = new EntitiesSchema();
//public static ConstSchema getInstance()
//	{ return instance; }

}
