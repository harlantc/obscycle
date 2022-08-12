/******************************************************************************/

package gov.sao.asc.obsed.view.event;

/******************************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/******************************************************************************/

public class BothButtonListener implements ActionListener
{
  BothInterface adaptee;

  /****************************************************************************/

  public BothButtonListener( BothInterface adaptee )
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void actionPerformed( ActionEvent event )
  {
    adaptee.both();
  }
  
  /****************************************************************************/

}

/******************************************************************************/
