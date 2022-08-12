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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
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
import ascds.LogMessage;
import captcha.*;

/******************************************************************************/
/**
  * TOOSearchServlet  class handles actions from the search/query page.
  * It validates the query parameters, executes the query. If successful,
  * displays a list of TOO observations and their followups that match
  * the query parameter.
 */

public class TOOSearchServlet extends HttpServlet 
{
    private static final long serialVersionUID = 1;
    private Properties triggerTooProperties;
    private String triggerTooDataPath;
    private String triggerTooLogFile;
    private boolean showDebug=false;
    private LogMessage logMessage;
    private Integer maxDisplay;
    private String releaseDown;
    private String cdoMessage;
    private boolean usecaptcha;

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
      triggerTooLogFile = triggerTooProperties.getProperty("triggertoo.log.file");
      maxDisplay = new Integer(triggerTooProperties.getProperty("triggertoo.max.display"));
      logMessage = new LogMessage(triggerTooLogFile,"0660");
      usecaptcha = UseRecaptcha.doRecaptcha(triggerTooProperties);

      String myShowDebug = triggerTooProperties.getProperty("show.debug");
      showDebug = myShowDebug.equalsIgnoreCase("true");
      releaseDown = triggerTooProperties.getProperty("release.down.file");
      if (releaseDown == null) releaseDown = "";
      cdoMessage = triggerTooProperties.getProperty("cdo.message.file");
      if (cdoMessage == null) cdoMessage = "";


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

