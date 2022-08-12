var myForm;

function ValidSIM(data)
{
  var ptype = myForm.getItemValue("type");
  var retval=true;
  if (ptype == null || ptype.indexOf("CAL") < 0)  {
      var detector = myForm.getItemValue("detector");
      var rlbl="sim_trans_offset_" + detector;
      retval = validate_range_special(myForm,rlbl,"sim_trans_offset");
    }
  else  {
    retval= validate_range(myForm,"sim_trans_offset");
  }
  return retval;
}

function ValidYOffset(data)
{
  var retval=true;
  var ptype = myForm.getItemValue("type");
  if (ptype == null || ptype.indexOf("CAL") < 0)  {
    var detector = myForm.getItemValue("detector");
    var rlbl="y_det_offset_" + detector;
    retval = validate_range_special(myForm,rlbl,"y_det_offset");
  } else {
    retval= validate_range(myForm,"y_det_offset");
  }
  return retval;
}
function ValidZOffset(data)
{
  var retval= validate_range(myForm,"z_det_offset");
  if (retval && data && data.toString().trim().length >0){
    var ptype = myForm.getItemValue("type");
    if (ptype == null || ptype.indexOf("CAL") < 0) {
      var detector = myForm.getItemValue("detector");
      var rlbl="z_det_offset_" + detector;
      retval = validate_range_special(myForm,rlbl,"z_det_offset");
    }
  }
  return retval;
}

function ValidMag(data)
{
  return validate_range(myForm,"vmagnitude");
}
function clear_errors()
{
  var lbls = new Array("targname","ss_object","poslbl",
        "ra","dec","photo1","offsetlbl","vmagnitude",
        "y_det_offset","z_det_offset","sim_trans_offset","offsetlbl",
	"gridlbl","grid_name","num_pointings","max_radius","pointing_constraint");
  //reset error labels
  for (var ii=0;ii<lbls.length;ii++) {
    set_errlbl(false,myForm,lbls[ii]);
  }
  clear_status();

}

