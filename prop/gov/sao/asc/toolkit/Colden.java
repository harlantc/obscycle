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

import info.*;
import ascds.NameResolver;
import captcha.*;
import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Date;
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

/******************************************************************************/
/**
 * Provide GUI support for the Colden (Galactic Neutral Hydrogen
 * Density Calculator) program.
 * 
 * August 2004: Colden can now function without the use of cookies. If cookies
 * are enabled, the toolkit can "remember" coordinate values between the different
 * tools.  However, with cookies turned off, colden can still function properly.
 */

public class Colden extends HttpServlet implements ToolkitConstants
{


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
		     HttpServletResponse response)
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
    toolkitProperties = Toolkit.getProperties( context );
    nameResolver = new NameResolver(toolkitProperties);
    try {
      maxParamLength = new Integer(toolkitProperties.getProperty( "max.param.length") );
    } catch (Exception e) {
      maxParamLength=30;
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
   *    <li>NAMERESOLVER - use NED/SIMBAD to retrieve coordinates
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
    Boolean badparams=false;
    // For debugging, output all the parameters.
    logger.trace( "Colden Dumping parameters ..." );
    String parameterName, parameterValue;
    for ( Enumeration parameters = request.getParameterNames();
          parameters.hasMoreElements(); ) {
      parameterName = (String) parameters.nextElement();
      parameterValue = request.getParameter( parameterName );
      if (parameterName.indexOf("recap") < 0 && parameterValue.length() > maxParamLength.intValue()) {

         badparams=true;
         logger.info( "Colden Bad Parameter, clear all : " + parameterName + " length = " +  parameterValue.length() );
      }
      logger.trace("Colden: " + parameterName + " = " +  parameterValue );
    }

    // Get the session object.
    HttpSession session = request.getSession( true );

    try {
    Toolkit.addProps(toolkitProperties);
    // Initialize the validation state and the output.
    initializeValidationState( request );

    // Set the output to initialized because even if the user has
    // selected "view output", the calculation and the output value
    // will be reset
    session.setAttribute( "output", "" );
    String operation = request.getParameter( "operation" );
    operation=Toolkit.stripInput(operation);
    boolean usecaptcha = UseRecaptcha.doRecaptcha(toolkitProperties);

    if (usecaptcha && operation != null && operation != "" && !operation.equals("CLEAR") && !operation.equals("REFRESH") ) {
      String remoteIP = new String("");
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

    
    //August 2004
    //Call the initalize routines - this allows us to make sure the
    //session has all the required information, without requiring cookies
    //to be enabled. 
    //If the request is to clear the values, that will happen in the
    //following section where operation equals clear
    initializeInputValues(request, false);
    initializeOutputValues(request, true);
	

    // Determine which operation to process.
    if ( operation == null || operation.equals( "" ) )
    {
    }
    else if ( operation.equals( "NAMERESOLVER" ) )
    {
	session.setAttribute( "coldenWarnings", new Boolean(false));
	nameresolver( request, session );
    }
    else if ( operation.equals( "CLEAR" ) )
    {
      // Force the initial values to a reset state.
      initializeInputValues( request, true );
      initializeOutputValues( request, true );
      session.setAttribute( "output", "Values cleared" );
    }
    else if ( operation.equals( "CALCULATE" ) )
    {
	// Reset values and do the calculation.
	session.setAttribute( "coldenWarnings", new Boolean(false));
	calculate( request, session );

    }
    else if ( operation.equals( "VIEW OUTPUT" ) )
    {
	//Since we want to function without requiring cookies, we need
	//to perform the calculation again without relying on session
	//information - so the work here is more than just the noop.
      session.setAttribute( "coldenWarnings", new Boolean( false ) );
      
      //perform the calculation so that all the parameters are set
      calculate(request, session);

    }
    else if ( operation.equals( "REFRESH" ) )
    {
	//The init routines which would be called here have already
	//been called - changed August 2004
    }
    else
    {
      session.setAttribute( "output", "Unknown operation." );
    }
    } catch (Exception exc) {
      initializeInputValues( request, true );
      initializeOutputValues( request, true );
      session.setAttribute( "output", "Values cleared" );
      logger.error(exc.getMessage());
    }
  }

  /**
   * Private variables
   */

  private Properties toolkitProperties;
  private NameResolver nameResolver;
  private Integer maxParamLength=30;
  private static Logger logger = Logger.getLogger(Colden.class);

  // ***************************************************************************
  // Private methods
  // ***************************************************************************

  private void calculate( HttpServletRequest request, HttpSession session )
  {
    // Validate the input parameters.
    ToolkitValidator validator =
      new ToolkitValidator( request, toolkitProperties );

    // Generate the validation state.
    String[] errors = {
      validator.validateEquinoxParameter( "equinox.input",
					  "equinoxLabelBGColor", "equinox" ),
      validator.validateRangeLow( "low.velocity.input", "rangeLowLabelBGColor",
				  "velocity.range.limits", "velocityRangeLow",
				  "velocityRangeHigh" ),
      validator.validateRangeHigh( "high.velocity.input",
				   "rangeHighLabelBGColor",
				   "velocity.range.limits",
				   "velocityRangeLow", "velocityRangeHigh" ),
      validator.validateInputPosition( "ra-l2-el.input",
				       ToolkitValidator.POSITION1, 
				       "position1LabelBGColor",
				       "inputPosition1" ),
      validator.validateInputPosition( "dec-b2-eb.input",
				       ToolkitValidator.POSITION2,
				       "position2LabelBGColor",
				       "inputPosition2" )
    };

    // Do the calculation only if no errors were tripped.
    if ( !validator.errorsOccurred( errors ) )
    {
      String par;

      // Initialize the output buffer.
      String buffer = "<div class=\"transcript\" >";
      buffer += toolkitProperties.getProperty( "output.header.text" );

      // Setup the environment.
      String relPath = toolkitProperties.getProperty( "ascds.release" );
      String ldlibDataVar = "LD_LIBRARY_PATH=" + relPath + "/lib:" + relPath + "/ots/lib" ;
      String jcmLibDataVar = "JCMLIBDATA=" + relPath + "/config/jcm_data/";
      logger.trace(ldlibDataVar);
      logger.trace(jcmLibDataVar);
      String[] environment = {  jcmLibDataVar,ldlibDataVar  };

      // Get the equinox parameters.
      String inputSystem = Parameter.getSystem( request, "equinox",
						"inputCoordinateSelector" );

      // Get the velocity information.
      String velocityRangeHigh = "550.0";
      String velocityRangeLow = "-550.0";
      String dataset = Parameter.get( request, "dataset" );
      String velocityRangeSelector =
	Parameter.get( request, "velocityRangeSelector" );

      if (dataset == null || !dataset.equals("BELL"))
        dataset = "NRAO";

      if ( dataset.equals( "BELL" ) &&
	   "Restricted".equals( velocityRangeSelector ) )
      {
	velocityRangeLow = Parameter.get( request, "velocityRangeLow" );
	velocityRangeHigh = Parameter.get( request, "velocityRangeHigh" );
      } 

      // Get the position selections.
      String inputRA =
	getPosition( request, Parameter.get( request, "inputPosition1" ),
		     RE_NONNEGATIVE_SEXAGESIMAL, RE_NONNEGATIVE_DECIMAL );
      String inputDec =
	getPosition( request, Parameter.get( request, "inputPosition2" ),
		     RE_SEXAGESIMAL, RE_DECIMAL );

      // Determine the input format.  Assume that both inputs are in
      // the same format.  At some point a check for this should be
      // added to the validator.
      String format = getInputFormat( inputRA );

      // Generate, execute and log the command.
      buffer += toolkitProperties.getProperty( "interactive.inputs.text" );
      String command = relPath + "/bin/prop_colden_exe from " +
	inputSystem + format + " p1 vlims " + velocityRangeLow + " " +
	velocityRangeHigh + " dataset " + dataset + " eval " +
	inputRA + " " + inputDec;
      buffer +=  "from " + inputSystem + format + " p1 vlims " + velocityRangeLow + " " +
	velocityRangeHigh + " dataset " + dataset + " eval " +
	inputRA + " " + inputDec + "<br>";
      logger.info(command);
      //buffer += toolkitProperties.getProperty( "output.environment.text" ) +
	//jcmLibDataVar + "<br>" +
	//toolkitProperties.getProperty( "batch.command.text" ) + command + "<br>" +
      buffer += toolkitProperties.getProperty( "output.transcript.text" );
      try
      {
	Runtime runtime = Runtime.getRuntime();
	Process process = runtime.exec( command, environment );
	InputStreamReader reader = new InputStreamReader( process.getInputStream() );
	BufferedReader in = new BufferedReader( reader);

	// Parse the output matching the regular expression against the
	// command output.
	Pattern regexpPattern = Pattern.compile( RE_DECIMAL + RE_WHITESPACE +
				 "(" + RE_DECIMAL + ")" + RE_WHITESPACE +
				 "(" + RE_DECIMAL + ")" + RE_WHITESPACE +
				 "(" + RE_DECIMAL + ")" + RE_WHITESPACE +
				 "\\((.+)\\)<br>" );
	Pattern regexpPattern2 = Pattern.compile( RE_DECIMAL + RE_WHITESPACE +
				 "(" + RE_DECIMAL + ")" + RE_WHITESPACE +
				 "(" + RE_DECIMAL + ")" + RE_WHITESPACE +
				 "(" + RE_DECIMAL + ")" + "<br>");
	// Read each line of output from the command.
	String line;

	// Deal with the placing the output into the results components.
	String resultsL2;
	String resultsB2;
	String resultsNH;
	String resultsComments = " ";
        int gotmatch = 0;

	while ( (line = in.readLine()) != null )
	{
          line += "<br>"; 
	  Matcher match = regexpPattern.matcher( line );
          //logger.trace("MATCH: " + line + "-----");
          if (!match.find()) {
	    match = regexpPattern2.matcher( line );
            if (match.find()) {
              gotmatch = 1;
            }
          }
          else {
            gotmatch = 1;
          }
          
          if (gotmatch > 0) {
	    // Strip off the HMS output position values.
	    resultsL2 = match.group( 1 );
	    session.setAttribute( "resultsL2", resultsL2 );
	    resultsB2 = match.group( 2 );
	    session.setAttribute( "resultsB2", resultsB2 );
	    resultsNH = match.group( 3 );
	    session.setAttribute( "resultsNH", resultsNH );
            if (match.groupCount() > 3) {
	      resultsComments = match.group( 4 );
	      session.setAttribute( "resultsComments", resultsComments );
            }
          }
          if (line.indexOf("Looking for") < 0) 
	    buffer += line;
	}

	// Close the input stream.
	in.close();
	reader.close();
        // according to web, this should happen on cleanup but doesn't always
        process.getInputStream().close();
        process.getOutputStream().close();
        process.getErrorStream().close();



	if ( gotmatch == 0 )
	{
	  // There is none.  Output an error indication.
	  session.setAttribute( "resultsL2", "See Output" );
          //logger.trace(buffer);
	}
      
      }
      //      catch ( REException exc )
      //{
      //request.setAttribute( "resultsL2", exc.getMessage() );
      //	session.setAttribute( "resultsL2", "XXXXXX" );
      //	buffer += exc.getMessage() + "<br>";
      //}
      catch ( IOException exc )
      {
        exc.printStackTrace();
	String message =
	  "Got an IO exception generating/getting results.";
	logger.error( message );
	buffer += message + "<br>";
      }
    
      // Append the environment and command inputs to the output
      // buffer.
      //logger.info( "Calculation done." );
      buffer += "</div>";
      session.setAttribute( "output", buffer );

    }
  }

  // ***************************************************************************
  /*
   * set the session with  NED or SIMBAD coordinates for a given target name
   * @param request  Http servlet request
   * @param session  Http session
   *
  */
  private void nameresolver( HttpServletRequest request, HttpSession session )
  {
      
    // Validate the input parameters.

   String targetname = Parameter.get( request, "targetName");
   String theCoords;

   // create list of resolvers
   ////String[] resolverList = {"ned","simbad"};
   String resolverSelector = Parameter.get( request, "resolverSelector");
   String[] resolverList = resolverSelector.split("/");

   ToolkitValidator validator =
      new ToolkitValidator( request, toolkitProperties );

    // Generate the validation state.
   String[] errors = {
     validator.validateParameter( "target-name.input", "(.+)",
	"targetNameLabelBGColor", "targetName", null )
   };

   if ( !validator.errorsOccurred( errors ) )
    {
     try {
       theCoords = nameResolver.resolve(targetname,resolverList);
       if (theCoords != null) {
	   //If the resolve method returns something, an error occurred
	   session.setAttribute( "inputPosition1", "" );
	   session.setAttribute( "inputPosition2", "" );
	   session.setAttribute( "inputErrors", new Boolean( true ) );
	   session.setAttribute( "errorScript",
				 validator.buildErrorScript(theCoords));
	   session.setAttribute("targetNameLabelBGColor",validator.BG_COLOR_ERROR);
           logger.info(targetname + "==>" + theCoords);
       } else {
	   //No error, get the ra & dec values
	   String theRA = nameResolver.getRA();
	   String theDec = nameResolver.getDec();
	   session.setAttribute( "inputCoordinateSelector","J2000");
	   session.setAttribute( "inputPosition1", theRA );
	   session.setAttribute( "inputPosition2", theDec );
       }
     } catch (IOException exc) {
       logger.error( exc.getMessage() );
       session.setAttribute( "inputPosition1", "" );
       session.setAttribute( "inputPosition2", "" );

       String warning = exc + "\\nPlease contact the CXC HelpDesk";
       session.setAttribute( "coldenWarnings", new Boolean( true ) );
       session.setAttribute( "coldenWarningsScript",
            Toolkit.buildWarningsScript(warning));
     } 
   } 
  }

  /****************************************************************************/
  /**
   * Return a position as either a decimal or sexagesimal string.  A
   * sexagesimal string will be returned as a normalized value:
   *
   * ## ## ##.##
   *
   * @param request The request object
   * @param position The coordinates
   * @param sexagesimalRE reqular expression to use 
   * @param decimalRE reqular expression to use 
   *
   * @return  Either null, a decimal string or a normalized
   * sexagesimal value.
   */

  private String getPosition( HttpServletRequest request,
			      String position, 
			      String sexagesimalRE,
			      String decimalRE )
  {
    String result = null;
    position = position.replaceAll(":"," ");

    // Set up the regular expressions ...
    try {
	Pattern regexp = Pattern.compile( "^\\s*(?:" + sexagesimalRE + ")\\s*$" );
	Matcher sexagesimalMatch = regexp.matcher( position );
	regexp = Pattern.compile( "^\\s*" + decimalRE + "\\s*$" );
	Matcher decimalMatch = regexp.matcher( position );

	// First check for a sexagesimal match.
	if ( sexagesimalMatch.find()) { 
	    // Now determine which form we have.  Assume 3 parts.
	    String hh_dd = sexagesimalMatch.group( 1 );
	    String mm = sexagesimalMatch.group( 2 );
	    String ss = sexagesimalMatch.group( 3 );
	    
	    // Test the assumption.
	    if ( "".equals( hh_dd ) )
		{
		    // The assumption is wrong.  Normalize the result.
		    hh_dd = sexagesimalMatch.group( 4 );
		    mm = sexagesimalMatch.group( 5 );
		    result = hh_dd + " " + mm + " 0.0";
		}
	    else {
		result = position;
	    }
	} else if ( decimalMatch.find() ) {
	    result = position;
	}
    } catch ( Exception exc )  {
	logger.error( exc.getMessage() );
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Parse the input to determine whether it is in decimal degrees or
   * HMS format.
   * @param position  input ra/dec value
   * @return String input format detected
   */

  private String getInputFormat( String position )
  {
    // Default to sexagesimal.
    String result = "/HMS";

    // Detect embedded whitespace to determine if HMS format is being used.
    try {
	Pattern sexRegexp = Pattern.compile( RE_SEXAGESIMAL );
	Matcher match = sexRegexp.matcher( position );

	if ( !match.find() )
	    result = "/DEG";
    } catch ( Exception exc ) {
	exc.printStackTrace();
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
    Parameter.initialize( request, clear, "dataset", "NRAO" );
    Parameter.initialize( request, clear, "equinox", "J2000" );
    Parameter.initialize( request, clear, "inputCoordinateSelector",
			  "Equatorial (J2000)" );
    Parameter.initialize( request, clear, "inputPosition1", "" );
    Parameter.initialize( request, clear, "inputPosition2", "" );
    Parameter.initialize( request, clear, "velocityRangeHigh", "550.0" );
    Parameter.initialize( request, clear, "velocityRangeLow", "-550.0" );
    Parameter.initialize( request, clear, "velocityRangeSelector", "Full" );
    Parameter.initialize( request, clear, "targetName", "" );
    Parameter.initialize( request, clear, "resolverSelector", "SIMBAD/NED" );
    Parameter.initialize( request, clear, "coldenWarnings", 
	new Boolean( false ) );
    Parameter.initialize( request, clear, "coldenWarningsScript", "" );

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
    Parameter.initialize( request, clear, "resultsL2", "" );
    Parameter.initialize( request, clear, "resultsB2", "" );
    Parameter.initialize( request, clear, "resultsNH", "" );
    Parameter.initialize( request, clear, "resultsComments", "" );
  }

  //***************************************************************************
  // Set the session validation state.
  //***************************************************************************
  private void initializeValidationState( HttpServletRequest request )
  {
    // Get the session object.
    HttpSession session = request.getSession( true );

    // Initialize the error flag and the output.
    session.setAttribute( "inputErrors", new Boolean( false ) );
    session.setAttribute( "equinoxLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "rangeLowLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "rangeHighLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "position1LabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "position2LabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "targetNameLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
  }

  /****************************************************************************/

}

/******************************************************************************/
