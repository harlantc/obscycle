package info;

import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Date;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

//Needed for the Java Mail library
import javax.mail.internet.*;
import javax.mail.*;

import ascds.LogMessage;
import ascds.FileUtils;
import db.DBConnection;

// The User class 

public class User {
    private int userID;
    private String memberType;
    private String userType;
    private String userName;
    private String userFirst;
    private String userEmail;
    private Institution userInstitution;
    private String panelName;
    private boolean allowedAccessNow; //can be used to prevent access to a certain panel
    private boolean allowedLPAccess; //can be used to prevent edit access to LPs 
    private static String currentUsersFileStr;
    private static int timeout; //default is none, -1
    private boolean allowEdit;
    private String emailAddress;
    

    private static int USERID = 0;
    private static int USERTYPE = 1;
    private static int MEMBERTYPE = 2;
    private static int MODE = 3;
    private static int USERNAME = 4;
    private static int EMAILADDRESS = 5;
    private static int PANELNAME = 6;
    private static int ALLOWEDIT = 7;
    private static int ALLOWEDACCESS =8;
    private static int LOGINDATE = 9;


    //like type, but defines the current mode of the user, ie. when a chair is acting like a reviewer
    private String mode;

    public User() {
	mode = new String("");
	allowedAccessNow = true;
	allowedLPAccess = false;
	allowEdit = true;
    }
    
    public User(int inputID, String reportsDataPath) {
	allowedLPAccess = false;
	try {
	    mode = new String("");
	    userID = inputID;
	    DBConnection dbConnect = new DBConnection(reportsDataPath, false);
	    dbConnect.loadUser(this);
	    allowedAccessNow = true;
	    allowEdit = true;

	} catch(IOException ioex) {
	    LogMessage.println(ioex.getMessage());
	    LogMessage.println("Caught IO exception connecting to dB");

	    //Set the values to default values
	    mode = new String("");
	    userID = inputID;
	    memberType = ReportsConstants.NONE;
	    userType = ReportsConstants.NONE;
	    userName = ReportsConstants.NONE;
	    userFirst = ReportsConstants.NONE;
	    allowedAccessNow = false;

	} catch(Exception ex) {
	    LogMessage.println("Error in User constructor");
	    LogMessage.println(ex.getMessage());
	}
	
    }


    public User(String inUserName, String type) {
	userName = inUserName;
	userFirst = new String("");
	userType = type;
	memberType = type;
	mode = type;
        userInstitution = new Institution("");
        userEmail = new String("");
	allowedAccessNow = true;
	allowedLPAccess = false;
	allowEdit = true;
    
    }

    public boolean userExists() {
	if(userName != null) {
	    return true;
	} else {
	    return false;
	}
    }

    public void setUserName(String inUserName) {
	userName = inUserName;
    }
    public void setUserFirst(String inStr) {
	userFirst = inStr;
    }
    public void setUserInstitution(String inStr) {
	userInstitution = new Institution(inStr);
    }
    public void setUserEmail(String inStr) {
	userEmail = inStr;
    }


    public void setUserType(String inputType) {
	userType = inputType;
	memberType = inputType;
	
	if(userType.equals(ReportsConstants.DEPUTYCHAIR)) {
	  userType = ReportsConstants.CHAIR;
	}
	if (userType.equals(ReportsConstants.PUNDITCHAIR) ||
	    userType.equals(ReportsConstants.PUNDITDEPUTY) ) {
	  userType = ReportsConstants.CHAIR;
	}
    }
    private void setMemberType(String inputMemberType) {
	memberType = inputMemberType;
    }

    public void setPanelName(String inputPanelName) {
	panelName = inputPanelName;
    }

    public void setMode(String inputMode) {
	mode = inputMode;
    }

    public void setUserID(int inputID) {
	userID = inputID;
    }

    public void setAllowedAccessNow(boolean inputAccess) {
	allowedAccessNow = inputAccess;
    }

    public void setAllowedLPAccess(boolean inputAccess) {
	allowedLPAccess = inputAccess;
    }

    public void setAllowedToEdit(boolean inAllowEdit) {
	allowEdit = inAllowEdit;
    }

