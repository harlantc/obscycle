<table cellpadding="2" cellspacing="2" border = "0">
  <thead>
  <tr valign="bottom">
    <th valign="bottom" align="left"><a href="prop_help_pimms.jsp#Model">Model:</a></th>
    <th  valign="bottom" align="left" bgcolor="<%= session.getAttribute( "nhLabelBGColor" ) %>">
      <a href="prop_help_pimms.jsp#NH">Galactic NH:</a>
    </th>
    <th  valign="bottom" align="left" bgcolor="<%= session.getAttribute( "redshiftLabelBGColor" ) %>">
	<a href="prop_help_pimms.jsp#Redshift">Redshift(z): </a>
    </th>
    <th align="left" bgcolor="<%= session.getAttribute( "redshiftedNHLabelBGColor" ) %>">
	<a href="prop_help_pimms.jsp#Redshift">Redshifted NH: </a>
    </th>
    <th>&nbsp;</th>
    <% if ( "PL".equals( session.getAttribute( "modelSelector" ) ) ) { %>
      <th valign="bottom" align="left" bgcolor="<%= session.getAttribute( "photonIndexLabelBGColor" ) %>">
        <a href="prop_help_pimms.jsp#Photon Index">Photon&nbsp;Index: </a>
      </th>
      <th>&nbsp;</th>
    <% } else if ( "RS".equals( session.getAttribute( "modelSelector" ) )  ||
                   "APEC".equals( session.getAttribute( "modelSelector" )) || 
                   "MEKAL".equals( session.getAttribute( "modelSelector" )) 
                 ) { %>
      <th valign="bottom" align="left"><a href="prop_help_pimms.jsp#Abundance">Abundance:&nbsp;&nbsp;</a></th>
      <th valign="bottom" align="left"><a href="prop_help_pimms.jsp#logT">&nbsp;log&nbsp;T&nbsp;|&nbsp;keV:&nbsp;</a></th>
    <% } else { %>
      <th valign="bottom" align="left" bgcolor="<%= session.getAttribute( "ktLabelBGColor" ) %>">
        <a href="prop_help_pimms.jsp#kT">kT: </a></th>
        <th>&nbsp;</th>
    <% } %>

    <% if ( showFlux( request, "Absorbed" ) ) { %>
      <th align="left" bgcolor="<%= session.getAttribute( "absorbedFluxLabelBGColor" ) %>">
        <a href="prop_help_pimms.jsp#Absorbed Flux">Absorbed&nbsp;Flux:</a>
      </th>
    <% } else if ( showFlux( request, "Unabsorbed" ) ) { %>
      <th align="left" bgcolor="<%= session.getAttribute( "unabsorbedFluxLabelBGColor" ) %>">
        <a href="prop_help_pimms.jsp#Unabsorbed Flux">Unabsorbed&nbsp;Flux:</a>
      </th>
    <% } else if ( showFluxDensity( request, "Absorbed" ) ) { %>
      <th align="left" bgcolor="<%= session.getAttribute( "absorbedFluxLabelBGColor" ) %>">
        <a href="prop_help_pimms.jsp#Absorbed FluxDensity">Absorbed&nbsp;Flux&nbsp;Density:</a>
      </th>
    <% } else if ( showFluxDensity( request, "Unabsorbed" ) ) { %>
      <th align="left" bgcolor="<%= session.getAttribute( "unabsorbedFluxLabelBGColor" ) %>">
        <a href="prop_help_pimms.jsp#Unabsorbed FluxDensity">Unabsorbed&nbsp;Flux&nbsp;Density: </a>
    <% } else { %>
      <th align="left" bgcolor="<%= session.getAttribute( "countRateLabelBGColor" ) %>">
        <a href="prop_help_pimms.jsp#Count Rate">Count&nbsp;Rate: </a>
      </th>
    <% } %>
  </tr>
  </thead>
  <tbody>
  <tr valign="top">
    <td><SELECT size="1" name="modelSelector"
                onChange="this.form.submit()">
    <%
      String[] modelChoices = {"Power Law", "Black Body", "Therm. Brems.", 
	"Plasma/APEC","Plasma/MEKAL","Plasma/Raymond Smith"};
      String[] values = {"PL", "BB", "TB", "APEC","MEKAL","RS"};
      for ( int i = 0; i < modelChoices.length; i++ )
      {
    %>
        <option<%= values[i].equals( session.getAttribute( "modelSelector" ) ) ? " SELECTED" : "" %> value="<%= values[i] %>"><%= modelChoices[i] %></option>
    <% } %>
        </select>
    </td>
    <td><input type="text" name="NH" size="8"
	       value="<%= session.getAttribute( "NH" ) %>"><br>cm**-2 
    </td>
    <td ><input type="text" name="redshift" size="8"
	         value="<%= session.getAttribute( "redshift" ) %>"> 
      </td>
      <td ><input type="text" name="redshiftedNH" size="8" 
	         value="<%= session.getAttribute( "redshiftedNH" ) %>"><br>cm**-2 
      </td>
      <td>&nbsp;</td>
    <% if ( "PL".equals( session.getAttribute( "modelSelector" ) ) ) { %>
      <td><input type="text" name="photonIndex" size="8"
                 value="<%= session.getAttribute( "photonIndex" ) %>"><br>N=AE**-a </td>
      <td>&nbsp;</td>
    <% } else if ( "RS".equals( session.getAttribute( "modelSelector" )) || 
                   "APEC".equals( session.getAttribute( "modelSelector" )) || 
                   "MEKAL".equals( session.getAttribute( "modelSelector" )) 
                 ) { %>
      <td><SELECT size="1" name="abundance">
        <%
          String[] solarAbundanceChoices = { "0.2","0.4","0.6","0.8","1.0" };
          for ( int i = 0; i < solarAbundanceChoices.length; i++ )
          {
        %>
          <option<%= solarAbundanceChoices[i].equals( session.getAttribute( "abundance" ) ) ? " SELECTED" : "" %> value="<%= solarAbundanceChoices[i] %>"><%= solarAbundanceChoices[i] %></option>
        <% } %></select><br>solar
      </td>
      <td><SELECT size="1" name="logT">
        <%
          String[] logT = {
"5.60 | 0.0343", 
"5.65 | 0.0385", 
"5.70 | 0.0432", 
"5.75 | 0.0485", 
"5.80 | 0.0544", 
"5.85 | 0.0610", 
"5.90 | 0.0684", 
"5.95 | 0.0768", 
"6.00 | 0.0862", 
"6.05 | 0.0967", 
"6.10 | 0.1085", 
"6.15 | 0.1217", 
"6.20 | 0.1366", 
"6.25 | 0.1532", 
"6.30 | 0.1719", 
"6.35 | 0.1929", 
"6.40 | 0.2165", 
"6.45 | 0.2429", 
"6.50 | 0.2725", 
"6.55 | 0.3058", 
"6.60 | 0.3431", 
"6.65 | 0.3849", 
"6.70 | 0.4319", 
"6.75 | 0.4846", 
"6.80 | 0.5437", 
"6.85 | 0.6101", 
"6.90 | 0.6845", 
"6.95 | 0.7680", 
"7.00 | 0.8617", 
"7.05 | 0.9669", 
"7.10 | 1.0849", 
"7.15 | 1.2172", 
"7.20 | 1.3658", 
"7.25 | 1.5324", 
"7.30 | 1.7194", 
"7.35 | 1.9292", 
"7.40 | 2.1646", 
"7.45 | 2.4287", 
"7.50 | 2.7250", 
"7.55 | 3.0575", 
"7.60 | 3.4306", 
"7.65 | 3.8492", 
"7.70 | 4.3189", 
"7.75 | 4.8459", 
"7.80 | 5.4372", 
"7.85 | 6.1006", 
"7.90 | 6.8450", 
"7.95 | 7.6802", 
"8.00 | 8.6173", 
"8.05 | 9.6688", 
"8.10 |10.8486", 
"8.15 |12.1723", 
"8.20 |13.6576", 
"8.25 |15.3240", 
"8.30 |17.1939", 
"8.35 |19.2918", 
"8.40 |21.6458", 
"8.45 |24.2870", 
"8.50 |27.2504", 
};
          for ( int i = 0; i < logT.length; i++ )
          {
            if ( ( "MEKAL".equals( session.getAttribute( "modelSelector" ))   && i > 7) || !"MEKAL".equals(session.getAttribute( "modelSelector" )) ) {
        %>
          <option<%= logT[i].equals( session.getAttribute( "logT" ) ) ? " SELECTED" : "" %> value="<%= logT[i] %>"><%= logT[i] %></option>
        <% } } %></select></td>


    <% } else { %>
      <td><input type="text" name="kT" size="8"
	         value="<%= session.getAttribute( "kT" ) %>"><br>keV
      </td>
      <td>&nbsp;</td>
    <% } %>
    <% if ( showFlux( request, "Absorbed" ) ) { %>
      <td><input type="text" name="absorbedFlux" size="8"
                 value="<%= session.getAttribute( "absorbedFlux" ) %>"><br>erg/cm**2/s
      </td>
    <% } else if ( showFlux( request, "Unabsorbed" ) ) { %>
      <td><input type="text" name="unabsorbedFlux" size="8"
	         value="<%= session.getAttribute( "unabsorbedFlux" ) %>"><br>erg/cm**2/s
      </td>
    <% } else if ( showFluxDensity( request, "Absorbed" ) ) { %>
      <td><input type="text" name="absorbedFlux" size="8"
                 value="<%= session.getAttribute( "absorbedFlux" ) %>"><br>erg/cm**2/s/keV </td>
    <% } else if ( showFluxDensity( request, "Unabsorbed" ) ) { %>
      <td><input type="text" name="unabsorbedFlux" size="8"
	         value="<%= session.getAttribute( "unabsorbedFlux" ) %>"><br>erg/cm**2/s/keV </td>
    <% } else { %>
      <td><input type="text" name="countRate" size="8"
	         value="<%= session.getAttribute( "countRate" ) %>"><br>cts/s </td>
    <% } %>
  </tr>
  </tbody>
