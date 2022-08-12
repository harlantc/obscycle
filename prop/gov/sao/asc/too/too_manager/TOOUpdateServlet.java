/*
  Copyrights:
 
  Copyright (c) 2000-2014 Smithsonian Astrophysical Observatory
 
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
// TOOUpdateServlet

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
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
import org.apache.commons.lang3.*;
import info.*;
import db.*;
import ascds.Coordinate;
import ascds.RunCommand;
import ascds.FileUtils;
import ascds.LogMessage;
import ascds.NameResolver;


/******************************************************************************/
/**
 * TOOUpdateServlet handles the submit functions for the select triggered
 * TOO.  Functions available are to save data to the database, send a
 * TOO status message , perform the NameResolver action in NED/Simbad to
 * find coordinates, or 'Cancel' and return to the full list of Triggered TOOs.
 *
 */
public class TOOUpdateServlet extends HttpServlet 
{
    private Properties triggerTooProperties;
    private String triggerTooDataPath;
    private String tooReceiverPath;
    private String observationDetailLink;
    private NameResolver nameResolver;
    private String cxcHelpDesk;
    private String usintScript;
    private boolean showDebug=false;
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

      // name resolover for NED/Simbad
      nameResolver = new NameResolver(triggerTooProperties,"\n");

      // location of script for USINT contact information
      //usintScript = triggerTooProperties.getProperty("cxc.usint");

      // link to detail observation information
      observationDetailLink = triggerTooProperties.getProperty("observation.detail.link");
      
      // initialize log file
      String triggerTooLogFile = triggerTooProperties.getProperty("toomanager.log.file");
      logMessage = new LogMessage(triggerTooLogFile,"660");

