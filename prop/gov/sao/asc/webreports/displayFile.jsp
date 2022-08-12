<%@ page session="true" trimDirectiveWhitespaces="true" import="java.io.*,info.User " %>
<% 
//response.setHeader("Cache-Control","no-store"); //HTTP 1.1
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {


response.addCookie(new Cookie("JSESSIONID", session.getId()));
User currentUser = (User)session.getAttribute("user");
boolean goodFile = true;
String proposalFileDir = (String)session.getAttribute("proposalFileDir");
String reportsDataPath = (String)session.getAttribute("reportsDataPath");
String tarDataPath = (String)session.getAttribute("tarDataPath");
String panelTar = (String)session.getAttribute("panelTar");
String bppTar = (String)session.getAttribute("bppTar");
String fullPath = null;
boolean adminAccess = false;
String subStr = "";
String dispStr = "";
String contentType;

if (currentUser != null) {


  String fileName = request.getParameter("fileName");
  if (fileName.indexOf('/') >= 0) {
    subStr = fileName.substring((fileName.lastIndexOf('/')+1));
    dispStr = "attachment;filename=\"";
    dispStr += subStr;
    dispStr += "\"";
  }
  if (proposalFileDir == null || fileName == null) {
     contentType = "text/plain";
     goodFile = false;
  }
  else if (fileName.indexOf(".cmts") >= 0 ) {
     if (currentUser.isReviewer() && 
         !fileName.startsWith(currentUser.getPanelName())) {
       goodFile = false;
     }
     contentType = "text/plain";
     proposalFileDir = reportsDataPath + "/cmtedits/";
  }
  else if (fileName.indexOf("adminGrades") >= 0 &&
           (currentUser.isAdmin() || currentUser.isDeveloper()) ) {
     contentType = "text/plain";
     adminAccess=true;
     fullPath = proposalFileDir;
  }
  else if (fileName.indexOf("panelTar") == 0) {
     contentType = "application/gzip";
     dispStr = "attachment;filename=\"chandraPanel.tar.gz\"";
     response.setHeader("Content-Disposition",dispStr);
     proposalFileDir = tarDataPath + "/";
  }
  else if (fileName.indexOf("bppTar") == 0) {
     contentType = "application/gzip";
     dispStr = "attachment;filename=\"chandraBPP.tar.gz\"";
     response.setHeader("Content-Disposition",dispStr);
     proposalFileDir = tarDataPath + "/";
  } 
  else if (fileName.indexOf(".") == 0) {
     contentType = "text/plain";
     goodFile = false;
  }
  else if (fileName.indexOf("..") >= 0) {
     contentType = "text/plain";
     goodFile = false;
  }
  else if (fileName.indexOf(".pdf") > 0) {
     contentType = "application/pdf";
     response.setHeader("Content-Disposition",dispStr);
  }
  else if (fileName.indexOf(".csv") > 0) {
     contentType = "text/csv";
     response.setHeader("Content-Disposition",dispStr);
  }
  else if (fileName.indexOf(".tsv") > 0) {
     contentType = "text/tsv";
     response.setHeader("Content-Disposition",dispStr);
  }
  else if (fileName.indexOf(".ps") > 0) {
     contentType = "application/postscript";
     response.setHeader("Content-Disposition",dispStr);
  }
  else if (fileName.indexOf(".tar.gz") > 0) {
     contentType = "application/x-tar";
     response.setHeader("Content-Disposition",dispStr);
  }
  else if (fileName.endsWith(".txt")    ||  
           fileName.endsWith(".list")   ||
           fileName.endsWith(".stats")  ||
           fileName.endsWith(".status") ||
           fileName.endsWith(".prelim_grades")  ||
           fileName.indexOf("technical/") == 0  ||
           fileName.indexOf("conflict_files/") == 0  ||
           fileName.indexOf("proposer_input/") == 0) {
     contentType = "text/plain";
  }
  else {
     contentType = "text/plain";
     goodFile = false;
  }

  response.setContentType(contentType);
 
  if (goodFile) {
    if (fileName.indexOf("panelTar") >= 0) {
      fullPath=proposalFileDir + "/" + panelTar;
    } else if (fileName.indexOf("bppTar") >=0) {
      fullPath=proposalFileDir + "/" + bppTar;
    }
    else if (!adminAccess) {
      fullPath = proposalFileDir + "/" + fileName;
    } 
    try {
      if (contentType.indexOf("text") >=0) {
        File fileDir = new File(fullPath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir),"UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;

        while((line = reader.readLine())!= null){
          sb.append(line+"\n");
        }
        out.println(sb.toString());
        reader.close();
      } else {
        Long fileSizeInBytes = new File(fullPath).length();
        // chrome failed without this most of the time, but not all :-)
        response.addHeader("Content-Length", fileSizeInBytes.toString());

        FileInputStream in = new FileInputStream(fullPath);
        ServletOutputStream sos = response.getOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
          sos.write(buf,0,len);
        }
        in.close();
        sos.flush();
        sos.close();
      }
    }
    catch (Exception ex) {
       contentType = "text/plain";
       out.write("Unable to read file." );
    }
  }
  else {
    contentType = "text/plain";
    out.write("Invalid file access ");
  }
}
else  {
     contentType = "text/plain";
     out.print("No session available");
}
%>
<% } catch (Exception e) {};
 } %>

