<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<head>
<title>Proposal Planning Toolkit: Resource Cost Calculator Help</title>
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
  <div class="propheadercenter">Proposal Planning Toolkit: Resource Cost Calculator Help
  </div>
</div>


<p>This is a help page specific to using Resource Cost Calculator online.</p>

<p>
<h1>Inputs</h1>
<p>
<h2><a name="Coordinate">Input Coordinates</a></h2>
The user may enter the input coordinates or resolve a <i>Target Name</i>
using the <i>Resolve Name</i> button.

<h2>Coordinate System</h2>
        Select the coordinate system for specifying the 
        column direction.  The Resource Cost Calculator
        supports the following choices: 
        <ul>
          <li>  Equatorial (J2000) - Equatorial w/ Julian epoch J2000
          <li>  Ecliptic (B1950) - Ecliptic w/ Besselian epoch B1950
        </ul>

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

        <li>ECLIPTIC - <a name="EL"> <B>EL</B> </a>
        - Ecliptic Longitude, given in either sexagesimal or
        decimal format.  Sexagesimal values are specified as "hh mm
        [ss[.ss]]". 
        The range for EL is:<br>

                Sexagesimal     00 00 00.00 to 24 00 00.00 <br>
                Decimal         00.00 to 360.00 <br>

        <li>ECLIPTIC - <a name="EB"> <B>EB</B> </a>
        - Ecliptic Latitude, given in either sexagesimal or
        decimal format.  Sexagesimal values are specified as "sdd mm
        [ss[.ss]]".
        The range for EB is:<br>

                Sexagesimal     -90 00 00.00 to 90 00 00.00 <br>
                Decimal         -90.00 to 90.00 <br>

      </ul>

<h2><a name="Resolver"> Target Name</a></h2>
        Enter a valid target name and click the <i>Resolve Name</i>
        button.  This will query the catalog[s] specified in the
        <i>Name Resolver</i> field.
        <br><br>
        If one, and only one entry is found, the coordinates will be 
        displayed in J2000.  If no entry is found or more than one
        entry is retrieved, the user will be asked to modify
        the <i>Target Name</i> and try again.

<h2><a name="NameResolver">Name Resolver</a></h2>
        Name of the service or services that will be used to resolve
        the target name when the <i>Resolve Name</i> button is
        pressed. The services will be chosen in the specified
        order. For example, "NED/SIMBAD" will first search for the
        target name in NED and then, if the name was not found, in
        SIMBAD.

<h2><a name="ExposureTime"></a>Proposed Exposure Time</h2>
        The total exposure time in kiloseconds for requested
        observation of this target.

<h2><a name="ScienceInstrument"></a>Science Instrument</h2>
        Specifies which detector will be on the optical axis during the
        observation. The choices are: ACIS-I, ACIS-S, HRC-I, or HRC-S.

<h2><a name="RequiredChipCount"></a>Required Chip Count</h2>
        Required Chip Count for ACIS Instruments. Valid input range is
        1 to 4.
        <br><br>
        If an HRC Instrument is selected, the Chip Count is defaulted
        to zero.

<h2><a name="Constraints"></a>Constraints</h2>
        The addition of a constraint (or multiple constraints) will increase
        the final resource cost. When stepping through each day of the cycle
        and determining the visibility of a target, the constraint is taken
        into account. If the constraint makes the target not visible on a day
        (e.g., a window, roll, uninterrupt, or phase constraint) that target
        will get a score of 0 on that day. Otherwise, the visibility score for
        the target is divided by a number based on the difficulty of the
        constraint. The more difficult the constraint, the larger the dividing
        number and the lower the score for a given day.

<h2><a name="UninterruptConstraint"></a>Uninterrupt Constraint</h2>
        Value indicating that the science can only be optimized if the
        observation is interrupted as little as possible.
        <br><br>
        Options are: Y (Yes, required), N (No). The default is 'N'.
	<br>
        'Y' indicates that the science goals can only be achieved if
        the observation is not interrupted.
        <br><br>
        Observations >180 ksec will have to be interrupted due to the
        satellite orbit. If the uninterrupted flag is checked, the
        observations are constrained such that the pieces will be
        observed as contiguously as possible, and certainly in
        adjacent spacecraft orbits. 
        <br><br>
        'Uninterrupted' and 'Split Interval' observations are mutually
        exclusive. 

