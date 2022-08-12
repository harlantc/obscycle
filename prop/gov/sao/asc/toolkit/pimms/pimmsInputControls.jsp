<table>
  <tr>
    <% if ( "mission".equals( session.getAttribute( "inputMode" ) ) ) { %>
      <td><%@ include file = "pimmsInputMissionSelector.jsp" %></td>
      <td><%@ include file = "pimmsInputInstrument.jsp" %>
    <% } else { %>
      <td><%@ include file = "pimmsInputFluxSelector.jsp" %></td>
    <% } %>
  </tr>
</table>
