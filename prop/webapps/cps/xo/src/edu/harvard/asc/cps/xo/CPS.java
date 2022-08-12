package edu.harvard.asc.cps.xo;
/************************************************************************/
/*     Copyrights:                                                      */
/*                                                                      */
/*     Copyright (c) 2017,2019,2021 Smithsonian Astrophysical Observatory*/
/*                                                                      */
/*    Permission to use, copy, modify, distribute,  and  sell  this     */
/*    software  and  its  documentation  for  any purpose is hereby     */
/*    granted  without  fee,  provided  that  the  above  copyright     */
/*    notice  appear  in  all  copies  and that both that copyright     */
/*    notice and this permission notice appear in supporting  docu-     */
/*    mentation,  and  that  the  name  of  the  Smithsonian Astro-     */
/*    physical Observatory not be used in advertising or  publicity     */
/*    pertaining  to distribution of the software without specific,     */
/*    written  prior  permission.   The  Smithsonian  Astrophysical     */
/*    Observatory  makes  no  representations about the suitability     */
/*    of this software for any purpose.  It  is  provided  "as  is"     */
/*    without express or implied warranty.                              */
/*    THE  SMITHSONIAN  ASTROPHYSICAL  OBSERVATORY  DISCLAIMS   ALL     */
/*    WARRANTIES  WITH  REGARD  TO  THIS  SOFTWARE,  INCLUDING  ALL     */
/*    IMPLIED  WARRANTIES  OF  MERCHANTABILITY  AND FITNESS, IN  NO     */
/*    EVENT  SHALL  THE  SMITHSONIAN  ASTROPHYSICAL  OBSERVATORY BE     */
/*    LIABLE FOR ANY SPECIAL,  INDIRECT  OR  CONSEQUENTIAL  DAMAGES     */
/*    OR  ANY  DAMAGES  WHATSOEVER RESULTING FROM LOSS OF USE, DATA     */
/*    OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,  NEGLIGENCE  OR     */
/*    OTHER  TORTIOUS  ACTION, ARISING OUT OF OR IN CONNECTION WITH     */
/*    THE USE OR PERFORMANCE OF THIS SOFTWARE.                          */
/*                                                                      */
/************************************************************************/

import edu.harvard.cda.proposal.xo.YesNo;
import edu.harvard.cda.proposal.xo.YesNoPreferred;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.commons.lang3.StringEscapeUtils;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;
import java.util.Date;
import java.util.Enumeration;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.URLEncoder;
import java.net.InetAddress;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;


/******************************************************************************/
/**
 * The CPS class provides access to the cps properties
 */

public class CPS extends HttpServlet {


  /****************************************************************************/
  /**
   * Handle the INIT operation.  This operation is invoked by the
   * servlet engine when it starts up.  Set the cps properties if
   * they have not already been set.  This includes the proposal data
   * path.
   *
   * @param config The configuration object established by the servlet
   * engine.
   */

  public void init( ServletConfig config ) throws ServletException {
      
      ServletContext context = config.getServletContext();

      // Determine if the cps properties attribute has been set.
      CPS.cpsProperties =  (Properties) context.getAttribute( "cps.properties" );
      if ( CPS.cpsProperties == null ) {
	  CPS.cpsProperties = CPS.getProperties( context );
      }
  }

