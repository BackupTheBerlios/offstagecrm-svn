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
package offstage;
import java.sql.*;
import java.util.*;
import citibob.sql.*;
import citibob.multithread.*;
import offstage.db.FullEntityDbModel;
import offstage.db.EntityListTableModel;
import offstage.db.TestConnPool;
import citibob.multithread.*;
import citibob.sql.*;
import citibob.swing.typed.*;
import offstage.schema.*;
import citibob.mail.*;
import javax.mail.internet.*;
import offstage.equery.swing.EQueryModel2;

public class FrontApp
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
OffstageSchemaSet sset;

FullEntityDbModel fullEntityDm;
EQueryModel2 equeries;
MailingModel2 mailings;
EntityListTableModel simpleSearchResults;
ActionRunner guiRunner;		// Run user-initiated actions; when user hits button, etc.
	// This will put on queue, etc.
ActionRunner appRunner;		// Run secondary events, in response to other events.  Just run immediately
MailSender mailSender;	// Way to send mail (TODO: make this class MVC.)
// -------------------------------------------------------
public ConnPool getPool() { return pool; }
public ActionRunner getGuiRunner() { return guiRunner; }
public ActionRunner getAppRunner() { return appRunner; }
public MailSender getMailSender() { return mailSender; }
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
	this.swingerMap = new offstage.types.OffstageSwingerMap();
	
	this.pool = pool;
	//pool = new DBConnPool();
	MailSender sender = new GuiMailSender();
	ExpHandler expHandler = new MailExpHandler(sender,
			new InternetAddress("citibob@earthlink.net"), "OffstageCRM", stdoutDoc);
	guiRunner = appRunner = new SimpleDbActionRunner(pool, expHandler);
	//guiRunner = new SimpleDbActionRunner(pool);
	try {
		dbb = pool.checkout();
		st = dbb.createStatement();
	

		dbChange = new DbChangeModel();
		this.sset = new OffstageSchemaSet(st, dbChange);
		fullEntityDm = new FullEntityDbModel(sset, appRunner);
		mailings = new MailingModel2(st, sset);//, appRunner);
//	mailings.refreshMailingids();
		equeries = new EQueryModel2(st, mailings, sset);
		simpleSearchResults = new EntityListTableModel();
	} finally {
		st.close();
		pool.checkin(dbb);
	}
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
public EQueryModel2 getEQueryModel2()
	{ return equeries; }
public DbChangeModel getDbChange()
	{ return dbChange; }
public OffstageSchemaSet getSchemaSet() { return sset; }
public SwingerMap getSwingerMap() { return swingerMap; }
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
