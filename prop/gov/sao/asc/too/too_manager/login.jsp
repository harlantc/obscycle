<%@ page session="true" import="java.io.*" %>
<% 
  String message = (String)session.getAttribute("logMessage");
  String userName = (String)session.getAttribute("userName");
  String mgrMsg = (String)session.getAttribute("mgrMsg");
  if (message == null) {
    message = new String("");
  }
  if (mgrMsg == null) {
    mgrMsg = new String("");
    if (userName == null) {
      // probably the first time through
      String htpath = System.getenv("OBSCYCLE_DATA_PATH");
      if (htpath.length() > 2) {
        htpath += "triggertoo/mgr.msg";
        try {
          BufferedReader in = new BufferedReader(new FileReader(htpath));
          String nextLine;
          while ((nextLine = in.readLine()) != null) {
            mgrMsg += nextLine;
          }
          in.close();
        }
        catch (Exception exc) {
          mgrMsg = "";
        }
      }
    }
  }
  if (userName == null) {
    userName = new String("");
  }
  
%>


<%@ include file = "tooManagerHead.jsp" %>

<body class="body" >
<script type="text/javascript" src="toomanager.js"></script>
<table width="100%" cellspacing="0" cellpadding="0">
<tr>
<td><img src="header_left.gif" alt="Chandra Science"></td>
<td colspan="4"><h1>Chandra TOO Manager</h1></td>
</tr>
</table>
<form action="/toomanager/login" method="post">
<%=mgrMsg%>
<font color="red"><%= message %></font>
<p>
Please enter your Database User Name and Password, and click Login.
<p>
<table>
<tr>
<td><b>Database User:</b></td>
<td><input class="editl" name="userName" value="<%= userName %>" size="15" /></td>
</tr>
<tr>
<td><b>Password: </b></td>
<td> <input class="editl" type="password" name="password" value="" size="10" /> </td>
</tr>
</table>
<p>
<input type="submit" name="Submit" value="TOO Manager"/>
<input type="submit" name="Submit" value="DDT Manager"/>
</p>
</form>
</body>
</html>
