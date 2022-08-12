package info;
/*
  Copyrights:
 
  Copyright (c) 2000 Smithsonian Astrophysical Observatory
 
  Permission to use, copy, modify, distribute, and  sell  this
  software  and  its  documentation  for any purpose is hereby
  granted without  fee,  provided  that  the  above  copyright
  notice  appear  in  all  copies and that both that copyright
  notice and this permission notice appear in supporting docu-
  mentation,  and  that  the  name  of the  Smithsonian Astro-
  physical Observatory not be used in advertising or publicity
  pertaining to distribution of the software without specific,
  written  prior  permission.   The Smithsonian  Astrophysical
  Observatory makes no representations about  the  suitability
  of  this  software for any purpose.  It is provided  "as is"
  without express or implied warranty.
  THE  SMITHSONIAN  INSTITUTION  AND  THE  SMITHSONIAN  ASTRO-
  PHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES  WITH REGARD TO
  THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANT-
  ABILITY AND FITNESS,  IN  NO  EVENT  SHALL  THE  SMITHSONIAN
  INSTITUTION AND/OR THE SMITHSONIAN ASTROPHYSICAL OBSERVATORY
  BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES
  OR ANY DAMAGES  WHATSOEVER  RESULTING FROM LOSS OF USE, DATA
  OR PROFITS,  WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
  OTHER TORTIOUS ACTION,  ARISING OUT OF OR IN CONNECTION WITH
  THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

/******************************************************************************/

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/******************************************************************************/
/**
 * Provide operations to access, test and initialize request and
 * session parameters (variables).
 */

public class Parameter extends Object implements ToolkitConstants
{

  /****************************************************************************/
  /**
   * Return an equinox value.  If the parameter indicated by the
   * <i>equinox</i> argument exists in the request object, then return
   * it's value, otherwise return null.  Persist the value if it is
   * non-null.
   *
   * @param request The browser generated request object.
   * @param coordSystemCode A code identifying the system associated
   * with the given equinox.
   * @param system The name of the coordinate system parameter.
   * @param equinox The name of the Equinox parameter.
   *
   * @return The value of the indicated equinox parameter.
   */

