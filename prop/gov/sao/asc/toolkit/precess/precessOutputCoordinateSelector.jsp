<table>
  <thead><tr><th align="left"><a href="prop_help_precess.jsp#Coordinate">Coordinate System:</a></th></tr></thead>
  <tbody>
    <tr>
      <td><select size="1" name="outputCoordinateSelector" onChange="this.form.submit()"> 
	  <%@ include file = "precessOutputCoordinateSystems.jsp" %>
               <option <%= coordinateSystems[i].equals( session.getAttribute( "outputCoordinateSelector" )) ? "selected" : "" %> value="<%= coordinateSystems[i] %>"><%= coordinateSystems[i] %></option>
	  <% } %></select></td>
    </tr>
  </tbody>
</table>
