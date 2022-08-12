package info;

/* imports */
import java.util.List;
import java.util.ArrayList;
import java.lang.IllegalArgumentException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;   
import java.util.Calendar;

import ascds.Coordinate;
import info.RelativeTime;
import info.AbsoluteTime;

/**
 * Classes to store and manage MPCat records.
 *  - primary usage is Resource Cost Calculator Tool (RCCalc)
 * 
 *    Element                      Description
      cycle                        Cycle
      observer                     Observer
      propnum                      Proposal Number
      seqnum                       Sequence Number
      obsid                        Observation ID
      target                       Target Name
      obstype                      Observation Type
      longitude                    Longitude
      latitude                     Latitude
      frame                        Coordinate frame
      exptime                      Proposed exposure time (ks)
      instrument                   Science instrument
      req_chipcnt                  Requested chip count
      opt_chipcnt                  Optional chip count
      chips                        Chip selections
      continuous                   Observation is uninterrupted?
      split_interval               Number of days within which to split
      pnt_constraint               Apply pointing constraint?
      phase_constraint             Phase Constraint
      multi_telescope              Coordinate with other telescopes?
      multi_telescope_interval      - for how long? (in days)
      group_constraint             Group Constraint
      monitor_constraints          Monitor Constraint(s)
      roll_constraints             Roll Constraint(s)
      window_constraints           Window Constraint(s)
 *
 */
public class MPCatRecord
{
    private String   cycle;
    private String   observer;
    private Integer  propnum;
    private Integer  seqnum;
    private Integer  obsid;
    private String   target;
    private String   obstype;
    private String   longitude;
    private String   latitude;
    private String   frame;
    private Float    exptime;
    private String   instrument;
    private Integer  req_chipcnt;
    private Integer  opt_chipcnt;
    private String[] chips;
    private boolean  continuous;
    private Float    split_interval;
    private boolean  pnt_constraint;
    private boolean  multi_telescope;
    private Float    multi_telescope_interval;
    private PhaseConstraint phase_constraint;
    private GroupConstraint group_constraint;
    private List<MonitorConstraint> monitor_constraints;
    private List<RollConstraint> roll_constraints;
    private List<WindowConstraint> window_constraints;

    public MPCatRecord(){
        /* initialize to defaults */
        cycle = "24";        // TODO: This will change annually.. get from ENV?
        observer = "RCC";
        propnum = 99999999;
        seqnum = 999999;
        obsid = 99999;
        target = "";
        obstype = "GO";
        longitude = "";
        latitude = "";
        frame = "Equatorial (J2000)";
        exptime = 0.0f;
        instrument = "ACIS-S";
        req_chipcnt = 4;
        opt_chipcnt = 0;
        chips = new String[10];
        for ( int ii=0; ii<10; ii++){ chips[ii] = "N"; }
        continuous = true;
        split_interval = Float.NaN;
        pnt_constraint = false;
        phase_constraint = null;
        multi_telescope = false;
        multi_telescope_interval = 0.0f;
        group_constraint = null;
        monitor_constraints = new ArrayList<MonitorConstraint>();
        roll_constraints = new ArrayList<RollConstraint>();
        window_constraints = new ArrayList<WindowConstraint>();
    }

