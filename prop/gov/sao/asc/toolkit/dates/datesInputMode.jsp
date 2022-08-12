<table>
 <tr>
  <td><a href="prop_help_dates.jsp#Convert">Convert</a> from: 
      <select size="1" name="conversionModeSelector"
	      onChange="this.form.submit()">
         <%
           for ( int i = 0; i < options.length; i++ ) { %>
             <option <%= options[i].getValue().equals( (String) session.getAttribute( "conversionModeSelector" )) ? "selected" : "" %> value="<%= options[i].getValue() %>"><%= options[i].getText() %></option>
         <% } %></select></td>
  </tr>
</table>
