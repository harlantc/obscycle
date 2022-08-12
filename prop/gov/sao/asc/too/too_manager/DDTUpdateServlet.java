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
// DDTUpdateServlet

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
import ascds.Coordinate;
import ascds.RunCommand;
import ascds.FileUtils;
import ascds.LogMessage;
import ascds.NameResolver;


/******************************************************************************/
/**
 * DDTUpdateServlet handles the submit functions for the selected DDT
 * proposal.  Functions available are to save data to the database, send a
 * DDT status message or 'Cancel' and return to the full list of DDTs
 *
 */
public class DDTUpdateServlet extends HttpServlet 
{
    private Properties triggerTooProperties;
    private String triggerTooDataPath;
    private String tooReceiverPath;
    private String cxcHelpDesk;
    private String usintScript;
    private boolean showDebug=true;
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
   
      // data path for files
      triggerTooDataPath = triggerTooProperties.getProperty("triggertoo.data.path");
      tooReceiverPath = triggerTooProperties.getProperty("too.receiver.path");

      // helpdesk
      cxcHelpDesk = triggerTooProperties.getProperty("cxc.helpdesk");

      // location of script for USINT contact information
      usintScript = triggerTooProperties.getProperty("usint.script");

      
      // initialize log file
      String triggerTooLogFile = triggerTooProperties.getProperty("toomanager.log.file");
      logMessage = new LogMessage(triggerTooLogFile,"660");

      // get debug flag
      String myShowDebug = triggerTooProperties.getProperty("show.debug");
      if (myShowDebug.equalsIgnoreCase("false")) 
	showDebug = false;
      

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
    DDTEntry ddtEntry = null;
    String message = new String(" ");
    String fpmessage = new String(" ");
    String submitFilename = new String("");
    String usintContact = new String("");
    String operation;
    String origOperation = new String("");
    Integer proposalID = new Integer(0);
    String proposal_number = new String("");
    RequestDispatcher dispatcher = null;
    Boolean isDDTManager = false;
    Boolean isDDTReadOnly = true;
    Boolean isDDTConflict = false;
    Boolean isDDTMigrate = false;


    DDTInfo ddtinfo = new DDTInfo();

    HttpSession session = request.getSession(true);
    Enumeration parameterList = request.getParameterNames();
    while( parameterList.hasMoreElements() ) {
      String sName = parameterList.nextElement().toString();
      LogMessage.println("DDTUpdate: Param " + sName + " = " + request.getParameter(sName));
    }



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
      logMessage.println("Current User: " + userName);

      // get the proposal that was selected 
      try {
        proposalID = Parameter.getInteger(request,"proposalID");
        if (proposalID == null) proposalID=0;
      } catch (Exception e) {
        logMessage.println("ERROR: invalid proposal Id " );
        proposalID=0;
      }

      // get the original operation (save, send comments only, etc)
      operation = Parameter.get(request,"Submit");
      origOperation = operation;
      // if it's a update status type of function, then set the operation
      // to the DDT status value.
      
      if (operation != null  && 
          (operation.equals(TriggerTooConstants.SENDMSG) ||
           operation.equals(TriggerTooConstants.DDTUPDATE) ||
           operation.equals(TriggerTooConstants.SAVE)) ) {
         operation = Parameter.get(request,TriggerTooConstants.TOOSTATUS);
      }

