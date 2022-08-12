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

package gov.sao.asc.obsed.view;

/******************************************************************************/

import gov.sao.asc.event.CancelButtonListener;
import gov.sao.asc.event.CancelInterface;
import gov.sao.asc.event.ClearButtonListener;
import gov.sao.asc.event.ClearInterface;
import gov.sao.asc.event.OKButtonListener;
import gov.sao.asc.event.OKInterface;
import gov.sao.asc.obsed.database.ColumnEntry;
import gov.sao.asc.obsed.database.Condition;
import gov.sao.asc.obsed.database.DatabaseConfiguration;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.database.Filter;
import gov.sao.asc.obsed.view.ConfigurationException;
import gov.sao.asc.obsed.view.event.AndButtonListener;
import gov.sao.asc.obsed.view.event.AndInterface;
import gov.sao.asc.obsed.view.event.FilteredButtonListener;
import gov.sao.asc.obsed.view.event.FilteredInterface;
import gov.sao.asc.obsed.view.event.OrButtonListener;
import gov.sao.asc.obsed.view.event.OrInterface;
import gov.sao.asc.obsed.view.event.UnfilteredButtonListener;
import gov.sao.asc.obsed.view.event.UnfilteredInterface;
import gov.sao.asc.util.ComponentUtil;
import gov.sao.asc.util.GridBagLayoutUtil;
import gov.sao.asc.util.LogClient;
import gov.sao.asc.util.Pair;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.sql.Types;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

/******************************************************************************/
/**
 *
 * @author Paul Michael Reilly
 * @author Paul Matthew Reilly
 *
 * @version %I%, %G%
 */

