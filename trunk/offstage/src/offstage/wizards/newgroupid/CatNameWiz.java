/*
 * PersonWiz.java
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
public class CatNameWiz extends HtmlWiz {
	
/**
 * Creates a new instance of PersonWiz 
 */
public CatNameWiz(java.awt.Frame owner, String cattype)
throws org.xml.sax.SAXException, java.io.IOException
{
	super(owner, "New Category (2 of 2)", true);
//	setSize(600,460);
	addWidget("cattype", new JLabel(cattype));
	addTextField("catname", new JStringSwinger());
	loadHtml();
}


//public static void main(String[] args)
//throws Exception
//{
//	JFrame f = new JFrame();
//	f.setVisible(true);
//	CatNameWiz wiz = new CatNameWiz(f);
//	wiz.setVisible(true);
//	System.out.println(wiz.getSubmitName());
//	
//	wiz = new CatNameWiz(f);
//	wiz.setVisible(true);
//	System.out.println(wiz.getSubmitName());
//	
//	System.exit(0);
//}
}
