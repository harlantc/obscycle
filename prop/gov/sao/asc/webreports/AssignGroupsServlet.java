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
// AssignGroupsServlet

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
import info.*;
import db.DBConnection;

/******************************************************************************/
/**
 */

public class AssignGroupsServlet extends HttpServlet 
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

      LogMessage.println("----------- Entering AssignGroupsServlet ------------");      
      //Reload properties
      reportsProperties = Reports.getProperties();
      String timeout = reportsProperties.getProperty("reports.session.timeout");

    // Get the session object.
    HttpSession session = request.getSession(false );
    String startPageURL = reportsProperties.getProperty("reports.start.url");
    String groupType = reportsProperties.getProperty("groups.name.type");
    RequestDispatcher dispatcher = null;
    int validUser = ReportsConstants.INVALIDENTRY;
    boolean caughtError = false;
    String reviewerPanel = request.getParameter("panelName");
    String reviewerName = new String("");
    if (groupType == null) {
      groupType = new String("");
    }

    String accessDateFile  = reportsProperties.getProperty("reports.access.date.file");
    Boolean proposalGroupsDeadline = Reports.proposalGroupsDeadline(accessDateFile);
   String proposalFileDir  = reportsProperties.getProperty("proposal.file.dir");



    int reportsID = -1;
    if(session != null) {
	Integer reportsIDInt = (Integer)session.getAttribute("reportsID");
	if(reportsIDInt != null) {
	    reportsID = reportsIDInt.intValue();
	    validUser = User.isValidUser(reportsID);
	}
    }
    else {
      LogMessage.println("AssignGroupsServlet: session is null");
    }


    if(validUser != ReportsConstants.VALIDENTRY) {
        Integer tInt = (Integer)reportsID;
	LogMessage.println("AssignGroupsServlet: Not a valid user for " + tInt.toString());
	
	if(session != null) {
	    session.invalidate();
	}
	//Send user back to the start page
	response.sendRedirect(startPageURL);

    } else {
	//Valid user has entered the page
	String reportsDataPath = reportsProperties.getProperty("reports.data.path");
	if (reportsDataPath == null) {
	    LogMessage.println("AssignGroupsServlet: reports path is null");
	}
	

	//Look up username  and panel in proposal database to get type
	//Integer userID = new Integer(request.getParameter("userID"));
	//User theUser = new User(userID.intValue(),reportsDataPath);
	User theUser = (User)session.getAttribute("user");
	Integer userID = new Integer(theUser.getUserID());
	String panelName = theUser.getPanelName();

        LogMessage.println ("AssignGroupsServlet: memberType" + theUser.getMemberType() + " userType=" + theUser.getType());
        if (theUser.isAdmin() || theUser.isDeveloper()) {
             panelName = reviewerPanel;
             LogMessage.println("AssignGroupsServlet: Admin user for " + panelName + " looking at " + userID.toString());
        } 
	
	//Get the file which contains the paths to the the science justification, proposal


	String operation =  request.getParameter("Submit");
        if (operation != null) {
	  LogMessage.println("AssignGroupsServlet: Operation is " + operation);
        }
	
	boolean showDebug = false;
        ProposalGroupsList groupsList = null;

	try {
	    groupsList =  new ProposalGroupsList(reportsDataPath, showDebug);
            // now call routine to read in the groups
            groupsList.loadProposalsOnPanel(userID,reviewerName,
                        panelName );
	    
	} catch (Exception e) {
	    //e.printStackTrace();
	    caughtError = true;
	    LogMessage.println("AssignGroupsServlet:service routine - Caught exception for user ID " + userID);
	    LogMessage.println("Unable to initialize proposal list." + e.getMessage());
	}
        // re-load the existing groups file
        groupsList.readGroups();
	
	if (operation != null ) {
	   saveGroups(request,groupsList);
           if ( operation.compareToIgnoreCase("Save") == 0) {
             LogMessage.println("Saving groups for " + theUser.getUserName());
             // save all the grades to a file
             groupsList.saveGroups();
          }
	}


	//If we had an error getting the proposals on this panel, send the user
	//back to the start page.
	if(caughtError) {
	    //Send user back to the start page
            LogMessage.println("Leaving AssignGroupsServlet: caught error");
	    response.sendRedirect(startPageURL);
	} else {
           session.setAttribute("proposalFileDir", proposalFileDir);
	    session.setAttribute("groupsList", groupsList);
	    session.setAttribute("user", theUser);
	    session.setAttribute("panel", panelName);
	    session.setAttribute("groupType", groupType);
	    session.setAttribute("backLink", "/displayGroups.jsp");
            session.setAttribute("proposalGroupsDeadline", proposalGroupsDeadline);

	    if(theUser.isDeveloper()) {
		timeout = new String("-1");
	    }
	    session.setAttribute("timeout", timeout);

	    //Forward request to the jsp to display the resulting page
            if ( operation != null && operation.compareToIgnoreCase(ReportsConstants.CSVVERSION) == 0) {
              dispatcher = getServletContext().getRequestDispatcher("/displayGroupsPrint.jsp");

            }
            else {
	      dispatcher = getServletContext().getRequestDispatcher("/displayGroups.jsp");
            }
            LogMessage.println("Leaving AssignGroupsServlet: " + theUser.getUserName());
	    dispatcher.forward(request, response);
	}
	
    }
  }

    

  /**
   * save the group assignments for this panel
   * @param request Http servlet request
   * @param groupsList list of groups for the proposals
   * @exception ServletException servlet exception
   * @exception IOException  unable to write to file
   */
  private void saveGroups( HttpServletRequest request,
	ProposalGroupsList groupsList )
      throws ServletException, IOException {

    String parameterName, parameterValue;
    for ( Enumeration parameters = request.getParameterNames();
          parameters.hasMoreElements(); ) {
	parameterName = (String) parameters.nextElement();
	parameterValue = request.getParameter( parameterName );
        //LogMessage.println( "GROUP: Parameter: " + parameterName + " = " + parameterValue  + "------");

      if (parameterName.startsWith("prop")) {
        // get the proposalNumber and set the group 
        // get the conflict value for same proposal number
        StringTokenizer st = new StringTokenizer(parameterName,"prop");
        String propnum = st.nextToken();
        String groupName = new String("");
        if (parameterValue != null && parameterValue.length() > 0 ) {
          groupName = new String(parameterValue);
          groupName = groupName.trim();
          groupName = stripInput(groupName);
        }
        // need to find the proposal to set the group
        groupsList.setGroupNameForProposal(propnum,groupName);
        
      }
    }

  }


  public String stripInput( String value )
  {

    String retValue;

    if (value != null) {
      int slen = value.length();
      if (slen > 50) { slen=50; }
      retValue = value.substring(0,slen);
      retValue = retValue.replaceAll("[^\\p{ASCII}]","");
      retValue = retValue.replaceAll("\""," ");
      retValue = retValue.replaceAll("'"," ");
      retValue = retValue.replaceAll("`"," ");
      retValue = retValue.replaceAll("\\\\"," ");
      if (!value.equals(retValue)) {
        LogMessage.println("Groups: before: " + value);
        LogMessage.println("Groups: after : " + retValue);
      }
    } else {
       retValue =value;
    }

    return retValue;
  }


}
