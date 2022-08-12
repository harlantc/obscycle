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

package gov.sao.asc.obsed.database;

/******************************************************************************/
/**
 * <P>The AlreadyExistsException class provides information on a
 * failed attempt to create a new record.
 *
 * <P>Each RecordDoesNotExistException provides several kinds of
 * information:
 * <UL>
 *   <LI> a string describing the error.  This is used as the Java Exception
 *       message, and is available via the getMessage() method
 *   <LI> A server name string
 *   <LI> A user name string
 * </UL>
 */

public class AlreadyExistsException extends Exception
{
  /**
   * The table for which the exception occurred.
   */
  private String tableName;

  /**
   * The key value associated with the record index.
   */
  private Object keyValue;

  /****************************************************************************/
  /**
   * Construct a fully-specified CouldNotConnectException  
   *
   * @param reason a description of the exception 
   * @param url the database URL
   * @param user the database username
   */

  public AlreadyExistsException( String reason, String tableName,
				 Object keyValue )
  {
    super( reason );
    this.tableName = tableName;
    this.keyValue = keyValue;
  }

  /****************************************************************************/
  /**
   * Get the key value at which the exception occurred.
   *
   * @return the key value.
   */

  public Object getKeyValue()
  {
    return keyValue;
  }

  /****************************************************************************/
  /**
   * Get the table for which the insertion failed.
   *
   * @return the table name.
   */

  public String getTableName()
  {
    return tableName;
  }

  /****************************************************************************/

}

/******************************************************************************/