    public void setAllowedToEdit(String inAllowEdit) {
	allowEdit = false;
	if(inAllowEdit.equals("true")) {
	    allowEdit = true;
	}
    }

    public void setEmailAddress(String inputEmailAddress) {
	emailAddress = new String(inputEmailAddress);
    }


    public int getUserID() {
	return userID;
    }

    public String getType() {
	return userType;
    }
    public String getMemberType() {
	return memberType;
    }

    public String getPanelName() {
	return panelName;
    }

    public String getMode() {
	return mode;
    }

    // this is the email from the proposal database
    public String getUserEmail() {
        return userEmail;
    }
    public String getUserInstitution() {
        return userInstitution.getInstitutionName();
    }
    public String getUserModifiedInstitution() {
        return userInstitution.getModifiedInstitution();
    }

    // this is the email passed in from CDO's reviewer page
    public String getEmailAddress() {
	return emailAddress;
    }


    public boolean isAllowedAccessNow() {
	return allowedAccessNow;
    }


    public boolean isAllowedToEdit() {
	return allowEdit;
    }

    public boolean canAdminEdit() {
	if(isAdmin() && allowEdit) {
	    return true;
	} else {
	    return false;
	}
    }

    public boolean isAllowedLPAccess() {
	boolean accessLPs = false;
	
	Properties reportsProperties = Reports.getProperties(); 
	String accessDateFile  = reportsProperties.getProperty("reports.access.date.file");
	accessLPs = Reports.chairLPAccessReports(accessDateFile);
	
	//Only admins and chairs can access the LP reports, and only
	//after the access date specified in the config file.
	if(accessLPs && (isChair() || isAdmin() || isPundit())) {
	    accessLPs = true;
	} else {
	    accessLPs = false;
	}

	return accessLPs;
    }


    public boolean isDeputyChair() {
	if(memberType != null && 
           (memberType.equals(ReportsConstants.DEPUTYCHAIR) ||
            memberType.equals(ReportsConstants.PUNDITDEPUTY) )) {
	    return true;
	} else {
	    return false;
	}
    }
    public boolean isChair() {
	if(memberType != null && 
           (memberType.equals(ReportsConstants.CHAIR) ||
            memberType.equals(ReportsConstants.PUNDITCHAIR)) ) {
	    return true;
	} else {
	    return false;
	}
    }
    public boolean inChairMode() {
	if(userType != null && userType.equals(ReportsConstants.CHAIR)  ) {
	    return true;
	} else {
	    return false;
	}
    }

    public boolean isReviewer() {
	if(userType != null &&
           userType.equals(ReportsConstants.REVIEWER)) {
	    return true;
	} else {
	    return false;
	}
    }

    public boolean isAdmin() {
	if(userType != null &&
	   userType.equals(ReportsConstants.ADMIN)) {
	    return true;
	} else {
	    return false;
	}
    }

    public boolean isFacilitator() {
	if(userType != null &&
	   userType.equals(ReportsConstants.FACILITATOR)) {
	    return true;
	} else {
	    return false;
	}
    }

    public boolean isPundit() {
	if(memberType != null &&
	   (memberType.equals(ReportsConstants.PUNDIT) ||
	    memberType.equals(ReportsConstants.PUNDITDEPUTY) ||
	    memberType.equals(ReportsConstants.PUNDITCHAIR)) ) {
	    return true;
	} else {
	    return false;
	}
    }




    public boolean isDeveloper() {
	if(userType != null &&
	   userType.equals(ReportsConstants.DEVELOPER)) {
	    return true;
	} else {
	    return false;
	}
    }

    public boolean editLPLink(String reportsDataPath) {
	boolean editLPLink = false;
	try {
	    DBConnection dbConnect = new DBConnection(reportsDataPath, false);

	    if(inChairMode()) {
		editLPLink = dbConnect.chairHasLP(panelName);
	    }

	} catch(Exception ex) {
	    LogMessage.print("User::editLPLink - Caught exception - ");
	    LogMessage.println(ex.getMessage());
	}

	return editLPLink;
    }



    public String getUserName() {
	return userName;
    }
    public String getUserFirst() {
	return userFirst;
    }
    
