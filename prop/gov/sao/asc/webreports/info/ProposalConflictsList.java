// Copyright (c) 2003-2016, 2021,2022 Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS 
//                               INFO Package
//--------------------------------------------------------------------------
//                             ProposalConflictsList
//****************************************************************************
package info;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.text.SimpleDateFormat;
import db.DBConnection;
import ascds.LogMessage;
import ascds.FileUtils;


/** 
  * This class supports the proposal personal conflicts for a panel
 */
public class ProposalConflictsList
{
  private String  reportsDataPath;
  private String  conflictsFile;
  private String  punditFile;
  private String  panelName;
  private boolean showDebug;
  private Vector<Proposal> propList;
  private Vector<User>  memberList;
  private GradeConflictList  gradesList;
  private HashMap<String,String> panels;

  
  

  /**
   * Constructor
   *
   * @param reportsDataPath  directory path for webreports files
   * @param punditFile  fullpath filename of pundit information
   * @param showDebug flag indicating whether debug statements should be logged
  */
  public ProposalConflictsList (String reportsDataPath, String punditFile, 
	boolean showDebug)
  {
    this.reportsDataPath = new String(reportsDataPath);
    this.showDebug = showDebug;
    this.punditFile = new String(punditFile);

    LogMessage.println("ProposalConflictsList: Using " + punditFile);

    propList = new Vector<Proposal>();
    memberList = new Vector<User>();

    gradesList = new GradeConflictList();
  }

  /**
    * load all proposals for this panel for the reviewer
    *
    * @param panelName panel name
    * @exception Exception unable to retrieve proposals
   */
  public void loadPanel( String panelName)
	throws Exception
  {
    DBConnection dbconn = null;

    this.panelName = new String(panelName);
    setConflictsFilename();

    LogMessage.println("loadPanel: " + panelName );
    
    try {
      dbconn = new DBConnection(reportsDataPath,showDebug);

      if (dbconn != null) {
        propList = dbconn.loadProposalsOnPanel(panelName);
        memberList = dbconn.loadPanelMembers(panelName);
        // Pundits are now part of database
        //if (panelName.equals("BPP") ) {
           //readPundits();
        //}
        dbconn.loadCoIs(propList);
        dbconn.loadConflictsByPanel(panelName,gradesList);
        if (panelName.equals("BPP")) {
           panels = dbconn.getLPPanels();
           for (int rr=0;rr< propList.size();rr++) {
             Proposal prop = propList.get(rr);
             prop.setPanelName(panels.get(prop.getProposalNumber()));
           }
        }
      }
      buildConflicts();

    }

    catch (Exception sqle) {
      throw new Exception("An error occurred in retrieving proposals: " + sqle.toString());
    }

  }


  /**
    * return number of proposals in this class
    *
    * @return int number of proposals
   */
  public int size()
  {
    return propList.size();
  }

  /**
    * Return proposal for the given index
    *
    * @param idx  index of proposal in vector
    * @return Proposal proposal for given index or null
   */
  public Proposal get(int idx)
  {
    if (idx >= 0 && idx < propList.size()) {
      return (Proposal)propList.get(idx);
    }
    else {
      return null;
    }
  }

  /**
   * Set the comment on conflict mitigation for the specified proposal number
   *
   * @param propnum  proposal number
   * @param comment  comment on conflict mitigation for this proposal
   *
   */
  public void setCommentForProposal(String propnum,String comment)
  {
    boolean foundMatch = false;
    Iterator ii = propList.iterator();
    while ( ii.hasNext()  && !foundMatch) {
      Proposal prop = (Proposal)ii.next();
      if (prop.getProposalNumber().compareTo(propnum) == 0) {
        foundMatch=true;
        prop.setConflictComment(comment);
      }
    }
  }
  /**
   * read Pundits for this review
   * Pundit file format: last\tfirst\temail\tinstitution
   * @exception IOException I/O errors
   */
  public void readPundits()  throws IOException
  {

    LogMessage.println("Reading pundit file  " + punditFile);

    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(punditFile));

      String nextLine;
      String last = new String("");
      String first = new String("");
      String email = new String("");
      String inst = new String("");

      nextLine = in.readLine();

