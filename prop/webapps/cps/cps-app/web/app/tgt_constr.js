var myForm,wingrid,rollgrid;
var use_format = "%d-%M-%Y %H:%i",lblW=175;
var maxWin=20, maxRoll=20;
var rollAngCol=2;rollTolCol=3,roll180Col=4;

  
function selectAll(mygrid)
{
  mygrid.checkAll();
}
function deselectAll(mygrid)
{
  mygrid.uncheckAll();

}

var editWinId=0;
function confirmWinResult(result) {
  if (result) {
    wingrid.deleteRow(editWinId);
    isEmpty_win();
    setChanges(1);
    return false;
  }
}

function winedit_cell(stage,rowId,celInd,nval,oval) 
{
  editWinId=rowId;
  if (stage == 2) {
    if (celInd == 1)  {
      if (nval == 'N') {
        doConfirm("Are you sure you want to delete this window constraint?",confirmWinResult); 
        return false;
      } 
    }
  }
  if (stage != 0) setChanges(1);
  
  return true;
}
var editRollId = 0;
function confirmRollResult(result) {
  if (result) {
    rollgrid.deleteRow(editRollId);
    isEmpty_roll();
    setChanges(1);
    return false;
  }
}
function rolledit_cell(stage,rowId,celInd,nval,oval) 
{
  editRollId=rowId;
  var isvalid=true;
  if (stage == 2) {
    if (celInd == 1)  {
      if (nval == 'N') {
        doConfirm("Are you sure you want to delete this roll constraint?",confirmRollResult); 
        return false;
      } 
    }
    if (celInd == rollAngCol || celInd == rollTolCol)  {
      isvalid = ValidRoll(nval);
      set_cell_attr(rollgrid,rowId,celInd,isvalid);
    }
  }
  if (stage != 0 ) setChanges(1);
  return true;
}


function show_calendar(myCal,row_id,col_ind)
{
  var dval = wingrid.cells(row_id,col_ind).getValue();
  if (dval == null || dval.length < 2) {
    // if window stop default to window start
    if (col_ind == 3)
      dval = wingrid.cells(row_id,2).getValue();
  }
  
  if (dval == null || dval.length < 2) 
    wingrid.cells(row_id,col_ind).setValue(myForm.getItemValue("ao_start"));
  else
    wingrid.cells(row_id,col_ind).setValue(dval);


  return true;
}

function ValidRoll(data) {
   var retval = true;
   if (data && data.toString().trim().length >0) {
     var x =parseFloat(data) ;
     if (isNaN(x) || x != data.toString().trim())
        retval = false;
     else if (x < 0 || x > 360)
        retval = false;
   } else {
     retval = false; // required
   }
   return retval;
}

function clear_errors()
{
  var lbls = new Array("phase_constraint_flag","coord_constraint","group_constraint","phase_period","phase_epoch","phase_start","phase_start_margin","phase_end","phase_end_margin","phase_unique","group_id","group_interval","coord_interval","coord_obs");
  //reset error labels
  for (var ii=0;ii<lbls.length;ii++) {
    set_errlbl(false,myForm,lbls[ii]);
  }
  clear_status();
}

