<table>
<tr>
  <th align="right"><a href="prop_help_rccalc.jsp#CoordinatedObs">Coordinated Observation:</a></th>
  <td>
      &nbsp;&nbsp;&nbsp;&nbsp;
      <input type="radio" name="coordinatedObs" value="Yes"
             onClick="this.form.submit()"
        <%= "Yes".equals( session.getAttribute( "coordinatedObs" ) ) ? "checked" : "" %>>
	<a href="prop_help_rccalc.jsp#CoordinatedObs">Yes</a>  

      <input type="radio" name="coordinatedObs" value="No"
	     onClick="this.form.submit()"
        <%= "No".equals( session.getAttribute( "coordinatedObs" ) ) ? "checked" : "" %>>
        <a href="prop_help_rccalc.jsp#CoordinatedObs">No</a>
  </td>
</tr>

<% if ( ((String) session.getAttribute( "coordinatedObs" )).equals( "Yes" ) ) { %>
<tr>
    <td align="right" bgcolor="<%= session.getAttribute("coordinatedObsLabelBGColor" ) %>">
      <a href="prop_help_rccalc.jsp#CoordinationWindow"><b>Coordination Window:</b></a>
    </td>
    <td>
      &nbsp;&nbsp;&nbsp;&nbsp;
      <input size="16" name="obsInterval" required value="<%=session.getAttribute( "obsInterval" ) %>">&nbsp;&nbsp;days 
  </td>
 </tr>
<% } %>

</table>
