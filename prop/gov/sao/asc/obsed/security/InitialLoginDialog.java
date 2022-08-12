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

package gov.sao.asc.obsed.security;

/******************************************************************************/

import gov.sao.asc.event.OKInterface;
import gov.sao.asc.event.OKButtonListener;
import gov.sao.asc.event.ExitInterface;
import gov.sao.asc.event.ExitListener;
import gov.sao.asc.util.GridBagLayoutUtil;
import gov.sao.asc.util.LogClient;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/******************************************************************************/
/**
 * The <b>InitialLoginDialog</b> provides support for the initial
 * login dialog presented to a User upon invocation of the Editor.  It
 * allows the User to specify the DB user name and password as well as
 * the DB server name.  It also allows the User to abort the login
 * process.
 */

public class InitialLoginDialog extends LoginDialog
  implements ExitInterface
{
  /****************************************************************************/
  // Constructors.

  /**
   * Construct an initial login dialog object using the given username
   * and servername to seed the dialog text boxes.
   *
   * @param userName The DB user ID.
   * @param serverName The DB server ID.
   */

  public InitialLoginDialog( String userName, String serverName )
  {
    super( userName, serverName );
  }

  /****************************************************************************/
  // Public Methods.

  /**
   * Capture the fact that the login process either failed or was canceled.
   */

  public void exit()
  {
    exited = true;
    setVisible( false );
  }

  /****************************************************************************/
  /**
   * Set up the initial dialog controls.
   */

  public void init()
  {
    // Initialize the dialog, setting the title, obtaining a content
    // pane and establishing the layout policy to be GridBag.
    setTitle( "DB Login Controls:" );
    Container contentPane = getContentPane();
    contentPane.setLayout( new GridBagLayout() );

    // Layout the DB user name label and text box.
    GridBagLayoutUtil.addComponent( contentPane, new JLabel( "Username: " ),
				    0, 0, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.EAST,
				    1.0, 0.5, 10, 5, 5, 5 );
    GridBagLayoutUtil.addComponent( contentPane, userTextField,
				    1, 0, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.5, 10, 5, 5, 5 );

    // Layout the DB password label and password box.
    GridBagLayoutUtil.addComponent( contentPane, new JLabel( "Password: " ),
				    0, 1, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.EAST,
				    1.0, 0.5, 5, 10, 5, 5 );
    GridBagLayoutUtil.addComponent( contentPane, passwordField,
				    1, 1, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.5, 5, 5, 10, 5 );

    // Layout the DB server label and text box.
    GridBagLayoutUtil.addComponent( contentPane, new JLabel( "Server: " ),
				    0, 2, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.EAST,
				    1.0, 0.5, 10, 5, 5, 5 );
    GridBagLayoutUtil.addComponent( contentPane, serverTextField,
				    1, 2, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.5, 10, 5, 5, 5 );

    // Layout the control buttons: OK and Exit (cancel).  Put them in
    // a separate panel.
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout( new GridBagLayout() );

    // Layout the OK button.
    JButton okButton = new JButton("OK");
    okButton.addActionListener( new OKButtonListener(this) );
    GridBagLayoutUtil.addComponent( buttonPanel, okButton,
				    0, 0, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.5, 5, 5, 5, 5 );

    // Layout the exit (cancel) button.
    JButton exitButton = new JButton( "Exit" );
    exitButton.addActionListener( new ExitListener( this ) );
    GridBagLayoutUtil.addComponent( buttonPanel, exitButton,
                                    1, 0, 1, 1,
                                    GridBagConstraints.NONE,
                                    GridBagConstraints.WEST,
                                    1.0, 0.5, 5, 5, 5, 5 );

    // Add the controls to the login dialog.
    GridBagLayoutUtil.addComponent( contentPane, buttonPanel,
				    0, 3, 2, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.CENTER,
				    1.0, 1.0, 5, 5, 5, 5 );
  }

  /****************************************************************************/
  /**
   * Indicate whether or not the login process either was canceled or failed.
   *
   * @returns <b>true</b> if the login process failed or was aborted,
   * <b>false</b> otherwise.
   */

  public boolean wasExited()
  {
    return( exited );
  }

  /****************************************************************************/

  // Private Methods.

  /****************************************************************************/

  // Private Variables.

  /**
   * State information on the success/failure of the login process.
   */

  private boolean exited;
  
  /****************************************************************************/
  
}

/******************************************************************************/
