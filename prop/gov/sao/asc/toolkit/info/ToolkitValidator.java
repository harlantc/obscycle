package info;
/*
  Copyrights:
 
  Copyright (c) 1998-2019,2021 Smithsonian Astrophysical Observatory
 
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
import java.util.Objects;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/******************************************************************************/
/**
 * Provide operations to support input field validation.
 * These include:
 *   - checking that a parameter value is within a given range
 *   - checking that an energy value is within acceptable limits
 *   - collecting and reporting multiple input errors.
 */

public class ToolkitValidator implements ToolkitConstants
{
  /**
   * Heading (label) background color (opaque) code for input
   * parameters that are within acceptable limits.
   */

  public static final String BG_COLOR_NORMAL = "#eeeeee";

  /**
   * Heading (label) background color (red) code for input parameters
   * that are in error.
   */

  public static final String BG_COLOR_ERROR = "#ff0000";

  /**
   * Position code for the first of a pair of position specs.
   */

  public static final String POSITION1 = "RA-L2-EL";

  /**
   * Position code for the second of a pair of position specs.
   */

  public static final String POSITION2 = "DEC-B2-EB";

  /****************************************************************************/
  /**
   * Construct a validation object.
   *
   * @param request The browser generated request packet object.
   * @param toolkitProperties The set of toolkit properties.  The
   * validation object obtains limit information, regular expresssions
   * and some miscellaneous constants from the toolkit properties.
   *
   * NOTE: When convenient, remove the <i>session</i> and
   * <i>toolkitParameters</i>.  The session is deriveable from the
   * <i>request</i> object.  Toolkit properties can be obtained using
   * static methods on the <b>Toolkit</b> class.
   */

  public ToolkitValidator( HttpServletRequest request,
                           Properties toolkitProperties )
  {
    this.request = request;
    session = request.getSession( true );
    this.toolkitProperties = toolkitProperties;
  }

  /**
   * Indicate whether or not a named parameter's value
   * is a valid Longitude value.. supported formats:
   *   HH:MM:SS.SSS
   *   HH MM SS.SSS
   *   Decimal degrees
   *
   * @param paramName Parameter name, key in request/session.
   *
   * @param paramColorKey Parameter box color, key in request/session.
   *
   * @param fieldKey Property key giving the field label.
   *
   * @return null if parameter value is valid, otherwise an error message.
   */
  public String validateLongitudeParameter( String paramName,
                                            String paramColorKey, 
                                            String fieldKey)
  {
      String errmsg;
      errmsg = validateInputPosition( fieldKey, POSITION1, paramColorKey, paramName );

      return errmsg;
  }

  /**
   * Indicate whether or not a named parameter's value
   * is a valid Latitude value.. supported formats:
   *   DD:MM:SS.SSS
   *   DD MM SS.SSS
   *   Decimal degrees
   *
   * @param paramName Parameter name, key in request/session.
   *
   * @param paramColorKey Parameter box color, key in request/session.
   *
   * @param fieldKey Property key giving the field label.
   *
   * @return null if parameter value is valid, otherwise an error message.
   */
  public String validateLatitudeParameter( String paramName,
                                           String paramColorKey, 
                                           String fieldKey)
  {
      String errmsg;
      errmsg = validateInputPosition( fieldKey, POSITION2, paramColorKey, paramName );

      return errmsg;
  }

  /**
   * Indicate whether or not a named parameter's value falls within a range.
   *
   * @param paramName Parameter name, key in request/session.
   *
   * @param paramColorKey Parameter box color, key in request/session.
   *
   * @param rangeKey Property key giving the valid range limits.
   *
   * @param fieldKey Property key giving the field label.
   *
   * @return null if parameter value is valid, otherwise an error message.
   */
  public String validateRangeParameter( String paramName,
                                        String paramColorKey,
                                        String rangeKey,
                                        String fieldKey )
  {
    String errmsg = null;

    // Get parameter value.
    String value = Parameter.get( request, paramName );
    String limits = toolkitProperties.getProperty( rangeKey );

    // Validate value against specified range limits.
    if ( "".equals( value ) ){
        errmsg = toolkitProperties.getProperty( "blank.reason.text" );
    }
    else if (limits == null ){
        errmsg = "limits not found";
    }
    else{
        try{
            if ( !inRange( value, rangeKey ) ){
                errmsg = toolkitProperties.getProperty("out-of-range.reason.text");
            }
        }catch( NumberFormatException ex ){
            errmsg = toolkitProperties.getProperty("invalid-numeric.reason.text");
        }
    }    

    if ( errmsg == null ){
        session.setAttribute( paramColorKey, BG_COLOR_NORMAL );
    }else{
        // Enhance error message, update session state
        errmsg = getProcessedError( errmsg, paramColorKey, fieldKey );
    }

    return errmsg;
  }

