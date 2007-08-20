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

//OutputStream pdfOut;		// Current place where we send our PDF files
DocumentFormatRegistry registry;
ConcatPdfWriter concat;		// null if no concatenation
Thread concatThread;

// -----------------------------------------------------------------
// ---------------------------------------------------------------
public JodPdfWriter(String oofficeExe) throws IOException, InterruptedException
{
	registry = new XmlDocumentFormatRegistry(); 
	connectOOo(oofficeExe);
	concat = null;			// One-by-one output files
}

public JodPdfWriter(String oofficeExe, OutputStream pdfOut) throws IOException, InterruptedException
{
	this(oofficeExe);
	concat = new ConcatPdfWriter(pdfOut);
}

public void close() throws IOException
{
	// Must check for concatThread==null; otherwise, concat.close() will hang.
	if (concat != null && concatThread == null) concat.close();
	closeOOo();
}

public void writeReport(final InputStream templateIn, final Object dataModel)
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

		writeReport(templateIn, dataModel, pout2);

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
	
public void writeReport(final InputStream templateIn, final Object dataModel, final OutputStream out)
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
		DocumentConverter converter = new OpenOfficeDocumentConverter(connection, registry); 
		DocumentFormat odt = registry.getFormatByFileExtension("odt"); 
		DocumentFormat pdf = registry.getFormatByFileExtension("pdf"); 
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


// ====================================================================
// OOo connection
OpenOfficeConnection connection;
Process proc;
int ooPid = -1;			// PID of the pre-existing OpenOffice.org process (Windows)
InputStream stderr;

void connectOOo(String oofficeExe) throws IOException, InterruptedException
{
	String osName = System.getProperty("os.name");
	if (osName.startsWith("Windows")) {
		ooPid = windowsGetOOProcessID();
	}
	
	proc = Runtime.getRuntime().exec(
		oofficeExe + " -headless -norestore -nodefault -nolockcheck -accept=socket,port=8100;urp;StarOffice.ServiceManager");
	stderr = proc.getErrorStream();

	// Handle the OOo server we just started.
	Thread ooThread = new Thread() {
	public void run() {
		try {
			String line = null;
			int c;
			while ((c = stderr.read()) > 0) ;
			proc.destroy();
		} catch (Throwable t) {
			exceptions[E_OO] = t;
			t.printStackTrace();
		}
		System.out.println("ooThread exiting");
	}};
	ooThread.setDaemon(true);
	ooThread.start();
	
	// connect to the OpenOffice.org instance we just started or ressucitated
	for (int i=0; ; ++i) {
		try {
			System.out.println("Connection try " + i);
			connection = new SocketOpenOfficeConnection("127.0.0.1", 8100);
			connection.connect();
			break;
		} catch(IOException e) {
			if (i < 10) Thread.currentThread().sleep(1000);
			else throw e;
		}
	}

}

void closeOOo() throws IOException
{
	// close the connection to OOo
	connection.disconnect();

	// Only kill if OO wasn't already running when we started
	// because if OOo WAS running when we started, it will exit
	// on its own.  NOTE: "Killing" this on Unix/Linux doesn't hurt,
	// so we don't have to bother setting ooPid above for Unix.
	if (ooPid < 0) {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			windowsKillOpenOffice();
		} else {
			stderr.close();
			proc.destroy();
		}
	}
}
// ========================================================================
// See: http://www.oooforum.org/forum/viewtopic.phtml?p=59246

/**
* Kill OpenOffice.
*
* Supports Windows XP, Solaris (SunOS) and Linux.
* Perhaps it supports more OS, but it has been tested
* only with this three.
*
* @throws IOException  Killing OpenOffice didn't work
*/
public static void killOpenOffice() throws IOException
{
   String osName = System.getProperty("os.name");
   if (osName.startsWith("Windows"))
   {
	  windowsKillOpenOffice();
   }
   else if (osName.startsWith("SunOS") || osName.startsWith("Linux"))
   {
	  unixKillOpenOffice();
   }
   else
   {
	  throw new IOException("Unknown OS, killing impossible");
   }
}

/**
* Kill OpenOffice on Windows XP.
*/
private static void windowsKillOpenOffice() throws IOException
{
	Runtime.getRuntime().exec("tskill soffice");
}

/**
* Kill OpenOffice on Unix.
*/
private static void unixKillOpenOffice() throws IOException
{
   Runtime runtime = Runtime.getRuntime();

   String pid = unixGetOOProcessID();
   if (pid != null)
   {
	  while (pid != null)
	  {
		 String[] killCmd = {"/bin/bash", "-c", "kill -9 "+pid};
		 runtime.exec(killCmd);

		 // Is another OpenOffice prozess running?
			pid = unixGetOOProcessID();
	  }
   }
}

/**
* Get OpenOffice prozess id. (Unix/Linux)
*/
private static String unixGetOOProcessID() throws IOException
{
   Runtime runtime = Runtime.getRuntime();

   // Get prozess id
   String[] getPidCmd = {"/bin/bash", "-c", "ps -e|grep soffice|awk '{print $1}'"};
   Process getPidProcess = runtime.exec(getPidCmd);

   // Read prozess id
   InputStreamReader isr = new InputStreamReader(getPidProcess.getInputStream());
   BufferedReader br = new BufferedReader(isr);

   return br.readLine();
}

private static int windowsGetOOProcessID() throws IOException
{
   Runtime runtime = Runtime.getRuntime();

   // Get prozess id
   String[] getPidCmd = {"qprocess"};
   Process getPidProcess = runtime.exec(getPidCmd);

   // Read prozess id
   InputStreamReader isr = new InputStreamReader(getPidProcess.getInputStream());
   BufferedReader br = new BufferedReader(isr);
   String line;
   int ooPid = -1;
   while ((line = br.readLine()) != null) {
	   int space2 = line.lastIndexOf(' ');
	   if (space2 < 0) continue;
	   int space1 = space2-1;
	   while (line.charAt(space1) == ' ') --space1;
	   int space0 = space1-1;
	   while (line.charAt(space0) != ' ') --space0;
	   String spname = line.substring(space2+1);
	   if ("soffice.bin".equals(spname)) {
		   String spid = line.substring(space0+1, space1+1);
		   ooPid = Integer.parseInt(spid);
	   }
   }
   getPidProcess.destroy();
   return ooPid;
}
// ======================================================================

public static void main(String[] args) throws Exception
{
//	System.out.println("PID = " + windowsGetOOProcessID());
	doTest("c:\\Program Files\\OpenOffice.org 2.2\\program\\soffice.bin");
}
public static void doTest(String oofficeExe) throws Exception
{
        File indir = new File("h:\\svn\\offstage\\reports");
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
	JodPdfWriter jout = new JodPdfWriter(oofficeExe, pdfOut);
	try {
		jout.writeReport(new FileInputStream(new File(indir, "test1.odt")), data);
		jout.writeReport(new FileInputStream(new File(indir, "test1.odt")), data);
	} catch(Exception e) {
            e.printStackTrace();
        } finally {
		jout.close();
	}
}
}
