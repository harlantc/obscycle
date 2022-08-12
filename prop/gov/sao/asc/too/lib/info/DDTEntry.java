package info;
/*
  Copyrights:

  Copyright (c) 2014,2020-2021 Smithsonian Astrophysical Observatory

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
import java.lang.Object;
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
import info.DDTFileFilter;
import ascds.FileUtils;

/**
  * The TriggerTooEntry class contains all the information related to 
  * TOO that has been triggered
 */
public class DDTEntry {
    private Integer proposal_id;
    private String  status;
    private String  coordinator ;
    private String  urgency ;
    private String  proposalNumber;
    private String  proposalAO;
    private Integer proposalAOId;
    private String  piFirst;
    private String  piEmail;
    private String  principalInvest;
    private String  submissionDate ;
    private String  proposalStatus;
    private String  proposalType;
    private String  proposalTitle;
    private String  coiContact;
    private String  coiFirst;
    private String  coiLast;
    private String  coiEmail;
    private String  approvalDate ;
    private String  proposalAbstract ;
    private String  subjectCategory ;
    private String  dataRights ;
    private Integer ocat_propid ;
    private Double  requested_time ;
    private Double  approved_time ;

    private String  rpsFilename;
    private String  sjFilename;
    private String  conflictFilename;

    private Vector<CommentHistory>  commentHistory ;
    private CommentHistory  currentComment;
    private PropTargetList tgtList;


    private FormatUtils fu;


    /** 
      * Constructor
      */
    public DDTEntry(){
	init();
    }

    private void init() {

        fu = new FormatUtils();

        proposal_id = new Integer(0);
        status = new String("");
        coordinator = new String("");
	urgency = new String("");
        proposalNumber = new String("");
        principalInvest = new String("");
        submissionDate = new String("");
        proposalStatus = new String("");
        proposalType = new String("");
        proposalAbstract = new String("");
        subjectCategory = new String("");
        dataRights = new String("");
        proposalTitle = new String("");
        proposalAO = new String("");
        proposalAOId = new Integer(0);
        coiContact = new String("");
        coiLast = new String("");
        coiEmail = new String("");
        approvalDate = new String("");
        ocat_propid = new Integer(0);
	rpsFilename = new String("");
	sjFilename = new String("");
	conflictFilename = new String("");
        requested_time = new Double(0.0);
        approved_time = new Double(0.0);

        currentComment = new CommentHistory();
        commentHistory = new Vector<CommentHistory>();
        tgtList = null;


    }

    /**
      * Copy from an existing record
      * @param inputDDT input DDTEntry record
      */
    public void copy(DDTEntry inputDDT) {
        proposal_id = inputDDT.getProposalID();
	status = inputDDT.getStatus();
	coordinator = inputDDT.getCoordinator();
	urgency = inputDDT.getUrgency();
        proposalNumber = inputDDT.getProposalNumber();
	principalInvest = inputDDT.getPI();
	submissionDate = inputDDT.getSubmissionDate();
        proposalStatus = inputDDT.getProposalStatus();
        proposalType = inputDDT.getProposalType();
        proposalAbstract = inputDDT.getProposalAbstract();
        subjectCategory = inputDDT.getSubjectCategory();
        dataRights = inputDDT.getDataRights();
        proposalTitle = inputDDT.getProposalTitle();
        proposalAO = inputDDT.getProposalAO();
        proposalAOId = inputDDT.getProposalAOId();
        coiContact = inputDDT.getCoIContact();
        coiLast = inputDDT.getCoI();
        coiEmail = inputDDT.getCoIEmail();
        approvalDate = inputDDT.getApprovalDate();
        ocat_propid = inputDDT.getOCatID();
	currentComment = inputDDT.getCurrentComment();
	commentHistory = inputDDT.getCommentHistoryList();
        tgtList = inputDDT.getTargetList();

    }


