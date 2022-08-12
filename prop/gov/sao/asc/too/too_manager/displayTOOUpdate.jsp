<%@ page session="true" import="java.text.* , java.util.* , info.*" %>
<% 
ObservationList obsList = (ObservationList)session.getAttribute("obsList");
ObservationList altList = (ObservationList)session.getAttribute("altList");
TriggerTooEntry tooEntry = (TriggerTooEntry)session.getAttribute("tooEntry");
String triggerFilename = (String)session.getAttribute("triggerFilename");
String pocStr = (String)session.getAttribute("mpPOC");
String message = (String)session.getAttribute("message");
String coordMessage = (String)session.getAttribute("coordMessage");
String msgclass = (String)session.getAttribute("msgclass");
String obsidLink = (String)session.getAttribute(TriggerTooConstants.OBSDETAIL);
obsidLink= obsidLink.replace("&","&amp;");
String tooSummary = (String)session.getAttribute("tooSummary");
if (tooSummary == null) { tooSummary = "";}

String usintContact = (String)session.getAttribute("usintContact");
String displayComments = (String)session.getAttribute("displayComments");
if (displayComments == null) 
displayComments = "on";
String displayAlts = (String)session.getAttribute("displayAlts");
if (displayAlts == null) 
displayAlts = "on";

String fpreadonly=" class='editl'";
String fpdisabled="";
String fieldsetAlts;
String fieldsetArrow;
if (displayAlts.equals("on"))  {
fieldsetAlts = "<fieldset id=\"fieldset_Alts\" style=\"display:block;border:0\">";
fieldsetArrow = "<img  src=\"small_tri_down.gif\" name=\"DispAlts\" alt=\"on\" onclick=\"return showHide(document.TOOManager,this,'fieldset_Alts','displayAlts');\" > <input type=\"hidden\" value=\"on\" name=\"displayAlts\">";
}
else {
fieldsetAlts = "<fieldset id=\"fieldset_Alts\" style=\"display:none;border:0\">";
fieldsetArrow = "<img  src=\"small_tri_right.gif\" name=\"DispAlts\" alt=\"off\" onclick=\"return showHide(TOOManager,this,'fieldset_Alts','displayAlts');\" > <input type=\"hidden\" value=\"off\" name=\"displayAlts\">";
}

String fieldsetCmts;
String fieldsetCmtsArrow;
if (displayComments.equals("on"))  {
fieldsetCmts = "<fieldset id=\"fieldset_Comments\" style=\"display:block;border:0\">";
fieldsetCmtsArrow = "<img  src=\"small_tri_down.gif\" name=\"DispComments\" alt=\"on\" onclick=\"return showHide(document.TOOManager,this,'fieldset_Comments','displayComments');\" > <input type=\"hidden\" value=\"on\" name=\"displayComments\">";
}
else {
fieldsetCmts = "<fieldset id=\"fieldset_Comments\" style=\"display:none;border:0\">";
fieldsetCmtsArrow = "<img  src=\"small_tri_right.gif\" name=\"DispComments\" alt=\"off\" onclick=\"return showHide(document.TOOManager,this,'fieldset_Comments','displayComments');\" > <input type=\"hidden\" value=\"off\" name=\"displayComments\">";
}

Boolean isManager = (Boolean)session.getAttribute("isManager");
Boolean isReadOnly = (Boolean)session.getAttribute("isReadOnly");
String ccemail = (String)session.getAttribute("ccemail");
String managerRole = "";
if (isManager.booleanValue() == false) {
managerRole = "disabled";
fpreadonly = "readOnly";
fpdisabled = "disabled";
}

String theSeqNbr = new String("");
String theObsid = new String("");
String theProposal = new String("");

