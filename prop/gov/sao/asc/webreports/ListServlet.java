/*
  Copyrights:
 
  Copyright (c) 2014 Smithsonian Astrophysical Observatory
 
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
// ListServlet

import java.io.BufferedReader;
import java.io.FileReader;
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

public class ListServlet extends HttpServlet 
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

      LogMessage.println("----------- Entering ListServlet ------------");      
      //Reload properties
      reportsProperties = Reports.getProperties();
      String timeout = reportsProperties.getProperty("reports.session.timeout");

     Vector<String> csvHdrs = new Vector();
     Vector csvData = new Vector();
     String csvTitle = "&nbsp;";

    // Get the session object.
    HttpSession session = request.getSession(false );
    String startPageURL = reportsProperties.getProperty("reports.start.url");
    RequestDispatcher dispatcher = null;
    int validUser = ReportsConstants.INVALIDENTRY;
    boolean caughtError = false;
    String listType = request.getParameter("listType");
    boolean sortList = false;
    String csvMsg="";

    String accessDateFile  = reportsProperties.getProperty("reports.access.date.file");
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
      LogMessage.println("ListServlet: session is null");
    }


    if(validUser != ReportsConstants.VALIDENTRY) {
        Integer tInt = (Integer)reportsID;
	LogMessage.println("ListServlet: Not a valid user for " + tInt.toString());
	
	if(session != null) {
	    session.invalidate();
	}
	//Send user back to the start page
	response.sendRedirect(startPageURL);

    } else {
	//Valid user has entered the page
	String reportsDataPath = reportsProperties.getProperty("reports.data.path");
	if (reportsDataPath == null) {
	    LogMessage.println("ListServlet: reports path is null");
            csvMsg = "Unexpected error. Unable to determine reports path";
	}
	
	//get user
	User theUser = (User)session.getAttribute("user");
	Integer userID = new Integer(theUser.getUserID());
	String panelName = theUser.getPanelName();

        LogMessage.println ("ListServlet: memberType" + theUser.getMemberType() + " userType=" + theUser.getType());
        if (theUser.isAdmin() || theUser.isDeveloper()) {
          panelName = request.getParameter("panelName");
          LogMessage.println("ListServlet: Admin user for " + panelName + " looking at " + userID.toString());
        } 
	
        String fname = "";
        boolean gotFile= true;
  	boolean showDebug = false;

        if (listType != null && reportsDataPath != null) {
	  LogMessage.println("ListServlet: List Type is " + listType);

          String fullname = reportsDataPath + "/lists/";

          if (listType.equals("1")) {
            fname = "simple_panel" + panelName + ".tsv";
            csvTitle = "Simple Conflicts for Panel " + panelName; 
          } else if (listType.equals("2")) {
            fname = "cross_panel" + panelName + ".tsv";
            csvTitle = "Cross Conflicts for Panel " + panelName; 
          } else if (listType.equals("3")) {
            fname = "simple_panelbpp.tsv";
            csvTitle = "Simple Conflicts for the BPP " ;
          } else if (listType.equals("4")) {
            fname = "cross_panelbpp.tsv";
            csvTitle = "Cross Conflicts for the BPP ";
          } else if (listType.equals("6")) {
            gotFile=true;
            sortList = true;
            fname = "tc_summaries_panel" + panelName + ".tsv";
            LogMessage.println("ListServlet: found  " + fname);
            csvTitle = "Time Critical Summary for Panel " + panelName;
          } else if (listType.equals("7")) {
            gotFile=true;
            sortList = true;
            fname = "tc_summaries_panelbpp.tsv";
            LogMessage.println("ListServlet: found  " + fname);
            csvTitle = "Time Critical Summary for the BPP" ;
          } else {
            fname  = listType;
            sortList = true;
            gotFile=true;
            //csvMsg = "Error: Invalid list requested";
          }
          if (gotFile) {
	    try {
              LogMessage.println("ListServlet: " + fname);
              fullname += fname;
              File dataFile = new File(fullname);
              String[] inputArr;
              String inputLine;
              if (dataFile.exists()) {
                 LogMessage.println("ListServlet: file exists" );
                 BufferedReader fileBR = new BufferedReader(new FileReader(dataFile));
                 inputLine = fileBR.readLine();
                 if (inputLine != null) {
                   // verifying what is in the file
                   inputLine = inputLine.replaceAll("[^\\p{ASCII}]","");
                   inputArr = inputLine.split("\t");
                   for (int ii=0;ii<inputArr.length;ii++) {
                     //LogMessage.println("ListServlet: adding hdr " + inputArr[ii]);
                     csvHdrs.add(inputArr[ii]);
                   }
                 }
                 while ((inputLine = fileBR.readLine()) != null) {
                   inputArr = inputLine.split("\t");
                   Vector<String> dd = new Vector();
                   for (int ii=0;ii<inputArr.length;ii++) {
                     dd.add(inputArr[ii]);
                     // LogMessage.println("ListServlet: adding data " + inputArr[ii]);
                   }
                   csvData.add(dd);
                 }
              }
              else {
                gotFile = false;
                csvMsg = "Error: Unable to access list.";

              }
	    } catch (Exception e) {
	       caughtError = true;
	       LogMessage.println("ListServlet:service routine - Caught exception for user ID " + userID);
	       LogMessage.println("ListServlet: " + e.getMessage());
               csvMsg = "Error: Unable to read list";
	    }
	  }
       } else {
         if (listType == null) {
           csvMsg = "Error: Invalid list type";
         }
         caughtError = true;
         LogMessage.println("ListServlet: no type or data path");
       }

	//If we had an error getting the proposals on this panel, send the user
	//back to the start page.
	if(caughtError || !gotFile) {
            LogMessage.println("ListServlet error: " + csvMsg);
        }
	session.setAttribute("sortList", new Boolean(sortList));
	session.setAttribute("listType", listType);
	session.setAttribute("csvFile", fname);
	session.setAttribute("csvMsg", csvMsg);
	session.setAttribute("user", theUser);
	session.setAttribute("panel", panelName);
	session.setAttribute("csvTitle", csvTitle);
	session.setAttribute("csvHdrs", csvHdrs);
	session.setAttribute("csvData", csvData);
	session.setAttribute("reportsDataPath", reportsDataPath);
        session.setAttribute("proposalFileDir", proposalFileDir);
	session.setAttribute("backLink", "/displayList.jsp");

	if(theUser.isDeveloper()) {
	  timeout = new String("-1");
	}
	session.setAttribute("timeout", timeout);

	//Forward request to the jsp to display the resulting page
	dispatcher = getServletContext().getRequestDispatcher("/displayList.jsp");
        LogMessage.println("Leaving ListServlet: " + theUser.getUserName());
	dispatcher.forward(request, response);
	
    } // not null
  }
}

    

