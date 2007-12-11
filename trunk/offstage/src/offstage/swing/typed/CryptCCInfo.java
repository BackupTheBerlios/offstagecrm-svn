/*
 * JEncCCInfo.java
 *
 * Created on August 6, 2007, 12:23 AM
 */

package offstage.swing.typed;

import offstage.crypt.*;
import offstage.*;
import javax.swing.*;
import java.awt.event.*;
import citibob.types.*;
import java.awt.*;
import citibob.swing.typed.*;

/**
 *
 * @author  citibob
 */
public class CryptCCInfo extends javax.swing.JPanel implements BindContainer
{

String oldccinfo;
JDialog popupDialog;

Component[] bindComponents;
public java.awt.Component[] getBindComponents() { return bindComponents; }

/** Creates new form JEncCCInfo */
public CryptCCInfo()
{
	initComponents();
	bindComponents = new Component[] {lccinfo};
	
	// Pressing ENTER will initiate search.
	ccinfo.addKeyListener(new KeyAdapter() {
	public void keyTyped(KeyEvent e) {
		//System.out.println(e.getKeyChar());
		if (e.getKeyChar() == '\n') bOKActionPerformed(null);
	}});

	
}

public void initRuntime(KeyRing kr)
{
	ccinfo.initRuntime(kr);
	lcctype.setJType(offstage.schema.EntitiesSchema.ccTypeModel, "<none>");
	llast4.setJTypeString();
	lexpdate.setJType(String.class, new offstage.types.ExpDateSFormat());
	lccinfo.setJTypeString();
	clear();
}
String defaultName;
String defaultZip;
public void setDefaults(String name, String zip)
{
	defaultName = name;
	defaultZip = zip;
}

public void clear()
{
	lcctype.setValue(null);
	llast4.setValue(null);
	lexpdate.setValue(null);
}

protected void showPopup()
{
	String xname = (String)lccname.getValue();
		if (xname == null) xname = defaultName;
	ccinfo.initValue(xname, (String)lcctype.getValue(), (String)lexpdate.getValue(), defaultZip);
	
//	oldccinfo = ccinfo.getValue();
	popupDialog = new JDialog((java.awt.Frame)citibob.swing.WidgetTree.getRoot(this));
	popupDialog.setLocationRelativeTo(this);
	popupDialog.setTitle("Credit Card Info");
	popupDialog.setContentPane(popupPanel);
	popupDialog.pack();
	popupDialog.setVisible(true);
//	popupDialog.addWindowListener(new WindowAdapter() {
//	    public void windowClosing(WindowEvent e) {
//			ccinfo.setValue(oldccinfo);		// Undo any edits.
//		}
//	});
}

//public void initRuntime(FrontApp fapp)
//{
//	kr = fapp.getKeyRing();
//}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        popup = new javax.swing.JPopupMenu();
        popupPanel = new javax.swing.JPanel();
        ccinfo = new offstage.swing.typed.JTypedCCInfo();
        jPanel1 = new javax.swing.JPanel();
        bOK = new javax.swing.JButton();
        lccinfo = new citibob.swing.typed.JTypedLabel();
        jLabel10 = new javax.swing.JLabel();
        lcctype = new citibob.swing.typed.JTypedLabel();
        jLabel11 = new javax.swing.JLabel();
        llast4 = new citibob.swing.typed.JTypedLabel();
        jLabel12 = new javax.swing.JLabel();
        lexpdate = new citibob.swing.typed.JTypedLabel();
        jLabel13 = new javax.swing.JLabel();
        lccname = new citibob.swing.typed.JTypedLabel();
        jPanel2 = new javax.swing.JPanel();
        bSetCC = new javax.swing.JButton();
        bClearCC = new javax.swing.JButton();

        popupPanel.setLayout(new java.awt.BorderLayout());

        popupPanel.setPreferredSize(new java.awt.Dimension(300, 170));
        popupPanel.add(ccinfo, java.awt.BorderLayout.CENTER);