      // get debug flag
      String myShowDebug = triggerTooProperties.getProperty("show.debug");
      showDebug = myShowDebug.equalsIgnoreCase("true")  ;
      
  }

   

  /****************************************************************************/
  /**
   * Handle a submission from a browser.  
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
    ObservationList obsList = new ObservationList();
    ObservationList altList = new ObservationList();
    TriggerTooEntry tooEntry = null;
    String message = new String(" ");
    String submitFilename = new String("");
    String usintContact = new String("");
    String operation;
    String origOperation = new String("");
    Integer triggerID = new Integer(0);
    Integer obsid = new Integer(0);
    RequestDispatcher dispatcher = null;
    String str;
    Boolean isManager = false;
    Boolean isReadOnly = false;


    TOOInfo tooinfo = new TOOInfo();

    HttpSession session = request.getSession(true);
    session.setAttribute(TriggerTooConstants.OBSDETAIL,observationDetailLink);
    String tooSummary = triggerTooProperties.getProperty("cxc.toosummary");

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
      try {
        triggerID = Parameter.getInteger(request,"triggerID");
        if  (triggerID==null) triggerID=0;
      } catch (Exception e) {
        logMessage.println("ERROR: invalid triggerID " );
        triggerID=0;
      }
      operation = Parameter.get(request,"Submit");
      origOperation = operation;
      if (operation == null) operation = "Initial";

      // if status update, save off status as the operation 
      if (operation.equals(TriggerTooConstants.SENDMSG) ||
        operation.equals(TriggerTooConstants.SAVE) ) {
        operation = Parameter.get(request,TriggerTooConstants.TOOSTATUS);
      }

      if (triggerID==0) {  
        session.setAttribute("message","Unexpected error.  Invalid trigger ID");
        dispatcher = getServletContext().getRequestDispatcher("/displayTOOManager.jsp");
        dispatcher.forward(request, response);
      } else if (operation.equals(TriggerTooConstants.CANCEL)) {
        message = ""; 
        session.setAttribute("message",message);
        dispatcher = getServletContext().getRequestDispatcher("/displayTOOManager.jsp");
        dispatcher.forward(request, response);
      }
      else {
        
        dispatcher = getServletContext().getRequestDispatcher("/displayTOOUpdate.jsp");
        logMessage.println("***TOOUpdate: operation=" + operation + " triggerid=" + triggerID);
      
        try {

          // create database connections
          DBTriggerToo dbconn = null;
          DBObservation dboconn = null;
          String testLevel = triggerTooProperties.getProperty("test.level");
          if (testLevel != null && testLevel.equals("1")) {
            dbconn = new DBTriggerToo(triggerTooDataPath,showDebug);
            dboconn = new DBObservation(triggerTooDataPath,showDebug);
          }
          else {
            dbconn = new DBTriggerToo(userName,userPwd,triggerTooDataPath,showDebug);
            dboconn = new DBObservation(userName,userPwd,triggerTooDataPath,showDebug);
          }
        
          try {
            isManager = new Boolean(dbconn.isManagerRole());
            isReadOnly = new Boolean(dbconn.isReadOnly());

            // get TOO entry 
            tooEntry = dbconn.getTriggerTooEntry(triggerID);
            if (tooEntry != null) {
              // get the comments
              dbconn.getCommentHistory(triggerID,tooEntry);
              tooSummary = tooSummary.replaceAll("PNO",tooEntry.getProposalNumber());
              tooSummary = tooSummary.replaceAll("NN",tooEntry.getCycle());

              if (!operation.equals("Initial")) {

                try {
                  // get the input parameters
                  processParameters(request,operation,tooEntry,tooinfo);

                  try {
                    if (isManager && 
                        (origOperation.equals(TriggerTooConstants.SENDMSG) || 
                         origOperation.equals(TriggerTooConstants.SAVE))) {
                      message = updateDatabaseFields(tooEntry,dbconn,tooinfo);
                      String msg2;
                      msg2 = updateDatabaseComment(tooEntry,dbconn,false);
                      if (msg2 != null) {
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
                  if (operation.equals(TriggerTooConstants.SAVEDRAFT) ||
                      operation.equals(TriggerTooConstants.SENDCMT) ||
                      (origOperation.equals(TriggerTooConstants.SAVE) && 
                       !isManager)) {
                    boolean isDraft = false;
                    if (operation.equals(TriggerTooConstants.SAVEDRAFT)) {
                      isDraft = true;
                    }
                    String msg2;
                    msg2 = updateDatabaseComment(tooEntry,dbconn,isDraft);
                    if (msg2 == null) {
                      if (message.length() < 2) {
                        tooinfo.msgclass="msg";
                      }
                      if (isDraft) { 
                        message += "Comments successfully saved as 'Draft'.";
                      } else  {
                        message += "Comments successfully saved.";
                      }
                    }
                  }  // saving comments only

                    
                  // get the observation info again in case of changes 
                  // make sure we have what's actually in database
                  obsid = tooEntry.getObsid();
                  obsList = dboconn.getAllObservationListbyObsid(obsid);
                  altList = dboconn.getObservationListbyAltGroup(
                     tooEntry.getAlternateGroupName(),tooEntry.getProposalID());
                  for (int aa=0;aa<altList.size();aa++) {
                    Observation altObs = (Observation)altList.get(aa);
                    if (altObs.getPreID().intValue() <= 0)  {
                      int linkedCount = dboconn.getLinkedCount(altObs.getObsid());
                      altObs.setLinkedCount(linkedCount);
                    }
                  }

                  if (origOperation.equals(TriggerTooConstants.SAVE) ) {
                    message = "Data successfully saved, no email sent.";
                    tooinfo.msgclass = "msg";
                  }
                  // send email of comments only
                  else if (operation.equals(TriggerTooConstants.SENDCMT)) {
                    String filename = new String("");
                    filename = sendTooStatusUpdate(operation,tooEntry,obsList,dbconn,true,tooinfo);
                    String jspName = "/displaySentMessage.jsp?fileName=" + filename;
                    message = "Successfully sent updated comments for Observation Id ";
                    message +=  tooEntry.getObsid();
                    dispatcher = getServletContext().getRequestDispatcher(jspName);
                  }
                  else if (origOperation.equals(TriggerTooConstants.SENDMSG) ) {
                    String statusStr = operation;
                    String filename = new String("");
                    filename = sendTooStatusUpdate(operation,tooEntry,
                                                   obsList,dbconn,false,tooinfo);
                    String jspName = "/displaySentMessage.jsp?fileName=" + filename;
                    message = "Successfully sent '" + statusStr + "' message for Observation ID ";
                    message +=  tooEntry.getObsid();
                    dispatcher = getServletContext().getRequestDispatcher(jspName);
                    // if alternates exist, send reminder out to arcops to
                    // review the status
                    // Make sure it's at least 2 entries, cause if it's only
                    // 1 alternate, it must be itself
                    if (statusStr.equals(TriggerTooConstants.APPROVED)  &&
                        altList.size() > 1) {
                       sendAlternateGroupMessage(obsid,altList);
                    }
                    // if fast processing, send notification to fastproc
                    if (statusStr.equals(TriggerTooConstants.APPROVED)) {
                       if (tooEntry.getFastProc().equals("approved")) {
                         sendFastProcessingMessage(obsid,tooEntry.getTriggerID());
                       }
                    }
                  }
                  else if (operation.equals(TriggerTooConstants.UPDATEOBSCAT)) {
                    session.setAttribute("origRA",tooEntry.getOverrideRAString());
                    session.setAttribute("origDec",tooEntry.getOverrideDecString());
                    session.setAttribute("origTgtName",tooEntry.getOverrideTargetName());
                    dispatcher = getServletContext().getRequestDispatcher("/displayObsCatUpdates.jsp");
                  }
                }
                catch (Exception exc) {
                  message += exc.getMessage();
                  tooinfo.msgclass = "error";
                }
              }
              else {
                obsid = tooEntry.getObsid();
                obsList = dboconn.getAllObservationListbyObsid(obsid);
                altList = dboconn.getObservationListbyAltGroup(
		            tooEntry.getAlternateGroupName(),
                            tooEntry.getProposalID());
                for (int aa=0;aa<altList.size();aa++) {
                  Observation altObs = (Observation)altList.get(aa);
                  int linkedCount = dboconn.getLinkedCount(altObs.getObsid());
                  altObs.setLinkedCount(linkedCount);
                }
              }
              submitFilename = triggerTooDataPath + "/" + obsid + "_v" +
	 	     tooEntry.getVersion() + ".submit";
            }
          }
          catch (Exception exc) {
            message += "Error occurred retrieving TOO Entries from the database.\n" ;
            message += exc.getMessage();
            tooinfo.msgclass = "error";
          }
      
        }
        catch (Exception exc) {
           message += "Unable to connect to database.\n";
           message += exc.getMessage();
           tooinfo.msgclass = "error";
        }

        //Forward request to the jsp to display the resulting page
        if (message == null) {
           message = "";
        }
        else {
          logMessage.println(message);
        }
        logMessage.println("setting session variables ");
        
        session.setAttribute("isManager",isManager);
        session.setAttribute("isReadOnly",isReadOnly);
        session.setAttribute("tooSummary",tooSummary);
        session.setAttribute("usintContact",usintContact);
        session.setAttribute("message",message);
        session.setAttribute("msgclass",tooinfo.msgclass);
        session.setAttribute("obsList",obsList);
        session.setAttribute("altList",altList);
        session.setAttribute("tooEntry",tooEntry);
        session.setAttribute("triggerFilename",submitFilename);
        session.setAttribute("ccemail",tooinfo.ccemail);
        session.setAttribute(TriggerTooConstants.RESOLVERLIST,tooinfo.resolverSelector);
        session.setAttribute("displayAlts",tooinfo.displayAlts);
        session.setAttribute("displayComments",tooinfo.displayComments);
        dispatcher.forward(request, response);
      }
    }

  }

  // ---------------------------------------------------
  // Send a TOO status message 
  // ---------------------------------------------------
  private String sendTooStatusUpdate(String operation,
	TriggerTooEntry tooEntry,ObservationList obsList,DBTriggerToo dbconn,
	boolean isCmtOnly,TOOInfo tooinfo)
	throws IOException
  {
    String filename = new String("");
    String rpsFilename = new String("");
    String message = new String("");
    boolean newFile = false;
    int ii = 1;
    Integer iversion = new Integer(ii);

   String pocStr = "";
    String pocFile = triggerTooProperties.getProperty("mp.poc.file");
    BufferedReader pocFileBF = null;
    String inputLine;
    try {
      pocFileBF = new BufferedReader(new FileReader(pocFile));
      while( (inputLine = pocFileBF.readLine()) != null) {
        if (!inputLine.startsWith("#")) {
          if (pocStr.indexOf(inputLine.trim()) < 0 ) {
            pocStr += inputLine;
          }
        }
      }
    } catch(Exception exc) {
      logMessage.printException(exc);
    } finally {
      try {
        pocFileBF.close();
      }
      catch (Exception exc) {
        logMessage.printException(exc);
      }
    }


    // now build the status message
    while (!newFile) {
      try {
        iversion = new Integer(ii);
        filename = triggerTooDataPath + "/status/";
        filename += tooEntry.getObsid().toString();
        filename += "_v" + tooEntry.getVersion().toString();
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
        message = "Unable to create file for TOO Status update request.";
        message += " Please notify " + cxcHelpDesk;
        throw new IOException(message);

      }
    }
    rpsFilename = tooReceiverPath + "/";
    rpsFilename += tooEntry.getObsid().toString();
    rpsFilename += "_v" + tooEntry.getVersion().toString();
    rpsFilename += "_" + iversion.toString();
    rpsFilename += ".status";
 

    try {
      // send the status message
      // just copy the file to the receiver directory, if that fails,
      // then try sending the status message instead
      tooEntry.setUSINT(pocStr);
      tooEntry.writeTriggerStatus(filename,obsList,isCmtOnly);
      if (!FileUtils.copy(filename,rpsFilename))  {
        // status  is mailed only if the file copy failed
        MailUtility mu = new MailUtility(triggerTooDataPath,triggerTooProperties); 
        String fromEmail = mu.getFromEmail();
        String toEmail = mu.getStatusEmail();
        String subject =  TriggerTooConstants.SUBJECT_STATUS + "(#" + tooEntry.getSequenceNumber() + ")";
        try {
          mu.mailFile(fromEmail,toEmail,null,subject, filename);
        } catch(Exception mailEx) {
          LogMessage.println("TOO Manager Mail::Caught exception : " + mailEx.getMessage());
          throw new Exception (mailEx.getMessage());
        }
      }
      FileUtils.setPermissions(rpsFilename,"460");

      message = "Sent Status message";
      tooinfo.msgclass = "msg";
    }
    catch (Exception exc) {
      logMessage.printException(exc);
      message = "Unable to submit status message. Please notify " + cxcHelpDesk;
      throw new IOException(message);
    }

    // the above status message is only mailed if the file i/o fails.
    // Always send cc mail if any,  do this separate in case of email address
    // issues
    if (tooinfo.ccemail != null && tooinfo.ccemail.length() > 2) {
        MailUtility mu = new MailUtility(triggerTooDataPath,triggerTooProperties); 
        String fromEmail = mu.getFromEmail();
        String subject =  TriggerTooConstants.SUBJECT_STATUS + "(#" + tooEntry.getSequenceNumber() + ")";
        try {
          mu.mailFile(fromEmail,tooinfo.ccemail,null,subject, filename);
          LogMessage.println("Sent additional email to "  + tooinfo.ccemail + " for\n   " + subject);
        } catch(Exception mailEx) {
          LogMessage.println("TOO Manager Mail::Caught exception : " + mailEx.getMessage());
          message = "Unable to send mail to additional email address: " + tooinfo.ccemail;
          throw new IOException (message);
        }
    }

    return filename;
  }

 
  // ---------------------------------------------------
  // Get the user input
  // ---------------------------------------------------
  private void processParameters( HttpServletRequest request,
	String operation,
	TriggerTooEntry tooEntry, TOOInfo tooinfo)
  {

     String str;

     LogMessage.println("Processing Parameters"); 

     tooinfo.displayAlts = Parameter.get(request,"displayAlts");
     tooinfo.displayComments = Parameter.get(request,"displayComments");

     tooinfo.ccemail = Parameter.get(request,TriggerTooConstants.CCEMAIL);
     if (tooinfo.ccemail == null) tooinfo.ccemail="";
     tooEntry.setAdditionalEmail(tooinfo.ccemail);

     tooinfo.resolverSelector = Parameter.get(request,TriggerTooConstants.RESOLVERLIST);

     if (!operation.equals(TriggerTooConstants.SENDCMT)) {
       str = Parameter.get(request,TriggerTooConstants.TOOSTATUS);
       logMessage.println("Status: " + str);
       if (!str.equalsIgnoreCase(tooEntry.getStatus())) {
         tooEntry.setStatus(str);
         tooinfo.newStatus = true;
       }

       str = Parameter.get(request,TriggerTooConstants.RESPONSEWINDOW);
       logMessage.println("Urgency: " + str);
       if (tooEntry.getUrgency().indexOf(str) < 0) {
         tooEntry.setUrgency(str);
         tooinfo.newUrgency = true;
       }


       logMessage.println("FastProc: again ");
       str = Parameter.get(request,TriggerTooConstants.FASTPROCSTATUS);
       if (str != null)  {
         if (!tooEntry.getFastProc().equals(str)) {
           tooEntry.setFastProc(str);
           tooinfo.newFastProc=true;
         }
       }
       str = Parameter.get(request,TriggerTooConstants.FASTPROCCOMMENT);
       if (str != null)  {
         str =  str.replaceAll("[^\\p{ASCII}]","");
         logMessage.println("FastProcComment: " + str);
         if (!tooEntry.getFastProcComment().equals(str)) {
           tooEntry.setFastProcComment(str);
           tooinfo.newFastProc=true;
         }
       }
     }
  
     str = Parameter.get(request,TriggerTooConstants.COMMENT);
     str =  str.replaceAll("[^\\p{ASCII}]","");
     if (showDebug) {
       logMessage.println("Comment: " + str);
     }
     tooEntry.setComment(str);

  }

  private String updateDatabaseComment(TriggerTooEntry tooEntry,DBTriggerToo dbconn,
	boolean isDraft) throws Exception

  {
     CommentHistory chist;
     String message = null;

     tooEntry.setDraftComment(isDraft);
     chist = tooEntry.getCurrentComment();
      
     try { 
       if (chist.getCommentID() > 0) {
         // update existing comment
LogMessage.println("updating existing comment");
         dbconn.updateComments(chist.getCommentID(),chist.getStatus(),
		chist.getComment());
       }
       else {
         if (chist.getComment()  != null && 
             chist.getComment().length() > 1){
           dbconn.insertComment(tooEntry.getTriggerID(),chist.getStatus(),
		chist.getComment());
         }
       }

       // get the comments again and reset the new comment field
       tooEntry.clearCurrentComment();
       dbconn.getCommentHistory(tooEntry.getTriggerID(),tooEntry);
     }
     catch (Exception exc) {
       message = "Obsid " + tooEntry.getObsid() + ": "; 
       message += exc.getMessage();
       throw new SQLException(message);
     }

     return message;
  }

  private String updateDatabaseFields( TriggerTooEntry tooEntry,DBTriggerToo dbconn,TOOInfo tooinfo)
	throws Exception
  {
    String message = null;


    if (tooinfo.newStatus) {
      try {
        dbconn.updateStatus(tooEntry.getTriggerID(),tooEntry.getStatus());
      }
      catch (Exception exc) {
        message = "Obsid " + tooEntry.getObsid() + ": "; 
        message += exc.getMessage();
        throw new SQLException(message);
      }
    }
    if (tooinfo.newFastProc) {
      try {
        dbconn.updateFastProc(tooEntry.getObsid(),tooEntry.getFastProc(),
		tooEntry.getFastProcComment());
      } catch (Exception exc) {
        message = "Obsid " + tooEntry.getObsid() + ": "; 
        message += exc.getMessage();
        throw new SQLException(message);
      }
    }

    if (tooinfo.newUrgency) {
      try {
        dbconn.updateUrgency(tooEntry.getTriggerID(),tooEntry.getUrgency());
      }
      catch (Exception exc) {
        message = "Obsid " + tooEntry.getObsid() + ": "; 
        message += exc.getMessage();
        throw new SQLException(message);
      }
    }

    return message;
  }

  // --------------------------------------------------------------------------
  // Send a reminder to arcops to review status of alternate targets for this
  // group.
  // --------------------------------------------------------------------------
  private void sendAlternateGroupMessage(Integer obsid,ObservationList altList)
      throws Exception
  {
     String str;
     String cmd;
     String message = new String("");
     Observation obs = null;
     String arcopsMsg = "";
     String updateCoords;
     String filename = new String("");
     boolean newFile = false;
     int ii = 1;
     Integer iversion = new Integer(ii);
     File outputFile = null;
     String proposalNumber = "";


    // now create the file 
    while (!newFile) {
      try {
        iversion = new Integer(ii);
        filename = triggerTooDataPath + "/alternate_groups/";
        filename += obsid.toString();
        filename += "_v" + iversion.toString();
        filename += ".alternate.group";
        
        outputFile = new File(filename);
        if(outputFile.exists()) {
          ii += 1;
        }
        else {
          newFile = true;
        }
      }
      catch (Exception exc) {
        logMessage.printException(exc);
        message = "Unable to create file for TOO Alternate Group message.";
        message += " Please notify " + cxcHelpDesk;
        throw new IOException(message);

      }
    }

    try {
      FileWriter outputFW = new FileWriter(filename.toString());
      PrintWriter outputPW = new PrintWriter(outputFW);
      outputPW.println("An observation has been approved and it is part of an alternate");
      outputPW.println("group set. Please verify the status of the alternate group.\n");

      obs = (Observation)altList.get(0);
      proposalNumber = obs.getProposalNumber();

      if (proposalNumber != null) {
        outputPW.println("Proposal Number:                " + proposalNumber);
      }
      outputPW.println("Alternate Target Group Name:    " + obs.getAlternateGroupName());
      outputPW.println("Alternate Target Approved Count:" + obs.getAlternateApprovedCount().toString());
      outputPW.print("\n" + StringUtils.rightPad("SeqNbr",6) + "  ");
      outputPW.print(StringUtils.rightPad("ObsId",6) + "  ");
      outputPW.print(StringUtils.rightPad("Target Name",22) + "  ");
      outputPW.print(StringUtils.rightPad("Status ",10) + "  ");
      outputPW.print(StringUtils.rightPad("#Followups ",11) + "  ");
      outputPW.println("\n---------------------------------------------------------------");
      for (ii=0; ii<altList.size(); ii++) {
        obs = (Observation)altList.get(ii);
        if (obs.getPreID().intValue() <= 0)  {
          outputPW.print(StringUtils.rightPad(obs.getSequenceNumber(),6) + "  ");
          outputPW.print(StringUtils.rightPad(obs.getObsid().toString(),6) + "  ");
          outputPW.print(StringUtils.rightPad(obs.getTargetName(),22) + "  ");
          outputPW.print(StringUtils.rightPad(obs.getStatus(),10) + "  ");
          outputPW.print(obs.getLinkedCount().toString() + "  ");
          if (obs.getObsid().equals(obsid)) {
            outputPW.print("    (Triggered) ");
          }
          outputPW.println("");
        }
      }
      outputPW.close();
      outputFW.close();
      FileUtils.setPermissions(outputFile,"440");
    }
    catch (Exception exc) {
      logMessage.printException(exc);
      message += exc.getMessage();
    }

    MailUtility mu = new MailUtility(triggerTooDataPath,triggerTooProperties); 
    String fromEmail = mu.getFromEmail();
    String toEmail =  mu.getAlternateEmail();
    String subject = proposalNumber + "- " + TriggerTooConstants.SUBJECT_ALTERNATE; 
    try {
      mu.mailFile(fromEmail,toEmail,null,subject, filename);
    } catch(Exception mailEx) {
      LogMessage.println("TOO Manager Mail::Caught exception : " + mailEx.getMessage());
      message = mailEx.getMessage();
    }

    if (message.length() > 0) {
      throw new Exception(message);
    }
  }

  // --------------------------------------------------------------------------
  // Send Fast Processing Message on every Status Update of APPROVED
  // --------------------------------------------------------------------------
  private void sendFastProcessingMessage(Integer obsid,Integer triggerId)
      throws Exception
  {
     String str;
     String cmd;
     String message = new String("");
     Observation obs = null;
     String fastprocMsg = "";
     String filename = new String("");
     File outputFile = null;
     boolean sendFile=true;


    // now create the file, if it already exists then we already notified 
    // so don't send it again 
      try {
        filename = triggerTooDataPath + "/status/";
        filename += obsid.toString();
        filename += "_" + triggerId.toString();
        filename += ".fastproc";
        
        outputFile = new File(filename);
        if (outputFile.exists()) {
          sendFile=false;
        }
      }
      catch (Exception exc) {
        logMessage.printException(exc);
        message = "Unable to create file for TOO Fast Processing message.";
        message += " Please notify " + cxcHelpDesk;
        throw new IOException(message);

      }

    if (sendFile) {
      try {
        FileWriter outputFW = new FileWriter(filename.toString());
        PrintWriter outputPW = new PrintWriter(outputFW);
        outputPW.println("Fast Processing has been approved for TOO obsid " + obsid);
        outputPW.close();
        outputFW.close();
        FileUtils.setPermissions(outputFile,"440");
      }
      catch (Exception exc) {
        logMessage.printException(exc);
        message += exc.getMessage();
      }
  
      MailUtility mu = new MailUtility(triggerTooDataPath,triggerTooProperties); 
      String fromEmail = mu.getFromEmail();
      String toEmail =  mu.getFastProcEmail();
      String subject = "TOO " + obsid + ": " + TriggerTooConstants.FASTPROCMSG; 
      try {
        mu.mailFile(fromEmail,toEmail,null,subject, filename);
      } catch(Exception mailEx) {
        LogMessage.println("TOO Manager Mail::Caught exception : " + mailEx.getMessage());
        message = mailEx.getMessage();
      }
    }
  
    if (message.length() > 0) {
      throw new Exception(message);
    }
  }
  

}

