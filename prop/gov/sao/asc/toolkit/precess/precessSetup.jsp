<%@ page import = "java.text.DecimalFormat" %>

<%!
   String[] coordinateSystems;

   String[] systemsWithB1950 = {
    "Equatorial (B1950)",
    "Ecliptic (B1950)"
   };
   String[] systemsAll = {
    "Equatorial (J2000)",
    "Equatorial (B1950)",
    "Equatorial (Xxxxx)",
    "Galactic", "Ecliptic (B1950)", "Ecliptic (Bxxxx)", "Constellation"
   };
   String[] systemsWithEclipticWithoutConstellation = {
    "Equatorial (J2000)",
    "Equatorial (B1950)",
    "Equatorial (Xxxxx)",
    "Galactic", "Ecliptic (B1950)", "Ecliptic (Bxxxx)"
   };
   String[] systemsWithoutEcliptic = {
    "Equatorial (B1950)",
    "Equatorial (J2000)",
    "Equatorial (Xxxxx)",
    "Galactic", "Constellation"
   };
   String[] systemsWithoutEclipticWithoutConstellation = {
    "Equatorial (B1950)",
    "Equatorial (J2000)",
    "Equatorial (Xxxxx)",
    "Galactic"
   };

// Declare several methods used to support processing.

// Return the first position label.
String getSecondPositionLabel( HttpServletRequest request, String selector )
{
  HttpSession session = request.getSession( true );
  String result = "<a href=\"prop_help_precess.jsp#Dec\">Dec";
  String selection = (String) session.getAttribute( selector );
  if ( selection.startsWith( "Equatorial" ) )
  {
     result = "<a href=\"prop_help_precess.jsp#Dec\">Dec";
  }
  else if ( selection.startsWith( "Galactic" ) )
  {
     result = "<a href=\"prop_help_precess.jsp#B2\">B2";
  }
  else if ( selection.startsWith( "Ecliptic" ) )
  {
     result = "<a href=\"prop_help_precess.jsp#EB\">EB";
  }
  return result;
}
  
String getFirstPositionLabel( HttpServletRequest request, String selector )
{
  HttpSession session = request.getSession( true );
  String result;
  String selection = (String) session.getAttribute( selector );
  if ( selection.startsWith( "Equatorial" ) )
  {
     result = "<a href=\"prop_help_precess.jsp#RA\">RA";
  }
  else if ( selection.startsWith( "Galactic" ) )
  {
     result = "<a href=\"prop_help_precess.jsp#L2\">L2";
  }
  else if ( selection.startsWith( "Ecliptic" ) )
  {
     result = "<a href=\"prop_help_precess.jsp#EL\">EL";
  }
  else {
     result = "<a href=\"prop_help_precess.jsp#RA\">RA";
  }
  return result;
}
  
// Determine if the current request provides a coordinate system with
// the equinox embedded.
boolean needsEquinox( HttpServletRequest request, String selector,
		      String equinox )
{
  boolean result = false;
  HttpSession session = request.getSession( true );
  String selection = (String) session.getAttribute( selector );
  if ( "Equatorial (Xxxxx)".equals( selection ) ||
       "Ecliptic (Bxxxx)".equals( selection ) )
  {
    // Set the result and seed (if required) the initial equinox text.
    result = true;
    String value = (String) session.getAttribute( equinox );
    if ( value == null || "".equals( value ) || "J".equals( value ) )
    {
      session.putValue( equinox, "B" );
    }
  }
  else if ( "Equatorial (Xxxxx)".equals( selection ) )
  {
    // Set the result and seed (if required) the initial equinox text.
    result = true;
    String value = (String) session.getAttribute( equinox );
    if ( value == null || "".equals( value ) || "B".equals( value ) )
    {
      session.putValue( equinox, "J" );
    }
  }
  return result;
}

// Determine if the current request provides a coordinate system with
// the equinox embedded.
boolean displayPosition( HttpServletRequest request )
{
  boolean result = false;
  HttpSession session = request.getSession( true );
  String selection = (String) session.getAttribute( "outputCoordinateSelector" );
  result = !selection.equals( "Constellation" );
  return result;
}

// Return an indication that any input errors were detected.
boolean inputErrorsDetected( HttpServletRequest request )
{
  boolean result = false;
  HttpSession session = request.getSession( true );
  Boolean haveInputErrors = (Boolean) session.getAttribute( "inputErrors" );
  result = haveInputErrors.booleanValue();
  return result;
}
  
%>
