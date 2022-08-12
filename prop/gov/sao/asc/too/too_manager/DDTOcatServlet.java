/*
  Copyrights:
 
  Copyright (c) 2014 Smithsonian Astrophysical Observatory
 
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
// DDTOcatServlet

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import info.*;
import db.*;
import ascds.FileUtils;
import ascds.LogMessage;


/******************************************************************************/
/**
 * DDTOcatServlet handles the submit functions for the selected DDT
 * proposal.  Functions available are to save data to the database, send a
 * DDT status message , perform the NameResolver action in NED/Simbad to
 * find coordinates, or 'Cancel' and return to the full list of DDTs
 *
 */
public class DDTOcatServlet extends HttpServlet 
{
    private Properties triggerTooProperties;
    private String triggerTooDataPath;
    private String tooReceiverPath;
    private String cxcHelpDesk;
    private boolean showDebug=false;
    private LogMessage logMessage;
    private String observationDetailLink;

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

      // initialize log file
      String triggerTooLogFile = triggerTooProperties.getProperty("toomanager.log.file");
      logMessage = new LogMessage(triggerTooLogFile,"660");


      loadProperties();
  }
  private void loadProperties() {
   
      // data path for files
      triggerTooDataPath = triggerTooProperties.getProperty("triggertoo.data.path");
      tooReceiverPath = triggerTooProperties.getProperty("too.receiver.path");

      // helpdesk
      cxcHelpDesk = triggerTooProperties.getProperty("cxc.helpdesk");

      // link to detail observation information
      observationDetailLink = triggerTooProperties.getProperty("observation.detail.link");

      // get debug flag
      String myShowDebug = triggerTooProperties.getProperty("show.debug");
      showDebug = myShowDebug.equalsIgnoreCase("true")  ;

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
      		throws ServletException, IOException 
  {
    ObservationList obsList = null;
    String message = new String(" ");
    String proposal_number = new String("");
    RequestDispatcher dispatcher = null;
    String str;
    String proposalNumber;
    String  msgclass;

    // default to error strings
    msgclass = new String("error");

    HttpSession session = request.getSession(true);

    String userName = (String)session.getAttribute("userName");
    String userPwd = (String)session.getAttribute("userPwd");
    if (userName == null || userName.equals("") ||
        userPwd == null || userPwd.equals("") ) {

      message = new String(TriggerTooConstants.INVALID_USER);
      logMessage.println(message);
    
      //Forward request to the jsp to display the login page
      session.setAttribute("logMessage",message);
      dispatcher = getServletContext().getRequestDispatcher("/login.jsp");
      dispatcher.forward(request, response);
    }
    else {
      loadProperties();
      logMessage.println("Current User: " + userName);
      proposalNumber = Parameter.get(request,"prop");
      dispatcher = getServletContext().getRequestDispatcher("/displayOcat.jsp");
      if (proposalNumber != null && proposalNumber.length() > 0) {
        try {
          DBObservation dbconn = null;

          // create database connections
          String testLevel = triggerTooProperties.getProperty("test.level");
          if (testLevel != null && testLevel.equals("1")) {
            dbconn = new DBObservation(triggerTooDataPath,showDebug);
          }
  
          else {
            dbconn = new DBObservation(userName,userPwd,triggerTooDataPath,showDebug);
          }
       
          try {
            // get TOO entry 
            obsList = dbconn.getObsListbyProposalNumber(proposalNumber);
          }
          catch (Exception exc) {
            LogMessage.printException(exc);
            message += "Error occurred retrieving Observation Entries from the database.\n" ;
            message += exc.getMessage();
            msgclass = "error";
          }
      
        }
        catch (Exception exc) {
           message += "Unable to connect to database.\n";
           message += exc.getMessage();
           msgclass = "error";
        }
        logMessage.println(message);

        //Forward request to the jsp to display the resulting page
        if (message == null) {
           message = "";
        }
        session.setAttribute("proposalNumber",proposalNumber);
        session.setAttribute("message",message);
        session.setAttribute("msgclass",msgclass);
        session.setAttribute("obsList",obsList);
        session.setAttribute(TriggerTooConstants.OBSDETAIL,observationDetailLink);
        dispatcher.forward(request, response);
      }
    }

  }

}

