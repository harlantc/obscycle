<%@ page isErrorPage="true" %>
<html>
<head>
<title>Show Error Page</title>
</head>
<body>
<h1>Oops...</h1>
<p>Sorry, an error occurred.</p>
<pre>
<% String msg = exception.getMessage(); %>
<%= msg %>
</pre>
</body>
</html>

