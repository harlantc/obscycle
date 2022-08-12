/******************************************************************************/

package gov.sao.asc.event;

/******************************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/******************************************************************************/

public class OKButtonListener implements ActionListener
{
  OKInterface adaptee;

  /****************************************************************************/

  public OKButtonListener(OKInterface adaptee)
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void actionPerformed(ActionEvent event)
  {
    adaptee.ok();
  }
  
  /****************************************************************************/

}

/******************************************************************************/
