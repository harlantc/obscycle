$.tablesorter.addParser({
  id: "checkbox",
  is: function(){
    return false;
  },
  format: function(s, table, cell, cellIndex) {
    var $c = $(cell),
    $input = $c.find('input[type="checkbox"]'),
    isChecked = $input.length ? $input[0].checked : '';
    // adding class to row, indicating that a checkbox is checked; includes
    // a column index in case more than one checkbox happens to be in a row
    $c.closest('tr').toggleClass('checked-' + cellIndex, isChecked);
    // returning plain language here because this is what is shown in the
    // group headers - change it as desired
    return $input.length ? isChecked ? 'checked' : 'unchecked' : s;
  },
  parsed : true, // filter widget flag
  type: "text"
});


$.tablesorter.addParser({
    id: 'inputs',
    is: function(s) {
        return false;
    },
    format: function(s, table, cell) {
        var $c = $(cell);
        // return 1 for true, 2 for false, so true sorts before false
        if (!$c.hasClass('updateInput')) {
            $c
            .addClass('updateInput')
            .bind('keyup', function() {
                $(table).trigger('updateCell', [cell, false]); // false to prevent resort
            });
        }
        return $c.find('input').val();
    },
    type: 'text'
});


function userHasTimedOutOld() {
        document.exitForm.target = "_self";
        document.exitForm.action = "/reports/reportsLogout";
        document.exitForm.operation.value="TIMEDOUT";
        document.exitForm.submit();
}



function addGroup(e)
{
  var f = e.form;
  var newgrp = e.value;
  if (!isBlank(newgrp)) {
    for (var ii=0; ii< f.length; ii++) {
      var eSel = f.elements[ii]
      var eleOpt = new Option(newgrp,newgrp);
      if (eSel.name.indexOf("grp") >= 0 ) {
        var foundit=0;
        for (var jj=0;jj< eSel.length;jj++) {
          if (eSel.options[jj].value == newgrp)  {
            foundit=1;
          }
        }
        if (foundit== 0)  {
          eSel.options[eSel.length] = eleOpt;
        }
      }
    }
  }
}
function setGrpText(e)
{
  var gval = e.options[e.selectedIndex].value;
  var pname = "prop" + e.name.substr(3);
  var f = e.form;
  for (var ii=0; ii< f.length; ii++) {
      var eSel = f.elements[ii]
      if (eSel.name.indexOf(pname) >= 0) {
        eSel.value = gval;
        e.selectedIndex = 0;
        document.groupForm.ichanged.value = "1";
      }
  }
}

function isBlank(s)
{
  var ii;

  if ( s == null || s == "") {
     return true;
  }
  for (ii=0; ii < s.length; ii++) {
     var ch = s.charAt(ii);
     if ((ch != '') && (ch != ' ') && (ch != '\n') && (ch != '\t')) {
        return false;
     }
  }
  return true;
}
function checkReportChange()
{
  if (document.theform.ichanged.value == "1") {
    return( confirm("Please be sure to save any changes first.\n Press 'Cancel' to return to the Reports page.\nPress 'Ok' to continue to the main menu. Data will NOT be saved."));
  }
  return true;
}

function checkGroupChange()
{
  if (document.groupForm.ichanged.value == "1") {
    return( confirm("Please be sure to save any changes first.\n Press 'Cancel' to return to the Proposal Groups form.\nPress 'Ok' to continue to the main menu. Data will NOT be saved."));
  }
  return true;
}

function checkGradeChange()
{
  if (document.gradesForm.ichanged.value == "1") {
    return( confirm("Please be sure to save any changes first.\n Press 'Cancel' to return to the Preliminary Grades form.\nPress 'Ok' to continue to the main menu. Data will NOT be saved."));
  }
  return true;
}

function resetGradeForm(f)
{
  for (var ii=0; ii< f.length; ii++) {
    var e = f.elements[ii]
    if (e.type == "text" && e.name.indexOf("prop") >= 0 ) {
        e.className="good-field";
    }
  }
  return true;
}

function validateGrade(field)
{
  var grade;
  var emsg;
  var tdfield;

  field.className="good-field";
  if (isBlank(field.value)) {
     return true;
  }
  grade = parseFloat(field.value);
  if(field.value != grade  || grade < 0 || grade>5.0 ) {
     field.className="bad-field";
     field.focus();
     return false;
  }
  return true;

}

/**
 * Check whether first last inst all filled in. If not alert.
 * @returns {boolean}
 */
