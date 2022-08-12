// Copyright (c) 2003-2015,2022 Smithsonian Astrophysical Observatory
// You may do anything you like with this file except remove this copyright.

//****************************************************************************
//                              CXC Data System
//****************************************************************************

//****************************************************************************
//                               WEBREPORTS 
//                               DB Package
//--------------------------------------------------------------------------
//                               DBConnection
//****************************************************************************
package db;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.HashMap;
import java.text.*;

import ascds.LogMessage;

import info.User;
import info.ReviewReport;
import info.Proposal;
import info.GradeConflictList;
import info.GradeConflict;
import info.ReportsConstants;

/** 
 * This class handles the database queries for the Webreports application.
 *
 */
public class DBConnection 
{

  /** 
   * directory path for the webreports files
   */
  private String reportDataPath;

  private String mainQuery;

  /**
   * sybase interfaces file
   */
  private String interfacesFilename;

  /** 
    * properties file for database connection 
   */
  private Properties sqlUser;


  /** 
   * database connection
   */
  private Connection conn;

  /** 
   * debug flag
   */
  private boolean showDebug;


  /**
    *  Constructor 
    *
    * @param reportDataPath   home directory for the webreports files
    * @param showDebug   boolean value indicating whether to print debug
    *                    statements to the log file
    * @exception IOException  file i/o errors
   */
  public DBConnection(String reportDataPath,boolean showDebug ) 
	throws IOException
  {
    this.reportDataPath = reportDataPath;
    this.showDebug = showDebug;
    sqlUser = new Properties();

    init();
  }

  /**
    * Sets the database user name
    *
    * @param user  database user name
   */
  public void setUser(String user)
  {
    sqlUser.setProperty("user",user);
  }

  /**
    * Sets the database password 
    *
    * @param pwd  database password
   */
  public void setPassword(String pwd)
  {
    sqlUser.setProperty("password",pwd);
  }

  /**
    * Sets the SQL server
    *
    * @param server  SQL server to use for database connection
   */
  public void setSQLServer(String server)
  {
    sqlUser.setProperty("server",server);
  }

  /**
    * load user information from file
    * @exception IOException  file i/o errors
   */
  private void loadServerInfo() throws IOException
  {
    
      String nextLine;
      String str=null;
      BufferedReader in = null;
      String reportUserFile = reportDataPath;
      reportUserFile += "/.htreport";
      in = new BufferedReader(new FileReader(reportUserFile));

      while ((nextLine = in.readLine()) != null) {
        int valueStart = nextLine.indexOf("=");
        if (valueStart > -1) {
          str=nextLine.trim().substring(valueStart+1);
        }
        if ( nextLine.trim().startsWith("user=") ) {
          setUser(str);  
        }
        if ( nextLine.trim().startsWith("server=") ) {
          setSQLServer(str);  
        }
      }
      if (in != null) {
        in.close();
      }
      reportUserFile = reportDataPath;
      reportUserFile += "/.htreportp";
      in = new BufferedReader(new FileReader(reportUserFile));
      while ((nextLine = in.readLine()) != null) {
          str=nextLine.trim();
          setPassword(str);  
      }
      if (in != null) {
        in.close();
      }
  }


  /**
   * initialize the sybase interfaces file and 
   * user specific sql server information
    * @exception IOException  file i/o errors
   */
  private void init() throws IOException
  {
     
    //Load in the environment variables

    String interfacesDirectory = System.getenv("SYBASE");
    if (interfacesDirectory == null)
      throw new IOException("Environment variable SYBASE not found.");

    interfacesFilename = interfacesDirectory + "/interfaces";


    //Load in user/server information
    loadServerInfo();

    mainQuery = new String("select distinct proposal_number,type,category_descrip,convert(varchar(1),pi.first),pi.last,title,joint,multi_cycle,linked_propnum,panel_name,panel.panel_id,primary_reviewer,secondary_reviewer,(pri.last +  ',' + substring(pri.first,1,1)),(sec.last + ',' + substring(sec.first,1,1))");
    mainQuery += "  from proposal,proposal_review pr,panel,context" +
                 "  ,axafusers..person_short pi " +
                 "  ,axafusers..person_short pri " +
                 "  ,axafusers..person_short sec " +
                 "  where proposal.ao_str=context.current_ao_str " +
                 "  and proposal.piid = pi.pers_id" +
                 "  and proposal.proposal_id = pr.proposal_id" +
                 "  and pr.panel_id = panel.panel_id";

  }


