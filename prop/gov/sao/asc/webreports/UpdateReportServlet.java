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
// UpdateReportServlet
// This class handles the processing of the review form, when the
// user has clicked a button on the report.

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
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
import info.User;
import ascds.LogMessage;
import info.Reports;
import info.ReportsConstants;
import info.ReviewReport;
import info.CmtEdits;
import org.apache.commons.lang3.*;


/******************************************************************************/
/**
 */

public class UpdateReportServlet extends HttpServlet {

  private static final long serialVersionUID = 1;
  private Properties reportsProperties;

  /****************************************************************************/
  /**
   * Handle a GET request from a browser --- simply initialize validation state.
   *
   * @param request  The request object as passed in by the browser.
   * @param response The response object that will be passed back to the browser.
   */

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    service(request, response);

  }

  /****************************************************************************/
  /**
   * Handle a POST request from a browser --- simply initialize validation state.
   *
   * @param request  The request object as passed in by the browser.
   * @param response The response object that will be passed back to the browser.
   */

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    service(request, response);
  }

  /****************************************************************************/
  /**
   * Handle the INIT operation.  This operation is invoked by the servlet engine when it starts up.
   * Set the tookit properties if they have not already been set.  This includes the reports data
   * path.
   *
   * @param config The configuration object established by the servlet engine.
   */

  public void init(ServletConfig config) throws ServletException {
    ServletContext context = config.getServletContext();
    super.init(config);

    reportsProperties = Reports.getProperties(context);
  }

  /****************************************************************************/
  /**
   * Handle a submission from a browser.  Five operations are provided:
   *
   * @param request  The request object as passed in by the browser.
   * @param response The response object that will be passed back to the browser.
   */

  public void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    LogMessage.println("---- Entering UpdateReportServlet ----");
    boolean beforePR = true;
    boolean newStatus = false;

    //Reload properties
    reportsProperties = Reports.getProperties();
    String timeout = reportsProperties.getProperty("pas.session.timeout");

    String operation = request.getParameter("operation");
    String startPageURL = reportsProperties.getProperty("reports.start.url");
    String reportsDataPath = reportsProperties.getProperty("reports.data.path");
    String proposalFileDir = reportsProperties.getProperty("proposal.file.dir");


    HttpSession session = request.getSession(false);
    int validUser = ReportsConstants.INVALIDENTRY;
    String msg = new String("");

    int reportsID = -1;
    if (session != null) {
      Integer reportsIDInt = (Integer) session.getAttribute("reportsID");
      if (reportsIDInt != null) {
        reportsID = reportsIDInt.intValue();
        validUser = User.isValidUser(reportsID);
      }
      //DEBUG
/* Don't need this
 Enumeration sesvar = session.getAttributeNames();
 while(sesvar.hasMoreElements()) {
            String sesname = (String)sesvar.nextElement();
            LogMessage.println("UpdateReportsServlet: " + sesname + " = " + session.getAttribute(sesname));
 }
*/

    } else {
      LogMessage.println("UpdateReportServlet: Session is null  ");
    }

    if (validUser == ReportsConstants.VALIDENTRY) {
      String userName = request.getParameter("userName");
      LogMessage
          .println("UpdateReportServlet service: operation = " + operation + " for " + userName);

      Boolean beforePRBool = (Boolean) session.getAttribute("beforePR");
      Boolean isAnonymous = (Boolean)session.getAttribute("isAnonymous");
      if (isAnonymous == null) isAnonymous = new Boolean(true);
      if (beforePRBool != null) {
        beforePR = beforePRBool.booleanValue();
      }

      session.setAttribute("message", msg);  // init message string
      session.setAttribute("proposalFileDir", proposalFileDir);
      session.setAttribute("reportsDataPath", reportsDataPath);
      session.setAttribute("ichanged", "0");

      User theUser = (User) session.getAttribute("user");
      if (theUser.isDeveloper()) {
        timeout = new String("-1");
      }
      session.setAttribute("timeout", "-1");
      session.setAttribute("pasTimeout", timeout);

      String displayNotes = (String) request.getParameter("DisplayNotes");
      session.setAttribute("displayNotes", displayNotes);
      String notes = "";
      session.setAttribute("notes", notes);
      String displayCmtEdits = (String) request.getParameter("DisplayCmtEdits");
      session.setAttribute("displayCmtEdits", displayCmtEdits);
      String cmtedits = "";
      session.setAttribute("cmtedits", cmtedits);

      if (operation == null) {
        LogMessage.println("UpdateReportServlet::service - Error, no operation defined");
      } else if (operation.equals(ReportsConstants.TIMEDOUTREP)) {
        //If the user has timed out while entering information, they don't want
        //to lose it.  So save the data to a file, and let the user reload it
        //the next time it's viewed.
        saveTimedOutReport(request, response);
      } else if (operation.equals(ReportsConstants.SAVE) ||
          operation.equals(ReportsConstants.UNCOMPLETE) ||
          operation.equals(ReportsConstants.SAVECMTEDITS) ||
          operation.equals(ReportsConstants.SAVENOTES)) {
        saveReport(request, response, beforePR, newStatus,
            operation, displayNotes, displayCmtEdits, theUser, session);
      } else if (operation.equals(ReportsConstants.REASSIGN)) {
        String proposalNumber = request.getParameter("propNum");
        String rptType = request.getParameter("reportType");
        session.setAttribute("reassignProp", proposalNumber);
        RequestDispatcher dispatcher = null;
        dispatcher = getServletContext().getRequestDispatcher(
            ("/reassignReport.jsp?operation=" + operation + "&reassignProp=" + proposalNumber
                + "&reassignType=" + rptType));
        dispatcher.forward(request, response);

      } else if (operation.equals(ReportsConstants.UNLOCK)) {
        LogMessage.println("UpdateReportServlet: Admin user requesting to unlock file");
        ReviewReport rr = createReport(request, newStatus);
        rr.unlock();

        //Need to re-read this report from the file
        boolean loadedReport = rr.loadReport(userName, reportsDataPath, proposalFileDir, beforePR);
        session.setAttribute("report", rr);

        RequestDispatcher dispatcher = null;
        dispatcher = getServletContext().getRequestDispatcher("/displayReport.jsp");
        dispatcher.forward(request, response);

      } else if (operation.equals(ReportsConstants.NAMEUNLOCK)) {
        //This feature is used by the facilitator, to unlock a report
        //by the lock filename.  In this case, we don't need to update
        //any actual report information, since the facilitator won't be
        //viewing the report
        int reportIndex = Integer.parseInt(request.getParameter("reportIndex"));
        Vector reportsList = (Vector) session.getAttribute("reportsList");
        ReviewReport rr = (ReviewReport) reportsList.get(reportIndex);
        rr.unlock();

        LogMessage.println("UpdateReportServlet: User " + userName + " unlocking report " + rr
            .getProposalNumber());

        RequestDispatcher dispatcher = null;
        dispatcher = getServletContext().getRequestDispatcher("/displayFacilitatorView.jsp");
        dispatcher.forward(request, response);
      } else if (operation.equals(ReportsConstants.LISTPROPOSALS)) {
        listProposals(request, response);
      } else if (operation.equals(ReportsConstants.PRINTVERSION)) {
        // With GDoc, no need for different print-versions for disabled/able as was done previously
        ReviewReport rr = null;
        LogMessage.println(
            "UpdateReportServlet: Display session version of review report for " + userName);
        rr = (ReviewReport) session.getAttribute("report");


        Vector<ReviewReport> printList = new Vector<ReviewReport>();
        printList.addElement(rr);
        session.setAttribute("printList", printList);
        session.setAttribute("useSessionReport", new Boolean(true));

        RequestDispatcher dispatcher = null;
        dispatcher = getServletContext().getRequestDispatcher("/displayPrintVersion.jsp");
        dispatcher.forward(request, response);

      } else {
        LogMessage.println("UpdateReportServlet: Invalid operation from report page" + operation);
      }
    } else {
      //If there is a user ID, print it to the log file
      String userIDStr = request.getParameter("userID");
      int userID = -1;

      if (userIDStr != null) {
        userID = Integer.parseInt(userIDStr);

        LogMessage
            .println("UpdateReportServlet: User " + userID + " being redirected to start page");
      }

      //User hasn't entered here correctly
      response.sendRedirect(startPageURL);
    }

  }


  /*
   * listProposals
   *
   * This routine will unlock any lock file, and redirect the user to the list of proposals
   */
  private void listProposals(HttpServletRequest request, HttpServletResponse response) {
    boolean newStatus = false;
    //Redirect user to the list of proposal page. The page will
    //depend on if the user is a reviewer or chair/admin
    String mode = request.getParameter("userMode");
    int userID = Integer.parseInt(request.getParameter("userID"));
    String userType = request.getParameter("userType");
    String userName = request.getParameter("userName");

    //Create the report, and write it to a file, just in case the user
    //made changes but didn't click the save button.
    ReviewReport rr = createReport(request, newStatus);
    String reportsDataPath = reportsProperties.getProperty("reports.data.path");
    rr.writeUnsavedReport(ReportsConstants.UNSAVED, reportsDataPath, userID, userName);
    rr.unlock(userName);

    String idPar = new String("userID=" + userID);
    String namePar = new String("userName=" + userName);
    String typePar = new String("userType=" + userType);
    String params = idPar + "&" + namePar + "&" + typePar;

    String reviewerURL = new String("/reviewReports.jsp?");
    reviewerURL += params;

    String chairURL = new String("/viewPanel?");
    String reviewerOption = request.getParameter("reviewerOptions");
    chairURL += params;
    if (reviewerOption != null && reviewerOption.length() > 2) {
      chairURL += "&reviewerOptions=" + reviewerOption;
    }

    String adminURL = new String("/viewPanel?panelName=");
    String panelName = request.getParameter("panelName");
    adminURL += panelName;
    if (reviewerOption != null && reviewerOption.length() > 2) {
      adminURL += "&reviewerOptions=" + reviewerOption;
    }

    if (rr.isLP()) {
      adminURL = new String("/viewLP");
      chairURL = new String("/viewLP");
      reviewerURL = new String("/viewLP?access=edit");

      HttpSession session = request.getSession(false);
      User theUser = (User) session.getAttribute("user");
      Boolean editReportsBool = (Boolean) session.getAttribute("editLPReports");
      if (editReportsBool != null && editReportsBool.booleanValue()) {
        chairURL = new String("/viewLP?access=edit");
      }
      if (theUser.isAllowedToEdit()) {
        adminURL = new String("/viewLP?access=edit");
      }
    }

    LogMessage.print("UpdateReportServlet: listProposals: " + params);
    try {
      RequestDispatcher dispatcher = null;
      if (mode.equals(ReportsConstants.REVIEWER)) {
        LogMessage.println(
            "UpdateReportServlet: Sending redirect to: " + reviewerURL + "for " + userName);
        dispatcher = getServletContext().getRequestDispatcher(reviewerURL);
        dispatcher.forward(request, response);
      } else if (mode.equals(ReportsConstants.CHAIR) ||
          mode.equals(ReportsConstants.PUNDIT)) {
        LogMessage
            .println("UpdateReportServlet: Sending redirect to: " + chairURL + "for " + userName);
        dispatcher = getServletContext().getRequestDispatcher(chairURL);
        dispatcher.forward(request, response);
      } else if (mode.equals(ReportsConstants.ADMIN) ||
          mode.equals(ReportsConstants.DEVELOPER)) {
        LogMessage
            .println("UpdateReportServlet: Sending redirect to: " + adminURL + "for " + userName);
        dispatcher = getServletContext().getRequestDispatcher(adminURL);
        dispatcher.forward(request, response);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      LogMessage.println("UpdateReportServlet: Caught exception in listProposals routine:");
      LogMessage.println(ex.getMessage());
    }
  }

  private String getascii(String istr) {
    String cstr = istr;
    int i = 1;

    if (cstr == null)
      cstr = "";
    //LogMessage.println("input: " + istr);
    try {
      // these seem to be the most common of utf-8 to ascii
      // the proposal review code would need to be updated to accept utf-8
      cstr = StringEscapeUtils.unescapeHtml4(cstr);
      cstr = cstr.replaceAll("\u0085", "...");
      cstr = cstr.replaceAll("\u0091", "\'");
      cstr = cstr.replaceAll("\u0092", "\'");
      cstr = cstr.replaceAll("\u0093", "\"");
      cstr = cstr.replaceAll("\u0094", "\"");
      cstr = cstr.replaceAll("\u0095", "o ");
      cstr = cstr.replaceAll("\u0096", "-");
      cstr = cstr.replaceAll("\u0097", "-");
      cstr = cstr.replaceAll("\u0098", "~");
      cstr = cstr.replaceAll("\u00b0", "deg");
      cstr = cstr.replaceAll("\u00b2", "^2");
      cstr = cstr.replaceAll("\u00b3", "^3");
      cstr = cstr.replaceAll("\u00b9", "^1");
      cstr = cstr.replaceAll("\u00bc", "1/4");
      cstr = cstr.replaceAll("\u00bd", "1/2");
      cstr = cstr.replaceAll("\u00be", "3/4");
      cstr = cstr.replaceAll("\u00b1", "+/-");
      cstr = cstr.replaceAll("\u00f7", "/");
      cstr = cstr.replaceAll("\u2070", "^0");
      cstr = cstr.replaceAll("\u2071", "^1");
      cstr = cstr.replaceAll("\u2072", "^2");
      cstr = cstr.replaceAll("\u2073", "^3");
      cstr = cstr.replaceAll("\u2074", "^4");
      cstr = cstr.replaceAll("\u2075", "^5");
      cstr = cstr.replaceAll("\u2076", "^6");
      cstr = cstr.replaceAll("\u2077", "^7");
      cstr = cstr.replaceAll("\u2078", "^8");
      cstr = cstr.replaceAll("\u2079", "^9");
      cstr = cstr.replaceAll("\\^(\\d)\\^(\\d)", "^$1$2");

      cstr = StringUtils.stripAccents(cstr);
      cstr = cstr.replaceAll("[^\\p{ASCII}]", "??");

    } catch (Exception exc) {
      LogMessage.printException(exc);
      LogMessage.println("failed for: " + cstr);
    }

    //LogMessage.println("out: " + cstr);

    return cstr;
  }


  /*
   * createReport
   * This routine takes a proposal number and creates a report object,
   * based on the form data.  The routine is used by the saveReport, checkoffReport
   * and the completeReport functions, to create the ReviewReport object with
   * the appropriate values filled in.
   */
  private ReviewReport createReport(HttpServletRequest request, boolean newStatus) {
    String proposalNumber = request.getParameter("propNum");
    String reportType = request.getParameter("reportType");
    int reviewerID = Integer.parseInt(request.getParameter("reviewerID"));
    String reportsDataPath = reportsProperties.getProperty("reports.data.path");
    String operation = request.getParameter("operation");

    newStatus = false;
    ReviewReport rr = new ReviewReport(proposalNumber);
    rr.setDataPath(reportsDataPath);
    rr.setType(reportType);
    if (rr.isLP()) {
      rr.setLPReportLinks(reportsDataPath, proposalNumber);
    }
    //set the reviewer id which is needed for the filename
    rr.setReviewerID(reviewerID);
    rr.setPanelName(request.getParameter("panelName"));
    rr.determineStatus(reportsDataPath);
    String oldStatus = rr.getReportStatus();
    if (operation.equals(ReportsConstants.UNCOMPLETE)) {
      newStatus = true;
    } else {
      rr.setReportStatus(request.getParameter("reportStatus"));
    }
    if (oldStatus.length() > 2 &&
        !oldStatus.equals(rr.getReportStatus()) &&
        !rr.getReportStatus().equals(ReportsConstants.SAVE)) {
      newStatus = true;
    }
    if (oldStatus.length() < 2 &&
        !rr.getReportStatus().equals(ReportsConstants.SAVE)) {
      newStatus = true;
    }

    LogMessage.println("NEWSTAT for " + proposalNumber + ": oldStatus=" + oldStatus + " rpt=" + rr
        .getReportStatus() + " isNewStatus=" + newStatus + " operation="+ operation + " ----");

    //Save the information to the review report
    rr.setCategory(request.getParameter("category"));
    rr.setPI(request.getParameter("piName"));
    rr.setTitle(request.getParameter("title"));
    rr.setProposalType(request.getParameter("proposalType"));
    rr.setConstrainedTargets(request.getParameter("constrainedTargs"));
    rr.setProposalJoint(request.getParameter("joint"));
    rr.setProposalMulticycle(request.getParameter("multicycle"));
    rr.setPrimaryReviewerName(request.getParameter("priLast"));
    rr.setSecondaryReviewerName(request.getParameter("secLast"));
    //LogMessage.println("RR: " + request.getParameter("scienceImportance"));

    //Set the values of the report itself
    rr.setPrelimComplete(request.getParameter("preComp"));
    rr.setSI(request.getParameter("scienceImportance"));
    rr.setSJ(request.getParameter("scienceJustification"));
    rr.setFmtClarity(request.getParameter("fmtClarity"));
    rr.setFeasibility(request.getParameter("feasibility"));
    rr.setFeasibilityConstraint(request.getParameter("feasibilityConstraint"));
    rr.setChandraUse(request.getParameter("useOfChandra"));
    rr.setClarity(request.getParameter("clarity"));

    rr.setTargetsTaken(request.getParameter(ReportsConstants.HIGHERRANKED));
    rr.setTOOLimited(request.getParameter(ReportsConstants.TOO));
    rr.setConstrained(request.getParameter(ReportsConstants.CONSTRAINED));

    String cstr = getascii(request.getParameter("comments"));
    rr.setComments(cstr);

    cstr = getascii(request.getParameter("specificRecs"));
    rr.setRecs(cstr);

    cstr = getascii(request.getParameter("whyGradeNotHigher"));
    rr.setGradeReason(cstr);

    cstr = getascii(request.getParameter("technicalReview"));
    rr.setTechnicalReview(cstr);

    cstr = getascii(request.getParameter("effort"));
    rr.setEffort(cstr);

    rr.initLock();

    return rr;
  }


  private void saveReport(HttpServletRequest request,
      HttpServletResponse response, boolean beforePR, boolean newStatus,
      String operation, String displayNotes, String displayCmtEdits, User theUser,
      HttpSession session) {
    try {
      int reviewerID = Integer.parseInt(request.getParameter("reviewerID"));
      int userID = Integer.parseInt(request.getParameter("userID"));
      String proposalNumber = request.getParameter("propNum");
      String userType = request.getParameter("userType");
      String userMode = request.getParameter("userMode");
      String userName = request.getParameter("userName");
      String panelName = request.getParameter("panelName");
      String notes = request.getParameter("notes");
      String cmtEdits = request.getParameter("cmtEdits");
      String rptType = request.getParameter("reportType");
      String reportsDataPath = reportsProperties.getProperty("reports.data.path");
      String proposalFileDir = reportsProperties.getProperty("proposal.file.dir");
      String pgrPath = reportsProperties.getProperty("pgr.path");
      String ichanged = request.getParameter("ichanged");
      String cmtPanel = panelName;
      if (rptType.equals("LP")) {
        cmtPanel = ReportsConstants.BPP_PANEL;
      }
      String msg = new String("");
      String badmsg = new String("");
      CmtEdits theCmtEdits = new CmtEdits(proposalNumber, cmtPanel, reportsDataPath);

      if (operation.equals(ReportsConstants.SAVENOTES) || operation.equals(ReportsConstants.SAVE)) {
        try {
          // TODO OC-210 Removed capability to save notes. remove savenotes infrastructure?
          if (operation.equals(ReportsConstants.SAVENOTES))
            session.setAttribute("ichanged", ichanged);
          msg = "Notes successfully saved.";
        } catch (Exception exc) {
          badmsg += "Unable to save notes for " + proposalNumber + "<br>";
          LogMessage.println("Unable to save notes for " + proposalNumber + " : " + userID);
        }
      }
      if (operation.equals(ReportsConstants.SAVECMTEDITS) || operation
          .equals(ReportsConstants.SAVE)) {
        try {
          LogMessage
              .println("Trying to save comment edits for " + proposalNumber + " : " + userName);
          theCmtEdits.saveCmtEdits(cmtEdits, userName);
          msg = "Comment edit inputs successfully saved." + "";
          if (operation.equals(ReportsConstants.SAVECMTEDITS))
            session.setAttribute("ichanged", ichanged);
        } catch (Exception exc) {
          badmsg += "Unable to save comment edits for " + proposalNumber + "<br>";
          LogMessage.println("Unable to save comment edits for " + proposalNumber + " : " + userID);
        }
      }
      String cmtEditsHistory = theCmtEdits.getLastModified();

      ReviewReport rr = createReport(request, newStatus);
      String current_status = rr.getReportStatus();
      String title;
      if (rr.isLP()) {
        title = "bpp" + "_" + rr.getProposalNumber() +".LP";
      } else {
        title = rr.getPanelName() + "_" + rr.getProposalNumber();
      }

      //Save the report
      rr.setReviewerID(reviewerID); //set the reviewer id which is needed for the filename
      //pass user id so we know who edited the file
      if (operation.equals(ReportsConstants.UNCOMPLETE)) {

        // Update Google Doc permissions
        if (current_status.equals(ReportsConstants.COMPLETE) ||
            current_status.equals(ReportsConstants.CHECKOFF)) {
          boolean skipChairs = false;
          boolean skipPrimSec = false;
          if (current_status.equals(ReportsConstants.COMPLETE)){
            skipChairs = true;
          } else {
            // CHECKOFF is for chairs clicking complete, prim/sec maintain reader access
            skipPrimSec = true;
          }
          Map<String, String> email_roles = new HashMap<>();
          email_roles = rr.getPanelEmails(skipChairs, skipPrimSec);
          ArrayList<String> emails = new ArrayList<>(email_roles.keySet());
          rr.updateDocPermissions(title, "writer", emails, pgrPath);
        }

        rr.loadReport(userName, reportsDataPath, proposalFileDir, beforePR, false);
//LogMessage.println("loaded report");
        // if (rr.getReportStatus().equals(ReportsConstants.COMPLETE))
        {
          rr.setReportStatus(request.getParameter("reportStatus"));
          rr.setPrelimComplete(request.getParameter("prelimComp"));
          if (rr.saveReport(userID, userName, reportsDataPath)) {
          } else {
            badmsg += "Error occurred saving data. Please contact the <a href=\"/cgi-gen/up.cgi?AIMACTION=vlogin\">CXC HelpDesk</a>";
          }
        }
        //else {
        //msg = "Report is no longer available for editing.\n";
        //}
      } else if (operation.equals(ReportsConstants.SAVE)) {
        Map<String, String> email_roles;
        String getStatus = request.getParameter("reportStatus");
        if (rr.saveReport(userID, userName, reportsDataPath)) {
          msg = "Data successfully saved.";
        } else {
          badmsg += "Error occurred saving data. Please contact the <a href=\"/cgi-gen/up.cgi?AIMACTION=vlogin\">CXC HelpDesk</a>";
        }
        // Update Google Doc permissions and merge Doc into report file
        if (!beforePR) {
          rr.updateDoc(title, pgrPath);
          boolean skipChairs = false;
          boolean skipPrimSec = false;
          if (!(theUser.isAdmin() || theUser.isDeveloper()) &&
              (current_status.equals(ReportsConstants.COMPLETE) ||
                  current_status.equals(ReportsConstants.CHECKOFF))) {
            if (current_status.equals(ReportsConstants.COMPLETE)) {
              skipChairs = true;
            } else {
              skipPrimSec = true;
            }
            email_roles = rr.getPanelEmails(skipChairs, skipPrimSec);
            ArrayList<String> emails = new ArrayList<>(email_roles.keySet());
            rr.updateDocPermissions(title, "reader", emails, pgrPath);
          }
          // admin is  the only account that uses save to complete AND uncomplete, handle separately
          else if (theUser.isAdmin() || theUser.isDeveloper()) {
            if (current_status.equals(ReportsConstants.FINALIZE) ||
                current_status.equals(ReportsConstants.CHECKOFF)) {
              // chair/dep: read, prim/sec: read
              email_roles = rr.getPanelEmails();
              ArrayList<String> emails = new ArrayList<>(email_roles.keySet());
              rr.updateDocPermissions(title, "reader", emails, pgrPath);
            } else if (current_status.equals(ReportsConstants.COMPLETE)) {
              // chair/dep: write, prim/sec: read
              // Since mix of permissions, need to assign all roles.
              email_roles = rr.getPanelEmails();
              ArrayList<String> roles = new ArrayList<>();
              for (String type : email_roles.values()) {
                if (type.equals(ReportsConstants.CHAIR) ||
                    type.equals(ReportsConstants.DEPUTYCHAIR) ||
                    type.equals(ReportsConstants.PUNDITCHAIR) ||
                    type.equals(ReportsConstants.PUNDITDEPUTY)) {
                  roles.add("writer");
                } else {
                  roles.add("reader");
                }
              }
              ArrayList<String> emails = new ArrayList<>(email_roles.keySet());
              rr.updateDocPermissions(title, roles, emails, pgrPath);
            }
            // CDO sets back to reviewer can edit, ReportsConstants.SAVE
            else {
              // chair/dep: write, prim/sec: write
              email_roles = rr.getPanelEmails();
              ArrayList<String> emails = new ArrayList<>(email_roles.keySet());
              rr.updateDocPermissions(title, "writer", emails, pgrPath);
            }
          }
        }
      }
      if (badmsg.length() > 2) {
        msg += "<br>" + badmsg;
      }

      // if the status has changed to a completed type of state we want
      // to redirect the user back to the list of proposals.  
      if ((operation.equals(ReportsConstants.SAVE) && newStatus)) {
        listProposals(request, response);
      } else {

        // Set values in session      
        session.setAttribute("report", rr);

        //Determine if this is before peer review
        session.setAttribute("beforePR", new Boolean(false));
        session.setAttribute("message", msg);
        if (Reports.beforePeerReview()) {
          session.setAttribute("beforePR", new Boolean(true));
        }
        session.setAttribute("notes", notes);
        session.setAttribute("cmtEdits", cmtEdits);
        session.setAttribute("cmtEditsHistory", cmtEditsHistory);

        //Send user back to the report
        try {
          LogMessage.println("UpdateReportServlet: send user back to report " + userName);
          RequestDispatcher dispatcher = null;
          dispatcher = getServletContext().getRequestDispatcher("/displayReport.jsp");
          dispatcher.forward(request, response);
        } catch (Exception ex) {
          LogMessage.println(ex.getMessage());
          LogMessage.println("In UpdateReportServlet, caught exception.");
        }
      }

    } catch (Exception ex) {
      //somebody is doing something bad
      LogMessage.printException(ex);
    }
  }


  /**
   * saveTimedOutReport This routine will save the report if a user has timed out. The user will
   * have a chance to "recover" this report when they login the next time.
   *
   * @param request  HTTP request servlet
   * @param response HTTP response servlet
   */
  private void saveTimedOutReport(HttpServletRequest request, HttpServletResponse response) {
    String reportsDataPath = reportsProperties.getProperty("reports.data.path");
    boolean newStatus = false;
    //If there is a user ID, print it to the log file
    String userIDStr = request.getParameter("userID");
    if (userIDStr != null) {
      String mode = request.getParameter("userMode");
      int userID = Integer.parseInt(request.getParameter("userID"));
      String userType = request.getParameter("userType");
      String userName = request.getParameter("userName");
      LogMessage.println("User " + userID + " being redirected to start page");

      //System.err.println("Trying to remove lock in UpdateServlet: " + lockFilename + " : user = " + userName);

      //Create the report, and write it to a file, just in case the user
      //made changes but timed out
      ReviewReport rr = createReport(request, newStatus);
      rr.writeUnsavedReport(ReportsConstants.TIMEDOUTREP, reportsDataPath, userID, userName);
      rr.unlock(userName);
      //System.err.println("Trying to remove lock in UpdateServlet: " + lockFilename + " : user = " + userName);

    }

    HttpSession session = request.getSession(false);
    session.setAttribute("userTimedOut", new Boolean(true));
    try {
      RequestDispatcher dispatcher = null;
      dispatcher = getServletContext().getRequestDispatcher("/reportsLogout");
      dispatcher.forward(request, response);
    } catch (Exception ex) {
      LogMessage
          .println("In UpdateReportServlet:saveTimedOutReport:Caught exception:" + ex.getMessage());

    }

  }
}

