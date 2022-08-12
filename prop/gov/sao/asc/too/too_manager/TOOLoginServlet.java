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
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.*;
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
  * TOOLoginServlet verifies user name/password and sets the session 
  * for the TOO Manager.
  *
 */

public class TOOLoginServlet extends HttpServlet 
{
    private Properties triggerTooProperties;
    private String triggerTooDataPath;
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

      System.err.println("init: getProperties");
      triggerTooProperties = TriggerToo.getProperties(context );
      triggerTooDataPath = triggerTooProperties.getProperty("triggertoo.data.path") ;
      String myShowDebug = triggerTooProperties.getProperty("show.debug");
      showDebug = myShowDebug.equalsIgnoreCase("true");

      String triggerTooLogFile = triggerTooProperties.getProperty("toomanager.log.file");
      logMessage = new LogMessage(triggerTooLogFile,"660");

  }

   

  /****************************************************************************/
  /**
   * Handle a submission from a browser.  
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   * set session login parameters and then forwards to TOO manager display
   */

  public void service( HttpServletRequest request, HttpServletResponse response )
    throws ServletException, IOException {

    System.err.println("login servlet: getProperties");
    triggerTooProperties = TriggerToo.getProperties();
    triggerTooDataPath = triggerTooProperties.getProperty("triggertoo.data.path") ;
    String mgrMessage = triggerTooProperties.getProperty("mgr.message.file") ;
    if (mgrMessage == null) mgrMessage = "";
    String releaseDown = triggerTooProperties.getProperty("release.down.file");
    if (releaseDown == null) releaseDown = "";

    String userName = new String("");
    String userPwd = new String("");
    String operation = new String("");


    // Map<String, String> env = System.getenv();
    // for (String envName : env.keySet()) {
    //   logMessage.println(envName + " = " + env.get(envName));
    // }


    userName = Parameter.get(request,"userName");
    if (userName == null) userName="";
    userPwd = Parameter.get(request,"password");
    if (userPwd == null) userPwd="";
    operation = Parameter.get(request,"Submit");

    String mgrText = "";
    File releaseDownFile = new File(releaseDown);
    if (releaseDownFile.exists() && releaseDown.length() > 3 ) {
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
    } else {

      File mgrMessageFile = new File(mgrMessage);
      if (mgrMessage.length() > 3 && mgrMessageFile.exists() ) {
        try {
            FileReader fileR = new FileReader(mgrMessageFile);
            BufferedReader mgrFileBR = new BufferedReader(fileR);
            String inputLine;
            while( (inputLine = mgrFileBR.readLine()) != null) {
                mgrText += inputLine;
            }
            mgrFileBR.close();
            fileR.close();
         }
         catch (Exception exc) {
            LogMessage.printException(exc);
         }
      }


      HttpSession session = request.getSession(true);
      session.setAttribute("userName",userName);
      session.setAttribute("userPwd",userPwd);
      session.setAttribute("logMessage","");
      session.setAttribute("mgrMsg",mgrText);
      session.setMaxInactiveInterval(60 * 60 * 3);  // 3 hours

      RequestDispatcher dispatcher = null;
      try {
        DBTriggerToo dbconn = null;
        String testLevel = triggerTooProperties.getProperty("test.level");
        if (testLevel != null && testLevel.equals("1")) {
          logMessage.println("TOO/DDT Manager: TEST  for " +   triggerTooDataPath);
          dbconn = new DBTriggerToo(triggerTooDataPath,showDebug);
        }
        else {
          logMessage.println("TOO/DDT Manager: login  " + userName  +  " for " + triggerTooDataPath);
          dbconn = new DBTriggerToo(userName,userPwd,triggerTooDataPath,showDebug);
        }
        dbconn.isValidConnection();
        if (operation.indexOf("DDT") < 0) {
          session.setAttribute("entryLink","TOO");
          dispatcher = getServletContext().getRequestDispatcher("/tooManager.jsp");
        }
        else {
          session.setAttribute("entryLink","DDT");
          dispatcher = getServletContext().getRequestDispatcher("/ddtManager.jsp");
        }

      }
      catch (Exception exc) {
        logMessage.printException(exc);
        String message = new String(TriggerTooConstants.INVALID_USER);
        session.setAttribute("logMessage",message);
        dispatcher = getServletContext().getRequestDispatcher("/login.jsp");
      }
  
      dispatcher.forward(request, response);

    }
  }


}

