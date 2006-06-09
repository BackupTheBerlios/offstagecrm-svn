package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.io.*;
import offstage.web.*;
import offstage.web.collections.*;
import java.util.*;
import java.text.*;

public final class ViewEnrollments_jsp extends org.apache.jasper.runtime.HttpJspBase
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

// Get id of person
String _id = (String)request.getParameter( "id" );
Integer id = null;
try {
    id = new Integer(_id);
} catch (Throwable t){
    System.out.println(t);
}

// Get fname of person
String fname = null;
ResultSetArrayList familyList = (ResultSetArrayList)sess.getAttribute( "familyList" );
if ( familyList != null ){
    // Get fname 
    Map person = familyList.get( "entityid", id );
    fname = (String)person.get("firstname");
}

// If has enrolled in a course during this session...then show 'Finished Enrolling'
// instead of 'Back' at the end of the enrollment screen
Boolean hasEnrolledDuringSession = (Boolean)sess.getAttribute( "hasEnrolledDuringSession" );
ResultSetArrayList currentEnrollments = (ResultSetArrayList)sess.getAttribute("currentEnrollments");
ResultSetArrayList eligibleEnrollments = (ResultSetArrayList)sess.getAttribute("eligibleEnrollments");
ArrayList famDropDown = (ArrayList)sess.getAttribute( "famDropDown" );

// If id, fname or any of the ResultSetArrayLists or famDropDown are null then redirect to main page
if ( id == null || fname == null || currentEnrollments == null || 
        eligibleEnrollments == null || famDropDown == null ){
    // EITHER redirect to show payment plan if already enrolled...
    if ( hasEnrolledDuringSession != null && hasEnrolledDuringSession.booleanValue() == true ){
        response.sendRedirect( request.getContextPath() + "/GetPaymentPlansServlet" );
    }
    // OR GetFamilyStatusServlet
    else {
        response.sendRedirect( request.getContextPath() + "/GetFamilyStatusServlet" );
    }
    return;
}

