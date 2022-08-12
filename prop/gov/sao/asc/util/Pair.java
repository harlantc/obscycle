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

public class Pair
  implements Comparable
{
  Object key;
  Object value;

  /****************************************************************************/

  public Pair( Object key, Object value )
  {
    this.key = key;
    this.value = value;
  }

  /****************************************************************************/

  public int compareTo( Object object ) 
  {
    return( compareTo( (Pair) object ) );
  }

  /****************************************************************************/

  public int compareTo( Pair pair ) 
  {
    return( ((Comparable) value).compareTo( pair.getValue() ) );
  }

  /****************************************************************************/

  public boolean equals( Object object )
  {
    boolean result = false;

    if ( object instanceof Pair )
    {
      Pair pair = (Pair) object;

      if ( pair.getKey().equals( key ) && pair.getValue().equals( value ) )
      {
        result = true;
      }
    }

    return( result );
  }

  /****************************************************************************/

  public Object getKey()
  {
    return(key);
  }

  /****************************************************************************/

  public Object getValue()
  {
    return(value);
  }

  /****************************************************************************/

  public void setKey(Object key)
  {
    this.key = key;
  }

  /****************************************************************************/

  public void setValue(Object value)
  {
    this.value = value;
  }

  /****************************************************************************/

  public String toString()
  {
    return( value.toString() );
  }

  /****************************************************************************/

}

/******************************************************************************/