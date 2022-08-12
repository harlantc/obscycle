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

/******************************************************************************/
import java.lang.String;
import java.lang.Double;
import java.lang.Integer;
import java.io.*;
import java.util.Vector;
import java.util.Date;
import java.text.Format;
import java.text.NumberFormat;
import ascds.Coordinate;
import ascds.LogMessage;
import info.FormatUtils;
import ascds.FileUtils;

/**
  * The TriggerTooEntry class contains all the information related to 
  * TOO that has been triggered
 */
public class TriggerTooEntry {
    private Integer triggertoo_id;
    private Integer obsid;
    private Integer version;
    private String  sequenceNumber;
    private String  cycle;
    private String  principalInvest;
    private String  status;
    private String  submissionDate ;
    private String  coordinator ;
    private String  usint ;
    private String  urgency ;
    private String  targetName ;
    private String  overrideTargetName ;
    private Double  ra;
    private Double  dec;
    private String  raString;
    private String  decString;
    private Double  overrideRA;
    private Double  overrideDec;
    private String  overrideRAString;
    private String  overrideDecString;
    private Vector<CommentHistory>  commentHistory ;
    private CommentHistory  currentComment;
    private String  alternateGroupName;
    private Integer alternateApprovedCount;
    private Integer proposalID;
    private String  proposalNumber;
    private String  ltsDate;
    private String  stsDate;
    private String  cxcStart;
    private String  cxcStop;
    private String  additionalEmail;
    private String  fastproc;
    private String  fastprocComment;

    private boolean isValidCoords;

    private FormatUtils fu;


    /** 
      * Constructor
      */
    public TriggerTooEntry() {
	init();
    }

    private void init() {

        fu = new FormatUtils();

        triggertoo_id = new Integer(0);
        obsid = new Integer(0);
        version = new Integer(0);
        sequenceNumber = new String("");
        cycle = new String("");
        principalInvest = new String("");
        status = new String("");
        submissionDate = new String("");
        coordinator = new String("");
	usint = new String("");
	urgency = new String("");
        targetName = new String("");
        overrideTargetName = new String("");
        alternateGroupName = new String("");
        alternateApprovedCount = new Integer(0);
        proposalID = new Integer(0);
        proposalNumber = new String("");
        ltsDate = new String("");
        stsDate = new String("");
        cxcStart = new String("");
        cxcStop = new String("");
        additionalEmail = new String("");
        fastproc = new String("");
        fastprocComment = new String("");

        ra = TriggerTooConstants.EMPTY_VALUE;
        dec = TriggerTooConstants.EMPTY_VALUE;
        overrideRA = TriggerTooConstants.EMPTY_VALUE;
        overrideDec = TriggerTooConstants.EMPTY_VALUE;
        isValidCoords = false;
        
        currentComment = new CommentHistory();
        commentHistory = new Vector<CommentHistory>();


    }

    /**
      * Copy from an existing record
      * @param inputTrigger input TriggerTooEntry record
      */
    public void copy(TriggerTooEntry inputTrigger) {
        triggertoo_id = inputTrigger.getTriggerID();
        obsid = inputTrigger.getObsid();
        version = inputTrigger.getVersion();
	sequenceNumber = inputTrigger.getSequenceNumber();
	cycle = inputTrigger.getCycle();
	principalInvest = inputTrigger.getPI();
	status = inputTrigger.getStatus();
	submissionDate = inputTrigger.getSubmissionDate();
	coordinator = inputTrigger.getCoordinator();
	usint = inputTrigger.getUSINT();
	urgency = inputTrigger.getUrgency();
        targetName = inputTrigger.getTargetName();
        targetName = inputTrigger.getOverrideTargetName();
        ra = inputTrigger.getRA();
        dec = inputTrigger.getDec();
        overrideRA = inputTrigger.getOverrideRA();
        overrideDec = inputTrigger.getOverrideDec();
        isValidCoords = inputTrigger.isValidCoords();
	currentComment = inputTrigger.getCurrentComment();
	commentHistory = inputTrigger.getCommentHistoryList();
	alternateGroupName = inputTrigger.getAlternateGroupName();
	alternateApprovedCount = inputTrigger.getAlternateApprovedCount();
        proposalID = inputTrigger.getProposalID();
        proposalNumber = inputTrigger.getProposalNumber();
        ltsDate = inputTrigger.getLTSDate();
        stsDate = inputTrigger.getSTSDate();
        cxcStart = inputTrigger.getCXCStart();
        cxcStop = inputTrigger.getCXCStop();
        additionalEmail = inputTrigger.getAdditionalEmail();
        fastproc = inputTrigger.getFastProc();
        fastprocComment = inputTrigger.getFastProcComment();

    }


