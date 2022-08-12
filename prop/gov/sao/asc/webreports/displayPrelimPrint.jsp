<%@ page session="true" import="java.util.*, info.*,org.apache.commons.lang3.StringEscapeUtils " %> <%
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {


  response.addCookie(new Cookie("JSESSIONID", session.getId()));
  PrelimGradesList prelimGradesList = (PrelimGradesList) session.getAttribute("prelimGradesList");
  Date now = new Date();
  String contentType = "text/csv";
  User currentUser = (User)session.getAttribute("user");
  String reviewerId  =  (String)session.getAttribute("reviewerID");
  Boolean isAnonymous = (Boolean)session.getAttribute("isAnonymous");
  if (isAnonymous == null) isAnonymous=true;
  
  if (currentUser != null) {
    try {
      String dispStr="attachment;filename=\"";
      String filename = "cxc_prelim_grades_" + reviewerId + ".csv";
      dispStr += filename;
      dispStr += "\"";
      response.setContentType(contentType);
      response.setHeader("Content-Disposition",dispStr);
      if (!isAnonymous)
        out.println("Proposal,Grade,Conflict,P.I.,Title");
      else
        out.println("Proposal,Grade,Conflict,Title");
      if (prelimGradesList != null) {
        for(int index=0; index < prelimGradesList.size(); index++) {
          Proposal prop = prelimGradesList.get(index);
          String grade;
          if (prop.getPrelimGrade().isNaN() || 
              prop.getPrelimGrade().doubleValue() < 0.0) {
            grade = new String("");
          }     
          else {
            grade = prop.getPrelimGrade().toString();
          }
          out.write(prop.getProposalNumber() + "," + grade + ",");
          out.write(prop.getPrelimGradeConflict() + "," );
          if (!isAnonymous) out.write('"' + prop.getPI() + '"' + "," );
          out.write('"' + prop.getTitle() + '"' );
          out.println("");
        }
      } 
      out.println("\n,,,,___Preliminary Grades as of " + now.toString() );
    } catch (Exception exc) {
      contentType = "text/plain";
      response.setContentType(contentType);
      out.write("Unable to access preliminary grades.\n\n");
    }
  }  else {
    contentType = "text/plain";
    response.setContentType(contentType);
    out.write("Invalid access\n\n");
  }
%>
<% } catch (Exception e) {};
 } %>

