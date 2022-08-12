package edu.harvard.asc.cps.cpsapp;
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
import edu.harvard.asc.cps.xo.*;
import edu.harvard.cda.proposal.restclient.IRestClient;
import edu.harvard.cda.proposal.restclient.RestClient;
import edu.harvard.cda.proposal.xo.*;
import edu.harvard.cda.coords.CoordSystem;
import edu.harvard.cda.coords.Coordinate;

import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.tika.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.lang.Thread;

import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/******************************************************************************/
/**
 */

public class TgtManager extends HttpServlet 
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
	service(request, response);
  }

  /****************************************************************************/
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
	service(request, response);
  }

  /****************************************************************************/
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

    cpsProperties = CPS.getProperties(context);
    uploadPath = cpsProperties.getProperty("cps.upload.path");

    
  }

  /****************************************************************************/
  /**
   * Handle a submission from a browser.  Five operations are provided:
   * <ol>
   *    <li>RENUMBER - 
   *    <li>DELETE - 
   *    <li>CREATE - 
   *    <li>CLONE - 
   *    <li>UPLOAD - 
   * </ol>
   *
   * @param request The request object as passed in by the browser.
   * @param response The response object that will be passed back to
   * the browser.
   */

  public void service( HttpServletRequest request,
		       HttpServletResponse response )
    throws ServletException, IOException
  {
    RequestDispatcher dispatcher = null;
    String tgtmsg = "";
    String url="";
    Integer clonetgt = 0;
    HttpSession session;

    // Get the session object.
    session = request.getSession(false);
    if (session == null || !request.isRequestedSessionIdValid()) {
      logger.error("Invalid Session");
      url  = response.encodeURL("/prop_logout.jsp?msg=Invalid Session");
      dispatcher = getServletContext().getRequestDispatcher(url);
      dispatcher.forward(request, response);
    }
    //logger.trace( "Session "  + session.getId());

    // CfP 
    session.removeAttribute("tgtmsg");

    Integer userId;
    try {
      userId=(Integer)session.getAttribute("userId");
      if (userId == null || userId.intValue() <= 0) {
        logger.error("Invalid session, no userid");
        try {
          session.invalidate();
        } catch (Exception e) {
          logger.error(e);
        }

        url  = response.encodeURL("/prop_logout.jsp?msg=Invalid User");
        dispatcher = getServletContext().getRequestDispatcher(url);
        dispatcher.forward(request, response);
      }
    }
    catch (Exception exc) {
      logger.error(exc);
      session.invalidate();
      url  = response.encodeURL("/prop_logout.jsp?msg=Invalid User");
      dispatcher = getServletContext().getRequestDispatcher(url);
      dispatcher.forward(request, response);
    }

    try {
      Collection<Part> fileParts = request.getParts();
      for (Part part : fileParts) {
         logger.debug("PART: " + part.getName() + "----------");
      }
    } catch (Exception ex) {
       logger.error("Exception printing parts",ex);
       tgtmsg=CPS.getHtmlError("Error processing parameters") + "\n";
    }

    String  pidStr = "";
    String  operation="";
    String  gridvalues="";
    Integer pid = 0;
    
    try {
      pidStr= Parameter.get(request,"pid");
      pid = Integer.valueOf(pidStr);
      operation = Parameter.get(request, "operation" );
      gridvalues = Parameter.get(request, "gridvalues" );
    }
    catch (Exception exc) {
      logger.error(exc);
      tgtmsg = CPS.getHtmlError("Error occurred processing parameters.") + "\n";
    }
    logger.info("Operation is " + operation + " for PID " + pidStr);
    if (tgtmsg.equals("")) {
      
      if (operation.equals("RENUMBER")) {
        tgtmsg = processRenumber(session,gridvalues,pid);
      }
      else if (operation.equals("CREATE")) {
        tgtmsg = processCreate(session,request,pid);
      }
      else if (operation.equals("DELETE")) {
        tgtmsg = processDelete(session,gridvalues,pid);
      }
      else if (operation.equals("UPLOAD")) {
         tgtmsg = processUpload(session,request,pid,clonetgt);
      }
      else {
         tgtmsg = CPS.getHtmlError("Invalid request.") + "\n";
      }
    }
     //try {
     //Thread.sleep(3000);
     //} catch (Exception e) {};


    // this sets the session attribute for the return message
    session.setAttribute("tgtmsg",tgtmsg);

    String cparam="";
    if (clonetgt.intValue() > 0) 
      cparam="&clonetgt=" + clonetgt;
    url  = response.encodeURL("/app/prop_tgt_manage.jsp?id=" + pidStr + cparam );
    logger.info("tgtmsg: " + tgtmsg  + " goto: " + url);
    dispatcher = getServletContext().getRequestDispatcher(url);
    dispatcher.forward(request, response);

  }

  private String processUpload(HttpSession session,HttpServletRequest request,Integer pid,Integer clonetgt)
  {
    String msg = "";
    int tcnt = 0;
    Boolean clonetoo =true;
    Integer gridcnt= Integer.valueOf(0);
    Vector<String> tlist = new Vector<String>();
    Part filePart=null;
    InputStream is = null;

    try {
      filePart = request.getPart("upload_tgt");
      clonetoo = Parameter.getBoolean(request, "clonetoo" );
      clonetgt = Parameter.getInteger(request, "clonetgt" );
      gridcnt = Parameter.getInteger(request, "gridcnt" );
    }  catch (Exception exc) {
      logger.error("upload",exc);
      msg = CPS.getHtmlError("Error occurred processing parameters") + "\n";
    }

    if (msg.equals("") && filePart != null) {
      BufferedReader br = null;
      try {
        logger.info("Reading " + getFileName(filePart) + " size=" + filePart.getSize());
        is = filePart.getInputStream();
        Tika tika = new Tika();
        String contentType = tika.detect(is); 
        if (contentType.indexOf("plain") >= 0) 
        {
          br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
          String nextLine = "";
          while ((nextLine = br.readLine()) != null) {
            if (!nextLine.startsWith("#") )
              tlist.add(nextLine);
          }
          br.close();
        }
        else { 
          logger.info("TIKA: " + contentType);
          msg = CPS.getHtmlError("Invalid file type. Input file must be plain text.") + "\n";
        }

        is.close();
      } catch (Exception exc) {
        logger.error(exc);
        msg = CPS.getHtmlError("Error occurred processing target file for upload.") + "\n";
        try {
         if (br != null)
           br.close();
        } catch (Exception e) {
          logger.error(e);
        }
      } 
      if (tlist.size() > 50) 
        msg += CPS.getHtmlError("Too many targets specified. Maximum number of targets is 50 per file upload.") + "\n";
      if ((tlist.size() + gridcnt.intValue())  > 999)
        msg += CPS.getHtmlError("Too many targets specified. Maximum number of total targets for a proposal is 999.") + "\n";
      if (msg.equals("")) {
        try {
          CPSUtils cpsutil = new CPSUtils();
          IRestClient api = cpsutil.getAPI(cpsProperties,session);
          Coordinate coords;
          Double etime;
          Double cntrate;

          for (int ii=0;ii<tlist.size() && ii < 999;ii++) {
            int linenbr=ii+1;
            etime= Double.valueOf(0);
            cntrate=Double.valueOf(0);
            try  {
              logger.debug(tlist.get(ii));
              String[] inStr = tlist.get(ii).split(",");
              if (inStr.length < 5) {
                msg += CPS.getHtmlError("Line " + linenbr + ": Invalid number of parameters for: " + tlist.get(ii) + "\n" ) ;
              }
              else {
                int ff = inStr.length - 4;
                try {
                  coords = validCoords(inStr[ff],inStr[ff+1]);
                  if (coords == null) {
                    logger.debug("Coord check failed: " + inStr[ff] + " " + inStr[ff+1]);
                    msg += CPS.getHtmlError("Line " + linenbr + ": Invalid coordinates for: " + tlist.get(ii)) + "\n";
                  }
                } catch (Exception exc){
                  msg += CPS.getHtmlError("Line " + linenbr + "Invalid coordinates for: " + tlist.get(ii)) + "\n";
                  logger.error("upload",exc);
                }
                try  {
                  etime = Double.valueOf(inStr[ff+2]);
                  if (etime < 1 || etime > 10000) {
                    msg += CPS.getHtmlError("Line " + linenbr + ": Requested time is invalid for: " + tlist.get(ii)) + "\n";
                   } 
                } catch (Exception exc){
                  msg += CPS.getHtmlError("Line " + linenbr + ": Requested time is invalid for: " + tlist.get(ii)) + "\n";
                  logger.error("upload",exc);
                }
                try  {
                  cntrate = Double.valueOf(inStr[ff+3]);
                } catch (Exception exc){
                  msg += CPS.getHtmlError("Line " + linenbr + ": Count Rate is invalid for:  " + tlist.get(ii)) + "\n";
                  logger.error("upload",exc);
                }
              }
            }
            catch (Exception exc){
                msg += CPS.getHtmlError("Line " + linenbr + ": Error occured processing : " + tlist.get(ii)) + "\n";
                logger.error("upload",exc);
            }
          }
          if (msg.equals("")) {
            for (int ii=0;ii<tlist.size() && ii < 999;ii++) {
              int linenbr = ii+1;
              try  {
                String[] inStr = tlist.get(ii).split(",");
                int ff = inStr.length - 4;
                StringBuffer tname = new StringBuffer("");
                coords = validCoords(inStr[ff+0],inStr[ff+1]);
                etime = Double.valueOf(inStr[ff+2]);
                cntrate = Double.valueOf(inStr[ff+3]);
                for (int xx=0;xx< ff;xx++) {
                  if (inStr[xx] != null) 
                    tname.append(inStr[xx].trim());
                }
                TargetParams tp = new TargetParams(etime,coords.getLon(),coords.getLat(),cntrate,tname.toString());
                if (clonetgt <= 0) {
	          TargetInsertDatum tc = new TargetInsertDatum(1,tp);
                  api.targetInsert(pid.intValue(),tc);
                } else  {
                  TargetCloneDatum tc = new TargetCloneDatum(pid,clonetoo.booleanValue(),1,tp);
                  api.targetClone(clonetgt.intValue(),tc);
                } 
                tcnt += 1;
              } catch (Exception exc) {
                logger.error(exc);
                msg += CPS.getHtmlError("Line " + linenbr + ": Error occurred adding entry for: ") + tlist.get(ii) + "\n";
              }
            }
          }
          if (tcnt > 0) 
            api.proposalUpdateLastSaved(pid.intValue());
        } catch (Exception exc) {
          logger.error(exc);
          msg += CPS.getHtmlError("Error occurred processing target file.") + "\n";
        }
      }
    }
    String retmsg = ""; 
    if (msg.length() > 1)  {
      retmsg = "Upload failed.\n" + msg;
    } else {
      retmsg = msg;
    }
    
    retmsg +=  tcnt + " out of " + tlist.size() + " targets added.\n";
    return retmsg;
  }

  private String processCreate(HttpSession session,HttpServletRequest request,Integer pid)
  {
    String  msg="";
    Integer tgtcnt = 0;
    Boolean clonetoo;
    Integer clonetgt;

    try {
       clonetoo = Parameter.getBoolean(request, "clonetoo" );
       clonetgt = Parameter.getInteger(request, "clonetgt" );
       tgtcnt = Parameter.getInteger(request, "tgtcnt" );
       CPSUtils cpsutil = new CPSUtils();
       IRestClient api = cpsutil.getAPI(cpsProperties,session);

       if (clonetgt <= 0) {
         TargetInsertDatum tc = new TargetInsertDatum(tgtcnt.intValue(),null);
         api.targetInsert(pid.intValue(),tc);
         String tstr=" target";
         if (tgtcnt > 1) tstr+="s";
         msg =tgtcnt + tstr + " successfully added.";
       }
       else {
         TargetCloneDatum tc = new TargetCloneDatum(pid,clonetoo.booleanValue(),tgtcnt.intValue(),null);
         api.targetClone(clonetgt.intValue(),tc);
         msg =tgtcnt + " targets successfully cloned.";
       }
       api.proposalUpdateLastSaved(pid.intValue());
         
    } catch (Exception exc) {
      
      logger.error(exc);
      msg += CPS.getHtmlError("Error: Unable to create targets.") + "\n";
    }
    return msg;
  }

  private String processRenumber(HttpSession session,String gridvalues,Integer pid)
  {
    String msg="";
    logger.info("RENUMBER: " + gridvalues);
    try {
      String[] earr = gridvalues.split("=");
      Integer targid = Integer.valueOf(earr[0].trim());
      Integer targno = Integer.valueOf(earr[1].trim());
      CPSUtils cpsutil = new CPSUtils();
      IRestClient api = cpsutil.getAPI(cpsProperties,session);
      api.targetReorder(targid.intValue(),targno.intValue());
      msg = "Targets successfully reordered";
      api.proposalUpdateLastSaved(pid.intValue());
    } catch (Exception exc) {
      logger.error(exc);
      msg = CPS.getHtmlError("Unable to reorder targets.") + "\n";
    }

    return msg;

  }
   

  private String processDelete(HttpSession session,String gridvalues,Integer pid)
  {
    String msg="";
    try {
      Integer targid = Integer.valueOf(gridvalues);
      CPSUtils cpsutil = new CPSUtils();
      IRestClient api = cpsutil.getAPI(cpsProperties,session);
      api.targetDelete(targid);

      msg="Target successfully deleted.";
      api.proposalUpdateLastSaved(pid.intValue());
    } catch (Exception exc) {
      logger.error(exc);
      msg = CPS.getHtmlError("Unable to delete target.") + "\n";
    }
   
    return msg;
  }

  /* ------------------------------------------------------------------ */
  private Coordinate validCoords(String raStr,String decStr)
  {
    Coordinate coords = null;

    if (raStr  != null && raStr.length() > 0 &&
        decStr != null && decStr.length() > 0 ) {
      try {
        coords = new Coordinate(raStr,decStr,"J2000");
      }
      catch (Exception exc) {
        logger.error(exc);
        coords = null;
      }
    }
    return coords;
  }

  /* ------------------------------------------------------------------ */
  private String getFileName(final Part part) {
    final String partHeader = part.getHeader("content-disposition");
    logger.info("Part Header = " +  partHeader);
    for (String content : part.getHeader("content-disposition").split(";")) {
        if (content.trim().startsWith("filename")) {
            return content.substring(
                    content.indexOf('=') + 1).trim().replace("\"", "");
        }
    }
    return null;
  }



  /**
   * Private variables
   */
    private static final long serialVersionUID = 1;
    private Properties cpsProperties;
    private String uploadPath;
    private static Logger logger = Logger.getLogger(TgtManager.class);




  /**
   * Private methods
   */

  /****************************************************************************/


}

/******************************************************************************/
