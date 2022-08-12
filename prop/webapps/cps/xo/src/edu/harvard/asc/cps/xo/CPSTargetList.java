package edu.harvard.asc.cps.xo;

/*
  Copyrights:

  Copyright (c) 2017-2020 Smithsonian Astrophysical Observatory

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import org.apache.log4j.Logger;

import edu.harvard.cda.proposal.xo.*;

/******************************************************************************/
/**
 * The CPSTargetList class provides access to the targets for a proposal
 * and methods to provide info from all targets
 */

public class CPSTargetList extends ArrayList<CPSTarget>
{
  private static Logger logger = Logger.getLogger(CPSTargetList.class);
  private static final long serialVersionUID = 1;


  /**
   * Constructor
   *
   */
  public CPSTargetList() {
  }


  /**
   * Calculate the total time requested for a specified group id. 
   * Iterate through all targets requesting that group Id and sum the 
   * requested exposure time.
   *
   * @param   groupId The group identification
   * @return  Double  total requested exposure time for given group Id
   *
   */
  public Double getGroupTotal(String groupId)
  {
    Double groupTotal = Double.valueOf(0.);
    if (groupId != null ) {
      for (int ii=0; ii< this.size();ii++) {
        if ( this.get(ii).tgt.group_id != null &&
             groupId.equals(this.get(ii).tgt.group_id)) {
          groupTotal +=this.get(ii).tgt.prop_exposure_time;
        }
      }
    }
    return groupTotal;
  }

  /**
   * Calculate the total slew tax for the proposal
   *
   * @param   withProb boolean value indicating slew tax should include TOO probability
   * @param   excludeAltSet HashSet of targids for TOO alternate targets to exclude from calculations
   * @return  Double total slew tax for proposal excluding alternates
   *
   */
  public Double getSlewTotal(boolean withProb,HashSet<Integer>excludeAltSet)
  {
    Double slewTotal = Double.valueOf(0.);
    double prob = 1.0;

    // but then subtract for alternates 
    // and then probability
    for (int ii=0; ii< this.size();ii++) {
      CPSTarget tgt = this.get(ii);
      if (!excludeAltSet.contains(tgt.tgt.targid)) {
        prob=1.0;
        if (withProb && tgt.tgt.probability != null)
          prob = tgt.tgt.probability;
        if (withProb && tgt.tgt.monitor_flag == YesNoPreferred.PREFERRED)
          prob = .8;
        slewTotal += (tgt.slewTax * prob);
      }
      else {
        logger.debug("Exclude " + tgt.tgt.targid + " -- " + tgt.tgt.targ_num);
      }
    }
    return slewTotal;
  }
  /**
   * Calculate the total time for the proposal
   *
   * @param   withProb boolean value indicating slew tax should include TOO probability
   * @param   excludeAltSet HashSet of targids for TOO alternate targets to exclude from calculations
   * @return  Double total time for proposal excluding alternates
   *
   */
  public Double getTotalTime(boolean withProb,HashSet<Integer>excludeAltSet)
  {
    Double totalTime = Double.valueOf(0.);
    double prob = 1.0;

    // but then subtract for alternates 
    // and then probability
    for (int ii=0; ii< this.size();ii++) {
      CPSTarget tgt = this.get(ii);
      if (!excludeAltSet.contains(tgt.tgt.targid)) {
        prob=1.0;
        if (withProb && tgt.tgt.probability != null)
          prob = tgt.tgt.probability;
        Double ttime = tgt.tgt.prop_exposure_time;  
        if (ttime == null) ttime = Double.valueOf(0);
        totalTime += (ttime * prob);
      }
    }
    return totalTime;
  }
  /**
   * Set the TOO scores for each target 
   *
   */
  public void setTOOScores()
  {
    for (int ii=0; ii< this.size();ii++) {
       CPSTarget tgt = this.get(ii);
       tgt.setTOOScores();
    }
  }


  /**
   * Get  total constraints for the proposal
   * Example: Total constraints for proposal: Difficult*3.00 Easy*4.00
   * 

  /**
   * Set the constraint grades for each target 
   *
   */
  public void setTargetConstraintGrades()
  {
    Double totalGroupTime=Double.valueOf(0.0);
    for (int ii=0; ii< this.size();ii++) {
       CPSTarget tgt = this.get(ii);
       if (tgt.tgt.group_id != null)
         totalGroupTime = getGroupTotal(tgt.tgt.group_id);
       tgt.calcConstraintGrades(totalGroupTime);
    }
  }


