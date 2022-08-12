package edu.harvard.asc.cps.xo;
/************************************************************************/
/*     Copyrights:                                                      */
/*                                                                      */
/*     Copyright (c) 2017-2019 Smithsonian Astrophysical Observatory    */
/*                                                                      */
/*    Permission to use, copy, modify, distribute,  and  sell  this     */
/*    software  and  its  documentation  for  any purpose is hereby     */
/*    granted  without  fee,  provided  that  the  above  copyright     */
/*    notice  appear  in  all  copies  and that both that copyright     */
/*    notice and this permission notice appear in supporting  docu-     */
/*    mentation,  and  that  the  name  of  the  Smithsonian Astro-     */
/*    physical Observatory not be used in advertising or  publicity     */
/*    pertaining  to distribution of the software without specific,     */
/*    written  prior  permission.   The  Smithsonian  Astrophysical     */
/*    Observatory  makes  no  representations about the suitability     */
/*    of this software for any purpose.  It  is  provided  "as  is"     */
/*    without express or implied warranty.                              */
/*    THE  SMITHSONIAN  ASTROPHYSICAL  OBSERVATORY  DISCLAIMS   ALL     */
/*    WARRANTIES  WITH  REGARD  TO  THIS  SOFTWARE,  INCLUDING  ALL     */
/*    IMPLIED  WARRANTIES  OF  MERCHANTABILITY  AND FITNESS, IN  NO     */
/*    EVENT  SHALL  THE  SMITHSONIAN  ASTROPHYSICAL  OBSERVATORY BE     */
/*    LIABLE FOR ANY SPECIAL,  INDIRECT  OR  CONSEQUENTIAL  DAMAGES     */
/*    OR  ANY  DAMAGES  WHATSOEVER RESULTING FROM LOSS OF USE, DATA     */
/*    OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,  NEGLIGENCE  OR     */
/*    OTHER  TORTIOUS  ACTION, ARISING OUT OF OR IN CONNECTION WITH     */
/*    THE USE OR PERFORMANCE OF THIS SOFTWARE.                          */
/*                                                                      */
/************************************************************************/

import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.*;
import java.text.SimpleDateFormat;
import org.apache.commons.lang3.StringEscapeUtils;
import edu.harvard.cda.proposal.xo.ProposalSummary;


public class CPSProposalSummary
{
   private String editPrivs;
   public boolean isDDTProp;
   private String submissionDateStr;
   public String currentAO;
   public ProposalSummary ps;
   private static Logger logger = Logger.getLogger(CPSProposalSummary.class);


   public CPSProposalSummary(ProposalSummary ps)
   {
        this.ps = ps;
        this.currentAO = ps.currentAO;
        submissionDateStr = CPS.convertMS(ps.submissionDateMSSE);

   }

   public boolean isEdit() {
     if (editPrivs.indexOf('M') >= 0) 
       return true;
     else 
       return false;
   }

