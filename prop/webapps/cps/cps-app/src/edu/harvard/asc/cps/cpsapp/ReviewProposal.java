package edu.harvard.asc.cps.cpsapp;
/************************************************************************/
/*     Copyrights:                                                      */
/*                                                                      */
/*     Copyright (c) 2017-2021 Smithsonian Astrophysical Observatory    */
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
import ascds.NameResolver;
import ascds.RunCommand;
import edu.harvard.cda.coords.Coordinate;
import edu.harvard.asc.cps.xo.*;
import edu.harvard.cda.jutil.rest.InternalServerException;
import edu.harvard.cda.proposal.restclient.IRestClient;
import edu.harvard.cda.proposal.xo.*;

import org.apache.commons.lang3.StringEscapeUtils;

import org.apache.log4j.Logger;
import java.lang.Runtime;
import java.lang.Process;
import java.io.*;
import java.util.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/******************************************************************************/
/**
 */

public class ReviewProposal extends HttpServlet 
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
    relPath = cpsProperties.getProperty("ascds.release");
    dataPath = cpsProperties.getProperty("cps.data.path");

    
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
    String pid="";
    String operation="";
    String isGrid="";
    String str = "";
    String msg="";
    String propno="";
    Integer mypid = 0;
    Integer userId = 0;
    int uid = 0;
    Boolean ddtRequest= false;

    HttpSession session;
    CPSProposal cps;
    CPSTargetList tgtList;
    Double slewrate;
    Double segtime;
    Double gridgrp;
    HashSet<Integer> excludeAltSet;


    // Get the session object.
    String url  = response.encodeURL("/prop_logout.jsp?msg=Invalid Session");
    session = request.getSession(false);
    if (session == null || !request.isRequestedSessionIdValid()) {
      logger.error("Invalid Session");
      msg = "Invalid session";
      propno="Invalid";
      response.sendRedirect(url);
    }
    else {
      // For debugging, output all the parameters.
      if (logger.isTraceEnabled()) {
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
           logger.debug("Exception printing params",exc);
        }
      }
  
      try {
        userId = (Integer)session.getAttribute("userId");
        if (userId == null || userId.intValue() <= 0) {
          logger.error("Invalid session, no userid");
          try {
            session.invalidate();
            response.sendRedirect(url);
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
      if (!propno.equals("Invalid")) {
        try {
          pid = Parameter.get(request,"pid");
          operation = Parameter.get(request, "operation" );
          isGrid = Parameter.get(request, "isGrid" );
          if (operation == null) operation="";
          msg = "";
          Boolean sess_pid = (Boolean)session.getAttribute(pid);

         if (sess_pid == null ) {
           propno="Invalid";
           msg = "Unauthorized proposal access";
           logger.error("uid=" + uid + "): Unauthorized access pid=" + pid );
         }
         logger.info("uid=" + uid + "  proposal=" + pid + " operation=" + operation);
         ddtRequest=(Boolean)session.getAttribute("ddtRequest");

        } catch (Exception ex) {
          logger.error("uid="+uid+": for pid=" + pid,ex);
          msg = "Invalid parameters.";
          propno="Invalid";
        }
      }
    }
   
    if (!propno.equals("Invalid")) {
      try {
        segtime = Double.valueOf(cpsProperties.getProperty("SEGMENT.TIME"));
        slewrate = Double.valueOf(cpsProperties.getProperty("SLEW.TAX.RATE"));
        gridgrp = Double.valueOf(cpsProperties.getProperty("GRID.GROUP.MAX"));
      } catch (Exception exc) {
         logger.error("getProperty",exc);
         segtime  =Double.valueOf(90);
         slewrate =Double.valueOf(1.5);
         gridgrp  =Double.valueOf(45);
      }
  
      excludeAltSet = new HashSet<Integer>();

      try {
        mypid = Integer.valueOf(pid);
        String currentAO = (String)session.getAttribute("currentAO");
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);

        cps = (CPSProposal)session.getAttribute("curprop");
        if (cps == null ) {
          msg = "ERROR: Unable to retrieve proposal";
        }
        else {
          try {
            cps.vupload = api.proposalUploadInfo(mypid.intValue());
          } catch (Exception upexc)  {
            logger.error(pid,upexc);
          }
          if ( operation.equals("slewblk") && 
              (cps.isDDTProp || cps.vprop.type.indexOf("TOO")>=0))  {
             List<Integer>aList = api.proposalAltTargetsToExclude(mypid.intValue());
             if (aList != null) {
               for (int aa=0;aa<aList.size();aa++) {
		 excludeAltSet.add(aList.get(aa));
                 logger.debug("Adding to ExcludeAlts: " + aList.get(aa));
               }
            }
          }
          
          tgtList = new CPSTargetList();
          if (!operation.equals("ocatblk")) {
            List<VTargetCPSWithDetails> pss = null;
            pss = api.retrieveViewTargetCPSWithDetailsByProposalId(mypid.intValue());
            for (int ii = 0 ; ii < pss.size() ; ii++) {
              Double totalGroupTime=0.0;
              String responseTime="";
              if (cps.vprop.ddtProposalDatum != null)
                responseTime = cps.vprop.ddtProposalDatum.response_time;

              CPSTarget tgt = new CPSTarget(pss.get(ii),cps.isEdit,cps.isDDTProp,cps.vprop.multiCycle,responseTime,cpsProperties);
              tgt.calcNPntAndSlew(segtime.doubleValue(),slewrate.doubleValue(),
                   gridgrp.doubleValue());

              tgtList.add(tgt);
            }
          }
          if (operation.equals("slewblk"))
            str = getSlewTaxEntry(cps,tgtList,excludeAltSet);
          else if (operation.equals("constraints")) {
            //str = getConstraintEntry(tgtList);  this is the old way
            if ( cps.vprop.type.indexOf("TOO") >= 0) 
              str = getTOOScores(tgtList);
            else
              str = getConstraintScores(cps.vprop.proposalNumber);
         } else if (operation.equals("valgrid"))  
            str = getValidationGridEntry(cps,tgtList);
          else if (operation.equals("valblk")) 
            str = getValidationEntry(cps,tgtList);
          else if (operation.equals("ocatblk"))
            str = getOcatConflictEntry(cps);
          else if (operation.equals("simblk"))
            str = getCoordCheckEntry(tgtList,true);
          else if (operation.equals("SAVE"))
            str = processSave(session,request,cps,tgtList);
        }
      }
      catch (Exception exc) {
        propno ="Invalid";
        msg = "oops";
        logger.error(pid,exc);
      }
    }

    if (propno.equals("Invalid") || propno.equals("Error")) {
      if (operation.equals("simblk") || operation.equals("constraints") ||
 	 operation.equals("valgrid")) {
         str = "{\nrows:[\n";
         str += "{ id:\"" + propno + "\",\ndata:[\"" + msg +"\"]},\n]\n}";
      } else if (!operation.equals("SAVE")) {
        str = "<data>\n";
        str += "<emsg>" + msg + "</emsg>,";
        if (propno.equals("Invalid"))
          str += "<propno>" + propno + "</propno>,";
        str += "</data>\n";
      } else  {
        str += msg;
      }
    }
    logger.info(msg);
    if (operation.equals("ocatblk"))
      logger.trace(str);
    else
      logger.debug(str);

    PrintWriter out=null;
    out=response.getWriter();
    if (operation.equals("simblk") || operation.equals("constraints") ||
        operation.equals("valgrid")) {
      response.setCharacterEncoding(CPSConstants.CHARSET);
      response.setContentType("application/json");
    }
    else if (!operation.equals("SAVE")) {
      response.setContentType("text/xml");
      out.println(CPSConstants.XMLSTR);
    }
    out.println(str);
    out.close();
 
  }

  public String getCoordCheckEntry(CPSTargetList tgtList,boolean isGrid)
  {
      String[] resolverList= new String[]{"SIMBAD","NED"};
      String targname;
      String str = "{\nrows:[\n";
      String result="";

      NameResolver nameResolver = new NameResolver(cpsProperties);
      for (int ii=0; ii< tgtList.size() ; ii++) {
        result="";
        Coordinate coords = new Coordinate();
        CPSTarget cpstgt=tgtList.get(ii);
        targname =  CPS.getString(cpstgt.tgt.targname);
        if (targname == null) targname = "";
        if (targname.length() > 1 &&
	    cpstgt.tgt.ra != null && cpstgt.tgt.dec != null &&
            (cpstgt.tgt.dec != 0 || cpstgt.tgt.ra != 0)) {
          try {
            logger.trace(resolverList[0] + "--" + targname + "--");
            String errmsg = nameResolver.resolve(targname,resolverList);
            if (errmsg == null) {
              coords = new Coordinate(nameResolver.getRA(),
			nameResolver.getDec(),"J2000");
              double arcmin  = calc_distance(cpstgt.tgt.ra,cpstgt.tgt.dec,
		coords.getLon(),coords.getLat());
              result = "distance=" + arcmin;
            }
            else {
              result = errmsg.replaceAll("(\\r|\\n)"," ");
            }
          } catch (Exception exc) {
            logger.error("Yikes",exc);
            result += "Unexpected result from NameResolver.";
          }
        } 
        cpstgt.setCoords(CPS.getDoubleCoord(cpstgt.tgt.ra,0),CPS.getDoubleCoord(cpstgt.tgt.dec,0));
        str += "{ \"id\":\"" + CPS.getInt(cpstgt.tgt.targid) + "\",\ndata:[";
        str += "\"" + CPS.getInt(cpstgt.tgt.targ_num) + "\",";
        str += "\"" + StringEscapeUtils.escapeHtml4(targname) + "\",";
        str += "\"" + StringEscapeUtils.escapeHtml4(CPS.getString(cpstgt.raString)) + "\",";
        str += "\"" + StringEscapeUtils.escapeHtml4(CPS.getString(cpstgt.decString)) + "\",";
        if (coords.getLon() !=0 || coords.getLat() != 0) {
          str += "\"" + StringEscapeUtils.escapeHtml4(coords.getSexagesimalLon()) + "\",";
          str += "\"" + StringEscapeUtils.escapeHtml4(coords.getSexagesimalLat()) + "\",";
        } else {
          str += "\"" + "" + "\",";
          str += "\"" + "" + "\",";
        }
        str += "\"" + StringEscapeUtils.escapeHtml4(result) + "\",";

        str += "] },\n";
      }
      if (tgtList.size() ==0)
        str += "{ \"id\":\"0\",data:[,\"No Targets\",,,,,,] }\n";

      str += "]\n}\n";

      return str;
  }

  public String getValidationGridEntry(CPSProposal cps, CPSTargetList tgtList)
  {
    StringBuffer str = new StringBuffer("{\nrows:[\n");
    int cntr = 0;
    ValidateProposal valResults = new ValidateProposal(cpsProperties);
    valResults.verifyProposal(cps,tgtList);

    for (int ii=0; ii< valResults.errorList.size() ; ii++) {
       str.append("{ \"id\":\"" + (ii+1) + "\",");
       str.append(valResults.errorList.get(ii).getGrid() + "},");
    }
    str.append("]\n}\n");
    return str.toString();
 }

  public String getValidationEntry(CPSProposal cps,CPSTargetList tgtList)
  {
      // but we also need all the targets so it doesn't belong here

      ValidateProposal valResults = new ValidateProposal(cpsProperties);
      valResults.verifyProposal(cps,tgtList);

      String myresult = "";
      StringBuffer errors = new StringBuffer("");
      StringBuffer warnings = new StringBuffer("");
      StringBuffer msgs = new StringBuffer("");
      int cntr=0;
      if (valResults.errorList.size() > 0) {
        for (int ii=0; ii < valResults.errorList.size() ;ii++) {
          if (valResults.errorList.get(ii).isError()) {
            errors.append(valResults.errorList.get(ii).getMsg(true) + "\n");
            cntr++;
          }
        }
        myresult += "Found " + cntr + " Errors.\n";
      } else 
        myresult += "No errors found.";
         
      cntr=0;
      if (valResults.errorList.size() > 0) {
        for (int ii=0; ii < valResults.errorList.size() ;ii++) {
          if (valResults.errorList.get(ii).isWarn()) {
            warnings.append(valResults.errorList.get(ii).getMsg(true) + "\n");
            cntr++;
          }
        }
        myresult += "Found " + cntr + " warnings.\n";
      }
      cntr=0;
      if (valResults.errorList.size() > 0) {
        for (int ii=0; ii < valResults.errorList.size() ;ii++) {
          if (valResults.errorList.get(ii).isNote()) {
            msgs.append(valResults.errorList.get(ii).getMsg(true) + "\n");
            cntr++;
          }
        }
        myresult += "Found " + cntr + " messages.\n";
      }

      if (errors.length() > 1) 
        myresult += "\n\n<span style='text-decoration:underline' class='valerr'>Errors:</span>\n" + errors;
      if (warnings.length() > 1) 
        myresult += "\n\n<span style='text-decoration:underline' class='valwarn'>Messages:</span>\n" + warnings;
      if (msgs.length() > 1) 
        myresult += "\n\n<span style='text-decoration:underline' class='valwarn'>Messages:</span>\n" + msgs;


      String str = "<data>\n";
      str += "<propno>" + cps.vprop.proposalNumber + "</propno>,";
      str += "<pid>" + cps.vprop.proposalId + "</pid>,";
      str += "<operation>val</operation>,";
      str += "<emsg></emsg>,";
      str += "<retstr><![CDATA[" + myresult + "]]></retstr>,";
      str += "</data>\n";

      return str;
  }
  public String getOcatConflictEntry(CPSProposal cps)
  {
     String result = "";
     try {
       String pu= cpsProperties.getProperty("cps.pc");
       String pc= cpsProperties.getProperty("cps.propconflict");
       String cmd = relPath + "/bin/propconflict -U " + pu + " -q " + dataPath + "/.htpc -f " + pc + " -s db=ocat -t db=prop,prop=" + cps.vprop.proposalNumber;
       logger.debug(cmd);

       ArrayList<String> envVarList = setEnvironment();
       RunCommand runtime = new RunCommand(cmd,envVarList,null);
       result = runtime.getOutMsg();
       result += runtime.getErrMsg();
       logger.error(runtime.getErrMsg());
    } catch (Exception ex) {
       logger.error("coordcmd" , ex);
       result += "Error occurred processing results of conflict check.\n";
    }

      String str = "<data>\n";
      str += "<propno>" + cps.vprop.proposalNumber + "</propno>,";
      str += "<pid>" + cps.vprop.proposalId + "</pid>,";
      str += "<emsg></emsg>,";
      str += "<operation>ocat</operation>,";
      str += "<retstr><![CDATA[" + result + "]]></retstr>,";
      str += "</data>\n";

      return str;
  }

  public String getTOOScores(CPSTargetList tgtList) {

     String str = "{\nrows:[\n";

     // TOO scores are counted as 1 per trigger based on response category
     // Then each followup counts as .5  based on accumulating time from
     // initial trigger
     tgtList.setTOOScores();

     for (int ii=0; ii< tgtList.size() ; ii++) {
       CPSTarget cpstgt=tgtList.get(ii);
       str += "{ \"id\":\"" + CPS.getInt(cpstgt.tgt.targid) + "\",\ndata:[";
       str += "\"" + cpstgt.tgt.proposal_number + "\",";
       str += "\"" + cpstgt.tgt.targ_num + "\",";
       str += "\"" + cpstgt.tgt.targname + "\",";
       if (cpstgt.vf_cnt > 0)
         str += "\"" + cpstgt.vf_cnt + "\",";
       else 
         str += "\" \",";
       if (cpstgt.f_cnt > 0)
         str += "\"" + cpstgt.f_cnt + "\",";
       else 
         str += "\" \",";
       if (cpstgt.m_cnt > 0)
         str += "\"" + cpstgt.m_cnt + "\",";
       else 
         str += "\" \",";
       if (cpstgt.s_cnt > 0)
         str += "\"" + cpstgt.s_cnt + "\"";
       else 
         str += "\" \",";
       str += "]},\n";
     }
      str += "] }";

     return str;     
  }

  public String getConstraintScores(String propNum) {
     String result = "";
     try {
       String tmppath = (String)cpsProperties.getProperty("cps.tmp.path");
       String webbin=cpsProperties.getProperty("web.bin.directory");
       String pu= cpsProperties.getProperty("cps.pc");
       // run script from bin area just like we do for the pdf script
       String cmd = webbin + "/prop_resources.tcsh -U " + pu + " -q " + dataPath + "/.htpdf " + " -p " + propNum + " -t " + tmppath;
       logger.info(cmd);

       ArrayList<String> envVarList = setEnvironment();
       RunCommand runtime = new RunCommand(cmd,envVarList,null);
       result = runtime.getOutMsg();
       logger.error(runtime.getErrMsg());
    } catch (Exception ex) {
       logger.error("prop_ddt.tcsh" , ex);
       result += "Error occurred processing constraint scores";
    }
    return result;
  }

  public String getConstraintEntry(CPSTargetList tgtList)
  {
    
    String fmt="%.2f";
    String str = "{\nrows:[\n";
    int cntr = 0;

    logger.trace("getConstraintEntry");
    tgtList.setTargetConstraintGrades();
    logger.trace("AFTER setConstraintEntry tgts=" + tgtList.size() );

    for (int ii=0; ii< tgtList.size() ; ii++) {
      CPSTarget cpstgt=tgtList.get(ii);
      ConstraintGrade wg = cpstgt.getWorstGrade();
      logger.debug("AFTER setConstraintEntry wg=" + wg );
      String worstGrade = "";
      if (wg != null) {
        double prob=1.0;
        if (cpstgt.tgt.probability != null && cpstgt.tgt.probability > 0) 
           prob = cpstgt.tgt.probability;
        if (cpstgt.tgt.monitor_flag != null && cpstgt.tgt.monitor_flag == YesNoPreferred.PREFERRED) 
           prob = .8;


        worstGrade += ConstraintGrade.fromValue(wg.value) + "*" + String.format(fmt,(cpstgt.constrnumPointings *prob));
        str += "{ \"id\":\"" + CPS.getInt(cpstgt.tgt.targid) + "\",\ndata:[";
        str += "\"" + CPS.getInt(cpstgt.tgt.targ_num) + "\",";

        str += "\"" + worstGrade + "\",";  //grade*pnt
        if (cpstgt.unintGrade != null)
          str += "\"" + ConstraintGrade.fromValue(cpstgt.unintGrade.value) + "\"";
        str += ",";
        if (cpstgt.coordGrade != null)
          str += "\"" + ConstraintGrade.fromValue(cpstgt.coordGrade.value) + "\"";
        str += ",";
        if (cpstgt.rollGrade != null)
          str += "\"" + ConstraintGrade.fromValue(cpstgt.rollGrade.value) + "\"";
        str += ",";
        if (cpstgt.winGrade != null)
          str += "\"" + ConstraintGrade.fromValue(cpstgt.winGrade.value) + "\"";
        str += ",";
        if (cpstgt.phaseGrade != null)
          str += "\"" + ConstraintGrade.fromValue(cpstgt.phaseGrade.value) + "\"";
        str += ",";
        if (cpstgt.monGrade != null)
          str += "\"" + ConstraintGrade.fromValue(cpstgt.monGrade.value) + "\"";
        str += ",";
        if (cpstgt.groupGrade != null)
          str += "\"" + ConstraintGrade.fromValue(cpstgt.groupGrade.value) + "\"";
        str += ",";
        if (cpstgt.fupGrade != null)
          str += "\"" + ConstraintGrade.fromValue(cpstgt.fupGrade.value) + "\"";
        
        str += "] },\n";
        cntr += 1;
      }
    }
    if (tgtList.size() ==0)
       str += "{ \"id\":\"0\",data:[,\"No Targets\",,,,,,,,] }\n";
    else if (cntr ==0)
       str += "{ \"id\":\"0\",data:[,\"No Constraints\",,,,,,,,] }\n";
      

    str += "]\n}\n";

    return str;
  }

  public String getSlewTaxEntry(CPSProposal cps,CPSTargetList tgtList,HashSet<Integer>excludeAltSet)
  {
      String lbl="";
      String fmt="%9.2f";

      if (cps.vprop.type.indexOf("TOO") >= 0) 
         lbl = " (with probability)";
      Double slewTotal = tgtList.getSlewTotal(true,excludeAltSet);
      
      //tgtList.setTargetConstraintGrades();
      //String constraints = tgtList.getConstraintTotal(true,excludeAltSet);
      String constraints = "";

      Double tval = Double.valueOf(0.0); 
      if (cps.vprop.totalTime != null) tval = tgtList.getTotalTime(true,excludeAltSet);
      String myresult = "";
      myresult += constraints + "\n\n";
      myresult += "Total Time for Proposal = " + String.format(fmt,tval) + lbl + "\n";
      myresult += "Total Slew Time         = " + String.format(fmt,slewTotal) + lbl + "\n";
      myresult += "                          ------------\n";
      myresult += "                          " + String.format(fmt,tval + slewTotal) + lbl + "\n";

      String str = "<data>\n";
      str += "<propno>" + cps.vprop.proposalNumber + "</propno>,";
      str += "<pid>" + cps.vprop.proposalId + "</pid>,";
      str += "<operation>slew</operation>,";
      str += "<retstr><![CDATA[" + myresult + "]]></retstr>,";
      str += "</data>\n";

      return str;
  }


  private String processSave(HttpSession session,HttpServletRequest request,CPSProposal cps,CPSTargetList tgtList)
  {
    String msg = "";
    String valmsg = "";
    Boolean isSubmit=false;
    Integer allowEdit=0;
    try {
      cps.vprop.availPeerReview = Parameter.get(request,"avail_peer_review");
      if (Parameter.get(request,"ready").equals("1")) isSubmit= true;
      // DEBUG
      if (isSubmit) {
        ValidateProposal valResults = new ValidateProposal(cpsProperties);
        valResults.verifyProposal(cps,tgtList);
        if (valResults.hasErrors() > 0) {
           valmsg += "Proposal submission failed.\nErrors exist in your proposal.\nPlease check the Validation report, correct any errors and try again.\n";
           isSubmit=false;
        }
      }
    } catch (Exception exc) {
       msg += "Error occurred saving proposal.";
       logger.error("Save",exc);
    }

    if (msg.equals("")) {
      try {
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        api.proposalSubmit(cps.vprop.proposalId,cps.vprop.availPeerReview,isSubmit.booleanValue());

        PersonShort ps = null;
        if (valmsg.equals("") && isSubmit) {
           // save off the allow edit flag because it gets reset in db after submission
           allowEdit = cps.vprop.allowEdit;

           // need to re-retrieve w submission date
           cps.vprop = api.retrieveProposalInformation(cps.vprop.proposalId);
           if (cps.vprop.submitterId != null && 
	       cps.vprop.piId.intValue() != cps.vprop.submitterId.intValue()) {
             ps = api.getSubmitterFromProposal(cps.vprop.proposalId);
           }

           valmsg = mailSubmitMessage(ps,cps,allowEdit);
           if (cps.isDDTProp) {
              // create DDT receipt message 
              writeDDTReceipt(cps,tgtList);
              writeDDTConfirmation(cps);
              // create DDT pdf for DDT Manager
              CPSPdf mypdf = new CPSPdf();
              String outdir = cpsProperties.getProperty("ddt.upload.path");
              mypdf.genPDF(cps.vprop.proposalNumber,outdir,cpsProperties,true, false);
           }
        }
        if (valmsg.equals(""))
          msg=CPSConstants.SAVE_OK;
        else 
          msg=valmsg;

      } catch (InternalServerException iexc) {
          logger.error("Submit",iexc);
          String x = iexc.getMessage();
          msg = "Error occurred saving Proposal. Data NOT saved.";
      } catch (Exception exc) {
          logger.error("PI",exc);
          msg = "Unable to save Proposal.";
      }
    }
    return msg;
  }

  // ----------------------------------------------------------------------
  // sends email To: Submitter
  // If PI different from submitter, bcc:PI
  // If Observer, bcc:Observer
  // If allowing edit after the deadline, bcc:cdo
  // ----------------------------------------------------------------------
  private String mailSubmitMessage(PersonShort submitter,CPSProposal cps,Integer allowEdit)
  {
    String msg = "";
    String toAddr = "";
    String ccAddr = "";
    String bccAddr = "";

    String subject = cpsProperties.getProperty("confirm.subject");

    String confirmFile = "";
    if (cps.isDDTProp)  {
      confirmFile = cpsProperties.getProperty("confirm.ddt.msg");
      subject = cpsProperties.getProperty("confirm.ddt.subject");
    }
    else if (cps.vprop.type.indexOf("CAL") >= 0) 
      confirmFile = cpsProperties.getProperty("confirm.nocfp.msg");
    else if (cps.vprop.joint != null && cps.vprop.joint.indexOf("-") >= 0) 
      confirmFile = cpsProperties.getProperty("confirm.nocfp.msg");
    else if (!cps.isArcTheory()) 
      confirmFile = cpsProperties.getProperty("confirm.msg");
    else
      confirmFile = cpsProperties.getProperty("confirm.arc.msg");
    if (confirmFile == null) {
      msg = "Unable to Send confirmation message";
      logger.error("confirm.msg property is missing.");
    }
    if (subject == null) subject = "Chandra Proposal Receipt";
    subject += " " + cps.vprop.proposalNumber;
    logger.trace(confirmFile + " subject=" + subject);

    ProposalCoInvestigator observer = cps.getObserver();


    // now read in file and fill in the info
    if (msg.equals("")) {
      FileReader fileR = null;
      BufferedReader currentFileBR = null;
      try {
        fileR = new FileReader(confirmFile);
        currentFileBR = new BufferedReader(fileR);
        String inputLine = null;
        StringBuffer inputFile = new StringBuffer("");
        while( (inputLine = currentFileBR.readLine()) != null) {
          inputFile.append(inputLine + "\n") ;
        }
        currentFileBR.close();
        String confirmTxt = inputFile.toString();
        confirmTxt = confirmTxt.replaceAll("<PROPOSAL_NUMBER>",cps.vprop.proposalNumber);
        confirmTxt = confirmTxt.replaceAll("<PI_NAME>",(cps.vprop.first + " " + cps.vprop.last));
        String ptitle = cps.vprop.title.replaceAll("\\$","\\\\\\$");
        confirmTxt = confirmTxt.replaceAll("<PROP_TITLE>",ptitle);
        String tstr="";
        if (observer != null) tstr = observer.first + " " + observer.last;
        confirmTxt = confirmTxt.replaceAll("<OBSERVER_NAME>",tstr);
        tstr = "";
        if (submitter != null) 
           tstr = submitter.first + " " + submitter.last;
	else if (cps.vprop.piId.intValue() == cps.vprop.submitterId.intValue()) 
           tstr = cps.vprop.first + " " + cps.vprop.last;
        confirmTxt = confirmTxt.replaceAll("<SUBMITTER_NAME>",tstr);

        tstr = "";
        if (cps.vprop.totalTime != null && cps.vprop.totalTime.doubleValue() > 0) 
          tstr = cps.vprop.totalTime.toString();
        confirmTxt = confirmTxt.replaceAll("<TOTAL_TIME>",tstr);

        tstr = "";
        if (cps.vprop.numTargets != null && cps.vprop.numTargets.intValue() > 0) 
          tstr = cps.vprop.numTargets.toString();
        confirmTxt = confirmTxt.replaceAll("<NUM_TARGETS>",tstr);

        // need to reretrieve because date is null!
        tstr = CPS.convertMS(cps.vprop.submissionDateMSSE,true);
        confirmTxt = confirmTxt.replaceAll("<SUBMISSION_DATE>",tstr);

        if (allowEdit != null && allowEdit.intValue() > 0) {
          String cdoAddr = cpsProperties.getProperty("cdo.email");
          if (cdoAddr == null || cdoAddr.equals("")) {
             logger.error("bcc: cdo.email is null in properties file");
          } else {
            bccAddr = cdoAddr;
          } 
        }

        if ((cps.vprop.type.indexOf("CAL") >= 0)  || 
            (cps.vprop.joint != null && cps.vprop.joint.indexOf('-') > 0)) {
          String tmpstr= cpsProperties.getProperty("notify.email");
          if (tmpstr == null || tmpstr.equals("")) {
            logger.error("BCC notify.email is null in properties file");
          } else {
            if (bccAddr.length() > 2) 
               bccAddr += ",";
            bccAddr += tmpstr;
          } 
        } 
       
        MailUtility mu = new MailUtility(cpsProperties);

        // PI   all as separate emails so we don't expose emails sigh
        toAddr = cps.vprop.email;
        logger.info("MAIL PI: " + toAddr + " cc:" + ccAddr + " " + " bcc: " + bccAddr + " " + subject + "\n");
        mu.mailMessage(mu.getFromEmail(),toAddr,ccAddr,bccAddr,subject,confirmTxt);
        // Now mail submitter if not the PI
        if (submitter != null)  {
          toAddr = submitter.email;
          logger.info("MAIL Submitter: " + toAddr + " " + subject + "\n");
          logger.trace(confirmTxt);
          mu.mailMessage(mu.getFromEmail(),toAddr,null,null,subject,confirmTxt);
        } 
        // CC: observer 
        if (observer != null) { 
          toAddr = observer.email;
          logger.info("MAIL Observer: " + toAddr + " " + subject + "\n");
          logger.trace(confirmTxt);
          mu.mailMessage(mu.getFromEmail(),toAddr,null,null,subject,confirmTxt);
        } 

      } catch (Exception exc) {
        msg = "Unable to Send confirmation message";
        logger.error("Confirmation Message Failed",exc);
        try {
          if (currentFileBR != null)
            currentFileBR.close();
        } catch (Exception e) {
          logger.error(e);
        }
      }
      
    }
    
    return msg;

  }
  private void writeDDTConfirmation(CPSProposal cps)
  {
     String result = "";
     try {
       String ddtpath = (String)cpsProperties.getProperty("ddt.path");
       String tmppath = (String)cpsProperties.getProperty("cps.tmp.path");
       String webbin=cpsProperties.getProperty("web.bin.directory");
       String pu= cpsProperties.getProperty("cps.pc");
       String responseTime="SLOW";
       if (cps.vprop.ddtProposalDatum != null)
          responseTime = cps.vprop.ddtProposalDatum.response_time;

       // run script from bin area just like we do for the pdf script
       String cmd = webbin + "/prop_ddt.tcsh -U " + pu + " -q " + dataPath + "/.htpc " + " -p " + cps.vprop.proposalNumber  + " -r " + responseTime;
       cmd += " -o " + ddtpath + " -d " + tmppath;
       logger.debug(cmd);

       ArrayList<String> envVarList = setEnvironment();
       String[] env;
       env = new String[envVarList.size()];
       int ii = 0;
       for (ListIterator itr = envVarList.listIterator(); itr.hasNext();) {
         // set the string array entry to the next environment variable in list
         env[ii++] = (String) itr.next();
       }

       // don't wait for results
       Process p = Runtime.getRuntime().exec(cmd, env, null);
 
    } catch (Exception ex) {
       logger.error("prop_ddt.tcsh" , ex);
       result += "Error occurred processing results of DDT confirmation message.\n";
    }
  }


  public double calc_distance(double ra1, double dec1, double ra2, double dec2 ) {
    double rrad;
    double sindec1, sindec2;
    double cosdec1, cosdec2;
    double arcmindiff = 0.0;

    if (ra1 == ra2 && dec1 == dec2) {
      arcmindiff = 0.0;
    } else {

      sindec1 = Math.sin( Math.toRadians(dec1) );
      cosdec1 = Math.cos( Math.toRadians(dec1) );
      sindec2 = Math.sin( Math.toRadians(dec2) );
      cosdec2 = Math.cos( Math.toRadians(dec2) );
  
      rrad = Math.acos( sindec2 * sindec1 +
	  cosdec2 * cosdec1 * Math.cos(Math.toRadians(ra2 - ra1)) );
  
      arcmindiff = radian2arcmin(rrad);
    }
    return arcmindiff;
  }

  public double radian2arcmin(double radian)
  {
   return (radian / Math.PI) * 10800;
  }



  private String printProposal(CPSProposal cps,CPSTargetList tgtList)
  {
    String fname = "";
    try {
      fname = (String)cpsProperties.getProperty("cps.tmp.path");
      fname += "/" + cps.vprop.proposalNumber + ".txt";
      logger.debug("PRINT " + fname);
      File pfile = new File(fname);
      PrintWriter outPW =  new PrintWriter(new FileWriter(pfile));
      outPW.println(cps.WriteProposal());
      outPW.println(tgtList.WriteTargets()); 
      outPW.close();
    } 
    catch (Exception exc) {
      fname="";
      logger.error("Print: " , exc);
    }
    return fname;

  }

  private void writeDDTReceipt(CPSProposal cps,CPSTargetList tgtList)
  {
    String fname = "";
    try {
      fname = (String)cpsProperties.getProperty("ddt.path");
      fname += "/" + cps.vprop.proposalNumber + ".prop";
      logger.debug("PRINT " + fname);
      File pfile = new File(fname);
      PrintWriter outPW =  new PrintWriter(new FileWriter(pfile));
      outPW.println(cps.WriteDDT());
      outPW.println(tgtList.WriteDDTTargets()); 
      outPW.close();
    } 
    catch (Exception exc) {
      fname="";
      logger.error("Print: " , exc);
      try {
        MailUtility mu = new MailUtility(cpsProperties);
        mu.mailErrorMessage("CPS: Unable to write DDT receipt for " + cps.vprop.proposalNumber);
        logger.error(cps.WriteDDT());
        logger.error(tgtList.WriteDDTTargets()); 
      } catch (Exception mexc) {
         logger.error(mexc);
      }
    }

  }
  private  ArrayList<String> setEnvironment()
  {
    String syblib = "";
    String server=cpsProperties.getProperty("cps.pcserv");
    ArrayList<String> envVarList = new ArrayList<String>();
    String envStr = "ASCDS_BIN=" + relPath + "/bin";
    envVarList.add(envStr);
    envStr = "ASCDS_INSTALL=" + relPath ;
    envVarList.add(envStr);
    envStr= "DB_LOCAL_SQLSRV=" + server;
    envVarList.add(envStr);
    envStr= "DB_REMOTE_SQLSRV=" + server;
    envVarList.add(envStr);
    envStr= "DB_PROP_SQLSRV=" + server;
    envVarList.add(envStr);
    envStr= "DB_OCAT_SQLSRV=" + server;
    envVarList.add(envStr);
    syblib = System.getenv("SYBASE");
    envStr = "SYBASE=" + syblib;
    envVarList.add(envStr);
    envStr = "SYBASE_OCS=" + System.getenv("SYBASE_OCS");
    envVarList.add(envStr);
    syblib += "/" + System.getenv("SYBASE_OCS") + "/lib";
    envStr = "LD_LIBRARY_PATH=" + relPath + "/lib:" +relPath + "/otslib:" + relPath + "/ots/lib:" + syblib ;
    envVarList.add(envStr);
    envStr = "LANG=en_US.UTF-8";
    envVarList.add(envStr);
    envStr = "PATH=" + System.getenv("PATH");
    envStr += ":" + relPath + "/bin";
    envVarList.add(envStr);
    envStr = "ASCDS_PROP_LOGS=" + cpsProperties.getProperty("cps.tmp.path");
    envVarList.add(envStr);
    envStr = "ASCDS_VERSION=CPS";
    envVarList.add(envStr); 

    return envVarList;
  }

     
  /**
   * Private variables
   */
    private static final long serialVersionUID = 1;
    private Properties cpsProperties;
    private static Logger logger = Logger.getLogger(ReviewProposal.class);
    private String relPath;
    private String dataPath;


  /**
   * Private methods
   */

  /****************************************************************************/


}

/******************************************************************************/
