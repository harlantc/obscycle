<table>
  <thead><tr>
    <th align="left"><a href="prop_help_colden.jsp#Coordinate">Coordinate System:</a></th>
    <% if ( needsEquinox( request, "inputCoordinateSelector", "equinox" ) ) { %>
	<th align="left" bgcolor="<%= session.getAttribute( "equinoxLabelBGColor" ) %>">
	  <a href="prop_help_colden.jsp#Equinox">Equinox:</a>
	  </th>
    <% } %>
    <th align="left" bgcolor="<%= session.getAttribute( "position1LabelBGColor" ) %>">
        <%= getFirstPositionLabel( request, "inputCoordinateSelector" ) %>:</a>
    </th>
    <th align="left" bgcolor="<%= session.getAttribute( "position2LabelBGColor" ) %>">
        <%= getSecondPositionLabel( request, "inputCoordinateSelector" ) %>:</a>
    </th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td valign="top"><select size="1" name="inputCoordinateSelector"
                  onChange="this.form.submit()"> 
	  <%@ include file = "coldenCoordinateSystems.jsp" %>
               <option <%= coordinateSystems[i].equals( session.getAttribute( "inputCoordinateSelector" )) ? "selected" : "" %> value="<%= coordinateSystems[i] %>"><%= coordinateSystems[i] %></option>
	  <% } %></select>
      </td>
      <% if ( needsEquinox( request, "inputCoordinateSelector", "equinox" ) ) { %>
      <td valign="top"><input size="12" name="equinox" value="<%= session.getAttribute( "equinox" ) %>"></td>
      <% } %>
      <td valign="top"><input size="16" name="inputPosition1"
                 value="<%= session.getAttribute( "inputPosition1" ) %>">
        <br><font size="-1"><i>DDD.DD</i> or <i>HH&nbsp;MM&nbsp;SS.ss</i></font>
      </td>
      <td valign="top"><input size="16" name="inputPosition2"
                 value="<%= session.getAttribute( "inputPosition2" ) %>">
        <br><font size="-1"><i>sDD.DD</i> or <i>sDD&nbsp;MM&nbsp;SS.ss</i></font>
      </td>
    </tr>
  </tbody>
</table>



