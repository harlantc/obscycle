<table>
  <thead><tr><th align="left" bgcolor="<%= session.getAttribute("outputInstBGColor")%>"><a href="prop_help_pimms.jsp#Mission">Detector/Grating/Filter:</a></th></tr></thead>
  <tbody>
    <tr>
      <td>
        <select size="1" name="outputInstrument"
                onChange="setDefaultOutputEnergyLevels( this.form ); this.form.submit()">
                 <% String mission = (String) session.getAttribute( "outputMissionSelector" ); %>
                 <%@ include file = "pimmsInstruments.jsp" %>
                   <option <%= instrumentChoices[i].equals( session.getAttribute( "outputInstrument" )) ? "selected" : "" %> value="<%= instrumentChoices[i] %>"><%= instrumentChoices[i] %></option>
                 <% } %>
        </select>
      </td>
    </tr>
  </tbody>
</table>