    /* Mutators: Simple set methods until value validation is incorporated.   */
    public void setChipCount( String requested, String optional ){
        this.setChipCount( Integer.valueOf(requested), Integer.valueOf(optional) );
    }
    public void setChipCount( Integer requested, Integer optional )
    {
        // RCC does not care about chip selection, so providing
        // this simple assignment for counts.
        this.req_chipcnt = requested;
        this.opt_chipcnt = optional;
    }
    public void setChipSelection( String[] selection )
    {
        // RCC does not care about chip selection
        //  - added this for test cases.
        Integer requested = 0;
        Integer optional = 0;

        for ( int ii=0; ii < 10; ii++ ){
            if ( selection[ii].compareTo("Y") == 0 ){
                this.chips[ii] = "Y";
                requested++;
            }else if ( selection[ii].startsWith("O") ){
                this.chips[ii] = selection[ii];
                requested++;
                optional++;
            }
            else{
                this.chips[ii] = "N";
            }
        }
        this.setChipCount( requested, optional );
    }
    public void setContinuous( String value ){
        this.setContinuous( string_to_bool( value ) );
    }
    public void setContinuous( boolean value ){
        this.continuous = value;
    }
    public void setCycle( String cycle ){
        this.setCycle( Integer.valueOf( cycle ) );
    }
    public void setCycle( Integer cycle ){
        if ( (cycle < 0) || (cycle > 99) ){
            throw new IllegalArgumentException("cycle must be in range 0:99");
        }
        this.cycle = cycle.toString();
    }
    public void setExposureTime( String exptime ){
        this.setExposureTime( Double.valueOf( exptime ) );
    }
    public void setExposureTime( Double exptime ){
        this.exptime = exptime.floatValue();
    }
    public void setGroupConstraint( String group_id,
                                    String pre_min_lead,
                                    String pre_max_lead ){
        this.group_constraint = new GroupConstraint( group_id, 
                                                     pre_min_lead,
                                                     pre_max_lead);
    }
    public void setGroupConstraint( String group_id,
                                    String duration,  // being deprecated
                                    String pre_min_lead,
                                    String pre_max_lead ){
        this.setGroupConstraint( group_id, Double.valueOf( duration ),
                                 pre_min_lead, pre_max_lead );
    }
    public void setGroupConstraint( String group_id,
                                    Double duration,  // being deprecated
                                    String pre_min_lead,
                                    String pre_max_lead ){
        this.group_constraint = new GroupConstraint( group_id, 
                                                     duration,
                                                     pre_min_lead,
                                                     pre_max_lead);
    }
    public void setInstrument( String si ){
        // TODO: Screen to allowed values
        // TODO: Enforce relation to chip count settings
        this.instrument = si;
    }
    public void setMultiTelescope( String interval ){
        this.setMultiTelescope( Double.valueOf(interval) );
    }
    public void setMultiTelescope( Double interval ){
        this.multi_telescope = true;
        this.multi_telescope_interval = interval.floatValue();
    }
    public void setObsID( Integer obsid ){
        this.obsid = obsid;
    }
    public void setObsType( String obstype ){
        // TODO: Screen allowed value set
        this.obstype = obstype;
    }
    public void setObserver( String observer ){
        this.observer = observer;
    }
    public void setPhaseConstraint( String epoch, String period,
                                    String start, String start_margin,
                                    String end, String end_margin,
                                    String unique){
        this.setPhaseConstraint( epoch, period, 
                                 Double.valueOf(start), Double.valueOf(start_margin),
                                 Double.valueOf(end), Double.valueOf(end_margin),
                                 string_to_bool(unique));
    }
    public void setPhaseConstraint( String epoch, String period,
                                    Double start, Double start_margin,
                                    Double end, Double end_margin,
                                    boolean unique){
        this.phase_constraint = new PhaseConstraint(epoch,
                                                    period,
                                                    start,
                                                    start_margin,
                                                    end,
                                                    end_margin,
                                                    unique);
    }
    public void setPointingConstraint( String value ){
        this.setPointingConstraint( string_to_bool( value ) );
    }
    public void setPointingConstraint( boolean value ){
        this.pnt_constraint = value;
    }
    public void setPosition( String longitude, String latitude, String frame ){
        // always present decimal format to resource cost calculator script.
        // if string form is given, handle decimal vs sexagesimal formats.
        try{
            this.setPosition( Double.valueOf(longitude), Double.valueOf(latitude), frame );
        }catch( NumberFormatException e1 ){
            // Failed to convert to Double.. try Sexagesimal
            try {
                Coordinate coord = new Coordinate( longitude, latitude, frame );
                this.setPosition( coord.getLon(), coord.getLat(), frame );
            }catch( NumberFormatException e2 ){
                // also failed..
                throw new IllegalArgumentException( "invalid format for longitude or latitude." );
            }
        }
        catch( Exception e3 ){
            throw new IllegalArgumentException( "unknown issue with arguments." );
        }

    }
    public void setPosition( Double longitude, Double latitude, String frame ){
        if ( frame.startsWith( "Equatorial" ) || ( frame.startsWith( "Ecliptic" ) ) ){
            this.frame = frame;
            this.longitude = String.format("%1$.6f", longitude );
            this.latitude  = String.format("%1$.6f", latitude  );
        }
        else{
            throw new RuntimeException( "Unsupported coordinate frame: " + frame );
        }
    }
    public void setProposalNumber( Integer propnum ){
        this.propnum = propnum;
    }
    public void setSequenceNumber( Integer seqnum ){
        this.seqnum = seqnum;
    }
    public void setSplitInterval( String interval ){
        this.setSplitInterval( Double.valueOf(interval) );
    }
    public void setSplitInterval( Double interval ){
        this.split_interval = interval.floatValue();
    }
    public void setTarget( String targetname ){
        this.target = targetname;
    }

