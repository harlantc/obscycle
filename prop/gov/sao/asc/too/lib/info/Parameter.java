package info;
/************************************************************************/
/*     Copyrights:                                                      */
/*                                                                      */
/*     Copyright (c) 2019 Smithsonian Astrophysical Observatory         */
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

import java.nio.charset.StandardCharsets;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import ascds.LogMessage;


/******************************************************************************/
/**
 * Provide operations to access, test and initialize request and
 * session parameters (variables).
 */

public class Parameter extends Object 
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
   * @return A string containg the current value associated with
   * parameter <i>name</i>.
   */

  public static String getEncoded(  HttpServletRequest request, String name )
  {
    // First try the request message value.
    String result = request.getParameter( name );
    result = Parameter.stripInput(result,true);
    if (result != null) 
       if (result.equals("")) result=null;
    if (result != null) {
       result = result.trim();
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
      else 
        dval = null;
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
        // No TOO/DDT input field should be greater than 3000 char
        if (slen > 3000) { slen=3000; }
  
        retValue = value.substring(0,slen);
        retValue = StringEscapeUtils.unescapeHtml4(retValue);

        if (!allowEncoded)  {
          retValue = retValue.replaceAll("[^\\x00-\\x7F]", "?");

        }
        if (!value.equals(retValue)) {
          LogMessage.println("TOO Trigger strip input: " + value +  "  out:" +  retValue );
        }
        retValue = retValue.replaceAll("`","\\`");
        retValue = retValue.replaceAll("\\\\"," ");

      } 
    }  catch (Exception e) {
       LogMessage.println(e.getMessage());
       retValue="";
    }

    return retValue;
  }



  /****************************************************************************/

}

/******************************************************************************/
