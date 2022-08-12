// Copyright (c) 2003-2016, 2021, Smithsonian Astrophysical Observatory
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
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.*;
import ascds.LogMessage;


/** 
 * This class contains all the information related to a proposal
 */
public class Proposal {
    private String proposalNumber;
    private String subjectCategory;
    private String principalInvest;
    private String piFirst;
    private String piLast;
    private Integer piid;
    private Institution piInstitution;
    private String piEmail;
    private String title;
    private String internalType;
    private String type;
    private String joint;
    private String multicycle;
    private String linkedProposalNumber;
    private String constrainedTargets;
    private double prelimGrade;
    private String personalConflict;
    private String gradeConflict;
    private String groupName;
    private String piCoIConflict;
    private String institutionConflict;
    private String conflictComment;
    private String gradesConflict;
    private Vector<User> coiList;
    private boolean foundMatch;
    private String contactConflict;
  
    private String primaryReviewer;
    private String secondaryReviewer;
    private String panelName;

    public Proposal() {
	init();
    }

    /**
     * 
     * @param propNum proposal number
     */
    public Proposal(String propNum) {
	init();
	proposalNumber = propNum;
    }
    
    private void init() {
        proposalNumber = new String("");
	subjectCategory = new String("");
	principalInvest = new String("");
	piFirst = new String("");
	piLast = new String("");
	piid = new Integer("0");
	piInstitution = new Institution("");
	piEmail = new String("");
	title = new String("");
	type = new String("");
	internalType = new String("");
	joint = new String("");
	multicycle = new String("");
	linkedProposalNumber = new String("");
        constrainedTargets = new String("");
        prelimGrade = -1;
	personalConflict = new String("");
	gradeConflict = new String("");
	groupName = new String("");
        piCoIConflict = new String("");
        institutionConflict = new String("");
        gradesConflict = new String("");
        conflictComment = new String("");
	coiList = new Vector<User>();

        primaryReviewer = new String("");
        secondaryReviewer = new String("");
        panelName = new String("");
        foundMatch=false;
        contactConflict = "";
    }

    /**
     * 
     * @param inputProp proposal number
     */
    public void copy(Proposal inputProp) {
	proposalNumber = inputProp.getProposalNumber();
        subjectCategory = inputProp.subjectCategory;
	principalInvest = inputProp.getPI();
	piFirst = inputProp.getPIFirst();
	piLast = inputProp.getPILast();
	piid = inputProp.piid;
	piInstitution = inputProp.piInstitution;
	piEmail = inputProp.piEmail;
	title = inputProp.getTitle();
        type = inputProp.type;
        internalType = inputProp.internalType;
        joint = inputProp.joint;
        multicycle = inputProp.multicycle;
        linkedProposalNumber = inputProp.linkedProposalNumber;
        constrainedTargets = inputProp.constrainedTargets;


	prelimGrade = inputProp.prelimGrade;
	personalConflict = inputProp.personalConflict;
	gradeConflict = inputProp.gradeConflict;
	groupName = inputProp.groupName;
	piCoIConflict = inputProp.piCoIConflict;
	institutionConflict = inputProp.institutionConflict;
	gradesConflict = inputProp.gradesConflict;
	conflictComment = inputProp.conflictComment;
	coiList = inputProp.coiList;
	primaryReviewer = inputProp.primaryReviewer;
	secondaryReviewer = inputProp.secondaryReviewer;
	panelName = inputProp.panelName;
	contactConflict = inputProp.contactConflict;

    }

    /**
     * set the proposal number
     *
     * @param inputParam  proposal number
    */
    public void setProposalNumber(String inputParam) {
	proposalNumber = inputParam;
    }

    /**
     * set the proposal science category (subject category)
     *
     * @param inputParam  subject category
    */
    public void setCategory(String inputParam) {
	subjectCategory = inputParam;
    }

