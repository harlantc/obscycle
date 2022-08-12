<%@ page session="true" import="java.util.*, edu.harvard.asc.cps.xo.CPS" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1
%>

<%
String login = (String)session.getAttribute("login");
if (login == null) login = "unknown";
Boolean ddtRequest = (Boolean)session.getAttribute("ddtRequest");
Boolean isCfP = CPS.isCfP();
if (ddtRequest == null) ddtRequest=false;
String maintitle = "Chandra Proposal Submission";
String cdoMessage = "";
if (ddtRequest) {
  maintitle = "Chandra Director's Discretionary Time (DDT)";
  cdoMessage = CPS.getDDTCDOMessage();
} else {
  cdoMessage = CPS.getCDOMessage();
}

String editProfile = CPS.getEditProfile(ddtRequest);
String helpDesk = CPS.getHelpDesk();
String officialDate = CPS.getCfPOfficialDate();
Long endDate = CPS.getCfPEndDate();
Long cfpDate = CPS.getCfPDate();
String startURL = CPS.getStartURL();


%>


<%@ include file="cps_hdr.html" %>

<script type="text/javascript">

var tgtOptions=null;
var maintgttabbar=0;
var mainTabbar;
var mainOldId;
var navForm;
var mainwarnmsg;
var tgtentry;
var helpurl = HELP_URL;
var ddtrequest = <%=ddtRequest%>;
var isCfP = <%=isCfP%>;
var timeoutFS=null;
var valWindow=null;
var timerId;
timerId= setTimeout( 'checkSession();', 1*60*1000);
  var userFile = "";
  var theId;


function setDeadline()
{
  var deadlineTimer;
  var officialDate = "<%=officialDate%>";
  var endDate = <%=endDate%>;
  var cfpDate = <%=cfpDate%>;
  var cdate = new Date().getTime();
  var timeOffset = Math.abs(getCookie('clientTimeOffset'));
  if (!isNaN(timeOffset))  {
    cdate =  cdate - timeOffset;
  }

  

  var msg="";
  //alert(endDate + "--" + cdate);
  if (!ddtrequest) {
    if (isCfP) {
      // we usually give a few minutes past deadline for final saves
      var daysLeft =  (cfpDate - cdate)/  (24.0 * 60.0 * 60.0 * 1000.0);
      var xtraLeft =  (endDate - cdate)/  (24.0 * 60.0 * 60.0 * 1000.0);
  
      if (daysLeft < 0){
        msg = "<span class='mainwarnblderr'>It is past the deadline.</span><span class='mainwarnmsgerr'> Please save and submit your proposal now!  Deadline: " + officialDate+ "</span>";
      } else {
        if (Math.floor(daysLeft) < 2) {
          if (daysLeft > 1) 
            msg = "There is <span style='color:#dd0000;font-weight:bold;'>" + "1"  + "</span> day until the " + officialDate + " deadline.";
          else {
            var yy = Math.ceil(((daysLeft*86400)+ 359)/(60 * 60));
            var hr="hours";
            var hr1="are";
            if (yy < 2) {
               hr= "hour";
               hr1="is";
            }
              
            msg = "There " + hr1 + " <span style='color:#dd0000;font-weight:bold;'>less than "  + yy + " " + hr + "</span> until the " + officialDate + " deadline.";
            //msg = "There is <span style='color:#dd0000;font-weight:bold;'>" + "less than 1"  + "</span> day until the " + officialDate + " deadline.";
          }
        } else {
          msg = "There are <span style='font-weight:bold;'>" + Math.floor(daysLeft) + "</span> days left until the " + officialDate + " deadline.";
        } 
      } 
      var timeout=30*60*1000;
      if (daysLeft > 0) {
        if (daysLeft  < 1)
          timeout= 1*60*1000;
      }
      else if (xtraLeft > 0) {
        timeout= 1*60*1000;
      }
      else {
        isCfP=false;
        timeout = .5*60*1000;
      }
      //alert(timeout);
      deadlineTimer= setTimeout( 'setDeadline();',timeout);
    }      
    else {
       //alert("not cfp");
    }
  }      
  msg += "<br>" + "<%=cdoMessage%>" ;
    
  mainwarnmsg.innerHTML=msg;
}
  

function postLoad()
{
  if (!ddtrequest)
    setDeadline();
  else 
    mainwarnmsg.innerHTML="<%=cdoMessage%>";
  
  calcOffset();

  timeoutFS = document.getElementById("timeoutFS");

  
}

function edit_profile() 
{
  var changes=getCookie("unsavedChanges");
  if (changes!=null && changes>0)
  {
    var txt = UNSAVED_CHANGES;
    if (changes==2) {
      txt = "The Submission acknowledgement has been checked but the application will not be sent until the [Submit Application] button is pressed.  Press [Ok] to continue without submitting.";
    }
    dhtmlx.confirm({ type:"confirm",
        type: "confirm-dialog",
        width:"auto",
        text: UNSAVED_CHANGES,
        ok:"Continue",
        cancel:"Return to the form",
        callback: function(result){
          if (result) {
            setChanges(0);
            self.location.replace("<%=editProfile%>");
          }
        }
    });
  } else {
    self.location.replace("<%=editProfile%>");
  }
  return;
}


