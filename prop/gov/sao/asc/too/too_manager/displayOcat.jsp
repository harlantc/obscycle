<%@ page session="true" import="java.text.* , java.util.* , info.*" %>
<%
  ObservationList obsList = (ObservationList)session.getAttribute("obsList");
  String proposalNumber = (String)session.getAttribute("proposalNumber");
  String message = (String)session.getAttribute("message");
  String msgclass = (String)session.getAttribute("msgclass");
  String pocStr = (String)session.getAttribute("mpPOC");
  String obsidLink = (String)session.getAttribute(TriggerTooConstants.OBSDETAIL);


  response.addCookie(new Cookie("JSESSIONID", session.getId()));
%>
<%@ include file = "ddtManagerHead.jsp" %>
<body class="body" >
<script type="text/javascript" src="toomanager.js"></script>
<table width="100%" cellspacing="0" cellpadding="0">
<tr>
<td><img src="header_left.gif" alt="Chandra Science">
</td>
<td class="title">Chandra ObsCat Information</td>
<td>&nbsp;</td>
</tr>
<td align="left"><a href="/toomanager/ddtManager.jsp?operation=return">Back to Chandra DDT Manager</a></td>
<td class="subtitle"> Proposal Number <%= proposalNumber %> </td>
<td align="right"><a href="/toomanager/logout">Logout</a></td>
</tr>
</table>
<p><font class="<%= msgclass %>"> <%= message %></font></p>
<%
if (obsList != null &&obsList.size()>0) {
%>

<table cellpadding=5 border=1>
<tr>
<th class="hdr"><%=TriggerTooConstants.SEQNBR%></th>
<th class="hdr"><%=TriggerTooConstants.OBSID%></th>
<th class="hdr"><%=TriggerTooConstants.STATUS%></th>
<th class="hdr"><%=TriggerTooConstants.EXPTIME%></th>
<th class="hdr"><%=TriggerTooConstants.INSTRUMENT%></th>
<th class="hdr"><%=TriggerTooConstants.GRATING%></th>
<th class="hdr"><%=TriggerTooConstants.PREID%></th>
<th class="hdr"><%=TriggerTooConstants.PREMIN%></th>
<th class="hdr"><%=TriggerTooConstants.PREMAX%></th>
<th class="hdr">LTS Date</th>
<th class="hdr">SOE Date</th>
</tr>


<%
   NumberFormat nf = NumberFormat.getInstance();
   nf.setMaximumFractionDigits(2);
   nf.setMinimumFractionDigits(2);
   nf.setGroupingUsed(false);
   Double dval;
   Integer ival;
   String rowClass = "alt1";
   for (int ii=0;ii<obsList.size();ii++) {
     Observation obs = (Observation)obsList.get(ii);
     dval = obs.getRemainingExpTime();
     String expTime = nf.format(dval);
     String preID = "&nbsp;" ;
     String minLead = "&nbsp;" ;
     String maxLead = "&nbsp;" ;
     ival = obs.getPreID();
     if (ival.intValue() > 0) {
        preID = ival.toString();
     }
     dval = obs.getPreMinLead();
     if (dval.doubleValue() > 0) {
       minLead= nf.format(dval);
     }
     dval = obs.getPreMaxLead();
     if (dval.doubleValue() > 0) {
       maxLead= nf.format(dval);
     }
     String ltsDate;
     String stsDate;
     ltsDate = obs.getLTSDate();
     if (ltsDate == null || ltsDate.length() == 0) {
       ltsDate = "&nbsp;";
     }
     stsDate = obs.getSTSDate();
     if (stsDate == null || stsDate.length() == 0) {
       stsDate = "&nbsp;";
     }


%>
<tr class="<%=rowClass%>" onmouseover="this.className='highlight'" onmouseout="this.className='<%=rowClass%>'" >
<td class="list"><%=obs.getSequenceNumber()%></td>
<td class="list"><a href="<%=obsidLink%><%=obs.getObsid()%>" target="_blank"><%= obs.getObsid() %></a></td>
<td class="list"><%=obs.getStatus()%></td>
<td class="list"><%=expTime%></td>
<td class="list"><%=obs.getInstrument()%></td>
<td class="list"><%=obs.getGrating()%></td>
<td class="list"><%=preID%></td>
<td class="list"><%=minLead%></td>
<td class="list"><%=maxLead%></td>
<td class="list"><%=ltsDate%></td>
<td class="list"><%=stsDate%></td>
</tr>
<%
     if (rowClass.equals("alt1")) {
       rowClass="alt2";
     } else {
       rowClass="alt1";
     }
   }
%>

</table>
<% }
   else {
%>
<font class="error">
<h3>Unable to find ObsCat Data for proposal number <%=proposalNumber%></h3>
</font>
<% }
%>
<p />
<p />
<%@ include file = "ddtManagerLinks.jsp" %>
</body></html>



