var myForm;

function ValidNRAO(data) {
  return validate_range(myForm,"nrao_time");
}
function ValidNOAO(data) {
  return validate_range(myForm,"noao_time");
}
function ValidHST(data) {
  return validate_irange(myForm,"hst_time");
}
function ValidSWIFT(data) {
  return validate_range(myForm,"swift_time");
}
function ValidXMM(data) {
  return validate_range(myForm,"xmm_time");
}
function ValidNUSTAR(data) {
  return validate_range(myForm,"nustar_time");
}
function clear_errors() {
  var lbls = new Array("hst_time","hst_instr","xmm_time","swift_time",
	"nustar_time","noao_time","noao_instr","nrao_time","nrao_instr");
  //reset error labels
  for (var ii=0;ii<lbls.length;ii++) {
    set_errlbl(false,myForm,lbls[ii]);
  }
  clear_status();
}

function validate_joint() {
  var retval=true;
  var str = "";

  clear_errors();
  if (!validate_irange(myForm,"hst_time"))
    str += "Invalid range for HST orbits. " + get_range("hst_time") + "<br>";
  if (!validate_dependencies(myForm,["hst_time","hst_instr"]))
    str += "FOR HST, both HST orbits and instrument required<br>";
  if (!validate_range(myForm,"xmm_time"))
    str += "Invalid range for XMM time.  " + get_range("xmm_time") + "<br>";
  if (!validate_range(myForm,"nustar_time"))
    str += "Invalid range for NuSTAR time.  " + get_range("nustar_time") + "<br>";
  if (!validate_range(myForm,"swift_time"))
    str += "Invalid range for Swift time.  " + get_range("swift_time") + "<br>";
  if (!validate_range(myForm,"noao_time"))
    str += "Invalid range for NOAO time.  " + get_range("noao_time") + "<br>";
  if (!validate_dependencies(myForm,["noao_time","noao_instr"]))
    str += "FOR NOAO, both NOAO nights and instrument required<br>";
  if (!validate_range(myForm,"nrao_time"))
    str += "Invalid range for NRAO time.  " + get_range("nrao_time") + "<br>";
  if (!validate_dependencies(myForm,["nrao_time","nrao_instr"]))
    str += "FOR NRAO, both NRAO hours and instrument required<br>";
   
  if (str.length > 1) {
    set_status("<span class='errlbl'>" + SAVE_FAILED +  str + "</span>");
    retval=false;
  }
  return retval;

}

function postLoad() {
  var propno=myForm.getItemValue("propno");
  var emsg=myForm.getItemValue("emsg");
  if (emsg != null && emsg.length > 2) {
    if (propno.indexOf("Invalid") >= 0)  {
      doAlertLogoff(emsg);
      return;
     } else {
      doAlert(emsg,badalertReturn);
    }
  }
  else {
    var ptype= myForm.getItemValue("proptype"); 
    if (ptype.indexOf("ARC") < 0 && ptype.indexOf("GTO") < 0) 
      myForm.setItemLabel("joint_fs",propno + ": Joint Observations  "  );
    else
      myForm.setItemLabel("joint_fs",propno + ": Joint Observations  " + myForm.getItemValue("proposal_title") );
    myForm.setItemLabel("joint_reviewfs",propno + ": Joint Review  " + myForm.getItemValue("proposal_title"));

    var jstr = myForm.getItemValue("joint");
    if (jstr.indexOf("CXO-") >= 0) {
      myForm.setItemValue("joint_type",jstr);
    }  else {
      myForm.setItemValue("joint_type","No");
    }

    if (ptype.indexOf("ARC") >= 0) {
      //hide everything but NOAO
      myForm.hideItem("joint_reviewfs");
      myForm.hideItem("hst_time");
      myForm.hideItem("hst_instr");
      myForm.hideItem("nrao_time");
      myForm.hideItem("nrao_instr");
      myForm.hideItem("xmm_time");
      myForm.hideItem("swift_time");
      myForm.hideItem("nustar_time");
      myForm.hideItem("hst_instrument");
      var lbl=myForm.getItemLabel("instlbl");
      lbl += " Archive proposals may only request joint time from NOAO.";
      myForm.setItemLabel("instlbl",lbl);
    }
    else if (ptype.indexOf("GTO") >= 0) {
      myForm.hideItem("joint_reviewfs");
    }
    handle_change("joint_type",myForm.getItemValue("joint_type"),null,DONOTCLEARSTATUS);
  }
  var isEdit=myForm.getItemValue("isEdit");
  set_editButtons(myForm,isEdit);
  cps_style_load();
  setChanges(0);

}

