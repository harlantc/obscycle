<%@ page session="true" import="captcha.UseRecaptcha, info.*, java.util.Properties" %>

<% try {
  Cookie cookie = new Cookie("JSESSIONID", session.getId());
  cookie.setHttpOnly(true);
  response.addCookie(cookie);
  ServletContext sc = request.getServletContext();
  Properties toolkitProp = (Properties) sc.getAttribute("toolkit.properties");
  if (toolkitProp == null) {
    toolkitProp = new Properties();
    Toolkit.addProps(toolkitProp);
  }
  boolean useRecaptcha = UseRecaptcha.doRecaptcha(toolkitProp);

%>

<%@ include file = "toolkit.html" %>
<center>
<font size="+2"><b>Resource Cost Calculator</b></font>
</center>

<%-- Execute the rccalc servlet on each post. --%>
<jsp:include page="RCCalc" />

<% if (useRecaptcha) { %>
<script src='https://www.google.com/recaptcha/api.js' async defer></script>
<% } %>

<%-- Set and initialize the session values. --%>
<%@ include file = "rccalcSetup.jsp" %>

<% if ( ((Boolean) session.getAttribute( "inputErrors" )).booleanValue() ) { %>
  <%= session.getAttribute( "errorScript" ) %>
<% } %>

<form id="jspForm" name="jspForm" method="POST" action="rccalc.jsp">
<center>

<table border="1">
  <tr>
    <td align="center">
	<%@ include file = "rccalcInputPosition.jsp" %>
	&nbsp;&nbsp;&nbsp;&nbsp;<b>or</b> 
        <%@ include file = "rccalcInputResolver.jsp" %>
	<hr>
        <%@ include file = "rccalcInputExposure.jsp" %>
	<hr>
        <%@ include file = "rccalcInputUninterruptConstraint.jsp" %>
	<hr>
        <%@ include file = "rccalcInputCoordinatedConstraint.jsp" %>
	<hr>
	<%@ include file = "rccalcInputPhaseConstraint.jsp" %>
	<hr>
	<%@ include file = "rccalcInputPointingConstraint.jsp" %>
	<hr>
	<%@ include file = "rccalcInputGroupConstraint.jsp" %>
	<hr>
	<%@ include file = "rccalcInputMonitorConstraint.jsp" %>
	<hr>
	<%@ include file = "rccalcInputWindowConstraint.jsp" %>
	<hr>
	<%@ include file = "rccalcInputRollConstraint.jsp" %>
    </td>
  </tr>
</table>

<p>

<%@ include file = "buttons.jsp" %>

<%@ include file = "rccalcResults.jsp" %>

</center>

</form>

<p>

<% } catch (Exception e){}  %>


<%@ include file = "footer.html" %>

</BODY>
</HTML>
