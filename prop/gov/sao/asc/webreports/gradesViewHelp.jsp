<%@ page session="true" import="java.util.*, info.User, info.ReportsConstants" %>
<%@ include file="reportsHead.html" %>


<body onload="window.focus();">

<div class="helpDiv">


<%@ include file = "cxcheader.html" %>
<div class="helphdr">
 Chandra Panel Access Help: Preliminary Grades
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

<!-- *** BEFORE PEER REVIEW *** -->

<% if(currentUser == null || !currentUser.isAdmin()) { %>
<!-- *** REVIEWER BEFORE PEER REVIEW *** -->
<h2>Preliminary Grades: </h2>
Please enter preliminary grades for all proposals on your panel for which you do not have
a conflict.  These grades will be used to triage proposals at the start of the review.  Since 
we recommend triage of the lowest 25% of proposals when ranked by preliminary grade, your grades 
are important. Only grades entered by the deadline will be 
used at the <i>Chandra Peer Review</i>.
<p>
Grades should be between 0.00 and 5.00 with 0.00 being the lowest.  Note that a grade of 0 is 
usually reserved for a proposal that is technically infeasible in some way. If a conflict of
interest exists, and you are unable to evaluate the proposal, please notify the Director's Office.
Note that you cannot enter a grade for a proposal that has a check in the 'Conflict' box.
<p>
Clicking on the proposal number link will allow you to view the science justification for that proposal.
<br><br>
<b>Save button:</b> Please save the form often.  If you leave this page, any values not saved will be 
lost. You may save as many times as you like before the deadline.
<br>
<br>
The column labels allow you to sort the table based on that field.
<br>

<% } else if(currentUser.isAdmin()) { %>
<!-- *** ADMIN BEFORE PEER REVIEW *** -->
<h2>Preliminary Grades: </h2>
As an administrator, you may view the preliminary grades entered by the reviewers.
You may either view the grades by reviewer (after selecting a particular panel), or
you may view the preliminary and secondary reviewer's grades for each proposal.
Regardless of whether you are in admin-edit mode or not, you may NOT edit the grades.


<% } %>


</div>
<p>
<%@ include file = "footer.html" %>

</div>
  </body>
</html>
