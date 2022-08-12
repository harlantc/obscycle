var myForm;


function clear_errors() {
  var lbls = new Array("proposal_type","requested_budget","request_extra_flag",
	"category_descrip","proposal_title","scikeylbl","mprop",
        "lprop","linked_propnum","multi_cycle","rights_justification");
  //reset error labels
  for (var ii=0;ii<lbls.length;ii++) {
    set_errlbl(false,myForm,lbls[ii]);
  }
  clear_status();
}

function validate_proposal()
{
  var retval=true;
  var str = "";
  var val,val2;
  var reqitems = new Array("proposal_type", "request_extra_flag",
	"category_descrip","proposal_title");
  // dont require abstract until final submission
  if (myForm.getItemValue("request_extra_flag") != 'N' && 
      myForm.getItemValue("isDDT") == "true") {
     reqitems.push("rights_justification");
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

  ptype = myForm.getItemValue("proposal_type");
  val2 = myForm.getItemValue("requested_budget");
  if ((ptype.indexOf("ARCH") >=0) ||
      (ptype.indexOf("THEO") >=0)) {
    if (val2==null || val2.toString().trim() == "") { 
      str += "Missing required field: "  + myForm.getItemLabel("requested_budget") + "<br>";
      myForm.showItem("atsum");
      set_errlbl(true,myForm,"requested_budget");
    }
    else if (!validate_range(myForm,"requested_budget")) {
        str += myForm.getItemLabel("requested_budget") + " is invalid. " + get_range("requested_budget") + "<br>";
        myForm.showItem("atsum");
        set_errlbl(true,myForm,"requested_budget");
    }
    if (myForm.getItemValue("multi_cycle") == "Y") {
      str += myForm.getItemLabel("mprop") + " is not allowed for Archive/Theory Proposals.<br>";
      set_errlbl(true,myForm,"multi_cycle");
    }
    if (myForm.getItemValue("request_extra_flag") != "N") {
      str += myForm.getItemLabel("request_extra_flag") + " must be No for Archive/Theory/VLP Proposals.<br>";
      set_errlbl(true,myForm,"request_extra_flag");
    }
    
  } else {
    if (val2!=null && val2.toString().trim().length > 0) {
      str += "Requested budget not allowed for proposal type of "  + ptype + "<br>";
      myForm.showItem("atsum");
      set_errlbl(true,myForm,reqitems[ii]);
    }
    if (ptype.indexOf("GTO") >= 0 || ptype.indexOf("DDT") >= 0) {
      if (myForm.getItemValue("multi_cycle") == "Y") {
        str += myForm.getItemLabel("mprop") + " is not allowed for " + ptype + " Proposals.<br>";
        set_errlbl(true,myForm,"multi_cycle");
      }
    }
    if (ptype.indexOf("CAL") >= 0 ) {
      if (myForm.getItemValue("request_extra_flag") != "N") {
        str += myForm.getItemLabel("request_extra_flag") + " must be No for Calibration Proposals.<br>";
        set_errlbl(true,myForm,"request_extra_flag");
      }
      if (myForm.getItemValue("multi_cycle") == "Y") {
        str += myForm.getItemLabel("mprop") + " is not allowed for CAL Proposals.<br>";
        set_errlbl(true,myForm,"multi_cycle");
      }
    }
  }

/*
  if (!ValidScikey()) {
      set_errlbl(true,myForm,"scikeyResults");
      str += "At least 4 Science Keywords are required." + "<br>";
  }
  else  {
      set_errlbl(false,myForm,"scikeyResults");
  }
*/

  // don't care about linked proposals, they may or may not know number

  if (str.length > 1) {
    str += process_warnings();
    set_status("<span class='errlbl'>" + SAVE_FAILED +  str + "</span>");
    retval=false;
  }
  
  return retval;
}
   

function ValidScikey(data)
{
  // now just a warning if any entered
  var retval=true;
  var data=myForm.getItemValue("scikeyResults");
  var arr = data.split(";");
  if (arr.length < 4) {
    return false;
  } 
  return retval;
}


function before_change(id, old_val,new_val) 
{
  var retval = true;
  var confirmTxt=""
  var reqXtra= myForm.getItemValue("request_extra_flag");
  if (id=="proposal_type") {
    if ((new_val == "ARCHIVE" || new_val == "THEORY" || new_val == "VLP") &&
        (old_val != "ARCHIVE" && old_val != "THEORY" && old_val != "VLP")) {
      if (new_val != "VLP") {
        confirmTxt= "After \"Save\", changing the Proposal Type  to <b>" + new_val + "</b> will <b>delete any existing targets</b> for this proposal.  Are you sure you want to change the proposal type?";
        }
      reqXtra="N"
    }
    else if (new_val.indexOf("TOO") < 0 && old_val.indexOf("TOO") >= 0)  {
      confirmTxt= "After \"Save\", changing the Proposal Type  to <b>" + new_val + "</b> will <b>delete any existing TOO details</b> for this proposal.  Are you sure you want to change the proposal type?";
    }
    else if ((old_val == "ARCHIVE" || old_val == "THEORY" || old_val == "VLP") &&
             (new_val != "ARCHIVE" && new_val != "THEORY" && new_val == "VLP")) {
      reqXtra=" "
    }
    else if (old_val == "CAL" && new_val != "CAL" && myForm.getItemValue("isCfP") == "false") {
        confirmTxt="Are you sure this is not a Calibration proposal?<br>  You may no longer be able to edit the proposal after this is changed.  Contact the CXC HelpDesk with any questions.";
    }
  }
  else if (id=="multi_cycle") {
    if (old_val == "N" && new_val === true)
      confirmTxt= "After 'Save', this will clear any Projected Time in future cycles for all targets. Are you sure you want to change <b>Is this a Multi-Cycle proposal</b> to No?";
  }
  if (myForm.getItemValue("isEdit") == "false") 
    return false;
  else {
    if (confirmTxt.length > 2) {
      myForm.setUserData("propno","id",id);
      myForm.setUserData("propno","newval",new_val);
      myForm.setUserData("propno","xtra",reqXtra);
      doConfirm(confirmTxt,confirmMultiCycle);
      return false;
    } else  {
     return retval;
    }
  }
}
function confirmMultiCycle(result)
{
  if (result) {
      var id = myForm.getUserData("propno","id");
      var new_val = myForm.getUserData("propno","newval");
      var xtra = myForm.getUserData("propno","xtra");
      if (id == "multi_cycle") new_val="N";
      myForm.setItemValue(id,new_val);
      myForm.setItemValue("request_extra_flag",xtra);
      set_errlbl(false,myForm,"request_extra_flag");
      if (id == "proposal_type" && new_val != "CAL") {
        type_change(new_val);
        //myForm.enableItem("request_extra_flag");
      }
      setChanges(1);
  }
  return result;
}
         
function linked_block(ptype) {
  if (ptype.indexOf("CAL") >= 0 || ptype.indexOf("DDT") >= 0 ) {
     myForm.hideItem("lpropblk");
     myForm.hideItem("lblock");
     myForm.setItemValue("linked_propnum","");
     myForm.setItemValue("linked_proposal","N");
  } else {
     myForm.showItem("lpropblk");
  }
}
function multi_cycle_block(ptype) {
  if (ptype.indexOf("ARC") >= 0 || ptype.indexOf("THE") >= 0 ||
      ptype.indexOf("CAL") >= 0 ||
      ptype.indexOf("GTO")>=0 || ptype.indexOf("DDT")>=0) {
      myForm.hideItem("multicycleblk");
      myForm.setItemValue("multi_cycle","N");
   } else {
      myForm.showItem("multicycleblk");
   }
}

function handle_change(id, val,state,test) 
{
  if (!test || test.indexOf(DONOTCLEARSTATUS) < 0) 
    clear_success_status();
  if (id=="linked_proposal") {
    if (val =="Y") {
      myForm.showItem("lblock");
    } else {
      myForm.hideItem("lblock");
      myForm.setItemValue("linked_propnum","");
    }
  }
  else if (id=="proposal_type") {
    multi_cycle_block(val);
    linked_block(val);
    type_change(val);
  } else if (id=="requested_budget") {
    validRange(myForm,id);
  } else if (id=="request_extra_flag") {
    var robj = myForm.getContainer("rightshelp");
    var ptype=myForm.getItemValue("proposal_type");
    robj.innerHTML=" ";

    if (myForm.getItemValue("isDDT") == "true") {
      if (val == "N") {
        var xval = myForm.getItemValue("rights_justification");
        if (!xval || xval.trim() == "")
          myForm.hideItem("rights_justification");
        else
          myForm.showItem("rights_justification");
      } else  {
          myForm.showItem("rights_justification");
      }
    }
    else if (ptype.indexOf("THEORY") < 0 && ptype.indexOf("ARC") < 0 &&  
             ptype.indexOf("VLP") < 0 && val != "S" ) {
        robj.innerHTML=" Non-standard Proprietary Rights selected.";
    }
  }
      
  setChanges(1);
}

function type_change(val) 
{
    if (val == "ARCHIVE" || val =="THEORY" || val =="VLP") {
      if (val != "VLP"){
        myForm.showItem("atsum");
      }
      myForm.setItemValue("request_extra_flag","N");
      myForm.disableItem("request_extra_flag");
    }
    else {
      var data_rights = myForm.getItemValue("request_extra_flag");
      myForm.hideItem("atsum");
      myForm.setItemValue("requested_budget","");
      if (val != "CAL") {
        myForm.enableItem("request_extra_flag");
        if (val.indexOf("DDT") < 0)  {
          if (data_rights == null || data_rights == "N")
            myForm.setItemValue("request_extra_flag","S");
        } 
      } else {
        myForm.disableItem("request_extra_flag");
        myForm.setItemValue("request_extra_flag","N");
        myForm.setItemValue("rights_justification","");
        myForm.hideItem("rights_justification");
      }
    }
}

function postXML()
{
    myTree.setItemText("myroot2","Keywords - Please select 4 or more keywords.","Click to expand list.");

    loadPage();
}


function postLoad()
{
  var propno = myForm.getItemValue("propno");
  var ptitle = myForm.getItemValue("proposal_title");
  var ptype = myForm.getItemValue("proposal_type");
  var mypid = myForm.getItemValue("pid");
  var prights = myForm.getItemValue("request_extra_flag");
  var emsg = myForm.getItemValue("emsg");
  var ptype = myForm.getItemValue("proposal_type");


  if (propno.indexOf("Invalid") >= 0) {
    doAlertLogoff(emsg);
  }
  if (emsg != null && emsg.length > 1){
    doAlert(emsg,badalertReturn);   
  }
  else {
    // just in case proposal number changed
    if (parent.mainTabbar) parent.mainTabbar.setItemText(mypid,propno,ptitle);

    var tgtid = mypid + "TargetManage";
    var jointid = mypid + "Joint";
    multi_cycle_block(ptype) ;

    if (ptype.indexOf("ARC") >= 0 || ptype.indexOf("THE") >= 0 ||
        ptype.indexOf("VLP") >= 0) {
      if (prights == "" || prights==" " )  
         prights="N";
      if (ptype.indexOf("VLP") < 0){
        // delete the Targets navigation menu
        if (parent.mainTabbar) {
          if (parent.mainTabbar.getIndexById(tgtid) > 0)
            parent.mainTabbar.deleteItem(tgtid,false);
          if (ptype.indexOf("ARC") >= 0 ) {
            // if archive, make sure Joint navigation menu
            if (parent.mainTabbar.getIndexById(jointid) <= 0) {
              var coitid = mypid + "CoI";
              parent.mainTabbar.insertNewNext(coitid,jointid,"Joint Time Requests");
            }
          } else if (ptype.indexOf("THE") >= 0 ) {
            // if theory, remove the Joint
            if (parent.mainTabbar.getIndexById(jointid) > 0) {
              parent.mainTabbar.deleteItem(jointid,false);
            }
          }
        }
        myForm.disableItem("request_extra_flag");
      }
   } else {
       if (ptype.indexOf("CAL") < 0)  {
        if (prights == "" || prights == " ")  {
          if (myForm.getItemValue("isDDT") == "true") 
            prights="N";
          else  {
            prights="S";
          }
        }
      } else {
        if (prights == "" || prights == " ")  {
          prights="N";
        }
      }
      // Joint except if CAL or DDT
      if (parent.mainTabbar) {
      if (ptype.indexOf("CAL") >= 0 || ptype.indexOf("DDT") >=0)  {
         if (parent.mainTabbar.getIndexById(jointid) > 0)
           parent.mainTabbar.deleteItem(jointid,false);
      } else if (parent.mainTabbar.getIndexById(jointid) <= 0) {
          var coitid = mypid + "CoI";
          parent.mainTabbar.insertNewNext(coitid,jointid,"Joint Time Requests");
      }
      if (parent.mainTabbar.getIndexById(tgtid) == null ) {
        var upid=mypid + "Upload";
        var treeidx = parent.mainTabbar.getIndexById(upid);
        if (treeidx > 0) {
          treeidx = treeidx-1;
          nextid = parent.mainTabbar.getItemIdByIndex(mypid,treeidx);
          parent.mainTabbar.insertNewNext(nextid,tgtid,"Targets");
        }
      }
      }
    }
    
    if (myForm.getItemValue("isDDT") == "false") {
      myForm.hideItem("rightsblk");
    }  else {
      myForm.hideItem("lpropblk");
      // change abstract label
      myForm.setItemLabel("abstract","<a href='javascript:openHelpWindow(\"#Abstract\")'>Science Justification:</a><br><span class='lblclass'>(1000 chars.)</span>");
      myForm.getInput("abstract").maxLength=1000;
    }
  
    handle_change("proposal_type",myForm.getItemValue("proposal_type"),null,DONOTCLEARSTATUS);
    handle_change("linked_proposal",myForm.getItemValue("linked_proposal"),null,DONOTCLEARSTATUS);
    myForm.setItemLabel("propfs",myForm.getItemValue("propno") + ": Cover Page  " );
    myForm.setItemValue("request_extra_flag",prights);
    handle_change("request_extra_flag",myForm.getItemValue("request_extra_flag"),null,DONOTCLEARSTATUS);

    set_editButtons(myForm,myForm.getItemValue("isEdit"));
    var scikey = myForm.getItemValue("scikeyResults");
    var arr = scikey.split(";");
    for (var ii=0;ii<arr.length;ii++) {
      var str = myTree.getAllUnchecked();
      var items=str.split(',');
      for (var jj=0;jj<items.length;jj++) {
        var str = myTree.getItemText(items[jj]);
        if (str.toLowerCase() == arr[ii].toString().trim().toLowerCase()) {
          myTree.setCheck(items[jj],true);
          scikey_selected(items[jj]) ;
          break;
        }
      }
    }
    if (scikey.length < 1){
      myTree.openItem("myroot2");
    }   
   }
   if (myForm.getItemValue("isEdit") == "false")  {
     myTree.attachEvent("onBeforeCheck",function (id,state) {
        return false;
     });
     myForm.disableItem("linked_proposal","Y");
     myForm.disableItem("linked_proposal","N");
     myForm.disableItem("multi_cycle","Y");
     myForm.disableItem("multi_cycle","N");
   }

   setChanges(0);  
   cps_style_load();

   return true;
}

function loadPage() 
{
  clear_scikey();
  myForm.setItemValue("operation","LOAD");
  myForm.load("/cps-app/cps_loadprop?page=PROPOSAL&pid="+ pid,postLoad);
  return true;
}
function process_warnings() 
{
  var msg="";
  if (!ValidScikey()) {
    msg += "Warning: At least 4 Science Keywords are required." + "<br>";
  }
  var abstract=myForm.getItemValue("abstract");
  abstract.replace(/ /g,"");
  if (abstract.length < 1)
   msg += "Warning: Abstract is required<br>";
  
  return msg;
}

function save_changes() 
{
  if (validate_proposal()) {
    myForm.setItemValue("operation","SAVE");
    myForm.disableItem("save");
    document.body.className = "waiting";
    myForm.send("/cps-app/cps_saveprop","post",function(name,retstr) {
      document.body.className = "";
      myForm.enableItem("save");

      retstr += process_warnings();
      if (process_save(retstr,true))
        setChanges(0);
    });
  }
}

function clear_scikey() {
  var str=myTree.getAllChecked();
  var arr = str.split(",");
  for (var ii=0;ii<arr.length;ii++) {
    myTree.setCheck(arr[ii],false);
    var parent=myTree.getParentId(arr[ii]);
    myTree.setItemStyle(parent,"color:black;font-weight:normal;");
  }
  myTree.closeAllItems();
}
function scikey_clicked(id) {
  if (myTree.hasChildren(id)) { 
    if (myTree.getOpenState(id) == true)
      myTree.closeItem(id);
    else
      myTree.openItem(id);
    return;
  }
  if (myForm.getItemValue("isEdit") == "false") 
     return;

  if (myTree.isItemChecked(id))
    myTree.setCheck(id,false);
  else
    myTree.setCheck(id,true);
  scikey_checked(id);
  scikey_selected(id) ;
}


function scikey_checked(id) {
  var oldstr = myForm.getItemValue("scikeyResults");
  var selstr = "";
  //alert(myTree.getAttribute(id,"text"));
  var str=myTree.getAllChecked();
  //alert(str);
  var arr = str.split(",");
  for (var ii=0;ii<arr.length;ii++) {
    if (arr[ii].length < 1 || arr[ii].indexOf("_0") > 0 || arr[ii].indexOf("myroot") >= 0) {
    } else {
      if (selstr.length > 1) {
        selstr += ";";
      }
      selstr += myTree.getItemText(arr[ii]);
    }
  }
  if (selstr.length > 255) {
    doAlert("Science Keywords: Too many selections. Maximum of 255 characters.",alertReturn);
    myTree.setCheck(id,0);
  }
  //alert(selstr);
  if (oldstr != selstr) {
    myForm.setItemValue("scikeyResults",selstr);
    setChanges(1)
  }
}

function scikey_selected(w) {
  var parent=myTree.getParentId(w);
  if (myTree.isItemChecked(w) ) {
    if (parent) {
      myTree.setItemStyle(parent,"font-weight:bold;");
      //myTree.selectItem(parent);
    } 
  } else {
    if (parent) {
      var cnt=0;
      var chkstr= myTree.getSubItems(parent);
      var arr=chkstr.split(",");
      for (var ii=0;ii< arr.length;ii++) {
        if (myTree.isItemChecked(arr[ii])) cnt++;
      }
      if (cnt== 0) 
         myTree.setItemStyle(parent,"color:black;font-weight:normal;");
    }
  }
}


function reset_fields() {
 // myForm.reset();
  clear_errors();
  loadPage();

}

function before_load(id,values) {
  isDDT = values["isDDT"];
      
  buildPropType(myForm.getOptions("proposal_type"),isDDT);
  buildPropRights(myForm.getOptions("request_extra_flag"),isDDT);
  return true;
}


function doOnLoad() {
  var lblW=160;

  var formData = [ 
     {type:"settings",position:"label-left",labelWidth:"auto"},
       {type:"input",hidden:true,name:"propno",value:""},
       {type:"input",hidden:true,name:"pid",value:pid},
       {type:"input",hidden:true,name:"emsg",value:""},
       {type:"input",hidden:true,name:"operation",value:""},
       {type:"input",hidden:true,name:"page",value:"PROPOSAL"},
       {type:"input",hidden:true,name:"isEdit",value:"false"},
       {type:"input",hidden:true,name:"isCfP",value:"false"},
       {type:"input",hidden:true,name:"isDDT",value:"false"},
       {type:"fieldset",name:"propfs",label:"Proposal",width:"95%",list:[
         {type:"block",list:[
           {type:"select",name:"proposal_type",className:"reqLbl",label:"<a href='javascript:openHelpWindow(\"#ObservationType\")'>Proposal Type:</a>",labelWidth:lblW},
         ]},
         {type:"block",name:"atsum",list:[
           {type:"input",name:"requested_budget",offsetLeft:20,label:"<a href='javascript:openHelpWindow(\"#ArchiveBudget\")'>Proposed Budget:</a>"},
           {type:"newcolumn"},
           {type:"label",className:"fldhelp",name:"atunits",label:"(units=1000 U.S. dollars) "},
         ]},
         {type:"block",list:[
           {type:"block", blockOffset:0,name:"rightsfld",list:[
             {type:"select",name:"request_extra_flag",label:"<a href='javascript:openHelpWindow(\"#ProprietaryData\")'>Proprietary Rights:</a>",className:"reqLbl",labelWidth:lblW},
             {type:"newcolumn"},
             {type:"container",className:"errmsg",offsetTop:10,name:"rightshelp",label:""},
           ]},
           {type:"block", name:"rightsblk",list:[
             {type:"input",name:"rights_justification",label:"<a href='javascript:openHelpWindow(\"#DDTRights\")'>Justification of Request for Proprietary Rights:</a>",labelWidth:"auto",maxLength:200,inputWidth:400},
           ]},
         ]},
         {type:"block", name:"multicycleblk",list:[
           {type:"label",name:"mprop",label:"<a href='javascript:openHelpWindow(\"#Multicycle\")'>Is this a Multicycle proposal?</a>",  labelWidth:"auto"},
           {type:"newcolumn",offset:20},
           {type:"radio",name:"multi_cycle",label:"Yes",value:"Y",position:"label-right",checked: false},
           {type:"newcolumn",offset:20},
           {type:"radio",name:"multi_cycle",label:"No",value:"N",position:"label-right",checked: true},
         ]},
         {type:"block",name:"lpropblk",list:[
           {type:"label",name:"lprop",label:"<a href='javascript:openHelpWindow(\"#Linked\")'>Is this proposal linked to another proposal in this cycle?</a> ",labelWidth:"auto"},
           {type:"newcolumn",offset:20},
           {type:"radio",name:"linked_proposal",value:"Y",label:"Yes",position:"label-right",checked: false },
           {type:"newcolumn",offset:20},
           {type:"radio",name:"linked_proposal",value:"N",label:"No",position:"label-right",checked: true},
         ]},
         {type:"block",name:"lblock",offsetLeft:20,list:[
           {type:"input",name:"linked_propnum",label:"<a href='javascript:openHelpWindow(\"#Linked2\")'>If known, enter the proposal number of the linked proposal:</a>",labelWidth:"auto",maxLength:8,inputWidth:100},
         ]}
       ]},
       {type:"fieldset",name:"const",label:"Science ",width:"95%",list:[
        {type:"block",list:[

          {type:"input",name:"proposal_title",label:"<a href='javascript:openHelpWindow(\"#ProposalTitle\")'>Title:</a>",className:"reqLbl",maxLength:120,inputWidth:500,labelWidth:lblW},
          {type:"select",name:"category_descrip",label:"<a href='javascript:openHelpWindow(\"#SubjectCategory\")'>Subject Category:</a>",className:"reqLbl",labelWidth:lblW},
        ]}  ,
        {type:"block",list:[
          {type:"label",className:"reqLbl",name:"scikeyx",label:"<a class='reqLbl' href='javascript:openHelpWindow(\"#Keywords\")' title='Be inclusive; your selections may be used for preliminary matching of proposals to reviewers, and also for archive searches.'>Science Keywords:</a><br><span class='lblclass'>(255 chars)</span>",labelWidth:lblW},
          {type:"newcolumn",offset:0},
          {type: "label",label:"<div id='ktree' style=''></div>"},
          {type:"input",name:"scikeylbl",label:"",value:" ",hidden:true},
        ]},
        {type:"block",offsetLeft:0,offsetTop:0,list:[
          {type:"input",disabled:true,className:"reqLbl",name:"scikeyResults",label:" ",labelWidth:lblW,rows:2,maxLength:255,inputWidth:500,value:" "},
          //{type:"newcolumn",offset:10},
          //{type:"label",className:'fldhelp',name:"keywordshlp",label:"<span class='fldhelp'>Please select five(5) or more keywords (maximum of 255 characters) describing your proposal science.  Be inclusive; your selections may be used for preliminary matching of proposals to reviewers, and also for archive searches.</span> ",labelWidth:600},
        ]},
        {type:"block",list:[
          {type:"input",name:"abstract",className:"reqLbl",label:"<a href='javascript:openHelpWindow(\"#Abstract\")'>Abstract:</a><br><span class='lblclass'>(800 chars.)</span>",labelWidth:lblW,rows:6,maxLength:800,inputWidth:500}
        ]}  
      ]},
    {type:"container",name:"status",className:"statusContainer"},
   {type:"block",offsetLeft:150,offsetTop:15,list:[
        {type:"button",name:"save",value:"Save Changes"},
        {type:"newcolumn"},
        {type:"button",name:"discard",value:"Discard Changes"},
    ]},


];


         myForm = new dhtmlXForm("formB_container",formData);

         myForm.setSkin('dhx_skyblue');
         buildSubCat(myForm.getOptions("category_descrip"));
         buildPropType(myForm.getOptions("proposal_type"),"false");
         buildPropRights(myForm.getOptions("request_extra_flag"),"false");
         myForm.setItemValue("request_extra_flag","S");
         myForm.hideItem("atsum");
         myForm.hideItem("lblock");
         myForm.enableLiveValidation(false);
         buildTooltips(myForm);

         myTree = new dhtmlXTreeObject("ktree",null,null,"myroot");
         myTree.setImagePath(NAV_IMG_PATH);
         myTree.enableTreeImages(false);
         myTree.enableTreeLines(false);
         myTree.enableCheckBoxes(true,false);
         myTree.enableSmartCheckboxes(true);
         myTree.enableHighlighting(true);
         myTree.load("/cps-app/science_keywords.xml",postXML,"xml");
         myTree.setOnCheckHandler(scikey_selected);
         myTree.attachEvent("OnCheck",scikey_checked);
         myTree.attachEvent("OnClick",scikey_clicked);


         myForm.attachEvent("onButtonClick",function(id){
            clear_status();
            if (id=="save") save_changes();
            else if (id=="discard") reset_fields();
         });
         myForm.attachEvent("onBeforeChange", before_change);
         myForm.attachEvent("onChange", handle_change);
         myForm.attachEvent("onBeforeDataLoad", before_load);

}   

