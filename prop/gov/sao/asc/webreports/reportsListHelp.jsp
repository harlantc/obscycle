<%@ page session="true" import="java.util.*, info.User, info.ReportsConstants" %>

<%@ include file = "reportsHead.html" %>

<body onload="window.focus();">
<div class="helpDiv">

<%@ include file = "cxcheader.html" %>
<div class="helphdr">
Chandra Panel Access Help: Reports List 
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
<b>Timeout</b>: The Panel Access Site application has a <%=timeoutValueMinutes%> minute timeout period.  
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


<h2>List of Reports View: </h2>
This view displays all the review reports for proposals on which you are 
either a primary or secondary reviewer.  The top section lists the 
review reports for proposals for which you are the primary reviewer.  
The bottom section lists the reports on which you are the secondary
reviewer.
<p>
The <b>Printable </b> link on this page opens up a new window with all the 
reports in that section displayed.  You can then choose the <i>Print</i> 
option in the browser to actually print the 
reports. The link corresponds only to the reports in that section, eg. 
you can choose to print only the reports on which you are a primary reviewer, 
or only those on which you are a secondary reviewer.
<p>


<% } else { %>
<!-- *** DURING PEER REVIEW *** -->

<h2>List of Reports View: </h2>
This view is divided into 2 sections: 
<br>
<ul>
<li>Top section: This section lists the Peer Review reports for which you are 
the primary reviewer. These reports will be initialized with the data from 
the primary review report, which is written by the primary reviewer.  
Secondary reviewer comments are also included in the text section.  
From the report you will have access to view 
both the primary and secondary review report.
<p>
<li>Bottom section: This section lists the Peer Review reports on which you are 
the secondary reviewer.  You may view these reports, but you may not edit them.
You may also view the primary and secondary review reports, which are available
through links on the report itself.

</ul>

<p>
The primary reviewer will concatenate information from the reports
created by the primary and secondary reviewers to create the Peer Review report.
The primary reviewer will also add information to reflect the discussion of 
the panel.

<p>
The <b>Print All</b> links on this page open up a new window with all the 
"Peer Review" reports in that section displayed. Each link corresponds only 
to the reports in that section, eg. you can 
choose to print only the "Peer Review" reports on which you are a primary 
reviewer, or only those on which you are a secondary reviewer.  You can then 
choose the <i>Print</i> option in the browser to actually print the reports. 

<% } %>

The following gives a description of the fields appearing on the page:
<br><br>

<table border="1">
<tr>
<th>Proposal</th> 
<td>
    Clicking on a proposal number will open the report where you
    access the editable comments and where you will indicate when
    the report is "complete".  Only reports which have not been
    marked as completed may be edited.
</td>
</tr><tr>
<th>Type</th> 
<td>This displays the type of proposal.</td>
</tr><tr>
<th><%=ReportsConstants.APPLABEL %></th> 
<td>This field will display the completion status for the report. 
<table>
<tr>
<td> Blank </td>
<td>
The report has not been started or the reviewer has edited the report but 
not approved it yet.</td>
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
<th>Last Updated </th> 
<td>Last Updated is the timestamp of the most recent import of text to PAS
    from the report comments Google Doc. The PAS has automated imports on a
    regular schedule and will always import the latest version of the
    Google Docs document when you "complete" your report.
</tr><tr>
<th>Title</th> 
<td>This field displays the title of the proposal.
</td>
</tr><tr>
<th>Secondary&nbsp;Reviewer<br>Primary&nbsp;Reviewer</th>  
<td>Displays the other reviewer  assigned to this report.</td>
</tr>
<tr>
<th>Request Reassignment</th>
<td>Allows the reviewer to request reassignment of this proposal due to a conflict of interest or the appearance of a conflict of interest</td>
</tr>
</table>


</div>

<p>
<%@ include file = "footer.html" %>
</div>

  </body>
</html>