String conflictLink = "";
String needObscat = "";
String altStr = "";
int openObsCount = 0;
Observation theObs = null;
if (obsList.size() > 0) {
theObs = obsList.getByObsid(tooEntry.getObsid());
if (theObs.isUnobserved()== false || isManager.booleanValue() == false) {
}
openObsCount = obsList.countOpenObsids();

}
response.addCookie(new Cookie("JSESSIONID", session.getId()));
%>
<%@ include file = "tooManagerHead.jsp" %>
<% if (theObs != null) {
theSeqNbr = theObs.getSequenceNumber();
theObsid  = theObs.getObsid().toString();
theProposal  = theObs.getProposalNumber();
}
if (isManager.booleanValue() == true )
  conflictLink = "<a href=\"tooPropconflict?triggerID=" + tooEntry.getTriggerID()+ "\" target=\"_blank\" >View Conflicts</a>";


%>
<body class="body" >
<script type="text/javascript" src="toomanager.js"></script>

<div class="hdrleft">
<img src="header_left.gif" alt="Chandra Science">
<br>
<a href="/toomanager/tooManager.jsp?operation=return">Back to Chandra TOO Manager</a>
</div>
<div class="hdrcenter">
<font class="biggerLabel">
Chandra TOO Status Update
</font>
<p>
<font class="bigLabel">
Sequence Number <%= theSeqNbr %>,  Observation ID <%= theObsid %>, Proposal <%= theProposal %>
</font>
</div>
<div class="hdrright">
<a href="/toomanager/logout">Logout</a>
<p>
<a href="https://icxc.harvard.edu/uspp/">Internal CDO Site</a>
<br>
<a href="https://icxc.harvard.edu/mta/CUS/Usint/obsid_usint_list.cgi">USINT</a>
</div>
<div style="clear:both;">

<% if (message != null && message.length() > 0) {
%>
<br><font class="<%= msgclass %>"> <%= message %></font>
<% } %>

