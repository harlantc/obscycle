/******************************************************************************/

package gov.sao.asc.event;

/******************************************************************************/

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/******************************************************************************/

public class CheckBoxListener implements ChangeListener
{
  CheckBoxInterface adaptee;

  /****************************************************************************/

  public CheckBoxListener( CheckBoxInterface adaptee )
    {
	this.adaptee = adaptee;
    }

  /****************************************************************************/

  public void stateChanged( ChangeEvent event )
    {
      adaptee.setModified( true );
    }
  
  /****************************************************************************/

}

/******************************************************************************/
