/*
 * CCEncoding.java
 *
 * Created on August 3, 2007, 12:18 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.crypt;

import citibob.wizard.*;

/**
 *
 * @author citibob
 */
public class CCEncoding {

static final String[] headers = {"ct", "cc", "ex", "cv", "zp"};
static final String[] fields = {"cctype", "ccnumber", "expdate", "seccode", "zip"};

static String encode(TypedHashMap v)
{
	String[] val = new String[fields.length];
	for (int i=0; i<fields.length; ++i) {
		val[i] = v.getString(fields[i]);
	}
	return NVEncoding.encode(headers, val);
}

static TypedHashMap decode(String s)
{
//	HashMap m
	return null;
}

}