function verifyContacts(){
  // If all 3 fields in a row filled in, number of fields should be divisible by 3
  let fields = document.querySelectorAll('input[type=text], input[list]')
  let good_field_count = 0;
  fields.forEach((field) => {
    if (!isBlank(field.value)){
      good_field_count++;
    }});
  if (good_field_count % 3 === 0){
    document.cform.submit();
    return true;
  } else{
      alert("All of first name, last name, and institute must be included for an entry.");
      return false;
  }
}


function verifyGradeForm(f)
{
  var msg;
  var gradeErr = false;
  var conflictErr = false;
  var f;
  var grade;

  for (var ii=0; ii< f.length; ii++) {
    var e = f.elements[ii]
    if (e.type == "text" && e.name.indexOf("prop") >= 0 ) {
        e.className="good-field";
        if (!isBlank(e.value)) {
           grade = parseFloat(e.value);
           if(e.value != grade  || grade < 0 || grade>5.0 ) {
               e.className="bad-field";
               gradeErr = true;
           }
           else {
            var c = f.elements[ii+1];
             if (c.checked == true) {
               conflictErr = true;
               e.className="bad-field";
             }
           }

        }
    }
  }
  msg = "";
  if (gradeErr == true) {
      msg = "Grade values must be a numeric value between 0 and 5.\n";
      msg += "Please correct invalid values and try again.\n";
  }
  if (conflictErr == true) {
      //msg += "\nPlease enter a grade OR check the Conflict box if you are unable to grade the proposal.";
      msg += "\nYou have entered both a grade and checked the Conflict box. If you have a conflict, you";
      msg += " may not enter a grade.";
  }
  if (msg != "") {
    alert(msg);
    return(false);
  }
  return true;

}


function resetScrollCookies()
{
  document.cookie = "panel_scroll_y=0";
  document.cookie = "lp_scroll_y=0";
  document.cookie = "rlist_scroll_y=0";
  document.cookie = "commentsx=0";
  document.cookie = "commentsy=0";
  document.cookie = "specificRecsx=0";
  document.cookie = "specificRecsx=0";
  document.cookie = "whyGradeNotHigherx=0";
  document.cookie = "whyGradeNotHighery=0";

  return true;

}

function resetScrollCookie(lbl) 
{
  document.cookie = lbl + "0";
  return true;

}
function setScrollPosition(lbl)
{
  var msg;
  var val;
  var yy;
  var allcookies;
  var pos;
  var start;
  var end;
  var lbllen;

  allcookies = document.cookie;
  pos = allcookies.indexOf(lbl);
  lbllen = lbl.length;
  if (pos > -1) {
    start = pos + lbllen;
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


function getScrollPosition(lbl)
{
  var yy;
  var msg;

  if (navigator.appName.indexOf("Microsoft") != -1) {
    yy = document.body.scrollTop;
  }
  else {
    yy = self.pageYOffset;
  }
  document.cookie = lbl + yy;
  return true;
}

function verifySU() {
  if (confirm("Are you really sure you want to switch your login to the selected reviewer?")) {
     return true;
  } else {
     return false;
  }
}

function doSelect(s)
{    
   var bb = s.options[s.selectedIndex].value;
   var myLink = "#" + bb;
   location.replace(myLink);
}

function showHide(elem)
{
  if (document.getElementById(elem).style.display == 'none')   {
      document.getElementById(elem).style.display = 'block' ;
  }
  else {
      document.getElementById(elem).style.display = 'none' ;
  }
}

function clearfields() {
  var x= confirm("Are you sure you want to erase all displayed values?");
  if(x) {
    var f =   document.theform;
      for (var ii=0; ii< f.length; ii++) {
      var e = f.elements[ii]
      if (e.type == "text") {
        e.value = "";
      }
      else if (e.type == "textarea") {
        if (e.name != "notes" && e.name !="cmtEdits") {
          e.value = "";
        }
      }
      else if (e.type == "radio") {
        e.checked = false;
      }
      else if (e.type == "checkbox") {
        if (e.name != "DisplayNotes" && e.name != "DisplayCmtEdits") {
          e.checked = false;
        }
      }
      else if (e.type == "select-multiple") {
        e.selectedIndex = -1;
      }
      else if (e.type == "select-one") {
        e.selectedIndex = 0;
      }
    }
  }
}


function clickAndDisable()
{

  let self = this
  if (this.clicked) return false;

  this.clicked = true;
  setTimeout(function() {self.clicked = false;}, 10000);

  return true;
}
