<table>

  <tr>
    <th align="left" bgcolor="<%= session.getAttribute( "inputEnergyLowLabelBGColor" ) %>">
      <a href="prop_help_pimms.jsp#Input Energy">Input Energy:</a>
    </th>
    <% if ("mission".equals( session.getAttribute( "inputMode" ) ) ) { %>
      <td align="left">
	<input type="text" name="inputEnergyLow" size="8" value="<%= session.getAttribute( "inputEnergyLow" ) %>">
	<input type="hidden" name="inputFluxEnergyLow" size="8" value="<%= session.getAttribute( "inputFluxEnergyLow" ) %>">
	<input type="hidden" name="inputDensityEnergyLow" size="8" value="<%= session.getAttribute( "inputDensityEnergyLow" ) %>">
      </td>
      <td align="center" bgcolor="<%= session.getAttribute( "inputEnergyHighLabelBGColor" ) %>">to</td>
      <td><input type="text" name="inputEnergyHigh" size="8"
	       value="<%= session.getAttribute( "inputEnergyHigh" ) %>">
	<input type="hidden" name="inputFluxEnergyHigh" size="8" value="<%= session.getAttribute( "inputFluxEnergyHigh" ) %>">
	<input type="hidden" name="inputDensityEnergyHigh" size="8" value="<%= session.getAttribute( "inputDensityEnergyHigh" ) %>">
 	</td>
    <% } else if ("flux".equals( session.getAttribute( "inputMode" ) ) ) { %>
      <td align="left"><input type="text" name="inputFluxEnergyLow" size="8"
                            value="<%= session.getAttribute( "inputFluxEnergyLow" ) %>">
	<input type="hidden" name="inputEnergyLow" size="8" value="<%= session.getAttribute( "inputEnergyLow" ) %>">
	<input type="hidden" name="inputDensityEnergyLow" size="8" value="<%= session.getAttribute( "inputDensityEnergyLow" ) %>">
	</td>
      <td align="center" bgcolor="<%= session.getAttribute( "inputEnergyHighLabelBGColor" ) %>">to </td>
      <td><input type="text" name="inputFluxEnergyHigh" size="8"
	       value="<%= session.getAttribute( "inputFluxEnergyHigh" ) %>">
	<input type="hidden" name="inputEnergyHigh" size="8" value="<%= session.getAttribute( "inputEnergyHigh" ) %>">
	<input type="hidden" name="inputDensityEnergyHigh" size="8" value="<%= session.getAttribute( "inputDensityEnergyHigh" ) %>">
	</td>
    <% } else {%>
      <td align="left"><input type="text" name="inputDensityEnergyLow" size="8"
                            value="<%= session.getAttribute( "inputDensityEnergyLow" ) %>">
	<input type="hidden" name="inputEnergyLow" size="8" value="<%= session.getAttribute( "inputEnergyLow" ) %>">
	<input type="hidden" name="inputEnergyHigh" size="8" value="<%= session.getAttribute( "inputEnergyHigh" ) %>">
	<input type="hidden" name="inputFluxEnergyLow" size="8" value="<%= session.getAttribute( "inputFluxEnergyLow" ) %>">
	<input type="hidden" name="inputFluxEnergyHigh" size="8" value="<%= session.getAttribute( "inputFluxEnergyHigh" ) %>">
	</td>
    <% } %>
    <td align="left">keV</td>
    <% if ( showDefault( request, "inputMode" ) ) { %>
	<td align="right"><input type="button" name="setDefaultInput" value="Default"
                                 onClick="setDefaultInputEnergyLevels( this.form )"></td>
    <% } %>
  </tr>
</table>
