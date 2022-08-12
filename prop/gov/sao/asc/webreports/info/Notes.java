// Copyright (c) 2003, Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS
//                               INFO Package
//--------------------------------------------------------------------------
//                               Notes
//****************************************************************************
package info;


import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import ascds.LogMessage;
import ascds.FileUtils;

/** *********************************************************
 * This class contains all the information needed to manage
 * private notes for a review report.  An instance of this class is 
 * needed for each user and each proposal.
 */
public class Notes {
  private String proposalNumber;
  private int    userID;
  private String userName;
  private String reportsDataPath;
  private String notesFilename;


  /**
   * Constructor
   *
   * @param iProposalNumber  proposal number 
   * @param iUserID  user id
   * @param iPath  directory path for lock files  
   * @param iUserName  user name
   */
  public Notes(String iProposalNumber,int iUserID,String iPath, String iUserName)
  {
    String tname;
    proposalNumber = iProposalNumber;
    userID = iUserID;
    reportsDataPath = iPath;


    StringBuffer filenamePath = new StringBuffer(reportsDataPath);
    filenamePath.append("/notes/");
    if (userID > 0) {
      filenamePath.append(userID);
    }
    else {
      tname = iUserName.replaceAll(" ","-");
      filenamePath.append(iUserName);
    }
    filenamePath.append("_");
    filenamePath.append(proposalNumber);
    filenamePath.append(".notes");
    notesFilename = filenamePath.toString();
  }


  /**
   * save notes for current user and proposal number
   *
   * @param notes  notes text input
   */
  public void saveNotes(String notes)
  {

    try {
      File theFile = new File(notesFilename);
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

      String tmp = notes.trim();
      if (tmp.length() <= 0) {
         theFile = new File(notesFilename);
         theFile.delete();
      }
      else {
        PrintWriter out;
        out = new PrintWriter(new FileWriter(notesFilename));
        out.println(notes);
        if (out != null) {
          out.close();
          FileUtils.setPermissions(notesFilename,"660");
        }
      }
    }
    catch (Exception exc) {
      LogMessage.println("Unable to save notes file for " + proposalNumber + " : " + userID);
      LogMessage.printException(exc);
    }

  }



  /**
   * Reads note file for current user and proposal number
   *
   * @return notes  notes text 
   */
  public String readNotes()
  {
    String notes = "";

    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(notesFilename));
      String nextLine;
      while ((nextLine = in.readLine()) != null) {
        notes += nextLine + "\n";
      }
      in.close();
    }
    catch (FileNotFoundException ioe) {
      LogMessage.println("File not found for " + notesFilename);
    }
    catch (IOException ioe) {
      LogMessage.println ("Unable to read in " + notesFilename);
    }

    return notes;
  }


}