    /**
     * as of cycle 17, no longer hide GTO type
     * set the proposal type,
     * for external display.
     *
     * @param inputParam  proposal type
    */
    public void setProposalType(String inputParam) {
       internalType = inputParam;
       type = inputParam;
       /* ******************************************
       if (inputParam.equals("GTO") ||
           inputParam.equals("GTO/ALTERNATE") ) {
          type = "GO";
       }
       else if (inputParam.equals("GTO/TOO") ||
           inputParam.equals("GTO/TOO/ALTERNATE") ) {
          type = "TOO";
       }
       else if (inputParam.equals("GTO/LP")) {
          type = "LP";
       }
       ********************************************* */
    }


    /**
     * set the proposal joint parameter
     *
     * @param inputParam  proposal joint 
    */
    public void setJoint(String inputParam) {
	joint = inputParam;
    }
    /**
     * set the proposal multicycle parameter
     *
     * @param inputParam  proposal multicycle 
    */
    public void setMulticycle(String inputParam) {
	multicycle = inputParam;
    }
    
    /**
     * set the proposal linked parameter
     *
     * @param inputParam  linked proposal number 
    */
    public void setLinkedProposalNumber(String inputParam) {
	linkedProposalNumber = inputParam;
    }

    /**
     * set the proposal constrained targets parameter
     *
     * @param inputParam  proposal constrained target 
    */
    public void setConstrainedTargets(String inputParam) {

	if(inputParam.equals("N")) {
	  constrainedTargets = new String("No");
	} else if(inputParam.equals("Y")) {
	  constrainedTargets = new String("Yes");
	} else if(inputParam.equals("P")) {
	  constrainedTargets = new String("Preferred");
	}
        else {
	  constrainedTargets = inputParam;
        }
    }

    /**
     * set the proposal P.I. parameter
     *
     * @param inputParam  proposal principal investigator
    */
    public void setPI(String inputParam) {
	principalInvest = inputParam;
    }
    public void setPIFirst(String inputParam) {
	piFirst = inputParam;
    }
    public void setPILast(String inputParam) {
	piLast = inputParam;
    }
    /**
     * set the proposal piid parameter
     *
     * @param inputParam  proposal pers_id 
    */
    public void setPIID(int inputParam) {
	piid = new Integer(inputParam);
    }
    public void setPIID(Integer inputParam) {
	piid = inputParam;
    }
    /**
     * set the proposal PI Institution parameter
     *
     * @param inputParam  proposal PI Institution 
    */
    public void setPIInstitution(String inputParam) {
	piInstitution = new Institution(inputParam);
    }
    /**
     * set the proposal PI Email parameter
     *
     * @param inputParam  proposal PI Email 
    */
    public void setPIEmail(String inputParam) {
	piEmail = inputParam;
    }
    

    /**
     * set the proposal title parameter
     *
     * @param inputParam  proposal title 
    */
    public void setTitle(String inputParam) {
	title = inputParam;
    }

    /**
     * set the proposal preliminary grade parameter
     *
     * @param inputParam  proposal preliminary grade 
    */
    public void setPrelimGrade(Double inputParam) {
	prelimGrade = inputParam.doubleValue();
    }

    /**
     * set the proposal preliminary grade conflict parameter
     *
     * @param inputParam  proposal preliminary grade conflict 
    */
    public void setPrelimGradeConflict(String inputParam) {
	gradeConflict = inputParam;
    }
    public void setPersonalConflict(String inputParam) {
	personalConflict = inputParam;
    }

  /**
   * set the proposal contact conflict parameter
   * @param inputParam proposal has contact conflict
   */
  public void setContactConflict(String inputParam) {
    contactConflict = inputParam;
  }

    /**
     * set the proposal group name parameter
     *
     * @param inputParam  proposal group name 
    */
    public void setGroupName(String inputParam) {
	groupName = inputParam.trim();
    }

