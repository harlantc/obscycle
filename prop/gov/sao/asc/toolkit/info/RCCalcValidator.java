package info;
/*
  Copyrights:
 
  Copyright (c) 1998-2019,2021 Smithsonian Astrophysical Observatory
 
  Permission to use, copy, modify, distribute, and  sell  this
  software  and  its  documentation  for any purpose is hereby
  granted without  fee,  provided  that  the  above  copyright
  notice  appear  in  all  copies and that both that copyright
  notice and this permission notice appear in supporting docu-
  mentation,  and  that  the  name  of the  Smithsonian Astro-
  physical Observatory not be used in advertising or publicity
  pertaining to distribution of the software without specific,
  written  prior  permission.   The Smithsonian  Astrophysical
  Observatory makes no representations about  the  suitability
  of  this  software for any purpose.  It is provided  "as is"
  without express or implied warranty.
  THE  SMITHSONIAN  INSTITUTION  AND  THE  SMITHSONIAN  ASTRO-
  PHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES  WITH REGARD TO
  THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANT-
  ABILITY AND FITNESS,  IN  NO  EVENT  SHALL  THE  SMITHSONIAN
  INSTITUTION AND/OR THE SMITHSONIAN ASTROPHYSICAL OBSERVATORY
  BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES
  OR ANY DAMAGES  WHATSOEVER  RESULTING FROM LOSS OF USE, DATA
  OR PROFITS,  WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
  OTHER TORTIOUS ACTION,  ARISING OUT OF OR IN CONNECTION WITH
  THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

import java.lang.Math;
import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import ascds.Coordinate;
import info.RelativeTime;
import info.AbsoluteTime;

/******************************************************************************/

/**
 * Provide operations to validate RCC tool fields.
 * These include:
 *   - basic format and range checking
 *   - verifying correlated fields
 */

public class RCCalcValidator extends ToolkitValidator
{
    private ArrayList<String> errStack;

  /**
   * Constructor
   *
   * @param request The browser generated request packet object.
   * @param toolkitProperties The set of toolkit properties.
   */
  
  public RCCalcValidator( HttpServletRequest request,
                          Properties toolkitProperties )
  {
      super( request, toolkitProperties );
      errStack = new ArrayList<String>();
  }

  /**
   * Clear error stack.
   */
  public void clearErrorStack(){
      errStack.clear();
  }

  /** 
   * Get messages from error stack.
   *
   * @return Issue messages stored in error stack.
   */
  public String[] getIssues(){
      return errStack.toArray( new String[0] );
  }

  /** 
   * Indicates if any validation errors have occured.
   *
   * @return True if errors have occured, otherwise False.
   */
  public boolean hasIssues(){
      return !errStack.isEmpty();
  }

  /**
   * Updates session state, and message attributes.
   */
  public void updateSessionState(){
      errorsOccurred( getIssues() );
  }

  /**
   * Validate content of the Position fields.
   *
   * @return False if fields are valid, otherwise True.
   */
  public boolean validatePosition()
  {
      String errmsg; 
      boolean anyprobs = false;

      // * frame
      //   TODO: check against enumerated list?

      // * Longitude
      errmsg = validateLongitudeParameter("inputPosition1",
                                          "position1LabelBGColor",
                                          "ra-l2-el.input");
      anyprobs |= updateErrorStack( errmsg );

      // * Latitude
      errmsg = validateLatitudeParameter("inputPosition2",
                                         "position2LabelBGColor",
                                         "dec-b2-eb.input");
      anyprobs |= updateErrorStack( errmsg );

      // Perform correlation checks:
      //  - lon/lat must not both be zero.
      if ( !anyprobs ){
          String lon = Parameter.get( this.getRequest(), "inputPosition1");
          String lat = Parameter.get( this.getRequest(), "inputPosition2");
          String frame = Parameter.get( this.getRequest(), "inputCoordinateSelector");
          try{
              Coordinate coord = new Coordinate(lon,lat,frame);
              Double dlon = coord.getLon();
              Double dlat = coord.getLat();
              if ( ( dlon == 0.0 ) && ( dlat == 0.0 ) ){
                  errmsg = "Position: Longitude and Latitude must not both be zero.\n";
                  anyprobs |= updateErrorStack( errmsg );
              }
          }catch ( Exception ex ){
              anyprobs |= updateErrorStack( ex.getMessage() );
          }
          if ( anyprobs ){
              // set field color to indicate error.
              setFieldColor("position1LabelBGColor", BG_COLOR_ERROR );
              setFieldColor("position2LabelBGColor", BG_COLOR_ERROR );
          }
      }

      return anyprobs;
  }