function validate_tgt()
{
  var retval=true;
  var str = "";
  var val,val2;
  var range_fields = new Array("vmagnitude");
  var range_fields_special = new Array();

  clear_errors();

  var posflg =  myForm.getItemValue("targ_position_flag");
  var ra     =  myForm.getItemValue("ra");
  var dec    =  myForm.getItemValue("dec");
  var propType =  myForm.getItemValue("type");
  var detector =  myForm.getItemValue("detector");
  var point = myForm.getItemValue("pointing_constraint");
  if (propType.indexOf("CAL") < 0) {
     range_fields_special.push("y_det_offset");
     range_fields_special.push("z_det_offset");
     range_fields_special.push("sim_trans_offset");
  } else {
     range_fields.push("y_det_offset");
     range_fields.push("z_det_offset");
     range_fields.push("sim_trans_offset");
  }

  if (point === '')
  {
    str += "Must select option for: " + myForm.getItemLabel("pointing_constraint");
    set_errlbl(true, myForm, "pointing_constraint");
  }

  var isDDT = myForm.getItemValue("isDDT");
  if (posflg != "Y" && isDDT == "true") {
    str += myForm.getItemLabel("poslbl") + " must be Y for DDT proposals.<br>";
    set_errlbl(true,myForm,"poslbl");
  }
  else if (posflg != "Y" && 
      (propType.indexOf("TOO") < 0) && 
      (myForm.getItemValue("ss_object") == "NONE")) {
    str += myForm.getItemLabel("poslbl") + " must be a TOO proposal or Solar System Object must be selected.<br>";
    set_errlbl(true,myForm,"poslbl");
  }
  str += validate_yn(posflg,myForm,["poslbl","ra","dec"]);
  str += validate_yn(myForm.getItemValue("raster_scan"),myForm,["gridlbl","grid_name","num_pointings","max_radius"]);

  if ((myForm.getItemValue("ss_object") == "NONE") &&
      (myForm.getItemValue("targname").toString().trim().length == 0)) {
    str += myForm.getItemLabel("targname") + " is required.<br>";
    set_errlbl(true,myForm,"targname");
  }

  for (var ii=0;ii<range_fields.length;ii++) {
    if (!validate_range(myForm,range_fields[ii])) {
      set_errlbl(true,myForm,range_fields[ii]);
      str += myForm.getItemLabel(range_fields[ii]) + " is invalid. " + get_range(range_fields[ii]) + "<br>";
    }
  }
  for (var ii=0;ii<range_fields_special.length;ii++) {
    if (!validate_range_special(myForm,range_fields_special[ii]+"_"+detector,range_fields_special[ii])) {
      set_errlbl(true,myForm,range_fields_special[ii]);
      str += myForm.getItemLabel(range_fields_special[ii]) + " is invalid. " + get_range(range_fields_special[ii]+"_"+detector) + "<br>";
    }
  }

  str += validate_yn(myForm.getItemValue("photometry_flag"),myForm,["photo1","vmagnitude"]);
  if (myForm.getItemValue("ss_object").toUpperCase() != "NONE") {
    if (myForm.getItemValue("photometry_flag") == "Y") {
      str += myForm.getItemLabel("photo1") + " is not allowed if Solar System Object is specified.";
      set_errlbl(true,myForm,"photo1");
    }
  }
       

  var tgtset = myForm.getItemValue("tgtset");
  if (tgtset == "Default") {
      var tstr = "";
      tstr += validate_yn("N",myForm,["offsetlbl","y_det_offset"]);
      tstr += validate_yn("N",myForm,["offsetlbl","z_det_offset"]);
      tstr += validate_yn("N",myForm,["offsetlbl","sim_trans_offset"]);
      str += tstr.replace(/ set/g," Specify");
  }
  var raster_scan = myForm.getItemValue("raster_scan");
  if (raster_scan=="Y") {
    var pnt =parseFloat(myForm.getItemValue("num_pointings"));
    var rad =parseFloat(myForm.getItemValue("max_radius"));
    if (isNaN(pnt) || pnt < 2) {
      set_errlbl(true,myForm,"num_pointings");
      str += myForm.getItemLabel("num_pointings") + " must be 2 or more<br>";
    } 
    if (isNaN(rad) || rad <= 0) {
      set_errlbl(true,myForm,"max_radius");
      str += myForm.getItemLabel("max_radius") + " must be greater than 0<br>";
    } else {
      if (!isNaN(pnt) && rad > pnt) {
        set_errlbl(true,myForm,"max_radius");
        str += myForm.getItemLabel("max_radius") + " cannot exceed 1 degree times the number of pointings. <br>";
      }
    }
  }

  if (str.length > 1) {
    set_status("<span class='errlbl'>" + SAVE_FAILED +  str + "</span>");
    retval=false;
  }
  return retval;
}
  


function handle_change(id, val,state,test) 
{
  if (!test || test.indexOf(DONOTCLEARSTATUS) < 0)
    clear_success_status();

  if (id != "resolverList")
    setChanges(1);
  if (id=="tgtset" ) {
    var str=myForm.getCheckedValue(id);
    if ( str=="Specify") {
      myForm.showItem("oblock");
    } else {
      var chkarr = ["y_det_offset","z_det_offset","sim_trans_offset"];
      if ( check_fields(myForm,chkarr) == 0) {
        myForm.hideItem("oblock");
      }
    }
  } else if (id=="raster_scan" ) {
    var str=myForm.getCheckedValue(id);
    if ( str=="Y") {
      var mflag = myForm.getItemValue("tgtmon");
      if (mflag == "Y" || mflag=="P" || mflag=="F") {
        doAlert("Grids are not allowed for targets with Monitor/Followup observations.",alertReturn);
        myForm.setItemValue("raster_scan","N");
      }
      else 
        myForm.showItem("gridblk");
    } else {
      var chkarr = ["num_pointings","grid_name","max_radius"];
      if ( check_fields(myForm,chkarr) == 0) {
        myForm.hideItem("gridblk");
      }
    }
  } else if (id=="photometry_flag" ) {
    var str=myForm.getCheckedValue(id);
    if (str == "Y") {
      myForm.showItem("vmagblk");
    } else {
      var chkarr = ["vmagnitude"];
      if ( check_fields(myForm,chkarr) == 0) 
        myForm.hideItem("vmagblk");
    }
  } else if (id=="targ_position_flag") {
    var str=myForm.getCheckedValue(id);
    if (str == "N") {
      var chkarr = ["ra","dec"];
      if ( check_fields(myForm,chkarr) == 0)  {
        myForm.hideItem("rablk");
        myForm.hideItem("decblk");
      }
    } else {
      myForm.showItem("rablk");
      myForm.showItem("decblk");
    }
  }
  else if (id == "pointing_constraint") {
    val = myForm.getItemValue("pointing_constraint");
    if (val != null && val =="Y" )
      document.getElementById("offsetwarn").innerHTML ="This counts as a constraint.";
    else
      document.getElementById("offsetwarn").innerHTML ="";
  }

}

