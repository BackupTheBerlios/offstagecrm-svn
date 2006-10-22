/*
 * NewRecordWiz2.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.wizards.newrecord;

import java.util.*;
import citibob.swing.typed.*;
import citibob.swing.html.*;
import offstage.types.*;
import javax.swing.*;
import offstage.wizards.*;
import offstage.*;
import offstage.gui.*;
import citibob.swing.html.HtmlWiz;

/**
 *
 * @author citibob
 */
public class DupsWiz extends HtmlWiz {

/** Should this Wiz screen be cached when "Back" is pressed? */
public boolean getCacheWiz() { return false; }

/**
 * Creates a new instance of NewRecordWiz2 
 */
public DupsWiz(java.awt.Frame owner, java.sql.Statement st, FrontApp fapp, String idSql)
throws org.xml.sax.SAXException, java.io.IOException, java.sql.SQLException
{
	super(owner, "Possible Duplicate Name", true);

	
	IDListViewer listView = new IDListViewer();
	listView.initRuntime(st, fapp.getFullEntityDm(),
		idSql, null,
		fapp.getGuiRunner(), fapp.getSwingerMap());
//	html.getMap().put("idlistviewer", listView);
	html.addWidget("idlistviewer", listView);
	addSubmitButton("dontadd", "Don't Add");
	addSubmitButton("addanyway", "Add Anyway");
	this.setSize(new java.awt.Dimension(750, 550));
	loadHtml();
}


public static void main(String[] args)
throws Exception
{
	JFrame f = new JFrame();
	f.setVisible(true);
	InitWiz wiz = new InitWiz(f);
	wiz.setVisible(true);
	System.out.println(wiz.getSubmitName());
	
	System.exit(0);
}
}
