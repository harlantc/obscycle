//package <path to package being tested>

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import info.Toolkit;
import info.MPCat;

public class RCCalcTest {
    private RCCalc tool;
    private HttpServletRequest request;

    private String rcc_path = "/data/mpcritrc/bin";
    private String test_rcc_path = "/data/mpcrit4/bin";

    private String[] baselines = { "",
                                  "(\n:ao 24\n:observer RCC\n:prop-num 99999999\n:seq-nbr 999999\n:id 99999\n:name \n:type GO\n:ra 275.488333\n:dec 64.343389\n:prop-exp-time 175.000000\n:si HRC-S\n:requested-chip-count 0\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 N\n:ccds4 N\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:phase-constr-flag N\n:multi-tel-flag N\n:monitor N\n:monitor N\n)\n",
                                  "(\n:ao 24\n:observer RCC\n:prop-num 99999999\n:seq-nbr 999999\n:id 99999\n:name \n:type GO\n:ra 275.488333\n:dec 64.343389\n:prop-exp-time 175.000000\n:si HRC-S\n:requested-chip-count 0\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 N\n:ccds4 N\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:phase-constr-flag N\n:multi-tel-flag Y\n:multi-telescopes-interval 364.000000\n:monitor N\n:monitor N\n)\n",
                                  "(\n:ao 24\n:observer RCC\n:prop-num 99999999\n:seq-nbr 999999\n:id 99999\n:name \n:type GO\n:ra 275.488333\n:dec 64.343389\n:prop-exp-time 175.000000\n:si HRC-S\n:requested-chip-count 0\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 N\n:ccds4 N\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:phase-constr-flag Y\n:phase-epoch 2018:117:00:00:00.000\n:phase-period 038:10:42:29.952\n:phase-start 0.250000\n:phase-start-margin 0.050000\n:phase-end 0.500000\n:phase-end-margin 0.050000\n:phase-unique N\n:multi-tel-flag N\n:monitor N\n:monitor N\n)\n",
                                  "(\n:ao 24\n:observer RCC\n:prop-num 99999999\n:seq-nbr 999999\n:id 99999\n:name \n:type GO\n:ra 275.488333\n:dec 64.343389\n:prop-exp-time 175.000000\n:si HRC-S\n:requested-chip-count 0\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 N\n:ccds4 N\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:phase-constr-flag N\n:multi-tel-flag N\n:monitor Y\n:pre-min-lead 000:00:00:00.000\n:pre-max-lead 020:00:00:00.000\n:group-id NGC 5506_22700247\n)\n",
                                  "(\n:ao 24\n:observer RCC\n:prop-num 99999999\n:seq-nbr 999999\n:id 99999\n:name \n:type GO\n:ra 275.488333\n:dec 64.343389\n:prop-exp-time 175.000000\n:si HRC-S\n:requested-chip-count 0\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 N\n:ccds4 N\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:phase-constr-flag N\n:multi-tel-flag N\n:monitor N\n:monitor Y\n:pre-min-lead-1 000:00:00:00.000\n:pre-max-lead-1 000:00:00:00.000\n:prop-exp-time-1 100.000000\n:split-interval-1 5.0\n:pre-min-lead-2 000:00:00:00.000\n:pre-max-lead-2 002:00:00:00.000\n:prop-exp-time-2 75.000000\n:split-interval-2 1.0\n)\n",
                                  "(\n:ao 24\n:observer RCC\n:prop-num 99999999\n:seq-nbr 999999\n:id 99999\n:name \n:type GO\n:ra 275.488333\n:dec 64.343389\n:prop-exp-time 175.000000\n:si HRC-S\n:requested-chip-count 0\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 N\n:ccds4 N\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:window-constr-flag-1 Y\n:window-constr-start-1 2023:001:00:00:00.000\n:window-constr-stop-1 2023:365:00:00:00.000\n:phase-constr-flag N\n:multi-tel-flag N\n:monitor N\n:monitor N\n)\n",
                                  "(\n:ao 24\n:observer RCC\n:prop-num 99999999\n:seq-nbr 999999\n:id 99999\n:name \n:type GO\n:ra 275.488333\n:dec 64.343389\n:prop-exp-time 175.000000\n:si HRC-S\n:requested-chip-count 0\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 N\n:ccds4 N\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:roll-flag-1 Y\n:roll-180-1 Y\n:roll-1 340.000000\n:roll-tolerance-1 60.000000\n:phase-constr-flag N\n:multi-tel-flag N\n:monitor N\n:monitor N\n)\n",
                                  "(\n:ao 24\n:observer RCC\n:prop-num 99999999\n:seq-nbr 999999\n:id 99999\n:name \n:type GO\n:ra 275.488333\n:dec 64.343389\n:prop-exp-time 175.000000\n:si HRC-S\n:requested-chip-count 0\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 N\n:ccds4 N\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:phase-constr-flag N\n:multi-tel-flag N\n:monitor Y\n:pre-min-lead 000:00:00:00.000\n:pre-max-lead 020:00:00:00.000\n:group-id group_0000\n)\n",
                                  "(\n:ao 24\n:observer RCC\n:prop-num 99999999\n:seq-nbr 999999\n:id 99999\n:name \n:type GO\n:ra 275.488333\n:dec 64.343389\n:prop-exp-time 175.000000\n:si HRC-S\n:requested-chip-count 0\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 N\n:ccds4 N\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:phase-constr-flag N\n:multi-tel-flag N\n:monitor N\n:monitor N\n:split-interval 16.0\n)\n"};
    private String[] baseresponses = { "",
                                       "Resource Score assessment completed for 1 targets!\nObsID | Targname             | RA     | Dec    | Exposure | R | W | P | U | C | M | G | S | X | Normalized_N  | Normalized_N1 | Normalized_N2 | ELon   | ELat   \n99999 |                      | 275.49 |  64.34 |  175.0   | N | N | N | N | N | N | N | N | N |   498.86      |     0.00      |     0.00      | 318.32 |  86.82 \n"};

