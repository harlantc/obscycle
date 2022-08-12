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
 * DDTManagerServlet handles retrieving the list of DDT Proposals from
 * the database. It also retrieves the current MP contact numbers .
 * This class handles displaying the available coordinators for a DDT
 * and updates the database if the coordinator value has been changed.
 *
 */

public class DDTManagerServlet extends HttpServlet 
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

      triggerTooProperties = TriggerToo.getProperties(context );
      String triggerTooLogFile = triggerTooProperties.getProperty("toomanager.log.file");
      logMessage = new LogMessage(triggerTooLogFile,"660");
      logMessage.println("TOO Manager initialization");
      loadProperties();
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
   * Handle a submission from a browser.  
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
      Vector<DDTEntry> ddtList = new Vector<DDTEntry>();
      Integer proposalID = new Integer(0);
      Integer maxAO=new Integer(13); // default, but should never need it
      String tooLink="return";
      Vector<String> aos = null;
      Vector<AOCycle> ao_totals = new Vector<AOCycle>();

      HttpSession session = request.getSession(true);
      String userName = (String)session.getAttribute("userName");
      String userPwd = (String)session.getAttribute("userPwd");
      String entryLink = (String)session.getAttribute("entryLink");
      
      logMessage.println("DDT Login for " + userName);
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
        // load properties variables
        loadProperties();

        //   update the record and redisplay the page 
        try {
          proposalID= Parameter.getInteger(request,"proposalID");
          if (proposalID == null) proposalID=0;
        }
        catch (Exception e) {
          proposalID=0;
        }
        String coordinator = Parameter.get(request,"hiddenCoordinator");

        String operation = Parameter.get(request,"operation");
        if (operation != null ) {
          logMessage.println("DDT Manager: " + operation);
        }

        String[] searchStr = (String[])request.getParameterValues("cycle");
        String[] searchstatStr = (String[])request.getParameterValues("searchstat");
        String sortStr = Parameter.get(request,"ordr");
        String statStr = "";
        String aoStr = "";
        String ddtcalTo = Parameter.get(request,"cal_to");
        String ddtcalFrom = Parameter.get(request,"cal_from");
        if (operation != null && operation.equals("return")) {
          aoStr = (String)session.getAttribute("ddtsearchStr");
          statStr = (String)session.getAttribute("ddtstatStr");
        } else {
          if (searchStr != null) {
            for (int ss=0;ss< searchStr.length ; ss++) {
              if (aoStr.length() > 0) 
                aoStr += ",";
              else 
                aoStr = "(";
              aoStr += "'" + searchStr[ss] + "'";
            }
            aoStr += ")";
          }
          if (searchstatStr != null) {
            for (int ss=0;ss< searchstatStr.length ; ss++) {
              if (statStr.length() > 0) 
                statStr += ",";
              else 
                statStr = "(";
              statStr += "'" + searchstatStr[ss] + "'";
            }
            statStr += ")";
          }
        }
        logMessage.println("SEARCH: " + aoStr);
        logMessage.println("SEARCH: " + statStr + " " + ddtcalFrom + " " + ddtcalTo);
        logMessage.println("SORT: " + sortStr);
           

        try {
          DBDDT dbconn = null;
          String testLevel = triggerTooProperties.getProperty("test.level");
          if (testLevel != null && testLevel.equals("1")) {
            dbconn = new DBDDT(triggerTooDataPath,showDebug);
          }
          else {
            dbconn = new DBDDT(userName,userPwd,triggerTooDataPath,showDebug);
          }
         if (proposalID.intValue() > 0 ) {
            logMessage.println("Coordinator:  " + coordinator);
           try {
             if (!coordinator.equalsIgnoreCase("nochange") ) {
               logMessage.println("Update coordinator: " + proposalID.toString() + " to " + coordinator);
               dbconn.updateCoordinator(proposalID,coordinator);
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
           if (aoStr == "" && proposalID.intValue() <= 0 && ( operation == null || operation=="")) { 
             if (entryLink == "DDT") tooLink = "";
             String tstr = triggerTooProperties.getProperty("ddt_ao_default");
             Integer aocnt = null;
             if (tstr != null) {
               aocnt = new Integer(tstr);
             }
             if (aocnt == null || aocnt.intValue() <= 0) {
               aocnt = 2;
             }

             for (int ii=0;ii<aos.size() && ii < aocnt;ii++) {
               if (aoStr.length() > 0)
                 aoStr += ",";
               else
                 aoStr = "(";
               aoStr += "'" + aos.get(ii) + "'";
             }
             if (aos.size() > 0) aoStr += ")";
           }

           ddtList = dbconn.getDDTList(aoStr,statStr,sortStr,ddtcalFrom,ddtcalTo);
           ao_totals = dbconn.getAOCycleTotals();
           logMessage.println("CYCLE TOTALS : " + ao_totals.size()); 
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
       Integer cnt = new Integer(ddtList.size());
       if (message.length() > 1) {
         message += "<br>";
       }
       message += cnt.toString() + " DDT observations retrieved.";
       session.setAttribute("ddtmessage",message);
       session.setAttribute("ddtmsgClass",msgClass);
       session.setAttribute("mpPOC",pocStr);
       session.setAttribute("coordinatorList",coordinatorList);
       session.setAttribute("tooLink",tooLink);
       session.setAttribute("ddtList",ddtList);
       session.setAttribute("ddtsearchStr",aoStr);
       session.setAttribute("ddtstatStr",statStr);
       session.setAttribute("ddtcalFrom",ddtcalFrom);
       session.setAttribute("ddtcalTo",ddtcalTo);
       session.setAttribute("ddtAO",aos);
       session.setAttribute("ddtAOTotals",ao_totals);
       dispatcher = getServletContext().getRequestDispatcher("/displayDDTManager.jsp");
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
          coordinatorList.add(inputLine.trim());
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
      File pfile = new File(pocFile);
      if (pfile.exists()) {
      pocFileBF = new BufferedReader(new FileReader(pocFile));
      while( (inputLine = pocFileBF.readLine()) != null) {
        if (!inputLine.startsWith("#")) {
          if (pocStr.indexOf(inputLine.trim()) < 0 ) {
            if (pocStr.length() > 1) {
              pocStr += "<br>";
            }
            pocStr += "USINT POC: " + inputLine;
          }
        }
      }
      }
    } catch(Exception exc) {
      logMessage.printException(exc);
    } finally {
      try {
        pagerFileBF.close();
        if (pocFileBF != null)
        pocFileBF.close();
      }
      catch (Exception exc) {
        logMessage.printException(exc);
      }
    }
    
    return pocStr;
  }

}

