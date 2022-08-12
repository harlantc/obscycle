/*
  Copyrights:
 
  Copyright (c) 1998 Smithsonian Astrophysical Observatory
 
  Permission to use, copy, modify, distribute, and sell this software
  and its documentation for any purpose is hereby granted without fee,
  provided that the above copyright notice appear in all copies and
  that both that copyright notice and this permission notice appear in
  supporting documentation, and that the name of the Smithsonian
  Astrophysical Observatory not be used in advertising or publicity
  pertaining to distribution of the software without specific, written
  prior permission.  The Smithsonian Astrophysical Observatory makes
  no representations about the suitability of this software for any
  purpose.  It is provided "as is" without express or implied
  warranty.  THE SMITHSONIAN INSTITUTION AND THE SMITHSONIAN
  ASTROPHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES WITH REGARD TO
  THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
  AND FITNESS, IN NO EVENT SHALL THE SMITHSONIAN INSTITUTION AND/OR
  THE SMITHSONIAN ASTROPHYSICAL OBSERVATORY BE LIABLE FOR ANY SPECIAL,
  INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER
  RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
  CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
  CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

/******************************************************************************/

package gov.sao.asc.obsed.view.form;

/******************************************************************************/

import gov.sao.asc.obsed.Constants;
import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.database.ColumnEntry;
import gov.sao.asc.obsed.database.DatabaseConfiguration;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.database.DatabaseModel;
import gov.sao.asc.obsed.database.Database;
import gov.sao.asc.obsed.view.ComponentEntry;
import gov.sao.asc.obsed.view.Configuration;
import gov.sao.asc.obsed.view.ConfigurationException;
import gov.sao.asc.obsed.view.form.event.FormMouseListener;
import gov.sao.asc.obsed.view.form.event.TextChangeListener;
import gov.sao.asc.util.LogClient;
import gov.sao.asc.util.XML;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.text.Document;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/******************************************************************************/

public class FormViewConfiguration extends Configuration
{
  private int column;
  private int row;
  private String formName;
  private URL url;
  private Vector<NavigationalEntry> displayEntries;
  private FormViewController viewController;

  /****************************************************************************/

  public FormViewConfiguration( String formName,
				FormViewController viewController )
  {
    super();
    this.formName = formName;
    this.viewController = viewController;
  }

  /****************************************************************************/

  public void addDisplayEntry(Node entryNode)
  {
    String name = XML.getTextForChildElement( entryNode, "name" );

    String tableName = XML.getTextForChildElement( entryNode, "tableName" );

    String columnName = XML.getTextForChildElement( entryNode, "columnName" );

    if (Editor.getInstance().isDebug()) {
      System.out.println("formview: " + columnName );
    }

    displayEntries.addElement( new NavigationalEntry( name, tableName,
						      columnName ) );      
  }

  /****************************************************************************/

  public Vector getDisplayEntries()
  {
    return(displayEntries);
  }

  /****************************************************************************/
  /**
   * Return the collection of items to display in a combo box.
   *
   * @param element  The XML node
   *
   * @returns A list of items.
   */

  private Vector<Object> getComboBoxItems( Element element,
				   FormComponentEntry entry )
    throws ConfigurationException
  {
    // Initialize the list from the database.
    Vector<Object> items = new Vector<Object>( entry.getColumnEntry().getChoices() );

    // Deal with the "New" and "None" attributes and sort the items.
    processAttributeItems( entry, element, items );

    return items;
  }

  /****************************************************************************/
  /**
   * Return the collection of items to display in a key combo box.
   *
   * @param element  The XML node
   *
   * @returns A list of items.
   */

  public Vector<Object> getKeyComboBoxItems( Element element,
				     FormComponentEntry entry )
  {
    // Generate and Set the key databasekey for this component entry.
    Element databaseElement =
      XML.getChildElement( element, "keyinfo" );
    String keyTableName =
      XML.getTextForChildElement( databaseElement, "keyTableName" );
    String keyColumnName =
      XML.getTextForChildElement( databaseElement, "keyColumnName" );
    DatabaseKey keyDatabaseKey =
      new DatabaseKey( keyTableName, keyColumnName );
    entry.setKeyDatabaseKey( keyDatabaseKey );
    
    // Generate the list of items for the key combo box using the
    // keyDatabaseKey to seed the values.
    Database database = Editor.getInstance().getDatabase();
    Vector<Object> items = database.getKeys( keyDatabaseKey );

    // Deal with the "New" and "None" attributes and sort the items.
    processAttributeItems( entry, element, items );

    return items;
  }

