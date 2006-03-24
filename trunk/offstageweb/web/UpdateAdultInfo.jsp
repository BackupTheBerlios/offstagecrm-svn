<%@ include file="jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>
<%
String id = (String)request.getParameter( "id" );

ResultSetArrayList familyList = (ResultSetArrayList)sess.getAttribute("familyList");
Iterator i = familyList.listIterator();

String dob = null;
Integer entityid = null;
Map adult = null;
while ( i.hasNext() ){
    adult = (Map)i.next();
    entityid = (Integer)adult.get("entityid");
    if ( id.compareTo( entityid.toString() ) == 0 ) {
        
        // Need to convert date to a String with format: mm/dd/yyyy
        Calendar c = Calendar.getInstance();
        java.sql.Date date = (java.sql.Date)adult.get("dob");
        c.setTimeInMillis(date.getTime());
        int month = c.get(Calendar.MONTH) + 1;
        int _date = c.get(Calendar.DATE);
        int year = c.get(Calendar.YEAR);
        dob = new String(  month + "/" + _date + "/" + year );
        break;
    }
}
%>
<table>
<tr><td style="font-size:x-large;" colspan="2">Enter Adult Information:</td></tr>
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

<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2"><input type="submit" name="submit" value="Submit">
<input type="submit" name="submit" value="Cancel"></td></tr>
</form>
</table>