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
 * PersonPanel.java
 *
 * Created on February 9, 2005, 8:18 PM
 */

package offstage.gui;

import citibob.swing.RowModel;
import citibob.swing.RowModel.ColListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import citibob.jschema.*;
import citibob.jschema.swing.*;
import citibob.swing.typed.*;
//import citibob.jschema.swing.JSchemaWidgetTree;
import citibob.swing.table.*;
import offstage.FrontApp;
import offstage.db.FullEntityDbModel;
import citibob.multithread.*;
import citibob.app.App;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import citibob.sql.*;

/**
 *
 * @author  citibob
 */
public class PersonPanel 
extends javax.swing.JPanel {
    
    SchemaRowModel model;	// The RowModel (which uses the schema)
	//SchemaBuf phonesSb;
	//TableModel family;
	FullEntityDbModel dm;
//	Statement st;
//	ActionRunner runner;
	App app;
	
//    public static void main(String[] args) throws Exception
//    {
//
//
//		FrontApp app = new FrontApp();
//		FullEntityDbModel dm = app.getFullEntityDm();
//		Statement st = app.createStatement();
//
//		dm.setKey(146141);
//		dm.doSelect(st);
//		System.out.println("Type = " + dm.getEntityType());
//
//		PersonPanel personPanel = new PersonPanel();
//		personPanel.initRuntime(st, dm);//personRM, dm.getPhonesSb());
//
//		
//		
//	    JFrame frame = new JFrame();
//	    frame.getContentPane().add(personPanel);
//		frame.pack();
//	    frame.setVisible(true);
//		System.out.println("Done");
//    }
    
	/** Creates new form PersonPanel */
	public PersonPanel() {
		initComponents();
		genderButtonGroup.add("M", maleButton);
		genderButtonGroup.add("F", femaleButton);
		genderButtonGroup.add(null, unknownGenderButton);
		
		familyTable.addPropertyChangeListener("value", new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			app.runGui(PersonPanel.this, new BatchRunnable() {
			public void run(SqlRunner str) throws Exception {
				Integer EntityID = (Integer)familyTable.getValue();
				if (EntityID == null) return;
				dm.setKey(EntityID);
				dm.doSelect(str);
			}});
		}});
		
