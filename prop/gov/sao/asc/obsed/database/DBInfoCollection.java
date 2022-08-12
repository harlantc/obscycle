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

package gov.sao.asc.obsed.database;

/******************************************************************************/

import gov.sao.asc.obsed.Editor;
import gov.sao.asc.util.LogClient;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.JarURLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/******************************************************************************/
/**
 * The <code>DBInfoCollection</code> class maintains a collection of
 * DB information, such as server names, DB names, descriptions,
 * ports, etc.  A unique tag is used to access a particular set of DB
 * information.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G% */

public class DBInfoCollection extends Object
{
  /**
   * Implementation is a hashtable.
   */
  private Hashtable collection;

  /****************************************************************************/
  /**
   * The default constructor sets up the base map.
   *
   * @param configFile   a URL string for the page containing
   *                     the configuration data.
   */

  public DBInfoCollection()
  {
    super();

    String dbIdentifier;
    String line;
    String portNumber;
    String serverDescription;
    String serverName;
    Properties properties;
    String systemName;
    String tag;
    StringTokenizer tokenizer;

    collection = new Hashtable();

    try
    {
    // Read the configuration file setting up a server per line.
      Editor editor = Editor.getInstance();
      InputStream is = editor.getConfigurationInputStream( "Servers.cf" );
      InputStreamReader inputStreamReader = new InputStreamReader( is );
      BufferedReader reader = new BufferedReader( inputStreamReader );

      // Process each line.
      while ( reader.ready() )
      {
	// Read a line from the configuration file.
	line = reader.readLine();

	if ( line == null )
	{
	  // This cass is equivalent to an end of file and is caused
	  // by unexplained behavior from the use of a jar file.
	  reader.close();
	  return;
	}

	// Ignore comment lines.
	if ( !line.startsWith( "#" ) )
	{
	  // Break the input into tokens.
	  tokenizer = new StringTokenizer( line, ":" );
	  if ( tokenizer.hasMoreTokens() )
	  {
	    // Pick off the unique tag and validate that it is unique.
	    // Reject the record if the tag has already been used.
	    tag = tokenizer.nextToken();
	    if ( collection.get( tag ) == null )
	    {

	      // Pick off the server name.
	      serverName = tokenizer.nextToken();
	    
	      // Pick off the server description.
	      serverDescription = tokenizer.nextToken();

	      // Pick off the system name.
	      systemName = tokenizer.nextToken();

	      // Pick off the port number.
	      portNumber = tokenizer.nextToken();

	      // Pick off the database identifier.
	      dbIdentifier = tokenizer.nextToken();
	    
	      // Put the server information into a properties object.
	      properties = new Properties();
	      properties.put( "server", serverName );
	      properties.put( "description", serverDescription );
	      properties.put( "system", systemName );
	      properties.put( "port", portNumber );
	      properties.put( "database", dbIdentifier );

	      // Associate the unique tag with this set of properties.
	      collection.put( tag, properties );
	    }
	    else
	    {
	      String message =
		"DBInfoCollection.DBInfoCollection(): " +
		"Overloaded (ignored) tag used in line:" + line;
	      LogClient.logMessage( message );
	      JOptionPane.showMessageDialog( null, message, "Unique tag error",
					     JOptionPane.WARNING_MESSAGE );
	    }
	  }
	}
      }
      
      // Clean up.
      reader.close();
    }
    catch ( Exception e )
    {
      LogClient.printStackTrace( e );
    }
  }

  /****************************************************************************/
  /**
   * Maps the specified <code>tag</code> to a database name.  Throws
   * an UnrecognizedTagException if no such mapping exists.
   * <p>
   *
   * @param      tag  a unique mapping tag.
   * @return     the server database name.
   */

  public String getDBName( String tag )
    throws UnrecognizedTagException
  {
    String result = null;

    Properties props = (Properties) collection.get( tag );
    if ( props == null )
    {
      throw new UnrecognizedTagException( "Failed to obtain DB name.", tag );
    }
    else
    {
      result = (String) props.get( "database" );
    }
    return result;
  }


  /****************************************************************************/

  /**
   * Maps the specified <code>tag</code> to a description.  Throws
   * an UnrecognizedTagException if no such mapping exists.
   * <p>
   *
   * @param      tag  a unique mapping tag.
   * @return     the server description.
   */

  public String getDescription( String tag )
    throws UnrecognizedTagException
  {
    String result = null; 

    Properties props = (Properties) collection.get( tag );
    if ( props == null )
    {
      throw new UnrecognizedTagException( "Failed to obtain description.", tag );
    }
    else
    {
      result = (String) props.get( "description" );
    }
    return result;
  }

  /****************************************************************************/
  /**
   * Maps the specified <code>tag</code> to a port number. Throws
   * an UnrecognizedTagException if no such mapping exists.
   * <p>
   *
   * @param      tag  the server name.
   * @return     the port number.
   */

  public String getPort( String tag )
    throws UnrecognizedTagException
  {
    String result = null;

    Properties props = (Properties) collection.get( tag );
    if ( props == null )
    {
      throw new UnrecognizedTagException( "Failed to obtain port.", tag );
    }
    else
    {
      result = (String) props.get( "port" );
    }
    return result;
  }

  /****************************************************************************/
  /**
   * Provide a list of the servers.
   *
   * @return A list of the servers.
   */

  public Set getServers()
  {
    return collection.keySet();
  }

  /****************************************************************************/
  /**
   * Maps the specified <code>tag</code> to a server name.  Throws
   * an UnrecognizedTagException if no such mapping exists.
   * <p>
   *
   * @param      tag  a unique mapping tag.
   * @return     the server name.
   */

  public String getServer( String tag )
    throws UnrecognizedTagException
  {
    String result = null;

    Properties props = (Properties) collection.get( tag );
    if ( props == null )
    {
      throw new UnrecognizedTagException( "Failed to obtain server name.", tag );
    }
    else
    {
      result = (String) props.get( "server" );
    }
    return result;
  }

  /****************************************************************************/
  /**
   * Maps the specified <code>tag</code> to a system name.  Throws an
   * UnrecognizedTagException if there is no valid mapping.
   *
   * @param      server  a unique mapping tag.
   * @return     the system name as a fully qualified internet system
   *             name (includes domain).
   */

  public String getSystem( String tag )
    throws UnrecognizedTagException
  {
    String result = null;

    Properties props = (Properties) collection.get( tag );
    if ( props == null )
    {
      throw new UnrecognizedTagException( "Failed to obtain system name.", tag );
    }
    else
    {
      result = (String) props.get( "system" );
    }
    return result;
  }

  /****************************************************************************/
  /**
   * Override the toString() method.
   *
   * @return     the system name as a fully qualified internet system
   *             name (includes domain).
   */

  public String toString()
  {
    String result = null;

    result = collection.toString();

    return result;
  }

  /****************************************************************************/

}

/******************************************************************************/
