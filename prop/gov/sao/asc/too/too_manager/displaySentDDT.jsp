<%@ page session="true" import="java.io.*,info.*,org.apache.commons.lang3.*" %>
<% 
  String message = (String)session.getAttribute("ddtmessage");
  Boolean isDDTManager = (Boolean)session.getAttribute("isDDTManager");
  Boolean isDDTMigrate = (Boolean)session.getAttribute("isDDTMigrate");
  DDTEntry theDDT = (DDTEntry)session.getAttribute("ddtEntry");
  String migrateLink = "&nbsp";
  
  if (message == null) {
     message = new String("");
  }

  if (theDDT == null) {
     message += "<br>DDT in session is null";
  }
  else {
     if (theDDT.getApprovalDate() != null && 
         theDDT.getApprovalDate().length() > 2  &&
	 !theDDT.inObsCat() &&
	 isDDTMigrate.booleanValue() == true && 
         theDDT.getStatus().equals(TriggerTooConstants.APPROVED) ) {
       migrateLink="<font class=\"bigRed\">DDT Approved:</font> <a href=\"/toomanager/displayDDTMigrate.jsp\">" +
		TriggerTooConstants.DDTUPDATE + "</a>";
     }
  }


  String fileName = (String)request.getParameter("fileName");

  response.addCookie(new Cookie("JSESSIONID", session.getId()));
%>
<%@ include file = "ddtManagerHead.jsp" %>
<body class="body" >
<table width="100%" cellspacing="0" cellpadding="0">
<tr>
<td><img src="header_left.gif" alt="Chandra Science"></td>
<td colspan="4"><h1>Chandra DDT Manager</h1></td>
</tr>
</table>
<p />
<table border="0" width="100%">
<tr>
<td class="left">
<a href="/toomanager/ddtManager.jsp">Back to Chandra DDT Manager</a>
</td>
<td class="center">
<%=migrateLink%>
</td>
</tr>
</table>
<p>
<b><i><%= message %></i></b>
<hr>
<pre>
<%

   try {
     BufferedReader br = new BufferedReader(new FileReader(fileName));
     String line1 = null, line2 = null;
     while ( (br != null) && (line1 = br.readLine()) != null ) {
      
       out.write(StringEscapeUtils.escapeHtml4(line1));
       out.write("\n");
     }
     if (br != null) {
	br.close();
     }
   } catch (Exception exc) {
      out.write("Unexpected Error reading status file.\n\n");
   }
  
%>

</pre>


</body></html>
