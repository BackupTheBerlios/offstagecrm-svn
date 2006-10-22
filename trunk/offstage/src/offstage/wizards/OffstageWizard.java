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
import javax.swing.*;
import java.sql.*;
import offstage.db.*;
import offstage.*;

/**
 *
 * @author citibob
 */
public class OffstageWizard extends Wizard
{

protected FrontApp fapp;

    /** Creates a new instance of OffstageWizard */
    public OffstageWizard(String wizardName, FrontApp fapp, java.awt.Frame frame, String startState)
	{
		super(wizardName, frame, startState);
		this.fapp = fapp;
    }

}
