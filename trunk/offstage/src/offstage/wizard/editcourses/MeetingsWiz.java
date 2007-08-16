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
 * CoursesEditor.java
 *
 * Created on June 8, 2007, 10:08 PM
 */

package offstage.wizard.editcourses;

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

/**
 *
 * @author  citibob
 */
public class MeetingsWiz extends citibob.swing.JPanelWiz
{

IntKeyedDbModel meetingsSb;
FrontApp fapp;
int courseid;

	/** Creates new form CompleteStatusPanel */
	public MeetingsWiz(FrontApp xfapp, Statement xst, int termid, int courseid)
	throws SQLException
	{
		super("Edit Meetings");
		this.courseid = courseid;
		initComponents();
		this.fapp = xfapp;
		
		// Set up terms selector
		ResultSet rs;
		rs = xst.executeQuery("select name from termids where groupid = " + SqlInteger.sql(termid));	
		rs.next();
		term.setText(rs.getString(1));
		rs.close();
		rs = xst.executeQuery("select name from courseids where courseid = " + SqlInteger.sql(courseid));	
		rs.next();
		course.setText(rs.getString(1));
		rs.close();
		
//		term.setJType(new DbKeyedModel(xst, fapp.getDbChange(), "termids",
//			"select termid, name from termids where iscurrent order by firstdate"), "<none>");
//		course.setJType(new DbKeyedModel(xst, fapp.getDbChange(), "courseids",
//			"select courseid, name from termids where termid = " + termid + " order by name"), "<none>");
		
		// Set up courses editor
		meetingsSb = new IntKeyedDbModel(fapp.getSchema("meetings"),
			"courseid", fapp.getDbChange());
		meetingsSb.setKey(courseid);
		meetingsSb.setOrderClause("dtstart");
		meetings.setModelU(meetingsSb.getSchemaBuf(),
			new String[] {"Start", "End"},
			new String[] {"dtstart", "dtnext"},
			null, fapp.getSwingerMap());

		Swinger swing = new SqlTimestampSwinger("GMT", fapp.getTimeZone(), "EEE MMM dd HH:mm");
		meetings.setRenderEditU("dtstart", swing);
		meetings.setRenderEditU("dtnext", swing);
		
		meetingsSb.doSelect(xst);
	}
	
	/** After the Wiz is done running, report its output into a Map. */
	public void getAllValues(java.util.Map map)
	{
	}

	public void backPressed() { saveCur(); }
	public void nextPressed() { saveCur(); }

	public void saveCur()
	{
		fapp.runGui(MeetingsWiz.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			if (meetingsSb.valueChanged()) {
				meetingsSb.doUpdate(st);
				meetingsSb.doSelect(st);
			}
		}});
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
        bAdd = new javax.swing.JButton();
        bDel = new javax.swing.JButton();
        bRestore = new javax.swing.JButton();
        bSave = new javax.swing.JButton();
        bAutoFill = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        meetings = new citibob.jschema.swing.StatusTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        term = new citibob.swing.typed.JTypedLabel();
        jLabel2 = new javax.swing.JLabel();
        course = new citibob.swing.typed.JTypedLabel();

        setLayout(new java.awt.BorderLayout());

        bAdd.setText("Add");
        bAdd.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bAddActionPerformed(evt);
            }
        });

        jPanel1.add(bAdd);

        bDel.setText("Del");
        bDel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bDelActionPerformed(evt);
            }
        });

        jPanel1.add(bDel);

        bRestore.setText("Restore");
        bRestore.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bRestoreActionPerformed(evt);
            }
        });

        jPanel1.add(bRestore);

        bSave.setText("Save");
        bSave.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bSaveActionPerformed(evt);
            }
        });

        jPanel1.add(bSave);

        bAutoFill.setText("Auto Fill");
        bAutoFill.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bAutoFillActionPerformed(evt);
            }
        });

        jPanel1.add(bAutoFill);

        add(jPanel1, java.awt.BorderLayout.SOUTH);

        meetings.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(meetings);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Term: ");
        jPanel2.add(jLabel1, new java.awt.GridBagConstraints());

        term.setText("jTypedLabel1");
        jPanel2.add(term, new java.awt.GridBagConstraints());

        jLabel2.setText("   Course: ");
        jPanel2.add(jLabel2, new java.awt.GridBagConstraints());

        course.setText("jTypedLabel1");
        jPanel2.add(course, new java.awt.GridBagConstraints());

        add(jPanel2, java.awt.BorderLayout.NORTH);

    }// </editor-fold>//GEN-END:initComponents

	private void bAutoFillActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bAutoFillActionPerformed
	{//GEN-HEADEREND:event_bAutoFillActionPerformed
		fapp.runGui(MeetingsWiz.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			offstage.db.DB.w_meetings_autofill(st, courseid, fapp.getTimeZone());
			meetingsSb.doSelect(st);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bAutoFillActionPerformed
	
	private void bSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSaveActionPerformed
	{//GEN-HEADEREND:event_bSaveActionPerformed
		saveCur();
//		fapp.runGui(MeetingsWiz.this, new StRunnable() {
//		public void run(Statement st) throws Exception {
//			  meetingsSb.doUpdate(st);
//			  meetingsSb.doSelect(st);
//		  }});
// TODO add your handling code here:
	}//GEN-LAST:event_bSaveActionPerformed

	private void bRestoreActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bRestoreActionPerformed
	{//GEN-HEADEREND:event_bRestoreActionPerformed
		fapp.runGui(MeetingsWiz.this, new StRunnable() {
		public void run(Statement st) throws Exception
		  {
			  meetingsSb.doSelect(st);
		  }});
// TODO add your handling code here:
	}//GEN-LAST:event_bRestoreActionPerformed

	private void bDelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDelActionPerformed
	{//GEN-HEADEREND:event_bDelActionPerformed
		fapp.runGui(MeetingsWiz.this, new ERunnable()
		{ public void run() throws Exception
		  {
			  int selected = meetings.getSelectedRow();
			  if (selected != -1) {
				  meetingsSb.getSchemaBuf().deleteRow(selected);
			  }
		  }});
	}//GEN-LAST:event_bDelActionPerformed

	private void bAddActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bAddActionPerformed
	{//GEN-HEADEREND:event_bAddActionPerformed
		fapp.runGui(MeetingsWiz.this, new ERunnable()
		{ public void run() throws Exception
		  {
			  meetingsSb.getSchemaBuf().insertRow(-1);
		  }});
// TODO add your handling code here:
	}//GEN-LAST:event_bAddActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAdd;
    private javax.swing.JButton bAutoFill;
    private javax.swing.JButton bDel;
    private javax.swing.JButton bRestore;
    private javax.swing.JButton bSave;
    private citibob.swing.typed.JTypedLabel course;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private citibob.jschema.swing.StatusTable meetings;
    private citibob.swing.typed.JTypedLabel term;
    // End of variables declaration//GEN-END:variables
	
}
