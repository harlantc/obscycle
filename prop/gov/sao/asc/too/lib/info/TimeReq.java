package info;
/*
  Copyrights:
 
  Copyright (c) 2014 Smithsonian Astrophysical Observatory
 
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


import java.lang.String;
import java.lang.Integer;
import java.lang.Double;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.lang3.*;
import ascds.LogMessage;


/**
  * This class contains all the information related to an observation.
  * needed for triggering/managing a TOO
  */

public class TimeReq {
    private Integer targid;
    private Integer ordr;
    private String  time_constraint;
    private String  tstart ;
    private String  tstop ;

    /**
      * Constructor
      */
    public TimeReq() {
	init();
    }

    private void init() {
        targid = new Integer(0);
        ordr = new Integer(0);
        time_constraint = new String("");
        tstart = new String("");
        tstop = new String("");

    }

    /**
      * copy  - copy the Time Constraint record
      * @param inputObs  input TimeReq
      */
    public void copy(TimeReq inputObs) {
        targid = inputObs.getTargID();
        ordr = inputObs.getOrdr();
        time_constraint = inputObs.getTimeConstraint();
        tstart = inputObs.getTstart();
        tstop = inputObs.getTstop();
    }


    /**
      * Set routines
      * @param inputValue target id
      */
    public void setTargID(Integer inputValue) {
	targid = inputValue;
    }
    public void setTargID(int inputValue) {
       targid = new Integer(inputValue);
    }
    public void setOrdr(Integer inputValue) {
	ordr = inputValue;
    }
    public void setOrdr(int inputValue) {
       ordr = new Integer(inputValue);
    }
    public void setTimeConstraint(String inputValue) {
      if (inputValue != null) {
	time_constraint = inputValue.trim();
      } else {
	time_constraint = inputValue;
      }
    }
    public void setTstart(String inputValue) {
      if (inputValue != null) {
	tstart = inputValue.trim();
      } else {
	tstart = inputValue;
      }
    }
    public void setTstop(String inputValue) {
      if (inputValue != null) {
	tstop = inputValue.trim();
      } else {
	tstop = inputValue;
      }
    }

    // get routines
    public Integer getTargID() {
	return targid;
    }
    public Integer getOrdr() {
	return ordr;
    }
    public String getTimeConstraint() {
       return time_constraint;
    }
    public String getTstart() {
       return tstart;
    }
    public String getTstop() {
	return tstop;
    }



  /**
   * write roll information in printer friendly format 
   * @param outputPW   output buffer
   * @return boolean   true if no issues
   */
  public boolean writeOutput(PrintWriter outputPW)
  {
    boolean retval=true;
    String[] strArray;

    try {

       outputPW.print(StringUtils.leftPad(ordr.toString(),6));
       outputPW.print("  ");
       outputPW.print(StringUtils.leftPad(time_constraint,6));
       outputPW.print("  ");
       outputPW.print(StringUtils.leftPad(tstart,22));
       outputPW.print("  ");
       outputPW.print(StringUtils.leftPad(tstop,22));
       outputPW.print("  ");

    } 
    catch(Exception exc) {
       LogMessage.println("TimeReq: Caught exception in writeOutput function");
       LogMessage.println(exc.getMessage());
       retval  = false;
    }


    return retval;
  }

}
