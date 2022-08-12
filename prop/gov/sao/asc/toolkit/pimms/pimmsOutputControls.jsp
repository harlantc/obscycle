<table>
  <tr>
    <% if (  "mission".equals( session.getAttribute( "outputMode" ) ) ) { %>
      <td><%@ include file = "pimmsOutputMissionSelector.jsp" %></td>
      <td><%@ include file = "pimmsOutputInstrument.jsp" %>
    <% } else { %>
      <td><%@ include file = "pimmsOutputFluxSelector.jsp" %></td>
    <% } %>
  </tr>
</table>
