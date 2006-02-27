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

/**
 *
 * @author citibob
 */
public class EntityDbModel extends IntKeyedDbModel
{
	
	FamilyTableModel family;	// List of family members of current entity.
	
	/** Creates a new instance of EntityDbModel */
	public EntityDbModel(Schema schema, ActionRunner appRunner) {
		super(new EntityBuf(schema), "entityid", false);
		
		family = new FamilyTableModel(appRunner);
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
}
