  <tr>
    <td align="right"><a
      href="prop_help_rccalc.jsp#ScienceInstrument"><b>Science Instrument:</b></a>
    </td>
    <td>
      &nbsp;&nbsp;&nbsp;&nbsp;
      <input type="radio" name="instrument" value="ACIS-I" 
      	     onClick="this.form.submit()"
      <%= "ACIS-I".equals( session.getAttribute( "instrument" ) ) ? "checked" : "" %>>
      <a href="prop_help_rccalc.jsp#ScienceInstrument">ACIS-I</a>

      <input type="radio" name="instrument" value="ACIS-S"
      	     onClick="this.form.submit()"
      <%= "ACIS-S".equals( session.getAttribute( "instrument" ) ) ? "checked" : "" %>>
      <a href="prop_help_rccalc.jsp#ScienceInstrument">ACIS-S</a>

      <input type="radio" name="instrument" value="HRC-I"
      	     onClick="this.form.submit()"
      <%= "HRC-I".equals( session.getAttribute( "instrument" ) ) ? "checked" : "" %>>
      <a href="prop_help_rccalc.jsp#ScienceInstrument">HRC-I</a>

      <input type="radio" name="instrument" value="HRC-S"
      	     onClick="this.form.submit()"
      <%= "HRC-S".equals( session.getAttribute( "instrument" ) ) ? "checked" : "" %>>
      <a href="prop_help_rccalc.jsp#ScienceInstrument">HRC-S</a> &nbsp;&nbsp;
    </td>
  </tr>

  <tr>    
    <td align="right" bgcolor="<%= session.getAttribute( "requiredChipCountLabelBGColor" ) %>">
      <a href="prop_help_rccalc.jsp#RequiredChipCount"><b>Required Chip Count:</b></a>
    </td>
    <td>
      &nbsp;&nbsp;&nbsp;&nbsp; 
      <% if (("HRC-I".equals( session.getAttribute( "instrument" )) ) || ("HRC-S".equals( session.getAttribute( "instrument" ) ))) { 
           session.setAttribute( "requiredChipCount", "0" );
      %>
          <input size="16" disabled name="requiredChipCount" value="<%=session.getAttribute( "requiredChipCount" ) %>"> 
      <% } else { 
          if ( (session.getAttribute( "requiredChipCount" )) == "0" ) {
              session.setAttribute( "requiredChipCount", "4" );
          }
      %>
          <input size="16" name="requiredChipCount" value="<%=session.getAttribute( "requiredChipCount" ) %>"> 
      <% } %>
    </td>
  </tr>
