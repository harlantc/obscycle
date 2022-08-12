<%@ page session="true" import="java.util.*,java.text.SimpleDateFormat, info.*, org.apache.commons.lang3.*" %>
<%@ page import="info.TriggerTooConstants" %>

<% 

  try {
   String message = (String)session.getAttribute("message"); 
   String cdoText = (String)session.getAttribute("cdoText"); 
   String obsid = StringEscapeUtils.escapeHtml4((String)request.getParameter("obsid") );
   String seqnbr = StringEscapeUtils.escapeHtml4((String)request.getParameter("seqnbr")); 
   String propnum = StringEscapeUtils.escapeHtml4((String)request.getParameter("propnum")); 
   String pilast = StringEscapeUtils.escapeHtml4((String)request.getParameter("pilast")); 
   String xx = StringEscapeUtils.escapeHtml4((String)request.getParameter("xx") );
   if (xx != null && xx.length() > 0) message = xx;

   if (message == null) { message = ""; }
   if (cdoText == null) { cdoText = ""; }
   if (obsid == null)   { obsid = ""; }
   if (seqnbr == null)  { seqnbr = ""; }
   if (propnum == null) { propnum = ""; }
   if (pilast == null)  { pilast = ""; }

   Boolean usecaptcha = (Boolean)session.getAttribute("usecaptcha");
   if (usecaptcha == null) usecaptcha = new Boolean(true);

   String todayStr="";
   try {
     Date today = new Date();
     SimpleDateFormat dFormat = new SimpleDateFormat("E MMM dd YYYY  hh:mm a");
     todayStr= "The local time in Cambridge,MA is " + dFormat.format(today);
     //todayStr = "The local time in Cambridge, MA is " +  today.toString();
   } catch (Exception ex) {
   }


%>

<%@ include file = "triggertooHead.html" %>

<script type="text/javascript">
function onSubmit(token) {
document.getElementById("trigForm").submit();
}
</script>

<body>
<% if (usecaptcha) { %>
<script src='https://www.google.com/recaptcha/api.js' async defer></script>
<% } %>
<script type="text/javascript" src="triggertoo.js"></script>
<div class="topDiv">
  <div id="cxcheaderplain">
    <div class="menudiv">
      <div class="hdrleft">
        <a href="/" target="_top"><img src="/soft/include/cxcheaderlogo.png" alt="CXC Home Page" border="0"></a>
      </div>
      <div class="hdrcenter">
        <img src="/soft/include/blank.gif" alt="" width="1" height="50">
        <font class="mainhdr"> Chandra TOO Trigger Search Page </font>
      </div>
      <div class="hdrright">
        <a class="mainlink3" href="http://cxc.harvard.edu/helpdesk">HelpDesk</a>
      </div>
       
    </div>
  </div>
<form id="trigForm" name="trigForm" method="post" action="/triggertoo/triggerTOO.jsp" >
<p>
<div class="msgbar">
 <%=cdoText%>
<span style="color:#dd0000;font-weight:bold;">Triggering pages CXC staff: Please read and time your triggering  appropriately.</span> 
<span style="color:#000000;font-weight:bold;"><%=todayStr %></span>
<p>
Submission of TOO triggers and DDT requests with response windows
&lt;= 5 days will trigger pagers/phones for selected CXC
personnel - some 24 hrs per day 7 days per week and some between 8-22H
(EDT/EST) 7 days per week. <b>Please be sure that your program requires
rapid turn-around before submitting requests with response windows
starting at 5 days or less, and to the extent possible please submit
such requests during normal EDT/EST weekday working hours.</b>
<p>
TOOs/DDT requests with Medium turn-around times having response 
windows from 5 to 30 days trigger pagers/phones on weekend days as well
as on work days from 8-22H EDT/EST. <b>To the extent possible please
submit such triggers/requests during normal EDT/EST weekday working hours.</b>
<p>
Peer-reviewed TOOs are TOO proposals that were submitted and accepted 
as a result of the Chandra Call for Proposals (CfP). Since their 
scientific objectives have already been approved, we require only 
confirmation of a few details and  a brief description of how the trigger 
criteria has been met.  Please use the form below to identify the TOO
you wish to trigger.  
</p>

<p>Unanticipated TOOs are charged to Director's Discretionary Time. 
These will not have not been previously accepted through the Chandra Call 
for Proposals. For the unanticipated TOOs, a compelling scientific case 
must be made for inserting them into the mission timeline. 
To submit an RfO (Request for Observation) of this type, please 
see the instructions for <a href="http://cxc.harvard.edu/proposer/Chandra_RfO.html">Director's Discretionary Time</a>.
</div>
<p>
<input class="errmsg" name="errmsg" type="text" size="80" maxlength="500" value="<%= message %>" >
<br>
Please identify the TOO Observation you wish to trigger by specifying the
search criteria using <b>ONE</b> of the fields below. 
<table  cellpadding="1" cellspacing="0">
<tbody>
<tr>
<th class="label"><a href="triggertooHelp.jsp#obsid">Observation ID: </a></th>
<td class="field"><input size="6" maxlength="6" name="obsid" value="<%=obsid %>"></td>
</tr>
<tr> <td colspan="2">&nbsp;-or-</td> </tr>
<tr>
<th class="label"><a href="triggertooHelp.jsp#seqnbr">Sequence Number: </a></th>
<td class="field"><input size="8" maxlength="10" name="seqnbr" value="<%=seqnbr %>"></td>
</tr>
<tr> <td colspan="2">&nbsp;-or-</td> </tr>
<tr>
<th class="label"><a href="triggertooHelp.jsp#propnum">Proposal Number: </a></th>
<td class="field"><input size="8" maxlength="10" name="propnum" value="<%=propnum %>"></td>
</tr>
<tr> <td colspan="2">&nbsp;-or-</td> </tr>
<tr>
<th class="label"><a href="triggertooHelp.jsp#pi"><%= TriggerTooConstants.PILAST%>: </a></th>
<td class="field"><input size="27" maxlength="60" name="pilast" value="<%=pilast %>"> </td>
</tr>
</tbody>
</table>
<p> 
<% if (usecaptcha) { %>
<div id='recaptcha' class="g-recaptcha"
          data-sitekey="<%=TriggerTooConstants.SITE_KEY%>"
          data-callback="onSubmit"
          data-size="invisible">
</div>

<input class="submitBtn" type="submit" name="Submit" value="Search" onclick="if (validateSearchCriteria(this.form)) { grecaptcha.execute();} return false;" >
<% } else { %>
  <input class="submitBtn" type="submit" name="Submit" value="Search" onclick="return validateSearchCriteria(this.form);" >
<% } %>
<input class="submitBtn" type="button" name="Clear" value="Clear"  onclick='clearFields(this.form);return false;' >
</p>
</form>
</div>
<div class="footerDiv">
<%@ include file="cxcfooterj.html" %>
</div>

</body>
</html>
<% } catch (Exception e) {
   }
%>