  /**
   * Validate content of the Exposure Time field.
   *
   * @return False if fields are valid, otherwise True.
   */
  public boolean validateExposureTime()
  {
      String errmsg; 
      boolean anyprobs = false;

      errmsg = validateRangeParameter( "propExposureTime", 
                                       "exposureTimeLabelBGColor",
                                       "exposure-time.limits",
                                       "exposure-time.input" );
      anyprobs |= updateErrorStack( errmsg );

      return anyprobs;
  }

  /**
   * Validate content of the Split Constraint field(s).
   *
   * @return False if fields are valid, otherwise True.
   */
  public boolean validateSplitConstraint()
  {
      String errmsg; 
      String uninterruptedFlag;
      String splitFlag;
      String tmpstr;
      Double splittime;
      Double propexptime;
      boolean anyprobs = false;
      boolean problem = false;

      // Correlation check:
      // - split constraint and uninterrupted must not both be "Yes"
      uninterruptedFlag = Parameter.get( this.getRequest(), "uninterrupted");
      splitFlag = Parameter.get( this.getRequest(), "splitConstraint");
      if ( uninterruptedFlag.equalsIgnoreCase("YES") && splitFlag.equalsIgnoreCase("YES") ){
          anyprobs = true;
          updateErrorStack("Split Constraint and Uninterrupted must not both be 'Yes'.\n");
          setFieldColor( "uninterruptLabelBGColor", BG_COLOR_ERROR );
          setFieldColor( "intervalLabelBGColor", BG_COLOR_ERROR );
      }

      // Validate SplitInterval field
      errmsg = validateRangeParameter( "splitInterval",
                                       "intervalLabelBGColor",
                                       "split-interval.limits",
                                       "split-interval.input" );
      problem |= updateErrorStack( errmsg );

      if ( !problem ){

          // Correlation check:
          // - split interval must be >= exposure time
          //   NOTE: exposure time in 'ks'.. split interval in 'days'
          tmpstr = Parameter.get( this.getRequest(), "propExposureTime");
          try{
              propexptime = Double.valueOf( tmpstr );
          } catch( Exception ex ){
              propexptime = Double.NaN;
          }
          // split interval value vetted above..
          tmpstr = Parameter.get( this.getRequest(), "splitInterval");
          splittime = convertDaysToKiloseconds( Double.valueOf( tmpstr ) );

          if ( splittime < propexptime ){
              problem = true;
              updateErrorStack("Split Interval: must be >= Exposure Time.\n");
              setFieldColor("exposureTimeLabelBGColor", BG_COLOR_ERROR );
              setFieldColor("intervalLabelBGColor", BG_COLOR_ERROR );
          }
      }
      anyprobs |= problem;

      return anyprobs;
  }

  /**
   * Validate content of the Coordination Window Constraint field(s).
   *
   * @return False if fields are valid, otherwise True.
   */
  public boolean validateCoordinationWindow()
  {
      String errmsg; 
      boolean anyprobs = false;

      errmsg = validateRangeParameter( "obsInterval",
                                       "coordinatedObsLabelBGColor",
                                       "coordination-window.limits",
                                       "coordination-window.input" );
      anyprobs |= updateErrorStack( errmsg );

      return anyprobs;
  }

