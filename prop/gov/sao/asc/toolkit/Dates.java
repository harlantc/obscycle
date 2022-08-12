/*
  Copyrights:
 
  Copyright (c) 2000,2019 Smithsonian Astrophysical Observatory
 
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

import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import info.*;
import captcha.*;


/******************************************************************************/
/**
 * Provide GUI support for the Dates program.  The Dates program
 * permits conversions to and from a number of date systems, such as
 * Julian, Modified Julian, Day of Year and Chandra Time.
 */

public class Dates extends HttpServlet implements ToolkitConstants
{


  /****************************************************************************/
  /****************************************************************************/
  /**
   * Handle a GET request from a browser --- simply initialize
   * validation state.
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void doGet( HttpServletRequest request,
		     HttpServletResponse response )
    throws ServletException, IOException
  {
    initializeValidationState( request );
  }

  /****************************************************************************/
  /**
   * Handle a POST request from a browser --- simply initialize
   * validation state.
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void doPost( HttpServletRequest request,
		      HttpServletResponse response )
    throws ServletException, IOException
  {
    initializeValidationState( request );
  }

  /****************************************************************************/
  /**
   * Handle the INIT operation.  This operation is invoked by the
   * servlet engine when it starts up.  Set the tookit properties if
   * they have not already been set.  This includes the proposal data
   * path.
   *
   * @param config The configuration object established by the servlet
   * engine.
   */

  public void init( ServletConfig config )
    throws ServletException
  {
    // Fetch the toolkit properties.  Note that if this is the first
    // time the global toolkit properties are accessed, the prop data
    // path property will get appended to the default (jar file
    // resident) toolkit properties.
    ServletContext context = config.getServletContext();
    toolkitProperties = Toolkit.getProperties(context);


    try {
      maxParamLength = new Integer(toolkitProperties.getProperty( "max.param.length") );
    } catch (Exception e) {
      maxParamLength=30;
    }


    try
    {
      failRE = new String( "fail" );
      dateRE = new String( RE_DATE );
      jdRE = new String( RE_JD );
      mjdRE = new String( RE_MJD );
      doyRE = new String( RE_DOY );
      timeRE = new String( RE_TIME );
      trimRE = new String( RE_EMBEDDED_PERIOD );
      decimalRE = new String( RE_DECIMAL );
      numberRE = new String( RE_NUMBER );
    }
    catch ( Exception exc )
    {
      logger.error( "Initialization failed building regular expressions" );
    }
  }

