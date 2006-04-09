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
/*
 * EClauseTableTest.java
 *
 * Created on June 30, 2005, 9:44 PM
 */

package offstage.equery.swing;
import java.util.*;
import java.sql.*;
import citibob.sql.*;
import offstage.db.TestConnPool;
import offstage.equery.EQuery;
import offstage.equery.EQuerySchema;
import offstage.schema.*;

/**
 *
 * @author  citibob
 */
public class EClauseTableTest extends javax.swing.JFrame {

    /** Creates new form EClauseTableTest */
    public EClauseTableTest() throws SQLException {
		initComponents();

		Connection db = new TestConnPool().checkout();
		Statement st = db.createStatement();

		OffstageSchemaSet sset = new OffstageSchemaSet(st, null);
		EQuerySchema eqs = new EQuerySchema(st, sset);
		EQuery eq = new EQuery();
		eq.newClause("Clause 1");
		eq.addElement("phones", "phone", "=", "617-308-0436 zzz");
		eq.addElement("phones", "groupid", "=", new Integer(109));
		eq.newClause("Clause 2");
		eq.addElement("donations", "groupid", "=", new Integer(169));
		eq.newClause("Clause 3");
		eq.addElement("persons", "lastname", "like", "%Fischer%");

		//ArrayList cla = eq.getClause(0);
System.out.println("new EClauseTableModel");
		EClauseTableModel cm = new EClauseTableModel(eqs);
		EQueryTableModel qm = new EQueryTableModel(cm);
		eQueryEditor.initRuntime(qm,cm, new citibob.sql.pgsql.DefaultSwingerMap());
		qm.setQuery(eq);

		SqlQuery sql = new SqlQuery();
		eq.writeSqlQuery(eqs, sql);
		sql.addTable("entities as main");
		sql.addColumn("main.entityid");
		sql.setDistinct(true);
		String ssql = sql.getSelectSQL();
		System.out.println(ssql);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        eQueryEditor = new offstage.equery.swing.EQueryEditor();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(eQueryEditor, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) throws Exception
	{
		final EClauseTableTest test = new EClauseTableTest();



        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                test.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private offstage.equery.swing.EQueryEditor eQueryEditor;
    // End of variables declaration//GEN-END:variables
// =====================================================
}
