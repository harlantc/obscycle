var myForm,mygrid;
var sortCol=1;
var persidCol=0,selectCol=1,emailCol=2,coinumCol=3,lastCol=4,firstCol=5,countryCol=6,instCol=7;
var reqitems = new Array("coi_last","coi_first","coi_email",
		"coi_country","coi_institute");

var gridPersIds="";


function after_sort(col,type,direction)
{
  //mygrid.setSortImgState(true,col,direction);
  sortCol=col;
  set_status("");
}


function delete_entry() {
  var doclear=0;
  var coinum= mygrid.cellById(mygrid.getSelectedRowId(),coinumCol).getValue();
  var msg = "Are you sure you want to delete the selected Co-Investigator?";
  if (myForm.getItemValue("coi_contact")=="Y" && coinum==1){
    msg +="<br>This will clear the data for the Observing Investigator.";
    doclear=1;
  }   
  myForm.setUserData("coi_phone","doclear",doclear);

  doclear =0;
  if (myForm.getItemValue("cost_persid") == mygrid.getSelectedRowId()) {
    msg +="<br>This will clear the Cost PI.";
    doclear=1;
  }
  // now check if cost PI is after one being deleted because then we need to
  // update that as well
  var ival = parseInt(coinum);
  var cval = parseInt(myForm.getItemValue("cost_pi"));
  if (cval > ival) {
       doclear=2;
  }
  myForm.setUserData("cost_pi","doclear",doclear);

  doConfirm(msg,confirmDelete);

}
function confirmDelete(result) 
{
  if (result) {
    var doclear = myForm.getUserData("coi_phone","doclear");
    var docostclear = myForm.getUserData("cost_pi","doclear");
    //myForm.setItemValue("gridvalues",mygrid.cellById(mygrid.getSelectedRowId(),coinCol).getValue());
    myForm.setItemValue("gridvalues",mygrid.getSelectedRowId());
    myForm.setItemValue("operation","DELETECOI");
    clear_coifs(0);
    myForm.hideItem("coieditfs");
    if (doclear == 1 || docostclear>0) {
      if (doclear == 1) {
        myForm.setItemValue("coi_contact","N");
        myForm.setItemValue("coi_phone","");
      }
      if (docostclear == 1) {
        myForm.setItemValue("cost_pi","");
        myForm.setItemValue("cost_persid","");
      }
      else if (docostclear == 2) {
        var cval = parseInt(myForm.getItemValue("cost_pi"));
        cval = cval -1;
        myForm.setItemValue("cost_pi",cval);
      }
      myForm.setItemValue("operation","DELETECOIOBS");
      save_changes("deletebutton");
    } else { 
      save_grid("deletebutton");
    }
  }
}

function can_drag() 
{
  reset_coi_fieldset();
  obj = mygrid.getUserData("","drag");
  if (obj != null && obj=="nodrag")
    return false;
  if (sortCol==coinumCol)  {
    return true;
  }
  else {
    set_status("<span class='errmsg'>Co-Investigators must be sorted by ascending number(#) before re-ordering</span>");
    return false;
  }
}


function can_renumber(sid,tid) {
  for (var ii=0;ii<mygrid.getRowsNum();ii++) {
    var coino=mygrid.cellByIndex(ii,coinumCol).getValue();
    if (coino != (ii+1)) {
      doAlert("CoIs must be sorted by ascending number(#) before re-ordering",alertReturn);
      return false;
    }
  }
  return true;
}
function renumber_entry(sid,tid) {
  reset_coi_fieldset();
  myForm.setItemValue("operation","RENUMBERCOI");
  //myForm.setItemValue("gridvalues",mygrid.cellById(sid,coinCol).getValue());
  myForm.setItemValue("gridvalues",sid);
  //alert(mygrid.cellById(tid,coinumCol).getValue());
  myForm.setItemValue("coi_number",mygrid.cellById(tid,coinumCol).getValue());
  myForm.setItemValue("cost_pi",mygrid.cellById(tid,coinumCol).getValue());
  save_grid(null);
}


