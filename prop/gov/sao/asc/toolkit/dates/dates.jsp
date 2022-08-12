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
<font size="+2"><b>Dates</b></font>: Date Conversion Tool
</center>
<%-- Execute the dates servlet on each post. --%>
<jsp:include page="Dates" />
<% if (useRecaptcha) { %>
<script src='https://www.google.com/recaptcha/api.js' async defer></script>
<% } %>

<%-- Set and initialize the session values. --%>
<%@ include file = "datesSetup.jsp" %>

<% if ( ((Boolean) session.getAttribute( "inputErrors" )).booleanValue() ) { %>
  <%= session.getAttribute( "errorScript" ) %>
<% } %>

<form id="jspForm" name="jspForm" method="POST" action="dates.jsp">
<center>

<table border="1">
  <tr>
    <td>
	<table border="0">
	  <tr><td><%@ include file = "datesInputMode.jsp" %></td></tr>
	  <tr><td><%@ include file = "datesInputDate.jsp" %></td></tr>
        </table>
    </td>
  </tr>
</table>

<p>

<%@ include file = "buttons.jsp" %>

<%@ include file = "datesResults.jsp" %>

</center>

</form>

<p>

<% if ( "VIEW OUTPUT".equals( request.getParameter( "operation" ) ) ) { %>
  <%= session.getAttribute( "output" ) %>
<% } %>
<% } catch (Exception e){}  %>


<%@ include file = "footer.html" %>

</BODY>
</HTML>
