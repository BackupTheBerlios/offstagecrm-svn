///*
// * NewRecordWiz2.java
// *
// * Created on October 8, 2006, 6:08 PM
// *
// * To change this template, choose Tools | Options and locate the template under
// * the Source Creation and Management node. Right-click the template and choose
// * Open. You can then make changes to the template in the Source Editor.
// */
//
//package offstage.wizard.editcourses;
//
//import citibob.swing.html.*;
//import java.util.*;
//import citibob.swing.typed.*;
//import citibob.swing.html.*;
//import offstage.types.*;
//import javax.swing.*;
//import offstage.wizards.*;
//import citibob.wizard.*;
//import citibob.util.*;
//import citibob.jschema.*;
//import java.sql.*;
//
///**
// *
// * @author citibob
// */
//public class TermListWiz extends HtmlWiz {
//	
///**
// * Creates a new instance of NewRecordWiz2 
// */
//public TermListWiz(java.awt.Frame owner, Statement st, citibob.app.App app)
//throws org.xml.sax.SAXException, java.io.IOException, SQLException
//{
//	super(owner, "Edit Courses: Select Term", true);
//
//	// Get our list of terms
//	SchemaBufDbModel tm = new SchemaBufDbModel(app.getSchema("termids"));
//	tm.setWhereClause("iscurrent");
//	tm.setOrderClause("firstdate");
//	tm.doSelect(st);
//
//	// Stick them in a widget
//	JTypedSelectTable tab = new JTypedSelectTable();
//	tab.setModelU(tm.getSchemaBuf(),
//		new String[] {"Type", "Name", "From", "To (+1)"},
//		new String[] {"termtypeid", "name", "firstdate", "nextdate"},
//		new boolean[4], app.getSwingerMap());
//
//	JTypedScrollPane scroll = new JTypedScrollPane(tab);
//	scroll.add(tab);
//	addWidget("terms", scroll);
//	
////	KeyedModel kmodel = new KeyedModel();
////		kmodel.addItem("donationids", "Donations");
////	JKeyedComboBox combo = new JKeyedComboBox(kmodel);
////	combo.setValue("donationids");
////	addWidget("table", combo);
////	addTextField("catname", new JStringSwinger());
//	loadHtml();
//}
//
//}
