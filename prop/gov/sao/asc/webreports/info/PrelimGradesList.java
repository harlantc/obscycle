// Copyright (c) 2003-2016, 2021 Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS 
//                               INFO Package
//--------------------------------------------------------------------------
//                               PrelimGradesList
//****************************************************************************
package info;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.lang.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import db.DBConnection;
import ascds.LogMessage;
import ascds.FileUtils;


/**
 * This class handles the preliminary grades for the Peer Review
 */
@SuppressWarnings("unchecked") 
public class PrelimGradesList
{
  private String  reportsDataPath;
  private String  prelimGradesFile;
  private String contactsFile;
  private String  panelName;
  private String  reviewerName;
  private Integer userId;
  private boolean showDebug;
  private Vector<Proposal>  dblist;
  private ArrayList<String[]> contactsList;

  /**
   * Constructor
   *
   * @param reportsDataPath  directory path for webreports files
   * @param showDebug flag indicating whether debug statements should be logged
   */
  public PrelimGradesList (String reportsDataPath, boolean showDebug)
  {
    this.reportsDataPath = new String(reportsDataPath);
    this.showDebug = showDebug;
    this.showDebug= true;

    contactsList = new ArrayList<>();
    dblist = new Vector<Proposal>();
  }

  /**
    * return reviewer name for these preliminary grades
    *
    * @return String  name of the reviewer
   */
  public String getReviewerName() 
  {
    return this.reviewerName;
  }

  public String  getFilename() 
  {
    return prelimGradesFile;
  }


  /**
   *
   * @return ArrayList of Arrays of user-input personal conflicts contacts.
   */
  public ArrayList<String[]> getContactsList() { return contactsList; }

