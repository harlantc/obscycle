package edu.harvard.asc.cps.xo;
/************************************************************************/
/*     Copyrights:                                                      */
/*                                                                      */
/*     Copyright (c) 2017-2021 Smithsonian Astrophysical Observatory    */
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import edu.harvard.cda.proposal.xo.VProposalCPSWithDetails;
import edu.harvard.cda.proposal.xo.ProposalCoInvestigator;
import edu.harvard.cda.proposal.xo.ProposalUploadInfo;
import edu.harvard.cda.proposal.xo.PersonAndPersonShort;


public class CPSProposal 
{
  public Boolean isDDTProp;
  public String currentAO;
  public Boolean isEdit;
  public Boolean isCfP;
  public VProposalCPSWithDetails vprop;
  public ProposalUploadInfo vupload;
  private static Logger logger = Logger.getLogger(CPSProposal.class);



  /**
   * Constructor
   *
   * @param ps proposal rest client data 
   * @param ao current cycle
   *
   */
  public CPSProposal(VProposalCPSWithDetails ps,String ao)
  {
    vprop = ps;
    vupload = null;
    currentAO = ao;
    isEdit = false;
    isCfP = false;
  }

  /**
   * Determine whether this proposal is editable by the current user
   *
   * @param  isCfP      true if the current time is within the normal CfP
   * @param  ddtRequest true if this is a DDT request
   * @param  ddtAO      current DDT cycle
   * @param  isGTO      true if this is a GTO request during GTO submission period
   * @param  uid        pers_id of the current user
   * @return boolean    value indicating  success/failure
   *
   */
  public boolean setEditPrivs(boolean isCfP,boolean ddtRequest,String ddtAO,boolean isGTO,int uid) 
  {
    boolean retval=true;
    this.isDDTProp=false;
    this.isEdit=false;
    this.isCfP=isCfP;
    try {
      Integer xx= Integer.valueOf(vprop.proposalNumber.substring(4));
      if (xx.intValue() > 8000) {
        isDDTProp=true;
      }
    }
    catch (Exception exc) {
      logger.error("setEditPrivs",exc);
      retval=false;
    }

    try {
     if (vprop.submitterId != null && uid == vprop.submitterId.intValue()) {

      if (!vprop.status.equals("INCOMPLETE")) {
         // must be incomplete to edit  
         // in proposal summary, they can edit the status 
         logger.debug(vprop.proposalId + "noedit: Status is " + vprop.status );
      }
      else if ((ddtRequest && !isDDTProp ) ||
               (!ddtRequest && isDDTProp)) {
         // must have entered as DDT for DDTs or vice versa
      }
      else if (vprop.allowEdit != null && vprop.allowEdit.intValue() > 0) {
          isEdit = true;
      } 
      else if (ddtRequest) {
        if (isDDTProp.booleanValue() )  {
          if (ddtAO == null || ddtAO.equals("") || ddtAO.equals(vprop.ao)) {
            isEdit = true;
          }
        }
      } 
      else if (vprop.type.equals("CAL")) {
        if (vprop.ao.equals(currentAO) ) {
          isEdit = true;
        }
      }
      else if (vprop.joint.startsWith("CXO-")) {
        if (vprop.ao.equals(currentAO) ) {
          isEdit = true;
        }
      }
      else if (vprop.type.indexOf("GTO") >= 0) {
        if ((isGTO || isCfP) && vprop.ao.equals(currentAO)) {
            isEdit = true;
         }
      }
      else {
        if (vprop.ao.equals(currentAO) && this.isCfP) {
          isEdit = true;
        }
        else {
         logger.debug(vprop.proposalId + "noedit: not current ao");
        }
      }

     } else if (vprop.submitterId == null) {
        logger.error("Submitter Id is null for " + vprop.proposalId);
     }
    } catch (Exception exc) {
       retval = false;
       logger.error("setEditPrivs",exc);
    }

    logger.debug(vprop.proposalId + "editpriv return: " + isEdit);
    return retval;
  }

