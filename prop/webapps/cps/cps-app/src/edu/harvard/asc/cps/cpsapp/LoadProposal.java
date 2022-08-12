package edu.harvard.asc.cps.cpsapp;
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

import edu.harvard.asc.cps.xo.*;
import edu.harvard.cda.proposal.restclient.IRestClient;
import edu.harvard.cda.proposal.xo.VProposalCPSWithDetails;
import edu.harvard.cda.proposal.xo.ProposalUploadedArtifact;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
 


/******************************************************************************/
/**
 */

public class LoadProposal extends HttpServlet 
{


  /****************************************************************************/
  /**
   * Handle a GET request from a browser --- simply initialize
   * validation state.
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void doGet( HttpServletRequest request,
		     HttpServletResponse response)
    throws ServletException, IOException
  {
        service(request,response);
  }

  /**************************************************************************/
  /**
   * Handle a POST request from a browser --- simply initialize
   * validation state.
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void doPost( HttpServletRequest request,
		      HttpServletResponse response )
    throws ServletException, IOException
  {
        service(request,response);
  }

  /**************************************************************************/
  /**
   * Handle the INIT operation.  This operation is invoked by the
   * servlet engine when it starts up.  
   *
   * @param config The configuration object established by the servlet
   * engine.
   */

  public void init( ServletConfig config )
    throws ServletException
  {

    ServletContext context = config.getServletContext();
    super.init(config);
    Properties cpsProperties = CPS.getProperties(context);

  }

