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
<font size="+2"><b>Colden</b></font>: Galactic Neutral Hydrogen Density Calculator
</center>

<% if (useRecaptcha) { %>
<script src='https://www.google.com/recaptcha/api.js' async defer></script>
<% } %>

<%-- Execute the servlet on each post. --%>
<jsp:include page="Colden" />


<%-- Provide (import) common useful functions for JSP processing. --%>
<%@ include file = "coldenSetup.jsp" %>

<% if ( ((Boolean) session.getAttribute( "inputErrors" )).booleanValue() ) { %>
  <%= session.getAttribute( "errorScript" ) %>
<% } %>

<form id="jspForm" name="jspForm" method="POST" action="colden.jsp">
<center>
<table border="0">
  <tr>
    <td>
	<table border="1">
	  <tr><td><%@ include file = "coldenInputPosition.jsp" %>
	   &nbsp;&nbsp;&nbsp;&nbsp;<b>or</b> 
	  <%@ include file = "coldenInputResolver.jsp" %></td></tr>
	  <tr><td><%@ include file = "coldenVelocity.jsp" %></td></tr>
        </table>
    </td>
  </tr>
</table>

<p>

<%@ include file = "buttons.jsp" %>

<%@ include file= "coldenResults.jsp" %>

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