  /**
   *  Determine if user can still view.  This is to support a CoI that 
   *  already logged in but has recently been removed from a proposal
   *  @param uid  user database id
   *  @return boolean true if user should still view  proposal
   */
  public boolean isViewable(int uid) 
  {
    boolean isViewable=false;

    if (vprop.submitterId != null && uid == vprop.submitterId.intValue()) {
       isViewable=true;
    } else  if (vprop.piId != null && uid==vprop.piId.intValue()) {
       isViewable=true;
    } else if (vprop.coInvestigators != null) {
      for (int ii=0;ii<vprop.coInvestigators.size();ii++) {
        ProposalCoInvestigator coi= vprop.coInvestigators.get(ii);
        if (uid == coi.pers_id ) {
          isViewable=true;
          break;
        }
      }
    }
    return isViewable;
  }

  /**
   *   Review and Submit page
   *  @return String xml for summary page
   */
  public String getSummaryEntry()
  {
      String str = "<data>\n";
      str += "<propno>" + vprop.proposalNumber + "</propno>,";
      str += "<pid>" + vprop.proposalId + "</pid>,";
      str += "<isEdit>" + isEdit.toString() + "</isEdit>,";
      str += "<isDDT>" + isDDTProp.toString() + "</isDDT>,";
      str += "<pstatus>" + CPS.getString(vprop.status) + "</pstatus>,";
      str += "<proposal_type>" + CPS.getString(vprop.type) + "</proposal_type>,";
      str += "<currentAO>" + currentAO + "</currentAO>,";
      str += "<mcop>" + String.valueOf(vprop.multiCycle) + "</mcop>,";
      str += "<proposal_title><![CDATA[" + CPS.getString(vprop.title) + "]]></proposal_title>,";
      str += "<total_time>" + CPS.getDouble(vprop.totalTime,0) + "</total_time>,";
      str += "<num_targets>" + CPS.getInt(vprop.numTargets) + "</num_targets>,";
      str += "<ready>" + "0" +  "</ready>";
      str += "<avail_peer_review><![CDATA[" + CPS.getString(vprop.availPeerReview) + "]]></avail_peer_review>";
      if (vprop.ddtProposalDatum != null) 
        str += "<response_time><![CDATA[" + CPS.getString(vprop.ddtProposalDatum.response_time) + "]]></response_time>,";
      str += "</data>\n";

      return str;
  }

  /**
   *   Supplemental Files Page
   *
   *  @param  uid  pers_id of the current user
   *  @return String xml for supplemental page form
   */
  public String getUploadEntry(int uid)
  {
      String is_bpp="N";
      Long team = Long.valueOf(0);
      if (vupload != null) {
        // the proposal Team Expertise
        if (vupload.cvUploadMSSE != null)
          team =  vupload.cvUploadMSSE;
      
        logger.debug("Team Expertise:  " + team + " proposalId= " + vprop.proposalId + " pi=" + vprop.piId  + " submitter=" + vprop.submitterId);
      }
      if (isBPP()) is_bpp="Y";


      String str = "<data>\n";
      str += "<propno>" + vprop.proposalNumber + "</propno>,";
      str += "<pid>" + vprop.proposalId + "</pid>,";
      str += "<isEdit>" + isEdit.toString() + "</isEdit>,";
      str += "<isDDT>" + isDDTProp.toString() + "</isDDT>,";
      str += "<isBPP>" + is_bpp + "</isBPP>,";
      str += "<team_upload>" + team +   "</team_upload>,";
      str += "<proposal_title><![CDATA[" + CPS.getString(vprop.title) + "]]></proposal_title>,";
      str += "</data>\n";

      return str;
  }

