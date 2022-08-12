<%@ page session="true" import="java.util.*, edu.harvard.cda.proposal.xo.PersonAndPersonShort,edu.harvard.asc.cps.xo.CPS,org.apache.commons.lang3.StringEscapeUtils" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1
%>

<%
String pid = (String)request.getParameter("id");
if (pid == null)  pid = "unknown";

%>

<%@ include file="cps_hdr.html" %>
   <script src="../options.js"></script>
   <script src="prop_pi.js"></script>
   <script>
    var pid = "<%=pid%>";

   function doThis() {
       showTimeoutWarn(false);
       doOnLoad();
   }
 

   </script>

</head>
 
<body onload="doThis()">
  <div id="msgbody" class="msgbody">
    <img src="/cps-app/imgs/ajax-loader.gif" alt="Loading...">
  </div>
  <div id="pagebody" class="pagebody">

   <div id='instrLbl' class="instrLbl"><div id="piinstr" class="instrTxt"></div></div>
   <div style="clear:both;float:left;" id="formB_container" ></div> 
  </div>
</body>
</html>