<h2><a name="SplitConstraint"></a>Split Constraint</h2>
        Value indicating whether, if the observation is split into
        shorter segments during scheduling, all parts must be
        completed within a defined interval.
        <br><br>
        Options are: Y (Yes, required), N (No). The default is 'N'. 
        <br>
        <ul>
	<li> <a name="SplitInterval"></a>Split Interval <br>
	  Relative Time Interval between segments of the observation
	  if it is split during scheduling; all segments of the
	  observation are to occur within this interval, from the
	  beginning of the first to the end of the last.
	</ul>

        'Uninterrupted' and 'Split Interval' observations are mutually
        exclusive.  
	<br><br>
	'Grouping' and 'Split Interval' observations are mutually
	exclusive.

<h2><a name="CoordinatedObs"></a>Coordinated Observation</h2>
        Must this observation be coordinated with that of another
        observatory? Value indicating that the Chandra observations
        are to be coordinated with another observatory. 
        <br>
        <ul>
        <li> <a name="CoordinationWindow"></a>Coordination Window <br>
        Time Interval for coordinated observations. Valid range is 
        0.0 - 364.0 days. 
        </ul>

<h2><a name="PhaseConstraint"></a>Phase Constraint</h2>
        Phase Dependent Observation. Value indicating that the
        observation is to be performed only within a specified phase
        range for a periodically-varying target.
        <br><br>
        Options are: Y (Yes, required), N (No). The default is 'N'.
        <br>
        <ul>
        <li> <a name="PhaseEpoch"></a>Epoch (MJD) <br>
	     For Phase Dependent observations, the reference date (MJD)
	     corresponding to a phase of 0.0. Observations will be made at an
	     integral number of Periods from this date, plus offsets as needed to
	     locate the observations within the specified phase range. The
             reference date must be within 5 years of the current date.
	<li> <a name="PhasePeriod"></a>Period (days) <br>
             The period in days characterizing the target variability.
	<li> <a name="PhaseStart"></a>Minimum Phase <br>
             Minimum phase in the variable phenomenon to be
             observed. Values must be between 0 and 1.
	<li> <a name="PhaseStartMargin"></a>Minimum Phase Error <br>
	     Tolerance in the minimum phase. This parameter sets how
	     precisely the phase range will be covered. Values must be
	     between 0 and 0.5. 
	<li> <a name="PhaseStop"></a>Maximum Phase <br>
             Maximum phase in the variable phenomenon to be
             observed. Values must be between 0 and 1.
	<li> <a name="PhaseStopMargin"></a>Maximum Phase Error <br>
             Tolerance in the maximum phase. This parameter sets how
             precisely the phase range will be covered. Values must be
             between 0 and 0.5. 
	<li> <a name="PhaseUnique"></a>Unique Phase <br>
	     If a phase constrained observation is split and the
	     observations end up in different phase windows, do the
	     split observations need to sample unique parts of the
	     phase window, or can they overlap? <br> 
	     Options are: Y (Yes, sample unique parts of phase
	     window), N (No, they can overlap). 
	</ul>

<h2><a name="PointingConstraint"></a>Pointing Constraint</h2>
        Value indicating that the final pointing information/offsets
        need to be modified based upon the roll angle.
        <br><br>
        Options are: Y (Yes, required), N (No). The default is 'N'.

<h2><a name="GroupConstraint"></a>Group Constraint</h2>
        Logical value indicating whether the observation needs to be
        observed within a relative time range with other targets in
        this proposal. 
        <br><br>
        Options are: Y (Yes, required), N (No). The default is 'N'.
        <br>
        <ul>
        <li> <a name="GroupPreMaxLead"></a>Maximum Time Interval <br>
          Relative Time Interval for grouping observations; all
	  observations are to occur within this interval, from the
	  beginning of the first to the end of the last. Valid range
	  is 0.0 - 364.0 days.  
	</ul>

	'Monitoring' and 'Grouping' observations are mutually
	exclusive. 
	<br><br>
	'Grouping' and 'Split Interval' observations are mutually
	exclusive. 

