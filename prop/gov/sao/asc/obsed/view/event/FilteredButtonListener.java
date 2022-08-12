/******************************************************************************/

package gov.sao.asc.obsed.view.event;

/******************************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/******************************************************************************/

public class FilteredButtonListener implements ActionListener
{
  FilteredInterface adaptee;

  /****************************************************************************/

  public FilteredButtonListener(FilteredInterface adaptee)
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void actionPerformed(ActionEvent event)
  {
    adaptee.filtered();
  }
  
  /****************************************************************************/

}

/******************************************************************************/
