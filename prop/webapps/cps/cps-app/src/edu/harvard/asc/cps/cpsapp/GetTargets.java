package edu.harvard.asc.cps.cpsapp;
/************************************************************************/
/*     Copyrights:                                                      */
/*                                                                      */
/*     Copyright (c) 2017-2019 Smithsonian Astrophysical Observatory	*/
/*                                                                      */
/*    Permission to use, copy, modify, distribute,  and  sell  this	*/
/*    software  and  its  documentation  for  any purpose is hereby	*/
/*    granted  without  fee,  provided  that  the  above  copyright	*/
/*    notice  appear  in  all  copies  and that both that copyright	*/
/*    notice and this permission notice appear in supporting  docu-	*/
/*    mentation,  and  that  the  name  of  the  Smithsonian Astro-	*/
/*    physical Observatory not be used in advertising or  publicity	*/
/*    pertaining  to distribution of the software without specific,	*/
/*    written  prior  permission.   The  Smithsonian  Astrophysical	*/
/*    Observatory  makes  no  representations about the suitability	*/
/*    of this software for any purpose.  It  is  provided  "as  is"	*/
/*    without express or implied warranty.				*/
/*    THE  SMITHSONIAN  ASTROPHYSICAL  OBSERVATORY  DISCLAIMS   ALL	*/
/*    WARRANTIES  WITH  REGARD  TO  THIS  SOFTWARE,  INCLUDING  ALL	*/
/*    IMPLIED  WARRANTIES  OF  MERCHANTABILITY  AND FITNESS, IN  NO	*/
/*    EVENT  SHALL  THE  SMITHSONIAN  ASTROPHYSICAL  OBSERVATORY BE	*/
/*    LIABLE FOR ANY SPECIAL,  INDIRECT  OR  CONSEQUENTIAL  DAMAGES	*/
/*    OR  ANY  DAMAGES  WHATSOEVER RESULTING FROM LOSS OF USE, DATA	*/
/*    OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,  NEGLIGENCE  OR	*/
/*    OTHER  TORTIOUS  ACTION, ARISING OUT OF OR IN CONNECTION WITH	*/
/*    THE USE OR PERFORMANCE OF THIS SOFTWARE.				*/
/*                                                                      */
/************************************************************************/

import edu.harvard.asc.cps.xo.*;
import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringEscapeUtils;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Enumeration;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.harvard.cda.proposal.restclient.IRestClient;
import edu.harvard.cda.proposal.restclient.RestClient;
import edu.harvard.cda.proposal.xo.VTargetCPS;
import edu.harvard.cda.proposal.xo.VProposalCPSWithDetails;

/******************************************************************************/

