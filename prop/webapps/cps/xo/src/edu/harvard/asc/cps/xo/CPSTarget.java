package edu.harvard.asc.cps.xo;
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

import edu.harvard.cda.coords.*;
import ascds.RunCommand;
import edu.harvard.cda.proposal.xo.*;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.lang.Math;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Properties;
import java.text.SimpleDateFormat;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.*;
import org.apache.log4j.Logger;
import com.google.gson.*;


public class CPSTarget
{
   public Boolean isEdit;
   public Boolean isMulti;
   public Boolean isDDTProp;
   public Boolean isValidCoords;
   public Boolean isDDTTgt;
   public VTargetCPSWithDetails tgt;
   public String raString = "";
   public String decString = "";
   public Double  ra;
   public Double  dec;
   public double  slewTax;
   public int     numPointings;

   // TOO category counts.  
   public double  vf_cnt;
   public double  f_cnt;
   public double  m_cnt;
   public double  s_cnt;

   public int     constrnumPointings; // if TOO, don't count constraint on 1st
                                      // since already penalized for TOO

   public ConstraintGrade    phaseGrade;
   public ConstraintGrade    coordGrade;
   public ConstraintGrade    unintGrade;
   public ConstraintGrade    groupGrade;
   public ConstraintGrade    winGrade;
   public ConstraintGrade    monGrade;
   public ConstraintGrade    rollGrade;
   public ConstraintGrade    fupGrade;

   private Properties        cpsProperties;
   private static Logger logger = Logger.getLogger(CPSTarget.class);

 /**
   * Constructor
   *
   * @param isEdit    true if proposal is editable by current user
   * @param isDDTProp true if proposal is a DDT
   * @param isMulti   true if proposal is a multi_cycle
   * @param responseTime  string  DDT proposal usage
   * @param inProperties  Properties  
   *
   */
   public CPSTarget(Boolean isEdit,Boolean isDDTProp,Boolean isMulti,String responseTime,Properties inProperties) 
   {
        this.cpsProperties=inProperties;
        this.isEdit = isEdit;
        this.isDDTProp = isDDTProp;
        this.isMulti=isMulti;
        this.slewTax=0;
        this.numPointings=0;
        this.constrnumPointings=0;
        this.vf_cnt=0;
        this.f_cnt=0;
        this.m_cnt=0;
        this.s_cnt=0;
        this.isDDTTgt=isDDTProp;
        if (responseTime != null && 
            responseTime.indexOf(CPSConstants.NONTRANSIENT) >= 0) 
           this.isDDTTgt=false;
          
   }

 /**
   * Constructor
   *
   * @param tgt       target structure returned from database
   * @param isEdit    true if proposal is editable by current user
   * @param isDDTProp true if proposal is a DDT
   * @param isMulti   true if proposal is a multi_cycle
   * @param responseTime  string  DDT proposal usage
   * @param inProperties  Properties  
   *
   */
   public CPSTarget(VTargetCPSWithDetails tgt,Boolean isEdit,Boolean isDDTProp,Boolean isMulti,String responseTime,Properties inProperties)
   {
        this.cpsProperties=inProperties;
        this.tgt = tgt;
        this.isEdit = isEdit;
        this.isDDTProp = isDDTProp;
        this.isMulti=isMulti;
        this.isDDTTgt=isDDTProp;
        if (responseTime != null && 
            responseTime.indexOf(CPSConstants.NONTRANSIENT) >= 0) 
           this.isDDTTgt=false;
        setCoords(CPS.getDoubleCoord(tgt.ra,0),CPS.getDoubleCoord(tgt.dec,0));
    }


 /**
   *  Target Pointing page
   *
   * @return String xml for target pointing data
  */
  public String getPointingEntry()
  {
    String str = "<data>\n";

    str += "<propno>" + tgt.proposal_number + "</propno>,";
    str += "<pid>" + tgt.proposal_id + "</pid>,";
    str += "<tid>" + tgt.targid + "</tid>,";
    str += "<tgtnbr>" + tgt.targ_num + "</tgtnbr>,";
    str += "<isEdit>" + isEdit.toString() + "</isEdit>,";
    str += "<isDDT>" + isDDTProp.toString() + "</isDDT>,";
    str += "<tgtmon>" + CPS.getYNP(tgt.monitor_flag) + "</tgtmon>,";
    str += "<targname><![CDATA[" + CPS.getString(tgt.targname) + "]]></targname>,";
    str += "<detector>" + tgt.instrument_name + "</detector>,";
    str += "<type>" + tgt.type + "</type>,";
    if ((tgt.type.indexOf("TOO") >= 0) || isDDTTgt == true) {
      if (tgt.trigger_target == null) {
        tgt.trigger_target=true;
      }
      str += "<trigger_target>" + CPS.getYN(tgt.trigger_target) + "</trigger_target>,";
    } else {
      tgt.trigger_target=false;
      str += "<trigger_target>" + "X" + "</trigger_target>,";
    }
    str += "<ss_object><![CDATA[" + CPS.getString(tgt.ss_object) + "]]></ss_object>,";
    str += "<targ_position_flag>" + CPS.getYN(tgt.targ_position_flag) + "</targ_position_flag>,";
    str += "<ra><![CDATA[" + CPS.getString(raString) + "]]></ra>,";
    str += "<dec><![CDATA[" + CPS.getString(decString) + "]]></dec>,";
    str += "<photometry_flag>" + CPS.getYN(tgt.photometry_flag) + "</photometry_flag>,";
    str += "<vmagnitude>" + CPS.getDouble(tgt.vmagnitude,0) + "</vmagnitude>,";
    if ((tgt.y_det_offset != null ) ||
        (tgt.z_det_offset != null ) ||
        (tgt.sim_trans_offset != null ))
      str += "<tgtset>" + "Specify" + "</tgtset>,";
    else
      str += "<tgtset>" + "Default" + "</tgtset>,";

    str += "<y_det_offset>" + CPS.getDouble(tgt.y_det_offset,0) + "</y_det_offset>,";
    str += "<z_det_offset>" + CPS.getDouble(tgt.z_det_offset,0) + "</z_det_offset>,";
    str += "<sim_trans_offset>" + CPS.getDouble(tgt.sim_trans_offset,0) + "</sim_trans_offset>,";
    str += "<raster_scan>" + CPS.getYN(tgt.raster_scan) + "</raster_scan>,";
    str += "<grid_name><![CDATA[" + CPS.getString(tgt.grid_name) + "]]></grid_name>,";
    str += "<num_pointings>" + CPS.getInt(tgt.grid_num_pointings) + "</num_pointings>,";
    str += "<max_radius>" + CPS.getDouble(tgt.grid_max_radius,0) + "</max_radius>,";
    str += "<pointing_constraint>" + CPS.getYN(tgt.pointing_constraint,"") + "</pointing_constraint>,";
    str += "</data>\n";

    return str;
  }

