<%@ page session="true" import="java.util.*, info.User, info.ReportsConstants, org.apache.commons.lang3.StringEscapeUtils" %>
<%@ include file = "reportsHead.html" %>
<body>



<% 
response.addCookie(new Cookie("JSESSIONID", session.getId()));
response.setHeader("Cache-Control","no-store"); //HTTP 1.1

String cdoText = (String)session.getAttribute("cdoText");
boolean topMenuPage = true;
String helpPage = null;
String subHeading = new String("Administrator View : Main Menu"); 
User currentUser = (User)session.getAttribute("user");
String currentAO = (String)session.getAttribute("currentAO");
String userType = currentUser.getType();
String userName = currentUser.getUserName();
if(userName == null) {
  userName = "Admin"; //default
}

int userID = currentUser.getUserID();
//Set the userID session attribute so we can logout
session.setAttribute("userID", String.valueOf(userID));
Boolean beforePRBool = (Boolean)session.getAttribute("beforePR");
boolean beforePR = false;
if(beforePRBool != null) {
  beforePR = beforePRBool.booleanValue();
}
Boolean accessPreReports = (Boolean)session.getAttribute("accessPreReports");
boolean preConflicts = false;
if(accessPreReports != null) {
  preConflicts = accessPreReports.booleanValue();
}

Vector<String> listFile = null;

Boolean validUserBool = new Boolean(false);
validUserBool = (Boolean)session.getAttribute("validUser");
boolean validUser = false;
String theURL = "";
Vector panelsList = (Vector) session.getAttribute("panelsList");
int numPanels = 0;
if (panelsList != null) {
  numPanels = panelsList.size();
}

if(validUserBool != null && validUserBool.booleanValue()) {
	validUser = true;
}
//String pgradesFile = (String)session.getAttribute("prelimGrades");
//System.err.println("displayAdminPage: valid user = " + validUser);
//String gradesStatusFile = (String)session.getAttribute("gradesStatusFile");

  listFile = (Vector<String>)session.getAttribute("listFile");
  if (listFile == null) {
    listFile = new Vector<String>();
  }

%>

<%@ include file = "header.jsp" %>
<br>
<%=cdoText%>

