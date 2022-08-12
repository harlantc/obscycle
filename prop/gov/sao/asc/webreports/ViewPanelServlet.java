/*
  Copyrights:
 
  Copyright (c) 2000, 2022 Smithsonian Astrophysical Observatory
 
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
// ViewPanelServlet

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
import java.text.DateFormat;
import java.text.ParseException;
import info.User;
import ascds.LogMessage;
import info.Reports;
import info.ReviewReport;
import info.ReportsConstants;

/******************************************************************************/
/**
 */

public class ViewPanelServlet extends HttpServlet 
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
   * Handle a submission from a browser.  
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void service( HttpServletRequest request, HttpServletResponse response )
      throws ServletException, IOException {

      //Reload properties
      reportsProperties = Reports.getProperties();
      String timeout = reportsProperties.getProperty("reports.session.timeout");

      String reportsDataPath = reportsProperties.getProperty("reports.data.path");      
      String startPageURL = reportsProperties.getProperty("reports.start.url");      
      String panelName = request.getParameter("panelName");
      int userID = -1;
      Boolean beforePRBool = null;
      boolean beforePR = true;
      int validUser = ReportsConstants.INVALIDENTRY;
      User theUser = null;
      Vector reportsList = null;
      String userType = null;
      HttpSession session = request.getSession(false);

      LogMessage.println("---- Entering ViewPanelServlet ----");


      //Make sure the user is valid - Check if there is a session, 
      //and confirm that the user object exists. Otherwise, we can't 
      //process this request.
      if(session != null) {
	  Integer reportsIDInt = (Integer)session.getAttribute("reportsID");
	  if(reportsIDInt != null) {
	      int reportsID = reportsIDInt.intValue();
	      validUser = User.isValidUser(reportsID);

	      theUser = (User)session.getAttribute("user");
	      if(theUser == null) {
		  LogMessage.println("Error: session exists, but no user object");
		  validUser = ReportsConstants.INVALIDENTRY;
	      } else {
		  userType = theUser.getType();
	          if(userType != null && 
                     userType.equals(ReportsConstants.REVIEWER)) {
		    LogMessage.println("Error: Why is reviewer in ViewPanel?");
		    validUser = ReportsConstants.INVALIDENTRY;
                  }
	      }
	  }
      }

      String proposalFileDir  = reportsProperties.getProperty("proposal.file.dir");

      if(validUser == ReportsConstants.VALIDENTRY) {
	  beforePRBool = (Boolean)session.getAttribute("beforePR");
	  if(beforePRBool != null) 
	      beforePR = beforePRBool.booleanValue();
	  
	  try {

	      if(userType != null && theUser.inChairMode()) {
		  //Get list of reports on this panel from the database using
		  //the panelname in the user object
		  panelName = theUser.getPanelName();
	      } else if(userType != null && 
			(userType.equals(ReportsConstants.ADMIN) ||
			 userType.equals(ReportsConstants.DEVELOPER))) {
		  //The panel name for admins will be whichever panel is being viewed. So set the
		  //parameter in the user object, based on the panel name specified in the request.
		  theUser.setPanelName(panelName);
	      }  else {
                 panelName="";
              }
                 
	      
	      //Get the reports on the panel, and then set the session attribute
	      reportsList = ReviewReport.getRROnPanelByName(panelName, reportsDataPath, beforePR);
              session.setAttribute("reportsDataPath", reportsDataPath);
              session.setAttribute("reportsList", reportsList);
	      session.setAttribute("backLink", "/displayPanel.jsp");
              session.setAttribute("proposalFileDir", proposalFileDir);
	      if(theUser.isDeveloper()) {
		  timeout = new String("-1");
	      }
	      session.setAttribute("timeout", timeout);

	      //Forward request to the jsp to display the resulting page
	      RequestDispatcher dispatcher = null;
	      dispatcher = getServletContext().getRequestDispatcher("/displayPanel.jsp");
	      dispatcher.forward(request, response);
	  } catch(Exception ex) {
	      ex.printStackTrace();
	      LogMessage.println("Caught exception in ViewPanelServlet: " + ex.getMessage());
	  }

      } else {
	  LogMessage.println("ViewPanelServlet: Not a valid user");
	  response.sendRedirect(startPageURL);
      }
  }




}

