/*
  Copyrights:
 
  Copyright (c) 1998 Smithsonian Astrophysical Observatory
 
  Permission to use, copy, modify, distribute, and sell this software
  and its documentation for any purpose is hereby granted without fee,
  provided that the above copyright notice appear in all copies and
  that both that copyright notice and this permission notice appear in
  supporting docu- mentation, and that the name of the Smithsonian
  Astro- physical Observatory not be used in advertising or publicity
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

package gov.sao.asc.obsed.database;

/******************************************************************************/
import gov.sao.asc.obsed.Constants;
import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.view.ConfigurationException;
import gov.sao.asc.util.LogClient;
import gov.sao.asc.util.Pair;
import java.sql.Driver;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Types;
import java.sql.SQLWarning;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import javax.swing.JOptionPane;

/******************************************************************************/

public class Database extends Object
{
  /**
   * The field offset for the obsid value in a locked table record.
   */

  private final int OBSID = 1;

  private Connection connection;
  private Hashtable<String, ResultSet> resultSetCache;
  private String dbName;
  private String description;
  private PreparedStatement formsPreparedStatement;
  private CallableStatement getTypeStatement;
  private DatabaseModel model;
  private char[] password;
  private Hashtable<String,Properties> permissionsHashtable;
  private String server;
  private String interfacesFilename;
  private Hashtable<String,Integer> typesCache;
  private Hashtable<String,Integer> typeMap;
  private String user;
 
  private Properties sqlUser;

  /****************************************************************************/
  /**
   * Default constructor.
   */

  public Database()
  {
    // Create the data model;
    model = new DatabaseModel( this );

    // Load and use the sybase driver.
    String driver = "com.sybase.jdbc4.jdbc.SybDriver";
    try
    {
      Class.forName( driver );
    }
    catch ( ClassNotFoundException cnfe )
    {
      LogClient.printStackTrace( cnfe );
    }

    // Set some default values.
    server = "sqlsao";
    interfacesFilename = "/soft/SYBASE15.7/interfaces";
    description = "Production server.";
    dbName = "axafocat";
    user = "eduser";
    password = null;

    sqlUser = new Properties();


    resultSetCache = new Hashtable<String,ResultSet>();

    // Build the SQL Type map
    typeMap = new Hashtable<String,Integer>();
    typeMap.put( "int", new Integer( Types.INTEGER ) );
    typeMap.put( "intn", new Integer( Types.INTEGER ) );
    typeMap.put( "char", new Integer( Types.CHAR ) );
    typeMap.put( "nchar", new Integer( Types.CHAR ) );
    typeMap.put( "varchar", new Integer( Types.VARCHAR ) );
    typeMap.put( "float", new Integer( Types.FLOAT ) );
    typeMap.put( "floatn", new Integer( Types.FLOAT ) );
    typeMap.put( "text", new Integer( Types.LONGVARCHAR ) );
    typeMap.put( "datetimn", new Integer( Types.DATE ) );
    typeMap.put( "datetime", new Integer( Types.DATE ) );
    typeMap.put( "unknown", new Integer( Types.NULL ) );
  }
 
  /****************************************************************************/
  /**
   * Close the current connection.
   */

  public void close()
  {

    // Close the current connection if there is one.
    if ( connection != null )
    {
      try
      {
	connection.close();
      }
      catch (SQLException e)
      {
	LogClient.printStackTrace( e );
      }
    }
  }

  /****************************************************************************/
  /**
   * Return true iff the permission for <i>columnName</i> is <i>value</i>.
   *
   * @param columnName
   * @param value
   */

  private boolean checkPermission( String prop, String value )
  {
    boolean result;
    result = (prop != null) && (prop.equals( value ));
	if ( Editor.getInstance().isDebug() )
	{
	  System.out.println( "permissions => " + prop +
			      "; value: " + value );
	}
    return result;
  }

  /****************************************************************************/
  /**
   * Connect to the configured database.
   */

  public void connect()
    throws CouldNotConnectException
  {
    String url = null;

    // Open a connection to the database.
    try
    {
      String pw = new String( password );
      sqlUser.setProperty("user",user);
      sqlUser.setProperty("password",pw);
      sqlUser.setProperty("database",dbName);

      // Initialize the database URL.
      url = "jdbc:sybase:jndi:file://" + interfacesFilename  + "?" + server;
      DriverManager.setLoginTimeout(20);
      DriverManager.registerDriver((Driver)Class.forName( "com.sybase.jdbc4.jdbc.SybDriver" ).newInstance());
      connection = DriverManager.getConnection( url, sqlUser );
    }
    catch ( Exception se )
    {
      LogClient.printStackTrace( se );
      throw new CouldNotConnectException( "Connection error.", url, user );
    }
  }

  /****************************************************************************/
  /**
   * Create a new acisparam record and return the record index.  If the
   * values used to generate the new record match those of an existing
   * record, that record is returned instead of creating a new record.
   *
   * @param newKeyValue The key value used to access the data selected
   * for the (potentially) new record.
   *
   * @returns A vector containing s single NewRecordResult object.
   */

  private Vector<NewRecordResult> createNewACISParamRecord( Object newKeyValue )
    throws RecordInsertionException
  {
    return createNewACISParamRecord( newKeyValue, false );
  }

  /****************************************************************************/
  /**
   * Create a new acisparam record and return the record index.  If the
   * values used to generate the new record match those of an existing
   * record, that record is returned instead of creating a new record.
   *
   * @param newKeyValue The key value used to access the data selected
   * for the (potentially) new record.
   * 
   * @param force If "1" then unconditionally create a new acisparam
   * record, otherwise 
   *
   * @returns A vector containing s single NewRecordResult object.
   */

  private Vector<NewRecordResult> createNewACISParamRecord( Object newKeyValue, boolean force )
    throws RecordInsertionException
  {
    String message = null;
    Vector<NewRecordResult> results = null;
    String tableName = "acisparam";
    Object value = Integer.valueOf( "0" );

    // Set up the force value.
    Object forceValue =
      force ? Integer.valueOf( "1" ) : Integer.valueOf( "0" );

    try
    {
      // Set up the parameters for the <i>acisparam_ingest</i> stored
      // procedure call.
      Parameter insertionParams = new Parameter( tableName, newKeyValue );
      insertionParams.add( "returnCode", Parameter.OUT, Types.INTEGER, value );
      insertionParams.add( "exp_mode", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "ccdi0_on", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "ccdi1_on", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "ccdi2_on", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "ccdi3_on", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "ccds0_on", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "ccds1_on", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "ccds2_on", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "ccds3_on", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "ccds4_on", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "ccds5_on", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "bep_pack", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "onchip_sum", Parameter.IN, Types.CHAR );
      insertionParams.add( "onchip_row_count", Parameter.IN, Types.INTEGER );
      insertionParams.add( "onchip_column_count", Parameter.IN, Types.INTEGER );
      insertionParams.add( "frame_time", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "subarray", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "subarray_start_row", Parameter.IN, Types.INTEGER );
      insertionParams.add( "subarray_row_count", Parameter.IN, Types.INTEGER );
      insertionParams.add( "duty_cycle", Parameter.IN, Types.CHAR );
      insertionParams.add( "secondary_exp_count", Parameter.IN, Types.INTEGER );
      insertionParams.add( "primary_exp_time", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "secondary_exp_time", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "eventfilter", Parameter.IN, Types.CHAR );
      insertionParams.add( "eventfilter_lower", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "eventfilter_higher", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "most_efficient", Parameter.IN, Types.CHAR );
      insertionParams.add( "multiple_spectral_lines", Parameter.IN, Types.CHAR );
      insertionParams.add( "spectra_max_count", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "force_flag", Parameter.IN, Types.INTEGER, forceValue );
      insertionParams.add( "acisid", Parameter.OUT, Types.INTEGER, null );

      // Insert the table into the database.
      String storedProc = "acisparam_ingest";
      Vector resultSetValues =
	executeStoredProcedure( storedProc, insertionParams );

      // Test the resultCode for a sensible value.
      Integer returnCode = (Integer) insertionParams.get( 0 );
      if ( returnCode == null )
      {
	// Internal (JDBC or SQL) error.  Synthesize an exception.
	message = "Invalid (null) return code returned from the " +
	  storedProc + " stored procedure.";
	throw new RecordInsertionException( message, tableName, -1 );
      }
      else
      {
	NewRecordResult newRecord;
	Object keyValue;

	// Fetch the return code.
	int code = returnCode.intValue();

	// Provide debugging help.
	if ( Editor.getInstance().isDebug() )
	{
	  System.out.println( "acisparam_ingest => " + code +
			      "; resultSet: " + resultSetValues );
	}

	// Case on the returnCode.
	switch ( code )
	{
	case 0:
	  // A new record was created.  
	  // Create a NewRecordResult object using the acisid
	  // value and use that value as the result.
	  Vector acisidVector = (Vector) resultSetValues.elementAt( 0 );
	  keyValue = acisidVector.elementAt( 0 );
	  newRecord = 
	    new NewRecordResult( tableName, "acisid",
                                 keyValue, newKeyValue, code );
	  results = new Vector<NewRecordResult>();
	  results.add( newRecord );

	  // Deal with the obsid value ...  deferred.  For now this
	  // must be done by the User.  The sticking point is how to
	  // pass in an obsid value.  We really, really need to invent
	  // GUI elements that are not backed by the database but are
	  // part of the data model.
	  break;

	case -108:
	  // One or more potentially matching records were found.  Get
	  // the acisid value from the result set. 
	  Object matchKeyValue = null;
	  keyValue = null;
	  Object testValue = null;
	  Iterator i = resultSetValues.iterator();
	  while ( i != null && i.hasNext() )
	  {
	    // Check this acisid for a match.
	    Vector v = (Vector) i.next();
	    if ( v != null )
	    {
	      // Test the current record for a match.
	      keyValue = v.elementAt( 0 );
	      matchKeyValue = keyValue;
	      break;
	    }
	  }

	  // Test for a match.
	  if ( matchKeyValue != null )
	  {
	    // There is a match.  Use this acisid value as the result.
	    newRecord = 
	      new NewRecordResult( tableName, "acisid",
			matchKeyValue, newKeyValue, code );
	    results = new Vector<NewRecordResult>();
	    results.add( newRecord );
	  }
	  break;

	default:
	  // A fatal or standard Sybase error occurred.  Raise a
	  // RecordInsertionException.
	  message = "Unexpected stored procedure failure.";
	  throw new RecordInsertionException( message, tableName, code );
	}
      }
    }
    catch ( SQLException sqle )
    {
      if (message != null)  {
        LogClient.logMessage( message );
      }
      if ( Editor.getInstance().isDebug() ) {
        LogClient.printStackTrace( sqle );
      }
      throw new RecordInsertionException( sqle.getMessage(), tableName, -2 );
    }

    return results;
  }

