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

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.*;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;
import org.apache.commons.lang3.StringEscapeUtils;
import edu.harvard.cda.proposal.xo.VTargetCPS;
import edu.harvard.cda.coords.CoordSystem;
import edu.harvard.cda.coords.Coordinate;


public class CPSTargetSummary
{
   public Boolean isEdit;
   public Boolean isDDTProp;
   public Boolean isMulti;
   public Boolean isValidCoords;
   public VTargetCPS tgt;
   public String raString = "";
   public String decString = "";
   public Double  ra;
   public Double  dec;
   private static Logger logger = Logger.getLogger(CPSTargetSummary.class);


   public CPSTargetSummary(VTargetCPS tgt,Boolean isEdit,Boolean isDDTProp,Boolean isMulti)
   {
        this.tgt = tgt;
        this.isEdit = isEdit;
        this.isMulti = isMulti;
        this.isDDTProp = isDDTProp;
        setCoords(CPS.getDoubleCoord(tgt.ra,0),CPS.getDoubleCoord(tgt.dec,0));

   }

   public String getGridEntry()
   {
     String str = "";
     String tname = "";
     tname = CPS.getString(tgt.targname);
     if (tname.length() <2 && !CPS.getString(tgt.ss_object).equalsIgnoreCase("NONE")) 
       tname = CPS.getString(tgt.ss_object);
     str = "{ \"id\":" + tgt.targid + ",\n\"data\":[";
     str += "\"" + tgt.targ_num + "\",";
     str += "\"" + StringEscapeUtils.escapeHtml4(tname) + "\",";
     str += "\"" + StringEscapeUtils.escapeHtml4(CPS.getString(raString)) + "\",";
     str += "\"" + StringEscapeUtils.escapeHtml4(CPS.getString(decString)) + "\",";
     str += "\"" + StringEscapeUtils.escapeHtml4(tgt.instrument_name) + "\",";
     str += "\"" + StringEscapeUtils.escapeHtml4(tgt.grating_name) + "\",";
     str += "\"" + CPS.getDouble(tgt.prop_exposure_time,1) + "\",";
     str += "\"" + CPS.getDouble(tgt.est_cnt_rate,1) + "\"";
     str += "] }\n";

     return str;
   }



   public void setCoords(String raStr, String decStr)
    {
       Coordinate coords;

       isValidCoords = true;
       raString = raStr;
       decString = decStr;

       if (raStr  != null && raStr.length() > 0 &&
           decStr != null && decStr.length() > 0 ) {
         try {
           coords = new Coordinate(raStr,decStr,"J2000");
           ra = Double.valueOf(coords.getLon());
           dec = Double.valueOf(coords.getLat());
           raString  = coords.getSexagesimalLon();
           decString = coords.getSexagesimalLat();
         }
         catch (RuntimeException e) {
           logger.error(e);
           isValidCoords=false;
         }
         catch (Exception exc) {
           isValidCoords = false;
         }
       }
       else {
         ra= 0.0;
         dec = 0.0;
         raString = "";
         decString = "";
       }
    }
}

