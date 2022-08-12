<%@ page session="true" import="java.util.*, edu.harvard.asc.cps.xo.CPS" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1
%>

<%
String cdoMessage = CPS.getNoDDTMessage();

%>

<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML//EN">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
   <meta http-equiv="Pragma" content="no-cache">
   <meta http-equiv="Cache-Control" content="no-cache">
   <meta http-equiv="Expires" content="Thu, 19 Aug 1999 00:00:00 GMT">
</head>
<body >

<%=cdoMessage%>

</body>
<p>

</html>
