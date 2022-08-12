package edu.harvard.asc.cps.cpsapp;
/************************************************************************/
/*     Copyrights:                                                      */
/*                                                                      */
/*     Copyright (c) 2017 Smithsonian Astrophysical Observatory         */
/*                                                                      */
/*    Permission to use, copy, modify, distribute,  and  sell  this     */
/*    software  and  its  documentation  for  any purpose is hereby     */
/*    granted  without  fee,  provided  that  the  above  copyright     */
/*    notice  appear  in  all  copies  and that both that copyright     */
/*    notice and this permission notice appear in supporting  docu-     */
/*    mentation,  and  that  the  name  of  the  Smithsonian Astro-     */
/*    physical Observatory not be used in advertising or  publicity     */
/*    pertaining  to distribution of the software without specific,     */
/*    written  prior  permission.   The  Smithsonian  Astrophysical     */
/*    Observatory  makes  no  representations about the suitability     */
/*    of this software for any purpose.  It  is  provided  "as  is"     */
/*    without express or implied warranty.                              */
/*    THE  SMITHSONIAN  ASTROPHYSICAL  OBSERVATORY  DISCLAIMS   ALL     */
/*    WARRANTIES  WITH  REGARD  TO  THIS  SOFTWARE,  INCLUDING  ALL     */
/*    IMPLIED  WARRANTIES  OF  MERCHANTABILITY  AND FITNESS, IN  NO     */
/*    EVENT  SHALL  THE  SMITHSONIAN  ASTROPHYSICAL  OBSERVATORY BE     */
/*    LIABLE FOR ANY SPECIAL,  INDIRECT  OR  CONSEQUENTIAL  DAMAGES     */
/*    OR  ANY  DAMAGES  WHATSOEVER RESULTING FROM LOSS OF USE, DATA     */
/*    OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,  NEGLIGENCE  OR     */
/*    OTHER  TORTIOUS  ACTION, ARISING OUT OF OR IN CONNECTION WITH     */
/*    THE USE OR PERFORMANCE OF THIS SOFTWARE.                          */
/*                                                                      */
/************************************************************************/

import edu.harvard.asc.cps.xo.*;
import edu.harvard.cda.jutil.rest.InternalServerException;
import edu.harvard.cda.jutil.servlet.ServletUtil;
import edu.harvard.cda.cxclogin.filter.CASAFSessionScopeAttribute;
import edu.harvard.cda.cxclogin.filter.AuthUserInfoForApp;
import edu.harvard.cda.cxcaccount.restclient.IRestClient;
import edu.harvard.cda.cxcaccount.restclient.RestClient;

import org.apache.log4j.Logger;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Appender;
import java.net.URLEncoder;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Date;
import java.text.DateFormat;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



/******************************************************************************/
/**
 */

public class Login extends HttpServlet 
{



  /****************************************************************************/
  /**
   * Handle a GET request from a browser --- simply initialize
   * validation state.
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void doGet( HttpServletRequest request,
		     HttpServletResponse response)
    throws ServletException, IOException
  {
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

  public void doPost( HttpServletRequest request,
		      HttpServletResponse response )
    throws ServletException, IOException
  {
	service(request, response);
  }

  /****************************************************************************/
  /**
   * Handle the INIT operation.  This operation is invoked by the
   * servlet engine when it starts up.  Set the CPS properties
   *
   * @param config The configuration object established by the servlet
   * engine.
   */

  public void init( ServletConfig config )
    throws ServletException
  {
    ServletContext context = config.getServletContext();
    super.init(config);
    Properties cpsProperties = CPS.getProperties(context);

  }

