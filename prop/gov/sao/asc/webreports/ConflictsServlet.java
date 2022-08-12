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
// ConflictsServlet

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.File;
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
import ascds.LogMessage;
import info.User;
import info.Reports;
import info.ReportsConstants;
import info.Proposal;
import info.ProposalConflictsList;
import info.ModifiedInstitutions;
import db.DBConnection;

/******************************************************************************/
/**
 */

public class ConflictsServlet extends HttpServlet 
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
   * Handle a submission from a browser.  
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void service( HttpServletRequest request, HttpServletResponse response )
      throws ServletException, IOException {

      LogMessage.println("----------- Entering ConflictsServlet ------------");      
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

    int reportsID = -1;
    if(session != null) {
	Integer reportsIDInt = (Integer)session.getAttribute("reportsID");
	if(reportsIDInt != null) {
	    reportsID = reportsIDInt.intValue();
	    validUser = User.isValidUser(reportsID);
	}
    }
    else {
      LogMessage.println("ConflictServlet: session is null.");
    }

    if(validUser != ReportsConstants.VALIDENTRY) {
        Integer tInt = (Integer)reportsID;
	LogMessage.println("ConflictsServlet: Not a valid user for " + tInt.toString());
	
	if(session != null) {
	    session.invalidate();
	}
	//Send user back to the start page
	response.sendRedirect(startPageURL);

    } else {
	//Valid user has entered the page
	String reportsDataPath = reportsProperties.getProperty("reports.data.path");
	String punditFilename = reportsProperties.getProperty("pundits.filename");
       
	if (reportsDataPath == null) {
	    LogMessage.println("ConflictsServlet: reports path is null");
	}
	

	//Look up username  and panel in proposal database to get type
	User theUser = (User)session.getAttribute("user");
	Integer userID = new Integer(theUser.getUserID());
	String panelName = theUser.getPanelName();

   LogMessage.println ("ConflictsServlet: USER= " + theUser.getMemberType() + " userType=" + theUser.getType());
        if (reviewerPanel != null ) { 
          if (theUser.isAdmin() || theUser.isDeveloper() ) {
            panelName = reviewerPanel;
            LogMessage.println("ConflictsServlet: Admin user for " + panelName + " looking at " + userID.toString());
          }
          if(theUser.inChairMode() || theUser.isPundit())  {
            if (reviewerPanel.equalsIgnoreCase("bpp")) {
              panelName = reviewerPanel;
            }
          }
        } 
	

	String operation =  request.getParameter("Submit");
	//LogMessage.println("Operation is " + operation);
	
	boolean showDebug = true;
        ProposalConflictsList conflictList= 
		new ProposalConflictsList(reportsDataPath,punditFilename,showDebug);
	try {
          conflictList.loadPanel( panelName );
	} catch (Exception e) {
	  caughtError = true;
	  LogMessage.println("ConflictsServlet:service routine - Caught exception for user ID " + userID);
	  LogMessage.println("Unable to initialize proposal list." + e.getMessage());
	}
	
	if (operation != null && operation.compareToIgnoreCase("Save") == 0) {
            LogMessage.println("Saving conflict comments for panel " + panelName);
	    saveConflicts(request,conflictList);
	}
	else  {
	    // actually don't need to know any other operation because it should
	    // re-load the existing conflicts file for this user
	    conflictList.readConflicts();

	}

	
	//If we had an error getting the proposals on this panel, send the user
	//back to the start page.
	if(caughtError) {
	    //Send user back to the start page
            LogMessage.println("Leaving ConflictServlet : caught error"); 
	    response.sendRedirect(startPageURL);
	} else {
            session.setAttribute("proposalFileDir", proposalFileDir);
	    session.setAttribute("conflictList", conflictList);
	    session.setAttribute("user", theUser);
	    session.setAttribute("panel", panelName);
	    session.setAttribute("backLink", "/displayConflicts.jsp");
	    if(theUser.isDeveloper()) {
		timeout = new String("-1");
	    }
	    session.setAttribute("timeout", timeout);

	    //Forward request to the jsp to display the resulting page
            LogMessage.println("Leaving ConflictServlet : " + theUser.getUserName());
	    dispatcher = getServletContext().getRequestDispatcher("/displayConflicts.jsp");
	    dispatcher.forward(request, response);
	}
	
    }
  }

    

  /**
   * save the conflict comments for this panel
   * @param request Http servlet request
   * @param conflictList list of conflicts
   * @exception ServletException servlet exception
   * @exception IOException  unable to write to file
   */
  private void saveConflicts( HttpServletRequest request,ProposalConflictsList conflictList )
      throws ServletException, IOException {

    String parameterName, parameterValue;
    for ( Enumeration parameters = request.getParameterNames();
          parameters.hasMoreElements(); ) {
	parameterName = (String) parameters.nextElement();
	parameterValue = request.getParameter( parameterName );
      // LogMessage.println( "Parameter: " + parameterName + " = " + parameterValue  + "------");

      if (parameterName.startsWith("prop")) {
        // get the proposalNumber and set the group 
        // get the conflict value for same proposal number
        StringTokenizer st = new StringTokenizer(parameterName,"prop");
        String propnum = st.nextToken();
        String comment = new String("");
        if (parameterValue != null && parameterValue.length() > 0 ) {
          comment = new String(parameterValue);
        }
        // need to find the proposal to set the group
        conflictList.setCommentForProposal(propnum,comment);
        
      }
    }
    conflictList.saveConflicts();

    

  }

}

