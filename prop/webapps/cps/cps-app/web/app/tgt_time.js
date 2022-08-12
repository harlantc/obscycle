var monitorLbl="Monitor";
var myForm,mongrid,use_format="%Y-%m-%d";
var preMaxLead=MAX_PRE_LEAD;
var minLeadCol=3,maxLeadCol=4,targnumCol=5,splitIntCol=6;

function selectAll()
{
  mongrid.checkAll();
}
function deselectAll()
{
  mongrid.uncheckAll();
}
function ValidObsTime0(data) {
  set_errlbl(false,myForm,"obs_time");
  return ValidObsTime(data);
}
function ValidObsTime1(data) {
  set_errlbl(false,myForm,"obs_time_1");
  return ValidObsTime(data);
}
function ValidObsTime2(data) {
  set_errlbl(false,myForm,"obs_time_2");
  return ValidObsTime(data);
}
function ValidTargetNumber(data)
{
  var retval = true;
  if (data && data.toString().trim().length >0) {
    var x =parseFloat(data) ;
    if (isNaN(x)) retval = false;
    if (x < 0) retval=false;
  } else {
    retval=false;  // required
  }
  return retval;
}

function ValidMinLead(data,maxLead)
{
  var retval = true;
  if (data && data.toString().trim().length >0) {
    var x =parseFloat(data) ;
    var y =parseFloat(maxLead) ;
    if (isNaN(x)) retval = false;
    else if (x < 1 || x > preMaxLead) retval=false;
    else if (!isNaN(y) && x >= y) retval=false;
  } else {
    retval=false;  // required
  }
  return retval;
}
function ValidMaxLead(data,minLead)
{
  var retval = true;
  if (data && data.toString().trim().length >0) {
    var x =parseFloat(data) ;
    var y =parseFloat(minLead) ;
    if (isNaN(x)) retval=false;
    else if (x < 1 || x > preMaxLead) retval=false;
    else if (!isNaN(y) && x <= y) retval=false;
  } else {
    retval=false;  // required
  }
  return retval;
}

function ValidSplit(data,minLead,maxLead)
{
  var retval = true;
  if (data && data.toString().trim().length >0) {
    var split =parseFloat(data) ;
    var maxlead =parseFloat(maxLead) ;
    var minlead =parseFloat(minLead) ;
    if (isNaN(split)) retval=false;
    else if (split < 1 || split >= maxlead - minlead) retval=false;
  }
  else{
    retval=false;
  }
  return retval;
}
function days2ks(days){
  //86400 seconds / day
  var days_in_ks = days * 86.4;
  return days_in_ks
}

function ValidObsTime(data)
{
  var retval = true;
  var trig = myForm.getItemValue("trigger_target");
  var propType = myForm.getItemValue("type");
  var isDDT = myForm.getItemValue("isDDT");
  if (propType.indexOf("TOO") < 0 && isDDT == "false") {
    trig = "Y";
  }
  if (data && data.toString().trim().length >0) {
    var x =parseFloat(data) ;
    if (trig == 'N') {
      if (!(isNaN(x)) && x > 0) {
         retval=false;
      }
    } else if (isNaN(x) || x != data.toString().trim()) {
      retval = false;
    } else if (x < 1 || x > 10000) {
      retval = false;
    }
  }
  return retval;
}

