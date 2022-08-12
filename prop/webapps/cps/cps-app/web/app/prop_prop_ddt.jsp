<%@ page session="true" import="java.util.*" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1
%>
<%
String pid = (String)request.getParameter("id");
if (pid == null) pid = "unknown";
%>


<%@ include file="cps_hdr.html" %>
   <script type="text/javascript" src="../options.js"></script>

   <script type="text/javascript">
      var pid = "<%=pid%>";


      function handle_change(id, val) {
        if (id == "xmm_ddt") {
          if (val == "Y") 
             myForm.showItem("xmmblock");
          else {
            var chkarr = ["xmm_status"];
            if ( check_fields(myForm,chkarr) == 0) {
              myForm.hideItem("xmmblock");
            }

          }
        }
        else if (id == "prev_request") {
          if (val == "Y") 
             myForm.showItem("prevblock");
          else {
            var chkarr = ["prev_cycles"];
            if ( check_fields(myForm,chkarr) == 0) {
              myForm.hideItem("prevblock");
            }
          }
        } else if (id == "response_time") {
          clear_errors();
          var lbl = new Array("response_justification","contact_info",
	    "target_justification","next_cfp");
          var reqlbls = "";
          if (val.indexOf("NON") < 0)  {
            reqlbls += "response_justification ";
            reqlbls += "target_justification ";
            reqlbls += "next_cfp ";
          }
          if (val.indexOf("FAST") >= 0) 
            reqlbls += "contact_info ";

          for (var ii=0;ii<lbl.length;ii++) {
            var lstr="";
            if (reqlbls.indexOf(lbl[ii]) >= 0)  {
              lstr = set_label_req(myForm.getItemLabel(lbl[ii]));
            } else {
              lstr = set_label_opt(myForm.getItemLabel(lbl[ii]));
            }
            myForm.setItemLabel(lbl[ii],lstr);
          }
        }
        setChanges(1);

      }

      function before_change(id,old_val,new_val) {
        var retval = true;
        if (id == "response_time") {
           if (new_val == "NON-TRANSIENT" && old_val.length > 1) {
             myForm.getContainer("responsemsg").innerHTML= "TOO Details will be deleted for Non-Transient requests.";
             myForm.getContainer("responsemsg").className= "errmsg";
           } else {
             myForm.getContainer("responsemsg").innerHTML= " ";
           }
        }
        return retval;
      }


      function alertResult(result) {
        parent.mainTabbar.selectItem("m1",true,false);
      }
      function postLoad()
      {
        myForm.getContainer("responsemsg").innerHTML= " ";
        var propno = myForm.getItemValue("propno");
        var emsg = myForm.getItemValue("emsg");
        if (propno.indexOf("Invalid") >= 0) {
          top.location.replace("/cps-app/prop_logout.jsp");
        }
        if (emsg != null && emsg.length > 1){
           doAlert(emsg,alertResult);
        }
        else {
          handle_change("xmm_ddt",myForm.getItemValue("xmm_ddt"));
          handle_change("prev_request",myForm.getItemValue("prev_request"));
          handle_change("response_time",myForm.getItemValue("response_time"));
          myForm.setItemLabel("responsefs",myForm.getItemValue("propno") + ": Response Time  " + myForm.getItemValue("proposal_title"));
          set_editButtons(myForm,myForm.getItemValue("isEdit"));

          myForm.hideItem("coiblock");
          myForm.hideItem("coiold");
          setChanges(0);  
        }
         cps_style_load();


      }

      function loadPage() {
         myForm.load("/cps-app/cps_loadprop?page=DDT&pid="+ pid,postLoad);
      }
   </script>
</head>
 
