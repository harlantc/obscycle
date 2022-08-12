
function clearFields(f)
{  
  for (var ii=0; ii< f.length; ii++) {
    var e = f.elements[ii]
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
      e.selectedIndex = -1;
    }
    else if (e.type == "select-one") {
      e.selectedIndex = 0;
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
function isNumber(e)
{
  if(e.value != parseFloat(e.value)) {
    emsg = e.name;
    emsg += " must be numeric.\n";
    alert(emsg);
    e.color = "red";
    return false;
  }
  return true;
}

function clearScrollPosition()
{
  document.cookie = "frame_scroll_y=-1" ;
}
function getScrollPosition()
{
  var yy;
  var msg;

  if (navigator.appName.indexOf("Microsoft") != -1) {
    yy = document.body.scrollTop;
  }
  else {
    yy = self.pageYOffset;
  }
  document.cookie = "frame_scroll_y=" + yy;
  return true;
}
function setScrollPosition()
{
  var msg;
  var val;
  var yy;
  var allcookies;
  var pos;
  var start;
  var end;

  allcookies = document.cookie;
  pos = allcookies.indexOf("frame_scroll_y=");
  if (pos > -1) {
    start = pos + 15;
    end = allcookies.indexOf(";",start);
    if (end == -1) end = allcookies.length;
    val = allcookies.substring(start,end);
    yy = parseInt(val);
  }
  if (yy > -1) {
    window.scrollTo(0,yy);
  }
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


function validateSearchCriteria(f)
{
  var fldCheck=0;

  f.errmsg.value="";
  for (var ii=0; ii< f.length; ii++) {
    var e = f.elements[ii];
    if (e.type == "text" &&
        (e.name == "obsid" || e.name == "seqnbr" ||
         e.name == "propnum" || e.name == "pilast") ) {
      if ( isBlank(e.value) ) {
      }
      else {
        fldCheck= fldCheck + 1;
        if (e.name == "obsid") {
          if(e.value != parseInt(e.value)) {
            f.errmsg.value = "Observation Id must be numeric.";
            fldCheck= -9;
          }
        }
        else if (!validWildcard(e.value)) {
          f.errmsg.value = "At least 2 characters must be specified.";
          fldCheck = -9;
        }
      }
    }
  }
  if (fldCheck == 0) {
    f.errmsg.value="Please specify a search criteria.";
    return false;
  }
  else if (fldCheck > 1 ) {
    f.errmsg.value="Please specify only 1 search criteria.";
    return false;
  }
  else if (fldCheck < 0 ) {
    return false;
  }
  else {
    return true;
  }
}

function validWildcard(s)
{
  var xx;
  var ccnt;

  ccnt = 0;

  for (xx=0; xx < s.length; xx++) {
     var ch = s.charAt(xx);
     if (ch != '\%')  {
        ccnt = ccnt + 1;
     }
     else if (ccnt < 2) {
        ccnt = 0;
     }
        
  }
  if (ccnt > 1) {
    return true;
  } 
  else  {
    return false;
  } 
}

function verifyUpdate(f)
{
  var msg;
  var val;
  var pos;

  val = f.operation.value;
  if (val == "Send" || val == null) {
    msg = "Are you sure you want to update the ObsCat and SEND a TOO Status Message?";
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
function verifyTriggerSubmit() {
  var msg;
  msg = "Are you sure you want to Trigger this TOO observation? ";
  if (confirm(msg)) {
     return true;
  }
  return false;
}

function verifySubmit(f)
{
  var msg;
  var val;
  var pos;
  var sendP = /^send/i;
  var saveP = /^save/i;

  val = f.operation.value;
  if (val == null || saveP.test(val)) {
    msg = "Are you sure you want to SAVE the data to the database?";
  }
  else if (sendP.test(val)) {
    msg = "Are you sure you want to SAVE the data and SEND a TOO Status Message?";
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

