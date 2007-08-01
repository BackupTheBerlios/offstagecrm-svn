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
import javax.swing.JOptionPane;
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
import offstage.schema.*;
import citibob.wizard.*;
import offstage.accounts.gui.*;

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
IntKeyedDbModel entitiesSchoolDb;
SchemaBufRowModel entitiesSchoolRm;
IntKeyedDbModel actransDb;
int actypeid = ActransSchema.AC_SCHOOL;

	/** Creates new form PersonSchool */
	public PersonSchool()
	{
		initComponents();
	}


	
public void initRuntime(FrontApp xfapp, Statement st, int entityid) throws SQLException
{
	this.fapp = xfapp;
	this.entityid = entityid;
	

	// Make sure we have a school record
	SchoolDB.w_students_create(st, entityid);

	lAdult.initRuntime(fapp);

	// Set up transactions table
	actransDb = new IntKeyedDbModel(fapp.getSchema("actrans"), "entityid");
	actransDb.setOrderClause("dtime desc");
//	actransDb.setWhereClause("actypeid = (select actypeid from actypes where name = 'school')");
	actransDb.setWhereClause("actypeid = " + SqlInteger.sql(actypeid));
	trans.setModelU(actransDb.getSchemaBuf(),
		new String[] {"Type", "Date", "Amount", "Description"},
		new String[] {"tableoid", "dtime", "amount", "description"},
		new String[] {null, null, null, "description"},
		new boolean[] {false, false, false, false},
		fapp.getSwingerMap(), fapp.getSFormatterMap());
	
	// Bind widgets to the school record
	entitiesSchoolDb = new IntKeyedDbModel(fapp.getSchema("entities_school"), "entityid");
	entitiesSchoolDb.setKey(entityid);
	entitiesSchoolRm = new SchemaBufRowModel(entitiesSchoolDb.getSchemaBuf());
	entitiesSchoolRm.addColListener(entitiesSchoolRm.findColumn("adultid"),
	new citibob.swing.RowModel.ColAdapter() {
		public void valueChanged(final int col) {
			if (entitiesSchoolRm.get(col) == null) return;
			fapp.runApp(new StRunnable() {
			public void run(Statement st) throws Exception {
				adultidChanged(st, (Integer)entitiesSchoolRm.get(col));
			}});
		}
		public void curRowChanged(final int col) {
			if (entitiesSchoolRm.get(col) == null) return;
			fapp.runApp(new StRunnable() {
			public void run(Statement st) throws Exception {
				adultidChanged(st, (Integer)entitiesSchoolRm.get(col));
			}});
		}
	});
	TypedWidgetBinder.bindRecursive(this, entitiesSchoolRm, fapp.getSwingerMap());
	entitiesSchoolDb.doSelect(st);
	entitiesSchoolRm.setCurRow(0);	// Must be done after above doSelect() this is cumbersome.

	// Read person info
	ResultSet rs = st.executeQuery(
		"select p.* from persons p where entityid = " + SqlInteger.sql(entityid));
	rs.next();
	TypedWidgetBinder.setValueRecursive(this, rs, fapp.getSwingerMap(), fapp.getSqlTypeSet());
	rs.close();
	
//	// Read name of responsible adult
//	String adultName = SQL.readString(st,
//		"select ae.firstname + ae.lastname as adultname" +
//		" from entities_school es, entities ae" +
//		" where es.adultid = ae.entityid" +
//		" and entityid = " + SqlInteger.sql(entityid));
//	lAdultName.setText(adultName);
//
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
	
//	// Set up dropdown
//	ActransSchema schema = (ActransSchema)fapp.getSchema("actrans");
//	translist.setKeyedModel(schema.tableKmodel);
	
	
//	new JoinedSchemaBufDbModel(fapp.getDbChange(), specs);
//	terms.setSelectedIndex(0);		// Should throw a value changed event
	termChanged(st);
}

