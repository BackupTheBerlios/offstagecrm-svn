/*
Offstage CRM: Enterprise Database for Arts Organizations
This file Copyright (c) 2006 by Robert Fischer

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
/*
 * EntityPanel.java
 *
 * Created on June 5, 2005, 9:48 AM
 */

package offstage.gui;


import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import citibob.sql.*;
import citibob.jschema.*;
import citibob.multithread.*;
import citibob.jschema.swing.*;
//import citibob.jschema.swing.JSchemaWidgetTree;
import citibob.swing.table.*;
import citibob.swing.typed.*;
import java.awt.*;
import java.awt.event.*;
import offstage.FrontApp;
import offstage.db.FullEntityDbModel;
/**
 *
 * @author  citibob
 */
public class EntityPanel extends javax.swing.JPanel {
	
	/** Creates new form EntityPanel */
	public EntityPanel() {
		initComponents();
	}

	EntityPanel getThis() { return this; }
	
	public void initRuntime(Statement st, ActionRunner runner, FullEntityDbModel dm, SwingerMap smap)
	throws java.sql.SQLException
	{
		mainPanel.initRuntime(st, runner, dm, smap);
		donationsPanel.initRuntime(st, dm.getDonationSb(),
			new String[] {"Type", "Date", "Amount"},
			new String[] {"groupid", "date", "amount"}, smap);
		eventsPanel.initRuntime(st, dm.getEventsSb(),
			new String[] {"Event", "Role"},
			new String[] {"groupid", "role"}, smap);
		notesPanel.initRuntime(st, dm.getNotesSb(),
			new String[] {"Type", "Date", "Note"},
			new String[] {"groupid", "date", "note"}, smap);
		ticketsPanel.initRuntime(st, dm.getTicketsSb(),
			new String[] {"Event", "Type", "#Tix", "Payment"},
			new String[] {"groupid", "tickettypeid", "numberoftickets", "payment"}, smap);
		interestsPanel.initRuntime(st, dm.getInterestsSb(),
			new String[] {"Interest", "By Person", "Referred By"},
			new String[] {"groupid", "byperson", "referredby"}, smap);
		classesPanel.initRuntime(st, dm.getClassesSb(),
			new String[] {"Class", "Comments"},
			new String[] {"groupid", "comments"}, smap);
		dm.addListener(new FullEntityDbModel.Adapter() {
		public void entityTypeChanged(int type) {
			CardLayout cl = (CardLayout)(mainPanel.getLayout());
			switch(type) {
				case FullEntityDbModel.ORG :
					cl.show(mainPanel, "org");
				break;
				case FullEntityDbModel.PERSON :
					cl.show(mainPanel, "person");
				break;
			}
		}});
	}	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        info.clearthought.layout.TableLayout _tableLayoutInstance;

        groupPanels = new javax.swing.JTabbedPane();
        donationsPanel = new offstage.gui.GroupPanel();
        eventsPanel = new offstage.gui.GroupPanel();
        notesPanel = new offstage.gui.GroupPanel();
        ticketsPanel = new offstage.gui.GroupPanel();
        interestsPanel = new offstage.gui.GroupPanel();
        classesPanel = new offstage.gui.GroupPanel();
        mainPanel = new offstage.gui.MainEntityPanel();

        groupPanels.setPreferredSize(new java.awt.Dimension(458, 200));
        groupPanels.addTab("Donations", donationsPanel);

        groupPanels.addTab("Events", eventsPanel);

        groupPanels.addTab("Notes", notesPanel);

        groupPanels.addTab("Tickets", ticketsPanel);

        groupPanels.addTab("Interests", interestsPanel);

        groupPanels.addTab("Classes", classesPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
            .add(groupPanels, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 749, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(mainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(groupPanels, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private offstage.gui.GroupPanel classesPanel;
    private offstage.gui.GroupPanel donationsPanel;
    private offstage.gui.GroupPanel eventsPanel;
    private javax.swing.JTabbedPane groupPanels;
    private offstage.gui.GroupPanel interestsPanel;
    private offstage.gui.MainEntityPanel mainPanel;
    private offstage.gui.GroupPanel notesPanel;
    private offstage.gui.GroupPanel ticketsPanel;
    // End of variables declaration//GEN-END:variables

//	public static void main(String[] args) throws Exception
//    {
//
//
//		FrontApp app = new FrontApp();
//		FullEntityDbModel dm = app.getFullEntityDm();
//		Statement st = app.getPool().checkout().createStatement();
//
//		dm.setKey(139208);
//		dm.doSelect(st);
//		System.out.println("Type = " + dm.getEntityType());
//
//		EntityPanel personPanel = new EntityPanel();
//		personPanel.initRuntime(st, dm);//personRM, dm.getPhonesSb());
//
//		
//		
//	    JFrame frame = new JFrame();
//	    frame.getContentPane().add(personPanel);
//		frame.pack();
//	    frame.setVisible(true);
//		System.out.println("Done");
//    }
	
}
