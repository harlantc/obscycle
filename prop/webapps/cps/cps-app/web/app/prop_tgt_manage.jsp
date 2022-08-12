<%@ page session="true" import="java.util.*" %>
<% response.setHeader("Cache-Control","no-cache,no-store"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", -1); 
%>
<%


String pid = (String)request.getParameter("id");
if (pid == null) pid = "unknown";
String emsg = (String)session.getAttribute("tgtmsg");
if (emsg == null) emsg = "";
if (emsg == "ok") emsg = "";
emsg = emsg.replaceAll("\\n","<br>");
session.removeAttribute("tgtmsg");

Integer clonetgt =  new Integer(0);
String cloneStr = (String)request.getParameter("clonetgt");
if (cloneStr != null && !cloneStr.equals("")) {
  try {
     clonetgt = new Integer(cloneStr);
  } catch (Exception e) {
     clonetgt=0;
  }
}
%>

<%@ include file="cps_hdr.html" %>
</head>
<body onload="showTimeoutWarn(false);doOnLoad();">

   <script type="text/javascript">
      var myForm,mygrid;
      var sortCol=0;

  
      function ValidTotalTgtCnt(data) {
        var retval=false;
        retval= validate_range(myForm,"tgtcnt");
        if (retval) {
          // now validate with number already exists
          var totalNum = mygrid.getRowsNum() + parseInt(data);
          //alert("Total = " + totalNum);
          if (totalNum > 999) {
            set_errlbl(true,myForm,"tgtcnt");
            var msg= "Total number of targets may not exceed 999. This includes existing targets.";
            set_errstatus(msg);
            return (false);
          }
        } else {
            set_errlbl(true,myForm,"tgtcnt");
            var msg= get_range("tgtcnt") ;
            set_errstatus(msg);

        }  
        return retval;
      }

      function ValidTgtCnt(data) {
        var retval=false;
        retval= validate_range(myForm,"tgtcnt");
        return retval;
      }
      
      function add_targets() {
         myForm.showItem("addtargetfs");
         var obj = myForm.getInput("tgtcnt");
         obj.scrollIntoView();
      }
      function alertResult()
      {
      }
      function add_entry() {
         if (myForm.getItemValue("tgtadd_type") == "UPLOAD") {
           var ufile = myForm.getItemValue("upload_tgt");
           if (ufile == null || ufile.length < 1) {
             set_status("<span class='errmsg'>No targets added. Please specify file for upload.</span>");
             return;
           }
           else {
             myForm.setItemValue("operation","UPLOAD" );
             var uval = myForm.getItemValue("uval");
             if (uval == 1)  {
               myForm.disableItem("save");
               document.getElementById("tgtManageForm").submit();
             } else  {
               set_status("<span class='errmsg'>No targets added. Unable to access file for upload.");
               return;
             }
           }
         }
         else {
           myForm.setItemValue("operation","CREATE");
           var ctarget= myForm.getItemValue("clonetgt");
           var tcnt = parseInt(myForm.getItemValue("tgtcnt"));
           var ext="";
           var msg="";
           if (!ValidTotalTgtCnt(tcnt)) {
             return false;
           }
           else {
             msg = "Are you sure you want to ";
             if (tcnt > 1) ext="s";
             if (ctarget == "0") {
               msg += "create " + tcnt + " blank target" + ext + "?";
             } else {
               var t = myForm.getSelect("clonetgt");
               msg += "clone  '" + t.options[t.selectedIndex].text + "' \nand create " + tcnt + " new target" + ext + "?";
             }
 
             doConfirm(msg,confirmResult);
           }
         }
      }

      function scrollToBottom() {
        var mmsg="<%=emsg%>";
        if (mmsg.indexOf("ail") > 0) {
          var obj = myForm.getContainer("status");
          obj.scrollIntoView();
        }
      }
      function confirmResult(result) {
        if (result) {
          save_changes();
        }
      }

      function upload_fail(realName)
      {
         doAlert(("Upload failed for " + realName),alertReturn);
      }

      function after_sort(col,type,direction)
      {
        sortCol = col;
        //mygrid.setSortImgState(true,col,direction);
      }

      function handle_change(id)
      {
        //set_status("");
        var rval = myForm.getItemValue(id);
        if (id == "clonetgt") {
          if (rval == "0") {
            myForm.hideItem("toodetails");
          }
          else {
            if (myForm.getItemValue("isTOO") == "true") 
              myForm.showItem("toodetails");
           else 
              myForm.hideItem("toodetails");
          }
        }
        else if (id == "tgtadd_type") {
           if (rval == "Specify") {
             myForm.showItem("tgtcnt");
             myForm.hideItem("tgtfile");
             myForm.setRequired("tgtcnt",true);
           } else {
             myForm.hideItem("tgtcnt");
             myForm.showItem("tgtfile");
             myForm.setRequired("tgtcnt",false);
             var obj = myForm.getContainer("status");
             obj.scrollIntoView();
           }
        }
        else if (id == "upload_tgt") {

          var elem = myForm.getInput(id);
          myForm.setItemValue("uval","1");
          var file = elem.files[0];
          //alert("size " + file.size);
          if (file.size > 10000) {
            myForm.setItemValue("uval","0");
            msg = "File <b>" + file.name + "</b> exceeds maximum size of 10KB allowed for upload of target information.";
            set_status_err(msg);
            elem.value="";
          }
          else if (window.FileReader) {
            var reader = new FileReader();
            reader.onerror = function(e){
                myForm.setItemValue("uval","0");
                set_status_err("Error occurred reading file " + file.name);
            }
            reader.onabort = function(e){
              myForm.setItemValue("uval","0");
              set_status_err("Error occurred reading file " + file.name);
            }
            reader.onprogress = function(e){
               myForm.setItemValue("uval","1");
            };
            reader.onloadend = function(e){
               myForm.setItemValue("uval","1");
            };
            reader.readAsText(file); 
          }
        }
      }
      function no_drag() {
         return false;
      }
      function can_drag() {
        if (sortCol==0)  {
          return true;
        }
        else {
          set_status("<span class='errmsg'>Targets must be sorted by ascending number(#) before re-ordering</span>");
          return false;
        }
      }
  
      function can_renumber(sid,tid) {
         for (var ii=0;ii<mygrid.getRowsNum();ii++) {
           var targno=mygrid.cellByIndex(ii,0).getValue();
           if (targno != (ii+1)) {
             doAlert("Targets must be sorted by ascending number(#) before re-ordering",alertReturn);
             return false;
           }
         }
         return true;
      }

      function renumber_entry(sid,tid) {
         var row;
         var str = '';
         str += sid + "=" + mygrid.cellById(tid,0).getValue();
         //alert("Renumber " + str);
         myForm.setItemValue("operation","RENUMBER");
         myForm.setItemValue("gridvalues",str);
         save_changes();
      }
  
      function delete_entry() {
         myForm.setItemValue("upload_tgt","");
         var rowId=mygrid.getSelectedRowId();
         if (rowId == null) {
           doAlert("No target is selected",alertReturn);
           return;
         }
         if (mygrid.getRowsNum() < 2) {
           doAlert("At least 1 target must exist for this proposal. Target is not deleted.",alertReturn);
           return;
         }
         var targno=mygrid.cellById(rowId,0).getValue();
         var tname=mygrid.cellById(rowId,1).getValue();
         var msg = "Are you sure you want to delete target " + targno + " " + tname + "?";
         doConfirm(msg,confirmDelete);
      }

      function save_changes() {
        myForm.disableItem("addbutton");
        myForm.disableItem("createbutton");
        myForm.disableItem("clonebutton");
        myForm.disableItem("deletebutton");
        myForm.disableItem("modifybutton");
        mygrid.detachEvent("onBeforeDrag");
        mygrid.attachEvent("onBeforeDrag",no_drag);
      
        cps_style_load();
        document.getElementById("tgtManageForm").submit();
        
      }
  
      function confirmDelete(result) {
        if (result) {
          var rowId=mygrid.getSelectedRowId();
          myForm.setItemValue("operation","DELETE");
          myForm.setItemValue("gridvalues",rowId)
          save_changes();
        }
      }

      function cancel_add() {
        myForm.hideItem("addtargetfs");
        var ele = document.getElementById("errmsg"); 
        if (ele != null) {
          ele.innerHTML="";
        }
      }
      function clone_selected()
      {
         var rowId = mygrid.getSelectedRowId();
         myForm.showItem("addtargetfs");
         myForm.setItemValue("clonetgt",rowId);
         set_status("");
         handle_change("clonetgt");
      }

      function tgt_selected(rowId,celInd) {
        var rowId = mygrid.getSelectedRowId();
        var propno=myForm.getItemValue("propno");
        var url="pid=" + <%=pid%> + "&tid="+ rowId + "&propno=" + propno ;
        var changes=getCookie("unsavedChanges");
        if (changes!=null && changes>0) {
          dhtmlx.confirm({ type:"confirm",
            text: UNSAVED_CHANGES,
            ok:"Continue without saving",
            width:"auto",
            cancel:"Return to the form",
            callback: function(result){
               if (result)  {
                self.location.replace("/cps-app/app/prop_tgt_edit.jsp?" + url);
                //parent.mainTabbar.clearSelection();
                return true;
               } else{
               }
            }});
          return false;
        } else {
          self.location.replace("/cps-app/app/prop_tgt_edit.jsp?" + url);
          //parent.mainTabbar.clearSelection();
          return true;
        }
      }

      function row_selected(rowId,celInd) {
        clear_status();
        myForm.setItemValue("clonetgt","0");
        myForm.setItemValue("operation","NO OP");
        var str = myForm.getItemValue("isEdit");
        //alert("row_selected: " + myForm.getItemValue("isEdit"));
        if (str == "true") {
          myForm.enableItem("addbutton");
          myForm.enableItem("clonebutton");
          if (mygrid.getRowsNum() > 1 )
            myForm.enableItem("deletebutton");
          else
            myForm.disableItem("deletebutton");
        } else {
          myForm.disableItem("addbutton");
          myForm.disableItem("deletebutton");
          myForm.disableItem("clonebutton");
        } 
        // this is modify or view
        myForm.enableItem("modifybutton");
        return true;
      }

      function gridPostLoad() {
        //mygrid.loadSizeFromCookie("tgtmgr");
        //mygrid.setSizes();
        var obj=document.getElementById("tgtinstr");
        if (obj != null) {
          if (mygrid.getRowsNum() <2 && 
              mygrid.cellByIndex(0,1).getValue().length < 2 ) {
            obj.innerHTML="We start all observing proposals with one default target. To edit, select a row in the table and click <b>Modify Selected</b>. To add additional targets, click on the <b> Add Target(s)</b> button. These additional targets will start with the same default values unless overwritten by selecting an existing target to clone or using the file upload option.  They can also be modified later as needed.";
          }  else {
            obj.innerHTML="To edit a target, select its row in the table and click on <b>Modify Selected</b>. To Re-prioritize  targets, drag-n-drop the target rows within the table.";
          }
        }
        sortCol=0;
        var opts = myForm.getOptions("clonetgt");
        opts.add(new Option("No","0"));
        myForm.setItemValue("gridcnt",mygrid.getRowsNum());
        for (var ii=0;ii<mygrid.getRowsNum();ii++) {
          var targid = mygrid.getRowId(ii);
          var txt=mygrid.cellByIndex(ii, 0).getValue() + " " + mygrid.cellByIndex(ii, 1).getValue();
          opts.add(new Option(txt,targid));
        }   
        var mmsg="<%=emsg%>";
        if (mmsg.indexOf("Upload failed") >= 0) {
          myForm.setItemValue("clonetgt","<%=clonetgt%>");
          myForm.setItemValue("tgtadd_type","UPLOAD");
          handle_change("tgtadd_type");
          myForm.showItem("addtargetfs");
        }

        mygrid.setSortImgState(true,0,"asc");
        buildTargetOptions(mygrid);
        cps_style_load();
        scrollToBottom();
      }

      function postLoad() {
        var mmsg="<%=emsg%>";
        var emsg= myForm.getItemValue("emsg");

        //alert("mmsg=" + mmsg);
        var propno=myForm.getItemValue("propno");
        if (propno.indexOf("Invalid") == 0) {
          doAlertLogoff(myForm.getItemValue("emsg"));
          return;
        }
        if (propno.indexOf("Error") == 0) {
          doAlert(myForm.getItemValue("emsg"),badalertReturn);
          return;
        }

        var obj1=document.getElementById("tgtlbl");
        obj1.innerHTML=propno + ": <a href='javascript:openHelpWindow(\"#Targets\")'>Targets</a>  " + myForm.getItemValue("proposal_title") ;
        set_status(mmsg);

        myForm.setItemLabel("tgtsumm",propno );
        var str = myForm.getItemValue("isEdit");
        var instr=document.getElementById("tgtinstr");
        if (str != "true") {
          myForm.disableItem("addbutton");
          myForm.disableItem("deletebutton");
          myForm.setItemLabel("modifybutton","View");
          myForm.disableItem("modifybutton");
          myForm.disableItem("clonebutton");
          myForm.hideItem("addtargetfs");
          mygrid.enableDragAndDrop(false);
        } else {
          myForm.enableItem("addbutton");
          if (mmsg.indexOf("No targets") >= 0) {
            myForm.showItem("addtargetfs");
          }
          instr.style.display="block";
        }
        str = "pid=" + "<%=pid%>" ;
        mygrid.clearAll();
        mygrid.load("/cps-app/cps_tgts?operation=LOAD&" + str,gridPostLoad,"json");
        setChanges(0);
      } 


      function resizeLayout(){
        //alert('resize');
        mygrid.setSizes();
      }

      function loadPage() {
         var str = "pid=" + <%=pid%> ;
         myForm.load("/cps-app/cps_tgts?operation=PAGELOAD&" + str,postLoad);
      }

      function doOnLoad() {
         var formData=[
           {type:"input",hidden:true,name:"operation",value:""},
           {type:"input",hidden:true,name:"page",value:"TGTMANAGE"},
           {type:"input",hidden:true,name:"pid",value:"<%=pid%>"},
           {type:"input",hidden:true,name:"propno",value:""},
           {type:"input",hidden:true,name:"proposal_title",value:""},
           {type:"input",hidden:true,name:"isEdit",value:"false"},
           {type:"input",hidden:true,name:"isTOO",value:""},
           {type:"input",hidden:true,name:"gridvalues",value:""},
           {type:"input",hidden:true,name:"emsg",value:""},
           {type:"input",hidden:true,name:"uval",value:"0"},
           {type:"input",hidden:true,name:"gridcnt",value:"0"},
           {type:"input",hidden:true,name:"aval",value:"0"},
           {type:"block",offsetTop:20,list:[
             {type:"button",name:"addbutton",value:"Add Target(s)"},
             {type:"newcolumn",offset:30},
             {type:"button",name:"clonebutton",value:"Clone Selected"},
             {type:"newcolumn",offset:30},
             {type:"button",name:"modifybutton",value:"Modify Selected"},
             {type:"newcolumn",offset:30},
             {type:"button",name:"deletebutton",value:"Delete Selected"},
           ]},
           {type:"block",offsetTop:20,list:[
            {type:"fieldset",id:"addtargetfs", name:"addtargetfs",label:"Add Target",width:"95%",list:[
            {type:"fieldset",name:"clonefs",label:"Clone Target",width:"95%",list:[
               {type:"block",name:"cloneblk",list:[
                   {type:"label",label:"If you want empty target forms, leave this as 'No'.  If you want the new targets to use the same parameters as one you've already created, indicate which target to clone. This feature may be used whether you are adding a specified number of targets or uploading a file."},
                   {type:"select", name: "clonetgt",label:"<a href='javascript:openHelpWindow(\"#CloneTarget\")'>Clone Target:</a>"}
                 ]},
                 {type:"block",name:"toodetails",list:[
                   {type:"label", name: "clonetoolbl",label:"<a href='javascript:openHelpWindow(\"#CloneTOODetails\")'>Clone TOO Details?</a>" },
                   {type:"newcolumn",offset:20},
                   {type:"radio",name:"clonetoo",value:"Y",label:"Yes:",position:"label-right",checked:true},
                   {type:"newcolumn",offset:20},
                   {type:"radio",name:"clonetoo",value:"N",label:"No",position:"label-right"}
                 ]},
               ]},
              {type:"fieldset",name:"tgtcntfs",label:"Add Targets",width:"95%",list:[

                {type:"block",list:[
                  {type:"container",name:"tgtaddlbl",label:"Add Targets by: "},
                  {type:"newcolumn",offset:10},
                  {type:"radio",name:"tgtadd_type",value:"Specify",label:"<a href='javascript:openHelpWindow(\"#AddTarget\")'>Specify Number of targets to Add</a>",checked:true,position:"label-right"},
                  {type:"newcolumn",offset:10},
                  {type:"radio",name:"tgtadd_type",value:"UPLOAD",label:"<a href='javascript:openHelpWindow(\"#UploadTarget\")'>Upload File</a>",checked:false,position:"label-right" },
                ]},
                
                {type:"block",list:[
                  {type:"newcolumn",offset:10},
                  {type:"input",label:"<a href='javascript:openHelpWindow(\"#NumberTgts\")'>Number of Targets:</a>",name:"tgtcnt",value:"1",validate:"ValidTgtCnt",width:40},
                ]},
                {type:"block",list:[
                  {type:"newcolumn",offset:10},
                  {type:"block",name:"tgtfile",list:[
                    {type:"file", id:"upload_tgt", name:"upload_tgt", label:"", position:"label-left"},
                    {type:"newcolumn"},
                    {type:"label", name:"upload_lbl", label:"Format: Target Name,RA,Dec,Exposure Time(ksec),Count Rate<br>Your upload must include comma-separated-values for each additional target. <br>All other values will be copied from the cloned target.  <br>All 5 fields must be specified for each target being added.", position:"label-left"},
                  ]},
                ]},
              ]},
               {type:"block",list:[
                  {type:"button",name:"createbutton",value:"Create Target(s)"},
                  {type:"newcolumn",offset:30},
                  {type:"button",name:"cancelbutton",value:"Cancel"},
               ]}
            ]}
          ]},
          {type:"container",id:"status",name:"status",className:"statusContainer"},
         ];

         myForm=new dhtmlXForm("formB_container",formData);
         //cps_style_init();

         myForm.setSkin("dhx_skyblue");
         myForm.enableLiveValidation(true);
         myForm.hideItem("addtargetfs");
     
         myForm.hideItem("toodetails");
         myForm.hideItem("tgtfile");
         myForm.disableItem("addbutton");
         myForm.disableItem("deletebutton");
         myForm.disableItem("modifybutton");
         myForm.disableItem("clonebutton");
         myForm.attachEvent("onUploadFail",upload_fail);
         myForm.attachEvent("onChange",function(id) {
                clear_status();
		handle_change(id);
          });
         myForm.attachEvent("onButtonClick",function(id){
            clear_status();
            if ( id=="addbutton")  add_targets();
            else if ( id=="modifybutton") tgt_selected();
            else if ( id=="clonebutton") clone_selected();
            else if ( id=="deletebutton") delete_entry();
            else if (id=="createbutton") add_entry();
            else if (id=="cancelbutton") cancel_add();
         });
         buildTooltips(myForm);

         mygrid=new dhtmlXGridObject('grid');
         //myForm.bind(mygrid);  this seems to lose form data after row_selected
         mygrid.setHeader("#,Target&nbsp;Name,RA,Dec,Detector,Grating,ObservingTime,CountRate");
         //mygrid.setInitWidths("70,150,100,100,85,85,95,120");
         mygrid.setInitWidthsP("5,22,15,15,10,10,13,*");
         mygrid.setColumnMinWidth("20,20,20,20,20,20,20,20");
         mygrid.setColAlign("center,left,left,left,center,center,center,center");
         mygrid.setSkin("dhx_skyblue");
         mygrid.setColTypes("ed,ro,ro,ro,ro,ro,ro,ro");
         mygrid.setColSorting("int,custom_str_sort,custom_str_sort,custom_str_sort,custom_str_sort,custom_str_sort,int,int");
         mygrid.setImagePath(CSS_IMG_PATH);
         mygrid.init();
         mygrid.enableAutoHeight(true,300);
         //mygrid.enableAutoSizeSaving("tgtmgr");
         //mygrid.enableAutoWidth(true);
         mygrid.enableRowsHover(true,"gridhover");
         mygrid.attachEvent("onRowSelect",row_selected);
         mygrid.attachEvent("onRowDblClicked",tgt_selected);
         mygrid.attachEvent("onBeforeDrag",can_drag);
         mygrid.attachEvent("onDrag",can_renumber);
         mygrid.attachEvent("onDrop",renumber_entry);
         mygrid.attachEvent("onAfterSorting",after_sort);
         mygrid.enableDragAndDrop(true);

          //window.addEventListener("resize",resizeLayout, false);

         loadPage();
      }   
   </script>
  <div id="msgbody" class="msgbody">
    <img src="/cps-app/imgs/ajax-loader.gif" alt="Loading...">
  </div>
  <div id="pagebody" class="pagebody">
 
   <form id="tgtManageForm"  enctype="multipart/form-data" method="POST" action="/cps-app/tgt_mgr">
<br>
   <div class="instrLbl"><div id="tgtinstr" class="instrTxt" style="display:none;margin-left:20px;margin-bottom:10px;width:95%;"></div></div>
   <div id='tgtlbl' class="fslbl"  style="padding-top:5px;margin-left:20px"></div>
   <div id='grid' style='margin-left:20;width:95%'></div>
   <div id="formB_container" style="width:95%" ></div>


   </form>
  </div>
</body>
</html>
