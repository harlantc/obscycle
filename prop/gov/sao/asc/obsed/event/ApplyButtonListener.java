/******************************************************************************/

package gov.sao.asc.obsed.event;

/******************************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/******************************************************************************/

public class ApplyButtonListener implements ActionListener
{
  ApplyInterface adaptee;

  /****************************************************************************/

  public ApplyButtonListener(ApplyInterface adaptee)
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void actionPerformed(ActionEvent event)
  {
    adaptee.apply();
  }
  
  /****************************************************************************/

}

/******************************************************************************/
