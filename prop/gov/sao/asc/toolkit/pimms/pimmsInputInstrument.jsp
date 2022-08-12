<table>
  <thead><tr><th align="left" bgcolor="<%= session.getAttribute("inputInstBGColor")%>" ><a href="prop_help_pimms.jsp#Mission">Detector/Grating/Filter:</a></th></tr></thead>
  <tbody><tr><td><select size="1" name="inputInstrument"
                         onChange="setDefaultInputEnergyLevels( this.form )">
                 <% String mission = (String) session.getAttribute( "inputMissionSelector" ); %>
	         <%@ include file = "pimmsInstruments.jsp" %>
                   <option <%= instrumentChoices[i].equals( session.getAttribute( "inputInstrument" )) ? "selected" : "" %> value="<%= instrumentChoices[i] %>"><%= instrumentChoices[i] %></option>
                 <% } %></select></td></tr></tbody>
</table>
