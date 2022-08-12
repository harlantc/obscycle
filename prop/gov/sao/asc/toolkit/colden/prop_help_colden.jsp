<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<head>
<title>Proposal Planning Toolkit: Colden Help</title>
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
  <div class="propheadercenter">Proposal Planning Toolkit: Colden Help
  </div>
</div>


<p>This is a help page specific to using Colden online. For more
  information, please consult the CIAO colden help page <a href="http://cxc.harvard.edu/ciao/ahelp/colden.html">here</a>.</p>

<p>
<h2><a name="Coordinate">Input Coordinates</a></h2>
The user may enter the input coordinates or resolve a <i>Target Name</i>
using the <i>Resolve Name</i> button.

<h2>Coordinate System</h2>

        Select the coordinate system for specifying the 
	column direction.  Colden supports the following choices: 
        <ul>
	  <li>	Equatorial (B1950) - Equatorial w/ Besselian epoch B1950
	  <li>	Equatorial (J2000) - Equatorial w/ Julian epoch J2000
	  <li>	Equatorial (Xxxxx) - Equatorial w/ specified Besselian epoch or
Julian epoch
	  <li>	Galactic  - Galactic
	  <li>	Ecliptic (B1950) - Ecliptic w/ Besselian epoch B1950
	  <li>	Ecliptic (Bxxxx) - Ecliptic w/ specified Besselian epoch	 
        </ul>


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

		Sexagesimal	00 00 00.00 to 24 00 00.00 <br>
		Decimal		00.00 to 360.00 <br>

	<li>EQUATORIAL - <a name="Dec"> <B>Dec</B> </a>
        - Declination, given in either sexagesimal or
	decimal format.  Sexagesimal values are specified as "sdd mm [ss[.ss]]".
	The range for Dec is:<br>

		Sexagesimal	-90 00 00.00 to 90 00 00.00 <br>
		Decimal		-90.00 to 90.00 <br>

	<li>GALACTIC - <a name="L2"> <B>L2</B> </a>
        - Longitude, given in either sexagesimal or decimal 
	format.  Sexagesimal values are specified as "hh mm [ss[.ss]]".  The 
	range for L2 is:<br>

		Sexagesimal	00 00 00.00 to 24 00 00.00 <br>
		Decimal		00.00 to 360.00 <br>

	<li>GALACTIC - <a name="B2"> <B>B2</B> </a>
        - Latitude, given in either sexagesimal or decimal 
	format.  Sexagesimal values are specified as "sdd mm [ss[.ss]]".  The 
	range for B2 is:<br>

		Sexagesimal	-90 00 00.00 to 90 00 00.00 <br>
		Decimal		-90.00 to 90.00 <br>

	<li>ECLIPTIC - <a name="EL"> <B>EL</B> </a>
        - Ecliptic Longitude, given in either sexagesimal or
	decimal format.  Sexagesimal values are specified as "hh mm [ss[.ss]]". 
        The range for EL is:<br>

		Sexagesimal	00 00 00.00 to 24 00 00.00 <br>
		Decimal		00.00 to 360.00 <br>

	<li>ECLIPTIC - <a name="EB"> <B>EB</B> </a>
        - Ecliptic Latitude, given in either sexagesimal or
	decimal format.  Sexagesimal values are specified as "sdd mm [ss[.ss]]".
	The range for EB is:<br>

		Sexagesimal	-90 00 00.00 to 90 00 00.00 <br>
		Decimal		-90.00 to 90.00 <br>
      </ul>

<h2><a name="Resolver"> Target Name</a></h2>

        Enter a valid target name and click the <i>Resolve Name</i> button.
        This will query the catalog[s] specified in the
        <i>Name Resolver</i> field.
        If one, and only one entry is found, the coordinates will be 
        displayed in J2000.  If no entry is found or more than one entry
        is retrieved, the user will be asked to modify the <i>Target Name</i>
        and try again.

<h2><a name="NameResolver">Name Resolver</a></h2>

        Name of the service or services that will be used to resolve
        the target name when the <i>Resolve Name</i> button is pressed. The
        services will be chosen in the specified order. For example,
        "NED/SIMBAD" will first search for the target name in NED and
        then, if the name was not found, in SIMBAD. 

