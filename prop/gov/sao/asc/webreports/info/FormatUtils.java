package info;
/*
  Copyrights:
 
  Copyright (c) 2000-2016 Smithsonian Astrophysical Observatory
 
  Permission to use, copy, modify, distribute, and  sell  this
  software  and  its  documentation  for any purpose is hereby
  granted without  fee,  provided  that  the  above  copyright
  notice  appear  in  all  copies and that both that copyright
  notice and this permission notice appear in supporting docu-
  mentation,  and  that  the  name  of the  Smithsonian Astro-
  physical Observatory not be used in advertising or publicity
  pertaining to distribution of the software without specific,
  written  prior  permission.   The Smithsonian  Astrophysical
  Observatory makes no representations about  the  suitability
  of  this  software for any purpose.  It is provided  "as is"
  without express or implied warranty.
  THE  SMITHSONIAN  INSTITUTION  AND  THE  SMITHSONIAN  ASTRO-
  PHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES  WITH REGARD TO
  THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANT-
  ABILITY AND FITNESS,  IN  NO  EVENT  SHALL  THE  SMITHSONIAN
  INSTITUTION AND/OR THE SMITHSONIAN ASTROPHYSICAL OBSERVATORY
  BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES
  OR ANY DAMAGES  WHATSOEVER  RESULTING FROM LOSS OF USE, DATA
  OR PROFITS,  WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
  OTHER TORTIOUS ACTION,  ARISING OUT OF OR IN CONNECTION WITH
  THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

import java.lang.String;
import org.apache.commons.lang3.text.*;
import ascds.LogMessage;

/** 
 * The FormatUtils class  contains utilities to format strings.
 */

public class FormatUtils {

  private Integer lineLen;

  /** 
   * FormatUtils constructor 
   * default line length for wordwrap of 79
   */
  public FormatUtils() {
     lineLen = new Integer(79);
  }

  /** 
   * FormatUtils constructor
   * @param maxLength   line length for wordwrap
   */
  public FormatUtils(int maxLength) {
     lineLen = new Integer(maxLength);
  }
 
  public String getWrapped(String inputStr)
  {
    String returnStr = new String("");
    String str;
    String[] strArray;
    String delim = "\n";

    if (inputStr != null) {
      str =inputStr.replaceAll("\r\n","\n");
      str =str.replaceAll("\r","\n");
      returnStr = this.wordWrap(str,delim);
    }
    return returnStr;
  }



  /**
   * wordWrap -  split string into array with no line exceeding the 
   *             maximum line length.
   *
   * @param inputStr   input String
   * @param delim end-of-line delimiter
   * @return String  string split into maximum line length using specified delimiter
   */
  public String wordWrap(String inputStr,String delim) {
    String returnStr = new String("");
    String[] strArray;
    String tmpStr;

    strArray = inputStr.split(delim);

    for (int ss =0; ss < strArray.length; ss++) {
      if (strArray[ss].length() > lineLen.intValue()) {
          returnStr += WordUtils.wrap(strArray[ss],lineLen.intValue(),delim,false);
          returnStr += delim;
      }
      else {
         returnStr += strArray[ss] + delim;
     }
   } 
         
    return returnStr;
  }



}
