//package info;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import info.RelativeTime;

public class RelativeTimeTest {

    @Before
    public void setUp(){
    }

    @After
    public void tearDown(){
    }

    @Test
    public void test_constructor_default() {
        RelativeTime result;

        result = new RelativeTime();
        assertEquals( "000:00:00:00.000", result.toString() );

    }
    @Test(expected=NumberFormatException.class)
    public void test_constructor_emptyValue() {
        RelativeTime result;
        try{
            result = new RelativeTime( " " );
        }catch( NumberFormatException ex ){
            assertEquals( "Invalid format.", ex.getMessage());
            throw ex;
        }
    }
    @Test(expected=NumberFormatException.class)
    public void test_constructor_badFormat() {
        RelativeTime result;
        try{
            result = new RelativeTime( "038 10 42 29.952" );
        }catch( NumberFormatException ex ){
            assertEquals( "Invalid format.", ex.getMessage());
            throw ex;
        }
    }
    @Test
    public void test_constructor_RelTime() {
        RelativeTime result;

        result = new RelativeTime("038:10:42:29.952");
        assertEquals( "038:10:42:29.952", result.toString() );
    }
    @Test
    public void test_constructor_Decimal() {
        RelativeTime result;

        result = new RelativeTime("38.44618");
        assertEquals( "038:10:42:29.952", result.toString() );

	// even day
        result = new RelativeTime("207.00");
        assertEquals( "207:00:00:00.000", result.toString() );

	// even hour
        result = new RelativeTime("207.25");
        assertEquals( "207:06:00:00.000", result.toString() );

	// even minute
        result = new RelativeTime("207.85");
        assertEquals( "207:20:24:00.000", result.toString() );
    }
    @Test
    public void test_toDecimal(){
        RelativeTime rt;
        Double expected;
        Double result;
        Double epsilon = 0.00000001;

        rt = new RelativeTime("000:00:00:00.000");
        result = rt.toDecimal();
        expected = Double.valueOf(0.0);
        assertEquals( expected, result, epsilon );

        rt = new RelativeTime("001:00:00:00.000");
        result = rt.toDecimal();
        expected = Double.valueOf(1.0);
        assertEquals( expected, result, epsilon );

        rt = new RelativeTime("001:01:00:00.000");
        result = rt.toDecimal();
        expected = Double.valueOf(1.04166666);
        assertEquals( expected, result, epsilon );

        rt = new RelativeTime("001:01:01:00.000");
        result = rt.toDecimal();
        expected = Double.valueOf(1.04236111);
        assertEquals( expected, result, epsilon );

        rt = new RelativeTime("001:01:01:01.000");
        result = rt.toDecimal();
        expected = Double.valueOf(1.04237268);
        assertEquals( expected, result, epsilon );

        rt = new RelativeTime("001:01:01:01.001");
        result = rt.toDecimal();
        expected = Double.valueOf(1.04237269);
        assertEquals( expected, result, epsilon );

        rt = new RelativeTime("001:12:00:00.000");
        result = rt.toDecimal();
        expected = Double.valueOf(1.5000000);
        assertEquals( expected, result, epsilon );

        // individual elements not range checked 
        rt = new RelativeTime("001:36:90:00.000");
        result = rt.toDecimal();
        expected = Double.valueOf(2.5625000);
        assertEquals( expected, result, epsilon );
    }

    @Test
    public void test_isLongerThan() {
        RelativeTime rt1;
        RelativeTime rt2;
        boolean result;

        rt1 = new RelativeTime("38.44618");
        rt2 = new RelativeTime("12.12345");

        // True case
        result = rt1.isLongerThan( 1.0 );
        assertTrue( result );

        // False case
        result = rt1.isLongerThan( 100.0 );
        assertFalse( result );

        // False case
        result = rt1.isLongerThan( 38.44618 );
        assertFalse( result );

        // True case
        result = rt1.isLongerThan( rt2 );
        assertTrue( result );

        // False case
        result = rt2.isLongerThan( rt1 );
        assertFalse( result );

    }
    
    @Test
    public void test_subtract() {
        RelativeTime rt1;
        RelativeTime rt2;
        RelativeTime delta;
        Double epsilon = 0.00000001;

        rt1 = new RelativeTime("38.44618");
        rt2 = new RelativeTime("00.00000");

        assertEquals( "000:00:00:00.000", rt2.toString() );
        
        delta = rt1.subtract( rt2 );
        assertEquals( Double.valueOf( 38.44618 ), delta.toDecimal(), epsilon );

        delta = rt2.subtract( rt1 );
        assertEquals( Double.valueOf( -38.44618 ), delta.toDecimal(), epsilon );

        delta = rt1.subtract( rt1 );
        assertEquals( Double.valueOf( 0.00000 ), delta.toDecimal(), epsilon );
    }

    @Test
    public void test_getComponents() {
        RelativeTime rt;
        Double epsilon = 0.00000001;

        rt = new RelativeTime("038:10:42:29.952");
        assertEquals( Integer.valueOf( 38 ), rt.getDays() );
        assertEquals( Integer.valueOf( 10 ), rt.getHours() );
        assertEquals( Integer.valueOf( 42 ), rt.getMinutes() );
        assertEquals( Double.valueOf( 29.952 ), rt.getSeconds(), epsilon );

        rt = new RelativeTime();
        assertEquals( Integer.valueOf( 0 ), rt.getDays() );
        assertEquals( Integer.valueOf( 0 ), rt.getHours() );
        assertEquals( Integer.valueOf( 0 ), rt.getMinutes() );
        assertEquals( Double.valueOf( 0.0), rt.getSeconds(), epsilon );

        rt = new RelativeTime("038:90:132:500.0");
        assertEquals( Integer.valueOf( 38 ), rt.getDays() );
        assertEquals( Integer.valueOf( 90 ), rt.getHours() );
        assertEquals( Integer.valueOf( 132 ), rt.getMinutes() );
        assertEquals( Double.valueOf( 500.0 ), rt.getSeconds(), epsilon );
        assertEquals( "041:20:20:20.000", rt.toString() );
    }

}
