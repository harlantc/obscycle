<%@ page session="true" import="java.util.*, info.User, info.ReportsConstants" 
%>

<%@ include file = "reportsHead.html" %>

<body onload="window.focus();">
<div class="helpDiv">

<%@ include file = "cxcheader.html" %>

<div class="helphdr">
Chandra Panel Access Help Information
</div>

<div class="helpbar">
<h3>General Information: </h3>
<ul>
<li><b>Logout</b>:Please remember to logout of the site when finished editing/grading reports, by clicking the
"Logout" button.  There is no logout button on the report page itself, so please return to the list of
proposals, or the top-level menu, to exit the site. The logout button will exit you both from
the Panel Access Site, as well as the Reviewer's (RWS) site.
</ul>
</div>
<p>
<div class="helpbar">
<p>
<h3>Review Report Actions: </h3>
<ul>
<li><b>Completed</b>: This button imports the latest version of the comments document,
  saves the report, and sets the completion status of the report.
  The report is considered finished at this point and will no longer be editable.
  For Preliminary Reports written before the Peer Review, clicking the Completed button lets the
  Director's office know the Preliminary Report has been finished.
<p>If the report is not editable, the <b>Completed By:</b> field will show the
current completion status of the report.  The status options are as follows:
<table border="1">
<tr>
<td> Blank </td>
<td>
The report has not been started or the reviewer has edited the report but 
not completed it yet.
</td>
</tr><tr>
<td>Reviewer</td> 
<td>The report has been completed and approved by the reviewer.
The report is now available for the Chair/Deputy Chair of the panel to review.
If the report has been completed by the Panel, the reviewer may no longer edit the report without contacting the Chair/Deputy Chair.  
If the report has not been completed by the Panel, the reviewer may use the <b>Un-Complete</b> button to re-edit the report.
</td>
</tr><tr>
<td>Panel</td>
<td> The report has been approved by the Chair/Deputy Chair of the panel.
If the report has not been completed by CDO, the chair/deputy chair may use 
the <b>Un-Complete</b> button to re-edit the report.
</td>
</tr><tr>
<td>CDO</td>
<td> The report has been approved by CDO and is considered final.</td>
</tr>
</table>
  Completion status options do not apply to the Preliminary Report.
<p><li><b>Uncomplete</b>: Use this button to re-edit a previously 'Completed' report.
<p><li>
<b>Request Reassignment due to conflicts:</b> Please use this button to request reassignment of this proposal due to a conflict of interest or the appearance of a conflict of interest.
<p><li>
<b>Printer-friendly:</b> Click on this link to open up a new window with 
the report displayed in printer-friendly format.  All the values in the report 
will be displayed in format suitable for printing.
</ul>
</div>
<p>
<%@ include file="reportFieldHelp.html" %>
<p>

<%@ include file="footer.html" %>
</div>
  </body>
</html>
