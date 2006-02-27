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
import citibob.jschema.pgsql.*;

public class PersonsSchema extends EntitiesSchema
{

public PersonsSchema()
{
	super();
	table = "persons";
	appendCols(new Column[] {
		new Column(new SqlString(30), "title", false),
		new Column(new SqlString(50), "occupation", false),
		new Column(new SqlString(30), "salutation", false),
		new Column(new SqlString(50), "firstname", false),
		new Column(new SqlString(50), "middlename", false),
		new Column(new SqlString(50), "lastname", false),
		new Column(new SqlString(1), "gender", false),
		new Column(new SqlDate(), "dob", false),
		new Column(new SqlString(100), "customaddressto", false),
		new Column(new SqlString(100), "email", false)
	});
}
// ------------------------------------------
// Singleton stuff
private static PersonsSchema instance = new PersonsSchema();
public static ConstSchema getInstance()
	{ return instance; }

}
