package edu.harvard.asc.cps.cpsapp;
/************************************************************************/
/*     Copyrights:                                                      */
/*                                                                      */
/*     Copyright (c) 2017-2020 Smithsonian Astrophysical Observatory    */
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

import org.apache.tika.*;
//import org.apache.pdfbox.pdmodel.*;
import com.google.common.io.ByteStreams;

import org.apache.log4j.Logger;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;
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

public class PropUpload extends HttpServlet 
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

    uiLbl = new HashMap<String,String>();
    uiLbl.put("sj","Science Justification");
    uiLbl.put("pc","Previous Chandra");
    uiLbl.put("team","Team Expertise");

 
    try {
      maxFileSize= Integer.valueOf(cpsProperties.getProperty("cps.sj.size"));
      if (maxFileSize <= 0) {
        maxFileSize=10;
        logger.error("Using default maxFileSize: 10 ");
      }
    } catch (Exception ex) {
      maxFileSize=10;
      logger.error("Using default maximum FileSize: 10 " , ex);
    }
    try {
      maxSJ= Integer.valueOf(cpsProperties.getProperty("cps.sj.pages"));
      if (maxSJ <= 0) {
        maxSJ=4;
        logger.error("Using default maxSJ pages: 4 ");
      }
    } catch (Exception ex) {
      maxSJ=4;
      logger.error("Using default maximum SJ pages: 4 ");
    }
    try {
      maxSJBPP= Integer.valueOf(cpsProperties.getProperty("cps.sj.bpp.pages"));
      if (maxSJBPP <= 0) {
        maxSJBPP=6;
        logger.error("Using default maxSJ BPP pages: 6 ");
      }
    } catch (Exception ex) {
      maxSJBPP=6;
      logger.error("Using default maximum SJBPP pages: 6 ");
    }
    try {
      maxTeam= Integer.valueOf(cpsProperties.getProperty("cps.team.pages"));
      if (maxTeam <= 0) {
        maxTeam=4;
        logger.error("Using default maxTeam pages: 4 ");
      }
    } catch (Exception ex) {
      maxTeam=4;
      logger.error("Using default maximum Team Expertise page: 4 " , ex);
    }
    
  }

  /****************************************************************************/
  /**
   * Handle a submission from a browser. 
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
    String url;
    String pid="";
    Integer userId = 0;
    int uid = 0;
    Integer mypid= Integer.valueOf(0);
    String msg;
    CPSMsg cpsmsg = new CPSMsg();

    msg = "";

    // Get the session object.
    HttpSession session = request.getSession(false);
    if (session == null || !request.isRequestedSessionIdValid()) {
      logger.error("Invalid Session");
      msg = "Invalid Session";
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

          msg = "Invalid session, no User";
        }
        else {
          uid = userId.intValue();
        }
      } catch (Exception ex) {
        logger.error(ex.getMessage() );
        logger.debug("Invalid user ",ex );
        msg = "Invalid user.";
      }
    }
    if (!msg.equals("")) {
      url  = response.encodeURL("/prop_logout.jsp?msg=Invalid Session");
      dispatcher = getServletContext().getRequestDispatcher(url);
      dispatcher.forward(request, response);
    }

    logger.debug( "uid= " + uid + "  Session "  + session.getId());
    session.removeAttribute("upload");
    session.removeAttribute("uploadok");

    // For debugging, output all the parameters.
    logger.trace( "Dumping parts ..." );
    try {
      Collection<Part> fileParts = request.getParts();
      for (Part part : fileParts) {
         logger.trace("PART: " + part.getName() + "----------");
      }
    } catch (Exception ex) {
       logger.error("Exception printing parts",ex);
    }
    logger.trace( "Dumping parameters ..." );
    String parameterName, parameterValue;
    try {
      for ( Enumeration parameters = request.getParameterNames();
          parameters.hasMoreElements(); ) {
        parameterName = (String) parameters.nextElement();
        parameterValue = request.getParameter( parameterName );
        logger.trace( parameterName + " = " +  parameterValue );
      }
    } catch (Exception exc) {
       logger.error("Exception printing params",exc);
    }



     try {
       pid = Parameter.get(request,"pid");
       mypid = Integer.valueOf(pid);
       String type = Parameter.get(request,"upload_type");
       type = type.toLowerCase();
//Thread.sleep(5000);

       // make sure the proposal being requested is valid for this user/session
       Boolean sess_pid = (Boolean)session.getAttribute((mypid.toString()));
       logger.info("uid=" + uid + ": " + sess_pid + " for " + mypid.toString() + " type=" + type);
       if (sess_pid == null ) {
         msg = "Unauthorized proposal access";
         logger.error("uid=" + uid + "): Unauthorized access pid=" + pid );
       }
       else {
         cpsmsg =processPart(request,session,mypid.intValue(),type);
         if (cpsmsg.msg2.length() > 2) msg += cpsmsg.msg2;
       }

    } catch (Exception exc) {
       logger.error(exc);
    }
    session.setAttribute("upload",msg);
    session.setAttribute("uploadok",cpsmsg.msg);
     
    url  = response.encodeURL("/app/prop_prop_upload.jsp?id=" + pid);
    dispatcher = getServletContext().getRequestDispatcher(url);
    dispatcher.forward(request, response);

  }

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

  private CPSMsg processPart(HttpServletRequest request,HttpSession session, int pid, String type)
  {
    Boolean isBPP=false;
    CPSMsg cpsmsg  = new CPSMsg();
    cpsmsg.retval = true;
    try {
      CPSUtils cpsutil = new CPSUtils();
      IRestClient api = cpsutil.getAPI(cpsProperties,session);

      Part filePart = request.getPart("uploadFile");
      String fname = getFileName(filePart);
      logger.info("Reading " + fname + " size=" + filePart.getSize());
      if (filePart.getSize() > (maxFileSize * 1000000L)) {
         cpsmsg.retval=false;
         cpsmsg.msg="File exceeds maximum allowed size of " + maxFileSize + "Mb";
      } 
      else if (fname != null && !fname.equals("") ) {
        InputStream is = filePart.getInputStream();
        byte[] blob = ByteStreams.toByteArray(is);
        is.close();

        is = filePart.getInputStream();
        // Keep this check for now --- we had instance of invalid pdf for python reader but valid w/Okular
        Tika tika = new Tika();
        String contentType = tika.detect(is);
        is.close();
        // Trying page check
        if (contentType.indexOf("pdf") >= 0)
        {
/*
          try {
            is = filePart.getInputStream();
            PDDocument doc = PDDocument.load(is);
            Integer numPages= doc.getNumberOfPages();
            logger.info(fname +  " Page Count=" + numPages);
            is.close();

            Integer allowedPages=maxSJ;
            if (type.equals("sj")) {
              isBPP = Parameter.getBoolean(request,"isBPP");
              if (isBPP == null || isBPP) allowedPages = maxSJBPP;
            } else if (type.equals("cv")) {
              allowedPages = maxCV;
            }
            if (numPages > allowedPages) {
              cpsmsg.msg2 += uiLbl.get(type) + ": Page Count exceeds allowed number of " + allowedPages +  " pages. <br>";
            }
            if (doc != null) doc.close();
          } catch (Exception ex) {
            logger.error("PDFBox Invalid file " + fname, ex);
          }
*/
          logger.info("Saving " + fname + " size=" + blob.length  + " filesize=" + filePart.getSize());
          // proposalUploadArtifact requires type cv instead of team.
          String dbType = type.equals("team") ? "cv" : type;
          api.proposalUploadArtifact(pid, dbType, blob, fname);
          cpsmsg.msg += uiLbl.get(type) + ": Successfully uploaded " + fname  + " with a size of " + blob.length + " bytes<br>";
          logger.info(cpsmsg.msg);
          api.proposalUpdateLastSaved(pid);
          //Thread.sleep(21000);
       
        } else {
            logger.info("TIKA: " + contentType);
            cpsmsg.msg2 += uiLbl.get(type) + ": Invalid file type for " + fname + ".  Input file must be PDF.<br>";
        }
      } 
    } catch (Exception exc) {
      logger.error("Upload failed",exc);
      cpsmsg.msg2 += uiLbl.get(type) + ": Error occurred saving file.<br>";
      cpsmsg.retval = false;
    }
    return cpsmsg;
  }

  /**
   * Private variables
   */
    private static final long serialVersionUID = 1;
    private Properties cpsProperties;
    private static Logger logger = Logger.getLogger(PropUpload.class);
    HashMap<String,String> uiLbl;
    private Integer maxFileSize;
    private Integer maxSJ;
    private Integer maxSJBPP;
    private Integer maxTeam;




  /**
   * Private methods
   */

  /****************************************************************************/


}

/******************************************************************************/
