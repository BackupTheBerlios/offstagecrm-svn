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
import citibob.sql.DbChangeModel;

public class FrontApp
{

public static final int PEOPLE_SCREEN = 0;
public static final int QUERIES_SCREEN = 1;
public static final int MAILINGS_SCREEN = 2;
int screen = PEOPLE_SCREEN;


/** Connection to our SQL database. */
//Connection db;
DbChangeModel dbChange;
ConnPool pool;

FullEntityDbModel fullEntityDm;
EQueryBrowserApp equeryBrowserApp;
MailingModel mailings;
EntityListTableModel simpleSearchResults;
ActionRunner guiRunner;		// Run user-initiated actions; when user hits button, etc.
	// This will put on queue, etc.
ActionRunner appRunner;		// Run secondary events, in response to other events.  Just run immediately
// -------------------------------------------------------
public ConnPool getPool() { return pool; }
public ActionRunner getGuiRunner() { return guiRunner; }
public ActionRunner getAppRunner() { return appRunner; }
//public Connection createConnection()
//throws SQLException
//{
//	return DBConnection.getConnection();
//}
// -------------------------------------------------------
public FrontApp(ConnPool pool) throws SQLException
{
	Connection dbb = null;
	Statement st = null;

	this.pool = pool;
	//pool = new DBConnPool();
	guiRunner = appRunner = new SimpleDbActionRunner(pool);
	//guiRunner = new SimpleDbActionRunner(pool);
	try {
		dbb = pool.checkout();
		st = dbb.createStatement();
	

		dbChange = new DbChangeModel();
		fullEntityDm = new FullEntityDbModel(appRunner);
		mailings = new MailingModel(st, appRunner);
//	mailings.refreshMailingids();
		equeryBrowserApp = new EQueryBrowserApp(st, mailings);
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
public MailingModel getMailingModel()
	{ return mailings; }
public EQueryBrowserApp getEqueryBrowserApp()
	{ return equeryBrowserApp; }
public DbChangeModel getDbChange()
	{ return dbChange; }
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
