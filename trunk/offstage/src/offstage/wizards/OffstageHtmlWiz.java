/*
 * OffstageHtmlWiz.java
 *
 * Created on June 24, 2007, 7:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.wizards;

import citibob.swing.html.*;
import java.util.*;
import citibob.swing.typed.*;
import citibob.swing.html.*;
import offstage.types.*;
import javax.swing.*;
import offstage.wizards.*;
import citibob.wizard.*;
import offstage.gui.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author citibob
 */
public class OffstageHtmlWiz extends HtmlWiz
{
	
public OffstageHtmlWiz(Frame owner, String title, boolean modal)
{ super(owner, title, modal); }

public EntitySelector addEntitySelector(String name, citibob.app.App app)
{ return addEntitySelector(name, new EntitySelector(), app); }

/** Adds an EntitySelector JPanel object, one that submits on double-click.  Convenience method. */
public EntitySelector addEntitySelector(final String name, EntitySelector sel, citibob.app.App app)
{
	sel.initRuntime(app);
	// Double-clicking will go to selected person
	sel.getSearchTable().addMouseListener(new MouseAdapter() {
	public void mouseClicked(MouseEvent e) {
		if( e.getClickCount() != 2 ) return;
		setSubmit(name);
		setVisible(false);
	}});
	html.addWidget(name, sel);
	return sel;
}

}
