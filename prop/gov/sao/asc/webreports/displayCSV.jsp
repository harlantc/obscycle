<%@ page session="true" import="java.io.*,info.User,org.apache.commons.lang3.StringEscapeUtils " %> <% 
//response.setHeader("Cache-Control","no-store"); //HTTP 1.1
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {


response.addCookie(new Cookie("JSESSIONID", session.getId()));
User currentUser = (User)session.getAttribute("user");
boolean goodFile = true;
String proposalFileDir = (String)session.getAttribute("proposalFileDir");
String reportsDataPath = (String)session.getAttribute("reportsDataPath");
String fullPath = null;
String subStr = "";
String dispStr = "";
String contentType = "text/plain";

if (currentUser != null) {

  String fileName = request.getParameter("fileName");

  if (proposalFileDir == null || fileName == null) {
     goodFile = false;
  }
  else if (fileName.indexOf(".tsv") >= 0 ) {
     proposalFileDir = reportsDataPath + "/lists/";
     if (fileName.indexOf('_') >= 0) {
       subStr = fileName.substring((fileName.indexOf('_') + 1));
     } else {
       subStr=fileName;
     }
     subStr = subStr.replaceAll("tsv","csv"); 
     dispStr = "attachment;filename=\"";
     dispStr += subStr;
     dispStr += "\"";
     contentType="text/csv";
  }
  else {
     goodFile = false;
  }

  response.setContentType(contentType);
 
  if (goodFile) {
    fullPath = proposalFileDir + "/" + fileName;
    try {
      File dataFile = new File(fullPath);
      String[] inputArr;
      String inputLine;
      BufferedReader fileBR = new BufferedReader(new FileReader(dataFile));
      response.setHeader("Content-Disposition",dispStr);

      while ((inputLine = fileBR.readLine()) != null) {
        inputArr = inputLine.split("\t");
        for (int ii=0;ii<inputArr.length;ii++) {
           if (ii != 0) out.write(",");
           if (inputArr[ii].indexOf("href") > 0) {
             inputArr[ii] = inputArr[ii].replaceAll("\\<.*?>","");
             inputArr[ii] = inputArr[ii].replaceAll(" PI","");
             inputArr[ii] = inputArr[ii].replaceAll(" Tech","");
           }
           inputArr[ii] = inputArr[ii].trim();
           inputArr[ii] = StringEscapeUtils.escapeCsv(inputArr[ii]);
           out.write(inputArr[ii] );

           //if (!inputArr[ii].matches("[0-9.]*")) {
             //inputArr[ii] = inputArr[ii].replaceAll("\"","\"\"");
             //out.write("\"" + inputArr[ii] + "\"");
           //} else {
             //out.write(inputArr[ii] );
           //}
        }
        out.println("");
      }

      fileBR.close();
    }

    catch (Exception ex) {
       contentType = "text/plain";
       response.setContentType(contentType);
       out.write("Unable to read file." );
    }
  }
  else {
    out.write("Invalid file access ");
  }
}
else  {
    out.write("No session available");
}
%>
<% } catch (Exception e) {};
 } %>

