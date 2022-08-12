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

import edu.harvard.cda.cxclogin.filter.CASAFSessionScopeAttribute;
import edu.harvard.cda.proposal.restclient.IRestClient;
import edu.harvard.cda.proposal.restclient.RestClient;


import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;
import javax.servlet.http.HttpSession;



/******************************************************************************/
/**
 */

public class CPSUtils 
{
   public CPSUtils() {
   }

   public IRestClient getAPI(Properties cpsProperties,HttpSession session) 
      throws Exception 
   {
      StringBuffer sbuf = new StringBuffer("");
      String cpsapi = "";
      FileReader fileR = null;
      BufferedReader cdoFileBR = null;
      try {
        String cpsfile = (cpsProperties.getProperty("cps.appdir")).trim() +  ".htcps";
        logger.trace(cpsfile);
        fileR = new FileReader(cpsfile);
        cdoFileBR = new BufferedReader(fileR);
        String inputLine;
        while( (inputLine = cdoFileBR.readLine()) != null) {
            sbuf.append(inputLine);
       }
        cdoFileBR.close();
        fileR.close();
      } catch (Exception exc) {
         logger.error(exc);
         try {
           if (cdoFileBR != null) 
            cdoFileBR.close();
           if (fileR != null) 
            fileR.close();
         } catch (Exception e) {
           logger.error(e);
         }
      }
      cpsapi = (sbuf.toString()).trim();
      String dalURL = cpsProperties.getProperty("cps.dal.url");
      String cpsuser = cpsProperties.getProperty("cps.app");


      IRestClient api = new RestClient(dalURL,cpsuser, cpsapi,
        CASAFSessionScopeAttribute.SERVICE_TICKET.get(session, String.class)
        );

      return api;
  }



  /**
   * Private variables
   */
  private static Logger logger = Logger.getLogger(CPSUtils.class);

}
