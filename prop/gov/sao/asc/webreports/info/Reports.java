package info;
/*
  Copyrights:
 
  Copyright (c) 2000-2016 Smithsonian Astrophysical Observatory
 
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

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.regex.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Enumeration;
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
 * The Reports class provides access to the reports properties
 */

public class Reports extends HttpServlet {



  /****************************************************************************/
  /**
   * Handle the INIT operation.  This operation is invoked by the
   * servlet engine when it starts up.  Set the reports properties if
   * they have not already been set.  This includes the proposal data
   * path.
   *
   * @param config The configuration object established by the servlet
   * engine.
   */

  public void init( ServletConfig config ) throws ServletException {
      
      ServletContext context = config.getServletContext();


      // Determine if the reports properties attribute has been set.
      reportsProperties =  (Properties) context.getAttribute( "reports.properties" );
      if ( reportsProperties == null ) {
	  reportsProperties = Reports.getProperties( context );
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
	    reportsProperties.getProperty( "warning.message.text" );
	String result =
	    "<script type=\"text/javascript\"> alert( \"" + alertMessage + "\\n";
	result += reasons;
	result += "\\n\" ); </script>";
	return result;
    }

    /***************************************************************
     * read .htAccessDates file searching for key value
     * @param dateFilename   date file, default is from .htproperties
     * @param keyval   key name of search value
     * @return String  null or value founce for key provided
    */
    public static String accessDates(String dateFilename,String keyval)
    {
	BufferedReader dateFileBF = null;
	String inputLine = null;
	String[] inputArray;
	String key = null;
	String accessDateStr = null;
        String dateFile;

        if (dateFilename == null) {
          dateFile  = reportsProperties.getProperty("reports.access.date.file");
        } else {
          dateFile = dateFilename;
        }

	try {
            //LogMessage.println("Reading " + dateFile);
            FileReader fileR = new FileReader(dateFile);
	    dateFileBF = new BufferedReader(fileR);
	    while( (inputLine = dateFileBF.readLine()) != null) {
                if (inputLine.indexOf("^") >= 0) {
		  inputArray = inputLine.split("\\^");
                }
                else {
		  inputArray = inputLine.split(":");
                }
		key = inputArray[0];
		if(key.equals(keyval)) {
		    accessDateStr = inputArray[1];
		    break;
		}
	    }
           dateFileBF.close();
           fileR.close();
	} catch(Exception ex) {
	    ex.printStackTrace();
	    LogMessage.println("Caught exception in parsing dates file");
	}
        return accessDateStr;
     }

    private static boolean compareDates(String accessDateStr,boolean useBefore ) {
	boolean retval = false;

        try {
	  if(accessDateStr != null) {
	    Date currentDate = new Date();
            Date accessDate = null;
            if (accessDateStr.indexOf(":") > 0) {
	      accessDate = (DateFormat.getDateTimeInstance()).parse(accessDateStr);
            } else {
	      accessDate = (DateFormat.getDateInstance()).parse(accessDateStr);
            }
	    if(useBefore)  {
	      if(accessDate.before(currentDate)) {
	        retval = true;
	      }
	    }
            else  {
	      if(accessDate.after(currentDate)) {
	        retval = true;
	      }
	    }
	  }
	} catch(Exception ex) {
	    ex.printStackTrace();
	    LogMessage.println("Caught exception in parsing access Date: " + accessDateStr);
	}

	return retval;
    }


    /***
     * accessReports
     * This routine returns true if the PAS is open for reviewers 
     * Key = 'accessDate'
     * @param dateFile  filename of dates file . 
     * @return boolean  true if current date is after the access date
     */
    public static boolean accessReports(String dateFile) {
	boolean retval = false;
	String accessDateStr = null;

        try {
          accessDateStr = accessDates(dateFile,ReportsConstants.ACCESSDATE);
          retval = compareDates(accessDateStr,true);
	} catch(Exception ex) {
	    ex.printStackTrace();
	    LogMessage.println("Caught exception in parsing access date");
	}

	return retval;
    }

    /***
     * This routine returns true if the current date is after the 
     * access date listed in the file for pre access to panel but
     * before the end date listed in the file for that panel
     * Key = ##start,##end where ## is the panel name
     * @param dateFile  filename of dates file 
     * @param panel  panel name
     * @return boolean true if pre access allowed
     */
    public static boolean accessPreReports(String dateFile,String panel) {
	boolean retval = false;
	String startDateStr = null;
	String endDateStr = null;
        String pstart = panel + "start";
        String pend = panel + "end";

 
        try {
          startDateStr = accessDates(dateFile,pstart);
          endDateStr = accessDates(dateFile,pend);
          if (startDateStr != null && endDateStr != null) {
            retval = compareDates(startDateStr,true);
            if (retval )
              retval = compareDates(endDateStr,false);
          }
	} catch(Exception ex) {
            retval=false;
	    ex.printStackTrace();
	    LogMessage.println("Caught exception in parsing panel start/end date for " + panel );
	}

	return retval;
    }
	    

	    

    /***
     * This routine returns true if Administrators are allowed access.
     * This is before the site is open to reviewers.
     * Key = adminDate
     * @param dateFile  filename of dates file 
     * @return boolean  true if administrators are allowed access
     */
    public static boolean adminAccessReports(String dateFile) {
	boolean retval = false;
	String accessDateStr = null;

	try {
		
           accessDateStr = accessDates(dateFile,ReportsConstants.ADMINDATE);
           retval = compareDates(accessDateStr,true);
	} catch(Exception ex) {
	    ex.printStackTrace();
	    LogMessage.println("Caught exception in parsing admin date");
	}

	return retval;
    }

    /***
     * This routine returns true if Chairs are allowed access to the BPP reports.
     * Key = chairLPDate
     * @param dateFile  filename of dates file 
     * @return boolean  true if  Chairs are allowed access to the BPP reports
     */
    public static boolean chairLPAccessReports(String dateFile) {
	boolean retval = false;
	String accessDateStr = null;

	try {
		
           accessDateStr = accessDates(dateFile,ReportsConstants.CHAIRLPDATE);
           retval = compareDates(accessDateStr,true);
	} catch(Exception ex) {
	    ex.printStackTrace();
	    LogMessage.println("Caught exception in parsing chair LP date");
	}

	return retval;
    }


    /***
     * This routine is used if the site is open to Reviewers but a 
     * panel is not ready. If this entry exists, it will return true
     * if current date is after the date in the file
     * Key = ## (panel name)
     * @param dateFile  filename of dates file 
     * @param panelName  panel name
     * @return boolean returns true if panel is open for Reviewer access
     */
    public static boolean accessPanel(String dateFile, String panelName) {
	boolean retval = true;
	String accessDateStr = null;

	try {
            accessDateStr= accessDates(dateFile,panelName);
            if (accessDateStr != null) {
              retval = compareDates(accessDateStr,true);
            }
	} catch(Exception ex) {
	    ex.printStackTrace();
	    LogMessage.println("Caught exception Reports:accessPanel");
	}

	return retval;
    }



    /****
     * This routine returns true if it is before the peer review which
     * is the Preliminary reports view
     * Key = peerReviewDate
     * @return boolean true if before the Peer Review
     */
    public static boolean beforePeerReview() {
	boolean retval = false;
	String accessDateStr = null;

	try {
          accessDateStr = accessDates(null,ReportsConstants.PRDATE);
          // true if accessdate is after the current date
          retval = compareDates(accessDateStr,false);
          //LogMessage.println("beforePeerReview: " + accessDateStr + " -- " + retval);
	    
	} catch(Exception ex) {
	    ex.printStackTrace();
	    LogMessage.println("Caught exception Reports:beforePeerReview");
	}

	return retval;
    }

    /***
     * Preliminary Grades drop-dead date
     * Key = prelimGradesDate
     * @param dateFile  filename of dates file 
     * @return boolean returns true if the current date is before the access date listed in the file
     */
    public static boolean prelimGradesDeadline(String dateFile) {
	boolean retval = false;
	String accessDateStr = null;

        try {
          accessDateStr = accessDates(dateFile,ReportsConstants.PRELIMDEADLINE);
          // true if accessdate is before the current date
          retval = compareDates(accessDateStr,true);
	} catch(Exception ex) {
	    ex.printStackTrace();
	    LogMessage.println("Caught exception in parsing preliminary grades deadline");
	}
        
        return retval;
    }

    /***
     * Proposal Groups drop-dead date
     * Key = propGroupsDeadline
     * @param dateFile  filename of dates file 
     * @return boolean returns true if the current date is before the access date listed in the file
     */
    public static boolean proposalGroupsDeadline(String dateFile) {
	boolean retval = false;
	String accessDateStr = null;

        try {
          accessDateStr = accessDates(dateFile,ReportsConstants.GROUPSDEADLINE);
          // true if accessdate is before the current date
          retval = compareDates(accessDateStr,true);
	} catch(Exception ex) {
	    ex.printStackTrace();
	    LogMessage.println("Caught exception in parsing proposal groups deadline");
	}
        
        return retval;
    }

    /***
     * Reviewer access end date
     * Key = endReviewerAccess
     * @param dateFile  filename of dates file 
     * @return boolean true if the current date is before the access date listed in the file
     */
    public static boolean endReviewerAccess(String dateFile) {
	boolean retval = false;
	String accessDateStr = null;

        try {
          accessDateStr = accessDates(dateFile,ReportsConstants.ENDREVIEWER);
          // true if accessdate is before the current date
          retval = compareDates(accessDateStr,false);
	} catch(Exception ex) {
	    ex.printStackTrace();
	    LogMessage.println("Caught exception in parsing end reviewer access date");
	}
        
        return retval;
    }

    /***
     * Chair access end date
     * Key = endChairAccess
     * @param dateFile  filename of dates file 
     * @return boolean This routine returns true if the current date is before the access date listed in the file
     */
    public static boolean endChairAccess(String dateFile) {
	boolean retval = false;
	String accessDateStr = null;

        try {
          accessDateStr = accessDates(dateFile,ReportsConstants.ENDCHAIR);
          // true if accessdate is before the current date
          retval = compareDates(accessDateStr,false);
	} catch(Exception ex) {
	    ex.printStackTrace();
	    LogMessage.println("Caught exception in parsing end chair access date");
	}
        
        return retval;
    }

    /***
     * Pundit access end date
     * Key = endPunditAccess
     * @param dateFile  filename of dates file 
     * @return boolean true if the current date is before the access date listed in the file
     */
    public static boolean endPunditAccess(String dateFile) {
	boolean retval = false;
	String accessDateStr = null;

        try {
          accessDateStr = accessDates(dateFile,ReportsConstants.ENDPUNDIT);
          // true if accessdate is before the current date
          retval = compareDates(accessDateStr,false);
	} catch(Exception ex) {
	    ex.printStackTrace();
	    LogMessage.println("Caught exception in parsing end pundit access date");
	}
        
        return retval;
    }

    /***
     * Facilitator access end date
     * Key = endFacilitatorAccess
     * @param dateFile  filename of dates file 
     * @return boolean true if the current date is before the access date listed in the file
     */
    public static boolean endFacilitatorAccess(String dateFile) {
	boolean retval = false;
	String accessDateStr = null;

        try {
          accessDateStr = accessDates(dateFile,ReportsConstants.ENDFACILITATOR);
          // true if accessdate is before the current date
          retval = compareDates(accessDateStr,false);
	} catch(Exception ex) {
	    ex.printStackTrace();
	    LogMessage.println("Caught exception in parsing end facilitator access date");
	}
        
        return retval;
    }

    public  static String getReleaseDown( )  {

      String relMsg="";
      String releaseDown = reportsProperties.getProperty("release.down.file");
      if (releaseDown == null) releaseDown = "";
      File releaseDownFile = new File(releaseDown);
      if (releaseDownFile.exists() && releaseDown.length() > 3 ) {
        try {
          FileReader fileR = new FileReader(releaseDownFile);
          BufferedReader releaseFileBR = new BufferedReader(fileR);
          String inputLine;
          while( (inputLine = releaseFileBR.readLine()) != null) {
            relMsg += inputLine;
          }
          releaseFileBR.close();
          fileR.close();
        }
        catch (Exception exc) {
          relMsg = "Down for maintenance.  Please try again later.";
          LogMessage.printException(exc);
        }
      }
  
      return relMsg;
    }


    public static String getCDOMessage( )  {

      String cdoText = "";
      String cdoMessage = reportsProperties.getProperty("cdo.message.file") ;
  
      if (cdoMessage == null) cdoMessage = "";
      File cdoMessageFile = new File(cdoMessage);
      if (cdoMessage.length() > 3 && cdoMessageFile.exists() ) {
        try {
          FileReader fileR = new FileReader(cdoMessageFile);
          BufferedReader cdoFileBR = new BufferedReader(fileR);
          String inputLine;
          while( (inputLine = cdoFileBR.readLine()) != null) {
            cdoText += inputLine;
          }
          cdoFileBR.close();
          fileR.close();
        }
        catch (Exception exc) {
          LogMessage.printException(exc);
        }
      }
      return cdoText;
    }



  /****************************************************************************/
  /**
   * Return the reports properties.  These are stored as a resource in
   * the reports jar file.
   *
   * @return The set of Reports configuration properties.
   */

    public static Properties getProperties() {
	reloadExtraProperties();
	return Reports.reportsProperties;
    }

  /****************************************************************************/
  /**
   * Return the reports properties.  These are stored as a resource in
   * the reports jar file.
   *
   * @param context The servlet context object.
   *
   * @return The set of Reports configuration properties.
   */

  public static Properties getProperties( ServletContext context)  {

    if ( Reports.reportsProperties == null )
    {

      Properties reportsProperties = null;

      try
      {
	// Load the reports properties file.
	reportsProperties = new Properties();
	InputStream propStream =
	    context.getResourceAsStream( "/reports.properties" );
	if ( propStream != null )
	{
	  // Load the properties file and save the properties as a servlet attribute.
	  reportsProperties.load( propStream );
	  context.setAttribute( "reports.properties", reportsProperties );

	  // Close the file connection.
	  propStream.close();
	}
	else
	{
	  LogMessage.err( "Cannot access property file." );
	}
      }
      catch ( IOException iox )
      {
	context.log( "Unexpected I/O exception occurred setting up properties.",
		     iox );
      }

      reportsContext = context;

      // Set the static copy.
      Reports.reportsProperties = reportsProperties;
      reloadExtraProperties();
    }

    // Return the global value.
    return Reports.reportsProperties;
  }


  /****************************************************************************/
  /**
   * reloadProperties
   * Return the reports properties.  These are stored as a resource in
   * the reports jar file.
   *
   * @return The set of Reports configuration properties.
   */

    //public static Properties reloadProperties( ServletContext context)  {
    public static Properties reloadProperties2()  {
	Properties newProp = new Properties();
	reportsContext.removeAttribute("reports.properties");
	Reports.reportsProperties = null;

	try {
	    // Load the reports properties file.
	    InputStream propStream = reportsContext.getResourceAsStream( "/reports.properties" );

	    if ( propStream != null ) {
		// Load the properties file and save the properties as a servlet attribute.
		newProp.load( propStream );
		reportsContext.setAttribute( "reports.properties", newProp );

		// Close the file connection.
		propStream.close();
		
	    } else {
		LogMessage.err( "Cannot access property file." );
	    }
	} catch ( IOException iox ) {
	    reportsContext.log( "Unexpected I/O exception occurred setting up properties.",
				iox );
	}

	// Set the static copy.
	Reports.reportsProperties = newProp;
	
	// Return the global value.
	return Reports.reportsProperties;
  }

    /*
     * reloads the .htExtraConfig file to get the values for fields which may
     * change, like timeout
     */
    private static void reloadExtraProperties()  {
	Properties newProp = new Properties();
	
	try {
          if(extraConfigFile == null) {
            String htpath = System.getenv("OBSCYCLE_DATA_PATH");
            if (htpath.length() < 3) {
              htpath=null;
            }
            htpath += "/prop/webreports";
	    extraConfigFile = htpath + "/.htproperties";
            LogMessage.warn("using " + extraConfigFile);
          }
	  InputStream propStream = new FileInputStream(extraConfigFile);
	    
	  if(propStream != null) {
		newProp.load(propStream);

		//Add the new properties to the original reportsProperties
		Enumeration newProperties = newProp.propertyNames();
		String currentPropertyName = null;
		String currentPropertyValue = null;
		String oldPropertyValue = null;
		while(newProperties.hasMoreElements()) {
		    currentPropertyName = (String)newProperties.nextElement();
		    currentPropertyValue = newProp.getProperty(currentPropertyName);

		    //Display the change if this property already existed, or if it's 
		    //a new property.  Otherwise, if the value is remaining the same,
		    //don't print anything to the log file
		    oldPropertyValue = null;
		    oldPropertyValue = reportsProperties.getProperty(currentPropertyName);
		    if(oldPropertyValue == null) {
			//LogMessage.warn("Adding: " + currentPropertyName + " : " + currentPropertyValue);
		    } else if(!currentPropertyValue.equals(oldPropertyValue)) {
			LogMessage.warn("Changing: " + currentPropertyName 
					   + " from " + oldPropertyValue 
					   + " to " + currentPropertyValue);
		    }

		    reportsProperties.setProperty(currentPropertyName, currentPropertyValue); 

		}
	    }
	    propStream.close();
	} catch(Exception ex) {
	    LogMessage.err("Error: Caught exception in Reports::reloadExtraProperties: " + 
			       ex.getMessage());
	}

    }



  /****************************************************************************/
  /**
   * Private variables
   */

    private static Properties reportsProperties = null;
    private static ServletContext reportsContext = null;
    private static String extraConfigFile; 
}

/******************************************************************************/
