package edu.harvard.asc.cps.cpsapp;
/************************************************************************/
/*     Copyrights:                                                      */
/*                                                                      */
/*     Copyright (c) 2017,2020 Smithsonian Astrophysical Observatory    */
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
import edu.harvard.cda.jutil.rest.InternalServerException;
import edu.harvard.asc.cps.xo.*;
import edu.harvard.cda.proposal.restclient.IRestClient;
import edu.harvard.cda.proposal.restclient.RestClient;
import edu.harvard.cda.proposal.xo.*;

import org.apache.log4j.Logger;
import java.lang.Thread;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Date;
import java.nio.file.Files;
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

public class SaveProposal extends HttpServlet 
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

    cpsProperties = CPS.getProperties(context);
    uploadPath = cpsProperties.getProperty("cps.upload.path");

    
  }

  /****************************************************************************/
  /**
   * Handle a submission from a browser.  Five operations are provided:
   * <ol>
   *    <li>NULL - get the proposals?
   *    <li>CREATE - 
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
    RequestDispatcher dispatcher = null;
    HttpSession session;
    CPSProposal cps;

    request.setCharacterEncoding(CPSConstants.CHARSET);
    logger.trace("DBG: " + request.getCharacterEncoding());
    // Get the session object.
    session = request.getSession(false);
    if (session == null || !request.isRequestedSessionIdValid()) {
      logger.error("Invalid Session");
      response.sendError(HttpServletResponse.SC_FORBIDDEN,"Invalid Session");
      return;
    }
    //logger.info( "Session "  + session.getId());

    // For debugging, output all the parameters.
    //logger.debug( "Dumping parameters ..." );
    String parameterName, parameterValue;
    try {
      for ( Enumeration parameters = request.getParameterNames();
          parameters.hasMoreElements(); ) {
        parameterName = (String) parameters.nextElement();
        parameterValue = request.getParameter( parameterName );
        logger.debug( parameterName + " = " +  parameterValue );
      }
    } catch (Exception exc) {
       logger.error("Exception printing params",exc);
    }

    String mypage = "";

    mypage= Parameter.get(request,"page");
    String pid = Parameter.get(request,"pid");
    Integer ipid = Integer.valueOf(0);
    try {
       ipid = Integer.valueOf(pid);
    } catch (Exception x) {
       ipid = 0;
    } 
        
    String operation = Parameter.get(request, "operation" );
   
    if (mypage == null) {
       logger.error ("page is null");
       dispatcher = getServletContext().getRequestDispatcher("/app/prop_prop_summary.jsp?id=" + pid);
      dispatcher.forward(request, response);
    }
    else {
     if ( operation == null ) operation = "";
     String msg = operation;
     String msg2 = "";
     logger.info(operation +  ": " + mypage + " for " + ipid);

     cps= (CPSProposal) session.getAttribute("curprop");
     if (cps == null) {
       msg = "ERROR: Unable to retrieve current proposal";
     }
     else if ( cps.vprop.proposalId != ipid.intValue()) {
       msg = "ERROR: Unable to retrieve proposal";
     }
     else {
       if (operation.equals("SAVE")) {
         if (mypage.equals("PROPOSAL")) {
          msg = processCover(session,request,cps);
         }
         else if (mypage.equals("JOINT")) {
          msg = processJoint(session,request,cps);
         }
         else if (mypage.equals("PI")) {
          msg = processPI(session,request,cps);
         }
         else if (mypage.equals("COI")) {
          msg = processCoIContact(session,request,cps);
         }
         else if (mypage.equals("DDT")) {
          msg = processDDT(session,request,cps);
         }
         else {
           msg = "Unexpected " + mypage;
           logger.debug("SAVE: " + msg );
         }
       } else if (operation.equals("MODIFYCOI")) {
          msg = processModifyCoI(session,request,cps);
          if (msg.indexOf(CPSConstants.SAVE_OK) >= 0) {
            msg = processCoIContact(session,request,cps);
          }
       }
       else if (operation.equals("ADDCOI")) {
          msg = processModifyCoI(session,request,cps);
          if (msg.indexOf(CPSConstants.SAVE_OK) >= 0) {
            msg = processCoIContact(session,request,cps);
          }
       }
       else if (operation.equals("DELETECOI")) {
          msg = processDeleteCoI(session,request,cps);
       }
       else if (operation.equals("DELETECOIOBS")) {
          msg = processDeleteCoI(session,request,cps);
          if (msg.indexOf(CPSConstants.SAVE_OK) >= 0) {
            msg = processCoIContact(session,request,cps);
          }
       }
       else if (operation.equals("RENUMBERCOI")) {
          msg = processRenumberCoI(session,request,cps);
       }
       else if (mypage.equals("UPLOAD") && operation.equals("DEMOTE")) {
          msg = processDemoteUpload(session,request,cps);
       } 
       if (msg.indexOf(CPSConstants.SAVE_OK) >= 0) {
          updateLastSaved(session,cps.vprop.proposalId);
       }
     }
     logger.info("Save for " + mypage + ":" + msg);
     //try {
     //Thread.sleep(3000);
     //} catch (Exception e) {};


     response.setContentType("text/plain");
     PrintWriter out=response.getWriter();
     if (!msg.startsWith(CPSConstants.SAVE_OK)) {
        out.print(CPSConstants.SAVE_FAILED);
     }

     out.print(msg);
     out.close();

    }

  }

  private String processCover(HttpSession session,HttpServletRequest request,CPSProposal cps)
  {
    String msg = "";
    cps.vprop.type = Parameter.get(request,"proposal_type");
    cps.vprop.scienceKeywords = Parameter.get(request,"scikeyResults");
    cps.vprop.categoryDescription = Parameter.get(request,"category_descrip");
    cps.vprop.title = Parameter.get(request,"proposal_title");
    cps.vprop.abstractText = Parameter.get(request,"abstract");
    String rights_just = Parameter.get(request,"rights_justification");
    cps.vprop.linkedPropNum = Parameter.get(request,"linked_propnum");
    try {
      cps.vprop.linkedProposal = Parameter.getBoolean(request,"linked_proposal"); 
    } catch (Exception exc) {
       msg += "Invalid value for Linked Proposal\n";
       logger.error("linked",exc);
    }
    try {
      cps.vprop.requestExtraFlag = Parameter.getChar(request,"request_extra_flag");
    } catch (Exception exc) {
       msg += "Invalid value for Proprietary Rights\n";
       logger.error("rights",exc);
    }
    try {
      cps.vprop.multiCycle = Parameter.getBoolean(request,"multi_cycle"); 
    } catch (Exception exc) {
       msg += "Invalid value for MultiCycle\n";
       logger.error("multicycle",exc);
    }
    try {
      cps.vprop.totalTime = Parameter.getDouble(request,"requested_budget"); 
    } catch (Exception exc) {
       msg += "Invalid value for Requested Budget\n";
       logger.error("totaltime",exc);
    }

    if (msg.equals("")) {
      CPSUtils cpsutil = new CPSUtils();
      try {
        ProposalUpdateCoverDatum pc = new ProposalUpdateCoverDatum(
	  cps.vprop.proposalId,cps.vprop.type,cps.vprop.categoryDescription,
	  cps.vprop.requestExtraFlag,rights_just,cps.vprop.multiCycle,
	  cps.vprop.linkedProposal,cps.vprop.linkedPropNum,
	  cps.vprop.title,cps.vprop.scienceKeywords,cps.vprop.abstractText,
	  cps.vprop.totalTime);
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        api.proposalUpdateCover(pc);

        msg=CPSConstants.SAVE_OK;
      } catch (InternalServerException iexc) {
          logger.error("Cover-i",iexc);
          msg = "Error occurred saving Proposal Cover information. Data NOT saved.";
          msg += "\n" + iexc.getMessage();
      } catch (Exception exc) {
          logger.error("Cover",exc);
          msg = "Unable to save Proposal Cover information";
      }
    }

    return msg;
  }
  

  private String processJoint(HttpSession session,HttpServletRequest request,CPSProposal cps)
  {
    String joint="";
    String msg="";
      
  
    try {
      cps.vprop.hstTime = Parameter.getInteger(request,"hst_time");
      cps.vprop.hstInstruments = Parameter.get(request,"hst_instr");
    } catch (Exception exc) {
      logger.error("Invalid HST",exc);
      msg += "ERROR: Invalid HST parameters";
    }
    try {
      cps.vprop.xmmTime = Parameter.getDouble(request,"xmm_time");
    } catch (Exception exc) {
      logger.error("Invalid XMM",exc);
      msg += "ERROR: Invalid XMM parameter";
    }
    try {
      cps.vprop.swiftTime = Parameter.getDouble(request,"swift_time");
    } catch (Exception exc) {
      logger.error("Invalid Swift",exc);
      msg += "ERROR: Invalid Swift parameter";
    }
    try {
      cps.vprop.nustarTime = Parameter.getDouble(request,"nustar_time");
    } catch (Exception exc) {
      logger.error("Invalid NuSTAR",exc);
      msg += "ERROR: Invalid NuSTAR parameter";
    }
    try {
      cps.vprop.noaoTime = Parameter.getDouble(request,"noao_time");
      cps.vprop.noaoInstruments = Parameter.get(request,"noao_instr");
    } catch (Exception exc) {
      logger.error("Invalid NOAO",exc);
      msg += "ERROR: Invalid NOAO parameters";
    }
    try {
      cps.vprop.nraoTime = Parameter.getDouble(request,"nrao_time");
      cps.vprop.nraoInstruments = Parameter.get(request,"nrao_instr");
    } catch (Exception exc) {
      logger.error("Invalid NRAO",exc);
      msg += "ERROR: Invalid NRAO parameters";
    }
    cps.vprop.joint = Parameter.get(request,"joint_type");
    
    if (msg.equals("")) {
      try {
        JointFieldsDatum jf = cps.vprop.getJointFields();
      
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        api.jointInsert(jf);

        msg=CPSConstants.SAVE_OK;
      } catch (InternalServerException iexc) {
          logger.error("Cover-i",iexc);
          msg = "Error occurred saving Joint information. Data NOT saved.";
          msg += "\n" + iexc.getMessage();
      } catch (Exception exc) {
          logger.error("Joint",exc);
          msg = "Unable to save Joint data";
          msg = "Error occurred saving Joint information. Data NOT saved.";
      }
    }

     
    return msg;
  }
  private String processPI(HttpSession session,HttpServletRequest request,CPSProposal cps)
  {
    String msg = "";
    String id="";
    Integer piid= null;
    try {
      id = Parameter.get(request,"id");
      cps.vprop.first = Parameter.getEncoded(request,"first");
      cps.vprop.middle = Parameter.getEncoded(request,"middle");
      cps.vprop.last = Parameter.getEncoded(request,"lastname");
      cps.vprop.institution = Parameter.getEncoded(request,"institute");
      cps.vprop.street = Parameter.getEncoded(request,"street");
      cps.vprop.department = Parameter.getEncoded(request,"dept");
      cps.vprop.mailStop = Parameter.getEncoded(request,"mailstop");
      cps.vprop.city = Parameter.getEncoded(request,"city");
      cps.vprop.state = Parameter.getEncoded(request,"state");
      cps.vprop.zip = Parameter.get(request,"zip");
      cps.vprop.country = Parameter.get(request,"country");
      cps.vprop.orcId = Parameter.get(request,"orcid");
      cps.vprop.email = Parameter.get(request,"email");
      cps.vprop.phone = Parameter.get(request,"telephone");
    } catch (Exception exc) {
       msg += "Invalid  PI Parameters. " + exc.getMessage();
       logger.error("PI",exc);
    }
    try {
      if (id != null && !id.equals("")) {
        piid = Integer.valueOf(id); 
      }
    } catch (Exception exc) {
      logger.error("Unable to convert PI id to integer" + id);
    }

    if (msg.equals("")) {
      try {
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        if (piid == null || piid <= 0) {
          ProposalUpdatePIDatum pc = new ProposalUpdatePIDatum(
	    cps.vprop.first,cps.vprop.middle, cps.vprop.last,
	    cps.vprop.email,cps.vprop.mailStop,
	    cps.vprop.phone,cps.vprop.institution,
	    cps.vprop.department,cps.vprop.street,
	    cps.vprop.city,cps.vprop.state,
	    cps.vprop.zip,cps.vprop.country,
	    cps.vprop.orcId);
          api.proposalUpdatePI(cps.vprop.proposalId,pc);
        } else {
          api.proposalUpdatePIByPersId(cps.vprop.proposalId,piid);
        }

        msg=CPSConstants.SAVE_OK;
      } catch (InternalServerException iexc) {
          logger.error("PI",iexc);
          msg = "Error occurred saving Proposal PI information. Data NOT saved.";
          msg += "\n" + iexc.getMessage();
      } catch (Exception exc) {
          logger.error("PI",exc);
          msg = "Unable to save Proposal PI information";
      }
    }

    return msg;
  }

  private String processCoIContact(HttpSession session,HttpServletRequest request,CPSProposal cps)
  {
    String msg = "";
    try {
      cps.vprop.coiContact = Parameter.getBoolean(request,"coi_contact");
      cps.vprop.coiPhone = Parameter.get(request,"coi_phone");
      cps.vprop.costPiCoin = Parameter.getInteger(request,"cost_pi");
    } catch (Exception exc) {
       msg += "Invalid CoI Contact Parameters.";
       logger.error("CoIContact",exc);
    }

    if (msg.equals("")) {
      try {
        ProposalUpdateCOIContactDatum pc = new ProposalUpdateCOIContactDatum(
	  cps.vprop.coiContact.booleanValue(),cps.vprop.coiPhone,
	  cps.vprop.costPiCoin);
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        api.proposalUpdateCOIContact(cps.vprop.proposalId,pc);

        msg=CPSConstants.SAVE_OK;
      } catch (InternalServerException iexc) {
          logger.error("COIContact",iexc);
          msg = "Error occurred saving Proposal CoI Contact information. Data NOT saved.";
          msg += "\n" + iexc.getMessage();

      } catch (Exception exc) {
          logger.error("CoIContact",exc);
          msg = "Unable to save Proposal Coi Contact information";
      }
    }

    return msg;
  }
  private String processModifyCoI(HttpSession session,HttpServletRequest request,CPSProposal cps)
  {
    String msg = "";
    Integer coi_number = null;
    Integer persid = null;
    String id = null;
    try {
      id = Parameter.get(request,"id");
      cps.vprop.first = Parameter.getEncoded(request,"coi_first");
      cps.vprop.last = Parameter.getEncoded(request,"coi_last");
      cps.vprop.institution = Parameter.getEncoded(request,"coi_institute");
      cps.vprop.country = Parameter.get(request,"coi_country");
      cps.vprop.email = Parameter.get(request,"coi_email");
      coi_number= Parameter.getInteger(request,"coi_number");
      if (coi_number != null && coi_number <= 0)
        coi_number=null;
    } catch (Exception exc) {
       msg += "Invalid  CoI Parameters.";
       logger.error("CoI",exc);
    }
    try {
      if (id != null && !id.equals("")) {
        persid = Integer.valueOf(id); 
      }
    } catch (Exception exc) {
      logger.error("Unable to convert PI id to integer" + id);
    }


    if (msg.equals("")) {
      try {
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        if (persid == null || persid <= 0) {
          ProposalIngestCOIDatum pc = new ProposalIngestCOIDatum(
	    cps.vprop.first,cps.vprop.last,cps.vprop.email,
	    cps.vprop.institution,cps.vprop.country,null);
          if (coi_number==null)
            api.proposalInsertCOI(cps.vprop.proposalId,pc);
          else
            api.proposalUpdateCOI(cps.vprop.proposalId,coi_number.intValue(),pc);
        } else {
          // We have a 'selectable' CoI
          api.proposalUpdateCOIByPersId(cps.vprop.proposalId,coi_number,persid);
        }

        msg=CPSConstants.SAVE_OK;
      } catch (InternalServerException iexc) {
          logger.error("PI",iexc);
          msg = "Error occurred saving Proposal CoI information. Data NOT saved.";
          msg += "\n" + iexc.getMessage();
      } catch (Exception exc) {
          logger.error("CoI",exc);
          msg = exc.getMessage();
      }
    }

    return msg;
  }
  private String processDeleteCoI(HttpSession session,HttpServletRequest request,CPSProposal cps)
  {
    String msg = "";
    Integer gridValues = null;
    try {
      gridValues= Parameter.getInteger(request,"gridvalues");
    } catch (Exception exc) {
       msg += "Unable to identify CoI to delete.";
       logger.error("CoI",exc);
    }

    if (msg.equals("")) {
      try {
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        api.coiDelete(cps.vprop.proposalId,gridValues.intValue());

        msg=CPSConstants.SAVE_OK;
      } catch (InternalServerException iexc) {
          logger.error("PI",iexc);
          msg = "Error occurred deleting CoI information. Data NOT saved.";
          msg += "\n" + iexc.getMessage();
      } catch (Exception exc) {
          logger.error("CoI",exc);
          msg = "Unable to Delete CoI ";
      }
    }

    return msg;
  }

  private String processRenumberCoI(HttpSession session,HttpServletRequest request,CPSProposal cps)
  {
    String msg = "";
    String cmsg = "";
    Integer gridValues = null;
    Integer coi_number = null;
    try {
      gridValues= Parameter.getInteger(request,"gridvalues");
      coi_number= Parameter.getInteger(request,"coi_number");
      ProposalCoInvestigator coin = cps.getObserver();
      if (coi_number.intValue() == 1 || 
        (coin !=null && gridValues != null && coin.coin_id == gridValues.intValue() && coin.coin_number == 1)) {
        Boolean coi_contact= Parameter.getBoolean(request,"coi_contact");
        if (coi_contact) {
          cmsg = "<span class='errmsg'>The observer has changed, please make sure the Observer information is correct.</span>";
        }
      }
    } catch (Exception exc) {
       msg += "Unable to identify CoI to renumber.";
       logger.error("CoI",exc);
    }

    if (msg.equals("")) {
      try {
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        api.coiRenumber(cps.vprop.proposalId,gridValues.intValue(),
		coi_number.intValue());

        msg=CPSConstants.SAVE_OK;
        msg += cmsg;
      } catch (InternalServerException iexc) {
          logger.error("CoIRenumber",iexc);
          msg = "Error occurred renumbering CoIs. Data NOT saved.";
          msg += "\n" + iexc.getMessage();
      } catch (Exception exc) {
          logger.error("CoI",exc);
          msg = "Unable to Renumber CoIs. ";
      }
    }

    return msg;
  }


  private String processDDT(HttpSession session,HttpServletRequest request,CPSProposal cps)
  {
    String msg="";
    Boolean xmm_ddt = false;
    Boolean prev_request = false;
  
    try {
      if (cps.vprop.ddtProposalDatum == null) {
          cps.vprop.ddtProposalDatum = new DDTProposalDatum();
      }
      cps.vprop.ddtProposalDatum.contact_info = Parameter.get(request,"contact_info");
      cps.vprop.ddtProposalDatum.target_justification = Parameter.get(request,"target_justification");
      cps.vprop.ddtProposalDatum.next_cfp = Parameter.get(request,"next_cfp");
      cps.vprop.ddtProposalDatum.transient_behavior = Parameter.get(request,"transient_behavior");
      cps.vprop.ddtProposalDatum.response_time = Parameter.get(request,"response_time");
      cps.vprop.ddtProposalDatum.response_justification = Parameter.get(request,"response_justification");
      xmm_ddt = Parameter.getBoolean(request,"xmm_ddt",false);
      cps.vprop.ddtProposalDatum.xmm_status = Parameter.get(request,"xmm_status");
      prev_request = Parameter.getBoolean(request,"prev_request",false);
      cps.vprop.ddtProposalDatum.prev_cycles = Parameter.get(request,"prev_cycles");
    } catch (Exception exc) {
      logger.error("Invalid DDT",exc);
      msg += "ERROR: Invalid DDT parameters";
    }
    if (msg.equals("")) {
      try {
        ProposalUpdateDDTDatum pc = new ProposalUpdateDDTDatum(
	  cps.vprop.ddtProposalDatum.contact_info,
          cps.vprop.ddtProposalDatum.target_justification,
	  cps.vprop.ddtProposalDatum.next_cfp,
          cps.vprop.ddtProposalDatum.transient_behavior,
	  cps.vprop.ddtProposalDatum.response_time,
          cps.vprop.ddtProposalDatum.response_justification,
	  xmm_ddt, cps.vprop.ddtProposalDatum.xmm_status,
	  prev_request, cps.vprop.ddtProposalDatum.prev_cycles);

        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        api.proposalUpdateDDT(cps.vprop.proposalId,pc);
      
        msg=CPSConstants.SAVE_OK;
      } catch (InternalServerException iexc) {
          logger.error("DDT",iexc);
          msg = "Error occurred saving DDT details. Data NOT saved.";
          msg += "\n" + iexc.getMessage();

      } catch (Exception exc) {
          logger.error("DDT",exc);
          msg = "Unable to save DDT data";
      }
    }
    return msg;
  }

  private String processDemoteUpload(HttpSession session,HttpServletRequest request,CPSProposal cps)
  {
    String msg="";
   
    String uptype= Parameter.get(request,"utype");

    try {
      CPSUtils cpsutil = new CPSUtils();
      IRestClient api = cpsutil.getAPI(cpsProperties,session);
      api.proposalDemoteUpload(cps.vprop.proposalId,uptype);
      msg=CPSConstants.SAVE_OK;
    } catch (InternalServerException iexc) {
      logger.error("DemoteUpload (internal)",iexc);
      msg = "Error occurred removing PDF. Data NOT saved.";
      msg += "\n" + iexc.getMessage();
    } catch (Exception iexc) {
      logger.error("ProfileCV",iexc);
      msg = "Error occurred removing PDF. Data NOT saved.";
    }

    return msg;
    
  }
      

  private void updateLastSaved(HttpSession session,int proposalId)
  {
    try {
      CPSUtils cpsutil = new CPSUtils();
      IRestClient api = cpsutil.getAPI(cpsProperties,session);
      api.proposalUpdateLastSaved(proposalId);
    } catch (Exception exc) {
      logger.error("LastSaved",exc);
    }

  }


/******************************************************************************/
  /**
   * Private variables
   */
    private static final long serialVersionUID = 1;
    private Properties cpsProperties;
    private String uploadPath;
    private static Logger logger = Logger.getLogger(SaveProposal.class);




  /**
   * Private methods
   */

  /****************************************************************************/


}

/******************************************************************************/