function clear_errors()
{
  var lbls = new Array("obs_time","obs_time_1","obs_time_2",
	"mon_constr","triggerlbl","split_interval");
  //reset error labels
  for (var ii=0;ii<lbls.length;ii++) {
    set_errlbl(false,myForm,lbls[ii]);
  }
  clear_status();
}
function validate_time()
{
  var retval=true;
  var str = "";
  var obsarr = new Array("obs_time_1","obs_time_2");

  clear_errors();

  var trig = myForm.getItemValue("trigger_target");
  var tgtmon = myForm.getItemValue("tgtmon");
  var otime = myForm.getItemValue("obs_time");
  var val1 = myForm.getItemValue("obs_time_1");
  var val2 = myForm.getItemValue("obs_time_2");
  var splitint = myForm.getItemValue("split_interval");
  var unint = myForm.getItemValue("uninterrupt");
  var monflag = myForm.getItemValue("tgtmon");
  var propType = myForm.getItemValue("type");
  var isDDTorTOO = false
  if (propType.indexOf("TOO") >= 0 ||  myForm.getItemValue("isDDT") == "true"){
      isDDTorTOO = true;
  }

  // obs time required unless non-trigger TOO
  if (trig!= 'N')  {
    if (otime==null || otime.toString().trim() == "")  {
      arr = myForm.getItemLabel("obs_time").split("<br");
      str += "Missing required field: "  + arr[0] + "<br>";
      set_errlbl(true,myForm,"obs_time");
      retval = false;
    }
    else if (!validate_range(myForm,"obs_time")) {
       retval=false;
       str += myForm.getItemLabel("obs_time") + " " + get_range("obs_time") + "<br>";
    }
    if (!validate_range(myForm,"obs_time_1")) {
       retval=false;
       str += myForm.getItemLabel("obs_time_1") + " " + get_range("obs_time_1") + "<br>";
    }
    if (!validate_range(myForm,"obs_time_2")) {
       retval=false;
       str += myForm.getItemLabel("obs_time_2") + " " + get_range("obs_time_2") + "<br>";
    }
    var v1 = parseFloat(val1);
    var v2 = parseFloat(val2);
    var v12 = 0;
    if (!isNaN(v1)) v12 += v1;
    if (!isNaN(v2)) v12 += v2;
    if (v12 > otime) {
       retval=false;
       set_errlbl(true,myForm,"obs_time");
       str += myForm.getItemLabel("obs_time") + ": Projected Time must be <= Observing Time<br>";
    }
    // total exposure time <= split interval  <= 2 years
    if (splitint != ""){
      var splitInKs = days2ks(splitint);
      if (splitInKs < otime){
        set_errlbl(true,myForm,"split_interval");
        str += myForm.getItemLabel("split_interval") +
                                  " Split Interval must be >= Observing Time<br>"
      }
    }
    if (splitint > MAX_PRE_LEAD*2+1){
      set_errlbl(true,myForm,"split_interval");
      str += myForm.getItemLabel("split_interval") +
                                 " Split Interval must be < 2 years<br>"
    }
    // Can't be both split and uninterrupt
    if (splitint != "" && unint === 'Y'){
      set_errlbl(true,myForm,"split_interval");
      str += myForm.getItemLabel("split_interval") + " " +
             " Uninterrupt flag and Split Interval time are mutually exclusive<br>"
    }
  } else  {
    var ival = parseFloat(otime);
    if (!isNaN(ival) &&  ival != 0) {
      arr = myForm.getItemLabel("obs_time").split("<br");
      set_errlbl(true,myForm,"obs_time");
      str += myForm.getItemLabel("obs_time") + ": Observation time must be 0 for non-trigger target";
      retval = false;
    }
  }

  // No tgt split obs and mon split obs. Reset tgt split if mon, no error.
  if (monflag =='Y'){
    myForm.setItemValue("split_interval",null);

    }
  
  // check that sum of monitor/followups equals observing time
  if (tgtmon != "N") {
    var hdrs = new Array("","","Exposure Time","Minimum Time Interval","Maximum Time Interval","Target Number","Split Interval");
    var tottime= 0;
    var nrows = mongrid.getRowsNum();
    for (var ii =0;ii < nrows; ii++) {
      var badrow =0;
      var cc=2;
      var cval = parseFloat(mongrid.cellByIndex(ii, cc).getValue());
      if (!isNaN(cval) && cval > 0) {
        tottime += cval;
        if (cval >= otime && otime > 0 )  {
          str+="<span class='errlbl'>" + monitorLbl + ":</span>Order " + mongrid.cellByIndex(ii,1).getValue()  +  ": " + hdrs[cc] + " must be less than the Observing Time<br>";
          retval=false;
        }
        else if (cval== 0) {
          str+="<span class='errlbl'>" + monitorLbl + ":</span>Order " + mongrid.cellByIndex(ii,1).getValue()  +  ": " + hdrs[cc] + " must be > 0<br>";
          retval=false;
        }
      } else {
          str+="<span class='errlbl'>" + monitorLbl + ":</span>Order " + mongrid.cellByIndex(ii,1).getValue()  +  ": " + hdrs[cc] + " must be > 0<br>";
      }

      if (ii > 0) {
        cc=minLeadCol;
        if (!ValidMinLead(mongrid.cellByIndex(ii,cc).getValue())) {
          str+="<span class='errlbl'>" + monitorLbl + ":</span>Order  " + mongrid.cellByIndex(ii,1).getValue() + ": " + hdrs[cc] + " is invalid. Valid range is 1--" + preMaxLead + " days.<br>";
          retval = false;
        }
        cc=maxLeadCol;
        if (!ValidMaxLead(mongrid.cellByIndex(ii,cc).getValue(),mongrid.cellByIndex(ii,cc-1).getValue())) {
          str+="<span class='errlbl'>" + monitorLbl + ":</span>Order  " + mongrid.cellByIndex(ii,1).getValue() + ": " + hdrs[cc] + " is invalid. Valid range is (Minimum Time Interval +1) - " + preMaxLead + " days.<br>";
          retval = false;
        }
        // split interval < premax - premin. Don't check for TOO or DDT
        cc = splitIntCol;
        if (!isDDTorTOO && !ValidSplit(mongrid.cellByIndex(ii,cc).getValue(),
                        mongrid.cellByIndex(ii,minLeadCol).getValue(),
                        mongrid.cellByIndex(ii,maxLeadCol).getValue())) {
          str+="<span class='errlbl'>" + monitorLbl + ":</span>Order  " + mongrid.cellByIndex(ii,1).getValue() + ": " + hdrs[cc] + " is invalid. Valid range is Split Interval < (Maximum Time Interval - Minimum Time Interval ) days.<br>";
          retval = false;
        }
        if (tgtmon == "F") {
          cc=targnumCol;
          if (!ValidTargetNumber(mongrid.cellByIndex(ii,cc).getValue())) {
            str+="<span class='errlbl'>" + monitorLbl + ":</span>Order  " + mongrid.cellByIndex(ii,1).getValue() + ": " + hdrs[cc] + " is invalid<br>";
            retval = false;
          }
        }
      }
      // First col has split but no pre-min/max, < 365 days
      else{
        cc = splitIntCol;
        if (!isDDTorTOO && !ValidSplit(mongrid.cellByIndex(ii,cc).getValue(),1,MAX_PRE_LEAD+1)) {
          str+="<span class='errlbl'>" + monitorLbl + ":</span>Order  " + mongrid.cellByIndex(ii,1).getValue() + ": " + hdrs[cc] + " is invalid. Valid range for initial observation is Split Interval <  " + preMaxLead + " days.<br>";
          retval = false;
        }
      }
    }
    if (tottime != otime) {
      str += "<span class='errlbl'>" + monitorLbl + ":</span> Sum of " + monitorLbl + " exposure time must equal observing time. Sum of the Exposure Time is " + tottime;
      set_errlbl(true,myForm,"mon_constr");
      retval=false;
    }
  }
  if (str.length > 1) {
    set_status("<span class='errlbl'>" + SAVE_FAILED +  str + "</span>");
    retval=false;
  }
  return retval;
}


