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
public class OOConnect
{

// OOo connection
OpenOfficeConnection connection;
Process proc;
int ooPid = -1;			// PID of the pre-existing OpenOffice.org process (Windows)
InputStream stderr;
File runsofficeTmp;		// Temporary file created on Macintosh...

OpenOfficeConnection getConnection() { return connection; }


// ---------------------------------------------------------------
public OOConnect(String oofficeExe) throws IOException, InterruptedException
{
	String osName = System.getProperty("os.name");
	if (osName.startsWith("Windows")) {
		ooPid = windowsGetOOProcessID();
	} else if (osName.startsWith("Mac")) {
		ooPid = macGetOOProcessID();
	}

	String cmdOptions = " -headless -norestore -nodefault -nolockcheck '-accept=socket,port=8100;urp;StarOffice.ServiceManager'";
	String cmd = oofficeExe + cmdOptions;
	if (osName.startsWith("Mac")) {
		System.out.println("OOConnect: cmd = " + cmd);
		runsofficeTmp = File.createTempFile("runsoffice", ".sh");
		runsofficeTmp.deleteOnExit();
		FileWriter fout = new FileWriter(runsofficeTmp);
		fout.write(cmd);
		fout.write('\n');
		fout.close();
		proc = Runtime.getRuntime().exec("sh " + runsofficeTmp.getPath());
	} else {		// Windows, Linux
		cmd = cmd.replace("'", "");
		System.out.println("OOConnect: cmd = " + cmd);
		proc = Runtime.getRuntime().exec(cmd);
	}
	stderr = proc.getErrorStream();

	// Handle the OOo server we just started.
	Thread ooThread = new Thread() {
	public void run() {
		try {
			String line = null;
			int c;
			while ((c = stderr.read()) > 0) System.out.print(c);;
			proc.destroy();
		} catch (Throwable t) {
			//exceptions[E_OO] = t;
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

void close() throws IOException
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
		} else if (osName.startsWith("Mac")) {
			macKillOpenOffice();
			runsofficeTmp.delete();
		} else {
			stderr.close();
			proc.destroy();
		}
	}
}
// ========================================================================
// See: http://www.oooforum.org/forum/viewtopic.phtml?p=59246


/**
* Kill OpenOffice on Windows XP.
*/
private static void windowsKillOpenOffice() throws IOException
{
	Runtime.getRuntime().exec("tskill soffice");
}

private static void macKillOpenOffice() throws IOException
{
   Runtime runtime = Runtime.getRuntime();

   int pid = macGetOOProcessID();
	while (pid >= 0) {
		String killCmd = "kill -9 "+pid;
		runtime.exec(killCmd);

		// Is another OpenOffice prozess running?
		pid = macGetOOProcessID();
	}
}

private static int macGetOOProcessID() throws IOException
{
   Runtime runtime = Runtime.getRuntime();

   // Get prozess id
   Process getPidProcess = runtime.exec("ps ax");

	// Read prozess id
	// Read prozess id
	InputStreamReader isr = new InputStreamReader(getPidProcess.getInputStream());
	BufferedReader br = new BufferedReader(isr);
	String line;
	int ooPid = -1;
	while ((line = br.readLine()) != null) {
		if (!line.contains("soffice")) continue;
		int start = 0;
		for (; line.charAt(start) == ' '; ++start);
		int end = start;
		for (; line.charAt(end) != ' '; ++end);
		ooPid = Integer.parseInt(line.substring(start, end));
	}
	getPidProcess.destroy();
	return ooPid;
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

}