  private static void initializeLogger()
  {
    String serverAddr = null;
    Properties logProperties = new Properties();
    String log4jFile = cpsProperties.getProperty("cps.log4j");
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


    private static boolean compareDates(String inDateStr,boolean useBefore ) {
	boolean retval = false;

        try {
	  if(inDateStr != null) {
	    Date currentDate = new Date();
            Date inDate = null;
      
      if (inDateStr.indexOf(':') > 0) {
	      inDate = (DateFormat.getDateTimeInstance()).parse(inDateStr);
            } else {
	      inDate = (DateFormat.getDateInstance()).parse(inDateStr);
            }
	    if(useBefore)  {
	      if(inDate.before(currentDate)) {
                logger.trace(inDateStr + " is before current");
	        retval = true;
	      }
	    }
            else  {
	      if(inDate.after(currentDate)) {
                logger.trace(inDateStr + " is after current");
	        retval = true;
	      }
	    }
	  }
	} catch(Exception ex) {
	    //ex.printStackTrace();
	    logger.error("Caught exception in parsing access Date: " + inDateStr);
	}

	return retval;
    }

    public static Long getCfPEndDate() {
      Long theDate = Long.valueOf(0);
      try {
        String dateStr = cpsProperties.getProperty("cfp.end.date");
        Date df = (DateFormat.getDateTimeInstance()).parse(dateStr);
        theDate = Long.valueOf(df.getTime());
      } catch (Exception exc) {
        logger.error(exc.getMessage());
        logger.debug("Deadline",exc);
      }
      return theDate;
    }

    public static Long getCfPDate() {
      Long theDate = Long.valueOf(0);
      try {
        String dateStr = cpsProperties.getProperty("cfp.official.date");
        Date df = (DateFormat.getDateTimeInstance()).parse(dateStr);
        theDate = Long.valueOf(df.getTime());
      } catch (Exception exc) {
        logger.error(exc.getMessage());
        logger.debug("Deadline",exc);
      }
      return theDate;
    }

    public static String getCfPOfficialDate() {
     
      String dateStr = "";
      try {
        dateStr= cpsProperties.getProperty("cfp.official.date.str");
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
      if (dateStr==null) dateStr="";
      return dateStr;
    }

    public static String getStartURL() {
      String startURL = null;
      try {
        startURL = (String)cpsProperties.getProperty("cps.start.url");
      } catch (Exception e) {
        logger.error(e);
      }
      if (startURL==null) startURL="http://cxc.harvard.edu/proposer";
      return startURL;
    }

    public static String getHelpDesk() {
      String helpDesk = "";
      try {
         helpDesk = (String)cpsProperties.getProperty("cxchelp.url");
      } catch (Exception e) {
        logger.error(e);
      }
      if (helpDesk==null) helpDesk="http://cxc.harvard.edu/helpdesk";
      return helpDesk;
    }
    public static String getAOStart() {
      String ao_start = null;
      try {
        ao_start= (String)cpsProperties.getProperty("cps.ao.start");
      } catch (Exception e) {
        logger.error(e);
      }
      if (ao_start==null) ao_start="01-Jan-2020 00:00";
      return ao_start;
    }

    
    public static String getEditProfile(Boolean ddtRequest) {
      String editProfile = null;
      try {
        editProfile = cpsProperties.getProperty("cda.profile.url");
      } catch (Exception e) {
        logger.error(e);
      }
      if (editProfile == null) editProfile="";
      if (editProfile.length() > 1) {
        editProfile = editProfile.trim();
        if (ddtRequest) editProfile =  editProfile.replace("login","ddtlogin");
      }

      return editProfile;
    }

    /***
     * isCfP
     * @return boolean returns true if the current date is during the CfP dates 
     */
    public static boolean isCfP() {
	boolean retval = false;

        try {
          String startDateStr = cpsProperties.getProperty("cfp.start.date");
          String endDateStr = cpsProperties.getProperty("cfp.end.date");
          retval = compareDates(startDateStr,true);
          if (retval)
            retval = compareDates(endDateStr,false);
	} catch(Exception ex) {
	    ex.printStackTrace();
	    logger.error("Caught exception in parsing access date");
	}

	return retval;
    }
    /***
     * isGTO  submission period
     *
     * @return boolean true if the current date is during the GTO dates 
     */
    public static boolean isGTO() {
	boolean retval = false;
        try {
          String startDateStr = cpsProperties.getProperty("gto.start.date");
          String endDateStr = cpsProperties.getProperty("gto.end.date");
          retval = compareDates(startDateStr,true);
          if (retval)
            retval = compareDates(endDateStr,false);
	} catch(Exception ex) {
	    ex.printStackTrace();
	    logger.error("Caught exception in parsing access date");
	}

	return retval;
    }

    public static double deadlineCfP(boolean flg) {
	double retval = 0;
        String endDateStr;

        try {
            if (flg)
              endDateStr = cpsProperties.getProperty("cfp.official.date");
            else 
              endDateStr = cpsProperties.getProperty("cfp.end.date");
	    Date currentDate = new Date();
            Date endDate = null;
            if (endDateStr.indexOf(':') > 0) {
	      endDate = (DateFormat.getDateTimeInstance()).parse(endDateStr);
            } else {
	      endDate = (DateFormat.getDateInstance()).parse(endDateStr);
            }
            logger.trace("COMPARE " + endDateStr + "---" + endDate.getTime());
            logger.trace("COMPARE to " + currentDate.getTime());
            long diff = endDate.getTime() - currentDate.getTime();
            double diffDays = (double)diff / (24.0 * 60.0 * 60.0 * 1000.0);
            logger.trace("COMPARE DIFFDays: " + diffDays);
            retval = diffDays;

	} catch(Exception ex) {
	    ex.printStackTrace();
	    logger.error("Caught exception in parsing access date");
	}

	return retval;
    }


    public  static String getReleaseDown( )  {

      StringBuffer relMsg=new StringBuffer("");
      String releaseDown = cpsProperties.getProperty("release.down.file");
      if (releaseDown == null) releaseDown = "";
      File releaseDownFile = new File(releaseDown);
      if (releaseDownFile.exists() && releaseDown.length() > 3 ) {
        FileReader fileR = null;
        BufferedReader releaseFileBR = null;
        try {
          fileR = new FileReader(releaseDownFile);
          releaseFileBR = new BufferedReader(fileR);
          String inputLine;
          while( (inputLine = releaseFileBR.readLine()) != null) {
            relMsg.append(inputLine);
          }
          releaseFileBR.close();
          fileR.close();
        }
        catch (Exception exc) {
          relMsg = new StringBuffer("Down for maintenance.  Please try again later.");
          logger.error("Release Down error",exc);
          try {
            if (releaseFileBR != null) releaseFileBR.close();
            if (fileR != null) fileR.close();
          } catch (Exception e) {
             logger.error(e);
          }
        }
      }
  
      return relMsg.toString();
    }

    public static String getCDOMessage( )  {
      String cdoMessage = cpsProperties.getProperty("cdo.message.file") ;

      return getCDOMessage(cdoMessage);
         
    }

    public static String getDDTCDOMessage( )  {
      String cdoMessage = cpsProperties.getProperty("cdo.ddt.message.file") ;

      return getCDOMessage(cdoMessage);
    }
         

    public static String getCDOMessage(String cdoMessage )  {

      StringBuffer cdoText = new StringBuffer("");
      //String cdoMessage = cpsProperties.getProperty("cdo.message.file") ;
  
      if (cdoMessage == null) cdoMessage = "";
      File cdoMessageFile = new File(cdoMessage);
      if (cdoMessage.length() > 3 && cdoMessageFile.exists() ) {
        FileReader fileR = null;
        BufferedReader cdoFileBR = null;
        try {
          fileR = new FileReader(cdoMessageFile);
          cdoFileBR = new BufferedReader(fileR);
          String inputLine;
          while( (inputLine = cdoFileBR.readLine()) != null) {
            cdoText.append(inputLine);
          }
          cdoFileBR.close();
          fileR.close();
        }
        catch (Exception exc) {
          logger.error("CDO Message",exc);
          try {
            if (cdoFileBR != null) cdoFileBR.close();
            if (fileR != null) fileR.close();
          } catch (Exception e) {
             logger.error(e);
          }
        }
      }
      return cdoText.toString();
    }

    public  static String getNoDDTMessage( )  {

      StringBuffer relMsg=new StringBuffer("");
      String releaseDown = cpsProperties.getProperty("noddt.message.file");
      if (releaseDown == null) releaseDown = "";
      File releaseDownFile = new File(releaseDown);
      if (releaseDownFile.exists() && releaseDown.length() > 3 ) {
        FileReader fileR = null;
        BufferedReader releaseFileBR = null;
        try {
          fileR = new FileReader(releaseDownFile);
          releaseFileBR = new BufferedReader(fileR);
          String inputLine;
          while( (inputLine = releaseFileBR.readLine()) != null) {
            relMsg.append(inputLine);
          }
          releaseFileBR.close();
          fileR.close();
        }
        catch (Exception exc) {
          relMsg = new StringBuffer("DDTs are currently not being accepted at this time. Please contact the CXC HelpDesk with any questions.");
          logger.error("DDT Down error",exc);
          try {
            if (releaseFileBR != null) releaseFileBR.close();
            if (fileR != null) fileR.close();
          } catch (Exception e) {
             logger.error(e);
          }
        }
      }
  
      return relMsg.toString();
    }

  /****************************************************************************/
  /**
   * Return the cps properties.  These are stored as a resource in
   * the cps jar file.
   *
   * @return The set of CPS configuration properties.
   */

    public static Properties getProperties() {
	reloadProperties();
	return CPS.cpsProperties;
    }

  /****************************************************************************/
  /**
   * Return the cps properties.  These are stored as a resource in
   * the cps jar file.
   *
   * @param context The servlet context object.
   *
   * @return The set of CPS configuration properties.
   */

  public static Properties getProperties( ServletContext context)  {

    try {
      if ( cpsProperties == null ) {
        cpsProperties = new Properties();
        reloadProperties();
        initializeLogger();
        System.out.println(getCurrentDate() + ": CPS NEW PROPERTIES");
      } 
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    // Return the global value.
    return CPS.cpsProperties;
  }


    /*
     * reloads the .htproperties file to get the values for fields which may
     * change, like timeout
     */
    private static void reloadProperties()  {
	Properties newProp = new Properties();
	InputStream propStream = null;
	
	try {
          if(theConfigFile == null) {
            String htpath = System.getenv("OBSCYCLE_DATA_PATH");
            if (htpath.length() < 3) {
              htpath=null;
            }
	    theConfigFile = htpath + CPSConstants.PROPERTYFILE;
            System.out.println(getCurrentDate() + "  CPS: " + theConfigFile);
          }
	  propStream = new FileInputStream(theConfigFile);
	  if(propStream != null) {
		newProp.load(propStream);

		//Add the new properties to the original cpsProperties
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
		    oldPropertyValue = cpsProperties.getProperty(currentPropertyName);
		    if(oldPropertyValue == null) {
			//logger.warn("Adding: " + currentPropertyName + " : " + currentPropertyValue);
		    } else if(!currentPropertyValue.equals(oldPropertyValue)) {
			System.out.println(getCurrentDate() + ": Changing: " + currentPropertyName );
					   //+ " from " + oldPropertyValue 
					   //+ " to " + currentPropertyValue);
		    }

		    cpsProperties.setProperty(currentPropertyName, currentPropertyValue); 

		}
	    }
	    propStream.close();
	} catch(Exception ex) {
            try {
	      if (propStream != null) propStream.close();
            } catch (Exception e) {
	      logger.error(e);
            }
	    //logger.error("Error: Caught exception in CPS::reloadProperties: " + 
			       //ex.getMessage());
	    ex.printStackTrace();
	}

    }
   public static Long convertDate(String ival)
   {
       Long lval = null;
       try {
         DateFormat formatter= new SimpleDateFormat("dd-MMM-yyyy kk:mm");
         Date inDate = formatter.parse(ival);
         lval = Long.valueOf(inDate.getTime());
       } catch (Exception exc) {
         logger.error("convertDate",exc);
       }
       return lval;
   }


   public static String convertMS(Integer ival)
   {
     String retval = "";
     if (ival != null) {
        retval = convertMS(Long.valueOf(ival.toString()),false);
     }
     return retval;
   }
   public static String convertMS(Long ival) 
   {
     String retval = "";
     if (ival != null) {
        retval = convertMS(ival,false);
     }
     return retval;
   }

   public static String convertMS(Long ival,boolean zoneFlg)
   {
     String retval = "";
     if (ival != null) {
       try {
         Date dstr = new Date(ival);
         String datePattern = "dd-MMM-YYYY HH:mm";
         if (zoneFlg) datePattern += " z";
         SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
         retval = sdf.format(dstr);
       } catch (Exception exc) {
         logger.error("convertMS", exc);
         retval="";
       }
     }
     return retval;
   }
  public static String getDoubleCoord(Double dd,int flg)
   {
     String retval="";
     if (dd != null)  {
       if (flg==0 || (flg==1 && dd.doubleValue() >0)) {
         retval = String.format("%10.6f",dd);
       }
     }
     return retval;
   }

  public static String getDouble(Double dd,int flg)
   {
     String retval="";
     if (dd != null)  {
       if (flg==0 || (flg==1 && dd.doubleValue() >0)) {
         retval = dd.toString();
       }
     }
     return retval;
   }

  public static String getFloat(Float dd,int flg)
   {
     String retval="";
     if (dd != null)  {
       if (flg==0 || (flg==1 && dd.floatValue() >0)) {
         retval = dd.toString();
       }
     }
     return retval;
   }

   public static String getInt(Integer dd)
   {
     if (dd == null)
       return "";
     else
       return dd.toString();
   }

   public static String getXmlString(String ss)
   {
      String istr = StringEscapeUtils.escapeXml10(getString(ss));
      return istr;
   }
   public static String getHtmlString(String ss)
   {
      String istr = StringEscapeUtils.escapeHtml4(getString(ss));
      //istr = StringEscapeUtils.escapeXml(istr);
      return istr;
   }
   public static String getHtmlError(String ss) 
   {
      String istr = "<span class='errmsg'>" + StringEscapeUtils.escapeHtml4(getString(ss)) + "</span>";
      return istr;
   }
      
     

   public static String getString(String ss)
   {
     if (ss == null)
       return "";
     else
       return ss;
   }
   public static String getYO(String ss)
   {
     if (ss == null)
       return "";
     else if (ss.equals("N"))
       return "";
     else
       return ss;
   }

   public static YesNoPreferred getYNP(String tstr)
   {
     YesNoPreferred cval = YesNoPreferred.NO;
     
     if (tstr != null) {
       if (tstr.indexOf('P') >= 0)
         cval = YesNoPreferred.PREFERRED;
       else if (tstr.indexOf('Y') >= 0)
         cval = YesNoPreferred.YES;
     }
     return cval;
   }
   public static String getYNP(YesNoPreferred cval)
   {
     String retval="";
     
     if (cval != null) 
       retval= cval.toString();

     return retval;
   }

   public static String getYN(Boolean bb,String defaultVal)
   {
     String retval = defaultVal;
     if  (bb != null) {
       retval = getYN(bb);
     }
     return retval;
   }
        
   public static String getYN(Boolean bb)
   {
     String retval = "N";
     if (bb!= null && bb) {
       retval="Y";
     }
     return retval;
   }

   public static String getYN(YesNo cval)
   {
     String retval="";
     
     if (cval != null) 
       retval= cval.toString();

     return retval;
   }

   public static String getYN(YesNo cval, String defaultVal)
   {
     String retval=defaultVal;
     
     if (cval != null) 
       retval= cval.toString();

     return retval;
   }

   public static String getDeadlineMsg(boolean isCfP,boolean ddtRequest) 
   {
     String deadline="";
     String officialDate = cpsProperties.getProperty("cfp.official.date.str");
     if (!ddtRequest) {
       if (isCfP) {
         double daysLeft = CPS.deadlineCfP(true);
         if (daysLeft < 0)  
           deadline += "<span class='errmsg3'>It is past the deadline. Please save and submit your proposal now.  Deadline: " + officialDate+ "</span>";
         else if (daysLeft < .05)  {
            daysLeft = Math.ceil(daysLeft * 24 * 60);
            if (daysLeft <= 1) 
              deadline += "<span class='errmsg3'>There is less than 1 minute until the " + officialDate + " deadline.</span>";
            else
              deadline += "<span class='errmsg3'>There are less than " + (int)daysLeft + " minutes until the " + officialDate + " deadline.</span>";
       
         }
         else if (daysLeft < .2)  {
            daysLeft = Math.ceil(daysLeft * 24);
            if (daysLeft <= 1) 
              deadline += "<span class='errmsg2'>There is less than 1 hour until the " + officialDate + " deadline.</span>";
            else
              deadline += "<span class='errmsg2'>There are less than " + (int)daysLeft + " hours until the " + officialDate + " deadline.</span>";
         }
         else  {
           daysLeft=Math.ceil(daysLeft);
           if (daysLeft <= 1.0) 
             deadline += "<span class='errmsg'>There is less than 1 day until the " + officialDate + " deadline.</span>";
           else
             deadline += "There are <span style='font-weight:bold;'>" + (int)daysLeft + "</span> days left until the " + officialDate + " deadline.";
         }
       }
       else {
         deadline = "The Call for Proposals(CfP) submission period has closed.";
       }
     }
     return deadline;
   }
 
   public static String getCurrentDate() {
     String pattern = "yyyy-MM-dd hh:mm:ss";
     SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
     String curdate="";

     try {
       curdate = simpleDateFormat.format(new Date());
     } catch (Exception e) {
       curdate ="";
     } 
     return  curdate;
  }


  /**
   * Private variables
   */

    private static Properties cpsProperties = null;
    private static String theConfigFile = null; 
    private static Logger logger = Logger.getLogger(CPS.class);
    private static final long serialVersionUID = 1;


       
}

/******************************************************************************/
