/******************************************************************************/

package gov.sao.asc.util;

/******************************************************************************/

import gov.sao.asc.event.RadioButtonInterface;
import gov.sao.asc.event.RadioButtonListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import javax.swing.JRadioButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

/******************************************************************************/

public class RadioButtonBorderPanel extends JPanel
  implements RadioButtonInterface
{
  boolean enabled;
  int borderSpacing;
  int radioButtonOffset;
  int contentPaneSpacing;
  Container contentPane;
  JRadioButton radioButton;

  /****************************************************************************/

  public RadioButtonBorderPanel(String title)
  {
    borderSpacing = 2;
    contentPaneSpacing = (borderSpacing * 2) + 2;
    radioButtonOffset = contentPaneSpacing + 10;

    setLayout( new GridBagLayout() );

    radioButton = new JRadioButton(title);

    radioButton.addActionListener( new RadioButtonListener(this) );

    radioButton.setSize( radioButton.getPreferredSize() );

    GridBagLayoutUtil.addComponent(this, radioButton,
                                   0, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 
                                   0, radioButtonOffset, 0, 0);

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

  public void radioButton()
  {
    setEnabled( radioButton.isSelected() );
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

    int radioButtonAscent = (radioButton.getHeight() / 2);

    Rectangle borderRectangle = 
      new Rectangle(borderSpacing, radioButtonAscent,
                    getWidth() - borderSpacing, getHeight() - borderSpacing);

    Rectangle radioButtonRectangle = radioButton.getBounds();

    radioButtonRectangle.x -= 5;
    radioButtonRectangle.width += 5;

    paintBorder(graphics, borderRectangle, radioButtonRectangle);
  }

  /****************************************************************************/

  private void paintBorder(Graphics graphics, 
                           Rectangle borderRectangle,
                           Rectangle radioButtonRectangle)
  {
    graphics.setColor( getBackground().darker() );

    // Draw left
    graphics.drawLine(borderRectangle.x, borderRectangle.y, 
                      borderRectangle.x, borderRectangle.height - 2);

    // Draw top
    graphics.drawLine(borderRectangle.x, borderRectangle.y,
                      radioButtonRectangle.x, borderRectangle.y);

    graphics.drawLine(radioButtonRectangle.x + radioButtonRectangle.width, 
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
                      radioButtonRectangle.x, borderRectangle.y + 1);

    graphics.drawLine(radioButtonRectangle.x + radioButtonRectangle.width, 
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
    radioButton.setSelected(checked);

    setEnabled(checked);
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
