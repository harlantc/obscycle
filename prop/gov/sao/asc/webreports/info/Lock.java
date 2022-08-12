// Copyright (c) 2003, Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS
//                               INFO Package
//--------------------------------------------------------------------------
//                               Lock
//****************************************************************************
package info;


import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import db.DBConnection;
import ascds.LogMessage;
import ascds.FileUtils;

/** *********************************************************
 * This class contains all the information needed to manage
 * a lock for a review report.  An instance of this class is 
 * needed for each peer review report.
 */
public class Lock {
    private String type;
    private String proposalNumber;
    private String panelName;
    private String reportsDataPath;
    private String lockFilename;


    /**
     * Constructor
     *
     * @param iProposalNumber  proposal number 
     * @param iPanelName  panel name
     * @param iType  type of review report for this lock
     * @param iPath  directory path for lock files  
     */
    public Lock(String iProposalNumber,String iPanelName,String iType,String iPath)
    {
	init();
        proposalNumber = iProposalNumber;
        type = iType;
        panelName = iPanelName;
        reportsDataPath = iPath;

        if (type.equals(ReportsConstants.LP) || 
            type.equals(ReportsConstants.FINAL)) {
          panelName = "bpp";
        }


        if (iProposalNumber != null && panelName != null && iType != null &&
	iPath != null ) {
	  StringBuffer filenamePath = new StringBuffer(reportsDataPath);
	  filenamePath.append(panelName);
	  filenamePath.append("_");
	  filenamePath.append(proposalNumber);
	  filenamePath.append(".");
	  filenamePath.append(type);
	  filenamePath.append(ReportsConstants.LOCKEXT);
          lockFilename = filenamePath.toString();

        }
        else {
          lockFilename = null;
        }
    }


    private void init() {
	type = new String("");
	panelName = new String("");
        reportsDataPath = new String("");
    }


    /** 
     * Creates a lock for the specified user
     *
     * @param userName  user creating lock 
     * @param userID    pers_id of user creating lock
     * @return boolean  true if lock created
     */

    public boolean lock(String userName,int userID) 
    {
      boolean returnVal = false;
       

      try {
        // is file locked by other than the current user?
        if (isLocked(userName)) {
          LogMessage.println("Lock file already exists for "  + lockFilename);
        } else {
          File lockFile = new File(lockFilename);
          lockFile.createNewFile();
          FileWriter lockFileFW = new FileWriter(lockFile);
          PrintWriter lockFilePW = new PrintWriter(lockFileFW);
  
          //keep track of who is editing the file
          String lockFileInfo = new String(userName + ":" + userID);
          lockFilePW.println(lockFileInfo); 
          lockFilePW.close();
          lockFileFW.close();
          FileUtils.setPermissions(lockFile,"660");
          returnVal = true;
        }
      } catch(Exception ex) {
        LogMessage.println("Error: Caught exception creating new lock in  Lock::lock");
      }

      return returnVal;
    }

           
    /**
     *
     * This routine deletes the lock file associated with a report. 
     *
     * @param userName  user name to unlock
     * @return boolean  true if lock successfully deleted
     */
    public boolean unlock(String userName) {
       boolean returnVal = false;

       if (userName == null || userName.equals(whoLocked())) {
          LogMessage.println("Unlock for " + userName);
          returnVal = unlock();
       }
       return returnVal;
    }

    /**
     * This routine deletes the lock file associated with a report. 
     *
     * @return boolean  true if lock successfully deleted
     */
    public boolean unlock() {
	boolean returnVal = true;
        try {
	  File lockFile = new File(lockFilename);
	  if(lockFile.exists()) {
	      returnVal = lockFile.delete();
	  } 
        } 
        catch (Exception exc) {
          LogMessage.printException(exc);
        }
	
	return returnVal;
    }

    /**
     * This routine checks if a lock exists for this report
     *
     * @return boolean  true if lock exists 
     */
    public boolean isLocked() {
       return(isLocked((String)null));
    }

    /**
     * This routine checks if a lock exists for this report by 
     * other than the specified user.
     *
     * @param userName  name of user
     * @return boolean  true if lock exists for other than this user on this report
     */
    public boolean isLocked(String userName) 
    {
      boolean returnVal = false;
      try {
	File lockFile = new File(lockFilename);
	if (lockFile.exists()) {
          if (!isOldLock()) {
            if (userName == null || !userName.equals(whoLocked())) {
	      returnVal = true;
            }
          }
          else {
            // old lock so remove it
            unlock();
	  } 
	} 
      } 
      catch (Exception exc) {
        LogMessage.printException(exc);
      }
	
      return returnVal;
    }

    /**
     * This routine finds who locked the current report
     *
     * @return String  name of user who locked the current report or null
     */
    public String whoLocked() 
    {
      int lockFileUserId = -1;
      String lockFileUserName;
   
      lockFileUserName = whoLocked(lockFileUserId);
      return lockFileUserName;
    }

    /**
     * This routine finds who locked the current report 
     *
     * @param lockFileUserId  set to the user id of the current lock
     * @return String  name of user who locked the current report or null
     */
    public String whoLocked(int lockFileUserId) 
    {
      String lockFileUserName = new String("");
      try {
	File lockFile = new File(lockFilename);
	LogMessage.println("Getting username from lockfile: " + lockFilename);
	if(lockFile.exists()) {
	  FileReader fileR = new FileReader(lockFile);
	  BufferedReader lockFileBF = new BufferedReader(fileR);
	  String inputLine = null;
	  inputLine = lockFileBF.readLine();

	  if(inputLine != null) {
            String[] lockFileInfo = inputLine.split(":");
            lockFileUserName = lockFileInfo[0];
	    lockFileUserId = Integer.parseInt(lockFileInfo[1]);
          }
          lockFileBF.close();
          fileR.close();
	} 
	    
      } catch(Exception ex) {
	ex.printStackTrace();
	LogMessage.println("Caught exception in Lock:whoLocked");
	LogMessage.println(ex.getMessage());
      }

      return lockFileUserName;
    }


    /**
    *
    * This routine will create the filename which is by panel,proposal number
    * type of report with a .LOCK extension
    * @return String name of lock file
    */
    private String getLockFilename() {

        return lockFilename;
    }
	

    private boolean isOldLock() {

      boolean returnVal = false;

      try {
        File lockFile = new File(lockFilename);
        long lockFileCreatedms = lockFile.lastModified();
        long currentDatems = (new Date()).getTime();
        long timeSinceLockCreated = currentDatems - lockFileCreatedms;
        long twoHoursInMS = 2 * 60 * 60 * 1000;
        long sessionTimeInMS = 0 ;
        int sessionTimeout = User.getTimeoutPeriod();
        if(sessionTimeout != -1) {
          sessionTimeInMS = sessionTimeout * 60 * 1000;
        }

        //If the amount of time since the lock file was created exceeds
        //the session timeout, then remove the lock file and allow the
        //user to edit this file.
        if(timeSinceLockCreated > twoHoursInMS ||
           (sessionTimeout != -1 && timeSinceLockCreated > sessionTimeInMS)) {
          returnVal = true;
        }
      }
      catch (Exception exc) {
        LogMessage.printException(exc);
      }

      return returnVal;
   }


}

