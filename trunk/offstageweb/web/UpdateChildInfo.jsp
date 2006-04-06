<%@ include file="db_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>

<%
    Integer entityid = new Integer(0);
    String firstname = null;
    String middlename = null;
    String lastname = null;
    String gender = null;
    java.sql.Date dob = null;
    String email = null;
    String relprimarytype = null;
    
    String id = (String)request.getParameter("id");
    
    ResultSetArrayList familyList = (ResultSetArrayList)sess.getAttribute("familyList");
    Iterator i = familyList.listIterator();
    while ( i.hasNext() && id.compareTo( entityid.toString() ) != 0 ){
        HashMap m = (HashMap)i.next();
        entityid = (Integer)m.get("entityid");
        firstname = (String)m.get("firstname");
        middlename = (String)m.get("middlename");
        lastname = (String)m.get("lastname");
        gender = (String)m.get("gender");
        dob = (java.sql.Date)m.get("dob");
        email = (String)m.get("email");
        relprimarytype = (String)m.get("relprimarytype");
    }
    Calendar c = Calendar.getInstance();
    c.setTimeInMillis( dob.getTime() );
%>

<table>
<tr><td style="font-size:x-large;" colspan="2">Update Child Information</td></tr>
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
<td><input type="Text"  maxlength="10" name="dob" size="10" 
value="<%=c.get(Calendar.MONTH)+1%>/<%=c.get(Calendar.DATE)%>/<%=c.get(Calendar.YEAR)%>" autocomplete=off >
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