<%@ page session="true" import="java.text.* , java.util.* , info.*, org.apache.commons.lang3.*" %>
<% 
  PropTargetList tgtList = (PropTargetList)session.getAttribute("tgtList");
  DDTEntry theDDT = (DDTEntry)session.getAttribute("ddtEntry");
  String pocStr = new String("");
  String coi = new String("&nbsp;");
  String propNum = new String("");
  String propType = new String("DDT");

  response.addCookie(new Cookie("JSESSIONID", session.getId()));
  if (theDDT != null) {
     propNum = theDDT.getProposalNumber();
     propType = theDDT.getProposalType();
  }
%>
<%@ include file = "ddtManagerHead.jsp" %>
<body class="body" >
<table width="100%" cellspacing="0" cellpadding="0">
<tr>
<td><img src="header_left.gif" alt="Chandra Science"> </td>
<td class="title">Chandra <%=propType%> Proposal <%= propNum %> </td>
</tr>
</table>

<% 
   if (theDDT != null) {
     if (theDDT.getObserver() != null) {
       coi  = theDDT.getObserver();
     }
%>
<div class="ddtpbar" >
<table>
<tr>
<th class="label">P.I.:</th>
<td class="top"><%= theDDT.getPI()%></td>
<td width="50">&nbsp;</td>
<th class="label">Observer:</th>
<td class="top"><%= coi %></td>
</tr>
<tr>
<th class="label" nowrap="nowrap">Science Category:</th>
<td class="top"><%= theDDT.getSubjectCategory()%></td>
<td>&nbsp;</td>
<th class="label" nowrap="nowrap">Proprietary Rights:</th>
<td class="top"><%= theDDT.getDataRights()%></td>
</tr>
<tr>
<th class="label">Title:</th>
<td class="top" colspan="5"><%= theDDT.getProposalTitle()%></td>
</tr>
</table>
<table>
<tr>
<th class="labelt">Science Justification:</th>
<td class="top"><%= StringEscapeUtils.escapeHtml4(theDDT.getProposalAbstract())%></td>
</tr>
</table>
</div>
<% if (tgtList.size() > 0) {

   NumberFormat nf = NumberFormat.getInstance();
   nf.setMaximumFractionDigits(2);
   nf.setMinimumFractionDigits(2);
   nf.setGroupingUsed(false);
   for (int ii=0;ii<tgtList.size();ii++) {
     PropTarget tgt = (PropTarget) tgtList.get(ii);
     Vector fupList= tgt.getFollowups();
     Vector rollreq= tgt.getRollReq();
     Vector timereq= tgt.getTimeReq();
     Vector aciswin= tgt.getAcisWin();
     Double dval;
     dval = tgt.getExpTime();
     String expTime = nf.format(dval);
     String targetName = new String("&nbsp;");
     if (tgt.getTargetName() != null) {
       targetName = tgt.getTargetName();
     }
     AcisParam ap = tgt.getAcisParam();


%>
<div class="cmtspanbar">
<table border="0" >
<tr>
<th class="labelt">Target Name:</th>
<td class="top"><%=targetName%></td>
<td width="50">&nbsp;</td>
<th class="labelt">Description:</th> 
<td class="top"><%=tgt.getTargetDescription()%></td>
</tr>

<tr>
<th nowrap="nowrap" class="label">Right Ascension:</th>
<td nowrap="nowrap">&nbsp;<%=tgt.getRAString() %></td>
<td >&nbsp;</td>
<th class="label">Grid:</th>
<% if (tgt.getGridName() != null && tgt.getGridName() != "") { %>
<td><%=tgt.getGridName() %> </td>
<% } else { %>
<td >&nbsp;</td>
<% } %>
</tr>
<tr>
<th class="label">Declination:</th>
<td nowrap="nowrap"><%=tgt.getDecString() %></td>
<td >&nbsp;</td>
<% if (tgt.getGridName() != null && tgt.getGridName() != "") { %>
<th nowrap="nowrap" class="label">Grid Pointings:</th>
<td><%=tgt.getGridPointings().toString() %></td>
</tr>
<tr>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<th class="label">Grid Radius:</th>
<td><%=tgt.getGridRadius().toString() %></td>
</tr>
<% } else { %>
<td>&nbsp;</td>
<td>&nbsp;</td>
</tr>
<% } %>
<tr><td colspan="5">&nbsp;</td></tr>
<tr>
<th class="label">Instrument:</th>
<td ><%=tgt.getInstrument() %></td>
<td >&nbsp;</td>
<th class="label">HRC Timing:</th>
<td ><%=tgt.getHRCTiming() %></td>
</tr>
<tr>
<th class="label">Grating:</th>
<td ><%=tgt.getGrating() %></td>
<td >&nbsp;</td>
<td >&nbsp;</td>
<td >&nbsp;</td>
</tr>
<tr><td colspan="5">&nbsp;</td></tr>
<tr>
<th class="label">Y Offset:</th>
<% if (tgt.getYDetOffset() != TriggerTooConstants.EMPTY_VALUE) { %>
<td ><%=tgt.getYDetOffset().toString() %></td>
<% } else { %>
<td>&nbsp;</td>
<% } %>
<td>&nbsp;</td>
<th class="label">Sim Offset:</th>
<% if (tgt.getSimTransOffset() != TriggerTooConstants.EMPTY_VALUE) { %>
<td ><%=tgt.getSimTransOffset().toString() %></td>
<% } else { %>
<td>&nbsp;</td>
<% } %>
</tr>
<tr>
<th class="label">Z Offset:</th>
<% if (tgt.getZDetOffset() != TriggerTooConstants.EMPTY_VALUE) { %>
<td ><%=tgt.getZDetOffset().toString() %></td>
<% } else { %>
<td>&nbsp;</td>
<% } %>
<td>&nbsp;</td>
<th class="label">Est.Count Rate:</th>
<% if (tgt.getEstCntRate() != TriggerTooConstants.EMPTY_VALUE) { %>
<td ><%=tgt.getEstCntRate().toString() %></td>
<% } else { %>
<td>&nbsp;</td>
<% } %>
</tr>
<tr>
<th class="label">Photometry:</th>
<td ><%=tgt.getPhotometry() %></td>
<td >&nbsp;</td>
<th class="label">1st Order Rate:</th>
<% if (tgt.getForderCntRate() != TriggerTooConstants.EMPTY_VALUE) { %>
<td ><%=tgt.getForderCntRate().toString() %></td>
<% } else { %>
<td>&nbsp;</td>
<% } %>
</tr>
<tr>
<th class="label">V Magnitude:</th>
<% if (tgt.getVMagnitude() != TriggerTooConstants.EMPTY_VALUE) { %>
<td ><%=tgt.getVMagnitude().toString() %></td>
<% } else { %>
<td>&nbsp;</td>
<% } %>
<td >&nbsp;</td>
<th class="label">Total&nbsp;Field&nbsp;Rate:</th>
<% if (tgt.getTotalCntRate() != TriggerTooConstants.EMPTY_VALUE) { %>
<td ><%=tgt.getTotalCntRate().toString() %></td>
<% } else { %>
<td>&nbsp;</td>
<% } %>
</tr>
<tr><td colspan="5">&nbsp;</td></tr>
<tr>
<th class="label">Requested&nbsp;Time:</th>
<td ><%=tgt.getExpTime() %></td>
<td >&nbsp;</td>
<% if (tgt.getInitialTime().doubleValue() > 0) { %>
<th class="label">Nbr. Followups:</th>
<% if (fupList != null && fupList.size()> 0) { %>
<td><%= fupList.size() %></td>
<% } else { %>
<td>0</td>
<% } %>
<% } else { %>
<th class="label">Nbr. Observations:</th>
<% if (fupList != null && fupList.size()> 0) { %>
<td><%= fupList.size() %></td>
<% } else { %>
<td>0</td>
<% } %>
<% } %>
</tr>

<tr>
<th class="label">CXC Start:</th>
<% if (tgt.getResponseStart() != TriggerTooConstants.EMPTY_VALUE) { %>
<td ><%=tgt.getResponseStart() %></td>
<% } else { %>
<td>&nbsp;</td>
<% } %>
<td>&nbsp;</td>
<th class="label">Response&nbsp;Type:</th>
<% if (tgt.getResponseWindow() != null && tgt.getResponseWindow() != "") { %>
<td ><%=tgt.getResponseWindow() %> <font size="-1">days</font></td>
<% } else { %>
<td ><%=theDDT.getUrgency()%></td>
<% } %>
</tr>
<tr>
<th class="label">CXC Stop:</th>
<% if (tgt.getResponseStop() != TriggerTooConstants.EMPTY_VALUE) { %>
<td ><%=tgt.getResponseStop() %></td>
<% } else { %>
<td>&nbsp;</td>
<% } %>
<td >&nbsp;</td>
<th class="label">Time Critical:</th>
<td ><%=tgt.getTimeCritical() %></td>
</tr>
<tr><td colspan="5">&nbsp;</td></tr>
</table>
<table>
<tr>
<th class="labelt">Remarks:</th>
<td class="top"><%=tgt.getRemarks() %></td>
</tr>
<tr>
<th class="labelt">Trigger Criteria:</th>
<td class="top" ><%=tgt.getTrigCriteria() %></td>
</tr>
<tr>
<th class="labelt">Followup&nbsp;Remarks:</th>
<td class="top"><%=tgt.getFupRemarks() %></td>
</tr>
</table>

<p>
<%   // Any followups
  if (fupList != null && fupList.size()> 0) {
     String fhdr = "Followup Observations";
     if (tgt.getMonitorFlag() != null && 
         (tgt.getMonitorFlag().equals("Y") || tgt.getMonitorFlag().equals("P")))  {
        fhdr = "Monitor Observations";
      }
%>
<table border="1">
<caption><%=fhdr%></caption>
<tr>
<th class="hdr">Ordr</th>
<th class="hdr">Exp.Time</th>
<th class="hdr">Min. Interval</th>
<th class="hdr">Max. Interval</th>
<th class="hdr">Status</th>
</tr>
<%
     for (int tt=0;tt<fupList.size();tt++) {
         DDTFollowup fup = (DDTFollowup)fupList.get(tt);
         String fupstat = fup.getStatus();
         if (fupstat == null || fupstat.equals("")) {
           fupstat = "&nbsp;";
         }
%>
<tr>
<td class="list"><%= fup.getOrdr().toString() %></td>
<td class="list"><%= fup.getExpTime().toString() %></td>
<td class="list"><%= fup.getMinLead().toString() %></td>
<td class="list"><%= fup.getMaxLead().toString() %></td>
<td class="list"><%= fupstat %></td>
</tr>
<% } %>
</table>
<p>
<% } %>
<hr>
<table>
<tr>
<th><u><font class="bigLabel">Constraints</font></u></th>
<td width="70">&nbsp;</td>
<td>
<font class="tiny">	(Y=Yes,Required  P=Preferred  N=No Constraint)</font>
</td>
</tr>
</table>
<table border="0">
<tr>
<th class="label">Window Constraint:</th>
<%   // Any window constraints
  if (timereq != null && timereq.size()> 0) {
%>
<td>
<table border="1">
<tr>
<th class="hdr">Constraint?</th>
<th class="hdr">Window Start</th>
<th class="hdr">Window Stop</th>
</tr>
<%
     for (int tt=0;tt<timereq.size();tt++) {
        TimeReq treq = (TimeReq)timereq.get(tt);
%>
<tr>
<td class="list"><%= treq.getTimeConstraint() %></td>
<td class="list"><%= treq.getTstart() %></td>
<td class="list"><%= treq.getTstop() %></td>
</tr>
<% } %>
</table>
</td>
<% } else { %>
<td class="left">N</td>
<% } %>
</tr>
<tr>
<th class="label">Roll Constraints:</th>
<%   // Any roll constraints
  if (rollreq != null && rollreq.size()> 0) {
%>
<td>
<table border="1">
<tr>
<th class="hdr">Constraint?</th>
<th class="hdr">180?</th>
<th class="hdr">Roll Angle</th>
<th class="hdr">Tolerance</th>
</tr>
<%
     for (int rr=0;rr<rollreq.size();rr++) {
        RollReq rreq = (RollReq)rollreq.get(rr);
%>
<tr>
<td class="list"><%= rreq.getRollConstraint() %></td>
<td class="list"><%= rreq.getRoll180() %></td>
<td class="list"><%= rreq.getRoll().toString() %></td>
<td class="list"><%= rreq.getRollTolerance().toString() %></td>
</tr>
<% } %>
</table>
</td>
<% } else {%>
<td class="left">N</td>
<% } %>
</tr>
</tr>
<tr>
<th class="label">Monitor Constraint:</th>
<% if (tgt.getMonitorFlag() != null ) { %>
<td class="left"><%=tgt.getMonitorFlag()%> </td>
<% } else { %>
<td class="left">N</td>
<% } %>
</tr>
<tr>
<th class="label">Phase Constraint:</th>
<% if (tgt.getPhaseFlag() != null ) { %>
<td class="left"><%=tgt.getPhaseFlag() %></td>
<% } else { %>
<td class="left">N</td>
<% } %>
</tr>
<% if (tgt.getPhaseFlag() != null && !tgt.getPhaseFlag().equals("") &&
       !tgt.getPhaseFlag().equals("N")) {
%>

<tr>
<td colspan="2">
<table>
  <tr>
  <td>&nbsp;&nbsp;</td>
  <td>Period</td><td><%=tgt.getPhasePeriod() %></td>
  <td>Epoch</td><td><%=tgt.getPhaseEpoch() %></td>
  </tr>
  <tr>
  <td>&nbsp;&nbsp;</td>
  <td>Min.Phase</td><td><%=tgt.getPhaseStart() %></td>
  <td>Min.Phase Error</td><td><%=tgt.getPhaseStartMargin() %></td>
  </tr>
  <tr>
  <td>&nbsp;&nbsp;</td>
  <td>Max.Phase</td><td><%=tgt.getPhaseEnd() %></td>
  <td>Max.Phase Error</td><td><%=tgt.getPhaseEndMargin() %></td>
  </tr>
</table>
</td>
</tr>
<% } %>
<tr>
<th class="label">Uninterrupt:</th>
<td class="left"><%=tgt.getUninterrupt() %></td>
</tr>
<tr>
<th class="label">Pointing Update Constraint:</th>
<td class="left"><%=tgt.getPointingConstraint() %></td>
</tr>
<tr>
<th class="label">Coordinated:</th>
<td class="left"><%=tgt.getMultitelescope() %></td>
</tr>
<% if (tgt.getMultitelescope() != null && !tgt.getMultitelescope().equals("") &&
       !tgt.getMultitelescope().equals("N")) {
%>
<tr>
<td colspan="2">
  <table>
  <tr>
  <td>&nbsp;&nbsp;</td>
  <td>Interval:</td><td><%=tgt.getMultitelescopeInterval() %></td>
  <td>&nbsp;&nbsp;</td>
  <td>Telescopes:</td><td><%=tgt.getObservatories() %></td>
  </tr>
  </table>
</td>
</tr>
<% } %>
<tr>
<th class="label">Constraint in Remarks:</th>
<td class="left"><%=tgt.getRemarksFlag() %></td>
</tr>
<% if (tgt.getGroupID() != null && tgt.getGroupID() != ""  ) {
%>
<tr>
<th class="label">Group ID:</th>
<td class="left"><%=tgt.getGroupID() %></td>
</tr>
<tr>
<th class="label">Group Interval:</th>
<td class="left"><%=tgt.getGroupInterval().toString() %></td>
</tr>
<% } %>
</table>

<p>
<hr>
<% if(tgt.getInstrument().indexOf("A") == 0) { %>
<font class="bigLabel"><u>ACIS Parameters</u></font> 
<table border="0">
<tr>
<th class="label">Most Efficient:</th>
<td class="left"><%=ap.getMostEfficient() %></td>
<td width="50">&nbsp;</td>
<th class="label" >Frame Time:</th>
<% if (ap.getFrameTime() != TriggerTooConstants.EMPTY_VALUE) { %>
<td class="left"><%=ap.getFrameTime() %></td>
<% } else { %>
<td>&nbsp;</td>
<% } %>
</tr>
<tr>
<th class="label">Exposure Mode:</th><td class="left"><%=ap.getExpMode() %></td>
<td>&nbsp;</td>
<th class="label">Event TM Format:</th><td class="left"><%=ap.getBEPPack() %></td>
</tr>
<tr>
<td colspan="5">
<table>
<tr>
<td>CCDs:</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td class="rightb">I0:</td><td class="left"><%=ap.getCCDI0() %></td>
<td class="rightb">I1:</td><td class="left"><%=ap.getCCDI1() %></td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
</tr>
<tr>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td class="rightb">I2:</td><td class="left"><%=ap.getCCDI2() %></td>
<td class="rightb">I3:</td><td class="left"><%=ap.getCCDI3() %></td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
<td>&nbsp;</td>
</tr>
<tr>
<td>&nbsp;</td>
<td class="rightb">&nbsp;S0:</td><td class="left"><%=ap.getCCDS0() %></td>
<td class="rightb">&nbsp;S1:</td><td class="left"><%=ap.getCCDS1() %></td>
<td class="rightb">&nbsp;S2:</td><td class="left"><%=ap.getCCDS2() %></td>
<td class="rightb">&nbsp;S3:</td><td class="left"><%=ap.getCCDS3() %></td>
<td class="rightb">&nbsp;S4:</td><td class="left"><%=ap.getCCDS4() %></td>
<td class="rightb">&nbsp;S5:</td><td class="left"><%=ap.getCCDS5() %></td>
</tr>
</table>
</td>
</tr>
<tr>
<th class="label">Spectra Max Count:</th>
<% if (ap.getSpectraMaxCount() != TriggerTooConstants.EMPTY_VALUE) { %>
<td class="left"><%=ap.getSpectraMaxCount() %></td>
<% } else { %>
<td>&nbsp;</td>
<% } %>
<td>&nbsp;</td>
<th class="label">Multiple Spectral Lines:</th>
<td class="left"><%=ap.getMultipleSpectralLines() %></td>
</tr>
<tr>
<th class="label">Subarray:</th>
<td class="left"><%=ap.getSubarray() %></td>
<td>&nbsp;</td>
<td>&nbsp</td>
<td>&nbsp</td>
</tr>
<% if (ap.getSubarray() != null && ap.getSubarray().equalsIgnoreCase("Custom"))
{ %>
<tr><td colspan="5">
<table>
<tr>
<td width="20">&nbsp;</td>
<td >Subarray Start:</td>
<td><%=ap.getSubarrayStart() %></td>
<td width="20">&nbsp;</td>
<td >Rows:</td><td><%=ap.getSubarrayNo() %></td>
</tr>
</table>
</td>
</tr>
<% } %>
<tr>
<th class="label">Alternate Exposure:</th>
<td class="left"><%=ap.getAltExp() %></td>
<td>&nbsp</td>
<td>&nbsp;</td>
<td>&nbsp</td>
</tr>
<% if (ap.getAltExp() != null && ap.getAltExp().equalsIgnoreCase("Y")) { %>
<tr>
<td colspan="5">
<table>
<tr>
<td width="20">&nbsp;</td>
<td >Sec. Exposure Count:</td>
<td><%=ap.getSecondaryExp() %></td>
<td width="20">&nbsp;</td>
<td >Primary Exposure Time:</td>
<td><%=ap.getPrimaryExpTime() %></td>
</tr>
</table>
</td>
</tr>
<% } %>
<tr>
<th class="label">Energy Filter:</th>
<td class="left"><%=ap.getEnergyFilter() %></td>
<td>&nbsp</td>
<td>&nbsp</td>
<td>&nbsp</td>
</tr>
<% if (ap.getEnergyFilter() != null && ap.getEnergyFilter().equalsIgnoreCase("Y"
)) { %>
<tr>
<td colspan="5">
<table>
<tr>
<td width="20">&nbsp;</td>
<td >Lower Energy Threshold:</td>
<td><%=ap.getEnergyFilterLower() %></td>
<td width="20">&nbsp;</td>
<td >Range:</td>
<td><%=ap.getEnergyFilterRange() %></td>
</tr>
</table>
</td>
</tr>
<% } %>
<tr>
<th class="label">Spatial Windows?</th>
<td class="left"><%=ap.getSPWindow() %></td>
<td> &nbsp;</td>
<td> &nbsp;</td>
<td> &nbsp;</td>
</tr>
</table>
&nbsp;&nbsp;&nbsp;<%=ap.getSPAdditional()%>
<% } %>
<%   // Any spatial windows 
  if (aciswin != null && aciswin.size()> 0) {
%>
<table border="1">
<caption>Spatial Windows </caption>
<tr>
<th class="hdr">Chip</th>
<th class="hdr">Sampling Frequency</th>
<th class="hdr">Starting Column</th>
<th class="hdr">Column Width</th>
<th class="hdr">Starting Row</th>
<th class="hdr">Row Height</th>
<th class="hdr">Lower Energy Threshold </th>
<th class="hdr">Energy Range </th>
</tr>
<%
     for (int aw=0;aw<aciswin.size();aw++) {
        AcisWin awin = (AcisWin)aciswin.get(aw);
%>
<tr>
<td class="list"><%= awin.getChip() %></td>
<td class="list"><%= awin.getSample().toString() %></td>
<td class="list"><%= awin.getStartCol().toString() %></td>
<td class="list"><%= awin.getColWidth().toString() %></td>
<td class="list"><%= awin.getStartRow().toString() %></td>
<td class="list"><%= awin.getRowHeight().toString() %></td>
<% if (awin.getLowerThreshold() != TriggerTooConstants.EMPTY_VALUE) { %>
<td class="list"><%= awin.getLowerThreshold().toString() %></td>
<% } else { %>
<td>&nbsp;</td>
<% } %>
<% if (awin.getEnergyRange() != TriggerTooConstants.EMPTY_VALUE) { %>
<td class="list"><%= awin.getEnergyRange().toString() %></td>
<% } else { %>
<td>&nbsp;</td>
<% } %>
</tr>
<% } %>
</table>
<% } %>

</div>
<p>





<% } %>
    

<% } else {  %>
<p>
<hr>
<p>
<h3> No Observations retrieved for proposal.</h3>

<% }  %>
<% }   %>  

</body>
</html>