  /**
   * Validate content of the Chip Selection field(s).
   *
   * @return False if fields are valid, otherwise True.
   */
  public boolean validateChipSelection()
  {
      String errmsg; 
      boolean anyprobs = false;

      String instrument = Parameter.get( this.getRequest(), "instrument");
      // TODO: validate instrument to enumeration set?

      if ( instrument.startsWith("ACIS") ){
          errmsg = validateRangeParameter( "requiredChipCount", 
                                           "requiredChipCountLabelBGColor",
                                           "acis-chip-count.limits",
                                           "req-chip-count.input" );
      }else{
          errmsg = validateRangeParameter( "requiredChipCount", 
                                           "requiredChipCountLabelBGColor",
                                           "hrc-chip-count.limits",
                                           "req-chip-count.input" );
      }

      anyprobs |= updateErrorStack( errmsg );

      return anyprobs;
  }

  /**
   * Validate content of the Phase Constraint field(s).
   *
   * @return False if fields are valid, otherwise True.
   */
  public boolean validatePhaseConstraint()
  {
      String errmsg; 
      boolean anyprobs = false;

      errmsg = validateAbsTimeParameter("phaseEpoch",
                                        "phaseEpochLabelBGColor",
                                        "phase-epoch.input" );
      anyprobs |= updateErrorStack( errmsg );

      errmsg = validateRelTimeParameter("phasePeriod",
                                        "phasePeriodLabelBGColor",
                                        "phase-period.input" );
      anyprobs |= updateErrorStack( errmsg );

      errmsg = validateRangeParameter( "phaseStart", 
                                       "phaseStartLabelBGColor",
                                       "phase-start.limits",
                                       "phase-start.input" );
      anyprobs |= updateErrorStack( errmsg );

      errmsg = validateRangeParameter( "phaseStartMargin",
                                       "phaseStartMarginLabelBGColor",
                                       "phase-start-margin.limits",
                                       "phase-start-margin.input" );
      anyprobs |= updateErrorStack( errmsg );

      errmsg = validateRangeParameter( "phaseStop",
                                       "phaseStopLabelBGColor",
                                       "phase-stop.limits",
                                       "phase-stop.input" );
      anyprobs |= updateErrorStack( errmsg );

      errmsg = validateRangeParameter( "phaseStopMargin",
                                       "phaseStopMarginLabelBGColor",
                                       "phase-stop-margin.limits",
                                       "phase-stop-margin.input" );
      anyprobs |= updateErrorStack( errmsg );

      // enforce phase epoch time is within 5 years of current date.
      if ( !anyprobs ){ 
          try{   
              String pe_string = Parameter.get( this.getRequest(), "phaseEpoch");

              // Phase Epoch may be MJD day or Date string.
              AbsoluteTime phase_epoch;
              try{ 
                  phase_epoch = AbsoluteTime.fromMJD( Double.valueOf( pe_string ) );
              } catch ( NumberFormatException ex ){
                  phase_epoch = new AbsoluteTime( pe_string );
              }

              AbsoluteTime now = new AbsoluteTime();
              
              double delta = Math.abs( now.getDeltaDays( phase_epoch ) );
              if ( delta > 365.0*5 ){
                  anyprobs = true;
                  errmsg = "Phase Epoch: Must be within 5 years of today's date.\n";
                  updateErrorStack( errmsg );
                  setFieldColor("phaseEpochLabelBGColor", BG_COLOR_ERROR );
              }
          }catch ( Exception ex ){
              errmsg = "Phase Constraint: "+ex.getMessage()+"\n";
              System.out.println( errmsg + ex );
              anyprobs |= updateErrorStack( errmsg );
          }
      }
      return anyprobs;
  }