function postLoad()
{
  var propno = myForm.getItemValue("propno");
  if (propno.indexOf("Invalid") >= 0) {
    top.location.replace("/cps-app/prop_logout.jsp");
  }
  if (propno.indexOf("Error") >= 0) {
    var emsg = myForm.getItemValue("emsg");
    doAlert(emsg,badalertReturn);
    return;
  }

  var val=myForm.getItemValue("trigger_target")
  if (parent.tgttabbar) {
    var tabele =  parent.tgttabbar.tabs("t7");
    if (val != "Y")
      tabele.disable();
    else
      tabele.enable();
  }

  myForm.setItemLabel("tgtfs","Target " + myForm.getItemValue("tgtnbr"));

  // for offsets check which radio button to set
  var chkarr = ["y_det_offset","z_det_offset","sim_trans_offset"];
  if ( check_fields(myForm,chkarr) == 0) 
     myForm.setItemValue("tgtset","Default");
  else
     myForm.setItemValue("tgtset","Specify");


  handle_change("tgtset",null,null,DONOTCLEARSTATUS);
  handle_change("raster_scan",null,null,DONOTCLEARSTATUS);
  handle_change("photometry_flag",null,null,DONOTCLEARSTATUS);
  handle_change("targ_position_flag",null,null,DONOTCLEARSTATUS);
  var isEdit = myForm.getItemValue("isEdit");
  set_editButtons(myForm,isEdit);
  if (isEdit == "false") {
    myForm.disableItem("photometry_flag","Y");
    myForm.disableItem("photometry_flag","N");
    myForm.disableItem("raster_scan","Y");
    myForm.disableItem("raster_scan","N");
    myForm.disableItem("targ_position_flag","Y");
    myForm.disableItem("targ_position_flag","N");
    myForm.disableItem("tgtset","Default");
    myForm.disableItem("tgtset","Specify");
    myForm.disableItem("resolve");

  }

  cps_style_load();

  setChanges(0);
}

function loadPage() 
{
  var url ="/cps-app/cps_loadtgt?page=POINTING&pid=" + pid + "&tid=" +tid;
  myForm.load(url,postLoad);
}


function save_changes() 
{
  if (validate_tgt()) {
    myForm.setItemValue("operation","SAVE");
    myForm.disableItem("save");
    document.body.className = "waiting";
    myForm.send("/cps-app/cps_savetgt","post",function(name,retstr) {
      document.body.className = "";
      myForm.enableItem("save");
      if (process_save(retstr,false)) {
        var tid = myForm.getItemValue("tid");
        var tno = myForm.getItemValue("tgtnbr");
        var tname = myForm.getItemValue("targname");
        if (tname == null || tname == "") {
           tname = myForm.getItemValue("ss_object");
           if (tname == "NONE") tname = "";
        }
        // now reset the 'Switch to Target' text in case name changed
        var tstr= "#" + tno + ": " + tname;
        top.parent.tgtentry.updateOption(tid,tid,tstr);
         
        setChanges(0);
      }
    });
  }
}