</table>
<table border = "0" >
<tbody>
<tr>
    <td>
<%@ include file = "pimmsBackgroundEstimationInput.jsp" %>
</td><td>

  <table border = "0" align="center">
  <tbody>
<tr valign="middle">
    <% if ( showPileup( request ) ) { %>
        <th align="left" bgcolor="<%= session.getAttribute( "frameTimeLabelBGColor" ) %>">
        <a href="prop_help_pimms.jsp#Frame Time">Frame Time: </a>
        </th>
    <% if ( "C".equals( session.getAttribute( "frameSelector" ) ) ) { %>
      <th align="left" bgcolor="<%= session.getAttribute( "nbrchipsLabelBGColor") %>">
        <a href="prop_help_pimms.jsp#NbrChips">Nbr Chips: </a>
      </th>
      <th align="left" bgcolor="<%= session.getAttribute( "subarrayLabelBGColor") %>">
        <a href="prop_help_pimms.jsp#Subarray">Subarray: </a>
      </th>
      <% } %>
    <% } %>
</tr>
<tr>
    <% if ( showPileup( request ) ) { %>
         <td><SELECT size="1" name="frameSelector"
                onChange="this.form.submit()">
         <%
            String[] frameChoices = {"Specify", "Calculate"};
            String[] framevalues = {"S", "C"};
            for ( int i = 0; i < frameChoices.length; i++ )
            {
         %>
        <option<%= framevalues[i].equals( session.getAttribute( "frameSelector" ) ) ? " SELECTED" : "" %> value="<%= framevalues[i] %>"><%= frameChoices[i] %></option>
      <% } %>
      </select>
      <% if ( "C".equals( session.getAttribute( "frameSelector" ) ) ) { %>
       <td>
       </td>
      <td><SELECT size="1" name="nbrchipsSelector"
                onChange="">
      <%
        String[] nbrchipsChoices = {"6", "5", "4", "3", "2", "1"};
        for ( int i = 0; i < nbrchipsChoices.length; i++ )
        {
      %>
        <option<%= nbrchipsChoices[i].equals( session.getAttribute( "nbrchipsSelector" ) ) ? " SELECTED" : "" %> value="<%= nbrchipsChoices[i] %>"><%= nbrchipsChoices[i] %></option>
      <% } %>
      </select>
      </td>

      <td><SELECT size="1" name="subarraySelector"
                onChange="">
      <%
        String[] subarrayChoices = {"None", "1/2", "1/4", "1/8"};
        for ( int i = 0; i < subarrayChoices.length; i++ )
        {
      %>
        <option<%= subarrayChoices[i].equals( session.getAttribute( "subarraySelector" ) ) ? " SELECTED" : "" %> value="<%= subarrayChoices[i] %>"><%= subarrayChoices[i] %></option>
      <% } %>
      </select>
      </td>
      <% } else { %>
        <td><input type="text" name="frameTime" size="8"
                 value="<%= session.getAttribute( "frameTime" ) %>"> sec
      </td>
      <% } %>
    <% } %>
</tr>
</tbody>
</table>
</td></tr>
</table>
