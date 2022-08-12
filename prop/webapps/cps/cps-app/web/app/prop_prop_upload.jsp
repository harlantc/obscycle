<%@ page session="true" import="java.util.*,edu.harvard.asc.cps.xo.CPS" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1
%>

<%
String  pid = (String)request.getParameter("id");
if (pid == null)  pid = "unknown";

String loadMsg="";
String emsg = (String)session.getAttribute("upload");
if (emsg!=null && emsg.length() > 2) {
  loadMsg = "<span class='errmsg'>" + emsg + "</span>";
}
emsg = (String)session.getAttribute("uploadok");
if (emsg!=null && emsg.length() > 2) {
  loadMsg += emsg;
}
loadMsg += "<br>";
session.removeAttribute("upload");
session.removeAttribute("uploadok");

Boolean ddtRequest= (Boolean)session.getAttribute("ddtRequest");
if (ddtRequest==null) ddtRequest=false;
String editProfile = CPS.getEditProfile(ddtRequest);

%>
<%@ include file="cps_hdr.html" %>

   <script type="text/javascript">
      var myForm;
      var pid = "<%= pid %>";
      var loadmsg = "<%=loadMsg%>";
      var pdfarr= new Array();
      var pdfarrDisabled= new Array();
      var TypeCol=1,PDFCol=2,DateCol=3;

    function download_pdf(thelink) {
        disable_links();
        window.setTimeout(function(){
          enable_links(1);},100);
        window.location=thelink;
         
    }

     function doLink(name, value) {
         var thelink = "/cps-app/cps_loadprop?page=PDF&pid=" + pid + "&type=" + name; 
         var str = "<a style='font-weight:normal;text-decoration:underline;' href='javascript:download_pdf(\"" + thelink + "\")'>" + value + "</a>";
         //var str = "<a href='/cps-app/cps_loadprop?page=PDF&pid=" + pid + "&type=" + name + "'>" + value + "</a>";
         return str;
      }

   function before_change(id,oldval,newval) 
   {
     var retval=true;
     if (myForm.getItemValue("uval") == "1")
       retval = false;
     if (retval && id=="upload_type") {
        var obj = myForm.getContainer("filesize");
        var lbl= myForm.getItemLabel(id,oldval);
        obj.innerHTML="";
        if (oldval=="sj") obj.innerHTML += lbl + " maximum size is 10 Mb";
        if (oldval=="pc") obj.innerHTML += lbl + " maximum size is 1 Mb";
        if (oldval=="team") obj.innerHTML += lbl + " maximum size is 4 Mb";
     }
     return retval;
   }

     function handle_change(id,value ) 
     {
        if (id == "upload_type") {
           clear_status();
           var obj = myForm.getInput("uploadFile");
           obj.value="";
           myForm.showItem('upblk');
        }
  
        if (id == "uploadFile") {
          var myfile = myForm.getItemValue("upload_type");
          var fsize = 10000000;
          if (myfile == "team")
            fsize = 4000000;
          if (myfile == "pc") 
            fsize = 1000000;
          validate_file("uploadFile",fsize);
        }
      }

      function validate_file(id,fsize) {
        clear_status();
        var retval = true;
        var msg = "";
        var fsizem = "";
        try  {
           fsizem= fsize/1000000;
        } catch (e) {

        }
        var elem = myForm.getInput(id);
        if (elem && elem.files[0]) {
          var file = elem.files[0];
             
          if (file.size > fsize) {
            var str = get_status();
            var ext ="s";
            if (fsizem <= 1) ext="";
            str += "<span class='errmsg'>"  + elem.files[0].name + ": File exceeds maximum limit of " + fsizem +"Mbyte" + ext + "</span><br>";
            set_status(str);
            enable_links(1);
            set_errlbl(true,myForm,id);
          }
          else if (window.FileReader) {
            var reader = new FileReader();
            var obj = myForm.getInput("uploadFile");
            reader.onerror = function(e){
              set_status("<span class='errmsg'>"  + "Error occurred accessing " + elem.files[0].name + ". Please check your file permissions.</span><br>");
               set_errlbl(true,myForm,id);
               obj.value="";
               console.log(reader.error)
               enable_links(1);
            }
            reader.onabort = function(e){
              var str = get_status();
              set_status(str + "<span class='errmsg'>"  + "Error occurred accessing " + elem.files[0].name + ". Please check your file permissions</span><br>");
              set_errlbl(true,myForm,id);
               obj.value="";
               console.log(reader.error)
               enable_links(1);
            }
            reader.onload = function(e){
              if (myForm.getItemValue("uval") != "1") {
                save_changes("SAVE");
              } else  {
                set_errstatus("Upload already in progress.");
              }
            };
            reader.readAsText(file);
          }
        }
        return retval;
      }


      function handle_error(name,value,res) {
         var lb=myForm.getItemLabel(name);
         var stslb = myForm.getItemLabel("status");
         if (stslb.substr(0,21) == "Invalid input- Please")
         {
            var mysep = ", "; 
            var lastbr = stslb.lastIndexOf("<br>");
            if (stslb.length-lastbr > 70) mysep = ", <br>"; 
            set_status(stslb+mysep+lb);
         }
         else 
         {
            set_status("Invalid input- Please check " + lb);
         }
      } 

  function postLoad() {
    var pno = myForm.getItemValue("propno");
    var emsg=myForm.getItemValue("emsg");

    if (emsg != null && emsg.length > 2) {
      if (pno.indexOf("Invalid") >= 0)  {
        doAlertLogoff(emsg);
        return;
      } else {
        doAlert(emsg,badalertReturn);
      }
      return;
    }

    var isEdit=myForm.getItemValue("isEdit");
    set_editButtons(myForm,isEdit);
    if (isEdit == "false" ) {
      myForm.hideItem("uploadfs");
      // myForm.hideItem("profileblk");
      myForm.setItemLabel("gridCaption",pno + " : Supporting Files " + myForm.getItemValue("proposal_title"));

      var instr= document.getElementById("upinstr");
      instr.style.display="none";
      mygrid.setColumnHidden(0,true);
    } else {
      myForm.setItemLabel("gridCaption",pno + " : Supporting Files " );
      mygrid.setColumnHidden(0,false);
    }

    cps_style_load();
    myForm.setItemLabel("uploadfs",myForm.getItemValue("propno") + ": Uploads " + myForm.getItemValue("proposal_title"));

    set_status(loadmsg);

    gridLoad();
  }

  function gridLoad()
  {
    mygrid.clearAll();
    mygrid.load("/cps-app/cps_loadprop?page=UPLOADGRID&pid="+pid,gridpostLoad,"json");

    setChanges(0);
  }

  function enable_links(dogrid)
  {
    if (dogrid) {
       gridpostLoad();
    }
    var isEdit=myForm.getItemValue("isEdit");
    if (isEdit=="true")  {
      myForm.showItem("uploadfs");
    }
  }



  function disable_links()
  {
    // the sj,pc,team link
    for (var ii=0;ii<mygrid.getRowsNum();ii++) {
      mygrid.cells(mygrid.getRowId(ii),PDFCol).setValue(pdfarrDisabled[ii].toUpperCase());
    }
    // hide the input 
    // can't enable/disable items because submit gets null value and
    // we disable before submitting, sigh
    myForm.hideItem("uploadfs");
  }


  function gridpostLoad(flg)
  {
    pdfarrDisabled =[];
    pdfarr=[];
    if (mygrid.getRowsNum() < 1) {
      // should never happen
      mygrid.addRow("0",["No files uploaded","",""]);
    }
    else {
      for (var ii=0;ii<mygrid.getRowsNum();ii++) {
        var up_type = mygrid.getRowId(ii);
        var dd = mygrid.cells(mygrid.getRowId(ii),DateCol).getValue();
        var doteam = mygrid.cells(mygrid.getRowId(ii),PDFCol).getValue();
        if (dd != null && dd.length > 1 && dd.indexOf("No upload")< 0) {
          mygrid.cells(up_type,0).setDisabled(false);
          if (up_type.toUpperCase() =="TEAM" && doteam.indexOf("PDF") < 0) {
             //mygrid.cellByIndex(ii,1).setValue("");
             pdfarr.push(doteam);
             pdfarrDisabled.push(doteam);
          } else {
            var up_type_link = (up_type.indexOf("team") >= 0) ? "te" : up_type;
            var ll= doLink(up_type,up_type_link.toUpperCase() + " PDF");
            mygrid.cells(mygrid.getRowId(ii),PDFCol).setValue(ll);
            pdfarr.push(ll);
            pdfarrDisabled.push(up_type.toUpperCase() + " PDF");
          }
        }
        else {
          //mygrid.cells(up_type,0).setDisabled(true);
          mygrid.setCellExcellType(mygrid.getRowId(ii),0,"ro");
          mygrid.cellByIndex(ii,0).setValue("");
          pdfarrDisabled.push("");
        }
      }

      var isEdit=myForm.getItemValue("isEdit");
      enable_links(0);
    }
    mygrid.setSizes();
  }

  function confirmRemovePDF(result)
  {
    var rowId = myForm.getUserData("operation","pdfRow");
    if (result) {
      set_status("");
      disable_links();
      document.body.className = "waiting";
      if (rowId.indexOf("team") >= 0) {
          // db Proposal code and syabse require type be cv, not team
          myForm.setItemValue("utype","cv");
      }
      else {
          myForm.setItemValue("utype", rowId);
      }
      myForm.setItemValue("operation","DEMOTE");
      myForm.setItemValue("page","UPLOAD");
      myForm.send("/cps-app/cps_saveprop","post",function(name,retstr) {
        document.body.className = "";
        enable_links();
        if (process_save(retstr,false)) {
          gridLoad();
        }
      });
     }
     else {
       set_status("Request cancelled.<p>");
     }
     mygrid.cells(rowId,0).setValue(0);
     mygrid.cells(rowId,0).setDisabled(false);
  }

      function loadPage() {
         myForm.load("/cps-app/cps_loadprop?page=UPLOAD&pid="+pid,postLoad);
         clear_status();
      }


      function save_changes(id) {
           
        myForm.setItemValue("operation",id);
        var type = myForm.getItemValue("upload_type").toUpperCase();
        document.body.className="waiting";
        disable_links();
        myForm.setItemValue("uval","1");
        myForm.hideItem("upblk");
        set_status("<span class='progress'><br>Upload of " + type + " in progress.</span> Please <span class='reqLbl'>STAY</span> on this page until the upload is complete.</span>");
        document.getElementById("uploadForm").submit();
      }


      function doOnLoad() {

         var formData = [
           {type:"input",hidden:true,name:"operation",value:""},
           {type:"input",hidden:true,name:"page",value:"UPLOAD"},
           {type:"input",hidden:true,name:"pid",value:"<%=pid%>"},
           {type:"input",hidden:true,name:"propno"},
           {type:"input",hidden:true,name:"proposal_title"},
           {type:"input",hidden:true,name:"emsg",value:""},
           {type:"input",hidden:true,name:"uval",value:"0"},
           {type:"input",hidden:true,name:"utype",value:""},
           {type:"input",hidden:true,name:"isEdit",value:"false"},
           {type:"input",hidden:true,name:"isDDT",value:"false"},
           {type:"input",hidden:true,name:"isBPP",value:"false"},
           {type:"input",hidden:true,name:"team_upload", value:null },
           {type:"fieldset",name:"uploadfs",label:"Uploads",list:[
             {type:"block",list:[
               {type:"container",name:"uplbl",label:"<a href='javascript:openHelpWindow(\"#sjupload\")'>Specify type of file to Upload</a>:"},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"upload_type",position:"label-right",label:"Science Justification",value:"sj",className:'reqLbl'},
               {type:"newcolumn",offset:20},
               {type:"radio",name:"upload_type",label:"Team Expertise", position:"label-right",value:"team"},
             ]},

             {type:"block",name:"upblk",offsetLeft:40,offsetTop:20,list:[
               {type:"file",id:"uploadFile",name:"uploadFile",label:"File: "},
               {type:"newcolumn",offset:10},
               {type:"container",name:"filesize",className:"sizeContainer"},
             ]},
           ]},
           {type:"container",name:"status",className:"statusContainer"},
           {type:"fieldset",offsetTop:10,name:"gridCaption",label:"Supporting Files",list:[
              {type:"label",label:"<div id='grid' style='clear:both;margin-left:20px;width:90%;'></div>",offsetTop:0},
               {type:"container",name:"grid",className:"sizeContainer"},
            ]}
           



        ];

         myForm = new dhtmlXForm("formB_container",formData);
         //cps_style_init();

         myForm.setSkin('dhx_skyblue');

         myForm.hideItem("upblk");
         myForm.attachEvent("onChange", handle_change);
         myForm.attachEvent("onBeforeChange", before_change);
         buildTooltips(myForm);
              
         mygrid=new dhtmlXGridObject('grid');
         mygrid.setHeader("Remove?,Type,View PDF,Upload Date");
         mygrid.setColumnMinWidth("20,20,20,20");
         mygrid.setInitWidths("70,175,120,150");
         mygrid.setColAlign("center,left,left,left");
         mygrid.setSkin("dhx_skyblue");
         mygrid.setColTypes("ch,ro,ro,ro");
         mygrid.setColSorting("na,na,na,na");
         mygrid.setImagePath(CSS_IMG_PATH);
         mygrid.enableAutoHeight(true);
         mygrid.enableAutoWidth(true);
         mygrid.enableMultiline(true);
         mygrid.enableRowsHover(true,"gridhover");
         mygrid.init();
           
         mygrid.attachEvent("onBeforeSelect",function(rowid,oldrow,newcol) {
            return false;
         });
         mygrid.attachEvent("onCheck",function(rowId,cellInd,state) {
            if (state) {
                mygrid.cells(rowId,cellInd).setDisabled(true);
                myForm.setUserData("operation","pdfRow",rowId);
                var confirmTxt = "Are you sure you want to remove the " + 
			mygrid.cells(rowId,TypeCol).getValue() + " file ?";
                doConfirm(confirmTxt,confirmRemovePDF);

            } else {
               myForm.setUserData("operation","pdfRow",0);
               mygrid.cells(rowId,cellInd).setDisabled(false);
            }
         });



         window.dhx.attachEvent("onLoadXMLError", function(obj) {
               doAlert("Unexpected error",alertReturn);
         });
         window.dhx.attachEvent("onAjaxError", function(obj) {
               doAlert("Unexpected Ajax error",alertReturn);
         });

         loadPage();

      }   

   </script>
</head>
 
<body onload="showTimeoutWarn(false);doOnLoad()">
  <div id="msgbody" class="msgbody">
    <img src="/cps-app/imgs/ajax-loader.gif" alt="Loading...">
  </div>
  <div id="pagebody" class="pagebody">

<form id="uploadForm"  enctype="multipart/form-data" method="POST" action="/cps-app/prop_upload">
   <div class="instrLbl" ><div id="upinstr" class="instrTxt"> <ul style="padding-left:5px;margin:0!important">
See the <a href="/proposer/"  target='_parent' >Chandra CfP</a> for requirements and size restrictions.
</div></div>
   <div id="formB_container" style="width:95%;float:left;clear:both;padding-bottom:40px;" ></div> 
   
   <!--<div id='grid' style="clear:both;margin-left:20px;width:90%;"></div> -->
</form>
</div>
</body>
</html>
