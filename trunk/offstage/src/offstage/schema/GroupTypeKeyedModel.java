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
/*
 * PhoneTypeKeyedModel.java
 *
 * Created on April 6, 2005, 10:24 PM
 */

package offstage.schema;

import citibob.jschema.KeyedModel;
import java.sql.*;


/**
 *
 * @author citibob
 */
public class GroupTypeKeyedModel extends KeyedModel {

/** Creates a new instance of PhoneTypeKeyedModel */
public GroupTypeKeyedModel(Statement st, String idTableName) throws SQLException
{
	super();
	ResultSet rs = null;
	try {
		rs = st.executeQuery("select groupid, name from " + idTableName + " order by name");
		addAllItems(rs, 1, 2);
	} finally {
		rs.close();
	}
}

}
