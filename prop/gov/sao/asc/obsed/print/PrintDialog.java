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

import gov.sao.asc.event.CancelButtonListener;
import gov.sao.asc.event.CancelInterface;
import gov.sao.asc.event.PrintListener;
import gov.sao.asc.event.PrintInterface;
import gov.sao.asc.event.RadioButtonListener;
import gov.sao.asc.event.RadioButtonInterface;
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

class PrintDialog extends JDialog 
  implements CancelInterface, PrintInterface, RadioButtonInterface
{
  /**
   * Waiting print status (user response pending).
   */
  public final static int WAITING = 0;

  /**
   * Print print status (user activated "print").
   */
  public final static int PRINT = 1;

  /**
   * Cancelled print status (user activated "Cancel");
   */
  public final static int CANCELLED = 2;

  static final String DEFAULT_FILENAME = "out.ps";
  static final String DEFAULT_PRINTERCOMMAND = "lpr";

  /**
   * Print status used to determine result from modal dialog.
   */
  private int printStatus = WAITING;

  // Print Destination Radiobox and Fields
  JRadioButton printerRadioButton;
  JRadioButton fileRadioButton;

  JTextField printCommandTextField;
  JTextField fileTextField;

  /****************************************************************************/
  /**
   * Creates a modal unix print dialog.
   */
  
  public PrintDialog(JFrame parent, String title) 
  {
    super(parent, title, true);

    init();
  }

  /****************************************************************************/

  public void cancel()
  {
    printStatus = CANCELLED;
    setVisible(false);
  }

  /****************************************************************************/

  public int getDestinationType()
  {
    int result;

    if ( printerRadioButton.isSelected() )
      {
        result = PSPrinterJob.PRINTER;
      }
    else
      {
        result = PSPrinterJob.FILE;
      }

    return(result);
  }

  /****************************************************************************/

  public String getFile()
  {
    return( fileTextField.getText() );
  }

  /****************************************************************************/

  public String getPrintCommand()
  {
    return( printCommandTextField.getText() );
  }

  /****************************************************************************/
  /**
   * Return the print dialogs current state, either
   * WAITING, PRINT, or CANCEL.
   */

  public int getState() 
  {
    return printStatus;
  }

  /****************************************************************************/

  public void init()
  {
    Container contentPane = getContentPane();

    contentPane.setLayout( new GridBagLayout() );

    Font font = getParent().getFont();

    if (font == null)
    {
      font = new Font("Dialog", Font.PLAIN, 12);
    }

    Font boldFont = font.deriveFont(Font.BOLD);
    setFont(boldFont);

    //
    // Create Print Destination RadioBox
    //
    GridBagLayoutUtil.addComponent(contentPane, new JLabel("Print to:", JLabel.RIGHT), 
                                   0, 0, 1, 1,
                                   GridBagConstraints.NONE, 
                                   GridBagConstraints.EAST, 
                                   0.0, 0.0, 10, 10, 10, 5);

    ButtonGroup destinationButtonGroup = new ButtonGroup();

    printerRadioButton = new JRadioButton("Printer");

    destinationButtonGroup.add(printerRadioButton);

    printerRadioButton.addActionListener( new RadioButtonListener(this) );
    printerRadioButton.setSelected(true);

    GridBagLayoutUtil.addComponent(contentPane, printerRadioButton,
                                   1, 0, 1, 1, 
                                   GridBagConstraints.NONE, 
                                   GridBagConstraints.WEST, 
                                   0.0, 0.0, 10, 5, 10, 10);

    fileRadioButton = new JRadioButton("File");

    destinationButtonGroup.add(fileRadioButton);

    fileRadioButton.addActionListener( new RadioButtonListener(this) );

    GridBagLayoutUtil.addComponent(contentPane, fileRadioButton,
                                   2, 0, 1, 1, 
                                   GridBagConstraints.NONE, 
                                   GridBagConstraints.WEST, 
                                   0.0, 0.0, 10, 10, 10, 10);

    GridBagLayoutUtil.addComponent(contentPane, new JLabel("Print Command:", 
                                                           JLabel.RIGHT),
                                   0, 1, 1, 1, 
                                   GridBagConstraints.NONE, 
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 0, 10, 0, 5);

    printCommandTextField = new JTextField(24);

    printCommandTextField.setFont(font);
    printCommandTextField.setText(DEFAULT_PRINTERCOMMAND);

    GridBagLayoutUtil.addComponent(contentPane, printCommandTextField,
                                   1, 1, 2, 1, 
                                   GridBagConstraints.HORIZONTAL, 
                                   GridBagConstraints.WEST, 
                                   0.0, 0.0, 10, 5, 10, 10);	

    GridBagLayoutUtil.addComponent(contentPane, new JLabel("File:", 
                                                           JLabel.RIGHT),
                                   0, 2, 1, 1, 
                                   GridBagConstraints.NONE, 
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 0, 10, 0, 5);

    fileTextField = new JTextField(28);
 
    fileTextField.setFont(font);
    fileTextField.setText(DEFAULT_FILENAME);
    fileTextField.setEnabled(false);

    GridBagLayoutUtil.addComponent(contentPane, fileTextField,
                                   1, 2, 2, 1, 
                                   GridBagConstraints.HORIZONTAL, 
                                   GridBagConstraints.CENTER, 
                                   0.0, 0.0, 10, 5, 10, 10);

    GridBagLayoutUtil.addComponent(contentPane, new JSeparator(),
                                   0, 3, 3, 1, 
                                   GridBagConstraints.HORIZONTAL, 
                                   GridBagConstraints.CENTER, 
                                   0.0, 0.0, 0, 0, 0, 0);

    JPanel buttonPanel = initButtonPanel();

    GridBagLayoutUtil.addComponent(contentPane, buttonPanel,
                                   0, 4, 3, 1, 
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

    JButton printButton = new JButton("Print");

    printButton.addActionListener( new PrintListener(this) );

    GridBagLayoutUtil.addComponent(result, printButton,
                                   0, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    JButton cancelButton = new JButton("Cancel");

    cancelButton.addActionListener( new CancelButtonListener(this) );

    GridBagLayoutUtil.addComponent(result, cancelButton,
                                   1, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    return(result);
  }

  /****************************************************************************/

  public void print()
  {
    printStatus = PRINT;
    setVisible(false);
  }

  /****************************************************************************/

  public void radioButton()
  {
    if ( printerRadioButton.isSelected() )
    {
      printCommandTextField.setEnabled(true);
      fileTextField.setEnabled(false);
    } 
    else 
    {
      printCommandTextField.setEnabled(false);
      fileTextField.setEnabled(true);
    }
  }

  /****************************************************************************/

  public static void main(String[] args)
  {
    PrintDialog dialog = new PrintDialog(new javax.swing.JFrame(), "This");

    dialog.show();
  }

  /****************************************************************************/
  /**
   * Sets the current print destination to the specified type.
   */

  public void setDestinationType(int destinationType)  
  {
    if (destinationType == PSPrinterJob.PRINTER)
    {
      printerRadioButton.setSelected(true);
    }
    else
    {
      fileRadioButton.setSelected(true);
    }
  }

  /****************************************************************************/
  /**
   * Sets the destination string of the specified destination type.
   */
  
  public void setFile(String file)  
  {
    fileTextField.setText(file);
  }

  /****************************************************************************/
  /**
   * Sets the destination string of the specified destination type.
   */
  
  public void setPrintCommand(String printCommand)  
  {
    printCommandTextField.setText(printCommand);
  }

  /****************************************************************************/

  public int getPrintStatus() 
  {
    return(printStatus);
  }

  /****************************************************************************/

}

/******************************************************************************/
