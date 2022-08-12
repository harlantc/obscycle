<%@ page session="true" import="java.util.*, info.* " %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ page import="ascds.LogMessage" %>

<%
try {

response.setHeader("Cache-Control","no-store"); //HTTP 1.1
response.addCookie(new Cookie("JSESSIONID", session.getId()));

String reviewerID = (String)session.getAttribute("reviewerID");
User currentUser = (User)session.getAttribute("user");
String userName = "";
Integer userId = new Integer(0);
String sortKey = (String)session.getAttribute("sortKey");
String currentPanel = (String)session.getAttribute("panelName");
PrelimGradesList prelimGradesList = (PrelimGradesList) session.getAttribute("prelimGradesList");
ArrayList<String[]> contactsList = (ArrayList<String[]>) session.getAttribute("contactsList");
ModifiedInstitutions institutions = (ModifiedInstitutions) session.getAttribute("institutions");
Integer pcontRows = (Integer) session.getAttribute("pcontRows");
Boolean isAnonymous = (Boolean)session.getAttribute("isAnonymous");
String preConfGuidelines = (String)session.getAttribute("preConfGuidelines");
if (isAnonymous == null) isAnonymous=true;



if (currentUser != null) {
    userId  = new Integer(currentUser.getUserID());
    userName = currentUser.getUserName();
}

boolean topMenuPage = false;

%>



<% 

  String subHeading = "Panel " + currentUser.getPanelName();
  String helpPage = "";
  String userType = new String (currentUser.getType());
  String returnStr = "/reports/login.jsp?file=NoFile";


    

%> 
<%@ include file = "reportsHead.html" %>
<body>
<script  type="text/javascript">
$(document).ready(function() {
  $("#preconflictTable").tablesorter( {
        widgets: ["saveSort","zebra"],
        headers: {
        0: { sorter:'checkbox'}
        }
  });

});
</script>


<form name="exitForm" method="POST" action="/reports/reportsLogout" target="_self">
<input type="hidden" name="operation" value = "NONE">
<div id="cxcheaderplain">
<div class="menudiv">
<div class="hdrleft">
<a href="/" target="_top"><img src="cxcheaderlogo.png" alt="CXC Home Page" border="0"></a>
</div>
<div class="hdrcenter">
<img src="blank.gif" alt="" width="1" height="50">
<span class="mainhdr"> Personal Conflicts : <%= userName %> </span>
<br>
<span class="mainhdr3"><%= subHeading %> </span>

</div>
</div>
</div>
<br>
<div style="float:right;padding:10px;" >
<input type="button" value="Logout"  class="linkBtn"
        onClick='exitForm.target="_self";exitForm.action="/reports/reportsLogout";exitForm.operation.value="<%=ReportsConstants.EXIT%>";this.form.submit()'>
</div>
</form>
<div class="instructspanbar">
  Please identify any personal conflicts you have that could affect your
  fair review of a proposal, or be perceived as affecting a fair review.
  <ul>
    <li>There is no minimum or maximum number of names you should enter</li>
    <li>Examples may include, but are not limited to:
    <ul>
      <li>Family members or close friends who tend to write Chandra proposals.</li>
      <li>Close collaborators at other institutions who may submit Chandra proposals.</li>
      <li>People on teams that significantly overlap with your research and regularly
        compete for the same resources.</li>
    </ul>
    </li>
    <li>You do not need to list researchers at your current institution or anyone who wrote a
      proposal that you are listed on. We automatically track those conflicts.</li>
    <li>For more information and examples, please refer to our
      <a href="<%=preConfGuidelines%>" target="_blank">help file.</a></li>
    <li>Click on the Save button to add new rows.</li>
  </ul>
<p>
</div>

<p>
<form name="cform" method="POST" target="_self" action="/reports/preConflict.jsp">
<input type=hidden name="userID" value="<%=userId.toString()%>">
<input type="hidden" name="reviewerID" value="<%=reviewerID%>">
<input type="hidden" name="sortKey" value="<%=sortKey%>">
<input type="hidden" name="operation" value="Save">
<p>

<span class="btnDiv">
<input type="button" value="Save" class="btn" onClick='verifyContacts()'>
</span>
  <br>
Click save to enter a person and add another blank row.
<p>
<table id="personConflictTable" class="tablesorter" border="1" width="90%" >
<%--<table id="personConflictTable" class="tablesorter" >--%>
  <thead>
  <tr class="header">
    <th>First Name</th>
    <th>Last Name</th>
    <th>Institute</th>
  </tr>
  </thead>
  <tbody>
  <%
  if (prelimGradesList != null) {
    String[] contacts;  // first\tlast\tinstitution
    int contactSize = contactsList.size();
    for (int ii=0; ii < contactSize +1; ii++) {
      String firstName = "", lastName = "", currentInst = "";
      if (ii < contactSize){
        contacts = contactsList.get(ii);
        firstName = StringEscapeUtils.escapeHtml4(contacts[ReportsConstants.CONTACTFIRST]);
        lastName = StringEscapeUtils.escapeHtml4(contacts[ReportsConstants.CONTACTLAST]);
        currentInst = StringEscapeUtils.escapeHtml4(contacts[ReportsConstants.CONTACTINSTITUTE]);
        LogMessage.println("Set contact in jsp for " + userId.toString() + " " +
                           "pcont size: " + contactsList.size() + ": " +
                           firstName + " " + lastName + " " + currentInst);
      }
    %>
    <tr>
      <td>
        <span style="white-space:nowrap">
          <input type="text" class="good-field" size=20 maxlength=20
                 placeholder="enter first name" name="<%="firstName_"+ii%>"
                 value="<%=firstName%>"  >
        </span>
      </td>
      <td>
  <%--      <span style="white-space:nowrap">--%>
        <input type="text" class="good-field" maxlength=20
               placeholder="enter last name" name="<%="lastName_"+ii%>"
               value="<%=lastName%>" >
  <%--      </span>--%>
      </td>
      <td>
        <span style="white-space:nowrap">
          <input list="<%="inst_"+ii%>" name="<%="inst_"+ii%>" class="good-field"
                 placeholder="select/enter institution" value="<%=currentInst%>"
                 style="width: 75%;">
          <datalist id="<%="inst_"+ii%>">
          <%
          Vector<Institution> institutionList = institutions.getinstitutionList();
          for (Institution listInst : institutionList) {
            String instName = listInst.getInstitutionName();
          %>
              <option value="<%= instName %>"> <%= instName %>
          <%
          }
          %>
          </datalist>
        </span>
      </td>
  </tr>
  <%
    } //end pcontRows for loop
  } //end null check
  %>
  </tbody>
</table>
<p>

<% if (!isAnonymous){ %>
  <table id="preconflictTable" class="tablesorter" border="1" width="90%">
  <thead>
  <tr class="header">
  <th>Conflict</th>
  <th>Proposal</th>
  <th>P.I.</th>
  <th>P.I. Institute</th>
  <th class="sorter-false">Co-Investigators</th>
  </tr>
  </thead>
  <tbody>
  <%
  if (prelimGradesList != null) {
    for(int index=0; index < prelimGradesList.size(); index++) {

      Proposal prop = prelimGradesList.get(index);
      String checkName= "cprop" + prop.getProposalNumber();
      String gradeConflict = new String(prop.getPrelimGradeConflict());

      String checkbox="";
      if (gradeConflict.startsWith("C") || gradeConflict.startsWith("c"))
      {
        checkbox="checked";
      }
  %>
  <tr>
  <td align="center"><input type="checkbox" class="checkbox" name="<%= checkName %>" <%= checkbox %> onclick="this.form.ichanged.value=1"></td>
  <td><%= prop.getProposalNumber()%> </td>
  <td><%= StringEscapeUtils.escapeHtml4(prop.getPI()) %> </td>
  <td><%= StringEscapeUtils.escapeHtml4(prop.getPIInstitution()) %> </td>
  <td>
  <%
    Vector<User> cois= prop.getCoIList();
    String $coistr="<table width='100%'><tbody><tr><td>";
    for(int cc=0; cc < cois.size(); cc++) {
      User ucoi=cois.get(cc);
      String cname = ucoi.getUserName() + "," + ucoi.getUserFirst();
      $coistr += "<tr style='background-color:initial;'><td width='40%'>" + StringEscapeUtils.escapeHtml4(cname) + "</td><td>";
      $coistr += StringEscapeUtils.escapeHtml4(ucoi.getUserInstitution());
      $coistr += "</td></tr>";
    }
  %>
  <%= $coistr%>
  </tbody>
  </table>
  </td> </tr>
  <%
    } //end for loop
  } //end null check
  %>
  </tbody>
  </table>

<%
  } // end isAnon
%>
  <p>
  <span class="btnDiv">
  <input type="button" value="Save" class="btn" onClick='verifyContacts()'>
  </span>
  <p>
</form>
<%@ include file = "footer.html" %>
</body>
</html>

<% } catch (Exception e) {};  %>

