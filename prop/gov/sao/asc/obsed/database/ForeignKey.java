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
/**
 * Encapsulates a column in another table related by a foreign key to
 * another table.
 *
 * @author Paul Matthew Reilly
 *
 * @version %I%, %G%
 */

public class ForeignKey extends DatabaseKey
{
  private DatabaseKey foreignKey;

  /****************************************************************************/

  public ForeignKey( String tableName, String columnName, 
                     String foreignKeyTableName, String foreignKeyColumnName )
  {
    super( tableName, columnName );

    foreignKey = new DatabaseKey( foreignKeyTableName, foreignKeyColumnName );
  }

  /****************************************************************************/

  public int compareTo( Object object ) 
  {
    return( compareTo( (ForeignKey) object ) );
  }

  /****************************************************************************/

  public int compareTo( ForeignKey foreignKey ) 
  {
    DatabaseKey databaseKey = 
      new DatabaseKey( foreignKey.getForeignTableName(), 
                       foreignKey.getForeignColumnName() );

    int result = super.compareTo( databaseKey );

    if ( result == 0 )
    {
      result = super.compareTo( foreignKey );
    }

    return( result );
  }

  /****************************************************************************/
  /**
   * Returns true if the two DatabaseKey's have the same table name
   * and column name.
   */

  public boolean equals( DatabaseKey databaseKey )
  {
    boolean result = false;

    if ( databaseKey instanceof ForeignKey )
    {
      ForeignKey foreignKey = (ForeignKey) databaseKey;

      new DatabaseKey( foreignKey.getForeignTableName(), 
                       foreignKey.getForeignColumnName() );

      if ( foreignKey.getTableName().equals( getTableName() ) &&
           foreignKey.getColumnName().equals( getColumnName() ) &&
           foreignKey.getForeignTableName().equals( getForeignTableName() ) &&
           foreignKey.getForeignColumnName().equals( getForeignColumnName() ) )
      {
        result = true;
      }
    }

    return( result );
  }

  /****************************************************************************/

  public String getForeignColumnName()
  {
    return( foreignKey.getColumnName() );
  }

  /****************************************************************************/

  public String getForeignTableName()
  {
    return( foreignKey.getTableName() );
  }

  /****************************************************************************/
  /**
   * Return the column name.  To be used with a JComboBox to provide a
   * label for this object.
   */

  public String toString()
  {
    StringBuffer result = new StringBuffer();

    result.append( super.toString() );
    result.append( "Foreign Key: " + foreignKey );

    return( result.toString() );
  }

  /****************************************************************************/

}

/******************************************************************************/