    @Before
    public void setUp(){
        tool  = new RCCalc();
        request = new MockRequest();
    }

    @After
    public void tearDown(){
        tool  = null;
        request = null;
    }

    @Test
    public void test_storeSession_case1() {
        /* Basic.. no constraints.  */
        this.run_storeSession_test( 1 );
    }
    @Test
    public void test_storeSession_case2() {
        /* with multiTelescope.  */
        this.run_storeSession_test( 2 );
    }
    @Test
    public void test_storeSession_case3() {
        /* with phase constraint.  */
        this.run_storeSession_test( 3 );
    }
    @Test
    public void test_storeSession_case4() {
        /* with group constraint.  */
        this.run_storeSession_test( 4 );
    }
    @Test
    public void test_storeSession_case5() {
        /* with monitor constraint(s).  */
        this.run_storeSession_test( 5 );
    }
    @Test
    public void test_storeSession_case6() {
        /* with window constraint(s).  */
        this.run_storeSession_test( 6 );
    }
    @Test
    public void test_storeSession_case7() {
        /* with roll constraint(s).  */
        this.run_storeSession_test( 7 );
    }
    @Test
    public void test_storeSession_case8() {
        /* with group constraint.  */
        this.run_storeSession_test( 8 );
    }
    @Test
    public void test_storeSession_case9() {
        /* with split constraint.  */
        this.run_storeSession_test( 9 );
    }

    @Test
    public void test_getTempFilename_props(){
        String result;

        // depends on toolkit properties
        try {
            load_properties();
        }catch (Exception ex ){
            fail( ex.getMessage() );
        }

        result = tool.getTempFilename();
        assertTrue( result.startsWith("/tmp/rcctest/mpcat-") );
    }

