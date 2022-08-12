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
// LoginServlet

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.net.URL;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.Vector;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
import java.text.DateFormat;
import java.text.ParseException;
import info.User;
import info.Reports;
import info.ReviewReport;
import info.ReportsConstants;
import info.ModifiedInstitutions;
import info.ProposalFileList;
import ascds.LogMessage;
import db.DBConnection;

/******************************************************************************/
/**
 */

public class LoginServlet extends HttpServlet 
{
    private static final long serialVersionUID = 1;
    private Properties reportsProperties;
    private LogMessage logFile;
    private ModifiedInstitutions modifiedInstitutions;
    private ProposalFileList proposalFileList;
    private String proposalFileDir;
    private boolean showDebug;
    private String rwsPath;


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
    
      String logFilename = reportsProperties.getProperty("reports.log.file");
      logFile = new LogMessage(logFilename);
      String currentUsersFile = reportsProperties.getProperty("reports.current.users");
      User.setCurrentUsersFile(currentUsersFile);

      String instDataPath = reportsProperties.getProperty("inst.conflict.filename");
      modifiedInstitutions = new ModifiedInstitutions(instDataPath);
  
      String linkedFilesList = reportsProperties.getProperty("linked.files.filename");
      proposalFileDir = reportsProperties.getProperty("proposal.file.dir");
      proposalFileList = new ProposalFileList(linkedFilesList);

      String str = reportsProperties.getProperty("show.debug");
      if (str.indexOf("1") >= 0)
        showDebug=true;
      else
        showDebug=false;
          
