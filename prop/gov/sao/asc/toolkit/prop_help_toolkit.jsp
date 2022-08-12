<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<head>
<title>Proposal Planning Toolkit: General Help</title>
<%@ include file="cxcds_meta.html" %>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="/soft/include/cxcds.css" type="text/css" media="screen">
<link rel="stylesheet" href="toolkit.css" type="text/css" media="screen">
</head>

<body class="body">
<div class="topDiv">
  <div id="cxcheader">
    <div class="propheaderleft">
       <a href="/index.html"> <img id="spacecraft" src="/soft/include/cxcheaderlogo.png" alt="Chandra X-Ray Observatory (CXC)"></a>
    </div>
    <div class="propheadercenter">Proposal Planning Toolkit: General Help </div>
  </div>


<p>
<h2>Input Parameter Definitions</h2>
Help on specific inputs can be obtained by clicking on any underlined
parameter in the GUI.  A separate help file is provided for each of
PIMMS, Precess, Colden, and Dates.  The help file includes the range
of acceptable input values for each parameter.
<ul>
<li><a href="prop_help_pimms.jsp">PIMMS Help</a>
<li><a href="prop_help_colden.jsp">Colden Help</a>
<li><a href="prop_help_precess.jsp">Precess Help</a>
<li><a href="prop_help_dates.jsp">Dates Help</a>
<li><a href="prop_help_rccalc.jsp">Resource Cost Calculator Help</a>
</ul>

<h2>Button Definitions</h2>
The functions common within the PIMMS, Precess, Colden, Dates, and
Resource Cost Calculator GUIs are:
  <ul>
    <li> Calculate - determine algorithm results based upon user's input.
                     The interface with the command line executable output is displayed
         at the bottom of the browser window.
    <li> Clear - remove user's previous inputs from display and restore
         baseline settings for "pick lists"          
    <li> Help - access this help file
  </ul>

<h2>GUI Behavior</h2>
The input boxes provided on the GUI display are updated dynamically
based upon the user's selections.  This design ensures that only input 
boxes relevant to the calculation are provided to the user.
As a convenience, the values in the output boxes are cleared whenever the 
GUI display needs to be redrawn for the inputs.  To avoid unnecessary 
redrawing of the GUI, the values in the output boxes are not cleared when
the user makes an input change which does not require a redraw.

<h2>Saving of Results</h2>
After clicking "Calculate",  the Web browser's 
Save As function (format = text) should be used to save the results to a 
file.  The GUI labels will appear first.  Following this is the executable
call statement which contains the inputs.  Last will be the results of
the call to the command line executable. 

<h2>Printing of Results</h2>
The Web browser's Print function should be used to print the current
inputs and results.  A screen capturing technique should be used if 
preservation of the GUI display (as opposed to the content) is required.

<p>
<%@ include file="footer.html" %>
</body>
</html>

