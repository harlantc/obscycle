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
import java.text.Format;
import java.text.NumberFormat;
import ascds.LogMessage;
import org.apache.commons.lang3.*;


/**
  * This class contains all the information related to an observation.
  * needed for triggering/managing a TOO
  */

public class RollReq {
    private Integer targid;
    private Integer ordr;
    private String  roll_constraint;
    private String  roll_180 ;
    private Double  roll;
    private Double  roll_tolerance;

    /**
      * Constructor
      */
    public RollReq() {
	init();
    }

    private void init() {
        targid = new Integer(0);
        ordr = new Integer(0);
        roll_constraint = new String("");
        roll_180 = new String("");
        roll = TriggerTooConstants.EMPTY_VALUE;
        roll_tolerance = TriggerTooConstants.EMPTY_VALUE;

    }

    /**
      * copy  - copy the Roll record
      * @param inputObs  input RollReq
      */
    public void copy(RollReq inputObs) {
        targid = inputObs.getTargID();
        ordr = inputObs.getOrdr();
        roll_constraint = inputObs.getRollConstraint();
        roll_180 = inputObs.getRoll180();
        roll = inputObs.getRoll();
        roll_tolerance = inputObs.getRollTolerance();
    }


    // set routines
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
    public void setRollConstraint(String inputValue) {
      if (inputValue != null) {
	roll_constraint = inputValue.trim();
      } else {
	roll_constraint = inputValue;
      }
    }
    public void setRoll180(String inputValue) {
      if (inputValue != null) {
	roll_180 = inputValue.trim();
      } else {
	roll_180 = inputValue;
      }
    }
    public void setRoll(Double inputValue) {
	roll = inputValue;
    }
    public void setRoll(double inputValue) {
	roll = new Double(inputValue);
    }
    public void setRollTolerance(Double inputValue) {
	roll_tolerance = inputValue;
    }
    public void setRollTolerance(double inputValue) {
	roll_tolerance = new Double(inputValue);
    }


    // get routines
    public Integer getTargID() {
	return targid;
    }
    public Integer getOrdr() {
	return ordr;
    }
    public String getRollConstraint() {
       return roll_constraint;
    }
    public String getRoll180() {
       return roll_180;
    }
    public Double getRoll() {
	return roll;
    }
    public Double getRollTolerance() {
	return roll_tolerance;
    }



  /**
   * write roll information in printer friendly format 
   * @param outputPW output buffer
   * @return boolean  true on success
   */
  public boolean writeOutput(PrintWriter outputPW)
  {
    boolean retval=true;
    String[] strArray;

    try {
       NumberFormat nf = NumberFormat.getInstance();
       nf.setMaximumFractionDigits(6);
       nf.setMinimumFractionDigits(6);

       outputPW.print(StringUtils.leftPad(ordr.toString(),6));
       outputPW.print("  ");
       outputPW.print(StringUtils.leftPad(roll_constraint,6));
       outputPW.print("  ");
       outputPW.print(StringUtils.leftPad(roll_180,6));
       outputPW.print("  ");
       outputPW.print(nf.format(roll.doubleValue()));
       outputPW.print("  ");
       outputPW.print(nf.format(roll_tolerance.doubleValue()));
       outputPW.println("");

    } 
    catch(Exception exc) {
       LogMessage.println("RollReq: Caught exception in writeOutput function");
       LogMessage.println(exc.getMessage());
       retval  = false;
    }


    return retval;
  }

}
