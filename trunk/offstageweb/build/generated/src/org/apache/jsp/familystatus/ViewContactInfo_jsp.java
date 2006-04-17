package org.apache.jsp.familystatus;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.io.*;
import offstage.web.*;
import offstage.web.collections.*;
import java.util.*;

public final class ViewContactInfo_jsp extends org.apache.jasper.runtime.HttpJspBase
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

Map contactInfo = (Map)sess.getAttribute( "contactInfo" );

ResultSetArrayList phones = (ResultSetArrayList)sess.getAttribute( "phones" );
Iterator i = phones.iterator();

//  extension might be null - if there was no extension in the set of phone numbers
String extension = (String)sess.getAttribute( "extension" );

      out.write("\n");
      out.write("<table>\n");
      out.write("\n");
      out.write("<tr><td colspan=\"2\" >Contact Info[<a href=\"");
      out.print(root);
      out.write("/familystatus/UpdateContactInfo.jsp\">Edit</a>]</td></tr>\n");
      out.write("<tr><td colspan=\"2\"><hr/></td></tr>\n");
      out.write("<tr>\n");
      out.write("<td>Address To</td>\n");
      out.write("<td>\n");
      out.print(contactInfo.get("customaddressto"));
      out.write("\n");
      out.write("</td></tr>\n");
      out.write("\n");
      out.write("<tr><td>Address 1</td><td>\n");
      out.print(contactInfo.get("address1"));
      out.write("\n");
      out.write("</td></tr>\n");
      out.write("\n");
      out.write("<tr><td>Address 2</td><td>\n");
      out.print(contactInfo.get("address2"));
      out.write("\n");
      out.write("</td></tr>\n");
      out.write("\n");
      out.write("<tr><td>City</td>\n");
      out.write("<td>\n");
      out.print(contactInfo.get("city"));
      out.write("\n");
      out.write("</td></tr>\n");
      out.write("\n");
      out.write("<tr><td>State</td>\n");
      out.write("<td>\n");
      out.print(contactInfo.get("state"));
      out.write("\n");
      out.write("</td></tr>\n");
      out.write("\n");
      out.write("<tr><td>Zip</td>\n");
      out.write("<td>\n");
      out.print(contactInfo.get("zip"));
      out.write("\n");
      out.write("</td></tr>\n");
      out.write("\n");
      out.write("<tr><td>Country</td><td>\n");
      out.print(contactInfo.get("country"));
      out.write("\n");
      out.write("</td></tr>\n");
      out.write("\n");
      out.write("<tr>\n");
      out.write("<td>Email</td>\n");
      out.write("<td>\n");
      out.print(contactInfo.get("email"));
      out.write("\n");
      out.write("</td>\n");
      out.write("</tr>\n");
      out.write("\n");
      out.write("<tr><td colspan=\"2\"><hr/></td></tr>\n");
      out.write("<tr>\n");
      out.write("<td colspan=\"2\">Phone Numbers[<a href=\"");
      out.print(root);
      out.write("/familystatus/UpdatePhones.jsp\">Edit</a>]\n");
      out.write("</td></tr>\n");
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

      out.write("\t\n");
      out.write("<tr><td colspan=\"2\"><hr/></td></tr>\n");
      out.write("\n");
      out.write("<tr><td colspan=\"2\">[<a href=\"");
      out.print(root);
      out.write("/FamilyStatus.jsp\">Home</a>]</td></tr>\n");
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
