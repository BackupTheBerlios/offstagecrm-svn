/*
 * DonorReport.java
 *
 * Created on February 10, 2007, 9:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import offstage.*;
import citibob.app.*;
import java.sql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import citibob.multithread.*;
import static citibob.jschema.JoinedSchemaBufDbModel.TableSpec;
import offstage.gui.MultiSBTest;


/**
 
 *
 * @author citibob
 */
public class DonorReport extends JoinedSchemaBufDbModel
{
	
	/** Creates a new instance of DonorReport */
	public DonorReport(App app, String entityidSql)
	{
		super(null, new TableSpec[] {
			new TableSpec(app.getSchema("persons"), null),
//			new TableSpec(app.getSchema("phones")),
			new TableSpec
		});
		
		mdm.setWhereClause("persons.entityid = phones.entityid and persons.lastname like '%Fisch%'");
		
		jTypeTable1.setSwingerMap(app.getSwingerMap());
		jTypeTable1.setModel(mdm.newJTypeTableModel());
		mdm.doSelect(st);
	}
	
}
