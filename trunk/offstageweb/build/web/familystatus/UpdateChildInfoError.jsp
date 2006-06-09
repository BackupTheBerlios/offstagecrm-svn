<%@ include file="/jsp_h.jsp" %>
<%@page import="java.util.*"%>

<% 
Map child = (Map)sess.getAttribute("person");
ArrayList genderlist = (ArrayList)sess.getAttribute("genderlist");
ArrayList relprimarytypelist = (ArrayList)sess.getAttribute("relprimarytypelist");

if ( child == null || genderlist == null || relprimarytypelist == null ){
    response.sendRedirect( request.getContextPath() + 
            "/GetFamilyStatusServlet" 
            );
    return;
} 
Iterator i = genderlist.iterator();
Iterator ii = relprimarytypelist.iterator();
String gender = (String)child.get("gender");
String relprimarytype = (String)child.get("relprimarytype");
%>

<table>
<tr><td style="font-size:x-large;" colspan="2">Input Error: <%=child.get("badInput")%></td></tr>
<tr><td colspan="2"><hr/></td></td>
<tr><td style="font-size:x-large;" colspan="2">Please Edit Child Information</td></tr>
<form method="POST" action="<%=root%>/UpdateChildInfoServlet?id=<%=child.get("entityid")%>">
<tr><td colspan="2"><hr/></td></td>

<tr>
<td>First Name</td>
<td><input type="text" name="firstname" value="<%=child.get("firstname")%>"><font color="#FF0000"></font></td>
</tr>

<tr>
<td>Last Name</td>
<td><input type="text" name="lastname" value="<%=child.get("lastname")%>"><font color="#FF0000"></font></td>
</tr>

<tr>
<td>Middle Name</td>
<td><input type="text" name="middlename" value="<%=child.get("middlename")%>"><font color="#FF0000"></font></td>
</tr>

<tr>
<td>Gender</td>
<td>
<select name="gender" >
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
<td><input type="Text"  maxlength="10" name="dob" size="10" value="<%=child.get("dob")%>" autocomplete=off >
</tr>

<tr>
<td>Email</td>
<td><input type="text" name="email" size="28" value="<%=child.get("email")%>"><font color="#FF0000"></font></td>
</tr>

<tr>
<td>Child's Relation to Primary Adult</td>
<td>
<select name="relprimarytype">
<%
    while ( ii.hasNext() ){
        Map menuitem = (Map)ii.next();
        String id = (String)menuitem.get("value");
%>
<option value="<%=id%>"
<%
        if ( id != null && relprimarytype != null && id.compareTo(relprimarytype) == 0 ){
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
<tr><td colspan="2"><hr/></td></td>
<tr><td colspan="2"><input type="submit" name="submit" value="Submit">
<input type="submit" name="submit" value="Cancel"></td></tr>
</form>
</table>