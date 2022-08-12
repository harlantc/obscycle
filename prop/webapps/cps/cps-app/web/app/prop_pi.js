var myForm;
var errlbls = new Array("first", "last",
        "institute","country","email","telephone","orcid");

function isValidOrcId(data)
{
  var retval=true;
  var ptrn="^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[0-9X]$";
  if (data && data.trim().length > 0 && !data.match(ptrn)) {
    retval = false;
  }
  return retval;

}
function validate_pi()
{
  var retval=true;
  var str="";
  var val,val2;
  var reqitems = new Array("last","first", "email","telephone","institute","country");

  clear_errors();

  for (var ii=0;ii<reqitems.length;ii++) {
    val = myForm.getItemValue(reqitems[ii]);
    if (val==null || val.toString().trim() == "") {
      arr = myForm.getItemLabel(reqitems[ii]).split("<br");
      str += "Missing required field: "  + arr[0] + "<br>";
      set_errlbl(true,myForm,reqitems[ii]);
    }
  }

  var email = myForm.getItemValue("email");
  if (!validate_email(email)) {
      str += "Invalid email address." + "<br>";
      set_errlbl(true,myForm,"email");
  }
  if (!isValidOrcId(myForm.getItemValue("orcid"))) {
      str += "Invalid OrcId format: ####-####-####-####"
      set_errlbl(true,myForm,"orcid");
  }


  if (str.length > 1) {
    set_status("<span class='errlbl'>" + SAVE_FAILED +  str + "</span>");
    retval=false;
  }

  return retval;
}

function clear_errors() {
  //reset error labels
  for (var ii=0;ii<errlbls.length;ii++) {
    set_errlbl(false,myForm,errlbls[ii]);
  }
  clear_status();
}



function hidePIInfo(flg)
{
   myForm.hideItem("misc");
   myForm.hideItem("addrblk");
   myForm.setReadonly("first",true);
   myForm.setReadonly("middle",true);
   myForm.setReadonly("last",true);
   myForm.disableItem("country");
   myForm.disableItem("institute");
   //myForm.getCombo("country").disable();
   //myForm.getCombo("institute").disable();
   myForm.disableItem("filterby");
   if (flg ==0) myForm.hideItem("piblkmain");
   
}

function showPIInfo() 
{
   myForm.showItem("piblkmain");
   myForm.showItem("misc");
   myForm.showItem("addrblk");
   myForm.setReadonly("first",false);
   myForm.setReadonly("middle",false);
   myForm.setReadonly("last",false);
   myForm.enableItem("country");
   myForm.enableItem("institute");
   myForm.enableItem("pi_combo");
   //myForm.getCombo("country").enable();
   //myForm.getCombo("institute").enable();
   myForm.enableItem("filterby");
}



function handle_change(id, val,state,test) {
  clear_status(); 
  if (id=="pi_combo" ) {
    var previd=myForm.getItemValue("id");
    var ival = myForm.getCombo(id).getSelectedValue();
    //console.log("handlechange picombo: " + ival );
    //console.log("handlechange id: " + myForm.getItemValue("id") );
    if (ival != null) {
      hidePIInfo(1);

      if (ival != previd) {
        var url ="/cps-app/cps_props?operation=SELECTEDPI&persid=" + ival;
        myForm.load(url,postLoad2);
        setChanges(1);  
      }
    }
    else {
      if (previd != null && previd > 0) {
          myForm.setItemValue("first","");
          myForm.setItemValue("middle","");
      }

      myForm.setItemValue("id",0);
      showPIInfo();
    }
  }
  else if (id=="country" ) {
    if (myForm.isItemChecked("filterby")) {
      var rval = myForm.getItemValue("country");
      myForm.getCombo("institute").clearAll();
      var url ="/cps-app/cps_props?operation=INSTOPTIONS&cntry=" + rval;
      myForm.getCombo("institute").load(url);
    }
  } 
  else if (id=="filterby" ) {
    var rval = "ALL";
    if (myForm.isItemChecked(id)) {
      var rval = myForm.getItemValue("country");
    }
    myForm.getCombo("institute").clearAll();
    var url ="/cps-app/cps_props?operation=INSTOPTIONS&cntry=" + rval;
    myForm.getCombo("institute").load(url);
  }
  else if (id=="institute" && val==0) {
    //set_status("Please contact cxchelp@cfa.harvard.edu if your institution is missing from the 'Current Institution' selection box.");
  }
  else if (id=="country" && val==0) {
    //set_status("Please contact cxchelp@cfa.harvard.edu if your country is missing from the 'Country' selection box.");
  }
  else {
    //set_status("Press [Save Changes] to save updates");
    setChanges(1);  
  }
}

