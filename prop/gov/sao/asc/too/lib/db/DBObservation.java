package db;
/*
  Copyrights:

  Copyright (c) 2000-2014 Smithsonian Astrophysical Observatory

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

/**
  * The DBObservation class contains information about the TOO
  * observation. This is info from axafocat or entered by the
  * user when triggering a TOO.
  */

import java.sql.SQLException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.io.IOException;

import ascds.LogMessage;
import ascds.Coordinate;
import info.Observation;
import info.ObservationList;

public class DBObservation  
{

  private Database database;
  private boolean showDebug;


  /** 
    * Constructor
    * @param triggerTooDataPath   directory path to TOO Trigger files
    * @param showDebug	boolean value, if true then log debug messages to file
    * @exception IOException file i/o issue
    */
  public DBObservation(String triggerTooDataPath,
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
    * @param showDebug	boolean value, if true then log debug messages to file
    * @exception IOException file i/o issue
    */
  public DBObservation(String userName, String userPwd,
	String triggerTooDataPath, boolean showDebug ) 
	throws IOException
  {
    this.showDebug = showDebug;   
    String databaseName = "axafocat";
    database = new Database(userName,userPwd,triggerTooDataPath,databaseName,showDebug,true);

  }


  /**
    * Retrieve unobserved TOO observations by obsid. 
    * @param obsid  observation id
    * @return vector of unobserved observations and any followups for specified obsid
    * @exception SQLException SQL error
    */
  public ObservationList getObservationListbyObsid(Integer obsid) 
	throws SQLException{

    ObservationList observationList;

    String sqlQuery = new String( "obsid= ?" );
    sqlQuery += " and (status ='unobserved' or status='untriggered')";
    observationList = executeTOOSearchQuery(sqlQuery,obsid,true);

    return(observationList);
  }

  /**
    * Retrieve all TOOs including archived/observed by obsid 
    * @param obsid  observation id
    * @return vector of observations and any followups for specified obsid
    * @exception SQLException SQL error
    */
  public ObservationList getAllObservationListbyObsid(Integer obsid) 
	throws SQLException{

    ObservationList observationList;

    String sqlQuery = new String( "obsid= ?" );
    observationList = executeTOOSearchQuery(sqlQuery,obsid,true);


    return(observationList);
  }

  /**
    * Retrieve unobserved TOOs by sequence number
    * @param seqnbr  sequence number
    * @return vector of observations and any followups for specified sequence number
    * @exception SQLException SQL error
    */
  public ObservationList getObservationListbySequenceNumber(String seqnbr) 
	throws SQLException{

    ObservationList observationList;
    
    String sqlQuery = new String( "seq_nbr like ?");
    sqlQuery += " and (status ='unobserved' or status='untriggered')";
    observationList = executeTOOSearchQuery(sqlQuery,seqnbr,false);

    return(observationList);
  }

  /**
    * Retrieve unobserved TOOs by proposal number
    * @param propnum  proposal number
    * @return vector of observations and any followups for specified proposal number
    * @exception SQLException SQL error
    */
  public ObservationList getObservationListbyProposalNumber(String propnum) 
	throws SQLException{

    ObservationList observationList;
	
    String sqlQuery = new String( "prop_num like ? ");
    sqlQuery += " and (status ='unobserved' or status='untriggered')";
    observationList = executeTOOSearchQuery(sqlQuery,propnum,false);

    return(observationList);
  }

  /**
    * Retrieve unobserved TOOs by PI last name 
    * @param pilast  P.I. last name 
    * @return vector of observations and any followups for specified P.I.
    * @exception SQLException SQL error
    */
  public ObservationList getObservationListbyPI(String pilast) 
	throws SQLException{

    ObservationList observationList;
    //String newpi = pilast.replaceAll("'","''");
    //LogMessage.println("PI:" + newpi);
    String newpi = pilast;

    String sqlQuery = new String( "lower(last) like ?" );
    sqlQuery += " and (status ='unobserved' or status='untriggered')";
    observationList = executeTOOSearchQuery(sqlQuery,newpi.toLowerCase(),false);

    return(observationList);
  }

  /**
    * Retrieve count of linked observations for given obsid
    * @param obsid   Observation Id
    * @return int    Count of linked observations
    * @exception SQLException SQL error
    */
  public int getLinkedCount(Integer obsid) throws SQLException
  {

    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    int linkedCount = -1;

    String sqlStmt = "exec pub_ocat_retr_linked_obs " +  obsid.toString() ;
        

	try {
	 connection = database.getConnection();
        }
        catch (Exception exc) {
          //LogMessage.printException(exc);
          throw new SQLException("Unable to connect to the database.");
        }

        try {
	    pstmt = connection.prepareStatement(sqlStmt);
            if (showDebug) {
              LogMessage.println( sqlStmt + "\n");
            }
	    rs = pstmt.executeQuery();
	    while (rs.next()) {
                linkedCount += 1;
	    }
	    
	    
	} catch (Exception sqle) {
            LogMessage.printException(sqle);
	    throw new SQLException("An error occured in retrieving the data: " + sqle.getMessage());
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
		LogMessage.println("SQLException during close(): " + sqle.toString());
            }
        } // finally

	return linkedCount;
  }
    
