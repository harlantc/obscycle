<%@ page session="true" import="java.util.*" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1
%>
<%
String pid = (String)request.getParameter("id");
if (pid == null) pid = "unknown";

%> 

<%@ include file="cps_hdr.html" %>
   <script src="prop_joint.js"></script>

</head>
 
<body onload="pid=<%=pid%>;showTimeoutWarn(false);doOnLoad();">
  <div id="msgbody" class="msgbody">
    <img src="/cps-app/imgs/ajax-loader.gif" alt="Loading...">
  </div>
  <div id="pagebody" class="pagebody">
   <div id="formB_container" ></div> 
  </div>
</body>
</html>