  /****************************************************************************/
  /**
   * Create a new dither table record.
   *
   * @param newKeyValue The record ID used to access the table data
   * model values.
   *
   * @returns A single NewRecordResult object encapsulated in a vector
   * describing the results of the record creation attempt.
   */

  private Vector<NewRecordResult> createNewDitherRecord( Object newKeyValue )
    throws RecordInsertionException
  {
    Vector<NewRecordResult> results;
    String tableName = "dither";
    Object value = new Integer( 0 );

    try
    {
      // Set up the parameters for the <i>dither_ingest</i> stored
      // procedure call.
      Parameter insertionParams = new Parameter( tableName, newKeyValue );
      insertionParams.add( "returnCode", Parameter.OUT, Types.INTEGER, value );
      insertionParams.add( "obsid", Parameter.IN, Types.INTEGER );
      insertionParams.add( "y_amp", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "y_freq", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "y_phase", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "z_amp", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "z_freq", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "z_phase", Parameter.IN, Types.DOUBLE );

      // Save the record ID that we are trying to create.
      Object keyValue = insertionParams.get( 1 );

      // Insert the table into the database and process the output to
      // catch any errors.
      executeStoredProcedure( "dither_ingest", insertionParams );
      results = getNewRecordOutput( tableName, "obsid",
		insertionParams, newKeyValue );

      // If the results are empty, then a record was created using the
      // value of the obsid field.
      if ( results.size() == 0 )
      {
	results = new Vector<NewRecordResult>();
	NewRecordResult result = 
	  new NewRecordResult( tableName, "obsid",keyValue, newKeyValue, 0 );
	results.add( result );
      }
    }
    catch ( SQLException sqle )
    {
      String message = sqle.getMessage();
      LogClient.logMessage( message );
      if ( Editor.getInstance().isDebug() ) {
        LogClient.printStackTrace( sqle );
      }
      throw new RecordInsertionException( message, tableName, -9999 );
    }

    return results;
  }

  /****************************************************************************/
  /**
   * Create a new TOO record and return the record index.  If the
   * values used to generate the new record match those of an existing
   * record, that record id is returned instead of creating a new
   * record.
   */

  private Vector<NewRecordResult> createNewTOOParamRecord( Object newKeyValue )
    throws RecordInsertionException
  {
    Vector<NewRecordResult>  results= null;
    String tableName = "too";
    Object value;
    String message = null;

    try
    {
      // Set up the parameters for the <i>ocattoo_ingest</i> stored
      // procedure call.
      Parameter insertionParams = new Parameter( "too", newKeyValue );
      insertionParams.add( "returnCode", Parameter.OUT, Types.INTEGER, null );
      insertionParams.add( "type", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "start", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "stop", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "followup", Parameter.IN, Types.INTEGER );
      insertionParams.add( "alt_group_name", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "alt_approved_cnt", Parameter.IN, Types.INTEGER );
      insertionParams.add( "result", Parameter.OUT, Types.INTEGER, null );

      // Insert the table into the database.
      String storedProc = "ocattoo_ingest";
      Vector resultSetValues =
	executeStoredProcedure( storedProc, insertionParams );

      // Test the resultCode for a sensible value.
      Object keyValue;
      keyValue = insertionParams.get( 7 );
      Integer returnCode = (Integer) insertionParams.get( 0 );
      if ( returnCode == null )
      {
	// Internal (JDBC or SQL) error.  Synthesize an exception.
	message = "Invalid (null) return code returned from the " +
	  storedProc + " stored procedure.";
	throw new RecordInsertionException( message, tableName, -1 );
      }
      else
      {
	NewRecordResult newRecord;

	// Fetch the return code.
	int code = returnCode.intValue();

	// Provide debugging help.
	if ( Editor.getInstance().isDebug() )
	{
	  System.out.println( "ocattoo_Ingest => " + code +
			      "; resultSet: " + resultSetValues );
	}

	// Case on the returnCode.
        int columnIndex = getColumnIndex( tableName, "trig" );
	Object trigValue =
	    (String) model.getValueAt( tableName, newKeyValue, columnIndex );
        columnIndex = getColumnIndex( tableName, "remarks" );
	Object remarksValue =
	    (String) model.getValueAt( tableName, newKeyValue, columnIndex );
	switch ( code )
	{
	case 0:
	  // A new record was created.  Deal with the trigger,remarks
	  // field.  Create a NewRecordResult object using the tooid
	  // value and use that value as the result.
	  newRecord = 
	    new NewRecordResult( tableName, "tooid",
                                 keyValue, newKeyValue, code );
	  results = new Vector<NewRecordResult>();
	  results.add( newRecord );

	  // Store the `trig' and `remarks' values into the created too
	  // record .

          String sql_tooremarks = "update too set trig = ? ";
          sql_tooremarks += ", remarks= ? ";
          sql_tooremarks += " where tooid = ";
          sql_tooremarks += keyValue;

	  if ( Editor.getInstance().isDebug() )
	  {
            System.out.println("Database: " + sql_tooremarks);
          } 

  
          PreparedStatement statement = 
    	       connection.prepareStatement(sql_tooremarks);
          if (trigValue == null) {
            statement.setNull(1,getType(tableName, "trig") );
          }
          else {
            statement.setObject(1,trigValue);
          }
          if (remarksValue == null) {
            statement.setNull(2,getType(tableName, "remarks") );
          }
          else {
            statement.setObject(2,remarksValue);
          }

          executeUpdate(statement);

	  break;

	case -108:
	  break;

	default:
	  // A fatal or standard Sybase error occurred.  Raise a
	  // RecordInsertionException.
	  message = "Unexpected stored procedure failure.";
	  throw new RecordInsertionException( message, tableName, code );
	}
      }
    }
    catch ( SQLException sqle )
    {
      message = sqle.getMessage();
      LogClient.logMessage( message );
      if ( Editor.getInstance().isDebug() ) {
        LogClient.printStackTrace( sqle );
      }
      throw new RecordInsertionException( message, tableName, -9999 );
    }

    return results;
  }

  /****************************************************************************/
  /**
   * Create a new HRC record and return the record index.  If the
   * values used to generate the new record match those of an existing
   * record, that record id is returned instead of creating a new
   * record.
   */

  private Vector<NewRecordResult> createNewHRCParamRecord( Object newKeyValue )
    throws RecordInsertionException
  {
    Vector<NewRecordResult> results = null;
    String tableName = "hrcparam";
    Object value;

    try
    {
      // Set up the parameters for the <i>hrcparam_ingest</i> stored
      // procedure call.
      Parameter insertionParams = new Parameter( "hrcparam", newKeyValue );
      insertionParams.add( "returnCode", Parameter.OUT, Types.INTEGER, null );
      insertionParams.add( "si_mode",Parameter.IN, Types.VARCHAR );
      insertionParams.add( "trigger_level", Parameter.IN, Types.INTEGER );
      insertionParams.add( "range_switch_level", Parameter.IN, Types.INTEGER );
      insertionParams.add( "spect_mode", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "antico_enable", Parameter.IN, Types.CHAR );
      insertionParams.add( "width_enable", Parameter.IN, Types.CHAR );
      insertionParams.add( "width_threshold", Parameter.IN, Types.INTEGER );
      insertionParams.add( "uld_enable", Parameter.IN, Types.CHAR );
      insertionParams.add( "upper_level_disc", Parameter.IN, Types.INTEGER );
      insertionParams.add( "blank_enable", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "u_blank_hi", Parameter.IN, Types.INTEGER );
      insertionParams.add( "v_blank_hi", Parameter.IN, Types.INTEGER );
      insertionParams.add( "u_blank_lo", Parameter.IN, Types.INTEGER );
      insertionParams.add( "v_blank_lo", Parameter.IN, Types.INTEGER );
      insertionParams.add( "py_shutter_position", Parameter.IN, Types.INTEGER );
      insertionParams.add( "my_shutter_position", Parameter.IN, Types.INTEGER );
      insertionParams.add( "hrc_zero_block", Parameter.IN, Types.CHAR );
      insertionParams.add( "timing_mode", Parameter.IN, Types.CHAR );
      insertionParams.add( "result", Parameter.OUT, Types.INTEGER, null );

      // Insert the table into the database.
      executeStoredProcedure( "hrcparam_ingest", insertionParams );
      results =
	getNewRecordOutput( tableName, "hrcid",insertionParams, newKeyValue );
    }
    catch ( SQLException sqle )
    {
      String message = sqle.getMessage();
      LogClient.logMessage( message );
      if ( Editor.getInstance().isDebug() ) {
        LogClient.printStackTrace( sqle );
      }
      throw new RecordInsertionException( message, tableName, -9999 );
    }

    return results;
  }

  /***************************************************************************/
  /**
   * Create a new ACISWIN record and return the record index.  If the
   * values used to generate the new record match those of an existing
   * record, that record id is returned instead of creating a new
   * record.
   */

  private Vector<NewRecordResult> createNewACISWinRecord( Object newKeyValue )
    throws RecordInsertionException
  {
    Vector<NewRecordResult> results = null;
    String tableName = "aciswin";
    Object value = new Integer(0);

    try
    {
      // Set up the parameters for the <i>aciswin_ingest</i> stored
      // procedure call.
      Parameter insertionParams = new Parameter( "aciswin", newKeyValue );
      insertionParams.add( "returnCode", Parameter.OUT, Types.INTEGER, null) ;
      insertionParams.add( "obsid", Parameter.IN, Types.INTEGER );
      insertionParams.add( "chip", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "include_flag", Parameter.IN, Types.CHAR );
      insertionParams.add( "start_row", Parameter.IN, Types.INTEGER );
      insertionParams.add( "start_column", Parameter.IN, Types.INTEGER );
      insertionParams.add( "width", Parameter.IN, Types.INTEGER );
      insertionParams.add( "height", Parameter.IN, Types.INTEGER );
      insertionParams.add( "lower_threshold", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "pha_range", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "sample", Parameter.IN, Types.INTEGER );
      insertionParams.add( "ordr", Parameter.IN, Types.INTEGER );
      insertionParams.add( "result", Parameter.OUT, Types.INTEGER, null );

      // Insert the table into the database.
      executeStoredProcedure( "aciswin_ingest", insertionParams );
      results =
        getNewRecordOutput( tableName, "aciswin_id", insertionParams, newKeyValue );
    }
    catch ( SQLException sqle )
    {
      String message = sqle.getMessage();
      LogClient.logMessage( message );
      if ( Editor.getInstance().isDebug() ) {
        LogClient.printStackTrace( sqle );
      }
      throw new RecordInsertionException( message, tableName, -9999 );
    }

    return results;
  }
  /****************************************************************************/
  /**
   * Create a new observing_cycle table record.
   *
   * @param newKeyValue The record ID used to access the table data
   * model values.
   *
   * @returns A single NewRecordResult object encapsulated in a vector
   * describing the results of the record creation attempt.
   */