    public void addMonitorConstraint( String pre_min_lead,
                                      String pre_max_lead,
                                      String prop_exptime){
        this.addMonitorConstraint( pre_min_lead,
                                   pre_max_lead,
                                   Double.valueOf( prop_exptime ));
    }
    public void addMonitorConstraint( String pre_min_lead,
                                      String pre_max_lead,
                                      Double prop_exptime ){
        MonitorConstraint constraint;
        constraint = new MonitorConstraint( pre_min_lead,
                                            pre_max_lead,
                                            prop_exptime );
        this.monitor_constraints.add( constraint );
    }
    public void addMonitorConstraint( String pre_min_lead,
                                      String pre_max_lead,
                                      String prop_exptime,
                                      String split_interval){
        this.addMonitorConstraint( pre_min_lead,
                                   pre_max_lead,
                                   Double.valueOf( prop_exptime ),
                                   Double.valueOf( split_interval ));
    }
    public void addMonitorConstraint( String pre_min_lead,
                                      String pre_max_lead,
                                      Double prop_exptime,
                                      Double split_interval
                                      ){
        MonitorConstraint constraint;
        constraint = new MonitorConstraint( pre_min_lead,
                                            pre_max_lead,
                                            prop_exptime,
                                            split_interval );
        this.monitor_constraints.add( constraint );
    }
    public void addRollConstraint( String use_flag,
                                   String is180ok,
                                   String angle,
                                   String tolerance ){
        this.addRollConstraint( string_to_bool( use_flag ),
                                string_to_bool( is180ok ),
                                Double.valueOf( angle ),
                                Double.valueOf( tolerance ));
    }
    public void addRollConstraint( boolean use_flag,
                                   boolean is180ok,
                                   Double angle,
                                   Double tolerance ){
        RollConstraint constraint;
        constraint = new RollConstraint( use_flag,
                                         is180ok,
                                         angle,
                                         tolerance);
        this.roll_constraints.add( constraint );
    }
    public void addWindowConstraint( String  use_flag,
                                     String  window_start,
                                     String  window_stop ){
        this.addWindowConstraint( string_to_bool( use_flag ),
                                  window_start,
                                  window_stop );
    }
    public void addWindowConstraint( boolean use_flag,
                                     String  window_start,
                                     String  window_stop ){
        WindowConstraint constraint;
        constraint = new WindowConstraint( use_flag,
                                           window_start,
                                           window_stop );
        this.window_constraints.add( constraint );
    }


    /**
     * Get String representation of records.
     *
     * @return String  Formatted string representation of contained records.
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        String line;

        sb.append("(\n");
        sb.append(":ao ").append(cycle).append("\n");
        sb.append(":observer ").append(observer).append("\n");
        sb.append(":prop-num ").append(propnum).append("\n");
        sb.append(":seq-nbr ").append(seqnum).append("\n");
        sb.append(":id ").append(obsid).append("\n");
        sb.append(":name ").append(target).append("\n");
        sb.append(":type ").append(obstype).append("\n");

        if ( frame.startsWith( "Equatorial" ) ){
            sb.append(":ra ").append(longitude).append("\n");
            sb.append(":dec ").append(latitude).append("\n");
        }else if ( frame.startsWith( "Ecliptic" ) ){
            sb.append(":elon ").append(longitude).append("\n");
            sb.append(":elat ").append(latitude).append("\n");
        }
        sb.append(":prop-exp-time ").append(String.format("%1$8.6f",exptime)).append("\n");
        sb.append(":si ").append(instrument).append("\n");

        sb.append(":requested-chip-count ").append(req_chipcnt).append("\n");
        sb.append(":optional-chip-count ").append(opt_chipcnt).append("\n");

        sb.append(":ccdi0 ").append(chips[0]).append("\n");
        sb.append(":ccdi1 ").append(chips[1]).append("\n");
        sb.append(":ccdi2 ").append(chips[2]).append("\n");
        sb.append(":ccdi3 ").append(chips[3]).append("\n");
        sb.append(":ccds0 ").append(chips[4]).append("\n");
        sb.append(":ccds1 ").append(chips[5]).append("\n");
        sb.append(":ccds2 ").append(chips[6]).append("\n");
        sb.append(":ccds3 ").append(chips[7]).append("\n");
        sb.append(":ccds4 ").append(chips[8]).append("\n");
        sb.append(":ccds5 ").append(chips[9]).append("\n");

        sb.append(":uninterrupt ").append(bool_to_string( continuous )).append("\n");
        sb.append(":pointing_constraint ").append(bool_to_string( pnt_constraint )).append("\n");

        if ( !roll_constraints.isEmpty() ){
            for ( int ii=0; ii < roll_constraints.size(); ii++ ){
                    sb.append( roll_constraints.get(ii).toString(ii+1) );
            }
        }

        if ( !window_constraints.isEmpty() ){
            for ( int ii=0; ii < window_constraints.size(); ii++ ){
                    sb.append( window_constraints.get(ii).toString(ii+1) );
            }
        }

        if ( phase_constraint == null ){
            sb.append(":phase-constr-flag N\n" );
        }
        else{
            sb.append(":phase-constr-flag Y\n" );
            sb.append( phase_constraint );
        }

        sb.append(":multi-tel-flag ").append(bool_to_string( multi_telescope )).append("\n");
        if ( multi_telescope ){
            sb.append(":multi-telescopes-interval ").append(String.format("%1$.6f", multi_telescope_interval)).append("\n");
        }

        if ( group_constraint != null){
            sb.append(":monitor Y\n" );
            sb.append( group_constraint );
        }
        else{
            /* no group constraint */
            sb.append(":monitor N\n" );

