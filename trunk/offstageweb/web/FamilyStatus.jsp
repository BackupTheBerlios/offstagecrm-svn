<%@ include file="/db_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>

<%
Double cardBalance = (Double)sess.getAttribute( "cardBalance" );
Integer primaryentityid = (Integer)sess.getAttribute( "primaryentityid" );
ResultSetArrayList familyList = (ResultSetArrayList)sess.getAttribute( "familyList" );
Map primaryadult = familyList.get( "entityid", primaryentityid );
Iterator i = familyList.listIterator();
%>

<table>
<tr><td>Family</td><td>Members</td><td></td></tr>
<tr><td colspan="3"><hr/></td></tr>
<tr>
<td></td>
<td><%=primaryadult.get("firstname")%>&nbsp;<%=primaryadult.get("lastname")%>&nbsp;(Primary Adult)</td>
<td>
[<a href="<%=root%>/GetAccountInfoServlet?id=<%=primaryadult.get("entityid")%>">Update Personal Info</a>]
[<a href="<%=root%>/GetRegistrationsServlet?id=<%=primaryadult.get("entityid")%>">Registrations</a>]
[<a href="<%=root%>/GetEnrollmentsServlet?id=<%=primaryadult.get("entityid")%>">Enrollments</a>]
</td></tr>
<%
// If primary adult show card balance (if any)
if ( cardBalance != null ){
%>
<tr><td></td><td colspan="2"><li>
[<a href="<%=root%>/">Class Card</a>] has a balance of $<%=cardBalance%> remaining.</li>
</td>
</tr>
<%
}
%>
<%
while ( i.hasNext() ){
    Map m = (Map)i.next();
    Integer entityid = (Integer)m.get("entityid");
    // primaryentityid found when searched for primary adult(above)
    // When this entity is found skip it
    if ( entityid.compareTo(primaryentityid) == 0 ){
        continue;
    }
%>
<tr>
<td></td>
<td><%=m.get("firstname")%>&nbsp;<%=m.get("lastname")%>&nbsp;</td>
<td>
[<a href="<%=root%>/GetAccountInfoServlet?id=<%=entityid%>">Update Personal Info</a>]
[<a href="<%=root%>/GetRegistrationsServlet?id=<%=entityid%>">Registrations</a>]
[<a href="<%=root%>/GetEnrollmentsServlet?id=<%=entityid%>">Enrollments</a>]
</td></tr>
<%
}
%>
<tr><td colspan="3"><hr/></td></tr>
<tr><td colspan="3">[<a href="<%=root%>/GetContactInfoServlet">Update Contact Info</a>]
[<a href="<%=root%>/familystatus/InsertChildInfo.jsp">Add Child</a>]</td></tr>
<tr><td colspan="3">
[<a href="<%=root%>/GetPaymentPlansServlet">Create/Review Payment Plans</A>]
[View & Pay Invoices]</td></tr>
<tr><td colspan="3">[Calendar]</td></tr>
</table>