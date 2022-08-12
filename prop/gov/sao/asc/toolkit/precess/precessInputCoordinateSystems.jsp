<%
   String outputSelection = (String) session.getAttribute( "outputCoordinateSelector" );
   if ( outputSelection.startsWith( "Ecliptic" ) )
   {
     coordinateSystems = systemsWithoutEclipticWithoutConstellation;
   }
   else
   {
     coordinateSystems = systemsWithEclipticWithoutConstellation;
   }
   for ( int i = 0; i < coordinateSystems.length; i++ ) { 
%>