  /****************************************************************************/

  public static void main(String[] args)
  {
    FormViewConfiguration FormViewConfiguration = 
      new FormViewConfiguration( "target", null );
  }

  /****************************************************************************/
  /**
   * Handle any items specified as attributes,such as "New" and "None".
   *
   * @param items  The base list of items.
   * @param element  
   *
   * @returns A list of items.
   */

  public void processAttributeItems( ComponentEntry entry, Element element,
				     Vector<Object> items )
  {
    // Replace any occurrences of a NULL with the component entry
    // representation of a null.
    String nullString = entry.getNullString();
    int index = 0;
    while ( (index = items.indexOf( null, index )) != -1 )
    {
      // Replace the item at index.
      items.setElementAt( nullString, index );
    }

    // Insure at least one occurrence of a NULL exists.
    if ( !items.contains( nullString ) )
    {
      // Put the item at the beginning of the list.
      items.insertElementAt( nullString, 0 );
    }

    // Remove the "None" element if specified by the "none" attribute.
    if ( element.getAttribute( "none" ).equalsIgnoreCase( "false" ) )
    {
      items.remove( nullString );
    }

    // Handle a "new" attribute, if present.
    if ( element.getAttribute( "new" ).equalsIgnoreCase( "true" ) )
    {
      // Put the item at the beginning of the list.
      items.insertElementAt( Constants.NEW, 0 );
    }
  }

  /****************************************************************************/

  private void processCheckBox( Node node )
    throws ConfigurationException
  {
    FormComponentEntry FormComponentEntry = 
      new FormComponentEntry( Constants.CHECKBOX );

    setupComponent(node, FormComponentEntry);
  }

  /****************************************************************************/
  /**
   * Deal with a combo box specification.
   */
  
  private void processComboBox( Node node, int comboBoxType )
    throws ConfigurationException
  {
    try
    {
      // Setup a text area Constants. First setup the geometry, then get
      // the tooltip and the label.
      FormComponentEntry entry = new FormComponentEntry( comboBoxType );
      setupComponent( node, entry );

      // Use the combo box type to determine how to proces the
      // initialization.
      Vector<Object> items = null;
      switch ( comboBoxType )
      {
      case Constants.COMBOBOX:
	// Get the list of items to be presented in the combo box from
	// the database.
	items = getComboBoxItems( (Element) node, entry );
	break;

      case Constants.KEYCOMBOBOX:
	// Initialize a combo box for a table's key column.
	// Process the "new" attribute, if specified.
	items = getKeyComboBoxItems( (Element) node, entry );
	break;

      case Constants.OBSIDCOMBOBOX:
	// Initialize a combo box for the table's obsid field.  Use
	// the choices for the target.obsid but filter out entries
	// that are locked or already exist in this table. Also add an
	// entry for NONE at the beginning of the list.
	String tableName = keyEntry.getTableName();
	Database database = Editor.getInstance().getDatabase();
	//items = database.getNewObsidValues( tableName );
	items = database.executeQuery( entry.getQueryString() );
        // Don't add the "None" element if specified by the "none" attribute.
        Element element = (Element)node;
        if ( element.getAttribute( "none" ).equalsIgnoreCase( "false" ) ) {
        }
        else {
	  items.insertElementAt( Constants.NONE, 0 );
        }
	break;

      default:
	break;
      }

      // Create the combo box component using the combo box list to
      // seed the values.  Initialize the selected item to the first
      // one.
      JComboBox comboBox = new JComboBox( items );
      comboBox.setSelectedIndex( 0 );

      // Set up the event handlers to deal with changes.
      ComboBoxController controller =
	new ComboBoxController( comboBox, entry, viewController );
      comboBox.setActionCommand( "MARK" );
      comboBox.addActionListener( controller );
      comboBox.addItemListener( controller );

      // Set the combo box attributes and register the combo box as
      // both the event generating component and the component
      // decorated with visual status information.
      String toolTipString = entry.getColumnEntry().getTooltip();
      comboBox.setToolTipText( toolTipString );
      entry.setComponents( comboBox );

      // Find the combo box button component and add a mouse listener
      // to it.  Simply adding a mouse listener to the Combo Box
      // doesn't work.
      int count = comboBox.getComponentCount();
      Component c;
      for( int index = 0; index < count; index++ )
      {
	c = comboBox.getComponent( index );
	if ( c instanceof javax.swing.plaf.metal.MetalComboBoxButton )
	{
	  c.addMouseListener( new FormMouseListener ( viewController ) );
	}
      }

      // Collect the entry.
      componentEntries.addElement( entry );
    }
    catch ( Exception exc )
    {
      LogClient.printStackTrace( exc );
      throw new ConfigurationException( exc.getMessage(), url.toString(), 
					node.toString() );
    }
  }

