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

package gov.sao.asc.obsed;

/******************************************************************************/

import gov.sao.asc.event.ExitInterface;
import gov.sao.asc.event.ExitListener;
import gov.sao.asc.event.PrintInterface;
import gov.sao.asc.event.PrintListener;
import gov.sao.asc.obsed.constraint.ConstraintChecker;
import gov.sao.asc.obsed.constraint.ConstraintViolationException;
import gov.sao.asc.obsed.database.Filter;
import gov.sao.asc.obsed.database.Database;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.database.IngestedException;
import gov.sao.asc.obsed.database.UpdateFailedException;
import gov.sao.asc.obsed.event.AboutInterface;
import gov.sao.asc.obsed.event.AboutListener;
import gov.sao.asc.obsed.event.CancelAllInterface;
import gov.sao.asc.obsed.event.CancelAllListener;
import gov.sao.asc.obsed.event.CancelInterface;
import gov.sao.asc.obsed.event.CancelListener;
import gov.sao.asc.obsed.event.CreateViewInterface;
import gov.sao.asc.obsed.event.DeleteRowInterface;
import gov.sao.asc.obsed.event.DeleteRowListener;
import gov.sao.asc.obsed.event.FontSizeInterface;
import gov.sao.asc.obsed.event.FontSizeListener;
import gov.sao.asc.obsed.event.HelpInterface;
import gov.sao.asc.obsed.event.HelpListener;
import gov.sao.asc.obsed.event.HistoryInterface;
import gov.sao.asc.obsed.event.HistoryListener;
import gov.sao.asc.obsed.event.LoginInterface;
import gov.sao.asc.obsed.event.LoginListener;
import gov.sao.asc.obsed.event.NewRecordInterface;
import gov.sao.asc.obsed.event.PrintViewInterface;
import gov.sao.asc.obsed.event.PrintViewListener;
import gov.sao.asc.obsed.event.RefreshInterface;
import gov.sao.asc.obsed.event.RefreshListener;
import gov.sao.asc.obsed.event.RemoveViewInterface;
import gov.sao.asc.obsed.event.RemoveViewListener;
import gov.sao.asc.obsed.event.RenameViewInterface;
import gov.sao.asc.obsed.event.RenameViewListener;
import gov.sao.asc.obsed.event.SaveAllInterface;
import gov.sao.asc.obsed.event.SaveAllListener;
import gov.sao.asc.obsed.event.SaveInterface;
import gov.sao.asc.obsed.event.SaveListener;
import gov.sao.asc.obsed.print.Printer;
import gov.sao.asc.obsed.security.CouldNotLoginException;
import gov.sao.asc.obsed.security.SecurityModel;
import gov.sao.asc.obsed.view.CancelledException;
import gov.sao.asc.obsed.view.ChangeList;
import gov.sao.asc.obsed.view.ConfigurationException;
import gov.sao.asc.obsed.view.SplashView;
import gov.sao.asc.obsed.view.View;
import gov.sao.asc.obsed.view.event.ControlListener;
import gov.sao.asc.obsed.view.event.ControlEvent;
import gov.sao.asc.obsed.view.form.FormView;
import gov.sao.asc.obsed.view.ViewMessage;
import gov.sao.asc.util.Frame;
import gov.sao.asc.util.ComponentUtil;
import gov.sao.asc.util.LogClient;
import gov.sao.asc.util.Profiler;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.SecureClassLoader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.TableColumnModel;

/******************************************************************************/
/**
 * The <code>Editor</code> class is the top level ObsCat GUI
 * application class.
 *
 * <p>There are two constructors.  The default constuctor is used when
 * <code>ObsCatApp</code> is invoked from an applet.  The second
 * constructor passes in the frame object used to contain
 * <code>ObsCatApp</code> when invoked as an application.
 *
 * @author Paul Michael Reilly
 * @author Paul Matthew Reilly
 *
 * @version %I%, %G% */