    //  Set routines
    public void setTriggerID(int inputValue) {
	triggertoo_id = new Integer(inputValue);
    }
    public void setTriggerID(Integer inputValue) {
	triggertoo_id = inputValue;
    }

    public void setObsid(int inputValue) {
	obsid = new Integer(inputValue);
    }
    public void setObsid(Integer inputValue) {
	obsid = inputValue;
    }

    public void setVersion(int inputValue) {
	version = new Integer(inputValue);
    }
    public void setVersion(Integer inputValue) {
	version = inputValue;
    }

    public void setSequenceNumber(String inputValue) {
      if (inputValue != null) {
	sequenceNumber = inputValue.trim();
      } else {
	sequenceNumber = new String("");
      }
    }
    public void setCycle(String inputValue) {
      if (inputValue != null) {
	cycle = inputValue.trim();
      } else {
	cycle = new String("");
      }
    }
    public void setPI(String inputValue) {
      if (inputValue != null) {
	principalInvest = inputValue.trim();
      } else {
	principalInvest = new String("");
      }
    }
    public void setStatus(String inputValue) {
      if (inputValue != null ) {
	status = inputValue.trim();
      } else {
	status = new String("");
      }
    }
    public void setSubmissionDate(String inputValue) {
      if (inputValue != null) {
	submissionDate = inputValue.trim();
      } else {
	submissionDate = new String("");
      }
    }
    public void setCoordinator(String inputValue) {
      if (inputValue != null) {
	coordinator = inputValue.trim();
      } else {
	coordinator = new String("");
      }
    }
    public void setUSINT(String inputValue) {
      if (inputValue != null) {
	usint = inputValue.trim();
      } else {
	usint = new String("");
      }
    }
    public void setUrgency(String inputValue) {
      if (inputValue != null) {
	urgency = inputValue.trim();
      } else {
	urgency = new String("");
      }
    }
    public void setTargetName(String inputValue) {
      if (inputValue != null) {
	targetName = inputValue.trim();
      } else {
	targetName = new String("");
      }
    }
    public void setOverrideTargetName(String inputValue) {
      if (inputValue != null) {
	overrideTargetName = inputValue.trim();
      } else {
	overrideTargetName = new String("");
      }
    }
    public void setCurrentComment(CommentHistory chist)
    {
       currentComment = chist;
    }
    public void setDraftComment(boolean isDraft) {
      if (isDraft) {
        currentComment.setStatus(TriggerTooConstants.DRAFTSTATUS);
      }
      else  {
        currentComment.setStatus("");
      }
    }
    public void setComment(String inputValue) {
	currentComment.setComment(inputValue);
    }
    public void clearCommentHistory() {
	commentHistory.clear();
    }
    public void clearCurrentComment() {
        currentComment = new CommentHistory();
    }
    public void addCommentHistory(CommentHistory inputValue) {
	commentHistory.add(inputValue);
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
    public void setOverrideRA(Double inputValue) {
	overrideRA = inputValue;
    }
    public void setOverrideRA(double inputValue) {
	overrideRA = new Double(inputValue);
    }
    public void setOverrideDec(Double inputValue) {
	overrideDec = inputValue;
    }
    public void setOverrideDec(double inputValue) {
	overrideDec = new Double(inputValue);
    }

    // set initial coordinate values 
    public void setInitCoords() {
       Coordinate coords;

       isValidCoords = true;
       if (ra  != TriggerTooConstants.EMPTY_VALUE && 
           dec != TriggerTooConstants.EMPTY_VALUE ) {
         try {
           coords = new Coordinate(ra.toString(),dec.toString(),"J2000");
           ra  = new Double(coords.getLon());
           dec = new Double(coords.getLat());
           raString  = new String(coords.getSexagesimalLon());
           decString = new String(coords.getSexagesimalLat());
         }
         catch (Exception exc) {
           LogMessage.println(exc.getMessage());
         }
       }
       else {
         ra  = TriggerTooConstants.EMPTY_VALUE;
         dec = TriggerTooConstants.EMPTY_VALUE;
         raString = new String("");
         decString= new String("");
       }
    }

    /**
      * set override coordinates
      * @param raStr  override RA value
      * @param decStr override Dec value
      */
    public void setOverrideCoords(String raStr, String decStr) 
    {
       Coordinate coords;

       isValidCoords = true;
       overrideRAString = raStr;
       overrideDecString = decStr;
       
       if ( (raStr  != null && raStr.length() > 0 && 
             (decStr == null || decStr.length() <= 0 )) || 
            (decStr  != null && decStr.length() > 0 && 
             (raStr == null || raStr.length() <= 0 ))) {
         isValidCoords = false;
       }
       else if (raStr  != null && raStr.length() > 0 && 
           decStr != null && decStr.length() > 0  &&
	   !raStr.equals(TriggerTooConstants.EMPTY_VALUE.toString()) &&
	   !decStr.equals(TriggerTooConstants.EMPTY_VALUE.toString()) ) {
         try {
           coords = new Coordinate(raStr,decStr,"J2000");
           overrideRA = new Double(coords.getLon());
           overrideDec = new Double(coords.getLat());
           overrideRAString  = new String(coords.getSexagesimalLon());
           overrideDecString = new String(coords.getSexagesimalLat());
         }
         catch (Exception exc) {
           LogMessage.println(exc.getMessage());
           isValidCoords = false;
         }
       }
       else {
         overrideRA= TriggerTooConstants.EMPTY_VALUE;
         overrideDec = TriggerTooConstants.EMPTY_VALUE;
         overrideRAString = new String("");
         overrideDecString = new String("");
       }
    }
    public void setAlternateGroupName(String inputValue) {
      if (inputValue != null) {
	alternateGroupName = inputValue.trim();
      } else {
	alternateGroupName = new String("");
      }
    }
    public void setAlternateApprovedCount(int inputValue) {
	alternateApprovedCount = new Integer(inputValue);
    }
    public void setAlternateApprovedCount(Integer inputValue) {
	alternateApprovedCount = inputValue;
    }
    public void setProposalID(int inputValue) {
	proposalID = new Integer(inputValue);
    }
    public void setProposalID(Integer inputValue) {
	proposalID = inputValue;
    }
    public void setProposalNumber(String inputValue) {
      if (inputValue != null) {
	proposalNumber = inputValue.trim();
      } else {
	proposalNumber = new String("");
      }
    }
    public void setLTSDate(String inputValue) {
      if (inputValue != null) {
	ltsDate = inputValue.trim();
      } else {
	ltsDate = new String("");
      }
    }
    public void setSTSDate(String inputValue) {
      if (inputValue != null) {
	stsDate = inputValue.trim();
      } else {
	stsDate = new String("");
      }
    }
    public void setCXCStart(String inputValue) {
      if (inputValue != null) {
	cxcStart = inputValue.trim();
      } else {
	cxcStart = new String("");
      }
    }
    public void setCXCStop(String inputValue) {
      if (inputValue != null) {
	cxcStop = inputValue.trim();
      } else {
	cxcStop = new String("");
      }
    }
    public void setFastProc(String inputValue) {
      if (inputValue != null) {
	fastproc= inputValue.trim();
      } else {
	fastproc= new String("");
      }
    }
    public void setFastProcComment(String inputValue) {
      if (inputValue != null) {
	fastprocComment = inputValue.trim();
      } else {
	fastprocComment = new String("");
      }
    }
    public void setAdditionalEmail(String inputValue) {
      if (inputValue != null) {
	additionalEmail = inputValue.trim();
      } else {
	additionalEmail = new String("");
      }
    }

    //  Get routines
    public Integer getTriggerID() {
	return triggertoo_id;
    }
    public Integer getObsid() {
	return obsid;
    }
    public Integer getVersion() {
	return version;
    }
    public String getSequenceNumber() {
	return sequenceNumber;
    }
    public String getCycle() {
	return cycle;
    }
    public String getPI() {
	return principalInvest;
    }
    public String getStatus() {
	return status;
    }
    public String getSubmissionDate() {
	return submissionDate;
    }
    public String getCoordinator() {
	return coordinator;
    }
    public String getUSINT() {
	return usint;
    }
    public String getUrgency() {
	return urgency;
    }
    public String getTargetName() {
	return targetName;
    }
    public String getOverrideTargetName() {
	return overrideTargetName;
    }
    public CommentHistory getCurrentComment() {
	return currentComment;
    }
    public String getComment() {
	return currentComment.getComment();
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
    public Double getOverrideRA() {
	return overrideRA;
    }
    public String getOverrideRAString() {
       
	return overrideRAString;
    }
    public Double getOverrideDec() {
	return overrideDec;
    }
    public String getOverrideDecString() {
	return overrideDecString;
    }
    public Coordinate getCoords() {
       Coordinate coords;

       coords = new Coordinate(ra.doubleValue(),dec.doubleValue());

       return coords;
    }

    public Coordinate getOverrideCoords() {
       Coordinate coords;

       coords = new Coordinate(overrideRA.doubleValue(),overrideDec.doubleValue());

       return coords;
    }


    /**
      * Return the new target name or null if no new values are entered
      * @return String modified target name or null 
      */
    public String getModifiedTargetName() {
      String tname = null;
      if (overrideTargetName != null && overrideTargetName.length() > 0) {
         tname = overrideTargetName;
      }
      else if (targetName != null && targetName.length() > 0) {
         tname = targetName;
      }
      return tname;
    }

    /**
      * Return the new coordinates or null if no new values are entered
      * @return Coordinate  modified coordinates or null
      */
    public Coordinate getModifiedCoords() {
      Coordinate coords = null;
      if (overrideRA != null && overrideRA != TriggerTooConstants.EMPTY_VALUE) {
LogMessage.println("getModifiedCoords: using override="  + overrideRA);
         coords  = getOverrideCoords();
      }
      else if (ra != null && ra != TriggerTooConstants.EMPTY_VALUE) {
         coords  = getCoords();
      }
      return coords;
    }
         

    public Vector<CommentHistory> getCommentHistoryList() {
       return commentHistory;
    }

    public String getCommentHistory(boolean useDraft) {
       String str = new String("");

       for (int ii=0;ii<commentHistory.size();ii++) {
          CommentHistory chist = (CommentHistory)commentHistory.get(ii);
          if (useDraft || 
              !chist.getStatus().equalsIgnoreCase(TriggerTooConstants.DRAFTSTATUS)) {
            str += "\n" + chist.getCreationDate() ;
            str += "  Author: " + chist.getUserName() + "\n";
            str += chist.getComment();
            str += "\n";
          }
       }
       return str;
    }
    public String getAlternateGroupName() {
	return alternateGroupName;
    }
    public Integer getAlternateApprovedCount() {
	return alternateApprovedCount;
    }
    public Integer getProposalID() {
	return proposalID;
    }
    public String getProposalNumber() {
	return proposalNumber;
    }
    public String getLTSDate() {
	return ltsDate;
    }
    public String getSTSDate() {
	return stsDate;
    }
    public String getCXCStart() {
	return cxcStart;
    }
    public String getCXCStop() {
	return cxcStop;
    }
    public String getAdditionalEmail() {
	return additionalEmail;
    }
    public String getFastProc() {
	return fastproc;
    }
    public String getFastProcComment() {
	return fastprocComment;
    }



  /**
    * isValidCoords -  did coordinates parse correctly
    * @return boolean  true if coordinates are valid
   */
  public boolean isValidCoords()
  {
    return isValidCoords;
  }

  /**
    * isModifiedCoords -  vhave coordinates been modified
    * @return boolean  true if coordinates have been modified
   */
  public boolean isModifiedCoords()
  {
    boolean retval = false;

    if ((targetName != null && targetName.length() > 0)  ||
       (overrideTargetName != null && 
        overrideTargetName.length() > 0)  ||
       ra != TriggerTooConstants.EMPTY_VALUE ||
       dec != TriggerTooConstants.EMPTY_VALUE ||
       overrideRA != TriggerTooConstants.EMPTY_VALUE ||
       overrideDec != TriggerTooConstants.EMPTY_VALUE ) {
       retval = true;
    }
    
    return retval;
      
  }



  /**
    * writeTriggerStatus -  printer friendly  format
    * @param filename  input filename
    * @param obsList   vector of observations
    * @param isCmtOnly is comment only message, no paging
    * @return boolean  true if status message written successfully
   */
  public boolean writeTriggerStatus(String filename,ObservationList obsList,
	boolean isCmtOnly)
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
        retval = writeOutput(outputPW,obsList,isCmtOnly);
        outputPW.close();
        outputFW.close();
        FileUtils.setPermissions(outputFile,"440");
      }
      catch (Exception exc) {
        LogMessage.println("TriggerTOO: Unable to write output to " + filename);
        LogMessage.println(exc.getMessage());
      }
    }
     
