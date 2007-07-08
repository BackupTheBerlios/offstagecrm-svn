/*
 * PersonWiz.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.accounts.gui;

import citibob.swing.html.*;
import java.util.*;
import citibob.swing.typed.*;
import citibob.swing.html.*;
import offstage.types.*;
import javax.swing.*;
import offstage.wizards.*;
import citibob.wizard.*;
import citibob.app.*;
import offstage.schema.*;
import citibob.jschema.*;

/**
 *
 * @author citibob
 */
public class CheckpaymentWiz extends HtmlWiz {

	
/**
 * Creates a new instance of PersonWiz 
 */
public CheckpaymentWiz(java.awt.Frame owner, App app)
throws org.xml.sax.SAXException, java.io.IOException
{
	super(owner, "New Check Payment", app.getSwingerMap(), true);
	
	Schema schema = app.getSchema("actrans");
//	SwingerMap swingers = app.getSwingerMap();
	
	setSize(600,460);
//	TypedWidgetMap map = new TypedWidgetMap();
	addTextField("amount", schema);
	addTextField("description", schema);
	addTextField("name", schema);
	addTextField("checknumber", schema);
	addTextField("phone", schema);
	addWidget("dtime", schema).setValue(null);
	loadHtml();
}

}