  public static String getEquinoxValue( HttpServletRequest request,
					int coordSystemCode,
					String system, String equinox )
  {
    String result = null;
    String equinoxValue = null;

    // Get the equinox spec if it is provided.
    if ( Parameter.has( request, equinox ) )
    {
      equinoxValue = Parameter.get( request, equinox );
    }

    switch ( coordSystemCode )
    {
    default:
    case SYSTEM_EQUATORIAL:
      // Determine whether to extract the equinox value from the
      // coordinate system value or from the equinox input.
      if ( equinoxValue == null )
      {
	// Extract the equinox value from the system value to use as
	// the result.
	try
	{
	    Pattern regexp = Pattern.compile( "[(]([BJ]\\d+\\.?\\d*)[)]" );
	    Matcher match = regexp.matcher( system );

	    match.find();
	    result = match.group( 1 ); 
	}
	catch ( Exception exc )
	{
	  // Force an error.
 	  result = "";
 	}
      }
      else
      {
	// Use the equinox input value.
	result = equinoxValue;
      }
      break;

    case SYSTEM_ECLIPTIC:
      // Determine whether to extract the equinox value from the
      // coordinate system value or from the equinox input.
      if ( equinoxValue == null )
      {
	// Extract the equinox value from the system value to use as
	// the result.
	try
	{
	    Pattern regexp = Pattern.compile( "[(]B(\\d+\\.?\\d*)[)]" );
	    Matcher match = regexp.matcher( system );
	    
	    match.find();
	    result = match.group(1); 

	}
	catch ( Exception exc )
	{
	  // Force an error.
 	  result = "";
 	}
      }
      else
      {
	// Use the equinox input value excluding the leading "B".
	result = equinoxValue.substring( 1 );
      }
      break;
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Return the value associated with a coordinate system parameter,
   * null if no such parameter exists.  The output is affected by the
   * coordinate system type: an Equatorial system is returned as is;
   * an Ecliptic system is returned with an "EC" prefix; and a
   * Galactic system is returned simply as "G"
   *
   * @param request The browser generated request object.
   * @param equinox The name of the equinox parameter.
   * @param system The name of the coordinate system parameter.
   *
   * @return The value of the coordinate system parameter.
   */

  public static String getSystem( HttpServletRequest request,
				  String equinox, String system )
  {
    String result = null;

    // Map the system to a code.
    String systemValue = Parameter.get( request, system );
    if ( systemValue == null )
    {
      // The system value should not be null.  We should generate a
      // bug report at this point but this is outside the scope of
      // this testing/fix pass.  For now force an error to occur by
      // returning a bogus result.
      result = "INTERNAL ERROR";
    }
    else if ( systemValue.startsWith( "Equatorial" ) )
    {
      // For an equatorial system use an equinox value, if one is
      // available.
      result = Parameter.getEquinoxValue( request, SYSTEM_EQUATORIAL,
					  systemValue, equinox );
    }
    else if ( systemValue.startsWith( "Ecliptic" ) )
    {
      // For an ecliptic system also use an equinox value if one is
      // available.
      result = "EC" + Parameter.getEquinoxValue( request, SYSTEM_ECLIPTIC,
						 systemValue, equinox );
    }
    else
    {
      // Galactic
      result = "G";
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Return the value associated with a parameter.  If the value is in
   * the current request, return that value (and persist that value),
   * otherwise return the persisted value.
   *
   * @param request The browser generated request object.
   * @param name The parameter identifier.
   *
   * @return A string containg the current value associated with
   * parameter <i>name</i>.
   */

  public static String get(  HttpServletRequest request, String name )
  {
    // Get the session value.
    HttpSession session = request.getSession( true );

    // First try the request message value.
    String result = request.getParameter( name );
    result = Toolkit.stripInput(result);

    if ( result == null )
    {
      // The parameter is not in the message, use the session value.
      result = (String) session.getAttribute( name );
    }
    else
    {
      // Save the parameter as a session value.
      session.setAttribute( name, result );
    }
    if (result != null) {
       result = result.trim();
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Determine if the request object includes the named parameter.
   * Return true if it does, false otherwise.
   *
   * @param request The browser generated request object.
   * @param name The parameter name.
   *
   * @return true iff the named parameter is specified in the request
   * object.
   */

  public static boolean has( HttpServletRequest request, String name )
  {
    String value = request.getParameter( name );
    value = Toolkit.stripInput(value);
    return value != null;
  }

  /****************************************************************************/
  /**
   * Initialize a parameter --- handle persistence and resetting.  A
   * parameter is initialized as follows:
   *
   * 1) The <i>clear</i> flag is false: if the parameter has been
   * passed in this request packet, then the value is stored in the
   * session (cookie).  If the parameter is not passed and a session
   * value exists, then nothing is done with the value.  If no value
   * exists for the session value, then it is set to the default
   * value.
   *
   * 2) The <i>clear</i> flag is true: the session value is set to the
   * default.
   *
   * @param request The browser generated request object.
   * @param clear If set use the default value.
   * @param name Parameter name.
   * @param defaultValue The default value.
   */

  public static void initialize( HttpServletRequest request,
				 boolean clear,
				 String name,
				 Object defaultValue )
  {
    // Get the session value.
    HttpSession session = request.getSession( true );

    // Determine if the value needs to be cleared (set to its default
    // value).
    if ( clear )
    {
      // It does.
      session.setAttribute( name, defaultValue );
    }
    else
    {
      // It doesn't.  See if the parameter is in the current request
      // message.
      String value = request.getParameter( name );
      value = Toolkit.stripInput(value);
      if ( value != null )
      {
	// It is. Save it. 
	session.setAttribute( name, value );
      }
      else
      {
	// It is not.  See if a session value exists.
	Object obj = (Object) session.getAttribute( name );
	if ( obj == null )
	{
	  // It doesn't.  Use the default value.
	  session.setAttribute( name, defaultValue );
	}
      }
    }
  }

  /****************************************************************************/
}

/******************************************************************************/
