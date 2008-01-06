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
package offstage;
import java.sql.*;
import java.util.*;
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
import offstage.db.*;
import citibob.jschema.log.*;
import citibob.swing.prefs.*;
import java.io.*;
import offstage.crypt.*;
import citibob.gui.*;

public class FrontApp extends citibob.app.App
{


public static final int ACTIONS_SCREEN = 0;
public static final int PEOPLE_SCREEN = 1;
//public static final int SCHOOL_SCREEN = 2;
public static final int MAILINGS_SCREEN = 2;
int screen = PEOPLE_SCREEN;

final File configDir;
/** Connection to our SQL database. */
//Connection db;
Properties props;
KeyRing keyRing;
DbChangeModel dbChange;
ConnPool pool;
Stack<SqlBatchSet> batchSets = new Stack();
//SqlBatchSet batchSet;
SwingerMap swingerMap;
//SFormatMap sFormatterMap;
OffstageSchemaSet sset;
EQuerySchema equerySchema;
citibob.reports.Reports reports;
FrameSet frameSet;

//FullEntityDbModel fullEntityDm;
//EQueryModel2 equeries;
//MailingModel2 mailings;
//EntityListTableModel simpleSearchResults;
SwingActionRunner guiRunner;		// Run user-initiated actions; when user hits button, etc.
	// This will put on queue, etc.
ActionRunner appRunner;		// Run secondary events, in response to other events.  Just run immediately
MailSender mailSender;	// Way to send mail (TODO: make this class MVC.)
SqlTypeSet sqlTypeSet;		// Conversion between SQL types and SqlType objects
ExpHandler expHandler;

int loginID;			// entityID of logged-in database application user
TreeSet<String> loginGroups;	// Groups to which logged-in user belongs (by name)
citibob.jschema.log.QueryLogger logger;			// Log all changes to database
SwingPrefs swingPrefs = new SwingPrefs();

/** TODO: This is temporary. */
public static final TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
public static final SqlDate sqlDate = new SqlDate(timeZone, true);	// Used for on-the-fly Sql creation
public static final SqlTimestamp sqlTimestamp = new SqlTimestamp("GMT", true);
//public static final TimeZone timeZone = TimeZone.getTimeZone("US/Pacific");
//public static final TimeZone timeZone = TimeZone.getTimeZone("Americas/Chicago");
// -------------------------------------------------------
public Properties getProps() { return props; }
public KeyRing getKeyRing() { return keyRing; }
public TimeZone getTimeZone() { return timeZone; }

public SwingPrefs getSwingPrefs() { return swingPrefs; }
public QueryLogger getLogger() { return logger; }
public int getLoginID() { return loginID; }
public ConnPool getPool() { return pool; }
public SqlBatchSet getBatchSet() { return batchSets.peek(); }
public void pushBatchSet()
{
	SqlBatchSet bs = new SqlBatchSet(pool);
	batchSets.push(bs);
}
public void popBatchSet() throws Exception
{
	SqlBatchSet bs = batchSets.pop();
	bs.runBatches();
}
public ExpHandler getExpHandler() { return expHandler; }
public File getConfigDir() { return configDir; }
public void runGui(java.awt.Component c, CBRunnable r) { guiRunner.doRun(c, r); }
/** Only runs the action if logged-in user is a member of the correct group.
 TODO: This functionality should be maybe in the ActionRunner? */
public void runGui(java.awt.Component c, String group, CBRunnable r) {
	runGui(c,r);
//	if (loginGroups.contains(group)) {
//		runGui(c,r);
//	} else {
//		javax.swing.JOptionPane.showMessageDialog(c, "You are not authorized for that action.");
//	}
}
public void runGui(java.awt.Component c, String[] groups, CBRunnable r)
{
	runGui(c,r);
	
//	if (groups == null) {
//		runGui(c, r);
//		return;
//	}
//	for (String g : groups) {
//		if (loginGroups.contains(g)) {
//			runGui(c,r);
//			return;
//		}
//	}
//	javax.swing.JOptionPane.showMessageDialog(c, "You are not authorized for that action.");
}

//public void runGui(CBRunnable r) { guiRunner.doRun(null, r); }
public void runApp(CBRunnable r) { appRunner.doRun(r); }
public MailSender getMailSender() { return mailSender; }
public Schema getSchema(String name) { return sset.get(name); }
public FrameSet getFrameSet() { return frameSet; }
public citibob.sql.SqlTypeSet getSqlTypeSet() { return sqlTypeSet; }
public citibob.reports.Reports getReports() { return reports; }

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

InputStream openPropFile(String name) throws IOException
{
	// First: try loading external file
//	File dir = new File(System.getProperty("user.dir"), "config");
	File f = new File(configDir, name);
	if (f.exists()) return new FileInputStream(f);

	// File doesn't exist; read from inside JAR file instead.
	Class klass = offstage.config.OffstageVersion.class;
	String resourceName = klass.getPackage().getName().replace('.', '/') + "/" + name;
	return klass.getClassLoader().getResourceAsStream(resourceName);

}
Properties loadProps() throws IOException
{
	Properties props;

	props = new Properties();
	InputStream in = openPropFile("app.properties");
	props.load(in);


//	props = new Properties(props);
//	props.load(openPropFile("site.properties"));

	props = new Properties(props);
	String os = System.getProperty("os.name");
        int space = os.indexOf(' ');
        if (space >= 0) os = os.substring(0,space);
	InputStream inn = openPropFile(os + ".properties");
	if (inn != null) props.load(inn);

	return props;
}
// -------------------------------------------------------
public FrontApp(ConnPool pool)
throws Exception
//SQLException, java.io.IOException, javax.mail.internet.AddressException,
//java.security.GeneralSecurityException
{
	frameSet = new offstage.gui.OffstageFrameSet(this);
	ConsoleFrame consoleFrame = (ConsoleFrame)frameSet.getFrame("console");
	configDir = new File(System.getProperty("user.dir"), "config");
	props = loadProps();

	// Load the crypto keys
//	File userDir = new File(System.getProperty("user.dir"));
//	File pubDir = new File(userDir, props.getProperty("crypt.pubdir"));
	String pubLeaf = props.getProperty("crypt.pubdir");
	File pubDir = (pubLeaf.charAt(0) == File.separatorChar ?
		new File(pubLeaf) : new File(configDir, pubLeaf)); 

	String privLeaf = props.getProperty("crypt.privdir");
	File privDir = (privLeaf.charAt(0) == File.separatorChar ?
		new File(privLeaf) : new File(configDir, privLeaf)); 
	keyRing = new KeyRing(pubDir, privDir);
	if (!keyRing.pubKeyLoaded()) {
		javax.swing.JOptionPane.showMessageDialog(null,
			"The public key failed to load.\n" +
			"You will be unable to enter credit card details.");
	}

	this.mailSender = new citibob.mail.GuiMailSender();
//	this.swingerMap = new citibob.sql.pgsql.SqlSwingerMap();
	this.sqlTypeSet = new citibob.sql.pgsql.PgsqlTypeSet();
	this.swingerMap = new offstage.types.OffstageSwingerMap(getTimeZone());
//	this.sFormatterMap = new offstage.types.OffstageSFormatMap();
	
	this.pool = pool;
	this.batchSets = new Stack();
	pushBatchSet();
	// ================
	SqlBatchSet str = new SqlBatchSet(pool);
	//pool = new DBConnPool();
	MailSender sender = new GuiMailSender();
	expHandler = new MailExpHandler(sender,
			new InternetAddress("citibob@comcast.net"), "OffstageArts", consoleFrame.getDocument());
	guiRunner = new BusybeeDbActionRunner(this, expHandler);
	appRunner = new SimpleDbActionRunner(this, expHandler);
	//guiRunner = new SimpleDbActionRunner(pool);
	
	// Figure out who we're logged in as
	String sql = "select entityid from dblogins where username = " +
		SqlString.sql(System.getProperty("user.name"));
	str.execSql(sql, new RsRunnable() {
	public void run(SqlRunner str, ResultSet rs) throws SQLException {
		if (rs.next()) {
			loginID = rs.getInt("entityid");
		} else {
			loginID = -1;
		}
		rs.close();
	}});

	// Figure out what groups we belong to (for action permissions)
	loginGroups = new TreeSet();
	sql = " select distinct name from dblogingroups g, dblogingroupids gid" +
		" where g.entityid=" + SqlInteger.sql(loginID) +
		" and g.groupid = gid.groupid";
	str.execSql(sql, new RsRunnable() {
	public void run(SqlRunner str, ResultSet rs) throws SQLException {
		while (rs.next()) loginGroups.add(rs.getString("name"));
		rs.close();
	}});
	
	dbChange = new DbChangeModel();
	this.sset = new OffstageSchemaSet(str, dbChange, getTimeZone());
	str.runBatches();		// Our SchemaSet must be set up before we go on.
	// ================
	
	// ================
	str = new SqlBatchSet(pool);
	logger = new OffstageQueryLogger(getAppRunner(), getLoginID());	
//	fullEntityDm = new FullEntityDbModel(this);
//	mailings = new MailingModel2(str, this);//, appRunner);

//	mailings.refreshMailingids();
//		equeries = new EQueryModel2(st, mailings, sset);
//	simpleSearchResults = new EntityListTableModel(this.getSqlTypeSet());
	
	equerySchema = new EQuerySchema(getSchemaSet());
	str.runBatches();
	// ================
	
	reports = new offstage.reports.OffstageReports(this);
}
//public EntityListTableModel getSimpleSearchResults()
//	{ return simpleSearchResults; }
//public Statement createStatement() throws java.sql.SQLException
//	{ return db.createStatement(); }

//public Connection getDb()
//	{ return db; }

// ------------------------------------
//public FullEntityDbModel getFullEntityDm()
//	{ return fullEntityDm; }
//public MailingModel2 getMailingModel()
//	{ return mailings; }
//public EQueryModel2 getEQueryModel2()
//	{ return equeries; }
public DbChangeModel getDbChange()
	{ return dbChange; }
public OffstageSchemaSet getSchemaSet() { return sset; }
public SwingerMap getSwingerMap() { return swingerMap; }
public SFormatMap getSFormatMap() { return (SFormatMap)swingerMap; }
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