<h2><a name="MonitorConstraint"></a>Monitor Constraint</h2>
        Logical value indicating whether the observation needs to be
        observed more than 1 time. 
        <br><br>
        Options are: Y (Yes, required), N (No). The default is 'N'. 
	<br>
        <ul>
	<li> <a name="MonitorExposure"></a> Exposure Time <br>
	     Exposure time in kiloseconds allocated for each
	     observation. The total of the exposure times must equal
	     the time specified in the Total Observing Time field.
        <li> <a name="MonitorPreMinLead"></a> Minimum Time Interval  <br>
	     Minimum interval from the end of the preceding
	     observation to the beginning of the current observation
	     in days. Valid range is 0.0 - 364.0 days.
	<li> <a name="MonitorPreMaxLead"></a> Maximum Time Interval <br>
             Maximum interval from the end of the preceding
             observation to the beginning of the current observation
             in days. Valid range is 0.0 - 364.0 days.
	<li> <a name="MonitorSplitInterval"></a>Split Interval <br>
	     Relative Time Interval between observations if they need
	     to be split; all observations are to occur within this
	     interval, from the beginning of the first to the end of
	     the last. <br>
	     For Monitor series observations, valid range is: Split
	     Interval < Maximum Time Interval - Minimum Time Interval
	</ul>

	'Monitoring' and 'Grouping' observations are mutually
	exclusive. 

<h2><a name="WindowConstraint"></a>Window Constraint</h2>
        Logical value indicating a window constraint exists for this observation.
        <br><br>
        Options are: Y (Yes, required), N (No). The default is 'N'.
        <br>
        <ul>
	<li> <a name="WindowStartTime"></a> Start Time <br>
	     The time in UT specifying the earliest observation start
	     time.
	<li> <a name="WindowStopTime"></a> Stop Time <br>
	     The time in UT specifying the latest observation end
	     time.
	</ul>

	<b>Field Input:</b>
	<ol>
	  <li> Browsers that support the Calendar Widget (e.g. Safari
	       v14.1+, Firefox v93+, Chrome v20+ )
	    <ul>
	      <li> The widget will pop-up to aid in selecting the
	      date-time in the format:
		<br>&nbsp;&nbsp;&nbsp;&nbsp;
		mm/dd/yyyy hh:mm:ss.sss [AM|PM]
		<br>Note: Depending on the specific browser being used, some time fields may
		need to be manually set.
	    </ul>
	  <li> Browsers that do not support the widget (e.g. Firefox
	       versions below v93)
	    <ul>
	      <li>Users should manually enter the date-time in the
		following format:
		<br>&nbsp;&nbsp;&nbsp;&nbsp;
		YYYY:DOY:HH:MM:SS.sss (DOY = Day Of Year)
		<br>Note:
		The <a href="https://cxc.harvard.edu/toolkit/dates.jsp">Dates</a>
		application in this toolkit can help with obtaining Year and
		DOY from a calendar date.
	    </ul>
	</ol>


<h2><a name="RollConstraint"></a>Roll Constraint</h2>
        Value indicating a roll constraint exists for this observation.
        <br><br>
        A roll constraint translates directly into a constraint on the
        day and time when an observation may be carried out. It should
        only be specified for cases in which a specific attitude is
        required to meet scientific objectives. 
	<br><br>
        Options are: Y (Yes, required), N (No). The default is 'N'. 

	<br>
        <ul>
	<li> <a name="RollAngleTolerance"></a>Roll Angle and Roll Tolerance <br>
	  The spacecraft roll angle and roll tolerance (defined as the
	  half-range and assumed symmetric) for the observation. Valid
	  range is 0.0 - 360. 
	<li> <a name="RollRotation"></a>Is 180 Rotation OK? <br>
          Logical value indicating whether a 180 degree rotation of
          the roll angle is acceptable. The default is 'N'. 
	</ul>


<p>
<h1>Results</h1>

<h2><a name="ResourceCost">Resource Cost</a></h2>
       The normalized resource cost to Chandra for any target.
       <br><br>
       The resource cost is an estimate of how difficult a given
       observation would be, based on (at the very least)
       the location of the target in the sky (its visibility) and the length
       of the exposure. Constraints are also taken into account and
       increase the resource cost of a target. The more difficult the
       constraint, the larger the final resource score will be.
       In cases where the proposed observation cannot be performed as
       specified (e.g., long, uninterrupted ACIS exposures) the
       resource cost calculator may return the value "Inf", indicating
       an infinite resource cost. In this case the user must consider
       whether they can reduce the constraints on the observation
       (e.g., by removing the uninterrupt constraint) while still
       meeting their science goals.
       <br><br>
       Per the CfP, the total RC budget for Cycle 24 is anticipated to be
       approximately 27,500.
       <br><br>
       For further details regarding the Resource Cost numerical
       value, please refer to the CfP at:
       <br>&nbsp;&nbsp;&nbsp;&nbsp;
           <a href="https://cxc.harvard.edu/proposer/CfP">
           https://cxc.harvard.edu/proposer/CfP </a>
       <br><br>
<p>
<%@ include file="footer.html" %>


</BODY>
</HTML>


