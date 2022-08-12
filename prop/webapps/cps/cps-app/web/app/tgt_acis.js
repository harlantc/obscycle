var myForm;
var statCol=2;
var ccdLblStr="<a class='reqLbl' href='javascript:openHelpWindow(\"#CCDsOn\")'>CCDs On:</a>";


function clear_fields() {
  myForm.clear();
  clear_status();
}

function before_change(id) {
  if (myForm.getItemValue("isEdit") == "false")
    return false;
  else 
    return true;
}

function handle_change(id,val,state,test) {
  if (!test || test.indexOf(DONOTCLEARSTATUS) < 0)
    clear_success_status();

  if (id == "exp_mode")  {
    var rval = myForm.getItemValue(id);
    if (rval == "CC") {
      myForm.showItem("ccwarn");
    } else
      myForm.hideItem("ccwarn");
  }
  if (id == "exp_mode" || id == "bep_pack")
    validate_expmode();

  setChanges(1);
}
  

function save_changes() {

  if (validate_acis()) {
    myForm.setItemValue("operation","SAVE");
    myForm.disableItem("save");
    document.body.className = "waiting";
    myForm.send("/cps-app/cps_savetgt","post",function(name,retstr) {
       document.body.className = "";
       myForm.enableItem("save");
       if (process_save(retstr,false))
	 setChanges(0);
    });
    return true;
  }
}
  
function reset_fields() {
  set_errlbl(false,myForm,"ccdlbl");
  set_errlbl(false,myForm,"chipsel");
  set_errlbl(false,myForm,"multiple_spectral_lines");
  clear_status();
  loadPage();
} 

function validate_expmode() {
  var retval=true;
  var str="";
  var bep = myForm.getItemValue("bep_pack");
  var expmode = myForm.getItemValue("exp_mode");
  set_errlbl(false,myForm,"bep_pack");
  if (expmode == "CC") {
    if (bep == "VF") {
      set_errlbl(true,myForm,"bep_pack");
      str += myForm.getItemLabel("bep_pack") + " VF is not allowed for CC Exposure Mode";
      retval=false;
    }
  }

  set_status("<span class='errlbl'>" + str + "</span>");
  return retval;
}

function validate_acis() {
  var retval=true;
 
  var str="";
  
  if (! validate_expmode()) 
     return false;

  str += validate_ccds();
  set_errlbl(false,myForm,"multiple_spectral_lines");
  {
    var gd = myForm.getItemValue("detector") + "/";
    gd += myForm.getItemValue("grating");
    if (gd == "ACIS-I/NONE") {
      if (myForm.getItemValue("spectra_max_count") == "") {
        set_errlbl(true,myForm,"spectra_max_count");
        str += myForm.getItemLabel("spectra_max_count") + " is required for " + gd + "</br>";
        retval=false;
      } 
      else if (!validate_range(myForm,"spectra_max_count")) {
        set_errlbl(true,myForm,"spectra_max_count");
        str += myForm.getItemLabel("spectra_max_count") + ": " + get_range("spectra_max_count") + "<br>";
        retval=false;
      } 
      if (myForm.getItemValue("multiple_spectral_lines") == "") {
        set_errlbl(true,myForm,"multiple_spectral_lines");
        str += myForm.getItemLabel("multiple_spectral_lines") + " is required for " + gd + "</br>";
        retval=false;
      } 
    } 
  } 
  
  if (str.length > 1) {
    set_status("<span class='errlbl'>" + SAVE_FAILED +  str + "</span>");
    retval=false;
  }

  return retval;
}

