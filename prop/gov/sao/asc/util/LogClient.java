/*
  Copyrights:
 
  Copyright (c) 1998, 2000 Smithsonian Astrophysical Observatory
 
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

package gov.sao.asc.util;

/******************************************************************************/

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/******************************************************************************/
/**
 * Provide a general mechanism for logging error messages.
 */

public class LogClient extends Object
{
  /****************************************************************************/
  /**
   * Log a message to a web server using basic URL encoded
   * client/servlet messaging.  Also display the message on the User's
   * console.
   */

  public static void logMessage( String message )
  {
    String errorMessage = null;

    try
    {
      // Establish a connection to the servlet engine.  The default
      // can be overridden by a command line switch.
      String servletEngineURL =
	System.getProperty( "servletEngine.url", 
			    "http://www.axaf.harvard.edu");
      URL url = new URL( servletEngineURL + "/logger/LogServer" );
      URLConnection connection = url.openConnection();

      // Set up the servlet to handle both input and output.  Disable
      // caching.  Handle encoded form content.
      connection.setDoInput( true );
      connection.setDoOutput( true );
      connection.setUseCaches( false );
      connection.setRequestProperty( "Content-Type", 
                                     "application/x-www-form-urlencoded" );

      // Use a writer to pass messages from the client to the servlet
      // engine. Output the encoded header followed by the URL encoded
      // message text.  Insure that the message text is not empty.
      PrintWriter writer =
	new PrintWriter( connection.getOutputStream(), true );
      if ( message == null )
      {
	message = "No information provided";
      }
      writer.println( "username=" + username + "&" + 
		      "message=" + URLEncoder.encode( message,"ISO-8859-1" ) );
      writer.close();

      // Report any feedback from the servlet engine.
      if ( connection.getContentLength() > 0 )
      {
        InputStreamReader inputStreamReader =
          new InputStreamReader( connection.getInputStream() );

        BufferedReader input = new BufferedReader( inputStreamReader );
        
        while ( input.ready() )
        {
          System.out.println( input.readLine() );
        }
        
        input.close();
      }
    }
    catch ( Exception exception )
    {
      errorMessage = exception.getMessage();
      exception.printStackTrace();
    }

    // Check for errors in handling the message while talking to the
    // serlet engine.
    if ( errorMessage != null )
    {
      // Got some. Notify the user.
      System.err.println( "Logging error: " + errorMessage );
      System.err.println( "File logging disabled." );
    }

    // Output the message to the console
    System.err.println( message );
  }
  
  /****************************************************************************/
  /**
   * Log a stack trace for an unprocessed error.
   */

  public static void printStackTrace( Throwable throwable )
  {
    String errorMessage = null;

    try
    {
      // Establish a connection to the servlet engine.  The default
      // can be overridden by a command line switch.
      String servletURL = System.getProperty( "servletEngine.url",
					      "http://www.axaf.harvard.edu" );
      URL url = new URL( servletURL + "/logger/LogServer" );
      URLConnection connection = url.openConnection();

      // Set up the servlet to handle both input and output.  Disable
      // caching.  Handle encoded form content.
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.setUseCaches(false);
      connection.setRequestProperty( "Content-Type", 
				     "application/x-www-form-urlencoded" );

      // Use a writer to pass messages from the client to the servlet
      // engine.  Extend it anonymously to handle encoding messages.xo
      PrintWriter writer =
	new PrintWriter( connection.getOutputStream(), true )
	  {
	    String lineSeparator =
	      (String) System.getProperty( "line.separator" );
	    
	    public void println(char s[]) 
	    {
	      String string = new String(s + lineSeparator);
              try {
	        print( URLEncoder.encode(string,"ISO-8859-1" ) );
              }
              catch (Exception exc) {
                exc.printStackTrace();
              }
	    }
	  };

      // Output the encoded header followed by the stack trace
      // terminated with a newline.
      writer.print( "username=" + LogClient.username + "&" + "message=" );
      throwable.printStackTrace( writer );
      writer.print( "\n" );
      writer.close();

      // Report any feedback from the servlet engine.
      if ( connection.getContentLength() > 0 )
      {
        InputStreamReader inputStreamReader =
          new InputStreamReader( connection.getInputStream() );

        BufferedReader input = new BufferedReader( inputStreamReader );
        
        while ( input.ready() )
        {
          System.out.println( input.readLine() );
        }
        
        input.close();
      }
    }
    catch ( Exception exception )
    {
      errorMessage = exception.getMessage();
      exception.printStackTrace();
    }

    if ( errorMessage != null )
    {
      System.err.println( "Logging error: " + errorMessage );
      System.err.println( "File logging disabled." );
    }

    // Output the stack trace to the console
    throwable.printStackTrace();
  }

  /****************************************************************************/

  public static void setUsername( String username )
  {
    LogClient.username = username;
  }

  /****************************************************************************/
  // Private variables

  /****************************************************************************/
  /**
   * The User's login name on the client.
   */

  private static String username;

  /****************************************************************************/

}

/******************************************************************************/
