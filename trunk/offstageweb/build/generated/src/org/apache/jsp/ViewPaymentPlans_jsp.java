package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.io.*;
import offstage.web.*;
import offstage.web.collections.*;
import java.util.*;
import java.text.*;

public final class ViewPaymentPlans_jsp extends org.apache.jasper.runtime.HttpJspBase
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

// If has enrolled in a course during this session...then show 'Finished Enrolling'
// instead of 'Back' at the end of the enrollment screen
ResultSetArrayList currentPP = (ResultSetArrayList)sess.getAttribute( "currentPP" );
Map ppinvoices = (Map)sess.getAttribute( "ppinvoices" );
ResultSetArrayList enrollmentsWOpplan = (ResultSetArrayList)sess.getAttribute( "enrollmentsWOpplan" );
ArrayList pplantypeidsmenu = (ArrayList)sess.getAttribute("pplantypeidsmenu");
ArrayList paymenttypeidmenu = (ArrayList)sess.getAttribute("paymenttypeidmenu");
ArrayList cctypemenu = (ArrayList)sess.getAttribute("cctypemenu");

// If missing any of the above parts then return to /GetFamilyStatusServlet
if ( currentPP == null || ppinvoices == null || enrollmentsWOpplan == null ||
        paymenttypeidmenu == null || pplantypeidsmenu == null || cctypemenu == null ){
    response.sendRedirect( request.getContextPath() + "/GetFamilyStatusServlet" );
    return;
}

// Else get iterators and show info
Iterator i = currentPP.listIterator();
Iterator ii = enrollmentsWOpplan.listIterator();
Iterator iii = pplantypeidsmenu.iterator();
Iterator iiii = paymenttypeidmenu.iterator();
Iterator iiiii = cctypemenu.iterator();
System.out.println("cctypemenu is: " + cctypemenu);
Logic logic = new Logic();

      out.write("\n");
      out.write("\n");
      out.write("<table>\n");
      out.write("<tr><td colspan=\"7\">Family Payment Plans</td></td>\n");
      out.write("<tr><td colspan=\"7\"><hr/></td></td>\n");

if ( i.hasNext() ){

      out.write("\n");
      out.write("<tr><td colspan=\"7\">Current Payment Plans in Effect:</td></td>\n");
      out.write("\n");
      out.write("<tr style=\"background-color:#ffcc66\">\n");
      out.write("<td>pplanid</td><td>termname</td>\n");
      out.write("<td>planname</td><td>Total Bill</td><td>Remaining Balance</td>\n");
      out.write("<td>Payment Type</td><td>ccnumber</td></tr>\n");

    while ( i.hasNext() ){
        Map pp = (Map)i.next();
        Integer pplanid = (Integer)pp.get("pplanid");

      out.write("\n");
      out.write("<tr ><td>");
      out.print(pp.get("pplanid"));
      out.write("</td><td>");
      out.print(pp.get("termname"));
      out.write("</td>\n");
      out.write("<td>");
      out.print(pp.get("planname"));
      out.write("</td>\n");
      out.write("<td>");
      out.print(pp.get("amountpp"));
      out.write("</td>\n");
      out.write("<td>");
      out.print(pp.get("remainpp"));
      out.write("</td>\n");
      out.write("<td>");
      out.print(pp.get("paymentname"));
      out.write("</td>\n");
      out.write("<td>");
      out.print(pp.get("ccnumber"));
      out.write("</td></tr>\n");

        ResultSetArrayList invoices = (ResultSetArrayList)ppinvoices.get(pplanid);
        Iterator iiiiii = invoices.listIterator();
        if ( iiiiii.hasNext() ) {

      out.write("\n");
      out.write("<tr>\n");
      out.write("<td></td>\n");
      out.write("<td></td>\n");
      out.write("<td>Invoices</td></tr>\n");
      out.write("<tr style=\"background-color:#ffcc66\">\n");
      out.write("<td style=\"background-color:#ffffff\"></td>\n");
      out.write("<td style=\"background-color:#ffffff\"></td>\n");
      out.write("<td>Invoice ID</td>\n");
      out.write("<td>Total Due</td>\n");
      out.write("<td>Remaining Invoice Balance</td>\n");
      out.write("<td>Due Date</td>\n");
      out.write("<td>Date Paid</td>\n");
      out.write("</tr>\n");

            while ( iiiiii.hasNext() ){
                Map invoice = (Map)iiiiii.next();

      out.write("\n");
      out.write("<tr>\n");
      out.write("<td></td>\n");
      out.write("<td></td>\n");
      out.write("<td>");
      out.print(invoice.get("invoiceid"));
      out.write("</td>\n");
      out.write("<td>");
      out.print(invoice.get("amount"));
      out.write("</td>\n");
      out.write("<td>");
      out.print(invoice.get("remain"));
      out.write("</td>\n");
      out.write("<td>");
      out.print(invoice.get("ddue"));
      out.write("</td>\n");
      out.write("<td>");
      out.print(invoice.get("dtime"));
      out.write("</td>\n");
      out.write("</tr>   \n");

            }
        }

      out.write("\n");
      out.write("<tr><td colspan=\"7\"><hr/></td></tr>\n");

    }
}

      out.write("\n");
      out.write("<tr><td colspan=\"7\">\n");
      out.write("<table>\n");
      out.write("<form method=\"POST\" action=\"");
      out.print(root);
      out.write("/InsertPaymentPlanServlet\">\n");

