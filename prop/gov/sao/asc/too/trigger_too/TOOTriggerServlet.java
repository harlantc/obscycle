/*
  Copyrights:
 
  Copyright (c) 2000-2019 Smithsonian Astrophysical Observatory
 
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
// TOOListServlet

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import info.*;
import db.Database;
import db.DBObservation;
import db.DBTriggerToo;
import ascds.NameResolver;
import ascds.RunCommand;
import ascds.LogMessage;

/******************************************************************************/
/**
 *  TOOTriggerServlet class handles the triggering of a TOO. Provides a
 *  printer friendly format of the TOO values or submits an RPS formatted
 *  message to trigger the TOO. 
 */

public class TOOTriggerServlet extends HttpServlet 
{
    private static final long serialVersionUID = 1;
    private Properties triggerTooProperties;
    private String triggerTooDataPath;
    private String tooReceiverPath;
    private String observationDetailLink;
    private Integer maxRequests;
    private boolean showDebug=false;
    private NameResolver nameResolver;
    private String cxcHelpDesk;
    private LogMessage logMessage;


  /***************************************************************************/
  /**
   * Handle a GET request from a browser --- simply initialize
   * validation state.
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void doGet( HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
      service(request, response);
  }

  /***************************************************************************/
  /**
   * Handle a POST request from a browser --- simply initialize
   * validation state.
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void doPost( HttpServletRequest request, HttpServletResponse response )
      throws ServletException, IOException {
      service(request, response);
  }

  /*************************************************************************/
  /**
   * Handle the INIT operation.  This operation is invoked by the
   * servlet engine when it starts up.  Set the tookit properties if
   * they have not already been set.  This includes the proposal data
   * path.
   *
   * @param config The configuration object established by the servlet
   * engine.
   */

  public void init( ServletConfig config ) throws ServletException  {
      ServletContext context = config.getServletContext();
      super.init(config);

      triggerTooProperties = TriggerToo.getProperties(context );
      triggerTooDataPath = triggerTooProperties.getProperty("triggertoo.data.path");
      String tstr = triggerTooProperties.getProperty("triggertoo.max.requests");
      if (tstr != null) {
         maxRequests = new Integer(tstr);
      }
      if (maxRequests == null || maxRequests.intValue() <= 0) {
         maxRequests = new Integer(20);
      }
      tooReceiverPath = triggerTooProperties.getProperty("too.receiver.path");
      cxcHelpDesk = triggerTooProperties.getProperty("cxc.helpdesk");
      nameResolver = new NameResolver(triggerTooProperties,"\n");
      String triggerTooLogFile = triggerTooProperties.getProperty("triggertoo.log.file");
      logMessage = new LogMessage(triggerTooLogFile,"660");

      String myShowDebug = triggerTooProperties.getProperty("show.debug");
      showDebug = myShowDebug.equalsIgnoreCase("true");

      observationDetailLink = triggerTooProperties.getProperty("webchaser.detail.link");


  }

   

  /****************************************************************************/
  /**
   * Handle a submission from a browser.  Five operations are provided:
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   * Retrieves the list of proposals for this user
   */