function save_changes() {
  if (validate_time()) {
    myForm.disableItem("save");
    myForm.setItemValue("page","TIME");
    myForm.setItemValue("operation","SAVE");
    gridvalues= getGridValues(mongrid);
    myForm.setItemValue("gridvalues",gridvalues);
   
    document.body.className = "waiting";
    myForm.send("/cps-app/cps_savetgt","post",function(name,retstr) {
        document.body.className = "";
        myForm.enableItem("save");
        if (process_save(retstr,false)) {
         too_tab();
         setChanges(0);
       }
     });
  }

}

function too_tab() {
  var val=myForm.getItemValue("trigger_target")
  if (parent.tgttabbar) {
    var tabele =  parent.tgttabbar.tabs("t7");
    if (val != "Y")
      tabele.disable();
    else
      tabele.enable();
  }
}


  
function reset_fields() {
  clear_errors();
  loadPage();
} 

function handle_changes(id,val,state,test) {
  var val;

  if (!test || test.indexOf(DONOTCLEARSTATUS) < 0)
    clear_success_status();

  setChanges(1);
  if (id == "tgtmon") {
    process_monitor();
  }
  else if (id == "trigger_target")  {
    val = myForm.getItemValue("trigger_target");
    if ( val =="N") {
      myForm.setItemLabel("trigobslbl","Observing Time must be 0 for non-trigger targets"); 
    } else  {
      myForm.setItemLabel("trigobslbl","");
   }
    
  }
  else if (id == "isMulti") {
    val = myForm.getItemValue("isMulti");
    if (val =="Y") {
      myForm.showItem("multiblk");
      preMaxLead=MAXMC_PRE_LEAD;
    } else {
      myForm.hideItem("multiblk");
    }
  }
  else if (id == "uninterrupt") {
    val = myForm.getItemValue("uninterrupt");
    if (val != null && val =="Y" )
      document.getElementById("unintwarn").innerHTML ="This counts as a constraint.";
    else
      document.getElementById("unintwarn").innerHTML ="";
  }

  return true;
}
function cell_selected(stage,rowId,celInd,nval,oval) {
  var isvalid=true;
  var minL = 1;
  var maxL = preMaxLead +1;
  if (stage == 2) {
    if (celInd ==minLeadCol ) {
      isvalid = ValidMinLead(nval,mongrid.cells(rowId,maxLeadCol).getValue());
      set_cell_attr(mongrid,rowId,minLeadCol,isvalid);
      isvalid = ValidMaxLead(mongrid.cells(rowId,maxLeadCol).getValue(),nval); 
      set_cell_attr(mongrid,rowId,maxLeadCol,isvalid);
    }
    else if (celInd ==maxLeadCol ) {
      isvalid = ValidMinLead(mongrid.cells(rowId,minLeadCol).getValue(),nval);
      set_cell_attr(mongrid,rowId,minLeadCol,isvalid);
      isvalid = ValidMaxLead(nval,mongrid.cells(rowId,minLeadCol).getValue()); 
      set_cell_attr(mongrid,rowId,maxLeadCol,isvalid);
    }
    else if (celInd ==splitIntCol ) {
      if (mongrid.cells(rowId,minLeadCol).getValue() != "N/A"){
        minL = mongrid.cells(rowId,minLeadCol).getValue();
        maxL = mongrid.cells(rowId,maxLeadCol).getValue()
      }
      isvalid = ValidSplit(nval,minL, maxL);
      set_cell_attr(mongrid,rowId,splitIntCol,isvalid);
    }
    else if (celInd ==2) {
      isvalid = ValidObsTime(nval); 
      set_cell_attr(mongrid,rowId,celInd,isvalid);
    }
    else {
      set_cell_attr(mongrid,rowId,celInd,true);
    }

  }
  if (stage != 0)  setChanges(1);
  return true;
}

