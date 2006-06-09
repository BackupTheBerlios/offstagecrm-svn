<%@ include file="/jsp_h.jsp" %>
<%@page import="java.util.*"%>
<% 
// Get objects from session variable...
Map adult = (Map)sess.getAttribute("person");
Map account = (Map)sess.getAttribute("account");
ArrayList genderlist= (ArrayList)sess.getAttribute("genderlist");

// Validate objects gotten from session variable
if ( adult == null || account == null || genderlist == null ){
    System.out.println("REDIRECTING TO GETFAMILYSTATUSSERVLET FROM UPDATEADULTINFOERROR");
    response.sendRedirect( request.getContextPath() + 
            "/GetFamilyStatusServlet" 
            );
    return;
} 
String gender = (String)adult.get("gender");
Iterator i = genderlist.iterator();
%>

<table>
<tr><td style="font-size:x-large;" colspan="2">Input Error: <%=adult.get("badInput")%></td></tr>
<tr><td colspan="2"><hr/></td></td>
<tr><td style="font-size:x-large;" colspan="2">Please Edit Adult Information</td></tr>
<form method="POST" action="<%=root%>/UpdateAdultInfoServlet?id=<%=adult.get("entityid")%>">
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
<%
    while ( i.hasNext() ){
        Map menuitem = (Map)i.next();
        String id = (String)menuitem.get("value");
%>
<option value="<%=id%>"
<%
        if ( id != null && gender != null && id.compareTo(gender) == 0 ){
%>
selected
<%
        }
%>
><%=menuitem.get("label")%></option>
<%
    }
%>
</select> 
</td>
</tr>

<tr>
<td>Date of Birth (mm/dd/yyyy)</td>
<td><input type="Text"  maxlength="10" name="dob" size="10" value="<%=adult.get("dob")%>" autocomplete=off >
</tr>

<tr>
<td>Email</td>
<td><input type="text" name="email" size="28" value="<%=adult.get("email")%>"><font color="#FF0000"></font></td>
</tr>

<tr>
<td>Occupation</td>
<td><input type="text" name="occupation" value="<%=adult.get("occupation")%>"></td>
</tr>

<tr>
<td>Job Title</td>
<td><input type="text" name="title" value="<%=adult.get("title")%>"></td>
</tr>

<tr><td colspan="2"><hr/></td></td>
<tr><td colspan="2">Edit Username/Password</td></td>
<tr><td colspan="2"><hr/></td></td>

<tr>
<td>Username</td>
<td><input type="text" name="username" value="<%=account.get("username")%>">
</td></tr>

<tr>
<td>Password</td>
<td><input type="text" name="password" value="<%=account.get("password")%>">
</td></tr>

<tr><td colspan="2"><hr/></td></td>
<tr><td colspan="2"><input type="submit" name="submit" value="Submit">
<input type="submit" name="submit" value="Back"></td></tr>
</form>
</table>