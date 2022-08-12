<%@ page session="true" import="java.text.* , java.util.* , info.*" %>
<% 
  PropTargetList tgtList = (PropTargetList)session.getAttribute("tgtList");
  DDTEntry theDDT = (DDTEntry)session.getAttribute("ddtEntry");
  String rpsFilename = (String)session.getAttribute("rpsFilename");
  String sjFilename = (String)session.getAttribute("sjFilename");
  String propFilename = (String)session.getAttribute("propFilename");
  String pocStr = (String)session.getAttribute("mpPOC");
  String message = (String)session.getAttribute("ddtmessage");
  String msgclass = (String)session.getAttribute("ddtmsgclass");
  Boolean isDDTManager = (Boolean)session.getAttribute("isDDTManager");
  Boolean isDDTReadOnly = (Boolean)session.getAttribute("isDDTReadOnly");
  Boolean isDDTConflict = (Boolean)session.getAttribute("isDDTConflict");
  Boolean isDDTMigrate = (Boolean)session.getAttribute("isDDTMigrate");
  String disabled = "";
  String disabledfp = "";
  String selectClass = "class='update'";
  String readonly = " class=\"edit\" ";
  String readonlyfp = " class=\"editl\" ";
  String ccemail = (String)session.getAttribute("ccemail");
  String managerRole = "";
  String displayComments = (String)session.getAttribute("displayComments");
  String subbtn = "class='subbtn' ";


  if (displayComments == null)
    displayComments = "on";

  String fieldsetCmts;
  String fieldsetCmtsArrow;
  if (displayComments.equals("on"))  {
    fieldsetCmts = "<fieldset id=\"fieldset_Comments\" style=\"display:block;border:0\">";
    fieldsetCmtsArrow = "<img  src=\"small_tri_down.gif\" name=\"DispComments\" alt=\"on\" onclick=\"return showHide(document.DDTManager,this,'fieldset_Comments','displayComments');\" > <input type=\"hidden\" value=\"on\" name=\"displayComments\">";
  }
  else {
    fieldsetCmts = "<fieldset id=\"fieldset_Comments\" style=\"display:none;border:0\">";
    fieldsetCmtsArrow = "<img  src=\"small_tri_right.gif\" name=\"DispComments\" alt=\"off\" onclick=\"return showHide(document.DDTManager,this,'fieldset_Comments','displayComments');\" > <input type=\"hidden\" value=\"off\" name=\"displayComments\">";
  }
  message = message.replaceAll("\n","<br>");


  String editStr = "";
  String rpsLink = "&nbsp;";
  String sjLink = "&nbsp;";
  if (rpsFilename != null && rpsFilename.length() > 0) {
      rpsLink = "<a href=\"displayFile.jsp?type=rpsFilename\">View Proposal PDF</a>";
  }
  if (sjFilename != null && sjFilename.length() > 0) {
      sjLink = " <a href=\"displayFile.jsp?type=sjFilename\">Science Justification</a>";
  }
  String conflictLink = "&nbsp;";
  if (isDDTManager.booleanValue() == false) {
    managerRole = "disabled";
    subbtn="";
  }
  else if (isDDTConflict.booleanValue() == true) {
    conflictLink = "<a href=\"ddtPropconflict?proposalID=" + theDDT.getProposalID() + "\" target=\"_blank\" >View Proposal Conflicts</a>";

  }


  response.addCookie(new Cookie("JSESSIONID", session.getId()));
