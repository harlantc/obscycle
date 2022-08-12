package db;
/*
  Copyrights:

  Copyright (c) 2000-2014,2019 Smithsonian Astrophysical Observatory

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

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.io.IOException;
import java.util.Vector;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;


import ascds.LogMessage;
import info.*;

public class DBTriggerToo 
{

  private boolean  showDebug;
  private Database database;



  /** 
    * Constructor
    * @param triggerTooDataPath   directory path to TOO Trigger files
    * @param showDebug  boolean value, if true then log debug messages to file
    * @exception IOException thrown on errors
    */
  public DBTriggerToo(String triggerTooDataPath,
	boolean showDebug ) 
	throws IOException
  {
    this.showDebug = showDebug;
    String databaseName = "axafocat";
    database = new Database(triggerTooDataPath,databaseName,showDebug,true);
  }

  /** 
    * Constructor
    * @param userName  database user name
    * @param userPwd   database user password
    * @param triggerTooDataPath   directory path to TOO Trigger files
    * @param showDebug  boolean value, if true then log debug messages to file
    * @exception IOException thrown on errors
    */
  public DBTriggerToo(String userName,String userPwd,String triggerTooDataPath,
	boolean showDebug ) 
	throws IOException
  {
    this.showDebug = showDebug;
    String databaseName = "axafocat";
    database = new Database(userName,userPwd,triggerTooDataPath,databaseName,showDebug,true);
  }

  /**
    * Verify that username/password can create a valid connection.
    * @exception SQLException thrown on sql errors
    */ 
  public void isValidConnection() throws SQLException {

    String message = new String("");
    Connection connection = null;
    try {
      connection = database.getConnection();
      connection.close();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database. ");
    }

  }

  /**
    * Check if current user has privileges for the manager role
    * @return boolean true  if user has too_manager_role
    * @exception SQLException thrown on sql errors
    */
  public boolean isManagerRole() throws SQLException
  {
    boolean retval = false;
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;

    String sqlStmt = new String("exec sp_helprotect tootrigger," +
        database.getUser() );

    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to the database.");
    }

   
    try {
      pstmt = connection.prepareStatement(sqlStmt);
      if (showDebug) {
        LogMessage.println( sqlStmt + "\n");
      }
      rs = pstmt.executeQuery();
      while (rs.next()) {
        String theAction = rs.getString(4);
        String theColumn = rs.getString(6);
        if ( theAction.equalsIgnoreCase("update") &&
             theColumn.equalsIgnoreCase("override_ra") )  {
          retval = true;
        }
      }

    } catch (Exception sqle) {
      LogMessage.printException(sqle);
      throw new SQLException("An error occured in retrieving the data: " +
         sqle.getMessage());
    } finally {
      try {
        if (rs != null)
          rs.close();
        if (pstmt != null)
          pstmt.close();
        if (connection != null)
          connection.close();
      } catch (SQLException sqle) {
        LogMessage.printException(sqle);
        LogMessage.println("SQLException during close(): " + sqle.toString());
      }
    }
    return retval;
  }

  /**
    * Check if we're accessing the read-only database 
    * @return boolean true  if user has read_only access
    * @exception SQLException thrown on sql errors
    */
  public boolean isReadOnly() throws SQLException
  {
    boolean retval = false;
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;

    String sqlStmt = new String("exec sp_helprotect tootrigger_comment," +
        database.getUser() );

    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to the database.");
    }

   
    try {
      pstmt = connection.prepareStatement(sqlStmt);
      if (showDebug) {
        LogMessage.println( sqlStmt + "\n");
      }
      rs = pstmt.executeQuery();
      retval = true;
      while (rs.next()) {
        String theAction = rs.getString(4);
        String theColumn = rs.getString(6);
        if ( theAction.equalsIgnoreCase("update") &&
             theColumn.equalsIgnoreCase("comments") )  {
          retval = false;
        }
      }

    } catch (Exception sqle) {
      LogMessage.printException(sqle);
      throw new SQLException("An error occured in retrieving the data: " +
         sqle.getMessage());
    } finally {
      try {
        if (rs != null)
          rs.close();
        if (pstmt != null)
          pstmt.close();
        if (connection != null)
          connection.close();
      } catch (SQLException sqle) {
        LogMessage.printException(sqle);
        LogMessage.println("SQLException during close(): " + sqle.toString());
      }
    }
    return retval;
  }
  /**
    * Get AO cycles for search options
    * @return int cycle
    * @exception SQLException thrown on sql errors
    */
  public Vector<String> getAOCycles() throws SQLException
  {
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    Vector<String> aos = new Vector<String>();

    String sqlStmt = new String("select distinct obs_ao_str from tootrigger,target where tootrigger.obsid = target.obsid order by obs_ao_str desc");

    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to the database.");
    }
   
    try {
      pstmt = connection.prepareStatement(sqlStmt);
      if (showDebug) {
        LogMessage.println( sqlStmt + "\n");
      }
      rs = pstmt.executeQuery();
      while (rs.next()) {
        aos.add(rs.getString(1));
      }

    } catch (Exception sqle) {
      LogMessage.printException(sqle);
      throw new SQLException("An error occured in retrieving the max cycle: " +
         sqle.getMessage());
    } finally {
      try {
        if (rs != null)
          rs.close();
        if (pstmt != null)
          pstmt.close();
        if (connection != null)
          connection.close();
      } catch (SQLException sqle) {
        LogMessage.printException(sqle);
        LogMessage.println("SQLException during close(): " + sqle.toString());
      }
    }
    return aos;
  }


  /** 
    * inserts a new tootrigger entry in the database 
    * @param obsid   obsid
    * @param version   version
    * @param urgency  response window type such as SLOW,MEDIUM,FAST      
    * @param origTargetName  original target name
    * @param targetName  if editable, new target name 
    * @param ra  if editable, new right ascension
    * @param dec  if editable, new declination
    * @param isEditable  true if user could modify target name/coordinates
    * @param comments  comments from user
    * @exception SQLException thrown on sql errors
    */
  public void insertNewTrigger(Integer obsid, Integer version,
	String urgency,
        String origTargetName, String targetName,
        Double ra, Double dec,boolean isEditable,
        String comments)
        throws SQLException
  {
    Connection connection = null;
    CallableStatement cstmt=null;
    String message = new String("");
    boolean  stat = true;
    int triggerID = 0;
    int commentID = 0;
    int idx;


    LogMessage.println("Insert Parameters: \ndeclare @id int,@cmt_id int\nexec tootrigger_ingest " + obsid + "," + version + ",\'" + urgency + "\',\'" + targetName + "\'," + ra.toString() + "," + dec.toString() + "\n, @id output, @cmt_id output");
    LogMessage.println("update tootrigger_comment set comments =\n\"" + comments + "\" \nwhere comment_id = @cmt_id\n");
    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database. ");
    }
    try {
      String command = "{? = call tootrigger_ingest(?,?,?,?,?,?,?,?)}";
      cstmt = connection.prepareCall (command);

      // Register input parameters
      idx = 2;
      cstmt.setInt(idx++, obsid.intValue());
      cstmt.setInt(idx++, version.intValue());
      cstmt.setString(idx++, urgency);

      if (origTargetName ==  null  || 
          !origTargetName.equalsIgnoreCase(targetName) ) {
        cstmt.setString(idx++, targetName);
      } else {
        cstmt.setNull(idx++,Types.VARCHAR);
      }
      if (isEditable) {
        cstmt.setDouble(idx++, ra.doubleValue());
        cstmt.setDouble(idx++, dec.doubleValue());
      } else {
        cstmt.setNull(idx++, Types.DOUBLE);
        cstmt.setNull(idx++, Types.DOUBLE);
      }

      // Register output parameter
      cstmt.registerOutParameter(idx++, Types.INTEGER);
      cstmt.registerOutParameter(idx++, Types.INTEGER);

      // executeQuery returns values via a resultSet
      ResultSet rs = cstmt.executeQuery();
      while (rs.next()) {
        // get value returned by myudr
        int status = rs.getInt(1);
        if (status < 0) {
          throw new SQLException ("Invalid return code from trigger TOO ingest procedure");
         }
      }
      // Retrieve OUT parameters from myudr
      idx = idx - 2;
      triggerID = cstmt.getInt(idx++);
      commentID = cstmt.getInt(idx++);
      rs.close();
      cstmt.close();
      updateComments(commentID,null,comments,connection);
    }
    catch (Exception exc) {
      message = "Unable to insert new tootrigger record in database for ";
      message += obsid.toString() + " request=" + version.toString();
      message += "\n" + exc.getMessage();
      LogMessage.printException(exc);
      throw new SQLException(message);
    }
    finally
    {
      try {
        if (connection != null)
          connection.close();
      }
      catch (SQLException sqle) {
        LogMessage.printException(sqle);
      }
    }

    return;
  }



  /**
    * Insert a new comment for a trigger too
    * @param triggerID  database id for tootrigger entry
    * @param comment  new comment to insert
    * @param cmtStatus  status of new comment (draft?)
    * @exception SQLException thrown on sql errors
    */
  public void insertComment(Integer triggerID,String cmtStatus,String comment)
     throws SQLException
  {
    String message = new String("");
    Connection connection = null;
    int commentID = 0;

    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database. ");
    }
    try {
      String command = "{? = call tootrigger_comment_ingest(?,?)}";
      CallableStatement cstmt = connection.prepareCall (command);

      // Register input parameters
      cstmt.setInt(2, triggerID.intValue());
      cstmt.registerOutParameter(3, Types.INTEGER);
      ResultSet rs = cstmt.executeQuery();
      while (rs.next())
      {
        // get value returned by the stored procedure
        int status = rs.getInt(1);
        if (status < 0) {
         throw new SQLException ("Unable to create new comment entry for Trigger TOO record.");
        }
      }
      commentID = cstmt.getInt(3);
      updateComments(commentID,cmtStatus,comment,connection);
    }
    catch (SQLException exc) {
       LogMessage.printException(exc);
       throw new SQLException(exc.getMessage());
    }
    finally {
      try {
        if (connection != null) {
          connection.close();
        }
      }
      catch (SQLException exc) {
        LogMessage.printException(exc);
      }
    }

    return;
  }


  public void updateComments(int commentID, String cmtStatus,String comments)
	throws SQLException
  {
     updateComments(commentID,cmtStatus,comments,database.getConnection());
  }

  /**
    * Update text field for the trigger too comment record
    * @param commentID  comment id of newly created entry in tootrigger_comment
    * @param cmtStatus  status of new comment( draft ?)
    * @param comments  comment text field
    * @param connection  database connection
    * @exception SQLException thrown on sql errors
    */
  public void updateComments(int commentID, String cmtStatus,String comments,
  	Connection connection)
	throws SQLException
  {
    String cStatus = cmtStatus; 
  
    // update the  comments
    if (comments != null) {
      comments = comments.replace('\"','\'');
    }

    String sqlStmt = "update tootrigger_comment set status=?,comments = ? where comment_id = ? ";
    PreparedStatement pstmt = connection.prepareStatement(sqlStmt);
    if (cStatus == null || cStatus.length() <= 0) {
      cStatus = "Complete";
    }
    pstmt.setString(1,cStatus);
    pstmt.setString(2,comments);
    pstmt.setInt(3,commentID);
    pstmt.executeUpdate();
    database.processWarnings(pstmt);
      
    return ;
  }       

  /**
    * Retrieve the comment history for a too entry and set the field in
    * the too record.
    * @param triggerID  database id for tootrigger entry
    * @param tooEntry  associated TriggerTOOEntry record
    * @exception SQLException thrown on sql errors
    */
  public void getCommentHistory(Integer triggerID,TriggerTooEntry tooEntry) 
	throws SQLException
  {

    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    int ii;
    String userName;

    String sqlStmt = "select comment_id,tootrigger_id,creation_date, " +
        "user_name,status,comments" +
	" from tootrigger_comment where tootrigger_id=" + triggerID +
        " order by creation_date desc";

    try {
      connection = database.getConnection();
      userName = database.getUser();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database. ");
    }
    try {
      pstmt = connection.prepareStatement(sqlStmt);
      if (showDebug) {
        LogMessage.println( sqlStmt + "\n");
      }
      rs = pstmt.executeQuery();
      tooEntry.clearCommentHistory();
      while (rs.next()) {
	ii=1;
	CommentHistory chist = new CommentHistory();
        chist.setCommentID(rs.getInt(ii++));
	chist.setTriggerID(rs.getInt(ii++));
	chist.setCreationDate(rs.getString(ii++));
	chist.setUserName(rs.getString(ii++));
	chist.setStatus(rs.getString(ii++));
	chist.setComment(rs.getString(ii++));
        if (chist.getStatus().equalsIgnoreCase(TriggerTooConstants.DRAFTSTATUS)
           && chist.getUserName().equalsIgnoreCase(userName)) {
          tooEntry.setCurrentComment(chist);
        }
        else {
          tooEntry.addCommentHistory(chist);
        }
      }
    } 
    catch (Exception sqle) {
      LogMessage.printException(sqle);
      throw new SQLException("An error occured retrieving comments from the database: " + sqle.getMessage());
    }  
    finally {
      try {
	if (rs != null)
	  rs.close();
	if (pstmt != null)
	  pstmt.close();
	if (connection != null) 
	  connection.close();
      }
      catch (SQLException sqle) {
        LogMessage.printException(sqle);
      }
    }
    return ;
  }

  /**
    * Retrieve a single tootrigger record by the trigger id
    * @param triggerID  database id for tootrigger entry
    * @return TriggerTOOEntry record
    * @exception SQLException thrown on sql errors
    */
  public TriggerTooEntry getTriggerTooEntry(Integer triggerID) 
       	throws SQLException
  {
    String query = new String("");

    if (triggerID != null && triggerID.intValue() > 0) {
      query = " and tootrigger_id = " + triggerID;
    }
    Vector<TriggerTooEntry> triggerList = getTriggerTooList(query);
    TriggerTooEntry tooEntry = new TriggerTooEntry();
    if (triggerList != null && triggerList.size() == 1) {
      tooEntry = (TriggerTooEntry)triggerList.get(0);
    }
    else {
      throw new SQLException("Unable to retrieve single record for TOO trigger " + triggerID);
    }
    return tooEntry;
  }
     
   
  /**
    * Retrieve a vector of all TOO trigger records from the database 
    * @param aoStr  cycles to search, comma separated
    * @param sortBy sort criteria
    * @return vector of TriggerTOOEntry records
    * @exception SQLException thrown on sql errors
    */
  public Vector<TriggerTooEntry> getTriggerTooList(String aoStr,String sortBy) 
	throws SQLException
  {
    String query = new String("");
    if (aoStr != null && aoStr.length() > 0) {
      query = "and obs_ao_str in "  + aoStr;
    }

    String orderBy = new String("order by submission_date desc");
    if (sortBy != null ) {
       if (sortBy.indexOf("subdate") >= 0)
           orderBy = "order by submission_date desc";
       else if (sortBy.indexOf("status") >= 0)
           orderBy = "order by tootrigger.status";
       else if (sortBy.indexOf("obsid") >= 0)
           orderBy = "order by tootrigger.obsid";
       else if (sortBy.indexOf("seqnum") >= 0)
           orderBy = "order by vt.seq_nbr";
       else if (sortBy.indexOf("pi") >= 0)
           orderBy = "order by lower(vt.last)";

    }
    query += " " + orderBy;

    Vector<TriggerTooEntry> triggerList = this.getTriggerTooList(query);
    return triggerList;
  }

  /**
    * Retrieve a vector of all TOO trigger records from the database 
    * @param query  search criteria for TOO trigger records
    * @return vector of TriggerTOOEntry records
    * @exception SQLException thrown on sql errors
    */
  public Vector<TriggerTooEntry> getTriggerTooList(String query) 
	throws SQLException
  {

    Vector<TriggerTooEntry> triggerList = new Vector<TriggerTooEntry>();
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    int ii;
    double dval;
    Timestamp tdate;
    Date ldate;
    SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String sqlStmt = "select tootrigger.tootrigger_id,tootrigger.obsid" +
	",vt.seq_nbr, tootrigger.request,tootrigger.submission_date"  +
        ",tootrigger.status,tootrigger.coordinator" +
	",tootrigger.response_type" +
       	",tootrigger.target_name,tootrigger.ra,tootrigger.dec" +
	",tootrigger.override_target_name,tootrigger.override_ra" +
        ",tootrigger.override_dec" +
	",vt.obs_ao_str,vt.last,vt.alternate_group_name" +
	",vt.approved_count,vt.proposal_id,vt.prop_num" +
        ",vt.lts_lt_plan,vt.soe_st_sched_date,vt.start,vt.stop " +
	",fp.status,fp.comment" +
	" from tootrigger ,view_too vt,axafobstat..fast_proc fp " +
	" where tootrigger.obsid = vt.obsid and tootrigger.obsid *= fp.obsid " + query ;


    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database. ");
    }
    try {
      pstmt = connection.prepareStatement(sqlStmt);
      if (showDebug) {
        LogMessage.println( sqlStmt + "\n");
      }
      rs = pstmt.executeQuery();
      while (rs.next()) {
	ii=1;
	TriggerTooEntry trig = new TriggerTooEntry();
	trig.setTriggerID(rs.getInt(ii++));
	trig.setObsid(rs.getInt(ii++));
	trig.setSequenceNumber(rs.getString(ii++));
	trig.setVersion(rs.getInt(ii++));
        tdate = rs.getTimestamp(ii++);
        if (!rs.wasNull()) {
	  trig.setSubmissionDate(sdtf.format(tdate));
        }
        else {
	  trig.setSubmissionDate("");
        }
	trig.setStatus(rs.getString(ii++));
	trig.setCoordinator(rs.getString(ii++));
	trig.setUrgency(rs.getString(ii++));
	trig.setTargetName(rs.getString(ii++));
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
          trig.setRA(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  trig.setDec(dval);
        }
	trig.setInitCoords();
	trig.setOverrideTargetName(rs.getString(ii++));
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  trig.setOverrideRA(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  trig.setOverrideDec(dval);
        }
	trig.setOverrideCoords(trig.getOverrideRA().toString(),
                               trig.getOverrideDec().toString());
	trig.setCycle(rs.getString(ii++));
	trig.setPI(rs.getString(ii++));
	trig.setAlternateGroupName(rs.getString(ii++));
	trig.setAlternateApprovedCount(rs.getInt(ii++));
	trig.setProposalID(rs.getInt(ii++));
	trig.setProposalNumber(rs.getString(ii++));

        ldate = rs.getDate(ii++);
        if (rs.wasNull()) {
	  trig.setLTSDate("");
        }
        else {
	  trig.setLTSDate(sdf.format(ldate));
        }
        tdate = rs.getTimestamp(ii++);
        if (!rs.wasNull()) {
	  trig.setSTSDate(sdtf.format(tdate));
        }
        else {
	  trig.setSTSDate("");
        }
	trig.setCXCStart(rs.getString(ii++));
	trig.setCXCStop(rs.getString(ii++));
	trig.setFastProc(rs.getString(ii++));
	trig.setFastProcComment(rs.getString(ii++));

        triggerList.add(trig);
      }
	    
	    
    } catch (Exception sqle) {
       LogMessage.printException(sqle);
       throw new SQLException("An error occured retrieving tootrigger record: " + sqle.getMessage());
    }  finally {
       try {
	 if (rs != null)
	   rs.close();
	 if (pstmt != null)
	   pstmt.close();
	 if (connection != null) 
	   connection.close();
       } catch (SQLException sqle) {
         LogMessage.printException(sqle);
       }
     } // finally


      return(triggerList);
    }

  /**
    * Update the coordinator field for a trigger too record
    * @param triggerID  database id for tootrigger entry
    * @param coordinator  assigned coordinator
    * @exception SQLException thrown on sql errors
    */ 
  public void updateCoordinator(Integer triggerID, String coordinator)
	throws SQLException
  {
    Connection connection = null;
  
    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database. ");
    }
    try {
      // now update the  comments
      String sqlStmt = "update tootrigger set coordinator= ? where tootrigger_id = ?";
      PreparedStatement pstmt = connection.prepareStatement(sqlStmt);
      pstmt.setString(1,coordinator);
      pstmt.setInt(2,triggerID.intValue());
      pstmt.executeUpdate();
      database.processWarnings(pstmt);
          
    }
    catch (SQLException exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to update Coordinator for TOO Trigger id= " + triggerID.toString() + ". \n" + exc.getMessage() );
    }
    finally {
      try {
        if (connection != null) {
          connection.close();
        }
      }   
      catch (SQLException exc) {
        LogMessage.printException(exc);
      }
    }
  }

  /**
    * Update the target override values for a given entry
    * @param triggerID   database id for tootrigger entry
    * @param targetName  new target name
    * @param ra  new right ascension
    * @param dec new declination
    * @exception SQLException thrown on sql errors
    */
  public void updateOverrideValues(Integer triggerID, 
	String targetName,Double ra,Double dec )
	throws SQLException
  {
    Connection connection = null;
  
    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database. ");
    }
    try {
      // now update the  comments
      String sqlStmt = "update tootrigger set override_target_name= ?," +
       " override_ra= ?, override_dec= ? where tootrigger_id = ?";
      PreparedStatement pstmt = connection.prepareStatement(sqlStmt);
      pstmt.setString(1,targetName);
      LogMessage.println("Setting override values for " + triggerID + "," + ra + "," + dec);
      if (ra == TriggerTooConstants.EMPTY_VALUE) {
        pstmt.setNull(2,Types.DOUBLE);
      } else {
        pstmt.setDouble(2,ra.doubleValue());
      }
      if (ra == TriggerTooConstants.EMPTY_VALUE) {
        pstmt.setNull(3,Types.DOUBLE);
      } else {
        pstmt.setDouble(3,dec.doubleValue());
      }
      pstmt.setInt(4,triggerID.intValue());
      pstmt.executeUpdate();
      database.processWarnings(pstmt);
          
    }
    catch (SQLException exc) {
       LogMessage.printException(exc);
       throw new SQLException("Unable to update override coordinates for TOO Trigger id= " + triggerID.toString() + ". \n" + exc.getMessage());
    }
    finally {
      try {
        if (connection != null) {
          connection.close();
        }
      }   
      catch (SQLException exc) {
        LogMessage.printException(exc);
      }
    }
  }

  /**
    * Update status field for the trigger too record
    * @param triggerID   database id for tootrigger entry
    * @param status status value
    * @exception SQLException thrown on sql errors
    */
  public void updateStatus(Integer triggerID, String status)
	throws SQLException
  {
  
    // update the  status
    Connection connection = null;
  
    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database. ");
    }
    try {
      String sqlStmt = "update tootrigger set status = ? where tootrigger_id = ? ";
      PreparedStatement pstmt = connection.prepareStatement(sqlStmt);
      pstmt.setString(1,status);
      pstmt.setInt(2,triggerID.intValue());
      pstmt.executeUpdate();
      database.processWarnings(pstmt);
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to update status for TOO Trigger id= " + triggerID.toString()  + ". \n" + exc.getMessage());
    }
    finally {
      try {
        if (connection != null) {
          connection.close();
        }
      }   
      catch (SQLException exc) {
        LogMessage.printException(exc);
      }
    }
      
    return ;
  }
  /**
    * Update fastproc field for the trigger too record
    * @param obsid   database id for tootrigger entry
    * @param fpstatus status value
    * @param fpcomment  fast process comment
    * @exception SQLException thrown on sql errors
    */
  public void updateFastProc(Integer obsid, String fpstatus,String fpcomment)
	throws SQLException
  {
  
    // update the  status
    Connection connection = null;
  
    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database. ");
    }
    try {
      String sqlStmt = "{ ? = call too_update_fast_proc(?,?,?) }";
      CallableStatement cstmt = connection.prepareCall(sqlStmt);
      cstmt.registerOutParameter( 1, Types.INTEGER );
      cstmt.setInt(2,obsid.intValue());
      cstmt.setString(3,fpstatus);
      cstmt.setString(4,fpcomment);
      cstmt.execute();
      int status=cstmt.getInt(1);
      if (status < 0) {
         database.processWarnings(cstmt);
      }

    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to update Fast Proc for TOO Obsid id= " + obsid.toString()  + ". \n" + exc.getMessage());
    }
    finally {
      try {
        if (connection != null) {
          connection.close();
        }
      }   
      catch (SQLException exc) {
        LogMessage.printException(exc);
      }
    }
      
    return ;
  }
  /**
    * Update urgency (response type) field for the trigger too record
    * @param triggerID   database id for tootrigger entry
    * @param responseType new urgency/response type
    * @exception SQLException thrown on sql errors
    */
  public void updateUrgency(Integer triggerID, String responseType)
	throws SQLException
  {
  
    // update the  response_type
    Connection connection = null;
  
    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database. ");
    }
    try {
      String sqlStmt = "update tootrigger set response_type = ? where tootrigger_id = ? ";
      PreparedStatement pstmt = connection.prepareStatement(sqlStmt);
      pstmt.setString(1,responseType);
      pstmt.setInt(2,triggerID.intValue());
      pstmt.executeUpdate();
      database.processWarnings(pstmt);
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to update response window for TOO Trigger id= " + triggerID.toString()  + ". \n" + exc.getMessage());
    }
    finally {
      try {
        if (connection != null) {
          connection.close();
        }
      }   
      catch (SQLException exc) {
        LogMessage.printException(exc);
      }
    }

    return ;

  }
  public String getDBUser()
  {
     String retval = "";
     if (database != null) {
       retval = database.getUser();
     }
     return retval;
  }
  public String getDBServer() 
  {
     String retval = "";
     if (database != null) {
       retval = database.getServer();
     }
     return retval;
  }
  public String getProposalServer() {
     String propServer = null;
     if (database != null)
        propServer= database.getProposalServer();
     return propServer;
  }

  public String getDBPwd()
  {
     String retval = "";
     if (database != null) {
       retval = database.getPassword();
     }
     return retval;
  }


}
 
    


