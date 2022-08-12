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
// ViewGradesSummary

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
import java.util.ArrayList;
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
import info.Reports;
import info.ReportsConstants;
import db.DBConnection;
import ascds.LogMessage;
import ascds.RunCommand;

/******************************************************************************/
/**
 */

public class ViewGradesSummary extends HttpServlet 
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

      //Reload properties
      reportsProperties = Reports.getProperties();
      String timeout = reportsProperties.getProperty("reports.session.timeout");
      if(timeout != null) {
	  User.setTimeoutPeriod(Integer.parseInt(timeout));
      }
      
    // Get the session object.
    HttpSession session = request.getSession(false );
    String startPageURL = reportsProperties.getProperty("reports.start.url");
    RequestDispatcher dispatcher = null;
    int validUser = ReportsConstants.INVALIDENTRY;
    boolean caughtError = false;
    int userID = -1;
    String userType = null;
    User theUser = null;
    Vector memberList = null;
    String theFile = null;

    LogMessage.println("Entering ViewGradesSummary");
    //Make sure the user has requested the page through the CDO site and 
    //is logged in

    if(session != null) {
	Integer reportsID = ((Integer)session.getAttribute("reportsID"));
        if (reportsID != null) {
	  validUser = User.isValidUser(reportsID.intValue());
        }

	theUser = (User)session.getAttribute("user");
	if(theUser == null) {
	    LogMessage.println("ViewGradesSummary: session exists, but no user object");
	    validUser = ReportsConstants.INVALIDENTRY;
	} 
        else if (theUser.getType() == null || 
            (!theUser.isAdmin() && !theUser.isDeveloper()) ) {
	    LogMessage.println("ViewGradesSummary: invalid user type ");
	    validUser = ReportsConstants.INVALIDENTRY;
        }
    }

    if(validUser != ReportsConstants.VALIDENTRY) {
	LogMessage.println("ViewGradesSummary: Not a valid user");
	
	if(session != null) {
	    session.invalidate();
	}
	
	//Send user back to the start page
	response.sendRedirect(startPageURL);

    } else {
	//Valid user has entered the page
	String reportsDataPath = reportsProperties.getProperty("reports.data.path");
	if (reportsDataPath == null) {
	    LogMessage.println("ViewGradesSummary:reports path is null");
	}

//make sure user is admin or developer?
	boolean showDebug = true;
        String ftype = request.getParameter("ftype");
        String fpanel = request.getParameter("panel");

	try {
          //Generate the files listing the preliminary grades by proposal and
          //the primary and secondary reviewers preliminary grades for each
          //proposal
          String prelimGradesScriptPath = reportsProperties.getProperty("web.bin.directory");
          String prelimGradesScript = new String(prelimGradesScriptPath + "/prop_prelim_grades.pl");
          
          String gradesDirectory = new String(reportsDataPath + "/grades");
          String outputDirectory = new String(gradesDirectory);
          String scriptOptions = new String(" -w " + reportsDataPath + 
		" -d " + gradesDirectory + " -o " + outputDirectory);
          //The file with the status of the grades
          if (ftype.equals("conflict") ) {
             prelimGradesScript = new String(prelimGradesScriptPath + "/prop_pre_conflicts.pl");
             scriptOptions += "/pre_conflicts.txt";
             theFile = outputDirectory + "/pre_conflicts.txt";
          }
          else if (ftype.equals("status") ) {
            String accessDateFile  = reportsProperties.getProperty("reports.access.date.file");
            scriptOptions += " -t " + accessDateFile;
            theFile = new String(gradesDirectory);
            theFile += "/prelim_grades_status.txt";
            scriptOptions += " -x";
          }
	  else if (ftype.indexOf("panel") >= 0 ) {
            theFile = new String(gradesDirectory);
           
            theFile += "/panel" + fpanel + "_prelim_grades.txt";
            scriptOptions += " -x";
          }
          else {
            //This a file that lists the preliminary grade given
            //by the primary and secondary reviewer for each proposal
            theFile = reportsProperties.getProperty("reports.prelim.grades");
            scriptOptions += " -y";
          }
   
          ArrayList<String> envVarList = new ArrayList<String>();
	  String envStr = "ASCDS_BIN=" + prelimGradesScriptPath;
          envVarList.add(envStr);

          String scriptCommand = new String(prelimGradesScript + scriptOptions);
          LogMessage.println("Running script: " + scriptCommand);

          
          RunCommand rc = new RunCommand(scriptCommand,envVarList,null);
          LogMessage.println(rc.getOutMsg());
          LogMessage.println(rc.getErrMsg());

	} catch (Exception e) {
	    caughtError = true;
            e.printStackTrace();
	    LogMessage.println("ViewGradesSummary:service routine - Caught exception ");
	}


	//Forward request to the jsp to display the resulting page
        session.setAttribute("proposalFileDir",theFile);
        String url = "/displayFile.jsp?fileName=adminGrades";
	dispatcher = getServletContext().getRequestDispatcher(url );
	dispatcher.forward(request, response);
     }
	
  }


}

