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

import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.database.Database;
import gov.sao.asc.obsed.database.DBServerMapper;
import gov.sao.asc.obsed.database.CouldNotConnectException;
import gov.sao.asc.obsed.database.UnrecognizedServerException;
import gov.sao.asc.obsed.database.UnrecognizedTagException;
import gov.sao.asc.util.ComponentUtil;
import gov.sao.asc.util.LogClient;
import java.awt.Container;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/******************************************************************************/
/**
 * Provides the facilities that deal with security, specifically
 * logging in to the DB and selecting a DB server.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class SecurityModel extends Object
{

  /****************************************************************************/
  /**
   * Construct the security model object.
   */

  public SecurityModel()
  {
    editor = Editor.getInstance();
    db = editor.getDatabase();
  }

  /****************************************************************************/
  /**
   * Authenticate the user.  If <i>username</i> is non-null use it as
   * the seed value for a user name text box.  If it is null, then
   * probe the environment variable, <b>?</b> for a seed value.
   * Otherwise seed the text box with an empty string.  After both a
   * username and a password have been provided validate that the
   * username/password pair is acceptable to the Database.  Make the
   * validation attempt three times.
   *
   * @param userName The DB user name.
   * @param serverName The DB server name.
   *
   * @throws CouldNotLoginException if the username/password pair 
   */

  public void login( String userName, String serverName )
    throws CouldNotLoginException
  {
    // Put up the initial login dialog in the middle of the screen.
    InitialLoginDialog loginDialog = 
      new InitialLoginDialog( userName, serverName );
    loginDialog.setLocation( ComponentUtil.center( loginDialog ) );
    loginDialog.setVisible(true);

    // Exit if the login dialog was aborted.
    if ( loginDialog.wasExited() )
    {
      editor.exit();
    }

    // Set the DB server/username/password parameters.
    setLogin( loginDialog.getUserName(),
	      loginDialog.getPassword(),
	      loginDialog.getServerName() );
  }

  /****************************************************************************/
  /**
   * Authenticate the user using another pass phrase.  First create a
   * ReLoginDialog, center it, then make it visible.  Then wait until
   * it gets hidden.  After it is hidden, check to see if the user
   * cancelled.  If not, get the alias and passphrase, then process
   * the secure files.
   */

  public void relogin() throws CouldNotLoginException
  {
    // Put up the login dialog in the middle of the screen.
    ReLoginDialog loginDialog =
      new ReLoginDialog( db.getUser(), db.getServer() );
    loginDialog.setLocation( ComponentUtil.center( loginDialog ) );
    loginDialog.setVisible(true);

    // Determine if the login process was canceled.
    if ( ! loginDialog.wasCanceled() )
    {
      // It wasn't.  Set the new DB server/username/password.
      setLogin( loginDialog.getUserName(),
		loginDialog.getPassword(),
		loginDialog.getServerName() );
    }
  }

  /****************************************************************************/
  /**
   * Set the login information: the current DB username/password pair
   * and the unique tag identifying the server name, and database name.
   *
   * @param username
   * @param password
   * @param server
   */

  public void setLogin( String username,
			char[] password,
			String server )
    throws CouldNotLoginException
  {
    // Close any existing DB connections.
    db.close();
    
    // Open a new connection.
    String message;
    try
    {
      // Map the server to a system address and port number.
      DBServerMapper mapper = new DBServerMapper( server );
      db.setServer( server );
      db.setInterfacesFilename( mapper.getInterfacesFilename() );
      db.setUser( username );
      db.setPassword( password );
      db.connect();

      // Flush the permissions cache for the new user.
      db.resetPermissions();
    }
    catch ( CouldNotConnectException cne )
    {
      LogClient.printStackTrace( cne );
      message = 
	new String( "Connection error.  Could not connect to DB server: `" +
		    server + "' as User `" + username + "'" );
      throw new CouldNotLoginException( message, server, username );
    }
    catch ( UnrecognizedServerException use )
    {
      LogClient.printStackTrace( use );
      message = new String( "Unrecognized Server error.  " +
			    "Could not connect to DB server: `" +
			    server + "' as User `" + username + "'" );
      throw new CouldNotLoginException( message, server, username );
    }
    catch ( IOException iox )
    {
      LogClient.printStackTrace( iox );
      message = new String( "Failed to read servers file (/soft/sybase/interfaces).  " +
			    "Could not connect to DB server: `" +
			    server + "' as User `" + username + "'" );
      throw new CouldNotLoginException( message, server, username );
    }
  }

  /****************************************************************************/
  // Private variables

  /**
   * A reference to the single database object.  Convenience variable.
   */

  private Database db;

  /**
   * A reference to the single Editor object.  Convenience variable.
   */

  private Editor editor;

  /****************************************************************************/

}

/******************************************************************************/
