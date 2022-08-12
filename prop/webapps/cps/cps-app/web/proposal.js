  var MAX_COIS=50;
  var MAX_OBS=50;
  var MAX_PRE_LEAD=364;
  var MAXMC_PRE_LEAD=1000;
  var MAX_SPATWIN=36;
  var CSS_IMG_PATH = "/cps-app/dhtmlx-skyblue/imgs/";
  var NAV_IMG_PATH = "/cps-app/dhtmlx-web/imgs/";
  var SAVE_FAILED = "<span class='errmsgb'>**Save failed.</span><br>";
  var SAVE_OK = "<span style='font-weight:bold;'>Save successful.</span><br>";
  var TGT_TBL_INSTR = "<span class='instrTxtNoPad'>Click in table cell to edit an entry</span>";
  var HELP_WIN="width=850,height=675,toolbar=yes,menubar=no,scrollbars=yes,resizable=yes";
  var HELP_URL="/soft/cps/Chandra.help.html";
  var HELP_DDT_URL="/soft/cps/ChandraDDT.help.html";
  var DISABLED_CELL_BG = "#dddddd";
  var DONOTCLEARSTATUS = "DONOTCLEARSTATUS";
  var UNSAVED_CHANGES = "There are unsaved changes.<br>Press [Continue] and LOSE all changes.<br>Press [Return to form] to save your changes.";  
  var style_init="none";
  var style_load="block";

  var valRangeArray = new Array();
  valRangeArray["requested_budget"] = new Array(1,1000);
  valRangeArray["obs_time"] = new Array(1,10000);
  valRangeArray["obs_time_1"] = new Array(1,10000);
  valRangeArray["obs_time_2"] = new Array(1,10000);
  valRangeArray["hst_time"] = new Array(1,250);
  valRangeArray["xmm_time"] = new Array(0.001,1000);
  valRangeArray["swift_time"] = new Array(0.001,500);
  valRangeArray["nustar_time"] = new Array(0.001,1000);
  valRangeArray["noao_time"] = new Array(0.1,99);
  valRangeArray["nrao_time"] = new Array(0.1,336);
  valRangeArray["vmagnitude"] = new Array(-15.0,20.0);
  valRangeArray["phase_start"] = new Array(0.001,1);
  valRangeArray["phase_end"] = new Array(0.001,1);
  valRangeArray["phase_start_margin"] = new Array(0,.5);
  valRangeArray["phase_end_margin"] = new Array(0,.5);
  valRangeArray["group_interval"] = new Array(0,364);
  valRangeArray["coord_interval"] = new Array(0,364);
  valRangeArray["subarray_start"] = new Array(1,924);
  valRangeArray["subarray_rows"] = new Array(100,1024);
  valRangeArray["eventfilter_lower"] = new Array(0.08,15.0);
  valRangeArray["eventfilter_lower_ACIS-I"] = new Array(0.36,15.0);
  valRangeArray["eventfilter_lower_ACIS-S"] = new Array(0.24,15.0);
  valRangeArray["eventfilter_range"] = new Array(0.1,15.0);
  valRangeArray["secondary_exp_count"] = new Array(0,15.0);
  valRangeArray["primary_exposure_time"] = new Array(0.2,10.0);
  valRangeArray["frame_time"] = new Array(0,10.0);
  valRangeArray["sim_trans_offset"] = new Array(-190.500,126.621);
  valRangeArray["sim_trans_offset_HRC-S"] = new Array(-12.5439,61.3518);
  valRangeArray["sim_trans_offset_HRC-I"] = new Array(-61.3518,126.621);
  valRangeArray["sim_trans_offset_ACIS-S"] = new Array(-190.5,22.5685);
  valRangeArray["sim_trans_offset_ACIS-I"] = new Array(-22.5685,27.4739);

  valRangeArray["y_det_offset"] = new Array(-120.0,120.0);
  valRangeArray["z_det_offset"] = new Array(-120.0,120.0);

  valRangeArray["y_det_offset_ACIS-I"] = new Array(-10.0,10.0);
  valRangeArray["z_det_offset_ACIS-I"] = new Array(-10.0,10.0);
  valRangeArray["y_det_offset_ACIS-S"] = new Array(-30.0,30.0);
  valRangeArray["z_det_offset_ACIS-S"] = new Array(-30.0,30.0);
  valRangeArray["y_det_offset_HRC-I"] = new Array(-20.0,20.0);
  valRangeArray["z_det_offset_HRC-I"] = new Array(-20.0,20.0);
  valRangeArray["y_det_offset_HRC-S"] = new Array(-50.0,50.0);
  valRangeArray["z_det_offset_HRC-S"] = new Array(-50.0,50.0);


  valRangeArray["probability"] = new Array(0.1,1);
  valRangeArray["start"] = new Array(0.1,180);
  valRangeArray["stop"] = new Array(0,365);
  valRangeArray["tgtcnt"] = new Array(1,50);
  valRangeArray["tgtcnttot"] = new Array(1,999);
  valRangeArray["spectra_max_count"] = new Array(1,100000);
  valRangeArray["spwin_start"] = new Array(1,1023);
  valRangeArray["spwin_width"] = new Array(1,1024);
  valRangeArray["spwin_sfreq"] = new Array(0,255);

  var statusVals = {};
  statusVals["APPROVED"] = "Approved";
  statusVals["PROPOSED"] = "Submitted";
  statusVals["REJECTED"] = "Not Approved";
  statusVals["INCOMPLETE"] = "In progress";
  statusVals["WITHDRAWN"] = "Withdrawn";
  statusVals["HOLD"] = "Hold";

