<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<head>
<title>Proposal Planning Toolkit: PIMMS Help</title>
<%@ include file="cxcds_meta.html" %>
<link rel="stylesheet" href="/soft/include/cxcds.css" type="text/css" media="screen">
<link rel="stylesheet" href="toolkit.css" type="text/css" media="screen">
</head>

<body class="body">
<div class="topDiv">
  <div id="cxcheader">
    <div class="propheaderleft">
       <a href="/index.html"> <img id="spacecraft" src="/soft/include/cxcheaderlogo.png" alt="Chandra X-Ray Observatory (CXC)"></a>
    </div>
    <div class="propheadercenter">Proposal Planning Toolkit: PIMMS Help
      <br><div style="font-size:medium">PIMMS: Portable, Interactive Multi-Mission Simulator</div>
    </div>
  </div>


<p>This is a help page specific to using PIMMS online. For more
  information, please consult the CIAO PIMMS help
  page <a href="http://cxc.harvard.edu/ciao/ahelp/pimms.html">here</a>.</p>

<p>
<h2><a name="Mission">Mission & Detector/Grating/Filter</a></h2>

	The missions supported through the Toolkit GUI are:
	<ul>
	  <li>ASCA
	  <li>HITOMI 
	  <li>CHANDRA - The effective area curves are provided for the
                current and all previous NRAs, and can be viewed through the
                <a href="/cgi-bin/prop_viewer/build_viewer.cgi?ea">
                Effective Area File Viewer</A>.<br>
		More information is provided on the
                <a href="http://cxc.harvard.edu/caldb/proposal_planning.html">
                Chandra Proposal Planning Calibration Files</A> page.
                <br> Note that the CHANDRA effective area files include
                the sum of source count rates in positive and negative orders
                <br> (i.e. the 1st order files contain orders +/-1 and the 
                letghi file contains orders +/-2 through +/-11).
	  <li>EINSTEIN
	  <li>EXOSAT
	  <li>GINGA
	  <li>NUSTAR
	  <li>ROSAT
	  <li>SAX
	  <li>SWIFT
	  <li>SUZAKU
	  <li>XMM
	  <li>XTE
	</ul>
	When you select a mission for input or output, then clicking on the 
	corresponding "Detector/Grating/Filter" button will display the 
	available options for that mission.

<a name="Density"> </a>
<h2><a name="Flux">Flux or Flux Density</a></h2>

        For normalizing the source spectrum, choose whether to 
	specify 
<ul>
<li>its flux over a specified energy range;
<li>its flux density at a specified energy; 
<li> the count rate it would produce in a selected instrument.
</ul>
For flux or flux density normalization, specify whether to take the value as
diminished by line-of-sight absorption or not:

	The FLUX choices are:
        <ul>
	  <li>Absorbed Flux (or Flux Density) - Use flux from the source spectrum at the 
				telescope aperture (before application of 
				instrumental effects, and including absorption 
				in the ISM).
          <li>Unabsorbed Flux (or Flux Density) - Use flux from the source spectrum at the 
				telescope aperture (before application of 
				instrumental effects, and as if there were no 
				absorption in the ISM).
        </ul>

<h2>Energy Range</h2>
        
        <ul>
	  <li><a name="Input Energy"><B>Input Energy</B></a> 
        - Specify the energy bounds of the source spectrum, in keV.  If 
	you are normalizing the source spectrum by specifying the count rate 
	it would produce in a selected instrument, you may elect to use the 
	default energy range of the instrument.
For flux density, specify the energy, in keV, at which the flux density is 
evaluated.
          <li><a name="Output Energy"><B>Output Energy</B></a> 
        - Specify the output energy range of interest, in keV.  You may 
	elect to use the default energy range of the detecting instrument.
For flux density, specify the energy, in keV, at which the flux density is 
evaluated.
        </ul>

<h2><a name="Model">Model</a></h2>

	Select the spectral shape to convolve with the effective area 
	curve of the instrument.  PIMMS supports the following one-parameter 
	models:
        <ul>
	  <li>	Power Law - The parameter is Photon Index, defined such that 
		AE**(-(photon index)) is the flux in photons / cm**2 / s .

	  <li>	Black Body - The parameter is energy (kT) in keV.

	  <li>	Thermal Bremsstrahlung - The parameter is energy (kT) in keV.  
		The model includes the Gaunt factor.

          <li>  Plasma - APEC/MEKAL/Raymond-Smith
<br>The parameters are temperature, abundance and NH.
                <p>PIMMS v4.0 is distributed with APEC and Raymond-Smith models at
59 temperatures (logT of 5.60 to 8.50 in an increment of 0.05) times 5
abundance (0.2 to 1.0 in an increment of 0.2), while MEKAL has a narrower
temperature range starting with logT of 6.0 (51 temperatures to logT=8.50).
For MEKAL and APEC, ``solar'' abundances are those due to Anders &amp; Grevesse, 
while the solar standard for the Raymond-Smith grid has become unclear due to
passage of time.  PIMMS will select the nearest temperature and abundance
that is supported by the grid in use.


                <p>The Raymond-Smith models provided through the GUI support up
                to a maximum energy of 8 keV.  Any inputs exceeding 8 keV 
                are reset within the code to 8 keV.
                [8 keV is used as the maximum energy for normalizing the 
                source spectrum (i.e., as the maximum energy
		contributing to the "input" flux or count rate), and also as 
		the maximum energy contributing to the "output" count rate.  
		The errors caused by these truncations are opposite in sense
		(the input truncation overestimates the source, the output 
		truncation underestimates the count rate that that source 
		would produce).  The errors are generally small, as the
		effective areas are diminishing rapidly at these high 
                energies.]
         </ul>

