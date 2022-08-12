<%@ page session="true" import="java.io.*,ascds.LogMessage " %>
<% 
//response.setHeader("Cache-Control","no-store"); //HTTP 1.1
  String message = "";
  String fileName = "";
  String type = (String)request.getParameter("type");
  if (type == null || type.length() <= 0) {
    fileName = (String)session.getAttribute("triggerFilename");
  }
  else {
    fileName = (String)session.getAttribute(type);
  }
  response.addCookie(new Cookie("JSESSIONID", session.getId()));

  boolean goodFile = true;


  String contentType;
  if (fileName == null) {
    contentType = "text/plain";
    message += "File name is null." ;
    goodFile = false;
  }
  else if (fileName.indexOf(".") == 0) {
     contentType = "text/plain";
     message += "Invalid File." ;
     goodFile = false;
  }
  else {
    File xfile = new File(fileName);
    if (xfile.exists()) {

      String subStr = fileName.substring(fileName.lastIndexOf('/'));
      String dispStr = "attachment;filename=\"";
      dispStr += subStr;
      dispStr += "\"";
      if (fileName.indexOf(".pdf") > 0) {
        contentType = "application/pdf";
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
      else if (fileName.indexOf(".txt") > 0 ||  
               fileName.indexOf(".submit") > 0 ||
               fileName.indexOf("conflicts") > 0) {
        contentType = "text/plain";
      } 
      else {
        contentType = "text/plain";
        message += " Unexpected File Type";
        goodFile = false;
      }

    }
    else {
      contentType = "text/plain";
      message += "File not found" ;
      goodFile = false;
    }
  }
  response.setContentType(contentType);
 
  if (goodFile) {
    if (contentType.indexOf("text") >= 0) {
       BufferedReader br = null;
       try {
         File tFile = new File(fileName);
         br = new BufferedReader(new InputStreamReader(new FileInputStream(tFile),"UTF-8"));
         StringBuilder sb = new StringBuilder();
         String line;
         while((line = br.readLine())!= null){
           sb.append(line+"\n");
         }
         out.println(sb.toString());
         br.close();
       } catch (Exception e) {
          if (br != null) br.close();
       }
    }
    else { 
      FileInputStream in = new FileInputStream(fileName);
      BufferedInputStream bis = new BufferedInputStream(in);
      int i = 0;
      while ((i = bis.read()) != -1) {
        out.write(i);
      }
      bis.close();
      in.close();
    }
  }
  else {
    out.write("Invalid file access. ");
    out.write(message);
    LogMessage.println("Invalid file access: " + fileName);

  }

%>
