<table>
 <% if ( "flux".equals( session.getAttribute( "inputMode" ) ) ) { %>
  <thead><tr><th align="left"><a href="prop_help_pimms.jsp#Flux">Flux:</a></th></tr></thead>
 <% } else { %>
  <thead><tr><th align="left"><a href="prop_help_pimms.jsp#Density">Flux&nbsp;Density:</a></th></tr></thead>
 <% } %>
  <tbody>
    <tr>
      <td align="center"><select size="1" name="inputFluxSelector"
                                 onChange="this.form.submit()">
      <% 
        String[] FluxChoices = {"Absorbed", "Unabsorbed"};
        for( int i = 0; i < FluxChoices.length; i++ ) { %>
          <option<%= FluxChoices[i].equals( session.getAttribute( "inputFluxSelector" )) ? " selected" : "" %> value="<%= FluxChoices[i] %>"><%= FluxChoices[i] %></option>
      <% } %></select></td>
    </tr>
  </tbody>
</table>
