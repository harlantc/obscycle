package info;
/*
  Copyrights:
 
  Copyright (c) 2014 Smithsonian Astrophysical Observatory
 
  Permission to use, copy, modify, distribute, and  sell  this
  software  and  its  documentation  for any purpose is hereby
  granted without  fee,  provided  that  the  above  copyright
  notice  appear  in  all  copies and that both that copyright
  notice and this permission notice appear in supporting docu-
  mentation,  and  that  the  name  of the  Smithsonian Astro-
  physical Observatory not be used in advertising or publicity
  pertaining to distribution of the software without specific,
  written  prior  permission.   The Smithsonian  Astrophysical
  Observatory makes no representations about  the  suitability
  of  this  software for any purpose.  It is provided  "as is"
  without express or implied warranty.
  THE  SMITHSONIAN  INSTITUTION  AND  THE  SMITHSONIAN  ASTRO-
  PHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES  WITH REGARD TO
  THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANT-
  ABILITY AND FITNESS,  IN  NO  EVENT  SHALL  THE  SMITHSONIAN
  INSTITUTION AND/OR THE SMITHSONIAN ASTROPHYSICAL OBSERVATORY
  BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES
  OR ANY DAMAGES  WHATSOEVER  RESULTING FROM LOSS OF USE, DATA
  OR PROFITS,  WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
  OTHER TORTIOUS ACTION,  ARISING OUT OF OR IN CONNECTION WITH
  THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/


import java.lang.String;
import java.lang.Integer;
import java.lang.Double;
import java.util.regex.*;
import java.util.Vector;
import java.util.List;
import java.util.LinkedList;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.util.Date;
import java.text.Format;
import java.text.NumberFormat;
import java.text.BreakIterator;
import ascds.LogMessage;
import ascds.Coordinate;
import ascds.FileUtils;
import org.apache.commons.lang3.*;


/**
  * This class contains all the information related to an observation.
  * needed for triggering/managing a TOO
  */

public class PropTarget {
    private Integer proposal_id;
    private String  proposalNumber;
    private Integer targid;
    private Integer targnum;
    private String  targetName ;
    private String  ssobjectName ;
    private String  targetDescription ;
    private String  photometry;
    private Double  ra;
    private Double  dec;
    private String  instrument;
    private String  grating;
    private String  hrcTiming;
    private AcisParam acisparam;
    private Double  expTime;
    private Double  approvedTime;
    private Double  initialTime;
    private Double  vMagnitude;
    private Double  estCntRate;
    private Double  forderCntRate;
    private Double  totalCntRate;
    private String  extendedSrc;
    private Double  yDetOffset;
    private Double  zDetOffset;
    private Double  simTransOffset;
    private String  gridName;
    private Integer gridPointings;
    private Integer gridApproved;
    private Double  gridRadius;
    private String  status;
    private String  multitelescope;
    private String  observatories;
    private Double  multitelescope_interval;
    private String  uninterrupt;
    private String  pointing_constraint;
    private String  monitor_flag;
    private String  group;
    private String  group_id;
    private Double  group_interval;
    private String  time_critical;
    private String  phase_flag;
    private Double  phase_period;
    private Double  phase_epoch;
    private Double  phase_start;
    private Double  phase_start_margin;
    private Double  phase_end;
    private Double  phase_end_margin;
    private String  remarksFlag;
    private String  remarks;
    private String  responseWindow;
    private Double  responseStart;
    private Double  responseStop;
    private String  triggerTarget;
    private String  trigCriteria;
    private String  fupRemarks;
    private String  fastProc;
    private String  fastProcStatus;
    private String  fastProcComment;
    private Integer ocat_propid;  
    private Vector<RollReq> rollreq; 
    private Vector<TimeReq> timereq; 
    private Vector<AcisWin> aciswin; 

    // editable ?
    private String  raString;
    private String  decString;
  
    private Vector<DDTFollowup> followups;


    private boolean isValidCoords;
    private boolean isEditable;

    private FormatUtils fu;
    private int  padSize;

    

    /**
      * Constructor
      */
    public PropTarget() {
	init();
    }

