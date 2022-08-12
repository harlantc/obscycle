/******************************************************************************/

package gov.sao.asc.event;

/******************************************************************************/

import javax.swing.event.*;

/******************************************************************************/

public class TextAreaListener implements DocumentListener
{
  private TextAreaInterface adaptee;

  /****************************************************************************/

  public TextAreaListener(TextAreaInterface adaptee)
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