function add_entry() {
  var ordr=1;
  var propType = myForm.getItemValue("type");
  var isDDT = myForm.getItemValue("isDDT");
  var tgtnbr = myForm.getItemValue("tgtnbr");
  var rowcnt = mongrid.getRowsNum();
  var cval = mongrid.cellByIndex(0,2).getValue();
  if (rowcnt == 1 && cval.indexOf("No obser") >= 0) {
     mongrid.clearAll();
     mongrid.setColumnHidden(0,false);
     mongrid.setColumnHidden(1,false);
     rowcnt=0;
  }
  mongrid.setEditable(true);
  if (rowcnt < MAX_OBS) {
    if (isDDT == "false" && propType.indexOf("TOO") < 0)  {
      tgtnbr = "";
    } else
      ordr=0;
    var ordr=ordr + rowcnt;
    if (isDDT == "false" && propType.indexOf("TOO") < 0 && ordr == 1) {
      mongrid.addRow(ordr,["",ordr,"","N/A","N/A",tgtnbr]);
      mongrid.cells(ordr,minLeadCol).setDisabled(true);
      mongrid.cells(ordr,maxLeadCol).setDisabled(true);
      mongrid.cells(ordr,targnumCol).setDisabled(true);
      mongrid.cells(ordr,minLeadCol).setBgColor(DISABLED_CELL_BG);
      mongrid.cells(ordr,maxLeadCol).setBgColor(DISABLED_CELL_BG);
      var ordr2 = ordr+1;
      mongrid.addRow(ordr2,["",ordr2,"","","",tgtnbr]);
    }
    else if ((isDDT == "true" || propType.indexOf("TOO") >= 0) && ordr == 0)  {
      mongrid.addRow(999,["","Initial","","N/A","N/A","N/A"]);
      mongrid.cells(999,minLeadCol).setDisabled(true);
      mongrid.cells(999,maxLeadCol).setDisabled(true);
      mongrid.cells(999,targnumCol).setDisabled(true);
      mongrid.cells(999,minLeadCol).setBgColor(DISABLED_CELL_BG);
      mongrid.cells(999,maxLeadCol).setBgColor(DISABLED_CELL_BG);
      mongrid.cells(999,targnumCol).setBgColor(DISABLED_CELL_BG);
      var ordr2 = ordr+1;
      mongrid.addRow(ordr2,["",ordr2,"","","",tgtnbr]);
    }
    else
      mongrid.addRow(ordr,["",ordr,"","","",tgtnbr]);


    mongrid.selectRowById(ordr);
    window.setTimeout(function(){
        mongrid.selectCell(rowcnt,2,false,false,true,true);
        mongrid.editCell();
        },1);
    setChanges(1);

  } else {
    doAlert(("Maximum number of observations is " + MAX_OBS + "."),alertReturn);
  }
}

