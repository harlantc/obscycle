<%@ page import = "java.text.DecimalFormat" %>

<%!
   String[] coordinateSystems;

   String[] systemsWithEclipticWithoutConstellation = {
    "Equatorial (J2000)",
    "Ecliptic (B1950)", 
   };

// Declare several methods used to support processing.

// Return the first position label.
String getFirstPositionLabel( HttpServletRequest request, String selector )
{
  HttpSession session = request.getSession( true );
  String result = "<a href=\"prop_help_rccalc.jsp#RA\">RA";
  String selection = (String) session.getAttribute( selector );
  if ( selection.startsWith( "Equatorial" ) )
  {
     result = "<a href=\"prop_help_rccalc.jsp#RA\">RA";
  }
  else if ( selection.startsWith( "Ecliptic" ) )
  {
     result = "<a href=\"prop_help_rccalc.jsp#EL\">EL";
  }
  return result;
}
  
String getSecondPositionLabel( HttpServletRequest request, String selector )
{
  HttpSession session = request.getSession( true );
  String result = "<a href=\"prop_help_rccalc.jsp#Dec\">Dec";
  String selection = (String) session.getAttribute( selector );
  if ( selection.startsWith( "Equatorial" ) )
  {
     result = "<a href=\"prop_help_rccalc.jsp#Dec\">Dec";
  }
  else if ( selection.startsWith( "Ecliptic" ) )
  {
     result = "<a href=\"prop_help_rccalc.jsp#EB\">EB";
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
  if ( "Ecliptic (Bxxxx)".equals( selection ) )
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

%>
