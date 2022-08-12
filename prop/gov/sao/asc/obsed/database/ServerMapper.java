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

import gov.sao.asc.util.Logger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

/******************************************************************************/
/**
 * The <code>ServerMapper</code> class maps server names to a
 * description, system name and a port.  A server identifies a Sybase
 * engine.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G% */

public class ServerMapper extends Hashtable
{

  /****************************************************************************/
  /**
   * The default constructor sets up the base map.
   *
   * @param configFile   a URL string for the page containing
   *                     the configuration data.
   */

  public ServerMapper( String configFile )
  {
    super();

    String dbIdentifier;
    String line;
    String portNumber;
    String serverDescription;
    String serverName;
    Properties serverProperties;
    String systemName;
    StringTokenizer tokenizer;

    // Read the configuration file setting up a server per line.
    try
    {
      URL url = new URL( configFile );
      InputStreamReader inputStreamReader = 
	new InputStreamReader( url.openStream() );
      BufferedReader reader = new BufferedReader( inputStreamReader );

      while ( reader.ready() )
      {
	// Read a line from the configuration file.
	line = reader.readLine();

	// Ignore comment lines.
	if ( !line.startsWith( "#" ) )
	{
	  // Break the input into tokens.
	  tokenizer = new StringTokenizer( line, ":" );
	  if ( tokenizer.hasMoreTokens() )
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
	    serverProperties = new Properties();
	    serverProperties.put( "description", serverDescription );
	    serverProperties.put( "system", systemName );
	    serverProperties.put( "port", portNumber );
	    serverProperties.put( "database", dbIdentifier );

	    // Associate the server name with this information.
	    put( serverName, serverProperties );
	  }
	}
      }
    }
    catch ( Exception e )
    {
      Logger.printStackTrace( e );
    }
  }

  /****************************************************************************/
  /**
   * Maps the specified <code>server</code> to a database identifier.  Returns
   * the associated id or null if there is none.
   * <p>
   *
   * @param      server  the server name.
   * @return     the server database identifier or null if the server name is
   *             not recognized.
   */

  public String mapDBIdentifier( String server )
  {
    String result;

    Properties props = (Properties) get( server );
    if ( props == null )
    {
      result = null;
    }
    else
    {
      result = (String) props.get( "database" );
    }
    return result;
  }


  /****************************************************************************/

  /**
   * Maps the specified <code>server</code> to a description.  Returns
   * the associated description or null if there is none.
   * <p>
   *
   * @param      server  the server name.
   * @return     the server description or null if the server name is
   *             not recognized.
   */

  public String mapDescription( String server )
  {
    String result;

    Properties props = (Properties) get( server );
    if ( props == null )
    {
      result = null;
    }
    else
    {
      result = (String) props.get( "description" );
    }
    return result;
  }


  /****************************************************************************/

  /**
   * Maps the specified <code>server</code> to a port number.  Returns
   * the associated port number or null if there is no such server.
   * <p>
   *
   * @param      server  the server name.
   * @return     the port number or null if the server name is not
   *             recognized.
   */

  public String mapPort( String server )
  {
    String result;

    Properties props = (Properties) get( server );
    if ( props == null )
    {
      result = null;
    }
    else
    {
      result = (String) props.get( "port" );
    }
    return result;
  }

  /****************************************************************************/

  /**
   * Maps the specified <code>server</code> to a system name.  Returns
   * the associated system name or null if there is none.
   * <p>
   *
   * @param      server  the server name.
   * @return     the system name as a fully qualified internet system
   *             name (includes domain).  Return null if the server
   *             name is not recognized.
   */

  public String mapSystem( String server )
  {
    String result;

    Properties props = (Properties) get( server );
    if ( props == null )
    {
      result = null;
    }
    else
    {
      result = (String) props.get( "system" );
    }
    return result;
  }

  /****************************************************************************/

  public static void main( String[] args )
  {
    String server;
    String description;
    String system;
    String port;
    Properties props;

    String url = "http://www-axaf.harvard.edu:8024/obsed/data";
    ServerMapper map = new ServerMapper( url );

    for ( Enumeration e = map.keys(); e.hasMoreElements(); )
    {
      server = (String) e.nextElement();
      props = (Properties) map.get( server );
      description = (String) props.get( "description" );
      system = (String) props.get( "system" );
      port = (String) props.get( "port" );
      System.out.println( "Server: " + server + " : " + description + ", " +
			  system + ", " + port );
    }
    System.exit( 0 );
  }

  /****************************************************************************/
}

/******************************************************************************/