  public void service( HttpServletRequest request, 
                       HttpServletResponse response )
    throws ServletException, IOException {

    RequestDispatcher dispatcher = null;
    request.setCharacterEncoding("UTF-8");
    HttpSession session = request.getSession(false);
    String sstr= "";
    if (session != null  && request.isRequestedSessionIdValid()) {
       sstr = (String)session.getAttribute("searchpage");
       if (sstr  == null || !sstr.equals("okbytrigger") ) {
         LogMessage.println("Invalidating session");
         session.invalidate();
         session=null;
       }
    }
    if (session == null || !request.isRequestedSessionIdValid()) {
       LogMessage.println("Session is invalid");
       dispatcher = getServletContext().getRequestDispatcher("/displayTOOSearch.jsp?xx=Invalid%20session.");
       dispatcher.forward(request, response);
       return;
    }

    String operation = Parameter.get(request,"Submit");

    Integer obsid = new Integer(0);
    ObservationList observationList = new ObservationList();
    String message = new String("");
    String coordMessage = new String("");
    String str;
    Observation observation = null;

    // get the obsid and the observation list from the session
    String resolverSelector = Parameter.get(request,TriggerTooConstants.RESOLVERLIST);

    observation=null;
    try {
        obsid = Parameter.getInteger(request,"obsid");
        if (obsid != null) {
          DBObservation dbconn = new DBObservation(triggerTooDataPath,showDebug);
          observationList = dbconn.getObservationListbyObsid(obsid);
          observation = observationList.getByObsid(obsid);
          if (observation == null) {
             message="Unable to retrieve observation.";
          }
        } else {
          message = "Unable to retrieve observation.";
        }
    }
    catch (Exception exc) {
        //exc.printStackTrace();
        message = "Unable to retrieve Observation " + obsid.toString() ;
        message += " from the database. Please contact the CXC HelpDesk." ;
        logMessage.println(message);
        logMessage.println(exc.toString());
    }

    if (operation != null && operation.equalsIgnoreCase(TriggerTooConstants.CANCEL)) {
       message = "Request canceled";
    }
    
    // default page
    dispatcher = getServletContext().getRequestDispatcher("/displayTrigger.jsp");
    // initialize any output variables
    initOutputParameters(session);

    if (observation != null && operation == null ) {
      //Forward request to the jsp to display the resulting page
    }
    else if (observation == null || operation.equalsIgnoreCase(TriggerTooConstants.CANCEL)) {
      session.setAttribute("message",message);
      dispatcher = getServletContext().getRequestDispatcher("/displayTOOSearch.jsp");
         
    }
    else {
      logMessage.println("Operation=" + operation + " for " + obsid.toString() + " using " + tooReceiverPath);

      coordMessage = processParameters(request,observation);
      if (!observation.isValidCoords()) {
        // invalid coords
        logMessage.println(coordMessage);
        session.setAttribute("RALabelBG",TriggerTooConstants.ERRORCLASS);
        session.setAttribute("DecLabelBG",TriggerTooConstants.ERRORCLASS);
      }
      if ( operation.equalsIgnoreCase(TriggerTooConstants.NAMERESOLVER) ) {
        coordMessage = nameResolver(request,resolverSelector,observation);
        if (coordMessage != null && coordMessage.length() > 0) {
          session.setAttribute("TargetLabelBG",TriggerTooConstants.ERRORCLASS);
        }
      }
      else if ( operation.equalsIgnoreCase(TriggerTooConstants.PRINTER) ) {
        try {
          String filename = triggerTooDataPath + "/tmp/";
          filename += obsid.toString();
          filename += ".tmp";
        
          observation.writeObservation(filename);
          session.setAttribute("displayFilename",filename);
          //String jspName = "/displayFile.jsp?deleteFile=yes&fileName=" + filename;
          String jspName = "/displayFile.jsp?deleteFile=yes" ;
          dispatcher = getServletContext().getRequestDispatcher(jspName);
        }
        catch (Exception exc) {
          exc.printStackTrace();
          message = "Unexpected error trying to write printer friendly version";
          logMessage.println(message);
          logMessage.println(exc.toString());

        }
      }
      else if ( operation.equalsIgnoreCase(TriggerTooConstants.SUBMIT) ) {
        boolean missingFields = false;
        boolean fieldError = false;
        logMessage.println("Verify submission for obsid " + obsid.toString() + " urgency=" + observation.getUrgency());

        // verify contact info
        str = observation.getContactInfo();
        if (str != null && str.length() > 255) {
           fieldError = true;
           message += TriggerTooConstants.CONTACTINFO + " exceeds the maximum of 255 characters.<br>";
           session.setAttribute("ContactInfoLabelBG",TriggerTooConstants.ERRORCLASS); 
        }
        if (!(observation.getUrgency().startsWith(TriggerTooConstants.SLOW)) &&
             (str == null || str.length() <= 0))  {
          session.setAttribute("ContactInfoLabelBG",TriggerTooConstants.ERRORCLASS); 
          missingFields = true;
          logMessage.println("Missing 24 Hr. Contact Information");
        }

        // verify trigger justification
        str = observation.getTriggerJustify();
        if (str == null || str.length() <= 0) {
           session.setAttribute("TriggerJustifyLabelBG",TriggerTooConstants.ERRORCLASS); 
           missingFields = true;
           logMessage.println("Missing Trigger Justification." );
        }
        else if (str.length() > 1000) {
           fieldError = true;
           message += TriggerTooConstants.TRIGGERJUSTIFY + " exceeds the maximumof 1000 characters.<br>";
           session.setAttribute("TriggerJustifyLabelBG",TriggerTooConstants.ERRORCLASS); 
        }
        // verify response window 
        str = observation.getResponseChange();
        if (str != null && str.length() > 400) {
           fieldError = true;
           message += TriggerTooConstants.RESPONSECHANGES + " exceeds the maximum of 400 characters.<br>";
           session.setAttribute("ResponseChangesLabelBG",TriggerTooConstants.ERRORCLASS); 
        }

        // verify Target Name 
        str = observation.getTargetName();
        if (str == null || str.length() <= 0) {
           session.setAttribute("TargetLabelBG",TriggerTooConstants.ERRORCLASS); 
           missingFields = true;
           logMessage.println("Missing Target Name");
        }
        // verify Coordinates
        str = observation.getRAString();
        if (str == null || str.length() <= 0) {
        //if (observation.getRA().doubleValue() == 0.0) 
           session.setAttribute("RALabelBG",TriggerTooConstants.ERRORCLASS); 
           missingFields = true;
           logMessage.println("Missing RA");
        }
        str = observation.getDecString();
        if (str == null || str.length() <= 0) {
        //if (observation.getDec().doubleValue() == 0.0) 
           session.setAttribute("DecLabelBG",TriggerTooConstants.ERRORCLASS); 
           missingFields = true;
           logMessage.println("Missing Dec");
        }
        if (missingFields) {
           message += "Required fields are missing.";
        }
        else if (!fieldError && observation.isValidCoords()) {
          String filename = new String("");
          message =  submitTriggerRequest(obsid,observation,session);
          if (message == null || message.length() <= 0) {
            String jspName = "/displaySuccessfulSubmit.jsp";
            dispatcher = getServletContext().getRequestDispatcher(jspName);
          }
        }
      }
      else {
        message = "Unknown operation encountered.";
        logMessage.println(message + " " + operation);
      }
         
     } 
 
      
     logMessage.println(message);
     session.setAttribute(TriggerTooConstants.RESOLVERLIST,resolverSelector);
     session.setAttribute("selectedObservation",observation);
     session.setAttribute("obsList",observationList);
     session.setAttribute("message",message);
     session.setAttribute("coordMessage",coordMessage);
     dispatcher.forward(request, response);
  }