  /****************************************************************************/

  private void processConfigurationFile()
    throws ConfigurationException
  {
    Element form = null;
    URI uri = null;

    try
    {
      // Deal with the preamble to set up the form node.
      String suffix = viewController.getFormView().getSuffix();
      url = Editor.getInstance().getConfigurationURL( formName + suffix );
      uri = new URI(url.toString());

      org.w3c.dom.Document document = null;
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      document =  builder.parse(uri.toString());

      form = document.getDocumentElement();

      // Sequentially deal with navigation and GUI elements.
      processTitle( form.getElementsByTagName("title").item(0) );
      processInfo( form.getElementsByTagName("info").item(0) );
      processKey( form.getElementsByTagName("key").item(0) );
      processNavigation( form.getElementsByTagName("navigation").item(0) );
      processGUI( form.getElementsByTagName("GUI").item(0) );
    }
    catch ( MalformedURLException malformedURLException )
    {
      throw new ConfigurationException( malformedURLException.getMessage(),
					url.toString(), null );
    }
    catch ( IOException ioException )
    {
      throw new ConfigurationException( ioException.getMessage(),
					url.toString(), null );
    }
    catch ( SAXParseException saxParseException )
    {
      String message = "XML parse error at line " +
	saxParseException.getLineNumber() +
	", uri " + saxParseException.getSystemId() +
	", because " + saxParseException.getMessage();
      String urlString = (url != null ? url.toString() : null);
      String formString = (form != null ? form.toString() : null);
      throw new ConfigurationException( message, urlString, formString );
    }
    catch ( SAXException saxException ) 
    {
      String message = saxException.getMessage();
      String urlString = (url != null ? url.toString() : null);
      String formString = (form != null ? form.toString() : null);
      throw new ConfigurationException( message, urlString, formString );
    }
    catch ( Exception exc ) {
      String urlString = (url != null ? url.toString() : null);
      throw new ConfigurationException( exc.getMessage(), urlString, null );
    }
  }
  
  /****************************************************************************/

