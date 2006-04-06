<%@ include file="db_h.jsp" %>
<%
ResultSet rs = st.executeQuery("select * from courseids");
%>
[<a href="<%=root%>/GetFamilyStatusServlet">Family Status</a>]
        
<p>This is a sample JSP page.</p>

<% while (rs.next()) { %>
    Course <%= rs.getInt("courseid") %> is called <%= rs.getString("name") %><br/>
<% } %>

<p>Click <a href="<%=root%>/SampleServlet">here</a> for the sample servlet.</p>