function clear_monitor(flg) {

  var rowId = mongrid.getRowId(0);
  var isDDT = myForm.getItemValue("isDDT");
  var propType = myForm.getItemValue("type");
   mongrid.clearSelection();
   mongrid.enableMultiselect(true);
   for (var ii=1;ii<mongrid.getRowsNum();ii++) {
     var chk=mongrid.cellByIndex(ii,0).getValue();
     if (chk == 1) {
       mongrid.selectRow(ii,false,true);
     }
   }
   // did not select 1st row in code above
   mongrid.deleteSelectedRows();
   mongrid.enableMultiselect(false);

   // can only delete 1st row, if all rows deleted
   var chk=mongrid.cellByIndex(0,0).getValue();
   if (chk == 1) {
      if (mongrid.getRowsNum() >1 ) {
         doAlert("1st Row can only be removed if all rows are removed",alertReturn);
      }
      else {
        mongrid.selectRow(0,false,true); 
        mongrid.deleteSelectedRows();
      }
   }

   // reorder 
   var ordr=0; // ddt/too
   if (isDDT == "false" && propType.indexOf("TOO") < 0)  
      ordr=1;
   for (var ii=1;ii<mongrid.getRowsNum();ii++) {
     var newordr = ordr + ii;
     mongrid.setRowId(ii,newordr);
     mongrid.cellByIndex(ii,1).setValue(newordr);
   }

   isEmpty_grid();
   setChanges(1);
}

function process_monitor() 
{
  var retval=true;
  var raster_scan = myForm.getItemValue("raster_scan");
  var tmon=myForm.getItemValue("tgtmon");
  var propType = myForm.getItemValue("type");
  if (tmon == "Y" || tmon == "F") {
    if (propType.indexOf("TOO") >= 0 ) 
      document.getElementById("monwarn").innerHTML ="Follow-ups count toward the TOO trigger count.";
    else 
      document.getElementById("monwarn").innerHTML ="This counts as a constraint.";
  } else {
    document.getElementById("monwarn").innerHTML ="";
  }
  if (tmon == "Y" || tmon == "F" || tmon=="P") {
    if (raster_scan == "Y" || raster_scan=="P") {
      doAlert(( monitorLbl + " are not allowed for targets requesting a Grid."),alertReturn);
      myForm.setItemValue("tgtmon","N");
    } else {
      var splitint = myForm.getItemValue("split_interval");
      myForm.showItem("mondiv");
      if (splitint != null && splitint.toString().trim() !== "") {
        doAlert(("Choosing a Monitor Series will delete the current Split" +
            " Interval value\n. Please enter new Split Intervals for each" +
            " observation in Monitor Series "), alertReturn);
      }
      myForm.hideItem("split_interval");
    }
  } else {
    myForm.hideItem("mondiv");
    myForm.showItem("split_interval");
  }
  return retval;
}

