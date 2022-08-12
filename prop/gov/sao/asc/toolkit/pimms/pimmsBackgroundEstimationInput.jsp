<% if ( showEstimationInput( request ) ) { %>
  <table border = "0">
      <thead><tr>
        <th><a href="prop_help_pimms.jsp#Source">Source:</a> </th>
        <% if ( "extended source".equals( session.getAttribute( "source" ) ) ) { %>
          <th align="left"><a href="prop_help_pimms.jsp#Size">Size:</a></th>
        <% } %>
      </tr></thead>
      <tbody><tr>
        <td align="center"><select size="1" name="source" onChange="this.form.submit()">
	  <%
            String[] sourceChoices = {"point source", "extended source" };
            for ( int i = 0; i < sourceChoices.length; i++ ) { %>
              <option<%= sourceChoices[i].equals( session.getAttribute( "source" ) ) ? " selected" : "" %> value="<%= sourceChoices[i] %>"><%= sourceChoices[i] %></option>
         <% } %></select></td>
        <% if ( "extended source".equals( session.getAttribute( "source" ) ) ) { %>
	  <td align="center"><input type="text" name="extendedSize" size="8"
	                            value="<%= session.getAttribute( "extendedSize" ) %>">
	   arcsec^2</td>
        <% } %>
    </tr> </tbody>
  </table>
<% } %>
