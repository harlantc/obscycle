/*
  Copyrights:
 
  Copyright (c) 2000 Smithsonian Astrophysical Observatory
 
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
// TOOObscatUpdate

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
 * TOOObscatUpdate handles the submit functions for the select triggered
 * TOO.  Functions available are to save data to the database, send a
 * TOO status message , perform the NameResolver action in NED/Simbad to
 * find coordinates, or 'Cancel' and return to the full list of Triggered TOOs.
 *
 */
public class TOOObscatUpdate extends HttpServlet 
{
    private Properties triggerTooProperties;
    private String triggerTooDataPath;
    private String tooReceiverPath;
    private String observationDetailLink;
    private NameResolver nameResolver;
    private String cxcHelpDesk;
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
      // initialize log file
      String triggerTooLogFile = triggerTooProperties.getProperty("toomanager.log.file");
      logMessage = new LogMessage(triggerTooLogFile,"660");


      loadProperties();
  }
  private void loadProperties() {
   
      // data path for files
      triggerTooDataPath = triggerTooProperties.getProperty("triggertoo.data.path");
      // helpdesk
      cxcHelpDesk = triggerTooProperties.getProperty("cxc.helpdesk");

      // name resolover for NED/Simbad
      nameResolver = new NameResolver(triggerTooProperties,"\n");

      // link to detail observation information
      observationDetailLink = triggerTooProperties.getProperty("observation.detail.link");
      
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
    String operation;
    Integer triggerID = new Integer(0);
    Integer obsid = new Integer(0);
    RequestDispatcher dispatcher = null;
    String str;
    String message="";
    Boolean isManager = false;
    Boolean isReadOnly = false;
    Boolean isValidParams = false;
    TOOInfo tooinfo = new TOOInfo();
    tooinfo.msgclass="";


    HttpSession session = request.getSession(true);
    session.setAttribute(TriggerTooConstants.OBSDETAIL,observationDetailLink);
    session.setAttribute("RALabelBG","field");
    session.setAttribute("DecLabelBG","field");

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
      dispatcher = getServletContext().getRequestDispatcher("/displayObsCatUpdates.jsp");

      tooEntry = (TriggerTooEntry) session.getAttribute("tooEntry");
      obsList = (ObservationList) session.getAttribute("obsList");
      operation = Parameter.get(request,"Submit");
      logMessage.println("***TOOObscatUpdate: operation=" + operation + " obsid= " + tooEntry.getObsid());
      if (operation != null && operation.equals(TriggerTooConstants.CANCEL)) {
          message = "Obscat Update Canceled."; 
          // needed in case of nameresolver function
          String origra = (String)session.getAttribute("origRA");
          String origdec = (String)session.getAttribute("origDec");
          String origtname = (String)session.getAttribute("origTgtName");
          if (origra == null) origra="";
          if (origdec == null) origdec="";
          if (origtname == null) origtname="";

          tooEntry.setOverrideCoords(origra,origdec);
          tooEntry.setOverrideTargetName(origtname);
          session.setAttribute("message",message);
          session.setAttribute("tooEntry",tooEntry);
          dispatcher = getServletContext().getRequestDispatcher("/displayTOOUpdate.jsp");
          dispatcher.forward(request, response);
      } else {

      // Save off the input parameters
      isValidParams = processParameters(request,operation,tooEntry,obsList,tooinfo);
      if (tooinfo.updateMessage != null && tooinfo.updateMessage.length() > 0) {
        message = tooinfo.updateMessage;
      }

      if (operation.equals(TriggerTooConstants.NAMERESOLVER)) {
        session.setAttribute("coordMessage",tooinfo.coordMessage);
      }
      else  if (operation.equals(TriggerTooConstants.APPLYOBSCAT)) {
        try {
          DBTriggerToo dbconn = null;
          DBObservation dboconn = null;

          // create database connections
          String testLevel = triggerTooProperties.getProperty("test.level");
          if (testLevel != null && testLevel.equals("1")) {
            dbconn = new DBTriggerToo(triggerTooDataPath,showDebug);
            dboconn = new DBObservation(triggerTooDataPath,showDebug);
          }
          else {
            dbconn = new DBTriggerToo(userName,userPwd,triggerTooDataPath,showDebug);
            dboconn = new DBObservation(userName,userPwd,triggerTooDataPath,showDebug);
          }
       
          if (!isValidParams) {
            if (!tooEntry.isValidCoords()) {
             session.setAttribute("RALabelBG",TriggerTooConstants.ERRORCLASS);
             session.setAttribute("DecLabelBG",TriggerTooConstants.ERRORCLASS);
            }
            tooinfo.msgclass = "error";
            session.setAttribute("message",message);
            session.setAttribute("msgClass",tooinfo.msgclass);
            session.setAttribute("tooEntry",tooEntry);
            session.setAttribute("obsList",obsList);
          }
          else {
            try {
	      String newTargetName = tooEntry.getModifiedTargetName();
              Coordinate newCoords = tooEntry.getModifiedCoords();

              obsid = tooEntry.getObsid();
              performObsCatUpdate(request,dboconn,dbconn,obsid,tooEntry,
                  tooEntry.getVersion(), newTargetName,newCoords,obsList);
              obsList = dboconn.getAllObservationListbyObsid(obsid);
              dispatcher = getServletContext().getRequestDispatcher("/displayTOOUpdate.jsp");
            }
            catch (Exception exc) {
              message += exc.getMessage();
              tooinfo.msgclass = "error";
            }
          }
        }
        catch (Exception exc) {
           message += "Unable to connect to database.\n";
           message += exc.getMessage();
           tooinfo.msgclass = "error";
        }
        logMessage.println(message);
      }

      //Forward request to the jsp to display the resulting page
      if (message == null) {
         message = "";

      }
      session.setAttribute("message",message);
      session.setAttribute("coordMessage",tooinfo.coordMessage);
      session.setAttribute("msgclass",tooinfo.msgclass);
      session.setAttribute("obsList",obsList);
      session.setAttribute("tooEntry",tooEntry);
      session.setAttribute(TriggerTooConstants.RESOLVERLIST,tooinfo.resolverSelector);
      dispatcher.forward(request, response);
    }
    }

  }

  // ---------------------------------------------------
  // Get the user input
  // ---------------------------------------------------
  private boolean processParameters( HttpServletRequest request,
	String operation, TriggerTooEntry tooEntry,
	ObservationList obsList,TOOInfo tooinfo)
  {
     Double appTime = null;
     Double cxcStart = null;
     Double cxcStop = null;
     Double preMin = null;
     Double preMax = null;
     String fldLabel;
     String stmp,str;
     boolean isValidParams = true;

     tooinfo.resolverSelector = Parameter.get(request,TriggerTooConstants.RESOLVERLIST);
     tooinfo.updateMessage = "";
  
     if (operation.equals(TriggerTooConstants.NAMERESOLVER)) {
        tooinfo.coordMessage = nameResolver(request,tooinfo.resolverSelector,tooEntry);
     }
     else {
       String raStr = Parameter.get(request,TriggerTooConstants.RA);
       String decStr = Parameter.get(request,TriggerTooConstants.DEC);
       tooEntry.setOverrideCoords(raStr,decStr);
       isValidParams =tooEntry.isValidCoords();
     }

     str = Parameter.get(request,TriggerTooConstants.TARGETNAME);
     tooEntry.setOverrideTargetName(str);

     for (int ii=0;ii<obsList.size(); ii++) {
         Observation obs = obsList.get(ii);
         String baseLabel = obs.getObsid().toString();

         fldLabel =  "apptime" + baseLabel;
         stmp = Parameter.get(request,fldLabel);
         if (stmp != null ) {
           appTime = getDouble( stmp);
           if (appTime !=  null && appTime >= 1.0) {
             obs.setApprovedExpTime(appTime);
           } else {
             isValidParams = false;
             tooinfo.updateMessage = "Approved Exposure Time is invalid: " + stmp +  " for Obsid " + baseLabel + "\n";
           }
         }

         if (ii==0) {
           fldLabel =  "cxcstart" ;
           stmp = Parameter.get(request,fldLabel);
           if (stmp != null ) {
             cxcStart = getDouble( stmp);
             if (cxcStart !=  null && cxcStart > 0.0) {
               obs.setResponseStart(cxcStart);
             } else {
               isValidParams = false;
               tooinfo.updateMessage = "CXC Start is invalid: " + stmp +  " for Obsid " + baseLabel + "\n";
             }
           }
           fldLabel =  "cxcstop" ;
           stmp = Parameter.get(request,fldLabel);
           if (stmp != null ) {
             cxcStop = getDouble( stmp);
             if (cxcStop !=  null && cxcStop > 0.0) {
               obs.setResponseStop(cxcStop);
             } else {
               isValidParams = false;
               tooinfo.updateMessage = "CXC Stop is invalid: " + stmp +  " for Obsid " + baseLabel + "\n";
             }
           }
         }
        

         if (ii != 0) {
           fldLabel =  "min" + baseLabel;
           stmp = Parameter.get(request,fldLabel);
           if (stmp != null ) {
             preMin = getDouble( stmp);
             if (preMin !=  null && preMin.doubleValue() >= 0.0) {
               obs.setPreMinLead(preMin);
             } else {
               isValidParams = false;
               tooinfo.updateMessage += "Min Lead is invalid: " + stmp + " for Obsid " + baseLabel + "\n";
             }
           }
  
           fldLabel =  "max" + baseLabel;
           stmp = Parameter.get(request,fldLabel);
           if (stmp != null ) {
             preMax = getDouble( stmp);
             if (preMax !=  null && preMax.doubleValue() >= 0.0) {
               obs.setPreMaxLead(preMax);
             } else {
               isValidParams = false;
               tooinfo.updateMessage += "Max Lead is invalid: " + stmp +" for Obsid " + baseLabel +  "\n";
             }
           }
           if (preMin != null  && 
               preMax != null  && preMin.doubleValue() > preMax.doubleValue()) {
              isValidParams = false;
              tooinfo.updateMessage += "Min Lead Time is greater than Max Lead Time for Obsid " + baseLabel + "\n";
           }
         }

     }
     return isValidParams;         
  }

  // ---------------------------------------------------
  // process input param
  // ---------------------------------------------------
  private Double getDouble (String fldStr)
  {
    Double dval = null; 

    fldStr =  fldStr.trim();
    try {
      dval = new Double(fldStr);
    } catch (Exception exc) {
      // don't set dval cause it's bad
    }

    return dval;
  }

  
  // ---------------------------------------------------
  // Update obscat if needed for the target name and/or
  // the coordinates.
  // ---------------------------------------------------
  private void performObsCatUpdate( HttpServletRequest request,
	DBObservation dboconn,DBTriggerToo dbconn, 
	Integer obsid,TriggerTooEntry tooEntry,
	Integer version,
	String targetName,Coordinate coords,ObservationList obsList)
	throws Exception
  {

     String str;
     String cmd;
     String message = new String("");
     Observation obs = null;
     String updateCoords;
     String filename = new String("");
     boolean newFile = false;
     boolean didUpdate = false;
     int ii = 1;
     Integer iversion = new Integer(ii);
     File outputFile = null;
     String proposalNumber = "";

    // build the update message
    while (!newFile) {
      try {
        iversion = new Integer(ii);
        filename = triggerTooDataPath + "/obscat_update/";
        filename += obsid.toString();
        filename += "_v" + version.toString();
        filename += "_" + iversion.toString();
        filename += ".obscat.update";
        
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
        message += "Unable to create file for TOO Obscat Update request.";
        message += " Please notify " + cxcHelpDesk;
        throw new IOException(message);

      }
    }

    try {
      FileWriter outputFW = new FileWriter(filename.toString());
      PrintWriter outputPW = new PrintWriter(outputFW);
      outputPW.println("The following obscat updates have been applied for a triggered TOO.\nIf needed, please use the Sequence Editor to verify/update approval times for all observations of the proposal.\n");

      for (ii=0; ii<obsList.size(); ii++) {
        boolean updatedThisEntry = false;
        obs = (Observation)obsList.get(ii);

        proposalNumber = obs.getProposalNumber();
        str = "obsid" + obs.getObsid().toString();
        outputPW.print("Sequence Nbr: " + obs.getSequenceNumber()) ;
        outputPW.print("  ObsId: " + obs.getObsid().toString());
        outputPW.println("  Proposal Number: " + proposalNumber);
        updateCoords  = Parameter.get(request,str);
        if (updateCoords != null) {
          try {
            if (coords != null)  {
              if (obs.getCoords() == null || !coords.equals(obs.getCoords())) {
                logMessage.println("Modify ObsCat coords for: " + obs.getObsid().toString());
                dboconn.updateCoordinates(obs.getObsid(),coords);
                outputPW.println("Old Coordinate: " + obs.getRAString() + "  " + obs.getDecString());
                outputPW.println("New Coordinate: " + coords.getSexagesimalLon() + "  " + coords.getSexagesimalLat() );

                didUpdate = true;
                updatedThisEntry = true;

              }
              else {
                 outputPW.println("Coordinates already applied for this observation");
              }
            }
            if (targetName != null)  {
              logMessage.println("Modify ObsCat target name for: " + obs.getObsid().toString() + " to " + targetName);
              if (!targetName.equals(obs.getTargetName())) {
                dboconn.updateTargetName(obs.getObsid(),targetName);
                outputPW.println("Old Target Name: " + obs.getTargetName() );
                outputPW.println("New Target Name: " + targetName);
                didUpdate = true;
                updatedThisEntry = true;
              }
              else {
                 outputPW.println("Target Name already applied for this observation");
              }
            }
            // update the tootrigger record  for the 1st entry 
            if (ii==0 && updatedThisEntry==true) {
              try {
                dbconn.updateOverrideValues(tooEntry.getTriggerID(),
                         tooEntry.getOverrideTargetName(),
                         tooEntry.getOverrideRA(),tooEntry.getOverrideDec());
              }
              catch (Exception exc) {
                message += "Obsid " + tooEntry.getObsid() + ": ";
                message += exc.getMessage();
                throw new SQLException(message);
              }
            }

          }
          catch  (Exception exc) {
            message += "Unable to update target coordinate values for ";
            message += obs.getObsid() + "\n";
            message += exc.getMessage();

          }
        }  // end change approved for this obsid
        else {
          outputPW.println("Coordinate changes not applied to this observation.");
        }

        try {
          String umsg = "";
          if (obs.getApprovedExpTime().doubleValue() != obs.getOrigApprovedExpTime().doubleValue() ) {
            logMessage.println("Modify approved time for: " + obs.getObsid().toString() + " to " + obs.getApprovedExpTime().toString() + " from " + obs.getOrigApprovedExpTime().doubleValue() );
            umsg += "Old Approved Time: " + obs.getOrigApprovedExpTime() + "\n";
            umsg += "New Approved Time: " + obs.getApprovedExpTime() + "\n" ;
            umsg += "Please use the Sequence Editor to validate approved and assigned times: http://cda.harvard.edu/seqedit\n";
          }

          if (obs.getPreMinLead().doubleValue() != obs.getOrigPreMinLead().doubleValue() ) {
            logMessage.println("Modify min lead time for: " + obs.getObsid().toString());
            umsg += "Old Pre Min Lead: " + obs.getOrigPreMinLead() + "\n";
            umsg += "New Pre Min Lead: " + obs.getPreMinLead() + "\n";
          }
          if (obs.getPreMaxLead().doubleValue() != obs.getOrigPreMaxLead().doubleValue() ) {
            logMessage.println("Modify max lead time for: " + obs.getObsid().toString());
            umsg += "Old Pre Max Lead: " + obs.getOrigPreMaxLead() + "\n";
            umsg += "New Pre Max Lead: " + obs.getPreMaxLead() + "\n";
          }
          if (umsg.length() > 4) {
            dboconn.updateTOOTarget(obs.getObsid(),
                obs.getApprovedExpTime().doubleValue(),
		obs.getPreMinLead().doubleValue(),
                obs.getPreMaxLead().doubleValue());
            didUpdate = true;
            outputPW.println(umsg);
            umsg="";
          }
          if (ii==0 && 
             (obs.getResponseStart().doubleValue() != obs.getOrigResponseStart().doubleValue()  ||
              obs.getResponseStop().doubleValue() != obs.getOrigResponseStop().doubleValue()  )) {
            logMessage.println("Modify cxc start time for: " + obs.getObsid().toString() + " to " + obs.getResponseStart().toString() + " from " + obs.getOrigResponseStart().doubleValue() );
            logMessage.println("Modify cxc stop time for: " + obs.getObsid().toString() + " to " + obs.getResponseStop().toString() + " from " + obs.getOrigResponseStop().doubleValue() );
            umsg += "Old CXC Start/Stop: " + obs.getOrigResponseStart() +"/" + obs.getOrigResponseStop() + "\n";
            umsg += "New CXC Start/Stop: " + obs.getResponseStart() +"/" + obs.getResponseStop() + "\n";
            dboconn.updateTOOStartStop(obs.getObsid(),
		obs.getResponseStart(),obs.getResponseStop());
            didUpdate=true;
            outputPW.println(umsg);
          }

        }
        catch (Exception exc) {
            message += "Unable to update target values for ";
            message += obs.getObsid() + "\n";
            message += exc.getMessage();
        }

        outputPW.println("");
      }
      outputPW.close();
      outputFW.close();
      FileUtils.setPermissions(outputFile,"440");
    }
    catch (Exception exc) {
      logMessage.printException(exc);
      message += exc.getMessage();
    }


    if (didUpdate) {
      MailUtility mu = new MailUtility(triggerTooDataPath,triggerTooProperties); 
      String fromEmail = mu.getFromEmail();
      String toEmail = mu.getUpdateEmail();
      String subject = proposalNumber + "- " + TriggerTooConstants.SUBJECT_UPDATE;
      try {
        mu.mailFile(fromEmail,toEmail,null,subject, filename);
      } catch(Exception mailEx) {
        LogMessage.println("TOO Manager Mail::Caught exception : " + mailEx.getMessage());
        throw new Exception (mailEx.getMessage());
      }
      LogMessage.println("Sent Obscat Update message to " + toEmail);

    }

    if (message.length() > 0) {
      throw new Exception(message);
    }
  }

  // ---------------------------------------------------
  // execute the NameResolver function for Ned,Simbad and
  // fill in the coordinates
  // ---------------------------------------------------
  private String nameResolver( HttpServletRequest request,
	String inResolverSelector,TriggerTooEntry tooEntry)

  {
    // Validate the input parameters.
    String targetname = Parameter.get(request,TriggerTooConstants.TARGETNAME);
    String theCoords = new String("");
    String[] resolverList = inResolverSelector.split("/");

    logMessage.println ("attempting to resolve " + targetname + " using " + inResolverSelector);

 
    try {
       theCoords = nameResolver.resolve(targetname,resolverList);
       if (theCoords == null) {
         String theRA = nameResolver.getRA();
         String theDec = nameResolver.getDec();
         tooEntry.setOverrideCoords(theRA,theDec);
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

}

