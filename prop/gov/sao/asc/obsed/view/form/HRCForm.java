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

package gov.sao.asc.obsed.view.form;

/******************************************************************************/

import gov.sao.asc.event.CancelButtonListener;
import gov.sao.asc.event.CancelInterface;
import gov.sao.asc.event.OKButtonListener;
import gov.sao.asc.event.OKInterface;
import gov.sao.asc.obsed.Constants;
import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.data.ConstraintException;
import gov.sao.asc.obsed.data.HRCParameterBlock;
import gov.sao.asc.util.ComboBox;
import gov.sao.asc.util.ComboBoxModel;
import gov.sao.asc.util.GridBagLayoutUtil;
import gov.sao.asc.util.LocationUtil;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

/******************************************************************************/

public class HRCForm extends JFrame
  implements CancelInterface, OKInterface
{
  ComboBox zeroBlockComboBox;
  ComboBox chopDutyCycleComboBox;
  ComboBox configComboBox;
  FormListener listener;
  JTextField chopNumberTextField;
  JTextField fractionTextField;

  /****************************************************************************/

  public HRCForm(FormListener listener)
  {
    this.listener = listener;

    init();
  }

  /****************************************************************************/

  public void cancel()
  {
    listener.cancelled();
  }

  /****************************************************************************/

  public HRCParameterBlock getValues()
    throws ConstraintException, NumberFormatException
  {
    HRCParameterBlock result = new HRCParameterBlock();

    result.setConfiguration( (String) configComboBox.getSelectedObject() );

    String fractionString = fractionTextField.getText();
    
    if (! fractionString.equals("") )
    {
      result.setChopFraction( Double.valueOf(fractionString) );
    }

    result.setZeroBlock( (String) zeroBlockComboBox.getSelectedObject() );

    result.setChopDutyCycle( (String) chopDutyCycleComboBox.getSelectedObject() );

    String chopNumberString = chopNumberTextField.getText();
    
    if (! chopNumberString.equals("") )
    {
      result.setChopDutyCycleNumber( Integer.valueOf(chopNumberString) );
    }

    return(result);
  }

  /****************************************************************************/

  public void init()
  {
    setTitle("HRC Form");
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    Container contentPane = getContentPane();
    
    contentPane.setLayout( new GridBagLayout() );

    JPanel hrcPanel = initHRCPanel();

    hrcPanel.setBorder( new EtchedBorder() );

    GridBagLayoutUtil.addComponent(contentPane, hrcPanel, 
                                   0, 0, 5, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    JPanel buttonPanel = initButtonPanel();

    GridBagLayoutUtil.addComponent(contentPane, buttonPanel, 
                                   0, 1, 5, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER,
                                   1.0, 0.0, 5, 5, 5, 5);
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

  private JPanel initHRCPanel()
  {
    JPanel result = new JPanel();

    result.setLayout( new GridBagLayout() );

    JLabel configLabel = new JLabel("Config", JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(result, configLabel,
                                   0, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    ComboBoxModel configModel =
      new ComboBoxModel( HRCParameterBlock.getConfigurations() );

    configComboBox = new ComboBox(configModel);

    GridBagLayoutUtil.addComponent(result, configComboBox,
                                   1, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel fractionLabel = new JLabel("Chop Fraction", JLabel.RIGHT);

    fractionLabel.setEnabled(false);

    GridBagLayoutUtil.addComponent(result, fractionLabel,
                                   2, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    fractionTextField = new JTextField(8);

    fractionTextField.setEnabled(false);

    GridBagLayoutUtil.addComponent(result, fractionTextField,
                                   3, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);


    JLabel zeroBlockLabel = new JLabel("Zero Block", JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(result, zeroBlockLabel,
                                   4, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    ComboBoxModel zeroBlockModel =
      new ComboBoxModel( Constants.getBooleans() );

    // Default to No
    zeroBlockModel.setSelectedItem(Constants.NO);

    zeroBlockComboBox = new ComboBox(zeroBlockModel);

    GridBagLayoutUtil.addComponent(result, zeroBlockComboBox,
                                   5, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel chopLabel = new JLabel("Chop Duty Cycle", JLabel.RIGHT);

    chopLabel.setEnabled(false);

    GridBagLayoutUtil.addComponent(result, chopLabel,
                                   0, 1, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    ComboBoxModel chopDutyCycleModel = 
      new ComboBoxModel( HRCParameterBlock.getChopDutyCycles() );

    chopDutyCycleComboBox = new ComboBox(chopDutyCycleModel);
    
    chopDutyCycleComboBox.setEnabled(false);

    GridBagLayoutUtil.addComponent(result, chopDutyCycleComboBox,
                                   1, 1, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel chopNumberLabel = new JLabel("Chop Duty Number", JLabel.RIGHT);

    chopNumberLabel.setEnabled(false);

    GridBagLayoutUtil.addComponent(result, chopNumberLabel,
                                   2, 1, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    chopNumberTextField = new JTextField(10);

    chopNumberTextField.setEnabled(false);

    GridBagLayoutUtil.addComponent(result, chopNumberTextField,
                                   3, 1, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    return(result);
  }

  /****************************************************************************/

  public void ok()
  {
    listener.completed();
  }

  /****************************************************************************/

  public void setValues(HRCParameterBlock paramBlock)
  {
    String configuration = paramBlock.getConfiguration();

    configComboBox.setSelectedItem(configuration);

    Double chopFraction = paramBlock.getChopFraction();

    if (chopFraction != null)
    {
      fractionTextField.setText( chopFraction.toString() );
    }
    
    String zeroBlock = paramBlock.getZeroBlock();

    zeroBlockComboBox.setSelectedItem(zeroBlock);

    String chopDutyCycle = paramBlock.getChopDutyCycle();

    chopDutyCycleComboBox.setSelectedItem(chopDutyCycle);

    Integer chopNumber = paramBlock.getChopDutyCycleNumber();

    if (chopNumber != null)
    {
      chopNumberTextField.setText( chopNumber.toString() );
    }
  }

  /****************************************************************************/

  public void show()
  {
    pack();

    setLocation( LocationUtil.center(this) );

    super.show();
  }

  /****************************************************************************/

}

/******************************************************************************/
