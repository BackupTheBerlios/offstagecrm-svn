/*
 * PersonWiz.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.wizards.newrecord;

import citibob.swing.html.*;
import java.util.*;
import citibob.swing.typed.*;
import citibob.swing.html.*;
import offstage.types.*;
import javax.swing.*;
import offstage.wizards.*;
import citibob.wizard.*;

/**
 *
 * @author citibob
 */
public class PersonWiz extends HtmlWiz {
	
/**
 * Creates a new instance of PersonWiz 
 */
public PersonWiz(java.awt.Frame owner)
throws org.xml.sax.SAXException, java.io.IOException
{
	super(owner, "New Record", true);
	setSize(600,400);
//	TypedWidgetMap map = new TypedWidgetMap();
	addTextField("firstname", new JStringSwinger());
	addTextField("middlename", new JStringSwinger());
	addTextField("lastname", new JStringSwinger());
	addTextField("address1", new JStringSwinger());
	addTextField("address2", new JStringSwinger());
	addTextField("city", new JStringSwinger());
	addTextField("state", new JStringSwinger());
	addTextField("zip", new JStringSwinger());
	addTextField("phone", new PhoneSwinger());
	addTextField("email", new JStringSwinger());
	
	loadHtml();
}


public static void main(String[] args)
throws Exception
{
	JFrame f = new JFrame();
	f.setVisible(true);
	PersonWiz wiz = new PersonWiz(f);
	wiz.setVisible(true);
	System.out.println(wiz.getSubmitName());
	
	wiz = new PersonWiz(f);
	wiz.setVisible(true);
	System.out.println(wiz.getSubmitName());
	
	System.exit(0);
}
}
