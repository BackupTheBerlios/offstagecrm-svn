/*sw
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
package offstage;
import java.sql.*;
import java.util.*;
import citibob.sql.*;
import citibob.multithread.*;
import offstage.db.FullEntityDbModel;
import offstage.db.EntityListTableModel;
import offstage.db.TestConnPool;
import offstage.equery.*;
import citibob.multithread.*;
import citibob.sql.*;
import citibob.swing.typed.*;
import offstage.schema.*;
import citibob.mail.*;
import javax.mail.internet.*;
//import offstage.equery.swing.EQueryModel2;
import citibob.jschema.*;
import java.util.prefs.*;
import citibob.text.*;
import citibob.sql.pgsql.*;

public class FrontApp implements citibob.app.App
{

public static final int ACTIONS_SCREEN = 0;
public static final int PEOPLE_SCREEN = 1;
public static final int QUERIES_SCREEN = 2;
public static final int MAILINGS_SCREEN = 3;
int screen = PEOPLE_SCREEN;


/** Connection to our SQL database. */
//Connection db;
DbChangeModel dbChange;
ConnPool pool;
SwingerMap swingerMap;
SFormatterMap sFormatterMap;
OffstageSchemaSet sset;
EQuerySchema equerySchema;

FullEntityDbModel fullEntityDm;
//EQueryModel2 equeries;
MailingModel2 mailings;
EntityListTableModel simpleSearchResults;
SwingActionRunner guiRunner;		// Run user-initiated actions; when user hits button, etc.
	// This will put on queue, etc.
ActionRunner appRunner;		// Run secondary events, in response to other events.  Just run immediately
MailSender mailSender;	// Way to send mail (TODO: make this class MVC.)
SqlTypeSet sqlTypeSet;		// Conversion between SQL types and SqlType objects

int loginID;			// entityID of logged-in database application user
TreeSet<String> loginGroups;	// Groups to which logged-in user belongs (by name)
// -------------------------------------------------------
//public int getLoginID() { return loginID; }
public ConnPool getPool() { return pool; }
public void runGui(java.awt.Component c, CBRunnable r) { guiRunner.doRun(c, r); }
/** Only runs the action if logged-in user is a member of the correct group */
public void runGui(java.awt.Component c, String group, CBRunnable r) {
	if (loginGroups.contains(group)) {
		runGui(c,r);
	} else {
		javax.swing.JOptionPane.showMessageDialog(c, "You are not authorized for that action.");
	}
}
public void runGui(java.awt.Component c, String[] groups, CBRunnable r)
{
	if (groups == null) {
		runGui(c, r);
		return;
	}
	for (String g : groups) {
		if (loginGroups.contains(g)) {
			runGui(c,r);
			return;
		}
	}
	javax.swing.JOptionPane.showMessageDialog(c, "You are not authorized for that action.");
}

//public void runGui(CBRunnable r) { guiRunner.doRun(null, r); }
public void runApp(CBRunnable r) { appRunner.doRun(r); }
public MailSender getMailSender() { return mailSender; }
public Schema getSchema(String name) { return sset.get(name); }
public citibob.sql.SqlTypeSet getSqlTypeSet() { return sqlTypeSet; }

//// Legacy...
//public SwingActionRunner getGuiRunner() { return guiRunner; }
public ActionRunner getAppRunner() { return appRunner; }

/** @returns Root user preferences node for this application */
public java.util.prefs.Preferences userRoot()
{
	Preferences p = Preferences.userRoot();
	p = p.node("offstage");
	return p;
}

/** @returns Root system preferences node for this application */
public java.util.prefs.Preferences systemRoot()
{
	return null;
//	Preferences p = Preferences.systemRoot();
//	p = p.node("offstage");
}

