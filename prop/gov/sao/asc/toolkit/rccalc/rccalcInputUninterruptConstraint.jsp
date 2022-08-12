<table>
 <tr>
  <td align="right" bgcolor="<%= session.getAttribute( "uninterruptLabelBGColor" ) %>">
      <a href="prop_help_rccalc.jsp#UninterruptConstraint"><b>Uninterrupt Constraint:</b></a>
  </td>
  <td>
      &nbsp;&nbsp;&nbsp;&nbsp;
      <input type="radio" name="uninterrupted" value="Yes"
             onClick="this.form.submit()"
        <%= "Yes".equals( session.getAttribute( "uninterrupted" ) ) ? "checked" : "" %>>
	<a href="prop_help_rccalc.jsp#UninterruptConstraint">Yes</a>  

      <input type="radio" name="uninterrupted" value="No"
	     onClick="this.form.submit()"
        <%= "No".equals( session.getAttribute( "uninterrupted" ) ) ? "checked" : "" %>>
        <a href="prop_help_rccalc.jsp#UninterruptConstraint">No</a>
  </td>
</tr>


<tr>
  <td align="right" bgcolor="<%= session.getAttribute("intervalLabelBGColor" ) %>">
    <a href="prop_help_rccalc.jsp#SplitConstraint"><b>Split Constraint:</b></a>
  </td>
  <td>
      &nbsp;&nbsp;&nbsp;&nbsp;

      <% if ( ((String) session.getAttribute("uninterrupted")).equals("Yes" ) ||
              ((String) session.getAttribute("groupConstraint")).equals("Yes" )) { 
           session.setAttribute("splitConstraint", "No");
      %> 
        <input disabled type="radio" name="splitConstraint" value="Yes"
          <%= "Yes".equals( session.getAttribute( "splitConstraint" ) ) ? "checked" : "" %>>
  	  <a href="prop_help_rccalc.jsp#SplitConstraint">Yes</a>  

        <input disabled type="radio" name="splitConstraint" value="No"
          <%= "No".equals( session.getAttribute( "splitConstraint" ) ) ? "checked" : "" %>>
          <a href="prop_help_rccalc.jsp#SplitConstraint">No</a>
      <% } else { %>
        <input type="radio" name="splitConstraint" value="Yes"
             onClick="this.form.submit()"
        <%= "Yes".equals( session.getAttribute( "splitConstraint" ) ) ? "checked" : "" %>>
	<a href="prop_help_rccalc.jsp#SplitConstraint">Yes</a>  

        <input type="radio" name="splitConstraint" value="No"
	     onClick="this.form.submit()"
        <%= "No".equals( session.getAttribute( "splitConstraint" ) ) ? "checked" : "" %>>
        <a href="prop_help_rccalc.jsp#SplitConstraint">No</a>

      <% } %>

  </td>
</tr>


<% if ( ((String) session.getAttribute( "splitConstraint" )).equals( "Yes" ) ) { %>
<tr>

  <td align="right" bgcolor="<%= session.getAttribute("intervalLabelBGColor" ) %>">
    <a href="prop_help_rccalc.jsp#SplitInterval"><b>Split Interval:</b></a>
  </td>
  <td>
    &nbsp;&nbsp;&nbsp;&nbsp;
    <input size="16" name="splitInterval" required value="<%=session.getAttribute( "splitInterval" ) %>"> &nbsp;&nbsp;days
  </td>
 </tr>
<% } %>


</table>