function postLoad2()
{
  // ONLY Called when PI SELECTED
  var ii = myForm.getItemValue("institute");
  var co = myForm.getCombo("institute");
  co.setComboText(ii);
  hidePIInfo(1);
  clear_errors();

  var errmsg = myForm.getItemValue("emsg");
  if (errmsg.length > 2) {
    set_status("<span class='errmsg'>" + errmsg + "</span>");
  }
}

function not_pi()
{
  set_editButtons(myForm,"true");
  myForm.forEachItem(function(name){
    var tt= myForm.getItemType(name);
    if (tt=="input") {
      if (!myForm.isItemHidden(name)) {
        myForm.setReadonly(name,false);
        myForm.setItemValue(name,"");
      }
    } else if (tt=="combo") {
      myForm.enableItem(name);
      myForm.getCombo(name).setComboText(" ");
      myForm.getCombo(name).unSelectOption();
    }
    else if (tt=="checkbox") {
      myForm.enableItem(name);
    }
  });
  myForm.setItemValue("id",0);
  myForm.getCombo("pi_combo").unSelectOption();
  myForm.hideItem("notpi");
  showPIInfo();
  var obj=document.getElementById("piinstr");
  obj.innerHTML="";
  obj=document.getElementById("instrLbl");
  obj.style.display="none";

  setChanges(1);
}

function postLoad()
{
  var propno=myForm.getItemValue("propno");
  var emsg = myForm.getItemValue("emsg");
  if (propno.indexOf("Invalid") >= 0) {
    doAlertLogoff(emsg);
    return;
  }
  if (emsg != null && emsg.length > 1){
    doAlert(emsg,badalertReturn);
    return;
  }
  var ii = myForm.getItemValue("institute");
  var co = myForm.getCombo("institute");
  co.setComboText(ii);

  //var co = myForm.getCombo("last");
  //if (myForm.getItemValue("cxc")=="true")  {
    //co.setComboValue(myForm.getItemValue("id"));
  //}
  //else {
    //co.setComboText(myForm.getItemValue("last"));
  //}
  myForm.setItemValue("id",0);
  myForm.getCombo("pi_combo").unSelectOption();
  myForm.setItemLabel("pifs",propno + ": Principal Investigator  " + myForm.getItemValue("proposal_title"));
  var isEdit = myForm.getItemValue("isEdit");
  var instr = "";
  if (isEdit=="false") {
    instr = "If this information is incorrect, please edit your profile using the link at the top of this page.";
  }
  if (myForm.getItemValue("sid") == "true")  {
    instr += "<br>If you are not the PI, use the <b>I am not the PI</b> button below to clear and unlock this form.";
    myForm.showItem("notpi");
    myForm.hideItem("piblkmain");
  }
  else {
    myForm.hideItem("notpi");
    myForm.showItem("piblkmain");
  }
  if (myForm.getItemValue("cxc") == "true")  {
    hidePIInfo(1) ;
  } else {
    showPIInfo() ;
  }
  // this will disable form fields if necessary
  set_editButtons(myForm,isEdit);
  if (isEdit=="false")  myForm.hideItem("piblkmain");

  var obj=document.getElementById("piinstr");
  obj.innerHTML=instr;
  obj=document.getElementById("instrLbl");
  if (instr == "") obj.style.display="none";
  else obj.style.display="block";
    
  setChanges(0);
  cps_style_load();

}

function loadPage() 
{
  myForm.setItemValue("operation","LOAD");
  var url = "/cps-app/cps_loadprop?page=PI&pid=" + pid;
  myForm.load(url,postLoad);
}