  /**
   * Indicate whether or not a named parameter's value 
   * is a valid Absolute Time:
   *   o YYYY:DDD:HH:MM:SS[.SSS]
   *   o YYYY-MM-DD'T'HH:MM:SS[.SSS]
   *   o decimal day - assumed to be MJD
   *
   * @param paramName Parameter name, key in request/session.
   *
   * @param paramColorKey Parameter box color, key in request/session.
   *
   * @param fieldKey Property key giving the field label.
   *
   * @return null if parameter value is valid, otherwise an error message.
   */
  public String validateAbsTimeParameter( String paramName,
                                          String paramColorKey,
                                          String fieldKey )
  {
    ArrayList<String> errorList = new ArrayList<String>();
    String errmsg = null;

    // Get parameter value.
    String value = Parameter.get( request, paramName );

    // Validate value.
    if ( "".equals( value ) ){
        errorList.add( toolkitProperties.getProperty( "blank.reason.text" ) );
    }
    else{

        AbsoluteTime instant = null;
        try{
            // Simple decimal value is interpreted as MJD date.
            if ( Pattern.compile( RE_DECIMAL ).matcher( value ).matches() ){
                instant = AbsoluteTime.fromMJD( Double.valueOf( value ) );
            }
            else{
                // interpret datetime string
                instant = new AbsoluteTime( value );
            }

            // Verify component ranges
            //  o the constructor performs sanity checks on the field values
            //    and throws an exception for bad content.
            //  o these checks allow the toolkit to constrain the values within
            //    the normal range of each field.
            if (! inRange( instant.getYear().doubleValue(), "calendar.year.limits" )){
                errorList.add( toolkitProperties.getProperty("year.out-of-range.reason.text") );
            }
            if (! inRange( instant.getDayOfYear().doubleValue(), "calendar.doy.limits" )){
                errorList.add( toolkitProperties.getProperty("doy.out-of-range.reason.text") );
            }
            if (! inRange( instant.getMonth().doubleValue(), "calendar.month.limits" )){
                errorList.add( toolkitProperties.getProperty("month.out-of-range.reason.text") );
            }
            if (! inRange( instant.getDayOfMonth().doubleValue(), "calendar.day.limits" )){
                errorList.add( toolkitProperties.getProperty("day.out-of-range.reason.text") );
            }
            if (! inRange( instant.getHours().doubleValue(), "calendar.hour.limits" )){
                errorList.add( toolkitProperties.getProperty("hours.out-of-range.reason.text") );
            }
            if (! inRange( instant.getMinutes().doubleValue(), "calendar.min.limits" )){
                errorList.add( toolkitProperties.getProperty("minutes.out-of-range.reason.text") );
            }
            if (! inRange( instant.getSeconds().doubleValue(), "calendar.sec.limits" )){
                errorList.add( toolkitProperties.getProperty("seconds.out-of-range.reason.text") );
            }

        }catch( IllegalArgumentException ex ){
            if ( ex.getMessage().contains("not in supported format") ){
                errorList.add( toolkitProperties.getProperty("invalid-format.reason.text") );
            }
            else {
                errorList.add( ex.getMessage() );
            }
        }catch( Exception ex ){
            errorList.add( toolkitProperties.getProperty("unknown.reason.text"+ex) );
        }
    }

    // remove any null entries
    errorList.removeIf(Objects::isNull);

    if ( errorList.isEmpty() ){
        session.setAttribute( paramColorKey, BG_COLOR_NORMAL );
    }else{
        // Enhance error message, update session state
        errmsg = "";
        for ( String item: errorList ){
            errmsg += getProcessedError( item, paramColorKey, fieldKey );
        }
    }

    return errmsg;
  }

