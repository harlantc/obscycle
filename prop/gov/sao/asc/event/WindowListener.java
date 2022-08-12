/******************************************************************************/

package gov.sao.asc.event;

/******************************************************************************/

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/******************************************************************************/

public class WindowListener extends WindowAdapter
{
  private WindowInterface adaptee;

  /****************************************************************************/

  public WindowListener(WindowInterface adaptee)
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void windowClosed(WindowEvent event)
  {
    adaptee.windowClosed();
  }
  
  /****************************************************************************/

}

/******************************************************************************/
