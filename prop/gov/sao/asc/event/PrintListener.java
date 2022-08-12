/******************************************************************************/

package gov.sao.asc.event;

/******************************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/******************************************************************************/

public class PrintListener implements ActionListener
{
  PrintInterface adaptee;

  /****************************************************************************/

  public PrintListener(PrintInterface adaptee)
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void actionPerformed(ActionEvent event)
  {
    adaptee.print();
  }
  
  /****************************************************************************/

}

/******************************************************************************/
