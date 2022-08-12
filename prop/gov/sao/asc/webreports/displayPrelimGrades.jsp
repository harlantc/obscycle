<%@ page session="true" import="java.util.*, info.PrelimGradesList" %>
<%@ page import="info.*,org.apache.commons.lang3.StringEscapeUtils" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1 
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {


response.addCookie(new Cookie("JSESSIONID", session.getId()));
String reviewerID = (String)session.getAttribute("reviewerID");
String currentAO = (String)session.getAttribute("currentAO");
Boolean isAnonymous = (Boolean)session.getAttribute("isAnonymous");
if (isAnonymous == null) isAnonymous=true;
boolean topMenuPage = false;
User currentUser = (User)session.getAttribute("user");
String userName = "";
Integer userId = new Integer(0);
if (currentUser != null) {
    userId  = new Integer(currentUser.getUserID());
    userName = currentUser.getUserName();
}

%>

<%@ include file = "reportsHead.html" %>
<body>  
<script  type="text/javascript">


$(document).ready(function() {


  $("#gradeTable").tablesorter( {
        widgets: ["saveSort","zebra"],
	emptyTo: "none",
	headers: {
	0: { sorter:'inputs'} ,
	1: { sorter:'checkbox'} 
        }
  });

});



</script>


<% 
  boolean isEditable=false;
  String  disabledStr = "disabled"; // Checkbox always disabled for DAPR
  String  readonlyStr = "";
  String  returnStr = "";
  String  mainStr = "Main Menu";
  boolean afterDeadline = ((Boolean)session.getAttribute("prelimDeadline")).booleanValue();
  boolean beforePR = ((Boolean)session.getAttribute("beforePR")).booleanValue();
  String  deadlineMsg = "";

  String currentPanel = (String)session.getAttribute("panelName");
  String userType = new String (currentUser.getType());
  if (userType.equals(ReportsConstants.REVIEWER) ||
      userType.equals(ReportsConstants.CHAIR)   ||
      userType.equals(ReportsConstants.DEPUTYCHAIR)   ||
      userType.indexOf(ReportsConstants.PUNDIT) >= 0)  {
    returnStr = "/reports/login.jsp?file=NoFile";
    if (!afterDeadline) {
      isEditable = true;
      topMenuBtnCB = "<img src='arrow-left.png' alt=' ' class='backimg' onmouseover=\"this.className='backactv';\" onmouseout=\"this.className='backimg';\" onclick='if (checkGradeChange()) { exitForm.target=\"_self\";exitForm.action=\"" +  returnStr + "\";exitForm.submit();}'> <input type=\"button\" class=\"mainBtn\" value=\"Main Menu\" onClick='if (checkGradeChange()) { exitForm.target=\"_self\";exitForm.action=\"" +  returnStr + "\";exitForm.submit();}'>";
    }
    else {
      readonlyStr = "readonly style='background-color:inherit;'";
      deadlineMsg = "<span class='err'>Grades are no longer being accepted at this time.</span>";
    }
  }
  else {
     mainStr = "Back to Reviewer List";
     readonlyStr = "readonly";
     returnStr = "/reports/viewMembers?panelName=";
     returnStr +=  currentPanel;
  }
  if (topMenuBtnCB == null) {
     topMenuBtnCB="<img src='arrow-left.png' alt=' ' class='backimg' onmouseover=\"this.className='backactv';\" onmouseout=\"this.className='backimg';\" onclick='exitForm.target=\"_self\";exitForm.action=\"" + returnStr + "\";exitForm.submit()' ><input type=\"button\" value=\"" + mainStr + "\"  class=\"mainBtn\" onClick='exitForm.target=\"_self\";exitForm.action=\"" + returnStr + "\";exitForm.submit()' >";
  }


  PrelimGradesList prelimGradesList = (PrelimGradesList) session.getAttribute("prelimGradesList");

  String dateFile = null;
  String peerDates  = Reports.accessDates(dateFile,ReportsConstants.PRWEEK);
  String prelimDueDate = Reports.accessDates(dateFile,ReportsConstants.PRELIMDATE);
  String fname = prelimGradesList.getFilename();

  String helpPage = new String("/reports/gradesViewHelp.jsp");
  String conflictsHelpPage = new String("/reports/conflictsViewHelp.jsp");
  String subHeading = new String("Preliminary Grades for Panel ");
  subHeading += currentPanel;

 

%> 
<%@ include file = "header.jsp" %>



<div class="instructspanbar">
<% if (isEditable) { %>
Please enter preliminary grades for all proposals for which you do not  have a 
conflict. These grades will be used to triage proposals at the start of the 
review.  Since we recommend that triage should remove the lowest
25% of proposals when ranked by preliminary grade, your grades are important.
Grades entered by the <b><%= prelimDueDate %></b> deadline 
will be used at the <i>Chandra Peer Review</i> which is scheduled 
for <%= peerDates %>.
<p>
Grades should be between 0.00 and 5.00 with 0.00 being the lowest.
Note that a grade of 0 is usually reserved for a proposal that is 
technically infeasible in some way.
If a <a href="<%=conflictsHelpPage%>">conflict of interest</a> exists,
such that you should not evaluate the proposal, please notify the Director's Office.
<p>
<i>Please <b>Save</b></i> the form often.  If you leave this page, 
any values not saved will be lost. You may save as many times as you like 
before  the deadline.
<p>
<% } else { %>
<%= deadlineMsg %>
<br>
The Preliminary Grades are for viewing only and are not editable.
<br>
<% } %>
The 'Proposal' column links to the merged Science Justification/Proposal file.
<br>
The column labels allow you to sort the table based on that field.
</div>

<p>
<form name="gradesForm" method="POST" target="_self" action="/reports/assignGrades.jsp"  >
<input type=hidden name="userID" value="<%=userId.toString()%>">
<input type="hidden" name="operation" value="">
<input type="hidden" name="reviewerID" value="<%=reviewerID%>">
<input type="hidden" name="panelName" value="<%=currentPanel%>">
<input type="hidden" name="ichanged" value="0">
<table width="99%">
<tr>
<% if (isEditable) { %>
<td>
<span class="btnDiv">
<input type="submit" class="btn" name="Submit" value="Save" onclick='this.form.target="_self";return verifyGradeForm(this.form);'>
</span>
</td>
<% } else {%>
<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
<% } %>
<td align="center">
<span class="largeb">Reviewer:</span> <%= prelimGradesList.getReviewerName() %>
</td>
<td align="right">
<input type="submit" name="Submit" value="<%=ReportsConstants.CSVVERSION%>" class="linkBtn" onclick='gradesForm.target="_blank";return verifyGradeForm(this.form);'>
</td>
</tr>
</table>
<p>
<table id="gradeTable" class="tablesorter" border="1">
<thead>
<tr class="header">
<th>Grade</th>
<th>Conflict</th>
<th>Proposal</th>
<% if (!isAnonymous) { %>
<th>P.I.</th>
<% } %>
<th>Title</th>
</tr>
</thead>
<tbody>
<%
if (prelimGradesList != null) {

  for(int index=0; index < prelimGradesList.size(); index++) {
    Proposal prop = prelimGradesList.get(index);

    String gradeName= "prop" + prop.getProposalNumber();
    String checkName= "cprop" + prop.getProposalNumber();
    String gradeConflict = new String(prop.getPrelimGradeConflict());
    String grade;
    String checkbox="";
    // To pass the hidden checkbox field the correct value when checkbox is disabled
    String checkHidden=""; 
    String sciJustFile=prop.getMergedFile();

    String propLink = prop.getProposalNumber();
    if (sciJustFile != null && sciJustFile.length() > 0) {
        propLink="<a href=\"/reports/displayFile.jsp?fileName=";
        propLink += sciJustFile + "\" target=\"propListSJ\" >";
        propLink += prop.getProposalNumber();
        propLink += "</a>";
    }

    if (prop.getPrelimGrade().isNaN() || prop.getPrelimGrade().doubleValue() < 0.0) {
      grade = new String("");
    } 
    else {
      grade = prop.getPrelimGrade().toString();
    }
    if (gradeConflict.startsWith("C") || gradeConflict.startsWith("c"))  
    {
      checkbox="checked";
      checkHidden="on";
    }


%>
<tr>
<td><input type="text" size=4 maxlength=4 name="<%=gradeName%>" <%= readonlyStr %> class="good-field"  value="<%=grade%>" onChange="this.form.ichanged.value=1;return(validateGrade(this));"></td>
<td align="center"><input type="checkbox" name="<%= checkName %>" <%= checkbox %> <%= disabledStr %>  onclick="this.form.ichanged.value=1"></td>
<%-- disabled checkbox doesn't send values when submitted so use hidden field with same name/value--%>
<input type="hidden" name="<%= checkName %>" value ="<%= checkHidden %>" >
<td><%= propLink %> </td> 
<% if (!isAnonymous) { %>
<td><%= StringEscapeUtils.escapeHtml4(prop.getPI()) %> </td> 
<% } %>
<td><%= StringEscapeUtils.escapeHtml4(prop.getTitle()) %> </td> 
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
<input type="submit" name="Submit" class="btn" value="Save" onclick='this.form.target="_self";return verifyGradeForm(this.form);'>
</span>
</td>
</tr>
</table>
<% }  %>
<p>

<%=topMenuBtnCB%>
</form>
<%@ include file = "footer.html" %>
</body>
</html>

<% } catch (Exception e) {};
 } %>

