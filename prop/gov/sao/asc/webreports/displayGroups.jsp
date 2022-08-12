<%@ page session="true" import="java.util.*, info.ProposalGroupsList" %>
<%@ page import="info.*, org.apache.commons.lang3.StringEscapeUtils" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1 
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {


response.addCookie(new Cookie("JSESSIONID", session.getId()));
%>
<%@ include file = "reportsHead.html" %>
<% 

  Integer userId = new Integer(0);
  boolean isEditable=false;
  String  disabledStr = "";
  String  returnStr = "";
  String  onchangeStr = "";
  String  verifyFormStr = "onclick='this.form.target=\"_self\";'";
  boolean topMenuPage = false;
  String currentAO = (String)session.getAttribute("currentAO");
  User currentUser = (User)session.getAttribute("user");
  Boolean isAnonymous = (Boolean)session.getAttribute("isAnonymous");
  if (isAnonymous == null) isAnonymous=true;

  String userName = "";
  String  mainStr = "Main Menu";
  boolean afterDeadline = ((Boolean)session.getAttribute("proposalGroupsDeadline")).booleanValue();
  String  deadlineMsg = "";

  if (currentUser != null) {
    userId  = new Integer(currentUser.getUserID());
    userName = currentUser.getUserName();
  }
  String dateFile = null;
  String currentPanel = (String)session.getAttribute("panel");
  String groupsDueDate = Reports.accessDates(dateFile,ReportsConstants.GROUPSDATE);


  returnStr = "/reports/login.jsp?file=NoFile";
  if (currentUser.isChair() || 
     (currentUser.isAdmin() && currentUser.isAllowedToEdit())) {
    if (!afterDeadline) {
      isEditable = true;
      topMenuBtnCB = "<img src='arrow-left.png' alt=' ' class='backimg' onmouseover=\"this.className='backactv';\" onmouseout=\"this.className='backimg';\" onClick='if (checkGroupChange()) { exitForm.target=\"_self\";exitForm.action=\"" +  returnStr + "\";exitForm.submit();}'> <input type=\"button\" class=\"mainBtn\" value=\"Main Menu\" onClick='if (checkGroupChange()) { exitForm.target=\"_self\";exitForm.action=\"" +  returnStr + "\";exitForm.submit();}'>";


    }
    else {
      disabledStr = "readonly";
      deadlineMsg = "<span class='err'>Groups are no longer being accepted at this time.</span>";
    }

  }
  else {
     disabledStr = "readonly";
  }
  String groupType = (String)session.getAttribute("groupType");
  //onchangeStr = new String("onchange='this.form.ichanged.value=1;'");
  onchangeStr = new String("onchange='this.form.ichanged.value=1;addGroup(this);return true;'");

  ProposalGroupsList groupsList = (ProposalGroupsList)session.getAttribute("groupsList");
  
  String helpPage = new String("/reports/groupsViewHelp.jsp");
  String subHeading = new String("Proposal Groups for Panel ");
  subHeading += currentPanel;
String dbg = "";

  Vector optvec = new Vector();
  
  String optionStr = "";
  for(int index=0; index < groupsList.size(); index++) {
    Proposal prop = groupsList.get(index);
    if (prop.getGroupName() != null && prop.getGroupName().length() > 0) {
      optionStr = "<option value=\"" + StringEscapeUtils.escapeHtml4(prop.getGroupName()) + "\">" + StringEscapeUtils.escapeHtml4(prop.getGroupName());
      boolean didit = false;
      for(int vv=0; vv < optvec.size(); vv++) {
        if (optionStr.compareTo((String)optvec.get(vv)) < 0) {
dbg += "added optionStr<br>";
           optvec.add(vv,optionStr);
           didit = true;
           break;
        }
        else if (optionStr.compareTo((String)optvec.get(vv)) == 0) {
dbg += "found same<br>";
           didit = true;
           break;
        }
      }
      if (!didit) {
           optvec.add(optionStr);
      }
    }
  }
  optionStr = "<option value=\"\">&nbsp;";
  for(int vv=0; vv < optvec.size(); vv++) {
    optionStr += (String)optvec.get(vv);
  }
%> 
<body>  
<script type="text/javascript">
$(document).ready(function() {

  $("#groupTable").tablesorter( {
	widgets: ["saveSort","zebra"],
        textSorter: function (a, b) {
          return a.localeCompare(b);
        },
	emptyTo: "none",
        headers: {
        0: { sorter:'inputs'} 
        }
  });
        
});         
</script>

<%@ include file = "header.jsp" %>

<form name="groupForm" method="POST" action="/reports/assignGroups.jsp"  target="_self" >
<div class="instructspanbar">
<% if (isEditable) {
%>
Please enter group assignments for the proposals in your panel by <b> <%= groupsDueDate %> </b>.
The group pulldown menu is initialized with existing group names assigned to 
the proposals in your panel. When you type in a new group, it is added to the pulldown menu. 
After the 'Save', the page is then reloaded and the group menu is re-initialized
in sorted order.
<p>
Please <b>Save</b> the form often.  If you leave this page, 
any values not saved will be lost.
<br><br>
<% } else { %>
<%= deadlineMsg %>
<br>You have entered this site in read-only mode. 
The Proposal Groupings are for viewing only and are not editable.
<br>
<% } %>
The 'Proposal' column links to the merged Science Justification/Proposal file.
<br>
The column labels allow you to sort the table based on that field.
</div>

<p>
<input type=hidden name="userID" value="<%=userId.toString()%>">
<input type=hidden name="panelName" value="<%=currentPanel%>">
<input type="hidden" name="ichanged" value="0">
<input type=hidden name="operation" value="">
<table width = "99%">
<tr>
<% if (isEditable) {
%>
<td>
<span class="btnDiv">
<input type="submit" name="Submit" class="btn" value="Save" <%=verifyFormStr%> >
</span>
</td>
<% } %>
<td align="right">
<td align="right">
<input type="submit" name="Submit" class="linkBtn" value="<%=ReportsConstants.CSVVERSION%>" onclick='groupForm.target="_blank";'>
</td>
</tr></table>
<p>
<table id="groupTable" class="tablesorter" border ="1"> 
<thead>
<tr class="header">
<th>Group</th>
<th>Proposal</th>
<% if (!isAnonymous) { %>
<th>P.I.</th>
<% } %>
<th>Title</th>
</tr>
</thead>
<tbody>
<%
if (groupsList != null) {
  

  for(int index=0; index < groupsList.size(); index++) {
    Proposal prop = groupsList.get(index);

    String groupName= "prop" + prop.getProposalNumber();
    String groupSel= "grp" + prop.getProposalNumber();
    String group = prop.getGroupName();
    if (!isEditable && group.length() <= 0) {
       group="&nbsp;";
    } else {
       group = StringEscapeUtils.escapeHtml4(group);
    }

    String sciJustFile=prop.getMergedFile();
    String propLink = prop.getProposalNumber();
    if (sciJustFile != null && sciJustFile.length() > 0) {
        propLink="<a href=\"/reports/displayFile.jsp?fileName=";
        propLink += sciJustFile + "\" target=\"propListSJ\" >";
        propLink += prop.getProposalNumber();
        propLink += "</a>";
    }

%>
<tr>
<td>
<% if (isEditable) { %>
<span style="white-space:nowrap"><input type="text" size=12 maxlength=20 name="<%=groupName%>" <%= disabledStr %> class="good-field"  value="<%=group%>" <%= onchangeStr %> ><select name="<%=groupSel%>" onchange="setGrpText(this);"><%=optionStr%></select>
</span>
<% } else { %>
<input style='background-color:inherit;' type="text" size=12 maxlength=20 name="<%=groupName%>" <%= disabledStr %> class="good-field"  value="<%=group%>">
<% } %>
</td>
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
<input type="submit" name="Submit" class="btn" value="Save" <%=verifyFormStr%> >
</span>
</td>
</tr>
</table>
<% } %>
<p>
<%=topMenuBtn%>


</form>

<%@ include file = "footer.html" %>
</body>
</html>
<% } catch (Exception e) {};
 } %>


