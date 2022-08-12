/*
  Copyrights:
 
  Copyright (c) 1998, 1999, 2000 Smithsonian Astrophysical Observatory
 
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
  warranty.  THE SMITHSONIAN INSTITUTION AND THE SMITHSONIAN ASTRO-
  PHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES WITH REGARD TO THIS
  SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND
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

import gov.sao.asc.event.CancelButtonListener;
import gov.sao.asc.event.CancelInterface;
import gov.sao.asc.event.OKButtonListener;
import gov.sao.asc.event.OKInterface;
import gov.sao.asc.util.GridBagLayoutUtil;
import gov.sao.asc.util.LogClient;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/******************************************************************************/
/**
 * Provide a dialog suitable for use to change the DB access
 * username/password/server configuration.
 */

public class ReLoginDialog extends LoginDialog
  implements CancelInterface
{
  boolean canceled;
  
  /****************************************************************************/
  // Constructors

  /**
   * Construct a ReLoginDialog using seeds for the user name, server
   * name and server list.
   */

  public ReLoginDialog( String userName, String serverName )
  {
    super( userName, serverName );
  }

  /****************************************************************************/
  // Public Methods.

  /**
   * Abort the relogin process.
   */

  public void cancel()
  {
    canceled = true;
    setVisible( false );
  }

  /****************************************************************************/
  /**
   * Layout and display the relogin dialog.
   */

  public void init()
  {
    // Initialize the dialog, setting the title, obtaining a content
    // pane and establishing the layout policy to be GridBag.
    setTitle( "DB Access Controls Selection:" );
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
    okButton.addActionListener( new OKButtonListener( this ) );
    GridBagLayoutUtil.addComponent( buttonPanel, okButton,
				    0, 0, 1, 1,
				    GridBagConstraints.NONE,
				    GridBagConstraints.WEST,
				    1.0, 0.5, 5, 5, 5, 5 );

    // Layout the cancel button.
    JButton cancelButton = new JButton( "Cancel" );
    cancelButton.addActionListener( new CancelButtonListener( this ) );
    GridBagLayoutUtil.addComponent( buttonPanel, cancelButton,
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
   * Indicate whether or not the relogin process was canceled.
   *
   * @returns <b>true</b> if the relogin process was canceled,
   * <b>false</b> otherwise.
   */

  public boolean wasCanceled()
  {
    return( canceled );
  }

  /****************************************************************************/
  
}

/******************************************************************************/
