package edu.harvard.asc.cps.xo;
/************************************************************************/
/*     Copyrights:                                                      */
/*                                                                      */
/*     Copyright (c) 2019-2020 Smithsonian Astrophysical Observatory    */
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
import ascds.RunCommand;
import org.apache.log4j.Logger;
import java.lang.Runtime;
import java.lang.Process;
import java.util.Properties;
import java.util.ArrayList;

public class CPSPdf 
{
  private static Logger logger = Logger.getLogger(CPSProposal.class);

  /**
   * Constructor
   */
   public CPSPdf() 
   {
   }

  /**
   *  @param propno proposal number
   * @param outdir output directory
   * @param cpsProperties properties
   * @param isDDT  true if this is DDT submission
   * @param isAnon true generates anonymized PDF
   *
   */
  public void genPDF(String propno, String outdir, Properties cpsProperties,
                     boolean isDDT, boolean isAnon)
  {
   String envStr;
    try {
       ArrayList<String> envVarList = new ArrayList<String>();
       String pu=cpsProperties.getProperty("cps.pc");
       String server=cpsProperties.getProperty("cps.pcserv");
       String sybpy=cpsProperties.getProperty("sybase.path");
       String syb=cpsProperties.getProperty("sybase");
       String relpath=cpsProperties.getProperty("ascds.release");
       String datapath=cpsProperties.getProperty("cps.data.path");
       envStr = "HOME=" + datapath ;
       envVarList.add(envStr);
       envStr = "GENPDF=" + datapath + "/.htpdf"; 
       envVarList.add(envStr); 
       envStr = "SYBPYTHON=" + sybpy; 
       envVarList.add(envStr); 
       envStr = "SYBASE=" + syb; 
       envVarList.add(envStr); 
       envStr = "ASCDS_INSTALL=" + relpath ; 
       envVarList.add(envStr);
       envStr = "ASCDS_BIN=" + relpath + "/bin";
       envVarList.add(envStr);
       envStr= "DB_LOCAL_SQLSRV=" + server;
       envVarList.add(envStr);
       envStr= "DB_REMOTE_SQLSRV=" + server;
       envVarList.add(envStr);
       envStr= "DB_PROP_SQLSRV=" + server;
       envVarList.add(envStr);
       envStr= "DB_OCAT_SQLSRV=" + server;
       envVarList.add(envStr);
       envStr = "LANG=en_US.UTF-8";
       envVarList.add(envStr);
       envStr = "PATH=" + System.getenv("PATH");
       envStr += ":" + relpath + "/bin";
       envVarList.add(envStr);
       envStr = "ASCDS_PROP_LOGS=" + cpsProperties.getProperty("cps.tmp.path");
       envVarList.add(envStr);
       envStr = "ASCDS_VERSION=CPS";
       envVarList.add(envStr);
       envStr = "OBSCYCLE_DATA_PATH=" + System.getenv("OBSCYCLE_DATA_PATH");
       logger.trace(envStr); 
       envVarList.add(envStr);

       String webbin=cpsProperties.getProperty("web.bin.directory");
 
       // 1=dbuser 2=server 3=proposal 4=output_directory
       String cmd = webbin + "/prop_genPDF.tcsh ";
       cmd += " " +  pu + " " + server + " " + propno + " " + outdir ; 
       if (isDDT) {
          cmd += " b";
          logger.info(cmd); 
          String[] arr = new String[envVarList.size()];
          arr = envVarList.toArray(arr);
          Process p = Runtime.getRuntime().exec(cmd,arr);
       }
       else {
         if (isAnon) {
             cmd += " h";
         }
         logger.info(cmd); 
         RunCommand runtime = new RunCommand(cmd,envVarList,null);
         logger.info(runtime.getOutMsg());
         if (runtime.getErrMsg() != null && !runtime.getErrMsg().equals(""))
           logger.error(runtime.getErrMsg());
       }

    } catch (Exception exc) {
      logger.error(exc);
    }
  }
}