    return retval;
  }

  // -----------------------------------------------------
  // write the printer friendly version of the Trigger 
  // -----------------------------------------------------
  private boolean writeOutput(PrintWriter outputPW,ObservationList obsList,
	boolean isCmtOnly)
  {
    boolean retval=true;
    String[] strArray;

    try {
       NumberFormat nfi = NumberFormat.getInstance();
       nfi.setMinimumIntegerDigits(5);
       nfi.setGroupingUsed(false);
       NumberFormat nf = NumberFormat.getInstance();
       nf.setMaximumFractionDigits(6);
       nf.setMinimumFractionDigits(6);
       nf.setMinimumIntegerDigits(3);
       NumberFormat nft = NumberFormat.getInstance();
       nft.setMaximumFractionDigits(2);
       nft.setMinimumFractionDigits(2);
       Observation obs = obsList.getByObsid(getObsid());


       if (isCmtOnly) {
         outputPW.println(TriggerTooConstants.CMT_ONLY + "\n");
       }
       outputPW.print(getSubmissionDate() + ": ");
       outputPW.print("Peer-reviewed TOO Trigger request from ");
       outputPW.print(obs.getPIFirst() + ". " + obs.getPI() +"; ");
       outputPW.println("Paging urgency " + getUrgency() + " " +
            getCXCStart() + "-" + getCXCStop() + " days.");
      
       outputPW.println("\nStatus for tag number: " + obs.getSequenceNumber());
       outputPW.println("\nObservation Id: " + obs.getObsid() );
       outputPW.println("Observation Sequence Number: " + obs.getSequenceNumber() + "\n");

       outputPW.println("Current Target Name: " + obs.getTargetName()) ;
       outputPW.print("Current RA : ");
       if (obs.getRAString() != null && 
             obs.getRA() != TriggerTooConstants.EMPTY_VALUE) {
         outputPW.print(obs.getRAString() + "    " + nf.format(obs.getRA()));
       }
       outputPW.print("\nCurrent Dec: " );
       if (obs.getDecString() != null && 
             obs.getDec() != TriggerTooConstants.EMPTY_VALUE) {
          outputPW.print(obs.getDecString() + "    " + nf.format(obs.getDec()));
       }
       outputPW.println("");
           
       if (isReqValues(obs)) {
         outputPW.print("\nRequested Target Name: " );
         outputPW.println(getTargetName()) ;
         outputPW.print("Requested RA : " );
         if (getRAString() != null && 
             getRA() != TriggerTooConstants.EMPTY_VALUE) {
           outputPW.print(getRAString() + "    " + nf.format(getRA()));
         }
         outputPW.print("\nRequested Dec: ");
         if (getDecString() != null && 
             getDec() != TriggerTooConstants.EMPTY_VALUE) {
           outputPW.print(getDecString() + "    " + nf.format(getDec()));
         }
         outputPW.print("\n");
       }
       if (isOverrideValues(obs) ) {
         outputPW.print("\nOverride Target Name: " );
         outputPW.println(getOverrideTargetName()) ;
         outputPW.print("Override RA : " );
         if (getOverrideRAString() != null && 
             getOverrideRA() != TriggerTooConstants.EMPTY_VALUE) {
           outputPW.print(getOverrideRAString() + "    " + nf.format(getOverrideRA()));
         }
         outputPW.print("\nOverride Dec: " );
         if (getOverrideDecString() != null && 
             getOverrideDec() != TriggerTooConstants.EMPTY_VALUE) {
           outputPW.print(getOverrideRAString() + "    " + nf.format(getOverrideRA()));
         }
         outputPW.print("\n");
       }

       Integer cnt = new Integer(obsList.size() - 1);
       
       outputPW.print("\nObsCat Values: " + obs.getInstrument()+ "/");
       outputPW.println(obs.getGrating() + ", " + obs.getSIMode() + ", " + nft.format(obs.getRemainingExpTime()) + " ksec");

       outputPW.println("Number of Followups: " + cnt);



       outputPW.println("\nSTATUS:");
       writeStatus(outputPW,TriggerTooConstants.ACKNOWLEDGED);
       writeStatus(outputPW,TriggerTooConstants.APPROVED);
       writeStatus(outputPW,TriggerTooConstants.NOTAPPROVED);
       writeStatus(outputPW,TriggerTooConstants.WITHDRAWN);

       if (getFastProc() != null  && getFastProc().indexOf("appr") >= 0) {
         outputPW.print ("\n  [x] ") ;
         outputPW.println(TriggerTooConstants.FASTPROCMSG );
         outputPW.println("\n" + TriggerTooConstants.FASTPROCCOMMENT + ": "  + fu.getWrapped(getFastProcComment()));
       }
       else  {
         outputPW.print ("\n  [ ] ");
         outputPW.println(TriggerTooConstants.FASTPROCMSG );
       }

       if (obsList.size() > 1) {
         outputPW.println("\n");
         outputPW.println("Linked Observations:");
         outputPW.println(" SeqNbr  ObsId  PreId  MinLead     MaxLead");
         outputPW.println(" --------------------------------------------");
         for (int ii=0;ii<obsList.size();ii++) {
           Observation obslink = (Observation)obsList.get(ii);
           if (obs.getObsid() != obslink.getObsid()) {
             outputPW.print(" " + obslink.getSequenceNumber());  
             outputPW.print("  " + nfi.format(obslink.getObsid()));  
             outputPW.print("  " + nfi.format(obslink.getPreID()));  
             outputPW.print("  " + nf.format(obslink.getPreMinLead()));  
             outputPW.println("  " + nf.format(obslink.getPreMaxLead()));  
           }
         }
       }
       if (alternateApprovedCount.intValue() > 0) {
         outputPW.println("\n");
         outputPW.println("Alternate Target Group: " + alternateGroupName + ", approved count=" + alternateApprovedCount.toString() + "\n");
       }
   
       outputPW.println("\n");
       if (getCoordinator() != null && getCoordinator().length() > 0) {
          outputPW.println("Coordinator:   " + getCoordinator());
       }
       if (getUSINT() != null && getUSINT().length() > 0) {
          outputPW.println("Current USINT: " + getUSINT());
       }
      
       outputPW.println("\nCOMMENTS:");
       String allCmts = fu.getWrapped(getCommentHistory(false));
       outputPW.println(allCmts);

    } 
    catch(Exception exc) {
       LogMessage.println("TriggerTOO: Caught exception in writeOutput function");
       LogMessage.println(exc.getMessage());
       retval  = false;
    }

    return retval;
  }

  private void writeStatus(PrintWriter outputPW, String istatus)
  {
    if (getStatus().compareToIgnoreCase(istatus) == 0) {
      outputPW.print ("  [x] ") ;
    }
    else {
      outputPW.print ("  [ ] ") ;
    }
    outputPW.println(istatus);
  }

  private boolean isNewReqValues(Observation obs)
  {
     boolean retval = false;
     if ( getRA() != TriggerTooConstants.EMPTY_VALUE && 
          Math.abs(getRA() - obs.getRA()) > .000001) {
        retval = true;
     }
     else if ( getDec() != TriggerTooConstants.EMPTY_VALUE  && 
               Math.abs(getDec()- obs.getDec()) > .000001) {
        retval = true;
     }
     else if ( getTargetName().length() > 1 && 
               !(getTargetName().equals(obs.getTargetName()))) {
        retval = true;
     }
     return retval;
  }
  private boolean isNewOverrideValues(Observation obs)
  {
     boolean retval = false;
     double rdiff = Math.abs(getOverrideRA() - obs.getRA());
     if ( getOverrideRA() != TriggerTooConstants.EMPTY_VALUE  && 
          Math.abs(getOverrideRA() - obs.getRA()) > .000001) {
        retval = true;
     }
     else if ( getOverrideDec() != TriggerTooConstants.EMPTY_VALUE  && 
               Math.abs(getOverrideDec() - obs.getDec()) > .000001) {
        retval = true;
     }
     else if ( getOverrideTargetName().length() > 1 && !(getOverrideTargetName().equals(obs.getTargetName()))) {
        retval = true;
     }
     return retval;
  }
  private boolean isReqValues(Observation obs)
  {
     boolean retval = false;
     if ( getRA() != TriggerTooConstants.EMPTY_VALUE  ||
          getDec() != TriggerTooConstants.EMPTY_VALUE || 
          getTargetName().length() > 1 ) {
        retval = true;
     }
     return retval;
  }
  private boolean isOverrideValues(Observation obs)
  {
     boolean retval = false;
     if ( getOverrideRA() != TriggerTooConstants.EMPTY_VALUE  ||
          getOverrideDec() != TriggerTooConstants.EMPTY_VALUE || 
          getOverrideTargetName().length() > 1 ) {
        retval = true;
     }
     return retval;
  }


}
