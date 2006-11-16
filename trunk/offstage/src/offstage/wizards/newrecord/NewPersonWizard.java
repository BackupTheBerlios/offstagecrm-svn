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
public class NewPersonWizard extends OffstageWizard {

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
	
public NewPersonWizard(offstage.FrontApp xfapp, Statement xst, java.awt.Frame xframe)
{
	super("New Person", xfapp, xframe, "person");
	this.st = xst;
// ---------------------------------------------
//addState(new State("init", "init", "init") {
//	public HtmlWiz newWiz() throws Exception
//		{ return new InitWiz(frame); }
//	public void process() throws Exception
//	{
//		String s = v.getString("type");
//		if (s != null) state = s;
//	}
//});
//// ---------------------------------------------
//addState(new State("person", "init", null) {
addState(new State("person", null, null) {
	public HtmlWiz newWiz() throws Exception
		{ return new PersonWiz(frame); }
	public void process() throws Exception
	{
		if (state == null) {
			// First: do a simple check of data entry
			if (!isValid()) {
				JOptionPane.showMessageDialog(wiz,
					"Invalid input.\nPlease fill in all the fields!");
				state = "person";
			} else {
				String idSql = offstage.db.DupCheck.checkDups(st, v, 3, 20);
				v.put("idsql", idSql);
				System.out.println("DupCheck sql: " + idSql);
				int ndups = DB.countIDList(st, idSql);
				if (ndups == 0) {
					createPerson();
					state = "finished";
				} else {
					state = "checkperson";
				}
				//state = (ndups == 0 ? "finished" : "checkperson");
			}
		}
	}
});
// ---------------------------------------------
// Duplicates were found; double-check.
addState(new State("checkperson", "person", null) {
	public HtmlWiz newWiz() throws Exception
		{ return new DupsWiz(frame, st, fapp, v.getString("idsql")); }
	public void process() throws Exception
	{
		String submit = v.getString("submit");
		if ("dontadd".equals(submit)) state = null;
		if ("addanyway".equals(submit)) {
			createPerson();
			state = "finished";
System.out.println("Add anyway!");
		}
	}
});
// ---------------------------------------------
// Duplicates were found; double-check.
addState(new State("finished", null, null) {
	public HtmlWiz newWiz() throws Exception
		{ return new FinishedWiz(frame); }
	public void process() throws Exception
		{}
});
// ---------------------------------------------

}

// ====================================================
private void addSCol(SqlQuery q, String col)
{
	String val = v.getString(col);
	if (val != null) q.addColumn(col, SqlString.sql(val));
}
void createPerson() throws SQLException
{
	// Make main record
	int id = DB.r_nextval(st, "entities_entityid_seq");
	SqlQuery q = new SqlQuery("persons", SqlQuery.INSERT);
	q.addColumn("entityid", SqlInteger.sql(id));
	addSCol(q, "lastname");
	addSCol(q, "middlename");
	addSCol(q, "firstname");
	addSCol(q, "address1");
	addSCol(q, "address2");
	addSCol(q, "city");
	addSCol(q, "state");
	addSCol(q, "zip");
	addSCol(q, "email");
	String sql = q.getInsertSQL();
System.out.println(sql);
	st.execute(sql);
	
	// Make phone record --- first dig for keyed model...
	String phone = v.getString("phone");
	if (phone != null) {
		q = new SqlQuery("phones", SqlQuery.INSERT);
		q.addColumn("entityid", SqlInteger.sql(id));
		q.addColumn("groupid", "(select groupid from phoneids where name = 'home')");
		q.addColumn("phone", SqlString.sql(phone));
		sql = q.getInsertSQL();
System.out.println(sql);
		st.execute(sql);
	}
//	Schema phones = fapp.getSchemaSet().phones;
//	Column col = phones.getCol(phones.findCol("groupid"));
//	SqlEnum type = (SqlEnum)col.getType();
//	KeyedModel kmodel = type.getKeyedModel();
//	kmodel.
}

boolean notnull(String field)
{
	return (v.getString(field) != null);
}
/** Initial check on validity of info inputted. */
boolean isValid()
{
	return notnull("lastname");
}

public static void main(String[] args) throws Exception
{
	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
	Statement st = pool.checkout().createStatement();
	FrontApp fapp = new FrontApp(pool,null);
	Wizard wizard = new NewPersonWizard(fapp, st, null);
	wizard.runWizard();
}

}
