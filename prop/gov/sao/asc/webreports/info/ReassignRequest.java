// Copyright (c) 2015, Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS
//                               INFO Package
//--------------------------------------------------------------------------
//                               ReassignRequest
//****************************************************************************
package info;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.text.SimpleDateFormat;

//Needed for the Java Mail library
import javax.mail.internet.*;
import javax.mail.*;

import ascds.LogMessage;
import ascds.FileUtils;

/** 
 * This class contains the ReassignRequest support.  This is for reviewers
 * who feel they have a conflict with an assigned report.
 */
public class ReassignRequest {
    private String  proposalNumber;
    private Integer reviewerID;
    private String  cmt;
    private String  reportType;
    private String  reassignFile;
    private String  reportsDataPath;
    private boolean showDebug=true;
    private User    theUser;

    public ReassignRequest(String iReportsDataPath, User iUser,String iType)
    {
       reportsDataPath = iReportsDataPath;
       proposalNumber="";
       reviewerID=0;
       cmt="";
       reassignFile="";
       theUser = iUser;
       reportType=iType;

    }
    public String SaveRequest(String iProp,Integer iReviewer,String iCmt)
    {
       boolean retval=true;
       proposalNumber = iProp;
       reviewerID = new Integer(iReviewer);
       cmt = iCmt;
       String msg = "Request successfully submitted.";
       setReassignFilename();
       try {
         writeFile();
         SendMessage();
       } 
       catch (Exception exc) {
         retval=false;
         msg = "Error occured submitting reassignment request. Please contact cxoreview";
         LogMessage.println("ReassignRequest: Save failed for " + reassignFile);
         LogMessage.println("ReassignRequest: Save failed for " + cmt);
         LogMessage.printException(exc);
       }
       return msg;
    }

    public boolean  GetRequest(String iProp,Integer iReviewer)
    {
       boolean retval=true;

       proposalNumber = iProp;
       reviewerID = new Integer(iReviewer);
       cmt="";
       setReassignFilename();
       try {
         readFile();
       } 
       catch (Exception exc) {
         retval=false;
         LogMessage.println("ReassignRequest: Read failed for " + reassignFile);
         LogMessage.printException(exc);
       }
       return retval;
    }

    public void copy(ReassignRequest irr) 
    {
       proposalNumber = irr.getProposalNumber();
       reviewerID = irr.getReviewerID();
       cmt = irr.getComment();
       reportsDataPath = irr.reportsDataPath;
       reassignFile = irr.reassignFile;
        
    }
    
    public String getComment()
    {
       return cmt;
    }
    public String getProposalNumber()
    {
       return proposalNumber;
    }
    public String getReportType()
    {
       return reportType;
    }
    public Integer getReviewerID()
    {
       return reviewerID;
    }
    private void setReassignFilename()
    {
      String ext = ReportsConstants.REASSIGNEXT;

      reassignFile =  reportsDataPath + "/" + ReportsConstants.REASSIGNDIR;
      reassignFile += "/" + reviewerID.toString() + "_" + proposalNumber ;
      reassignFile += ext;

    }

    private void writeFile() throws IOException
    {

       if (showDebug) {
         LogMessage.println("Write reassign file  " + reassignFile);
       }

       PrintWriter outPW;
       File theFile = new File(reassignFile);
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
       outPW = new PrintWriter(new FileWriter(reassignFile));

       String pline = "^\t";
       pline +=  getProposalNumber() + "\t" ;
       pline +=  getReviewerID().toString() + "\t" ;
       pline +=  cmt;
       outPW.println(pline);

       outPW.close();
       FileUtils.setPermissions(reassignFile,"660");
    }

    private void readFile() throws IOException
    {
      LogMessage.println("Reading reassign file  " + reassignFile);

      BufferedReader in = null;
      try {
        in = new BufferedReader(new FileReader(reassignFile));

        String nextLine;
        String propnum = "";
        String persid = "";

        nextLine = in.readLine();
        while (nextLine != null) {
          if (nextLine.startsWith("#")) {
          }
          else if (nextLine.startsWith("^")) {
            StringTokenizer st = new StringTokenizer( nextLine,"\t",false);
            
            st.nextToken();
            if (st.hasMoreTokens())
              propnum = st.nextToken();
            if (st.hasMoreTokens())
              persid = st.nextToken();
            if (st.hasMoreTokens())
              cmt = new String(st.nextToken());

          } else {
            cmt += "\n" + nextLine;
          }

          nextLine = in.readLine();
        }
        in.close();
      }
      catch (FileNotFoundException ioe) {
        LogMessage.println("File not found for " + reassignFile);
      }
      catch (IOException ioe) {
        LogMessage.println("Unable to read in " + reassignFile);
        LogMessage.printException(ioe);
        throw new IOException ("Unable to read " + reassignFile);
      }
    }

    private void SendMessage()
    {
       try 
       {
         Properties reportsProperties = Reports.getProperties(); 

         javax.mail.Session mailSession = javax.mail.Session.getDefaultInstance(Reports.getProperties());
         MimeMessage mimeMessage = new MimeMessage( mailSession );

         String cxoEmail = reportsProperties.getProperty("cxo.email.address");
         String fromEmail = reportsProperties.getProperty("from.email.address");
         String cdo_str = reportsProperties.getProperty("reassign.msg");

         String message = "\nReassignment Request for Panel " + theUser.getPanelName();
         message += "\nReviewer: " + theUser.getUserFirst() + " " + theUser.getUserName();
         message += "\nProposal: " + proposalNumber;
         message += "\nReportType: " + reportType;
         message += "\n\n" + cmt;
         message += "\n\n" + cdo_str;
         //message += "\n\nThis ticket was opened by the conflict feature of the PAS.\nDO NOT REPLY to this ticket - it will come right back to cxoreview. Instead, please post an Internal Note to record any action taken.\n"; 
         LogMessage.println ("Reassignment Request: " + proposalNumber + " " + theUser.getUserName() + "(" + reportType +") Panel " + theUser.getPanelName());
         LogMessage.println("Sent to " + cxoEmail);
         try {
           mimeMessage.setFrom(new InternetAddress(fromEmail));
           mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(cxoEmail));
           mimeMessage.setSubject( "Chandra PAS: Reassignment Request - " + proposalNumber );
           mimeMessage.setText( message, "UTF-8");
           Transport.send( mimeMessage );
         } catch(Exception mailEx) {
           LogMessage.println("ReassignRequest caught mail exception : " + mailEx.getMessage());
           LogMessage.printException(mailEx);
         }
       } catch(Exception exc) {
         LogMessage.println("ReassignRequest caught exception : " + exc.getMessage());
         LogMessage.printException(exc);
       }
    }

}
