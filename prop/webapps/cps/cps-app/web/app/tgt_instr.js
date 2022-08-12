var myForm;
var old_detector;
var lbls = new Array("est_cnt_rate","forder_cnt_rate","total_fld_cnt_rate");

function ValidCR(data)
{
  retval = true;
  if (data && data.toString().trim().length >0) {
    var x =parseFloat(data) ;
    if (isNaN(x) || x != data.toString().trim())
      retval = false;
    else if (x < 0 || x > 10000)
      retval = false;
  }
  return retval;
}
function clear_errors()
{
  //reset error labels
  for (var ii=0;ii<lbls.length;ii++) {
    set_errlbl(false,myForm,lbls[ii]);
  }
  clear_status();
}

function validate_instr() {
  var retval=true;
  var str = "";
  var reqitems = new Array("est_cnt_rate");

  clear_errors();

  if (myForm.getItemValue("grating") != "NONE") {
    reqitems.push("forder_cnt_rate");
  } else {
    if (myForm.getItemValue("forder_cnt_rate") != null && 
        myForm.getItemValue("forder_cnt_rate").trim().length > 0) {
      str += myForm.getItemLabel("forder_cnt_rate") + " is not allowed for Grating=NONE. <br>";
      set_errlbl(true,myForm,"forder_cnt_rate");
    }
  }
  for (var ii=0;ii<reqitems.length;ii++) {
    val = myForm.getItemValue(reqitems[ii]);
    if (val==null || val.toString().trim() == "") {
      arr = myForm.getItemLabel(reqitems[ii]).split("<br");
      str += "Missing required field: "  + arr[0] + "<br>";
      set_errlbl(true,myForm,reqitems[ii]);
    }
  }
  for (var ii=0;ii<lbls.length;ii++) {
    if (!ValidCR(myForm.getItemValue(lbls[ii]))) {
      str += myForm.getItemLabel(lbls[ii]) + " is invalid. <br>";
      set_errlbl(true,myForm,lbls[ii]);
    } 
  }
  if (str.length < 1) {
    try {
      var est_cr=parseFloat(myForm.getItemValue("est_cnt_rate"));
      var tot_cr=parseFloat(myForm.getItemValue("total_fld_cnt_rate"));
      if (est_cr> tot_cr ) {
        str += myForm.getItemLabel("total_fld_cnt_rate") + " must be >= " + myForm.getItemLabel("est_cnt_rate");
        set_errlbl(true,myForm,"total_fld_cnt_rate");
      } 
      
    }  catch(e) {
    }
    
  }
    

  if (str.length > 1)
  {
    set_status("<span class='errlbl'>" + SAVE_FAILED +  str + "</span>");
    retval=false;
  } 
  return retval;
}


function before_change(id,new_val,btnstate) {
  var retval=true;
  // for the radio buttons, it sends the new selection and state
  if (myForm.getItemValue("isEdit") == "false")
    retval= false;
  else {
    if (id=="detector") {
      if ((old_detector.indexOf("ACIS") >= 0) &&
          (new_val.indexOf("HRC") >= 0)) {
        myForm.setItemLabel("detwarn", "<span class='errmsg'>This will delete all existing ACIS parameters.</span>");
      }
      else {
        myForm.setItemLabel("detwarn", "");
      }
    }
  }
  return retval;
}

function acis_tabs() {
  if (parent.tgttabbar) {
    var val= myForm.getItemValue("detector");
    var tabele =  parent.tgttabbar.tabs("t4");
    var tabele2 =  parent.tgttabbar.tabs("t5");
    if (val == "HRC-I" || val == "HRC-S") {
      tabele.disable();
      tabele2.disable();
    } else   {
      tabele.enable();
      tabele2.enable();
    }
  }
}

function handle_change(id,val,state,test) {
  if (!test || test.indexOf(DONOTCLEARSTATUS) < 0)
    clear_success_status();

  if (id=="detector") {
    old_detector=val;
    if (val == "HRC-I" || val == "HRC-S") {
      myForm.showItem("hrcblk");
    } else {
      myForm.hideItem("hrcblk");
    }
  } else if (id=="grating") {
    chkarr = ["forder_cnt_rate"];
    if (val == "NONE" && !check_fields(myForm,chkarr)) {
      myForm.hideItem("cr1blk");
    } else {
      myForm.showItem("cr1blk");
    }
  }
  setChanges(1);
}