    private void init() {
        proposal_id = new Integer(0);
        proposalNumber = new String("");
        targid = new Integer(0);
        targnum = new Integer(0);
        targetName = new String("");
        ssobjectName = new String("");
        targetDescription = new String("");
        ra = TriggerTooConstants.EMPTY_VALUE;
        dec = TriggerTooConstants.EMPTY_VALUE;
	instrument = new String("");
        grating = new String("");
        hrcTiming = new String("");
        acisparam = new AcisParam();
        expTime = new Double(0.0);
        approvedTime = TriggerTooConstants.EMPTY_VALUE;
        initialTime = TriggerTooConstants.EMPTY_VALUE;
        status = new String("");
        vMagnitude = TriggerTooConstants.EMPTY_VALUE;
        yDetOffset = TriggerTooConstants.EMPTY_VALUE;
        zDetOffset = TriggerTooConstants.EMPTY_VALUE;
        simTransOffset = TriggerTooConstants.EMPTY_VALUE;
        estCntRate = TriggerTooConstants.EMPTY_VALUE;
        forderCntRate = TriggerTooConstants.EMPTY_VALUE;
        totalCntRate = TriggerTooConstants.EMPTY_VALUE;
        extendedSrc = new String("");
        gridName = new String("");
        gridPointings = TriggerTooConstants.EMPTY_INT;
        gridApproved = TriggerTooConstants.EMPTY_INT;
        gridRadius = TriggerTooConstants.EMPTY_VALUE;
        multitelescope = new String("");
        observatories = new String("");
        multitelescope_interval = new Double(0.0);
        uninterrupt = new String("");
        pointing_constraint = new String("");
        monitor_flag = new String("");
        group_id = new String("");
        group_interval = new Double(0.0);
        time_critical = new String("");
        phase_flag = new String("");
        phase_period = TriggerTooConstants.EMPTY_VALUE;
        phase_epoch = TriggerTooConstants.EMPTY_VALUE;
        phase_start = TriggerTooConstants.EMPTY_VALUE;
        phase_start_margin = TriggerTooConstants.EMPTY_VALUE;
        phase_end = TriggerTooConstants.EMPTY_VALUE;
        phase_end_margin = TriggerTooConstants.EMPTY_VALUE;
        remarks = new String("");
        remarksFlag = new String("");
        rollreq = new Vector<RollReq>();
        timereq = new Vector<TimeReq>();
        aciswin = new Vector<AcisWin>();
        responseWindow = new String("");
        responseStart = TriggerTooConstants.EMPTY_VALUE;
        responseStop = TriggerTooConstants.EMPTY_VALUE;
        ocat_propid = new Integer(0);
        triggerTarget = new String("");
        trigCriteria = new String("");
        fupRemarks = new String("");
        fastProc = new String("");
        fastProcStatus = new String("");
        fastProcComment = new String("");

        raString = new String("");
        decString = new String("");


        followups = new Vector<DDTFollowup>();
        isValidCoords = false;
        padSize=30;

 
    }

    /**
      * copy  - copy the PropTarget
      * @param inputObs  input PropTarget
      */
    public void copy(PropTarget inputObs) {
        proposal_id = inputObs.getProposalID();
	proposalNumber = inputObs.getProposalNumber();
        targid = inputObs.getTargID();
        targnum = inputObs.getTargetNumber();
        targetName = inputObs.getOrigTargetName();
        ssobjectName = inputObs.getSSObjectName();
	targetDescription = inputObs.getTargetDescription();
        ra = inputObs.getRA();
        dec = inputObs.getDec();
        instrument = inputObs.getInstrument();
        grating = inputObs.getGrating();
        hrcTiming = inputObs.getHRCTiming();
        acisparam = inputObs.getAcisParam();
        expTime = inputObs.getExpTime();
        approvedTime = inputObs.getApprovedTime();
        initialTime = inputObs.getInitialTime();
	status = inputObs.getStatus();
	photometry = inputObs.getPhotometry();
	vMagnitude = inputObs.getVMagnitude();
	yDetOffset = inputObs.getYDetOffset();
	zDetOffset = inputObs.getZDetOffset();
	simTransOffset = inputObs.getSimTransOffset();
	estCntRate = inputObs.getEstCntRate();
	forderCntRate = inputObs.getForderCntRate();
	totalCntRate = inputObs.getTotalCntRate();
	extendedSrc = inputObs.getExtendedSrc();
	gridName = inputObs.getGridName();
	gridPointings = inputObs.getGridPointings();
	gridApproved = inputObs.getGridPointings();
	gridRadius = inputObs.getGridRadius();
        multitelescope = inputObs.getMultitelescope();
        observatories = inputObs.getObservatories();
        multitelescope_interval = inputObs.getMultitelescopeInterval();
        uninterrupt =  inputObs.getUninterrupt();
        pointing_constraint =  inputObs.getPointingConstraint();
        monitor_flag =  inputObs.getMonitorFlag();
        group_id = inputObs.getGroupID();
        group_interval = inputObs.getGroupInterval();
        time_critical = inputObs.getTimeCritical();
        phase_flag = inputObs.getPhaseFlag();
        phase_period = inputObs.getPhasePeriod();
        phase_epoch = inputObs.getPhaseEpoch();
        phase_start = inputObs.getPhaseStart();
        phase_start_margin = inputObs.getPhaseStartMargin();
        phase_end = inputObs.getPhaseEnd();
        phase_end_margin = inputObs.getPhaseEndMargin();
        remarks = inputObs.getRemarks();
        remarksFlag = inputObs.getRemarksFlag();
        rollreq = inputObs.getRollReq();
        timereq = inputObs.getTimeReq();
        aciswin = inputObs.getAcisWin();
        triggerTarget = inputObs.getTriggerTarget();
        trigCriteria = inputObs.getTrigCriteria();
        fupRemarks = inputObs.getFupRemarks();
        fastProc = inputObs.getFastProc();
        fastProcStatus = inputObs.getFastProcStatus();
        fastProcComment = inputObs.getFastProcComment();

        responseWindow = inputObs.getResponseWindow();
        responseStart = inputObs.getResponseStart();
        responseStop = inputObs.getResponseStop();
        ocat_propid = inputObs.getOcatPropid();

        raString = inputObs.getRAString();
        decString = inputObs.getDecString();


        followups = inputObs.getFollowups();

        isValidCoords = inputObs.isValidCoords();
        

    }