  /**
    * load all proposals for this panel for the reviewer
    * 
    * @param user  user id
    * @param reviewerName name of reviewer
    * @param panelName panel name
    * @exception Exception unable to retrieve proposals
   */
  public void loadProposalsOnPanel(Integer user,String reviewerName,
	String panelName)
	throws Exception
  {
    DBConnection dbconn = null;

    this.panelName = new String(panelName);
    this.userId = user;
    this.reviewerName = reviewerName;

    prelimGradesFile = setFilenames(ReportsConstants.GRADEDIR, ReportsConstants.PRELIM);
    contactsFile = setFilenames(ReportsConstants.CONFLICTDIR, ReportsConstants.PERSONALCONF);

    try {
      dbconn = new DBConnection(reportsDataPath,showDebug);

      if (dbconn != null) {
        dblist = dbconn.loadProposalsOnPanel(panelName);
        dbconn.loadCoIs(dblist);
        dbconn.loadConflicts(userId,dblist);
      }
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
    return dblist.size();
  }

  /**
    * Return proposal for the given index
    *
    * @param idx  index of proposal in vector
    * @return Proposal proposal for given index or null
   */
  public Proposal get(int idx)
  {
    if (idx >= 0 && idx < dblist.size()) {
      return (Proposal)dblist.get(idx);
    }
    else {
      return null;
    }
  }

  /**
   * Set preliminary grade for specified proposal number.
   *
   * @param propnum  proposal number
   * @param grade    grade assigned to this proposal
   * @param conflict "C" if conflict exists and reviewer can't give a grade 
   * @param fileMatch if true, set flag that you found proposal in the file
   * @return boolean  true on success
   */
  public boolean setPreliminaryGradeForProposal(String propnum,Double grade,
        String conflict ,boolean fileMatch)
  {
    boolean foundMatch = false;
    Iterator ii = dblist.iterator();
    while ( ii.hasNext()  && !foundMatch) {
      Proposal prop = (Proposal)ii.next();
      if (prop.getProposalNumber().compareTo(propnum) == 0) {
        foundMatch=true;
        prop.setPrelimGrade(grade);
        if (fileMatch)  {
          prop.setFoundMatch(true);
          if (prop.getPersonalConflict().equalsIgnoreCase("C")) {
             conflict="C";
          }
        }
        prop.setPrelimGradeConflict(conflict);
      }
    }
    return foundMatch;
  }


  /**
   * Sets contactConflict flag in Proposal object
   * @param propnum Proposal number of an existing contact conflict
   */
  public void setContactConflictForProposal(String propnum) {
    for (Proposal prop : dblist) {
      if (prop.getProposalNumber().compareTo(propnum) == 0) {
        prop.setContactConflict("C");
        break;
      }
    }
  }

  /** 
   * Read the preliminary grades file for current user and set
   * any previously saved preliminary grades in the proposal object.
   * Grades file format: panel\tpropnum\tgrade\tconflict.
   *
   * @exception IOException I/O errors
   *
  */
  public void readGrades()  throws IOException
  {

    BufferedReader in = null;
    //System.err.println("Parse grades file  " + prelimGradesFile);
    try {
      in = new BufferedReader(new FileReader(prelimGradesFile));
      String nextLine;

      while ((nextLine = in.readLine()) != null) {
        // parse panel,propnum,grade from file
        // if propnum , then set the grade for the proposal object
        // else log fact that proposal not in list anymore.
        String propnum = new String("");
        String panel = new String("");
        String conflict = new String("");
        String sval; 
        //Double  grade = new Double(0.0);
	Double  grade = new Double(-1.0);
        StringTokenizer st = new StringTokenizer( nextLine,"\t",false);
        if (st.hasMoreTokens()) {
          panel = st.nextToken();
          if (st.hasMoreTokens()) {
            propnum = st.nextToken();
            if (st.hasMoreTokens()) {
              sval = new String(st.nextToken());
              try {
                grade = new Double(sval);
              }
              catch (NumberFormatException e) {
                // this is ok because it might be empty
              }
              if (st.hasMoreTokens()) {
                conflict = st.nextToken();
              }
            }
  
          }
        }
        if (propnum.length() > 0) {
          //if (showDebug)
	   //LogMessage.println("Set grade for "  + propnum + " grade=" + grade.toString());
          // set grade and if personal conflict, set grade conflict 
          setPreliminaryGradeForProposal(propnum, grade,conflict,true );
        }
      }
      in.close();
    }
    catch (FileNotFoundException ioe) {
      LogMessage.println("File not found for " + prelimGradesFile);
    }
    catch (IOException ioe) {
      throw new IOException ("Unable to read in " + prelimGradesFile);
    }
  }
  /**
   * Parse the personal contacts file for current user.
   * Set any contact conflicts in the proposal object.
   * Grades file format: first\tlast\tinstitution\tconflict proposal number.
   *
   * @exception IOException I/O errors
   *
   */
  public void readContacts()  throws IOException
  {
    LogMessage.println("Parse personal conflict file  " + contactsFile);
    try {
      InputStream is = new FileInputStream(contactsFile);
      BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

      String nextLine;

      while ((nextLine = in.readLine()) != null) {
        String first = "";
        String last = "";
        String inst = "";
        String cprop = "";
        StringTokenizer st = new StringTokenizer(nextLine,"\t",false);
        if (st.hasMoreTokens()) {
          first = st.nextToken();
          if (st.hasMoreTokens()) {
            last = st.nextToken();
            if (st.hasMoreTokens()) {
              inst = st.nextToken();
              if (st.hasMoreTokens()) {
                cprop = st.nextToken();
              } else {
                cprop = "";
              }
            }
          }
        }

        String[] personalContact = {first, last, inst, cprop};
        contactsList.add(personalContact);
        // Set contact conflict from file if the conflict entered was already matched.
        if (!cprop.isEmpty()) {
          setContactConflictForProposal(cprop);
        }
      }
      in.close();
    }
    catch (FileNotFoundException ioe) {
      LogMessage.println("File not found for " + contactsFile);
    }
    catch (IOException ioe) {
      throw new IOException ("Unable to read in " + contactsFile);
    }
  }


  // setConflicts will set any institution/user conflicts found for any 
  // proposal not existing in the current Preliminary Grades file.  So the
  // first time through, it will set for all proposals.
  public void setConflicts(User theUser)  
  {
    Double grade= new Double(-1.0);

    Iterator ii = dblist.iterator();
    while ( ii.hasNext() ) {
      Proposal prop = (Proposal)ii.next();
      // we didn't find the proposal in the file and there is a conflict,
      // so initialize the conflict
      if (!prop.getFoundMatch() && prop.isConflict(theUser))
          setPreliminaryGradeForProposal(prop.getProposalNumber(), grade,"C",false );
    }
  }

  /**
   * Save the grades and contacts to a file for this reviewer.
   *
   * @exception IOException I/O errors
   */
  public void saveGrades() throws IOException
  {
    File theFile = new File(prelimGradesFile);
    if (theFile.exists()) {
      moveOldFile(theFile);
    }

    File theContConFile = new File(contactsFile);
    if (theContConFile.exists()) {
      moveOldFile(theContConFile);
    }
    DBConnection dbconn=null;
    try {
      dbconn = new DBConnection(reportsDataPath,showDebug);
    } catch (Exception exc) {
      LogMessage.printException(exc);
      LogMessage.println("PrelimGradesList: Unable to connect to database.");
    }
    //write pcont
    PrintWriter outPcont;
    OutputStream os = new FileOutputStream(contactsFile);
    outPcont = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
    for (String[] pcont : contactsList) {
      String line = pcont[ReportsConstants.CONTACTFIRST] + "\t" +
                    pcont[ReportsConstants.CONTACTLAST] + "\t" +
                    pcont[ReportsConstants.CONTACTINSTITUTE] + "\t" +
                    pcont[ReportsConstants.CONTACTPROPSAL];
      outPcont.println(line);
    }

    PrintWriter out;
    out = new PrintWriter(new FileWriter(prelimGradesFile));
    for (Proposal prop : dblist) {
      Double grade = prop.getPrelimGrade();
      String pline = panelName + "\t";
      pline += prop.getProposalNumber() + "\t";
      if (grade >= 0) {
        pline += grade.toString() + "\t";
      } else {
        pline += " \t";
      }
      pline += prop.getPrelimGradeConflict();
      out.println(pline);

      if (dbconn != null && !prop.getPrelimGradeConflict().equalsIgnoreCase(prop.getPersonalConflict())) {
        String inactiveFlg = "Y";
        if (prop.getPrelimGradeConflict().equalsIgnoreCase("C"))
          inactiveFlg = "N";
        try {
          dbconn.updateReviewerConflict(userId, prop.getProposalNumber(), "Personal", inactiveFlg);
        } catch (SQLException exc) {
          LogMessage.printException(exc);
          LogMessage.println(
              "Conflict Update failed for " + userId + " " + prop.getProposalNumber() + " "
                  + inactiveFlg);
        }
      }
    }
    if (out != null) {
      out.close();
      FileUtils.setPermissions(prelimGradesFile,"660");
    }

    if (outPcont != null) {
      outPcont.close();
      FileUtils.setPermissions(contactsFile,"660");
    }
  }

  /**
   * Move old file to tmp when saving anew
   * @param theFile File to move to tmp
   */
  private void moveOldFile(File theFile) {
    String newFile = theFile.getParent();
    newFile += "/" + ReportsConstants.TMPDIR  + "/";
    newFile += theFile.getName();

    //date pattern is: day of week (3 chars)_month(3 chars)_
    String datePattern = "EEE_MMM_d_H:mm:SS";
    Date today = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
    String filenameExt = formatter.format(today);
    newFile +=  "." + filenameExt;
    File theNewFile = new File(newFile);
    theFile.renameTo(theNewFile);
  }

  /**
   * Set name of files with given extension
   * @param dir directory path to file
   * @param extname suffix of file
   * @return Full path to filename
   */
  private String setFilenames(String dir, String extname) {
    String ext = ReportsConstants.getExtension(extname);
    String filename =  reportsDataPath + "/";
    filename +=  dir + "/";
    filename += panelName + "_";
    filename += userId.toString();
    filename +=  ext;

    if (showDebug) {
      LogMessage.println(extname + " File: " + filename);
    }
    return filename;
  }


  /**
   * Determine if reviewer-input personal conflict  (firt, last, inst) matches that if
   * any PIs/CoIs for proposals in panel. Checks for matches with multiple proposals.
   * Sets/unsets cont contact and prelimGrade conflicts
   *  @param contact [first name, last name, institute]
   *  @param matches hashmap of propnum, conflictValue
    * @return Boolean of whether existing conflict was removed
   */
  public boolean matchContacts(String[] contact, Map<String,String> matches){
    String pFirst = contact[ReportsConstants.CONTACTFIRST],
           pLast = contact[ReportsConstants.CONTACTLAST],
           pInst = contact[ReportsConstants.CONTACTINSTITUTE];
    /*  Proposers names stored in proposal..person_short are sometimes of the form
     * "F. Last" or "F.-M Last". Proposal.java:isConflict only checks first initial.
     *  matchContacts follows suit.
     * */
    boolean removedConflict = false;
    for (Proposal prop: dblist) {
      String propNum = prop.getProposalNumber();
      // No cont flag + prelim grade means it's not a user entered conflict, always skip
      boolean existingContactConflict  = prop.getContactConflict().equalsIgnoreCase("C") ;
      if (prop.getPrelimGradeConflict().equalsIgnoreCase("C") &&
          !existingContactConflict) {
        continue;
      }

      // First check if last, inst, first initial, inst  matches PI.
      // last is checked first since it will more quickly rule out a match.
      if (prop.getPILast().equalsIgnoreCase(pLast) &&
          prop.getPIFirst().regionMatches(true, 0,pFirst,0,1) &&
          prop.getPIInstitution().equalsIgnoreCase(pInst)
          ){
        matches.put(propNum, "C");
        prop.setContactConflict("C");
        prop.setPrelimGradeConflict("C");
        contact[ReportsConstants.CONTACTPROPSAL] = propNum;

      // Next check cois. Stop when a single CoI is matched since conflict is for entire Proposal.
      } else {
        for (User coi: prop.getCoIList()) {
          if (coi.getUserName().equalsIgnoreCase(pLast) &&
              coi.getUserFirst().regionMatches(true, 0,pFirst,0,1) &&
              coi.getUserInstitution().equalsIgnoreCase(pInst)){
            matches.put(propNum, "C");
            prop.setContactConflict("C");
            prop.setPrelimGradeConflict("C");
            contact[ReportsConstants.CONTACTPROPSAL] = propNum;
            break;
          }
        }
      }
      // Remove cont conflict  if user removes it.
      if (existingContactConflict &&  !matches.containsKey(propNum))  {
        matches.put(propNum, "");
        prop.setContactConflict("");
        prop.setPrelimGradeConflict("");
        removedConflict = true;
      }
    }
    return removedConflict;
  }


  public void  sort(String sortKey)
  {
    Comparator cmp;

    if (sortKey.indexOf("Grade") >= 0){
      cmp = new PGradeComparer();
    }
    else if (sortKey.indexOf("P.I") >= 0){
      cmp = new PPIComparer();
    }
    else if (sortKey.indexOf("Title") >= 0){
      cmp = new PTitleComparer();
    }
    else if (sortKey.indexOf("Con") >= 0){
      cmp = new PConflictComparer();
    }
    else {
      cmp = new PNumberComparer();
    }

    Collections.sort(dblist,cmp );
  }


}  // end of class

