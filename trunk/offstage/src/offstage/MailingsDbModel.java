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
 * MailingDbModel.java
 *
 * Created on July 11, 2005, 10:31 PM
 */

package offstage;
import citibob.jschema.*;
import java.sql.*;
import java.sql.*;
import java.io.*;
import net.sf.jasperreports.engine.*;
import java.util.*;
import javax.swing.event.*;
import offstage.db.DB;
import offstage.schema.MailingsSchema;
import offstage.schema.MailingidsSchema;

/**
 *
 * @author citibob
 */
public class MailingsDbModel extends MultiDbModel {

IntKeyedDbModel mailingids;		// Set to just one record (for 1 mailing)
IntKeyedDbModel mailings;		// Set to entire mailing info
int mailingID;					// Current mailing ID
Statement st;

public SchemaBuf getMailingsSb()
	{ return mailings.getSchemaBuf(); }
public SchemaBuf getMailingidsSb()
	{ return mailingids.getSchemaBuf(); }

public void setKey(int mailingID)
{
	this.mailingID = mailingID;
	mailingids.setKey(mailingID);
	mailings.setKey(mailingID);
}

/** Creates a new instance of MailingDbModel */
public MailingsDbModel(final Statement st) {
	this.st=st;
	add(mailings = new IntKeyedDbModel(new MailingsSchema(), "groupid", false));
	mailings.setInstantUpdate(st, true);
	add(mailingids = new IntKeyedDbModel(new MailingidsSchema(), "groupid", false));
	mailingids.setInstantUpdate(st, true);

	// Refresh mailingids table when this changes.
	mailingids.getSchemaBuf().addTableModelListener(new TableModelListener() {
	public void tableChanged(TableModelEvent e) {
		try {
			mailingids.doSelect(st);
		} catch(SQLException ee) {
			ee.printStackTrace(System.out);
		}
	}});
}

public void makeReport() throws SQLException, JRException
{
	ResultSet rs = null;
	InputStream in = null;
	try {
		DB.w_mailings_makereport(st, mailingID);

		in = Object.class.getResourceAsStream("/jmbt/front/reports/AddressLabels.jasper");
		rs = st.executeQuery("select * from mailings" +
				" where groupid=" + mailingID +
				" and isgood = 't'" +
				" order by country, zip");
		HashMap params = new HashMap();
		JRResultSetDataSource jrdata = new JRResultSetDataSource(rs);
		JasperPrint jprint = net.sf.jasperreports.engine.JasperFillManager.fillReport(in, params, jrdata);
		net.sf.jasperreports.view.JasperViewer.viewReport(jprint, false);
	} finally {
		try { rs.close(); } catch(Exception e) {}
		try { in.close(); } catch(Exception e) {}		
	}

	
}

}