 /**
   *  Target Observing Time page
   *
   * @return String xml for target observing time data
  */
  public String getTimingEntry()
  {
    String str = "<data>\n";
    str += "<propno>" + tgt.proposal_number + "</propno>,";
    str += "<pid>" + tgt.proposal_id + "</pid>,";
    str += "<tid>" + tgt.targid + "</tid>,";
    str += "<tgtnbr>" + tgt.targ_num + "</tgtnbr>,";
    str += "<type>" + tgt.type + "</type>,";
    str += "<isMulti>" + CPS.getYN(isMulti) + "</isMulti>,";

    str += "<isDDT>" + isDDTTgt.toString() + "</isDDT>,";
    str += "<isEdit>" + isEdit.toString() + "</isEdit>,";
    str += "<raster_scan>" + CPS.getYN(tgt.raster_scan) + "</raster_scan>,";
    str += "<obs_time>" + CPS.getDouble(tgt.prop_exposure_time,1) + "</obs_time>,";
    str += "<obs_time_1>" + CPS.getDouble(tgt.est_time_cycle_n1,0) + "</obs_time_1>,";
    str += "<obs_time_2>" + CPS.getDouble(tgt.est_time_cycle_n2,0) + "</obs_time_2>,";
    str += "<uninterrupt>" + CPS.getYNP(tgt.uninterrupt) + "</uninterrupt>,";
    str += "<split_interval>" + CPS.getDouble(tgt.split_interval,0) + "</split_interval>,";
    if ((tgt.type.indexOf("TOO") >= 0) || isDDTTgt == true) {
      if (tgt.trigger_target == null) {
         tgt.trigger_target=true;
      }
      str += "<trigger_target>" + CPS.getYN(tgt.trigger_target) + "</trigger_target>,";
      if (tgt.followup != null && tgt.followup.intValue() > 0) {
         str += "<tgtmon>" + "F" + "</tgtmon>,";
      }  else {
         str += "<tgtmon>" + "N" + "</tgtmon>,";
      }
    }
    else {
      str += "<tgtmon>" + CPS.getYNP(tgt.monitor_flag) + "</tgtmon>,";
      str += "<trigger_target>" + "X" + "</trigger_target>,";

    }

    str += "</data>\n";

    return str;
  }


 /**
   *  Target Instrument page
   *
   * @return String xml for target instrument data
  */
  public String getInstrumentEntry()
  {
    String str = "<data>\n";
    str += "<propno>" + tgt.proposal_number + "</propno>,";
    str += "<pid>" + tgt.proposal_id + "</pid>,";
    str += "<tid>" + tgt.targid + "</tid>,";
    str += "<tgtnbr>" + tgt.targ_num + "</tgtnbr>,";
    str += "<isEdit>" + isEdit.toString() + "</isEdit>,";
    str += "<isDDT>" + isDDTProp.toString() + "</isDDT>,";
    str += "<detector>" + tgt.instrument_name + "</detector>,";
    str += "<grating>" + tgt.grating_name + "</grating>,";
    str += "<hrcTimingMode>" + CPS.getYN(tgt.timing_mode) + "</hrcTimingMode>,";
    str += "<est_cnt_rate>" + CPS.getDouble(tgt.est_cnt_rate,0) + "</est_cnt_rate>,";
    str += "<forder_cnt_rate>" + CPS.getDouble(tgt.forder_cnt_rate,0) + "</forder_cnt_rate>,";
    str += "<total_fld_cnt_rate>" + CPS.getDouble(tgt.total_fld_cnt_rate,0) + "</total_fld_cnt_rate>,";
    str += "<extended_src>" + CPS.getYN(tgt.extended_src) + "</extended_src>,";

    str += "</data>\n";

    return str;
  }

 /**
   *  ACIS Requried Page
   *
   * @return String xml for acis required parameters
  */
  public String getAcisReqEntry()
  {
    String str = "<data>\n";
    str += "<propno>" + tgt.proposal_number + "</propno>,";
    str += "<pid>" + tgt.proposal_id + "</pid>,";
    str += "<tid>" + tgt.targid + "</tid>,";
    str += "<tgtnbr>" + tgt.targ_num + "</tgtnbr>,";
    str += "<isEdit>" + isEdit.toString() + "</isEdit>,";
    str += "<isDDT>" + isDDTProp.toString() + "</isDDT>,";
    str += "<detector>" + tgt.instrument_name + "</detector>,";
    str += "<grating>" + tgt.grating_name + "</grating>,";
    str += "<exp_mode>" + CPS.getString(tgt.exp_mode) + "</exp_mode>,";
    str += "<bep_pack>" + CPS.getString(tgt.bep_pack) + "</bep_pack>,";
    str += "<ccds0_on>" + CPS.getYO(tgt.ccds0_on) + "</ccds0_on>,";
    str += "<ccds1_on>" + CPS.getYO(tgt.ccds1_on) + "</ccds1_on>,";
    str += "<ccds2_on>" + CPS.getYO(tgt.ccds2_on) + "</ccds2_on>,";
    str += "<ccds3_on>" + CPS.getYO(tgt.ccds3_on) + "</ccds3_on>,";
    str += "<ccds4_on>" + CPS.getYO(tgt.ccds4_on) + "</ccds4_on>,";
    str += "<ccds5_on>" + CPS.getYO(tgt.ccds5_on) + "</ccds5_on>,";
    str += "<ccdi0_on>" + CPS.getYO(tgt.ccdi0_on) + "</ccdi0_on>,";
    str += "<ccdi1_on>" + CPS.getYO(tgt.ccdi1_on) + "</ccdi1_on>,";
    str += "<ccdi2_on>" + CPS.getYO(tgt.ccdi2_on) + "</ccdi2_on>,";
    str += "<ccdi3_on>" + CPS.getYO(tgt.ccdi3_on) + "</ccdi3_on>,";
    str += "<chipsel>" + CPS.getYN(tgt.chip_confirm) + "</chipsel>,";
    str += "<spectra_max_count>" + CPS.getDouble(tgt.spectra_max_count,1) + "</spectra_max_count>,";
    str += "<multiple_spectral_lines>" + CPS.getYN(tgt.multiple_spectral_lines,"") + "</multiple_spectral_lines>,";

    str += "</data>\n";

    return str;
  }


