
function showHide(f,btn,elem,helem)
{
  var hiddenEle ;
  for (var ii=0; ii< f.length; ii++) {
    if (f.elements[ii].name == helem) {
       hiddenEle = f.elements[ii];
       break;
    }
  }

  if (document.getElementById(elem).style.display == 'none')   {
      document.getElementById(elem).style.display = 'block' ;
      btn.src="small_tri_down.gif";
      hiddenEle.value = "on" ;
  }
  else {
      btn.src="small_tri_right.gif";
      document.getElementById(elem).style.display = 'none' ;
      hiddenEle.value = "off" ;
  }
}



function clearFields(f)
{  
  for (var ii=0; ii< f.length; ii++) {
    var e = f.elements[ii];
    if (e.type == "text") {
      e.value = "";
    }
    else if (e.type == "radio") {
      e.checked = false;
    }
    else if (e.type == "checkbox") {
      e.checked = false;
    }
    else if (e.type == "select-multiple") {
      for (var jj=0;jj<e.options.length;jj++){
        e.options[jj].selected=false;
      } 
    }
    else if (e.type == "select-one") {
      if (e.name != "Coordinator") {
        for (var jj=0;jj<e.options.length;jj++){
          e.options[jj].selected=false;
        } 
      } 
    }
  }
}


function validateText(e,maxLength) 
{

  var msg;
  if (e.value.length >= maxLength) {
    msg = e.name;
    msg += "\nYour comments must be ";
    msg += maxLength;
    msg += " characters or less.";
    alert(msg);
    e.focus();
    return false;
  } 
  else {
    return true;
  }
}

function statusChanged(inE,fupFlg)
{
  var f = inE.form;
  var appFld = inE.name;
  appFld += "-appTime";

  var gridFld = inE.name
  gridFld += "-grid";
  var grid = getElement(gridFld,f);
  gridFld += "Hidden";
  var gridH = getElement(gridFld,f);

  var cxcstartFld = inE.name
  cxcstartFld += "-cxcstart";
  var cxcstart = getElement(cxcstartFld,f);

  var fpFld = inE.name
  fpFld += "-fastproc";
  var fastproc = getElement(fpFld,f);

  var fpFld = inE.name
  fpFld+= "-fastprocCmt";
  var fastprocCmt = getElement(fpFld,f);

  if (inE.options[inE.selectedIndex].value != "accepted") {
    
    if (grid != null) {
      grid.value = 0;
    }
    if (fastproc != null) {
      fastproc.value="";
      fastproc.disabled=true;
    } 
    if (fastprocCmt != null) {
      fastprocCmt.value="";
      fastprocCmt.readOnly=true;
      fastprocCmt.style.backgroundColor="#dddddd";
    }

    if (fupFlg == 0) {
      var a = getElement(appFld,f);
      a.value = 0.0;
      rejectFups(inE);
    }
  } else {
    /* if this is the main target, go reset the followups */
    if (grid != null && gridH != null) {
       grid.value = gridH.value;
    }
    if (fupFlg == 0) {
      acceptFups(inE);
    }
    if (fastproc != null && cxcstart != null)
      fastproc.disabled=false;
    if (fastprocCmt != null && cxcstart != null)  {
      fastprocCmt.readOnly=false;
      fastprocCmt.style.backgroundColor="white";
     }
  }
  return true;
}
      
/* for followups, find the target field name, then  */
/* resum the total approved time */
function sumFups2(inE)
{
  var x = inE.name;
  var f = inE.form;
  var y = parseInt(x);
  for (var ii=0; ii< f.length; ii++) {
    var e = f.elements[ii];
    if (e.name == y) {
       sumFups(e);
    }
  }
}

function getElement(ename,f)
{
  var retE = null;
  var ele;

  for (var ee=0; ee< f.length; ee++) {
    ele = f.elements[ee];
    if (ele.name == ename) {
      retE = ele;
      ee=f.length;
    }
  }
  return retE;
}

function getFupSize(fupFld,f)
{
  var fupSize = 0;

  fupFld += "-fupSize";
  /* first find how many followups */
  for (var ii=0; ii< f.length; ii++) {
    var e = f.elements[ii];
    if (e.name == fupFld) {
      fupSize = e.value;
    }
  }
  
  return fupSize;
}

