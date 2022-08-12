<%@ page session="true" import="java.util.*, info.User, info.ReportsConstants" %>

<%@ include file = "reportsHead.html" %>

<body onload="window.focus();">
<div class="helpDiv">

<%@ include file = "cxcheader.html" %>

<div class="helphdr">
 Chandra Panel Access Help: Main Menu 
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


<% String backLink = (String)session.getAttribute("backLink"); %>
<form name="theform" method="POST" action="/reports/<%=backLink%>">
<!-- back link button not needed as this should open in a new window. -->
<!--<input type="button" value="Return to previous page" onClick="this.form.submit();"> -->
</form>


<% 
response.addCookie(new Cookie("JSESSIONID", session.getId()));

User currentUser = (User)session.getAttribute("user");
String type = currentUser.getType();

String reportString= "";
String prelimGradesString= "";
String downloadString = "";
String techString="";
  techString = "<u>Technical/Proposer Inputs:</u> This option allows you to view all the technical reviews and proposer input for all proposals on the panel.";

if(currentUser.isChair() || currentUser.isPundit()) { 
  downloadString = "<u>Download </u> the panel or BPP file containing the Science Justifications, RPS forms, conflict information and proposal lists . The BPP file contains all the Large Project/X-ray Visionary Project proposals that were submitted .";
} else {
  downloadString = "<u>Download </u> the panel file containing the Science Justifications, RPS forms, conflict information and proposal lists for your panel. ";
}
%>

<div class="helpbar">
<h2>General Information: </h2>
<b>Logout</b>:Please remember to logout of the site when finished editing/grading reports, by clicking the
"Logout" button.  There is no logout button on the report page itself, so please return to the list of
proposals, or the top-level menu, to exit the site. The logout button will exit you both from
the Panel Access Site, as well as the Reviewer's (RWS) site.
<p>

<% if(timeoutValueMinutes != -1) { %>
<b>Timeout</b>: The Panel Access Site has a <%=timeoutValueMinutes%> minute timeout period.  If the 
application does not detect any activity for <%=timeoutValueMinutes%> minutes, it will log you out.  While
viewing a report, a warning box will appear when you have 5 minutes left, to allow you to save any data. 
If the full <%=timeoutValueMinutes%> minutes elapses with no activity, no further editing of the report 
can be done.  Instead, you will be directed to a page indicating that the timeout period has been reached
and provides you a link to return to the site to login again.  In addition, the report you were editing
when you timed out will be available from the report the next time you login.
<% } %>
<br>
</div>
<p>
<div class="helpbar">