  // ---------------------------------------------------
  // Initialize output parameter variables and labels
  // ---------------------------------------------------
  private void initOutputParameters (HttpSession session) 
  {
     session.setAttribute("message","");
     session.setAttribute("coordMessage","");
     session.setAttribute("fileName","");
     session.setAttribute("RALabelBG",TriggerTooConstants.LABELCLASS);
     session.setAttribute("DecLabelBG",TriggerTooConstants.LABELCLASS);
     session.setAttribute("TargetLabelBG",TriggerTooConstants.LABELCLASS);
     session.setAttribute("TriggerJustifyLabelBG",TriggerTooConstants.LABELCLASS);
     session.setAttribute("ContactInfoLabelBG",TriggerTooConstants.LABELCLASS);
     session.setAttribute("ResponseChangesLabelBG",TriggerTooConstants.LABELCLASS);
     session.setAttribute(TriggerTooConstants.OBSDETAIL,observationDetailLink);
  }

  // ---------------------------------------------------
  // Get the user input
  // ---------------------------------------------------
  private String processParameters( HttpServletRequest request,Observation observation) 
  {

     String str;
     String message = new String("");

     str = Parameter.get(request,TriggerTooConstants.CONTACTINFO);
     observation.setContactInfo(str);

     str = Parameter.get(request,TriggerTooConstants.TRIGGERJUSTIFY);
     observation.setTriggerJustify(str);

     str = Parameter.get(request,TriggerTooConstants.RESPONSECHANGES);
     observation.setResponseChange(str);

     str = Parameter.get(request,TriggerTooConstants.OBSCHANGES);
     observation.setObsChanges(str);

     // If the observation is editable, check target name/coordinate input
     if (observation.isEditable()) {
       str = Parameter.get(request,TriggerTooConstants.TARGETNAME);
       observation.setTargetName(str);
       logMessage.println("setting target name " + str + " for " + observation.getObsid());
       String raStr = Parameter.get(request,TriggerTooConstants.RA);
       String decStr = Parameter.get(request,TriggerTooConstants.DEC);
       try {
         observation.setCoords(raStr,decStr);
       }
       catch (Exception exc) 
       {
         logMessage.println("Coordinate error: " +  exc.toString());
       }
       if (!observation.isValidCoords()) {
         message = "Invalid coordinates. Please try again.";
       }
     }

     return message;
   }

  // ---------------------------------------------------
  // execute the NameResolver function for Ned,Simbad and
  // fill in the coordinates 
  // ---------------------------------------------------
  private String nameResolver( HttpServletRequest request,String resolverSelector,Observation observation)

