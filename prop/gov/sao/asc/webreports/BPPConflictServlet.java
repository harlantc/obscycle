/*
  Copyrights:
 
  Copyright (c) 2018, 2021 Smithsonian Astrophysical Observatory
 
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
// BPPConflictServlet

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ascds.LogMessage;
import info.Reports;
import info.ReportsConstants;
import info.User;
import info.PrelimGradesList;
import info.Proposal;
import info.ModifiedInstitutions;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/******************************************************************************/
/**
 */

public class BPPConflictServlet extends HttpServlet 
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

    LogMessage.println("---- Entering BPPConflictServlet ----");      
      
    //Reload properties
    reportsProperties = Reports.getProperties();
    String timeout = reportsProperties.getProperty("reports.session.timeout");

    // Get the session object.
    HttpSession session = request.getSession(false );
    String startPageURL = reportsProperties.getProperty("reports.start.url");
    RequestDispatcher dispatcher = null;
    int validUser = ReportsConstants.INVALIDENTRY;
    boolean caughtError = false;
    String reviewerName = null;
    Integer reviewerID =  null;
    User theUser = null;
    String tempStr = request.getParameter("reviewerID"); 
    if (tempStr != null) {
      reviewerID = new Integer (tempStr);
    }

    String accessDateFile  = reportsProperties.getProperty("reports.access.date.file");

    //Make sure the user has requested the page through the CDO site and is logged in
    int reportsID = -1;
    if(session != null) {
	Integer reportsIDInt = (Integer)session.getAttribute("reportsID"); 
        theUser = (User)session.getAttribute("user");
	if(reportsIDInt != null) {
	    reportsID = reportsIDInt.intValue();
	    validUser = User.isValidUser(reportsID);
	}
    }
    else {
      LogMessage.println("BPPConflictServlet: session is null");      
    }


    if(validUser != ReportsConstants.VALIDENTRY || 
       (!theUser.isPundit() && !theUser.isChair()) ) {
        Integer tInt = (Integer)reportsID;
	LogMessage.println("BPPConflictServlet: Not a valid user " + tInt.toString());
	
	if(session != null) {
	    session.invalidate();
	}
	
	//Send user back to the start page
	response.sendRedirect(startPageURL);

    } else {
	//Valid user has entered the page

	String reportsDataPath = reportsProperties.getProperty("reports.data.path");
    Integer pcontRows = Integer.valueOf(reportsProperties.getProperty("pcont.rows"));
	if (reportsDataPath == null) {
	    LogMessage.println("BPPConflictServlet: reports path is null");
	}
	

	//Look up username  and panel in proposal database to get type
	Integer userID = new Integer(theUser.getUserID());
	String panelName = "BPP";
      String preConfGuidelines = reportsProperties.getProperty("preconf.guidelines.url");
    String instDataPath = reportsProperties.getProperty("inst.conflict.filename");
    ModifiedInstitutions modInstitutions = new ModifiedInstitutions(instDataPath);
    ArrayList<String[]> contactsList = new ArrayList<>();
    reviewerName = theUser.getUserName();
    reviewerID = userID;

	String operation =  request.getParameter("operation");
	
        PrelimGradesList prelimGradesList = null;
	boolean showDebug = false;
	try {
	    prelimGradesList = new PrelimGradesList(reportsDataPath,showDebug);
	    prelimGradesList.loadProposalsOnPanel(userID,reviewerName, panelName );
        contactsList = prelimGradesList.getContactsList();
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    caughtError = true;
	    LogMessage.println("BPPConflictServlet: loading propsals - Caught exception for user ID " + userID);
	}
	
	// re-load the existing grades file for this user
      prelimGradesList.readGrades();
      prelimGradesList.readContacts();
        // this will set conflicts for any proposals not in the
        // existing file
	prelimGradesList.setConflicts(theUser);

	if (operation != null ) {
	  savePrelimConflicts(request, prelimGradesList, contactsList, pcontRows);
          if ( operation.compareToIgnoreCase("Save") == 0) {
            LogMessage.println("Saving grades for " + theUser.getUserName());
            // save all the grades to a file
              prelimGradesList.saveGrades();
          }
          LogMessage.println("BPPConflicts: operation=" + operation + " for " +theUser.getUserName());
            
	}
	
	//If we had an error getting the proposals on this panel, send the user
	//back to the start page.
	if(caughtError) {
	    //Send user back to the start page
	    response.sendRedirect(startPageURL);
	} else {
      session.setAttribute("prelimGradesList", prelimGradesList);
      session.setAttribute("user", theUser);
      session.setAttribute("reviewerID", reviewerID.toString());
      session.setAttribute("userID", reviewerID.toString());
      session.setAttribute("panelName", panelName);
      session.setAttribute("institutions", modInstitutions);
      session.setAttribute("contactsList", contactsList);
      session.setAttribute("pcontRows", pcontRows);
      session.setAttribute("preConfGuidelines", preConfGuidelines);
      session.setAttribute("ichanged", "0");

	    //Send the user back to servlet. No need to go through
	    //servlet, as all the information is already in the session.
	    session.setAttribute("backLink", "/displayPreConflict.jsp");

	    session.setAttribute("timeout", timeout); 

	    //Forward request to the jsp to display the resulting page
	    dispatcher = getServletContext().getRequestDispatcher("/displayBPPConflict.jsp");
            LogMessage.println("Leaving BPPConflictServlet: " + reviewerID.toString());
	    dispatcher.forward(request, response);
	}
	
    }
  }


  //  Get the preliminary grades value for each proposal from the form
  private void savePrelimConflicts(HttpServletRequest request,
      PrelimGradesList prelimGradesList,
      ArrayList<String[]> contactList,
      Integer pcontRows)
      throws ServletException, IOException {

    int numParams = -1;
    Double dummygrade= -1.0;

    for (int ii=0; ii< prelimGradesList.size();ii++) {
      Proposal prop = prelimGradesList.get(ii);
      numParams += 1;
      String propnum = prop.getProposalNumber();
      String conflictName = "cprop" + propnum;
      String conflictValue = "";
      if (request.getParameter(conflictName) != null &&
          request.getParameter(conflictName).compareToIgnoreCase("on") == 0) {
        conflictValue = "C";
      }
      //LogMessage.println("processing " + conflictName + " conflict=" + conflictValue);
      prelimGradesList.setPreliminaryGradeForProposal(propnum,dummygrade,
          conflictValue,false);
    }

    // Get personal Contact list by clearing out the existing and repopulating with new
    ArrayList<String[]> newContactList = new ArrayList<>();
    Map<String, String> matches = new HashMap<>();
    boolean moreRows = true;
    int nrow = 0;
    String firstName, lastName, currentInst;
    while(moreRows) {
      firstName = StringEscapeUtils.unescapeHtml4(request.getParameter("firstName_"+nrow));
      lastName = StringEscapeUtils.unescapeHtml4(request.getParameter("lastName_"+nrow));
      currentInst = request.getParameter("inst_"+nrow);
      if (!StringUtils.isBlank(firstName) && !StringUtils.isBlank(lastName) && !StringUtils.isBlank(currentInst)) {
        String[] contact = {firstName, lastName, currentInst,""};
        prelimGradesList.matchContacts(contact, matches);
        newContactList.add(contact);
        nrow++;
      }
      else{
        //  If no rows, still need to check that there weren't entries removed
        moreRows = false;
        String[] contact = {"", "", "",""};
        prelimGradesList.matchContacts(contact, matches);
      }
    }
    contactList.clear();
    contactList.addAll(newContactList);

    if (numParams < 0) {
      LogMessage.println("PreConflictServlet ERROR: no request parameters for " +  prelimGradesList.getReviewerName());
    }
  }

}

