<%@ include file="/jsp_h.jsp" %>
<%@page import="java.util.*"%>
<% 
String badInput = (String)sess.getAttribute("badInput");

Map adult = (Map)sess.getAttribute("adult");
Integer entityid = (Integer)adult.get("entityid" );
String firstname = (String)adult.get("firstname");
String middlename = (String)adult.get( "middlename" );
String lastname = (String)adult.get( "lastname" );
String gender = (String)adult.get( "gender" );
String dob = (String)adult.get( "dob" );
String email = (String)adult.get( "email" );
String occupation = (String)adult.get( "occupation" );
String title = (String)adult.get( "title" );
%>

<table>
<tr><td style="font-size:x-large;" colspan="2">Input Error: <%=badInput%></td></tr>
<tr><td colspan="2"><hr/></td></td>
<tr><td style="font-size:x-large;" colspan="2">Please Edit Adult Information</td></tr>
<form method="POST" action="<%=root%>/UpdateAdultInfoServlet?id=<%=entityid%>">
<tr><td colspan="2"><hr/></td></td>

<tr>
<td>First Name</td>
<td><input type="text" name="firstname" value="<%=adult.get("firstname")%>">
</td></tr>

<tr>
<td>Middle Name</td>
<td><input type="text" name="middlename" value="<%=adult.get("middlename")%>">
</td></tr>

<tr>
<td>Last Name</td>
<td><input type="text" name="lastname" value="<%=adult.get("lastname")%>">
</td></tr>

<td>Gender</td>
<td>
<select name="gender">
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
<td>Occupation</td>
<td><input type="text" name="occupation" value="<%=occupation%>"></td>
</tr>

<tr>
<td>Job Title</td>
<td><input type="text" name="title" value="<%=title%>"></td>
</tr>

<tr><td colspan="2"><hr/></td></td>
<tr><td colspan="2"><input type="submit" name="submit" value="Submit">
<input type="submit" name="submit" value="Cancel"></td></tr>
</form>
</table>