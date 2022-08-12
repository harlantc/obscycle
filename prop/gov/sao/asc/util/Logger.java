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
import java.net.UnknownServiceException;
import java.util.Date;

/******************************************************************************/

public class Logger extends Object
{
  private static String filename;
  private static PrintWriter writer;
  private static int count = 0;

  /****************************************************************************/

  public static void logMessage(String message)
  {
    // Make sure that we have a writer object.  Just try this once and
    // handle failures gracefully.
    if ( writer == null && count == 0 )
    {
      setupWriter();
    }

    // Actually output the message.
    if ( writer != null )
    {
      writer.println( URLEncoder.encode( message ) );
    }
    else
    {
      // Do this just once.
      if ( count == 0 )
      {
	System.out.println( "Logging disbabled." );
	count++;
      }
    }

    // Dump a copy of the message to the console, if one exists.
    System.out.println( message );
  }
  
  /****************************************************************************/

  public static void printStackTrace( Throwable throwable )
  {
    if ( writer == null && count == 0 )
    {
      // Setup the writer object.
      setupWriter();
    }

    if ( writer != null )
    {
      throwable.printStackTrace( writer );
    }
    throwable.printStackTrace();
  }

  /****************************************************************************/

  public static void start( String alias, String message )
  {
    Date d = new Date();
    filename = alias;
    String message1 = d.toString() + ": Log started for " + alias + "\012" +
      message;
    Logger.logMessage( message1 );
  }

  /****************************************************************************/

  private static void setupWriter()
  {
    // Set up the writer object.
    try
    {
      String serverURL = System.getProperty( "server.url", 
					     "http://www.axaf.harvard.edu" );
      
      URL url = new URL( serverURL + "/obsed/logs" );
	
      URLConnection connection = url.openConnection();

      connection.setDoInput( true );
      connection.setDoOutput( true );
      connection.setUseCaches( false );

      connection.setRequestProperty( "Content-Type", 
				     "application/x-www-form-urlencoded" );
      
      writer = new PrintWriter( connection.getOutputStream(), true );
    }
    // Deal with I/O errors.
    catch ( UnknownServiceException use )
    {
      System.out.println( "Logging service is not available.  " +
			  "Messages will be output to the console only." );
    }
    catch ( IOException exception )
    {
      exception.printStackTrace();
    }
  }

  /****************************************************************************/

}

/******************************************************************************/
