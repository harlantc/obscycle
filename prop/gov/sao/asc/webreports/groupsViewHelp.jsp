<%@ page session="true" import="java.util.*, info.User, info.ReportsConstants" %>
<%@ include file="reportsHead.html"%>

<body onload="window.focus();">

<div class="helpDiv">

<%@ include file = "cxcheader.html" %>
<div class="helphdr">
Chandra Panel Access Help: Groups
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

<h2>Groups View:</h2>
<% if(currentUser.inChairMode()) { %>
This page allows you to enter group assignments for the proposals in your panel. 
<% } else if(currentUser.isAdmin()) { %>
This page allows you to view the group assignments for the proposals in 
this panel.  If you have
entered in administrator-edit mode, you may also edit the groups.  
<% } %>

A group name consists of a maximum 20 alphanumeric characters. Assign the
same group name to any proposals that you would wish to discuss together at
the Peer Review.
<p>
The group pulldown menu is built each time the page is loaded. It is 
initialized with group names assigned to the proposals for the current page. 
If you type in a new group, it is added to the pulldown menu. After the 'Save', 
the page is then reloaded and the group menu is re-initialized in sorted order.
<p>
<b>Save button:</b>Please save the form often.  If you leave this page, 
any values not saved will be lost. 
<p>
Clicking on the proposal number link will allow you to view the science justification for that proposal.
<br>The column labels allow you to sort the table based on that field.

</div>
<p>
<%@ include file = "footer.html" %>
</div>
  </body>
</html>
