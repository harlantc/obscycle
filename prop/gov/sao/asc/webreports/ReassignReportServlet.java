/*
  Copyrights:
 
  Copyright (c) 2015 Smithsonian Astrophysical Observatory
 
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
// ReassignReportServlet
// This class will handle the request to reassign a proposal

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import db.DBConnection;
import info.User;
import info.ReportsConstants;
import info.ReassignRequest;
import info.Reports;
import ascds.LogMessage;
import org.apache.commons.lang3.*;

/******************************************************************************/
/**
 */

public class ReassignReportServlet extends HttpServlet 
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

      LogMessage.println("---- Entering ReassignReportServlet  ----");
      
      //Reload properties
      reportsProperties = Reports.getProperties();
      String timeout = reportsProperties.getProperty("reports.session.timeout");

      HttpSession session = request.getSession( false );
      String startPageURL = reportsProperties.getProperty("reports.start.url");
      int validUser = ReportsConstants.INVALIDENTRY;

      Boolean canSave=true;
      String msg="";

      int reportsID = -1;
      if(session != null) {
	  Integer reportsIDInt = (Integer)session.getAttribute("reportsID");
	  if(reportsIDInt != null) {
	      reportsID = reportsIDInt.intValue();
	      validUser = User.isValidUser(reportsID);
	      //System.err.println("ReassignReportServlet::isValidUser returns = " + validUser);
	  } 
          /*
          Enumeration sesvar = session.getAttributeNames();
          while(sesvar.hasMoreElements()) {
            String sesname = (String)sesvar.nextElement();
            LogMessage.println("ReassignReportsServlet: Session " + sesname + " = " + session.getAttribute(sesname));
          }
          Enumeration parameterList = request.getParameterNames();
          while( parameterList.hasMoreElements() ) {
            String sName = parameterList.nextElement().toString();
            LogMessage.println("ReassignReportsServlet: Param " + sName + " = " + request.getParameter(sName));
          }
          LogMessage.println("ReassignReportsServlet: Done with params");
          } 
          end debug only */
      }
      else {
        LogMessage.println("ReassignReportServlet: session is null " );
      }

      //Only process request if the user has entered the site properly
      if(validUser == ReportsConstants.VALIDENTRY) {
          Integer rid;
          User theUser = (User)session.getAttribute("user");
          if (request.getParameter("reviewerID") != null) {
            int reviewerID = Integer.parseInt(request.getParameter("reviewerID"));
            rid = new Integer(reviewerID);
          } else {
            rid = reportsID;
          }
	  //int reviewerID = rid.intValue();
	  int userID = theUser.getUserID();
	  String operation = request.getParameter("operation");
	  String proposalNumber = (String)request.getParameter("reassignProp");
	  String reportType = (String)request.getParameter("reassignType");
	  String reportsDataPath = reportsProperties.getProperty("reports.data.path");
          String reassignb="";
          LogMessage.println("Reassign Request from " + userID  + " for " + proposalNumber + "  " + reportType );
          if (operation == null ) {
            operation = ReportsConstants.REASSIGN;
          }
          if (operation.equals(ReportsConstants.REASSIGNb)) {
            // so that cancel goes back to report and not to the list
            reassignb="b";
          }
  
          LogMessage.println("Reassign Request operation is " + operation );
	  session.setAttribute("user", theUser);
          session.setAttribute("reviewerID",rid.toString());
	  session.setAttribute("reassignb", reassignb);
          ReassignRequest rr = new ReassignRequest(reportsDataPath,theUser,reportType);
          if ( operation.equals(ReportsConstants.REASSIGN) || operation.equals(ReportsConstants.REASSIGNb) ) {

            rr.GetRequest(proposalNumber,rid);

            session.setAttribute("reassignEdit",canSave);
            session.setAttribute("reassignMsg",msg);
	    session.setAttribute("reassignReport", rr);
	    LogMessage.println("ReassignReportServlet:  initial for " + rid.toString());
	    //Forward request to the display report jsp
	    RequestDispatcher dispatcher = null;
	    dispatcher = getServletContext().getRequestDispatcher("/displayReassign.jsp");
	    dispatcher.forward(request, response);
          } else if(operation.equals("Submit")) {
            String cmt=request.getParameter("cmt");
            cmt = StringEscapeUtils.unescapeHtml4(cmt);
            msg = rr.SaveRequest(proposalNumber,rid,cmt);
            try {
              DBConnection dbConnect = new DBConnection(reportsDataPath, true);
              // want to track all primary/secondary requests but also want
              // to add to entries used for conflicts in preliminary grades etc
              dbConnect.updateReviewerConflict(rid,rr.getProposalNumber(),rr.getReportType(),"N");
              dbConnect.updateReviewerConflict(rid,rr.getProposalNumber(),"Personal","N");
            }
            catch (Exception exc) {
              LogMessage.printException(exc);
            }
            canSave=false;
            session.setAttribute("reassignEdit",canSave);
            session.setAttribute("reassignMsg",msg);
	    session.setAttribute("reassignReport", rr);
	    //Forward request to the display report jsp
	    RequestDispatcher dispatcher = null;
	    dispatcher = getServletContext().getRequestDispatcher("/displayReassign.jsp");
	    dispatcher.forward(request, response);

          } else if(operation.equals(ReportsConstants.LISTPROPOSALS) ) {
            request.setAttribute("reassignMsg","");
            listProposals(request, response);
          } else {
	    //Invalid operation 
	    LogMessage.println("ReassignReportServlet: Not a valid operation for " + operation);
	    //send user back to the login page
	    response.sendRedirect(startPageURL);
          }
      } else {
	  //Invalid user - requested page incorrectly
          Integer tInt = (Integer)reportsID;
	  LogMessage.println("ReassignReportServlet: Not a valid user for " + tInt.toString());

	  //send user back to the login page
	  response.sendRedirect(startPageURL);

      }
    }

    /*
     * listProposals
     *
     * This routine will redirect the user to the list of proposals
     */
    private void listProposals(HttpServletRequest request, HttpServletResponse response) {
        //Redirect user to the list of proposal page. The page will
        //depend on if the user is a reviewer or chair/admin
        String mode = request.getParameter("userMode");
        int userID = Integer.parseInt(request.getParameter("userID"));
        String userType = request.getParameter("userType");
        String userName = request.getParameter("userName");

        String idPar = new String("userID="+ userID);
        String namePar = new String("userName=" + userName);
        String typePar = new String("userType=" + userType);
        String params = idPar + "&" + namePar + "&" + typePar;

        String reviewerURL = new String("/reviewReports.jsp?");
        reviewerURL += params;

        try {
            RequestDispatcher dispatcher = null;
            LogMessage.println("ReassignReportServlet: Sending redirect to: " + reviewerURL + "for " + userName);
            dispatcher = getServletContext().getRequestDispatcher(reviewerURL);
            dispatcher.forward(request, response);
        } catch(Exception ex) {
            ex.printStackTrace();
            LogMessage.println("ReassignReportServlet: Caught exception in listProposals routine:");
            LogMessage.println(ex.getMessage());
        }
    }

}

