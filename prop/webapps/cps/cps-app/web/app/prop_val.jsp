<%@ page session="true" import="java.util.*" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1
%>

<%

String pid = (String)request.getParameter("pid");
if (pid == null)  pid = "unknown";
String pno = (String)request.getParameter("pno");
if (pno == null)  pno = "unknown";

%>
<%@ include file="cps_hdr.html" %>
</head>
 
<body onload="showTimeoutWarn(false);doOnLoad()">
<p>
<span id='tgttitle'>Validation Results</span>
<p>
 <div id="tgtinstr" class="instrLbl" style="width:95%;margin-left:0px;margin-bottom:10px;">
 To see updates, reload this window or return to the <b>Review & Submit</b> section of your proposal.
 </div>

<span id="valmsg" class="processLbl"></span>
<div id='valgriddiv' style='margin:0;width:95%'></div>
<div id="formB_container" ></div>
<script type="text/javascript">
  var pid = "<%=pid%>";
  var pno = "<%=pno%>";
  var myForm,valgrid;

  function doOnLoad() {

    formData = [
      {type:"input",hidden:true,name:"operation",value:""},
      {type:"input",hidden:true,name:"page",value:"SUMMARY"},
      {type:"input",hidden:true,name:"pid",value:"<%=pid%>"},
      {type:"input",hidden:true,name:"propno",value:"<%=pno%>"},
  
      {type:"container",name:"status",className:"statusContainer"}
  
    ];

    myForm = new dhtmlXForm("formB_container",formData);
    myForm.setSkin('dhx_skyblue');
    valgrid=new dhtmlXGridObject('valgriddiv');
    valgrid.setHeader("Type,Page,Tgt#,Tid,Message");
    valgrid.setInitWidthsP("10,20,10,10,*");
    valgrid.setColumnMinWidth("10,10,10,10,10");
    valgrid.setColAlign("center,center,center,center,left");
    valgrid.setColSorting("na,str,int,str,str");
    valgrid.setCustomSorting(sort_span,0);
    valgrid.setSkin("dhx_skyblue");
    valgrid.setColTypes("ro,ro,ro,ro,ro");
    valgrid.setImagePath(CSS_IMG_PATH);
    valgrid.enableAutoHeight(true);
    valgrid.enableAutoWidth(true);
    valgrid.enableMultiline(true);
    valgrid.setColumnHidden(3,true);
    valgrid.init();


    loadPage();
  }

  function loadPage() {
    clear_status();
    var url= "&pid=" + pid + "&operation=valgrid";
 var vmsg = document.getElementById('valmsg');  
 if (vmsg) vmsg.innerHTML= "<img src='/cps-app/imgs/ajax-loader.gif' alt='Loading...'>";




    valgrid.load("/cps-app/cps_review?page=SUMMARY"+url,postGrid,"json");

  }
  function postGrid() {
 var vmsg = document.getElementById('valmsg');  
 if (vmsg) vmsg.innerHTML= "";
    var obj=document.getElementById("tgttitle");
    var pno = myForm.getItemValue("propno");
    if (obj != null && pno != null)
      obj.innerHTML="Validation Results for Proposal " + pno;
  }


  function sort_span(a,b,order){
    var aval = a.split('>')[1];
    var bval = b.split('>')[1];
    if (order=="asc")
      return aval>bval?1:-1;
    else
      return aval<bval?1:-1;
  }



    
</script>
</body>
</html>
