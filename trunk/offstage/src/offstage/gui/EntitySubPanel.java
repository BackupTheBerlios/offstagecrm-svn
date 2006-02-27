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
 * PersonPanel.java
 *
 * Created on February 9, 2005, 3:04 PM
 */

package offstage.gui;

import java.sql.*;
import citibob.jschema.*;
import offstage.db.FullEntityDbModel;

/**
 *
 * @author  citibob
 */
public class EntitySubPanel extends javax.swing.JPanel {
FullEntityDbModel dbModel;
	
	/** Creates new form PersonPanel */
	public EntitySubPanel() {
		initComponents();
	}

/*
public void initRuntime(SchemaBuf schemaBuf)
{
//	dbModel = app.getFullEntityDm();
//	TableRowModel personRm = new TableRowModel(dbModel.getPersonSb());

	// TableRowModel is an adapter between TableModel and single-row GUI widgets.
	TableRowModel rm = new TableRowModel(schemaBuf);

	// Bind to the SchemaBuf we were given.
	JSchemaWidgetTree.bindToSchemaRow(this, schemaBuf.getSchema(), rm);

//	Statement st = app.createStatement();
//	st.close();

}
*/
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents
        info.clearthought.layout.TableLayout _tableLayoutInstance;

        jLabel1 = new javax.swing.JLabel();
        address1 = new citibob.jschema.swing.JSchemaTextField();
        address2 = new citibob.jschema.swing.JSchemaTextField();
        city = new citibob.jschema.swing.JSchemaTextField();
        state = new citibob.jschema.swing.JSchemaTextField();
        zip = new citibob.jschema.swing.JSchemaTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        entityid = new citibob.jschema.swing.JSchemaTextField();
        lastupdated = new citibob.jschema.swing.JSchemaTextField();
        relprimarytypeid = new offstage.gui.RelPrimaryTypesIntComboBox();
        jLabel4 = new javax.swing.JLabel();

        _tableLayoutInstance = new info.clearthought.layout.TableLayout();
        _tableLayoutInstance.setHGap(0);
        _tableLayoutInstance.setVGap(0);
        _tableLayoutInstance.setColumn(new double[]{info.clearthought.layout.TableLayout.FILL,30,info.clearthought.layout.TableLayout.PREFERRED});
        _tableLayoutInstance.setRow(new double[]{info.clearthought.layout.TableLayout.PREFERRED,info.clearthought.layout.TableLayout.PREFERRED,info.clearthought.layout.TableLayout.PREFERRED,info.clearthought.layout.TableLayout.PREFERRED,info.clearthought.layout.TableLayout.PREFERRED,info.clearthought.layout.TableLayout.PREFERRED,info.clearthought.layout.TableLayout.PREFERRED,info.clearthought.layout.TableLayout.PREFERRED});
        setLayout(_tableLayoutInstance);

        setPreferredSize(new java.awt.Dimension(200, 0));
        jLabel1.setText("Address");
        add(jLabel1, new info.clearthought.layout.TableLayoutConstraints(0, 0, 2, 0, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

        address1.setColName("address1");
        address1.setPreferredSize(new java.awt.Dimension(4, 100));
        add(address1, new info.clearthought.layout.TableLayoutConstraints(0, 1, 2, 1, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

        address2.setColName("address2");
        add(address2, new info.clearthought.layout.TableLayoutConstraints(0, 2, 2, 2, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

        city.setColName("city");
        add(city, new info.clearthought.layout.TableLayoutConstraints(0, 3, 0, 3, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

        state.setColName("state");
        add(state, new info.clearthought.layout.TableLayoutConstraints(1, 3, 1, 3, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

        zip.setColName("zip");
        zip.setPreferredSize(new java.awt.Dimension(80, 19));
        add(zip, new info.clearthought.layout.TableLayoutConstraints(2, 3, 2, 3, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

        jLabel2.setText("ID");
        add(jLabel2, new info.clearthought.layout.TableLayoutConstraints(0, 4, 0, 4, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

        jLabel3.setText("Last Modified");
        add(jLabel3, new info.clearthought.layout.TableLayoutConstraints(1, 4, 2, 4, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

        entityid.setEditable(false);
        entityid.setColName("entityid");
        add(entityid, new info.clearthought.layout.TableLayoutConstraints(0, 5, 0, 5, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

        lastupdated.setEditable(false);
        lastupdated.setColName("lastupdated");
        add(lastupdated, new info.clearthought.layout.TableLayoutConstraints(1, 5, 2, 5, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

        relprimarytypeid.setColName("relprimarytypeid");
        relprimarytypeid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                relprimarytypeidActionPerformed(evt);
            }
        });

        add(relprimarytypeid, new info.clearthought.layout.TableLayoutConstraints(0, 7, 2, 7, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

        jLabel4.setText("Relationship to Primary");
        add(jLabel4, new info.clearthought.layout.TableLayoutConstraints(0, 6, 2, 6, info.clearthought.layout.TableLayout.FULL, info.clearthought.layout.TableLayout.FULL));

    }//GEN-END:initComponents

private void relprimarytypeidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_relprimarytypeidActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_relprimarytypeidActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private citibob.jschema.swing.JSchemaTextField address1;
    private citibob.jschema.swing.JSchemaTextField address2;
    private citibob.jschema.swing.JSchemaTextField city;
    private citibob.jschema.swing.JSchemaTextField entityid;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private citibob.jschema.swing.JSchemaTextField lastupdated;
    private offstage.gui.RelPrimaryTypesIntComboBox relprimarytypeid;
    private citibob.jschema.swing.JSchemaTextField state;
    private citibob.jschema.swing.JSchemaTextField zip;
    // End of variables declaration//GEN-END:variables
	
}