  private Vector<NewRecordResult> createNewObservingCycleRecord( Object newKeyValue )
    throws RecordInsertionException
  {
    Vector<NewRecordResult> results;
    String tableName = "observing_cycle";

    try
    {
      // Set up the parameters for the <i>observing_cycle_ingest</i> stored
      // procedure call.
      Parameter insertionParams = new Parameter( tableName, newKeyValue );
      insertionParams.add( "returnCode", Parameter.OUT, Types.INTEGER, null) ;
      insertionParams.add( "ao_str", Parameter.IN, Types.VARCHAR );
      insertionParams.add( "ao_start", Parameter.IN, Types.TIMESTAMP );
      insertionParams.add( "ao_end", Parameter.IN, Types.TIMESTAMP );
      insertionParams.add( "result", Parameter.OUT, Types.INTEGER, null );

      // Insert the table into the database and process the output to
      // catch any errors.
      executeStoredProcedure( "observing_cycle_ingest", insertionParams );
      results = getNewRecordOutput( tableName, "ao_id",insertionParams, newKeyValue );

    }
    catch ( SQLException sqle )
    {
      String message = sqle.getMessage();
      LogClient.logMessage( message );
      if ( Editor.getInstance().isDebug() ) {
        LogClient.printStackTrace( sqle );
      }
      throw new RecordInsertionException( message, tableName, -9999 );
    }

    return results;
  }

  /****************************************************************************/
  /**
   * Create a new mt_ephem table record.
   *
   * @param newKeyValue The record ID used to access the table data
   * model values.
   *
   * @returns A single NewRecordResult object encapsulated in a vector
   * describing the results of the record creation attempt.
   */

  private Vector<NewRecordResult> createNewMtEphemRecord( Object newKeyValue )
    throws RecordInsertionException
  {
    Vector<NewRecordResult> results;
    String tableName = "mt_ephem";
    Object value = new Integer( 0 );

    try
    {
      // Set up the parameters for the <i>mtephem_ingest</i> stored
      // procedure call.
      Parameter insertionParams = new Parameter( tableName, newKeyValue );
      insertionParams.add( "returnCode", Parameter.OUT, Types.INTEGER, value );
      insertionParams.add( "obsid", Parameter.IN, Types.INTEGER );
      insertionParams.add( "epoch", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "axis", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "eccentricity", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "inclination", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "raan", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "aop", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "ma", Parameter.IN, Types.DOUBLE );

      // Save the record ID that we are trying to create.
      Object keyValue = insertionParams.get( 1 );

      // Insert the table into the database and process the output to
      // catch any errors.
      executeStoredProcedure( "mtephem_ingest", insertionParams );
      results = getNewRecordOutput( tableName, "obsid",insertionParams, newKeyValue );

      // If the results are empty, then a record was created using the
      // value of the obsid field.
      if ( results.size() == 0 )
      {
	results = new Vector<NewRecordResult>();
	NewRecordResult result = 
	  new NewRecordResult( tableName, "obsid",keyValue, newKeyValue, 0 );
	results.add( result );
      }
    }
    catch ( SQLException sqle )
    {
      String message = sqle.getMessage();
      LogClient.logMessage( message );
      if ( Editor.getInstance().isDebug() ) {
        LogClient.printStackTrace( sqle );
      }
      throw new RecordInsertionException( message, tableName, -9999 );
    }

    return results;
  }

  /****************************************************************************/
  /**
   * Create one or more new records and return a list of results for
   * each record created.  If the values used to generate the new
   * record match those of an existing record, that record is returned
   * instead of creating a new record.
   *
   * @param tableName The name of the table in which to create a new record.
   * @param newKeyValue The key value used to access the model data to
   * be used in supplying the stored procedure doing the actual
   * ingest.
   *
   * @returns A vector containing a result code set for each record
   * created.  The set of data consists of a return code and a record
   * ID.
   */

  public Vector createNewRecord( String tableName, Object newKeyValue )
    throws RecordInsertionException
  {
    // Predispose the result.
    Vector<NewRecordResult> result = null;

    // Case on the table name based on the most likely selection.
    if ( tableName.equals( "target" ) )
    {
      result = createNewTargetRecord( newKeyValue );
    }
    else if ( tableName.equals( "acisparam" ) )
    {
      result = createNewACISParamRecord( newKeyValue );
    }
    else if ( tableName.equals( "aciswin" ) )
    {
      result = createNewACISWinRecord( newKeyValue );
    }
    else if ( tableName.equals( "hrcparam" ) )
    {
      result = createNewHRCParamRecord( newKeyValue );
    }
    else if ( tableName.equals( "sim" ) )
    {
      result = createNewSIMRecord( newKeyValue );
    }
    else if ( tableName.equals( "rollreq" ) )
    {
      result = createNewRollreqRecord( newKeyValue );
    }
    else if ( tableName.equals( "timereq" ) )
    {
      result = createNewTimereqRecord( newKeyValue );
    }
    else if ( tableName.equals( "phasereq" ) )
    {
      result = createNewPhasereqRecord( newKeyValue );
    }
    else if ( tableName.equals( "dither" ) )
    {
      result = createNewDitherRecord( newKeyValue );
    }
    else if ( tableName.equals( "too" ) )
    {
      result = createNewTOOParamRecord( newKeyValue );
    }
    else if ( tableName.equals( "mt_ephem" ) )
    {
      result = createNewMtEphemRecord( newKeyValue );
    }
    else if ( tableName.equals( "observing_cycle" ) )
    {
      result = createNewObservingCycleRecord( newKeyValue );
    }
    else
    {
      // Unsupported table.
      String message = "No record creation support for table: " +
	tableName;
      throw new RecordInsertionException( message, tableName, -1 );
    }
    
    return result;
  }

  /****************************************************************************/
  /**
   * Create a new timereq table record.
   *
   * @param newKeyValue The record ID used to access the table data
   * model values.
   *
   * @returns A single NewRecordResult object encapsulated in a vector
   * describing the results of the record creation attempt.
   */

  private Vector<NewRecordResult> createNewTimereqRecord( Object newKeyValue )
    throws RecordInsertionException
  {
    Vector<NewRecordResult> results;
    String tableName = "timereq";
    Object value = new Integer( 0 );

    try
    {
      // Set up the parameters for the <i>timereq_ingest</i> stored
      // procedure call.
      Parameter insertionParams = new Parameter( tableName, newKeyValue );
      insertionParams.add( "returnCode", Parameter.OUT, Types.INTEGER, value );
      insertionParams.add( "obsid", Parameter.IN, Types.INTEGER );
      insertionParams.add( "ordr", Parameter.IN, Types.INTEGER );
      insertionParams.add( "window_constraint", Parameter.IN, Types.CHAR );
      insertionParams.add( "tstart", Parameter.IN, Types.TIMESTAMP );
      insertionParams.add( "tstop", Parameter.IN, Types.TIMESTAMP );
      insertionParams.add( "result", Parameter.OUT, Types.INTEGER, null );

      // Insert the table into the database and process the output to
      // catch any errors.
      executeStoredProcedure( "timereq_ingest", insertionParams );
      results =
        getNewRecordOutput( tableName, "timereq_id", insertionParams, newKeyValue );
    }

    catch ( SQLException sqle )
    {
      String message = sqle.getMessage();
      LogClient.logMessage( message );
      if ( Editor.getInstance().isDebug() ) {
        LogClient.printStackTrace( sqle );
      }
      if ( message.indexOf("Foreign key") >= 0 ) {
         message = "Invalid ObsId. Please correct and try again.";
      }
      throw new RecordInsertionException( message, tableName, -9999 );
    }

    return results;
  }

  /****************************************************************************/
  /**
   * Create a new rollreq table record.
   *
   * @param newKeyValue The record ID used to access the table data
   * model values.
   *
   * @returns A single NewRecordResult object encapsulated in a vector
   * describing the results of the record creation attempt.
   */

  private Vector<NewRecordResult> createNewRollreqRecord( Object newKeyValue )
    throws RecordInsertionException
  {
    Vector<NewRecordResult> results;
    String tableName = "rollreq";
    Object value = new Integer( 0 );

    try
    {
      // Set up the parameters for the <i>rollreq_ingest</i> stored
      // procedure call.
      Parameter insertionParams = new Parameter( tableName, newKeyValue );
      insertionParams.add( "returnCode", Parameter.OUT, Types.INTEGER, value );
      insertionParams.add( "obsid", Parameter.IN, Types.INTEGER );
      insertionParams.add( "ordr", Parameter.IN, Types.INTEGER );
      insertionParams.add( "roll_constraint", Parameter.IN, Types.CHAR );
      insertionParams.add( "roll_180", Parameter.IN, Types.CHAR );
      insertionParams.add( "roll", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "roll_tolerance", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "result", Parameter.OUT, Types.INTEGER, null );

      // Insert the table into the database and process the output to
      // catch any errors.
      executeStoredProcedure( "rollreq_ingest", insertionParams );
      results = getNewRecordOutput(tableName,"rollreq_id",insertionParams,newKeyValue );
    }

    catch ( SQLException sqle )
    {
      String message = sqle.getMessage();
      LogClient.logMessage( message );
      if ( Editor.getInstance().isDebug() ) {
        LogClient.printStackTrace( sqle );
      }
      if ( message.indexOf("Foreign key") >= 0 ) {
         message = "Invalid ObsId. Please correct and try again.";
      }
      throw new RecordInsertionException( message, tableName, -9999 );
    }

    return results;
  }

  /****************************************************************************/
  /**
   * Create a new sim table record.
   *
   * @param newKeyValue The record ID used to access the table data
   * model values.
   *
   * @returns A single NewRecordResult object encapsulated in a vector
   * describing the results of the record creation attempt.
   */

