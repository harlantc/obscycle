/******************************************************************************/

package gov.sao.asc.obsed.view.event;

/******************************************************************************/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/******************************************************************************/

public class ConeSearchButtonListener implements ActionListener
{
  ConeSearchInterface adaptee;

  /****************************************************************************/

  public ConeSearchButtonListener( ConeSearchInterface adaptee )
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void actionPerformed( ActionEvent event )
  {
    adaptee.coneSearch();
  }
  
  /****************************************************************************/

}

/******************************************************************************/
