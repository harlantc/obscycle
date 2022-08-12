<%@ page session="true" import="java.util.*" %>
<% response.setHeader("Cache-Control","no-cache,no-store"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", -1); 
%>
<%
String pid = (String)request.getParameter("pid");
if (pid == null) {
  pid = "unknown";
}
String tid = (String)request.getParameter("tid");
if (tid == null) {
  tid = "unknown";
}  
String tabpage = (String)request.getParameter("page");
if (tabpage == null) {
  tabpage = "unknown";
} 


String detector = (String)session.getAttribute(tid+"detector");
if (detector == null) detector = "tooACIS";

%>

<%@ include file="cps_hdr.html" %>


<script>
var tgttabbar;
var detector="<%=detector%>";
var tracktgtpage=1;
var myForm;

function set_tabbar_url(id,urlparam)
{
  if (id == "t1") 
    tgttabbar.tabs("t1").attachURL( "prop_tgt_pointing.jsp?" + urlparam,false);
  if (id == "t2") 
    tgttabbar.tabs("t2").attachURL( "prop_tgt_time.jsp?" + urlparam,false);
  if (id == "t3") 
    tgttabbar.tabs("t3").attachURL( "prop_tgt_instr.jsp?" + urlparam,false);
  if (id == "t4") 
    tgttabbar.tabs("t4").attachURL( "prop_tgt_acis.jsp?" + urlparam,false);
  if (id == "t5") 
    tgttabbar.tabs("t5").attachURL( "prop_tgt_acisopt.jsp?" + urlparam,false);
  if (id == "t6") 
    tgttabbar.tabs("t6").attachURL( "prop_tgt_constr.jsp?" + urlparam,false);
  if (id == "t7") 
    tgttabbar.tabs("t7").attachURL( "prop_tgt_too.jsp?" + urlparam,false);
  if (id == "t8") 
    tgttabbar.tabs("t8").attachURL( "prop_tgt_remarks.jsp?" + urlparam,false);
}

function postLoad() {
  var ptitle = myForm.getItemValue("proposal_title");
  var propno = myForm.getItemValue("propno");
  var hdr = document.getElementById("mytabhdr");
  hdr.innerHTML=propno + ": " + ptitle;
  tgttabbar.attachHeader(hdr);
  
  
}
function doOnLoad(skin) {

  var formData=[
    {type:"input",hidden:true,name:"page",value:"TGTMANAGE"},
    {type:"input",hidden:true,name:"propno",value:""},
    {type:"input",hidden:true,name:"proposal_title",value:""}
  ];
  // needed for the header above the tabbar
  myForm=new dhtmlXForm("formB_container",formData);
  var str = "pid=" + <%=pid%> ;
  myForm.load("/cps-app/cps_tgts?operation=PAGELOAD&" + str,postLoad);

  skin = skin||"material";
  tgttabbar = new dhtmlXTabBar({parent:"tabarea3",skin:skin,
        tabs:[ 
	  {id:"t1",text: "Target Pointing"},
	  {id:"t2",text: "Observing Time"},
	  {id:"t3",text: "Instrument"},
	  {id:"t4",text: "ACIS (req)"},
	  {id:"t5",text: "ACIS (opt)"},
	  {id:"t6",text: "Constraints"},
	  {id:"t7",text: "TOO Details"},
	  {id:"t8",text: "Remarks"},
	]
	});

  top.document.maintgttabbar= tgttabbar;
  tgttabbar.enableAutoReSize(true);
  tgttabbar.setArrowsMode("auto");
 
  var urlparam= "pid=<%=pid%>&tid=<%=tid%>";
  //tgttabbar.tabs("t1").attachURL( "prop_tgt_pointing.jsp?" + urlparam);
  set_tabbar_url(parent.lastTab,urlparam)
<%
detector = (String)session.getAttribute(tid +"detector");
if (detector == null) detector="";
%>
  detector="<%=detector%>";

  //alert("tgt_nav=" + detector);
  if (detector.indexOf("ACIS") < 0) {
    tgttabbar.tabs("t4").disable();
    tgttabbar.tabs("t5").disable();
  }  else {
    tgttabbar.tabs("t4").enable();
    tgttabbar.tabs("t5").enable();
  } 
  if (detector.indexOf("too") >= 0){
    tgttabbar.tabs("t7").enable();
  } else {
    tgttabbar.tabs("t7").disable();
  }

  tgttabbar.attachEvent("onSelect",function(id){
     var changes=getCookie("unsavedChanges");
     if (changes!=null && changes>0)
     {
      dhtmlx.confirm({ type:"confirm",
        text: UNSAVED_CHANGES,
        type:"confirm-dialog",
        width:"auto",
        ok:"Continue without saving",
        cancel:"Return to the form",
        callback: function(result){
           if (result)  {
             set_tabbar_url(id,urlparam);
             parent.lastTab=id;
             showHideNav(id);
             setChanges(0);
             tgttabbar.tabs(id).setActive();
             return true;
           }
           else {
             return false;
           }
      }});
      return false;
     } else {
         set_tabbar_url(id,urlparam);
         parent.lastTab=id;
         showHideNav(id);
         var ifr = tgttabbar.tabs(id).getFrame();
         var elem = ifr.contentWindow.document.getElementById("pagebody");
         if (elem) elem.style.display="none";
         return(true);
     }
  });

  if (tracktgtpage == 0 || parent.lastTab == null ||
      !tgttabbar.tabs(parent.lastTab).isEnabled())  {
     parent.lastTab="t1";
  }
  var pagetab = "<%=tabpage%>";
  if (pagetab != "unknown" ) 
    parent.lastTab=pagetab;
  //alert("LAST " +  parent.lastTab);

  tgttabbar.tabs(parent.lastTab).setActive();

    //window.addEventListener("resize",resizeLayout, false);

}
     //function resizeLayout(){
         //tgttabbar.setSizes();
      //}

</script>
</head>

<body onLoad="showTimeoutWarn(false);doOnLoad('dhx_skyblue');">

<div id="formB_container"></div>
<div id="mytabhdr" class="fslbl2"> </div>
<div id="tabarea3" ></div>

</body>
</html>
