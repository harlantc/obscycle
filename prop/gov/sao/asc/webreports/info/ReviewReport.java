// Copyright (c) 2003-2016, 2021, 2022 Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS
//                               INFO Package
//--------------------------------------------------------------------------
//                               Proposal
//****************************************************************************
package info;


import ascds.RunCommand;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import db.DBConnection;
import ascds.LogMessage;
import ascds.FileUtils;

/** *********************************************************
 * This class contains all the information needed to display
 * a review report.  An instance of this class is needed for
 * each primary, secondary and peer review report.
 */
public class ReviewReport {
    private boolean showDebug;
    private String type;
    private String lastUpdate;
    private Date fileDate = null;
    private String status;
    private String reportStatus;
    private Proposal proposal;
    private String panelName;
    private int panelID;
    private String reviewerLastName;
    private int reviewerID;
    private int secondaryReviewerID;
    // *Last name are really Last,F!
    private String secondaryLastName;
    private int primaryReviewerID;
    private String primaryLastName;
    public String punditPrimaryLastName;
    private int userID; //to distinguish when a chair/admin/developer views a file
    private Vector<Integer> additionalReviewers; //for LPs
    private Vector<String> additionalPanels; //for LPs
    private Vector<String> peerForLPPanels; //panel peer report link for LPs
    private String reportsDataPath;
    private String timedOutFilename;
    //Following are the fields in the report itself
    private String scienceImportance;
    private String scienceJustification;
    private String fmtClarity;
    private String feasibility;
    private String feasibilityConstraint;
    private String chandraUseCapability;
    private String clarity;
    private String comments;
    private String specificRecommendations;
    private String reasonGradeNotHigher;
    private String technicalReview;
    private String degreeOfEffort;
    private boolean printOnly;
    private Lock   reportLock;
    private User   theUser;
    private FormatUtils fu;
    private String prelimComplete; // true if the prelim report was Completed

    //Following are only used on the peer review form

    //refers to "Good Proposal, but all targets assigned to higher ranked proposal"
    //private boolean targetsTaken; 
    private String targetsTaken; 
    //"Good Proposal, but TOO observations are limited"
    //private boolean TOOLimited;
    private String TOOLimited;
    //"Good Proposal, but constrained observations are limited"
    //private boolean constrainedLimited;
    private String constrainedLimited;

    /**
     * Constructor
     *
     */
    public ReviewReport() {
	init();
    }

    /**
     * Constructor
     *
     * @param propNum  proposal number
     */
    public ReviewReport(String propNum ) {
	proposal = new Proposal(propNum);
	init();
    }

    private void init() {
        showDebug=true;
        fu = new FormatUtils();
        reportLock = null;
        theUser = null;
	type = new String("");
	lastUpdate = new String("");
	status = new String("");
	if(proposal == null) {
	    proposal = new Proposal();
	}
	panelName = new String("");
	reviewerLastName = new String("");
	scienceImportance = new String("");
	scienceJustification = new String("");
	fmtClarity = new String("");
	feasibility = new String("");
	feasibilityConstraint = new String("");
	chandraUseCapability = new String("");
	clarity = new String("");
	comments = new String("");
	specificRecommendations = new String("");
	reasonGradeNotHigher = new String("");
	technicalReview = new String("");
	degreeOfEffort = new String("");

	targetsTaken = new String("");
	TOOLimited = new String("");
	constrainedLimited = new String("");

	additionalReviewers = new Vector<Integer>();
	additionalPanels = new Vector<String>();
	peerForLPPanels = new Vector<String>();
        printOnly = false;
        punditPrimaryLastName = "";
        prelimComplete = "";
    }

    /**
     * This method compares to ReviewReport objects 
     *
     * @param inputRR  input ReviewReport object to compare against
     * @return boolean  true, if reports are equal
     */
    public boolean equals(ReviewReport inputRR) {
	boolean retval = true;

	if(!scienceImportance.equals(inputRR.getSI())) {
	    retval = false;
	}
	
	if(!scienceJustification.equals(inputRR.getSJ())) {
	    retval = false;
	}
	if(!fmtClarity.equals(inputRR.getFmtClarity())) {
	    retval = false;
	}
	
	if(!feasibility.equals(inputRR.getFeasibility())) {
	    retval = false;
	}
	if(!feasibilityConstraint.equals(inputRR.getFeasibilityConstraint())) {
	    retval = false;
	}

	if(!chandraUseCapability.equals(inputRR.getChandraUse())) {
	    retval = false;
	}

	if(!clarity.equals(inputRR.getClarity())) {
	    retval = false;
	}

	String inputComments = (inputRR.getComments()).trim();
	String tempComments = comments.trim();
	if(!tempComments.equals(inputComments)) {
	    retval = false;
	}

	String inputRecs = (inputRR.getRecs()).trim();
	String tempRecs = specificRecommendations.trim();
	if(!tempRecs.equals(inputRecs)) {
	    retval = false;
	}

	String inputReason = (inputRR.getGradeReason()).trim();
	String tempReason = reasonGradeNotHigher.trim();
	if(!tempReason.equals(inputReason)) {
	    retval = false;
	}

	String inputTechnicalReview = (inputRR.getTechnicalReview()).trim();
	String tempTechnicalReview = technicalReview.trim();
	if(!tempTechnicalReview.equals(inputTechnicalReview)) {
	    retval = false;
	}

	if(!degreeOfEffort.equals(inputRR.getEffort())) {
	    retval = false;
	}

	if(!targetsTaken.equals(inputRR.getTargetsTaken())) {
	    retval = false;
	}
	   
	if(!TOOLimited.equals(inputRR.getTOOLimited())) {
	    retval = false;
	}

	if(!constrainedLimited.equals(inputRR.getConstrained())) {
	    retval = false;
	}

        if(!prelimComplete.equals(inputRR.getPreComp())) {
            retval = false;
        }

	return retval;
    }


    //*****************************************************
    //Set routines
    //*****************************************************

    /** 
     * set the report type
     *
     * @param inputType  input report type 
     */
    public void setType(String inputType) {
	type = inputType;
    }

    /** 
     * set the proposal science category
     *
     * @param inputCategory  input science category (subject)
     */
    public void setCategory(String inputCategory) {
	proposal.setCategory(inputCategory);
    }

    /** 
     * set the proposal Principal Investigator (P.I.)
     *
     * @param inputPI  input principal investigator name (PI) 
     */
    public void setPI(String inputPI) {
	proposal.setPI(inputPI);
    }

    /** 
     * set the proposal title
     *
     * @param inputTitle  input proposal title  
     */
    public void setTitle(String inputTitle) {
	proposal.setTitle(inputTitle);
    }

    /** 
     * set the proposal type
     *
     * @param inputType  input proposal type 
     */
    public void setProposalType(String inputType) {
	proposal.setProposalType(inputType);
    }

    /** 
     * set the proposal constrained target identifier
     *
     * @param inputParam  input constrained target  
     */
    public void setConstrainedTargets(String inputParam) {
	proposal.setConstrainedTargets(inputParam);
    }

    /** 
     * set the proposal joint identifier
     *
     * @param inputParam  input joint */
    public void setProposalJoint(String inputParam) {
	proposal.setJoint(inputParam);
    }
    /** 
     * set the proposal multicycle identifier
     *
     * @param inputParam  input multicycle  
     */
    public void setProposalMulticycle(String inputParam) {
	proposal.setMulticycle(inputParam);
    }

    
    /** 
     * set the original report status.  This is the
     * status of the report when the object is first initialized.
     * If the file has been saved, it's the extension on the filename.
     *
     * @param inputStatus  input report status
     */
    public void setStatus(String inputStatus) {
	status = inputStatus;
    }
    /** 
     * set the new report status as a result of user action.
     *
     * @param inputStatus  input report status 
     */
    public void setReportStatus(String inputStatus) {
	reportStatus = inputStatus;
    }

    /** 
     * set the report data path
     *
     * @param inReportsDataPath  directory path for webreport files
     */
    public void setDataPath(String inReportsDataPath) {
	reportsDataPath = new String(inReportsDataPath);
    }

    /** 
     * set the proposal for this report
     *
     * @param inputProp  input proposal
     */
    public void setProposal(Proposal inputProp) {
	proposal.copy(inputProp);
    }

    /** 
     * set the panel name for this report
     *
     * @param inputPanelName  input panel name
     */
    public void setPanelName(String inputPanelName) {
	panelName = inputPanelName;
    }

    /** 
     * set the panel identifier 
     *
     * @param inputPanelID  input panel identifier
     */
    public void setPanelID(int inputPanelID) {
	panelID = inputPanelID;
    }
    
    /** 
     * set the reviewer name
     *
     * @param inputParam  input reviewer name 
     */
    public void setReviewerName(String inputParam) {
	reviewerLastName = inputParam;
    }
    /** 
     * set the secondary reviewer name 
     *
     * @param inputParam  input secondary reviewer name
     */
    public void setSecondaryReviewerName(String inputParam) {
	secondaryLastName = inputParam;
    }
    /** 
     * set the primary reviewer name 
     *
     * @param inputParam  input primary reviewer name
     */
    public void setPrimaryReviewerName(String inputParam) {
	primaryLastName = inputParam;
    }


    /** 
     * set the reviewer identification 
     *
     * @param inputParam  input reviewer id
     */
    public void setReviewerID(int inputParam) {
	reviewerID = inputParam;
	userID = reviewerID;
    }
    /** 
     * set the secondary reviewer identification 
     *
     * @param inputParam  input secondary reviewer id
     */
    public void setSecondaryReviewerID(int inputParam) {
	secondaryReviewerID = inputParam;
    }
    /** 
     * set the primary reviewer identification 
     *
     * @param inputParam  input primary reviewer id
     */
    public void setPrimaryReviewerID(int inputParam) {
	primaryReviewerID = inputParam;
    }


    /** 
     * set the user identification
     *
     * @param inputParam  input report type 
     */
    public void setUserID(int inputParam) {
	userID = inputParam;
        theUser = new User(userID,reportsDataPath);
    }

    /** 
     * set the Importance of science rating
     *
     * @param inputSI  input importance of science rating
     */
    public void setSI(String inputSI) {
        if (inputSI != null) {
	  scienceImportance = inputSI;
        }
    }

    /** 
     * set the Proposal Science Justification rating
     *
     * @param inputSJ  input science justification rating
     */
    public void setSJ(String inputSJ) {
        if (inputSJ != null) {
	  scienceJustification = inputSJ;
        }
    }

