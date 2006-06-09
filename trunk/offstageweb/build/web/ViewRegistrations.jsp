<%@ include file="/jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>
<%

// Get id
String _id = (String)request.getParameter( "id" );
Integer id = null;
try {
    id = new Integer( _id );
} catch(Throwable t){
    System.out.println(t);
}

// Get fname
ResultSetArrayList familyList = (ResultSetArrayList)sess.getAttribute("familyList");
String fname = null;
if ( familyList != null ){
    Map person = familyList.get( "entityid", id );
    if ( person != null )
        fname = (String)person.get("firstname");
}

// Get registered and eligible programs
ResultSetArrayList registeredPrograms = (ResultSetArrayList)sess.getAttribute("registeredPrograms");
ResultSetArrayList eligiblePrograms = (ResultSetArrayList)sess.getAttribute("eligiblePrograms");

// If any of these four variables are null, then redirect
if ( id == null || fname == null || eligiblePrograms == null || registeredPrograms == null ){
        response.sendRedirect( request.getContextPath() + 
                "/GetFamilyStatusServlet" 
                );
}
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

<tr><td>Programs Eligible To Register For</td></td>
<tr><td><hr/></td></td>
<form method="POST" action="<%=root%>/InsertRegistrationServlet?id=<%=id%>">
<%
    while ( ii.hasNext() ) {
        Map eprogram = (Map)ii.next();
%>
<tr><td>
<input type="checkbox" name="<%=eprogram.get("programid")%>" ><%=eprogram.get("name")%>
</td></tr>
<%
    }
%>
<tr><td colspan="2"><hr/></td></td>
<tr><td colspan="2">
<input type="submit" name="submit" value="Register">
<input type="submit" name="submit" value="Back"></td></tr>
</form>
</table>