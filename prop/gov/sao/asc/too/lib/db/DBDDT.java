package db;
/*
  Copyrights:

  Copyright (c) 2014, 2020 Smithsonian Astrophysical Observatory

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

public class DBDDT 
{

  private boolean  showDebug;
  private Database database;
  private String   dataPath; 


  /** 
    * Constructor
    * @param triggerTooDataPath   directory path to DDT files
    * @param showDebug  boolean value, if true then log debug messages to file
    * @exception IOException thrown on io errors
    */
  public DBDDT(String triggerTooDataPath,
	boolean showDebug ) 
	throws IOException
  {
    this.showDebug = showDebug;
    String databaseName = "proposal";
    database = new Database(triggerTooDataPath,databaseName,showDebug,false);
    dataPath = triggerTooDataPath;
  }

  /** 
    * Constructor
    * @param userName  database user name
    * @param userPwd   database user password
    * @param triggerTooDataPath   directory path to DDT files
    * @param showDebug  boolean value, if true then log debug messages to file
    * @exception IOException thrown on io errors
    */
  public DBDDT(String userName,String userPwd,String triggerTooDataPath,
	boolean showDebug ) 
	throws IOException
  {
    this.showDebug = showDebug;
    String databaseName = "proposal";
    database = new Database(userName,userPwd,triggerTooDataPath,databaseName,showDebug,false);
    dataPath = triggerTooDataPath;
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
  public String getDBServer()
  {
     String retval = "";
     if (database != null) {
       retval = database.getServer();
     }
     return retval;
  }
  public String getDBUser()
  {
     String retval = "";
     if (database != null) {
       retval = database.getUser();
     }
     return retval;
  }
  public String getDBPwd()
  {
     String retval = "";
     if (database != null) {
       retval = database.getPassword();
     }
     return retval;
  }
  public String getDDTServer()
  {
     String retval = "";
     if (database != null) {
       retval = database.getDDTServer();
     }
     return retval;
  }

 

  /**
    * Check if current user has privileges for the manager role
    * @return boolean true  if user has too_manager_role
    * @exception SQLException thrown on sql errors
    */
  public boolean isDDTManagerRole() throws SQLException
  {
    boolean retval = false;
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;

    String sqlStmt = new String("exec sp_helprotect ddtmanager," +
        database.getUser() );
    //String sqlStmt = new String("exec sp_displayroles" );

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
        //LogMessage.println( sqlStmt + "\n");
      }
      rs = pstmt.executeQuery();
      while (rs.next()) {
        String theAction = rs.getString(4);
        String theColumn = rs.getString(6);
        if ( theAction.equalsIgnoreCase("update") &&
             theColumn.equalsIgnoreCase("response_type") )  {
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
  public boolean isDDTReadOnly() throws SQLException
  {
    boolean retval = false;
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;

    String sqlStmt = new String("exec sp_helprotect ddtmanager_comment," +
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
        //LogMessage.println( sqlStmt + "\n");
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
    * Check if current user has privileges for the manager role
    * @return boolean true  if user has too_manager_role
    * @exception SQLException thrown on sql errors
    */
  public boolean isProp2ocatAllowed() throws SQLException
  {
    boolean retval = false;
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;

    String sqlStmt = new String("exec sp_helprotect  pub_retr_target_by_propnum, " +
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
        //LogMessage.println( sqlStmt + "\n");
      }
      rs = pstmt.executeQuery();
      while (rs.next()) {
        String theGrantee = rs.getString(2);
        String theAction = rs.getString(4);
        if ( theAction.equalsIgnoreCase("execute") &&
             theGrantee.equalsIgnoreCase("prop2ocat_too_prop_role") )  {
          retval = true;
        }
      }

    } catch (Exception sqle) {
      LogMessage.printException(sqle);
      throw new SQLException("An error occured in retrieving permissions : " +
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
    * Check if user can run the conflict check
    * @return boolean true  if user has permissions for conflicts 
    * @exception SQLException thrown on sql errors
    */
  public boolean isConflictAllowed() throws SQLException
  {
    boolean retval = false;
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;

    String sqlStmt = new String("exec sp_helprotect  pub_conflict_checker," +
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
        //LogMessage.println( sqlStmt + "\n");
      }
      rs = pstmt.executeQuery();
      while (rs.next()) {
        String theAction = rs.getString(4);
        String theColumn = rs.getString(6);
        if ( theAction.equalsIgnoreCase("execute") ) {
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

  public Vector<AOCycle> getAOCycleTotals() throws SQLException
  {
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    Vector<AOCycle> aos= new Vector<AOCycle>();
    int ii = 0;

    String sqlStmt = new String("create table #tmpao (ao_str varchar(10),ttime float NULL)\ncreate table #tmpaao (ao_str varchar(10),ttime float NULL)\ninsert #tmpao select ao_str,sum(prop_exposure_time) from proposal vp,target vt,ddtmanager dm where vp.proposal_id = vt.proposal_id and (convert(int,right(proposal_number, char_length(proposal_number)-4)) > 8000) and vp.proposal_id = dm.proposal_id and dm.status!='Request Withdrawn' group by ao_str\ninsert #tmpaao select ao_str,sum(approved_exposure_time) from proposal vp,target vt,ddtmanager dm where vp.proposal_id = vt.proposal_id and (convert(int,right(proposal_number, char_length(proposal_number)-4)) > 8000) and vp.proposal_id = dm.proposal_id and dm.status='Approved' and vt.status='accepted' group by ao_str\nselect distinct r.ao_str,r.ttime,a.ttime from #tmpao r,#tmpaao a where r.ao_str *= a.ao_str order by r.ao_str desc\ndrop table #tmpaao\ndrop table #tmpao");

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
        AOCycle ao = new AOCycle();
        ao.setAOCycle(rs.getString(1)); 
        ao.setTotalRequestedTime(rs.getDouble(2)); 
        ao.setTotalApprovedTime(rs.getDouble(3)); 
        aos.add(ao);
        LogMessage.println("DB added " + ao.getAOCycle());
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




  public Vector<String> getAOCycles() throws SQLException
  {
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    Vector<String> aos= new Vector<String>();
    int ii = 0;

    String sqlStmt = new String("select distinct ao_str from ddtmanager,view_ddt_proposal where ddtmanager.proposal_id = view_ddt_proposal.proposal_id order by ao_str desc");

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
    * Insert a new comment for a ddt 
    * @param proposalNumber  propsal number for ddtmanager entry
    * @param cmtStatus  status of comment for insert
    * @param comment  new comment to insert
    * @exception SQLException thrown on sql errors
    */
  public void insertComment(String proposalNumber,String cmtStatus,String comment)
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
      String command = "{? = call ddtmanager_comment_insert(?,?)}";
      CallableStatement cstmt = connection.prepareCall (command);

      // Register input parameters
      cstmt.setString(2, proposalNumber);
      cstmt.registerOutParameter(3, Types.INTEGER);
      cstmt.registerOutParameter(1, Types.INTEGER);
      ResultSet rs = cstmt.executeQuery();
      while (rs.next())
      {
        // get value returned by the stored procedure
        int status = rs.getInt(1);
        if (status < 0) {
         throw new SQLException ("Unable to create new comment entry for DDT Manager  record.");
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
    * @param cmtStatus  status of comment, draft
    * @param comments   comment text field
    * @param connection database connection
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

    String sqlStmt = "update ddtmanager_comment set status=?,comments = ? where comment_id = ? ";
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
    * Retrieve the comment history for a ddt entry and set the field in
    * the ddt record.
    * @param proposalID  database id for ddtmanager entry
    * @param ddtEntry  associated DDTManagerEntry record
    * @exception SQLException thrown on sql errors
    */
  public void getCommentHistory(Integer proposalID,DDTEntry ddtEntry) 
	throws SQLException
  {

    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    int ii;
    String userName;

    String sqlStmt = "select comment_id,proposal_id,creation_date, " +
        "user_name,status,comments" +
	" from ddtmanager_comment where proposal_id=" + proposalID +
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
      ddtEntry.clearCommentHistory();
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
          ddtEntry.setCurrentComment(chist);
        }
        else {
          ddtEntry.addCommentHistory(chist);
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
    * Retrieve a single ddt record by the proposal_id
    * @param proposalID  database id for ddtmanager entry
    * @return DDTEntry record
    * @exception SQLException thrown on sql errors
    */
  public DDTEntry getDDTEntry(Integer proposalID) 
       	throws SQLException
  {
    String query = new String("");

    if (proposalID != null && proposalID.intValue() > 0) {
      query = " and ddtmanager.proposal_id = " + proposalID;
    }
    Vector<DDTEntry> ddtList = retrieveDDTs(query,"");
    DDTEntry ddtEntry = new DDTEntry();
    if (ddtList != null && ddtList.size() == 1) {
      ddtEntry = (DDTEntry)ddtList.get(0);
    }
    else {
      throw new SQLException("Unable to retrieve single record for DDT Manager " + proposalID);
    }
    return ddtEntry;
  }
  /**
    * Retrieve a vector of all DDT manager records from the database 
    * Input is the gui query and sort by parameters
    * @param aoStr  cycles to search
    * @param statStr  status to search
    * @param sortBy   sort order
    * @param calFrom  start submission date to search
    * @param calTo   end submission date  to search
    * @return vector of DDTEntry records
    * @exception SQLException thrown on sql errors
    */
  public Vector<DDTEntry> getDDTList(String aoStr,String statStr,String sortBy,String calFrom, String calTo) 
	throws SQLException
  {
    String query = "";
    if (aoStr != null && aoStr.length() > 0) {
      query += " and proposal.ao_str in "  + aoStr + " ";
    }
    if (statStr != null && statStr.length() > 0) {
      query += " and ddtmanager.status in "  + statStr + " ";
    }
    if (calFrom != null && calFrom.length() > 1) 
      query += " and submission_date >= '" + calFrom + "'";
    if (calTo != null && calTo.length() > 1) 
      query += " and submission_date <= '" + calTo + "'";

    String orderBy = new String("order by submission_date desc");
    if (sortBy != null ) {
       if (sortBy.indexOf("subdate") >= 0) 
           orderBy = "order by submission_date desc";
       else if (sortBy.indexOf("status") >= 0) 
           orderBy = "order by ddtmanager.status";
       else if (sortBy.indexOf("pstat") >= 0) 
           orderBy = "order by proposal.status";
       else if (sortBy.indexOf("pi") >= 0) 
           orderBy = "order by lower(pi_last),pi_first";
       else 
           orderBy = "order by proposal_number";

    }
    LogMessage.println("DDT: query=" + query + " orderBy=" + orderBy);


    Vector<DDTEntry> ddtList = this.retrieveDDTs(query,orderBy);
    return ddtList;
  }
     
   
  /**
    * Retrieve a vector of all DDT manager records from the database 
    * @return vector of DDTEntry records
    * @exception SQLException thrown on sql errors
    */
  public Vector<DDTEntry> getDDTList() 
	throws SQLException
  {
      String query = new String("and (approval_email_date is null or ocat_propid is null)");
      String orderBy = new String("order by proposal_number");
      Vector<DDTEntry> ddtList = this.retrieveDDTs(query,orderBy);
      return ddtList;
  }

  /**
    * Retrieve a vector of all DDT records from the database 
    * @param query  search criteria for DDT records
    * @param orderBy  sort criteria
    * @return vector of DDTEntry records
    * @exception SQLException thrown on sql errors
    */
  private Vector<DDTEntry> retrieveDDTs(String query,String orderBy) 
	throws SQLException
  {

    Vector<DDTEntry> ddtList = new Vector<DDTEntry>();
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    int ii;
    double dval;
    Timestamp tdate;
    Date ldate;
    SimpleDateFormat sdtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String sqlStmt = "create table #ddtgt (proposal_id int,apptime float DEFAULT 0 NULL)\ninsert #ddtgt select distinct tgt.proposal_id, sum(tgt.approved_exposure_time) from proposal p, target tgt where p.proposal_id = tgt.proposal_id and (convert(int,right(proposal_number, char_length(proposal_number)-4)) > 8000) group by tgt.proposal_id\n";
    sqlStmt += " select ddtmanager.proposal_id," +
        "ddtmanager.status,ddtmanager.coordinator," +
	"ddtmanager.response_type, " +
	"proposal.proposal_number,pi_first,pi_last, pi_email," +
	"proposal.submission_date,proposal.status, " +
	"proposal.type, proposal.title,proposal.ao_str,proposal.ao_id," +
	"proposal.coi_contact,coi_first , coi_last, coi_email, " +
	"proposal.total_time,tgt.apptime," +
	"proposal.abstract, category_descrip,request_extra_flag, " +
	"ddtmanager.approval_email_date " +
	", prop_info.ocat_propid " +
	"from ddtmanager ,view_ddt_proposal proposal, #ddtgt tgt, " + 
	"axafocat..prop_info prop_info " +
	"where proposal.proposal_id = ddtmanager.proposal_id  " +
	"and proposal.proposal_id = tgt.proposal_id " +
	"and proposal.proposal_id *= prop_info.proposal_id " +
        query  + " " + orderBy;

    sqlStmt += "\ndrop table #ddtgt";

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
	DDTEntry ddt = new DDTEntry();
	ddt.setProposalID(rs.getInt(ii++));
	ddt.setStatus(rs.getString(ii++));
	ddt.setCoordinator(rs.getString(ii++));
	ddt.setUrgency(rs.getString(ii++));
	ddt.setProposalNumber(rs.getString(ii++));
	ddt.setPIFirst(rs.getString(ii++));
	ddt.setPI(rs.getString(ii++));
	ddt.setPIEmail(rs.getString(ii++));
        tdate = rs.getTimestamp(ii++);
        if (!rs.wasNull()) {
          ddt.setSubmissionDate(sdtf.format(tdate));
        } else {
          ddt.setSubmissionDate("");
        }
	ddt.setProposalStatus(rs.getString(ii++));
	ddt.setProposalType(rs.getString(ii++));
	ddt.setProposalTitle(rs.getString(ii++));
	ddt.setProposalAO(rs.getString(ii++));
	ddt.setProposalAOId(rs.getInt(ii++));
	ddt.setCoIContact(rs.getString(ii++));
	ddt.setCoIFirst(rs.getString(ii++));
	ddt.setCoI(rs.getString(ii++));
	ddt.setCoIEmail(rs.getString(ii++));
        dval = rs.getDouble(ii++);
	ddt.setRequestedTime(dval);
        dval = rs.getDouble(ii++);
	ddt.setApprovedTime(dval);
	ddt.setProposalAbstract(rs.getString(ii++));
	ddt.setSubjectCategory(rs.getString(ii++));
	ddt.setDataRights(rs.getString(ii++));
        tdate = rs.getTimestamp(ii++);
        if (!rs.wasNull()) {
          ddt.setApprovalDate(sdtf.format(tdate));
        } else {
          ddt.setApprovalDate("");
        }
	ddt.setOCatID(rs.getInt(ii++));
        if (dataPath != null) {
          String ddtPath = dataPath + "/chandra_rfo_ddt";
          ddt.setRPSFilename(ddtPath);
          ddt.setSJFilename(ddtPath);
          ddtPath = dataPath + "/conflicts";
          ddt.setConflictFilename(ddtPath);
        }
        ddtList.add(ddt);
      }
	    
	    
    } catch (Exception sqle) {
       LogMessage.printException(sqle);
       throw new SQLException("An error occured retrieving ddtmanager record: " + sqle.getMessage());
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


      return(ddtList);
    }
  /**
    * Retrieve a vector of all PropTarget records from the database 
    * for the given proposal_id
    * @param proposalID   proposal id for target retrieval
    * @return vector of PropTarget records
    * @exception SQLException thrown on sql errors
    */
  public PropTargetList getPropTargetList(Integer proposalID) 
	throws SQLException
  {

    PropTargetList tgtList = new PropTargetList();
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    int ii,ival;
    double dval;


    String sqlStmt = "select target.proposal_id,proposal_number," +
	"targid, targ_num,targname,target.description,ss_object,ra,dec," +
	"instrument_name,grating_name,timing_mode," +
        "target.status,prop_exposure_time,target.approved_exposure_time," +
	"photometry_flag,vmagnitude,y_det_offset,z_det_offset,sim_trans_offset," +
	"est_cnt_rate,forder_cnt_rate,total_fld_cnt_rate,extended_src," +
	"grid_name,num_pointings,approved_num_pointings,max_radius," +
	"time_critical,uninterrupt,pointing_constraint,monitor_flag," +
	"multitelescope,observatories," +
	"multitelescope_interval," +
	"group_id,group_interval," +
	"phase_constraint_flag,phase_period," +
	"phase_epoch,phase_start,phase_start_margin," +
	"phase_end,phase_end_margin," +
	"constr_in_remarks,target.remarks," +
	"exp_mode,bep_pack,ccdi0_on,ccdi1_on,ccdi2_on,ccdi3_on," +
	"ccds0_on,ccds1_on,ccds2_on,ccds3_on,ccds4_on,ccds5_on," +
	"most_efficient,frame_time," +
	"subarray,subarray_start_row,subarray_row_count," +
	"duty_cycle,secondary_exp_count,primary_exp_time," +
	"eventfilter,eventfilter_lower,eventfilter_range," +
	"spwindow,spadditional,spectra_max_count,multiple_spectral_lines," +
	"tootype,start,stop,time, " +
	"trigger_target,trig,tooremarks," +
	"fast_proc,fast_proc_status,fast_proc_comment," +
	"api.ocat_propid" +
	" from view_ddt_target target, axafocat..prop_info api  " +
	" where target.proposal_id = " + proposalID.toString() +
	" and target.proposal_id *= api.proposal_id" +
        " order by target.targ_num";

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
	PropTarget ddt = new PropTarget();
	ddt.setProposalID(rs.getInt(ii++));
	ddt.setProposalNumber(rs.getString(ii++));
	ddt.setTargID(rs.getInt(ii++));
	ddt.setTargetNumber(rs.getInt(ii++));
	ddt.setTargetName(rs.getString(ii++));
	ddt.setTargetDescription(rs.getString(ii++));
	ddt.setSSObjectName(rs.getString(ii++));
	ddt.setRA(rs.getDouble(ii++));
	ddt.setDec(rs.getDouble(ii++));
        ddt.setInitCoords();
	ddt.setInstrument(rs.getString(ii++));
	ddt.setGrating(rs.getString(ii++));
	ddt.setHRCTiming(rs.getString(ii++));
        if (rs.wasNull())
	  ddt.setHRCTiming("");
  
	ddt.setStatus(rs.getString(ii++));
	ddt.setExpTime(rs.getDouble(ii++));
        
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setApprovedTime(dval);
        }
	ddt.setPhotometry(rs.getString(ii++));
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setVMagnitude(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setYDetOffset(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setZDetOffset(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setSimTransOffset(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setEstCntRate(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setFOrderCntRate(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setTotalCntRate(dval);
        }
	ddt.setExtendedSrc(rs.getString(ii++));

	ddt.setGridName(rs.getString(ii++));
        ival = rs.getInt(ii++);
        if (!rs.wasNull()) {
	  ddt.setGridPointings(ival);
        }
        ival = rs.getInt(ii++);
        if (!rs.wasNull()) {
	  ddt.setGridApproved(ival);
        }

        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setGridRadius(dval);
        }
        
	ddt.setTimeCritical(rs.getString(ii++));
	ddt.setUninterrupt(rs.getString(ii++));
	ddt.setPointingConstraint(rs.getString(ii++));
	ddt.setMonitorFlag(rs.getString(ii++));
	ddt.setMultitelescope(rs.getString(ii++));
	ddt.setObservatories(rs.getString(ii++));
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setMultitelescopeInterval(dval);
        }
	ddt.setGroupID(rs.getString(ii++));
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setGroupInterval(dval);
        }

	ddt.setPhaseFlag(rs.getString(ii++));
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setPhasePeriod(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setPhaseEpoch(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setPhaseStart(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setPhaseStartMargin(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setPhaseEnd(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  ddt.setPhaseEndMargin(dval);
        }
	ddt.setRemarksFlag(rs.getString(ii++));
	ddt.setRemarks(rs.getString(ii++));
        if (rs.wasNull()) 
	  ddt.setRemarks("");

        AcisParam acisparam = new AcisParam();
	acisparam.setExpMode(rs.getString(ii++));
	acisparam.setBEPPack(rs.getString(ii++));
	acisparam.setCCDI0(rs.getString(ii++));
	acisparam.setCCDI1(rs.getString(ii++));
	acisparam.setCCDI2(rs.getString(ii++));
	acisparam.setCCDI3(rs.getString(ii++));
	acisparam.setCCDS0(rs.getString(ii++));
	acisparam.setCCDS1(rs.getString(ii++));
	acisparam.setCCDS2(rs.getString(ii++));
	acisparam.setCCDS3(rs.getString(ii++));
	acisparam.setCCDS4(rs.getString(ii++));
	acisparam.setCCDS5(rs.getString(ii++));
	acisparam.setMostEfficient(rs.getString(ii++));
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  acisparam.setFrameTime(dval);
        }
	acisparam.setSubarray(rs.getString(ii++));
        ival = rs.getInt(ii++);
        if (!rs.wasNull()) {
	  acisparam.setSubarrayStart(ival);
        }
        ival = rs.getInt(ii++);
        if (!rs.wasNull()) {
	  acisparam.setSubarrayNo(ival);
        }
	acisparam.setAltExp(rs.getString(ii++));
        ival = rs.getInt(ii++);
        if (!rs.wasNull()) {
	  acisparam.setSecondaryExp(ival);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  acisparam.setPrimaryExpTime(dval);
        }
	acisparam.setEnergyFilter(rs.getString(ii++));
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  acisparam.setEnergyFilterLower(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  acisparam.setEnergyFilterRange(dval);
        }
	acisparam.setSPWindow(rs.getString(ii++));
	acisparam.setSPAdditional(rs.getString(ii++));
        if (rs.wasNull()) 
	  acisparam.setSPAdditional("");

        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  acisparam.setSpectraMaxCount(dval);
        }
	acisparam.setMultipleSpectralLines(rs.getString(ii++));
        if (rs.wasNull()) 
	  acisparam.setMultipleSpectralLines("");
        ddt.setAcisParam(acisparam);
        
        
	ddt.setResponseWindow(rs.getString(ii++));
        if (rs.wasNull()) 
	  ddt.setResponseWindow("");

        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) 
	  ddt.setResponseStart(dval);

        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) 
	  ddt.setResponseStop(dval);
         
        dval = rs.getDouble(ii++);
        if (!rs.wasNull())  
	  ddt.setInitialTime(dval);
         
	ddt.setTriggerTarget(rs.getString(ii++));
        if (rs.wasNull())  
	  ddt.setTriggerTarget("Y");
	ddt.setTrigCriteria(rs.getString(ii++));
        if (rs.wasNull())  
	  ddt.setTrigCriteria("");
	ddt.setFupRemarks(rs.getString(ii++));
        if (rs.wasNull())  
	  ddt.setFupRemarks("");
	ddt.setFastProc(rs.getString(ii++));
	ddt.setFastProcStatus(rs.getString(ii++));
	ddt.setFastProcComment(rs.getString(ii++));
	ddt.setOcatPropid(rs.getInt(ii++));
        tgtList.add(ddt);
      }
	    
	    
    } catch (Exception sqle) {
       LogMessage.printException(sqle);
       throw new SQLException("An error occured retrieving ddtmanager record: " + sqle.getMessage());
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

     // now retrieve all the supporting records
     for (int tt=0;tt < tgtList.size();tt++) {
       PropTarget tgt = tgtList.get(tt);
       tgt.setFollowup(getDDTFollowups(tgt.getTargID()));
       if (tgt.getTimeCritical() != null &&
           !(tgt.getTimeCritical().equalsIgnoreCase("N"))) {
         tgt.setRollReq(getRollReq(tgt.getTargID()));
         tgt.setTimeReq(getTimeReq(tgt.getTargID()));
       }
       if (tgt.getAcisParam().getSPWindow() != null &&
           tgt.getAcisParam().getSPWindow().equalsIgnoreCase("Y")) {
         tgt.setAcisWin(getAcisWin(tgt.getTargID()));
       }
     }

     return(tgtList);
    }

  /**
    * Retrieve a vector of all Roll Constraint records from the database 
    * for the given target
    * @param targid  target id
    * @return vector of RollReq records
    * @exception SQLException thrown on sql errors
    */
  public Vector<RollReq> getRollReq(Integer targid) 
	throws SQLException
  {

    Vector<RollReq> rr = new Vector<RollReq>();
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    int ii,ival;
    double dval;


    String sqlStmt = "select distinct targid,ordr,roll_constraint," +
	"roll_180,roll,roll_tolerance " +
	" from rollreq " +
	" where rollreq.targid = " + targid.toString() +
        " order by ordr ";

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
	RollReq rollreq = new RollReq();
	rollreq.setTargID(rs.getInt(ii++));
	rollreq.setOrdr(rs.getInt(ii++));
	rollreq.setRollConstraint(rs.getString(ii++));
	rollreq.setRoll180(rs.getString(ii++));
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  rollreq.setRoll(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  rollreq.setRollTolerance(dval);
        }

        rr.add(rollreq);
      }
	    
	    
    } catch (Exception sqle) {
       LogMessage.printException(sqle);
       throw new SQLException("An error occured retrieving roll record: " + sqle.getMessage());
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


     return(rr);
  }

  /**
    * Retrieve a vector of all Time Constraint records from the database 
    * for the given target
    * @param targid  target id
    * @return vector of TimeReq records
    * @exception SQLException thrown on sql errors
    */
  public Vector<TimeReq> getTimeReq(Integer targid) 
	throws SQLException
  {

    Vector<TimeReq> tt = new Vector<TimeReq>();
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    int ii,ival;
    double dval;


    String sqlStmt = "select targid,ordr,time_constraint," +
	"tstart,tstop" +
	" from timereq " +
	" where timereq.targid = " + targid.toString() +
        " order by ordr ";

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
	TimeReq timereq = new TimeReq();
	timereq.setTargID(rs.getInt(ii++));
	timereq.setOrdr(rs.getInt(ii++));
	timereq.setTimeConstraint(rs.getString(ii++));
	timereq.setTstart(rs.getString(ii++));
	timereq.setTstop(rs.getString(ii++));

        tt.add(timereq);
      }
	    
	    
    } catch (Exception sqle) {
       LogMessage.printException(sqle);
       throw new SQLException("An error occured retrieving roll record: " + sqle.getMessage());
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


    return(tt);
  }

  /**
    * Retrieve a vector of all Acis Window records from the database 
    * for the given target
    * @param targid  target id
    * @return vector of AcisWin records
    * @exception SQLException thrown on sql errors
    */
  public Vector<AcisWin> getAcisWin(Integer targid) 
	throws SQLException
  {

    Vector<AcisWin> aw = new Vector<AcisWin>();
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    int ii,ival;
    double dval;


    String sqlStmt = "select distinct targid,ordr,chip," +
	"sample,start_column,width,start_row,height, " +
	"lower_threshold,pha_range " +
	" from aciswin,target_aciswin ta" +
	" where ta.targid = " + targid.toString() +
	" and ta.aciswin_id =  aciswin.aciswin_id" +
        " order by ordr ";

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
	AcisWin aciswin = new AcisWin();
	aciswin.setTargID(rs.getInt(ii++));
	aciswin.setOrdr(rs.getInt(ii++));
	aciswin.setChip(rs.getString(ii++));
        ival = rs.getInt(ii++);
        if (!rs.wasNull()) {
	  aciswin.setSample(ival);
        }
        ival = rs.getInt(ii++);
        if (!rs.wasNull()) {
	  aciswin.setStartCol(ival);
        }
        ival = rs.getInt(ii++);
        if (!rs.wasNull()) {
	  aciswin.setColWidth(ival);
        }
        ival = rs.getInt(ii++);
        if (!rs.wasNull()) {
	  aciswin.setStartRow(ival);
        }
        ival = rs.getInt(ii++);
        if (!rs.wasNull()) {
	  aciswin.setRowHeight(ival);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  aciswin.setLowerThreshold(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull()) {
	  aciswin.setEnergyRange(dval);
        }

        aw.add(aciswin);
      }
	    
	    
    } catch (Exception sqle) {
       LogMessage.printException(sqle);
       throw new SQLException("An error occured retrieving aciswin record: " + sqle.getMessage());
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


     return(aw);
  }


  /**
    * Retrieve a vector of all Followup records from the database 
    * for the given target
    * @param targid   target id to use for followup retrieval
    * @return vector of DDTTarget records
    * @exception SQLException thrown on sql errors
    */
  public Vector<DDTFollowup> getDDTFollowups(Integer targid)
	throws SQLException
  {

    Vector<DDTFollowup> fupList = new Vector<DDTFollowup>();
    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    int ii;
    double dval;

    String sqlStmt = "select observation.targid," +
	"observation.ordr,observation.obs_time," +
	"observation.pre_min_lead,observation.pre_max_lead," +
	"observation.status,observation.targ_num " +
	" from observation " +
	" where observation.targid=" + targid.toString() + 
        " order by observation.ordr";

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
	DDTFollowup ddt = new DDTFollowup();
	ddt.setTargid(rs.getInt(ii++));
	ddt.setOrdr(rs.getInt(ii++));
	ddt.setExpTime(rs.getDouble(ii++));
	ddt.setMinLead(rs.getDouble(ii++));
	ddt.setMaxLead(rs.getDouble(ii++));
	ddt.setStatus(rs.getString(ii++));
	ddt.setTargetNumber(rs.getInt(ii++));
        fupList.add(ddt);
      }
	    
	    
    } catch (Exception sqle) {
       LogMessage.printException(sqle);
       throw new SQLException("An error occured retrieving ddtmanager record: " + sqle.getMessage());
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


      return(fupList);
    }
  /**
    * Update the approval_email_date date field for a ddt record
    * @param proposalID  database id for ddt entry
    * @param resetFlg  if true, reset approval date to null 
    * @exception SQLException thrown on sql errors
    */ 
  public void setDDTApprovalDate(Integer proposalID,boolean resetFlg)
	throws SQLException
  {
    Connection connection = null;
    String sqlStmt;
  
    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database. ");
    }
    try {
      // now update the  comments
      if (!resetFlg) {
        sqlStmt = "update ddtmanager set approval_email_date= getdate() where proposal_id = ?";
      } else {
        sqlStmt = "update ddtmanager set approval_email_date= null where proposal_id = ?";
      }
      LogMessage.println (sqlStmt + proposalID.toString());
          
      PreparedStatement pstmt = connection.prepareStatement(sqlStmt);
      pstmt.setInt(1,proposalID.intValue());
      pstmt.executeUpdate();
      database.processWarnings(pstmt);
          
    }
    catch (SQLException exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to update approval email date for DDT id= " + proposalID.toString() + ". \n" + exc.getMessage() );
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
    * Update the coordinator field for a trigger too record
    * @param proposalID  database id for ddt entry
    * @param coordinator  assigned coordinator
    * @exception SQLException thrown on sql errors
    */ 
  public void updateCoordinator(Integer proposalID, String coordinator)
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
      String sqlStmt = "update ddtmanager set coordinator= ? where proposal_id = ?";
      PreparedStatement pstmt = connection.prepareStatement(sqlStmt);
      pstmt.setString(1,coordinator);
      pstmt.setInt(2,proposalID.intValue());
      pstmt.executeUpdate();
      database.processWarnings(pstmt);
          
    }
    catch (SQLException exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to update Coordinator for DDT id= " + proposalID.toString() + ". \n" + exc.getMessage() );
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
    * Update the too response times and initial exposure time
    * @param proposalNumber  proposal number
    * @param targnum  target number
    * @param targid   database id for observation entry
    * @param initTime  time for initial observation
    * @param cxcStart response start (days)
    * @param cxcStop  response stop (days)
    * @exception SQLException thrown on sql errors
    */
  public void updateTOO(String proposalNumber,Integer targnum, Integer targid, 
	Double initTime, Double cxcStart, Double cxcStop)
	throws SQLException
  {
    Connection connection = null;
  
    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("DDT Manager Unable to connect to database. ");
    }
    try {
      // now update the  status and exposure time
      String command = "{ ? = call ddtmanager_too_update(?,?,?,?,?)}";
      CallableStatement cstmt = connection.prepareCall (command);

      // Register input parameters
      cstmt.setString(2, proposalNumber);
      cstmt.setInt(3, targid.intValue());
      // value could be null
      if (initTime.doubleValue() < 0) {
        cstmt.setNull(4, Types.DOUBLE);
      }
      else {
        cstmt.setDouble(4, initTime.doubleValue());
      }
      cstmt.setDouble(5, cxcStart.doubleValue());
      cstmt.setDouble(6, cxcStop.doubleValue());
      cstmt.registerOutParameter( 1, Types.INTEGER );
      LogMessage.println ("DBDDT: updating TOO parameters for " + targid.toString());

      cstmt.execute();
      int status=cstmt.getInt(1);
      if (status < 0) {
         database.processWarnings(cstmt);
      }
    }
    catch (SQLException exc) {
       LogMessage.printException(exc);
       throw new SQLException("Unable to update too for DDT target number " + targnum.toString() + " \n" + exc.getMessage());
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
    * Update the followup status and exposure time
    * @param targnum  target number
    * @param targid   database id for observation entry
    * @param ordr     ordr for observation
    * @param fupStatus   new followup status
    * @param appTime  approved time for followup observation
    * @param minLead  minimum lead time for preceding observation
    * @param maxLead  maximum lead time for preceding observation
    * @exception SQLException thrown on sql errors
    */
  public void updateFollowup(Integer targnum, Integer targid, 
	Integer ordr, String fupStatus, Double appTime,
	Double minLead, Double maxLead)
	throws SQLException
  {
    Connection connection = null;
  
    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("DDT Manager Unable to connect to database. ");
    }
    try {
      // now update the  status and exposure time
      String command = "{ ? = call ddtmanager_observation_update(?,?,?,?,?,?)}";
      CallableStatement cstmt = connection.prepareCall (command);

      cstmt.registerOutParameter( 1, Types.INTEGER );

      // Register input parameters
      cstmt.setInt(2, targid.intValue());
      cstmt.setInt(3, ordr.intValue());
      cstmt.setString(4, fupStatus);
      if (appTime.doubleValue() < 0) 
        cstmt.setNull(5, Types.DOUBLE);
      else
        cstmt.setDouble(5, appTime.doubleValue());
      if (minLead.doubleValue() < 0) 
        cstmt.setNull(6, Types.DOUBLE);
      else
        cstmt.setDouble(6, minLead.doubleValue());
      if (maxLead.doubleValue() < 0) 
        cstmt.setNull(7, Types.DOUBLE);
      else
        cstmt.setDouble(7, maxLead.doubleValue());
      LogMessage.println ("DBDDT: updating Observation parameters for " + targid.toString() + " ordr=" + ordr.toString());

      cstmt.execute();
      int status=cstmt.getInt(1);
      if (status < 0) {
         database.processWarnings(cstmt);
      }
    }
    catch (SQLException exc) {
       LogMessage.printException(exc);
       throw new SQLException("Unable to update followup observation for DDT target " + targnum.toString() + " ordr= " + ordr.toString() + ". \n" + exc.getMessage());
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
    * Update proposal status field for the DDT record
    * @param proposalNumber   proposal number
    * @param propStatus   status value
    * @exception SQLException thrown on sql errors
    */
  public void updateProposalStatus(String proposalNumber, String propStatus)
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
      String command = "{ ? = call ddtmanager_proposal_update(?,?)}";
      CallableStatement cstmt = connection.prepareCall(command);
      cstmt.registerOutParameter( 1, Types.INTEGER );
      cstmt.setString(2,proposalNumber);
      cstmt.setString(3,propStatus);
      cstmt.execute();
      int status=cstmt.getInt(1);
      if (status < 0) {
         database.processWarnings(cstmt);
      }
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to update proposal status for DDT = " + proposalNumber + ". \n" + exc.getMessage());
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
    * Update status field for the DDT target   record
    * @param proposalNumber   proposal Number for DDT
    * @param targnum   target Number for DDT
    * @param targid      target id for target entry
    * @param tgtStatus   status value
    * @param appTime     approved exposure time
    * @param gridApproved   number of grids approved
    * @exception SQLException thrown on sql errors
    */
  public void updateTarget(String proposalNumber,
	Integer targnum, Integer targid, 
	String tgtStatus, Double appTime,Integer gridApproved)
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
      String command = "{ ? = call ddtmanager_target_update(?,?,?,?,?}";
      CallableStatement cstmt = connection.prepareCall(command);
      cstmt.registerOutParameter( 1, Types.INTEGER );
      cstmt.setString(2,proposalNumber);
      cstmt.setInt(3,targid.intValue());
      cstmt.setString(4,tgtStatus);
      if (appTime.doubleValue() < 0) 
        cstmt.setNull(5, Types.DOUBLE);
      else
        cstmt.setDouble(5,appTime.doubleValue());
      if (gridApproved.intValue() < 0) 
        cstmt.setNull(6, Types.INTEGER);
      else
        cstmt.setInt(6,gridApproved.intValue());
      cstmt.execute();
      int status=cstmt.getInt(1);
      if (status < 0) {
         database.processWarnings(cstmt);
      }
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to update status for DDT target number " + targnum.toString()  + ". \n" + exc.getMessage());
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
    * Update fast processing fields for the DDT target   record
    * @param targid      target id for target entry
    * @param fpStatus    status value
    * @param fpComment   comment value
    * @exception SQLException thrown on sql errors
    */
  public void updateFastProc( Integer targid, String fpStatus, String fpComment)
	throws SQLException
  {
  
    // update the fast processing fields
    Connection connection = null;
  
    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database. ");
    }
    try {
      String command = "{ ? = call update_ddt_fast_proc(?,?,?)}";
      CallableStatement cstmt = connection.prepareCall(command);
      cstmt.registerOutParameter( 1, Types.INTEGER );
      cstmt.setInt(2,targid.intValue());
      if (fpStatus == null || fpStatus.length() < 2)
        cstmt.setNull(3,Types.VARCHAR);
      else
        cstmt.setString(3,fpStatus);
      if (fpComment == null || fpComment.length() < 2)
        cstmt.setNull(4,Types.VARCHAR);
      else
        cstmt.setString(4,fpComment);

      cstmt.execute();
      int status=cstmt.getInt(1);
      if (status < 0) {
         database.processWarnings(cstmt);
      }
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to update fast processing for DDT target id " + targid.toString()  + ". \n" + exc.getMessage());
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
    * Update status field for the DDT manager   record
    * @param proposalID   database id for ddtmanager entry
    * @param status status value
    * @exception SQLException thrown on sql errors
    */
  public void updateStatus(Integer proposalID, String status)
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
      String sqlStmt = "update ddtmanager set status = ? where proposal_id = ? ";
      PreparedStatement pstmt = connection.prepareStatement(sqlStmt);
      pstmt.setString(1,status);
      pstmt.setInt(2,proposalID.intValue());
      pstmt.executeUpdate();
      database.processWarnings(pstmt);
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to update status for DDT proposal id= " + proposalID.toString()  + ". \n" + exc.getMessage());
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
    * Update urgency (response type) field for the ddt too record
    * @param proposalID   database id for ddtmanager entry
    * @param responseType new urgency/response type
    * @exception SQLException thrown on sql errors
    */
  public void updateUrgency(Integer proposalID, String responseType)
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
      String sqlStmt = "update ddtmanager set response_type = ? where proposal_id = ? ";
      PreparedStatement pstmt = connection.prepareStatement(sqlStmt);
      pstmt.setString(1,responseType);
      pstmt.setInt(2,proposalID.intValue());
      pstmt.executeUpdate();
      database.processWarnings(pstmt);
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to update response window for DDT proposal id= " + proposalID.toString()  + ". \n" + exc.getMessage());
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
    * Update data rights (proprietary rights ) field for the ddt
    * @param proposalID   proposal ID
    * @param dataRights    proprietary data rights 
    * @exception SQLException thrown on sql errors
    */
  public void updateDataRights(Integer  proposalID, String dataRights)
	throws SQLException
  {
  
    // update the  dataRights
    Connection connection = null;
  
    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database. ");
    }
    try {
      String sqlStmt = "update view_ddt_proposal set request_extra_flag = ? where proposal_id = ? ";
      PreparedStatement pstmt = connection.prepareStatement(sqlStmt);
      pstmt.setString(1,dataRights);
      pstmt.setInt(2,proposalID.intValue());
      pstmt.executeUpdate();
      database.processWarnings(pstmt);
    }
    catch (Exception exc) {
      LogMessage.printException(exc);
      throw new SQLException("Unable to update proprietary rights for DDT proposal id " + proposalID.toString()  + ". \n" + exc.getMessage());
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
}
 
    


