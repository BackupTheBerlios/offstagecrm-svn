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

/**
 *
 * @author citibob
 */
public class InitWiz extends HtmlWiz {
	
/**
 * Creates a new instance of NewRecordWiz2 
 */
public InitWiz(java.awt.Frame owner)
throws org.xml.sax.SAXException, java.io.IOException
{
	super(owner, "New Record", true);
	
	KeyedButtonGroup type = new KeyedButtonGroup();
	JRadioButton b;
	b = new JRadioButton("Person");
		b.setOpaque(false);
		type.add("person", b);
		addWidget("person", b);
	b = new JRadioButton("Organization");
		b.setOpaque(false);
		type.add("organization", b);
		addWidget("organization", b);
	html.getMap().put("type", type);
	
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
