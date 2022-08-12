package edu.harvard.asc.cps.xo;

import java.util.ArrayList;
import java.util.Properties;
import java.util.HashMap;
import java.util.Date;
import org.joda.time.*;
import org.apache.log4j.Logger;
import edu.harvard.cda.proposal.xo.*;


public class  ValidateProposal 
{
  public ArrayList<GUIMessage> errorList; 
  private Properties cpsProperties;
  private static Logger logger = Logger.getLogger(ValidateProposal.class);
  public Boolean hasErrors;

  // Target Group and Alternate Target 
  private  HashMap<String,Integer> grpCnt = null;
  private  HashMap<String,Double> grpTime = null;
  private  HashMap<String,Double> grpInterval = null;
  private  HashMap<String,Integer> atgCnt = null;
  private  HashMap<String,Integer> atgReq = null;
  private  HashMap<Integer,Double> tgtHash = null;
  private  double ttime=0.0;
  private  double ttime_1=0.0;
  private  double ttime_2=0.0;
  private  double ttime_slew=0.0;
  private  int tc_cnt =0;


  public ValidateProposal(Properties cpsProp) {
    cpsProperties = cpsProp;
    errorList = new ArrayList<GUIMessage>();
  }

  public void verifyProposal(CPSProposal cps,CPSTargetList tgtList) 
  {
    
    ttime=0.0;
    ttime_1=0.0;
    ttime_2=0.0;
    ttime_slew=0.0;
    tc_cnt=0;
    grpCnt = new HashMap<String,Integer>();
    grpTime = new HashMap<String,Double>();
    grpInterval = new HashMap<String,Double>();
    atgCnt = new HashMap<String,Integer>();
    atgReq = new HashMap<String,Integer>();
    tgtHash = new HashMap<Integer,Double>();
    

    try {

    if (hasRequired(cps.vprop.totalTime,false))
      ttime = cps.vprop.totalTime.doubleValue();
      ttime_slew = ttime;

    // populate target info needed for overall proposal checks
    buildTargetInfo(tgtList);


    // REQUIRED Fields
    if (!hasRequired(cps.vprop.categoryDescription) )
      errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.COVER_PAGE,"Subject Category is required."));
    if (!hasRequired(cps.vprop.title) )
      errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.COVER_PAGE,"Proposal Title is required."));
    if (!hasRequired(cps.vprop.abstractText) )
      errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.COVER_PAGE,"Proposal Abstract is required."));
    if (!hasRequired(cps.vprop.scienceKeywords) ) {
      errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.COVER_PAGE,"Science Keywords are required."));
    } else {
      int counter = 0;
      // k1;k2;k3;k4;k5
      for( int ii=0; ii<cps.vprop.scienceKeywords.length(); ii++ ) {
        if( cps.vprop.scienceKeywords.charAt(ii) == ';' ) 
          counter++;
      } 
      if (counter < 3) {
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.COVER_PAGE,"Minimum of 4 Science Keywords are required."));
      }
    }
    if (!hasRequired(cps.vprop.type) )
      errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.COVER_PAGE,"Proposal Type is required."));
    else if (!cps.isArcTheory() &&
               !hasRequired(cps.vprop.numTargets,false)) 
      errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.COVER_PAGE,"Proposal Type of " + cps.vprop.type + " requires at least 1 target."));
    if (cps.isArcTheory()) {
       if (!hasRequired(cps.vprop.totalTime,false))
          errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.COVER_PAGE,"Proposal Type of " + cps.vprop.type + " requires Requested Budget > 0."));
    }

    // Proposal Type
    Integer vlptime= Integer.valueOf(cpsProperties.getProperty("VLP.TIME"));
    Integer lptime= Integer.valueOf(cpsProperties.getProperty("LP.TIME"));
    if (cps.vprop.type.indexOf("VLP") >= 0 ) {
      if (ttime_slew < lptime)
         errorList.add(new GUIMessage(CPSConstants.WARN_TYPE,CPSConstants.COVER_PAGE,"Total time including slew may be less than the MINIMUM " + vlptime + "ksec for Very Large Project(VLP) and "+ lptime + "ksec for Large Project(LP) proposals. Consider using a GO proposal. "));
      else if (ttime_slew < vlptime && ttime_slew >= lptime )
          errorList.add(new GUIMessage(CPSConstants.WARN_TYPE,CPSConstants.COVER_PAGE,"Total time including slew may be less than the MINIMUM " + vlptime + "ksec for Very Large Project(VLP) proposals. Consider using an LP proposal. "));
    } else if (cps.vprop.type.indexOf("LP") >= 0 ) {
      if (ttime_slew < lptime)
        errorList.add(new GUIMessage(CPSConstants.WARN_TYPE,CPSConstants.COVER_PAGE,"Total time including slew may be less than the MINIMUM " + lptime + "ksec for Large Project(LP) proposals. Consider using a GO proposal. "));
      else if (ttime_slew >= vlptime)
          errorList.add(new GUIMessage(CPSConstants.WARN_TYPE,CPSConstants.COVER_PAGE,"Total time including slew may be greater than the MINIMUM " + vlptime + "ksec for Very Large Project(VLP) proposals. Consider using a VLP proposal. "));
    } else if (cps.vprop.type.indexOf("XVP") >= 0 ) {
      Integer xvptime= Integer.valueOf(cpsProperties.getProperty("XVP.TIME"));
      if (ttime_slew < xvptime)
         errorList.add(new GUIMessage(CPSConstants.WARN_TYPE,CPSConstants.COVER_PAGE,"Total time including slew may be less than the MINIMUM " + lptime+ "ksec for X-ray Visionary Projects(XVP) proposals. "));
    } else if (cps.vprop.type.indexOf("ARC") >= 0 ) {
        //datarights, joint None or NRAO
    }

    if (cps.vprop.requestExtraFlag == null) {
         errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.COVER_PAGE,"Proprietary Rights is required.")); 
    } else {
      if ((cps.vprop.requestExtraFlag != 'S' && cps.vprop.requestExtraFlag !='D') &&
         (cps.vprop.type.indexOf("ARC") < 0  &&
          cps.vprop.type.indexOf("THE") < 0 &&
          cps.vprop.type.indexOf("VLP") < 0)) {
          String drights = cps.vprop.requestExtraFlag.toString();
          if (drights.equals("N")) drights ="No Proprietary Rights";
          else if (drights.equals("1")) drights += " month";
          else drights += " months";
          errorList.add(new GUIMessage(CPSConstants.NOTE_TYPE,CPSConstants.COVER_PAGE,"Non-standard Proprietary Rights :  " + drights)); 
      }
      if ((cps.vprop.type.indexOf("DDT") >= 0) &&
          (cps.vprop.requestExtraFlag != 'N')) {
        if (cps.vprop.ddtProposalDatum == null ||
            cps.vprop.ddtProposalDatum.rights_justification == null ||
            cps.vprop.ddtProposalDatum.rights_justification.length() < 2) {
          errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.COVER_PAGE,"Proprietary Rights Justification is required.")); 
        }
      }
    }
    if (!hasRequired(cps.vprop.piId,false) )
      errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.PI_PAGE,"Principal Investigator(PI) is required."));

    // No CoIs
    if (cps.vprop.coInvestigators == null || cps.vprop.coInvestigators.size() <= 0) 
       errorList.add(new GUIMessage(CPSConstants.NOTE_TYPE,CPSConstants.COI_PAGE,"No Co-Investigators have been specified."));

    else if (cps.vprop.piId != null) {
      for (int cc = 0;cc < cps.vprop.coInvestigators.size();cc++) {
        if (cps.vprop.piId.intValue() == cps.vprop.coInvestigators.get(cc).pers_id)
           errorList.add(new GUIMessage(CPSConstants.WARN_TYPE,CPSConstants.COI_PAGE,"The PI of this proposal is also identified as a CoI for " + cps.vprop.last));
      }
    }
       

    // JOINT
    if (cps.vprop.joint.indexOf("CXO") >= 0 ) {
      String jj = cps.vprop.joint.replace("CXO-","");
      errorList.add(new GUIMessage(CPSConstants.NOTE_TYPE,CPSConstants.JOINT_PAGE,"This proposal has already been approved at the " + jj + " review."));
    }
    // Linked
    if (cps.vprop.linkedProposal != null && cps.vprop.linkedProposal) {
      if (cps.vprop.linkedPropNum == null || cps.vprop.linkedPropNum.equals("")) {
        errorList.add(new GUIMessage(CPSConstants.WARN_TYPE,CPSConstants.COVER_PAGE,"Linked Proposal Number has not been entered for this proposal."));
       }
    }
    // Multicycle
    //logger.debug("MULTICYCLE: " + cps.vprop.multiCycle);
    //logger.debug("MULTICYCLE: " + ttime_1 + "--" + ttime_2 + "--" + ttime);
    if (cps.vprop.multiCycle != null && cps.vprop.multiCycle) {
      if (ttime_1 == 0 && ttime_2 == 0) 
errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.COVER_PAGE,"MultiCycle Proposals must have Projected Times for future cycles.  No targets found with projected times in future cycles."));
      if ((ttime_1 + ttime_2) >= ttime)
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.COVER_PAGE,"MultiCycle Proposals must have at least 1ks of time allocated to the current cycle."));
      if (tc_cnt <= 0)
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.COVER_PAGE,"MultiCycle Proposals must have a constraint that requires time in the future cycle."));
      if (cps.vprop.type.indexOf("TOO") >= 0) 
        errorList.add(new GUIMessage(CPSConstants.NOTE_TYPE,CPSConstants.COVER_PAGE,"Multi-Cycle Proposal: All TOO triggers must be within the current cycle, although followups may extend into future cycles."));
    } else if (ttime_1 != 0 ||  ttime_2 != 0)  {
      errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.COVER_PAGE,"MultiCycle Proposal must be 'Yes' if targets have Projected Times for future cycles."));
    }

    // uploads
    
    if (cps.needsSJ()) {
      if (cps.vupload == null || 
          cps.vupload.sjUploadMSSE == null || cps.vupload.sjUploadMSSE.longValue() <= 0)
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.FILES_PAGE,"Missing Science Justification"));
      if (cps.vupload == null || 
          cps.vupload.cvUploadMSSE == null || cps.vupload.cvUploadMSSE.longValue() <= 0)
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.FILES_PAGE,"Missing Team Expertise"));
      //if (cps.vupload == null || 
          //cps.vupload.pcUploadMSSE == null || cps.vupload.pcUploadMSSE.longValue() <= 0)
        //errorList.add(new GUIMessage(CPSConstants.WARN_TYPE,CPSConstants.FILES_PAGE,"Missing Previous Chandra "));
    }

    // ------------------------------------------------------------------------
    // Validate DDT proposals
    // ------------------------------------------------------------------------
    if (cps.isDDTProp) {
      if (cps.vprop.ddtProposalDatum == null  || 
          (cps.vprop.ddtProposalDatum.response_time == null)  ||
          (cps.vprop.ddtProposalDatum.response_time.length() < 2)) {
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.DDT_PAGE,"Response Time is required."));
      } 
      else if (!cps.vprop.ddtProposalDatum.response_time.equals(CPSConstants.NONTRANSIENT)) {
        if ((cps.vprop.ddtProposalDatum.response_justification == null) ||
            cps.vprop.ddtProposalDatum.response_justification.length() < 2) {
           errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.DDT_PAGE,"Justification of Response Time is required."));
        }
        if ((cps.vprop.ddtProposalDatum.response_time.indexOf("FAST") >= 0) &&
            (cps.vprop.ddtProposalDatum.contact_info == null  ||
             cps.vprop.ddtProposalDatum.contact_info.length() < 2)) {
           errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.DDT_PAGE,"24 Hour Contact Information is required."));
        }
        if ((cps.vprop.ddtProposalDatum.target_justification == null) ||
            cps.vprop.ddtProposalDatum.target_justification.length() < 2) {
           errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.DDT_PAGE,"Justification for this Particular Target is required."));
        }
        if ((cps.vprop.ddtProposalDatum.next_cfp == null) ||
            cps.vprop.ddtProposalDatum.next_cfp.length() < 2) {
           errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.DDT_PAGE,"Why Can't the observation wait for the next Chandra CfP is required."));
        }
          
      }
    }
    

      
    // --------------------------------------------------------------------------------
    // Validate each Target 
    // --------------------------------------------------------------------------------
    for (int ii=0;ii< tgtList.size();ii++) {
       CPSTarget cpstgt = tgtList.get(ii);
       // go validate individual targets
       validateTarget(cps,cpstgt,tgtHash);
       //this the trigger target for a TOO?: Target Number 1 is not referenced in the TOO Trigger target Followup table 

    }
    // Observing time for entire observational proposal
    if (!cps.isArcTheory() && !hasRequired(ttime,false))
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.TARGET_PAGE,
                "Must have at least one target with Observing Time > 0"));

    if (hasErrors() <= 0) {
       errorList.add(0,new GUIMessage(CPSConstants.SUCCESS_TYPE,"","Proposal successfully verified, no errors found"));
    }
   
    } catch (Exception exc) {
      logger.error("VALIDATE",exc);
      errorList.add(0,new GUIMessage(CPSConstants.ERROR_TYPE,"","UNEXPECTED ERROR, please contact the HelpDesk"));
      
    }

  }
          
  // ----------------------------------------------------------------------------------
  // validate each target
  // ----------------------------------------------------------------------------------
  private void validateTarget(CPSProposal cps,CPSTarget cpstgt,HashMap<Integer,Double>tgtHash) 
  {
    String tstr;

    try {
    String detector = CPS.getString(cpstgt.tgt.instrument_name);
    String grating = CPS.getString(cpstgt.tgt.grating_name);

    tstr = CPS.getString(cpstgt.tgt.ss_object);
    if (tstr.equalsIgnoreCase("None")) {
      if (!hasRequired(cpstgt.tgt.targname))
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.POINTING_PAGE,
          cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Target Name must be entered."));
    }
    if (cps.vprop.type.indexOf("TOO") < 0)  {
       if (cpstgt.isDDTProp && (cpstgt.tgt.targ_position_flag == null || !cpstgt.tgt.targ_position_flag) ) {
          errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.POINTING_PAGE,
          cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Is Target Position known and fixed? must be set to Y"));
       } else if ((cpstgt.tgt.targ_position_flag == null || !cpstgt.tgt.targ_position_flag)  && 
            (cpstgt.tgt.ss_object == null || cpstgt.tgt.ss_object.equalsIgnoreCase("NONE")))
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.POINTING_PAGE,
          cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Is Target Position known and fixed? must be set to Y"));
;
    }
    if (cpstgt.tgt.targ_position_flag == null || cpstgt.tgt.targ_position_flag) {
      if (!hasRequired(cpstgt.tgt.ra,true) || !hasRequired(cpstgt.tgt.dec,true))
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.POINTING_PAGE,
          cpstgt.tgt.targ_num,cpstgt.tgt.targid,"RA and Dec are required."));
      //if (!hasRequired(cpstgt.tgt.dec,true))
        //errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.POINTING_PAGE,
          //cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Dec is required for " + cps.vprop.type  + " proposals."));
    }

    // trigger target
    if (cpstgt.tgt.raster_scan != null && cpstgt.tgt.raster_scan) {
       if (cpstgt.tgt.grid_num_pointings > cpstgt.tgt.prop_exposure_time) 
         errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.POINTING_PAGE,
           cpstgt.tgt.targ_num,cpstgt.tgt.targid," Grid Number of Pointings must be <= Observing Time requested. Minimum of 1ks per pointing."));
       if (cpstgt.tgt.monitor_flag != null && cpstgt.tgt.monitor_flag != YesNoPreferred.NO) 
         errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.POINTING_PAGE,
           cpstgt.tgt.targ_num,cpstgt.tgt.targid," Grids are not allowed if  monitoring/followup observations."));
    }

    // OFFSETS
    if (hasRequired(cpstgt.tgt.y_det_offset,false)) {
      CPSMsg cpsmsg= cpstgt.validYDetOffset();
      if (!cpsmsg.retval) 
         errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.POINTING_PAGE,cpstgt.tgt.targ_num,cpstgt.tgt.targid,cpsmsg.msg));
    }
    if (hasRequired(cpstgt.tgt.z_det_offset,false)) {
      CPSMsg cpsmsg= cpstgt.validZDetOffset();
      if (!cpsmsg.retval) 
         errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.POINTING_PAGE,cpstgt.tgt.targ_num,cpstgt.tgt.targid,cpsmsg.msg));
    }
    if (hasRequired(cpstgt.tgt.sim_trans_offset,false)) {
      CPSMsg cpsmsg= cpstgt.validSIM();
      if (!cpsmsg.retval) 
         errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.POINTING_PAGE,cpstgt.tgt.targ_num,cpstgt.tgt.targid,cpsmsg.msg));

    }
    
    if (cpstgt.tgt.monitor_flag != null && cpstgt.tgt.monitor_flag != YesNoPreferred.NO) {
       if (cpstgt.tgt.observations == null || cpstgt.tgt.observations.size() < 2) {
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.TIME_PAGE,
          cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Do you want more than one observation of this target?  must be set to N. "));
      }
    }

    if (cps.shouldHaveTOODetails()) {
      if (cpstgt.tgt.trigger_target == null )  {
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.TIME_PAGE,
          cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Is this the trigger target for a TOO must be set to Y/N "));
        // not set so require for rest of the validation
        cpstgt.tgt.trigger_target=Boolean.valueOf(true);
      }
      if ( cpstgt.tgt.trigger_target) {

        // observing time is required for trigger target
        if (!hasRequired(cpstgt.tgt.prop_exposure_time,false))
          errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.TIME_PAGE,
            cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Observing Time must be > 0"));

        // and check that obslist has target number and that target number exists?
        if (cpstgt.tgt.observations != null) {
          for (int oo=0;oo< cpstgt.tgt.observations.size() ;oo++) {
            Observation fup = cpstgt.tgt.observations.get(oo);
            if (fup.targ_num == null) {
              errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.TIME_PAGE,
                cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Followup Target must be specified."));
            } else if (cpstgt.tgt.targ_num != fup.targ_num.intValue())  { 
              if (tgtHash.containsKey(fup.targ_num)) {
                Double dval = tgtHash.get(fup.targ_num);
                if (dval != null && dval > 0) 
                  errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.TIME_PAGE,
                    cpstgt.tgt.targ_num,cpstgt.tgt.targid,"If followup target is not the current target, then it must be a non-trigger TOO target. Please check parameters for followup target #" + fup.targ_num ));
              }
              else {
                errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.TIME_PAGE,
                  cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Followup Target " + fup.targ_num + " not found."));
              }
            }
          }
        }
                
      } else {
        // non-trigger target
        if (hasRequired(cpstgt.tgt.prop_exposure_time,false)) 
          errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.TIME_PAGE,
            cpstgt.tgt.targ_num,
            cpstgt.tgt.targid,"Observing time for non-trigger TOO target must be 0"));
      }
    } else {
      if (!hasRequired(cpstgt.tgt.prop_exposure_time,false))
          errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.TIME_PAGE,
            cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Observing Time must be > 0"));
    }
    if (hasRequired(cpstgt.tgt.prop_exposure_time,false)) {
      CPSMsg cpsmsg= cpstgt.validObservations();
      if (!cpsmsg.retval) 
         errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.TIME_PAGE,cpstgt.tgt.targ_num,cpstgt.tgt.targid,cpsmsg.msg));
    }

    if (cps.vprop.multiCycle == null || !cps.vprop.multiCycle) {
      if (hasRequired(cpstgt.tgt.est_time_cycle_n1,false) ||
         hasRequired(cpstgt.tgt.est_time_cycle_n2,false)) {
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.TIME_PAGE,
        cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Proposals must be declared a Multi-Cycle if projected times for future cycles is entered."));
      }
    }
    if (!hasRequired(cpstgt.tgt.est_cnt_rate,true)) 
      errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.INST_PAGE,
        cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Source Count Rate must be entered."));
   
    if (detector.indexOf("ACIS") >= 0) {
      if (!hasRequired(cpstgt.tgt.exp_mode)) 
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISREQ_PAGE,
        cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Exposure Mode is required."));

      if (!hasRequired(cpstgt.tgt.bep_pack)) 
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISREQ_PAGE,
        cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Event Telemetry is required."));
      
      if (hasRequired(cpstgt.tgt.exp_mode) && 
          cpstgt.tgt.exp_mode.equals("CC")) {

        if (hasRequired(cpstgt.tgt.bep_pack) && cpstgt.tgt.bep_pack.equals("VF")) {
           errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,
              CPSConstants.ACISOPT_PAGE,
              cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Event Telemetry of VF is not allowed for CC Exposure Mode."));
         }
         if (hasRequired(cpstgt.tgt.most_efficient) && !cpstgt.tgt.most_efficient ) {
           errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,
              CPSConstants.ACISOPT_PAGE,
              cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Use most efficient frame time must be Yes for CC Exposure Mode."));
         }
         if (hasRequired(cpstgt.tgt.subarray) && !cpstgt.tgt.subarray.equals("NONE")) {
           errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,
              CPSConstants.ACISOPT_PAGE,
              cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Subarrays are not allowed for CC Exposure Mode."));
         }
         if (hasRequired(cpstgt.tgt.duty_cycle) && cpstgt.tgt.duty_cycle) {
           errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,
              CPSConstants.ACISOPT_PAGE,
              cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Alternating Exposure is not allowed for CC Exposure Mode."));
         }
       }
      if (detector.indexOf("ACIS-I") >= 0 && grating.indexOf("NONE") >= 0) {
        if (!hasRequired(cpstgt.tgt.spectra_max_count,false) ) {
          errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISREQ_PAGE,
          cpstgt.tgt.targ_num,cpstgt.tgt.targid,"What is the maximum number of counts you expect in any of the spectra you will analyze from this observation? is required."));
        }
        if (!hasRequired(cpstgt.tgt.multiple_spectral_lines) ) {
          errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISREQ_PAGE,
          cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Do you expect to analyze more than 2 resolved spectral lines in that spectrum? is required."));
        }
      } else {
        if (hasRequired(cpstgt.tgt.spectra_max_count,false) ) {
          errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISREQ_PAGE,
          cpstgt.tgt.targ_num,cpstgt.tgt.targid,"What is the maximum number of counts you expect in any of the spectra you will analyze from tblank observation? must be blank."));
        }
        if (hasRequired(cpstgt.tgt.multiple_spectral_lines) &&
	    cpstgt.tgt.multiple_spectral_lines)  {
          errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISREQ_PAGE,
          cpstgt.tgt.targ_num,cpstgt.tgt.targid,"'Do you expect to analyze more than 2 resolved spectral lines in that spectrum?' must be blank")); 
        }
      }
         
      int chipcnt=cpstgt.getChipCount();
      if (chipcnt > 1 && !CPS.getYN(cpstgt.tgt.chip_confirm).equals("Y")) {
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISREQ_PAGE,
        cpstgt.tgt.targ_num,cpstgt.tgt.targid,"I have reviewed the selection of ACIS chips and identified any chips whose operation is optional: You must confirm your chip selection if more than 1 CCD has been selected. "));
      }
      if (chipcnt <= 0) 
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISREQ_PAGE,
        cpstgt.tgt.targ_num,cpstgt.tgt.targid,"CCDs On are required."));
      else if (cpstgt.tgt.frame_time != null && cpstgt.tgt.frame_time > 0) {
        double frame_time = cpstgt.tgt.frame_time.doubleValue();
        if (chipcnt < 3) {
          if ( frame_time < .1)
            errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISOPT_PAGE,
                cpstgt.tgt.targ_num,
                cpstgt.tgt.targid,"Frame time must be 0. or .1 - 10.0 for 1 or 2 CCDs."));
        } else if (chipcnt < 5) {
          if ( frame_time < .2)
            errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISOPT_PAGE,
                cpstgt.tgt.targ_num,
                cpstgt.tgt.targid,"Frame time must be 0. or .2 - 10.0 for 3 or 4 CCDs."));
        }
        else if ( frame_time < .3)
            errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISOPT_PAGE,
                cpstgt.tgt.targ_num,
                cpstgt.tgt.targid,"Frame time must be 0. or .3 - 10.0 for 5 or 6 CCDs."));
        frame_time *= 10;
        if ((frame_time / (int)(frame_time)) > 1) {
            errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISOPT_PAGE,
                cpstgt.tgt.targ_num,cpstgt.tgt.targid,
                "Frame time must be specified in intervals of .1"));
        }

      }
      if (cpstgt.tgt.subarray != null && cpstgt.tgt.subarray.equalsIgnoreCase("CUSTOM")) {
        if (chipcnt > 1) {
          if (cpstgt.tgt.subarray_row_count == null || cpstgt.tgt.subarray_row_count < 128)
            errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISOPT_PAGE,
                cpstgt.tgt.targ_num,cpstgt.tgt.targid,
                "Subarray Number of Rows  must be 128 or greater if more than 1 chip has been selected."));
        }
      }

      if (cpstgt.tgt.targetACISSpatialWindows != null) {
        for (int aa=0;aa<cpstgt.tgt.targetACISSpatialWindows.size();aa++) {
           TargetACISSpatialWindow asw = cpstgt.tgt.targetACISSpatialWindows.get(aa);
           if (asw.chip == null) 
             errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISOPT_PAGE,
             cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Missing spatial widow chip."));
           else if (!cpstgt.isChipSelected(asw.chip)) 
             errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISOPT_PAGE,
             cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Spatial Window Chip " + asw.chip + " has not been selected."));
           if ((asw.start_column + asw.width) > 1025)  
             errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISOPT_PAGE,
             cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Chip " + asw.chip + ": Spatial Window Start Column + Width cannot exceed 1024"));
           if ((asw.start_row + asw.height) > 1025)  
             errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.ACISOPT_PAGE,
             cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Chip " + asw.chip + ": Spatial Window Start Row + Height cannot exceed 1024"));

        }       
      }       

   }
   if (hasRequired(cpstgt.tgt.phase_epoch,false)) {
     double mjd = 0;
     try {
       Date date= new Date();
       double jd = DateTimeUtils.toJulianDay(date.getTime());
       mjd =  jd - 2400000.5;
     } catch (Exception exc) {
       logger.error(exc);
     }
     double pmax = mjd + (365*6);
     double pmin = mjd - (365*6);
     if (cpstgt.tgt.phase_epoch < pmin || cpstgt.tgt.phase_epoch > pmax) {
       errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.CONSTRAINT_PAGE,
       cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Phase Epoch must be within 5 years of current date."));
     }
   }
   for (int tt=0;tt< cpstgt.tgt.timeRequests.size();tt++) {
      Date date = new Date();
      TimeRequest ps = cpstgt.tgt.timeRequests.get(tt);
      if ((ps.tstart < date.getTime())  ||
          (ps.tstop < date.getTime())) {
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.CONSTRAINT_PAGE,
             cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Window constraint " + CPS.getInt(ps.ordr) + " must be greater than the current date."));
      }
    }


    if (cps.shouldHaveTOODetails()) {
      if (cpstgt.tgt.trigger_target) {
        if (cpstgt.tgt.trig == null || cpstgt.tgt.trig.equals("")) 
          errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.TOO_PAGE,
             cpstgt.tgt.targ_num,cpstgt.tgt.targid,"TOO Trigger Criteria must be specified."));
          
      }
    }

    if (hasRequired(cpstgt.tgt.group_id)) 
      validate_GroupConstraint(cpstgt);

    if (hasRequired(cpstgt.tgt.atg_group_name)) 
      validate_TOOAlternates(cpstgt);
    }
    catch (Exception exc) {
      logger.error("VALIDATETGT",exc);
      errorList.add(0,new GUIMessage(CPSConstants.ERROR_TYPE,"","UNEXPECTED ERROR, please contact the HelpDesk"));
    }
  }


  // ----------------------------------------------------------------------------------
  // Group Constraint: need to verify that time requested is <= interval
  // and interval needs to be same for every target  (by group_id)
  private void validate_GroupConstraint(CPSTarget cpstgt)
  {   
    // grpCnt = # of tgts in this group (must be > 1)
    // grpTime = total time requested by targets in this group
    // grpInterval = interval by group
    logger.debug("Target " + cpstgt.tgt.targid + "  group=" + cpstgt.tgt.group_id + "------");
    if (hasRequired(cpstgt.tgt.group_id)) {
      String key = cpstgt.tgt.group_id;
      Double gval = grpInterval.get(key);
      Double tval = grpTime.get(key);
      if (cpstgt.tgt.observations != null && cpstgt.tgt.observations.size() > 0) {
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.CONSTRAINT_PAGE,
             cpstgt.tgt.targ_num,cpstgt.tgt.targid,
             "Groups are not allowed for targets with monitor/followup observations."));
      }
      // No Group + Split Interval.
      // Don't need to check monitor split_interval since above forbids it
      if (cpstgt.tgt.split_interval != null) {
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.CONSTRAINT_PAGE,
             cpstgt.tgt.targ_num,cpstgt.tgt.targid,
             "Groups are not allowed for targets with split interval constraint."));
      }
      //if (grpCnt.get(key) < 2) 
        //errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.CONSTRAINT_PAGE,
		//cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Group '" + key + "':  More than 1 target must be specfied for Group Constraints"));
      if (gval == null ||  gval.compareTo(cpstgt.tgt.group_interval) != 0)
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.CONSTRAINT_PAGE,
             cpstgt.tgt.targ_num,cpstgt.tgt.targid,
             "Group '" + key + "':  Group Interval of " + String.format("%1$.4f",cpstgt.tgt.group_interval) + " does not match interval of " + CPS.getDouble(gval,0) ));
      if ( tval == null || tval.compareTo((cpstgt.tgt.group_interval.doubleValue() * 86.4)) > 0)
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.CONSTRAINT_PAGE,
             cpstgt.tgt.targ_num,cpstgt.tgt.targid,
	     "Group '" + key + "': Total observing time of " + tval + " for the group is greater than the specified 'Group Interval' of " + String.format("%1$.4f",cpstgt.tgt.group_interval)));
    }
  }

  // ----------------------------------------------------------------------------------
  // Alternates:  requested count should be same (by atg_group_name)
  // and requested count < number of targets in atg_group_name
  private void validate_TOOAlternates(CPSTarget cpstgt)
  {
    if (hasRequired(cpstgt.tgt.atg_group_name))  {
      // the requested count for an alternate target group must be
      // less than the number of targets in that group
      // and must be the same for all targets requesting that group
      //atgCnt = number of targets found for this alternate group
      //atgReq = requested count found for this alternate group
      String key = cpstgt.tgt.atg_group_name;
      Integer  areq = (Integer)atgReq.get(key);
      if (areq.intValue() != cpstgt.tgt.atg_req_count.intValue())
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.TOO_PAGE,
          cpstgt.tgt.targ_num,cpstgt.tgt.targid,"Alternate Target Group Requested Count does not match " + atgReq.get(key)));
      if (atgCnt.get(key) <= atgReq.get(key)) 
        errorList.add(new GUIMessage(CPSConstants.ERROR_TYPE,CPSConstants.TOO_PAGE,
          cpstgt.tgt.targ_num,cpstgt.tgt.targid,"The requested count for an Alternate Target Group must be less than the number of targets in the group. Current number of targets in group \'" + key + "\' is " + atgCnt.get(key) + " Requested count is " +  atgReq.get(key) ));

    }
  }


  // ------------------------------------------------------------------
  // build hash of target info for alternate targets and groups
  // calculate time with slew tax;
  // ------------------------------------------------------------------
  private void buildTargetInfo(CPSTargetList tgtList)
  {

    // add slewtax into total time 
    // get sum of times for future cycle
    // get count of time critical
    // get group and alternate target info
    for (int ii=0;ii< tgtList.size();ii++) {
      CPSTarget cpstgt = tgtList.get(ii);
      Integer cnt = Integer.valueOf(0);
      Double dval=0.0;


      // get target number and exposure time which will be used
      // when validating the TOO followups
      tgtHash.put(Integer.valueOf(cpstgt.tgt.targ_num),cpstgt.tgt.prop_exposure_time);

      //ttime += cpstgt.slewTax;
      ttime_slew += cpstgt.slewTax;
      if (hasRequired(cpstgt.tgt.est_time_cycle_n1,false))
        ttime_1 += cpstgt.tgt.est_time_cycle_n1;
      if (hasRequired(cpstgt.tgt.est_time_cycle_n2,false))
        ttime_2 += cpstgt.tgt.est_time_cycle_n2;
      if (cpstgt.tgt.time_critical == YesNoPreferred.YES) 
        tc_cnt +=1;
       
      if (hasRequired(cpstgt.tgt.group_id)) {
        // add to count to verify >1 for each group
        if (grpCnt.containsKey(cpstgt.tgt.group_id)) 
          cnt =grpCnt.get(cpstgt.tgt.group_id);
        cnt = cnt+1;
        grpCnt.put(cpstgt.tgt.group_id,cnt);

        // add exposure time  so we can verify interval later
        if (grpTime.containsKey(cpstgt.tgt.group_id))  
          dval =grpTime.get(cpstgt.tgt.group_id);
  
        if (cpstgt.tgt.prop_exposure_time != null) {
          dval += cpstgt.tgt.prop_exposure_time;
          grpTime.put(cpstgt.tgt.group_id,dval);
        }
        // check Interval is same
        dval=0.0;
        if (grpInterval.containsKey(cpstgt.tgt.group_id))  
          dval = grpInterval.get(cpstgt.tgt.group_id);
        else
          dval = cpstgt.tgt.group_interval;

        if (dval.compareTo(cpstgt.tgt.group_interval) ==0)
          grpInterval.put(cpstgt.tgt.group_id,dval);
      }

      if (hasRequired(cpstgt.tgt.atg_group_name)) {
        Integer ival;
        // add 1 to number requesting this
        if (atgCnt.containsKey(cpstgt.tgt.atg_group_name)) 
          cnt =atgCnt.get(cpstgt.tgt.atg_group_name);
        cnt = cnt+1;
        atgCnt.put(cpstgt.tgt.atg_group_name,cnt);

        if (atgReq.containsKey(cpstgt.tgt.atg_group_name)) 
          ival = atgReq.get(cpstgt.tgt.atg_group_name);
        else
          ival = cpstgt.tgt.atg_req_count;
        logger.debug("ATG" + cpstgt.tgt.atg_group_name + " ival=" + ival);
        if (ival.compareTo(cpstgt.tgt.atg_req_count) == 0) {
          logger.debug("add atgReq " +  cpstgt.tgt.atg_group_name );
          atgReq.put(cpstgt.tgt.atg_group_name,ival);
        }
      }
    }  // end target loop
  } 

  // ----------------------------------------------------------------------------------
  // ----------------------------------------------------------------------------------
  public int hasErrors()
  {
    int errcnt=0;
    for (int ii=0;ii<errorList.size();ii++) {
      if (errorList.get(ii).isError()) errcnt += 1;
    }
    return errcnt;
  }
  private boolean hasRequired(String fld)
  {
    if (fld == null || fld.trim().length() < 1)
       return false;
    else 
       return true;
  }

  private boolean hasRequired(Boolean fld)
  {
    if (fld == null )
       return false;
    else 
       return true;
  }

  private boolean hasRequired(Integer fld,boolean allow0)
  {
    if (fld == null || (!allow0 && fld.intValue() == 0))
       return false;
    else 
       return true;
  }
  private boolean hasRequired(Double fld,boolean allow0)
  {
    if (fld == null || (!allow0 && fld.doubleValue() == 0))
       return false;
    else 
       return true;
  }

} // end of class
