<table>

  <tr>
    
    <td align="right" bgcolor="<%= session.getAttribute( "exposureTimeLabelBGColor" ) %>">
      &nbsp;&nbsp;&nbsp;&nbsp; 
      <a href="prop_help_rccalc.jsp#ExposureTime"><b>Proposed Exposure Time:</b></a>
    </td>
    <td>
      &nbsp;&nbsp;&nbsp;&nbsp; 
      <input size="16" name="propExposureTime" value="<%=session.getAttribute( "propExposureTime" ) %>"> 
      &nbsp;ks
    </td>
  </tr>


  <%@ include file="rccalcInputInstrument.jsp" %>
</table>

