/******************************************************************************/

package gov.sao.asc.obsed.view.event;

/******************************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/******************************************************************************/

public class OrButtonListener implements ActionListener
{
  OrInterface adaptee;

  /****************************************************************************/

  public OrButtonListener(OrInterface adaptee)
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void actionPerformed(ActionEvent event)
  {
    adaptee.or();
  }
  
  /****************************************************************************/

}

/******************************************************************************/
