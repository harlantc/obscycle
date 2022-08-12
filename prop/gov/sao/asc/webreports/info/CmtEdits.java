// Copyright (c) 2003-2016, Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS
//                               INFO Package
//--------------------------------------------------------------------------
//                               CmtEdits
//****************************************************************************
package info;


import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import ascds.LogMessage;
import ascds.FileUtils;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/** *********************************************************
 * This class contains all the information needed to manage
 * private cmtEdits for a review report.  An instance of this class is 
 * needed for each user and each proposal.
 */
public class CmtEdits {
  private String proposalNumber;
  private String panel;
  private String reportsDataPath;
  private String cmtEditsFilename;


  /**
   * Constructor
   *
   * @param iProposalNumber  proposal number 
   * @param iPanel  panel name 
   * @param iPath  directory path for cmt files  
   */
  public CmtEdits(String iProposalNumber, String iPanel,String iPath )
  {
    String tname;
    proposalNumber = iProposalNumber;
    panel = iPanel;
    reportsDataPath = iPath;


    StringBuffer filenamePath = new StringBuffer(reportsDataPath);
    filenamePath.append("/cmtedits/");
    filenamePath.append(panel);
    filenamePath.append("_");
    filenamePath.append(proposalNumber);
    filenamePath.append(".cmts");
    cmtEditsFilename = filenamePath.toString();
  }


  /**
   * save cmtEdits for current user and proposal number
   *
   * @param cmts  comment text input
   * @param revLast  last name of reviewer
   * @exception Exception unable to save comments
   */
  public void saveCmtEdits(String cmts,String revLast)
	throws Exception
  {
    int ii = 0;

    try {
      String tmp = "";
      if (cmts != null) tmp = cmts.trim();
      if (tmp.length() <= 0) {
         // don't save off anything, leave as is
      }
      else {
        File theFile = new File(cmtEditsFilename);
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
          FileUtils.copy(cmtEditsFilename,newFile);
          LogMessage.println("COPY Comments: " + cmtEditsFilename + " to " + newFile);
          
        }
        // get a file channel
        FileChannel fileChannel = new FileOutputStream(theFile, false).getChannel();
        // get an exclusive lock on this channel
        while (ii < 3) {
          FileLock lock = null; 
          try {
            lock = fileChannel.tryLock();
          }
          catch (Exception exc) {
              LogMessage.printException(exc);
          }
            
          if ( lock != null) {
            ii=99;

            String datePattern = new String("EEE MMM d H:mm");
            Date today = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
            String tstr = formatter.format(today) + " -- " + revLast;
            tstr += "\n" + cmts + "\n\n";
            fileChannel.write(ByteBuffer.wrap(tstr.getBytes()));
            //Thread.sleep(15000); // testing
            lock.release();
            fileChannel.close();
            FileUtils.setPermissions(cmtEditsFilename,"660");
  
          }
          ii++;
          if (ii <3) {
            LogMessage.println("Waiting for lock to open");
	    //Thread.sleep(10000); // testing
          }
        }
      }
    }
    catch (Exception exc) {
       
      LogMessage.println("Unable to save cmtEdits file for " + proposalNumber + " : " + panel);
      LogMessage.printException(exc);
      throw new Exception ("Unable to save cmtEdits file for " + proposalNumber + " : " + panel);
    }
    if (ii!= 0 && ii < 5) {
      LogMessage.println("Never got a lock for cmtEdits file " + proposalNumber + " : " + panel);
      throw new Exception ("Unable to save cmtEdits file for " + proposalNumber + " : " + panel);
    }
       

  }
  /**
   * Returns last modified date of comment edits file
   *
   * @return String last modified date
   **/
  public String getLastModified()
  {
    String theDate = "";
    try {
      File theFile = new File(cmtEditsFilename);
      if (theFile.exists()) {
        Date cfileDate = null;
        String cdatePattern = new String("dd MMM HH:mm");
        SimpleDateFormat csdf = new SimpleDateFormat(cdatePattern);
        cfileDate = new Date(theFile.lastModified());
        theDate = csdf.format(cfileDate);
      }
    } catch (Exception exc) {
      LogMessage.printException(exc);
    }
    return theDate;
  }




  /**
   * Reads cmtEditsfile for current panel and proposal number
   *
   * @return cmtEdits  text 
   */
  public String readCmtEdits()
  {
    String cmtEdits = "";

    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(cmtEditsFilename));
      // skip the first line with date/user
      String nextLine = in.readLine();
      while ((nextLine = in.readLine()) != null) {
        cmtEdits += nextLine + "\n";
      }
      in.close();
    }
    catch (FileNotFoundException ioe) {
      LogMessage.println("File not found for " + cmtEditsFilename);
    }
    catch (IOException ioe) {
      LogMessage.println ("Unable to read in " + cmtEditsFilename);
    }

    return cmtEdits;
  }


}