function save_changes() {
  myForm.setItemValue("operation","SAVE");
  if (validate_instr()) {
    myForm.disableItem("save");
    document.body.className = "waiting";
    myForm.send("/cps-app/cps_savetgt","post",function(name,retstr) {
        document.body.className = "";
        myForm.enableItem("save");
        if (process_save(retstr,false)) {
         acis_tabs();
         setChanges(0);
       }
     });
  }
}
  
function reset_fields() {
  clear_errors();
  loadPage();

} 

function postLoad() {
  var propno = myForm.getItemValue("propno");
  if (propno.indexOf("Invalid") >= 0) {
    top.location.replace("/cps-app/prop_logout.jsp");
  }
  if (propno.indexOf("Error") >= 0) {
    var emsg = myForm.getItemValue("emsg");
    doAlert(emsg,badalertReturn);
    return;
  }


  old_detector = myForm.getItemValue("detector");
  handle_change("detector",old_detector,null,DONOTCLEARSTATUS);
  handle_change("grating",myForm.getItemValue("grating"),null,DONOTCLEARSTATUS);
  var isEdit = myForm.getItemValue("isEdit");
  set_editButtons(myForm,isEdit);
  if (isEdit != "true") {
    myForm.disableItem("detector","ACIS-I");
    myForm.disableItem("detector","ACIS-S");
    myForm.disableItem("detector","HRC-I");
    myForm.disableItem("detector","HRC-S");
    myForm.disableItem("grating","HETG");
    myForm.disableItem("grating","LETG");
    myForm.disableItem("grating","NONE");
    myForm.disableItem("extended_src","Y");
    myForm.disableItem("extended_src","N");
    myForm.disableItem("hrcTimingMode","Y");
    myForm.disableItem("hrcTimingMode","N");
  }
  cps_style_load();     

  setChanges(0);
} 

function loadPage() {
  var url ="/cps-app/cps_loadtgt?page=INSTRUMENT&pid=" + pid + "&tid=" +tid;
  myForm.load(url,postLoad);

}

