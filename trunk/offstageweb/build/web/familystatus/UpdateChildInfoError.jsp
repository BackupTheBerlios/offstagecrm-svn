<%@ include file="/jsp_h.jsp" %>
<% 
String entityid = (String)request.getParameter("id");
System.out.println( "id is: " + entityid );
String badInput = (String)sess.getAttribute("badInput");
String firstname = (String)sess.getAttribute("firstname");
String middlename = (String)sess.getAttribute("middlename");
String lastname = (String)sess.getAttribute("lastname");
String gender = (String)sess.getAttribute("gender");
String dob = (String)sess.getAttribute("dob");
String email = (String)sess.getAttribute("email");
String relprimarytype = (String)sess.getAttribute("relprimarytype");
%>

<table>
<tr><td style="font-size:x-large;" colspan="2">Input Error: <%=badInput%></td></tr>
<tr><td colspan="2"><hr/></td></td>
<tr><td style="font-size:x-large;" colspan="2">Please Edit Child Information</td></tr>
<form method="POST" action="<%=root%>/UpdateChildInfoServlet?id=<%=entityid%>">
<tr><td colspan="2"><hr/></td></td>

<tr>
<td>First Name</td>
<td><input type="text" name="firstname" value="<%=firstname%>"><font color="#FF0000"></font></td>
</tr>

<tr>
<td>Last Name</td>
<td><input type="text" name="lastname" value="<%=lastname%>"><font color="#FF0000"></font></td>
</tr>

<tr>
<td>Middle Name</td>
<td><input type="text" name="middlename" value="<%=middlename%>"><font color="#FF0000"></font></td>
</tr>

<tr>
<td>Gender</td>
<td>
<select name="gender" value="<%=gender%>">
<option value="m">male</option>
<option value="f">female</option>
</select> 
</td>
</tr>

<tr>
<td>Date of Birth (mm/dd/yyyy)</td>
<td><input type="Text"  maxlength="10" name="dob" size="10" value="<%=dob%>" autocomplete=off >
</tr>

<tr>
<td>Email</td>
<td><input type="text" name="email" size="28" value="<%=email%>"><font color="#FF0000"></font></td>
</tr>

<tr>
<td>Child's Relation to Primary Adult</td>
<td>
<select name="relprimarytype" value="<%=relprimarytype%>">
<option value="child">Son/Daughter</option>
<option value="grandchild">Grandson/Granddaughter</option>
<option value="sibling">Brother/Sister</option>
<option value="cousin">Cousin</option>
<option value="niece">Niece</option>
<option value="niece">Nephew</option>
</select> 
</td>
</tr>
<tr><td colspan="2"><hr/></td></td>
<tr><td colspan="2"><input type="submit" name="submit" value="Submit">
<input type="submit" name="submit" value="Cancel"></td></tr>
</form>
</table>