var myForm;

  function validate_too() 
  {
    var reqlbls = new Array("start","stop","probability","trigger");
    var ii;
    var str=""
    var retval = true;

    clear_errors();

    for (ii=0;ii<(reqlbls.length);ii++) {
      val = myForm.getItemValue(reqlbls[ii]);
      if (val==null || val.toString().trim() == "") {
        arr = myForm.getItemLabel(reqlbls[ii]).split("<br");
        str += "Missing required field: "  + arr[0] + "<br>";
        set_errlbl(true,myForm,reqlbls[ii]);
      }
    }
    str += validate_yn(myForm.getItemValue("too_alt"),myForm,["too_alt_lbl","alt_group_name","alt_requested_count"]);
  

    if (str.indexOf("robability") < 0 && !validate_range(myForm,"probability")) {
      str += "Invalid range for Probability.  " + get_range("probability") + "<br>";
    }
    var start = parseFloat(myForm.getItemValue("start"));
    var stop = parseFloat(myForm.getItemValue("stop"));
    if (!isNaN(start) && !isNaN(stop)) {
       if (stop < start) {
        str += myForm.getItemLabel('startl') + " Stop value must be less than Start";
        set_errlbl(true,myForm,"stop");
      }
    }

    if (str.length > 1) {
      set_status("<span class='errlbl'>" + SAVE_FAILED +  str + "</span>");
      retval=false;
    }
    return retval;
  }

  function clear_errors()
  {
    var lbls = new Array("start","stop","probability","trigger","too_alt_lbl","alt_group_name","alt_requested_count");

    //reset error labels
    for (var ii=0;ii<lbls.length;ii++) {
      set_errlbl(false,myForm,lbls[ii]);
    }
    clear_status();
  }


  function ValidTOOStart(data) {
    var retval = validate_range(myForm,"start");
    if (retval) {
      var retval2 = validate_range(myForm,"stop");
      if (retval && retval2) {
        var x = parseFloat(data);
        var y = parseFloat(myForm.getItemValue("stop"));
        if (x > y) {
          retval=false;
          myForm.setUserData("start","tooltip","Response Window Start must be less than the stop window.");
          set_errlbl(true,myForm,"start");
        }
      }
    }
    return retval;
  }
  function ValidTOOStop(data) {
    var retval = validate_range(myForm,"stop");
    if (retval) {
      var retval2 = validate_range(myForm,"start");
      if (retval && retval2) {
        var x = parseFloat(data);
        var y = parseFloat(myForm.getItemValue("start"));
        if (y > x) {
          retval=false;
          myForm.setUserData("stop","tooltip","Response Window Stop must be greater than the start window.");
          set_errlbl(true,myForm,"stop");
        }
      }
    }
    return retval;
  }

  function ValidTOOProb(data) {
    return validate_range(myForm,"probability");
  }

  function reset_fields() {
    clear_errors();
    loadPage();
    setChanges(0);
  } 


  function handle_change(id,val,state,test) {
    if (!test || test.indexOf(DONOTCLEARSTATUS) < 0)
      clear_success_status();

        if (id == "start") {
          response_type();
        }
        else if (id == "too_alt") {
          if (val == "N") {
            var chkarr= ["alt_group_name","alt_requested_count"];
            if ( check_fields(myForm,chkarr) == 0)
              myForm.hideItem("altgrp");
          } else {
            myForm.showItem("altgrp");
          }
        }
        if (id == "alt_group_name") {
           if (val.indexOf("_____") > 0) {
             var varr = val.split("_____");
             var cnt=varr[1];
             myForm.setItemValue("alt_requested_count",cnt);

           }
        }

        setChanges(1);
  }

  function save_changes() {
    if (validate_too()) {
      myForm.setItemValue("operation","SAVE");
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
  function response_type() 
      {
        var ival = myForm.getItemValue("start");
        var rstr="";
        var rtype="";
        var msg="";
        var isDDT = myForm.getItemValue("isDDT");
        if (!isNaN(parseFloat(ival))) {
          if (ival < 5) {
             rstr="<span class='errmsg'>Very Fast</span>";
             rtype="0-5";
          } else if (ival < 20) {
             rstr="<span class='errmsg'>Fast</span>";
             rtype="5-20";
          } else if (ival < 40) {
             rstr="<span class='errmsg'>Medium</span>";
             rtype="20-40";
          } else  {
             rstr="Slow";
             rtype=">=40";
          }
          myForm.setItemValue("too_type",rtype);
          if (isDDT == "false") {
             msg = "Response Category: " + rstr ;
             myForm.setItemLabel("response_type",msg);
          }
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
    var validTOO = myForm.getItemValue("validTOO")
    var isEdit = myForm.getItemValue("isEdit");
    if (validTOO != "true") isEdit="false";

    var isDDT = myForm.getItemValue("isDDT");
    if (isDDT == "true") {
      myForm.setItemValue("probability",1);
      myForm.hideItem("probblk");
      myForm.hideItem("altfs");
      myForm.hideItem("resubfs");
    }
    if (isEdit == "true") {
      handle_change("start",myForm.getItemValue("start"),null,DONOTCLEARSTATUS) ;
    }
    var val = myForm.getItemValue("alt_group_name");
    var co = myForm.getCombo("alt_group_name");
    if (val.length > 1)
      co.setComboText(val);

    handle_change("too_alt",myForm.getItemValue("too_alt"),null,DONOTCLEARSTATUS) ;
    set_editButtons(myForm,isEdit);

    cps_style_load();

    setChanges(0);
  } 

  function loadPage() {
    var url ="/cps-app/cps_loadtgt?page=TOO&pid=" + pid + "&tid=" +tid;
    myForm.load(url,postLoad);
    setChanges(0);
  }

  function doOnLoad() {
         var formData=[
	   {type:"settings", position:"label-left", },
	   {type:"input",hidden:true,name:"operation",value:""},
           {type:"input",hidden:true,name:"isEdit",value:""},
           {type:"input",hidden:true,name:"pid",value:""},
           {type:"input",hidden:true,name:"tid",value:""},
           {type:"input",hidden:true,name:"altid",value:""},
           {type:"input",hidden:true,name:"page",value:"TOO"},
           {type:"input",hidden:true,name:"validTOO",value:""},
           {type:"input",hidden:true,name:"tgtnbr",value:""},
           {type:"input",hidden:true,name:"propno",value:""},
           {type:"input",hidden:true,name:"emsg",value:""},
           {type:"input",hidden:true,name:"too_type",value:""},
           {type:"input",hidden:true,name:"isDDT",value:""},

               {type:"fieldset",name:"nameobs",label:"Trigger Target ",list:[
               {type:"block",name:"resubfs",list:[
               {type:"label", name: "too_resub_lbl",label:"<a href='javascript:openHelpWindow(\"#TOOCancel\")'>If this TOO is similar to a proposal approved in the previous Cycle, should this TOO be canceled if the previous Cycle TOO is triggered?</a>",labelWidth:500,position:"label-top"},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"too_cancel",value:"Y",label:"Yes", position:"label-right",checked:false},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"too_cancel",value:"N",label:"No",position:"label-right",checked:true}
               ]},
               {type:"block",list:[
               {type:"label", name: "startl",label:"<a class='reqLbl' href='javascript:openHelpWindow(\"#TOOStart\")'>Exact CXC Response Window:</a> "},
               {type:"newcolumn",offset:5},
               {type:"input", name: "start",position:"label-right",label:"<a href='javascript:openHelpWindow(\"#TOOStart\")'>Start</a>",validate:"ValidTOOStart",inputWidth:50 },
               {type:"newcolumn",offset:20},
               {type:"input", name: "stop",position:"label-right",label:"<a href='javascript:openHelpWindow(\"#TOOStop\")'>Stop</a>" ,validate:"ValidTOOStop",inputWidth:50},
               {type:"newcolumn",offset:10},
               {type:"label", name: "too_stop_lbl",label:"(days)."},
                 {type:"newcolumn",offset:40},
                 {type:"label", name: "response_type",label:""},
               ]},
               {type:"block", name: "probblk", list:[
                 {type:"input", name: "probability",className:"reqLbl",label:"<a href='javascript:openHelpWindow(\"#TOOProb\")'>Probability of TOO occuring this cycle:</a>" ,inputWidth:50,validate:"ValidTOOProb"},
               ]},
               {type:"block",list:[
               {type:"input", name: "trigger",className:"reqLbl",label:"<a href='javascript:openHelpWindow(\"#TOOTrigger\")'>TOO trigger criteria:</a><br> <span class='fldhelp'> (400 characters)</span>" ,maxLength:400,rows:4, inputWidth:550,labelWidth:175},
               {type:"input", name: "remarks",label:"<a href='javascript:openHelpWindow(\"#TOOFollowupInstructions\")'>TOO Followup Instructions:</a><br><span class='fldhelp'> (1500 characters)</span> ",rows:4 ,maxLength:1500, inputWidth:550,labelWidth:175}
               ]}
               ]},
               {type:"fieldset",name:"altfs",label:"Alternate Target Group",list:[
               {type:"block",list:[
               {type:"label", name: "too_alt_lbl",label:"<a href='javascript:openHelpWindow(\"#AlternateGroup\")'>Is this part of an Alternate target group?</a>",position:"label-top" },
               {type:"newcolumn",offset:20},
               {type:"radio",name:"too_alt",value:"Y",label:"Yes",position:"label-right", checked:false},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"too_alt",value:"N",label:"No",position:"label-right",checked:true}
               ]},
               {type:"block",name:"altgrp",list:[
               {type:"label", name: "altinstr",label:"If you are specifying alternate targets in this proposal for a group of TOOs, please identify a group name and then specify the number of targets you are requesting for this group. For example, if you want to observe 1 of 3 TOOs in this proposal, then the 3 TOO's should all specify the same group name (i.e.: alt1) and the number of targets requested would be '1'. The alternate targets can then be added using the Add Target button. You must specify the identical group name for all the alternate targets."},
               {type:"combo", name: "alt_group_name",label:"<a href='javascript:openHelpWindow(\"#AlternateGroup\")'>Alternate target group name:</a>",maxLength:30,inputWidth:300 },
               {type:"input", name: "alt_requested_count",label:"<a href='javascript:openHelpWindow(\"#AlternateNumber\")'>Number of targets requested for this alternate group:</a>",inputWidth:50}
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
         myForm.hideItem("altgrp");
         myForm.enableLiveValidation(true);
         buildTooltips(myForm);

         var alts= myForm.getCombo("alt_group_name");
         alts.enableAutocomplete();
         alts.allowFreeText(true);
         var alturl ="/cps-app/cps_props?operation=ALTOPTIONS&pid=" + pid;
         alts.load(alturl).then(function() {
           loadPage();
         });

         myForm.attachEvent("onValidateError",handle_error);
         myForm.attachEvent("onAfterValidate", after_validate);
         myForm.attachEvent("onChange",handle_change);
         myForm.attachEvent("onButtonClick",function(id){
            clear_status();
            if (id=="save") save_changes();
            else if (id=="discard") reset_fields();
         });

      }   
