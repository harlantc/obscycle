package edu.harvard.asc.cps.cpsapp;
/************************************************************************/
/*     Copyrights:                                                      */
/*                                                                      */
/*     Copyright (c) 2017 Smithsonian Astrophysical Observatory         */
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

import ascds.NameResolver;
import org.apache.log4j.Logger;
import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.IOException;


/******************************************************************************/
/**
 */

public class ResolveName extends HttpServlet 
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
        service(request,response);
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
    String startPageURL = cpsProperties.getProperty("cps.start.url");
    String str="";
    String errmsg="";
    NameResolver nameResolver = new NameResolver(cpsProperties);

    // Get the session object.
    HttpSession session = request.getSession( false );
    if (session == null) {
      logger.info("ResolveName: session is null" );
      response.sendRedirect(startPageURL);
    }
    StringBuffer outputStr = new StringBuffer();
    response.setContentType("text/xml");
    PrintWriter out=response.getWriter();
    out.println(CPSConstants.XMLSTR);

    String operation = Parameter.get(request, "operation" );
    String targname  = Parameter.get( request,"targname" );
    String resolverSelector = Parameter.get( request, "resolverSelector");
    String[] resolverList = resolverSelector.split("/");
    logger.trace("ResolveName: " + targname);

    String ra="";
    String dec="";
    str = "<data>\n";
    try {
      errmsg = nameResolver.resolve(targname,resolverList);
      if (errmsg == null) {
         ra = nameResolver.getRA();
         dec = nameResolver.getDec();
         str += "<ra>" + ra + "</ra>,";
         str += "<dec>" + dec + "</dec>";
         errmsg="";
      }
    } catch (Exception exc) {
      logger.error("Yikes",exc);
      errmsg = "Unexpected result from NameResolver.";
    }

    str += "<errmsg>" + errmsg + "</errmsg>";
    str += "</data>\n";

    outputStr.append(str);
    logger.info(outputStr.toString());
    out.println(outputStr.toString());
    out.close();
  }

  /**
   * Private variables
   */
   private static final long serialVersionUID = 1;
   private Properties cpsProperties;
   private static Logger logger = Logger.getLogger(ResolveName.class);



  /**
   * Private methods
   */

  /****************************************************************************/


}

/******************************************************************************/