  /****************************************************************************/
  /**
   * Handle a submission from a browser.  Five operations are provided:
   * <ol>
   *    <li>CLEAR - reset the session variables to their default state.
   *    <li>CALCULATE - check for input errors and execute the command
   *        line tool.
   *    <li>VIEW OUTPUT - display the output from the command line tool.
   *    <li>HELP - display the generic toolkit help file.
   * </ol>
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void service( HttpServletRequest request,
		       HttpServletResponse response )
    throws ServletException, IOException
  {

    // reload properties in case they changed
    Toolkit.addProps(toolkitProperties);

    Boolean badparams=false;
    // For debugging, output all the parameters.
    String parameterName, parameterValue;
    for ( Enumeration parameters = request.getParameterNames();
	  parameters.hasMoreElements(); )
    {
      parameterName = (String) parameters.nextElement();
      parameterValue = request.getParameter( parameterName );
      if (parameterName.indexOf("recap") < 0 && parameterValue.length() > maxParamLength.intValue()) {

         badparams=true;
         logger.info( "Bad Parameter, clear all: " + parameterName + " length = " +  parameterValue.length() );
      }

    }

    // Get the session object.
    String remoteIP = new String("");
    HttpSession session = request.getSession( true );

    try {

    // Initialize the validation state and the output.
    initializeValidationState( request );
    session.setAttribute( "output", "" );
    String operation = request.getParameter( "operation" );
    operation=Toolkit.stripInput(operation);

    //August 2004
    //Call the initalize routines - this allows us to make sure the
    //session has all the required information, without requiring cookies
    //to be enabled. 
    //If the request is to clear the values, that will happen in the
    //following section where operation equals clear
    initializeInputValues(request, false);
    initializeOutputValues(request, true);
    boolean usecaptcha = UseRecaptcha.doRecaptcha(toolkitProperties);

    if (usecaptcha && operation != null && operation != "" && !operation.equals("CLEAR") && !operation.equals("REFRESH") ) {
      try {
        remoteIP = ClientIPAddress.getFrom(request,false,null);
      } catch (Exception e)
      {
      }
      String gresponse = request.getParameter("g-recaptcha-response");
      if (gresponse == null || gresponse.equals("")) {
         logger.info("NO RECAPTCHA --- " + remoteIP);
         badparams=true;
      }
      else {
        VerifyRecaptcha verifyRecaptcha = new VerifyRecaptcha();
        try {
          if (!verifyRecaptcha.verify(toolkitProperties,gresponse,remoteIP)) {
            logger.info("BAD RECAPTCHA --- " + remoteIP);
            badparams=true;
          }
        }
        catch (Exception exc) {
          if (remoteIP == null) remoteIP="unknown";
          logger.error("failed to verify " + remoteIP);
          logger.error(exc);
          badparams=true;
        }
      }
    }
    if (badparams) { operation="CLEAR"; }

    // Determine which operation to process.
    if ( operation == null) 
    {
	//No need to do anything since the input and output values
	//have already been initialized
    } 
    else if ( operation.equals( "CLEAR" ) )
    {
      // Clear the input/output values.
      initializeInputValues( request, true );
      initializeOutputValues( request, true );
    }
    else if ( operation.equals( "CALCULATE" ) )
    {
      calculate( request, session );
    }
    else if ( operation.equals( "VIEW OUTPUT" ) )
    {
	// To ensure that the tool can work without cookies, we need to
	// redo the calculation
	calculate( request, session );
    }
    else
    {
      session.setAttribute( "output", "Unknown operation!" );
    }
    } catch (Exception exc) {
      initializeInputValues( request, true );
      initializeOutputValues( request, true );
      logger.error(exc.getMessage());
    }
  }


  /****************************************************************************/
  /**
   * Private variables.
   */

  private Properties toolkitProperties;

  private String dateRE;
  private String jdRE;
  private String mjdRE;
  private String doyRE;
  private String timeRE;
  private String failRE;
  private String trimRE;
  private String decimalRE;
  private String numberRE;


  private Integer maxParamLength=30;
  private static Logger logger = Logger.getLogger(Dates.class);



  // ****************************************************************************
  // Private methods.
  // ****************************************************************************

