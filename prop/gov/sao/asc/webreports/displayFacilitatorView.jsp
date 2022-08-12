<%@ page session="true" import="java.util.*, info.User, info.ReportsConstants" %>
<%@ page import="info.ReviewReport, info.ReportsConstants" %>

<%@ include file = "reportsHead.html" %>


<body>	

<% 
response.addCookie(new Cookie("JSESSIONID", session.getId()));
response.setHeader("Cache-Control","no-store"); //HTTP 1.1

//for the header.jsp file not to display the "return to top menu" link
boolean topMenuPage = true;
User currentUser = (User)session.getAttribute("user");
String currentAO = (String)session.getAttribute("currentAO");
session.setAttribute("reportsID", new Integer(currentUser.getUserID()));
String userName = currentUser.getUserName();
Vector reportsList = (Vector) session.getAttribute("reportsList");
String panelName = currentUser.getPanelName();
String subHeading="";
String helpPage = null; //Let the header.jsp file figure this out
String conflictURL = new String("/reports/assignConflicts.jsp");
%>


<%@ include file = "header.jsp" %>
<script type="text/javascript">
$(document).ready(function() {
  $("#csvTable").tablesorter({
	widgets: ["zebra"],
	headers: {
	0: { sorter:'inputs'} 
	}
     });
});
</script>


<div class="instructspanbar">
<p>
Press the <b>Refresh</b> link to update the display.
<p>
View the <a href="<%=conflictURL%>">Reviewer Conflicts</a> for your panel.
</div>

<form name="theForm" method="POST" action="/reports/updateReport">
<input type="hidden" name="reportIndex" value ="" >
<input type="hidden" name="operation" value ="<%=ReportsConstants.NAMEUNLOCK%>" >
<div>
<div class="facdivLink">
<a href="/reports/login.jsp?file=NoFile">Refresh</a>
</div>
<div class="facdiv">
Panel <%=panelName%>
</div>
</div>
<br>
<div style="clear:both;float:left">
<table id="csvTable" class="tablesorter" cellspacing="5"> 
<thead>
<tr class="header">

<th>Proposal</th>
<th align="center"><%=ReportsConstants.APPLABEL%></th>
<th align="left">P.I.</th>
<th align="left">Primary Reviewer</th>
<th align="left">Secondary Reviewer</th>
<th align="left">Proposal Title</th>
</tr> 
</thead>

<% 
int index=0;
int listSize = reportsList.size();
 for(index=0; index < listSize; index++) {
    ReviewReport rr = (ReviewReport)reportsList.get(index);
    String propNum = rr.getProposalNumber();
    int reviewerID = rr.getReviewerID();
    String status = rr.getStatus();
    String sciJustFile=rr.getMergedFile();
    String propLink = propNum;
    if (sciJustFile != null && sciJustFile.length() > 0) {
        propLink="<a href=\"/reports/displayFile.jsp?fileName=";
        propLink += sciJustFile + "\" target=\"groupSJ\" >";
        propLink += propNum;
        propLink += "</a>";
    }
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


%>
<tr>
<td align="left"> <%= propLink %> <%=techLink%> <%=pinputLink%></td> 
<td align="center"> <%= status %> </td> 
<td class="nowrap"> <%= rr.getPI() %> </td> 
<td class="nowrap"> <%= rr.getPrimaryReviewerName() %> </td> 
<td class="nowrap"> <%= rr.getSecondaryReviewerName() %> </td> 
<td align="left"> <%= rr.getTitle() %> </td> 

</tr>


<% } //end for loop %>

</table>
</div>
</form>

<hr>
  </body>
</html>


