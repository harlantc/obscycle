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
// MemberListServlet

import java.io.*;
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
import info.User;
import info.Reports;
import info.ReportsConstants;
import db.DBConnection;
import ascds.LogMessage;
import ascds.FileUtils;

/******************************************************************************/
/**
 */

public class MemberListServlet extends HttpServlet 
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
   * servlet engine when it starts up.  Set the report properties if
   * they have not already been set.  This includes the reports data
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
   */

  public void service( HttpServletRequest request, HttpServletResponse response )
      throws ServletException, IOException {

      //Reload properties
      reportsProperties = Reports.getProperties();
      String timeout = reportsProperties.getProperty("reports.session.timeout");
      if(timeout != null) {
	  User.setTimeoutPeriod(Integer.parseInt(timeout));
      }
      
    // Get the session object.
    HttpSession session = request.getSession(false );
    String startPageURL = reportsProperties.getProperty("reports.start.url");
    RequestDispatcher dispatcher = null;
    int validUser = ReportsConstants.INVALIDENTRY;
    boolean caughtError = false;
    int userID = -1;
    String userType = null;
    User theUser = null;
    Vector memberList = null;

    LogMessage.println("------------ Entering MemberListServlet ------------ ");
    //Make sure the user has requested the page through the CDO site and 
    //is logged in
    String panelName = request.getParameter("panelName");
    String viewType = request.getParameter("viewType");
    String operation = request.getParameter("operation");
    LogMessage.println("MemberListServlet: operation=" + operation + "  type=" + viewType + " panel= " +  panelName);
    if (viewType != null) {
      LogMessage.println("MemberListServlet: operation=" + operation + "  type=" + viewType );
    }

    if(session != null) {
	Integer reportsID = ((Integer)session.getAttribute("reportsID"));
        if (reportsID != null) {
	  validUser = User.isValidUser(reportsID.intValue());
        }

	theUser = (User)session.getAttribute("user");
	if(theUser == null) {
	    LogMessage.println("Error: session exists, but no user object");
	    validUser = ReportsConstants.INVALIDENTRY;
	} 
	else if(!theUser.isAdmin() && !theUser.isDeveloper()){
	    LogMessage.println("Error: user is not admin, invalid entry point.");
	    validUser = ReportsConstants.INVALIDENTRY;
	} 
    }


    if(validUser != ReportsConstants.VALIDENTRY) {
	LogMessage.println("MemberListServlet: Not a valid user");
	
	if(session != null) {
	    session.invalidate();
	}
	
	//Send user back to the start page
	response.sendRedirect(startPageURL);

    } else {
      //Valid user has entered the page
      String reportsDataPath = reportsProperties.getProperty("reports.data.path");
      if (reportsDataPath == null) {
          LogMessage.println("reports path is null");
      }
      if (operation == null || !operation.equals("switchUser")) {

	//Look up username  and panel in proposal database to get type
        LogMessage.println("MemberListServlet: panel is " + panelName);
	
	boolean showDebug = true;
	try {
          DBConnection dbConnect = new DBConnection(reportsDataPath, showDebug);
          memberList =  dbConnect.loadPanelMembers(panelName);
          LogMessage.println("Admin: retrieved " + memberList.size() + "members");
	} catch (Exception e) {
	    caughtError = true;
            e.printStackTrace();
	    LogMessage.println("MemberListServlet:service routine - Caught exception for panel " + panelName);
	    LogMessage.println("Unable to initialize member list.");
	}


	//panel01.prelim_grades
	String gradesDirectory = new String(reportsDataPath + "/grades");
	
	//If we had an error getting the proposals on this panel, send the user
	//back to the start page.
	if(caughtError) {
	    //Send user back to the start page
	    response.sendRedirect(startPageURL);
	} else {
	    session.setAttribute("memberList", memberList);
	    session.setAttribute("user", theUser);
	    session.setAttribute("panelName", panelName);
	    session.setAttribute("viewType", viewType);


	    //Forward request to the jsp to display the resulting page
	    dispatcher = getServletContext().getRequestDispatcher("/displayMemberList.jsp");
	    dispatcher.forward(request, response);
	}
	
      } else {
        String memberID = request.getParameter("memberID");
        String memberType = request.getParameter("memberType");
        String basename= "." + theUser.getUserName() + memberID;
        String filename = reportsDataPath + "/rws/" + basename;
        String pline = "";

        if (memberID != null) {
	  LogMessage.println("MemberList: switch user to memberID " + memberID);
          try {
            PrintWriter out;
            if (memberType.startsWith("Pundit")) {
              pline = memberType + "\t" + memberID + "\t switchToPundit\n";
            } else {
              pline = memberType + "\t" + memberID + "\t \n";
            }
            out = new PrintWriter(new FileWriter(filename));
            out.println(pline);
            out.close();
            FileUtils.setPermissions(filename,"660");
	    String loginURL = "/login.jsp?file=" + basename;
            session.invalidate();
	    dispatcher = getServletContext().getRequestDispatcher(loginURL);
            dispatcher.forward(request, response);
          }
          catch (Exception exc) {
            LogMessage.println("MemberListServlet: switch user failed.");
            LogMessage.printException(exc);
	    response.sendRedirect(startPageURL);
          }
	} else {
	  //Send user back to the start page
	  response.sendRedirect(startPageURL);
        }
      } 
    }
  }

    

}