  /**
   * Indicate whether or not a named parameter's value 
   * is a valid Relative Time.
   * Supported formats:
   *   o DDD:HH:MM:SS.SSS
   *   o DDD.DDDD
   *
   * @param paramName Parameter name, key in request/session.
   *
   * @param paramColorKey Parameter box color, key in request/session.
   *
   * @param fieldKey Property key giving the field label.
   *
   * @return null if parameter value is valid, otherwise an error message.
   */
  public String validateRelTimeParameter( String paramName,
                                          String paramColorKey,
                                          String fieldKey )
  {
    ArrayList<String> errorList = new ArrayList<String>();
    String errmsg = null;

    // Get parameter value.
    String value = Parameter.get( request, paramName );

    // Validate value against specified range limits.
    if ( "".equals( value ) ){
        errorList.add( toolkitProperties.getProperty( "blank.reason.text" ) );
    }
    else{
        try{
            RelativeTime period = new RelativeTime( value );

            // Verify component ranges
            if (! inRange( period.getDays().doubleValue(), "relative.days.limits" )){
                errorList.add( toolkitProperties.getProperty("doy.out-of-range.reason.text") );
            }
            if (! inRange( period.getHours().doubleValue(), "calendar.hour.limits" )){
                errorList.add( toolkitProperties.getProperty("hours.out-of-range.reason.text") );
            }
            if (! inRange( period.getMinutes().doubleValue(), "calendar.min.limits" )){
                errorList.add( toolkitProperties.getProperty("minutes.out-of-range.reason.text") );
            }
            if (! inRange( period.getSeconds(), "calendar.sec.limits" )){
                errorList.add( toolkitProperties.getProperty("seconds.out-of-range.reason.text") );
            }

        }catch ( NumberFormatException ex ){
            errorList.add( toolkitProperties.getProperty("invalid-format.reason.text") );
        }catch( Exception ex ){
            errorList.add( toolkitProperties.getProperty("unknown.reason.text"+ex) );
        }
    }

    // remove any null entries
    errorList.removeIf(Objects::isNull);

    if ( errorList.isEmpty() ){
        session.setAttribute( paramColorKey, BG_COLOR_NORMAL );
    }else{
        // Enhance error message, update session state
        errmsg = "";
        for ( String item: errorList ){
            errmsg += getProcessedError( item, paramColorKey, fieldKey );
        }
    }

    return errmsg;
  }

    /**
     * Convert decimal days to ks
     *   86400.0 seconds/day
     *
     * @param days duration in days
     * @return duration in ks (kiloseconds)
     */
    public Double convertDaysToKiloseconds( Double days ) {
        Double ks;
        if ( days.isNaN() ){
            ks = Double.NaN;
        }
        else{
            ks = days * 86.400;
        }
        return ks;
    }

  /****************************************************************************/
  /**
   * Return the lower limit value for a range check.
   *
   * @param key The property name used to access a pair of values
   * defining the range of acceptable values.
   *
   * @return The low value in the range spec.
   */

  public double getLowLimit( String key )
  {
    double result = 0.0;

    // Fetch the range from the property
    String range = toolkitProperties.getProperty( key, "0.0 1000.0" );

    // Extract the low bound and return it as a double value.
    StringTokenizer st = new StringTokenizer( range );
    String lower = st.nextToken();
    result = (Double.valueOf( lower )).doubleValue();
    return result;
  }

  /****************************************************************************/
  /**
   * Return the high limit value for a range check.
   *
   * @param key The property name used to access a pair of values
   * defining the range of acceptable values.
   *
   * @return The high value in the range.
   */

