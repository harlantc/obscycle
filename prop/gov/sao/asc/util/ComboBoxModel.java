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

package gov.sao.asc.util;

/******************************************************************************/

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;

/******************************************************************************/

public class ComboBoxModel extends DefaultComboBoxModel
{
  static String NOTSELECTED = "Not Selected";

  /****************************************************************************/

  public ComboBoxModel(Vector items)
  {
    super( (Vector) items.clone() );

    insertElementAt(NOTSELECTED, 0);
  }

  /****************************************************************************/

  public Object getSelectedObject()
  {
    String result;

    result = (String) super.getSelectedItem();

    if ( result.equals(NOTSELECTED) )
    {
      result = null;
    }

    return(result);
  }

  /****************************************************************************/

  public void setSelectedItem(Object item)
  {
    if (item != null)
    {
      super.setSelectedItem(item);
    }
    else
    {
      super.setSelectedItem(NOTSELECTED);
    }
  }

  /****************************************************************************/

}

/******************************************************************************/
