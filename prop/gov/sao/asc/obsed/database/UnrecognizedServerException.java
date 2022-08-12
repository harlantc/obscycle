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
 * <P>The UnrecognizedServerException class provides information on a
 * server name error.
 *
 * <P>Each UnrecognizedServerException provides several kinds of information: 
 * <UL>
 *   <LI> a string describing the error.  This is used as the Java Exception
 *       message, and is available via the getMessage() method
 *   <LI> A server string which specifies the server name.
 * </UL>
 */

public class UnrecognizedServerException extends Exception
{
  private String server;

  /****************************************************************************/
  /**
   * Construct a fully-specified UnrecognizedServerException  
   *
   * @param reason a description of the exception 
   * @param server the database server
   */

  public UnrecognizedServerException( String reason, String server )
  {
    super( reason );
    this.server = server;
  }

  /****************************************************************************/
  /**
   * Get the server name.
   *
   * @return the server name.
   */

  public String getServerName()
  {
    return server;
  }	

  /****************************************************************************/

}

/******************************************************************************/