    //  Set routines
    public void setProposalID(Integer inputValue) {
	proposal_id = inputValue;
    }
    public void setProposalID(int inputValue) {
       proposal_id = new Integer(inputValue);
    }
    public void setProposalNumber(String inputValue) {
      if (inputValue != null) {
	proposalNumber = inputValue.trim();
      } else {
	proposalNumber = inputValue;
      }
    }
    public void setTargID(Integer inputValue) {
	targid = inputValue;
    }
    public void setTargID(int inputValue) {
       targid = new Integer(inputValue);
    }
    public void setTargetNumber(Integer inputValue) {
	targnum = inputValue;
    }
    public void setTargetNumber(int inputValue) {
       targnum = new Integer(inputValue);
    }
    public void setTargetName(String inputValue) {
      if (inputValue != null) {
	targetName = inputValue.trim();
      } else {
	targetName = inputValue;
      }
    }
    public void setTargetDescription(String inputValue) {
      if (inputValue != null) {
	targetDescription = inputValue.trim();
      } else {
	targetDescription = inputValue;
      }
    }
    public void setSSObjectName(String inputValue) {
      if (inputValue != null) {
	ssobjectName = inputValue.trim();
      } else {
	ssobjectName = inputValue;
      }
    }
    public void setRA(Double inputValue) {
	ra = inputValue;
    }
    public void setRA(double inputValue) {
	ra = new Double(inputValue);
    }
    public void setDec(Double inputValue) {
	dec = inputValue;
    }
    public void setDec(double inputValue) {
	dec = new Double(inputValue);
    }
    public void setInstrument(String inputValue) {
      if (inputValue != null) {
	instrument = inputValue.trim();
      } else {
	instrument = inputValue;
      }
    }
    public void setGrating(String inputValue) {
      if (inputValue != null) {
	grating = inputValue.trim();
      } else {
	grating = inputValue;
      }
    }
    public void setHRCTiming(String inputValue) {
      if (inputValue != null) {
	hrcTiming = inputValue.trim();
      } else {
	hrcTiming = inputValue;
      }
    }
    public void setPhotometry(String inputValue) {
      if (inputValue != null) {
	photometry = inputValue.trim();
      } else {
	photometry = inputValue;
      }
    }
    public void setAcisParam(AcisParam inputValue) {
	acisparam = inputValue;
    }
    public void setExpTime(Double inputValue) {
	expTime = inputValue;
    }
    public void setExpTime(double inputValue) {
	expTime = new Double(inputValue);
    }
    public void setApprovedTime(Double inputValue) {
	approvedTime = inputValue;
    }
    public void setApprovedTime(double inputValue) {
	approvedTime = new Double(inputValue);
    }
    public void setInitialTime(Double inputValue) {
	initialTime = inputValue;
    }
    public void setInitialTime(double inputValue) {
	initialTime = new Double(inputValue);
    }
    public void setStatus(String inputValue) {
      if (inputValue != null) {
	status = inputValue.trim();
      } else {
	status = "";
      }
    }
    public void setYDetOffset(Double inputValue) {
	yDetOffset = inputValue;
    }
    public void setYDetOffset(double inputValue) {
	yDetOffset = new Double(inputValue);
    }
    public void setZDetOffset(Double inputValue) {
	zDetOffset = inputValue;
    }
    public void setZDetOffset(double inputValue) {
	zDetOffset = new Double(inputValue);
    }
    public void setSimTransOffset(Double inputValue) {
	simTransOffset = inputValue;
    }
    public void setSimTransOffset(double inputValue) {
	simTransOffset = new Double(inputValue);
    }
    public void setVMagnitude(Double inputValue) {
	vMagnitude = inputValue;
    }
    public void setVMagnitude(double inputValue) {
	vMagnitude = new Double(inputValue);
    }
    public void setEstCntRate(Double inputValue) {
	estCntRate = inputValue;
    }
    public void setEstCntRate(double inputValue) {
	estCntRate = new Double(inputValue);
    }
    public void setFOrderCntRate(Double inputValue) {
	forderCntRate = inputValue;
    }
    public void setFOrderCntRate(double inputValue) {
	forderCntRate = new Double(inputValue);
    }
    public void setTotalCntRate(Double inputValue) {
	totalCntRate = inputValue;
    }
    public void setTotalCntRate(double inputValue) {
	totalCntRate = new Double(inputValue);
    }
    public void setExtendedSrc(String inputValue) {
      if (inputValue != null) {
	extendedSrc = inputValue.trim();
      }
      else {
        extendedSrc = inputValue;
      }
    }
    public void setGridName(String inputValue) {
      if (inputValue != null) {
	gridName = inputValue.trim();
      }
      else {
        gridName = inputValue;
      }
    }
    public void setGridPointings(Integer inputValue) {
	gridPointings = inputValue;
    }
    public void setGridPointings(int inputValue) {
	gridPointings = new Integer(inputValue);
    }
    public void setGridApproved(Integer inputValue) {
	gridApproved = inputValue;
    }
    public void setGridApproved(int inputValue) {
	gridApproved = new Integer(inputValue);
    }
    public void setGridRadius(Double inputValue) {
	gridRadius = inputValue;
    }
    public void setGridRadius(double inputValue) {
	gridRadius = new Double(inputValue);
    }
    public void setMultitelescope(String inputValue) {
      if (inputValue != null) {
	multitelescope = inputValue.trim();
      }
      else {
        multitelescope = inputValue;
      }
    }
    public void setObservatories(String inputValue) {
      if (inputValue != null) {
	observatories = inputValue.trim();
      }
      else {
        observatories = inputValue;
      }
    }
    public void setMultitelescopeInterval(Double inputValue) {
        multitelescope_interval = inputValue;
    }
    public void setMultitelescopeInterval(double inputValue) {
        multitelescope_interval = new Double(inputValue);
    }

