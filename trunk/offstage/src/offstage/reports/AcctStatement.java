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

/** Get a listing of students for each parent */
public static HashMap<Integer,String> getStudentNames(App app, Statement st, int termid, int payerid)
throws SQLException
{
	HashMap<Integer,String> map = new HashMap();
	String sql =
		" select es.adultid, p.lastname, p.firstname" +
		" from persons p, entities_school es\n" +
		" where p.entityid = es.entityid\n" +
		" and es.entityid <> es.adultid" +
//		" and tr.termid = " + SqlInteger.sql(termid) +
		(payerid < 0 ? "" : " and es.adultid = " + SqlInteger.sql(payerid)) +
		" order by es.adultid";
System.out.println("sql");
	RSTableModel mod = new RSTableModel(app.getSqlTypeSet());
	mod.executeQuery(st, sql);
	TableModelGrouper tmg = new TableModelGrouper(mod, new String[] {"adultid"});
	JTypeTableModel tt;
	while ((tt = tmg.next()) != null) {
		for (int i=0; i<tt.getRowCount(); ++i) {
			String fname = (String)tt.getValueAt(i,2) + " " + (String)tt.getValueAt(i,1);
			Integer id = (Integer)tt.getValueAt(i,0);
			String names = map.get(id);
			if (names == null) {
				names = fname;
			} else {
				names = names + ", " + fname;
			}
			map.put(id, names);
		}
	}
	return map;
}

public static String getSql(int termid, int payerid, int actypeid)
{
	return
		" select act.*," +
		" p.cclast4, p.firstname || ' ' || p.lastname as payername" +
		" from actrans act, persons p" +		// p is payer
		" where act.entityid = p.entityid" +
		" and actypeid = " + SqlInteger.sql(actypeid) +
		(payerid < 0 ? "" : " and act.entityid = " + SqlInteger.sql(payerid)) +
//		" and act.termid = " + SqlInteger.sql(termid) + "\n" +
		" order by p.lastname, p.firstname, act.entityid, act.date, act.actransid";
}
	
public static List<HashMap<String,Object>> makeJodModels(App app, Statement st, int termid, int payerid, java.util.Date today)
throws SQLException
{
	HashMap<Integer,String> studentMap = getStudentNames(app, st, termid, payerid);
	
	String sterm = SQL.readString(st, "select name from termids where groupid = " + termid);
	List<HashMap<String,Object>> models = new ArrayList();

	String sql = getSql(termid, payerid, ActransSchema.AC_SCHOOL);
System.out.println(sql);
	RSTableModel rsmod = new RSTableModel(app.getSqlTypeSet());
		rsmod.executeQuery(st, sql);

//	// Do basic query
//	IntKeyedDbModel actransDb = new IntKeyedDbModel(app.getSchema("actrans"), "entityid");
//	actransDb.setWhereClause(" actypeid = " + SqlInteger.sql(ActransSchema.AC_SCHOOL));
//	actransDb.setOrderClause("entityid, date, actransid");
//	actransDb.setKey(payerid);		// Could be -1, would mean all payers
//	actransDb.doSelect(st);
////	SchemaBuf sb = actransDb.getSchemaBuf();

	// Group it by payer...
	String[] gcols = new String[] {"entityid"};
	TableModelGrouper group = new TableModelGrouper(rsmod, gcols);
	JTypeTableModel sb;
	while ((sb = group.next()) != null) {
	
		HashMap<String,Object> data = new HashMap();
		models.add(data);
		Integer PayerID = (Integer)sb.getValueAt(0, sb.findColumn("entityid"));

		// Add on account balance
		BalTableModel bal = new BalTableModel(sb.getRowCount());
		int amtcol = sb.findColumn("amount");
		int desccol = sb.findColumn("description");
		double dbal = 0;
		double sumtuition = 0;
		double sumfees = 0;
		double sumpayments = 0;
		double sumscholarship = 0;
		String paymenttype = "";
		for (int i=0; i<sb.getRowCount(); ++i) {
			// Set balance
			double amt = (Double)sb.getValueAt(i, amtcol);
			dbal += amt;
			bal.setValueAt(dbal, i, 0);

			// Correct description if there is none
			String desc = (String)sb.getValueAt(i, desccol);
			if (amt < 0 && (desc == null || "".equals(desc.trim()))) {
				desc = "Payment, Thank You!";
				sb.setValueAt(desc, i, desccol);
			}
			
			// Sort into tuition, registration, payment records
			if (amt < 0) sumpayments += amt;
			else if (desc != null) {
				if (desc.contains("Fee")) sumfees += amt;
				if (desc.contains("Tuition")) sumtuition += amt;
				if (desc.contains("Scholarship")) sumscholarship += amt;
			}
			
			// Set payment type
			if (sb.getValueAt(i, sb.findColumn("cclast4")) != null) {
				paymenttype = "cc";
			}
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
		NumberFormat mfmt = NumberFormat.getCurrencyInstance();
		double overdue = (rs0.getRowCount() == 0 ? 0 : (Double)rs0.getValueAt(rs0.getRowCount()-1, balcol));
		data.put("overdue", overdue <= 0 ? mfmt.format(0.0D) : mfmt.format(overdue));
		double paynow = (Double)rs1.getValueAt(rs1.getRowCount()-1, balcol);
		data.put("paynow", paynow <= 0 ? mfmt.format(0.0D) : mfmt.format(paynow));
		data.put("sumtuition", mfmt.format(Math.abs(sumtuition)));
		data.put("sumfees", mfmt.format(Math.abs(sumfees)));
		data.put("sumscholarship", sumscholarship == 0 ? "" : mfmt.format(Math.abs(sumscholarship)));
		data.put("sumpayments", mfmt.format(Math.abs(sumpayments)));
		data.put("balance", mfmt.format(Math.abs(dbal)));
		data.put("paymenttype", paymenttype);
		
		// Add misc stuff
		data.put("sterm", sterm);
		String studentName = studentMap.get(PayerID);
		data.put("studentname", studentName == null ? "<none>" : studentName);
		data.put("payername", rs0.getValueAt(0, mod.findColumn("payername")));
		DateFormat dfmt = new SimpleDateFormat("MMM dd, yyyy");
			dfmt.setTimeZone(app.getTimeZone());
		data.put("date", dfmt.format(today));
		data.put("duedate", (rs1.getRowCount() == 0 ? "" :
			dfmt.format((java.util.Date)rs1.getValueAt(0,dtcol))));
	}
	return models;
}
public static void doAccountStatements(FrontApp fapp, Statement st, int termid, int payerid, java.util.Date dt)
throws Exception
{
	if (dt == null) dt = new java.util.Date();
	List models = makeJodModels(fapp, st, termid, payerid, fapp.sqlDate.truncate(dt));
	ReportOutput.viewJodReport(fapp, "AcctStatement.odt", models);
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
public static void main(String[] args) throws Exception
{
	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
	Statement st = pool.checkout().createStatement();
	FrontApp fapp = new FrontApp(pool,null);
	
	int termid = 346;
	AcctStatement.doAccountStatements(fapp, st, termid, 12633, new java.util.Date());
}
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