function validate_constraints()
{
  var retval=true;
  var str = "";
  var range_fields = new Array( "phase_start", "phase_start_margin",
"phase_end", "phase_end_margin", "group_interval", "coord_interval");

  clear_errors();

  if (!ValidEpoch(myForm.getItemValue("phase_epoch"))) {
    set_errlbl(true,myForm,"phase_epoch");
    str += "Phase Epoch: Value must be within 5 years of current date.<br>";
  }

  for (var ii=0;ii<range_fields.length;ii++) {
    if (!validate_range(myForm,range_fields[ii])) {
      set_errlbl(true,myForm,range_fields[ii]);
      str += myForm.getItemLabel(range_fields[ii]) + " is invalid. " + get_range(range_fields[ii]) + "<br>";
    }
  }

  var flg=myForm.getItemValue("phase_constraint_flag");
  str += validate_yn(flg,myForm,["phase_constraint_flag","phase_period","phase_epoch","phase_start","phase_start_margin","phase_end","phase_end_margin","phase_unique"]);
  flg=myForm.getItemValue("group_constraint");
  str += validate_yn(flg,myForm,["group_constraint","group_id","group_interval"]);
  flg=myForm.getItemValue("coord_constraint");
  str += validate_yn(flg,myForm,["coord_constraint","coord_interval","coord_obs"]);


  var nrows = wingrid.getRowsNum();
  for (ii =0;ii < nrows; ii++) {
    var doit = true;
    if (wingrid.cellByIndex(ii,1).getValue() != "N") {
     for (var cc=1;cc<wingrid.getColumnsNum();cc++) {
      var val = wingrid.cellByIndex(ii,cc).getValue();
      var clbl = wingrid.getColumnLabel(cc);
      if (val == null || val.length < 1) {
         doit = false;
         str += "Window Constraint Row " + (ii+1) + ": " + clbl + " is required.<br>";
      }
     }
     if (doit) {
       //new Date(year, month, day, hours, minutes, seconds, milliseconds);
       var d1 = wingrid.cellByIndex(ii,2).getValue();
       var sdate = convertDate(d1);
       var d2 = wingrid.cellByIndex(ii,3).getValue();
       var edate = convertDate(d2)
       if (sdate > edate) {
         str += "Window Constraint Row " + (ii+1) + ": Window Stop must be greater than Window Start: " + d2 + "<br>";
       }
       var tday = new Date();
       if (sdate < tday.getTime()) {
         str += "Window Constraint Row " + (ii+1) + ": Window Start must be greater than current date.<br>";
       }
     }
   }
  }


  var nrows = rollgrid.getRowsNum();
  for (ii =0;ii < nrows; ii++) {
    if (rollgrid.cellByIndex(ii,1).getValue() != "N") {
      for (var cc=rollAngCol;cc<= rollTolCol;cc++) {
      var val = rollgrid.cellByIndex(ii,cc).getValue();
      var clbl = rollgrid.getColumnLabel(cc);
      if (!ValidRoll(val)) {
          str+="Roll Constraint Row " + (ii+1)+ ": " + clbl + " is invalid. Valid range is 0-360 degrees.<br>";
          retval = false;
      }
      }
      var val = rollgrid.cellByIndex(ii,roll180Col).getValue();
      var clbl = rollgrid.getColumnLabel(roll180Col);
      if (val == null || val.length < 1) {
         str += "Roll Constraint Row " + (ii+ 1) + ": " + clbl +  " is required.<br>";
      }
    }
  }

  
  if (str.length > 1) {
    set_status("<span class='errlbl'>" + SAVE_FAILED +  str + "</span>");
    retval=false;
  }

  return retval;

}

function ValidEpoch(data)
{
  var retval=true;
  var mjd= parseFloat(myForm.getItemValue("mjd"));
  var mjda = mjd + (365*6);
  var mjdb = mjd - (365*6);
  if (data && data.toString().trim().length >0) {
    var x =parseFloat(data) ;
    if (isNaN(x) || x != data.toString().trim()) {
      retval = false;
    } else {
      if (x < mjdb || x > mjda)
        retval = false;
    }
  }
  return retval;
   
}

function ValidCoordInterval(data)
{
  return validate_range(myForm,"coord_interval");
}
function ValidGroupInterval(data)
{
  return validate_range(myForm,"group_interval");
}
function ValidPhaseEnd(data)
{
  return validate_range(myForm,"phase_end");
}
function ValidPhaseStart(data)
{
  return validate_range(myForm,"phase_start");
}
function ValidPhaseStartErr(data)
{
  return validate_range(myForm,"phase_start_margin");
}
function ValidPhaseEndErr(data)
{
  return validate_range(myForm,"phase_end_margin");
}


