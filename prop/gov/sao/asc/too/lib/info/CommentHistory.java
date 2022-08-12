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

/**
 * The CommentHistory class handle the tootrigger_comment information
 * from the database. 
 */

import java.io.PrintWriter;
import ascds.LogMessage;
import java.lang.Object;
import org.apache.commons.lang3.*;


public class CommentHistory {
    private Integer commentId;
    private Integer triggertooId;
    private String creationDate ;
    private String comment;
    private String userName;
    private String status;

    /** 
      * Constructor
      */
    public CommentHistory() {
	init();
    }

    /** 
      * Initialize the private variables
      */
    private void init() {
        commentId = new Integer(0);
        triggertooId = new Integer(0);
        creationDate = new String("");
        comment = new String("");
        userName = new String("");
        status = new String("");

    }

    /**
      * copy from an existing CommentHistory record
      * @param inputRecord  input comment record
      */
    public void copy(CommentHistory inputRecord) {
        triggertooId = inputRecord.getTriggerID();
        commentId    = inputRecord.getCommentID();
	creationDate = inputRecord.getCreationDate();
	comment      = inputRecord.getComment();
	userName     = inputRecord.getUserName();
	status        = inputRecord.getStatus();
    }


    //  Set routines
    public void setCommentID(Integer inputValue) {
	commentId = inputValue;
    }
    public void setCommentID(int inputValue) {
	commentId = new Integer(inputValue);
    }
    public void setTriggerID(Integer inputValue) {
	triggertooId = inputValue;
    }
    public void setTriggerID(int inputValue) {
	triggertooId = new Integer(inputValue);
    }
    public void setCreationDate(String inputValue) {
      if (inputValue != null) {
	creationDate = inputValue.trim();
      } else {
	creationDate = inputValue;
      }
    }
    public void setComment(String inputValue) {
      if (inputValue != null) {
	comment = inputValue.trim();
      }
      else {
	comment = "";
      }
    }
    public void setUserName(String inputValue) {
      if (inputValue != null) {
	userName = inputValue.trim();
      } else {
	userName = "";
      }
    }
    public void setStatus(String inputValue) {
      if (inputValue != null) {
	status = inputValue.trim();
      } else {
	status = "";
      }
    }

    //  Get routines
    public Integer getCommentID() {
	return commentId;
    }
    public Integer getTriggerID() {
	return triggertooId;
    }
    public String getCreationDate() {
	return creationDate;
    }
    public String getComment() {
	return comment;
    }
    public String getCommentHTML() {
        String cmtHTML = StringEscapeUtils.escapeHtml4(comment);
        cmtHTML = cmtHTML.replaceAll("\r\n","<br>");
        cmtHTML = cmtHTML.replaceAll("\r","<br>");
        cmtHTML = cmtHTML.replaceAll("\n","<br>");
	return cmtHTML;
    }
    public String getUserName() {
	return userName;
    }
    public String getStatus() {
	return status;
    }

    /**
      * write the printer friendly version of the Comment 
      * @param outputPW output print writer class
      * @param printDrafts true if draft comments should be printed
      * @return boolean true, if successful write, else false
      */
  private boolean writeOutput(PrintWriter outputPW,boolean printDrafts)
  {
    boolean retval=true;

    try {

      if (printDrafts || !status.equalsIgnoreCase(TriggerTooConstants.DRAFTSTATUS)) {
    
        outputPW.println(this.getCreationDate());
        outputPW.println(this.getComment());
      }
    } 
    catch(Exception exc) {
       LogMessage.println("CommentHistory: Caught exception in writeOutput function");
       LogMessage.println(exc.getMessage());
       retval  = false;
    }

    return retval;
  }


}
