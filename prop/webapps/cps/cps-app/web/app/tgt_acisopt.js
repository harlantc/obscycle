var myForm,spatwingrid, statCol=2;
var lbls = new Array("sublbl","subarray_start","subarray_rows",
	"mostefflbl","frame_time",
	"altexplbl","secondary_exp_count","primary_exposure_time",
	"eventfilterlbl","eventfilter_lower","eventfilter_range",
	"swlbl"
        );

function selectAll()
{
  spatwingrid.checkAll();
}
function deselectAll()
{
  spatwingrid.uncheckAll();
}


function ValidET(data) {
   var retval = true;
   if (data && data.toString().trim().length >0) {
     var x =parseFloat(data) ;
     if (isNaN(x) || x != data.toString().trim()) 
        retval = false;
     else if (x < .08 || x > 15.0) 
        retval = false;
   }
   return retval;
}
function ValidER(data) {
   var retval = true;
   if (data && data.toString().trim().length >0) {
     var x =parseFloat(data) ;
     if (isNaN(x) || x != data.toString().trim()) 
        retval = false;
     else if (x < .1 || x > 15.0) 
        retval = false;
   }
   return retval;
}

function ValidSF(data) {
   var retval = true;
   if (data && data.toString().trim().length >0) {
     var x =parseInt(data) ;
     if (isNaN(x) || x != data.toString().trim()) 
        retval = false;
     else if (x < 0 || x > 255) 
        retval = false;
   }
   return retval;
}
function ValidSpatWin(sp_start,w_or_h) {
  var retval = true;
  try {
     var x =parseInt(sp_start) ;
     var y =parseInt(w_or_h) ;
     if (isNaN(x) || isNaN(y)) 
       retval =false;
      else if ((x + y) > 1024)  
       retval=false;
  }
  catch (e) {
    retval = false;
  }
  return retval;
}

function ValidSP(data) {
   var retval=true;
   if (data && data.toString().trim().length >0) {
     var x =parseInt(data) ;
     if (isNaN(x) || x != data.toString().trim()) 
        retval = false;
     else if (x < 1 || x > 1023) 
        retval = false;
   }
   return retval;
}
function ValidSP2(data) {
   var retval = true;
   if (data && data.toString().trim().length >0) {
     var x =parseInt(data) ;
     if (isNaN(x) || x != data.toString().trim()) 
        retval = false;
     else if (x < 1 || x > 1024) 
        retval = false;
   }
   return retval;
}

function ValidEFLower(data) 
{
  var retval = true;
  var detector = myForm.getItemValue("detector");
  if (data && data.toString().trim().length >0) {
    var rlbl = "eventfilter_lower_"+ detector;
    retval = validate_range_special(myForm,rlbl,"eventfilter_lower");
  }
  return retval;
}
function ValidEFRange(data) 
{
  return validate_range(myForm,"eventfilter_range");
}
function ValidSubarrayStartRow(data) 
{
  return validate_irange(myForm,"subarray_start");
}
function ValidSubarrayNbrRows(data) 
{
  return validate_irange(myForm,"subarray_rows");
}
function ValidAltExpSec(data) 
{
  return validate_irange(myForm,"secondary_exp_count");
}
function ValidAltExpPri(data) 
{
  return validate_range(myForm,"primary_exposure_time");
}
function ValidFrameTime(data) 
{
  return validate_range(myForm,"frame_time");
}

function validateMostEfficient(val) {
  var str="";
  var expmode = myForm.getItemValue("exp_mode");
  if (expmode == "CC") {
    if (val != "Y") {
      str+= "Use most efficient frame time  must be Yes for CC Exposure Mode."
    }
  }
  return str;
}

function validateSubarray(val) {
  var str="";
  var expmode = myForm.getItemValue("exp_mode");
  if (expmode == "CC") {
    if (val != "NONE") {
      str+= "Subarray must be None for CC Exposure Mode."
    }
  }
  return str;
}
function validateAltExp(val) {
  var str="";
  var expmode = myForm.getItemValue("exp_mode");
  if (expmode == "CC") {
    if (val != "N") {
      str+= "Alternating Exposures must be No for CC Exposure Mode."
    }
  }
  return str;
}
  
