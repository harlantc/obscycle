<%@ page session="true" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Timeout Warning</title>
  </head>

  <body>
<span style="color:#dd0000; font-weight:bold;"> Chandra Panel Access Site: Timeout Warning</span>
<br>
Your session has been inactive and will be timed out in 5 minutes. 
<br>
Please <b>Save</b> your data if necessary, or exit the reports if you are finished.
<br><br>
<input style="background-color:red;padding:5px;" type="button" OnClick="window.close();" VALUE="Close window">

    
  </body>
</html>
