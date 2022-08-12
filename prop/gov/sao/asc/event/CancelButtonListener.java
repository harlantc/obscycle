/******************************************************************************/

package gov.sao.asc.event;

/******************************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/******************************************************************************/

public class CancelButtonListener implements ActionListener
{
  CancelInterface adaptee;

  /****************************************************************************/

  public CancelButtonListener(CancelInterface adaptee)
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void actionPerformed(ActionEvent event)
  {
    adaptee.cancel();
  }
  
  /****************************************************************************/

}

/******************************************************************************/
