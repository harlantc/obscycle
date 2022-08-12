package info;

import java.util.regex.Pattern;

import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Class to interpret and manipulate Absolute Time field values.
 *   o Calendar dates
 *   o MJD dates
 *
 */
public class AbsoluteTime
{
    private Calendar date;

    public static final String ABSOLUTE_TIME_FORMAT_1 = "yyyy:DDD:HH:mm:ss.SSS";
    public static final String ABSOLUTE_TIME_FORMAT_2 = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static final String MJD_ZERO = "1858-11-17T00:00:00.000";

    /**
     *  Construct a AbsoluteTime instance with the current date/time.
     */
    public AbsoluteTime(){
        date = Calendar.getInstance();
    }

    /**
     *  Construct a AbsoluteTime instance from the provided text representation.
     *  Supported Formats:
     *    o Year:DoY:Time - yyyy:DDD:HH:mm:ss[.SSS]
     *    o DateTime      - yyyy-MM-dd'T'HH:mm:ss[.SSS]
     *
     * @param source Absolute time text representation
     */
    public AbsoluteTime( String source ){
        date = null;

        // NOTE: 
        // the input string may pass the regular expression check 
        // and still fail the parsing, which is more precise.
        //
        // pre-process input string to accommodate partial values.
        String tstr = fixAbsTimeString( source );

        if ( Pattern.compile( ToolkitConstants.RE_ABSOLUTE_TIME1 ).matcher( tstr ).matches() ){
            // Absolute Time Format 1
            date = new_calendar( tstr, ABSOLUTE_TIME_FORMAT_1 );
        }
        else if ( Pattern.compile( ToolkitConstants.RE_ABSOLUTE_TIME2 ).matcher( tstr ).matches() ){
            // Absolute Time Format 2
            date = new_calendar( tstr, ABSOLUTE_TIME_FORMAT_2 );
        }

        if ( date == null ){
            throw( new IllegalArgumentException( "Input source string not in supported format." ) );
        }
    }

    /**
     *    Corrects the ATF value string which may/may not include the seconds portion
     *
     *    Even though the string passes the pattern check, if the User enters
     *    a time with decimal seconds having more or less than 3 chars, this 
     *    will not be interpreted correctly.
     *
     *    Additionally, the calendar widget, when the User enters zero seconds, 
     *    it is not included in the Parameter value:
     *        2022-01-01T01:00:00.000 is returned as 2022-01-01T01:00
     *
     *    Rather than making more strict expressions, we are opting to screen
     *    the value and make adjustments as needed.
     *
     * @param value Date string to correct
     * @return Date string with any needed corrections applied
     */
    public static String fixAbsTimeString( String value ){

        String TIME_A = "(.+)[:T](\\d+):(\\d+):(\\d+\\.\\d*)"; // decimal seconds
        String TIME_B = "(.+)[:T](\\d+):(\\d+):(\\d+)";        // no milliseconds
        String TIME_C = "(.+)[:T](\\d+):(\\d+)";               // no seconds

        if ( Pattern.compile(TIME_A).matcher( value ).matches() ) {
            // must have millisecond precision
            value = value + "000";                                   // at least enough
            value = value.replaceAll("(\\.\\d\\d\\d)\\d*$", "$1" );  // trim to 3
        }
        else if ( Pattern.compile(TIME_B).matcher( value ).matches() ) {
            value = value + ".000";
        }
        else if ( Pattern.compile(TIME_C).matcher( value ).matches() ) {
            value = value + ":00.000";
        }

        return value;
    }

    /**
     *  Creates AbsoluteTime from provided MJD date.
     *
     * @param mjd MJD Date to convert
     *
     * @return AbsoluteTime instance corresponding to the provided MJD day.
     */
    public static AbsoluteTime fromMJD( Double mjd ){
        // I chose this approach rather than a Constructor because it seemed 
        // incorrect to assume that any Double input was an MJD date.  
        // This forces the user to acknowledge what input is expected.

        SimpleDateFormat atformat = new SimpleDateFormat( ABSOLUTE_TIME_FORMAT_2 );
        atformat.setTimeZone( TimeZone.getTimeZone("UTC") );

        Calendar c = Calendar.getInstance( TimeZone.getTimeZone("UTC") );

        /* create date at MJD zero point */
        try {
            c.setTime( atformat.parse( MJD_ZERO ) );
        }
        catch ( ParseException ex ){
            // This should not happen, we are parsing an internally defined string
            return null;
        }

        /* shift by input MJD days */
        /* o number of days */
        c.add( Calendar.DAY_OF_YEAR, mjd.intValue() );

        /* o fraction of day in milliseconds */
        Double ms = (mjd - mjd.intValue())*86400.0*1000.0;
        c.add( Calendar.MILLISECOND, ms.intValue() );

        /* set result */
        AbsoluteTime result = new AbsoluteTime();
        result.date = c;

        return result;
    }