    /*****
     * This routine is called to populate the user name, reviewer type,
     * and panel assignment for the user object
     *
     * @param theUser  User class containing database userid to use for
     *                 loading user/panel information from the database.
     * @exception IOException  file i/o errors
     * @exception SQLException SQL errors
     */
    public void loadUser(User theUser) 	throws SQLException, IOException  {
	String sql_stmt = "";
	PreparedStatement stmt=null;
	ResultSet rs=null;
	
	try {
	  sql_stmt =  "select last,first,member_type,panel_name,institution";
	  sql_stmt += " from panel,context c,panel_member pm";
	  sql_stmt += " ,axafusers..person_short ps";
	  sql_stmt += " where ps.pers_id = ? " ;
	  sql_stmt += " and ps.pers_id = pm.pers_id";
	  sql_stmt += " and pm.panel_id = panel.panel_id";
	  sql_stmt += " and panel.ao = c.current_ao_id" ;
	  
	  if (showDebug) {
	      LogMessage.println(sql_stmt + " -- " + theUser.getUserID() );
	  }
	  int ii;
	  
	  Connection conn = getConnection();
	  if (conn != null) {
	      
	      stmt = conn.prepareStatement(sql_stmt);
              stmt.setInt(1,theUser.getUserID());
	      
	      rs = stmt.executeQuery();
	      
	      while (rs.next()) {
		  ii=1;
		  theUser.setUserName(rs.getString(ii++));
		  theUser.setUserFirst(rs.getString(ii++));
		  theUser.setUserType(rs.getString(ii++));
		  theUser.setPanelName(rs.getString(ii++));
		  theUser.setUserInstitution(rs.getString(ii++));
	      }
	  }

	} catch (SQLException sqle) {
	    throw new SQLException("An error occurred in retrieving the data from the db: " + sqle.toString());
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
		throw new SQLException("An error occurred while closing the SQL query: " + sqle.getMessage());
	    }
	} // finally
    }
    /**
     * Returns the current AO
     *
     * @return String returns current context AO string from the database
     */
    public String getCurrentAO()
    {
	String sql_stmt;
	Statement stmt=null;
	ResultSet rs=null;
        String aoStr = new String("");

	try {
	  Connection conn = getConnection();
	  sql_stmt =  "select current_ao_str from context";
	  int ii;
	  
	  if (conn != null) {
	      
	      stmt = conn.createStatement();
	      rs = stmt.executeQuery(sql_stmt);
	      while (rs.next()) {
		  ii=1;
                  aoStr = rs.getString(ii++);
              }
          }
	} catch (SQLException sqle) {
	    LogMessage.printException(sqle);
	    LogMessage.println("An error occurred in retrieving the data from the db: " + sqle.toString());
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
		LogMessage.println("An error occurred while closing the SQL query: " + sqle.getMessage());
	    }
	} // finally

      return aoStr;
    }
        
	

    /*****
     * This routine is called to create vector of user object
     * for all the reviewers assigned to the given panel.
     *
     * @param panelName  panel used for query
     * @return Vector    all reviewers assigned to the specified panel
     * @exception IOException  file i/o errors
     * @exception SQLException SQL errors
     */
    public Vector<User> loadPanelMembers(String panelName ) 	
	throws SQLException, IOException  
    {
	String sql_stmt = "";
	PreparedStatement stmt=null;
	ResultSet rs=null;
        Vector<User> members = new Vector<User>();
	
	try {
          // for all regular panels use the panel_member table
          if (panelName.equals("all")) {
	    sql_stmt =  "select ps.pers_id,last,first,member_type,panel_name,";
	    sql_stmt +=  "institution,email";
	    sql_stmt += " from panel,context c,panel_member pm";
	    sql_stmt += " ,axafusers..person_short ps";
	    sql_stmt += " where ps.pers_id = pm.pers_id";
	    sql_stmt += " and pm.panel_id = panel.panel_id";
	    sql_stmt += " and panel.ao = c.current_ao_id" ;
            sql_stmt += " order by panel_name,member_type,lower(last)";
	  }
          else if (!panelName.equals("BPP")) {
	    sql_stmt =  "select ps.pers_id,last,first,member_type,panel_name,";
	    sql_stmt +=  "institution,email";
	    sql_stmt += " from panel,context c,panel_member pm";
	    sql_stmt += " ,axafusers..person_short ps";
	    sql_stmt += " where panel_name = ?";
	    sql_stmt += " and ps.pers_id = pm.pers_id";
	    sql_stmt += " and pm.panel_id = panel.panel_id";
	    sql_stmt += " and panel.ao = c.current_ao_id" ;
            sql_stmt += " order by member_type,lower(last)";
	  }
          else   {
            // The BPP panel consists of pundits and all panel chairs
	    sql_stmt =  "select ps.pers_id,last,first,member_type,panel_name,";
	    sql_stmt +=  "institution,email";
	    sql_stmt += " from panel,context c,panel_member pm";
	    sql_stmt += " ,axafusers..person_short ps";
	    sql_stmt += " where (lower(member_type) = \'chair\'";
	    sql_stmt += " or lower(member_type) like \'pundit%\')";
	    sql_stmt += " and ps.pers_id = pm.pers_id";
	    sql_stmt += " and pm.panel_id = panel.panel_id";
	    sql_stmt += " and panel.ao = c.current_ao_id" ;
            sql_stmt += " order by member_type,lower(last)";
	  }
          
	  int ii;
	  
	  Connection conn = getConnection();
	  if (conn != null) {
	      
	      stmt = conn.prepareStatement(sql_stmt);
              if (!panelName.equals("BPP") && !panelName.equals("all")) {
                  stmt.setString(1,panelName);
              }
	      rs = stmt.executeQuery();
	      
	      while (rs.next()) {
		  ii=1;
                  User theUser = new User();
		  theUser.setUserID(rs.getInt(ii++));
		  theUser.setUserName(rs.getString(ii++));
		  theUser.setUserFirst(rs.getString(ii++));
		  theUser.setUserType(rs.getString(ii++));
		  theUser.setPanelName(rs.getString(ii++));
		  theUser.setUserInstitution(rs.getString(ii++));
		  theUser.setUserEmail(rs.getString(ii++));
                  members.add(theUser);
	      }

	  }

	} catch (SQLException sqle) {
	    LogMessage.printException(sqle);
	    LogMessage.println("db: loadPanelMembers: " +sql_stmt);
	    throw new SQLException("An error occurred in retrieving the data from the db: " + sqle.toString());
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
		throw new SQLException("An error occurred while closing the SQL query: " + sqle.getMessage());
	    }
	} // finally

      return members;
    }


    /* 
     * Routine used by administrators to see a list of all panels
     * for the current AO.
     *
     * @return Vector All panels for the current AO
     */
    public Vector<String> getPanels() {
	Connection conn = null;
	Statement stmt=null;
	ResultSet rs=null;
	Integer panelID = new Integer(-1);
	String panelName = null;
	Vector<String> panelList = new Vector<String>();
	
	String sqlQuery = new String( "select panel_id, panel_name ");
	sqlQuery += " from panel p, context c";
	sqlQuery += " where c.current_ao_id = p.ao";
	sqlQuery += " and panel_name != 'BPP' ";
	sqlQuery += " order by panel_name";

	try {
	    int ii;
	    
	    conn = getConnection();
	    stmt = conn.createStatement();
	    
	    rs = stmt.executeQuery(sqlQuery);
	    while (rs.next()) {
		ii=1;
		
		panelID = rs.getInt(ii++);
		panelName = rs.getString(ii++);
		panelList.add(panelID.toString());
		panelList.add(panelName);
	    }
	} catch(Exception ex) {
	    LogMessage.printException(ex);
	    LogMessage.println("Error - caught exception in dbconnection, getpanels");
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
	        LogMessage.printException(sqle);
		//throw new SQLException("An error occurred while closing the SQL query: " + sqle.getMessage());
		LogMessage.println("Caught exception in getpanels: " + sqle.getMessage());
	    }
	} // finally

	return(panelList);
    }

    /** 
     * Routine used to set the panels for LP/VLP/TOO-LPS
     * These reports may have additional reviewers/panels.
     *
     * @param rr ReviewReport
     * @param proposalNumber proposal number
     * @exception SQLException SQL errors
     */
    public void getLPPanels(ReviewReport rr, String proposalNumber) 
	throws SQLException
    {
	Connection conn = null;
	PreparedStatement stmt=null;
	ResultSet rs=null;
        String pnum;

	String sqlQuery = new String("select panel_name from proposal p, proposal_review pr,panel where p.proposal_id = pr.proposal_id ");
	sqlQuery += " and p.proposal_number = ? and pr.panel_id = panel.panel_id and panel_name != 'LP' ";
	sqlQuery += "  order by panel.panel_id desc ";


	try {
	    int ii;

	    conn = getConnection();
	    stmt = conn.prepareStatement(sqlQuery);
            stmt.setString(1,proposalNumber);
	    rs = stmt.executeQuery();
	    while (rs.next()) {
		ii=1;

		String panelName = rs.getString(ii++);
		rr.addPanelName(panelName);
	    }
	} catch(Exception ex) {
	    LogMessage.printException(ex);
	    LogMessage.println("Error - caught exception in dbconnection, getLPReviewerIDs");
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
		throw new SQLException("An error occurred while closing the SQL query: " + sqle.getMessage());
	    }
	} // finally
    }
    /*
     * Routine used to set the panels for LP/VLP/TOO-LPS
     *
     * @return HashMap    of proposal numbers and their assigned panels
     * @exception SQLException SQL errors
     */
    public HashMap<String,String> getLPPanels() 
	throws SQLException
    {
	Connection conn = null;
	PreparedStatement stmt=null;
	ResultSet rs=null;
        String pnum;
        HashMap<String,String> panels = new HashMap<String,String>();

	String sqlQuery = new String("select proposal_number,panel_name from proposal p, proposal_review pr,panel,context where p.proposal_id = pr.proposal_id");
	sqlQuery += " and p.ao_str=context.current_ao_str";
	sqlQuery += " and pr.panel_id = panel.panel_id and panel_name != 'LP'  and panel_name != 'XVP'";
	sqlQuery += "  order by proposal_number,panel.panel_id ";


	try {
	    conn = getConnection();
	    stmt = conn.prepareStatement(sqlQuery);
	    rs = stmt.executeQuery();
	    while (rs.next()) {
                int ii=1;
                String propnum = rs.getString(ii++);
                String pname="";
                pname = panels.get(propnum);
                if (pname != null && pname.length() > 1) 
                  pname += ",";
                else 
                  pname="";
                pname += rs.getString(ii++);
                panels.put(propnum,pname);
	    }
	} catch(Exception ex) {
	    LogMessage.printException(ex);
	    LogMessage.println("Error - caught exception in dbconnection, getLPPanels");
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
		throw new SQLException("An error occurred while closing the SQL query: " + sqle.getMessage());
	    }
	} // finally
      return panels;
    }

    /**
     *
     * This routine returns true if the chair is on a panel with LP/VLP proposals
     * @param panelName  current panel
     * @return boolean true if this panel has any LP/VLP proposals 
     * @exception SQLException SQL errors
     */
    public boolean chairHasLP(String panelName) throws SQLException {
	boolean chairHasLPReports = false;
	String currentPanelName = null;
	Connection conn = null;
	PreparedStatement stmt=null;
	ResultSet rs=null;
	
	String sqlQuery = new String("select distinct panel_name");
	sqlQuery += " from proposal,proposal_review,panel,context";
	sqlQuery += " where proposal.ao_str=context.current_ao_str";
	sqlQuery += " and proposal.proposal_id = proposal_review.proposal_id";
	sqlQuery += " and proposal_review.panel_id = panel.panel_id";
	sqlQuery += " and panel.panel_name = ?";
	sqlQuery += " and (((select count(proposal_id) from panel_proposal pp";
	sqlQuery += " where pp.proposal_id = proposal.proposal_id) > 1)";
	sqlQuery += " or proposal.type like '%LP%' or proposal.type like '%VP%' ) ";
	sqlQuery += " order by proposal_number";
        if (showDebug) {
          LogMessage.println("db: chairHasLP: " + panelName );
        }


	try {
	    int ii;

	    conn = getConnection();
	    stmt = conn.prepareStatement(sqlQuery);
            stmt.setString(1,panelName);
	    rs = stmt.executeQuery();
	    while (rs.next()) {
		ii=1;
		currentPanelName = rs.getString(ii++);

		if(panelName.equals(currentPanelName)) {
		    chairHasLPReports = true;
		    break;
		}
		
	    }
	} catch(Exception ex) {
	    LogMessage.printException(ex);
	    LogMessage.print("DBConnection::chairHasLP - Caught exception:");
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
		throw new SQLException("An error occurred while closing the SQL query: " + sqle.getMessage());
	    }
	} // finally

        return chairHasLPReports;
    }


    /***
     * This routine retrieves all LP/VLPs assigned to the same subject 
     * categories as the assigned chair panel.  Code assumes panels with
     * same subject area have all the same subject areas
     *
     * @param panelName  panel name
     * @return Vector all LP/VLPs reports assigned to the panel
     * @exception SQLException SQL errors
     */
    public Vector<ReviewReport> getChairLPReports(String panelName) throws SQLException {
	Vector<ReviewReport> reportsList = new Vector<ReviewReport>();

	PreparedStatement stmt=null;
        LogMessage.println("db: getChairLPreports, panel=" + panelName);
        
        String sqlQuery = mainQuery;
        sqlQuery += "  and pr.primary_reviewer *= pri.pers_id";
	sqlQuery += "  and pr.secondary_reviewer *= sec.pers_id";
        sqlQuery += " and pr.panel_id in (" +
   		" select panel.panel_id from panel,panel_subcat " +
        	" where panel.panel_id  = panel_subcat.panel_id " +
        	" and panel_subcat.catid in (select catid " + 
                " from panel_subcat ps,panel pnl" +
                " where ps.panel_id = pnl.panel_id and panel_name=? ))" ;
        sqlQuery += " and ((select count(proposal_id) from panel_proposal pp";
	sqlQuery += " where pp.proposal_id = proposal.proposal_id) > 1";
	sqlQuery += " or proposal.type like '%LP%' or proposal.type like '%VP%')";
        sqlQuery += " and pr.panel_id = panel.panel_id";
	sqlQuery += " order by proposal_number";

        try {
	  Connection conn = getConnection();
	  if (conn != null) {
	    stmt = conn.prepareStatement(sqlQuery);
            stmt.setString(1,panelName);

            reportsList = retrieveReports(stmt,ReportsConstants.LP); 
          }
	} catch(Exception ex) {
	    LogMessage.printException(ex);
	    LogMessage.println("Error - caught exception in dbconnection, getChairLPReports ");
	} finally {
	    try {
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
		throw new SQLException("An error occurred while closing the SQL query: " + sqle.getMessage());
	    }
	} // finally

	return(reportsList);
	
    }


    /***
     *
     * Routine used to retrieve the LP/VLPs by given proposal numbers 
     *
     * @param  props  comma separated list of proposal numbers ie: "12201212","12201213" 
     * @return Vector all LP/VLPs 
     * @exception SQLException SQL errors
     */
    public Vector<ReviewReport> getLPReports(String props) 
	throws SQLException {
	Vector<ReviewReport> reportsList = new Vector<ReviewReport>();
	PreparedStatement stmt=null;
        
        LogMessage.println("db: getLPReports for subset");
        String sqlQuery = mainQuery;
        sqlQuery += "  and pr.primary_reviewer *= pri.pers_id";
	sqlQuery += "  and pr.secondary_reviewer *= sec.pers_id";
	sqlQuery += "  and proposal_number in ( " + props + ") ";
	sqlQuery += "  order by proposal_number,panel_id asc";

        try {
	  Connection conn = getConnection();
	  if (conn != null) {
	    stmt = conn.prepareStatement(sqlQuery);
            //stmt.setString(1,props);

            reportsList = retrieveReports(stmt,ReportsConstants.LP); 
          }
	} catch(Exception ex) {
	    LogMessage.printException(ex);
	    LogMessage.println("Error - caught exception in dbconnection, getLPReports");
	} finally {
	    try {
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
		throw new SQLException("An error occurred while closing the SQL query: " + sqle.getMessage());
	    }
	} // finally

	return(reportsList);

     }

    /***
     *
     * Routine used to retrieve all the LP/VLPs and any other proposal
     * assigned to more than 1 panel.
     *
     * @return Vector all LP/VLPs 
     * @exception SQLException SQL errors
     */
    public Vector<ReviewReport> getLPReports() throws SQLException {
	Vector<ReviewReport> reportsList = new Vector<ReviewReport>();
	

	PreparedStatement stmt=null;
        LogMessage.println("db: getLPReports");
        
        String sqlQuery = mainQuery;
        sqlQuery += "  and pr.primary_reviewer *= pri.pers_id";
	sqlQuery += "  and pr.secondary_reviewer *= sec.pers_id";
	sqlQuery += "  and ((select count(proposal_id) from panel_proposal pp";
	sqlQuery += "  where pp.proposal_id = proposal.proposal_id) > 1";
	sqlQuery += "  or proposal.type like '%LP%' or proposal.type like '%VP%')";
	sqlQuery += "  order by proposal_number,panel_id asc";

        try {
	  Connection conn = getConnection();
	  if (conn != null) {
	    stmt = conn.prepareStatement(sqlQuery);
            reportsList = retrieveReports(stmt,ReportsConstants.LP); 
          }
	} catch(Exception ex) {
	    LogMessage.printException(ex);
	    LogMessage.println("Error - caught exception in dbconnection,getLPReports ");
	} finally {
	    try {
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
		throw new SQLException("An error occurred while closing the SQL query: " + sqle.getMessage());
	    }
	} // finally

		
	return(reportsList);

	
    }

    /***
     * This routine initializes a  primary or peer review for a single proposal 
     * on a panel.  While it returns a vector for coding consistency, there
     * should be only 1 entry
     *
     * @param panelName current panel name
     * @param propNum    proposal number
     * @param beforePR  true if this is before the peer review
     * @return Vector all review reports for the panel
     * @exception SQLException SQL errors
     */
    public Vector<ReviewReport> getReportByPropNumPanel(String panelName, 
	String propNum,boolean beforePR) throws SQLException {
        String rptType;
	Vector<ReviewReport> reportsList = new Vector<ReviewReport>();
   
        if (beforePR) {
           rptType = ReportsConstants.PRIMARY;
           LogMessage.println("db: getReportByPropNumPanel before PR=" + panelName);
        }
        else {
           rptType = ReportsConstants.PEER;
           LogMessage.println("db: getReportByPropNumPanel panel=" + panelName); 
        }
	PreparedStatement stmt=null;
        
        String sqlQuery = mainQuery;
        sqlQuery += "  and pr.primary_reviewer *= pri.pers_id";
	sqlQuery += "  and pr.secondary_reviewer *= sec.pers_id";
	sqlQuery += " and panel.panel_name = ? ";
        sqlQuery += " and proposal_number= ? " ;
	sqlQuery += "  order by proposal_number";

        try {
	  Connection conn = getConnection();
	  if (conn != null) {
	    stmt = conn.prepareStatement(sqlQuery);
            stmt.setString(1,panelName);
            stmt.setString(2,propNum);

            reportsList = retrieveReports(stmt,rptType); 
          }
	} catch(Exception ex) {
	    LogMessage.printException(ex);
	    LogMessage.println("Error - caught exception in dbconnection,getReportByPropNumPanel ");
	} finally {
	    try {
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
		throw new SQLException("An error occurred while closing the SQL query: " + sqle.getMessage());
	    }
	} // finally

	return(reportsList);
    }


    /***
     * This routine gets both the primary and secondary reports on the panel 
     * if it is before the peer review.  It get the peer review reports if
     * it is during/after the peer review.
     *
     * @param panelName current panel name
     * @param beforePR  true if this is before the peer review
     * @return Vector all review reports for the panel
     * @exception SQLException SQL errors
     */
    public Vector<ReviewReport> getReportsOnPanelByName(String panelName, boolean beforePR) throws SQLException {
	Vector<ReviewReport> reportsList = new Vector<ReviewReport>();
        String rptType;
   
        if (beforePR) {
           rptType = ReportsConstants.PRIMARY;
           LogMessage.println("db: getReportsOnPanelByName, before PR=" + panelName); 
        }
        else {
           rptType = ReportsConstants.PEER;
           LogMessage.println("db: getReportsOnPanelByName, peer=" + panelName); 
        }
	PreparedStatement stmt=null;
        
        String sqlQuery = mainQuery;
        sqlQuery += "  and pr.primary_reviewer = pri.pers_id";
	sqlQuery += "  and pr.secondary_reviewer *= sec.pers_id";
	sqlQuery += " and panel.panel_name = ? ";
	sqlQuery += " order by lower(pri.last), pri.pers_id,proposal_number";

        try {
	  Connection conn = getConnection();
	  if (conn != null) {
	    stmt = conn.prepareStatement(sqlQuery);
            stmt.setString(1,panelName);

            reportsList = retrieveReports(stmt,rptType); 
          }
	} catch(Exception ex) {
	    LogMessage.printException(ex);
	    LogMessage.println("Error - caught exception in dbconnection, getReportsOnPanelByName");
	} finally {
	    try {
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
		throw new SQLException("An error occurred while closing the SQL query: " + sqle.getMessage());
	    }
	} // finally


	if(beforePR) {
	    getSecondaryReports(panelName, reportsList);
	}

	return(reportsList);
    }

    /***
     * Routine used to retrieve review report information from the database
     *
     * @param stmt  prepared sql statement to execute
     * @param rptType  type of report (primary,secondary,peer,final)
     * @return Vector all reports that match the @param criteria 
     * @exception SQLException SQL errors
     */
    private Vector<ReviewReport> retrieveReports(PreparedStatement stmt,String rptType) 
	throws SQLException 
    {
	Vector<ReviewReport> reportsList = new Vector<ReviewReport>();
	ResultSet rs=null;
	
        if (showDebug) {
          LogMessage.println("retrieveReports: " + rptType  );
        }
	String previousPropNum = null;
	Proposal newProp = null;
	ReviewReport rr = null;

	try {
	    int ii;

	    rs = stmt.executeQuery();
	    while (rs.next()) {
		ii=1;

		String proposalNumber = rs.getString(ii++);
		String type = rs.getString(ii++);
                String category = rs.getString(ii++);
                String piFirstName = rs.getString(ii++);
                String piLastName = rs.getString(ii++);
                String piName = piLastName + ", " + piFirstName;
                String title =  rs.getString(ii++);
		String joint = rs.getString(ii++);
		String multicycle = rs.getString(ii++);
		String linkedProposalNumber = rs.getString(ii++);
		String panelName = rs.getString(ii++);
		int panelID = rs.getInt(ii++);
		int primaryReviewerID = rs.getInt(ii++);
		int secondaryReviewerID = rs.getInt(ii++);
		String primaryLast = rs.getString(ii++);
		String secondaryLast = rs.getString(ii++);
		
		if(previousPropNum != null &&
		   previousPropNum.equals(proposalNumber)) {
		    rr.addPrimaryReviewer(primaryReviewerID);
		    rr.addPanelName(panelName);
		    //LogMessage.println("Adding primary reviewer id " + 
		    //primaryReviewerID + " to prop" + proposalNumber);
		} else {
		    //peer review reports
		    newProp = new Proposal(proposalNumber);
                    newProp.setCategory(category);
                    newProp.setPI(piName);
                    newProp.setPILast(piLastName);
                    //LogMessage.println("Adding " + piLastName);
                    newProp.setPIFirst(piFirstName);
                    newProp.setTitle(title);
                    newProp.setJoint(joint);
                    newProp.setMulticycle(multicycle);
                    newProp.setLinkedProposalNumber(linkedProposalNumber);
                    newProp.setProposalType(type);
                    newProp.setPrimaryReviewer(primaryLast);
                    newProp.setSecondaryReviewer(secondaryLast);
                    newProp.setPanelName(panelName);

		    rr = new ReviewReport();
                    rr.setDataPath(reportDataPath);
		    rr.setProposal(newProp);
                    if (rptType.equals(ReportsConstants.SECONDARY)) {
                      rr.setReviewerID(secondaryReviewerID);
                      rr.setReviewerName(secondaryLast);
                    }
                    else {
                      rr.setReviewerID(primaryReviewerID);
                      rr.setReviewerName(primaryLast);
                    }
		    rr.setSecondaryReviewerID(secondaryReviewerID);
		    rr.setSecondaryReviewerName(secondaryLast);
		    rr.setPrimaryReviewerID(primaryReviewerID);
		    rr.setPrimaryReviewerName(primaryLast);
		    rr.setType(rptType); 
		    rr.setPanelName(panelName); 
		    rr.setPanelID(panelID); 
                    rr.initLock();
		    reportsList.add(rr);
		}
		
		previousPropNum = new String(proposalNumber);
	    }
	} catch(Exception ex) {
	    LogMessage.printException(ex);
	    LogMessage.println("Error - caught exception in query, retrieveReports");
	} finally {
	    try {
		if (rs != null)
		    rs.close();
	    }  catch (SQLException sqle) {
		throw new SQLException("An error occurred while closing the SQL result set: " + sqle.getMessage());
	    }
	} // finally

	return(reportsList);

	
    }


    /***
     * This routine returns the secondary review reports for the panel
     *
     * @param panelName panel name
     * @param reportsList current reports already retrieved
     * @return Vector reports list with secondary reports appended  
     * @exception SQLException SQL errors
     */
    public Vector<ReviewReport> getSecondaryReports(String panelName, 
	Vector<ReviewReport> reportsList) throws SQLException {

	PreparedStatement stmt=null;
        Vector<ReviewReport> secondaryList = null; 
        
        LogMessage.println("db: getSecondaryReports"); 
        String sqlQuery = mainQuery;
        sqlQuery += "  and pr.primary_reviewer *= pri.pers_id";
	sqlQuery += "  and pr.secondary_reviewer *= sec.pers_id";
	sqlQuery += " and panel.panel_name = ? ";
        sqlQuery += " order by lower(sec.last),sec.pers_id, proposal_number";

        try {
	  Connection conn = getConnection();
	  if (conn != null) {
	    stmt = conn.prepareStatement(sqlQuery);
            stmt.setString(1,panelName);

            secondaryList = retrieveReports(stmt,ReportsConstants.SECONDARY);
          }
        
	} catch(Exception ex) {
	    LogMessage.printException(ex);
	    LogMessage.println("Error - caught exception in dbconnection, getSecondaryReports");
	} finally {
	    try {
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
		throw new SQLException("An error occurred while closing the SQL query: " + sqle.getMessage());
	    }
	} // finally

        if (secondaryList != null) {
          for (int ii=0;ii< secondaryList.size();ii++) {
            reportsList.add(secondaryList.get(ii));
          }
        }
	return(reportsList);
    }


    /** 
     * Returns a Vector of ReviewReport objects assigned to a reviewer
     *
     * @param userID reviewer id 
     * @param beforePR true if before the Peer Review
     * @return Vector ReviewReport reports for given reviewer
    */
    public Vector<ReviewReport> getReportsByUserID(int userID, boolean beforePR) {
	Vector<ReviewReport> reportsList = null;

        LogMessage.println("db: getReportsByUserID, " +userID); 
	String sqlQuery =  new String("select distinct proposal_number,type,category_descrip,convert(varchar(1),pi.first),pi.last,title,joint,multi_cycle,linked_propnum,panel_name,panel.panel_id,primary_reviewer,secondary_reviewer,(pri.last+',' + substring(pri.first,1,1)),(sec.last+',' + substring(sec.first,1,1))");
	sqlQuery += " from proposal p,panel,context c";
  	sqlQuery += " ,proposal_review pr, axafusers..person_short pi";
 	sqlQuery += " ,axafusers..person_short pri,axafusers..person_short sec";
 	sqlQuery += " where p.ao_str = c.current_ao_str" ;
	sqlQuery += " and p.proposal_id = pr.proposal_id"; 
	sqlQuery += " and p.piid = pi.pers_id"; 
	sqlQuery += " and pr.panel_id = panel.panel_id";
	sqlQuery += " and pr.primary_reviewer *= pri.pers_id";
	sqlQuery += " and pr.secondary_reviewer *= sec.pers_id";
	sqlQuery += " and (pr.primary_reviewer = ?";
	sqlQuery += " or  pr.secondary_reviewer = ?)";
	sqlQuery += " order by proposal_number";

	try {
	    reportsList = reportExecuteQuery(sqlQuery, userID, beforePR);
	} catch(Exception ex) {
	    LogMessage.println("Error - caught exception in dbconnection, getreportsbyuserid");
	}

	return reportsList;
    }

    /**
     * Get proposal information for a given proposal number
     * Also determines if there are constrained targets
     *   If type == "TOO" or number of targets that are time constrained (ie.
     *   the time_critical field in the target table is Y) is greater than 0, 
     *   the *   proposal is constrained. A Value of "p" in the time_critical 
     *   field means preferred.
     *
     * @param proposalNumber proposal number
     * @return Proposal proposal information for given proposal
     * @exception SQLException SQL errors
     */
    public Proposal getProposal(String proposalNumber) throws SQLException {
	Proposal newProp = null;
	Connection conn = null;
	PreparedStatement stmt=null;
	ResultSet rs=null;

	String sqlQuery = new String("select distinct joint,multi_cycle,category_descrip,convert(varchar(1),pi.first) , pi.last,title,type,max(time_critical)");
	sqlQuery += " from proposal p, axafusers..person_short pi,target t";
	sqlQuery += " where proposal_number = ?";
	sqlQuery += " and p.piid=pi.pers_id and p.proposal_id *= t.proposal_id";
	sqlQuery += " group by proposal_number,category_descrip,pi.first,pi.last,title";


	try {
	    int ii;
	    
	    conn = getConnection();
	    stmt = conn.prepareStatement(sqlQuery);
	    stmt.setString(1,proposalNumber); 
	    rs = stmt.executeQuery();
	    while (rs.next()) {
		ii=1;

		String joint = rs.getString(ii++);
		String multicycle = rs.getString(ii++);
		String category = rs.getString(ii++);
		String piFirstName = rs.getString(ii++);
		String piLastName = rs.getString(ii++);
		String piName = new String(piLastName + ", " + piFirstName);
		String title =  rs.getString(ii++);
		String type =  rs.getString(ii++);
                String constrainedResult = rs.getString(ii++);
                if (rs.wasNull() ) {
                  constrainedResult = "N";
                }
		    
		newProp = new Proposal(proposalNumber);
		newProp.setJoint(joint);
		newProp.setMulticycle(multicycle);
		newProp.setCategory(category);
		newProp.setPI(piName);
		newProp.setPIFirst(piFirstName);
		newProp.setPILast(piLastName);
		newProp.setTitle(title);
		newProp.setProposalType(type);

		if(type.indexOf("TOO") > -1) {
                  constrainedResult="Y";
                }
		newProp.setConstrainedTargets(constrainedResult); 

	    }

	} catch(Exception ex) {
	    LogMessage.printException(ex);
	    LogMessage.println("Error - caught exception in dbconnection, getproposal");
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
		throw new SQLException("An error occurred while closing the SQL query: " + sqle.getMessage());
	    }
	} // finally




	return(newProp);
    }


    /**
      * Get the report type for a proposal for the given reviewer.
      * Types can be primary/peer or secondary/secondary peer.
      * 
      *
      * @param userID  reviewer id
      * @param proposalNumber proposal number
      * @param beforePR  true, if before the peer review
      * @return  String report type assigned to the reviewer for the proposal number
     * @exception SQLException SQL errors
      */
    public String getReportType(int userID, String proposalNumber, boolean beforePR) throws SQLException {
	String returnType = ReportsConstants.PEER;
	Connection conn = null;
	PreparedStatement stmt=null;
	ResultSet rs=null;

	String sqlQuery = new String("select primary_reviewer, secondary_reviewer");
	sqlQuery += " from proposal_review pr, proposal p";
	sqlQuery += " where p.proposal_number = ?";
	sqlQuery += " and p.proposal_id = pr.proposal_id";
        if (showDebug) {
           LogMessage.println("db:getReportType " + sqlQuery);
        }

	try {
	    int ii;

	    conn = getConnection();
	    stmt = conn.prepareStatement(sqlQuery);
	    stmt.setString(1,proposalNumber);
	    
	    rs = stmt.executeQuery();
	    while (rs.next()) {
		ii=1;
		int primaryID = rs.getInt(ii++);
		int secondaryID = rs.getInt(ii++);

		if(userID == primaryID) {
		    if(beforePR) {
			returnType = ReportsConstants.PRIMARY;
		    } else {
			returnType = ReportsConstants.PEER;
		    }
		} else if(userID == secondaryID) {
		    if(beforePR) {
			returnType = ReportsConstants.SECONDARY;
		    } else {
			returnType = ReportsConstants.SECONDARYPEER;
		    }
		    

		}
	    }
	} catch(Exception ex) {
	    LogMessage.printException(ex);
	    LogMessage.println("Error - caught exception in dbconnection, getreporttype");
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
		throw new SQLException("An error occurred while closing the SQL query: " + sqle.getMessage());
	    }
	} // finally


	return(returnType);
    }




  /** 
   * Execute the SQL query and build the vector of Review Reports.
   * The userID is used to determine the type of report if it's before 
   * the peer review (primary or secondary).
   *
   * @param sqlStmt SQL statement
   * @param userID  reviewer ID
   * @param beforePR true, if before the peer review
   * @return Vector  review reports that satisfy the SQL query
   * @exception SQLException SQL errors
   */
    private Vector<ReviewReport> reportExecuteQuery(String sqlStmt, int userID, boolean beforePR) throws SQLException {
	Connection conn = null;
	Vector<ReviewReport> reportsList = new Vector<ReviewReport>();
	PreparedStatement stmt=null;
	ResultSet rs=null;
	int ii;

        if (showDebug) {
	  LogMessage.println("reportExecuteQuery: " + sqlStmt);
        }
	
	try {
	    conn = getConnection();
	    stmt = conn.prepareStatement(sqlStmt);
            stmt.setInt(1,userID);
            stmt.setInt(2,userID);
	    
	    rs = stmt.executeQuery();
	    
	    while (rs.next()) {
		ii=1;
                String proposalNumber = rs.getString(ii++);
                String type = rs.getString(ii++);
                String category = rs.getString(ii++);
                String piFirstName = rs.getString(ii++);
                String piLastName = rs.getString(ii++);
                String piName = piLastName + ", " + piFirstName;
                String title =  rs.getString(ii++);
                String joint =  rs.getString(ii++);
                String multicycle =  rs.getString(ii++);
                String linkedProposalNumber =  rs.getString(ii++);
                String panelName = rs.getString(ii++);
		int panelID = rs.getInt(ii++);
                int primaryReviewerID = rs.getInt(ii++);
                int secondaryReviewerID = rs.getInt(ii++);
                String primaryLast = rs.getString(ii++);
                String secondaryLast = rs.getString(ii++);

                Proposal newProp = new Proposal(proposalNumber);
                newProp.setCategory(category);
                newProp.setPI(piName);
                newProp.setTitle(title);
                newProp.setJoint(joint);
                newProp.setMulticycle(multicycle);
                newProp.setLinkedProposalNumber(linkedProposalNumber);
                newProp.setProposalType(type);

		
		// Create the proposal object and the review reports
		ReviewReport primaryRR = new ReviewReport();
                primaryRR.setDataPath(reportDataPath);
		primaryRR.setProposal(newProp);
		primaryRR.setType(ReportsConstants.PRIMARY);
		primaryRR.setReviewerName(primaryLast);
		primaryRR.setReviewerID(primaryReviewerID);
		primaryRR.setSecondaryReviewerName(secondaryLast);
		primaryRR.setSecondaryReviewerID(secondaryReviewerID);
		primaryRR.setPanelName(panelName);
		primaryRR.setPanelID(panelID);
		
		ReviewReport secondaryRR = new ReviewReport();
                secondaryRR.setDataPath(reportDataPath);
		secondaryRR.setProposal(newProp);
		secondaryRR.setType(ReportsConstants.SECONDARY);
		secondaryRR.setReviewerName(secondaryLast);
		secondaryRR.setReviewerID(secondaryReviewerID);
		secondaryRR.setPrimaryReviewerName(primaryLast);
		secondaryRR.setPrimaryReviewerID(primaryReviewerID);
		secondaryRR.setPanelName(panelName);
		secondaryRR.setPanelID(panelID);
		
		//Add to the list of all the reports
		if(userID == primaryReviewerID) {
		    if(!beforePR) {
			//If we're at peer review, then the person who is
			//the primary reviewer will see the peer review report.
			primaryRR.setType(ReportsConstants.PEER);
		    }
                    primaryRR.initLock();
		    reportsList.add(primaryRR);
		} else if(userID == secondaryReviewerID) {
		    if(!beforePR) {
			//secondary reviewers should view the peer review report
			//which will be edited by the primary reviewer.
			secondaryRR.setReviewerID(primaryReviewerID); 
			secondaryRR.setType(ReportsConstants.SECONDARYPEER);
		    }
                    secondaryRR.initLock();
		    reportsList.add(secondaryRR);
		}
	    }
	    
	    
	} catch (Exception sqle) {
	    throw new SQLException("An error occured in retrieving the data from the db: " + sqle.toString());
	}  finally {
	    try {
		if (rs != null)
		    rs.close();
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    } catch (SQLException sqle) {
	        LogMessage.printException(sqle);
		LogMessage.println("SQLException during close(): " + sqle.getMessage());
	    }
	} // finally

	return reportsList;
    }

  /**
   * Retrieve all proposals for a given panel
   *
   * @param panelName panel name
   * @return Vector  vector of all proposals assigned to the panel for the current AO
   * @exception SQLException SQL errors
   */
  public Vector<Proposal> loadProposalsOnPanel( String panelName )
        throws SQLException
  {
    String sql_stmt = "";

    PreparedStatement stmt=null;
    ResultSet rs=null;
    Vector<Proposal> dblist = new Vector<Proposal>();

    try {
        sql_stmt =  "select distinct proposal_number,convert(varchar(1),ps.first),ps.last,title,";
        sql_stmt += "type,joint,multi_cycle,category_descrip,linked_propnum,";
        sql_stmt += "ps.institution,ps.email,ps.pers_id";

        if (!panelName.equals("BPP")) {
          sql_stmt += ",(pri.last + ',' + substring(pri.first,1,1)),(sec.last+',' + substring(sec.first,1,1)),panel_name ";
          sql_stmt += " from proposal p,panel,context c,panel_proposal pp";
          sql_stmt += " ,proposal_review pr";
          sql_stmt += " ,axafusers..person_short ps";
          sql_stmt += " ,axafusers..person_short pri";
          sql_stmt += " ,axafusers..person_short sec";
          sql_stmt += " where p.ao_str = c.current_ao_str" ;
          sql_stmt += " and p.piid = ps.pers_id";
          sql_stmt += " and p.proposal_id = pp.proposal_id";
          sql_stmt += " and pp.panel_id = panel.panel_id";
	  sql_stmt += " and p.proposal_id *= pr.proposal_id";
          sql_stmt += " and pp.panel_id *= pr.panel_id";
	  sql_stmt += " and pr.primary_reviewer *= pri.pers_id";
	  sql_stmt += " and pr.secondary_reviewer *= sec.pers_id";
          if (panelName.indexOf("ALL") >= 0) {
          }
          else if (panelName.indexOf(",") < 0) {
            sql_stmt += " and panel.panel_name = ? ";
          } else {
            sql_stmt += " and panel.panel_name in( " + panelName + ")";
          }
          sql_stmt += " order by panel_name,proposal_number";
        } else {
          sql_stmt += ",null,null,null";
          sql_stmt += " from proposal p,panel,context c,panel_proposal pp";
          sql_stmt += " ,axafusers..person_short ps";
          sql_stmt += " where p.ao_str = c.current_ao_str" ;
          sql_stmt += " and p.piid = ps.pers_id";
          sql_stmt += " and p.proposal_id = pp.proposal_id";
          sql_stmt += " and pp.panel_id = panel.panel_id";
          sql_stmt += " and (p.type like '%LP%' or p.type like '%VP%' or ";
          sql_stmt += " (select count(proposal_id) from panel_proposal pp2";
          sql_stmt += " where pp2.proposal_id = p.proposal_id) > 1) ";
          sql_stmt += " order by proposal_number";
        }

        int ii;

        Connection conn = getConnection();
        if (conn != null) {
          stmt = conn.prepareStatement(sql_stmt);
          if (!panelName.equals("BPP") &&
              !panelName.equals("ALL") &&
              panelName.indexOf(",") < 0) {
            stmt.setString(1,panelName);
            LogMessage.println("Loading proposals for panel " +  panelName);
          }
          rs = stmt.executeQuery();

          while (rs.next())
          {
            ii=1;
            Proposal prop = new Proposal();
            prop.setProposalNumber(rs.getString(ii++));
            String piFirstName = rs.getString(ii++);
            String piLastName = rs.getString(ii++);
            String piName = piLastName + ", " + piFirstName;
            prop.setPI(piName);
            prop.setPIFirst(piFirstName);
            prop.setPILast(piLastName);
            prop.setTitle(rs.getString(ii++));
            prop.setProposalType(rs.getString(ii++));
            prop.setJoint(rs.getString(ii++));
            prop.setMulticycle(rs.getString(ii++));
            prop.setCategory(rs.getString(ii++));
            prop.setLinkedProposalNumber(rs.getString(ii++));
            prop.setPIInstitution(rs.getString(ii++));
            prop.setPIEmail(rs.getString(ii++));
            prop.setPIID(rs.getInt(ii++));
            prop.setPrimaryReviewer(rs.getString(ii++));
            prop.setSecondaryReviewer(rs.getString(ii++));
            prop.setPanelName(rs.getString(ii++));
   

            //Add to the list
            dblist.add(prop);
          }
        }
        else {
          throw new SQLException("A connection to the database couldn't be made.");
        }
    }
    catch (Exception sqle) {
      LogMessage.println("loadProposalsOnPanel: " + sql_stmt);
      LogMessage.printException(sqle);
      throw new SQLException("An error occurred in retrieving proposals from the database: " + sqle.toString());
    }
    finally {
      try {
        if (rs != null)
          rs.close();
        if (stmt != null)
          stmt.close();
	if (conn != null) 
	  conn.close();
      }
      catch (SQLException sqle) {
        throw new SQLException("An error occurred while closing the SQL query: "
                               + sqle.getMessage());
      }
    } // finally

    return dblist;
  }

    /**
     * Get Pundit Primary name for a proposal
     *
     * @param proposalNumber proposal number
     * @return String punditName for given proposal or ""
     * @exception SQLException SQL errors
     */
    public String getPrimaryPundit(String proposalNumber) throws SQLException {
	String punditName="";
	Connection conn = null;
	PreparedStatement stmt=null;
	ResultSet rs=null;

	String sqlQuery = new String("select distinct (last) " +
         " from proposal p,axafusers..person_short ps,proposal_review pr,panel" +
	 " where proposal_number = ?" +
	 " and p.proposal_id=pr.proposal_id and pr.panel_id = panel.panel_id" +
	 " and panel_name like '%P'  " +
         " and pr.primary_reviewer = ps.pers_id" );
 

	try {
	    int ii;
	    
	    conn = getConnection();
	    stmt = conn.prepareStatement(sqlQuery);
	    stmt.setString(1,proposalNumber); 
	    rs = stmt.executeQuery();
	    while (rs.next()) {
		ii=1;
		punditName = rs.getString(ii++);
	    }

	} catch(Exception ex) {
	    LogMessage.printException(ex);
	    LogMessage.println("Error - caught exception in dbconnection, getPrimaryPundit");
	} finally {
	    try {
		if (rs != null)
		    rs.close();
		if (stmt != null)
		    stmt.close();
		if (conn != null) 
		    conn.close();
	    }  catch (SQLException sqle) {
		throw new SQLException("An error occurred while closing the SQL query: " + sqle.getMessage());
	    }
	} // finally




	return(punditName);
    }

  /**
   * Retrieve all CoIs for a given proposal
   *
   * @param propList vector of Proposals for which CoIs are retrieved
   * @exception SQLException SQL errors
   */
  public void loadCoIs( Vector<Proposal> propList)
        throws SQLException
  {
    String sql_stmt;

    PreparedStatement stmt=null;
    ResultSet rs=null;

    Connection conn = getConnection();
    sql_stmt =  "select convert(varchar(1),ps.first),ps.last,\n";
    sql_stmt += "ps.institution,ps.email,ps.pers_id ";
    sql_stmt += "from proposal p,axafusers..person_short ps,coin";
    sql_stmt += " where p.proposal_number = ?" ; 
    sql_stmt += " and p.proposal_id = coin.proposal_id";
    sql_stmt += " and coin.pers_id = ps.pers_id";
    
    try {
      if (conn != null) {
        stmt = conn.prepareStatement(sql_stmt);
      
        Iterator pidx = propList.iterator();
        while ( pidx.hasNext() ) {
          Proposal prop = (Proposal)pidx.next();
   
          int ii;
          stmt.setString(1,prop.getProposalNumber());
          rs = stmt.executeQuery();

          while (rs.next())
          {
            ii=1;
            User coi = new User();
            coi.setUserFirst(rs.getString(ii++));
            coi.setUserName(rs.getString(ii++));
            coi.setUserInstitution(rs.getString(ii++));
            coi.setUserEmail(rs.getString(ii++));
            coi.setUserID(rs.getInt(ii++));
   

            //Add to the list
            prop.addCoI(coi);
          }
        }
      }
      else {
        throw new SQLException("A connection to the database couldn't be made.");
      }
    }
    catch (Exception sqle) {
      throw new SQLException("An error occurred in retrieving CoIs from the database: " + sqle.toString());
    }
    finally {
      try {
        if (rs != null)
          rs.close();
        if (stmt != null)
          stmt.close();
	if (conn != null) 
	  conn.close();
      }
      catch (SQLException sqle) {
        throw new SQLException("An error occurred while closing the SQL query: "
                               + sqle.getMessage());
      }
    } // finally

  }
  /**
   * Retrieve all Conflicts for a given reviewer
   *
   * @param reviewerId  reviewer pers_id from database
   * @param propList vector of Proposals for reviewer
   * @exception SQLException SQL errors
   */
  public void loadConflicts( int reviewerId, Vector<Proposal> propList)
        throws SQLException
  {
    String sql_stmt;

    PreparedStatement stmt=null;
    ResultSet rs=null;

    Connection conn = getConnection();
    sql_stmt =  "select distinct proposal_number from proposal,reviewer_conflicts where proposal.proposal_id = reviewer_conflicts.proposal_id and pers_id = " + reviewerId +  " and reviewer_conflicts.type='Personal' and inactive != 'Y'";
    try {
      if (conn != null) {
        stmt = conn.prepareStatement(sql_stmt);
        rs = stmt.executeQuery();
        int pid;
        while (rs.next()) {
          String pno = rs.getString(1);
          Iterator pidx = propList.iterator();
          while ( pidx.hasNext() ) {
            Proposal prop = (Proposal)pidx.next();
            if (prop.getProposalNumber().equals(pno)) {
              prop.setPersonalConflict("C");
              break;
            }
          }
   
        }
      }
      else {
        throw new SQLException("A connection to the database couldn't be made.");
      }
    }
    catch (Exception sqle) {
      throw new SQLException("An error occurred in retrieving Conflicts from the database: " + sqle.toString());
    }
    finally {
      try {
        if (rs != null)
          rs.close();
        if (stmt != null)
          stmt.close();
	if (conn != null) 
	  conn.close();
      }
      catch (SQLException sqle) {
        throw new SQLException("An error occurred while closing the SQL query: "
                               + sqle.getMessage());
      }
    } // finally

  }

  /**
   * Retrieve all Conflicts for a given panel
   *
   * @param panelName  panel name
   * @param gradeList vector of proposal grade/conflict for panel
   * @exception SQLException SQL errors
   */
  public void loadConflictsByPanel( String panelName,GradeConflictList gradeList)
        throws SQLException
  {
    String sql_stmt;

    PreparedStatement stmt=null;
    ResultSet rs=null;

    Connection conn = getConnection();
    if (!panelName.equalsIgnoreCase("BPP")) {
      sql_stmt = "select distinct proposal.proposal_number,rev.last,convert(varchar(1),rev.first),rev.pers_id,panel.panel_name" +
       " from proposal,reviewer_conflicts rc,context,panel," +
       " panel_proposal,panel_member, axafusers..person_short rev" +
       " where proposal.proposal_id = rc.proposal_id" +
       " and rc.proposal_id = panel_proposal.proposal_id" +
       " and panel.panel_name= ? " +
       " and panel_proposal.panel_id = panel.panel_id" +
       " and rc.pers_id = panel_member.pers_id" +
       " and panel_member.panel_id = panel.panel_id" +
       " and rc.pers_id = rev.pers_id" +
       " and inactive != 'Y' and rc.type='Personal' " +
       " and proposal.ao_str = context.current_ao_str";
    } else {
      sql_stmt = "select distinct proposal.proposal_number,rev.last,convert(varchar(1),rev.first),rev.pers_id,'BPP' panel_name" +
       " from proposal,reviewer_conflicts rc,context," +
       " panel_member, axafusers..person_short rev, panel" +
       " where proposal.proposal_id = rc.proposal_id" +
       " and rc.pers_id = panel_member.pers_id" +
       " and panel_member.panel_id = panel.panel_id" +
       " and (proposal.type like '%LP%' or proposal.type like 'XVP')" +
       " and (member_type = 'Chair' or member_type like 'Pundit%')" +
       " and rc.pers_id = rev.pers_id" +
       " and inactive != 'Y' and rc.type='Personal' " +
       " and proposal.ao_str = context.current_ao_str";
    }
    LogMessage.println("Conflicts: " + sql_stmt);

    try {
      if (conn != null) {
        stmt = conn.prepareStatement(sql_stmt);
        if (!panelName.equalsIgnoreCase("BPP")) {
          stmt.setString(1,panelName);
        }
        rs = stmt.executeQuery();
        while (rs.next()) {
          int ii=1;
          String pno = rs.getString(ii++);
          String revLast = rs.getString(ii++);
          String revFirst = rs.getString(ii++);
          String revName = revLast + ", " + revFirst;
          Integer revId = rs.getInt(ii++);
          String revPanel = rs.getString(ii++);
          GradeConflict gc = new GradeConflict(panelName,pno,revId.toString(),revName,revPanel);
          gradeList.add(gc);
        }
      }
      else {
        throw new SQLException("A connection to the database couldn't be made.");
      }
    }
    catch (Exception sqle) {
      LogMessage.printException(sqle);
      throw new SQLException("An error occurred in retrieving Conflicts from the database: " + sqle.toString());
    }
    finally {
      try {
        if (rs != null)
          rs.close();
        if (stmt != null)
          stmt.close();
	if (conn != null) 
	  conn.close();
      }
      catch (SQLException sqle) {
        throw new SQLException("An error occurred while closing the SQL query: "
                               + sqle.getMessage());
      }
    } // finally

  }




  /**
   * initiate a SQL database connection
   *
   * @return Connection database connection
   * @exception SQLException SQL errors
   */
  public Connection getConnection() throws SQLException
  {
    String sqlsrv = null;

    try {
      // Setup driver
      DriverManager.setLoginTimeout(20);
      DriverManager.registerDriver((Driver)Class.forName
         ( "com.sybase.jdbc4.jdbc.SybDriver" ).newInstance());
      } 
      catch (Exception e) {
        throw new SQLException("The jdbc driver could not be accessed: " + e.toString());
      }

      // Try to connect to the local sql server
      try {
        sqlsrv =  sqlUser.getProperty("server");
        if (sqlsrv == null)
          throw new SQLException("SQL server not set.");

         
        String url = "jdbc:sybase:jndi:file://" + interfacesFilename + "?" + sqlsrv ;
        //LogMessage.println(url);
        conn = DriverManager.getConnection(url, sqlUser);
        Statement stmt = conn.createStatement();
        stmt.execute("use proposal");
        stmt.close();
      } 
      catch (SQLException sqle) {
	LogMessage.printException(sqle);
        throw new SQLException("Unable to connect to SQL server " + sqlsrv + "\n");
      }

      return conn;
  }

    /**
      * Closes the current database connection
      * @exception SQLException SQL errors
     */
    public void close() throws SQLException  {
	if (conn != null) {
	    conn.close();
	}
    }

    /** 
      * Update the personal conflicts in database
      *
      * @param reviewer_id  the reviewer database id
      * @param propnum  the proposal number
      * @param type  type of reviewer conflict
      * @param flag  Y for inactive conflict, else N
      * @exception SQLException SQL errors
     */
    public void updateReviewerConflict(int reviewer_id, String propnum, 
	String type,String flag)
	throws SQLException
    {
        CallableStatement cstmt=null;
        Connection conn=null;

        try {
	  conn = getConnection();
          // now insert or update the  conflict entry
          String command = "{ ? = call reviewer_conflict_update(?,?,?,?)}";
          cstmt = conn.prepareCall (command);
          cstmt.setInt(2, reviewer_id);
          cstmt.setString(3, propnum);
          cstmt.setString(4, type);
          cstmt.setString(5, flag);
          cstmt.registerOutParameter(1, Types.INTEGER);
          cstmt.execute();
          int status = cstmt.getInt(1);
          if (status < 0) {
              processWarnings(cstmt);
          }
        }
        catch (Exception exc) {
          LogMessage.printException(exc);
          throw new SQLException("PAS conflicts unable to update database.");
        }
        finally {
          try {
          if (cstmt != null) cstmt.close();
          if (conn != null) conn.close();
        }
        catch (SQLException sqle) {
          throw new SQLException("An error occurred while closing the SQL query: "
                               + sqle.getMessage());
        }
      } // finally
    }
        

    public void processWarnings( Statement statement )
   	 throws SQLException
    {
      String message = null;
      SQLWarning warning = statement.getWarnings();
   
      while ( warning != null ) {
        if ( message == null ) {
          message = warning.getMessage();
        }
        else {
          message += warning.getMessage();
        }
        warning = warning.getNextWarning();
      }
      if ( message != null ) {
        throw new SQLException( "SQL failure: " + message );
      }
  
    }

}
 
    