    /** 
     * set the Clarity of Formatting rating
     *
     * @param inputStr  input clarity of formatting rating
     */
    public void setFmtClarity(String inputStr) {
        if (inputStr != null) {
	  fmtClarity = inputStr;
        }
    }
    
    /** 
     * set the feasibility rating
     *
     * @param inputFeas  input feasiblity rating
     */
    public void setFeasibility(String inputFeas) {
        if (inputFeas != null) {
	  feasibility = inputFeas;
        }
    }

    /** 
     * set the feasibility of constraint preference rating
     *
     * @param inputFeas  input feasiblity rating
     */
    public void setFeasibilityConstraint(String inputFeas) {
        if (inputFeas != null) {
	  feasibilityConstraint = inputFeas;
        }
    }

    /** 
     * set the Use of Chandra Capability rating
     *
     * @param inputUse  input Use of Chandra Capability rating
     */
    public void setChandraUse(String inputUse) {
        if (inputUse != null) {
	  chandraUseCapability = inputUse;
      }
    }
    
    /** 
     * set the Clarity of Proposal rating
     *
     * @param inputClarity  input clarity of proposal rating
     */
    public void setClarity(String inputClarity) {
        if (inputClarity != null) {
	  clarity = inputClarity;
        }
    }

    /** 
     * set the Degree of Effort required for analysis
     *
     * @param inputEffort  input degree of effort
     */
    public void setEffort(String inputEffort) {
        if (inputEffort != null) {
	  degreeOfEffort = inputEffort;
        }
    }
    

    /** 
     * set the comments for "reason grade not higher"
     *
     * @param inputReason  input reason grade not higher comments
     */
    public void setGradeReason(String inputReason) {
        if (inputReason != null) {

	  reasonGradeNotHigher = inputReason.trim();
        }
    }

    /** 
     * set the technical review 
     *
     * @param inputTechnicalReview  input TechnicalReview comments
     */
    public void setTechnicalReview(String inputTechnicalReview) {
        if (inputTechnicalReview != null) {

	  technicalReview = inputTechnicalReview.trim();
        }
    }
    
    
    /** 
     * set the comments for "specific recommendations "
     *
     * @param inputRecs  input specific recommendations comments
     */
    public void setRecs(String inputRecs) {
        if (inputRecs != null) {
	  specificRecommendations = inputRecs.trim();
        }
    }
    
    /** 
     * set the general comments 
     *
     * @param inputComments  input comments
     */
    public void setComments(String inputComments) {
         
        if (inputComments != null) {
	  comments = inputComments.trim();
        }
    }

    /**
     * set the prelimComplete value
     *
     * @param inputPreComp  input prelimComplete flag
     */
    public void setPrelimComplete(String inputPreComp) {
        if (inputPreComp != null) {
            prelimComplete = inputPreComp;
        }
    }

    /**
     * This method sets the radio button value for "Good proposal, 
     * but all targets were allocated to higher ranked proposals"
     * which is only available on the peer review form.  This value will be
     * NULL when reading in the primary reports, which are used to
     * initialize the peer review forms. This will allow the radio buttons
     * to be blank in the peer review form when first viewed.
     *
     * @param input  input value
    */
    public void setTargetsTaken(String input) {
	if(input == null || input.equals("NULL")) {
	    targetsTaken = new String("");
	} else {
	    targetsTaken = input;
	}
    }
    
    /**
     * This method sets the radio button value for "Good proposal, 
     * but TOO observations are limited"
     * which is only available on the peer review form.  This value will be
     * NULL when reading in the primary reports, which are used to
     * initialize the peer review forms. This will allow the radio buttons
     * to be blank in the peer review form when first viewed.
     *
     * @param input  input value
    */
    public void setTOOLimited(String input) {
	if(input == null || input.equals("NULL")) {
	    TOOLimited = new String("");
	} else {
	    TOOLimited = input;
	}
    }

    /**
     * This method sets the radio button value for "Good proposal, 
     * but constrained observations are limited"
     * which is only available on the peer review form.  This value will be
     * NULL when reading in the primary reports, which are used to
     * initialize the peer review forms. This will allow the radio buttons
     * to be blank in the peer review form when first viewed.
     *
     * @param input  input value
    */
    public void setConstrained(String input) {
	if(input == null || input.equals("NULL")) {
	    constrainedLimited = new String("");
	} else {
	    constrainedLimited = input;
	}	
    }

    /**
     * Set flag indicating this report is only being used to get a
     * 'printer-friendly' version and should not be 'locked'. 
     *
     * @param input boolean value indicating this report is only for printing
     */
    public void setPrintOnly(boolean input) {
	printOnly = input;
    }



    /**
     * If the report is on more than 1 panel, this will add
     * the primary reviewer to the reviewers for this report.
     * This is needed because the primary reviewers from all panels
     * are allowed to edit this report.
     *
     * @param inputReviewerID  input reviewer id 
     */
    public void addPrimaryReviewer(int inputReviewerID) {
	additionalReviewers.add(new Integer(inputReviewerID));	
    }
    /**
     * If the report is on more than 1 panel, this will track
     * the additional panels this report is assigned to.  
     *
     * @param inputPanelName  input panel name
     */
    public void addPanelName(String inputPanelName) {
	additionalPanels.add(new String(inputPanelName));	
    }

    /** 
     * set the last updated reports date 
     *
     * @param theFile  input file 
     */
    private void setLastUpdate(File theFile) {
        if (theFile.exists()) {
          fileDate = new Date(theFile.lastModified());
          String datePattern = new String("dd MMM HH:mm");
          SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
          if (fileDate != null) {
	    lastUpdate = sdf.format(fileDate);
          }
       }
    }
    /**
     * Determine the status of the report based on the filename
     *
     * @param inReportsDataPath directory for all the webreport files
     */
    public void determineStatus(String inReportsDataPath) {
	reportsDataPath = new String(inReportsDataPath);
	determineStatus();
    }

    /**
     * Determine the status of the report based on the filename
     * The status will be blank unless the reports has  been 
     * approved by the reviewer, chair/deputy-chair, or CDO.
     *
     */
    public void determineStatus() {
	String checkedOffReport = getFilename(ReportsConstants.CHECKOFF);
	String completedReport = getFilename(ReportsConstants.COMPLETE);
	String savedReport = getFilename(ReportsConstants.SAVE);
	String finalizedReport = getFilename(ReportsConstants.FINALIZE);
	String reportFilename = new String(savedReport);

        //LogMessage.println("determineStatus:" +
		//"checkedOff: " + checkedOffReport + "\n" +
		//"completed: " + completedReport + "\n" +
		//"saved: " + savedReport + "\n" +
		//"finalized: " + finalizedReport );

	File checkedOffFile = new File(checkedOffReport);
	File completedFile = new File(completedReport);
	File savedFile = new File(savedReport);
	File finalizedFile = new File(finalizedReport);

	if(finalizedFile.exists()) {
	    status = ReportsConstants.FINALIZE;
            setLastUpdate(finalizedFile);
	    reportFilename = new String(finalizedReport);
	} else if(checkedOffFile.exists()) {
	    status = ReportsConstants.CHECKOFF;
            setLastUpdate(checkedOffFile);
	    reportFilename = new String(checkedOffReport);
	} else if(completedFile.exists()) {
	    status = ReportsConstants.COMPLETE;
            setLastUpdate(completedFile);
	    reportFilename = new String(completedReport);
	} else if(savedFile.exists()) {
	    fileDate = new Date(savedFile.lastModified());
            setLastUpdate(savedFile);
	}

	reportStatus = status;

	//LogMessage.println("ReviewReport: filename = " + reportFilename);
	
    }



    //*****************************************************
    //Get routines
    //*****************************************************
    /**
     *  get the current report data path 
     *
     * @return String current reportsDataPath
     */
    public String getDataPath() {
	return reportsDataPath;
    }



    /**
     *  get the proposal number
     *
     * @return String proposal number
     */
    public String getProposalNumber() {
	return(proposal.getProposalNumber());
    }

    /**
     *  get the proposal joint
     *
     * @return String proposal joint
     */
    public String getProposalJoint() {
	return(proposal.getJoint());
    }

    /**
     *  get the proposal multicycle
     *
     * @return String proposal multicycle
     */
    public String getProposalMulticycle() {
	return(proposal.getMulticycle());
    }


    /**
     *  get the report type
     *
     * @return String report type 
     */
    public String getType() {
	return type;
    }

    /**
     *  get the principal investigator (P.I.) 
     *
     * @return String P.I.
     */
    public String getPI() {
	return(proposal.getPI());
    }

    /**
     *  get the proposal title
     *
     * @return String proposal title
     */
    public String getTitle() {
	return(proposal.getTitle());
    }

    /**
     *  get the proposal science category
     *
     * @return String proposal science category
     */
    public String getCategory() {
	return (proposal.getCategory());
    }

    /**
     *  get the proposal type
     *
     * @return String proposal type
     */
    public String getProposalType() {
	return(proposal.getProposalType());
    }
    /**
     *  get the linked proposal number
     *
     * @return String linked proposal number
     */
    public String getLinkedProposalNumber() {
	return(proposal.getLinkedProposalNumber());
    }

    /**
     *  get the constrained targets. 
     *
     * @return String constrained targets
     */
    public String getConstrainedTargets() {
	return(proposal.getConstrainedTargets());
    }
    public boolean isNewerCmt() {
      boolean retval = false;
      try {
        String fullpath =  getDataPath();
        fullpath += "cmtedits/";
        String fname= panelName;
        if (isLP()) {
          fname=ReportsConstants.BPP_PANEL;
        }
        fname +=   "_" + proposal.getProposalNumber() + ".cmts";
        fullpath += fname;
        File theFile = new File(fullpath);
        if (theFile.exists()) {
          if (fileDate != null) {
            Date theDate = new Date(theFile.lastModified());
            retval = theDate.after(fileDate);
            LogMessage.println("ReviewReport:: " + fullpath + ": " +  fileDate.toString() + " cmt " + theDate.toString() );
          } else {
            LogMessage.println("ReviewReport::isNewerCmt report file date is null for " + fullpath);
            retval = true;
          }
        }
      }
      catch (Exception exc) {
            LogMessage.println("ReviewReport:isNewerCmt caught exception.") ;
            LogMessage.printException(exc);
      }

      return retval;
    }

