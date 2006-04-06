<%@ include file="/jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>

<%
ResultSetHashMap account = (ResultSetHashMap)sess.getAttribute("account");
%>
<table>
<tr><td style="font-size:x-large;" colspan="2">Edit Username/Password</td></tr>
<form method="POST" action="<%=root%>/UpdateAccountInfoServlet">
<tr><td colspan="2"><hr/></td></td>

<tr>
<td>Username</td>
<td><input type="text" name="username" value="<%=account.get("username")%>">
</td></tr>

<tr>
<td>Password</td>
<td><input type="text" name="password" value="<%=account.get("password")%>">
</td></tr>

<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2"><input type="submit" name="submit" value="Submit">
<input type="submit" name="submit" value="Cancel"></td></tr>
</form>
</table>