function before_change(id,oval,nval) {
  if (myForm.getItemValue("isEdit") == "false")
     return false;

  var retval = true;
  var expmode = myForm.getItemValue("exp_mode");
  var str = "";
  if (expmode == "CC") {
    if (id == "most_efficient")
      str += validateMostEfficient(oval);
    if (id == "subarray")
      str += validateSubarray(oval);
    if (id == "duty_cycle")
      str += validateAltExp(oval);
    if (str != "") {
       doAlert(str,alertReturn);
       retval=false;
    }
  }

  return retval;
}

function handle_change(id,val,state,test) {
  var str;
  setChanges(1);
  if (!test || test.indexOf(DONOTCLEARSTATUS) < 0)
    clear_success_status();

        if (id == "eventfilter") {
          if (val == 'N') {
           var chkarr = ["eventfilter_lower","eventfilter_range"];
           if ( check_fields(myForm,chkarr) == 0) 
             myForm.hideItem("evtfiltblk");
          } else {
             myForm.showItem("evtfiltblk");
          }
        }
        else if (id == "duty_cycle") {
          if (val == 'N') {
            var chkarr = ["secondary_exp_count","primary_exposure_time"];
            if ( check_fields(myForm,chkarr) == 0) 
               myForm.hideItem("altblk");
          } else {
             myForm.showItem("altblk");
          }
        }
        else if (id == "most_efficient") {
          if (val == 'Y') {
            var chkarr = ["frame_time"];
            if ( check_fields(myForm,chkarr) == 0) 
             myForm.hideItem("mosteffblk");
          } else {
             myForm.showItem("mosteffblk");
          }
        }
        else if (id == "spwindow") {
           var duh = myForm.getContainer("swinstr");
           duh.innerHTML= "<span class='fldhelpsmall'>Please  ensure that your target falls within the chosen windows. (e.g. use <a href='javascript:openHelpWindow(\"#ov_spwin\")'>ObsVis:</a>)</span>" ;
          var nrows = spatwingrid.getRowsNum();
          if (val == 'N') {
             var cval ="";
             if (nrows > 0) 
               cval = spatwingrid.cellByIndex(0,1).getValue();
             myForm.hideItem("spatwinblk");
             if (nrows > 0 && cval.length > 1)  {
               duh.innerHTML= "<span class='errmsg'>All Spatial Windows will be deleted! </span>" ;
             }
          } else {
             myForm.showItem("spatwinblk");
          }
        }
        else if (id == "subarray") {
          if (val == 'NONE') {
             myForm.hideItem("subinstr");
          } else {
              myForm.showItem("subinstr");
          }
          if (val == 'CUSTOM') {
             myForm.showItem("subarrayblk");
          } else {
            var chkarr = ["subarray_start","subarray_rows"];
            if ( check_fields(myForm,chkarr) == 0)  {
              myForm.hideItem("subarrayblk");
            }
          }
        }
      }

function clear_errors()
{
  //reset error labels
  for (var ii=0;ii<lbls.length;ii++) {
    set_errlbl(false,myForm,lbls[ii]);
  }
  clear_status();
}