 /**
   *  ACIS Optional Parameters Page
   *
   * @return String xml for acis optional parameters
  */
  public String getAcisOptEntry()
  {
    String str = "<data>\n";
    str += "<propno>" + tgt.proposal_number + "</propno>,";
    str += "<pid>" + tgt.proposal_id + "</pid>,";
    str += "<tid>" + tgt.targid + "</tid>,";
    str += "<tgtnbr>" + tgt.targ_num + "</tgtnbr>,";
    str += "<isEdit>" + isEdit.toString() + "</isEdit>,";
    str += "<isDDT>" + isDDTProp.toString() + "</isDDT>,";
    str += "<detector>" + tgt.instrument_name + "</detector>,";
    str += "<grating>" + tgt.grating_name + "</grating>,";
    str += "<exp_mode>" + CPS.getString(tgt.exp_mode) + "</exp_mode>,";
    str += "<most_efficient>" + CPS.getYN(tgt.most_efficient,"Y") + "</most_efficient>,";
    str += "<frame_time>" + CPS.getDouble(tgt.frame_time,0) + "</frame_time>,";
    str += "<subarray>" + CPS.getString(tgt.subarray) + "</subarray>,";
    str += "<subarray_start>" + CPS.getInt(tgt.subarray_start_row) + "</subarray_start>,";
    str += "<subarray_rows>" + CPS.getInt(tgt.subarray_row_count) + "</subarray_rows>,";
    str += "<duty_cycle>" + CPS.getYN(tgt.duty_cycle) + "</duty_cycle>,";
    str += "<secondary_exp_count>" + CPS.getInt(tgt.secondary_exp_count) + "</secondary_exp_count>,";
    str += "<primary_exposure_time>" + CPS.getDouble(tgt.primary_exp_time,0) + "</primary_exposure_time>,";
    str += "<eventfilter>" + CPS.getYN(tgt.eventfilter) + "</eventfilter>,";
    str += "<eventfilter_lower>" + CPS.getDouble(tgt.eventfilter_lower,0) + "</eventfilter_lower>,";
    str += "<eventfilter_range>" + CPS.getDouble(tgt.eventfilter_range,0) + "</eventfilter_range>,";
    str += "<spwindow>" + CPS.getYN(tgt.spwindow) + "</spwindow>,";

    str += "</data>\n";

    return str;
  }

 /**
   *  Target Constraints Page
   *
   * @return String xml for target constraint 
  */
  public String getConstraintEntry()
  {
    Date date= new Date();
    Double mjd = Double.valueOf(0);
    try {
      double jd = DateTimeUtils.toJulianDay(date.getTime());
      mjd =  Double.valueOf(jd - 2400000.5);
    } catch (Exception exc) {
      logger.error(exc);
    }
    String str = "<data>\n";
    str += "<propno>" + tgt.proposal_number + "</propno>,";
    str += "<pid>" + tgt.proposal_id + "</pid>,";
    str += "<tid>" + tgt.targid + "</tid>,";
    str += "<tgtnbr>" + tgt.targ_num + "</tgtnbr>,";
    str += "<isEdit>" + isEdit.toString() + "</isEdit>,";
    str += "<isDDT>" + isDDTProp.toString() + "</isDDT>,";
    str += "<ao_start>" + CPS.getAOStart() + "</ao_start>,";
    str += "<mjd>" + mjd.toString() + "</mjd>,";
    str += "<phase_constraint_flag>" + CPS.getYNP(tgt.phase_constraint_flag) + "</phase_constraint_flag>,";
    str += "<phase_epoch>" + CPS.getDouble(tgt.phase_epoch,0) + "</phase_epoch>,";
    str += "<phase_period>" + CPS.getDouble(tgt.phase_period,0) + "</phase_period>,";
    str += "<phase_start>" + CPS.getDouble(tgt.phase_start,0) + "</phase_start>,";
    str += "<phase_start_margin>" + CPS.getDouble(tgt.phase_start_margin,0) + "</phase_start_margin>,";
    str += "<phase_end>" + CPS.getDouble(tgt.phase_end,0) + "</phase_end>,";
    str += "<phase_end_margin>" + CPS.getDouble(tgt.phase_end_margin,0) + "</phase_end_margin>,";
    str += "<phase_unique>" + CPS.getYN(tgt.phase_unique,"") + "</phase_unique>,";
    str += "<group_constraint>" + CPS.getYNP(tgt.group_obs) + "</group_constraint>,";
    str += "<group_id><![CDATA[" + CPS.getString(tgt.group_id) + "]]></group_id>,";
    str += "<group_interval>" + CPS.getDouble(tgt.group_interval,0) + "</group_interval>,";
    str += "<coord_constraint>" + CPS.getYNP(tgt.multitelescope) + "</coord_constraint>,";
    str += "<coord_interval>" + CPS.getDouble(tgt.multitelescope_interval,0) + "</coord_interval>,";
    str += "<coord_obs><![CDATA[" + CPS.getString(tgt.observatories) + "]]></coord_obs>,";

    str += "</data>\n";

    return str;
  }

 /**
   *  Target TOO Details
   *
   * @return String xml for TOO details
  */
  public String getTooEntry()
  {
    Boolean validTOO = Boolean.valueOf(false);
    if (tgt.type.indexOf("TOO") >= 0 || isDDTTgt) {
      validTOO=true;
    }
         
    String str = "<data>\n";
    str += "<propno>" + tgt.proposal_number + "</propno>,";
    str += "<pid>" + tgt.proposal_id + "</pid>,";
    str += "<tid>" + tgt.targid + "</tid>,";
    str += "<tgtnbr>" + tgt.targ_num + "</tgtnbr>,";
    str += "<altid>" + CPS.getInt(tgt.alternate_id) + "</altid>,";
    str += "<isEdit>" + isEdit.toString() + "</isEdit>,";
    str += "<isDDT>" + isDDTProp.toString() + "</isDDT>,";
    str += "<validTOO>" + validTOO.toString() + "</validTOO>,";
    str += "<too_cancel>" + CPS.getYN(tgt.too_cancel) + "</too_cancel>,";
    str += "<start>" + CPS.getDouble(tgt.start,0) + "</start>,";
    str += "<stop>" + CPS.getDouble(tgt.stop,0) + "</stop>,";
    str += "<probability>" + CPS.getDouble(tgt.probability,0) + "</probability>,";
    str += "<trigger><![CDATA[" + CPS.getString(tgt.trig) + "]]></trigger>,";
    str += "<remarks><![CDATA[" + CPS.getString(tgt.tooremarks) + "]]></remarks>,";
    if (tgt.alternate_id != null && tgt.alternate_id.intValue() > 0)
      str += "<too_alt>" + "Y" + "</too_alt>,";
    else
      str += "<too_alt>" + "N" + "</too_alt>,";
    str += "<alt_group_name><![CDATA[" + CPS.getString(tgt.atg_group_name) + "]]></alt_group_name>,";
    str += "<alt_requested_count>" + CPS.getInt(tgt.atg_req_count) + "</alt_requested_count>,";

    str += "</data>\n";

    return str;
  }

 /**
   *  Target Remarks
   *
   * @return String xml for target remarks
  */
  public String getRemarksEntry()
  {
    String str = "<data>\n";
    str += "<propno>" + tgt.proposal_number + "</propno>,";
    str += "<pid>" + tgt.proposal_id + "</pid>,";
    str += "<tid>" + tgt.targid + "</tid>,";
    str += "<tgtnbr>" + tgt.targ_num + "</tgtnbr>,";
    str += "<isEdit>" + isEdit.toString() + "</isEdit>,";
    str += "<isDDT>" + isDDTProp.toString() + "</isDDT>,";
    str += "<remark_constraint>" + CPS.getYNP(tgt.constr_in_remarks) + "</remark_constraint>,";
    str += "<target_remarks><![CDATA[" + CPS.getString(tgt.remarks) + "]]></target_remarks>,";

    str += "</data>\n";

    return str;
  }

