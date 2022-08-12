/******************************************************************************/

package gov.sao.asc.obsed.event;

/******************************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/******************************************************************************/

public class PrintViewListener implements ActionListener
{
  PrintViewInterface adaptee;

  /****************************************************************************/

  public PrintViewListener(PrintViewInterface adaptee)
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void actionPerformed(ActionEvent event)
  {
    adaptee.printView();
  }
  
  /****************************************************************************/

}

/******************************************************************************/