<% if (theObs != null) {
String observer = new String();
if (theObs.getObserver() != null) {
  observer  = theObs.getObserver();
} else {
  observer = "&nbsp;";
}
String observerEmail = new String();
if (theObs.getObserverEmail() != null) {
  observerEmail  = theObs.getObserverEmail();
} else {
  observerEmail = "&nbsp;";
}
String theTargetName = new String("");
if (theObs.getTargetName() != null) {
theTargetName = theObs.getTargetName();
}
if (!tooEntry.getAlternateGroupName().equals(null) &&
 !tooEntry.getAlternateGroupName().equals("") ) {
altStr = "<b>Alternate Target Group:</b> ";
altStr += tooEntry.getAlternateGroupName();
altStr += "&nbsp;&nbsp;&nbsp;<b>Approved Count :</b> ";
altStr += tooEntry.getAlternateApprovedCount();
}

  String fopt = new String();
  if (tooEntry.getFastProc().equals(""))
    fopt += "<option value=\"\">No";
  else
    fopt += "<option value=\"rejected\">No";
  fopt += "<option value=\"approved\" ";
  if (tooEntry.getFastProc().equals("approved"))
    fopt += "selected";
  fopt += ">Yes";

  if (tooEntry.getFastProc().equals("completed") ||
      tooEntry.getFastProc().equals("canceled")) {
    fopt = "<option value='" + tooEntry.getFastProc() + "'>" + tooEntry.getFastProc();
    fpreadonly = "readOnly";
    fpdisabled = "disabled";
  }


%>
<form name="TOOManager" method="post" action="/toomanager/tooUpdate.jsp" onsubmit="return verifySubmit(this);" >
<input type="hidden" name="triggerID" value="<%= tooEntry.getTriggerID() %>" >
<input type="hidden" name="operation" value="Save" >
<input type="image" name="Submit" value="<%=TriggerTooConstants.SAVE%>" src="/toomanager/blank.gif" alt=" " onclick="return(false);">
<div class="infobar">
<table width="100%" border="0">

<tr>
<td width="5%"><b>P.I.: </b></td>
<td width="15%"><%= theObs.getPI() %> <%= theObs.getPIEmail() %></td>
<td><b>Coordinated?:</b>
<%= theObs.getCoordinatedObs() %></td>
<td class="right"><a href="displayFile.jsp">View&nbsp;Trigger&nbsp;Form</a></td>
</tr>
<tr>
<td class="field"><b>Observer:</b></td>
<td><%= observer %> <%= observerEmail %></td>
<% if (theObs.getObservatories() != null ) { %>
<td class="field"><b>Observatories: </b>
<%= theObs.getObservatories() %></td>
<% } else {%>
<td colspan="1">&nbsp;</td>
<% }%>
<td class="right"><%=tooSummary%></td>
</tr>
<tr>
<td class="topb">Remarks:</td>
<% if (theObs.getRemarks() != null) { %>
<td colspan="2"><%= theObs.getRemarks() %></td>
<% } else  { %>
<td colspan="2">&nbsp;</td>
<% } %>
<td class="right"><%= conflictLink %></td>
</tr>
<% if (theObs.getMPRemarks() != null && !theObs.getMPRemarks().equals("N/A")) { %>
<tr>
<td class="topb">MP&nbsp;Remarks:</td>
<td colspan="3"><%= theObs.getMPRemarks() %></td>
</tr>
<% } %>
</table>
</div>
<p>

<% if (obsList.size() > 0) {
%>
<div class="hdrbar">
<font class="biggerLabel">Observation Information</font>
<% if (isManager.booleanValue() == true && isReadOnly.booleanValue() == false &&
       openObsCount > 0) {
%>
<input type="submit" name="Submit" class="update" value="<%= TriggerTooConstants.UPDATEOBSCAT %>" onclick="this.form.operation.value='<%=TriggerTooConstants.UPDATEOBSCAT%>'; " >
<font class="tiny">(Update coordinate information, approved time, min/max preceding intervals)</font>

<% } %>
<p >

<%
  String reqClass= "hdr";
  String obsRA = "&nbsp;";
  String obsDec = "&nbsp;";
  if (theObs.getRAString() != null && theObs.getRAString().length() > 1) {
    obsRA = theObs.getRAString();
  }
  if (theObs.getDecString() != null && theObs.getDecString().length() > 1) {
    obsDec = theObs.getDecString();
  }

  boolean isdiff = false;
  String reqRA = "&nbsp;";
  String reqDec = "&nbsp;";
  String reqName = "&nbsp;";
  String overrideRA = "&nbsp;";
  String overrideDec = "&nbsp;";
  String overrideName = "&nbsp;";
  double diffVal = 0.0;
  boolean isoverride = false;

  if (tooEntry.getOverrideTargetName() != null && tooEntry.getOverrideTargetName().length() > 1) {
    overrideName = tooEntry.getOverrideTargetName();
    if (!overrideName.equals(theTargetName)) isdiff = true;
    isoverride = true;
  }
  if (tooEntry.getTargetName() != null && tooEntry.getTargetName().length() > 1) {
    reqName = tooEntry.getTargetName();
    if (!isoverride && !reqName.equals(theTargetName)) isdiff = true;
  }
  isoverride = false;
  if (tooEntry.getOverrideRAString() != null && tooEntry.getOverrideRAString().length() > 1) {
    overrideRA = tooEntry.getOverrideRAString();
    diffVal = theObs.getRA().doubleValue() - tooEntry.getOverrideRA().doubleValue();
    if (diffVal < 0) diffVal *= -1;
    if (diffVal > 0.000001) {
       isdiff = true;
    }
    isoverride = true;
  }
  if (tooEntry.getRAString() != null && tooEntry.getRAString().length() > 1) {
    reqRA = tooEntry.getRAString();
    diffVal = theObs.getRA().doubleValue() - tooEntry.getRA().doubleValue();
    if (diffVal < 0) diffVal *= -1;
    if (!isoverride && diffVal > 0.000001) {
       isdiff = true;
    }
  }

  isoverride = false;
  if (tooEntry.getOverrideDecString() != null && tooEntry.getOverrideDecString().length() > 1) {
    overrideDec = tooEntry.getOverrideDecString();
    diffVal = theObs.getDec().doubleValue() - tooEntry.getOverrideDec().doubleValue();
    if (diffVal < 0) diffVal *= -1;
    if (diffVal > 0.000001) {
       isdiff = true;
    }
    isoverride = true;
  }
  if (tooEntry.getDecString() != null && tooEntry.getDecString().length() > 1) {
    reqDec = tooEntry.getDecString();
    diffVal = theObs.getDec().doubleValue() - tooEntry.getDec().doubleValue();
    if (diffVal < 0) diffVal *= -1;
    if (!isoverride && diffVal > 0.000001) {
       isdiff = true;
    }
  }
  if (isdiff) {
     needObscat = "<input type=\"hidden\" name=\"needObscat\" value=\"true\"> ";
     reqClass = "red";
  }
%>
<table class="tblCoord" border="1">
<caption><b>Coordinate Data for Obs ID <%=theObsid%></b></caption>
<tbody>
<tr>
<th class="<%=reqClass%>">&nbsp;</th>
<th class="hdr">Target Name </th>
<th class="hdr">RA </th>
<th class="hdr">Dec</th>
</tr>
<tr class="alt2">
<th class="label">Current</th>
<td class="list"><%=theTargetName%></td>
<td class="list"><%=obsRA%></td>
<td class="list"><%=obsDec%></td>
</tr>
<tr class="alt2">
<th class="label">Requested</th>
<td class="list"><%=reqName%></td>
<td class="list"><%=reqRA%></td>
<td class="list"><%=reqDec%></td>
</tr>
<% if (overrideName.indexOf("nbsp") < 0 ||
       overrideRA.indexOf("nbsp") < 0 ||
       overrideDec.indexOf("nbsp") < 0 ) {
%>
<tr>
<th class="label">Override</th>
<td class="list"><%=overrideName%></td>
<td class="list"><%=overrideRA%></td>
<td class="list"><%=overrideDec%></td>
</tr>

<% } %>

</tbody>
</table>



<p>
<table class="tblCoord" border="1">
<caption><b>Observation Data</b></caption>
<tbody><tr>
<th class="hdr"><%= TriggerTooConstants.SEQNBR %> </th>
<th nowrap class="hdr"><%= TriggerTooConstants.OBSID %></th>
<th nowrap class="hdr"><%= TriggerTooConstants.TARGETNAME %></th>
<th class="hdr"><%= TriggerTooConstants.APPTIME %></th>
<th class="hdr"><%= TriggerTooConstants.EXPTIME %></th>
<th class="hdr"><%= TriggerTooConstants.INSTRUMENT %></th>
<th class="hdr"><%= TriggerTooConstants.GRATING %></th>
<th class="hdr">CXC Start</th>
<th class="hdr">CXC Stop</th>
<%
   if (obsList.size() > 1) {
%>
<th class="hdr"><%= TriggerTooConstants.PREID %></th>
<th class="hdr"><%= TriggerTooConstants.PREMIN %></th>
<th class="hdr"><%= TriggerTooConstants.PREMAX %></th>
<% } %>
</tr>

<% 
   NumberFormat nf = NumberFormat.getInstance();
   nf.setMaximumFractionDigits(2);
   nf.setMinimumFractionDigits(2);
   nf.setGroupingUsed(false);
   for (int ii=0;ii<obsList.size();ii++) {
     Observation obs = (Observation) obsList.get(ii);
     Double dval;
     String preMinLead = new String("&nbsp;");
     String preMaxLead = new String("&nbsp;");

     dval = obs.getRemainingExpTime();
     String expTime = nf.format(dval);
     dval = obs.getApprovedExpTime();
     String appTime = nf.format(dval);

     String targetName = new String("&nbsp;");
     if (obs.getTargetName() != null) {
       targetName = obs.getTargetName();
     }

     String preID = new String("&nbsp;");
     if (obs.getPreID().intValue() > 0) {
        preID = obs.getPreID().toString();
        dval = obs.getPreMinLead();
        preMinLead = nf.format(dval);
        dval = obs.getPreMaxLead();
        preMaxLead = nf.format(dval);
     }
     
     String updateName = "update" + obs.getObsid();
     String appName = "apptime" + obs.getObsid();
     String minName = "min" + obs.getObsid();
     String maxName = "max" + obs.getObsid();
     String cl1 = "";
     if (ii==0) {
         cl1 = "class='alt2'";
     }
%>
<tr <%=cl1%>>
<td class="list"><%= obs.getSequenceNumber() %></td>
<td class="list"><a href="<%=obsidLink%><%=obs.getObsid()%>" target="_blank"><%= obs.getObsid() %></a></td>
<td class="list"><%= targetName %></td>
<td class="list"><%= appTime %></td>
<td class="list"><%= expTime %></td>
<td class="list"><%= obs.getInstrument() %></td>
<td class="list"><%= obs.getGrating() %></td>

<%
     if (ii== 0) {
%>
<td class="list"><%= obs.getResponseStart() %></td>
<td class="list"><%= obs.getResponseStop() %></td>

<% } else { %>
<td>&nbsp;</td>
<td>&nbsp;</td>
<% 
  }
     if (obsList.size() > 1) {
%>
<td class="list"><%= preID %></td>
<td class="list"><%= preMinLead %></td>
<td class="list"><%= preMaxLead %></td>
<%    } %>
</tr>
<%  } %>

</tbody>
</table>
<% }  %>

<% if (altList.size() > 1) {
%>
<br>
<%= fieldsetArrow %>
<%= altStr %>
<%= fieldsetAlts %>
<legend></legend>
<table cellpadding="1" cellspacing="0" border="1" >
<tbody><tr>
<th class="hdr"><%= TriggerTooConstants.SEQNBR %> </th>
<th class="hdr"><%= TriggerTooConstants.OBSID %></th>
<th class="hdr"><%= TriggerTooConstants.TARGETNAME %></th>
<th class="hdr"><%= TriggerTooConstants.EXPTIME %></th>
<th class="hdr"><%= TriggerTooConstants.STATUS %></th>
<th class="hdr">#Followups</th>
</tr>

<% 
   NumberFormat nf = NumberFormat.getInstance();
   nf.setMaximumFractionDigits(2);
   nf.setMinimumFractionDigits(2);

   for (int ii=0;ii<altList.size();ii++) {
     Observation obs = (Observation) altList.get(ii);
     Integer ival = obs.getLinkedCount();
     String linkedCount = new String ("&nbsp;");
     if (ival.intValue() > 0) {
        linkedCount = ival.toString();
     }
     Double dval;
     if (obs.getPreID().intValue() <= 0)  {

       dval = obs.getRemainingExpTime();
       String expTime = nf.format(dval);

       String targetName = new String("&nbsp;");
       if (obs.getTargetName() != null) {
         targetName = obs.getTargetName();
       }
%>
<tr>
<td class="list"><%= obs.getSequenceNumber() %></td>
<td class="list"><a href="<%=obsidLink%><%=obs.getObsid()%>" target="_blank"><%= obs.getObsid() %></a></td>
<td class="list"><%= targetName %></td>
<td class="list"><%= expTime %></td>
<td class="list"><%= obs.getStatus() %></td>
<td class="list"><%= linkedCount %></td>
</tr>
<%
      }
    }
%>

</tbody>
</table>
</fieldset>
<% }  %>
<p >
</div>
<%
if (openObsCount > 0 && isReadOnly.booleanValue() == false) {
%>
<font class="biggerLabel">Comments</font>
<input type="submit" class="subbtn" name="Submit" value="<%= TriggerTooConstants.SAVEDRAFT %>" onclick="this.form.operation.value='<%= TriggerTooConstants.SAVEDRAFT%>';" >
<br>
<textarea class="txtcmt" name="<%= TriggerTooConstants.COMMENT %>" rows="8" cols="100">
<%= tooEntry.getComment() %>
</textarea>
<% } %>
<p>
<%= fieldsetCmtsArrow %>
<b>Comments History:</b>
<%= fieldsetCmts %>
<legend>&nbsp;</legend>
<%  Vector cmtlist = tooEntry.getCommentHistoryList();
    for (int ii=0;ii<cmtlist.size();ii++) {
       CommentHistory cmt = (CommentHistory)cmtlist.get(ii);
       String statusStr = "";
       if (cmt.getStatus() != null && cmt.getStatus().equalsIgnoreCase(TriggerTooConstants.DRAFTSTATUS)) {
           statusStr = "Status: " + cmt.getStatus();
       }
%>
<div class="cmtspanbar">
<%= cmt.getCreationDate() %>
<img src="blank.gif" width="30" height="5" alt="">
Author:<%= cmt.getUserName() %>
<img src="blank.gif" width="30"  height="1" alt="">
<%= statusStr %>
<br>
<%= cmt.getCommentHTML() %>
</div>
<p>
<% } %>
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
    if ( tooEntry.getStatus().equals(ss.get(ii))) {
       sopt += " selected";
    }
    sopt += ">";
    sopt += ss.get(ii);
  }

  String ropt = new String();
  Vector rw = new Vector();
  rw.add(new String(TriggerTooConstants.SLOW));
  rw.add(new String(TriggerTooConstants.MEDIUM));
  rw.add(new String(TriggerTooConstants.FAST));
  for (int ii=0; ii < rw.size(); ii++) {
    ropt += "<option";
    if  (tooEntry.getUrgency().indexOf((String)rw.get(ii)) > -1 ) {
       ropt += " selected";
    }
    ropt += ">";
    ropt += rw.get(ii);
  }
