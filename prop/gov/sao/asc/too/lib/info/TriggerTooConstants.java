package info;
/*
  Copyrights:

  Copyright (c) 2000 Smithsonian Astrophysical Observatory

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

/******************************************************************************/



/**
  * TriggerTooConstants class contains all the constants needed in the 
  * Trigger TOO and TOO Manager applications
  */

public class TriggerTooConstants {

  public static String CMT_ONLY = new String("***Comment Update Only***");
  public static String CYCLE = new String("Cycle");
  public static String APPTIME = new String("App Time");
  public static String EXPTIME = new String("Rem Time");
  public static String INSTRUMENT = new String("Instrument");
  public static String GRATING = new String("Grating");
  public static String PREID = new String("Followup");
  public static String FUP = new String("Followups (follow ObsID)");
  public static String PREMIN = new String("Min Lead");
  public static String PREMAX = new String("Max Lead");
  public static String OBSID = new String("Obs ID");
  public static String SEQNBR = new String("Seq Num");
  public static String PROPNUM = new String("Proposal");
  public static String PINAME = new String("P.I.");
  public static String PILAST = new String("P.I. Last Name");
  public static String PIEMAIL = new String("P.I. Email");
  public static String PIPHONE = new String("P.I. Phone");
  public static String OBSERVER    = new String("Observer");
  public static String OBSERVERLAST = new String("Observer Last Name");
  public static String OBSERVEREMAIL = new String("Observer Email");
  public static String OBSERVERPHONE = new String("Observer Phone");
  public static String TITLE = new String("Proposal Title");
  public static String TYPE = new String("Type");
  public static String STATUS = new String("Status");
  public static String TARGETNAME = new String("Target Name");
  public static String RA = new String("RA");
  public static String COORD = new String("Position (Equatorial J2000)");
  public static String DEC = new String("Dec");
  public static String CONTACTINFO = new String("24 Hr. Contact Information");
  public static String DATARIGHTS = new String("Proprietary Rights");
  public static String RESPONSEWINDOW = new String("Response Window");
  public static String RESPONSESTART = new String("CXC Start");
  public static String RESPONSESTOP  = new String("CXC Stop");
  public static String TRIGGERCRITERIA = new String("Trigger Criteria");
  public static String TRIGGERJUSTIFY = new String("Trigger Justification");
  public static String RESPONSECHANGES = new String("Changes in Response Window");
  public static String FASTPROCMSG = new String("Fast Processing Required");
  public static String FASTPROCSTATUS = new String("Fast Proc?");
  public static String FASTPROCCOMMENT = new String("Fast Proc Comment");
  public static String OBSCHANGES = new String("Observation Change Requests ");
  public static String COMMENT = new String("Comments");
  public static String NONE = new String("None");

  public static String CCEMAIL = new String("Additional Email");

  public static String RAFORMAT  = new String("HH MM SS.ss  or DDD.DD");
  public static String DECFORMAT = new String("sDD MM SS.ss  or sDDD.DD");

  public static String ERRORCLASS = new String("class=\"error\"");
  public static String LABELCLASS = new String("class=\"label\"");

  public static String DRAFTSTATUS = new String("Draft");

  public static String TOOSTATUS = new String("TOO Status");
  public static String NAMERESOLVER = new String("Resolve Name");
  public static String RESOLVERLIST = new String("Resolver List");
  public static String SAVEDRAFT = new String("Save Draft");
  public static String SENDCMT = new String("Email Comments Only");
  public static String SENDMSG = new String("Email + Status Update");
  public static String SUBMIT = new String("Submit");
  public static String PRINTER = new String("Printer Friendly");
  public static String SAVE = new String("Save");
  public static String SAVECMT = new String("Save Comment");
  public static String UPDATEOBSCAT = new String("Update Obscat");
  public static String APPLYOBSCAT = new String("Apply ObsCat Updates");
  public static String DDTUPDATE = new String("Migrate To ObsCat");
  public static String CANCELNOSEND = new String("Cancel");
  public static String CANCEL = new String("Cancel");
  public static String PENDING = new String("First Notification");
  public static String ACKNOWLEDGED = new String("Acknowledged");
  public static String APPROVED = new String("Approved");
  public static String NOTAPPROVED = new String("Not Approved");
  public static String WITHDRAWN = new String("Request Withdrawn");

  public static String FAST = new String("FAST");
  public static String SLOW = new String("SLOW");
  public static String MEDIUM = new String("MEDIUM");
  public static String NONTRANS = new String("NON-TRANSIENT");

  public static String INVALID_USER = new String("<b>ERROR:<b> Invalid <b>Database User</b> name and <b>Password</b>.<br>");

  public static String OBSDETAIL = new String ("obsDetail");

  public static Double  EMPTY_VALUE = new Double(-9999.);
  public static Integer EMPTY_INT   = new Integer(-9999);



  public static String SUBJECT_STATUS = new String("Chandra Status Update for TOO ");
  public static String SUBJECT_UPDATE = new String("TOO ObsCat Update");
  public static String SUBJECT_ALTERNATE = new String("TOO ObsCat Alternate Groups");
  public static String SUBJECT_FAILURE = new String("Trigger TOO FAILURE");
  public static String DDTSUBJECT_STATUS = new String("Chandra Status Update for DDT ");

  public static String SITE_KEY="6LeAXVIUAAAAAG_121eDaHXXc_uAOkcKnF-LGl4A";
}
