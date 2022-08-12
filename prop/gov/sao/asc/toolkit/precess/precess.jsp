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
<font size="+2"><b>Precess</b></font>: Coordinate Conversion and Precession Tool
</center>
<% if (useRecaptcha) { %>
<script src='https://www.google.com/recaptcha/api.js' async defer></script>
<% } %>


<%-- Execute the servlet on each post. --%>
<jsp:include page="Precess" />


<%-- Set and initialize the session values. --%>
<%@ include file = "precessSetup.jsp" %>

<% if ( ((Boolean) session.getAttribute( "inputErrors" )).booleanValue() ) { %>
  <%= session.getAttribute( "errorScript" ) %>
<% } %>

<form id="jspForm" name="jspForm" method="POST" action="precess.jsp">
<center>
	<table border="1">
	  <tr>
	    <td class="hdr">I<br>n<br>p<br>u<br>t</td>
	    <td><%@ include file = "precessInputPosition.jsp" %>
             &nbsp;&nbsp;&nbsp;&nbsp;<b>or</b> 
            <%@ include file = "precessInputResolver.jsp" %></td>
          </tr>
        </table>
	<p>
	<table border="1">
	  <tr><td class="hdr">O<br>u<br>t<br>p<br>u<br>t</td>
	  <td><%@ include file = "precessOutputControls.jsp" %></td></tr>
        </table>

<p>

<%@ include file = "buttons.jsp" %>

<%@ include file = "precessResults.jsp" %>

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
