//package info;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

import info.ToolkitValidator;

public class ToolkitValidatorTest {

    private ToolkitValidator validator;
    private HttpServletRequest request;
    private Properties props;

    @Before
    public void setUp(){
        try {
            props = load_properties();
        }catch (Exception ex ){
            fail( ex.getMessage() );
        }
        request = new MockRequest();
        validator = new ToolkitValidator(request, props);
    }

    @After
    public void tearDown(){
        validator = null;
        request = null;
        props = null;
    }

    /*   Longitude Parameter */
    @Test
    public void test_validatePosition_emptyValue() {
        String result;

        // Case: value is empty
        request.setAttribute("sample", " " );
        result = validator.validateLongitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertEquals( "Sample: empty field\n", result );
        assertEquals( validator.BG_COLOR_ERROR, (String)request.getSession().getAttribute("sampleBoxColor") );
    }

    @Test
    public void test_validatePosition_badFormat() {
        String result;

        // Case: HHh MMm SS[.SSS]s   DDd MM' SS.SSS"
        request.setAttribute("sample", "10h 45m 03.55s" );
        result = validator.validateLongitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertEquals( "Sample: invalid format\n", result );
        assertEquals( validator.BG_COLOR_ERROR, (String)request.getSession().getAttribute("sampleBoxColor") );
    }

    @Test
    public void test_validatePosition_sexagesimalValue() {
        String result;
        //     * HH:MM[:SS[.SSS]]  DD:MM[:SS.SSS]]
        //     * HH MM[ SS[.SSS]]  DD MM[ SS.SSS]]

        // Longitudes
        request.setAttribute("sample", "10:45:03.55" );
        result = validator.validateLongitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

        request.setAttribute("sample", "10:45:03" );
        result = validator.validateLongitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

        request.setAttribute("sample", "10:45" );
        result = validator.validateLongitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

        request.setAttribute("sample", "10 45 03.55" );
        result = validator.validateLongitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

        request.setAttribute("sample", "10 45 03" );
        result = validator.validateLongitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

        request.setAttribute("sample", "10 45" );
        result = validator.validateLongitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

        // Latitudes
        request.setAttribute("sample", "-59:41:03.97" );
        result = validator.validateLatitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

        request.setAttribute("sample", "-59:41:03" );
        result = validator.validateLatitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

        request.setAttribute("sample", "-59:41" );
        result = validator.validateLatitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

        request.setAttribute("sample", "-59 41 03.97" );
        result = validator.validateLatitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

        request.setAttribute("sample", "-59 41 03" );
        result = validator.validateLatitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

        request.setAttribute("sample", "-59 41" );
        result = validator.validateLatitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

    }

    @Test
    public void test_validatePosition_decimalValue() {
        String result;

        request.setAttribute("sample", "10.750986" );
        result = validator.validateLongitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

        request.setAttribute("sample", "-59.684436" );
        result = validator.validateLatitudeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

    }


    /*   Range Parameter */
    @Test
    public void test_validateRange_noProperty() {
        String result;

        // Case: property does not exist
        request.setAttribute("sample", "0.275" );
        result = validator.validateRangeParameter( "sample", "sampleBoxColor", "test.bogus.range", "test.sample.input" );
        assertEquals( "Sample: limits not found\n", result );
        assertEquals( validator.BG_COLOR_ERROR, (String)request.getSession().getAttribute("sampleBoxColor") );
    }

    @Test
    public void test_validateRange_emptyValue() {
        String result;

        // Case: value is empty
        request.setAttribute("sample", " " );
        result = validator.validateRangeParameter( "sample", "sampleBoxColor", "test.sample.range", "test.sample.input" );
        assertEquals( "Sample: empty field\n", result );
        assertEquals( validator.BG_COLOR_ERROR, (String)request.getSession().getAttribute("sampleBoxColor") );
    }

    @Test
    public void test_validateRange_valueNotNumeric() {
        String result;

        // Case: value is non-numeric
        request.setAttribute("sample", "BOGUS" );
        result = validator.validateRangeParameter( "sample", "sampleBoxColor", "test.sample.range", "test.sample.input" );
        assertEquals( "Sample: value must be numeric\n", result );
        assertEquals( validator.BG_COLOR_ERROR, (String)request.getSession().getAttribute("sampleBoxColor") );
    }

    @Test
    public void test_validateRange_numericValuePlus() {
        String result;

        // Case: value is non-numeric
        request.setAttribute("sample", "0.275 Plus other stuff" );
        result = validator.validateRangeParameter( "sample", "sampleBoxColor", "test.sample.range", "test.sample.input" );
        assertEquals( "Sample: value must be numeric\n", result );
        assertEquals( validator.BG_COLOR_ERROR, (String)request.getSession().getAttribute("sampleBoxColor") );
    }

    @Test
    public void test_validateRange_lowValue() {
        String result;

        // Case: value is non-numeric
        request.setAttribute("sample", "-0.275" );
        result = validator.validateRangeParameter( "sample", "sampleBoxColor", "test.sample.range", "test.sample.input" );
        assertEquals( "Sample: out of range\n", result );
        assertEquals( validator.BG_COLOR_ERROR, (String)request.getSession().getAttribute("sampleBoxColor") );
    }

    @Test
    public void test_validateRange_highValue() {
        String result;

        // Case: value is non-numeric
        request.setAttribute("sample", "10.5" );
        result = validator.validateRangeParameter( "sample", "sampleBoxColor", "test.sample.range", "test.sample.input" );
        assertEquals( "Sample: out of range\n", result );
        assertEquals( validator.BG_COLOR_ERROR, (String)request.getSession().getAttribute("sampleBoxColor") );
    }

