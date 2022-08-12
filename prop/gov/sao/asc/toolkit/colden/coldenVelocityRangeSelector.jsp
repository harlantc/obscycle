<% if ( ((String) session.getAttribute( "dataset" )).equals( "BELL" ) ) { %>
  <td><select size="1" name="velocityRangeSelector"
              onChange="this.form.submit()">
     <%
	String[] ranges = { "Full", "Restricted" };
	for ( int i = 0; i < ranges.length; i++ ) { 
     %>
	<option <%= ranges[i].equals( session.getAttribute( "velocityRangeSelector" )) ? "selected" : "" %> value="<%= ranges[i] %>"><%= ranges[i] %></option>
	<% } %></select></td>
<% } else { %>
  <td><center>Full</center></td>
<% } %>
