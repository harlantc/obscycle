<%@ page session="true" import="java.text.* , java.util.* , info.*" %>
<% 
  ObservationList obsList = (ObservationList)session.getAttribute("obsList");
  TriggerTooEntry tooEntry = (TriggerTooEntry)session.getAttribute("tooEntry");
  String pocStr = (String)session.getAttribute("mpPOC");
  String message = (String)session.getAttribute("message");
  if (message == null) {
     message = "";
  }
  message = message.replaceAll("\n","<br>");
  
  String coordMessage = (String)session.getAttribute("coordMessage");
  if (coordMessage == null) coordMessage="";
  coordMessage = coordMessage.replaceAll("\n","<br>");
  String msgclass = (String)session.getAttribute("msgclass");
  String obsidLink = (String)session.getAttribute(TriggerTooConstants.OBSDETAIL);
  String raClass = (String)session.getAttribute("RALabelBG") ;
  String decClass = (String)session.getAttribute("DecLabelBG") ;
  if (raClass == null) {
    raClass = "";
  }
  if (decClass == null) {
    decClass = "";
  }
  String ccemail = (String)session.getAttribute("ccemail");
  if (ccemail == null) ccemail = "";
  String theSeqNbr = new String("");
  String theObsid = new String("");
  String theTargetName = new String("");

  Observation theObs = null;
  if (obsList.size() > 0) {
    theObs = obsList.getByObsid(tooEntry.getObsid());
    theSeqNbr = theObs.getSequenceNumber();
    theObsid  = theObs.getObsid().toString();
    if (theObs.getTargetName() != null) {
       theTargetName = theObs.getTargetName();
    }

    String reqRA = "&nbsp;";
    String reqDec = "&nbsp;";
    String reqName = "&nbsp;";
    if (tooEntry.getTargetName() != null && tooEntry.getTargetName().length() > 1) {
      reqName = tooEntry.getTargetName();
    }
    if (tooEntry.getRAString() != null && tooEntry.getRAString().length() > 1) {
      reqRA = tooEntry.getRAString();
    }
    if (tooEntry.getDecString() != null && tooEntry.getDecString().length() > 1) {
      reqDec = tooEntry.getDecString();
    }
  
  response.addCookie(new Cookie("JSESSIONID", session.getId()));
%>
<%@ include file = "tooManagerHead.jsp" %>
<body class="body" >
<script type="text/javascript" src="toomanager.js"></script>
<div class="hdrleft">
<img src="header_left.gif" alt="Chandra Science">
<br>
<a href="/toomanager/tooManager.jsp?operation=return">Back to Chandra TOO Manager</a>
</div>
<div class="hdrcenter">
<font class="biggerLabel">
Chandra TOO ObsCat Update
</font>
<p>
<font class="bigLabel">
Sequence Number <%= theSeqNbr %>,  Observation ID <%= theObsid %>
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

<font class="error"> <%= coordMessage %></font>
<p><font class="<%= msgclass %>"> <%= message %></font></p>
<form name="goto" method="post" action="/toomanager/tooObscatUpdate.jsp" onsubmit="return verifyUpdate(this);" >
<input type="hidden" name="operation" value="Cancel">
<input type="hidden" name="triggerID" value="<%= tooEntry.getTriggerID() %>">
<input type="hidden" name="ccemail" value="<%= ccemail %>">
<center>
<div class="instructbar">
<p>Coordinate and/or Target Name changes may be applied to the observations
below.  If 'Override' values exist, they will be applied.  If not, the
user requested values will be applied.  Changes will only be applied  
to those observations that have the <b><i>'Update Coordinates?'</i></b> box checked.
If you do not want the observation coordinates updated, please
uncheck the box in the <b><i>Update Coordinates?</i></b> column.  
<p>Please verify all approved time, preceding min/max lead time changes before
applying the updates.
</div>
<br>
<table class="updates" cellpadding="1" cellspacing="0" border="1" >
<caption><font class="bigLabel">Coordinate Update Request</font></caption>
<tbody><tr>
<th class="hdr2">&nbsp;</th>
<th class="hdr2">Requested</th>
<th class="hdr2">Override</th>
</tr>
<tr>
<th class="hdr">Target Name </th>
<td class="list"><%= reqName %></td>
<td class="field"><input class="editl" type="text" size="30" name="<%= TriggerTooConstants.TARGETNAME %>" value="<%= tooEntry.getOverrideTargetName() %>"  ><select size="1" name="<%= TriggerTooConstants.RESOLVERLIST %>">
  <%
  String[] resolverChoices = {"SIMBAD/NED", "NED/SIMBAD", "SIMBAD", "NED"} ;
  for ( int i = 0; i < resolverChoices.length; i++ ) {
  %>
   <option<%= resolverChoices[i].equals( session.getAttribute( TriggerTooConstants.RESOLVERLIST ) ) ? " SELECTED" : "" %> value="<%= resolverChoices[i] %>"><%=resolverChoices[i] %></option>
  <% }
  %>
</select>&nbsp;&nbsp;<input class="subbtn" type="submit" name="Submit" value="<%=TriggerTooConstants.NAMERESOLVER%>" onclick="this.form.operation.value='<%=TriggerTooConstants.NAMERESOLVER%>';" ></td>
</tr>
<tr>
<th class="hdr">RA</th>
<td class="list"><%= reqRA %></td>
<td <%= raClass %>><input class="editl" type="text" size="13" name="<%= TriggerTooConstants.RA %>" value="<%= tooEntry.getOverrideRAString()%>" ><font size="-1"><%= TriggerTooConstants.RAFORMAT %> </font>
</td>
</tr>
<tr>
<th class="hdr">Dec</th>
<td class="list"><%= reqDec %></td>
<td <%= decClass %> ><input class="editl" type="text" size="13" name="<%= TriggerTooConstants.DEC %>" value="<%= tooEntry.getOverrideDecString() %>"  > <font size="-1">
<%= TriggerTooConstants.DECFORMAT %></font></td>
</tr>
</tbody>
</table>

</center>

<p>
<center>
<table class="updates" cellpadding="1" cellspacing="0" border="1" >
<caption><font class="bigLabel">Observation Update Request</font></caption>
<tbody><tr>
<th class="hdr" rowspan="2"><%= TriggerTooConstants.SEQNBR %> </th>
<th class="hdr" rowspan="2"><%= TriggerTooConstants.OBSID %></th>
<th class="hdr" rowspan="2"><%= TriggerTooConstants.APPTIME %></th>
<th class="hdr" rowspan="2">CXC Start</th>
<th class="hdr" rowspan="2">CXC Stop</th>
<% if (obsList.size() > 1) { %>
<th class="hdr" rowspan="2"><%= TriggerTooConstants.PREID %></th>
<th class="hdr" rowspan="2"><%= TriggerTooConstants.PREMIN %></th>
<th class="hdr" rowspan="2"><%= TriggerTooConstants.PREMAX %></th>
<% } %>
<th class="hdr" colspan="3">Current ObsCat Values</th>
<th class="hdr" rowspan="2">Update<br>Coordinates? </th>
</tr>
<tr>
<th class="hdr"><%= TriggerTooConstants.TARGETNAME %></th>
<th class="hdr"><%= TriggerTooConstants.RA %></th>
<th class="hdr"><%= TriggerTooConstants.DEC %></th>
</tr>

<% 
   NumberFormat nf = NumberFormat.getInstance();
   nf.setMaximumFractionDigits(2);
   nf.setMinimumFractionDigits(2);
   Double dval;
   for (int ii=0;ii<obsList.size();ii++) {
     Observation obs = (Observation) obsList.get(ii);

     String targetName = new String("");
     if (obs.getTargetName() != null) {
       targetName = obs.getTargetName();
     }

     dval = obs.getApprovedExpTime();
     String appTime = nf.format(dval);

     String preMinLead = new String("&nbsp;");
     String preMaxLead = new String("&nbsp;");
     String preID = new String("&nbsp;");
     if (obs.getPreID().intValue() > 0) {
        preID = obs.getPreID().toString();
        dval = obs.getPreMinLead();
        preMinLead = nf.format(dval);
        dval = obs.getPreMaxLead();
        preMaxLead = nf.format(dval);
     }

     String editClass = "class='edit' ";
     String defaultChk = " checked ";
     if (!obs.isUnobserved()) {
         editClass = "readOnly";
         defaultChk = " ";
     }


     String appName = "apptime" + obs.getObsid();
     String minName = "min" + obs.getObsid();
     String maxName = "max" + obs.getObsid();

     String rstr = obs.getRAString();
     String dstr = obs.getDecString();
     if (rstr == null || rstr.equals("")) {
       rstr = "&nbsp;";
     }
     if (dstr == null || dstr.equals("")) {
       dstr = "&nbsp;";
     }


%>
<tr>
<td class="list"><%= obs.getSequenceNumber() %></td>
<td class="list"><a href="<%=obsidLink%><%=obs.getObsid()%>" target="_blank"><%= obs.getObsid() %></a></td>
<td class="list" > <input <%=editClass%> size="8" type="text" name="<%= appName%>" value="<%=appTime %>"  onchange="return isValidTime(this) ;" ></td>
<% if (ii==0) { %>
<td class="list"><input <%=editClass%> size="5" type="text" name="cxcstart" value="<%=tooEntry.getCXCStart() %>"  onchange="return isNumber(this);" ></td>
<td class = "list" ><input <%=editClass%> size = "5" type = "text" name = "cxcstop" value = "<%=tooEntry.getCXCStop() %>"  onchange="return isNumber(this);" ></td>
<% } else { %>
<td>&nbsp;</td>
<td>&nbsp;</td>
<% } %>
<% if (obsList.size() > 1) { %>
<td class="list"><%= preID %></td>
<%   if (obs.getPreID().intValue() > 0) { %>
<td class="list" ><input <%=editClass%> size="6"  type="text" name="<%=minName%>" value="<%= preMinLead %>"  onchange="return isNumber(this);" ></td>
<td class="list" > <input <%=editClass%>  size="6" type="text" name="<%=maxName%>" value="<%= preMaxLead %>" onchange="return isNumber(this);"> </td>
<%   } else { %>
<td>&nbsp;</td>
<td>&nbsp;</td>
<%   } %>
<% } %>
<td class="list"><%= targetName %></td>
<td class="list"><%= rstr %></td>
<td class="list"><%= dstr %></td>
<td class="edit"> <input <%=editClass%> type="checkbox" name="obsid<%=obs.getObsid()%>" <%=defaultChk%> ></td>
</tr>
<%  } %>

</tbody>
</table>
</center>
<% }  %>
<p>
<p>
<center>
<div class="divBtn">
<input class="subbtn" type="submit" name="Submit" value="<%=TriggerTooConstants.APPLYOBSCAT%>"
onclick="this.form.operation.value='<%=TriggerTooConstants.APPLYOBSCAT%>';" >
<input class="subbtn" type="submit" name="Submit" value="<%=TriggerTooConstants.CANCEL%>" 
onclick="this.form.operation.value='<%=TriggerTooConstants.CANCEL%>';" >
</div>
</center>
<br>

</form>
</div>
</body></html>
