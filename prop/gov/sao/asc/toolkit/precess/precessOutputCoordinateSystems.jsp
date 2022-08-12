<%
   String inputSelection = (String) session.getAttribute( "inputCoordinateSelector" );
   if ( inputSelection.startsWith( "Ecliptic" ) )
   {
     coordinateSystems = systemsWithoutEcliptic;
   }
   else
   {
      coordinateSystems = systemsAll;
   }
   for ( int i = 0; i < coordinateSystems.length; i++ ) { 
%>