    //  Set routines
    public void setProposalID(int inputValue) {
	proposal_id = new Integer(inputValue);
    }
    public void setProposalID(Integer inputValue) {
	proposal_id = inputValue;
    }
    public void setRequestedTime(double inputValue) {
	requested_time = new Double(inputValue);
    }
    public void setApprovedTime(double inputValue) {
	approved_time = new Double(inputValue);
    }
    public void setStatus(String inputValue) {
      if (inputValue != null ) {
	status = inputValue.trim();
      } else {
	status = new String("");
      }
    }
    public void setCoordinator(String inputValue) {
      if (inputValue != null) {
	coordinator = inputValue.trim();
      } else {
	coordinator = new String("");
      }
    }
    public void setUrgency(String inputValue) {
      if (inputValue != null) {
	urgency = inputValue.trim();
      } else {
	urgency = new String("");
      }
    }
    public void setProposalNumber(String inputValue) {
      if (inputValue != null) {
	proposalNumber = inputValue.trim();
      } else {
	proposalNumber = new String("");
      }
    }
    public void setPIFirst(String inputValue) {
      if (inputValue != null) {
	piFirst = inputValue.trim();
      } else {
	piFirst = new String("");
      }
    }
    public void setPIEmail(String inputValue) {
        if (inputValue != null) {
            piEmail = inputValue.trim();
        } else {
            piEmail = new String("");
        }
    }
    public void setPI(String inputValue) {
      if (inputValue != null) {
	principalInvest = inputValue.trim();
      } else {
	principalInvest = new String("");
      }
    }
    public void setSubmissionDate(String inputValue) {
      if (inputValue != null) {
	submissionDate = inputValue.trim();
      } else {
	submissionDate = new String("");
      }
    }
    public void setProposalStatus(String inputValue) {
      if (inputValue != null) {
	proposalStatus = inputValue.trim();
      } else {
	proposalStatus = new String("");
      }
    }
    public void setProposalType(String inputValue) {
      if (inputValue != null) {
	proposalType = inputValue.trim();
      } else {
	proposalType = new String("");
      }
    }
    public void setProposalAbstract(String inputValue) {
      if (inputValue != null) {
	proposalAbstract = inputValue.trim();
      } else {
	proposalAbstract = new String("");
      }
    }
    public void setSubjectCategory(String inputValue) {
      if (inputValue != null) {
	subjectCategory = inputValue.trim();
      } else {
	subjectCategory = new String("");
      }
    }
    public void setDataRights(String inputValue) {
      if (inputValue != null) {
	dataRights = inputValue.trim();
      } else {
	dataRights = new String("");
      }
    }
    public void setProposalTitle(String inputValue) {
      if (inputValue != null) {
	proposalTitle = inputValue.trim();
      } else {
	proposalTitle = new String("");
      }
    }
    public void setProposalAO(String inputValue) {
      if (inputValue != null) {
	proposalAO = inputValue.trim();
      } else {
	proposalAO = new String("");
      }
    }
    public void setProposalAOId(int inputValue) {
	proposalAOId = new Integer(inputValue);
    }

