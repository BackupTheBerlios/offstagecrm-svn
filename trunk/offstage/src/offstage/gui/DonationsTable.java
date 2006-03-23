/*
 * DonationsTable.java
 *
 * Created on March 12, 2006, 9:13 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.gui;

import javax.swing.table.*;
import javax.swing.*;
import javax.swing.event.*;
import java.sql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import citibob.swing.typed.*;
import citibob.swing.*;
import offstage.schema.GroupTypeKeyedModel;

/**
 *
 * @author citibob
 */
public class DonationsTable extends GroupsTable {

    /** Creates a new instance of DonationsTable */
    public DonationsTable() {
    }

//public void initRuntime(Statement st, SchemaBuf sb)
//throws java.sql.SQLException
//{
//	super.initRuntime(st, sb);
//	String fmt = "yyyy-MM-dd";
//	ColPermuteTableModel model = (ColPermuteTableModel)getModel();
//	setRenderEdit(model.findColumnU("date"), new DateRenderEdit(fmt));
//
//}
}
