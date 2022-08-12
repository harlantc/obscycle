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
import gov.sao.asc.event.ClearButtonListener;
import gov.sao.asc.event.ClearInterface;
import gov.sao.asc.event.ComboBoxInterface;
import gov.sao.asc.event.ComboBoxListener;
import gov.sao.asc.event.OKButtonListener;
import gov.sao.asc.event.OKInterface;
import gov.sao.asc.obsed.Constants;
import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.data.ACISParameterBlock;
import gov.sao.asc.obsed.data.ConstraintException;
import gov.sao.asc.util.ComboBox;
import gov.sao.asc.util.ComboBoxBorderPanel;
import gov.sao.asc.util.ComboBoxModel;
import gov.sao.asc.util.GridBagLayoutUtil;
import gov.sao.asc.util.LocationUtil;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/******************************************************************************/

public class ACISForm extends JFrame
  implements CancelInterface, ClearInterface, ComboBoxInterface, OKInterface
{
  ComboBox biasAfterComboBox;
  ComboBox i0ComboBox;
  ComboBox i1ComboBox;
  ComboBox i2ComboBox;
  ComboBox i3ComboBox;
  ComboBox s0ComboBox;
  ComboBox s1ComboBox;
  ComboBox s2ComboBox;
  ComboBox s3ComboBox;
  ComboBox s4ComboBox;
  ComboBox s5ComboBox;
  ComboBox subarrayComboBox;
  ComboBoxBorderPanel biasRequestPanel;
  ComboBoxBorderPanel dutyCyclePanel;
  ComboBoxBorderPanel eventFilterPanel;
  ComboBoxBorderPanel eventThresholdPanel;
  ComboBoxBorderPanel onChipSummingPanel;
  ComboBoxBorderPanel spatialWindowPanel;
  ComboBoxBorderPanel standardChipsPanel;
  FormListener listener;
  JComboBox bepPackComboBox;
  JComboBox exposureModeComboBox;
  JLabel subarrayStartRowLabel;
  JLabel subarrayRowCountLabel;
  JTextField eventFilterHigherTextField;
  JTextField eventFilterLowerTextField;
  JTextField frameTimeTextField;
  JTextField frequencyTextField;
  JTextField onChipColumnCountTextField;
  JTextField onChipRowCountTextField;
  JTextField primaryExposureTimeTextField;
  JTextField secondaryExposureCountTextField;
  JTextField secondaryExposureTimeTextField;
  JTextField spatialWindowPhaseRangeTextField;
  JTextField spatialWindowHeightTextField;
  JTextField spatialWindowLowerThresholdTextField;
  JTextField spatialWindowSampleTextField;
  JTextField spatialWindowStartColumnTextField;
  JTextField spatialWindowStartRowTextField;
  JTextField spatialWindowWidthTextField;
  JTextField subarrayFrameTimeTextField;
  JTextField subarrayRowCountTextField;
  JTextField subarrayStartRowTextField;
  JTextField thresholdPhaseTextField;

  /****************************************************************************/

  public ACISForm(FormListener listener)
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

  public void clear()
  {
    exposureModeComboBox.setSelectedIndex(0); 

    // Standard Chips
    standardChipsPanel.setSelectedItem(Constants.YES);

    i0ComboBox.setSelectedItem(Constants.NO);
    i1ComboBox.setSelectedItem(Constants.NO);
    i2ComboBox.setSelectedItem(Constants.NO);
    i3ComboBox.setSelectedItem(Constants.NO);
                           
    s0ComboBox.setSelectedItem(Constants.NO);
    s1ComboBox.setSelectedItem(Constants.NO);
    s2ComboBox.setSelectedItem(Constants.NO);
    s3ComboBox.setSelectedItem(Constants.NO);
    s4ComboBox.setSelectedItem(Constants.NO);
    s5ComboBox.setSelectedItem(Constants.NO);

    // Subarray
    subarrayComboBox.setSelectedItem(Constants.NO);
    subarrayStartRowTextField.setText("");
    subarrayRowCountTextField.setText("");
    subarrayFrameTimeTextField.setText("");

    // Duty Cycle
    dutyCyclePanel.setSelectedItem(Constants.NO);
    secondaryExposureCountTextField.setText("");
    primaryExposureTimeTextField.setText("");
    secondaryExposureTimeTextField.setText("");

    bepPackComboBox.setSelectedIndex(0);
    frameTimeTextField.setText("");

    // On-chip Summing
    onChipSummingPanel.setSelectedItem(Constants.NO);
    onChipRowCountTextField.setText("");
    onChipColumnCountTextField.setText("");

    // Event Threshold
    eventThresholdPanel.setSelectedItem(Constants.NO);
    thresholdPhaseTextField.setText("");

    // Event Filter
    eventFilterPanel.setSelectedItem(Constants.NO);
    eventFilterLowerTextField.setText("");
    eventFilterHigherTextField.setText("");

    // Spatial Window
    spatialWindowPanel.setSelectedItem(Constants.NO);
    spatialWindowStartRowTextField.setText("");
    spatialWindowStartColumnTextField.setText("");
    spatialWindowSampleTextField.setText("");
    spatialWindowHeightTextField.setText("");
    spatialWindowWidthTextField.setText("");
    spatialWindowLowerThresholdTextField.setText("");
    spatialWindowPhaseRangeTextField.setText("");

    // Bias
    biasRequestPanel.setSelectedItem(Constants.NO);
    frequencyTextField.setText("");
    biasAfterComboBox.setSelectedIndex(1);
  }

  /****************************************************************************/

  public void comboBox()
  {
    String subarrayType = (String) subarrayComboBox.getSelectedItem();

    if ( subarrayType.equals(Constants.CUSTOM) )
    {
      subarrayStartRowLabel.setEnabled(true);
      subarrayStartRowTextField.setEnabled(true);

      subarrayRowCountLabel.setEnabled(true);
      subarrayRowCountTextField.setEnabled(true);
    }
    else
    {
      subarrayStartRowLabel.setEnabled(false);
      subarrayStartRowTextField.setEnabled(false);

      subarrayRowCountLabel.setEnabled(false);
      subarrayRowCountTextField.setEnabled(false);
    }
  }

  /****************************************************************************/

  public ACISParameterBlock getValues()
  throws ConstraintException, NumberFormatException
  {
    ACISParameterBlock result = new ACISParameterBlock();

    String exposureMode = (String) exposureModeComboBox.getSelectedItem();

    result.setExposureMode(exposureMode);

    // Standard Chips
    String standardChips = 
      (String) standardChipsPanel.getSelectedItem();

    result.setStandardChips(standardChips);

    if ((standardChips != null) && standardChips.equals(Constants.NO) )
    {
      result.setI0( (String) i0ComboBox.getSelectedObject() );
      result.setI1( (String) i1ComboBox.getSelectedObject() );
      result.setI2( (String) i2ComboBox.getSelectedObject() );
      result.setI3( (String) i3ComboBox.getSelectedObject() );
                                                  
      result.setS0( (String) s0ComboBox.getSelectedObject() );
      result.setS1( (String) s1ComboBox.getSelectedObject() );
      result.setS2( (String) s2ComboBox.getSelectedObject() );
      result.setS3( (String) s3ComboBox.getSelectedObject() );
      result.setS4( (String) s4ComboBox.getSelectedObject() );
      result.setS5( (String) s5ComboBox.getSelectedObject() );
    }

    // Subarray
    result.setSubarray( (String) subarrayComboBox.getSelectedObject() );

    String subarrayStartRow = subarrayStartRowTextField.getText();

    if (! subarrayStartRow.equals("") )
    {
      result.setSubarrayStartRow( Integer.valueOf(subarrayStartRow) );
    }

    String subarrayRowCount = subarrayRowCountTextField.getText();

    if (! subarrayRowCount.equals("") )
    {
      result.setSubarrayRowCount( Integer.valueOf(subarrayRowCount) );
    }

    String subarrayFrameTime = subarrayFrameTimeTextField.getText();

    if (! subarrayFrameTime.equals("") )
    {
      result.setSubarrayFrameTime( Double.valueOf(subarrayFrameTime) );
    }

    // Duty Cycle
    String dutyCycle = (String) dutyCyclePanel.getSelectedItem();

    result.setDutyCycle(dutyCycle);

    String secondaryExposureCount = secondaryExposureCountTextField.getText();

    if (! secondaryExposureCount.equals("") )
    {
      result.setSecondaryExposureCount( Integer.valueOf(secondaryExposureCount) );
    }

    String primaryExposureTime = primaryExposureTimeTextField.getText();

    if (! primaryExposureTime.equals("") )
    {
      result.setPrimaryExposureTime( Double.valueOf(primaryExposureTime) );
    }

    String secondaryExposureTime = secondaryExposureTimeTextField.getText();

    if (! secondaryExposureTime.equals("") )
    {
      result.setSecondaryExposureTime( Double.valueOf(secondaryExposureTime) );
    }

    result.setBEPPack( (String) bepPackComboBox.getSelectedItem() );

    String frameTime = frameTimeTextField.getText();

    if (! frameTime.equals("") )
    {
      result.setFrameTime( Double.valueOf(frameTime) );
    }

    // On-chip Summing
    String onChipSumming = (String) onChipSummingPanel.getSelectedItem();

    result.setOnChipSumming(onChipSumming);

    String onChipRowCount = onChipRowCountTextField.getText();

    if (! onChipRowCount.equals("") )
    {
      result.setOnChipRowCount( Integer.valueOf(onChipRowCount) );
    }

    String onChipColumnCount = onChipColumnCountTextField.getText();

    if (! onChipColumnCount.equals("") )
    {
      result.setOnChipColumnCount( Integer.valueOf(onChipColumnCount) );
    }

    // Event Threshold
    String eventThreshold = (String) eventThresholdPanel.getSelectedItem();

    result.setEventThreshold(eventThreshold);

    String thresholdPhase = thresholdPhaseTextField.getText();

    if (! thresholdPhase.equals("") )
    {
      result.setThresholdPhase( Double.valueOf(thresholdPhase) );
    }

    // Event Filter
    String eventFilter = (String) eventFilterPanel.getSelectedItem();
      
    result.setEventFilter(eventFilter);

    String eventFilterLower = eventFilterLowerTextField.getText();

    if (! eventFilterLower.equals("") )
    {
      result.setEventFilterLower( Double.valueOf(eventFilterLower) );
    }

    String eventFilterHigher = eventFilterHigherTextField.getText();

    if (! eventFilterHigher.equals("") )
    {
      result.setEventFilterHigher( Double.valueOf(eventFilterHigher) );
    }

    // Spatial Window
    String spatialWindow = (String) spatialWindowPanel.getSelectedItem();

    result.setSpatialWindow(spatialWindow);

    String spatialWindowStartRow = spatialWindowStartRowTextField.getText();

    if (! spatialWindowStartRow.equals("") )
    {
      result.setSpatialWindowStartRow( Integer.valueOf(spatialWindowStartRow) );
    }

    String spatialWindowStartColumn = spatialWindowStartColumnTextField.getText();

    if (! spatialWindowStartColumn.equals("") )
    {
      result.setSpatialWindowStartColumn( Integer.valueOf(spatialWindowStartColumn) );
    }

    String spatialWindowSample = spatialWindowSampleTextField.getText();

    if (! spatialWindowSample.equals("") )
    {
      result.setSpatialWindowSample( Integer.valueOf(spatialWindowSample) );
    }

    String spatialWindowHeight = spatialWindowHeightTextField.getText();

    if (! spatialWindowHeight.equals("") )
    {
      result.setSpatialWindowHeight( Integer.valueOf(spatialWindowHeight) );
    }

    String spatialWindowWidth = spatialWindowWidthTextField.getText();

    if (! spatialWindowWidth.equals("") )
    {
      result.setSpatialWindowWidth(Integer.valueOf(spatialWindowWidth) );
    }

    String spatialWindowLowerThreshold = spatialWindowLowerThresholdTextField.getText();

    if (! spatialWindowLowerThreshold.equals("") )
    {
      result.setSpatialWindowLowerThreshold( Double.valueOf(spatialWindowLowerThreshold) );
    }

    String spatialWindowPhaseRange = spatialWindowPhaseRangeTextField.getText();

    if (! spatialWindowPhaseRange.equals("") )
    {
      result.setSpatialWindowPhaseRange( Double.valueOf(spatialWindowPhaseRange) );
    }

    // Bias
    result.setBiasRequest( (String) biasRequestPanel.getSelectedItem() );
   
    String frequency = frequencyTextField.getText();

    if (! frequency.equals("") )
    {
      result.setFrequency( Double.valueOf(frequency) );
    }
    
    result.setBiasAfter( (String) biasAfterComboBox.getSelectedObject() );

    result.validate();

    return(result);
  }

  /****************************************************************************/

  public void init()
  {
    setTitle("ACIS Form");
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    Container contentPane = getContentPane();
    
    contentPane.setLayout( new GridBagLayout() );

    JLabel exposureModeLabel = new JLabel("Exposure Mode", JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(contentPane, exposureModeLabel,
                                   0, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.25, 0.0, 5, 5, 5, 5);

    exposureModeComboBox = 
      new JComboBox( ACISParameterBlock.getExposureModes() );

    GridBagLayoutUtil.addComponent(contentPane, exposureModeComboBox,
                                   1, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.25, 0.0, 5, 5, 5, 5);

    standardChipsPanel = initStandardChipsPanel();

    GridBagLayoutUtil.addComponent(contentPane, standardChipsPanel, 
                                   0, 1, 4, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    JPanel subarrayPanel = initSubarrayPanel();

    GridBagLayoutUtil.addComponent(contentPane, subarrayPanel, 
                                   0, 2, 4, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER,
                                   1.0, 0.0, 5, 5, 5, 5);

    dutyCyclePanel = initDutyCyclePanel();

    GridBagLayoutUtil.addComponent(contentPane, dutyCyclePanel, 
                                   0, 3, 4, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER,
                                   1.0, 0.0, 5, 5, 5, 5);

    JPanel otherPanel = initOtherPanel();

    GridBagLayoutUtil.addComponent(contentPane, otherPanel, 
                                   0, 4, 4, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER,
                                   1.0, 0.0, 5, 5, 5, 5);

    onChipSummingPanel = initOnChipSummingPanel();

    GridBagLayoutUtil.addComponent(contentPane, onChipSummingPanel, 
                                   0, 5, 4, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER,
                                   1.0, 0.0, 5, 5, 5, 5);

    eventThresholdPanel = initEventThresholdPanel();

    GridBagLayoutUtil.addComponent(contentPane, eventThresholdPanel, 
                                   0, 6, 4, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER,
                                   1.0, 0.0, 5, 5, 5, 5);

    eventFilterPanel = initEventFilterPanel();

    GridBagLayoutUtil.addComponent(contentPane, eventFilterPanel, 
                                   0, 7, 4, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER,
                                   1.0, 0.0, 5, 5, 5, 5);

    spatialWindowPanel = initSpatialWindowPanel();

    GridBagLayoutUtil.addComponent(contentPane, spatialWindowPanel, 
                                   0, 8, 4, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER,
                                   1.0, 0.0, 5, 5, 5, 5);

    biasRequestPanel = initBiasPanel();

    GridBagLayoutUtil.addComponent(contentPane, biasRequestPanel, 
                                   0, 9, 4, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER,
                                   1.0, 0.0, 5, 5, 5, 5);

    JPanel buttonPanel = initButtonPanel();

    GridBagLayoutUtil.addComponent(contentPane, buttonPanel, 
                                   0, 10, 4, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.CENTER,
                                   1.0, 0.0, 5, 5, 5, 5);
  }

  /****************************************************************************/

  private ComboBoxBorderPanel initBiasPanel()
  {
    ComboBoxModel model = 
      new ComboBoxModel( Constants.getBooleans() );

    ComboBoxBorderPanel result = 
      new ComboBoxBorderPanel("Bias", model, "Y");

    Container contentPane = result.getContentPane();

    contentPane.setLayout( new GridBagLayout() );

    JLabel frequencyLabel = new JLabel("Freq", JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(contentPane, frequencyLabel,
                                   0, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    frequencyTextField = new JTextField(3);

    GridBagLayoutUtil.addComponent(contentPane, frequencyTextField,
                                   1, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel biasAfterLabel = new JLabel("After", JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(contentPane, biasAfterLabel,
                                   2, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    ComboBoxModel biasAfterModel =
      new ComboBoxModel( Constants.getBooleans() );

    biasAfterComboBox = new ComboBox(biasAfterModel);

    GridBagLayoutUtil.addComponent(contentPane, biasAfterComboBox,
                                   3, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    return(result);
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

    JButton clearButton = new JButton("Clear");

    clearButton.addActionListener( new ClearButtonListener(this) );

    GridBagLayoutUtil.addComponent(result, clearButton,
                                   1, 0, 1, 1,
                                   GridBagConstraints.BOTH,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    JButton cancelButton = new JButton("Cancel");

    cancelButton.addActionListener( new CancelButtonListener(this) );

    GridBagLayoutUtil.addComponent(result, cancelButton,
                                   2, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    return(result);
  }

  /****************************************************************************/

  private ComboBoxBorderPanel initDutyCyclePanel()
  {
    ComboBoxModel model = 
      new ComboBoxModel( Constants.getBooleans() );

    ComboBoxBorderPanel result = 
      new ComboBoxBorderPanel("Duty Cycle", model, "Y");

    Container contentPane = result.getContentPane();

    contentPane.setLayout( new GridBagLayout() );

    JLabel secondaryExposureCountLabel = new JLabel("Number");

    GridBagLayoutUtil.addComponent(contentPane, secondaryExposureCountLabel,
                                   0, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    secondaryExposureCountTextField = new JTextField(2);

    GridBagLayoutUtil.addComponent(contentPane, 
                                   secondaryExposureCountTextField,
                                   1, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel primaryExposureTimeLabel = new JLabel("Time Primary");

    GridBagLayoutUtil.addComponent(contentPane, primaryExposureTimeLabel,
                                   2, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    primaryExposureTimeTextField = new JTextField(4);

    GridBagLayoutUtil.addComponent(contentPane, primaryExposureTimeTextField,
                                   3, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel secondaryExposureTimeLabel = new JLabel("Time Secondary");

    GridBagLayoutUtil.addComponent(contentPane, secondaryExposureTimeLabel,
                                   4, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    secondaryExposureTimeTextField = new JTextField(4);

    GridBagLayoutUtil.addComponent(contentPane, 
                                   secondaryExposureTimeTextField,
                                   5, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    return(result);
  }

  /****************************************************************************/

  private ComboBoxBorderPanel initEventFilterPanel()
  {
    ComboBoxModel model = 
      new ComboBoxModel( Constants.getBooleans() );

    ComboBoxBorderPanel result = 
      new ComboBoxBorderPanel("Event Filter", model, "Y");

    Container contentPane = result.getContentPane();

    contentPane.setLayout( new GridBagLayout() );

    JLabel eventFilterLowerLabel = new JLabel("Lower");

    GridBagLayoutUtil.addComponent(contentPane, eventFilterLowerLabel,
                                   0, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    eventFilterLowerTextField = new JTextField(4);

    GridBagLayoutUtil.addComponent(contentPane, eventFilterLowerTextField,
                                   1, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel eventFilterHigherLabel = new JLabel("Range");

    GridBagLayoutUtil.addComponent(contentPane, eventFilterHigherLabel,
                                   2, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    eventFilterHigherTextField = new JTextField(4);

    GridBagLayoutUtil.addComponent(contentPane, eventFilterHigherTextField,
                                   3, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    return(result);
  }

  /****************************************************************************/

  private ComboBoxBorderPanel initEventThresholdPanel()
  {
    ComboBoxModel model = 
      new ComboBoxModel( Constants.getBooleans() );

    ComboBoxBorderPanel result = 
      new ComboBoxBorderPanel("Event Threshold", model, "Y");

    Container contentPane = result.getContentPane();

    contentPane.setLayout( new GridBagLayout() );

    JLabel thresholdPhaseLabel = new JLabel("Threshold");

    GridBagLayoutUtil.addComponent(contentPane, thresholdPhaseLabel,
                                   0, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    thresholdPhaseTextField = new JTextField(4);

    GridBagLayoutUtil.addComponent(contentPane, thresholdPhaseTextField,
                                   1, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    return(result);
  }

  /****************************************************************************/

  private ComboBoxBorderPanel initOnChipSummingPanel()
  {
    ComboBoxModel model = 
      new ComboBoxModel( Constants.getBooleans() );

    ComboBoxBorderPanel result = 
      new ComboBoxBorderPanel("On-chip Summing", model, "Y");

    Container contentPane = result.getContentPane();

    contentPane.setLayout( new GridBagLayout() );

    JLabel onChipRowCountLabel = new JLabel("Rows");

    GridBagLayoutUtil.addComponent(contentPane, onChipRowCountLabel,
                                   0, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    onChipRowCountTextField = new JTextField(4);

    GridBagLayoutUtil.addComponent(contentPane, 
                                   onChipRowCountTextField,
                                   1, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel onChipColumnCountLabel = new JLabel("Columns");

    GridBagLayoutUtil.addComponent(contentPane, onChipColumnCountLabel,
                                   2, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    onChipColumnCountTextField = new JTextField(4);

    GridBagLayoutUtil.addComponent(contentPane, onChipColumnCountTextField,
                                   3, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    return(result);
  }

  /****************************************************************************/

  public JPanel initOtherPanel()
  {
    JPanel result = new JPanel();

    result.setLayout( new GridBagLayout() );

    JLabel bepPackLabel = new JLabel("BEP Pack", JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(result, bepPackLabel, 
                                   0, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    bepPackComboBox = new JComboBox( ACISParameterBlock.getBEPPacks() );

    GridBagLayoutUtil.addComponent(result, bepPackComboBox, 
                                   1, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel frameTimeLabel = new JLabel("Frame Time", JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(result, frameTimeLabel, 
                                   2, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    frameTimeTextField = new JTextField(8);

    GridBagLayoutUtil.addComponent(result, frameTimeTextField,
                                   3, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    return(result);
  }

  /****************************************************************************/

  private ComboBoxBorderPanel initSpatialWindowPanel()
  {
    ComboBoxModel model = 
      new ComboBoxModel( Constants.getBooleans() );

    ComboBoxBorderPanel result = 
      new ComboBoxBorderPanel("Spatial Window", model, "Y");

    Container contentPane = result.getContentPane();

    contentPane.setLayout( new GridBagLayout() );

    JLabel spatialWindowStartRowLabel = new JLabel("Start Row", JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(contentPane, spatialWindowStartRowLabel,
                                   0, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);
    
    spatialWindowStartRowTextField = new JTextField(3);

    GridBagLayoutUtil.addComponent(contentPane, spatialWindowStartRowTextField,
                                   1, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel spatialWindowStartColumnLabel = 
      new JLabel("Start Column", JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(contentPane, spatialWindowStartColumnLabel,
                                   2, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    spatialWindowStartColumnTextField = new JTextField(3);

    GridBagLayoutUtil.addComponent(contentPane, 
                                   spatialWindowStartColumnTextField,
                                   3, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel spatialWindowSampleLabel = new JLabel("Sample Rate", JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(contentPane, spatialWindowSampleLabel,
                                   4, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    spatialWindowSampleTextField = new JTextField(4);

    GridBagLayoutUtil.addComponent(contentPane, spatialWindowSampleTextField,
                                   5, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel spatialWindowHeightLabel = new JLabel("Height", JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(contentPane, spatialWindowHeightLabel,
                                   0, 1, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    spatialWindowHeightTextField = new JTextField(4);

    GridBagLayoutUtil.addComponent(contentPane, spatialWindowHeightTextField,
                                   1, 1, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel spatialWindowWidthLabel = new JLabel("Width", JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(contentPane, spatialWindowWidthLabel,
                                   2, 1, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    spatialWindowWidthTextField = new JTextField(4);

    GridBagLayoutUtil.addComponent(contentPane, spatialWindowWidthTextField,
                                   3, 1, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel spatialWindowLowerThresholdLabel = new JLabel("Lower Energy", 
                                                         JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(contentPane, 
                                   spatialWindowLowerThresholdLabel,
                                   0, 2, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    spatialWindowLowerThresholdTextField = new JTextField(4);

    GridBagLayoutUtil.addComponent(contentPane, 
                                   spatialWindowLowerThresholdTextField,
                                   1, 2, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel spatialWindowPhaseRangeLabel = new JLabel("Energy Range", 
                                                     JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(contentPane, spatialWindowPhaseRangeLabel,
                                   2, 2, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    spatialWindowPhaseRangeTextField = new JTextField(4);

    GridBagLayoutUtil.addComponent(contentPane, 
                                   spatialWindowPhaseRangeTextField,
                                   3, 2, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    return(result);
  }

  /****************************************************************************/

  private ComboBoxBorderPanel initStandardChipsPanel()
  {
    ComboBoxModel model = 
      new ComboBoxModel( Constants.getBooleans() );

    ComboBoxBorderPanel result = 
      new ComboBoxBorderPanel("Standard Chips", model, "N");

    Container contentPane = result.getContentPane();

    contentPane.setLayout( new GridBagLayout() );
    
    JLabel i0Label = new JLabel("I0");

    GridBagLayoutUtil.addComponent(contentPane, i0Label,
                                   0, 0, 5, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    ComboBoxModel i0Model =
      new ComboBoxModel( Constants.getBooleans() );

    i0ComboBox = new ComboBox(i0Model);

    GridBagLayoutUtil.addComponent(contentPane, i0ComboBox,
                                   5, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel i1Label = new JLabel("I1");

    GridBagLayoutUtil.addComponent(contentPane, i1Label,
                                   6, 0, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    ComboBoxModel i1Model =
      new ComboBoxModel( Constants.getBooleans() );

    i1ComboBox = new ComboBox(i1Model);

    GridBagLayoutUtil.addComponent(contentPane, i1ComboBox,
                                   7, 0, 5, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel i2Label = new JLabel("I2");

    GridBagLayoutUtil.addComponent(contentPane, i2Label,
                                   0, 1, 5, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);
  
    ComboBoxModel i2Model =
      new ComboBoxModel( Constants.getBooleans() );

    i2ComboBox = new ComboBox(i2Model);

    GridBagLayoutUtil.addComponent(contentPane, i2ComboBox,
                                   5, 1, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);
  
    JLabel i3Label = new JLabel("I3");

    GridBagLayoutUtil.addComponent(contentPane, i3Label,
                                   6, 1, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    ComboBoxModel i3Model =
      new ComboBoxModel( Constants.getBooleans() );

    i3ComboBox = new ComboBox(i3Model);

    GridBagLayoutUtil.addComponent(contentPane, i3ComboBox,
                                   7, 1, 5, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel s0Label = new JLabel("S0");

    GridBagLayoutUtil.addComponent(contentPane, s0Label,
                                   0, 2, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    ComboBoxModel s0Model =
      new ComboBoxModel( Constants.getBooleans() );

    s0ComboBox = new ComboBox(s0Model);

    GridBagLayoutUtil.addComponent(contentPane, s0ComboBox,
                                   1, 2, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel s1Label = new JLabel("S1");

    GridBagLayoutUtil.addComponent(contentPane, s1Label,
                                   2, 2, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    ComboBoxModel s1Model =
      new ComboBoxModel( Constants.getBooleans() );

    s1ComboBox = new ComboBox(s1Model);

    GridBagLayoutUtil.addComponent(contentPane, s1ComboBox,
                                   3, 2, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel s2Label = new JLabel("S2");

    GridBagLayoutUtil.addComponent(contentPane, s2Label,
                                   4, 2, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    ComboBoxModel s2Model =
      new ComboBoxModel( Constants.getBooleans() );

    s2ComboBox = new ComboBox(s2Model);

    GridBagLayoutUtil.addComponent(contentPane, s2ComboBox,
                                   5, 2, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel s3Label = new JLabel("S3");

    GridBagLayoutUtil.addComponent(contentPane, s3Label,
                                   6, 2, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    ComboBoxModel s3Model =
      new ComboBoxModel( Constants.getBooleans() );

    s3ComboBox = new ComboBox(s3Model);

    GridBagLayoutUtil.addComponent(contentPane, s3ComboBox,
                                   7, 2, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel s4Label = new JLabel("S4");

    GridBagLayoutUtil.addComponent(contentPane, s4Label,
                                   8, 2, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    ComboBoxModel s4Model =
      new ComboBoxModel( Constants.getBooleans() );

    s4ComboBox = new ComboBox(s4Model);

    GridBagLayoutUtil.addComponent(contentPane, s4ComboBox,
                                   9, 2, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel s5Label = new JLabel("S5");

    GridBagLayoutUtil.addComponent(contentPane, s5Label,
                                   10, 2, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    ComboBoxModel s5Model =
      new ComboBoxModel( Constants.getBooleans() );

    s5ComboBox = new ComboBox(s5Model);

    GridBagLayoutUtil.addComponent(contentPane, s5ComboBox,
                                   11, 2, 1, 1,
                                   GridBagConstraints.NONE,
                                   GridBagConstraints.CENTER,
                                   0.0, 0.0, 5, 5, 5, 5);

    return(result);
  }

  /****************************************************************************/

  private JPanel initSubarrayPanel()
  {
    JPanel result = new JPanel();

    result.setBorder( new TitledBorder("Subarray") );
    result.setLayout( new GridBagLayout() );

    JLabel subarrayLabel = new JLabel("Type", JLabel.RIGHT);
    
    GridBagLayoutUtil.addComponent(result, subarrayLabel,
                                   0, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    ComboBoxModel model =
      new ComboBoxModel( ACISParameterBlock.getSubarrays() );

    subarrayComboBox = new ComboBox(model);

    subarrayComboBox.addItemListener( new ComboBoxListener(this) );

    GridBagLayoutUtil.addComponent(result, subarrayComboBox,
                                   1, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    subarrayStartRowLabel = new JLabel("Start Row", JLabel.RIGHT);
    
    GridBagLayoutUtil.addComponent(result, subarrayStartRowLabel,
                                   2, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    subarrayStartRowTextField = new JTextField(3);

    GridBagLayoutUtil.addComponent(result, subarrayStartRowTextField,
                                   3, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    subarrayRowCountLabel = new JLabel("No. Rows", JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(result, subarrayRowCountLabel,
                                   4, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    subarrayRowCountTextField = new JTextField(4);

    GridBagLayoutUtil.addComponent(result, subarrayRowCountTextField,
                                   5, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    JLabel subarrayFrameTimeLabel = new JLabel("Frame Time", JLabel.RIGHT);

    GridBagLayoutUtil.addComponent(result, subarrayFrameTimeLabel,
                                   6, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.EAST,
                                   0.0, 0.0, 5, 5, 5, 5);

    subarrayFrameTimeTextField = new JTextField(4);

    GridBagLayoutUtil.addComponent(result, subarrayFrameTimeTextField,
                                   7, 0, 1, 1,
                                   GridBagConstraints.HORIZONTAL,
                                   GridBagConstraints.WEST,
                                   0.0, 0.0, 5, 5, 5, 5);

    // This triggers stuff that depends all the subarray
    // components being created already.  Must do these at the end.
    subarrayComboBox.setSelectedItem(Constants.NO);

    return(result);
  }

  /****************************************************************************/

  public static void main(String[] args)
  {
    ACISForm form = new ACISForm(null);

    form.show();
  }

  /****************************************************************************/

  public void ok()
  {
    listener.completed();
  }

  /****************************************************************************/
  
  public void setValues(ACISParameterBlock parameterBlock)
  {
    exposureModeComboBox.setSelectedItem( parameterBlock.getExposureMode() );

    // Standard Chips
    String standardChips = parameterBlock.getStandardChips();

    standardChipsPanel.setSelectedItem(standardChips);

    if ((standardChips != null) && standardChips.equals(Constants.YES) )
    {
      // Fix this!!!
      i0ComboBox.setSelectedItem(Constants.YES);
      i1ComboBox.setSelectedItem(Constants.YES);
      i2ComboBox.setSelectedItem(Constants.YES);
      i3ComboBox.setSelectedItem(Constants.YES);
                              
      s0ComboBox.setSelectedItem(Constants.YES);
      s1ComboBox.setSelectedItem(Constants.YES);
      s2ComboBox.setSelectedItem(Constants.YES);
      s3ComboBox.setSelectedItem(Constants.YES);
      s4ComboBox.setSelectedItem(Constants.YES);
      s5ComboBox.setSelectedItem(Constants.YES);
    }
    else
    {
      i0ComboBox.setSelectedItem( parameterBlock.getI0() );
      i1ComboBox.setSelectedItem( parameterBlock.getI1() );
      i2ComboBox.setSelectedItem( parameterBlock.getI2() );
      i3ComboBox.setSelectedItem( parameterBlock.getI3() );

      s0ComboBox.setSelectedItem( parameterBlock.getS0() );
      s1ComboBox.setSelectedItem( parameterBlock.getS1() );
      s2ComboBox.setSelectedItem( parameterBlock.getS2() );
      s3ComboBox.setSelectedItem( parameterBlock.getS3() );
      s4ComboBox.setSelectedItem( parameterBlock.getS4() );
      s5ComboBox.setSelectedItem( parameterBlock.getS5() );
    }

    // Subarray
    String subarray = parameterBlock.getSubarray();

    subarrayComboBox.setSelectedItem(subarray);

    Integer subarrayStartRow = parameterBlock.getSubarrayStartRow();

    if (subarrayStartRow != null)
    {
      subarrayStartRowTextField.setText( subarrayStartRow.toString() );
    }

    Integer subarrayRowCount = parameterBlock.getSubarrayRowCount();

    if (subarrayRowCount != null)
    {
      subarrayRowCountTextField.setText( subarrayRowCount.toString() );
    }

    Double subarrayFrameTime = parameterBlock.getSubarrayFrameTime();
    
    if (subarrayFrameTime != null)
    {
      subarrayFrameTimeTextField.setText( subarrayFrameTime.toString() );
    }

    // Duty Cycle
    String dutyCycle = parameterBlock.getDutyCycle();

    dutyCyclePanel.setSelectedItem(dutyCycle);

    Integer secondaryExposureCount = parameterBlock.getSecondaryExposureCount();

    if (secondaryExposureCount != null)
    {
      secondaryExposureCountTextField.setText( secondaryExposureCount.toString() );
    }

    Double primaryExposureTime = parameterBlock.getPrimaryExposureTime();
    
    if (primaryExposureTime != null)
    {
      primaryExposureTimeTextField.setText( primaryExposureTime.toString() );
    }

    Double secondaryExposureTime = parameterBlock.getSecondaryExposureTime();

    if (secondaryExposureTime != null)
    {
      secondaryExposureTimeTextField.setText( secondaryExposureTime.toString() );
    }

    bepPackComboBox.setSelectedItem( parameterBlock.getBEPPack() );

    Double frameTime = parameterBlock.getFrameTime();

    if (frameTime != null)
    {
      frameTimeTextField.setText( frameTime.toString() );
    }

    // On-chip Summing
    String onChipSumming = parameterBlock.getOnChipSumming();

    onChipSummingPanel.setSelectedItem(onChipSumming);

    Integer onChipRowCount = parameterBlock.getOnChipRowCount();
      
    if (onChipRowCount != null)
    {
      onChipRowCountTextField.setText( onChipRowCount.toString() );
    }

    Integer onChipColumnCount = parameterBlock.getOnChipColumnCount();
    
    if (onChipColumnCount != null)
    {
      onChipColumnCountTextField.setText( onChipColumnCount.toString() );
    }

    // Event Threshold
    String eventThreshold = parameterBlock.getEventThreshold();

    eventThresholdPanel.setSelectedItem(eventThreshold);

    Double thresholdPhase = parameterBlock.getThresholdPhase();

    if (thresholdPhase != null)
    {
      thresholdPhaseTextField.setText( thresholdPhase.toString() );
    }

    // Event Filter
    String eventFilter = parameterBlock.getEventFilter();

    eventFilterPanel.setSelectedItem(eventFilter);

    Double eventFilterLower = parameterBlock.getEventFilterLower();

    if (eventFilterLower != null)
    {
      eventFilterLowerTextField.setText( eventFilterLower.toString() );
    }

    Double eventFilterHigher = parameterBlock.getEventFilterHigher();

    if (eventFilterHigher != null)
    {
      eventFilterHigherTextField.setText( eventFilterHigher.toString() );
    }

    // Spatial Window
    String spatialWindow = parameterBlock.getSpatialWindow();

    spatialWindowPanel.setSelectedItem(spatialWindow);

    Integer spatialWindowStartRow = parameterBlock.getSpatialWindowStartRow();

    if (spatialWindowStartRow != null)
    {
      spatialWindowStartRowTextField.setText( spatialWindowStartRow.toString() );
    }

    Integer spatialWindowStartColumn = parameterBlock.getSpatialWindowStartColumn();
    
    if (spatialWindowStartColumn != null)
    {
      spatialWindowStartColumnTextField.setText(spatialWindowStartColumn.toString());
    }
    
    Integer spatialWindowSample = parameterBlock.getSpatialWindowSample();

    if (spatialWindowSample != null)
    {
      spatialWindowSampleTextField.setText( spatialWindowSample.toString() );
    }

    Integer spatialWindowHeight = parameterBlock.getSpatialWindowHeight();

    if (spatialWindowHeight != null)
    {
      spatialWindowHeightTextField.setText( spatialWindowHeight.toString() );
    }

    Integer spatialWindowWidth = parameterBlock.getSpatialWindowWidth();

    if (spatialWindowWidth != null)
    {
      spatialWindowWidthTextField.setText( spatialWindowWidth.toString() );
    }

    Double spatialWindowLowerThreshold = 
      parameterBlock.getSpatialWindowLowerThreshold();

    if (spatialWindowLowerThreshold != null)
    {
      spatialWindowLowerThresholdTextField.setText( spatialWindowLowerThreshold.toString() );
    }

    Double spatialWindowPhaseRange = parameterBlock.getSpatialWindowPhaseRange();

    if (spatialWindowPhaseRange != null)
    {
      spatialWindowPhaseRangeTextField.setText( spatialWindowPhaseRange.toString() );
    }

    // Bias
    String biasRequest = parameterBlock.getBiasRequest();

    biasRequestPanel.setSelectedItem(biasRequest);

    Double frequency = parameterBlock.getFrequency();

    if (frequency != null)
    {
      frequencyTextField.setText( frequency.toString() );
    }

    String biasAfter = parameterBlock.getBiasAfter();

    biasAfterComboBox.setSelectedItem(biasAfter);
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
