<%@ page session="true" import="java.util.*, info.ProposalConflictsList, info.ReviewReport" %>
<%@ page import="info.*" %>
<%@ page import="ascds.LogMessage" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1 
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {

response.addCookie(new Cookie("JSESSIONID", session.getId()));
%>

<%@ include file = "reportsHead.html" %>
<body>  
<script  type="text/javascript">
function checkConflictChange()
{
  if (document.conflictForm.ichanged.value == "1") {
    return( confirm("Please be sure to save any changes first.\n Press 'Cancel' to return to the Conflicts form.\nPress 'Ok' to continue to the main menu. Data will NOT be saved."));
  }
  return true;
}
$(document).ready(function() {
  $("#conflictTable").tablesorter( {
	widgets: ["saveSort","zebra"]
  });
});

</script>




<% 
  Integer userId = new Integer(0);
  boolean isEditable=false;
  String  disabledStr = "class=\"confcmt\"";
  String  onchangeStr = "";
  String  verifyFormStr = "";
  boolean topMenuPage = false;
  String currentAO = (String)session.getAttribute("currentAO");
  User currentUser = (User)session.getAttribute("user");
  Boolean isAnonymous = (Boolean)session.getAttribute("isAnonymous");
  if (isAnonymous == null) isAnonymous=true;
  String userName = "";

  if (currentUser != null) {
    userId  = new Integer(currentUser.getUserID());
    userName = currentUser.getUserName();
  }
  String currentPanel = (String)session.getAttribute("panel");
  String returnStr = "/reports/login.jsp?file=NoFile";

  if (currentPanel.equals("BPP")) {
    if ( (currentUser.isAdmin() && currentUser.isAllowedToEdit())) {
       isEditable = true;
    }
  } else {
    if (currentUser.inChairMode() || 
       (currentUser.isAdmin() && currentUser.isAllowedToEdit())) {
       isEditable = true;
    }
  }

  if (!isEditable) {
    disabledStr = "readonly class=\"noeditconf\"";
  } else {
    topMenuBtnCB = "<img src='arrow-left.png' alt=' ' class='backimg' onmouseover=\"this.className='backactv';\" onmouseout=\"this.className='backimg';\" onClick='if (checkConflictChange()) { exitForm.target=\"_self\";exitForm.action=\"" + returnStr+ "\";exitForm.submit();}'> <input type=\"button\" value=\"Main Menu\"  class=\"mainBtn\" onClick='if (checkConflictChange()) { exitForm.target=\"_self\";exitForm.action=\"" + returnStr+ "\";exitForm.submit();}'>";
  }


  ProposalConflictsList conflictList = (ProposalConflictsList)session.getAttribute("conflictList");
  
  String helpPage = new String("/reports/conflictsViewHelp.jsp");
  String subHeading = new String("Reviewer Conflicts for Panel ");
  subHeading += currentPanel;

%> 

<%@ include file = "header.jsp" %>
<form name="conflictForm" method="POST" action="assignConflicts.jsp" >

<div class="instructspanbar">
<% if (isEditable) { %>
Please enter any mitigation factors used to limit the panel member conflicts
for the proposals on this panel.
<% } %>
<% if (currentPanel.equals("BPP")) { %>
An '*' indicates that the proposal was on the same panel as the Chair.
<% } %>
<p>
<% if (isEditable) { %>
Please <b>Save</b> the form often.  If you leave this page, 
any values not saved will be lost. 
<br>
<% } else { %>
The Reviewer Conflicts are for viewing only and are not editable.
<% } %>
</div>

<p>
<input type=hidden name="userID" value="<%=userId.toString()%>">
<input type=hidden name="panelName" value="<%=currentPanel%>">
<input type=hidden name="ichanged" value="0">
<% if (isEditable) {
%>
<table >
<tr>
<td>
<span class="btnDiv">
<input type="submit" name="Submit" class="btn" value="Save" >
</span>
</td>
</tr></table>
<% } %>
<p>
<table id="conflictTable" class="tablesorter" border ="1"> 
<thead>
<tr class="header">
<% if (currentPanel.equals("BPP")) { %>
<th>Panel</th>
<% } %>
<th>Proposal</th>
<% if (!isAnonymous) { %>
  <th>P.I.</th>
  <th class="sorter-false">Panel Member is PI/CoI </th>
  <th class="sorter-false">Institution Conflicts </th>
  <th class="sorter-false">Reported Conflict</th>
<% } else { %>
  <th class="sorter-false">Panel Member Conflict </th>
<% } %>
<th  class="sorter-false" align="left"> Comments/Mitigation </th>
</tr>
</thead>
<tbody>
<%
if (conflictList != null) {
  String replacePI = "<br>";
  String replaceCoI = "<br>";
  if (!isAnonymous) {
    replacePI = "<br>PI:";
    replaceCoI = "<br>CoI:";
  }
  for(int index=0; index < conflictList.size(); index++) {
    ArrayList<String> conflictNames = new ArrayList<String>();
    Proposal prop = conflictList.get(index);
    String conflictName= "prop" + prop.getProposalNumber();
    String rpsFormFile=prop.getMergedFile();
    String propLink;
    if (rpsFormFile != null && rpsFormFile.length() > 0) {
       propLink="<a href=\"/reports/displayFile.jsp?fileName=";
       propLink += rpsFormFile +  "\" target=\"groupRPS\" >";
       propLink += prop.getProposalNumber();
       propLink += "</a>";
    }
    else {
       propLink = prop.getProposalNumber();
    }
    String piConflict = prop.getPICoIConflict();
    if (piConflict.length() < 2) {
      piConflict = "&nbsp";
    }
    piConflict = piConflict.replaceAll("PI:", replacePI);
    piConflict = piConflict.replaceAll("CoI:", replaceCoI);
    piConflict = piConflict.replaceAll("\\*","<span style='color:#dd0000;'>\\*</span>");
    conflictNames.add(piConflict);
    String instConflict = prop.getInstitutionConflict();
    if (instConflict.length() < 2) {
      instConflict = "&nbsp";
    }  else {
      instConflict = instConflict.replaceAll("PI:",replacePI);
      instConflict = instConflict.replaceAll("CoI:",replaceCoI);
//      instConflict = instConflict.replaceFirst("<br>","");
      instConflict = instConflict.replaceAll("\\*","<span style='color:#dd0000;'>\\*</span>");
      conflictNames.add(instConflict);
    }
    String gradesConflict = prop.getGradesConflict();
    if (gradesConflict.length() < 2) {
      gradesConflict = "&nbsp";
    } else {
      gradesConflict = gradesConflict.replaceAll("---","<br>");
      conflictNames.add(gradesConflict);
    }

    String conflicted;
    if (piConflict.equals("&nbsp") && instConflict.equals("&nbsp") && gradesConflict.equals("&nbsp")){
      conflicted = "";
    } else {
      // Only keep unique panelist names
      ArrayList<String> conflictNameUnique = new ArrayList<String>(
              new HashSet<String>(conflictNames));
      ReviewReport rr = new ReviewReport(prop.getProposalNumber());
      conflicted = rr.arrayListToString(conflictNameUnique);
      LogMessage.println("CONFLICT: prop_id " + prop.getProposalNumber() );
      LogMessage.println("CONFLICT:     conflicted " + conflicted );
      LogMessage.println("CONFLICT:     conflictNames " + conflictNames );
      LogMessage.println("CONFLICT:     conflictNameUnique " + conflictNameUnique );
      LogMessage.println("CONFLICT:     piConflict " + piConflict );
      LogMessage.println("CONFLICT:     instConflict " + instConflict );
      LogMessage.println("CONFLICT:     gradesConflict " + gradesConflict );

    }

%>
<tr>
<% if (currentPanel.equals("BPP")) { %>
<td><%= prop.getPanelName() %> </td> 
<% } %>
<td><%= propLink %> </td> 
<% if (!isAnonymous) { %>
  <td class=nowrap><%= prop.getPI() %> </td>
  <td><%= piConflict %> </td>
  <td><%= instConflict %> </td>
  <td><%= gradesConflict %> </td>
<% } else{ %>
  <td><%= conflicted %></td>
<% }  %>
<td><textarea name="<%=conflictName%>" cols="70" rows="2" <%= disabledStr %> onchange="this.form.ichanged.value=1;"> <%= prop.getConflictComment()%> </textarea></td>
</tr>
<% 
  } //end for loop
} //end null check
%>

</tbody>
</table>
<p>
<% if (isEditable) { %>
<table>
<tr>
<td>
<span class="btnDiv">
<input type="submit" name="Submit" class="btn" value="Save" <%=verifyFormStr%> >
</span>
</td>
</tr>
</table>
<% } %>
<p>
</form>
<%=topMenuBtn%>
<%@ include file = "footer.html" %>

</body>
</html>

<% } catch (Exception e) {}; } %>
