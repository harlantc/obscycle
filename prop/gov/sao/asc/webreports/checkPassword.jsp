<%@ page import="java.util.*, java.io.*, info.ReportsConstants" %>
<%@ page import="java.security.MessageDigest, java.security.SecureRandom" %>

<%
String userName = request.getParameter("first");
String password = request.getParameter("password");

String userType = ReportsConstants.DEVELOPER;
String loggedInFilename = null;
int userID = ReportsConstants.DEVELOPERID;
boolean validUser = false;
String inputPasswd="";
String line;
String[] resultArray ;

  String fdir = System.getenv("OBSCYCLE_DATA_PATH");
  if (fdir != null) {
    fdir += "/prop/webreports/"; 
  } else {
    fdir = "/tmp";
  }

    // encrypt password
    MessageDigest md = null;
    try
    {
      String salt = "";
      String sFile = new String(fdir) + "/.htbdoor";
      BufferedReader sfileBF  = new BufferedReader(new FileReader(sFile));
      while( (line = sfileBF.readLine()) != null) {
         salt=line;
      }
      

      md = MessageDigest.getInstance("SHA-256"); //step 2
      md.update(salt.getBytes());
      byte[] bytes = md.digest(password.getBytes());
      StringBuilder sb = new StringBuilder();
      for(int i=0; i< bytes.length ;i++)
      {
        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
      }
      inputPasswd = sb.toString();
    }
    catch(Exception ex)
    {
      System.out.println("checkPassword: Unable to process password.");
      System.out.println(ex.getMessage());
    }


  String passwordDir =  fdir;
  fdir += "/rws/";
  String passwordFile = new String(passwordDir);
  passwordFile += ".htAdminPasswd";
  String basename = "";


if(inputPasswd != null) {
  String correctPasswd = null;

  BufferedReader passwdBF  = new BufferedReader(new FileReader(passwordFile));
  while( (line = passwdBF.readLine()) != null) {
	if(line.indexOf(userName) != -1) {
	   resultArray = line.split(":");
	   correctPasswd = resultArray[1];
	   if(correctPasswd.equals(inputPasswd)) {
	       validUser = true;
               if (resultArray.length > 2) {
                 userType = resultArray[2];
                 if (userType.toLowerCase().indexOf("admin") >= 0) {
                   userID = ReportsConstants.ADMINID;
                 }
               }
	       //LogMessage.println("User " + userName + " logged in");
	       Random r = new Random();
               basename = ".ht" + Long.toString(Math.abs(r.nextLong()), 36);
	       loggedInFilename = fdir +  basename;
               PrintWriter loggedInPW = new PrintWriter(new FileWriter(loggedInFilename));
	       loggedInPW.println(userType + " " + userID + " " + userName);
	       loggedInPW.close();
	   }
	}
    }
}
 

%> 

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Chandra Panel Access Site</title>
</head>
<body>	

<%
if(validUser) {
%>
<%= userType %> <%= userName %> logged in.
<hr>
<a href="/reports/login.jsp?file=<%=basename%>"> Enter Panel Access Site

<% } else { %>
Error: Cannot access the site.

<% } %>
    <hr>
  </body>
</html>