<body onload="showTimeoutWarn(false);doOnLoad()">
  <div id="msgbody" class="msgbody">
    <img src="/cps-app/imgs/ajax-loader.gif" alt="Loading...">
  </div>
  <div id="pagebody" class="pagebody">

   <div id="formB_container" ></div> 
   </div>

   <script>
      var myForm,formData;
      var scikeyLbl = "<a href='javascript:openHelpWindow(\"#Keywords\")'>Science Keywords:</a>";

      function clear_errors() {
        var lbls = new Array("response_time","contact_info", 
            "response_justification", "target_justification",
            "next_cfp", "transient_behavior","xmm_status","prev_cycles");
        for (var ii=0;ii<lbls.length;ii++) {
          set_errlbl(false,myForm,lbls[ii]);
        }
        clear_status();
      }


      function validate_ddt() 
      {
        var retval=true;
        var str="";
        var reqitems = new Array("response_time" );
        var urgency=myForm.getItemValue("response_time");
        if (urgency.indexOf("NON-TRAN") < 0) {
          reqitems.push("response_justification");
        }
        if (urgency.indexOf("FAST") >= 0) {
            reqitems.push("contact_info");
        }
        if (urgency.indexOf("NON-TRAN") < 0) {
          reqitems.push("target_justification");
          //reqitems.push("transient_behavior");
          //if (urgency.indexOf("SLOW") < 0) {
          reqitems.push("next_cfp");
          //}
        }

        clear_errors();

        for (var ii=0;ii<reqitems.length;ii++) {
          val = myForm.getItemValue(reqitems[ii]);
          if (val==null || val.toString().trim() == "") {
            arr = myForm.getItemLabel(reqitems[ii]).split("<br");
            str += "Missing required field: "  + arr[0] + "<br>";
            set_errlbl(true,myForm,reqitems[ii]);
          }
        }
        str += validate_yn(myForm.getItemValue("xmm_ddt"),myForm,["xmm_lbl","xmm_status"]);
        str += validate_yn(myForm.getItemValue("prev_request"),myForm,["prev_lbl","prev_cycles"]);
        if (str.length > 1) {
          set_status("<span class='errlbl'>" + SAVE_FAILED +  str + "</span>");
          retval=false;
        }

        return retval;

      }

      function save_changes() 
      {
        myForm.setItemValue("operation","SAVE");
        if (validate_ddt()) {
          myForm.disableItem("save");
          document.body.className = "waiting";
          myForm.send("/cps-app/cps_saveprop","post",function(name,retstr) {
             document.body.className = "";
             myForm.enableItem("save");
             if (process_save(retstr,false))  {
               myForm.getContainer("responsemsg").innerHTML= " ";
               setChanges(0);
             }
          });
        }
      }

      function reset_fields() {
         loadPage();
         setChanges(0);  
      }

      function doOnLoad() {

         var lblW=140,lblW2=350;
         var inW=600;
         formData = [ 
           {type:"settings",position:"label-left"},
            {type:"input",hidden:true,name:"propno",value:"TBD"},
            {type:"input",hidden:true,name:"proposal_title",value:""},
            {type:"input",hidden:true,name:"pid",value:""},
            {type:"input",hidden:true,name:"emsg",value:""},
            {type:"input",hidden:true,name:"isEdit",value:""},
            {type:"input",hidden:true,name:"operation",value:""},
            {type:"input",hidden:true,name:"page",value:"DDT"},
           {type:"fieldset",name:"responsefs",label:"Response Time",width:"95%",list:[
            {type:"block",name:"respblock",blockOffset:0,list:[
              {type:"select",name:"response_time",label:"<a href='javascript:openHelpWindow(\"#ResponseTime\")'>Response Time:</a>" ,className:"reqLbl",position:"label-left", options:[
                 {value:"",text:"",checked:true},
                 {value:"NON-TRANSIENT",text:"Non-Transient"},
                 {value:"SLOW",text:"Slow >30 days"},
                 {value:"MEDIUM",text:"Medium: > 5 days and <= 30 days"},
                 {value:"FAST",text:"Fast: <= 5 days"},
               ]},
              {type:"newcolumn",offset:20},
              {type:"container",name:"responsemsg",offsetTop:10,className:"statusContainer"},
               ]},
               {type:"input",name:"response_justification",label:"<a style='font-weight:normal;' href='javascript:openHelpWindow(\"#ResponseTime\")'>Justification of Response Time:</a> <span class='lblclass'>(400 chars)</span>",maxLength:400,rows:3,inputWidth:inW,position:"label-top"},

           {type:"input",name:"contact_info",label:"<a style='font-weight:normal;' href='javascript:openHelpWindow(\"#ContactInfo\")'>24 Hour Contact Information:</a><span class='lblclass'> (255 chars)</span>",maxLength:255,inputWidth:inW,rows:2,position:"label-top"}
             ]},
           {type:"fieldset",name:"const",label:"Justification ",width:"95%",list:[
               {type:"input",name:"target_justification",label:"<a style='font-weight:normal;' href='javascript:openHelpWindow(\"#JustTarget\")'>Justification for this Particular Target:</a> <span class='lblclass'>(200 chars)</span>",maxLength:200,inputWidth:inW,rows:2,position:"label-top"},
               {type:"input",name:"next_cfp",label:"<a style='font-weight:normal;' href='javascript:openHelpWindow(\"#WhyNotCfP\")'>Why can't the observation wait for the next Chandra CfP:</a> <span class='lblclass'>(400 chars)</span>",rows:3,maxLength:400,inputWidth:inW,position:"label-top"},
               {type:"input",name:"transient_behavior",label:"<a style='font-weight:normal;' href='javascript:openHelpWindow(\"#Transient Behavior\")'>If transient, likelihood of detection,duration and recurrence of transient behavior:</a> <span class='lblclass'>(300 chars)</span>",rows:3,maxLength:300,inputWidth:inW,position:"label-top"},
          ]},
          {type:"fieldset",name:"misc",label:"Miscellaneous ",width:"95%",list:[
            {type:"block",list:[
              {type:"label",name:"xmm_lbl",label:"<span class='lblclass'><a href='javascript:openHelpWindow(\"#XMMDDT\")'>Have you/Co-Is requested XMM DDT time for this/similar proposal?</a></span> "},
              {type:"newcolumn",offset:20},
              {type:"radio",name:"xmm_ddt",label:"Yes",value:"Y",position:"label-right",checked: false},
              {type:"newcolumn",offset:20},
              {type:"radio",name:"xmm_ddt",label:"No",value:"N",position:"label-right",checked: true},
            ]},
            {type:"block",name:"xmmblock",offsetLeft:20,list:[
              {type:"input",name:"xmm_status",label:"<a href='javascript:openHelpWindow(\"#XMMDDT\")'>Please indicate status of XMM request:</a>  <span class='lblclass'>(255 chars)</span>",maxLength:255,inputWidth:inW,position:"label-top"},
            ]},
            {type:"block",list:[
              {type:"label",name:"prev_lbl",label:"<a href='javascript:openHelpWindow(\"#CXCPrev\")'>Have you/Co-Is ever submitted similar/related proposals to a Chandra Call for Proposals (CfP)?</span> "},
              {type:"newcolumn",offset:20},
              {type:"radio",name:"prev_request",value:"Y",label:"Yes",position:"label-right",checked: false},
              {type:"newcolumn",offset:20},
              {type:"radio",name:"prev_request",value:"N",label:"No",position:"label-right",checked: true},
            ]},
            {type:"block",name:"prevblock",offsetLeft:20,list:[
               {type:"input",name:"prev_cycles",label:"<a href='javascript:openHelpWindow(\"#CXCPrev\")'>If Yes, please indicate cycles(s), PI name, status:</a> <span class='lblclass'>(255 chars)</span>",maxLength:255,inputWidth:inW,position:"label-top"},
            ]}
          ]},
   {type:"container",name:"status",className:"statusContainer"},
   {type:"block",offsetLeft:150,offsetTop:15,list:[
        {type:"button",name:"save",value:"Save Changes"},
        {type:"newcolumn"},
        {type:"button",name:"discard",value:"Discard Changes"},
        {type:"newcolumn"},
    ]},


];


         myForm = new dhtmlXForm("formB_container",formData);

         myForm.setSkin('dhx_skyblue');
         myForm.enableLiveValidation(true);

         myForm.attachEvent("onButtonClick",function(id){
            clear_status();
            if (id=="save") save_changes();
            else if (id=="discard") reset_fields();
         });
         myForm.attachEvent("onBeforeChange",function(id) {
         if (myForm.getItemValue("isEdit") == "false")
           return false;
         else
           return true;
         });

         myForm.attachEvent("onChange", handle_change);
         myForm.attachEvent("onBeforeChange", before_change);

         loadPage();
      }   
   </script>
</body>
</html>