 /**
   *  Target Monitor/Followup Table
   *
   * @return String json for monitor/followups
  */
  public String getMonitorEntry()
  {
    String str = "{\n\"rows\":[\n";
   
    if (tgt.followup != null && tgt.followup.intValue() > 0) {
      String rid= CPS.getInt(tgt.targid) + "-" + "0";
      str += "{ \"id\":\"" + rid + "\",\n\"data\":[\"\",";
      str += "\"" + "Initial" + "\",";
      str += "\"" + CPS.getDouble(tgt.time,0) + "\",";
      str += "\"" + "N/A" + "\",";
      str += "\"" + "N/A" + "\",";
      str += "\"" + "N/A" + "\"";
      str += "] },\n";
    }
    for (int ii=0;ii< tgt.observations.size(); ii++) {
      Observation ps = tgt.observations.get(ii);
      String rid= CPS.getInt(ps.targid) + "-" + CPS.getInt(ps.ordr);
      if (ii != 0) str += ",";
      str += "{ \"id\":\"" + rid + "\",\n\"data\":[\"\",";
      str += "\"" + CPS.getInt(ps.ordr) + "\",";
      str += "\"" + CPS.getDouble(ps.req_obs_time,0) + "\",";
      if ((tgt.followup == null || tgt.followup.intValue() ==0)  && ps.ordr == 1) {
        str += "\"" + "N/A" + "\",";
        str += "\"" + "N/A" + "\",";
        str += "\"" + "N/A" + "\",";
      } else {
        str += "\"" + CPS.getDouble(ps.pre_min_lead,0) + "\",";
        str += "\"" + CPS.getDouble(ps.pre_max_lead,0) + "\",";
        str += "\"" + CPS.getInt(ps.targ_num) + " \",";
      }
      str += "\"" + CPS.getDouble(ps.split_interval,0) + " \"";
      str += "] }\n";
    }
    str += "]\n}\n";

    return str;
  }

 /**
   *  Target ACIS Spatial Windows Table
   *
   * @return String json for ACIS spatial windows
  */
  public String getSpatialWindowEntry()
  {
    String str = "{\n\"rows\":[\n";
    for (int ii=0;ii< tgt.targetACISSpatialWindows.size(); ii++) {
       TargetACISSpatialWindow ps = tgt.targetACISSpatialWindows.get(ii);
       if (ii != 0) str += ",";
       str += "{ \"id\":" + ps.aciswin_id + ",\n\"data\":[\"\",";
       str += "\"" + CPS.getString(ps.chip) + "\",";
       str += "\"" + CPS.getInt(ps.sample) + "\",";
       str += "\"" + CPS.getInt(ps.start_column) + "\",";
       str += "\"" + CPS.getInt(ps.width) + "\",";
       str += "\"" + CPS.getInt(ps.start_row) + "\",";
       str += "\"" + CPS.getInt(ps.height) + "\",";
       str += "\"" + CPS.getDouble(ps.lower_threshold,1) + "\",";
       str += "\"" + CPS.getDouble(ps.pha_range,1) + "\"";
       str += "] }\n";
    }

    str += "]\n}\n";

    return str;
  }

 /**
   *  Target Roll Constraints Table
   *
   * @return String json for roll constraints
  */
  public String getRollEntry()
  {
    String str = "{\n\"rows\":[\n";
    for (int ii=0;ii< tgt.rollRequests.size(); ii++) {
      RollRequest ps = tgt.rollRequests.get(ii);
      String rid= CPS.getInt(ps.targid) + "-" + CPS.getInt(ps.ordr);
      if (ii != 0) str += ",";
      str += "{ \"id\":\"" + rid + "\",\n\"data\":[\"\",";
      //str += "\"" + CPS.getInt(ps.ordr) + "\",";
      str += "\"" + CPS.getYNP(ps.roll_constraint) + "\",";
      str += "\"" + CPS.getDouble(ps.roll,0)+ "\",";
      str += "\"" + CPS.getDouble(ps.roll_tolerance,0) + "\",";
      str += "\"" + CPS.getYN(ps.roll_180) + "\"";
      str += "] }\n";
    }
    str += "]\n}\n";

    return str;
  }

 /**
   *  Target Window Constraints Table
   *
   * @return String json for window constraints
  */
  public String getWindowEntry()
  {
    String str = "{\n\"rows\":[\n";
    for (int ii=0;ii< tgt.timeRequests.size(); ii++) {
      TimeRequest ps = tgt.timeRequests.get(ii);
      String sstr = CPS.convertMS(ps.tstart);
      String estr = CPS.convertMS(ps.tstop);
      String rid= CPS.getInt(ps.targid) + "-" + CPS.getInt(ps.ordr);
      if (ii != 0) str += ",";
      str += "{ \"id\":\"" + rid + "\",\n\"data\":[\"\",";
      //str += "\"" + CPS.getInt(ps.ordr) + "\",";
      str += "\"" + CPS.getYNP(ps.time_constraint) + "\",";
      str += "\"" + sstr + "\",";
      str += "\"" + estr + "\"";
      str += "] }\n";
    }


    str += "]\n}\n";

    return str;
  }


 /**
   *  Validate and set Coordinates
   *
   * @param raStr   RA can be in degrees or sexagesimal
   * @param decStr  Declination can be in degrees or sexagesimal
  */
   public void setCoords(String raStr, String decStr)
    {
       Coordinate coords;

       isValidCoords = true;
       raString = raStr;
       decString = decStr;

       if (raStr  != null && raStr.length() > 0 &&
           decStr != null && decStr.length() > 0 ) {
         try {
           coords = new Coordinate(raStr,decStr,"J2000");
           ra = Double.valueOf(coords.getLon());
           dec = Double.valueOf(coords.getLat());
           raString  = coords.getSexagesimalLon();
           decString = coords.getSexagesimalLat();
         }
         catch (RuntimeException ex) {
           isValidCoords = false;
         }
         catch (Exception exc) {
           isValidCoords = false;
         }
       }
       else {
         ra= Double.valueOf(0.0);
         dec = Double.valueOf(0.0);
         raString = "";
         decString = "";
       }
    }
    public Coordinate getEcliptic()
    {
      Coordinate coords = null;
      try {
        CoordSystem eclipticSystem = new CoordSystem("ECLIPTIC",2000);
        Coordinate j2coords  = new Coordinate(tgt.ra,tgt.dec,"J2000");
        coords = j2coords.transformTo(eclipticSystem);
      } catch (Exception e) {
        logger.error(e);
        coords=null;
      }
      return coords;

    }


