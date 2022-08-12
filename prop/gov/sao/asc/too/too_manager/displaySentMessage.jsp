<%@ page session="true" import="java.io.*,org.apache.commons.lang3.*" %>
<% 
  String message = (String)session.getAttribute("message");
  if (message == null) {
     message = new String("");
  }
  String fileName = (String)request.getParameter("fileName");

  response.addCookie(new Cookie("JSESSIONID", session.getId()));
%>
<%@ include file = "tooManagerHead.jsp" %>
<body class="body" >
<table width="100%" cellspacing="0" cellpadding="0">
<tr>
<td><img src="header_left.gif" alt="Chandra Science"></td>
<td colspan="4"><h1>Chandra TOO Manager</h1></td>
</tr>
</table>
<p>
<a href="/toomanager/tooManager.jsp">Back to Chandra TOO Manager</a>
</p>
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