%>
<%@ include file = "ddtManagerHead.jsp" %>
<body class="body" >
<script type="text/javascript" src="toomanager.js"></script>
<table width="100%" cellspacing="0" cellpadding="0">
<tr>
<td><img src="header_left.gif" alt="Chandra Science">
</td>
<td class="title">Chandra DDT Proposal Update Request</td>
<td>&nbsp;</td>
</tr>
<tr>
<td align="left"><a href="/toomanager/ddtManager.jsp?operation=return">Back to Chandra DDT Manager</a></td>
<td class="subtitle"> <%=theDDT.getProposalType()%> Proposal Number <%= theDDT.getProposalNumber() %> 
</td>
<td align="right"><a href="/toomanager/logout">Logout</a></td>
</tr>
</table>
<p><font class="<%= msgclass %>"> <%= message %></font></p>
<% if (theDDT != null) {
     String observer = new String();
     if (theDDT.getObserver() != null) {
       observer  = theDDT.getObserver();
     }
    String observerEmail = new String();
    if (theDDT.getObserverEmail() != null) {
        observerEmail  = theDDT.getObserverEmail();
    }
     Integer ocat_propid = theDDT.getOCatID();
     String ocatLink = "&nbsp;"; 
     if (ocat_propid.intValue()> 0) {
       ocatLink="<a href=\"ddtOCat.jsp?prop=" ;
       ocatLink+= theDDT.getProposalNumber() + "\">View ObsCat Data</a>";
     }
     if (theDDT.inObsCat() || isDDTReadOnly.booleanValue() == true ||
	isDDTManager.booleanValue() == false) {
         
       disabled = "disabled";
       disabledfp = "disabled";
       readonly = "readonly";
       readonlyfp = "readonly";
       selectClass="";
     }
        
%>
<form name="DDTManager" method="post" action="/toomanager/ddtUpdate.jsp" onsubmit="return verifyDDTSubmit(this);" >
<input type="hidden" name="proposalID" value="<%= theDDT.getProposalID() %>" >
<input type="hidden" name="requestedTime" value="<%= theDDT.getRequestedTime() %>" >
<input type="hidden" name="operation" value="Save" >
<input type="image" name="Submit" value="<%=TriggerTooConstants.SAVE%>" src="/toomanager/blank.gif" alt=" " onclick="return(false);">
<div class="infobar">
<table width="100%">
<tr>
<td><b>P.I.:</b> <%= theDDT.getPI() %> <%= theDDT.getPIEmail() %> </td>
<%--<td><b>P.I.:</b> <%= theDDT.getPI() %></td>--%>
<td class="right"><a href="displayDDTProposal.jsp">View&nbsp;Proposal</a>
</tr>
<tr>
<td><b>Observer:</b> <%= observer %> <%= observerEmail %></td>
<%--<td><b>Observer:</b> <%= observer %></td>--%>
<td class="right"><%=rpsLink%><%=sjLink%></td>
</tr>
<tr >
<td rowspan="2"><b>Title:</b> <%= theDDT.getProposalTitle() %></td>
<td class="right"><%=conflictLink%></td>
</tr>
<tr>
<td class="right"><%=ocatLink %></td>
</tr>
</table>
</div>
<p>

<% if (tgtList != null && tgtList.size() > 0) {
%>
<font class="bigLabel"><a href="displayDDTProposal.jsp">Target Information</a></font>
<table cellpadding="1" cellspacing="0" border="1" >
<tbody><tr>
<th class="hdr">Tgt</th>
<th class="hdr"><%= TriggerTooConstants.TARGETNAME %></th>
<th class="hdr"><%= TriggerTooConstants.RA %></th>
<th class="hdr"><%= TriggerTooConstants.DEC %></th>
<th class="hdr">Req. Time</th>
<th class="hdr">App. Time</th>
<th class="hdr"><%= TriggerTooConstants.INSTRUMENT %></th>
<th class="hdr"><%= TriggerTooConstants.GRATING %></th>
<th class="hdr"><%= TriggerTooConstants.RESPONSESTART %></th>
<th class="hdr"><%= TriggerTooConstants.RESPONSESTOP %></th>
<th class="hdr"><%= TriggerTooConstants.STATUS %> </th>
<th class="hdr"><%= TriggerTooConstants.FASTPROCSTATUS %> </th>
<th class="hdr"><%= TriggerTooConstants.FASTPROCCOMMENT %> </th>
</tr>

<% 
   NumberFormat nf = NumberFormat.getInstance();
   nf.setMaximumFractionDigits(2);
   nf.setMinimumFractionDigits(2);
   nf.setGroupingUsed(false);
   for (int ii=0;ii<tgtList.size();ii++) {
     PropTarget tgt = (PropTarget) tgtList.get(ii);
     Vector fupList= tgt.getFollowups();
     Double dval;
     String fupSizeName = tgt.getTargID().toString();
     fupSizeName += "-fupSize";
     String onchange = "onchange=\"statusChanged(this,0);sumFups(this);\"";

     dval = tgt.getExpTime();
     String expTime = nf.format(dval);
     String appTime = "";
     String atime = "";
     dval = tgt.getApprovedTime();
     if (dval.doubleValue() >= 0) { 
        atime = nf.format(dval);
     }
     String fpName = tgt.getTargID().toString();
     fpName += "-fastproc";
     String fpcName = tgt.getTargID().toString();
     fpcName += "-fastprocCmt";
        
     String expFldName = tgt.getTargID().toString();
     expFldName += "-expTime";
     String startFldName = tgt.getTargID().toString();
     startFldName += "-cxcstart";
     String stopFldName = tgt.getTargID().toString();
     stopFldName += "-cxcstop";
     String fldName = tgt.getTargID().toString();
     fldName += "-appTime";
     Integer fupSize = new Integer(0);
     if (fupList == null || fupList.size()==0) {
        appTime = "<input type=\"text\" name=\"" + fldName+ "\" size=\"8\" value=\"" + atime+ "\" onchange=\"return(isNumber(this)); \" " + readonly + "  >";
     }
     else  {
        fupSize = new Integer(fupList.size());
        appTime = "<input type=\"text\" name=\"" + fldName+ "\" size=\"8\" value=\"" + atime+ "\" readonly>";
     }
     

     String targetName = new String("&nbsp;");
     if (tgt.getTargetName() != null) {
       targetName = tgt.getTargetName();
     }



     String statOptions="";
     if (tgt.getStatus().equalsIgnoreCase("rejected")) {
        statOptions="<option value=\"\" >&nbsp;";
        statOptions+="<option value=\"accepted\" >Accept";
        statOptions+="<option value=\"rejected\" selected >Reject";
     } else if (tgt.getStatus().equalsIgnoreCase("accepted")) {
        statOptions="<option value=\"\" >&nbsp;";
        statOptions+="<option value=\"accepted\" selected>Accept";
        statOptions+="<option value=\"rejected\" >Reject";
     } 
     else {
        statOptions="<option value=\"\" selected>&nbsp;";
        statOptions+="<option value=\"accepted\" >Accept";
        statOptions+="<option value=\"rejected\" >Reject";
     }
     String cxcStart = "&nbsp;";
     String cxcStop = "&nbsp;";
     if (tgt.getResponseStart().doubleValue() >= 0) {
       cxcStart = "<input type=\"text\" name=\"" + startFldName + 
           "\" size=\"6\" value=\"" +
           tgt.getResponseStart() + "\"" + readonly + " onchange=\"return(isNumber(this));\" >";
     }
     if (tgt.getResponseStop().doubleValue() >= 0) {
       cxcStop = "<input type=\"text\" name=\"" + stopFldName + 
           "\" size=\"6\" value=\"" +
           tgt.getResponseStop() + "\"" + readonly + " onchange=\"return(isNumber(this));\" >";
     }
     String fopt = new String();
     fopt += "<option value=\"\">No";
     fopt += "<option value=\"approved\" ";
     if (tgt.getFastProcStatus().equals("approved"))
       fopt += "selected";
     fopt += ">Yes";
    
     // has to have too entry to set fast processing
     String fpedisabled = disabledfp;
     String fpereadonly = readonlyfp; 
     if (cxcStart.indexOf("nbsp") > 0 ||
         theDDT.getUrgency().equals(TriggerTooConstants.NONTRANS) ||
         !tgt.getStatus().equalsIgnoreCase("accepted")) {
       fpedisabled = "disabled";
       fpereadonly = "readonly";
     }
       

%>
<tr class="alt1" >
<td class="list"><%= tgt.getTargetNumber() %></td>
<td class="list"><%= targetName %></td>
<td class="list" nowrap><%= tgt.getRAString() %></td>
<td class="list" nowrap><%= tgt.getDecString() %></td>
<td class="list"><%= expTime %>
<input type="hidden" name="<%= expFldName %>" value="<%= expTime%>">
<input type="hidden" name="<%= fupSizeName %>" value="<%= fupSize.toString()%>">
</td>
<td class="list"><%= appTime %></td>
<td class="list"><%= tgt.getInstrument() %></td>
<td class="list"><%= tgt.getGrating() %></td>
<td class="list"><%= cxcStart %></td>
<td class="list"><%= cxcStop %></td>
<td class="list"><select <%=selectClass%> name="<%= tgt.getTargID().toString() %>" <%= onchange %> <%= disabled %> >
<%= statOptions %>
</select></td>
<td><select name="<%=fpName%>" <%=fpedisabled%> > <%=fopt%> </select</td>
<td> <input name="<%=fpcName%>" <%=fpereadonly%> type="text" maxLength=255 size="30" value="<%=tgt.getFastProcComment()%>"></td>
</tr>
<%   // Any followups for the current set of targets?
  if (fupList != null && fupList.size()> 0) {

%>
<tr class="fups" >
<td colspan="11" align="right">
<table border="1" class="fups" >
<tr align="right"> 
<th class="hdr">Observation</th>
<th class="hdr">Exp.Time</th>
<th class="hdr">Min. Interval</th>
<th class="hdr">Max. Interval</th>
<th class="hdr">TargNum</th>
<th class="hdr">Status</th>
</tr>

<%
     for (int ff=0;ff<fupList.size();ff++) {
        DDTFollowup fup = (DDTFollowup)fupList.get(ff);
        String fupOptions = new String("");
	String fupKey= fup.getTargid().toString() + "-";
	fupKey += fup.getOrdr().toString() ;
        String fupOnchange = " onchange=\"sumFups2(this);\""; 
        String tnum = "&nbsp;";
        if (fup.getTargetNumber() > 0) 
          tnum = fup.getTargetNumber().toString();


        if (fup.getStatus().equalsIgnoreCase("rejected")) {
           fupOptions="<option value=\"\" >&nbsp;";
           fupOptions+="<option value=\"accepted\" >Accept";
           fupOptions+="<option value=\"rejected\" selected >Reject";
        } else if (fup.getStatus().equalsIgnoreCase("accepted")) {
           fupOptions="<option value=\"\" >&nbsp;";
           fupOptions+="<option value=\"accepted\" selected>Accept";
           fupOptions+="<option value=\"rejected\" >Reject";
        } else {
           fupOptions="<option value=\"\" selected>&nbsp;";
           fupOptions+="<option value=\"accepted\" >Accept";
           fupOptions+="<option value=\"rejected\" >Reject";
        } 
        dval = fup.getExpTime();
        if (dval.doubleValue() >= 0) { 
           atime = nf.format(dval);
        }
        else {
           atime = "";
        }
        
        String fupAppTime = "";
        String fupExpTime = "";
        String fupFldName = fupKey + "-appTime" ;
        String fupExpFldName = fupKey + "-expTime" ;
        String monDisabled="";
        
        
        fupAppTime = "<input type=\"text\" name=\"" + fupFldName + "\" size=\"8\" value=\"";
        fupAppTime += atime + "\" onchange=\"isNumber(this);sumFups2(this);\" " + readonly +  " >";
        fupExpTime = "<input type=\"hidden\" name=\"" + fupExpFldName + "\" size=\"8\" value=\"";
        fupExpTime += atime + "\" >";
    
        String fupMinLead = "" ;
        String fupMaxLead = "" ;
        fupMinLead= nf.format(fup.getMinLead());
        fupMaxLead= nf.format(fup.getMaxLead());
        if (tgt.getMonitorFlag() != null && 
	   (tgt.getMonitorFlag().equals("Y") || tgt.getMonitorFlag().equals("P") ) && 
            fup.getOrdr().intValue() == 1) {
           monDisabled=" disabled "; 
         }
%>
<% if (fup.getOrdr().intValue() == 1  && tgt.getInitialTime().doubleValue() >= 0) { %>
<tr>
<td class="list">Trigger</td>
<td class="list">
<input type="text" name="<%=tgt.getTargID().toString()%>-initTime" size="8" value="<%=tgt.getInitialTime().toString()%>" <%= readonly%>  onchange="if (isNumber(this)) { sumFups2(this); return true;} else { return false;} "  >
<input type="hidden" name="<%=tgt.getTargID().toString()%>-initExpTime" size="8" value="<%=tgt.getInitialTime().toString()%>" >
</td>
<td class="empty">&nbsp;</td>
<td class="empty">&nbsp;</td>
<td class="empty">&nbsp;</td>
<td class="empty">&nbsp;</td>
</tr>
<% } %>
<tr>
<td class="list"><%= fup.getOrdr()%></td>
<td class="list"><%=fupAppTime%>
<%= fupExpTime %> </td>
<td class="list"><input type="text" name="<%=fupKey%>-minLead" size=6 value="<%=fupMinLead%>" <%=monDisabled%> <%=readonly%> onchange="return(isNumber(this));" ></td>
<td class="list"><input type="text" name="<%=fupKey%>-maxLead" size=6 value="<%=fupMaxLead%>" <%=monDisabled%>  <%= readonly%> onchange="return(isNumber(this));" ></td>
<td class="list"><%=tnum%></td>
<td class="list"><select <%=selectClass%> name="<%= fupKey %>" <%= fupOnchange%> <%= disabled %> >
<%= fupOptions %>
</select></td>
</tr>
<% } %>
</table>
</td>
<td colspan="2">&nbsp;</td>
</tr>


<%
  }
  if (tgt.getGridRadius().doubleValue() > 0) {
    String gridLabel = tgt.getTargID().toString();
    gridLabel += "-grid";
    String gridHLabel = gridLabel + "Hidden";
    Integer gridReq = tgt.getGridPointings();
    Integer gridPnt = tgt.getGridApproved();
    if (tgt.getGridApproved().intValue() < 0) {
        gridPnt=tgt.getGridPointings();
    }
%>
<tr>
<td colspan="11" align="right">
<table width="100%" border="0" >
<tr>
<td width="20%" class="grid" >&nbsp;</td>
<td><b>Grid Name :</b>&nbsp;<%=tgt.getGridName()%>
</td>
<td class="right"><b>Number of Pointings :</b>&nbsp;
<input type="text" name=<%=gridLabel%> size=3 value=<%=gridPnt.toString() %> <%=readonly%> >
<input type="hidden" name=<%=gridHLabel%> size=3 value=<%=gridReq.toString() %>>
</td>
<td class="right"><b>Radius :</b>&nbsp;<%=tgt.getGridRadius().toString()%>
</td>
</tr>
</table>
</td>
<td colspan="2">&nbsp;</td>
</tr>
<% } %>
<%  } // end of target list %>

</tbody>
</table>
<% }  %>


<p>
<%
if (isDDTReadOnly.booleanValue() == false) {
%>
<font class="bigLabel">Comments</font>&nbsp;&nbsp;
<input type="submit" class="subbtn" name="Submit" value="<%= TriggerTooConstants.SAVEDRAFT %>" onclick="this.form.operation.value='<%= TriggerTooConstants.SAVEDRAFT%>';" >
<br>
<textarea class="editl" name="<%= TriggerTooConstants.COMMENT %>" rows="8" cols="100">
<%= theDDT.getComment() %>
</textarea>
</p>
<% } %>
<%= fieldsetCmtsArrow %> <b>Comments History:</b>
<%= fieldsetCmts %>
<legend>&nbsp;</legend>
<%  Vector cmtlist = theDDT.getCommentHistoryList();
    for (int ii=0;ii<cmtlist.size();ii++) {
       CommentHistory cmt = (CommentHistory)cmtlist.get(ii);
       String statusStr = "";
       if (cmt.getStatus() != null && cmt.getStatus().equalsIgnoreCase(TriggerTooConstants.DRAFTSTATUS)) {
           statusStr = "Status: " + cmt.getStatus();
       }
%>
<div class="cmtspanbar">
<table cellspacing="10">
<tr>
<td class="bolder" >
<%= cmt.getCreationDate() %></td>
<td class="bolder">Author:<%= cmt.getUserName() %></td>
<td><%= statusStr %></td>
</tr>
</table>
<%= cmt.getCommentHTML() %>
</div>
<% } %>
<p>
</fieldset>


<%
  String sopt = new String();
  Vector ss = new Vector();
  ss.add(new String(TriggerTooConstants.PENDING));
  ss.add(new String(TriggerTooConstants.ACKNOWLEDGED));
  ss.add(new String(TriggerTooConstants.APPROVED));
  ss.add(new String(TriggerTooConstants.NOTAPPROVED));
  ss.add(new String(TriggerTooConstants.WITHDRAWN));

  for (int ii=0; ii < ss.size(); ii++) {
    sopt += "<option";
    if ( theDDT.getStatus().equals(ss.get(ii))) {
       sopt += " selected";
    }
    sopt += ">";
    sopt += ss.get(ii);
  }

  String dopt = new String();
  Vector dw = new Vector();
  dw.add(new String("N"));
  dw.add(new String("D"));
  for (int ii=0; ii < dw.size(); ii++) {
    dopt += "<option";
    if  (theDDT.getDataRights().indexOf((String)dw.get(ii)) > -1 ) {
       dopt += " selected";
    }
    dopt += ">";
    dopt += dw.get(ii);
  }

  String ropt = new String();
  Vector rw = new Vector();
  rw.add(new String(TriggerTooConstants.NONTRANS));
  rw.add(new String(TriggerTooConstants.SLOW));
  rw.add(new String(TriggerTooConstants.MEDIUM));
  rw.add(new String(TriggerTooConstants.FAST));
  for (int ii=0; ii < rw.size(); ii++) {
    ropt += "<option";
    if  (theDDT.getUrgency().indexOf((String)rw.get(ii)) > -1 ) {
       ropt += " selected";
    }
    ropt += ">";
    ropt += rw.get(ii);
  }
  String oldUrg = new String("");
  if ((theDDT.getUrgency().compareToIgnoreCase(TriggerTooConstants.SLOW) != 0) &&
     (theDDT.getUrgency().compareToIgnoreCase(TriggerTooConstants.NONTRANS) != 0) &&
     (theDDT.getUrgency().compareToIgnoreCase(TriggerTooConstants.MEDIUM) != 0) &&
     (theDDT.getUrgency().compareToIgnoreCase(TriggerTooConstants.FAST) != 0 )) {
     oldUrg =  "&nbsp;&nbsp;Original Urgency: " + theDDT.getUrgency();
  }
%>
<table>
<tr>
<th class="bigLabel">Paging Type:</th>
<td> <select <%=selectClass%> name="<%= TriggerTooConstants.RESPONSEWINDOW%>" <%=disabled%> >  
<%= ropt %> </select>
<%= oldUrg %>
</td></tr>
<tr>
<th class="bigLabel">Status:</th>
<td><select <%=selectClass%> name="<%= TriggerTooConstants.TOOSTATUS %>" <%=disabled%> onchange="return(ddtStatusCheck(this));">
<%= sopt %> </select>
</td>
</tr>
<tr>
<th class="big2Label"><%=TriggerTooConstants.DATARIGHTS%>:</th>
<td> <select <%=selectClass%> name="<%= TriggerTooConstants.DATARIGHTS%>" <%=disabled%> >  
<%= dopt %> </select>
</td></tr>
<tr>
<th class="big2Label"><%=TriggerTooConstants.CCEMAIL%>:</th>
<td> <input type="text" name="<%= TriggerTooConstants.CCEMAIL%>" size="50"
value="<%=ccemail%>" class="editl" >  </td>
</tr>
</table>
<hr>
<%
if ( isDDTReadOnly.booleanValue()==false) {
%>
<center>
<table class="btn">
<tr valign="top">
<% if (!theDDT.inObsCat()) {
%>
<td class="center">
<input type="submit" <%=subbtn%> name="Submit" value="<%= TriggerTooConstants.SAVE%>" <%=managerRole %> onclick="this.form.operation.value='<%= TriggerTooConstants.SAVE%>'; " >
<br><font size="-1">(save all updates<br>no email sent)</font></td>
<% } else { %>
<td class="center">
<input type="submit" <%=subbtn%> name="Submit" value="<%= TriggerTooConstants.SAVECMT%>" <%=managerRole %> onclick="this.form.operation.value='<%= TriggerTooConstants.SAVECMT%>'; " >
<br><font size="-1">(save comment<br>no email sent)</font></td>
<% } %>

<td class="center">
<input type="submit" class="subbtn" name="Submit" value="<%= TriggerTooConstants.SENDCMT %>" onclick="this.form.operation.value='<%= TriggerTooConstants.SENDCMT%>';" >
<br><font size="-1">(no page)</font></td>
<% 
   String btnOK = managerRole;
   if (theDDT.inObsCat()) {
       btnOK="disabled";
       subbtn="";
   }
%>
<td class="center">
<input type="submit" <%=subbtn%> name="Submit" value="<%= TriggerTooConstants.SENDMSG%>" <%=btnOK%> onclick="this.form.operation.value='<%= TriggerTooConstants.SENDMSG%>'; " >
<br><font size="-1">(paging based on <br>response window)</font></td>
<%
   String migrateOK = "";
   subbtn = "class='subbtn' ";

   if (isDDTMigrate.booleanValue() == false ||
       theDDT.inObsCat() ||
      (theDDT.getApprovalDate() == null || 
       theDDT.getApprovalDate().length() < 1)) {
      migrateOK = "disabled";
      subbtn = "";

   }
%>
<td class="center">
<input type="submit" <%=subbtn%> name="Submit" value="<%= TriggerTooConstants.DDTUPDATE%>" <%=migrateOK%> onclick="this.form.operation.value='<%= TriggerTooConstants.DDTUPDATE%>'; " >
<br><font size="-1">(save all updates<br>no email sent)</font>
</td>
<td><input type="reset" class="subbtn" name="Reset" value="Reset"><br>&nbsp;</td>
<td><input type="submit" class="subbtn" name="Submit" value="<%= TriggerTooConstants.CANCEL%>" onclick="this.form.operation.value='<%= TriggerTooConstants.CANCEL%>';" ><br>&nbsp;</td>
</tr>
</table>
</center>
<%   } 
   } 
   else {
%>
<h3> No Observations retrieved for obsid.</h3>

<% }  %>

</form>
<p>
<%@ include file = "ddtManagerLinks.jsp" %>
</body></html>