   /**
     * Calculate the number of pointings and slew tax for a target
     *
     * Number of pointings is based on the Maximum segment time.
     *   For Grids:   
     *     pointingTime = totalTime / number of grid pointings
     *     nPt = Math.ceil((pointingTime/SEGMENT_TIME)) * number_grid_pointings
     *   For Monitor/Followups
     *     foreach observation:  nPt += Math.ceil(obsTime/SEGMENT_TIME)
     *   For all others
     *     nPt += Math.ceil(obsTime/SEGMENT_TIME)
     *              
     *              
     * Regular Slew Tax:
     *   slewTax = SLEW_RATE * Npt;
     *
     *
     * The grid slew tax is:
     *   grid_time = total_time/number_grid_pointings_requested       
     *   if (grid_time less than GRID_GROUP_TIME) 
     *     for each group of time (GRID_GROUP_TIME) 
     *       slewTax += add SLEW_RATE for 1st, SLEW_RATE/3 for subsequent segments
     *   else
     *     slewTax = (grid_time/SEGMENT_TIME) * SLEW_RATE * number_grid_pointings_requested 
     *   
     * @param segTime      maximum of time per observation allowed
     * @param slewRate     slew rate to be charged per pointing
     * @param gridGroupMax maximum time for a 'group of grids'
     */
   public void calcNPntAndSlew(double segTime,double slewRate,double gridGroupMax) {
      numPointings= 0;
      constrnumPointings= 0;
      slewTax=0;
      double exptime = 0;
      double ttime = 0;
      if (tgt.prop_exposure_time != null)
         exptime = tgt.prop_exposure_time.doubleValue();

       
      if (tgt.raster_scan != null && tgt.raster_scan.booleanValue() == true &&
          tgt.grid_num_pointings != null && tgt.grid_num_pointings.intValue() > 0 ) {
        // This is a grid
        ttime = exptime/tgt.grid_num_pointings.intValue(); 
        numPointings = (int)(Math.ceil(ttime/segTime));
        numPointings *= tgt.grid_num_pointings.intValue();
        logger.debug(tgt.targid + " This is a grid for  " + ttime + numPointings);
        int newgp=1;
        double tgp=0;

        if (ttime < gridGroupMax) {
          for (int nexp = 1; nexp <= tgt.grid_num_pointings.intValue(); nexp++){
            if (newgp == 1) {
              tgp = tgp + ttime + slewRate;
              slewTax += slewRate;
              logger.trace("Adding grid " + slewRate);
              newgp = 0;
            } else {
              logger.trace("GRID check newgp=" + newgp + "  tgp=" + tgp + " " + ttime);
              if(newgp == 0 && (tgp + ttime + (slewRate / 3 )) <= segTime) {
                tgp = tgp + ttime + (slewRate / 3);
                slewTax = slewTax + (slewRate / 3);
                logger.trace("Adding grid " + (slewRate/3));
              }  else {
                 slewTax += slewRate;
                 logger.trace("Adding group grid " + slewRate);
              }
              if( (tgp + ttime + (slewRate/ 3)) > segTime) {
                tgp = 0;
                newgp = 1;
              }
            }
          }
        }  else {
          // charge like regular observations
          slewTax = numPointings * slewRate;
        }
           
      } else if (tgt.observations != null && tgt.observations.size() > 0) {
        for (int ii=0;ii< tgt.observations.size(); ii++) {
          numPointings += (int)(Math.ceil(tgt.observations.get(ii).obs_time/segTime));
        }
        // get initial TOO observation
        if (tgt.time != null && tgt.time.doubleValue() > 0)  {
          numPointings += (int)(Math.ceil(tgt.time.doubleValue()/segTime));
        }
        slewTax = numPointings * slewRate;
      }
      else {
        numPointings = (int)(Math.ceil(exptime/segTime));
        slewTax = numPointings * slewRate;
      }
      constrnumPointings = numPointings;
      
      logger.debug(tgt.targid + " nPt=" + numPointings  + " slew=" + slewTax);
   }

  /** Calculate TOO charge, Follow-up observations will count as "half" TOOs.
    * From MP: Finally, regarding the assignment of TOO categories to follow-up
    * observations, our definition is that the TOO category is determined by
    * the separation between the follow-up observation and the *first*
    * observation, *not* the preceding observation. Thus, for example, a TOO
    * with a 1-4 day response, and follow-ups at intervals of 7-15 days and
    * 15-25 days would count as one VF (the first observation), 1/2  Fast
    * (defined by 7 days after the first observation), and 1/2 Medium (defined
    * by 7+15 = 22 days after the first observation).
    */

    public void setTOOScores()
    {
      vf_cnt=0;
      f_cnt=0;
      m_cnt=0;
      s_cnt=0;
      if ((tgt.trigger_target != null) && (tgt.trigger_target == true) && tgt.start != null) { 
        double tstart = tgt.start.doubleValue();
        if (tstart < 5 ) vf_cnt +=1;
        else if (tstart < 20 ) f_cnt +=1;
        else if (tstart < 40 ) m_cnt +=1;
        else s_cnt += 1;
        if (tgt.observations != null && (tgt.observations.size() > 0)) {
          double days = tstart;
          for (int oo=0;oo< tgt.observations.size();oo++) {
            Observation obs = tgt.observations.get(oo);
            days += obs.pre_min_lead;
            if (days < 5) vf_cnt +=.5; 
            else if (days < 20) f_cnt +=.5; 
            else if (days < 40) m_cnt +=.5; 
            else if (days >= 40 ) s_cnt +=.5; 
          }
        }
      }
    }

