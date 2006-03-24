<%@ include file="db_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>

<%
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
%>

<table>
<tr><td>Family</td><td>Members</td><td></td></tr>
<%
while ( i.hasNext() ){
    HashMap m = (HashMap)i.next();
    Integer entityid = (Integer)m.get("entityid");
    Integer adultid = (Integer)m.get("adultid");
    String fname = (String)m.get("firstname");
    String lname = (String)m.get("lastname");
%>
<tr>
<td></td>
<%
    if ( entityid.compareTo(adultid) == 0 ){
%>
<td><%=fname%>&nbsp;<%=lname%>&nbsp;(adult)</td>
<td>
[<a href="<%=root%>/ViewAdultInfo.jsp?id=<%=entityid%>">Update Personal Info</a>]
[Registrations]
[Enrollments]
</td>
</tr>
<%
        if ( hasCardBalance ){
%>
<tr><td></td><td colspan="2"><li>
[<a href="<%=root%>/">Class Card</a>] has a balance of $<%=cardBalance%> remaining.</li>
</td>
</tr>
<%
        }
    } else {
%>
<td><%=fname%>&nbsp;<%=lname%>&nbsp;</td>
<td>
[<a href="<%=root%>/ViewChildInfo.jsp?id=<%=entityid%>">Update Personal Info</a>]
[Registrations]
[Enrollments]</td></tr>
<% } %>
<% 
} 
%>
<tr></tr>
<tr></tr>
<tr><td colspan="3">[<a href="<%=root%>/GetContactInfoServlet">Update Contact Info</a>]
[<a href="<%=root%>/ChildInfo.jsp">Add Child</a>]</td></tr>
<tr><td colspan="3">[Create/Review Payment Plans][View & Pay Invoices]</td></tr>
<tr><td colspan="3">[Calendar]</td></tr>
</table>
<%
    } catch ( SQLException e ){
        System.out.println( e );
        response.sendRedirect( root + "/login.jsp" );
    }
%>