<!-- *** BEFORE PEER REVIEW *** -->
<% if(beforePR) { 
   prelimGradesString = "<u>Assign preliminary grades:</u> This option allows you to assign preliminary grades for all the proposals that are on your panel.  These preliminary grades are used to determine which proposals will be triaged during the Peer Review.";

   reportString = "<u>Edit your Primary and Secondary Review Reports:</u> This option allows you to edit or view the reports for which you are either a primary or secondary reviewer.  The reports for the proposals on which you are a primary reviewer will be used to initialize the Peer Review reports that you will edit during the Peer Review.  Secondary reviewer comments will also be included when creating the Peer Review report.";
%>

<% if(currentUser.isReviewer()) { %>
<!-- *** REVIEWER BEFORE PEER REVIEW *** -->
<h2>Main Menu View:</h2>
From the main menu, your options:
<ul>
<li><%=reportString%>
<p>
<li><%=techString%>
<p>
<li><u>View Proposal Group Assignments:</u> This link allows you to view the
proposal groups being assigned by the Chair of your panel.  
<p>
<li><%=prelimGradesString%>
<p>
<li><%=downloadString%>
</ul>

<% } else if(currentUser.inChairMode() || currentUser.isPundit()) { %>
<!-- *** CHAIR BEFORE PEER REVIEW *** -->
<h2>Main Menu View:</h2>
From this main menu, you will have three options:<br>
<ul>
<li><%=reportString%>
<p>
<li><%=techString%>
<p>
<p>
<% if(currentUser.isChair()) { %>
<li><u>Create/Modify Proposal Group Assignments:</u> This link allows you to create and edit proposal
groups for the proposals on your panel.
<% }  else { %>
<li><u>View Proposal Group Assignments:</u> This link allows you to view the
proposal groups being assigned by the Chair of your panel.  
<% }  %>

<p>
<li><%=prelimGradesString%>
<p>
<li><%=downloadString%>
</ul>

<% } else if(currentUser.isAdmin()) { %>
<!-- *** ADMIN BEFORE PEER REVIEW *** -->
<h2>Main Menu View:</h2>
<ul>
<li><u>Review Reports :</u>  Clicking on a panel allows you to 
view all the review reports on a particular panel.  You can view both the 
primary and secondary review reports for any
panel. 
 Only the administrator-edit account has access to edit the review 
reports, otherwise, admins have view-only access to the reports.
<p>
<li><u>Reviewer Conflicts</u>: Clicking on a panel allows you to view reviewer conflicts for a particular panel.  
Only the administrator-edit account has access to edit the reviewer conflicts.
<p>
<li><u>Proposal Groups:</u>  Clicking on a panel allows you to view the proposal groups 
created by the chairperson of this panel.  If you are using the 
administrator-edit account, you may edit these groupings, otherwise, 
you have view-only access.
<p>
<li><u>Preliminary Grades:</u>  Clicking on a panel allows you to view the 
preliminary grades for each reviewer on a particular panel.  
You may also view all grades by proposal for a selected panel.
Preliminary grades may NOT be edited by an administrator.
<p>
<li><u>Reviewers List:</u>  Clicking on a panel allows you to view the 
primary and secondary reviewers for each proposal on a particular panel.  
<p>
<li><%=techString%>
<% if (currentUser.canAdminEdit()) { %>
<p>
<li><u>Switch to Reviewer:</u>  Clicking on a panel allows you to view all the 
panel members. You may select a reviewer and you will re-enter the Panel Access Site as that reviewer. You must <b>Logout</b> to re-enter with administrative privileges.
<% } %>
<p>
<li><u>View Preliminary Grades given by Primary/Secondary Reviewers</u> 
This link will allow you to view the preliminary grades given by the primary 
and secondary reviewers for all proposals.
<p>
<li><u>View Grade Submission Status</u> This link will allow you to view
which reviewers have failed to submit any preliminary grades. It will also
display the date that preliminary grades were last updated for each
reviewer.
</ul>

<% } %>





<% } else { %>
<!-- *********************************************** -->
<!-- ************ AT/DURING PEER REVIEW ************ -->

<% 
  String printableString = "<u>Printable Primary Review reports :</u> This option allows you to view and print all primary review reports for which you are the primary reviewer.  <p> <li><u>Printable Secondary Review reports :</u> This option allows you to view and print all secondary review reports for which you are the secondary reviewer.";
  prelimGradesString = "<u>View your preliminary grades </u> This link will allow you to view the preliminary grades assigned by you. In addition you can access the RPS Forms and Science Justifications for all proposals on your panel.";
     reportString = "<u>Edit your Peer Review Reports :</u> This option allows you to edit the Peer Review reports for proposals on which you are a primary reviewer. You also have view-only access to the Peer Review reports for proposals on which you are a secondary reviewer.  As a reviewer for any proposal, you may also view the primary and secondary review reports for that proposal.  <p> The Peer Review report will be initialized with the data from the primary review report, although secondary reviewer comments will also be included in the text fields.";

  if(currentUser.isReviewer()) { 
%>
    

<!-- *** REVIEWER AT PEER REVIEW *** -->
<h2>Main Menu View:</h2>
<ul>
<li><%=reportString%>
<p>
<li><%=techString%>
<p>
<li><%=printableString%>
<p>
<li><u>View Proposal Group Assignments:</u> This link allows you to view the
proposal groups assigned by the Chair of your panel.  
<p>
<li><%=prelimGradesString%>
<p>
<li><%=downloadString%>
</ul>


<% } else if(currentUser.inChairMode() || currentUser.isPundit()) { %>
<!-- *** CHAIR AT PEER REVIEW *** -->
<h2>Main Menu View:</h2>
<ul>
<li><%=reportString%>
<p>
<li><u>Approve Peer Review reports for all proposals in panel:</u> This option 
allows you to view the Peer Review reports for all the proposals on your panel.
You will need to double-check each of the reports and mark it as 
"Approved By: Panel" when you have reviewed the report.
<p>
<li><u>View primary/secondary reviewers:</u> This option allows you to view all
the primary and secondary reviewers for all proposals in the panel.
<p>
<li><%=techString%>
<p>
<li><%=printableString%>
<p>
<li><u>Read reports for all Large/X-Ray Visionary Proposals at the review:</u>  
This link allows you to read the Peer Review reports for all the Large/X-Ray Visionary proposals at the review. This option provides you with read-only access. 
Please note that as these proposals are on more than one
panel, the proposal will have more than one Peer Review report.
This option is not available the first two days of the review while the
Peer Review reports are being written by the panels.
<p>
<li><u>Edit reports for Large/X-Ray Visionary Proposals in your panel's subject area:</u>  
This link allows you to edit the Peer Review reports for the Large/X-Ray Visionary 
proposals that are in your panel's subject areas..  
This option is not available the first two days of the review while the
Peer Review reports are being written by the panels.

<p>
<li><u>Edit and View the Reviewer Conflicts</u> : This option allows you to view/edit the 
Reviewer Conflicts for your panel. You should take note of any conflict a 
reviewer has with a proposal on your panel and state what actions were taken
to mitigate the conflict.
<p>
<li><u>View your Proposal Group Assignments:</u> This link allows you to view  the  proposal groups for the proposals on your panel. If you need to change groups assignments during the Peer Review, please talk to your Panel Facilitator.
<p>
<li><%=prelimGradesString%>
<p>
<li><%=downloadString%>

</ul>



<% } else if(currentUser.isAdmin()) { %>
<!-- *** ADMIN AT PEER REVIEW *** -->
<h2>Main Menu View:</h2>
<br>
<ul>
<li><u>Review Reports :</u>  Clicking on a panel allows you to 
view all the review reports on a particular panel.  
 Only the administrator-edit account has access to edit the review 
reports, otherwise, admins have view-only access to the reports.
<p>
<li><u>Reviewer Conflicts</u>: Clicking on a panel allows you to view reviewer 
conflicts for a particular panel.  
Only the administrator-edit account has access to edit the reviewer conflicts.
<p>
<li><u>Proposal Groups:</u>  Clicking on a panel allows you to view the proposal groups 
created by the chairperson of this panel.  If you are using the 
administrator-edit account, you may edit these groupings, otherwise, 
you have view-only access.
<p>
<li><u>Preliminary Grades:</u>  Clicking on a panel allows you to view the 
preliminary grades for each reviewer on a particular panel.  
You may also view all grades by proposal for a selected panel.
Preliminary grades may NOT be edited by an administrator.
<p>
<li><u>Reviewers List:</u>  Clicking on a panel allows you to view the 
primary and secondary reviewers for each proposal on a particular panel.  
<p>
<li><%=techString%>
<% if (currentUser.canAdminEdit()) { %>
<p>
<li><u>Switch to Reviewer:</u>  Clicking on a panel allows you to view all the 
panel members. You may select a reviewer and you will re-enter the Panel Access Site as that reviewer. You must <b>Logout</b> to re-enter with administrative privileges.
<% } %>
<p>
<li><u>View Preliminary Grades given by Primary/Secondary Reviewers</u> 
This link will allow you to view the preliminary grades given by the primary 
and secondary reviewers for all proposals.
<p>
<li><u>View Grade Submission Status</u> This link will allow you to view
which reviewers have failed to submit any preliminary grades. It will also
display the date that preliminary grades were last updated for each
reviewer.
</ul>

<% } else if(currentUser.isFacilitator()) { %>
Click the link in the <i>Proposal</i> column to view the PDF file for this proposal.



<% }//close if user is facilitator %>

<% }//close if during peer review %>

</div>
<p>
<%@ include file = "footer.html" %>
</div>

  </body>
</html>
