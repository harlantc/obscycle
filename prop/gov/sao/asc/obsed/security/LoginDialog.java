/*
  Copyrights:
 
  Copyright (c) 1998, 1999, 2000 Smithsonian Astrophysical Observatory
 
  Permission to use, copy, modify, distribute, and sell this software
  and its documentation for any purpose is hereby granted without fee,
  provided that the above copyright notice appear in all copies and
  that both that copyright notice and this permission notice appear in
  supporting docu- mentation, and that the name of the Smithsonian
  Astro- physical Observatory not be used in advertising or publicity
  pertaining to distribution of the software without specific, written
  prior permission.  The Smithsonian Astrophysical Observatory makes
  no representations about the suitability of this software for any
  purpose.  It is provided "as is" without express or implied
  warranty.  THE SMITHSONIAN INSTITUTION AND THE SMITHSONIAN ASTRO-
  PHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES WITH REGARD TO THIS
  SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANT- ABILITY AND
  FITNESS, IN NO EVENT SHALL THE SMITHSONIAN INSTITUTION AND/OR THE
  SMITHSONIAN ASTROPHYSICAL OBSERVATORY BE LIABLE FOR ANY SPECIAL,
  INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER
  RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
  CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
  CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/
/******************************************************************************/

package gov.sao.asc.obsed.security;

/******************************************************************************/

import gov.sao.asc.event.OKInterface;
import gov.sao.asc.event.OKButtonListener;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/******************************************************************************/
/**
 * Provides basic support for a login dialog.
 */

public abstract class LoginDialog extends JDialog
  implements OKInterface
{

  // Constructors

  /****************************************************************************/
  
  public LoginDialog( String userName, String serverName )
  {
    super( new JFrame(), true );

    // Construct and seed the GUI components.  Start with the user
    // name and password.
    passwordField = new JPasswordField( PASSWORD_FIELD_WIDTH );
    userTextField = new JTextField( USERNAME_FIELD_WIDTH );
    if ( userName != null )
    {
      userTextField.setText( userName );
    }

    // Seed the server name.
    serverTextField = new JTextField( SERVERNAME_FIELD_WIDTH );
    if ( serverName != null )
    {
      serverTextField.setText( serverName );
    }

    // Layout and present the dialog.
    init();
    setSize( 500, 300 );
  }

  /****************************************************************************/
  /**
   * Access the DB password.
   *
   * @returns The DB password.
   */

  public char[] getPassword()
  {
    return( passwordField.getPassword() );
  }

  /****************************************************************************/
  /**
   * Access the DB server name.
   *
   * @returns The DB server name.
   */

  public String getServerName()
  {
    return serverTextField.getText();
  }

  /****************************************************************************/
  /**
   * Access the DB user name.
   *
   * @returns The DB user name.
   */

  public String getUserName()
  {
    return userTextField.getText();
  }

  /****************************************************************************/
  /**
   * Place the GUI components onto the base dialog.
   */

  public abstract void init();

  /****************************************************************************/
  /**
   * Remove the dialog from the screen.
   */

  public void ok()
  {
    setVisible( false );
  }

  /****************************************************************************/
  // Protected Variables

  /**
   * GUI for the User to provide a DB user name.  Seeded with a value during construction.
   */
  protected JTextField userTextField;

  /**
   * GUI for the User to select a DB server name.  Seeded with a value
   * during construction.
   */
  protected JTextField serverTextField;

  /**
   * GUI for the User to provide a DB password.
   */
  protected JPasswordField passwordField;
  
  /****************************************************************************/
  // Private Variables

  /**
   * Field width constant.
   */
  private final int PASSWORD_FIELD_WIDTH = 16;

  /**
   * Field width constant.
   */
  private final int USERNAME_FIELD_WIDTH = 16;

  /**
   * Field width constant.
   */
  private final int SERVERNAME_FIELD_WIDTH = 16;


  /****************************************************************************/
  
}

/******************************************************************************/
