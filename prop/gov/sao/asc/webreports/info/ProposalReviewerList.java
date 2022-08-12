// Copyright (c) 2003, Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS 
//                               INFO Package
//--------------------------------------------------------------------------
//                             ProposalReviewerList
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
  * This class supports the proposal primary/secondary review assignments for a panel
 */
@SuppressWarnings("unchecked")
public class ProposalReviewerList
{
  private String  reportsDataPath;
  private String  panelName;
  private boolean showDebug;
  private Vector<Proposal>  dblist;
  
  

  /**
   * Constructor
   *
   * @param reportsDataPath  directory path for webreports files
   * @param showDebug flag indicating whether debug statements should be logged
  */
  public ProposalReviewerList (String reportsDataPath, boolean showDebug)
  {
    this.reportsDataPath = new String(reportsDataPath);
    this.showDebug = showDebug;

    dblist = new Vector<Proposal>();
  }

  /**
    * return panel name for these proposals 
    *
    * @return String  name of the panel
   */
  public String getPanelName() 
  {
    return this.panelName;
  }

  /**
    * load all proposals for multiple panels
    *
    * @param panelNames panel names
    * @exception Exception unable to load proosals
   */
  public void loadProposalsOnPanels( Vector<String> panelNames )
	throws Exception
  {
    DBConnection dbconn = null;
    try {

      String pnames = "";
      for (int ii=0;ii<panelNames.size();ii++) {
        if (ii > 0) pnames += ",";
        pnames += "'" + panelNames.get(ii) + "'"; 
        this.panelName += panelNames.get(ii) + " ";
      }

      dbconn = new DBConnection(reportsDataPath,showDebug);
      if (dbconn != null) {
        dblist = dbconn.loadProposalsOnPanel(pnames);
      }
      LogMessage.println("Added data for panel " + pnames + "=" + dblist.size());
    }

    catch (Exception sqle) {
      throw new Exception("An error occurred in retrieving proposals: " + sqle.toString());
    }
  }

  /**
    * load all proposals for this panel
    *
    * @param panelName panel name
    * @exception Exception unable to load proposals
   */
  public void loadProposalsOnPanel( String panelName )
	throws Exception
  {
    DBConnection dbconn = null;

    this.panelName = new String(panelName);

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



  public void  sort(String sortKey)
  {
    Comparator cmp;

    if (sortKey.indexOf("Primary") >= 0){
      cmp = new PPrimaryComparer();
    }
    else if (sortKey.indexOf("Secondary") >= 0){
      cmp = new PSecondaryComparer();
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