    public void setMonitorFlag(String inputValue) {
      if (inputValue != null) {
	monitor_flag = inputValue.trim();
      }
      else {
        monitor_flag = inputValue;
      }
    }
    public void setPointingConstraint(String inputValue) {
      if (inputValue != null) {
	pointing_constraint = inputValue.trim();
      }
      else {
        pointing_constraint = "";
      }
    }
    public void setUninterrupt(String inputValue) {
      if (inputValue != null) {
	uninterrupt = inputValue.trim();
      }
      else {
        uninterrupt = inputValue;
      }
    }
    public void setGroupID(String inputValue) {
      if (inputValue != null) {
	group_id = inputValue.trim();
      }
      else {
        group_id = inputValue;
      }
    }
    public void setGroupInterval(Double inputValue) {
        group_interval = inputValue;
    }
    public void setGroupInterval(double inputValue) {
        group_interval = new Double(inputValue);
    }
    public void setTimeCritical(String inputValue) {
      if (inputValue != null) {
	time_critical = inputValue.trim();
      }
      else {
        time_critical = inputValue;
      }
    }
    public void setPhaseFlag(String inputValue) {
      if (inputValue != null) {
	phase_flag = inputValue.trim();
      }
      else {
        phase_flag = inputValue;
      }
    }
    public void setPhasePeriod(Double inputValue) {
        phase_period = inputValue;
    }
    public void setPhasePeriod(double inputValue) {
        phase_period = new Double(inputValue);
    }
    public void setPhaseEpoch(Double inputValue) {
        phase_epoch = inputValue;
    }
    public void setPhaseEpoch(double inputValue) {
        phase_epoch = new Double(inputValue);
    }
    public void setPhaseStart(Double inputValue) {
        phase_start = inputValue;
    }
    public void setPhaseStart(double inputValue) {
        phase_start = new Double(inputValue);
    }
    public void setPhaseStartMargin(Double inputValue) {
        phase_start_margin = inputValue;
    }
    public void setPhaseStartMargin(double inputValue) {
        phase_start_margin = new Double(inputValue);
    }
    public void setPhaseEnd(Double inputValue) {
        phase_end = inputValue;
    }
    public void setPhaseEnd(double inputValue) {
        phase_end = new Double(inputValue);
    }
    public void setPhaseEndMargin(Double inputValue) {
        phase_end_margin = inputValue;
    }
    public void setPhaseEndMargin(double inputValue) {
        phase_end_margin = new Double(inputValue);
    }
    public void setRemarks(String inputValue) {
      if (inputValue != null) {
	remarks = inputValue.trim();
      }
      else {
        remarks = inputValue;
      }
    }
    public void setRemarksFlag(String inputValue) {
      if (inputValue != null) {
	remarksFlag = inputValue.trim();
      }
      else {
        remarksFlag = inputValue;
      }
    }
    public void setTriggerTarget(String inputValue) {
      if (inputValue != null) {
	triggerTarget = inputValue.trim();
      }
      else {
        triggerTarget = inputValue;
      }
    }
    public void setTrigCriteria(String inputValue) {
      if (inputValue != null) {
	trigCriteria = inputValue.trim();
      }
      else {
        trigCriteria = inputValue;
      }
    }
    public void setFupRemarks(String inputValue) {
      if (inputValue != null) {
	fupRemarks = inputValue.trim();
      }
      else {
        fupRemarks = inputValue;
      }
    }
    public void setFastProc(String inputValue) {
      if (inputValue != null) {
	fastProc = inputValue.trim();
      }
      else {
        fastProc = "";
      }
    }
    public void setFastProcStatus(String inputValue) {
      if (inputValue != null) {
	fastProcStatus = inputValue.trim();
      }
      else {
        fastProcStatus = "";
      }
    }
    public void setFastProcComment(String inputValue) {
      if (inputValue != null) {
	fastProcComment = inputValue.trim();
      }
      else {
        fastProcComment = "";
      }
    }
    public void setRollReq(Vector<RollReq> rr) {
       rollreq.clear();
       for (int ii=0;ii<rr.size();ii++) {
         rollreq.add(rr.get(ii));
       }
    }
    public void setTimeReq(Vector<TimeReq> tt) {
       timereq.clear();
       for (int ii=0;ii<tt.size();ii++) {
         timereq.add(tt.get(ii));
       }
    }
    public void setAcisWin(Vector<AcisWin> aw) {
       aciswin.clear();
       for (int ii=0;ii<aw.size();ii++) {
         aciswin.add(aw.get(ii));
       }
    }

