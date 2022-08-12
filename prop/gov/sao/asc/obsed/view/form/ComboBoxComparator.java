/*
  Copyrights:
 
  Copyright (c) 1998 Smithsonian Astrophysical Observatory
 
  Permission to use, copy, modify, distribute, and sell this software
  and its documentation for any purpose is hereby granted without fee,
  provided that the above copyright notice appear in all copies and
  that both that copyright notice and this permission notice appear in
  supporting docu- mentation, and that the name of the Smithsonian
  Astro- physical Observatory not be used in advertising or publicity
  pertaining to distribution of the software without specific, written
  prior permission.  The Smithsonian Astrophysical Observatory makes
  no representations about the suitability of this software for any
  purpose.  It is provided "as is" without express or implied
  warranty.  THE SMITHSONIAN INSTITUTION AND THE SMITHSONIAN ASTRO-
  PHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES WITH REGARD TO THIS
  SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANT- ABILITY AND
  FITNESS, IN NO EVENT SHALL THE SMITHSONIAN INSTITUTION AND/OR THE
  SMITHSONIAN ASTROPHYSICAL OBSERVATORY BE LIABLE FOR ANY SPECIAL,
  INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER
  RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
  CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
  CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

/******************************************************************************/

package gov.sao.asc.obsed.view.form;

/******************************************************************************/

import java.util.Comparator;

/******************************************************************************/
/**
 * A ComboBoxComparator enables tailored sorting of the contents of a
 * vector.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class ComboBoxComparator extends Object
  implements Comparator
{

  /****************************************************************************/
  /**
   * Constructor
   */

  ComboBoxComparator()
  {
  }

  /****************************************************************************/
  /**
   * Compare two objects, returning -1, 0, or +1 as o1 is less than,
   * equal to, or greater than o2.
   */

  public int compare( Object o1, Object o2 )
  {
    // Predispose the result to o1 being less than o2.
    int result = -1;

    // Handle nulls.
    if ( o1 == null )
    {
      result = -1;
    } 
    else if ( o2 == null )
    {
      result = 1;
    }
    else if ( o1.getClass() == o2.getClass() )
    {
      int i1, i2;
      if ( o1 instanceof Integer )
      {
	i1 = ((Integer) o1).intValue();
	i2 = ((Integer) o2).intValue();
	if ( i1 == i2 )
	{
	  result = 0;
	}
	else if ( i1 < i2 )
	{
	  result = -1;
	}
	else
	{
	  result = +1;
	}
      }
      else if ( o1 instanceof String )
      {
	try
	{
	  // Determine if i1 is a number.
	  i1 = Integer.parseInt( (String) o1 );

	  // It is.  Determine if i2 is a number also.
	  try
	  {
	    i2 = Integer.parseInt( (String) o2 );

	    // It is.  
	    if ( i1 == i2 )
	    {
	      result = 0;
	    }
	    else if ( i1 < i2 )
	    {
	      result = -1;
	    }
	    else
	    {
	      result = +1;
	    }
	  }
	  catch ( NumberFormatException exc3 )
	  {
	    // i1 is a number but i2 is not.
	    result = +1;
	  }
	}
	catch ( NumberFormatException exc1 )
	{
	  try
	  {
	    // Determine if i2 is a number.
	    i2 = Integer.parseInt( (String) o2 );

	    // It is and i1 is not.
	    result = -1;
	  }
	  catch ( NumberFormatException exc2 )
	  {
	    result = ((String) o1).compareTo( (String) o2 );
	  }
	}
      }
    }
    else if ( o1 instanceof String )
    {
      result = -1;
    }
    else
    {
      result = +1;
    }
    return result;
  }

  /****************************************************************************/
  /**
   * Handle a new selection in a combo box.
   */

  public boolean equals( Object obj )
  {
    boolean result = false;

    if ( obj instanceof ComboBoxComparator )
    {
      result = true;
    }

    return result;
  }
}

/******************************************************************************/
