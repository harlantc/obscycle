<%@ page session="true" import="java.util.*, info.User, info.ReviewReport" %>
<%@ page import="info.ReportsConstants" %>


<%@ include file = "reportsHead.html" %>
<body onload="setScrollPosition('lp_scroll_y=');" onunload="getScrollPosition('lp_scroll_y=');">
<script  type="text/javascript">
$(document).ready(function() {


  $("#bppTable").tablesorter( {
        widgets: ["saveSort","zebra"],
	emptyTo: 'none'
  });

});

</script>

<% 
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {

response.addCookie(new Cookie("JSESSIONID", session.getId()));
response.setHeader("Cache-Control","no-store"); //HTTP 1.1 

String helpPage = new String("/reports/lpViewHelp.jsp");

boolean topMenuPage = false;
User currentUser = (User)session.getAttribute("user");
String currentAO = (String)session.getAttribute("currentAO");
String userName = currentUser.getUserName();
int userID = currentUser.getUserID();
String userType = currentUser.getType();
boolean chairMode = currentUser.inChairMode();
Boolean bppFileExists = (Boolean)session.getAttribute("bppFile");
String bppAccess = (String)session.getAttribute("bppAccess");
Boolean isAnonymous = (Boolean)session.getAttribute("isAnonymous");
if (isAnonymous == null) isAnonymous=true;
Vector reportsList = (Vector) session.getAttribute("reportsList");
boolean beforePR = ((Boolean)session.getAttribute("beforePR")).booleanValue();
boolean editLPReports = false;
Boolean editLPReportsBool = (Boolean)session.getAttribute("editLPReports");
if(editLPReportsBool != null) {
  editLPReports = editLPReportsBool.booleanValue();
}

String titleType = "Big Project Panel";
int listSize = reportsList.size();
String subHeading = ReportsConstants.BPP;

if(editLPReports) {
   subHeading = ReportsConstants.BPP;
}
if (userType.equalsIgnoreCase("Pundit") ) {
//  topMenuPage = true;
  
}

String astr = "";
String bppTar = (String)session.getAttribute("bppTar");

%> 



<%@ include file = "header.jsp" %>
<div class="instructspanbar">
Chairs and Pundits will edit the Big Project reports. 
<br> Each report will be initialized with data from the Peer Review reports available from the panels. 
<% if(editLPReports) { %>
<br>Click on a link to view or edit the report. 
<% astr = "&access=edit";
   } else { 
%>
<br>Click on the link to view the report. 
<% } %>
<p>
<%
   if (currentUser.isPundit() || !chairMode || (chairMode && !editLPReports)) {
     if (bppFileExists.booleanValue() == true) {
       if (bppAccess.equals("all") ) {
%>
Access <a href="/reports/viewLP?bppAccess=sub<%=astr%>">subset of <%=ReportsConstants.BPPTYPE%> proposals</a> under discussion.<br>
<%     } else {
%>
Access <a href="/reports/viewLP?bppAccess=all<%=astr%>">all of the <%=ReportsConstants.BPPTYPE%> proposals</a>.<br>

<%     }
     }
   } 

   if (bppTar != null) { 
%>
Download the <a href="<%=bppTar%>"><%=ReportsConstants.BPPTYPE%></a> file with science justifications, RPS forms, and lists. 
<% } %>

</div>
<p>

<div style="width:99%">
<span style="font-size:x-large;font-weight:bold;float:left;margin-left:50px;"><%= titleType%> Review Reports</span>
<span style="float:right;"><a href="/reports/printReports?printType=<%=ReportsConstants.LP%>&amp;reviewerID=-1" target="_blank">Printable Reports </a> </span>
</div>
<table id="bppTable" class="tablesorter" width="99%" cellspacing="2"> 
<thead>
<tr class="header"> 
<th>Panel</th> 
<th>Proposal</th> 
<th>Type</th>
<th><%=ReportsConstants.APPLABEL%></th>
<th>Last Updated</th>
<% if (!isAnonymous) { %>
<th>P.I.</th>
<% } %>
<th align="left">Proposal Title</th>
</tr> 
</thead>
<tbody>


<%
  for(int index=0; index < listSize; index++) {
     ReviewReport rr = (ReviewReport)reportsList.get(index);
     String propNum = rr.getProposalNumber();
     String reportType = rr.getType();
     int reviewerID = rr.getReviewerID();
     String reportStatus = rr.getStatus();
     if (reportStatus.equals(ReportsConstants.CHECKOFF)) {
        reportStatus="Pundit";
     }
     if (reportStatus.equals(ReportsConstants.COMPLETE)) {
        reportStatus="Panel Chair";
     }
    String lastUpdate = rr.getLastUpdate();
    if (reportStatus.length() < 2 && lastUpdate.length() > 2) {
          reportStatus =   "in-progress";
    }
    if (reportStatus.length() < 2 && lastUpdate.length() < 2) {
          reportStatus="&nbsp;";
          lastUpdate="&nbsp;";
    }
    if (rr.isNewerCmt()) {
      lastUpdate += "<img src='gold.gif' alt='new'>";
    }


     String proposalType = rr.getProposalType();
     String linkedProposal = new String("");
     if (proposalType.equals("ARCHIVE")) {
       linkedProposal = "LP:" + rr.getLinkedProposalNumber();
     }
     String techLink = "";
     String techFile = rr.getTechnicalFile();
     if (techFile != null && techFile.length() > 1) {
        String techURL = "/reports/displayFile.jsp?fileName=" + techFile;
        techURL = response.encodeURL(techURL);
        techLink = "<a href=\"" + techURL + "\" target=\"techRev\" class=\"tech\">Tech</a>";
     }

     String pinputLink = "";
     String pinputFile = rr.getProposerInputFile();
     if (pinputFile != null && pinputFile.length() > 1) {
       String pinputURL = "/reports/displayFile.jsp?fileName=" + pinputFile;
       pinputURL = response.encodeURL(pinputURL);
       pinputLink = "<a href=\"" + pinputURL + "\" target=\"pinputRev\" class=\"pi\">PI</a>";
     }

    String pnames = rr.getPanels();
    pnames = pnames.replaceAll(",LP","");
    pnames = pnames.replaceAll("LP","");
    
%>
<tr >
<td align="left"><%= pnames %> </td>
<td align="left"><a href="/reports/getReport.jsp?reviewerID=<%=userID%>&amp;mode=<%=userType%>&amp;type=LP&amp;propNum=<%= propNum %>"><%= propNum %></a> <%=techLink%> <%=pinputLink%><br>
<%= linkedProposal %></td>
<td align="left"><%= proposalType %> </td>
<td align="center"> <%= reportStatus %> </td> 
<td align="center"> <%= lastUpdate %> </td> 
<% if (!isAnonymous) { %>
<td align="left"> <%= rr.getPI() %> </td> 
<% } %>
<td align="left" > <%= rr.getTitle() %> </td> 
</tr> 


<% 
} //end for loop
%>


</tbody>
</table>
<p>
<form name="mainMenuForm" method="POST" action="/reports/login.jsp" target="_self">
<%=topMenuBtn%>
</form>

<%@ include file = "footer.html" %>
<% } catch (Exception e) {};
 } %>

  </body>
</html>


