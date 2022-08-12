<% if ( ((String) session.getAttribute( "dataset" )).equals( "BELL" ) &&
        ((String) session.getAttribute( "velocityRangeSelector" )).equals( "Restricted" ) ) { %>
  <td align="left"><input type="text" name="velocityRangeLow" size="12"
                          value="<%= session.getAttribute( "velocityRangeLow" ) %>"></td>
  <td align="middle">to</td>
  <td><input type="text" name="velocityRangeHigh" size="12"
	     value="<%= session.getAttribute( "velocityRangeHigh" ) %>"></td>
<% } else { %>
  <td>-550.0</td>
  <td>to</td>
  <td>550.0</td>
<% } %>
  <td>km/s</td>
