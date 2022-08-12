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
import java.io.FileWriter;
import java.io.File;
import java.text.Format;
import java.text.NumberFormat;
import java.text.BreakIterator;
import ascds.LogMessage;
import org.apache.commons.lang3.*;


/**
  * This class contains all the information related to an observation.
  * needed for triggering/managing a TOO
  */

public class DDTFollowup {
    private Integer targid;
    private Integer ordr;
    private Double  exp_time;
    private Double  min_lead;
    private Double  max_lead;
    private Integer targnum;
    private String  status;
    private int     padSize;

    /**
      * Constructor
      */
    public DDTFollowup() {
	init();
    }

    private void init() {
        padSize=14;
        targid = new Integer(0);
        ordr = new Integer(0);
        targnum = new Integer(0);
        exp_time = new Double(0);
        min_lead = new Double(0);
        max_lead = new Double(0);
	status = new String("");
    }

    /**
      * copy  - copy the followup observation
      * @param inputObs  input DDTFollowup
      */
    public void copy(DDTFollowup inputObs) {
        targid = inputObs.getTargid();
        ordr = inputObs.getOrdr();
        targnum = inputObs.getTargetNumber();
        exp_time = inputObs.getExpTime();
        min_lead = inputObs.getMinLead();
        max_lead = inputObs.getMaxLead();
        status = inputObs.getStatus();
        

    }


    //  Set routines
    public void setTargid(Integer inputValue) {
	targid = inputValue;
    }
    public void setTargid(int inputValue) {
	targid = new Integer(inputValue);
    }
    public void setOrdr(Integer inputValue) {
	ordr = inputValue;
    }
    public void setOrdr(int inputValue) {
	ordr = new Integer(inputValue);
    }
    public void setTargetNumber(Integer inputValue) {
	targnum = inputValue;
    }
    public void setTargetNumber(int inputValue) {
	targnum = new Integer(inputValue);
    }
    public void setStatus(String inputValue) {
      if (inputValue != null) {
	status = inputValue.trim();
      } else {
	status = "";
      }
    }
    public void setExpTime(Double inputValue) {
	exp_time = inputValue;
    }
    public void setExpTime(double inputValue) {
	exp_time = new Double(inputValue);
    }
    public void setMinLead(Double inputValue) {
	min_lead = inputValue;
    }
    public void setMinLead(double inputValue) {
	min_lead = new Double(inputValue);
    }
    public void setMaxLead(Double inputValue) {
	max_lead = inputValue;
    }
    public void setMaxLead(double inputValue) {
	max_lead = new Double(inputValue);
    }


    //  Get routines
    public Integer getTargid() {
	return targid;
    }
    public Integer getOrdr() {
	return ordr;
    }
    public Integer getTargetNumber() {
	return targnum;
    }
    public String getStatus() {
	return status;
    }
    public Double getExpTime() {
	return exp_time;
    }
    public Double getMinLead() {
	return min_lead;
    }
    public Double getMaxLead() {
	return max_lead;
    }

  /**
   * write observation information in printer friendly format 
   * @param outputPW   output buffer
   * @return boolean   true if no issues
   */
  public boolean writeOutputHeader(PrintWriter outputPW)
  {
    boolean retval=true;
  
    try {
       outputPW.print(StringUtils.rightPad("Observation",padSize));
       outputPW.print(StringUtils.rightPad("Exposure Time",padSize));
       outputPW.print(StringUtils.rightPad("MinLead",padSize));
       outputPW.print(StringUtils.rightPad("MaxLead",padSize));
       outputPW.print(StringUtils.rightPad("Target Number",padSize));
       outputPW.println("");
       outputPW.print(StringUtils.rightPad("-----------",padSize));
       outputPW.print(StringUtils.rightPad("-------------",padSize));
       outputPW.print(StringUtils.rightPad("-------",padSize));
       outputPW.print(StringUtils.rightPad("-------",padSize));
       outputPW.print(StringUtils.rightPad("-------------",padSize));
       outputPW.println("");
    }
    catch(Exception exc) {
       LogMessage.println("TriggerTOO: Caught exception in writeOutput function");
       LogMessage.println(exc.getMessage());
       retval  = false;
    }
    return retval;
  }

  public boolean writeTrigger(PrintWriter outputPW,Double expTime,Integer tnum)
  {
    boolean retval=true;
  
    try {
       outputPW.print(StringUtils.rightPad("Trigger",padSize));
       outputPW.print(StringUtils.rightPad(expTime.toString(),padSize));
       outputPW.print(StringUtils.rightPad(" ",padSize));
       outputPW.print(StringUtils.rightPad(" ",padSize));
       outputPW.print(StringUtils.rightPad(tnum.toString(),padSize));
       outputPW.println("");
    }
    catch(Exception exc) {
       LogMessage.println("TriggerTOO: Caught exception in writeOutput function");
       LogMessage.println(exc.getMessage());
       retval  = false;
    }
    return retval;
  }

     
  public boolean writeOutput(PrintWriter outputPW)
  {
    boolean retval=true;
    String[] strArray;

    try {
       NumberFormat nf = NumberFormat.getInstance();
       nf.setMaximumFractionDigits(6);
       nf.setMinimumFractionDigits(6);


       outputPW.print(StringUtils.rightPad(getOrdr().toString(),padSize));
       outputPW.print(StringUtils.rightPad(getExpTime().toString(),padSize));
       outputPW.print(StringUtils.rightPad(getMinLead().toString(),padSize));
       outputPW.print(StringUtils.rightPad(getMaxLead().toString(),padSize));
       outputPW.print(StringUtils.rightPad(getTargetNumber().toString(),padSize));
       outputPW.println("");

    } 
    catch(Exception exc) {
       LogMessage.println("TriggerTOO: Caught exception in writeOutput function");
       LogMessage.println(exc.getMessage());
       retval  = false;
    }

    return retval;
  }


}
