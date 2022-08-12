<%@ page import = "java.text.DecimalFormat" %>

<%!
// A support class.

private class Option
{
  private String text;
  private String value;

  public Option( String text, String value )
  {
    this.text = text;
    this.value = value;
  }

  public String getText()
  {
    return text;
  }

  public String getValue()
  {
    return value;
  }
}

private final Option[] options = {
  new Option( "Calendar", "date" ),
  new Option( "Julian", "jd" ),
  new Option( "Modified Julian", "mjd" ),
  new Option( "Day of Year", "doy" ),
  new Option( "Chandra Time", "time" )
};

// Declare several methods used to support processing.

// Return the units associated with the date.
String getInputDateUnits( HttpServletRequest request )
{
  String result;
  HttpSession session = request.getSession( true );
  String mode = (String) session.getAttribute( "conversionModeSelector" );
  if ( "date".equals( mode ) )
  {
    result = "YYYY&nbsp;&nbsp;MMM&nbsp;&nbsp;DD&nbsp;&nbsp;[HH:MM:SS.SS]";
  }
  else if ( "jd".equals( mode ) ||
	    "mjd".equals( mode ) )
  {
    result = "decimal";
  }
  else if ( "doy".equals( mode ) )
  {
    result = "YYYYDDD[.DDDD]";
  }
  else if ( "time".equals( mode ) )
  {
    result = "SSSSSSSS";
  }
  else
  {
    result = "Unknown mode";
  }
  return result;
}

%>