<h2><a name="NH">Galactic NH</a></h2>
        Neutral hydrogen column density. If the value is 30.0 or less, it is 
        interpreted as log10(NH). Otherwise the value is NH, in cm**(-2), 
        specified with Fortran-style exponent (e.g., 2.5e+21). The range 
        accepted is 0.0 through 6.3E25. NOTE: only photoelectric absorption is 
        taken into account. At levels above ~1e24, the material becomes thick 
        to Compton scattering and the results are not expected to be accurate.

<h2><a name="Redshift">Redshift</a></h2>
        Optionally, all components may be redshifted using a common z (in which 
        case, Redshifted NH values are interpreted as an intrinsic absorber, 
        with the same z) with an optional (always z=0) Galactic NH.

<h2><a name="Redshift NH">Redshifted NH</a></h2>
      The neutral hydrogen column density of an intrinsic absorber at 
      the redshift of the source.  

<h2><a name="Photon Index">Photon Index</a></h2>

	Parameter for Power Law models, defined such that 
	AE**(-(photon index)) is the flux in photons / cm**2 / s .  
        The photon index may have any value.  A negative value specifies a 
	power law that increases with increasing energy in photon space.

<h2><a name="kT">kT</a></h2>

	Parameter for the Black Body and Thermal Bremsstrahlung models, the 
	energy (kT) in keV.  The range is 0.01 keV through 107 keV .

<h2><a name="Abundance">Abundance</a></h2>
	Parameter for Raymond-Smith models, the abundance, either 0.2, 0.4,
        0.6, 0.8, or 1.0 solar (as defined by Allen).

<h2><a name="logT">log T | keV</a></h2>
Parameter for Raymond-Smith models, the base 10 logarithm of the temperature in 
degrees Kelvin, or the equivalent energy in keV.   Values of logT supported range 
from 5.60 through 8.50, in increments of 0.05.


<h2><a name="Count Rate">Count Rate</a></h2>

	Count rate in the instrument selected for normalizing 
	the source spectrum, in counts / s .  The range is 1.0e-7 through 
	1.0e+6.

<h2><a name="Absorbed Flux">Absorbed Flux</a></h2>

	Flux, after line-of-sight absorption, in 
	ergs / cm**2 / s, for normalizing the source spectrum.  The range is 
	1.0e-18 through 1.0e-6.

<h2><a name="Unabsorbed Flux">Unabsorbed Flux</a></h2>

	Flux, disregarding line-of-sight absorption, in 
	ergs / cm**2 / s, for normalizing the source spectrum.  The range is 
	1.0e-18 through 1.0e-6.  For unabsorbed flux, both intrinsic and 
        Galactic absorption will temporarily be set to 0. 

<h2><a name="Absorbed FluxDensity">Absorbed Flux Density</a></h2>

	Flux Density, after line-of-sight absorption, in 
	ergs / cm**2 / s / keV, for normalizing the source spectrum.  The range is 
	1.0e-18 through 1.0e-9.

<h2><a name="Unabsorbed FluxDensity">Unabsorbed Flux Density</a></h2>

	Flux Density, disregarding line-of-sight absorption, in 
	ergs / cm**2 / s / keV, for normalizing the source spectrum.  The range is 
	1.0e-18 through 1.0e-9.

<h2><a name="Source">Source</a></h2>

	When the mission selected for output is CHANDRA and background 
	calibration data are available for the selected detector/grating 
	combination, the Toolkit will provide information about 
	instrument-specific background count rates (not included in the 
	PIMMS prediction).  Specify whether the source is a point source 
	or an extended source. Extended source size specification affects
        only background determination.


<h2><a name="Size">Size</a></h2>

	For an extended source to be observed with CHANDRA, specify the 
        area of 
	the source in arcsec**2, to enable estimation of the background count 
	rate.  (The background estimate for a source smaller than 
        7.0 arcsec**2 
	is the same as for a point source.)


<h2><a name="Frame Time">Frame Time</a></h2>

        The frame time is the fundamental unit of exposure for ACIS.
        The option exists to either "Specify" the frame time, or to 
        have the routine "Calculate" the frame time.<br>

        When specifying frame time, the valid range is from 0.2 to 10.0 
        seconds, in 0.1 second increments for simplicity.  
        The default value is 3.2 seconds.
        Frame times greater than the default will INCREASE the probability
        of the occurrence of pileup.
        <br>
        When calculating frame time, it is necessary to enter the Number of
        Chips, and Subarray, described below.  The choice of instrument
        (ACIS-I or ACIS-S) is also used in the frame time calculation.
        <br>
        Please refer to the Specific Help for Chandra RPS (Target Form -> ACIS
        Parameters -> Parameters that affect PILEUP -> Subarray: Frame Time)
        and the Proposers' Observatory Guide (ACIS chapter) for more 
	information regarding the selection of frame time and how its value 
        affects pileup.
        <br>
    