  /**
    * Retrieve unobserved TOOs by Alternate Group and proposal_id
    * @param group_name   Alternate target group name
    * @param proposalId  Proposal id  
    * @return vector of observations and any followups for specified P.I.
    * @exception SQLException SQL error
    */
  public ObservationList getObservationListbyAltGroup(String group_name,
	Integer proposalId) throws SQLException{

    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    ObservationList observationList = new ObservationList();

    String sqlStmt = "select seq_nbr,vt.obsid,prop_num,obs_ao_str," + 
	"last,first,email,phone,coi_last,coi_first,coi_email,coi_phone, " +
	"title,type,targname,object,instrument,grating, " +
	"ra,dec,approved_exposure_time,rem_exp_time,status,too_type,trig, " +
	"pre_id,pre_min_lead,pre_max_lead,start,stop,remarks,mp_remarks," +
	"multitelescope,observatories,alternate_group_name, " +
	"approved_count,lts_lt_plan,soe_st_sched_date,si_mode " +
	"from view_too vt " +
	"where lower(alternate_group_name) like '" + group_name.toLowerCase() +
        "' and proposal_id = " + proposalId.toString() +
        " order by vt.obsid" ;

	try {
	 connection = database.getConnection();
        }
        catch (Exception exc) {
          //LogMessage.printException(exc);
          throw new SQLException("Unable to connect to the database.");
        }

        try {
	    pstmt = connection.prepareStatement(sqlStmt);
            if (showDebug) {
              LogMessage.println( sqlStmt + "\n");
            }
	    rs = pstmt.executeQuery();
	    while (rs.next()) {
		Observation obs = new Observation();
                assign_db_fields(obs,rs);
                observationList.add(obs);
	    }
	    
	    
	} catch (Exception sqle) {
            LogMessage.printException(sqle);
	    throw new SQLException("An error occured in retrieving the data: " + sqle.getMessage());
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
		LogMessage.println("SQLException during close(): " + sqle.toString());
            }
        } // finally