  private Vector<NewRecordResult> createNewSIMRecord( Object newKeyValue )
    throws RecordInsertionException
  {
    Vector<NewRecordResult> results;
    String tableName = "sim";
    Object value = new Integer( 0 );

    try
    {
      // Set up the parameters for the <i>sim_ingest</i> stored
      // procedure call.
      Parameter insertionParams = new Parameter( tableName, newKeyValue );
      insertionParams.add( "returnCode", Parameter.OUT, Types.INTEGER, value );
      insertionParams.add( "obsid", Parameter.IN, Types.INTEGER);
      insertionParams.add( "trans_offset", Parameter.IN, Types.DOUBLE);
      insertionParams.add( "focus_offset", Parameter.IN, Types.DOUBLE);

      // Save the record ID that we are trying to create.
      Object keyValue = insertionParams.get( 1 );

      // Insert the table into the database and process the output to
      // catch any errors.
      executeStoredProcedure( "sim_ingest", insertionParams );
      results = getNewRecordOutput( tableName,"obsid", insertionParams, newKeyValue );

      // If the results are empty, then a record was created using the
      // value of the obsid field.
      if ( results.size() == 0 )
      {
	results = new Vector<NewRecordResult>();
	NewRecordResult result = 
	  new NewRecordResult( tableName, "obsid",keyValue, newKeyValue, 0 );
	results.add( result );
      }
    }
    catch ( SQLException sqle )
    {
      String message = sqle.getMessage();
      LogClient.logMessage( message );
      if ( Editor.getInstance().isDebug() ) {
        LogClient.printStackTrace( sqle );
      }
      throw new RecordInsertionException( message, tableName, -9999 );
    }

    return results;
  }

  /****************************************************************************/
  /**
   * Create a new phasereq table record.
   *
   * @param newKeyValue The record ID used to access the table data
   * model values.
   *
   * @returns A single NewRecordResult object encapsulated in a vector
   * describing the results of the record creation attempt.
   */

  private Vector<NewRecordResult> createNewPhasereqRecord( Object newKeyValue )
    throws RecordInsertionException
  {
    Vector<NewRecordResult> results;
    String tableName = "phasereq";
    Object value = new Integer( 0 );

    try
    {
      // Set up the parameters for the <i>phasereq_ingest</i> stored
      // procedure call.
      Parameter insertionParams = new Parameter( tableName, newKeyValue );
      insertionParams.add( "returnCode", Parameter.OUT, Types.INTEGER, value );
      insertionParams.add( "obsid", Parameter.IN, Types.INTEGER );
      insertionParams.add( "phase_period", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "phase_epoch", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "phase_start", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "phase_end", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "phase_start_margin", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "phase_end_margin", Parameter.IN, Types.DOUBLE );
      insertionParams.add( "phase_unique", Parameter.IN, Types.CHAR );

      // Save the record ID that we are trying to create.
      Object keyValue = insertionParams.get( 1 );

      // Insert the table into the database and process the output to
      // catch any errors.
      executeStoredProcedure( "phasereq_ingest", insertionParams );
      results = getNewRecordOutput( tableName,"obsid", insertionParams, newKeyValue );

      // If the results are empty, then a record was created using the
      // value of the obsid field.
      if ( results.size() == 0 )
      {
	results = new Vector<NewRecordResult>();
	NewRecordResult result = 
	  new NewRecordResult( tableName, "obsid",keyValue, newKeyValue, 0 );
	results.add( result );
      }
    }
    catch ( SQLException sqle )
    {
      String message = sqle.getMessage();
      LogClient.logMessage( message );
      if ( Editor.getInstance().isDebug() ) {
        LogClient.printStackTrace( sqle );
      }
      throw new RecordInsertionException( message, tableName, -9999 );
    }

    return results;
  }

  /****************************************************************************/
  /**
   * Create a new target table record.
   */

  private Vector<NewRecordResult> createNewTargetRecord( Object newKeyValue )
    throws RecordInsertionException
  {
    Vector<NewRecordResult> result = null;
    ResultSet resultSet;
    int returnCode = -1;
    String name;
    Object obj;
    Object keyValue;
    int columnIndex;

    try
    {
      // OBSOLETE!!!!!!!!
      // Set up the parameters for the target_insert stored procedure
      // call.  The first two parameter are the return code and the
      if (returnCode == -1) {
        String message = "OBSOLETE: Target insert not allowed in obscat_editor.";
        throw new SQLException( message);
      }
    }
    catch ( SQLException sqle )
    {
      String message = sqle.getMessage();
      LogClient.logMessage( message );
      if ( Editor.getInstance().isDebug() ) {
        LogClient.printStackTrace( sqle );
      }
      throw new RecordInsertionException( message, "target", returnCode );
    }

    return result;
  }

  /****************************************************************************/

  public void deleteRow(String tableName, String keyColumnName, 
                        Object keyColumnValue)
    throws IngestedException, SQLException
  {
    if ( tableName.equals("target") )
    {
      if ( keyColumnName.equals("obsid") )
      {
        deleteTargetRow( ((Integer) keyColumnValue).intValue() );
      }
      else
      {
        int obsID = lookupObsID(keyColumnName, keyColumnValue);

        deleteTargetRow(obsID);
      }
    }
    if ( tableName.equals("too") ) {
        deleteTooRow( ((Integer) keyColumnValue).intValue() );
    }
    if ( tableName.equals("grant_info") ) {
        deleteGrantInfoRow( ((Integer) keyColumnValue).intValue() );
    }
    else
    {
      // Execute the delete SQL statement, then close it and return.
      PreparedStatement statement = 
	connection.prepareStatement("DELETE FROM " + tableName + 
				    " WHERE " + keyColumnName + " = ?");
      statement.setObject(1, keyColumnValue);
      executeUpdate( statement );

      statement.close();
    }

    // Remove the row from the data model.
    model.removeRow( tableName, keyColumnValue );
  }

  /****************************************************************************/

  public void deleteTargetRow(int obsID)
    throws IngestedException, SQLException
  {
    if ( getObsIDCount("mp", obsID) > 0 )
    {
      throw( new IngestedException() );
    }

    if ( getObsIDCount("lookup", obsID) > 0 )
    {
      throw( new IngestedException() );
    }

    // Exceute the obs_delete stored procedure, close the statement
    // and return.
    CallableStatement statement = 
      connection.prepareCall("{ call obs_delete(?) }");
    statement.setInt(1, obsID);
    executeUpdate( statement );
    statement.close();
  }

  /****************************************************************************/
  public void deleteTooRow(int tooID)
    throws SQLException
  {
    // Exceute the obs_delete stored procedure, close the statement
    // and return.
    CallableStatement statement = 
      connection.prepareCall("{ call too_delete(?) }");
    statement.setInt(1, tooID);
    executeUpdate( statement );
    statement.close();
  }
  /****************************************************************************/
  public void deleteGrantInfoRow(int grantID)
    throws SQLException
  {
    // Exceute the obs_delete stored procedure, close the statement
    // and return.
    CallableStatement statement = 
      connection.prepareCall("{ call grant_delete(?) }");
    statement.setInt(1, grantID);
    executeUpdate( statement );
    statement.close();
  }





  /****************************************************************************/
  /**
   *  Extract data from the database using a given query.
   *
   * @param query The SQL expression.
   *
   * @returns The results packed into a vector of Objects.
   */

