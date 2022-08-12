<%@ page session="true" import="java.util.*, info.* " %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1 
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {


response.addCookie(new Cookie("JSESSIONID", session.getId()));

String reviewerID = (String)session.getAttribute("reviewerID");
String msg = (String)session.getAttribute("reassignMsg");
Boolean canSave = ((Boolean)session.getAttribute("reassignEdit"));
if (canSave == null) {
   canSave= new Boolean(true);
}
if (msg == null) msg="";
ReassignRequest rr= (ReassignRequest)session.getAttribute("reassignReport");
User currentUser = (User)session.getAttribute("user");
String userName = "";
Integer userId = new Integer(0);
String reassignb = (String)session.getAttribute("reassignb");


if (currentUser != null) {
    userId  = new Integer(currentUser.getUserID());
    userName = currentUser.getUserName();
}
String cmt = "";
String propNum="Yikes";
String reassignType="";
if (rr != null) {
  propNum = rr.getProposalNumber();
  reassignType = rr.getReportType();
  cmt = StringEscapeUtils.escapeHtml4(rr.getComment());
   
}

boolean topMenuPage = false;
String cancelURL="";
if (!reassignb.equals("b")) {
  cancelURL="/reports/getReport.jsp?reviewerID=" + reviewerID + "&propNum=" + propNum;
} else {
  cancelURL="/reports/reviewReports.jsp";
}

  String rptHeader = "Reassignment Request";
  String helpPage = "";
  if (cmt == null) cmt="";
  String userType = new String (currentUser.getType());
  String returnStr = "/reports/login.jsp?file=NoFile";

%> 
<%@ include file = "reportsHead.html" %>
<body>

<form name="exitForm" method="POST" action="/reports/reportsLogout" target="_self">
<div id="cxcheaderplain">
<div class="menudiv">
<div class="hdrleft">
<a href="/" target="_top"><img src="cxcheaderlogo.png" alt="CXC Home Page" border="0"></a>
</div>
<div class="hdrcenter">
<img src="blank.gif" alt="" width="1" height="50">
<span class="mainhdr"><%= rptHeader %> </span>
<br>
<span class="mainhdr3">Proposal <%= propNum %> </span>
</div>
</div>
</div>
</form>

<p>
<form name="rform" method="POST" target="_self" action="/reports/reassignReport.jsp"  >
<input type=hidden name="userID" value="<%=userId.toString()%>">
<input type="hidden" name="reviewerID" value="<%=reviewerID%>">
<input type="hidden" name="reassignProp" value="<%=propNum%>">
<input type="hidden" name="reassignType" value="<%=reassignType%>">
<input type="hidden" name="operation" value="REASSIGN">
<table width="99%">
<tr> <td align="left">
<img src='arrow-left.png' alt=' ' class='backimg' onmouseover=\"this.className='backactv';\" onmouseout=\"this.className='backimg';\" onclick='rform.operation.value="<%=ReportsConstants.LISTPROPOSALS%>";rform.submit();'>
<input class="mainBtn" type="button" value="Back to list of proposals" onclick='rform.operation.value="<%=ReportsConstants.LISTPROPOSALS%>";this.form.submit();'>
</td>
<td> &nbsp;</td>
</tr>
</table>
<p>
<%= msg %>
<p>
<span style="font-weight:bold;">Proposal Number: <%= propNum %></span>
<p><span style="font-weight:bold;">Reason for reassignment:</span>
<textarea name="cmt" rows="10" cols="80" class="textpcmt"><%=cmt%></textarea>
<p>

<% if (canSave.booleanValue()) { %>
<span class="btnDiv">
<input type="submit" name="Submit" class="btn" value="Send Message" onclick="rform.operation.value='Submit';return true;" >
&nbsp;&nbsp;
<a class="cancelBtn" href="<%=cancelURL%>">Cancel</a>
</span>
<% } %>
</form>
<%@ include file = "footer.html" %>
</body>
</html>

<% } catch (Exception e) {};
 } %>

