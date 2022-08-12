package edu.harvard.asc.cps.cpsapp;
/************************************************************************/
/*     Copyrights:                                                      */
/*                                                                      */
/*     Copyright (c) 2017-2019 Smithsonian Astrophysical Observatory    */
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
import edu.harvard.cda.proposal.restclient.IRestClient;
import edu.harvard.cda.proposal.restclient.RestClient;
import edu.harvard.cda.proposal.xo.VProposalCPSWithDetails;
import edu.harvard.cda.proposal.xo.VTargetCPSWithDetails;

import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Properties;
import java.util.List;
import java.util.Enumeration;
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

public class LoadTarget extends HttpServlet 
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
        service(request,response);
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
        service(request,response);
  }

  /****************************************************************************/
  /**
   * Handle the INIT operation.  This operation is invoked by the
   * servlet engine when it starts up.  
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
   * Handle a submission from a browser.  Operations supported:
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void service( HttpServletRequest request,
		       HttpServletResponse response )
    throws ServletException, IOException
  {

    String propno="";
    String msg="";
    String str="";
    Integer userId = 0;
    int uid = 0;
    StringBuffer outputStr = new StringBuffer();

    // reload properties
    Properties cpsProperties = CPS.getProperties();

    // Get the session object.
    HttpSession session = request.getSession( false );

    if (session == null || !request.isRequestedSessionIdValid()) {
      logger.error("Invalid session");
      propno= "Invalid";
      msg = "Invalid session.";
    }
    else {
      try {
        userId = (Integer)session.getAttribute("userId");
        if (userId == null || userId.intValue() <= 0) {
          logger.error("Invalid session, no userid");
          try {
          session.invalidate();
          } catch (Exception e) {
            logger.error(e);
          }

          propno= "Invalid";
        }
        else {
          uid = userId.intValue();
        }
      } catch (Exception ex) {
        logger.error(ex.getMessage() );
        logger.debug("Invalid user ",ex );
        msg = "Invalid user.";
        propno= "Invalid";
      }
    }
    String mypage = "";
    String pid = "";
    String tid = "";
    Integer mypid = 0;
    Integer mytid = 0;
    CPSProposal cpsProp = null;
/*
    Enumeration keys = session.getAttributeNames();
    while (keys.hasMoreElements())
    {
      String key = (String)keys.nextElement();
      logger.trace(key + ": " + session.getValue(key) );
    }
*/

 
    if (!propno.equals("Invalid")) {

      try {
        mypage = Parameter.get(request, "page" );
        pid = Parameter.get(request, "pid" );
        tid = Parameter.get(request, "tid" );
        mypid = Integer.valueOf(pid);
        mytid = Integer.valueOf(tid);
        // make sure the proposal being requested is valid for this user/session
        Boolean sess_pid = (Boolean)session.getAttribute((mypid.toString()));
        logger.info("uid=" + uid + ": " +  mypage +" for pid=" + mypid + " targid= " + mytid);
        if (sess_pid == null ) {
          propno="Invalid";
          msg = "Unauthorized proposal access";
          logger.error("uid=" + uid + "): Unauthorized access pid=" + pid );
        }
        cpsProp = (CPSProposal)session.getAttribute("curprop");
        if (cpsProp != null && cpsProp.vprop.proposalId != mypid.intValue())
          cpsProp = null;

      } catch (Exception ex) {
        logger.error(ex.getMessage());
        logger.debug("uid="+uid+": for pid=" + pid,ex);
        msg = "Invalid parameters.";
        propno="Invalid";
      }
    }

    PrintWriter out=null;
    Boolean isXML=true;

    if (!propno.equals("Invalid")) {
      try {
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        if (cpsProp == null) {
          String currentAO = (String)session.getAttribute("currentAO");
          String ddtAO = (String)session.getAttribute("ddtAO");
          boolean isCfP = CPS.isCfP();
          boolean isGTO = CPS.isGTO();
          boolean ddtRequest=(boolean)session.getAttribute("ddtRequest");
          VProposalCPSWithDetails vprop = api.retrieveProposalInformation(mypid.intValue());
          cpsProp = new CPSProposal(vprop,currentAO);
          cpsProp.setEditPrivs(isCfP,ddtRequest,ddtAO,isGTO,uid);
        }

        CPSTarget cps = null;
        if (mypage.equals("MONITOR") || mypage.equals("SPATWIN") || 
            mypage.equals("ROLL") || mypage.equals("WINDOW") ) {
          // only called after other constraints loaded
          cps = (CPSTarget) session.getAttribute("curtgt");
          logger.debug(mypage + ": " + mytid.toString() + " detector =" + cps.tgt.instrument_name  );
        } else {
          VTargetCPSWithDetails vtgt = api.retrieveViewTargetCPSWithDetailsByTargetId(mytid.intValue());
          //  now verify targid belongs to proposal
          if (vtgt.proposal_id != mypid.intValue()){
            propno="Invalid";
            msg = "Unauthorized target access";
            logger.error("uid=" + uid + "): Unauthorized access pid=" + pid  + " tid=" + tid);
          }
          else {

            String responseTime="";
            if (cpsProp.vprop.ddtProposalDatum != null)
              responseTime = cpsProp.vprop.ddtProposalDatum.response_time;
            cps = new CPSTarget(vtgt,cpsProp.isEdit,cpsProp.isDDTProp,cpsProp.vprop.multiCycle,responseTime,cpsProperties);
            String tstr="";
            Boolean tooDetails= cps.shouldHaveTOODetails();
            if (tooDetails) tstr ="too";
            if (vtgt.instrument_name != null)
              tstr += vtgt.instrument_name;
            session.setAttribute(mytid.toString() + "detector",tstr);
            session.removeAttribute("curtgt");
            session.setAttribute("curtgt",cps);
            logger.info(mypage + ": " + mytid.toString() + tstr);
          }
        }
        if (!propno.equals("Invalid")) {
          if (!cpsProp.isViewable(uid) ) {
            // this won't work because we don't want to add extra database load 
            // retrieving the proposal every time, so it doesn't know coi was removed
            // but it will fail when they go to look at another target or proposal page
            propno="Error";
            str = "<data><propno>Error</propno><emsg>Proposal is no longer viewable for the current user.</emsg></data>";
          }
          else if (mypage.equals("TIME")) {
            str = cps.getTimingEntry();
          }
          else if (mypage.equals("POINTING")) {
            str = cps.getPointingEntry();
          }
          else if (mypage.equals("INSTRUMENT")) {
            str = cps.getInstrumentEntry();
          }
          else if (mypage.equals("ACISREQ")) {
            str = cps.getAcisReqEntry();
          }
          else if (mypage.equals("ACISOPT")) {
            str = cps.getAcisOptEntry();
          }
          else if (mypage.equals("TOO")) {
            str = cps.getTooEntry();
          }
          else if (mypage.equals("CONSTRAINT")) {
            str = cps.getConstraintEntry();
          }
          else if (mypage.equals("REMARKS")) {
            str = cps.getRemarksEntry();
          }
          else if (mypage.equals("MONITOR")) {
            isXML=false;
            str = cps.getMonitorEntry();
          }
          else if (mypage.equals("SPATWIN")) {
            isXML=false;
            str = cps.getSpatialWindowEntry();
          }
          else if (mypage.equals("ROLL")) {
            isXML=false;
            str = cps.getRollEntry();
          }
          else if (mypage.equals("WINDOW")) {
            isXML=false;
            str = cps.getWindowEntry();
          }
          else {
            msg = "Unexpected load page type";
            logger.error(msg);
            str = "<data>\n";
            str += "<emsg>" + msg + "</emsg>,";
            str += "</data>\n";
          }
        }

      } catch (Exception ex) {
         logger.error("uid="+uid+": for pid=" + pid,ex);
         msg = "Unable to retrieve data for" + propno;
         propno="Invalid";
      }

    }
    if (propno.equals("Invalid")) {
      str = "<data>\n";
      str += "<propno>" + propno + "</propno>,";
      str += "<emsg>" + msg + "</emsg>,";
      str += "</data>\n";
    }
    if (isXML) {
      response.setContentType("text/xml");
      out=response.getWriter();
      out.println(CPSConstants.XMLSTR);
    }  else {
      response.setContentType("application/json");
      response.setCharacterEncoding(CPSConstants.CHARSET);
      out=response.getWriter();
    }

    logger.debug(mypage + " done.");
    outputStr.append(str);
    logger.trace(outputStr.toString());
    out.println(outputStr.toString());
    out.close();
  }

  /**
   * Private variables
   */
   private static final long serialVersionUID = 1;
   private Properties cpsProperties;
   private static Logger logger = Logger.getLogger(LoadTarget.class);



  /**
   * Private methods
   */

  /****************************************************************************/


}

/******************************************************************************/
