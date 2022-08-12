<table>
  <thead>
    <tr>
      <th align="left"><a href="prop_help_colden.jsp#Dataset">Dataset:</a></th>
      <th>&nbsp;</th>
      <th align="left"><a href="prop_help_colden.jsp#Velocity Range">Velocity Range:</a></th>
      <th align="left" bgcolor="<%= session.getAttribute( "rangeLowLabelBGColor" ) %>">
	  <a href="prop_help_colden.jsp#Velocity Range">Low:</a>
      </th>
      <th>&nbsp;</th>
      <th align="left" bgcolor="<%= session.getAttribute( "rangeHighLabelBGColor" ) %>">
      	  <a href="prop_help_colden.jsp#Velocity Range">High:</a>
      </th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><input type="radio" name="dataset" value="NRAO"
	         onClick="this.form.submit()"
	  <%= "NRAO".equals( session.getAttribute( "dataset" ) ) ? "checked" : "" %>>
          <a href="prop_help_colden.jsp#NRAO">NRAO</a>
      </td>
      <td><input type="radio" name="dataset" value="BELL"
	         onClick="this.form.submit()"
	  <%= "BELL".equals( session.getAttribute( "dataset" ) ) ? "checked" : "" %>>
          <a href="prop_help_colden.jsp#Bell">Bell</a>
      </td>
      <%@ include file = "coldenVelocityRangeSelector.jsp" %>
      <%@ include file = "coldenVelocityRange.jsp" %>
    </tr>
  </tbody>
</table>