void adultidChanged(Statement st, int adultid) throws SQLException
{
	actransDb.setKey(adultid);
	actransDb.doSelect(st);

	// Set up account balance
	acbal.setJType(new JavaJType(Double.class),
		new FormatFormatter(java.text.NumberFormat.getCurrencyInstance()));
	acbal.setValue(new Double(offstage.db.DB.r_acct_balance(st, adultid, actypeid)));	
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
		" select courseid, c.name || ' (' || dw.shortname || ')'" +
		" from courseids c, daysofweek dw" +
		" where c.dayofweek = dw.javaid" +
		" and termid = " + stermid));


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
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel6 = new javax.swing.JPanel();
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
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        firstname = new citibob.swing.typed.JTypedLabel();
        lastname = new citibob.swing.typed.JTypedLabel();
        jLabel2 = new javax.swing.JLabel();
        lentityid = new citibob.swing.typed.JTypedLabel();
        jLabel3 = new javax.swing.JLabel();
        terms = new citibob.swing.typed.JKeyedComboBox();
        lSpace = new javax.swing.JLabel();
        programs = new citibob.swing.typed.JKeyedComboBox();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        bSave = new javax.swing.JButton();
        bUndo = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        GroupScrollPanel1 = new javax.swing.JScrollPane();
        trans = new citibob.jschema.swing.StatusTable();
        controller1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        bCash = new javax.swing.JButton();
        bCheck = new javax.swing.JButton();
        bCc = new javax.swing.JButton();
        bAdjust = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        acbal = new citibob.swing.typed.JTypedLabel();
        jLabel5 = new javax.swing.JLabel();
        lAdult = new offstage.gui.EntityIDLabel();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jPanel6.setLayout(new java.awt.BorderLayout());

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

        courselist.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                courselistActionPerformed(evt);
            }
        });

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

        jPanel2.add(jPanel4, java.awt.BorderLayout.NORTH);

        jPanel6.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Person: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        jPanel1.add(jLabel1, gridBagConstraints);

        firstname.setText("FirstName");
        firstname.setColName("firstname");
        jPanel1.add(firstname, new java.awt.GridBagConstraints());

        lastname.setText("jTypedLabel1");
        lastname.setColName("lastname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        jPanel1.add(lastname, gridBagConstraints);

        jLabel2.setText("ID: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jLabel2, gridBagConstraints);

        lentityid.setText("jTypedLabel1");
        lentityid.setColName("entityid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        jPanel1.add(lentityid, gridBagConstraints);

        jLabel3.setText("Term: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel3, gridBagConstraints);

        terms.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(terms, gridBagConstraints);

        lSpace.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel1.add(lSpace, gridBagConstraints);

        programs.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        programs.setColName("programid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(programs, gridBagConstraints);

        jLabel9.setText("Level: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(jLabel9, gridBagConstraints);

        jPanel6.add(jPanel1, java.awt.BorderLayout.NORTH);

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

        jPanel6.add(jPanel3, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setLeftComponent(jPanel6);

        jPanel7.setLayout(new java.awt.BorderLayout());

        trans.setModel(new javax.swing.table.DefaultTableModel(
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
        GroupScrollPanel1.setViewportView(trans);

        jPanel7.add(GroupScrollPanel1, java.awt.BorderLayout.CENTER);

        controller1.setLayout(new java.awt.BorderLayout());

        jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel8.setText("Add Transaction:");
        jPanel8.add(jLabel8);

        bCash.setText("Cash");
        bCash.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bCashActionPerformed(evt);
            }
        });

        jPanel8.add(bCash);

        bCheck.setText("Check");
        bCheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bCheckActionPerformed(evt);
            }
        });

        jPanel8.add(bCheck);

        bCc.setText("Credit Card");
        bCc.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bCcActionPerformed(evt);
            }
        });

        jPanel8.add(bCc);

        bAdjust.setText("Adjustment");
        bAdjust.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bAdjustActionPerformed(evt);
            }
        });

        jPanel8.add(bAdjust);

        controller1.add(jPanel8, java.awt.BorderLayout.CENTER);

        jPanel7.add(controller1, java.awt.BorderLayout.SOUTH);

        jPanel9.setLayout(new java.awt.GridBagLayout());

        jLabel6.setText("Account History");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(jLabel6, gridBagConstraints);

        jLabel7.setText("Balance: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel9.add(jLabel7, gridBagConstraints);

        acbal.setText("2500");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(acbal, gridBagConstraints);

        jLabel5.setText("Adult (payer): ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel9.add(jLabel5, gridBagConstraints);

        lAdult.setColName("adultid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel9.add(lAdult, gridBagConstraints);

        jPanel7.add(jPanel9, java.awt.BorderLayout.NORTH);

        jSplitPane1.setRightComponent(jPanel7);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents

	private void courselistActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_courselistActionPerformed
	{//GEN-HEADEREND:event_courselistActionPerformed
// TODO add your handling code here:
	}//GEN-LAST:event_courselistActionPerformed

	private void bAdjustActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bAdjustActionPerformed
	{//GEN-HEADEREND:event_bAdjustActionPerformed
		fapp.runGui(PersonSchool.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			Wizard wizard = new TransactionWizard(fapp, st, null, entityid, actypeid);
			wizard.runWizard("adjustment");
			actransDb.doSelect(st);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bAdjustActionPerformed

	private void bCcActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bCcActionPerformed
	{//GEN-HEADEREND:event_bCcActionPerformed
		fapp.runGui(PersonSchool.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			Wizard wizard = new TransactionWizard(fapp, st, null, entityid, actypeid);
			wizard.runWizard("ccpayment");
			actransDb.doSelect(st);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bCcActionPerformed

	private void bCheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bCheckActionPerformed
	{//GEN-HEADEREND:event_bCheckActionPerformed
		fapp.runGui(PersonSchool.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			Wizard wizard = new TransactionWizard(fapp, st, null, entityid, actypeid);
			wizard.runWizard("checkpayment");
			actransDb.doSelect(st);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bCheckActionPerformed

	private void bCashActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bCashActionPerformed
	{//GEN-HEADEREND:event_bCashActionPerformed
		fapp.runGui(PersonSchool.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			Wizard wizard = new TransactionWizard(fapp, st, null, entityid, actypeid);
			wizard.runWizard("cashpayment");
			actransDb.doSelect(st);
		}});
	}//GEN-LAST:event_bCashActionPerformed

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
			try {
				st.executeUpdate(sql);
			} catch(SQLException e) {
				String msg = "Error in enrollment.  Are you trying\n" +
					"to enroll someone in the same course twice?";
				JOptionPane.showMessageDialog(PersonSchool.this, msg);
			}
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
			int col = entitiesSchoolRm.findColumn("adultid");
			Integer Oldadultid = (Integer)entitiesSchoolRm.getOrigValue(col);
			Integer Adultid = (Integer)entitiesSchoolRm.get(col);

			entitiesSchoolDb.doUpdate(st);
			entitiesSchoolDb.doSelect(st);
			enrolledDb.doUpdate(st);
			enrolledDb.doSelect(st);

			actransDb.doUpdate(st);			
			int termid = (Integer)terms.getValue();
//			SchoolDB.w_tuitiontrans_calcTuition(st, termid, entityid);
			if (Oldadultid != null) SchoolDB.w_tuitiontrans_calcTuitionByAdult(st, termid, Oldadultid);
			if (Adultid != null) SchoolDB.w_tuitiontrans_calcTuitionByAdult(st, termid, Adultid);
			actransDb.doSelect(st);
		}});
	}//GEN-LAST:event_bSaveActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane GroupScrollPanel;
    private javax.swing.JScrollPane GroupScrollPanel1;
    private citibob.swing.typed.JTypedLabel acbal;
    private javax.swing.JButton addEnrollment;
    private javax.swing.JButton bAdjust;
    private javax.swing.JButton bCash;
    private javax.swing.JButton bCc;
    private javax.swing.JButton bCheck;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bUndo;
    private javax.swing.JPanel controller;
    private javax.swing.JPanel controller1;
    private citibob.swing.typed.JKeyedComboBox courselist;
    private javax.swing.JButton delEnrollment;
    private citibob.jschema.swing.StatusTable enrollments;
    private citibob.swing.typed.JTypedLabel firstname;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar2;
    private offstage.gui.EntityIDLabel lAdult;
    private javax.swing.JLabel lSpace;
    private citibob.swing.typed.JTypedLabel lastname;
    private citibob.swing.typed.JTypedLabel lentityid;
    private citibob.swing.typed.JKeyedComboBox programs;
    private citibob.swing.typed.JKeyedComboBox terms;
    private citibob.jschema.swing.StatusTable trans;
    // End of variables declaration//GEN-END:variables
	
}
