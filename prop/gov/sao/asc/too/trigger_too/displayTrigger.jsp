<%@ page session="true" import="java.util.* , info.*,java.text.* , org.apache.commons.lang3.*" %>
<%  

    String message = (String)session.getAttribute("message");
    String coordMessage = (String)session.getAttribute("coordMessage");
    ObservationList obsList = (ObservationList)session.getAttribute("obsList");
    Observation observation = (Observation)session.getAttribute("selectedObservation");
    String obsid = StringEscapeUtils.escapeHtml4(observation.getObsid().toString());
    String obsidLink = (String)session.getAttribute(TriggerTooConstants.OBSDETAIL);
    obsidLink = obsidLink.replace("&","&amp;");
    String contactStr = "";
    if (!observation.getUrgency().startsWith(TriggerTooConstants.SLOW)) {
       contactStr = ", <b>Required</b>";
    }

%>
<%@ include file = "triggertooHead.html" %>

<body class="body" onload="setScrollPosition();" >
<script type="text/javascript" src="triggertoo.js"></script>

<div id="topDiv" class="topDiv">
<div id="cxcheaderplain">
<div class="menudiv">
<div class="hdrleft">
<a href="/" target="_top"><img src="/soft/include/cxcheaderlogo.png" alt="CXC Home Page" border="0"></a>
</div>
<div class="hdrcenter">
<img src="/soft/include/blank.gif" alt="" width="1" height="50">
<font class="mainhdr"> Chandra TOO Trigger </font>
</div>
</div>
</div>
<p>
<font class="error"><%= message %></font>
</p>
<form name="goto" method="POST" action="/triggertoo/triggerObservation.jsp" >
<div id="cmsg" class='errdiv'>Request canceled.</div>
<input type="hidden" name="obsid" value=<%= obsid.toString() %> >
<div  class="hdrbar">
<table>
<tr>
<th class="label"><%= TriggerTooConstants.OBSID %>:</th>
<td class="left"><%= obsid %></td>
<td width="30">&nbsp;</td>
<td class="rightb"><%= TriggerTooConstants.SEQNBR %>:</td>
<td class="left"><%= StringEscapeUtils.escapeHtml4(observation.getSequenceNumber() )%></td>
<td width="30">&nbsp;</td>
<td class="rightb"><%= TriggerTooConstants.PROPNUM %>:</td>
<td class="left"><%= StringEscapeUtils.escapeHtml4(observation.getProposalNumber() )%></td>
</tr>
<tr>
<th class="label"><%= TriggerTooConstants.PINAME %>:</th> 
<td class="left" colspan="5"><%= StringEscapeUtils.escapeHtml4(observation.getPI()) %></td>
<% if (observation.getObserver() != null &&
       observation.getObserver().length() >0 ) {
       
%>
<td class="rightb"><%= TriggerTooConstants.OBSERVER %>: </td>
<td class="left"> <%= StringEscapeUtils.escapeHtml4(observation.getObserver()) %></td>
<% } else { %>
<td>&nbsp;</td>
<td>&nbsp;</td>
<% } %>
</tr>
<tr>
<th class="label">Title:</th> 
<td class="left" colspan="7"><%= StringEscapeUtils.escapeHtml4(observation.getTitle()) %></td>
</tr>
</table>
</div>
<p>
<table >
<tr><th <%= (String)session.getAttribute("ContactInfoLabelBG") %>>
<a href="/triggertoo/triggertooHelp.jsp#contactInfo"><%= TriggerTooConstants.CONTACTINFO %></a> (255 characters <%= contactStr %>)
</th></tr><tr><td class="field">
<textarea name="<%= TriggerTooConstants.CONTACTINFO %>" rows="4" cols="70" >
<%= StringEscapeUtils.escapeHtml4(observation.getContactInfo()) %>
</textarea>
</td></tr></table>

