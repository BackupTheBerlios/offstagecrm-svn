/*
 * SchoolPanel.java
 *
 * Created on August 9, 2007, 11:41 AM
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
import static citibob.swing.typed.TypedWidgetBinder.*;
import offstage.schema.*;
import citibob.wizard.*;
import offstage.accounts.gui.*;
import javax.swing.*;
import citibob.swing.*;

/**
 *
 * @author  citibob
 */
public class SchoolPanel extends javax.swing.JPanel
{

FrontApp fapp;

StudentDbModel studentDm;
SchemaBufRowModel studentRm;
PayerDbModel payerDm;
HouseholdDbModel householdDm;

IntKeyedDbModel actransDb;

MultiDbModel all = new MultiDbModel() {
	public void doSelect(Statement st) throws java.sql.SQLException
	{
		studentDm.doSelect(st);
		Integer pid = (Integer)studentRm.get("primaryentityid");
		familyTable.setPrimaryEntityID(st, pid);
		payerDm.setKey(studentDm.getAdultID());
		payerDm.doSelect(st);
		householdDm.setKey(pid);
		householdDm.doSelect(st);
	}
	public void setKey(int entityid)
	{
		intKey = entityid;
		studentDm.setKey(new Integer[] {entityid});
	}
};

/** Creates new form SchoolPanel */
public SchoolPanel()
{
	initComponents();
}

public void initRuntime(FrontApp xfapp, Statement st) throws SQLException
{
	this.fapp = xfapp;

	all.add(studentDm = new StudentDbModel(fapp));
	all.add(payerDm = new PayerDbModel(fapp));
	all.add(householdDm = new HouseholdDbModel(fapp));
	SwingerMap smap = fapp.getSwingerMap();
	
	// ================================================================
	// Student
	// Display student info from persons table
	studentRm = new SchemaBufRowModel(studentDm.personDb.getSchemaBuf());
	new TypedWidgetBinder().bind(lEntityID, studentRm, smap);
	vHouseholdID.initRuntime(fapp);
		new TypedWidgetBinder().bind(vHouseholdID, studentRm, smap);
	vStudentID.initRuntime(fapp);
		new TypedWidgetBinder().bind(vStudentID, studentRm, smap);
	KeyedModel gmodel = new KeyedModel();
		gmodel.addItem(null, "<Unknown>");
		gmodel.addItem("M", "Male");
		gmodel.addItem("F", "Female");
		gender.setKeyedModel(gmodel);
		new TypedWidgetBinder().bind(gender, studentRm, "gender", BT_READWRITE);
	familyTable.initRuntime(fapp);
		new TypedWidgetBinder().bind(familyTable, studentRm, "primaryentityid", BT_READ);
	TypedWidgetBinder.bindRecursive(StudentTab, studentRm, smap);
	
	// Initialize dropdowns when student changes.
	studentRm.addColListener("entityid", new RowModel.ColListener() {
		// Do nothing when user just changes values; it must be saved first.
		public void valueChanged(int col) {}
		// Do something when user moves to a different student
		public void curRowChanged(final int col) {
			fapp.runApp(new StRunnable() {
			public void run(Statement st) throws Exception {
				Integer ID = (Integer)studentRm.get(col);
				if (ID == null) return;
				String lastname = (String)studentRm.get(studentRm.findColumn("lastname"));
				vPayerID.setSearch(st, lastname);
				vHouseholdID.setSearch(st, lastname);
			}});
		}
	});

	// Change person when user clicks on family...
	familyTable.addPropertyChangeListener("value", new PropertyChangeListener() {
	public void propertyChange(final PropertyChangeEvent evt) {
		fapp.runGui(SchoolPanel.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			Integer EntityID = (Integer)evt.getNewValue();
			if (EntityID == null) return;
			changeStudent(st, EntityID);
		}});
	}});

	
	// Display student info from entities_school table
	final SchemaBufRowModel schoolRm = new SchemaBufRowModel(studentDm.schoolDb.getSchemaBuf());
	vPayerID.initRuntime(fapp);
		new TypedWidgetBinder().bind(vPayerID, schoolRm, smap);
	TypedWidgetBinder.bindRecursive(StudentTab, schoolRm, smap);

	// ================================================================
	// Payer
	SchemaBufRowModel payerRm = new SchemaBufRowModel(payerDm.personDb.getSchemaBuf());
	TypedWidgetBinder.bindRecursive(PayerPanel, payerRm, smap);
	payerPhonePanel.initRuntime(st, payerDm.phoneDb.getSchemaBuf(),
			new String[] {"Type", "Number"},
			new String[] {"groupid", "phone"}, smap);
	payerCCInfo.initRuntime(fapp.getKeyRing());
		
	// ================================================================
	// Account Transactions
	actransDb = new IntKeyedDbModel(fapp.getSchema("actrans"), "entityid");
	actransDb.setWhereClause(
		" actypeid = " + SqlInteger.sql(ActransSchema.AC_SCHOOL) +
		" and now()-dtime < 450");
	actransDb.setOrderClause("dtime desc");
	trans.setModelU(actransDb.getSchemaBuf(),
		new String[] {"Type", "Date", "Amount", "Description"},
		new String[] {"tableoid", "dtime", "amount", "description"},
		new String[] {null, null, null, "description"},
		new boolean[] {false, false, false, false},
		fapp.getSwingerMap(), fapp.getSFormatterMap());

	// Refresh account when payer changes
	schoolRm.addColListener("adultid", new RowModel.ColListener() {
		// Do nothing when user just changes values; it must be saved first.
		public void valueChanged(int col) {}
		// Do something when user saves and re-loads
		public void curRowChanged(final int col) {
			fapp.runApp(new StRunnable() {
			public void run(Statement st) throws Exception {
				Integer ID = (Integer)schoolRm.get(col);
				if (ID == null) return;
				changeAccount(st, ID);
			}});
		}
	});
	
	// ================================================================
	// Household
	SchemaBufRowModel householdRm = new SchemaBufRowModel(householdDm.personDb.getSchemaBuf());
	TypedWidgetBinder.bindRecursive(HouseholdPanel, householdRm, smap);
	householdPhonePanel.initRuntime(st, householdDm.phoneDb.getSchemaBuf(),
			new String[] {"Type", "Number"},
			new String[] {"groupid", "phone"}, smap);
	
	
	// ================================================================
	// Global Stuff
	// Edit another student
	searchBox.initRuntime(fapp);
	searchBox.addPropertyChangeListener("value", new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent evt) {
		fapp.runApp(new StRunnable() {
		public void run(Statement st) throws Exception {
			Integer EntityID = (Integer)searchBox.getValue();
			if (EntityID == null) return;
			int entityid = EntityID;
			changeStudent(st, entityid);
		}});
	}});

	// Set up terms selector
	vTermID.setKeyedModel(new DbKeyedModel(st, fapp.getDbChange(), "termids",
		"select termid, name from termids where iscurrent order by firstdate desc"));
	vTermID.addPropertyChangeListener("value", new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent evt) {
		fapp.runApp(new StRunnable() {
		public void run(Statement st) throws Exception {
			termChanged(st);
		}});
	}});

	changeStudent(st, 12633);