  /****************************************************************************/
  /**
   * Handle a submission from a browser.  Operations supported:
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void service( HttpServletRequest request,
		       HttpServletResponse response )
    throws ServletException, IOException
  {

    String propno="";
    String msg="";
    String str="";
    String mypage = "";
    String pid = "";
    String type = "";

    Integer userId = 0;
    Integer mypid = 0;
    int uid = 0;

    boolean isCfP=false;
    boolean isGTO=false;
    boolean ddtRequest=false;
    String ddtAO = null;

    StringBuffer outputStr = new StringBuffer();

    // reload properties
    Properties cpsProperties = CPS.getProperties();

    // Get the session object.
    HttpSession session = request.getSession( false );

    if (session == null || !request.isRequestedSessionIdValid()) {
      propno= "Invalid";
      msg = "Invalid session.";
    }
    else {
      try {
        userId = (Integer)session.getAttribute("userId");
        if (userId == null || userId.intValue() <= 0) {
          logger.error("Invalid session, no userid");
          try {
          session.invalidate();
          } catch (Exception e) {
            logger.error(e);
          }

          session=null;
          propno= "Invalid";
        }
        else {
          uid = userId.intValue();
        }
      } catch (Exception ex) {
        logger.error(ex.getMessage() );
        logger.error("Invalid user ",ex );
        msg = "Invalid user.";
        propno= "Invalid";
      }
    }
    if (logger.isTraceEnabled()) {
      try {
        for ( Enumeration parameters = request.getHeaderNames();
            parameters.hasMoreElements(); ) {
          String parameterName = (String) parameters.nextElement();
          String parameterValue = request.getHeader( parameterName );
          logger.trace( "LOAD HEADER: " + parameterName + " = " +  parameterValue );
        }
      } catch (Exception exc) {
        logger.error("oops",exc);
      }
    }

 
    if (!propno.equals("Invalid")) {
      try {
        mypage = Parameter.get(request, "page" );
        pid = Parameter.get(request, "pid" );
        type = Parameter.get(request, "type" );
        mypid = Integer.valueOf(pid);
        // make sure the proposal being requested is valid for this user/session
        Boolean sess_pid = (Boolean)session.getAttribute((mypid.toString()));
        if (sess_pid == null ) {
          propno="Invalid";
          msg = "Unauthorized proposal access";
          logger.error("uid=" + uid + "): Unauthorized access pid=" + pid );
        }
        isCfP = CPS.isCfP();
        isGTO = CPS.isGTO();
        ddtRequest=(boolean)session.getAttribute("ddtRequest");
        ddtAO=(String)session.getAttribute("ddtAO");
        logger.info("uid=" + uid + " " + mypage + "  proposal=" + pid + " isCfP=" + isCfP + " isGTO= " + isGTO) ;

      } catch (Exception ex) {
        logger.error("uid="+uid+": for pid=" + pid,ex);
        msg = "Invalid parameters.";
        propno="Error";
        str = "<data>\n";
        str += "<emsg>" + msg + "</emsg>";
        str += "</data>\n";
      }
    }


    PrintWriter out=null;

    if (mypage.indexOf("PDF") >= 0) {
    }
    else if (!mypage.equals("COIGRID")&& !mypage.equals("UPLOADGRID")) {
      response.setContentType("text/xml");
      out=response.getWriter();
      out.println(CPSConstants.XMLSTR);
    } else  {
      response.setCharacterEncoding(CPSConstants.CHARSET);
      response.setContentType("application/json");
      out=response.getWriter();
    }

    if (!propno.equals("Invalid") && !propno.equals("Error")) {
      try {
        String currentAO = (String)session.getAttribute("currentAO");
        CPSUtils cpsutil = new CPSUtils();
        IRestClient api = cpsutil.getAPI(cpsProperties,session);
        VProposalCPSWithDetails vprop = null;
        CPSProposal cps = null;
        if (!mypage.equals("PDF") && !mypage.equals("CPSPDF") && !mypage.equals("CPSPDFANON")) {
          try {
              vprop = api.retrieveProposalInformation(mypid.intValue());
              cps = new CPSProposal(vprop,currentAO);
          } catch (Exception apiexc) {
              logger.error("API" ,apiexc);
              msg = "Database error occurred." ;
              msg += apiexc.getMessage();
              propno= "Error";
              str = "<data>\n";
              str += "<emsg>" + msg + "</emsg>";
              str += "</data>\n";
          }

         if (!cps.isViewable(uid) ) {
            propno="Error";
            msg = "This proposal is no longer viewable for the current user.";
         }
      

         if (!propno.equals("Invalid") && !propno.equals("Error")) {
          cps.setEditPrivs(isCfP,ddtRequest,ddtAO,isGTO,uid);
          session.removeAttribute("curprop");
          session.setAttribute("curprop",cps);
          if (mypage.equals("PROPOSAL")) {
            str = cps.getProposalEntry();
          }
          else if (mypage.equals("SUMMARY")) {
            str = cps.getSummaryEntry();
          }
          else if (mypage.equals("UPLOAD")) {
            session.removeAttribute("upload");
            cps.vupload = api.proposalUploadInfo(mypid.intValue());
            str = cps.getUploadEntry(uid);
          }
          else if (mypage.equals("JOINT")) {
            str = cps.getJointEntry();
          }
          else if (mypage.equals("PI")) {
            str = cps.getPIEntry(uid);
          }
          else if (mypage.equals("COI")) {
            str = cps.getCoIEntry();
          }
          else if (mypage.equals("COIGRID")) {
            str = cps.getCoIGridEntry();
          }
          else if (mypage.equals("UPLOADGRID")) {
            cps.vupload = api.proposalUploadInfo(mypid.intValue());
            str = cps.getUploadGridEntry(uid);
          }
          else if (mypage.equals("DDT")) {
            str = cps.getDDTEntry();
          }
          else {
            msg = "Unexpected load page type";
            logger.error("uid="+uid+": pid=" + pid + ": "+ msg);
            str = "<data>\n";
            str += "<emsg>" + msg + "</emsg>";
            str += "</data>\n";
          }
        }
       } else {
         // Process PDF which might also be TE
         try {
           //retrieve pdf from database 
           // need proposal_number,last,first,upload_type and bytes
           if (cps == null) {
             vprop = api.retrieveProposalInformation(mypid.intValue());
             cps = new CPSProposal(vprop,currentAO);
           }
           if (!cps.isViewable(uid) ) {
             
             propno = "Error";
             throw new Exception("Proposal is no longer viewable");

           }
           propno = cps.vprop.proposalNumber;
           String first = cps.vprop.first;
           if (first != null )
             first = first.substring(0,1);
           else 
             first = "n";
           first = first.replaceAll("[^A-Za-z]","");
           String last = cps.vprop.last;
           if (last != null) 
             last = last.replaceAll("[^A-Za-z]","");
           else 
             last = "unknown";
           int piid = 0;
           if (cps.vprop.piId != null) piid = cps.vprop.piId.intValue();
           // Make Team Expertise pdf end in te instead of team
           String fnameType = type.equals("team") ? "te" : type;
           String piName = first.toLowerCase() + last.toLowerCase() + "_";
           String fname= propno + "_" + piName + fnameType.toLowerCase() + ".pdf";
           String furl="attachment;filename=" + fname;
           // don't do this especially for supporting files page cause it 
           // messes up iframe!
           //String furl="inline;filename=" + fname;
           if (mypage.indexOf("CPSPDFANON") >= 0){
               String noName = fname.replace(piName, "");
               processDownloadPDF(propno,noName,response,cpsProperties,true);
           }
           else if (mypage.indexOf("CPSPDF") >= 0){
               processDownloadPDF(propno,fname,response,cpsProperties,false);
           }
           else {
             logger.info("Download " + furl);
             ProposalUploadedArtifact pua = null;
             if (type.equalsIgnoreCase("team")) {
               pua = api.proposalPrincipalInvestigatorCV(mypid.intValue());
             } else {
               pua = api.proposalRetrieveArtifact(mypid.intValue(),type);
             }
             if (pua != null && pua.blob.length > 1) {
               response.setContentType("application/pdf");
               response.addHeader("Content-Disposition", furl);
               ServletOutputStream outs=null;
               outs=response.getOutputStream();
               if (pua != null ) 
                 outs.write(pua.blob,0,pua.blob.length);
               outs.close();
             } else {
               response.setContentType("text/plain");
               response.resetBuffer();
               out=response.getWriter();
               msg = "Unable to retrieve PDF file for " + cps.vprop.proposalNumber;
               out.println(msg);
               out.close();
             }
           }
         }
         catch (Exception  exc) {
            logger.error("Yikes",exc);
            response.setContentType("text/plain");
            response.resetBuffer();
            out=response.getWriter();
            if (propno.equals("Error")) {
              msg = "Proposal is no longer viewable. Retrieval of PDF file failed.";
            } else {
              msg = "Retrieval of PDF file failed.";
            }
            out.println(msg);
            out.close();
         }
       }
         

      } catch (Exception ex) {
         logger.error("uid="+uid+": for pid=" + pid,ex);
         msg = "Unable to retrieve data for" + propno;
         str = "<data>\n";
         str += "<emsg>" + msg + "</emsg>";
         str += "</data>\n";
      }

    }
    if (propno.equals("Invalid") || propno.equals("Error")) {
      str = "<data>\n";
      str += "<propno>" + propno + "</propno>";
      str += "<emsg>" + msg + "</emsg>";
      str += "</data>\n";
    }
     

    if (mypage.indexOf("PDF") < 0 )  {
      outputStr.append(str);
      logger.debug(mypage + " done.");
      logger.trace(outputStr.toString());
      out.println(outputStr.toString());
      out.close();
    } else  if( propno.equals("Invalid") || propno.equals("Error")) {
      out.println(msg);
      out.close();
    }
  }
 
  private void processDownloadPDF(String propno, String fname,
                                  HttpServletResponse response,
                                  Properties cpsProperties, boolean isAnon)
         throws IOException
  {
     String msg="";
     String envStr;
     String outdir="";
     File fileR = null;
     FileInputStream is=null;
     try {
       outdir=cpsProperties.getProperty("cps.tmp.path");
       CPSPdf mypdf = new CPSPdf();
       mypdf.genPDF(propno,outdir,cpsProperties,false, isAnon);
       String fullfile= outdir + "/" + fname;
       String furl="attachment; filename=" + fname;
       //String furl="inline; filename=" + fname;
       fileR = new File(fullfile);
       is=new FileInputStream(fileR);
       ServletOutputStream outs=null;
       response.setContentType("application/pdf");
       response.addHeader("content-disposition", furl);
       outs=response.getOutputStream();
       byte buffer[]=new byte[20*1024];
       while(true) {
         int readSize=is.read(buffer);
         if(readSize==-1)
           break;
         outs.write(buffer,0,readSize);
       }
       outs.close();
       is.close();
       try {
         if (!fileR.delete()) {
           logger.error("Delete failed. " + fullfile);
         }
     
       } catch(Exception dexc) {
         logger.error("Delete failed",dexc);
       }

     } catch (Exception  exc) {
         try {
           if (is != null) is.close();
         } catch (Exception e) {
           logger.error(e);
         }

         PrintWriter out=null;
         logger.error("Yikes",exc);
         response.setContentType("text/plain");
         response.resetBuffer();
         out=response.getWriter();
         msg = "PDF Creation of Chandra form failed for " + propno;
         out.println(msg);
         out.close();
    }
  }

  /**
   * Private variables
   */
    private static final long serialVersionUID = 1;
    private static Logger logger = Logger.getLogger(LoadProposal.class);


  /**
   * Private methods
   */

  /****************************************************************************/


}

/******************************************************************************/