    public void setInstitutionConflict (String inputParam) {
        institutionConflict = inputParam.trim();
    }
    public void setPICoIConflict (String inputParam) {
        piCoIConflict = inputParam.trim();
    }
    public void setGradesConflict (String inputParam) {
        gradesConflict = inputParam.trim();
    }
    public void setConflictComment (String inputParam) {
        conflictComment = inputParam.trim();
    }
    public void addCoI(User inputParam) {
        coiList.add(inputParam);
    }

    /**
     * set the proposal primary reviewer parameter
     *
     * @param inputParam  proposal Primary Reviewer 
    */
    public void setPrimaryReviewer(String inputParam) {
	primaryReviewer = inputParam;
    }

    /**
     * set the proposal secondary reviewer parameter
     *
     * @param inputParam  proposal Secondary Reviewer 
    */
    public void setSecondaryReviewer(String inputParam) {
	secondaryReviewer = inputParam;
    }
    
    /**
     * set the proposal Panel parameter
     *
     * @param inputParam  proposal panel
    */
    public void setPanelName(String inputParam) {
	panelName = inputParam;
    }
    
    public void setFoundMatch(boolean inputParam) {
	foundMatch = inputParam;
    }

    /**
     * return the proposal number 
     *
     * @return String proposal number
     */
    public String getProposalNumber() {
	return proposalNumber;
    }

    /**
     * return the Principal Investigator
     *
     * @return String P.I.
     */
    public String getPI() {
	return principalInvest;
    }
    public String getPIFirst() {
	return piFirst;
    }
    public String getPILast() {
	return piLast;
    }

    /**
     * return the Principal Investigator ID
     *
     * @return Integer P.I. id
     */
    public Integer getPIID() {
	return piid;
    }
    /**
     * return the Principal Investigator Email
     *
     * @return String P.I. email
     */
    public String getPIEmail() {
	return piEmail;
    }
    /**
     * return the Principal Investigator Institution
     *
     * @return Institution P.I. institution
     */
    public String getPIInstitution() {
	return piInstitution.getInstitutionName();
    }
    public String getPIModifiedInstitution() {
	return piInstitution.getModifiedInstitution();
    }

    public String getPICoIConflict() {
      return piCoIConflict;
    }
    public String getInstitutionConflict() {
	return institutionConflict;
    }
    public String getGradesConflict() {
	return gradesConflict;
    }
    public String getConflictComment() {
	return conflictComment;
    }
    public Vector<User> getCoIList() {
       return coiList;
    }
    public boolean getFoundMatch() {
	return foundMatch;
    }

    /**
     * return the proposal title
     *
     * @return String proposal title
     */
    public String getTitle() {
	return title;
    }

    /**
     * return the science category
     *
     * @return String science category
     */
    public String getCategory() {
	return subjectCategory;
    }

    /**
     * return the proposal type
     *
     * @return String proposal type 
     */
    public String getProposalType() {
	return type;
    }

    /**
     * return the proposal joint type
     *
     * @return String joint 
     */
    public String getJoint() {
	return joint;
    }
    /**
     * return the proposal multicycle type
     *
     * @return String multicycle 
     */
    public String getMulticycle() {
	return multicycle;
    }
        
        
    /**
     * return the linked proposal number
     *
     * @return String  linked proposal number
     */
    public String getLinkedProposalNumber() {
	return linkedProposalNumber;
    }
        
    /**
     * return the constrained target (Preferred, Required  )
     *
     * @return String  constrained target
     */
    public String  getConstrainedTargets() {
	return constrainedTargets;
    }

    /**
     * return the vector of all the associated files
     *
     * @return Vector  associated files
     */
    public Vector<ProposalFile> getProposalFiles() {
        return(ProposalFileList.getProposalFiles(proposalNumber));
    }

    /**
     * return the science justification file
     *
     * @return String  science justification filename 
     */
    public String getScienceJustFile() {
      return(ProposalFileList.getScienceJustFile(proposalNumber));
    }

