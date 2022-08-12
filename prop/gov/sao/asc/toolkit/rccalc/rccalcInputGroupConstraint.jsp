<table>
  <tr>    
    <td align="right" bgcolor="<%= session.getAttribute("groupConstraintLabelBGColor" ) %>">
      <a href="prop_help_rccalc.jsp#GroupConstraint"><b>Group Constraint:</b></a>
    </td>
    <td>
        &nbsp;&nbsp;&nbsp;&nbsp;

	<% if ( ((String)session.getAttribute("monitorConstraint")).equals( "Yes" ) || 
	        ((String)session.getAttribute("splitConstraint")).equals( "Yes" )) { 
        %>  
           <input disabled type="radio" name="groupConstraint" value="Yes"
           	      onClick="this.form.submit()"
           <%= "Yes".equals( session.getAttribute( "groupConstraint" ) ) ?  "checked" : "" %>>
           <a href="prop_help_rccalc.jsp#GroupConstraint">Yes</a>  

           <input disabled type="radio" name="groupConstraint" value="No"
           	      onClick="this.form.submit()"
           <%= "No".equals( session.getAttribute( "groupConstraint" ) ) ?  "checked" : "" %>>
           <a href="prop_help_rccalc.jsp#GroupConstraint">No</a>
	<% } else { %>
           <input type="radio" name="groupConstraint" value="Yes"
           	      onClick="this.form.submit()"
           <%= "Yes".equals( session.getAttribute( "groupConstraint" ) ) ?  "checked" : "" %>>
           <a href="prop_help_rccalc.jsp#GroupConstraint">Yes</a>  

           <input type="radio" name="groupConstraint" value="No"
           	      onClick="this.form.submit()"
           <%= "No".equals( session.getAttribute( "groupConstraint" ) ) ?  "checked" : "" %>>
           <a href="prop_help_rccalc.jsp#GroupConstraint">No</a>
	<% } %>

    </td>
  </tr>

  <% if ( ((String) session.getAttribute( "groupConstraint")).equals( "Yes" ) ) { %>
  <tr>
    <td align="right" bgcolor="<%= session.getAttribute("groupPreMaxLabelBGColor" ) %>">
      &nbsp;&nbsp;&nbsp;&nbsp;
      <a href="prop_help_rccalc.jsp#GroupPreMaxLead"><b>Maximum Time Interval:</b></a>
    </td>
    <td>
      &nbsp;&nbsp;&nbsp;&nbsp; 
      <input size="16" name="groupPreMaxLead" required value="<%=session.getAttribute( "groupPreMaxLead" ) %>">&nbsp;&nbsp;days
    </td>
  </tr>
  <% } %>
</table>