    public void print() {
	LogMessage.println("User ID = " +  userID + ", name = " + userName + ", type = " + userType);
    }

    //Static methods 
    //Handle keeping track of current users

    public static void setCurrentUsersFile(String currentUsersFile) {
	currentUsersFileStr = currentUsersFile;
	timeout = -1;  //If no timeout is specified, the default is none
    }

    public static void setTimeoutPeriod(int inTimeout) {
	timeout = inTimeout;
    }

    public static int getTimeoutPeriod() {
	return timeout;
    }


    public void emailUser(ReviewReport rr, String lockedReportEditor) {
	String proposalNumber = rr.getProposalNumber();
	String message = null;
	LogMessage.println("User::emailUser: Emailing user " + userName + " who has proposal number " +
			   proposalNumber + " locked.");

	if(emailAddress == null) {
	    message = new String("User::emailUser: Cannot email user with locked report. ");
	    message += "No email address is given.";
	    LogMessage.println(message);
	} else {
	    //String testEmailAddress = new String("latha@head.cfa.harvard.edu"); //For Testing
	    //emailAddress = testEmailAddress; //For testing

	    LogMessage.println("Emailing user (" + emailAddress + ") with locked report #" + proposalNumber);
	    message = new String("Another user is trying to access a review report for proposal number ");
	    message += proposalNumber + " which is locked by " + lockedReportEditor + ".";
	    message += "If you are no longer editing the report, please respond to this email so we may unlock it.";
	    
	    javax.mail.Session mailSession = javax.mail.Session.getDefaultInstance(Reports.getProperties()); 
	    MimeMessage mimeMessage = new MimeMessage( mailSession );
	    
	    String fromEmail = null; 
	    Properties reportsProperties = Reports.getProperties(); 
	    fromEmail  = reportsProperties.getProperty("from.email.address");
	    if(fromEmail == null || fromEmail.length() == 0) {
		fromEmail = new String("cxoreview@head.cfa.harvard.edu");
	    }
	    try {
		//mimeMessage.setFrom(new InternetAddress("cxoreports@head.cfa.harvard.edu"));
		mimeMessage.setFrom(new InternetAddress(fromEmail));
		mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
		mimeMessage.setSubject( "Chandra Review Report: Locked Report #" + proposalNumber );
		mimeMessage.setText( message );
		
		Transport.send( mimeMessage );
	    } catch(Exception mailEx) {
		System.err.println("User::emailUser:Caught exception : " + mailEx.getMessage());
	    }
	}
    }

    /*
     * emailUser
     * Routine sends email to the user who has locked the file
     * @param rr  the review report that is locked
     */

    public void emailUser(ReviewReport rr) {
	String lockedReportEditor = new String("you");
	emailUser(rr, lockedReportEditor);
    }




    /**
     * addCurrentUser
     *
     * This routine is used to add a user to the file of current users.
     */
    public void addCurrentUser() {
	try {
	    //Check if file exists. If not, create it. If it does exist, need to
	    //open it and append to the file.
	    File currentUsersFile = new File(currentUsersFileStr);
	    if(!currentUsersFile.exists()) {
		currentUsersFile.createNewFile();
		FileUtils.setPermissions(currentUsersFileStr,"660");
	    }
 
	    //Check if the user already exists in the file - if so, we'll remove
	    //the user and then add them 
	    User theUser = User.getUser(userID);
	    
	    if(theUser != null) {
		//User already exists in the file
		int removedUser = User.removeUser(userID);
		if(removedUser == -2) {
		    LogMessage.println("Error removing duplicate user from current users file: " + userID);
		}
	    }

	    //At this point, if the user already existed in the file, he is removed, and
	    //can now be added to the file. Otherwise, the user is new and can also
	    //be added to the file.
	    PrintWriter currentUsersPW = new PrintWriter(new FileWriter(currentUsersFile, true));
	    int reportsID = userID;
	    Date dateAllowedAccess = new Date();
	    Date timeStamp = new Date();
	    //Date timeStampDate = new Date();
	    //DateFormat timeStampDF = DateFormat.getDateTimeInstance();
	    //String timeStamp = timeStampDF.format(timeStampDate);
	    //currentUsersPW.print(reportsID + ReportsConstants.CURRENTUSERSSEP);
	    currentUsersPW.print(userID + ReportsConstants.CURRENTUSERSSEP);
	    currentUsersPW.print(userType + ReportsConstants.CURRENTUSERSSEP);
	    currentUsersPW.print(memberType + ReportsConstants.CURRENTUSERSSEP);
	    currentUsersPW.print(mode + ReportsConstants.CURRENTUSERSSEP);
	    currentUsersPW.print(userName + ReportsConstants.CURRENTUSERSSEP);
	    currentUsersPW.print(emailAddress + ReportsConstants.CURRENTUSERSSEP);
	    currentUsersPW.print(panelName + ReportsConstants.CURRENTUSERSSEP);
	    currentUsersPW.print(allowEdit + ReportsConstants.CURRENTUSERSSEP);
	    currentUsersPW.print(dateAllowedAccess + ReportsConstants.CURRENTUSERSSEP);
	    currentUsersPW.println(timeStamp);
	    currentUsersPW.close();
	} catch (Exception ex) {
	    LogMessage.println("Error: Caught exception in User::addCurrentUser: " + ex.getMessage());
	}
    }