  /**
   *   Supplemental Files grid
   *
   *  @param  uid  pers_id of the current user
   *  @return String json for supplemental page form
   */
  public String getUploadGridEntry(int uid)
  {
    String str = "{\n\"rows\":[\n";
    String yuk = "No upload";
    String tstr = "";
    Boolean view_team=Boolean.valueOf(false);
    String warnstr = "";

  
    try {  
      // view Team: if current user is the PI
      if (vprop.piId != null && vprop.piId.intValue() == uid)
        view_team=true;
      else if ( (vprop.submitterId.intValue() == uid ) &&
        (vupload.cvPersId == null || vupload.cvPersId.intValue() ==0))
          view_team = true;

       // just in case they changed PIs and didn't update Profile TE
       if (vupload.cvPersId != null && vprop.piId != null &&
	   vupload.cvPersId.intValue() != vprop.piId.intValue())
         warnstr = "<span class='errmsg'>WARNING: Please refresh the TE for the current PI.</span></br>" ;

        
    } catch (Exception exc) {
      logger.debug("Error occurred determining if TE is viewable",exc);
    }

    if (vupload != null) {
      str += "{ id:" + "\"sj\"" + ",\ndata:[0,";
      str += "\"" + "Science Justification" + "\",";
      if (vupload.sjUploadMSSE != null)
        str += "\"" + "PDF" + "\",";
      else
        str += "\"" + "" + "\",";
    
      tstr = CPS.convertMS(vupload.sjUploadMSSE);
      if (tstr.equals("")) tstr = yuk;
      str += "\"" + tstr +  "\",";
      str += "] },\n";
      str += "{ id:" + "\"team\"" + ",\ndata:[0,";
      str += "\"" + "Team Expertise" + "\",";
      if (vupload.cvUploadMSSE != null)  {
        if ( view_team) {
            str += "\"" + "PDF" + "\",";
        } else {
            str += "\"" + "" + "\",";
        }
      } else {
        str += "\"" + "" + "\",";
      }
      tstr = CPS.convertMS(vupload.cvUploadMSSE);
      if (tstr.equals("")) tstr = yuk;
      str += "\"" + tstr +  "\"," + "\"" + warnstr + "\"";
      str += "] },\n";

      // As of Cycle 22, previous chandra combined in CV file,
      // and for cycle 23 CV replaced by TE, so
      // only display PC if it exist in database for the older proposals
      // also moved from 2nd row to 3rd since it is no longer being used
      if (vupload.pcUploadMSSE != null) {
        str += "{ id:" + "\"pc\"" + ",\ndata:[0,";
        str += "\"" + "Previous Chandra" + "\",";
        if (vupload.pcUploadMSSE != null)
          str += "\"" + "PDF" + "\",";
        else
          str += "\"" + "" + "\",";
        tstr = CPS.convertMS(vupload.pcUploadMSSE);
        if (tstr.equals("")) tstr = yuk;
        str += "\"" + tstr +  "\",";
        str += "] },\n";
      }
  
  
    }
  
    str += "]\n}\n";
  
    return str;
  }


  /**
   *  Proposal Cover page
   *  @return String xml for proposal cover form
   */
  public String getProposalEntry()
  {
      String str = "<data>\n";
      str += "<propno>" + vprop.proposalNumber + "</propno>,";
      str += "<pid>" + vprop.proposalId + "</pid>,";
      str += "<isEdit>" + isEdit.toString() + "</isEdit>,";
      str += "<isDDT>" + isDDTProp.toString() + "</isDDT>,";
      str += "<isCfP>" + isCfP.toString() + "</isCfP>,";
      str += "<proposal_type><![CDATA[" + vprop.type + "]]></proposal_type>,";
      if (vprop.type.indexOf("ARC") >= 0 || vprop.type.indexOf("THE") >= 0) 
        str += "<requested_budget>" + CPS.getDouble(vprop.totalTime,0) + "</requested_budget>,";
      else 
        str += "<requested_budget>" +   "</requested_budget>,";
      str += "<request_extra_flag>"+ vprop.requestExtraFlag + "</request_extra_flag>,";

      str += "<proposal_title><![CDATA[" + CPS.getString(vprop.title) + "]]></proposal_title>,";
      str += "<category_descrip><![CDATA[" + CPS.getString(vprop.categoryDescription) + "]]></category_descrip>,";
      str += "<abstract><![CDATA[" + CPS.getString(vprop.abstractText) + "]]></abstract>,";
      str += "<scikeyResults><![CDATA[" + CPS.getString(vprop.scienceKeywords) + "]]></scikeyResults>,";
      str += "<multi_cycle>" + CPS.getYN(vprop.multiCycle) + "</multi_cycle>,";
      str += "<linked_proposal>" + CPS.getYN(vprop.linkedProposal) + "</linked_proposal>,";
      str += "<linked_propnum>" + CPS.getString(vprop.linkedPropNum) + "</linked_propnum>";
      if (vprop.ddtProposalDatum != null) {
        str += "<rights_justification><![CDATA[" + CPS.getString(vprop.ddtProposalDatum.rights_justification) + "]]></rights_justification>,";
      } 

      str += "</data>\n";

     return str;
   }

