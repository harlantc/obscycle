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
// LogoutServlet

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Date;
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
import javax.servlet.http.Cookie;
import java.text.DateFormat;
import java.text.ParseException;
import info.User;
import info.Reports;
import info.ReportsConstants;
import ascds.LogMessage;
import db.DBConnection;

/******************************************************************************/
/**
 */

public class LogoutServlet extends HttpServlet 
{
    private static final long serialVersionUID = 1;
    private Properties reportsProperties;

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
      
      //initializeValidationState( request );
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
      //initializeValidationState( request );
  }

  /****************************************************************************/
  /**
   * Handle the INIT operation.  This operation is invoked by the
   * servlet engine when it starts up.  Set the tookit properties if
   * they have not already been set.  This includes the reports data
   * path.
   *
   * @param config The configuration object established by the servlet
   * engine.
   */

  public void init( ServletConfig config ) throws ServletException  {
      ServletContext context = config.getServletContext();
      super.init(config);

      reportsProperties = Reports.getProperties(context);
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

      LogMessage.println("----------- Entering LogoutServlet ------------");
      HttpSession session = request.getSession(true);
      String startPageURL = reportsProperties.getProperty("reports.start.url");
      String userIDStr = (String)session.getAttribute("userID");
      int validUser = ReportsConstants.INVALIDENTRY;
      String operation = request.getParameter("operation");
      User theUser = (User)session.getAttribute("user");
      boolean userTimedOut = false;

      if(session != null) {
	  Integer reportsIDInt = (Integer)session.getAttribute("reportsID");
	  int reportsID = -1;
	  if(reportsIDInt != null) {
	      reportsID = reportsIDInt.intValue();
	      validUser = User.isValidUser(reportsID);
	  } 

	  Boolean timedOutBool = (Boolean)session.getAttribute("userTimedOut");
	  if(timedOutBool != null) {
	      userTimedOut = timedOutBool.booleanValue();
	  }

	  //Check if the operation is timed out.
	  if(operation != null && operation.equals(ReportsConstants.TIMEDOUTREP)) {
	      userTimedOut = true;
	  }
      }
      
      //For consistency, this servlet will check to make sure the user is valid before 
      //doing anything.  
      if(validUser == ReportsConstants.VALIDENTRY) {
	  session.setAttribute("validUser", new Boolean(false)); //so no other pages are accessible
	  
	  if(userIDStr == null) {
	      LogMessage.println("Error: No userID in LogoutServlet");
	      return;
	  } 
	  


	  //?? Remove any lock files
	  int userID = Integer.parseInt(userIDStr);
	  LogMessage.println("Logging out user: " + userID);
	  User.removeUser(userID); //removes user from list of current users
	  session.invalidate();

	  if(theUser.isReviewer() || theUser.inChairMode() || theUser.isPundit()) {
	      startPageURL = reportsProperties.getProperty("reviewer.home.url");;
	  } else if(theUser.isFacilitator() ) {
	      startPageURL = reportsProperties.getProperty("facilitator.home.url");;
	  } else if(theUser.isAdmin()) {
	      startPageURL = reportsProperties.getProperty("admin.home.url");;
	  }

	  if(userTimedOut) {
	      //Set the start page for the site, and the timeout value, so that
	      //the page explaining the timeout can display the correct timeout
	      //value, and so the user can relogin easily.
	      session = request.getSession(true);
	      session.setAttribute("startPageURL", startPageURL);
	      
	      String timeout = reportsProperties.getProperty("reports.session.timeout");
	      if(timeout != null) {
		  session.setAttribute("timeout", timeout);
	      }

	      RequestDispatcher dispatcher = null;
	      dispatcher = getServletContext().getRequestDispatcher("/displayTimedOut.jsp");
	      dispatcher.forward(request, response);

	  } else if(operation.equals(ReportsConstants.EXIT)) {
	      //Whether or not the user was valid, they'll get sent to the login page.
	      response.sendRedirect(startPageURL); 
	  }
      } else {
	  // if(validUser == ReportsConstants.VALIDENTRY) {
	  //Whether or not the user was valid, they'll get sent to the login page.
	  response.sendRedirect(startPageURL); 

      } 
  }




}

