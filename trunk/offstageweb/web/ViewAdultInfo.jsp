<%@ include file="jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>
<%
String id = (String)request.getParameter( "id" );

ResultSetHashMap account = (ResultSetHashMap)sess.getAttribute( "account" );
ResultSetArrayList familyList = (ResultSetArrayList)sess.getAttribute("familyList");
Iterator i = familyList.listIterator();


// Find person on family list
Map adult = null;
while ( i.hasNext() ){
    adult = (Map)i.next();
    Integer entityid = (Integer)adult.get("entityid");
    if ( id.compareTo( entityid.toString() ) == 0 ) break;
}
%>

<table>
<tr colspan="2"><td>
Personal Info[<a href="<%=root%>/UpdateAdultInfo.jsp?id=<%=id%>">Edit</a>]
</td></tr>
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

<%
    // Show account info if account is in session
    if ( account != null ){
%>
<tr><td colspan="2"><hr/></td></tr>
<tr colspan="2"><td>
Username/Password[<a href="<%=root%>/UpdateAccount.jsp">Edit</a>]
</td></tr>
<tr><td colspan="2"><hr/></td></tr>

<tr>
<td>Username</td>
<td><%=account.get("username")%></td>
</tr>

<tr>
<td>Password</td>
<td><%=account.get("password")%></td>
</tr>
<%
    }
%>

<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2">
[<a href="<%=root%>/FamilyStatus.jsp">Back</a>]
</td></tr>
</table>