    public void setResponseWindow(String inputValue) {
      if (inputValue != null) {
	responseWindow = inputValue.trim();
      }
      else {
        responseWindow = "";
      }
    }
    public void setResponseStart(Double inputValue) {
       responseStart = inputValue;
    }
    public void setResponseStart(double inputValue) {
       responseStart = new Double(inputValue);
    }
    public void setResponseStop(Double inputValue) {
       responseStop = inputValue;
    }
    public void setResponseStop(double inputValue) {
       responseStop = new Double(inputValue);
    }
    public void setOcatPropid(Integer inputValue) {
	ocat_propid = inputValue;
    }
    public void setOcatPropid(int inputValue) {
       ocat_propid = new Integer(inputValue);
    }
    public void setFollowup(Vector<DDTFollowup> fup) {
       followups.clear();
       for (int ff=0;ff<fup.size();ff++) {
         followups.add(fup.get(ff));
       }
    }


    /**
      * set initial coordinate values and determine if target is editable
      */
    public void setInitCoords() {
       Coordinate coords;

       isValidCoords = true;
       if (ra != TriggerTooConstants.EMPTY_VALUE && dec != TriggerTooConstants.EMPTY_VALUE ) {
         try {
           coords = new Coordinate(ra.toString(),dec.toString(),"J2000");
           ra  = new Double(coords.getLon());
           dec = new Double(coords.getLat());
           raString  = new String(coords.getSexagesimalLon());
           decString = new String(coords.getSexagesimalLat());
         }
         catch (Exception exc) {
           exc.printStackTrace();
           isValidCoords = false;
         }
       }
       else {
         raString= new String("");
         decString= new String("");
       }
       setIsEditable();
    }


    /**
      * setCoords based on input ra and dec string
      * @param raStr  right ascension
      * @param decStr declination
      */
    public void setCoords(String raStr, String decStr) 
    {
       Coordinate coords;

       LogMessage.println("Setting COORDS: " + raStr  + "," + decStr + " for targid "  + targid.toString());
       isValidCoords = true;
       raString = raStr;
       decString = decStr;
       
       if (raStr  != null && raStr.length() > 0 && 
           decStr != null && decStr.length() > 0 ) {
         try {
           coords = new Coordinate(raStr,decStr,"J2000");
           ra = new Double(coords.getLon());
           dec = new Double(coords.getLat());
           raString  = new String(coords.getSexagesimalLon());
           decString = new String(coords.getSexagesimalLat());
         }
         catch (Exception exc) {
           LogMessage.println(exc.toString());
           isValidCoords = false;
         }
       }
       else {
         ra= new Double(0.0);
         dec = new Double(0.0);
         raString = new String("");
         decString = new String("");
       }
    }