function validate_ccds()
{
  var str="";
  var retval=true;
  var optccd = {};
  var ccds = new Array("ccdi0_on","ccdi1_on","ccdi2_on","ccdi3_on",
   "ccds0_on","ccds1_on","ccds2_on","ccds3_on","ccds4_on","ccds5_on");
  var on_cnt=0;
  var opt_cnt=0;

  set_errlbl(false,myForm,"ccdlbl");
  set_errlbl(false,myForm,"chipsel");

  for (var ii=0;ii<ccds.length;ii++) {
    var ele = myForm.getItemValue(ccds[ii]);
    if (ele == "Y") {
      on_cnt +=1;
    } else if (ele.indexOf('O') ==0) {
      opt_cnt +=1;
      if (!optccd[ele])
         optccd[ele] = 1;
      else
         optccd[ele] += 1;
    }
  }
  if (on_cnt == 0) {
    set_errlbl(true,myForm,"ccdlbl");
    str += ccdLblStr + " At least 1 chip MUST be required(Y).<br>";
    retval=false;
  }else if ((on_cnt + opt_cnt) > 6) {
    set_errlbl(true,myForm,"ccdlbl");
    str += ccdLblStr + " Maximum of 6 chips can be selected.<br>";
    retval=false;
  }else if (on_cnt > 4) {
    set_errlbl(true,myForm,"ccdlbl");
    str += ccdLblStr + " Maxiumum of 4 chips can be required.  Additional selections must be Optional.<br>";
    retval=false;
  } else if (opt_cnt > 0) {
    // optional chips must be unique
    for (ii=1;ii<6;ii++) {
      var ele = "O" + ii;
      if (optccd[ele] && optccd[ele] > 1) {
        set_errlbl(true,myForm,"ccdlbl");
        str += ccdLblStr + " Optional Chips must be unique. Off" + ii + " was used multiple times.<br>";
        retval=false;
      }
      if (optccd[ele] && optccd[ele] > 0 && ii >1) {
        var jj=ii-1;
        var ele2 = "O" + jj;
        if (!optccd[ele2] || optccd[ele2] < 1) {
          set_errlbl(true,myForm,"ccdlbl");
          str += ccdLblStr + " Optional Chips must be sequential. Off" + jj + " is missing.<br>";
          retval=false;
        }
      }
    }
  }
  if (on_cnt > 1 || opt_cnt > 0) {
    var chip_sel = myForm.getItemValue("chipsel");
    if (chip_sel == null || chip_sel != "Y")  {
      str+= myForm.getItemLabel("chipsel") + "<br>";
      set_errlbl(true,myForm,"chipsel");
      retval=false;
    }
  }

  return str;
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
  var str = myForm.getItemValue("detector") + "/";
  str += myForm.getItemValue("grating");
  myForm.setItemLabel("fslabel",str);
  if (str == "ACIS-I/NONE") {
    myForm.showItem("acisiblk");
  } else {
    myForm.hideItem("acisiblk");
    myForm.setItemValue("spectra_max_count","");
    myForm.setItemValue("multiple_spectral_lines","");
  }
  handle_change("exp_mode",null,null,DONOTCLEARSTATUS);

  var isEdit = myForm.getItemValue("isEdit");
  set_editButtons(myForm,isEdit);
  cps_style_load();
  myForm.setTooltip("ccdlbl","Chips marked Y will be on. Chips marked N will be off. Off1-9 could be turned off, where 1 is the first chip to be turned off.");


  setChanges(0)

} 

function loadPage() {
  var url ="/cps-app/cps_loadtgt?page=ACISREQ&pid=" + pid + "&tid=" +tid;
  myForm.load(url,postLoad);
}