            /* check monitor constraint.. they are mutually exclusive. */
            if ( monitor_constraints.isEmpty() ){
                /* NOTE: this logic is intentional, examples do not 
                   include this when there is a GROUP constraint. */
                sb.append(":monitor N\n" );
            }
            else{
                sb.append(":monitor Y\n" );
                for ( int ii=0; ii < monitor_constraints.size(); ii++ ){
                    sb.append( monitor_constraints.get(ii).toString(ii+1) );
                }
            }
        }

        if (! split_interval.isNaN() ){
            sb.append(":split-interval ").append(split_interval).append("\n");
        }

        sb.append(")\n");
        
        return sb.toString();
    }

    private String bool_to_string( boolean value ){
        String result;
        if ( value ){ result = "Y"; }
        else{ result = "N"; }
        return result;
    }
    private boolean string_to_bool( String value ){
        if ( (value != null) && ((value.compareToIgnoreCase( "YES" ) == 0 ) ||
				 (value.compareToIgnoreCase( "TRUE" ) == 0 )) ){
            return true;
        }
        else{
            return false;
        }
    }

    private class PhaseConstraint{
        public String  epoch;        // YYYY:DDD:HH:MM:SS || MJD
        public String  period;       // RelativeTime
        public Float   start;        // 
        public Float   start_margin; // 
        public Float   end;          // 
        public Float   end_margin;   // 
        public boolean unique;       // 

        public PhaseConstraint( String epoch, 
                                String period,
                                Double start,
                                Double start_margin,
                                Double end,
                                Double end_margin,
                                boolean unique){
            try {
                Double mjd = Double.valueOf( epoch );
                AbsoluteTime at = AbsoluteTime.fromMJD( mjd );
                this.epoch = at.toString();
            } catch (Exception ex){
                this.epoch = epoch;
            }
            //this.epoch = epoch;
            RelativeTime rt = new RelativeTime( period );
            this.period = rt.toString();
            this.start  = start.floatValue();
            this.start_margin = start_margin.floatValue();
            this.end = end.floatValue();
            this.end_margin = end_margin.floatValue();
            this.unique = unique;
        }
        public String toString(){
            StringBuilder sb = new StringBuilder();

            sb.append(":phase-epoch ").append(this.epoch).append("\n");
            sb.append(":phase-period ").append(this.period).append("\n");
            sb.append(":phase-start ").append(String.format("%1$8.6f",this.start)).append("\n");
            sb.append(":phase-start-margin ").append(String.format("%1$8.6f",this.start_margin)).append("\n");
            sb.append(":phase-end ").append(String.format("%1$8.6f",this.end)).append("\n");
            sb.append(":phase-end-margin ").append(String.format("%1$8.6f",this.end_margin)).append("\n");
            sb.append(":phase-unique ").append(bool_to_string( this.unique )).append("\n");

            return sb.toString();
        }
    }
    private class GroupConstraint{
        private String group_id;
        private String pre_min_lead;
        private String pre_max_lead;
        private Float  duration = null;     // deprecated

        public GroupConstraint( String group_id, 
                                String min_lead, 
                                String max_lead ){
            RelativeTime rt; // ensure relative time fields are in proper format.

            //TODO: group_id should not evaluate to FALSE in perl ("0", or "")
            this.group_id = group_id;

            rt = new RelativeTime( min_lead );
            this.pre_min_lead = rt.toString();
            rt = new RelativeTime( max_lead );
            this.pre_max_lead = rt.toString();
        }
        public GroupConstraint( String group_id, 
                                Double duration,
                                String min_lead, 
                                String max_lead ){
            this( group_id, min_lead, max_lead );
            this.duration = duration.floatValue();
        }
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append(":pre-min-lead ").append(this.pre_min_lead).append("\n");
            sb.append(":pre-max-lead ").append(this.pre_max_lead).append("\n");
            if (this.duration != null ){
                sb.append(":group-duration ").append(String.format("%1$.6f",this.duration )).append("\n");
            }
            sb.append(":group-id ").append(this.group_id).append("\n");
            return sb.toString();
        }
    }            
    private class MonitorConstraint{
        private String  pre_min_lead;
        private String  pre_max_lead;
        private Float   prop_exptime;
        private Float   split_interval = Float.NaN;

        public MonitorConstraint( String  min_lead,
                                  String  max_lead,
                                  Double  exptime){
            RelativeTime rt;
            rt = new RelativeTime( min_lead );
            this.pre_min_lead = rt.toString();
            rt = new RelativeTime( max_lead );
            this.pre_max_lead = rt.toString();
            this.prop_exptime = exptime.floatValue();
        }
        public MonitorConstraint( String  min_lead,
                                  String  max_lead,
                                  Double  exptime,
                                  Double  split_interval){
            this(min_lead, max_lead, exptime);
            this.split_interval = split_interval.floatValue();
        }
        public String toString( int index ){
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(":pre-min-lead-%1$d ",index)).append(this.pre_min_lead).append("\n");
            sb.append(String.format(":pre-max-lead-%1$d ",index)).append(this.pre_max_lead).append("\n");
            sb.append(String.format(":prop-exp-time-%1$d ",index)).append(String.format("%1$8.6f",this.prop_exptime)).append("\n");
            if (! this.split_interval.isNaN()){
                sb.append(String.format(":split-interval-%1$d ",index)).append(this.split_interval).append("\n");
            }
            return sb.toString();
        }
    }            
    private class RollConstraint{
        private boolean roll_flag;
        private boolean roll_180_ok;
        private Float   roll_angle;
        private Float   roll_angle_tolerance;
        
        public RollConstraint( boolean flag, 
                               boolean is180ok,
                               Double angle,
                               Double tolerance ){
            this.roll_flag = flag;
            this.roll_180_ok = is180ok;
            this.roll_angle = angle.floatValue();
            this.roll_angle_tolerance = tolerance.floatValue();
        }
        public String toString(int index){
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(":roll-flag-%1$d ",index)).append(bool_to_string( this.roll_flag )).append("\n");
            sb.append(String.format(":roll-180-%1$d ",index)).append(bool_to_string( this.roll_180_ok )).append("\n");
            sb.append(String.format(":roll-%1$d ",index)).append(String.format("%1$8.6f",this.roll_angle )).append("\n");
            sb.append(String.format(":roll-tolerance-%1$d ",index)).append(String.format("%1$8.6f",this.roll_angle_tolerance )).append("\n");
            return sb.toString();
        }
    }

    private class WindowConstraint{
        private boolean use_flag;
        private String  window_start;
        private String  window_stop;
        
        public WindowConstraint( boolean use_flag, 
                                 String window_start,
                                 String window_stop){

            this.use_flag = use_flag;

            // store window dates in AT1 format
            AbsoluteTime tstart = new AbsoluteTime( window_start );
            AbsoluteTime tstop  = new AbsoluteTime( window_stop );
            this.window_start = tstart.toString( AbsoluteTime.ABSOLUTE_TIME_FORMAT_1 );
            this.window_stop  = tstop.toString( AbsoluteTime.ABSOLUTE_TIME_FORMAT_1 );
        }
        public String toString(int index){
            StringBuilder sb = new StringBuilder();
            sb.append(String.format(":window-constr-flag-%1$d ",index)).append(bool_to_string( this.use_flag )).append("\n");
            sb.append(String.format(":window-constr-start-%1$d ",index)).append(this.window_start).append("\n");
            sb.append(String.format(":window-constr-stop-%1$d ",index)).append(this.window_stop).append("\n");
            return sb.toString();
        }
    }
}
