<%@ page session="true" import="java.util.*" %>
<%@ page import="info.*, org.apache.commons.lang3.StringEscapeUtils" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1 
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {


response.addCookie(new Cookie("JSESSIONID", session.getId()));
%>

<%@ include file = "reportsHead.html" %>
<body>  

<% 
  Integer userId = new Integer(0);
  String  disabledStr = "";
  String  returnStr = "";
  String  onchangeStr = "";
  boolean topMenuPage = false;
  String currentAO = (String)session.getAttribute("currentAO");
  User currentUser = (User)session.getAttribute("user");
  boolean sortList = false;
  if (session.getAttribute("sortList") != null)   {
    sortList = (Boolean)session.getAttribute("sortList");
  }
  String  listType= (String)session.getAttribute("listType");
  String  csvFile= (String)session.getAttribute("csvFile");
  String  csvMsg= (String)session.getAttribute("csvMsg");
  String userName = "";
  String  mainStr = "Main Menu";
  String smsg="&nbsp;";
  if (!sortList) {
     smsg = "Current list is not sortable.";
     disabledStr="class='sorter-false'";
  } else {
     smsg = "Click on column header to sort.";
  }


  if (currentUser != null) {
    userId  = new Integer(currentUser.getUserID());
    userName = currentUser.getUserName();
  }
  String dateFile = null;
  String currentPanel = (String)session.getAttribute("panel");

  returnStr = "/reports/login.jsp?file=NoFile";


  Vector<String> csvHdrs = new Vector<String>();
  if (session.getAttribute("csvHdrs") != null && session.getAttribute("csvHdrs") != "") {
    csvHdrs  = (Vector<String>)session.getAttribute("csvHdrs");
  }
  Vector csvData =  new Vector();
  if (session.getAttribute("csvData") != null && session.getAttribute("csvData")!= "") {
    csvData  = (Vector)session.getAttribute("csvData");
  }

  String csvTitle  = (String)session.getAttribute("csvTitle");
  session.setAttribute("csvData","" );
  session.setAttribute("csvHdrs","" );
  session.setAttribute("csvTitle","" );
  session.setAttribute("csvMsg","" );
  
  String subHeading = csvTitle;
  String helpPage = null;


%> 

<%@ include file = "header.jsp" %>

<script type="text/javascript">
$(document).ready(function() {
  $("#csvTable").tablesorter( {
	widgets: ["zebra"],
	emptyTo: "none"
	});
}); 
</script>



<form name="csvForm" method="POST" action="/reports/list.jsp"  target="_self" >

<%=csvMsg %>
<p>
<input type=hidden name="userID" value="<%=userId.toString()%>">
<input type=hidden name="panelName" value="<%=currentPanel%>">
<input type=hidden name="listType" value="<%=listType%>">
<div style="clear:both;width:99%;">
<div style="float:left;margin-left:5px;">
<%=smsg%>
</div>
<div style="float:right;">
<a class="pas" href="/reports/displayCSV.jsp?fileName=<%=csvFile%>" target="_blank"><%=ReportsConstants.CSVVERSION%></a>
</div>
</div>
<div style="clear:both;width:99%;">
<table id="csvTable" class="tablesorter" border="1">
<thead>
<tr>
<%
  int hdrSize = csvHdrs.size();
  for (int ii=0; ii< csvHdrs.size(); ii++) {
%>
<th <%=disabledStr%> > <%= StringEscapeUtils.escapeHtml4(csvHdrs.get(ii)) %> </th>
<% } %>
</tr>
</thead>
<tbody>
<%
if (csvData != null) {

  for(int ii=0; ii < csvData.size(); ii++) {
%>
<tr>
<%
    Vector<String> csvRow=  (Vector<String>)csvData.get(ii);
    int jj=0;
    for (; jj < csvRow.size(); jj++) {
      String tstr = csvRow.get(jj);
      if(tstr == null)  tstr = "&nbsp"; 
      else {
        tstr = tstr.trim();
        if (tstr.indexOf("href") < 0) {
          tstr = StringEscapeUtils.escapeHtml4(csvRow.get(jj).trim());
        }
      }
%>
<td><%=tstr%>&nbsp;</td>
      
<%
    }
    for(; jj < hdrSize; jj++) {
%>
      <td>&nbsp;</td>
<%
    }
%>
</tr>
<%

  } //end for loop
} //end null check
%>

</tbody>
</table>
</div>
<p>

</form>

<%@ include file = "footer.html" %>
</body>
</html>

<% } catch (Exception e) {};
 } %>