  /** 
   * Calculate the constraint grades for this target
   *
   * @param totalGroupTime total time for a group id for this target
   */
   public void calcConstraintGrades(Double totalGroupTime)
   {
      phaseGrade=null;
      coordGrade=null;
      unintGrade=null;
      groupGrade=null;
      winGrade=null;
      monGrade=null;
      fupGrade=null;
      rollGrade=null;
      

      if (tgt.phase_constraint_flag == YesNoPreferred.YES) {
        try {
          phaseGrade = determineGrade(tgt.phase_period,"PHASE_E","PHASE_A");
        } catch (Exception exc) {
          phaseGrade = ConstraintGrade.Error;
        }
      }
      if (tgt.multitelescope == YesNoPreferred.YES) {
        try {
          coordGrade = determineGradeAD(tgt.multitelescope_interval,"COORDINATED_A");
        } catch (Exception exc) {
          coordGrade = ConstraintGrade.Error;
        }
      }
      if (tgt.group_obs == YesNoPreferred.YES) {
        try {
          if (totalGroupTime != null && totalGroupTime.doubleValue() > 0) {
            double value = tgt.group_interval/(totalGroupTime.doubleValue()/86.4);
            groupGrade = determineGradeUp(value,"GROUP_E","GROUP_A");
          } else   {
            logger.debug("GRP: failed check for totalGroupTime") ;
            groupGrade = ConstraintGrade.Error;
          }
        } catch (Exception exc) {
           logger.error("group",exc);
           groupGrade = ConstraintGrade.Error;
        }   
      }

      if (tgt.uninterrupt == YesNoPreferred.YES) {
        try {
          // first find max time for observation
          int num_obs = 1;
          double value = 0;
          // initial time of TOO
          if (tgt.time != null && tgt.time.doubleValue() > 0) 
            value = tgt.time.doubleValue();

          // now check the monitor/followups
          if (tgt.observations != null && tgt.observations.size() > 0) {
            for (int ii=0;ii< tgt.observations.size();ii++) {
              if (tgt.observations.get(ii).obs_time > value) 
                value = tgt.observations.get(ii).obs_time;
            }
          }
          // no monitors or grid, use the full value
          if (value == 0 && tgt.prop_exposure_time != null) 
            value = tgt.prop_exposure_time.doubleValue();
          if (tgt.grid_num_pointings != null && tgt.grid_num_pointings > 0) 
            value = value / tgt.grid_num_pointings.intValue();
  
          unintGrade = determineGrade(value,"UNINTERRUPT_E","UNINTERRUPT_A");
        } catch (Exception exc) {
          unintGrade = ConstraintGrade.Error;       
          logger.error("unint",exc);
        }
      }

      if (tgt.observations != null && tgt.observations.size() > 0 ) {
        boolean is_too = false;
        int oo=1;
        if (tgt.trigger_target != null && tgt.trigger_target == true) {
          is_too = true;
          oo=0;  // initial obs is in too.time table
        }
        if (is_too || tgt.monitor_flag == YesNoPreferred.YES) {
          // Find which segment has the smallest Imax for the ao and use 
          // the fractol from that segment
          // Also need to get the max segment time from the followup

          // this is really monitor and NOT followups
          double max_r = 0.0;
          double max_e = 0.0;
          double max_i = 0.0;
          double mtol = 0.0;
          boolean terr=false;
          // use the observation requesting the greatest time
          for (;oo< tgt.observations.size();oo++) {
            double min_i=0;
            double fractol=0;
            Observation obs = tgt.observations.get(oo);
            if (obs.pre_max_lead > 0 && 
               (max_i == 0 || obs.pre_max_lead <= max_i)) {
              min_i = obs.pre_min_lead;
              if ((obs.pre_max_lead + obs.pre_min_lead) != 0) 
                fractol = (obs.pre_max_lead - obs.pre_min_lead) / (obs.pre_max_lead + obs.pre_min_lead);
              else 
                 fractol = .001;

              if (max_i == 0 || obs.pre_max_lead < max_i ||
                 (obs.pre_max_lead == max_i && fractol < mtol)) {
                max_i = obs.pre_max_lead;
                mtol = fractol;
              }
            }
            if (obs.obs_time > 0 && obs.obs_time > max_e)
              max_e = obs.obs_time;
            if (obs.obs_time > 0 && obs.obs_time > max_r)
              max_r = obs.obs_time;
            if (obs.obs_time < 1 && (obs.pre_max_lead >0 || obs.pre_min_lead > 0)) 
              terr=true;
          }
          if (max_e != 0 ) {
            max_e = max_e/86.4;
            double rval = (max_i * mtol) / max_e;
            if (is_too) {
              if (terr)
                fupGrade =  ConstraintGrade.Error;
              else
                fupGrade = determineGradeUp(rval,"MONITOR_E","MONITOR_A");
            } else {
              if (terr)
                monGrade =  ConstraintGrade.Error;
              else
                monGrade = determineGradeUp(rval,"MONITOR_E","MONITOR_A");
    
            }
          }
        }  
      }


      if (tgt.timeRequests != null && tgt.timeRequests.size() > 0) {
        double tval = 0;
        for (int ii=0; ii < tgt.timeRequests.size();ii++) {
          if (tgt.timeRequests.get(ii).time_constraint == YesNoPreferred.YES &&
              tgt.timeRequests.get(ii).tstart != null && 
              tgt.timeRequests.get(ii).tstop != null) {
            double tdiff = (double)((tgt.timeRequests.get(ii).tstop.longValue()/1000) - 
		(tgt.timeRequests.get(ii).tstart.longValue()/1000));
            if (tdiff > 0) tdiff = tdiff/86400.0;
            tval += tdiff; 
          }
        }
        logger.debug(tgt.targid + " Window : " + tval);
        winGrade = determineGradeUp(tval,"WINDOW_E","WINDOW_A");
      }
      if (tgt.rollRequests != null && tgt.rollRequests.size() > 0) {
        double rval = 0;
        for (int ii=0; ii < tgt.rollRequests.size();ii++) {
          if (tgt.rollRequests.get(ii).roll_constraint == YesNoPreferred.YES &&
              tgt.rollRequests.get(ii).roll != null && 
              tgt.rollRequests.get(ii).roll_tolerance != null) {

              char roll180='Y';
              if (!tgt.rollRequests.get(ii).roll_180) roll180='N';
              double val = calc_rollwindow(
                tgt.rollRequests.get(ii).roll.doubleValue(),
		tgt.rollRequests.get(ii).roll_tolerance.doubleValue(),
		roll180);
              if (val > 0) rval += val;
              logger.debug("calc_roll2window " + ii + " : "  + val);
          }
        }
        logger.debug("calc_roll2window final:  " +  tgt.targid + " = " +rval);
        rollGrade = determineGradeUp(rval,"ROLL_E","ROLL_A");
      }

      // if monitor/followup and no other constraints, then the 1st observation
      // doesn't count
      if (monGrade != null || fupGrade != null) {
        constrnumPointings=numPointings;
logger.debug(tgt.targid + " constrnumPointings: " + constrnumPointings);
        if (numPointings > 0 && 
            winGrade == null &&
            phaseGrade == null &&
            rollGrade == null &&
            unintGrade == null &&
            groupGrade == null &&
            coordGrade == null &&
            (tgt.constr_in_remarks == null || tgt.constr_in_remarks !=  YesNoPreferred.YES)) {
          constrnumPointings -= 1;
          logger.debug(tgt.targid + " constrnumPointings2: " + constrnumPointings);
        }
      }
   }

