<%@ include file="jsp_h.jsp" %>
<table>
<tr><td style="font-size:x-large;" colspan="2">Enter Child Information:</td></tr>
<form method="POST" action="<%=root%>/InsertChildServlet">
<tr><td colspan="2"><hr/></td></td>

<tr>
<td>First Name</td>
<td><input type="text" name="firstname"><font color="#FF0000"></font></td>
</tr>

<tr>
<td>Last Name</td>
<td><input type="text" name="lastname"><font color="#FF0000"></font></td>
</tr>

<tr>
<td>Middle Name</td>
<td><input type="text" name="middlename"><font color="#FF0000"></font></td>
</tr>

<tr>
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
<td><input type="Text"  maxlength="10" name="dob" size="10" value="mm/dd/yyyy" autocomplete=off >
</tr>

<tr>
<td>Email</td>
<td><input type="text" name="email" size="28"><font color="#FF0000"></font></td>
</tr>

<tr>
<td>Child's Relation to Adult</td>
<td>
<select name="relprimarytype">
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