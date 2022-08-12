/*
      Copyrights:
 
      Copyright (c) 1998 Smithsonian Astrophysical Observatory
 
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

/******************************************************************************/

package gov.sao.asc.obsed;

/******************************************************************************/

import java.util.Vector;

/******************************************************************************/

public class Constants
{
  public static final String PREFERRED = "PREFERRED";
  public static final String YES = "YES";
  public static final String NO = "NO";

  public static final String NONE = "<NONE>";
  public static final String CUSTOM = "CUSTOM";
  public static final String OTHER = "OTHER";
  public static final String NEW = "NEW";
  public static final String NULL = "NULL";
  public static final String DEFAULT = "DEFAULT";

  // special case database variables
  public static final String SPADDITIONAL = "spadditional";


  public static Vector<String> booleans;

  public static final int OBJECT = 0;
  public static final int INTEGER = 1;
  public static final int LONG = 2;
  public static final int STRING = 3;
  public static final int BOOLEAN = 4;
  public static final int FLOAT = 5;
  public static final int DOUBLE = 6;
  public static final int CHOICE = 7;
  public static final int DATE = 8;

  // GUI constants.  These reflect the GUI types supported.
  public static final int UNKNOWN = 0;
  public static final int COMBOBOX = 1;
  public static final int CHECKBOX = 2;
  public static final int SEPARATOR = 3;
  public static final int TEXTFIELD = 4;
  public static final int TEXTAREA = 5;
  public static final int LABEL = 6;
  public static final int TIMESTAMPBUTTON = 7;
  public static final int TRIPLEBOX = 8;
  public static final int KEYCOMBOBOX = 9;
  public static final int OBSIDCOMBOBOX = 10;
  public static final int OBSIDBUTTON = 11;
  public static final int SEQNBRBUTTON = 12;
  public static final int PREFERREDBOX = 13;

  // Values used in specifing seeding value for new records.
  public static final int NEWDEFAULT = 0;
  public static final int NEWCLONE = 1;
  public static final int NEWNULL = 2;


  // Value to use for the border thickness for highlighting GUI
  // components that have been changed in table views.
  public static final int BORDER_THICKNESS = 2;

  /****************************************************************************/

  static
  {
    booleans = new Vector<String>();

    booleans.addElement(NULL);
    booleans.addElement(YES);
    booleans.addElement(NO);
  }

  /****************************************************************************/

  public static Vector<String> getBooleans()
  {
    return(booleans);
  }

  /****************************************************************************/

}

/******************************************************************************/