function save_coi () {
  save_changes("save");
}


function mouse_over(id,ind) {
  if (ind == 0) 
    mygrid.cells(id,ind).cell.title="This number indicates the priority of Co-Investigators. If there is an observer, they should be listed as the first priority.";

  return false;
}
function hideCoiInfo() 
{
  myForm.hideItem("coi_email");
  myForm.setReadonly("coi_last",true);
  myForm.setReadonly("coi_first",true);
  myForm.disableItem("coi_country");
  myForm.disableItem("coi_institute");
  myForm.disableItem("filterby");
}
function showCoiInfo() 
{
  myForm.showItem("coi_email");
  myForm.setReadonly("coi_first",false);
  myForm.setReadonly("coi_last",false);
  myForm.enableItem("coi_country");
  myForm.enableItem("coi_institute");
  myForm.enableItem("filterby");
}
        
function clear_add() {
    clear_coifs(0);
    myForm.getCombo("coi_combo").unSelectOption();
}
function handle_change(id,val,state,test) {
  if (!test || test.indexOf(DONOTCLEARSTATUS) < 0)
    clear_success_status();

  if (id=="coi_combo") {
    var ival= myForm.getCombo(id).getSelectedValue();
    if (ival != null) {
      if (ival != myForm.getItemValue("id")) {
        var url ="/cps-app/cps_props?operation=SELECTEDCOI&persid=" + ival;
        myForm.load(url,postLoadCoI);
        setChanges(1);
      } else {
        hideCoiInfo();
      }
    } else {
      previd=myForm.getItemValue("id");
      if (previd != null && previd > 0) {
         clear_coifs(1);
      }
      myForm.setItemValue("id",0);
      showCoiInfo();
    }

  } else if (id=="filterby" ) {
    myForm.getCombo("coi_institute").clearAll();
    var rval="ALL";
    if (myForm.isItemChecked("filterby")) {
      rval = myForm.getItemValue("coi_country");
    }
    var url ="/cps-app/cps_props?operation=INSTOPTIONS&cntry=" + rval;
    myForm.getCombo("coi_institute").load(url);
  }
  else if (id=="coi_country") {
    if (myForm.isItemChecked("filterby")) {
      rval = myForm.getItemValue("coi_country");
      var url ="/cps-app/cps_props?operation=INSTOPTIONS&cntry=" + rval;
      myForm.getCombo("coi_institute").load(url);
    }
   //var changes=getCookie("unsavedChanges");
  } else {
    if (id=="coi_contact") {
      var rval = myForm.getItemValue(id);
      if (rval == "Y") {
        myForm.showItem("obsblock");
      } else {
        var chkarr = ["coi_phone"];
        if (check_fields(myForm,chkarr) ==0) 
          myForm.hideItem("obsblock");
        else
          myForm.showItem("obsblock");
      } 
    } 
    if (id=="cost_pi_list") {
      var persid =myForm.getItemValue("cost_pi_list");
      myForm.setItemValue("cost_persid", persid);
      if (persid != null && persid != "") {
        for (var row=0; row< mygrid.getRowsNum(); row++) {
          if (persid == mygrid.cellByIndex(row,persidCol).getValue())
             myForm.setItemValue("cost_pi", mygrid.cellByIndex(row,coinumCol).getValue());
        }
      } else 
        myForm.setItemValue("cost_pi", "");
    }
  }
  if (id=="filterby"  ) {
  } else  {
    setChanges(1) 
  }
  return true;
}