   /**
    * determines the constraint grade for the value
    *  if constraint < EASY  = easy
    *  else if constraint <= AVERAGE  = average
    *  else  DIFFICULT
    *
    * @param value calculated score for the constraint
    * @param eprop Easy property value to retrieve for this type of constraint
    * @param aprop Average property value to retrieve for this type of constraint
    * @return ConstraintGrade   returns easy,average,difficult
    */
   private ConstraintGrade determineGrade(double value,String eprop,String aprop)
   {
     ConstraintGrade retval=ConstraintGrade.Error;
     try {
       Double easyVal = Double.valueOf(cpsProperties.getProperty(eprop));
       Double avgVal = Double.valueOf(cpsProperties.getProperty(aprop));
       if (value <  easyVal.doubleValue()) {
         retval = ConstraintGrade.Easy;
       } else if (value <= avgVal.doubleValue()) {
         retval= ConstraintGrade.Average;
       } else  {
         retval= ConstraintGrade.Difficult;
       }
     } catch (Exception exc) {
       logger.error("constraints",exc);
     }
     return retval;
   }
   /**
    * determines the constraint grade for the value
    *  if constraint > EASY  = easy
    *  else if constraint >= AVERAGE  = average
    *  else  DIFFICULT
    *
    * @param value calculated score for the constraint
    * @param eprop Easy property value to retrieve for this type of constraint
    * @param aprop Average property value to retrieve for this type of constraint
    * @return ConstraintGrade   returns easy,average,difficult
    */
   private ConstraintGrade determineGradeUp(double value,String eprop,String aprop)
   {
     ConstraintGrade retval=ConstraintGrade.Error;
     try {
       Double easyVal = Double.valueOf(cpsProperties.getProperty(eprop));
       Double avgVal = Double.valueOf(cpsProperties.getProperty(aprop));
       if (value >  easyVal.doubleValue()) {
         retval = ConstraintGrade.Easy;
       } else if (value >= avgVal.doubleValue()) {
         retval= ConstraintGrade.Average;
       } else  {
         retval= ConstraintGrade.Difficult;
       }
     } catch (Exception exc) {
       logger.error("constraints",exc);
     }
     return retval;
   }
   /**
    * determines the constraint grade for the value
    * If   grade >=  Average   else Difficult
    *
    * @param value calculated score for the constraint
    * @param aprop Average property value to retrieve for this type of constraint
    * @return ConstraintGrade   returns easy,average,difficult
    */
   private ConstraintGrade determineGradeAD(double value,String aprop)
   {
     ConstraintGrade retval=ConstraintGrade.Error;
     try {
       Double avgVal = Double.valueOf(cpsProperties.getProperty(aprop));
       if (value >= avgVal.doubleValue()) {
         retval= ConstraintGrade.Average;
       } else  {
         retval= ConstraintGrade.Difficult;
       }
     } catch (Exception exc) {
       logger.error("constraints",exc);
     }
     return retval;
   }

   /**
    * determines the overall worst case constraint grade for this target
    *
    * @return ConstraintGrade return worst case constraint 
    */
   public ConstraintGrade getWorstGrade()
   {
     ConstraintGrade retval = null;

     HashSet<ConstraintGrade> chash = new HashSet<ConstraintGrade>();
     if (unintGrade != null)
         chash.add(unintGrade);
     if (coordGrade != null)
         chash.add(coordGrade);
     if (rollGrade != null)
         chash.add(rollGrade);
     if (winGrade != null)
         chash.add(winGrade);
     if (phaseGrade != null)
         chash.add(phaseGrade);
     if (monGrade != null)
         chash.add(monGrade);
     if (groupGrade != null)
         chash.add(groupGrade);
     if (fupGrade != null)
         chash.add(fupGrade);

     if (chash.contains(ConstraintGrade.Difficult) )
        retval = ConstraintGrade.Difficult;
     else if (chash.contains(ConstraintGrade.Average) )
        retval = ConstraintGrade.Average;
     else if (chash.contains(ConstraintGrade.Easy) )
        retval = ConstraintGrade.Easy;
     else if (chash.contains(ConstraintGrade.Error) )
        retval = ConstraintGrade.Error;

     return retval;
   }


   /**
    * calculate the roll window score for this target
    *
    * executes the mp_rollwin command
    * @return double calculated score for the roll constraint
    */
   private double calc_rollwindow(double roll,double tol, char flag180)
   {
       double retval = -1;
       String result;
       ArrayList<String> envVarList = new ArrayList<String>();
       String relPath = cpsProperties.getProperty("ascds.release"); 
       String envStr = "ASCDS_PROVIS_EPHEMERIS=" + relPath +"/data/provis.ephemeris.dat";
       envVarList.add(envStr);
       envStr = "ASCDS_PROVIS_CAL=" + relPath +"/data/astro.cal";
       envVarList.add(envStr);
       envStr = "JCMPATH=" + relPath +"/config/jcm_data";
       envVarList.add(envStr);
      
       String cmd = relPath + "/bin/" + cpsProperties.getProperty("ROLL2WIN_CMD");
       if (tgt.ra != null && tgt.dec != null) {
       cmd += " " + tgt.ra + " " + tgt.dec;
       cmd += " " + roll + " " + tol + " " + flag180;
       logger.debug(cmd);
       RunCommand runtime = new RunCommand(cmd,envVarList,null);
       result = runtime.getOutMsg();
       if (runtime.getErrMsg() != null && runtime.getErrMsg().length() > 1)
         logger.error(runtime.getErrMsg());
       logger.debug(result);
       if (result != null && result.indexOf("time satis") >= 0 )    {
         Scanner sc = new Scanner(result);
         while (sc.hasNext()) {
           if (sc.hasNextDouble()) {
             retval = sc.nextDouble();
             break;
           } else {
             sc.next();
           }
         }
       } // "%s time satisfying constraint: %d days"
       }  // ra and dec were null
       logger.debug("Roll Constraint " + retval );
       
       return retval;
  } 


  /**
    * verify if chip is turned on or optional
    *
    * @param  chip     chip id (I0-I3, S0-S5)
    * @return boolean  true if chip is Y or O#
   */
  public boolean isChipSelected(String chip)
  {
    boolean retval = false;
    String val = null;
    if (chip.equals("I0")) val=tgt.ccdi0_on;
    else if (chip.equals("I1")) val=tgt.ccdi1_on;
    else if (chip.equals("I2")) val=tgt.ccdi2_on;
    else if (chip.equals("I3")) val=tgt.ccdi3_on;
    else if (chip.equals("S0")) val=tgt.ccds0_on;
    else if (chip.equals("S1")) val=tgt.ccds1_on;
    else if (chip.equals("S2")) val=tgt.ccds2_on;
    else if (chip.equals("S3")) val=tgt.ccds3_on;
    else if (chip.equals("S4")) val=tgt.ccds4_on;
    else if (chip.equals("S5")) val=tgt.ccds5_on;
    if (val != null && !val.equals("N"))
       retval=true;

    return retval;
  }
       

  /**
    * return total count of chips On or Optional
    *
    * @return int total count of chips On or Optional
   */
  public int getChipCount()
  {
    int retval=0; 
    if (!CPS.getYO(tgt.ccdi0_on).equals("")) retval += 1;
    if (!CPS.getYO(tgt.ccdi1_on).equals("")) retval += 1;
    if (!CPS.getYO(tgt.ccdi2_on).equals("")) retval += 1;
    if (!CPS.getYO(tgt.ccdi3_on).equals("")) retval += 1;
    if (!CPS.getYO(tgt.ccds0_on).equals("")) retval += 1;
    if (!CPS.getYO(tgt.ccds1_on).equals("")) retval += 1;
    if (!CPS.getYO(tgt.ccds2_on).equals("")) retval += 1;
    if (!CPS.getYO(tgt.ccds3_on).equals("")) retval += 1;
    if (!CPS.getYO(tgt.ccds4_on).equals("")) retval += 1;
    if (!CPS.getYO(tgt.ccds5_on).equals("")) retval += 1;
  
    return retval;
  }

