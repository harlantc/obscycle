<%@ page session="true" import="java.util.*, info.User" %>
<%@ page import="info.ReviewReport, info.Reports" %>
<%@ page import="info.Proposal, info.ReportsConstants" %>


<%@ include file = "reportsHead.html" %>
<body onload="setScrollPosition('panel_scroll_y=');" >	

<% 
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {


response.addCookie(new Cookie("JSESSIONID", session.getId()));
response.setHeader("Cache-Control","no-store"); //HTTP 1.1

boolean topMenuPage = false;
String helpPage = new String("/reports/panelViewHelp.jsp");
String proposalFileDir = (String) session.getAttribute("proposalFileDir");
String reportsDataPath = (String) session.getAttribute("reportsDataPath");
User currentUser = (User)session.getAttribute("user");
String currentAO = (String)session.getAttribute("currentAO");
String userName = currentUser.getUserName();
int userID = currentUser.getUserID();
String userType = currentUser.getType();
String panelName = currentUser.getPanelName();
Boolean isAnonymous = (Boolean)session.getAttribute("isAnonymous");
if (isAnonymous == null) isAnonymous=true;
Vector reportsList = (Vector) session.getAttribute("reportsList");
String subHeading = new String("Panel " + panelName + " List");
String appLabel = ReportsConstants.APPLABEL;
int previousReviewerID = -1;
String lastUpdate="";
String status="";
String rowClass="alt1";
String dmsg="";

Boolean beforePRBool = (Boolean)session.getAttribute("beforePR");
boolean beforePR = beforePRBool.booleanValue();

String colspan="7";
String hrcolspan="9";
if (beforePR) {
    colspan="6";
    hrcolspan="8";
}

session.setAttribute("panelName", panelName);
%> 

<%@ include file = "header.jsp" %>

<div class="instructspanbar">
<% if (currentUser.isAdmin() && !currentUser.canAdminEdit()) { %>
This interface allows you to view the reports for all proposals in this panel.
<%
   }  else if (currentUser.isAdmin()) {
     if (beforePR) {
%>
This interface allows you to update the primary/secondary reports for all 
the proposals in this panel. 
<%    } else { %>
This interface allows you to update and approve reports for all the proposals in 
this panel. Please read and set the <b><%=ReportsConstants.APPLABEL%></b> to <i>CDO</i> 
for each report.
<p>
If a chair/deputy chair needs to re-edit a previously
completed report, just reset the <b><%=ReportsConstants.APPLABEL%></b> to <i>Reviewer</i>.
If a reviewer needs to re-edit a previously
completed report, just reset the <b><%=ReportsConstants.APPLABEL%></b> to 'blank'.
<br>
<%   }
   } else { 
%>
This interface allows you to update and approve reports for all the proposals in your panel. 
<br>A reviewer has finished editing a report if the <b><%=ReportsConstants.APPLABEL%></b> column is set to 'Reviewer'.
<br>After the reviewer has finished editing the report, please read and click the <b><%=ReportsConstants.APPLABEL%></b>
button for each report before leaving the review.
<ul>
<li> If a reviewer needs to re-edit a previously
completed report, just click the <b><%=ReportsConstants.UNAPPLABEL%></b> button.
</ul>


<% } %>
</div>
<p>

<% if(beforePR) { %>
<A href="#secondaryReports">Go to secondary reports</a>
<% } %>

<%
int hindex;
String rptType = new String("No ");
String reviewerOptions = new String("");
String secReviewerOptions = new String("");
for(hindex=0; hindex < 1 && hindex < reportsList.size(); hindex++) {
  ReviewReport hh = (ReviewReport)reportsList.get(hindex);
  rptType = hh.getType();
}
int oldID = -1;
int oldID2 = -1;
for(hindex=0; hindex < reportsList.size(); hindex++) {
   ReviewReport hh = (ReviewReport)reportsList.get(hindex);
   if(!beforePR || hh.getType().equals(ReportsConstants.PRIMARY)) {
     if (oldID == -1 || oldID != hh.getReviewerID()) {
       String revlink = "<option value=\"" + hh.getReviewerName() + "\">" + hh.getReviewerName();
       reviewerOptions += revlink;
       oldID = hh.getReviewerID();
     }
   }
   else if (hh.getType().equals(ReportsConstants.SECONDARY)) {
     if (oldID2 == -1 || oldID2 != hh.getReviewerID()) {
       String revlink = "<option value=\"" + hh.getReviewerName() + "Sec\">" + hh.getReviewerName();
       secReviewerOptions += revlink;
       oldID2 = hh.getReviewerID();
     }
   }
}
%>
  
<table width="99%"> 

<tr>
<th class="xlargebc" colspan="<%=hrcolspan%>">
<%= rptType %> Review Reports <select name="reviewerOptions" onChange='doSelect(this);'><%= reviewerOptions %></select>
</th>
</tr>


<%

  int listSize = reportsList.size();
  int index = 0;

  String baseJSPParam = new String("userID="+ userID);
  baseJSPParam += "&amp;mode=" + userType; 
  String baseLink= new String("");
  
  // To get prelimComp, need to load results from file, not just load from db.
  // For anon proposals, report PI is null, but want to display PI for admin.
  // Store PI in hashmap by proposal so it's not overwritten as null for admin.
  Map<String, String> propPI = new HashMap<String, String>();
  for(index=0; index < listSize; index++) {
     ReviewReport rr = (ReviewReport)reportsList.get(index);
     String reportType = rr.getType();
     String PI = rr.getPI();
     String propNum = rr.getProposalNumber();
     propPI.put(propNum, PI);
     String secLast = rr.getSecondaryReviewerName();
     if(secLast == null) secLast="&nbsp;";
     String techLink = "";
     String techFile = rr.getTechnicalFile();
     if (techFile != null && techFile.length() > 1) { 
        String techURL = "/reports/displayFile.jsp?fileName=" + techFile; 
        techURL = response.encodeURL(techURL);
        techLink = "<a href=\"" + techURL + "\" target=\"techRev\" class=\"tech\" >Tech</a>";
     }
     String pinputLink = "";
     String pinputFile = rr.getProposerInputFile();
     if (pinputFile != null && pinputFile.length() > 1) {
       String pinputURL = "/reports/displayFile.jsp?fileName=" + pinputFile;
       pinputURL = response.encodeURL(pinputURL);
       pinputLink = "<a href=\"" + pinputURL + "\" target=\"pinputRev\" class=\"pi\" >PI</a>";
     }
     if (rowClass.equals("alt1"))
       rowClass="alt2";
     else
       rowClass="alt1";

     int reviewerID = rr.getReviewerID();
     String getReportJSPParam = baseJSPParam;
     getReportJSPParam += "&amp;reviewerID=" + reviewerID;
     getReportJSPParam += "&amp;propNum=" + propNum;
     session.setAttribute("reviewerID", new Integer(reviewerID));
     rr.loadReport(userName, reportsDataPath, proposalFileDir, beforePR, false);
     //System.err.println("propNum = " + propNum + " reviewer id = " + reviewerID + " type = " + reportType);
     if(!beforePR || reportType.equals(ReportsConstants.PRIMARY)) {

	if(previousReviewerID == -1 || previousReviewerID != reviewerID) {
           baseLink = rr.getReviewerName();
%>

<tr><td colspan="<%=colspan%>">&nbsp;</td><td colspan="2">&nbsp</td></tr>
<tr class="panel"><td colspan="<%=colspan%>"><a name="<%=baseLink %>" ></a>&nbsp;</td> 
<td colspan="2" ><a href="/reports/printReports?printType=<%=rr.getType()%>&amp;reviewerID=<%=rr.getReviewerID()%>" target="_blank">Printable <%=reportType%> Reports for <b><%=rr.getReviewerName()%></b></a></td>
</tr> 
<tr class="header">
<th>Reviewer Name</th>
<th>Proposal</th>
<th>Type</th>
<% if (!beforePR) { %>
    <th><%=appLabel%></th>
<% } %>
<th>Last Updated</th>
<% if (!isAnonymous) { %>
    <th>P.I.</th>
<% } %>
<th>Title</th>
<th>Secondary Reviewer</th>
<th>Prelim Complete</th>
</tr> 

<% }  %>
<tr class="<%=rowClass%>" onmouseover="this.className='hover'" onmouseout="this.className='<%=rowClass%>'" >
<td align="left"> <%= rr.getReviewerName() %> </td> 
<td align="left"><a href="/reports/getReport.jsp?<%=getReportJSPParam%>" onclick="getScrollPosition('panel_scroll_y=');">
<%=propNum %></a> <%=techLink %> <%=pinputLink%></td>
<td align="center"><%= rr.getProposalType() %>
<% 
//Display a timestamp if the report isn't completed or checked off
status= rr.getStatus();
lastUpdate = rr.getLastUpdate();
if (status.length() < 2 && lastUpdate.length() > 2) {
  status =   "in-progress";
}
if (status.length() < 2 && lastUpdate.length() < 2) {
  status="&nbsp;";
  lastUpdate="&nbsp;";
}
if (!beforePR && rr.isNewerCmt()) {
  lastUpdate += "<img src='gold.gif' alt='new'>";
}

%>

<% if (!beforePR) { %>
    <td align="center"> <%= status %> </td>
<% } %>
<td align="center"> <%= lastUpdate %> </td>
<% if (!isAnonymous) { %>
    <td align="left"> <%= propPI.get(propNum) %>     </td>
<% } %>
<td align="left" > <%= rr.getTitle() %>     </td>
<td align="center"> <%= secLast %>     </td>
<% if (rr.getPreComp().equalsIgnoreCase("true")) { %>
    <td align="center"> <img src='gold.gif' alt='new'> </td>
<% } else { %>
    <td></td>
<% } %>
</tr> 

<% 
 	}
    previousReviewerID = reviewerID;
} 
%>

</table>
<%

  int numSecondary = ReviewReport.getNumSecondary(reportsList);
  if(numSecondary > 0) {

%>

<p>
<table width="99%">
<tr>
<th class="xlargebc" colspan="<%=hrcolspan%>">
<a name="secondaryReports">Secondary Review Reports</a> <select name="secReviewerOptions" onChange='doSelect(this);'><%= secReviewerOptions %></select>
<p>
</th>
</tr>




<%
}
  previousReviewerID = -1;
  baseLink = "";

   
  for(index=0; index < listSize; index++) {
     ReviewReport rr = (ReviewReport)reportsList.get(index);
     String reportType = rr.getType();
     String propNum = rr.getProposalNumber();
     int reviewerID = rr.getReviewerID();
     String getReportJSPParam = baseJSPParam;
     getReportJSPParam += "&amp;reviewerID=" + reviewerID;
     getReportJSPParam += "&amp;propNum=" + propNum;
     String rname = rr.getReviewerName();

     if (rowClass.equals("alt1"))
       rowClass="alt2";
     else
       rowClass="alt1";
     if(reportType.equals(ReportsConstants.SECONDARY) && beforePR) {

	if((previousReviewerID == -1 || previousReviewerID != reviewerID) &&
	rname != null) {
          baseLink = rr.getReviewerName() + "Sec";
          rowClass="alt2";
%>

<tr><td colspan="<%=colspan%>">&nbsp;</td><td colspan="2">&nbsp</td></tr>
<tr> <td colspan="<%=colspan%>"><a name="<%=baseLink %>" ></a>&nbsp;</td> 
<td colspan="2" ><a href="/reports/printReports?printType=<%=rr.getType()%>&amp;reviewerID=<%=rr.getReviewerID()%>" target="_blank">Printable <%=reportType%> Reports for <b><%=rr.getReviewerName()%></b></a></td>
</tr>
<tr class="header"> 
<th>Reviewer Name</th>
<th>Proposal Number</th>
<th>Type</th>
<% if (!beforePR) { %>
    <th><%=appLabel%></th>
<% } %>
<th>Last Updated</th>
<% if (!isAnonymous) { %>
    <th>P.I.</th>
<% } %>
<th>Title</th>
<th>Primary Reviewer</th>
<th>Prelim Complete</th>
</tr> 
<%
}
 if (rname != null ) {
%>


<tr class="<%=rowClass%>" onmouseover="this.className='hover'" onmouseout="this.className='<%=rowClass%>'" >
<td align=left> <%= rr.getReviewerName() %> </td> 
<td align=left><a href="/reports/getReport.jsp?<%=getReportJSPParam%>">
<%=propNum %> </a> </td>
<td align="center"><%= rr.getProposalType() %></td>
<% 
//Display a timestamp if the report isn't completed or checked off
status= rr.getStatus();
lastUpdate = rr.getLastUpdate();
if (status.length() < 2 && lastUpdate.length() > 2) {
  status =   "in-progress";
}
if (status.length() < 2 && lastUpdate.length() < 2) {
  status="&nbsp;";
  lastUpdate="&nbsp;";
}
%>

<% if (!beforePR) { %>
<td align="center"> <%= status %> </td>
<% } %>
<td align="center"> <%= lastUpdate %> </td>
<% if (!isAnonymous) { %>
<td align="left"> <%= propPI.get(propNum) %> </td>
<% } %>
<td align="left"><%=rr.getTitle()%></td>
<td align="center"> <%= rr.getPrimaryReviewerName() %> </td>
<% if (rr.getPreComp().equalsIgnoreCase("true")) { %>
    <td align="center"> <img src='gold.gif' alt='new'> </td>
<% } else { %>
    <td></td>
<% } %>
</tr> 

<% 
}
	  previousReviewerID = reviewerID;
	}
}
  if(numSecondary > 0) {
%>
</table>
<% } %>

<hr>
<p>
<form name="mainMenuForm" action="/reports/login.jsp">
<%=topMenuBtn%>
</form>

<%@ include file = "footer.html" %>
<% } catch (Exception e) {};
 } %>
  </body>

</html>