//public Connection createConnection()
//throws SQLException
//{
//	return DBConnection.getConnection();
//}
// -------------------------------------------------------
public FrontApp(ConnPool pool, javax.swing.text.Document stdoutDoc)
throws SQLException, java.io.IOException, javax.mail.internet.AddressException
{
	Connection dbb = null;
	Statement st = null;

	this.mailSender = new citibob.mail.GuiMailSender();
//	this.swingerMap = new citibob.sql.pgsql.SqlSwingerMap();
	this.sqlTypeSet = new citibob.sql.pgsql.PgsqlTypeSet();
	this.swingerMap = new offstage.types.OffstageSwingerMap();
	this.sFormatterMap = new offstage.types.OffstageSFormatterMap();
	
	this.pool = pool;
	//pool = new DBConnPool();
	MailSender sender = new GuiMailSender();
	ExpHandler expHandler = new MailExpHandler(sender,
			new InternetAddress("citibob@comcast.net"), "OffstageArts", stdoutDoc);
	guiRunner = new BusybeeDbActionRunner(pool, expHandler);
	appRunner = new SimpleDbActionRunner(pool, expHandler);
	//guiRunner = new SimpleDbActionRunner(pool);
	try {
		dbb = pool.checkout();
		st = dbb.createStatement();
	

		dbChange = new DbChangeModel();
		this.sset = new OffstageSchemaSet(st, dbChange);
		fullEntityDm = new FullEntityDbModel(sset, this);
		mailings = new MailingModel2(st, sset);//, appRunner);
		
		// Figure out who we're logged in as
		String sql = "select entityid from dblogins where username = " +
			SqlString.sql(System.getProperty("user.name"));
		ResultSet rs = st.executeQuery(sql);
		if (rs.next()) {
			loginID = rs.getInt("entityid");
		} else {
			loginID = -1;
		}
		rs.close();
		
		// Figure out what groups we belong to (for action permissions)
		loginGroups = new TreeSet();
		sql = " select distinct name from dblogingroups g, dblogingroupids gid" +
			" where g.entityid=" + SqlInteger.sql(loginID) +
			" and g.groupid = gid.groupid";
		rs = st.executeQuery(sql);
		while (rs.next()) loginGroups.add(rs.getString("name"));
		rs.close();
//	mailings.refreshMailingids();
//		equeries = new EQueryModel2(st, mailings, sset);
		simpleSearchResults = new EntityListTableModel(this.getSqlTypeSet());
	} finally {
		st.close();
		pool.checkin(dbb);
	}
	equerySchema = new EQuerySchema(getSchemaSet());
	
}
public EntityListTableModel getSimpleSearchResults()
	{ return simpleSearchResults; }
//public Statement createStatement() throws java.sql.SQLException
//	{ return db.createStatement(); }

//public Connection getDb()
//	{ return db; }

// ------------------------------------
public FullEntityDbModel getFullEntityDm()
	{ return fullEntityDm; }
public MailingModel2 getMailingModel()
	{ return mailings; }
//public EQueryModel2 getEQueryModel2()
//	{ return equeries; }
public DbChangeModel getDbChange()
	{ return dbChange; }
public OffstageSchemaSet getSchemaSet() { return sset; }
public SwingerMap getSwingerMap() { return swingerMap; }
public SFormatterMap getSFormatterMap() { return sFormatterMap; }
public EQuerySchema getEquerySchema() { return equerySchema;}
// -------------------------------------------------
public int getScreen()
{ return screen; }
public void setScreen(int s)
{
	this.screen = s;
	fireScreenChanged();
}
// -------------------------------------------------
// ===================================================
public static interface Listener
{
	void screenChanged();
}
public static class Adapter implements Listener
{
	public void screenChanged() {}
}
// ===================================================
// ===================================================
// Listener code
public LinkedList listeners = new LinkedList();
public void addListener(Listener l)
{ listeners.add(l); }
public void fireScreenChanged()
{
for (Iterator ii = listeners.iterator(); ii.hasNext(); ) {
	Listener l = (Listener)ii.next();
	l.screenChanged();
}
}
// ===================================================

}