    // Get routines
    public Integer getProposalID() {
	return proposal_id;
    }
    public String getProposalNumber() {
	return proposalNumber;
    }
    public Integer getTargID() {
	return targid;
    }
    public Integer getTargetNumber() {
	return targnum;
    }
    public String getTargetName() {
        if (targetName == null || targetName.length() <= 0) {
           return ssobjectName;
        }
        else {
           return targetName;
       }
    }
    public String getOrigTargetName() {
       return targetName;
    }
    public String getSSObjectName() {
       return ssobjectName;
    }
    public String getTargetDescription() {
	return targetDescription;
    }
    public Double getRA() {
	return ra;
    }
    public String getRAString() {
	return raString;
    }
    public Double getDec() {
	return dec;
    }
    public String getDecString() {
	return decString;
    }

    public String getInstrument() {
	return instrument;
    }
    public String getGrating() {
	return grating;
    }
    public String getHRCTiming() {
	return hrcTiming;
    }
    public AcisParam getAcisParam() {
	return acisparam;
    }
    public Double getExpTime() {
	return expTime;
    }
    public Double getApprovedTime() {
	return approvedTime;
    }
    public Double getInitialTime() {
	return initialTime;
    }
    public String getStatus() {
	return status;
    }
    public Double getYDetOffset() {
	return yDetOffset;
    }
    public Double getZDetOffset() {
	return zDetOffset;
    }
    public Double getSimTransOffset() {
	return simTransOffset;
    }
    public String getPhotometry() {
	return photometry;
    }
    public Double getVMagnitude() {
	return vMagnitude;
    }
    public Double getEstCntRate() {
	return estCntRate;
    }
    public Double getForderCntRate() {
	return forderCntRate;
    }
    public Double getTotalCntRate() {
	return totalCntRate;
    }
    public String getExtendedSrc() {
	return extendedSrc;
    }
    public String getGridName() {
	return gridName;
    }
    public Integer getGridPointings() {
	return gridPointings;
    }
    public Integer getGridApproved() {
	return gridApproved;
    }
    public Double getGridRadius() {
	return gridRadius;
    }
    public String getMultitelescope() {
	return multitelescope;
    }
    public String getObservatories() {
	return observatories;
    }
    public Double getMultitelescopeInterval() {
	return multitelescope_interval;
    }
    public String getUninterrupt() {
	return uninterrupt;
    }
    public String getPointingConstraint() {
	return pointing_constraint;
    }
    public String getMonitorFlag() {
	return monitor_flag;
    }
    public String getGroupID() {
	return group_id;
    }
    public Double getGroupInterval() {
	return group_interval;
    }
    public String getPhaseFlag() {
	return phase_flag;
    }
    public Double getPhasePeriod() {
	return phase_period;
    }
    public Double getPhaseEpoch() {
	return phase_epoch;
    }
    public Double getPhaseStart() {
	return phase_start;
    }
    public Double getPhaseStartMargin() {
	return phase_start_margin;
    }
    public Double getPhaseEnd() {
	return phase_end;
    }
    public Double getPhaseEndMargin() {
	return phase_end_margin;
    }
    public String getTimeCritical() {
	return time_critical;
    }
    public String getRemarksFlag() {
	return remarksFlag;
    }
    public String getRemarks() {
	return remarks;
    }
    public String getTriggerTarget() {
	return triggerTarget;
    }
    public String getTrigCriteria() {
	return trigCriteria;
    }
    public String getFupRemarks() {
	return fupRemarks;
    }
    public String getFastProc() {
	return fastProc;
    }
    public String getFastProcStatus() {
	return fastProcStatus;
    }
    public String getFastProcComment() {
	return fastProcComment;
    }
    public Vector<RollReq> getRollReq() {
        return rollreq;
    }
    public Vector<TimeReq> getTimeReq() {
        return timereq;
    }
    public Vector<AcisWin> getAcisWin() {
        return aciswin;
    }
    public String getResponseWindow() {
	return responseWindow;
    }
    public Double getResponseStart() {
	return responseStart;
    }
    public Double getResponseStop() {
	return responseStop;
    }
    public Integer getOcatPropid() {
	return ocat_propid;
    }
    public Vector<DDTFollowup> getFollowups() {
        return followups;
    }
    public Coordinate getCoords() {
       Coordinate coords = null;

       if (ra.doubleValue() != TriggerTooConstants.EMPTY_VALUE) {
         coords = new Coordinate(ra.doubleValue(),dec.doubleValue());
       }

       return coords;
    }