    public void setCoIContact(String inputValue) {
      if (inputValue != null) {
	coiContact = inputValue.trim();
      } else {
	coiContact = new String("");
      }
    }
    public void setCoIFirst(String inputValue) {
      if (inputValue != null) {
	coiFirst = inputValue.trim();
      } else {
	coiFirst = new String("");
      }
    }
    public void setCoI(String inputValue) {
      if (inputValue != null) {
	coiLast = inputValue.trim();
      } else {
	coiLast = new String("");
      }
    }
    public void setCoIEmail(String inputValue) {
        if (inputValue != null) {
            coiEmail = inputValue.trim();
        } else {
            coiEmail = new String("");
        }
    }
    public void setApprovalDate(String inputValue) {
      if (inputValue != null) {
	approvalDate = inputValue.trim();
      } else {
	approvalDate = new String("");
      }
    }
    public void setOCatID(int inputValue) {
	ocat_propid = new Integer(inputValue);
    }
    public void setOCatID(Integer inputValue) {
	ocat_propid = inputValue;
    }
    public void setTargetList(PropTargetList tList)
    {
        tgtList = tList;
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

    //  Get routines
    public Integer getProposalID() {
	return proposal_id;
    }
    public Double getRequestedTime() {
	return requested_time;
    }
    public String getRequestedTimeStr() {
       String retval = "";
       try {
         NumberFormat nf = NumberFormat.getInstance();
         nf.setMaximumFractionDigits(2);
         nf.setMinimumFractionDigits(2);
         nf.setGroupingUsed(false);
         retval = nf.format(requested_time);
       } catch (Exception exc) {
         LogMessage.printException(exc);
       }
       return retval;
   }

    public Double getApprovedTime() {
	return approved_time;
    }
    public String getApprovedTimeStr() {
       String retval = "";
       try {
         NumberFormat nf = NumberFormat.getInstance();
         nf.setMaximumFractionDigits(2);
         nf.setMinimumFractionDigits(2);
         nf.setGroupingUsed(false);
         retval = nf.format(approved_time);
       } catch (Exception exc) {
         LogMessage.printException(exc);
       }
       return retval;
   }
    public String getStatus() {
	return status;
    }
    public String getCoordinator() {
	return coordinator;
    }
    public String getUrgency() {
	return urgency;
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
    public String getObserver() {

        if (coiContact.equalsIgnoreCase("Y")) {
	   return coiLast;
        } else {
	   return "";
        }
    }
    public String getObserverEmail() {

        if (coiContact.equalsIgnoreCase("Y")) {
            return coiEmail;
        } else {
            return "";
        }
    }
    public String getSubmissionDate() {
	return submissionDate;
    }
    public String getProposalStatus() {
	return proposalStatus;
    }
    public String getProposalType() {
	return proposalType;
    }
    public String getProposalAbstract() {
	return proposalAbstract;
    }
    public String getSubjectCategory() {
	return subjectCategory;
    }
    public String getDataRights() {
	return dataRights;
    }
    public String getProposalTitle() {
	return proposalTitle;
    }
    public String getProposalAO() {
	return proposalAO;
    }
    public Integer getProposalAOId() {
	return proposalAOId;
    }
    public String getCoIContact() {
	return coiContact;
    }
    public String getCoI() {
	return coiLast;
    }
    public String getCoIEmail() {
        return coiEmail;
    }
    public String getApprovalDate() {
	return approvalDate;
    }
    public Integer getOCatID() {
	return ocat_propid;
    }
    public PropTargetList getTargetList() {
       return tgtList;
    }
    public CommentHistory getCurrentComment() {
	return currentComment;
    }
    public String getComment() {
	return currentComment.getComment();
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

    public boolean hasFastProc()
    {
      boolean retval=false;
      if (tgtList != null) 
        retval= tgtList.hasFastProc();
      return retval;
    }

  /**
    * writeDDTStatus -  printer friendly  format
    * @param filename  input filename
    * @param isCmtOnly is comment only message, no paging
    * @param tgtList   targets for DDT
    * @return boolean  true if status message written successfully
   */
  public boolean writeDDTStatus(String filename, PropTargetList tgtList,
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
      LogMessage.println("Caught exception in creating a new file for writing DDT");
      LogMessage.println(exc.getMessage());
      retval = false;
    }
    if (retval) {
      try {
        FileWriter outputFW = new FileWriter(filename.toString());
        PrintWriter outputPW = new PrintWriter(outputFW);
        boolean isApproved = false;
        if (getStatus().equals(TriggerTooConstants.APPROVED)) {
          isApproved = true;
        }
        retval = writeOutput(outputPW,isCmtOnly,isApproved);
        tgtList.writeOutput(outputPW,isApproved);
        outputPW.close();
        outputFW.close();
        FileUtils.setPermissions(outputFile,"440");
      }
      catch (Exception exc) {
        LogMessage.println("DDTEntry: Unable to write output to " + filename);
        LogMessage.println(exc.getMessage());
      }
    }
     
    return retval;
  }

  private boolean writeOutput(PrintWriter outputPW, boolean isCmtOnly,boolean isApproved)
  {
    boolean retval=true;

    try {
       NumberFormat nfi = NumberFormat.getInstance();
       nfi.setMinimumIntegerDigits(5);
       nfi.setGroupingUsed(false);
       NumberFormat nf = NumberFormat.getInstance();
       nf.setMaximumFractionDigits(6);
       nf.setMinimumFractionDigits(6);
       nf.setMinimumIntegerDigits(3);
       //PropTarget ddtentry = ddtList.getByObsid(getObsid());


       if (isCmtOnly) {
         outputPW.println(TriggerTooConstants.CMT_ONLY + "\n");
       }
       outputPW.print(getSubmissionDate() + ": ");
       outputPW.print(getProposalType() + " request from ");
       outputPW.print(getPIFirst() + ". " + getPI() + "; ");
       outputPW.println("Paging urgency " + getUrgency() + 
	" " + tgtList.getCXCStartStop()  + " .");

       outputPW.println("\nStatus for tag number: " + getProposalNumber());
       outputPW.print("RA:  ");
       outputPW.println(tgtList.getAllRAs());
       outputPW.print("Dec: ");
       outputPW.println(tgtList.getAllDecs());
       outputPW.println("");


       outputPW.println("\nSTATUS:");
       writeStatus(outputPW,TriggerTooConstants.ACKNOWLEDGED);
       writeStatus(outputPW,TriggerTooConstants.APPROVED);
       writeStatus(outputPW,TriggerTooConstants.NOTAPPROVED);
       writeStatus(outputPW,TriggerTooConstants.WITHDRAWN);

      outputPW.println(tgtList.getFastProc());

      outputPW.println("");
      if (getCoordinator() != null && getCoordinator().length() > 0) {
          outputPW.println("Coordinator: " + getCoordinator());
       }


       outputPW.println("\nCOMMENTS:");
       String allCmts = fu.getWrapped(getCommentHistory(false));
       outputPW.println(allCmts);

      if (isApproved) {
        String abstr = fu.getWrapped(proposalAbstract);
        outputPW.println("\n---------------------------------------------------------------");
        outputPW.println("ABSTRACT:");
        outputPW.println(abstr);
      }


       
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

  public boolean inObsCat()
  {
     boolean retval = false;
     if (ocat_propid > 0) {
        retval =  true;
     }
     return retval;
  }

  public void setRPSFilename(String ddtpath) 
  {
    if (ddtpath != null) {
      File dir = new File(ddtpath);
      String[] children;

      DDTFileFilter filter = new DDTFileFilter(proposalNumber,"_prop");
      children = dir.list(filter);
      assert children != null;
      if (children.length <= 0) {
        DDTFileFilter filter1 = new DDTFileFilter(proposalNumber,"_merged");
        children = dir.list(filter1);
      }
      if (children.length <= 0) {
        DDTFileFilter filter2 = new DDTFileFilter(proposalNumber,"_f");
        children = dir.list(filter2);
      }
      if (children.length > 0) {
        rpsFilename = ddtpath + "/" + children[0];
      }
    }
  }
  public void setSJFilename(String ddtpath) 
  {
    if (ddtpath != null) {
      File dir = new File(ddtpath);
      String[] children;
    
      DDTFileFilter filter = new DDTFileFilter(proposalNumber,"_sj");
      children = dir.list(filter);

      if (children.length > 0) {
        sjFilename = ddtpath + "/" + children[0];
      }
    }
  }
  public void setConflictFilename(String ddtpath) 
  {
    if (ddtpath != null) {
      File dir = new File(ddtpath);
      String[] children;
    
      DDTFileFilter filter = new DDTFileFilter(proposalNumber,"conflicts");
      children = dir.list(filter);

      if (children.length > 0) {
        conflictFilename = ddtpath + "/" + children[0];
      }
    }
  }

  public String getRPSFilename() {
    return rpsFilename;
  }
  public String getSJFilename() {
    return sjFilename;
  }
  public String getConflictFilename() {
    return conflictFilename;
  }

}
