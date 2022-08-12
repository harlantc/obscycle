/*
  Copyrights:
 
  Copyright (c) 2000-2016 Smithsonian Astrophysical Observatory
 
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
// TechListServlet

import java.io.IOException;
import java.util.Properties;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ascds.LogMessage;
import info.*;
import db.DBConnection;

/******************************************************************************/
/**
 */

public class TechListServlet extends HttpServlet 
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

      LogMessage.println("----------- Entering TechListServlet ------------");      
      //Reload properties
      reportsProperties = Reports.getProperties();
      String timeout = reportsProperties.getProperty("reports.session.timeout");

    // Get the session object.
    HttpSession session = request.getSession(false );
    String startPageURL = reportsProperties.getProperty("reports.start.url");
    String proposalFileDir  = reportsProperties.getProperty("proposal.file.dir");

    RequestDispatcher dispatcher = null;
    int validUser = ReportsConstants.INVALIDENTRY;
    boolean caughtError = false;
    String reviewerPanel = request.getParameter("panelName");
    String reviewerName = new String("");
    String lpreport = request.getParameter("type");

    String accessDateFile  = reportsProperties.getProperty("reports.access.date.file");


    int reportsID = -1;
    if(session != null) {
	Integer reportsIDInt = (Integer)session.getAttribute("reportsID");
	if(reportsIDInt != null) {
	    reportsID = reportsIDInt.intValue();
	    validUser = User.isValidUser(reportsID);
	}
    }
    else {
      LogMessage.println("TechListServlet: session is null");
    }


    if(validUser != ReportsConstants.VALIDENTRY) {
        Integer tInt = (Integer)reportsID;
	LogMessage.println("TechListServlet: Not a valid user for " + tInt.toString());
	
	if(session != null) {
	    session.invalidate();
	}
	//Send user back to the start page
	response.sendRedirect(startPageURL);

    } else {
      //Valid user has entered the page
      String reportsDataPath = reportsProperties.getProperty("reports.data.path");
      if (reportsDataPath == null) {
        LogMessage.println("TechListServlet: reports path is null");
      }
	

      // Get the current user info
      User theUser = (User)session.getAttribute("user");
      Integer userID = new Integer(theUser.getUserID());
      String panelName = theUser.getPanelName();

      LogMessage.println("type= " + lpreport );
      if (lpreport != null && lpreport.compareToIgnoreCase("bpp") == 0) {
        if (theUser.isAllowedLPAccess() || theUser.isChair() || theUser.isPundit()) {
          panelName = "BPP";
        }
      }
        
      LogMessage.println ("TechListServlet: memberType" + theUser.getMemberType() + " userType=" + theUser.getType() + "--" + panelName);
      if (theUser.isAdmin() || theUser.isDeveloper()) {
           panelName = reviewerPanel;
           LogMessage.println("TechListServlet: Admin user for " + panelName + " looking at " + userID.toString());
      } 
      if (theUser.isAllowedAccessNow() ) {
        //Get the file which contains the paths to the the science justification, proposal

  	String operation =  request.getParameter("Submit");
        if (operation != null) {
	  LogMessage.println("TechListServlet: Operation is " + operation);
        }
	
	boolean showDebug = false;
        ProposalReviewerList propList = null;

	try {
	    propList =  new ProposalReviewerList(reportsDataPath, showDebug);
            propList.loadProposalsOnPanel(panelName );
	    
	} catch (Exception e) {
	    //e.printStackTrace();
	    caughtError = true;
	    LogMessage.println("TechListServlet:service routine - Caught exception for user ID " + userID);
	    LogMessage.println("Unable to initialize proposal list." + e.getMessage());
	}

	//If we had an error getting the proposals on this panel, send the user
	//back to the start page.
	if(caughtError) {
	    //Send user back to the start page
            LogMessage.println("Leaving TechListServlet: caught error");
	    response.sendRedirect(startPageURL);
	} else {
            session.setAttribute("proposalFileDir", proposalFileDir);
	    session.setAttribute("propList", propList);
	    session.setAttribute("user", theUser);
	    session.setAttribute("panel", panelName);
	    session.setAttribute("backLink", "/displayTechList.jsp");

	    if(theUser.isDeveloper()) {
		timeout = new String("-1");
	    }
	    session.setAttribute("timeout", timeout);

	    //Forward request to the jsp to display the resulting page
	    dispatcher = getServletContext().getRequestDispatcher("/displayTechList.jsp");
            LogMessage.println("Leaving TechListServlet: " + theUser.getUserName());
	    dispatcher.forward(request, response);
	}
      }
      else {
	session.setAttribute("user", theUser);
	session.setAttribute("panel", panelName);
	dispatcher = getServletContext().getRequestDispatcher("/login.jsp?file=NoFile");
        LogMessage.println("TechList: user not authorized for this function: " + theUser.getUserName());
	dispatcher.forward(request, response);
      }
    }
  }

    


}