function isEmpty_win()
{
  if (wingrid.getRowsNum()<1) {
    wingrid.setColumnHidden(0,true);
    var ele = wingrid.getCombo(1);
    ele.put("N","No Window Constraints");
    wingrid.addRow(0,["","N","",""]);
    wingrid.setEditable(false);
    wingrid.cells(0,1).setBgColor(DISABLED_CELL_BG);
    wingrid.setColspan(0,1,3);

  }
}

function isEmpty_roll()
{
  if (rollgrid.getRowsNum()<1) {
    rollgrid.setColumnHidden(0,true);
    var ele = rollgrid.getCombo(1);
    ele.put("N","No Roll Constraints");
    rollgrid.addRow(0,["","N","","","Y"]);
    rollgrid.setEditable(false);
    rollgrid.cells(0,1).setBgColor(DISABLED_CELL_BG);
    rollgrid.setColspan(0,1,4);

  }
}

function clear_win() {
  wingrid.clearSelection();
  wingrid.enableMultiselect(true);
  for (var ii=0;ii<wingrid.getRowsNum();ii++) {
    var chk=wingrid.cellByIndex(ii,0).getValue();
    if (chk == 1)
      wingrid.selectRow(ii,false,true);
  }
  wingrid.deleteSelectedRows();
  wingrid.enableMultiselect(false);
  isEmpty_win();
  setChanges(1);
}

function clear_roll() {
  rollgrid.clearSelection();
  rollgrid.enableMultiselect(true);
  for (var ii=0;ii<rollgrid.getRowsNum();ii++) {
    var chk=rollgrid.cellByIndex(ii,0).getValue();
    if (chk == 1)
      rollgrid.selectRow(ii,false,true);
  }
  rollgrid.deleteSelectedRows();
  rollgrid.enableMultiselect(false);
  isEmpty_roll();
  setChanges(1);
}


     function fs_check(val,fs)
      {
        if (val)
          myForm.showItem(fs);
        else
          myForm.hideItem(fs);
      }
      function handle_change(id)
      {
        var str="";
        if (id == "winconstr") {
          str=myForm.isItemChecked(id);
          fs_check(str,"fswin");
        } else if (id == "rollconstr") {
          str=myForm.isItemChecked(id);
          fs_check(str,"fsroll");
        } else if (id == "phconstr") {
          str=myForm.isItemChecked(id);
          fs_check(str,"fsphase");
        } else if (id == "grpconstr") {
          str=myForm.isItemChecked(id);
          fs_check(str,"fsgroup");
        } else if (id == "coconstr") {
          str=myForm.isItemChecked(id);
          fs_check(str,"fscoord");
        } else if (id == "group_id") {
           setChanges(1);
           var val = myForm.getItemValue("group_id");
           if (val.indexOf("_____") > 0) {
             var varr = val.split("_____");
             var cnt=varr[1];
             myForm.setItemValue("group_interval",cnt);
           
           }
        } else  {
         setChanges(1);
       }
      }


      // this one is to not allow hiding of field if values filled in
      function handle_change_NOTUSED(id) {
        var str=myForm.isItemChecked(id);
        var fs;
        var chkarr;
        var hideItem=0;
        if (id == "phconstr") {
          fs = "fsphase";
          chkarr = ["phase_epoch","phase_period","phase_start","phase_start_margin","phase_end","phase_end_margin"];
          if (!str && check_fields(myForm,chkarr) == 0) {
            hideItem=1;
          }
        }
        else if (id == "grpconstr") {
          fs = "fsgroup";
          chkarr = ["group_id","group_interval"];
          if (!str && check_fields(myForm,chkarr) == 0) {
            hideItem=1;
          }
        }
        else if (id == "coconstr") {
          fs = "fscoord";
          chkarr = ["coord_interval","coord_obs"];
          if (!str  && check_fields(myForm,chkarr) == 0) {
            hideItem=1;
         }
        }
        else if (id == "rollconstr") {
          fs = "fsroll";
          if (!str ) {
            hideItem=1;
          }
        }
        else if (id == "winconstr") {
          fs = "fswin";
          if (!str ) {
            hideItem=1;
          }
        }
        if (hideItem  )
          myForm.hideItem(fs);
        else {
          myForm.showItem(fs);
          myForm.checkItem(id);
        }
     }

