<table>
  <tr>
    <td><%@ include file = "precessOutputCoordinateSelector.jsp" %></td>
    <% if ( needsEquinox( request, "outputCoordinateSelector", "outputEquinox" ) ) { %>
      <td><%@ include file = "precessOutputEquinox.jsp" %></td>
    <% } %>
  </tr>
</table>
