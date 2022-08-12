package edu.harvard.asc.cps.xo;
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

//CPSConstants class
//This class contains all the constants needed in the CPS application

public interface CPSConstants {

  public static String PROPERTYFILE = "/prop/cps/.htproperties";
 
  public static Double  EMPTY_VALUE = Double.valueOf(-9999.);
  public static Integer EMPTY_INT   = Integer.valueOf(-9999);


  public static String SAVE_OK = "SaveOK";
  public static String COVER_PAGE = "Cover Page";
  public static String PI_PAGE = "P.I.";
  public static String COI_PAGE = "Co-Investigators";
  public static String JOINT_PAGE = "Joint Time";
  public static String DDT_PAGE = "DDT";
  public static String FILES_PAGE = "Supporting Files";
  public static String TARGET_PAGE = "Targets";
  public static String POINTING_PAGE = "Target:Pointing";
  public static String TIME_PAGE = "Target:Observing Time";
  public static String INST_PAGE = "Target:Instrument";
  public static String ACISREQ_PAGE = "Target:ACIS(req)";
  public static String ACISOPT_PAGE = "Target:ACIS(opt)";
  public static String CONSTRAINT_PAGE = "Target:Constraints";
  public static String TOO_PAGE = "Target:TOO Details";
  public static String RMK_PAGE = "Target:Remarks";
  public static String ERROR_TYPE = "Error";
  public static String WARN_TYPE = "Warning";
  public static String NOTE_TYPE = "Note";
  public static String SUCCESS_TYPE = "Success";
  public static String SAVE_FAILED = "Data was NOT saved.";
  public static String DELIM = "_____";

  public static String NONTRANSIENT = "NON-TRANSIENT";


  public static String XMLSTR = "<?xml version='1.0' encoding='utf-8'?>";
  public static String CHARSET = "UTF-8";

}