/* reject all the followup observations because the    */
/* main target was rejected.  inE is the status column */
/* for the target with field name=targid               */
function rejectFups(inE)
{
  var f = inE.form;
  var fupFld = inE.name;
  var fupSize;
  var fupLbl;

  /* first find how many followups */
  fupSize = getFupSize(fupFld,f);
  
  /* now reject all the fups */
  var statFld = inE.name;
  for (var jj=0; jj<= fupSize ; jj++) {
    fupLbl = statFld + "-" + jj;
    var ele = getElement(fupLbl,f);
    if (ele != null) {
         ele.selectedIndex=inE.selectedIndex;
    }
  }
   
}
/* accept all the followup observations because the     */
/* main target was accepted.  Only accept followups not */
/* set to rejected.  inE is the status column           */
/* for the target with field name=targid                */
function acceptFups(inE)
{
  var f = inE.form;
  var fupFld = inE.name;
  var fupSize;
  var fupLbl;
  var fupExpLbl;
  var fupAppLbl;
  var exptime;

  /* first find how many followups */
  fupSize = getFupSize(fupFld,f);
  
  /* now accept all the followups */
  appFld = inE.name;
  
  for (var jj=1; jj<= fupSize ; jj++) {
    fupLbl = appFld + "-" + jj;
    var ele = getElement(fupLbl,f);
    if (ele != null ) {
         ele.selectedIndex=1;
    }
  }
   
}

function sumFups(inE)
{
  var f = inE.form;
  var fupFld  = inE.name + "-fupSize";
  var appFld  = inE.name + "-appTime";
  var initFld = inE.name + "-initTime";
  var expFld  = inE.name + "-expTime";
  var fupTime = 0.0;
  var fupSize = 0;
  var initTime = 0.0;
  var expTime = 0.0;
  var theTgtApp;
  var rejectTgt = 0;
  var acceptTgt = 0;

  /* first find how many followups */
  for (var ii=0; ii< f.length; ii++) {
    var e = f.elements[ii];
    if (e.name == fupFld) {
      fupSize = e.value;
    }
    if (e.name == appFld) {
       theTgtApp = e;
    }
    if (e.name == initFld) {
      initTime = parseFloat(e.value);
      if (isNaN(initTime)) {
         initTime = 0.0;
      }
    }
    if (e.name == expFld) {
       expTime = parseFloat(e.value);
      if (isNaN(expTime)) {
         expTime = 0.0;
      }
     
    }
    if (e.name == inE.name && e.options[e.selectedIndex].value == "rejected") {
       rejectTgt = 1;
    }
    if (e.name == inE.name && e.options[e.selectedIndex].value == "accepted") {
       acceptTgt = 1;
    }
  }
  
  /* now sum up the approved exposure times */
  /* for all accepted followup observations + initial time */
  if (fupSize > 0) {
   if (rejectTgt == 0  ) {
    appFld = inE.name;
    if (acceptTgt == 1) {
      fupTime = initTime;
    }
    for (var jj=1; jj<= fupSize ; jj++) {
      fupLbl = appFld + "-" + jj;
      var ele = getElement(fupLbl,f);
      if (ele.options[ele.selectedIndex].value == "accepted") {

        /* if status is accepted, then add the exposure time to the total */
        fupLbl = appFld + "-" + jj + "-appTime";
        ele = getElement(fupLbl,f);
        if (!isNaN(parseFloat(ele.value)))    {
          fupTime += parseFloat(ele.value);
        }
      }
    }
  }
  theTgtApp.value = fupTime;
 }
 else {
   if (acceptTgt == 1  ) {
     theTgtApp.value = expTime;
   }
 }

}
  
    
function ddtStatusCheck(ele)
{
   var msg;
   var newstat;

   newstat = ele.options[ele.selectedIndex].value;
   if (newstat.indexOf("Not") >= 0 || newstat.indexOf("Withdrawn") > 0 ) {
      msg = "No targets will be accepted for this proposal.\n Please select 'Ok' to confirm this status change to '" + newstat + "'";
      return(confirm(msg));
   }
   return true;
}

  

function verifyChange(e)
{
  var msg;
  var newValue;

  if (e.type == "text") {
    newValue = e.value;
  }
  else {
    newValue = e.options[e.selectedIndex].value;
  }
  msg = "Are you sure you want to update ";
  msg += e.name;
  msg += " to \n    '";
  msg += newValue;
  msg += "' ?";
  if (confirm(msg)) {
    if (e.name == "Coordinator" ) {
      e.form.hiddenCoordinator.value = newValue;
    } else {
      e.form.hiddenCoordinator.value = "nochange";
    }
    return true;
  } else {
    return false;
  }
}
function isValidTime(e)
{
  var dval;
  
  if(isBlank(e.value) ||  e.value != parseFloat(e.value)) {
    emsg = "Approved Time must be numeric.\n";
    alert(emsg);
    e.style.backgroundColor = "red";
    return false;
  }
  dval = parseFloat(e.value);
  if (dval < 1.0) {
    emsg = "Approved Time must be >= 1 ksec.\n";
    alert(emsg);
    e.style.backgroundColor = "red";
    return false;
  }

  e.style.backgroundColor = "#ffffff";
  return true;
}
function isNumberNoAlert(e)
{
  if(!isBlank(e.value) &&  e.value != parseFloat(e.value)) {
    e.style.backgroundColor = "red";
    return false;
  }

  e.style.backgroundColor = "#ffffff";
  return true;
}

