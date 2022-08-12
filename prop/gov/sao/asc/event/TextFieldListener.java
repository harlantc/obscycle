/******************************************************************************/

package gov.sao.asc.event;

/******************************************************************************/

import javax.swing.event.*;

/******************************************************************************/

public class TextFieldListener implements DocumentListener
{
  TextFieldInterface adaptee;

  /****************************************************************************/

  public TextFieldListener(TextFieldInterface adaptee)
  {
    this.adaptee = adaptee;
  }

  /****************************************************************************/

  public void changedUpdate(DocumentEvent event)
  {
    adaptee.setModified(true);
  }
  
  /****************************************************************************/

  public void insertUpdate(DocumentEvent event)
  {
  }
  
  /****************************************************************************/

  public void removeUpdate(DocumentEvent event)
  {
  }
  
  /****************************************************************************/

}

/******************************************************************************/
