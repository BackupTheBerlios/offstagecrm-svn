/*
 * PhoneFormatter.java
 *
 * Created on October 8, 2006, 4:01 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.types;

import javax.swing.*;
import java.text.*;

/**
 *
 * @author citibob
 */
public class PhoneFormatter extends JFormattedTextField.AbstractFormatter
{
	
	
static int countDigits(String s)
{
	int n=0;
	for (int i=0; i<s.length(); ++i) {
		char c = s.charAt(i);
		if (c >= '0' && c <= '9') ++n;
	}
	return n;
}

static String removeNondigits(String s, int ndigits)
{
	StringBuffer ret = new StringBuffer(ndigits);
	for (int i=0; i<s.length(); ++i) {
		char c = s.charAt(i);
		if (c >= '0' && c <= '9') ret.append(c);
	}
	return ret.toString();
}

public static String unformat(String text)
{
	if (text == null) return null;
	if (countDigits(text) != 10) return text;
	return removeNondigits(text, 10);
}

/**
 * Parses <code>text</code> returning an arbitrary Object. Some
 * formatters may return null.
 *
 * @throws ParseException if there is an error in the conversion
 * @param text String to convert
 * @return Object representation of text
 */
public Object stringToValue(String text) throws
ParseException
{
	return unformat(text);
//	if (text == null) return null;
//	if (countDigits(text) != 10) return text;
//	return removeNondigits(text, 10);
}

/**
 * Returns the string value to display for <code>value</code>.
 *
 * @throws ParseException if there is an error in the conversion
 * @param value Value to convert
 * @return String representation of value
 */
public String valueToString(Object value) throws
ParseException
{
	if (value == null) return null;
	String text = (String)value;
	if (countDigits(text) != 10) return text;
	
	String digits = removeNondigits(text, 10);
	return digits.substring(0,3) + "-" + digits.substring(3,6) + "-" + digits.substring(6);
}

	
}