function exceedCoiMax() {
  return false;
}

   
function add_entry() {
   clear_errors();
   if (mygrid.getRowsNum() > MAX_COIS) {
      doAlert("You have exceeded the maximum number of Co-Investigators",exceedCoiMax);
      myForm.disableItem("addbutton");
      myForm.hideItem("coieditfs");
      return false;
   }
   clear_errors();
   myForm.showItem("coiblkmain");
   myForm.getCombo("coi_combo").unSelectOption();
   handle_change("coi_combo");
   myForm.setItemValue("operation","ADDCOI");
   myForm.setItemLabel("save","Add CoI");
   myForm.setItemLabel("coieditfs","Add Co-Investigator");
   clear_coifs(0);
   showCoiInfo() 
   //doFilter();
   myForm.showItem("coieditfs");
   var changes=getCookie("unsavedChanges");
   handle_change("coi_country");
   setChanges(changes);
   var obj = myForm.getInput("coi_email");
   obj.scrollIntoView();

   return true;
}
function clear_coifs(flg)
{
   myForm.setItemValue("coi_last","");
   myForm.setItemValue("coi_first","");
   myForm.setItemValue("coi_number","");
   myForm.setItemValue("coi_email","");
   co = myForm.getCombo("coi_institute");
   co.setComboText("");
   myForm.setItemValue("coi_country","USA");
}

function modify_entry() {
  var isEdit = myForm.getItemValue("isEdit");
  if (isEdit == "true") {
   clear_errors();
   var changes=getCookie("unsavedChanges");
   var rowId=mygrid.getSelectedRowId();
   myForm.setItemLabel("coieditfs","Modify Co-Investigator #" + mygrid.cellById(rowId,coinumCol).getValue());
   myForm.showItem("coieditfs");
   myForm.setItemValue("coi_number",mygrid.cellById(rowId,coinumCol).getValue());
   myForm.setItemValue("coi_last",mygrid.cellById(rowId,lastCol).getValue());
   myForm.setItemValue("coi_first",mygrid.cellById(rowId,firstCol).getValue());
   myForm.setItemValue("coi_email",decodeEntities(mygrid.cellById(rowId,emailCol).getValue()));
   myForm.getCombo("coi_country").setComboText(mygrid.cellById(rowId,countryCol).getValue());
   combo_setInsensitive(myForm.getCombo("coi_country"),mygrid.cellById(rowId,countryCol).getValue());
   handle_change("coi_country");
   myForm.getCombo("coi_institute").setComboText(mygrid.cellById(rowId,instCol).getValue());
   handle_change("coi_combo");
   myForm.hideItem("coiblkmain");
   myForm.setItemLabel("save","Modify CoI");
   myForm.setItemValue("operation","MODIFYCOI");
   setChanges(changes);
  }
}

function clear_errors() {
  //reset error labels
  for (var ii=0;ii<reqitems.length;ii++) {
    set_errlbl(false,myForm,reqitems[ii]);
  }
  set_errlbl(false,myForm,"coi_phone");
  set_errlbl(false,myForm,"coi_obs");
  
  clear_status();
}

function validate_coi() {
  var retval=true;
  var str="";
  set_errlbl(false,myForm,"coi_obs");
  str += validate_yn(myForm.getItemValue("coi_contact"),myForm,["coi_obs","coi_phone"]);
  if (str.length > 1) {
    set_status("<span class='errlbl'>" + SAVE_FAILED +  str + "</span>");
    retval=false;
  }
  return retval;
}

