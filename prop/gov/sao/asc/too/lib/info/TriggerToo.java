package info;
/*
  Copyrights:
 
  Copyright (c) 2000 Smithsonian Astrophysical Observatory
 
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

import java.io.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.UnavailableException;

import ascds.LogMessage;

/******************************************************************************/
/**
 * The TriggerToo class provides access to the triggertoo properties
 */

public class TriggerToo extends HttpServlet {


  /****************************************************************************/
  /**
   * Handle the INIT operation.  This operation is invoked by the
   * servlet engine when it starts up.  Set the triggertoo properties if
   * they have not already been set.  This includes the proposal data
   * path.
   *
   * @param config The configuration object established by the servlet
   * engine.
   */

  public void init( ServletConfig config ) throws ServletException {
      

      ServletContext context = config.getServletContext();

      
      // Determine if the triggertoo properties attribute has been set.
      triggerTooProperties =  (Properties) context.getAttribute( "triggertoo.properties" );
      if ( triggerTooProperties == null ) {
	  triggerTooProperties = TriggerToo.getProperties( context );
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

    public static String buildWarningsScript( String reasons ) {
	String alertMessage =
	    triggerTooProperties.getProperty( "warning.message.text" );
	String result =
	    "<script type=\"text/javascript\"> alert( \"" + alertMessage + "\\n";
	result += reasons;
	result += "\\n\" ); </script>";
	return result;
    }




  /****************************************************************************/
  /**
   * Return the Trigger TOO properties.  These are stored as a resource in
   * the triggertoo jar file.
   *
   * @return The set of configuration properties.
   */

  public static Properties getProperties()
  {
    //System.err.println("***IN TriggerToo:getProperties");
    reloadExtraProperties();
    return TriggerToo.triggerTooProperties;
  }

  /****************************************************************************/
  /**
   * Return the  properties.  These are stored as a resource in
   * the triggertoo jar file.
   *
   * @param context The servlet context object.
   *
   * @return The set of configuration properties.
   */

  public static Properties getProperties( ServletContext context)  {

    if ( TriggerToo.triggerTooProperties == null )
    {

      Properties triggerTooProperties = null;

      try
      {
	// Load the triggertoo properties file.
	triggerTooProperties = new Properties();
	InputStream propStream =
	    context.getResourceAsStream( "/triggertoo.properties" );
	if ( propStream != null )
	{
	  // Load the properties file and save the properties as a servlet attribute.

	  triggerTooProperties.load( propStream );
	  context.setAttribute( "triggertoo.properties", triggerTooProperties );

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
      TriggerToo.triggerTooProperties = triggerTooProperties;
      reloadExtraProperties();
    }

    // Return the global value.
    return TriggerToo.triggerTooProperties;
  }


  /****************************************************************************/
  /**
   * reloadProperties
   * Return the triggertoo properties.  These are stored as a resource in
   * the triggertoo jar file.
   *
   * @param context The servlet context object.
   *
   * @return The set of TriggerToo configuration properties.
   */

    public static Properties reloadPropertiesNOTUSED( ServletContext context)  {
	Properties newProp = null;
	newProp = new Properties();
	context.removeAttribute("triggertoo.properties");
	TriggerToo.triggerTooProperties = null;

	try {
	    // Load the triggertoo properties file.
	    InputStream propStream = context.getResourceAsStream( "/triggertoo.properties" );

	    if ( propStream != null ) {
		// Load the properties file and save the properties as a servlet attribute.
		newProp.load( propStream );
		context.setAttribute( "triggertoo.properties", newProp );

		// Close the file connection.
		propStream.close();
		
	    } else {
		System.err.println( "Cannot access property file." );
	    }
	} catch ( IOException iox ) {
	    context.log( "Unexpected I/O exception occurred setting up properties.",
			 iox );
	}

	// Set the static copy.
	TriggerToo.triggerTooProperties = newProp;
	
	// Return the global value.
	return TriggerToo.triggerTooProperties;
  }

    /*
     * reloads the .htProperties file to get the values for fields which may
     * change
     */
    private static void reloadExtraProperties()  {
        Properties newProp = new Properties();

        try {
            String htpath = System.getenv("OBSCYCLE_DATA_PATH");
            if (htpath.length() < 3) {
             htpath=null;
            }
            htpath += "/triggertoo";

            //System.err.println("Setting triggertoo.data.path=" + htpath);
            triggerTooProperties.setProperty("triggertoo.data.path", htpath);
            htpath += "/.htproperties";
            InputStream propStream = new FileInputStream(htpath);

            if(propStream != null) {
                newProp.load(propStream);

                //Add the new properties to the original triggerTooProperties
                Enumeration newProperties = newProp.propertyNames();
                String currentPropertyName = null;
                String currentPropertyValue = null;
                String oldPropertyValue = null;
                while(newProperties.hasMoreElements()) {
                  currentPropertyName = (String)newProperties.nextElement();
                  currentPropertyValue = newProp.getProperty(currentPropertyName);
                  //Display the change if property already existed, or if it's
                  //a new property.  Otherwise, if the value is remaining the
                  //same don't print anything to the log file
                  oldPropertyValue = null;
                  oldPropertyValue = triggerTooProperties.getProperty(currentPropertyName);

                  if (oldPropertyValue == null) {
                    System.err.println("Adding: " + currentPropertyName + ": " + currentPropertyValue);
                  } else if(!currentPropertyValue.equals(oldPropertyValue)) {
                    System.err.println("Changing: " + currentPropertyName
                                        + " from " + oldPropertyValue
                                        + " to " + currentPropertyValue);
                  }

                  triggerTooProperties.setProperty(currentPropertyName, currentPropertyValue);
                }
            }
            propStream.close();
        } catch(Exception ex) {
            System.err.println("Error: Caught exception in TriggerTOO::reloadExtraProperties: " +
                               ex.getMessage());
        }

    }



    
   

  /****************************************************************************/
  /**
   * Private variables
   */

  private static Properties triggerTooProperties = null;
}

/******************************************************************************/
