<%@ page session="true" import="java.util.*, info.User,info.ReviewReport " %>
<%@ page import="info.ReportsConstants,org.apache.commons.lang3.StringEscapeUtils" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1 %>


<%@ include file = "reportsHead.html" %>


<body >	


<!-- This form is necessary for the javascript timeout function -->
<form name="exitForm" method="POST" action="/reports/reportsLogout">
<input type="hidden" name="operation" value = "NONE"> 
</form>

<% 
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {


response.addCookie(new Cookie("JSESSIONID", session.getId()));
String fileName = request.getParameter("fileName");
String reportsDataPath = (String)session.getAttribute("reportsDataPath");
Vector printList = null;
ReviewReport rr = null;
if (fileName == null) {
   printList = (Vector)session.getAttribute("printList");
}
if (printList == null) {
  printList = new Vector();
}
User currentUser = null;
String mode = null;
int reviewerID = -1;
String proposalNum = null;

if(fileName != null) {
    String fullFilename = reportsDataPath + "/" ;
    if (fileName.indexOf(ReportsConstants.TIMEDOUTREP) > 0) {
       fullFilename +=  ReportsConstants.TMPDIR + "/";
    }
    fullFilename +=   fileName;
    rr = new ReviewReport();
    rr.setDataPath(reportsDataPath);
    rr.readReportFromFile(fullFilename);
    printList.addElement(rr);
    
} else if (printList.size() <= 0) {
    currentUser = (User)session.getAttribute("user");
    mode = currentUser.getMode();
    rr = (ReviewReport)session.getAttribute("report");
    if (rr != null) {
      printList.addElement(rr);
    }
}

//If during peer review, display the good proposals radio buttons
Boolean beforePRBool = (Boolean)session.getAttribute("beforePR");
Boolean isAnonymous = (Boolean)session.getAttribute("isAnonymous");
if (isAnonymous == null) isAnonymous=true;
boolean beforePR = beforePRBool.booleanValue();
if (printList.size() <= 0) {
%>
No reports found.

<% } else { 


for (int rptIndex=0; rptIndex < printList.size(); rptIndex++) {

  rr = (ReviewReport)printList.get(rptIndex);
  reviewerID = rr.getReviewerID();
  proposalNum = rr.getProposalNumber();

  String targetsTaken = rr.getTargetsTaken();
  String TOOLimited = rr.getTOOLimited();
  String constrainedLimited = rr.getConstrained();
  String header = rr.getType(); 
  if (header.equals(ReportsConstants.LP)) {
    header= ReportsConstants.BPP;
  }
  header += " Review Report for " + rr.getProposalNumber(); 

  String multicycle = "";
  if (rr.getProposalMulticycle() != null && 
      rr.getProposalMulticycle().equalsIgnoreCase("Y")) {
     multicycle = "&nbsp;&nbsp;Multicycle";
  }
     

%> 
<div class="printspanbar">
<div class="techhdr">
<%= header %>
</div>
<table> 
<tr>
<td><b>Subject Category:</b></td>
<td> <%= rr.getCategory() %> </td>
</tr>

<% if (!isAnonymous) { %>
<tr>
<td><b>P.I. Name:</b></td>
<td> <%= rr.getPI() %> </td>
</tr>
<% } %>

<tr>
<td><b>Proposal Title:</b></td>
<td> <%= StringEscapeUtils.escapeHtml4(rr.getTitle()) %> </td>
</tr>

<tr>
<td><b>Type:</b></td>
<td> <%= rr.getProposalType() %> <%=multicycle%> </td>
</tr>

<tr>
<td><b>Constrained Targets:</b>&nbsp;&nbsp;</td>
<td> <%= rr.getConstrainedTargets() %></td>
</tr>
<tr>
<td><b>Joint:</b>&nbsp;&nbsp;</td>
<td> <%= rr.getProposalJoint() %></td>
</tr>
</table>
<p>
<b><%= ReportsConstants.COMMENTS %>:</b>
<pre>
<%= StringEscapeUtils.escapeHtml4(rr.getCommentsWrapped()) %> 
</pre>
<b><%= ReportsConstants.RECOMMENDATIONS %>:</b>
<pre>
<%= StringEscapeUtils.escapeHtml4(rr.getRecsWrapped()) %>
</pre>
<!--
<b><%= ReportsConstants.REASON %>:</b>
<pre>
<%= StringEscapeUtils.escapeHtml4(rr.getGradeReasonWrapped()) %> 
</pre>
-->
<% if ((rr.isPeer() || rr.isSecondaryPeer() || rr.isLP()) &&
     rr.getTechnicalReview()!= null && rr.getTechnicalReview().length() > 2 ) {
%>
<b><%= ReportsConstants.TECHRPT %>:</b>
<pre>
<%= StringEscapeUtils.escapeHtml4(rr.getTechnicalReviewWrapped()) %> 
</pre>

<% } %>

<table>
<tr>
<td><b><%= ReportsConstants.EFFORT %>:</b><br><%=ReportsConstants.EFFORT2%></td>
<td style="vertical-align:top;"> <%= rr.getEffort() %> </td>
</tr>
</table>
</div>
<p class="pagebreakhere">

<% } %>
<% } %>


<% } catch (Exception e) {};
 } %>

  </body>
</html>



