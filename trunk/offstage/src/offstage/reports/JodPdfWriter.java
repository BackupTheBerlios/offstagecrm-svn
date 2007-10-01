/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2007 by Robert Fischer

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package offstage.reports;

import com.artofsolving.jodconverter.*;
import com.artofsolving.jodconverter.openoffice.connection.*;
import com.artofsolving.jodconverter.openoffice.converter.*;
import net.sf.jooreports.templates.*;
import java.io.*;
import java.util.*;
import com.pdfhacks.*;

/**
 *
 * @author citibob
 */
public class JodPdfWriter
{

static final int E_MAIN = 0;
static final int E_TEMPLATE = 1;
static final int E_OO = 2;
static final int E_CONCAT = 3;
Throwable[] exceptions = new Throwable[4];

OOConnect ooConnect;
DocumentFormatRegistry registry;
ConcatPdfWriter concat;		// null if no concatenation
Thread concatThread;
String outExt;
int nReports;				// # of times we've been written to
// -----------------------------------------------------------------
// ---------------------------------------------------------------
public JodPdfWriter(String oofficeExe, String outExt) throws IOException, InterruptedException
{
	this.outExt = outExt;
	registry = new XmlDocumentFormatRegistry(); 
	ooConnect = new OOConnect(oofficeExe);
	concat = null;			// One-by-one output files
}

public JodPdfWriter(String oofficeExe, OutputStream pdfOut, String outExt) throws IOException, InterruptedException
{
	this(oofficeExe, outExt);
	concat = new ConcatPdfWriter(pdfOut);
}

public void close() throws IOException
{
	// Must check for concatThread==null; otherwise, concat.close() will hang.
	if (concat != null && concatThread == null) concat.close();
	ooConnect.close();
}

public int getNumReports() { return nReports; }

public void writeReport(final InputStream templateIn, String ext, final Object dataModel)
throws IOException, DocumentTemplateException, com.lowagie.text.DocumentException, InterruptedException
{
	final Exception[] exp = new Exception[1];
	try {
		// Set up pipes to copy to concat
		final PipedInputStream pin2 = new PipedInputStream();
		final PipedOutputStream pout2 = new PipedOutputStream(pin2);
		concatThread = new Thread() { public void run() {
			try {
				System.out.println("Concat Running");
				concat.writePdfDoc(pin2);
				pin2.close();
				concat.flush();
				System.out.println("Concat done running");
			} catch(Exception e) {
				// throws IOException, com.lowagie.text.DocumentException
				exp[0] = e;
			}
		}};
		concatThread.setDaemon(true);
		concatThread.start();

		++nReports;
		writeReport(templateIn, ext, dataModel, pout2);
		
		pout2.close();		// Flush out, allows concatThread to finish.
		concatThread.join();	// Wait for concatenation to finish before moving on
		concatThread = null;
	} finally {
		if (exp[0] != null) {
			if (exp[0] instanceof IOException) throw (IOException) exp[0];
			if (exp[0] instanceof com.lowagie.text.DocumentException)
				throw (com.lowagie.text.DocumentException) exp[0];
		}
	}
}
	
private void writeReport(final InputStream templateIn, String ext, final Object dataModel, final OutputStream out)
throws IOException, DocumentTemplateException
{
	final Exception[] exp = new Exception[1];
	try {
		// Set up pipes and basic filling-in of the template
		final PipedInputStream pin = new PipedInputStream();
		final PipedOutputStream pout = new PipedOutputStream(pin);
		new Thread() { public void run() {
			try {
				DocumentTemplate template = new ZippedDocumentTemplate(templateIn);
				template.createDocument(dataModel, pout);
			} catch(Exception e) {
				// throws IOException, DocumentTemplateException
				exp[0] = e;
			}
	//		catch(IOException e) {
	//			exp[0] = e;
	//		} catch(DocumentTemplateException de) {
	//			exp[]
	////			exp[0] = e;
	////			e.printStackTrace();
	//		}
		}}.start();

		// Use our OOo connection to translate the document
		DocumentConverter converter = new OpenOfficeDocumentConverter(ooConnect.getConnection(), registry); 
		DocumentFormat odt = registry.getFormatByFileExtension(ext);
		DocumentFormat pdf = registry.getFormatByFileExtension(outExt); 
		converter.convert(pin, odt, out, pdf);

		// Close the pipes
		pin.close();
		pout.close();
	} finally {
		if (exp[0] != null) {
			if (exp[0] instanceof IOException) throw (IOException) exp[0];
			if (exp[0] instanceof DocumentTemplateException) throw (DocumentTemplateException) exp[0];
		}
	}
}


//public void exec(final InputStream[] templateIn, final Object[] dataModel, final OutputStream pdfOut) throws Exception
//{
//	try {
//		
//		// Set up pipes for concatenating PDF files
//		
//			
//		for (int i=0; i<templateIn.length; ++i) {
//			System.out.println("Processing file " + i);
//
//
//		}
//		concat.close();
//	} catch(Exception e) {
//		throw new Exception(
//			exceptions[0].getMessage() + "\n" +
//			exceptions[1].getMessage() + "\n" +
//			exceptions[2].getMessage() + "\n");
//	} finally {
//		try {
//			closeOOo();
//		} catch(Exception e) {
//			System.out.println("Exception closing: ");
//			e.printStackTrace();
//			throw e;
//		}
//	}
//}


// ======================================================================

public static void main(String[] args) throws Exception
{
//	System.out.println("PID = " + windowsGetOOProcessID());
//	doTest("c:\\Program Files\\OpenOffice.org 2.2\\program\\soffice.bin");
	doTest("soffice");
}
public static void doTest(String oofficeExe) throws Exception
{
//	File indir = new File("h:\\svn\\offstage\\reports");
//	File indir = new File("/Volumes/citibob/svn/offstage/reports");
	File indir = new File("/export/home/citibob/svn/offstage/reports");
	File outdir = new File(".");
	final Map data = new HashMap();
	data.put("name", "Joe");
	
	ArrayList items = new ArrayList();
	Map i1 = new HashMap();
		i1.put("firstname", "Martha");
		i1.put("lastname", "Magpie");
		items.add(i1);
	i1 = new HashMap();
		i1.put("firstname", "Joe");
		i1.put("lastname", "Schmoe");
		items.add(i1);		
	data.put("items", items);
	
	OutputStream pdfOut = new FileOutputStream(new File(outdir, "test1-out.pdf"));
	JodPdfWriter jout = new JodPdfWriter(oofficeExe, pdfOut, "pdf");
	try {
		jout.writeReport(new FileInputStream(new File(indir, "test1.odt")), "odt", data);
		jout.writeReport(new FileInputStream(new File(indir, "test1.odt")), "odt", data);
	} catch(Exception e) {
            e.printStackTrace();
        } finally {
		jout.close();
	}
}
}
