<table>
  <thead>
    <tr>
      <th align="center"><a href="prop_help_rccalc.jsp#ResourceCost"> Resource Cost: </a></th>
    </tr>
  </thead>
  <tbody>
    <tr> 
    <% 
        String operation = (String)session.getAttribute("operation");
	if (operation.equals( "VIEW OUTPUT" )) {
    %>
      <td align="center">
        <input autofocus type="text" name="resultsNormalizedCost" size="30"
      	  align="right" readonly  
          value="<%= session.getAttribute( "resultsNormalizedCost" ) %>">
      </td>
    <%  } else { %>
      <td align="center">
        <input type="text" name="resultsNormalizedCost" size="30"
      	  align="right" readonly 
          value="<%= session.getAttribute( "resultsNormalizedCost" ) %>">
      </td>
    <% } %>

    </tr>
  </tbody>
</table>
<% if ( ((Boolean) session.getAttribute( "rccalcWarnings" )).booleanValue() ) { %>
  <%= session.getAttribute( "rccalcWarningsScript" ) %>
<% } %>