public class GetTargets extends HttpServlet 
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
    boolean isError = false;
    Boolean isDDT=false;
    Boolean isTOO=false;
    Boolean isEdit = false;
    CPSProposal cpsProp = null;
    Integer userId=0;
    int uid=0;
    String msg = "Invalid session";
    StringBuffer str = new StringBuffer("");;

    Properties cpsProperties = CPS.getProperties();
    response.setCharacterEncoding(CPSConstants.CHARSET);

    String parameterName, parameterValue;
    if (logger.isTraceEnabled()) {
      try {
       for ( Enumeration parameters = request.getParameterNames();
             parameters.hasMoreElements(); ) {
         parameterName = (String) parameters.nextElement();
         parameterValue = request.getParameter( parameterName );
         logger.trace( parameterName + " = " +  parameterValue );
       }
      } catch (Exception exc) {
        logger.debug("Exception printing params",exc);
      }
    }


    String pidStr = Parameter.get(request,"pid");
    String operation = Parameter.get(request,"operation");

    // Get the session object.
    HttpSession session = request.getSession( false );
    if (session == null || !request.isRequestedSessionIdValid()) {

      logger.error("session is null" );
      isError = true;
    }
    else {
      logger.debug("session=" + session.getId() + " operation= " + operation);

      try {
        userId=(Integer)session.getAttribute("userId");
        if (userId == null || userId.intValue() <= 0) {
          logger.error("Invalid session, no userid");
          try {
            session.invalidate();
          } catch (Exception e) {
            logger.error(e);
          }

          session = null;
          isError=true;
        }
        else {
          uid=userId.intValue();
        }
       if (!isError) {
         Boolean sess_pid = (Boolean)session.getAttribute(pidStr);
         if (sess_pid == null ) {
           isError=true;
           msg = "Unauthorized proposal access";
           logger.error("uid=" + uid + "): Unauthorized access pid=" + pidStr );
         }
       }

      }
      catch (Exception exc) {
        logger.error(exc.getMessage() );
        logger.debug("Invalid user ",exc );
        isError=true;
      }
    }


    RequestDispatcher dispatcher = null;

    if (!isError) {

      try {
        Integer pid = Integer.valueOf(pidStr);
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        if (cpsProp == null) {
          String currentAO = (String)session.getAttribute("currentAO");
          String ddtAO = (String)session.getAttribute("ddtAO");
          boolean isCfP = CPS.isCfP();
          boolean isGTO = CPS.isGTO();
          boolean ddtRequest=(boolean)session.getAttribute("ddtRequest");
          VProposalCPSWithDetails vprop = api.retrieveProposalInformation(pid.intValue());
          cpsProp = new CPSProposal(vprop,currentAO);
          cpsProp.setEditPrivs(isCfP,ddtRequest,ddtAO,isGTO,uid);
          session.removeAttribute("curprop");
          session.setAttribute("curprop",cpsProp);
        }

        if (operation.equalsIgnoreCase("LOAD") || operation.equalsIgnoreCase("LOADTGTEDIT")) {
          List<VTargetCPS> pss = api.retrieveViewTargetCPSByProposalId(pid.intValue());
          List<CPSTargetSummary> cpssList = new ArrayList<>();
          for (int ii = 0 ; ii < pss.size() ; ii++) {
            //logger.info(ii + ": " + pss.get(ii));
            CPSTargetSummary cpss = new CPSTargetSummary(pss.get(ii),cpsProp.isEdit,cpsProp.isDDTProp,cpsProp.vprop.multiCycle);
            Boolean tooDetails = cpsProp.shouldHaveTOODetails();
            String tstr="";
            if (tooDetails) tstr ="too";
            if (cpss.tgt.instrument_name != null)
              tstr += cpss.tgt.instrument_name;
            logger.trace("TOODetails: " + cpss.tgt.targid + "=" + tstr);
            session.setAttribute(CPS.getInt(cpss.tgt.targid) +"detector",tstr);
            cpssList.add(cpss);
          }
          logger.info("Retrieved " + pss.size() + " for " + pid);
          if (operation.equalsIgnoreCase("LOAD")) {
            StringBuffer outputStr = new StringBuffer();
            response.setContentType("application/json");
            PrintWriter out=response.getWriter();
            outputStr.append("{\n\"rows\":[\n");
            for (int ii = 0 ; ii < cpssList.size() ; ii++) {
              //logger.info("value " + ii + ": " + cpssList.get(ii));
              if (ii != 0) str.append(",");
              str.append(cpssList.get(ii).getGridEntry());
            }
            outputStr.append(str);
            outputStr.append("]\n}\n");
            logger.trace(outputStr.toString());
            out.println(outputStr.toString());
            out.close();
          } else {
            //LOADTGTEDIT
            PrintWriter out=response.getWriter();
            response.setContentType("text/xml");
            out.println(CPSConstants.XMLSTR);
            out.println("<complete>");
            StringBuffer tstr = new StringBuffer("");
            for (int ii = 0 ; ii < cpssList.size() ; ii++) {
              CPSTargetSummary cps= cpssList.get(ii);
              String itemval = CPS.getInt(cps.tgt.targid);
              String tname = CPS.getString(cps.tgt.targname);
              if ((tname == null || tname.length() <2) && !CPS.getString(cps.tgt.ss_object).equalsIgnoreCase("NONE"))
                tname = CPS.getString(cps.tgt.ss_object);
              if (tname == null ) tname="";
              String tval = "#" + CPS.getInt(cps.tgt.targ_num) + ": " + tname;
              tstr.append("<option value=\"" + CPS.getXmlString(itemval) +  "\"><![CDATA[" + CPS.getString(tval) + "]]></option>");

            }
            logger.trace(tstr);
            out.println(tstr);
            out.println("</complete>");
            out.close(); 
          }
        }
        else if (operation.equals("PAGELOAD")) {
          // just getting some main proposal info
          String propno = cpsProp.vprop.proposalNumber;
          isDDT = cpsProp.isDDTProp;
          isEdit = cpsProp.isEdit;
          isTOO = cpsProp.isDDTProp;
          if (!isTOO) {
            if (cpsProp.vprop.type.indexOf("TOO") >= 0) {
              isTOO= true;
            }
          }
          String emsg="";
          if (!cpsProp.isViewable(uid)) {
            emsg="This proposal is no longer viewable.";
            propno="Error";
          }
          PrintWriter out=response.getWriter();
          response.setContentType("text/xml");
          out.println(CPSConstants.XMLSTR);
          out.println("<data>");
          out.println("<propno>" + propno +  "</propno>");
          out.println("<proposal_title><![CDATA[" + CPS.getString(cpsProp.vprop.title) +  "]]></proposal_title>");
          out.println("<pid>" + pidStr + "</pid>");
          out.println("<isEdit>" + isEdit.toString() + "</isEdit>");
          out.println("<isDDT>" + isDDT.toString() + "</isDDT>");
          out.println("<isTOO>" + isTOO.toString() + "</isTOO>");
          out.println("<tgtcnt>" + "1" + "</tgtcnt>");
          out.println("<emsg><![CDATA[" + CPS.getString(emsg)  + "]]></emsg>");
          out.println("</data>");
          out.close();
        }
      } catch (Exception exc) {
        logger.error("TARGETS" ,exc);
        isError = true;
      }
    } 

    if (isError) {
      str = new StringBuffer("{\nrows:[\n");
      str.append("{id:'error', data:[,\"" + msg + "\",,,,,,,] }\n");
      str.append("]}\n");
      response.setContentType("application/json");
      PrintWriter out=response.getWriter();
      logger.debug(str);
      out.println(str);
      out.close();
    }
  }

  /**
   * Private variables
   */
   private static final long serialVersionUID = 1;
   private static Logger logger = Logger.getLogger(GetTargets.class);




  /**
   * Private methods
   */

  /****************************************************************************/


}

/******************************************************************************/