  /**
   *  Proposal Joint page
   *  @return String xml for proposal joint form
   */
  public String getJointEntry()
  {
      String str = "<data>\n";
      str += "<propno>" + vprop.proposalNumber + "</propno>,";
      str += "<pid>" + vprop.proposalId + "</pid>,";
      str += "<isEdit>" + isEdit.toString() + "</isEdit>,";
      str += "<isDDT>" + isDDTProp.toString() + "</isDDT>,";
      str += "<joint>" + CPS.getString(vprop.joint) + "</joint>,";
      str += "<proptype>" + CPS.getString(vprop.type) + "</proptype>,";
      str += "<proposal_title><![CDATA[" + CPS.getString(vprop.title) + "]]></proposal_title>,";

      str += "<hst_time>" + CPS.getInt(vprop.hstTime) + "</hst_time>,";
      str += "<hst_instr><![CDATA[" + CPS.getString(vprop.hstInstruments) + "]]></hst_instr>,";
      str += "<xmm_time>" + CPS.getDouble(vprop.xmmTime,1) + "</xmm_time>,";
      str += "<swift_time>" + CPS.getDouble(vprop.swiftTime,1) + "</swift_time>,";
      str += "<nustar_time>" + CPS.getDouble(vprop.nustarTime,1) + "</nustar_time>,";
      str += "<noao_time>" + CPS.getDouble(vprop.noaoTime,1) + "</noao_time>,";
      str += "<noao_instr><![CDATA[" + CPS.getString(vprop.noaoInstruments) + "]]></noao_instr>,";
      str += "<nrao_time>" + CPS.getDouble(vprop.nraoTime,1) + "</nrao_time>,";
      str += "<nrao_instr><![CDATA[" + CPS.getString(vprop.nraoInstruments) + "]]></nrao_instr>,";
      str += "</data>\n";
      
      return str;
  }