public class FilterDialog extends JDialog
  implements AndInterface, CancelInterface, ClearInterface, FilteredInterface, 
             OKInterface, OrInterface, UnfilteredInterface
{

  protected int rowIndex;
  protected Configuration configuration;
  protected Filter filter;
  protected JPanel filterPanel;
  protected JPanel view;
  protected JRadioButton filteredButton;
  protected JViewport viewport;
  protected Vector<JComboBox> columnComboBoxes;
  protected Vector<Integer> combinationOperators;
  protected Vector<JComboBox> conditionOperatorComboBoxes;
  protected Vector<JTextField> valueTextFields;

  private boolean cancelled;
  private JButton andButton;
  private JButton clearButton;
  private JButton orButton;

  /****************************************************************************/
  
  public FilterDialog( JFrame owner, Configuration configuration )
  {
    super( owner, "Filter Selector", true );

    this.configuration = configuration;

    rowIndex = 1;

    columnComboBoxes = new Vector<JComboBox>();
    conditionOperatorComboBoxes = new Vector<JComboBox>();
    valueTextFields = new Vector<JTextField>();
    combinationOperators = new Vector<Integer>();

    init();

    unfiltered();
  }

  /****************************************************************************/

  public void and()
  {
    resizeViewport();

    combinationOperators.addElement( new Integer( Condition.AND ) );

    GridBagLayoutUtil.addComponent(view, new JLabel("AND"),
				   1, rowIndex++, 1, 1,
				   GridBagConstraints.NONE, 
                                   GridBagConstraints.WEST,
				   0.0, 0.0, 5, 5, 5, 5);

    JPanel constraintPanel = createConstraintPanel();

    GridBagLayoutUtil.addComponent(view, constraintPanel,
				   0, rowIndex, 1, 1,
				   GridBagConstraints.NONE, 
                                   GridBagConstraints.WEST,
				   0.0, 0.0, 5, 5, 5, 5);

    view.validate();
  }

  /****************************************************************************/

  public void cancel()
  {
    cancelled = true;
    dismiss();
  }

  /****************************************************************************/
  
  public void checkFilter()
    throws ConfigurationException, FilterException
  {
    this.filter = checkFilter( new Filter() );
  }

  /****************************************************************************/
  
  protected Filter checkFilter( Filter filter )
    throws ConfigurationException, FilterException
  {
    for ( int i = 0; i < columnComboBoxes.size(); i++ )
    {
      JComboBox columnComboBox = (JComboBox) columnComboBoxes.elementAt(i);

      Pair pair = (Pair) columnComboBox.getSelectedItem();

      DatabaseKey databaseKey = (DatabaseKey) pair.getKey();

      JComboBox conditionOperatorComboBox = 
        (JComboBox) conditionOperatorComboBoxes.elementAt( i );

      int conditionOperator = conditionOperatorComboBox.getSelectedIndex();

      JTextField valueTextField = (JTextField) valueTextFields.elementAt( i );

      String value = valueTextField.getText();

      DatabaseConfiguration databaseConfiguration = 
        DatabaseConfiguration.getInstance();

      ColumnEntry columnEntry = 
        databaseConfiguration.getColumnEntry( databaseKey );

      int sqlType = columnEntry.getSQLType();

      switch ( sqlType )
      {
      case ( Types.CHAR ):
      case ( Types.VARCHAR ):
      case ( Types.LONGVARCHAR ):
      case ( Types.DATE ):
      case ( Types.TIME ):
      case ( Types.TIMESTAMP ):
        {
          break;
        }
      default:
        {
          if ( value.equals("") )
          {
            String message = "An blank value is not allowed with this column.";

            throw ( new FilterException( message ) );
          }
          break;
        }
      }

      Condition condition;
      
      if ( i < combinationOperators.size() )
      {
        Integer combinationOperator = 
          (Integer) combinationOperators.elementAt( i );
        
        condition = new Condition( databaseKey, conditionOperator, value, 
                                   sqlType, combinationOperator.intValue() );
      }
      else
      {
        condition = new Condition( databaseKey, conditionOperator, value, sqlType );
      }

      filter.addCondition( condition );
    }

    return( filter );
  }

  /****************************************************************************/

  public void clear()
  {
    rowIndex = 0;

    columnComboBoxes.removeAllElements();
    conditionOperatorComboBoxes.removeAllElements();
    valueTextFields.removeAllElements();
    combinationOperators.removeAllElements();

    view.removeAll();

    JPanel constraintPanel = createConstraintPanel();

    GridBagLayoutUtil.addComponent(view, constraintPanel,
				   0, rowIndex, 1, 1,
				   GridBagConstraints.NONE, 
                                   GridBagConstraints.WEST,
				   0.0, 0.0, 5, 5, 5, 5);

    // Pushes the constraintPanel's into the top left corner.
    GridBagLayoutUtil.addComponent(view, Box.createGlue(),
				   3, 100, 1, 1,
				   GridBagConstraints.BOTH, 
                                   GridBagConstraints.CENTER,
				   1.0, 1.0, 5, 5, 5, 5);

    view.repaint();
    view.validate();
  }

  /****************************************************************************/

  public JPanel createConstraintPanel()
  {
    JPanel result = new JPanel();

    result.setLayout( new GridBagLayout() );

    JComboBox columnComboBox = new JComboBox( configuration.getColumnNames() );

    columnComboBoxes.addElement( columnComboBox );

    GridBagLayoutUtil.addComponent(result, columnComboBox,
				   0, 1, 1, 1,
				   GridBagConstraints.NONE, 
                                   GridBagConstraints.CENTER,
				   0.0, 0.0, 5, 5, 5, 5);

    JComboBox conditionOperatorComboBox = 
      new JComboBox( Condition.getExpressions() );

    conditionOperatorComboBoxes.addElement( conditionOperatorComboBox );

    GridBagLayoutUtil.addComponent(result, conditionOperatorComboBox,
				   1, 1, 1, 1,
				   GridBagConstraints.NONE, 
                                   GridBagConstraints.CENTER,
				   0.0, 0.0, 5, 5, 5, 5);

    JTextField valueTextField = new JTextField( 8 );

    valueTextFields.addElement( valueTextField );
    
    GridBagLayoutUtil.addComponent(result, valueTextField,
				   2, 1, 1, 1,
				   GridBagConstraints.NONE, 
                                   GridBagConstraints.CENTER,
				   0.0, 0.0, 5, 5, 5, 5);

    return( result );
  }

  /****************************************************************************/

  public void dismiss()
  {
    setVisible(false);
  }

  /****************************************************************************/
  
  public void enableFiltered( boolean enabled )
  {
    filterPanel.setEnabled( enabled );

    for (int i = 0; i < columnComboBoxes.size(); i++)
    {
      JComboBox columnComboBox = (JComboBox) columnComboBoxes.elementAt(i);
      
      columnComboBox.setEnabled( enabled );
    }
    
    for (int i = 0; i < conditionOperatorComboBoxes.size(); i++)
    {
      JComboBox conditionOperatorComboBox = 
        (JComboBox) conditionOperatorComboBoxes.elementAt(i);
      
      conditionOperatorComboBox.setEnabled( enabled );
    }
    
    for (int i = 0; i < valueTextFields.size(); i++)
    {
      JTextField valueTextField = (JTextField) valueTextFields.elementAt(i);
      
      valueTextField.setEnabled( enabled );
    }
    
    andButton.setEnabled( enabled );
    orButton.setEnabled( enabled );
    clearButton.setEnabled( enabled );
  }

  /****************************************************************************/

  public void filtered()
  {
    enableFiltered( true );
  }

  /****************************************************************************/
  
  public Filter getFilter()
  {
    return( filter );
  }

  /****************************************************************************/

  public void init()
  {
    setLocation( ComponentUtil.center(this) );
    setTitle("Extraction Filter Selector");

    Container contentPane = getContentPane();

    contentPane.setLayout( new GridBagLayout() );

    // Assemble the mode selection panel.
    JPanel modeSelectionPanel = initModeSelectionPanel();

    GridBagLayoutUtil.addComponent(contentPane, modeSelectionPanel,
				   0, 0, 1, 1,
				   GridBagConstraints.NONE, 
                                   GridBagConstraints.CENTER,
				   0.0, 0.0, 5, 5, 5, 5);

    // Assemble the form panel.
    filterPanel = initFilterPanel();

    GridBagLayoutUtil.addComponent(contentPane, filterPanel,
				   0, 1, 1, 1,
				   GridBagConstraints.BOTH, 
                                   GridBagConstraints.CENTER,
				   1.0, 1.0, 5, 5, 5, 5);

    JPanel buttonPanel = initButtonPanel();

    GridBagLayoutUtil.addComponent(contentPane, buttonPanel,
				   0, 2, 1, 1,
				   GridBagConstraints.NONE, 
                                   GridBagConstraints.CENTER,
				   0.0, 0.0, 5, 5, 5, 5);

    pack();
  }

  /****************************************************************************/

  public JPanel initButtonPanel()
  {
    JPanel result = new JPanel();

    result.setLayout( new GridBagLayout() );

    JButton okButton = new JButton("OK");

    okButton.addActionListener( new OKButtonListener(this) );
    okButton.setToolTipText("Accept the current values and dismiss the dialog.");

    GridBagLayoutUtil.addComponent(result, okButton,
				   0, 0, 1, 1,
				   GridBagConstraints.NONE, 
                                   GridBagConstraints.CENTER,
				   0.5, 0.0, 5, 5, 5, 5);

    JButton cancelButton = new JButton("Cancel");

    cancelButton.addActionListener( new CancelButtonListener(this) );
    cancelButton.setToolTipText("Cancel viewing this table.");

    GridBagLayoutUtil.addComponent(result, cancelButton,
				   1, 0, 1, 1,
				   GridBagConstraints.NONE, 
                                   GridBagConstraints.CENTER,
				   0.5, 0.0, 5, 5, 5, 5);

    return( result );
  }

  /****************************************************************************/

  public JPanel initFilterButtonPanel()
  {
    JPanel result = new JPanel();

    result.setLayout( new GridBagLayout() );

    andButton = new JButton("And");

    andButton.addActionListener( new AndButtonListener(this) );

    GridBagLayoutUtil.addComponent(result, andButton,
				   0, 0, 1, 1,
				   GridBagConstraints.HORIZONTAL, 
                                   GridBagConstraints.CENTER,
				   1.0, 0.3, 5, 5, 5, 5);

    orButton = new JButton("Or");

    orButton.addActionListener( new OrButtonListener(this) );

    GridBagLayoutUtil.addComponent(result, orButton,
				   0, 1, 1, 1,
				   GridBagConstraints.HORIZONTAL, 
                                   GridBagConstraints.CENTER,
				   1.0, 0.3, 5, 5, 5, 5);

    clearButton = new JButton("Clear");

    clearButton.addActionListener( new ClearButtonListener(this) );
    
    GridBagLayoutUtil.addComponent(result, clearButton,
				   0, 2, 1, 1,
				   GridBagConstraints.HORIZONTAL, 
                                   GridBagConstraints.CENTER,
				   1.0, 0.3, 5, 5, 5, 5);

    return( result );
  }

  /****************************************************************************/

  public JPanel initFilterPanel()
  {
    JPanel result = new JPanel();

    result.setLayout( new GridBagLayout() );
    result.setBorder( new TitledBorder("General Filtering") );

    JScrollPane scrollPane = new JScrollPane();

    scrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
    scrollPane.setPreferredSize( new Dimension( 600, 300 ) );

    view = new JPanel();

    view.setLayout( new GridBagLayout() );

    JPanel constraintPanel = createConstraintPanel();

    GridBagLayoutUtil.addComponent(view, constraintPanel,
				   0, rowIndex, 1, 1,
				   GridBagConstraints.NONE, 
                                   GridBagConstraints.NORTHWEST,
				   0.0, 0.0, 5, 5, 5, 5);

    // Pushes the constraintPanel's into the top left corner.
    GridBagLayoutUtil.addComponent(view, Box.createGlue(),
				   3, 100, 1, 1,
				   GridBagConstraints.BOTH, 
                                   GridBagConstraints.CENTER,
				   1.0, 1.0, 5, 5, 5, 5);

    viewport = scrollPane.getViewport();

    viewport.setView(view);

    GridBagLayoutUtil.addComponent(result, scrollPane,
				   0, 1, 1, 1,
				   GridBagConstraints.BOTH, 
                                   GridBagConstraints.CENTER,
				   1.0, 1.0, 5, 5, 5, 5);

    JPanel filterButtonPanel = initFilterButtonPanel();

    GridBagLayoutUtil.addComponent(result, filterButtonPanel,
				   1, 1, 1, 1,
				   GridBagConstraints.VERTICAL, 
                                   GridBagConstraints.CENTER,
				   1.0, 1.0, 5, 5, 5, 5);

    return( result );
  }

  /****************************************************************************/

  public JPanel initModeSelectionPanel()
  {
    JPanel result = new JPanel();

    result.setLayout( new GridBagLayout() );
    result.setBorder( new BevelBorder( BevelBorder.RAISED ) );

    ButtonGroup group = new ButtonGroup();

    JRadioButton unfilteredButton = new JRadioButton( "Unfiltered" );

    unfilteredButton.addActionListener( new UnfilteredButtonListener(this) );
    unfilteredButton.setSelected(true);
    unfilteredButton.setToolTipText("Use no constraints in querying the database.");

    group.add(unfilteredButton);

    GridBagLayoutUtil.addComponent(result, unfilteredButton,
				   0, 0, 1, 1,
				   GridBagConstraints.NONE, 
                                   GridBagConstraints.CENTER,
				   1.0, 0.25, 5, 5, 5, 5);

    filteredButton = new JRadioButton("Filtered");

    filteredButton.addActionListener( new FilteredButtonListener(this) );

    group.add(filteredButton);

    GridBagLayoutUtil.addComponent(result, filteredButton,
				   1, 0, 1, 1,
				   GridBagConstraints.NONE, 
                                   GridBagConstraints.CENTER,
				   1.0, 0.25, 5, 5, 5, 5);

    return(result);
  }

  /****************************************************************************/

  public boolean isFiltered()
  {
    return( filterPanel.isEnabled() );
  }

  /****************************************************************************/

  public void ok()
  {
    cancelled = false;

    try
    {
      if ( isFiltered() )
      {
        checkFilter();
      }

      dismiss();
    }
    catch ( ConfigurationException configurationException )
    {
      LogClient.printStackTrace( configurationException );

      JOptionPane.showMessageDialog( (JFrame) getOwner(),
                                     configurationException.getMessage(),
                                     "Configuation Error",
                                     JOptionPane.ERROR_MESSAGE );
    }
    catch ( FilterException filterException )
    {
      JOptionPane.showMessageDialog( (JFrame) getOwner(),
                                     filterException.getMessage(),
                                     "Filter Error",
                                     JOptionPane.ERROR_MESSAGE );
    }
  }

  /****************************************************************************/

  public void or()
  {
    resizeViewport();

    combinationOperators.addElement( new Integer( Condition.OR ) );

    GridBagLayoutUtil.addComponent(view, new JLabel("OR"),
				   1, rowIndex++, 1, 1,
				   GridBagConstraints.NONE, 
                                   GridBagConstraints.WEST,
				   0.0, 0.0, 5, 5, 5, 5);

    JPanel constraintPanel = createConstraintPanel();

    GridBagLayoutUtil.addComponent(view, constraintPanel,
				   0, rowIndex, 1, 1,
				   GridBagConstraints.NONE, 
                                   GridBagConstraints.WEST,
				   0.0, 0.0, 5, 5, 5, 5);

    view.validate();
  }

  /****************************************************************************/

  public void resizeViewport()
  {
    Dimension viewportSize = viewport.getViewSize();
    Dimension viewPreferredSize = view.getPreferredSize();

    if ( ( viewportSize.getHeight() < (viewPreferredSize.getHeight() + 50) ) ||
         ( viewportSize.getWidth() < (viewPreferredSize.getWidth() + 50) ) )
    {
      Dimension newViewportSize = 
        new Dimension( (int) viewPreferredSize.getWidth() + 50,
                       (int) viewPreferredSize.getHeight() + 50 );

      viewport.setViewSize( newViewportSize );
    }
  }

  /****************************************************************************/
  
  public void setFilter( Filter filter )
  {
    clear();

    Vector<Condition> conditions = filter.getConditions();

    for (int i = 0; i < conditions.size(); i++)
    {
      Condition condition = (Condition) conditions.elementAt(i);

      JComboBox columnComboBox = (JComboBox) columnComboBoxes.elementAt(i);

      DatabaseKey databaseKey = condition.getDatabaseKey();

      Pair pair = new Pair( databaseKey, databaseKey.getColumnName() );

      columnComboBox.setSelectedItem( pair );

      JComboBox conditionOperatorComboBox = 
        (JComboBox) conditionOperatorComboBoxes.elementAt(i);

      int conditionOperator = condition.getConditionOperator();

      conditionOperatorComboBox.setSelectedIndex( conditionOperator );

      JTextField valueTextField = (JTextField) valueTextFields.elementAt(i);

      valueTextField.setText( condition.getValue() );

      switch ( condition.getCombinationOperator() )
      {
      case( Condition.AND ):
        {
          and();

          break;
        }
      case( Condition.OR ):
        {
          or();

          break;
        }
      }
    }

    filteredButton.setSelected( true );
  }

  /****************************************************************************/

  public void unfiltered()
  {
    enableFiltered( false );
  }

  /****************************************************************************/

  public boolean wasCancelled()
  {
    return( cancelled );
  }

  /****************************************************************************/

}

/******************************************************************************/
