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
import edu.harvard.cda.coords.CoordSystem;
import edu.harvard.cda.coords.Coordinate;

import edu.harvard.cda.jutil.rest.InternalServerException;
import edu.harvard.asc.cps.xo.*;
import edu.harvard.cda.proposal.restclient.IRestClient;
import edu.harvard.cda.proposal.restclient.RestClient;
import edu.harvard.cda.proposal.xo.*;

import org.apache.log4j.Logger;
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
import java.util.*;
import java.lang.Thread;
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

public class SaveTarget extends HttpServlet 
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
    CPSTarget cps;
    HttpSession session;
    request.setCharacterEncoding(CPSConstants.CHARSET);
    Integer currentAO = 0;   

    // Get the session object.
    session = request.getSession(false);
    if (session == null) {
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
/*
Enumeration keys = session.getAttributeNames();
while (keys.hasMoreElements())
{
  String key = (String)keys.nextElement();
  logger.debug(key + ": " + session.getValue(key) );
}
*/


    String mypage = "";

    mypage= Parameter.get(request,"page");
    String tid = Parameter.get(request,"tid");
    Integer itid = Integer.valueOf(0);
    try {
       itid = Integer.valueOf(tid);
    }
    catch (Exception x) {
       itid=0;
    }
      
    String operation = Parameter.get(request, "operation" );
   
    if (mypage == null) mypage = "";
    if ( operation == null ) operation = "";
    String msg = operation;

   logger.info(operation + ": " + mypage + " for " + tid);

    try {
      currentAO= new Integer((String) session.getAttribute("currentAO"));
    } catch (Exception e) {
      logger.error ("Can't determine currentAO");
    }
    cps= (CPSTarget) session.getAttribute("curtgt");
    if (cps == null) {
      msg = "ERROR: Unable to retrieve current target. ";
      logger.error(msg + tid + "tgt" );
    }
    else if (cps.tgt.targid != itid.intValue()) {
      msg = "ERROR: Unable to retrieve target ";
      logger.error(msg + tid + "tgt  session=" + cps.tgt.targid  );
    } else {
       if (operation.equals("SAVE")) {
         msg = operation + " " + mypage;
         if (mypage.equals("POINTING")) {
           msg = processPointing(request,session,cps);
         }
         else if (mypage.equals("TIME")) {
           msg = processTimes(request,session,cps,currentAO);
         }
         else if (mypage.equals("INSTRUMENT")) {
           msg = processInstrument(request,session,cps);
         }
         else if (mypage.equals("ACISREQ")) {
           msg = processAcisRequired(request,session,cps);
         }
         else if (mypage.equals("ACISOPT")) {
           msg = processAcisOptional(request,session,cps);
         }
         else if (mypage.equals("CONSTRAINT")) {
           msg = processConstraints(request,session,cps);
         }
         else if (mypage.equals("TOO")) {
           msg = processTOO(request,session,cps);
         }
         else if (mypage.equals("REMARKS")) {
           msg = processRemarks(request,session,cps);
         }
         else if (mypage.equals("DDT")) {
           msg = processDDT(request,session,cps);
         }
         else {
           msg = "Unexpected " + mypage;
         }
       }
       else {
           msg = "Unexpected operation " + operation;
 
       }
       if (msg.indexOf(CPSConstants.SAVE_OK) >= 0) {
          updateLastSaved(session,cps.tgt.proposal_id);
       }
       logger.info(msg);
     }
     //try {
     //Thread.sleep(3000);
     //} catch (Exception e) {};


     response.setContentType("text/plain");
     PrintWriter out=response.getWriter();
     if (msg.indexOf(CPSConstants.SAVE_OK) < 0) {
        out.println(CPSConstants.SAVE_FAILED);
     } else {
        try {
          String tstr = "";
          Boolean tooDetails= cps.shouldHaveTOODetails();
          if (tooDetails) tstr ="too";
          if (cps.tgt.instrument_name != null)
             tstr += cps.tgt.instrument_name;

          Integer mytid = Integer.valueOf(cps.tgt.targid);
          session.setAttribute(mytid.toString() + "detector",tstr);
        } catch (Exception e) {
           logger.debug("attribute: ", e);
        }
     }

     out.println(msg);
     out.close();

  }

  private String processPointing(HttpServletRequest request,HttpSession session,CPSTarget cps)
  {
    String msg = "";
    String ra = "",dec = "";

    try {
      Boolean tgtpos = Parameter.getBoolean(request,"targ_position_flag");
      if (tgtpos) {
        ra = Parameter.get(request,"ra");
        dec = Parameter.get(request,"dec");
        logger.trace("ra=" + ra + " dec=" + dec);
        Coordinate coord = new Coordinate(ra,dec,"J2000");
        cps.tgt.ra = coord.getLon();
        cps.tgt.dec = coord.getLat();
      } else {
        cps.tgt.ra = null;
        cps.tgt.dec = null;
      }
    } catch (Exception exc) {
      msg = "Invalid RA,Dec entered.";
      msg += " " + exc.getMessage();
      logger.error("ra=" + ra + " dec=" + dec);
      logger.error(exc);
    }
    logger.trace("ra=" + cps.tgt.ra + " dec=" + cps.tgt.dec);

    if (msg.equals("")) {
      try {
        cps.tgt.targname = Parameter.get(request,"targname");
        cps.tgt.ss_object = Parameter.get(request,"ss_object");
      
        cps.tgt.targ_position_flag = Parameter.getBoolean(request,"targ_position_flag",true);
        cps.tgt.photometry_flag = Parameter.getBoolean(request,"photometry_flag",false);
        cps.tgt.vmagnitude = Parameter.getDouble(request,"vmagnitude");
        cps.tgt.y_det_offset = Parameter.getDouble(request,"y_det_offset");
        cps.tgt.z_det_offset = Parameter.getDouble(request,"z_det_offset");
        cps.tgt.sim_trans_offset = Parameter.getDouble(request,"sim_trans_offset");
        cps.tgt.raster_scan = Parameter.getBoolean(request,"raster_scan",false);
        cps.tgt.grid_name = Parameter.get(request,"grid_name");
        cps.tgt.grid_num_pointings = Parameter.getInteger(request,"num_pointings");
        cps.tgt.grid_max_radius = Parameter.getDouble(request,"max_radius");
        cps.tgt.pointing_constraint = Parameter.getBoolean(request,"pointing_constraint",false);
      } catch (Exception exc) {
        msg= "Error occurred validating parameters";
        logger.error("Yikes",exc);
      }
    }
    if (msg.equals("")) {
      try {
 
        TargetUpdatePointingDatum tc = new TargetUpdatePointingDatum(
            cps.tgt.targname,cps.tgt.ss_object,cps.tgt.targ_position_flag.booleanValue(),
            cps.tgt.ra,cps.tgt.dec,cps.tgt.photometry_flag,cps.tgt.vmagnitude,
            cps.tgt.y_det_offset,cps.tgt.z_det_offset,cps.tgt.sim_trans_offset,
            cps.tgt.raster_scan.booleanValue(), cps.tgt.grid_name,
            cps.tgt.grid_num_pointings,cps.tgt.grid_max_radius,cps.tgt.pointing_constraint);
        logger.debug("POINTING: " + tc.toString());
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        api.targetUpdatePointing(cps.tgt.targid,tc);
        msg=CPSConstants.SAVE_OK;
        String eclipticStr = "";
        if (cps.tgt.ra != null && cps.tgt.dec != null) {
          Double highEC;
          try {
            highEC= new Double(cpsProperties.getProperty("HIGH_ECLIPTIC"));
          } catch (Exception e) {
            logger.error("Unable to get HIGH_ECLIPTIC property");
            highEC=50.0;  // default
          }
          Double lowEC= new Double(highEC*-1);
         
          Coordinate ecliptic = cps.getEcliptic();
          if  (ecliptic != null) {
            if ((ecliptic.getLat() > highEC.doubleValue()) ||
                (ecliptic.getLat() < lowEC.doubleValue()) ) {
              eclipticStr = "\n<span class='errmsg'>Warning:</span>Target is at high ecliptic latitude. Observation time awarded for these targets is limited.";
            }
       
            logger.debug("Ecliptic (" + cps.tgt.ra + "/" + cps.tgt.dec + " : " + ecliptic.getLat() );
          } else  {
            eclipticStr += "Unable to determine ecliptic latitude.";
            logger.debug("Ecliptic (" + cps.tgt.ra + "/" + cps.tgt.dec + " : NULL" );
            logger.debug("Ecliptic is null");
          }
        }
        msg += eclipticStr;
      } catch (InternalServerException iexc) {
        msg= "Error occurred saving Target Pointing information";
        msg += "\n" + iexc.getMessage();
        logger.error("Pointing",iexc);

      } catch (Exception exc) {
        msg= "Error occurred saving Target Pointing information";
        logger.error("Pointing",exc);
      }
    }


    return msg;

  }
  private String processTimes(HttpServletRequest request,HttpSession session,CPSTarget cps,Integer currentAO)
  {
    String msg="";
    String gridvalues="";
    YesNoPreferred uninterrupt = YesNoPreferred.NO;
    YesNoPreferred monitor_flag = YesNoPreferred.NO;
    String mflag = "";

    try {
      gridvalues = Parameter.get(request,"gridvalues");
      cps.tgt.prop_exposure_time = Parameter.getDouble(request,"obs_time");
      cps.tgt.est_time_cycle_n1 = Parameter.getDouble(request,"obs_time_1");
      cps.tgt.est_time_cycle_n2 = Parameter.getDouble(request,"obs_time_2");

      uninterrupt = Parameter.getYNP(request,"uninterrupt");
      cps.tgt.trigger_target = Parameter.getBoolean(request,"trigger_target");
       
      // the gui also uses F to distinguish followups
      mflag = Parameter.get(request,"tgtmon");
      monitor_flag = Parameter.getYNP(request,"tgtmon");
      cps.tgt.split_interval = Parameter.getDouble(request,"split_interval");


    } catch (Exception exc) {
      msg= "Error occurred validating parameters";
      logger.error(exc);
    }
    if (msg.equals("")) {
      try  {
        TargetUpdateTimeDatum tc = new TargetUpdateTimeDatum(cps.tgt.prop_exposure_time,
	   cps.tgt.est_time_cycle_n1,cps.tgt.est_time_cycle_n2,
	   uninterrupt,monitor_flag,
	   cps.tgt.trigger_target,
     cps.tgt.split_interval); 
        logger.debug("TIMING: " + tc.toString());
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        api.targetUpdateTime(cps.tgt.targid,tc);
      } catch (Exception exc) {
        msg= "Error occurred saving Target Time information";
        logger.error(exc);
      }
    }
    if (msg.equals("")) {
      try {
        String[] obsarr = null;  
        if (gridvalues.indexOf("No obs") < 0) 
          obsarr= gridvalues.split("\\["); 
        List<ObservationBase> obsList=new ArrayList<>();;
        double time0= 0;
        double time1= 0;
        double time2= 0;
        try {
          time0 = cps.tgt.prop_exposure_time;
          if (cps.tgt.est_time_cycle_n1 != null)  
            time1= cps.tgt.est_time_cycle_n1;
          if (cps.tgt.est_time_cycle_n2 != null)  
            time2= cps.tgt.est_time_cycle_n2;
          time0= time0-time1-time2;
        } catch (Exception exc) {
          time0=0;
          time1=0;
          time2=0;
          logger.error("Unable to determine current cycle time");
        }
        logger.debug("Multicycle Times for " + cps.tgt.targid + ": " + time0 + "--" + time1 + "--"+time2);

        for (int ii=1;obsarr != null && ii<obsarr.length; ii++) {
          logger.debug("OBSARR " + ii + "=" + obsarr[ii]);
          ObservationBase obs = new ObservationBase();
          //obs.targid = cps.tgt.targid;
          String[] marr = obsarr[ii].split(","); 
          Integer ival = Integer.valueOf(0);
          Double  dval = Double.valueOf(0);
          for (int idx=2;idx<marr.length && idx < 8;idx++) {
            if (!marr[idx].equals("")) {
              // logger.debug("OBS " + idx + ":" +marr[idx] + "---");
              if (idx == 2 || idx==6)  {
                if (marr[idx].indexOf("N/A") >=0)
                  ival=null;
                else if (marr[idx].indexOf("Initial") >=0)
                  ival = Integer.valueOf(0);
                else
                  ival =  Integer.valueOf(marr[idx].trim());
              } else {
                if (marr[idx].indexOf("N/A") >=0) {
                   if (idx==4) dval = Double.valueOf(1);
                   else if (idx==5) dval = Double.valueOf(365);
                   else dval=null;
                } else 
                  dval =  Double.valueOf(marr[idx].trim());
              }

              switch (idx) {
                case 2:
                  obs.ordr=ival.intValue();
                  break;
                case 3:
                  obs.obs_time = dval.doubleValue();
                  if (currentAO > 0) {
                    Integer useAO = currentAO;
                    if (time0 > 0) {
                       time0 -= dval.doubleValue();
                    } else if (time1 > 0) {
		       useAO=currentAO+1;
                       time1 -= dval.doubleValue();
                    }  else if (time2 > 0) {
		       useAO=currentAO+2;
                       time2 -= dval.doubleValue();
                    }
                    logger.debug("useAO " + cps.tgt.targid + ": " + +useAO + "--" + time0 + "--" + time1 + "--"+time2);
		    obs.charge_ao_str=useAO.toString();
                  }
                  break;
                case 4:
                  obs.pre_min_lead = dval.doubleValue();
                  break;
                case 5:
                  obs.pre_max_lead = dval.doubleValue();
                  break;
                case 6:
                  obs.targ_num=ival;
                  break;
                case 7:
                  obs.split_interval = dval.doubleValue();
                  break;
                default:
                  break;
              }
            }
          }
          logger.debug("OBS  " + cps.tgt.targid + ": "  + obs.toString());
          obsList.add(obs);
        }
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        if (obsList.size() <= 0 || 
	   (monitor_flag == YesNoPreferred.NO && !mflag.equals("F")))
           api.observationDelete(cps.tgt.targid);
        else
           api.observationDeleteExistingAndInsert(cps.tgt.targid,obsList);
        msg=CPSConstants.SAVE_OK;
      } catch (InternalServerException iexc) {
        msg= "Error occurred saving monitor/followup data";
        msg += "\n" + iexc.getMessage();
        logger.error(iexc);
      } catch (Exception exc) {
         logger.error("oops",exc);
         msg = "Error occurred saving monitor/followup data.";
      }
    }
    return msg;
  }

  private String processInstrument(HttpServletRequest request,HttpSession session,CPSTarget cps)
  {
    String msg="";

    try {
      cps.tgt.instrument_name = Parameter.get(request,"detector");
      cps.tgt.grating_name = Parameter.get(request,"grating");
      cps.tgt.timing_mode = Parameter.getBoolean(request,"hrcTimingMode");
      cps.tgt.est_cnt_rate = Parameter.getDouble(request,"est_cnt_rate");
      cps.tgt.forder_cnt_rate = Parameter.getDouble(request,"forder_cnt_rate");
      cps.tgt.total_fld_cnt_rate = Parameter.getDouble(request,"total_fld_cnt_rate");
      cps.tgt.extended_src = Parameter.getBoolean(request,"extended_src",false);

    } catch (Exception exc) {
      msg= "Error occurred validating parameters";
      logger.error("params" ,exc);
    }
    if (msg.equals("")) {
      try  {
        TargetUpdateInstrumentDatum tc = new TargetUpdateInstrumentDatum(
	   cps.tgt.instrument_name,
	   cps.tgt.grating_name,
	   cps.tgt.timing_mode,
	   cps.tgt.est_cnt_rate,
	   cps.tgt.forder_cnt_rate,
	   cps.tgt.total_fld_cnt_rate,
           cps.tgt.extended_src.booleanValue());
        logger.debug("Instrument: " + tc.toString());
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        api.targetUpdateInstrument(cps.tgt.targid,tc);
        msg=CPSConstants.SAVE_OK;

      } catch (InternalServerException iexc) {
        msg= "Error occurred saving Target Instrument information";
        msg += "\n" + iexc.getMessage();
        logger.error(iexc);
      } catch (Exception exc) {
        msg= "Error occurred saving Target Instrument information";
        logger.error("instr",exc);
      }
    }
    return msg;

  }

  private String processAcisRequired(HttpServletRequest request,HttpSession session,CPSTarget cps)
  {
    String msg="";

    try {
      cps.tgt.exp_mode = Parameter.get(request,"exp_mode");
      cps.tgt.bep_pack = Parameter.get(request,"bep_pack");
      cps.tgt.ccdi0_on = Parameter.get(request,"ccdi0_on");
      cps.tgt.ccdi1_on = Parameter.get(request,"ccdi1_on");
      cps.tgt.ccdi2_on = Parameter.get(request,"ccdi2_on");
      cps.tgt.ccdi3_on = Parameter.get(request,"ccdi3_on");
      cps.tgt.ccds0_on = Parameter.get(request,"ccds0_on");
      cps.tgt.ccds1_on = Parameter.get(request,"ccds1_on");
      cps.tgt.ccds2_on = Parameter.get(request,"ccds2_on");
      cps.tgt.ccds3_on = Parameter.get(request,"ccds3_on");
      cps.tgt.ccds4_on = Parameter.get(request,"ccds4_on");
      cps.tgt.ccds5_on = Parameter.get(request,"ccds5_on");

      cps.tgt.chip_confirm = Parameter.getBoolean(request,"chipsel");
      cps.tgt.multiple_spectral_lines = Parameter.getBoolean(request,"multiple_spectral_lines");
      cps.tgt.spectra_max_count = Parameter.getDouble(request,"spectra_max_count");

    } catch (Exception exc) {
      msg= "Error occurred validating parameters";
      logger.error("params",exc);
    }
    if (msg.equals("")) {
      try  {
        TargetUpdateACISReqDatum tc = new TargetUpdateACISReqDatum(
          cps.tgt.exp_mode,cps.tgt.bep_pack,
          cps.tgt.ccdi0_on, cps.tgt.ccdi1_on, 
          cps.tgt.ccdi2_on, cps.tgt.ccdi3_on,
          cps.tgt.ccds0_on, cps.tgt.ccds1_on,
          cps.tgt.ccds2_on, cps.tgt.ccds3_on,
          cps.tgt.ccds4_on, cps.tgt.ccds5_on,
          cps.tgt.chip_confirm,
          cps.tgt.multiple_spectral_lines,
          cps.tgt.spectra_max_count);
        logger.debug("ACISReq: " + tc.toString());
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        api.targetUpdateACISReq(cps.tgt.targid,tc);
        msg=CPSConstants.SAVE_OK;
      } catch (InternalServerException iexc) {
        msg= "Error occurred saving ACIS Required parameters";
        msg += "\n" + iexc.getMessage();
        logger.error(iexc);
      } catch (Exception exc) {
        msg= "Error occurred saving ACIS Required parameters";
        logger.error("ACISREQ",exc);
      }
    }
    return msg;

  }

  private String processAcisOptional(HttpServletRequest request,HttpSession session,CPSTarget cps)
  {
    String msg="";
    String gridvalues="";

    try {
      gridvalues = Parameter.get(request,"gridvalues");
      cps.tgt.most_efficient = Parameter.getBoolean(request,"most_efficient");
      cps.tgt.frame_time = Parameter.getDouble(request,"frame_time");

      cps.tgt.subarray = Parameter.get(request,"subarray");
      cps.tgt.subarray_start_row = Parameter.getInteger(request,"subarray_start");
      cps.tgt.subarray_row_count = Parameter.getInteger(request,"subarray_rows");

      cps.tgt.duty_cycle = Parameter.getBoolean(request,"duty_cycle");
      cps.tgt.secondary_exp_count = Parameter.getInteger(request,"secondary_exp_count");
      cps.tgt.primary_exp_time = Parameter.getDouble(request,"primary_exposure_time");

      cps.tgt.eventfilter = Parameter.getBoolean(request,"eventfilter");
      cps.tgt.eventfilter_lower = Parameter.getDouble(request,"eventfilter_lower");
      cps.tgt.eventfilter_range = Parameter.getDouble(request,"eventfilter_range");

      cps.tgt.spwindow = Parameter.getBoolean(request,"spwindow");

    } catch (Exception exc) {
      msg= "Error occurred validating parameters";
      logger.error("params",exc);
    }

    if (msg.equals("")) {
      try  {
        TargetUpdateACISOptDatum tc = new TargetUpdateACISOptDatum(
          cps.tgt.most_efficient, cps.tgt.frame_time,
          cps.tgt.subarray,
          cps.tgt.subarray_start_row, cps.tgt.subarray_row_count,
          cps.tgt.duty_cycle ,
          cps.tgt.secondary_exp_count , cps.tgt.primary_exp_time ,
          cps.tgt.eventfilter, 
          cps.tgt.eventfilter_lower , cps.tgt.eventfilter_range ,
          cps.tgt.spwindow );

        logger.debug("ACISOpt: " + tc.toString());
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        api.targetUpdateACISOpt(cps.tgt.targid,tc);
      } catch (Exception exc) {
        msg= "Error occurred saving Target Acis Optional information";
        logger.error("aciswin",exc);
      }
    }
    if (msg.equals("")) {
      try {
      
        String[] obsarr = null;  
        if (gridvalues.indexOf("No spa") < 0) 
          obsarr= gridvalues.split("\\["); 
        Set<TargetACISSpatialWindowBase> aciswinList = new HashSet<TargetACISSpatialWindowBase>();
        HashMap<String,Integer> chipCnt = new HashMap<String,Integer>();
        for (int ii=1;obsarr != null && ii<obsarr.length; ii++) {
          //logger.debug("ACISWIN " + ii + "=" + obsarr[ii]);
          TargetACISSpatialWindowBase aciswin = new TargetACISSpatialWindowBase();
          aciswin.ordr = 1;
          String[] marr = obsarr[ii].split(","); 
          Integer ival = Integer.valueOf(0);
          Double  dval = Double.valueOf(0);
          for (int idx=2;idx<marr.length;idx++) {
            marr[idx] = marr[idx].trim();
            if (!marr[idx].equals("")) {
              logger.debug("OBS " + idx + ":" +marr[idx] + "---");
              if (idx == 2 ) {
                 aciswin.chip=marr[idx];
                 Integer cnt = (Integer)chipCnt.get(marr[idx]);
                 if (cnt == null || cnt < 0) cnt = 0;
                 cnt += 1;
                 chipCnt.put(marr[idx],cnt);
                 aciswin.ordr= cnt;
                 if (aciswin.ordr > 6) 
                    msg+= aciswin.chip + ": Maximum of 6 windows allowed per chip.";
              }
              else if (idx < 8)
                  ival =  Integer.valueOf(marr[idx]);
              else if (idx < 10)
                  dval =  Double.valueOf(marr[idx]);
              switch (idx) {
                case 3:
                  aciswin.sample = ival;
                  break;
                case 4:
                  aciswin.start_column = ival;
                  break;
                case 5:
                  aciswin.width = ival;
                  break;
                case 6:
                  aciswin.start_row = ival;
                  break;
                case 7:
                  aciswin.height = ival;
                  break;
                case 8:
                  aciswin.lower_threshold = dval;
                  break;
                case 9:
                  aciswin.pha_range = dval;
                  break;
                default:
                  break;
              }
        
              logger.debug("ACISWIN: " + aciswin.toString());
              aciswinList.add(aciswin);
            }
          }
        }
        if (msg.equals("")) {
          CPSUtils cpsutil = new CPSUtils();
          IRestClient api = cpsutil.getAPI(cpsProperties,session);
          if (aciswinList.size() <= 0 || cps.tgt.spwindow.booleanValue()==false) 
            api.aciswinDelete(cps.tgt.targid);
          else
            api.aciswinDeleteExistingAndInsert(cps.tgt.targid,aciswinList);
          msg=CPSConstants.SAVE_OK;
        }
      } catch (InternalServerException iexc) {
        msg= "Error occurred saving ACIS Spatial Windows";
        msg += "\n" + iexc.getMessage();
        logger.error(iexc);

      } catch (Exception exc) {
        logger.error("oops",exc);
        msg= "Error occurred saving ACIS Spatial Windows";
      }
    }
    return msg;

  }

  private String processConstraints(HttpServletRequest request,HttpSession session,CPSTarget cps)
  {
    String msg="";
    YesNoPreferred coord_flag = YesNoPreferred.NO;
    YesNoPreferred group_flag = YesNoPreferred.NO;
    YesNoPreferred phase_flag = YesNoPreferred.NO;
    String winvalues="";
    String rollvalues="";

    try {
      winvalues= Parameter.get(request,"winvalues");
      rollvalues= Parameter.get(request,"rollvalues");
      phase_flag = Parameter.getYNP(request,"phase_constraint_flag");
      cps.tgt.phase_epoch = Parameter.getDouble(request,"phase_epoch");
      cps.tgt.phase_period = Parameter.getDouble(request,"phase_period");
      cps.tgt.phase_start = Parameter.getDouble(request,"phase_start");
      cps.tgt.phase_end = Parameter.getDouble(request,"phase_end");
      cps.tgt.phase_start_margin = Parameter.getDouble(request,"phase_start_margin");
      cps.tgt.phase_end_margin = Parameter.getDouble(request,"phase_end_margin");
      cps.tgt.phase_unique = Parameter.getYN(request,"phase_unique");

      group_flag = Parameter.getYNP(request,"group_constraint");
      String groupid=Parameter.get(request,"group_id");
      if (groupid != null) { 
        String tstr[] = (Parameter.get(request,"group_id")).split(CPSConstants.DELIM);
        cps.tgt.group_id = tstr[0];
      } else {
        cps.tgt.group_id = null;
      }
      cps.tgt.group_interval = Parameter.getDouble(request,"group_interval");

      coord_flag = Parameter.getYNP(request,"coord_constraint");
      cps.tgt.multitelescope_interval = Parameter.getDouble(request,"coord_interval");
      cps.tgt.observatories = Parameter.get(request,"coord_obs");

    } catch (Exception exc) {
      msg= "Error occurred validating parameters";
      logger.error("params",exc);
    }
    if (msg.equals("") ) {
      try {
        String[] obsarr = winvalues.split("\\["); 
        Set<TimeRequestBaseMandatory> timereqList=new HashSet<>();
        for (int ii=1;ii<obsarr.length; ii++) {
          logger.debug("TIMEREQ " + ii + "=" + obsarr[ii]);
          String[] marr = obsarr[ii].split(","); 
          if (marr.length<3 || marr[2].indexOf('N') >= 0) 
             break;
          TimeRequestBase timereq = new TimeRequestBase();
          timereq.ordr=ii;
          for (int idx=2;idx<marr.length;idx++) {
            marr[idx] = marr[idx].trim();
            if (!marr[idx].equals("")) {
              //logger.debug("TIMEREQ " + idx + ":" +marr[idx] + "---");
              switch (idx) {
                case 2:
                  timereq.time_constraint = CPS.getYNP(marr[idx]);
                  break;
                case 3:
                  timereq.tstart = CPS.convertDate(marr[idx]);
                  break;
                case 4:
                  timereq.tstop = CPS.convertDate(marr[idx]);
                  break;
                default:
                  break;
              }
            }
          }
          if (timereq.tstop < timereq.tstart) {
            msg += "Window Constraint: Window Stop must be greater than Window Start   : " + marr[4] + "\n";
          }
          logger.debug("TIMEREQ: " + timereq.toString());
          TimeRequestBaseMandatory tr = new TimeRequestBaseMandatory(timereq.ordr,timereq.time_constraint,timereq.tstart,timereq.tstop);
          timereqList.add(tr);
        }
        if (msg == "") {
          CPSUtils cpsutil = new CPSUtils();
          IRestClient api = cpsutil.getAPI(cpsProperties,session);
          if (timereqList.size() <= 0 ) 
            api.timereqDelete(cps.tgt.targid);
          else
            api.timereqDeleteExistingAndInsert(cps.tgt.targid,timereqList);
        }
        
      } catch (Exception exc) {
         logger.error("oops",exc);
         msg = "Error occurred saving Window Constraints.";
      }
    }
    if (msg.equals("") ) {
      try {
        String[] obsarr =null ;  
        if (rollvalues.indexOf("No Roll") < 0) 
          obsarr= rollvalues.split("\\["); 
        Set<RollRequestBase> rollreqList=new HashSet<>();
        for (int ii=1;obsarr != null && ii<obsarr.length; ii++) {
          logger.debug("ROLLREQ " + ii + "=" + obsarr[ii]);
          RollRequestBase rollreq = new RollRequestBase();
          rollreq.ordr=ii;
          rollreq.roll_180=Boolean.valueOf(false);
          String[] marr = obsarr[ii].split(","); 
          if (marr.length<3 || marr[2].indexOf('N') >= 0) 
             break;
          for (int idx=2;idx<marr.length;idx++) {
            marr[idx] = marr[idx].trim();
            if (!marr[idx].equals("")) {
              logger.debug("ROLLREQ " + idx + ":" +marr[idx] + "---");
              switch (idx) {
                case 2:
                  rollreq.roll_constraint = CPS.getYNP(marr[idx]);
                  break;
                case 3:
                  rollreq.roll = Double.valueOf(marr[idx]);
                  break;
                case 4:
                  rollreq.roll_tolerance = Double.valueOf(marr[idx]);
                  break;
                case 5:
                  if (marr[idx].equals("Y") )
                    rollreq.roll_180 = true;
                  break;
                default:
                  break;
              }
            }
          }
          logger.debug("ROLLREQ: " + rollreq.toString());
          rollreqList.add(rollreq);
        }
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        if (rollreqList.size() <= 0 ) 
          api.rollreqDelete(cps.tgt.targid);
        else
          api.rollreqDeleteExistingAndInsert(cps.tgt.targid,rollreqList);
      } catch (InternalServerException iexc) {
        msg = "Error occurred saving Roll Constraints.";
        msg += "\n" + iexc.getMessage();
        logger.error(iexc);
      } catch (Exception exc) {
         logger.error("oops",exc);
         msg = "Error occurred saving Roll Constraints.";
      }
    }
    if (msg.equals("")) {
      try {
        TargetUpdateConstraintsDatum tc = new TargetUpdateConstraintsDatum(phase_flag, cps.tgt.phase_period,
            cps.tgt.phase_epoch, cps.tgt.phase_start, cps.tgt.phase_end, cps.tgt.phase_start_margin,
            cps.tgt.phase_end_margin, cps.tgt.phase_unique,  coord_flag, cps.tgt.observatories, cps.tgt.multitelescope_interval, group_flag,
            cps.tgt.group_id, cps.tgt.group_interval);
        // logger.debug("Constraints: " + tc.toString());
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties, session);
        api.targetUpdateConstraints(cps.tgt.targid, tc);

        msg = CPSConstants.SAVE_OK;
      } catch (Exception exc) {
        msg = "Error occurred saving Target Constraints information";
        logger.error("constr", exc);
      }
    }
    return msg;
  }
 
  private String processRemarks(HttpServletRequest request,HttpSession session,CPSTarget cps)
  {
    String msg="";
    YesNoPreferred constr_in_remarks = YesNoPreferred.NO;

    try {
      cps.tgt.remarks = Parameter.get(request,"target_remarks");
      constr_in_remarks = Parameter.getYNP(request,"remark_constraint");

    } catch (Exception exc) {
      msg= "Error occurred validating parameters";
      logger.error("params",exc);
    }
    if (msg.equals("")) {
      try  {
        TargetUpdateRemarksDatum tc = new TargetUpdateRemarksDatum(
	  constr_in_remarks,cps.tgt.remarks);
        //logger.debug("Remarks: " + tc.toString());
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        api.targetUpdateRemarks(cps.tgt.targid,tc);
        msg=CPSConstants.SAVE_OK;
      } catch (InternalServerException iexc) {
        msg= "Error occurred saving Target Remarks information";
        msg += "\n" + iexc.getMessage();
        logger.error(iexc);
      } catch (Exception exc) {
        msg= "Error occurred saving Target Remarks information";
        logger.error("remarks",exc);
      }
    }
    return msg;
  }

  private String processTOO(HttpServletRequest request,HttpSession session,CPSTarget cps)
  {
    String msg="";
    Boolean alt_flag=false;

    try {
      cps.tgt.too_cancel = Parameter.getBoolean(request,"too_cancel");
      cps.tgt.start = Parameter.getDouble(request,"start");
      cps.tgt.stop = Parameter.getDouble(request,"stop");
      cps.tgt.tootype = Parameter.get(request,"too_type");
      cps.tgt.probability = Parameter.getDouble(request,"probability");
      cps.tgt.trig = Parameter.get(request,"trigger");
      cps.tgt.tooremarks = Parameter.get(request,"remarks");
      alt_flag = Parameter.getBoolean(request,"too_alt");
      String astr= Parameter.get(request,"alt_group_name");
      if (astr != null) {
        String tstr[] = astr.split(CPSConstants.DELIM);
        cps.tgt.atg_group_name = tstr[0];
      } else {
        cps.tgt.atg_group_name = null;
      }
      cps.tgt.atg_req_count = Parameter.getInteger(request,"alt_requested_count");
      if (cps.tgt.start == null || cps.tgt.stop==null || cps.tgt.probability==null || cps.tgt.too_cancel==null) {
        msg= "Missing required parameters";
      }
    } catch (Exception exc) {
      msg= "Error occurred validating parameters";
      logger.error("params",exc);
    }
    if (msg.equals("")) {
      try  {
        TargetUpdateTOODatum tc = new TargetUpdateTOODatum(
	  cps.tgt.tootype,cps.tgt.too_cancel,
	  cps.tgt.start.doubleValue(),
	  cps.tgt.stop.doubleValue(),
          cps.tgt.probability.doubleValue(),
	  cps.tgt.response_time,
	  cps.tgt.trig, cps.tgt.tooremarks,
	  alt_flag,cps.tgt.atg_group_name,cps.tgt.atg_req_count);
	
        logger.debug("TOO: " + tc.toString());
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        api.targetUpdateTOO(cps.tgt.targid,tc);
        msg=CPSConstants.SAVE_OK;
      } catch (InternalServerException iexc) {
        msg= "Error occurred saving Target TOO Details. ";
        msg += "\n" + iexc.getMessage();
        logger.error(iexc);
      } catch (Exception exc) {
        msg= "Error occurred saving Target TOO Details. ";
        logger.error("too",exc);
      }
    }

    return msg;
  }
  private String processDDT(HttpServletRequest request,HttpSession session,CPSTarget cps)
  {
    String msg="";

    try {
      //cps.tgt.too_cancel = Parameter.getBoolean(request,"too_cancel");
      //cps.tgt.start = Parameter.getDouble(request,"start");
    } catch (Exception exc) {
      msg= "Error occurred validating parameters";
      logger.error("params",exc);
    }
    if (msg.equals("")) {
      try  {
        //TargetUpdateDDTDatum tc = new TargetUpdateDDTDatum(
	//  cps.tgt.tootype,cps.tgt.too_cancel);
	  
        //logger.debug("DDT: " + tc.toString());
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        //api.targetUpdateDDT(cps.tgt.targid,tc);
        msg=CPSConstants.SAVE_OK;
      } catch (InternalServerException iexc) {
        msg= "Error occurred saving Target DDT Details";
        msg += "\n" + iexc.getMessage();
        logger.error(iexc);
      } catch (Exception exc) {
        msg= "Error occurred saving Target DDT Details";
        logger.error("too",exc);
      }
    }

    return msg;
  }

  private void updateLastSaved(HttpSession session,int pid)
  {
    try {
      CPSUtils cpsutil = new CPSUtils();
      IRestClient api = cpsutil.getAPI(cpsProperties,session);
      api.proposalUpdateLastSaved(pid);
    } catch (Exception exc) {
      logger.error("LastSaved",exc);
    }
  }




  /**
   * Private variables
   */
    private static final long serialVersionUID = 1;
    private Properties cpsProperties;
    private static Logger logger = Logger.getLogger(SaveTarget.class);




  /****************************************************************************/


}

/******************************************************************************/