  /****************************************************************************/
  /**
   * Handle a submission from a browser.  
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void service( HttpServletRequest request,
		       HttpServletResponse response )
    throws ServletException, IOException
  {
    
     Properties cpsProperties = CPS.getProperties();
     if (logger.isTraceEnabled()) {
       try {
         Map<String, String> env = System.getenv();
         for (Iterator it=env.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            logger.trace(entry.getKey() + " = " + entry.getValue());
         }
       } catch (Exception ex) {
         logger.error(ex);
       }
     }
     Integer uid = Integer.valueOf(0);


    boolean allowDDTs =  false;
    String tstr = (String)cpsProperties.getProperty("allow.ddt");
    if (tstr != null  && tstr.indexOf("true") >= 0) {
      allowDDTs=true;
    }

    boolean ddtRequest=false;
    String ddtRequestStr = request.getRequestURI();
    logger.trace("CPSURL: " + ddtRequestStr);
    if (ddtRequestStr.indexOf("ddtlogin") > 0) 
      ddtRequest=true;
    else {
      ddtRequestStr = (String)Parameter.get(request,"isDDT");
      if (ddtRequestStr != null && ddtRequestStr.equals("Y"))
        ddtRequest=true;
    }

    logger.trace("DDTs allow: " + allowDDTs + "  ddtrequest=" +ddtRequest);

    try {
      // restrict permissions on CPS logging file 640
      Enumeration e = Logger.getRootLogger().getAllAppenders();
      while ( e.hasMoreElements() ){
        Appender app = (Appender)e.nextElement();
        if ( app instanceof FileAppender ){
          Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
          //add owners permission
          perms.add(PosixFilePermission.OWNER_READ);
          perms.add(PosixFilePermission.OWNER_WRITE);
          //add group permissions
          perms.add(PosixFilePermission.GROUP_READ);
          perms.add(PosixFilePermission.GROUP_WRITE);
          logger.debug("File: " + ((FileAppender)app).getFile());
          Files.setPosixFilePermissions( Paths.get(((FileAppender)app).getFile()), perms);
        }
      }
    } catch (Exception exc) {
      logger.error(exc);
    }
    HttpSession session= request.getSession(false);
    if (session == null || !request.isRequestedSessionIdValid()) {
      logger.error("session is invalid, redirect to welcome");
      redirectToWelcomePage(request, response,"Unable to validate session.");
    }
    else if (!CASAFSessionScopeAttribute.USER_INFO.isPresent(session)) {
      logger.error( "CASAFSession failed!");
      try {
        session.invalidate();
      } catch (Exception e) {
        logger.error(e);
      }

      redirectToWelcomePage(request, response,"Unable to validate current user.");
      //response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
    else if (!allowDDTs && ddtRequest) {
      logger.error( "Invalid DDT request ");
      try {
        session.invalidate();
      } catch (Exception e) {
        logger.error(e);
      }

      String noddtpath="/cps-app/prop_noddt.jsp";
      response.sendRedirect(noddtpath);
    }
    else {
      //logger.info( "Login Session "  + session.getId());

      String msg="";
      String akey = CASAFSessionScopeAttribute.USER_INFO.getValue();
      AuthUserInfoForApp ainfo = (AuthUserInfoForApp)session.getAttribute(akey);
      try {
        IRestClient  api = getCXCAPI(cpsProperties);
        String fuser=URLEncoder.encode(ainfo.username,java.nio.charset.StandardCharsets.UTF_8.toString());
        uid = Integer.valueOf((api.persIdFromUsername(fuser)));
        //uid = new Integer(api.persIdFromUsername(ainfo.username));
      } catch (InternalServerException iexc) {
         logger.error("Login failed for " +ainfo.username);
         logger.debug("CXCAPI failed.",iexc);
         msg= iexc.getMessage();
         uid=0;
      } catch (Exception exc) {
         logger.error("Login failed for " +ainfo.username);
         logger.debug("CXCAPI failed.",exc);
         msg = "Unable to determine user from login.";
         uid=0;
      }
      logger.info("Logged in  " +ainfo.username + " with uid=" + uid.toString()) ;
      if (uid > 0) {

        String mstr = (String)cpsProperties.getProperty("cps.session.timeout");
        Integer maxSession= -1;
        try {
          maxSession = Integer.valueOf(mstr.trim());
        } catch (Exception exc) {
          maxSession= -1;
        }
    
        if (maxSession > 0)
          session.setMaxInactiveInterval(maxSession.intValue() * 60);


        String releaseDownMsg= CPS.getReleaseDown();
        if (releaseDownMsg != null && !releaseDownMsg.equals("")) {
          response.sendError(response.SC_SERVICE_UNAVAILABLE,releaseDownMsg);
        }

       
        String auname=ainfo.username;
        session.setAttribute("userId",uid);
        session.setAttribute("login",auname);
        session.setAttribute("ddtRequest",ddtRequest);
  
        RequestDispatcher dispatcher = null;
        dispatcher = getServletContext().getRequestDispatcher("/app/prop_nav.jsp");
        dispatcher.forward(request, response);
      }
      else {
        redirectToWelcomePage(request, response,msg);
      }
    }
  }

  private static void redirectToWelcomePage(HttpServletRequest req, HttpServletResponse res,String msg) throws IOException {
    //String url = String.format("%s/%s"
                               //, ServletUtil.fullURLupToContextPath(req)
                               //, "/prop_logout.jsp");
    String url="/cps-app/prop_logout.jsp";
    if (msg != null) {
      url += "?msg=" + msg;
    }
    logger.info("sending redirect to: " + url);
    res.sendRedirect(url);
  }


   public IRestClient getCXCAPI(Properties cpsProperties)
      throws Exception
   {
      String cpsapi = "";
      StringBuffer tbuff = new StringBuffer("");
      FileReader fileR = null;
      BufferedReader cdoFileBR = null;
      try {
        String cpsfile = (cpsProperties.getProperty("cps.appdir")).trim() +  ".htcpx";
        logger.debug(cpsfile);
        fileR = new FileReader(cpsfile);
        cdoFileBR = new BufferedReader(fileR);
        String inputLine;
        while( (inputLine = cdoFileBR.readLine()) != null) {
           tbuff.append(inputLine);
       }
        cdoFileBR.close();
        fileR.close();
      } catch (Exception exc) {
         logger.error(exc.getMessage());
         logger.debug(exc);
         try {
           if (cdoFileBR != null)
             cdoFileBR.close();
           if (fileR != null)
             fileR.close();
         } catch (Exception e) {
           logger.error(e);
         }
      }
      cpsapi = (tbuff.toString()).trim();
      String dalURL = cpsProperties.getProperty("cps.cxcdal.url");
      String cpsuser = cpsProperties.getProperty("cps.cxc");

      IRestClient api = new RestClient(dalURL,cpsuser, cpsapi);

      return api;
  }


  /**
   * Private variables
   */
    private static Logger logger = Logger.getLogger(Login.class);

    private static final long serialVersionUID = 1;


  /**
   * Private methods
   */

  /****************************************************************************/


}

/******************************************************************************/
