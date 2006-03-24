<%@ include file="jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>
<%
String id = (String)request.getParameter( "id" );

ResultSetArrayList familyList = (ResultSetArrayList)sess.getAttribute("familyList");
Iterator i = familyList.listIterator();

Map child = null;
while ( i.hasNext() ){
    child = (Map)i.next();
    Integer entityid = (Integer)child.get("entityid");
    if ( id.compareTo( entityid.toString() ) == 0 ) break;
}
%>
<table>
<tr><td style="font-size:x-large;" colspan="2">Child Information:</td></tr>
<tr><td colspan="2"><hr/></td></td>

<tr>
<td>Name</td>
<td><%=child.get("firstname")%> <%=child.get("middlename")%> <%=child.get("lastname")%></td>
</tr>

<tr>
<td>Gender</td>
<td><%=child.get("gender")%></td>
</tr>

<tr>
<td>Date of Birth:</td>
<td><%=child.get("dob")%></td>
</tr>

<tr>
<td>Email</td>
<td><%=child.get("email")%></td>
</tr>

<tr>
<td>Child's Relation to Adult:&nbsp;</td>
<td><%=child.get("name")%></td>
</tr>

<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2">
[<a href="<%=root%>/FamilyStatus.jsp">Back</a>]
[<a href="<%=root%>/UpdateChildInfo.jsp?id=<%=id%>">Update</a>]

</table>