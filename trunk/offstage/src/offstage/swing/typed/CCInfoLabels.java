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
import citibob.swing.typed.*;
import java.awt.*;

/**
 *
 * @author  citibob
 */
public class CCInfoLabels extends javax.swing.JPanel implements BindContainer
{

KeyRing kr;
String oldccinfo;
JDialog popupDialog;

Component[] bindComponents;
public java.awt.Component[] getBindComponents() { return bindComponents; }

/** Creates new form JEncCCInfo */
public CCInfoLabels()
{
	initComponents();
	bindComponents = new Component[] {lccinfo};
	clear();
}

public void initRuntime()
{
	lcctype.setJType(offstage.schema.EntitiesSchema.ccTypeModel, "<none>");
	llast4.setJType(new JavaJType(String.class),
		new StringFormatter());
	lexpdate.setJType(new citibob.swing.typed.JavaJType(String.class),
		new offstage.types.ExpDateFormatter());
	lccinfo.setJType(new JavaJType(String.class),
		JTypedTextField.newFormatterFactory(new StringFormatter()));
}

public void clear()
{
	lcctype.setValue(null);
	llast4.setValue(null);
	lexpdate.setValue(null);
}

public boolean isFullySet()
{
	return lccinfo.getValue() != null;
}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        lccinfo = new citibob.swing.typed.JTypedTextField();
        jLabel10 = new javax.swing.JLabel();
        lcctype = new citibob.swing.typed.JTypedLabel();
        jLabel11 = new javax.swing.JLabel();
        llast4 = new citibob.swing.typed.JTypedLabel();
        jLabel12 = new javax.swing.JLabel();
        lexpdate = new citibob.swing.typed.JTypedLabel();
        jLabel13 = new javax.swing.JLabel();
        lccname = new citibob.swing.typed.JTypedLabel();

        lccinfo.setText("jTypedTextField1");
        lccinfo.setColName("ccinfo");

        setLayout(new java.awt.GridBagLayout());

        jLabel10.setText("CC Type: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
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

    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    protected citibob.swing.typed.JTypedTextField lccinfo;
    protected citibob.swing.typed.JTypedLabel lccname;
    protected citibob.swing.typed.JTypedLabel lcctype;
    protected citibob.swing.typed.JTypedLabel lexpdate;
    protected citibob.swing.typed.JTypedLabel llast4;
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
