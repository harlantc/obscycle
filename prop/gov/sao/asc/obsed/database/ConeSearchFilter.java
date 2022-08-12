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

package gov.sao.asc.obsed.database;

/******************************************************************************/

import java.util.Vector;

/******************************************************************************/
/**
 * The <code>Filter</code> object encapsulates all the conditions for
 * a single WHERE clause.
 *
 * @author Paul Matthew Reilly
 *
 * @version %I%, %G%
 */

public class ConeSearchFilter extends Filter
{
  String dec;
  String ra;
  String radius;
    
  /****************************************************************************/
  
  public String getDec()
  {
    return( dec );
  }

  /****************************************************************************/
  
  public String getRA()
  {
    return( ra );
  }

  /****************************************************************************/

  public boolean isBoth()
  {
    boolean result = false;

    if ( isFiltered() && isConeSearch() )
    {
      result = true;
    }

    return( result );
  }

  /****************************************************************************/

  public boolean isConeSearch()
  {
    boolean result = false;

    if ( ( ra != null ) && ( ! ra.equals("") ) )
    {
      result = true;
    }

    return( result );
  }

  /****************************************************************************/

  public boolean isFiltered()
  {
    boolean result = false;

    if ( conditions.size() > 0 )
    {
      result = true;
    }

    return( result );
  }

  /****************************************************************************/
  
  public String getRadius()
  {
    return( radius );
  }

  /****************************************************************************/

  public void setDec(String dec)
  {
    if ( dec.indexOf(".") < 0 )
    {
      this.dec = dec + ".0";
    }
    else
    {
      this.dec = dec;
    }
  }

  /****************************************************************************/

  public void setRA(String ra)
  {
    if ( ra.indexOf(".") < 0 )
    {
      this.ra = ra + ".0";
    }
    else
    {
      this.ra = ra;
    }
  }

  /****************************************************************************/

  public void setRadius(String radius)
  {
    if ( radius.indexOf(".") < 0 )
    {
      this.radius = radius + ".0";
    }
    else
    {
      this.radius = radius;
    }
  }

  /****************************************************************************/

  public String toSQL()
  {
    StringBuffer result = new StringBuffer();

    result.append( super.toSQL() );

    if ( ra != null )
    {
      if ( isFiltered() )
      {
        result.append( " AND " );
      }
      else
      {
        result.append( " WHERE " );
      }

      result.append( "cos(radians(" );
      result.append( radius );
      result.append( " / 60)) <= sin(radians(target.dec)) * sin(radians(" );
      result.append( dec );
      result.append( ")) + cos(radians(target.dec)) * cos(radians(" );
      result.append( dec );
      result.append( ")) * cos(radians(target.ra - " );
      result.append( ra );
      result.append( "))" );
    }
    
    return( result.toString() );
  }

  /****************************************************************************/
  
  public String toString()
  {
    StringBuffer result = new StringBuffer();

    result.append( super.toString() );

    result.append("RA: " + ra + "\n");
    result.append("Dec: " + dec + "\n");
    result.append("Radius: " + radius + "\n");

    return( result.toString() );
  }

  /****************************************************************************/

}

/******************************************************************************/