  {
    // Validate the input parameters.

    String targetname = Parameter.get(request,TriggerTooConstants.TARGETNAME);
    String theCoords = new String("");
    if(resolverSelector == null || resolverSelector.equals("")) {
       resolverSelector="SIMBAD/NED";
    }
    String[] resolverList = resolverSelector.split("/");

    logMessage.println ("attempting to resolve " + targetname + "using " + resolverSelector);
  
    try {
       theCoords = nameResolver.resolve(targetname,resolverList);
       if (theCoords == null) {
         String theRA = nameResolver.getRA();
         String theDec = nameResolver.getDec();
         observation.setCoords(theRA,theDec);
         theCoords = new String("");
       }
       else {
         logMessage.println("nameResolver: " + theCoords);
         theCoords = theCoords.replaceAll("\\n"," ");
         theCoords = theCoords.replaceAll("\\t"," ");
       }

    }
    catch (Exception exc) {
       exc.printStackTrace();
       theCoords = exc.getMessage();
      
    }
    return theCoords;
  }

  // ------------------------------------------------------
  // submit a trigger request by writing the final version
  // ------------------------------------------------------
  private String submitTriggerRequest(Integer obsid,Observation observation,HttpSession session )
  {
    String filename = new String("");
    String message = new String("");
    boolean newFile = false;
    int ii = 1;
    Integer iversion = new Integer(ii);
    MailUtility mu = new MailUtility(triggerTooDataPath,triggerTooProperties); 

    // first write the trigger request to a file
    try {
      while (!newFile && ii <= maxRequests.intValue()) {
        iversion = new Integer(ii);
        filename = triggerTooDataPath + "/";
        filename += obsid.toString();
        filename += "_v" + iversion.toString();
        filename += ".submit";
        
        File outputFile = new File(filename);
        if(outputFile.exists()) {
          ii += 1;
        }
        else {
          newFile = true;
        }
      }
    } catch (Exception exc) {
        //exc.printStackTrace();
        message = "Unable to create file for trigger submission request.";
        message += " Please notify " + cxcHelpDesk;
        logMessage.println(message);
        logMessage.println("TOOTrigger: " + exc.toString());
        try {
          mu.mailErrorMessage(exc.toString());
        }
        catch (Exception ex) {
          ex.printStackTrace();
          logMessage.println("Trigger mail error: " + exc.toString());
        }
    }
    if (ii  > maxRequests) {
      message = "Too many submissions for this observation. ";
      message += " Please notify " + cxcHelpDesk;
      logMessage.println(message);
    }

    // if we successfully created the file, then send the trigger status message
    if (message.length() <= 0) { 
      try {
        observation.writeSubmitObservation(filename);
        logMessage.println(" Submit Trigger request for " + obsid.toString() + " Version " + iversion.toString());
        String pfilename = tooReceiverPath + "/";
        pfilename += obsid.toString();
        pfilename += "_v" + iversion.toString();
        pfilename += ".prop";
        
        if (!observation.writeRPSNotify(pfilename)) {
             message = "writeRPSNotify  failed to create file: " + pfilename;
             throw new Exception(message);
        } 
        
        // now send the trigger status message to the RPS receiver
        //String fromEmail = triggerTooProperties.getProperty("from.email.address");
        //String toEmail = triggerTooProperties.getProperty("status.email.address");
        //try  
          //mu.mailMessage(fromEmail,toEmail,TriggerTooConstants.SUBJECT_TRIGGER,
		//sw.toString());
      } catch(Exception mailEx) {
        LogMessage.println("TriggerTOO::Caught exception : " + mailEx.getMessage());
        try {
          mu.mailErrorMessage( message);
          message = "";
        } catch (Exception mexc) {
          message = "Unable to send error message";
          message += mexc.toString();
          logMessage.println(message);
          mexc.printStackTrace();
        }
      }
      session.setAttribute("fileName",filename);

      // after sending the message insert the entry in the database
      try {
          DBTriggerToo dbconn = new DBTriggerToo(triggerTooDataPath,true);
          dbconn.insertNewTrigger(observation.getObsid(), iversion,
		  observation.getUrgency(),
                  observation.getOrigTargetName(), observation.getTargetName(),
		  observation.getRA(),observation.getDec(),
	          observation.isEditable(),
		  observation.getFullTriggerComments());
      }
      catch (Exception exc) {
        exc.printStackTrace();
        message = "Unable to update TOO Trigger information in the database.\n";
        message += exc.toString();
        logMessage.println(message);
        try {
          mu.mailErrorMessage( message);
          message = "";
        } catch (Exception mexc) {
          message = "Unable to send error message";
          message += mexc.toString();
          logMessage.println(message);
          mexc.printStackTrace();
        }
      }
    }

    return message;
  }

     
}

