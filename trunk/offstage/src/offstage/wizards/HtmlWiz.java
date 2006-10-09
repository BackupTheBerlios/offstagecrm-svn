/*
 * HtmlWiz.java
 *
 * Created on October 8, 2006, 5:25 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.wizards;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import citibob.swing.typed.*;
import citibob.swing.html.*;

/**
 *
 * @author citibob
 */
public class HtmlWiz extends HtmlDialog
{

//protected HtmlPanel html;

/**
 * Creates a new instance of HtmlWiz 
 */
public HtmlWiz(Frame owner, String title, boolean modal)
{
	super(owner, title, true);
	this.setSize(600, 400);
	this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

	// Standard Wizard buttons
	addSubmitButton("back", "<< Back");
	addSubmitButton("next", ">> Next");
	addSubmitButton("cancel", "Cancel");
}

	
}
