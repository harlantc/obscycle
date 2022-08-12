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

package gov.sao.asc.obsed.view;

/******************************************************************************/
/**
 * <P>Provides information on a configuration error.
 *
 * <P>Each instance provides several kinds of information: 
 * <UL>
 *   <LI> a string describing the error.  This is used as the Java Exception
 *       message, and is available via the getMessage() method
 *   <LI> The name of the configuration URL.
 *   <LI> The current XML node being processed.
 * </UL>
 */

public class ConfigurationException extends Exception
{
  private String url;
  private String node;

  /****************************************************************************/
  /**
   * Construct a fully-specified CouldNotConnectException  
   *
   * @param reason a description of the exception 
   * @param url the configuration URL
   * @param node the relevant XML node
   */

  public ConfigurationException( String reason, String url, String node )
  {
    super( reason );
    this.url = url;
    this.node = node;
  }

  /****************************************************************************/
  /**
   * Get the URL of the database we could not connect to.
   *
   * @return the URL.
   */

  public String getURL()
  {
    return url;
  }	

  /****************************************************************************/
  /**
   * Get the relevant XML node, if any.
   *
   * @return the XML node.
   */

  public String getNode()
  {
    return node;
  }

  /****************************************************************************/

}

/******************************************************************************/
