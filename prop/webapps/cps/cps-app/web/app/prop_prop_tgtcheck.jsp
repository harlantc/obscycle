<%@ page session="true" import="java.util.*" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1
%>

<%
String  pid = (String)request.getParameter("id");
if (pid == null)  pid = "unknown";
%>
<%@ include file="cps_hdr.html" %>

  <script type="text/javascript">
    var myForm,formData,simbadgrid,rcgrid;
    var pid = "<%= pid %>";
    var processMsg = "<span class='processLbl'><img src='/cps-app/imgs/ajax-loader.gif' alt='Loading..'></span>";

    function postGrid(oper) {
      var vv=null;
      var ao = parseInt(myForm.getItemValue("currentAO"));
      var ao1 = ao +1;
      var ao2 = ao +2;
      var mcop = myForm.getItemValue("mcop");

      //var oper = myForm.getItemValue("operation");
      if (oper.indexOf("sim") >= 0 && simbadgrid.getRowsNum() > 0) {
        myForm.setItemLabel("simbadmsg","");
        vv= simbadgrid.getRowId(0);
        simbadgrid.setSortImgState(true,0,"asc");
      } else if (oper.indexOf("constraints") >= 0 && rcgrid.getRowsNum() >0) {
        myForm.setItemLabel("rcmsg","");
        vv= rcgrid.getRowId(0);
        rcgrid.setColumnHidden(0,true);
        if (myForm.getItemValue("proposal_type").indexOf("TOO") >= 0) {
          rcgrid.setColLabel(3,"Very Fast");
          rcgrid.setColWidth(3,100);
          rcgrid.setColumnHidden(4,false);
          rcgrid.setColumnHidden(5,false);
          rcgrid.setColumnHidden(6,false);
        }
        else{
          rcgrid.setColumnHidden(6,true);
          if (mcop.includes("true")) {
            rcgrid.setColumnHidden(4,false);
            rcgrid.setColumnHidden(5,false);
            rcgrid.setColLabel(3,"ResourceCost Cycle " + ao); 
            rcgrid.setColLabel(4,"ResourceCost Cycle " + ao1);
            rcgrid.setColLabel(5,"ResourceCost Cycle " + ao2);
            rcgrid.setColWidth(4,150);
            rcgrid.setColWidth(5,150);
          }
          else{
            // hide mcop columns if not mcop
            rcgrid.setColumnHidden(4,true);
            rcgrid.setColumnHidden(5,true);
          }
        }
          
        if (vv == 0) {
            rcgrid.setColspan(vv,1,(rcgrid.getColumnsNum()-1));
            rcgrid.cells(vv,1).setHorAlign("left");
            rcgrid.cells(vv,1).setBgColor(DISABLED_CELL_BG);

        } else  {
          rcgrid.sortRows(1,"int","asc");
        }
      }

      if (vv != null && vv.toString().indexOf("Invalid") >= 0){
          top.location.replace("/cps-app/prop_logout.jsp");
      }
      
    }

    function download_pdf(isAnon="") {
      document.body.className = "waiting";
      isAnon ? myForm.disableItem("pdfanonbutton") : myForm.disableItem("pdfbutton");
      var url = "/cps-app/cps_loadprop?type=f&page=CPSPDF" + isAnon + "&pid=" + pid ;
      var mywin = window.open(url, "cpspdf", "alwaysLowered=yes, left=10, top=10,width=300, height=100, toolbar=no, menubar=no");
      mywin.document.title="CPS PDF";
      window.setTimeout(function() {
            document.body.className = "";
            isAnon ? myForm.enableItem("pdfanonbutton") : myForm.enableItem("pdfbutton");
          },3000);
    }

    function handle_change(id) {
      pid = myForm.getItemValue("pid");
      var url = "/cps-app/cps_review?pid=" + pid + "&operation=" + id;
      if ( id =="simblk" ) {
        var str =myForm.isItemChecked(id);
        var fs = id + "fs";
        if (str) {
          myForm.showItem(fs);
          if (simbadgrid.getRowsNum() < 1) {
            myForm.setItemLabel("simbadmsg",processMsg);
            myForm.setItemValue("operation",id);
            simbadgrid.load(url,null,"json").then(function() { postGrid(id); });
          }
        } else 
          myForm.hideItem(fs);
      } else if ( id =="ocatblk" ||
                  id =="slewblk" ||
                  id =="constraints" ) {
        var str =myForm.isItemChecked(id);
        var fs = id + "fs";
        if (str) {
          var cval="";
          var obj=null;
          myForm.showItem(fs);
          if (id == "slewblk")  {
            cval= myForm.getContainer("slewdiv").innerHTML.length;
            obj= myForm.getContainer("slewdiv");
          } else if (id == "ocatblk")  {
            cval= myForm.getContainer("conflicts").innerHTML.length;
            obj= myForm.getContainer("conflicts");
            var jj=document.getElementById("simbadgrid");
            //alert(jj.style.width);
            obj.style.width=jj.style.width;
          }
          if (cval < 2) {
            myForm.setItemValue("operation",id);
          if (id == "constraints")
              myForm.setItemLabel("rcmsg",processMsg);
            if (obj != null)
              obj.innerHTML = processMsg;
              //obj.innerHTML = "<span style='font-size:normal'>Loading ...You may view other results on this page while this request is processing.</span>";
            myForm.load(url,postLoad2);
          } else {
          }
        } else 
            myForm.hideItem(fs);
      } else if (id == "proposal_type") {
        var ptype = myForm.getItemValue("proposal_type");
        if (ptype.indexOf("ARC") >= 0  || ptype.indexOf("THE") >= 0 ) {
          myForm.setItemLabel("total_time","<a href='javascript:openHelpWindow(\"#ArchiveBudget\")'>Requested Budget</a>");
          myForm.hideItem("num_targets");
          myForm.hideItem("ocatblk");
          myForm.hideItem("ocatlbl");
          myForm.hideItem("simblk");
          myForm.hideItem("simlbl");
          myForm.hideItem("slewblk");
          myForm.hideItem("slewlbl");
          myForm.hideItem("constraints");
          myForm.hideItem("rclbl");
        }
      } else {
        setChanges(1);
      }
      clear_status();
    }

    function postLoad2() {
      var propno = myForm.getItemValue("propno");
      if (propno.indexOf("Invalid") >= 0) {
          doAlertLogoff(myForm.getItemValue("emsg"));
          return;
      }
      if (propno.indexOf("Error") >= 0) {
        var emsg = myForm.getItemValue("emsg");
        doAlert(emsg,badalertReturn);
        return;
      }

      set_status(myForm.getItemValue("emsg")); 

      var id = myForm.getItemValue("operation");
      if (id.indexOf("slew") >= 0) {
        var obj = myForm.getContainer("slewdiv");
        
        var str = "<pre>" + myForm.getItemValue("retstr").replace(/\n/g,"<br>") + "</pre>";
        obj.innerHTML = str;
      }
      else if (id.indexOf("constraints") >= 0) { 
        var pid = myForm.getItemValue("pid");
        var url= "/cps-app/cps_review?pid=" + pid + "&operation=constraints" ;
        rcgrid.load(url,null,"json").then(function() { postGrid(id); });
      }
      else if (myForm.getItemValue("operation").indexOf("ocat") >= 0) {
        var obj = myForm.getContainer("conflicts");
        var str = myForm.getItemValue("retstr").replace(/\n/g,"<br>") ;
        obj.innerHTML = str;
      }
    }


    function postLoad() {
      var pno = myForm.getItemValue("propno");
      var pid = myForm.getItemValue("pid");
      if (pno.indexOf("Invalid") >= 0) {
        doAlertLogoff(myForm.getItemValue("emsg"));
        return;
      }
      if (pno.indexOf("Error") >= 0) {
        doAlert(myForm.getItemValue("emsg"),badalertReturn);
        return;
      }

      set_status(myForm.getItemValue("emsg")); 

      var pstat = myForm.getItemValue("pstatus");
      myForm.setItemValue("pstatus",getProposalStatus(pstat));
      handle_change("proposal_type");
      var fslbl = myForm.getItemValue("propno") + ": Target Checks  " + myForm.getItemValue("proposal_title");
      myForm.setItemLabel("summaryfs",fslbl);
      cps_style_load();
      setChanges(0);  
      if (myForm.getItemValue("isDDT") == "true") {
        myForm.hideItem("constraints")
      }
      if (myForm.getItemValue("isEdit") != "true") {
        set_editButtons(myForm);
        myForm.enableItem("slewblk");
        myForm.enableItem("ocatblk");
        myForm.enableItem("simblk");
        myForm.enableItem("constraints")

      }
    }

    function loadPage() {
        clear_status();
        setChanges(0);
        myForm.load("/cps-app/cps_loadprop?page=SUMMARY&pid="+pid,postLoad);
    }

    function reset_fields() {
        myForm.reset();
        clear_status();
        setChanges(0);  
    }

    function doOnLoad() {
      formData = [
        {type:"input",hidden:true,name:"operation",value:""},
        {type:"input",hidden:true,name:"page",value:"SUMMARY"},
        {type:"input",hidden:true,name:"pid",value:"<%=pid%>"},
        {type:"input",hidden:true,name:"propno"},
        {type:"input",hidden:true,name:"currentAO"},
        {type:"input",hidden:true,name:"mcop"},
        {type:"input",hidden:true,name:"proposal_title"},
        {type:"input",hidden:true,name:"emsg",value:""},
        {type:"input",hidden:true,name:"retstr",value:""},
        {type:"input",hidden:true,name:"isEdit",value:"false"},
        {type:"input",hidden:true,name:"isGrid",value:"true"},
        {type:"input",hidden:true,name:"isDDT",value:"false"},
        {type:"input",hidden:true,name:"response_time",value:""},

        {type:"fieldset",name:"summaryfs",label:"Review",list:[
          {type:"block",list:[
              {type:"input",name:"proposal_type",readonly:true, inputWidth:100,label:"<a href='javascript:openHelpWindow(\"#ObservationType\")'>Type</a>:",value:""},
              {type:"newcolumn",offset:10},
              {type:"input",name:"pstatus",readonly:true, label:"<a href='javascript:openHelpWindow(\"#pstatus\")'>Status</a>:",value:"",inputWidth:150},
              {type:"newcolumn",offset:10},
              {type:"input",name:"total_time",readonly:true,label:"<a href='javascript:openHelpWindow(\"#tottime\")'>Total Time</a>:",value:"",inputWidth:50},
              {type:"newcolumn",offset:10},
              {type:"input",name:"num_targets",readonly:true,label:"<a href='javascript:openHelpWindow(\"#nbrtgt\")'>Number of Targets</a>:",value:"",inputWidth:50},
            ]
          },
            {type:"block",list:[
              {type:"label",label:"<a href='javascript:openHelpWindow(\"#Checks\")'>View:</a> "},
              {type:"newcolumn",offset:10},
              {type:"checkbox",position:"label-right",label:"<span class='errlbl'>Slew Tax</span>",name:"slewblk"},
              {type:"newcolumn",offset:10},
              {type:"checkbox",position:"label-right",label:"<span class='errlbl'>Resources</span>",name:"constraints"},
              {type:"newcolumn",offset:10},
              {type:"checkbox",position:"label-right",label:"Simbad/Ned Results",name:"simblk"},
              {type:"newcolumn",offset:10},
              {type:"checkbox",position:"label-right",label:"Chandra Conflicts",name:"ocatblk"},
              {type:"newcolumn",offset:30},
              {type:"button",offsetTop:0,name:"pdfbutton",value:"View Full Proposal Form"},
              {type:"newcolumn",offset:10},
              {type:"button",offsetTop:0,name:"pdfanonbutton",value:"View Anonymous Proposal Form"},
              {type:"newcolumn",offset:0},
              ]
            },
            {type:"block",list:[
              {type:"newcolumn",offset:0},
              {type:"label",label:"Full Proposal forms are for PI use only. Anonymous proposal forms submitted to peer reviewers contain no PI or CoI information."},
              ]
            }
          ]
        },
        {type:"fieldset",name:"slewblkfs",label:"Slew Tax",width:"95%",list:[
            {type:"block",list:[
              {type:"label",name:"slewmsg",label:""},
              {type:"container",name:"slewdiv",className:"slewcontainer"}
              ]
            }
          ]
        },
        {type:"fieldset",name:"constraintsfs",label:"Resources",width:"95%",list:[
            {type:"block",list:[
              {type:"label",name:"rcmsg",label:""},
              {type: "label",label:"<div id='rcgrid' style='margin:0;width:95%'></div>",offsetTop:0},
              ]
            }
          ]
        },
        {type:"fieldset",name:"simblkfs",label:"Simbad/NED Results",width:"100%",list:[
            {type:"block",list:[
              {type:"label",name:"simbadmsg",label:""},
              {type: "label",label:"<div id='simbadgrid' style='margin:0;width:95%'></div>",offsetTop:0},

              ]
            }
          ]
        },
        {type:"fieldset",name:"ocatblkfs",label:"Chandra Observation Catalog Conflicts",width:"100%",list:[
            {type:"block",list:[
              {type:"container",name:"conflicts",className:"valcontainer"}
              ]
            }
          ]
        },
        {type:"container",name:"status",className:"statusContainer"},


      ];
      myForm = new dhtmlXForm("formB_container",formData);
      myForm.setSkin('dhx_skyblue');
      myForm.hideItem("slewblkfs");
      myForm.hideItem("constraintsfs");
      myForm.hideItem("simblkfs");
      myForm.hideItem("ocatblkfs");
      // cps_style_init();
      buildTooltips(myForm);

      myForm.attachEvent("onChange", handle_change);
      myForm.attachEvent("onButtonClick",function(id) {
          clear_status();
          if (id=="pdfanonbutton") download_pdf("ANON");
          else if (id=="pdfbutton") download_pdf();
        }
      );

        simbadgrid=new dhtmlXGridObject('simbadgrid');
        simbadgrid.setHeader("#,Target Name,Proposal,#cspan,Simbad/NED,#cspan,Results",null,[,,"text-align:center",,"text-align:center",,,]);
        simbadgrid.attachHeader("#rspan,#rspan,RA,Dec,RA,Dec,#rspan");
        //simbadgrid.setInitWidths("50,100,100,100,100,100,300");
        simbadgrid.setInitWidthsP("5,10,12,12,12,12,37");
        simbadgrid.setColumnMinWidth("20,20,20,20,20,20,20");
        simbadgrid.setColAlign("center,center,center,center,center,center,center");
        simbadgrid.setColSorting("int,custom_str_sort,custom_str_sort,custom_str_sort,custom_str_sort,custom_str_sort,custom_str_sort");
        simbadgrid.setSkin("dhx_skyblue");
        simbadgrid.setColTypes("ro,ro,ro,ro,ro,ro,ro");
        simbadgrid.setImagePath(CSS_IMG_PATH);
        simbadgrid.init();
        simbadgrid.enableAutoHeight(true,300,true);
        simbadgrid.enableAutoWidth(true);
        //simbadgrid.enableRowsHover(true,"gridhover");

        rcgrid=new dhtmlXGridObject('rcgrid');
        rcgrid.setHeader("Proposal,Tgt#,TargetName,ResourceCost,Fast,Medium,Slow");
        rcgrid.setInitWidths("100,50,200,150,100,100,100");
        rcgrid.setColumnMinWidth("10,10,10,10,10,10,10");
        rcgrid.setColAlign("center,center,left,center,center,center,center");
        rcgrid.setColSorting("int,int,str,int,int,int,int");
        rcgrid.setSkin("dhx_skyblue");
        rcgrid.setColTypes("ro,ro,ro,ro,ro,ro,ro");
        rcgrid.setImagePath(CSS_IMG_PATH);
        rcgrid.init();
        rcgrid.enableAutoHeight(true,300,true);
        rcgrid.enableAutoWidth(true);
        rcgrid.enableColSpan(true);
        rcgrid.setColumnHidden(4,true);
        rcgrid.setColumnHidden(5,true);
        rcgrid.setColumnHidden(6,true);
        //rcgrid.enableRowsHover(true,"gridhover");

        loadPage();
    }

  </script>
</head>

<body onload="showTimeoutWarn(false);doOnLoad()">
  <div id="msgbody" class="msgbody">
      <img src="/cps-app/imgs/ajax-loader.gif" alt="Loading...">
  </div>
  <div id="pagebody" class="pagebody">

  <div id="formB_container" ></div> 
  </div>
</body>
</html>