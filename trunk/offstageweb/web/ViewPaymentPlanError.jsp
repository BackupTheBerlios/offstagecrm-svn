<%@ include file="/jsp_h.jsp" %>
<%@page import="offstage.web.*"%>
<%@page import="offstage.web.collections.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%
// If has enrolled in a course during this session...then show 'Finished Enrolling'
// instead of 'Back' at the end of the enrollment screen
ResultSetArrayList currentPP = (ResultSetArrayList)sess.getAttribute( "currentPP" );
Map ppinvoices = (Map)sess.getAttribute( "ppinvoices" );
ResultSetArrayList enrollmentsWOpplan = (ResultSetArrayList)sess.getAttribute( "enrollmentsWOpplan" );
ArrayList pplantypeidsmenu = (ArrayList)sess.getAttribute("pplantypeidsmenu");
ArrayList paymenttypeidmenu = (ArrayList)sess.getAttribute("paymenttypeidmenu");
ArrayList cctypemenu = (ArrayList)sess.getAttribute("cctypemenu");
Map paymentplan = (Map)sess.getAttribute("paymentplan");

// If missing any of the above parts then return to /GetFamilyStatusServlet
if ( currentPP == null || ppinvoices == null || enrollmentsWOpplan == null ||
        paymenttypeidmenu == null || pplantypeidsmenu == null || cctypemenu == null
        || paymentplan == null ){
    response.sendRedirect( request.getContextPath() + "/GetFamilyStatusServlet" );
    return;
}
// Else get iterators and show info
Iterator i = currentPP.listIterator();
Iterator ii = enrollmentsWOpplan.listIterator();
Iterator iii = pplantypeidsmenu.iterator();
Iterator iiii = paymenttypeidmenu.iterator();
Iterator iiiii = cctypemenu.iterator();

String cctypeid = (String)paymentplan.get( "cctypeid" );
Integer pplantypeid = (Integer)paymentplan.get( "pplantypeid" );
Integer paymenttypeid = (Integer)paymentplan.get( "paymenttypeid" );
Integer month = (Integer)paymentplan.get( "month" );
Integer year = (Integer)paymentplan.get( "year" );
String ccnumber = (String)paymentplan.get( "ccnumber" );
String name = (String)paymentplan.get( "name" );
String badinput = (String)paymentplan.get("badInput");

Logic logic = new Logic();
%>