function before_load(id,values)
{
  handle_preferred("uninterrupt",values);
  var opts = myForm.getOptions("tgtmon");

  if (opts.length <= 0) {
    var propType = values["type"];
    var isDDT = values["isDDT"];
    if (propType == null) propType="TBD";
    if (propType.indexOf("TOO") >= 0 || isDDT == "true") {
      myForm.showItem("triggerblk");
      // No need to split TOOs
      myForm.hideItem("split_interval")
      monitorLbl="Followups";
      opts.add(new Option("No Followups","N"));
      opts.add(new Option("Yes, Followups are required","F")); 
    }
    else {
      myForm.hideItem("triggerblk");
      monitorLbl="Monitors";
      opts.add(new Option("No constraint","N"));
      opts.add(new Option("Yes, I want this target to be split","Y"));
      //opts.add(new Option("Preferred, I have a preference for this target to be split","P"));
    }
    handle_preferred("tgtmon",values);
  }
  return true;
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

  var url = "/cps-app/cps_loadtgt?page=MONITOR&pid=" + pid  + "&tid=" +tid;
  mongrid.load(url,postLoad2,"json");

  handle_changes("isMulti",null,null,DONOTCLEARSTATUS);
  handle_changes("tgtmon",null,null,DONOTCLEARSTATUS);
  handle_changes("trigger_target",null,null,DONOTCLEARSTATUS);
  handle_changes("uninterrupt",null,null,DONOTCLEARSTATUS);
  myForm.setItemLabel("monitorfs",monitorLbl);

  var isEdit = myForm.getItemValue("isEdit");
  set_editButtons(myForm,isEdit);
  if (isEdit != "true") {
    myForm.hideItem("monbtns");
    myForm.disableItem("addmon");
    myForm.disableItem("clearmon");
    myForm.disableItem("trigger_target","Y");
    myForm.disableItem("trigger_target","N");
  }

  cps_style_load();
  myForm.setTooltip("mon_constr","To add a row, click 'Add Observation' button. To edit an existing row, double click in the field you want to modify.");
  too_tab();


  setChanges(0);
} 

function isEmpty_grid()
{
  if (mongrid.getRowsNum()<1) {
    mongrid.setColumnHidden(0,true);
    mongrid.setColumnHidden(1,true);
    mongrid.addRow(0,[""," ","No observations","","","",""]);
    mongrid.cells(0,2).setBgColor(DISABLED_CELL_BG);
    mongrid.cells(0,2).setHorAlign('l');
    mongrid.setColspan(0,2,4);
    mongrid.setEditable(false);
  } else {
    mongrid.cellByIndex(0,minLeadCol).setBgColor(DISABLED_CELL_BG);
    mongrid.cellByIndex(0,maxLeadCol).setBgColor(DISABLED_CELL_BG);
    mongrid.cellByIndex(0,targnumCol).setBgColor(DISABLED_CELL_BG);
  }
}


function postLoad2() {
  setChanges(0);
  isEmpty_grid();
  var propType = myForm.getItemValue("type");
  var isDDT = myForm.getItemValue("isDDT");
  if (isDDT == "false" && propType.indexOf("TOO") < 0) {
    mongrid.setColumnHidden(targnumCol,true);
  }
  else{
    mongrid.setColumnHidden(splitIntCol,true);
  }
  var isEdit = myForm.getItemValue("isEdit");
  if (isEdit != "true") {
    mongrid.setEditable(false);
    mongrid.setColumnHidden(0,true);
  }
}