public class Editor extends JApplet 
  implements AboutInterface, CancelAllInterface, CancelInterface, 
             CreateViewInterface, DeleteRowInterface, ExitInterface, 
             FontSizeInterface, HelpInterface, HistoryInterface, 
             LoginInterface, PrintInterface, PrintViewInterface, 
             RefreshInterface, RemoveViewInterface, RenameViewInterface,
             SaveAllInterface, SaveInterface, 
             NewRecordInterface
{



  /**
   * True if the user was successfully authenticated.
   */
  private boolean authenticated;

  /**
   * The base URL used to reference configuration files.
   */
  private URL baseURL;

  private ChangeList changeList;
  private ConstraintChecker constraintChecker;
  private static EventListenerList controlListenerList;
  private Database database;

  /**
   * The Database user name.
   */
  private String dbUserName;

  /**
   * The prefix for obtaining editor configuration files.
   */
  private final String configDirectory = "gov/sao/asc/obsed/data/";

  /*
   * Set based on command line switch -Dobsed.debug.
   */
  private boolean debugFlag;

  /**
   * Used to store the previously entered filter for each view, so the
   * user doesn't have to re-enter a filter to extend it.
   */
  private Hashtable<String,Filter> filters;

  private Frame frame;
  private static Editor instance;
  private boolean isApplet;
  private MessageHandler messageHandler;
  private Properties printProperties;
  private Properties properties;

  /**
   * The resource loader used to obtain configuration files.
   */
  URLClassLoader resourceLoader;

  private SecurityModel securityModel;
  private JTabbedPane tabbedPanel;
  private ViewMenu viewMenu;

  /****************************************************************************/
  /**
   * The default constructor when invoked from a browser.
   */

  public Editor()
  {
    super();

    // Setup as if the program was invoked as an applet.  For an
    // application, corrections will be made in the main() method.
    dbUserName = null;
    isApplet = true;

    frame = new Frame();
    frame.setTitle( "ObsCat Editor" );

    filters = new Hashtable<String,Filter>();
    printProperties = new Properties();

    instance = this;

    // Establish a list for Control Event listeners.
    controlListenerList = new EventListenerList();
  }

  /****************************************************************************/

  public void about()
  {
    String aboutText = loadFile( properties.getProperty( "about.file" ) );

    JOptionPane.showMessageDialog( frame, 
                                   aboutText,
                                   "About Obscat Editor",
                                   JOptionPane.PLAIN_MESSAGE );
  }

  /****************************************************************************/
  /**
   * Add an event listener for Control Events.
   */

  public void addControlListener( ControlListener l ) 
  {
    controlListenerList.add( ControlListener.class, l );
  } 

  /****************************************************************************/

  public void cancel()
  {
    View view = (View) tabbedPanel.getSelectedComponent();
   
    view.cancelCurrentChanges();
    // Tell listeners about the cancel event.
    ControlEvent controlEvent = new ControlEvent( "CANCEL" );
    fireControlEvent( controlEvent );
  }

  /****************************************************************************/

  public void cancelAll()
  {
    View view = (View) tabbedPanel.getSelectedComponent();
   
    view.cancelAllChanges(); 

    // Tell listeners about the cancel all event.
    ControlEvent controlEvent = new ControlEvent( "CANCEL_ALL" );
    fireControlEvent( controlEvent );
  }

  /****************************************************************************/
  /**
   * Create a view.
   *
   * @param className The class, which will be dynamically created.
   * The class must be a descendant of the View class.
   * @param viewName The name which will be used to generate the
   * configuration file name.
   * @param keyValue The database key value to show in the new view.
   */

  public void createView( String className, String viewName, Object keyValue )
  {
    String errorMessage = null;

    try
    {
      // Create the new view object, add it to the tabbed panel and
      // initialize it.
      Class viewClass = Class.forName( className );
      View view = (View) viewClass.newInstance();
      view.setViewName( viewName );
      view.setSelectedKeyValue( keyValue );

      // Save the current view if it is really a view object.
      Object component = tabbedPanel.getSelectedComponent();
      if ( component instanceof View )
      {
	view.setInvokingView( (View) component );
      }

      // Add the new view to the tabbed panel and make it the selected
      // component.
      tabbedPanel.add( view );
      tabbedPanel.setSelectedComponent( view );

      // Initialize the new view in its own thread.
      messageHandler.addMessage( new ViewMessage( Message.INIT, view ) );
    }
    catch ( ClassNotFoundException classNotFoundException )
    {
      errorMessage =
	"Editor.createView(): Internal software error --- operation aborted.\n" +
	classNotFoundException.getMessage() + " was not found.\n" +
	"Please report this problem as soon as possible.";
    }
    catch ( InstantiationException instantiationException )
    {
      errorMessage =
	"Editor.createView(): Internal software error --- operation aborted.\n" +
	"An instantiation error occurred.\n" +
	"Please report this problem as soon as possible.";
    }
    catch ( IllegalAccessException illegalAccessException )
    {
      errorMessage =
	"Editor.createView(): Internal software error --- operation aborted.\n" +
	"An illegal access violation occurred.\n" +
	"Please report this problem as soon as possible.";
    }

    // Check for any internal errors.
    if ( errorMessage != null )
    {
      LogClient.logMessage( errorMessage );
      JOptionPane.showMessageDialog( frame, errorMessage,
				     "Internal Error Dialog",
				     JOptionPane.WARNING_MESSAGE );
    }
  }

  /****************************************************************************/

  public void deleteRow()
  {
    Component view = tabbedPanel.getSelectedComponent();

  }

  /****************************************************************************/

  public void exit()
  {
    if (isApplet)
    {
      frame.dispose();
    }
    else
    {
      System.exit(0);
    }
  }

  /****************************************************************************/
  /**
   * Tell the control event listeners that a control event has occurred.
   */

  public void fireControlEvent( ControlEvent event ) 
  {
    // Guaranteed to return a non-null array
    Object[] listeners = controlListenerList.getListenerList();

    // Lazily create the event:
    if ( event == null )
    {
      event = new ControlEvent( " " );
    }

    // Process the listeners last to first, notifying those that are
    // interested in this event
    for ( int i = listeners.length - 2; i >= 0; i -= 2 )
    {
      if ( listeners[ i ] == ControlListener.class )
      {
	((ControlListener) listeners[ i + 1 ]).controlCommandPerformed( event);
      }              
    }
  } 

  /****************************************************************************/

  public void fontSize(int size)
  {
    UIDefaults defaults = UIManager.getDefaults();

    Enumeration myenum = defaults.keys();

    while ( myenum.hasMoreElements() )
    {
      String key = myenum.nextElement().toString();

      if (key.indexOf("font") >= 0)
      {
        Font font = defaults.getFont(key);

        FontUIResource newFont = new FontUIResource(font.getName(), 
                                                    font.getStyle(), 
                                                    size);
        
        defaults.put(key, newFont);
      }
    }

    SwingUtilities.updateComponentTreeUI(frame);
  }
  
  /****************************************************************************/

  public ChangeList getChangeList()
  {
    return( changeList );
  }

  /****************************************************************************/
  /**
   * Return an input stream object opened on a configuration file.
   *
   * @param configFile The name of the configuration file.
   *
   * @return null or an input stream object.
   *
   * @throws IOException - if an I/O error occurs, such as the file
   * does not exist.
   */

  public InputStream getConfigurationInputStream( String configFile )
    throws IOException
  {
    // Predispose the result towards an error.
    InputStream result = null;

    String path = configDirectory + configFile;
    result = resourceLoader.getResourceAsStream( path );

    if ( debugFlag && result == null )
    {
      System.err.println( "Input stream resource " + configFile +
			  " not found in " + configDirectory + "." );
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Return a URL for a configuration file.  The URL is generated by a
   * URL class loader.  A base directory, specified by the variable
   * configDirectory, is used as a prefix to the file name.
   *
   * @param configFile The file name without any directory components.
   *
   * @return null if no resource is found or a URL suitable to read
   * the content.
   */

  public URL getConfigurationURL( String configFile )
  {
    URL result = resourceLoader.findResource( configDirectory + configFile );

    if ( debugFlag && result == null )
    {
      System.err.println( "Resource " + configFile + " not found in " +
			  configDirectory + "." );
    }

    return result;
  }

  /****************************************************************************/

  public ConstraintChecker getConstraintChecker()
  {
    return constraintChecker;
  }

  /****************************************************************************/
  /**
   * Return the current view.  If the current view is *not* an
   * instance of class View, then return null.
   */ 
  public View getCurrentView()
  {
    Component c = tabbedPanel.getSelectedComponent();
    View result;
    if ( !( c instanceof View ) )
    {
      result = null;
    }
    else
    {
      result = (View) c;
    }
    return( result );
  }

  /****************************************************************************/
  /**
   * Return the Editor database object.
   */

  public Database getDatabase()
  {
    return database;
  }
   
  /****************************************************************************/
  /**
   * Return the frame object.
   */

  public Hashtable<String,Filter> getFilters()
  {
    return filters;
  }  

  /****************************************************************************/
  /**
   * Return the frame object.
   */

  public JFrame getFrame()
  {
    return frame;
  }  

  /****************************************************************************/
  /**
   * Return the Editor instance.  Creating a new one if instance is null.
   */

  public static Editor getInstance()
  {
    if (instance == null)
    {
      instance = new Editor();
    }

    return(instance);
  }  

  /****************************************************************************/
  /**
   * Return the message handler object.
   */

  public MessageHandler getMessageHandler()
  {
    return(messageHandler);
  }

  /****************************************************************************/
  /**
   * Return the Editor properties.
   */

  public Properties getProperties()
  {
    return(properties);
  }
    
  /****************************************************************************/
  /**
   * Return the filter selector object.
   */

  public SecurityModel getSecurityModel()
  {
    return securityModel;
  }

  /****************************************************************************/
  /**
   * Return the tabbed panel used in the applet/application.
   */

  public JTabbedPane getTabbedPanel()
  {
    return tabbedPanel;
  }
    
  /****************************************************************************/

  public ViewMenu getViewMenu()
  {
    return( viewMenu );
  }

  /****************************************************************************/

  public void help()
  {
    String helpText = loadFile( properties.getProperty( "help.file" ) );

    int height = Integer.parseInt( properties.getProperty( "help.height" ) );
    int width = Integer.parseInt( properties.getProperty( "help.width" ) );

    JTextArea messageTextArea = new JTextArea( helpText, height, width );

    JScrollPane scrollPane = new JScrollPane();

    scrollPane.getViewport().add( messageTextArea );

    JOptionPane.showMessageDialog( frame, 
                                   scrollPane,
                                   "Obscat Editor Help",
                                   JOptionPane.PLAIN_MESSAGE );
  }

  /****************************************************************************/
  /**
   * Provide the hook for the history function - to satisfy the
   * HistoryInterface.  Provide history for either a component in a
   * component in a Form view.
   */

  public void history(int ival)
  {
    Component view = tabbedPanel.getSelectedComponent();

    if (( view != null ) && ( view instanceof FormView ))
    {
      ((FormView) view).showHistory(ival);
    }
  }

  /****************************************************************************/
  /**
   * The main setup routine for the <b>Editor</b> GUI.  This
   * routine is called directly from an applet and indirectly from
   * <code>main()</code>.
   */

  public void init()
  {
    // Set up debug flag based on command line argument -Dobsed.debug.
    String debugLevel = System.getProperty( "obsed.debug" );
    if( "1".equals( debugLevel ) )
    {
      System.out.println( "Debugging enabled" );
      debugFlag = true;
    }
    else
    {
      debugFlag = false;
    }

    // Setup the resource loader.
    if ( isApplet )
    {
      baseURL = getDocumentBase();
    }
    resourceLoader = URLClassLoader.newInstance( new URL[] { baseURL } );

    // Deal with ObsCat Editor specific properties.
    // Load the properties from the configuration file.
    String dataDir = "obsed/data/";
    URL url = null;

    try
    {
      // Load the system properties.
      InputStream is = getConfigurationInputStream( "ObsEdProperties.pf" );
      properties = new Properties();
      if ( is != null )
      {
	properties.load( is );
	is.close();
      }
    }
    catch ( IOException ioe )
    {
      String message = "Editor(): Could not load application properties.  " +
	"Using default values.";
      LogClient.logMessage( message );
    }

    // Establish the user's identity for logging messages.
    LogClient.setUsername( System.getProperty( "user.name" ) );

    // Start the database.
    database = new Database();

    // Create the change list object.
    changeList = new ChangeList( database.getDatabaseModel() );

    // Create the constraint checker.
    constraintChecker = new ConstraintChecker();

    // Setup the security manager.
    securityModel = new SecurityModel();

    // Setup and start the message handler.
    messageHandler = new MessageHandler();
    messageHandler.start();

    // Initialize the menubar.
    JMenuBar menuBar = initMenuBar();
    frame.setJMenuBar(menuBar);

    // Create the tabbed panel.  Establish the View menu as a listener
    // for changes on the tabbed panel.  And add the tabbed panel to
    // the main frame presenting an initial splash screen.
    tabbedPanel = new JTabbedPane( JTabbedPane.BOTTOM );
    tabbedPanel.addChangeListener( getViewMenu() );
    tabbedPanel.addTab( "Start", new SplashView() );
    frame.getContentPane().add(tabbedPanel, BorderLayout.CENTER);

    // Present the initial login screen.
    login();

    // Configure the containing frame and display it in the center of
    // the screen.
    int width = 
      Integer.parseInt( properties.getProperty( "frame.width", "800" ) );
    int height =
      Integer.parseInt( properties.getProperty( "frame.height", "600" ) );
    Dimension frameSize = 
      ComponentUtil.sizeToFitScreen( new Dimension( width, height) );
    frame.setSize( frameSize );
    frame.validate();
    frame.setLocation( ComponentUtil.center( frame ) );
    frame.setVisible(true);
  }

  /****************************************************************************/

  public JMenuBar initMenuBar()
  {
    JMenuBar result = new JMenuBar();

    FileMenu fileMenu = new FileMenu( new ExitListener( this ),
				      new PrintListener( this ) );

    result.add(fileMenu);

    EditMenu editMenu = new EditMenu( new DeleteRowListener( this ) );

    //    result.add(editMenu);

    viewMenu = new ViewMenu( this,
                             new HistoryListener( this ),
                             new PrintViewListener( this ),
                             new RefreshListener( this ),
			     new RemoveViewListener( this ),
			     new RenameViewListener( this ),
                             new SaveListener( this ),
                             new CancelListener( this ),
                             new SaveAllListener( this ),
                             new CancelAllListener( this ),
			     this );

    result.add(viewMenu);

    // Add control event listener to view menu.
    addControlListener( (ControlListener)viewMenu );

    PreferencesMenu preferencesMenu = new PreferencesMenu( this );

    result.add(preferencesMenu);

    SecurityMenu securityMenu = 
      new SecurityMenu( new LoginListener( this ) );
    
    result.add( securityMenu );

    JMenu helpMenu = new HelpMenu( new HelpListener( this ),
                                   new AboutListener( this) );

    result.add( helpMenu );

    // Designate the help menu for the menubar.
    //result.setHelpMenu(helpMenu);

    return( result );
  }

  /****************************************************************************/
  /**
   * Return debug flag.
   */

  public boolean isDebug()
  {
    return debugFlag;
  }

  /****************************************************************************/
  /**
   * Load a configuration file, returning the content as a string.
   *
   * @param fileName The file name containing the desired text.
   *
   * @return the input file content as a string
   */

  public String loadFile( String fileName )
  {
    StringBuffer result = new StringBuffer();

    // Read the file.
    try
    {
      URL url = getConfigurationURL( fileName );
      InputStreamReader inputStreamReader = 
        new InputStreamReader( url.openStream() );
      BufferedReader reader = new BufferedReader( inputStreamReader );
      while ( reader.ready() )
      {
        result.append( reader.readLine() );
        result.append( "\n" );
      }
    }
    catch ( Exception exception )
    {
      LogClient.printStackTrace( exception );
    }

    return( result.toString() );
  }

  /****************************************************************************/
  /**
   * Post a dialog to permit a User to login.  If the User has already
   * logged in but is merely selecting a new DB profile the process
   * will continue until either it is either canceled or a successful
   * Username/Password has been detected.  On an initial login, the
   * User is given three opportunities to successfully log in.  After
   * the third failure the application exits.
   */

  public void login()
  {
    int attempts = 0;

    if ( authenticated )
    {
      relogin();
    }
    else
    {
      while (! authenticated)
      {
        try
        {
          // Create a login dialogue in the middle of the screen.
	  String username = System.getProperty( "user.name" );
	  String servername = System.getProperty( "server.name" );
          securityModel.login( username, servername );
          
          authenticated = true;
        }
        catch (CouldNotLoginException couldNotLoginException)
        {
          if (attempts < 3)
          {
	    String message =
	      "Login failed: " + couldNotLoginException.getMessage() +
	      "\nPlease try again.";
            JOptionPane.showMessageDialog(frame, message, 
                                          "Login Error Dialog",
                                          JOptionPane.ERROR_MESSAGE );
            attempts++;
          }
          else
          {
            System.exit(1);
          }
        }
      }
    }
  }

  /****************************************************************************/
  /**
   * Runs <code>Editor</code> as an application.
   */

  public static void main( String[] args )
  {
    Editor app = new Editor();

    try
    {
      // Get the base URL using the first argument.
      app.baseURL = new URL( args[0] );
    }
    catch ( MalformedURLException exc )
    {
      System.err.println( "The input URL (" + args[0] + ") is badly formed." +
			  "  Please try again." );
      System.exit( 1 );
    }

    // Undo the constructor initialization that assumed an applet and
    // complete the initialization.
    app.setApplet(false);
    app.init();
  }

  /****************************************************************************/
  /**
   * Create new record.  Satisfy <i>NewRecordInterface</i> implementation.
   *
   * @param seedingMode the seeding mode to be used to create the new
   * record (should be one of the integer constants from the Constants
   * class: NEWCLONE, NEWDEFAULT, NEWNULL).
   */
  public void newRecord( int seedingMode )
  {
    View view = (View) tabbedPanel.getSelectedComponent();
    view.createNewRecord( seedingMode ); 
  }

  /****************************************************************************/

  public void print()
  {
    Printer printer = new Printer();

    printer.print(frame);
  }

  /****************************************************************************/

  public void printView()
  {
    Component view = tabbedPanel.getSelectedComponent();

    if (view != null)
    {
      Printer printer = new Printer();

      printer.print(view);
    }
  }

  /****************************************************************************/
  
  public void refresh()
  {
    Component view = tabbedPanel.getSelectedComponent();

    if ((view != null) && (view instanceof View))
    {
      messageHandler.addMessage( new ViewMessage( Message.REFRESH, 
						  (View) view) );
    }
  }

  /****************************************************************************/

  public void relogin()
  {
    boolean reauthenticated = false;

    while (! reauthenticated)
    {
      try
      {
        securityModel.relogin();

        reauthenticated = true;
      }
      catch (CouldNotLoginException couldNotLoginException)
      {
        JOptionPane.showMessageDialog(frame, 
                                      "Login failed.  Please try again", 
                                      "Login Error Dialog",
                                      JOptionPane.ERROR_MESSAGE );
      }
    }
  }

  /****************************************************************************/
  /**
   * Remove an event listener.
   */

  public void removeControlListener( ControlListener l )
  {
    controlListenerList.remove( ControlListener.class, l );
  }
  
  /****************************************************************************/
  /**
   * Remove the current view.
   */

  public void removeView()
  {
    Component c = tabbedPanel.getSelectedComponent();
    removeView( (View) c );
  }

  /****************************************************************************/
  /**
   * Remove <i>view</i> from the tabbed panel.
   */

  public void removeView( View view )
  {
    tabbedPanel.remove( view );
  }

  /****************************************************************************/
  /**
   * Rename the current view.
   */

  public void renameView()
  {
    // Present a text box to type in a new name.  Then change the name
    // on the current tabbed pane tab.

    String name =
      JOptionPane.showInputDialog( "Specify a new name for this view: " );
    if ( name != null )
    {
      int i = tabbedPanel.getSelectedIndex();
      tabbedPanel.setTitleAt( i, name );
    }

  }

  /****************************************************************************/
  /**
   * Save the modifications to the currently selected record in the
   * current view.
   */

  public void save()
  {
    View view = (View) tabbedPanel.getSelectedComponent();
   
    try
    {
      // Save the current changes looking for exceptions.
      view.saveCurrentChanges();

      // Tell listeners about the save event.
      ControlEvent controlEvent = new ControlEvent( "SAVE" );
      fireControlEvent( controlEvent );
    }
    catch (ConfigurationException configurationException)
    {
      JOptionPane.showMessageDialog( frame, 
                                     configurationException.getMessage(), 
                                     "Configuration Error",
                                     JOptionPane.ERROR_MESSAGE );
    }
    catch (ConstraintViolationException constraintViolationException)
    {
      JOptionPane.showMessageDialog( frame, 
                                     constraintViolationException.getMessage(), 
                                     "Constraint Error",
                                     JOptionPane.ERROR_MESSAGE );

      DatabaseKey databaseKey = constraintViolationException.getDatabaseKey();

      // Select the component where the violation occurred.
      //FIXME
    }
    catch (UpdateFailedException updateFailedException)
    {
      JOptionPane.showMessageDialog( frame, 
                                     updateFailedException.getMessage(), 
                                     "Database Update Error",
                                     JOptionPane.ERROR_MESSAGE );
    }
  }

  /****************************************************************************/

  public void saveAll()
  {
    View view = (View) tabbedPanel.getSelectedComponent();
   
    view.saveAllChanges(); 

    // Tell listeners about the save all event.
    ControlEvent controlEvent = new ControlEvent( "SAVE_ALL" );
    fireControlEvent( controlEvent );
  }

  /****************************************************************************/

  public void setApplet(boolean isApplet)
  {
    this.isApplet = isApplet;
  }

  /****************************************************************************/
  /** 
   * Repaint the screen for the current view.
   */

  public void setChangeList( ChangeList changeList )
  {
    this.changeList = changeList;
  }

  /****************************************************************************/
  /**
   * Set the cursor on the main frame to the default hand pointer.
   */

  public void setDefaultCursor()
  {
    frame.setCursorBlock( false );
    frame.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
  }

  /****************************************************************************/
  /**
   * Set the title on the tab associated with a particular view.
   */

  public void setTitle( View view, String title )
  {
    int index = tabbedPanel.indexOfComponent( view );
    tabbedPanel.setTitleAt( index, title );
  }

  /****************************************************************************/
  /**
   * Set the selected view.
   *
   * @param view The view
   * @param keyValue The record ID to view.
   */

  public void setView( View view, Object keyValue )
  {
    // Select the view and key value.
    tabbedPanel.setSelectedComponent( view );
    view.setSelectedKeyValue( keyValue );
    view.update();
  }

  /****************************************************************************/
  /**
   * Set the cursor on the main frame to a clock face.
   */

  public void setWaitCursor()
  {
    frame.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
    frame.setCursorBlock( true );
  }

  /****************************************************************************/


  /****************************************************************************/

}

/******************************************************************************/
