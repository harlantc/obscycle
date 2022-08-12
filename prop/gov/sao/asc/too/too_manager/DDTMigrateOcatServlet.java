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
// DDTMigrateOcat

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.Vector;
import java.util.ArrayList;
import java.lang.Thread;
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

public class DDTMigrateOcatServlet extends HttpServlet 
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
    Integer proposalID = new Integer(0);
   


    String operation = request.getParameter("Submit");
    String userName = (String)session.getAttribute("userName");
    String userPwd = (String)session.getAttribute("userPwd");
    if (userName == null || userName.equals("") ||
        userPwd == null || userPwd.equals("") ) {

      message = new String(TriggerTooConstants.INVALID_USER);
      logMessage.println(message);
    
    }
    else if (operation == null || !operation.equals(TriggerTooConstants.DDTUPDATE)) {
      message = "Request canceled, proposal NOT migrated to ObsCat.";
      logMessage.println(message);
    }
    else {
      logMessage.println("Current User: " + userName);
      // get the proposal that was selected 
      String str = request.getParameter("proposalID");
      if (str != null) {
        proposalID = new Integer(str);
      }

      try {
        DBDDT dbconn = null;
	DDTEntry ddtEntry = null;

        // create database connections
        String testLevel = triggerTooProperties.getProperty("test.level");
        if (testLevel != null && testLevel.equals("1")) {
          dbconn = new DBDDT(triggerTooDataPath,showDebug);
        } else {
          dbconn = new DBDDT(userName,userPwd,triggerTooDataPath,showDebug);
        }

        try {
          Boolean isDDTManager = new Boolean(dbconn.isDDTManagerRole());
          Boolean isDDTMigrate = new Boolean(dbconn.isProp2ocatAllowed());
          if (isDDTMigrate) {
            // get DDT entry 
            ddtEntry = dbconn.getDDTEntry(proposalID);
            if (ddtEntry != null) {
              String server = dbconn.getDBServer();
              String ddtserver = dbconn.getDDTServer();
              String dbuser = dbconn.getDBUser();
              String ascdsenv = triggerTooProperties.getProperty("ascds.release");
              ArrayList<String> envVarList = new ArrayList<String>();
              String envStr = "ASCDS_BIN=" + ascdsenv + "/bin";
              envVarList.add(envStr);
              logMessage.println(envStr + "\n");
              envStr = "LD_LIBRARY_PATH=" + ascdsenv + "/lib" ;
              envVarList.add(envStr);
              logMessage.println(envStr + "\n");
              envStr= "DB_LOCAL_SQLSRV=" + server;
              envVarList.add(envStr);
              logMessage.println(envStr + "\n");
              envStr= "DB_REMOTE_SQLSRV=" + server;
              envVarList.add(envStr);
              logMessage.println(envStr + "\n");
              envStr= "DB_PROP_SQLSRV=" + server;
              envVarList.add(envStr);
              logMessage.println(envStr + "\n");
              envStr= "DB_OCAT_SQLSRV=" + ddtserver;
              envVarList.add(envStr);
              logMessage.println(envStr + "\n");
              envStr = "SYBASE=" + System.getenv("SYBASE");
              envVarList.add(envStr);
              logMessage.println(envStr + "\n");
              envStr = "SYBASE_OCS=" + System.getenv("SYBASE_OCS");
              envVarList.add(envStr);
              logMessage.println(envStr + "\n");

              String pwdfile = triggerTooDataPath + "/conflicts/.ht" + userName + "ddt";
              try {
                String y= "";
                String x= dbconn.getDBPwd();
                for (int yy=0;yy<x.length();yy++) {
                  char xxx = x.charAt(yy);
                  Integer tt= new Integer(Integer.toOctalString(xxx));
                  y += String.format("%03d",tt.intValue());
                }
                PrintWriter pwdPW = new PrintWriter(new FileWriter(pwdfile));
                pwdPW.println(y);
                pwdPW.close();
                FileUtils.setPermissions(pwdfile,"600");
     

                //String command = "/pool14/dmh/prop2ocat -U " + dbuser +
                String command = ascdsenv + "/bin/prop2ocat -U " + dbuser +
	 	  " -a "  + ddtEntry.getProposalAOId().toString()  +
                  " -p " + ddtEntry.getProposalNumber() + 
                  " -q " + pwdfile ;
                logMessage.println(command);

                // debug override command
                //command = "ldd /home/ascds/DS.release/bin/prop2ocat ";
                RunCommand rc = new RunCommand(command,envVarList,null);
                logMessage.println("logging prop2ocat out");
                logMessage.println(rc.getOutMsg());
                logMessage.println("logging prop2ocat err");
                logMessage.println(rc.getErrMsg());
                int ick = rc.getExitValue();
                logMessage.println("prop2ocat exit value is " + ick);

                // delete the pwd file
                try {
                  File pFile = new File(pwdfile);
                  if(pFile.exists()) {
                    boolean returnVal = pFile.delete();
                    if (!returnVal) {
                      logMessage.println("Delete failed for " + pwdfile);
                    } 
                  } 
                } 
                catch (Exception exc) {
                  logMessage.printException(exc);
                }

                // retrieve again and make sure ocat_id is set,
                // else redirect back to manager with error message
                // try 3 times just in case of replication delay

                int numTries=0;
                while (numTries < 3) {
                  ddtEntry = dbconn.getDDTEntry(proposalID);
                  if (!ddtEntry.inObsCat() ) {
                    try {
                      logMessage.println("Sleeping for 1 second, then retry");
                      Thread.sleep(1000);
                    }       
                    catch(InterruptedException ex) {
                    }
                    numTries++;
                  } else {
                    numTries =3;
                  }
                }

                // Now go to ObsCat display
                if (ddtEntry.inObsCat()) {
                  message= null;
                  ddtEntry.setProposalStatus("APPROVED");
		  dbconn.updateProposalStatus(ddtEntry.getProposalNumber(),
                	ddtEntry.getProposalStatus());

                  session.setAttribute("prop",ddtEntry.getProposalNumber());
                  String url = "/ddtOCat.jsp?prop=" ;
                  url += ddtEntry.getProposalNumber();
                  dispatcher = getServletContext().getRequestDispatcher(url );
               
                  try {
                    DBObservation dboconn = null;
                    // create database connections
                    if (testLevel != null && testLevel.equals("1")) {
                      dboconn = new DBObservation(triggerTooDataPath,showDebug);
                    } else {
                      dboconn = new DBObservation(userName,userPwd,triggerTooDataPath,showDebug);
                    }
                    ObservationList obsList = dboconn.getObsListbyProposalNumber(ddtEntry.getProposalNumber());
                    mailMigrateMsg(ddtEntry.getProposalNumber(),obsList);
                    

                  } catch (Exception exc) {
                     LogMessage.println("Obscat message failed");
                     LogMessage.printException(exc);
                  }
                     
                  dispatcher.forward(request, response);
               
                }
                else {
                  message = "Migration to ObsCat failed for " + ddtEntry.getProposalNumber();
                  logMessage.println(message);
                }
              }
              catch ( Exception exc) {
                message += "Error occurred processing prop2ocat request for DDT Proposal .\n" ;
                message += exc.getMessage();
                logMessage.printException(exc);
              }
            } else {
              message = "Unable to retrieve DDT entry.";
              logMessage.println(message);
            }
          } 
          else {
            message = "You do not have permissions to execute the prop2ocat application.";
            logMessage.println(message);
    
          }
              
        }
        catch (Exception exc) {
          message += "Error occurred processing conflict request for DDT Proposal .\n" ;
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
      session.setAttribute("ddtmessage",message);
      session.setAttribute("ddtmsgclass","error");
      String url = "/displayDDTUpdate.jsp";
      dispatcher = getServletContext().getRequestDispatcher(url);
      dispatcher.forward(request, response);
    }

  }

  public void mailMigrateMsg(String proposalNumber,ObservationList obsList)
  {
    File outputFile = null;
    Observation obs;

    try {
      boolean newFile = false;
      int ii = 1;
      Integer iversion = new Integer(ii);
      String filename = ".dummy";

      while (!newFile) {
        iversion = new Integer(ii);
        filename = triggerTooDataPath + "/obscat_update/";
        filename += proposalNumber;
        filename += "_" + iversion.toString();
        filename += ".ddt.migrate";
        outputFile = new File(filename);
        if(outputFile.exists()) {
          ii += 1;
        }
        else {
          newFile = true;
        }
      }
      FileWriter outputFW = new FileWriter(filename);
      PrintWriter outputPW = new PrintWriter(outputFW);
      NumberFormat nf = NumberFormat.getInstance();
      nf.setMaximumFractionDigits(2);
      nf.setMinimumFractionDigits(2);

      outputPW.println("The DDT proposal " + proposalNumber + " was migrated to the ObsCat.\n");
      outputPW.println("SeqNum  ObsId  AppTime  PreId");
      outputPW.println("------  -----  -------  -------");
      for (ii=0; ii<obsList.size(); ii++) {
        obs = (Observation)obsList.get(ii);
        outputPW.print(obs.getSequenceNumber().toString() + "  ");
        outputPW.print(obs.getObsid().toString() + "  ");
        outputPW.print(nf.format(obs.getApprovedExpTime().doubleValue()) + "  ");
        if (obs.getPreID().intValue() > 0) {
          outputPW.print(obs.getPreID().toString() + "  ");
        }
        outputPW.println("");
      }
      outputPW.println("");
      outputPW.close();
      outputFW.close();
      FileUtils.setPermissions(outputFile,"440");

      MailUtility mu = new MailUtility(triggerTooDataPath,triggerTooProperties); 
      String fromEmail = mu.getFromEmail();
      String toEmail = mu.getDDTObscatEmail();
      String subject = proposalNumber + "- DDT ObsCat";
      try {
        mu.mailFile(fromEmail,toEmail,null,subject, filename);
      } catch(Exception mailEx) {
        LogMessage.println("DDT Manager Mail::Caught exception : " + mailEx.getMessage());
        throw new Exception (mailEx.getMessage());
      }

     
    }
    catch (Exception exc) {
      LogMessage.println("Mailing DDT migrate message failed for " + proposalNumber);
      LogMessage.printException(exc);
    }
  }


}