    /**
     *  get the date for when the report was last updated
     *
     * @return String  last update date
     */
    public String getLastUpdate() {
	return lastUpdate;
    }
    public boolean isOlder(Date fdate) {
        boolean retval = false;

        if (fdate != null && fileDate != null) {
          try {
            retval = fdate.after(fileDate);
          }
          catch (Exception exc) {
            LogMessage.println("ReviewReport:isOlder caught exception.") ;
            LogMessage.printException(exc);
          }
        }

	return retval;
    }

    /**
     *  get the original report status. This is the status when
     *  the report file is read from disk.
     *
     * @return String original report status 
     */
    public String getStatus() {
	return status;
    }
    /**
     *  get the current report status 
     *
     * @return String current report status
     */
    public String getReportStatus() {
	return reportStatus;
    }


    /**
     * get the associated files for the proposal.  These files
     * are the science justification, RPS form, conflict, technical
     * reviews, etc..
     *
     * @return Vector associated proposal files
     */
    public Vector<ProposalFile> getProposalFiles() {
	return(proposal.getProposalFiles());
    }

    /**
     * get the science justification filename for this proposal
     *
     * @return String science justification filename
     */
    public String getScienceJustFile() {
	return(proposal.getScienceJustFile());
    }
    
    /**
     * get the RPS parameter form filename for this proposal
     *
     * @return String RPS parameter form filename
     */
    public String getRPSParamFile() {
	return(proposal.getRPSParam());
    }

    /**
     * get the RPS merged filename for this proposal
     *
     * @return String merged form filename
     */
    public String getMergedFile() {
	return(proposal.getMergedFile());
    }


    /**
     * get the Technical filename for this proposal or null
     *
     * @return String technical review filename
     */
    public String getTechnicalFile() {
	return(proposal.getTechnicalFile());
    }
    /**
     * get the Proposer Input filename for this proposal or null
     *
     * @return String Proposer input filename
     */
    public String getProposerInputFile() {
	return(proposal.getProposerInputFile());
    }
    
    /**
     * get the panel name for this review report
     *
     * @return String panel name 
     */
    public String getPanelName() {
	return panelName;
    }

    /**
     * get the panel id for this review report
     *
     * @return int panel id 
     */
    public int getPanelID() {
	return panelID;
    }
    
    /**
     * get the reviewer name for this review report
     *
     * @return String reviewer name 
     */
    public String getReviewerName() {
	return reviewerLastName;
    }

    /**
     * get the reviewer id for this review report
     *
     * @return int reviewer id 
     */
    public int getReviewerID() { 
	return reviewerID;
    }

    /**
     * get the secondary reviewer name for this review report
     *
     * @return String secondary reviewer name 
     */
    public String getSecondaryReviewerName() {
	return secondaryLastName;
    }
    /**
     * get the primary reviewer name for this review report
     *
     * @return String primary reviewer name 
     */
    public String getPrimaryReviewerName() {
	return primaryLastName;
    }

    /**
     * get the user ID of person viewing/editing report.  This may
     * be different than the reviewer assigned to the report.
     *
     * @return int user id
     */
    public int getUserID() { 
	return userID;
    }

    /**
     * get all the panels this proposal is assigned to.
     *
     * @return Vector  panel names
     */
    public Vector<String> getExtraPanels() {
        return additionalPanels;
    }

    /**
     * get all the panels this proposal is assigned to in a
     * comma separated string.
     *
     * @return String  panel names, comma separated
     */
    public String getPanels() {
      String panels = new String(panelName);
      for (int ii=0; ii < additionalPanels.size(); ii++) {
        panels += ",";
        panels += ((String)additionalPanels.get(ii));
      }
      return panels;
    }



    /**
     * get the importance of science rating
     *
     * @return String importance of science rating
     */
    public String getSI() {
	return scienceImportance;
    }

    /**
     * get the proposal science justification rating
     *
     * @return String proposal science justification rating
     */
    public String getSJ() {
	return scienceJustification;
    }
    /**
     * get the clarity of formatting  rating
     *
     * @return String clarity of formatting  rating
     */
    public String getFmtClarity() {
	return fmtClarity;
    }
    
    /**
    
    /**
     * get the feasiblity rating
     *
     * @return String feasiblity rating
     */
    public String getFeasibility() {
	return feasibility;
    }

    /**
     * get the feasiblity of science rating
     *
     * @return String feasiblity rating
     */
    public String getFeasibilityConstraint() {
	return feasibilityConstraint;
    }

    /**
     * get the use of Chandra capability rating
     *
     * @return String use of Chandra capability rating
     */
    public String getChandraUse() {
	return chandraUseCapability;
    }
    
    /**
     * get the clarity of proposal rating
     *
     * @return String clarity of proposal rating
     */
    public String getClarity() {
	return clarity;
    }

    /**
     * get the degree of effort rating
     *
     * @return String degree of effort rating
     */
    public String getEffort() {
	return degreeOfEffort;
    }
    
    /**
     * get the reason grade not higher comments
     *
     * @return String comments
     */
    public String getGradeReason() {
	return reasonGradeNotHigher;
    }
    /**
     * get the reason grade not higher comments.
     * This will wrap comments in 79 character line length.
     *
     * @return String comments
     */
    public String getGradeReasonWrapped() {
        String returnStr = fu.getWrapped(reasonGradeNotHigher);
        return returnStr;
    }

    /**
     * get the technical review comments
     *
     * @return String comments
     */
    public String getTechnicalReview() {
	return technicalReview;
    }
    /**
     * get the technical review comments
     * This will wrap comments in 79 character line length.
     *
     * @return String comments
     */
    public String getTechnicalReviewWrapped() {
        String returnStr = fu.getWrapped(technicalReview);
        return returnStr;
    }
    
    
    /**
     * get the specific recommendation comments.
     *
     * @return String comments
     */
    public String getRecs() {
	return specificRecommendations;
    }
    /**
     * get the specific recommendation comments.
     * This will wrap comments in 79 character line length.
     *
     * @return String comments
     */
    public String getRecsWrapped() {
        String returnStr = fu.getWrapped(specificRecommendations);
        return returnStr;
    }
    
    /**
     * get the  general comments.
     *
     * @return String comments
     */
    public String getComments() {
	return comments;
    }
    /**
     * get the general comments.
     * This will wrap comments in 79 character line length.
     *
     * @return String comments
     */
    public String getCommentsWrapped() {
        String returnStr = fu.getWrapped(comments);
        return returnStr;
    }

    /**
     * get the prelimComplete value
     *
     * @return String prelimComplete
     */
    public String getPreComp() {
        return prelimComplete;
    }
    /**
     * This method returns the  value for "Good proposal, 
     * but all targets were allocated to higher ranked proposals"
     *
     * @return String targets taken boolean value 
     */
    public String getTargetsTaken() {
	String returnString = targetsTaken;

	if(returnString.length() == 0) {
	    returnString = ReportsConstants.NONE;
	} 

	return returnString;
    }


    /**
     * This method returns the  value for 
     * "Good proposal, but TOO observations are limited"
     *
     * @return String TOOs limited boolean value 
     */
    public String getTOOLimited() {
	String returnString = TOOLimited;
	
	if(returnString.length() == 0) {
	    returnString = ReportsConstants.NONE;
	} 
	return returnString;
    }

    /**
     * This method returns the  value for 
     * "Good proposal, but constrained observations are limited"
     *
     * @return String constrained limited boolean value 
     */
    public String getConstrained() {
	String returnString = constrainedLimited;
	
	if(returnString.length() == 0) {
	    returnString = ReportsConstants.NONE;
	} 
	return returnString;
    }

    //Other helpful routines

    /**
     * determine the number of primary review reports that exist
     *
     * @param list all review reports
     * @return int number of primary review reports
     */
    public static int getNumPrimary(Vector<ReviewReport> list) {
      int numPrimary = 0;
      if (list != null) {
	int listSize = list.size();
	for(int index=0; index < listSize; index++) {
	    ReviewReport rr = (ReviewReport)list.get(index);
	    String rrType = rr.getType();
	    if(rrType.equals(ReportsConstants.PRIMARY) ||
	       rrType.equals(ReportsConstants.PEER)) {
		numPrimary++;
	    }
	}
      }
      return numPrimary;
    }

    /**
     * determine the number of secondary review reports that exist
     *
     * @param list all review reports
     * @return int number of secondary review reports
     */
    public static int getNumSecondary(Vector<ReviewReport> list) {

      int numSecondary = 0;
      if (list != null) {
	int listSize = list.size();
	for(int index=0; index < listSize; index++) {
	    ReviewReport rr = (ReviewReport)list.get(index);
	    String rrType = rr.getType();
	    if(rrType.equals(ReportsConstants.SECONDARY) || 
	       rrType.equals(ReportsConstants.SECONDARYPEER)) {
		numSecondary++;
	    }
	}
      }
      return numSecondary;
    }