        bOK.setText("OK");
        bOK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bOKActionPerformed(evt);
            }
        });

        jPanel1.add(bOK);

        popupPanel.add(jPanel1, java.awt.BorderLayout.SOUTH);

        lccinfo.setText("jTypedLabel1");
        lccinfo.setColName("ccinfo");

        setLayout(new java.awt.GridBagLayout());

        jLabel10.setText("CC Type: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(jLabel10, gridBagConstraints);

        lcctype.setText("jTypedLabel1");
        lcctype.setColName("cctype");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(lcctype, gridBagConstraints);

        jLabel11.setText("Last 4: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        add(jLabel11, gridBagConstraints);

        llast4.setText("jTypedLabel1");
        llast4.setColName("cclast4");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(llast4, gridBagConstraints);

        jLabel12.setText("Exp Date: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        add(jLabel12, gridBagConstraints);

        lexpdate.setText("jTypedLabel1");
        lexpdate.setColName("ccexpdate");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(lexpdate, gridBagConstraints);

        jLabel13.setText("Name onCard: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        add(jLabel13, gridBagConstraints);

        lccname.setText("jTypedLabel1");
        lccname.setColName("ccname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(lccname, gridBagConstraints);

        bSetCC.setText("Set CC Details");
        bSetCC.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bSetCCActionPerformed(evt);
            }
        });

        jPanel2.add(bSetCC);

        bClearCC.setText("Clear CC");
        bClearCC.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bClearCCActionPerformed(evt);
            }
        });

        jPanel2.add(bClearCC);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jPanel2, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

	private void bClearCCActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bClearCCActionPerformed
	{//GEN-HEADEREND:event_bClearCCActionPerformed
		this.clear();
		lccname.setValue(null);
// TODO add your handling code here:
	}//GEN-LAST:event_bClearCCActionPerformed

	private void bOKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bOKActionPerformed
	{//GEN-HEADEREND:event_bOKActionPerformed
		if (!ccinfo.isFullySet()) {
			JOptionPane.showMessageDialog(popupDialog,
				"Please fill in all credit card fields completely.");
		} else {
			popupDialog.setVisible(false);
			ccinfo.makeVal();
			if (ccinfo.getValue() == null) {
				JOptionPane.showMessageDialog(this,
					"Error encrypting credit card info.");
			} else {
				lccinfo.setValue(ccinfo.getValue());
				lccname.setValue(ccinfo.getCCName());
				lcctype.setValue(ccinfo.getCCType());
				lexpdate.setValue(ccinfo.getExpDate());
				llast4.setValue(ccinfo.getLast4());
			}
//			ccinfo.fireValueChanged(oldccinfo);
//	System.out.println("ccinfo.value = " + ccinfo.getValue());
		}
// TODO add your handling code here:
	}//GEN-LAST:event_bOKActionPerformed

	private void bSetCCActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSetCCActionPerformed
	{//GEN-HEADEREND:event_bSetCCActionPerformed
		showPopup();
	}//GEN-LAST:event_bSetCCActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bClearCC;
    private javax.swing.JButton bOK;
    private javax.swing.JButton bSetCC;
    private offstage.swing.typed.JTypedCCInfo ccinfo;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    protected citibob.swing.typed.JTypedLabel lccinfo;
    private citibob.swing.typed.JTypedLabel lccname;
    private citibob.swing.typed.JTypedLabel lcctype;
    private citibob.swing.typed.JTypedLabel lexpdate;
    private citibob.swing.typed.JTypedLabel llast4;
    private javax.swing.JPopupMenu popup;
    private javax.swing.JPanel popupPanel;
    // End of variables declaration//GEN-END:variables

public static void main(String[] args)
{
	JFrame f = new javax.swing.JFrame();
	f.setLayout(new java.awt.FlowLayout());
	final CryptCCInfo ccinfo = new CryptCCInfo();
	f.getContentPane().add(ccinfo);
	JButton jb = new javax.swing.JButton();
//	jb.addActionListener(new java.awt.event.ActionListener() {
//	    public void actionPerformed(ActionEvent e) {
//			String s =ccinfo.getValue();
//			System.out.println(s);
//			ccinfo.setValue(s);
//		}
//	});
	f.getContentPane().add(jb);
	f.pack();
	f.show();
}

}