<hr>
<table cellspacing="0" cellpadding="0" border="0">
<tr>
<th class="bigLabel">Approved trigger criteria:</th>
</tr><tr>
<td class="field"> 
<%= StringEscapeUtils.escapeHtml4(observation.getTriggerCriteria()) %>
</td> </tr>
<tr>
<td>&nbsp;</td>
</tr>
<tr>
<th <%= (String)session.getAttribute("TriggerJustifyLabelBG") %>><a href="/triggertoo/triggertooHelp.jsp#triggerCriteria" <%= (String)session.getAttribute("TriggerJustifyLabelBG")%> >Explain how the trigger criteria have been met</a>  (1000 characters, <b>Required</b>)</th>
</tr> 
<tr>
<td>
<textarea name="<%= TriggerTooConstants.TRIGGERJUSTIFY %>" rows="15" cols="70" >
<%= StringEscapeUtils.escapeHtml4(observation.getTriggerJustify()) %>
</textarea>
</td></tr>
</table>
<h2>Approved response window is <%= StringEscapeUtils.escapeHtml4(observation.getResponseWindow()) %> days.</h2>
<table cellspacing="0" cellpadding="0" border="0">
<tr>
<th <%= (String)session.getAttribute("ResponseChangesLabelBG") %>>
<a href="/triggertoo/triggertooHelp.jsp#responseWindow">Justification and details for any change in response window. </a></th>
</tr>
<tr>
<td>Exact response is <%= observation.getResponseStart() %> - <%= observation.getResponseStop() %> days.
<br>Faster response times may not be approved.  (400 characters) 
</td>
</tr> <tr>
<td>
<textarea name="<%= TriggerTooConstants.RESPONSECHANGES %>" rows="4" cols="70">
<%= StringEscapeUtils.escapeHtml4(observation.getResponseChange()) %>
</textarea>
</td>
</tr>
</table>

<hr>

