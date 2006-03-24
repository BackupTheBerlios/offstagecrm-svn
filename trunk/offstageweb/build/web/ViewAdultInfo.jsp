<%@ include file="jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>
<%
String id = (String)request.getParameter( "id" );

ResultSetArrayList familyList = (ResultSetArrayList)sess.getAttribute("familyList");
Iterator i = familyList.listIterator();

Map adult = null;
while ( i.hasNext() ){
    adult = (Map)i.next();
    Integer entityid = (Integer)adult.get("entityid");
    if ( id.compareTo( entityid.toString() ) == 0 ) break;
}
%>
<table>
<tr><td style="font-size:x-large;" colspan="2">Adult Information:</td></tr>
<tr><td colspan="2"><hr/></td></td>

<tr>
<td>Name</td>
<td><%=adult.get("firstname")%> <%=adult.get("middlename")%> <%=adult.get("lastname")%></td>
</tr>

<tr>
<td>Gender</td>
<td><%=adult.get("gender")%></td>
</tr>

<tr>
<td>Date of Birth:</td>
<td><%=adult.get("dob")%></td>
</tr>

<td>Email</td>
<td><%=adult.get("email")%></td>
</tr>

<tr>
<td>Occupation</td>
<td><%=adult.get("occupation")%></td>
</tr>

<tr>
<td>Job Title</td>
<td><%=adult.get("title")%></td>
</tr>


<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2">
[<a href="<%=root%>/FamilyStatus.jsp">Back</a>]
[<a href="<%=root%>/UpdateAdultInfo.jsp?id=<%=id%>">Update</a>]

</table>