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
import java.io.*;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
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

import edu.harvard.cda.jutil.rest.InternalServerException;
import edu.harvard.cda.jutil.rest.InternalServerExceptionData;
import edu.harvard.cda.proposal.restclient.IRestClient;
import edu.harvard.cda.proposal.restclient.RestClient;
import edu.harvard.cda.proposal.xo.ProposalSummary;
import edu.harvard.cda.proposal.xo.PersonAndPersonShort;
import edu.harvard.cda.proposal.xo.InstitutionDatum;
import edu.harvard.cda.proposal.xo.AlternateTargetGroup;
import edu.harvard.cda.proposal.xo.TargetGroupIdAndInterval;


/******************************************************************************/
/**
 */

@SuppressWarnings("unchecked")
public class GetProposals extends HttpServlet 
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
   * Handle a submission from a browser.  operations are provided:
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
    Integer userId;
    int uid=0;
    String str = "";
    String msg = "Invalid session";
    boolean ddtRequest=false;
    String ddtAO=null;

    Properties cpsProperties = CPS.getProperties();
    CPSUtils cpsutil = new CPSUtils();

    // Get the session object.
    HttpSession session = request.getSession( false );
    if (session == null || !request.isRequestedSessionIdValid()) {
      logger.error("session is null" );
      isError=true;
    }
    else {
      logger.trace("session=" + session.getId());

      try {
        userId=(Integer)session.getAttribute("userId");
        if (userId == null || userId.intValue() <= 0) {
          logger.error("Invalid session, no userid for " + session.getId());
          try { 
            session.invalidate();
          } catch (Exception e) {
            logger.error(e);
          }
          session=null;
          isError=true;
        }
        else {
          uid=userId.intValue();
        }
       
        if (session != null) {
          ddtRequest=(boolean)session.getAttribute("ddtRequest");
          ddtAO=(String)session.getAttribute("ddtAO");
          if (ddtAO == null || ddtAO.equals("") )  {
            try {
              IRestClient api = cpsutil.getAPI(cpsProperties,session);
              ddtAO = api.getCurrentDDTCycle();
              logger.info("DDT AO " + ddtAO);
              session.setAttribute("ddtAO",ddtAO);
            } catch (Exception e) {
              logger.error(e);
            }
          }
        }
        String tstr = cpsProperties.getProperty("force.inst.cntry.reload");
        if (tstr != null && tstr.indexOf("true") >= 0) {
          session.getServletContext().removeAttribute("cntryList");
          session.getServletContext().removeAttribute("instList");
        }
      }
      catch (Exception exc) {
        logger.error(exc.getMessage() );
        logger.error("Invalid user ",exc );
        isError=true;
      }
    }
    if (logger.isTraceEnabled()) {
      try {
       //for ( Enumeration parameters = request.getHeaderNames();
       for ( Enumeration parameters = request.getParameterNames();
            parameters.hasMoreElements(); ) {
          String parameterName = (String) parameters.nextElement();
          String parameterValue = request.getHeader( parameterName );
          logger.trace( "HEADER: " + parameterName + " = " +  parameterValue );
        }
      } catch (Exception exc) {
         logger.error(exc);
         logger.debug("Error printing parameters",exc);
      }
    }

    boolean isCfP = CPS.isCfP();
    boolean isGTO = CPS.isGTO();

    String operation = Parameter.get(request,"operation");
    logger.info("operation=" + operation);

    if (!isError && operation.equals("NAV")) {
      // use the real end because this determines how often to check
      Double deadlinedays = CPS.deadlineCfP(false);
      response.setContentType("text/xml");
      PrintWriter out=response.getWriter();
      out.println(CPSConstants.XMLSTR);

      str = "<data>\n";
      str += "<deadlinedays>" + CPS.getDouble(deadlinedays,0) + "</deadlinedays>";
      str += "</data>\n";
      logger.trace(str);
      out.println(str);
      out.close();
    }
    else if (!isError) {
      String currentAO = "";
      if (operation.equalsIgnoreCase("LOAD") ||
          operation.equalsIgnoreCase("FILTER")) {
        
        String filterBy = Parameter.get(request,"filterBy");
        StringBuffer outputStr = new StringBuffer();
        PrintWriter out=response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding(CPSConstants.CHARSET);
        try {
          List<CPSProposalSummary> cpssList = (List<CPSProposalSummary>)session.getAttribute("cpssList");
          if (operation.equalsIgnoreCase("LOAD")) {
            IRestClient api = cpsutil.getAPI(cpsProperties,session);
            cpssList = new ArrayList<>();
            List<ProposalSummary> pss = api.proposalSummaries(uid);
            for (int ii = 0 ; ii < pss.size() ; ii++) {
              //logger.info(ii + ": " + pss.get(ii));
              CPSProposalSummary cpss = new CPSProposalSummary(pss.get(ii));
              cpss.setEditPrivs(isCfP,ddtRequest,ddtAO,isGTO,uid);
              currentAO = cpss.ps.currentAO;
              cpssList.add(cpss);
              Integer pid = cpss.ps.proposalId;
              // used to verify valid proposal for current user
              session.setAttribute(pid.toString(),(Boolean)cpss.isEdit());
            }
            if (pss.size() <=0) {
              currentAO=api.getCurrentAOString();
            }
            logger.debug("Retrieved " + pss.size() + " for " + uid);
            session.setAttribute("currentAO",currentAO);
            session.removeAttribute("cpssList");
            session.setAttribute("cpssList",cpssList);
            filterBy =(String)session.getAttribute("filterBy");
          } else {
            currentAO = (String)session.getAttribute("currentAO");
            session.setAttribute("filterBy",filterBy);
          }
          if (cpssList == null) cpssList = new ArrayList<>();
          logger.info("Processing " + cpssList.size() + " for " + uid + " filter=" + filterBy);
          if (filterBy == null) filterBy="";
          outputStr.append("{\"filterBy\":\"");
          outputStr.append(filterBy);
          outputStr.append("\",\n\"rows\":[\n");
          int displayCnt=0;
          for (int ii = 0 ; ii < cpssList.size() ; ii++) {
            CPSProposalSummary cps=cpssList.get(ii);
            boolean matchesFilter=true;
            // these values are in propfilter.xml
            if (filterBy.equals("")) matchesFilter=true; 
            else {
              if (filterBy.contains("own") && 
                  (cps.ps.submitterId == null ||
		   uid != cps.ps.submitterId.intValue())) 
                 matchesFilter=false;
              if (filterBy.contains("edit") && !cps.isEdit()) matchesFilter=false;
              // if any are true, we use it
              if (filterBy.contains("cycle_")) {
                boolean cycleok = false;;
                if (filterBy.contains("current") && currentAO.equals(cps.ps.ao))
                  cycleok=true;
                if (filterBy.contains("lastyear")) {
                  try {
                    Integer aoint = Integer.valueOf(currentAO);
                    Integer psint = Integer.valueOf(cps.ps.ao);
                    int aodiff = aoint.intValue() - psint.intValue();
                    if (aodiff == 1) cycleok=true;
                  } catch (Exception exc) {
                    logger.error("AO filter",exc);
                  }
                }
                if (filterBy.contains("previousyears") && !currentAO.equals(cps.ps.ao)) 
                   cycleok=true;
                if (!cycleok) matchesFilter=false;
              }
              if (filterBy.contains("status_")) {
                boolean statusok = false;;
                String status = cps.ps.status;
                if (status== null) status = "incomplete";
                else status = status.toLowerCase();
                if (filterBy.contains(status)) statusok = true;
                logger.trace(filterBy + " status=" + status + " matchesFilter" + matchesFilter );
                if (!statusok) matchesFilter=false;
              }
            }
          
            //logger.debug("value " + ii + ": " + cpssList.get(ii));
            //filter!!!
            if (matchesFilter) {
              if (displayCnt != 0) str += ",";
              str += cpssList.get(ii).getGridEntry(uid);
              displayCnt++;
            }
          }
          logger.trace("displayCnt " + displayCnt );
          outputStr.append(str);
          outputStr.append("\n]}\n");
          logger.trace(outputStr.toString());
          out.println(outputStr.toString());
          out.close();
        } catch (InternalServerException exc) {
          logger.error("InternalServer",exc);
          String emsg = exc.getMessage();
          isError = true;
          str ="{\n\"rows\":[\n";
          str +="{\"id\":'error', \"data\":[' ','" + StringEscapeUtils.escapeHtml4(emsg) + "','','','','','','',''" + "] }\n";
          str += "]\n}\n";
          out.println(str);
          out.close();
        } catch (Exception exc) {
          logger.error("GetProposals",exc);
          str ="{\n\"rows\":[\n";
          str +="{\"id\":'error', \"data\":[' ','Unexpected Error','','','','','','',''" + "] }\n";
          str += "]\n}\n";
          isError = true;
          logger.error(str);
          out.println(str);
          out.close();
        }
      } else {
        try {
          IRestClient api = cpsutil.getAPI(cpsProperties,session);
          response.setContentType("text/xml");
          PrintWriter out=response.getWriter();
          out.println(CPSConstants.XMLSTR);
          if (operation.equalsIgnoreCase("SELECTEDPI" ) ||
              operation.equalsIgnoreCase("SELECTEDCOI") ) {
            str = processSelectedPI(api,operation,request,session);
          } else if (operation.equalsIgnoreCase("COIOPTIONS" ) ) {
            String cxconly = Parameter.get(request,"cxc");
            String excludeIds=Parameter.get(request,"exc");
            String mask=Parameter.get(request,"mask");
            String pos=Parameter.get(request,"pos");
            String page=Parameter.get(request,"page");
            if (excludeIds ==null) excludeIds="";
            if (cxconly == null) cxconly = "0";
            logger.debug("MASK: " + mask + "--" + pos + " Skip non-CXC: " + cxconly);
            str = processSelectablePersons(api,session,page,cxconly,excludeIds,mask);
          } else if (operation.equalsIgnoreCase("CNTRYOPTIONS" ) ) {
            str = processCountryOptions(api,session);
          } else if (operation.equalsIgnoreCase("INSTOPTIONS" ) ) {
            String cntry = Parameter.get(request,"cntry");
            if (cntry == null || cntry.length() < 2) cntry = "ALL";
            str = processInstitutions(api,session,cntry);
          } else if (operation.equalsIgnoreCase("ALTOPTIONS" ) ) {
            str = processAltTargetGroup(api,request);
          } else if (operation.equalsIgnoreCase("GROUPOPTIONS" ) ) {
            str = processGroupOptions(api,request,session);
          }
          out.println(str);
          logger.trace(str);
          out.close();
        } catch (Exception exc) {
          logger.error("YIKES",exc);
          str = "<complete>";
          str += "<option value=\"error\"><![CDATA[" + "Unexpected Error" + "]]></option>";
          str += "</complete>";
          logger.error(str);
        }
      }
    }
    else {
      // json  only
      str ="{\nrows:[\n";
      str +="{id:'error', data:[' ','Invalid Session','','','','','','',''" + "] }\n";
      str += "]\n}\n";
     
      response.setContentType("application/json");
      PrintWriter out=response.getWriter();
      logger.error(str);
      out.println(str);
      out.close();
    }
  }

  // -------------------------------------------------------------------
  // Returns form fields for selected PI or CoI
  private String processSelectedPI(IRestClient api,String operation,HttpServletRequest request,HttpSession session)
  {
    String str="";
    try {
      String rstr = Parameter.get(request,"persid");
      Integer persId = 0;
      try {
         persId = Integer.valueOf(rstr);
      } catch (Exception ex) {
         persId=0;
      }
      
      if (persId.intValue() > 0) {
          PersonAndPersonShort ps = api.retrievePerson(persId.intValue());
          if (ps != null) {
            str += "<data>\n";
            str += "<cxc>true</cxc>,";
            str += "<id>" + persId.toString() + "</id>,";
            str += "<emsg></emsg>";
            if (operation.equalsIgnoreCase("SELECTEDPI" )) {
              str += "<first><![CDATA[" + CPS.getString(ps.personShort.first) + "]]></first>,";
              str += "<middle><![CDATA[" + CPS.getString(ps.person.middle) + "]]></middle>,";
              str += "<last><![CDATA[" + CPS.getString(ps.personShort.last) + "]]></last>,";
              str += "<institute><![CDATA[" + CPS.getString(ps.personShort.institution) + "]]></institute>,";
              str += "<country><![CDATA[" + CPS.getString(ps.personShort.country) + "]]></country>,";
            } else {
              str += "<coi_first><![CDATA[" + CPS.getString(ps.personShort.first) + "]]></coi_first>,";
              str += "<coi_last><![CDATA[" + CPS.getString(ps.personShort.last) + "]]></coi_last>,";
              str += "<coi_institute><![CDATA[" + CPS.getString(ps.personShort.institution) + "]]></coi_institute>,";
              str += "<coi_country><![CDATA[" + CPS.getString(ps.personShort.country) + "]]></coi_country>,";
            }
            str += "</data>\n";
          }
      } else {
        throw new Exception("Unable to retrieve person record for " + persId);
      }
    }
    catch (Exception exc) {
      logger.error("SelectedPI: ",exc);
      str = "<data>";
      str += "<last></last>";
      str += "<coi_last></coi_last>";
      str += "<emsg><![CDATA[" + "Unexpected Error, Please contact the HelpDesk" + "]]></emsg>";
      str += "</data>\n";
    }
    //logger.trace(str);
    return str;
  }

  // -------------------------------------------------------------------
  // provide option list of selectable persons
  private String processSelectablePersons(IRestClient api,HttpSession session,String page,String cxconly,String excludeIds,String mask)
  {
    String str = "";
    String rstr;
    String lname;
    try {
      logger.trace("Retrieving selectable persons");
      List<PersonAndPersonShort> pssList = api.allSelectablePersons();
      str="<complete add=\"true\">";
      String[] ids = null;
      if (!excludeIds.equals("")) {
        ids = excludeIds.split(",");
      }
      for (int ii=0;ii<pssList.size();ii++) {
        PersonAndPersonShort ps = pssList.get(ii);
        lname= ps.personShort.last + ", " + ps.personShort.first;
        if (cxconly.equals("1") && (ps.personShort.username == null || ps.personShort.username.length() < 1)) {
           // skip these because we only want users with cxcaccounts
        } else {
          boolean doit=true;
          // now check any we want to exclude
          for (int rr=0;ids != null && rr< ids.length;rr++) {
             Integer  rid = Integer.valueOf(0);
             try {
                rid = Integer.valueOf(ids[rr]);
             } catch (Exception exc) {
                logger.error(exc);
             }
             if (rid == ps.personShort.pers_id) {
                doit=false;
                break;
             }
           }
           if (doit) {
             if (ps.personShort.username == null || 
                 ps.personShort.username.length() < 1) {
               //lname +=  " *";
               // don't display these at all!
               logger.debug("COIOPTIONS no cxcacct : " + ps.personShort.pers_id);
             } else {
               if (mask == null || lname.toLowerCase().indexOf(mask.toLowerCase()) ==0) 
                 str += "<option value=\"" + ps.personShort.pers_id +  "\"><![CDATA[" + CPS.getString(lname) + "]]></option>";
             }
          }
          else {
            logger.debug("COIOPTIONS skipping: " + ps.personShort.pers_id);
          }
        }
      }
      str += "</complete>";
    } catch (Exception exc) {
      logger.error("CoIOptions",exc);
      str = "<complete>";
      str += "<option value=\"error\"><![CDATA[" + "Unexpected Error" + "]]></option>";
      str += "</complete>";
    }
    //logger.trace("COIOPTIONS: " + str);
    return str;
  }

  // -------------------------------------------------------------------
  private String processCountryOptions(IRestClient api,HttpSession session)
  {
    StringBuffer str= new StringBuffer("");
    try {
      List<String> cntryList = (List<String>)session.getServletContext().getAttribute("cntryList");
      if (cntryList == null) cntryList = getCountryList(api,session);
      str.append("<complete>");
      for (int ii=0;ii<cntryList.size();ii++) {
        String ps = cntryList.get(ii);
        str .append("<option value=\"" + CPS.getXmlString(ps) +  "\"><![CDATA[" + CPS.getString(ps) + "]]></option>");
       }
       str.append("</complete>");
    } catch (Exception exc) {
      logger.error("CountryOptions",exc);
      str =  new StringBuffer("<complete>");
      str.append("<option value=\"error\"><![CDATA[" + "Unexpected Error" + "]]></option>");
      str.append("</complete>");
    }
    return str.toString();
  }
         
  private String processInstitutions(IRestClient api,HttpSession session,String cntry)
  {
    StringBuffer str = new StringBuffer("");
    try {
      List<InstitutionDatum> instList = (List<InstitutionDatum>)session.getServletContext().getAttribute("instList");
      if (instList == null) instList= getInstList(api,session);
      
      str.append("<complete>");
      logger.trace("Institution size is " + instList.size() + " filter=" + cntry + "___");
      for (int ii=0;ii<instList.size();ii++) {
        InstitutionDatum ps = instList.get(ii);
        if (cntry.equals("ALL") ||  ps.country.compareToIgnoreCase(cntry)==0 ) {
          str.append("<option value=\"" + CPS.getXmlString(ps.institution) +  "\"><![CDATA[" + CPS.getString(ps.institution) + "]]></option>\n");
        }
      }
      str.append("</complete>");
    } catch (Exception exc) {
      logger.error("Institutions",exc);
      str = new StringBuffer("<complete>");
      str.append("<option value=\"error\"><![CDATA[" + "Unexpected Error" + "]]></option>");
      str.append("</complete>");
    }
    return str.toString();
  }

  // -------------------------------------------------------------------
  private String processAltTargetGroup(IRestClient api,HttpServletRequest request)
  {
    StringBuffer str=new StringBuffer("");
    try {
      str.append("<complete>");
      Integer pid = Parameter.getInteger(request,"pid");

      List<AlternateTargetGroup> atgList = api.retrieveAlternateTargetGroups(pid.intValue());
      for (int ii=0;ii<atgList.size();ii++) {
        AlternateTargetGroup ps = atgList.get(ii);
        if (ps.group_name != null && ps.group_name.length() >1)  {
          String tstr = ps.group_name + CPSConstants.DELIM + ps.requested_count;
          str.append("<option value=\"" + tstr +  "\"><![CDATA[" + CPS.getString(ps.group_name) + "]]></option>");
        }
      }
      str.append("</complete>");
    } catch (Exception exc) {
      logger.error("AltTgtGroup",exc);
      str = new StringBuffer("<complete>");
      str.append("<option value=\"error\"><![CDATA[" + "Unexpected Error" + "]]></option>");
      str.append("</complete>");
    }
    return  str.toString();
  }

  private String processGroupOptions(IRestClient api,HttpServletRequest request,HttpSession session) {
    StringBuffer str= new StringBuffer("");
    try {   
      Integer pid = Parameter.getInteger(request,"pid");
      str.append("<complete>");
 
      List<TargetGroupIdAndInterval> groupList = api.retrieveDistinctTargetGroupIdAndIntervals(pid.intValue());
      for (int ii=0;ii<groupList.size();ii++) {
        TargetGroupIdAndInterval ps = groupList.get(ii);
        if (ps.group_id != null && ps.group_id.length() >1)  {
          String tstr= ps.group_id + CPSConstants.DELIM + ps.group_interval;
          str.append( "<option value=\"" + tstr +  "\"><![CDATA[" + CPS.getString(ps.group_id) + "]]></option>");
        }
      }
      str.append("</complete>");
    } catch (Exception exc) {
      logger.error(exc.getMessage());
      logger.debug("GroupOptions",exc);
      str = new StringBuffer("<complete>");
      str.append("<option value=\"error\"><![CDATA[" + "Unexpected Error" + "]]></option>");
      str.append("</complete>");
    }
    return str.toString();
  }

  private synchronized List<InstitutionDatum> getInstList(IRestClient api,HttpSession session) {
    List<InstitutionDatum> instList = null;
    try {
      instList = (List<InstitutionDatum>)session.getServletContext().getAttribute("instList");
      if (instList == null) {
        instList = api.getInstitutions();
        session.getServletContext().setAttribute("instList",instList);
      }
    } catch (Exception exc) {
      logger.error(exc);
    }
    return instList;
  }

  private synchronized List<String> getCountryList(IRestClient api,HttpSession session) {
      List<String> cntryList = null;
      try {
        cntryList = (List<String>)session.getServletContext().getAttribute("cntryList");
        if (cntryList == null) {
          cntryList = api.getCountries();
          session.getServletContext().setAttribute("cntryList",cntryList);
        }
    } catch (Exception exc) {
      logger.error(exc);
    }
    return cntryList;
  }


  /**
   * Private variables
   */
   private static final long serialVersionUID = 1;
   private static Logger logger = Logger.getLogger(GetProposals.class);



  /**
   * Private methods
   */

  /****************************************************************************/


}

/******************************************************************************/
