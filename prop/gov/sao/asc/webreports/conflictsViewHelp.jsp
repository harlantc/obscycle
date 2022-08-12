<%@ page session="true" import="java.util.*, info.User, info.ReportsConstants" %>
<%@ include file = "reportsHead.html" %>

<body onload="window.focus();">
<div class="helpDiv">
<%@ include file = "cxcheader.html" %>
<div class="helphdr">
Chandra Panel Access Help: Reviewer Conflicts 
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
<b>Logout</b>:Please remember to logout of the site when finished by clicking the
"Logout" button after <b>saving</b> your changes.  


<% if(timeoutValueMinutes != -1) { %>
<p><b>Timeout</b>: The Panel Access Site has a <%=timeoutValueMinutes%> minute timeout period.  
If the application does not detect any activity for <%=timeoutValueMinutes%> minutes, it will log 
you out. 
<% } %>
</div>
<p />
<div class="helpbar">

<h2>Reviewer Conflicts: </h2>
    Deputy chair should enter any additional reviewer conflicts and/or any
    actions taken to mitigate conflicts in the comment field for each proposal.
<p>
    The Panel Member Conflict column lists the names of anyone identified ahead
    of the Review as having either a strong institutional or personal conflict with this proposal.
<p>
Clicking on the proposal number link will allow you to view the science justification for that proposal.
<br><br>
<b>Save button:</b> Please save the form often.  If you leave this page, any values not saved will be 
lost.
<br>
</div>
<p>
<%@ include file = "footer.html" %>
</div>
</body>
</html>
