/*
 * JTypedCCInfo.java
 *
 * Created on August 5, 2007, 10:39 PM
 */

package offstage.swing.typed;

import citibob.swing.html.*;
import com.sun.security.auth.module.Krb5LoginModule;
import java.util.*;
import citibob.swing.typed.*;
import citibob.swing.html.*;
import offstage.types.*;
import javax.swing.*;
import offstage.wizards.*;
import citibob.wizard.*;
import citibob.app.*;
import offstage.schema.*;
import citibob.jschema.*;
import citibob.util.*;
import java.awt.event.*;
import java.awt.*;
import offstage.crypt.*;


/**
 *
 * @author  citibob
 */
public class JTypedCCInfo extends javax.swing.JPanel
implements TypedWidget//, java.beans.PropertyChangeListener
{
	
boolean infocus;		// Does at least one of our sub-items have focus?
boolean valValid;		// Is the value field valid, or does it need to be recomputed?
String val;
String last4;
KeyRing kr;



/** Creates new form JTypedCCInfo */
public JTypedCCInfo()
{
	initComponents();
	cctype.setKeyedModel(offstage.schema.EntitiesSchema.ccTypeModel);
	ccnumber.setJType(String.class, new CCFormatter());
	expdate.setJType(String.class, new ExpDateFormatter());
	ccv.setJType(String.class, new DigitsFormatter(3));
	zip.setJType(String.class, new DigitsFormatter(5));
	ccname.setJType(String.class, new StringFormatter());

	// Figure out when we might need to recompute the value
	FocusListener focus = new FocusListener() {
	    public void focusGained(FocusEvent e) {
			infocus = true;
			valValid = false;
		}
	    public void focusLost(FocusEvent e) {
			infocus = false;
		}
	};
	ccname.addFocusListener(focus);
	cctype.addFocusListener(focus);
	ccnumber.addFocusListener(focus);
	expdate.addFocusListener(focus);
	ccv.addFocusListener(focus);
	zip.addFocusListener(focus);
}

public void initRuntime(KeyRing kr)
{
	this.kr = kr;
}

public void addKeyListener(KeyListener kl) {
	ccname.addKeyListener(kl);
	cctype.addKeyListener(kl);
	ccnumber.addKeyListener(kl);
	expdate.addKeyListener(kl);
	ccv.addKeyListener(kl);
	zip.addKeyListener(kl);	
}

public String getValue()
{
	if (!valValid) makeVal();
	return val;
}
public String getLast4()
{
	String s = (String)ccnumber.getValue();
	if (s == null) return null;
	if (s.length() == 16) return s.substring(12);
	return null;
}
public String getCCName() { return (String)ccname.getValue(); }
public String getCCType() { return (String)cctype.getValue(); }
public String getExpDate() { return (String)expdate.getValue(); }
// ------------------------------------------------
public boolean isNameSet() { return ccname.getValue() != null; }
public boolean isCCTypeSet() { return cctype.getValue() != null; }
public boolean isCCNumberSet() {
	String s = (String)ccnumber.getValue();
	return (s != null && s.length() == 16);
}
public boolean isExpDateSet() {
	String s = (String)expdate.getValue();
	return (s != null && s.length() == 4);	
}
public boolean isCCVSet() {
	String s = (String)ccv.getValue();
	return (s != null && s.length() == 3);	
}
public boolean isZipSet() {
	String s = (String)zip.getValue();
	return (s != null && s.length() == 5);
}
public boolean isFullySet() {
	return isNameSet() && isCCTypeSet() && isCCNumberSet() &&
		isExpDateSet() && isCCVSet() && isZipSet();
}
// ------------------------------------------------

void makeVal()
{
	TypedHashMap map = new TypedHashMap();
	map.put("cctype", cctype.getValue());
	map.put("ccnumber", ccnumber.getValue());
	map.put("expdate", expdate.getValue());
	map.put("ccv", ccv.getValue());
	map.put("zip", zip.getValue());
	val = CCEncoding.encode(map);
	
	// Encrypt the value
	try {
		val = kr.encrypt(val);
	} catch(Exception e) {
		// kr == null, or public key not loaded.
		// Either way, don't leak any unencrypted information.
		val = null;
	}		
}

// ================================================================

/** Returns last legal value of the widget.  Same as method in JFormattedTextField */
//public Object getValue() { return val; }

/** Sets the value.  Same as method in JFormattedTextField.  Fires a
 * propertyChangeEvent("value") when calling setValue() changes the value. */
public void setValue(Object o) {
	if (o == null) {
		// Clear it out...
		cctype.setValue(null);
		ccnumber.setValue(null);
		expdate.setValue(null);
		ccv.setValue(null);
		zip.setValue(null);
	} else {
		TypedHashMap map = CCEncoding.decode((String)o);
		cctype.setValue(map.getString("cctype"));
		ccnumber.setValue(map.getString("ccnumber"));
		expdate.setValue(map.getString("expdate"));
		ccv.setValue(map.getString("ccv"));
		zip.setValue(map.getString("zip"));
	}
}

public void initValue(String xccname, String xcctype, String xexpdate)
{
	setValue(null);
	ccname.setValue(xccname);
	cctype.setValue(xcctype);
	expdate.setValue(xexpdate);
}
/** From TableCellEditor (in case this is being used in a TableCellEditor):
 * Tells the editor to stop editing and accept any partially edited value
 * as the value of the editor. The editor returns false if editing was not
 * stopped; this is useful for editors that validate and can not accept
 * invalid entries. */
public boolean stopEditing() {
	// Not to be used in a table cell...
	return false;
}

/** Is this object an instance of the class available for this widget?
 * If so, then setValue() will work.  See SqlType.. */
public boolean isInstance(Object o) {
	if (o == null) return true;
	return (o instanceof String);
	// Maybe try parsing the info...
}

/** Set up widget to edit a specific SqlType.  Note that this widget does not
 have to be able to edit ALL SqlTypes... it can throw a ClassCastException
 if asked to edit a SqlType it doesn't like. */
public void setJType(citibob.swing.typed.Swinger f) throws ClassCastException
{  }

String colName;

/** Row (if any) in a RowModel we will bind this to at runtime. */
public String getColName() { return colName; }

/** Row (if any) in a RowModel we will bind this to at runtime. */
public void setColName(String col) { this.colName = col; }

// ===========================================================
///** Pass along change in value from underlying typed widget. */
//public void propertyChange(java.beans.PropertyChangeEvent evt) {
////	firePropertyChange("value", evt.getOldValue(), evt.getNewValue());
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

        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        expdate = new citibob.swing.typed.JTypedTextField();
        ccnumber = new citibob.swing.typed.JTypedTextField();
        ccv = new citibob.swing.typed.JTypedTextField();
        zip = new citibob.swing.typed.JTypedTextField();
        cctype = new citibob.swing.typed.JKeyedComboBox();
        ccname = new citibob.swing.typed.JTypedTextField();
        jLabel7 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Name on Card:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        add(jLabel2, gridBagConstraints);

        jLabel3.setText("CC Number:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        add(jLabel3, gridBagConstraints);

        jLabel4.setText("Exp Date:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        add(jLabel4, gridBagConstraints);

        jLabel5.setText("CCV Code:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        add(jLabel5, gridBagConstraints);

        jLabel6.setText("Zip Code (5 digits):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        add(jLabel6, gridBagConstraints);

        expdate.setText("jTypedTextField2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        add(expdate, gridBagConstraints);

        ccnumber.setText("jTypedTextField4");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        add(ccnumber, gridBagConstraints);

        ccv.setText("jTypedTextField5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        add(ccv, gridBagConstraints);

        zip.setText("jTypedTextField6");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        add(zip, gridBagConstraints);

        cctype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cctype.setPreferredSize(new java.awt.Dimension(68, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        add(cctype, gridBagConstraints);

        ccname.setText("jTypedTextField4");
        ccname.setColName("ccname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        add(ccname, gridBagConstraints);

        jLabel7.setText("CC Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        add(jLabel7, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private citibob.swing.typed.JTypedTextField ccname;
    private citibob.swing.typed.JTypedTextField ccnumber;
    private citibob.swing.typed.JKeyedComboBox cctype;
    private citibob.swing.typed.JTypedTextField ccv;
    private citibob.swing.typed.JTypedTextField expdate;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private citibob.swing.typed.JTypedTextField zip;
    // End of variables declaration//GEN-END:variables


public static void main(String[] args)
{
	JFrame f = new JFrame();
	f.setLayout(new FlowLayout());
	final JTypedCCInfo ccinfo = new JTypedCCInfo();
	f.getContentPane().add(ccinfo);
	JButton jb = new JButton();
	jb.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
			String s =ccinfo.getValue();
			System.out.println(s);
			ccinfo.setValue(s);
		}
	});
	f.getContentPane().add(jb);
	f.pack();
	f.show();
}
	
}
