package info;
/*
  Copyrights:
 
  Copyright (c) 2000-2014 Smithsonian Astrophysical Observatory
 
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

public class Observation {
    private Integer obsid;
    private String  cycle;
    private String  sequenceNumber;
    private String  proposalNumber;
    private String  principalInvest;
    private String  piFirst;
    private String  piEmail;
    private String  piPhone;
    private String  observer;
    private String  observerFirst;
    private String  observerEmail;
    private String  observerPhone;
    private String  title;
    private String  type;
    private String  instrument;
    private String  grating;
    private String  triggerCriteria;
    private String  responseWindow;
    private Double  responseStart;
    private Double  origResponseStart;
    private Double  responseStop;
    private Double  origResponseStop;
    private String  urgency;
    private Double  appExpTime;
    private Double  origAppExpTime;
    private Double  remExpTime;
    private Integer preID;
    private Double  preMinLead;
    private Double  origPreMinLead;
    private Double  preMaxLead;
    private Double  origPreMaxLead;
    private String  objectName;
    private String  status;
    private String  remarks;
    private String  mp_remarks;
    private String  observatories;
    private String  coordinatedObs;
    private String  alternateGroupName;
    private Integer alternateApprovedCount;
    private String  ltsDate;
    private String  stsDate;
    private String  simode;

    // not always set 
    private Integer linkedCount;
    private int     padSize;

    // editable if empty to start with
    private String  origTargetName;
    private String  targetName;
    private Double  ra;
    private Double  dec;
    private String  raString;
    private String  decString;

    // editable fields
    private String  contactInfo;
    private String  responseChange;
    private String  triggerJustify;
    private String  obsChanges;
    private boolean isEditable;
    private boolean isValidCoords;

    private FormatUtils fu;

    

    /**
      * Constructor
      */
    public Observation() {
	init();
    }

    private void init() {
        fu = new FormatUtils();
        padSize = 28;
        obsid = new Integer(0);
        cycle = new String("");
        sequenceNumber = new String("");
        proposalNumber = new String("");
	principalInvest = new String("");
        piFirst = new String("");
        piEmail = new String("");
        piPhone = new String("");
        observer = new String("");
        observerFirst = new String("");
        observerEmail = new String("");
        observerPhone = new String("");
	title = new String("");
	type = new String("");
	status = new String("");
        instrument = new String("");
        grating = new String("");
        triggerCriteria = new String("");
        responseWindow = new String("");
        responseStart = new Double(0.0);
        origResponseStart = new Double(0.0);
        responseStop = new Double(0.0);
        origResponseStop = new Double(0.0);
        urgency = new String("");
        remExpTime = new Double(0.0);
        origAppExpTime = new Double(0.0);
        appExpTime = new Double(0.0);
        preID = new Integer(0);
        origPreMinLead = new Double(0.0);
        origPreMaxLead = new Double(0.0);
        preMinLead = new Double(0.0);
        preMaxLead = new Double(0.0);
        remarks = new String("");
        mp_remarks = new String("");
        observatories = new String("");
        coordinatedObs = new String("");
        alternateGroupName = new String("");
        alternateApprovedCount = new Integer(0);
        linkedCount = new Integer(-1);
        ltsDate = new String("");
        stsDate = new String("");
        simode  = new String("");

        origTargetName = new String("");
        targetName = new String("");
        ra = TriggerTooConstants.EMPTY_VALUE;
        dec = TriggerTooConstants.EMPTY_VALUE;
        isEditable = true;
        isValidCoords = false;

        contactInfo = new String("");
        responseChange = new String("");
        triggerJustify = new String("");
        obsChanges = new String("");
 
    }

    /**
      * copy  - copy the observation
      * @param inputObs  input Observation
      */
    public void copy(Observation inputObs) {
        LogMessage.println("Copying: " + obsid.toString() + "--" + inputObs.getContactInfo());
        obsid = inputObs.getObsid();
        cycle = inputObs.getCycle();
        sequenceNumber = inputObs.getSequenceNumber();
	proposalNumber = inputObs.getProposalNumber();
	principalInvest = inputObs.getPI();
        piFirst = inputObs.getPIFirst();
        piEmail = inputObs.getPIEmail();
        piPhone = inputObs.getPIPhone();
        observer = inputObs.getObserver();
        observerFirst = inputObs.getObserverFirst();
        observerEmail = inputObs.getObserverEmail();
        observerPhone = inputObs.getObserverPhone();
	title = inputObs.getTitle();
	type = inputObs.getType();
	status = inputObs.getStatus();
	objectName = inputObs.getObjectName();
        instrument = inputObs.getInstrument();
        grating = inputObs.getGrating();
        triggerCriteria = inputObs.getTriggerCriteria();
        responseWindow = inputObs.getResponseWindow();
        responseStart = inputObs.getResponseStart();
        origResponseStart = inputObs.getOrigResponseStart();
        responseStop = inputObs.getResponseStop();
        origResponseStop = inputObs.getOrigResponseStop();
        remExpTime = inputObs.getRemainingExpTime();
        origAppExpTime = inputObs.getOrigApprovedExpTime();
        appExpTime = inputObs.getApprovedExpTime();
        preID = inputObs.getPreID();
        origPreMinLead = inputObs.getOrigPreMinLead();
        origPreMaxLead = inputObs.getOrigPreMaxLead();
        preMinLead = inputObs.getPreMinLead();
        preMaxLead = inputObs.getPreMaxLead();
        remarks = inputObs.getRemarks();
        mp_remarks = inputObs.getMPRemarks();
        observatories = inputObs.getObservatories();
        coordinatedObs = inputObs.getCoordinatedObs();
        alternateGroupName = inputObs.getAlternateGroupName();
        alternateApprovedCount = inputObs.getAlternateApprovedCount();
        linkedCount = inputObs.getLinkedCount();
        ltsDate = inputObs.getLTSDate();
        stsDate = inputObs.getSTSDate();
        simode  = inputObs.getSIMode();

        origTargetName = inputObs.getOrigTargetName();
        targetName = inputObs.getTargetName();
        ra = inputObs.getRA();
        dec = inputObs.getDec();
        isEditable = inputObs.isEditable();
        isValidCoords = inputObs.isValidCoords();
        

        contactInfo = inputObs.getContactInfo();
        responseChange = inputObs.getResponseChange();
        triggerJustify = inputObs.getTriggerJustify();
        obsChanges = inputObs.getObsChanges();
    }


    //  Set routines
    public void setObsid(Integer inputValue) {
	obsid = inputValue;
    }
    public void setObsid(int inputValue) {
	obsid = new Integer(inputValue);
    }
    public void setSequenceNumber(String inputValue) {
      if (inputValue != null) {
	sequenceNumber = inputValue.trim();
      } else {
	sequenceNumber = inputValue;
      }
    }
    public void setCycle(String inputValue) {
      if (inputValue != null) {
	cycle = inputValue.trim();
      } else {
	cycle = inputValue;
      }
    }
    public void setProposalNumber(String inputValue) {
      if (inputValue != null) {
	proposalNumber = inputValue.trim();
      } else {
	proposalNumber = inputValue;
      }
    }
    public void setPI(String inputValue) {
      if (inputValue != null) {
	principalInvest = inputValue.trim();
      } else {
	principalInvest = inputValue;
      }
    }
    public void setPIFirst(String inputValue) {
      if (inputValue != null) {
	piFirst = inputValue.trim();
      } else {
	piFirst = inputValue;
      }
    }
    public void setPIEmail(String inputValue) {
      if (inputValue != null) {
	piEmail = inputValue.trim();
      } else {
	piEmail = inputValue;
      }
    }
    public void setPIPhone(String inputValue) {
      if (inputValue != null) {
	piPhone = inputValue.trim();
      } else {
	piPhone = inputValue;
      }
    }
    public void setObserver(String inputValue) {
      if (inputValue != null) {
	observer = inputValue.trim();
      } else {
	observer = inputValue;
      }
    }
    public void setObserverFirst(String inputValue) {
      if (inputValue != null) {
	observerFirst = inputValue.trim();
      } else {
	observerFirst = inputValue;
      }
    }
    public void setObserverEmail(String inputValue) {
      if (inputValue != null) {
	observerEmail = inputValue.trim();
      } else {
	observerEmail = inputValue;
      }
    }
    public void setObserverPhone(String inputValue) {
      if (inputValue != null) {
	observerPhone = inputValue.trim();
      } else {
	observerPhone = inputValue;
      }
    }
    public void setTitle(String inputValue) {
      if (inputValue != null) {
	title = inputValue.trim();
      } else {
	title = inputValue;
      }
    }
    public void setType(String inputValue) {
      if (inputValue != null) {
	type = inputValue.trim();
      } else {
	type = inputValue;
      }
    }
    public void setObjectName(String inputValue) {
      if (inputValue != null) {
	objectName = inputValue.trim();
      } else {
	objectName = inputValue;
      }
    }
    public void setStatus(String inputValue) {
      if (inputValue != null) {
	status = inputValue.trim();
      } else {
	status = inputValue;
      }
    }
    public void setRemarks(String inputValue) {
      if (inputValue != null) {
	remarks = inputValue.trim();
      } else {
	remarks = inputValue;
      }
    }
    public void setMPRemarks(String inputValue) {
      if (inputValue != null) {
	mp_remarks = inputValue.trim();
      } else {
	mp_remarks = inputValue;
      }
    }
    public void setObservatories(String inputValue) {
      if (inputValue != null) {
	observatories = inputValue.trim();
      } else {
	observatories = inputValue;
      }
    }
    public void setCoordinatedObs(String inputValue) {
      if (inputValue != null) {
	coordinatedObs = inputValue.trim();
      } else {
	coordinatedObs = inputValue;
      }
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
    public void setTriggerCriteria(String inputValue) {
      if (inputValue != null) {
	triggerCriteria = inputValue.trim();
      }
      else {
	triggerCriteria = inputValue;
      }
    }
    public void setResponseWindow(String inputValue) {
      if (inputValue != null) {
	responseWindow = inputValue.trim();
      }
      else {
        responseWindow = inputValue;
      }
    }
    public void setOrigResponseStart(double inputValue) {
	origResponseStart = new Double(inputValue);
	responseStart = origResponseStart;
    }
    public void setResponseStart(Double inputValue) {
       responseStart = inputValue;
    }
    public void setResponseStart(double inputValue) {
       responseStart = new Double(inputValue);
    }
    public void setOrigResponseStop(double inputValue) {
	origResponseStop = new Double(inputValue);
	responseStop = origResponseStop;
    }
    public void setResponseStop(Double inputValue) {
       responseStop = inputValue;
    }
    public void setResponseStop(double inputValue) {
       responseStop = new Double(inputValue);
    }
    public void setAlternateGroupName(String inputValue) {
      if (inputValue != null) {
	alternateGroupName = inputValue.trim();
      } else {
	alternateGroupName = inputValue;
      }
    }
    public void setAlternateApprovedCount(int inputValue) {
      alternateApprovedCount = new Integer(inputValue);
    }
    public void setAlternateApprovedCount(Integer inputValue) {
      alternateApprovedCount = inputValue;
    }

    public void setLinkedCount(int inputValue) {
      linkedCount = new Integer(inputValue);
    }

    public void setLTSDate(String inputValue) {
      if (inputValue != null) {
	ltsDate = inputValue.trim();
      } else {
	ltsDate = inputValue;
      }
    }
    public void setSTSDate(String inputValue) {
      if (inputValue != null) {
	stsDate = inputValue.trim();
      } else {
	stsDate = inputValue;
      }
    }
    public void setSIMode(String inputValue) {
      if (inputValue != null) {
	simode = inputValue.trim();
      } else {
	simode = inputValue;
      }
    }

    /**
      * setUrgency  - set urgency of FAST,MEDIUM,SLOW based 
      *               on response window stop value
      */
    public void setUrgency() {
      if (responseStop.doubleValue() > 0.0 &&
          responseStop.doubleValue() <=  5.0 ) {
        urgency = TriggerTooConstants.FAST;
      }
      else if (responseStop.doubleValue() <= 30.0) {
        urgency = TriggerTooConstants.MEDIUM;
      }
      else  {
        urgency = TriggerTooConstants.SLOW;
      }
      //urgency += " " + responseStart.toString() + "-" + responseStop.toString() + " days";

    }
   
    public void setRemainingExpTime(Double inputValue) {
	remExpTime = inputValue;
    }
    public void setRemainingExpTime(double inputValue) {
	remExpTime = new Double(inputValue);
    }
    public void setApprovedExpTime(Double inputValue) {
	appExpTime = inputValue;
    }
    public void setApprovedExpTime(double inputValue) {
	appExpTime = new Double(inputValue);
    }
    public void setOrigApprovedExpTime(double inputValue) {
	origAppExpTime = new Double(inputValue);
	appExpTime = origAppExpTime;
    }
    public void setPreID(Integer inputValue) {
	preID = inputValue;
    }
    public void setPreID(int inputValue) {
       Integer ival;
       ival = new Integer(inputValue);
       preID = ival;
    }
    public void setPreMinLead(Double inputValue) {
	preMinLead = inputValue;
    }
    public void setPreMinLead(double inputValue) {
	preMinLead = new Double(inputValue);
    }
    public void setOrigPreMinLead(double inputValue) {
	origPreMinLead = new Double(inputValue);
        preMinLead = origPreMinLead;
    }
    public void setPreMaxLead(Double inputValue) {
	preMaxLead = inputValue;
    }
    public void setPreMaxLead(double inputValue) {
	preMaxLead = new Double(inputValue);
    }
    public void setOrigPreMaxLead(double inputValue) {
	origPreMaxLead = new Double(inputValue);
        preMaxLead = origPreMaxLead;
    }
    public void setOrigTargetName(String inputValue) {
      if (inputValue != null) {
	origTargetName = inputValue.trim();
      } else {
	origTargetName = inputValue;
      }
      targetName = origTargetName;
    }
    public void setTargetName(String inputValue) {
      if (inputValue != null) {
	targetName = inputValue.trim();
      } else {
	targetName = inputValue;
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

       LogMessage.println("Setting COORDS: " + raStr  + "," + decStr + " for obsid "  + obsid.toString());
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

    public void setContactInfo(String inputValue) {
      if (inputValue != null) {
	contactInfo = inputValue.trim();
      } else {
	contactInfo = inputValue;
      }
    }
    public void setResponseChange(String inputValue) {
      if (inputValue != null) {
	responseChange = inputValue.trim();
      } else {
	responseChange = inputValue;
      }
    }
    public void setTriggerJustify(String inputValue) {
      if (inputValue != null) {
	triggerJustify = inputValue.trim();
      } else {
	triggerJustify = inputValue;
      }
    }
    public void setObsChanges(String inputValue) {
      if (inputValue != null) {
	obsChanges = inputValue.trim();
      } else {
	obsChanges = inputValue;
      }
    }

    // Get routines
    public Integer getObsid() {
	return obsid;
    }
    public String getSequenceNumber() {
	return sequenceNumber;
    }
    public String getCycle() {
	return cycle;
    }
    public String getProposalNumber() {
	return proposalNumber;
    }
    public String getPI() {
	return principalInvest;
    }
    public String getPIFirst() {
	return piFirst.substring(0,1);
    }
    public String getPIEmail() {
	return piEmail;
    }
    public String getPIPhone() {
	return piPhone;
    }
    public String getObserver() {
	return observer;
    }
    public String getObserverFirst() {
	return observerFirst;
    }
    public String getObserverEmail() {
	return observerEmail;
    }
    public String getObserverPhone() {
	return observerPhone;
    }
    public String getTitle() {
	return title;
    }
    public String getType() {
	return type;
    }
    public String getStatus() {
	return status;
    }
    public String getRemarks() {
	return remarks;
    }
    public String getMPRemarks() {
	return mp_remarks;
    }
    public String getObservatories() {
	return observatories;
    }
    public String getCoordinatedObs() {
	return coordinatedObs;
    }
    public String getObjectName() {
	return objectName;
    }
    public String getInstrument() {
	return instrument;
    }
    public String getGrating() {
	return grating;
    }
    public String getTriggerCriteria() {
	return triggerCriteria;
    }
    public String getResponseWindow() {
	return responseWindow;
    }
    public Double getOrigResponseStart() {
	return origResponseStart;
    }
    public Double getResponseStart() {
	return responseStart;
    }
    public Double getOrigResponseStop() {
	return origResponseStop;
    }
    public Double getResponseStop() {
	return responseStop;
    }
    public String getAlternateGroupName() {
	return alternateGroupName;
    }
    public Integer getAlternateApprovedCount() {
	return alternateApprovedCount;
    }
    public Integer getLinkedCount() {
	return linkedCount;
    }
    public String getUrgency() {
	return urgency;
    }
    public Double getRemainingExpTime() {
	return remExpTime;
    }
    public Double getApprovedExpTime() {
	return appExpTime;
    }
    public Double getOrigApprovedExpTime() {
	return origAppExpTime;
    }
    public Integer getPreID() {
	return preID;
    }
    public Double getPreMinLead() {
	return preMinLead;
    }
    public Double getOrigPreMinLead() {
	return origPreMinLead;
    }
    public Double getPreMaxLead() {
	return preMaxLead;
    }
    public Double getOrigPreMaxLead() {
	return origPreMaxLead;
    }
    public String getLTSDate() {
	return ltsDate;
    }
    public String getSTSDate() {
	return stsDate;
    }
    public String getSIMode() {
	return simode;
    }
    public String getOrigTargetName() {
	return origTargetName;
    }
    public String getTargetName() {
	return targetName;
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
    public Coordinate getCoords() {
       Coordinate coords = null;

       if (ra.doubleValue() != TriggerTooConstants.EMPTY_VALUE) {
         coords = new Coordinate(ra.doubleValue(),dec.doubleValue());
       }

       return coords;
    }
    public String getContactInfo() {
	return contactInfo;
    }
    public String getResponseChange() {
	return responseChange;
    }
    public String getTriggerJustify() {
	return triggerJustify;
    }
    public String getObsChanges() {
	return obsChanges;
    }
    public String getFullTriggerComments() {
      String cmt = new String ("");
      if (triggerJustify != null && triggerJustify.length() > 0) {
        cmt += "\nTrigger Justification:  ";
        cmt += triggerJustify;
      }
      if (responseChange != null && responseChange.length() > 0) {
        cmt += "\nResponse Change Request:  ";
        cmt += responseChange;
      }
      if (obsChanges != null && obsChanges.length() > 0) {
        cmt += "\nObsCat Change Request:  ";
        cmt += obsChanges;
      }
      if (contactInfo != null && contactInfo.length() > 0) {
        cmt += "\n24 Hr. Contact Information :  ";
        cmt += contactInfo;
      }
      return cmt;
    }

  /**
    * Determine is observation can be triggered
    * it should be have remaining exposure time and must be
    * the 1st object if followup observations exist
    * @return boolean true if the observation can be triggered
    */
  public boolean isTrigger()
  {
    boolean isTrigger;

    if (remExpTime.doubleValue() > 0.0 && 
	!isCompleted() &&
        preID.intValue() <= 0 ) {

      isTrigger = true;
    }
    else {
      isTrigger = false;
    }
          
    return isTrigger;
  }

  /**
   * Determine if target information is editable.
   * Current RA/DEC must be 0.0
   * @return boolean true, if ra/dec is editable
   */
  public boolean isEditable()
  {
    return isEditable;
  }

  public void setIsEditable()
  {

    if ((ra == TriggerTooConstants.EMPTY_VALUE || ra.doubleValue() == 0.0 )  &&
        (dec == TriggerTooConstants.EMPTY_VALUE || dec.doubleValue() == 0.0 )) {
      isEditable= true;
    }
    else {
      isEditable=false;
    }

  }
  /**
    * Check if observation has been completed
    * @return boolean true if Observation is already completed
    */
  public boolean isCompleted()
  {
     boolean returnValue = false;

     if (status.equalsIgnoreCase("archived") ||
         status.equalsIgnoreCase("observed"))  {

        returnValue = true;
     }
     return returnValue;
  }



  /**
    * Check if observation is still unobserved 
    * @return boolean true if observation is unobserved
    */
  public boolean isUnobserved()
  {
     boolean returnValue = false;
  
     if (status.equalsIgnoreCase("unobserved") ||
         status.equalsIgnoreCase("untriggered"))  {

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
   * Write observation in printer friendly format.
   * @param filename  output file name
   * @return boolean true if no write issues
   */
  public boolean writeSubmitObservation(String filename)
  {
    boolean retval = true;
    File outputFile = new File(filename);

    try {
      if(outputFile.exists()) {
        LogMessage.println("File already exists: " + filename );
        retval = false;
      } 
      else {
        //Create the blank file
        boolean createdFile = outputFile.createNewFile();
        if(!createdFile) {
          LogMessage.println("Error in creating new file: " + filename);
          retval = false;
        }
        
      }
    } 
    catch(Exception exc) {
      LogMessage.println("Caught exception in creating a new file for writing TOO observation");
      LogMessage.println(exc.getMessage());
      retval = false;
    }
    if (retval) {
      try {
        FileWriter outputFW = new FileWriter(filename.toString());
        PrintWriter outputPW = new PrintWriter(outputFW);
        Date fileDate = new Date(outputFile.lastModified());
        outputPW.print(StringUtils.rightPad("Submission Date",padSize));
        outputPW.print(" = ");
        outputPW.println(fileDate.toString());
        retval = writeOutput(outputPW);
        outputPW.close();
        outputFW.close();
        FileUtils.setPermissions(outputFile,"440"); 
      }
      catch (Exception exc) {
        LogMessage.println("Observation.writeSubmit: Unable to write final version to " + filename);
        LogMessage.println(exc.getMessage());
      }
    }
     
    return retval;
  }

  /**
   * writeRPSNotify  -  create the initial RPS input file for triggering
   *                    the TOO.  Contains required RPS formatted fields.
   * @param filename    filename for RPS trigger file
   * @return boolean true if no write issues
   */
  public boolean writeRPSNotify(String filename )
  {
    boolean retval = true;

    File outputFile = new File(filename);
    try {
      if(outputFile.exists()) {
        LogMessage.println("File already exists: " + filename );
        retval = false;
      } 
      else {
        //Create the blank file
        boolean createdFile = outputFile.createNewFile();
        if(!createdFile) {
          LogMessage.println("Error in creating new file: " + filename);
          retval = false;
        }
      }
    }
    catch(Exception exc) {
      LogMessage.println("Caught exception in creating a new file for writing TOO observation");
      LogMessage.println(exc.getMessage());
      retval = false;
    }

    if (retval) {
      try {
        // need this as an indicator to receiver process for stripping
        // parameters for paging email 
        FileWriter outputFW = new FileWriter(filename.toString());
        PrintWriter outputPW = new PrintWriter(outputFW);
 
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(6);
        nf.setMinimumFractionDigits(6);
        
        outputPW.print("DDT.CYCLE[]=");
        outputPW.println(this.getCycle());
        outputPW.print("PROPOSAL.NUMBER[]=");
        outputPW.println(this.getProposalNumber());
        outputPW.print("OBSCAT.SEQNBR[]=");
        outputPW.println(this.getSequenceNumber());
        outputPW.print("NAME.LAST[]=");
        outputPW.println(this.getPI());
        outputPW.print("NAME.FIRST[]=");
        outputPW.println(this.getPIFirst());
        outputPW.print("EMAIL.ADDRESS[]=");
        outputPW.println(this.getPIEmail());
        if (this.getObserverEmail() != null && 
            this.getObserverEmail().length() > 0) {
          outputPW.println("COICON.CONTACT.[]=Y");
          outputPW.print("COI.EMAIL(1)[]=");
          outputPW.println(this.getObserverEmail());
        }
        outputPW.print("OBSERVER.TYPE[]=");
        outputPW.println(this.getType());
        outputPW.print("PROPOSAL.TITLE[]=");
        outputPW.println(this.getTitle());
        outputPW.print("URGENCY[]=");
        String urgStr = " " + this.responseStart.toString() + "-" + this.responseStop.toString() + " days";
        outputPW.print(this.getUrgency());
        outputPW.println(urgStr);
        outputPW.print("TARGET.NAME[]=");
        if (this.getTargetName() != null) {
          outputPW.println(this.getTargetName());
        } else {
          outputPW.println("");
        }
        outputPW.print("COORD.RA[]=");
        outputPW.print(this.getRAString());
        outputPW.print("  ");

        outputPW.println(nf.format(this.getRA().doubleValue()));
        outputPW.print("COORD.DEC[]=");
        outputPW.print(this.getDecString());
        outputPW.print("  ");
        outputPW.println(nf.format(this.getDec().doubleValue()));
        outputPW.println("--------------------------------------------");
        Date currentDate = new Date();
        outputPW.print(StringUtils.rightPad("Submission Date",padSize));
        outputPW.print(" = ");
        outputPW.println(currentDate.toString());
        retval = writeOutput(outputPW);
        outputPW.close();
        outputFW.close();
        FileUtils.setPermissions(outputFile,"440"); 

      }
      catch (Exception exc) {
        LogMessage.println("Observation::RPSNotify: Unable to write RPS status message");
        LogMessage.println(exc.getMessage());
        retval = false;
      }
    }
     
    return retval;
  }

  /** 
   * writeObservation - write printer friendly format
   * @param filename output filename
   * @return boolean true if no write issues
   */
  public boolean writeObservation(String filename)
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
        LogMessage.println("Caught exception in creating a new file for writing TOO observation");
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
       retval = writeOutput(outputPW);
       outputPW.close();
       outputFW.close();
     }
     catch (Exception exc) {
       LogMessage.println("TriggerTOO: Unable to write printer friendly version");
       LogMessage.println(exc.getMessage());
       retval = false;
     }
     
     return retval;
  }


  /**
   * write observation information in printer friendly format 
   * @param outputPW output buffer
   * @return boolean true if no write issues
   */
  private boolean writeOutput(PrintWriter outputPW)
  {
    boolean retval=true;
    String[] strArray;

    try {
       NumberFormat nf = NumberFormat.getInstance();
       nf.setMaximumFractionDigits(6);
       nf.setMinimumFractionDigits(6);


       outputPW.print(StringUtils.rightPad(TriggerTooConstants.OBSID,padSize));
       outputPW.print(" = ");
       outputPW.println(this.getObsid().toString());
       outputPW.print(StringUtils.rightPad(TriggerTooConstants.SEQNBR,padSize));
       outputPW.print(" = ");
       outputPW.println(this.getSequenceNumber());
       outputPW.print(StringUtils.rightPad(TriggerTooConstants.PROPNUM,padSize));
       outputPW.print(" = ");
       outputPW.println(this.getProposalNumber());
       outputPW.print(StringUtils.rightPad(TriggerTooConstants.TITLE,padSize));
       outputPW.print(" = ");
       outputPW.println(this.getTitle());
       outputPW.println("");

       outputPW.print(StringUtils.rightPad(TriggerTooConstants.PINAME,padSize));
       outputPW.print(" = ");
       outputPW.println(this.getPI());
       //outputPW.print(StringUtils.rightPad(TriggerTooConstants.PIPHONE,padSize));
       //outputPW.print(" = ");
       //outputPW.println(this.getPIPhone());
       outputPW.print(StringUtils.rightPad(TriggerTooConstants.PIEMAIL,padSize));
       outputPW.print(" = ");
       outputPW.println(this.getPIEmail());

       if (this.getObserver() != null && this.getObserver().length() > 0) {
         outputPW.print(StringUtils.rightPad(TriggerTooConstants.OBSERVER,padSize));
         outputPW.print(" = ");
         outputPW.println(this.getObserver());
         //outputPW.print(StringUtils.rightPad(TriggerTooConstants.OBSERVERPHONE,padSize));
         //outputPW.print(" = ");
         //outputPW.println(this.getObserverPhone());
         outputPW.print(StringUtils.rightPad(TriggerTooConstants.OBSERVEREMAIL,padSize));
         outputPW.print(" = ");
         outputPW.println(this.getObserverEmail());
       }
       outputPW.println(fu.getWrapped(StringUtils.rightPad(TriggerTooConstants.CONTACTINFO,padSize) + " = " + this.getContactInfo()));
       outputPW.println("");

       outputPW.println(fu.getWrapped(StringUtils.rightPad(TriggerTooConstants.TRIGGERCRITERIA,padSize) + " = " + this.getTriggerCriteria()));
       outputPW.println("");
   

       outputPW.println(fu.getWrapped(StringUtils.rightPad(TriggerTooConstants.TRIGGERJUSTIFY,padSize) + " = " + this.getTriggerJustify()));
       outputPW.println("");
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
       outputPW.println(fu.getWrapped(StringUtils.rightPad(TriggerTooConstants.RESPONSECHANGES,padSize) + " = " + this.getResponseChange()));
       outputPW.println("");
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
       outputPW.println(this.getInstrument());
       outputPW.print(StringUtils.rightPad(TriggerTooConstants.GRATING,padSize));
       outputPW.print(" = ");
       outputPW.println(this.getGrating());
       outputPW.print(StringUtils.rightPad("SI Mode",padSize));
       outputPW.print(" = ");
       outputPW.println(this.getSIMode());
       outputPW.print(StringUtils.rightPad(TriggerTooConstants.EXPTIME,padSize));
       outputPW.print(" = ");
       outputPW.println(this.getRemainingExpTime().toString());

       if (alternateApprovedCount.intValue() > 0) {
         outputPW.println("");
         outputPW.print(StringUtils.rightPad("Alternate Target Group",padSize));
         outputPW.print(" = ");
         outputPW.print(this.getAlternateGroupName());
         outputPW.print("   Approved Count: ");
         outputPW.println(this.getAlternateApprovedCount().toString());

       }
       outputPW.println("");
       outputPW.print(fu.getWrapped(StringUtils.rightPad(TriggerTooConstants.OBSCHANGES,padSize) + " = " + this.getObsChanges()));
       outputPW.println("");

    } 
    catch(Exception exc) {
       LogMessage.println("TriggerTOO: Caught exception in writeOutput function");
       LogMessage.println(exc.getMessage());
       retval  = false;
    }


    return retval;
  }


}
