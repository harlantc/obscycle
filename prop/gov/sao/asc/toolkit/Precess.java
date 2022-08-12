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

import ascds.NameResolver;
import captcha.*;
import info.*;
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
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.Iterator;
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
 * Provide GUI support for the Precess (Coordinate Conversion and
 * Precession) program.
 */

public class Precess extends HttpServlet implements ToolkitConstants
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
    //toolkitProperties.list(System.out);

    nameResolver = new NameResolver(toolkitProperties);
    try {
      maxParamLength = new Integer(toolkitProperties.getProperty( "max.param.length") );
    } catch (Exception e) {
      maxParamLength=30;
    }

    try
    {
      // Set up the input error checking regular expression constants.
      sexRegexp =
	new String( "(?:^([-+]?\\d+) (\\d+)$)|(?:^([-+]?\\d+) (\\d+) (\\d+\\.?\\d*)$)" );
      decRegexp =
	new String( "^[-+]?\\d+\\.\\d*$" );
      equinoxRegexp =
	new String( "^[BJ]\\d\\d\\d\\d$" );
    }
    catch ( Exception rex )
    {
      // What to do?  This code should never get reached!  Just log it.
      logger.error( "Unexpected regular expression exception occurred during init." );
    }

     if (logger.isTraceEnabled()) {
       try {
         Map<String, String> env = System.getenv();
         for (Iterator it=env.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            logger.trace(entry.getKey() + " = " + entry.getValue());
         }
       } catch (Exception ex) {
         logger.error(ex);
       }
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
    HttpSession session = request.getSession( true );

    try {

    Toolkit.addProps(toolkitProperties);
    // Initialize the error flag and the output.
    initializeValidationState( request );
    session.setAttribute( "output", "" );
    String remoteIP = new String("");
    String operation = request.getParameter( "operation" );
    operation=Toolkit.stripInput(operation);
    boolean usecaptcha = UseRecaptcha.doRecaptcha(toolkitProperties);

    String gresponse = request.getParameter("g-recaptcha-response");
    if (usecaptcha && operation != null && operation != "" && !operation.equals("CLEAR") && !operation.equals("REFRESH") ) {
      logger.info("Recaptcha for OPERATION ---" + operation + "----");
      try {
        remoteIP = ClientIPAddress.getFrom(request,false,null);
      } catch (Exception e)
      {
      }
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
    if ( operation == null )
    {
	//Init routines have already been called
      session.setAttribute( "output", "Restarting" );
    }
    else if ( operation.equals( "CLEAR" ) )
    {
      // Clear the input/output values.
      initializeInputValues( request, true );
      initializeOutputValues( request, true );
      session.setAttribute( "output", "Values cleared" );
    }
    else if ( operation.equals( "CALCULATE" ) )
    {
      session.setAttribute( "precessWarnings", new Boolean(false));
      calculate( request, session );
    }
    else if ( operation.equals( "NAMERESOLVER" ) )
    {
      session.setAttribute( "precessWarnings", new Boolean(false));
      nameresolver( request, session );
    }

    else if ( operation.equals( "VIEW OUTPUT" ) )
    {
	// In case there is no session, perform the calculation again
      session.setAttribute( "precessWarnings", new Boolean(false));
      calculate( request, session );
    }
    else
    {
      session.setAttribute( "output", "?" );
    }
    } catch (Exception e) {
      initializeInputValues( request, true );
      initializeOutputValues( request, true );
      session.setAttribute( "output", "Values cleared" );
      logger.error(e.getMessage());

    }
  }

  /****************************************************************************/
  /**
   * Private Variables
   *
   * Note: These variables are shared amongst clients (browsers)
   * therefore take special pains to be sure that adding class
   * instance variables will not cause multi-threading issues.
   */

  private Properties toolkitProperties;
  private NameResolver nameResolver;
  private static Logger logger = Logger.getLogger(Precess.class);


  private Integer maxParamLength = 30;

  private String sexRegexp;
  private String decRegexp;
  private String equinoxRegexp;

  // ***************************************************************************

  private void calculate( HttpServletRequest request, HttpSession session )
  {

    // Validate the input parameters.
    ToolkitValidator validator =
      new ToolkitValidator( request, toolkitProperties );

    // Generate the validation state.
    String[] errors = {
      validator.validateEquinoxParameter( "input.equinox.input",
					  "inputEquinoxLabelBGColor",
					  "inputEquinox" ), 
      validator.validateEquinoxParameter( "output.equinox.input",
					  "outputEquinoxLabelBGColor",
					  "outputEquinox" ),
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
      // Determine if we are dealing with a constellation, position or
      // galactic output and handle accordingly.
      String selection = (String) session.getAttribute( "outputCoordinateSelector" );
      if ( selection.equals( "Constellation" ) )
      {
	calculateConstellation( request, session, validator );
      }
      else if ( selection.equals( "Galactic" ) )
      {
	calculatePosition( request, session, SYSTEM_GALACTIC, validator );
      }
      else if ( selection.startsWith( "Ecliptic" ) )
      {
	calculatePosition( request, session, SYSTEM_ECLIPTIC, validator );
      }
      else 
      {
	calculatePosition( request, session, SYSTEM_EQUATORIAL, validator );
      }
    }
  }

  // ***************************************************************************

  private void calculateConstellation( HttpServletRequest request,
				       HttpSession session,
				       ToolkitValidator validator )
  {
    // Initialize the output buffer.
    String buffer = "<div class=\"transcript\" >";
    buffer += toolkitProperties.getProperty( "output.header.text" );

    // Reset values.
    String par;
    session.setAttribute( "resultsConstellation", "" );

    // Setup the environment.
    String relPath =
      (String) toolkitProperties.getProperty( "ascds.release" );
    String jcmPathVar = "JCMPATH=" + relPath + "/config/jcm_data/";
    String[] environment = { jcmPathVar };

    // Generate, execute and log the command.
    String command = relPath + "/bin/prop_precess_exe";
    buffer += toolkitProperties.getProperty( "interactive.inputs.text" );
    try
    {
      Runtime runtime = Runtime.getRuntime();
      Process process = runtime.exec( command, environment );
      PrintWriter out = new PrintWriter( process.getOutputStream() );
      InputStreamReader reader = new InputStreamReader( process.getInputStream() );

      BufferedReader in = new BufferedReader(reader);

      // Get the equinox parameters.
      String inputEquinox = Parameter.getSystem( request, "inputEquinox",
						 "inputCoordinateSelector" );

      // Get the position selections.
      String inputPosition1 = Parameter.get( request, "inputPosition1" );
      inputPosition1 = inputPosition1.replaceAll(":"," ");
      String inputPosition2 = Parameter.get( request, "inputPosition2" );
      inputPosition2 = inputPosition2.replaceAll(":"," ");
      String inputFormat = getInputFormat( inputPosition1 );

      // Execute the sexigesimal conversion sub-command on the given
      // position, accumulating the sub-command input.
      buffer += executeConversion( out, inputFormat, "",
				   inputEquinox, "CON", 
				   inputPosition1, inputPosition2 );

      // Finish the command and flush the stream.
      command = "QUIT";
      out.println( command );
      out.flush();
      buffer += command + "<br>";

      // Read each line of output from the command.
      String line;
      buffer += toolkitProperties.getProperty( "output.transcript.text" );
      while ( (line = in.readLine()) != null )
      {
	buffer += line + "<br>";
      }

      // Close the I/O streams.
      out.close();
      in.close();
      process.getInputStream().close();
      process.getOutputStream().close();
      process.getErrorStream().close();


      // Parse the output.
      Pattern regexp = Pattern.compile( "<br>Constellation" + RE_WHITESPACE + "(.+)<br>-------" );
      Matcher match = regexp.matcher( buffer );


      // Deal with the placing the output into the results components.
      String resultsConstellation;

      // Process the match, if any.
      if ( !match.find() )
      {
        // There is none.  Output an error indication.
	session.setAttribute( "resultsConstellation", "See Output" );
      }
      else
      {
        // Strip off the output.
	resultsConstellation = match.group( 1 );
	session.setAttribute( "resultsConstellation", resultsConstellation );
      }
    }
    catch ( IOException exc )
    {
      exc.printStackTrace();
      String message =
	  "Got an IO exception generating/getting results.";
      logger.error( message );
      buffer += message + "<br>";
    }
    catch ( Exception exc )
    {
      request.setAttribute( "resultsConstellation", exc.getMessage() );
      session.setAttribute( "resultsEquinox", "XXXXXX" );
      buffer += exc.getMessage() + "<br>";
    }

    
    // Append the environment and command inputs to the output
    // buffer.
    buffer += "</div>";
    session.setAttribute( "output", buffer );
  }

  // ***************************************************************************

  private void calculatePosition( HttpServletRequest request,
				  HttpSession session,
				  int coordSystemCode,
				  ToolkitValidator validator )
  {
    // Initialize the outpur buffer.
    String buffer = "<div class=\"transcript\" >";
    buffer += toolkitProperties.getProperty( "output.header.text" );


    // Reset values.
    String par;
    session.setAttribute( "resultsEquinox", "" );
    session.setAttribute( "resultsFirstSexagesimal", "" );
    session.setAttribute( "resultsSecondSexagesimal", "" );
    session.setAttribute( "resultsFirstDecimal", "" );
    session.setAttribute( "resultsSecondDecimal", "" );

    // Setup the environment.
    String relPath =
      (String) toolkitProperties.getProperty( "ascds.release" );
    String jcmPathVar = "JCMPATH=" + relPath + "/config/jcm_data/";
    String[] environment = { jcmPathVar };

    // Generate, execute and log the command.
    String command = relPath + "/bin/prop_precess_exe";
    buffer +=  toolkitProperties.getProperty( "interactive.inputs.text" );
    try
    {
      Runtime runtime = Runtime.getRuntime();
      Process process = runtime.exec( command, environment );
      PrintWriter out = new PrintWriter( process.getOutputStream() );
      InputStreamReader reader = new InputStreamReader( process.getInputStream() );
      BufferedReader in = new BufferedReader( reader);

      // Get the equinox parameters.
      String inputEquinox = Parameter.getSystem( request, "inputEquinox",
						 "inputCoordinateSelector" );
      String outputEquinox = Parameter.getSystem( request, "outputEquinox",
						  "outputCoordinateSelector" );

      // Get the position selections.
      String inputPosition1 = Parameter.get( request, "inputPosition1" );
      inputPosition1 = inputPosition1.replaceAll(":"," ");
      String inputPosition2 = Parameter.get( request, "inputPosition2" );
      inputPosition2 = inputPosition2.replaceAll(":"," ");
      String inputFormat = getInputFormat( inputPosition1 );

      // Execute the sexigesimal conversion sub-command on the given
      // position, accumulating the sub-command input.
      buffer += executeConversion( out, inputFormat, "/HMS",
				   inputEquinox, outputEquinox, 
				   inputPosition1, inputPosition2 );

      // Execute the decimal degree conversion sub-command on the
      // given position, accumulating the sub-command input.
      buffer += executeConversion( out, inputFormat, "/DEG",
				   inputEquinox, outputEquinox, 
				   inputPosition1, inputPosition2 );

      // Finish the command and flush the stream.
      command = "QUIT";
      out.println( command );
      out.flush();
      buffer += command + "<br>";

      // Read each line of output from the command.
      String line;
      buffer += toolkitProperties.getProperty( "output.transcript.text" );
      while ( (line = in.readLine()) != null )
      {
	buffer += line + "<br>";
      }

      // Close the I/O streams.
      out.close();
      in.close();
      process.getInputStream().close();
      process.getOutputStream().close();
      process.getErrorStream().close();

      // Parse the sexigesimal output matching the sexagesimal regular
      // expression against the command output.
      Matcher sexagesimalMatch =
	  getMatch( request, session, FORMAT_SEXAGESIMAL, coordSystemCode, buffer );

      // Parse the decimal degree output matching the decimal regular
      // expression against the command output.
      Matcher decimalMatch =
	getMatch( request, session, FORMAT_DECIMAL, coordSystemCode, buffer );

      // Deal with the placing the output into the results components.
      String resultsEquinox;
      String resultsFirstSexagesimal;
      String resultsSecondSexagesimal;
      String resultsFirstDecimal;
      String resultsSecondDecimal;

      // Process the HMS match, if any.
      if ( !sexagesimalMatch.find() || !decimalMatch.find() )
      {
        // There is none.  Output an error indication.
	String message = "See Output";
	if ( coordSystemCode == SYSTEM_GALACTIC )
	{
	  session.setAttribute( "resultsFirstSexagesimal", message );
	}
	else
	{
	  session.setAttribute( "resultsEquinox", message );
	}
      }
      else
      {
        // Extract the output values.  First set the equinox output if
        // we are processing an Equatorial or Ecliptic coordinate
        // system.  Use a match index variable to account for the
        // Galactic vs Equatorial/Ecliptic match offset.
	int matchIndex = 1;
	if ( coordSystemCode == SYSTEM_EQUATORIAL ||
	     coordSystemCode == SYSTEM_ECLIPTIC )
	{
	  resultsEquinox = sexagesimalMatch.group( matchIndex++ );
	  session.setAttribute( "resultsEquinox", resultsEquinox );
	}

	// Now extract the remaining output.
	resultsFirstSexagesimal = sexagesimalMatch.group( matchIndex );
	session.setAttribute( "resultsFirstSexagesimal", resultsFirstSexagesimal );
	resultsSecondSexagesimal = sexagesimalMatch.group( matchIndex + 6 );
	session.setAttribute( "resultsSecondSexagesimal", resultsSecondSexagesimal );
	resultsFirstDecimal = decimalMatch.group( matchIndex );
	session.setAttribute( "resultsFirstDecimal", resultsFirstDecimal );
	resultsSecondDecimal = decimalMatch.group( matchIndex + 1 );
	session.setAttribute( "resultsSecondDecimal", resultsSecondDecimal );
      }
    }
    catch ( IOException exc )
    {
      //exc.printStackTrace();
      logger.error(exc);
      String message =
	  "Got an IO exception generating/getting results.";
      buffer += message + "<br>";
    }
    
    // Append the environment and command inputs to the output
    // buffer.
    buffer += "</div>";
    session.setAttribute( "output", buffer );
  }

  /****************************************************************************/
  /**
   * Execute a conversion sub-command accumulating the input.
   *
   * @param out  The output stream providing input to the precess
   * executable.
   *
   * @param inputFormat  The sexigesimal ( hour/minute/second) or
   * decimal degree specifier.
   * 
   * @param outputFormat  The sexigesimal ( hour/minute/second) or
   * decimal degree specifier.
   * 
   * @param inputSystem The input coordinate system.  Usually the
   * input equinox specification.
   *
   * @param outputSystem The output coordinate system.  Usually
   * the output equinox specification.
   *
   * @param inputPosition1  The right ascension position.
   *
   * @param inputPosition2  The declination position.
   *
   * @return The accumulated sub-command input.  */

  private String executeConversion( PrintWriter out,
				    String inputFormat, String outputFormat,
				    String inputSystem, String outputSystem,
				    String inputPosition1,
				    String inputPosition2 )
  {
    // Construct and execute the conversion sub-command, logging the sub-commands.
    String subcmd;
    String command = 
	"FROM " + inputSystem + inputFormat +
	" TO " + outputSystem + outputFormat +
	" CONVERT ";
    out.println( command );
    out.println( inputPosition1 );
    out.println( inputPosition2 );
    out.println( "QUIT" );
    subcmd = command + "<br>" + inputPosition1 + "<br>" + inputPosition2 +
      "<br>QUIT<br>";
    logger.info(subcmd);
    return subcmd;
  }

  /****************************************************************************/
  /**
   * Parse the input to determine whether it is in decimal degrees or
   * HMS format.
   *
   * @param position input coordinates
   * @return string  ra/dec format used
   */

  private String getInputFormat( String position )
  {
    String result;

    // Detect embedded whitespace to determine if HMS format is being used.
    Pattern sexRegexpPattern = Pattern.compile(sexRegexp);
    Matcher match = sexRegexpPattern.matcher( position );
    if ( !match.find() )
      result = "/DEG";
    else
      result = "/HMS";
    return result;
  }

  /****************************************************************************/
  /**
   * Do a regular expression match returning the result.
   *
   * @param request  http servlet request
   * @param session  the session
   * @param formatCode A code specifying a match using either a
   * decimal or sexagesimal regular expression.
   *
   * @param coordSystemCode A code specifying which coordinate system;
   * either Equatorial, Ecliptic or Galactic.
   *
   * @param buffer The string against which the match occurs.
   *
   * @return A regular expession containing extractable elements or
   * null if no matches occurred.
   */

  private Matcher getMatch( HttpServletRequest request,
			    HttpSession session, int formatCode,
			    int coordSystemCode, String buffer )
  {
    Matcher result = null;
    String reStr = "";

    // Set the initial match string, which is a funtion of the system
    // code.
    switch ( coordSystemCode )
    {
    case SYSTEM_ECLIPTIC:
      reStr += "Ecliptic l,b epoch" + RE_WHITESPACE + "(" + RE_DECIMAL + ")" +
	RE_WHITESPACE;
      break;

    default:
    case SYSTEM_EQUATORIAL:
      reStr += "RA,Dec" + RE_WHITESPACE + "(" + RE_EQUINOX + ")" + RE_WHITESPACE;
      break;

    case SYSTEM_GALACTIC:
      reStr += "Galactic l,b" + RE_WHITESPACE;
      break;
    }

    // Append the match strings for the results, which depend on the
    // type code.
    switch ( formatCode )
    {
    case FORMAT_SEXAGESIMAL:
      reStr += "(" + RE_SEXAGESIMAL + ")" + RE_WHITESPACE +
	"(" + RE_SEXAGESIMAL + ")<br>-------";
      break;

    default:
    case FORMAT_DECIMAL:
      reStr += "(" + RE_DECIMAL + ")" + RE_WHITESPACE +
	"(" + RE_DECIMAL + ")<br>-------";
      break;
    }

    // Now do the match and return the rsult.
    try {
	//RE regexp = new RE( reStr );
	//result = regexp.getMatch( buffer );

	Pattern regexp = Pattern.compile( reStr );
	result = regexp.matcher( buffer );

    }
    catch ( Exception exc )
    {
      if ( coordSystemCode == SYSTEM_GALACTIC )
      {
	request.setAttribute( "resultsFirstSexagesimal", exc.getMessage() );
	session.setAttribute( "resultsFirstSexagesimal", "XXXXXX" );
      }
      else
      {
	request.setAttribute( "resultsEquinox", exc.getMessage() );
	session.setAttribute( "resultsEquinox", "XXXXXX" );
      }
      buffer += exc.getMessage() + "<br>";
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
    Parameter.initialize( request, clear, "inputCoordinateSelector", "Equatorial (J2000)" );
    Parameter.initialize( request, clear, "inputEquinox", "B1950" );
    Parameter.initialize( request, clear, "inputPosition1", "" );
    Parameter.initialize( request, clear, "inputPosition2", "" );
    Parameter.initialize( request, clear, "outputCoordinateSelector", "Equatorial (J2000)" );
    Parameter.initialize( request, clear, "outputEquinox", "J2000" );
    Parameter.initialize( request, clear, "format", "/HMS" );
    Parameter.initialize( request, clear, "targetName", "" );
    Parameter.initialize( request, clear, "resolverSelector", "SIMBAD/NED" );
    Parameter.initialize( request, clear, "precessWarnings",
        new Boolean( false ) );
    Parameter.initialize( request, clear, "precessWarningsScript", "" );


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
    Parameter.initialize( request, clear, "resultsEquinox", "" );
    Parameter.initialize( request, clear, "resultsConstellation", "" );
    Parameter.initialize( request, clear, "resultsFirstSexagesimal", "" );
    Parameter.initialize( request, clear, "resultsSecondSexagesimal", "" );
    Parameter.initialize( request, clear, "resultsFirstDecimal", "" );
    Parameter.initialize( request, clear, "resultsSecondDecimal", "" );
  }

  // ***************************************************************************/
  // Set the session validation state.
  // ***************************************************************************/
  private void initializeValidationState( HttpServletRequest request )
  {
    // Get the session object.
    HttpSession session = request.getSession( true );

    // Initialize the error flag and the output.
    session.setAttribute( "inputErrors", new Boolean( false ) );
    session.setAttribute( "inputEquinoxLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "outputEquinoxLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "position1LabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "position2LabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "targetNameLabelBGColor",
                      ToolkitValidator.BG_COLOR_NORMAL );
  }


  // ***************************************************************************/
  // * Determine NED or SIMBAD coordinates for a given target name
  // ***************************************************************************/
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
         session.setAttribute( "inputPosition1", "" );
         session.setAttribute( "inputPosition2", "" );
         session.setAttribute( "inputErrors", new Boolean( true ) );
         session.setAttribute( "errorScript",
            validator.buildErrorScript(theCoords));
         session.setAttribute("targetNameLabelBGColor",validator.BG_COLOR_ERROR );
         logger.info(targetname + "==>" + theCoords);
       }
       else {
         String theRA = nameResolver.getRA();
         String theDec = nameResolver.getDec();
         session.setAttribute( "inputCoordinateSelector","J2000");
         session.setAttribute( "inputPosition1", theRA );
         session.setAttribute( "inputPosition2", theDec );
       }
     }

     catch (IOException exc) {
       String warning = exc.getMessage();
       warning +=  "\\nPlease contact the CXC HelpDesk\n";
       session.setAttribute( "precessWarnings", new Boolean( true ) );
       session.setAttribute( "precessWarningsScript",
            Toolkit.buildWarningsScript(warning));
       session.setAttribute( "inputPosition1", "" );
       session.setAttribute( "inputPosition2", "" );
       logger.error( exc.getMessage() );
     }

   }
  }


}

// *****************************************************************************
