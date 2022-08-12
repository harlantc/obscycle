//package info;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;
import java.text.SimpleDateFormat;
import info.AbsoluteTime;

public class AbsoluteTimeTest {

    AbsoluteTime instant;

    @Before
    public void setUp(){
    }

    @After
    public void tearDown(){
    }

    @Test
    public void test_constructor_default() {
        AbsoluteTime result;

        /* default constructor - returns 'now' */
        /*   o obviously can not validate results easily */
        result = new AbsoluteTime();
        assertTrue( (result != null) );

        /* to see result */
        //assertEquals( "now", result.toString() ); 
    }

    @Test
    public void test_constructor_AbsTime1() {

        String[] samples = {"2022:019:12:24:46.381",
                            "2022:039:12:24:46.3",
                            "2022:069:12:24:46.381654",
                           };
        String[] expected = {"2022:019:12:24:46.381",
                             "2022:039:12:24:46.300",
                             "2022:069:12:24:46.381",
        };

        for (int ii = 0; ii < samples.length; ii++ ){
            instant = new AbsoluteTime( samples[ii] );

            // validate
            assertNotNull( instant );
            assertEquals( expected[ii], instant.toString( AbsoluteTime.ABSOLUTE_TIME_FORMAT_1 ) ); 
        }
    }

