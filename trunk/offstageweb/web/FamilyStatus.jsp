<%@ include file="/db_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>

<%
Double cardBalance = (Double)sess.getAttribute( "cardBalance" );
ResultSetArrayList familyList = (ResultSetArrayList)sess.getAttribute( "familyList" );
Iterator i = familyList.listIterator();
%>

<table>
<tr><td>Family</td><td>Members</td><td></td></tr>
<tr><td colspan="3"><hr/></td></tr>
<%
System.out.println("IN FAMILYSTATUS!");
while ( i.hasNext() ){
    Map m = (Map)i.next();
    Integer entityid = (Integer)m.get("entityid");
    Integer adultid = (Integer)m.get("adultid");
    String fname = (String)m.get("firstname");
    String lname = (String)m.get("lastname");
%>
<tr>
<td></td>
<td>
<%=fname%>&nbsp;<%=lname%>&nbsp;
<%
    // Show that person is primary adult
    if ( entityid.compareTo(adultid) == 0 ){
%>
(adult)
<%
    }
%>
</td><td>
[<a href="<%=root%>/GetAccountInfoServlet?id=<%=entityid%>&adultid=<%=adultid%>">Update Personal Info</a>]
[<a href="<%=root%>/GetRegistrationsServlet?id=<%=entityid%>&dob=<%=m.get("dob")%>&fname=<%=fname%>">Registrations</a>]
[<a href="<%=root%>/GetEnrollmentsServlet?id=<%=entityid%>">Enrollments</a>]
</td></tr>
<%
    // If primary adult show card balance (if any)
    if ( entityid.compareTo(adultid) == 0 && cardBalance != null ){
%>
<tr><td></td><td colspan="2"><li>
[<a href="<%=root%>/">Class Card</a>] has a balance of $<%=cardBalance%> remaining.</li>
</td>
</tr>
<%
    }
}
%>
<tr><td colspan="3"><hr/></td></tr>
<tr><td colspan="3">[<a href="<%=root%>/GetContactInfoServlet">Update Contact Info</a>]
[<a href="<%=root%>/familystatus/ChildInfo.jsp">Add Child</a>]</td></tr>
<tr><td colspan="3">[Create/Review Payment Plans][View & Pay Invoices]</td></tr>
<tr><td colspan="3">[Calendar]</td></tr>
</table>