function log_me_out() 
{
  var changes=getCookie("unsavedChanges");
  if (changes!=null && changes>0)
  {
    var txt = UNSAVED_CHANGES;
    if (changes==2) {
      txt = "The Submission acknowledgement has been checked but the application will not be sent until the [Submit Application] button is pressed.  Press [Ok] to continue without submitting.";
    }
    dhtmlx.confirm({ type:"confirm",
	text: UNSAVED_CHANGES,
        type: "confirm-dialog",
        width:"auto",
	ok:"Continue",
	cancel:"Return to the form",
	callback: function(result){
          if (result) {
            setChanges(0);
            self.location.replace("/cps-app/prop_logout.jsp");
          }  
        }
    });
  } else {
    self.location.replace("/cps-app/prop_logout.jsp");
  }
}

function prop_checked(id)
{
  theId=id;
  userFile="";
  if (id!= "m1" && isNaN(id)) {
    var pid=id.match(/\d+/);
    if (id.indexOf("Proposal") > 0) {
      userFile = "prop_prop_proposal.jsp?id=" ;
    }
    else if (id.indexOf("PI") >0) {
      userFile = "prop_prop_pi.jsp?id=" ;
    }
    else if (id.indexOf("CoI")  > 0) {
      userFile = "prop_prop_coi.jsp?id=";
    }
    else if (id.indexOf("DDT")  > 0) {
      userFile = "prop_prop_ddt.jsp?id=";
    }
    else if (id.indexOf("Joint")  > 0) {
      userFile = "prop_prop_joint.jsp?id=";
    }
    else if (id.indexOf("Upload2")  > 0) {
      userFile = "prop_prop_upload2.jsp?id=";
    }
    else if (id.indexOf("Upload")  > 0) {
      userFile = "prop_prop_upload.jsp?id=";
    }
    else if (id.indexOf("TargetChecks")  > 0) {
      userFile = "prop_prop_tgtcheck.jsp?id=";
    }
    else if (id.indexOf("Validate")  > 0) {
      userFile = "prop_prop_validation.jsp?id=";
    }
    else if (id.indexOf("Submit")  > 0) {
      userFile = "prop_prop_summary.jsp?id=";
    }
    else if (id.indexOf("TargetManage") > 0)  {
      userFile = "prop_tgt_manage.jsp?id=";
    }
  }
  else {
    if (id == "m1") {
     userFile = "prop_manage.jsp?id=";
    }
    else {
    }
  }
  if (userFile.length > 2) {    
    var changes=getCookie("unsavedChanges");
    if (changes!=null && changes>0)
    {
      var oklbl="Continue without saving";
      var txt = UNSAVED_CHANGES;
      if (changes==2) {
        txt = "The Submission acknowledgement has been checked but the application will not be sent until the [Submit Application] button is pressed.  Press [Ok] to continue without submitting.";
         oklbl = "Ok";
      }
      dhtmlx.confirm({ type:"confirm",
        type: "confirm-dialog",
        width:"auto",
        text: UNSAVED_CHANGES,
        ok:oklbl,
        cancel:"Return to the form",
        callback: function(result){
          if (result) {
            setChanges(0);
            changePage();
          } else {
            mainTabbar.selectItem(mainOldId,false,false);
          }
       }
       });
      return false;
    } else {
      changePage();
      return true;
   }
  }
  else {
    var idstate = mainTabbar.getOpenState(id);
    mainTabbar.selectItem(mainOldId,false,false);
    if (idstate < 0)
      mainTabbar.openAllItems(id);
    else
      mainTabbar.closeAllItems(id);
    
  }
    

  //document.getElementById('tabarea1').style.overflowY =
   //(document.getElementById('tabarea1').offsetHeight > 399) ? 'auto' : 'hidden'; 
}

function changePage()
{
  mainOldId=theId;
  var pid=theId.match(/\d+/);
  userFile += pid;
  var ppid =mainTabbar.getParentId(theId);
  if (ppid != "myroot") {
    var pno = mainTabbar.getItemText(pid);
    var tag = pno.substring(4);
    if (tag > "8000")
      parent.helpurl = HELP_DDT_URL;
    else
      parent.helpurl = HELP_URL;
  } else {
    if (!ddtrequest)
      parent.helpurl = HELP_URL;
    else
      parent.helpurl = HELP_DDT_URL;
  }
  if (userFile.indexOf("prop_manage") >= 0) {
    obj = document.getElementById("divarr");
    obj.style.display=style_init;
  } else {
    obj = document.getElementById("divarr");
    obj.style.display=style_load;
  }
  mainTabbar.focusItem(theId);
  document.getElementById("tabarea2").innerHTML = "<iframe id='cpsframe' src='app/"+userFile+"' style='width:100%;height:100%;border:none;overflow:visible'>";
  showHideNav(theId);
}

function loadPage()
{
  navForm.load("/cps-app/cps_props?operation=NAV",postLoad);
}