    /**
     * getUser
     *
     * This routine is used to add a create a user object from the file of current
     * users, if the user is currently logged in. This routine will not return an
     * User object if the user has timed out, or is no longer in the file.
     * @param reportsID the current report
     * @return User the user object
     */

    //The string of containing the filename of current users should be a static
    //string, for this class.  That way each call doesn't need to pass this information
    //in, and is shared for all User objects.
    public static User getUser(int reportsID) {
	User theUser = null;
	File currentUsersFile = new File(currentUsersFileStr);
	
	if(!currentUsersFile.exists()) {
	    LogMessage.print("Error: Cannot find user with reportsID " + reportsID);
	    LogMessage.println(" as current users file " + currentUsersFileStr + " does not exist.");
	    //throw exception
	    return null;
	}

	try {
	    BufferedReader currentUsersBR = new BufferedReader(new FileReader(currentUsersFile));
	    String inputLine;
	    String[] inputArray;
	    String currentReportsIDStr;
	    int currentReportsID;
	    
	    while( (inputLine = currentUsersBR.readLine()) != null) {
		inputArray = inputLine.split(ReportsConstants.CURRENTUSERSSEP);
		currentReportsIDStr = inputArray[0];
		currentReportsID = Integer.parseInt(currentReportsIDStr);
		
		if(currentReportsID == reportsID) {
		    //No need to check for timeout which is handled by jsps
		    theUser = new User();
		    theUser.setUserID(Integer.parseInt(inputArray[USERID]));
		    theUser.setUserType(inputArray[USERTYPE]);
		    theUser.setMemberType(inputArray[MEMBERTYPE]);
		    theUser.setMode(inputArray[MODE]);
		    theUser.setUserName(inputArray[USERNAME]);
		    theUser.setEmailAddress(inputArray[EMAILADDRESS]);
		    theUser.setPanelName(inputArray[PANELNAME]);
		    theUser.setAllowedToEdit(inputArray[ALLOWEDIT]);

		    break;
		}
	    }
            currentUsersBR.close();

	} catch(Exception ex) {
	    LogMessage.println("Error: Caught exception in User::getUser - " + ex.getMessage());
	}

	return theUser;
    }

    
    /*
     * isValidUser
     * This routine will just return true or false, if the user is found in the current
     * users file.
     * @return int  returns VALIDENTRY if valid user, else INVALIDENTRY
     */
    public int isValidUser() {
	return(User.isValidUser(userID));
    }