    /**
     * return the RPS Parameter Form file
     *
     * @return String  RPS parameter form filename
     */
    public String getRPSParam() {
     
      return(ProposalFileList.getRPSParam(proposalNumber));
    }

    /**
     * return the RPS merged file
     *
     * @return String  RPS merged filename
     */
    public String getMergedFile() {
     
      return(ProposalFileList.getMergedFile(proposalNumber));
    }


    /**
     * return the Technical Review file
     *
     * @return String  technical review filename
     */
    public String getTechnicalFile() {
     
      return(ProposalFileList.getTechnicalFile(proposalNumber));
    }

    /**
     * return the ProposerInput file
     *
     * @return String  proposer input filename
     */
    public String getProposerInputFile() {
     
      return(ProposalFileList.getProposerInputFile(proposalNumber));
    }

    /**
     * return the preliminary grade
     *
     * @return Double preliminary grade for this proposal
     */
    public Double getPrelimGrade() {
	return (new Double(prelimGrade));
    }

    /**
     * return the preliminary grade conflict flag
     *
     * @return String preliminary grade conflict flag for this proposal
     */
    public String getPrelimGradeConflict() {
	return gradeConflict;
    }
    public String getPersonalConflict() {
	return personalConflict;
    }

  /**
   * return the contact conflict flag
   * @return String contact conflict flag for this proposal
   */
  public String getContactConflict() { return contactConflict; }

    /**
     * return the group name 
     *
     * @return String  group name for this proposal
     */
    public String getGroupName() {
	return groupName;
    }

    /**
     * return the primary reviewer name 
     *
     * @return String  primary reviewer name for this proposal
     */
    public String getPrimaryReviewer() {
	return primaryReviewer;
    }
    /**
     * return the secondary reviewer name 
     *
     * @return String  secondary reviewer name for this proposal
     */
    public String getSecondaryReviewer() {
	return secondaryReviewer;
    }
    /**
     * return the panel name 
     *
     * @return String  panel name for this proposal
     */
    public String getPanelName() {
	return panelName;
    }

   public boolean isConflict(User member)
   {
     boolean isconflict=false;

     if (personalConflict.equalsIgnoreCase("C"))  {
       isconflict=true;
       // LogMessage.println("conflict " + member.getUserID() + ": " + proposalNumber + " personal" );

     } else if (getPIID() == member.getUserID() ||
         getPIEmail().equalsIgnoreCase(member.getUserEmail()) ||
         (getPILast().equalsIgnoreCase( member.getUserName()) &&
          getPIFirst().regionMatches(true, 0,member.getUserFirst(),0,1))) {
        isconflict = true;
     } else if (compareInstitutions(getPIModifiedInstitution(),
		member.getUserModifiedInstitution()) ) {
        isconflict = true;
        // LogMessage.println(proposalNumber + " inst " + getPIModifiedInstitution() + "--" + member.getUserModifiedInstitution());
     }
     if (!isconflict) {
       Iterator cidx = coiList.iterator();
       while (cidx.hasNext() && !isconflict) {
          User coi = (User)cidx.next();
          //LogMessage.println("isConflict: " + member.getUserFirst()  + " --CoI: " + coi.getUserName() + "---" + coi.getUserFirst() ++ member.getUserName());
         // CoI inst conflicts no longer checked.
         if (coi.getUserID() == member.getUserID() ||
              coi.getUserEmail().equalsIgnoreCase(member.getUserEmail()) ||
             (coi.getUserName().equalsIgnoreCase(member.getUserName()) &&
             coi.getUserFirst().regionMatches(true, 0,member.getUserFirst(),0,1))) {
            isconflict = true;
          }
       }

     }
     return isconflict;
   }

    private boolean compareInstitutions(String inst1, String inst2)
  {
    boolean retval = false;

    retval = inst1.equalsIgnoreCase(inst2);

    return retval;
  }



}
