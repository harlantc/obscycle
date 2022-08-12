<table>
  <thead>
    <tr>
      <th align="left"><a href="prop_help_colden.jsp#L2">Galactic L2: </a></th>
      <th align="left"><a href="prop_help_colden.jsp#B2">B2: </a></th>
      <th align="left"><a href="prop_help_colden.jsp#NH">NH: </a></th>
      <th align="left"><a href="prop_help_colden.jsp#Comments">Comments: </a></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td align="left"><input type="text" name="resultsL2" size="16"
                              align="right" readonly
                              value="<%= session.getAttribute( "resultsL2" ) %>"></td>
      <td align="left"><input type="text" name="resultsB2" size="16" align="right"
                              readonly
                              value="<%= session.getAttribute( "resultsB2" ) %>"></td>
      <td align="left"><input type="text" name="resultsNH" size="16" align="right"
                              readonly
	                      value="<%= session.getAttribute( "resultsNH" ) %>"></td>
      <td align="left"><input type="text" name="resultsComments" size="40" align="right"
                              readonly
	                      value="<%= session.getAttribute( "resultsComments" ) %>"></td>
    </tr>
  </tbody>
</table>
<% if ( ((Boolean) session.getAttribute( "coldenWarnings" )).booleanValue() ) { %>
  <%= session.getAttribute( "coldenWarningsScript" ) %>
<% } %>