%>
<table>
<tr>
<th class="bigLabel"><%=TriggerTooConstants.RESPONSEWINDOW%>:</th>
<td> <select style="background-color:white;" name="<%= TriggerTooConstants.RESPONSEWINDOW%>">  
<%= ropt %> </select>
&nbsp;&nbsp;&nbsp;&nbsp;<font class="tiny">Original Response Window : <%=theObs.getResponseWindow()%> days</font>
</td></tr>
<tr>
<th class="bigLabel">Status:</th>
<td><select style="background-color:white;" name="<%= TriggerTooConstants.TOOSTATUS %>">
<%= sopt %> </select>
</td>
</tr>
<tr>
<th class="big2Label">Fast Processing:</th>
<td><select name="<%=TriggerTooConstants.FASTPROCSTATUS%>" <%=fpdisabled %> ><%=fopt%> </select></td>
</tr>
<tr>
<th class="big2Label">Fast Processing Comment:</th>
<td> <textarea name="<%=TriggerTooConstants.FASTPROCCOMMENT%>" <%=fpreadonly%> rows="2" cols="100"><%=tooEntry.getFastProcComment()%> </textarea></td>
</tr>
<%
if (openObsCount > 0 && isReadOnly.booleanValue()==false) {
%>
<tr>
<th class="big2Label"><%=TriggerTooConstants.CCEMAIL%>:</th>
<td> <input type="text" name="<%= TriggerTooConstants.CCEMAIL%>" size="50" 
value="<%=ccemail%>" >  </td>
</tr>
<% } %>
</table>
<p>
<%
if (openObsCount > 0 && isReadOnly.booleanValue()==false) {
%>
<%= needObscat %>
<center>
<p>
<table class="btn">
<tr style="vertical-align:top;" >
<td class="center">
<input type="submit"  class="subbtn" name="Submit" value="<%= TriggerTooConstants.SAVE%>" <%=managerRole%> onclick="this.form.operation.value='<%= TriggerTooConstants.SAVE%>'; " >
<br>(Save all updates, no email sent)</td>
<td class="center">
<input type="submit"  class="subbtn" name="Submit" value="<%= TriggerTooConstants.SENDCMT %>" onclick="this.form.operation.value='<%= TriggerTooConstants.SENDCMT%>';" >
<br>(no page)</td>
<td class="center">
<input type="submit"  class="subbtn" name="Submit" value="<%= TriggerTooConstants.SENDMSG%>" <%=managerRole%> onclick="this.form.operation.value='<%= TriggerTooConstants.SENDMSG%>'; " >
<br>(paging based on response window)</td>
<td><input type="reset"  class="subbtn" name="Reset" value="Reset"><br>&nbsp;</td>
<td><input type="submit"  class="subbtn" name="Submit" value="<%= TriggerTooConstants.CANCEL%>" onclick="this.form.operation.value='<%= TriggerTooConstants.CANCEL%>';" ><br>&nbsp;</td>
</tr>
</table>
</center>
<br>
<%   } 
   } 
   else {
%>
<h3> No Observations retrieved for obsid.</h3>

<% }  %>

</form>
</div>
<p>
<hr>
<%@ include file = "tooManagerLinks.jsp" %>
</body></html>
