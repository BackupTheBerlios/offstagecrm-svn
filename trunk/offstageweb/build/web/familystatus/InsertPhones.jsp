<%@ include file="/jsp_h.jsp" %>

<%
String name = request.getParameter("phonetype");
%>
<table>
<form method="POST" action="<%=root%>/InsertPhonesServlet?name=<%=name%>">
<tr><td style="font-size:x-large;" colspan="3">Add Phone Number:</td></tr>
<tr><td colspan="3"><hr/></td></tr>
<tr><td><%=name%></td>
<td><input type="text" name="number" >
<%
    if ( name.compareTo("work") == 0 ){
%>
ext. <input type="text" name="extension" >
<%
    }
%>
</td></tr>
<tr><td colspan="3"><hr/></td></tr>
<tr><td colspan="3"><input type="submit" name="submit" value="Submit">
<input type="submit" name="submit" value="Cancel"></td></tr>
</form>
</table>