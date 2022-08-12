<%@ page session="true" import="java.util.*" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1
%>

<%
String  pid = (String)request.getParameter("id");
if (pid == null)  pid = "unknown";
%>
<%@ include file="cps_hdr.html" %>

   <script type="text/javascript">
      var myForm,formData,simbadgrid,valgrid,rcgrid;
      var pid = "<%= pid %>";
      var processMsg = "<span class='processLbl'><img src='/cps-app/imgs/ajax-loader.gif' alt='Loading..'></span>";

     function sort_span(a,b,order){
        var aval = a.split('>')[1];
        var bval = b.split('>')[1];
        if (order=="asc")
          return aval>bval?1:-1;
        else
          return aval<bval?1:-1;
     }

      function postduh()
      {
       if (myForm.getItemValue("operation").indexOf("val") >= 0)
         valgrid.adjustColumnSize(3);
       else if (myForm.getItemValue("operation").indexOf("sim") >= 0)
         simbadgrid.adjustColumnSize(6);
      }

      function handle_change(id) {

         pid = myForm.getItemValue("pid");
         var url = "/cps-app/cps_review?pid=" + pid + "&operation=" + id;
           if (id == "ready") {
            var isEdit = myForm.getItemValue("isEdit");
            myForm.getContainer("ddtmsg").innerHTML="";
            setChanges(1);
            if (isEdit != "true") {
              myForm.disableItem("save");
            }
            else if (myForm.isItemChecked(id)) {
              myForm.setItemLabel("save","Submit Proposal");
              if ((myForm.getItemValue("isDDT") == "true") && 
                  (myForm.getItemValue("response_time") != null )) {
                var respTime=myForm.getItemValue("response_time").toLowerCase();
                if (respTime.indexOf("med") >= 0 || respTime.indexOf("fast") >= 0) {
                  myForm.getContainer("ddtmsg").innerHTML="<span class='errmsgb'>Note:</span>Submitting DDT proposals sends texts and alarms to CXC staff: Please time your submission appropriately."
                }
              }
            }
            else {
              myForm.setItemLabel("save","Save");
            }
         } else {
           setChanges(1);
         }
         clear_status(); 
      }

      function postLoad2() {
        var propno = myForm.getItemValue("propno");
        if (propno.indexOf("Invalid") >= 0) {
           doAlertLogoff(myForm.getItemValue("emsg"));
           return;
        }
        if (propno.indexOf("Error") >= 0) {
          var emsg = myForm.getItemValue("emsg");
          doAlert(emsg,badalertReturn);
          return;
        }

        set_status(myForm.getItemValue("emsg")); 
      }
  

      function postLoad() {
        var pno = myForm.getItemValue("propno");
        var pid = myForm.getItemValue("pid");
        if (pno.indexOf("Invalid") >= 0) {
          doAlertLogoff(myForm.getItemValue("emsg"));
          return;
        }
        if (pno.indexOf("Error") >= 0) {
          doAlert(myForm.getItemValue("emsg"),badalertReturn);
          return;
        }
 
        set_status(myForm.getItemValue("emsg")); 

        var pstat = myForm.getItemValue("pstatus");
        myForm.setItemValue("pstatus",getProposalStatus(pstat));
        handle_change("ready");
        handle_change("proposal_type");
        var fslbl = myForm.getItemValue("propno") + ": Submit  " + myForm.getItemValue("proposal_title");

        myForm.setItemLabel("submitfs",fslbl);
        cps_style_load();
        setChanges(0);

        if (myForm.getItemValue("isDDT") == "true") {
          myForm.hideItem("peerrev");
        }
        if (myForm.getItemValue("isEdit") != "true") {
          set_editButtons(myForm);
          myForm.disableItem("avail_peer_review","Y");
          myForm.disableItem("avail_peer_review","N");
          myForm.disableItem("avail_peer_review","Maybe");
        }
      }

      function loadPage() {
         clear_status();
         setChanges(0);  
         myForm.load("/cps-app/cps_loadprop?page=SUMMARY&pid="+pid,postLoad);
      }

      function save_changes(id) {
        setChanges(0);  
        if(myForm.isItemChecked("ready")) {
          var msg="";
          if (myForm.getItemValue("isDDT") == "true") 
            msg = "Are you sure you want to submit this DDT Proposal?\nYou will not be allowed to edit the proposal once it is submitted.";
          else
            msg = "Please confirm that you have finished editing this proposal and you wish to submit it to the CXC.";
          doConfirm(msg,ansConfirm); 
        }
        else {
          msg= "This will only Save the current data. It will NOT SUBMIT the proposal.";
          doAlert(msg,save_data);
        }
        return;
      }
        
      function save_data() 
      {
        myForm.setItemValue("operation","SAVE");
        myForm.disableItem("save");
        document.body.className = "waiting";
        myForm.send("/cps-app/cps_review","post",function(name,retstr) {
            document.body.className = "";
            if (process_save(retstr,false)) {
              if(myForm.isItemChecked("ready")) {
                set_status(SAVE_OK + "  Proposal has been submitted."); 
                var id = myForm.getItemValue("pid");
                var tb = parent.mainTabbar.getIndexById(id);
                if (tb != null) {
                  parent.mainTabbar.deleteChildItems(id);
                  parent.mainTabbar.deleteItem(id);
                }
                parent.mainTabbar.selectItem("m1",true,false);
              }
              else {
                myForm.enableItem("save");
              }
              setChanges(0);
            } else {
              if (retstr.indexOf("Validation") >=0) {
                myForm.enableItem("save");
                myForm.uncheckItem("ready");
                handle_change("ready");
                myForm.setItemValue("valgrid","1");
                handle_change("valgrid");
                set_status("<span class='errlbl'>" + SAVE_FAILED +  retstr + "</span>");
              }
            } 
          }
        );
      }

      function ansConfirm(result) {
        if(!result) {
          myForm.setItemValue("ready","0");
          set_status("Save cancelled.");
          return;
        }
        else {
          save_data();
        }
      }

      function doOnLoad() {

         formData = [
           {type:"input",hidden:true,name:"operation",value:""},
           {type:"input",hidden:true,name:"page",value:"SUMMARY"},
           {type:"input",hidden:true,name:"pid",value:"<%=pid%>"},
           {type:"input",hidden:true,name:"propno"},
           {type:"input",hidden:true,name:"proposal_title"},
           {type:"input",hidden:true,name:"emsg",value:""},
           {type:"input",hidden:true,name:"retstr",value:""},
           {type:"input",hidden:true,name:"isEdit",value:"false"},
           {type:"input",hidden:true,name:"isDDT",value:"false"},
           {type:"input",hidden:true,name:"response_time",value:""},

      {type:"fieldset",name:"submitfs",label:"Submit",list:[
      {type:"block",name:"peerrev",list:[
        {type:"label",name:"reviewer",label:"<a href='javascript:openHelpWindow(\"#PeerReview\")'>Would the P.I. be willing to participate in an upcoming Chandra peer review?</a>" },
        {type:"newcolumn",offset:20},
        {type:"radio",name:"avail_peer_review",value:"Y",label:"Yes",checked: false,position:"label-right"},
        {type:"newcolumn",offset:20},
        {type:"radio",name:"avail_peer_review",value:"N",label:"No",checked: true,position:"label-right"},
        {type:"newcolumn",offset:20},
        {type:"radio",name:"avail_peer_review",value:"Maybe",label:"Maybe",checked: false,position:"label-right"}
      ]},
      {type:"block",list:[
        {type:"checkbox",name:"ready",position:"label-right",className:"reqLbl",label:"&nbsp;<a href='javascript:openHelpWindow(\"#Submit\")'>I have reviewed my application and believe it to be ready for submission.</a>",check:false },
       {type:"block",list:[
        {type:"container",name:"ddtmsg",className:"statusContainer"},
        {type:"button",name:"save",value:"Save"},
        ]},
      ]},
    ]},
    {type:"container",name:"status",className:"statusContainer"},


];
         myForm = new dhtmlXForm("formB_container",formData);
         myForm.setSkin('dhx_skyblue');

         // cps_style_init();
         buildTooltips(myForm);

         myForm.attachEvent("onChange", handle_change);
         myForm.attachEvent("onButtonClick",function(id){
            clear_status();
            if (id=="save") save_changes(id);
         });

         loadPage();
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
</body>
</html>