function verify_coi() {
  var retval=true;
  var str="";
  var val;

  var selectedCoI = myForm.getCombo("coi_combo").getSelectedValue();
  if (selectedCoI == null) {
    for (var ii=0;ii<reqitems.length;ii++) {
      val = myForm.getItemValue(reqitems[ii]);
      if (val==null || val.toString().trim() == "") {
        arr = myForm.getItemLabel(reqitems[ii]).split("<br");
        str += "Missing required field: "  + arr[0] + "<br>";
        set_errlbl(true,myForm,reqitems[ii]);
      }
    }
    if (!validate_email(myForm.getItemValue("coi_email"))) {
        str += "Invalid Email ";
        set_errlbl(true,myForm,reqitems["coi_email"]);
    }
      
  } else {
    for (var row=0; row< mygrid.getRowsNum(); row++) {
      var persId = mygrid.cellByIndex(row,persidCol).getValue();
      if (persId == selectedCoI) {
        str += "CoI already exists as CoI #" + mygrid.cellByIndex(row,coinumCol).getValue();
        set_errlbl(true,myForm,"coi_last");
        break;
      }
    }
  }
  str += validate_yn(myForm.getItemValue("coi_contact"),myForm,["coi_obs","coi_phone"]);
  if (str.length > 1) {
    set_status("<span class='errlbl'>" + SAVE_FAILED +  str + "</span>");
    retval=false;
  }
    

  return retval;

}
function no_drag()
{
//alert("no_drag");
  return false;
}

function save_grid(btn) {
  var op = myForm.getItemValue("operation");
  if (btn != null) myForm.disableItem(btn);
  mygrid.setUserData("","drag","nodrag");

  disablebuttons();
  document.body.className = "waiting";
  myForm.send("/cps-app/cps_saveprop","post",function(name,retstr) {
    mygrid.setUserData("","drag","candrag");
    mygrid.clearSelection();
    enablebuttons();
    document.body.className = "";
    myForm.setItemValue("operation","SAVE");
    if (process_save(retstr,false)) {
    }
    loadGrid();
  });
}
  
function save_changes(btn) {
  var op = myForm.getItemValue("operation");
  clear_errors();
  if (op == "ADDCOI" || op=="MODIFYCOI") {
    if (!verify_coi()) {
      return false;
    }
  } else  {
    if (!validate_coi()) return false;
  }
  if (btn) myForm.disableItem(btn);
  //myForm.hideItem("coibtns");
  disablebuttons();
  mygrid.setUserData("","drag","nodrag");

  //alert(myForm.getItemValue("cost_pi") + "---" + myForm.getItemValue("cost_persid"));
  document.body.className = "waiting";
  myForm.send("/cps-app/cps_saveprop","post",function(name,retstr) {
    document.body.className = "";
    mygrid.setUserData("","drag","candrag");
    mygrid.clearSelection();
    enablebuttons();
    //myForm.showItem("coibtns");
    if (process_save(retstr,false)) {
      reset_coi_fieldset();
      // rebuild to get coi grid updated
      loadPage();
    } else {
      if (op == "ADDCOI" || op=="MODIFYCOI") {
      } else {
       myForm.setItemValue("operation","SAVE");
      }

      myForm.enableItem(btn);
    }
  });
}

function disablebuttons()
{
  myForm.disableItem("addbutton");
  myForm.disableItem("modifybutton");
  myForm.disableItem("deletebutton");
  myForm.disableItem("discard");
  myForm.disableItem("save");
}
function enablebuttons()
{
  myForm.enableItem("addbutton");
  myForm.enableItem("modifybutton");
  myForm.enableItem("deletebutton");
  myForm.enableItem("discard");
  myForm.enableItem("save");
}



function reset_coi_fieldset()
{
   myForm.setItemLabel("save","Save Changes");
   myForm.setItemValue("operation","SAVE");
   // the combo menu stays up if scrolled and then clicking in grid box, so force close
   //var coicombo = myForm.getCombo("coi_last"); 
   //coicombo.closeAll(); 

   myForm.hideItem("coieditfs");
}

function reset_fields() {
   clear_errors();
   reset_coi_fieldset();
   loadPage();
   clear_status();
   setChanges(0);
} 


function row_selected() {
   reset_coi_fieldset();
   var rowId = mygrid.getSelectedRowId();
   if (mygrid.cellById(rowId,selectCol).getValue() == "1")
     myForm.disableItem("modifybutton");
   else
     myForm.enableItem("modifybutton");
   myForm.enableItem("deletebutton");
   clear_status();
   //modify_entry();
   return true;
}