  /**
   *  Proposal PI page
   *
   *  @param  uid  pers_id of the current user
   *  @return String xml for proposal PI form
   */
  public String getPIEntry(int uid)
  {
      String sid="false";
      String str = "<data>\n";

      str += "<propno>" + vprop.proposalNumber + "</propno>,";
      str += "<pid>" + vprop.proposalId + "</pid>,";
      if (vprop.isPISelectable == null ) 
        str += "<cxc>false</cxc>,";
      else
        str += "<cxc>" + vprop.isPISelectable.booleanValue() + "</cxc>,";
      str += "<id>" + vprop.piId + "</id>,";
      if (isEdit && vprop.piId != null && uid == vprop.piId.intValue()) 
        sid="true";
      str += "<sid>" + sid + "</sid>,";
      if (vprop.piId != null && uid == vprop.piId.intValue()) 
        str += "<isEdit>" + "false" + "</isEdit>,";
      else
        str += "<isEdit>" + isEdit.toString() + "</isEdit>,";
      str += "<isDDT>" + isDDTProp.toString() + "</isDDT>,";
      str += "<proposal_title><![CDATA[" + CPS.getString(vprop.title) + "]]></proposal_title>,";
      str += "<first><![CDATA[" + CPS.getString(vprop.first) + "]]></first>,";
      str += "<middle><![CDATA[" + CPS.getString(vprop.middle) + "]]></middle>,";
      str += "<last><![CDATA[" + CPS.getString(vprop.last) + "]]></last>,";
      str += "<institute><![CDATA[" + CPS.getString(vprop.institution) + "]]></institute>,";
      str += "<country><![CDATA[" + CPS.getString(vprop.country) + "]]></country>,";
      if (vprop.isPISelectable == null || !vprop.isPISelectable) {

        str += "<medium><![CDATA[" + CPS.getString(vprop.personMedium) + "]]></medium>,";
        str += "<fax><![CDATA[" + CPS.getString(vprop.fax) + "]]></fax>,";
        str += "<street><![CDATA[" + CPS.getString(vprop.street) + "]]></street>,";
        str += "<dept><![CDATA[" + CPS.getString(vprop.department) + "]]></dept>,";
        str += "<mailstop><![CDATA[" + CPS.getString(vprop.mailStop) + "]]></mailstop>,";
        str += "<city><![CDATA[" + CPS.getString(vprop.city) + "]]></city>,";
        str += "<state><![CDATA[" + CPS.getString(vprop.state) + "]]></state>,";
        str += "<zip><![CDATA[" + CPS.getString(vprop.zip) + "]]></zip>,";
        str += "<orcid><![CDATA[" + CPS.getString(vprop.orcId) + "]]></orcid>,";
        str += "<email><![CDATA[" + CPS.getString(vprop.email) + "]]></email>,";
        str += "<telephone><![CDATA[" + CPS.getString(vprop.phone) + "]]></telephone>,";
      }
      str += "</data>\n";
      
      return str;
  }

  /**
   *  Proposal CoI page
   *  @return String xml for proposal CoI form
   */
  public String getCoIEntry()
  {
      Integer cost_persid = null;
      ProposalCoInvestigator costpi = getCostPI();
      if (costpi != null)
         cost_persid = costpi.pers_id;
      String str = "<data>\n";
      str += "<propno>" + vprop.proposalNumber + "</propno>,";
      str += "<pid>" + vprop.proposalId + "</pid>,";
      str += "<piid>" + CPS.getInt(vprop.piId) + "</piid>,";
      str += "<isEdit>" + isEdit.toString() + "</isEdit>,";
      str += "<isDDT>" + isDDTProp.toString() + "</isDDT>,";
      str += "<proposal_title><![CDATA[" + CPS.getString(vprop.title) + "]]></proposal_title>,";

      str += "<coi_contact>" +CPS.getYN(vprop.coiContact) +  "</coi_contact>,";
      str += "<coi_phone><![CDATA[" + CPS.getString(vprop.coiPhone) + "]]></coi_phone>,";
      str += "<cost_pi><![CDATA[" + CPS.getInt(vprop.costPiCoin) +  "]]></cost_pi>,";
      str += "<cost_persid><![CDATA[" + CPS.getInt(cost_persid) +  "]]></cost_persid>,";
      str += "<coi_last></coi_last>,";
      str += "<coi_first></coi_first>,";
      str += "<coi_email></coi_email>,";
      str += "<coi_country></coi_country>,";
      str += "<coi_institute></coi_institute>";
      str += "</data>\n";
      
      return str;
  }

  /**
   *  Proposal CoI Table (grid)
   *  @return String json for proposal CoI form
   */
  public String getCoIGridEntry()
  {
    String str = "{\n\"rows\":[\n";
    for (int ii=0;ii<vprop.coInvestigators.size();ii++) {
      ProposalCoInvestigator coi= vprop.coInvestigators.get(ii);
      if (ii!=0) str += ",";
      str += "{ \"id\":" + CPS.getInt(coi.coin_id) + ",\n\"data\":[";
      int isSelectable = 0;
      String email = coi.email;
      if (coi.is_selectable) {
         email = "";
         isSelectable=1;
      }
      str += "\"" + CPS.getInt(coi.pers_id) + "\",";
      str += "\"" + CPS.getInt(isSelectable) + "\",";
      str += "\"" + StringEscapeUtils.escapeHtml4(CPS.getString(email)) + "\",";
      str += "\"" + CPS.getInt(coi.coin_number) + "\",";
      str += "\"" + StringEscapeUtils.escapeHtml4(CPS.getString(coi.last)) + "\",";
      str += "\"" + StringEscapeUtils.escapeHtml4(CPS.getString(coi.first)) + "\",";
      str += "\"" + StringEscapeUtils.escapeHtml4(CPS.getString(coi.country)) + "\",";
      str += "\"" + StringEscapeUtils.escapeHtml4(CPS.getString(coi.institution)) + "\"";
      str += "] }\n";
    }
    str += "]\n}\n";

    return str;
  }

