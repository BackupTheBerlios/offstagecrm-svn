<%@ include file="jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="java.util.*"%>

<%
Map contactInfo = (Map)sess.getAttribute( "contactInfo" );
%>
<table>
<tr><td style="font-size:x-large;" colspan="2">Update Contact Info</td></tr>
<form method="POST" action="<%=root%>/UpdateContactInfoServlet">
<tr><td colspan="2"><hr/></td></td>

<tr>
<td>Address To</td>
<td>
<input type="text" name="customaddressto" value="<%=contactInfo.get("customaddressto")%>"></td>
</tr>

<tr><td>Address 1</td><td>
<input type="text" name="address1" value="<%=contactInfo.get("address1")%>"></td></tr>

<tr><td>Address 2</td><td>
<input type="text" name="address2" value="<%=contactInfo.get("address2")%>"></td></tr>

<tr><td>City</td>
<td><input type="text" name="city" value="<%=contactInfo.get("city")%>">
</td></tr>

<tr><td>State</td>
<td><input type="text" name="state" value="<%=contactInfo.get("state")%>">
</td></tr>

<tr><td>Zip</td>
<td><input type="text" name="zip" value="<%=contactInfo.get("zip")%>">
</td></tr>

<tr><td>Country</td><td>
<input type="text" name="country" value="<%=contactInfo.get("country")%>">
</td></tr>

<tr>
<td>Email</td>
<td><input type="text" name="email" size="28" value="<%=contactInfo.get("email")%>"><font color="#FF0000"></font></td>
</tr>

<tr><td colspan="2"><hr/></td></td>
<tr><td colspan="2"><input type="submit" name="submit" value="Submit">
<input type="submit" name="submit" value="Cancel"></td></tr>
</form>

</table>