/*
 * OffstageSwingerMap.java
 *
 * Created on October 8, 2006, 4:52 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.types;

import citibob.swing.typed.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.swing.pgsql.*;

/**
 *
 * @author citibob
 */
public class OffstageSwingerMap extends citibob.sql.pgsql.SqlSwingerMap
{
	
/** Creates a new instance of OffstageSwingerMap */
public OffstageSwingerMap(java.util.TimeZone tz) {
	super(tz);
	
	// SqlPhone
	this.addMaker(SqlPhone.class, new SwingerMap.Maker() {
	public Swinger newSwinger(JType sqlType) {
		return new PhoneSwinger();
	}});

	// OVERRIDE: SqlTime
	this.addMaker(SqlTime.class, new SwingerMap.Maker() {
	public Swinger newSwinger(JType sqlType) {
		return new SqlTimeSwinger((SqlTime)sqlType, "HH:mm");
	}});
	
}
	
}
