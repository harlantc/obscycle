<%@ page session="true" import="java.util.*, info.User, info.ReportsConstants" %>
<%@ include file="reportsHead.html" %>


<body onload="window.focus();">
<div class="helpDiv">

<%@ include file = "cxcheader.html" %>

<div class="helphdr">
Chandra Panel Access Help Information 
</div>

<%
int timeoutValueMinutes = -1; 
String timeoutStr = (String)session.getAttribute("timeout");
if(timeoutStr != null) {
	//Get session timeout, if it exists
	timeoutValueMinutes = Integer.parseInt(timeoutStr);
}

int timeoutValueMS = timeoutValueMinutes * 60 * 1000;

Boolean beforePRBool = (Boolean)session.getAttribute("beforePR");
boolean beforePR = beforePRBool.booleanValue();

%>

<!-- This form is necessary for the javascript timeout function -->
<form name="exitForm" method="POST" action="/reports/reportsLogout">
<input type="hidden" name="operation" value = "NONE"> 
</form>



<% 
response.addCookie(new Cookie("JSESSIONID", session.getId()));

User currentUser = (User)session.getAttribute("user");
String type = currentUser.getType();

%>

<div class="helpbar">
<h2>General Information: </h2>
<b>Logout</b>:Please remember to logout of the site when finished editing/grading reports, by clicking the
"Logout" button.  There is no logout button on the report page itself, so please return to the
list of proposals, or the top-level menu, to exit the site. 
<br><br>

<% if(timeoutValueMinutes != -1) { %>
<b>Timeout</b>: The Panel Access Site has a <%=timeoutValueMinutes%> minute timeout period.  
If the application does not detect any activity for <%=timeoutValueMinutes%> minutes, it will log 
you out.  While viewing a report, a warning box will appear when you have 5 minutes left, to 
allow you to save any data.  If the full <%=timeoutValueMinutes%> minutes elapses with no 
activity, no further editing of the report can be done.  Instead, you will be directed to
a page indicating that the timeout period has been reached and provides you a link to return 
to the site to login again.  In addition, the report you were editing when you timed out will 
be available from the report the next time you login.
<% } %>
</div>
<p>
<div class="helpbar">

<% if(beforePR) { %>

<% } else { %>
<!-- *** DURING PEER REVIEW *** -->

<h2>Large Project View: </h2>
<% if(currentUser.isAdmin()) { %>
<!-- *** ADMIN DURING PEER REVIEW *** -->
Admins may edit the Large Project reports if they are in admin-edit mode. 
Otherwise, admins have view-only access to the reports.
<p>
<% } %>
Chairs/Deputy Chairs may view all the Large Project reports at the conference, 
but may only edit the reports for Large Project proposals on their panel.
Pundits may edit all the Large Project reports.
<p>
Large Project proposals are assigned to more than one panel, so there will be 
more than one Peer Review report for each Large Project proposal.  The Peer 
Review report is edited by the primary reviewers for the proposal, and as 
the proposal may be on multiple panels, there may be multiple Peer Review 
reports.  One of these Peer Review reports (that has been approved) will 
be used to initialize the Large Project report.  The alternate Peer Review 
report may be viewed from the report, via a link.  Chairs/Deputy Chairs will 
combine the Peer Review reports to form the Large Project report.
<p>


<% } %>




<table border="1">
<tr>
<th>Panel </th> 
<td>The panel assignment(s) for this Large Project Proposal.</td>
</tr><tr>
<th>Proposal</th> 
<td>
    Clicking on a proposal number will open the report where you access the
    editable comments and where you will indicate when the report is "complete".
    Only reports which have not been marked as completed may be edited.
</td>
</tr><tr>
<th>Type</th> 
<td>This displays the type of proposal.</td>
</tr><tr>
<th>Completed By</th> 
<td>This field will display the "Completed By" status for the
report.
<table>
<tr>
<td> Blank </td>
<td>
The report has not been started or the reviewer has edited the report but 
not approved it yet.</td>
</tr><tr>
<td>Panel Chair</td> 
<td>The report has been completed and approved by a Chair/Deputy Chair.
A Chair/Deputy Chair may no longer edit the report.</td>
</tr><tr>
<td>Pundit</td>
<td> The report has been approved by a Pundit.
Pundits may no longer edit the report without contacting CDO personnel.
</td>
</tr><tr>
<td>CDO</td>
<td> The report has been approved by CDO and is considered final.</td>
</tr>
</table>
</td>
</tr><tr>
<th>Last Updated</th>  
<td>This field displays a timestamp of the last instance when the report was
modified.  </td>
</tr><tr>
<th>Title</th> 
<td>This field displays the title of the proposal. 
</td>
</tr>
</table>

</div>

<p>
<%@ include file = "cxcfooterj.html" %>
</div>
  </body>
</html>
