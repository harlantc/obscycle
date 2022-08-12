/*
  Copyrights:
 
  Copyright (c) 2000,2021 Smithsonian Astrophysical Observatory
 
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

import ascds.RunCommand;
import ascds.NameResolver;

import info.*;
import captcha.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;


/******************************************************************************/
/**
 * Provide GUI support for the Resource Cost Calculator program.
 */

public class RCCalc extends HttpServlet implements ToolkitConstants
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
    logger.trace( ">>> RCCalc Dumping parameters ..." );
    String parameterName, parameterValue;
    for ( Enumeration parameters = request.getParameterNames();
          parameters.hasMoreElements(); ) {
      parameterName = (String) parameters.nextElement();
      parameterValue = request.getParameter( parameterName );
      if (parameterName.indexOf("recap") < 0 && parameterValue.length() > maxParamLength.intValue()) {

         badparams=true;
         logger.info( "RCCalc Bad Parameter, clear all : " + parameterName + " length = " +  parameterValue.length() );
      }
      logger.trace("RCCalc: " + parameterName + " = " +  parameterValue );
    }

    // Get the session associated with the request, or create.
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
    //if (badparams) { operation="CLEAR"; }

    
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
        session.setAttribute( "rccalcWarnings", new Boolean(false));
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
        session.setAttribute( "rccalcWarnings", new Boolean(false));

        // Validate the current session content
        boolean status = this.validateSessionInParams( request );

        if (!status) {
            // Store current session content as MPCat record
            storeSession( request );

            // Do the calculation.
            calculate( request, session );

            // Clear MPCat content
            // TODO - TEMPORARY.. control of stack s/b independent of calculate.
            MPCat mpcat = (MPCat)session.getAttribute("warehouse");
            if ( mpcat != null ){
                mpcat.clear();
            }
        }

    }
    else if ( operation.equals( "VIEW OUTPUT" ) )
    {
        //Since we want to function without requiring cookies, we need
        //to perform the calculation again without relying on session
        //information - so the work here is more than just the noop.
      session.setAttribute( "rccalcWarnings", new Boolean( false ) );
      
      // Validate the current session content
      boolean status = this.validateSessionInParams( request );

      if (!status) {
            // Store current session content as MPCat record
            storeSession( request );

            // Perform the calculation so that all the parameters are set
            calculate( request, session );

            // Clear MPCat content
            // TODO - TEMPORARY.. control of stack s/b independent of calculate.
            MPCat mpcat = (MPCat)session.getAttribute("warehouse");
            if ( mpcat != null ){
                mpcat.clear();
            }
      }

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
     * Assign Toolkit properties
     *   - separates attribute setting from the source of the properties
     *   - mainly to support testing of methods which use the attribute.
     *
     * @param props Toolkit Properties 
     *
     **/
    protected void setToolkitProperties( Properties props ){
        this.toolkitProperties = props;
    }

    /** 
     * Store session content to MPCat record storage 
     *
     * NOTE: uses info/Parameter.get() which polls the request
     *       and then the underlying session for the value.
     *
     * @param request The request object as passed in by the browser.
     *
     **/
    protected void storeSession( HttpServletRequest request ){
        String tmpstr;
        Integer nrows;
        MPCat   mpcat;

        /* create new record instance */
        MPCatRecord record = new MPCatRecord();

	/* Set current cycle */
	try{
          String cycle = toolkitProperties.getProperty("proposal-cycle");
	  record.setCycle( cycle );
	}catch( NullPointerException | IllegalArgumentException ex ){
	    // continue with default value
	    //logger.error("RCC Errors: " + ex.getMessage());
	}

        /* xfer session content */
        record.setPosition( Parameter.get( request, "inputPosition1" ),
                            Parameter.get( request, "inputPosition2" ),
                            Parameter.get( request, "inputCoordinateSelector" ) );
        record.setExposureTime( Parameter.get( request, "propExposureTime" ) );
        record.setInstrument( Parameter.get( request, "instrument" ) );
        record.setChipCount( Parameter.get( request, "requiredChipCount" ), "0" );
        record.setContinuous( Parameter.get( request, "uninterrupted" ) );
        record.setPointingConstraint( Parameter.get( request, "pointingConstraint" ) );

        tmpstr = Parameter.get( request, "splitConstraint" );
        if ( tmpstr.equalsIgnoreCase("YES") ){
            record.setSplitInterval( Parameter.get( request, "splitInterval" ) );
        }

        tmpstr = Parameter.get( request, "coordinatedObs" );
        if ( tmpstr.equalsIgnoreCase("YES") ){
            record.setMultiTelescope( Parameter.get( request, "obsInterval" ) ) ;
        }
        tmpstr = Parameter.get( request, "phaseConstraint" );
        if ( tmpstr.equalsIgnoreCase("YES") ){
            record.setPhaseConstraint(Parameter.get( request, "phaseEpoch" ),
                                      Parameter.get( request, "phasePeriod" ),
                                      Parameter.get( request, "phaseStart" ),
                                      Parameter.get( request, "phaseStartMargin" ),
                                      Parameter.get( request, "phaseStop" ),
                                      Parameter.get( request, "phaseStopMargin" ),
                                      Parameter.get( request, "phaseUnique" ));
        }
        tmpstr = Parameter.get( request, "groupConstraint");
        if ( tmpstr.equalsIgnoreCase("YES") ){
            String groupID = Parameter.get( request, "groupID" );
            if ( groupID == null ){
                groupID = "group_0000";
            }
            record.setGroupConstraint(groupID,
                                      "000:00:00:00.000",
                                      Parameter.get( request, "groupPreMaxLead" ));
        }
        tmpstr = Parameter.get( request, "monitorConstraint");
        if ( tmpstr.equalsIgnoreCase("YES") ){
            try {
                nrows = (Integer) request.getSession(true).getAttribute( "monitorNumRows" );
            }catch( Exception ex ){ nrows = -1; }

            for ( int ii = 0; ii < nrows; ii++ ){

                String strndx = Integer.toString(ii);
                tmpstr = Parameter.get( request, "monitorSplitInterval"+strndx );
                if ( tmpstr.isEmpty() ){
                    // accommodates older example cases which do not include the monitorSplitInterval key.
                    record.addMonitorConstraint(Parameter.get( request, "monitorPreMinLead"+strndx ),
                                                Parameter.get( request, "monitorPreMaxLead"+strndx ),
                                                Parameter.get( request, "monitorExpTime"+strndx ));
                }else{
                    record.addMonitorConstraint(Parameter.get( request, "monitorPreMinLead"+strndx ),
                                                Parameter.get( request, "monitorPreMaxLead"+strndx ),
                                                Parameter.get( request, "monitorExpTime"+strndx ),
                                                Parameter.get( request, "monitorSplitInterval"+strndx ));
                }
            }
        }
        tmpstr = Parameter.get( request, "windowConstraint" );
        if ( tmpstr.equalsIgnoreCase("YES") ){
            try {
                nrows = (Integer) request.getSession(true).getAttribute( "windowNumRows" );
            }catch( Exception ex ){ nrows = -1; throw ex; }

            for ( int ii = 0; ii < nrows; ii++ ){

                String strndx = Integer.toString(ii);
                String useWindowConstraintFlag = "YES";
                record.addWindowConstraint(useWindowConstraintFlag,
                                           Parameter.get( request, "windowStartTime"+strndx ),
                                           Parameter.get( request, "windowStopTime"+strndx ));
            }
        }
        tmpstr = Parameter.get( request, "rollConstraint" );
        if ( tmpstr.equalsIgnoreCase("YES") ){
            try {
                nrows = (Integer) request.getSession(true).getAttribute( "rollNumRows" );
            }catch( Exception ex ){ nrows = -1; }

            for ( int ii = 0; ii < nrows; ii++ ){

                String strndx = Integer.toString(ii);
                String useRollConstraintFlag = "YES";
                record.addRollConstraint(useRollConstraintFlag,
                                         Parameter.get( request, "rollRotation"+strndx ),
                                         Parameter.get( request, "rollAngle"+strndx ),
                                         Parameter.get( request, "rollTolerance"+strndx ));
            }
        }

        /* pull mpcat instance from session, or create it */
        mpcat = (MPCat)request.getSession().getAttribute("warehouse");
        if ( mpcat == null ){
            mpcat = new MPCat();
            request.getSession().setAttribute("warehouse", mpcat);
        }

        /* add record to storage */
        mpcat.addRecord( record );
    }

    /** 
     * Validate session input parameter content.
     *
     * @param request The request object as passed in by the browser.
     *
     * @return True if validaton errors occured; otherwise False.
     **/
    protected boolean validateSessionInParams( HttpServletRequest request ){

      RCCalcValidator validator =
          new RCCalcValidator(request, toolkitProperties);

      ArrayList<String> issues    = new ArrayList<>();
      ArrayList<String> errorList = new ArrayList<>();
      String errmsg; 
      boolean result;

      Integer nrows;
      String tmpstr;

      // NOTE: Not sure what the philosophy should be here..
      //       check all fields, or check active fields.
      //       Some defaults are not valid values, so would need to
      //       have special validation for 'off' constraints/fields.

      // TODO: Currently performs basic validation of the field values.
      //       Need to add correlations between fields.
      //       RCCValidator class?

      // Validate the session parameters.
      // * Position
      validator.validatePosition();

      // * Exposure Time
      validator.validateExposureTime();

      // * Split Interval
      tmpstr = Parameter.get( request, "splitConstraint" );
      if ( tmpstr.equalsIgnoreCase("YES") ){
          validator.validateSplitConstraint();
      }

      // * Coordinated Observation
      tmpstr = Parameter.get( request, "coordinatedObs");
      if ( (tmpstr != null) && tmpstr.equalsIgnoreCase("YES") ){
          validator.validateCoordinationWindow();
      }

      // * Science Instrument & Chip Count
      validator.validateChipSelection();

      // Phase Constraint
      tmpstr = Parameter.get( request, "phaseConstraint");
      if ( (tmpstr != null) && tmpstr.equalsIgnoreCase("YES") ){
          validator.validatePhaseConstraint();
      }

      // Group Constraint
      tmpstr = Parameter.get( request, "groupConstraint");
      if ( (tmpstr != null) && tmpstr.equalsIgnoreCase("YES") ){
          validator.validateGroupConstraint();
      }

      // Monitor Constraints
      tmpstr = Parameter.get( request, "monitorConstraint");
      if ( (tmpstr != null) && tmpstr.equalsIgnoreCase("YES") ){
          validator.validateMonitorConstraint();
      }

      // Window Constraints
      tmpstr = Parameter.get( request, "windowConstraint");
      if ( (tmpstr != null) && tmpstr.equalsIgnoreCase("YES") ){
          validator.validateWindowConstraint();
      }

      // Roll Constraints
      tmpstr = Parameter.get( request, "rollConstraint");
      if ( (tmpstr != null) && tmpstr.equalsIgnoreCase("YES") ){
          validator.validateRollConstraint();
      }

      // Generate the validation state.
      result = validator.hasIssues();
      if ( result ){
          // updates error state elements in session.
          validator.updateSessionState();
      }

      return result;
    }

    /**
     * Generate filename to write MPCat file.
     *
     * @return filename with path in toolkit temp space.
     */
    protected String getTempFilename(){
      String filename;
      String tmpdir;

      try{
          tmpdir = toolkitProperties.getProperty("toolkit.temp.path");
      }catch(NullPointerException ex){
          tmpdir = "/tmp";
      }
      filename = tmpdir + "/" + "mpcat-" + UUID.randomUUID() + ".txt";

      return filename;
    }

    /**
     * Run RCC tool on current MPCat content
     *
     * @param mpcat    Stack of target data
     * @param rccPath  Path to rcc utility tool
     * @param mode     Mode in which to execute the tool ( 0=infile, 1=instring )
     *
     * @return Response from RCC execution.
     */
    protected String runRCCUtility( MPCat mpcat, String rccPath, String mode ){
      String rccTool = "resource_cost_calculator.pl ";
      String rccArgs;
      String tmpfile = "";
      String response;


      if (! rccPath.endsWith("/") ){
          rccPath = rccPath + "/";
      }

      // Setup to run RCC utility.. by -instring or -infile
      if ( mode.equals("0") ){
          tmpfile = getTempFilename();
          try{
              mpcat.toFile( tmpfile );
          }catch( Exception ex ){
              throw new RuntimeException("problem writing MPCat to file: " + ex.getMessage() );
          }
          rccArgs = "-infile " + tmpfile;
      }
      else if ( mode.equals("1") ){
          rccArgs = "-instring \'" + mpcat.toString().replace("\n"," ") + "\'";
      }
      else if ( mode.equals("2") ){
          // ** to use a canned file **/
          String rccInfilePath = toolkitProperties.getProperty("rcc.infile.path");
          String rccTestFile = toolkitProperties.getProperty("rcc.test.file");
          rccArgs = "-infile " + rccInfilePath + "/" + rccTestFile;
      }
      else{
          throw new IllegalArgumentException("Invalid mode.");
      }

      // Assemble command.
      String cmd = rccPath + rccTool + rccArgs;

      // Run RCC utility
      ArrayList<String> envVarList = setEnvironment();
      RunCommand runtime = new RunCommand(cmd, envVarList, null);

      // Cleanup tmpfile if used
      if (! tmpfile.isEmpty() ){
          File fh = new File( tmpfile );
          fh.delete();
      }

      // Check for errors
      String resultErr = runtime.getErrMsg();
      if (! resultErr.isEmpty() ){
          throw new RuntimeException("Error running RCC script: " + resultErr.trim() );
      }

      // Get results
      response = runtime.getOutMsg();

      return response;
    }

    /**
     * Parse results string from RCC utility
     * 
     * @param response Output of resource_cost_calculator.pl command.
     *
     * @return HashMap of Normalized Resource Cost scores ("NRC","NRC1","NRC2")
     *         each value is ArrayList of scores, one per target.
     */
    protected HashMap<String,ArrayList<String>> parseRCCResponse( String response )
    {
      /* Normalized Resource Cost: compatible with multiple targets */
      ArrayList<String> nrc  = new ArrayList<>();
      ArrayList<String> nrc1 = new ArrayList<>();
      ArrayList<String> nrc2 = new ArrayList<>();

      HashMap<String,ArrayList<String>> result = new HashMap<String,ArrayList<String>>();

      String[] norms = {"Normalized_N", "Normalized_N1", "Normalized_N2"};
      String[] rcLines;
      String[] fields;
      ArrayList<Integer> rcInds = new ArrayList<>(); // indexes to desired fields

      // separate lines
      rcLines = response.split(System.lineSeparator());

      Integer firstLine = 0;
      for (String rcLine : rcLines) {
          // 1st line isn't needed
          if (firstLine.equals(0)){
              firstLine++;
              continue;
          }

          // split rcline on field separator
          //   Use lambda function to strip white space
          fields = rcLine.split("\\|");
          fields = Arrays.stream(fields).map(String::trim).toArray(String[]::new);

          // Get index of norm cols from header line
          if (firstLine == 1) {
              for (String norm : norms) {
                  rcInds.add(Arrays.asList(fields).indexOf(norm));
              }
              if (rcInds.contains(-1)) {
                 throw new IllegalArgumentException("Error in RCC. Header column missing Normalized_N*.");
              }
              firstLine++;
              continue;
          }

          // For remaining lines.. add field values to output arrays
          nrc.add(fields[rcInds.get(0)]);
          nrc1.add(fields[rcInds.get(1)]);
          nrc2.add(fields[rcInds.get(2)]);
      }

      /* Store Normalized Resource Cost(s) */
      result.put("NRC",  nrc );
      result.put("NRC1", nrc1 );
      result.put("NRC2", nrc2 );

      return result;
    }

  /**
   * Private variables
   */

  private Properties toolkitProperties;
  private NameResolver nameResolver;
  private Integer maxParamLength=30;
  private static Logger logger = Logger.getLogger(RCCalc.class);

  // ***************************************************************************
  // Private methods
  // ***************************************************************************
  // ***************************************************************************
  /**
   * Run resource_cost_calculator.pl, parse the results, and pass to session.
   *
   * @param request  Http servlet request
   * @param session  Http session
   */
    private void calculate( HttpServletRequest request, HttpSession session ){
      HashMap<String,ArrayList<String>> rccResults;

      try {
          String rccInstring = toolkitProperties.getProperty("rcc.instring");
          String rccPath = toolkitProperties.getProperty("rcc.path");
          MPCat  mpcat = (MPCat)request.getSession().getAttribute("warehouse");
          String response;
          
          logger.info("Running RCC script: ");
          response = runRCCUtility( mpcat, rccPath, rccInstring );

          // Parse the string for RC, RC1, RC2
          rccResults = parseRCCResponse( response );

          // Set session attributes with results.
          // TODO If multiple targets are implemented return entire ArrayList
          session.setAttribute("resultsNormalizedCost",  rccResults.get("NRC").get(0));
          session.setAttribute("resultsNormalizedCost1", rccResults.get("NRC1").get(0));
          session.setAttribute("resultsNormalizedCost2", rccResults.get("NRC2").get(0));

      } catch (Exception ex) {
          logger.error("RCC Errors: " + ex.getMessage());

          session.setAttribute("resultsNormalizedCost", "Error in processing RC" );
          session.setAttribute("resultsNormalizedCost1", "Error in processing RC");
          session.setAttribute("resultsNormalizedCost2", "Error in processing RC");
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
              session.setAttribute( "rccalcWarnings", new Boolean( true ) );
              session.setAttribute( "rccalcWarningsScript",
                                     Toolkit.buildWarningsScript(warning));
          } 
      } 
  }

  /****************************************************************************/
  // MCD NOTE:
  //   Removed methods not used by this tool.
  //    - getPosition()
  //    - getInputFormat()
  //   Both are related to interpreting the Position fields.
  //
  //   If something IS needed in this tool, consider using 
  //      ascds.Coordinate class which seems to cover a lot of the same.
  //
  /****************************************************************************/

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
      Parameter.initialize( request, clear, "inputPosition1", "" );
      Parameter.initialize( request, clear, "inputPosition2", "" );
      Parameter.initialize( request, clear, "equinox", "J2000" );
      Parameter.initialize( request, clear, "inputCoordinateSelector", "Equatorial (J2000)" );
      Parameter.initialize( request, clear, "targetName", "" );
      Parameter.initialize( request, clear, "resolverSelector", "SIMBAD/NED" );

      Parameter.initialize( request, clear, "propExposureTime", "" );
      Parameter.initialize( request, clear, "instrument", "ACIS-S" );
      Parameter.initialize( request, clear, "requiredChipCount", "4" );

      Parameter.initialize( request, clear, "uninterrupted", "No");

      Parameter.initialize( request, clear, "splitConstraint", "No");
      Parameter.initialize( request, clear, "splitInterval", "");

      Parameter.initialize( request, clear, "coordinatedObs", "No");
      Parameter.initialize( request, clear, "obsInterval", "");

      Parameter.initialize( request, clear, "phaseConstraint", "No");
      Parameter.initialize( request, clear, "phaseEpoch", "");
      Parameter.initialize( request, clear, "phasePeriod", "");
      Parameter.initialize( request, clear, "phaseStart", "0.0");
      Parameter.initialize( request, clear, "phaseStartMargin", "0.0");
      Parameter.initialize( request, clear, "phaseStop", "0.0");
      Parameter.initialize( request, clear, "phaseStopMargin", "0.0");
      Parameter.initialize( request, clear, "phaseUnique", "No");

      Parameter.initialize( request, clear, "groupConstraint", "No");
      Parameter.initialize( request, clear, "groupPreMaxLead", "");

      Parameter.initialize( request, clear, "monitorConstraint", "No");
      Parameter.initialize( request, clear, "monitorOperation", ""); 

      Integer monitorNumRows = 0;
      if (clear) {
          monitorNumRows = (Integer)request.getSession( true ).getAttribute( "monitorNumRows" );
          int ii = 0;
          for (ii = 0; ii < monitorNumRows; ii++) {
              request.getSession( true ).removeAttribute( "monitorExpTime"+Integer.toString(ii));
              request.getSession( true ).removeAttribute( "monitorPreMinLead"+Integer.toString(ii));
              request.getSession( true ).removeAttribute( "monitorPreMaxLead"+Integer.toString(ii));
              request.getSession( true ).removeAttribute( "monitorSplitInterval"+Integer.toString(ii));
              request.getSession( true ).removeAttribute( "monitorRemoveCheck"+Integer.toString(ii));
          }
      }

      Parameter.initialize( request, clear, "monitorNumRows", 1); 

      if (clear == false ) {
          monitorNumRows = (Integer)request.getSession( true ).getAttribute( "monitorNumRows" );
          int ii = 0;
          for (ii = 0; ii < monitorNumRows; ii++) {
              Parameter.initialize( request, clear, "monitorExpTime"+Integer.toString(ii), ""); 
              Parameter.initialize( request, clear, "monitorPreMinLead"+Integer.toString(ii), ""); 
              Parameter.initialize( request, clear, "monitorPreMaxLead"+Integer.toString(ii), ""); 
              Parameter.initialize( request, clear, "monitorSplitInterval"+Integer.toString(ii), ""); 
              Parameter.initialize( request, clear, "monitorRemoveCheck"+Integer.toString(ii), null); 
          }
      }


      Parameter.initialize( request, clear, "windowConstraint", "No");
      Parameter.initialize( request, clear, "windowOperation", "");


      Integer windowNumRows = 0;
      if (clear) {
          windowNumRows = (Integer)request.getSession( true ).getAttribute( "windowNumRows" );
          int ii = 0;
          for (ii = 0; ii < windowNumRows; ii++) {
              request.getSession( true ).removeAttribute( "windowStartTime"+Integer.toString(ii));
              request.getSession( true ).removeAttribute( "windowStopTime"+Integer.toString(ii));
              request.getSession( true ).removeAttribute( "windowRemoveCheck"+Integer.toString(ii));
          }
      }

      Parameter.initialize( request, clear, "windowNumRows", 1); 

      if (clear == false ) {
          windowNumRows = (Integer)request.getSession( true ).getAttribute( "windowNumRows" );
          int ii = 0;
          for (ii = 0; ii < windowNumRows; ii++) {
              Parameter.initialize( request, clear, "windowStartTime"+Integer.toString(ii), ""); 
              Parameter.initialize( request, clear, "windowStopTime"+Integer.toString(ii), ""); 
              Parameter.initialize( request, clear, "windowRemoveCheck"+Integer.toString(ii), null); 
          }
      }

      Parameter.initialize( request, clear, "rollConstraint", "No");
      Parameter.initialize( request, clear, "rollOperation", "");

      Integer rollNumRows = 0;
      if (clear) {
          rollNumRows = (Integer)request.getSession( true ).getAttribute( "rollNumRows" );
          int ii = 0;
          for (ii = 0; ii < rollNumRows; ii++) {
              request.getSession( true ).removeAttribute( "rollRotation"+Integer.toString(ii));
              request.getSession( true ).removeAttribute( "rollAngle"+Integer.toString(ii));
              request.getSession( true ).removeAttribute( "rollTolerance"+Integer.toString(ii));
              request.getSession( true ).removeAttribute( "rollRemoveCheck"+Integer.toString(ii));
          }
      }

      Parameter.initialize( request, clear, "rollNumRows", 1); 

      if (clear == false ) {
          rollNumRows = (Integer)request.getSession( true ).getAttribute( "rollNumRows" );
          int ii = 0;
          for (ii = 0; ii < rollNumRows; ii++) {

	      // for annoying checkboxes that return null or 'yes'
	      // - if the checkbox is unchecked (i.e. null), reset session Attribute to default 
	      //   since the initialize() function is not set up to handle null as a valid input
	      if (request.getParameter("rollRotation"+Integer.toString(ii)) == null)
		  Parameter.initialize( request, true, "rollRotation"+Integer.toString(ii), null);
	      else
		  Parameter.initialize( request, clear, "rollRotation"+Integer.toString(ii), null);

              Parameter.initialize( request, clear, "rollAngle"+Integer.toString(ii), "");
              Parameter.initialize( request, clear, "rollTolerance"+Integer.toString(ii), "");
              Parameter.initialize( request, clear, "rollRemoveCheck"+Integer.toString(ii), null); 
          }
      }
      Parameter.initialize( request, clear, "pointingConstraint", "No");

      Parameter.initialize( request, clear, "rccalcWarnings", new Boolean( false ) );
      Parameter.initialize( request, clear, "rccalcWarningsScript", "" );
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
    Parameter.initialize( request, clear, "resultsNormalizedCost", "" );
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
    session.setAttribute( "equinoxLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "position1LabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "position2LabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "targetNameLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "exposureTimeLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "requiredChipCountLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "uninterruptLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "intervalLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "coordinatedObsLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "phaseConstraintLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "phaseEpochLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "phasePeriodLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "phaseStartLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "phaseStartMarginLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "phaseStopLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "phaseStopMarginLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "phaseUniqueLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "groupConstraintLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "groupIDLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "groupPreMaxLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "monitorConstraintLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "monitorIntervalLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "monitorExposureTimeLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "monitorPreMinLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "monitorPreMaxLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "windowConstraintLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "windowStartLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "windowStopLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "rollConstraintLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "rollRotationLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "rollAngleLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "rollToleranceLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    session.setAttribute( "pointingConstraintLabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );
    //session.setAttribute( "LabelBGColor", ToolkitValidator.BG_COLOR_NORMAL );

  }

    private  ArrayList<String> setEnvironment()
    {
        // The RCC invokes use MP::DB which requires SYBASE env var be set even
        // in -input/-instring mode even though the DB shouldn't be used.
        String syblib;
        ArrayList<String> envVarList = new ArrayList<String>();
        syblib = System.getenv("SYBASE");
        String envStr = "SYBASE=" + syblib;
        envVarList.add(envStr);
        envStr = "LD_LIBRARY_PATH=" + System.getenv("LD_LIBRARY_PATH");
        envVarList.add(envStr);
        envStr = "LANG=en_US.UTF-8";
        envVarList.add(envStr);
        // This fakes out ObsCat.pm in resource_cost_calculator.pl
        envStr = "ASCDS_VERSION=RCcalc";
        envVarList.add(envStr);
        return envVarList;
    }
}
