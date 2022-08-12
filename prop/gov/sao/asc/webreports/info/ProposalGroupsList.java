// Copyright (c) 2003, Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS 
//                               INFO Package
//--------------------------------------------------------------------------
//                             ProposalGroupsList
//****************************************************************************
package info;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.lang.*;
import db.DBConnection;
import ascds.LogMessage;
import ascds.FileUtils;
import info.*;


/** 
  * This class supports the proposal group assignments for a panel
 */
@SuppressWarnings("unchecked")
public class ProposalGroupsList
{
  private String  reportsDataPath;
  private String  groupsFile;
  private String  panelName;
  private String  reviewerName;
  private Integer userId;
  private boolean showDebug;
  private Vector<Proposal>  dblist;
  
  

  /**
   * Constructor
   *
   * @param reportsDataPath  directory path for webreports files
   * @param showDebug flag indicating whether debug statements should be logged
  */
  public ProposalGroupsList (String reportsDataPath, boolean showDebug)
  {
    this.reportsDataPath = new String(reportsDataPath);
    this.showDebug = showDebug;

    dblist = new Vector<Proposal>();
  }

  /**
    * return reviewer name for these groups 
    *
    * @return String  name of the reviewer
   */
  public String getReviewerName() 
  {
    return this.reviewerName;
  }
  /**
    * return panel name for these groups 
    *
    * @return String  name of the panel
   */
  public String getPanelName() 
  {
    return this.panelName;
  }

  /**
    * load all proposals for this panel for the reviewer
    *
    * @param user  user id
    * @param reviewerName name of reviewer
    * @param panelName panel name
    * @exception Exception unable to load proposals
   */
  public void loadProposalsOnPanel(Integer user,String reviewerName,
	String panelName )
	throws Exception
  {
    DBConnection dbconn = null;

    this.panelName = new String(panelName);
    this.userId = user;
    this.reviewerName = reviewerName;

    setGroupsFilename();

    
    try {
      dbconn = new DBConnection(reportsDataPath,showDebug);

      if (dbconn != null) {
        dblist = dbconn.loadProposalsOnPanel(panelName);
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
   * Set the group identifier for the specified proposal number
   *
   * @param propnum  proposal number
   * @param groupName    group name for this proposal
   *
   */
  public void setGroupNameForProposal(String propnum,String groupName)
  {
    boolean foundMatch = false;
    Iterator ii = dblist.iterator();
    while ( ii.hasNext()  && !foundMatch) {
      Proposal prop = (Proposal)ii.next();
      if (prop.getProposalNumber().compareTo(propnum) == 0) {
        foundMatch=true;
        prop.setGroupName(groupName);
      }
    }
  }

  /**
   * read the proposal groups for this panel.
   * Groups file format: panel\tpropnum\tgroup.
   *
   * @exception IOException unable to read file
   */
  public void readGroups()  throws IOException
  {

    if (showDebug) {
      LogMessage.println("Parse groups file  " + groupsFile);
    }

    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(groupsFile));

      String nextLine;

      while ((nextLine = in.readLine()) != null) {
        // parse panel,propnum,group from file
        // if propnum , then set the groupName for the proposal object
        // else log fact that proposal not in list anymore.
        String propnum = new String("");
        String panel = new String("");
        String groupName = new String("");
        String sval; 
        StringTokenizer st = new StringTokenizer( nextLine,"\t",false);
        if (st.hasMoreTokens()) {
          panel = st.nextToken();
          if (st.hasMoreTokens()) {
            propnum = st.nextToken();
            if (st.hasMoreTokens()) {
              groupName = new String(st.nextToken());
            }
  
          }
        }
        if (propnum.length() > 0) {
          setGroupNameForProposal(propnum, groupName );
        }  
      }  
      in.close();
    }
    catch (FileNotFoundException ioe) {
      LogMessage.println("File not found for " + groupsFile);
    }
    catch (IOException ioe) {
      throw new IOException ("Unable to read in " + groupsFile);
    }

  }




  /**
   * Save the group assignments to a file for this panel.
   *
   * @exception IOException unable to save file
   */
  public void saveGroups() throws IOException
  {
    if (showDebug) {
      LogMessage.println("Parse groups file  " + groupsFile);
    }

    PrintWriter out;
    File theFile = new File(groupsFile);
    if (theFile.exists()) {
      String newFile = theFile.getParent();
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

    out = new PrintWriter(new FileWriter(groupsFile));

    Iterator ii = dblist.iterator();
    while ( ii.hasNext() ) {
      Proposal prop = (Proposal)ii.next();
      String groupName = prop.getGroupName();
      String pline = panelName + "\t";
      pline +=  prop.getProposalNumber() + "\t" ; 
      pline +=  groupName;
      out.println(pline);
    }
    if (out != null) {
      out.close();
      FileUtils.setPermissions(groupsFile,"660");
    }
  }


  /**
    * create the proposal groups file name: panelnn.groups
   */
  private void setGroupsFilename()
  {
    String ext = ReportsConstants.GROUPEXT;
    
    groupsFile =  reportsDataPath + "/" + ReportsConstants.GROUPDIR;
    groupsFile += "/panel" + panelName + ext;
 
  }



  public void  sort(String sortKey)
  {
    Comparator cmp;

    if (sortKey.indexOf("Group") >= 0){
      cmp = new PGroupComparer();
    }
    else if (sortKey.indexOf("P.I") >= 0){
      cmp = new PPIComparer();
    }
    else if (sortKey.indexOf("Title") >= 0){
      cmp = new PTitleComparer();
    }
    else {
      cmp = new PNumberComparer();
    }
    Collections.sort(dblist,cmp );
  }

  
}  // end of class