<%
if(currentUser.isDeveloper() || currentUser.isAdmin()) {
     if(!beforePR) {
       if (currentUser.isAllowedToEdit()) {
         theURL = "/reports/viewLP?access=edit&amp;bppAccess=all";
       } else {
         theURL = "/reports/viewLP?bppAccess=all";
       }
       theURL = response.encodeURL(theURL);
%>

<!--
<table cellspacing="5" cellpadding="5">
<tr><td>
<a href="<%=theURL%>"> View Large Projects </a>
</td> </tr>
</table>
-->

<% } //close if(!beforePR)  %>
<table width = "99%" border="0" cellspacing="5" cellpadding="5">
<tr class="header">
<td style="background-color:#eeeeee;">&nbsp;</td>
<td colspan="<%=numPanels%>"><b>Panels</b></td>
</tr>
<tr class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'" > 
<td class="adminhdrl">Review Reports : </td>

<%

// This vector contains the panelID and then the panel name, we just
// need the panel name so we always skip the first entry for each panel. 
String currentPanelName = null;
String panelURL = null;
String memberURL = null;
String groupURL = null;
String propURL = null;
String techURL = null;
String conflictURL = null;
String targetConfURL = null;
String tmpURL;

int index=0;
while(index < numPanels) {
   currentPanelName = (String)panelsList.get(++index);
   index++;
   panelURL = new String("/reports/viewPanel?panelName=");
   panelURL += currentPanelName;	
   panelURL = response.encodeURL(panelURL);
// Previously hid LP report if !beforePR, showing it now for pundit prelim complete
%>
<td class="large"><a href="<%=panelURL%>"><%=currentPanelName%></a></td>
<% }
  if (beforePR) { %>
<td>&nbsp;</td>
<% } else { %>
<td class="large"><a href="<%=theURL%>">BPP</a></td>
<% } %>
</tr>
<tr class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'" > 
<td class="adminhdrl">Reviewer Conflicts : </td>
<%
index=0;
while(index < numPanels) {
   currentPanelName = (String)panelsList.get(++index);
   index++;
   conflictURL = new String("/reports/assignConflicts.jsp?panelName=");
   conflictURL += currentPanelName;	
   conflictURL = response.encodeURL(conflictURL);
   if (currentPanelName.equals("LP")) {
%> 
   <td>&nbsp;</td>
<% } else { %>
   <td class="large"><a href="<%=conflictURL%>"><%=currentPanelName%></a></td>
<% } } 
   currentPanelName = "BPP";
   conflictURL = new String("/reports/assignConflicts.jsp?panelName=");
   conflictURL += currentPanelName;	
   conflictURL = response.encodeURL(conflictURL);
%>
<td class="large"><a href="<%=conflictURL%>"><%=currentPanelName%></a></td>
</tr>
<tr class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'" > 
<td class="adminhdrl">Proposal Groups : </td>
<%
index=0;
while(index < numPanels) {
   currentPanelName = (String)panelsList.get(++index);
   index++;
   groupURL = new String("/reports/assignGroups.jsp?panelName=");
   groupURL += currentPanelName;	
   groupURL = response.encodeURL(groupURL);
   if (currentPanelName.equals("LP")) {
%>
     <td>&nbsp;</td>
<%
   } else {
%> 
   <td class="large"><a href="<%=groupURL%>"><%=currentPanelName%></a></td>
<% } } %>
<td>&nbsp;</td>
</tr>
<tr class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'" > 
<% if (preConflicts) { %>
<td class="adminhdrl">Pre-Conflicts : </td>
<% } else { %>
<td class="adminhdrl">Preliminary Grades : </td>
<% }  %>

<%
index=0;
while(index < numPanels) {
   currentPanelName = (String)panelsList.get(++index);
   index++;
   memberURL = new String("/reports/viewMembers?panelName=");
   memberURL += currentPanelName;	
   memberURL = response.encodeURL(memberURL);
   if (currentPanelName.equals("LP")) {
%>
     <td>&nbsp;</td>
<%
   } else {
%>
   <td class="large"><a href="<%=memberURL%>"><%=currentPanelName%></a></td>
<% } } %>
<td>&nbsp;</td>
</tr>
<tr class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'" > 
<td class="adminhdrl">Reviewer Assignments : </td>
<%
index=0;
while(index < numPanels) {
   currentPanelName = (String)panelsList.get(++index);
   index++;
   tmpURL = new String("/reports/proposalList.jsp?type=pri_sec&panelName=");
   tmpURL += currentPanelName;	
   propURL = StringEscapeUtils.escapeHtml4(tmpURL);
   propURL = response.encodeURL(propURL);
%> 
   <td class="large"><a href="<%=propURL%>"><%=currentPanelName%></a></td>
<% }  %>
<td>&nbsp;</td>
</tr>
<tr class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'" > 
<td class="adminhdrl">Technical and Proposer Input : </td>
<%
index=0;
while(index < numPanels) {
   currentPanelName = (String)panelsList.get(++index);
   index++;
   techURL = new String("/reports/techList.jsp?panelName=");
   techURL += currentPanelName;	
   techURL = response.encodeURL(techURL);
%> 
   <td class="large"><a href="<%=techURL%>" target="_blank"><%=currentPanelName%></a></td>
<% }  
   techURL = new String("/reports/techList.jsp?panelName=BPP");
   techURL = response.encodeURL(techURL);
%> 
   <td class="large"><a href="<%=techURL%>" target="_blank" >BPP</a></td>
</tr>
<%     if(!beforePR) { %>
<tr class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'" > 
<td class="adminhdrl">Single Target Conflicts: </td>
<%
  index=0;
  while(index < numPanels) {
    currentPanelName = (String)panelsList.get(++index);
    index++;
    tmpURL = new String("/reports/list.jsp?listType=1&panelName=");
    tmpURL += currentPanelName;	
    targetConfURL = StringEscapeUtils.escapeHtml4(tmpURL);
    targetConfURL = response.encodeURL(targetConfURL);
   if (currentPanelName.equals("LP")) {
%>
     <td>&nbsp;</td>
<%
   } else {
%>
   <td class="large"> <a href="<%=targetConfURL%>"><%=currentPanelName%></a></td>
<% } } %>
<%
   targetConfURL = new String("/reports/list.jsp?listType=3");
   targetConfURL = response.encodeURL(targetConfURL);
%>
   <td class="large"><a href="<%=targetConfURL%>">BPP</a></td>
</tr>
<tr class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'" > 
<td class="adminhdrl">Cross Panel Target Conflicts: </td>
<%
  index=0;
  while(index < numPanels) {
    currentPanelName = (String)panelsList.get(++index);
    index++;
    tmpURL = new String("/reports/list.jsp?listType=2&panelName=");
    tmpURL += currentPanelName;	
    targetConfURL = StringEscapeUtils.escapeHtml4(tmpURL);
    targetConfURL = response.encodeURL(targetConfURL);
   if (currentPanelName.equals("LP")) {
%>
     <td>&nbsp;</td>
<%
   } else {
%>
   <td class="large"><a href="<%=targetConfURL%>"><%=currentPanelName%></a></td>
<% } } %>
<%
   targetConfURL = new String("/reports/list.jsp?listType=4");
   targetConfURL = response.encodeURL(targetConfURL);
%>
   <td class="large"><a href="<%=targetConfURL%>">BPP</a></td>
</tr>
<% } %>

<% if (currentUser.canAdminEdit()) { %>
<tr class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'" > 
<td class="adminhdrl">Switch To Reviewer : </td>

<%
index=0;
while(index < numPanels) {
   currentPanelName = (String)panelsList.get(++index);
   index++;
   memberURL = new String("/reports/viewMembers?viewType=su&amp;panelName=");
   memberURL += currentPanelName;	
   memberURL = response.encodeURL(memberURL);
%>
   <td class="large"><a href="<%=memberURL%>"><%=currentPanelName%></a></td>
<% }  %>
<td>&nbsp;</td>
</tr>
<% } %>
</table>
<p>
<table cellspacing="5" cellpadding="5" bgcolor="#ffffff" border="1">
<%
      theURL = "/reports/viewGradesSummary?ftype=summary";
      theURL = response.encodeURL(theURL);
%>
<% if (preConflicts) { 
      theURL = "/reports/viewGradesSummary?ftype=conflict";
      theURL = response.encodeURL(theURL);
%>
<tr><td class="main" width="20%">Pre-Conflicts</td>
<td>
<ul>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.cla
ssName='normal'"><a href="<%=theURL%>" target="_blank">View Pre-Conflict Status</a>
 

</ul>
</td></tr>
<% } else { %>
<tr><td class="main" width="20%">Grades</td>
<td>
<ul>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.cla
ssName='normal'"><a href="<%=theURL%>" target="_blank">View Preliminary Grades given by primary and secondary reviewers for all proposals</a>
<%  
 
      theURL = "/reports/viewGradesSummary?ftype=status";
      theURL = response.encodeURL(theURL);

%> 
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'">
<a href="<%=theURL%>" target="_blank">View Grade Submission Status </a>
</ul>
</td></tr>
<% if ( listFile.size() > 0) {  %>
<tr><td class="main">Lists</td>
<td>
<ul>
<% for (int ll=0;ll<listFile.size(); ll++) {
%>
<li class="normal" onmouseover="this.className='highlight'" onmouseout="this.className='normal'"><%=listFile.get(ll)%>
<% } %>
</ul> </td></tr>
<% } %>
<% } %>
</table>

<% 
} //close if(user == Developer or user == admin)
%>

<p>
<hr>

  </body>
</html>


