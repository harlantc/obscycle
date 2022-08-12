<% if ( displayPosition( request ) ) { %>
  <%@ include file = "precessPositionResults.jsp" %>
<% } else { %>
  <%@ include file = "precessConstellationResults.jsp" %>
<% } %>

<% if ( ((Boolean) session.getAttribute( "precessWarnings" )).booleanValue() ) { %>
  <%= session.getAttribute( "precessWarningsScript" ) %>
<% } %>

