package edu.harvard.asc.cps.xo;
/************************************************************************/
/*     Copyrights:                                                      */
/*                                                                      */
/*     Copyright (c) 2017,2021 Smithsonian Astrophysical Observatory    */
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


import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.text.Normalizer;
import edu.harvard.cda.proposal.xo.YesNoPreferred;
import edu.harvard.cda.proposal.xo.YesNo;
import edu.harvard.cda.jutil.base.StringUtil;



/******************************************************************************/
/**
 * Provide operations to access, test and initialize request and
 * session parameters (variables).
 */

public class Parameter extends Object implements CPSConstants
{


  /****************************************************************************/
  /**
   * Return the value associated with a parameter.  If the value is in
   * the current request, return that value (and persist that value),
   * otherwise return the persisted value.
   *
   * @param request The browser generated request object.
   * @param name The parameter identifier.
   *
   * @return A string containg the current value associated with
   * parameter <i>name</i>.
   */

  public static String get(  HttpServletRequest request, String name )
  {
    // First try the request message value.
    String result = request.getParameter( name );
    result = Parameter.stripInput(result,false);
    if (result != null) 
       if (result.equals("")) result=null;
    if (result != null) {
       result = result.trim();
    }

    return result;
  }
  /****************************************************************************/
  /**
   * Return the value associated with a parameter.  If the value is in
   * the current request, return that value (and persist that value),
   * otherwise return the persisted value.
   *
   * @param request The browser generated request object.
   * @param name The parameter identifier.
   *
   * @return  string for current value associated with * parameter <i>name</i>.
   * @exception Exception if unexpected error occurs
   */
  public static String getEncoded(  HttpServletRequest request, String name )
      throws Exception
  {
    // First try the request message value.
    String result = request.getParameter( name );
    if (StringUtil.isISO_8859_1(result)) {
      result = Parameter.stripInput(result,true);
      if (result != null) 
         if (result.equals("")) result=null;
      if (result != null) {
         result = result.trim();
      }
    } else {
       throw new Exception("Invalid characters found in " +  name);
    }

    return result;
  }

  public static Integer getInteger(  HttpServletRequest request, String name )
        throws Exception
  {
    Integer ival = null;
    if (Parameter.has(request,name)) {
      String tstr = Parameter.get(request,name);
      if (tstr != null  && !tstr.equals(""))
        ival = Integer.valueOf(Parameter.get(request,name));
    }
    return ival;
  }
  public static Double getDouble(  HttpServletRequest request, String name )
        throws Exception
  {
    Double dval = null;
    if (Parameter.has(request,name)) {
      String tstr = Parameter.get(request,name);
      if (tstr != null  && !tstr.equals(""))
        dval = Double.valueOf(Parameter.get(request,name));
    }
    return dval;
  }
  public static Float getFloat(  HttpServletRequest request, String name )
        throws Exception
  {
    Float fval = null;
    if (Parameter.has(request,name)) {
      String tstr = Parameter.get(request,name);
      if (tstr != null  && !tstr.equals(""))
        fval = Float.valueOf(Parameter.get(request,name));
    }
    return fval;
  }
  public static Character getChar(  HttpServletRequest request, String name )
        throws Exception
  {
    Character cval = null;
    if (Parameter.has(request,name)) {
      String tstr = Parameter.get(request,name);
      if (tstr != null)
        cval = Character.valueOf(tstr.charAt(0));
    }
    return cval;

  }
  public static YesNoPreferred getYNP(HttpServletRequest request, String name )
        throws Exception
  {
    YesNoPreferred cval = YesNoPreferred.NO;
    if (Parameter.has(request,name)) {
      String tstr = Parameter.get(request,name);
      if (tstr != null) {
        if (tstr.indexOf('P') >= 0)
          cval = YesNoPreferred.PREFERRED;
        else if (tstr.indexOf('Y') >= 0)
          cval = YesNoPreferred.YES;
      }
    }
    return cval;
  }
public static YesNo getYN(HttpServletRequest request, String name )
  throws Exception
{
  YesNo cval = YesNo.NO;
    if (Parameter.has(request,name)) {
      String tstr = Parameter.get(request,name);
      if (tstr != null && (tstr.indexOf('Y') >= 0)) {
        cval = YesNo.YES;
      }
    }
return cval;
}
  public static Boolean getBoolean(  HttpServletRequest request, String name ,boolean defaultVal)
        throws Exception
  {
    Boolean bval = null;
    bval = getBoolean(request,name);
    if (bval == null) 
       bval = Boolean.valueOf(defaultVal);

    return bval;

  }

  public static Boolean getBoolean(  HttpServletRequest request, String name )
        throws Exception
  {
    Boolean bval = null;
    if (Parameter.has(request,name)) {
      String tstr = Parameter.get(request,name);
      if (tstr != null) {
        if ( tstr.equals("Y"))
          bval = Boolean.valueOf(true);
        else if (tstr.equals("N"))
          bval = Boolean.valueOf(false);
      }
    }
    return bval;

  }

  /****************************************************************************/
  /**
   * Determine if the request object includes the named parameter.
   * Return true if it does, false otherwise.
   *
   * @param request The browser generated request object.
   * @param name The parameter name.
   *
   * @return true iff the named parameter is specified in the request
   * object.
   */

  public static boolean has( HttpServletRequest request, String name )
  {
    String value = request.getParameter( name );
    return value != null;
  }

  public static String stripInput( String value,Boolean allowEncoded)
  {

    String retValue = null;
   try {
    if (value != null) {
      int slen = value.length();
      // NO FIELD in CPS is longer than this!!!
      if (slen > 3000) { slen=3000; }

      retValue = value.substring(0,slen);
      retValue = StringEscapeUtils.unescapeHtml4(retValue);

      // Strip double quotes
      retValue = retValue.replace("\"", "");

      if (!allowEncoded)  {
        retValue = retValue.replaceAll("[^\\x00-\\x7F]", "?");
      }
      if (!value.equals(retValue)) {
        logger.info("STRIP in: " + value +  "\nout:" +  retValue );
      }
     } 
    }  catch (Exception e) {
       logger.error(e);
    }

    return retValue;
  }




  /****************************************************************************/
  private static Logger logger = Logger.getLogger(Parameter.class);

}

/******************************************************************************/
