package info;
/*
  Copyrights:
 
  Copyright (c) 2016 Smithsonian Astrophysical Observatory
 
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
import java.lang.Double;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.NumberFormat;
import ascds.LogMessage;
import org.apache.commons.lang3.*;


/**
  * This class contains the information related to an observing cycle
  */

public class AOCycle {
    private String  ao_cycle;
    private Double  total_approved_time;
    private Double  total_requested_time;

    /**
      * Constructor
      */
    public AOCycle() {
	init();
    }

    private void init() {
        ao_cycle = new String("");
        total_approved_time = new Double(0);
        total_requested_time = new Double(0);

    }

    /**
      * copy  - copy the AOCycle record
      * @param inputObs  input AOCycle
      */
    public void copy(AOCycle inputObs) {
        ao_cycle = inputObs.getAOCycle();
        total_approved_time = inputObs.getTotalApprovedTime();
        total_requested_time = inputObs.getTotalRequestedTime();
    }


    //  Set routines
    public void setAOCycle(String inputValue) {
	ao_cycle = inputValue;
    }
    public void setTotalApprovedTime(double inputValue) {
       total_approved_time = new Double(inputValue);
    }
    public void setTotalRequestedTime(double inputValue) {
       total_requested_time = new Double(inputValue);
    }

    //  Get routines
    public String getAOCycle() {
       return ao_cycle;
    }
    public Double getTotalApprovedTime() {
       return total_approved_time;
    }
    public String getTotalApprovedTimeStr() {
       String retval = "";
       try {
         NumberFormat nf = NumberFormat.getInstance();
         nf.setMaximumFractionDigits(2);
         nf.setMinimumFractionDigits(2);
         nf.setGroupingUsed(false);
         retval = nf.format(total_approved_time);
       } catch (Exception exc) {
         LogMessage.println("AOCycle: Caught exception ");
         LogMessage.println(exc.getMessage());
       }
       return retval;
   }
    public Double getTotalRequestedTime() {
       return total_requested_time;
    }
    public String getTotalRequestedTimeStr() {
       String retval = "";
       try {
         NumberFormat nf = NumberFormat.getInstance();
         nf.setMaximumFractionDigits(2);
         nf.setMinimumFractionDigits(2);
         nf.setGroupingUsed(false);
         retval = nf.format(total_requested_time);
       } catch (Exception exc) {
         LogMessage.println("AOCycle: Caught exception ");
         LogMessage.println(exc.getMessage());
       }
       return retval;
   }


}
