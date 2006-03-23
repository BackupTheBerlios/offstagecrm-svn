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

public class EntitiesSchema extends ConstSchema
{

public EntitiesSchema()
{
	table = "entities";
	cols = new Column[] {
			new Column(new SqlInteger(false), "entityid", true),
			new Column(new SqlInteger(), "primaryentityid", false),
			new Column(new SqlString(100), "address1", false),
			new Column(new SqlString(50), "address2", false),
			new Column(new SqlString(50), "city", false),
			new Column(new SqlString(10), "state", false),
			new Column(new SqlString(10), "zip", false),
			new Column(new SqlString(50), "country", false),
			new Column(new SqlString(50), "recordsource", false),
			new Column(new SqlInteger(), "sourcekey", false),
			//new Column(new SqlInteger(), "ipeopleid", false),
			new Column(new SqlTimestamp(), "lastupdated", false),
			new citibob.jschema.Column(new SqlInteger(), "relprimarytypeid", false),
			//new citibob.jschema.Column(new SqlBool(), "isquery", false),
			new Column(new SqlBool(), "sendmail", false),
			new Column(new SqlBool(), "obsolete", false)
	};
}	
// ------------------------------------------
// Singleton stuff
private static EntitiesSchema instance = new EntitiesSchema();
public static ConstSchema getInstance()
	{ return instance; }

}
