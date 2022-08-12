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
import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Vector;
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
import ascds.LogMessage;

/******************************************************************************/
/**
 * TOOManagerServlet handles retrieving the list of triggered TOOs from
 * the database. It also retrieves the current MP contact numbers .
 * This class handles displaying the available coordinators for a triggered
 * TOO and updates the database if the coordinator value has been changed.
 *
 */

public class TOOManagerServlet extends HttpServlet 
{

    private Properties triggerTooProperties;
    private String triggerTooDataPath;
    private boolean showDebug=false;
    private LogMessage logMessage;
    private String mpPagerFile;
    private String mpPOCFile;
    private String coordinatorFile;

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

      System.err.println("TOOManager init: getProperties");
      triggerTooProperties = TriggerToo.getProperties(context );
      String triggerTooLogFile = triggerTooProperties.getProperty("toomanager.log.file");
      logMessage = new LogMessage(triggerTooLogFile,"660");
      loadProperties();
      logMessage.println("TOO Manager initialization");

  }

  private void loadProperties() {
    triggerTooDataPath = triggerTooProperties.getProperty("triggertoo.data.path");
    mpPagerFile = triggerTooProperties.getProperty("mp.oncall.file");
    mpPOCFile = triggerTooProperties.getProperty("mp.poc.file");
    coordinatorFile = triggerTooProperties.getProperty("coordinator.file");
    String myShowDebug = triggerTooProperties.getProperty("show.debug");
    showDebug = myShowDebug.equalsIgnoreCase("true");
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

      String message = new String("");
      String msgClass = new String("msg");
      Vector<TriggerTooEntry> tooList = new Vector<TriggerTooEntry>();
      Integer triggerID = new Integer(0);
      Vector<String> aos= null; // default, but should never need it

      HttpSession session = request.getSession(true);
      String userName = (String)session.getAttribute("userName");
      String userPwd = (String)session.getAttribute("userPwd");
      logMessage.println("Login for " + userName);
      if (userName == null || userName.equals("") ||
          userPwd == null || userPwd.equals("") ) {

        message = new String(TriggerTooConstants.INVALID_USER);
        //Forward request to the jsp to display the resulting page
        RequestDispatcher dispatcher = null;
        session.setAttribute("logMessage",message);
        dispatcher = getServletContext().getRequestDispatcher("/login.jsp");
        dispatcher.forward(request, response);
      }
      else {
        // triggerTooProperties = TriggerToo.getProperties();
         loadProperties();

        //   update the record and redisplay the page 
        String sval = Parameter.get(request,"triggerID");
        LogMessage.println("trigger id: " + sval);
        if (sval != null) {
          sval = sval.trim();
          if (sval.length() > 0) {
            triggerID = new Integer(sval);
          }
        }
        String coordinator = Parameter.get(request,"hiddenCoordinator");

        String operation = Parameter.get(request,"operation");
        if (operation != null ) {
          logMessage.println("TOO Manager: " + operation);
        }

        String[] searchStr = (String[])request.getParameterValues("cycle");
        String sortStr = Parameter.get(request,"ordr");
        String entryLink = (String)session.getAttribute("entryLink");
        String ddtLink="return";

        String aoStr = "";
        if (operation != null && operation.equals("return")) {
          aoStr = (String)session.getAttribute("searchStr");
          sortStr = (String)session.getAttribute("sortStr");
        } else if (searchStr != null) {
           for (int ss=0;ss< searchStr.length ; ss++) {
              if (aoStr.length() > 0)
                aoStr += ",";
              else
                aoStr = "(";
              aoStr += "'" + searchStr[ss] + "'";
           }
           aoStr += ")";
        }
        logMessage.println("SEARCH: " + aoStr);
        logMessage.println("SORT: " + sortStr);


        try {
          DBTriggerToo dbconn = null;
          String testLevel = triggerTooProperties.getProperty("test.level");
          if (testLevel != null && testLevel.equals("1")) {
            dbconn = new DBTriggerToo(triggerTooDataPath,showDebug);
         }
         else {
           dbconn = new DBTriggerToo(userName,userPwd,triggerTooDataPath,showDebug);
         }
         if (triggerID.intValue() > 0 ) {
            logMessage.println("Coordinator:  " + coordinator);
           try {
             if (!coordinator.equalsIgnoreCase("nochange") ) {
               logMessage.println("Update coordinator: " + triggerID.toString() + " to " + coordinator);
               dbconn.updateCoordinator(triggerID,coordinator);
             }
           }
           catch (Exception exc) {
             message = exc.getMessage();
             msgClass = "error";
             logMessage.printException(exc);
           } 
         }
         try {
           aos = dbconn.getAOCycles(); 
           //set the default to the 2 most recent cycles
           if (aoStr==""  && triggerID.intValue() <= 0 && (operation == null || operation=="")) { 
              String tstr = triggerTooProperties.getProperty("ao_default");
              Integer aocnt = null;
              if (tstr != null) {
                 aocnt = new Integer(tstr);
              }
              if (aocnt == null || aocnt.intValue() <= 0) {
                aocnt = 2;
              }
              if (entryLink == "TOO") ddtLink="";
              for (int ii=0;ii<aos.size() && ii < aocnt.intValue();ii++) {
                if (aoStr.length() > 0)
                  aoStr += ",";
                else
                  aoStr = "(";
                aoStr += "'" + aos.get(ii) + "'";
              }
              if (!aoStr.equals("")) 
                aoStr += ")";
           }
           tooList = dbconn.getTriggerTooList(aoStr,sortStr);
         }
         catch (Exception exc) {
           message = exc.getMessage();
           msgClass = "error";
           logMessage.printException(exc);
         }
      
       }
       catch (Exception exc) {
         message += exc.getMessage();
         msgClass = "error";
         logMessage.printException(exc);
       }

       //Read most recent POC info for Mission Planning
       String pocStr = readPagerFile(mpPagerFile,mpPOCFile);

       // Read most current list of coordinators
       Vector<String> coordinatorList;
       coordinatorList = readCoordinators(coordinatorFile);

       //Forward request to the jsp to display the resulting page
       RequestDispatcher dispatcher = null;
       Integer cnt = new Integer(tooList.size());
       if (message.length() > 1) {
         message += "<br>";
       }
       message += "<i>" + cnt.toString() + " TOO observations retrieved.</i>";
       session.setAttribute("message",message);
       session.setAttribute("msgClass",msgClass);
       session.setAttribute("mpPOC",pocStr);
       session.setAttribute("coordinatorList",coordinatorList);
       session.setAttribute("tooList",tooList);
       session.setAttribute("searchStr",aoStr);
       session.setAttribute("maxAO",aos);
       session.setAttribute("ddtLink",ddtLink);
       session.setAttribute("sortStr",sortStr);
       dispatcher = getServletContext().getRequestDispatcher("/displayTOOManager.jsp");
       dispatcher.forward(request, response);
    }

  }


  private Vector<String> readCoordinators(String cFile)
  {
    BufferedReader coordinatorFileBF = null;
    String inputLine = null;
    Vector<String> coordinatorList = new Vector<String>();

    try {
      coordinatorFileBF = new BufferedReader(new FileReader(cFile));
      while( (inputLine = coordinatorFileBF.readLine()) != null) {
        if (!inputLine.startsWith("#")) {
          coordinatorList.add((String)inputLine.trim());
        }
      }
    } catch(Exception exc) {
      logMessage.printException(exc);
    } finally {
      try {
        coordinatorFileBF.close();
      }
      catch (Exception exc) {
        logMessage.printException(exc);
      }
    }
    
    return coordinatorList;
  }
  

  private String readPagerFile(String pagerFile,String pocFile)
  {
    BufferedReader pagerFileBF = null;
    BufferedReader pocFileBF = null;
    String inputLine = null;
    String[] inputArray;
    String pocStr = new String("");

    LogMessage.println ("reading " + pagerFile);

    try {
      pagerFileBF = new BufferedReader(new FileReader(pagerFile));
      while( (inputLine = pagerFileBF.readLine()) != null) {
        if (!inputLine.startsWith("#")) {
          inputArray = inputLine.split(" ",2);
          if (pocStr.indexOf(inputArray[1].trim()) < 0 ) {
            if (pocStr.length() > 1) {
              pocStr += "<br>";
            }
            pocStr += inputArray[1];
          }
        }
      }
      pocFileBF = new BufferedReader(new FileReader(pocFile));
      while( (inputLine = pocFileBF.readLine()) != null) {
        if (!inputLine.startsWith("#")) {
          if (pocStr.indexOf(inputLine.trim()) < 0 ) {
            if (pocStr.length() > 1) {
              pocStr += "<br>";
            }
            pocStr += "Current USINT POC: " + inputLine;
          }
        }
      }
    } catch(Exception exc) {
      logMessage.printException(exc);
    } finally {
      try {
        pagerFileBF.close();
        pocFileBF.close();
      }
      catch (Exception exc) {
        logMessage.printException(exc);
      }
    }
    
    return pocStr;
  }

}

