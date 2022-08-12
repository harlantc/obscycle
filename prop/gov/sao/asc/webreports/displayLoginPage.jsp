<%@ page session="true" import="java.util.*, info.User,info.Reports,info.ReportsConstants" %>

<%
//Add the session id to the response cookie, in order to maintain the user login
//since sessions aren't maintained when Apache forwards requests to Tomcat

try {


response.addCookie(new Cookie("JSESSIONID", session.getId()));
response.setHeader("Cache-Control","no-store"); //HTTP 1.1


int validUser = ReportsConstants.INVALIDENTRY;
int timeoutValueMinutes = -1; 
int timeoutValueMS = timeoutValueMinutes * 60 * 1000;
String currentAO = "99";
if(session != null) {
   int reportsID = ((Integer)session.getAttribute("reportsID")).intValue(); 
   validUser = User.isValidUser(reportsID);

}



//Initalize variables for later use
User currentUser = null;
int userID = -1;
String userName = null;
String subHeading = new String("Main Menu");
String editReportsURL = null;
String gradeReportsURL = null;
String printPrimaryURL = null;
String printSecondaryURL = null;
String groupsReportsURL = null;
String proposalListURL = null;
String pancatListURL = null;
String bppListURL = null;
String techListURL = null;
String bpptechListURL = null;
String conflictsReportsURL = null;
String bppconflictsReportsURL = null;
boolean editLPLink = false;	
boolean topMenuPage = true;
String helpPage = null; //Let the header.jsp file figure this out
Boolean beforePRBool = null;
boolean beforePR = true;
String panelTar = null;
String bppTar = null;
String dateFile = null;
boolean hasPanel = true; 
boolean isPunditPanel = false; 
Vector<String> listFile = null;

String cdoText = (String)session.getAttribute("cdoText");
if (cdoText == null) cdoText="";


if(validUser == ReportsConstants.VALIDENTRY) {
        currentAO = (String)session.getAttribute("currentAO");
	currentUser = (User)session.getAttribute("user");
	userID = currentUser.getUserID();
	userName = currentUser.getUserName();
        beforePRBool = (Boolean)session.getAttribute("beforePR");
        beforePR = beforePRBool.booleanValue();
        panelTar = (String)session.getAttribute("panelTar");
        bppTar = (String)session.getAttribute("bppTar");
        listFile = (Vector<String>)session.getAttribute("listFile");

        if (currentUser.getPanelName() == null ||
            currentUser.getPanelName().length() < 2) {
          hasPanel = false;
        }
        if (currentUser.getPanelName().equals("BPP") ) {
             hasPanel = false;
        }
        if (currentUser.getPanelName().equals("LP") ) {
             isPunditPanel = true;
        }
        if (listFile == null) {
           listFile = new Vector<String>();
        }

        

  
	//Set the userID session attribute so we can logout
        session.setAttribute("userID", String.valueOf(userID));

	editReportsURL = new String("/reports/reviewReports.jsp");
	editReportsURL = response.encodeURL(editReportsURL);

	gradeReportsURL = new String("/reports/assignGrades.jsp");
	gradeReportsURL = response.encodeURL(gradeReportsURL);

        groupsReportsURL = new String("/reports/assignGroups.jsp");
	groupsReportsURL = response.encodeURL(groupsReportsURL);

        proposalListURL = new String("/reports/proposalList.jsp?type=pri_sec");
	proposalListURL = response.encodeURL(proposalListURL);
        pancatListURL = new String("/reports/proposalList.jsp?type=pancat");
	pancatListURL = response.encodeURL(pancatListURL);

        bppListURL = new String("/reports/proposalList.jsp?type=bpplist");
	bppListURL = response.encodeURL(bppListURL);

        techListURL = new String("/reports/techList.jsp");
	techListURL = response.encodeURL(techListURL);
        bpptechListURL = new String("/reports/techList.jsp?type=bpp");
	bpptechListURL = response.encodeURL(bpptechListURL);

	conflictsReportsURL = new String("/reports/assignConflicts.jsp");
	conflictsReportsURL = response.encodeURL(conflictsReportsURL);
	bppconflictsReportsURL = new String("/reports/assignConflicts.jsp?panelName=BPP");
	bppconflictsReportsURL = response.encodeURL(bppconflictsReportsURL);

        printPrimaryURL = new String("/reports/reviewReports.jsp");
        printPrimaryURL += "?operation=Print&amp;printType=Primary";
	printPrimaryURL = response.encodeURL(printPrimaryURL);

        printSecondaryURL = new String("/reports/reviewReports.jsp");
        printSecondaryURL += "?operation=Print&amp;printType=Secondary";
	printSecondaryURL = response.encodeURL(printSecondaryURL);

 	Boolean editLPLinkBool = (Boolean)session.getAttribute("editLPLink");
        if(editLPLinkBool != null && editLPLinkBool.booleanValue()) {
              editLPLink = true;
        }
}
%> 



<%@ include file = "reportsHead.html" %>
<body onLoad="resetScrollCookies();">

<script type="text/javascript">

// clear the reports textarea preferences
try {
  localStorage.removeItem("cmtEdit1");
  localStorage.removeItem("pNote");
  localStorage.removeItem("comments1");
  localStorage.removeItem("specificRecs");
  localStorage.removeItem("whyGradeNotHigher");
  localStorage.removeItem("tablesorter-savesort");
} catch(error) {
}
</script>


<div style="height:98%;">
<div class="topDiv">

<% if(validUser != ReportsConstants.VALIDENTRY) {
   //int blah = ((Integer)session.getAttribute("reportsID")).intValue(); 
   //System.err.println("Invalid user: id = " + blah);

%>

Error: Invalid entry. <br>Either the session has timed out, or you've incorrectly accessed
this page. Please return to the CDO Reviewer's Login site.

<% } else  { %>

<%@ include file = "header.jsp" %>

<%= cdoText%>
<table border="1" cellpadding="10" width="99%" bgcolor="#ffffff">
<% // PEER REPORTS Box %>
<% if (hasPanel) { %>
<tr>
<td class="main">Panel <%=currentUser.getPanelName()%><br>Review Reports</td>
<td><ul>

<% if(beforePR) { 
  String reviewDueDate = Reports.accessDates(dateFile,ReportsConstants.PRDATE) + " EDT";
%>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'"><a href="<%=editReportsURL%>">Edit</a> your Primary and Secondary Review reports. <span class="noticeme">All Primary and Secondary reports must be entered before <%=reviewDueDate%>.</span>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View <a href="<%=techListURL%>" target="_blank">Technical/Proposer Input </a> reports. 
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View <a href="<%=proposalListURL%>">primary/secondary reviewers</a> for all proposals in your panel.
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View the <a href="/reports/proposalList.jsp?type=panmem">Panel Members </a>
<% } else {  
  if (!isPunditPanel) {
%>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'"><a href="<%=editReportsURL%>">Edit</a> your Peer Review reports.
<%  if(currentUser.inChairMode() ) { %>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'"><a href="/reports/viewPanel">Approve</a> Peer Review reports for all proposals in your panel.
<p>
<%   }  %>
<% } %>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View <a href="<%=techListURL%>" target="_blank">Technical/Proposer Input </a> reports. 
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View <a href="<%=proposalListURL%>">primary/secondary reviewers</a> for all proposals in your panel.
<% if (currentUser.isChair() && !isPunditPanel) { %>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View the <a href="<%=pancatListURL%>">Proposals in  panels with the same science category</a> as your panel
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View the <a href="/reports/list.jsp?listType=6">Time Critical Summary</a> for proposals in your panel
<%   }  %>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View the <a href="/reports/proposalList.jsp?type=panmem">Panel Members </a>
<p>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">Printable <a href="<%=printPrimaryURL%>" target="_blank">Primary Review</a> reports.
<% if (!isPunditPanel) { %>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">Printable <a href="<%=printSecondaryURL%>" target="_blank">Secondary Review</a> reports. 
<%
     }
   }
%>
</ul></td></tr>

<% } %>
<%
// LP/VLP Box
if ((currentUser.isPundit() || currentUser.isChair()) ) {
%>
<tr>
<td class="main"><%=ReportsConstants.BPP%><br>(BPP)</td>
<td><ul>
<%
   if ((currentUser.isPundit() || currentUser.isChair()) && currentUser.isAllowedLPAccess()) {
%>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'"><a href="/reports/viewLP?access=edit">Edit</a> reports for all <%=ReportsConstants.BPPLBL%> at the review.
<p>
<% }
   if (!beforePR) { 
%>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View the <a href="/reports/list.jsp?listType=7">Time Critical Summary</a> for the <%=ReportsConstants.BPP%>
<% } %>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View <a href="<%=bpptechListURL%>" target="_blank">Technical/Proposer Input </a> reports for all <%=ReportsConstants.BPPLBL%>.
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View the <a href="<%=bppListURL%>"><%=ReportsConstants.BPPLBL%> List</a>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View the <a href="/reports/proposalList.jsp?type=panmemb">Panel Members </a>
</ul></td></tr>
<% }  

  if (!beforePR) {
%>
<tr><td class="main">Conflicts</td>
<td><ul>
<%--    Removed for Dual Anon--%>
<%--<% if (currentUser.isPundit()  || currentUser.isChair() ) { %>--%>
<%--<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">Identify <a href="/reports/bppConflict.jsp">Personal Conflicts </a> for the <%=ReportsConstants.BPP%>--%>
<%--<p>--%>
<%--<%--%>
<%--   }--%>
<%  if (currentUser.inChairMode() && !isPunditPanel) {
%>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'"><a href="<%=conflictsReportsURL%>">Edit</a> the Reviewer Conflicts Report for Panel <%=currentUser.getPanelName()%>.
<% } else if (hasPanel && !isPunditPanel) { %>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View the <a href="<%=conflictsReportsURL%>">Reviewer Conflicts Report for Panel <%=currentUser.getPanelName()%></a>.
<% } %>
<% if (currentUser.isPundit()  || currentUser.isChair() ) { %>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View the <a href="<%=bppconflictsReportsURL%>">Reviewer Conflicts for the <%=ReportsConstants.BPP%></a>.
<p>
<% }  

   if (currentUser.isChair() && hasPanel && !isPunditPanel) { 
%>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View the <a href="/reports/list.jsp?listType=1">target conflicts within Panel <%=currentUser.getPanelName()%></a>.
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View the <a href="/reports/list.jsp?listType=2">cross target conflicts for  Panel <%=currentUser.getPanelName()%></a>.
<p>
<% } 
   if (currentUser.isPundit()) { 
%>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View the <a href="/reports/list.jsp?listType=3">the target conflicts for the <%=ReportsConstants.BPP%></a>. 
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">View the <a href="/reports/list.jsp?listType=4">cross target conflicts for the <%=ReportsConstants.BPP%></a>. 
<% } %>
</ul></td></tr>
<% } %>

<%
// GROUPS
   if(hasPanel && !isPunditPanel) { 
%>
<tr><td class="main">Grades &amp; Groups</td>
<td><ul>
<% if(beforePR ) { 
  String prelimDueDate = Reports.accessDates(dateFile,ReportsConstants.PRELIMDATE);
  String groupDueDate = Reports.accessDates(dateFile,ReportsConstants.GROUPSDATE) ;
%>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'"><a href="<%=gradeReportsURL%>">Assign</a> preliminary grades to all proposals in your panel. <span class="noticeme">Preliminary Grades are due <%= prelimDueDate %></span>
<p>
<%   if (currentUser.isChair()) { 
%>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'"><a href="<%=groupsReportsURL%>">Create/Modify</a> Proposal Group Assignments.  <span class="noticeme">All groups must be entered by <%=groupDueDate%>.</span>
<% } else { %>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'"><a href="<%=groupsReportsURL%>">View</a> Proposal Group Assignments for Panel <%=currentUser.getPanelName()%>
<%   } %>
<p>
<% } else  { %>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'"><a href="<%=gradeReportsURL%>">View</a> your preliminary grades for all proposals in Panel <%=currentUser.getPanelName()%>.
<p>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'"><a href="<%=groupsReportsURL%>">View</a> Proposal Group Assignments for Panel <%=currentUser.getPanelName()%>
<%   } %>
</ul>
</tr>
<% } %>
<% if ( listFile.size() > 0) {  %>
<tr><td class="main">Lists</td>
<td><ul>
<% for (int ll=0;ll<listFile.size(); ll++) {
%>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'"><%=listFile.get(ll)%>
<% } %>
</ul> </td></tr>
<% } %>



<%
// TAR FILES 
if (bppTar != null || panelTar != null) {
%>
<tr><td class="main">Downloads</td>
<td><ul>
<%
 if (panelTar != null) {
%>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'"><a href="/reports/displayFile.jsp?fileName=panelTar">Panel</a> tar file containing proposals, proposal lists and various information on your panel and the review.
<% }
 if (bppTar != null) {
%>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'"><a href="/reports/displayFile.jsp?fileName=bppTar"><%=ReportsConstants.BPPLBL%></a> tar file containing proposals, proposal lists and various information for the Big Project panel.

<% } %>
</ul> </td></tr>
<% } %>




</table>



<%
} //matches else of if(validUser != ReportsConstants.VALIDENTRY) 
%>

</div>
<p>
<%@ include file = "footer.html" %>

</div>
  </body>
</html>


<% } catch (Exception e) {}; %>

