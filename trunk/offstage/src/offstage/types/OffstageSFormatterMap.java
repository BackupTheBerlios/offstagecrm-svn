/*
 * OffstageSFormatterMap.java
 *
 * Created on October 8, 2006, 4:52 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.types;

import citibob.swing.typed.*;
import citibob.text.*;

/**
 *
 * @author citibob
 */
public class OffstageSFormatterMap extends citibob.sql.pgsql.SqlSFormatterMap
{
	
/** Creates a new instance of OffstageSFormatterMap */
public OffstageSFormatterMap() {
	super();
	
	// SqlPhone
	this.addMaker(SqlPhone.class, new Maker() {
	public SFormatter newSFormatter(JType sqlType) {
		return new PhoneFormatter();
	}});
}
	
}
