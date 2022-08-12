/******************************************************************************/

package gov.sao.asc.util;

/******************************************************************************/

import gov.sao.asc.event.ComboBoxInterface;
import gov.sao.asc.event.ComboBoxListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/******************************************************************************/

public class ComboBoxBorderPanel extends JPanel
  implements ComboBoxInterface
{
  boolean enabled;
  int borderSpacing;
  int comboBoxOffset;
  int contentPaneSpacing;
  Container contentPane;
  ComboBox comboBox;
  JLabel label;
  String enabledItem;

  /****************************************************************************/

  public ComboBoxBorderPanel(String title, ComboBoxModel model)
  {
    borderSpacing = 2;
    contentPaneSpacing = (borderSpacing * 2) + 2;
    comboBoxOffset = contentPaneSpacing + 10;

    setLayout( new GridBagLayout() );

    label = new JLabel(title);

    GridBagLayoutUtil.addComponent(this, label,
                                   0, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 
                                   0, comboBoxOffset, 0, 2);

    comboBox = new ComboBox(model);

    comboBox.addItemListener( new ComboBoxListener(this) );

    comboBox.setSize( comboBox.getPreferredSize() );

    GridBagLayoutUtil.addComponent(this, comboBox,
                                   1, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 
                                   0, 2, 0, 0);

    contentPane = new JPanel();

    GridBagLayoutUtil.addComponent(this, contentPane,
                                   0, 1, 2, 1,
                                   GridBagConstraints.BOTH,
                                   GridBagConstraints.CENTER,
                                   1.0, 1.0, 
                                   0, contentPaneSpacing,
                                   contentPaneSpacing, contentPaneSpacing);
  }

  /****************************************************************************/

  public ComboBoxBorderPanel(String title, ComboBoxModel model, 
                             String enabledItem)
  {
    this(title, model);
    
    this.enabledItem = enabledItem;
  }

  /****************************************************************************/

  public void comboBox()
  {
    String item = (String) comboBox.getSelectedItem();

    if ( item.equals(enabledItem) )
    {
      setEnabled(true);
    }
    else
    {
      setEnabled(false);
    }
  }

  /****************************************************************************/

  public Container getContentPane()
  {
    return(contentPane);
  }

  /****************************************************************************/

  public Object getSelectedItem()
  {
    return( comboBox.getSelectedObject() );
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

    int comboBoxAscent = (comboBox.getHeight() / 2);

    Rectangle borderRectangle = 
      new Rectangle(borderSpacing, comboBoxAscent,
                    getWidth() - borderSpacing, getHeight() - borderSpacing);

    Rectangle labelRectangle = label.getBounds();
    Rectangle comboBoxRectangle = comboBox.getBounds();
    
    int y;

    if (labelRectangle.y > comboBoxRectangle.y)
    {
      y = labelRectangle.y;
    }
    else
    {
      y = comboBoxRectangle.y;
    }

    int height;

    if (labelRectangle.height > comboBoxRectangle.height)
    {
      height = labelRectangle.height;
    }
    else
    {
      height = comboBoxRectangle.height;
    }

    Rectangle voidRectangle = 
      new Rectangle(labelRectangle.x, y, 
                    labelRectangle.width + comboBoxRectangle.width, height);

    voidRectangle.x -= 5;
    voidRectangle.width += 12;

    paintBorder(graphics, borderRectangle, voidRectangle);
  }

  /****************************************************************************/

  private void paintBorder(Graphics graphics, 
                           Rectangle borderRectangle,
                           Rectangle voidRectangle)
  {
    graphics.setColor( getBackground().darker() );

    // Draw left
    graphics.drawLine(borderRectangle.x, borderRectangle.y, 
                      borderRectangle.x, borderRectangle.height - 2);

    // Draw top
    graphics.drawLine(borderRectangle.x, borderRectangle.y,
                      voidRectangle.x, borderRectangle.y);

    graphics.drawLine(voidRectangle.x + voidRectangle.width, 
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
                      voidRectangle.x, borderRectangle.y + 1);

    graphics.drawLine(voidRectangle.x + voidRectangle.width, 
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

  public void setEnabledItem(String enabledItem)
  {
    this.enabledItem = enabledItem;
  }

  /****************************************************************************/

  public void setSelectedIndex(int index)
  {
    comboBox.setSelectedIndex(index);
  }

  /****************************************************************************/

  public void setSelectedItem(Object item)
  {
    comboBox.setSelectedItem(item);
  }

  /****************************************************************************/

}

/******************************************************************************/
