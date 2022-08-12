package edu.harvard.asc.cps.cpsapp;

import edu.harvard.asc.cps.xo.*;
import edu.harvard.cda.proposal.restclient.IRestClient;
import edu.harvard.cda.proposal.restclient.RestClient;
import edu.harvard.cda.proposal.xo.ProposalKeys;
import edu.harvard.cda.proposal.xo.ProposalUpdateStatus;
import edu.harvard.cda.proposal.xo.ProposalInsertDatum;

import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringEscapeUtils;
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

public class PropManager extends HttpServlet 
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
   * <ol>
   *    <li>NULL - get the proposals?
   *    <li>CREATE - create/clone proposal
   *    <li>TRANSFER - transfer ownership of proposal
   *    <li>STATUS - status updates 
   * </ol>
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void service( HttpServletRequest request,
		       HttpServletResponse response )
    throws ServletException, IOException
  {
    String str;

    Properties cpsProperties = CPS.getProperties();
    int pid;
    CPSMsg cpsmsg = new CPSMsg();


    // For debugging, output all the parameters.
    logger.trace( "Dumping parameters ..." );
    String parameterName, parameterValue;
    try {
        for ( Enumeration parameters = request.getParameterNames();
          parameters.hasMoreElements(); ) {
          parameterName = (String) parameters.nextElement();
          parameterValue = request.getParameter( parameterName );
          logger.trace( parameterName + " = " +  parameterValue );
        }
    } catch (Exception exc) {
         logger.error(exc.getMessage());
         logger.debug("printing params",exc);
    }
    // Get the session object.
    HttpSession session = request.getSession(false);
    RequestDispatcher dispatcher = null;
    if (session == null || !request.isRequestedSessionIdValid()) {
       response.setContentType("text/plain");
       PrintWriter out=response.getWriter();
       out.println("Invalid session");
       return;
    }
    logger.debug( "Session "  + session.getId());
    int uid = (int)session.getAttribute("userId");
    
    String operation = Parameter.get(request,"operation");
 
    // DDT proposal
    boolean ddtRequest = (boolean)session.getAttribute("ddtRequest");

    boolean isCfP = CPS.isCfP();
    boolean isGTO = CPS.isGTO();
    logger.info("operation = " + operation + " ddt=" + ddtRequest + " cfp=" + isCfP + " gto=" + isGTO ); 

    if ( operation == null || operation.equals( "" ) )
    {
       logger.info( "operation is null " );
       response.setContentType("text/plain");
       PrintWriter out=response.getWriter();
       out.println("Invalid request");
       return;
    } else if ( operation.equalsIgnoreCase("CREATE")) {
       response.setContentType("text/plain");
       PrintWriter out=response.getWriter();
       cpsmsg = processCreate(request,session,cpsProperties,ddtRequest,isCfP,isGTO, uid);
       out.println(cpsmsg.msg);
       out.close();
    } else if ( operation.equalsIgnoreCase("STATUS")) {
       response.setContentType("text/plain");
       PrintWriter out=response.getWriter();
       cpsmsg = processStatusUpdate(request,session,cpsProperties);
       out.println(cpsmsg.msg);
       out.close();
    } else if ( operation.equalsIgnoreCase("TRANSFER")) {
       response.setContentType("text/plain");
       PrintWriter out=response.getWriter();
       cpsmsg = processTransferUpdate(request,session,cpsProperties);
       out.println(cpsmsg.msg);
       out.close();
    }
    else {
       logger.info( "operation is  "  + operation);
       response.setContentType("text/plain");
       PrintWriter out=response.getWriter();
       out.println("Unknown request");
       out.close();
    }

  }

  private CPSMsg processStatusUpdate(HttpServletRequest request,HttpSession session,Properties cpsProperties)
  {
       CPSMsg cpsmsg = new CPSMsg();
       String status=null;
       String propno = Parameter.get(request, "propno" );
       String pidStr = Parameter.get(request, "pid" );
       String pstat = Parameter.get(request, "changestatus" );
       Boolean isEdit = (Boolean)session.getAttribute(pidStr);
       try {
         Integer pid = Integer.valueOf(pidStr);
         if (isEdit) {
           CPSUtils cpsutil = new CPSUtils();
           IRestClient api = cpsutil.getAPI(cpsProperties,session);
           api.proposalUpdateStatus(pid,ProposalUpdateStatus.fromValue(pstat,true));
           logger.info("Status update successful for " + pidStr + "," + pstat);
           cpsmsg.msg = "Status update successful for " + propno;
         }
         else {
           cpsmsg.retval=false;
           cpsmsg.msg = "The Deadline has passed. No updates are allowed.";
         }
       }
       catch (Exception exc) {
         cpsmsg.retval = false;
         logger.error(exc);
         cpsmsg.msg = "Unable to update status for " + propno;
       }
       
       return cpsmsg;
  }
  private CPSMsg processTransferUpdate(HttpServletRequest request,HttpSession session,Properties cpsProperties)
  {
       CPSMsg cpsmsg = new CPSMsg();
       String status=null;
       String propno = Parameter.get(request, "propno" );
       String pidStr = Parameter.get(request, "pid" );
       String plast = Parameter.get(request, "lasttransfer" );
       Boolean isEdit = (Boolean)session.getAttribute(pidStr);
       try {
         Integer pid = Integer.valueOf(pidStr);
         Integer persId = null;
         if (isEdit) {
           try {
             persId = Integer.valueOf(plast);
           } catch(Exception ex) {
             logger.error(ex);
           }
           if (persId == null) {
             cpsmsg.retval=false; 
             cpsmsg.msg = "Unable to validate selected Owner";
           }
           else {

             CPSUtils cpsutil = new CPSUtils();
             IRestClient api = cpsutil.getAPI(cpsProperties,session);
             api.proposalUpdateSubmitter(pid,persId.intValue());
             logger.info("Transfer of ownership successful for " + pidStr + "," + persId);
             cpsmsg.msg = "Transfer of ownership successful for " + propno;
             cpsmsg.retval=true; 
           }
         }
         else {
           cpsmsg.retval=false;
           cpsmsg.msg = "The Deadline has passed. No updates are allowed.";
         }
       }
       catch (Exception exc) {
         cpsmsg.retval = false;
         logger.error(exc);
         cpsmsg.msg = "Unable to transfer ownership for " + propno;
       }
       
       return cpsmsg;
  }

  private CPSMsg processCreate(HttpServletRequest request,HttpSession session, Properties cpsProperties,
	boolean ddtRequest,boolean isCfP, boolean isGTO, int uid)
  {
       CPSMsg cpsmsg = new CPSMsg();
       Integer piid=null;
       Integer coid=null;
       Integer clonepno=null;
       Integer clonepiid=null;

       String cloneprop = Parameter.get(request,"cloneprop");
       String clonepi = Parameter.get(request,"clonepiid");
       String submitter = Parameter.get(request,"prop_who");
       String subcat = Parameter.get(request,"subject_category");
       String ptype = Parameter.get(request,"proposal_type");
       String jtype = Parameter.get(request,"joint_type");
       if (submitter.equals("PI")) {
          piid = uid;
       }
       if (submitter.equals("CoI")) {
          coid = uid;
       }
       try {
          if (cloneprop != null && !cloneprop.equals("") && !cloneprop.equals("NO")) {
            clonepno = Integer.valueOf(cloneprop);
          }
          if (clonepi != null && !clonepi.equals("") && !clonepi.equals("0")) {
            clonepiid = Integer.valueOf(clonepi);
          }
          // if cloning a proposal and the submitter is not the PI,
          // set the PI to the original PI
          if (piid == null && clonepiid != null && 
              clonepiid.intValue() != uid)  {
            piid = clonepiid;
          }
       } catch (Exception exc) {
          cpsmsg.msg = "Invalid Clone Proposal";
          cpsmsg.retval = false;
          logger.error("Invalid Clone proposal: " + cloneprop);
       } 
       try {
         ProposalInsertDatum pc = new ProposalInsertDatum(subcat,uid,piid,coid,ptype,jtype,ddtRequest);
         CPSUtils cpsutil = new CPSUtils();
         IRestClient api = cpsutil.getAPI(cpsProperties,session);
         ProposalKeys vprop;
         if (ddtRequest || isCfP  ||
             jtype.indexOf("CXO") >= 0 ||
             (ptype.indexOf("GTO") >= 0  && isGTO) ||
             ptype.indexOf("CAL") >= 0 ) {
           // before deadline or is a ddt  or joint or cal or gto
           // go create a proposal and get the proposal number
           if (clonepno == null || clonepno.intValue() <= 0) {
             vprop = api.proposalInsert(pc);
           } else {
             vprop = api.proposalClone(clonepno,pc);
           }
           String propno = vprop.number;
           Integer pid = vprop.id;
           logger.info("CREATE " + propno + " with id=" + pid);
           session.setAttribute(pid.toString(),Boolean.valueOf(true));
           // javascript parses on comma for success
           cpsmsg.msg =String.format("%d,%s,%s",pid,propno,ptype);
         }
         else {
            cpsmsg.msg = "The Deadline has passed. No new proposals are allowed.";
            cpsmsg.retval=false;
         }
       }  catch (Exception exc) {
          logger.error(exc);
          cpsmsg.msg = "Error occurred. Unable to create proposal.";
          cpsmsg.retval=false;
       }
       
       return cpsmsg;
  }

  /**
   * Private variables
   */
    private static final long serialVersionUID = 1;
    private static Logger logger = Logger.getLogger(PropManager.class);




  /**
   * Private methods
   */

  /****************************************************************************/


}

/******************************************************************************/
