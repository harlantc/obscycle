package edu.harvard.asc.cps.xo;
/*
  Copyrights:

  Copyright (c) 2017 Smithsonian Astrophysical Observatory

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

import org.apache.log4j.Logger;
import java.util.Properties;
import java.util.Date;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import java.util.StringTokenizer;



/******************************************************************************/
/**
 *  MailUtility class to support email notification 
 *
*/

public class MailUtility 
{
  
  private Properties myProperties;
  private String fromEmail;
  private String errorEmail;
  private String testEmail;
  private String testbccEmail;
  private boolean domail;

  private String errFilename;
  private Integer mailMinHours;
  private static Logger logger = Logger.getLogger(MailUtility.class);


  
  public MailUtility(Properties props)
  {
    domail= false;
    myProperties = props;
    try {
      mailMinHours = Integer.valueOf(myProperties.getProperty("errormail.min.hours"));
    } catch (Exception exc) {
      mailMinHours = Integer.valueOf(2);
    }
    String tstr = myProperties.getProperty("send.mail");
    if (tstr != null && tstr.indexOf("true") >= 0) 
      domail = true;
    fromEmail  = myProperties.getProperty("from.email");
    errorEmail = myProperties.getProperty("error.email");
    errFilename= myProperties.getProperty("mail.error.file");
    testEmail  = myProperties.getProperty("test.email");
    testbccEmail  = myProperties.getProperty("testbcc.email");

  }

  
  public String getFromEmail()
  {
    return fromEmail;
  }
  public String getErrorEmail()
  {
    return errorEmail;
  }

  public void mailFile(String fromAddr, String toAddr, String ccAddr,String bccAddr,
	String subject, String filename) throws Exception

  {
    StringBuffer theMessage = new StringBuffer("");

    FileReader fileReader = null;
    BufferedReader bufferedReader = null;
    try {
      // now read in the file and send the message to the RPS receiver
      fileReader = new FileReader(filename);
      bufferedReader = new BufferedReader(fileReader);
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
      logger.error("Mail",exc);
      try {
      if (bufferedReader != null) bufferedReader.close();
      if (fileReader != null) fileReader.close();
      } catch (Exception e) {
        logger.error(e);
      }
      throw new Exception("Unable to read input file for email message. ");
    }

    mailMessage(fromAddr,toAddr,ccAddr,bccAddr,subject,theMessage.toString());

  }

  public void mailFile(String fromAddr, String toAddr, String ccAddr,String bccAddr,
	 String subject, PrintWriter pw) throws Exception
  {
    String theMessage = "";

    try {
      // now read in the file and send the message to the RPS receiver
      theMessage = pw.toString();
    }
    catch (Exception exc) {
      logger.error("Mail",exc);
      throw new Exception("Unable to read input file for email message. ");
    }

    mailMessage(fromAddr,toAddr,ccAddr,bccAddr,subject,theMessage);

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
           if (diff < ((60L*60L*mailMinHours.intValue()) * 1000L) ) {
               sendmail=false;
           } else {
              if (!efile.setLastModified(now.getTime()))
                logger.error("Failed to set last modified for errFilename");
           }
        } else {
           new FileOutputStream(efile).close();
        }
        
      } catch (Exception exc) {
        logger.error("Mail",exc);
      }
      if (sendmail) {
        mailMessage(fromEmail,errorEmail,null,null,"CPS Mail Failure",
		theMessage);
      }
      else {
        logger.error("Mail NOT sent: " + theMessage);
      }
  }
    
 



  public void mailMessage(String fromAddr, String toAddr, String ccAddr,String bccAddr,
	String subject, String theMessage) throws Exception
  {
   String tmsg;

    try {
      javax.mail.Session mailSession = javax.mail.Session.getDefaultInstance(myProperties);
      MimeMessage mimeMessage = new MimeMessage( mailSession );

      if (toAddr == null  || toAddr.length() <= 0) 
      {
        logger.error("MailUtility::mailMessage: 'To' address is null for "  + subject);
        throw new Exception("To: email address is null.");
      }
      if (testEmail!=null && testEmail.length()>3) {
         logger.info("OVERRIDE MAIL addresses: to:" + toAddr  );
         toAddr = testEmail;
      }


      if(fromAddr == null || fromAddr.length() == 0) {
        fromAddr = "cdo@head.cfa.harvard.edu";
      }
      InternetAddress fromAddress = new InternetAddress(fromAddr);
      mimeMessage.setFrom(fromAddress);



      mimeMessage.setRecipients(Message.RecipientType.TO,
		InternetAddress.parse(toAddr));

      if (ccAddr != null && ccAddr.length() > 0) {
        if (testEmail!=null && testEmail.length()>3) {
          logger.info("OVERRIDE MAIL addresses: cc:" + ccAddr  );
          ccAddr = testEmail;
        }
        mimeMessage.setRecipients(Message.RecipientType.CC,
		InternetAddress.parse(ccAddr));
      }
      if (bccAddr != null && bccAddr.length() > 0) {
        if (testbccEmail!=null && testbccEmail.length()>3) {
          logger.info("OVERRIDE MAIL addresses: bcc:" + bccAddr  );
          bccAddr = testbccEmail;
        }
        mimeMessage.setRecipients(Message.RecipientType.BCC,
		InternetAddress.parse(bccAddr));
      }

      mimeMessage.setSubject( subject);
      mimeMessage.setText( theMessage, "iso-8859-1"); 
      //DataHandler dataHandler = new DataHandler(
		//new ByteArrayDataSource(body.getBytes(), "text/plain"));
      mimeMessage.setHeader("Content-Transfer-Encoding", "8bit");
      String logstr="NOT SENT:";
       
      if (domail) {
        logstr="SENT MAIL: ";
        Transport.send( mimeMessage );
      }
      logstr += subject + "  ";
      logstr += "To:" + toAddr  + "  ";
      if (ccAddr != null && ccAddr.length() > 0) {
        logstr += "cc:" + ccAddr  + "  ";
      }
      if (bccAddr != null && bccAddr.length() > 0) {
        logstr += "bcc:" + bccAddr  + " ";
      }
      logger.info(logstr);


    } catch(Exception mailEx) {
      logger.error("MailUtility::Caught exception : " + mailEx.toString());
      String msg = "Unable to send mail for TOO with subject : " + subject;
      throw new Exception (msg);
    }


  }


}
