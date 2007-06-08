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

/**
 *
 * @author citibob
 */
public class FinishedWiz extends HtmlWiz {
	
/**
 * Creates a new instance of NewRecordWiz2 
 */
public FinishedWiz(java.awt.Frame owner)
throws org.xml.sax.SAXException, java.io.IOException
{
	super(owner, "New Category", true);
	addSubmitButton("next", "Finished");
	loadHtml();
}

}
