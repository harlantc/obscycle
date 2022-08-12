<%@ page session="true" import="java.text.* , java.util.* , info.*" %>
<% 
  PropTargetList tgtList = (PropTargetList)session.getAttribute("tgtList");
  DDTEntry theDDT = (DDTEntry)session.getAttribute("ddtEntry");
  String pocStr = (String)session.getAttribute("mpPOC");
  String message = (String)session.getAttribute("ddtmessage");
  String msgclass = (String)session.getAttribute("ddtmsgclass");
  Boolean isDDTManager = (Boolean)session.getAttribute("isDDTManager");
  Boolean isDDTReadOnly = (Boolean)session.getAttribute("isDDTReadOnly");
  Boolean isDDTMigrate = (Boolean)session.getAttribute("isDDTMigrate");
  String ccemail = (String)session.getAttribute("ccemail");
  String managerRole = "";
  if (isDDTManager.booleanValue() == false) {
    managerRole = "disabled";
  }

  String editStr = "";

  response.addCookie(new Cookie("JSESSIONID", session.getId()));
%>
<%@ include file = "ddtManagerHead.jsp" %>
<body class="body" >
<script type="text/javascript" src="toomanager.js"></script>
<table width="100%" cellspacing="0" cellpadding="0">
<tr>
<td><img src="header_left.gif" alt="Chandra Science"></td>
<td class="title">Chandra DDT ObsCat Confirmation</td>
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
     Integer ocat_propid = theDDT.getOCatID();
     String ocatLink = "&nbsp;"; 
     if (ocat_propid.intValue()> 0) {
       ocatLink="<a href=\"ddtOCat.jsp?prop=" ;
       ocatLink+= theDDT.getProposalNumber() + "\">View ObsCat Data</a>";
     }
        
%>
<form name="goto" method="post" action="/toomanager/ddtMigrateOcat"  >
<input type="hidden" name="proposalID" value="<%= theDDT.getProposalID() %>" />
<input type="hidden" name="operation" value="Save" />
<input type="image" name="Submit" value="<%=TriggerTooConstants.SAVE%>" src="/toomanager/blank.gif" alt=" " onclick="return(false);">
<div class="instructbar">
<table cellspacing="10"> 
<tr>
<th class="label">P.I.:</th>
<td class="left"><%= theDDT.getPI() %></td>
<th class="label">Title:</th>
<td class="left"><%= theDDT.getProposalTitle() %></td>
</tr>
</table>
<b>
Please verify</b> that all the target information is correct before migrating
the proposal to the ObsCat.  If you need to change the information, please select 'Cancel'.
<br>You will NOT be allowed to edit any fields in the DDT Manager
once the proposal has been migrated to the ObsCat!!!
</div>

