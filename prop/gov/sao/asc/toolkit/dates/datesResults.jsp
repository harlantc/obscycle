<table>
  <tr>
    <td>
      <table>
        <thead>
          <tr>
            <th align="left">
               <a href="prop_help_dates.jsp#Calendar Date">Calendar Date:</a></th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td align="left"><input type="text" name="resultsCalendar" size="36"
                                    align="right" readonly
                                    value="<%= session.getAttribute( "resultsCalendar" ) %>"></td>
          </tr>
        </tbody>
      </table>
    </td>
    <td>
      <table>
        <thead>
          <tr>
            <th align="left">
              <a href="prop_help_dates.jsp#Chandra Time">Chandra Time:</a> </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td align="left"><input type="text" name="resultsChandraTime" size="20"
                                    align="right" readonly
                                    value="<%= session.getAttribute( "resultsChandraTime" ) %>"></td>
          </tr>
        </tbody>
      </table>
    </td>
  </tr>
</table>
<table>
  <tr>
    <td>
      <table>
        <thead>
          <tr>
            <th align="left">
               <a href="prop_help_dates.jsp#Julian Date">Julian Date:</a></th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td align="left"><input type="text" name="resultsJulian" size="24"
                                    align="right" readonly
                                    value="<%= session.getAttribute( "resultsJulian" ) %>"></td>
          </tr>
        </tbody>
      </table>
    </td>
    <td>
      <table>
        <thead>
          <tr>
            <th align="left">
              <a href="prop_help_dates.jsp#Modified Julian Date">Modified Julian Date:</a> </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td align="left"><input type="text" name="resultsJulianModified" size="22"
                                    align="right" readonly
                                    value="<%= session.getAttribute( "resultsJulianModified" ) %>"></td>
          </tr>
        </tbody>
      </table>
    </td>
    <td>
      <table>
        <thead>
          <tr>
            <th align="left">
              <a href="prop_help_dates.jsp#Day of Year">Day of Year:</a> </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td align="left"><input type="text" name="resultsDayOfYear" size="16"
                                    align="right" readonly
                                    value="<%= session.getAttribute( "resultsDayOfYear" ) %>"></td>
          </tr>
        </tbody>
      </table>
    </td>
  </tr>
</table>