//	all.setKey(new Integer(12633));
	all.doSelect(st);
	
}

public void changeStudent(Statement st, int entityid) throws SQLException
{
	// Make sure person has record in school system
	if (!SchoolDB.isInSchool(st, entityid)) {
		if (JOptionPane.showConfirmDialog(SchoolPanel.this,
			"The person you selected is not yet\n" +
			"in the school system.\n" +
			"Should that person be added now?",
			"Person Not in School", JOptionPane.YES_NO_OPTION)
			== JOptionPane.NO_OPTION) return;
		SchoolDB.w_students_create(st, entityid);
//		String sql =
//			" insert into entities_school (entityid)" +
//			" values (" + SqlInteger.sql(entityid) + ")";
//		st.executeUpdate(sql);
	}

	// Go to that record
	vHouseholdID.setEntityID(entityid);	// So it knows when we try to emancipate.
	all.setKey(entityid);
	all.doSelect(st);
}

public void changeAccount(Statement st, int payerid) throws SQLException
{
	actransDb.setKey(payerid);
	actransDb.doSelect(st);
}

void termChanged(Statement st)
{
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

        jPanel1 = new javax.swing.JPanel();
        vTermID = new citibob.swing.typed.JKeyedComboBox();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        PeopleHeader = new javax.swing.JPanel();
        searchBox = new offstage.swing.typed.EntitySelector();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        vPayerID = new offstage.swing.typed.EntityIDEditableLabel();
        jToolBar1 = new javax.swing.JToolBar();
        bSave = new javax.swing.JButton();
        bUndo = new javax.swing.JButton();
        vStudentID = new offstage.swing.typed.EntityIDLabel();
        lEntityID = new citibob.swing.typed.JTypedLabel();
        vHouseholdID = new offstage.swing.typed.HouseholdIDEditableLabel();
        bEmancipate = new javax.swing.JButton();
        PeopleMain = new javax.swing.JPanel();
        AdultTabs = new javax.swing.JTabbedPane();
        PayerPanel = new javax.swing.JPanel();
        FirstMiddleLast = new javax.swing.JPanel();
        lFirst = new javax.swing.JLabel();
        lMiddle = new javax.swing.JLabel();
        lLast = new javax.swing.JLabel();
        salutation = new citibob.swing.typed.JTypedTextField();
        firstname = new citibob.swing.typed.JTypedTextField();
        middlename = new citibob.swing.typed.JTypedTextField();
        lastname = new citibob.swing.typed.JTypedTextField();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        payerPhonePanel = new offstage.gui.GroupPanel();
        jPanel5 = new javax.swing.JPanel();
        payerCCInfo = new offstage.swing.typed.CryptCCInfo();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        entityid = new citibob.swing.typed.JTypedTextField();
        lastupdated = new citibob.swing.typed.JTypedTextField();
        addressPanel = new javax.swing.JPanel();
        address1 = new citibob.swing.typed.JTypedTextField();
        address2 = new citibob.swing.typed.JTypedTextField();
        city = new citibob.swing.typed.JTypedTextField();
        state = new citibob.swing.typed.JTypedTextField();
        zip = new citibob.swing.typed.JTypedTextField();
        jLabel6 = new javax.swing.JLabel();
        EmailPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        email1 = new citibob.swing.typed.JTypedTextField();
        bLaunchEmail = new javax.swing.JButton();
        HouseholdPanel = new javax.swing.JPanel();
        FirstMiddleLast1 = new javax.swing.JPanel();
        lFirst1 = new javax.swing.JLabel();
        lMiddle1 = new javax.swing.JLabel();
        lLast1 = new javax.swing.JLabel();
        salutation1 = new citibob.swing.typed.JTypedTextField();
        firstname1 = new citibob.swing.typed.JTypedTextField();
        middlename1 = new citibob.swing.typed.JTypedTextField();
        lastname1 = new citibob.swing.typed.JTypedTextField();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel8 = new javax.swing.JPanel();
        householdPhonePanel = new offstage.gui.GroupPanel();
        jPanel9 = new javax.swing.JPanel();
        FamilyScrollPanel = new javax.swing.JScrollPane();
        familyTable = new offstage.swing.typed.FamilySelectorTable();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        entityid1 = new citibob.swing.typed.JTypedTextField();
        lastupdated1 = new citibob.swing.typed.JTypedTextField();
        addressPanel1 = new javax.swing.JPanel();
        address3 = new citibob.swing.typed.JTypedTextField();
        address4 = new citibob.swing.typed.JTypedTextField();
        city1 = new citibob.swing.typed.JTypedTextField();
        state1 = new citibob.swing.typed.JTypedTextField();
        zip1 = new citibob.swing.typed.JTypedTextField();
        jLabel12 = new javax.swing.JLabel();
        EmailPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        email2 = new citibob.swing.typed.JTypedTextField();
        bLaunchEmail1 = new javax.swing.JButton();
        StudentTab = new javax.swing.JTabbedPane();
        StudentPane = new javax.swing.JPanel();
        FirstMiddleLast2 = new javax.swing.JPanel();
        lFirst2 = new javax.swing.JLabel();
        lMiddle2 = new javax.swing.JLabel();
        lLast2 = new javax.swing.JLabel();
        salutation2 = new citibob.swing.typed.JTypedTextField();
        firstname2 = new citibob.swing.typed.JTypedTextField();
        middlename2 = new citibob.swing.typed.JTypedTextField();
        lastname2 = new citibob.swing.typed.JTypedTextField();
        dob = new citibob.swing.typed.JTypedDateChooser();
        jLabel17 = new javax.swing.JLabel();
        programs = new citibob.swing.typed.JKeyedComboBox();
        jLabel14 = new javax.swing.JLabel();
        gender = new citibob.swing.typed.JKeyedComboBox();
        lGender = new javax.swing.JLabel();
        AccountTab = new javax.swing.JTabbedPane();
        AccountPane = new javax.swing.JPanel();
        GroupScrollPanel1 = new javax.swing.JScrollPane();
        trans = new citibob.jschema.swing.StatusTable();
        controller1 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        bCash = new javax.swing.JButton();
        bCheck = new javax.swing.JButton();
        bCc = new javax.swing.JButton();
        bAdjust = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        acbal = new citibob.swing.typed.JTypedLabel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel12 = new javax.swing.JPanel();
        GroupScrollPanel = new javax.swing.JScrollPane();
        enrollments = new citibob.jschema.swing.StatusTable();
        jPanel15 = new javax.swing.JPanel();
        bAddEnrollment = new javax.swing.JButton();
        bRemoveEnrollment = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        vTermID.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        vTermID.setPreferredSize(new java.awt.Dimension(68, 19));

        jLabel3.setText("Term: ");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jLabel3)
                .add(3, 3, 3)
                .add(vTermID, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel3)
            .add(vTermID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        PeopleHeader.setLayout(new java.awt.GridBagLayout());

        PeopleHeader.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        PeopleHeader.setPreferredSize(new java.awt.Dimension(790, 120));
        searchBox.setMinimumSize(new java.awt.Dimension(200, 47));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        PeopleHeader.add(searchBox, gridBagConstraints);

        jLabel2.setText("Student:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 3);
        PeopleHeader.add(jLabel2, gridBagConstraints);

        jLabel4.setText("Payer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 3);
        PeopleHeader.add(jLabel4, gridBagConstraints);

        jLabel5.setText("Household:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 3);
        PeopleHeader.add(jLabel5, gridBagConstraints);

        vPayerID.setColName("adultid");
        vPayerID.setPreferredSize(new java.awt.Dimension(300, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        PeopleHeader.add(vPayerID, gridBagConstraints);

        bSave.setText("Save");
        bSave.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bSaveActionPerformed(evt);
            }
        });

        jToolBar1.add(bSave);

        bUndo.setText("Undo");
        bUndo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bUndoActionPerformed(evt);
            }
        });

        jToolBar1.add(bUndo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weighty = 1.0;
        PeopleHeader.add(jToolBar1, gridBagConstraints);

        vStudentID.setText("entityIDLabel1");
        vStudentID.setColName("entityid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        PeopleHeader.add(vStudentID, gridBagConstraints);

        lEntityID.setText("jTypedLabel1");
        lEntityID.setColName("entityid");
        lEntityID.setMinimumSize(new java.awt.Dimension(83, 15));
        lEntityID.setPreferredSize(new java.awt.Dimension(83, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        PeopleHeader.add(lEntityID, gridBagConstraints);

        vHouseholdID.setColName("primaryentityid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        PeopleHeader.add(vHouseholdID, gridBagConstraints);

        bEmancipate.setText("Emancipate");
        bEmancipate.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bEmancipateActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        PeopleHeader.add(bEmancipate, gridBagConstraints);

        jPanel2.add(PeopleHeader, java.awt.BorderLayout.NORTH);

        PayerPanel.setLayout(new java.awt.GridBagLayout());

        FirstMiddleLast.setLayout(new java.awt.GridBagLayout());

        lFirst.setText("First");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast.add(lFirst, gridBagConstraints);

        lMiddle.setText("Mid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast.add(lMiddle, gridBagConstraints);

        lLast.setText("Last");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast.add(lLast, gridBagConstraints);

        salutation.setColName("salutation");
        salutation.setPreferredSize(new java.awt.Dimension(40, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        FirstMiddleLast.add(salutation, gridBagConstraints);

        firstname.setColName("firstname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast.add(firstname, gridBagConstraints);

        middlename.setColName("middlename");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        FirstMiddleLast.add(middlename, gridBagConstraints);

        lastname.setColName("lastname");
        lastname.setPreferredSize(new java.awt.Dimension(10, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast.add(lastname, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        PayerPanel.add(FirstMiddleLast, gridBagConstraints);

        jTabbedPane2.setFont(new java.awt.Font("Dialog", 1, 10));
        payerPhonePanel.setPreferredSize(new java.awt.Dimension(453, 180));

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(payerPhonePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(payerPhonePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
        );
        jTabbedPane2.addTab("Phone", jPanel4);

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(payerCCInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(payerCCInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
        );
        jTabbedPane2.addTab("Billing", jPanel5);

        jPanel7.setLayout(new java.awt.GridBagLayout());

        jLabel7.setText("ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel7.add(jLabel7, gridBagConstraints);

        jLabel8.setText("Last Modified");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel7.add(jLabel8, gridBagConstraints);

        entityid.setEditable(false);
        entityid.setColName("entityid");
        entityid.setPreferredSize(new java.awt.Dimension(100, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel7.add(entityid, gridBagConstraints);

        lastupdated.setEditable(false);
        lastupdated.setColName("lastupdated");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(lastupdated, gridBagConstraints);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(148, Short.MAX_VALUE))
        );
        jTabbedPane2.addTab("Misc.", jPanel6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        PayerPanel.add(jTabbedPane2, gridBagConstraints);

        addressPanel.setLayout(new java.awt.GridBagLayout());

        address1.setColName("address1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel.add(address1, gridBagConstraints);

        address2.setColName("address2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel.add(address2, gridBagConstraints);

        city.setColName("city");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel.add(city, gridBagConstraints);

        state.setColName("state");
        state.setPreferredSize(new java.awt.Dimension(30, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel.add(state, gridBagConstraints);

        zip.setColName("zip");
        zip.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel.add(zip, gridBagConstraints);

        jLabel6.setText("Address / City,State,Zip");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        addressPanel.add(jLabel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        PayerPanel.add(addressPanel, gridBagConstraints);

        EmailPanel.setLayout(new java.awt.GridBagLayout());

        jLabel11.setText("Email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        EmailPanel.add(jLabel11, gridBagConstraints);

        email1.setColName("email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        EmailPanel.add(email1, gridBagConstraints);

        bLaunchEmail.setText("*");
        bLaunchEmail.setMargin(new java.awt.Insets(1, 1, 1, 1));
        bLaunchEmail.setPreferredSize(new java.awt.Dimension(14, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        EmailPanel.add(bLaunchEmail, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        PayerPanel.add(EmailPanel, gridBagConstraints);

        AdultTabs.addTab("Payer", PayerPanel);

        HouseholdPanel.setLayout(new java.awt.GridBagLayout());

        FirstMiddleLast1.setLayout(new java.awt.GridBagLayout());

        lFirst1.setText("First");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast1.add(lFirst1, gridBagConstraints);

        lMiddle1.setText("Mid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast1.add(lMiddle1, gridBagConstraints);

        lLast1.setText("Last");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast1.add(lLast1, gridBagConstraints);

        salutation1.setColName("salutation");
        salutation1.setPreferredSize(new java.awt.Dimension(40, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        FirstMiddleLast1.add(salutation1, gridBagConstraints);

        firstname1.setColName("firstname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast1.add(firstname1, gridBagConstraints);

        middlename1.setColName("middlename");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        FirstMiddleLast1.add(middlename1, gridBagConstraints);

        lastname1.setColName("lastname");
        lastname1.setPreferredSize(new java.awt.Dimension(10, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast1.add(lastname1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        HouseholdPanel.add(FirstMiddleLast1, gridBagConstraints);

        jTabbedPane3.setFont(new java.awt.Font("Dialog", 1, 10));
        householdPhonePanel.setPreferredSize(new java.awt.Dimension(453, 180));

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(householdPhonePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(householdPhonePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
        );
        jTabbedPane3.addTab("Phone", jPanel8);

        familyTable.setModel(new javax.swing.table.DefaultTableModel(
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
        FamilyScrollPanel.setViewportView(familyTable);

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(FamilyScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(FamilyScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
        );
        jTabbedPane3.addTab("Family", jPanel9);

        jPanel11.setLayout(new java.awt.GridBagLayout());

        jLabel9.setText("ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel11.add(jLabel9, gridBagConstraints);

        jLabel10.setText("Last Modified");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel11.add(jLabel10, gridBagConstraints);

        entityid1.setEditable(false);
        entityid1.setColName("entityid");
        entityid1.setPreferredSize(new java.awt.Dimension(100, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel11.add(entityid1, gridBagConstraints);

        lastupdated1.setEditable(false);
        lastupdated1.setColName("lastupdated");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel11.add(lastupdated1, gridBagConstraints);

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(jPanel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(148, Short.MAX_VALUE))
        );
        jTabbedPane3.addTab("Misc.", jPanel10);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        HouseholdPanel.add(jTabbedPane3, gridBagConstraints);

        addressPanel1.setLayout(new java.awt.GridBagLayout());

        address3.setColName("address1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel1.add(address3, gridBagConstraints);

        address4.setColName("address2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel1.add(address4, gridBagConstraints);

        city1.setColName("city");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel1.add(city1, gridBagConstraints);

        state1.setColName("state");
        state1.setPreferredSize(new java.awt.Dimension(30, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel1.add(state1, gridBagConstraints);

        zip1.setColName("zip");
        zip1.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel1.add(zip1, gridBagConstraints);

        jLabel12.setText("Address / City,State,Zip");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        addressPanel1.add(jLabel12, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        HouseholdPanel.add(addressPanel1, gridBagConstraints);

        EmailPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel13.setText("Email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        EmailPanel1.add(jLabel13, gridBagConstraints);

        email2.setColName("email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        EmailPanel1.add(email2, gridBagConstraints);

        bLaunchEmail1.setText("*");
        bLaunchEmail1.setMargin(new java.awt.Insets(1, 1, 1, 1));
        bLaunchEmail1.setPreferredSize(new java.awt.Dimension(14, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        EmailPanel1.add(bLaunchEmail1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        HouseholdPanel.add(EmailPanel1, gridBagConstraints);

        AdultTabs.addTab("Household", HouseholdPanel);

        StudentPane.setLayout(new java.awt.GridBagLayout());

        FirstMiddleLast2.setLayout(new java.awt.GridBagLayout());

        lFirst2.setText("First");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast2.add(lFirst2, gridBagConstraints);

        lMiddle2.setText("Mid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast2.add(lMiddle2, gridBagConstraints);

        lLast2.setText("Last");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast2.add(lLast2, gridBagConstraints);

        salutation2.setColName("salutation");
        salutation2.setPreferredSize(new java.awt.Dimension(40, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        FirstMiddleLast2.add(salutation2, gridBagConstraints);

        firstname2.setColName("firstname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast2.add(firstname2, gridBagConstraints);

        middlename2.setColName("middlename");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        FirstMiddleLast2.add(middlename2, gridBagConstraints);

        lastname2.setColName("lastname");
        lastname2.setPreferredSize(new java.awt.Dimension(10, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast2.add(lastname2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        StudentPane.add(FirstMiddleLast2, gridBagConstraints);

        dob.setColName("dob");
        dob.setPreferredSize(new java.awt.Dimension(122, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        StudentPane.add(dob, gridBagConstraints);

        jLabel17.setText("DOB: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        StudentPane.add(jLabel17, gridBagConstraints);

        programs.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        programs.setColName("programid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        StudentPane.add(programs, gridBagConstraints);

        jLabel14.setText("Level: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        StudentPane.add(jLabel14, gridBagConstraints);

        gender.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gender.setColName("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        StudentPane.add(gender, gridBagConstraints);

        lGender.setText("Gender: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        StudentPane.add(lGender, gridBagConstraints);

        StudentTab.addTab("Student", StudentPane);

        AccountPane.setLayout(new java.awt.BorderLayout());

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

        AccountPane.add(GroupScrollPanel1, java.awt.BorderLayout.CENTER);

        controller1.setLayout(new java.awt.BorderLayout());

        jPanel13.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jPanel13.setPreferredSize(new java.awt.Dimension(484, 35));
        jLabel15.setText("Transaction:");
        jPanel13.add(jLabel15);

        bCash.setText("Cash");
        bCash.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bCash.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bCashActionPerformed(evt);
            }
        });

        jPanel13.add(bCash);

        bCheck.setText("Check");
        bCheck.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bCheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bCheckActionPerformed(evt);
            }
        });

        jPanel13.add(bCheck);

        bCc.setText("Credit");
        bCc.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bCc.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bCcActionPerformed(evt);
            }
        });

        jPanel13.add(bCc);

        bAdjust.setText("Adj.");
        bAdjust.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bAdjust.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bAdjustActionPerformed(evt);
            }
        });

        jPanel13.add(bAdjust);

        controller1.add(jPanel13, java.awt.BorderLayout.CENTER);

        AccountPane.add(controller1, java.awt.BorderLayout.SOUTH);

        jPanel14.setLayout(new java.awt.GridBagLayout());

        jLabel18.setText("Balance: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel14.add(jLabel18, gridBagConstraints);

        acbal.setText("2500");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel14.add(acbal, gridBagConstraints);

        AccountPane.add(jPanel14, java.awt.BorderLayout.NORTH);

        AccountTab.addTab("Account History", AccountPane);

        jPanel12.setLayout(new java.awt.BorderLayout());

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

        jPanel12.add(GroupScrollPanel, java.awt.BorderLayout.CENTER);

        bAddEnrollment.setText("Add Enrollment");
        jPanel15.add(bAddEnrollment);

        bRemoveEnrollment.setText("Remove Enrollment");
        jPanel15.add(bRemoveEnrollment);

        jPanel12.add(jPanel15, java.awt.BorderLayout.SOUTH);

        jTabbedPane4.addTab("Enrollments", jPanel12);

        org.jdesktop.layout.GroupLayout PeopleMainLayout = new org.jdesktop.layout.GroupLayout(PeopleMain);
        PeopleMain.setLayout(PeopleMainLayout);
        PeopleMainLayout.setHorizontalGroup(
            PeopleMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PeopleMainLayout.createSequentialGroup()
                .add(PeopleMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(StudentTab, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, AccountTab, 0, 0, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(AdultTabs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE))
            .add(jTabbedPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
        );
        PeopleMainLayout.setVerticalGroup(
            PeopleMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PeopleMainLayout.createSequentialGroup()
                .add(PeopleMainLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(PeopleMainLayout.createSequentialGroup()
                        .add(StudentTab, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(AccountTab, 0, 0, Short.MAX_VALUE))
                    .add(PeopleMainLayout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(AdultTabs, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 361, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTabbedPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2.add(PeopleMain, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Registrations", jPanel2);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 662, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 738, Short.MAX_VALUE)
        );
        jTabbedPane1.addTab("Actions", jPanel3);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents

	private void bEmancipateActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bEmancipateActionPerformed
	{//GEN-HEADEREND:event_bEmancipateActionPerformed
		fapp.runGui(SchoolPanel.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			studentRm.set("primaryentityid", all.getIntKey());
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bEmancipateActionPerformed

	private void bUndoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bUndoActionPerformed
	{//GEN-HEADEREND:event_bUndoActionPerformed
		fapp.runGui(SchoolPanel.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			all.doSelect(st);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bUndoActionPerformed

	private void bSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSaveActionPerformed
	{//GEN-HEADEREND:event_bSaveActionPerformed
		fapp.runGui(SchoolPanel.this, new StRunnable() {
		public void run(Statement st) throws Exception {
			all.doUpdate(st);
			all.doSelect(st);
		}});
	}//GEN-LAST:event_bSaveActionPerformed

	private void bAdjustActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bAdjustActionPerformed
	{//GEN-HEADEREND:event_bAdjustActionPerformed
//		fapp.runGui(SchoolPanel.this, new StRunnable()
//		{
//			public void run(Statement st) throws Exception
//			{
//				Wizard wizard = new TransactionWizard(fapp, st, null, entityid, actypeid);
//				wizard.runWizard("adjpayment");
//				actransDb.doSelect(st);
//			}});
// TODO add your handling code here:
	}//GEN-LAST:event_bAdjustActionPerformed

	private void bCcActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bCcActionPerformed
	{//GEN-HEADEREND:event_bCcActionPerformed
//		fapp.runGui(SchoolPanel.this, new StRunnable()
//		{
//			public void run(Statement st) throws Exception
//			{
//				Wizard wizard = new TransactionWizard(fapp, st, null, entityid, actypeid);
//				wizard.runWizard("ccpayment");
//				actransDb.doSelect(st);
//			}});
// TODO add your handling code here:
	}//GEN-LAST:event_bCcActionPerformed

	private void bCheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bCheckActionPerformed
	{//GEN-HEADEREND:event_bCheckActionPerformed
//		fapp.runGui(SchoolPanel.this, new StRunnable()
//		{
//			public void run(Statement st) throws Exception
//			{
//				Wizard wizard = new TransactionWizard(fapp, st, null, entityid, actypeid);
//				wizard.runWizard("checkpayment");
//				actransDb.doSelect(st);
//			}});
// TODO add your handling code here:
	}//GEN-LAST:event_bCheckActionPerformed

	private void bCashActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bCashActionPerformed
	{//GEN-HEADEREND:event_bCashActionPerformed
//		fapp.runGui(SchoolPanel.this, new StRunnable()
//		{
//			public void run(Statement st) throws Exception
//			{
//				Wizard wizard = new TransactionWizard(fapp, st, null, entityid, actypeid);
//				wizard.runWizard("cashpayment");
//				actransDb.doSelect(st);
//			}});
	}//GEN-LAST:event_bCashActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AccountPane;
    private javax.swing.JTabbedPane AccountTab;
    private javax.swing.JTabbedPane AdultTabs;
    private javax.swing.JPanel EmailPanel;
    private javax.swing.JPanel EmailPanel1;
    private javax.swing.JScrollPane FamilyScrollPanel;
    private javax.swing.JPanel FirstMiddleLast;
    private javax.swing.JPanel FirstMiddleLast1;
    private javax.swing.JPanel FirstMiddleLast2;
    private javax.swing.JScrollPane GroupScrollPanel;
    private javax.swing.JScrollPane GroupScrollPanel1;
    private javax.swing.JPanel HouseholdPanel;
    private javax.swing.JPanel PayerPanel;
    private javax.swing.JPanel PeopleHeader;
    private javax.swing.JPanel PeopleMain;
    private javax.swing.JPanel StudentPane;
    private javax.swing.JTabbedPane StudentTab;
    private citibob.swing.typed.JTypedLabel acbal;
    private citibob.swing.typed.JTypedTextField address1;
    private citibob.swing.typed.JTypedTextField address2;
    private citibob.swing.typed.JTypedTextField address3;
    private citibob.swing.typed.JTypedTextField address4;
    private javax.swing.JPanel addressPanel;
    private javax.swing.JPanel addressPanel1;
    private javax.swing.JButton bAddEnrollment;
    private javax.swing.JButton bAdjust;
    private javax.swing.JButton bCash;
    private javax.swing.JButton bCc;
    private javax.swing.JButton bCheck;
    private javax.swing.JButton bEmancipate;
    private javax.swing.JButton bLaunchEmail;
    private javax.swing.JButton bLaunchEmail1;
    private javax.swing.JButton bRemoveEnrollment;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bUndo;
    private citibob.swing.typed.JTypedTextField city;
    private citibob.swing.typed.JTypedTextField city1;
    private javax.swing.JPanel controller1;
    private citibob.swing.typed.JTypedDateChooser dob;
    private citibob.swing.typed.JTypedTextField email1;
    private citibob.swing.typed.JTypedTextField email2;
    private citibob.jschema.swing.StatusTable enrollments;
    private citibob.swing.typed.JTypedTextField entityid;
    private citibob.swing.typed.JTypedTextField entityid1;
    private offstage.swing.typed.FamilySelectorTable familyTable;
    private citibob.swing.typed.JTypedTextField firstname;
    private citibob.swing.typed.JTypedTextField firstname1;
    private citibob.swing.typed.JTypedTextField firstname2;
    private citibob.swing.typed.JKeyedComboBox gender;
    private offstage.gui.GroupPanel householdPhonePanel;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JToolBar jToolBar1;
    private citibob.swing.typed.JTypedLabel lEntityID;
    private javax.swing.JLabel lFirst;
    private javax.swing.JLabel lFirst1;
    private javax.swing.JLabel lFirst2;
    private javax.swing.JLabel lGender;
    private javax.swing.JLabel lLast;
    private javax.swing.JLabel lLast1;
    private javax.swing.JLabel lLast2;
    private javax.swing.JLabel lMiddle;
    private javax.swing.JLabel lMiddle1;
    private javax.swing.JLabel lMiddle2;
    private citibob.swing.typed.JTypedTextField lastname;
    private citibob.swing.typed.JTypedTextField lastname1;
    private citibob.swing.typed.JTypedTextField lastname2;
    private citibob.swing.typed.JTypedTextField lastupdated;
    private citibob.swing.typed.JTypedTextField lastupdated1;
    private citibob.swing.typed.JTypedTextField middlename;
    private citibob.swing.typed.JTypedTextField middlename1;
    private citibob.swing.typed.JTypedTextField middlename2;
    private offstage.swing.typed.CryptCCInfo payerCCInfo;
    private offstage.gui.GroupPanel payerPhonePanel;
    private citibob.swing.typed.JKeyedComboBox programs;
    private citibob.swing.typed.JTypedTextField salutation;
    private citibob.swing.typed.JTypedTextField salutation1;
    private citibob.swing.typed.JTypedTextField salutation2;
    private offstage.swing.typed.EntitySelector searchBox;
    private citibob.swing.typed.JTypedTextField state;
    private citibob.swing.typed.JTypedTextField state1;
    private citibob.jschema.swing.StatusTable trans;
    private offstage.swing.typed.HouseholdIDEditableLabel vHouseholdID;
    private offstage.swing.typed.EntityIDEditableLabel vPayerID;
    private offstage.swing.typed.EntityIDLabel vStudentID;
    private citibob.swing.typed.JKeyedComboBox vTermID;
    private citibob.swing.typed.JTypedTextField zip;
    private citibob.swing.typed.JTypedTextField zip1;
    // End of variables declaration//GEN-END:variables
public static void main(String[] args) throws Exception
{
	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
	Statement st = pool.checkout().createStatement();
	FrontApp fapp = new FrontApp(pool,null);

	SchoolPanel panel = new SchoolPanel();
	panel.initRuntime(fapp, st);
	
	JFrame frame = new JFrame();
	frame.setSize(600,800);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().add(panel);

	frame.setVisible(true);
}

}