    @Test
    public void test_validateRange_good() {
        String result;

        // Case: value is non-numeric
        request.setAttribute("sample", "0.275" );
        result = validator.validateRangeParameter( "sample", "sampleBoxColor", "test.sample.range", "test.sample.input" );
        assertNull( result );
        assertEquals( validator.BG_COLOR_NORMAL, (String)request.getSession().getAttribute("sampleBoxColor") );
    }

    /*   Absolute Time Parameter */
    @Test
    public void test_validateAbsTime_emptyValue() {
        String result;

        request.setAttribute("sample", " " );
        result = validator.validateAbsTimeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertEquals( "Sample: empty field\n", result );
        assertEquals( validator.BG_COLOR_ERROR, (String)request.getSession().getAttribute("sampleBoxColor") );
    }
    @Test
    public void test_validateAbsTime_badFormat() {
        String result;

        request.setAttribute("sample", "NGC.4275" );
        result = validator.validateAbsTimeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertEquals( "Sample: invalid format\n", result );
        assertEquals( validator.BG_COLOR_ERROR, (String)request.getSession().getAttribute("sampleBoxColor") );

        request.setAttribute("sample", "1999/Jun/23 04:30:59.984" );
        result = validator.validateAbsTimeParameter( "sample", "sampleBoxColor", "test.sample.input" );

    }
    @Test
    public void test_validateAbsTime_invalidContent() {
        String result;
        String expected = "Sample: invalid date\n";

        request.setAttribute("sample", "1999:402:99:99:99.999" );
        result = validator.validateAbsTimeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertEquals( expected, result );
        assertEquals( validator.BG_COLOR_ERROR, (String)request.getSession().getAttribute("sampleBoxColor") );
    }
    @Test
    public void test_validateAbsTime_format1() {
        String result;
        request.setAttribute("sample", "1999:204:04:30:59.984" );
        result = validator.validateAbsTimeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );
        assertEquals( validator.BG_COLOR_NORMAL, (String)request.getSession().getAttribute("sampleBoxColor") );
    }
    @Test
    public void test_validateAbsTime_format2() {
        String result;
        request.setAttribute("sample", "2022-03-01T04:30:59.984" );
        result = validator.validateAbsTimeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );
        assertEquals( validator.BG_COLOR_NORMAL, (String)request.getSession().getAttribute("sampleBoxColor") );
    }
    @Test
    public void test_validateAbsTime_mjd() {
        String result;
        request.setAttribute("sample", "51382.18819426" );
        result = validator.validateAbsTimeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );
        assertEquals( validator.BG_COLOR_NORMAL, (String)request.getSession().getAttribute("sampleBoxColor") );
    }

    /*   Relative Time Parameter */
    @Test
    public void test_validateRelTime_emptyValue() {
        String result;

        request.setAttribute("sample", " " );
        result = validator.validateRelTimeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertEquals( "Sample: empty field\n", result );
        assertEquals( validator.BG_COLOR_ERROR, (String)request.getSession().getAttribute("sampleBoxColor") );
    }
    @Test
    public void test_validateRelTime_badFormat() {
        String result;

        // Absolute time format 
        request.setAttribute("sample", "1999:204:04:30:59.984" );
        result = validator.validateRelTimeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertEquals( "Sample: invalid format\n", result );

    }
    @Test
    public void test_validateRelTime_invalidContent() {
        String result;
        String expected = "Sample: DDD out of range\nSample: HH out of range\nSample: MM out of range\nSample: SS out of range\n";

        request.setAttribute("sample", "1402:99:99:99.999" );
        result = validator.validateRelTimeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertEquals( expected, result );
        assertEquals( validator.BG_COLOR_ERROR, (String)request.getSession().getAttribute("sampleBoxColor") );
    }
    @Test
    public void test_validateRelTime_good() {
        String result;
        // Relative Time format
        request.setAttribute("sample", "204:07:15:12.5" );
        result = validator.validateRelTimeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );
        assertEquals( validator.BG_COLOR_NORMAL, (String)request.getSession().getAttribute("sampleBoxColor") );

        // can go > 1 year
        request.setAttribute("sample", "999:23:59:59.999" );
        result = validator.validateRelTimeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

        // Decimal format
        request.setAttribute("sample", "0.275" );
        result = validator.validateRelTimeParameter( "sample", "sampleBoxColor", "test.sample.input" );
        assertNull( result );

    }

    /* ---------------------------------------------------------------------- */
    private Properties load_properties()
        throws IOException {
        Properties props = new Properties();
        String propsfile;
        String htpathfile;

        try {
            propsfile = getClass().getClassLoader().getResource("toolkit.test.properties").getFile();
            htpathfile = getClass().getClassLoader().getResource("htpath.properties").getFile();
        }
        catch (Exception ex ){
            throw new IOException( "Problem finding properties file.");
        }

        try {
            FileInputStream fis;

            // Load the toolkit properties file.
            fis = new FileInputStream( new File( propsfile ) );
            props.load( fis );
            fis.close();

            // Add .htpath properties
            // NOTE: Toolkit.addProps( props );
            //  * expects at ${OBSCYCLE_DATA_PATH}/prop/toolkit/.htpath
            fis = new FileInputStream( new File( htpathfile ) );
            props.load( fis );
            fis.close();
        }
        catch ( IOException ex ){
            throw new IOException( "Problem setting up properties." + ex.getMessage());
        }

        return props;
    }

}
