/*
  Copyrights:
 
  Copyright (c) 2000-2021 Smithsonian Astrophysical Observatory
 
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
// GetReportServlet
// This class will handle the request from a list of proposals,
// to display the review report for a single proposal, with
// the appropriate links to the supporting documents 

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import db.DBConnection;
import info.User;
import info.ReportsConstants;
import info.ReviewReport;
import info.Reports;
import info.Notes;
import info.CmtEdits;
import ascds.LogMessage;

/******************************************************************************/
/**
 */

public class GetReportServlet extends HttpServlet 
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

      LogMessage.println("---- Entering GetReportServlet  ----");
      
      //Reload properties
      reportsProperties = Reports.getProperties();
      String timeout = reportsProperties.getProperty("pas.session.timeout");
      String proposalFileDir = reportsProperties.getProperty("proposal.file.dir");

      HttpSession session = request.getSession( false );
      String startPageURL = reportsProperties.getProperty("reports.start.url");
      int validUser = ReportsConstants.INVALIDENTRY;

      int reportsID = -1;
      if(session != null) {
	  Integer reportsIDInt = (Integer)session.getAttribute("reportsID");
	  if(reportsIDInt != null) {
	      reportsID = reportsIDInt.intValue();
	      validUser = User.isValidUser(reportsID);
	      //System.err.println("GetReportServlet::isValidUser returns = " + validUser);
	  } 
/*
          Enumeration sesvar = session.getAttributeNames();
          while(sesvar.hasMoreElements()) {
            String sesname = (String)sesvar.nextElement();
            LogMessage.println("GetReportsServlet: " + sesname + " = " + session.getAttribute(sesname));
          }
*/

      }
      else {
        LogMessage.println("GetReportServlet: session is null " );
      }

      //Only process request if the user has entered the site properly
      if(validUser == ReportsConstants.VALIDENTRY) {
          try {
	  int reviewerID = Integer.parseInt(request.getParameter("reviewerID"));
	  int userID =  reviewerID; 
	  String proposalNumber = request.getParameter("propNum");
	  String mode = request.getParameter("mode");
	  String reportType = request.getParameter("type");
	  User theUser = (User)session.getAttribute("user");
          String userName = theUser.getUserName();
	  boolean useReviewerID = false;
          //this value should be read from the .htExtraConfig file
	  boolean emailUserLockedReport = false; 
	  Boolean beforePRBool = (Boolean)session.getAttribute("beforePR");
	  Boolean editLP = (Boolean)session.getAttribute("editLPReports");
	  boolean beforePR = true;
          LogMessage.println("GetReportsServlet: " + userName + ":  mode =" +  mode +  "  reportType=" + reportType);
          if (editLP == null) {
             editLP = new Boolean(false);
          }
	  if(beforePRBool != null) {
	      beforePR = beforePRBool.booleanValue();
	  }
      boolean isAdminChairPunditDevel = mode != null &&
             (mode.equals(ReportsConstants.CHAIR) ||
              mode.equals(ReportsConstants.ADMIN) ||
              mode.equals(ReportsConstants.PUNDIT) ||
              mode.equals(ReportsConstants.DEVELOPER));

      if(mode == null || theUser.isReviewer()) {
	      mode = new String(ReportsConstants.REVIEWER);
	      userID = theUser.getUserID();

	  } else if(isAdminChairPunditDevel) {
	      //If the user is an admin or developer, or in chair mode,
	      //then the user is different from the reviewer. We need
	      //the reviewer ID to find the correct type of report 
	      //(primary, secondary or peer) in the database
	      userID = theUser.getUserID();
	      useReviewerID = true;
              LogMessage.println("GetReportServlet: FOR " + mode + " : " + userID + " is the userid");
	  }


	  String reportsDataPath = reportsProperties.getProperty("reports.data.path");
      String pgrPath = reportsProperties.getProperty("pgr.path");
	  String propFileDir = reportsProperties.getProperty("proposal.file.dir");
            String guidelinesUrl = reportsProperties.getProperty("guidelines.url");

	  //see if there is a review report in the session to use
	  ReviewReport rr = null;
	  boolean useSessionReport = false;
          if (theUser.getPanelName() != null && 
              (reportType == null  || !reportType.equals(ReportsConstants.LP))) {
            try {
               LogMessage.println("GetReportsServlet: " + userName + ": loading by panel=" + theUser.getPanelName() + "  proposal=" + proposalNumber);
               DBConnection dbConnect = null;
               dbConnect = new DBConnection(reportsDataPath, false);

               Vector<ReviewReport> reportsList = new Vector<ReviewReport>();
               reportsList=dbConnect.getReportByPropNumPanel(
			theUser.getPanelName(),proposalNumber,beforePR);
               if (reportsList.size() == 1) {
                   rr = (ReviewReport)reportsList.get(0);
                   rr.setType("");
               }
            } catch (Exception exc) {
               LogMessage.println("GetReportServlet: " + userName + ": Failed to retrieve single report info for " +proposalNumber);
            }
          }
          if (rr == null &&  !theUser.getMemberType().equals(ReportsConstants.REVIEWER) ) {
               LogMessage.println("GetReportServlet: Initializing review report by proposal number");
	       rr = new ReviewReport(proposalNumber);
               rr.setType("");
          }
            
          if (rr != null) {
	      rr.setPanelName(theUser.getPanelName());
              rr.setDataPath(reportsDataPath);

	      if(reportType != null) {
		  //Set the report type, if it's not null - this allows
		  //the report to send the user back to the correct "back"
		  //page.
		  rr.setType(reportType);
	      }
              else {
		  rr.setType("");
              }

	      //Admins, developers and chairs need to use the reviewer ID
	      //and not their user ID to find the correct type of report.
              //LogMessage.println ("setting the user id");
	      rr.setUserID(theUser.getUserID());
	      if(useReviewerID) {
		  rr.setReviewerID(reviewerID);
	      } else {
		  rr.setReviewerID(theUser.getUserID());
	      }

	      boolean loadedReport = false;

	      if(theUser != null ) {
		  boolean allowedToEdit = theUser.isAllowedToEdit();
                  // special case for view lp reports
                  if (rr.getType().equals(ReportsConstants.LP) && !editLP) {
                    allowedToEdit = false;
                  }
		  loadedReport = rr.loadReport(theUser.getUserName(), 
                         reportsDataPath,propFileDir,beforePR, allowedToEdit);

		  //Email the user who has a locked report if the report is                       //locked and the config file specifies to email the user.
		  String emailUserStr = reportsProperties.getProperty("reports.email.user.locked.report");
		  if(emailUserStr != null && emailUserStr.equals("1")) {
		      //System.err.println("Config file specifies to email user with locked report.");
		      emailUserLockedReport = true;
		  }
		  
		  if(emailUserLockedReport && rr.isAlreadyBeingEdited(theUser.getUserName())) {
		      String lockedReportEditor = rr.getCurrentFileEditor();
		      int lockedReportUserID = rr.getCurrentEditorID();
		      
		      User lockedReportUser = User.getUser(lockedReportUserID);
		      if(lockedReportUser.isAdmin()) {
			  lockedReportUser.emailUser(rr, lockedReportEditor);
		      } else {
			  lockedReportUser.emailUser(rr);
		      }
		  }
	      }
      // Create Doc if doesn't exist, get file_id
      if(!beforePR ) {
        String peer_report = rr.getPeerRevReportName();
        String title;
        if (rr.isLP()) {
          // why isn't peer named bpp here yet? it's panName_Prop.peer for pan a or b
          title = "bpp" + "_" + rr.getProposalNumber() +".LP";
          peer_report = title;
        } else {
           title = rr.getPanelName() + "_" + rr.getProposalNumber();
        }

        File peer_file = new File(peer_report);
        File reviewer_file = new File(peer_file + "." + ReportsConstants.COMPLETE);
        File checkoff_file = new File(peer_file + "." + ReportsConstants.CHECKOFF);
        File finalize_file = new File(peer_file + "." + ReportsConstants.FINALIZE);
        // Force save report first time on report page so .peer exists for google doc creation
        if ((peer_report.endsWith("peer") || peer_report.endsWith("LP")) &&
             !peer_file.exists() && !reviewer_file.exists() &&
             !checkoff_file.exists() && !finalize_file.exists()) {
          LogMessage.println("Saving report first time at PR for panel " + rr.getPanelName()+
              " userID:"+ userID + " " + userName + " " + proposalNumber + " " + peer_file);
          if (rr.saveReport(userID, userName, reportsDataPath)) {
            String msg = "Data successfully saved.";
          } else {
            String badmsg = "Error occurred saving data. Please contact the <a href=\"/cgi-gen/up.cgi?AIMACTION=vlogin\">CXC HelpDesk</a>";
          }
        }
        if ((rr.isPeer() || rr.isSecondaryPeer() ) &&
            (rr.isCheckedOff() || rr.isFinalized()) &&
            !isAdminChairPunditDevel) {
          LogMessage.println("Not running pas_google_reports for prim or sec"
              + " since report is checked off or finalized.");
        } else {
          Map<String, String> email_roles;
          email_roles = rr.getPanelEmails();
          ArrayList<String> emails = new ArrayList<>(email_roles.keySet());
          String doc_id = rr.createDoc(title, emails, "writer", pgrPath);
          LogMessage.println("Document ID number for " + title + " is: " + doc_id);
          String googleDoc = "https://docs.google.com/document/d/" + doc_id;
          session.setAttribute("googleDoc", googleDoc);
        }
      }

	  theUser.setMode(mode); //mode is used to clarify that a chair can be a reviewer
          session.setAttribute("ichanged","0");
          session.setAttribute("proposalFileDir", proposalFileDir);
          session.setAttribute("reportsDataPath", reportsDataPath);
            session.setAttribute("guidelinesUrl", guidelinesUrl);

	  session.setAttribute("user", theUser);
	  session.setAttribute("report", rr);
	  if(theUser.isDeveloper()) {
	      timeout = new String("-1");
	  }
	  session.setAttribute("timeout", "-1");
	  session.setAttribute("pasTimeout", timeout);


	  //reset this session variable so we reload the report from the file unless this
	  //servlet is called from the view help or view print version pages
	  session.setAttribute("useSessionReport", new Boolean(false)); 
          request.setAttribute("propNum",rr.getProposalNumber());
          request.setAttribute("reportType",rr.getType());
          Integer rid = new Integer(rr.getReviewerID());
          request.setAttribute("reviewerID",rid.toString());
          String cmtPanel = rr.getPanelName();
          //LogMessage.println("GetReportServlet:  type= " + rr.getType()) ;
          if (rr.getType().equals("LP")) {
             cmtPanel = ReportsConstants.BPP_PANEL;
          }
          Notes propNotes= new Notes(rr.getProposalNumber(),userID,reportsDataPath,userName); 
          CmtEdits propCmtEdits= new CmtEdits(rr.getProposalNumber(),cmtPanel,reportsDataPath); 
          String notes = propNotes.readNotes();
          String cmtEdits = propCmtEdits.readCmtEdits();
          String cmtEditsHistory = propCmtEdits.getLastModified();
          session.setAttribute("notes",notes);
          session.setAttribute("displayNotes","on");
          session.setAttribute("cmtEdits",cmtEdits);
          session.setAttribute("cmtEditsHistory",cmtEditsHistory);
          session.setAttribute("displayCmtEdits","on");
	  
	  LogMessage.println("Leaving GetReportServlet:  " + rid.toString());
	  //Forward request to the display report jsp
	  RequestDispatcher dispatcher = null;
	  dispatcher = getServletContext().getRequestDispatcher("/displayReport.jsp");
	  dispatcher.forward(request, response);
        } else {
	  //Invalid user - requested page incorrectly
	  //send user back to the login page
          Integer tInt = (Integer)reportsID;
	  LogMessage.println("GetReportServlet: Not a valid user for " + tInt.toString());
	  //send user back to the login page
	  response.sendRedirect(startPageURL);

        }
       } catch (Exception exc) {
          LogMessage.printException(exc);
	  LogMessage.println("GetReportServlet: Unexpected exception");
	  response.sendRedirect(startPageURL);
       }
      
      } else {
	  //Invalid user - requested page incorrectly
          Integer tInt = (Integer)reportsID;
	  LogMessage.println("GetReportServlet: Not a valid user for " + tInt.toString());

	  //send user back to the login page
	  response.sendRedirect(startPageURL);

      
      }

  }


}