  /**
   * Get  total constraints for the proposal
   * Example: Total constraints for proposal: Difficult*3.00 Easy*4.00
   * 
   * @param   withProb boolean value indicating slew tax should include TOO probability
   * @param   excludeAltSet HashSet of targids for TOO alternate targets to exclude from calculations
   * @return  String   constraint string
   *
   */
  public String getConstraintTotal(boolean withProb,HashSet<Integer>excludeAltSet)
  {
    Double npt = Double.valueOf(0.);
    double prob = 1.0;
    String ptype = "";
    String retstr="";
    String cstr="";
    String fmt="%.2f";
    String altstr="";
    HashMap<ConstraintGrade,Double> chash = new HashMap<ConstraintGrade,Double>();

    if (excludeAltSet.size() < 0) altstr="  and excluding alternates";
    // but then subtract for alternates so exec and find alts to exclude
    // and then probability
    for (int ii=0; ii< this.size();ii++) {
      CPSTarget tgt = this.get(ii);
      if (!excludeAltSet.contains(tgt.tgt.targid)) {
        prob=1.0;
        if (withProb && tgt.tgt.probability != null)
          prob = tgt.tgt.probability;
        if (withProb && tgt.tgt.monitor_flag == YesNoPreferred.PREFERRED)
          prob = .8;
          
 
        ConstraintGrade wg = tgt.getWorstGrade();
        if (chash.containsKey(wg))
           npt =  chash.get(wg);
        else
           npt = Double.valueOf(0.0);
        logger.debug(tgt.tgt.targid + "Adding " + wg + " npt=" + npt + " + " + (tgt.constrnumPointings*prob));
        npt += (tgt.constrnumPointings * prob);
        chash.put(wg,npt);
      }
      ptype = tgt.tgt.type;
    }
    if (chash.containsKey(ConstraintGrade.Difficult))
      cstr += ConstraintGrade.fromValue(ConstraintGrade.Difficult.value) + "*" + String.format(fmt,chash.get(ConstraintGrade.Difficult)) + " ";
    if (chash.containsKey(ConstraintGrade.Average))
      cstr += ConstraintGrade.fromValue(ConstraintGrade.Average.value) + "*" + String.format(fmt,chash.get(ConstraintGrade.Average)) + " ";
    if (chash.containsKey(ConstraintGrade.Easy))
      cstr += ConstraintGrade.fromValue(ConstraintGrade.Easy.value) + "*" + String.format(fmt,chash.get(ConstraintGrade.Easy)) + " ";
    if (cstr.length() > 2) {
      retstr += getAlternates(excludeAltSet);
      retstr += "Estimated Total constraints for proposal: ";
      retstr += cstr;
      if (withProb && ptype.indexOf("TOO") >= 0)
        retstr += " (with probability" + altstr + ")";
   
    }
    return retstr;
  }

  public String getAlternates(HashSet<Integer>excludeAltSet)
  {
    StringBuffer altstr = new StringBuffer("");
    HashMap<String,String> altgrps = new HashMap<String,String>();
    if (excludeAltSet.size()>0) {
      for (int ii=0; ii< this.size();ii++) {
        CPSTarget tgt = this.get(ii);
        if (!excludeAltSet.contains(tgt.tgt.targid) && 
             tgt.tgt.atg_group_name != null && !tgt.tgt.atg_group_name.equals("") ) {
          String altname =altgrps.get(tgt.tgt.atg_group_name);
          if (altname == null) altname = "";
          else altname += ",";
          altname += tgt.tgt.targ_num;
          altgrps.put(tgt.tgt.atg_group_name,altname);
        }
      }
    }
    for (String key: altgrps.keySet()) {
      String tnums = altgrps.get(key);
      altstr.append("Calculations for Alternate " + key + " using target number" );
      if (tnums.indexOf(',') > 0) altstr.append("s") ;
      altstr.append(": " + tnums + "\n");
    }
    if (altstr.length() > 1) altstr.append("\n");

    return altstr.toString();
  }

  public StringBuffer WriteTargets()
  {
    StringBuffer retstr = new StringBuffer("");
    for (int ii=0; ii< this.size();ii++) {
      CPSTarget tgt = this.get(ii);
      retstr.append(tgt.WriteTarget());
    }
    return retstr;
    
  }
  public StringBuffer WriteDDTTargets()
  {
    StringBuffer retstr = new StringBuffer("");
    for (int ii=0; ii< this.size();ii++) {
      CPSTarget tgt = this.get(ii);
      retstr.append(tgt.WriteDDT());
    }
    return retstr;
    
  }
      
}

