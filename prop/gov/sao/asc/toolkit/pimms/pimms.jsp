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
<center><b><font size="+2">PIMMS</font> v4.11a</b>: with ACIS Pile up and Background Count Estimation
</center>

<% if (useRecaptcha) { %>
<script src='https://www.google.com/recaptcha/api.js' async defer></script>
<% } %>

<%-- Execute the pimms servlet on each post. --%>
<jsp:include page="Pimms" />
<script type="text/javascript" src="pimms.js"></script>

<%-- Set and initialize the session values. --%>
<%@ include file = "pimmsSetup.jsp" %>

<% if ( ((Boolean) session.getAttribute( "inputErrors" )).booleanValue() ) { %>
  <%= session.getAttribute( "errorScript" ) %>
<% } %>

<form id="jspForm" name="jspForm" method="POST" action="pimms.jsp">
<center>
<table border="0" cellspacing="10">
  <tr>
    <td>
	<table border="1">
	  <tr><td><%@ include file = "pimmsInputMode.jsp" %></td></tr>
	  <tr><td><%@ include file = "pimmsInputControls.jsp" %></td></tr>
	  <tr><td><%@ include file = "pimmsInputEnergyControls.jsp" %></td></tr>
        </table>
    </td>
    <td>
	<table border="1">
	  <tr><td><%@ include file = "pimmsOutputMode.jsp" %></td></tr>
	  <tr><td><%@ include file = "pimmsOutputControls.jsp" %></td></tr>
	  <tr><td><%@ include file = "pimmsOutputEnergyControls.jsp" %></td></tr>
        </table>
    </td>
  </tr>
</table>

<table>
  <tr>
    <td><%@ include file = "pimmsModelParameters.jsp" %></td>
  </tr>
</table>

<!-- <%@ include file = "pimmsBackgroundEstimationInput.jsp" %> -->

<p>

<%@ include file = "buttons.jsp" %>

<%@ include file= "pimmsResults.jsp" %>

</center>


</form>

<center>
<p><font size="-1"><b>
The CXC updates PIMMS and related calibration files annually in coordination
with the release of the Call for Proposals for each Chandra Cycle.  Results for
other missions included in CXC PIMMS are therefore not guaranteed correct and
up-to-date for other missions proposal time frames.
</b></font>
</center>
<p>
<p>

<% if ( "VIEW OUTPUT".equals( request.getParameter( "operation" ) ) ) { %>
  <%= session.getAttribute( "output" ) %>
<% } %>
<% } catch (Exception e){}  %>

<%@ include file = "footer.html" %>

</BODY>
</HTML>
