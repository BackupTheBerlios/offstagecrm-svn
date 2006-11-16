/*
 * NewRecordWiz2.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.wizards.modify;

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
public class FindWiz extends HtmlWiz {

///** Should this Wiz screen be cached when "Back" is pressed? */
//public boolean getCacheWiz() { return false; }
SearchViewer listView;

/**
 * Creates a new instance of NewRecordWiz2 
 */
public FindWiz(java.awt.Frame owner, java.sql.Statement st, FrontApp fapp)
throws org.xml.sax.SAXException, java.io.IOException, java.sql.SQLException
{
	super(owner, "Possible Duplicate Name", true);

	listView = new SearchViewer();
	listView.initRuntime(st, fapp);
//	html.getMap().put("idlistviewer", listView);
	html.addWidget("searchview", listView);
	this.setSize(new java.awt.Dimension(750, 550));
	loadHtml();
}

public int getEntityID()
{ return listView.getDisplayedEntityID(); }

}