if ( ii.hasNext() ){

      out.write("\n");
      out.write("<tr><td colspan=\"4\">Enrollments Without a Payment Plan:</td></tr>\n");
      out.write("<tr style=\"background-color:#ffcc66\"><td>COURSE</td><td>TERM</td><td>DATES</td><td>MEETING TIMES</td></tr>\n");

    while ( ii.hasNext() ){
        Map course = (Map)ii.next();
        String dayofweek = logic.getDayOfWeek( ((Integer)course.get( "dayofweek" )) );            
        String firstdate = logic.getSimpleDate( (java.util.Date)course.get("firstdate"), "MMMdd" );
        String nextdate = logic.getSimpleDate( (java.util.Date)course.get("nextdate"), "MMMdd" );
        String tstart = logic.getSimpleTime( (java.sql.Time)course.get("tstart"), DateFormat.SHORT );
        String tnext = logic.getSimpleTime( (java.sql.Time)course.get("tnext"), DateFormat.SHORT );

      out.write("\n");
      out.write("<tr>\n");
      out.write("<td>");
      out.print(course.get("course_name"));
      out.write("</td>\n");
      out.write("<td>");
      out.print(course.get("term_name"));
      out.write("</td>\n");
      out.write("<td>");
      out.print(firstdate);
      out.write(" to ");
      out.print(nextdate);
      out.write("</td>\n");
      out.write("<td>");
      out.print(HTMLValue.toValue(dayofweek));
      out.write(',');
      out.write(' ');
      out.print(HTMLValue.toValue(tstart));
      out.write(" to ");
      out.print(HTMLValue.toValue(tnext));
      out.write("</td>\n");
      out.write("</tr>\n");

    }

      out.write("\n");
      out.write("<tr><td colspan=\"4\"><hr/></td></td>\n");
      out.write("<tr><td colspan=\"4\">Create A Payment Plan For Enrollments:</td></tr>\n");
      out.write("<tr>\n");
      out.write("<td>Payment Plan</td>\n");
      out.write("<td>\n");
      out.write("<select name=\"pplantypeid\">\n");

    while ( iii.hasNext() ) {
        Map menuitem = (Map)iii.next();

      out.write("\n");
      out.write("<option value=\"");
      out.print(menuitem.get("value"));
      out.write('"');
      out.write('>');
      out.print(menuitem.get("label"));
      out.write("</option>\n");

    }

      out.write("\n");
      out.write("</select> \n");
      out.write("</td>\n");
      out.write("</tr>\n");
      out.write("\n");
      out.write("<tr>\n");
      out.write("<td>Payment Type</td>\n");
      out.write("<td>\n");
      out.write("<select name=\"paymenttypeid\">\n");

    while ( iiii.hasNext() ) {
        Map menuitem = (Map)iiii.next();

      out.write("\n");
      out.write("<option value=\"");
      out.print(menuitem.get("value"));
      out.write('"');
      out.write('>');
      out.print(menuitem.get("label"));
      out.write("</option>\n");

    }

      out.write("\n");
      out.write("</select> \n");
      out.write("</td>\n");
      out.write("</tr>\n");
      out.write("\n");
      out.write("<tr><td colspan=\"2\">**If payment type is credit card please fill in credit card info:</td></tr>\n");
      out.write("<tr>\n");
      out.write("<td>Credit Card Type</td>\n");
      out.write("<td>\n");
      out.write("<select name=\"cctypeid\">\n");

    while ( iiiii.hasNext() ) {
        Map menuitem = (Map)iiiii.next();

      out.write("\n");
      out.write("<option value=\"");
      out.print(menuitem.get("value"));
      out.write('"');
      out.write('>');
      out.print(menuitem.get("label"));
      out.write("</option>\n");

    }

      out.write("\n");
      out.write("</select> \n");
      out.write("</td>\n");
      out.write("</tr>\n");
      out.write("\n");
      out.write("<tr>\n");
      out.write("<td>Name On Credit Card</td>\n");
      out.write("<td><input type=\"text\" name=\"name\"><font color=\"#FF0000\"></font></td>\n");
      out.write("</tr>\n");
      out.write("\n");
      out.write("<tr>\n");
      out.write("<td>Credit Card Number</td>\n");
      out.write("<td><input type=\"text\" name=\"ccnumber\"><font color=\"#FF0000\"></font></td>\n");
      out.write("</tr>\n");
      out.write("\n");
      out.write("<tr>\n");
      out.write("<td>Credit Card Expiration</td>\n");
      out.write("<td>\n");
      out.write("<select name=\"month\">\n");

    int m = 1;
    while ( m < 13 ) {

      out.write("\n");
      out.write("<option value=\"");
      out.print(m);
      out.write('"');
      out.write('>');
      out.print(m);
      out.write("</option>\n");

        ++m;
    }

      out.write("\n");
      out.write("</select> \n");
      out.write("<select name=\"year\">\n");

    Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int lastyear = year + 10;
    while ( year < lastyear ) {

      out.write("\n");
      out.write("<option value=\"");
      out.print(year);
      out.write('"');
      out.write(' ');
      out.write('>');
      out.print(year);
      out.write("</option>\n");

        ++year;
    }

      out.write("\n");
      out.write("</select> \n");
      out.write("</td>\n");
      out.write("</tr>\n");
      out.write("\n");
      out.write("</td></tr>\n");
      out.write("<tr><td colspan=\"4\"><hr/></td></tr>\n");
      out.write("<tr><td colspan=\"4\"><input type=\"submit\" name=\"submit\" value=\"Create New Payment Plan\">\n");
      out.write("<input type=\"submit\" name=\"submit\" value=\"Back\"></td></tr>\n");
      out.write("</form>\n");
      out.write("</table>\n");

// Else if there are no enrollments with out payment plans then skip 
// all the inputs and show just the back button
} else {

      out.write("\n");
      out.write("<tr><td><input type=\"submit\" name=\"submit\" value=\"Back\"></td></tr>\n");

}

      out.write("\n");
      out.write("</tr>\n");
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
