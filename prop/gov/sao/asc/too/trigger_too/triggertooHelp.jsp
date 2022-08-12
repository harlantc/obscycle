<%@ page session="true"  %>
<%@ include file = "triggertooHead.html" %>
<body>
<div class="topDiv">
  <div id="cxcheader">
    <div class="propheaderleft">
       <a href="/index.html"> <img id="spacecraft" src="/soft/include/cxcheaderlogo.png" alt="Chandra X-Ray Observatory (CXC)"></a>
    </div>
    <div class="propheadercenter">Chandra Trigger TOO
    </div>
  </div>
<div  style="margin:10px;">
<h2><a name="searchCriteria">** TOO Trigger Search Criteria</a></h2>
Only 1 field may be entered.  
Only TOOs that have not been observed will be retrieved. If the
TOO has linked observations, only the first target may be triggered.

<ul>
<li><a class="helplbl" name="obsid">Observation ID</a>
<br> Observation identification number 
<p>
<li><a class="helplbl" name="seqnbr">Sequence Number </a>
<br>Six-digit sequence number
<p>
<li><a class="helplbl" name="propnum">Proposal Number</a>
<br>
8-digit number identifying the proposal for an observation
<p>
Data Entry Information: Any Proposal Number that contains the entered value will be returned.
For example, entering 520 could match:
<pre>
  04800520
  05200866
  15200174
</pre>
<p>

<li><a class="helplbl" name="pi">P.I. Last Name</a>
<br>
Last name of the Principal Investigator
<p>
Data Entry Information: The matching is case insensitive. Any PI Name that contains the entered value will be returned.
For example, entering smith could match:
<pre>
    SMITH
    SMITHERS
    BLACKSMITH
</pre>
</ul> 

<hr>
<h2><a name="triggerList">** TOO Trigger Observation List</a></h2>
Only TOOs that have not been observed will be retrieved. If the
TOO has linked observations, only the first target may be triggered.


<h2><a name="triggerObservation">** TOO Trigger Observation </a></h2>
<p>
<ul>
<li><a class="helplbl" name="contactInfo">24 Hour Contact Information</a>
<br>
Please specify any additional 24-hour contact information. The contact 
   information should not be only the observer's daytime office
   number, but provide home phones, cell phones, and any additional 
   information such as which contact information to use for what 
   days/times.
<p>
<li><a class="helplbl" name="triggerCriteria">Trigger Criteria </a>
<br>
Although the Chandra Peer Review approved the scientific
objectives and observing plan of the proposal, the TOO itself must
also be justified. Please state why this particular TOO should be
observed, i.e., why it is a good example of the phenomena addressed in
the proposal. 
<i>Required</i>.

<p>
<li><a class="helplbl" name="responseWindow">Response Window / Urgency</a>
<br>
If necessary, please enter any changes to the approved response window.
You must justify any changes that you request. Please note that
<font class="error">faster response times may not be approved</font>. 
please make sure that this includes all information required to actually plan 
and schedule the entire observation, as you may not have an opportunity
for additional feedback or information before the observation occurs.

<p>
<li><a class="helplbl" name="targetName">Target Name</a>
<br>
Please specify the Target Name for this TOO.  You may use the <i>Resolve Name</i> button to then fill in the coordinates from the NED or Simbad database.

<p>
<li><a class="helplbl" name="coordInfo">Coordinates</a>
<br>
Target coordinates may be specified in decimal degrees or in sexagesimal format (hours/minutes/seconds or degrees/minutes/seconds) using either colons or spaces as separators. If the user enters decimal values for RA and Dec, they are assumed to be in decimal degrees. To specify decimal hours, add "h" or "H" after the number.

Examples:
<table cellspacing="10">
<tr><th>RA</th><th> Dec</th><th> System Translation</th></tr>
<tr><td> 20h 51m 06s</td><td>+30d 41' 00"</td><td> J2000 20 51 06.00 +30 41 00.00</td></tr>
<tr><td>20 51 06 </td><td>+30 41 </td><td>J2000 20 51 06.00 +30 41 00.00</td></tr>
<tr><td>20:51:06.0 </td><td>+30:41:00.0</td><td> J2000 20 51 06.00 +30 41 00.00</td></tr>
<tr> <td>23.7654 </td><td>+0.6857</td><td> J2000 01 35 03.70 +00 41 08.52</td></tr>
<tr><td>23.7654h</td><td> +0.6857</td><td> J2000 23 45 55.44 +00 41 08.52</td></tr>
<tr><td>18 12.5</td><td> -12 8.5</td><td> J2000 18 12 30.00 -12 08 30.00</td></tr>
</table>


For equatorial coordinates, if the RA is in sexagesimal format it is assumed to be in hours/minutes/seconds. 

<p>
<li><a class="helplbl" name="obsChanges">Observation Parameters</a>
<br>
Please enter any changes to the previously approved observation parameters
here.  These changes will be sent to the Chandra Director's Office for
approval.  Not all changes may be approved.
</ul>

<p />
</div>
</div>
<div class="footerDiv">
<%@ include file="cxcfooterj.html" %>
</div>
</body>
</html>


