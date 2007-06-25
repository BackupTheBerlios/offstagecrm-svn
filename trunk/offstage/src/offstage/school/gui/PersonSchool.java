/*
 * PersonSchool.java
 *
 * Created on June 11, 2007, 10:32 AM
 */

package offstage.school.gui;

import java.sql.*;
import citibob.jschema.*;
import citibob.jschema.swing.*;
import citibob.swing.table.*;
import citibob.swing.typed.*;
import citibob.multithread.*;
import citibob.jschema.swing.StatusTable;
import citibob.sql.*;
import citibob.app.*;
import offstage.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import citibob.util.*;
import java.util.*;
import citibob.sql.pgsql.*;
import citibob.swing.pgsql.*;
import citibob.sql.*;
import static citibob.jschema.JoinedSchemaBufDbModel.TableSpec;

/**
 *
 * @author  citibob
 */
public class PersonSchool extends javax.swing.JPanel
{

FrontApp fapp;
int entityid;
//int termid;
JoinedSchemaBufDbModel enrolledDb;

	/** Creates new form PersonSchool */
	public PersonSchool()
	{
		initComponents();
	}
	
public void initRuntime(FrontApp xfapp, Statement st, int entityid) throws SQLException
{
	this.fapp = xfapp;
	this.entityid = entityid;
	
	// Make sure we have a student record
	SchoolDB.w_students_create(st, entityid);
	
	// Read person info
	ResultSet rs = st.executeQuery("select * from persons where entityid = " + SqlInteger.sql(entityid));
	rs.next();
	TypedWidgetBinder.setValueRecursive(this, rs, fapp.getSwingerMap(), fapp.getSqlTypeSet());
	rs.close();
	
	// Set up terms selector
	terms.setKeyedModel(new DbKeyedModel(st, fapp.getDbChange(), "termids",
		"select termid, name from termids where iscurrent order by firstdate"));
	terms.addPropertyChangeListener("value", new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent evt) {
		fapp.runApp(new StRunnable() {
		public void run(Statement st) throws Exception {
			termChanged(st);
		}});
	}});


	// Set up enrollments table
	enrolledDb = new JoinedSchemaBufDbModel(null, new TableSpec[] {
			new TableSpec(fapp.getSchema("enrollments")),
			new TableSpec(fapp.getSchema("courseids"))
		});
//	enrolledDb.s
	enrolledDb.setOrderClause("courseids_dayofweek, courseids_tstart, courseids_name");
	enrollments.setModelU(enrolledDb.getTableModel(),
		new String[] {"Course", "Day", "Start", "Finish",
			"Role", "Custom Start", "Custom End (+1)", "Enrolled"},
		new String[] {"courseids_name", "courseids_dayofweek", "courseids_tstart", "courseids_tnext",
			"enrollments_courserole", "enrollments_dstart", "enrollments_dend", "enrollments_dtenrolled"},
		new boolean[] {false, false, false, false,
			true, true, true, false}, fapp.getSwingerMap());
	enrollments.setRenderEditU("courseids_dayofweek", new KeyedRenderEdit(new DayOfWeekKeyedModel()));
	enrolledDb.doSelect(st);
	
//	new JoinedSchemaBufDbModel(fapp.getDbChange(), specs);
//	terms.setSelectedIndex(0);		// Should throw a value changed event
	termChanged(st);
}

void termChanged(Statement st) throws SQLException
{
	String stermid = SqlInteger.sql((Integer)terms.getValue());
	// Populate student's enrollment for this term
	enrolledDb.setWhereClause("enrollments.courseid = courseids.courseid" +
		" and courseids.termid = " + stermid + " and enrollments.entityid = " + entityid);
	enrolledDb.doSelect(st);

	// Populate available courses for adding
	courselist.setKeyedModel(new DbKeyedModel(st, fapp.getDbChange(), "courseids",
		"select courseid, name from courseids where termid = " + stermid));
}

