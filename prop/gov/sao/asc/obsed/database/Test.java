// Test code for the use of the sp_helprotect system procedure.

package gov.sao.asc.obsed.database;

import java.util.Properties;
import java.util.Enumeration;

public class Test extends Object
{
  public Test()
  {
  }


  public static void main( String[] args )
  {
    // Test the functionality using the target table.
    System.out.println( "Testing sp_helprotect system procedure." );

    // Open the connection to the database.
    ASCDatabase db =
      new ASCDatabase( "http://www-axaf.harvard.edu:8024/obsed/data" );
    db.setServer( "SYBASE" );
    db.setDatabase( "axafocat" );
    db.setUser( "arcops" );
    db.setPassword( "arcopspd".toCharArray() );
    db.connect();

    // Get the target table permissions for the `arcops' user.
    Properties perm = db.getPermissions( "target", "eduser" );
    
    // Loop through the result printing the relevant information.
    for ( Enumeration e = perm.keys(); e.hasMoreElements(); )
    {
      String key = (String) e.nextElement();
      System.out.println( "Key: " + key + ", Value: " + (String) perm.get( key ) );
    }

    // Close the db and exit.
    db.close();
    System.exit( 0 );
  }
}
