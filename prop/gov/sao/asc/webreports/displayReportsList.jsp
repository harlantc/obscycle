<%@ page session="true" import="java.util.*, info.User" %>
<%@ page import="info.ReviewReport, info.ReportsConstants" %>

<%@ include file = "reportsHead.html" %>
<body onload="setScrollPosition('rlist_scroll_y=');" onunload="getScrollPosition('rlist_scroll_y=');">
<script type="text/javascript">
  $(document).ready(function () {
    $("#proplink").on("click", function() {
      $(this).attr("disabled", "disabled");
      doWork();
    });
  });

  function doWork() {
    //actually this function will do something and when processing is done the button is enabled by removing the 'disabled' attribute
    //I use setTimeout so you can see the button can only be clicked once, and can't be clicked again while work is being done
    setTimeout('$("#proplink").removeAttr("disabled")', 1500);
  }
</script>

<% 
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {


response.addCookie(new Cookie("JSESSIONID", session.getId()));
response.setHeader("Cache-Control","no-store"); //HTTP 1.1

boolean topMenuPage = false;
User currentUser = (User)session.getAttribute("user");
String currentAO = (String)session.getAttribute("currentAO");
Boolean isAnonymous = (Boolean)session.getAttribute("isAnonymous");
if (isAnonymous == null) isAnonymous=true;
String userName = currentUser.getUserName();
int userID = currentUser.getUserID();
boolean chairMode = currentUser.inChairMode();
Vector reportsList = (Vector) session.getAttribute("reportsList");
boolean beforePR = ((Boolean)session.getAttribute("beforePR")).booleanValue();
String reportType = null;
String titleType = "Primary";
String printType = new String(ReportsConstants.PRIMARY);
String subHeading = "";
String helpPage = new String("/reports/reportsListHelp.jsp");
String appLabel = new String(ReportsConstants.APPLABEL);
String colspan="5";
String hrcolspan="6";
if (isAnonymous) {
  colspan ="4";
  hrcolspan="5";
}
String rowClass="alt1";
String reassignLink="<a href=\"/reports/reassignReport.jsp?operation=" + ReportsConstants.REASSIGNb + "&amp;reassignProp=";
%>


<%@ include file = "header.jsp" %>

<div class="instructspanbar">
<% if(beforePR) { 
   appLabel = "Last Updated";
%>
<p>
This page allows you to access reports for all proposals on which you are an assigned reviewer.
<ul>
    <li>The lists below are separated by your  primary or secondary assignments.</li>
    <li>Click on the proposal number to open the report.</li>
    <li>You can access a read-only page containing all your reports by clicking on "Printable Reports" at the top of the table.</li>
</ul>
<% } else { %>
<p>
This page allows you to access reports for all proposals on which you are an assigned reviewer.
<ul>
    <li>The lists below are separated by  your  primary or secondary assignments.</li>
    <li>Click on the proposal number to open the report, which also gives you
        access to the Google Doc where you will edit your comments.</li>
    <li>Last Updated is the timestamp of the most recent import of text to PAS from
        the report comments Google Doc.
    <ul>
        <li>The PAS has automated imports on a regular schedule and will always
            import the latest version of the google doc when you "Complete" your report.</li>
    </ul>
        </li>
    <li>You can access a read-only page containing all your reports by clicking on
        "Printable Reports" at the top of the table.</li>
    <li>You must "Complete" each of the primary reports listed below by the end of the Review.
        For more information on this step of the process, please refer to the following
        <a href="reportViewHelp.jsp">report completion guide.</a></li>
</ul>
    <% } %>
</div>

<table width="99%" cellspacing="2" > 
<tr>

<%
if(!beforePR) {	
  titleType = "Peer";
  printType = new String(ReportsConstants.PEER);
%>
<td class="xlargebc" colspan="<%=hrcolspan%>" >Peer Review Reports: Primary reviewer</td>
<%
} else {
%>
<td class="xlargebc" colspan="<%=colspan%>" ><%= titleType%> Review Reports</td>
<%
}

  int listSize = 0;
  if (reportsList != null) { 
     listSize= reportsList.size();
  }
  int numPrimary = ReviewReport.getNumPrimary(reportsList);
  int numSecondary = ReviewReport.getNumSecondary(reportsList);
  if(numPrimary > 0 ) {
    String primaryURL = "/reports/printReports?printType=";
    primaryURL += printType;
    primaryURL += "&amp;reviewerID=";
    primaryURL += userID ;
    primaryURL = response.encodeURL(primaryURL);
%>

<td><a href="<%=primaryURL%>" target="_blank">Printable Reports</a></td>
</tr>

<tr class="header"> 
<th class="tblcenter">Proposal</th> 
<th class="tblcenter">Type</th> 
<% if (!beforePR) { %>
<th class="tblcenter"><%=appLabel%></th>
<% } %>
<th class="tblcenter">Last Updated</th>
<% if (!isAnonymous) { %>
<th class="tblcenter">P.I.</th>
<% } %>
<th class="tblcenter">Proposal Title</th>
<th class="tblcenter">Secondary Reviewer</th>
<%--<th class="tblcenter">Request Reassignment</th>--%>
</tr> 

<% } else { %>
<td>&nbsp;</td>
</tr>
<tr>
<td colspan="<%=hrcolspan%>">
No <%=titleType%> reports available for review.
</td>
</tr>

<% } 

  int index = 0;
  for(index=0; index < listSize; index++) {
     ReviewReport rr = (ReviewReport)reportsList.get(index);
     reportType = rr.getType();
     String propNum = rr.getProposalNumber();
     int reviewerID = rr.getReviewerID();



     //session.setAttribute("reviewerID", new Integer(reviewerID));	
     if((beforePR && reportType.equals(ReportsConstants.PRIMARY)) || 
	(!beforePR && reportType.equals(ReportsConstants.PEER))){

        String otherReviewer = rr.getSecondaryReviewerName();
        if (otherReviewer==null || otherReviewer.equals("")) {
          otherReviewer = "&nbsp;";
        }
        
        String status= rr.getStatus();
        String lastUpdate = rr.getLastUpdate();
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
        String theURL = "/reports/getReport.jsp?reviewerID=";
        theURL += reviewerID;
        theURL += "&amp;propNum=";
        theURL += propNum;
        theURL = response.encodeURL(theURL);

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

        if (rowClass.equals("alt1") ) 
           rowClass = "alt2";
        else
           rowClass = "alt1";
   String rlink= reassignLink + propNum +  "&amp;reassignType=Primary\">" + propNum + "</a>";
       
%>

    <tr class="<%=rowClass%>" onmouseover="this.className='hover'" onmouseout="this.className='<%=rowClass%>'" >
<td align="left"><a href="<%=theURL%>" id="proplink" onclick="return clickAndDisable.call(this)"><%= propNum %></a> <%=techLink%> <%=pinputLink%></td>
<td align="center"> <%= rr.getProposalType() %> </td> 
<% if (!beforePR) { %>
<td align="center"> <%= status %> </td> 
<% } %>
<td align="center"> <%= lastUpdate %> </td> 
<% if (!isAnonymous) { %>
<td style="white-space:nowrap;text-align:left;"> <%= rr.getPI() %> </td> 
<% } %>
<td align="left"> <%= rr.getTitle() %> </td> 
<td style="white-space:nowrap;text-align:center;"> <%= otherReviewer %> </td> 
<%--<td align="center"><%= rlink %>  </td>--%>
</tr> 

<%
	} //end else
}//end for loop
%>


<tr>
<td colspan="<%=hrcolspan%>" align="center"><hr></td>
</tr>

<tr>
<%
if(beforePR) {	
  //Display a break line, and the secondary title and header
  printType = new String(ReportsConstants.SECONDARY);
  reportType = new String(ReportsConstants.SECONDARY);
%>
<td class="xlargebc" colspan="<%=colspan%>" >Secondary Review Reports</td>

<% } else { 
  printType = new String(ReportsConstants.SECONDARYPEER);
  reportType = new String(ReportsConstants.SECONDARYPEER);
%>

<td class="xlargebc" colspan="<%=hrcolspan%>" >Peer Review Reports: Secondary reviewer</td>

<% }
    String secondaryURL = "/reports/printReports?printType=";
    secondaryURL += printType;
    secondaryURL += "&amp;reviewerID=";
    secondaryURL += userID ;
    secondaryURL = response.encodeURL(secondaryURL);

    if ( numSecondary > 0) {
 %>

<td><a href="<%=secondaryURL%>" target="_blank">Printable Reports</a></td>
</tr>


<tr class="header"> 
<th>Proposal</th> 
<th class="tblcenter">Type</th>
<% if (!beforePR) { %>
<th align="center"><%= appLabel %></th>
<% } %>
<th class="tblcenter">Last Updated</th>
<% if (!isAnonymous) { %>
<th class="tblcenter">P.I.</th>
<% } %>
<th class="tblcenter">Proposal Title</th>
<th class="tblcenter">Primary Reviewer</th>
<%--<th class="tblcenter"> Request Reassignment </th>--%>
</tr> 

<% } else { %>
<td>&nbsp;</td>
</tr>
<tr>
<td colspan="<%=hrcolspan%>">
No <%=reportType%> reports available for review.
</td>
</tr>
<% } %>

<%
  for(index=0; index < listSize; index++) {
     ReviewReport rr = (ReviewReport)reportsList.get(index);
     String propNum = rr.getProposalNumber();
     reportType = rr.getType();
     int reviewerID = rr.getReviewerID();
      
     if(reportType.equals(ReportsConstants.SECONDARY) ||
	reportType.equals(ReportsConstants.SECONDARYPEER)) {

        String otherReviewer = rr.getPrimaryReviewerName();
        String status= rr.getStatus();
        String lastUpdate = rr.getLastUpdate();
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

        String theURL = "/reports/getReport.jsp?reviewerID=";
        theURL += reviewerID;
        theURL += "&amp;propNum=";
        theURL += propNum;
        theURL = response.encodeURL(theURL);

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
           pinputLink = "<a href=\"" + pinputURL + "\" target=\"pinputRev\" class=\"pi\">PI</a>";
        }
        if (rowClass.equals("alt1"))
          rowClass = "alt2";
        else
          rowClass = "alt1";
    String rlink= reassignLink + propNum + "&amp;reassignType=Secondary\">" + propNum + "</a>";
%>
<tr class="<%=rowClass%>" onmouseover="this.className='hover'" onmouseout="this.className='<%=rowClass%>'" > 
<td align="left"><a href="<%=theURL%>"><%= propNum %></a> <%=techLink%> <%=pinputLink%></td>

<td align="center"> <%= rr.getProposalType() %> </td> 
<% if (!beforePR) { %>
<td align="center"> <%= status %> </td> 
<% } %>
<td align="center"> <%= lastUpdate %> </td> 
<% if (!isAnonymous) { %>
<td style="white-space:nowrap;text-align:left;"> <%= rr.getPI() %> </td> 
<% } %>
<td align="left"> <%= rr.getTitle() %> </td> 
<td style="white-space:nowrap;text-align:center;"> <%= otherReviewer %> </td> 
<%--<td align="center"><%= rlink %>  </td>--%>
</tr> 


<% 
	}// end if
} //end for loop
%>


</table>
    <hr>
<form name="mainMenuForm" action="/reports/login.jsp" >
<%=topMenuBtn%>
</form>

<%@ include file = "footer.html" %>

<% } catch (Exception e) {};
 } %>

  </body>
</html>


