<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<head>
<title>Proposal Planning Toolkit: Dates Help</title>
<%@ include file="cxcds_meta.html" %>
<link rel="stylesheet" href="/soft/include/cxcds.css" type="text/css" media="screen">
<link rel="stylesheet" href="toolkit.css" type="text/css" media="screen">
</head>
<body class="body">
<div class="topDiv">
<div id="cxcheader">
  <div class="propheaderleft">
       <a href="/index.html"> <img id="spacecraft" src="/soft/include/cxcheaderlogo.png"  alt="Chandra X-Ray Observatory (CXC)"></a>
  </div>
  <div class="propheadercenter">Proposal Planning Toolkit: Dates Help
  </div>
</div>

<p>This is a help page specific to using Dates online. For more
  information, please consult the CIAO Dates help page <a href="http://cxc.harvard.edu/ciao/ahelp/dates.html">here</a>.</p>
<p> 
<h2><a name="Convert"> Convert from Date System</a></h2>

  Select the date system to convert from.  
  The Dates GUI supports the following choices:
  <ul>
    <li> <a name="Calendar Date"> <B>Calendar Date</B> </a> 
         - Gregorian Calendar Date 
    <li> <a name="Julian Date"> <B>Julian Date</B> </a> 
         - Julian Day Number
    <li> <a name="Modified Julian Date"> <B>Modified Julian Date</B> </a> 
         - Julian Day Number minus 2400000.5
    <li> <a name="Day of Year"> <B>Day of Year</B> </a> 
         - Day of Year for Gregorian Calendar Date 
    <li> <a name="Chandra Time"> <B>Chandra Time</B> </a> 
         - Seconds since the start of the Chandra
           Clock on January 1, 1998
  </ul>


<h2><a name="Date">Date</a></h2>

  Input the date according to the following formats:
  <ul>
    <li>Calendar Date - YYYY Mon DD [HH:MM:SS.SS] where
   		        "Mon" is the first three letters of the month name.<br>
	The time of day is optional, and each successive field of the time of 
	day is optional; that is, minutes, seconds, and fractional seconds are 
	optional.

        <ul>
           <li>1997 Sep 01
           <li>1997 Jul 04 05:00:00.00
        </ul>
        <p>
    <li>Julian Date - XXXXXXX.XXXXXXXXXX<br>
        When entering a Julian Date or Modified Julian Date, you may specify 
        any number of decimal places, but the program truncates to 10 decimal 
        places.  Since this is a truncation and not a rounding, you may lose 
        precision at the tenth decimal place.  To get the maximum possible 
        precision, you should round the input Julian Date or Modified Julian 
        Date to 10 decimal places.
        <ul>
           <li>1234567.1234567890
        </ul>
        The minimum acceptable input is 1721425.5 which corresponds to
        0001 Jan 01 00:00:00.0
        <p>
    <li>Modified Julian Date -     XXXXX.XXXXXXXXXX<br>
        When entering a Julian Date or Modified Julian Date, you may specify 
        any number of decimal places, but the program truncates to 10 decimal 
        places.  Since this is a truncation and not a rounding, you may lose 
        precision at the tenth decimal place.  To get the maximum possible 
        precision, you should round the input Julian Date or Modified Julian 
        Date to 10 decimal places.
        <ul>
           <li>12345.1234567890
        </ul>
        The minimum acceptable input is -678575.0 which corresponds to
        0001 Jan 01 00:00:00.0
        <p>
    <li>Day of Year - YYYYDDD[.DDDD]<br>
        Day of Year for Gregorian Calendar Date
        <p>
    <li>Chandra Time - SSSSSSSS<br>
        Seconds since the start of the Chandra clock on January 1, 1998
   </ul>                               

<h2>Results</h2>

        Calendar Date, Julian Date, Modified Julian Date, Day of Year,
        and Chandra Time are displayed.


<h2>Notes</h2>
        All dates and times are in Coordinated Universal Time (UTC).  
        UTC is exactly:
        <ul>
            <li>Eastern Daylight Time + 4 hours, or 
            <li>Eastern Standard Time + 5 hours, or 
            <li>Pacific Daylight Time + 7 hours, or 
            <li>Pacific Standard Time + 8 hours, or 
            <li>Greenwich Mean Time, or 
            <li>British Summer Time - 1 hour, etc.
        </ul>
<p>
<%@ include file="footer.html" %>

</body>
</html>


