<%@ include file="/jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>
<%
String id = (String)request.getParameter( "id" );
String fname = (String)request.getParameter( "fname" );
String age = (String)request.getParameter( "age" );

ResultSetArrayList registeredPrograms = (ResultSetArrayList)sess.getAttribute("registeredPrograms");
ResultSetArrayList eligiblePrograms = (ResultSetArrayList)sess.getAttribute("eligiblePrograms");
Iterator i = registeredPrograms.listIterator();
Iterator ii = eligiblePrograms.listIterator();
%>

<table>
<tr><td><%=fname%>'s Registration Status</td></td>
<tr><td><hr/></td></td>
<%
    // Show if person has programs for which he/she has already registered for
    if ( i.hasNext() ){
%>
<tr><td>Programs Registered For</td></td>
<tr><td><hr/></td></td>
<tr><td><ul>
<%
        while ( i.hasNext() ) {
            Map program = (Map)i.next();
%>
<li>
<%=program.get("name")%>
</li>
<%
        }
%>
</ul></td></tr>
<tr><td><hr/></td></td>
<%
    }
%>

<%
    // Show if person eligible to register for any programs
    if ( ii.hasNext() ){
%>
<tr><td>Programs Eligible To Register For</td></td>
<tr><td><hr/></td></td>
<tr><td><ul>
<%
        while ( ii.hasNext() ) {
            Map eprogram = (Map)ii.next();
%>
<li>
<%=eprogram.get("name")%>
[<a href="<%=root%>/InsertRegistrationServlet?id=<%=id%>&age=<%=age%>&programid=<%=eprogram.get("programid")%>">Register</a>]
</li>
<%
        }
%>
</ul></td></tr>
<tr><td><hr/></td></tr>
<%
    }
%>
<tr><td>
[<a href="<%=root%>/FamilyStatus.jsp">Back</a>]
</td></tr>
</table>