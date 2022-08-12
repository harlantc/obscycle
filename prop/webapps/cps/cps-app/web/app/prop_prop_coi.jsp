<%@ page session="true" import="java.util.*,edu.harvard.cda.proposal.xo.PersonAndPersonShort,edu.harvard.asc.cps.xo.CPS" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1
%>
<%
String pid = (String)request.getParameter("id");
if (pid == null)  pid = "unknown";
%> 

<%@ include file="cps_hdr.html" %>
   <script src="prop_coi.js"></script>
   <script src="../options.js"></script>

   <script type="text/javascript">
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

   <div class="instrLbl"><div id="coiinstr" class="instrTxt">To edit a Co-I, select a row in the table and click <b>Modify Selected</b>. Drag-n-drop rows in the table to re-prioritze the Co-Investigators</div></div>
   <span style="clear:both;float:left;" id='coifs' class="fslbl">Co-Investigators</span>
   <div id='grid' style="margin-left:20;width:95%;"></div>
   <div id="formB_container" ></div>
  </div>
</body>
</html>