function doOnLoad() {
    var formData=[
          {type:"input",hidden:true,name:"operation",value:""},
           {type:"input",hidden:true,name:"isEdit",value:""},
           {type:"input",hidden:true,name:"pid",value:""},
           {type:"input",hidden:true,name:"tid",value:""},
           {type:"input",hidden:true,name:"page",value:"INSTRUMENT"},
           {type:"input",hidden:true,name:"tgtnbr",value:""},
           {type:"input",hidden:true,name:"propno",value:""},
           {type:"input",hidden:true,name:"emsg",value:""},

               {type:"fieldset",name:"name",label:"Instrument",list:[
               {type:"block",list:[
               {type:"label", name: "detectorLbl",label:"<a class='reqLbl' href='javascript:openHelpWindow(\"#Detector\")'>Detector:</a>" },
               {type:"newcolumn",offset:20},
               {type:"radio",name:"detector",value:"ACIS-I",label:"ACIS-I",position:"label-right"},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"detector",value:"ACIS-S",label:"ACIS-S",position:"label-right",checked: true},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"detector",value:"HRC-I",label:"HRC-I",position:"label-right"},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"detector",value:"HRC-S",label:"HRC-S",position:"label-right"},
               {type:"newcolumn",offset:20},
               {type:"label",name:"detwarn",value:""},
               ]},
               {type:"block",list:[
               {type:"label", name: "tgtgratlbl",label:"<a class='reqLbl' href='javascript:openHelpWindow(\"#Grating\")'>Grating:</a>" },
               {type:"newcolumn",offset:20},
               {type:"radio",name:"grating",value:"NONE",label:"None",position:"label-right",checked:true},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"grating",value:"HETG",label:"HETG",position:"label-right",checked: false},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"grating",value:"LETG",label:"LETG",position:"label-right",checked:false},
               {type:"newcolumn",offset:20}
               ]},
               {type:"block",name:"hrcblk",list:[
                 {type:"label", name: "tgtsuh",label:"<a href='javascript:openHelpWindow(\"#HRCTimingMode\")'>HRC Timing Mode:</a>" },
                 {type:"newcolumn",offset:20},
                 {type:"radio",name:"hrcTimingMode",label:"Yes",value:"Y",position:"label-right"},
                 {type:"newcolumn",offset:20},
                 {type:"radio",name:"hrcTimingMode",label:"No",value:"N",position:"label-right",checked:true},
               ]}
               ]},
              {type:"fieldset",name:"name2",label:"Count Rates",list:[
                {type:"label",label:"<span class='fldhelp'>All count rates are to be specified as observed (not pile-up corrected).</span>"},
                {type:"block",list:[
                  {type:"input", name: "est_cnt_rate",className:"reqLbl", label:"<a href='javascript:openHelpWindow(\"#CountRate\")'>Source Count Rate:</a>" ,labelWidth:150,inputWidth:100,validate:"ValidCR"},
                  {type:"newcolumn",offset:20},
                  {type:"label",className:'fldhelp',name: "cr_lbl",label: "<span class='fldhelp'>(c/s) For Gratings, this is the zero order count rate</span>"},
                 
                 ]},
                {type:"block",name:"cr1blk",list:[
                  {type:"input", name: "forder_cnt_rate", className:"reqLbl",label: "<a href='javascript:openHelpWindow(\"#OrderCountRate\")'>1st Order Count Rate:</a>" ,labelWidth:150,inputWidth:100,validate:"ValidCR"},
                  {type:"newcolumn",offset:20},
                  {type:"label",className:'fldhelp',name: "cr1_lbl",label: "<span class='fldhelp'>(c/s) Used only for Gratings.</span>"},
                 ]},
                {type:"block",list:[
                  {type:"input", name: "total_fld_cnt_rate", label: "<a href='javascript:openHelpWindow(\"#TotalFieldCountRate\")'>Total Field Count Rate:</a>" ,labelWidth:150,inputWidth:100,validate:"ValidCR"} ,
                  {type:"newcolumn",offset:20},
                  {type:"label",className:'fldhelp',name: "tfcr_lbl",label: "<span class='fldhelp'>(c/s)"},
                 ]},
               {type:"block",blockOffset:0,list:[
               {type:"block",list:[
                  {type:"label", name: "extsrc", label: "<a href='javascript:openHelpWindow(\"#ExtendedSource\")'>Is target an Extended Source?</a>" },
               {type:"newcolumn",offset:20},
               {type:"radio",name:"extended_src",value:"Y",label:"Yes",position:"label-left"},
               {type:"newcolumn",offset:10},
               {type:"radio",name:"extended_src",value:"N",label:"No",position:"label-left",checked:true},

               ]},
                  {type:"newcolumn",offset:20},
                  {type:"label",className:'fldhelp',labelWidth:300,name: "tfcr_lbl",label: "<span class='fldhelp'>Select <b>Yes</b> if most of the source count rate above comes fom extended emission."},
               ]},
               ]},
    {type:"container",name:"status",className:"statusContainer"},
    {type:"block",offsetLeft:150,offsetTop:15,list:[
        {type:"button",name:"save",value:"Save Changes"},
        {type:"newcolumn"},
        {type:"button",name:"discard",value:"Discard Changes"},
        {type:"newcolumn"}
    ]}
         
         ];


         myForm=new dhtmlXForm("formB_container",formData);

         myForm.setSkin('dhx_skyblue');
         myForm.attachEvent("onAfterValidate", after_validate);
         myForm.attachEvent("onBeforeChange",before_change);
         myForm.attachEvent("onChange",handle_change);
         myForm.attachEvent("onValidateError",handle_error);
         myForm.enableLiveValidation(true);
         myForm.hideItem("hrcblk");
         myForm.hideItem("cr1blk");
         myForm.attachEvent("onButtonClick",function(id){
            clear_status();
            if (id=="save") save_changes();
            else if (id=="discard") reset_fields();
         });
         loadPage();
}   
