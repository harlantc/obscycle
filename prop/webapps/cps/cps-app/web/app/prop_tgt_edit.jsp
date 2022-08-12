<%@ page session="true" import="java.util.*, edu.harvard.asc.cps.xo.*" %>
<% response.setHeader("Cache-Control","no-cache,no-store"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", -1); 
%>

<%
String pid = (String)request.getParameter("pid");
if (pid == null)  pid = "unknown";
String tid = (String)request.getParameter("tid");
if (tid == null)  tid = "unknown";

String pno = (String)request.getParameter("propno");
String propno="Proposal #";
if (pno == null) {
  propno = "";
} else {
  propno += pno;
}

String tabpage = (String)request.getParameter("page");
if (tabpage == null) {
  tabpage = "unknown";
}


%>

<%@ include file="cps_hdr.html" %>
</head>
<body onload="showTimeoutWarn(false);doOnLoad()">

   <script type="text/javascript">
     var myTgtEditForm, mygrid, lastTab;


   function setMyDiv(userFile)
   {
      var width=parent.innerWidth*.75;
      if (width > 800)
        document.getElementById("applic").style.width = width;

      document.getElementById("applic").innerHTML = "<iframe src='"+userFile+"' style='border:none;overflow:visible;margin-top:0;margin-bottom:0;width:98%;height:98%;' class='tgtdiv'>";
   }


   function before_change(id,oldval,newval) {
    
     //console.log(id + " oldval=" + oldval + "  nval=" + newval );


     var changes=getCookie("unsavedChanges");
     if (changes!=null && changes>0)
     {
        if (oldval == null) return false;
        if (oldval == newval) return false;
        dhtmlx.confirm({ type:"confirm",
          text: UNSAVED_CHANGES,
          type: "confirm-dialog",
          width:"auto",
          ok:"Continue without saving",
          cancel:"Return to the form",
          callback: function(result){
            if (result) {
              setChanges(0);
              myTgtEditForm.setItemValue('tgtentry',newval);
              changedTarget();
            } 
         }
       });
       return false;
     }
     else {
       return true;
     }

   }


   function changedTarget() {
     var  name = myTgtEditForm.getItemValue("tgtentry");

     // if tabpage is set, they entered through link from the
     // Review&Submit page, so use that value and then clear it 
     var pagetab = myTgtEditForm.getItemValue("tabpage");
     myTgtEditForm.setItemValue("tabpage","unknown");
      
     var tgtid = parseInt(name);
     if (tgtid > 0) {
       setChanges(0);
       var userFile = "prop_tgt_nav.jsp?pid=<%=pid%>&tid=" + name;
       if (pagetab  != "unknown") {
         userFile += "&page="  + pagetab;
       }  

       setMyDiv(userFile);
     }
   }

   function go_to_tgtmgr() {
     top.parent.mainTabbar.selectItem(<%=pid%>+"TargetManage",true)

   }
   function addTargetOptions(ele,url)
   {
     var pagetab = "<%=tabpage%>";
     myTgtEditForm.setItemValue("tabpage",pagetab);
     if (pagetab  != "unknown") {
        parent.tgtentry.load(url,postEditOpt);
       
     } else if (top.parent.tgtOptions != null) {
       for (var ii=0; ii< top.parent.tgtOptions.length;ii++) {
         var opt = top.parent.tgtOptions[ii];
         ele.addOption(opt[0],opt[1],null,null);
       }
       postEditOpt();
     }
   }


   function doOnLoad()
   {
     var formData = [
      {type:"block",list:[
      {type:"combo", name:"tgtentry", label:"Switch to Target: ",inputWidth:200},
      {type:"newcolumn",offset:20},
      {type:"button", offsetTop:0,name:"alltgt", value:"All Targets"},
      ]},
      {type:"input",hidden:true, name:"isEdit",value:"false"},
      {type:"input",hidden:true, name:"tabpage",value:"unknown"},
      //{type:"label", offsetTop:0,label:"<span class='instrTxt'>Navigate all tabs to input target parameters. Last tab is Remarks.</span>"},
      {type:"container", offsetTop:0,className:"tgtdiv",label:"<div id='applic' class='applic' ></div>"},
   {type:"container",name:"status",className:"statusContainer"},

   ];

      myTgtEditForm = new dhtmlXForm("formB_container", formData);
      //document.getElementById("applic").style.width = (parent.innerWidth * .7);
      myTgtEditForm.setSkin('dhx_skyblue');
      myTgtEditForm.attachEvent("onButtonClick", function(id) {
       if (id=="alltgt") go_to_tgtmgr();
      } );
      myTgtEditForm.attachEvent("onBeforeChange", before_change);
      myTgtEditForm.attachEvent("onChange", changedTarget);
      lastTab="t1";

      top.parent.tgtentry = myTgtEditForm.getCombo("tgtentry");
      var str = "pid=" + "<%=pid%>" ;
      var url = "/cps-app/cps_tgts?operation=LOADTGTEDIT&"+str;

      //parent.tgtentry.clearAll();
      top.parent.tgtentry.enableAutocomplete();
      top.parent.tgtentry.enableFilteringMode("between");
      top.parent.tgtentry.allowFreeText(false);

      // this hides the top frame navigation arrows
      obj = top.document.getElementById("divarr");
      if (obj != null) obj.style.display=style_init;
      document.body.className = "waiting";

      // do this if change alltgt from button to link
      // obj=myTgtEditForm.getContainer('alltgt');
      // obj.innerHTML="<a href='javascript:go_to_tgtmgr();'>All Targets</a>";

      // make the search box look different than other input fields
      var tgtobj = myTgtEditForm.getCombo("tgtentry");
      var inputobj=tgtobj.getInput();
      if (inputobj != null)
        inputobj.style.backgroundColor="#f9f9d0";

      // load the target options
      addTargetOptions(tgtobj,url);
       

      window.addEventListener("resize",resizeLayout, false);
   }


   function postEditOpt()
   {
      myTgtEditForm.setItemValue("tgtentry","<%=tid%>");
      //console.log("Selected " + "<%=tid%>");
      document.body.className = "";
      changedTarget();

   }
   function resizeLayout() {
      var width=parent.innerWidth*.75;
      if (width < 700) width=700;
      document.getElementById("applic").style.width = width;
       
   }

   </script>

<div style="height:100%;width:100%;">
   <div id="formB_container" style="overflow:visible;float:left;width:98%;height:96%;"></div>
<div id='tdivarr' class='divarr'>
<table border="0" width="100%">
<tr>
<td align="left" width=33%><span id='spanprev'><a class='fakeBtn' href='javascript:find_tgt_prev_tab()'><img src='/cps-app/imgs/arrow-left.png' class='arrowimg' alt=''>Tab Back</a></span></td>
<td align="center" width=33%> <a href='javascript:go_to_tgtmgr();'>Back to all Targets</a></td>
<td align="right" width=33%> <span id='spannext'><a class='fakeBtn' href='javascript:find_tgt_next_tab()'>Next Tab<img src='/cps-app/imgs/arrow-right.png' class='arrowimg' alt=''></a></span></td>
</tr>
</table>
</div>
</div>


</body>
</html>



