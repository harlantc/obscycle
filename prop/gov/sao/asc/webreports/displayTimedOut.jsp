<%@ page session="true" import="java.util.*, info.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<META http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Chandra Panel Access Site</title>
</head>

<body>	
<% 
response.addCookie(new Cookie("JSESSIONID", session.getId()));
String timeout = (String)session.getAttribute("timeout");
String startPageURL = (String)session.getAttribute("startPageURL");
/* kill session so that if the try to connect from another window */
/* it doesn't pick up this session variable */
session.invalidate();
%> 
<b>Session Expired</b>
<br><br>
Your session has expired due to inactivity.
If you were writing a report, this is due to the fact that you did not save your
changes for this period of time.  

<br>
However, if you were writing a report when your session expired, the report 
will be available the <u>first</u> time you login again.  Your expired report 
will appear as a link from the report page.  Click the link below to enter the 
Reviewers site, and continue editing your reports.

<br><br>
<a href="<%=startPageURL%>">Return to the login page</a>


  </body>
</html>


