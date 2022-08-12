
<% 
String pasVal= (String)session.getAttribute("pasTimeout");
int timeoutValueMinutes = -1;
int timeoutValueMS = -1;
try {
  timeoutValueMinutes = Integer.parseInt(pasVal.trim());
} catch (Exception exc) {
  timeoutValueMinutes = -1;
}
if (timeoutValueMinutes > 0) {
  timeoutValueMinutes -= 2;  // take off 2 , make sure we beat tomcat timeout
  timeoutValueMS = timeoutValueMinutes * 60 * 1000;
}


String fieldsetTimeout = "<fieldset id=\"timeoutFS\" style=\"display:none;border:0\"><legend> </legend> <span style=\"font-size:x-large; font-weight:bold; color:#cc0000;\">  WARNING .... timing out in 5 minutes </span></fieldset>";
%>

<script language="JavaScript" type="text/javascript"> 
<!-- hide from older browsers -- 

var timerId;
var timerIdw;

<% if(timeoutValueMinutes != -1) { %>
//Set the timeout value
timerId= setTimeout( 'userHasTimedOut();', <%=timeoutValueMS%> );

<% }

//warning is 5 mins before timeout
int timeoutWarningMinutes = timeoutValueMinutes - 5;
int timeoutWarningMS = -1;
if(timeoutWarningMinutes > 0) {
  timeoutWarningMS = timeoutWarningMinutes * 60 * 1000;
%>
timerIdw = setTimeout( 'showTimeoutWarning();', <%=timeoutWarningMS%>);

<% } %>

function showTimeoutWarning()
{
 showHide("timeoutFS");
 window.scrollTo(0,20);
}


function userHasTimedOut() {
        document.theform.target = "_self";
        document.theform.action = "/reports/updateReport";
        document.theform.operation.value="<%=ReportsConstants.TIMEDOUTREP%>";
        document.theform.submit();
}


// end hiding --> 
</script> 


