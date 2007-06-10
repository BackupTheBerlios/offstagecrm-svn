/*
 * OffstageWizard.java
 *
 * Created on October 12, 2006, 9:10 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.wizards;

import citibob.swing.html.*;
import citibob.swing.prefs.*;
import javax.swing.*;
import java.sql.*;
import offstage.db.*;
import offstage.*;
import java.util.prefs.*;
import citibob.wizard.*;
import java.awt.*;

/**
 *
 * @author citibob
 */
public class OffstageWizard extends citibob.swing.SwingWizard
{

protected FrontApp fapp;
java.util.prefs.Preferences wizardPref;		// Root node for this wizard
SwingPrefs swingPrefs = new SwingPrefs();

//	public Wiz createWiz() throws Exception { return newWiz(); }		// Override this to post-process wiz after it's created

protected abstract class OState extends citibob.swing.SwingWizard.State
{
	public Wiz createWiz() throws Exception
	{
		// Overridden to post-process wiz after it's created
		Wiz wiz = super.createWiz();
		Preferences wizPref = wizardPref.node(name);
		swingPrefs.setPrefs((Component)wiz, "", wizPref);
		return wiz;
	}
	public abstract void process() throws Exception;
	
	public OState(String name, String back, String next) {
		super(name, back, next);
	}
}


    /** Creates a new instance of OffstageWizard */
    public OffstageWizard(String wizardName, FrontApp fapp, java.awt.Frame frame, String startState)
	{
		super(wizardName, frame, startState);
//		wizardPref = Preferences.userRoot();
		wizardPref = fapp.userRoot().node("wizard").node(wizardName);
		this.fapp = fapp;
    }

}
