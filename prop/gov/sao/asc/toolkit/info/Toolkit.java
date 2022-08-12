package info;
/*
  Copyrights:
 
  Copyright (c) 2000,2019 Smithsonian Astrophysical Observatory
 
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

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.net.InetAddress;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.UnavailableException;

/******************************************************************************/
/**
 * The Toolkit class provides access to the toolkit properties and
 * also provides operations used by all the tools.
 */

public class Toolkit extends HttpServlet implements ToolkitConstants
{

  /****************************************************************************/
  /**
   * Handle the INIT operation.  This operation is invoked by the
   * servlet engine when it starts up.  Set the tookit properties if
   * they have not already been set.  This includes the proposal data
   * path.
   *
   * @param config The configuration object established by the servlet
   * engine.
   */

  public void init( ServletConfig config )
    throws ServletException
  {
    ServletContext context = config.getServletContext();

    // Determine if the toolkit properties attribute has been set.
    toolkitProperties =
      (Properties) context.getAttribute( "toolkit.properties" );
    if ( toolkitProperties == null )
    {
      // The attribute has not been set yet.  Do it now.
      toolkitProperties = Toolkit.getProperties( context );
    }
  }

  public static void addProps( Properties myprop) {

    try {

      // add in others by environment
      String htpath = System.getenv("OBSCYCLE_DATA_PATH");
      if (htpath.length() < 3) {
         htpath=null;
      }
      htpath += "/prop/toolkit";

      myprop.setProperty( "toolkit.data.path", htpath );
      htpath += "/.htpath";
      try {
        BufferedReader in = new BufferedReader(new FileReader(htpath));
        String nextLine;
        while ((nextLine = in.readLine()) != null) {
          if (nextLine.indexOf('#') == 0 ) {
            // ignore comment line
          }
          else {
            String[] strArr = nextLine.split("=");
            if (strArr.length == 2) {
              String p1 = strArr[0].trim();
              String p2 = strArr[1].trim();
              String oldProp = myprop.getProperty(p1);
              if (oldProp == null || !oldProp.equals(p2)) {
                myprop.setProperty( p1,p2 );
                // If switch to log4j make this a trace call maybe
                //System.err.println("Toolkit adding: " + p1 + "=" + p2  + "  old=" + oldProp);
              }
            }
          }
        }
        in.close();
      }
      catch (Exception exc) {
       exc.printStackTrace();
      }
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }


  /****************************************************************************/
  /**
   * Build and return a JavaScript program which will popup an alert
   * box detailing the reasons that the input is in error.
   *
   * @param reasons A list of field names and associated reason for
   * the error.
   *
   * @return The JavaScript program which produces a popup detailing
   * any input errors and the associated cause.
   */

  public static String buildWarningsScript( String reasons )
  {
    String alertMessage =
      toolkitProperties.getProperty( "warning.message.text" );
    String result =
      "<script type=\"text/javascript\"> alert( \"" + alertMessage + "\\n";
    result += reasons;
    result += "\\n\" ); </script>";
    return result;
  }
  public static String stripInput( String value )
  {

    String retValue;
    if (value != null) {
      int slen = value.length();
      if (slen > 200) { slen=200; }

      retValue = value.substring(0,slen);
      retValue = retValue.replaceAll("[^\\p{ASCII}]","");
      retValue = retValue.replaceAll("\""," ");
      retValue = retValue.replaceAll("'"," ");
      retValue = retValue.replaceAll("`"," ");
      retValue = retValue.replaceAll("\\\\"," ");
      if (value != retValue) {
        logger.trace("ToolkitValidator: before: " + value);
        logger.trace("ToolkitValidator: after : " + retValue);
      }
    } else {
       retValue =value;
    }

    return retValue;
  }

  
  /**
   * Return the toolkit properties.  These are stored as a resource in
   * the toolkit jar file.
   *
   * @return The set of Toolkit configuration properties.
   */

  public static Properties getProperties()
  {
    return Toolkit.toolkitProperties;
  }

  /****************************************************************************/
  /**
   * Return the toolkit properties.  These are stored as a resource in
   * the toolkit jar file.
   *
   * @param context The servlet context object.
   *
   * @return The set of Toolkit configuration properties.
   */

  public static Properties getProperties( ServletContext context)
  {
    if ( Toolkit.toolkitProperties == null )
    {

      Properties toolkitProperties = null;

      try
      {
	// Load the input error messages, etc. from the toolkit
	// properties file.
	toolkitProperties = new Properties();
	InputStream propStream =
	    context.getResourceAsStream( "/toolkit.properties" );
	if ( propStream != null )
	{
	  // Load the properties file, add the proposal tools data path
	  // and save the properties as a servlet attribute.
	  toolkitProperties.load( propStream );

	  // Close the file connection.
	  propStream.close();
	}
	else
	{
	  System.err.println( "Cannot access property file." );
	}
      }
      catch ( IOException iox )
      {
	context.log( "Unexpected I/O exception occurred setting up properties.",
		     iox );
      }

      // Set the static copy.
      try {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        addProps(toolkitProperties);
        Toolkit.toolkitProperties = toolkitProperties;
        initializeLogger();
        context.setAttribute( "toolkit.properties", Toolkit.toolkitProperties );
        logger.info(dateFormat.format(date) + ":Initial toolkit.data.path=" + Toolkit.toolkitProperties.getProperty("toolkit.data.path"));
        logger.info(dateFormat.format(date) + ":Initial use.captcha=" + Toolkit.toolkitProperties.getProperty("use.captcha"));
        
      }
      catch ( Exception exc )
      {
       exc.printStackTrace();
      }
    }

    // Return the global value.
    return Toolkit.toolkitProperties;
  }


  public static String[] getInstrument(String mission) {

     String[] instrumentChoices = null;
     if ( mission.equals( "ASCA" ) )
     {
       instrumentChoices = new String[] {
         "SIS/None/None",
         "GIS/None/None"
       };
     }
     else if ( mission.equals( "ASTROH" ) || mission.equals("HITOMI") )
     {
       instrumentChoices = new String[] {
         "SXS/None/OPEN",
         "SXS/None/OBF",
         "SXS/None/BE25",
         "SXI/None/None",
         "HXI/None/ALL",
         "HXI/None/TOP",
         "SGD/None/None"
       };
     }
     else if ( mission.equals( "EINSTEIN" ) )
     {
       instrumentChoices = new String[] {
         "IPC/None/None",
         "HRI/None/None"
       };
     }
     else if ( mission.equals( "EXOSAT" ) )
     {
       instrumentChoices = new String[] {
         "ME/None/None"
       };
     }
     else if ( mission.equals( "GINGA" ) )
     {
       instrumentChoices = new String[] {
         "LAC/None/TOP",
         "LAC/None/BOTH"
       };
     }
     else if ( mission.equals( "NUSTAR" ) )
     {
       instrumentChoices = new String[] {
         "None/None/None",
       };
     }
     else if ( mission.equals( "ROSAT" ) )
     {
       instrumentChoices = new String[] {
         "HRI/None/None",
         "PSPC/None/OPEN"
       };
     }
     else if ( mission.equals( "SAX" ) )
     {
       instrumentChoices = new String[] {
         "HPGSPC/None/None",
         "LECS/None/None",
         "MECS/None/None",
         "PDS/None/None",
         "WFC/None/None"
       };
     }
     else if ( mission.equals( "SWIFT" ) )
     {
       instrumentChoices = new String[] {
         "BAT/None/Single",
         "XRT/None/PC",
      "XRT/None/WT",
      "XRT/None/PD",
       };
     }
     else if ( mission.equals( "SUZAKU" ) )
     {
       instrumentChoices = new String[] {
         "HXD/None/GSO",
         "HXD/None/PIN",
         "XIS/None/BI",
         "XIS/None/FI",
       };
     }
     else if ( mission.equals( "XMM" ) )
     {
       instrumentChoices = new String[] {
         "MOS/None/THIN",
         "MOS/None/MEDIUM",
         "MOS/None/THICK",
         "PN/None/THIN",
         "PN/None/MEDIUM",
         "PN/None/THICK",
         "RGS1/None/O1",
         "RGS1/None/O2",
         "RGS2/None/O1",
         "RGS2/None/O2",
       };
     }
     else if ( mission.equals( "XTE" ) )
     {
       instrumentChoices = new String[] {
         "ASM/None/None",
         "PCA/None/None",
         "HEXTE/None/DEFAULT",
         "HEXTE/None/LLD10",
         "HEXTE/None/LLD15",
         "HEXTE/None/LLD20",
         "HEXTE/None/LLD25",
         "HEXTE/None/LLD30",
       };
     }
     else 
     {
       instrumentChoices = new String[] {
         "ACIS-I/None/None",
         "ACIS-I/HETG/ORDER0",
         "ACIS-I/LETG/ORDER0",
         "ACIS-S/None/None",
         "ACIS-S/HETG/ORDER0",
         "ACIS-S/HETG/HEG1",
         "ACIS-S/HETG/MEG1",
         "ACIS-S/HETG/HEG1MEG1",
         "ACIS-S/LETG/ORDER0",
         "ACIS-S/LETG/LETG1",
         "HRC-I/None/None",
         "HRC-I/LETG/ORDER0",
         "HRC-S/None/None",
         "HRC-S/LETG/ORDER0",
         "HRC-S/LETG/LETG1",
         "HRC-S/LETG/LETGHI"
       };
     }

     return instrumentChoices;
  } 
   
  private static void initializeLogger()
  {
    String serverAddr = null;
    Properties logProperties = new Properties();
    String log4jFile = toolkitProperties.getProperty("toolkit.log4j");

    try {
      serverAddr = InetAddress.getLocalHost().getHostName();
      if (serverAddr == null) serverAddr="unknown";
    } catch (Exception exc) {
      serverAddr = "unknown";
    }
    FileInputStream is = null;

    try
    {
      // load our log4j properties / configuration file
      is = new FileInputStream(log4jFile);
      logProperties.load(is);
      logProperties.put("LOGADDR",serverAddr);
      PropertyConfigurator.configure(logProperties);
      logger.info("Logging initialized.");
      is.close();
    }
    catch(IOException e)
    {
      try {
        if (is != null) is.close();
      } catch (Exception ex) {
         logger.error(ex);
      }
      throw new RuntimeException("Unable to load logging property " + log4jFile);
    }
  }



/******************************************************************************/

  /****************************************************************************/
  /**
   * Private variables
   */

  private static Properties toolkitProperties = null;
  private static Logger logger = Logger.getLogger(Toolkit.class);

}

/******************************************************************************/
