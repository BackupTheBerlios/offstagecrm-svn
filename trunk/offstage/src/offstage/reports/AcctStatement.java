/*
 * StudentSchedule.java
 *
 * Created on August 9, 2007, 12:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import com.artofsolving.jodconverter.*;
import com.artofsolving.jodconverter.openoffice.connection.*;
import com.artofsolving.jodconverter.openoffice.converter.*;
import net.sf.jooreports.templates.*;
import java.io.*;
import java.util.*;
import com.pdfhacks.*;
import citibob.sql.pgsql.*;
import java.sql.*;
import offstage.*;
import citibob.sql.*;
import citibob.swing.table.*;
import citibob.text.*;
import citibob.app.*;
import citibob.jschema.*;
import offstage.schema.*;
import citibob.swing.typed.*;
import java.text.*;

/**
 *
 * @author citibob
 */
public class AcctStatement
{

public static String getSql(int termid, int payerid, int actypeid)
{
	return
		" select act.*, firstname || ' ' || lastname as fullname" +
		" from actrans act, persons p" +
		" where act.entityid = p.entityid" +
		" and actypeid = " + SqlInteger.sql(actypeid) +
		(payerid < 0 ? "" : " and act.entityid = " + SqlInteger.sql(payerid)) +
		" and act.termid = " + SqlInteger.sql(termid);
}
	
public static List<HashMap<String,Object>> makeJodModels(App app, Statement st, int termid, int payerid, java.util.Date today)
throws SQLException
{
	String sterm = SQL.readString(st, "select name from termids where groupid = " + termid);
	List<HashMap<String,Object>> models = new ArrayList();

	String sql = getSql(termid, payerid, ActransSchema.AC_SCHOOL);
System.out.println(sql);
	RSTableModel rsmod = new RSTableModel(app.getSqlTypeSet());
		rsmod.executeQuery(st, sql);
		
	// Do basic query
	IntKeyedDbModel actransDb = new IntKeyedDbModel(app.getSchema("actrans"), "entityid");
	actransDb.setWhereClause(" actypeid = " + SqlInteger.sql(ActransSchema.AC_SCHOOL));
	actransDb.setOrderClause("entityid, date, actransid");
	actransDb.setKey(payerid);		// Could be -1, would mean all payers
	actransDb.doSelect(st);
//	SchemaBuf sb = actransDb.getSchemaBuf();

	// Group it by payer...
	String[] gcols = new String[] {"entityid"};
	TableModelGrouper group = new TableModelGrouper(actransDb.getSchemaBuf(), gcols);
	JTypeTableModel sb;
	while ((sb = group.next()) != null) {
	
		HashMap<String,Object> data = new HashMap();
		models.add(data);

		// Add on account balance
		BalTableModel bal = new BalTableModel(sb.getRowCount());
		int amtcol = sb.findColumn("amount");
		double dbal = 0;
		for (int i=0; i<sb.getRowCount(); ++i) {
			dbal += (Double)sb.getValueAt(i, amtcol);
			bal.setValueAt(dbal, i, 0);
		}
		MultiJTypeTableModel mod = new MultiJTypeTableModel(
			new JTypeTableModel[] {sb, bal});

		// Scan for first row after today
	//	java.util.Date tomorrow = new java.util.Date(today.getTime() + 86400*1000L);
		int dtcol = mod.findColumn("date");
	//	java.util.Date splitDt;
		int i=0;
		int split = 0;

		// Past charges
		for (; i< mod.getRowCount(); ++i) {
			java.util.Date dti = (java.util.Date)mod.getValueAt(i, dtcol);
			if (dti.getTime() >= today.getTime() + 86400*1000L-1L) break;
		}
		SubrowTableModel rs0 = new SubrowTableModel(mod, split, i);
		split = i;

		// Current charges
		SubrowTableModel rs1 = null;
		if (i < mod.getRowCount()) {
			java.util.Date splitDt = (java.util.Date)mod.getValueAt(i, dtcol);
			for (; i< mod.getRowCount(); ++i) {
				java.util.Date dti = (java.util.Date)mod.getValueAt(i, dtcol);
				if (dti.getTime() >= splitDt.getTime() + 86400*1000L-1L) break;
			}
			rs1 = new SubrowTableModel(mod, split, i);
			split = i;
		} else {
			rs1 = new SubrowTableModel(mod, i,i);
		}

		// Future charges
		SubrowTableModel rs2 = new SubrowTableModel(mod, split, mod.getRowCount());

		// Split into things owed and things not yet owed
		data.put("rs0", new TemplateTableModel(new StringTableModel(rs0, app.getSFormatterMap())));
		data.put("rs1", new TemplateTableModel(new StringTableModel(rs1, app.getSFormatterMap())));
		data.put("rs2", new TemplateTableModel(new StringTableModel(rs2, app.getSFormatterMap())));

		// Add totals...
		int balcol = mod.findColumn("balance");
		// TODO: This will throw exception if no rows...
		double overdue = (Double)rs0.getValueAt(rs0.getRowCount()-1, balcol);
		data.put("overdue", overdue <= 0 ? "0" : overdue);
		data.put("paynow", rs1.getValueAt(rs1.getRowCount()-1, balcol));
	//	data.put("remit", rs0.getValueAt(rs0.getRowCount()-1, balcol));

		// Add misc stuff
		data.put("sterm", sterm);
		data.put("sname", rs0.getValueAt(0, mod.findColumn("fullname")));
		DateFormat dfmt = new SimpleDateFormat("MMM dd, yyyy");
			dfmt.setTimeZone(app.getTimeZone());
		data.put("date", dfmt.format(today));
	}
	return models;
}
public static void doAccountStatements(FrontApp fapp, Statement st, int termid, int payerid, java.util.Date dt)
throws Exception
{
	if (dt == null) dt = new java.util.Date();
	List models = makeJodModels(fapp, st, termid, payerid, fapp.sqlDate.truncate(dt));
	ReportOutput.viewJodReport(fapp, "AcctStatment.odt", models);
}
//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	Statement st = pool.checkout().createStatement();
//	FrontApp fapp = new FrontApp(pool,null);
//
//	java.util.Calendar cal = new java.util.GregorianCalendar();
//		cal.set(Calendar.HOUR_OF_DAY, 0);
//		cal.set(Calendar.MINUTE, 0);
//		cal.set(Calendar.SECOND, 0);
//		cal.set(Calendar.MILLISECOND, 0);
//	
//	JodPdfWriter jout = new JodPdfWriter("/Applications/NeoOffice.app/Contents/program/soffice", new FileOutputStream("x.pdf"));
//	try {
//		HashMap data;
//		data = makeJodModel(fapp, st, 8, 12633, cal.getTime());
//		jout.writeReport(ReportOutput.openTemplateFile(fapp, "AcctStatement.odt"), data);
//		cal.add(Calendar.MONTH, 1);
//		data = makeJodModel(fapp, st, 8, 12633, cal.getTime());
//		jout.writeReport(ReportOutput.openTemplateFile(fapp, "AcctStatement.odt"), data);
//	} finally {
//		jout.close();
//	}
//	
//	Runtime.getRuntime().exec("acroread x.pdf");
//}
////public static void doTest(String oofficeExe) throws Exception
//public static void main(String[] args) throws Exception
//{
//	
//	OutputStream pdfOut = new FileOutputStream(new File(dir, "test1-out.pdf"));
//	JodPdfWriter jout = new JodPdfWriter(oofficeExe, pdfOut);
//	try {
//		jout.writeReport(new FileInputStream(new File(dir, "test1.odt")), data);
//		jout.writeReport(new FileInputStream(new File(dir, "test1.odt")), data);
//	} finally {
//		jout.close();
//	}
//}
// ================================================================
static class BalTableModel extends javax.swing.table.DefaultTableModel implements JTypeTableModel
{
	public BalTableModel(int nrow) {
		super(new String[] {"balance"}, nrow);
	}
	JType jString = new JavaJType(String.class);
	public JType getJType(int row, int col) { return jString; }
}
}