  public double getHighLimit( String key )
  {
    double result = 0.0;

    // Fetch the range from the property
    String range = toolkitProperties.getProperty( key, "0.0 1000.0" );

    // Extract and ignore the lower bound.
    StringTokenizer st = new StringTokenizer( range );
    String lower = st.nextToken();

    // Determine if there is an upper bound, i.e. if there are any
    // more tokens.
    if ( st.hasMoreTokens() )
    {
      // There are.  Extract and return the upper bound.
      String upper = st.nextToken();
      result = (Double.valueOf( upper )).doubleValue();
    }
    else
    {
      // There is no upper bound.  Return a default maximum value.
      result = Double.MAX_VALUE;
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Build and return a JavaScript program which will popup an alert
   * box detailing the reasons that the input is in error.
   *
   * @param reasons A list of field names and associated causes for
   * the error.
   *
   * @return the JavaScript program (script) which produces a popup
   * detailing any input errors and the associated cause.
   */

  public String buildErrorScript( String reasons )
  {
    String alertMessage =
      toolkitProperties.getProperty( "alert.message.text" );
    String result = "<font color=\"red\"><pre>" + alertMessage +"\n";
    result += reasons;
    result += "</pre></font>";
    return result;
  }

  /****************************************************************************/
  /**
   * Return an indication that errors have occurred.
   *
   * @param errors An array of error strings.  A null indicates that
   * no error occurred.
   *
   * @return true if any error strings are non null.
   */

  public boolean errorsOccurred( String[] errors )
  {
    // Initialize for no errors.
    boolean result = false;
    String reasons = "";

    // Process all the potential errors.
    for ( int i = 0; i < errors.length; i++ )
    {
      if ( errors[i] != null )
      {
        // Generate the error reason list.
        reasons += errors[i];
      }
    }

    // Determine if any errors occurred.
    if ( ! "".equals( reasons ) )
    {
      // Errors occurred.  Generate JavaScript code for a User popup.
      String errorScript = buildErrorScript( reasons );
      session.setAttribute( "errorScript", errorScript );
      session.setAttribute( "inputErrors", new Boolean( true ) );
      result = true;
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Return the name of the property which specifies the default
   * energy range for a particular mission and instrument.
   *
   * @param io A prefix that specifies either input or output energy
   * range.
   *
   * @return The key to use to access energy range.
   */

  public String getEnergyRangeKey( String io )
  {
    String result;

    // Determine the IO mode.
    String mode = Parameter.get( request, io + "Mode" );
    if ( "mission".equals( mode ) )
    {
      // per CDO as of cycle 16, no real limits let pimms cli handle values 
      result="arbitrary"; 

      // Fetch the mission name and the instrument spec.
      //String missionName = Parameter.get( request, io + "MissionSelector" );
      //String instrument = Parameter.get( request, io + "Instrument" );
      //result = missionName + "-" + instrument;
    }
    else
    {
      // Fetch the flux type.
      String fluxType = Parameter.get( request, io + "FluxSelector" );
      result = "Flux-" + fluxType;
    }
    return result;
  }

  /****************************************************************************/
  /**
   * Indicate whether or not an input parameter is a valid equinox value.
   *
   * @param fieldNameKey A key for accessing the field name property.
   *
   * @param label The name of the GUI input box label to be colored if
   * an error is encountered.
   *
   * @param parName The name of the GUI input box component to be
   * error checked.
   *
   * @return null if the input parameter <i>parName</i> is valid or an
   * error reason if the input is invalid.
   */

  public String validateEquinoxParameter( String fieldNameKey, String label,
                                          String parName )
  {
    String result = null;

    // Use the all-purpose parameter checker
    result = validateParameter( fieldNameKey, RE_EQUINOX, label, parName, null );

    return result;
  }

  /****************************************************************************/
  /**
   * Indicate whether or not an input parameter has an acceptable value.
   *
   * @param fieldNameKey The key for accessing the field name property.
   *
   * @param positionCode Identifies the input position class: RA, L2
   * or EL versus Dec, B2 or EB.
   *
   * @param label The name of the GUI input box label to be colored if
   * an error is encountered.
   *
   * @param par The name of the GUI input box component to be error
   * checked.
   *
   * @return null if the input parameter <i>parName</i> is valid or an
   * error reason if the input is invalid.
   */

  public String validateInputPosition( String fieldNameKey,
                                       String positionCode, 
                                       String label, String par )
  {
    // Initialize for a valid result.
    String error = null;
    session.setAttribute( label, BG_COLOR_NORMAL );

    // Get parameter value.
    String position = Parameter.get( request, par );

    // Validate value.
    if ( position == null || "".equals( position ) ){
      error = toolkitProperties.getProperty( "blank.reason.text" );
    }
    else{
        try{
            // Check that the input satisfies one of following:
            // - sexagesimal
            // - decimal

            // replace ':' to allow simpler regular expression
            position = position.replaceAll(":"," ");

            // Set patterns for both sexagesimal and decimal formats.
            // The position must be valid in one of the two formats
            // or an invalid format error is signaled.
            Pattern sexagesimalPattern; 
            Pattern decimalPattern; 
            Matcher sexagesimalMatch; 
            Matcher decimalMatch; 

            // Choose patterns for Longitiude or Latitude
            if ( positionCode.startsWith( "RA" ) ) {
                // Set up for RA/L2/EL inputs.
                sexagesimalPattern = Pattern.compile(RE_NONNEGATIVE_SEXAGESIMAL);
                decimalPattern = Pattern.compile(RE_NONNEGATIVE_DECIMAL);

            } else {
                // Set up for DEC/B2/EB inputs.
                sexagesimalPattern = Pattern.compile(RE_SEXAGESIMAL);
                decimalPattern = Pattern.compile(RE_DECIMAL);
            }
            sexagesimalMatch = sexagesimalPattern.matcher( position );
            decimalMatch = decimalPattern.matcher( position );

            // Check sexagesimal format.
            if ( sexagesimalMatch.matches() ) {

                // Extract the components.
                // assume long form match
                String hh_dd = sexagesimalMatch.group( 1 );
                String mm = sexagesimalMatch.group( 2 );
                String ss = sexagesimalMatch.group( 3 );

                // handle short form match
                if ( (hh_dd == null) ||  "".equals( hh_dd ) ){
                    hh_dd = sexagesimalMatch.group( 4 );
                    mm = sexagesimalMatch.group( 5 );
                    ss = "0.0";
                }
          
                // Verify the components.
                if ( !inRange( hh_dd, "SEXAGESIMAL/" + positionCode ) ){
                    if ( positionCode.startsWith( "RA" ) ){
                        error = toolkitProperties.getProperty( "hours.out-of-range.reason.text" );
                    }
                    else{
                        error = toolkitProperties.getProperty( "degrees.out-of-range.reason.text" );
                    }
                }
                else if ( !inRange( mm, "SEXAGESIMAL-MM/" + positionCode ) ){
                    error = toolkitProperties.getProperty( "minutes.out-of-range.reason.text" );
                }
                else if ( !inRange( ss, "SEXAGESIMAL-SS/" + positionCode ) ){
                    error = toolkitProperties.getProperty( "seconds.out-of-range.reason.text" );
                }

                // Convert to decimal, and validate
                double hoursOrDegrees = Double.parseDouble( hh_dd );
                double sign = hoursOrDegrees / Math.abs( hoursOrDegrees );
                double total = sign * ( Double.parseDouble( ss ) +
                                        60.0 * Double.parseDouble( mm ) +
                                        3600.0 * Math.abs( hoursOrDegrees ) );
                total = total / 3600.0;

                if ( !inRange( new Double( total ), "SEXAGESIMAL/" + positionCode ) ){
                    error = toolkitProperties.getProperty( "total.out-of-range.reason.text" );
                }

            }else if ( decimalMatch.matches() ){
                // Verify value is within range.
                if ( !inRange( position, "DECIMAL/" + positionCode ) ){
                    error = toolkitProperties.getProperty( "out-of-range.reason.text" );
                }
            }else{
                // Failed to match either format.
                error = toolkitProperties.getProperty( "invalid-format.reason.text" );
            }
        }
        catch ( Exception exc ){
            logger.error( "Unexpected exception validating position: " + positionCode );
            error = "Internal software error --- stored in servlet logs";
        }
    }

    // Update the label if an error has occurred and return the result.
    return getProcessedError( error, label, fieldNameKey );
  }


  /****************************************************************************/
  /**
   * Indicate whether or not a named parameter's value falls in a
   * range.
   *
   * @param fieldName The name of the field being checked.  Used as a
   * prefix for the reason code.
   *
   * @param label The name of the GUI input box label.
   *
   * @param parName The name of the GUI input box component.
   *
   * @param rangeKey The value range property key.
   *
   * @return null if the input is valid or an error reason if the
   * input is invalid.
   */

  public String validateParameter( String fieldName, String label,
                                   String parName, String rangeKey )
  {
    String result = null;

    // Default to a decimal number format.
    try
    {
        result = validateParameter( fieldName, RE_NUMBER, label, parName, rangeKey );
    }
    catch ( Exception exc ) {
        result = "Error in validateParameter routine";
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Indicate whether or not a named parameter has a numeric value.
   *
   * @param fieldName The name of the field being checked.  Uses as a
   * prefix for the reason code.
   *
   * @param label The name of the GUI input box label.
   *
   * @param parName The name of the GUI input box component.
   *
   * @return null if the input is a valid number or an error reason if
   * the input is invalid.
   */

  public String validateParameter( String fieldName, String label,
                                   String parName )
  {
    String result;

    // Default to a decimal number format.
    try
    {
        result = validateParameter( fieldName, RE_NUMBER, label, parName, null );
    }
    catch ( Exception exc )
    {
      result = "Error in validateParameter routine";
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Indicate whether or not a named parameter's value falls in a
   * specified range.  Use a specific regular expression to check the
   * input parameter syntax.
   *
   * @param fieldNameKey The prefix for the reason code.  This
   * parameter identifies the field by name.  It is usually the field
   * label.
   *
   * @param regexp Identifies the input position class: RA, L2
   * or EL versus Dec, B2 or EB.
   *
   * @param label The name of the GUI input box label.
   *
   * @param parName The name of the GUI input box component.
   *
   * @param rangeKey The value range property key.  This toolkit range
   * property identifies the acceptable limits for the parameter's
   * value.
   *
   * @return null if the input is valid or an error reason if the
   * input is invalid.
   */

    public String validateParameter( String fieldNameKey, String regexp,
                                     String label, String parName,
                                     String rangeKey ) 
  {
    // Initialize for a valid result.
    String error = null;
    session.setAttribute( label, BG_COLOR_NORMAL );

    logger.trace("Validating " + parName + " key=" + rangeKey + " using" + regexp);
    // Check that the parameter needs to be validated.
    if ( Parameter.has( request, parName ) )
    {
      // It does.  Check for an empty value.
      String parText = Parameter.get( request, parName );
      parText = parText.trim();
      if ( "".equals( parText ) )
      {
        // special case
        if (!parName.equalsIgnoreCase("nh"))  {
          // It's empty.  Set the result.
          error = toolkitProperties.getProperty( "blank.reason.text" );
        }
      }
      else
      {
        // It's not empty.  Check that it is a valid format.
          //REMatch match = regexp.getMatch( parText );
          Pattern pattern = Pattern.compile(regexp);
          Matcher matcher = pattern.matcher(parText);
        if ( matcher.matches() )
        {
          // It is.  Check that the value is in range.
          if ( rangeKey != null && !inRange( parText, rangeKey ) )
          {
            // Out of range.  Signal the error.
            error =
              toolkitProperties.getProperty( "out-of-range.reason.text" );
          }
        }
        else
        {
          // It is not a valid format.  Signal the error.
          error =
            toolkitProperties.getProperty( "invalid-format.reason.text" );
        }
      }
    }

    // Update the label if an error has occurred and return the result.
    return getProcessedError( error, label, fieldNameKey );
  }

  //*************************************************************************
  public String validateRangeLow( String fieldNameKey, String lowLabel,
                                  String rangeLimitKey,
                                  String lowParameterName,
                                  String highParameterName ,
                                String type)
  {
    // Initialize for a valid result.
    String error = null;
    session.setAttribute( lowLabel, BG_COLOR_NORMAL );

    // Determine if the range is being specified.
    String lowString = Parameter.get( request, lowParameterName );
    String highString = Parameter.get( request, highParameterName );
    if (lowString != null) 
      lowString= lowString.trim(); 
    if (highString != null) 
      highString= highString.trim(); 
    if (lowString == null ) {
    }
    else if (type.equals("mission") && 
           (lowString.equals("") || 
           (lowString.compareToIgnoreCase("default") == 0))) {
    }
    else 
    {
       if (highString == null || highString.equals("") || highString.compareToIgnoreCase("default")==0) {
           error = "Both low and high input Energy fields must be entered";
           error = getProcessedError( error, lowLabel, fieldNameKey );
       }  else {
           error = validateRangeLow( fieldNameKey, lowLabel,
                                  rangeLimitKey,
                                  lowParameterName,
                                  highParameterName );
       }
     }

     return error;

  }

  public String validateRangeHigh( String fieldNameKey, String highLabel,
                                   String rangeKey, String lowParameterName,
                                   String highParameterName,String type )
  {
    // Initialize for a valid result.
    String error = null;
    session.setAttribute( highLabel, BG_COLOR_NORMAL );

    // Determine if the range is being specified.
    String highString = Parameter.get( request, highParameterName );
    String lowString = Parameter.get( request, lowParameterName );
    if (lowString != null) 
      lowString= lowString.trim(); 
    if (highString != null) 
      highString= highString.trim(); 

    if (highString == null) {
    }
    else if (type.equals("mission") && 
             (highString.equals("") || 
             (highString.compareToIgnoreCase("default") == 0))) {
    }
    else 
    {
       if (lowString == null || lowString.equals("") || lowString.compareToIgnoreCase("default")==0) {
           error = "Both low and high output Energy fields must be entered";
           error = getProcessedError( error, highLabel, fieldNameKey );
       }  else {
           error = validateRangeHigh( fieldNameKey, highLabel,
                                  rangeKey,
                                  lowParameterName,
                                  highParameterName);
       }
    }
  
    return error;
  }

  /****************************************************************************/
  /**
   * Indicate whether a parameter being used as a lower limit in a
   * pair is less than the upper limit and that it falls in a
   * specified range.
   *
   * @param fieldNameKey A key for accessing the field name property.
   *
   * @param lowLabel The name of the GUI input box label.

   * @param rangeLimitKey  range limit key name
   *
   * @param lowParameterName The name of the GUI input parameter
   * holding the low value in the range.
   *
   * @param highParameterName The name of the GUI input parameter
   * holding the high value in the range.
   *
   * @return null if the input is valid or an error reason if the
   * input is invalid.
   */

  public String validateRangeLow( String fieldNameKey, String lowLabel,
                                  String rangeLimitKey,
                                  String lowParameterName,
                                  String highParameterName )
  {
    // Initialize for a valid result.
    String error = null;
    session.setAttribute( lowLabel, BG_COLOR_NORMAL );

    // Determine if the range is being specified.
    String lowString = Parameter.get( request, lowParameterName );
    if ( lowString != null )
    {
      // It is.  Check for an empty value.
      if ( "".equals( lowString ) )
      {
        // An invalid range value (empty) has been detected.  Set the
        // three flags.
        error = toolkitProperties.getProperty( "blank.reason.text" );
      }
      else
      {
        try
        {
          // Check that the value is a number.
            Pattern regexpPattern = Pattern.compile(RE_NUMBER);
            Matcher matcher = regexpPattern.matcher(lowString);
            if ( matcher.matches() )  {
            // It is.  Check that it is greater than the low limit.
            double lowLimit = getLowLimit( rangeLimitKey );
            Double lowValue = Double.valueOf( lowString );
            if ( lowValue.doubleValue() < lowLimit )
            {
              // It isn't.
              error = toolkitProperties.getProperty( "low.reason.text" );
            }
            else
            {
              // OK so far.  Make sure that the low value is less than
              // the high value.
              String hval = Parameter.get( request, highParameterName ) ;
              if (hval != null && !(hval.trim()).equals("")) {
                try { 
                  Double highValue = Double.valueOf(hval.trim());
                  if ( highValue.doubleValue() <= lowValue.doubleValue() )
                  {
                      error = toolkitProperties.getProperty( "invalid-range.reason.text" );
                  }
                } 
                catch (Exception exc) {
                      error = toolkitProperties.getProperty( "invalid-format.reason.text" );
                }
              }
            }
          }
          else
          {
            // An invalid value (format error) has been detected.  Set the
            // flags.
            error = toolkitProperties.getProperty( "invalid-format.reason.text" );
          }
        }
        catch ( Exception exc )
        {
          exc.printStackTrace();
          error =
            toolkitProperties.getProperty( "unknown.reason.text" );
        }
      }
    }

    // Update the label if an error has occurred and return the result.
    return getProcessedError( error, lowLabel, fieldNameKey );
  }

  /****************************************************************************/
  /**
   * Indicate whether or not a parameter which is the higher value in
   * a range is acceptable, ie that it is greater than a low value and
   * between a range of acceptable limits.
   *
   * @param fieldNameKey A key for accessing the field name property.
   *
   * @param highLabel The name of the GUI input box label.
   *
   * @param rangeKey A key for accessing the range property.
   *
   * @param lowParameterName The name of the GUI input parameter
   * holding the low value in the range.
   *
   * @param highParameterName The name of the GUI input parameter
   * holding the high value in the range.
   *
   * @return null if the input is valid or an error reason if the
   * input is invalid.
   */

  public String validateRangeHigh( String fieldNameKey, String highLabel,
                                   String rangeKey, String lowParameterName,
                                   String highParameterName )
  {
    // Initialize for a valid result.
    String error = null;
    session.setAttribute( highLabel, BG_COLOR_NORMAL );

    // Determine if the range is being specified.
    String highString = Parameter.get( request, highParameterName );
    if ( highString != null )
    {
      // It is.  Check for an empty value.
      if ( "".equals( highString ) )
      {
        // An invalid range value (empty) has been detected.  Set the
        // three flags.
        error = toolkitProperties.getProperty( "blank.reason.text" );
      }
      else
      {
        try
        {
          // Check that the value is a number.
            Pattern regexpPattern = Pattern.compile(RE_NUMBER);
            Matcher matcher = regexpPattern.matcher(highString);
            if ( matcher.matches() )  {
                
                // It is.  Check that it is less or equal to the high limit.
                double highLimit = getHighLimit( rangeKey );
                Double highValue = Double.valueOf( highString );
                if ( highValue.doubleValue() > highLimit )
                    {
                        // It isn't.
                        error = toolkitProperties.getProperty( "high.reason.text" );
                    }
                else
                    {
                        // OK so far.  Make sure that the high value is higher than
                        // the low value.
                        Double lowValue =
                Double.valueOf( Parameter.get( request, lowParameterName ) );
              if ( highValue.doubleValue() <= lowValue.doubleValue() )
              {
                error =
                  toolkitProperties.getProperty( "invalid-range.reason.text" );
              }
            }
          }
          else
          {
            // An invalid value (format error) has been detected.  Set the
            // flags.
            error = toolkitProperties.getProperty( "invalid-format.reason.text" );
          }
        }
        catch ( Exception exc )
        {
          error =
            toolkitProperties.getProperty( "unknown.reason.text" );
        }
      }
    }

    // Update the label if an error has occurred and return the result.
    return getProcessedError( error, highLabel, fieldNameKey );
  }


  /****************************************************************************/
  /**
   * Indicate whether or not a parameter which is the higher value in
   * a range is acceptable, ie that it is greater than a low value and
   * between a range of acceptable limits.
   *
   * @param fieldNameKey  the mission
   *
   * @param missionLbl The name of the GUI input box label.
   *
   * @param instrFld The name of the instrument field
   * @param instrLbl The name of the instrument label
   * @return null if the input is valid or an error reason if the
   * input is invalid.
   */

  public String validateMission( String fieldNameKey, String missionLbl,String instrFld, String instrLbl)
  {
    // Initialize for a valid result.
    String error = null;
    boolean foundMatch=false;
    session.setAttribute( missionLbl, BG_COLOR_NORMAL );
    session.setAttribute( instrLbl, BG_COLOR_NORMAL );
    String mission=null;
    String instrument=null;


    if (fieldNameKey.indexOf("NOMISSION") < 0) {
      mission = Parameter.get( request, fieldNameKey );
      instrument = Parameter.get( request, instrFld );
      logger.trace("mission =" + mission + "---------");

      if ( mission != null )
      {
        for (int ii=0;ii< ToolkitConstants.missionChoices.length; ii++) {
          if (mission.equals(ToolkitConstants.missionChoices[ii])) {
            foundMatch=true;
            break;
          }
        }
      }
      if (!foundMatch) {
        error = "Invalid option";
        // Update the label if an error has occurred and return the result.
        return getProcessedError( error, missionLbl, "mission-name.input" );
      } else {
          foundMatch=false;
          String[] instrumentChoices = null;
          instrumentChoices = Toolkit.getInstrument(mission);
          for ( int ii = 0; ii < instrumentChoices.length; ii++ ) {
            if (instrument.equals(instrumentChoices[ii])) {
              foundMatch=true;
              break;
            }
          }
          if (!foundMatch) {
             error = "Invalid option";
             // Update the label if an error has occurred and return the result.
             return getProcessedError( error, instrLbl, "instrument-name.input" );
          }
          else {
            return null;
          }
      }
    }
    else {
      return null;
    }
  }


  /****************************************************************************/
  /**
   * Private section.
   */

  /**
   * Keep private convenience copies for the request and session parameters.
   */

  private HttpServletRequest request;
  private HttpSession session;
  private static Logger logger = Logger.getLogger(ToolkitValidator.class);


  /**
   * Container for the Toolkit properties.
   */

  private Properties toolkitProperties;;

  /****************************************************************************/
  /**
   * If there is an error, update the label and return the error
   * string with the field name prepended.
   *
   * @param error The error indication, null iff no errors have
   * occurred, a reason code otherwise.
   * @param label The name of the GUI input box label.
   * @param fieldNameKey The key for accessing the field name property.
   *
   * @return null if the input is valid or an error reason with the
   * field name prepended if the input is invalid.
   */

  protected String getProcessedError( String error, String label,
                                    String fieldNameKey )
  {
    // Initialize for success.
    String result = null;

    // Determine if any errors have occurred.
    if ( error != null )
    {
      // Color code the label to visually signal an error.
      setFieldColor( label, BG_COLOR_ERROR );

      // Get the field name and prepend it to the error reason.
      String fieldName =
        toolkitProperties.getProperty( fieldNameKey, "Indeterminate field" );
      result = fieldName + ": " + error + "\n";
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Set the GUI field color to the provided color code.
   *
   * @param fieldLabel The GUI field label.
   * @param code The hex color code to assign. (eg: #FFFFFF)
   *
   */
    protected void setFieldColor( String fieldLabel, String code )
    {
        // set the session attribute for field color
        session.setAttribute( fieldLabel, code );
    }

  /****************************************************************************/
  /**
   * Return true IFF the provided value is within the specified range.
   *
   * @param value The double value to check.
   * @param rangeKey Toolkit property key for the range to apply.
   *
   * @return true iff the input is inclusively between the two bounds.
   */

  private boolean inRange( Double value, String rangeKey )
  {
    // Initialize for success.
    boolean result = true;

    // get range values
    double low = getLowLimit( rangeKey );
    double high = getHighLimit( rangeKey );
  
    logger.trace("Val=" + value + " Low:" + low + " High:" + high + " range: " +  rangeKey );

    // apply range check 
    if ( value.doubleValue() < low || value.doubleValue() > high )
    {
      result = false;
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Return true IFF the provided value, when converted to Double, 
   * is within the specified range.
   *
   * @param value The raw string value.
   * @param rangeKey Toolkit property key for the range to apply.
   *
   * @return true iff the input is inclusively between the two bounds.
   */

  private boolean inRange( String value, String rangeKey )
  {
    return inRange( new Double( value ), rangeKey );
  }

  /****************************************************************************/
  /**
   * Returns the stored request.
   *
   * @return request instance
   */

  protected HttpServletRequest getRequest()
  {
    return this.request;
  }

  /****************************************************************************/
  /**
   * Returns the stored toolkit properties
   *
   * @return properties instance
   */

  protected Properties getToolkitProperties()
  {
    return this.toolkitProperties;
  }

}
