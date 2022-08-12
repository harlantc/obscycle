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
%>

<!-- This form is necessary for the javascript timeout function -->
<form name="exitForm" method="POST" action="/reports/reportsLogout">
<input type="hidden" name="operation" value = "NONE"> 
</form>


<% String backLink = (String)session.getAttribute("backLink"); %>
<form name="theform" method="POST" action="/reports/<%=backLink%>">
<!-- back link button not needed as this should open in a new window. -->
<!--<input type="button" value="Return to previous page" onClick="this.form.submit();"> -->
</form>


<% 
response.addCookie(new Cookie("JSESSIONID", session.getId()));

User currentUser = (User)session.getAttribute("user");
String type = currentUser.getType();

if(currentUser.isReviewer() ||
   currentUser.inChairMode()) {
%>

<div class="helpbar">
<h2>General Information: </h2>
<b>Logout</b>:Please remember to logout of the site when finished editing/grading reports, by clicking the
"Logout" button.  There is no logout button on the report page itself, so please return to the<br>
list of proposals, or the top-level menu, to exit the site. <br><br>

<% if(timeoutValueMinutes != -1) { %>
<b>Timeout</b>: The Panel Access Site has a <%=timeoutValueMinutes%> minute timeout period.  If the application does not
detect any activity for <%=timeoutValueMinutes%> minutes, it will log you out.  While viewing a report, a warning box
will appear when you have 5 minutes left, to allow you to save any data.  If the full <%=timeoutValueMinutes%> minutes
elapses with no activity, no further editing of the report can be done.  Instead, you will be directed to
a page indicating that the timeout period has been reached and provides you a link to return to the site
to login again.  In addition, the report you were editing when you timed out will be available from the report
the next time you login.<br>
<% } %>

</div>
<p>
<div class="helpbar">
<h2>Personal  Conflicts: </h2>
    Please list individuals with whom you have a personal or professional
    relationship that may preclude an unbiased review. It is not necessary to
    list individuals who work at the same institution. Current  institutional
    conflicts will be identified by conflict-checking software.
    <br><br>
    Personal conflicts include:
    <br><br>
    <b>Family and close personal friends:</b> Examples include spouse/domestic
    partner, children, parents or people with whom you have an on-going
    friendship that extends beyond the workplace or collaborative social networks.
    <br><br>
    <b>Science competitors or people with whom you have had a serious personal
    disagreement:</b> Examples include someone with whom you've had an
    antagonistic scientific disagreement or professional conflict, or a former
    spouse/domestic partner.
    <br><br>
    <b>Close professional collaborators:</b> Examples include people you work
    with closely to write proposals and papers, members of your research group
    including current graduate students and postdocs. People who have recently
    (within about 3 years) moved from your immediate circle of collaborators
    also count as conflicts.
    <br><br>
    The examples above are not exhaustive and there are always grey area
    conflicts. Examples include people you may have been close friends with as
    a student or postdoc but contact is now sporadic, or members of large
    teams/collaborations.  You can probably trust your own judgment as to
    whether these are likely to bias your review, but also be aware that even
    the <b>potential appearance of bias</b> is a concern.  Feel free to contact the
    Director's Office via Helpdesk to discuss borderline conflicts.
    In general, we expect most people to have relatively few (~3-10) close
    contacts, with higher numbers likely for more senior researchers.
    <br><br>
</div>
<p>
<div class="helpbar">
<h2>List of Reports View: </h2>
This view will display all the review reports available for editing.  Before the Peer Review,
both the primary and secondary review reports will be available.  During the Peer Review, however,
only the peer review reports may be edited.  Only primary reviewers may edit the peer review report,
although the secondary reviewer may view it. <br><br>

The following gives a brief description of the fields appearing on the page:<br><br>

<b>Proposal Number:</b> Clicking on a proposal number will allow you to edit the report.
Only reports which have not been completed (or checked-off by a chair at the peer review) may be
edited.  <br><br>

<b>Status:</b> This field will display the status of the report.  For primary and secondary reports,
COMPLETE is the only value that will appear in the field, after the Completed button has been clicked
on the report page.  At Peer Review, when reports will be checked-off by the chair person, the status may
also have the value CHECKEDOFF, to indicate that the chair has approved the report.<br><br>

<b>Title:</b> This field displays the first 30 characters of the title.  Therefore, the title
may be longer than what is displayed. <br><br>

<b>Last Updated:</b>  This field displays a timestamp of the last instance when the report was
modified.  <br><br>

<% } 

if(currentUser.inChairMode()) {
%>
<hr>
<h2>Panel View:</h2>

<b>Report type:</b> Prior to peer review, the panel view will be inaccessible.  At peer <br>
review, however, only peer review reports will be displayed. <br><br>

<b>Status field:</b> This field will either display the timestamp of the last update of the <br>
report, or the status of the report as either completed or checked-off.<br>


<% } 

if(currentUser.isAdmin() ||
   currentUser.isDeveloper()) {
  //Display help info for admins and developers here
%>

<h2>Panel View:</h2>

<b>Report type:</b> Prior to peer review, the panel view will display primary review reports. <br>
At peer review, however, the peer review reports will be displayed. <br><br>

<b>Status field:</b> This field will either display the timestamp of the last update of the <br>
report, or the status of the report as either completed or checked-off.<br>


<% } %>
</div>
<p>

<%@ include file = "footer.html" %>
</div>
  </body>
</html>
