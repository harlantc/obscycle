<%@ page import = "java.text.DecimalFormat" %>

<%!

// Declare several methods used to support processing.

// Determine if the default buttons are to be shown.
boolean showDefault( HttpServletRequest request, String choice )
{
  boolean result = false;
  //String mode = request.getParameter( choice );
  //result = mode == null || "mission".equals( mode );
  return result;
}

// Determine if the background count rate needs to be displayed.
boolean showEstimationOutput( HttpServletRequest request )
{
  HttpSession session = request.getSession( true );
  return ((Boolean)session.getAttribute( "showEstimationOutput" )).booleanValue();
}

// Determine if the background source input needs to be displayed.
boolean showEstimationInput( HttpServletRequest request )
{
  HttpSession session = request.getSession( true );
  return ((Boolean)session.getAttribute( "showEstimationInput" )).booleanValue();
}

// Determine if the model parameters include the flux density choice.
boolean showFluxDensity( HttpServletRequest request, String choice )
{
  boolean result = false;

  HttpSession session = request.getSession( true );
  String mode = (String) session.getAttribute( "inputMode" );
  if ( "density".equals( mode ) )
  {
    String flux = (String) session.getAttribute( "inputFluxSelector" );
    result = flux == null || choice.equals( flux ); 
  }
  return result;
}

// Determine if the model parameters include the flux choice.
boolean showFlux( HttpServletRequest request, String choice )
{
  boolean result = false;

  HttpSession session = request.getSession( true );
  String mode = (String) session.getAttribute( "inputMode" );
  if ( "flux".equals( mode )  )
  {
    String flux = (String) session.getAttribute( "inputFluxSelector" );
    result = flux == null || choice.equals( flux ); 
  }
  return result;
}

// Determine if the frame time control needs to be presented.
boolean showPileup( HttpServletRequest request )
{
  HttpSession session = request.getSession( true );
  return ((Boolean)session.getAttribute( "showPileup" )).booleanValue();
}

// Determine if the output mode is flux and absorbed flux is the
// selection.
boolean isOutputFlux( HttpServletRequest request, String choice )
{
  boolean result = false;

  // Check that the output mode is either "null" (initial request) or
  // "flux", in which case the prediction GUI element uses units of
  // "erg/cm**2/s absorbed flux".
  String mode = request.getParameter( "outputMode" );
  if ( mode == null || (! mode.equals( "mission" )) )
  {
    String flux = request.getParameter( "outputFluxSelector" );
    if ( flux == null || flux.equals( choice ) )
    {
      result = true;
    }
  }

  return result;
}

// Convert a logT value (float) to a string such that it has two
// decimal digits, possibly a trailing "0".
String getLogT( double number )
{
  String result;
  DecimalFormat formatter = new DecimalFormat( "0.00" );
  result = formatter.format( number );
  return result;
}

boolean isANumber( String number )
{
  // Initialize for success.
  boolean result = true;
  try
  {
    double value = Double.parseDouble( number );
  }
  catch ( NumberFormatException exc )
  {
    result = false;
  }
  
  return result;
}

%>