function getProposalStatus(kval) {
  var retval = kval;
  if (statusVals.hasOwnProperty(kval) )
     retval = statusVals[kval];
  return retval;
}

function handle_preferred(selItem,values)
{
  var constr_flg = values[selItem];
  if (constr_flg != null && constr_flg == "P") {
    // old field could have  preferred constraint
    var opts= myForm.getOptions(selItem);
    if (opts.length < 3) {
      opts.add(new Option("Preferred","P"));
    }
  }
}



function set_cell_attr(thegrid,rowId,celInd,isvalid)
{
    if (!isvalid) {
      thegrid.cells(rowId,celInd).setTextColor('#dd0000');
    }
    else {
      thegrid.cells(rowId,celInd).setTextColor('black');
    }
}

function after_validate(status) {
  if (!status) {
     document.body.className="";
     myForm.enableItem("save");
  }
}
 
function set_label_req(str) {
   var retstr= str.replace(/normal/,"bold");
   return retstr;
}
function set_label_opt(str) {
   var retstr= str.replace(/bold/,"normal");
   return retstr;
}

function handle_error(name,value,res) {
  if (!res ) {
    
    set_errlbl(true,myForm,name);
    var userData = ""
    var obj = myForm.getInput(name);
    if (obj != null){
      userData=myForm.getUserData(name,"tooltip");
      if (userData == null) userData ="";
    }
  
    var lbl=myForm.getItemLabel(name);
    // find just label if this is a link reference
    var arr = lbl.split("href");
    if (arr.length >1) {
      var arr2= arr[1].split(">");
      arr= arr2[1].split("<");
      lbl= arr[0];
    }
    lbl = lbl.toString().trim();
    if (lbl != null &&  lbl != "" ) {
        set_status("<span class='errmsg'>Invalid input:</span> " + lbl + "  " + userData + "<br>");
    }
  } else  {
    set_errlbl(false,myForm,name);
  }
}

function set_editButtons(f,isEdit)
{
  if (isEdit != "true") {
    f.disableItem("save");
    f.disableItem("discard");
    //f.disableItem("clear");
    disable_formFields(f);
  }
  else {
    f.enableItem("save");
    f.enableItem("discard");
  }
}

