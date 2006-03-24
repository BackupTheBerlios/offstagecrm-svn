<%@ include file="jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>

<%
ResultSetArrayList phones = (ResultSetArrayList)sess.getAttribute( "phones" );
Iterator i = phones.iterator();

//  extension might be null - if there was no extension in the set of phone numbers
String extension = (String)sess.getAttribute( "extension" );
%>

<table>
<tr>
<td colspan="2">Phone Numbers</td>
</tr>
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

<tr><td colspan="2">
[<a href="<%=root%>/AddPhones.jsp">Add Phone</a>]
[<a href="<%=root%>/RemovePhones.jsp">Remove Phone</a>]
</td></tr>

<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2">[<a href="<%=root%>/FamilyStatus.jsp">Home</a>]</td></tr>
</table>