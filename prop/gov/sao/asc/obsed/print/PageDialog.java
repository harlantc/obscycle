/*
      Copyrights:
 
      Copyright (c) 1998 Smithsonian Astrophysical Observatory
 
      Permission to use, copy, modify, distribute, and  sell  this
      software  and  its  documentation  for any purpose is hereby
      granted without  fee,  provided  that  the  above  copyright
      notice  appear  in  all  copies and that both that copyright
      notice and this permission notice appear in supporting docu-
      mentation,  and  that  the  name  of the  Smithsonian Astro-
      physical Observatory not be used in advertising or publicity
      pertaining to distribution of the software without specific,
      written  prior  permission.   The Smithsonian  Astrophysical
      Observatory makes no representations about  the  suitability
      of  this  software for any purpose.  It is provided  "as is"
      without express or implied warranty.
      THE  SMITHSONIAN  INSTITUTION  AND  THE  SMITHSONIAN  ASTRO-
      PHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES  WITH REGARD TO
      THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANT-
      ABILITY AND FITNESS,  IN  NO  EVENT  SHALL  THE  SMITHSONIAN
      INSTITUTION AND/OR THE SMITHSONIAN ASTROPHYSICAL OBSERVATORY
      BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES
      OR ANY DAMAGES  WHATSOEVER  RESULTING FROM LOSS OF USE, DATA
      OR PROFITS,  WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
      OTHER TORTIOUS ACTION,  ARISING OUT OF OR IN CONNECTION WITH
      THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/
/******************************************************************************/

package gov.sao.asc.obsed.print;

/******************************************************************************/

import gov.sao.asc.event.OKButtonListener;
import gov.sao.asc.event.OKInterface;
import gov.sao.asc.util.GridBagLayoutUtil;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.print.PageFormat;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

/******************************************************************************/

class PageDialog extends JDialog 
  implements OKInterface
{
  // Print Options
  JLabel orientationLabel;
  JRadioButton portraitRadioButton;
  JRadioButton landscapeRadioButton;
  PageFormat pageFormat;

  /****************************************************************************/
  /**
   * Creates a modal unix print dialog.
   */
  
  public PageDialog(JFrame parent, String title) 
  {
    super(parent, title, true);

    init();
  }

  /****************************************************************************/

  public PageFormat getPageFormat()
  {
    if ( portraitRadioButton.isSelected() )
      {
        pageFormat.setOrientation(PageFormat.PORTRAIT);
      }
    else
      {
        pageFormat.setOrientation(PageFormat.LANDSCAPE);
      }

    return(pageFormat);
  }

  /****************************************************************************/

  public void init()
  {
    Container contentPane = getContentPane();

    contentPane.setLayout( new GridBagLayout() );

    ButtonGroup buttonGroup = new ButtonGroup();

    GridBagLayoutUtil.addComponent(contentPane, new JLabel("Orientation:", 
                                                           JLabel.RIGHT),
                                   0, 4, 1, 1, 
                                   GridBagConstraints.NONE, 
                                   GridBagConstraints.EAST, 
                                   0.0, 0.0, 10, 10, 10, 5);

    portraitRadioButton = new JRadioButton("Portrait");

    portraitRadioButton.setSelected(true);

    buttonGroup.add(portraitRadioButton);

    GridBagLayoutUtil.addComponent(contentPane, portraitRadioButton,
                                   1, 4, 1, 1, 
                                   GridBagConstraints.HORIZONTAL, 
                                   GridBagConstraints.WEST, 
                                   0.0, 0.0, 10, 5, 10, 10);

    landscapeRadioButton = new JRadioButton("Landscape");

    buttonGroup.add(landscapeRadioButton);

    GridBagLayoutUtil.addComponent(contentPane, landscapeRadioButton,
                                   2, 4, 1, 1, 
                                   GridBagConstraints.HORIZONTAL, 
                                   GridBagConstraints.WEST, 
                                   0.0, 0.0, 10, 10, 10, 10);
	
    GridBagLayoutUtil.addComponent(contentPane, new JSeparator(),
                                   0, 5, 3, 1, 
                                   GridBagConstraints.HORIZONTAL, 
                                   GridBagConstraints.CENTER, 
                                   0.0, 0.0, 0, 0, 0, 0);

    JPanel buttonPanel = initButtonPanel();

    GridBagLayoutUtil.addComponent(contentPane, buttonPanel,
                                   0, 6, 3, 1, 
                                   GridBagConstraints.HORIZONTAL, 
                                   GridBagConstraints.CENTER, 
                                   0.0, 0.0, 0, 0, 0, 0);

    pack();
  }

  /****************************************************************************/

  private JPanel initButtonPanel()
  {
    JPanel result = new JPanel();

    result.setLayout( new GridBagLayout() );

    JButton okButton = new JButton("OK");

    okButton.addActionListener( new OKButtonListener(this) );

    GridBagLayoutUtil.addComponent(result, okButton,
                                   0, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    return(result);
  }

  /****************************************************************************/

  public void ok()
  {
    setVisible(false);
  }

  /****************************************************************************/

  public static void main(String[] args)
  {
    PageDialog dialog = new PageDialog(new javax.swing.JFrame(), "This");

    dialog.show();
  }

  /****************************************************************************/

  public void setPageFormat(PageFormat pageFormat)
  {
    this.pageFormat = pageFormat;

    if (pageFormat.getOrientation() == PageFormat.PORTRAIT)
      {
        portraitRadioButton.setSelected(true);
      }
    else
      {
        landscapeRadioButton.setSelected(true);
      }
  }

  /****************************************************************************/

}

/******************************************************************************/