<h2><a name="Dataset"> Dataset</a></h2>

        Select the neutral hydrogen data set.  Colden supports the
        following choices:
        <ul>
           <li> <a name="NRAO"> <B>NRAO</B> </a>
                       - compilation by Dickey & Lockman; 
                       combines Bell data
                       with other surveys for all sky coverage; not velocity-
                       resolved, so does not support restricted velocity range
                       (full range is -550 to 550 km/s)
<pre>
From the header of the NRAO data set:

ORIGIN  = 'Dickey and Lockman, 1990, Annu. Rev. Astron. Astrophys., 28, 215'
HISTORY --------------------------------------------------------------------
HISTORY The data is from the CD-ROM "Images from the Radio Universe"
HISTORY produced by NRAO in 1992.
HISTORY The following surveys were merged and averaged over 1 X 1 degrees bins
HISTORY Burton, W. B., te Lintel Hekkert, P. 1986, Astron. Astrophys. Suppl. 65:
HISTORY 427-63
HISTORY Cleary, M. N., Heiles, C., Haslam, C. G. T. 1979, Astron. Astrophys.
HISTORY Suppl. 36: 95-127
HISTORY Jahoda, K., Lockman, F. J., McCammon, D. 1990, Ap. J. 354: In Press
HISTORY Kerr, F. J., Bowers, P. F., Jackson, P. D., Kerr, M. 1986, Astron.
HISTORY Astrophys. Suppl. 66:373-504
HISTORY Stark, A. A., Bally, J., Linke, R. A., Heiles, C. 1990:In Press
HISTORY Weaver, R. Williams, D. R. W. 1973, Astron. Astrophys. Suppl. 8:1-503
HISTORY --------------------------------------------------------------------
</pre>
           <li> <a name="Bell"> <B>Bell</B> </a>
                       - Bell Labs H1 Survey (Tony Stark et al.); northern sky 
                       (to 40 degrees south) only; velocity-resolved spectra
<pre>
The Bell dataset is a privately distributed (by Tony Stark) dataset made
available to the Einstein team in 1984; it is an earlier reduction of the 
data published in Stark et al 1992 ApJS 79. 77
</pre>
        </ul>


<h2><a name="Velocity Range">Velocity Range</a></h2>

	The Bell data set supports integrating over the full range of 
	velocities (-550. to 550. km/s) or over a restricted range.
        The Bell data have a velocity resolution of about 5 km/s.
        Specify the range of velocities.  


<h2>Results</h2>

<h2><a name="Galactic L2"> Galactic L2</a></h2>

        Longitude in Galactic coordinate system, in decimal degrees
        (ddd.dddd).

<h2><a name="B2"> B2</a></h2>

        Latitude in Galactic coordinate system, in decimal degrees
        (sdd.dddd)


<h2><a name="NH">NH</a></h2>

	The neutral hydrogen column density, in units of 1e+20 per cm**2.


<h2><a name="Comments">Comments</a></h2>

Auxiliary explanatory comments about the computation are provided.
      <ul>
        <li>(Interpolated) -            Value interpolated from four nearest
		    	                measurement directions

        <li>(At target) -               Measurement in exactly the specified 
					column direction

        <li>(At closest point) -	Measurement in observed direction 
					closest to the specified column 
					direction; the specified direction 
					does not have four nearest measurement
					directions suitable for interpolation

        <li>(Gain uncertain) -   	Specified column direction is less 
					than 0.2 deg from the equator (B1950);
					measurements near the equator in the 
					Stark et al. ("Bell") survey (ApJ 
					Suppl. 79, p77, 1992) may suffer from 
					large gain fluctuations

        <li>(Too far south) -		No value computed -- Specified column 
					direction is at least 40.0 deg south 
					of the equator (B1950); the Stark et 
					al. ("Bell") survey (ApJ Suppl. 79, 
					p77, 1992) used a Northern Hemisphere 
					telescope
      </ul>

 
<p>
<%@ include file="footer.html" %>


</BODY>
</HTML>


