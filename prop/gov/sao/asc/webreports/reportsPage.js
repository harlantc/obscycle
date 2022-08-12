
<script language="JavaScript" type="text/javascript"> 
<!-- hide from older browsers -- 

$(document).ready(function(){

//on load set the default or history size
var track_pcmt='cmtEdit1';
initSize(track_pcmt);
var track_pnote='pNote';
initSize(track_pnote);
var track_ta1='comments1'
initSize2(track_ta1);
var track_ta2='specificRecs'
initSize2(track_ta2);
var track_ta3='whyGradeNotHigher'
initSize2(track_ta3);


function initSize(track_ele){
   try {
     var ta_size=localStorage.getItem(track_ele);
     //default size
     if(ta_size==null) ta_size={width:'98%', height:'80px'};
     else ta_size=JSON.parse(ta_size);
     $('#'+track_ele).css(ta_size);
   } catch(e) {
   }
}
function initSize2(track_ele){
   try {
     var ta_size=localStorage.getItem(track_ele);
     //default size
     if(ta_size==null) ta_size={width:'98%', height:'300px'};
     else ta_size=JSON.parse(ta_size);
     $('#'+track_ele).css(ta_size);
   } catch(e) {
   }

}

//keep the latest in the local storage
$("textarea.textpcmt").resizable({
    resize: function() {
      try {
       var sizeHistory=JSON.stringify({width:this.style.width,height:this.style.height});
       localStorage.setItem(track_pcmt,sizeHistory);
      } catch(e) {
      }
    }
});

$("textarea.textnotes").resizable({
    resize: function() {
      try {
       var sizeHistory=JSON.stringify({width:this.style.width,height:this.style.height});
       localStorage.setItem(track_pnote,sizeHistory);
      } catch(e) {
      }
    }
});

$("textarea.txtcmt").resizable({
    resize: function() {
     try {
       var sizeHistory=JSON.stringify({width:this.style.width,height:this.style.height});
       localStorage.setItem(track_ta1,sizeHistory);
     } catch(e) {
     }
    }
});
$("textarea.txtcmt1").resizable({
    resize: function() {
     try {
       var sizeHistory=JSON.stringify({width:this.style.width,height:this.style.height});
       localStorage.setItem(track_ta2,sizeHistory);
     } catch(e) {
     }
    }
});
$("textarea.txtcmt2").resizable({
    resize: function() {
     try {
      var sizeHistory=JSON.stringify({width:this.style.width,height:this.style.height});
      localStorage.setItem(track_ta3,sizeHistory);
     } catch(e) {
     }
    }
});
});

function validateReport() { 
  var emptyFields = "Missing required fields: ";
  var submitRequest = true; 
  var effort = document.theform.effort.value;
  var rptType = document.theform.reportType.value;
  var status = document.theform.reportStatus.value;
 
  var statusMenu = document.theform.reportStatusMenu;
  if (statusMenu != null ) {
    status = document.theform.reportStatusMenu.options[document.theform.reportStatusMenu.selectedIndex].value;
    document.theform.reportStatus.value = status;
  }
     
  // no longer need to validate for save so just return true 
  if(status == "<%=ReportsConstants.SAVE%>" ) {
     return true;
  }
  if(effort == "<%=ReportsConstants.NONE %>") {
    emptyFields += "\n Degree of effort \n"; 	
    submitRequest = false;
  }

  if (submitRequest) { 
    var x;
     
    x= confirm("Are you sure you're report is complete and you have finished editing the report?\n");
    return x;
  } else { 
    emptyFields += "\n\nData was NOT SAVED.\n";
    alert(emptyFields); 
    return false; 
  } 
}

// end hiding --> 
</script> 