function clear_select(f,ss) {
  var opts = f.getOptions(ss);
  for (var i=0; i<opts.length; i++) {
    opts[i].selected = false;
  }
}

function openHelpWindow(ele)
{

   var url = HELP_URL;
   if (top.parent.helpurl != null) 
     url = top.parent.helpurl;
   if (ele) url +=  ele;
   var helpwin = window.open(url,'cpshelpWindownWNyQx18M',HELP_WIN);
   helpwin.focus();
   helpwin.focus();
}

function check_fields(theform,arr) 
{
  var ele;
  var retval = 0;
  for (var ii=0;ii<arr.length;ii++) {
    ele = theform.getItemValue(arr[ii]);
    if (ele && ele.toString().trim().length>0) {
      retval =1;
      break;
    }
  }
  return retval;
}
function validate_yn(ynval,eleForm,arr) 
{
  return validate_yn_flg(ynval,eleForm,arr,"Yes");
}
function validate_ynp(ynval,eleForm,arr) 
{
  return validate_yn_flg(ynval,eleForm,arr,"Y or P") ;
}

function validate_yn_flg(ynval,eleForm,arr,flg) 
{
  var retval = true;
  var str="";
  var lbl = eleForm.getItemLabel(arr[0]);
  if (ynval != 'N') {
    for (var ii=1;ii<arr.length;ii++) {
      ele = eleForm.getItemValue(arr[ii]);
      if (ele != null) ele = ele.toString().trim();
      if (!ele || ele.length==0) {
        retval=false;
        set_errlbl(true,eleForm,arr[ii]);
        str += eleForm.getItemLabel(arr[ii]) + " is required for " + lbl + "<br>";
      }
    }
  } else {
    for (var ii=1;ii<arr.length;ii++) {
      ele = eleForm.getItemValue(arr[ii]);
      if (ele != null) ele = ele.toString().trim();
      if (ele && ele.length>0) {
        retval=false;
        set_errlbl(true,eleForm,arr[ii]);
        str += lbl + " must be " + flg + " if " + eleForm.getItemLabel(arr[ii]) + " is entered." + "<br>";
      }
    }
  }
  return str;
}

function validate_dependencies(eleForm,arr) 
{
  var ele;
  var cnt= 0;
  var retval = true;
  var vals = new Array();
  for (var ii=0;ii<arr.length;ii++) {
    ele = eleForm.getItemValue(arr[ii]);
    if (ele != null) ele = ele.toString().trim();
    vals.push(ele);
    if (ele && ele.length>0) {
      cnt += 1;
    }
  }

  if (cnt > 0 && cnt != arr.length) {
    retval=false;
    for (var ii=0;ii<vals.length;ii++) {
       if (!vals[ii] || vals[ii].length <= 0) {
         set_errlbl(true,eleForm,arr[ii]);
       }
    }
  }
  return retval;
}

function validate_range_special(formEle,key,fld)
{
  var retval=true;

  // we have a range to check for the key
  var tarr = valRangeArray[key];
  var msg= "Valid range is between " +tarr[0] + " and " + tarr[1];
  formEle.setUserData(fld,"tooltip",msg);

  set_errlbl(false,formEle,fld);
  var ival = formEle.getItemValue(fld);
  if (ival != null && ival.toString().trim() != "") {
    var rs = parseFloat(ival);
    if (isNaN(rs) || ival.toString().trim() != rs) {
      set_errlbl(true,formEle,fld);
      retval=false;
    }
    else
    {
      if (ival < tarr[0] || ival > tarr[1]) {
        // out-of-range
        set_errlbl(true,formEle,fld);
        retval=false;
      } 
    }
  }
  return retval;
}