<p>
<center>
<% if (tgtList != null && tgtList.size() > 0) {
%>
<font class="bigLabel"><a href="displayDDTProposal.jsp">Target Information</a></font>


<table class="ddtocat" border="1">
<tbody><tr>
<th class="hdr">Tgt</th>
<th class="hdr"><%= TriggerTooConstants.TARGETNAME %></th>
<th class="hdr"><%= TriggerTooConstants.RA %></th>
<th class="hdr"><%= TriggerTooConstants.DEC %></th>
<th class="hdr">Req.Time</th>
<th class="hdr">App.Time</th>
<th class="hdr"><%= TriggerTooConstants.INSTRUMENT %></th>
<th class="hdr"><%= TriggerTooConstants.GRATING %></th>
<th class="hdr"><%= TriggerTooConstants.RESPONSESTART %></th>
<th class="hdr"><%= TriggerTooConstants.RESPONSESTOP %></th>
<th class="hdr"><%= TriggerTooConstants.STATUS %></th>
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

     dval = tgt.getExpTime();
     String expTime = nf.format(dval);

     String appTime = "&nbsp;";
     dval = tgt.getApprovedTime();
     if (dval.doubleValue() >= 0) { 
        appTime = nf.format(dval);
     }
        
     Integer fupSize = new Integer(0);
     if (fupList != null) {
        fupSize = new Integer(fupList.size());
     }
     String targetName = new String("&nbsp;");
     if (tgt.getTargetName() != null) {
       targetName = tgt.getTargetName();
     }

     String statusStr="";
     String rowclass="alt2";
     if (tgt.getStatus().equalsIgnoreCase("rejected")) {
        statusStr="Reject";
        rowclass="rej";
     } else if (tgt.getStatus().equalsIgnoreCase("accepted")) {
        statusStr="Accept";
     } else {
        statusStr="&nbsp;";
     }
     String cxcStart = "&nbsp;";
     String cxcStop = "&nbsp;";
     if (tgt.getResponseStart().doubleValue()  >= 0) {
       cxcStart = tgt.getResponseStart().toString();
     }
     if (tgt.getResponseStop().doubleValue()  >= 0) {
       cxcStop = tgt.getResponseStop().toString();
     }
       

%>
<tr class="<%=rowclass%>">
<td class="list"><%= tgt.getTargetNumber() %></td>
<td class="list"><%= targetName %></td>
<td class="list"><%= tgt.getRAString() %></td>
<td class="list"><%= tgt.getDecString() %></td>
<td class="list"><%= expTime %> </td>
<td class="list"><%= appTime %></td>
<td class="list"><%= tgt.getInstrument() %></td>
<td class="list"><%= tgt.getGrating() %></td>
<td class="list"><%= cxcStart %></td>
<td class="list"><%= cxcStop %></td>
<td class="list"><%= statusStr %></td>
</tr>
<%   // Any followups for the current set of targets?
  if (fupList != null && fupList.size()> 0) {
        dval = tgt.getInitialTime();
	String atime = "&nbsp";
        if (dval.doubleValue() >= 0) { 
           atime = nf.format(dval);
        }

%>
<tr align="right">
<td colspan="11">
<table class="fups" border="1">
<tr align="right"> 
<th class="hdr">Observation</th>
<th class="hdr">Exp.Time</th>
<th class="hdr">Min. Interval</th>
<th class="hdr">Max. Interval</th>
<th class="hdr">TargNum</th>
<th class="hdr">Status</th>
</tr>
<%  //don't display if its a monitor(non-transient)
  if (atime.indexOf("nbsp") < 0) { %>
<tr>
<td class="list">Trigger</td>
<td class="list"><%= atime %></td>
<td class="list">&nbsp;</td>
<td class="list">&nbsp;</td>
<td class="list"><%= tgt.getTargetNumber().toString() %></td>
<td class="list"><%= statusStr %></td>
</tr>
<% } %>


<%
     for (int ff=0;ff<fupList.size();ff++) {
        DDTFollowup fup = (DDTFollowup)fupList.get(ff);
        String fupOptions = new String("");
	String fupKey= fup.getTargid().toString() + "-";
	fupKey += fup.getOrdr().toString() ;
        rowclass="alt2";
        if (fup.getStatus().equalsIgnoreCase("rejected")) {
           fupOptions="Reject";
           rowclass="rej";
        } else if (fup.getStatus().equalsIgnoreCase("accepted")) {
           fupOptions="Accept";
        } else {
           fupOptions="&nbsp;";
        } 
        dval = fup.getExpTime();
        if (dval.doubleValue() >= 0) { 
           atime = nf.format(dval);
        }
        else {
           atime = "&nbsp;";
        }
        
        String fupMinLead = nf.format(fup.getMinLead());
        String fupMaxLead = nf.format(fup.getMaxLead());
%>
<tr class="<%=rowclass%>">
<td class="list"><%= fup.getOrdr()%></td>
<td class="list"><%=atime %> </td>
<td class="list"><%=fupMinLead%></td>
<td class="list"><%=fupMaxLead%></td>
<td class="list"><%=fup.getTargetNumber().toString()%></td>
<td class="list"> <%= fupOptions %> </td>
</tr>
<% } %>
</table>
</td>
</tr>


<%   } %>
<% } %>

</tbody>
</table>
<% }  %>
<% }  %>


<p>
</p>
<p>

<%
if (isDDTMigrate.booleanValue() == true) {
%>
<table class="btn">
<tr valign="top">
<td class="center"><input type="submit" class="subbtn" name="Submit" value="<%= TriggerTooConstants.DDTUPDATE%>" onclick="this.form.operation.value='<%= TriggerTooConstants.DDTUPDATE%>'; " >
<td><input type="submit" class="subbtn" name="Submit" value="<%= TriggerTooConstants.CANCELNOSEND%>" onclick="this.form.operation.value='<%= TriggerTooConstants.CANCELNOSEND%>';" > </td>


</tr>
</table>
<br>
<%   } 
%>
</center>
</form>
<p>
</body></html>
