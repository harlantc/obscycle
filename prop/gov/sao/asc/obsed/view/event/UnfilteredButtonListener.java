/******************************************************************************/

package gov.sao.asc.obsed.view.event;

/******************************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/******************************************************************************/

public class UnfilteredButtonListener implements ActionListener
{
  UnfilteredInterface adaptee;

  /****************************************************************************/

  public UnfilteredButtonListener(UnfilteredInterface adaptee)
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void actionPerformed(ActionEvent event)
  {
    adaptee.unfiltered();
  }
  
  /****************************************************************************/

}

/******************************************************************************/
