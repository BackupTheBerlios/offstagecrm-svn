/*
 * PhoneSwinger.java
 *
 * Created on October 8, 2006, 4:50 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.types;

/**
 *
 * @author citibob
 */
public class PhoneSwinger extends citibob.swing.typed.TypedTextSwinger
{

public PhoneSwinger()
{
	super(new SqlPhone());
}
/** Creates an AbstractFormatterFactory for a JFormattedTextField.  If this
 SqlType is never to be edited with a JFormattedTextField, it can just
 return null.  NOTE: This should return a new instance of AbstractFormatterFactory
 because one instance is required per JFormattedTextField.  It's OK for the
 factory to just store instances of 4 AbstractFormatters and return them as needed. */
public javax.swing.text.DefaultFormatterFactory newFormatterFactory()
{
	PhoneFormatter nff = new PhoneFormatter();
	return new javax.swing.text.DefaultFormatterFactory(nff);
}
	
}
