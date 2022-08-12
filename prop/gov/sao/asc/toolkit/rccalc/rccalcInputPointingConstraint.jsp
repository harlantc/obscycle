<table>
  <tr>    
    <td align="left" bgcolor="<%= session.getAttribute("pointingConstraintLabelBGColor" ) %>">
      <a href="prop_help_rccalc.jsp#PointingConstraint"><b>Pointing Constraint:</b></a>

        &nbsp;&nbsp;&nbsp;&nbsp;

        <input type="radio" name="pointingConstraint" value="Yes"
        <%= "Yes".equals( session.getAttribute( "pointingConstraint" ) ) ? "checked" : "" %>>
        <a href="prop_help_rccalc.jsp#PointingConstraint">Yes</a>  

        <input type="radio" name="pointingConstraint" value="No"
        <%= "No".equals( session.getAttribute( "pointingConstraint" ) ) ? "checked" : "" %>>
        <a href="prop_help_rccalc.jsp#PointingConstraint">No</a>

    </td>
  </tr>

</table>
