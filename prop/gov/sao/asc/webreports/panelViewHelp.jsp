<%@ page session="true" import="java.util.*, info.User, info.ReportsConstants" %>

<%@ include file="reportsHead.html" %>


<body onload="window.focus();">
<div class="helpDiv">
<%@ include file = "cxcheader.html" %>

<div class="helphdr">
Chandra Panel Access Help:  Panel 
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
<p>

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
<!-- *** BEFORE PEER REVIEW *** -->

<%  if(currentUser.isAdmin()) { %>
<!-- *** ADMIN BEFORE PEER REVIEW *** -->
<h2>Panel View: </h2>
<br>
This view allows you to read or edit any report on this panel.  You may use 
the drop-down menu to view the reports of a particular reviewer, or use 
the "Go to secondary reports" link on the left to go to the secondary 
review report section.  
<p>
You can print the primary or secondary reports for a particular reviewer 
by using the "Print Primary Reports" or "Print Secondary Reports" links 
next to that reviewer's name.  The link will open
up a new window, with all the reports displayed in a printer-friendly format.  
Use the browser's print button to actually print the reports.

<p>
If you are using the administrator-edit or admin
account, you will have access to view the Google Doc reports, but only administrator-edit
will be able to Complete reports.

<% } %>

<% } else { %>
<!-- *** DURING PEER REVIEW *** -->
<% if(currentUser.inChairMode()) { %>

<!-- *** CHAIR DURING PEER REVIEW *** -->
<h2>Panel View: </h2>
<br>
This view allows you to read or edit any report on this panel.  
You may use the drop-down menu to view the reports of a particular reviewer.

<% } else if(currentUser.isAdmin()) { %>

<!-- *** ADMIN DURING PEER REVIEW *** -->
<h2>Panel View: </h2>
<br>
This view allows you to read (or edit if you are in admin-edit mode) 
any Peer Review report on this panel.  You may use the drop-down menu to 
view the reports of a particular reviewer.

<% } %>
<p>
You can print the Peer Review reports for a particular reviewer by using 
the "Print Peer reports" links next to that reviewer's name.  The link 
will open up a new window, with all the reports displayed 
in a printer-friendly format.  Use the browser's print button to actually 
print the reports.
<% } %>

<p>
Fields in the view:
<table border="1">
<tr>
<th>Reviewer&nbsp;Name:</th>
<td>This field displays the last name of the primary reviewer for the proposal.
</td>
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
<th><%=ReportsConstants.APPLABEL%></th>
<td>This field will display the <b><%=ReportsConstants.APPLABEL%></b> status for the
report.
<table>
<tr>
<td> Blank </td>
<td>The report has not been edited/saved.</td>
</tr><tr>
<td>Reviewer</td>
<td>The report has been completed and approved by the reviewer.
The reviewer may no longer edit the report.</td>
</tr><tr>
<td>Panel</td>
<td> The report has been approved by the Chair/Deputy Chair.
The panel may no longer edit the report without contacting CDO personnel.
</td>
</tr><tr>
<td>CDO</td>
<td> The report has been approved by CDO and is considered final.</td>
</tr>
</table>
</td>
</tr><tr>
<th>Last Updated</th>
<td>Last Updated is the timestamp of the most recent import of text to
    PAS from the report comments Google Doc. The PAS has automated imports
    on a regular schedule and will always import the latest version of
    the Google Docs document when you "complete" your report.
</td>
</tr><tr>
<th>Title</th>
<td>This field displays the title of the proposal. 
</td>
</tr><tr>
<th>Secondary Reviewer</th>
<td>This field displays the name of the secondary reviewer assigned to this
proposal.  </td>
</tr>
</tr><tr>
    <th>Prelim Complete</th>
    <td>When a reviewer Completes their preliminary report, a gold circle will be displayed here. </td>
</tr>
</table>

</div>

<p>
<%@ include file = "cxcfooterj.html" %>
</div>
  </body>
</html>
