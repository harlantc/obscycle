<%@ page session="true" import="java.util.*" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1
%>

<%
String  pid = (String)request.getParameter("id");
if (pid == null)  pid = "unknown";
%>
<%@ include file="cps_hdr.html" %>

  <script type="text/javascript">
    var myForm,formData,valgrid;
    var pid = "<%= pid %>";
    var processMsg = "<span class='processLbl'><img src='/cps-app/imgs/ajax-loader.gif' alt='Loading..'></span>";

    function popout() {
      var valURL = "/cps-app/app/prop_val.jsp?pid=" + pid + "&pno=" + myForm.getItemValue("propno");
      top.parent.valWindow = window.open(valURL,"cpsvalWindownWNyQx18M","width=850,height=450,toolbar=no,location=no,titlebar=no,status=no,scrollbars=yes");
      top.parent.valWindow.focus();
    }

    function sort_span(a,b,order) {
      var aval = a.split('>')[1];
      var bval = b.split('>')[1];
      if (order=="asc")
        return aval>bval?1:-1;
      else
        return aval<bval?1:-1;
    }

      function postGrid(oper) {
        var vv=null;
        //var oper = myForm.getItemValue("operation");
        if (oper.indexOf("val") >= 0 && valgrid.getRowsNum() > 0) {
          myForm.setItemLabel("valmsg","");
          vv= valgrid.getRowId(0);
          valgrid.setSortImgState(true,0,"asc");
          addErrorLink();
        }
        if (vv != null && vv.toString().indexOf("Invalid") >= 0){
           top.location.replace("/cps-app/prop_logout.jsp");
        }
      }

    function handle_change(id) {
      pid = myForm.getItemValue("pid");
      var url = "/cps-app/cps_review?pid=" + pid + "&operation=" + id;
      if (id =="valgrid" ) {
        var fs = "valblkfs";
        if (valgrid.getRowsNum() < 1) {
          myForm.setItemValue("operation",id);
          myForm.setItemLabel("valmsg",processMsg);
          valgrid.load(url,null,"json").then(function() { postGrid(id); });
          if (top.parent.valWindow != null && !top.parent.valWindow.closed)
            popout();
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
      if (myForm.getItemValue("operation").indexOf("val") >= 0) {
        var obj = myForm.getContainer("validation");
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
      handle_change("ready");
      handle_change("proposal_type");
      var fslbl = myForm.getItemValue("propno") + ": Validation  " + myForm.getItemValue("proposal_title");
      myForm.setItemLabel("valblkfs",fslbl);
      cps_style_load();
      setChanges(0);
      handle_change("valgrid");
    }

    function loadPage() {
      clear_status();
      setChanges(0);  
      myForm.load("/cps-app/cps_loadprop?page=SUMMARY&pid="+pid,postLoad);
    }

    function addErrorLink() {
      pid = myForm.getItemValue("pid");
      propno = myForm.getItemValue("propno");
      var proplink = "<a href=\"javascript:parent.mainTabbar.selectItem('" + pid;
      var url="/cps-app/app/prop_tgt_edit.jsp?pid=" + pid + "&tid=TID&propno=" + propno + "&page=TAB" ;
      var tgtlink= "<a href=\"javascript:top.parent.mainTabbar.selectItem('" + pid + "TargetManage',false,false);self.location.replace('";
      for (var ii=0;ii<valgrid.getRowsNum();ii++) {
        var nvalue = valgrid.cellByIndex(ii,1).getValue();
        var lvalue = 0;
        var tab = 0;
        if (nvalue.indexOf("Cover") >= 0) 
          lvalue= proplink + "Proposal',true);\">" + nvalue + "</a>";
        else if (nvalue.indexOf("P.I") >= 0) 
          lvalue= proplink + "PI',true);\">" + nvalue + "</a>";
        else if (nvalue.indexOf("Co-") >= 0) 
          lvalue= proplink + "CoI',true);\">" + nvalue + "</a>";
        else if (nvalue.indexOf("DDT") >= 0) 
          lvalue= proplink + "DDT',true);\">" + nvalue + "</a>";
        else if (nvalue.indexOf("Joint") >= 0) 
          lvalue= proplink + "Joint',true);\">" + nvalue + "</a>";
        else if (nvalue.indexOf("Targets") >= 0)
            lvalue= proplink + "TargetManage',true);\">" + nvalue + "</a>";
        else if (nvalue.indexOf("Supp") >= 0) 
          lvalue= proplink + "Upload',true);\">" + nvalue + "</a>";
        else if (nvalue.indexOf("Pointing") >= 0) 
          tab = "t1";  
        else if (nvalue.indexOf("Observing Time") >= 0) 
          tab = "t2";  
        else if (nvalue.indexOf("Instrument") >= 0) 
          tab = "t3";  
        else if (nvalue.indexOf("ACIS(req") >= 0) 
          tab = "t4";  
        else if (nvalue.indexOf("ACIS(opt") >= 0) 
          tab = "t5";  
        else if (nvalue.indexOf("Constraint") >= 0) 
          tab = "t6";  
        else if (nvalue.indexOf("TOO") >= 0) 
          tab = "t7";  
        else if (nvalue.indexOf("Remarks") >= 0) 
          tab = "t8";  

        if (tab != 0) {
          lvalue = tgtlink + url + "');\">" + nvalue + "</a>";
          var tid = valgrid.cellByIndex(ii,3).getValue();
          lvalue = lvalue.replace(/TID/,tid);
          lvalue = lvalue.replace(/TAB/,tab);
          //console.log(lvalue);
        }

        if (lvalue != 0)
          valgrid.cellByIndex(ii,1).setValue(lvalue);
      }
    }

    function doOnLoad() {
      formData = [
        {type:"input",hidden:true,name:"operation",value:""},
        {type:"input",hidden:true,name:"page",value:"SUMMARY"},
        {type:"input",hidden:true,name:"pid",value:"<%=pid%>"},
        {type:"input",hidden:true,name:"propno"},
        {type:"input",hidden:true,name:"proposal_title"},
        {type:"input",hidden:true,name:"emsg",value:""},
        {type:"input",hidden:true,name:"retstr",value:""},
        {type:"input",hidden:true,name:"isEdit",value:"false"},
        {type:"input",hidden:true,name:"isDDT",value:"false"},
        {type:"input",hidden:true,name:"response_time",value:""},

        {type:"fieldset",name:"valblkfs",label:"Validation Results",width:"95%",list:[
            {type:"block",list:[
              {type:"label",label:"<a href='javascript:openHelpWindow(\"#Checks\")'>Validation Results </a> "}	,
              {type:"newcolumn",offset:0},
              {type:"label",label:"<a href='javascript:popout()'>View results in new window</a>"},
              {type:"label",name:"valmsg",label:""},
              {type: "label",label:"<div id='valgriddiv' style='margin:0;width:95%'></div>",offsetTop:0},
              ]
            }
          ]
        },
        {type:"container",name:"status",className:"statusContainer"},
      ];
      myForm = new dhtmlXForm("formB_container",formData);
      myForm.setSkin('dhx_skyblue');

      // cps_style_init();
      buildTooltips(myForm);

      myForm.attachEvent("onChange", handle_change);

      valgrid=new dhtmlXGridObject('valgriddiv');
      valgrid.setHeader("Type,Page,Tgt#,Tid,Message");
      //valgrid.setInitWidths("80,150,65,1,600");
      valgrid.setInitWidthsP("10,20,5,1,65");
      valgrid.setColumnMinWidth("10,10,10,10,10");
      valgrid.setColAlign("center,center,center,center,left");
      valgrid.setColSorting("sort_span,custom_str_sort,int,int,custom_str_sort");
      valgrid.setSkin("dhx_skyblue");
      valgrid.setColTypes("ro,ro,ro,ro,ro");
      valgrid.setImagePath(CSS_IMG_PATH);
      valgrid.enableAutoHeight(true,300);
      valgrid.enableAutoWidth(true);
      valgrid.enableMultiline(true);
      valgrid.setColumnHidden(3,true);
      valgrid.init();
      valgrid.attachEvent("OnBeforeSelect",function(new_row,old_row,new_col) {
        return false;
        }
      );

      //valgrid.enableRowsHover(true,"gridhover");
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