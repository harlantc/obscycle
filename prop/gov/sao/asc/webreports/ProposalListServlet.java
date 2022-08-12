/*
  Copyrights:
 
  Copyright (c) 2014-2016 Smithsonian Astrophysical Observatory
 
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
// ProposalListServlet

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.Vector;
import java.util.HashMap;
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
import ascds.FileUtils;
import info.*;
import db.DBConnection;

/******************************************************************************/
/**
 */

public class ProposalListServlet extends HttpServlet 
{
    private static final long serialVersionUID = 1;
    private Properties reportsProperties;
    private HashMap hm = new HashMap(); // static subject categories


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

      hm.put("10","SolarSys");
      hm.put("20","NormStars+WD");
      hm.put("30","WDBin+CV");
      hm.put("40","BH+NS Binaries");
      hm.put("50","SN+SNR+IsolNS");
      hm.put("51","GravWav");
      hm.put("61","NrmGals:DifEmis");
      hm.put("62","NrmGals:XrayPop");
      hm.put("70","ActGal+Quas");
      hm.put("80","ClustOfGals");
      hm.put("90","ExtDifEmis+Surv");
      hm.put("91","GalDifEmis+Surv");
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

      LogMessage.println("----------- Entering ProposalListServlet ------------");      
      //Reload properties
      reportsProperties = Reports.getProperties();
      String timeout = reportsProperties.getProperty("reports.session.timeout");

    // Get the session object.
    HttpSession session = request.getSession(false );
    String startPageURL = reportsProperties.getProperty("reports.start.url");
    String proposalFileDir  = reportsProperties.getProperty("proposal.file.dir");

    RequestDispatcher dispatcher = null;
    int validUser = ReportsConstants.INVALIDENTRY;
    boolean caughtError = false;
    String reviewerPanel = request.getParameter("panelName");
    String reviewerName = new String("");
    String listType = request.getParameter("type");

    String accessDateFile  = reportsProperties.getProperty("reports.access.date.file");


    int reportsID = -1;
    if(session != null) {
	Integer reportsIDInt = (Integer)session.getAttribute("reportsID");
	if(reportsIDInt != null) {
	    reportsID = reportsIDInt.intValue();
	    validUser = User.isValidUser(reportsID);
	}
    }
    else {
      LogMessage.println("ProposalListServlet: session is null");
    }


