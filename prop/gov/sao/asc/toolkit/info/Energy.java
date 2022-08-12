package info;
/*
  Copyrights:
 
  Copyright (c) 2000 Smithsonian Astrophysical Observatory
 
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

import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/******************************************************************************/
/**
 * Set up a pair of values supporting energy range validation.
 */

public class Energy implements ToolkitConstants
{
  /****************************************************************************/
  /**
   * Construct an energy object of the given type: input or output.
   *
   * @param request The browser generated request object.
   * @param energyType A string specifying either "input" or "output".
   * @param toolkitProperties The source of energy range limit
   * information.
   */

  public Energy( HttpServletRequest request, 
		 String energyType,
		 Properties toolkitProperties )
  {
    this.toolkitProperties = toolkitProperties;

    // Get the settings passed in the command.
    low = Parameter.get( request, energyType + "EnergyLow" );
    if (low != null && low.trim().compareToIgnoreCase("default")  == 0) 
       low = "";
    high = Parameter.get( request, energyType + "EnergyHigh" );
    if (high != null && high.trim().compareToIgnoreCase("default")  == 0) 
       high = "";

    // Deal with the Raymond Smith energy constraint.
    String model = Parameter.get( request, "modelSelector" );
    if ( "RS".equals( model ) )
    {
      // Test that the cutoff value has not been exceeded.
      double value;
      if (energyType.indexOf("Density") < 0 && high != null && !high.equals("")) {
        value = Double.parseDouble( high );
      }else {
        value = RAYMOND_SMITH_CUTOFF;
      }
      if ( value > RAYMOND_SMITH_CUTOFF )
      {
	// Set the upper energy level to the the Raymond Smith cutoff
	// value.
	high = String.valueOf( RAYMOND_SMITH_CUTOFF );
	HttpSession session = request.getSession( true );
	session.setAttribute( energyType + "EnergyHigh", high );

	// Append the warning reason onto the warning script session
	// value.  It will subsequently be merged into JavaScript
	// syntax to cause a popup to be displayed.
	String reason = (String) session.getAttribute( "pimmsWarningsScript" );
	reason +=
	  toolkitProperties.getProperty( "raymond.smith.cutoff.prefix.text" );
	reason += " " + energyType + " " +
	  toolkitProperties.getProperty( "raymond.smith.cutoff.suffix.text" );
	session.setAttribute( "pimmsWarningsScript", reason );
      }
    }
  }

  /****************************************************************************/
  /**
   * Return the lower energy value.
   *
   * @return The lower value in an energy range.
   */

  public String getLow()
  {
    return low;
  }

  /****************************************************************************/
  /**
   * Return the upper energy value.
   *
   * @return The upper value in an energy range.
   */

  public String getHigh()
  {
    return high;
  }

  /****************************************************************************/
  /**
   * Private variables
   */

  private String low;
  private String high;
  private Properties toolkitProperties;

  /****************************************************************************/

}

/******************************************************************************/
