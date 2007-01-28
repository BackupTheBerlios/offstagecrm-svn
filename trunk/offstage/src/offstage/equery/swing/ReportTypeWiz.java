/*
 * PersonWiz.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.equery.swing;

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
public class ReportTypeWiz extends HtmlWiz {
	
/**
 * Creates a new instance of PersonWiz 
 */
public ReportTypeWiz(java.awt.Frame owner)
throws org.xml.sax.SAXException, java.io.IOException
{
	super(owner, "Report Selection", true);
	setSize(600,400);
//	TypedWidgetMap map = new TypedWidgetMap();
//	addTextField("queryname", new JStringSwinger());
	
	loadHtml();
}


}
