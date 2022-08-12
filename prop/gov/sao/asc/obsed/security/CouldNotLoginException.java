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

package gov.sao.asc.obsed.security;

/**
 * <P>The CouldNotLoginException class provides information on a login
 * error.
 *
 * <P>Each CouldNotLoginException provides several kinds of information: 
 * <UL>
 *   <LI> a string describing the error.  This is used as the Java Exception
 *       message, and is available via the getMesage() method
 *   <LI> A "SQLstate" string which follows the XOPEN SQLstate conventions.
 *       The values of the SQLState string as described in the XOPEN SQL spec.
 *   <LI> An integer error code that is vendor specific.  Normally this will
 *	 be the actual error code returned by the underlying database.
 *   <LI> A chain to a next Exception.  This can be used to provided additional
 * 	 error information.
 * </UL>
 */

public class CouldNotLoginException extends Exception
{
  private String tag;
  private String user;

  /****************************************************************************/

  /**
   * Construct a fully-specified CouldNotLoginException  
   *
   * @param reason a description of the exception 
   * @param tag the database info unique tag
   * @param user the database username
   */

  public CouldNotLoginException( String reason, String tag, String user )
  {
    super( reason );
    this.tag = tag;
    this.user = user;
  }


  /****************************************************************************/

  /**
   * Get the unique tag.
   *
   * @return the unique tag.
   */

  public String getTag()
  {
    return tag;
  }	


  /****************************************************************************/

  /**
   * Get the user name.
   *
   * @return the user name.
   */

  public String getUser()
  {
    return user;
  }


  /****************************************************************************/

}

/******************************************************************************/
