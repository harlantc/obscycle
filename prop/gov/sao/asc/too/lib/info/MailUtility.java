package info;

/*
  Copyrights:

  Copyright (c) 2000-2017 Smithsonian Astrophysical Observatory

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

import java.util.Properties;
import java.util.Date;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.StringTokenizer;
import ascds.LogMessage;


/******************************************************************************/
/**
 *  MailUtility class to support email notification 
 *
*/

public class MailUtility 
{
  
  private Properties myProperties;
  private String fromEmail;
  private String statusEmail;
  private String alternateEmail;
  private String updateEmail;
  private String errorEmail;
  private String fastprocEmail;
  private String ddtobscatEmail;
  private String errFilename;
  private Integer mailMinHours;

  
  public MailUtility(String tooDataPath, Properties props)
  {
    myProperties = props;
    initAddresses(tooDataPath);
    try {
      mailMinHours = new Integer(props.getProperty("errormail.min.hours"));
    } catch (Exception exc) {
      mailMinHours = new Integer(2);
    }

    
  }


  private void initAddresses (String tooDataPath)
  {
    String theDataPath = tooDataPath;
    theDataPath += "/.htemail";
    errFilename=tooDataPath + "/.hterrorMail";
    //LogMessage.println("Using "  + errFilename);


    try {
      BufferedReader in = new BufferedReader(new FileReader(theDataPath));

      String nextLine;
      while ((nextLine = in.readLine()) != null) {
        int valueStart = nextLine.indexOf("=");
        String str=null;
        if (valueStart > -1) {
          str=nextLine.trim().substring(valueStart+1);
        }
        if ( nextLine.trim().startsWith("from") ) {
          fromEmail = str;
        }
        else if ( nextLine.trim().startsWith("status") ) {
          statusEmail = str;
        }
          else if ( nextLine.trim().startsWith("update") ) {
          updateEmail = str;
        }
        else if ( nextLine.trim().startsWith("fastproc") ) {
          fastprocEmail = str;
        }
        else if ( nextLine.trim().startsWith("alternate") ) {
          alternateEmail = str;
        }
        else if ( nextLine.trim().startsWith("ddtobscat") ) {
          ddtobscatEmail = str;
        }
        else if ( nextLine.trim().startsWith("error") ) {
          errorEmail = str;
        }
      }
      if (in != null) {
       in.close();
       }
    }
    catch (Exception exc) {
      exc.printStackTrace();
      LogMessage.println("Unable to initialize email addresses");
      LogMessage.println(exc.toString());
    }
  }
  
  public String getFromEmail()
  {
    return fromEmail;
  }
  public String getStatusEmail()
  {
    return statusEmail;
  }
  public String getUpdateEmail()
  {
    return updateEmail;
  }
  public String getAlternateEmail()
  {
    return alternateEmail;
  }
  public String getFastProcEmail()
  {
    return fastprocEmail;
  }
  public String getErrorEmail()
  {
    return errorEmail;
  }
  public String getDDTObscatEmail()
  {
    return ddtobscatEmail;
  }
  public void mailFile(String fromAddr, String toAddr, String ccAddr,
	String subject, String filename) throws Exception

  {
    StringBuffer theMessage = new StringBuffer("");

    try {
      // now read in the file and send the message to the RPS receiver
      FileReader fileReader = new FileReader(filename);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String inputLine;
      String str;
      while( (inputLine = bufferedReader.readLine()) != null) {
        str =inputLine.replaceAll("\r\n","\n");
        inputLine =str.replaceAll("\r","\n");

        theMessage.append(inputLine);
        theMessage.append("\n");
      }
      bufferedReader.close();
      fileReader.close();
    }
    catch (Exception exc) {
      LogMessage.println(exc.toString());
      throw new Exception("Unable to read input file for email message. ");
    }

    mailMessage(fromAddr,toAddr,ccAddr,subject,theMessage.toString());

  }

  public void mailFile(String fromAddr, String toAddr, String ccAddr,
	 String subject, PrintWriter pw) throws Exception
  {
    String theMessage = new String("");

    try {
      // now read in the file and send the message to the RPS receiver
      theMessage = pw.toString();
    }
    catch (Exception exc) {
      LogMessage.println(exc.toString());
      throw new Exception("Unable to read input file for email message. ");
    }

    mailMessage(fromAddr,toAddr,ccAddr,subject,theMessage);

  }

  public void mailErrorMessage(String theMessage)
      throws Exception
  {
      boolean sendmail=true;
      try {
        File efile = new File(errFilename);
        Date now = new Date();
        if (efile.exists()) {
           // only send error emails every 2 hours at most
           Date fileDate = new Date(efile.lastModified());
           long diff = now.getTime() - fileDate.getTime();
           if (diff < ((60*60*mailMinHours.intValue()) * 1000) ) {
               sendmail=false;
           } else {
              efile.setLastModified(now.getTime());
           }
        } else {
           new FileOutputStream(efile).close();
        }
        
      } catch (Exception exc) {
        LogMessage.printException(exc);
      }
      if (sendmail) {
        mailMessage(fromEmail,errorEmail,null,TriggerTooConstants.SUBJECT_FAILURE,
		theMessage);
      }
      else {
        LogMessage.println("Mail NOT sent: " + theMessage);
      }
  }
    
 



  public void mailMessage(String fromAddr, String toAddr, String ccAddr,
	String subject, String theMessage) throws Exception
  {
   String tmsg;

    try {
      javax.mail.Session mailSession = javax.mail.Session.getDefaultInstance(myProperties);
      MimeMessage mimeMessage = new MimeMessage( mailSession );

      if (toAddr == null  || toAddr.length() <= 0) 
      {
        LogMessage.println("MailUtility::mailMessage: 'To' address is null for "  + subject);
        throw new Exception("To: email address is null.");
      }

      if(fromAddr == null || fromAddr.length() == 0) {
        fromAddr = new String("cdo@head.cfa.harvard.edu");
      }
      InternetAddress fromAddress = new InternetAddress(fromAddr);
      mimeMessage.setFrom(fromAddress);

      mimeMessage.setRecipients(Message.RecipientType.TO,
		InternetAddress.parse(toAddr));

      if (ccAddr != null && ccAddr.length() > 0) {
         mimeMessage.setRecipients(Message.RecipientType.CC,
		InternetAddress.parse(ccAddr));
      }

      String subjectHdr = myProperties.getProperty("cxc.test");
      if (subjectHdr == null) subjectHdr = "";
      if (subject == null) subject="";
      subjectHdr += subject;

      mimeMessage.setSubject( subjectHdr);
      mimeMessage.setText( theMessage, "iso-8859-1"); 
      mimeMessage.setHeader("Content-Transfer-Encoding", "8bit");
      Transport.send( mimeMessage );
      LogMessage.println("Sent mail " + subject + "\n ") ;
      if (toAddr != null && toAddr.length() > 0) {
        LogMessage.println("To: " + toAddr  + "\n ") ;
      }
      if (ccAddr != null && ccAddr.length() > 0) {
        LogMessage.println("To: " + ccAddr  + "\n ") ;
      }

    } catch(Exception mailEx) {
      LogMessage.println("MailUtility::Caught exception : " + mailEx.toString());
      String msg = "Unable to send mail for TOO with subject : " + subject;
      throw new Exception (msg);
    }


  }


}
