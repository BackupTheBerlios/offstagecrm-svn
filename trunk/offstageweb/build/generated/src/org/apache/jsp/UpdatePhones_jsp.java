package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.io.*;
import offstage.web.*;
import offstage.web.collections.*;
import java.util.*;

public final class UpdatePhones_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(1);
    _jspx_dependants.add("/jsp_h.jsp");
  }

  public java.util.List getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    JspFactory _jspxFactory = null;
    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      _jspxFactory = JspFactory.getDefaultFactory();
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write('\n');

String root = request.getContextPath();
HttpSession sess = request.getSession();
//PrintWriter out = response.getWriter();   // already provided

      out.write('\n');
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");

ResultSetArrayList phones = (ResultSetArrayList)sess.getAttribute( "phones" );
Iterator i = phones.iterator();

//  extension might be null - if there was no extension in the set of phone numbers
String extension = (String)sess.getAttribute( "extension" );

      out.write("\n");
      out.write("\n");
      out.write("<table>\n");
      out.write("<tr>\n");
      out.write("<td colspan=\"2\">Phone Numbers</td>\n");
      out.write("</tr>\n");
      out.write("<tr><td colspan=\"2\"><hr/></td></tr>\n");

    while ( i.hasNext() ){
        Map phone = (Map)i.next();
        String name = (String)phone.get("name");
        String number = (String)phone.get("phone");
        if ( name.compareTo("extension") != 0 ){

      out.write("\n");
      out.write("<tr><td>");
      out.print(name);
      out.write("</td><td>");
      out.print(number);
      out.write('\n');

            // If extension exists attach it to the work number
            if ( name.compareTo("work") == 0 && extension != null ){

      out.write("\n");
      out.write(" ext. ");
      out.print(extension);
      out.write('\n');

            }

      out.write("\n");
      out.write("</td></tr>\n");

        }
    }

      out.write("\n");
      out.write("<tr><td colspan=\"2\"><hr/></td></tr>\n");
      out.write("\n");
      out.write("<tr><td colspan=\"2\">\n");
      out.write("[<a href=\"");
      out.print(root);
      out.write("/AddPhones.jsp\">Add Phone</a>]\n");
      out.write("[<a href=\"");
      out.print(root);
      out.write("/RemovePhones.jsp\">Remove Phone</a>]\n");
      out.write("</td></tr>\n");
      out.write("\n");
      out.write("<tr><td colspan=\"2\"><hr/></td></tr>\n");
      out.write("<tr><td colspan=\"2\">[<a href=\"");
      out.print(root);
      out.write("/FamilyStatus.jsp\">Home</a>]</td></tr>\n");
      out.write("</table>");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      if (_jspxFactory != null) _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
