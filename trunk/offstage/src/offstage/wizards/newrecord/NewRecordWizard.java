package offstage.wizards.newrecord;
/*
 * NewRecordWizard.java
 *
 * Created on October 8, 2006, 11:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import citibob.swing.html.*;
import javax.swing.*;
import java.sql.*;

/**
 *
 * @author citibob
 */
public class NewRecordWizard extends Wizard {

	Statement st;		// Datbase connection
	/*
addState(new State("", "", "") {
	public HtmlDialog newWiz()
		{ return new }
	public void process()
	{
		
	}
});
*/
	
public NewRecordWizard(Statement xst, java.awt.Frame xframe)
{
	super(xframe, "init");
	this.st = xst;
// ---------------------------------------------
addState(new State("init", "init", "init") {
	public HtmlDialog newWiz() throws Exception
		{ return new InitWiz(frame); }
	public void process() throws Exception
	{
		String s = v.getString("type");
		if (s != null) state = s;
	}
});
// ---------------------------------------------
addState(new State("person", "init", "checkperson") {
	public HtmlDialog newWiz() throws Exception
		{ return new PersonWiz(frame); }
	public void process() throws Exception
	{
//		if (!checkFieldsFilledIn()) return;
		String sql = offstage.db.DupCheck.checkDups(st, v, 3, 20);
		System.out.println("DupCheck sql: " + sql);
	}
});
// ---------------------------------------------
	
}

public static void main(String[] args) throws Exception
{
	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
	Statement st = pool.checkout().createStatement();
	Wizard wizard = new NewRecordWizard(st, null);
	wizard.runWizard();
}

}