  /**
   * Determine if target information is editable.
   * Current RA/DEC must be 0.0
   * @return boolean true if ra/dec can be edited
   */
  public boolean isEditable()
  {
    return isEditable;
  }

  public void setIsEditable()
  {
      if (ocat_propid > 0) {
         isEditable=false;
      }
      isEditable= true;

  }

  /**
    * Check if observation has been completed
    * @return boolean true if observation is complete
    */
  public boolean isCompleted()
  {
     boolean returnValue = false;
  
     if (ocat_propid > 0)  {
        returnValue = true;
     }
     return returnValue;
  }
 

  /**
    * did coordinates parse correctly
    * @return boolean true if valid coordinates
    */
  public boolean isValidCoords()
  {
    return isValidCoords;
  }


  /** 
   * writeObservation - write printer friendly format
   * @param filename output filename
   * @param isApproved  true if write approved exposure time too
   * @return boolean true if no errors
   */
  public boolean writeObservation(String filename,boolean isApproved)
  {
    boolean retval = true;

    File outputFile = new File(filename);
    // if file exists, rename it to a .bak 
    if(outputFile.exists()) {
      StringBuffer newFilePath = new StringBuffer(filename);
      newFilePath.append(".bak");
      File newFile = new File(newFilePath.toString());
      if (! outputFile.renameTo(newFile)) {
        LogMessage.println("Cannot rename file: " + filename + " to " + newFilePath);
        retval = false;
      }
      outputFile = new File(filename);
    } 
    if (retval) {
      //Create the blank file
      try {
        boolean createdFile = outputFile.createNewFile();
        if(!createdFile) {
          LogMessage.println("Error in creating new file: " + filename);
          retval = false;
        }
      } 
      catch(Exception exc) {
        LogMessage.println("Caught exception in creating a new file for writing DDT Target data");
        LogMessage.println(exc.getMessage());
        retval = false;
      }
    }

    try {
       FileUtils.setPermissions(outputFile,"660"); 
       FileWriter outputFW = new FileWriter(filename.toString());
       PrintWriter outputPW = new PrintWriter(outputFW);
       Date fileDate = new Date(outputFile.lastModified());
       outputPW.print(StringUtils.rightPad("Date",padSize));
       outputPW.print(" = ");
       outputPW.println(fileDate.toString());
       retval = writeOutput(outputPW,isApproved);
       outputPW.close();
       outputFW.close();
     }
     catch (Exception exc) {
       LogMessage.println("PropTarget: Unable to write printer friendly version");
       LogMessage.println(exc.getMessage());
       retval = false;
     }
     
     return retval;
  }


