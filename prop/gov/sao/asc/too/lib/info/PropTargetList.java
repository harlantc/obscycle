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

/******************************************************************************/

import java.lang.String;
import java.lang.Integer;
import java.util.Vector;
import java.io.PrintWriter;
import java.text.NumberFormat;

/**
   The PropTarget List class contains a vector of observations
 */
public class PropTargetList extends Vector<PropTarget>
{



    public PropTargetList() {
	init();
    }

    private void init() {
    }

/**
  * getByTargid  - returns a single PropTarget record as specified by 
  *               the targid
  * @param targid - targid 
  * @return PropTarget - matching target record or null
  */
    public PropTarget getByTargid( Integer targid)
    {
      PropTarget proptarget;
      PropTarget tgtMatch = null;
      for (int ii=0; ii< this.size() ; ii++) {
        proptarget = (PropTarget)this.get(ii);
        if (proptarget != null && 
            proptarget.getTargID().intValue() == targid.intValue()) {
           tgtMatch = proptarget;
           break;
        }
      }

      return tgtMatch;
    }


/**
  * writeOutput  - write output format for each target in the list
  * @param outputPW    PrintWriter  
  * @param isApproved  true if should write approved target times
  * @return boolean    true if no error occurred while writing, else false
  */
    public boolean  writeOutput(PrintWriter outputPW,boolean isApproved) 
    {
      boolean retval =true;
      boolean rval;
  
      PropTarget proptarget;
      outputPW.println("---------------------------------------------------------------");
      for (int ii=0; ii< this.size() ; ii++) {
        proptarget = (PropTarget)this.get(ii);
        outputPW.println("---------------------------------------------------------------");
        rval = proptarget.writeOutput(outputPW,isApproved);
        if (!rval) retval = rval;
      }
      return retval;
    }

  /**
  * hasFastProc  - return true if any target is approved and required fast proc
  * @return boolean true if target approved and requires fast processing
  */
    public boolean  hasFastProc() 
    {
      boolean retval=false;
      PropTarget proptarget;
      for (int ii=0; ii< this.size() ; ii++) {
        proptarget = (PropTarget)this.get(ii);
        if (proptarget.getStatus().equals("accepted")  &&
            proptarget.getFastProcStatus().equals("approved")) {
          retval=true;
        }
      }
      return retval;
    }

  /**
  * getFastProc  - write output format for each target in the list
  * @return String -  fast proc for each target
  */
    public String  getFastProc() 
    {
      String retval = new String("");
      String retval2 = new String("");

      PropTarget proptarget;
      for (int ii=0; ii< this.size() ; ii++) {
        proptarget = (PropTarget)this.get(ii);
        if (proptarget.getStatus().equals("accepted")  &&
            proptarget.getFastProcStatus().length() > 2) {
          if (retval.equals("")) {
            retval += "\n  [x] " ;
            retval += TriggerTooConstants.FASTPROCMSG + "\n";
          }
          retval2 += "   " + proptarget.getTargetName();
          if (proptarget.getFastProcComment() != null && 
              !proptarget.getFastProcComment().equals("")) {
            retval2 += ":  " +  proptarget.getFastProcComment() ;
          }
          retval2 += "\n";
        } 
      }
      if (retval.equals("")) {
        retval += "\n  [ ] ";
        retval += TriggerTooConstants.FASTPROCMSG + "\n";
      } 
      else {
        retval += "\nFast processing required for:\n" + retval2;
      }
      return retval;
    }
 
  /**
  * getAllRAs  - write output format for each target in the list
  * @return String -  ra output for all targets in single line
  */
    public String  getAllRAs() 
    {
      String retval = new String("");

      PropTarget proptarget;
      for (int ii=0; ii< this.size() ; ii++) {
        retval += " ";
        proptarget = (PropTarget)this.get(ii);
        retval += proptarget.getRAString() + "  ";
      }
      return retval;
    }

  /**
  * getAllDecs  - write output format for each target in the list
  * @return String -  dec output for all targets in single line
  */
    public String  getAllDecs() 
    {
      String retval = new String("");

      PropTarget proptarget;
      for (int ii=0; ii< this.size() ; ii++) {
        proptarget = (PropTarget)this.get(ii);
        retval += proptarget.getDecString() + "  ";
      }
      return retval;
    }

  /**
  * getCXCStartStop  - write output format for 1st CXC start/stop the list
  * @return String -   formatted output for "cxcStart-cxcStop days"
  */
    public String  getCXCStartStop() 
    {
      String retval = new String("");
      NumberFormat nfi = NumberFormat.getInstance();

      nfi.setMinimumIntegerDigits(1);
      nfi.setGroupingUsed(false);

      for (int ii=0; ii< this.size();ii++) {
        PropTarget proptarget = (PropTarget)this.get(ii);
        if (proptarget != null ) {
          if (proptarget.getTriggerTarget() != null &&
              !proptarget.getTriggerTarget().equals("N")) {
            if (proptarget.getResponseStart().doubleValue() >= 0) {
              retval += nfi.format(proptarget.getResponseStart()) + "-";
              retval += nfi.format(proptarget.getResponseStop()) + " days";
            }
            break;
          }
        }
      }

      return retval;
    }
}
