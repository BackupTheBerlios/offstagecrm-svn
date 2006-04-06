<%@ include file="/jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>

<%
ResultSetArrayList phones = (ResultSetArrayList)sess.getAttribute( "phones" );
Iterator i = phones.iterator();

//  extension might be null - if there was no extension in the set of phone numbers
String extension = (String)sess.getAttribute( "extension" );
if ( extension == null ) extension = "";

// If any of these are false need option to add phone
boolean hasHome = false, hasWork = false, hasFax = false;
%>

<table>
<tr>
<td colspan="3">Phone Numbers</td>
</tr>
<tr><td colspan="3"><hr/></td></tr>
<%
    while ( i.hasNext() ){
        Map phone = (Map)i.next();
        String name = (String)phone.get("name");
        String number = (String)phone.get("phone");

        // Check if missing one of these types of phone numbers
        if ( name.compareTo("home") == 0 ) hasHome = true;
        else if ( name.compareTo("work") == 0 ) hasWork = true;
        else if ( name.compareTo("fax") == 0) hasFax = true;
        
        // If not extension then proceed as usual...
        if ( name.compareTo("extension") != 0 ){
%>
<tr><td><%=name%></td><td><%=number%>
<%
            // If extension exists attach it to the work number
            if ( name.compareTo("work") == 0 && extension.compareTo("") != 0 ){
%>
 ext. <%=extension%>
</td><td>
[<a href="<%=root%>/familystatus/EditPhones.jsp?phonetype=<%=name%>&number=<%=number%>&extension=<%=extension%>">Edit</a>]
[<a href="<%=root%>/DeletePhonesServlet?phonetype=<%=name%>&hasExtension=yes">Remove</a>]
<%
            // Else proceed as usual
            } else {
%>
</td><td>
[<a href="<%=root%>/familystatus/EditPhones.jsp?phonetype=<%=name%>&number=<%=number%>">Edit</a>]
[<a href="<%=root%>/DeletePhonesServlet?phonetype=<%=name%>">Remove</a>]
</td></tr>
<%
            }
        }
    }
%>

<tr><td colspan="3">
<%
    // If work number not found then give option to add one
    if ( !hasWork ){
%>
[<a href="<%=root%>/familystatus/InsertPhones.jsp?phonetype=work">Add Work Phone</a>]
<%
    }
%>
<%
    // If home number not found then give option to add one
    if ( !hasHome ){
%>
[<a href="<%=root%>/familystatus/InsertPhones.jsp?phonetype=home">Add Home Phone</a>]
<%
    }
%>
<%
    // If fax number not found then give option to add one
    if ( !hasFax ){
%>
[<a href="<%=root%>/familystatus/InsertPhones.jsp?phonetype=fax">Add Fax</a>]
<%
    }
%>
</td></tr>

<tr><td colspan="3"><hr/></td></tr>
<tr><td colspan="3">[<a href="<%=root%>/FamilyStatus.jsp">Home</a>]</td></tr>
</table>