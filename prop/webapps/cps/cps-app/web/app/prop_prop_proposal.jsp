<%@ page session="true" import="java.util.*" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1
%>

<%
String pid = (String)request.getParameter("id");
if (pid == null) pid = "unknown";

%> 

<%@ include file="cps_hdr.html" %>

   <script src="../options.js"></script>
   <script src="prop_proposal.js"></script>

   <script type="text/javascript">
     var pid = "<%=pid%>";


   </script>
</head>
 
<body onload="showTimeoutWarn(false);doOnLoad()">
  <div id="msgbody" class="msgbody">
    <img src="/cps-app/imgs/ajax-loader.gif" alt="Loading...">
  </div>
  <div id="pagebody" class="pagebody">

    <div class="instrLbl"><div id="geninstr" class="instrTxt">Use menus at left or arrows at bottom to navigate.</div></div>
   <div style="clear:both;float:left" id="formB_container" ></div> 
</div>
</body>
</html>
