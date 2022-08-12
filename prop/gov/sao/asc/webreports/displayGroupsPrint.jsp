<%@ page session="true" import="java.util.*, info.*" %> <%
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {


  response.addCookie(new Cookie("JSESSIONID", session.getId()));
  ProposalGroupsList groupsList = (ProposalGroupsList) session.getAttribute("groupsList");
  Date now = new Date();
  String contentType = "text/csv";
  User currentUser = (User)session.getAttribute("user");
  String panelName = groupsList.getPanelName();
  Boolean isAnonymous = (Boolean)session.getAttribute("isAnonymous");
  if (isAnonymous == null) isAnonymous=true;
  
  if (currentUser != null) {
    try {
      String dispStr="attachment;filename=\"";
      String filename = "cxc_groups_" + panelName + ".csv";
      dispStr += filename;
      dispStr += "\"";
      response.setContentType(contentType);
      response.setHeader("Content-Disposition",dispStr);
    
      if (!isAnonymous)
        out.println("Group,Proposal,P.I.,Title");
      else
        out.println("Group,Proposal,Title");
      if (groupsList != null) {

        for(int index=0; index < groupsList.size(); index++) {
          Proposal prop = groupsList.get(index);

          out.write('"' + prop.getGroupName() + '"' + "," );
          out.write(prop.getProposalNumber() + "," );
          if (!isAnonymous) out.write('"' + prop.getPI() + '"' + "," );
          out.write('"' + prop.getTitle() + '"' );
          out.println("");
        }
      } 
      out.println("\n,,,___Proposal Groups as of " + now.toString() );
    } catch (Exception exc) {
      contentType = "text/plain";
      response.setContentType(contentType);
      out.write("Unable to access proposal groups.\n\n");
    }
  } else {
    contentType = "text/plain";
    response.setContentType(contentType);
    out.write("Invalid access\n\n");
  }
%>
<% } catch (Exception e) {};
 } %>