function postResolve() 
{
  document.body.className = "";
  var errmsg = myForm.getItemValue("errmsg");
  if (errmsg.length > 1) {
    doAlert(errmsg,alertReturn);
  }
}

function resolve_name() 
{
  var targname= myForm.getItemValue("targname");
  var resList= myForm.getItemValue("resolverList");
  if (targname != null && targname.length > 1) {
    setChanges(1);
    document.body.className = "waiting";
    myForm.load("/cps-app/resolve_name?targname="+encodeURIComponent(targname)+"&resolverSelector=" + resList,postResolve);
  } else {
    doAlert("Target Name is empty.",alertReturn);
  }
}


function reset_fields() {
  clear_errors();
  loadPage();
}

function doOnLoad() {

    var formData = [
           {type:"settings",position:"label-left",labelWidth:"auto" },
           {type:"input",hidden:true,name:"operation",value:""},
           {type:"input",hidden:true,name:"isEdit",value:""},
           {type:"input",hidden:true,name:"pid",value:""},
           {type:"input",hidden:true,name:"tid",value:""},
           {type:"input",hidden:true,name:"page",value:"POINTING"},
           {type:"input",hidden:true,name:"tgtnbr",value:""},
           {type:"input",hidden:true,name:"type",value:""},
           {type:"input",hidden:true,name:"detector",value:""},
           {type:"input",hidden:true,name:"propno",value:""},
           {type:"input",hidden:true,name:"tgtmon",value:""},
           {type:"input",hidden:true,name:"errmsg",value:""},
           {type:"input",hidden:true,name:"emsg",value:""},
           {type:"input",hidden:true,name:"isDDT",value:""},
           {type:"input",hidden:true,name:"trigger_target",value:""},
           {type:"fieldset",name:"tgtfs",label:"Target " ,list:[
             {type:"block",list:[
                 {type:"input",name:"targname",label:"<a href='javascript:openHelpWindow(\"#TargetName\")'>Target Name:</a>",maxLength:100},
                 {type:"newcolumn"},
                 {type:"select", name:"resolverList",label:"",options:[
                 {text:"SIMBAD/NED",value:"SIMBAD/NED"},
                 {text:"NED/SIMBAD",value:"NED/SIMBAD"},
                 {text:"SIMBAD",value:"SIMBAD"},
                 {text:"NED",value:"NED"}
               ]},
               {type:"newcolumn"},
               {type:"button", name:"resolve", value:"Resolve Name",offsetTop:2}
             ]},
           {type:"block",list:[
               {type:"label",label:"<span class='fldhelp'>If your target" +
                       " name can not be resolved into coordinates using" +
                       " the button above, please follow " +
                       "<a href='javascript:openHelpWindow(\"#TargetName\")'>CXC recommendations</a>" +
                       " on choosing an alternative, possibly resolvable, target name.</span>"},
               {type:"select",name:"ss_object",label:"<a href='javascript:openHelpWindow(\"#SolarSystemObject\")'>Solar System Object:</a>",options:[
		{value:"NONE",text:"None"},
		{value:"ASTEROID",text:"Asteroid"},
		{value:"COMET",text:"Comet"},
		{value:"EARTH",text:"Earth"},
		{value:"JUPITER",text:"Jupiter"},
		{value:"MARS",text:"Mars"},
		{value:"MOON",text:"Moon"},
		{value:"NEPTUNE",text:"Neptune"},
		{value:"PLUTO",text:"Pluto"},
		{value:"SATURN",text:"Saturn"},
		{value:"URANUS",text:"Uranus"},
		{value:"VENUS",text:"Venus"},
           ]}
        ]},
        {type:"block",list:[
           {type:"label",name:"poslbl",label:"<a href='javascript:openHelpWindow(\"#TargetPosition\")'>Is Target Position known and fixed?</a>"},
          {type:"newcolumn",offset:20},
          {type:"radio",name:"targ_position_flag",label:"Yes",value:"Y",position:"label-right",checked: true},
          {type:"newcolumn",offset:20},
          {type:"radio",name:"targ_position_flag",label:"No",value:"N",position:"label-right",checked: false}
        ]},
        {type:"block",name:"rablk",list:[
           {type:"input",offsetLeft:20,className:"reqLbl",name:"ra",label:"<a href='javascript:openHelpWindow(\"#R.A.\")'>RA:</a>",maxLength:15},
          {type:"newcolumn",offset:20},
           {type:"label",className:"fldhelp",label:"<span class='fldhelp'>hh mm ss.s or ddd.dddd</span>"},
        ]},
        {type:"block",name:"decblk",list:[
           {type:"input",offsetLeft:20,name:"dec",className:"reqLbl",label:"<a href='javascript:openHelpWindow(\"#Dec\")'>Dec:</a>",maxLength:15},
          {type:"newcolumn",offset:20},
           {type:"label",className:"fldhelp",label:"<span class='fldhelp'>+/-dd mm ss.s or +/-dd.dddd</span>"},
        ]} ,
        {type:"block",offsetTop:0,name:"ecblk",list:[
          {type:"label",offsetTop:1,offsetLeft:20,name:"ecliptic",className:"instlbl",label:"Time awarded to targets at <a href='javascript:openHelpWindow(\"#Ecliptic\")'>high ecliptic latitudes</a> will be limited." },
        ]},
        {type:"block",offsetTop:5,list:[
          {type:"label", name: "photo1", label: "<a href='javascript:openHelpWindow(\"#photometry\")'>Do you want optical monitor data for your target?</a>" },
          {type:"newcolumn",offset:20},
          {type:"radio",name:"photometry_flag",label:"Yes",value:"Y",position:"label-left",checked: false},
          {type:"newcolumn",offset:20},
          {type:"radio",name:"photometry_flag",label:"No",value:"N",position:"label-left",checked: true}
        ]},
               {type:"block", name:"vmagblk",list:[
                  {type:"input", offsetLeft:20,name: "vmagnitude", label:"<a href='javascript:openHelpWindow(\"#VMag\")'>V Magnitude of Target:</a>",validate:"ValidMag",inputWidth:50 },
               ]},

       ]},
      {type:"fieldset",name:"const",label:"Offsets ",list:[
        {type:"block",list:[
          {type:"label",name:"offsetlbl",label:"<a href='javascript:openHelpWindow(\"#TargetOffsets\")'>Offsets:<a>"},
          {type:"newcolumn",offset:20},
          {type:"radio",name:"tgtset",label:"Use Defaults",value:"Default",position:"label-right",checked: true},
          {type:"newcolumn",offset:20},
          {type:"radio",name:"tgtset",label:"Specify",value:"Specify",position:"label-right",checked: false}
        ]},
        {type:"block",name:"oblock",list:[
          {type:"block",name:"yblock",list:[
          {type:"input",name:"y_det_offset",label:"<a href='javascript:openHelpWindow(\"#TargetOffsets\")'>Y Detector Offset:</a>",labelWidth:"150",validate:"ValidYOffset"},
          {type:"newcolumn",offset:20},
           {type:"label",className:"fldhelp",label:"<span class='fldhelp'>arcmin</span>"},
          ]},
          {type:"block",name:"zblock",list:[
            {type:"input",name:"z_det_offset",label:"<a href='javascript:openHelpWindow(\"#TargetOffsets\")'>Z Detector Offset:</a>",labelWidth:"150",validate:"ValidZOffset"},
            {type:"newcolumn",offset:20},
            {type:"label",className:"fldhelp",label:"<span class='fldhelp'>arcmin</span>"},
          ]},
          {type:"block",name:"sblock",list:[
            {type:"input",name:"sim_trans_offset",label:"<a href='javascript:openHelpWindow(\"#SIMTransOffset\")'>SIM Translation Offset:</a>",labelWidth:"150",validate:"ValidSIM"},
            {type:"newcolumn",offset:20},
            {type:"label",className:"fldhelp",label:"<span class='fldhelp'>mm</span>"},
          ]}  
        ]},
        {type:"block",list:[
          { type: "select", name: "pointing_constraint",className: 'reqLbl', offsetLeft:5,label:"<a href='javascript:openHelpWindow(\"#OffsetConstraint\")'>Does the offset/pointing need to be adjusted once the observing roll angle is determined?</a><br><span id=\"offsetwarn\" style=\"color:red;font-size:small;\"></span> ",
          options:[
            { value:'', text: ' ', selected:true, disabled:true, hidden:true}]
         },
          ]},
      ]},
      {type:"fieldset",name:"gridset",label:"Grid",list:[
        {type:"block",list:[
    {type:"label",name:"gridlbl",label:"<a href='javascript:openHelpWindow(\"#Grid\")'>Is this Observation (part of) a Grid?</a>"},
          {type:"newcolumn",offset:20},
          {type:"radio",name:"raster_scan",label:"Yes",value:"Y",position:"label-right",checked: false},
          {type:"newcolumn",offset:20},
          {type:"radio",name:"raster_scan",label:"No",value:"N",position:"label-right",checked: true}
        ]},
        {type:"block", name:"gridblk", list:[
           {type:"label",className:"fldhelp",label:"Any observation intended to be part of a grid must have other similar pointings within 1 degree to qualify for a reduced slew tax. To be clear, in the final grid, no pointing may be farther than 1 degree from its nearest neighbor. Individual pointings within a grid may be entered as separate targets. Alternately, for a large number of pointings with identical observing parameters, you may specify all pointings by entering the average R.A. and Dec of the grid pointings in the R.A. and Dec fields above, and then also specifying the 3 general grid parameters below. Proposer MUST provide a detailed description of the grid positions on the sky in the science justification (preferably a figure)."},
           {type:"block", list:[
             {type:"input",name:"grid_name",offsetLeft:20,label:"<a href='javascript:openHelpWindow(\"#GridName\")'>Unique Grid Name:</a>", labelWidth:150,maxLength:30},
             {type:"input",name:"num_pointings",offsetLeft:20,label:"<a href='javascript:openHelpWindow(\"#GridPointings\")'>Number of Pointings?</a>",labelWidth:150,inputWidth:50,validate:"ValidInteger"},
            ]},
           {type:"block", list:[
             {type:"input",name:"max_radius",offsetLeft:20,label:"<a href='javascript:openHelpWindow(\"#GridRadius\")'>Distance from center of farthest grid pointing:</a>",validate:"ValidNumeric"},
             {type:"newcolumn",offset:20},
             {type:"label",className:"fldhelp",label:"<span class='fldhelp'>degrees</span>"},
          ]}
        ]}
     ]},
    {type:"container",name:"status",className:"statusContainer"},
    {type:"block",offsetLeft:150,offsetTop:15,list:[
        {type:"button",name:"save",value:"Save Changes"},
        {type:"newcolumn"},
        {type:"button",name:"discard",value:"Discard Changes"},
        {type:"newcolumn"}
    ]}

];
         myForm = new dhtmlXForm("formB_container",formData);
         //obj = document.getElementById("formB_container");
         //obj.style.display=style_init;

         myForm.setSkin('dhx_skyblue');
         myForm.hideItem("oblock");
         myForm.hideItem("vmagblk");
         // if TOO also hide grid question!
         myForm.hideItem("gridblk");
         buildTooltips(myForm);
         buildYN(myForm.getOptions("pointing_constraint"),0);


         myForm.attachEvent("onAfterValidate", after_validate);
         myForm.attachEvent("onChange", handle_change);
         myForm.attachEvent("onButtonClick",function(id){
            clear_status();
            if (id=="resolve") resolve_name();
            else if (id=="save") save_changes();
            else if (id=="discard") reset_fields();
         });
         myForm.attachEvent("onBeforeChange",function(id) {
           if (myForm.getItemValue("isEdit") == "false")
             return false;
           else
             return true;
         });

         myForm.attachEvent("onValidateError", handle_error);
         myForm.enableLiveValidation(true);
         loadPage();
}   
