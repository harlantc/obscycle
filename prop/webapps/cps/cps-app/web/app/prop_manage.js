      var myForm,mygrid,formData;
      var lblW=175;
      var permCol=1,piidCol=0;
      var ownCol=2,pnoCol=3,statCol=5,typeCol=4,submitCol=6,subCol=8,titCol=9;
      var canmodColor="#f0f0c6";  // yellow 
      //var canmodColor="#dae7f6"; // blue/gray
      var nomodColor="#d2d9e4";
      var modColor="#ffffff";
      var myTree;
      var FILTERBY="Filter";


      function clear_fields() {
         myForm.clear();
         //set_status("cleared");
      }

      function disable_buttons() {
            myForm.disableItem("clonebutton");
            myForm.disableItem("modbutton");
            myForm.disableItem("pdfbutton");
            myForm.disableItem("pdfanonbutton");
            myForm.disableItem("withdrawbutton");
            myForm.disableItem("transferbutton");
      }
      function withdraw_entry() {
         var doit=false;
         var rowId=mygrid.getSelectedRowId();
         var pid=rowId;
         var pno=mygrid.cellById(rowId,pnoCol).getValue();
         myForm.setItemValue("propno",pno);
         var pstat=mygrid.cellById(rowId,statCol).getValue();
         var msg;
         if (pstat == getProposalStatus("WITHDRAWN")) {
           pstat ="INCOMPLETE";
           msg = "Are you sure you want to activate this proposal " + pno + "?";
         } else {
           msg = "Are you sure you want to WITHDRAW proposal " + pno + "?";
           pstat ="WITHDRAWN";
         }
         myForm.setUserData("propno","pstat",pstat);
         myForm.setUserData("propno","rowId",rowId);
         doConfirm(msg,confirmStatusUpdate);
         return;    
      }

      function confirmStatusUpdate(result) {
        if (result) {
          var pstat = myForm.getUserData("propno","pstat");
          var rowId = myForm.getUserData("propno","rowId");
          sendWithdrawRestore(pstat,rowId);
        }
        return;
      }

      function sendWithdrawRestore(pstat,rowId)
      {
          var pid=rowId;
          var pno=mygrid.cellById(rowId,pnoCol).getValue();
          var ptitle=mygrid.cellById(rowId,titCol).getValue();
          var ptype=mygrid.cellById(rowId,typeCol).getValue();
          myForm.clearValidation("subject_category");
          myForm.setRequired("subject_category",false);
          myForm.setItemValue("operation","STATUS");
          myForm.setItemValue("changestatus",pstat);
          document.body.className = "waiting";
          myForm.send("/cps-app/cps_mgr","post",function(name,retstr) {
               document.body.className = "";
               if (retstr.search(/success/g) >= 0) {
                 if (pstat == "WITHDRAWN") {
                   myForm.setItemLabel("modbutton","View Selected");
                   myForm.setItemLabel("withdrawbutton","Restore Selected");
                   remove_tabs(pid);
                 }
                 else {
                   myForm.setItemLabel("withdrawbutton","Withdraw Selected");
                   myForm.setItemLabel("modbutton","Modify Selected");
                   add_tabs(pid,pno,ptitle,ptype);
                 }
                 // reload grid for filter
                 loadPage();
                 set_status(retstr);
               }
               else {
                 set_status("<span class='errmsg'>" + SAVE_FAILED + retstr + "</span>");
               }
          });

      }

      function cancel_add() {
        mygrid.clearSelection();
        myForm.hideItem("details");
        myForm.hideItem("transfer");
        myForm.clearValidation("subject_category");
        myForm.setRequired("subject_category",false);
        disable_buttons();
      }

      function clone_entry() {
         myForm.setValidation("subject_category","NotEmpty");
         myForm.setRequired("subject_category",true);
         disable_buttons();
         myForm.hideItem("transfer");
         myForm.showItem("details");
         myForm.setItemValue("operation","");
         var rowId=mygrid.getSelectedRowId();
         var pid=rowId;
         myForm.setItemValue("cloneprop",pid);
         handle_change("cloneprop", pid,null);

         var obj = myForm.getSelect("joint_type");
         obj.scrollIntoView();
      }
      function save_owner() {
         var rowId=mygrid.getSelectedRowId();
         set_errlbl(false,myForm,"lasttransfer")
         myForm.setItemValue("operation","TRANSFER");
         myForm.setRequired("subject_category",false);
         var ele = myForm.getCombo("lasttransfer");
         var eleLbl= ele.getComboText();
         var pno=mygrid.cellById(rowId,pnoCol).getValue();
         myForm.setItemValue("propno",pno);
         var plast = myForm.getItemValue("lasttransfer");
         if (plast == "") {
            set_errlbl(true,myForm,"lasttransfer")
            var msg = myForm.getItemLabel("lasttransfer") + " is required.";
            set_status(msg);
         }
         else  {
           var confirmTxt ="Are you sure you want to transfer ownership of this proposal to " + eleLbl +"?<br>You will no longer be able to edit this proposal!";
           doConfirm(confirmTxt,confirmTransfer);
         }
      }

      function confirmTransfer(result) {
        if (result) {
          document.body.className = "waiting";
          myForm.send("/cps-app/cps_mgr","post",function(name,retstr) {
            document.body.className = "";
            if (retstr.indexOf("successful") >= 0) {
              loadPage();
              myForm.hideItem("transfer");
              set_status(retstr);
            }
            else {
              set_status("<span class='errmsg'>" + SAVE_FAILED + retstr + "</span>");
            }
          });
        } else {
          set_status("Ownership of proposal was not transferred!");
        }
      }

      function show_transfer() {
        set_errlbl(false,myForm,"lasttransfer")
        var url ="/cps-app/cps_props?operation=COIOPTIONS&cxc=1&page=PROPMANAGE";
        var picombo = myForm.getCombo("lasttransfer");
        if (picombo.getOptionsCount() < 1) {
          picombo.load(url);
        } else {
          picombo.unSelectOption();
        }

        var rowId=mygrid.getSelectedRowId();
        var pno= "Proposal: " + mygrid.cellById(rowId,pnoCol).getValue();
        var title= "Title: " + mygrid.cellById(rowId,titCol).getValue();

        myForm.setItemLabel("pnotransfer",pno);
        myForm.setItemLabel("titletransfer",title);
        myForm.hideItem("details");
        myForm.showItem("transfer");
        disable_buttons();
      }
      
      function new_entry() {
         mygrid.clearSelection();
         myForm.setItemValue("cloneprop","0");
         myForm.setItemValue("clonepiid","0");
         myForm.setItemValue("proposal_type","GO");
         myForm.setItemValue("subject_category","");
         myForm.setValidation("subject_category","NotEmpty");
         myForm.setRequired("subject_category",true);
         myForm.setItemValue("joint_type","None");
         myForm.enableItem("joint_type");
         myForm.setItemValue("prop_who","PI");

         disable_buttons();
         myForm.setItemLabel("ptitle","");
         myForm.hideItem("transfer");
         myForm.showItem("details");
         myForm.setItemValue("operation","");
         var obj = myForm.getSelect("joint_type");
         obj.scrollIntoView();
      }


      function create_entry() {
        myForm.setValidation("subject_category","NotEmpty");
        myForm.setRequired("subject_category",true);
        myForm.setItemValue("operation","CREATE");
        var type = myForm.getItemValue("proposal_type");
        var joint = myForm.getItemValue("joint_type");
        if (!isCfP && ddtRequest == "false") {
          if ( type.indexOf("CAL") <0 && type.indexOf("GTO") < 0 &&
	      joint == "None") {
            var cmsg = "The Deadline has passed. Proposal creation is not allowed!";
            set_status("<span class='errmsg'>" + cmsg + "</span>");

            return false;
          }
        }
       
        document.body.className = "waiting";
        myForm.send("/cps-app/cps_mgr","post",function(name,retstr) {
          document.body.className = "";
             if (retstr.indexOf(",") >= 0) {
               var jarr=retstr.split(",");
               if (jarr[0] > 0) {
                 //pid ,pno,ptitle,ptype,isNew
                 go_to_tab(jarr[0],jarr[1],"",jarr[2],true);
               }
             }
             else 
               set_status("<span class='errmsg'>" + SAVE_FAILED + retstr + "</span>");
        });
      }

      function modify_entry() {
         myForm.hideItem("transfer");
         myForm.hideItem("details");
         var str = myForm.getItemLabel("modbutton");
         myForm.setItemValue("operation",str);
         var rowId=mygrid.getSelectedRowId();
         var pid=rowId;
         var priv=mygrid.cellById(rowId,permCol).getValue();
         var pno=mygrid.cellById(rowId,pnoCol).getValue();
         var ptitle=mygrid.cellById(rowId,titCol).getValue();
         var ptype=mygrid.cellById(rowId,typeCol).getValue();
         var pstat=mygrid.cellById(rowId,statCol).getValue();
         myForm.setItemValue("changestatus","");
         if (pstat == getProposalStatus("PROPOSED") && str.indexOf("Modify")>=0) {
           var mstr = "Do you want to modify this previously submitted proposal and reset the status to 'In progress' ?<br>You will need to re-submit this proposal.";
           dhtmlx.confirm({
	     ok:"Modify", cancel:"View Only",
             text:mstr,
             width:"auto",
	     callback:function(result){
               if (result) { 
                 myForm.setItemValue("operation","STATUS");
                 myForm.setItemValue("changestatus","INCOMPLETE");
                 myForm.setRequired("subject_category",false);
                 document.body.className = "waiting";
                 myForm.send("/cps-app/cps_mgr","post",function(name,retstr) {
                    document.body.className = "";
                    if (retstr.indexOf("Success") < 0) {
                      set_status(retstr);
                      go_to_tab(pid,pno,ptitle,ptype,false);
                    }
                    else {
                      set_status("<span class='errmsg'>" + SAVE_FAILED + retstr + "</span>");
                    }
                 });
               } else {
                 myForm.setItemValue("operation","View");
                 go_to_tab(pid,pno,ptitle,ptype,false);
               }
             }
           });
         } else {
           go_to_tab(pid,pno,ptitle,ptype,false);
       
         }
      }

      function download_pdf(isAnon="") {
          isAnon ? myForm.disableItem("pdfanonbutton") : myForm.disableItem("pdfbutton");
          var rowId=mygrid.getSelectedRowId();
          var pid = rowId;
          var pno = mygrid.cellById(rowId,pnoCol).getValue();
          var url = "/cps-app/cps_loadprop?type=f&page=CPSPDF" + isAnon + "&pid=" + pid ;
          document.body.className = "waiting";
          var mywin = window.open(url,"cpspdf","alwaysLowered=yes,left=10,top=10,width=300,height=100,toolbar=no,menubar=no");
          // this does a tab but takes the focus until return from gen
          //var pdfwin = window.open(url,"_blank");
          window.focus();
          window.focus();
          mywin.document.title="CPS PDF";
          window.setTimeout(function() {
              document.body.className = "";
              isAnon ? myForm.enableItem("pdfanonbutton") : myForm.enableItem("pdfbutton");
          },3000);
          // popup in new window but lose it if they navigate off page 
          //window.location.href= url;
          // But this did a popup which required a firefox prevention 
          // message
          /* myForm.send(url,"get",function(name,retstr) {
            var file = new Blob([retstr], { type: 'application/pdf' });
            var fileURL = URL.createObjectURL(file);
            window.open(fileURL, "Chandra Proposal Form");
          }); */



      }

      function remove_tabs(id) {
        if (parent.mainTabbar) {
          var tb = parent.mainTabbar.getIndexById(id);
          if (tb != null) {
           parent.mainTabbar.deleteChildItems(id);
           parent.mainTabbar.deleteItem(id);
          }
        }
      }

      function add_tabs(id,pno,ptitle,ptype) {
        var tag = pno.substring(4);
        var aotag = parseInt(pno.substring(0,2) + pno.substring(4));
        if (parent.mainTabbar) {
        var tb = parent.mainTabbar.getIndexById(id);
        if (tb == null) {
          //figure out where to insert by tagnumber
          var ids =parent.mainTabbar.getAllItemsWithKids();
          var xx = new Array();
          if (ids.length>1) xx = ids.split(",");
          var treeid=0;
          
          for (var ii=0;ii< xx.length;ii++) {
            var tstr = parent.mainTabbar.getItemText(xx[ii]);
            if (tstr != null && tstr > 0)  {
              var ttag = parseInt(tstr.substring(0,2) + tstr.substring(4));
              if (aotag > 0 && aotag  > ttag ) {
                if (ii > 0) treeid = xx[ii-1];
                else treeid="m1";
                break;
              }
            }
          }
          if (treeid <= 0)  {
            parent.mainTabbar.insertNewItem("myroot",id,pno);
          } else  {
            parent.mainTabbar.insertNewNext(treeid,id,pno);
          }
          parent.mainTabbar.insertNewChild(id,id+"Proposal");
          parent.mainTabbar.setItemText(id+"Proposal","Cover Page","Specify the Proposal type, title, abstract, etc ");
          parent.mainTabbar.insertNewChild(id,id+"PI");
          parent.mainTabbar.setItemText(id+"PI","P.I.","Specify the Principal Investigator");
          parent.mainTabbar.insertNewChild(id,id+"CoI");
          parent.mainTabbar.setItemText(id+"CoI","Co-Investigators","Add, Modify, Delete Co-Investigators");
          
          if (tag > "8000") {
            parent.mainTabbar.insertNewChild(id,id+"DDT","DDT");
            parent.mainTabbar.setItemText(id+"DDT","DDT","Specify your DDT specific parameters");
          }
          else {
            if (ptype.indexOf("THE") < 0 && ptype.indexOf("CAL") < 0 ) {
              parent.mainTabbar.insertNewChild(id,id+"Joint");
              parent.mainTabbar.setItemText(id+"Joint","Joint Time Requests","Specify any Joint observatory parameters");
            }
          }

          if (ptype.indexOf("ARC")<0 && ptype.indexOf("THE")<0) {
            parent.mainTabbar.insertNewChild(id,id+"TargetManage");
            parent.mainTabbar.setItemText(id+"TargetManage","Targets","Add, Modify, Delete targets");
          }
          // submit with 'file' input
          parent.mainTabbar.insertNewChild(id,id+"Upload");
          parent.mainTabbar.setItemText(id+"Upload","Supporting Files","Science Justification, Previous Chandra Experience, Team Expertise");
          parent.mainTabbar.insertNewChild(id,id+"TargetChecks");
          parent.mainTabbar.setItemText(id+"TargetChecks","Target Checks","Review target information");
          parent.mainTabbar.insertNewChild(id,id+"Validate");
          parent.mainTabbar.setItemText(id+"Validate","Validation","Review validation errors");
          parent.mainTabbar.insertNewChild(id,id+"Submit");
          parent.mainTabbar.setItemText(id+"Submit","Submit","Submit your proposal");
        }
        if (tag > "8000") 
           parent.helpurl = HELP_DDT_URL;
        else 
           parent.helpurl = HELP_URL;

        if (ptitle != null && ptitle != "")
          parent.mainTabbar.setItemText(id,pno,decodeEntities(ptitle));
        else
          parent.mainTabbar.setItemText(id,pno,decodeEntities(ptype) + ": no title");
        }
      }


      function go_to_tab(id,pno,ptitle,ptype,isnew) {
         add_tabs(id,pno,ptitle,ptype);
         if (parent.mainTabbar)
          parent.mainTabbar.selectItem(id+"Proposal",true);
      }
  
      function mouse_over(id,ind) {
      }
      function after_sort(col,type,direction)
      {
        mygrid.setSortImgState(true,col,direction);
      }

      function row_selected(rowId,celInd) {
         clear_status();
         myForm.hideItem("transfer");
         myForm.hideItem("details");
         disable_buttons();
         var perms=mygrid.cellById(rowId, permCol).getValue();  
         var status=mygrid.cellById(rowId, statCol).getValue();  
         var pid=rowId;
         myForm.setItemValue("pid",pid);
         myForm.setItemLabel("withdrawbutton","Withdraw Selected");
         myForm.setItemLabel("modbutton","View Selected");
         myForm.enableItem("modbutton");
         myForm.enableItem("pdfbutton");
         myForm.enableItem("pdfanonbutton");

         if (perms.indexOf("C") >= 0) 
           myForm.enableItem("clonebutton");
         else
           myForm.disableItem("clonebutton");

         if (status==getProposalStatus("WITHDRAWN") && perms.indexOf("M")>=0) {
           myForm.setItemLabel("withdrawbutton","Restore Selected");
         }
         if (perms.indexOf("M") >= 0) {
           myForm.enableItem("withdrawbutton");
           myForm.enableItem("transferbutton");
         
           if (status != getProposalStatus("WITHDRAWN")) {
             myForm.setItemLabel("modbutton","Modify Selected");
           }
         }
         myForm.setItemValue("operation","");
      }

     
      function postInitLoad() {
        myTree.enableCheckBoxes(true,false);
        //set the filter options
       
        // get rid of HOLD if there are none
        haveHold=0;
        for (var ii=0;ii<mygrid.getRowsNum();ii++) {
          var str=mygrid.cellByIndex(ii,statCol).getValue();
          if (str.indexOf("HOLD")  >= 0)  haveHold=1;
        }
        if (haveHold == 0) {
           myTree.deleteItem("status_hold",false);
        }
          
        var filterBy = mygrid.getUserData(null,"filterBy");
        var filtarr = filterBy.split(",");
        for (var ii=0;ii< filtarr.length;ii++) {
          myTree.setCheck(filtarr[ii],true);
        }
        if (mygrid.getRowsNum() < 1) {
          new_entry();
        }
        postLoad();
      }

      function checkProposals() {
        var treeList =parent.mainTabbar.getAllItemsWithKids("myroot");
        var arr = treeList.split(",");
        for (ii=0;ii<arr.length;ii++) {
          if(arr[ii]>0) {
             if (mygrid.getRowIndex(arr[ii]) <= 0) {
               remove_tabs(arr[ii]);
             }
          }
        }
      }


      function postLoad() {
        checkProposals();

        var filtopt=myTree.getAllChecked();
        var filtarr = filtopt.split(",");
        if (filtopt==null || filtopt =="")  {
           //myTree.setItemStyle("myfilt2","font-weight:normal");
           myTree.setItemText("myfilt2",FILTERBY);
        } else {
           //myTree.setItemStyle("myfilt2","font-weight:bold");
           myTree.setItemText("myfilt2","<span style='font-weight:bold;'>" + FILTERBY + "</span> (" + filtarr.length  +" items)");
        }
        myTree.enableCheckBoxes(true,false);
        //mygrid.adjustParentSize();
        //mygrid.loadSizeFromCookie("homepage");
        //mygrid.loadSortingFromCookie("homepage2");
        myForm.setItemValue("operation","");
        var opts = myForm.getOptions("cloneprop");
        opts.add(new Option("No",0));
        if (mygrid.getRowsNum() < 1) {
          mygrid.addRow(0,[0,"","No Proposals","","","","","","","","DUH"]);
          mygrid.cells(0,2).setHorAlign('l');
          mygrid.cells(0,2).setBgColor(DISABLED_CELL_BG);
          mygrid.setColspan(0,2,9);
          myForm.hideItem("cloneprop");
        }
        else {
          var rid=mygrid.getRowId(0);
          if (rid == "error") {
            var estr=mygrid.cellByIndex(0,permCol).getValue();
            if (estr.indexOf("Invalid") >= 0) {
              top.location.replace("/cps-app/prop_logout.jsp");
            }
            else {
              set_status("<span class='errmsg'>" + estr + "</span>");
              mygrid.clearAll();
            }
          } else {
            for (var ii=0;ii<mygrid.getRowsNum();ii++) {
              var pno=mygrid.cellByIndex(ii,pnoCol).getValue();
              var pid=mygrid.getRowId(ii);
              var perms=mygrid.cellByIndex(ii,permCol).getValue();
              var pstat=mygrid.cellByIndex(ii,statCol).getValue();
              var psub=mygrid.cellByIndex(ii,submitCol).getValue();
              //alert(ii + " = " + pno );
 
              if (perms.indexOf("C") >= 0) {
                opts.add(new Option(pno,pid));
              }
              pstatVal=getProposalStatus(pstat);
              if (pstatVal.indexOf("progress") >= 0) {
                  mygrid.cellByIndex(ii,submitCol).setValue("Not Submitted");
              }
              mygrid.cellByIndex(ii,statCol).setValue(pstatVal);
              if (perms.indexOf("M") >= 0 && pstat.indexOf("INCOMPLETE")>=0) {
                var ptype=mygrid.cellByIndex(ii,typeCol).getValue();
                var ptitle=mygrid.cellByIndex(ii,titCol).getValue();
                add_tabs(pid,pno,ptitle,ptype);
                if (parent.mainTabbar) parent.mainTabbar.closeAllItems(pid);
                mygrid.setRowColor(mygrid.getRowId(ii),modColor);
                
              }
              else {
                 if (perms.indexOf("M") >= 0 ) {
                   mygrid.setRowColor(mygrid.getRowId(ii),canmodColor);
                 } else {
                   mygrid.setRowColor(mygrid.getRowId(ii),nomodColor);
                 }
              }
            }
            
          }
        }
        setChanges(0);
        mygrid.setSortImgState(true,5,"desc");
        cps_style_load();
      }

      function handle_change(id, val,state)
      {
        if (id=="cloneprop") {
          if (val != 0) {
            var type=mygrid.cellById(val,typeCol).getValue();
            var subcat=mygrid.cellById(val,subCol).getValue();
            var piid=mygrid.cellById(val,piidCol).getValue();
            myForm.setItemValue("clonepiid",piid);
            if (ddtRequest == "true") {
              if (type != "CAL") type="DDT";
            } else if (type == "DDT") {
              type = "TOO";
            }
            if (type.indexOf("GTO") >=0) {
              myForm.setItemValue("joint_type","None");
              myForm.disableItem("joint_type");
            } else {
              myForm.enableItem("joint_type");
            }
            myForm.setItemValue("proposal_type",type);
            myForm.setItemValue("subject_category",subcat);
          } else {
            myForm.setItemValue("clonepiid","0");
          }
        }
        else if (id=="proposal_type") {
          if (val.indexOf("GTO") >= 0 || val.indexOf("CAL") >= 0 ||
              val.indexOf("ARC") >= 0 || val.indexOf("THE") >= 0){
            myForm.setItemValue("joint_type","None");
            myForm.disableItem("joint_type");
          } else {
            myForm.enableItem("joint_type");
          }
        }
        else if (id=="joint_type") {
          if (val.indexOf("None") < 0 ) {
            var jval = val.replace("CXO-","");
            doConfirm("Are you sure this proposal has already been approved at the " + jval + " review?",jointConfirm);
          }
        }
      }
      function jointConfirm(ans)
      {
        if (!ans) { 
          myForm.setItemValue("joint_type","None");
        }
      }

      function loadPage() {
          setChanges(0);
          mygrid.clearAndLoad("/cps-app/cps_props?operation=LOAD",postInitLoad,"json");
          disable_buttons();
      }

      function doOnLoad() {
         formData=[
	    {type:"input",hidden:true,name:"operation",value:""},
	    {type:"input",hidden:true,name:"propno",value:""},
	    {type:"input",hidden:true,name:"pid",value:""},
	    {type:"input",hidden:true,name:"changestatus",value:""},
	    {type:"input",hidden:true,name:"clonepiid",value:"0"},
	    {type:"input",hidden:true,name:"page",value:"PROPMANAGE"},
               {type:"block",offset:0,list:[
                  {type:"newcolumn",offset:10},
                  {type:"button",name:"newbutton",value:"New Proposal"},
                  {type:"newcolumn",offset:10},
                  {type:"button",name:"clonebutton",value:"Clone Selected"},
                  {type:"newcolumn",offset:10},
                  {type:"button",name:"modbutton",value:"Modify Selected"},
                  {type:"newcolumn",offset:10},
                  {type:"button",name:"withdrawbutton",value:"Withdraw Selected"},
               ]},
               {type:"block",offset:0,list:[
                  {type:"newcolumn",offset:10},
                  {type:"button",name:"pdfbutton",value:"View Full Proposal Form"},
                  {type:"newcolumn",offset:10},
                  {type:"button",name:"pdfanonbutton",value:"View Anonymous Proposal Form"},
                  {type:"newcolumn",offset:10},
                  {type:"button",name:"transferbutton",value:"Transfer Ownership"},
               ]},
               {type:"block",offset:0,list:[
                  {type:"newcolumn",offset:10},
                  {type:"label",label:"Full Proposal forms are for PI use only. Anonymous proposal forms submitted to peer reviewers contain no PI or CoI information."},
               ]},
               {type:"block",offsetTop:20,list:[
            {type:"fieldset",name:"transfer",label:"Transfer Ownership",width:"95%",list:[
               {type:"container",name:"instrlbl", label:"<div class='instrLbl'>Once you transfer ownership of this proposal, you will not be allowed to edit the proposal. If you are not currently a Co-I on the proposal, you will not be allowed to view the proposal</div>"},
               {type:"label",name:"pnotransfer",label:""},
               {type:"label",name:"titletransfer",offsetTop:0,label:""},
               {type:"combo",name:"lasttransfer",className:"reqLbl",label:"<a href='javascript:openHelpWindow(\"#TransferName\")'>Transfer Proposal Ownership to:</a>"},
                 {type:"block",list:[
                   {type:"button",name:"ownerbutton",value:"Save"},
                   {type:"newcolumn",offset:30},
                   {type:"button",name:"cancelbutton",value:"Cancel"},
                 ]},
            ]},
                 ]},
               {type:"block",offsetTop:20,list:[
            {type:"fieldset",name:"details",label:"New Proposal",width:"95%",list:[
              {type:"label",name:"ptitle",label:""},
              {type: "block",name:"whoblock", list:[
                 {type:"select",name:"cloneprop",label:"<a href='javascript:openHelpWindow(\"#CloneProposal\")'>Clone Proposal:?</a>",position:"label-left",labelWidth:lblW},
                 {type:"select",name:"prop_who",className:"reqLbl",label:"<a href='javascript:openHelpWindow(\"#Who\")'>Who are you?</a>",position:"label-left",labelWidth:lblW, options:[
                    {value:"PI",text:"Principal Investigator "},
                    {value:"CoI",text:"Co-Investigator"},
                    {value:"Other",text:"Other"}
                 ]},
                 {type:"select",name:"subject_category",className:"reqLbl",label:"<a href='javascript:openHelpWindow(\"#SubjectCategory\")'>Subject Category:</a>",position:"label-left",labelWidth:lblW},
                 {type:"select", name: "proposal_type",className:"reqLbl",label:"<a href='javascript:openHelpWindow(\"#ObservationType\")'>Proposal Type:</a>" , labelWidth:175 },
                 {type:"select",name:"joint_type",label:"<a href='javascript:openHelpWindow(\"#JointApproved\")'>Has this proposal already been approved by the HST, NRAO or XMM Review and allocated Chandra time?</a>" ,position:"label-left", options:[
                   {value:"None",text:"No",checked:true},
                   {value:"CXO-HST",text:"HST"},
                   {value:"CXO-NRAO",text:"NRAO"},
                   {value:"CXO-XMM",text:"XMM"},
                 ]},

                 {type:"block",list:[
                   {type:"button",name:"createbutton",value:"Create Proposal"},
                   {type:"newcolumn"},
                   {type:"button",name:"cancelbutton",value:"Cancel",offsetLeft:30},
                 ]},
                 ]},
              ]},
            ]},
    {type:"container",name:"status",className:"statusContainer"}
         ];

         
         myForm=new dhtmlXForm("formB_container",formData);
         obj = document.getElementById("formB_container");

         myForm.setSkin('dhx_skyblue');
         buildSubCat(myForm.getOptions("subject_category"));
         buildPropType(myForm.getOptions("proposal_type"),ddtRequest);
         myForm.attachEvent("onChange", handle_change);
         myForm.attachEvent("onAfterValidate", after_validate);
         myForm.attachEvent("onButtonClick",function(id){
            clear_status();
            if (id=="newbutton") new_entry();
            else if (id=="withdrawbutton") withdraw_entry();
            else if (id=="modbutton") modify_entry();
            else if (id=="pdfanonbutton") download_pdf("ANON");
            else if (id=="pdfbutton") download_pdf();
            else if (id=="clonebutton") clone_entry();
            else if (id=="createbutton") create_entry();
            else if (id=="transferbutton") show_transfer();
            else if (id=="ownerbutton") save_owner();
            else if (id=="cancelbutton") cancel_add();
         });
         if (ddtRequest == "true") {
            myForm.hideItem("joint_type");
         }
         myForm.hideItem("details");
         myForm.hideItem("transfer");
         var picombo = myForm.getCombo("lasttransfer");
         picombo.enableAutocomplete();
         picombo.enableFilteringMode("between");
         picombo.allowFreeText(false);
         picombo.setSize(250);


         mygrid=new dhtmlXGridObject('grid');
         //mygrid.setHeader("&nbsp;,P,Proposal,Type,Status,Submitted,<div tooltip='* indicates PI is not the person submitting the proposal.'>PI</div>,Subject Category,Title,Joint");
         var gdiv ="<div title='Click header to sort table. Double click on row to view/modify selected proposal.'>";
         var myhdr ="&nbsp;,P," + gdiv +"Own?</div>," + gdiv +"Proposal</div>," + gdiv +"Type</div>," + gdiv +"Status</div>," + gdiv +"Submitted</div>," + "<div title='An * indicates the PI is not the person submitting the proposal.'>PI</div>," + gdiv +"Subject Category</div>," + gdiv +"Title</div>," + gdiv + "Joint</div>";
         mygrid.setHeader(myhdr);
         mygrid.setInitWidthsP(".01,.01,5,9,10,10,13,13,13,22,*");
         mygrid.setColumnMinWidth(".01,.01,2,10,10,10,10,10,10,10,10");
         mygrid.setColAlign("left,left,center,left,left,left,left,left,left,left,left");
         mygrid.setSkin("dhx_skyblue");
         mygrid.setColTypes("ro,ro,ro,ro,ro,ro,ro,ro,ro,ro,ro");
         mygrid.setColSorting("str,str,str,int,custom_str_sort,custom_str_sort,custom_date_sort,custom_str_sort,custom_str_sort,custom_str_sort,custom_str_sort");
         mygrid.setColumnHidden(0,true);
         mygrid.setColumnHidden(1,true);
         mygrid.setIconsPath(CSS_IMG_PATH);
         mygrid.setImagePath(CSS_IMG_PATH);
         mygrid.init();
         mygrid.enableAutoHeight(true,300);
         mygrid.enableColSpan(true);

         //mygrid.enableAutoSizeSaving("homepage","path:/cps-app;");
         //mygrid.enableSortingSaving("homepage2","Path:/cps-app;");
         //mygrid.enableAutoWidth(true);
         mygrid.enableRowsHover(true,"gridhover");
         mygrid.attachEvent("onRowSelect",row_selected);
         mygrid.attachEvent("onRowDblClicked",modify_entry);
         mygrid.attachEvent("onAfterSorting",after_sort);
         mygrid.attachEvent("onMouseOver",mouse_over);


        window.dhx.attachEvent("onLoadXMLError",function(request,object) {
              //data[0] - request object
              //data[0].responseText - incorrect server side response
              //if ( data[0].status == 401 ) { // Session expired
                    top.location.replace="/cps-app/prop_logout.jsp";
               //}
               return false;
         });

        //window.addEventListener("resize",resizeLayout, false);

        myTree = new dhtmlXTreeObject("filtTreeDiv",null,null,"myfilt");
        myTree.setImagePath(NAV_IMG_PATH);
        myTree.enableTreeImages(false);
        myTree.enableTreeLines(false);
        myTree.enableCheckBoxes(true,false);
        myTree.enableSmartCheckboxes(true);
        myTree.enableHighlighting(true);
        myTree.setOnCheckHandler(filt_checked);
        myTree.attachEvent("OnClick",filt_clicked);
        myTree.load("/cps-app/propfilter.xml",postfilt,"xml");
           
      }

      function filt_clicked(id) {
         if (myTree.hasChildren(id)) { 
           if (myTree.getOpenState(id) == true)
             myTree.closeItem(id);
           else
             myTree.openItem(id);
             return;
         }
         if (myTree.isItemChecked(id))
            myTree.setCheck(id,false);
         else
            myTree.setCheck(id,true);
         filt_checked();
      }

      function filt_checked() {
        // send the checked filters and set in session
        // then reload grid with selected options
        var filterBy=myTree.getAllChecked();
        var url = "/cps-app/cps_props?operation=FILTER&filterBy=" + filterBy;
        myTree.enableCheckBoxes(false,false);
        mygrid.clearAndLoad(url,postLoad,"json");
      }

      function postfilt() {
        myTree.closeItem("myfilt2");
        loadPage();
      }

      function resizeLayout() {
      }