    /**
     * Obtains the value of the DAY_OF_MONTH component as an Integer.
     *
     * @return Day of Month component of this AbsoluteTime
     */
    public Integer getDayOfMonth(){
        return date.get( Calendar.DAY_OF_MONTH );
    }

    /**
     * Obtains the value of the DAY_OF_YEAR component as an Integer.
     *
     * @return Day of Year component of this AbsoluteTime
     */
    public Integer getDayOfYear(){
        return date.get( Calendar.DAY_OF_YEAR );
    }

    /** 
     * Determine difference in decimal days between 
     * this and the provided AbsoluteTime.
     *
     * @param other datetime to compare against
     * @return number of days between dates
     */
    public double getDeltaDays( AbsoluteTime other ){
        long delta_msec = date.getTime().getTime() - other.toDate().getTime();
        double result = (delta_msec/86400.0)/1000.0;
        return result;
    }

    /**
     * Obtains the value of the HOURs component as an Integer.
     *
     * @return Hours component of this AbsoluteTime
     */
    public Integer getHours(){
        return date.get( Calendar.HOUR_OF_DAY );
    }

    /**
     * Obtains the value of the MINUTEs component as an Integer.
     *
     * @return Minutes component of this AbsoluteTime
     */
    public Integer getMinutes(){
        return date.get( Calendar.MINUTE );
    }

    /**
     * Obtains the value of the MONTH component as an Integer.
     *
     * @return Month component of this AbsoluteTime
     */
    public Integer getMonth(){
        // Calendar stores month number starting at 0.
        //   o we want 1 - 12
        Integer month = date.get( Calendar.MONTH ) + 1;
        return month;
    }

    /**
     * Obtains the value of the SECOND component as an Double.
     *
     * @return Seconds component of this AbsoluteTime
     */
    public Double getSeconds(){
        Double nsecs;
        nsecs = Double.valueOf( date.get( Calendar.SECOND ) );
        nsecs += Double.valueOf( date.get( Calendar.MILLISECOND ) )/1000.0;
        
        return nsecs;
    }

    /**
     * Obtains the value of the YEAR component as an Integer.
     *
     * @return Year component of this AbsoluteTime
     */
    public Integer getYear(){
        return date.get( Calendar.YEAR );
    }

    /**
     * Checks if this instance is longer than the specified number of days.
     *
     * @param other datetime to compare against
     * @return true if this datetime is before the input datetime. false otherwise.
     */
    public boolean isBefore( AbsoluteTime other ){
        boolean result = date.getTime().before( other.toDate() );
        return result;
    }

    /**
     * Returns Date object representing this AbsoluteTime
     *
     * @return a Date representing this AbsoluteTime value.
     */
    public Date toDate(){
        Date result = date.getTime();
        return result;
    }

    /**
     *  Return a string representation of the AbsoluteTime
     *   o time given in UTC
     *
     *  format - default = ABSOLUTE_TIME_FORMAT_1
     *
     * @return formatted string representation of this AbsoluteTime.
     */
    public String toString(){
        String result;

        result = this.toString( ABSOLUTE_TIME_FORMAT_1 );
        return result;
    }

    /**
     *  Return a string representation of the AbsoluteTime
     *   o time given in UTC
     *
     * @param format SimpleDateFormat compatible format string
     * @return formatted string representation of this AbsoluteTime.
     *
     */
    public String toString( String format ){
        String result;

        SimpleDateFormat atformat = new SimpleDateFormat( format );
        atformat.setTimeZone( TimeZone.getTimeZone("UTC") );
        result = atformat.format( date.getTime() );

        return result;
    }

    /**
     *  create Calendar instance populated with the provided date.
     *
     *  NOTE: the string and format have already been paired.
     *
     * @param source Input date string
     * @param format Input date string format (SimpleDateTime compatibile)
     *
     * @return Calendar instance
     */
    private Calendar new_calendar( String source, String format ){
        Calendar c;

        // Setup formatter
        //  o non-lenient will throw exception for bad field content.
        //    rather than normalizing it into the other fields.
        SimpleDateFormat atf = new SimpleDateFormat( format );
        atf.setTimeZone( TimeZone.getTimeZone("UTC") );
        atf.setLenient( false );

        try{
            // Parse the date string
            Date d = atf.parse( source );

            // Create Calendar (UTC)
            c = Calendar.getInstance( TimeZone.getTimeZone("UTC") );

            // Set to date
            c.setTime( d );
        }
        catch ( ParseException ex ){
            throw( new IllegalArgumentException("invalid date") );
        }

        return c;
    }

}