    if(validUser != ReportsConstants.VALIDENTRY) {
        Integer tInt = (Integer)reportsID;
	LogMessage.println("ProposalListServlet: Not a valid user for " + tInt.toString());
	
	if(session != null) {
	    session.invalidate();
	}
	//Send user back to the start page
	response.sendRedirect(startPageURL);

    } else {
      //Valid user has entered the page
      String reportsDataPath = reportsProperties.getProperty("reports.data.path");
      if (reportsDataPath == null) {
        LogMessage.println("ProposalListServlet: reports path is null");
      }
	

      // Get the current user info
      User theUser = (User)session.getAttribute("user");
      Boolean isAnonymous = (Boolean)session.getAttribute("isAnonymous");
      if (isAnonymous == null) isAnonymous = new Boolean(true);
      Integer userID = new Integer(theUser.getUserID());
      String panelName = theUser.getPanelName();


      LogMessage.println ("ProposalListServlet: user=" + theUser.getUserName() + "  memberType=" + theUser.getMemberType() + " userType=" + theUser.getType());
      if (theUser.isAdmin() || theUser.isDeveloper()) {
           panelName = reviewerPanel;
           if (listType.indexOf("panmema") >= 0) {
              panelName="all";
           }
           LogMessage.println("ProposalListServlet: Admin user for " + panelName + " looking at " + userID.toString());
      } 
      if (theUser.isPundit() && reviewerPanel != null) {
        panelName = reviewerPanel;
        LogMessage.println("ProposalListServlet: Pundit for " + panelName + " looking at " + userID.toString());
      }
      if (panelName != null)
      {
        //Get the file which contains the paths to the the science justification, proposal

        if (listType != null) {
	  LogMessage.println("ProposalListServlet: List type is " + listType + " for "  + theUser.getUserName());
        }
	
	boolean showDebug = false;
        ProposalReviewerList propList = null;
        String csvFile ="";
        String csvmsg ="";
        String csvTitle ="";
        boolean sortList=true;
        Vector<String> csvHdr = new Vector<String>();
        Vector csvData = new Vector();
        String filename = reportsDataPath + "/" + "lists/" ;
	propList =  new ProposalReviewerList(reportsDataPath, showDebug);

        if (listType.indexOf("pri_sec") >= 0) {
	  try {
            propList.loadProposalsOnPanel(panelName );

            csvTitle ="Primary and Secondary Reviewers for Panel " + panelName;
            csvHdr.add("Panel");
            csvHdr.add("Proposal");
            if (!isAnonymous) csvHdr.add("P.I.");
            csvHdr.add("Type");
            csvHdr.add("Primary Reviewer");
            csvHdr.add("Secondary Reviewer");
            csvHdr.add("Title");
            csvHdr.add("Science Category");
            if (userID.intValue() < 0) 
              csvFile= theUser.getUserName();
            else 
              csvFile += userID.toString();
            csvFile += "_pri_sec_panel";
            csvFile +=  panelName  + ".tsv"; 
            filename +=  csvFile ;

            for (int rr=0;rr< propList.size();rr++) {
              Proposal prop = propList.get(rr);
              

              Vector<String> dd = new Vector<String>();
              String plinks = getPropLinks(prop,response) ;
              dd.add(prop.getPanelName());
              dd.add(plinks);
              if (!isAnonymous) dd.add(prop.getPI());
              dd.add(prop.getProposalType());
              dd.add(prop.getPrimaryReviewer());
              dd.add(prop.getSecondaryReviewer());
              dd.add(prop.getTitle());
              String subcat = prop.getProposalNumber();
              subcat = subcat.substring(2,4);
              String scicat = (String)hm.get((Object)subcat);
              dd.add(scicat);
              csvData.add(dd);
            }
          } catch (Exception exc) {
	    LogMessage.println("ProposalListServlet:service routine - Caught exception for user ID " + userID);
           LogMessage.printException(exc);
	   caughtError = true;
         }
       }
       else if (listType.indexOf("bpplist") >= 0) {
	  try {
            DBConnection dbConnect = new DBConnection(reportsDataPath, showDebug);
            propList.loadProposalsOnPanel("BPP" );
            HashMap<String,String> panels = dbConnect.getLPPanels();

            csvTitle ="BPP Proposal List" ;
            csvHdr.add("Panels");
            csvHdr.add("Proposal");
            csvHdr.add("Type");
            if (!isAnonymous) csvHdr.add("P.I.");
            csvHdr.add("Title");
            csvFile = userID.toString();
            csvFile += "_bpplist.tsv";
            filename +=  csvFile ;

            for (int rr=0;rr< propList.size();rr++) {
              Proposal prop = propList.get(rr);
              Vector<String> dd = new Vector<String>();
	      dd.add(panels.get(prop.getProposalNumber()));
              String plinks = getPropLinks(prop,response) ;
              dd.add(plinks);
              dd.add(prop.getProposalType());
              if (!isAnonymous) dd.add(prop.getPI());
              dd.add(prop.getTitle());
              csvData.add(dd);
            
            }
          } catch (Exception exc) {
	    LogMessage.println("ProposalListServlet:service routine - Caught exception for user ID " + userID);
           LogMessage.printException(exc);
	   caughtError = true;
         }
       }
       else if (listType.indexOf("pancat") >= 0) {
         Vector<String> pnames ;
         pnames = getPanelGroups(panelName, reportsDataPath);
	 try {
            propList.loadProposalsOnPanels(pnames );
            String x = pnames.toString();
            x = x.replaceAll("[\\[\\]]","");
            x = x.replace(',','_');
            x = x.replaceAll(" ","");

            csvTitle ="Proposals in panels with same Science Category " + x;
            LogMessage.println("csvTitle = " +csvTitle);
            csvHdr.add("Panel");
            csvHdr.add("Proposal");
            if (!isAnonymous) csvHdr.add("P.I.");
            csvHdr.add("Title");
            csvHdr.add("Science Category");
            csvFile = userID.toString();
            csvFile += "_panels" ;
            csvFile +=  x  + ".tsv"; 
            filename +=  csvFile ;


            for (int rr=0;rr< propList.size();rr++) {
              Proposal prop = propList.get(rr);
              Vector<String> dd = new Vector<String>();

              //LogMessage.println("adding " + prop.getProposalNumber());
              dd.add(prop.getPanelName());
              String plinks = getPropLinks(prop,response) ;
              dd.add(plinks);
              if (!isAnonymous) dd.add(prop.getPI());
              dd.add(prop.getTitle());
              String subcat = prop.getProposalNumber();
              subcat = subcat.substring(2,4);
              //LogMessage.println("subcat " + subcat);
              String scicat = (String)hm.get((Object)subcat);
              //LogMessage.println("scicat " + scicat);
              dd.add(scicat);
              csvData.add(dd);
            
            }
          } catch (Exception exc) {
	    LogMessage.println("ProposalListServlet:service routine - Caught exception for user ID " + userID);
           LogMessage.printException(exc);
	   caughtError = true;
         }
       }
       else if (listType.indexOf("panmem") >= 0 ) {
          if (listType.indexOf("panmemb") >= 0 ) {
            panelName = "BPP";
          }
	  try {
            Vector<User> memberList;
            DBConnection dbConnect = new DBConnection(reportsDataPath, showDebug);
            memberList =  dbConnect.loadPanelMembers(panelName);

            csvTitle ="Members for Panel " + panelName;
            csvHdr.add("Panel");
            csvHdr.add("Name");
            csvHdr.add("Type");
            if (theUser.isAdmin() || theUser.isDeveloper()) {
              csvHdr.add("Email");
            }
            csvFile = userID.toString();
            csvFile += "_member_list.tsv";
            filename +=  csvFile ;

            for (int rr=0;rr< memberList.size();rr++) {
              User member = memberList.get(rr);
              
              Vector<String> dd = new Vector<String>();
              dd.add(member.getPanelName());
              String tstr = member.getUserName();
              if (member.getUserFirst() != null)
                tstr += "," + member.getUserFirst();
              dd.add(tstr);
              dd.add(member.getMemberType());
              if (theUser.isAdmin() || theUser.isDeveloper()) {
                dd.add(member.getUserEmail());
              }
              csvData.add(dd);
            
            }
          } catch (Exception exc) {
	    LogMessage.println("ProposalListServlet:service routine - Caught exception for user ID " + userID);
           LogMessage.printException(exc);
	   caughtError = true;
         }
       }

       //write out the tab delimited file
       writeTSVFile(filename,csvHdr,csvData);

       //If we had an error getting the proposals on this panel, send the user
       //back to the start page.
       if(caughtError) {
         //Send user back to the start page
         LogMessage.println("Leaving ProposalListServlet: caught error");
         response.sendRedirect(startPageURL);
       } else {
	 session.setAttribute("reportsDataPath", reportsDataPath);
         session.setAttribute("proposalFileDir", proposalFileDir);
	 session.setAttribute("user", theUser);
	 session.setAttribute("panel", panelName);
	 session.setAttribute("csvHdrs", csvHdr);
	 session.setAttribute("csvData", csvData);
	 session.setAttribute("csvTitle", csvTitle);
	 session.setAttribute("csvMsg", csvmsg);
	 session.setAttribute("csvFile", csvFile);
         session.setAttribute("sortList", new Boolean(sortList));
         session.setAttribute("backLink", "/displayList.jsp");

         if(theUser.isDeveloper()) {
	   timeout = new String("-1");
	 }
	 session.setAttribute("timeout", timeout);

         if ( listType != null ) {

	   //Forward request to the jsp to display the resulting page
	   //dispatcher = getServletContext().getRequestDispatcher("/displayPropList.jsp");
	   dispatcher = getServletContext().getRequestDispatcher("/displayList.jsp");
         }
         LogMessage.println("Leaving ProposalListServlet: " + theUser.getUserName());

	 dispatcher.forward(request, response);
        }
      }
      else {
	session.setAttribute("user", theUser);
	session.setAttribute("panel", panelName);
	dispatcher = getServletContext().getRequestDispatcher("/login.jsp?file=NoFile");
        LogMessage.println("ProposalList: user not authorized for this function: " + theUser.getUserName());
	dispatcher.forward(request, response);
      }
    }
  }

  public void writeTSVFile(String filename,
	Vector<String>hdrs,Vector data)
  {
    try {
    
      File theFile = null;
      PrintWriter out;
      out = new PrintWriter(new FileWriter(filename));

      for (int ii=0;ii<hdrs.size();ii++) {
        if (ii!=0) out.write("	");
        out.write(hdrs.get(ii));
      }
      out.println("");

      for (int ii=0;ii<data.size();ii++) {
         Vector<String> csvdata;
         csvdata = (Vector<String>)data.get(ii);
         for (int rr=0;rr<csvdata.size();rr++) {
           if (rr!=0) out.write("	");
           if (csvdata.get(rr) != null)
             out.write(csvdata.get(rr));
         }
         out.println("");
      }
    out.close();
    FileUtils.setPermissions(filename,"660");

    
    } catch (Exception exc) {
      LogMessage.printException(exc);
    }
  }
  
  public String getPropLinks(Proposal prop,HttpServletResponse response)
  {
    String theURL;

    String sciJustFile=prop.getMergedFile();
    String propLink = prop.getProposalNumber();
    if (sciJustFile != null && sciJustFile.length() > 0) {
      theURL = "/reports/displayFile.jsp?fileName=" + sciJustFile;
      theURL = response.encodeURL(theURL);
      propLink="<a href=\"" + theURL +  "\"  target=\"propListSJ\" >";
      propLink += prop.getProposalNumber();
      propLink += "</a>";
    }
    String techLink = "";
    String techFile = prop.getTechnicalFile();
    if (techFile != null && techFile.length() > 1) {
      theURL = "/reports/displayFile.jsp?fileName=" + techFile;
      theURL = response.encodeURL(theURL);
      techLink = "<a href=\"" + theURL + "\" target=\"techRev\" class=\"tech\">Tech</a>";
    }
    String pinputLink = "";
    String pinputFile = prop.getProposerInputFile();
    if (pinputFile != null && pinputFile.length() > 1) {
      theURL = "/reports/displayFile.jsp?fileName=" + pinputFile;
      theURL = response.encodeURL(theURL);
      pinputLink = "<a href=\"" + theURL + "\" target=\"pinputRev\" class=\"pi\">PI</a>";
    }
    String retstr = propLink + " " + " " + techLink + " " + pinputLink ;
    return retstr;
  }

  public Vector<String> getPanelGroups(String pname,String reportsDataPath)
  {
    LogMessage.println("Find panel groups for " + pname);
    Vector<String>pnames = new Vector<String>();
    try {
      String fullname =  reportsDataPath +  "/.htpanels";
      File panelFile = new File(fullname);
      String inputLine;
      BufferedReader fileBR = new BufferedReader(new FileReader(panelFile));
      while ((inputLine = fileBR.readLine()) != null) {
        // verifying what is in the file
        inputLine = inputLine.replaceAll("[^\\p{ASCII}]","");
        if (inputLine.indexOf(pname) >= 0) {
          String[] arr =inputLine.split("_");
          for (int aa=0;aa<arr.length;aa++) {
            pnames.add(arr[aa]);
            LogMessage.println("Adding panel to group:  " + arr[aa]);
          }
        }
      }
      LogMessage.println("Using panel setup from .htpanels.");
    } catch (Exception exc) {
      LogMessage.printException(exc);
      LogMessage.println("Using default panel setup.");
      pnames.clear();
      if ("01".indexOf(pname) >= 0 || "02".indexOf(pname) >= 0  ) {
        pnames.add("01");
        pnames.add("02");
      }
      if ("03".indexOf(pname) >= 0 || "04".indexOf(pname) >= 0  ) {
        pnames.add("03");
        pnames.add("04");
      }
      if ("05".indexOf(pname) >= 0 || "06".indexOf(pname) >= 0  ) {
        pnames.add("05");
        pnames.add("06");
      }
      if ("07".indexOf(pname) >= 0 || "08".indexOf(pname) >= 0  ) {
        pnames.add("07");
        pnames.add("08");
      }
      if ("09".indexOf(pname) >= 0 || "10".indexOf(pname) >= 0  ||
          "11".indexOf(pname) >= 0 ) {
        pnames.add("09");
        pnames.add("10");
        pnames.add("11");
      }
      if ("XVP".indexOf(pname) >= 0) {
        pnames.add("XVP");
      }
      if ("LP".indexOf(pname) >= 0) {
        pnames.add("LP");
      }

    }
    return pnames;
  }
}

