<%@ page session="true" import="java.util.*" %>
<% response.setHeader("Cache-Control","no-cache,no-store"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", -1); 
%>

<%
String pid = (String)request.getParameter("pid");
if (pid == null) {
  pid = "unknown";
}
String tid = (String)request.getParameter("tid");
if (tid == null) {
  tid = "unknown";
}

%>

<%@ include file="cps_hdr.html" %>
   <script type="text/javascript" src="../options.js"></script>

   <script type="text/javascript">
      var tid = "<%=tid%>";
      var pid = "<%=pid%>";
   </script>

</head>
 
<body onload="showTimeoutWarn(false);doOnLoad()">
  <div id="msgbody" class="msgbody">
    <img src="/cps-app/imgs/ajax-loader.gif" alt="Loading...">
  </div>
  <div id="pagebody" class="pagebody">

   <div id="formB_container" ></div>
   <script>
      var myForm,formData,lblW=175;


      function handle_change(id) {
        if (id == "remark_constraint") {
          val = myForm.getItemValue("remark_constraint");
          if (val != null && val =="Y" )
            document.getElementById("rmkwarn").innerHTML ="This counts as a constraint.";
          else
            document.getElementById("rmkwarn").innerHTML ="";
        }
        setChanges(1);
        clear_success_status();
      }
  
      function validate_remarks()
      {
        var retval=true;
        set_errlbl(false,myForm,"target_remarks");
        var str;
        if (myForm.getItemValue("remark_constraint") != "N") {
          if (!validate_dependencies(myForm,["remark_constraint","target_remarks"])) {
            var str = myForm.getItemLabel("target_remarks") + ": Remarks are required if " + myForm.getItemLabel("remark_constraint") + " is Yes";
            set_status(str);
            retval=false;
          }
        }
        return retval;
      }

      function save_changes() {
        myForm.setItemValue("operation","SAVE");
        if (validate_remarks()) {
          myForm.disableItem("save");
          document.body.className = "waiting";
          myForm.send("/cps-app/cps_savetgt","post",function(name,retstr) {
           document.body.className = "";
           myForm.enableItem("save");
           if (process_save(retstr,false))
             setChanges(0);
          });
        } else {
          myForm.enableItem("save");
        }
        return true;
      }
  
      function reset_fields() {
        loadPage();
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

        var isEdit = myForm.getItemValue("isEdit");
        set_editButtons(myForm,isEdit);
        handle_change("remark_constraint");
        setChanges(0);
        cps_style_load();

      } 

      function before_load(id,values) {
        // this handles case of the constraints that previously allowed preferred
        handle_preferred("remark_constraint",values);
        return true;
      }

      function input_change(id,new_val,form) {
        if (id== "target_remarks") {
          if (new_val.length==1 ) {
            myForm.setItemValue("remark_constraint","Y");
            handle_change("remark_constraint");
          }
        }
        return true;
      }


      function loadPage() {
        var url ="../cps_loadtgt?page=REMARKS&pid=" + pid + "&tid=" +tid;
        myForm.load(url,postLoad);

      }

      function doOnLoad() {
         formData=[
           {type:"settings",position:"label-left"},
           {type:"input",hidden:true,name:"operation",value:""},
           {type:"input",hidden:true,name:"isEdit",value:""},
           {type:"input",hidden:true,name:"pid",value:""},
           {type:"input",hidden:true,name:"tid",value:""},
           {type:"input",hidden:true,name:"page",value:"REMARKS"},
           {type:"input",hidden:true,name:"propno",value:""},
           {type:"input",hidden:true,name:"tgtnbr",value:""},
           {type:"input",hidden:true,name:"emsg",value:""},

              {type:"fieldset",name:"fsremark",label:"Remarks",list:[
               {type:"block",list:[
                  {type:"select",name:"remark_constraint",label:"<a href='javascript:openHelpWindow(\"#AddConstraints\")'>Are there additional constraints in the Remarks?</a>"},
                 {type:"newcolumn",offset:20},
                 {type:"label",label:"<span id='rmkwarn' class='errmsgSmall'></span>"}
               ]},
               {type:"block",list:[
{type:"input", name:"target_remarks", label:"<a href='javascript:openHelpWindow(\"#Remarks\")'>Remarks (600 chars)</a>",  rows:10,inputWidth:600 , maxLength:600},
               ]}
              ]},
    {type:"container",name:"status",className:"statusContainer"},
    {type:"block",offsetLeft:150,offsetTop:25,list:[
        {type:"button",name:"save",value:"Save Changes"},
        {type:"newcolumn"},
        {type:"button",name:"discard",value:"Discard Changes"},
        {type:"newcolumn"},
    ]},


         ];

         myForm=new dhtmlXForm("formB_container",formData);

         myForm.setSkin('dhx_skyblue');
         buildYN(myForm.getOptions("remark_constraint"),0);

         myForm.attachEvent("onBeforeDataLoad", before_load);
         myForm.attachEvent("onChange",handle_change);
         myForm.attachEvent("onInputChange",input_change);
         myForm.attachEvent("onButtonClick",function(id){
            clear_status();
            if (id=="save") save_changes();
            else if (id=="discard") reset_fields();
         });

         buildTooltips(myForm);

         loadPage();
      }   
   </script>
</div>
</body>
</html>
