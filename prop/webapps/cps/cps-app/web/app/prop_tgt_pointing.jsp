<%@ page session="true" import="java.util.*" %>
<% response.setHeader("Cache-Control","no-cache,no-store"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", -1); 
%>

<%
String pid = (String)request.getParameter("pid");
if (pid == null)  pid = "unknown";
String tid = (String)request.getParameter("tid");
if (tid == null) tid = "unknown";

%>

<%@ include file="cps_hdr.html" %>
   <script src="tgt_pointing.js"></script>
   <script src="../options.js"></script>

   <script type="text/javascript">
      var tid = "<%=tid%>";
      var pid = "<%=pid%>";

   </script>
</head>
 
<body onload="showTimeoutWarn(false);doOnLoad()">
  <div id="msgbody" class="msgbody">
    <img src="/cps-app/imgs/ajax-loader.gif" alt="Loading...">
  </div>
  <div id="pagebody" class="pagebody">
   <div id="formB_container" ></div> 
  </div>
</body>
</html>