  /**
   * Validate content of the Group Constraint field(s).
   *
   * @return False if fields are valid, otherwise True.
   */
  public boolean validateGroupConstraint()
  {
      String errmsg; 
      boolean anyprobs = false;

      errmsg = validateRelTimeParameter( "groupPreMaxLead",
                                         "groupPreMaxLeadLabelBGColor",
                                         "group-max-lead.input" );
      anyprobs |= updateErrorStack( errmsg );

      // Perform correlation checks, if field is OK:
      if ( !anyprobs ){
          String s2 = Parameter.get( this.getRequest(), "groupPreMaxLead");
          RelativeTime maxlead = new RelativeTime( s2 );
          errmsg = null;

          double lowLimit  =  getLowLimit( "group-max-lead.limits" );
          double highLimit = getHighLimit( "group-max-lead.limits" );
          if ( (maxlead.toDecimal() < lowLimit) || (maxlead.toDecimal() > highLimit) )  {
                  errmsg = getProcessedError( "must be within min/max intervals.",
                                              "groupPreMaxLeadLabelBGColor",
                                              "group-max-lead.input");
                  anyprobs |= updateErrorStack( errmsg );
          }
      }

      return anyprobs;
  }


  /**
   * Validate content of the Monitor Constraint field(s).
   *
   * @return False if fields are valid, otherwise True.
   */
  public boolean validateMonitorConstraint()
  {
      String errmsg; 
      boolean anyprobs = false;
      boolean problem = false;
      String s1;
      String s2;
      Double propexptime = Double.NaN;
      Double totexptime = 0.0;

      Integer nrows = (Integer)this.getRequest().getSession( true ).getAttribute( "monitorNumRows" );

      if ( nrows > 0 ){
          // need Proposal Exposure time for correlation validation
          s1 = Parameter.get( this.getRequest(), "propExposureTime");
          try{
              propexptime = Double.valueOf( s1 );
          } catch( Exception ex ){
              propexptime = Double.NaN;
          }
      }

      for (int ii = 0; ii < nrows; ii++) {

          // ********************************************************************************
          // Monitor Window Exposure Time
          problem = false;
          errmsg = validateRangeParameter( "monitorExpTime"+Integer.toString(ii),
                                           "monitorExposureTimeLabelBGColor",
                                           "monitor-exp-time.limits",
                                           "monitor-exp-time.input" );
          if (errmsg != null){ errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");}
          problem |= updateErrorStack( errmsg );

          if ( !problem && !propexptime.isNaN() ){

              // Correlation check:
              // - each row exposure time < proposal exposure time
              s1 = Parameter.get( this.getRequest(), "monitorExpTime"+Integer.toString(ii));
              Double monexptime = Double.valueOf( s1 ); // already vetted

              if ( monexptime > propexptime ){
                  errmsg = getProcessedError( "must be less than proposal exposure time.",
                                              "monitorExposureTimeLabelBGColor",
                                              "monitor-exp-time.input");
                  if (errmsg != null){ errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");}
                  problem |= updateErrorStack( errmsg );
              }

              // add up individual exposure times for second correlation check
              totexptime += monexptime;
          }
          anyprobs |= problem;

          // ********************************************************************************
          // Lead Interval
          RelativeTime minlead = null;
          RelativeTime maxlead = null;
          problem = false;

          errmsg = validateRelTimeParameter( "monitorPreMinLead"+Integer.toString(ii),
                                             "monitorPreMinLeadLabelBGColor",
                                             "monitor-min-lead.input" );
          if (errmsg != null){ errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");}
          problem |= updateErrorStack( errmsg );

          errmsg = validateRelTimeParameter( "monitorPreMaxLead"+Integer.toString(ii),
                                             "monitorPreMaxLeadLabelBGColor",
                                             "monitor-max-lead.input" );
          if (errmsg != null){ errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");}
          problem |= updateErrorStack( errmsg );

          // Perform correlation checks, if fields themselves are OK:
          if ( !problem ){
              s1 = Parameter.get( this.getRequest(), "monitorPreMinLead"+Integer.toString(ii));
              s2 = Parameter.get( this.getRequest(), "monitorPreMaxLead"+Integer.toString(ii));
              minlead = new RelativeTime( s1 );
              maxlead = new RelativeTime( s2 );
              errmsg = null;
              if ( ii == 0 ){
                  // Row 0, min/max interval must be "000:00:00:00.000"
                  if ( minlead.isLongerThan( 0.0 ) ){
                      errmsg = getProcessedError( "must be '000:00:00:00.000'",
                                                  "monitorPreMinLeadLabelBGColor",
                                                  "monitor-min-lead.input");
                      errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");
                      problem |= updateErrorStack( errmsg );
                  }
                  if ( maxlead.isLongerThan( 0.0 ) ){
                      errmsg = getProcessedError( "must be '000:00:00:00.000'",
                                                  "monitorPreMaxLeadLabelBGColor",
                                                  "monitor-max-lead.input");
                      errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");
                      problem |= updateErrorStack( errmsg );
                  }
              }
              else{
                  // maxlead must be > minlead
                  if ( minlead.isLongerThan( maxlead ) ){
                      errmsg = getProcessedError( "must be greater than Min.",
                                                  "monitorPreMaxLeadLabelBGColor",
                                                  "monitor-max-lead.input");
                      errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");
                      problem |= updateErrorStack( errmsg );
                  }
              }
          }
          anyprobs |= problem;

          // ********************************************************************************
          // Monitor Split Intervals
          problem = false;
          errmsg = validateRangeParameter( "monitorSplitInterval"+Integer.toString(ii),
                                           "monitorIntervalLabelBGColor",
                                           "split-interval.limits",
                                           "monitor-split-interval.input" );
          if (errmsg != null){ errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");}
          problem |= updateErrorStack( errmsg );

          // Correlation check: monitor split interval within min/max lead.
          if ( !problem && (minlead != null) && (maxlead != null) ){
              Double delta = ( (ii == 0 ) ? 364.0 : (maxlead.subtract(minlead).toDecimal()) );
              s1 = Parameter.get( this.getRequest(), "monitorSplitInterval"+Integer.toString(ii));
              Double interval = Double.valueOf( s1 ); // already vetted

              if ( interval < 1.0 || interval >= delta ){
                  errmsg = getProcessedError( "must be within min/max intervals.",
                                              "monitorIntervalLabelBGColor",
                                              "monitor-split-interval.input");
                  errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");
                  problem |= updateErrorStack( errmsg );
              }
          }
          anyprobs |= problem;
      }

      // Correlation check: Sum of Exposure times == proposed exposure time.
      if ( !propexptime.isNaN() ){
          if ( Math.abs(totexptime - propexptime) > 0.001 ){
              errmsg = getProcessedError( "total exposure times must equal proposal exposure time.",
                                          "monitorExposureTimeLabelBGColor",
                                          "monitor-exp-time.input");
              anyprobs |= updateErrorStack( errmsg );
          }
      }          

      return anyprobs;
  }

  /**
   * Validate content of the Window Constraint field(s).
   *
   * @return False if fields are valid, otherwise True.
   */
  public boolean validateWindowConstraint()
  {
      String errmsg; 
      boolean anyprobs = false;

      Integer nrows = (Integer)this.getRequest().getSession( true ).getAttribute( "windowNumRows" );

      for (int ii = 0; ii < nrows; ii++) {
          errmsg = validateAbsTimeParameter( "windowStartTime"+Integer.toString(ii),
                                             "windowStartLabelBGColor",
                                             "window-start.input" );
          if (errmsg != null){ errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");}
          anyprobs |= updateErrorStack( errmsg );

          errmsg = validateAbsTimeParameter( "windowStopTime"+Integer.toString(ii),
                                             "windowStopLabelBGColor",
                                             "window-stop.input" );
          if (errmsg != null){ errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");}
          anyprobs |= updateErrorStack( errmsg );
      }

      // Correlation checks:
      //  - Window stop must be after window start
      //  - Window times must be within 2 years of current cycle (ao:)
      if ( !anyprobs ){
          for ( int ii = 0; ii < nrows; ii++ ){
              try{

                  String start_time = Parameter.get( this.getRequest(), "windowStartTime"+Integer.toString(ii));
                  String stop_time  = Parameter.get( this.getRequest(), "windowStopTime"+Integer.toString(ii));

                  AbsoluteTime tstart = new AbsoluteTime( start_time );
                  AbsoluteTime tstop  = new AbsoluteTime( stop_time );

                  // enforce start date must be greater than current date
                  AbsoluteTime now = new AbsoluteTime();
                  if ( tstart.isBefore( now ) ){
                      anyprobs = true;
                      errmsg = "Window Constraint: Start time must be after today's date.\n";
                      errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");
                      updateErrorStack( errmsg );
                      setFieldColor("windowStartLabelBGColor", BG_COLOR_ERROR );
                  }

                  // enforce stop time to be after start time
                  if ( tstop.isBefore( tstart ) ){
                      anyprobs = true;
                      errmsg = "Window Constraint: Stop time must be after start time.\n";
                      errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");
                      updateErrorStack( errmsg );
                      setFieldColor("windowStartLabelBGColor", BG_COLOR_ERROR );
                      setFieldColor("windowStopLabelBGColor", BG_COLOR_ERROR );
                  }

                  // enforce window times within 2 years of cycle date.
                  AbsoluteTime tcycle = getCycleDate();
                  double delta = Math.abs( tcycle.getDeltaDays( tstart ) );
                  if ( delta > 365.0*2 ){
                      anyprobs = true;
                      errmsg = "Window Start Time: Must be within 2 years of cycle.\n";
                      errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");
                      updateErrorStack( errmsg );
                      setFieldColor("windowStartLabelBGColor", BG_COLOR_ERROR );
                  }
                  delta = Math.abs( tcycle.getDeltaDays( tstop ) );
                  if ( delta > 365.0*2 ){
                      anyprobs = true;
                      errmsg = "Window Stop Time: Must be within 2 years of cycle.\n";
                      errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");
                      updateErrorStack( errmsg );
                      setFieldColor("windowStopLabelBGColor", BG_COLOR_ERROR );
                  }

              }catch ( Exception ex ){
                  errmsg = "Window Constraint: "+ex.getMessage()+"\n";
                  errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");
                  anyprobs |= updateErrorStack( errmsg );
              }
          }
      }

      return anyprobs;
  }

  /**
   * Validate content of the Roll Constraint field(s).
   *
   * @return False if fields are valid, otherwise True.
   */
  public boolean validateRollConstraint()
  {
      String errmsg; 
      boolean anyprobs = false;

      Integer nrows = (Integer)this.getRequest().getSession( true ).getAttribute( "rollNumRows" );
      for (int ii = 0; ii < nrows; ii++) {
          errmsg = validateRangeParameter( "rollAngle"+Integer.toString(ii),
                                           "rollAngleLabelBGColor",
                                           "roll-angle.limits",
                                           "roll-angle.input" );
          if (errmsg != null){ errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");}
          anyprobs |= updateErrorStack( errmsg );
          
          errmsg = validateRangeParameter( "rollTolerance"+Integer.toString(ii),
                                           "rollToleranceLabelBGColor",
                                           "roll-angle.limits",
                                           "roll-tolerance.input" );
          if (errmsg != null){ errmsg = errmsg.replace(": ", " "+Integer.toString(ii+1)+": ");}
          anyprobs |= updateErrorStack( errmsg );
      }

      return anyprobs;
  }

  /**
   * Adds message to the error stack if not null/empty
   *   @param errmsg validation error message.
   *   @return True if stack updated, otherwise False.
   *
   */
    private boolean updateErrorStack( String errmsg ){
        boolean result = false;
        if ( errmsg != null && !errmsg.isEmpty() ){
            errStack.add( errmsg );
            result = true;
        }
        return result;
    }

    /**
     * Calculate the reference date for the current cycle
     *  - default cycle = "24" for testing
     *
     * @return current cycle start date
     */
    private AbsoluteTime getCycleDate() {
        // Cycle Reference Date = "2000:244:00:00:00.000"

        String cycle = this.getToolkitProperties().getProperty( "proposal-cycle" );
        String cycleDate = String.format("20%1$2d:244:00:00:00.000", Integer.parseInt(cycle) );
        AbsoluteTime result = new AbsoluteTime( cycleDate );

        return result;
    }
}
