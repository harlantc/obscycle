package info;

import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.lang.Math;

/**
 * Class to interpret and manipulate Relative Time field values.
 *
 */
public class RelativeTime
{
    private Integer  days;
    private Integer  hours;
    private Integer  minutes;
    private Double   seconds;

    public RelativeTime(){
        days = 0;
        hours = 0;
        minutes = 0;
        seconds = 0.0;
    }

    /**
     *  Construct a RelativeTime instance from the provided string expression.
     *  Supported formats include
     *    o Relative Time format: DDD:HH:MM:SS[.SSS]
     *    o Decimal days: DDD.DDDD
     *
     * @param period Relative time expression
     * @throws NumberFormatException if period is not in a supported format.
     */
    public RelativeTime( String period ){
        Pattern pattern = Pattern.compile( ToolkitConstants.RE_RELATIVE_TIME );
        Matcher matcher = pattern.matcher( period );

        if ( matcher.matches() ){
            days  = Integer.valueOf( matcher.group(1) );
            hours = Integer.valueOf( matcher.group(2) );
            minutes = Integer.valueOf( matcher.group(3) );
            seconds = Double.valueOf( matcher.group(4) );
        }
        else{
            // try decimal format 
            pattern = Pattern.compile( ToolkitConstants.RE_NONNEGATIVE_DECIMAL);
            matcher = pattern.matcher( period );
            if ( matcher.matches() ){
                Double ndays = Double.valueOf( period );
                init( ndays );
            }
            else{
                throw new NumberFormatException("Invalid format.");
            }
        }
    }

    /**
     *  Construct a RelativeTime instance given the period (in days).
     *
     * @param period Relative time in days.
     */
    public RelativeTime( Double period ){
        init( period );
    }

    /**
     * Returns a string representation of this object.
     * Format:  DDD:HH:MM:SS.SSS
     *
     * @return a String representation of this object.
     */
    public String toString(){
        String result = null;

        // normalize internal representation:
        //   o so components are in their typcial ranges.
        normalize();
        result = String.format( "%03d:%02d:%02d:%06.3f", 
                                days.intValue(),
                                hours.intValue(),
                                minutes.intValue(),
                                seconds.floatValue());

        return result;
    }
    /**
     * Convert to decimal form.
     *
     * @return instance expressed as decimal "number of days".
     */
    public Double toDecimal(){
        Double ndays;

        Double dd = Double.valueOf( days );
        Double hh = Double.valueOf( hours );
        Double mm = Double.valueOf( minutes );
        Double ss = seconds;

        ndays = dd + ( hh + (mm + (ss/60.0))/60.0 )/24.0;

        return ndays;
    }

    /**
     * Obtains the value of the DAYS component as an Integer.
     *
     * @return Days component of this RelativeTime
     */
    public Integer getDays(){
        return days;
    }
    /**
     * Obtains the value of the HOURS component as an Integer.
     *
     * @return Hours component of this RelativeTime
     */
    public Integer getHours(){
        return hours;
    }
    /**
     * Obtains the value of the MINUTES component as an Integer.
     *
     * @return MINUTES component of this RelativeTime
     */
    public Integer getMinutes(){
        return minutes;
    }
    /**
     * Obtains the value of the SECONDS component as an Double.
     *
     * @return SECONDS component of this RelativeTime
     */
    public Double getSeconds(){
        return seconds;
    }

    /**
     * Checks if this instance is longer than the specified number of days.
     *
     * @param ndays value to test against
     * @return true if this instance is longer than the specified number of days. false otherwise.
     */
    public boolean isLongerThan( Double ndays ){
        boolean result = ( (this.toDecimal() > ndays ) ? true : false );
        return result;
    }

    /**
     * Checks if this instance is longer than the provided RelativeTime.
     *
     * @param other RelativeTime to test against
     * @return true if this instance is longer than the specified RelativeTime. false otherwise.
     */
    public boolean isLongerThan( RelativeTime other ){
        boolean result = ( (this.toDecimal() > other.toDecimal() ) ? true : false );
        return result;
    }

    /**
     * Computes a new RelativeTime whose value is this - other.
     *
     * @param other RelativeTime to subtract from this instance.
     * @return new RelativeTime with the result.
     */
    public RelativeTime subtract( RelativeTime other ){
        Double ndays = this.toDecimal() - other.toDecimal();
        RelativeTime result = new RelativeTime( ndays );
        return result;
    }


    // set the internal representation based on the provided decimal.
    private void init( Double ndays ){
        Double temp;
        Double dval = ndays;
        days = dval.intValue();

        dval = (dval - days)*24.0;
        hours = dval.intValue();
        
        dval = (dval - hours)*60.0;
        temp = dval + 0.00001; // a little padding for precision issues with intValue()
        minutes = temp.intValue();
        
        dval = (dval - minutes)*60.0;
        if ( Math.abs(dval) < 0.00001 ){
            seconds = 0.0000000000;
        }
        else{
            seconds = dval;
        }
    }
    // reset the internal representation based on the provided decimal.
    private void normalize(){
        Double ndays = this.toDecimal();
        init( ndays );
    }
}