function validate_acisopt() {
  var retval=true;
  var str = "";
  var range_fields = new Array("frame_time",
		"subarray_start","subarray_rows",
		"secondary_exp_count","primary_exposure_time",
		"eventfilter_range");

  var spwin_chk = new Array(null,null,"spwin_sfreq",	
		"spwin_start","spwin_width",
		"spwin_start","spwin_width",
		"eventfilter_lower","eventfilter_range");


  clear_errors();

   
  for (var ii=0;ii<range_fields.length;ii++) {
    var tmpret;
    if ((range_fields[ii].indexOf("subarray") >=0) || (range_fields[ii].indexOf("secondary") >=0)) {
      tmpret= validate_irange(myForm,range_fields[ii]);
      if (!tmpret)  {
        str += myForm.getItemLabel(range_fields[ii]) + " is invalid. " + get_irange(range_fields[ii]) + "<br>";
        set_errlbl(true,myForm,range_fields[ii]);
      }
    } else {
      tmpret= validate_range(myForm,range_fields[ii]);
      if (!tmpret)  {
        str += myForm.getItemLabel(range_fields[ii]) + " is invalid. " + get_range(range_fields[ii]) + "<br>";
        set_errlbl(true,myForm,range_fields[ii]);
      }
    }
  }
  var detector = myForm.getItemValue("detector");
  var rlbl = "eventfilter_lower_" + detector;
  if (!validate_range_special(myForm,rlbl,"eventfilter_lower")) {
      set_errlbl(true,myForm,"eventfilter_lower");
      str += myForm.getItemLabel("eventfilter_lower") + " is invalid. " + get_range(rlbl) + "<br>";
  }

  var val = "N";
  var tstr="";

  if (myForm.getItemValue("most_efficient") == "N")
    val = "Y";
  tstr = validate_yn(val,myForm,["mostefflbl","frame_time"]);
  str += tstr.replace(/Yes/,"No");
    
  str += validate_yn(myForm.getItemValue("duty_cycle"),myForm,["altexplbl","secondary_exp_count","primary_exposure_time"]);
  str += validate_yn(myForm.getItemValue("eventfilter"),myForm,["eventfilterlbl","eventfilter_lower","eventfilter_range"]);

  val = "N";
  if (myForm.getItemValue("subarray") == "CUSTOM") val = "Y";
  tstr = validate_yn(val,myForm,["sublbl","subarray_start","subarray_rows"]);
  str += tstr.replace(/Yes/g,"Custom");
  if (val == "Y")  {
    var substart = parseInt(myForm.getItemValue("subarray_start"));
    var subrows = parseInt(myForm.getItemValue("subarray_rows"));
    if (!isNaN(substart) && !isNaN(subrows)) {
      if (substart +subrows > 1025) {
        set_errlbl(true,myForm,"subarray_rows");
        str+= myForm.getItemLabel("subarray_rows") + ":  (Start Row) + (No. Rows) cannot exceed 1024.<br>" ;
      }
    }
  }
  str += validateAltExp(myForm.getItemValue("duty_cycle"));
        

  if (myForm.getItemValue("spwindow") == "Y") {
    var cval = spatwingrid.cellByIndex(0,2).getValue();
    if (cval.indexOf("No spa") >= 0 ) {
      set_errlbl(true,myForm,"swlbl");
      str+= myForm.getItemLabel("swlbl") + ": Spatial windows must be specified.<br>" ;
    }
    else {
      var fld=0;
      var cnt=0;
      for (var ii=0;ii<spatwingrid.getRowsNum();ii++) {
        fld = 0;
        lbl = "Spatial Window";
        if (spatwingrid.cellByIndex(ii,1).getValue())
          lbl += " Chip " + spatwingrid.cellByIndex(ii,1).getValue() ;
        else
          lbl += " Row " + (ii+1);
        for (var cc=1;cc<9;cc++) {
          var isvalid=true;
          var cval = spatwingrid.cellByIndex(ii,cc).getValue();
          var clbl = spatwingrid.getColumnLabel(cc);
          if (cc==2)   isvalid = ValidSF(cval);
          if (cc==3 || cc==5)   isvalid = ValidSP(cval);
          if (cc==4 || cc==6)   isvalid = ValidSP2(cval);
          if (cc==7 )   isvalid = ValidET(cval);
          if (cc==8 )   isvalid = ValidER(cval);
          if (!isvalid) {
            var tstr="";
            if (cc>6)
              tstr = get_range(spwin_chk[cc]);
            else
              tstr = get_irange(spwin_chk[cc]);
            str+=lbl + ": Invalid " + clbl + " . " + tstr + "<br>";
            set_errlbl(true,myForm,"swlbl");
          }
          if (cc<7 && cval != null && cval.toString().length > 0) {
            fld+=1;
          }
        }
        if (!ValidSpatWin(spatwingrid.cellByIndex(ii,3).getValue(),
               spatwingrid.cellByIndex(ii,4).getValue())) {
          set_errlbl(true,myForm,"swlbl");
          str+= lbl + ": Start Column + Width cannot exceed 1024.<br>";
        }
        if (!ValidSpatWin(spatwingrid.cellByIndex(ii,5).getValue(),
               spatwingrid.cellByIndex(ii,6).getValue())) {
          set_errlbl(true,myForm,"swlbl");
          str+= lbl + ": Start Row + Height cannot exceed 1024.<br>";
        }

        if (fld != 0 && fld != 6) {
          set_errlbl(true,myForm,"swlbl");
          str+= lbl + ": Chip Id, Sampling Frequency, Start Column, Width, Start Row, Height are required.<br>";
        } 
        if (fld != 0) cnt++;
      }
      if (cnt < 1) {
        set_errlbl(true,myForm,"swlbl");
        str+= "Use Spatial Windows: At least 1 entry required.<br>";
      }
    }
  }
       
  if (str.length > 1) {
    set_status("<span class='errlbl'>" + SAVE_FAILED +  str + "</span>");
    retval=false;
  }
  return retval;

}
  
