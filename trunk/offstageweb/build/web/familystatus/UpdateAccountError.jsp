<%@ include file="/jsp_h.jsp" %>

<%
String message = (String)sess.getAttribute( "message" );
String username = (String)request.getParameter( "username" );
String password = (String)request.getParameter( "password" );
%>
<table>
<tr><td style="font-size:x-large;" colspan="2"><%=message%></td></tr>
<form method="POST" action="<%=root%>/UpdateAccountInfoServlet">
<tr><td colspan="2"><hr/></td></td>

<tr>
<td>Username</td>
<td><input type="text" name="username" value="<%=username%>">
</td></tr>

<tr>
<td>Password</td>
<td><input type="text" name="password" value="<%=password%>">
</td></tr>

<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2"><input type="submit" name="submit" value="Submit">
<input type="submit" name="submit" value="Cancel"></td></tr>
</form>
</table>