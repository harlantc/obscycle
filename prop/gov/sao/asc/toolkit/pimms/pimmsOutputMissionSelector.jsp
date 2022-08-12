<table>
  <thead><tr><th align="left"bgcolor="<%= session.getAttribute( "outputMissionBGColor" ) %>"><a href="prop_help_pimms.jsp#Mission">Mission:</a></th></tr></thead>
  <tbody>
    <tr>
      <td><select size="1" name="outputMissionSelector"
	          onChange="this.form.submit()">
             <%@ include file = "pimmsMissions.jsp" %>
               <option<%= ToolkitConstants.missionChoices[i].equals( session.getAttribute( "outputMissionSelector" )) ? " selected" : "" %> value="<%=ToolkitConstants.missionChoices[i] %>"><%= ToolkitConstants.lblmissionChoices[i] %></option>
	  <% } %></select></td>
    </tr>
  </tbody>
</table>
