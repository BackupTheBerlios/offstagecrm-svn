<%@ include file="/jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>
<%
String id = (String)request.getParameter( "id" );

String fname = (String)request.getParameter( "fname" );

// If has enrolled in a course during this session...then show 'Finished Enrolling'
// instead of 'Back' at the end of the enrollment screen
Boolean hasEnrolledDuringSession = (Boolean)sess.getAttribute( "hasEnrolledDuringSession" );
ResultSetArrayList currentEnrollments = (ResultSetArrayList)sess.getAttribute("currentEnrollments");
ResultSetArrayList eligibleEnrollments = (ResultSetArrayList)sess.getAttribute("eligibleEnrollments");
ResultSetArrayList familyList = (ResultSetArrayList)sess.getAttribute( "familyList" );
Iterator i = currentEnrollments.listIterator();
Iterator ii = eligibleEnrollments.listIterator();
Iterator iii = familyList.listIterator();
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
<tr style="background-color:#ffcc66"><td>COURSE</td><td>PROGRAM</td><td>MEETING TIMES</td><td>TERMS</td></tr>
<%
        while ( i.hasNext() ) {
            Map course = (Map)i.next();
            String day = logic.getDay( ((Integer)course.get( "dayofweek" )) );
%>
<tr>
<td><%=course.get("course_name")%></td>
<td><%=course.get("term_name")%></td>
<td><%=day%>, <%=course.get("tstart")%> - <%=course.get("tnext")%></td>
<td><%=course.get("firstdate")%> - <%=course.get("nextdate")%></td>
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
            String eday = logic.getDay( ((Integer)ecourse.get( "dayofweek" )) );
            
            // If coursesetid not equal then previous and current courses are in different coursesets
            Integer currcoursesetid = (Integer)ecourse.get( "coursesetid" );
            if ( prevcoursesetid.compareTo( currcoursesetid ) != 0 ){
%>
<tr><td colspan="4"><hr/></td></td>
<tr><td colspan="4">
<%=ecourse.get("coursesetids_name")%>, 
(FROM:<%=ecourse.get("firstdate")%> TO:<%=ecourse.get("nextdate")%>)
[<a href="<%=root%>/InsertEnrollmentSetServlet?id=<%=id%>&setid=<%=ecourse.get("coursesetid")%>">Enroll in Set</a>]
</td></tr>
<tr style="background-color:#ffcc66"><td>COURSE</td><td>PROGRAM</td><td>MEETING TIMES</td><td></td></tr>
<%
            }
%>
<tr>
<td><%=ecourse.get("courseids_name")%></td>
<td><%=ecourse.get("term_name")%></td>
<td><%=eday%>, <%=ecourse.get("tstart")%> - <%=ecourse.get("tnext")%></td>
<td></td>
</tr>
<%
            prevcoursesetid = currcoursesetid;
        }
    }
%>
<tr><td colspan="4"><hr/></td></td>
<%
    // Don't show if only one person is in the family
    if ( familyList.size() > 1 ){
%>
<tr><td colspan="4">Enroll Another Family Member:
<form method="POST" action="<%=root%>/GetEnrollmentsServlet">
<select name="id">
<%
        Integer eid = new Integer( id );
        while ( iii.hasNext() ){
            Map person = (Map)iii.next();
            Integer entityid = (Integer)person.get("entityid");
            if ( entityid.compareTo( eid ) != 0 ){
                String firstname = (String)person.get("firstname");
%>
<option value="<%=entityid%>" ><%=firstname%></option>
<%
            }
        }
%>
</select>
<input type="submit" name="submit" value="Submit"></input>
</form>
</td></tr>
<%
    }
%>
<tr><td colspan="4">
[<a href="<%=root%>/familystatus/ChildInfo.jsp?id=<%=id%>&fname=<%=fname%>&message=enrollmentprocess">Enroll a New Child</a>]
</td></tr>
<tr><td colspan="4">
<%
    // EITHER show href for Payment Plan screen 
    // OR there hasn't been any new enrollment during this session SO show href 
    // for Family Status screen.
    if ( hasEnrolledDuringSession != null && hasEnrolledDuringSession.booleanValue() == true ){
%>
[<a href="<%=root%>/GetPaymentPlansServlet">Finished Enrolling</a>]
<%
    } else {
%>
[<a href="<%=root%>/FamilyStatus.jsp">Back</a>]
<%
    }
%>
</td></tr>
</table>