   public boolean setEditPrivs(boolean isCfP,boolean isDDT,String ddtAO,boolean isGTO,int uid) {
     boolean retval=true;
     editPrivs="V";
     isDDTProp=false;
     try {
       Integer xx= Integer.valueOf(ps.proposalNumber.substring(4));
       if (xx.intValue() > 8000) {
         isDDTProp=true;
       }
     }
     catch (Exception exc) {
       retval=false;
       logger.error("EditPrivs",exc);
     }
     try {
       // can clone if they are PI or Submitter
       if (ps.submitterId != null && uid == ps.submitterId.intValue()) 
         editPrivs += ",C";
       else if (ps.piId != null && uid==ps.piId.intValue()) 
         editPrivs += ",C";
     
       // is current user the submitter?  
       if (ps.submitterId == null ||  uid != ps.submitterId.intValue()) {
         // no edit if they didn't submit the proposal   
       }
       else if ((isDDT && isDDTProp) || (!isDDT && !isDDTProp)) {
         if (ps.allowEdit != null && ps.allowEdit.intValue() > 0) {
           //  CDO override for edit privileges
           editPrivs += ",M";
         }
         else if (isDDT) {
           if ( isDDTProp ) {
             // DDTs can only be edited if they haven't been submitted
             // eventually we need to verify it is current ddt ao!
             // not withdrawn cause for DDTs that can be set after submission
             // and we already have ddtmanager entry. need to investigate
             // further ....  Maybe allow if no entry in ddtmanager???
             if (ps.status.equals("INCOMPLETE")) {
               if (ddtAO == null || ddtAO.equals("") || ddtAO.equals(ps.ao)) {
                 editPrivs += ",M";
               }
             }
           }
         }
         else if (ps.type.equals("CAL")) {
           // CALs can only be edited if they haven't submitted because we might 
           // have started approving/migrating them
           if (ps.ao.equals(currentAO) && 
               (ps.status.equals("INCOMPLETE") || ps.status.equals("WITHDRAWN"))) {
             editPrivs += ",M";
           }
         }
         else if (ps.joint.startsWith("CXO-")) {
           // Joint from other reviews can only be edited if not submitted 
           // because we might be trying to approve and migrate
           if (ps.ao.equals(currentAO) && 
               (ps.status.equals("INCOMPLETE") || ps.status.equals("WITHDRAWN"))) {
             editPrivs += ",M";
           } else {
              logger.trace("JOINT CXO no edit: " + currentAO +  ps.ao + "  " + ps.status);
           }
         }
         else if (ps.type.indexOf("GTO") >= 0) {
           // GTOs can come in during the regular cycle and then after. 
           // sometimes they are migrated to OCat early
           if (!ps.status.equals("APPROVED") &&
               !ps.status.equals("HOLD")  &&
               !ps.status.equals("REJECTED") ) {
             if ((isGTO || isCfP) && ps.ao.equals(currentAO)) {
               editPrivs += ",M";
             }
           }
         }
         else {
           if (ps.ao.equals(currentAO) && isCfP) {
             if (!ps.status.equals("APPROVED") &&
                 !ps.status.equals("REJECTED") ) {
               editPrivs += ",M";
             }
           }
         }
       }
     }
     catch (Exception exc) {
       retval = false;
       
     }

     return retval;
   }

   public String getGridEntry(int uid)
   {
     String str = "";
     String pistr = "";
     /* this would identify where PI and Submitter differ */
     /* after drop 3b was told to remove this */
     /*
     try {
        logger.debug("piid=" + ps.piId.intValue());
        logger.debug(ps.proposalId + ": submitterid=" + ps.submitterId.intValue());
        if (ps.piId.intValue() != ps.submitterId.intValue()) pistr="*";
     } catch (Exception exc) {
        logger.trace("pi-submitter",exc);
     }
     */
     String ownStr = "";
     try {
       if (ps.submitterId != null && ps.submitterId.intValue() == uid)
         ownStr="Y";
     } catch (Exception exc) {
        logger.trace("own-submitter",exc);
     }
     str = "\n{ \"id\":\"" + ps.proposalId + "\",\n\"data\":[";
     if (ps.piId != null)
       str += "\"" + ps.piId + "\",";
     else 
       str += "\"" + "0" + "\",";
     str += "\"" + editPrivs + "\",";
     str += "\"" + ownStr + "\",";
     str += "\"" + ps.proposalNumber + "\",";
     str += "\"" + ps.type + "\",";
     str += "\"" + ps.status + "\",";
     str += "\"" + submissionDateStr + "\",";
     str += "\"" + StringEscapeUtils.escapeHtml4(CPS.getString(ps.last)) + pistr + "\",";
     str += "\"" + StringEscapeUtils.escapeHtml4(CPS.getString(ps.categoryDescription)) + "\",";
     str += "\"" + StringEscapeUtils.escapeHtml4(CPS.getString(ps.title)) + "\",";
     str += "\"" + StringEscapeUtils.escapeHtml4(CPS.getString(ps.joint)) + "\"";
     str += "] }";

     return str;
   }

  
}