function cost_pi_options() {
  var changes=getCookie("unsavedChanges");
  var cost_persid=myForm.getItemValue("cost_persid");
  var ele= myForm.getOptions("cost_pi_list");
  var cost_idx=-1;
  // clear out existing entries
  if (ele != null && ele.length > 0) {
    for (var ii=0;ii< ele.length;ii++) {
      ele.remove(ii);
    }
  }
  ele.length = 0;
  ele.add(new Option("",""));
  for (var row=0; row< mygrid.getRowsNum(); row++) {
    //var persId=mygrid.getRowId(row);
    var persId = mygrid.cellByIndex(row,persidCol).getValue();
    if (persId > 0) {
      var ordr = mygrid.cellByIndex(row,coinumCol).getValue();
      var clast = mygrid.cellByIndex(row,lastCol).getValue();
      var cfirst = mygrid.cellByIndex(row,firstCol).getValue();
      var ctry = mygrid.cellByIndex(row,countryCol).getValue();
      if (clast != null && clast.length > 0 && 
          ctry != null && ctry.indexOf("USA") >= 0) {
       str = clast + ", "   + cfirst;
       ele.add(new Option(str,persId));
      }
      if (cost_persid == persId) cost_idx=row;
    }
  }
  if (cost_persid != null && cost_persid != "") {
    myForm.setItemValue("cost_pi_list",cost_persid);

    myForm.setItemValue("cost_pi", mygrid.cellByIndex(cost_idx,coinumCol).getValue());
  } else {
    myForm.setItemValue("cost_pi","");
    myForm.setItemValue("cost_pi_list","");
  }
  
  setChanges(changes);
}

function postLoadCoI() {
  var val = myForm.getItemValue("coi_institute");
  var co = myForm.getCombo("coi_institute");
  if (val.length > 1)
    co.setComboText(val);
  else 
    co.setComboValue("");
  myForm.setItemValue("coi_email","");
  hideCoiInfo(); 


  var errmsg = myForm.getItemValue("emsg");
  if (errmsg.length > 2) {
    set_status("<span class='errmsg'>" + errmsg + "</span>");
  }

}

function postLoad() {
  var propno=myForm.getItemValue("propno");
  if (propno.indexOf("Invalid") >= 0) {
    var msg = myForm.getItemValue("emsg");
    doAlertLogoff(msg);
    return false;
  }
  myForm.setItemValue("operation","SAVE");
  //myForm.setItemLabel("coifs",propno + ": Co-Investigators");
  var fs=document.getElementById("coifs");
  fs.innerHTML=propno +  ": Co-Investigators  " + myForm.getItemValue("proposal_title");
  handle_change("coi_contact",null,null,DONOTCLEARSTATUS);
  postLoadCoI();

  var isEdit = myForm.getItemValue("isEdit");
  set_editButtons(myForm,isEdit);
  myForm.disableItem("modifybutton");
  myForm.disableItem("deletebutton");
  myForm.disableItem("renumberbutton");
  if (isEdit == "false") {
    myForm.hideItem("coibtns");
    myForm.disableItem("coi_contact","Y");
    myForm.disableItem("coi_contact","N");
    mygrid.enableDragAndDrop(false);
    var instr=document.getElementById("coiinstr");
    if (instr) instr.style.display="none";
  }
  loadGrid();
  setChanges(0);
} 

function postGrid() {
  gridPersIds = "";
  var obj = myForm.getContainer("status2");
  obj.innerHTML = "";
  if (mygrid.getRowsNum() < 1) {
    mygrid.setColumnHidden(0);
    mygrid.addRow(0,["","","","No CoIs","","","",""]);
    mygrid.cells(0,3).setBgColor(DISABLED_CELL_BG);
    mygrid.setColspan(0,3,5);
    mygrid.cells(0,3).setHorAlign('l');
    mygrid.setEditable(false);
  }
  else {
    for (var ii=0;ii<mygrid.getRowsNum();ii++) {
      var persid=mygrid.cellByIndex(ii,persidCol).getValue();
      gridPersIds += persid + ",";
      if (persid == myForm.getItemValue("piid")) {
         obj.innerHTML="<span class='errmsg'>Warning: </span>CoI " + mygrid.cellByIndex(ii,lastCol).getValue() + " is the P.I. of this proposal.";
      }
    }
  }
  mygrid.setSortImgState(true,0,"asc");
  sortCol=coinumCol;
  //mygrid.adjustParentSize();
  cps_style_load();
  cost_pi_options();
}

