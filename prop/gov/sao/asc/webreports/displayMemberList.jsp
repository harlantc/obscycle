<%@ page session="true" import="java.util.*, info.User,info.ReportsConstants, org.apache.commons.lang3.StringEscapeUtils"  %>

<%@ include file = "reportsHead.html" %>

<body>	
<% 
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {


response.addCookie(new Cookie("JSESSIONID", session.getId()));
response.setHeader("Cache-Control","no-store"); //HTTP 1.1


  Integer userId = new Integer(0);
  int listSize = 0;
  String currentAO = (String)session.getAttribute("currentAO");
  String viewType = (String)session.getAttribute("viewType");
  String userName = "";
  User currentUser = (User)session.getAttribute("user");
  if (currentUser != null) {
    userId  = new Integer(currentUser.getUserID());
    userName = currentUser.getUserName();
  }

  Vector memberList = (Vector) session.getAttribute("memberList");
  if (memberList != null) {
    listSize = memberList.size();
  }
  String panelName = (String)session.getAttribute("panelName");

  boolean  topMenuPage = false;
  String helpPage = new String("/reports/topMenuViewHelp.jsp");
  String subHeading = new String("Reviewers on Panel ");
  subHeading += panelName;


%> 


<%@ include file = "header.jsp" %>

<div class="instructspanbar">
<% if (currentUser.canAdminEdit() && viewType != null && viewType.equals("su") ) {  %>
This page allows CDO to switch their view to that of a selected reviewer.  Once you select the user link you will be logged in as that reviewer.  You must logout and re-enter to re-gain the administrative privileges.
<% } else { %>
This page allows access to preliminary grades for the members of
the selected panel. Click on the panel member name to view the
current preliminary grades for that member.
You may not edit the grades. 
<p>
Click <a href="/reports/viewGradesSummary?ftype=panel&panel=<%=panelName%>" target="_blank">here</a> to view <b>all grades</b> by proposal for this panel.
<% }  %>
</div>
<form name="memberForm" method="POST" action="/reports/viewMembers"  target="_self" >

<p>
<input type="hidden" name="panelName" value="<%=panelName%>"> 
<input type="hidden" name="memberID" value=""> 
<input type="hidden" name="memberType" value=""> 
<input type="hidden" name="operation" value="" > 
<table cellspacing="1" cellpadding="10"> 
<tr class="header"> 
<th align="left">Reviewer Name</th> 
<th align="left">Type</th>
</tr> 
<%
  int index = 0;
  String rowClass="alt1";
  for(index=0; index < listSize; index++) {
     User member = (User)memberList.get(index);
     Integer reviewerID = new Integer(member.getUserID());
     if (rowClass.equals("alt1") ) 
       rowClass = "alt2";
     else
       rowClass = "alt1";
     String mname= member.getUserName() + ", " + member.getUserFirst();

%>
<tr class="<%=rowClass%>" onmouseover="this.className='hover'" onmouseout="this.className='<%=rowClass%>'" > 
<td>
<% if (currentUser.canAdminEdit() && viewType != null && viewType.equals("su") ) { 
     String linkLbl= "Switch to " + mname;
%>
<input type="button" class="linkBtn" name="Submit" value="<%=linkLbl%>" onclick='document.memberForm.operation.value="switchUser"; document.memberForm.memberType.value="<%=member.getMemberType()%>"; document.memberForm.memberID.value="<%=reviewerID%>";if (verifySU()) {document.memberForm.submit();} else { return false; }' >
<% } else { %>
<a href="/reports/assignGrades.jsp?panelName=<%=panelName%>&amp;reviewerID=<%=reviewerID.toString()%>"> <%= mname %></a>
<% } %>
</td>
<td> <%= member.getMemberType() %> </td> 
</tr> 

<% 
  } 
%>


</table>


</form>
<% } catch (Exception e) {};
 } %>

</body>
</html>


