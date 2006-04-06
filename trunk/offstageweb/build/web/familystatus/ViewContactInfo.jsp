<%@ include file="/jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>

<%
Map contactInfo = (Map)sess.getAttribute( "contactInfo" );

ResultSetArrayList phones = (ResultSetArrayList)sess.getAttribute( "phones" );
Iterator i = phones.iterator();

//  extension might be null - if there was no extension in the set of phone numbers
String extension = (String)sess.getAttribute( "extension" );
%>
<table>

<tr><td colspan="2" >Contact Info[<a href="<%=root%>/UpdateContactInfo.jsp">Edit</a>]</td></tr>
<tr><td colspan="2"><hr/></td></tr>
<tr>
<td>Address To</td>
<td>
<%=contactInfo.get("customaddressto")%>
</td></tr>

<tr><td>Address 1</td><td>
<%=contactInfo.get("address1")%>
</td></tr>

<tr><td>Address 2</td><td>
<%=contactInfo.get("address2")%>
</td></tr>

<tr><td>City</td>
<td>
<%=contactInfo.get("city")%>
</td></tr>

<tr><td>State</td>
<td>
<%=contactInfo.get("state")%>
</td></tr>

<tr><td>Zip</td>
<td>
<%=contactInfo.get("zip")%>
</td></tr>

<tr><td>Country</td><td>
<%=contactInfo.get("country")%>
</td></tr>

<tr>
<td>Email</td>
<td>
<%=contactInfo.get("email")%>
</td>
</tr>

<tr><td colspan="2"><hr/></td></tr>
<tr>
<td colspan="2">Phone Numbers[<a href="<%=root%>/UpdatePhones.jsp">Edit</a>]
</td></tr>
<tr><td colspan="2"><hr/></td></tr>
<%
    while ( i.hasNext() ){
        Map phone = (Map)i.next();
        String name = (String)phone.get("name");
        String number = (String)phone.get("phone");
        if ( name.compareTo("extension") != 0 ){
%>
<tr><td><%=name%></td><td><%=number%>
<%
            // If extension exists attach it to the work number
            if ( name.compareTo("work") == 0 && extension != null ){
%>
 ext. <%=extension%>
<%
            }
%>
</td></tr>
<%
        }
    }
%>	
<tr><td colspan="2"><hr/></td></tr>

<tr><td colspan="2">[<a href="<%=root%>/FamilyStatus.jsp">Home</a>]</td></tr>
