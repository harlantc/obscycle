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

/**
 * Database class contains the generic class to connect to the
 * database.  
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Driver;

import ascds.LogMessage;


public class Database 
{

  private String dataPath;
  private Properties sqlUser;
  private String interfacesFilename;
  private boolean showDebug;
  private Connection conn;
  private String databaseName;
  private String ocatServer;
  private String propServer;


  /**
   * Database Constructor
   * @param dataPath  directory path for Trigger TOO files
   * @param database  SQL database to use for connection
   * @param showDebug   value determining whether to log debug messages
   * @param isTOO   value determining whether to TOO/Trigger, false if DDTManager 
   * @exception IOException  file  i/o issue
   */
  public Database(String dataPath,
	String database, boolean showDebug,boolean isTOO ) 
	throws IOException
  {
    this.dataPath = dataPath;
    this.showDebug = showDebug;
    sqlUser = new Properties();
    databaseName = database;

    init(true,isTOO);
  }


  /**
   * Database Constructor which defaults to axafocat database
   * @param userName  database user name
   * @param userPwd  database password
   * @param dataPath  directory path for Trigger TOO files
   * @param showDebug   value determining whether to log debug messages
   * @param isTOO   value determining whether to TOO/Trigger, false if DDTManager 
   * @exception IOException  file  i/o issue
   */
  public Database(String userName,String userPwd,String dataPath,
	boolean showDebug ,boolean isTOO ) 
	throws IOException
  {
    this.dataPath = dataPath;
    this.showDebug = showDebug;
    sqlUser = new Properties();
    setUser(userName);
    setPassword(userPwd);
    databaseName = new String("axafocat");

    init(false,isTOO);
  }

  /**
   * Database Constructor which defaults to axafocat database
   * @param userName  database user name
   * @param userPwd  database password
   * @param dataPath  directory path for Trigger TOO files
   * @param database  SQL database to use for connection
   * @param showDebug   value determining whether to log debug messages
   * @param isTOO   value determining whether to TOO/Trigger, false if DDTManager 
   * @exception IOException  file  i/o issue
   */
  public Database(String userName,String userPwd,String dataPath,
	String database, boolean showDebug, boolean isTOO) 
	throws IOException
  {
    this.dataPath = dataPath;
    this.showDebug = showDebug;
    sqlUser = new Properties();
    setUser(userName);
    setPassword(userPwd);
    databaseName = database;

    init(false,isTOO);
  }

  /**
    * setUser  
    * @param user  database user name 
    */
  public void setUser(String user)
  {
    sqlUser.setProperty("user",user);
  }

  /**
    * setPassword
    * @param pwd  database password
    */
  public void setPassword(String pwd)
  {
    sqlUser.setProperty("password",pwd);
  }

  /**
    * setSQLServer
    * @param server SQL server name
    */
  public void setSQLServer(String server)
  {
    sqlUser.setProperty("server",server);
  }

  public String getUser()
  {
     String userName = sqlUser.getProperty("user");

     return userName;
  }
  public String getServer()
  {
     String serverName = sqlUser.getProperty("server");

     return serverName;
  }
  public String getPassword()
  {
     String pwd = sqlUser.getProperty("password");

     return pwd;
  }

  public String getDDTServer()
  {
     return ocatServer;
  }

  // used by the TOO Manager propconflict
  public String getProposalServer() 
  {
    return propServer;
  }
 

  /**
    * loadUserInfo - loads database user info from file
    * @exception IOException file i/o issue
    */
  private void loadUserInfo() throws IOException
  {
    
      String theDataPath = dataPath;
      BufferedReader in = null;
      
      theDataPath += "/.htpwd";
      in = new BufferedReader(new FileReader(theDataPath));
      String nextLine;
      while ((nextLine = in.readLine()) != null) {
        String str=null;
        str=nextLine.trim();
        setPassword(str);  
      }
      if (in != null) {
        in.close();
      }

      theDataPath = dataPath;
      theDataPath += "/.htuser";
      BufferedReader  inU = new BufferedReader(new FileReader(theDataPath));
      while ((nextLine = inU.readLine()) != null) {
        String str=null;
        str=nextLine.trim();
        setUser(str);  
      }
      if (inU != null) {
        inU.close();
      }
  }

  /**
    * loadServerInfo - loads database server info from file
    * @param fname  server info file
    * @exception IOException file i/o issue
    * @return String server info
    */
  private String loadServerInfo(String fname) throws IOException
  {
    
      String theDataPath = dataPath;
      BufferedReader in = null;
      
      theDataPath += "/" + fname;
      in = new BufferedReader(new FileReader(theDataPath));

      String nextLine;
      String str=null;
      while ((nextLine = in.readLine()) != null) {
        str=nextLine.trim();

      }
      if (in != null) {
        in.close();
      }
      return str;
  }

  /**
   * initialize the sybase interfaces file and 
   * user specific sql server information
   * @param loadUser  true if need to load User info
   * @param isTOO     true if TOO, false if DDT
   * @exception IOException file i/o issue
   */
  private void init(boolean loadUser,boolean isTOO) throws IOException
  {
     
    //Load in the environment variables
    String interfacesDirectory = System.getenv("SYBASE");
    if (interfacesDirectory == null)
      throw new IOException("Environment variable SYBASE not found.");

    interfacesFilename = interfacesDirectory + "/interfaces";

    ocatServer = loadServerInfo(".htserver");
    propServer = loadServerInfo(".htddtserver");

    if (isTOO) {
      setSQLServer(ocatServer);
      LogMessage.println("Setting TOO to " + ocatServer);
    } else {
      setSQLServer(propServer);
      LogMessage.println("Setting DDT to " + propServer);
    }

    if (loadUser) {
      //Load in user information
      loadUserInfo();
    }
  }


  /**
   * connect to database sql server
   * @return  database Connection
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

         sqlUser.setProperty("database",databaseName);

        String url = "jdbc:sybase:jndi:file://" + interfacesFilename + "?" + sqlsrv;

        //String url = "jdbc:sybase:Tds:" +  localSocket + "/" + databaseName ;
        LogMessage.println("User = " + sqlUser.getProperty("user") + "-" + sqlsrv + "  " + url);
  

        conn = DriverManager.getConnection(url, sqlUser);

      } 
      catch (SQLException sqle) {
        LogMessage.printException(sqle);
        while (sqle != null)
        {
           LogMessage.println("SQL Error : State=["+
             sqle.getSQLState() +"] "+ sqle.getMessage());
           sqle = sqle.getNextException();
        } 
        throw new SQLException("Unable to connect to SQL server " + sqlsrv);
      }



      return conn;
  }

    public void close() throws SQLException  {
	if (conn != null) {
	    conn.close();
	}
    }

  /**
    * Examine a call statement to see if any warnings were generated.
    * Throw an SQL exception using the warning text as the exception
    * message.
    * @param statement SQL statement 
    * @exception SQLException SQL errors
    *
    */
  public void processWarnings( Statement statement )
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

}