public void refreshEnroll(Statement st) throws SQLException
{
//	enrollSb.doUpdate(st);
//	coursesSb.setKey((Integer)terms.getValue());
//	coursesSb.doSelect(st);
}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        firstname = new citibob.swing.typed.JTypedLabel();
        lastname = new citibob.swing.typed.JTypedLabel();
        jLabel2 = new javax.swing.JLabel();
        lentityid = new citibob.swing.typed.JTypedLabel();
        jLabel3 = new javax.swing.JLabel();
        terms = new citibob.swing.typed.JKeyedComboBox();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        GroupScrollPanel = new javax.swing.JScrollPane();
        enrollments = new citibob.jschema.swing.StatusTable();
        controller = new javax.swing.JPanel();
        courselist = new citibob.swing.typed.JKeyedComboBox();
        jPanel5 = new javax.swing.JPanel();
        addEnrollment = new javax.swing.JButton();
        delEnrollment = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        bSave = new javax.swing.JButton();
        bUndo = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Person:");

        firstname.setText("FirstName");
        firstname.setColName("firstname");

        lastname.setText("jTypedLabel1");
        lastname.setColName("lastname");

        jLabel2.setText("ID:");

        lentityid.setText("jTypedLabel1");
        lentityid.setColName("entityid");

        jLabel3.setText("Term:");

        terms.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel1)
                        .add(5, 5, 5)
                        .add(firstname, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(5, 5, 5)
                        .add(lastname, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 134, Short.MAX_VALUE)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lentityid, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(terms, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(firstname, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lastname, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(lentityid, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel2)))
                .add(3, 3, 3)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(terms, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.BorderLayout());

        enrollments.setModel(new javax.swing.table.DefaultTableModel(
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
        GroupScrollPanel.setViewportView(enrollments);

        jPanel4.add(GroupScrollPanel, java.awt.BorderLayout.CENTER);

        controller.setLayout(new java.awt.BorderLayout());

        controller.add(courselist, java.awt.BorderLayout.CENTER);

        addEnrollment.setText("Add");
        addEnrollment.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                addEnrollmentActionPerformed(evt);
            }
        });

        jPanel5.add(addEnrollment);

        delEnrollment.setText("Del");
        delEnrollment.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                delEnrollmentActionPerformed(evt);
            }
        });

        jPanel5.add(delEnrollment);

        controller.add(jPanel5, java.awt.BorderLayout.EAST);

        jPanel4.add(controller, java.awt.BorderLayout.SOUTH);

        jLabel4.setText("Enrollments");
        jPanel4.add(jLabel4, java.awt.BorderLayout.NORTH);

        jPanel2.add(jPanel4, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        bSave.setText("Save");
        bSave.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bSaveActionPerformed(evt);
            }
        });

        jToolBar2.add(bSave);

        bUndo.setText("Undo");
        bUndo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bUndoActionPerformed(evt);
            }
        });

        jToolBar2.add(bUndo);

        jPanel3.add(jToolBar2, java.awt.BorderLayout.CENTER);

        add(jPanel3, java.awt.BorderLayout.SOUTH);

    }// </editor-fold>//GEN-END:initComponents

	private void delEnrollmentActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_delEnrollmentActionPerformed
	{//GEN-HEADEREND:event_delEnrollmentActionPerformed
		fapp.runGui(PersonSchool.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			String sql = "delete from enrollments" +
				" where entityid = " + SqlInteger.sql(entityid) +
				" and courseid = " + SqlInteger.sql((Integer)courselist.getValue());
			st.executeUpdate(sql);
			enrolledDb.doSelect(st);
		}});
	}//GEN-LAST:event_delEnrollmentActionPerformed

	private void addEnrollmentActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addEnrollmentActionPerformed
	{//GEN-HEADEREND:event_addEnrollmentActionPerformed
		fapp.runGui(PersonSchool.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			String sql = "insert into enrollments (entityid, courseid, courserole, dtenrolled)" +
				" values (" + SqlInteger.sql(entityid) + ", " +
				SqlInteger.sql((Integer)courselist.getValue()) +
				", (select courseroleid from courseroles where name = 'student')" + 
				", now())";
			st.executeUpdate(sql);
			enrolledDb.doSelect(st);
// TODO: Status col isn't showing due to lack of functionality for JoinedSchema...
// in StatusSchemaBuf and StatusTable
// This functionality is not yet implemented in JoinedSchemaBufDbModel...
//			SchemaBuf esb = enrolledDb.getSchemaBuf(0);
//			esb.insertRow(-1,
//				new String[] {"entityid", "courseid"},
//				new Object[] {entityid, courselist.getValue()});
		}});
	}//GEN-LAST:event_addEnrollmentActionPerformed

	private void bUndoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bUndoActionPerformed
	{//GEN-HEADEREND:event_bUndoActionPerformed
		fapp.runGui(PersonSchool.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			enrolledDb.doSelect(st);
		}});
	}//GEN-LAST:event_bUndoActionPerformed

	private void bSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSaveActionPerformed
	{//GEN-HEADEREND:event_bSaveActionPerformed
		fapp.runGui(PersonSchool.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			enrolledDb.doUpdate(st);
			enrolledDb.doSelect(st);
			
			int termid = (Integer)terms.getValue();
			SchoolDB.w_tuitiontrans_calcTuition(st, termid, entityid);
		}});
	}//GEN-LAST:event_bSaveActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane GroupScrollPanel;
    private javax.swing.JButton addEnrollment;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bUndo;
    private javax.swing.JPanel controller;
    private citibob.swing.typed.JKeyedComboBox courselist;
    private javax.swing.JButton delEnrollment;
    private citibob.jschema.swing.StatusTable enrollments;
    private citibob.swing.typed.JTypedLabel firstname;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JToolBar jToolBar2;
    private citibob.swing.typed.JTypedLabel lastname;
    private citibob.swing.typed.JTypedLabel lentityid;
    private citibob.swing.typed.JKeyedComboBox terms;
    // End of variables declaration//GEN-END:variables
	
}
