<%@ page session="true" import="java.util.*, java.io.*" %>
<%@ page import="info.*, org.apache.commons.lang3.StringEscapeUtils" %>
<% response.setHeader("Cache-Control","no-store"); //HTTP 1.1 
if (session == null || !request.isRequestedSessionIdValid()) {


} else {

try {


response.addCookie(new Cookie("JSESSIONID", session.getId()));
%>

<%@ include file = "reportsHead.html" %>
<body>  
<% 
  String currentPanel = (String)session.getAttribute("panel");
  String proposalFileDir = (String)session.getAttribute("proposalFileDir");
  ProposalReviewerList propList = (ProposalReviewerList)session.getAttribute("propList");
  Date now = new Date();
  int fcnt = 0;
%> 

<div class="techhdr">
Technical and Proposer Input Files for Panel <%= currentPanel %> as of  <%= now.toString()%> 
</div>

<%
if (propList != null) {

  for(int index=0; index < propList.size(); index++) {
    Proposal prop = propList.get(index);
    String propnum = prop.getProposalNumber();
    String techFile = prop.getTechnicalFile();
    if (techFile != null && techFile != "") {
      String fileName = proposalFileDir + "/" + techFile;
      if (fileName.indexOf(".txt") > 0 ||
	fileName.indexOf("technical") > 0 ) {
%>
<div class="printspanbar">
<span style="font-weight:bold;">Technical Review for <%=propnum%> </span>
<%
       if (techFile.indexOf(".pdf") > 0) {
         out.write("Technical Review PDF file available ");
         out.write("<a href=\"/reports/displayFile.jsp?fileName=");
         out.write(techFile);
         out.write("\">here</a>");
       } else {
%>
<pre>
<%
        
         try {
            FileReader fileR = new FileReader(fileName);
            BufferedReader currentFileBR = new BufferedReader(fileR);
            String inputLine = null;
            while( (inputLine = currentFileBR.readLine()) != null) {
                out.write(StringEscapeUtils.escapeHtml4(inputLine));
                out.write("\n");
            }
            currentFileBR.close();
            fileR.close();

         }
         catch (Exception exc) {
           out.write("Error occurred reading " + techFile);
         }
       }
       fcnt++;
%>
</pre>
</div>
<p>
<% 
      }  
    }
    String propFile = prop.getProposerInputFile();
    if (propFile != null && propFile != "") {
      String fileName = proposalFileDir + "/" + propFile;
      if (fileName.indexOf(".txt") > 0 ||
	fileName.indexOf("technical") > 0  ||
	fileName.indexOf("proposer_input") > 0  ) {
%>
<div class="printspanbar2">
<span style="font-weight:bold;">Proposer Input for <%=propnum%> </span>
<%
      if (propFile.indexOf(".pdf") > 0) {
         out.write("Proposer Input PDF file available ");
         out.write("<a href=\"/reports/displayFile.jsp?fileName=");
         out.write(propFile);
         out.write("\">here</a>");
        
      }  else {
%>
<pre>
<%
        try {
            FileReader fileR = new FileReader(fileName);
            BufferedReader currentFileBR = new BufferedReader(fileR);
            String inputLine = null;
            while( (inputLine = currentFileBR.readLine()) != null) {
                out.write(StringEscapeUtils.escapeHtml4(inputLine));
                out.write("\n");
            }
            currentFileBR.close();
            fileR.close();
        }
        catch (Exception exc) {
          out.write("Error occurred reading " + propFile);
        }
      }
      fcnt++;
%>
</pre>
</div>
<p>
<%
      }
    }
  } //end for loop
} //end null check
if (fcnt == 0) {
%>
No technical reviews or proposer input files found.
<% } %>


</body>
</html>
<% } catch (Exception e) {};
 } %>

