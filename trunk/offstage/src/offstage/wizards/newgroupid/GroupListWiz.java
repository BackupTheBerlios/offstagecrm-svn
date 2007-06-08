/*
 * NewRecordWiz2.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.wizards.newgroupid;

import citibob.swing.html.*;
import java.util.*;
import citibob.swing.typed.*;
import citibob.swing.html.*;
import offstage.types.*;
import javax.swing.*;
import offstage.wizards.*;
import citibob.wizard.*;
import citibob.util.*;

/**
 *
 * @author citibob
 */
public class GroupListWiz extends HtmlWiz {
	
/**
 * Creates a new instance of NewRecordWiz2 
 */
public GroupListWiz(java.awt.Frame owner)
throws org.xml.sax.SAXException, java.io.IOException
{
	super(owner, "New Category", true);
//	KeyedModel kmodel = new KeyedModel();
//		kmodel.addItem("donationids", "Donations");
//	JKeyedComboBox combo = new JKeyedComboBox(kmodel);
//	combo.setValue("donationids");
//	addWidget("table", combo);
//	addTextField("catname", new JStringSwinger());
	loadHtml();
}

}
