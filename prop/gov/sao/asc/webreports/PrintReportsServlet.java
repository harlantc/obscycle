/*
  Copyrights:
 
  Copyright (c) 2000-2019 Smithsonian Astrophysical Observatory
 
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
// PrintReportServlet
// This class will handle the request from a list of proposals,
// to display the review report for a single proposal, with
// the appropriate links to the supporting documents 
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileWriter;
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
import info.ReportsConstants;
import info.ReviewReport;
import info.Reports;
import ascds.LogMessage;

/******************************************************************************/
/**
 */

public class PrintReportsServlet extends HttpServlet 
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

      //Reload properties
      reportsProperties = Reports.getProperties();
      String timeout = reportsProperties.getProperty("reports.session.timeout");
      if(timeout != null) {
	  User.setTimeoutPeriod(Integer.parseInt(timeout));
      }

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
      }

      //Only process request if the user has entered the site properly
      if(validUser == ReportsConstants.VALIDENTRY) {
	  int reviewerID = Integer.parseInt(request.getParameter("reviewerID"));
	  String printType = request.getParameter("printType");
	  User theUser = (User)session.getAttribute("user");
	  Boolean beforePRBool = (Boolean)session.getAttribute("beforePR");
	  boolean beforePR = true;
	  if(beforePRBool != null) {
	      beforePR = beforePRBool.booleanValue();
	  }
          Vector reportsList = (Vector)session.getAttribute("reportsList");
          Vector<ReviewReport> printList = new Vector<ReviewReport>();

	  String reportsDataPath = reportsProperties.getProperty("reports.data.path");
	  String propFileDir = reportsProperties.getProperty("proposal.file.dir");

	  //for all the proposal numbers for this user
          //LogMessage.println("***Print All for " + printType + "  reviewer: " + reviewerID );
          LogMessage.println("***Print All for " + printType + "  reviewer: " + reviewerID  + " size=" + reportsList.size());
          for (int ii=0;ii<reportsList.size();ii++) {
	      ReviewReport rr = (ReviewReport) reportsList.get(ii);
	      boolean loadedReport = false;
              // match on reviewer id for primary or secondary, then print
              // report for right type
              
              if ((printType.equals(ReportsConstants.FINAL) && !beforePR) ||
                  (printType.equals(ReportsConstants.LP) && !beforePR) ||
                  (reviewerID == rr.getReviewerID() &&
                   printType.equals(rr.getType()))  ||
                  (reviewerID != rr.getReviewerID() &&
                   printType.equals(ReportsConstants.SECONDARYPEER) )) {
         
                rr.setPrintOnly(true);
                loadedReport= rr.loadReport("printUser",reportsDataPath,propFileDir,beforePR);
                rr.setPrintOnly(false);
                if (loadedReport) {
                  if (rr.getType().equals(ReportsConstants.SECONDARYPEER)) {
                    rr.setType(ReportsConstants.PEER);
                  }
                  printList.addElement(rr);
                }
              }
              else {
                //LogMessage.println("Skipping " + rr.getProposalNumber() + "   reviewerid=" + rr.getReviewerID() + "  type " + rr.getType());
              }
	  }
	  //session.setAttribute("printViewList", tempReportsFilename);
          LogMessage.println("***PrintList " + printList.size());
	  session.setAttribute("printList", printList);
          // for the case of no primary or no secondary reports for current user
	  session.setAttribute("report", null);


	  //Redirect user
	  RequestDispatcher dispatcher = null;
	  dispatcher = getServletContext().getRequestDispatcher("/displayPrintVersion.jsp");
	  dispatcher.forward(request, response);

      } else {
	  //Invalid user - requested page incorrectly
	  LogMessage.println("PrintReportServlet: Not a valid user");

	  //send user back to the login page
	  response.sendRedirect(startPageURL);

      
      }
  }




}

