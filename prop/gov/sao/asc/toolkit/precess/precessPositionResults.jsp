<table>
  <thead>
    <tr>
      <% if ( !"Galactic".equals( (String) session.getAttribute( "outputCoordinateSelector" ) ) ) { %>
        <th align="left"><a href="prop_help_precess.jsp#Equinox">Equinox:</a></th>
      <% } %>
      <th align="left"><%= getFirstPositionLabel( request, "outputCoordinateSelector" ) %>:</a></th>
      <th></th>
      <th align="left"><%= getSecondPositionLabel( request, "outputCoordinateSelector" ) %>:</a></th>
      <th></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <% if ( !"Galactic".equals( (String) session.getAttribute( "outputCoordinateSelector" ) ) ) { %>
        <td align="left"><input type="text" name="resultsEquinox" size="8"
                              align="right" readonly
                              value="<%= session.getAttribute( "resultsEquinox" ) %>"></td>
      <% } %>
      <td align="left"><input type="text" name="resultsFirstSexagesimal" size="16"
                              align="right" readonly
                              value="<%= session.getAttribute( "resultsFirstSexagesimal" ) %>">
      </td>
      <td> HH MM SS.SS </td>
      <td align="left"><input type="text" name="resultsSecondSexagesimal" size="16"
	                      align="right" readonly
	                      value="<%= session.getAttribute( "resultsSecondSexagesimal" ) %>">
      </td>
      <td> sDD MM SS.SS </td>
    </tr>
    <tr>
      <% if ( !"Galactic".equals( (String) session.getAttribute( "outputCoordinateSelector" ) ) ) { %>
        <td></td>
      <% } %>
      <td align="left"><input type="text" name="resultsFirstDecimal" size="16" align="right"
                              readonly
                              value="<%= session.getAttribute( "resultsFirstDecimal" ) %>">
      </td>
      <td> decimal degrees</td>
      <td align="left"><input type="text" name="resultsSecondDecimal" size="16" align="right"
                              readonly
	                      value="<%= session.getAttribute( "resultsSecondDecimal" ) %>">
      </td>
      <td> decimal degrees</td>
    </tr>
  </tbody>
</table>
