/*
 * PersonWiz.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.reports;

import citibob.swing.html.*;
import java.util.*;
import citibob.swing.typed.*;
import citibob.swing.html.*;
import offstage.types.*;
import javax.swing.*;
import offstage.wizards.*;
import citibob.wizard.*;
import citibob.app.*;

/**
 *
 * @author citibob
 */
public class TicketParamsWiz extends HtmlWiz {
	
/**
 * Creates a new instance of PersonWiz 
 */
public TicketParamsWiz(java.awt.Frame owner, App app)
throws org.xml.sax.SAXException, java.io.IOException
{
	super(owner, "New Query", app.getSwingerMap(), true);
	setSize(600,400);
	citibob.jschema.Schema schema = app.getSchema("tickets");
	addWidget("groupid", schema);
//.getCol("tickettypeid").getType()
//	TypedWidgetMap map = new TypedWidgetMap();
//	addTextField("queryname", new JStringSwinger());
	
	loadHtml();
}

//
//public static void main(String[] args)
//throws Exception
//{
//	JFrame f = new JFrame();
//	f.setVisible(true);
//	TicketParamsWiz wiz = new TicketParamsWiz(f);
//	wiz.setVisible(true);
//	System.out.println(wiz.getSubmitName());
//	
//	wiz = new TicketParamsWiz(f);
//	wiz.setVisible(true);
//	System.out.println(wiz.getSubmitName());
//	
//	System.exit(0);
//}
}