function loadPage() {
  //dhtmlxError.catchError("ALL", error_handler); deprecated
  myForm.setItemValue("operation","LOAD");
  myForm.load("/cps-app/cps_loadprop?page=JOINT&pid="+pid,postLoad);
}

function save_changes() {
  myForm.setItemValue("operation","SAVE");
  if (validate_joint()) {
    myForm.disableItem("save");
    document.body.className = "waiting";
    myForm.send("/cps-app/cps_saveprop","post",function(name,retstr) {
      document.body.className = "";
      myForm.enableItem("save");
      if (process_save(retstr,false))
         setChanges(0);
    });
  }
}

function handle_change(id,val,state,test) {
  if (!test || test.indexOf(DONOTCLEARSTATUS) < 0)
    clear_success_status();

   if (id == "joint_type") {
     if (val == "No") {
       myForm.showItem("joint_fs");
     }
     else {
       var chkarr = new Array("hst_time","hst_instr","xmm_time","swift_time",
        "nustar_time","noao_time","noao_instr","nrao_time","nrao_instr");
       if (check_fields(myForm,chkarr) == 0)
         myForm.hideItem("joint_fs");
       else
         myForm.showItem("joint_fs");
     }
   }
   setChanges(1);
}


function confirmResult(result)
{
  if (result) {
    var newval = myForm.getUserData("joint_type","newval");
    myForm.setItemValue("joint_type",newval);
    clear_errors();
    var chkarr = new Array("hst_time","hst_instr","xmm_time","swift_time",
	     "nustar_time","noao_time","noao_instr","nrao_time","nrao_instr");
    for (var ii=0;ii<chkarr.length;ii++) {
      myForm.setItemValue(chkarr[ii],"");
    }
    myForm.hideItem("joint_fs");
    setChanges(1);
  }
}

function before_change(id,oval,newval) {
   var rval = myForm.getItemValue(id);
   if (id == "joint_type") {
     if (rval == "No") {
     }
     else {
       myForm.setUserData(id,"newval",newval);
       var dd=myForm.getSelect(id);
       var tstr=dd.options[dd.selectedIndex].text;
       var msg = "Are you sure this proposal has already been approved at the " + tstr + " review?"; 
       doConfirm(msg,confirmResult);
       return false;
     }
   }
   //setChanges(1);
   return true;
}
   
function clear_fields() {
  myForm.clear();
  clear_select(myForm,"nrao_instr");
  clear_status();
  setChanges(1);  
}

function reset_fields() {
  clear_errors();
  loadPage();
}