    /**
     * isValidUser
     * This routine just takes the userID (aka reportsID) as input
     * @param  reportsID  user id
     * @return int  returns VALIDENTRY if valid user, else INVALIDENTRY
     */
    public static int isValidUser(int reportsID) {
	File currentUsersFile = new File(currentUsersFileStr);
	int validUser = ReportsConstants.INVALIDENTRY;
	if(!currentUsersFile.exists()) {
	    LogMessage.print("Error: Cannot find user with reportsID " + reportsID);
	    LogMessage.println(" as current users file " + currentUsersFileStr + " does not exist.");
	    //throw exception
	    return validUser;
	}

	try {
	    BufferedReader currentUsersBR = new BufferedReader(new FileReader(currentUsersFile));
	    String inputLine;
	    String[] inputArray;
	    int currentReportsID;
	    
	    while( (inputLine = currentUsersBR.readLine()) != null) {
		inputArray = inputLine.split(ReportsConstants.CURRENTUSERSSEP);
		currentReportsID = Integer.parseInt(inputArray[USERID]);

		if(currentReportsID == reportsID) {
		    validUser = ReportsConstants.VALIDENTRY;
		    break;
		}
	    }
            currentUsersBR.close();
	} catch(Exception ex) {
	    LogMessage.println("Error: Caught exception in User::isValidUser - " + ex.getMessage());
	}


	return validUser;
    }




    /**
     * UserTimedOut
     *
     * This routine determines if the user has timed out.  The input date string
     * is taken from the file of current users.  The routine needs to parse this,
     * add on the timeout value, and then compare to the current time to determine
     * if the timout period has elapsed.
     * @param reportsID  id of report
     * @param loginDateStr  input data string
     * @return boolean  true if user has timed out
     */
    private static boolean UserTimedOut(int reportsID, String loginDateStr) {
	boolean timedOut = false;
	//LogMessage.println("In User::UserTimedOut - timeout = " + timeout);

	try {
	    //Wed Feb 22 14:03:24 EST 2006
	    DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
	    Date loggedInDate = formatter.parse(loginDateStr);
	    
	    //Calendar logoutDateCal = Calendar.getInstance();
	    Calendar logoutDateCal = Calendar.getInstance();
	    logoutDateCal.setTime(loggedInDate);
	    logoutDateCal.add(Calendar.MINUTE, timeout);
	    Date logoutDate = logoutDateCal.getTime();
	    Date currentDate = new Date();
	    
	    //LogMessage.println("Current date = " + currentDate);
	    //LogMessage.println("Login date = " + loggedInDate);
	    //LogMessage.println("Logout date = " + logoutDate);
	    

	    if(currentDate.after(logoutDate)) {
		//User has timed out, remove the user from the list of current users
		timedOut = true;
		removeUser(reportsID);
		LogMessage.println("User " + reportsID + " has timed out");
	    } 
	} catch(Exception ex) {
	    LogMessage.println("Caught exception in User:UserTimedOut - " + ex.getMessage());
	}

	return timedOut;
    }


    /**
     * removeUser
     * This routine is used to remove a user from the file of current users.
     * @param reportsID  id of report
     * @return int  report ID on success, -2 on error
     */
    public static int removeUser(int reportsID) {
	File currentUsersFile = new File(currentUsersFileStr);
	
	if(!currentUsersFile.exists()) {
	    LogMessage.print("Error: Cannot find user with reportsID " + reportsID);
	    LogMessage.println(" as current users file " + currentUsersFileStr + " does not exist.");
	    //throw exception
	    return -2;
	}
	
	try {
	    BufferedReader currentUsersBR = new BufferedReader(new FileReader(currentUsersFile));
	    String inputLine;
	    String[] inputArray;
	    ArrayList<String> currentUsers = new ArrayList<String>();
	    int currentReportsID;
	    
	    while( (inputLine = currentUsersBR.readLine()) != null) {
		inputArray = inputLine.split(ReportsConstants.CURRENTUSERSSEP);
		currentReportsID = Integer.parseInt(inputArray[USERID]);
		if(currentReportsID != reportsID) {
		    currentUsers.add(inputLine);
		} else {
		    LogMessage.println("Removing user with reports ID = " + reportsID);
		}
	    }
            currentUsersBR.close();

	    //Now write the current users back to the file; don't append, but overwrite the file
	    PrintWriter currentUsersPW = new PrintWriter(new FileWriter(currentUsersFile, false));
	    for(int index=0; index < currentUsers.size(); index++) {
		currentUsersPW.println((String)currentUsers.get(index));
	    }
	    currentUsersPW.close();

	    
	} catch(Exception ex) {
	    LogMessage.println("Error: Caught exception in User::getUser - " + ex.getMessage());
	}

	return reportsID;
    }



}
