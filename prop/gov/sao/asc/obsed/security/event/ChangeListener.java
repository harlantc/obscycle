/******************************************************************************/

package gov.sao.asc.obsed.security.event;

/******************************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/******************************************************************************/

public class ChangeListener implements ActionListener
{
  ChangeInterface adaptee;

  /****************************************************************************/

  public ChangeListener(ChangeInterface adaptee)
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void actionPerformed(ActionEvent event)
  {
    adaptee.change();
  }
  
  /****************************************************************************/

}

/******************************************************************************/
