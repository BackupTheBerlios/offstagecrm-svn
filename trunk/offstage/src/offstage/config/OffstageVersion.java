/*
 * OffstageVersion.java
 *
 * Created on April 16, 2006, 5:36 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.config;

import citibob.version.*;
import java.util.prefs.*;
import java.util.*;
import java.sql.*;

/**
 *
 * @author citibob
 */
public class OffstageVersion {

//public final Version frontendVersion;
//public final Version dbVersion;
//public final Version prefVersion;
//
//// Converters from given version to actual version
//public final Version.Entry dbEntry;
//public final Version.Entry prefEntry;
//
//
//Version.Entry[] dbWorks = null;
//Version.Entry[] prefWorks = null;
//
//public final Preferences pref;
//public final Preferences guiPref;
//
///** Creates a new instance of OffstageVersion */
//public OffstageVersion(Statement st)
//throws SQLException, java.util.prefs.BackingStoreException
//{
//	// Version of this front-end
//	frontendVersion = new Version(0,3,0);
//
//	// Mappings of what database, etc. versions this front-end works with.
//	// Most-preferred mappings are first!
//	dbWorks = new Version.Entry[] {
//		new Version.Entry(new Version.Range(0,3,0), null)
//	};
//	prefWorks = new Version.Entry[] {
//		new Version.Entry(new Version.Range(0,3,0), null)
//	};
//
//	// Make sure the database is right version
//	dbVersion = new Version(st, "dbversion");
//	dbEntry = getEntry(dbWorks, dbVersion);
//
//	// Decide on version of preferences to use.
//	Preferences prefRoot = Preferences.userRoot();
//	prefRoot = prefRoot.node("offstage");
//	Version[] prefVersions = citibob.version.Version.getAvailablePrefVersions(prefRoot);
//	Arrays.sort(prefVersions);
//	boolean goodPref = false;
//	for (int i=prefVersions.length-1; ; --i) {	// search from largest
//		if (i < 0) break;
//		Version.Entry e = getEntry(prefWorks, prefVersions[i]);
//		if (e != null && e.converter == null) {
//			prefEntry = e;
//			prefVersion = prefVersions[i];
//			pref = prefRoot.node(prefVersion.toString());
//			guiPref = pref.node("gui");
//			goodPref = true;
//			break;
//		}
//	}
//}
//
///** Make sure the versions found are appropriate. */
//public void checkVersions() throws VersionException
//{
//	if (dbEntry == null || dbEntry.converter != null) throw new VersionException(
//		"Database version " + dbVersion + " not appropriate for front-end version " + frontendVersion);
//	
//	if (prefEntry == null || prefEntry.converter != null) throw new VersionException(
//		"No suitable preferences for front-end version " + frontendVersion + " found!");
//}
//
//
///** Gets the entry corresponding to v. */
//private static Version.Entry getEntry(Version.Entry[] e, Version v)
//{
//	for (int i=0; i<e.length; ++i) {
//		if (e[i].range.inRange(v)) return e[i];
//	}
//	return null;
//}


}