    /**
     *  determines if report has been approved by the Reviewer
     *
     * @return boolean value indicating reviewer has approved the report
     */
    public boolean isCompleted() {
	if(status.equals(ReportsConstants.COMPLETE)) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     *  determines if report has been approved by the Panel
     *
     * @return boolean value indicating Panel has approved the report
     */
    public boolean isCheckedOff() {
	if(status.equals(ReportsConstants.CHECKOFF)) {
	    return true;
	} else {
	    return false;
	}
    }
    /**
     *  determines if report has been approved by the CDO
     *
     * @return boolean value indicating CDO has approved the report
     */
    public boolean isFinalized() {
	if(status.equals(ReportsConstants.FINALIZE)) {
	    return true;
	} else {
	    return false;
	}
    }
    
    /**
     *  determines if report is a primary review report
     *
     * @return boolean value indicating this is a primary review report
     */
    public boolean isPrimary() {
	if(type.equals(ReportsConstants.PRIMARY)) {
	    return true;
	} else {
	    return false;
	}
    }


    /**
     *  determines if report is a secondary review report
     *
     * @return boolean value indicating this is a secondary review report
     */
    public boolean isSecondary() {
	if(type.equals(ReportsConstants.SECONDARY)) {
	    return true;
	} else {
	    return false;
	}
    }


    /**
     *  determines if report is a secondary peer review report
     *
     * @return boolean value indicating this is a secondary peer review report
     */
    public boolean isSecondaryPeer() {
	if(type.equals(ReportsConstants.SECONDARYPEER)) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     *  determines if report is a peer review report
     *
     * @return boolean value indicating this is a peer review report
     */
    public boolean isPeer() {
	if(type.equals(ReportsConstants.PEER)) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     *  determines if report is a final review report
     *  This is the report that combines the peer review reports for
     *  thos proposals assigned to more than 1 panel.
     *
     * @return boolean value indicating this is a final review report
     */
    public boolean isLP() {
	if(type.equals(ReportsConstants.LP)) {
	    return true;
	} else {
	    return false;
	}
    }


    /***
     * addLastModified
     * 
     * This routine will add a date timestamp and the user ID of the user who 
     * has modified the file. It will be used by the uncomplete and uncheckoff 
     * routines, so the file knows time and the last person to modify it.
     *
     * @param currentFile   current report file
     * @param userName      current reviewer
     * @param mode          operation being performed  
     * @return boolean      true if successful
     */
    private boolean addLastModified(File currentFile, String userName, String mode) {
	boolean returnVal = true;
	
	try {
	    if(!currentFile.exists()) {
		returnVal = false;
	    }
	    
	    //Read in the original file
	    FileReader fileR = new FileReader(currentFile);
	    BufferedReader currentFileBR = new BufferedReader(fileR);
	    String inputLine = null;
	    Vector<String> inputFile = new Vector<String>();
	    
	    while( (inputLine = currentFileBR.readLine()) != null) {
		inputFile.add(inputLine);
	    }
	    currentFileBR.close();
	    fileR.close();

	    
	    //Now get the same file, to write to output and add user information
	    FileWriter outputFW = new FileWriter(currentFile);
	    PrintWriter outputPW = new PrintWriter(outputFW);
	    outputPW.print(ReportsConstants.INFO + ":");
	    outputPW.println("Last operation: " + mode + " by " + userName + " at " + new Date());
	    
	    while(inputFile.size() > 0) {
		String currentLine = (String)inputFile.get(0);
		outputPW.println(currentLine);
		inputFile.removeElementAt(0);
	    }

	    outputPW.close();
	    outputFW.close();

	} catch(Exception ex) {
	    ex.printStackTrace();
	    LogMessage.println("Caught exception in ReviewReport:saveVersion");
	}


	return returnVal;
    }


    /**
     * This routine will be called by the UpdateReportServlet, when a user 
     * clicks the 'Save' button to save the report.  The actual writing of 
     * the file will be handled by the writeReport routine.
     *
     * @param userID  user id of person editing report
     * @param userName name of person editing report
     * @param inReportsDataPath directory path of webreport files
     * @return boolean value indicating report successfully saved
     */
    public boolean saveReport(int userID, String userName, String inReportsDataPath) {
	reportsDataPath = new String(inReportsDataPath);

        // get the filename for the current status
	String filename = getFilename(reportStatus);

        // get the filename for the old status
	String oldFilename = getFilename(status);
	LogMessage.println("Backup: " + oldFilename + " oldStatus" + status);
        backupFile(oldFilename);
        
	LogMessage.println("Saving report for " + userName + " - " + filename);
	boolean success = writeReport(userID, userName, filename);
        if (success) {
           LogMessage.println("Setting new status: "  + reportStatus + " for " + filename);
           status=reportStatus;
           File newFile = new File(filename);
	   success = addLastModified(newFile, userName, reportStatus);
           setLastUpdate(newFile);
        }
	return success;
    }


    /**
     * This routine will be called when a report has timed out for lack
     * of activity.  It saves the report to a backup file just in case
     * the user may want it.
     *
     * @param operation  operation value that called this routine
     * @param inReportsDataPath directory path of webreport files
     * @param userID  user id of person editing report
     * @param userName name of person editing report
     * @return boolean value indicating report successfully saved
     */
    public boolean writeUnsavedReport(String operation, String inReportsDataPath, int userID, String userName) {
	boolean success = false;
	reportsDataPath = new String(inReportsDataPath);
	boolean useReviewerID = false;
	if(operation == ReportsConstants.TIMEDOUTREP) {
	    useReviewerID = true;
	}

	String filename = getFilename(operation, ReportsConstants.TMPDIR, useReviewerID);
	LogMessage.println("Saving report which wasn't saved: " + filename);

	success = writeReport(userID, userName, filename);
	return success;
    }

    /**
     * This routine is called to rename(move) a file to the TMPDIR .
     *
     * @param filename full file name
     * @return boolean value indicating file successfully moved
     */
    public boolean backupFile(String filename)
    {
      boolean retval = true;
      StringBuffer newFilePath;

      int lastSlash = filename.lastIndexOf('/');      
      if (lastSlash > 0 && filename.lastIndexOf("/tmp/") < 0) {
        newFilePath = new StringBuffer(filename.substring(0,lastSlash));
        newFilePath.append("/" + ReportsConstants.TMPDIR );
        newFilePath.append(filename.substring(lastSlash));
      } 
      else {
        newFilePath = new StringBuffer(filename);
      }

      //date pattern is: day of week (3 chars)_month(3 chars)_
      String datePattern = new String("EEE_MMM_d_H:mm:SS");

      Date today = new Date();
      SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
      String filenameExt = formatter.format(today);
      newFilePath.append("." + filenameExt);

      File outputFile = new File(filename);
      if (outputFile.exists()) {
        File newFile = new File(newFilePath.toString());
        boolean renamedFile = outputFile.renameTo(newFile);
        if(!renamedFile) {
          //throw an exception
          LogMessage.println("Cannot rename file: " + filename + " to " + newFilePath);
          retval = false;
        }
      }
      return retval;

    }

    /**
     * This routine is called by the completeReport, checkoffReport and saveReport
     * routines, to handle the actual writing of the report to a file.
     * The filename will be passed in, and the routine moves the file
     * if it already exists.  Then it saves all the information to the
     * input filename. 
     * @param userID  database user id
     * @param userName  user name
     * @param filename  name of report file
     * @return boolean  true on success 
     */
    private boolean writeReport(int userID, String userName, String filename) {
	
        boolean retval = false;
	//Check if the file already exists, and if so, rename it
        if ( !backupFile(filename)) {
	  LogMessage.println("Unable to backup " + filename );
	}
        else {
	  // now write the new file
	  try {
	    FileWriter outputFW = new FileWriter(filename.toString());
	    PrintWriter outputPW = new PrintWriter(outputFW);

	    //int userID, String userName
	    outputPW.print(ReportsConstants.INFO + ":");
	    outputPW.println("Last updated by user ID = " + userID + " : user name = " + userName);

            retval = printReport(outputPW,false);
	    outputPW.close();
	    outputFW.close();
            FileUtils.setPermissions(filename,"660");

	  } catch(Exception ex) {
            ex.printStackTrace();
	    LogMessage.println("Caught exception in printReport function");
            retval = false;
	  }

        }
      return retval;
    }
  
     /**
      * This method prints the content of the report 
      *
      * @param outputPW  print writer
      * @param wrapLines value indicating if comment lines should be wrapped
      * @return boolean value indicating report successfully printed
      */
     public boolean printReport(PrintWriter outputPW,boolean wrapLines)
     {
          boolean retval = false;

          try {
	    outputPW.print("Review: ");
	    if(isLP()) {
		outputPW.println("Chandra Final Review Form");
	    } else {
		outputPW.println("Chandra " + type + " Review Form\n");
            }

            String padFormat = new String ("%-19.19s");

	    //If the report is an LP report, then it's a final review.
	    outputPW.format(padFormat,ReportsConstants.REVIEWFORM);
	    outputPW.print(" = ");
	    if(isLP()) {
		outputPW.println("Final");
	    } else {
		outputPW.println(type);
	    }

            outputPW.format(padFormat,ReportsConstants.PROPNUMBER);
	    outputPW.print(" = ");
	    outputPW.println(proposal.getProposalNumber());

	    outputPW.format(padFormat,ReportsConstants.PANEL);
	    outputPW.print(" = ");
	    outputPW.println(getPanelName());

	    outputPW.format(padFormat,ReportsConstants.CATEGORY);
	    outputPW.print(" = ");
	    outputPW.println(proposal.getCategory());
	    
	    outputPW.format(padFormat,ReportsConstants.PINAME);
	    outputPW.print(" = ");
	    outputPW.println(proposal.getPI());
	    
	    outputPW.format(padFormat,ReportsConstants.TITLE);
	    outputPW.print(" = ");
	    outputPW.println(proposal.getTitle());
	    
	    outputPW.format(padFormat,ReportsConstants.PROPTYPE);
	    outputPW.print(" = ");
	    outputPW.println(proposal.getProposalType());
	    
	    outputPW.format(padFormat,ReportsConstants.MULTICYCLE);
	    outputPW.print(" = ");
	    outputPW.println(proposal.getMulticycle());

	    outputPW.format(padFormat,ReportsConstants.JOINT);
	    outputPW.print(" = ");
	    outputPW.println(proposal.getJoint());

	    outputPW.format(padFormat,ReportsConstants.CONSTTARGET);
	    outputPW.print(" = ");
	    outputPW.println(proposal.getConstrainedTargets());

	    outputPW.println("___________________________");
	    outputPW.println("Review Report:");
	    
            padFormat = "%-56.56s";
            outputPW.format(padFormat,ReportsConstants.PRELIMCOMPLETE);
            outputPW.print(" = ");
            outputPW.println(prelimComplete);

	    outputPW.format(padFormat,ReportsConstants.SCIENCEIMPORTANCE);
	    outputPW.print(" = ");
	    outputPW.println(scienceImportance);
	    
	    outputPW.format(padFormat,ReportsConstants.SCIENCEJUSTIFICATION);
	    outputPW.print(" = ");
	    outputPW.println(scienceJustification);

	    outputPW.format(padFormat,ReportsConstants.FEASIBILITY);
	    outputPW.print(" = ");
	    outputPW.println(feasibility);

	    outputPW.format(padFormat,ReportsConstants.FEASIBILITYCONSTRAINT);
	    outputPW.print(" = ");
	    outputPW.println(feasibilityConstraint);
	    
	    outputPW.format(padFormat,ReportsConstants.CAPABILITY);
	    outputPW.print(" = ");
	    outputPW.println(chandraUseCapability);
	    
	    outputPW.format(padFormat,ReportsConstants.CLARITY);
	    outputPW.print(" = ");
	    outputPW.println(clarity);

	    outputPW.format(padFormat,ReportsConstants.FMTCLARITY);
	    outputPW.print(" = ");
	    outputPW.println(fmtClarity);
	    
	    
            if (!isPrimary()  && !isSecondary()) {
	      outputPW.print(ReportsConstants.HIGHERRANKED);
	      outputPW.print(" = ");
	      outputPW.println(targetsTaken);
	    
	      outputPW.print(ReportsConstants.TOO);
	      outputPW.print(" = ");
	      outputPW.println(TOOLimited);
	    
	      outputPW.print(ReportsConstants.CONSTRAINED);
	      outputPW.print(" = ");
	      outputPW.println(constrainedLimited);
	    }
  
	    outputPW.print(ReportsConstants.COMMENTS);
	    outputPW.println(" = ");
            if (wrapLines) {
              outputPW.println(getCommentsWrapped());
            } else {
	      outputPW.println(comments);
            }
	    outputPW.println(ReportsConstants.ENDCOMMENTS);
            outputPW.println();
	    
	    outputPW.print(ReportsConstants.RECOMMENDATIONS);
	    outputPW.println(" = ");
            if (wrapLines) {
              outputPW.println(getRecsWrapped());
            } else {
	      outputPW.println(specificRecommendations);
            }
	    outputPW.println(ReportsConstants.ENDRECS);
            outputPW.println();
	    
	    //outputPW.print(ReportsConstants.REASON);
	    //outputPW.println(" = ");
            //if (wrapLines) {
              //outputPW.println(getGradeReasonWrapped());
            //} else {
	      //outputPW.println(reasonGradeNotHigher);
            //}
	    //outputPW.println(ReportsConstants.ENDREASON);
            //outputPW.println();

            if (technicalReview != null && technicalReview.length() > 2) {
	      outputPW.print(ReportsConstants.TECHRPT);
	      outputPW.println(" = ");
              if (wrapLines) {
                outputPW.println(getTechnicalReviewWrapped());
              } else {
	        outputPW.println(technicalReview);
              }
	      outputPW.println(ReportsConstants.ENDTECHRPT);
              outputPW.println();
            }
	    
	    outputPW.print(ReportsConstants.EFFORT);
	    outputPW.print(" = ");
	    outputPW.println(degreeOfEffort);
	    outputPW.println(ReportsConstants.EFFORT2);
	    
	    outputPW.println("\n__________End_Review_Report__________");
            retval = true;
	  } catch(Exception ex) {
            ex.printStackTrace();
	    LogMessage.println("Caught exception in printReport function");
	  }

	return retval;
    }
  

    /**
     * This method determines the full filename for this report
     * 
     * @param operation  status of the report
     * @param directory  directory path to use 
     * @param useReviewerID  value indicating to use the reviewer id instead of the user id
     * @return String full file name
     */
    private String getFilename(String operation, String directory, boolean useReviewerID) {
	String proposalNumber = getProposalNumber();
	String panelName = getPanelName();
	String filenameExtension = ReportsConstants.getExtension(type);
	StringBuffer filenamePath = new StringBuffer(reportsDataPath);
	if(directory != null) {
	    filenamePath.append("/" + directory);
	}
	
	
	// The timed out files created when a user times out needs the reviewer
	// ID in the filename, so it can be reloaded only when that user logs 
        // in again.
	if(useReviewerID) {
	    if (type.equals(ReportsConstants.FINAL) || 
                type.equals(ReportsConstants.LP)) {
		filenamePath.append("/bpp_" + reviewerID + "_" + proposalNumber + filenameExtension);
	    }
	    else {
		filenamePath.append("/" + reviewerID + "_" + panelName + "_" + proposalNumber + filenameExtension);
	    }

	} else {
	    if (type.equals(ReportsConstants.FINAL) || 
                type.equals(ReportsConstants.LP)) {
		filenamePath.append("/bpp_" + proposalNumber + filenameExtension);
	    }
	    else {
		filenamePath.append("/" + panelName + "_" + proposalNumber + filenameExtension);
	    }
	}


	if(operation.equals(ReportsConstants.COMPLETE)) {
	    filenamePath.append("." + ReportsConstants.COMPLETE);
	} else if(operation.equals(ReportsConstants.CHECKOFF)) {
	    filenamePath.append("." + ReportsConstants.CHECKOFF);
	} else if(operation.equals(ReportsConstants.UNSAVED)) {
	    filenamePath.append("." + ReportsConstants.UNSAVED);
	} else if(operation.equals(ReportsConstants.FINALIZE)) {
	    filenamePath.append("." + ReportsConstants.FINALIZE);
        } else if(operation.equals(ReportsConstants.TIMEDOUTREP)) {
	    filenamePath.append("." + ReportsConstants.TIMEDOUTREP);
        } 

	return(filenamePath.toString());
    }    


    /****
     *
     * This routine takes the type of operation as input, and determines the
     * filename for this report.
     * @param operation time of file operation
     * @param directory directory to search
     * @return String filename of report
     */
    private String getFilename(String operation, String directory) {
	boolean useReviewerID = false;
	return(getFilename(operation, directory, useReviewerID));
    }

    /****
     *
     * This routine takes the type of operation as input, and determines the
     * filename for this report.
     * @param operation time of file operation
     * @return String filename of report
     */
    private String getFilename(String operation) {
	return(getFilename(operation, null));
    }

    /****
     *
     * This routine is used to get the filename of a peer review report 
     * for an lp report, to be loaded the first time the lp report is viewed.
     * @param panelName  panel name of report
     * @param proposalNumber   proposal number of report
     * @return File return the File
     */
    private File getLPFilename(String panelName, String proposalNumber) {
	String filenameExtension = ReportsConstants.getExtension(ReportsConstants.PEER);
	String filenamePath = new String(reportsDataPath);
        filenamePath += "/" + panelName + "_" + proposalNumber + filenameExtension;
	String finalFilename = new String(filenamePath + "." + ReportsConstants.FINALIZE);
	String completedFilename = new String(filenamePath + "." + ReportsConstants.COMPLETE);
	String checkedOffFilename = new String(filenamePath + "." + ReportsConstants.CHECKOFF);

	File completedFile = new File(completedFilename);
	File checkedOffFile= new File(checkedOffFilename);
	File finalFile= new File(finalFilename);
	
	if(finalFile.exists()) {
	    filenamePath = finalFilename;
        } else if(checkedOffFile.exists()) {
	    filenamePath = checkedOffFilename;
	} else if(completedFile.exists()) {
	    filenamePath = completedFilename;
	}

	return(new File(filenamePath));
    }


    /**
     * This routine determines if a timed out file exists.
     *
     * @return boolean value indicating a timed out file exists.
     */
    public boolean timedOutFileExists() {
	if(timedOutFilename != null && timedOutFilename != "") {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * gets the filename of the timed out file that has been saved 
     *
     * @return String timed out filename
     */
    public String getTimedOutFilename() {
	return timedOutFilename;
    }

    /**
     * sets the filename of the timed out file that has been saved 
     *
     * @param inTimedOutFilename timed out filename
     */
    public void setTimedOutFilename(String inTimedOutFilename) {
	timedOutFilename = new String(inTimedOutFilename);
    }
    



    /**
     * Method returns vector of peer reports for proposals on more than 1 panel
     *
     * @return Vector  peer report filenames 
     */
    public Vector<String> getLPPeerReportNames() {
	return peerForLPPanels;
    }
    

    /**
     * Determine name for the peer review report which is based on
     * the file extension
     *
     * @return String peer review report filename
     */
    public String getPeerRevReportName() {
	//Change the type of the report to determine the filename of the peer 
        //review report
	String saveType = type;
	type = ReportsConstants.PEER;
	String primaryReportFilename = getFilename(ReportsConstants.CHECKOFF);
	File primaryReportFile = new File(primaryReportFilename);
	if(!primaryReportFile.exists()) {
	  primaryReportFilename = getFilename(ReportsConstants.COMPLETE);
	  primaryReportFile = new File(primaryReportFilename);
	  if(!primaryReportFile.exists()) {
	    primaryReportFilename = getFilename(ReportsConstants.SAVE);
	  }
	}

	type = saveType;
	return primaryReportFilename;
    }

    /**
     * Determine name for the primary review report which is based on
     * the file extension
     *
     * @return String primary review report filename
     */
    public String getPriRevReportName() {
	//Change the type of the report to determine the filename of the primary review report
	String saveType = type;
	type = ReportsConstants.PRIMARY;
	String primaryReportFilename = getFilename(ReportsConstants.COMPLETE);

	File primaryReportFile = new File(primaryReportFilename);
	if(!primaryReportFile.exists()) {
	    primaryReportFilename = getFilename(ReportsConstants.SAVE);
	}

	type = saveType;
	return primaryReportFilename;
    }

    /***
     *
     * This routine determines the filename for the secondary review report, 
     * which is accessed via a link on the peer review report.  
     *
     * @return String secondary review report filename
     */
    public String getSecRevReportName() {
	String proposalNumber = getProposalNumber();
	

	//Change the type of the report, to determine the filename 
	//of the secondary review report
	String saveType = type;
	type = ReportsConstants.SECONDARY;
	String secondaryReportFilename = new String("");

	secondaryReportFilename = getFilename(ReportsConstants.COMPLETE);

	File secondaryReportFile = new File(secondaryReportFilename);
	if(!secondaryReportFile.exists()) {
	   secondaryReportFilename = getFilename(ReportsConstants.SAVE);
	}

	//Reset the values we changed.
	type = saveType;
	return secondaryReportFilename;
    }
    /**
     * Determine name for the special pundit LP panel
     *
     * @return String pundit panel review report filename
     */
    public String getPriPunditReportName() {
        
        String proposalNumber = getProposalNumber();
        String priPunditReportName = reportsDataPath + "/";
        priPunditReportName += "LP_" + proposalNumber + ReportsConstants.PRIMARYEXT;
       
	return priPunditReportName;
    }


    public boolean loadReport(String userName, String inReportsDataPath, String propFileDir,
			      boolean beforePR) { 
	return(loadReport(userName, inReportsDataPath, propFileDir,beforePR,
			  true));

    }

    /***
     * 
     * This routine will create a lock file, if necessary, and then either load
     * data from the file or get data from the database.
     *
     * @param userName  reviewer name
     * @param inReportsDataPath directory for reports area 
     * @param propFileDir directory for reports files 
     * @param beforePR true for preliminary reports view
     * @param allowedToEdit true for user allowed to edit report
     * @return boolean true, if successfule
     */
    public boolean loadReport(String userName, String inReportsDataPath, String propFileDir,
			      boolean beforePR, boolean allowedToEdit) { 
	DBConnection dbConnect = null;
	reportsDataPath = new String(inReportsDataPath);
	String proposalNumber = getProposalNumber();

	
	try {	

	    //If the type isn't defined, look it up in the db
	    if(type.equals("")) {
                dbConnect = new DBConnection(reportsDataPath, showDebug);
		type = dbConnect.getReportType(reviewerID, proposalNumber, beforePR);
                LogMessage.println("loadReport retrieved reportType from database for " +  proposalNumber + " reviewer=" +reviewerID);
	    }
	    
	    LogMessage.println("User " + userName + "(ID=" + reviewerID +  ") loading " 
			       + type + " report for proposal number " + proposalNumber);


	    //Determine the status of the report, either completed or checked-off
	    determineStatus();
	    String filename = null;

	    if(isFinalized()) {
		filename = getFilename(ReportsConstants.FINALIZE);
            } else if(isCheckedOff()) {
		filename = getFilename(ReportsConstants.CHECKOFF);
	    } else if(isCompleted()) {
		filename = getFilename(ReportsConstants.COMPLETE);
	    } else {
		filename = getFilename(ReportsConstants.SAVE);
	    }

	    LogMessage.println("opening report: " + filename);


	    //If the report is being viewed for the first time,
	    //initialize the report with the data from the primary
	    //review report
	    File report = new File(filename);
	    File primaryReviewReport = new File(getPriRevReportName());
            File secondaryReviewReport = new File(getSecRevReportName());



	  if(report.exists()) {
		readReport(report);
		
		if(isLP()) {
		    setLPReportLinks(proposalNumber);
		}
	  } else if(isLP()) {
		//The first time we're viewing an LP report, we need to initialize it
		//with the data from a peer review report.

		loadLPReport(proposalNumber,propFileDir);
           
          } else {
	    if( (type.equals(ReportsConstants.PEER) ||
			type.equals(ReportsConstants.SECONDARYPEER)) &&
		       primaryReviewReport.exists()) {
		//The first time the peer review report is opened, it should
		//load data from the primary review report. After this, the
		//report will exist as a .peer, and will be opened by the 
		//first if statement above.  In addition, cdo has requested
                //that the secondary comments be appended to the initial
                // report
                String comments2 = new String("");
                String specificRec2 = new String("");
                String reason2 = new String("");
                if (secondaryReviewReport.exists()) {
                   readReport(secondaryReviewReport);
                   if (comments != null && comments.length()>1) {
                     comments2 = "\n\nSecondary Review :\n";
                     comments2 += comments;
                   }
                   if (specificRecommendations != null && 
                       specificRecommendations.length()>1) {
                     specificRec2 = "\n\nSecondary Review :\n";
                     specificRec2 += specificRecommendations;
                   }
                   if (reasonGradeNotHigher != null && 
                       reasonGradeNotHigher.length()>1) {
                     reason2 = "\n\nSecondary Review :\n";
                     reason2 += reasonGradeNotHigher;
                   }
                }
		readReport(primaryReviewReport);
                comments += comments2;
                specificRecommendations += specificRec2;
                reasonGradeNotHigher += reason2;
              } else if( (type.equals(ReportsConstants.PEER) ||
                          type.equals(ReportsConstants.SECONDARYPEER)) &&
                        secondaryReviewReport.exists()) {
       
                readReport(secondaryReviewReport);

	      } else {
		//Report doesn't exist. Get proposal information from the database
		LogMessage.println("no report, get data from db for " + proposalNumber);
		if(dbConnect == null) {
		    dbConnect = new DBConnection(reportsDataPath, showDebug);
		}

		//Set the proposal to be the one returned from the db query
		proposal = dbConnect.getProposal(proposalNumber);

	      }
              // Initial Peer Report, go get the techreview
	      if (isPeer() || isSecondaryPeer()) {
                technicalReview =readTechReport(propFileDir,getTechnicalFile());
	      }
	    }
            // this request was withdrawn so don't do the extra db connection
   	    //if(dbConnect == null) {
              //dbConnect = new DBConnection(reportsDataPath, showDebug);
            //}
            //punditPrimaryLastName = dbConnect.getPrimaryPundit(proposalNumber);

	    //Check if there is a timed out report for this user and report
	    checkTimedOutReport();
	    
	} catch(Exception ex) {
	    LogMessage.println("Caught exception in loadReport:" + ex.getMessage());
	    ex.printStackTrace();
	}
	
      return true;
    }

    /**
     * This routine is used by the loadReport function to determine if 
     * there is a timed out report for this user and report.  If so, this 
     * function will set the timedOutFilename to the name of the file, 
     * which is then displayed in the report by the displayReport.jsp page.
     */
    private void checkTimedOutReport() {
	//LogMessage.println("In ReviewReport::checkTimedOutReport");
	boolean useReviewerID = true;

	timedOutFilename = getFilename(ReportsConstants.TIMEDOUTREP, ReportsConstants.TMPDIR, 
				       useReviewerID);
	LogMessage.println("Looking for file: " + timedOutFilename);
	File timedOutFile = new File(timedOutFilename);


	if(timedOutFile.exists()) {
	    //Move the timed out filename so we don't load it again
	    String oldTimedOutFilename = new String(timedOutFilename + ".OLD");
	    File oldTimedOutFile = new File(oldTimedOutFilename);
	    boolean movedFile = timedOutFile.renameTo(oldTimedOutFile);

	    
	    //if(movedFile) 
	    if(oldTimedOutFile.exists()) {
		LogMessage.println("Moved timed out file: " + timedOutFilename + " to: " +
				   oldTimedOutFilename);
	    } else {
		LogMessage.println("Error in User::checkTimedOutReport - Couldn't move " +
				   timedOutFilename + " to " + oldTimedOutFilename);
	    }

	    //Change the timedOutFilename string in this class to the
	    //name that the file has been moved to
	    timedOutFilename = oldTimedOutFilename;
	    
	} else {
	    //The timedOutFilename pointer should only exist if the file exists.
	    timedOutFilename = null;
	}
    }





    /***
     *
     * This routine will be used to set the link for the other peer review 
     * report for LP reports or reports assigned to more than 1 panel.
     *
     * @param proposalNumber proposal number
     */
    private void setLPReportLinks(String proposalNumber) {
	DBConnection dbConnect = null;

        additionalPanels.clear(); 
        peerForLPPanels.clear(); 
	try {
	    //Find the reviewer ids for this proposal
	    if(dbConnect == null) {
		dbConnect = new DBConnection(reportsDataPath, showDebug);
	    }
	    
	    dbConnect.getLPPanels(this, proposalNumber);
            for (int pidx=0; pidx < additionalPanels.size(); pidx++) {
              String panel1 = ((String)additionalPanels.get(pidx));
              if (!panel1.equals("LP")) {
		File peerReviewReport = getLPFilename(panel1, proposalNumber);
		String peerForLPFilename = new String("");
		if(peerReviewReport.exists()) {
		   peerForLPFilename = new String(reportsDataPath);
		   peerForLPFilename += "/" + peerReviewReport.getName();
		}
                peerForLPPanels.add(peerForLPFilename);
	      }
	    }
	} catch(Exception ex) {
	    LogMessage.println("ReviewReport:setLPReportLinks - Caught exception: ");
	    LogMessage.println(ex.getMessage());
	}
    }

    public void setLPReportLinks(String inReportsDataPath, String proposalNumber) {
	reportsDataPath = inReportsDataPath;	
	setLPReportLinks(proposalNumber);
    }

    
    /***
     * 
     * This routine is used to initialize an LP report with a peer review report
     * if it exists.  It will also set the otherReportFilename member variable,
     * which will be used in the displayReport.jsp to display a link to the
     * other peer review report, if it exists.
     * @param proposalNumber proposal number
     * @param propFileDir directory for report files
     */
    private void loadLPReport(String proposalNumber,String propFileDir) {

	try {
            setLPReportLinks(reportsDataPath,proposalNumber);
            String comments2 = new String("");
            String specificRec2 = new String("");
            String reason2 = new String("");
            for (int pidx=1; pidx < peerForLPPanels.size();pidx++) {
	      File otherPeerReviewReport = new File((String)peerForLPPanels.get(pidx));
              //LogMessage.println("LP REPORT: " +  (String)peerForLPPanels.get(pidx));
              String thisPanel = (String)additionalPanels.get(pidx);
	      if(otherPeerReviewReport.exists()) {
                  //LogMessage.println("EXISTS");
                  readReport(otherPeerReviewReport);
                  if (comments != null && comments.length()>1) {
                    comments2 += "\n\nPANEL " + thisPanel + " :\n";
                    comments2 += comments;
                  }
                  if (specificRecommendations != null && 
                      specificRecommendations.length()>1) {
                    specificRec2 += "\n\nPANEL " + thisPanel + " :\n";
                    specificRec2 += specificRecommendations;
                  }
                  if (reasonGradeNotHigher != null && 
                      reasonGradeNotHigher.length()>1) {
                    reason2 += "\n\nPANEL " + thisPanel + " :\n";
                    reason2 += reasonGradeNotHigher;
                  }
               }
               else {
	         //Set the proposal to be the one returned from the db query
                 LogMessage.println("Loading from database " + proposalNumber);
	         DBConnection dbConnect = null;
	         if(dbConnect == null) {
	            dbConnect = new DBConnection(reportsDataPath, showDebug);
	         }
	         proposal = dbConnect.getProposal(proposalNumber);
               }
	    } // end loading secondary peer info
            if (peerForLPPanels.size() > 0) {
              File lpPeerReviewReport = new File((String)peerForLPPanels.get(0));
              String thisPanel = (String)additionalPanels.get(0);
              if (lpPeerReviewReport.exists()) {
		readReport(lpPeerReviewReport);
              } else {
                comments="";
                specificRecommendations="";
                reasonGradeNotHigher="";
              }
              String concatCmt = "";
              if (comments != null && comments.length()>1) {
                 concatCmt += "PANEL " + thisPanel + " :\n";
              }
              concatCmt += comments + comments2;
              setComments(concatCmt);

              if (specificRecommendations != null && specificRecommendations.length()>1) {
                concatCmt="PANEL " + thisPanel +  " :\n";
              }
              else {
                concatCmt="";
              }
              concatCmt += specificRecommendations + specificRec2;
              setRecs(concatCmt);
              if (reasonGradeNotHigher != null && reasonGradeNotHigher.length()>1) {
                concatCmt = "PANEL " + thisPanel + " :\n";
              }
              else {
                concatCmt="";
              }
              concatCmt += reasonGradeNotHigher + reason2;
              setGradeReason(concatCmt);
	  } 
          else {
	      //Need to get the proposal information from the database.
	      LogMessage.println("ReviewReport::loadLPReport: No peer review reports exist for proposal " + proposalNumber + "... loading from database");
		    
	      //Set the proposal to be the one returned from the db query
	      DBConnection dbConnect = null;
	      if(dbConnect == null) {
	         dbConnect = new DBConnection(reportsDataPath, showDebug);
	      }
	      proposal = dbConnect.getProposal(proposalNumber);
         }
         technicalReview =readTechReport(propFileDir,getTechnicalFile());
		
	} catch(Exception ex) {
	    LogMessage.println("ReviewReport::loadLPReport: Caught exception ");
	    LogMessage.println(ex.getMessage());
	}
    }



    /**
     * read the specified file into the review report object
     * @param filename  full path of report file name
     */
    public void readReportFromFile(String filename) {
	File reportFile = new File(filename);
	readReport(reportFile);
    }


    /**
     * This routine will open the existing report file and set this object's 
     * member variables according to the file
     * @param report  the report file
     */
    private void readReport(File report) {
	try {
	    FileReader fileR = new FileReader(report);
	    BufferedReader reportBF = new BufferedReader(fileR);
	    String inputLine;
	    String[] inputArray;
	    int numElements;
	    String paramName;
	    String paramValue = new String();

	    while( (inputLine = reportBF.readLine()) != null) {
		inputArray = inputLine.split("=",2);
		numElements = inputArray.length;
		paramName = (inputArray[0]).trim();

		if(numElements == 2) {
		    paramValue = (inputArray[1]).trim();
		}

		//Get the array elements
		if(paramName.equals(ReportsConstants.REVIEWFORM)) {
                    if (type == null || type.length() <= 0) {
		      setType(paramValue);
                    }

		} else if(paramName.equals(ReportsConstants.PROPNUMBER)) {
		    proposal.setProposalNumber(paramValue);

                }else if(paramName.equals(ReportsConstants.PANEL)) {
		    setPanelName(paramValue);

		} else    if(paramName.equals(ReportsConstants.CATEGORY)) {
		    setCategory(paramValue);

		} else if(paramName.equals(ReportsConstants.PINAME)) {
		    setPI(paramValue);
		    
		} else if(paramName.equals(ReportsConstants.TITLE)) {
		    setTitle(paramValue);
		    
		} else if(paramName.equals(ReportsConstants.PROPTYPE)) {
		    setProposalType(paramValue);
		} else if(paramName.equals(ReportsConstants.MULTICYCLE)) {
		    setProposalMulticycle(paramValue);
		} else if(paramName.equals(ReportsConstants.JOINT)) {
		    setProposalJoint(paramValue);
		} else if(paramName.equals(ReportsConstants.CONSTTARGET)) {
		    setConstrainedTargets(paramValue);

                } else if(paramName.equals(ReportsConstants.PRELIMCOMPLETE)) {
                    setPrelimComplete(paramValue);

		} else if(paramName.equals(ReportsConstants.SCIENCEIMPORTANCE)) {
		    setSI(paramValue);
		    
		} else if(paramName.equals(ReportsConstants.SCIENCEJUSTIFICATION)) {
		    setSJ(paramValue);

		} else if(paramName.equals(ReportsConstants.FMTCLARITY)) {
		    setFmtClarity(paramValue);
		    
		} else if(paramName.equals(ReportsConstants.FEASIBILITY)) {
		    setFeasibility(paramValue);

		} else if(paramName.equals(ReportsConstants.FEASIBILITYCONSTRAINT)) {
		    setFeasibilityConstraint(paramValue);
		    
		} else if(paramName.equals(ReportsConstants.CAPABILITY)) {
		    setChandraUse(paramValue);
		    
		} else if(paramName.equals(ReportsConstants.CLARITY)) {
		    setClarity(paramValue);
		    
		} else if(paramName.equals(ReportsConstants.HIGHERRANKED)) {
		    setTargetsTaken(paramValue);
		    
		} else if(paramName.equals(ReportsConstants.TOO)) {
		    setTOOLimited(paramValue);
		    
		} else if(paramName.equals(ReportsConstants.CONSTRAINED)) {
		    setConstrained(paramValue);
		    
		} else if(paramName.equals(ReportsConstants.COMMENTS)) {
		    StringBuffer commentsSB = new StringBuffer();
		    inputLine = reportBF.readLine();

		    while(!inputLine.equals(ReportsConstants.ENDCOMMENTS)) {
			commentsSB.append(inputLine + "\n");
			inputLine = reportBF.readLine();
		    }
		    setComments(commentsSB.toString());
		    
		} else if(paramName.equals(ReportsConstants.RECOMMENDATIONS)) {
		    StringBuffer recsSB = new StringBuffer();
		    inputLine = reportBF.readLine();

		    while(!inputLine.equals(ReportsConstants.ENDRECS)) {
			recsSB.append(inputLine + "\n");
			inputLine = reportBF.readLine();
		    }
		    setRecs(recsSB.toString());
		    
		} else if(paramName.equals(ReportsConstants.REASON)) {
		    StringBuffer reasonSB = new StringBuffer();
		    inputLine = reportBF.readLine();

		    while(!inputLine.equals(ReportsConstants.ENDREASON)) {
			reasonSB.append(inputLine + "\n");
			inputLine = reportBF.readLine();
		    }
		    setGradeReason(reasonSB.toString());
		} else if(paramName.equals(ReportsConstants.TECHRPT)) {
		    StringBuffer techSB = new StringBuffer();
		    inputLine = reportBF.readLine();

		    while(!inputLine.equals(ReportsConstants.ENDTECHRPT)) {
			techSB.append(inputLine + "\n");
			inputLine = reportBF.readLine();
		    }
		    setTechnicalReview(techSB.toString());
		    
		} else if(paramName.equals(ReportsConstants.EFFORT)) {
		    setEffort(paramValue);
		    
		}
	    }
	    reportBF.close();
	    fileR.close();
            FileUtils.setPermissions(report,"660");

	} catch(Exception ex) {
	    LogMessage.println("Caught exception in readReport routine " + ex.getMessage());
	    ex.printStackTrace();
	}
    }
    


    /***
     *
     * This routine will be used to get the large project reports
     *
     * @param reportsDataPath  directory of reports area
     * @param theUser information about current user
     * @param editLPReports  allowed to edit BPP reports
     * @param bppForceAll retrieve all BPP reports
     * @param bppFilename  filename containing bpp proposals (not used?)
     * @return Vector the review reports
     */
    public static Vector<ReviewReport> getLPReports(String reportsDataPath, User theUser, boolean editLPReports, boolean bppForceAll, String bppFilename) {
	Vector<ReviewReport> reportsList = new Vector<ReviewReport>();

	try {
	    DBConnection dbConnect = new DBConnection(reportsDataPath, true);

	    //if(theUser.inChairMode() && !theUser.isPundit() && editLPReports) {
		//reportsList = dbConnect.getChairLPReports(theUser.getPanelName());
	    //} else if((theUser.inChairMode() && !editLPReports)  ||
	    if(theUser.inChairMode()   ||
	               theUser.isAdmin() || theUser.isDeveloper()  ||
                       theUser.isPundit()) {
               String props = null;
	       File bppFile = new File(bppFilename);
               if (bppFile.exists() && bppFile.length() > 3 && !bppForceAll) {
                 props = new String("");
                 FileReader fileR = new FileReader(bppFile);
                 BufferedReader bppFileBF = new BufferedReader(fileR);
                 String inputLine = null;
                 while ((inputLine = bppFileBF.readLine()) != null) {
                   props += "'" + inputLine.trim() + "',";
                 }
                 bppFileBF.close();
                 fileR.close();
               }
               if (props != null && props.length() > 0) {
                 String queryStr = props.substring(0,props.length()-1);
	         reportsList = dbConnect.getLPReports(queryStr);
               } else {
	         reportsList = dbConnect.getLPReports();
               }

	    }

	    //Set the status and last updated fields for each report ???
	    int numReports = reportsList.size();
	    //LogMessage.println("numReports = " + numReports);
	    for(int index=0; index < numReports; index++) {
		ReviewReport rr = (ReviewReport)reportsList.get(index);
		String trueType = rr.getType();
		rr.setType("LP"); //to get the right extension for determining status
		rr.determineStatus(reportsDataPath);
		rr.setType(trueType);
	    }

	} catch(Exception ex) {
	    LogMessage.println("Error in getLPReports routine");
	}

	return reportsList;
    }


    /***
     * This routine retrieves all the review reports for the given user
     *
     * @param userID  database user id
     * @param reportsDataPath  directory of reports area
     * @param beforePR  true if preliminary reports (before the peer review)
     * @return Vector the review reports
     */
    public static Vector<ReviewReport> getRRByUserID(int userID, String reportsDataPath, boolean beforePR) {
	Vector<ReviewReport> reportsList = new Vector<ReviewReport>();

	try {
	    DBConnection dbConnect = new DBConnection(reportsDataPath, true);
	    reportsList = dbConnect.getReportsByUserID(userID, beforePR);

	    //Set the status and last updated fields for each report
	    int numReports = reportsList.size();
	    for(int index=0; index < numReports; index++) {
		ReviewReport rr = (ReviewReport)reportsList.get(index);
		rr.determineStatus(reportsDataPath);
	    }

	} catch(Exception ex) {
	    LogMessage.println("Error in getRRByUserID routine");
	}

	return reportsList;
    }


    /**
     * Retrieve ReviewReport objects for all proposals on the given panel
     * @param panelName  panel name
     * @param reportsDataPath  directory of reports area
     * @param beforePR  true if preliminary reports (before the peer review)
     * @return Vector the review reports
     */
    public static Vector<ReviewReport> getRROnPanelByName(String panelName, String reportsDataPath, boolean beforePR) {
	Vector<ReviewReport> reportsList = new Vector<ReviewReport>();
	//LogMessage.println("****\n In getRROnPanelByName");

	try {
	    DBConnection dbConnect = new DBConnection(reportsDataPath, true);
	    reportsList = dbConnect.getReportsOnPanelByName(panelName, beforePR);
	    int numReports = reportsList.size();

	    for(int index=0; index < numReports; index++) {
		ReviewReport rr = (ReviewReport)reportsList.get(index);
		
		rr.determineStatus(reportsDataPath);
		
	    }

	} catch(Exception ex) {
	    LogMessage.println("Error in getRRonpanelByName routine: " + ex.getMessage());
	}

	return reportsList;
    }




   public void initLock()
   {
      reportLock = new Lock(proposal.getProposalNumber(),panelName,type,
                            reportsDataPath);
   }

    /**
     * get the name of person editing the report
     *
     * @return String name of person editing the report
     */
    public String getCurrentFileEditor() {
	return reportLock.whoLocked();
    }

    /**
     * get the id of person editing the report
     *
     * @return int id of person editing the report
     */
    public int getCurrentEditorID() {
        int editorID = -1;
        String editorName;
       
        editorName = reportLock.whoLocked(editorID); 

        
	return editorID;
    }

    /**
     * Determines if report is currently be edited
     *
     * @param userName  user name
     * @return boolean value indicated if report is being edited
     */
    public boolean isAlreadyBeingEdited(String userName) {
	return (reportLock.isLocked(userName) ) ;
    }
    public boolean isLocked() {
	return (reportLock.isLocked() ) ;
    }

    public boolean unlock(String userName) {
       boolean returnVal = false;
 
       if (reportLock != null) {
           returnVal =reportLock.unlock(userName);
       }
       return returnVal;
    }
    public boolean unlock() {
       boolean returnVal = false;
 
       if (reportLock != null) {
           returnVal =reportLock.unlock();
       }
       return returnVal;
    }

    /***
     * Create the lock file to know that someone is editing the file,
     * and store the filename which is used to unlock the file.
     * @param userName name of user
     */
    private void checkOrCreateLock(String userName) {

	//Secondary reviewers cannot edit peer review reports, so no need
	//to create a lock file if they are viewing the report

        LogMessage.println("checkOrCreateLock: userName    = " + userName);
        //LogMessage.println("checkOrCreateLock: reportStatus= " + reportStatus);
        //LogMessage.println("checkOrCreateLock: type        = " + type);
        //LogMessage.println("checkOrCreateLock: user        = " + theUser.getType());
        if (isSecondaryPeer()  || 
            (reportStatus.equals(ReportsConstants.COMPLETE) && 
               theUser != null && theUser.isReviewer()) ||
            (reportStatus.equals(ReportsConstants.COMPLETE) && isLP() &&
               theUser != null && 
               (theUser.isReviewer() || theUser.isChair() || 
                theUser.isDeputyChair() )) ||
            (reportStatus.equals(ReportsConstants.CHECKOFF) && 
               theUser != null && 
               (theUser.isReviewer() || theUser.isChair() || 
                theUser.isDeputyChair() || theUser.isPundit()))
            ) {
            
            if (type != null) { 
              LogMessage.println("No lock for " + type + " -- " + userName);
            } 
            else {
              LogMessage.println("No lock for unknown type -- " +  userName);
            } 
        }
    }

    public boolean lock(String userName) 
    {
	return  reportLock.lock(userName,userID);
    }




    public boolean startedLPReport() {
      boolean returnVal = false;
      String proposalNumber = getProposalNumber();
      String filenamePath = new String(reportsDataPath);

      File fileDir = new File(filenamePath);
      final String filename = "bpp_" + proposalNumber + ReportsConstants.LPEXT;

      // This example does not return any files that start with `.'.
      FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File fileDir, String name) {
            boolean retval = name.startsWith(filename);
            if  (retval && name.indexOf("lock") >= 0)  {
              retval = false;
            }
            return retval;
        }
      };

      String[] bppFiles =  fileDir.list(filter);
      if (bppFiles.length > 0) {
          returnVal = true;
      }

      return returnVal;
    }

    private String readTechReport(String propFileDir,String techFileName) {
   
      StringBuffer techReport = new StringBuffer("");
      if (propFileDir != null && techFileName != null && techFileName.length() > 2) {
        LogMessage.println("TECH: " + techFileName);
        if (techFileName.indexOf("pdf") < 0 ) {
          try {
            String fileName = propFileDir + "/" + techFileName;
            FileReader fileR = new FileReader(fileName);
            BufferedReader currentFileBR = new BufferedReader(fileR);
            String inputLine = null;
            while( (inputLine = currentFileBR.readLine()) != null) {
              techReport.append(inputLine);
              techReport.append("\n");
            }
            currentFileBR.close();
            fileR.close();
          }
          catch (Exception exc) {
            LogMessage.printException(exc);
          }
        }
      }
      LogMessage.println("TECH: " + techReport.toString());
      return techReport.toString();
    }

  /**
   * Invoke pas_google_reports python script for creating Google Docs,
   * updating Doc permissions, getting file_id, or updating PAS report comments.
   * Several Overridden/Overloaded methods included.
   * @param title Title of Google Doc to interact with
   * @param emails List of email addresses to set permissions for
   * @param action pas_google_reports.py action
   * @param roles Permission type (reader, writer)
   * @param pgrPath pas_google_reports executable path
   * @return Result from running pas_google_reports.py
   */
  public String googleDocs(String title, ArrayList<String> emails, String action,
      ArrayList<String> roles, String pgrPath) {
    String result = "";
    // All actions need title and action name. Create and perms need emails and role
    String args =  " -t " + title + " -a " + action;
    if (action.equals("create") || action.equals("perms")){
      String email_str = arrayListToString(emails);
      String role_str = arrayListToString(roles);
      args += " -e " + email_str + " -r " + role_str;
    }

    try {
      String pgr = reportsDataPath + pgrPath;
      String cmd = pgr + args;
      LogMessage.println("Running pas_google_reports : " + cmd);
      ArrayList<String> envVarList = setEnvironment();
      RunCommand runtime = new RunCommand(cmd, envVarList, null);
      result = runtime.getOutMsg();
      LogMessage.println("pas_google_reports Error outMsg: " + result);
      LogMessage.println("pas_google_reports Error msg: " + runtime.getErrMsg());
    } catch (Exception ex) {
      LogMessage.println("pas_google_reports Exception msg: " + ex);
      result += "Error occurred modifying permissions";
    }
    return result;
  }

  public String createDoc(String title, ArrayList<String> emails, String role,
      String pgrPath){
    ArrayList<String> roles = new ArrayList<>(Collections.singletonList(role));
      return googleDocs(title, emails, "create", roles, pgrPath);
  }

  public String updateDocPermissions(String title, String role, ArrayList<String> emails,
      String pgrPath){
    ArrayList<String> roles = new ArrayList<>(Collections.singletonList(role));
    return googleDocs(title, emails, "perms", roles, pgrPath);
  }

  public String updateDocPermissions(String title, ArrayList<String> roles,
      ArrayList<String> emails, String pgrPath){
    return googleDocs(title, emails, "perms", roles, pgrPath);
  }

  public String updateDoc(String title, String pgrPath){
    return googleDocs(title, new ArrayList<>(), "update", new ArrayList<>(), pgrPath);
  }

  public String getDocID(String title, String pgrPath){
      return googleDocs(title, new ArrayList<>(), "fileid", new ArrayList<>(), pgrPath);
  }

  /**
   * Set ASCDS_PROP_DIR env var for pas_google_reports
   * @return List of env vars
   */
  private ArrayList<String> setEnvironment() {
    ArrayList<String> envVarList = new ArrayList<>();
    String envStr = "ASCDS_PROP_DIR=" + reportsDataPath;
    envVarList.add(envStr);
    LogMessage.println("envVarList " + envVarList);
    return envVarList;
  }

  /**
   * Overloaded arrayListToString with default space delimiter
   * @param list ArrayList
   * @return String of space delimited elements from ArrayList
   */
  public String arrayListToString(ArrayList<String> list){
    return arrayListToString(list, " ");
  }

  /**
   * Convert ArrayList to delimited String
   * @param list ArrayList
   * @param delim delimiter between Strings
   * @return String of delimited elements from ArrayList
   */
  public String arrayListToString(ArrayList<String> list, String delim) {
    if (list.isEmpty()){
    return "dummy";
    }

    StringBuilder sb = new StringBuilder();
    for(String s :list)
    {
      sb.append(s);
      sb.append(delim);
    }
    return sb.toString();
  }

  /**
   * Overloaded getPanelEmails for not skipping chairs/PrimSec
   * @return List of email addresses
   */
  public Map<String, String> getPanelEmails(){
    return getPanelEmails(false, false);
  }

  /**
   * Get emails addresses for Panelists
   * @param skipChairs Don't get Chair/Vice email
   * @param skipPrimSec Don't get Primary/Secondary email
   * @return List of email addresses
   */
  public Map<String, String> getPanelEmails(boolean skipChairs, boolean skipPrimSec){
    Map<String, String> emails = new HashMap<>();
    Vector<User> memberList;
    try {
      DBConnection dbConnect = new DBConnection(reportsDataPath, false);
      if (type.equals("LP")) {
        memberList = dbConnect.loadPanelMembers("BPP");
      } else {
        memberList = dbConnect.loadPanelMembers(panelName);
      }

      for (User member : memberList) {
        String memberType = member.getMemberType();
        // primaryLastName, secondaryLastName are LastName, firstInitial!
        // memberName follows that
        String memberName = member.getUserName() + "," + member.getUserFirst().charAt(0);
        if ( !skipChairs &&
            (memberType.equals(ReportsConstants.DEPUTYCHAIR) ||
             memberType.equals(ReportsConstants.CHAIR) ||
             memberType.equals(ReportsConstants.PUNDITDEPUTY) ||
             memberType.equals(ReportsConstants.PUNDITCHAIR))) {
          emails.put(member.getUserEmail(), memberType);
        }
        // If reviewer also is chair/deputy, don't modify.
        if (!skipPrimSec && (memberName.equals(secondaryLastName) ||
            memberName.equals(primaryLastName) ||
            memberName.equals(punditPrimaryLastName)) &&
            !(memberType.equals(ReportsConstants.DEPUTYCHAIR) ||
            memberType.equals(ReportsConstants.CHAIR) ||
            memberType.equals(ReportsConstants.PUNDITDEPUTY)||
            memberType.equals(ReportsConstants.PUNDITCHAIR))) {
          emails.put(member.getUserEmail(), memberType);
        }
      }
    } catch (Exception exc) {
      LogMessage.println("UpdateReportServlet:getPanelEmails - Caught exception for user ID " + userID);
      LogMessage.printException(exc);
      boolean caughtError = true;
    }
    return emails;
  }

  private String relPath;
}
