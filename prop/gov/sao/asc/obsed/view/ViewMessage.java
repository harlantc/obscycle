/******************************************************************************/

package gov.sao.asc.obsed.view;

/******************************************************************************/

import gov.sao.asc.obsed.*;

/******************************************************************************/

/**
 * Provide a View messaging facility.
 */

public class ViewMessage extends Message
{
  private View view;

  /****************************************************************************/

  /**
   * Build a View message.
   */

  public ViewMessage( int command, View view )
  {
    super( command, null );
    this.view = view;
  }

  /****************************************************************************/

  /**
   * Return the view associated with this message.
   */

  public View getView() 
  {
    return view;
  }

}

/******************************************************************************/