function doOnLoad() {

        var formData = [
           {type:"input",hidden:true,name:"operation",value:""},
           {type:"input",hidden:true,name:"page",value:"JOINT"},
           {type:"input",hidden:true,name:"pid"},
           {type:"input",hidden:true,name:"propno",value:"0"},
           {type:"input",hidden:true,name:"proposal_title"},
           {type:"input",hidden:true,name:"proptype"},
           {type:"input",hidden:true,name:"emsg"},
           {type:"input",hidden:true,name:"joint"},
           {type:"input",hidden:true,name:"isEdit",value:"false"},
           {type:"input",hidden:true,name:"isDDT",value:"false"},

           {type:"fieldset",name:"joint_reviewfs",label:"Joint Review",width:"95%",list:[
             {type:"block",list:[
               {type:"select",name:"joint_type",label:"<a href='javascript:openHelpWindow(\"#JointApproved\")'>Has this proposal already been approved by the HST, NRAO or XMM review and allocated Chandra time?</a>" ,position:"label-left", options:[ 
	         {value:"No",text:"No",checked:true},
	         {value:"CXO-HST",text:"HST"},
	         {value:"CXO-NRAO",text:"NRAO"},
	         {value:"CXO-XMM",text:"XMM"},
	       ]},
	     ]},
	   ]},
           {type:"fieldset",name:"joint_fs",label:"Joint Observations",width:"95%",list:[
             {type:"label",name:"instlbl",label:"The Joint Proposal section should ONLY be filled out if this is a joint proposal which also requests time on one/more of the facilities listed below. To request Joint time through the Chandra Review, please enter the joint proposal parameters."},
             {type:"block",list:[
               {type:"input",name:"hst_time",label:"<a href='javascript:openHelpWindow(\"#HSTTime\")'>HST Orbits:</a>",position:"label-left", labelWidth:120,inputWidth:50,maxLength:10,validate:"ValidHST"},
               {type:"newcolumn",offset:20},
               {type:"input",name:"hst_instr",label:"<a href='javascript:openHelpWindow(\"#HSTInstrument\")'>Instruments:</a>",position:"label-left", labelWidth:"auto",inputWidth:300,maxLength:60 }
             ]},
             {type:"block",list:[
               {type:"input",name:"xmm_time",label:"<a href='javascript:openHelpWindow(\"#XMMTime\")'>XMM (ksec):</a>",position:"label-left", labelWidth:120,inputWidth:50,maxLength:10,validate:"ValidXMM"},
               {type:"input",name:"swift_time",label:"<a href='javascript:openHelpWindow(\"#SwiftTime\")'>Swift (ksec):</a>",position:"label-left", labelWidth:120,inputWidth:50,maxLength:10,validate:"ValidSWIFT"},
               {type:"input",name:"nustar_time",label:"<a href='javascript:openHelpWindow(\"#NustarTime\")'>NuSTAR (ksec):</a>",position:"label-left", labelWidth:120,inputWidth:50,maxLength:10,validate:"ValidNUSTAR"}
             ]},
             {type:"block",list:[
               {type:"input",name:"noao_time",label:"<a href='javascript:openHelpWindow(\"#NOAOTime\")'>NOAO Nights:</a>",position:"label-left", labelWidth:120, inputWidth:50,validate:"ValidNOAO",maxLength:10},
               {type:"newcolumn",offset:20},
               {type:"input",name:"noao_instr",label:"<a href='javascript:openHelpWindow(\"#NOAOInstrument\")'>Instruments:</a>",position:"label-left", labelWidth:"auto",inputWidth:300,maxLength:60 }
             ]},
             {type:"block",list:[
               {type:"input",name:"nrao_time",label:"<a href='javascript:openHelpWindow(\"#NRAOTime\")'>NRAO Hours:</a>",position:"label-left", labelWidth:120, inputWidth:50,validate:"ValidNRAO",maxLength:10},
               {type:"newcolumn",offset:20},
               {type:"multiselect",name:"nrao_instr",label:"<a href='javascript:openHelpWindow(\"#NRAOInstrument\")'>Telescopes:</a>",position:"label-left", labelWidth:"auto", options:[
	          {value:"",text:" ",checked:true},
	          {value:"GBT",text:"GBT"},
	          {value:"VLA",text:"VLA"},
	          {value:"VLBA",text:"VLBA"}
               ]}  
             ]}
           ]},
           {type:"container",name:"status",className:"statusContainer"},
           {type:"block",offsetLeft:150,offsetTop:15,list:[
             {type:"button",name:"save",value:"Save Changes"},
             {type:"newcolumn"},
             {type:"button",name:"discard",value:"Discard Changes"}
         ]}
        ];


        myForm = new dhtmlXForm("formB_container",formData);
        //cps_style_init();

        myForm.setSkin('dhx_skyblue');
        myForm.setItemValue("pid",pid);
        myForm.enableLiveValidation(true);
        buildTooltips(myForm);
        myForm.attachEvent("onButtonClick",function(id){
           clear_status();
           if (id=="save") save_changes();
           else if (id=="discard") reset_fields();
           else if (id=="clear") clear_fields();
        });

        myForm.attachEvent("onChange", handle_change);
        myForm.attachEvent("onBeforeChange", before_change);
        myForm.attachEvent("onValidateError", handle_error);
        myForm.attachEvent("onAfterValidate", after_validate);

        loadPage();
}   