	return observationList;
  }
    

  /**
    * Retrieve target(s) by proposal number from Ocat (not view_too)
    * @param proposalNumber  proposal_number  
    * @return vector of observations 
    * @exception SQLException SQL error
    */
  public ObservationList getObsListbyProposalNumber(String proposalNumber) 
	throws SQLException{

    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    ObservationList observationList = new ObservationList();

    String sqlStmt = "select seq_nbr,obsid,prop_num,obs_ao_str," +
	"ps.last,ps.first,ps.email,p.phone," +
	"coi.last,coi.first,coi.email,coi_phone, " +
	"prop_info.title,target.type,targname,object,instrument,grating, " +
	"ra,dec,approved_exposure_time,rem_exp_time,status,too.type,trig, " +
	"pre_id,pre_min_lead,pre_max_lead," +
	"start,stop,target.remarks,mp_remarks," +
	"multitelescope,observatories,NULL, " +
	"NULL,lts_lt_plan,soe_st_sched_date,si_mode " +
	"from prop_info,target,too,axafusers..person_short ps, " +
	"axafusers..person p,axafusers..person_short coi " +
	"where prop_num ='" + proposalNumber.trim() + "'" +
	" and target.status not in ('discarded','canceled') " +
	" and prop_info.ocat_propid = target.ocat_propid" +
	" and prop_info.piid = ps.pers_id" +
	" and prop_info.piid = p.pers_id" +
	" and prop_info.coin_id *= coi.pers_id" +
	" and target.tooid *= too.tooid" +
        " order by obsid" ;

	try {
	 connection = database.getConnection();
        }
        catch (Exception exc) {
          //LogMessage.printException(exc);
          throw new SQLException("Unable to connect to the database.");
        }

        try {
	    pstmt = connection.prepareStatement(sqlStmt);
            if (showDebug) {
              LogMessage.println( sqlStmt + "\n");
            }
	    rs = pstmt.executeQuery();
	    while (rs.next()) {
		Observation obs = new Observation();
                assign_db_fields(obs,rs);
                observationList.add(obs);
	    }
	    
	    
	} catch (Exception sqle) {
            LogMessage.printException(sqle);
	    throw new SQLException("An error occured in retrieving the data: " + sqle.getMessage());
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
		LogMessage.println("SQLException during close(): " + sqle.toString());
            }
        } // finally

	return observationList;
  }
    


  /**
    * Execute the SQL query and build the vector of Observations
    * query may include search by obsid,sequence number, proposal
    * number or PI last name.
    * @param query SQL query to retrieve TOO Observations and their followups
    * @param qval  input parameter object for query
    * @param isInt  true if input parameter is integer, else string
    * @return vector of observations and any followups for specified P.I.
    * @exception SQLException SQL error
    */
  private ObservationList executeTOOSearchQuery(String query, Object qval,boolean isInt ) 
	throws SQLException {

    Connection connection = null;
    PreparedStatement pstmt=null;
    ResultSet rs=null;
    int ii;
    ObservationList observationList = new ObservationList();

    String sqlCreate = "create table #dtmp (obsid int)";

    String sqlInsert = "insert #dtmp select obsid "  +
        " from view_too  where  " +
	query ;
    LogMessage.println(sqlInsert);

    String sqlInsertLinked = "while  exists " +
	"(select 1 from target where pre_id in " +
	"(select distinct obsid from #dtmp) and " +
	"obsid not in (select distinct obsid from #dtmp)" +
	"and status not in ('discarded','canceled')) " +
	" begin " +
	"insert #dtmp select obsid from target " +
	"where pre_id in (select distinct obsid from #dtmp) and " +
	"obsid not in (select distinct obsid from #dtmp) " +
	"and status not in ('discarded','canceled') " +
	"end";
    String sqlInsertLinked2 = "while  exists " +
	"(select 1 from target where obsid in " +
	"(select distinct obsid from #dtmp) and " +
	"pre_id not in (select distinct obsid from #dtmp)" +
	"and status not in ('discarded','canceled')) " +
	" begin " +
	"insert #dtmp select pre_id from target " +
	"where obsid in (select distinct obsid from #dtmp) and " +
	"pre_id not in (select distinct obsid from #dtmp) " +
	"and status not in ('discarded','canceled') " +
	"end";

    String sqlStmt = "select seq_nbr,vt.obsid,prop_num,obs_ao_str," + 
	"last,first,email,phone,coi_last,coi_first,coi_email,coi_phone, " +
	"title,type,targname,object,instrument,grating, " +
	"ra,dec,approved_exposure_time,rem_exp_time,status,too_type,trig, " +
	"pre_id,pre_min_lead,pre_max_lead,start,stop,remarks,mp_remarks, " +
	"multitelescope,observatories,alternate_group_name, " +
	"approved_count,lts_lt_plan,soe_st_sched_date,si_mode " +
	"from #dtmp,view_too vt " +
	"where #dtmp.obsid = vt.obsid " +
        " order by vt.obsid" ;

	try {
	 connection = database.getConnection();
        }
        catch (Exception exc) {
          //LogMessage.printException(exc);
          throw new SQLException("Unable to connect to the database.");
        }

        try {
	    pstmt = connection.prepareStatement(sqlCreate);
            pstmt.executeUpdate();
        

	    pstmt = connection.prepareStatement(sqlInsert);
            if (isInt) {
              pstmt.setInt(1,(Integer)qval);
            } else  {
              pstmt.setString(1,(String)qval);
            }

            pstmt.executeUpdate();

	    pstmt = connection.prepareStatement(sqlInsertLinked);
            pstmt.executeUpdate();
	    
	    pstmt = connection.prepareStatement(sqlInsertLinked2);
            pstmt.executeUpdate();
        
	    pstmt = connection.prepareStatement(sqlStmt);
            if (showDebug) {
              LogMessage.println( sqlStmt + "\n");
            }
	    rs = pstmt.executeQuery();
	    while (rs.next()) {
		Observation obs = new Observation();
                assign_db_fields(obs,rs);
                observationList.add(obs);
	    }
	    
	    
	} catch (Exception sqle) {
            LogMessage.printException(sqle);
	    throw new SQLException("An error occured in retrieving the data: " + sqle.getMessage());
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
		LogMessage.println("SQLException during close(): " + sqle.toString());
            }
        } // finally

	return observationList;
    }

