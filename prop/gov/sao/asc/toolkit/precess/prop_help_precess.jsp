<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<head>
<title>Proposal Planning Toolkit: Precess Help</title>
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
  <div class="propheadercenter">Proposal Planning Toolkit: Precess Help
  </div>
</div>


<p>This is a help page specific to using Precess online. For more
  information, please consult the CIAO Precess help
  page <a href="http://cxc.harvard.edu/ciao/ahelp/precess.html">here</a>.</p>

<p>
<h2><a name="Coordinate">Input Coordinates</a></h2>
The user may enter the input coordinates or resolve a <i>Target Name</i>
using the <i>Resolve Name</i> button.

<h2>Coordinate System</h2>

        Select the coordinate system to convert from/to. 
	Precess supports the following choices: 
        <ul>
	  <li>	Equatorial(B1950) - Equatorial w/ Besselian epoch B1950
	  <li>	Equatorial(J2000) - Equatorial w/ Julian epoch J2000
	  <li>	Equatorial(Xxxxx) - Equatorial w/ specified Besselian or Julian epoch
	  <li>	Galactic  - Galactic
	  <li>	Ecliptic(B1950) - Ecliptic w/ Besselian epoch B1950
	  <li>	Ecliptic(Bxxxx) - Ecliptic w/ specified Besselian epoch	 
	  <li>  (CONVERT "TO" ONLY) Constellation - Returns the constellation 
                located at the given input position
        </ul>

	Note about Ecliptic: Precess does not allow conversions from one 
        Ecliptic system to another Ecliptic system.  The GUI therefore will 
	allow Ecliptic to be in either the Input list or the Output list, 
	but not both at the same time.<br>


<h2><a name="Equinox">Equinox</a></h2>

        Specify the epoch.  This is enabled only if the selected 
	Coordinate System is Equatorial(Xxxxx) or Ecliptic(Bxxxx).


<h2>Coordinates</h2>

        Input a set of coordinates in the format of the
        selected coordinate system or resolve a target name which will
        set the RA/Dec coordinates in J2000.
        The supported coordinate ranges are listed below.<br>

      <ul>

	<li>EQUATORIAL - <a name="RA"> <B>RA</B> </a>
        - Right Ascension, given in either sexagesimal or
	decimal format.  Sexagesimal values are specified as "hh mm [ss[.ss]]" 
	(that is, hours and minutes are required, seconds and fractions of 
	seconds are optional). 
        The range for RA is:<br>

		Sexagesimal	00 00 00.00 to 24 00 00.00<br>
		Decimal		00.00 to 360.00<br>

	<li>EQUATORIAL - <a name="Dec"> <B>Dec</B> </a>
        - Declination, given in either sexagesimal or
	decimal format.  Sexagesimal values are specified as "sdd mm [ss[.ss]]".
	The range for Dec is:<br>

		Sexagesimal	-90 00 00.00 to 90 00 00.00<br>
		Decimal		-90.00 to 90.00<br>

	<li>GALACTIC - <a name="L2"> <B>L2</B> </a>
        - Longitude, given in either sexagesimal or decimal 
	format.  Sexagesimal values are specified as "hh mm [ss[.ss]]".  The 
	range for L2 is:<br>

		Sexagesimal	00 00 00.00 to 24 00 00.00<br>
		Decimal		00.00 to 360.00<br>

	<li>GALACTIC - <a name="B2"> <B>B2</B> </a>
        - Latitude, given in either sexagesimal or decimal 
	format.  Sexagesimal values are specified as "sdd mm [ss[.ss]]".  The 
	range for B2 is:<br>

		Sexagesimal	-90 00 00.00 to 90 00 00.00<br>
		Decimal		-90.00 to 90.00<br>

	<li>ECLIPTIC - <a name="EL"> <B>EL</B> </a>
        - Ecliptic Longitude, given in either sexagesimal or
	decimal format.  Sexagesimal values are specified as "hh mm [ss[.ss]]". 
        The range for EL is:<br>

		Sexagesimal	00 00 00.00 to 24 00 00.00<br>
		Decimal		00.00 to 360.00<br>

	<li>ECLIPTIC - <a name="EB"> <B>EB</B> </a>
        - Ecliptic Latitude, given in either sexagesimal or
	decimal format.  Sexagesimal values are specified as "sdd mm [ss[.ss]]".
	The range for EB is:<br>

		Sexagesimal	-90 00 00.00 to 90 00 00.00<br>
		Decimal		-90.00 to 90.00<br>
      </ul>

<h2><a name="Resolver"> Target Name</a></h2>

        Enter a valid target name and click the <i>Resolve Name</i> button.
        This will query the catalog[s] specified in the
        <i>Name Resolver</i> field.
        If one, and only one entry is found, the coordinates will be 
        displayed in J2000.  If no entry is found or more than one entry
        is retrieved, the user will be asked to modify the <i>Target Name</i>
        and try again.


<h2><a name="NameResolver"> Name Resolver</a></h2>

        Name of the service or services that will be used to resolve
        the target name when the <i>Resolve Name</i> button is pressed. The
        services will be chosen in the specified order. For example,
        "NED/SIMBAD" will first search for the target name in NED and
        then, if the name was not found, in SIMBAD. 


<h2><a name="Constellation">Constellation Output</a></h2>
	
        The output is the constellation in which the given input
position lies.  Positions are precessed to B1875.0 and compared with
the Delporte (1935) constellation definitions.

<h2>Output Format</h2>

The output is provided in two formats.<br>
      <ul>
        <li>Sexagesimal: hh mm ss.ss & sdd mm ss.ss 
	<li>Decimal: ddd.dddd & sdd.dddd 
      </ul>
<p>
<%@ include file="footer.html" %>

</body>
</html>


