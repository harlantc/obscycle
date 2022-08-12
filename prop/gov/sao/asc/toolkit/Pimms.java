/*
  Copyrights:
 
  Copyright (c) 2014,2019,2020,2021 Smithsonian Astrophysical Observatory
 
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import info.*;
import captcha.*;

/******************************************************************************/
/**
 * Provide GUI support for the Pimms (Portable, Interactive
 * Multi-Mission Simulator) program including ACIS Pileup and
 * Background Count Estimation.
 */

public class Pimms extends HttpServlet implements ToolkitConstants
{
   String defaultMission = "CHANDRA-AO24";


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
		      HttpServletResponse response)
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
   * </ol>
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void service( HttpServletRequest request,
		       HttpServletResponse response)
    throws ServletException, IOException
  {
    Boolean badparams = false;
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


    HttpSession session = request.getSession( true );
    try {
    Toolkit.addProps(toolkitProperties);
    initializeValidationState( request );
    String value = (String) session.getAttribute( "output" );
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
    if (badparams) { operation= "CLEAR"; }

    //August 2004
    //Call the initalize routines - this allows us to make sure the
    //session has all the required information, without requiring cookies
    //to be enabled. 
    //If the request is to clear the values, that will happen in the
    //following section where operation equals clear
    initializeInputValues(request, false,session);
    initializeOutputValues(request, true);

    // Determine which operation to process.
    if ( operation == null) 
    {
       // System.err.println ("****PIMMS OPERATION is null*****************");
	//No need to do anything since the input and output values
	//have already been initialized
    } 
    else if ( operation.equals( "CLEAR" ) )
    {
      // Force the initial values to a reset state.
      initializeInputValues( request, true ,session);
      initializeOutputValues( request, true );
      session.setAttribute( "output", "Values cleared" );
    }
    else if ( operation.equals( "CALCULATE" ) )
    {
      // Do the calculation.
      calculate( request, session );
    }
    else if ( operation.equals( "VIEW OUTPUT" ) )
    {
      // The only thing to do is to disable warning messages.
      calculate( request, session );
      session.setAttribute( "pimmsWarnings", new Boolean( false ) );
    }
    else
    {
      String paramValue = Parameter.get(request, "pimmsPrediction" );
      if (paramValue == null || paramValue.length() > 40) {
        logger.info("clearing input values");
        initializeInputValues( request, true ,session);
      }
      session.setAttribute( "output", "Unknown operation!" );
    }

    // Generate utility values for the JSP modules.
    session.setAttribute( "showPileup", new Boolean( showPileup( session ) ) );
    session.setAttribute( "showEstimationInput",
		      new Boolean( showEstimationInput( session ) ) );
    session.setAttribute( "showEstimationOutput",
		      new Boolean( showEstimationOutput( session ) ) );
    } catch (Exception e) {
      initializeInputValues( request, true ,session);
      initializeOutputValues( request, true );
      session.setAttribute( "output", "Values cleared" );
      logger.error(e);
    }
  }

  /****************************************************************************/
  /**
   * Private variables
   */

  private Properties toolkitProperties;
  private Integer maxParamLength=30;
  private static Logger logger = Logger.getLogger(Pimms.class);


  // ***********************************************************************
  // Private methods  
  // ***********************************************************************

  private void calculate( HttpServletRequest request, HttpSession session )
  {
    String buffer = "<div class=\"transcript\" >";
    buffer += toolkitProperties.getProperty( "output.header.text" );

    try {
    // Validate the input parameters.
    ToolkitValidator validator =
      new ToolkitValidator( request, toolkitProperties );

    // Determine if we need to calculate the frame time first
    String frameMode = Parameter.get( request, "frameSelector" );
    if ( "C".equals( frameMode ) ) {
      String relPath =
	(String) toolkitProperties.getProperty( "ascds.release" );
      String fullcommand = relPath + "/bin/prop_frametime_exe";
      String command = "";

      String nbrChips = Parameter.get( request, "nbrchipsSelector" );
      command +=  " -q " + nbrChips;

      String instrument = Parameter.get( request, "outputInstrument" );
      StringTokenizer st = new StringTokenizer( instrument, "/" );
      String detector = st.nextToken();

      if ("ACIS-I".equals(detector) ) {
        command += " 1" ;
      }
      else if ("ACIS-S".equals(detector) ) {
        command += " 5" ;
      }
      String subarray = Parameter.get( request, "subarraySelector" );
      if ("1/2".equals(subarray) ) {
        command += " .5";
      }
      else if ("1/4".equals(subarray) ) {
        command += " .25";
      }
      else if ("1/8".equals(subarray) ) {
        command += " .125";
      }
      else {
        command += " 1";
      }
      //buffer += toolkitProperties.getProperty( "interactive.inputs.text" );
      //buffer += command;
      buffer += "<br><b>Frametime Results:</b>";
      fullcommand += command;
      logger.info(fullcommand);
      buffer += processFrameTime(request,session,fullcommand);
    }

    // Generate the validation state.
    String inputKey = validator.getEnergyRangeKey( "input" );
    String outputKey = validator.getEnergyRangeKey( "output" );

    String fluxparams = "flux.limits";
    String energyLbl = "inputEnergyLow";
    String energyHighLbl = "inputEnergyHigh";
    String outenergyLbl = "outputEnergyLow";
    String outenergyHighLbl = "outputEnergyHigh";
    if ( "flux".equals(Parameter.get(request,"inputMode") ) ) {
       energyLbl = "inputFluxEnergyLow";
       energyHighLbl = "inputFluxEnergyHigh";
    }
    if ( "density".equals(Parameter.get(request,"inputMode") ) ) {
       fluxparams = "density.limits";
       energyLbl = "inputDensityEnergyLow";
       energyHighLbl = "inputDensityEnergyHigh";
    }
    if ( "flux".equals(Parameter.get(request,"outputMode") ) ) {
       outenergyLbl = "outputFluxEnergyLow";
       outenergyHighLbl = "outputFluxEnergyHigh";
    }
    if ( "density".equals(Parameter.get(request,"outputMode") ) ) {
       outenergyLbl = "outputDensityEnergyLow";
       outenergyHighLbl = "outputDensityEnergyHigh";
    }

    //Make sure that if there is a redshift specified, then the redshifted NH
    //is valid.  Also ensure that there is no redshifted NH if there is no redshift
    String inputRedshift = Parameter.get(request, "redshift" );
    String inputRedshiftedNH = Parameter.get(request, "redshiftedNH" );
    String inputNH = Parameter.get( request, "NH" );
    String redshiftedNH_errorMsg = new String();
    String redshiftNH_valid = new String();

    if (inputNH == null) { inputNH = ""; }
    if (inputRedshift == null) { inputRedshift = ""; }
    if (inputRedshiftedNH == null) { inputRedshiftedNH = ""; }


    if(inputRedshiftedNH.length() > 0) {

	if(validator.validateParameter( "redshiftedNH.input", "redshiftedNHLabelBGColor", "redshiftedNH",
					"redshiftedNH.limits" ) != null) {

	    redshiftNH_valid = new String(validator.validateParameter( "redshiftedNH.input", 
								       "redshiftedNHLabelBGColor", 
								       "redshiftedNH",
								       "redshiftedNH.limits" ));
	    session.setAttribute( "redshift", inputRedshift);
	    session.setAttribute( "redshiftedNH", inputRedshiftedNH);
	}
    }

    // if redshift(z) not entered, but Redshifted NH entered -> error
    if(inputRedshift.length() == 0 && 
       inputRedshiftedNH.length() > 0) {
	//Error!  Cannot specify a redshiftedNH without a redshift
	StringBuffer sb = new StringBuffer("<br>Please specify z for redshifted NH.");
	session.setAttribute( "redshiftLabelBGColor", ToolkitValidator.BG_COLOR_ERROR);
	session.setAttribute( "redshift", "");
	session.setAttribute( "redshiftedNH", inputRedshiftedNH);
	redshiftedNH_errorMsg = new String(sb);
    }
    // if redshift(z) entered, but Galactic NH not entered -> error
    if(inputNH.length() == 0 && 
       inputRedshift.length() > 0) {
	//Error!  Cannot specify a redshiftedNH without an Galactic NH
	StringBuffer sb = new StringBuffer("<br>Please specify Galactic NH for redshift(z)");
	session.setAttribute( "NHLabelBGColor", ToolkitValidator.BG_COLOR_ERROR);
	session.setAttribute( "NH", "");
	session.setAttribute( "redshiftedNH", inputRedshiftedNH);
	redshiftedNH_errorMsg = new String(sb);
    }

    String msnfld="NOMISSION";
    String msnfld2="NOMISSION";
    if ("mission".equals(Parameter.get(request,"inputMode") )) 
       msnfld="inputMissionSelector";
    if ("mission".equals(Parameter.get(request,"outputMode") ))
       msnfld2="outputMissionSelector";
    
   logger.trace("msnfld= " + msnfld +  "   msnfld2=" + msnfld2);

   String[] errors = {
      validator.validateMission(msnfld,"inputMissionBGColor","inputInstrument","inputInstBGColor"),
      validator.validateMission(msnfld2,"outputMissionBGColor","outputInstrument","outputInstBGColor"),
      validator.validateRangeLow( "low.input.energy",
                                  "inputEnergyLowLabelBGColor", inputKey,
                                  energyLbl,energyHighLbl,
                                  Parameter.get(request,"inputMode")  ),
      validator.validateRangeHigh( "high.input.energy",
                                   "inputEnergyHighLabelBGColor", inputKey,
                                  energyLbl,energyHighLbl,
                                  Parameter.get(request,"inputMode")  ),
      validator.validateRangeLow( "low.output.energy",
                                  "outputEnergyLowLabelBGColor", outputKey,
                                  outenergyLbl,outenergyHighLbl ,
                                  Parameter.get(request,"outputMode")  ),
      validator.validateRangeHigh( "high.output.energy",
                                   "outputEnergyHighLabelBGColor", outputKey,
                                  outenergyLbl,outenergyHighLbl ,
                                  Parameter.get(request,"outputMode")  ),
      //Cycle 6 - NH can be empty, defaults to a value of 0
      validator.validateParameter( "nh.input", "nhLabelBGColor", "NH",
                                  "nh.limits" ),
      validator.validateParameter( "photon-index.input", 
                                   "photonIndexLabelBGColor", "photonIndex" ),
      validator.validateParameter( "kt.input", "ktLabelBGColor", "kT",
                                   "kt.limits" ),
      validator.validateParameter( "count-rate.input", "countRateLabelBGColor",
                                   "countRate", "count-rate.limits" ),
      validator.validateParameter( "absorbed-flux.input",
                                   "absorbedFluxLabelBGColor",
                                   "absorbedFlux", fluxparams ),
      validator.validateParameter( "unabsorbed-flux.input",
                                   "unabsorbedFluxLabelBGColor",
                                   "unabsorbedFlux", fluxparams ),
      validator.validateParameter( "frame-time.input", RE_FRAMETIME,
                                   "frameTimeLabelBGColor",
                                   "frameTime", "frame-time.limits" ),
      redshiftNH_valid,
      redshiftedNH_errorMsg
    };


    // Do the calculation only if no errors were tripped.
    if ( !validator.errorsOccurred( errors ) ) {
      String par;
      session.setAttribute( "output", "Computing" );

      // Set the proposal environment variables using a property file to
      // access the values.

      // Setup the environment variables based on  property.  
      // Log the variable values to the output.
      String relPath =
	(String) toolkitProperties.getProperty( "ascds.release" );
      String pimmsDataPath =
	(String) toolkitProperties.getProperty( "pimms.data.path" );
      String dataVar = "ASCDS_PROP_PMS_DATA=" + pimmsDataPath ;
      String modelVar =
	"ASCDS_PROP_PMS_MODEL=" + relPath + "/config/pimms/models";
      String ldlibDataVar = "LD_LIBRARY_PATH=" + relPath + "/lib:" + relPath + "/ots/lib" ;
      String[] environment = { modelVar, dataVar,ldlibDataVar };
      //buffer += toolkitProperties.getProperty( "output.environment.text" );
      //buffer += modelVar + "<br>";
      //buffer += dataVar + "<br>";

      // Generate, execute and log the command to execute Pimms.
      String inputMode = Parameter.get( request, "inputMode" );
      String outputMode = Parameter.get( request, "outputMode" );
      String command = relPath + "/bin/prop_pimms_exe";
      //buffer += toolkitProperties.getProperty( "interactive.command.text" );
      //buffer += command + "<br>";
      buffer += toolkitProperties.getProperty( "interactive.inputs.text" );
      String notemsg = " ";

      String subCmd = "";
      try 
      {
	Runtime runtime = Runtime.getRuntime();
	Process process = runtime.exec( command, environment );
	PrintWriter out = new PrintWriter( process.getOutputStream() );

	// Case on the input/output modes
	if ( "mission".equals( inputMode ) )
	{
	  if ( "mission".equals( outputMode ) )
	    subCmd += processMissionMission( request, session, out );
	  else if ( "flux".equals( outputMode ) )
	  {
	    String flux = Parameter.get( request, "outputFluxSelector" );
	    boolean flag = "Unabsorbed".equals( flux );
	    subCmd += processMissionFlux( request, session, out, flag );
	  }
          else
	  {
	    String flux = Parameter.get( request, "outputFluxSelector" );
	    boolean flag = "Unabsorbed".equals( flux );
	    subCmd += processMissionDensity( request, session, out, flag );
	  }
	}
	else if ( "flux".equals( inputMode ) )
	{
	  String flux = Parameter.get( request, "inputFluxSelector" );
	  boolean inputFlag = "Unabsorbed".equals( flux );
	  if ( "mission".equals( outputMode ) )
	    subCmd += processFluxMission( request, session, out, inputFlag );
	  else if ( "flux".equals( outputMode ) )
	  {
	    flux = Parameter.get( request, "outputFluxSelector" );
	    boolean outputFlag = "Unabsorbed".equals( flux );
	    subCmd +=
	      processFluxFlux( request, session, out, inputFlag, outputFlag );
	  }
	  else 
	  {
	    flux = Parameter.get( request, "outputFluxSelector" );
	    boolean outputFlag = "Unabsorbed".equals( flux );
	    subCmd +=
	      processFluxDensity( request, session, out, inputFlag, outputFlag );
	  }
	}
        // must be density
	else 
	{
	  String flux = Parameter.get( request, "inputFluxSelector" );
	  boolean inputFlag = "Unabsorbed".equals( flux );
	  if ( "mission".equals( outputMode ) )
	    subCmd += processDensityMission( request, session, out, inputFlag );
	  else if ( "flux".equals( outputMode ) )
	  {
	    flux = Parameter.get( request, "outputFluxSelector" );
	    boolean outputFlag = "Unabsorbed".equals( flux );
	    subCmd +=
	      processDensityFlux( request, session, out, inputFlag, outputFlag );
	  }
          else 
	  {
	    flux = Parameter.get( request, "outputFluxSelector" );
	    boolean outputFlag = "Unabsorbed".equals( flux );
	    subCmd +=
	      processDensityDensity( request, session, out, inputFlag, outputFlag );
	  }
	}


	// Finish the command and flush the stream.
        logger.info(command + " " + subCmd);
        buffer += subCmd;
	buffer += toolkitProperties.getProperty( "output.transcript.text" );
	out.println( "QUIT" );
	out.flush();

        InputStreamReader reader = new InputStreamReader( process.getInputStream() );
	BufferedReader in = new BufferedReader( reader);
	// Read each line of output from the prop_pimms_exe command.
	String line;
	while ( (line = in.readLine()) != null )
	{
	  buffer += line + "<br>";
          if (line.indexOf("a more reasonable point source extraction") > 0 ) {
            notemsg += "Note: " + line.replace('(',' ') ;
          }
          if (line.indexOf("arcsec radius would be roughly") > 0)  {
            notemsg += line.replace(')',' ')  + "<br>\n";
          } 
	}

        InputStreamReader ereader = new InputStreamReader( process.getErrorStream() );
	BufferedReader ein = new BufferedReader( ereader);
        String ebuffer = null;
	while ( (line = ein.readLine()) != null )
	{
          ebuffer += line;
        }
        if (ebuffer != null) {
          logger.info("PimmsError: " + ebuffer);
        }
    
	out.close();
	in.close();
	ein.close();
        process.getInputStream().close();
        process.getOutputStream().close();
        process.getErrorStream().close();

      }
      catch ( IOException exc )
      {
        //exc.printStackTrace();
        logger.error( exc );
	String message = "Got an IO exception generating/getting the pimms output.";
	logger.error( message );
	buffer += message + "<br>";
      }

      // Deal with any warnings.  First check for a pileup warning.
      String warnings = (String) session.getAttribute( "pimmsWarningsScript" );
      String mission = (String) session.getAttribute( "outputMissionSelector" );
      String instrument = (String) session.getAttribute( "outputInstrument" );
      String source = Parameter.get( request, "source" );
      if ( mission.startsWith( "XMM" ) && instrument.startsWith("MOS"))  {
         notemsg += "Note: The estimated MOS rates are per instrument.  Integration over the entire chip (not just in the source region) assumed.<br><font color='red'> Warning: MOS1 Timing mode data now suffers from an out-of-scale column which results in 20-30% loss of counts for sources at the nominal position.</font><br>\n";
      }

      if ( mission.startsWith( "CHANDRA" ) &&
	   instrument.startsWith( "ACIS" ) &&
	   !instrument.endsWith( "None/None" ) && 
	   source.startsWith( "point" ) )
      {
	warnings += "\\n" + toolkitProperties.getProperty( "pileup.warning.text" );
      }

      // Now set the warnings flag and wrap the reasons around a
      // JavaScript popup.
      session.setAttribute("notemsg",notemsg);
      session.setAttribute("warningmsg","");

      if ( warnings.length() > 0 )
      {
	session.setAttribute( "pimmsWarnings", new Boolean( true ) );
	session.setAttribute( "pimmsWarningsScript", 
			  Toolkit.buildWarningsScript( warnings ) );
        warnings = "";
      }

      // Now parse the pimms output.
      String prediction;
      String buffer_pre;
      try   {
	  // Generate a regular expression object to extract the result
	  // from. The expression is dependent on the output mode.
	  String regexpStr = "";
	  String matcherInputStr = "";
	  
	  Pattern regexpErrPattern = Pattern.compile("ERROR");
	  Matcher regexpErrMatcher = regexpErrPattern.matcher(buffer);
	  
	  if ( regexpErrMatcher.find() ) {
	      prediction = "See Output";
	      buffer_pre = prediction;
	      session.setAttribute( "pimmsPrediction", prediction );
	  } else if ( "flux".equals( outputMode ) ) {
	      Pattern regexpPrePattern = Pattern.compile("predicts ([^,]+)cm");
	      Matcher regexpPreMatcher = regexpPrePattern.matcher(buffer);
	      
	      if ( !regexpPreMatcher.find() ) {
		  prediction = "See Output";
		  buffer_pre = prediction;
		  session.setAttribute( "pimmsPrediction", prediction );
	      } else {
		  buffer_pre = regexpPreMatcher.group( 1 );
		  regexpStr = "of ([^:blank:]+) ergs" ;
		  matcherInputStr = new String( buffer_pre );

	      }
	  } else if ( "density".equals( outputMode ) ) {
	      Pattern regexpPrePattern = Pattern.compile("predicts ([^,]+)cm");
	      Matcher regexpPreMatcher = regexpPrePattern.matcher(buffer);
	    
	    if ( !regexpPreMatcher.find() ) {
		prediction = "See Output";
		buffer_pre = prediction;
		session.setAttribute( "pimmsPrediction", prediction );
	    } else {
		buffer_pre = regexpPreMatcher.group( 1 );
		regexpStr =  "keV of ([^:blank:]+) ergs";
		matcherInputStr = new String( buffer_pre );

	    }
	  } else {
	      regexpStr = "predicts ([^:blank:]+) cps";
	      matcherInputStr = new String(buffer);
	  }


	  Pattern regexpPattern = Pattern.compile( regexpStr );
	  Matcher regexpMatcher = regexpPattern.matcher( matcherInputStr );
	  
	  if ( !regexpMatcher.find() ) {
	      prediction = "See Output";
	      session.setAttribute( "pimmsPrediction", prediction );
	  } else {
	      prediction = regexpMatcher.group( 1 );
              //DecimalFormat df = new DecimalFormat();
              //df.setMaximumFractionDigits(20);
              //df.setMinimumIntegerDigits(0);
              //Double dval = new Double(prediction);
              //String tdbl = df.format(dval);
	      //session.setAttribute( "pimmsPrediction", tdbl );
	      session.setAttribute( "pimmsPrediction", prediction );
	  }
      } catch ( Exception exc)  {
	  prediction = "XXXXXX";
	  request.setAttribute( "pimmsPrediction", exc.getMessage() );
	  session.setAttribute( "pimmsPrediction", prediction );
	  buffer += exc.getMessage();
      }


      
      // Determine if pileup needs to be called.
      if ( (Parameter.has( request, "frameTime" ))  ||
           (Parameter.has( request, "subarraySelector")) )
      {
	// Generate the pileup command string and process the command.
	String frameTime = Parameter.get( request, "frameTime" );
	command = relPath + "/bin/prop_pileup_exe -q " + prediction +
	  " " + frameTime;
        //buffer += toolkitProperties.getProperty( "interactive.inputs.text" );
	//buffer +=  prediction + " " + frameTime;
	buffer +=  "<br><b>Pileup Results:</b>";
	buffer += processPileup( request, session, command );
      }

      // Determine the background estimation count rate.
      String backgroundCountRate = "";
      if ( showEstimationOutput( session ) )
      {
	// Use the given property to look up either a value or a
	// message.
	instrument = (String) session.getAttribute( "outputInstrument" );
 	String property = toolkitProperties.getProperty( "background." + instrument );
	String number = toolkitProperties.getProperty( property );
	if ( isANumber( number ) )
	{
	  // The lookup returned a number.  If the background source
	  // is an extended source then the background estimation
	  // value is a function of the size value.  Save the source
	  // and size parameters.
	  String extendedSize = Parameter.get( request, "extendedSize" );
	  double size = 0;
	  if ( extendedSize != null && extendedSize.length() > 0) {
            if (isANumber(extendedSize))
	    {
	      // If the extended size parameter is non-null then
	      // extended source is selected.  Convert the size
	      // parameter to a double.
	      size = Double.parseDouble( extendedSize );
            }
	  }
          
	  if ( "extended source".equals( source ) && size > 7.0 )
	  {
	    double rate = Double.parseDouble( number ) * size / 7.0;
	    session.setAttribute( "backgroundCountRate", String.valueOf( rate ) );
	  }
	  else
	  {
	    session.setAttribute( "backgroundCountRate", number );
	  }
	}
	else
	{
	  // The lookup returned a message.  Simply output it.
	  session.setAttribute( "backgroundCountRate", number );
	}
      }
      buffer += "</div>";

      // Append the environment and command inputs to the output
      // buffer.
      session.setAttribute( "output", buffer );
    }
    } catch (Exception exc) {
        request.setAttribute( "pimmsPrediction", "Invalid");
        session.setAttribute( "pimmsPrediction", "XXXXXX" );
        logger.error(exc.getMessage());
     };
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
   * @param session  the session
   */

  private void initializeInputValues( HttpServletRequest request, 
				      boolean clear,
				      HttpSession session )
  {

    Parameter.initialize( request, clear, "operation", "REFRESH" );
    Parameter.initialize( request, clear, "inputMode", "mission" );
    Parameter.initialize( request, clear, "outputMode", "mission" );
    Parameter.initialize( request, clear, "inputMissionSelector",defaultMission);
    Parameter.initialize( request, clear, "inputInstrument",
			  "ACIS-I/None/None" );
    Parameter.initialize( request, clear, "inputFluxSelector", "Absorbed" );
    Parameter.initialize( request, clear, "inputEnergyLow", "default" );
    Parameter.initialize( request, clear, "inputEnergyHigh", "default" );
    Parameter.initialize( request, clear, "inputFluxEnergyLow", "0.2" );
    Parameter.initialize( request, clear, "inputFluxEnergyHigh", "10.0" );
    Parameter.initialize( request, clear, "inputDensityEnergyLow", "1.0" );
    Parameter.initialize( request, clear, "inputDensityEnergyHigh", "10.0" );

    Parameter.initialize( request, clear, "outputMissionSelector",defaultMission);

    String outputMode = Parameter.get( request, "outputMode" );
    String mission = (String) session.getAttribute("outputMissionSelector" );
    String inst = Parameter.get(request,"outputInstrument" );
    if (outputMode != null && outputMode.equals("mission"))  {
      if ( mission != null && mission.startsWith("CHANDRA") &&
         inst != null && !inst.startsWith("ACIS") && !inst.startsWith("HRC")) {
          Parameter.initialize( request, true, "outputInstrument",
            "ACIS-I/None/None");
      }
      else {
        Parameter.initialize( request, clear, "outputInstrument",
            "ACIS-I/None/None");
      }
    }
    else {
        Parameter.initialize( request, clear, "outputInstrument",
            "ACIS-I/None/None");
    }
    Parameter.initialize( request, clear, "outputFluxSelector", "Absorbed" );
    Parameter.initialize( request, clear, "outputEnergyLow", "default" );
    Parameter.initialize( request, clear, "outputEnergyHigh", "default" );
    Parameter.initialize( request, clear, "outputDensityEnergyLow","1.0");
    Parameter.initialize( request, clear, "outputDensityEnergyHigh", "10.0" );
    Parameter.initialize( request, clear, "outputFluxEnergyLow", "0.2" );
    Parameter.initialize( request, clear, "outputFluxEnergyHigh", "10.0" );
    Parameter.initialize( request, clear, "modelSelector", "PL" );
    Parameter.initialize( request, clear, "NH", "" );
    Parameter.initialize( request, clear, "photonIndex", "" );
    Parameter.initialize( request, clear, "kT", "" );
    Parameter.initialize( request, clear, "absorbedFlux", "" );
    Parameter.initialize( request, clear, "unabsorbedFlux", "" );
    Parameter.initialize( request, clear, "countRate", "" );
    Parameter.initialize( request, clear, "frameSelector", "Specify" );
    Parameter.initialize( request, clear, "frameTime", "3.2" );
    Parameter.initialize( request, clear, "subarraySelector", "None" );
    Parameter.initialize( request, clear, "nbrchipsSelector", "6" );
    Parameter.initialize( request, clear, "abundance", "" );
    Parameter.initialize( request, clear, "logT", "" );
    Parameter.initialize( request, clear, "redshift", "" );
    Parameter.initialize( request, clear, "redshiftedNH", "" );


    if (outputMode != null && outputMode.equals("mission")   &&
        mission != null && !mission.startsWith(defaultMission) ) {
      Parameter.initialize( request, true, "source", "point source" );
    } else {
      Parameter.initialize( request, clear, "source", "point source" );
    }
    Parameter.initialize( request, clear, "extendedSize", "0.0" );
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
    Parameter.initialize( request, clear, "backgroundCountRate", "" );
    Parameter.initialize( request, clear, "countsPerFrame", "" );
    Parameter.initialize( request, clear, "countsPerSecond", "" );
    Parameter.initialize( request, clear, "pileup", "" );
    Parameter.initialize( request, clear, "pimmsWarnings",
			  new Boolean( false ) );
    Parameter.initialize( request, clear, "pimmsWarningsScript", "" );
    Parameter.initialize( request, clear, "pimmsPrediction", "" );
    Parameter.initialize( request, clear, "warningmsg", " " );
    Parameter.initialize( request, clear, "notemsg", " " );

  }

  /****************************************************************************/
  /**
   * Determine if a given string is a number, i.e. an integer, decimal
   * or scientific notation value.
   *
   * @param number The string to test.
   *
   * @return A boolean indication.
   */

  private boolean isANumber( String number )
  {
    // Initialize for success.
    boolean result = true;

    String str = number.trim();
    try
    {
      double value = Double.parseDouble( str );
    }
    catch ( NumberFormatException exc )
    {
      result = false;
      if (number != null) 
        logger.error("isANumber: " + number + " is not a number");
      else
        logger.error("isANumber: number is null");
    }

    return result;
  }

  // ***********************************************************************
  //  Set the session validation state.  
  // ***********************************************************************

  private void initializeValidationState( HttpServletRequest request )
  {
    // Get the session object.
    HttpSession session = request.getSession( true );

    // Initialize the error flag and the output.
    session.setAttribute( "inputErrors", new Boolean( false ) );
    session.setAttribute( "inputMissionBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "inputInstBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "outputMissionBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "outputInstBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "inputEnergyLowLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "inputEnergyHighLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "outputEnergyLowLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "outputEnergyHighLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "nhLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "redshiftLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "redshiftedNHLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "photonIndexLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "ktLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "countRateLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "absorbedFluxLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "unabsorbedFluxLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "frameTimeLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "subarrayLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "nbrchipsLabelBGColor",
		      ToolkitValidator.BG_COLOR_NORMAL );
  }

  // *************************************************************************

  private String processPileup( HttpServletRequest request,
				HttpSession session, 
				String command )
  {
    String buffer = "<br>";

    // Now execute the command, collect and parse the command output.
    try
    {
      // Execute the command and setup to get the command output.
      Runtime runtime = Runtime.getRuntime();
      logger.info(command);
      Process process = runtime.exec( command );
      InputStreamReader reader = new InputStreamReader( process.getInputStream() );
      BufferedReader in = new BufferedReader( reader );
	
      // Read each line of output from the pileup command.
      String line;
      String pileupBuffer = "";
      while ( (line = in.readLine()) != null )
      {
	pileupBuffer += line + " ";
	buffer += line + "<br>";
      }
      in.close();
      process.getInputStream().close();
      process.getOutputStream().close();
      process.getErrorStream().close();
      logger.info("pimms command done");

      // Parse the pileup percentage output.
      Pattern regexp = Pattern.compile("pileup_pct=([^:blank:]+) " );
      Matcher matcher = regexp.matcher( pileupBuffer );
      if ( matcher.find() )
      {
	String pileup = matcher.group( 1 );
	session.setAttribute( "pileup", pileup );
      }

      // Parse the pileup counts/frame.
      regexp = Pattern.compile("counts/frame=([^:blank:]+) " );
      matcher = regexp.matcher( pileupBuffer );
      if ( matcher.find() )
      {
	String countsPerFrame = matcher.group( 1 );
        try {
           DecimalFormat df = new DecimalFormat("0.##E0");
           df.setMaximumFractionDigits(20);
           df.setMinimumIntegerDigits(1);
           Double dval = new Double(countsPerFrame);
           countsPerFrame = df.format(dval);
        } catch (Exception exc) {
          logger.error( exc );
        }
	session.setAttribute( "countsPerFrame", countsPerFrame );
      }

      // Parse the pileup counts/sec.
      regexp = Pattern.compile( "counts/sec=([^:blank:]+) " );
      matcher = regexp.matcher( pileupBuffer );

      if ( matcher.find() )
      {
	String countsPerSecond = matcher.group( 1 );
        try {
           DecimalFormat df = new DecimalFormat("0.##E0");
           df.setMaximumFractionDigits(20);
           df.setMinimumIntegerDigits(1);
           Double dval = new Double(countsPerSecond);
           countsPerSecond = df.format(dval);
        } catch (Exception exc) {
          logger.error( exc );
        }
	session.setAttribute( "countsPerSecond", countsPerSecond );
      }

      // Parse the warning message, if any.
      regexp = Pattern.compile( "(Warning: .*\\.)" );
      matcher = regexp.matcher( pileupBuffer );

      if ( matcher.find() )
      {
	String warning = matcher.group( 1 );
	if ( warning != null )
	{
          session.setAttribute("warningmsg",warning);
	  // Enable the warnings flag and output a suitable script to
	  // display the script.
	  //session.setAttribute( "pimmsWarnings", new Boolean( true ) );
	  //String script =
	    //"<script type=\"text/javascript\"> alert( \"" + warning + "\\n";
	  //script += "\\n\" ); </script>";
	  //session.setAttribute( "pimmsWarningsScript", script );
	}
      }
      else
      {
	buffer += "No warning message.<br>";
      }
    }
    catch ( IOException ioExc )
    {
      String message = "Got an IO exception generating/getting the pileup output.";
      logger.error( message );
      logger.error( ioExc );
      buffer += message + "<br>";
    }
    catch ( Exception exc )//REException reExc )
    {
      logger.error( exc );
      String message = "Got RE exception parsing pileup output.";
      logger.error( message );
      buffer += message + "<br>";
    }
    return buffer;
  }

  // ***********************************************************************

  private String processMissionMission( HttpServletRequest request,
					HttpSession session,
					PrintWriter out )
  {
    // Do the PIMMS subcommands.
    String buffer = processMissionFromCommand( request, session, out );
    buffer += processModelCommand( request, session, out );
    buffer += processMissionInstrumentCommand( request, session, out );
    buffer += processMissionGoCommand( request, session, out );
    return buffer;
  }

  // ***********************************************************************

  private String processMissionFlux( HttpServletRequest request,
				     HttpSession session,
				     PrintWriter out,
				     boolean outputIsUnabsorbed )
  {
    // Do the PIMMS subcommands.
    String buffer = processMissionFromCommand( request, session, out );
    buffer += processModelCommand( request, session, out );
    buffer +=
      processFluxInstrumentCommand( request, session, out, outputIsUnabsorbed );
    buffer += processMissionGoCommand( request, session, out );
    return buffer;
  }

  // ***********************************************************************

  private String processMissionDensity( HttpServletRequest request,
				     HttpSession session,
				     PrintWriter out,
				     boolean outputIsUnabsorbed )
  {
    // Do the PIMMS subcommands.
    String buffer = processMissionFromCommand( request, session, out );
    buffer += processModelCommand( request, session, out );
    buffer +=
      processDensityInstrumentCommand( request, session, out, outputIsUnabsorbed );
    buffer += processMissionGoCommand( request, session, out );
    return buffer;
  }


  // ***********************************************************************

  private String processFluxMission( HttpServletRequest request,
				     HttpSession session,
				     PrintWriter out,
				     boolean inputIsUnabsorbed )
  {
    // Do the PIMMS subcommands.
    String buffer =
      processFluxFromCommand( request, session, out, inputIsUnabsorbed );
    buffer += processModelCommand( request, session, out );
    buffer += processMissionInstrumentCommand( request, session, out );
    buffer +=
      processFluxGoCommand( request, session, out, inputIsUnabsorbed );
    return buffer;
  }

  // ***********************************************************************
  private String processFluxFlux( HttpServletRequest request,
					  HttpSession session,
					  PrintWriter out,
					  boolean inputIsUnabsorbed,
					  boolean outputIsUnabsorbed )
  {
    // Do the PIMMS subcommands.
    String buffer =
      processFluxFromCommand( request, session, out, inputIsUnabsorbed );
    buffer += processModelCommand( request, session, out );
    buffer +=
      processFluxInstrumentCommand( request, session, out, outputIsUnabsorbed );
    buffer += processFluxGoCommand( request, session, out, inputIsUnabsorbed );
    return buffer;
  }

  // ***********************************************************************
  private String processFluxDensity( HttpServletRequest request,
					  HttpSession session,
					  PrintWriter out,
					  boolean inputIsUnabsorbed,
					  boolean outputIsUnabsorbed )
  {
    // Do the PIMMS subcommands.
    String buffer =
      processFluxFromCommand( request, session, out, inputIsUnabsorbed );
    buffer += processModelCommand( request, session, out );
    buffer +=
      processDensityInstrumentCommand( request, session, out, outputIsUnabsorbed );
    buffer += processFluxGoCommand( request, session, out, inputIsUnabsorbed );
    return buffer;
  }


  // ***********************************************************************

  private String processDensityMission( HttpServletRequest request,
				     HttpSession session,
				     PrintWriter out,
				     boolean inputIsUnabsorbed )
  {
    // Do the PIMMS subcommands.
    String buffer =
      processDensityFromCommand( request, session, out, inputIsUnabsorbed );
    buffer += processModelCommand( request, session, out );
    buffer += processMissionInstrumentCommand( request, session, out );
    buffer +=
      processFluxGoCommand( request, session, out, inputIsUnabsorbed );
    return buffer;
  }


  // ***********************************************************************
  private String processDensityFlux( HttpServletRequest request,
					  HttpSession session,
					  PrintWriter out,
					  boolean inputIsUnabsorbed,
					  boolean outputIsUnabsorbed )
  {
    // Do the PIMMS subcommands.
    String buffer =
      processDensityFromCommand( request, session, out, inputIsUnabsorbed );
    buffer += processModelCommand( request, session, out );
    buffer +=
      processFluxInstrumentCommand( request, session, out, outputIsUnabsorbed );
    buffer += processFluxGoCommand( request, session, out, inputIsUnabsorbed );
    return buffer;
  }

  // ***********************************************************************
  private String processDensityDensity( HttpServletRequest request,
					  HttpSession session,
					  PrintWriter out,
					  boolean inputIsUnabsorbed,
					  boolean outputIsUnabsorbed )
  {
    // Do the PIMMS subcommands.
    String buffer =
      processDensityFromCommand( request, session, out, inputIsUnabsorbed );
    buffer += processModelCommand( request, session, out );
    buffer +=
      processDensityInstrumentCommand( request, session, out, outputIsUnabsorbed );
    buffer += processFluxGoCommand( request, session, out, inputIsUnabsorbed );
    return buffer;
  }

  // ***********************************************************************

  private String processMissionFromCommand( HttpServletRequest request,
					    HttpSession session,
					    PrintWriter out )
  {
    // Generate and output the FROM subcommand.
    String mission = Parameter.get( request, "inputMissionSelector" );
    Energy energy = new Energy( request, "input", toolkitProperties );
    String instrument = Parameter.get( request, "inputInstrument" );
    String detector = "";
    String grating = "None";
    String filter = "None";
    String command = "";
    if (instrument == null) instrument="";
    if (mission == null) mission="";

    try {
    StringTokenizer st = new StringTokenizer( instrument, "/" );
    if (st != null && st.hasMoreTokens()) 
      detector = st.nextToken();
    if (st != null && st.hasMoreTokens()) 
      grating = st.nextToken();
    if (st != null && st.hasMoreTokens()) 
      filter = st.nextToken();
    String cmdMission = mission;
    if (detector.equals("None")) detector = "";
    
    command = "FROM " + cmdMission + " ";


    if ( !grating.equals( "None" ) )
	command += grating + "-";
    command += detector;
    if ( !"None".equals( filter ) )
	command += " " + filter;

    if (energy.getLow() != null && !energy.getLow().equals("")) {
      command += processEnergyHiLo(energy);
    }
    } catch (Exception e) {
      command = "Unknown";
    }
    String buffer = command + "<br>";
    out.println( command );
    return buffer;
  }

  // ***********************************************************************

  private String processDensityFromCommand( HttpServletRequest request,
					 HttpSession session,
					 PrintWriter out,
					 boolean isUnabsorbed )
  {
    // Generate and output the FROM subcommand.
    String command="";
    try {
    Energy energy = new Energy( request, "inputDensity", toolkitProperties );
    if (energy.getLow() != null && !energy.getLow().equals("")) {
      DecimalFormat df = new DecimalFormat();
      df.setMaximumFractionDigits(9);
      df.setMinimumFractionDigits(1);
      df.setMinimumIntegerDigits(0);

      Double dval = new Double(energy.getLow());
      String tdbl = df.format(dval);
      command = "FROM DENSITY ERGS " + tdbl;
      if ( isUnabsorbed )
        command += " UNABSORBED";
    }
    } catch (Exception e) {
      command="Unknown";
    }
    String buffer = command + "<br>";
    out.println( command );
    return buffer;
  }

  // ***********************************************************************

  private String processFluxFromCommand( HttpServletRequest request,
					 HttpSession session,
					 PrintWriter out,
					 boolean isUnabsorbed )
  {
    // Generate and output the FROM subcommand.
    String command="";
    try {
    Energy energy = new Energy( request, "inputFlux", toolkitProperties );
    command = "FROM FLUX ERGS ";
    command += processEnergyHiLo(energy);
    if ( isUnabsorbed )
      command += " UNABSORBED";
    } catch (Exception e) {
      command="Unknown";
    }
    String buffer = command + "<br>";
    out.println( command );
    return buffer;
  }

  // ***********************************************************************

  private String processModelCommand( HttpServletRequest request,
				    HttpSession session,
				    PrintWriter out )
  {
    // Generate and output the MODEL subcommand.
    String plasma = "";
    String par;
    String model = Parameter.get( request, "modelSelector" );


    String nh = Parameter.get( request, "NH" );
    if (nh != null) 
      nh = nh.trim();
    if(nh == null || nh.length() == 0) 
       nh = "0";

    String redshift = Parameter.get( request, "redshift" );
    String redshiftedNH = Parameter.get( request, "redshiftedNH" );
    if (redshift.length() > 0 && redshiftedNH.length() == 0) {
       redshiftedNH = "0";
    }

    if ( model != null && ("BB".equals( model ) || "TB".equals( model )) )
    {
      // Deal with a ? model.
      par = Parameter.get( request, "kT" );
    }
    else if ( model != null &&
            ( "RS".equals( model ) || "APEC".equals(model) || 
              "MEKAL".equals(model)) )
    {
      // Deal with a Plasma model.
      plasma = " PLASMA " + model;
      model = "plasma";
      String abundance = Parameter.get( request, "abundance" );
      String logT = Parameter.get( request, "logT" );
      if (abundance == null || abundance.length() <= 0) {
         abundance = ".2";
      }
      if (logT == null || logT.length() <= 0) {
         logT = "5.95 | 0.0768";
      }
      par = logT.substring(0,4) + " logT " + abundance;
    }
    else
    {
      // Deal with a Power Law model.
      model = "PL" ;
      par = Parameter.get( request, "photonIndex" );
    }
    if (par == null || par.length() == 0 ) {
       par = "1";
    }

    //Initialize command variable, then depending on input parameters, build
    //the actual string
    String command = null;
    String pimmsModel = " MODEL " + model + " " ;
    
    if(redshift.length() == 0) {
	// no redshift specified
	StringBuffer sb = new StringBuffer(pimmsModel + par + " " + nh);
    	command = new String(sb);
	
    } else {
	// redshift specified, determine if there was a redshifted NH given
	if(redshiftedNH.length() > 0) {
	    StringBuffer sb = new StringBuffer(pimmsModel + par + " " + redshiftedNH + " z " + redshift + " " + nh);
	    command = new String(sb);
	} else {
	    StringBuffer sb = new StringBuffer(pimmsModel +  par + " " + nh + " z " + redshift);
	    command = new String(sb);
	}
    }
   

    String buffer = command + "<br>";
    out.println( command );
    if (plasma.length() > 0) {
      out.println( plasma );
      buffer += plasma + "<br>";
    }
    return buffer;
  }


  // ***********************************************************************
  private String processMissionInstrumentCommand( HttpServletRequest request,
						HttpSession session,
						PrintWriter out )
  {
    String command;
    try {
    // Generate the INSTRUMENT command input.
    String mission = Parameter.get( request, "outputMissionSelector" );
    Energy energy = new Energy( request, "output", toolkitProperties );
    String instrument = Parameter.get( request, "outputInstrument" );
    StringTokenizer st = new StringTokenizer( instrument, "/" );
    String detector = "";
    String grating = "None";
    String filter = "None";
    if (st != null && st.hasMoreTokens()) 
      detector = st.nextToken();
    if (st != null && st.hasMoreTokens()) 
      grating = st.nextToken();
    if (st != null && st.hasMoreTokens()) 
      filter = st.nextToken();
    if (detector.equals("None")) detector = "";
    String cmdMission = mission;

    logger.info("Output Mission is : " + cmdMission);
       
    command = "INSTRUMENT " + cmdMission + " ";

    if ( !grating.equals( "None" ) )
	command += grating + "-";
    command += detector;
    if ( !"None".equals( filter ) )
    {
      command += " " + filter;
    }
    if (energy.getLow() != null && !energy.getLow().equals("")) {
      command += processEnergyHiLo(energy);
    }
    } catch (Exception e) {
      command="Unknown";
    }
 
    String buffer = command + "<br>";
    out.println( command );
    return buffer;
  }

  private String processEnergyHiLo(Energy energy) 
  {
    String command = "";
    try {
      DecimalFormat df = new DecimalFormat();
      df.setMaximumFractionDigits(9);
      df.setMinimumFractionDigits(1);
      df.setMinimumIntegerDigits(0);

      Double dval = new Double(energy.getLow());
      String tdbl = df.format(dval);
      dval = new Double(energy.getHigh());
      String tdbh = df.format(dval);
      command += " " + tdbl.replaceAll(",","") + "-" + tdbh.replaceAll(",","");
    } catch (Exception e) {
      command = " 0 - 0 ";
    }
    return command;

  }

  // ***********************************************************************

  private String processFluxInstrumentCommand( HttpServletRequest request,
					       HttpSession session,
					       PrintWriter out,
					       boolean isUnabsorbed )
  {
    // Generate the INSTRUMENT command input.
    Energy energy = new Energy( request, "outputFlux", toolkitProperties );
    String command = "INSTRUMENT FLUX ERGS ";
    command += processEnergyHiLo(energy);
    if ( isUnabsorbed )
      command += " UNABSORBED";
    String buffer = command + "<br>";
    out.println( command );
    return buffer;
  }
  // ***********************************************************************

  private String processDensityInstrumentCommand( HttpServletRequest request,
					       HttpSession session,
					       PrintWriter out,
					       boolean isUnabsorbed )
  {
    // Generate the INSTRUMENT command input.
    Energy energy = new Energy( request, "outputDensity", toolkitProperties );
    String command = "INSTRUMENT DENSITY ERGS " + energy.getLow() ;
    if ( isUnabsorbed )
      command += " UNABSORBED";
    String buffer = command + "<br>";
    out.println( command );
    return buffer;
  }

  // ***********************************************************************

  private String processMissionGoCommand( HttpServletRequest request,
					  HttpSession session,
					  PrintWriter out )
  {
    // Generate the mission GO subcommand.
    String countRate = Parameter.get( request, "countRate" );
    if (countRate==null || countRate.length() == 0) { countRate = "0"; }
    String command = "GO " + countRate;
    String buffer = command + "<br>";
    out.println( command );
    return buffer;
  }

  // ***********************************************************************

  private String processFluxGoCommand( HttpServletRequest request,
				       HttpSession session,
				       PrintWriter out,
				       boolean isUnabsorbed )
  {
    // Generate the flux GO subcommand.
    String fluxParameter = isUnabsorbed ? "unabsorbedFlux" : "absorbedFlux";
    String flux = Parameter.get( request, fluxParameter );
    if (flux==null || flux.length() == 0) { flux = "0"; }
    String command = "GO " + flux;
    String buffer = command + "<br>";
    out.println( command );
    return buffer;
  }

  // ***********************************************************************

  private boolean showEstimationInput( HttpSession session )
  {
    String instrument = (String) session.getAttribute( "outputInstrument" );
    String mission = (String) session.getAttribute( "outputMissionSelector" );
    return ("mission".equals( session.getAttribute( "outputMode" ) ) &&
      mission.indexOf(defaultMission) >= 0 &&
      ( instrument.startsWith( "ACIS" ) ||
	( instrument.startsWith( "HRC" ) && 
	  instrument.endsWith( "None/None" ) ) ));
  }

  // ***********************************************************************

  private boolean showEstimationOutput( HttpSession session )
  {
    String instrument = (String) session.getAttribute( "outputInstrument" );
    String mission = (String) session.getAttribute( "outputMissionSelector" );
    return ( "mission".equals( session.getAttribute( "outputMode" ) ) &&
              mission.indexOf("CHANDRA-") >= 0 &&
	     ( instrument.endsWith( "None/None" ) ||
	       instrument.startsWith( "HRC-S/LETG" ) ) );
  }

  // ***********************************************************************
  //
  // Check that the output mode is either "null" (initial request) or
  // "mission", in which case the instrument must specify one of the
  // "ACIS" types in order to enable pileup parameter displays.
  // ***********************************************************************

  private boolean showPileup( HttpSession session )
  {
    boolean result = false;

    String mode = (String) session.getAttribute( "outputMode" );
    if ( mode == null || "mission".equals( mode ) )
    {
      String mission = (String) session.getAttribute( "outputMissionSelector" );
      String instrument = (String) session.getAttribute( "outputInstrument" );
      String source = (String) session.getAttribute( "source" );
      result = instrument == null || 
	( mission.startsWith("CHANDRA") && instrument.startsWith( "ACIS" ) &&
	  "point source".equals( source ) );
    }
    return result;
  }


  // ***********************************************************************

  private String processFrameTime( HttpServletRequest request,
				HttpSession session, 
				String command )
  {
    String buffer = "<br>";

    // Now execute the command, collect and parse the command output.
    try
    {
      // Execute the command and setup to get the command output.
      Runtime runtime = Runtime.getRuntime();
      Process process = runtime.exec( command );
      InputStreamReader reader = new InputStreamReader( process.getInputStream() );
      BufferedReader in = new BufferedReader( reader );
	
      // Read each line of output from the frameTime command.
      String line;
      while ( (line = in.readLine()) != null )
      {
	buffer += line + " <br>";
      }
      in.close();
      process.getInputStream().close();
      process.getOutputStream().close();
      process.getErrorStream().close();

      // clear what's there
      session.setAttribute( "frameTime", "" );
      request.setAttribute( "frameTime", "" );

      StringTokenizer st = new StringTokenizer(buffer , " " );
      while (st.hasMoreTokens()) {
        String frameTime = st.nextToken();
        try {
          double ftime = Double.parseDouble(frameTime);
          //It is, so set the frameTime  field
          session.setAttribute( "frameTime", frameTime );
          request.setAttribute( "frameTime", frameTime );
        }
        catch ( NumberFormatException exc1 )
        {
          // don't need to do anything
        }

      }
    }

    catch ( IOException exc )
    {
      String message = "IO exception generating/getting the frame time output.";
      logger.error( message );
      logger.error( exc );
      buffer += message + "<br>";
    }

    return buffer;
  }


}  // end of Pimms