    @Test
    public void test_getTempFilename_failsafe(){
        String result;

        result = tool.getTempFilename();
        assertTrue( result.startsWith("/tmp/mpcat-") );
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_runRCCUtility_badmode(){
        String result;
 
        try{
            result = tool.runRCCUtility( null, rcc_path, "999" );
        }catch( IllegalArgumentException ex ){
            assertEquals( "Invalid mode.", ex.getMessage() );
            throw ex;
        }
        fail("Expected error did not occur.");
    }
    @Test(expected=RuntimeException.class)
    public void test_runRCCUtility_badpath(){
        String result;

        // setup MPCat with sample data.
        this.init_default();
        tool.storeSession( request );
        MPCat mpcat = (MPCat)request.getSession().getAttribute("warehouse");
 
        try{
            result = tool.runRCCUtility( mpcat, "/blah/dne", "0" );
        }catch( RuntimeException ex ){
            assertEquals( "Error running RCC script: /blah/dne/resource_cost_calculator.pl: Command not found.", ex.getMessage() );
            throw ex;
        }
        fail("Expected error did not occur.");
    }
    @Test
    public void test_runRCCUtility_filemode(){
        String expected = this.baseresponses[1];
        String result;

        // setup MPCat with sample data.
        this.init_default();
        tool.storeSession( request );
        MPCat mpcat = (MPCat)request.getSession().getAttribute("warehouse");
 
        result = tool.runRCCUtility( mpcat, rcc_path, "0" );
        assertEquals( expected, result );

    }
    @Test
    public void test_runRCCUtility_stringmode(){
        String expected = this.baseresponses[1]; // expects same results as file mode.
        String result;

        // setup MPCat with sample data.
        this.init_default();
        tool.storeSession( request );
        MPCat mpcat = (MPCat)request.getSession().getAttribute("warehouse");
 
        result = tool.runRCCUtility( mpcat, rcc_path, "1" );
        assertEquals( expected, result );

    }

    @Test(expected=IllegalArgumentException.class)
    public void test_parseRCCResponse_bad(){
        HashMap<String,ArrayList<String>> result;

        /* Parser given multi-line input not in correct format */
        String response_bad = "Resource Score assessment completed for 1 targets!\nBwah haa haa.. unexpected text here!";

        try{
            result = tool.parseRCCResponse( response_bad );
        }catch( IllegalArgumentException ex ){
            assertEquals( "Error in RCC. Header column missing Normalized_N*.", ex.getMessage() );
            throw ex;
        }
        fail("Expected error did not occur.");
    }
    @Test
    public void test_parseRCCResponse_good(){
        HashMap<String,ArrayList<String>> result;

        String response_empty = "";
        String response_21610537 = "Resource Score assessment completed for 1 targets!\nObsID | Targname             | RA     | Dec    | Exposure | R | W | P | U | C | M | G | S | X | Normalized_N  | Normalized_N1 | Normalized_N2 | ELon   | ELat   \n67515 |            H1821+643 | 275.49 |  64.34 |  175.0   | N | N | N | N | N | N | N | N | N |   469.83      |     0.00      |     0.00      | 318.32 |  86.82 ";
        String response_22200427 = "Resource Score assessment completed for 3 targets!\nObsID | Targname             | RA     | Dec    | Exposure | R | W | P | U | C | M | G | S | X | Normalized_N  | Normalized_N1 | Normalized_N2 | ELon   | ELat   \n69518 |        TIC 402980664 |  16.73 |  80.46 |   22.3   | N | N | N | N | N | N | N | N | N |    55.63      |     0.00      |     0.00      |  70.00 |  62.35 \n69519 |        TIC 402980664 |  16.73 |  80.46 |   22.3   | N | N | Y | N | N | N | N | N | N |    80.73      |     0.00      |     0.00      |  70.00 |  62.35 \n69520 |        TIC 402980664 |  16.73 |  80.46 |   22.3   | N | N | Y | N | N | N | N | N | N |   106.57      |     0.00      |     0.00      |  70.00 |  62.35 \n";

        /* RCC response on error appears to be */
        /*  - nothing to stdout                */
        /*  - error message to stderr          */
        /* Test results of empty string input  */
        try{
            result = tool.parseRCCResponse( response_empty );
            assertEquals( result.get("NRC").size(), 0 );
        }catch( Exception ex ){
            fail(ex.getMessage());
        }

        /* RCC response for single target input */
        try{
            result = tool.parseRCCResponse( response_21610537 );
            assertEquals( 1, result.get("NRC").size() );
            assertEquals( "469.83", result.get("NRC").get(0) );
            assertEquals( "0.00",   result.get("NRC1").get(0));
            assertEquals( "0.00",   result.get("NRC2").get(0));
        }catch( Exception ex ){
            fail();
        }

        /* RCC response for multi-target input */
        try{
            result = tool.parseRCCResponse( response_22200427 );
            assertEquals( 3, result.get("NRC").size() );
            assertEquals( "55.63", result.get("NRC").get(0) );
            assertEquals( "80.73", result.get("NRC").get(1) );
            assertEquals( "106.57", result.get("NRC").get(2) );
            assertEquals( "0.00", result.get("NRC1").get(0) );
            assertEquals( "0.00", result.get("NRC1").get(1) );
            assertEquals( "0.00", result.get("NRC1").get(2) );
            assertEquals( "0.00", result.get("NRC2").get(0) );
            assertEquals( "0.00", result.get("NRC2").get(1) );
            assertEquals( "0.00", result.get("NRC2").get(2) );
        }catch( Exception ex ){
            fail();
        }
    }

    @Test
    public void test_validateSession_default(){
        boolean result;

        init_validate_default();
 
        result = tool.validateSessionInParams( request );
        assertFalse( result );
    }

    @Test
    public void test_validateSession_bad(){
        boolean result;
        String expected;
        expected  = "<font color=\"red\"><pre>Errors detected!  Please correct the following list and try again:\n";
        expected += "DEC or B2 or EB input: invalid format\n";
        expected += "Exposure Time: out of range\n";
        expected += "Split Interval: out of range\n";
        expected += "Coordination Window: out of range\n";
        expected += "Chip Count: out of range\n";
        expected += "Phase Start: out of range\n";
        expected += "Group Max. Interval: MM out of range\n";
        expected += "Monitor Exposure Time 1: value must be numeric\n";
        expected += "Monitor Split Interval 1: value must be numeric\n";
        expected += "Monitor Exposure Time: total exposure times must equal proposal exposure time.\n";
        expected += "Window Start 1: invalid date\n";
        expected += "Roll Tolerance 1: out of range\n";
        expected += "</pre></font>";
    
        init_validate_bad();
        result = tool.validateSessionInParams( request );
    
        // verify overall result
        assertTrue( result );
    
        // verify composite error message in session
        String errmsg = (String)request.getSession().getAttribute("errorScript");
        assertEquals( expected, errmsg );
    }

    /* ---------------------------------------------------------------------- */
    /* Private methods                                                        */
    /* ---------------------------------------------------------------------- */
    private void run_storeSession_test(int ii) {
        String expected = baselines[ii];

        /* setup resource with default data */
        this.init_default();

        /* add case data to resource */
        this.init_case( ii );

        /* call method */
        tool.storeSession( request );

        /* verification */
        MPCat mpcat = (MPCat)request.getSession().getAttribute("warehouse");
        String result = mpcat.toString();
        assertEquals( expected, result );

        /* clear MPCat for next case. */
        mpcat.clear();
    }

    private void load_properties()
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

            // Store properties in tool
            tool.setToolkitProperties( props );
        }
        catch ( IOException ex ){
            throw new IOException( "Problem setting up properties." + ex.getMessage());
        }

    }
    private void init_case( int count ){

        switch (count){
        case 2:
            /* with multiTelescope.  */
            request.setAttribute("coordinatedObs", "Yes");
            request.setAttribute("obsInterval", "364.000000");
            break;
        case 3:
            /* with phase constraint.  */
            request.setAttribute("phaseConstraint", "Yes");
            request.setAttribute("phaseEpoch",  "2018:117:00:00:00.000");
            request.setAttribute("phasePeriod", "038:10:42:29.952");
            request.setAttribute("phaseStart",  "0.250000");
            request.setAttribute("phaseStartMargin", "0.050000");
            request.setAttribute("phaseStop", "0.500000");
            request.setAttribute("phaseStopMargin", "0.050000");
            request.setAttribute("phaseUnique", "No");
            break;
        case 4:
            /* with group constraint.  */
            request.setAttribute("groupConstraint", "Yes");
            request.setAttribute("groupID", "NGC 5506_22700247");
            request.setAttribute("groupPreMinLead", "000:00:00:00.000");
            request.setAttribute("groupPreMaxLead", "020:00:00:00.000");
            break;
        case 5:
            /* with monitor constraint (multi-row).  */
            // -  In practice, NumRows is an Integer available only in the session.
            request.setAttribute("monitorConstraint", "Yes");
            request.getSession().setAttribute("monitorNumRows", Integer.valueOf(2));
            request.setAttribute("monitorExpTime0", "100.0");
            request.setAttribute("monitorPreMinLead0", "000:00:00:00.000");
            request.setAttribute("monitorPreMaxLead0", "000:00:00:00.000");
            request.setAttribute("monitorSplitInterval0", "5");
            request.setAttribute("monitorExpTime1", "75.0");
            request.setAttribute("monitorPreMinLead1", "000:00:00:00.000");
            request.setAttribute("monitorPreMaxLead1", "002:00:00:00.000");
            request.setAttribute("monitorSplitInterval1", "1");
            break;
        case 6:
            /* with window constraint (multi-row).  */
            // -  In practice, NumRows is an Integer available only in the session.
            request.setAttribute("windowConstraint", "Yes");
            request.getSession().setAttribute("windowNumRows", Integer.valueOf(1));
            request.setAttribute("windowStartTime0", "2023-01-01T00:00:00.000");
            request.setAttribute("windowStopTime0", "2023-12-31T00:00:00.000");
            break;
        case 7:
            /* with roll constraint (multi-row).  */
            // -  In practice, NumRows is an Integer available only in the session.
            request.setAttribute("rollConstraint", "Yes");
            request.getSession().setAttribute("rollNumRows", Integer.valueOf(1));
            request.setAttribute("rollRotation0", "Yes");
            request.setAttribute("rollAngle0", "340.000000");
            request.setAttribute("rollTolerance0", "60.000000");
            break;
        case 8:
            /* with group constraint  */
            request.setAttribute("groupConstraint", "Yes");
            request.setAttribute("groupPreMaxLead", "020:00:00:00.000");
            break;
        case 9:
            /* with split constraint  */
            request.setAttribute("splitConstraint", "Yes");
            request.setAttribute("splitInterval", "16");
            break;
        default:
            break;
        }
        
    }
    private void init_default(){
        /* Basic example with no constraints.  */
        request.setAttribute("equinox", "J2000" );
        request.setAttribute("resolverSelector", "SIMBAD/NED" );
        request.setAttribute("inputPosition1", "275.488333" );
        request.setAttribute("inputPosition2", "64.343389" );
        request.setAttribute("inputCoordinateSelector", "Equatorial (J2000)" );
        request.setAttribute("targetName", "" );
        request.setAttribute("propExposureTime", "175.000000" );
        request.setAttribute("instrument", "HRC-S" );
        request.setAttribute("requiredChipCount", "0" );
        request.setAttribute("uninterrupted", "No");
        request.setAttribute("splitConstraint", "No");
        request.setAttribute("splitInterval", "");
        request.setAttribute("coordinatedObs", "No");
        request.setAttribute("obsInterval", "");
        request.setAttribute("phaseConstraint", "No");
        request.setAttribute("phaseEpoch", "");
        request.setAttribute("phasePeriod", "");
        request.setAttribute("phaseStart", "0.0");
        request.setAttribute("phaseStartMargin", "0.0");
        request.setAttribute("phaseStop", "0.0");
        request.setAttribute("phaseStopMargin", "0.0");
        request.setAttribute("phaseUnique", "No");
        request.setAttribute("groupConstraint", "No");
        request.setAttribute("groupPreMaxLead", "");
        request.setAttribute("monitorConstraint", "No");
        request.setAttribute("monitorOperation", "");
        request.setAttribute("monitorExpTime", "");
        request.setAttribute("monitorPreMinLead", "");
        request.setAttribute("monitorPreMaxLead", "");
        request.setAttribute("monitorSplitInterval", "");
        request.setAttribute("windowConstraint", "No");
        request.setAttribute("windowConstraintTable", "");
        request.setAttribute("windowStartTime", "");
        request.setAttribute("windowStopTime", "");
        request.setAttribute("rollConstraint", "No");
        request.setAttribute("rollConstraintTable", "");
        request.setAttribute("rollRotation", "");
        request.setAttribute("rollAngle", "");
        request.setAttribute("rollTolerance", "");
        request.setAttribute("pointingConstraint", "No");
        request.getSession().setAttribute("rollNumRows", Integer.valueOf(0));
        request.getSession().setAttribute("windowNumRows", Integer.valueOf(0));
        request.getSession().setAttribute("monitorNumRows", Integer.valueOf(0));

    }

    private void init_validate_default(){
        // depends on toolkit properties
        try {
            load_properties();
        }catch (Exception ex ){
            fail( ex.getMessage() );
        }

        // setup request with valid data.
        this.init_default();

        // add all constraints.
        for (int ii=2; ii<=8; ii++ ){
            this.init_case(ii);
        }

    }
    private void init_validate_bad(){

        // start with good setup
        init_validate_default();

        // replace select elements with bad field content
        request.setAttribute("inputPosition2", "BadValue");
        request.setAttribute("propExposureTime", "99999");
        request.setAttribute("splitConstraint", "Yes");
        request.setAttribute("splitInterval", "-100.0");
        request.setAttribute("coordinatedObs", "Yes");
        request.setAttribute("obsInterval", "-100.0");
        request.setAttribute("instrument", "ACIS-I");
        request.setAttribute("requiredChipCount", "7");
        request.setAttribute("phaseStart", "100");
        request.setAttribute("rollTolerance0", "-60.000000");
        request.setAttribute("windowStartTime0", "2022:000:00:00:00.000");
        request.setAttribute("monitorExpTime0", "None");
        request.setAttribute("monitorSplitInterval0", "BadValue");
        request.setAttribute("groupConstraint", "Yes");
        request.setAttribute("groupPreMaxLead", "020:00:99:00.000");

    }
}
