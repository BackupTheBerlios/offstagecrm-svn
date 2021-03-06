/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2007 by Robert Fischer

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/*
 * CChoice2.java
 *
 * Created on August 6, 2007, 9:58 PM
 */

package offstage.swing.typed;


import java.awt.CardLayout;
import java.sql.*;
import citibob.app.*;
import javax.swing.*;
import citibob.sql.pgsql.*;
import citibob.sql.*;
import citibob.swing.typed.*;
/**
 *
 * @author  citibob
 */
public class CCChooser extends javax.swing.JPanel
{
int entityid;

static final int T_OLD = 0;		// Tab for old cc info
static final int T_NEW = 1;		// Tab for new cc info

	
	/** Creates new form CChoice2 */
	public CCChooser()
	{
		initComponents();
	}
public void initRuntime(offstage.crypt.KeyRing kr)
{
	oldCard.initRuntime();
	newCard.initRuntime(kr);
}
public void setEntityID(SqlRunner str, int entityid, final App app)
//throws SQLException
{
	this.entityid = entityid;
	String sql =
		" select * from entities where entityid = " + SqlInteger.sql(entityid);
	str.execSql(sql, new RsRunnable() {
	public void run(SqlRunner str, ResultSet rs) throws SQLException {
		rs.next();
		TypedWidgetBinder.setValueRecursive(oldCard, rs, app.getSwingerMap(), app.getSqlTypeSet());
		if (oldCard.isFullySet()) {
			// Old card is good --- give option to use it
			jTabbedPane1.setEnabledAt(T_OLD, true);
			jTabbedPane1.setSelectedIndex(T_OLD);
		} else {
			jTabbedPane1.setEnabledAt(T_OLD, false);
			jTabbedPane1.setSelectedIndex(T_NEW);
		}
		newCard.initValue(oldCard);
		saveCard.setValue(false);

	}});
}

public void saveNewCardIfNeeded(SqlRunner str) throws SQLException
{
	if (saveCard.getBoolValue()) saveNewCard(str);
}

/** Save credit card details back to the database. */
public void saveNewCard(SqlRunner str) throws SQLException
{
	ConsSqlQuery sql = new ConsSqlQuery("entities", ConsSqlQuery.UPDATE);
	sql.addWhereClause("entityid = " + SqlInteger.sql(entityid));
	getNewCard(sql);
	str.execSql(sql.getSql());
}

public void getNewCard(ConsSqlQuery sql)
{
	sql.addColumn("ccname", SqlString.sql(newCard.getCCName()));
	sql.addColumn("cctype", SqlString.sql(newCard.getCCType()));
	sql.addColumn("cclast4", SqlString.sql(newCard.getLast4()));
	sql.addColumn("ccexpdate", SqlString.sql(newCard.getExpDate()));
	sql.addColumn("ccinfo", SqlString.sql(newCard.getValue()));
}
public void getOldCard(ConsSqlQuery sql)
{
	sql.addColumn("ccname", SqlString.sql((String)oldCard.lccname.getValue()));
	sql.addColumn("cctype", SqlString.sql((String)oldCard.lcctype.getValue()));
	sql.addColumn("cclast4", SqlString.sql((String)oldCard.llast4.getValue()));
	sql.addColumn("ccexpdate", SqlString.sql((String)oldCard.lexpdate.getValue()));
	sql.addColumn("ccinfo", SqlString.sql((String)oldCard.lccinfo.getValue()));
}
public void getCard(ConsSqlQuery sql)
{
	switch (jTabbedPane1.getSelectedIndex()) {
		case T_OLD :	// Old card
			getOldCard(sql);
			break;
		case T_NEW :	// New card
			getNewCard(sql);
			break;
	}
}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        jTabbedPane1 = new javax.swing.JTabbedPane();
        oldCard = new offstage.swing.typed.CCInfoLabels();
        jPanel1 = new javax.swing.JPanel();
        newCard = new offstage.swing.typed.JTypedCCInfo();
        saveCard = new citibob.swing.typed.JBoolCheckbox();

        jTabbedPane1.addTab("Card on File", oldCard);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel1.add(newCard, java.awt.BorderLayout.CENTER);

        saveCard.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        saveCard.setText("Save this card for future use.");
        saveCard.setMargin(new java.awt.Insets(0, 0, 0, 0));
        saveCard.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                saveCardActionPerformed(evt);
            }
        });

        jPanel1.add(saveCard, java.awt.BorderLayout.SOUTH);

        jTabbedPane1.addTab("New Card", jPanel1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

	private void saveCardActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveCardActionPerformed
	{//GEN-HEADEREND:event_saveCardActionPerformed
// TODO add your handling code here:
	}//GEN-LAST:event_saveCardActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private offstage.swing.typed.JTypedCCInfo newCard;
    private offstage.swing.typed.CCInfoLabels oldCard;
    private citibob.swing.typed.JBoolCheckbox saveCard;
    // End of variables declaration//GEN-END:variables
	
}
