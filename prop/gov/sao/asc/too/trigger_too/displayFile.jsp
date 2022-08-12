<%@ page session="true" import="java.io.*, org.apache.commons.lang3.*" %>
<% 
  String message = (String)session.getAttribute("message");
  String fileName = (String)session.getAttribute("displayFilename");
  String deleteFile = (String)request.getParameter("deleteFile");


%>
<%@ include file = "triggertooHead.html" %>
<body class="body" >
<pre>
<%
   if (fileName != null && fileName.endsWith(".tmp") &&
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

       if (deleteFile.equals("yes") ) {
         if (tFile.isFile()) {
           tFile.delete();
         }
       }
     }
     catch (Exception exc) {
       out.write("Unable to display file.");
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
</body></html>
