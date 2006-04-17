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
package offstage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import java.io.*;
import net.sf.jasperreports.engine.*;
import citibob.jschema.SchemaBufDbModel;
import java.util.*;
import offstage.db.FullEntityDbModel;

public class Main
{
public static void main(String[] args) throws Exception
{
	
	FrontApp app = null;//new FrontApp(null);
	FullEntityDbModel dm = app.getFullEntityDm();
	Statement st = app.getPool().checkout().createStatement();

	InputStream in = Object.class.getResourceAsStream("/jmbt/front/reports/AddressLabels.jasper");
	ResultSet rs = st.executeQuery("select * from mailings where groupid=250");
	HashMap params = new HashMap();
	JRResultSetDataSource jrdata = new JRResultSetDataSource(rs);
	JasperPrint jprint = net.sf.jasperreports.engine.JasperFillManager.fillReport(in, params, jrdata);
	rs.close();
	in.close();
	net.sf.jasperreports.view.JasperViewer.viewReport(jprint, false);
	
	// net.sf.jasperreports.view.JasperDesignViewer.viewReportDesign(in, false);
//	in.close();
//	net.sf.jasperreports.view.JasperDesignViewer.viewReportDesign("build/reports/AddressLabels.jasper", false);
			
/*	
	FrontApp app = new FrontApp();
	FullEntityDbModel dm = app.getFullEntityDm();
	Statement st = app.createStatement();

	dm.setKey(146135);
	dm.doSelect(st);
	System.out.println("Type = " + dm.getEntityType());

	dm.setKey(125874);
	dm.doSelect(st);
	System.out.println("Type = " + dm.getEntityType());

	dm.setKey(17);
	dm.doSelect(st);
	System.out.println("Type = " + dm.getEntityType());

	st.close();
*/
			
// 	// Open the database
// 	Class.forName("org.postgresql.Driver");
// 	Connection db = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/jmbt", "jmbt", "fiercecookie");
// 	Statement st = db.createStatement();
// 
// 	SchemaBufDbModel dm = new OnePersonDbModel();
// 	dm.setKey(
// 	dm.doSelect(st);
// //	dm.getSchemaBuf().setValueAt("Funny Name", 0, 1);
// //	dm.doUpdate(st);
// 
// 	db.close();
}
}