      while (nextLine != null) {
        // parse last,first,institution from file
        if (!nextLine.startsWith("#")) {
          last = "";
          first="";
          email = "";
          inst = "";
           
          String sval; 
          StringTokenizer st = new StringTokenizer( nextLine,"\t",false);
          if (st.hasMoreTokens()) {
            last = st.nextToken();
          }
          if (st.hasMoreTokens()) {
            first = st.nextToken();
            if (st.hasMoreTokens()) {
              email = st.nextToken();
              if (st.hasMoreTokens()) {
                inst = new String(st.nextToken());
                User theUser = new User();
   	        theUser.setUserID(-1);
                theUser.setUserName(last);
                theUser.setUserFirst(first);
                theUser.setUserType("Pundit");
                theUser.setPanelName("BPP");
                theUser.setUserInstitution(inst);
                theUser.setUserEmail(email);
                memberList.add(theUser);
              }
            }
          }
          
        }
        nextLine = in.readLine();
      }
      in.close();
    }
    catch (FileNotFoundException ioe) {
      LogMessage.println("File not found for " + punditFile);
    }
    catch (IOException ioe) {
      throw new IOException ("Unable to read in " + punditFile);
    }

  }
  


  /**
   * read the proposal comments for this panel.
   * Conflicts file format: ^\tpanel\tpropnum\tcomments.
   * Also takes a File object as input. This file is the file which matches
   * a proposal number to the path where the rps parameter file exists.  
   *
   * @exception IOException I/O errors
   */
  public void readConflicts()  throws IOException
  {

    if (showDebug) {
      LogMessage.println("Parse conflicts file  " + conflictsFile);
    }

    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(conflictsFile));
      LogMessage.println("Reading conflict file: " + conflictsFile);

      String nextLine;
      String propnum = new String("");
      String panel = new String("");
      String comment = new String("");

      nextLine = in.readLine();

      while (nextLine != null) {
        // parse panel,propnum,conflict from file
        // if propnum , then set the conflict comments for the proposal object
        // else log fact that proposal not in list anymore.
        if (nextLine.startsWith("^")) {
          if (propnum.length() > 0) {
            setCommentForProposal(propnum, comment );
          }  
          panel = "";
          propnum="";
          comment = "";
           
          String sval; 
          StringTokenizer st = new StringTokenizer( nextLine,"\t",false);
          st.nextToken();
          if (st.hasMoreTokens()) {
            panel = st.nextToken();
          }
          if (st.hasMoreTokens()) {
            propnum = st.nextToken();
            if (st.hasMoreTokens()) {
              comment = new String(st.nextToken());
            }
  
          }
        }
        else {
          comment += "\n" + nextLine;
        }
        nextLine = in.readLine();
      }
      if (propnum.length() > 0) {
        setCommentForProposal(propnum, comment );
      }  
      in.close();
    }
    catch (FileNotFoundException ioe) {
      LogMessage.println("File not found for " + conflictsFile);
    }
    catch (IOException ioe) {
      throw new IOException ("Unable to read in " + conflictsFile);
    }

  }




  /**
   * Save the conflict comments to a file for this panel.
   *
   * @exception IOException I/O errors
   */
  public void saveConflicts() throws IOException
  {
    if (showDebug) {
      LogMessage.println("Parse conflicts file  " + conflictsFile);
    }

    PrintWriter outPW;
    File theFile = new File(conflictsFile);
    String newFile;


    if (theFile.exists()) {
      newFile = theFile.getParent();
      newFile += "/" + ReportsConstants.TMPDIR  + "/";
      newFile += theFile.getName();

      //date pattern is: day of week (3 chars)_month(3 chars)_
      String datePattern = new String("EEE_MMM_d_H:mm:SS");
      Date today = new Date();
      SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
      String filenameExt = formatter.format(today);
      newFile +=  "." + filenameExt;
      File theNewFile = new File(newFile);
      theFile.renameTo(theNewFile);
    }
      
    outPW = new PrintWriter(new FileWriter(conflictsFile));

    Iterator ii = propList.iterator();
    while ( ii.hasNext() ) {
      Proposal prop = (Proposal)ii.next();
      String comment = prop.getConflictComment();
      String pline = "^\t" + panelName + "\t";
      pline +=  prop.getProposalNumber() + "\t" ; 
      pline +=  comment;
      outPW.println(pline);
    }
    if (outPW != null) {
      outPW.close();
      FileUtils.setPermissions(conflictsFile,"660");
    }
    printerFriendlyConflicts();
  }

  public void printerFriendlyConflicts() throws IOException
  {
    FormatUtils fu = new FormatUtils();
    PrintWriter outPW;

    String conflictsFilePF = conflictsFile + ".txt";
    outPW = new PrintWriter(new FileWriter(conflictsFilePF));

    Iterator ii = propList.iterator();
    while ( ii.hasNext() ) {
      Proposal prop = (Proposal)ii.next();
      outPW.println("Panel             : " + panelName );
      outPW.println("Proposal          : " +prop.getProposalNumber() ) ; 
      String tmpStr = "PI/CoI Conflicts  : " + prop.getPICoIConflict() ; 
      outPW.println((fu.wordWrap(tmpStr,"\n                    ")).trim());
      tmpStr = "Inst.  Conflicts  : " + prop.getInstitutionConflict();
      outPW.println((fu.wordWrap(tmpStr,"\n                    ")).trim());
      tmpStr = "Personal Conflicts: " + prop.getGradesConflict();
      tmpStr= tmpStr.replaceAll("---"," ");
      outPW.println((fu.wordWrap(tmpStr,"\n                    ")).trim());
      tmpStr = "Comment           : " + prop.getConflictComment();
      outPW.println(fu.wordWrap(tmpStr,"\n"));
    }
    if (outPW != null) {
      outPW.close();
      FileUtils.setPermissions(conflictsFilePF,"660");
    }
  }

  /**
    * create the conflicts comment file name: panelnn.conflicts
   */
  private void setConflictsFilename()
  {
    String ext = ReportsConstants.CONFLICTEXT;
    
    conflictsFile =  reportsDataPath + "/" + ReportsConstants.CONFLICTDIR;
    conflictsFile += "/panel" + panelName + ext;
 
  }

  private void buildConflicts() throws Exception
  {
    try {
    
    Iterator pidx = propList.iterator();
    while (pidx.hasNext()) {
      Proposal prop = (Proposal)pidx.next();
      String piCoIConflict = new String("");
      String instConflict = new String("");
      String gradesConflict = new String("");
      Vector<User> coiList = prop.getCoIList();

      Iterator midx = memberList.iterator();
      while (midx.hasNext()) {
        User member = (User)midx.next();
        String suffix="";
        String pnls="NULL";
        if (panelName.equals("BPP")) {
           if (prop.getPanelName() != null  && prop.getPanelName().indexOf(member.getPanelName()) >= 0)   {
             suffix = "*";
           }
        }
        if (prop.getPIID() == member.getUserID() ||
            prop.getPIEmail().equalsIgnoreCase(member.getUserEmail()) ||
            (prop.getPILast().equalsIgnoreCase( member.getUserName()) &&
             prop.getPIFirst().regionMatches(true, 0,member.getUserFirst(),0,2 ))) {
          piCoIConflict += "PI:" +  member.getUserName() + ", " + member.getUserFirst().charAt(0) + suffix + " ";
        }
        if (compareInstitutions(prop.getPIModifiedInstitution(),member.getUserModifiedInstitution()) >= 0) {
          instConflict += "PI:"  + member.getUserName() + ", " + member.getUserFirst().charAt(0) + suffix + " ";
        }
        Iterator cidx = coiList.iterator(); 
        boolean gotCoI = false;
        boolean gotInst= false;
        while (cidx.hasNext()) {
          User coi = (User)cidx.next();
          if (!gotCoI) {
            // No longer check for CoI inst conflicts
            if (coi.getUserID() == member.getUserID() ||
                coi.getUserEmail().equalsIgnoreCase(member.getUserEmail()) ||
               (coi.getUserName().equalsIgnoreCase(member.getUserName()) &&
             coi.getUserFirst().regionMatches(true, 0,member.getUserFirst(),0,2 ))) {
              piCoIConflict += "CoI:" +  member.getUserName() + ",  " + member.getUserFirst().charAt(0) + suffix + " ";
              gotCoI = true;
            }
          }
        }

        // grade conflict. member name is already last,F
        gradesConflict += gradesList.getConflict(prop.getProposalNumber(),
              member.getUserID(),pnls);
      }
      prop.setPICoIConflict(piCoIConflict);
      prop.setInstitutionConflict(instConflict);
      prop.setGradesConflict(gradesConflict);
    }
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
    }
  }

  private int compareInstitutions(String inst1, String inst2)
  {
    int retval = -1;
    if (inst1.length() > inst2.length()) {
       retval = (inst1.indexOf(inst2));
    }
    else {
       retval = (inst2.indexOf(inst1));
    }
    return retval;
  }
      

}  // end of class

  