// Else get iterators and show info
Iterator i = currentEnrollments.listIterator();
Iterator ii = eligibleEnrollments.listIterator();
Iterator iii = famDropDown.iterator();
Logic logic = new Logic();

      out.write("\n");
      out.write("\n");
      out.write("<table>\n");
      out.write("<tr><td colspan=\"4\">");
      out.print(fname);
      out.write("'s Enrollment Status</td></td>\n");
      out.write("<tr><td colspan=\"4\"><hr/></td></td>\n");

    // Show if person has programs for which he/she has already registered for
    if ( i.hasNext() ){

      out.write("\n");
      out.write("<tr><td colspan=\"4\">Courses Enrolled In</td></td>\n");
      out.write("<tr><td colspan=\"4\"><hr/></td></td>\n");
      out.write("<tr style=\"background-color:#ffcc66\"><td>COURSE</td><td>TERM</td><td>DATES</td><td>MEETING TIMES</td></tr>\n");

        while ( i.hasNext() ) {
            Map course = (Map)i.next();
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

    }

      out.write('\n');

    // Show if person eligible to register for any programs
    if ( ii.hasNext() ){

      out.write("\n");
      out.write("<tr><td colspan=\"7\">Eligible Course Sets</td></td>\n");

        // You want to keep track of which courseset a courseid belongs to.
        // So IF the previous coursesetid is different from the current one,
        // THEN the current course is in a different courseset then the previous course
        Integer prevcoursesetid = new Integer( Integer.MIN_VALUE );
        while ( ii.hasNext() ) {
            Map ecourse = (Map)ii.next();
            String eday = logic.getDayOfWeek( ((Integer)ecourse.get( "dayofweek" )) );
            
            // If coursesetid not equal then previous and current courses are in different coursesets
            Integer currcoursesetid = (Integer)ecourse.get( "coursesetid" );
            if ( prevcoursesetid.compareTo( currcoursesetid ) != 0 ){

      out.write("\n");
      out.write("<tr><td colspan=\"4\"><hr/></td></td>\n");
      out.write("<tr><td colspan=\"4\">\n");
      out.print(ecourse.get("coursesetids_name"));
      out.write(',');
      out.write(' ');
      out.write('\n');
      out.print(logic.getSimpleDate((java.util.Date)ecourse.get("firstdate"), "MMMdd" ));
      out.write(" to\n");
      out.print(logic.getSimpleDate((java.util.Date)ecourse.get("nextdate"), "MMMdd" ));
      out.write("\n");
      out.write("[<a href=\"");
      out.print(root);
      out.write("/InsertEnrollmentSetServlet?id=");
      out.print(id);
      out.write("&setid=");
      out.print(ecourse.get("coursesetid"));
      out.write("\">Enroll</a>]\n");
      out.write("</td></tr>\n");
      out.write("<tr style=\"background-color:#ffcc66\"><td>COURSE</td><td>TERM</td><td>MEETING TIMES</td></tr>\n");

            }
            String tstart = logic.getSimpleTime( (java.sql.Time)ecourse.get("tstart"), DateFormat.SHORT );
            String tnext = logic.getSimpleTime( (java.sql.Time)ecourse.get("tnext"), DateFormat.SHORT );

      out.write("\n");
      out.write("<tr>\n");
      out.write("<td>");
      out.print(ecourse.get("courseids_name"));
      out.write("</td>\n");
      out.write("<td>");
      out.print(ecourse.get("term_name"));
      out.write("</td>\n");
      out.write("<td>");
      out.print(eday);
      out.write(',');
      out.write(' ');
      out.print(tstart);
      out.write(' ');
      out.write('-');
      out.write(' ');
      out.print(tnext);
      out.write("</td>\n");
      out.write("<td></td>\n");
      out.write("</tr>\n");

            prevcoursesetid = currcoursesetid;
        }
    }

      out.write("\n");
      out.write("\n");
      out.write("<tr><td colspan=\"4\"><hr/></td></td>\n");
      out.write("<form method=\"POST\" name=\"insertenrollment\" action=\"");
      out.print(root);
      out.write("/InsertEnrollmentSetServlet\">\n");

    // Don't show if only one person is in the family
    if ( familyList.size() > 1 ){

      out.write("\n");
      out.write("<tr><td colspan=\"4\">\n");
      out.write("Enroll Another Family Member:<br/>\n");
      out.write("<select name=\"id\">\n");

        while ( iii.hasNext() ){
            Map menuitem = (Map)iii.next();
            Integer value = (Integer)menuitem.get("value");

      out.write("\n");
      out.write("<option value=\"");
      out.print(value);
      out.write('"');
      out.write('\n');

            if ( value != null && id != null && value.compareTo(id) == 0 ){

      out.write("\n");
      out.write("selected\n");

            }

      out.write('\n');
      out.write('>');
      out.print(menuitem.get("label"));
      out.write("</option>\n");

        }

      out.write("\n");
      out.write("</select>\n");
      out.write("<input type=\"submit\" name=\"submit\" value=\"View Enrollment Status\"></input>\n");

    }

      out.write("\n");
      out.write("<input type=\"submit\" name=\"submit\" value=\"Back\"></input>\n");
      out.write("</td></tr>\n");
      out.write("</form>\n");
      out.write("\n");
      out.write("<tr><td colspan=\"4\"><hr/></td></td>\n");
      out.write("<tr><td colspan=\"4\" >\n");
      out.write("<form method=\"POST\" name=\"addchild\" action=\"");
      out.print(root);
      out.write("/familystatus/InsertChildInfo.jsp?id=");
      out.print(id);
      out.write("&message=enrollmentprocess\" >\n");
      out.write("<input type=\"submit\" name=\"submit\" value=\"Add Child to Account\"></input>\n");
      out.write("</td></tr>\n");
      out.write("</form>\n");
      out.write("</table>\n");
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