  private void calculate( HttpServletRequest request, HttpSession session )
  {
    try {
    // Validate the input parameters.
    ToolkitValidator validator =
      new ToolkitValidator( request, toolkitProperties );

    // Determine the appropriate regular expression and range key to
    // use to validate the input.
    String modeIn = Parameter.get( request, "conversionModeSelector" );
    String regexp = getInputRegexp( modeIn );
    String rangeKey = getRangeKey( modeIn );

    // If the input is of type calendar/date, then split it to determine
    // if each component is valid
    String inputDate = Parameter.get(request, "datesInputDate");
    String invalidInputMsg = new String();  //need this to display if an error is found with the time format

    if( "date".equalsIgnoreCase ( modeIn ) ) { 
	String[] inputDateComponents = inputDate.split("\\s+");
	int index = 0;
	int numComponents = inputDateComponents.length;

	if(numComponents < 3 || numComponents > 4) {
	    //Error, not enough or too many components
	    invalidInputMsg = new String("\\nError: Date input is not valid");
	} 

	try {
	    //Make sure the year is valid?
	    int year = Integer.parseInt(inputDateComponents[0]);
	    if(year <= 0) {
		invalidInputMsg = new String("\\nError: Year cannot be less than or equal to 0");
	    }

	    if(numComponents == 4) {
		//There is a time component, make sure it's valid
		String inputTime = inputDateComponents[3];
		String[] timeComponents = inputTime.split(":");
		int numTimeComponents = timeComponents.length;
		int hours = 0, minutes = 0;
		float seconds = 0;
		
		if(numTimeComponents == 3) {
		    seconds = Float.parseFloat(timeComponents[2]);
		}
		
		if(numTimeComponents >= 2) {
		    minutes = Integer.parseInt(timeComponents[1]);
		}
		
		if(numTimeComponents >= 1) {
		    hours = Integer.parseInt(timeComponents[0]);
		}
		
		//Validate the time input
		if(hours < 0 || hours > 24 || minutes < 0 || minutes > 59 ||
		   seconds < 0 || seconds > 59) {
		    invalidInputMsg = new String("\\nError: Time is invalid");
		}
	    }
	} catch(NumberFormatException numberFormatEx) {
	    //Catch any number format exceptions, which will be thrown if the
	    //by the parseInt and parseFloat routines if the parameter is not
	    //the correct type - and print the input parameters to the standard
	    //error log so we can determine what went wrong.
	    logger.error("User input is incorrect: ");
	    index = 0;
	    while(index < numComponents) {
		logger.error("Parameter[" + index + "] == " + inputDateComponents[index]); 
		index++;
	    }
	    //Set the error string
	    invalidInputMsg = new String("\\nError: Input date format is invalid");
	}
    }


    // Generate the validation state.
    String[] errors = {
      validator.validateParameter( "date.input", regexp, "dateLabelBGColor",
				   "datesInputDate", rangeKey ),
      invalidInputMsg
    };

    //If the validateParameter routine returned an invalid input error, then we can
    //disregard the other error message, which is basically the same message.
    if(errors[0] != null) {
	errors[1] = new String();
    }

    //If the time is invalid, the invalidInputMsg string has been set - and we need
    //to change the color of the field name.  Must be done after the validateParameter
    //call above, as that resets the color.
    if(invalidInputMsg.length() > 0) {
	session.setAttribute("dateLabelBGColor", ToolkitValidator.BG_COLOR_ERROR);
    }	


    // Do the calculation only if no errors were tripped.
    if ( !validator.errorsOccurred( errors ) )
    {
      // Initialize the output buffer.
      String buffer = "<div class=\"transcript\" >";
      buffer += toolkitProperties.getProperty( "output.header.text" );

      // Reset values.
      String par;

      // Log the environment information.
      //buffer += toolkitProperties.getProperty( "output.environment.text" );
      //for ( int i = 0; i < environment.length; i++ )
      //{
	//buffer += environment[i] + "<br>";
      //}

      // Get the conversion mode.
      session.setAttribute( "conversionModeSelector", modeIn );

      // Get the date to be converted.
      String date = getDate( request, session );

      // Generate the output.
      buffer +=
	convertDate( session, modeIn, "date", date, "resultsCalendar", validator );
      buffer +=
	convertDate( session, modeIn, "jd", date, "resultsJulian", validator );
      buffer +=
	convertDate( session, modeIn, "mjd", date, "resultsJulianModified", validator );
      buffer +=
	convertDate( session, modeIn, "doy", date, "resultsDayOfYear", validator );
      buffer +=
	convertDate( session, modeIn, "time", date, "resultsChandraTime", validator );

     
      // Append the environment and command inputs to the output
      // buffer.
      buffer += "</div>";
      session.setAttribute( "output", buffer );
    }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  /****************************************************************************/

  /**
   * Convert a date from a given calendar code to another calendar code.
   *
   * @param session  session instance
   * @param inputCalendarCode  input calendar data format
   * @param outputCalendarCode  output calendar data format
   * @param inputValue  input value
   * @param destination  output results
   * @param validator  validator for input values
   *
   * @return The string representation of the converted date.
   */

  private String convertDate( HttpSession session, String inputCalendarCode, 
			      String outputCalendarCode,
			      String inputValue,
			      String destination,
			      ToolkitValidator validator )
  {
    String log;

    log = toolkitProperties.getProperty( "interactive.inputs.text" ) ;
    // Generate, execute and log the command.
    String relPath =
      (String) toolkitProperties.getProperty( "ascds.release" );
    String jcmPathVar = "JCMPATH=" + relPath + "/config/jcm_data";
    String[] environment = new String[]  {  jcmPathVar };

    String command = relPath + "/bin/prop_dates_exe p0 from " +
      inputCalendarCode + " to " + outputCalendarCode + " eval " +
      inputValue;
    log +=  "from " +
      inputCalendarCode + " to " + outputCalendarCode + " eval " +
      inputValue;

    // called for every conversion but just log the first one which is 'date'
    if (outputCalendarCode.equals("date")) {
      logger.info(command);
    }

    //log = toolkitProperties.getProperty( "batch.command.text" ) + command + "<br>";

    try
    {
      Runtime runtime = Runtime.getRuntime();
      Process process = runtime.exec( command, environment );
      InputStreamReader reader = new InputStreamReader( process.getInputStream() );
      BufferedReader in = new BufferedReader( reader);

      // Read each line of output from the command.
      String line;
      String commandOutput = "";
      String newCommandOutput = "";
      log += toolkitProperties.getProperty( "output.transcript.text" );
      while ( (line = in.readLine()) != null )
      {
	  //commandOutput += line + "\n";
        if (line.indexOf("JCMPATH") < 0) {
	  commandOutput += line;
	  commandOutput += " ";
        }
	//logger.info( "command line: " + line + "------" );
	log += line + "<br>";
      }

      // Close the input stream.
      in.close();
      process.getInputStream().close();
      process.getOutputStream().close();
      process.getErrorStream().close();

      // Generate the regular expression based on the output mode.
      String regexp = getOutputRegexp( outputCalendarCode );

      // Using the appropriate regular expression look for a match.
      Pattern regexpPattern = Pattern.compile(regexp);
      Matcher matcher = regexpPattern.matcher( commandOutput );

      // Process the match, if any.
      if ( !matcher.find() )
      {
        // There is none.  Output an error indication.
	session.setAttribute( destination, "See Output" );
	logger.info( "No match for RE: " + regexp );
	logger.info( "Command output: " + commandOutput );
      }
      else
      {
        // Got one.  Extract the relevant data.
	String value = matcher.group( 1 );
	session.setAttribute( destination, trimZeroes( value ) );
      }
    }
    catch ( IOException exc )
    {
      exc.printStackTrace();
      String message =
	  "Got an IO exception generating/getting results.";
      logger.error( message );
      log += message + "<br>";
    }
    return log;
  }
    
  /****************************************************************************/

  /**
   * Return the date string suitably formatted to pass as an argument
   * on the command line.
   *
   * @param request  input servlet request
   * @param session  session instance
   *
   * @return The string representation of the converted date.  */

  private String getDate( HttpServletRequest request, HttpSession session )
  {
    String result;
    String date = Parameter.get( request, "datesInputDate" );
    StringBuffer sb = new StringBuffer( date );
    int N = sb.length();
    int j = 0;
    for ( int i = 0; i < N; i++, j++ )
    {
      if ( sb.charAt( j ) == ':' )
      {
        sb.insert( j, "\\" );
	j++;
      }
    }
    return sb.toString();
  }

  /****************************************************************************/
  /**
   * @return  a string with excess leading or trailing zeroes removed.
   *
   * <p>
   *
   * @param str a string of the form dddd[.ddddd] where `d'
   * can be one of [0-9].
   *
   * <p>
   *
   * If <code>str</code> represents an empty character sequence, or the
   * first and last characters in <code>str</code> both have non-zero
   * characters, then the input <code>str</code> object is returned.
   *
   * <p>
   *
   * Otherwise, a new string is created by removing leading and trailing
   * zeroes.
   *
   * <p>
   *
   * If this new string is empty or ".", then "0" is returned.
   *
   * <p>
   *
   * If the new string is of the form "dddddd." then a single trailing
   * zero is appended and the resultant string returned.
   *
   * <p>
   *
   * If the new string is of the form ".ddddd" then a single leading
   * zero is prepended the resultant string returned.
   *
   * <p>
   *
   * Otherwise, the new string is returned unmodified.
   */

  private String trimZeroes( String str )
  {

    // Copy the argument to a working area.
    StringBuffer sb = new StringBuffer( str );

    // See if we have a trimmable string.
    Pattern trimREPattern = Pattern.compile(trimRE);
    Matcher matcher = trimREPattern.matcher( str );
    
    if ( matcher.find() ) {
	// The argument is potentially trimmable.  Get the indices of the match.
	// These are the new methods for when deprecated ones aren't available
	int start = matcher.start();
	int end = matcher.end();
	
	// Strip off any leading zeroes.
	while ( start < end && sb.charAt( start ) == '0' ) {
	    // Remove the leading zero and trim the trailing index.
	    sb.deleteCharAt( start );
	    end--;
	}
	
	// Insure that there is at least one leading zero.
	if ( sb.charAt( start) == '.' ) {
	    // There isn't.  Insert one compensating the end index.
	    sb.insert( start, '0' );
	    end++;
	}

	// Strip off any trailing zeroes.  First adjust the end index to
	// point to the last character in the range.
	end--;
	while ( end > start && sb.charAt( end ) == '0' ) {
	    // Remove a trailing zero.
	    sb.deleteCharAt( end );
	    end--;
	}

	// Insure that there is at least one trailing zero.
	if ( sb.charAt( end ) == '.' ) {
	    sb.insert( end + 1, '0' );
	}
    } 
    
    return sb.toString();
  }

  /****************************************************************************/
  /**
   * Returns a regular expression suitable to detect a match for the
   * input type.
   *
   * @param format The selected input format.
   *
   * @return A regular expression suitable to use for matching an
   * input of the given type.
   */

  private String getInputRegexp( String format )
  {
      String result;

    // Walk through the known types.
    if ( "date".equalsIgnoreCase( format ) )
    {
      result = dateRE;
    }
    else if ( "jd".equalsIgnoreCase( format ) ||
	      "mjd".equalsIgnoreCase( format ) )
    {
      // Accept a decimal value.
      result = numberRE;
    }
    else if ( "doy".equalsIgnoreCase( format ) )
    {
      result = timeRE;
    }
    else if ( "time".equalsIgnoreCase( format ) )
    {
      result = timeRE;
    }
    else
    {
      result = failRE;
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Returns a regular expression suitable to extract a match for the
   * input type from the command output.
   *
   * @param format The selected input format.
   *
   * @return A regular expression suitable to use for matching an
   * input of the given type.
   */

  private String getOutputRegexp( String format )
  {
    String result;

    // Walk through the known types.
    if ( "date".equalsIgnoreCase( format ) )
    {
      result = dateRE;
    }
    else if ( "jd".equalsIgnoreCase( format ) )
    {
      result = jdRE;
    }
    else if ( "mjd".equalsIgnoreCase( format ) )
    {
      result = mjdRE;
    }
    else if ( "doy".equalsIgnoreCase( format ) )
    {
      result = doyRE;
    }
    else if ( "time".equalsIgnoreCase( format ) )
    {
      result = timeRE;
    }
    else
    {
      result = failRE;
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Returns a string key used to look up limits for a particular date
   * format.  Null is returned if there are no bounds on the input.
   *
   * @param format The selected input format.
   *
   * @return A string (possibly null) used to lookup a range for
   * validating a particular input format.
   */

  private String getRangeKey( String format )
  {
    String result = null;

    // Walk through the known types.
    if ( "jd".equalsIgnoreCase( format ) ) {
	result = "jd.limits";
    } else if ( "mjd".equalsIgnoreCase( format ) ) {
	result = "mjd.limits";
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Initialize input parameters.  Each parameter is processed as
   * follows: 
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
   * @param request The servlet request.
   * @param clear If set use the default value.
   */

  private void initializeInputValues( HttpServletRequest request, 
				      boolean clear )
  {
    Parameter.initialize( request, clear, "operation", "REFRESH" );
    Parameter.initialize( request, clear, "conversionModeSelector", "date" );
    Parameter.initialize( request, clear, "datesInputDate", "" );
  }

  /****************************************************************************/
  /**
   * Initialize output parameters.  Each parameter is processed as
   * follows: 
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
   * @param request The servlet request.
   * @param clear If set use the default value.
   */

  private void initializeOutputValues( HttpServletRequest request, 
				       boolean clear )
  {
    Parameter.initialize( request, clear, "resultsCalendar", "" );
    Parameter.initialize( request, clear, "resultsJulian", "" );
    Parameter.initialize( request, clear, "resultsJulianModified", "" );
    Parameter.initialize( request, clear, "resultsDayOfYear", "" );
    Parameter.initialize( request, clear, "resultsChandraTime", "" );
  }

  /****************************************************************************/
  /**
   * Set the session validation state.
   * @param request  http servlet request
   */

  private void initializeValidationState( HttpServletRequest request )
  {
    // Get the session object.
    HttpSession session = request.getSession( true );

    // Initialize the error flag and the output.
    session.setAttribute( "inputErrors", new Boolean( false ) );
    session.setAttribute( "dateLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
  }

  /****************************************************************************/

}

/******************************************************************************/
