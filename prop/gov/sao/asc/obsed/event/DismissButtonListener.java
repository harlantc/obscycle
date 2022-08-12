/******************************************************************************/

package gov.sao.asc.obsed.event;

/******************************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/******************************************************************************/

public class DismissButtonListener implements ActionListener
{
  DismissInterface adaptee;

  /****************************************************************************/

  public DismissButtonListener(DismissInterface adaptee)
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void actionPerformed(ActionEvent event)
  {
    adaptee.dismiss();
  }
  
  /****************************************************************************/

}

/******************************************************************************/