private void assign_db_fields (Observation obs, ResultSet rs) 
   throws Exception
{
   int ii=1;
   double dval;
   Timestamp tdate;
   Date ldate;
   SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
   SimpleDateFormat sdtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	obs.setSequenceNumber(rs.getString(ii++));
	obs.setObsid(rs.getInt(ii++));
	obs.setProposalNumber(rs.getString(ii++));
	obs.setCycle(rs.getString(ii++));
	obs.setPI(rs.getString(ii++));
	obs.setPIFirst(rs.getString(ii++));
	obs.setPIEmail(rs.getString(ii++));
	obs.setPIPhone(rs.getString(ii++));
	obs.setObserver(rs.getString(ii++));
	obs.setObserverFirst(rs.getString(ii++));
	obs.setObserverEmail(rs.getString(ii++));
	obs.setObserverPhone(rs.getString(ii++));
	obs.setTitle(rs.getString(ii++));
	obs.setType(rs.getString(ii++));
	obs.setOrigTargetName(rs.getString(ii++));
	obs.setObjectName(rs.getString(ii++));
	obs.setInstrument(rs.getString(ii++));
	obs.setGrating(rs.getString(ii++));
        dval = rs.getDouble(ii++);
        if (!rs.wasNull() ) {
          obs.setRA(dval);
        }
        dval = rs.getDouble(ii++);
        if (!rs.wasNull() ) {
	  obs.setDec(dval);
        }
        obs.setInitCoords();
	obs.setOrigApprovedExpTime(rs.getDouble(ii++));
	obs.setRemainingExpTime(rs.getDouble(ii++));
	obs.setStatus(rs.getString(ii++));
	obs.setResponseWindow(rs.getString(ii++));
	obs.setTriggerCriteria(rs.getString(ii++));
	obs.setPreID(rs.getInt(ii++));
	obs.setOrigPreMinLead(rs.getDouble(ii++));
	obs.setOrigPreMaxLead(rs.getDouble(ii++));
	obs.setOrigResponseStart(rs.getDouble(ii++));
	obs.setOrigResponseStop(rs.getDouble(ii++));
	obs.setRemarks(rs.getString(ii++));
	obs.setMPRemarks(rs.getString(ii++));
	obs.setCoordinatedObs(rs.getString(ii++));
	obs.setObservatories(rs.getString(ii++));
	obs.setAlternateGroupName(rs.getString(ii++));
	obs.setAlternateApprovedCount(rs.getInt(ii++));
        ldate = rs.getDate(ii++);
        if (rs.wasNull()) {
          obs.setLTSDate("");
        }
        else {
          obs.setLTSDate(sdf.format(ldate));
        }

        tdate = rs.getTimestamp(ii++);
        if (!rs.wasNull()) {
          obs.setSTSDate(sdtf.format(tdate));
        } else {
          obs.setSTSDate("");
        }

	obs.setSIMode(rs.getString(ii++));
	obs.setUrgency();
  }

  /**
    * update the coordinates field in the database for an observation record
    * @param obsid   observation id to be updated
    * @param coords  new coordinates for observation
    * @exception SQLException SQL error
    */
  public void updateCoordinates(Integer obsid, Coordinate coords)
        throws SQLException
  {
    Connection connection = null;
 
    try {
      connection = database.getConnection();
    }
    catch (Exception exc){
      LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database.");
    }
    try {
      // now update the  comments
      String sqlStmt = "update target set ra= ?,dec=? where obsid = ?";
      PreparedStatement pstmt = connection.prepareStatement(sqlStmt);
      pstmt.setDouble(1,coords.getLon());
      pstmt.setDouble(2,coords.getLat());
      pstmt.setInt(3,obsid.intValue());
      pstmt.executeUpdate();
      database.processWarnings(pstmt);

    }
    catch (SQLException sqle) {
       LogMessage.printException(sqle);
       LogMessage.println("updating " +  obsid + " ra=" + coords.getLon() + " dec=" + coords.getLat());
       throw new SQLException("Unable to update Coordinates for  Obsid= " + obsid.toString() + "\n" + sqle.getMessage() + "\n");
    }
    finally {
      try {
        if (connection != null) {
          connection.close();
        }
      }
      catch (SQLException sqle) {
        LogMessage.printException(sqle);
        LogMessage.println("SQLException during close(): " + sqle.toString());
      }
    }
  }

  /**
    * update the target name in the database for an observation record
    * @param obsid   observation id to be updated
    * @param targetName  new target name for observation
    * @exception SQLException SQL error
    */
  public void updateTargetName(Integer obsid, String targetName)
        throws SQLException
  {
    Connection connection = null;
 
    try {
      connection = database.getConnection();
    }
    catch (Exception exc){
      //LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database.");
    }
    try {
      // now update the  comments
      String sqlStmt = "update target set targname= ? where obsid = ?";
      PreparedStatement pstmt = connection.prepareStatement(sqlStmt);
      pstmt.setString(1,targetName);
      pstmt.setInt(2,obsid.intValue());
      pstmt.executeUpdate();
      database.processWarnings(pstmt);

    }
    catch (SQLException sqle) {
      LogMessage.printException(sqle);
      throw new SQLException("Unable to update Target Name for  Obsid= " + obsid.toString() + "\n" + sqle.getMessage() + "\n");
    }
    finally {
      try {
        if (connection != null) {
          connection.close();
        }
      }
      catch (SQLException sqle) {
        LogMessage.printException(sqle);
        LogMessage.println("SQLException during close(): " + sqle.toString());
      }
    }
  }
 /**
    * update the cxc start/stop fields in the database for an observation record
    * @param obsid   observation id to be updated
    * @param cxcstart  new cxc start
    * @param cxcstop  new cxc stop
    * @exception SQLException SQL error
    */
  public void updateTOOStartStop(Integer obsid, double cxcstart ,double cxcstop)
        throws SQLException
  {
    Connection connection = null;
 
    try {
      connection = database.getConnection();
    }
    catch (Exception exc){
      //LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database.");
    }
    try {
      // now update the  too record
      String sqlStmt = "update too set start= ?,stop=?  from target,too where obsid = ? and target.tooid = too.tooid";
      PreparedStatement pstmt = connection.prepareStatement(sqlStmt);
      pstmt.setDouble(1,cxcstart);
      pstmt.setDouble(2,cxcstop);
      pstmt.setInt(3,obsid.intValue());
      pstmt.executeUpdate();
      database.processWarnings(pstmt);

    }
    catch (SQLException sqle) {
       LogMessage.printException(sqle);
       LogMessage.println("updating " +  obsid + " cxcstart=" + cxcstart + " cxcstop=" + cxcstop);
       throw new SQLException("Unable to update start/stop response for  Obsid= " + obsid.toString() + "\n" + sqle.getMessage() + "\n");
    }
    finally {
      try {
        if (connection != null)
          connection.close();
      }
      catch (SQLException sqle) {
        LogMessage.printException(sqle);
      }
    }
  }


 /**
    * updates to TOO target fields
    * @param obsid    observation id
    * @param approved_exposure_time   approved exposure time for observation
    * @param pre_min_lead    pre min lead time for observation
    * @param pre_max_lead    pre max lead time for observation
    * @exception SQLException SQL error
    */
  public void updateTOOTarget(Integer obsid, 
	Double approved_exposure_time,
	Double pre_min_lead,
	Double pre_max_lead)
        throws SQLException
  {
    Connection connection = null;
    CallableStatement cstmt=null;
    String message = new String("");
    boolean  stat = true;
    int idx;

    try {
      connection = database.getConnection();
    }
    catch (Exception exc) {
      //LogMessage.printException(exc);
      throw new SQLException("Unable to connect to database. ");
    }
    try {
      String command = "{? = call update_too_target(?,?,?,?)}";
      cstmt = connection.prepareCall (command);

      cstmt.registerOutParameter(1,Types.INTEGER);

      // Register input parameters
      idx = 2;
      cstmt.setInt(idx++, obsid.intValue());
      cstmt.setDouble(idx++, approved_exposure_time.doubleValue());
      if (pre_min_lead != null && pre_min_lead.doubleValue() >= 0) {
        cstmt.setDouble(idx++, pre_min_lead.doubleValue());
      } else {
        cstmt.setNull(idx++, Types.DOUBLE);
      }
      if (pre_max_lead != null && pre_max_lead.doubleValue() >= 0) {
        cstmt.setDouble(idx++, pre_max_lead.doubleValue());
      } else {
        cstmt.setNull(idx++, Types.DOUBLE);
      }

      // executeQuery returns values via a resultSet
      cstmt.execute();
      int status = cstmt.getInt(1);
      if (status < 0) {
        database.processWarnings(cstmt);
      }
      cstmt.close();
    }
    catch (Exception exc) {
      message = "Unable to update target fields in database for obsid  ";
      message += obsid.toString(); 
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




}
 
    