function validate_range(formEle,key) {
  var retval=true;
  var rs;
  var msg;

  set_errlbl(false,formEle,key);
  var tarr = valRangeArray[key];
  
  msg= "Valid range is between " +tarr[0] + " and " + tarr[1];
  formEle.setUserData(key,"tooltip",msg);

  var ival = formEle.getItemValue(key);
  if (ival != null && ival.toString().trim() != "") {
    rs = parseFloat(ival);
    if (isNaN(rs) || ival.toString().trim() != rs) {
      set_errlbl(true,formEle,key);
      retval=false;
    }
    else
    {
      // we have a range to check for the key
      var tarr = valRangeArray[key];
      if (ival < tarr[0] || ival > tarr[1]) {
        // out-of-range
        set_errlbl(true,formEle,key);
        retval=false;
      } 
    }
  }
  return retval;
}

function validate_irange(formEle,key)
{
  var retval=true;
  var rs;
  var msg;

  set_errlbl(false,formEle,key);
  var tarr = valRangeArray[key];
  
  msg= "Valid range is integer value between " +tarr[0] + " and " + tarr[1];
  formEle.setUserData(key,"tooltip",msg);

  var ival = formEle.getItemValue(key);
  if (ival != null && ival.toString().trim() != "") {
    rs = parseInt(ival);
    if (isNaN(rs) || ival.toString().trim() != rs) {
      set_errlbl(true,formEle,key);
      retval=false;
    }
    else
    {
      // we have a range to check for the key
      var tarr = valRangeArray[key];
      if (ival < tarr[0] || ival > tarr[1]) {
        // out-of-range
        set_errlbl(true,formEle,key);
        retval=false;
      } 
    }
  }
  return retval;
}

function get_range(key)
{
  var str="";
  var tarr = valRangeArray[key];
 
  if (tarr && tarr.length > 1) {
    str += "Valid range is between " + tarr[0] + " and " +tarr[1];
  }
  return str;  
}

function get_irange(key)
{
  var str="";
  var tarr = valRangeArray[key];
 
  if (tarr && tarr.length > 1) {
    str += "Valid range is integer between " + tarr[0] + " and " +tarr[1];
  }
  return str;  
}