//		familyTable.addMouseListener(new DClickTableMouseListener(familyTable) {
//		public void doubleClicked(final int row) {
//		}});

	}
	
	public void initRuntime(SqlRunner str, App xapp, FullEntityDbModel dm)
	//throws java.sql.SQLException
	{
		this.app = xapp;
		this.dm = dm;
		//SchemaBufRowModel model = dm.getPersonRm();
		SchemaBufRowModel xmodel = new SchemaBufRowModel(dm.getPersonSb());
		//SchemaBuf phonesSb = dm.getPhonesSb();

		//this.phonesSb = phonesSb;
		this.model = xmodel;
		this.vHouseholdID.initRuntime(app);
		
		// Bind the Family Table thingy (it's special)
		familyTable.initRuntime(app);
		
		// Change family table contents when user re-reads from db
		model.addColListener(model.findColumn("primaryentityid"), new RowModel.ColAdapter() {
		public void curRowChanged(final int col) {
			app.runApp(new BatchRunnable() {
			public void run(SqlRunner str) throws Exception {
				if (model.getCurRow() < 0) return;
//				if (familyTable.isInSelect()) return;	// Don't re-query just cause user is clicking
				Integer OrigEntityID = (Integer)model.getOrigValue(col);
				Integer EntityID = (Integer)model.get(col);
//				Integer OrigEntityID = (Integer)dm.getPersonSb().getValueAt(0, col);
//				Integer EntityID = (Integer)dm.getPersonSb().getValueAt(0, col);
				if (EntityID == null) return;
				if (OrigEntityID != null && OrigEntityID.intValue() == EntityID.intValue()) {
					// Orig == Value --- greater class probably just re-read from DB.
					// So now we need to re-read too.  This problem should REALLY be
					// solved by adding events to DbModel.
					familyTable.setPrimaryEntityID(str, EntityID);
//					vHouseholdID.setEntityID(EntityID);
				}
			}});
		}});

		model.addColListener(model.findColumn("entityid"), new RowModel.ColAdapter() {
		public void curRowChanged(final int col) {
			app.runApp(new ERunnable() {
			public void run() throws Exception {
				if (model.getCurRow() < 0) return;
//				if (familyTable.isInSelect()) return;	// Don't re-query just cause user is clicking
				Integer OrigEntityID = (Integer)model.getOrigValue(col);
				Integer EntityID = (Integer)model.get(col);
//				Integer OrigEntityID = (Integer)dm.getPersonSb().getValueAt(0, col);
//				Integer EntityID = (Integer)dm.getPersonSb().getValueAt(0, col);
				if (EntityID == null) return;
				if (OrigEntityID != null && OrigEntityID.intValue() == EntityID.intValue()) {
					// Orig == Value --- greater class probably just re-read from DB.
					// So now we need to re-read too.  This problem should REALLY be
					// solved by adding events to DbModel.
					vHouseholdID.setEntityID(EntityID);
				}
			}});
		}});
		
		
		TypedWidgetBinder.bindRecursive(this, model, app.getSwingerMap());
		new TypedWidgetBinder().bind(genderButtonGroup, xmodel);
//		new IsPrimaryBinder().bind(cbIsPrimary, model);	// Should just do a regular listener as above; this is read-only

		this.entitySubPanel1.initRuntime(app);
		
		phonePanel.initRuntime(str, dm.getPhonesSb(),
			new String[] {"Type", "Number"},
			new String[] {"groupid", "phone"}, app.getSwingerMap());
		
			
//		phonesTable.initRuntime(dm.getPhonesSb());
//		this.addPhoneType.setModel(new GroupTypeKeyedModel(st, "phoneids"));
		// phonesTable.setModel(new ColPermuteTableModel(phonesSb,
		//	new String[] {"Type", "Phone"},
		//	new String[] {"groupid", "phone"}));
//		familyTable.setModel(dm.getFamily());
//		familyTable.initRuntime(dm.getPersonDb().getFamily());
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

        genderButtonGroup = new citibob.swing.typed.KeyedButtonGroup();
        entitySubPanel1 = new offstage.gui.EntitySubPanel();
        MiscInfo = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        occupation = new citibob.swing.typed.JTypedTextField();
        title = new citibob.swing.typed.JTypedTextField();
        url = new citibob.swing.typed.JTypedTextField();
        dob = new citibob.swing.typed.JTypedDateChooser();
        orgname = new citibob.swing.typed.JTypedTextField();
        jLabel9 = new javax.swing.JLabel();
        email1 = new citibob.swing.typed.JTypedTextField();
        jLabel11 = new javax.swing.JLabel();
        bLaunchEmail = new javax.swing.JButton();
        bLaunchBrowser = new javax.swing.JButton();
        FamilyPane = new javax.swing.JPanel();
        FamilyScrollPanel = new javax.swing.JScrollPane();
        familyTable = new offstage.swing.typed.FamilySelectorTable();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        vHouseholdID = new offstage.swing.typed.HouseholdIDEditableLabel();
        phonePanel = new offstage.gui.GroupPanel();
        lPhoneNumbers = new javax.swing.JLabel();
        FirstMiddleLast = new javax.swing.JPanel();
        lFirst = new javax.swing.JLabel();
        lMiddle = new javax.swing.JLabel();
        lLast = new javax.swing.JLabel();
        salutation = new citibob.swing.typed.JTypedTextField();
        firstname = new citibob.swing.typed.JTypedTextField();
        middlename = new citibob.swing.typed.JTypedTextField();
        lastname = new citibob.swing.typed.JTypedTextField();
        GenderX = new javax.swing.JPanel();
        isorg = new citibob.swing.typed.JBoolCheckbox();
        mailprefid = new citibob.swing.typed.JKeyedComboBox();
        Gender = new javax.swing.JPanel();
        maleButton = new javax.swing.JRadioButton();
        femaleButton = new javax.swing.JRadioButton();
        unknownGenderButton = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        customaddressto = new citibob.swing.typed.JTypedTextField();
        jLabel10 = new javax.swing.JLabel();
        bEmancipate = new javax.swing.JButton();

        genderButtonGroup.setColName("gender");

        entitySubPanel1.setPreferredSize(null);

        MiscInfo.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText("Occup.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel4, gridBagConstraints);

        jLabel5.setText("DOB");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Title");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel6, gridBagConstraints);

        jLabel7.setText("URL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel7, gridBagConstraints);

        occupation.setColName("occupation");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(occupation, gridBagConstraints);

        title.setColName("title");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(title, gridBagConstraints);

        url.setColName("url");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(url, gridBagConstraints);

        dob.setColName("dob");
        dob.setPreferredSize(new java.awt.Dimension(122, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(dob, gridBagConstraints);

        orgname.setColName("orgname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(orgname, gridBagConstraints);

        jLabel9.setText("Org.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel9, gridBagConstraints);

        email1.setColName("email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(email1, gridBagConstraints);

        jLabel11.setText("Email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel11, gridBagConstraints);

        bLaunchEmail.setText("*");
        bLaunchEmail.setMargin(new java.awt.Insets(1, 1, 1, 1));
        bLaunchEmail.setPreferredSize(new java.awt.Dimension(14, 19));
        bLaunchEmail.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bLaunchEmailActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        MiscInfo.add(bLaunchEmail, gridBagConstraints);

        bLaunchBrowser.setText("*");
        bLaunchBrowser.setMargin(new java.awt.Insets(1, 1, 1, 1));
        bLaunchBrowser.setPreferredSize(new java.awt.Dimension(14, 19));
        bLaunchBrowser.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bLaunchBrowserActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        MiscInfo.add(bLaunchBrowser, gridBagConstraints);

        FamilyPane.setLayout(new java.awt.BorderLayout());

        FamilyPane.setPreferredSize(new java.awt.Dimension(418, 100));
        FamilyScrollPanel.setPreferredSize(new java.awt.Dimension(300, 64));
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

        FamilyPane.add(FamilyScrollPanel, java.awt.BorderLayout.CENTER);

        jLabel8.setText("Family Members");
        FamilyPane.add(jLabel8, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel12.setText("Household:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 3);
        jPanel2.add(jLabel12, gridBagConstraints);

        vHouseholdID.setColName("primaryentityid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(vHouseholdID, gridBagConstraints);

        FamilyPane.add(jPanel2, java.awt.BorderLayout.NORTH);

        phonePanel.setPreferredSize(new java.awt.Dimension(453, 180));

        lPhoneNumbers.setText("Phone Numbers");

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

        GenderX.setLayout(new java.awt.GridBagLayout());

        isorg.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        isorg.setText("is Org. Record?");
        isorg.setColName("isorg");
        isorg.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        GenderX.add(isorg, gridBagConstraints);

        mailprefid.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        mailprefid.setColName("mailprefid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        GenderX.add(mailprefid, gridBagConstraints);

        Gender.setLayout(new javax.swing.BoxLayout(Gender, javax.swing.BoxLayout.Y_AXIS));

        maleButton.setText("Male");
        maleButton.setMargin(null);
        maleButton.setPreferredSize(new java.awt.Dimension(54, 19));
        Gender.add(maleButton);

        femaleButton.setText("Female");
        femaleButton.setMargin(null);
        femaleButton.setPreferredSize(new java.awt.Dimension(69, 19));
        Gender.add(femaleButton);

        unknownGenderButton.setText("Unknown");
        unknownGenderButton.setMargin(null);
        unknownGenderButton.setPreferredSize(new java.awt.Dimension(85, 19));
        Gender.add(unknownGenderButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        GenderX.add(Gender, gridBagConstraints);

        jLabel1.setText("Mail Preference");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        GenderX.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        GenderX.add(jLabel2, gridBagConstraints);

        customaddressto.setColName("customaddressto");

        jLabel10.setText("To");

        bEmancipate.setText("Emancipate");
        bEmancipate.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bEmancipateActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(MiscInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 262, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 48, Short.MAX_VALUE)
                        .add(GenderX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 155, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(FirstMiddleLast, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 322, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 26, Short.MAX_VALUE)
                        .add(bEmancipate)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(FamilyPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(entitySubPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jLabel10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(customaddressto, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(lPhoneNumbers)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(phonePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(FirstMiddleLast, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(bEmancipate))
                        .add(9, 9, 9)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(GenderX, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                            .add(MiscInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lPhoneNumbers)
                            .add(jLabel10)
                            .add(customaddressto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(FamilyPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 181, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(entitySubPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(phonePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 157, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

	private void bLaunchEmailActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bLaunchEmailActionPerformed
	{//GEN-HEADEREND:event_bLaunchEmailActionPerformed
		citibob.gui.BareBonesMailto.mailto((String)email1.getValue());
	}//GEN-LAST:event_bLaunchEmailActionPerformed

	private void bEmancipateActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bEmancipateActionPerformed
	{//GEN-HEADEREND:event_bEmancipateActionPerformed
		app.runGui(PersonPanel.this, new BatchRunnable() {
		public void run(SqlRunner str) throws Exception {
				model.set("primaryentityid", dm.getPersonSb().getValueAt(0, "entityid"));
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bEmancipateActionPerformed

	private void bLaunchBrowserActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bLaunchBrowserActionPerformed
	{//GEN-HEADEREND:event_bLaunchBrowserActionPerformed
		citibob.gui.BareBonesBrowserLaunch.openURL((String)url.getValue());
	}//GEN-LAST:event_bLaunchBrowserActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel FamilyPane;
    private javax.swing.JScrollPane FamilyScrollPanel;
    private javax.swing.JPanel FirstMiddleLast;
    private javax.swing.JPanel Gender;
    private javax.swing.JPanel GenderX;
    private javax.swing.JPanel MiscInfo;
    private javax.swing.JButton bEmancipate;
    private javax.swing.JButton bLaunchBrowser;
    private javax.swing.JButton bLaunchEmail;
    private citibob.swing.typed.JTypedTextField customaddressto;
    private citibob.swing.typed.JTypedDateChooser dob;
    private citibob.swing.typed.JTypedTextField email1;
    private offstage.gui.EntitySubPanel entitySubPanel1;
    private offstage.swing.typed.FamilySelectorTable familyTable;
    private javax.swing.JRadioButton femaleButton;
    private citibob.swing.typed.JTypedTextField firstname;
    private citibob.swing.typed.KeyedButtonGroup genderButtonGroup;
    private citibob.swing.typed.JBoolCheckbox isorg;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lFirst;
    private javax.swing.JLabel lLast;
    private javax.swing.JLabel lMiddle;
    private javax.swing.JLabel lPhoneNumbers;
    private citibob.swing.typed.JTypedTextField lastname;
    private citibob.swing.typed.JKeyedComboBox mailprefid;
    private javax.swing.JRadioButton maleButton;
    private citibob.swing.typed.JTypedTextField middlename;
    private citibob.swing.typed.JTypedTextField occupation;
    private citibob.swing.typed.JTypedTextField orgname;
    private offstage.gui.GroupPanel phonePanel;
    private citibob.swing.typed.JTypedTextField salutation;
    private citibob.swing.typed.JTypedTextField title;
    private javax.swing.JRadioButton unknownGenderButton;
    private citibob.swing.typed.JTypedTextField url;
    private offstage.swing.typed.HouseholdIDEditableLabel vHouseholdID;
    // End of variables declaration//GEN-END:variables
	// --------------------------------------------------------------

}
