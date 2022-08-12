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
   <script src="tgt_constr.js"></script>
   <script src="../proposal.js"></script>
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

<div style="width:100%;clear:both;float:left;">
<div class="instrTxt">
<span style='font-size:large;'><a href='javascript:openHelpWindow(\"#CONSTRAINTS\")'>Constraints:</a></span>
<ul style="margin:0!important;" > 
<li> If any constraint is set to Y (i.e., required), the observation is time constrained and therefore subject to the limits set for the number of approved constrained observations during the Chandra Peer Review. 
<li>You may want to use the <a href='/soft/provis/' target='_top'>PRoVis</a> tool to investigate Chandra spacecraft roll, pitch and visibility for your target as a function of date. Chandra Mission Planning schedules all observations, but information from PRoVis can be especially useful for proposers whose observations require constraints.  
<li> <span class="errmsg">BEFORE SUBMISSION,</span> please use the 
Constraints/SlewTax check on the 'Review and Submit' page to estimate the constraint difficulty level of each target and the total slew tax for your proposal.
</ul>
</div>
</div>

<div id="formB_container" ></div>
</div>
</body>
</html>