function loadPage() {
  mongrid.clearAll();
  var url ="/cps-app/cps_loadtgt?page=TIME&pid=" + pid + "&tid=" +tid;
  myForm.load(url,postLoad);

}
function confirmResult(result) {
  if (result) {
    myForm.setItemValue("tgtmon","N");
    document.getElementById("monwarn").innerHTML ="";
    mongrid.clearAll();
    isEmpty_grid();
    myForm.hideItem("mondiv");
    setChanges(1);
  }
}

function doOnLoad() {
         var formData=[
          {type:"input",hidden:true,name:"operation",value:""},
           {type:"input",hidden:true,name:"pid",value:""},
           {type:"input",hidden:true,name:"tid",value:""},
           {type:"input",hidden:true,name:"page",value:"TIME"},
           {type:"input",hidden:true,name:"propno",value:""},
           {type:"input",hidden:true,name:"tgtnbr",value:""},
           {type:"input",hidden:true,name:"type",value:""},
           {type:"input",hidden:true,name:"isEdit",value:""},
           {type:"input",hidden:true,name:"isDDT",value:""},
           {type:"input",hidden:true,name:"isMulti",value:""},
           {type:"input",hidden:true,name:"emsg",value:""},
           {type:"input",hidden:true,name:"raster_scan",value:""},
           {type:"input",hidden:true,name:"gridvalues",value:""},

               {type:"fieldset",name:"nameobs",label:"Observing Time",list:[
               {type:"block",name:"triggerblk",list:[
               {type:"label", name: "triggerlbl",label:"<a href='javascript:openHelpWindow(\"#TriggerTarget\")'>Is this the trigger target for the TOO?</a>"},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"trigger_target",value:"Y",label:"Yes",position:"label-right" },
               {type:"newcolumn",offset:20},
               {type:"radio",name:"trigger_target",value:"N",label:"No",position:"label-right" },
               ]},
               {type:"block",name:"timeblk",list:[
                 {type:"input", name: "obs_time",className:"reqLbl",label:"<a href='javascript:openHelpWindow(\"#TotalObservationTime\")'>Total Observing Time:</a>" ,inputWidth:100,validate:"ValidObsTime0"},
                 {type:"newcolumn",offset:5},
                 {type:"label",name:"unit1",label:"(ks)"},
                 {type:"newcolumn",offset:20},
                 {type:"label",name:"trigobslbl",label:""}
               ]},
               {type:"block",name:"multiblk",list:[
                 {type:"block",list:[
                   {type:"input", name: "obs_time_1",label:"<a href='javascript:openHelpWindow(\"#MultiCycleTgt\")'>Time Projected to be used in Cycle+1:</a>",inputWidth:100,validate:"ValidObsTime1" },
                 {type:"newcolumn",offset:5},
                 {type:"label",name:"unit2",label:"(ks)"}
                   ]},
               {type:"block",list:[
                   {type:"input", name: "obs_time_2",label:"<a href='javascript:openHelpWindow(\"#MultiCycleTgt\")'>Time Projected to be used in Cycle+2:</a>" ,inputWidth:100,validate:"ValidObsTime2"},
                 {type:"newcolumn",offset:5},
                 {type:"label",name:"unit3",label:"(ks)"}
                   ]},
                 ]},
               {type:"block",list:[
                 {type:"select", name: "uninterrupt",label:"<a href='javascript:openHelpWindow(\"#UninterruptedObservation\")'>Uninterrupted?</a><br><span id=\"unintwarn\" class='errmsgSmall'></span> " },
                 {type:"newcolumn",offset:20},
                 {type:"input", name: "split_interval",label:"<a href='javascript:openHelpWindow(\"#SplitObservation\")'>Time Interval if Observation is Split:</a>"},
                 {type:"newcolumn",offset:5},
                 {type:"label",name:"splitint",label:"(days)"}
               ]}
               ]},
               {type:"fieldset",name:"monitorfs",label:"Monitor/Followups",list:[
                 {type:"block",list:[
                   {type:"label", name: "mon_constr",label:"<a href='javascript:openHelpWindow(\"#MonitorFlag\")'>Do you want more than one observation of this target?</a><br><span id=\"monwarn\" class='errmsgSmall'></span> "},
                   {type:"newcolumn",offset:20},
                   {type:"select",name:"tgtmon"},
                 ]},
                 {type:"block",name:"mondiv",inputTop:0,list:[
                   {type:"label",offsetTop:0,label:"Please specify the exposure time and the minimum/maximum interval in days for which each observation should be spaced from its preceding observation. ",labelWidth:600},
                   {type:"block",offsetTop:0,name:"clearbtns",position:"label-left",list:[
                     {type:"label",name:"selectall",label:"<div class='tinybtn'><a href='javascript:selectAll();'>Select All<a></div>"},
                     {type:"newcolumn",offset:2},
                     {type:"label",name:"deselectall",label:"<div class='tinybtn'><a href='javascript:deselectAll();'>Deselect All<a></div>"},
                 {type:"newcolumn",offset:10},
                  {type:"label",name:"moninstr",label:TGT_TBL_INSTR}

                    ]},
                   {type: "label",label:"<div id='grid' style='margin:0;width:95%'></div>",offsetTop:0},
                   {type:"block",name:"monbtns",list:[
                     {type:"button",name:"addmon",value:"Add Observation",offsetLeft:30 },
                     {type:"newcolumn",offset:30},
                     {type:"button",name:"clearmon",value:"Remove selected observations"},
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

    myForm=new dhtmlXForm("formB_container",formData);

    myForm.setSkin('dhx_skyblue');
    myForm.setItemLabel("monitorfs",monitorLbl);
    buildYN(myForm.getOptions("uninterrupt"),0);
    myForm.enableLiveValidation(true); 
    buildTooltips(myForm);

    myForm.attachEvent("onBeforeChange",function(id,old_val,new_val) {
      if (id == "tgtmon" && new_val=="N") {
        if (mongrid.cells(mongrid.getRowId(0),2).getValue() != null && 
            mongrid.cells(mongrid.getRowId(0),2).getValue().indexOf("No obs") < 0) {
          doConfirm("Are you sure you want to remove all observations for this target?",confirmResult);
          myForm.showItem("split_interval");

         return false;
        }
      }
      return true;
    }); 

    myForm.attachEvent("onChange",handle_changes);
    myForm.attachEvent("onBeforeDataLoad", before_load);
    myForm.attachEvent("onValidateError",handle_error);
    myForm.attachEvent("onAfterValidate", after_validate);
    myForm.attachEvent("onButtonClick",function(id){
      clear_status();
      if ( id=="clearmon") clear_monitor(0);
      else if ( id=="addmon") add_entry();
      else if ( id=="save") save_changes();
      else if (id=="discard") reset_fields();
    });
    myForm.hideItem("mondiv");


    mongrid=new dhtmlXGridObject('grid');
    mongrid.setHeader("Remove?,Order,<a href='javascript:openHelpWindow(\"#MonitorTime\")'>Exposure Time(ks)</a>,<a href='javascript:openHelpWindow(\"#MonitorMinInterval\")'>Minimum Time Interval (days)</a>,<a href='javascript:openHelpWindow(\"#MonitorMaxInterval\")'>Maximum Time Interval (days)</a>,<a href='javascript:openHelpWindow(\"#FupTargetNum\")'>Target Number</a>,<a href='javascript:openHelpWindow(\"#SplitObservation\")'>Time Interval if Observation is Split (days)</a>");
    mongrid.setInitWidths("80,60,150,150,150,100,150");
    mongrid.setColumnMinWidth("20,20,50,50,50,1,50");
    mongrid.setColAlign("center,center,center,center,center,center,center");
    mongrid.setSkin("dhx_skyblue");
    mongrid.setColTypes("ch,ro,ed,ed,ed,ed,ed");
    mongrid.setImagePath(CSS_IMG_PATH);
    mongrid.enableAutoHeight(true,600);
    mongrid.enableColSpan(true);
    mongrid.enableAutoWidth(true);
    mongrid.enableRowsHover(true,"gridhover");
    mongrid.attachEvent("onRowSelect",function (id,ind) {
             mongrid.editCell();
    });
    mongrid.attachEvent("onEditCell",cell_selected);
    //myForm.bind(mongrid);  breaks the FORM save
    mongrid.init();

    set_status("");
    loadPage();
  }   
