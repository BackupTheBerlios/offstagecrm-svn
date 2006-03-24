<!-- header for JSPs that use the database. -->
<%@ include file="jsp_h.jsp" %>

<%@page import="java.sql.*"%>
<%@page import="citibob.sql.*"%>
<%
Connection dbb = (Connection)request.getAttribute("db");
Statement st = (Statement)request.getAttribute("st");
%>
