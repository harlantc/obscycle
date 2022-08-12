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

package gov.sao.asc.obsed.constraint;

/******************************************************************************/

import gov.sao.asc.obsed.Constants;
import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.database.ColumnEntry;
import gov.sao.asc.obsed.database.Database;
import gov.sao.asc.obsed.database.DatabaseConfiguration;
import gov.sao.asc.obsed.database.DatabaseKey;
import gov.sao.asc.obsed.database.DatabaseModel;
import gov.sao.asc.util.LogClient;
import gov.sao.asc.obsed.view.ComponentEntry;
import gov.sao.asc.obsed.view.Configuration;
import gov.sao.asc.obsed.view.ConfigurationException;
import gov.sao.asc.obsed.database.RecordDoesNotExistException;
import gov.sao.asc.obsed.view.ComponentEntry;
import java.awt.Component;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

/******************************************************************************/
/**
 * A ConstraintChecker object processes constraint handling in a
 * general fashion.  It is based of the key column value lookup
 * scheme.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class ConstraintChecker extends Object
{
  // Constants for handling various types of constraints.
  public final static int BOUNDSF = 0;
  public final static int BOUNDSI = 1;
  public final static int GRATING = 2;
  public final static int NONE = 3;
  public final static int OFFSET = 4;
  public final static int POSITION = 5;
  public final static int ROLL = 6;
  public final static int SEGMENT = 7;
  public final static int TIME = 8;
  public final static int VMAG = 9;
  public final static int PHOTO = 10;
  public final static int PROPINFO = 11;
  public final static int AOSTR = 12;
  public final static int COI = 13;
  public final static int EVENTFILTER = 14;
  public final static int MONITOR = 15;

  // Code for the constraint type.
  private int constraintType;

  private Database database;
  private DatabaseModel databaseModel;

  // Application wide properties.
  private Properties properties;

  /****************************************************************************/
  /**
   * Construct a constraint checking object.
   */

  public ConstraintChecker()
  {
    // Set up for database access.
    database = Editor.getInstance().getDatabase();
    databaseModel = database.getDatabaseModel();

    // Get the system wide properties to access error messages.
    properties = Editor.getInstance().getProperties();
  }

  /****************************************************************************/
  /**
   * Check that a constraint is satisified.
   */

  public void check( Object keyColumnValue, String typeString, 
                     ColumnEntry columnEntry )
    throws ConstraintViolationException
  {
    // Map the string to an int and case on the result.
    int type = convert( typeString );
    switch ( type )
    {
    case AOSTR:
      checkAoStrConstraint( keyColumnValue, columnEntry );
      break;

    case BOUNDSF:
      checkDecimalBoundsConstraint( keyColumnValue, columnEntry );
      break;

    case BOUNDSI:
      checkIntegerBoundsConstraint( keyColumnValue, columnEntry );
      break;

    case GRATING:
      checkGratingConstraint( keyColumnValue, columnEntry );
      break;

    case OFFSET:
      checkOffsetConstraint( keyColumnValue, columnEntry );
      break;

    case PHOTO:
      checkPhotometryFlagConstraint( keyColumnValue, columnEntry );
      break;

    case POSITION:
      checkPositionConstraint( keyColumnValue, columnEntry );
      break;

    case PROPINFO:
      checkPropInfoConstraint( keyColumnValue, columnEntry );

      // This is a hack to workaround an issue in
      // NewView.processApply().  processApply only checks the
      // constraints for a single table, thus it will miss this check
      // until procssApply() determines the set of tables it must
      // process.
      checkAoStrConstraint( keyColumnValue, columnEntry );
      break;

    case ROLL:
      checkRollConstraint( keyColumnValue, columnEntry );
      break;

    case SEGMENT:
      checkSegmentConstraint( keyColumnValue, columnEntry );
      break;

    case TIME:
      checkTimeConstraint( keyColumnValue, columnEntry );
      break;

    case VMAG:
      checkVisualMagnitudeConstraint( keyColumnValue, columnEntry );
      break;

    case COI:
      checkCoIConstraint( keyColumnValue, columnEntry );
      break;

    case EVENTFILTER:
      checkEventFilterConstraint( keyColumnValue, columnEntry );
      break;

    case MONITOR:
      checkMonitorConstraint( keyColumnValue, columnEntry );
      break;


    default:
      LogClient.logMessage( "Constraint.process(): Internal error." );
      break;
    }
  }

  /****************************************************************************/
  /**
   * Check that the the value of ao_str is one of the valid choices.
   */

  private void checkAoStrConstraint( Object keyColumnValue, 
				     ColumnEntry columnEntry )
    throws ConstraintViolationException
  {
    // Get the current relevant values from the data model.
    Object ocatPropID =
      getValue( keyColumnValue, "target", "ocat_propid", "obsid" );
    Object aoStr =
      getValue( ocatPropID, "prop_info", "ao_str", "ocat_propid" );

    // Get the current value of this component.
    // Validate that ao_str is a valid choice.
    boolean aoStrConstraintViolation = true;
    if ( aoStr != null &&
	 aoStr instanceof String )
    {
      try
      {
	// Check that the value matches the set valid choices.
	DatabaseKey aoStrDatabaseKey =
	  new DatabaseKey( "prop_info", "ao_str" );
	DatabaseConfiguration databaseConfiguration =
	  DatabaseConfiguration.getInstance();
	ColumnEntry aoStrColumnEntry =
	  databaseConfiguration.getColumnEntry( aoStrDatabaseKey );
	Vector choices = aoStrColumnEntry.getChoices();
	aoStrConstraintViolation = choices.indexOf( aoStr ) == -1;
      }
      catch ( ConfigurationException exception )
      {
	// Handle an unexpected exception.
	LogClient.printStackTrace( exception );
      }
    }
    else if ( ocatPropID == null || ocatPropID instanceof Integer )
    {
      // In this case, the DB code will force ao_str to be valid to
      // satisfy the stored procedure.
      aoStrConstraintViolation = false;
    }
    else
    {
      aoStrConstraintViolation = true;
    }
    if ( aoStrConstraintViolation )
    {
      String message =
	properties.getProperty( "propinfo.constraint.aostr.message" );
      throw new ConstraintViolationException( message, null );
    }
  }

  /****************************************************************************/
  /**
   * Check that the decimal value of the invoking component falls in a
   * range bounded by specified limits.
   */

  private void checkDecimalBoundsConstraint( Object keyColumnValue, 
                                             ColumnEntry columnEntry )
    throws ConstraintViolationException
  {
    // Get the current value of this component.
    Double doubleObject = 
      (Double) databaseModel.getValueAt( keyColumnValue, columnEntry );

    if ( doubleObject != null )
    {
      double doubleValue = doubleObject.doubleValue();

      // Fetch the bounds and do the check.
      double lowerBound = columnEntry.getMinimum().doubleValue();
      double upperBound = columnEntry.getMaximum().doubleValue();
      
      if ( ( doubleValue < lowerBound ) || ( doubleValue > upperBound ) )
      {
        // Out of range.  Generate an exception.
        String message = ( "Out of Range error.  " +
                           "Parameter: " + columnEntry.getName() +
                           "  Value: " + doubleValue + 
                           ", lower: " + lowerBound +
                           ", upper: " + upperBound );

        DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                   columnEntry.getName() );

        throw new ConstraintViolationException( message, databaseKey );
      }
    }
  }  

  /****************************************************************************/
  /**
   * Check that the invoking component as well as any participating
   * components satisfy a grating constraint.
   */

  private void checkGratingConstraint( Object keyColumnValue, 
                                       ColumnEntry columnEntry  )
    throws ConstraintViolationException
  {
    // Get the current values for grating and first order count rate.
    String grating =
      (String) getValue( keyColumnValue, "target", "grating", "obsid" );

    Double firstOrderCountRate =
      (Double) getValue( keyColumnValue, "target", "forder_cnt_rate", "obsid" );

    if (grating != null) {
      if ( ( grating.equals( "NONE") && firstOrderCountRate != null ) ||
         (! grating.equals( "NONE" ) && firstOrderCountRate == null ) )
      {
      String message = ( "<html>Grating Error.  " + 
                         "<i>Grating</i> and <i>1st order rate</i> " + 
                         "must be set as a pair." );
      
      DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                 columnEntry.getName() );

      throw new ConstraintViolationException( message, databaseKey );
      }
    }
  }

  /****************************************************************************/
  /**
   * Check that the decimal value of the invoking component falls in a
   * range bounded by specified limits.
   */

  private void checkIntegerBoundsConstraint( Object keyColumnValue, 
                                             ColumnEntry columnEntry )
    throws ConstraintViolationException
  {
    // Get the current value of this component.
    Integer integerObject = 
      (Integer) databaseModel.getValueAt( keyColumnValue, columnEntry );

    if ( integerObject != null )
    {
      int integerValue = integerObject.intValue();

      // Fetch the bounds and do the check.
      int lowerBound = columnEntry.getMinimum().intValue();
      int upperBound = columnEntry.getMaximum().intValue();
      
      if ( ( integerValue < lowerBound ) || ( integerValue > upperBound ) )
      {
        // Out of range.  Generate an exception.
        String message = ( "Integer bounds check error.  " +
                           "Parameter: " + columnEntry.getName() +
                           "  Value: " + integerValue + 
                           ", lower: " + lowerBound +
                           ", upper: " + upperBound );

        DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                   columnEntry.getName() );

        throw new ConstraintViolationException( message, databaseKey );
      }
    }
  }  

  /****************************************************************************/
  /**
   * Handle a detector offset constraint check. Detects violations of
   * the constraints: If either Y Offset is NULL or Z Offset is NULL
   * then both must be NULL; if Y Offset is non-NULL or Z Offset is
   * non-NULL then both must be non-NULL.  In the cases where the y
   * offset or z offset have non-null values (satisfying the previous
   * sentence) these values must be between a range of minimum and
   * maximum values determined by the observation type and the
   * instrument type.
   */

  public void checkOffsetConstraint( Object keyColumnValue, 
                                     ColumnEntry columnEntry )
    throws ConstraintViolationException
  {
    // Get the current values for the parameters.
    Double yOffset = 
      (Double) getValue( keyColumnValue, "target", "y_det_offset", "obsid" );
    Double zOffset =
      (Double) getValue( keyColumnValue, "target", "z_det_offset", "obsid" );
    String instrumentString =
      (String) getValue( keyColumnValue, "target", "instrument", "obsid" );
    String typeString =
      (String) getValue( keyColumnValue, "target", "type", "obsid" );

    double maximum;
    double minimum;

    // Initialize the constraints min and max values.
   if (typeString != null && instrumentString != null) {
    if ( typeString.equals( "CAL" ) )
    {
      // Set the min/max values for a calibration observation.
      minimum = -120.0;
      maximum = 120.0;
    }
    else
    {
      // Set the min/max values for a non-calibration observation.
      if ( instrumentString.equals( "ACIS-I" ) )
      {
        minimum = -10.0;
        maximum = 10.0;
      }
      else if ( instrumentString.equals( "ACIS-S" ) )
      {
        minimum = -30.0;
        maximum = 30.0;
      }
      else if ( instrumentString.equals( "HRC-I" ) )
      {
        minimum = -20.0;
        maximum = 20.0;
      }
      else if ( instrumentString.equals( "HRC-S" ) )
      {
        minimum = -50.0;
        maximum = 50.0;
      }
      else
      {
	// Provide fail-safe values for min and max.
	minimum = -10;
	maximum = 10;
      }
    }

    // Now determine if the constraints are satisfied.

    // Detect a y, z offset specification.
    if ( (yOffset == null && zOffset != null) ||
         (zOffset == null && yOffset != null) ) {
      String message = ( "Detector Offset Error." ); 
      if ( yOffset == null ) {
         message += ( "  Y Offset must be entered. " );
      }
      if ( zOffset == null ) {
         message += ( "  Z Offset must be entered. " );
      }
      DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                 columnEntry.getName() );
      throw new ConstraintViolationException( message, databaseKey );
    }
    else 
    {
      double yOffsetValue = 0.0;
      double zOffsetValue = 0.0;

      // Detect a y, z offset constraint violation.
      if (yOffset != null)
        yOffsetValue = yOffset.doubleValue();
       
      if (zOffset != null)
        zOffsetValue = zOffset.doubleValue();

      if ( (yOffsetValue > maximum) || (yOffsetValue < minimum) ||
           (zOffsetValue > maximum) || (zOffsetValue < minimum) )
      {
        String message =
          "Detector Offset Error: Y Offset and Z Offset must be " + "between " +
          minimum + " and " + maximum + ".";

        DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                   columnEntry.getName() );

        throw new ConstraintViolationException( message, databaseKey );
      }
    }

   }
  }

  /****************************************************************************/
  /**
   * Detect violations of the photometry flag constraint.  Enforces:
   * "Photometry flag must be N for SS/MT".
   */

  public void checkPhotometryFlagConstraint( Object keyColumnValue, 
                                             ColumnEntry columnEntry  )
    throws ConstraintViolationException
  {
    String message = null;

    // Fetch the photometry flag and solar system object values.
    String photometryFlag =
      (String) getValue( keyColumnValue, "target", "photometry_flag", "obsid" );
    String object =
      (String) getValue( keyColumnValue, "target", "object", "obsid" );

    // Check the constaint.
    if ( photometryFlag != null && photometryFlag.equals( "Y" ) &&
	 ( object == null || !object.equals( "NONE" ) ) )
    {
      // The constraint was not satisfied.  Throw an exception.
      message =
	properties.getProperty( "photometryflag.constraint.error.message" );
      DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                 columnEntry.getName() );
      throw new ConstraintViolationException( message, databaseKey );
    }
  }

  /****************************************************************************/
  /**
   * Enforce the position constaint.  Detect violations of the
   * constraint that ra and dec are optional when a SS/MT object is
   * defined or type equals TOO, otherwise they are required.
   */

  public void checkPositionConstraint( Object keyColumnValue, 
                                       ColumnEntry columnEntry  )
    throws ConstraintViolationException
  {
    // Set up the parameters: Solar System Object, RA, and DEC.
    String objectString =
      (String) getValue( keyColumnValue, "target", "object", "obsid" );
    Double ra = (Double) getValue( keyColumnValue, "target", "ra", "obsid" );
    Double dec = (Double) getValue( keyColumnValue, "target", "dec", "obsid" );
    String type = (String) getValue( keyColumnValue, "target", "type", "obsid" );

    // Guarantee that the solar system object is non-null.
    if ( objectString == null )
    {
      objectString = "NONE";
    }

    // Guarantee that the type field is non-null.
    if ( type == null )
    {
      type = Constants.NONE;
    }

    if ( ( objectString.equals( "NONE" ) ) && ( ! type.equals("TOO") ) )
    {
      // The solar system object is empty or type is not TOO.

      // Verify that both RA and DEC are specified.
      if ( ( ra == null ) || ( dec == null ) )
      {
	String message = properties.getProperty( "position.constraint.required" );

        DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                   columnEntry.getName() );

	throw new ConstraintViolationException( message, databaseKey );
      }
    }
    else
    {
      // SS/MT object has been specified or type is TOO.

      // Verify that RA and DEC are specified as a pair.
      if ( ( ra == null ) != ( dec == null ) )
      {
	// All three are null.  Complain.
	String message = properties.getProperty( "position.constraint.pair" );

        DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                   columnEntry.getName() );
        
	throw new ConstraintViolationException( message, databaseKey );
      }
    }
  }

  /****************************************************************************/
  /**
   * Enforce the prop_info constraint.  Detect violations of the
   * constraint that if ocat_propid is "NEW" proposal_id must be
   * "NONE".
   */

  public void checkPropInfoConstraint( Object keyColumnValue, 
                                       ColumnEntry columnEntry  )
    throws ConstraintViolationException
  {
    // Set up the parameters: proposal_id and ocat_propid.
    Object proposalID =
      getValue( keyColumnValue, "target", "proposal_id", "obsid" );
    Object ocatPropID =
      getValue( keyColumnValue, "target", "ocat_propid", "obsid" );

    // Determine if ocat_propid is "NEW"
    if ( ocatPropID instanceof String &&
	 Constants.NEW.equals( (String) ocatPropID ) )
    {
      // It is.  Validate that proposal_id is "NONE"
      if ( !(proposalID == null || 
	     (proposalID instanceof String &&
	      Constants.NONE.equals( (String) proposalID ))) )
      {
	//String message =
	    //properties.getProperty( "propinfo.constraint.none.message" );
	//throw new ConstraintViolationException( message, null );
      }
    }
  }

  /****************************************************************************/
  /**
   * Enforces: "If roll angle is NULL, roll tolerance must also be
   * NULL and if roll angle is non-NULL or tolerance is non-NULL they
   * must both be non-NULL and both must be between 0 and 360 degrees.
   */

  public void checkRollConstraint( Object keyColumnValue, 
                                   ColumnEntry columnEntry  )
    throws ConstraintViolationException
  {
    // Set up the parameters: 
    Double angle = 
      (Double) getValue( keyColumnValue, "rollreq", "roll", "rollreq_id" );
    Double tolerance =
      (Double) getValue( keyColumnValue, "rollreq", "roll_tolerance", "rollreq_id" );

    if ( ( angle == null ) && ( tolerance != null ) )
    {
      String message =
        "Roll Error.  If roll angle is NULL, tolerance must also be NULL.";
      
      DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                 columnEntry.getName() );
      
      throw new ConstraintViolationException( message, databaseKey );
    }
    else if ( ( angle != null ) && ( tolerance == null ) )
    {
      String message = "Roll Error.  If roll angle is non-NULL, tolerance " +
        "must also be non-NULL.";
      
      DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                 columnEntry.getName() );
      
      throw new ConstraintViolationException( message, databaseKey );
    }
  }

  /****************************************************************************/
  /**
   * Enforces: "The maximum number of segments is 1.  Valid values are
   * NULL or 1.
   */

  public void checkSegmentConstraint( Object keyColumnValue, 
                                      ColumnEntry columnEntry  )
    throws ConstraintViolationException
  {
    // Set up the parameters: 
    Integer segmentMax = 
      (Integer) getValue( keyColumnValue, "target", "seg_max_num", "obsid" );

    if ( ( segmentMax != null ) && ( segmentMax.intValue() != 1 ) )
    {
      String message = "Max Segment Error.  The maximum number of segments " +
	"must be either NULL or 1.";

      DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                 columnEntry.getName() );

      throw new ConstraintViolationException( message, databaseKey );
    }
  }

  /****************************************************************************/
  /**
   * Handle a time constraint violation check.  Checks for stop times
   * preceding start times.
   */

  public void checkTimeConstraint( Object keyColumnValue, 
                                   ColumnEntry columnEntry )
    throws ConstraintViolationException
  {
    // Set up the parameters: 
    Timestamp startTime =
      (Timestamp) getValue( keyColumnValue, "timereq", "tstart", "timereq_id" );
    Timestamp stopTime =
      (Timestamp) getValue( keyColumnValue, "timereq", "tstop", "timereq_id" );

    // Deal with the case where both are specified.
    if ( startTime != null && stopTime != null )
    {
      if ( !startTime.before( stopTime ) )
      {
        String message = "Time Error.  Start Time must be before Stop Time.";

        DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                   columnEntry.getName() );

        throw new ConstraintViolationException( message, databaseKey );
      }
    }
    // Detect incomplete specifications.
    else if ( startTime != null && stopTime == null )
    {
      String message = "Time Error.  If Start Time is non-NULL, Stop Time must " +
        "also be non-NULL.";

      DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                 columnEntry.getName() );

      throw new ConstraintViolationException( message, databaseKey );
    }
    else if ( startTime == null && stopTime != null )
    {
      String message = "Time Error.  If Start Time is NULL, Stop Time " +
        "must also be NULL.";

      DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                 columnEntry.getName() );

      throw new ConstraintViolationException( message, databaseKey );
    }
  }

  /****************************************************************************/
  /**
   * Detect violations of the visual magnitude constraint.  Enforces:
   * "If photometryFlag is Y, visualMagnitude must be set."
   */

  public void checkVisualMagnitudeConstraint( Object keyColumnValue, 
                                              ColumnEntry columnEntry  )
    throws ConstraintViolationException
  {
    String photometryFlag =
      (String) getValue( keyColumnValue, "target", "photometry_flag", "obsid" );

    Double visualMagnitude =
      (Double) getValue( keyColumnValue, "target", "vmagnitude", "obsid" );

    if ( ( photometryFlag != null && photometryFlag.equals( "Y" ) ) && ( visualMagnitude == null ) )
    {
      String message =
	properties.getProperty( "visual-magnitude.constraint.error.message" );

      DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                 columnEntry.getName() );

      throw new ConstraintViolationException( message, databaseKey );
    }
  }

  /****************************************************************************/
  /**
   * Detect violations of the CoI propinfo constraint.  Enforces:
   * "If CoI Contact is Y, CoI id and phone number must be set
   * "If CoI Contact is n, CoI id and phone number must be null
   */

  public void checkCoIConstraint( Object keyColumnValue, 
                         ColumnEntry columnEntry  )
    throws ConstraintViolationException
  {
    String coiFlag =
      (String) getValue( keyColumnValue, "prop_info", "coi_contact", "ocat_propid" );

    Integer coiID =
      (Integer) getValue( keyColumnValue, "prop_info", "coin_id", "ocat_propid" );

    String coiPhone =
      (String) getValue( keyColumnValue, "prop_info", "coi_phone", "ocat_propid" );

    if ( ( coiFlag != null && coiFlag.equals( "YES" ) ) && ( coiPhone == null || coiID == null) )
    {
      String message = "Co-I Id and Co-I Phone must be entered if Co-I Contact is YES.";

      DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                 columnEntry.getName() );

      throw new ConstraintViolationException( message, databaseKey );
    }
    if ( ( coiFlag == null || coiFlag.equals( "NO" ) || coiFlag.equals("N")) && 
	( coiPhone != null || coiID != null) )
    {
      String message = "Co-I Id and Co-I Phone must be null if CoI Contact is NO.";

      DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                 columnEntry.getName() );

      throw new ConstraintViolationException( message, databaseKey );
    }
  }

  /****************************************************************************/
  /**
   * Detect violations of the event (energy ) filter constraint.  Enforces:
   * "If acisparam.eventfilter is Y, eventfilter_lower,eventfilter_higher is
   * required"
   */

  public void checkEventFilterConstraint( Object keyColumnValue, 
                                              ColumnEntry columnEntry  )
    throws ConstraintViolationException
  {
    String eventFilterFlag =
      (String) getValue( keyColumnValue, "acisparam", "eventfilter", "acisid" );

    Double eventFilterLower =
      (Double) getValue( keyColumnValue, "acisparam", "eventfilter_lower", "acisid" );
    Double eventFilterHigher =
      (Double) getValue( keyColumnValue, "acisparam", "eventfilter_higher", "acisid" );

    if ( ( eventFilterFlag != null && eventFilterFlag.equalsIgnoreCase("YES")) && 
         ( eventFilterLower == null  || eventFilterHigher == null) )
    {
      String message = "Event Filter Lower and Range must be entered if Event Filter is set to YES";

      DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                 columnEntry.getName() );
      throw new ConstraintViolationException( message, databaseKey );
    }
    if ( ( eventFilterFlag == null || eventFilterFlag.equalsIgnoreCase("NO") ||
	   eventFilterFlag.equalsIgnoreCase("N") || 
	   eventFilterFlag.equalsIgnoreCase("NULL") || 
	   eventFilterFlag.equals("")) && 
           ( eventFilterLower != null  || eventFilterHigher != null) )
    {
      String message = "Event Filter Lower and Range must be null if Event Filter is set to NO";
      DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                 columnEntry.getName() );
      throw new ConstraintViolationException( message, databaseKey );
    }

    
  }

  /****************************************************************************/
  /**
   * Enforces: If pre_id is non-NULL , monitor_flag must be set
   */

  public void checkMonitorConstraint( Object keyColumnValue, 
                                   ColumnEntry columnEntry  )
    throws ConstraintViolationException
  {
    // Set up the parameters: 
    String monitorFlag = 
      (String) getValue( keyColumnValue, "target", "monitor_flag", "obsid" );
    Integer pre_id =
      (Integer) getValue( keyColumnValue, "target", "pre_id", "obsid" );

    //LogClient.logMessage( "Constraint.process(): monitor=" + monitorFlag  + " "  + pre_id + "\n");
    //LogClient.logMessage( "Constraint.process(): pre_id=" + pre_id  + " "  + pre_id + "\n");

    if ( ( pre_id != null  && pre_id.intValue() > 0) && 
           ( monitorFlag.equalsIgnoreCase("NULL") || monitorFlag.equalsIgnoreCase("NO") ) )
    {
      String message = "Preceding Error.  If pre_id is set, monitor flag must also be set." ;
      DatabaseKey databaseKey = new DatabaseKey( columnEntry.getTableName(),
                                                 columnEntry.getName() );
      throw new ConstraintViolationException( message, databaseKey );
    }
  }


  /****************************************************************************/
  /**
   * Convert a string to a more manageable code.
   */

  private int convert( String type )
  {
    int result = NONE;

    if ( type.equalsIgnoreCase( "BOUNDSI" ) )
    {
      result = BOUNDSI;
    }
    else if ( type.equalsIgnoreCase( "BOUNDSF" ) )
    {
      result = BOUNDSF;
    }
    else if ( type.equalsIgnoreCase( "PHOTO" ) )
    {
      result = PHOTO;
    }
    else if ( type.equalsIgnoreCase( "PROPINFO" ) )
    {
      result = PROPINFO;
    }
    else if ( type.equalsIgnoreCase( "AOSTR" ) )
    {
      result = AOSTR;
    }
    else if ( type.equalsIgnoreCase( "POSITION" ) )
    {
      result = POSITION;
    }
    else if ( type.equalsIgnoreCase( "VMAG" ) )
    {
      result = VMAG;
    }
    else if ( type.equalsIgnoreCase( "OFFSET" ) )
    {
      result = OFFSET;
    }
    else if ( type.equalsIgnoreCase( "GRATING" ) )
    {
      result = GRATING;
    }
    else if ( type.equalsIgnoreCase( "ROLL" ) )
    {
      result = ROLL;
    }
    else if ( type.equalsIgnoreCase( "TIME" ) )
    {
      result = TIME;
    }
    else if ( type.equalsIgnoreCase( "SEGMENT" ) )
    {
      result = SEGMENT;
    }
    else if ( type.equalsIgnoreCase( "COI" ) )
    {
      result = COI;
    }
    else if ( type.equalsIgnoreCase( "EVENTFILTER" ) )
    {
      result = EVENTFILTER;
    }
    else if ( type.equalsIgnoreCase( "MONITOR" ) )
    {
      result = MONITOR;
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Return the value associated with an argument.
   *
   * @param keyColumnValue  The database model row index.
   * @param argumentIndex  The argument number.  An index into a
   * vector of arguments contained in this constraint object.
   *
   * @author Paul Michael Reilly
   *
   * @version %I%, %G%
   */

  private Object getValue( Object keyColumnValue, String tableName,
			   String columnName, String keyColumnName )
  {
    Object result = null;

    int columnIndex = database.getColumnIndex( tableName, columnName );

    // Return the value from the database model.
    result = databaseModel.getValueAt( tableName, keyColumnValue, columnIndex );

    return result;
   }

  private Object setValue( Object value, Object keyColumnValue, 
			   ColumnEntry columnEntry )
  {
    Object result = null;


    // Return the value from the database model.
    try {
      databaseModel.setValueAt( value, keyColumnValue, columnEntry );
    }
    catch ( RecordDoesNotExistException exception )
      {
	// Handle an unexpected exception.
	LogClient.printStackTrace( exception );
      }

    return result;
   }

  /****************************************************************************/

}

/******************************************************************************/
