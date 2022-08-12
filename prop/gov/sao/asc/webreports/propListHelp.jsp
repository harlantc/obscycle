<%@ page session="true" import="java.util.*, info.User, info.ReportsConstants" %>
<%@ include file="reportsHead.html" %>


<body onload="window.focus();">

<div class="helpDiv">


<%@ include file = "cxcheader.html" %>
<div class="helphdr">
Chandra Panel Access Help: Proposal List 
</div>


<!-- This form is necessary for the javascript timeout function -->

<form name="exitForm" method="POST" action="/reports/reportsLogout">
<input type="hidden" name="operation" value = "NONE"> 
</form>



<% 
response.addCookie(new Cookie("JSESSIONID", session.getId()));

User currentUser = (User)session.getAttribute("user");

%>

<div class="helpbar">
<h2>General Information: </h2>
<b>Logout</b>:Please remember to logout of the site when finished editing/grading reports, by clicking the
"Logout" button.  There is no logout button on the report page itself, so please return to the
list of proposals, or the top-level menu, to exit the site. 
<br><br>

</div>
<p>
<div class="helpbar">
<p>
The column labels allow you to sort the table based on that field.
<br> <br>
<table border="1">
<tr>
<th>Proposal</th>
<td>
Clicking on a proposal number will allow you to view the science justification and proposal parameters for that proposal.
</td>
</tr><tr>
<th>Primary Reviewer</th>
<td>Primary reviewer assigned to proposal for the current panel.</td>
</tr><tr>
<th>Secondary Reviewer</th>
<td>Secondary reviewer assigned to proposal for the current panel.</td>
</tr><tr>
<th>Title</th>
<td>This field displays the title of the proposal.
</td>
</tr>
</table>


</div>
<p>
<%@ include file = "footer.html" %>

</div>
  </body>
</html>
