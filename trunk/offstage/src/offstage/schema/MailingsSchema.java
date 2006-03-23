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
import citibob.sql.DbChangeModel;
import java.sql.*;

public class MailingsSchema extends ConstSchema
{

public MailingsSchema(Statement st, DbChangeModel change)
throws SQLException
{
	super();
	table = "mailings";
	KeyedModel kmodel = new DbKeyedModel(st, change,
		"mailingids", "groupid", "name", "name");
	cols = new Column[] {
		new Column(new SqlEnum(kmodel, false), "groupid", true),
		new Column(new SqlInteger(false), "entityid", true),
		new Column(new SqlString(), "addressto", false),
		new Column(new SqlString(), "address1", false),
		new Column(new SqlString(), "address2", false),
		new Column(new SqlString(), "city", false),
		new Column(new SqlString(), "state", false),
		new Column(new SqlString(), "zip", false),
		new Column(new SqlString(), "country", false)
	};
}

}