  /**
   * write observation information in printer friendly format 
   * @param outputPW output buffer
   * @param isApproved  true if write approved exposure time too
   * @return boolean true if no errors
   */
  public boolean writeOutput(PrintWriter outputPW,boolean isApproved)
  {
    boolean retval=true;
    String[] strArray;

    try {
       NumberFormat nf = NumberFormat.getInstance();
       nf.setMaximumFractionDigits(6);
       nf.setMinimumFractionDigits(6);

       outputPW.print(StringUtils.rightPad(TriggerTooConstants.PROPNUM,padSize));
       outputPW.print(" = ");
       outputPW.println(this.getProposalNumber());
       outputPW.print(StringUtils.rightPad("Target Number",padSize));
       outputPW.print(" = ");
       outputPW.println(this.getTargetNumber().toString());
       outputPW.print(StringUtils.rightPad(TriggerTooConstants.TARGETNAME,padSize));
       outputPW.print(" = ");
       if (this.getTargetName() != null) {
         outputPW.println(this.getTargetName());
       } else {
         outputPW.println("");
       }
       outputPW.println(StringUtils.rightPad(TriggerTooConstants.COORD,padSize));
       outputPW.print(StringUtils.rightPad(TriggerTooConstants.RA,padSize));
       outputPW.print(" =  ");
       outputPW.print(this.getRAString());
       outputPW.print("      ");
       outputPW.println(nf.format(ra.doubleValue()));
       outputPW.print(StringUtils.rightPad(TriggerTooConstants.DEC,padSize));
       outputPW.print(" = ");
       outputPW.print(this.getDecString());
       outputPW.print("      ");
       outputPW.println(nf.format(dec.doubleValue()));
       outputPW.print(StringUtils.rightPad(TriggerTooConstants.INSTRUMENT,padSize));
       outputPW.print(" = ");
       outputPW.print(this.getInstrument());
       outputPW.print("/");
       outputPW.println(this.getGrating());

       outputPW.print(StringUtils.rightPad("Requested Time",padSize));
       outputPW.print(" = ");
       outputPW.println(this.getExpTime().toString());

       if(isApproved == true) {
         outputPW.print(StringUtils.rightPad("Approved Time",padSize));
         outputPW.print(" = ");
         outputPW.println(this.getApprovedTime().toString());
       }

       outputPW.print(StringUtils.rightPad("Time Critical?",padSize));
       outputPW.print(" = ");
       outputPW.println(this.getTimeCritical());
       outputPW.print(StringUtils.rightPad("Coordinated Obs?",padSize));
       outputPW.print(" = ");
       outputPW.println(this.getMultitelescope());
       if (this.getObservatories() != null && observatories.length() > 0) {
         outputPW.print(StringUtils.rightPad("Observatories",padSize));
         outputPW.print(" = ");
         outputPW.println(this.getObservatories());
       }
       if (this.getResponseWindow() != null && this.getResponseWindow() != "") {
         outputPW.print(StringUtils.rightPad(TriggerTooConstants.RESPONSEWINDOW,padSize));
         outputPW.print(" = ");
         outputPW.print(this.getResponseWindow());
         outputPW.println(" (days) ");
         outputPW.print(StringUtils.rightPad(TriggerTooConstants.RESPONSESTART,padSize));
         outputPW.print(" = ");
         outputPW.print(this.getResponseStart());
         outputPW.println(" (days) ");
         outputPW.print(StringUtils.rightPad(TriggerTooConstants.RESPONSESTOP,padSize));
         outputPW.print(" = ");
         outputPW.print(this.getResponseStop());
         outputPW.println(" (days) ");

       }
       if (this.getFastProcStatus() != null && this.getFastProcStatus() != "") {
         outputPW.print(StringUtils.rightPad(TriggerTooConstants.FASTPROCSTATUS,padSize));
         outputPW.print(" = ");
         outputPW.println(this.getFastProcStatus());
         outputPW.print(StringUtils.rightPad(TriggerTooConstants.FASTPROCCOMMENT,padSize));
         outputPW.print(" = ");
         outputPW.println(this.getFastProcComment());
       }
    
       if (gridName == null || gridName.length() < 1) {
         if (getTriggerTarget().equals("N")) {
           outputPW.print(StringUtils.rightPad("Trigger Target:",padSize));
           outputPW.println(" = N");
         }
         else {
           Integer numObs = new Integer(1);
           outputPW.print(StringUtils.rightPad("Observations Requested",padSize));
           if (followups != null && followups.size() > 0) {
             if (this.getInitialTime().doubleValue() > 0) {
               // trigger + followups
               numObs += followups.size();
             } else {
               // this must be a monitor
               numObs = followups.size();
             }
           }
           outputPW.print(" = ");
           outputPW.println(numObs.toString());

           if(isApproved == true) {
             Integer appObs = new Integer(0);
             if ((followups.size() <= 0 || this.getInitialTime().doubleValue() > 0) && this.getApprovedTime() > 0)  {
                appObs = 1;
             }
             for (int ff=0;ff<followups.size();ff++) {
               if (followups.get(ff).getStatus().equals("accepted")) {
                   appObs += 1;
               }
             }
             outputPW.print(StringUtils.rightPad("Observations Approved",padSize));
             outputPW.print(" = ");
             outputPW.println(appObs.toString());
           }
         }
        
       }
       else {
         outputPW.print(StringUtils.rightPad("Grid Name",padSize));
         outputPW.print(" = ");
         outputPW.println(this.getGridName());
         outputPW.print(StringUtils.rightPad("Grid Pointings",padSize));
         outputPW.print(" = ");
         outputPW.println(this.getGridPointings().toString());
         if(isApproved == true) {
           outputPW.print(StringUtils.rightPad("Approved Grid Pointings",padSize));
           outputPW.print(" = ");
           outputPW.println(this.getGridApproved().toString());
         }
         outputPW.print(StringUtils.rightPad("Grid Radius",padSize));
         outputPW.print(" = ");
         outputPW.print(this.getGridRadius());
         outputPW.println(" (degrees) ");
       
       }
       outputPW.println("");
       outputPW.println("");


    } 
    catch(Exception exc) {
       LogMessage.println("PropTarget: Caught exception in writeOutput function");
       LogMessage.printException(exc);
       retval  = false;
    }


    return retval;
  }



}