    @Test
    public void test_constructor_AbsTime2() {

        String[] samples = {"2022-01-19T12:24:46.381",
                            "2022-03-19T12:24:46.3",
                            "2022-06-19T12:24:46.381654",
                           };
        String[] expected = {"2022-01-19T12:24:46.381",
                             "2022-03-19T12:24:46.300",
                             "2022-06-19T12:24:46.381",
        };

        for (int ii = 0; ii < samples.length; ii++ ){

            instant = new AbsoluteTime( samples[ii] );

            // validate
            assertNotNull( instant );
            assertEquals( expected[ii], instant.toString( AbsoluteTime.ABSOLUTE_TIME_FORMAT_2 ) ); 
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_constructor_bad_format() {

        String tstring = "2022/01/19 12:24:46.046";

        try {
            instant = new AbsoluteTime( tstring );
        }catch( RuntimeException ex ){
            assertEquals( "Input source string not in supported format.", ex.getMessage() );
            throw ex;
        }
        fail();
    }
    @Test(expected=IllegalArgumentException.class)
    public void test_constructor_bad_AbsTime1() {

        // DOY is too large
        String tstring = "2022:939:12:24:46.300";

        try {
            instant = new AbsoluteTime( tstring );
        }catch( RuntimeException ex ){
            assertEquals( "invalid date", ex.getMessage() );
            throw ex;
        }
        fail();
    }
    @Test(expected=IllegalArgumentException.class)
    public void test_constructor_bad_AbsTime2() {

        // HOUR is too large
        String tstring = "2022-03-19T62:24:46.3";

        try {
            instant = new AbsoluteTime( tstring );
        }catch( RuntimeException ex ){
            assertEquals( "invalid date", ex.getMessage() );
            throw ex;
        }
        fail();
    }

    @Test
    public void test_isBefore() {
        AbsoluteTime sample;
        boolean result;

        // reference is now.
        AbsoluteTime now = new AbsoluteTime();

        // check true 
        sample = new AbsoluteTime("2000:001:00:00:00.00");
        result = sample.isBefore( now );
        assertTrue( result );

        // check false
        sample = new AbsoluteTime("2999:001:00:00:00.00");
        result = sample.isBefore( now );
        assertFalse( result );
    }

    @Test
    public void test_getComponents() {
        Double epsilon = 0.0001;

        instant = new AbsoluteTime("2022:139:12:24:46.080");
        assertEquals( Integer.valueOf( 2022 ), instant.getYear() );
        assertEquals( Integer.valueOf( 5 ),  instant.getMonth() );
        assertEquals( Integer.valueOf( 139 ), instant.getDayOfYear() );
        assertEquals( Integer.valueOf( 19 ), instant.getDayOfMonth() );
        assertEquals( Integer.valueOf( 12 ), instant.getHours() );
        assertEquals( Integer.valueOf( 24 ), instant.getMinutes() );
        assertEquals( Double.valueOf( 46.080 ), instant.getSeconds(), epsilon );

        instant = new AbsoluteTime("2022-05-19T12:24:46.080");
        assertEquals( Integer.valueOf( 2022 ), instant.getYear() );
        assertEquals( Integer.valueOf( 5 ),  instant.getMonth() );
        assertEquals( Integer.valueOf( 139 ), instant.getDayOfYear() );
        assertEquals( Integer.valueOf( 19 ), instant.getDayOfMonth() );
        assertEquals( Integer.valueOf( 12 ), instant.getHours() );
        assertEquals( Integer.valueOf( 24 ), instant.getMinutes() );
        assertEquals( Double.valueOf( 46.080 ), instant.getSeconds(), epsilon );
    }

    @Test
    public void test_getDeltaDays() {
        AbsoluteTime sample;
        AbsoluteTime reference;
        Double result;
        Double epsilon = 0.0001;

        reference = new AbsoluteTime("2000:001:00:00:00.00");
        sample    = new AbsoluteTime("2000:002:00:00:00.00");

        // positive
        result = sample.getDeltaDays( reference );
        assertEquals( 1.0000, result, epsilon );

        // negative
        result = reference.getDeltaDays( sample );
        assertEquals( -1.0000, result, epsilon );

        // leap days ( 366.0 + 365.0 )
        sample = new AbsoluteTime("2002:001:00:00:00.00");
        result = sample.getDeltaDays( reference );
        assertEquals( 731.0000, result, epsilon );

        // decimal days (10.34938)
        sample = new AbsoluteTime("2000:011:08:23:07.00");
        result = sample.getDeltaDays( reference );
        assertEquals( 10.3494, result, epsilon );
    }

    @Test
    public void test_toString() {

        Double sample = 59598.51720;
        String expected_at1 = "2022:019:12:24:46.080";
        String expected_at2 = "2022-01-19T12:24:46.080";

        instant = AbsoluteTime.fromMJD( sample );

        // default is AT1 format
        assertEquals( expected_at1, instant.toString() );

        // specify AT1 format
        assertEquals( expected_at1, instant.toString( AbsoluteTime.ABSOLUTE_TIME_FORMAT_1 ) );

        // specify AT2 format
        assertEquals( expected_at2, instant.toString( AbsoluteTime.ABSOLUTE_TIME_FORMAT_2 ) );

    }

    @Test
    public void test_fromMJD() {

        // Values obtained from HEASARC Date conversion tool
        //  * verified with the DATES toolkit in this package.
        Double[] samples = { 0.0000,
                             40587.000,    // Jan 01, 1970
                             44239.000,    // Jan 01, 1980
                             47892.000,    // Jan 01, 1990
                             51544.000,    // Jan 01, 2000
                             55197.000,    // Jan 01, 2010
                             58849.000,    // Jan 01, 2020
                             62502.000,    // Jan 01, 2030
                             51272.900,    // Apr 04, 1999 @ 21:36:00.00
                             51382.18820,  // Jul 23, 1999 @ 04:31:00.479
                             52163.36574,  // Sep 11, 2001 @ 08:46:39.936
                             59598.51720,  // Jan 19, 2022 @ 12:24:46.046
                             72357.50000   // Dec 31, 2056 @ 12:00:00.000
                             };

        String[] expected = { "1858:321:00:00:00.000",
                              "1970:001:00:00:00.000",
                              "1980:001:00:00:00.000",
                              "1990:001:00:00:00.000",
                              "2000:001:00:00:00.000",
                              "2010:001:00:00:00.000",
                              "2020:001:00:00:00.000",
                              "2030:001:00:00:00.000",
                              "1999:094:21:36:00.000",
                              "1999:204:04:31:00.479",
                              "2001:254:08:46:39.936",
                              "2022:019:12:24:46.080",
                              "2056:360:12:00:00.000" };

        for ( int ii = 0; ii < samples.length; ii++ ){
            instant = AbsoluteTime.fromMJD( samples[ ii ] );
            assertEquals( expected[ii], instant.toString() );
        }
    }

    @Test
    public void test_fixAbsTimeString() {

        String[] samples = {  "2022-01-19T12:24:12.345",
                              "2022-02-19T12:24:12.34",
                              "2022-03-19T12:24:12.3",
                              "2022-05-19T12:24:12",
                              "2022-06-19T12:24",
                              "2022-07-19T12:24:46.381659"};

        String[] expected = { "2022-01-19T12:24:12.345",
                              "2022-02-19T12:24:12.340",
                              "2022-03-19T12:24:12.300",
                              "2022-05-19T12:24:12.000",
                              "2022-06-19T12:24:00.000",
                              "2022-07-19T12:24:46.381"};

        String result;

        // The fix is to allow the string to be properly interpreted by SimpleDateFormat
        SimpleDateFormat format = new SimpleDateFormat(AbsoluteTime.ABSOLUTE_TIME_FORMAT_2);
        Date date;

        for ( int ii = 0; ii < samples.length; ii++ ){
            result = AbsoluteTime.fixAbsTimeString( samples[ ii ] );

            try{ 
                date = format.parse( result );
            }catch( Exception ex ){
                date = null;
            }

            assertNotNull( String.format("case: %1$s",samples[ii]), date );
            assertEquals( expected[ii], result );
        }

        // Should work on AT1 format as well
        result = AbsoluteTime.fixAbsTimeString( "2022:019:12:24:46.381659" );
        assertEquals( "2022:019:12:24:46.381", result );

        result = AbsoluteTime.fixAbsTimeString( "2022:019:12:24:12.3" );
        assertEquals( "2022:019:12:24:12.300", result );

        result = AbsoluteTime.fixAbsTimeString( "2022:019:12:24:12." );
        assertEquals( "2022:019:12:24:12.000", result );

        // Non-matching strings pass unchanged.
        result = AbsoluteTime.fixAbsTimeString( "51382.18820" );
        assertEquals( "51382.18820", result );
    }
}
