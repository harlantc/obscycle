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
%> 

The panel to which you are assigned is not available for editing at this time.<br>
Please try again later. <br>
  </body>
</html>


