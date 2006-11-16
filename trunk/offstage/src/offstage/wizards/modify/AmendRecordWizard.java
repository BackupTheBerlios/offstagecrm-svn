package offstage.wizards.modify;
/*
 * NewRecordWizard.java
 *
 * Created on October 8, 2006, 11:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import citibob.sql.pgsql.SqlInteger;
import citibob.swing.html.*;
import javax.swing.*;
import java.sql.*;
import offstage.db.*;
import offstage.wizards.*;
import offstage.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;

/**
 *
 * @author citibob
 */
public class AmendRecordWizard extends OffstageWizard {

	Statement st;		// Datbase connection
	/*
addState(new State("", "", "") {
	public HtmlWiz newWiz()
		{ return new }
	public void process()
	{
		
	}
});
*/
	
public AmendRecordWizard(offstage.FrontApp xfapp, Statement xst, java.awt.Frame xframe)
{
	super("New Person", xfapp, xframe, "find");
	this.st = xst;
// ---------------------------------------------
addState(new State("find", null, null) {
	public HtmlWiz newWiz() throws Exception
		{ return new FindWiz(frame, st, fapp); }
	public void process() throws Exception
	{
		FindWiz w = (FindWiz)wiz;
//System.out.println("entityid = " + w.getEntityID());
		v.put("entityid", w.getEntityID());
	}
});
}
// ---------------------------------------------

}

