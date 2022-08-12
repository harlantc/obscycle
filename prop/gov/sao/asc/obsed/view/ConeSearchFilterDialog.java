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

import gov.sao.asc.obsed.database.Condition;
import gov.sao.asc.obsed.database.ConeSearchFilter;
import gov.sao.asc.obsed.database.Filter;
import gov.sao.asc.obsed.view.ConfigurationException;
import gov.sao.asc.obsed.view.event.BothButtonListener;
import gov.sao.asc.obsed.view.event.BothInterface;
import gov.sao.asc.obsed.view.event.ConeSearchButtonListener;
import gov.sao.asc.obsed.view.event.ConeSearchInterface;
import gov.sao.asc.obsed.view.event.FilteredButtonListener;
import gov.sao.asc.obsed.view.event.FilteredInterface;
import gov.sao.asc.obsed.view.event.UnfilteredButtonListener;
import gov.sao.asc.obsed.view.event.UnfilteredInterface;
import gov.sao.asc.util.GridBagLayoutUtil;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

/******************************************************************************/
/**
 *
 * @author Paul Matthew Reilly
 *
 * @version %I%, %G%
 */

public class ConeSearchFilterDialog extends FilterDialog
  implements BothInterface, ConeSearchInterface
{
  private JPanel coneSearchPanel;
  private JRadioButton bothButton;
  private JRadioButton coneSearchButton;
  private JTextField decTextField;
  private JTextField raTextField;
  private JTextField radiusTextField;

  /****************************************************************************/

  public ConeSearchFilterDialog( JFrame owner, Configuration configuration )
  {
    super( owner, configuration );
  }  

  /****************************************************************************/

  public void both()
  {
    enableFiltered( true );
    enableConeSearch( true );
  }

  /****************************************************************************/
  
  public void checkFilter()
    throws ConfigurationException, FilterException
  {
    this.filter = checkFilter( new ConeSearchFilter() );
  }

  /****************************************************************************/
  
  public Filter checkFilter( ConeSearchFilter coneSearchFilter )
    throws ConfigurationException, FilterException
  {
    ConeSearchFilter result;

    if ( filterPanel.isEnabled() )
    {
      result = (ConeSearchFilter) super.checkFilter( coneSearchFilter );
    }
    else
    {
      result = coneSearchFilter;
    }

    if ( coneSearchPanel.isEnabled() )
    {
      String ra = raTextField.getText();
      String dec = decTextField.getText();
      String radius = radiusTextField.getText();
      
      if (! ( ra.equals("") || dec.equals("") || radius.equals("") ) )
      {
        result.setRA( ra );
        result.setDec( dec );
        result.setRadius( radius );
      }
      else if ( ( ! ra.equals("") ) || 
                ( ! dec.equals("") ) || 
                ( ! radius.equals("") ) )
      {
        String message = "A cone search requires an RA, Dec, and Radius value.";
        
        throw ( new FilterException( message ) );
      }
    }

    return(result);
  }

  /****************************************************************************/

  public void coneSearch()
  {
    enableFiltered( false );
    enableConeSearch( true );
  }

  /****************************************************************************/
  
  public void enableConeSearch( boolean enabled )
  {
    coneSearchPanel.setEnabled( enabled );

    Component components[] = coneSearchPanel.getComponents();

    for ( int i = 0; i < components.length; i++ )
    {
      components[i].setEnabled( enabled );
    }
  }

  /****************************************************************************/

  public void filtered()
  {
    super.filtered();

    enableConeSearch( false );
  }

  /****************************************************************************/

  public JPanel initConeSearchPanel()
  {
    JPanel result = new JPanel();

    result.setBorder( new TitledBorder("Cone Search") );
    result.setLayout( new GridBagLayout() );

    GridBagLayoutUtil.addComponent( result, new JLabel("RA: ", JLabel.RIGHT),
				    0, 0, 1, 1,
				    GridBagConstraints.HORIZONTAL, 
                                    GridBagConstraints.CENTER,
				    0.0, 0.0, 5, 5, 5, 5 );

    raTextField = new JTextField(10);

    GridBagLayoutUtil.addComponent( result, raTextField,
				    1, 0, 1, 1,
				    GridBagConstraints.HORIZONTAL, 
                                    GridBagConstraints.CENTER,
				    0.0, 0.0, 5, 5, 5, 5 );

    GridBagLayoutUtil.addComponent( result, new JLabel("Dec: ", JLabel.RIGHT),
				    2, 0, 1, 1,
				    GridBagConstraints.HORIZONTAL, 
                                    GridBagConstraints.CENTER,
				    0.0, 0.0, 5, 5, 5, 5 );

    decTextField = new JTextField(10);

    GridBagLayoutUtil.addComponent( result, decTextField,
				    3, 0, 1, 1,
				    GridBagConstraints.HORIZONTAL, 
                                    GridBagConstraints.CENTER,
				    0.0, 0.0, 5, 5, 5, 5 );

    GridBagLayoutUtil.addComponent( result, 
                                    new JLabel("Radius (arcmin): ", JLabel.RIGHT),
				    4, 0, 1, 1,
				    GridBagConstraints.HORIZONTAL, 
                                    GridBagConstraints.CENTER,
				    0.0, 0.0, 5, 5, 5, 5 );

    radiusTextField = new JTextField(10);

    GridBagLayoutUtil.addComponent( result, radiusTextField,
				    5, 0, 1, 1,
				    GridBagConstraints.HORIZONTAL, 
                                    GridBagConstraints.CENTER,
				    0.0, 0.0, 5, 5, 5, 5 );

    return(result);
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

    GridBagLayoutUtil.addComponent( view, constraintPanel,
				    0, rowIndex, 1, 1,
				    GridBagConstraints.NONE, 
                                    GridBagConstraints.NORTHWEST,
				    0.0, 0.0, 5, 5, 5, 5 );

    // Pushes the constraintPanel's into the top left corner.
    GridBagLayoutUtil.addComponent( view, Box.createGlue(),
				    3, 100, 1, 1,
				    GridBagConstraints.BOTH, 
                                    GridBagConstraints.CENTER,
				    1.0, 1.0, 5, 5, 5, 5 );

    viewport = scrollPane.getViewport();

    viewport.setView( view );

    GridBagLayoutUtil.addComponent( result, scrollPane,
				    0, 1, 1, 1,
				    GridBagConstraints.BOTH, 
                                    GridBagConstraints.CENTER,
				    1.0, 1.0, 5, 5, 5, 5 );

    JPanel filterButtonPanel = initFilterButtonPanel();

    GridBagLayoutUtil.addComponent( result, filterButtonPanel,
				    1, 1, 1, 1,
				    GridBagConstraints.VERTICAL, 
                                    GridBagConstraints.CENTER,
				    1.0, 1.0, 5, 5, 5, 5 );

    coneSearchPanel = initConeSearchPanel();

    GridBagLayoutUtil.addComponent( result, coneSearchPanel,
                                    0, 2, 1, 1,
                                    GridBagConstraints.HORIZONTAL, 
                                    GridBagConstraints.CENTER,
                                    1.0, 0.0, 5, 5, 5, 5 );

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

    GridBagLayoutUtil.addComponent( result, unfilteredButton,
				    0, 0, 1, 1,
				    GridBagConstraints.NONE, 
                                    GridBagConstraints.CENTER,
				    1.0, 0.25, 5, 5, 5, 5 );

    filteredButton = new JRadioButton( "Filtered" );

    filteredButton.addActionListener( new FilteredButtonListener(this) );

    group.add( filteredButton );

    GridBagLayoutUtil.addComponent( result, filteredButton,
				    1, 0, 1, 1,
				    GridBagConstraints.NONE, 
                                    GridBagConstraints.CENTER,
				    1.0, 0.25, 5, 5, 5, 5 );

    coneSearchButton = new JRadioButton( "Cone Search" );

    coneSearchButton.addActionListener( new ConeSearchButtonListener(this) );

    group.add( coneSearchButton );

    GridBagLayoutUtil.addComponent( result, coneSearchButton,
				    2, 0, 1, 1,
				    GridBagConstraints.NONE, 
                                    GridBagConstraints.CENTER,
				    1.0, 0.25, 5, 5, 5, 5 );

    bothButton = new JRadioButton( "Filtered and Cone Search" );

    bothButton.addActionListener( new BothButtonListener(this) );

    group.add( bothButton );

    GridBagLayoutUtil.addComponent( result, bothButton,
				    3, 0, 1, 1,
				    GridBagConstraints.NONE, 
                                    GridBagConstraints.CENTER,
				    1.0, 0.25, 5, 5, 5, 5 );

    return(result);
  }

  /****************************************************************************/

  public boolean isFiltered()
  {
    boolean result = false;

    if ( filterPanel.isEnabled() || coneSearchPanel.isEnabled() )
    {
      result = true;
    }

    return( result );
  }

  /****************************************************************************/
  
  public void setFilter( ConeSearchFilter filter )
  {
    super.setFilter( filter );

    if ( filter.isBoth() )
    {
      enableFiltered( true );
      enableConeSearch( true );

      decTextField.setText( filter.getDec() );
      raTextField.setText( filter.getRA() );
      radiusTextField.setText( filter.getRadius() );

      bothButton.setSelected( true );
    }
    else if ( filter.isConeSearch() )
    {
      enableFiltered( false );
      enableConeSearch( true );

      decTextField.setText( filter.getDec() );
      raTextField.setText( filter.getRA() );
      radiusTextField.setText( filter.getRadius() );

      coneSearchButton.setSelected( true );
    }
  }

  /****************************************************************************/

  public void unfiltered()
  {
    super.unfiltered();

    enableConeSearch( false );
  }

  /****************************************************************************/

}

/******************************************************************************/