function isNumber(e)
{
  if(!isBlank(e.value) &&  e.value != parseFloat(e.value)) {
    emsg = "Value must be numeric.\n";
    alert(emsg);
    e.style.backgroundColor = "red";
    return false;
  }
  dval = parseFloat(e.value);
  if (dval < 0.0) {
    emsg = "Value must be >= 0 \n";
    alert(emsg);
    e.style.backgroundColor = "red";
    return false;
  }
  
  e.style.backgroundColor = "#ffffff";
  return true;
}

function isBlank(s)
{
  var xx;
  for (xx=0; xx < s.length; xx++) {
     var ch = s.charAt(xx);
     if ((ch != '') && (ch != ' ') && (ch != '\n') && (ch != '\t')) {
        return false;
     }
  }
  return true;
}


function verifyUpdate(f)
{
  var msg;
  var val;
  var errmsg = 0;
  var ele;

  val = f.operation.value;
  if (val.indexOf("Cancel") >= 0) { 
    msg = "Are you sure you want to CANCEL any ObsCat changes?";
  } 
  else if (val.indexOf("Apply") >= 0) { 
    for (var ee=0; ee< f.length && errmsg==0; ee++) {
      ele = f.elements[ee];
      if (ele.name.indexOf("apptime") >= 0 ) {
        if(!isBlank(ele.value) &&  ele.value != parseFloat(ele.value)) {
          errmsg = 1;
        }
        else if (parseFloat(ele.value) < 1.0) {
          errmsg = 1;
        }
      }
      else if (ele.name.indexOf("min") >= 0 ||
               ele.name.indexOf("max") >= 0) {
        if(!isBlank(ele.value) &&  ele.value != parseFloat(ele.value)) {
          errmsg = 1;
        }
        else if (parseFloat(ele.value) < 0.0) {
          errmsg = 1;
        }
      }
    }
    if (errmsg == 1) {
      alert ("Please correct any errors before applying updates.");
      return false;
    }
 
    msg = "Are you sure you want to UPDATE the ObsCat?";
  }

  if (msg == null) {
    return true;
  } 

  if  (confirm(msg)) {
    return true;
  }
  else {
    return false;
  }
}

function verifyTriggerSubmit() {
  var msg;
  msg = "Are you sure you want to Trigger this TOO observation? ";
  if (confirm(msg)) {
     return true;
  }
  return false;
}