function save_changes() {
  if (validate_acisopt()) {
    myForm.setItemValue("operation","SAVE");
    gridvalues= getGridValues(spatwingrid);
    myForm.setItemValue("gridvalues",gridvalues);
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
} 

function clear_spatwin() {
   spatwingrid.clearSelection();
   spatwingrid.enableMultiselect(true);
   for (var ii=0;ii<spatwingrid.getRowsNum();ii++) {
     var chk=spatwingrid.cellByIndex(ii,0).getValue();
     if (chk == 1)
       spatwingrid.selectRow(ii,false,true);
   }
   spatwingrid.deleteSelectedRows();
   spatwingrid.enableMultiselect(false);
   isEmpty_grid();
   setChanges(1);
}


function add_entry() {
  var rowcnt = spatwingrid.getRowsNum();
  var cval = spatwingrid.cellByIndex(0,2).getValue();
  if (rowcnt == 1 && cval.indexOf("No spat") >= 0) {
    spatwingrid.clearAll();
    spatwingrid.setColumnHidden(0,false);
    spatwingrid.setColumnHidden(1,false);
    spatwingrid.setColWidth(0,"80");
    spatwingrid.setColWidth(1,"80");
    rowcnt=0;
  }
  spatwingrid.setEditable(true);

  if (rowcnt < MAX_SPATWIN) {
    var ordr=rowcnt+1;
    spatwingrid.addRow(ordr,["","","","","","","","",""]);
    spatwingrid.selectRowById(ordr);
    window.setTimeout(function(){
        spatwingrid.selectCell(ordr-1,1,false,false,true,true);
        spatwingrid.editCell();
        },1);
    setChanges(1);
  } else {
    doAlert("Maximum of " + MAX_SPATWIN + " spatial windows has been exceeded.",alertReturn);
  }
  spatwingrid.setSizes();
}

function row_selectedOld(rowId,celInd) {
  var addRow = true;
  var rowcnt = spatwingrid.getRowsNum();
  for (var ii=0;ii<rowcnt; ii++) {
    var cval = spatwingrid.cellByIndex(ii,0).getValue();
    if (cval.length < 1 ) {
      addRow = false;
    }
  }
  if (addRow) {
    add_entry();
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

  var str = myForm.getItemValue("detector") + "/";
  str += myForm.getItemValue("grating");

  var fslbl = str + ":  Optional Parameters that affect PILEUP";
  myForm.setItemLabel("fslabel",fslbl);
  var fslbl = str + ":  Optional Parameters that affect Telemetry";
  myForm.setItemLabel("fstlabel",fslbl);

  handle_change("spwindow",myForm.getItemValue("spwindow"),null,DONOTCLEARSTATUS);
  handle_change("subarray",myForm.getItemValue("subarray"),null,DONOTCLEARSTATUS);
  handle_change("duty_cycle",myForm.getItemValue("duty_cycle"),null,DONOTCLEARSTATUS);
  handle_change("eventfilter",myForm.getItemValue("eventfilter"),null,DONOTCLEARSTATUS);
  handle_change("most_efficient",myForm.getItemValue("most_efficient"),null,DONOTCLEARSTATUS);

  var isEdit = myForm.getItemValue("isEdit");
  set_editButtons(myForm,isEdit);
  if (isEdit != "true") {
    disableYN("duty_cycle");
    disableYN("spwindow");
    disableYN("eventfilter");
    disableYN("most_efficient");
    myForm.disableItem("subarray","NONE");
    myForm.disableItem("subarray","CUSTOM");
    myForm.disableItem("subarray","1/2");
    myForm.disableItem("subarray","1/4");
    myForm.disableItem("subarray","1/8");
    myForm.hideItem("spatwinbtns");
  }

  spatwingrid.clearAll();
  var url ="/cps-app/cps_loadtgt?page=SPATWIN&pid=" + pid + "&tid=" +tid;
  spatwingrid.load(url,postLoad2,"json");

  setChanges(0);
  cps_style_load();

}
function disableYN(name)
{
  myForm.disableItem(name,"Y");
  myForm.disableItem(name,"N");
}

function postLoad2()
{
  setChanges(0);
  isEmpty_grid();
  var isEdit = myForm.getItemValue("isEdit");
  if (isEdit != "true") {
     spatwingrid.setEditable(false);
     spatwingrid.setColumnHidden(0,true);
  }
}

function isEmpty_grid()
{
  if (spatwingrid.getRowsNum()<1) {
    spatwingrid.setColumnHidden(0,true);
    spatwingrid.setColumnHidden(1,true);
    spatwingrid.addRow(0,[""," ","No spatial windows","","","","","","",""]);
    spatwingrid.cells(0,2).setBgColor(DISABLED_CELL_BG);
    spatwingrid.cells(0,2).setHorAlign('l');
    spatwingrid.setEditable(false);
    spatwingrid.setColspan(0,2,7);

  }
}

function edit_cell(stage,rowId,celInd,nval,oval)
{
  var isvalid=true;
  if (stage == 2) {
    if (celInd == 1)  {
    }
    else if (celInd == 2 )  {
      isvalid = ValidSF(nval);
      set_cell_attr(spatwingrid,rowId,celInd,isvalid);
    }
    else if (celInd == 3 || celInd==5)  {
      isvalid = ValidSP(nval);
      set_cell_attr(spatwingrid,rowId,celInd,isvalid);
    }
    else if (celInd == 4 || celInd==6)  {
      isvalid = ValidSP2(nval);
      set_cell_attr(spatwingrid,rowId,celInd,isvalid);
    }
    else if (celInd == 7 )  {
      isvalid = ValidET(nval);
      set_cell_attr(spatwingrid,rowId,celInd,isvalid);
    }
    else if (celInd == 8 )  {
      isvalid = ValidER(nval);
      set_cell_attr(spatwingrid,rowId,celInd,isvalid);
    }
  }
  if (stage != 0)  setChanges(1);
  return true;
}




function loadPage() {
  var url ="/cps-app/cps_loadtgt?page=ACISOPT&pid=" + pid + "&tid=" +tid;
  myForm.load(url,postLoad);
  setChanges(0);

} 

function doOnLoad() {
  var formData=[
           {type:"settings",position:"label-left",labelWidth:"auto"},
           {type:"input",hidden:true,name:"operation",value:""},
           {type:"input",hidden:true,name:"isEdit",value:""},
           {type:"input",hidden:true,name:"pid",value:""},
           {type:"input",hidden:true,name:"tid",value:""},
           {type:"input",hidden:true,name:"page",value:"ACISOPT"},
           {type:"input",hidden:true,name:"tgtnbr",value:""},
           {type:"input",hidden:true,name:"propno",value:""},
           {type:"input",hidden:true,name:"emsg",value:""},
           {type:"input",hidden:true,name:"detector",value:"ACIS-I"},
           {type:"input",hidden:true,name:"grating",value:"None"},
           {type:"input",hidden:true,name:"exp_mode",value:"TE"},
           {type:"input",hidden:true,name:"gridvalues",value:""},
           {type:"fieldset",name:"fslabel",label:"Optional Parameters that affect PILEUP", list:[
               {type:"block",list:[
                  {type:"label", name: "mostefflbl", label: "<a href='javascript:openHelpWindow(\"#Use most efficient values\")'>Use most efficient frame exposure time:</a>",offsetTop:2},
		{type:"newcolumn",offset:10},
               {type:"radio",name:"most_efficient",value:"Y",label:"Yes",position:"label-right",checked: true },
		{type:"newcolumn",offset:10},
               {type:"radio",name:"most_efficient",value:"N",label:"No",position:"label-right",checked: false}
               ]} ,
               {type:"block",name:"mosteffblk",list:[
                  {type:"input", name: "frame_time", offsetLeft:"20",label: "<a href='javascript:openHelpWindow(\"#FrameTime\")'>CCD Frame Exposure time:</a>",inputWidth:50,validate:"ValidFrameTime" }
               ]} ,
               {type:"container",name:"subinstr"},
               {type:"block",list:[
                  {type:"label", name: "sublbl", label: "<a href='javascript:openHelpWindow(\"#Subarray\")' >Subarray Type:</a>" ,offsetTop:5},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"subarray",value:"NONE",label:"None",position:"label-right",checked: true},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"subarray",value:"1/2",label:"Standard 1/2",position:"label-right",checked: false},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"subarray",value:"1/4",label:"Standard 1/4",position:"label-right",checked: false},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"subarray",value:"1/8",label:"Standard 1/8",position:"label-right",checked: false},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"subarray",value:"CUSTOM",label:"Custom",position:"label-right",checked: false}
               ]},
               {type:"block",name:"subarrayblk",list:[
                  {type:"input", offsetLeft:"30",name: "subarray_start", label: "<a href='javascript:openHelpWindow(\"#SubarrayStartRow\")'>Subarray Start Row:</a>",inputWidth:100,labelWidth:150,validate:"ValidSubarrayStartRow"},
                  {type:"input", offsetLeft:"30",name: "subarray_rows", label: "<a href='javascript:openHelpWindow(\"#SubarrayRows\")'>Number of Rows:</a>",inputWidth:100,labelWidth:150,validate:"ValidSubarrayNbrRows" }
               ]} ,
               {type:"block",name:"altexpblk",list:[
                  {type:"label", name: "altexplbl", label:"<a href='javascript:openHelpWindow(\"#UseDutyCycle\")'>Use Alternating Exposure Readout:</a>",offsetTop:2},
		{type:"newcolumn",offset:10},
               {type:"radio",name:"duty_cycle",value:"Y",label:"Yes",position:"label-right",checked: false},
		{type:"newcolumn",offset:10},
               {type:"radio",name:"duty_cycle",value:"N",label:"No",position:"label-right",checked: true},
               ]},
               {type:"block",name:"altblk",list:[
                  {type:"input", name: "secondary_exp_count", offsetLeft:20,label: "<a href='javascript:openHelpWindow(\"#NumberofCycles\")'>Number of Secondary Exposures :</a>",inputWidth:100,validate:"ValidAltExpSec"},
                  {type:"block",blockOffset:0,name:"alt2blk",list:[
                    {type:"input", name: "primary_exposure_time", offsetLeft:20,label: "<a href='javascript:openHelpWindow(\"#ExposureTimeforPrimaryCycle\")'>Exposure Time Primary Cycle:</a>",inputWidth:100,validate:"ValidAltExpPri"},
		    {type:"newcolumn",offset:10},
                    {type:"label",className:'fldhelp',label: "<span class='fldhelp'>(secs)</span>"}
                  ]} 
               ]} 
            ]},
               {type:"fieldset",name:"fstlabel",label:"Optional Parameters that affect Telemetry", list:[
               {type:"block",list:[
                  {type:"label", name: "eventfilterlbl", label: "<a href='javascript:openHelpWindow(\"#UseEnergyFilter\")'>Use Custom Event Energy Filter:</a>",offsetTop:2 },
		{type:"newcolumn",offset:10},
               {type:"radio",name:"eventfilter",value:"Y",label:"Yes",position:"label-right",checked: false},
		{type:"newcolumn",offset:10},
               {type:"radio",name:"eventfilter",value:"N",label:"No",position:"label-right",checked: true},
             ]},
               {type:"block",name:"evtfiltblk",list:[
                 {type:"block",list:[
                  {type:"input", name: "eventfilter_lower", offsetLeft:10,label: "<a href='javascript:openHelpWindow(\"#LowerEnergyThreshold\")'>Lower energy threshold:</a>", inputWidth:100,labelWidth:175,validate:"ValidEFLower"},
		{type:"newcolumn"},
                  {type:"label",className:'fldhelp',name: "lower_lbl", offsetLeft:20,label: "<span class='fldhelp'>(keV)</span>"},
                  ]},
                 {type:"block",list:[
                  {type:"input", name: "eventfilter_range", offsetLeft:10,label: "<a href='javascript:openHelpWindow(\"#EnergyFilterRange\")'>Energy Filter Range:</a>" , inputWidth:100,labelWidth:175,validate:"ValidEFRange"},
		{type:"newcolumn"},
                  {type:"label",className:'fldhelp',name: "range_lbl", offsetLeft:20,label: "<span class='fldhelp'>(keV)</span><span class='fldhelpsmall'> Upper Energy Threshold=(Lower Energy Threshold + Energy Filter Range)</span>"},
                  ]},
               ]} ,
               {type:"block",list:[
                  {type:"label", name: "swlbl", label: "<a href='javascript:openHelpWindow(\"#UseSpatialWindow\")'>Use Spatial Windows:</a>" ,offsetTop:2},
		  {type:"newcolumn",offset:10},
                  {type:"radio",name:"spwindow",value:"Y",label:"Yes",position:"label-right",checked: false},
		  {type:"newcolumn",offset:10},
                  {type:"radio",name:"spwindow",value:"N",label:"No",position:"label-right",checked: true},
		  {type:"newcolumn",offset:10},
                  {type:"container", name: "swinstr",offsetTop:10}
               ]},
               {type:"block",name:"spatwinblk",inputTop:0,list:[
                   {type:"block",name:"clearbtns",offsetTop:0,position:"label-left",list:[
                     {type:"label",name:"selectall",label:"<div class='tinybtn'><a href='javascript:selectAll();'>Select All<a></div>"},
                     {type:"newcolumn",offset:2},
                     {type:"label",name:"deselectall",label:"<div class='tinybtn'><a href='javascript:deselectAll();'>Deselect All<a></div>"},
                 {type:"newcolumn",offset:10},
                  {type:"container",name:"wininstr"},

                    ]},
                 {type:"label",label:"<div id='grid' style='margin:0;width:95%'></div>",offsetTop:0},
                 {type:"label",label:"<span class='fldhelp'>* Starting Row and Row Height only apply to 2-dimensional windows (i.e., TE-mode)</span>"},
		  {type:"block",name:"spatwinbtns",list:[
                     {type:"button",name:"addspatwin",value:"Add Spatial Window",offsetLeft:30},
                     {type:"newcolumn",offset:30},
                     {type:"button",name:"clearspatwin",value:"Remove selected spatial windows"},
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
  myForm.hideItem("evtfiltblk");
  myForm.hideItem("spatwinblk");
  myForm.hideItem("subarrayblk");
  myForm.hideItem("mosteffblk");
  myForm.hideItem("altblk");
  myForm.hideItem("subinstr");
  var istr = myForm.getContainer("subinstr");
  istr.innerHTML="<span style='padding-left:20px;' class='fldhelpsmall'>Please  ensure that your target falls within the chosen subarray. (e.g. use <a href='javascript:openHelpWindow(\"#Subarray\")'>ObsVis:</a>)</span>";
  istr = myForm.getContainer("wininstr");
  istr.innerHTML = TGT_TBL_INSTR;

  myForm.enableLiveValidation(true);
  buildTooltips(myForm);


  myForm.attachEvent("onAfterValidate", after_validate);
  myForm.attachEvent("onChange",handle_change);
  myForm.attachEvent("onBeforeChange",before_change);
  myForm.attachEvent("onValidateError",handle_error);
  myForm.attachEvent("onButtonClick",function(id){
            clear_status();
            if (id=="save") save_changes();
            else if (id=="discard") reset_fields();
            else if ( id=="clearspatwin") clear_spatwin();
            else if ( id=="addspatwin") add_entry();

  });

  spatwingrid=new dhtmlXGridObject('grid');
  spatwingrid.setHeader("Remove?,<a href='javascript:openHelpWindow(\"#ChipforWindow\")'>Chip&nbsp;Id</a>,<div title='Accept 1:n events(0=none 1=all etc.)'><a href='javascript:openHelpWindow(\"#SampleRateforWindow\")'>Sampling Frequency</a></div>,<div title='1-1023'><a href='javascript:openHelpWindow(\"#StartColumnforWindow\")'>Start Column</a></div>,<div title='1-1024'><a href='javascript:openHelpWindow(\"#WidthofWindow\")'>Column Width</a></div>,<div title='1-1023'><a href='javascript:openHelpWindow(\"#StartRowforWindow\")'>Start Row</a></div>,<div title='1-1024'><a href='javascript:openHelpWindow(\"#HeightofWindow\")'>Row Height</a></div>,<div title='keV'><a href='javascript:openHelpWindow(\"#LowerThresholdforWindow\")'>Lower&nbsp;Energy Threshold</a></div>,<div title='keV'><a href='javascript:openHelpWindow(\"#EnergyRangeforWindow\")'>Energy Range</a></div>");
  //spatwingrid.setInitWidths("55,90,90,90,90,90,90,100,90");
  spatwingrid.setInitWidthsP("6,12,12,12,11,12,11,12,12");
  spatwingrid.setColumnMinWidth("20,20,20,20,20,20,20,20,20");
  spatwingrid.setColAlign("center,center,center,center,center,center,center,center,center");
  spatwingrid.setSkin("dhx_skyblue");
  spatwingrid.setColTypes("ch,co,ed,ed,ed,ed,ed,ed,ed");
  spatwingrid.setImagePath(CSS_IMG_PATH);
  spatwingrid.enableValidation("false,true,true,true,true,true,true,true,true");
 // spatwingrid.setColValidators("null,NotEmpty,ValidSF,ValidSP,ValidSP2,ValidSP,ValidSP2,ValidET,ValidER");
  spatwingrid.enableAutoHeight(true,200);
  spatwingrid.enableAutoWidth(true);
  spatwingrid.enableRowsHover(true,"gridhover");
  spatwingrid.enableColSpan(true);

  buildChips(spatwingrid.getCombo(1));
  spatwingrid.attachEvent("onRowSelect",function (id,ind) {
     spatwingrid.editCell();
  });
  spatwingrid.attachEvent("onEditCell",edit_cell);
  spatwingrid.init();


  set_status("");
  loadPage();
}   
