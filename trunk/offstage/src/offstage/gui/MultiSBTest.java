/*
 * MultiSBTest.java
 *
 * Created on February 4, 2007, 4:34 PM
 */

package offstage.gui;

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
 * @MultiSBTestibob
 */
public class MultiSBTest extends javax.swing.JFrame
{
App app;
JoinedSchemaBufDbModel mdm;

	/** CreatesMultiSBTestultiSBTest */
	public MultiSBTest()
	{
		initComponents();
	}

	public void initRuntime(App app, Statement st) throws SQLException
	{
		this.app = app;
//		SchemaBuf persons = new SchemaBuf(app.getSchema("persons"));
//		SchemaBuf phones = new SchemaBuf(app.getSchema("phones"));
		

		mdm = new JoinedSchemaBufDbModel(null, new TableSpec[] {
			new TableSpec(app.getSchema("persons")),
			new TableSpec(app.getSchema("phones"))
		});
		
		mdm.setWhereClause("persons.entityid = phones.entityid and persons.lastname like '%Fisch%'");
		
		jTypeTable1.setSwingerMap(app.getSwingerMap());
		jTypeTable1.setModel(mdm.newJTypeTableModel());
		mdm.doSelect(st);
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        jScrollPane1 = new javax.swing.JScrollPane();
        jTypeTable1 = new citibob.swing.JTypeTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        jTypeTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTypeTable1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jButton1.setText("Update");
        jButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton1ActionPerformed(evt);
            }
        });

        getContentPane().add(jButton1, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
	{//GEN-HEADEREND:event_jButton1ActionPerformed
	app.runGui(new StRunnable() {
	public void run(Statement st) throws Exception {
		mdm.doUpdate(st);
	}});

// TODO add your handling code here:
	}//GEN-LAST:event_jButton1ActionPerformed
	
//	/**
//	 * @param args the command line arguments
//	 */
//	public static void main(String args[])
//	{
//		java.awt.EventQueue.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				new MultiSBTest().setVisible(true);
//			}
//		});
//	}
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private citibob.swing.JTypeTable jTypeTable1;
    // End of variables declaration//GEN-END:variables
	
public static void main(String[] args) throws Exception
{
	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
	Statement st = pool.checkout().createStatement();
	FrontApp fapp = new FrontApp(pool,null);
	
	MultiSBTest frame = new MultiSBTest();
	frame.initRuntime(fapp, st);
	frame.setVisible(true);
}

}
