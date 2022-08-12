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
 * <P>The TargetInsertionException class provides information on a
 * target table insertion error.
 *
 * <P>Each TargetTableInsertionException provides several kinds of
 * information:
 * <UL>
 *   <LI> a string describing the error.  This is used as the Java Exception
 *       message, and is available via the getMessage() method
 *   <LI> A server name string
 *   <LI> A user name string
 * </UL>
 */

public class TargetInsertionException extends Exception
{
  private int returnCode;

  /****************************************************************************/
  /**
   * Construct a fully-specified CouldNotConnectException  
   *
   * @param reason a description of the exception 
   * @param url the database URL
   * @param user the database username
   */

  public TargetInsertionException( String reason, int returnCode )
  {
    super( reason );
    this.returnCode = returnCode;
  }

  /****************************************************************************/
  /**
   * Get the stored procedure return code.
   *
   * @return the return code.
   */

  public int getReturnCode()
  {
    return returnCode;
  }

  /****************************************************************************/

}

/******************************************************************************/
