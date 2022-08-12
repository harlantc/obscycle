<%@ page session="true" import="java.util.*, info.User, info.ReviewReport" %>
<%@ page import="info.ReportsConstants" %>


<%@ include file="reportsHead.html" %>
<body>	


<% 
response.setHeader("Cache-Control","no-store"); //HTTP 1.1 


String bppTar = (String)session.getAttribute("bppTar");
session.invalidate();

%> 
<p>Error: Pundits cannot access the site now.  Please try again later.
<p>
<% if (bppTar != null) { %>
<p>Download the <a href="<%=bppTar%>">LP/VLP</a> file with science justifications, RPS forms, and lists. 
<% } %>



</body>
</html>