  /**
   *  Proposal DDT Page
   *  @return String xml for proposal DDT form
   */
  public String getDDTEntry()
  {
      String str = "<data>\n";
      str += "<propno>" + vprop.proposalNumber + "</propno>,";
      str += "<pid>" + vprop.proposalId + "</pid>,";
      str += "<isEdit>" + isEdit.toString() + "</isEdit>,";
      str += "<isDDT>" + isDDTProp.toString() + "</isDDT>,";
      str += "<proposal_title><![CDATA[" + CPS.getString(vprop.title) + "]]></proposal_title>,";

      // coi info
      if (vprop.ddtProposalDatum != null) {
        str += "<contact_info><![CDATA[" + CPS.getString(vprop.ddtProposalDatum.contact_info) + "]]></contact_info>,";

        str += "<target_justification><![CDATA[" + CPS.getString(vprop.ddtProposalDatum.target_justification) + "]]></target_justification>,";
        str += "<response_time><![CDATA[" + CPS.getString(vprop.ddtProposalDatum.response_time) + "]]></response_time>,";
        str += "<response_justification><![CDATA[" + CPS.getString(vprop.ddtProposalDatum.response_justification) + "]]></response_justification>,";
        str += "<next_cfp><![CDATA[" + CPS.getString(vprop.ddtProposalDatum.next_cfp) + "]]></next_cfp>,";
        str += "<transient_behavior><![CDATA[" + CPS.getString(vprop.ddtProposalDatum.transient_behavior) + "]]></transient_behavior>,";
        str += "<xmm_ddt><![CDATA[" + vprop.ddtProposalDatum.xmm_ddt + "]]></xmm_ddt>,";
        str += "<xmm_status><![CDATA[" + CPS.getString(vprop.ddtProposalDatum.xmm_status) + "]]></xmm_status>,";
        str += "<prev_request><![CDATA[" + vprop.ddtProposalDatum.prev_request + "]]></prev_request>,";
        str += "<prev_cycles><![CDATA[" + CPS.getString(vprop.ddtProposalDatum.prev_cycles) + "]]></prev_cycles>,";
      }
      str += "</data>\n";
      
      return str;
  }


  /*
   *  Determines if a Science Justification (SJ) is needed 
   *  SJ is not needed for GTO, CAL or DDTs
   *  SJ is not needed for Joint proposal approved at the other review
   *
   * @return boolean   true if SJ is required
   *
   */
  public boolean needsSJ()  {
     boolean retval =true;   
     if (isDDTProp) retval=false;
     else if (vprop.type.indexOf("GTO") >= 0) retval = false;
     else if (vprop.type.indexOf("CAL") >= 0) retval = false;
     else if (vprop.joint != null && vprop.joint.indexOf("CXO") >= 0) retval = false;
 
     return retval;
  }

  /*
   *  Determines if TOO Details are required
   *  (note: this needs to be improved because slow DDTs don't need details)
   *
   * @return boolean   true if TOO Details are required
   *
   */
  public boolean shouldHaveTOODetails()
  {
     boolean retval =false;   
     if (vprop.type.indexOf("TOO") >=0) 
        retval = true;
     else if (isDDTProp) {
        String responseTime=null;
        if (vprop.ddtProposalDatum != null)
            responseTime = vprop.ddtProposalDatum.response_time;
       
        if ((responseTime == null) || 
            (responseTime.indexOf(CPSConstants.NONTRANSIENT) < 0))
          retval = true;
     }
       
     return retval;
  }
  /*
   *  Determines if this is an Archive or Theory proposal
   *
   * @return boolean   true if archive or theory request
   *
   */
  public boolean isArcTheory()
  {
     boolean retval =false;   
     if (vprop.type.indexOf("ARC") >= 0 ||
         vprop.type.indexOf("THE") >= 0 )
       retval=true;

     return retval;
  }

