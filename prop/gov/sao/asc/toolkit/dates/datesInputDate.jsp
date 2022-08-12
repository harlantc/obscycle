<table>
  <tr>
    <th align="left" bgcolor="<%= session.getAttribute( "dateLabelBGColor" ) %>">
      <a href="prop_help_dates.jsp#Date">Date:</a>
    </th>
    <td><input size="24" name="datesInputDate" 
               value="<%= session.getAttribute( "datesInputDate" ) %>"> <%= getInputDateUnits( request ) %></td>
  </tr>
</table>
