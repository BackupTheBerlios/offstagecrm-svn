/*
 * Cleanse.java
 *
 * Created on November 4, 2007, 11:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.cleanse;

import citibob.jschema.*;
import citibob.multithread.*;
import citibob.sql.*;
import citibob.sql.pgsql.SqlInteger;
import java.util.*;
import java.sql.*;
import offstage.schema.*;
import citibob.jschema.log.*;
import offstage.db.*;
import offstage.*;
import citibob.app.*;
import citibob.sql.pgsql.*;

public class Merge
{

/** Merges data FROM dm0 TO dm1 */
public static void merge(FullEntityDbModel dm0, FullEntityDbModel dm1)
{
	mergePersons(dm0.getPersonSb(), dm1.getPersonSb());
}	

/** Merge main part of the record.. */
public static void mergePersons(SchemaBuf sb0, SchemaBuf sb1)
{
	mergeMain(sb0, sb1);
	int eidCol = sb0.findColumn("entityid");
	int pidCol = sb0.findColumn("primaryentityid");
	
	int eid1 = (Integer)sb1.getValueAt(0, eidCol);
	int pid1 = (Integer)sb1.getValueAt(0, pidCol);
	if (eid1 == pid1) {
		Integer Pid0 = (Integer)sb0.getValueAt(0, pidCol);
		sb1.setValueAt(Pid0, 0, pidCol);
	}
}

/** Merge main part of the record.. */
public static void mergeMain(SchemaBuf sb0, SchemaBuf sb1)
{
	for (int col=0; col < sb0.getColumnCount(); ++col) mergeCol(sb0, sb1, col);
}
/** Merge main part of the record.. */
public static void mergeCol(SchemaBuf sb0, SchemaBuf sb1, int col)
{
	Object val1 = sb1.getValueAt(0, col);
System.out.println(col + " val1 = " + val1);
	if (val1 == null) {
		Object val0 = sb0.getValueAt(0, col);
		sb1.setValueAt(val0, 0, col);
	}
}

}
