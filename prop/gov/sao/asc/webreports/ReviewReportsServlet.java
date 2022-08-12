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
// ReviewReportsServlet

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
import info.User;
import info.ReviewReport;
import info.Reports;
import info.ReportsConstants;
import ascds.LogMessage;
import db.DBConnection;

/******************************************************************************/
/**
 */

public class ReviewReportsServlet extends HttpServlet 
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
   * they have not already been set.  This includes the proposal data
   * path.
   *
   * @param config The configuration object established by the servlet
   * engine.
   */

  public void init( ServletConfig config ) throws ServletException  {
      ServletContext context = config.getServletContext();
      super.init(config);

      reportsProperties = Reports.getProperties(context );
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

      LogMessage.println("---- Entering ReviewReportsServlet ----");

      //Reload properties
      reportsProperties = Reports.getProperties();
      String timeout = reportsProperties.getProperty("reports.session.timeout");
      String proposalFileDir  = reportsProperties.getProperty("proposal.file.dir");

      // Get the session object - Null means the user timed out or logged out
      HttpSession session = request.getSession( false );
      String startPageURL = reportsProperties.getProperty("reports.start.url");
      int validUser = ReportsConstants.INVALIDENTRY;

      if(session != null) {
	  Integer reportsIDInt = (Integer)session.getAttribute("reportsID");
	  int reportsID = -1;
	  if(reportsIDInt != null) {
	      reportsID = reportsIDInt.intValue();
	      validUser = User.isValidUser(reportsID);
	  }
 //DEBUG
/*
 Enumeration sesvar = session.getAttributeNames();
 while(sesvar.hasMoreElements()) {
            String sesname = (String)sesvar.nextElement();
            LogMessage.println("ReviewReportsServlet: " + sesname + " = " + session.getAttribute(sesname));
 }
*/

      }

      //Forward request to the jsp to display the resulting page
      RequestDispatcher dispatcher = null;
      if(validUser == ReportsConstants.VALIDENTRY) {
	  User theUser = (User)session.getAttribute("user");
	  int userID = theUser.getUserID();

	  //Determine if we're viewing the site before or during the Peer Review
	  boolean beforePR = true;
	  Boolean beforePRBool = (Boolean)session.getAttribute("beforePR");
	  if(beforePRBool != null) {
	      beforePR = beforePRBool.booleanValue();
	  }

	  String reportsDataPath = reportsProperties.getProperty("reports.data.path");
          String operation = request.getParameter("operation");
          String printType = request.getParameter("printType");

	  Vector<ReviewReport> reportsList = null;
          if (printType != null && 
              (printType.equals("Primary") || printType.equals("Secondary"))) {
             beforePR = true;
          }
	  reportsList = ReviewReport.getRRByUserID(userID, reportsDataPath, beforePR);


	  boolean allowedAccessNow = theUser.isAllowedAccessNow();

	  if(allowedAccessNow) {
	      // Set the session object list.
              session.setAttribute("proposalFileDir", proposalFileDir);
	      session.setAttribute("reportsList", reportsList);
	      if(theUser.isDeveloper()) {
		  timeout = new String("-1");
	      }
	      session.setAttribute("timeout", timeout);

	      //reset this session variable so we load the report from the file 
	      session.setAttribute("useSessionReport", new Boolean(false)); 
	      
              if (operation != null && operation.equals("Print")) {
                String theURL= "/printReports?printType=" + printType + "&reviewerID=" + userID;
                LogMessage.println("ReviewReportServlet: " + theURL);
	        dispatcher = getServletContext().getRequestDispatcher(theURL);
	        dispatcher.forward(request, response);
              }
              else {
                LogMessage.println("ReviewReportServlet: backLink for " + theUser.getUserName());
	        session.setAttribute("backLink", "reviewReports.jsp");
	        dispatcher = getServletContext().getRequestDispatcher("/displayReportsList.jsp");
	        dispatcher.forward(request, response);
              }
	  } else {
              LogMessage.println("ReviewReportServlet: panel is Unavailable for " + theUser.getUserName());
	      dispatcher = getServletContext().getRequestDispatcher("/panelUnavailable.jsp");
	      dispatcher.forward(request, response);
	  }
      } else {
	  LogMessage.println("ReviewReportsServlet: Not a valid user");

	  //send user back to the login page
	  response.sendRedirect(startPageURL);
      }

  }

}

