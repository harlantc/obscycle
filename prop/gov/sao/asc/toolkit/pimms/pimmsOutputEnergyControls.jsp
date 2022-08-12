<table>
  <tr>
    <th align="left" bgcolor="<%= session.getAttribute( "outputEnergyLowLabelBGColor" ) %>">
      <a href="prop_help_pimms.jsp#Output Energy">Output Energy:</a>
    </th>
  <% if ( "mission".equals( session.getAttribute( "outputMode" ) ) ) { %>
      <td align="left"><input type="text" name="outputEnergyLow" size="8"
             value="<%= session.getAttribute( "outputEnergyLow" ) %>">
      <input type="hidden" name="outputFluxEnergyLow" size="8" value="<%= session.getAttribute( "outputFluxEnergyLow" ) %>">
        <input type="hidden" name="outputDensityEnergyLow" size="8" value="<%= session.getAttribute( "outputDensityEnergyLow" ) %>">
	</td>
    <td align="center" bgcolor="<%= session.getAttribute( "outputEnergyHighLabelBGColor" ) %>">to</td>
    <td><input type="text" name="outputEnergyHigh" size="8"
	       value="<%= session.getAttribute( "outputEnergyHigh" ) %>">
       <input type="hidden" name="outputFluxEnergyHigh" size="8" value="<%= session.getAttribute( "outputFluxEnergyHigh" ) %>">
        <input type="hidden" name="outputDensityEnergyHigh" size="8" value="<%= session.getAttribute( "outputDensityEnergyHigh" ) %>">
	</td>
  <% }else if ( "flux".equals( session.getAttribute( "outputMode" ) ) ) { %>
      <td align="left"><input type="text" name="outputFluxEnergyLow" size="8"
             value="<%= session.getAttribute( "outputFluxEnergyLow" ) %>">
       <input type="hidden" name="outputEnergyLow" size="8" value="<%= session.getAttribute( "outputEnergyLow" ) %>">
        <input type="hidden" name="outputDensityEnergyLow" size="8" value="<%= session.getAttribute( "outputDensityEnergyLow" ) %>">
	</td>
    <td align="center" bgcolor="<%= session.getAttribute( "outputEnergyHighLabelBGColor" ) %>">to</td>
    <td><input type="text" name="outputFluxEnergyHigh" size="8"
	       value="<%= session.getAttribute( "outputFluxEnergyHigh" ) %>">
       <input type="hidden" name="outputEnergyHigh" size="8" value="<%= session.getAttribute( "outputEnergyHigh" ) %>">
        <input type="hidden" name="outputDensityEnergyHigh" size="8" value="<%= session.getAttribute( "outputDensityEnergyHigh" ) %>">
	</td>
    <% } else { %>
      <td align="left"><input type="text" name="outputDensityEnergyLow" size="8"
             value="<%= session.getAttribute( "outputDensityEnergyLow" ) %>">
       <input type="hidden" name="outputEnergyLow" size="8" value="<%= session.getAttribute( "outputEnergyLow" ) %>">
        <input type="hidden" name="outputEnergyHigh" size="8" value="<%= session.getAttribute( "outputEnergyHigh" ) %>">
        <input type="hidden" name="outputFluxEnergyLow" size="8" value="<%= session.getAttribute( "outputFluxEnergyLow" ) %>">
        <input type="hidden" name="outputFluxEnergyHigh" size="8" value="<%= session.getAttribute( "outputFluxEnergyHigh" ) %>">
	</td>
    <td align="left">keV</td>
    <% } %>
    <% if ( showDefault( request, "outputMode" ) ) { %>
	<td align="right"><input type="button" name="setDefaultOutput" value="Default"
                                 onClick="setDefaultOutputEnergyLevels( this.form )"></td>
    <% } %>
  </tr>
</table>

