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
/*
 * TermsDbModel.java
 *
 * Created on January 23, 2006, 4:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage;

import citibob.sql.*;
import citibob.jschema.*;
import java.sql.*;
import java.io.*;
import net.sf.jasperreports.engine.*;
import java.util.*;
import javax.swing.event.*;
import offstage.db.DB;
import offstage.schema.*;
import citibob.sql.DbChangeModel;

/**
 * No setKey() in this class; it just displays terms for past 2 years.
 * @author citibob
 */
public class CourseidsDbModel extends IntKeyedDbModel
{
DbChangeModel dbChange;

/** Creates a new instance of TermsDbModel */
public CourseidsDbModel(DbChangeModel dbChange)
throws java.sql.SQLException
{
	super(new CourseidsSchema(), "termid");
	//this.st = st;
	this.dbChange = dbChange;
}

public void doUpdate(Statement st) throws SQLException
{
	super.doUpdate(st);
	dbChange.fireCoursesChanged(st);
}

}
