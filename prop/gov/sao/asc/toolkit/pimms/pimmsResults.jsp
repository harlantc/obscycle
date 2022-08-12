<% 
  String units = "";
  if ( "flux".equals( session.getAttribute( "outputMode" ) ) ) { 
    if ( isOutputFlux( request, "Absorbed" ) ) { 
      units = "erg/cm**2/s&nbsp;absorbed&nbsp;flux";
    } else { 
      units = "erg/cm**2/s&nbsp;unabsorbed&nbsp;flux";
    } 
  } else if ( "density".equals(session.getAttribute("outputMode" ))) { 
    if ( isOutputFlux( request, "Absorbed" ) ) { 
      units = "erg/cm**2/s/keV&nbsp;absorbed&nbsp;flux&nbsp;density";
    } else { 
      units = "erg/cm**2/s/keV&nbsp;unabsorbed&nbsp;flux&nbsp;density";
    } 
  }  else {
    units = "cts/sec&nbsp;count&nbsp;rate";
  }

%>

</center>
<font size="-1"><b><%=session.getAttribute("notemsg") %></b></font>
<font size="-1" color="red"><%= session.getAttribute( "warningmsg" ) %> </font>
<center>
<table border=0>
  <thead>
    <tr>
      <th align="left"><a href="prop_help_pimms.jsp#PIMMS Prediction">PIMMS Prediction: </a></th>
      <% if ( showPileup( request ) ) { %>
        <th align="left" ><a href="prop_help_pimms.jsp#% Pileup">Pileup: </a> </th>
        <th align="left" colspan="2"> <a href="prop_help_pimms.jsp#cts/frame after Pileup">Predicted piled count rate: </a> </th>
      <% } %>
      <% if ( showEstimationOutput( request ) ) { %>
        <th align="left">
	    <a href="prop_help_pimms.jsp#Background Count Rate">Background Count Rate:</a>
	</th> 
      <% } %>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td align="center"><input type="text" name="pimmsPrediction" align="right"
          readonly value="<%= session.getAttribute( "pimmsPrediction" ) %>">
	<br><font class="unitFont"><%= units %></font>
      </td>
      <% if ( showPileup( request ) ) { %>
        <td align="center"><input type="text" name="pileup" size="8" align="right"
             readonly value="<%= session.getAttribute( "pileup" ) %>">
		<br> %</td>
        <td align="center"><input type="text" name="countsPerFrame" size="12" align="right"
             readonly value="<%= session.getAttribute( "countsPerFrame" ) %>">
	<br><font class="unitFont">cts/frame</font> </td>
        <td align="center"><input type="text" name="countPerSecond" size="12" align="right"
                 readonly
                 value="<%= session.getAttribute( "countsPerSecond" ) %>">
	<br> <font class="unitFont">cts/sec</font> </td>
      <% } %>
      <% if ( showEstimationOutput( request ) ) { %>
        <% if ( "".equals( session.getAttribute( "backgroundCountRate" ) ) ||
		isANumber( (String) session.getAttribute( "backgroundCountRate" ) ) ) { %>
          <td align="center"><input type="text" size="22" readonly name="backgroundCountRate"
	        value="<%= session.getAttribute( "backgroundCountRate" ) %>"><br> <font class="unitFont">cts/sec</font> </td>
        <% } else { %>
          <td align="center"><%= session.getAttribute( "backgroundCountRate" ) %></td>
        <% } %>
      <% } %>
    </tr>
  </tbody>
</table>

<% if ( ((Boolean) session.getAttribute( "pimmsWarnings" )).booleanValue() ) { %>
  <%= session.getAttribute( "pimmsWarningsScript" ) %>
<% } %>
