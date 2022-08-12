<table>
  <tr>
    <td align="left" bgcolor="<%= session.getAttribute( "targetNameLabelBGColor" ) %>"><a href="prop_help_rccalc.jsp#Resolver">Target Name:</a></td>
    <td valign="top"><input size="22" name="targetName" value="<%= session.getAttribute( "targetName" ) %>"></td>
    <% 
      if (useRecaptcha) { %>
        <td> <input type="button" value="Resolve Name" 
	  onClick='jspForm.operation.value = "NAMERESOLVER"; return dorecaptcha();'></td>
    <% }  else { %>
        <td> <input type="button" value="Resolve Name" 
  	  onClick='jspForm.operation.value = "NAMERESOLVER"; this.form.submit();'></td>
    <% }  %>
  </tr>
  <tr>
    <td align="left"> <a href="prop_help_rccalc.jsp#NameResolver">Name Resolver:</a>
    </td>
    <td><select size="1" name="resolverSelector">
      <%
        String[] resolverChoices = {"SIMBAD/NED", "NED/SIMBAD", "SIMBAD", "NED"};
        for ( int i = 0; i < resolverChoices.length; i++ )
        {
      %>
          <option<%= resolverChoices[i].equals( session.getAttribute( "resolverSelector" ) ) ? " SELECTED" : "" %> value="<%= resolverChoices[i] %>"><%= resolverChoices[i] %></option>
     <% } %>
        </select>
    </td>
  </tr>
</table>



