/******************************************************************************/

package gov.sao.asc.util;

/******************************************************************************/

import gov.sao.asc.event.CheckBoxInterface;
import gov.sao.asc.event.CheckBoxListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

/******************************************************************************/

public class CheckBoxBorderPanel extends JPanel
  implements CheckBoxInterface
{
  boolean enabled;
  boolean negateCheckBox;
  int borderSpacing;
  int checkBoxOffset;
  int contentPaneSpacing;
  Container contentPane;
  JCheckBox checkBox;

  /****************************************************************************/

  public CheckBoxBorderPanel(String title)
  {
    negateCheckBox = false;

    borderSpacing = 2;
    contentPaneSpacing = (borderSpacing * 2) + 2;
    checkBoxOffset = contentPaneSpacing + 10;

    setLayout( new GridBagLayout() );

    checkBox = new JCheckBox(title);

    checkBox.addChangeListener( new CheckBoxListener(this) );

    checkBox.setSize( checkBox.getPreferredSize() );

    GridBagLayoutUtil.addComponent(this, checkBox,
                                   0, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 
                                   0, checkBoxOffset, 0, 0);

    contentPane = new JPanel();

    GridBagLayoutUtil.addComponent(this, contentPane,
                                   0, 1, 1, 1,
                                   GridBagConstraints.BOTH,
                                   GridBagConstraints.CENTER,
                                   1.0, 1.0, 
                                   0, contentPaneSpacing,
                                   contentPaneSpacing, contentPaneSpacing);
  }

  /****************************************************************************/

  public CheckBoxBorderPanel(String title, boolean negateCheckBox)
  {
    this(title);
    
    this.negateCheckBox = negateCheckBox;
  }

  /****************************************************************************/

  public void checkBox()
  {
    boolean enabled;

    if (! negateCheckBox)
    {
      enabled = checkBox.isSelected();
    }
    else
    {
      enabled = (! checkBox.isSelected());
    }

    setEnabled(enabled);
  }

  /****************************************************************************/

  public Container getContentPane()
  {
    return(contentPane);
  }

  /****************************************************************************/

  public boolean isEnabled()
  {
    return(enabled);
  }

  /****************************************************************************/

  public void paint(Graphics graphics)
  {
    super.paint(graphics);

    int checkBoxAscent = (checkBox.getHeight() / 2);

    Rectangle borderRectangle = 
      new Rectangle(borderSpacing, checkBoxAscent,
                    getWidth() - borderSpacing, getHeight() - borderSpacing);

    Rectangle checkBoxRectangle = checkBox.getBounds();

    checkBoxRectangle.x -= 5;
    checkBoxRectangle.width += 5;

    paintBorder(graphics, borderRectangle, checkBoxRectangle);
  }

  /****************************************************************************/

  private void paintBorder(Graphics graphics, 
                           Rectangle borderRectangle,
                           Rectangle checkBoxRectangle)
  {
    graphics.setColor( getBackground().darker() );

    // Draw left
    graphics.drawLine(borderRectangle.x, borderRectangle.y, 
                      borderRectangle.x, borderRectangle.height - 2);

    // Draw top
    graphics.drawLine(borderRectangle.x, borderRectangle.y,
                      checkBoxRectangle.x, borderRectangle.y);

    graphics.drawLine(checkBoxRectangle.x + checkBoxRectangle.width, 
                      borderRectangle.y, 
                      borderRectangle.width - 2, borderRectangle.y);

    graphics.setColor( getBackground().darker() );

    // Draw right
    graphics.drawLine(borderRectangle.width - 2, borderRectangle.y, 
                      borderRectangle.width - 2, borderRectangle.height - 2);

    // Draw bottom
    graphics.drawLine(borderRectangle.x, borderRectangle.height - 2,
                      borderRectangle.width - 2, borderRectangle.height - 2);
	
    graphics.setColor( getBackground().brighter() );

    // Draw left
    graphics.drawLine(borderRectangle.x + 1, borderRectangle.y + 1, 
                      borderRectangle.x + 1, borderRectangle.height - 3);

    // Draw top
    graphics.drawLine(borderRectangle.x + 1, borderRectangle.y + 1, 
                      checkBoxRectangle.x, borderRectangle.y + 1);

    graphics.drawLine(checkBoxRectangle.x + checkBoxRectangle.width, 
                      borderRectangle.y + 1, 
                      borderRectangle.width - 3, borderRectangle.y + 1);

    // Draw right
    graphics.drawLine(borderRectangle.width - 1, borderRectangle.y, 
                      borderRectangle.width - 1, borderRectangle.height - 1);

    // Draw bottom
    graphics.drawLine(borderRectangle.x, borderRectangle.height - 1,
                      borderRectangle.width - 1, borderRectangle.height - 1);
  }

  /****************************************************************************/

  public void setContentPane(Container contentPane)
  {
    this.contentPane = contentPane;
  }

  /****************************************************************************/

  public void setChecked(boolean checked)
  {
    checkBox.setSelected(checked);

    if (! negateCheckBox)
    {
      setEnabled(checked);
    }
    else
    {
      setEnabled(! checked);
    }
  }

  /****************************************************************************/

  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;

    Component components[] = contentPane.getComponents();

    for (int i = 0; i < components.length; i++)
    {
      components[i].setEnabled(enabled);
    }
  }

  /****************************************************************************/

}

/******************************************************************************/
