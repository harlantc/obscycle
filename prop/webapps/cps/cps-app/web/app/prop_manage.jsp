<%@ page session="true" import="java.util.*,edu.harvard.asc.cps.xo.CPS" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1
%>

<%
Boolean ddtRequest= (Boolean)session.getAttribute("ddtRequest");
if (ddtRequest==null) ddtRequest=false;
Boolean isCfP= (Boolean)CPS.isCfP();
if (isCfP==null) isCfP=false;
%>

<%@ include file="cps_hdr.html" %>


   <script src="../options.js"></script>
   <script src="prop_manage.js"></script>

   <script>
   function doThis() {
         showTimeoutWarn(false);
       doOnLoad();
   }

   </script>

</head>
 
<body  onload="doThis();">
  <div id="msgbody" class="msgbody">
    <img src="/cps-app/imgs/ajax-loader.gif" alt="Loading...">
  </div>
  <div id="pagebody" class="pagebody">

   <div id="homeDiv" style="width:94%">
   <div id="filtDiv" class="filtDiv">
      <div id="filtTreeDiv" class="filtTreeDiv"> </div>
   </div>
   <span id="homeinstr" class="homeinstr">*Proposals with a gray background are not editable</span>
</div>
   <div id='grid' style='z-index:0;margin-left:20;width:95%'></div>
   <div id="formB_container" style="width:95%"></div>
   <script>
       var ddtRequest= "false";
       if (<%=ddtRequest%>)
         ddtRequest = "true";
      var isCfP = <%=isCfP.booleanValue()%>;


   </script>
  </div>
</body>
</html>