function add_roll() {
  var rowcnt = rollgrid.getRowsNum();
  var cval = rollgrid.cellByIndex(0,1).getValue();
  var ele = rollgrid.getCombo(1);
  if (rowcnt == 1 && ele.get(cval).indexOf("No Roll") >= 0) {
    rollgrid.clearAll();
    rollgrid.setColumnHidden(0,false);
    // this changes the "No Roll Constraints" to plain No Constraint
    ele.put("N","No Constraint");
    rowcnt=0;
    setChanges(1);
  }
  rollgrid.setEditable(true);
  if (rowcnt < maxRoll) {
    setChanges(1);
    rowcnt += 1;
    rollgrid.addRow(rowcnt,["","Y","","",""]);
    //rollgrid.selectRowById(rowcnt);
    window.setTimeout(function(){
      rollgrid.selectCell(rowcnt-1,2,true,true,true);
      },1);
  }
  myForm.setItemFocus("addroll");
}


function add_win() {
  var rowcnt = wingrid.getRowsNum();
  var cval = wingrid.cellByIndex(0,1).getValue();
  var ele = wingrid.getCombo(1);
  if (rowcnt == 1 && ele.get(cval).indexOf("No Win") >= 0) {
    wingrid.clearAll();
    wingrid.setColumnHidden(0,false);
    // this changes the "No Window Constraints" to plain No Constraint
    ele.put("N","No Constraint");
    rowcnt=0;
    setChanges(1);
  }
  wingrid.setEditable(true);
  if (rowcnt < maxWin) {
    setChanges(1);
    rowcnt = rowcnt+1;
    wingrid.addRow(rowcnt,["","Y","",""]);
    wingrid.selectRowById(rowcnt);

    window.setTimeout(function(){
      wingrid.selectCell(rowcnt-1,2,false,true,true);
      },1);
  }
  
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

      var isEdit = myForm.getItemValue("isEdit");
      set_editButtons(myForm,isEdit);
      

      var val = myForm.getItemValue("group_id");
      var co = myForm.getCombo("group_id");
      if (val.length > 1)
        co.setComboText(val);

      if ((myForm.getItemValue("phase_constraint_flag") == "Y") ||
          (myForm.getItemValue("phase_constraint_flag") == "P")) {
        myForm.checkItem("phconstr");
        handle_change("phconstr");
      }
      if ((myForm.getItemValue("group_constraint") == "Y") ||
          (myForm.getItemValue("group_constraint") == "P")) {
        myForm.checkItem("grpconstr");
        handle_change("grpconstr");
      }
      if ((myForm.getItemValue("coord_constraint") == "Y") ||
          (myForm.getItemValue("coord_constraint") == "P")) {
     
        myForm.checkItem("coconstr");
        handle_change("coconstr");
      }
      var url ="/cps-app/cps_loadtgt?page=ROLL&pid=" + pid + "&tid=" +tid;
      rollgrid.load(url,postLoadroll,"json");
      var url ="/cps-app/cps_loadtgt?page=WINDOW&pid=" + pid + "&tid=" +tid;
      wingrid.load(url,postLoadwin,"json");

      setChanges(0);
      if (isEdit != "true") {
         wingrid.setEditable(false);
         rollgrid.setEditable(false);
         rollgrid.setColumnHidden(0,true);
         wingrid.setColumnHidden(0,true);
         myForm.hideItem("winbtns");
         myForm.hideItem("rollbtns");
      } 
        
         
      cps_style_load();

    }

    function postLoadroll() {
      if (rollgrid.getRowsNum() <= 0){
        isEmpty_roll();
      } else {
        myForm.checkItem("rollconstr");
        handle_change("rollconstr");
      }
      setChanges(0);
    }

    function postLoadwin() {
      if (wingrid.getRowsNum() <= 0){
        isEmpty_win();
      } else {
        myForm.checkItem("winconstr");
        handle_change("winconstr");
      }
      setChanges(0);
    }

    function loadPage() {
      rollgrid.clearAll();
      wingrid.clearAll();
      var url ="/cps-app/cps_loadtgt?page=CONSTRAINT&pid=" + pid + "&tid=" +tid;
      myForm.load(url,postLoad);

    }
           
    function save_changes() {
      myForm.setItemValue("operation","SAVE");
      var gridvalues= getGridValues(rollgrid);
      myForm.setItemValue("rollvalues",gridvalues);
      var gridvalues= getGridValues(wingrid);
      myForm.setItemValue("winvalues",gridvalues);
      if (validate_constraints()) {
        myForm.disableItem("save");
        document.body.className = "waiting";

        myForm.send("/cps-app/cps_savetgt","post",function(name,retstr) {
          document.body.className = "";
          myForm.enableItem("save");
          if (process_save(retstr,false))
            setChanges(0);
        });
      }
    }

    function reset_fields() {
       clear_errors();
       loadPage();
       setChanges(0);
    }

    function before_load(id,values) {
      // this handles case of the constraints that previously allowed preferred
      handle_preferred("coord_constraint",values);
      handle_preferred("group_constraint",values);
      handle_preferred("phase_constraint_flag",values);
      return true;
    }

    function doOnLoad() {
         var formData=[
           {type:"settings",position:"label-left"},
          {type:"input",hidden:true,name:"operation",value:""},
           {type:"input",hidden:true,name:"isEdit",value:""},
           {type:"input",hidden:true,name:"pid",value:""},
           {type:"input",hidden:true,name:"tid",value:""},
           {type:"input",hidden:true,name:"page",value:"CONSTRAINT"},
           {type:"input",hidden:true,name:"tgtnbr",value:""},
           {type:"input",hidden:true,name:"propno",value:""},
           {type:"input",hidden:true,name:"emsg",value:""},
           {type:"input",hidden:true,name:"ao_start",value:""},
           {type:"input",hidden:true,name:"rollvalues",value:""},
           {type:"input",hidden:true,name:"winvalues",value:""},
           {type:"input",hidden:true,name:"mjd",value:""},
              {type:"block",list:[
	         {type:"newcolumn",offset:20},
                 {type:"checkbox",name:"winconstr",label:"<a href='javascript:openHelpWindow(\"#WindowConstraints\")'>Window</a>",position:"label-top" },
	         {type:"newcolumn",offset:20},
                 {type:"checkbox",name:"rollconstr",label:"<a href='javascript:openHelpWindow(\"#RollConstraint\")'>Roll</a>",position:"label-top" },
	         {type:"newcolumn",offset:20},
                 {type:"checkbox",name:"phconstr",label:"<a href='javascript:openHelpWindow(\"#PhaseDependentObservation\")'>Phase:</a>",position:"label-top"},
                 {type:"newcolumn",offset:20},
                 {type:"checkbox",name:"grpconstr",label:"<a href='javascript:openHelpWindow(\"#GroupObservation\")'>Group</a>",position:"label-top" },
                 {type:"newcolumn",offset:20},
                 {type:"checkbox",name:"coconstr",label:"<a href='javascript:openHelpWindow(\"#Telescopes\")'>Coordinated</a>",position:"label-top" },
               ]},
              {type:"fieldset",name:"fswin",label:"Window Constraints",list:[
                {type:"label",label:"Use the largest viable window.  Do not enter window constraints merely to indicate periods of sunblock, as these are accounted for by Chandra Mission Planning. "},
                {type:"block",name:"clearbtns",offsetTop:0,position:"label-left",list:[
                  {type:"label",name:"selectall",label:"<div class='tinybtn'><a href='javascript:selectAll(wingrid);'>Select All<a></div>"},
                  {type:"newcolumn",offset:2},
                  {type:"label",name:"deselectall",label:"<div class='tinybtn'><a href='javascript:deselectAll(wingrid);'>Deselect All<a></div>"},
                  {type:"newcolumn",offset:10},
                  {type:"label",name:"wininstr",label:TGT_TBL_INSTR},
                ]},

                {type: "label",offsetTop:0,label:"<div id='wingrid' style='width:600'></div>"},
                {type:"block",name:"winbtns",list:[
                  {type:"button",name:"addwin",value:"Add Window Constraint",offsetLeft:30},
                  {type:"newcolumn",offset:20},
                  {type:"button",name:"clearwin",value:"Remove selected windows"},
                 ]},
               ]},
               {type:"fieldset",name:"fsroll",label:"Roll Constraints",list:[
                 {type:"label",label:"A roll constraint translates directly into a constraint on the day and time when an observation may be carried out. It should only be specified for cases in which a specific attitude is required to meet scientific objectives."},
                {type:"block",name:"clearbtnsr",offsetTop:0,position:"label-left",list:[
                  {type:"label",name:"selectallr",label:"<div class='tinybtn'><a href='javascript:selectAll(rollgrid);'>Select All<a></div>"},
                  {type:"newcolumn",offset:2},
                  {type:"label",name:"deselectallr",label:"<div class='tinybtn'><a href='javascript:deselectAll(rollgrid);'>Deselect All<a></div>"},
                  {type:"newcolumn",offset:10},
                  {type:"label",name:"rollinstr",label:TGT_TBL_INSTR},
                ]},
                 {type: "label",offsetTop:0,label:"<div id='rollgrid' style='width:600'></div>"},
                 {type:"block",name:"rollbtns",list:[
                   {type:"button",name:"addroll",value:"Add Roll Constraint",offsetLeft:30},
                   {type:"newcolumn",offset:20},
                   {type:"button",name:"clearroll",value:"Remove selected roll constraints"},
                ]},
              ]},
              {type:"fieldset",name:"fsphase",label:"Phase Constraints ",list:[
               {type:"label",label:"For Phase Dependent observations, the reference date (MJD) corresponding to a phase of 0.0. Observations will be made at an integral number of Periods from this date,plus offsets as needed to locate the observations within the specified phase range. The reference date must be within 5 years of the current date."},
               {type:"block",list:[
                  {type:"select",name:"phase_constraint_flag",label:"<a href='javascript:openHelpWindow(\"#PhaseDependentObservation\")'>Phase Constraint:</a>",labelWidth:lblW},
                  {type:"input",name:"phase_epoch",label:"<a href='javascript:openHelpWindow(\"#PhaseDependentEpoch\")'>Epoch (MJD):</a>",inputWidth:100,labelWidth:lblW,validate:"ValidEpoch"},
                  {type:"input",name:"phase_period",label:"<a href='javascript:openHelpWindow(\"#PhaseDependentPeriod\")'>Period (days):</a>",inputWidth:100,labelWidth:lblW,validate:"ValidNumeric"},
                  {type:"input",name:"phase_start",label:"<a href='javascript:openHelpWindow(\"#MinimumPhase\")'>Minimum Phase:</a>",inputWidth:100,labelWidth:lblW,validate:"ValidPhaseStart"},
                  {type:"input",name:"phase_start_margin",label:"<a href='javascript:openHelpWindow(\"#MinimumPhaseError\")'>Minimum Phase Error:</a>",inputWidth:100,labelWidth:lblW,validate:"ValidPhaseStartErr"},
                  {type:"input",name:"phase_end",label:"<a href='javascript:openHelpWindow(\"#MaximumPhase\")'>Maximum Phase:</a>",inputWidth:100,labelWidth:lblW,validate:"ValidPhaseEnd"},
                  {type:"input",name:"phase_end_margin",label:"<a href='javascript:openHelpWindow(\"#MaximumPhaseError\")'>Maximum Phase Error:</a>",inputWidth:100,labelWidth:lblW,validate:"ValidPhaseEndErr"},
                  {type:"select",name:"phase_unique",label:"<a href='javascript:openHelpWindow(\"#PhaseUnique\")'>Unique Phase:</a>",labelWidth:lblW,
                  options:[
                    { value:'', text: ' ', selected:true, disabled:true, hidden:true}]
                 },
               ]}
               ]},
              {type:"fieldset",name:"fsgroup",label:"Group Observations ",list:[
                {type:"label",label:"If this target needs to be observed within a relative time range with other targets in this proposal, enter a unique Group Identification to be used for all targets in the group. Enter the maximum time interval(days) that you wish all targets to be observed. This is often used for rasters or grids to facilitate alignment of roll angles. "},
               {type:"block",list:[
                  {type:"select",name:"group_constraint",label:"<a href='javascript:openHelpWindow(\"#GroupObservation\")'>Group Observation:</a>",labelWidth:lblW},
                  {type:"combo",name:"group_id",label:"<a href='javascript:openHelpWindow(\"#GroupIdentification\")'>Group Identification:</a>",inputWidth:100,maxLength:50,labelWidth:lblW,inputWidth:300},
               ]},
               {type:"block",list:[
                  {type:"input",name:"group_interval",label:"<a href='javascript:openHelpWindow(\"#GroupInterval\")'>Time Interval for Group:</a>",inputWidth:100,labelWidth:lblW,validate:"ValidGroupInterval"},
                   {type:"newcolumn",offset:20},
                   {type:"label",label:"(days)"}
               ]}
              ]},
              {type:"fieldset",name:"fscoord",label:"Coordinated Observatories ",list:[
                 {type:"label",label:"Specify the maximum permitted interval encompassing the Chandra observation and those of the coordinating observatory(ies). The minimum value is the duration of the longer of the observations of either of two coordinating observatories, which implies that the shorter observation will nest entirely within the longer one. For more than two coordinating observatories, provide details in the Remarks field and check 'Are there additional constraints or preferences in the remarks?'"},
                  {type:"select",name:"coord_constraint",label:"<a href='javascript:openHelpWindow(\"#Telescopes\")'>Must this observation be coordinated with that of another observatory?</a>"},
                  {type:"input",name:"coord_interval",label:"<a href='javascript:openHelpWindow(\"#TelescopesInterval\")'>Maximum time interval encompassing coordinated observations:</a>(days)",inputWidth:100,validate:"ValidCoordInterval"},
                  {type:"input",name:"coord_obs",label:"<a href='javascript:openHelpWindow(\"#Observatories\")'>Observatories:</a>",inputWidth:500,maxLength:255}
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
         buildYN(myForm.getOptions("phase_constraint_flag"),0);
         buildYN(myForm.getOptions("phase_unique"),0,1);
         buildYN(myForm.getOptions("group_constraint"),0);
         buildYN(myForm.getOptions("coord_constraint"),0);

         var grpurl ="/cps-app/cps_props?operation=GROUPOPTIONS&pid=" + pid;
         var grps= myForm.getCombo("group_id");
         grps.load(grpurl);
         grps.enableAutocomplete();
         grps.allowFreeText(true);

         
         myForm.hideItem("fswin");
         myForm.hideItem("fsroll");
         myForm.hideItem("fsphase");
         myForm.hideItem("fsgroup");
         myForm.hideItem("fscoord");
         myForm.enableLiveValidation(true);
         buildTooltips(myForm);

         myForm.attachEvent("onAfterValidate", function(status){
           if (!status) myForm.enableItem("save");
         });
         myForm.attachEvent("onBeforeDataLoad", before_load);
         myForm.attachEvent("onValidateError",handle_error);
         myForm.attachEvent("onChange",handle_change);
         myForm.attachEvent("onButtonClick",function(id){
            clear_status();
            if (id=="save") save_changes();
            else if (id=="discard") reset_fields();
            else if (id=="addwin") add_win();
            else if (id=="addroll") add_roll();
            else if (id=="clearwin") clear_win();
            else if (id=="clearroll") clear_roll();
         });

         wingrid=new dhtmlXGridObject('wingrid');
         wingrid.setHeader("Remove?,<a href='javascript:openHelpWindow(\"#WindowConstraint\")'>Constraint</a>,<a href='javascript:openHelpWindow(\"#WindowConstraintStart\")'>Window&nbsp;Start</a>,<a href='javascript:openHelpWindow(\"#WindowConstraintStop\")'>Window&nbsp;Stop</a>");
         wingrid.setInitWidths("70,150,150,150");
         wingrid.setColumnMinWidth("10,20,20,20");
         wingrid.setColAlign("center,left,left,left");
         wingrid.setSkin("dhx_skyblue");
         wingrid.setColTypes("ch,coro,dhxCalendar,dhxCalendar");
         wingrid.setDateFormat(use_format,use_format);
         // no sorting!
         wingrid.setImagePath(CSS_IMG_PATH);
         wingrid.enableColSpan(true);
         wingrid.enableAutoHeight(true,400);
         wingrid.enableAutoWidth(true);
         wingrid.enableRowsHover(true,"gridhover");
         wingrid.init();

         var ele = wingrid.getCombo(1);
         buildYN(ele,1);
         //ele.allowFreeText(false);
         wingrid.attachEvent("onCalendarShow",show_calendar);
         wingrid.attachEvent("onEditCell",winedit_cell);
         wingrid.attachEvent("onRowSelect",function (id,ind) {
             wingrid.editCell();
         });

         rollgrid=new dhtmlXGridObject('rollgrid');
         rollgrid.setHeader("Remove?,<a href='javascript:openHelpWindow(\"#RollFlag\")'>Constraint</a></span>,<a href='javascript:openHelpWindow(\"#RollAngle\")'>Roll&nbsp;Angle</a>,<a href='javascript:openHelpWindow(\"#RollAngle\")'>Roll&nbsp;Tolerance</a>,<a href='javascript:openHelpWindow(\"#Roll180\")'>180 Rotation?</a>");
         rollgrid.setInitWidths("70,150,150,150,150");
         rollgrid.setColumnMinWidth("10,20,20,20,20");
         rollgrid.setColAlign("center,left,left,left,left");
         rollgrid.setSkin("dhx_skyblue");
         rollgrid.setColTypes("ch,coro,ed,ed,coro");
         rollgrid.setImagePath(CSS_IMG_PATH);
         rollgrid.enableValidation(false,false,true,true,false);
         //rollgrid.setColValidators(",,ValidRoll,ValidRoll,");
         rollgrid.enableAutoHeight(true,300);
         rollgrid.enableAutoWidth(true);
         rollgrid.enableRowsHover(true,"gridhover");
         rollgrid.enableColSpan(true);

         ele= rollgrid.getCombo(1);
         buildYN(ele,1);
         var ele= rollgrid.getCombo(roll180Col);
         ele.put("N","No");
         ele.put("Y","Yes");
         rollgrid.attachEvent("onRowSelect",function (id,ind) {
             rollgrid.editCell();
         });
         rollgrid.attachEvent("onEditCell",rolledit_cell);
/*
         rollgrid.attachEvent("onValidationError",function(id,col,val,rule) {
            rollgrid.cells(id,col).setAttribute("invalid","true");
            rollgrid.cells(id,col).setTextColor('#dd0000');
            return true;
         });
         rollgrid.attachEvent("onValidationCorrect",function(id,col,val,rule) {
            rollgrid.cells(id,col).setAttribute("invalid","false");
            rollgrid.cells(id,col).setTextColor('#000000');
            return true;
         });
*/
         rollgrid.init();

         loadPage();
      }   