  public void service( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException {


    String message = new String(" ");
    String searchValue = new String("");
    String searchType = new String("");
    String remoteIP = new String("");
    RequestDispatcher dispatcher = null;

    // reload for recaptcha
    triggerTooProperties = TriggerToo.getProperties();
    usecaptcha = UseRecaptcha.doRecaptcha(triggerTooProperties);

    ObservationList observationList = new ObservationList();
    MailUtility mu = new MailUtility(triggerTooDataPath,triggerTooProperties);
    String gresponse = request.getParameter("g-recaptcha-response");
    HttpSession session = request.getSession(true);
    session.setAttribute("usecaptcha",(Boolean)usecaptcha);
    String userAgent = "";
    try {
      remoteIP = ClientIPAddress.getFrom(request,false,null); 
      userAgent = request.getHeader("User-Agent");
    } catch (Exception e) {
    }

    File releaseDownFile = new File(releaseDown);
    if (releaseDown.length() > 3 && releaseDownFile.exists() ) {
        String relMsg = "";
        try {
          FileReader fileR = new FileReader(releaseDownFile);
          BufferedReader releaseFileBR = new BufferedReader(fileR);
          String inputLine;
          while( (inputLine = releaseFileBR.readLine()) != null) {
                relMsg += inputLine;
          }
          releaseFileBR.close();
          fileR.close();
        }
        catch (Exception exc) {
          relMsg = "Down for maintenance.  Please try again later.";
          LogMessage.printException(exc);
        }

        response.sendError(response.SC_SERVICE_UNAVAILABLE,relMsg);
        return;
    }
    try {
      File cdoMessageFile = new File(cdoMessage);
      String cdoText = "";
      if (cdoMessage.length() > 3 && cdoMessageFile.exists() ) {
        FileReader fileR = new FileReader(cdoMessageFile);
        BufferedReader cdoFileBR = new BufferedReader(fileR);
        String inputLine;
        while( (inputLine = cdoFileBR.readLine()) != null) {
          cdoText += inputLine;
        }
        cdoFileBR.close();
        fileR.close();
        session.setAttribute("cdoText",cdoText);
      }
    } catch (Exception e) {
      LogMessage.printException(e);
    }
        

    //LogMessage.println("RECAPTCHA: " + gresponse);

    // count input params
    int searchcnt=0;
    for ( Enumeration parameters = request.getParameterNames();
            parameters.hasMoreElements(); )
    {
      String parameterName = (String) parameters.nextElement();
      String parameterValue = request.getParameter( parameterName );
      if (parameterValue != null && parameterValue.length() > 0 && showDebug)
        LogMessage.println(remoteIP + ": " + userAgent + ": " + parameterName + "=" + parameterValue);
      if (parameterName.indexOf("g-recaptcha-response")< 0 && 
          !parameterName.equals("errmsg") &&
          parameterValue != null && parameterValue.length() > 100) {
         // assume bad guy
         LogMessage.println(remoteIP + ": Parameter is too long " + parameterName);
         searchcnt=99;
      }
      if (parameterValue != null && parameterValue.length() > 0 &&
          (parameterName.indexOf("obsid") >= 0 ||
           parameterName.indexOf("seqnbr") >= 0 ||
           parameterName.indexOf("propnum") >= 0 ||
           parameterName.indexOf("pilast") >= 0 )) {
         searchcnt++;
      }
    }
  
    if (searchcnt > 0) {
      LogMessage.println( remoteIP + ": usecaptcha= " + usecaptcha + "  searchcnt=" + searchcnt);

      if (usecaptcha) {
        if (gresponse == null || gresponse.equals("")) {
          logMessage.println("NO RECAPTCHA --- " + remoteIP + ": " + userAgent);
          dispatcher = getServletContext().getRequestDispatcher("/displayTOOSearch.jsp");
          dispatcher.forward(request, response);
          return;
        }
        VerifyRecaptcha verifyRecaptcha = new VerifyRecaptcha();
        try {
          if (!verifyRecaptcha.verify(triggerTooProperties,gresponse,remoteIP)) {
            logMessage.println("BAD RECAPTCHA --- " + remoteIP + ": " + userAgent);
            dispatcher = getServletContext().getRequestDispatcher("/displayTOOSearch.jsp");
            dispatcher.forward(request, response);
            return;
          }
        }
        catch (Exception exc) {
          if (remoteIP == null) remoteIP="unknown";
          logMessage.println("failed to verify " + remoteIP);
          logMessage.printException(exc);
          dispatcher = getServletContext().getRequestDispatcher("/displayTOOSearch.jsp");
          dispatcher.forward(request, response);
          return;
        }
      }
    }
    
    if (searchcnt == 1) {
      try {

        DBObservation dbconn = null;
        searchValue = Parameter.get(request,"obsid");
        
        try {
          if (searchValue != null && searchValue.length() > 0) {
            Integer obsid = new Integer(-1);
            try {
              obsid = new Integer(searchValue);
            }
            catch (Exception exc) {
              // don't fill up log cause someone is probably hacking
              //logMessage.printException(exc);
              obsid = -1;
            }
            if (obsid.intValue() > 0) {
              dbconn= new DBObservation(triggerTooDataPath,showDebug);
              logMessage.println("Search by Obsid: " + obsid.toString());
              observationList = dbconn.getObservationListbyObsid(obsid);
            } else  {
              logMessage.println("invalid obsid" + searchValue + "  obsid="  + obsid);
            }
          }
          else {
            searchValue = Parameter.get(request,"seqnbr");
            if (searchValue == null || searchValue.length() <= 1) { 
              searchValue = Parameter.get(request,"propnum");
              if (searchValue == null || searchValue.length() <= 1) { 
                searchValue = Parameter.getEncoded(request,"pilast");
                if (searchValue == null || searchValue.length() <= 1) { 
                   searchValue = null;
                }
                else {
                  dbconn= new DBObservation(triggerTooDataPath,showDebug);
                  searchValue = searchValue.trim();
                  if (searchValue.length() < 60) {
                    observationList = dbconn.getObservationListbyPI("%" + searchValue + "%");
                    logMessage.println("Search by PI: " + searchValue);
                  } else {
                    logMessage.println("invalid PI name: " + searchValue);
                  }
                }
              }
              else {
                searchValue = searchValue.trim();
                if (!searchValue.matches("[0-9 %]{1,10}")) {
                  logMessage.println("invalid propnum: " + searchValue);
                } else {
                  dbconn= new DBObservation(triggerTooDataPath,showDebug);
                  observationList = dbconn.getObservationListbyProposalNumber("%" + searchValue + "%");
                  logMessage.println("Search by ProposalNbr: " + searchValue);
                }
              }
            }
            else {
              searchValue = searchValue.trim();
              if (!searchValue.matches("[0-9 %]{1,10}")) {
                logMessage.println("invalid seqnbr" + searchValue);
              } else {
                dbconn= new DBObservation(triggerTooDataPath,showDebug);
                observationList = dbconn.getObservationListbySequenceNumber("%" + searchValue + "%");
                logMessage.println("Search by SeqNbr: " + searchValue);
              }
            }
          }
        }
        catch (Exception exc) {
            //logMessage.printException(exc);
            message = "Error occurred retrieving data.";
            logMessage.println("TOOSearchEXC:" +  exc.getMessage());
            logMessage.println("searchValue:" +  searchValue);
            try {
              mu.mailErrorMessage(exc.toString());
            }
            catch (Exception ex) {
              logMessage.printException(ex);
              logMessage.println(exc.toString());
            }
          }
      }
      catch (Exception exc) {
         //exc.printStackTrace();
         message = "Error occurred retrieving data.";
         logMessage.println("TOOSearchEXC2:" +  exc.getMessage());
         try {
           mu.mailErrorMessage(exc.toString());
         }
         catch (Exception ex) {
           logMessage.printException(ex);
           logMessage.println(exc.toString());
         }
       }
     } else {
        // initial load
     } 

      //Forward request to the jsp to display the resulting page
      if (observationList != null && observationList.size() > 0) {
        message = observationList.size() + " observations retrieved.";
        logMessage.println(message);
      }
      else if (searchValue != null && searchcnt != 0 && message.length() < 2) {
        message = "No observations found for current search criteria.";
      }
      else {
        // got nothing
      }
        
      if (message != null) {
        message = message.replaceAll("\"","'");
      }
      session.setAttribute("message",message);
      session.setAttribute("searchpage","okbytrigger");

      if (observationList.size() <= 0 ) {
        dispatcher = getServletContext().getRequestDispatcher("/displayTOOSearch.jsp");
      }
      else {
        // eventually change to jsp to display data
        session.setAttribute("obsList",observationList);
        session.setAttribute("maxDisplay",maxDisplay);
        dispatcher = getServletContext().getRequestDispatcher("/displayTOOList.jsp");
      }
      dispatcher.forward(request, response);

  }


}

