/*
  Copyrights:
 
  Copyright (c) 2019 Smithsonian Astrophysical Observatory
 
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
// MemberListServlet

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ArrayList;
import java.text.NumberFormat;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import info.*;
import db.*;
import ascds.FileUtils;
import ascds.LogMessage;
import ascds.RunCommand;

/******************************************************************************/
/**
 */

public class TOOConflictServlet extends HttpServlet 
{
    private static final long serialVersionUID = 1;
    private Properties triggerTooProperties;
    private String triggerTooDataPath;
    private LogMessage logMessage;
    private boolean showDebug=true;




  /****************************************************************************/
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

  /****************************************************************************/
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

  /****************************************************************************/
  /**
   * Handle the INIT operation.  This operation is invoked by the
   * servlet engine when it starts up.  Set the report properties if
   * they have not already been set.  This includes the reports data
   * path.
   *
   * @param config The configuration object established by the servlet
   * engine.
   */

  public void init( ServletConfig config ) throws ServletException  {
      ServletContext context = config.getServletContext();
      super.init(config);

      triggerTooProperties = TriggerToo.getProperties(context );
   
      // data path for files
      triggerTooDataPath = triggerTooProperties.getProperty("triggertoo.data.path");
      // initialize log file
      String triggerTooLogFile = triggerTooProperties.getProperty("toomanager.log.file");
      logMessage = new LogMessage(triggerTooLogFile,"660");

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
   */

  public void service( HttpServletRequest request, HttpServletResponse response )
      throws ServletException, IOException {

      
    // Get the session object.
    HttpSession session = request.getSession(true );
    RequestDispatcher dispatcher = null;
    String message = null;
    Boolean isManager = false;
    TriggerTooEntry tooEntry = null;
    Integer triggerID = new Integer(0);
    DBTriggerToo dbconn = null;
    DBObservation dboconn = null;


    String userName = (String)session.getAttribute("userName");
    String userPwd = (String)session.getAttribute("userPwd");
    if (userName == null || userName.equals("") ||
        userPwd == null || userPwd.equals("") ) {

      message = new String(TriggerTooConstants.INVALID_USER);
      logMessage.println(message);
    
    }
    else {
      logMessage.println("Current User: " + userName);
      // get the proposal that was selected 
       

      try {

        // create database connections
        String testLevel = triggerTooProperties.getProperty("test.level");
        if (testLevel != null && testLevel.equals("1")) {
          dbconn = new DBTriggerToo(triggerTooDataPath,showDebug);
          dboconn = new DBObservation(triggerTooDataPath,showDebug);
        } else {
          dbconn = new DBTriggerToo(userName,userPwd,triggerTooDataPath,showDebug);
          dboconn = new DBObservation(userName,userPwd,triggerTooDataPath,showDebug);
        }
        try {
          triggerID = Parameter.getInteger(request,"triggerID");
          if  (triggerID==null) triggerID=0;
        } catch (Exception e) {
          logMessage.println("ERROR: invalid triggerID " );
          triggerID=0;
        }

        try {
          isManager = new Boolean(dbconn.isManagerRole());
          if (isManager) {
            // get TOO entry 
            tooEntry = dbconn.getTriggerTooEntry(triggerID);
            if (tooEntry != null) {
              String server = dbconn.getDBServer();
              String dbuser = dbconn.getDBUser();
              String ascdsenv = triggerTooProperties.getProperty("ascds.release");
              // permissions for proposal conflict are on the DB_PROP_SQLSRV
              String propserver = dbconn.getProposalServer();

              ArrayList<String> envVarList = new ArrayList<String>();
              String envStr = "ASCDS_BIN=" + ascdsenv + "/bin";
              envVarList.add(envStr);
              envStr = "LD_LIBRARY_PATH=" + ascdsenv + "/lib:" +ascdsenv + "/otslib:" + ascdsenv + "/ots/lib" ;
              envVarList.add(envStr);
              envStr= "DB_LOCAL_SQLSRV=" + propserver;
              envVarList.add(envStr);
              envStr= "DB_REMOTE_SQLSRV=" + propserver;
              envVarList.add(envStr);
              envStr= "DB_PROP_SQLSRV=" + propserver;
              envVarList.add(envStr);
              envStr = "SYBASE=" + System.getenv("SYBASE");
              envVarList.add(envStr);
              envStr = "SYBASE_OCS=" + System.getenv("SYBASE_OCS");
              envVarList.add(envStr);
              envStr = "LANG=en_US.UTF-8";
              envVarList.add(envStr);

              envStr = "PATH=" + System.getenv("PATH");
              envStr += ":" + ascdsenv + "/bin";
              envVarList.add(envStr);
              logMessage.println("TOO conflict: " +  envVarList);

              String pwdfile = triggerTooDataPath + "/conflicts/.ht" + userName + "too";
              String theFile = triggerTooDataPath + "/conflicts/" + userName + 
                  tooEntry.getObsid().toString() + ".conflicts";
              try {
                File pFile = new File(theFile);
                if(pFile.exists()) {
                    pFile.delete();
                }

                PrintWriter pwdPW = new PrintWriter(new FileWriter(pwdfile));
                pwdPW.println(dbconn.getDBPwd());
                pwdPW.close();
                FileUtils.setPermissions(pwdfile,"600");
      
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(6);
                nf.setMinimumFractionDigits(6);

                double tra =0;
                double tdec=0;
                String tname="";

                ObservationList obsList = new ObservationList();
                obsList = dboconn.getAllObservationListbyObsid(tooEntry.getObsid());
                // check conflicts for each distinct set of coordinates
                for (int ii=0;ii<obsList.size();ii++) {
                  Observation obs = obsList.get(ii);    
                  double obs_ra,obs_dec;
                  if (obs.getRA()==TriggerTooConstants.EMPTY_VALUE && obs.getDec()==TriggerTooConstants.EMPTY_VALUE)  {
                     obs_ra = tooEntry.getRA();
                     obs_dec = tooEntry.getDec();
                     tname = tooEntry.getTargetName();
                     logMessage.println("TOO Conflict: using requested RA/Dec for " + obs.getObsid());
                  } else {
                     obs_ra = obs.getRA();
                     obs_dec = obs.getDec();
                     tname = obs.getTargetName();
                     logMessage.println("TOO Conflict: using observation RA/Dec for " + obs.getObsid());
                  }

          
                  if (tra != obs_ra && tdec != obs_dec) {
                    String command = "echo '=============== PROCESSING COORDINATES for " + tname + " ==============' >> " +  theFile + ";";
                    command += ascdsenv + "/bin/propconflict.pl -U " + 
                    dbuser + " -d " + nf.format(obs_ra) + "/" +
	            nf.format(obs_dec) + 
                    " -q " + pwdfile + " >> " + theFile;
                    logMessage.println(command);
                    RunCommand rc = new RunCommand(command,envVarList,null);
                    logMessage.println(rc.getOutMsg());
                    logMessage.println(rc.getErrMsg());
                    logMessage.println("------------------------------");
                    tra = obs_ra;
                    tdec = obs_dec;
                  }  else {
                    logMessage.println("Coordinates match previous observation.");
                  }
                }

                // delete the pwd file
                try {
                  pFile = new File(pwdfile);
                  if(pFile.exists()) {
                    boolean returnVal = pFile.delete();
                    if (!returnVal) {
                      logMessage.println("Delete failed for " + pwdfile);
                    } 
                  } 
                }
                catch (Exception exc) {
                   logMessage.println("Delete failed for " + pwdfile);
                   logMessage.printException(exc);
                }
  
                pFile = new File(theFile);
                if(pFile.exists()) {
                  FileUtils.setPermissions(theFile,"660");

	          //Forward request to the jsp to display the resulting page
		  session.setAttribute("conflicts",theFile);
                  String url = "/displayFile.jsp?type=conflicts" ;
	          dispatcher = getServletContext().getRequestDispatcher(url );
                  response.setCharacterEncoding("UTF-8");
	          dispatcher.forward(request, response);
                }
                else {
                  message = "propconflict command failed for " + tooEntry.getObsid();
                  logMessage.println(message);
                }
              }
              catch ( Exception exc) {
                message += "Error occurred processing conflict request for TOO Trigger .\n" ;
                message += exc.getMessage();
                logMessage.printException(exc);
              }
            } else {
              message = "Unable to retrieve TOO entry.";
              logMessage.println(message);
            }
          } 
          else {
            message = "You do not have permissions to execute the propconflict application.";
            logMessage.println(message);
    
          }
              
        }
        catch (Exception exc) {
          message += "Error occurred processing conflict request for TOO Trigger .\n" ;
          message += exc.getMessage();
          logMessage.printException(exc);
        }
      
      }
      catch (Exception exc) {
        message += "Unable to connect to database.\n";
        message += exc.getMessage();
        logMessage.printException(exc);
      }

    }

    if (message != null) {
      //Forward request to the jsp to display the login page
      session.setAttribute("message",message);
      String url = "/displayTOOUpdate.jsp";
      dispatcher = getServletContext().getRequestDispatcher(url);
      dispatcher.forward(request, response);
    }

  }

}

