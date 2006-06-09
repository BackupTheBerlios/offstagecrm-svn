<%@ include file="/jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%
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
%>

<table>
<tr><td colspan="4"><%=fname%>'s Enrollment Status</td></td>
<tr><td colspan="4"><hr/></td></td>
<%
    // Show if person has programs for which he/she has already registered for
    if ( i.hasNext() ){
%>
<tr><td colspan="4">Courses Enrolled In</td></td>
<tr><td colspan="4"><hr/></td></td>
<tr style="background-color:#ffcc66"><td>COURSE</td><td>TERM</td><td>DATES</td><td>MEETING TIMES</td></tr>
<%
        while ( i.hasNext() ) {
            Map course = (Map)i.next();
            String dayofweek = logic.getDayOfWeek( ((Integer)course.get( "dayofweek" )) );            
            String firstdate = logic.getSimpleDate( (java.util.Date)course.get("firstdate"), "MMMdd" );
            String nextdate = logic.getSimpleDate( (java.util.Date)course.get("nextdate"), "MMMdd" );
            String tstart = logic.getSimpleTime( (java.sql.Time)course.get("tstart"), DateFormat.SHORT );
            String tnext = logic.getSimpleTime( (java.sql.Time)course.get("tnext"), DateFormat.SHORT );
%>
<tr>
<td><%=course.get("course_name")%></td>
<td><%=course.get("term_name")%></td>
<td><%=firstdate%> to <%=nextdate%></td>
<td><%=HTMLValue.toValue(dayofweek)%>, <%=HTMLValue.toValue(tstart)%> to <%=HTMLValue.toValue(tnext)%></td>
</tr>
<%
        }
%>
<tr><td colspan="4"><hr/></td></td>
<%
    }
%>
<%
    // Show if person eligible to register for any programs
    if ( ii.hasNext() ){
%>
<tr><td colspan="7">Eligible Course Sets</td></td>
<%
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
%>
<tr><td colspan="4"><hr/></td></td>
<tr><td colspan="4">
<%=ecourse.get("coursesetids_name")%>, 
<%=logic.getSimpleDate((java.util.Date)ecourse.get("firstdate"), "MMMdd" )%> to
<%=logic.getSimpleDate((java.util.Date)ecourse.get("nextdate"), "MMMdd" )%>
[<a href="<%=root%>/InsertEnrollmentSetServlet?id=<%=id%>&setid=<%=ecourse.get("coursesetid")%>">Enroll</a>]
</td></tr>
<tr style="background-color:#ffcc66"><td>COURSE</td><td>TERM</td><td>MEETING TIMES</td></tr>
<%
            }
            String tstart = logic.getSimpleTime( (java.sql.Time)ecourse.get("tstart"), DateFormat.SHORT );
            String tnext = logic.getSimpleTime( (java.sql.Time)ecourse.get("tnext"), DateFormat.SHORT );
%>
<tr>
<td><%=ecourse.get("courseids_name")%></td>
<td><%=ecourse.get("term_name")%></td>
<td><%=eday%>, <%=tstart%> - <%=tnext%></td>
<td></td>
</tr>
<%
            prevcoursesetid = currcoursesetid;
        }
    }
%>

<tr><td colspan="4"><hr/></td></td>
<form method="POST" name="insertenrollment" action="<%=root%>/InsertEnrollmentSetServlet">
<%
    // Don't show if only one person is in the family
    if ( familyList.size() > 1 ){
%>
<tr><td colspan="4">
Enroll Another Family Member:<br/>
<select name="id">
<%
        while ( iii.hasNext() ){
            Map menuitem = (Map)iii.next();
            Integer value = (Integer)menuitem.get("value");
%>
<option value="<%=value%>"
<%
            if ( value != null && id != null && value.compareTo(id) == 0 ){
%>
selected
<%
            }
%>
><%=menuitem.get("label")%></option>
<%
        }
%>
</select>
<input type="submit" name="submit" value="View Enrollment Status"></input>
<%
    }
%>
<input type="submit" name="submit" value="Back"></input>
</td></tr>
</form>

<tr><td colspan="4"><hr/></td></td>
<tr><td colspan="4" >
<form method="POST" name="addchild" action="<%=root%>/familystatus/InsertChildInfo.jsp?id=<%=id%>&message=enrollmentprocess" >
<input type="submit" name="submit" value="Add Child to Account"></input>
</td></tr>
</form>
</table>
