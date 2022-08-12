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

import java.util.regex.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




/******************************************************************************/
/**
 * Provide services for mapping a server name to an IP address and a
 * port number.
 *
 * This class must be moved into a servlet in order to preserve
 * the notion of ObsEd running as an applet (or via a Java plug-in).
 */


public class DBServerMapper extends HttpServlet
{

  /****************************************************************************/

  public void doGet( HttpServletRequest request, HttpServletResponse response ) 
    throws ServletException, IOException
  {
    String errorCode = "OK";
    String server = request.getParameter( "server" );
    String interfacesFilename = "";

    try
    {
      // Open and read the /soft/sybase/interfaces file.  Use lineRead
      //Load in the environment variables

      String interfacesDirectory = System.getenv("SYBASE");
      if (interfacesDirectory == null)
        throw new IOException("Environment variable SYBASE not found.");

      interfacesFilename = interfacesDirectory + "/interfaces";


    }
    catch ( Exception iox )
    {
      // Handle an IO exception, i.e. the servlet could not read the
      // interfaces file.
      iox.printStackTrace();
      errorCode = "IOX";
    }

    // Now output the data to the client.
    PrintWriter out = response.getWriter();
    out.println( errorCode + "," + interfacesFilename + ",");
  }

  /****************************************************************************/

  public void doPost( HttpServletRequest request, HttpServletResponse response ) 
    throws ServletException, IOException
  {
    doGet( request, response );
  }

}