<h2><a name="NbrChips">Number of Chips</a></h2>

        This item appears when Frame Time is set to "Calculate".
        It is the number of ACIS chips set.  The choices are 1-6.  
        The default is 6.

<h2><a name="Subarray">Subarray</a></h2>
 
        This item appears when Frame Time is set to "Calculate".
        A subarray is a reduced region of the CCDs (all of the CCDs that are
        turned on) that will be read.  A reduced region may also help to
        reduce the effects of pulse pile-up.  
        The choices are None, 1/2, 1/4, 1/8.  The default is None.

<h2><a name="PIMMS Prediction">PIMMS Prediction</a></h2>

	The predicted count rate (in the absence of pileup for ACIS), in 
	counts / s.  This is 100% of the count rate for the full field of view.
        <br>When viewing the full results through the "View Output" button, the
        Model Normalization is provided.  If using the Power Law model, 
        the units of Model Normalization are photons/cm^2/s/keV at 1 keV.

<h2><a name="% Pileup">% Pileup</a></h2>

	The estimated pileup percentage, defined as the ratio of the number of
	frames with two or more events to the number of frames with
	one or more events times 100, for the predicted count rate 
        (assuming a point source) with ACIS.  The ACIS spectrum will be 
        "piled up" by this amount.  Pileup predictions are currently not
        available for extended sources.
        <br>
	Pileup is calculated using nine 3x3-pixel detect cells.  The
	inner cell is assumed to have an encircled energy fraction of
	0.886; the remaining energy is distributed equally to the
	surrounding eight cells.  The number of counts per frame is
	determined by multiplying the frame time by the count rate,
	and expected count rates are calculated for each cell given
	its encircled energy fraction.  
	<p>
	The cts/frame output is the summation of the individual detect
	cell values.
	<p>
        Pileup warnings:
        <ol> 
        <li>The pileup fraction assumes the user is calculating the flux
        over the *entire* bandpass of the ACIS-I or ACIS-S instrument.  
        If the energy
        range is restricted, the pileup fraction calculated will be
        *under*-estimated because fewer photons will be included in the
        pileup calculation than the full bandpass.  The severity of
	the effects of pileup 
        depends upon the spectral energy distribution (SED) of a source;
	however, the PIMMS calculation does not take the SED into account.     
        <p>
        <li>The current pileup model begins to break down and becomes 
        increasingly invalid as a predictor for pileup fractions greater 
        than about 75%.  75% pileup is reached approximately when the PIMMS 
        output is equivalent to about 3 counts/frame or higher.  Because the
        model does break down above 75%, the "cts/frame" and "cts/s" results
        may appear anomalous when the pileup percentage is this high.
	Please refer to the Proposers' Observatory Guide for information on 
        mitigating the pileup effect.
        <p>
	<li>For grating observations,
        the pileup estimate is an upper limit based on the undispersed 
        1st order count rate. Improved pileup estimates may be obtained by 
        reducing the energy range being considered; however, users who need 
        accurate simulations in order to ensure pileup will not be a problem 
        are advised to use the MARX simulation tool.
        </ol>

<h2><a name="cts/frame after Pileup">Piled counts per frame</a></h2>
The estimated number of observed counts per frame assuming that ACIS pileup occurs at the estimated percentage  (see <a href="#% Pileup">% Pileup</a> for a detailed explanation of the PIMMS pileup model) .

<h2><a name="cts/second after Pileup">Piled count rate</a></h2>
The estimated observed count rate (number of counts per second)  assuming that ACIS pileup occurs at the estimated percentage. This quantity is the "Piled counts per frame" divided by the input "frame time" 
The estimated observed count rate (number of counts per second)  assuming that ACIS pileup occurs at the estimated percentage. This quantity is the "Piled counts per frame" divided by the input "frame time"
	(see <a href="#% Pileup">% Pileup</a> for a detailed explanation
	of the PIMMS pileup model).

<h2><a name="Background Count Rate">Background Count Rate</a></h2>

	If background calibration data are available for the selected 
	CHANDRA output instrument, the Toolkit will present either an 
	estimate of the total background in the source area, in counts / s, 
	over the default energy range for the instrument (regardless of the 
	energy range selected for the source count rate); or information to 
	enable the user to make a qualitative estimate of the background.  
	The computations assume a 1.5" radius point source circle, which 
	encloses 90% of the total energy (as quoted in the PIMMS Prediction 
	(see above)) at 6.4 keV.  For extended sources, the count rate is 
	normalized to the input area. [A background count rate is provided 
	for all non-grating cases i.e. */None/None, and background 
	information is provided for the HRC-S/LETG/* cases.  For background 
	information on grating observations, the user is advised to consult 
	the appropriate chapter in the Proposers' Observatory Guide.]

<p>
<%@ include file="footer.html" %>

</body>
</html>