<table>
<tr><td colspan="7">Family Payment Plans </td></td>
<tr><td colspan="7" bgcolor="red">Input Error: <%=badinput%></td></td>
<tr><td colspan="7"><hr/></td></td>
<%
if ( i.hasNext() ){
%>
<tr><td colspan="7">Current Payment Plans in Effect:</td></td>

<tr style="background-color:#ffcc66">
<td>pplanid</td><td>termname</td>
<td>planname</td><td>Total Bill</td><td>Remaining Balance</td>
<td>Credit Card Type</td><td>ccnumber</td></tr>
<%
    while ( i.hasNext() ){
        Map pp = (Map)i.next();
        Integer pplanid = (Integer)pp.get("pplanid");
%>
<tr ><td><%=pp.get("pplanid")%></td><td><%=pp.get("termname")%></td>
<td><%=pp.get("planname")%></td>
<td><%=pp.get("amountpp")%></td>
<td><%=pp.get("remainpp")%></td>
<td><%=pp.get("paymentname")%></td>
<td><%=pp.get("ccnumber")%></td></tr>
<%
        ResultSetArrayList invoices = (ResultSetArrayList)ppinvoices.get(pplanid);
        Iterator iiiiii = invoices.listIterator();
%>
<tr>
<td></td>
<td></td>
<td>Invoices</td></tr>
<tr style="background-color:#ffcc66">
<td style="background-color:#ffffff"></td>
<td style="background-color:#ffffff"></td>
<td>Invoice ID</td>
<td>Total Due</td>
<td>Remaining Invoice Balance</td>
<td>Due Date</td>
<td>Date Paid</td>
</tr>
<%
        while ( iiiiii.hasNext() ){
            Map invoice = (Map)iiiiii.next();
%>
<tr>
<td></td>
<td></td>
<td><%=invoice.get("invoiceid")%></td>
<td><%=invoice.get("amount")%></td>
<td><%=invoice.get("remain")%></td>
<td><%=invoice.get("ddue")%></td>
<td><%=invoice.get("dtime")%></td>
</tr>   
<%
        }
    }
}
%>
<tr><td colspan="7"><hr/></td></tr>
<tr><td colspan="7">
<table>
<form method="POST" action="<%=root%>/InsertPaymentPlanServlet">
<%
if ( ii.hasNext() ){
%>
<tr><td colspan="4">Enrollments Without a Payment Plan:</td></tr>
<tr style="background-color:#ffcc66"><td>COURSE</td><td>TERM</td><td>DATES</td><td>MEETING TIMES</td></tr>
<%
    while ( ii.hasNext() ){
        Map course = (Map)ii.next();
        String dayofweek = logic.getDayOfWeek( ((Integer)course.get( "dayofweek" )) );            
        String firstdate = logic.getSimpleDate( (java.util.Date)course.get("firstdate"), "MMMdd" );
        String nextdate = logic.getSimpleDate( (java.util.Date)course.get("nextdate"), "MMMdd" );
        String tstart = logic.getSimpleTime( (java.sql.Time)course.get("tstart"), DateFormat.SHORT );
        String tnext = logic.getSimpleTime( (java.sql.Time)course.get("tnext"), DateFormat.SHORT );
%>
<tr>
<td><%=course.get("course_name")%></td>
<td><%=course.get("term_name")%></td>
<td><%=firstdate%> to <%=nextdate%></td>
<td><%=HTMLValue.toValue(dayofweek)%>, <%=HTMLValue.toValue(tstart)%> to <%=HTMLValue.toValue(tnext)%></td>
</tr>
<%
    }
%>
<tr><td colspan="4"><hr/></td></td>
<tr><td colspan="4">Create A Payment Plan For Enrollments:</td></tr>
<tr>
<td>Payment Plan</td>
<td>
<select name="pplantypeid">
<%
    while ( iii.hasNext() ) {
        Map menuitem = (Map)iii.next();
        Integer id = (Integer)menuitem.get("value");
%>
<option value="<%=id%>"
<%
        if ( id != null && pplantypeid != null && id.compareTo(pplantypeid) == 0 ){
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
<td>Payment Type</td>
<td>
<select name="paymenttypeid">
<%
    while ( iiii.hasNext() ) {
        Map menuitem = (Map)iiii.next();
        Integer id = (Integer)menuitem.get("value");
%>
<option value="<%=id%>"
<%
        if ( id != null && paymenttypeid != null && id.compareTo(paymenttypeid) == 0 ){
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

<tr><td colspan="2">**If payment type is credit card please fill in credit card info:</td></tr>
<tr>
<td>Credit Card Type</td>
<td>
<select name="cctypeid">
<%
    while ( iiiii.hasNext() ) {
        Map menuitem = (Map)iiiii.next();
        String id = (String)menuitem.get("value");
%>
<option value="<%=id%>"
<%
        if ( id != null && cctypeid != null && id.compareTo(cctypeid) == 0 ){
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
<td>Name On Credit Card</td>
<td><input type="text" name="name" value="<%=name%>"><font color="#FF0000"></font></td>
</tr>

<tr>
<td>Credit Card Number</td>
<td><input type="text" name="ccnumber" value="<%=ccnumber%>" ><font color="#FF0000"></font></td>
</tr>

<tr>
<td>Credit Card Expiration</td>
<td>
<select name="month">
<%
    int m = 1;
    while ( m < 13 ) {
%>
<option value="<%=m%>"
<%
        if ( month != null && month.intValue() == m ){
%>
selected
<%
        }
%>
><%=m%></option>
<%
        ++m;
    }
%>
</select> 
<select name="year">
<%
    Calendar c = Calendar.getInstance();
    int y = c.get(Calendar.YEAR);
    int lastyear = y + 10;
    while ( y < lastyear ) {
%>
<option value="<%=y%>"
<%
        if ( year != null && year.intValue() == y ){
%>
selected
<%
        }
%>
><%=y%></option>
<%
        ++y;
    }
%>
</select> 
</td>
</tr>

</td></tr>
<tr><td colspan="4"><hr/></td></tr>
<tr><td colspan="4"><input type="submit" name="submit" value="Create New Payment Plan">
<input type="submit" name="submit" value="Back"></td></tr>
</form>
</table>
<%
// Else if there are no enrollments with out payment plans then skip 
// all the inputs and show just the back button
} else {
%>
<tr><td><input type="submit" name="submit" value="Back"></td></tr>
<%
}
%>
</tr>
<tr><td colspan="7"><hr/></td></tr>
</table>