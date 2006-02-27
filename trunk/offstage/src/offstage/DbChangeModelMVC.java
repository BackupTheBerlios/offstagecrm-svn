/*
JSchema: library for GUI-based database applications
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

/**  Notifications to changes in the data in the overall database.  This is important
for some pieces of data that end up in drop-downs and would need to be re-queried. */
public abstract class DbChangeModelMVC
{
public static interface Listener {
    /**  Something has changed in the terms table. */
    public void termsChanged(Statement st) throws SQLException;


    /**  Something has changed in the courses table. */
    public void coursesChanged(Statement st) throws SQLException;
}
// ======================================================
public static class Adapter implements DbChangeModelMVC.Listener {
    /**  Something has changed in the terms table. */
    public void termsChanged(Statement st) throws SQLException{}


    /**  Something has changed in the courses table. */
    public void coursesChanged(Statement st) throws SQLException{}
}
// ======================================================
java.util.LinkedList listeners = new java.util.LinkedList();
public void addListener(DbChangeModelMVC.Listener l)
	{ listeners.add(l); }
public void removeListener(DbChangeModelMVC.Listener l)
	{ listeners.remove(l); }

// ======================================================
public void fireTermsChanged(Statement st) throws SQLException
{
	for (java.util.Iterator ii=listeners.iterator(); ii.hasNext(); ) {
		DbChangeModelMVC.Listener l = (DbChangeModelMVC.Listener)ii.next();
		l.termsChanged(st);
	}
}
public void fireCoursesChanged(Statement st) throws SQLException
{
	for (java.util.Iterator ii=listeners.iterator(); ii.hasNext(); ) {
		DbChangeModelMVC.Listener l = (DbChangeModelMVC.Listener)ii.next();
		l.coursesChanged(st);
	}
}
}
