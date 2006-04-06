<%@ include file="/jsp_h.jsp" %>
<%
    String dob = (String)sess.getAttribute( "dob" );
    String firstname = (String)sess.getAttribute( "firstname" );
    sess.setAttribute( "badInput", "Child must be under age 19" );
%>
According to the date of birth given (<%=dob%>), 
<%=firstname%> is over 18 and must register as an adult.<br/>
To register as an adult: [New Student Registration]<br/>
To edit <%=firstname%>'s date of birth:[<a href="<%=root%>/ChildInfoError.jsp">Edit Child Info</a>]
[<a href="<%=root%>/FamilyStatus.jsp">Home</a>]