function set_errlbl(isErr,formEle,key)
{
  var lbl=formEle.getItemLabel(key);
  if (isErr && lbl) {
    if (lbl.indexOf("class=\"errlbl\"") > 0) {
    } else {
      if (lbl.indexOf("href") > 0) {
        str = lbl.replace(/href=/,"class=\"errlbl\" href=");
        formEle.setItemLabel(key,str);
      } 
    } 
  } else {
    if (lbl && lbl.indexOf("href") > 0) {
      var str = lbl.replace(/class=\"errlbl\"/,"");
      formEle.setItemLabel(key,str);
    }
  }
}


function set_errlbl_old(b,formEle,key)
{
  var lbl=formEle.getItemLabel(key);
  if (b) {
    if (lbl.indexOf("class='errlbl'") > 0) {
    } else {
      var str = "<span class='errlbl'>" + lbl + "</span>";
      formEle.setItemLabel(key,str);
    }
  } else {
    if (lbl.indexOf("errlbl") > 0) {
       var ii = lbl.indexOf(">");
       var ss = lbl.lastIndexOf("<");
       var str = lbl.substr(ii+1,ss);
       formEle.setItemLabel(key,str);
    }
  }
}

function getGridValues(igrid)
{
  var gridvalues="";
  var rowcnt = igrid.getRowsNum();
  for (var ii=0;ii<rowcnt; ii++) {
    var rid = igrid.getRowId(ii);
    gridvalues += "[" + rid + ",";
    for (var cc=0;cc<igrid.getColumnsNum();cc++) {
      gridvalues +=  igrid.cellById(rid,cc).getValue() + ",";
    }
    gridvalues += "]";
  }
  return gridvalues;
}


function convertDate(inVal)
{
  var d1= inVal;
  // inval = "dd-mmm-yyyy hh:mm"
  monarr=new Array("Jan","Feb","Mar","Apr","May","Jun",
                   "Jul", "Aug","Sep","Oct","Nov","Dec");

  var dtmp = d1.replace(/ /g,"-");
  d1 = dtmp.replace(/:/g,"-");
  var darr=d1.split("-");
  var mm=0;
  for (mm=0;mm< monarr.length;mm++) {
    if (monarr[mm]==darr[1]) {
      break;
    }    
  }
  var sdate = new Date(darr[2],mm,darr[0],darr[3],darr[4],0,0);
  return sdate.getTime();
}

function combo_setInsensitive(ele,itext) {
  var foundIt=false;
  var str_lc = itext.toLowerCase();
  for (var ii=0;ii< ele.getOptionsCount();ii++) {
     var opt_lc = ele.getOptionByIndex(ii).value.toLowerCase();
     if (opt_lc == str_lc) {
        foundIt=true;
        ele.selectOption(ii);
     }
  }
  return foundIt;
}

function process_save(retstr,doLoad)
{
  var retval = false;
  if (retstr.indexOf("SaveOK") < 0) {
    if ((retstr.indexOf("403") > 0) || retstr == "") {
      doAlertLogoff("Invalid Session");
    }
    else {
      //alert(retstr);
      set_status("<span class='errmsg'>" + SAVE_FAILED + retstr + "</span>");
    }
  } else {
    retval=true;
    if (doLoad) loadPage();
    retstr = retstr.replace(/SaveOK/,"");
    set_status(SAVE_OK  + retstr);
  }
  return retval;
}
function set_status_err(msg)
{
  set_status("<span class='errmsg'>" + msg + "</span>");
}
function clear_success_status() {
  var obj = myForm.getContainer("status");
  val = obj.innerHTML;
  if (val.indexOf("Save successful") >= 0)
    obj.innerHTML=""
}

function set_status(val)
{
  var obj = myForm.getContainer("status");
  obj.innerHTML=val;

  if (val != null && val != "") 
    obj.scrollIntoView();
}
function set_errstatus(val)
{
  var obj = myForm.getContainer("status");
  obj.innerHTML="<span class='errmsg'>" + val + "</span>";
  obj.scrollIntoView();
}
function clear_status(val)
{
  //myForm.setItemLabel("status", val);
  var obj = myForm.getContainer("status");
  obj.innerHTML="";
}
function get_status()
{
  //myForm.setItemLabel("status", val);
  var obj = myForm.getContainer("status");
  return (obj.innerHTML);
}

function custom_date_sort(a,b,order)
{
  var aval=Number.MAX_VALUE;
  var bval=Number.MAX_VALUE;
  if (a.length > 0) aval = convertDate(a);
  if (b.length > 0) bval = convertDate(b);

  if (order=="asc") 
      return aval>bval?1:-1;
  else
      return aval<bval?1:-1;

}
function custom_str_sort(a,b,order)
{
    return (a.toLowerCase()>b.toLowerCase()?1:-1)*(order=="asc"?1:-1);
 
}


function disable_formFields(f)
{
    f.forEachItem(function(name){
        var tt= f.getItemType(name);
        if (tt=="input") {
          if (!f.isItemHidden(name))
            f.setReadonly(name,true);
        } else if (tt=="radio") {
           // would have to use onbeforechange or hardcode disable
        } else if (tt=="file") {
           f.disableItem(name);
        } else if (tt=="multiselect") {
           f.disableItem(name); 
        } else if (tt=="select") {
           f.disableItem(name); 
        } else if (tt=="combo") {
           f.disableItem(name);
        } else if (tt=="checkbox") {
           f.disableItem(name);
        }
        //else {alert(name + "--" + tt); }
     });

   return true;
}

function setTimeoutMsg(msg)
{
  top.parent.timeoutFS.innerHTML=msg;
}

function showTimeoutWarn(showit)
{
  if (top.parent.timeoutFS != null)  {
    if (showit) {
      top.parent.timeoutFS.style.display = 'block' ;
    }  
    else {
       top.parent.timeoutFS.style.display = 'none' ;
    }
  } else {
  }
}

function validRange(f,name)
{
  clear_status();
  set_errlbl(false,f,name);
  if (!validate_range(f,name)) {
    set_errlbl(true,f,name);
    var str = f.getItemLabel(name) + " is invalid. " + get_range(name) + "<br>";
    set_status(str);
  }
}

function validNumeric(f,name)
{
   var retval=true;
   set_errlbl(false,f,name);
   a = f.getItemValue(name);
   if (!a.toString().match(/(^-?\d\d*[\.|,]\d*$)|(^-?\d\d*$)|(^-?[\.|,]\d\d*$)/))  {
     set_errlbl(true,f,name);
     set_status(f.getItemLabel(name) + ": is invalid.");
     retval=false;
   } else {
     clear_status();
   }
   return retval;
}
   
function decodeEntities(encodedString) {
    var textArea = document.createElement('textarea');
    textArea.innerHTML = encodedString;
    return textArea.value;
}
function find_tgt_next_tab() {
  
  var isactive=false;
  top.document.maintgttabbar.goToNextTab();
  while (!isactive) {
    var curtab= top.document.maintgttabbar.getActiveTab()
    if (!top.document.maintgttabbar.tabs(curtab).isEnabled()) 
      top.document.maintgttabbar.goToNextTab();
    else 
      isactive=true;
  }
  showHideNav(top.document.maintgttabbar.getActiveTab());
}

function find_tgt_prev_tab() {
  var isactive=false;
  top.document.maintgttabbar.goToPrevTab();
  while (!isactive) {
    var curtab= top.document.maintgttabbar.getActiveTab()
    if (!top.document.maintgttabbar.tabs(curtab).isEnabled()) 
      top.document.maintgttabbar.goToPrevTab();
    else 
      isactive=true;
  }
  showHideNav(top.document.maintgttabbar.getActiveTab());
} 


function find_next_tab()
{ 
  var nid = null;
  var sid = parent.mainTabbar.getSelectedItemId();
  var pid = parent.mainTabbar.getParentId(sid);
  var idx = parent.mainTabbar.getIndexById(sid);
  var str =parent.mainTabbar.getSubItems(pid);
  if (str != null) {
     var cnt= occurrences(str,",",false);
     if (idx<cnt)
        nid = parent.mainTabbar.getChildItemIdByIndex(pid,(idx+1));
  }
  
  if (nid != null) {
    showHideNav(nid);
    parent.mainTabbar.selectItem(nid,true);
  }
  
}

function find_prev_tab()
{ 
  var nid = null;
  var sid = parent.mainTabbar.getSelectedItemId();
  var pid = parent.mainTabbar.getParentId(sid);
  var idx = parent.mainTabbar.getIndexById(sid);
 
  if (idx > 0)  
    nid = parent.mainTabbar.getChildItemIdByIndex(pid,(idx-1));

  if (nid != null) {
    showHideNav(nid);
    parent.mainTabbar.selectItem(nid,true);
  }
  
}

function showHideNav(nid) {
  var obj = parent.document.getElementById("spannext");
  if (obj) {
    obj.style.display="block";
    if (nid.indexOf("Submit") >= 0 || nid.indexOf("t8") >= 0)
      obj.style.display="none";
  }
  obj = parent.document.getElementById("spanprev");
  if (obj) {
    obj.style.display="block";
    if (nid.indexOf("Proposal") > 0 || nid.indexOf("t1") >= 0)
      obj.style.display="none";
  }
}


/** Function that count occurrences of a substring in a string;
 * @param {String} string               The string
 * @param {String} subString            The sub string to search for
 * @param {Boolean} [allowOverlapping]  Optional. (Default:false)
 *
 * @author Vitim.us https://gist.github.com/victornpb/7736865
 * @see Unit Test https://jsfiddle.net/Victornpb/5axuh96u/
 * @see http://stackoverflow.com/questions/4009756/how-to-count-string-occurrence-in-string/7924240#7924240
 */
function occurrences(string, subString, allowOverlapping) {

    string += "";
    subString += "";
    if (subString.length <= 0) return (string.length + 1);

    var n = 0,
        pos = 0,
        step = allowOverlapping ? 1 : subString.length;

    while (true) {
        pos = string.indexOf(subString, pos);
        if (pos >= 0) {
            ++n;
            pos += step;
        } else break;
    }
    return n;
}


function cps_style_init()
{
  //obj = document.getElementById("formB_container");
  //obj.style.display=style_init;

  // display loading icon
  obj = document.getElementById("msgbody");
  if (obj)
    obj.style.display=style_load;

  // hide the page
  obj = document.getElementById("pagebody");
  if (obj)
    obj.style.display=style_init;
}


function cps_style_load()
{
  //obj = document.getElementById("formB_container");
  //obj.style.display=style_load;
  // hide the loading icon
  obj = document.getElementById("msgbody");
  if (obj)
    obj.style.display=style_init;

  // display the page
  obj = document.getElementById("pagebody");
  if (obj)
    obj.style.display=style_load;
}

function alertReturn(result)
{
}

function badalertReturn() {
  if (top.parent.mainTabbar) top.parent.mainTabbar.selectItem("m1",true,false);
}



function doAlertLogoff(msg)
{
  dhtmlx.alert({ type:"alert",
        type: "confirm-dialog",
        text: msg,
        width:"auto",
        callback: function(result) {
          top.location.replace("/cps-app/prop_logout.jsp");
        }
  });
  return;
}


function doAlert(msg,donefunc)
{
  dhtmlx.alert({ type:"alert",
        type: "confirm-dialog",
        text: msg,
        width:"auto",
        callback: donefunc
  });
  return;
}

function doConfirm(msg,donefunc)
{
  dhtmlx.confirm({ type:"confirm",
        type: "confirm-dialog",
        text: msg,
        width:"auto",
        callback: donefunc
  });
  return;
}

function getCookie(c_name)
{
  var i,x,y,ARRcookies=top.document.cookie.split(";");
  for (i=0;i<ARRcookies.length;i++)
  {
    x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
    y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
    x=x.replace(/^\s+|\s+$/g,"");
    if (x==c_name)
    {
      return unescape(y);
    }
  }
  
  return null;
}

function setChanges(value)
{
  var exdate=new Date();
  exdate.setDate(exdate.getDate() + 7);
  var c_value=escape(value) + ";path=\"/\"; expires="+exdate.toUTCString();
  top.document.cookie="unsavedChanges" + "=" + c_value;
}


function clearTargetOptions()
{
  if (top.parent.tgtOptions != null) {
     for (var ii=top.parent.tgtOptions.length; ii> 0;ii--) {
        top.parent.tgtOptions.pop();
     }
  }
  top.parent.tgtOptions=null;
}

function buildTargetOptions(thegrid)
{
  clearTargetOptions();
  top.parent.tgtOptions = new Array();
  for (var ii=0;ii<thegrid.getRowsNum();ii++) {
     var opts = new Array();
     opts.push(thegrid.getRowId(ii));
     var tstr = '#' + thegrid.cellByIndex(ii,0).getValue();
     tstr += " " + thegrid.cellByIndex(ii,1).getValue();
     opts.push(tstr);
     top.parent.tgtOptions.push(opts);
   }
} 


function validate_email(email) {
  var retval=true;
  if (email == null || email == "") retval = true;
  else if (!!email.match(/(^[a-z]([a-z0-9_\'\.\-]*)@([a-z0-9_\.\-]*)([.][a-z]{3})$)|(^[a-z]([a-z0-9_\'\.\-]*)@([a-z0-9_\-\.]*)(\.[a-z]{2,4})$)/i))
  // from web, untested. this is the full emails but not sure rest of older code
  // could handle it
  //else if (!!email.match(/^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})/) )
  {
     retval= true;
  }
  else
     retval = false;
  return retval;
}

