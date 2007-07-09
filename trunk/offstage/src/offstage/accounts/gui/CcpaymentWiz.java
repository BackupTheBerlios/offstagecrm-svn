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
import citibob.util.*;

/**
 *
 * @author citibob
 */
public class CcpaymentWiz extends HtmlWiz {

	
/**
 * Creates a new instance of PersonWiz 
 */
public CcpaymentWiz(java.awt.Frame owner, App app)
throws org.xml.sax.SAXException, java.io.IOException
{
	super(owner, "New Cash Payment", app.getSwingerMap(), true);
	
	Schema schema = app.getSchema("ccpayments");
//	SwingerMap swingers = app.getSwingerMap();
	
	setSize(600,460);
//	TypedWidgetMap map = new TypedWidgetMap();
	addWidget("namount", "amount", schema);		// Negative of amount...
	KeyedModel kmodel = new KeyedModel();
		kmodel.addItem(null, "<None>");
		kmodel.addItem(new Character('m'), "Master Card");
		kmodel.addItem(new Character('v'), "Visa");
	addWidget("cctype", new JKeyedComboBox(kmodel));
	addTextField("ccnumber", new JStringSwinger());
	addTextField("expdate", new JStringSwinger());
	addTextField("seccode", new JStringSwinger());
	addTextField("description", schema);
	addWidget("dtime", schema).setValue(null);
	loadHtml();
}

}