  /**
    * Validate SIM Trans Offset
    *
    * @return CPSMsg  return true/false and error string
   */
  public CPSMsg validSIM()
  {
    CPSMsg cmsg = new CPSMsg();
    String tstr;

    if ((tgt.type == null || tgt.type.indexOf("CAL") < 0) &&
         tgt.sim_trans_offset != null) {
      try{
        tstr = "SIM." + tgt.instrument_name + ".MIN";
        Double val_min = Double.valueOf(cpsProperties.getProperty(tstr));
        tstr = "SIM." + tgt.instrument_name + ".MAX";
        Double val_max = Double.valueOf(cpsProperties.getProperty(tstr));

        if (tgt.sim_trans_offset < val_min || tgt.sim_trans_offset > val_max) {
           cmsg.msg = "SIM Translation Offset must be between " + val_min + " and " + val_max;
           cmsg.retval=false;
        
        }
      }
      catch (Exception exc) {
         logger.debug("SIM",exc);
         cmsg.retval=false;
         cmsg.msg="Unable to validate SIM offsets";
      }
    }

    return cmsg;
  }

  /**
    * Validate Y Detector Offset
    *
    * @return CPSMsg  return true/false and error string
   */
  public CPSMsg validYDetOffset()
  {
    CPSMsg cmsg = new CPSMsg();
    String tstr;

    if ((tgt.type == null || tgt.type.indexOf("CAL") < 0) &&
         tgt.y_det_offset != null) {
      try{
        tstr = "DET." + tgt.instrument_name + ".MIN";
        Double val_min = Double.valueOf(cpsProperties.getProperty(tstr));
        tstr = "DET." + tgt.instrument_name + ".MAX";
        Double val_max = Double.valueOf(cpsProperties.getProperty(tstr));

        if (tgt.y_det_offset < val_min || tgt.y_det_offset > val_max) {
           cmsg.msg = "Y Detector Offset must be between " + val_min + " and " + val_max;
           cmsg.retval=false;
        }  
      }
      catch (Exception exc) {
         logger.debug("YDET",exc);
         cmsg.retval=false;
         cmsg.msg="Unable to validate Y Detector offset";
      }
    }

    return cmsg;
  }

  /**
    * Validate Z Detector Offset
    *
    * @return CPSMsg  return true/false and error string
   */
  public CPSMsg validZDetOffset()
  {
    CPSMsg cmsg = new CPSMsg();
    String tstr;

    if ((tgt.type == null || tgt.type.indexOf("CAL") < 0) &&
         tgt.z_det_offset != null) {
      try{
        tstr = "DET." + tgt.instrument_name + ".MIN";
        Double val_min = Double.valueOf(cpsProperties.getProperty(tstr));
        tstr = "DET." + tgt.instrument_name + ".MAX";
        Double val_max = Double.valueOf(cpsProperties.getProperty(tstr));

        if (tgt.z_det_offset < val_min || tgt.z_det_offset > val_max) {
           cmsg.msg = "Z Detector Offset must be between " + val_min + " and " + val_max;
           cmsg.retval=false;
        }  
      }
      catch (Exception exc) {
         logger.debug("ZDET",exc);
         cmsg.retval=false;
         cmsg.msg="Unable to validate Z Detector offset";
      }
    }

    return cmsg;
  }

  /**
    * Validate monitor/followup observations equal observing time requested
    *
    * @return CPSMsg  return true/false and error string
   */
  
  public CPSMsg validObservations() {
    CPSMsg cmsg = new CPSMsg();
    try {
      double ootime= 0;
      if (tgt.observations != null) {
        for (int oo=0;oo< tgt.observations.size() ;oo++) {
          Observation fup = tgt.observations.get(oo);
          ootime += fup.obs_time;
        }
      }
      if (tgt.trigger_target != null && tgt.trigger_target &&
          tgt.time != null)
          ootime += tgt.time.doubleValue();
      if (ootime > 0 ) {
        if (Math.abs(ootime - tgt.prop_exposure_time.doubleValue()) > .00001) {
          cmsg.retval=false;
          if (tgt.trigger_target == null || !tgt.trigger_target)
            cmsg.msg="Observing Time is not equal to sum of observations.";
          else
            cmsg.msg="Observing Time is not equal to initial time + sum of followups.";
        }
      }
    }
    catch (Exception exc) {
      logger.debug("Observations",exc);
      cmsg.retval=false;
      cmsg.msg="Unable to validate monitor/followups.";
    }
    return cmsg;
  }

  /*
   * If target is a trigger Target for a TOO or DDT
   * @return Boolean  if this target should have TOO details
   */
  public Boolean shouldHaveTOODetails() {
    Boolean retval=false;
    if ((tgt.type.indexOf("TOO") >= 0) || isDDTTgt == true) {
      if (tgt.trigger_target == null || tgt.trigger_target) {
        retval=true;
      }
    }
    return retval;
  } 

  public String WriteTarget()
  {
    String json = "";
    try {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      json = gson.toJson(tgt);
    } catch (Exception e) {
      json="";
      logger.error(e);
    }
    return json;
  }

  public String WriteDDT()
  {
     StringBuffer sb = new StringBuffer("");
     sb.append("TARGET.NUMBER[]=" + tgt.targ_num + "\n");
     sb.append("TARGET.NAME[]=" + CPS.getString(tgt.targname) + "\n");
     sb.append("COORD.RA[]=" + CPS.getString(raString) + "\n");
     sb.append("COORD.DEC[]=" + CPS.getString(decString) + "\n");
     sb.append("INST.DETECTOR[]=" + CPS.getString(tgt.instrument_name) + "\n");
     sb.append("TOTAL.OBS.TIME[]=" + CPS.getDouble(tgt.prop_exposure_time,1) + "\n");
     sb.append("NUMBER.OBS[]=" + CPS.getInt(tgt.num_observations) + "\n");
     
     sb.append("TOO.MAX.FOLLOWUP[]=" + CPS.getInt(tgt.observations.size()) + "\n");
     sb.append("TOO.INITIAL.TIME[]=" + CPS.getDouble(tgt.time,0) + "\n");
     sb.append("TOO.START[]=" + CPS.getDouble(tgt.start,0) + "\n");
     sb.append("TOO.STOP[]=" + CPS.getDouble(tgt.stop,0) + "\n");
     int ii=0;
     if (tgt.time != null && tgt.time > 0) {
       for (ii=0;ii< tgt.observations.size(); ii++) {
         Observation ps = tgt.observations.get(ii);
         sb.append("TOO.FUP.TIME[]="   + CPS.getDouble(ps.req_obs_time,0) + "\n");
       }
     }
     if (ii <=0) 
       sb.append("TOO.FUP.TIME[]= \n" );
     return sb.toString();
  }



} // end of class
