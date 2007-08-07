/*
 * NVPairs.java
 *
 * Created on August 3, 2007, 12:16 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.crypt;

import java.util.*;

/**
 *
 * @author citibob
 */
public class NVEncoding {

static String encode(String[] headers, String[] vals)
{
	StringBuffer sb = new StringBuffer();
	int nfield = headers.length;
	for (int i=0; i<nfield; ++i) {
		if (vals[i] != null) {
			sb.append(headers[i]);
			sb.append(":");
			sb.append(vals[i]);
			sb.append(";");
		}
	}
	return sb.toString();
}
static Map decode(String s)
{
	HashMap map = new HashMap();
	String[] pairs = s.split(";");
	if (pairs.length < 1) return map;
	for (int i=0; i<pairs.length; ++i) {
		String p = pairs[i];
		int sc = p.indexOf(':');
		if (sc > 0) map.put(p.substring(0,sc), p.substring(sc+1));
	}
	return map;
}

}
