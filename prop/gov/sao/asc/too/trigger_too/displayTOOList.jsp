<%@ page session="true" import="java.text.* , java.util.* , info.*" %>
<%  
  ObservationList observationList = (ObservationList)session.getAttribute("obsList");
  Integer maxDisplay = (Integer)session.getAttribute("maxDisplay");
  if (maxDisplay.intValue() <= 0) {
    maxDisplay = new Integer(200);
  }

  int maxCnt = 0;
  String sizeMsg = "";
  if (observationList != null) {
    maxCnt = observationList.size();
    if (maxCnt > maxDisplay.intValue()) {
      sizeMsg = "Only the first " + maxDisplay.toString();
      sizeMsg +=  " of " + maxCnt;
      sizeMsg += " observations are displayed.  Please refine your query.<br>";
      maxCnt = maxDisplay.intValue();
    } 
  } 
  Cookie cookie = new Cookie("JSESSIONID", session.getId());
  cookie.setHttpOnly(true);
  response.addCookie(cookie);

%>
<%@ include file = "triggertooHead.html" %>
<body>
<script type="text/javascript" src="triggertoo.js"></script>
<div class="topDiv">
<div id="cxcheaderplain">
<div class="menudiv">
<div class="hdrleft">
<a href="/" target="_top"><img src="/soft/include/cxcheaderlogo.png" alt="CXC Home Page" border="0"></a>
</div>
<div class="hdrcenter">
<img src="/soft/include/blank.gif" alt="" width="1" height="50">
<font class="mainhdr"> Chandra TOO Trigger Search Results </font>
</div>
</div>
</div>
<font class="error"><%=sizeMsg%> </font>
<br>
<div  class="hdrbar">
If your observation is a series of
observations,  only the 1st observation is available to be triggered.
Only TOO observations that have not been observed may be triggered.
<p>
Please select the observation you wish to trigger by clicking on
the link in the <b>Observation ID</b> column.  
</div>
<br>
<table  width= "100%" cellpadding="1" cellspacing="0" border="1" >
<tr>
<th class="hdr"><%= TriggerTooConstants.CYCLE %></th>
<th class="hdr"><%= TriggerTooConstants.PROPNUM %> </th>
<th class="hdr"><%= TriggerTooConstants.SEQNBR %> </th>
<th class="hdr"><%= TriggerTooConstants.OBSID %></th>
<th class="hdr"><%= TriggerTooConstants.TARGETNAME %></th>
<th class="hdr"><%= TriggerTooConstants.EXPTIME %></th>
<th class="hdr"><%= TriggerTooConstants.INSTRUMENT %></th>
<th class="hdr"><%= TriggerTooConstants.GRATING %></th>
<th class="hdr"><%= TriggerTooConstants.PREID %></th>
<th class="hdr"><%= TriggerTooConstants.PREMIN %></th>
<th class="hdr"><%= TriggerTooConstants.PREMAX %></th>
<th class="hdr"><%= TriggerTooConstants.PINAME %></th>
</tr>
<% if (observationList != null) {
     String rowClass="alt1";
     for (int idx=0; idx < maxCnt; idx++) {
        Observation obs = (Observation)observationList.get(idx);
        Integer obsid;
        String obsidLink = new String("");
   
        obsid = obs.getObsid();
        if (obs.isTrigger() ) {
          obsidLink = "<a href=\"/triggertoo/triggerObservation.jsp?obsid=";
          obsidLink += obsid.toString();
          obsidLink += "\">";
          obsidLink += obsid.toString();
          obsidLink += "</a>";
        }
        else {
          obsidLink = obsid.toString();
        }
        String targetName = new String("&nbsp;");
        if (obs.getTargetName() != null) {
          targetName = obs.getTargetName();
        }
        Integer ival;
        Double dval,ddval;
        String preID = "&nbsp;";
        String preMinLead;
        String preMaxLead;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        dval = obs.getRemainingExpTime();
        String expTime = nf.format(dval);

        ival = obs.getPreID();
        if (ival.intValue() > 0)  {
           preID = "Yes";
        }
        dval  = obs.getPreMinLead();
        ddval = obs.getPreMaxLead();
        if (dval.doubleValue() <= 0.0 && ddval.doubleValue() <= 0.0) {
          preMinLead = "&nbsp;";
          preMaxLead = "&nbsp;";
        }
        else {
          preMinLead = nf.format(dval.doubleValue());
          preMaxLead = nf.format(ddval.doubleValue());
        }
       
%>
<tr class="<%=rowClass%>" onmouseover="this.className='hover';" onmouseout="this.className='<%=rowClass%>';">
<td class="list"><%= obs.getCycle() %></td>
<td class="list"><%= obs.getProposalNumber() %></td>
<td class="list"><%= obs.getSequenceNumber() %></td>
<td class="list"><%= obsidLink %></td>
<td class="list"><%= targetName %></td>
<td class="list"><%= expTime %></td>
<td class="list"><%= obs.getInstrument() %></td>
<td class="list"><%= obs.getGrating() %></td>
<td class="list"><%= preID %></td>
<td class="list"><%= preMinLead %></td>
<td class="list"><%= preMaxLead %></td>
<td class="list"><%= obs.getPI() %></td>
</tr>
<% 
  if (rowClass.equals("alt1")) 
     rowClass="alt2";
  else
     rowClass="alt1";
} 
%>
<% } %>
</table>
</div>
<p>
<div class="footerDiv">
<%@ include file="cxcfooterj.html" %>
</div>

</body>
</html>

