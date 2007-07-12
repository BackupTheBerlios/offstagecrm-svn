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
/*
 * EntityDbModel.java
 *
 * Created on February 19, 2006, 12:31 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.db;

import java.sql.*;
import citibob.jschema.*;
import citibob.multithread.*;
import citibob.swing.table.*;
import citibob.app.*;
import citibob.sql.pgsql.*;

/**
 *
 * @author citibob
 */
public class EntityDbModel extends IntKeyedDbModel
{
	
	FamilyTableModel family;	// List of family members of current entity.
	
	/** Creates a new instance of EntityDbModel */
	public EntityDbModel(Schema schema, App app) {
		super(new EntityBuf(schema), "entityid", false, null);
		
		family = new FamilyTableModel(app);//.getAppRunner(), app.getSqlTypeSet());
		SchemaBufRowModel rm = new SchemaBufRowModel(this.getSchemaBuf());
		family.bind(rm);
	}
	public FamilyTableModel getFamily() { return family; }
	
	public void doInsert(Statement st) throws SQLException
	{
//		// Custom select the entityid to use for this record; it should
//		// be placed in not one but two columns!
//		String sql = "select nextval('public.entities_entityid_seq'::text);";
//		ResultSet rs = st.executeQuery(sql);
//		rs.next();
//		Integer id = (Integer)rs.getObject(1);
//		rs.close();
		
		SchemaBuf sb = this.getSchemaBuf();
		Integer Entityid = (Integer)sb.getValueAt(0, "entityid");
	
		// Set family to self if user hasn't set the family otherwise already.
		int pei = sb.findColumn("primaryentityid");
		if (sb.getValueAt(0, pei) == null) sb.setValueAt(Entityid, 0, pei);
		
		// Now do the insert qu\ery!
		super.doInsert(st);
	}
	public void doClear()
	{
		super.doClear();
		family.setRowCount(0);
	}
	
	
	/** Adds entityid to the family of person currently in entity editor */
	public void addToFamily(Statement st, int entityid)
	throws SQLException
	{
	//	int peid = DB.getPrimaryEntityID(st, entityid);
	//	setValueAt(new Integer(peid), 0, findColumn("primaryentityid"));
		SchemaBuf sb = getSchemaBuf();
		int peid = (Integer)sb.getValueAt(0, sb.findColumn("primaryentityid"));
		st.executeUpdate("update entities set primaryentityid = " + SqlInteger.sql(peid) +
			" where entityid = " + SqlInteger.sql(entityid));
//		this.doUpdate(st);
//		this.doSelect(st);
	}

}
