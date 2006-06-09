<%@ include file="/jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="java.util.*"%>
<%@page import="offstage.web.collections.HTMLValue"%>

<%
// Get objects from session
Map contactInfo = (Map)session.getAttribute( "contactInfo" );
Map phones = (Map)sess.getAttribute("phones");
Map home = null;
Map work = null;
Map fax = null;
if ( phones != null ){
    home = (Map)phones.get("home");
    work = (Map)phones.get("work");
    fax = (Map)phones.get("fax");
}
System.out.println(phones);
// Validate objects gotten from session
if ( contactInfo == null || phones == null ){
    response.sendRedirect( request.getContextPath() + 
            "/GetFamilyStatusServlet" 
            );
    return;
}
%>
<table>
<tr><td style="font-size:x-large;" colspan="2">Update Contact Info</td></tr>
<form method="POST" action="<%=root%>/UpdateContactInfoServlet">
<tr><td colspan="2"><hr/></td></td>

<tr>
<td>Address To</td>
<td>
<input type="text" name="customaddressto" value="<%=HTMLValue.toValue( contactInfo.get("customaddressto") )%>"></td>
</tr>

<tr><td>Address 1</td><td>
<input type="text" name="address1" value="<%=HTMLValue.toValue( contactInfo.get("address1") )%>"></td></tr>

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


<tr><td style="font-size:x-large;" colspan="3">Edit Phone Number:</td></tr>
<tr><td colspan="3"><hr/></td></tr>
<tr>
<td>Work</td>
<td>
<input type="text" name="wphone" value="<%=HTMLValue.toValue( work, "phone" )%>" >
ext. <input type="text" name="wext" value="<%=HTMLValue.toValue( work, "ext" )%>" >
<input type="checkbox" name="wdomestic"
<%
    // If it is an international phone then check
    if ( ( HTMLValue.toValue( work, "domestic") ).compareTo("false") == 0 ){
%>
checked
<%
    }
%>
> International Number
</td></tr>

<td>Home</td>
<td>
<input type="text" name="hphone" value="<%=HTMLValue.toValue( home, "phone")%>" >
ext. <input type="text" name="hext" value="<%=HTMLValue.toValue( home, "ext")%>" >
<input type="checkbox" name="hdomestic"
<%
    // If it is an international phone then check
    if ( ( HTMLValue.toValue( home, "domestic") ).compareTo("false") == 0 ){
%>
checked
<%
    }
%>
> International Number
</td></tr>

<td>Fax</td>
<td>
<input type="text" name="fphone" value="<%=HTMLValue.toValue( fax, "phone")%>" >
ext. <input type="text" name="fext" value="<%=HTMLValue.toValue( fax, "ext")%>" >
<input type="checkbox" name="fdomestic"
<%
    // If it is an international phone then check
    if ( ( HTMLValue.toValue( fax, "domestic") ).compareTo("false") == 0 ){
%>
checked
<%
    }
%>
> International Number
</td></tr>

<tr><td colspan="3"><hr/></td></tr>

<tr><td colspan="2"><input type="submit" name="submit" value="Submit">
<input type="submit" name="submit" value="Cancel"></td></tr>
</form>

</table>