<table>
  <tr>    
    <td align="right" bgcolor="<%= session.getAttribute("phaseConstraintLabelBGColor" ) %>">
      <a href="prop_help_rccalc.jsp#PhaseConstraint"><b>Phase Constraint:</b></a>
    </td>
    <td>
	&nbsp;&nbsp;&nbsp;&nbsp;
      	<input type="radio" name="phaseConstraint" value="Yes"
	       onClick="this.form.submit()"
        <%= "Yes".equals( session.getAttribute( "phaseConstraint" ) ) ?  "checked" : "" %>>
        <a href="prop_help_rccalc.jsp#PhaseConstraint">Yes</a>  

	<input type="radio" name="phaseConstraint" value="No"
               onClick="this.form.submit()"
        <%= "No".equals( session.getAttribute( "phaseConstraint" ) ) ? "checked" : "" %>>
        <a href="prop_help_rccalc.jsp#PhaseConstraint">No</a>
    </td>
  </tr>

  <% if ( ((String) session.getAttribute( "phaseConstraint")).equals( "Yes" ) ) { %>
  <tr>
    <td align="right" bgcolor="<%= session.getAttribute("phaseEpochLabelBGColor" ) %>">
      &nbsp;&nbsp;&nbsp;&nbsp;
      <a href="prop_help_rccalc.jsp#PhaseEpoch"><b>Phase Epoch:</b></a>
    </td>
    <td>
      &nbsp;&nbsp;&nbsp;&nbsp;
      <input size="16" name="phaseEpoch" required value="<%=session.getAttribute( "phaseEpoch" ) %>">&nbsp;&nbsp;MJD
    </td>
  </tr>

  <tr>
    <td align="right" bgcolor="<%= session.getAttribute("phasePeriodLabelBGColor" ) %>">
      &nbsp;&nbsp;&nbsp;&nbsp;
      <a href="prop_help_rccalc.jsp#PhasePeriod"><b>Phase Period:</b></a>
    </td>
    <td>
      &nbsp;&nbsp;&nbsp;&nbsp;
      <input size="16" name="phasePeriod" required value="<%=session.getAttribute( "phasePeriod" ) %>">&nbsp;&nbsp;days
    </td>
  </tr>

  <tr>
    <td align="right" bgcolor="<%= session.getAttribute("phaseStartLabelBGColor" ) %>">
      &nbsp;&nbsp;&nbsp;&nbsp;
      <a href="prop_help_rccalc.jsp#PhaseStart"><b>Minimum Phase:</b></a>
    </td>
    <td>
      &nbsp;&nbsp;&nbsp;&nbsp;
      <input size="16" name="phaseStart" required value="<%=session.getAttribute( "phaseStart" ) %>">
    </td>
  </tr>

  <tr>
    <td align="right" bgcolor="<%= session.getAttribute("phaseStartMarginLabelBGColor" ) %>">
      &nbsp;&nbsp;&nbsp;&nbsp;
      <a href="prop_help_rccalc.jsp#PhaseStartMargin"><b>Minimum Phase Error:</b></a>
    </td>
    <td>
      &nbsp;&nbsp;&nbsp;&nbsp;
      <input size="16" name="phaseStartMargin" required value="<%=session.getAttribute( "phaseStartMargin" ) %>">
    </td>
  </tr>

  <tr>
    <td align="right" bgcolor="<%= session.getAttribute("phaseStopLabelBGColor" ) %>">
      &nbsp;&nbsp;&nbsp;&nbsp;
      <a href="prop_help_rccalc.jsp#PhaseStop"><b>Maximum Phase:</b></a>
    </td>
    <td>
      &nbsp;&nbsp;&nbsp;&nbsp;
      <input size="16" name="phaseStop" required value="<%=session.getAttribute( "phaseStop" ) %>">
    </td>
  </tr>

  <tr>
    <td align="right" bgcolor="<%= session.getAttribute("phaseStopMarginLabelBGColor" ) %>">
      &nbsp;&nbsp;&nbsp;&nbsp;
      <a href="prop_help_rccalc.jsp#PhaseStopMargin"><b>Maximum Phase Error:</b></a>
    </td>
    <td>
      &nbsp;&nbsp;&nbsp;&nbsp;
      <input size="16" name="phaseStopMargin" required value="<%=session.getAttribute( "phaseStopMargin" ) %>">
    </td>
  </tr>

  <tr>
    <td align="right" bgcolor="<%= session.getAttribute("phaseUniqueLabelBGColor" ) %>">
      &nbsp;&nbsp;&nbsp;&nbsp;
      <a href="prop_help_rccalc.jsp#PhaseUnique"><b>Unique Phase:</b></a>
    </td>
    <td>
      &nbsp;&nbsp;&nbsp;&nbsp;
      <input type="radio" name="phaseUnique" value="Yes"
      <%= "Yes".equals( session.getAttribute( "phaseUnique" ) ) ?  "checked" : "" %>>
      <a href="prop_help_rccalc.jsp#PhaseUnique">Yes</a>  

      <input type="radio" name="phaseUnique" value="No"
      <%= "No".equals( session.getAttribute( "phaseUnique" ) ) ? "checked" : "" %>>
      <a href="prop_help_rccalc.jsp#PhaseUnique">No</a>
    </td>
  </tr>

  <% } %>

</table>