  public void processGUI( Node guiNode )
  {
    // Loop through each component spec.
    NodeList nodeList = guiNode.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++)
    {
      // Process a component specification.
      Node node = nodeList.item(i);
      if ( node instanceof Element )
      {
	// Catch any configuration errors such that the errant node
	// will be logged but ignored.
	try
	{
	  if ( node.getNodeName().equals( "textfield" ) )
	  {
	    processTextField(node);
	  }
	  else if ( node.getNodeName().equals( "triplebox" ) )
	  {
	    processTripleBox(node);
	  }
	  else if ( node.getNodeName().equals( "preferredbox" ) )
	  {
	    processPreferredBox(node);
	  }
	  else if ( node.getNodeName().equals( "combobox" ) )
	  {
	    processComboBox(node, Constants.COMBOBOX );
	  }
	  else if ( node.getNodeName().equals( "keycombobox" ) )
	  {
	    processComboBox(node, Constants.KEYCOMBOBOX );
	  }
	  else if ( node.getNodeName().equals( "obsidcombobox" ) )
	  {
	    processComboBox(node, Constants.OBSIDCOMBOBOX );
	  }
	  else if ( node.getNodeName().equals( "obsidbutton" ) )
	  {
	    processObsIDButton(node);
	  }
	  else if ( node.getNodeName().equals( "seqnbrbutton" ) )
	  {
	    processSequenceNumberButton(node);
	  }
	  else if ( node.getNodeName().equals( "separator" ) )
	  {
	    processSeparator(node);
	  }
	  else if ( node.getNodeName().equals( "label" ) )
	  {
	    processLabel(node);
	  }
	  else if ( node.getNodeName().equals( "textarea" ) )
	  {
	    processTextArea(node);
	  }
	  else if ( node.getNodeName().equals( "timestampbutton" ) )
	  {
	    processTimestampButton(node);
	  }
	  else
	  {
	    LogClient.logMessage( "Unrecognized node." );
	    LogClient.logMessage( "URL: " + url );
	    LogClient.logMessage( "Node: " + node );
	    Throwable t = new Throwable();
	    LogClient.printStackTrace( t );
	  }
	}
	catch ( ConfigurationException ce )
	{
	  LogClient.logMessage( ce.getMessage() );
	  LogClient.logMessage( "URL: " + ce.getURL() );
	  LogClient.logMessage( "Node: " + ce.getNode() );
	  ce.printStackTrace();
	}
      }
    }
  }

  /****************************************************************************/

  private void processLabel( Node node )
    throws ConfigurationException
  {
    // Setup a node.  Handle errors in node specifications by
    // reporting an error and ignoring the node.
    try
    {
      // Setup a label Constants. First get the label text and deal with
      // the geometry.
      FormComponentEntry entry = new FormComponentEntry( Constants.LABEL );
      String labelString = XML.getTextForChildElement( node, "labelText" );
      setupComponent( node, entry );

      // Create the label and save it back as the component value.
      // Make the normal label empty.
      JLabel label = new JLabel( labelString );
      entry.setComponents( label );
      entry.setLabel( null );

      // Collect the entry.
      componentEntries.addElement( entry );
    }
    catch ( Exception exc )
    {
      throw new ConfigurationException( exc.getMessage(), url.toString(), 
					node.toString() );
    }

  }

  /****************************************************************************/
  /**
   * Process a navigation node.  A navigation node can be either a key
   * entry or a display entry.  There can be one or more display
   * entries and a single key entry.  The last key entry is the
   * relevant key entry in the presence of multiple key entries.
   */

  public void processNavigation( Node navigationNode )
  {
    NodeList nodeList = navigationNode.getChildNodes();
      
    for (int i = 0; i < nodeList.getLength(); i++)
    {
      Node node = nodeList.item(i);

      if (node instanceof Element)
      {
        addDisplayEntry(node);
      }
    }
  }

  /****************************************************************************/
  /**
   * Handle an ObsID button type.  It is a button with the button face
   * displaying either "default" or the currently selected value.  The
   * associated action routine is the face editor.
   *
   * @param node The XML node specification.
   */

  private void processObsIDButton( Node node )
    throws ConfigurationException
  {
    try
    {
      // Create the entry and set it up.
      FormComponentEntry entry = new FormComponentEntry( Constants.OBSIDBUTTON );
      setupComponent( node, entry );

      // Create the button and register it as the entry's event
      // component and status component.
      JButton button;
      button = new JButton();
      button.setText( Constants.DEFAULT );
      button.setBackground( Color.white );
      entry.setComponents( button );

      // Set the text field attributes.
      String toolTipString = entry.getColumnEntry().getTooltip();
      button.setToolTipText( toolTipString );

      // Set up mouse listener for this text field.
      button.addMouseListener( new FormMouseListener ( viewController ) );

      // Set up the change handlers.
      button.setActionCommand( "OBSID" );
      button.addActionListener( viewController );

      // Collect the entry.
      componentEntries.addElement( entry );
    }
    catch ( Exception exc )
    {
      LogClient.printStackTrace( exc );
      throw new ConfigurationException( exc.getMessage(), url.toString(), 
					node.toString() );
    }
  }

  /****************************************************************************/
  /**
   * Handle a Sequence Number button type.  It is a button with the
   * button face displaying either a seed sequence number or an actual
   * new sequence number.
   *
   * @param node The XML node specification.
   */

  private void processSequenceNumberButton( Node node )
    throws ConfigurationException
  {
    try
    {
      // Create the entry and set it up.
      FormComponentEntry entry = new FormComponentEntry( Constants.SEQNBRBUTTON );
      setupComponent( node, entry );

      // Create the button and register it as the entry's event
      // component and status component.
      JButton button;
      button = new JButton();
      button.setText( Constants.NONE );
      button.setBackground( Color.white );
      entry.setComponents( button );

      // Set the text field attributes.
      String toolTipString = entry.getColumnEntry().getTooltip();
      button.setToolTipText( toolTipString );

      // Set up mouse listener for this text field.
      button.addMouseListener( new FormMouseListener ( viewController ) );

      // Set up the change handlers.
      button.setActionCommand( "SEQNBR" );
      button.addActionListener( viewController );

      // Collect the entry.
      componentEntries.addElement( entry );
    }
    catch ( Exception exc )
    {
      LogClient.printStackTrace( exc );
      throw new ConfigurationException( exc.getMessage(), url.toString(), 
					node.toString() );
    }
  }

  /****************************************************************************/
  /**
   * Handle an SQL DATE type.  It is a button with the button face
   * displaying the date and time.  The associated action routine is
   * the face editor.
   */
  private void processTimestampButton( Node node )
    throws ConfigurationException
  {
    try
    {
      // Create the entry and set it up.
      FormComponentEntry entry = new FormComponentEntry( Constants.TIMESTAMPBUTTON );
      setupComponent( node, entry );

      // Create the button and register it as the entry's event
      // component and status component.
      JButton button;
      button = new JButton();
      button.setBackground( Color.white );
      entry.setComponents( button );

      // Set the text field attributes.
      String toolTipString = entry.getColumnEntry().getTooltip();
      button.setToolTipText( toolTipString );

      // Set up mouse listener for this text field.
      button.addMouseListener( new FormMouseListener ( viewController ) );

      // Set up the change handlers.
      button.setActionCommand( "TIMESTAMP" );
      button.addActionListener( viewController );

      // Collect the entry.
      componentEntries.addElement( entry );
    }
    catch ( Exception exc )
    {
      LogClient.printStackTrace( exc );
      throw new ConfigurationException( exc.getMessage(), url.toString(), 
					node.toString() );
    }
  }

  /****************************************************************************/

  private void processSeparator( Node node )
  {
    // Setup a separator Constants. First deal with the geometry.
    FormComponentEntry entry = new FormComponentEntry( Constants.SEPARATOR );
    row++;
    column = 0;
    entry.setRow( row );
    entry.setColumn( column );
    entry.setColumnWidth( GridBagConstraints.REMAINDER );
    entry.setAnchor( GridBagConstraints.CENTER );
    entry.setFill( GridBagConstraints.HORIZONTAL );

    // Create the separator in the next row.
    JSeparator separator = new JSeparator();
    entry.setComponents( separator );

    // Collect the entry.
    componentEntries.addElement( entry );
  }

  /****************************************************************************/

  private void processTextArea( Node node )
    throws ConfigurationException
  {
    // Setup a node.  Handle errors in node specifications by
    // reporting an error and ignoring the node.
    try
    {
      // Setup a text area and it's geometry, label, etc.
      FormComponentEntry entry = new FormComponentEntry( Constants.TEXTAREA );
      setupComponent( node, entry );

      // Create the text area component using <i>size</i>, if
      // specified, as the number of columns and <i>height</i> as the
      // number of rows.  This component will be the used to generate
      // events.
      JTextArea textArea = new JTextArea();
      entry.setEventComponent( textArea );
      int size = entry.getSize();
      int height = entry.getRowHeight();
      if ( size > 0 )
      {
	textArea.setColumns( size );
      }
      if ( height > 1 )
      {
	// Set the number of rows based on the height specification.
	textArea.setRows( height );

	// Reset the component geometry such that this row takes only
	// one grid row.
	entry.setRowHeight( 1 );
      }

      // Wrap the text area in a scrolled window using the label text,
      // if provided, as a header.  Make this scroll pane the status
      // component, i.e. it will be decorated with status information.
      JScrollPane component = new JScrollPane( textArea );
      entry.setViewableComponent( component );
      JLabel label = entry.getLabel();
      if ( label != null )
      {
	component.setColumnHeaderView( label );
	// Don't set the label to null - it is needed for the history
	// function and doesn't hurt anything else.
	//	entry.setLabel( null );
      }

      // Set the text area attributes.
      String toolTipString = entry.getColumnEntry().getTooltip();
      textArea.setToolTipText( toolTipString );
      textArea.setLineWrap( true );

      // Set up mouse listener for this text area.
      textArea.addMouseListener( new FormMouseListener ( viewController ) );

      // Set up the change handling attributes.
      javax.swing.text.Document doc = textArea.getDocument();
      TextChangeListener listener =
	new TextChangeListener( viewController, textArea );
      doc.addDocumentListener( listener );

      // Collect the entry.
      componentEntries.addElement( entry );
    }
    catch ( Exception exc )
    {
      throw new ConfigurationException( exc.getMessage(), url.toString(), 
					node.toString() );
    }
  }
  
  /****************************************************************************/

  private void processTextField( Node node )
    throws ConfigurationException
  {
    // Setup a node.  Handle errors in node specifications by
    // reporting an error and ignoring the node.
    try
    {
      // Setup a text area and it's geometry, label, etc.
      FormComponentEntry entry = new FormComponentEntry( Constants.TEXTFIELD );
      setupComponent( node, entry );

      // Create the text field component using the given size, if any.
      JTextField textField;
      int size = entry.getSize();
      if ( size > 0 )
      {
	textField = new JTextField( size );
      }
      else
      {
	textField = new JTextField();
      }
      entry.setComponents( textField );

      // Set the text field attributes.
      String toolTipString = entry.getColumnEntry().getTooltip();
      textField.setToolTipText( toolTipString );

      // Set up mouse listener for this text field.
      textField.addMouseListener( new FormMouseListener ( viewController ) );

      // Set up the change handlers.
      textField.setActionCommand( "MARK" );
      textField.addActionListener( viewController );
      javax.swing.text.Document doc = textField.getDocument();
      TextChangeListener listener =
	new TextChangeListener( viewController, textField );
      doc.addDocumentListener( listener );

      // Collect the entry.
      componentEntries.addElement( entry );
    }
    catch ( Exception exc )
    {
      LogClient.printStackTrace( exc );
      throw new ConfigurationException( exc.getMessage(), url.toString(), 
					node.toString() );
    }
  }

  /****************************************************************************/
  /**
   * Deal with a preferred box specification.
   */
  
  private void processPreferredBox( Node node )
    throws ConfigurationException
  {
    // Setup a node.  Handle errors in node specifications by
    // reporting an error and ignoring the node.
    try
    {
      // Setup a text area Constants. First setup the geometry, then get
      // the tooltip and the label.
      FormComponentEntry entry = new FormComponentEntry(Constants.PREFERREDBOX);
      setupComponent( node, entry );
      String itemsString = XML.getTextForChildElement( node, "items" );
      
      // Create the combo box component using the combo box list to
      // seed the values.
      Vector<String> items = new Vector<String>();
      items.addElement( Constants.NULL );
      items.addElement( Constants.YES );
      items.addElement( Constants.PREFERRED );
      items.addElement( Constants.NO );
      JComboBox comboBox = new JComboBox( items );
      entry.setComponents( comboBox );
    
      // Set the combo box attributes.
      ColumnEntry columnEntry = entry.getColumnEntry();
      String toolTipString = columnEntry.getTooltip();
      comboBox.setToolTipText( toolTipString );

      // Find the combo box button component and add a mouse listener
      // to it.  Simply adding a mouse listener to the combo box
      // doesn't work.
      int count = comboBox.getComponentCount();
      Component c;
      for( int index = 0; index < count; index++ )
      {
	c = comboBox.getComponent( index );
	if ( c instanceof javax.swing.plaf.metal.MetalComboBoxButton )
	{
	  c.addMouseListener( new FormMouseListener ( viewController ) );
	}
      }

      // Set up the event handlers to deal with changes.
      ComboBoxController controller =
	new ComboBoxController( comboBox, entry, viewController );
      comboBox.setActionCommand( "MARK" );
      comboBox.addActionListener( controller );
      comboBox.addItemListener( controller );
      // Collect the entry.
      componentEntries.addElement( entry );
    }
    catch ( Exception exc )
    {
      LogClient.printStackTrace( exc );
      throw new ConfigurationException( exc.getMessage(), url.toString(), 
					node.toString() );
    }
  }

  /****************************************************************************/
  /**
   * Deal with a triple box specification.
   */
  
  private void processTripleBox( Node node )
    throws ConfigurationException
  {
    // Setup a node.  Handle errors in node specifications by
    // reporting an error and ignoring the node.
    try
    {
      // Setup a text area Constants. First setup the geometry, then get
      // the tooltip and the label.
      FormComponentEntry entry = new FormComponentEntry( Constants.TRIPLEBOX );
      setupComponent( node, entry );
      String itemsString = XML.getTextForChildElement( node, "items" );
      
      // Create the combo box component using the combo box list to
      // seed the values.
      Vector<String> items = new Vector<String>();
      items.addElement( Constants.NULL );
      items.addElement( Constants.YES );
      items.addElement( Constants.NO );
      JComboBox comboBox = new JComboBox( items );
      entry.setComponents( comboBox );
    
      // Set the combo box attributes.
      ColumnEntry columnEntry = entry.getColumnEntry();
      String toolTipString = columnEntry.getTooltip();
      comboBox.setToolTipText( toolTipString );

      // Find the combo box button component and add a mouse listener
      // to it.  Simply adding a mouse listener to the combo box
      // doesn't work.
      int count = comboBox.getComponentCount();
      Component c;
      for( int index = 0; index < count; index++ )
      {
	c = comboBox.getComponent( index );
	if ( c instanceof javax.swing.plaf.metal.MetalComboBoxButton )
	{
	  c.addMouseListener( new FormMouseListener ( viewController ) );
	}
      }

      // Set up the event handlers to deal with changes.
      ComboBoxController controller =
	new ComboBoxController( comboBox, entry, viewController );
      comboBox.setActionCommand( "MARK" );
      comboBox.addActionListener( controller );
      comboBox.addItemListener( controller );

      // Collect the entry.
      componentEntries.addElement( entry );
    }
    catch ( Exception exc )
    {
      LogClient.printStackTrace( exc );
      throw new ConfigurationException( exc.getMessage(), url.toString(), 
					node.toString() );
    }
  }

  /****************************************************************************/
  /**
   * Load (or reload) a configuratoin file.
   */

  public void reload()
  {
    row = 0;
    column = 0;

    displayEntries = new Vector<NavigationalEntry>();
    componentEntries = new Vector<ComponentEntry>();

    try
    {
      processConfigurationFile();
    }
    catch ( ConfigurationException exc )
    {
      LogClient.logMessage( exc.getMessage() );
      LogClient.logMessage( "URL: " + exc.getURL() );
      LogClient.logMessage( "Node: " + exc.getNode() );
      LogClient.logMessage( "Configuration: \n" + this.toString() );
    }
  }

  /****************************************************************************/

  public void setupComponent( Node guiNode, FormComponentEntry entry )
    throws ConfigurationException
  {
    // Insure we have a valid elment.
    if ( guiNode instanceof Element )
    {
      // Fetch the tag, if any.
      Element element = (Element) guiNode;
      entry.setTag( element.getAttribute( "tag" ) );

      // Check for a size specification.
      String sizeString = XML.getTextForChildElement( guiNode, "size" );
      if ( sizeString != null )
      {
	entry.setSize( Integer.parseInt( sizeString ) );
      }

      // Check for a row specification.
      Element rowElement = XML.getChildElement( guiNode, "row" );
      if ( rowElement != null )
      {
	// Handle a row spec. Process the number spec and set the
	// row height.
	String rowString = XML.getTextForChildElement( rowElement, "number" );
	if ( rowString == null || rowString.equals( "." ) )
	{
	  // Use the current row.
	}
	else if ( rowString.equals( "+" ) )
	{
	  row++;
	}
	else
	{
	  row = Integer.parseInt( rowString );
	}
	String height = XML.getTextForChildElement( rowElement, "height");
	if ( height != null )
	{
	  entry.setRowHeight( Integer.parseInt( height ) );
	}
      }

      // Set the component's row value.
      entry.setRow( row );

      // Check for a column specification.
      Element columnElement = XML.getChildElement( guiNode, "column" );
      if ( columnElement != null )
      {
	// Handle a column spec.  Process the number spec and set
	// the column width.
	String columnString = 
	  XML.getTextForChildElement( columnElement, "number" );
	if ( columnString == null || columnString.equals( "." ) )
	{
	  // Use the current column value.
	}
	else if ( columnString.equals("+") )
	{
	  column++;
	}
	else
	{
	  column = Integer.parseInt( columnString );
	}
	
	// Deal with the column width.
	String widthString =
	  XML.getTextForChildElement( columnElement, "width" );
	if ( widthString != null && widthString.equals( ">" ) )
	{
	  entry.setColumnWidth( GridBagConstraints.REMAINDER );
	}
	else if ( widthString != null )
	{
	  entry.setColumnWidth( Integer.parseInt( widthString ) );
	}
      }

      // Set the component's column value.  Bump the column counter by
      // default.
      entry.setColumn( column );
      column++;

      // Check for a null string specification.  Assume the default.
      String nullString = Constants.NONE;
      Object nullValue = null;
      Element nullElement = XML.getChildElement( guiNode, "null" );
      if ( nullElement != null )
      {
	// Set the null string representation and value to those
	// specified in the null node.
	nullString =
	  XML.getTextForChildElement( nullElement, "representation" );
	nullValue =
	  (Object) XML.getTextForChildElement( guiNode, "value" );
      }
      entry.setNullString( nullString );
      entry.setNullValue( nullValue );

      // Check for an anchor specification.
      String anchorString = XML.getTextForChildElement( guiNode, "anchor" );
      if ( anchorString != null )
      {
	if ( anchorString.equals( "EAST" ) )
	{
	  entry.setAnchor( GridBagConstraints.EAST );
	}
	else if ( anchorString.equals( "WEST" ) )
	{
	  entry.setAnchor( GridBagConstraints.WEST );
	}
	else if ( anchorString.equals( "CENTER" ) )
	{
	  entry.setAnchor( GridBagConstraints.CENTER );
	}
      }
      else
      {
	entry.setAnchor( GridBagConstraints.WEST );
      }

      // Setup the database information.
      Element databaseElement = XML.getChildElement( guiNode, "database" );
      if ( databaseElement != null )
      {
	entry.setDatabaseKey( getPrimaryKey( databaseElement ) );
	entry.setJoinDatabaseKey( getSecondaryKey( databaseElement ) );
	String queryString =
	  XML.getTextForChildElement( databaseElement, "queryString" );
	entry.setQueryString( queryString );
      }

      // If a label exists then create a label Constants.
      String labelString = XML.getTextForChildElement( guiNode, "labelText" );
      if ( labelString != null )
      {
	// Create the label and bump the column count.
	JLabel label = new JLabel( labelString + ": " );
	entry.setLabel( label );
	column++;
      }
    }
  }

  /****************************************************************************/

  public String toString()
  {
    StringBuffer result = new StringBuffer();

    result.append( super.toString() );

    if ( displayEntries != null )
    {
      Iterator i = displayEntries.iterator();
      while ( i.hasNext() )
      {
	result.append( "Display Entry: \n" + 
		       i.next() + "\n" );
      }
    }

    return( result.toString() );
  }

  /****************************************************************************/

}

/******************************************************************************/