function doOnLoad(skin) {

   setChanges(0);
   skin = skin||"material";
   var navData = [
     {type:"input",hidden:true,name:"deadlinedays",value:""},
     {type:"input",hidden:true,name:"isCfP",value:""},
   ];
   navForm = new dhtmlXForm("formB_container",navData);
   navForm.setSkin('dhx_skyblue');

   mainTabbar = new dhtmlXTreeObject("tabarea1","100%","100%","myroot");
   mainTabbar.setImagePath(NAV_IMG_PATH);
   mainTabbar.setSkin("dhx_web");
   mainTabbar.enableTreeImages(false);
   mainTabbar.enableCheckBoxes(false,false);
   mainTabbar.enableSmartCheckboxes(true);
   mainTabbar.enableHighlighting(true);
   mainTabbar.attachEvent("OnClick",prop_checked);

   mainTabbar.insertNewItem("myroot","m1","Home");
   mainTabbar.selectItem("m1",true,false)
   mainTabbar.setItemText("m1","Home","Click on proposal number to expand/contract sections. Click on section link to navigate directly to that section form");

  //dhtmlxEvent(window,"load",function(){mainTabbar.adjustSize()});
  mainwarnmsg = document.getElementById("mainwarnmsg");
  loadPage();

}


function calcOffset() {
    var serverTime = getCookie('serverTime');
    serverTime = serverTime==null ? null : Math.abs(serverTime);
    var clientTimeOffset = (new Date()).getTime() - serverTime;
    top.document.cookie ="clientTimeOffset=" + clientTimeOffset ;
}

function checkSession() {
    var sessionExpires = Math.abs(getCookie('mycps'));
    var timeOffset = Math.abs(getCookie('clientTimeOffset'));
    if (sessionExpires > 0) {
      var localTime = (new Date()).getTime();
      theDiff = sessionExpires - (localTime-timeOffset) ;
      var sessiontime = 1*60*1000;
      if (theDiff <= 0) {
        userHasTimedOut();
      } else if (theDiff < (5*60*1000))  {
        //  under 5 minutes
        var mindiff = theDiff/(60*1000);
        var minlbl=" minutes";
        if (Math.ceil(mindiff) < 2) minlbl = " minute";
        setTimeoutMsg("<span class=\"mainwarnblderr\">  WARNING .... timing out in less than " + Math.ceil(mindiff) + minlbl + "</span>");
        showTimeoutWarn(true);
        sessiontime = 30*1000;
      } else if (theDiff > (10*60*1000))  {
        sessiontime = 5*60*1000;
        showTimeoutWarn(false);
      } else  {
        showTimeoutWarn(false);
      }
      timerId = setTimeout('checkSession()', sessiontime);
    }
}


function userHasTimedOut() {
        top.location.replace("/cps-app/prop_logout.jsp");
}


</script>
</head>

<body onload="doOnLoad('dhx_skyblue');" >

<div style="height:7%;width:100%;clear:both;overflow:visible;">
  <div id="header" style="float:left;width:1%;"> </div>
  <div id="topcenter" style="margin-left:5px;float:left;width:55%;margin-top:5px;">
    <span id="maintitle" ><a href="/proposer/"><%=maintitle%></a></span>
    <br><span id="mainwarnmsg" class="mainwarnmsg"></span>
  </div>
  <div id="topright" style="float:right;width:40%;margin-top:5px;margin-right:2%;">
    <div id="toprl" style="float:left;"> Welcome <%= login %>
      <span style="margin-left:10;">
          <a href="javascript:edit_profile()" > Edit&nbsp;Profile</a>
      </span>
      <span style="margin-left:10;">
          <a href='javascript:openHelpWindow(0);'> CPS Help</a>
      </span>
      <span style="margin-left:10;">
          <a href="<%=helpDesk%>" target="_blank"> HelpDesk</a>
      </span>
      <span style="margin-left:20;">
          <a href="javascript:log_me_out()" >Logout</a> 
      </span>
    </div>
  </div>
</div>

<div style="height:80%;width:100%;clear:both;">
  <fieldset id="timeoutFS" style="display:none;border:0"><legend> </legend> <span style="font-size:150%; font-weight:bold; color:red">  WARNING .... timing out in less than 5 minutes </span></fieldset>

  <div id="tabarea1"></div> 
  <div id="tabareadata" >
    <div id="tabarea2"></div> 
    <div id='divarr' class='divarr'><span class='spanprev' id='spanprev'><a class='fakeBtn' href='javascript:find_prev_tab()'><img src='/cps-app/imgs/arrow-left.png' alt="" class='arrowimg'>Back</a></span><span id="spannext" class='spannext'><a class='fakeBtn' href='javascript:find_next_tab()'>Next<img src='/cps-app/imgs/arrow-right.png' alt="" class='arrowimg'> </a></span>
    </div>
    <div id="formB_container" style="height:1px;width:99%"></div>
  </div>
</div>

<div style="width:99%;clear:both;height:10%;overflow:hidden;position:absolute;bottom:1px;">
<%@ include file ="../footer.html" %>
</div>
</body>

</html>
