/******************************************************************************/

package gov.sao.asc.obsed.view.event;

/******************************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/******************************************************************************/

public class AndButtonListener implements ActionListener
{
  AndInterface adaptee;

  /****************************************************************************/

  public AndButtonListener(AndInterface adaptee)
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void actionPerformed(ActionEvent event)
  {
    adaptee.and();
  }
  
  /****************************************************************************/

}

/******************************************************************************/