function doOnLoad() {
  var lblW=150;
  var formData=[
           {type:"settings",position:"label-left",labelWidth:"auto"},
           {type:"input",hidden:true,name:"operation",value:""},
           {type:"input",hidden:true,name:"pid",value:""},
           {type:"input",hidden:true,name:"tid",value:""},
           {type:"input",hidden:true,name:"isEdit",value:""},
           {type:"input",hidden:true,name:"page",value:"ACISREQ"},
           {type:"input",hidden:true,name:"tgtnbr",value:""},
           {type:"input",hidden:true,name:"propno",value:""},
           {type:"input",hidden:true,name:"emsg",value:""},
           {type:"input",hidden:true,name:"detector",value:"ACIS-I"},
           {type:"input",hidden:true,name:"grating",value:"None"},
           {type:"input",hidden:true,name:"chips",value:"None"},
           {type:"fieldset",name:"fslabel",label:"Required Parameters", list:[
               {type:"block",list:[
                 {type:"select", name: "exp_mode",className:"reqLbl", label:"<a href='javascript:openHelpWindow(\"#ExposureMode\")'>Exposure Mode:</a>",labelWidth:lblW,options:[
                    {value:"TE",text:"Timed Exposure (TE)",checked:true},
                    {value:"CC",text:"Continuous Clocking(CC)"},
		 ]},
                 {type:"newcolumn",offset:20},
                 {type:"label",name:"ccwarn",label:"<span class='errmsg'>WARNING: CC mode will not result in an image.</span>"},
               ]},
               {type:"block",list:[
                  {type:"select", name: "bep_pack", className:"reqLbl",label: "<a href='javascript:openHelpWindow(\"#BEPPacking\")'>Event Telemetry :</a>" ,labelWidth:lblW, options:[
		   {value:"F",text:"Faint (TE,CC)",checked:true},
		   {value:"VF",text:"Very Faint (TE)"},
		   {value:"G",text:"Graded (TE,CC)"},
		  ]},
               ]},
      
               {type:"block",list:[
                  
                  {type:"label", name: "ccdlbl", label: "<a class='reqLbl' href='javascript:openHelpWindow(\"#CCDsOn\")'>CCDs On:</a><span style='padding-left:10px;' class='fldhelp'>Y (Required), N (turn off) or Off# (Optional with Turn-off order). Empty values will be read as N.<br>For example, Off1 should be the first optional CCD, meaning that it should be the first optional CCD to be turned off. </span>"},
                  ]},
                  {type:"block",list:[
                    {type:"select",label:"&nbsp;",className:"ccdshidden"},
                    {type:"select",label:"&nbsp;",className:"ccdshidden"},
                    {type:"select",name:"ccds0_on",label:"S0",className:"ccds"},
                    {type:"newcolumn",offset:0},
                    {type:"select",label:"&nbsp;",className:"ccdshidden"},
                    {type:"select",label:"&nbsp;",className:"ccdshidden"},
                    {type:"select",name:"ccds1_on",label:"S1",className:"ccds"},
                    {type:"newcolumn",offset:0},
                    {type:"select",name:"ccdi0_on",label:"I0",className:"ccds"},
                    {type:"select",name:"ccdi2_on",label:"I2",className:"ccds"},
                    {type:"select",name:"ccds2_on",label:"S2",className:"ccds"},
                    {type:"newcolumn",offset:0},
                    {type:"select",name:"ccdi1_on",label:"I1",className:"ccds"},
                    {type:"select",name:"ccdi3_on",label:"I3",className:"ccds"},
                    {type:"select",name:"ccds3_on",label:"S3",className:"ccds"},
                    {type:"newcolumn",offset:0},
                    {type:"select",label:"&nbsp;",className:"ccdshidden"},
                    {type:"select",label:"&nbsp;",className:"ccdshidden"},
                    {type:"select",name:"ccds4_on",label:"S4",className:"ccds"},
                    {type:"newcolumn",offset:0},
                    {type:"select",label:"&nbsp;",className:"ccdshidden"},
                    {type:"select",label:"&nbsp;",className:"ccdshidden"},
                    {type:"select",name:"ccds5_on",label:"S5",className:"ccds"},
                   ]},
               {type:"block", name:"chipselblk",list:[
 {type:"select", name: "chipsel", label: "<a href='javascript:openHelpWindow(\"#confirmChip\")'>I have reviewed the selection of ACIS chips and identified any chip whose operation is optional:</a><br><span class='fldhelp'> This field is required if more than 1 chip has been selected. </span>",options:[
	{value:"",text:""},
	{value:"Y",text:"Yes"},
	]},
 ]},
               {type:"block",name:"acisiblk",list:[
                  {type:"label",name:"acisilbl",label:"If you are using ACIS-I with no grating"},
                  {type:"block",name:"acisiblkx",list:[

                    {type:"input", name: "spectra_max_count", className:'reqLbl',label: "<a href='javascript:openHelpWindow(\"#Spectra\")'>What is the maximum number of counts you expect in any of the<br> spectra you will analyze from this observation?</a>",inputWidth:50},
                   {type:"select", name: "multiple_spectral_lines",className:"reqLbl", label:"<a href='javascript:openHelpWindow(\"#MultSpectral\")'>Do you expect to analyze more than 2 resolved spectral lines in that spectrum:</a>",options:[
                    {value:"",text:" ",checked:true},
                    {value:"Y",text:"Yes"},
                    {value:"N",text:"No"}
		   ]}
                 ]}
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

         myForm.attachEvent("onBeforeChange",before_change);
         myForm.attachEvent("onChange",handle_change);
         myForm.attachEvent("onButtonClick",function(id){
            clear_status();
            if (id=="save") save_changes();
            else if (id=="discard") reset_fields();
         });

         buildCCDs(myForm.getOptions("ccdi0_on"));
         buildCCDs(myForm.getOptions("ccdi1_on"));
         buildCCDs(myForm.getOptions("ccdi2_on"));
         buildCCDs(myForm.getOptions("ccdi3_on"));
         buildCCDs(myForm.getOptions("ccds0_on"));
         buildCCDs(myForm.getOptions("ccds1_on"));
         buildCCDs(myForm.getOptions("ccds2_on"));
         buildCCDs(myForm.getOptions("ccds3_on"));
         buildCCDs(myForm.getOptions("ccds4_on"));
         buildCCDs(myForm.getOptions("ccds5_on"));
         set_status("");
         loadPage();
}   
