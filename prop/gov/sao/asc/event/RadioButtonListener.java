/******************************************************************************/

package gov.sao.asc.event;

/******************************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/******************************************************************************/

public class RadioButtonListener implements ActionListener
{
  RadioButtonInterface adaptee;

  /****************************************************************************/

  public RadioButtonListener(RadioButtonInterface adaptee)
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void actionPerformed(ActionEvent event)
  {
    adaptee.radioButton();
  }
  
  /****************************************************************************/

}

/******************************************************************************/