  public Vector<Object> executeQuery( String query )
  {
    // Predispose the result to failure.
    Vector<Object> result= null;

    // Get the data from the database.
    try 
    {
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery( query );
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();

      // Unpack the result set into a vector.
      result = new Vector<Object>();
      while ( resultSet.next() )
      {
	// Collect the objects.
	result.addElement( resultSet.getObject( 1 ) );
      }
    }
    catch ( SQLException sqlException ) 
    {
      LogClient.printStackTrace( sqlException );
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Execute a stored procedure, returning a vector containing a result
   * code and, optionally, SQL results.
   * 
   * @param <i>parameters</i> A vector which hold the return code,
   * input parameters, output parameterss, and mixed parameters (both
   * input and output).  The return code consists of an integer and
   * can be one of:
   *        0 => success
   *    -9999 => failure
   *     -108 => depends on the calling method
   *       >0 => standard Sybase error code.
   *
   * @returns A two dimensional vector containing the result set data.
   */

  private Vector<Vector<Object>> executeStoredProcedure( String name, Parameter parameters )
    throws SQLException
  {
    // Generate the SQL query prefix,
    // appending the procedure name; maintain an index into the
    // current parameter.
    int i = 0;
    String query = "{ ?  = call " + name + " ( ";
    i++;

    // Append placeholders for each input and output.
    int N = parameters.size();
    for ( ; i < N; i++ )
    {
      query += "?";

      // Deal with the field separator.
      if ( i < N - 1 )
      {
	query += ", ";
      }
      else
      {
	query += " ) }";
      }
    }

    // Set up the call and deal with the return code parameter.
    i = 0;
    CallableStatement call = connection.prepareCall( query );
    i++;
    call.registerOutParameter( i, Types.INTEGER );

    // Process the parameters.
    setupParameters( call, parameters, i );

    // Execute the stored procedure and append the SQL results, if
    // any.
    Vector<Vector<Object>> results = execute( call );

    // Lastly, extract the output values, including the return code.
    extractOutput( call, parameters );


    return results;
  }

  /****************************************************************************/
  // This method should be eliminated.  Any callers should be
  // rewritten to use the executeStoredProcedure( String, Parameter )
  // variant.  For cycle 2 expedience, it has been left in place.

  /**
   * Execute a stored procedure, returning a result code.
   * <i>parameters</i> is a vector which hold the returnCode, the
   * inputs, outputs and parameters which are both an input and an
   * output. The returned result set can be null and sh
   */

  private Vector<Vector<Object>> executeStoredProcedure( String name,
					 Parameter parameters,
					 boolean hasReturnCode )
    throws SQLException
  {
    int returnCode = -1;
    Vector<Vector<Object>> results = null;
    String query;

    // Generate the SQL query prefix, appending the procedure name.
    int i = 0;
    if ( hasReturnCode )
    {
      query = "{ ?  = call " + name + " ( ";
      i++;
    }
    else
    {
      query = "{ call " + name + " ( ";
    }

    // Append placeholders for each input and output.
    int N = parameters.size();
    for ( ; i < N; i++ )
    {
      query += "?";

      // Deal with the field separator.
      if ( i < N - 1 )
      {
	query += ", ";
      }
      else
      {
	query += " ) }";
      }
    }

    // Set up the call and deal with a return code parameter, if
    // present.
    i = 0;
    CallableStatement call = connection.prepareCall( query );
    if ( hasReturnCode )
    {
      i++;
      call.registerOutParameter( i, Types.INTEGER );
    }

    // Process the parameters.
    setupParameters( call, parameters, i );

    // Execute the stored procedure and process the results.
    results = execute( call );

    // Process the return code.
    if ( hasReturnCode )
    {
      // Determine of the call failed.
      returnCode = call.getInt( 1 );
      if ( returnCode == -9999 )
      {
	// It did.  Process warnings, if any.
	processWarnings( call );
      }
    }

    // Lastly, extract the output values.
    extractOutput( call, parameters );

    return results;
  }

  /****************************************************************************/
  /**
   * Execute a stored procedure, returning a results vector.  This
   * variant of executeStoredProcedure assumes that the result will be
   * passed via the last parameter instead of via the stored procedure
   * result vector.  This output is then processed to build a new result
   * vector.
   *
   * @param procedureName  The stored procedure id.
   * @param parameters     The list of input/output parameters passed to
   *                       the stored procedure.
   * @param tableName      The table id.
   * @param newKeyValue    The record id used to access values from the data
   *                       model.
   */

  private Vector<NewRecordResult> executeStoredProcedure( String procedureName,
					 Parameter parameters,
					 String tableName,
					 Object newKeyValue )
    throws SQLException, RecordInsertionException
  {
    Object keyValue = null;
    int returnCode = -9999;
    Vector<NewRecordResult> results;

    // Execute the stored procedure and get the result from the last
    // parameter.
    executeStoredProcedure( procedureName, parameters, false );
    int index = parameters.size() - 1;
    keyValue = parameters.get( index );

    // Take an exception if there is no result.
    if ( keyValue == null )
    {
      String message =
	"Unknown failure inserting into table " + tableName + ".";
      throw new RecordInsertionException( message, tableName, -9999 );
    }

    // Determine whether or not we have a new record, reused record or an error.
    returnCode = ((Integer) keyValue).intValue();
    if ( returnCode > 0 )
    {
      // The operation was successful.  Next determine if the return
      // code is a new record id or a reused one.
      if ( model.hasRecord( tableName, keyValue ) )
      {
	// The result is an existing record.  Generate a more suitable
	// return code.
	returnCode = -107;
      }
      else
      {
	// The result is a new record.
	returnCode = 0;
      }
    }
    else
    {
      // An error occurred.  Take an exception.
      String message =
	"Unknown failure inserting into table " + tableName + ".";
      throw new RecordInsertionException( message, tableName, returnCode );
    }

    // Create the result vector, generate the single entry and return it.
    results = new Vector<NewRecordResult>();
    NewRecordResult newRecordResult = 
      new NewRecordResult( tableName, keyValue, newKeyValue, returnCode );
    results.add( newRecordResult );
    return results;
  }

  /****************************************************************************/
  /**
   * Execute a stored procedure returning the fully processed results.
   * This method follows the JDBC recommendations on handling stored
   * procedure output.
   *
   * @param call The callable statement.
   *
   * @returns A vector containing the processed result set, null if a
   * result set is not generated.
   */

  private Vector<Vector<Object>> execute( CallableStatement call )
    throws SQLException
  {
    Vector<Vector<Object>> results = null;
    String message = null;

    call.execute();

    // JDBC recommended processing:
    while ( true )
    {
      int resultCount = call.getUpdateCount();
      if ( resultCount >= 0 )
      {
	call.getMoreResults();
	continue;
      }

      // Now we should have results.  Save them, if any.
      ResultSet resultSet = call.getResultSet();
      int N = 0;
      if ( resultSet != null )
      {
	// Get the number of columns in the result set.
	ResultSetMetaData metaData = resultSet.getMetaData();
	N = metaData.getColumnCount();

	// Create the results vector.
	results = new Vector<Vector<Object>>();
      }

      // Process each result set, i.e. extract each object into a
      // column vector then insert the column vector into the results
      // vector.
      while ( resultSet != null && resultSet.next() )
      {
	// Generate the column results vector.
	Vector<Object> columnResults = new Vector<Object>();
	for ( int i = 1; i <= N; i++ )
	{
	  columnResults.add( resultSet.getObject( i ) );
	}

	// Add the column results to the return value.
	results.add( columnResults );
      }

      // Deal with any warning messages.
      processWarnings( call );
      
      break;
    }

    return results;
  }

  /****************************************************************************/
  /**
   * Execute an SQL update statement.  Collect any warning messages
   * and pass them up as an exception.
   */

  private void executeUpdate( PreparedStatement statement )
    throws SQLException
  {
    String message = "";
    
    // Execute the statement and process any warnings.
    statement.executeUpdate();
    processWarnings( statement );
  }

  /****************************************************************************/
  /**
   * Execute an SQL update statement.  Collect any warning messages
   * and pass them up as an exception.
   */

  private void executeUpdate( Statement statement, String expression )
    throws SQLException
  {
    String message = null;
    
    // Execute the statement and process any warnings.
    statement.executeUpdate( expression );
    processWarnings( statement );
  }

  /****************************************************************************/
  /**
   * Extract the output from a stored procedure execution.  The
   * results from the call statement are returned back to the set of
   * parameters.
   *
   * @param call The stored proceure call statement.
   * @param parameters The set of input/output parameters.
   */

  private void extractOutput( CallableStatement call, Parameter parameters )
    throws SQLException
  {
    int parameterType;
    Object value;

    // Loop through each parameter getting the output from the call
    // statement.
    int N = parameters.size();
    for ( int i = 0; i < N; i++ )
    {
      // Filter out input-only parameters.
      parameterType = parameters.getType( i );
      if ( parameterType == Parameter.INOUT || parameterType == Parameter.OUT )
      {
	// The parameter is an output.  Get the returned value by it's SQL type.
	switch( parameters.getDBType( i ) )
	{
	case Types.CHAR:
	case Types.VARCHAR:
	  value = call.getString( i + 1 );
	  break;

	case Types.DATE:
	  value = call.getDate( i + 1 );
	  break;
	    
	case Types.DOUBLE:
	  value = new Double( call.getDouble( i + 1 ) );
	  break;
	    
	case Types.FLOAT:
	  value = new Float( call.getFloat( i + 1 ) );
	  break;

	case Types.INTEGER:
	  value = new Integer( call.getInt( i + 1 ) );
	  break;

	case Types.TIMESTAMP:
	  value = call.getTimestamp( i + 1 );
	  break;
	    
	default:
	  // Deal with an internal software error.
	  throw new SQLException( "Unsupported type for field: " + 
				  parameters.getName( i ) );
	}

	// Store the output value into the parameter.
	parameters.put( i, value );
      }
    }
  }

  /****************************************************************************/
  /**
   * Return a set of formatted strings from the given vector.
   */

  private String generateSQLList(Vector vector)
  {
    StringBuffer result = new StringBuffer();

    switch ( vector.size() )
    {
    case 1:
      {
        result.append( vector.firstElement() );
        break;
      }
    default:
      {
        result.append(vector.firstElement() + ", ");

        for ( int i = 1; i < vector.size() - 1; i++ )
        {
          result.append(vector.elementAt( i ) + ", ");
        }

        result.append( vector.lastElement() );

        LogClient.logMessage( "TableConfiguration: elements are " + result );

        break;
      }
    }

    return( result.toString() );
  }

  /****************************************************************************/
  /**
   * Return the set of choices for a particular column.
   *
   * @param tableName The table ID.
   * @param columnName The column ID.
   *
   * @return The set of distinct values in the column.
   */

  public Vector<Object> getChoices( String tableName, String columnName )
  {
    String query = "SELECT DISTINCT " + columnName + " FROM " + tableName +
      	" ORDER BY " + columnName;

    return executeQuery( query );
  }

  /****************************************************************************/
  /**
   * Return the column index of the column <I>columnName</I> in table
   * <I>tableName</I>.  Indexing starting with zero.
   */

  public int getColumnIndex( String tableName, String columnName )
  {
    // Initialize the result.
    int result = -1;

    // Get the dummy data for this table.
    ResultSet results = (ResultSet) resultSetCache.get( tableName );

    try
    {
      if ( results == null )
      {
	String query = ("SELECT * FROM " + tableName + " WHERE 1 = 0");

	// Fetch a dummy result set from the database.  Store it keyed
	// on the table name.
	Statement statement = connection.createStatement();

	results = statement.executeQuery( query );

	resultSetCache.put( tableName, results );
      }

      result = results.findColumn( columnName ) - 1;
          if ( Editor.getInstance().isDebug() )
          {
            System.out.println("Dummy Record: " + columnName + " result: " + result );
          }
     
    }
    catch ( SQLException sqlException )
    {
      LogClient.printStackTrace( sqlException );
    }

    return( result );
  }

  /****************************************************************************/
  /**
   *  Return the data model.
   */

  public DatabaseModel getDatabaseModel()
  {
    return model;
  }

  /****************************************************************************/
  /**
   * getDate: Helper method used to get a date from the database.
   */

  public Date getDate( ResultSet resultSet, int index )
  {
    java.sql.Date result;

    try
      {
	result = resultSet.getDate(index);
      }
    catch (SQLException sqlException)
      {
	result = null;
      }
    catch (NullPointerException nullPointerException)
      {
	// There appears to be a bug in the PostgreSQL handling of a
	// null date.  Ignore it.
	result = null;
      }
    catch (Exception generalException)
      {
	LogClient.printStackTrace( generalException );
	result = null;
      }

    return result;
  }

  /****************************************************************************/
  /**
   * getDBName: Return the name of the current database.
   */

  public String getDBName()
  {
    return dbName;
  }

  /****************************************************************************/
  /**
   * getDescription: Return the description of the current database selection.
   */

  public String getDescription()
  {
    return description;
  }

  /****************************************************************************/
  /**
   *  Extract data from the database into a two-dimensional vector.
   *  Return the vector.
   */

  public Vector<Object> getFilteredKeys( DatabaseKey keyEntry, Filter filter )
  {
    // Set up the query string.
    String query = ( "SELECT " + keyEntry.getColumnName() + 
                     " FROM " + keyEntry.getTableName() +
                     filter.toSQL()  + 
                     " ORDER BY " + keyEntry.getColumnName() );

    return executeQuery( query );
  }

  /****************************************************************************/
  /**
   * Return the history of a particular cell as an array of elements.
   */

  public Vector<Vector<String>> getHistory( String columnName, Integer observationID )
  {
    Vector<Vector<String>> result = new Vector<Vector<String>>();

    try
    {
      PreparedStatement statement = 
	connection.prepareStatement("SELECT column, old_value, new_value, " + 
				    "user_name, change_date " + 
				    "FROM history " + 
				    "WHERE column like ? " + 
				    "AND obsid = ? " +
				    "ORDER BY change_date DESC");

      statement.setString(1, columnName);
      statement.setInt(2, observationID.intValue());
      if ( Editor.getInstance().isDebug() )
	{
        System.out.println("history: " + columnName + " " + observationID);
        }
      ResultSet resultSet = statement.executeQuery();

      while ( resultSet.next() )
      {
	String columnValue = resultSet.getString("column");
	String oldValue = resultSet.getString("old_value");
	String newValue = resultSet.getString("new_value");
	String username = resultSet.getString("user_name");
	Timestamp changeDate = resultSet.getTimestamp("change_date");

	Vector<String> row = new Vector<String>();

	row.addElement(columnValue);
	row.addElement(username);
	row.addElement(oldValue);
	row.addElement(newValue);

        String timestamp = 
          (String) DateFormat.getDateTimeInstance().format(changeDate);

	row.addElement(timestamp);
	
	result.addElement(row);
      }

      resultSet.close();
      statement.close();
    }
    catch (SQLException exception)
    {
      exception.printStackTrace();
    }

    return(result);
  }

  /****************************************************************************/
  /**
   * Return the history of a particular cell as an array of elements.
   */

  public Vector<Vector<String>> getHistory( String columnName, String sequenceNumber )
  {
    Vector<Vector<String>> result = new Vector<Vector<String>>();

    try
    {
      PreparedStatement statement = 
	connection.prepareStatement("SELECT column,old_value, new_value, " + 
				    "user_name, change_date " + 
				    "FROM history " + 
				    "WHERE column like ? " + 
				    "AND seq_nbr = ? " +
				    "ORDER BY change_date DESC");

      statement.setString(1, columnName);
      statement.setString(2, sequenceNumber);

      ResultSet resultSet = statement.executeQuery();

      while ( resultSet.next() )
      {
	String columnValue = resultSet.getString("column");
	String oldValue = resultSet.getString("old_value");
	String newValue = resultSet.getString("new_value");
	String username = resultSet.getString("user_name");
	Timestamp changeDate = resultSet.getTimestamp("change_date");

	Vector<String> row = new Vector<String>();

	row.addElement(columnValue);
	row.addElement(username);
	row.addElement(oldValue);
	row.addElement(newValue);

        String timestamp = 
          (String) DateFormat.getDateTimeInstance().format(changeDate);

	row.addElement(timestamp);
	
	result.addElement(row);
      }

      resultSet.close();
      statement.close();
    }
    catch (SQLException exception)
    {
      exception.printStackTrace();
    }

    return(result);
  }

  /****************************************************************************/
  /**
   *  Return a Vector of the values for <I>databaseKey</I>.
   */

  public Vector<Object> getKeys( DatabaseKey databaseKey )
  {
    Vector<Object> result = new Vector<Object>();

    try
    {
      Statement statement = connection.createStatement();

      String query = ( "SELECT " + databaseKey.getColumnName() + 
		" FROM " + databaseKey.getTableName()  );
      if (databaseKey.getTableName().equalsIgnoreCase("observing_cycle")) {
              query += " where ao_id != 0 ";
      }
      query += " ORDER BY " + databaseKey.getColumnName();
      if ( Editor.getInstance().isDebug() ) {
	  System.out.println( "getKeys => " + query );
      }

      ResultSet resultSet = statement.executeQuery(query);

      while ( resultSet.next() )
      {
        result.addElement( resultSet.getObject(1) );
      }
    }
    catch ( SQLException sqlException )
    {
      LogClient.printStackTrace(sqlException);
    }

    return(result);
  }

  /****************************************************************************/
  /**
   * Return a list of results.  Treat each output parameter (excepting
   * the return code) as a new record ID or an error code.
   *
   * @param tableName The table.
   * @param params The set of input/output parameters passed to the
   * stored procedure.
   * @param originalKeyValue The initial record ID.
   *
   * @returns A set of NewRecordResult objects.
   */

  public Vector<NewRecordResult> getNewRecordOutput( String tableName, String columnName,
                                    Parameter params,
				    Object originalKeyValue )
    throws RecordInsertionException
  {
    Vector<NewRecordResult> results = new Vector<NewRecordResult>();
    int code;

    // Convert a "non" return code (the absence of a return code) as
    // success.
    Object returnCode = params.get( 0 );
    if ( returnCode == null )
    {
      returnCode = new Integer( 0 );
    }

    // Process the return code, which must be either -9999 indicating
    // failure, -107 indicating that the record already exists but the
    // operation will continue, -108 indicating that the desired
    // record already exists and the operation was canceled, or 0
    // indicating success.

    // Convert the code to an integer.
    code = ((Integer) returnCode).intValue();
    switch ( code )
    {
    case 0:
      // The operation was a success.  Walk the list of parameters
      // looking for output types.
      NewRecordResult result;
      for ( int i = 1; i < params.size(); i++ )
      {
	if ( params.getType( i ) == Parameter.OUT )
	{
	  Object keyValue = params.get( i );
	  code = ((Integer) keyValue).intValue();
	  if ( code > 0 )
	  {
	    result =
	      new NewRecordResult( tableName, columnName,keyValue, originalKeyValue, 0 );
	  }
	  else
	  {
	    result =
	      new NewRecordResult( tableName, columnName,keyValue, originalKeyValue, code);
	  }
	  results.add( result );
	}
      }
      break;

    case -107:
      // The record already exists --- SQL operation continued.
      result = 
	new NewRecordResult( tableName, columnName,originalKeyValue,
			     originalKeyValue, -107 );
      results.add( result );
      break;
	  
    case -108:
      // The record already exists --- SQL operation canceled.
      // The operation was a success.  Walk the list of parameters
      // looking for output types.
      for ( int i = 1; i < params.size(); i++ )
      {
	if ( params.getType( i ) == Parameter.OUT )
	{
	  // There should only be a single output parameter,
	  // specifically the record ID for the row which already
	  // exists.  Break out of the loop and return that ID.
	  Object keyValue = params.get( i );
	  result =
	    new NewRecordResult( tableName, columnName,keyValue, originalKeyValue, -108 );
	  results.add( result );
	  break;
	}
      }
      break;

    case -9999:
      String message =
	"Failure occurred inserting a record into the " + tableName + " table.";
      throw new RecordInsertionException( message, tableName, -9999 );
	
    default:
      message = "Unexpected result code: " + code;
      throw new RecordInsertionException( message, tableName, code );
    }
    
    return results;
  }

  /****************************************************************************/
  /**
   * Return a new sequence number using a stored procedure.
   */

  public String getNewSequenceNumber( Object newKeyValue,
				      String sequenceNumber )
    throws RecordInsertionException
  {
    String result = null;
    int returnCode = -1;

    // Create the parameters necessary to generate a new sequence
    // number. The first entry is a placeholder for the return code.
    Parameter getSeqNumParams = new Parameter( "target", newKeyValue );
    getSeqNumParams.add( "returnCode", Parameter.OUT, Types.INTEGER, null );

    // Generate the category value based on the current sequence number.
    int val = Integer.parseInt( sequenceNumber.substring( 0, 1 ) ) - 1;
    Integer categoryValue = new Integer( val );
    getSeqNumParams.add( "category", Parameter.IN, Types.INTEGER,
			 categoryValue );

    // Generate the cal_flag parameter.
    int columnIndex = getColumnIndex( "target", "type" );
    String string = (String) model.getValueAt( "target", newKeyValue, columnIndex );
    Object calFlag;
    if ( string.equals( "CAL" ) )
    {
      calFlag = new Integer( 1 );
    }
    else
    {
      calFlag = new Integer( 0 );
    }
    getSeqNumParams.add( "cal_flag", Parameter.IN, Types.INTEGER, calFlag );

    // Execute the stored procedure to get the new sequence number
    // and return it.
    try
    {
      Vector outputs =
	executeStoredProcedure( "pub_next_avail_seq_nbr", getSeqNumParams, true );
      if ( outputs == null )
      {
	String message = "Could not generate a new sequence number";
	throw new RecordInsertionException( message, "target", -9999 );
      }

      // Extract the return code from the parameters vector to check for errors.
      returnCode = ((Integer) getSeqNumParams.get( 0 )).intValue();
      if ( returnCode != 0 )
      {
	String message =
	  "Error occurred while generating a new sequence number:" + returnCode;
	throw new RecordInsertionException( message, "target", returnCode );
      }

      // Extract the sequence number from the database output.
      Vector v = (Vector) outputs.elementAt( 0 );
      result = (String) v.elementAt( 0 );
    }
    catch ( SQLException exception )
    {
      String message = exception.getMessage();
      LogClient.logMessage( message );
      LogClient.printStackTrace( exception );
    }

    return result;
  }

  /****************************************************************************/

  public int getObsIDCount(String table, int obsID)
    throws SQLException
  {
    int result = 0;

    try
    {
      PreparedStatement statement = 
        connection.prepareStatement("SELECT obsid FROM " + table + 
                                    " WHERE obsid = ?");

      statement.setInt(1, obsID);

      ResultSet resultSet = statement.executeQuery();
      
      while ( resultSet.next() )
      {
        result++;
      }
    }
    catch (SQLException sqlException)
    {
      LogClient.printStackTrace( sqlException );

      throw(sqlException);
    }

    return(result);
  }

  /****************************************************************************/
  /**
   *  Return a Vector of Pair's in table.
   */

  public Vector<Pair> getPairs( String table, String keyColumn, String valueColumn )
  {
    Vector<Pair> result = new Vector<Pair>();

    try
    {
      Statement statement = connection.createStatement();

      String query = ("SELECT " + keyColumn + ", " + valueColumn + 
                      " FROM " + table  );
      if (table.equalsIgnoreCase("observing_cycle") && 
	(keyColumn.equalsIgnoreCase("ao_id") || keyColumn.equalsIgnoreCase("ao_str") )) {
              query += " where ao_id != 0 ";
      }
      query +=  " ORDER BY " + valueColumn;

      ResultSet resultSet = statement.executeQuery(query);
      if ( Editor.getInstance().isDebug() )
         System.out.println("getPairs: " + query);

      while ( resultSet.next() )
      {
        String ptmp = resultSet.getString(2);
        if (resultSet.getObject(2) == null || 
            ptmp.startsWith(" ") ) {
	  if ( Editor.getInstance().isDebug() )
	  {
            System.out.println("Database pair: " + resultSet.getObject(1));
            System.out.println("         pair: " + resultSet.getObject(2));
	  }
          Pair pair = new Pair(resultSet.getObject(1),
 		resultSet.getObject(1));
          result.addElement(pair);
        }
        else {
          Pair pair = new Pair(resultSet.getObject(1),
                             resultSet.getObject(2));
          result.addElement(pair);
        }

      }
    }
    catch ( SQLException sqlException )
    {
      LogClient.printStackTrace(sqlException);
    }

    return(result);
  }

  /****************************************************************************/
  /**
   * Get the permissions associated with a particular table.
   */

  public Properties getPermissions( String table )
  {
    Properties result = new Properties();
    CallableStatement cs;
    ResultSet rs;

    try
    {
      // First determine if the curent user is the owner, in which
      // case simply return the a hashtable containing the pair "All",
      // "All".
      if ( isOwner( user ) )
      {
	result.put( "All", "All" );
      }
      else if (table.indexOf("axafobstat") < 0 && table.indexOf("axafusers") < 0) 
      {
	// Execute the sp_helprotect system procedure to get the
	// permissions for the given user and table.
	//cs = connection.prepareCall( "{? = call sp_helprotect}" );
	cs = connection.prepareCall( "{? = call sp_helprotect( ?, ? )}" );
	cs.setString( 2, table );
	cs.setString( 3, user );
	cs.registerOutParameter( 1, java.sql.Types.INTEGER );

    
	// Execute the system procedure and process the results.
	boolean results = cs.execute();
	int rowsAffected = 0;       
	do
	{
	  if ( results )
	  {
	    rs = cs.getResultSet();
	    
	    // Parse the output.
	    while ( rs.next() )
	    {
	      //String grantee = rs.getString( 2 );
	      String action = rs.getString( 4 );
	      String column = rs.getString( 6 );
              //if (grantee.equalsIgnoreCase("editor_role")) {
	        result.put( column, action );
              //}
	    }
	    rs.close();
	  }
	  else
	  {
	    rowsAffected = cs.getUpdateCount();
	  }
	  results = cs.getMoreResults();
	}
	while ( results || rowsAffected != -1);

	// Handle the system procedure return value.
	int dbResult = cs.getInt( 1 );
	if ( dbResult != 0 )
	{
	  // Deal with an error somehow.
	  // ??? probably should be a throw.
	  LogClient.logMessage( "Error returned from sp_helprotect system procedure." );
	}
      }
    }
    catch ( SQLException se )
    {
      LogClient.printStackTrace( se );
    }
    return result;
  }

  /****************************************************************************/
  /**
   * Return the currently selected database server.
   */

  public String getServer()
  {
    return( server );
  }

  /****************************************************************************/
  /**
   * Return the SQL type of the column <I>columnName</I> in table
   * <I>tableName</I>.
   */

  public int getSQLType( String tableName, String columnName )
  {
    // Initialize the result.
    int result = -1;

    // Get the dummy data for this table.
    ResultSet results = (ResultSet) resultSetCache.get( tableName );

    try
    {
      if ( results == null )
      {
	String query = ("SELECT * FROM " + tableName + " WHERE 1 = 0");

	// Fetch a dummy result set from the database.  Store it keyed
	// on the table name.
	Statement statement = connection.createStatement();

	results = statement.executeQuery( query );

	resultSetCache.put( tableName, results );
      }

      int columnIndex = results.findColumn( columnName );

      ResultSetMetaData metaData = results.getMetaData();

      result = metaData.getColumnType( columnIndex );
    }
    catch ( SQLException sqlException )
    {
      LogClient.printStackTrace( sqlException );
    }

    return( result );
  }

  /****************************************************************************/
  /**
   *  Extract data from the database into a two-dimensional vector.
   *  Return the vector.
   */

  public Vector<Vector<Object>> getTable( DatabaseKey databaseKey )
  {
    // Set up the query string.
    String query = ( "SELECT * FROM " + databaseKey.getTableName() + 
                     " ORDER BY " + databaseKey.getColumnName() );

    // Get the data from the database.
    LogClient.logMessage("Executing query (" + query + ").");
    if ( Editor.getInstance().isDebug() )
       System.out.println("getTable : " + query);

    Vector<Vector<Object>> result = new Vector<Vector<Object>>();

    try 
    {
      Statement statement = connection.createStatement();

      ResultSet resultSet = statement.executeQuery( query.toString() );

      int columnCount = resultSet.getMetaData().getColumnCount();

      // Unpack the result set into a vector.
      while ( resultSet.next() )
      {
	Vector<Object> newRow = new Vector<Object>();

	for (int i = 0; i < columnCount; i++) 
        {
	  Object object = resultSet.getObject( i + 1 );

          newRow.addElement( object );
	}

	result.addElement( newRow );
      }
    }
    catch ( SQLException sqlException ) 
    {
      LogClient.printStackTrace( sqlException );
    }

    return( result );
  }

  /****************************************************************************/
  /**
   *  Return the SQL type correponding to <i>column</i> in <i>table</i>.
   *
   *  <i>table</i> is the table name in the axafocat database.
   *  <i>column</i> is the column name in <i>table</i>.
   */

  public int getType( String table, String column )
    throws SQLException
  {
    int result = Types.NULL;

    // Make sure the cache has been initialised.
    if ( typesCache == null )
    {
      typesCache = new Hashtable<String,Integer>();
    }

    // The type information is cached.  See if the given table/column
    // pair is in the cache.
    Integer value = (Integer) typesCache.get( table + ":" + column );
    if ( value == null )
    {
      // It's not.  Encache the values for table.
      
      // Make sure there exists a call statement to use in getting the
      // type data.
      if ( getTypeStatement == null )
      {
	// There isn't.  Get one.
	getTypeStatement = connection.prepareCall( "{call sp_cols( ? )}" );
      }

      // Get the types for the columns in this table.
      getTypeStatement.setString( 1, table );
      ResultSet results = getTypeStatement.executeQuery();

      // Load the cache with the values from the result set.
      while ( results.next() )
      {
	String colStr = results.getString( 1 );
	String typeStr = results.getString( 2 );
	Integer typeVal = (Integer) typeMap.get( typeStr );
	if ( typeVal == null )
	{
	  String message = 
	    "Database:getType(): Unsupported Sybase/SQL type encountered: " + typeStr + " for " + colStr;
	  LogClient.logMessage( message );
	  typeVal = (Integer) typeMap.get( "unknown" );
	}
	typesCache.put( table + ":" + colStr, typeVal );
      }
      value = (Integer) typesCache.get( table + ":" + column );
    }
    result = value.intValue();

    return result;
  }

  /****************************************************************************/
  /**
   *  Return the set of types corresponding to the current prepared statement.
   *
   *  <i>type</i> is the DB data type for the seed value.
   *  <i>index</i> is the key value.
   */

  public int[] getTypes(Object index) throws InvalidQueryException
  {
    int[] result = null;

    // Insure we have a valid prepared statement.
    if ( formsPreparedStatement == null )
    {
      // Handle errors by throwing a suitable exception.
      String message =
	"Please execute a successful initPreparedStatement() call prior to getTypes().";
      throw new InvalidQueryException( message, "" );
    }

    // Talk to the database, setting the key parameter and executing the query.
    try
    {
      // Prime the query parameter value.
      setKeyValue(index );

      // Refresh the values and position the result set to the single
      // row of data.
      ResultSet values = formsPreparedStatement.executeQuery();
      ResultSetMetaData metadata = values.getMetaData();

      // There should be one and only one row of data.
      values.next();

      // Place the results in the return vector.
      int N = metadata.getColumnCount();
      result = new int[ N ];
      for ( int i = 1; i <= metadata.getColumnCount(); i++ )
      {
	result[ i - 1 ] = metadata.getColumnType( i );
      }
    }
    catch ( SQLException sqle )
    {
      // Deal with an SQL error.
      LogClient.logMessage( "An SQL error occurred while updating the form." );
      LogClient.printStackTrace( sqle );
      
      // Suggestions for further actions?
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Return the currently selected database user.
   */

  public String getUser()
  {
    return( user );
  }

  /****************************************************************************/
  /**
   *  Return a set of values for a table using a given row.  If the
   *  row value is 0 the result is a set of nulled values.
   *
   * @param <I>databaseKey</I> The table/column specification.  The
   * given column must be the key column for the given table.
   * @param <I>keyColumnValue</I> The row of interest.  Indexing starts with 0.
   *
   * @returns A vector consisting of the set of values for the given
   * table, column, row.  If row is 0, the values returned are nulls.
   */

  public Vector<Object> getValues( DatabaseKey databaseKey, Object keyColumnValue )
  {
    // Initialize the return value.
    Vector<Object> result = null;

    try
    {
      // Fetch all columns at row <i>rowIndex</i>.
      String query = ( "SELECT * FROM " + databaseKey.getTableName() +
                       " WHERE " + databaseKey.getColumnName() +
                       " = " + keyColumnValue );
      Statement statement = connection.createStatement();
      ResultSet values = statement.executeQuery( query );
      ResultSetMetaData metadata = values.getMetaData();
      int N = metadata.getColumnCount();

      // Queue up the one and only row, so values can be extracted.
      // If the record is the 0th record, then return a nulled vector
      // of the right size..
      result = new Vector<Object>();
      Class cl = keyColumnValue.getClass();
      String tmp_cl = (cl.getName()).toLowerCase();
      int recordIndex;
      int tmp_idx = tmp_cl.indexOf("double");
      if (tmp_idx < 0) {
        recordIndex = ((Integer) keyColumnValue).intValue();
      }
      else {
        recordIndex = ((Double) keyColumnValue).intValue();
      }
      if ( recordIndex == 0 )
      {
        // Loop through the result set initializing the values to null.
        for ( int i = 1; i <= N; i++ )
        {
          result.addElement( null );
        }
      }
      else if ( recordIndex > 0 && values.next() )
      {
        // Loop through the result set to copy the values from the
        // result set to the result vector.
        for ( int i = 1; i <= N; i++ )
        {
          Object value = values.getObject( i );
          result.addElement( value );
        }
      }
      else
      {
	// deal with an error: tbd
      }

      values.close();
      statement.close();
    }
    catch ( SQLException sqlException )
    {
      LogClient.printStackTrace( sqlException );
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Indicate whether the supplied <i>user</i> is the DB owner.
   */

  public boolean isOwner( String user )
  {
    boolean result = false;
    String query = ("SELECT l.name " + 
                    "FROM master..sysdatabases d, master..syslogins l " +
                    "WHERE d.name = '" + dbName + "' AND d.suid = l.suid");

    try
    {
      // Excecute the following SQL to get a single value which is the
      // name of the owner of the DB.
      Statement statement = connection.createStatement();
      ResultSet resultSet= statement.executeQuery( query );

      // Queue up the one and only row, so values can be extracted.
      resultSet.next();

      String owner = resultSet.getString( 1 );

      resultSet.close();
      statement.close();

      if ( owner.equals( user ) )
      {
	result = true;
      }
      else
      {
	result = false;
      }
    }
    catch ( SQLException sqlException )
    {
      LogClient.printStackTrace( sqlException );
    }
    return result;
  }

  /****************************************************************************/
  /**
   * Determine if a row is locked.
   *
   * @param keyValue  the row's unique obervation id
   */

  public boolean isRowLocked( String tableName, Object keyValue )
  {
    // Predispose the result to unlocked.
    boolean result = false;

    // Filter out all tables except the target table.
    if ( tableName.equals( "target" ) && 
	 keyValue instanceof Integer )
    {
      // Query the database to see if the record is locked.
      int obsid = ((Integer) keyValue).intValue();
      String query = ( "SELECT obsid FROM locked WHERE obsid =" + obsid );

      try
      {
	Statement statement = connection.createStatement();
	ResultSet resultSet = statement.executeQuery( query.toString() );

	while ( resultSet.next() )
	{
	  int i = resultSet.getInt( OBSID );
	  if( i > 0 )
	  {
	    result = true;
	  }
	}
      }
      catch ( SQLException sqlException )
      {
	LogClient.printStackTrace( sqlException );
      }
    }

    return result;
  }

  /****************************************************************************/
  /**
   * Determine if a column is writable.
   *
   * @param tableName the table
   * @param columnName  the column
   */

  public boolean isWritable( String tableName, String columnName )
  {
    boolean result;
    Properties permissions = (Properties) permissionsHashtable.get( tableName );

    // Determine if the cache has the permissions.
    if ( permissions == null )
    {
      // Fetch and cache the permissions from the database.
      permissions = getPermissions( tableName );
      permissionsHashtable.put( tableName, permissions );
    }

//System.out.println("Permissions: " + permissionsHashtable.toString());

    // Parse the permissions to determine if the column is writable.
    // First check to see if the permissions are unrestricted.
    result = checkPermission( permissions.getProperty( "All" ), "All" ) || 
      checkPermission( permissions.getProperty( columnName ), "Update" ) ||
      checkPermission( permissions.getProperty( "All" ), "Update" );
    return result;
  }

  /****************************************************************************/

  public int lookupObsID(String keyColumnName, Object keyColumnValue)
    throws SQLException
  {
    int result = 0;

    try
    {
      PreparedStatement statement = 
	connection.prepareStatement("SELECT obsid FROM target " + 
				    "WHERE " + keyColumnName + " = ?");

      statement.setObject(1, keyColumnValue);

      ResultSet resultSet = statement.executeQuery();

      if ( resultSet.next() )
      {
        result = resultSet.getInt("obsid");
      }
    }
    catch (SQLException sqlException)
    {
      LogClient.printStackTrace(sqlException);

      throw(sqlException);
    }

    return(result);
  }

  /****************************************************************************/
  /**
   * Examine a call statement to see if any warnings were generated.
   * Throw an SQL exception using the warning text as the exception
   * message.
   *
   * @param statement The SQL executable (expression or stored procedure).
   *
   * @throws SQLException The SQL executable generated warning text.
   */

  private void processWarnings( Statement statement )
    throws SQLException
  {
    String message = null;
    SQLWarning warning = statement.getWarnings();
    
    while ( warning != null )
    {
      if ( message == null )
      {
	message = warning.getMessage();
      }
      else
      {
	message += warning.getMessage();
      }
      warning = warning.getNextWarning();
    }
    if ( message != null )
    {
      throw new SQLException( "SQL failure: " + message );
    }
  }

  /****************************************************************************/
  /**
   * Reset the permissions by emptying out the hashtable containing
   * the current sets of permissions.
   */

  public void resetPermissions()
  {
    permissionsHashtable = new Hashtable<String,Properties>();
  }

  /****************************************************************************/
  /**
   * Set the current database identifier.
   */

  public void setDBName( String dbName )
  {
    this.dbName = dbName;
  }

  /****************************************************************************/
  /**
   * Set the current database description.
   */

  public void setDescription( String description )
  {
    this.description = description;
  }

  /****************************************************************************/
  /**
   * Set the current db username.
   */

  private void setKeyValue(Object index) throws SQLException
  {
    formsPreparedStatement.setObject(1, index);
  }

  /****************************************************************************/
  /**
   * Set the current db password.
   */

  public void setPassword( char[] password )
  {
    this.password = password;
  }


  /****************************************************************************/



  public void setServer( String server )
  {
    // Save the server name 
    this.server = server;
  }

  /****************************************************************************/
  /**
   * Set the current interfaces file for sybase.
   */

  public void setInterfacesFilename( String interfacesFilename )
  {
    this.interfacesFilename = interfacesFilename;
  }

  /****************************************************************************/
  /**
   * Set the current db username.
   */

  public void setUser( String user )
  {
    this.user = user;
  }

  /****************************************************************************/
  /**
   * Setup a stored procedure's parameters. Inputs will be used to set
   * placeholder values and outputs will be registered.
   */

  private void setupParameters( CallableStatement call,
				Parameter parameters,
				int startIndex )
    throws SQLException
  {
    Object value;
    int parameterType;
    int dbType;
    int N = parameters.size();
    for ( int i = startIndex; i < N; i++ )
    {
      parameterType = parameters.getType( i );
      dbType = parameters.getDBType( i );

      // Seed input values for IN and INOUT parameters.
      if ( parameterType == Parameter.IN || parameterType == Parameter.INOUT )
      {
	// Seed placeholder values.
	value = parameters.get( i );

	boolean debug = Editor.getInstance().isDebug();
	if ( debug )
	{
	  System.out.println( "Parameter " + parameters.getName(i) +
			      "(" + i + "): " + value );
	}

	if ( value == null )
	{
	  // Seed with a null value.
	  call.setNull( i + 1, dbType );
	}
	else
	{
	  // Seed with a non-null value.
	  switch ( dbType )
	  {
	  case Types.CHAR:
	  case Types.VARCHAR:
	    call.setString( i + 1, (String) value );
	    break;

	  case Types.DATE:
	    call.setDate( i + 1, (Date) value );
	    break;
	    
	  case Types.DOUBLE:
	    call.setDouble( i + 1, ((Double) value).doubleValue() );
	    break;
	    
	  case Types.FLOAT:
	    call.setFloat( i + 1, ((Float) value).floatValue() );
	    break;

	  case Types.INTEGER:
	    call.setInt( i + 1, ((Integer) value).intValue() );
	    break;

	  case Types.TIMESTAMP:
	    call.setTimestamp( i + 1, (Timestamp) value );
	    break;
	    
	  default:
	    // Deal with an internal software error.
	    throw new SQLException( "Unsupported type for field: " + 
				    parameters.getName( i ) );
	  }
	}
      }

      // Register the outputs.  Set the placeholder index to account
      // for the return code and the input parameters.
      if ( parameterType == Parameter.INOUT || parameterType == Parameter.OUT )
      {
	call.registerOutParameter( i + 1, dbType );
      }
    }
  }

  /****************************************************************************/
  /**
   *  Update one or more cells in the database using <i>expression</i>.
   */

  public void update( String expression )
    throws UpdateFailedException
  {
    // Put the data to the database.
    try 
    {
      Statement statement = connection.createStatement();
      executeUpdate( statement, expression );
    }
    catch ( SQLException se ) 
    {
      String message = "Database update failed for: " + expression + ".\n" +
	"Reason: " + se.getMessage();
      LogClient.logMessage( message );
      throw new UpdateFailedException( message );
    }
  }

  /****************************************************************************/
  /**
   *  Execute an update on table <i>tableName</i> setting each column,
   *  the key in <i>columns</i>, to the cooresponding value, where the
   *  <i>keyColumnName</i> is equal to <i>keyColumnValue</i>.
   */

  public void update(String tableName, Vector<String> columns, 
                     String keyColumnName, Object keyColumnValue)
    throws ConfigurationException, UpdateFailedException
  {
    StringBuffer sqlString = new StringBuffer();


    sqlString.append("UPDATE ");
    sqlString.append(tableName);
    sqlString.append(" SET ");

    for (int i = 0; i < columns.size(); i++)
    {
      String columnName = (String) columns.elementAt(i);
      sqlString.append(columnName);

      if ( i < (columns.size() - 1) )
      {
        sqlString.append(" = ?, ");
      }
      else
      {
        sqlString.append(" = ? ");
      }
    }

    sqlString.append("WHERE ");
    sqlString.append(keyColumnName);
    sqlString.append(" = ?");
    //System.out.println(sqlString);

    try 
    {
      PreparedStatement statement = 
         connection.prepareStatement( sqlString.toString() );

      int index = 1;

      for (int i = 0; i < columns.size(); i++)
      {
        String columnName = (String) columns.elementAt(i);

        DatabaseKey databaseKey = new DatabaseKey( tableName, columnName );

        ColumnEntry columnEntry = 
            DatabaseConfiguration.getInstance().getColumnEntry( databaseKey );

        Object columnValue = model.getValueAt( keyColumnValue, columnEntry );

        // Determine if we need to handle a null value.
        if (columnValue == null)
        {
          // Yes.  Prepare the value with a null.
          statement.setNull( index++, getType(tableName, columnName) );
        }
        else
        {
          // We don't.  Prepare the statement with a non-null value.
          statement.setObject( index++, columnValue );
        }
      }

      // Insert a value for the WHERE clause placeholder, then execute
      // the statement.
      statement.setObject( index, keyColumnValue );
      executeUpdate( statement );

      // Flush the cached data at this row forcing a subsequent
      // re-read from the database.  This is to support catching any
      // DB update side effects.
      model.invalidateRow( tableName, keyColumnValue );
    }
    catch ( Exception sqlException ) 
    {
      String message = "Database update failed with reason: \n" +
	sqlException.getMessage();
      throw new UpdateFailedException( message );
    }
  }

  /****************************************************************************/

}

/******************************************************************************/