<a name="obsinfo"></a>
<center>
<h2><i>Observation Parameters</i></h2>
</center>
<font class="error"><%= coordMessage %></font>
<br>
<% 
   if  (observation.isEditable() == true) {
%>
Please enter <b>target name and coordinates</b> if these were not known in advance
<%
   String raString = StringEscapeUtils.escapeHtml4(observation.getRAString());
   String decString = StringEscapeUtils.escapeHtml4(observation.getDecString());
   String targetName = new String ("");
   if (observation.getTargetName() != null) {
      targetName = StringEscapeUtils.escapeHtml4(observation.getTargetName());
   }
%>
<table>
<tr>
<th <%= (String)session.getAttribute("TargetLabelBG") %>><a <%=(String)session.getAttribute("TargetLabelBG")%>  href="/triggertoo/triggertooHelp.jsp#targetName"><%= TriggerTooConstants.TARGETNAME %>:</a></th>
<td class="field"><input size="50" name="<%= TriggerTooConstants.TARGETNAME %>"  value="<%= targetName %>"></td>
<td class="field"><select size="1" name="<%= TriggerTooConstants.RESOLVERLIST %>">
  <%
  String[] resolverChoices = {"SIMBAD/NED", "NED/SIMBAD", "SIMBAD", "NED"} ;
  for ( int i = 0; i < resolverChoices.length; i++ ) {
  %>
   <option<%= resolverChoices[i].equals( session.getAttribute( TriggerTooConstants.RESOLVERLIST ) ) ? " SELECTED" : "" %> value="<%= resolverChoices[i] %>"><%= resolverChoices[i] %></option>
  <% } %>
</select> </td>
<td>&nbsp;&nbsp;&nbsp;</td>
<td class="field" ><input class="submitBtn" type="submit" name="Submit" value="<%= TriggerTooConstants.NAMERESOLVER %>" onclick="getScrollPosition(this);" ></td>
</tr></table>
<p></p>
<table>
<tr>
<td class="field"><%= TriggerTooConstants.COORD %> </td>
<th <%= (String)session.getAttribute("RALabelBG") %>><a href="/triggertoo/triggertooHelp.jsp#coordInfo" <%= (String)session.getAttribute("RALabelBG") %>><%= TriggerTooConstants.RA %>:</a></th>
<td class="field">
<input size="12" name="<%= TriggerTooConstants.RA %>" value="<%= raString %>">
</td>
<th <%= (String)session.getAttribute("DecLabelBG") %>><a href="/triggertoo/triggertooHelp.jsp#coordInfo" <%= (String)session.getAttribute("DecLabelBG") %>><%= TriggerTooConstants.DEC %>:</a></th>
<td class="field">
<input size="13" name="<%= TriggerTooConstants.DEC %>" value="<%= decString %>">
</td>
</tr>
<tr>
<td class="field">&nbsp;</td>
<td class="field">&nbsp;</td>
<td class="small"><%= TriggerTooConstants.RAFORMAT %></td>
<td class="field">&nbsp;</td>
<td class="small"><%= TriggerTooConstants.DECFORMAT %></td>
</tr>
</table>
<% } 
else {
%>
<b><%= TriggerTooConstants.TARGETNAME %>:</b> <%= StringEscapeUtils.escapeHtml4(observation.getTargetName()) %> <br>
<%= TriggerTooConstants.COORD %> <b><%= TriggerTooConstants.RA %>:</b> <%=StringEscapeUtils.escapeHtml4(observation.getRAString()) %> <b><%= TriggerTooConstants.DEC %>:</b> <%=StringEscapeUtils.escapeHtml4(observation.getDecString()) %>
<% } %>
<p></p>
<% if (obsList.size() > 0) {
%>
<font class="bigLabel">Observation Information</font><font class="tiny"> Click on the Observation ID to view the full listing of instrument parameters.</font>
<table cellpadding="1" cellspacing="0" border="1" >
<tbody><tr>
<th class="hdr"><%= TriggerTooConstants.SEQNBR %> </th>
<th class="hdr"><%= TriggerTooConstants.OBSID %></th>
<th class="hdr"><%= TriggerTooConstants.TARGETNAME %></th>
<th class="hdr"><%= TriggerTooConstants.EXPTIME %></th>
<th class="hdr"><%= TriggerTooConstants.INSTRUMENT %></th>
<th class="hdr"><%= TriggerTooConstants.GRATING %></th>
<th class="hdr"><%= TriggerTooConstants.FUP %></th>
<th class="hdr"><%= TriggerTooConstants.PREMIN %></th>
<th class="hdr"><%= TriggerTooConstants.PREMAX %></th>
</tr>

<% 
   NumberFormat nf = NumberFormat.getInstance();
   nf.setMaximumFractionDigits(2);
   nf.setMinimumFractionDigits(2);
   for (int ii=0;ii<obsList.size();ii++) {
     Observation obs = (Observation) obsList.get(ii);
     Double dval;
     String preMinLead = new String("&nbsp;");
     String preMaxLead = new String("&nbsp;");

     dval = obs.getRemainingExpTime();
     String expTime = nf.format(dval);

     String tgtName = new String("&nbsp;");
     if (obs.getTargetName() != null) {
       tgtName = StringEscapeUtils.escapeHtml4(obs.getTargetName());
     }


     String preID = new String("&nbsp;");
     if (obs.getPreID().intValue() > 0) {
        preID = obs.getPreID().toString();
        dval = obs.getPreMinLead();
        preMinLead = nf.format(dval);
        dval = obs.getPreMaxLead();
        preMaxLead = nf.format(dval);
     }
     


%>
<tr>
<td class="list"><%= obs.getSequenceNumber() %></td>
<td class="list"><a href="<%=obsidLink%><%= obs.getObsid() %>" target="_blank"><%= obs.getObsid() %></a></td>
<td class="list"><%= tgtName %></td>
<td class="list"><%= expTime %></td>
<td class="list"><%= obs.getInstrument() %></td>
<td class="list"><%= obs.getGrating() %></td>
<td class="list"><%= preID %></td>
<td class="list"><%= preMinLead %></td>
<td class="list"><%= preMaxLead %></td>
</tr>
<%   %>
<%  } %>

</tbody>
</table>
<% }  %>

<p>
<!--
A full listing of instrument parameters is given 
<a href="<%=obsidLink%><%=observation.getSequenceNumber()%>" target="_blank">HERE</a>.<br>
-->

Please detail and explain any changes to your observation parameters 
in the box below:
<br>
<textarea name="<%= TriggerTooConstants.OBSCHANGES %>" rows="4" cols="70" >
<%= StringEscapeUtils.escapeHtml4(observation.getObsChanges()) %>
</textarea>
</p>
<font class="error"><%= message %></font>
<center>
<div class="btnDiv">
<input type="submit" name="Submit" class="trigbtn" value="<%=TriggerTooConstants.SUBMIT%>"  onclick="clearScrollPosition(); return (verifyTriggerSubmit());" >
<input type="submit" name="Submit" class="trigbtn" value="<%=TriggerTooConstants.PRINTER%>"  >
<input type="submit" name="Submit" class="trigbtn" value="<%=TriggerTooConstants.CANCEL%>"  onclick="clearScrollPosition();" >
</div>
</center>
</form>
</div>
<p>
<div class="footerDiv">
<%@ include file="cxcfooterj.html" %>
</div>

</body>
</html>


