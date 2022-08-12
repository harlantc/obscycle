/*
  Copyrights:
 
  Copyright (c) 2000-2022 Smithsonian Astrophysical Observatory
 
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
// AssignGradesServlet

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
import info.Reports;
import info.ReportsConstants;
import info.User;
import info.PrelimGradesList;
import db.DBConnection;

/******************************************************************************/
/**
 */

public class AssignGradesServlet extends HttpServlet 
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

      LogMessage.println("---- Entering AssignGradesServlet ----");      
      
      //Reload properties
      reportsProperties = Reports.getProperties();
      String timeout = reportsProperties.getProperty("reports.session.timeout");


    // Get the session object.
    HttpSession session = request.getSession(false );
    String startPageURL = reportsProperties.getProperty("reports.start.url");
    RequestDispatcher dispatcher = null;
    int validUser = ReportsConstants.INVALIDENTRY;
    boolean caughtError = false;
    String reviewerPanel = request.getParameter("panelName");
    String reviewerName = null;
    Integer reviewerID =  null;
    String tempStr = request.getParameter("reviewerID"); 
    if (tempStr != null) {
      reviewerID = new Integer (tempStr);
    }

    String proposalFileDir  = reportsProperties.getProperty("proposal.file.dir");
    String accessDateFile  = reportsProperties.getProperty("reports.access.date.file");
    Boolean prelimGradesDeadline = Reports.prelimGradesDeadline(accessDateFile);      


    //Make sure the user has requested the page through the CDO site and is logged in
    int reportsID = -1;
    if(session != null) {
	Integer reportsIDInt = (Integer)session.getAttribute("reportsID"); 
	if(reportsIDInt != null) {
	    reportsID = reportsIDInt.intValue();
	    validUser = User.isValidUser(reportsID);
	}
    }
    else {
      LogMessage.println("AssignGradesServlet: session is null");      
    }

    if(validUser != ReportsConstants.VALIDENTRY) {
        Integer tInt = (Integer)reportsID;
	LogMessage.println("AssignGradesServlet: Not a valid user " + tInt.toString());
	
	if(session != null) {
	    session.invalidate();
	}
	
	//Send user back to the start page
	response.sendRedirect(startPageURL);

    } else {
	//Valid user has entered the page

	String reportsDataPath = reportsProperties.getProperty("reports.data.path");
	if (reportsDataPath == null) {
	    LogMessage.println("AssignGradesServlet: reports path is null");
	}
	

	//Look up username  and panel in proposal database to get type
	User theUser = (User)session.getAttribute("user");
	Integer userID = new Integer(theUser.getUserID());
	String panelName = theUser.getPanelName();

        User reviewer = theUser;
        if (theUser.isAdmin() || theUser.isDeveloper()) {
             userID= reviewerID;
             reviewer = new User(reviewerID.intValue(),reportsDataPath);
             reviewerName = reviewer.getUserName();
             panelName = reviewerPanel;
             LogMessage.println("Admin user for " + panelName + " looking at " + userID.toString());
        } 
        else {
             reviewerName = theUser.getUserName();
             reviewerID = userID;
        }
	

	String operation =  request.getParameter("Submit");
	
        PrelimGradesList prelimGradesList = null;
	boolean showDebug = false;
	try {
	    prelimGradesList = new PrelimGradesList(reportsDataPath,showDebug);
	    prelimGradesList.loadProposalsOnPanel(userID,reviewerName,
			panelName );
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    caughtError = true;
	    LogMessage.println("AssignGradesServlet:service routine - Caught exception for user ID " + userID);
	    LogMessage.println("Unable to initialize proposal list.");
	}
	
	// re-load the existing grades file for this user
	prelimGradesList.readGrades();
	prelimGradesList.readContacts();
        // this will set conflicts for any proposals not in the
        // existing file
        prelimGradesList.setConflicts(reviewer);


	if (operation != null ) {
	  savePrelimGrades(request, prelimGradesList);
          if ( operation.compareToIgnoreCase("Save") == 0) {
            LogMessage.println("Saving grades for " + theUser.getUserName());
            // save all the grades to a file
              prelimGradesList.saveGrades();
          }
            
	}
	
	//If we had an error getting the proposals on this panel, send the user
	//back to the start page.
	if(caughtError) {
	    //Send user back to the start page
	    response.sendRedirect(startPageURL);
	} else {
            session.setAttribute("proposalFileDir", proposalFileDir);
	    session.setAttribute("prelimGradesList", prelimGradesList);
	    session.setAttribute("user", theUser);
            session.setAttribute("reviewerID", reviewerID.toString());
	    session.setAttribute("panelName", panelName);
	    session.setAttribute("prelimDeadline", prelimGradesDeadline);
	    //Send the user back to servlet. No need to go through
	    //servlet, as all the information is already in the session.
	    session.setAttribute("backLink", "/displayPrelimGrades.jsp");

	    //Allows the timeout value to be updated dynamically
	    if(theUser.isDeveloper()) {
		//no timeouts for developers
		timeout =  new String("-1"); 
	    }

	    session.setAttribute("timeout", timeout); 

	    //Forward request to the jsp to display the resulting page
           if ( operation != null && operation.compareToIgnoreCase(ReportsConstants.CSVVERSION) == 0) {
              dispatcher = getServletContext().getRequestDispatcher("/displayPrelimPrint.jsp");

            }
            else {
              dispatcher = getServletContext().getRequestDispatcher("/displayPrelimGrades.jsp");
            }

            LogMessage.println("Leaving assign Grades Servlet: " + reviewerID.toString());
	    dispatcher.forward(request, response);
	}
	
    }
  }

    

  //  Get the prelminary grades value for each proposal from the form
  private void savePrelimGrades(HttpServletRequest request,
	PrelimGradesList prelimGradesList)
      throws ServletException, IOException {

    String parameterName, parameterValue;
    int numParams = -1;
    for ( Enumeration parameters = request.getParameterNames();
          parameters.hasMoreElements(); ) {
      parameterName = (String) parameters.nextElement();
      parameterValue = request.getParameter( parameterName );

      if (parameterName.startsWith("prop")) {
        // get the proposalNumber and set the grade 
        // get the conflict value for same proposal number
        numParams += 1;
        StringTokenizer st = new StringTokenizer(parameterName,"prop");
        String propnum = st.nextToken();
	Double gradeValue = new Double(-1.0);
        if (parameterValue != null && parameterValue.length() > 0 ) {
          parameterValue = parameterValue.trim();
          try {
	    gradeValue = new Double(parameterValue);
          }
          catch (Exception exc) {
            LogMessage.println("Invalid grade: " + propnum + ":" + parameterValue);
          }
        }
        String conflictName = "cprop" + propnum;
        String conflictValue = new String(""); 
        if (request.getParameter(conflictName) != null && 
            request.getParameter(conflictName).compareToIgnoreCase("on") == 0) {
           conflictValue = "C";
        }
        prelimGradesList.setPreliminaryGradeForProposal(propnum,gradeValue,
	          conflictValue,false);
      }
    }

   if (numParams < 0) {
      LogMessage.println("AssignGradesServlet ERROR: no request parameters for " +  prelimGradesList.getReviewerName());
   }

    

  }

}

