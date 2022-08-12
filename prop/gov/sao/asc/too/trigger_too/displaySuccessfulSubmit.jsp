<%@ page session="true" import="java.io.*,  org.apache.commons.lang3.* " %>
<% 
  String fileName = (String)session.getAttribute("fileName");
%>
 
<%@ include file = "triggertooHead.html" %>

<body class="body">
<h3>The trigger for the TOO observation has been successfully submitted.
You will be notified of the status of your observation  by a member of the 
CXC staff.</h3>

<a href="/triggertoo/triggerTOO.jsp">Back to Chandra TOO Trigger Search Page</a>
<hr>

<pre>
<%
   if (fileName != null && fileName.endsWith(".submit") &&
        fileName.indexOf("trigger") >= 0) {

     BufferedReader br = null;
     try {
       File tFile = new File(fileName);
       br = new BufferedReader(new InputStreamReader(new FileInputStream(tFile),"UTF-8"));
       StringBuilder sb = new StringBuilder();
       String line;

       while((line = br.readLine())!= null){
         sb.append(line+"\n");
       }
       out.println(StringEscapeUtils.escapeHtml4(sb.toString()));
       br.close();

     }
     catch (Exception exc) {
       out.println("Unable to display file.");
     }
     finally {
        try {
          if (br != null)  br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
      }
   }

%>
</pre>
</body>
</html>