  /*
   *  Determines if this is a BPP proposal
   *
   * @return boolean   true if BPP (LP,VLP,XVP)
   *
   */
  public boolean isBPP()
  {
     boolean retval =false;   
     if (vprop.type.indexOf("LP") >= 0 ||
         vprop.type.indexOf("XVP") >= 0 )
       retval=true;

     return retval;
  }


  /*
   *  If proposal has 1st CoI as Observer flag set, return  the 1st CoI entry
   *
   * @return ProposalCoInvestigator  returns 1st CoI or null based on coi contact flag
   *
   */
  public ProposalCoInvestigator getObserver()
  {
    ProposalCoInvestigator observer=null;
    if (vprop.coiContact != null && vprop.coiContact.booleanValue() == true) {
      for (int ii=0;ii<vprop.coInvestigators.size();ii++) {
        ProposalCoInvestigator coi= vprop.coInvestigators.get(ii);
        if (coi.coin_number == 1) {
           observer=coi;
           break;
        }
      }
    }
    return observer;
  }

  /*
   *  If proposal has cost PI number, return the Cost PI
   *
   * @return ProposalCoInvestigator  returns cost pi record
   *
   */
  public ProposalCoInvestigator getCostPI()
  {
    ProposalCoInvestigator costpi=null;
    if (vprop.costPiCoin != null ) {
      for (int ii=0;ii<vprop.coInvestigators.size();ii++) {
        ProposalCoInvestigator coi= vprop.coInvestigators.get(ii);
        if (vprop.costPiCoin.intValue() == coi.coin_number) {
           costpi=coi;
           break;
        }
      }
    }
    return costpi;
  }

  public String WriteProposal()
  {
    // don't want to use base class because that has every field
    // including internal database fields
    String json = "";
    try {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      json = gson.toJson(vprop);
    } catch (Exception e) {
      json="";
      logger.error(e);
    }
    return json;

  }

  public String WriteDDT()
  {
     StringBuffer sb = new StringBuffer("");
     sb.append("SUBMISSION[]=CPS\n");
     sb.append("DDT.CYCLE[]=" + vprop.ao + "\n") ;
     sb.append("PROPOSAL.NUMBER[]=" + vprop.proposalNumber + "\n") ;
     sb.append("OBSERVER.TYPE[]=" + vprop.type + "\n") ;
     sb.append("PROPOSAL.TITLE[]=" + vprop.title + "\n") ;
     sb.append("SUBJECT.CATEGORY[]=" + vprop.categoryDescription + "\n") ;
     sb.append("NAME.LAST[]=" + vprop.last + "\n") ;
     sb.append("NAME.FIRST[]=" + vprop.first + "\n") ;
     sb.append("EMAIL.ADDRESS[]=" + vprop.email + "\n") ;
     ProposalCoInvestigator coi = getObserver();
     if (coi != null) {
       sb.append("COI.LAST[]=" + coi.last + "\n");
       sb.append("COI.EMAIL[]=" + coi.email + "\n");
     }

     if (vprop.ddtProposalDatum != null) {
       sb.append("CONTACT.INFO[]=" + CPS.getString(vprop.ddtProposalDatum.contact_info) + "\n");
       sb.append("URGENCY[]=" + vprop.ddtProposalDatum.response_time + "\n") ;
     } else {
       sb.append("URGENCY[]=SLOW\n") ;
     }
     sb.append("NUMBER.OF.TARGETS[]=" + CPS.getInt(vprop.numTargets) + "\n") ;
     sb.append("TOTAL.TIME[]=" + CPS.getDouble(vprop.totalTime,0) + "\n") ;
     return sb.toString();
  }
}