function verifyDDTSubmit(f)
{
  var msg = null;
  var oper;
  var sendP = /status update$/i;
  var saveD = /draft/i;
  var saveP = /^save/i;
  var cancelP = /^cancel/i;
  var onlyP = /only/i;
  var tgtAccepted = 0;
  var gotF = 0;
  var tstat;
  var ddtstat;
  var appTime;
  var tlbl;
  var gridApp;
  var trigApp;
  var tottime = 0;
  var reqtime = 0;
  var xmsg = "";


 oper = f.operation.value;
 if (oper != null && cancelP.test(oper)) {
    msg = "Are you sure you want to cancel your updates for this DDT?\nNo data will be saved.\n";
    if (confirm(msg)) {
      return true;
    } else {
      return false;
    }
  }
  
  for (var ii=0; ii< f.length; ii++) {
    var e = f.elements[ii];
    if (e.type == "text") {
       if (e.name.indexOf("appTime")  > 0  ||
           e.name.indexOf("initTime") > 0  ||
           e.name.indexOf("minLead")  > 0  ||
           e.name.indexOf("maxLead")  > 0  ||
           e.name.indexOf("cxcstart") > 0  ||
           e.name.indexOf("cxcstop")  > 0  ) {
          if (!isNumberNoAlert(e)) {
            msg = "Data NOT SAVED!\n\nPlease correct invalid values for approved times, preceding min/max lead times and/or CXC start/stop values.\n";
          }
       }
    }
    else if (e.name.indexOf("requestedTime")  >= 0 ) {
      reqtime = e.value;
    }
  }
    
  if (msg == null) {
    msg = "";
    for (var ii=0; ii< f.length; ii++) {
      var fupe;
      var e = f.elements[ii];
      /* status field for main target */
      if (e.name == parseInt(e.name)) {
        gotF = 0;
        tstat = e.options[e.selectedIndex].value;
        tlbl = e.name + "-appTime";
        appTime = getElement(tlbl,f);
        tlbl = e.name + "-grid";
        gridApp = getElement(tlbl,f);
        tlbl = e.name + "-initTime";
        trigApp = getElement(tlbl,f);

        /* check status of all followups */
        var fupSize = getFupSize(e.name,f);
        for (var jj=1; jj<= fupSize ; jj++) {
          tlbl = e.name + "-" + jj;
          fupe = getElement(tlbl,f);
          if (fupe != null) {
            var fstat = fupe.options[fupe.selectedIndex].value;
            if (fstat.indexOf("accepted") >= 0) {
               gotF = 1;
            }
          }
        }

        var atime = 0.0;
        if (appTime.value != null) {
          atime= parseFloat(appTime.value);
          if (isNaN(atime)) {
            atime = 0.0;
          }
        }
        tottime = tottime + atime;
        if (tstat.indexOf("accepted") >= 0) {
          tgtAccepted = 1;
          if (atime <= 0) {
             msg += "Approved Time must be > 0 if target is accepted.\n";
          }
          if (trigApp != null && trigApp.value <= 0 && gotF == 1) {
             msg += "Approved Time for Trigger target must be > 0 if target is accepted.\n";
          }
          if (gridApp != null && gridApp.value <= 0) {
            if (msg == null) msg="";
            msg += "Number of Pointings for Grid must be > 0 for accepted target.\n";
          }
        }
        else {
          if (atime > 0) {
            msg += "Approved Time must be 0 if target is NOT accepted.";
          }
        }
      
        if (tstat.indexOf("rejected") >= 0  && gotF == 1) {
          msg += "All followup observations must be rejected if the target is rejected.\n";
        }

      }

      /* save status of the DDT */
      if (e.name != null && e.name.indexOf("TOO Status") >= 0) {
        ddtstat = e.options[e.selectedIndex].value;
      }
    }
  }


  if (msg == null || msg=="") {
    if (tgtAccepted > 0 && 
        (ddtstat.indexOf("Not") >= 0 || ddtstat.indexOf("Withdrawn") >= 0 )) {
       msg = "Target status of 'accepted' not allowed for DDT Status of " + ddtstat;
       msg += "\n\nData NOT saved!";
    }
    else if (tgtAccepted == 0 && ddtstat.indexOf("Approved") == 0 ) {
       msg= "Please accept at least 1 target for DDT Status of " + ddtstat;
       msg += "\n\nData NOT saved!";
    }
    else {
      msg = null;
    }
  }


  
  if (msg == null)  {
    if (ddtstat.indexOf("Approved") != 0  && oper != null && oper.indexOf("Migrate") >= 0) {
      msg = "Please set status of DDT to 'Approved' if you want to migrate approved targets to the ObsCat";
    }
  }

  if (msg != null ) {
    alert(msg);
    return false;
  }
  if ((oper == null || !saveD.test(oper)) && ddtstat.indexOf("Approved") >= 0) {
    xmsg = "Total Time approved is " + tottime + "ks out of " + reqtime + "ks requested.\n\n";
  }
    
  if (oper == null || saveP.test(oper)) {
    msg = xmsg;
    msg += "Are you sure you want to SAVE the data to the database?";
  }
  else if (sendP.test(oper)) {
    msg = xmsg;
    msg += "Are you sure you want to SAVE the data and SEND a DDT Status Message?";
  }
  else if (onlyP.test(oper)) {
    msg = xmsg;
    msg += "Are you sure you want to SAVE the data and SEND a DDT Comment Message?";
  }
  if (msg == null ) {
     return true;
  }
  else if  (confirm(msg)) {
    return true;
  }
  else {
    return false;
  }


}

function verifySubmit(f)
{
  var msg;
  var val;
  var pos;
  var sendP = /status update$/i;
  var saveP = /save/i;

  var toostatus;
  var e;

  e = getElement("TOO Status",f);
  if (e != null)  {
    toostatus = e.options[e.selectedIndex].value;
  }
 
  e = getElement("needObscat",f)
  

  val = f.operation.value;
  if (val == null || saveP.test(val)) {
    msg = "Are you sure you want to SAVE the data to the database?";
  }
  else if (sendP.test(val)) {
    if (toostatus != null && toostatus.indexOf("Approved") == 0 &&
        e != null && e.value == "true") {
      msg = "Please verify COORDINATES have been updated if needed.\nPress CANCEL if you need to update the ObsCat first.\n\nPress OK to send APPROVED TOO Status message.\n";
    } else {
      msg = "Are you sure you want to update status to " + toostatus + " and SEND the TOO message?";
    }
  }
  if (msg == null) {
     return true;
  }
  else if  (confirm(msg)) {
    return true;
  }
  else {
    return false;
  }
}
