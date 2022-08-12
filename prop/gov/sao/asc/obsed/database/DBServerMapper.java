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

package gov.sao.asc.obsed.database;

/******************************************************************************/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

/******************************************************************************/
/**
 * Provide services for mapping a server name to an IP address and a
 * port number.
 *
 * @note This class must be moved into a servlet in order to preserve
 * the notion of ObsEd running as an applet (or via a Java plug-in).
 */

public class DBServerMapper
{

  // Constructors
  
  /**
   * Constructs the mapping between a server name and it's associated
   * IP address and port number.
   */

  public DBServerMapper( String serverName )
    throws UnrecognizedServerException, IOException
  {
    // Error/status message.
    String message;

    try
    {
      // Use a servlet to map the server name to a system address and
      // port number.
      String mapperURL = System.getProperty( "servletEngine.url",
					     "http://cxc.harvard.edu" );
      URL url = new URL( mapperURL + "/obsed/MapServer" );
      URLConnection connection = url.openConnection();
      connection.setDoInput( true );
      connection.setDoOutput( true );
      connection.setUseCaches( false );
      connection.setRequestProperty( "Content-Type",
				     "application/x-www-form-urlencoded" );
      PrintWriter writer = new PrintWriter( connection.getOutputStream(), true );
      writer.print( "server=" + serverName );
      writer.close();

      // Extract the results.
      if ( connection.getContentLength() != 0 )
      {
	// Either the content length is indeterminate or is is
	// positive.  In either case attempt to read the input stream.
	InputStreamReader inputStreamReader =
	  new InputStreamReader( connection.getInputStream() );
	BufferedReader input = new BufferedReader( inputStreamReader );
	String inputLine = "";
	while ( input.ready() )
	{
	  inputLine += input.readLine();
	}
	input.close();

	// Extract the servlet tokens using a string tokenizer.  The
	// format is: <status>,<port number>,<system address>
	StringTokenizer tokenizer = new StringTokenizer( inputLine, "," );
	status = tokenizer.nextToken();

	// Process the status
	if ( ! "OK".equals( status ) )
	{
	  if ( "BSN".equals( status ) )
	  {
	    // Bogus Server Name
	    message = "Unrecognized server name: " + serverName;
	    throw new UnrecognizedServerException( message, serverName );
	  }
	  else if ( "IOX".equals( status ) )
	  {
	    // IO Exception reading interfaces file.
	    message = "Could not read interfaces file.";
	    throw new UnrecognizedServerException( message, serverName );
	  }
	  else if ( "REX".equals( status ) )
	  {
	    message = "Unexpected regular expression exception.";
	    throw new UnrecognizedServerException( message, serverName );
	  }
	}
	else
	{
	  // Got an OK status.  Get the port number and system
	  // address.
	  interfacesFilename = tokenizer.nextToken();
	}
      }
      else
      {
	message = "Empty content returned from servlet.";
	throw new UnrecognizedServerException( message, serverName );
      }
    }
    catch ( Exception exc )
    {
      message = "Unexpected exception: " + exc.getMessage();
      exc.printStackTrace();
      throw new UnrecognizedServerException( message, serverName );
    }
  }
    
	
  /**
   * Return the DB server system IP address in dotted decimal format.
   */

  public String getInterfacesFilename()
  {
    return interfacesFilename;
  }


  /**
   * Use the <code>main()</code> method to provide a testing harness.
   */

  public static void main( String[] args )
  {
    try
    {
      DBServerMapper mapper = new DBServerMapper( args[0] );
      System.out.println( args[0] + " using : " +
			  mapper.getInterfacesFilename() + "." );
    }
    catch ( IOException iox )
    {
      System.err.println( "IO exception: " );
      iox.printStackTrace();
    }
    catch ( UnrecognizedServerException use )
    {
      System.err.println( "Server not recognized." );
      use.printStackTrace();
    }
    catch ( Exception exc )
    {
      System.err.println( "Unexpected exception" );
      exc.printStackTrace();
    }
  }

  // Private Variables and Constants

  /**
   * The sybase interfaces file
   */
  String interfacesFilename;

  /**
   * Status code.
   */
  String status;


}