      /* use this to get system properties if you have issues */
      //Properties p = System.getProperties();
      //Enumeration keys = p.keys();
      //while (keys.hasMoreElements()) {
        //String key = (String)keys.nextElement();
        //String value = (String)p.get(key);
        //LogMessage.println(key + " : " + value);
      //}
  
    }


    /****
     * getUserFromRWSLogin
     * This routine takes a filename as input and creates a user object with the
     * information in the file. This file specified as input will be generated by 
     * the CDO reviewer's site. This routine will remove the file after reading it.
     * @param filename         filename containing user login info
     * @param reportsDataPath  directory path for the PAS
     * @return User      user info object
     */
    private User getUserFromRWSLogin(String filename, String reportsDataPath) 
    {
      File loggedInFile = null;
      String inputLine = null;
      String[] inputArray;
      int currentUserID = -1;
      String userType = null;
      User newUser = null;
      int ii=0;

      rwsPath = reportsProperties.getProperty("rws.files");
      if(filename != null && filename.matches("[a-zA-Z0-9.]*") ){
        String fullpath = rwsPath + "/" + filename;
        loggedInFile = new File(fullpath);
        logFile.println("LoginServlet RWS Login: file = " + filename);

        try {
          if (!loggedInFile.exists()) {
             fullpath = reportsDataPath + "/rws/" + filename;
             loggedInFile = new File(fullpath);
             logFile.println("LoginServlet RWS Login: retry file = " + filename);
          }

          if (loggedInFile.exists()) {
            BufferedReader fileBR = new BufferedReader(new FileReader(loggedInFile)); 
    	    inputLine = fileBR.readLine();
    	    logFile.println("Reading line = " + inputLine);
    	    if (inputLine != null) {
              // verifying what is in the file 
	      inputLine = inputLine.replaceAll("[^\\p{ASCII}]","");
              inputLine = inputLine.replace("\\","");
              inputLine = inputLine.replaceAll("`","'");
              inputLine = inputLine.replaceAll("<","");
              inputLine = inputLine.replaceAll(">","");
    	      inputArray = inputLine.split("\\s");
    	      if (inputArray.length > 1) {
    		userType = inputArray[ii++];

                // special case of deputy chair with space in user type
                if (userType.compareToIgnoreCase("pundit") == 0) {
                  if (inputArray[ii].compareToIgnoreCase("chair") == 0)  {
                    userType = ReportsConstants.PUNDIT;
                    ii++;
                  }
                  else if (inputArray[ii].compareToIgnoreCase("deputy") == 0)  {
                    userType = ReportsConstants.PUNDIT;
                    ii++;
                  }
                }
                else if (inputArray[ii].compareToIgnoreCase("chair") == 0) { 
                  userType = ReportsConstants.DEPUTYCHAIR;
                  ii++;
                }

    	        //Reviewers and chairs: userid, type
                //Admin: 2 types - in axafusers and not in axafusers
    		if (userType.equals(ReportsConstants.DEVELOPER)) {
                  String userName = (String)inputArray[ii+1];
                  newUser = new User(userName, ReportsConstants.DEVELOPER);
                  newUser.setUserID(ReportsConstants.DEVELOPERID);
    		    
    		} else if ((userType.compareToIgnoreCase(ReportsConstants.ADMIN)) == 0 ||
    		       (userType.compareToIgnoreCase(ReportsConstants.ADMINEDIT)) == 0) 
                {
                   //x currentUserID = Integer.parseInt(inputArray[ii++]);
                   String userName = (String)inputArray[ii++];

                   newUser = new User(userName, ReportsConstants.ADMIN);
                   //x newUser.setUserID(currentUserID);
                   newUser.setUserID(ReportsConstants.ADMINID);

                   if(newUser.getUserName() == null) {
                     newUser.setUserName(ReportsConstants.ADMIN);
                   }

                   if( (userType.compareToIgnoreCase(ReportsConstants.ADMIN)) == 0) {
                     //admins can't edit unless they are in "adminedit" mode
                     newUser.setAllowedToEdit(false); 
                   }

                   //Check if there was an email address specified for this user
                   if(inputArray.length > ii) {
                     String userEmailAddress = (String)inputArray[ii++];
                     newUser.setEmailAddress(userEmailAddress);
                   }
    			
                } else if(userType.compareToIgnoreCase(ReportsConstants.FACILITATOR) == 0) {
                   //x ii++;
                   String userName = (String)inputArray[ii++];
                   String panelName = (String)inputArray[ii++];
                   newUser = new User(userName, ReportsConstants.FACILITATOR);
                   newUser.setUserID(ReportsConstants.FACILITATORID);
                   newUser.setAllowedToEdit(false); 
                   newUser.setPanelName(panelName);
                   if(newUser.getUserName() == null) {
                     newUser.setUserName(ReportsConstants.FACILITATOR);
                   }
                } else if(userType.compareToIgnoreCase(ReportsConstants.PUNDIT) == 0) {
                   currentUserID = Integer.parseInt(inputArray[ii++]);
                   String userName = (String)inputArray[ii++];
                   if (currentUserID > 0) {
                     newUser = new User(currentUserID, reportsDataPath);
                   } else {
                     newUser = new User(userName, ReportsConstants.PUNDIT);
                     newUser.setUserID(currentUserID);
                     if(newUser.getUserName() == null) {
                       newUser.setUserName(ReportsConstants.PUNDIT);
                     }
                   }

                } else if( (userType.compareToIgnoreCase(ReportsConstants.REVIEWER)) == 0 ||
                         (userType.compareToIgnoreCase(ReportsConstants.CHAIR)) == 0   ||
                         (userType.compareToIgnoreCase(ReportsConstants.DEPUTYCHAIR)) == 0 ) 
                {
                   currentUserID = Integer.parseInt(inputArray[ii++]);
                  //x ii++;  // skip past user name
                   newUser = new User(currentUserID, reportsDataPath);
                   //Check if there was an email address specified for this user
                   if(inputArray.length > ii) {
                     String userEmailAddress = (String)inputArray[ii++];
                     newUser.setEmailAddress(userEmailAddress);
                   }
                }
              }
            }    
            fileBR.close();

            //Remove the file
            boolean deleteSuccess = loggedInFile.delete();
            if(!deleteSuccess) {
              logFile.println("Error: Cannot delete logged in user file: " + filename);
             }
           } else {
             logFile.println("Error: file' " + filename + "' doesn't exist");
           }
         } catch(Exception ex) {
            ex.printStackTrace();
            logFile.println("Caught exception in LoginServlet:getUser");
         }
      } else {
          LogMessage.println("Invalid characters in filename for RWS login.");
      }

      return newUser;
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

    String bppTar;
    String panelTar;

    /*
    Map<String, String> env = System.getenv();
    for (String envName : env.keySet()) {
      LogMessage.println(envName + "=" + env.get(envName) );
    }
    */

    LogMessage.println(InetAddress.getLocalHost().getHostName());
    //Users will be validated by the CDO php page, and if the user is valid, the
    //user id will be put in a file called .htcurrentUsers, in the reports.data.path
    //area. 
    logFile.println("---- Entering LoginServlet ----");
      
    //Get latest reportsProperties from Reports class
    reportsProperties = Reports.getProperties(); //Reload each time

    //Get the CDO message, if any
    String cdoText = Reports.getCDOMessage();
    //Get the Release down message, if any
    String releaseDown = Reports.getReleaseDown();

    //Set user timeout period
    String timeout = reportsProperties.getProperty("reports.session.timeout");

    boolean validUserEntry = false;  
    boolean accessReports = false;
    boolean accessPreReports = false;
    boolean allowedAccessNow = true;
    boolean accessLPs = false;
    int userID = -1; 
    String reportsDataPath = reportsProperties.getProperty("reports.data.path");
    String tarDataPath = reportsProperties.getProperty("tar.data.path");
    String startPageURL = reportsProperties.getProperty("reports.start.url");
    User theUser = null;
    String userType = ReportsConstants.NOTYPE;      
    HttpSession session = null;
    Boolean validUserBool = null;
    boolean loggedIn = false;
    boolean beforePR = false;
    String listFname = reportsDataPath + "/.htlist";
    Vector<String> listFile= null;

    session = request.getSession(false);

    if (releaseDown.length() > 2)  {
      response.sendError(response.SC_SERVICE_UNAVAILABLE,releaseDown);
    }
    else {

      String panelName = null;
      bppTar = null;
      panelTar = null;
      //Get the user ID, either from the file specified or from the session 
      String rwsCreatedLoginFile = request.getParameter("file");

      // Check if the user has logged in from the RWS site, 
      // or is returning to this page from within the site.
      if(rwsCreatedLoginFile != null && !rwsCreatedLoginFile.equals("NoFile")) {
        //User logging in through the RWS site, clear session
        session = null;
        logFile.println("User logged in from RWS with file = " + rwsCreatedLoginFile);
        theUser = getUserFromRWSLogin(rwsCreatedLoginFile, reportsDataPath);
        if(theUser == null) {
          LogMessage.println("Error: User not logged in, cannot view page.");
        } else {
          theUser.addCurrentUser();
        }
      } else if(session != null) {
        Integer lrid = (Integer)session.getAttribute("reportsID");
        if (lrid == null) {
          LogMessage.println("Login session: reportsID is null");
        } else {
          //User returning to the main page from within the site
          int reportsID = ((Integer)session.getAttribute("reportsID")).intValue();
          LogMessage.println("User returning with " + reportsID);
          theUser = User.getUser(reportsID);
        }
/*
          Enumeration sesvar = session.getAttributeNames();
          while(sesvar.hasMoreElements()) {
            String sesname = (String)sesvar.nextElement();
            LogMessage.println("Login session: " + sesname + " = " + session.getAttribute(sesname));
          }
*/
      }


      //Determine if the user is valid
      if(theUser != null)  { 
        validUserEntry = true;
        logFile.println(theUser.getUserName() + " with ID " + theUser.getUserID() + " logged in.");
       
        panelName = theUser.getPanelName();	
        bppTar  = readTarFiles(reportsDataPath,panelName,true);
        panelTar  = readTarFiles(reportsDataPath,panelName,false);
        listFile = readListFiles(listFname,theUser);
        logFile.println("Found " + listFname + " linecnt: " + listFile.size());
      }

      //Determine if current date is after the access date specified in the 
      //properties file
      String accessDateFile  = reportsProperties.getProperty("reports.access.date.file");
      String anonReview  = reportsProperties.getProperty("anonymous.review");
      LogMessage.println("ANONYMOUS=" + anonReview);
      Boolean isAnonymous = false;
      if (anonReview == null || anonReview.indexOf("1") >= 0) 
          isAnonymous = true;
      if(theUser!= null && theUser.isAdmin()) isAnonymous = false;

      // admin access date may be different from the reviewers
      if(theUser!= null && theUser.isAdmin()) {
        accessReports = Reports.adminAccessReports(accessDateFile);      
        if (accessReports) {
          if (!Reports.accessReports(accessDateFile)) {
             accessPreReports = true;
          }
          // if accessible, then allow admin users access to LP reports 
          theUser.setAllowedLPAccess(true);
        }
      }
      else {
        accessReports = Reports.accessReports(accessDateFile);      
        if (theUser!=null && accessReports) {
           if (theUser.isReviewer()  ) {
              accessReports = Reports.endReviewerAccess(accessDateFile);      
           }
           else if (theUser.isChair() || theUser.isDeputyChair()  ) {
              accessReports = Reports.endChairAccess(accessDateFile);      
           }
           else if (theUser.isFacilitator()) {
              accessReports = Reports.endFacilitatorAccess(accessDateFile);      
           }
           else if (theUser.isPundit()) {
              accessReports = Reports.endPunditAccess(accessDateFile);      
           }
        }
      }
    

      //Always allow developers to access the site, for testing
      if(theUser!= null && theUser.isDeveloper()) {
        logFile.println("Developer logged in - always allow access.");
        accessReports = true;
        theUser.setAllowedLPAccess(true);
      }
      // it is before the access date, so check by panel for the pre-conflict access
      if (theUser!=null && !accessReports &&
	 (theUser.isReviewer() || theUser.inChairMode() || theUser.isPundit())) {
        accessPreReports=Reports.accessPreReports(accessDateFile,theUser.getPanelName());
        LogMessage.println("Pre Access for " + theUser.getUserName() + " : " + accessPreReports);
      }

      //Only need to determine user information if there isn't a session, and if
      //the user has entered validly.
      if(validUserEntry && (accessReports || accessPreReports) && session == null) {
        session = request.getSession(true);
        session.setAttribute("validUser", new Boolean(true));
        session.setAttribute("isAnonymous", isAnonymous);
        session.setAttribute("reportsDataPath", reportsDataPath);
        session.setAttribute("tarDataPath", tarDataPath);

      
        //Determine if the user should have access at this time
        panelName = theUser.getPanelName();	
        if(panelName != null) {
          allowedAccessNow = Reports.accessPanel(accessDateFile, panelName);
          theUser.setAllowedAccessNow(allowedAccessNow);
        } else if(theUser.isAdmin() || theUser.isDeveloper()) {
          logFile.println("No panel name for admin users");
        } else {
          //logFile.println("Error: Cannot determine panel name for user " + userID);
          logFile.println("Error: Cannot determine panel name for user " + theUser.getUserID());
        }
      
        //Set the user in the session
        session.setAttribute("user", theUser);
        session.setAttribute("reportsID", new Integer(theUser.getUserID()));
        if(theUser.isDeveloper()) {
          timeout = new String("-1");
        }
        session.setAttribute("timeout", timeout);
      } 

      // but always redetermine before PR status because this changes
      // at the review and they don't always log out
      if (session != null) {
        session.setAttribute("cdoText",cdoText);
        session.setAttribute("beforePR", new Boolean(false));
        beforePR = false;
        if(Reports.beforePeerReview() && session != null) {
          beforePR = true;
          session.setAttribute("beforePR", new Boolean(true));
        }
      }

      //If the user is a chair person, and at the peer review, determine
      //is on a panel with an LP/VLP.  If not, then the chair person should
      //only get read-only access to the LP/VLPs
      if(!beforePR && session != null && theUser.editLPLink(reportsDataPath) ) {
        session.setAttribute("editLPLink", new Boolean(true));
      }

      //Forward request to the jsp to display the resulting page
      //if there is no user found, then display an error page
      RequestDispatcher dispatcher = null;

      if(!validUserEntry) {
        if(session != null) {
          session.invalidate();
        }
      
        //Send user back to the login page
        response.sendRedirect(startPageURL);
      } else if(!accessReports && !accessPreReports) {
        //Display message that the reports site is currently unavailable.
        PrintWriter out = response.getWriter();
        //out.println("The Chandra Panel Access site is currently unavailable.  Please try again later.");
        out.println("The Chandra Panel Access site is not open for access at this time.  Please try again later.");
      
      } else if(!allowedAccessNow) {
        dispatcher = getServletContext().getRequestDispatcher("/panelUnavailable.jsp");
        dispatcher.forward(request, response);
      } else  {
        if(theUser != null) {
          String backLinkURL = new String("login.jsp");
          session.setAttribute("backLink", backLinkURL);
    	  session.setAttribute("startPageURL", startPageURL);
          try {
            DBConnection dbConnect = new DBConnection(reportsDataPath, showDebug);
            String aoStr = dbConnect.getCurrentAO();
          session.setAttribute("currentAO", aoStr);
    	  session.setAttribute("proposalFileDir", proposalFileDir);

          if(theUser.isAdmin() || theUser.isDeveloper()) {
      	    Vector<String> panelList =  dbConnect.getPanels();
    	    session.setAttribute("panelsList", panelList);
    	    session.setAttribute("listFile", listFile);
    	    session.setAttribute("accessPreReports", accessPreReports);
    	  

    	    dispatcher = getServletContext().getRequestDispatcher("/displayAdminPage.jsp");
    	    dispatcher.forward(request, response);

          } else if(theUser.isFacilitator()) {
    	    beforePR = ((Boolean)session.getAttribute("beforePR")).booleanValue();
    	    //Facilitators can only access reports at peer review
    	    if(!beforePR) {
    	      panelName = theUser.getPanelName();
    	      //Facilitators can only access site at peer review
    	      Vector reportsList = ReviewReport.getRROnPanelByName(panelName, reportsDataPath, false);
    	      session.setAttribute("reportsList", reportsList);
    	      session.setAttribute("userID", String.valueOf(ReportsConstants.FACILITATORID));
    	      session.setAttribute("proposalFileDir", proposalFileDir);

    	      dispatcher = getServletContext().getRequestDispatcher("/displayFacilitatorView.jsp");
    	      dispatcher.forward(request, response);
    	    } else {
    	      //Facilitators can't view site before the peer review
    	      PrintWriter out = response.getWriter();
    	      out.println("Error: Facilitators cannot access the site now.  Please try again later.");
    	    }

          } else if(theUser.userExists()) {
            if (accessPreReports) {
    	      dispatcher = getServletContext().getRequestDispatcher("/preConflict.jsp");
    	      dispatcher.forward(request, response);
            } else {
    	      session.setAttribute("panelTar", panelTar);
    	      session.setAttribute("listFile", listFile);
              if (theUser.isChair() || theUser.isPundit()) {
    	        session.setAttribute("bppTar", bppTar);
              }
                  
    	      dispatcher = getServletContext().getRequestDispatcher("/displayLoginPage.jsp");
    	      dispatcher.forward(request, response);
            } 
          } else {
    	    LogMessage.println("Error: User ID not found.");
          }
         } catch (Exception exc) {
            LogMessage.printException(exc);
            response.sendRedirect(startPageURL);
         }
        } else {
          LogMessage.println("Error: Invalid user id.");
        }
      }
      if (theUser != null) {
        LogMessage.println("Leaving login servlet " + theUser.getUserName());
        //LogMessage.println("Leaving login servlet " + theUser.getPanelName());
      } else {
        LogMessage.println("Leaving login servlet");
      }
    }
  }


  private String readTarFiles(String dirPath,String panelName,boolean isBPP)
  {
    String nextLine;
    String filename = null;
    String tarName = null;
    

    if (dirPath != null ) {
      filename = dirPath + ".httar";

      File inFile = new File(filename);
      if (inFile.exists()) {
        try {
            FileReader fileR = new FileReader(filename);
            BufferedReader linkedFilesBF = new BufferedReader(fileR);
            String inputLine;
            String[] inputArray;

            while( (inputLine = linkedFilesBF.readLine()) != null) {

              //Found matching line, get the path for the files
              //Format of file:  panel ftp_url
              inputArray = inputLine.split(" ");
              if (inputArray.length > 1)  {
                 if (isBPP && inputArray[0].equalsIgnoreCase("bpp")) {
                    tarName = inputArray[1];
                    //LogMessage.println("Found match for " + bppTar);
                 }
                 if (!isBPP && panelName != null && !panelName.equalsIgnoreCase("BPP") &&
                     inputArray[0].equalsIgnoreCase(panelName)) {
                    tarName = inputArray[1];
                    //LogMessage.println("Found match for " + panelTar);
                 }
              }
            }
            linkedFilesBF.close();
            fileR.close();
        }
        catch (Exception exc) {
          LogMessage.printException(exc);
        }
      }
    }
   
    return tarName;
  }

  // A = Admin
  // C = Chair
  // P = Pundit
  // B = PunditChair
  private Vector<String> readListFiles(String listFile,User theUser)
  {
    String nextLine;
    Vector<String> vlist = new Vector<String>();
    

    if (listFile != null ) {

      File inFile = new File(listFile);
      if (inFile.exists()) {
        try {
            FileReader fileR = new FileReader(listFile);
            BufferedReader linkedFilesBF = new BufferedReader(fileR);
            String inputLine;

            while( (inputLine = linkedFilesBF.readLine()) != null) {
              if (inputLine.indexOf("#") < 0) {
                String[] sarr= inputLine.split("	");
                if (sarr.length > 1 &&
                    ((sarr[0].indexOf("A") >= 0 && theUser.isAdmin()) ||
                     (sarr[0].indexOf("P") >= 0 &&  theUser.isPundit()) ||
                     (sarr[0].indexOf("B") >= 0 &&  theUser.isChair() && theUser.isPundit())  ||
                    (sarr[0].indexOf("C") >= 0 &&  theUser.isChair() && !theUser.isPundit()) )) {
                   vlist.add(sarr[1]);
                 }
              }
            }
            linkedFilesBF.close();
            fileR.close();
        }
        catch (Exception exc) {
          LogMessage.printException(exc);
        }
      }
   }
   
   return vlist;
 }






}

