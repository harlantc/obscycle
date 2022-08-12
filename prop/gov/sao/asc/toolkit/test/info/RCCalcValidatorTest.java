//package <path to package being tested>

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

import info.RCCalcValidator;

public class RCCalcValidatorTest {

    private RCCalcValidator validator;
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
        validator = new RCCalcValidator(request, props);
    }

    @After
    public void tearDown(){
        validator = null;
        request = null;
        props = null;
    }

    @Test
    public void test_validatePosition_good() {
        boolean results;

        init_session_good();
        results = validator.validatePosition();

        assertFalse( results );
    }
    @Test
    public void test_validatePosition_bad() {
        boolean results;
        String[] issues;

        init_session_bad();
        results = validator.validatePosition();

        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( "RA or L2 or EL input: empty field\n", issues[0] );
        assertEquals( "DEC or B2 or EB input: empty field\n", issues[1] );

        // correlation check: both must not be zero.
        request.setAttribute("inputPosition1", "0.0" );
        request.setAttribute("inputPosition2", "00:00:00.0000" );

        validator.clearErrorStack();
        results = validator.validatePosition();
        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( "Position: Longitude and Latitude must not both be zero.\n", issues[0]);
    }
    @Test
    public void test_validateExposureTime_good() {
        boolean results;

        init_session_good();
        results = validator.validateExposureTime();

        assertFalse( results );
    }
    @Test
    public void test_validateExposureTime_bad() {
        boolean results;
        String[] issues;

        init_session_bad();
        results = validator.validateExposureTime();

        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( "Exposure Time: value must be numeric\n", issues[0] );
    }
    @Test
    public void test_validateSplitConstraint_good() {
        boolean results;

        init_session_good();
        results = validator.validateSplitConstraint();

        assertFalse( results );
    }
    @Test
    public void test_validateSplitConstraint_bad() {
        boolean results;
        String[] issues;

        init_session_bad();
        results = validator.validateSplitConstraint();

        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( "Split Interval: out of range\n", issues[0] );
    }
    @Test
    public void test_validateSplitConstraint_badFlags() {
        boolean results;
        String[] issues;

        // test correlation check:
        init_session_good();
        request.setAttribute("uninterrupted", "Yes");
        request.setAttribute("splitConstraint", "Yes");
        results = validator.validateSplitConstraint();

        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( "Split Constraint and Uninterrupted must not both be 'Yes'.\n", issues[0] );

    }
    @Test
    public void test_validateSplitConstraint_badVsExptime() {
        boolean results;
        String[] issues;

        // test correlation check:
        init_session_good();
        request.setAttribute("propExposureTime", "500"); // 500 ks.
        request.setAttribute("splitConstraint", "Yes");
        request.setAttribute("splitInterval", "5.0");    // 5 days = 432 ks.
        results = validator.validateSplitConstraint();

        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( "Split Interval: must be >= Exposure Time.\n", issues[0] );

    }
    @Test
    public void test_validateCoordinationWindow_good() {
        boolean results;

        init_session_good();
        results = validator.validateCoordinationWindow();

        assertFalse( results );
    }
    @Test
    public void test_validateCoordinationWindow_bad() {
        boolean results;
        String[] issues;

        init_session_bad();
        results = validator.validateCoordinationWindow();

        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( "Coordination Window: out of range\n", issues[0] );
    }
    @Test
    public void test_validateChipSelection_good() {
        boolean results;

        init_session_good();
        results = validator.validateChipSelection();

        assertFalse( results );
    }
    @Test
    public void test_validateChipSelection_bad() {
        boolean results;
        String[] issues;

        init_session_bad();
        results = validator.validateChipSelection();

        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( "Chip Count: value must be numeric\n", issues[0] );
    }
    @Test
    public void test_validatePhaseConstraint_good() {
        boolean results;

        init_session_good();
        results = validator.validatePhaseConstraint();

        assertFalse( results );

        // try with MJD date
        init_session_good();
        request.setAttribute("phaseEpoch", "58235.000");

        results = validator.validatePhaseConstraint();
        assertFalse( results );

    }
    @Test
    public void test_validatePhaseConstraint_bad() {
        boolean results;
        String[] issues;

        init_session_bad();

        results = validator.validatePhaseConstraint();
        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( 6, issues.length);
        assertEquals("Phase Epoch: invalid format\n", issues[0]);
        assertEquals("Phase Period: invalid format\n", issues[1]);
        assertEquals("Phase Start: out of range\n", issues[2]);
        assertEquals("Phase Start Margin: out of range\n", issues[3]);
        assertEquals("Phase Stop: out of range\n", issues[4]);
        assertEquals("Phase Stop Margin: out of range\n", issues[5]);

        // correlation check: phase epoch date must be within 5 years of today's date
        init_session_good();
        request.setAttribute("phaseEpoch", "2010:117:00:00:00.000");
        validator.clearErrorStack();
        results = validator.validatePhaseConstraint();
        assertTrue( results );
        issues = validator.getIssues();
        assertEquals("Phase Epoch: Must be within 5 years of today's date.\n", issues[0]);

    }
    //@Test
    //public void test_validatePointingConstraint_good() {
    //}
    //@Test
    //public void test_validatePointingConstraint_bad() {
    //}
    @Test
    public void test_validateGroupConstraint_good() {
        boolean results;

        init_session_good();
        results = validator.validateGroupConstraint();

        assertFalse( results );
    }
    @Test
    public void test_validateGroupConstraint_bad() {
        boolean results;
        String[] issues;

        init_session_bad();
        results = validator.validateGroupConstraint();

        assertTrue( results );
        issues = validator.getIssues();
        assertEquals("Group Max. Interval: MM out of range\n", issues[0]);
    }
    @Test
    public void test_validateMonitorConstraint_good() {
        boolean results;

        init_session_good();
        results = validator.validateMonitorConstraint();

        assertFalse( results );
    }
    @Test
    public void test_validateMonitorConstraint_bad() {
        boolean results;
        String[] issues;

        init_session_bad();
        results = validator.validateMonitorConstraint();

        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( 4, issues.length );
        assertEquals( "Monitor Exposure Time 1: value must be numeric\n", issues[0]);
        assertEquals( "Monitor Split Interval 1: out of range\n", issues[1]);
        assertEquals( "Monitor Min. Interval 2: SS out of range\n", issues[2]);
        assertEquals( "Monitor Max. Interval 2: MM out of range\n", issues[3]);

        // correlation check: Lead interval min > max
        request.setAttribute("monitorPreMinLead1", "006:08:12:00.000" );
        request.setAttribute("monitorPreMaxLead1", "002:01:30:00.000" );

        validator.clearErrorStack();
        results = validator.validateMonitorConstraint();
        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( "Monitor Max. Interval 2: must be greater than Min.\n", issues[2]);
    }
    @Test
    public void test_validateMonitorConstraint_badExpTime() {
        boolean results;
        String[] issues;

        init_session_good();

        // customize for correlation check: Exposure times
        request.setAttribute("propExposureTime", "1.0" );
        request.setAttribute("monitorExpTime0", "0" );
        request.setAttribute("monitorExpTime1", "4.5" );

        results = validator.validateMonitorConstraint();

        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( "Monitor Exposure Time 1: out of range\n", issues[0]);
        assertEquals( "Monitor Exposure Time 2: must be less than proposal exposure time.\n", issues[1]);
    }
    @Test
    public void test_validateMonitorConstraint_badSplit() {
        boolean results;
        String[] issues;

        init_session_good();

        // customize for correlation check: Exposure times
        request.setAttribute("monitorSplitInterval0", "599" );
        request.setAttribute("monitorSplitInterval1", "4.5" );

        results = validator.validateMonitorConstraint();

        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( "Monitor Split Interval 1: must be within min/max intervals.\n", issues[0]);
        assertEquals( "Monitor Split Interval 2: must be within min/max intervals.\n", issues[1]);
    }

    @Test
    public void test_validateWindowConstraint_good() {
        boolean results;
    
        init_session_good();
        results = validator.validateWindowConstraint();
    
        assertFalse( results );
    }
    @Test
    public void test_validateWindowConstraint_bad() {
        boolean results;
        String[] issues;
    
        init_session_bad();
        results = validator.validateWindowConstraint();
    
        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( "Window Start 1: invalid date\n", issues[0]);
        assertEquals( "Window Stop 1: invalid date\n", issues[1]);

        // correlation check:
        //   * Window Stop > Window Start
        request.setAttribute("windowStartTime0", "2025-03-01T00:00:00.000");
        request.setAttribute("windowStopTime0",  "2025-01-01T00:00:00.000");

        validator.clearErrorStack();
        results = validator.validateWindowConstraint();
        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( "Window Constraint 1: Stop time must be after start time.\n", issues[0] );

        //   * must be within 2 years of current cycle (ao:)
        request.setAttribute("windowStartTime0", "2020-01-30T00:00:00.000");
        request.setAttribute("windowStopTime0",  "2030-01-30T00:00:00.000");

        validator.clearErrorStack();
        results = validator.validateWindowConstraint();
        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( "Window Constraint 1: Start time must be after today's date.\n", issues[0] );
        assertEquals( "Window Start Time 1: Must be within 2 years of cycle.\n", issues[1] );
        assertEquals( "Window Stop Time 1: Must be within 2 years of cycle.\n", issues[2] );
    }
    @Test
    public void test_validateRollConstraint_good() {
        boolean results;
    
        init_session_good();
        results = validator.validateRollConstraint();
    
        assertFalse( results );
    }
    @Test 
    public void test_validateRollConstraint_bad() {
        boolean results;
        String[] issues;
    
        init_session_bad();
        results = validator.validateRollConstraint();
    
        assertTrue( results );
        issues = validator.getIssues();
        assertEquals( "Roll Angle 1: out of range\n", issues[0]);
        assertEquals( "Roll Tolerance 1: out of range\n", issues[1]);
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

    /**
     * Initialize request/session with valid content.
     */
    private void init_session_good(){
        request.setAttribute("inputPosition1", "10:45:03.55" );
        request.setAttribute("inputPosition2", "-59:41:03.97" );
        request.setAttribute("propExposureTime", "10.0");
        request.setAttribute("uninterrupted", "No");
        request.setAttribute("splitConstraint", "Yes");
        request.setAttribute("splitInterval", "100.0");
        request.setAttribute("coordinatedObs", "Yes");
        request.setAttribute("obsInterval", "100.0");
        request.setAttribute("instrument", "ACIS-I");
        request.setAttribute("requiredChipCount", "3");
        request.setAttribute("phaseConstraint", "Yes");
        request.setAttribute("phaseEpoch",  "2018:117:00:00:00.000");
        request.setAttribute("phasePeriod", "038:10:42:29.952");
        request.setAttribute("phaseStart",  "0.250000");
        request.setAttribute("phaseStartMargin", "0.050000");
        request.setAttribute("phaseStop", "0.500000");
        request.setAttribute("phaseStopMargin", "0.050000");
        request.setAttribute("phaseUnique", "No");
        request.setAttribute("pointingConstraint", "No");
        request.setAttribute("groupConstraint", "Yes");
        request.setAttribute("groupID", "NGC 5506_22700247");
        request.setAttribute("groupPreMaxLead", "020:00:00:00.000");
        request.setAttribute("monitorConstraint", "Yes");
        request.getSession().setAttribute("monitorNumRows", Integer.valueOf(2));
        request.setAttribute("monitorExpTime0", "4.5");
        request.setAttribute("monitorPreMinLead0", "000:00:00:00.000");
        request.setAttribute("monitorPreMaxLead0", "000:00:00:00.000");
        request.setAttribute("monitorSplitInterval0", "5");
        request.setAttribute("monitorExpTime1", "5.5");
        request.setAttribute("monitorPreMinLead1", "000:00:00:00.000");
        request.setAttribute("monitorPreMaxLead1", "002:00:00:00.000");
        request.setAttribute("monitorSplitInterval1", "1");
        request.setAttribute("windowConstraint", "Yes");
        request.getSession().setAttribute("windowNumRows", Integer.valueOf(1));
        request.setAttribute("windowStartTime0", "2023-01-01T03:45");
        request.setAttribute("windowStopTime0",  "2023-12-31T00:00:00");
        request.setAttribute("rollConstraint", "Yes");
        request.getSession().setAttribute("rollNumRows", Integer.valueOf(1));
        request.setAttribute("rollRotation0", "Yes");
        request.setAttribute("rollAngle0", "340.000000");
        request.setAttribute("rollTolerance0", "60.000000");
    }
    /**
     * Initialize request/session with invalid content.
     */
    private void init_session_bad(){
        request.setAttribute("inputPosition1", " " );
        request.setAttribute("inputPosition2", " " );
        request.setAttribute("propExposureTime", "BadValue");
        request.setAttribute("uninterrupted", "No");
        request.setAttribute("splitConstraint", "Yes");
        request.setAttribute("splitInterval", "740");
        request.setAttribute("coordinatedObs", "Yes");
        request.setAttribute("obsInterval", "366.0");
        request.setAttribute("instrument", "ACIS-I");
        request.setAttribute("requiredChipCount", "BadValue");
        request.setAttribute("phaseConstraint", "Yes");
        request.setAttribute("phaseEpoch", "2021/12/25T00:00:00");
        request.setAttribute("phasePeriod", "038d 10h 42m 29.952s");
        request.setAttribute("phaseStart", "100");
        request.setAttribute("phaseStartMargin", "10");
        request.setAttribute("phaseStop",  "200");
        request.setAttribute("phaseStopMargin", "10");
        request.setAttribute("pointingConstraint", "BadValue");
        request.setAttribute("groupConstraint", "Yes");
        request.setAttribute("groupID", "NGC 5506_22700247");
        request.setAttribute("groupPreMaxLead", "020:00:99:00.000");
        request.setAttribute("monitorConstraint", "Yes");
        request.getSession().setAttribute("monitorNumRows", Integer.valueOf(2));
        request.setAttribute("monitorExpTime0", "None");
        request.setAttribute("monitorPreMinLead0", "000:00:00:00.000");
        request.setAttribute("monitorPreMaxLead0", "000:00:00:00.000");
        request.setAttribute("monitorSplitInterval0", "0.0");
        request.setAttribute("monitorExpTime1", "4.5");
        request.setAttribute("monitorPreMinLead1", "006:00:00:60.000");
        request.setAttribute("monitorPreMaxLead1", "007:00:60:00.000");
        request.setAttribute("monitorSplitInterval1", "1.0");
        request.setAttribute("windowConstraint", "Yes");
        request.getSession().setAttribute("windowNumRows", Integer.valueOf(1));
        request.setAttribute("windowStartTime0", "2022:000:00:00:00.000");
        request.setAttribute("windowStopTime0", "2022:365:100:00:00.000");
        request.setAttribute("rollConstraint", "Yes");
        request.getSession().setAttribute("rollNumRows", Integer.valueOf(1));
        request.setAttribute("rollRotation0", "Yes");
        request.setAttribute("rollAngle0", "375.000000");
        request.setAttribute("rollTolerance0", "-60.000000");
    }
}