      if (operation != null && operation.equals(TriggerTooConstants.CANCEL)) {
        message = ""; 
        session.setAttribute("ddtmessage",message);
        dispatcher = getServletContext().getRequestDispatcher("/displayDDTManager.jsp");
        dispatcher.forward(request, response);
      }
      else {
        
        dispatcher = getServletContext().getRequestDispatcher("/displayDDTUpdate.jsp");
        logMessage.println("***DDTUpdate: operation=" + operation + " proposalID=" + proposalID + "  origOper=" + origOperation);
      
        try {
          DBDDT dbconn = null;

          // create database connections
          String testLevel = triggerTooProperties.getProperty("test.level");
          if (testLevel != null && testLevel.equals("1")) {
            dbconn = new DBDDT(triggerTooDataPath,showDebug);
          }
  
          else {
            dbconn = new DBDDT(userName,userPwd,triggerTooDataPath,showDebug);
          }
       
          try {
            isDDTManager = new Boolean(dbconn.isDDTManagerRole());
            if (isDDTManager) {
               logMessage.println("IS MANAGER");
            }
            isDDTReadOnly = new Boolean(dbconn.isDDTReadOnly());
            isDDTConflict = new Boolean(dbconn.isConflictAllowed());
            isDDTMigrate = new Boolean(dbconn.isProp2ocatAllowed());
      

            // get DDT entry 
            ddtEntry = dbconn.getDDTEntry(proposalID);
            if (ddtEntry != null) {
              if (isDDTMigrate && origOperation != null && origOperation.equals(TriggerTooConstants.DDTUPDATE) && !isDDTManager && operation == null) {
               
                  operation = ddtEntry.getStatus();
                  LogMessage.println("Resetting operation to " + operation);
               }

              ddtEntry.setTargetList(dbconn.getPropTargetList(proposalID));

              // get the comments
              dbconn.getCommentHistory(proposalID,ddtEntry);

              if (operation != null) {
                try {
                  // get the input parameters unless this is a cancel
                  if ( !operation.equals(TriggerTooConstants.CANCELNOSEND) ) {
                     processParameters(request,operation,ddtEntry,ddtinfo);
                  }
                  if ( origOperation.equals(TriggerTooConstants.DDTUPDATE) &&
                       !operation.equals(TriggerTooConstants.APPROVED) ) {
                     message = "DDT Status must be APPROVED if you want to migrate the observation to the ObsCat";
                     LogMessage.println(message);
                     ddtinfo.msgclass = "error";
                   }
                   else {
                    try {
                    // manager- update status, exposure times, comments
                    if (isDDTManager &&  
                        (origOperation.equals(TriggerTooConstants.SENDMSG) || 
                         origOperation.equals(TriggerTooConstants.DDTUPDATE) || 
                         origOperation.equals(TriggerTooConstants.SAVE))) {
                      message = updateDatabaseFields(ddtEntry,dbconn,ddtinfo);
                      String msg2;
                      msg2 = updateDatabaseComment(ddtEntry,dbconn,false);
                      if (msg2 != null) {
                        if (message == null) { message = "";}
                        message += msg2;
                      }
                    }
                  }
                  catch (Exception exc) {
                    message += exc.getMessage();
                    LogMessage.printException(exc);
                    operation = TriggerTooConstants.SAVEDRAFT;
                    origOperation = operation;
                  }
                  // comment only type of save 
                  if (operation.equals(TriggerTooConstants.SAVEDRAFT) ||
                      operation.equals(TriggerTooConstants.SAVECMT) || 
                      operation.equals(TriggerTooConstants.SENDCMT) ||
                      (origOperation.equals(TriggerTooConstants.SAVE) && 
                       !isDDTManager)) {
                     boolean isDraft = false;
                     if (operation.equals(TriggerTooConstants.SAVEDRAFT)) {
                       isDraft = true;
                     }
                     String msg2;
                     msg2 = updateDatabaseComment(ddtEntry,dbconn,isDraft);
                     if (msg2 == null) {
                       ddtinfo.msgclass="msg";
                       if (isDraft) { 
                          message += "Comments successfully saved as 'Draft'.";
                       }
                       else  {
                          message += "Comments successfully saved.";
                       }
                     }
                  }
  
                  // get the observation info again in case of updates
                  // want to display the actual database values
                  if (!operation.equals(TriggerTooConstants.SAVEDRAFT) ) {
                    ddtEntry.setTargetList(dbconn.getPropTargetList(ddtEntry.getProposalID()));
                  } 
                  // this is a cancel from the update obscat
                  if (operation.equals(TriggerTooConstants.CANCELNOSEND)) {
                      ddtinfo.msgclass = "error";
                      message = "Update canceled.  Message was NOT sent.";
                  }
                  else if (origOperation.equals(TriggerTooConstants.SAVE) ||
                           operation.equals(TriggerTooConstants.SAVECMT) ) {
                  // this was a save only 
                      ddtinfo.msgclass = "msg";
                      message = "Data successfully saved, no email sent.";
                  }
                  // send email of comments only
                  else if (operation.equals(TriggerTooConstants.SENDCMT)) {
                    String filename = new String("");
                    filename = sendDDTStatusUpdate(operation,ddtEntry,dbconn,true,ddtinfo);
                    String jspName = "/displaySentDDT.jsp?fileName=" + filename;
                    ddtinfo.msgclass = "msg";
                    message = "Successfully sent updated comments for Proposal Number ";
                    message +=  ddtEntry.getProposalNumber();
                    dispatcher = getServletContext().getRequestDispatcher(jspName);
                  }
                  else if (origOperation.equals(TriggerTooConstants.SENDMSG)){
                    String filename = new String("");
                    filename = sendDDTStatusUpdate(operation,ddtEntry,dbconn,false,ddtinfo);
                    if (ddtEntry.getApprovalDate() == null ||
                        ddtEntry.getApprovalDate().length() < 2 ) {
                      if (ddtEntry.getStatus().equals(TriggerTooConstants.APPROVED)) {
                          dbconn.setDDTApprovalDate(ddtEntry.getProposalID(),false);
                          ddtEntry.setApprovalDate("db update");
                       }
                    } else {
                      // approval date is not null ,clear if status has been reset
                      if (!ddtEntry.getStatus().equals(TriggerTooConstants.APPROVED)) {
                          dbconn.setDDTApprovalDate(ddtEntry.getProposalID(),true);
                          ddtEntry.setApprovalDate("");
                       }
                    }
                    if (ddtEntry.getStatus().equals(TriggerTooConstants.APPROVED) &&
                        ddtEntry.hasFastProc()) {
                        try {
                          fpmessage = sendFastProcessingMessage(ddtEntry);
                        } catch (Exception exc) {
                          fpmessage = "\n" + exc.getMessage();
                        }
                    }
                        
                     
                    String jspName = "/displaySentDDT.jsp?fileName=" + filename;
                    ddtinfo.msgclass = "msg";
                    message = "Successfully sent status update for Proposal Number ";
                    message +=  ddtEntry.getProposalNumber();
                    message +=  " " + fpmessage;
                    dispatcher = getServletContext().getRequestDispatcher(jspName);
                 
                  }
                  else if (origOperation.equals(TriggerTooConstants.DDTUPDATE)){
                    dispatcher = getServletContext().getRequestDispatcher("/displayDDTMigrate.jsp");
                 
                  }
                }
                }
                catch (Exception exc) {
                  message += exc.getMessage();
                  ddtinfo.msgclass = "error";
                }
              }
              else {
                proposalID = ddtEntry.getProposalID();
                ddtEntry.setTargetList(dbconn.getPropTargetList(proposalID));
               
              }
              submitFilename = triggerTooDataPath + "/" + ddtEntry.getProposalNumber() + ".submit";
            }
          }
          catch (Exception exc) {
            message += "Error occurred retrieving DDT Entries from the database.\n" ;
            message += exc.getMessage();
            ddtinfo.msgclass = "error";
            LogMessage.printException(exc);
          }
      
        }
        catch (Exception exc) {
           message += "Unable to connect to database.\n";
           message += exc.getMessage();
           ddtinfo.msgclass = "error";
        }
        logMessage.println(message);

        //Forward request to the jsp to display the resulting page
        if (message == null) {
           message = "";
        }
        session.setAttribute("isDDTManager",isDDTManager);
        session.setAttribute("isDDTReadOnly",isDDTReadOnly);
        session.setAttribute("isDDTConflict",isDDTConflict);
        session.setAttribute("isDDTMigrate",isDDTMigrate);
        session.setAttribute("ddtmessage",message);
        session.setAttribute("ddtmsgclass",ddtinfo.msgclass);

        String sjfname="";
        if (ddtEntry != null) {
          session.setAttribute("tgtList",ddtEntry.getTargetList());
          if (ddtEntry.getRPSFilename() != null) 
            session.setAttribute("rpsFilename",ddtEntry.getRPSFilename());
          sjfname= ddtEntry.getSJFilename();
          if (sjfname != null && sjfname.length() > 2) {
            try {
              File pFile = new File(sjfname);
              if(!pFile.exists()) {
                sjfname = "";
              }
            }
            catch (Exception exc) {
            }
          }
        }
        else  {
          session.setAttribute("tgtList",null);
          ddtEntry = new DDTEntry();
        }

        session.setAttribute("ddtEntry",ddtEntry);
        session.setAttribute("triggerFilename",submitFilename);
        session.setAttribute("sjFilename",sjfname);
        session.setAttribute("ccemail",ddtinfo.ccemail);
        session.setAttribute("displayComments",ddtinfo.displayComments);
        dispatcher.forward(request, response);
      }
    }

  }

  // ---------------------------------------------------
  // Send a DDT status message 
  // ---------------------------------------------------
  private String sendDDTStatusUpdate(String operation,
	DDTEntry ddtEntry,DBDDT dbconn,
	boolean isCmtOnly,DDTInfo ddtinfo)
	throws IOException
  {
    String filename = new String("");
    String rpsFilename = new String("");
    String message = new String("");
    boolean newFile = false;
    int ii = 1;
    Integer iversion = new Integer(ii);
    PropTargetList tgtList = ddtEntry.getTargetList();


    // now build the status message
    while (!newFile) {
      try {
        iversion = new Integer(ii);
        filename = triggerTooDataPath + "/status/";
        filename += ddtEntry.getProposalNumber();
        filename += "_" + iversion.toString();
        filename += ".status.rps";
        
        File outputFile = new File(filename);
        if(outputFile.exists()) {
          ii += 1;
        }
        else {
          newFile = true;
        }
      }
      catch (Exception exc) {
        logMessage.printException(exc);
        message = "Unable to create file for DDT Status update request.";
        message += " Please notify " + cxcHelpDesk;
        throw new IOException(message);

      }
    }
    rpsFilename = tooReceiverPath + "/";
    rpsFilename += ddtEntry.getProposalNumber();
    rpsFilename += "_" + iversion.toString();
    rpsFilename += ".status";
 

    try {
      // send the status message
      // just copy the file to the receiver directory, if that fails,
      // then try sending the status message instead
      ddtEntry.writeDDTStatus(filename,tgtList,isCmtOnly);
      if (!FileUtils.copy(filename,rpsFilename))  {
        // status  is mailed only if the file copy failed
        MailUtility mu = new MailUtility(triggerTooDataPath,triggerTooProperties); 
        String fromEmail = mu.getFromEmail();
        String toEmail = mu.getStatusEmail();
        String subject =  TriggerTooConstants.DDTSUBJECT_STATUS + "(#" + ddtEntry.getProposalNumber() + ")";
        try {
          mu.mailFile(fromEmail,toEmail,null,subject, filename);
        } catch(Exception mailEx) {
          LogMessage.println("TOO Manager Mail::Caught exception : " + mailEx.getMessage());
          throw new Exception (mailEx.getMessage());
        }
      }
      FileUtils.setPermissions(rpsFilename,"460");

      message = "Sent Status message";
      ddtinfo.msgclass = "msg";
    }
    catch (Exception exc) {
      logMessage.printException(exc);
      message = "Unable to submit status message. Please notify " + cxcHelpDesk;
      throw new IOException(message);
    }

    // the above status message is only mailed if the file i/o fails.
    // Always send cc mail if any,  do this separate in case of email address
    // issues
    if (ddtinfo.ccemail != null && ddtinfo.ccemail.length() > 2) {
        MailUtility mu = new MailUtility(triggerTooDataPath,triggerTooProperties); 
        String fromEmail = mu.getFromEmail();
        String subject =  TriggerTooConstants.DDTSUBJECT_STATUS + "(#" + ddtEntry.getProposalNumber() + ")";
        try {
          mu.mailFile(fromEmail,ddtinfo.ccemail,null,subject, filename);
          LogMessage.println("Sent additional email to "  + ddtinfo.ccemail + " for\n   " + subject);
        } catch(Exception mailEx) {
          LogMessage.println("DDT Manager Mail::Caught exception : " + mailEx.getMessage());
          message = "Unable to send mail to additional email address: " + ddtinfo.ccemail;
          throw new IOException (message);
        }
    }

    return filename;
  }

  // ---------------------------------------------------
  // Send a DDT Fast Processing message 
  // ---------------------------------------------------
  private String sendFastProcessingMessage( DDTEntry ddtEntry)
	throws IOException
  {
    String filename = new String("");
    String message = new String("");
    boolean sendFile = true;
    PropTargetList tgtList = ddtEntry.getTargetList();


    // now build the status message
    try {
        filename = triggerTooDataPath + "/status/";
        filename += ddtEntry.getProposalNumber();
        filename += "_" + ddtEntry.getProposalID().toString();
        filename += ".fastproc";
        
        File outputFile = new File(filename);
        if(outputFile.exists()) {
          sendFile=false;
        }
    } catch (Exception exc) {
        logMessage.printException(exc);
        message = "Unable to create file for DDT Fast Processing request.";
        message += " Please notify " + cxcHelpDesk;
        throw new IOException(message);
    }

    if (sendFile) {
      try {
        FileWriter outputFW = new FileWriter(filename.toString());
        PrintWriter outputPW = new PrintWriter(outputFW);
        outputPW.println("Fast Processing has been approved for DDT Proposal " + ddtEntry.getProposalNumber());
        outputPW.close();
        outputFW.close();
        FileUtils.setPermissions(filename,"440");
      }
      catch (Exception exc) {
        logMessage.printException(exc);
        message = "Unable to write file for DDT Fast Processing request.";
        message += " Please notify " + cxcHelpDesk;
        throw new IOException(message);
      }
  
      try {
        MailUtility mu = new MailUtility(triggerTooDataPath,triggerTooProperties); 
        String fromEmail = mu.getFromEmail();
        String toEmail = mu.getFastProcEmail();
        String subject =  "DDT " + ddtEntry.getProposalNumber()  + ": " + TriggerTooConstants.FASTPROCMSG;
        try {
          mu.mailFile(fromEmail,toEmail,null,subject, filename);
        } catch(Exception mailEx) {
          LogMessage.println("DDT Manager Mail::Caught exception : " + mailEx.getMessage());
          message = "Unable to send fast processing message ";
          throw new IOException (message);
        }
      }
      catch (Exception exc) {
        logMessage.printException(exc);
        message = "Unable to mail file for DDT Fast Processing request.";
        throw new IOException(message);
      }
      message = "Successfully sent Fast Processing message.";
    }

    return message;
  }

   
  // ---------------------------------------------------
  // Get the user input
  // ---------------------------------------------------
  private void processParameters( HttpServletRequest request,
	String operation, DDTEntry ddtEntry,DDTInfo ddtinfo)
  {

     String str;
     String tstr;
     PropTargetList tgtList = ddtEntry.getTargetList();
     boolean isRejected = false;


     try {
     LogMessage.println("In processParameters: operation=" + operation + " numTargets=" + tgtList.size());

     ddtinfo.displayComments=Parameter.get(request,"displayComments");

     ddtinfo.ccemail = Parameter.get(request,TriggerTooConstants.CCEMAIL);
     if (ddtinfo.ccemail == null) ddtinfo.ccemail = "";
     
     if (!operation.equals(TriggerTooConstants.SENDCMT)  &&
         !operation.equals(TriggerTooConstants.SAVECMT) ) {
       str = Parameter.get(request,TriggerTooConstants.TOOSTATUS);
       if (str != null && !str.equalsIgnoreCase(ddtEntry.getStatus())) {
         logMessage.println("DDT Status: " + str);
         ddtEntry.setStatus(str);
         ddtinfo.newDDTStatus = true;
         if (str.equalsIgnoreCase(TriggerTooConstants.APPROVED)) {
           // don't set APPROVED until ready for prop2ocat
           ddtEntry.setProposalStatus("PROPOSED");
         }
         else if (str.equalsIgnoreCase(TriggerTooConstants.NOTAPPROVED)) { 
           ddtEntry.setProposalStatus("REJECTED");
           isRejected=true;
         } 
         else if  (str.equalsIgnoreCase(TriggerTooConstants.WITHDRAWN)) {
           ddtEntry.setProposalStatus("WITHDRAWN");
           isRejected=true;
         }
         else {
           ddtEntry.setProposalStatus("PROPOSED");
         }
       }

       str = Parameter.get(request,TriggerTooConstants.RESPONSEWINDOW);
       if (str != null &&ddtEntry.getUrgency().indexOf(str) < 0) {
         logMessage.println("Urgency: " + str);
         ddtEntry.setUrgency(str);
         ddtinfo.newUrgency = true;
       }
       str = Parameter.get(request,TriggerTooConstants.DATARIGHTS);
       if (str != null &&ddtEntry.getDataRights().indexOf(str) < 0) {
         logMessage.println("DataRights: " + str);
         ddtEntry.setDataRights(str);
         ddtinfo.newDataRights = true;
       }
       
       // get target status,exposure time,cxc start/stop  fields
       for (int ii=0; ii< tgtList.size() ; ii++) {
         PropTarget tgt= tgtList.get(ii);
         Vector<DDTFollowup> fupList = tgt.getFollowups();
         String targid = null;
         String tlbl = null;
         String tapp = null;
         Double appTime;
	 Double  dval;
         targid = tgt.getTargID().toString();
         // checking all the followups
         logMessage.println("PROCESS FUPS= " + fupList.size());
         if (fupList != null && fupList.size() > 0) {
            for (int ff=0; ff< fupList.size() ; ff++) {
              DDTFollowup fup = fupList.get(ff);
              String fupkey = targid + "-" + fup.getOrdr().toString();
              String fupStat = Parameter.get(request,fupkey);
              Double fupApp= new Double(0.0);
              if (!isRejected) {
                fup.setStatus(fupStat);
                tlbl = fupkey + "-appTime";
                logMessage.println(tlbl + "= " + Parameter.get(request,tlbl));
                fupApp =  Parameter.getDouble(request,tlbl);
              }  else {
                fup.setStatus("");
              }
              fup.setExpTime(fupApp);

              Double fupMin = null;
              Double fupMax = null;
              try {      
                tlbl = fupkey + "-minLead";
                fupMin = Parameter.getDouble(request,tlbl);
                if (fupMin != null) fup.setMinLead(fupMin);
              } catch (Exception e) {
                logMessage.println("Exception for " + tlbl + "= " + request.getParameter(tlbl));
              }

              try {
                tlbl = fupkey + "-maxLead";
                fupMax = Parameter.getDouble(request,tlbl);
                if (fupMax != null) fup.setMaxLead(fupMax);
              } catch (Exception e) {
                logMessage.println("Exception for " + tlbl + "= " + request.getParameter(tlbl));
              }
              
              if (showDebug) {
                LogMessage.println("DDT: " + fupkey + " status = " + fupStat +
                 " expTime= " + fupApp.toString() +
                 " minLead= " + fupMin.toString() +
                 " maxLead= " + fupMax.toString());
              }

           }

           // if followups exist get time for initial trigger observation
           try {
             tlbl = targid + "-initTime";
             if ( request.getParameter(tlbl) != null) {
               dval= Parameter.getDouble(request,tlbl);
               tgt.setInitialTime(dval);
               if (showDebug) {
                 LogMessage.println("DDT: " + targid + " initTime= " + dval.toString());
               }
             }
           } catch (Exception e) {
             logMessage.println("Exception for " + tlbl + "= " + request.getParameter(tlbl));
           }
         }
         String tStatus = Parameter.get(request,targid);
         if (!isRejected) {
           tgt.setStatus(tStatus);
         } else {
           tgt.setStatus("");
         }

         tlbl = targid + "-appTime";
         appTime = Parameter.getDouble(request,tlbl);
         if (appTime == null) appTime=TriggerTooConstants.EMPTY_VALUE;
         LogMessage.println("setting Approved Time for " + tlbl + " to " + appTime.toString());
         tgt.setApprovedTime(appTime);

         if (tgt.getResponseStart().doubleValue() >= 0) {
           tlbl = targid + "-cxcstart";
           dval = Parameter.getDouble(request,tlbl);
           if (dval == null) dval=TriggerTooConstants.EMPTY_VALUE;
           LogMessage.println("setting cxcstart to " + dval.toString());
           tgt.setResponseStart(dval);
         }

         if (tgt.getResponseStop().doubleValue() >= 0) {
           tlbl = targid + "-cxcstop";
           dval = Parameter.getDouble(request,tlbl);
           if (dval == null) dval=TriggerTooConstants.EMPTY_VALUE;
           LogMessage.println("setting cxcstop to " + dval.toString());
           tgt.setResponseStop(dval);
       
         }
         tlbl = targid + "-fastproc";
         String fpstatus = Parameter.get(request,tlbl);
         tlbl = targid + "-fastprocCmt";
         String fpcmt = Parameter.get(request,tlbl);
         tgt.setFastProcStatus(fpstatus);
         tgt.setFastProcComment(fpcmt);

         if (tgt.getGridPointings().intValue() > 0) {
           tlbl = targid + "-grid";
           Integer ival = Parameter.getInteger(request,tlbl);
           if (ival == null) ival= TriggerTooConstants.EMPTY_INT;
           LogMessage.println("setting Approved Number grid pointings to " + ival.toString());
           tgt.setGridApproved(ival);
         }
         if (showDebug) {
           LogMessage.println("DDT: " + targid + " status= " + tStatus +
            	" app= " + appTime.toString());
         }
       }
       LogMessage.println("Done with targets");
     }
  
     str = Parameter.get(request,TriggerTooConstants.COMMENT);
     if (showDebug) {
       logMessage.println("Comment: " + str);
     }
     ddtEntry.setComment(str);

    }
    catch (Exception exc) {
       LogMessage.printException(exc);
    }

  }

  private String updateDatabaseComment(DDTEntry ddtEntry,DBDDT dbconn,
	boolean isDraft) throws Exception

  {
     CommentHistory chist;
     String message = null;

     ddtEntry.setDraftComment(isDraft);
     chist = ddtEntry.getCurrentComment();
      
     try { 
       if (chist.getCommentID() > 0) {
         // update existing comment
         dbconn.updateComments(chist.getCommentID(),chist.getStatus(),
		chist.getComment());
       }
       else {
         if (chist.getComment()  != null && 
             chist.getComment().length() > 1){
           dbconn.insertComment(ddtEntry.getProposalNumber(),chist.getStatus(),
		chist.getComment());
         }
       }

       // get the comments again and reset the new comment field
       ddtEntry.clearCurrentComment();
       dbconn.getCommentHistory(ddtEntry.getProposalID(),ddtEntry);
     }
     catch (Exception exc) {
       message = "Proposal Number " + ddtEntry.getProposalNumber() + ": "; 
       message += exc.getMessage();
       throw new SQLException(message);
     }

     return message;
  }

  private String updateDatabaseFields( DDTEntry ddtEntry, DBDDT dbconn,DDTInfo ddtinfo)
	throws Exception
  {
    String message = null;


    if (ddtinfo.newDDTStatus) {
      try {
        dbconn.updateStatus(ddtEntry.getProposalID(),ddtEntry.getStatus());
      }
      catch (Exception exc) {
        message = "Proposal Number " + ddtEntry.getProposalNumber() + ": "; 
        message += exc.getMessage();
        message +="\n";
        throw new SQLException(message);
      }
    }

    if (ddtinfo.newUrgency) {
      try {
        dbconn.updateUrgency(ddtEntry.getProposalID(),ddtEntry.getUrgency());
      }
      catch (Exception exc) {
        message = "Proposal Number " + ddtEntry.getProposalNumber() + ": "; 
        message += exc.getMessage();
        message +="\n";
        throw new SQLException(message);
      }
    }

    if (ddtinfo.newDataRights) {
      try {
        dbconn.updateDataRights(ddtEntry.getProposalID(),ddtEntry.getDataRights());
      }
      catch (Exception exc) {
        message = "Proposal Number " + ddtEntry.getProposalNumber() + ": "; 
        message += exc.getMessage();
        message +="\n";
        throw new SQLException(message);
      }
    }
    // Now set Proposal Status 
    try {
      dbconn.updateProposalStatus(ddtEntry.getProposalNumber(),
		ddtEntry.getProposalStatus());
    }
    catch (Exception exc) {
      message = "Proposal Number " + ddtEntry.getProposalNumber() + ": "; 
      message += exc.getMessage();
      message +="\n";
      throw new SQLException(message);
    }
    
    // Now process the targets for the DDT, checking status and exposure time
    PropTargetList tgtList = ddtEntry.getTargetList();
    for (int ii=0; ii< tgtList.size() ; ii++) {
      PropTarget tgt= tgtList.get(ii);
      Vector<DDTFollowup> fupList = tgt.getFollowups();
      String targid = null;
      targid = tgt.getTargID().toString();
      if (fupList != null) {
        for (int ff=0; ff< fupList.size() ; ff++) {
           DDTFollowup fup = fupList.get(ff);
           try {
             dbconn.updateFollowup(tgt.getTargetNumber(),
 		  fup.getTargid(),fup.getOrdr(),
		  fup.getStatus(),fup.getExpTime(),
		  fup.getMinLead(),fup.getMaxLead());
           }
           catch (Exception exc) {
             message = "ERROR: Update failed for followup.  Not all updates may have been made.\n";
             message+= exc.getMessage();
             message+= "\n";
             throw new SQLException(message);
           }
        }
      }
      if (tgt.getResponseStart() != null && 
          tgt.getResponseStart().doubleValue() >= 0) {
        try {
          dbconn.updateTOO(tgt.getProposalNumber(),tgt.getTargetNumber(),
		tgt.getTargID(),
		tgt.getInitialTime(),
		tgt.getResponseStart(), tgt.getResponseStop());
        }
        catch (Exception exc) {
          message= exc.getMessage();
          throw new SQLException(message);
        }
      }
      try {
          dbconn.updateTarget(tgt.getProposalNumber(),tgt.getTargetNumber(),
            tgt.getTargID(),tgt.getStatus(),tgt.getApprovedTime(),
            tgt.getGridApproved());
          dbconn.updateFastProc(tgt.getTargID(),tgt.getFastProcStatus(),tgt.getFastProcComment());
      }
      catch (Exception exc) {
          message= exc.getMessage();
          message+= "\n";
          throw new SQLException(message);
        }
       
    } 

    return message;
  }

}

