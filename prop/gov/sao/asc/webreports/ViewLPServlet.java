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
// ViewLPServlet
// This servlet class is used to process requests to view large projects/
// very large projects and TOO/LPs.

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.File;
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
import java.text.DateFormat;
import java.text.ParseException;
import info.User;
import info.Reports;
import info.ReviewReport;
import ascds.LogMessage;
import info.ReportsConstants;

/******************************************************************************/
/**
 */

public class ViewLPServlet extends HttpServlet 
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
   * they have not already been set.  This includes the reports data
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
   * Handle a submission from a browser.  
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

      String reportsDataPath = reportsProperties.getProperty("reports.data.path");      
      String bppFile = reportsProperties.getProperty("bpp.props");      
      String startPageURL = reportsProperties.getProperty("reports.start.url");      
      String proposalFileDir  = reportsProperties.getProperty("proposal.file.dir");

      String userIDStr = request.getParameter("userID");
      String panelName = request.getParameter("panelName");
      int userID = -1;
      int reportsID = -1;
      Boolean beforePRBool = null;
      boolean beforePR = true;
      int validUser = ReportsConstants.INVALIDENTRY;
      User theUser = null;
      Vector reportsList = null;
      String userType = null;

      HttpSession session = request.getSession(false);

      LogMessage.println("---- Entering ViewLPServlet ----");
      if(session != null) {
	  Integer reportsIDInt = (Integer)session.getAttribute("reportsID");
	  if(reportsIDInt != null) {
	      reportsID = reportsIDInt.intValue();
	      validUser = User.isValidUser(reportsID);
	  }
      }

      if(validUser == ReportsConstants.VALIDENTRY) {
	  beforePRBool = (Boolean)session.getAttribute("beforePR");
	  if(beforePRBool != null) 
	      beforePR = beforePRBool.booleanValue();
	  
	  theUser = (User)session.getAttribute("user");
	  userID = theUser.getUserID();
	  userType = theUser.getType();

	  boolean editLPReports = false;
	  String lpReportsAccess = request.getParameter("access");
	  if(lpReportsAccess != null && lpReportsAccess.equals("edit")) {
	      editLPReports = true;
	  }
          boolean bppForceAll = false;
          // get the session first as the default, then check the parameter
	  String bppReportsAccess = (String)session.getAttribute("bppAccess");
	  if(bppReportsAccess != null && bppReportsAccess.equals("all")) {
	    bppForceAll = true;
          }
	  bppReportsAccess = request.getParameter("bppAccess");
	  if(bppReportsAccess != null && bppReportsAccess.equals("all")) {
	    bppForceAll = true;
          }
	  else if(bppReportsAccess != null && bppReportsAccess.equals("sub")) {
	    bppForceAll = false;
          }
          if (bppForceAll) {
            session.setAttribute("bppAccess","all");
          }
          else {
            session.setAttribute("bppAccess","sub");
          }

          Boolean bppFileExists = false;
          File xfile = new File(bppFile);
          if (xfile.exists() && xfile.length()>3) {
            bppFileExists = new Boolean(true);
          }
          session.setAttribute("bppFile",bppFileExists);


	  //Get the list of reports from the db
	  reportsList = ReviewReport.getLPReports(reportsDataPath, theUser, editLPReports,bppForceAll,bppFile);
	  session.setAttribute("reportsList", reportsList);
	  session.setAttribute("editLPReports", new Boolean(editLPReports));
	  if(theUser.isDeveloper()) {
	      timeout = new String("-1");
	  }
	  session.setAttribute("timeout", timeout);
          session.setAttribute("proposalFileDir", proposalFileDir);


	  RequestDispatcher dispatcher = null;
	  dispatcher = getServletContext().getRequestDispatcher("/displayLPReports.jsp");
	  dispatcher.forward(request, response);

      } else {
	  LogMessage.println("ViewLPServlet: Not a valid user");
	  response.sendRedirect(startPageURL);
      }
  }




}

