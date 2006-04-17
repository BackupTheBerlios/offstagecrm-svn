<%@ include file="/jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="java.util.*"%>

<%
    String customaddressto = (String)request.getParameter("customaddressto");
    String address1 = (String)request.getParameter("address1");
    String address2 = (String)request.getParameter("address2");
    String city = (String)request.getParameter("city");
    String state = (String)request.getParameter("state");
    String zip = (String)request.getParameter("zip");
    String country = (String)request.getParameter("country");
    String email = (String)request.getParameter("email");
%>

<table>
<tr><td style="font-size:x-large;" colspan="2">Update Contact Info Error</td></tr>
<tr><td style="font-size:x-large;color:red" colspan="2">Enter all fields marked with **</td></tr>
<form method="POST" action="<%=root%>/UpdateContactInfoServlet">
<tr><td colspan="2"><hr/></td></td>

<tr>
<td style="color:red">Address To**</td>
<td>
<input type="text" name="customaddressto" value="<%=customaddressto%>"></td>
</tr>

<tr><td style="color:red">Address 1**</td><td>
<input type="text" name="address1" value="<%=address1%>"></td></tr>

<tr><td>Address 2</td><td>
<input type="text" name="address2" value="<%=address2%>"></td></tr>

<tr><td style="color:red">City**</td>
<td><input type="text" name="city" value="<%=city%>">
</td></tr>

<tr><td style="color:red">State**</td>
<td><input type="text" name="state" value="<%=state%>">
</td></tr>

<tr><td style="color:red">Zip**</td>
<td><input type="text" name="zip" value="<%=zip%>">
</td></tr>

<tr><td style="color:red">Country**</td><td>
<input type="text" name="country" value="<%=country%>">
</td></tr>

<tr>
<td style="color:red">Email**</td>
<td><input type="text" name="email" size="28" value="<%=email%>"><font color="#FF0000"></font></td>
</tr>

<tr><td colspan="2"><hr/></td></td>
<tr><td colspan="2"><input type="submit" name="submit" value="Submit">
<input type="submit" name="submit" value="Cancel"></td></tr>
</form>

</table>