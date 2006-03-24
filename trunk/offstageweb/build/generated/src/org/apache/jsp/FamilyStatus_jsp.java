package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.io.*;
import java.sql.*;
import citibob.sql.*;
import offstage.web.*;
import offstage.web.collections.*;
import java.util.*;

public final class FamilyStatus_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  static {
    _jspx_dependants = new java.util.Vector(2);
    _jspx_dependants.add("/db_h.jsp");
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

      out.write("<!-- header for JSPs that use the database. -->\n");
      out.write('\n');

String root = request.getContextPath();
HttpSession sess = request.getSession();
//PrintWriter out = response.getWriter();   // already provided

      out.write('\n');
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");

Connection dbb = (Connection)request.getAttribute("db");
Statement st = (Statement)request.getAttribute("st");

      out.write('\n');
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");

    // Change this when add in log in process
    // Log in process will set primaryentityid in the session
//    int primaryentityid = 12633;
    int primaryentityid = 6056;
    sess.setAttribute( "primaryentityid", new Integer(primaryentityid) );

    try {
        boolean hasCardBalance = DB.hasCardBalance( st, primaryentityid );
System.out.println("has card balance is: " + hasCardBalance);
        double cardBalance = 0.0;
        if ( hasCardBalance )
            cardBalance = DB.getCardBalance( st, primaryentityid );
        ResultSet rs = DB.getFamily( st, primaryentityid );
        ResultSetArrayList familyList = new ResultSetArrayList( rs );
        sess.setAttribute( "familyList", familyList );
        
        // will retrieve "familyList" from session and then get iterator
        Iterator i = familyList.listIterator();

      out.write("\n");
      out.write("\n");
      out.write("<table>\n");
      out.write("<tr><td>Family</td><td>Members</td><td></td></tr>\n");

while ( i.hasNext() ){
    HashMap m = (HashMap)i.next();
    Integer entityid = (Integer)m.get("entityid");
    Integer adultid = (Integer)m.get("adultid");
    String fname = (String)m.get("firstname");
    String lname = (String)m.get("lastname");

      out.write("\n");
      out.write("<tr>\n");
      out.write("<td></td>\n");

    if ( entityid.compareTo(adultid) == 0 ){

      out.write("\n");
      out.write("<td>");
      out.print(fname);
      out.write("&nbsp;");
      out.print(lname);
      out.write("&nbsp;(adult)</td>\n");
      out.write("<td>\n");
      out.write("[<a href=\"");
      out.print(root);
      out.write("/ViewAdultInfo.jsp?id=");
      out.print(entityid);
      out.write("\">Update Personal Info</a>]\n");
      out.write("[Registrations]\n");
      out.write("[Enrollments]\n");
      out.write("</td>\n");
      out.write("</tr>\n");

        if ( hasCardBalance ){

      out.write("\n");
      out.write("<tr><td></td><td colspan=\"2\"><li>\n");
      out.write("[<a href=\"");
      out.print(root);
      out.write("/\">Class Card</a>] has a balance of $");
      out.print(cardBalance);
      out.write(" remaining.</li>\n");
      out.write("</td>\n");
      out.write("</tr>\n");

        }
    } else {

      out.write("\n");
      out.write("<td>");
      out.print(fname);
      out.write("&nbsp;");
      out.print(lname);
      out.write("&nbsp;</td>\n");
      out.write("<td>\n");
      out.write("[<a href=\"");
      out.print(root);
      out.write("/ViewChildInfo.jsp?id=");
      out.print(entityid);
      out.write("\">Update Personal Info</a>]\n");
      out.write("[Registrations]\n");
      out.write("[Enrollments]</td></tr>\n");
 } 
      out.write('\n');
 
} 

      out.write("\n");
      out.write("<tr></tr>\n");
      out.write("<tr></tr>\n");
      out.write("<tr><td colspan=\"3\">[<a href=\"");
      out.print(root);
      out.write("/GetContactInfoServlet\">Update Contact Info</a>]\n");
      out.write("[<a href=\"");
      out.print(root);
      out.write("/ChildInfo.jsp\">Add Child</a>]</td></tr>\n");
      out.write("<tr><td colspan=\"3\">[Create/Review Payment Plans][View & Pay Invoices]</td></tr>\n");
      out.write("<tr><td colspan=\"3\">[Calendar]</td></tr>\n");
      out.write("</table>\n");

    } catch ( SQLException e ){
        System.out.println( e );
        response.sendRedirect( root + "/login.jsp" );
    }

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
