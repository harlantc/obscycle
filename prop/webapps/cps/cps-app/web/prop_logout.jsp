<%@ page session="true" import="edu.harvard.asc.cps.xo.CPS" %>

<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1

String startURL = CPS.getStartURL();
String msg = (String)request.getParameter("msg");
if (msg == null) msg="";
if (session != null) {
  try {
    session.invalidate();
  } catch (Exception ex) {
  }
}

%>

<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML//EN">
<html>
<head>
   <script src="/cps-app/dhtmlx.js"></script>
   <title>CPS Logout</title>
</head>
 
<body id="banner" onLoad='doLogout();'>

<script language="JavaScript" type="text/javascript"> 


function clearAllCookies()
{
  var ii,ckey,cval,ARRcookies=top.document.cookie.split(";");
  for (ii=0; ii<ARRcookies.length; ii++) {
    ckey=ARRcookies[ii].substr(0,ARRcookies[ii].indexOf("="));
    ckey=unescape(ckey.replace(/^\s+|\s+$/g,""));
    if (ckey.indexOf("server") <0 && ckey.indexOf("mycps") < 0)  {
      ckey += "=;expires=Sat, 01-Jan-2000 00:00:00 GMT;";
      top.document.cookie=ckey;
    }
  }
}

function doLogout()
{
  if("<%=msg%>" != "") 
     alert("<%=msg%>");
  var startURL = "<%=startURL%>";
  if (startURL == null) startURL="http://cxc.harvard.edu/";
  clearAllCookies();
  top.location.replace(startURL);
}
</script>
</body>
</html>