function loadGrid() {
  mygrid.clearAll();
  url = "/cps-app/cps_loadprop?page=COIGRID&pid=" + pid;
  mygrid.load(url,postGrid,"json");
  myForm.disableItem("deletebutton");
  myForm.disableItem("modifybutton");
}

function loadPage() {
  myForm.setItemValue("operation","LOAD");
  var url = "/cps-app/cps_loadprop?page=COI&pid=" + pid;
  myForm.load(url,postLoad);
  setChanges(0);
}

function doFilter() {
  var coicombo = myForm.getCombo("coi_combo"); 
  coicombo.filter(function(opt){ 
     var oval= opt.value + ",";
     if (gridPersIds.indexOf(oval) >= 0) {
         // console.log("opt=" + opt.value);
         return false;
     } else {
        return true;
     }
  },false);
}


function doOnLoad() {
  var lblWidth=130;
  var inputW=400;
  var formData=[
    {type:"input",hidden:true,name:"operation",value:""},
    {type:"input",hidden:true,name:"page",value:"COI"},
    {type:"input",hidden:true,name:"propno",value:"0"},
    {type:"input",hidden:true,name:"proposal_title",value:""},
    {type:"input",hidden:true,name:"pid",value:pid},
    {type:"input",hidden:true,name:"piid",value:""},
    {type:"input",hidden:true,name:"emsg",value:""},
    {type:"input",hidden:true,name:"isEdit",value:"false"},
    {type:"input",hidden:true,name:"isDDT",value:"false"},
    {type:"input",hidden:true,name:"gridvalues",value:""},
    {type:"input",hidden:true,name:"cost_pi",value:""},
    {type:"input",hidden:true,name:"cost_persid",value:""},
    {type:"input",hidden:true,name:"cxc",value:""},
    {type:"input",hidden:true,name:"id",value:""},

       {type:"block", name:"coibtns", list:[
         {type:"button",name:"addbutton",value:"Add CoI",offsetLeft:30},
         {type:"newcolumn",offset:20},
         {type:"button",name:"modifybutton",value:"Modify Selected",offsetLeft:30},
         {type:"newcolumn",offset:20},
         {type:"button",name:"deletebutton",value:"Delete Selected"},
         {type:"newcolumn",offset:20},
    ]},
    {type:"block",offsetTop:20,list:[
    ]},
    {type:"fieldset",name:"prop",label:"Additional Information",width:"95%",list:[
       {type: "block",name:"whoblock", list:[
          {type:"label",name:"coi_obs",label:"<a href='javascript:openHelpWindow(\"#ObservingInvestigator\")'>Is the first Co-I responsible for the observation rather than the PI?</a>"},
          {type:"newcolumn",offset:20},
          {type:"radio",name:"coi_contact",value:"Y",label:"Yes",checked: false,position:"label-right"},
          {type:"newcolumn",offset:20},
          {type:"radio",name:"coi_contact",value:"N",label:"No",checked: true,position:"label-right"}
       ]},
       {type: "block",name:"obsblock", list:[
          {type:"input",name:"coi_phone",offsetLeft:20,label:"<a href='javascript:openHelpWindow(\"#ObservingInvestigatorTelephone\")'>Observing Investigator Telephone:</a>",maxLength:24 },
       ]},
       {type: "block", list:[
          {type:"select",name:"cost_pi_list",label:"<a href='javascript:openHelpWindow(\"#CostPI\")'>If PI is not based in USA and proposal has USA Co-Is, which Co-I will be the Cost PI?</a>" }
       ]},
     ]},
    {type:"fieldset",name:"coieditfs",label:"Edit Co-Investigator",width:"95%",list:[
       {type:"input",hidden:true,name:"coi_number",value:""},
       {type:"block",name:"coiblkmain",list:[
          {type:"combo",className:"reqLbl",labelWidth:lblWidth,name:"coi_combo",label:"<a href='javascript:openHelpWindow(\"#CoIs\")'>Selectable CoI:</a>"},
        {type:"newcolumn",offset:10},
        {type:"button",name:"clearbutton",value:"Clear",offsetTop:0}
       ]},
       {type:"block",list:[
          {type:"input",className:"reqLbl",labelWidth:lblWidth,name:"coi_last",inputWidth:inputW,maxLength:25,label:"<a href='javascript:openHelpWindow(\"#CoInvestigatorLastName\")'>Last:</a>"},
          {type:"input",className:"reqLbl",labelWidth:lblWidth,name:"coi_first",label:"<a href='javascript:openHelpWindow(\"#CoInvestigatorFirstName\")'>First:</a>",inputWidth:inputW,maxLength:20},
          {type:"input",className:"reqLbl",labelWidth:lblWidth,name:"coi_email",label:"<a href='javascript:openHelpWindow(\"#CoInvestigatorEMail\")'>Email Address</a>",inputWidth:inputW ,maxLength:50},
          {type:"combo",className:"reqLbl",labelWidth:lblWidth,name:"coi_country",label:"<a href='javascript:openHelpWindow(\"#CoInvestigatorCountry\")'>Country:</a>"},
       ]},
       {type:"block",list:[
          {type:"combo",className:"reqLbl",labelWidth:lblWidth,name:"coi_institute",label:"<a href='javascript:openHelpWindow(\"#CoInvestigatorInstitute\")'>Institution:</a>" },
        {type:"newcolumn",offset:10},
        {type:"checkbox",name:"filterby",label:"Filter by Country"}
       ]}
    ]},
     {type:"container",name:"status",className:"statusContainer"},
     {type:"container",name:"status2",className:"statusContainer"},
     {type:"block",offsetLeft:150,offsetTop:15,list:[
        {type:"button",name:"save",value:"Save Changes"},
        {type:"newcolumn"},
        {type:"button",name:"discard",value:"Discard Changes"},
     ]},

  ];

  myForm=new dhtmlXForm("formB_container",formData);

  myForm.attachEvent("onBeforeChange",function(id,old_val,new_val) { 
    if (myForm.getItemValue("isEdit") == "false") 
      return false; 
    else 
      return true;
    });
  myForm.attachEvent("onChange",handle_change);
  myForm.attachEvent("onButtonClick",function(id){
    clear_status();
    if (id=="addbutton") add_entry();
    else if (id=="modifybutton") modify_entry();
    else if (id=="deletebutton") delete_entry();
    else if (id=="clearbutton") clear_add();
    else if (id=="save") save_coi();
    else if (id=="discard") reset_fields();
  });
  myForm.hideItem("coieditfs");
  myForm.hideItem("obsblock");
  buildTooltips(myForm);

  var cntry = myForm.getCombo("coi_country");
  cntry.enableAutocomplete();
  cntry.enableFilteringMode(true);
  cntry.allowFreeText(false);
  cntry.setOptionWidth(300);
  cntry.setSize(inputW);


  var inst = myForm.getCombo("coi_institute");
  inst.setComboText("");
  inst.setPlaceholder("Please select an Institution");
  inst.enableAutocomplete();
  inst.enableFilteringMode("between");
  inst.allowFreeText(true);
  inst.DOMelem_input.maxLength = 100;
  inst.setOptionWidth(500);
  inst.setSize(inputW);


  // can't load coi options until after grid is loaded
  // need to exclude existing cois
  var coicombo = myForm.getCombo("coi_combo"); 
  var inputobj=coicombo.getInput();
  if (inputobj != null)
        inputobj.style.backgroundColor="#f9f9d0";
  coicombo.enableAutocomplete(); 
  coicombo.enableFilteringMode(true);
  coicombo.allowFreeText(false); 
  coicombo.DOMelem_input.maxLength = 25;
  coicombo.setOptionWidth(inputW);
  coicombo.setSize(inputW);
  coicombo.setPlaceholder("Selectable Co-Investigators: Search by last name");
 


  mygrid=new dhtmlXGridObject('grid');
  var gdiv="<div title=\"Click header to sort table. Click&#44 hold&#44 and drag rows to re-prioritize CoIs (only when table is sorted by priority #).\">";
  var myhdr= ",,," + gdiv + "#</div>," + gdiv + "Last</div>," + gdiv + "First</div>," +  gdiv + "Country</div>," + gdiv + "Institute</div>";
  mygrid.setHeader(myhdr);
  mygrid.setInitWidthsP(".001,.001,.001,5,15,15,15,*");
  mygrid.setColumnHidden(0,true);
  mygrid.setColumnHidden(1,true);
  mygrid.setColumnHidden(2,true);
  mygrid.setColumnMinWidth("0,0,0,20,30,30,30,30");
  mygrid.setColAlign("left,left,left,center,left,left,left,left");
  mygrid.setSkin("dhx_skyblue");
  mygrid.setColTypes("ro,ro,ro,ro,ro,ro,ro,ro");
  mygrid.setColSorting("na,na,na,int,custom_str_sort,custom_str_sort,custom_str_sort,custom_str_sort");
  mygrid.setImagePath(CSS_IMG_PATH);
  mygrid._in_header_special=function(tag,index,data){
      tag.innerHTML=data[0];
      tag.title="Click column header to sort table. Click, hold, and drag rows to re-prioritize CoIs (only when table is sorted by priority #).";
  };
  mygrid.init();
  mygrid.enableDragAndDrop(true);
  mygrid.rowToDragElement=function(id){
    //any custom logic here
    var text = mygrid.cells(id,coinumCol).getValue();
    return text;
  }
  mygrid.enableAutoHeight(true,300,true);
  mygrid.enableColSpan(true);
  //mygrid.enableAutoWidth(true);
  mygrid.enableRowsHover(true,"gridhover");
  mygrid.attachEvent("onMouseOver",mouse_over);
  mygrid.attachEvent("onBeforeDrag",can_drag);
  mygrid.attachEvent("onDrag",can_renumber);
  mygrid.attachEvent("onDrop",renumber_entry);
  mygrid.attachEvent("onAfterSorting",after_sort);
  var click_timer = null;
  mygrid.attachEvent("onRowSelect",function(){
    if (click_timer) window.clearTimeout(click_timer)
    click_timer=window.setTimeout(row_selected,300);
  });
  mygrid.attachEvent("onRowDblClicked",function(){
    if (click_timer) click_timer=window.clearTimeout(click_timer)
    window.clearTimeout(this.onRowSelectTime);
    var rowId = mygrid.getSelectedRowId();
    if (mygrid.cellById(rowId,selectCol).getValue() == "1")
       myForm.disableItem("modifybutton");
    else {
      myForm.enableItem("modifybutton");
      modify_entry();
    }
  });
  set_status("");

  var url ="/cps-app/cps_props?operation=CNTRYOPTIONS";
  cntry.load(url).then(function() {
     var url ="/cps-app/cps_props?operation=INSTOPTIONS&inst=ALL";
     inst.load(url).then(function() {
       var url ="/cps-app/cps_props?operation=COIOPTIONS";
       coicombo.load(url).then(function() {
         loadPage();
     });
     });
  });


}   