function save_changes() 
{
  var selectedId=myForm.getItemValue("id");
  var valResult=true;
  if (selectedId == null || selectedId <= 0) {
     valResult = validate_pi();
  }
  //myForm.setItemValue("lastname",myForm.getCombo("last").getComboText());
  myForm.setItemValue("lastname",myForm.getItemValue("last"));
  if (valResult) {
    myForm.setItemValue("operation","SAVE");
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


function reset_fields() 
{
  clear_errors();
  loadPage();
}

function doOnLoad() 
{
  var formData,lblW=150,inputW=400;

  formData = [
      {type:"input",hidden:true,name:"operation",value:""},
      {type:"input",hidden:true,name:"page",value:"PI"},
      {type:"input",hidden:true,name:"pid",value:pid},
      {type:"input",hidden:true,name:"sid"},
      {type:"input",hidden:true,name:"cxc"},
      {type:"input",hidden:true,name:"id"},
      {type:"input",hidden:true,name:"lastname"},
      {type:"input",hidden:true,name:"propno",value:0},
      {type:"input",hidden:true,name:"proposal_title",value:0},
      {type:"input",hidden:true,name:"emsg",value:0},
      {type:"input",hidden:true,name:"isEdit",value:"false"},
      {type:"input",hidden:true,name:"isDDT",value:"false"},
      {type:"settings",position:"label-left",labelWidth:"auto"},
      {type:"fieldset",name:"pifs",label:" Principal Investigator",list:[
        //{type:"label",name:"last_descr",label:"An * in the Last name pulldown indicates the user does not have a CXC account."},
          {type:"input",hidden:true,name:"title",label:"Title",inputWidth:100,value:"Dr."},
          {type:"block",name:"piblkmain",list:[
            {type:"combo",className:"reqLbl",name:"pi_combo",label:"<a href='javascript:openHelpWindow(\"#LastName\")'>Selectable PI:</a>"},
            {type:"newcolumn",offset:10},
            {type:"button",name:"clearbutton",value:"Clear",offsetTop:0}
          ]},
          {type:"block",list:[
          {type:"input",name:"last",className:"reqLbl",label:"<a href='javascript:openHelpWindow(\"#LastName\")'>Last:</a>",inputWidth:150,maxLength:25},
          {type:"newcolumn",offset:15},
          {type:"input",className:"reqLbl",name:"first",label:"<a href='javascript:openHelpWindow(\"#FirstName\")'>First:</a>",inputWidth:150,maxLength:20},
          {type:"newcolumn",offset:15},
          {type:"input",name:"middle",label:"<a href='javascript:openHelpWindow(\"#MiddleName\")'>Middle:</a>",inputWidth:150,maxLength:20}
        ]}
      ]},
      {type:"fieldset",name:"address",label:"Address",list:[
        {type:"block",list:[
          {type:"combo",name:"country",className:"reqLbl",label:"<a href='javascript:openHelpWindow(\"#Country\")'>Country</a>",labelWidth:lblW}
         ]},
         {type:"block",list:[
           {type:"combo",id:"institute",name:"institute",className:"reqLbl",label:"<a href='javascript:openHelpWindow(\"#Institute\")'>Primary Institution</a>",labelWidth:lblW},
           {type:"newcolumn",offset:10},
           {type:"checkbox",id:"filterby",name:"filterby",label:"Filter by Country"},
         ]},
         {type:"block",name:"addrblk", list:[
           {type:"input",name:"dept",label:"<a href='javascript:openHelpWindow(\"#Department\")'>Department</a>",inputWidth:inputW,labelWidth:lblW,maxLength:100},
           {type:"input",name:"street",label:"<a href='javascript:openHelpWindow(\"#Street\")'>Address</a>",inputWidth:inputW,labelWidth:lblW,maxLength:30},
           {type:"input",name:"mailstop",label:"<a href='javascript:openHelpWindow(\"#Mailstop\")'>Mailstop</a>",inputWidth:inputW,labelWidth:lblW,maxLength:30},
           {type:"input",name:"city",label:"<a href='javascript:openHelpWindow(\"#City\")'>City/Town</a>",inputWidth:inputW,labelWidth:lblW,maxLength:30},
           {type:"input",name:"state",label:"<a href='javascript:openHelpWindow(\"#State\")'>State/Province</a>",inputWidth:inputW,labelWidth:lblW,maxLength:30},
           {type:"input",name:"zip",label:"<a href='javascript:openHelpWindow(\"#Zip\")'>Zip/Postal Code</a>",inputWidth:inputW,labelWidth:lblW,maxLength:10},
         ]}
       ]},
       {type:"fieldset",name:"misc",label:"Misc",list:[
         {type:"block",list:[
           {type:"block",blockOffset:0,list:[
             {type:"input",name:"orcid",label:"<a href='javascript:openHelpWindow(\"#ORCID\")'>ORCID</a>",inputWidth:200, labelWidth:lblW,maxLength:20},
             {type:"newcolumn",offset:10},
             {type:"label",label:"( ####-####-####-#### )"},
           ]},
           {type:"input",name:"email",className:"reqLbl",label:"<a href='javascript:openHelpWindow(\"#NetworkAddress\")'>Email Address</a>",inputWidth:inputW, labelWidth:lblW,maxLength:50},
           {type:"input",className:"reqLbl",name:"telephone",label:"<a href='javascript:openHelpWindow(\"#TelephoneNumber\")'>Telephone</a>",inputWidth:inputW,labelWidth:lblW,maxLength:30},
         ]},
       ]},
       {type:"container",name:"status",className:"statusContainer"},
       {type:"block",offsetLeft:120,offsetTop:15,list:[
         {type:"button",name:"save",value:"Save Changes"},
         {type:"newcolumn"},
         {type:"button",name:"discard",value:"Discard Changes"},
         {type:"newcolumn",offset:30},
         {type:"button",name:"notpi",value:"I am not the PI"},
       ]},
   ];

         myForm = new dhtmlXForm("formB_container",formData);
         //cps_style_init();

         myForm.setSkin('dhx_skyblue');
         myForm.enableLiveValidation(true);
         buildTooltips(myForm);
         myForm.hideItem("notpi");

         var picombo = myForm.getCombo("pi_combo");
         var inputobj=picombo.getInput();
         if (inputobj != null)
           inputobj.style.backgroundColor="#f9f9d0";

         picombo.enableAutocomplete();
         picombo.enableFilteringMode(true);
         picombo.allowFreeText(false);
         picombo.DOMelem_input.maxLength = 25;
         picombo.setSize(250);
         picombo.setOptionWidth(250);
         picombo.attachEvent("onChange", handle_change);
         picombo.setPlaceholder("Select a PI");


         var inst=myForm.getCombo("institute");
         inst.setPlaceholder("Please select an Institution");
         inst.enableAutocomplete();
         inst.enableFilteringMode("between");
         inst.allowFreeText(true);
         inst.DOMelem_input.maxLength = 100;
         inst.setOptionWidth(500);
         inst.setSize(inputW);
         myForm.setItemValue("institute","");

         var cntry= myForm.getCombo("country");
         cntry.enableAutocomplete();
         cntry.enableFilteringMode("between");
         cntry.allowFreeText(false);
         cntry.DOMelem_input.maxLength = 30;
         cntry.setSize(inputW);
         myForm.setItemValue("country","");
         myForm.attachEvent("onButtonClick", function(id){ 
              clear_status();
              if (id=="save") save_changes();
              else if (id=="discard") reset_fields();
              else if (id=="clearbutton") not_pi();
              else if (id=="notpi") not_pi();
         });
         myForm.attachEvent("onChange", handle_change);

         var url ="/cps-app/cps_props?operation=CNTRYOPTIONS";
         cntry.load(url).then(function() {
           var url ="/cps-app/cps_props?operation=INSTOPTIONS&cntry=ALL";
           inst.load(url).then(function() {
             var url ="/cps-app/cps_props?operation=COIOPTIONS";
             picombo.load(url,loadPage);
           });
        });

}   
