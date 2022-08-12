//package info;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import info.MPCatRecord;

public class MPCatRecordTest {

    private String expected_default  = "(\n:ao 24\n:observer RCC\n:prop-num 99999999\n:seq-nbr 999999\n:id 99999\n:name \n:type GO\n:ra \n:dec \n:prop-exp-time 0.000000\n:si ACIS-S\n:requested-chip-count 4\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 N\n:ccds4 N\n:ccds5 N\n:uninterrupt Y\n:pointing_constraint N\n:phase-constr-flag N\n:multi-tel-flag N\n:monitor N\n:monitor N\n)\n";
    private String expected_21610537 = "(\n:ao 21\n:observer Bogdan\n:prop-num 21610537\n:seq-nbr 999999\n:id 67515\n:name H1821+643\n:type VLP\n:ra 275.488333\n:dec 64.343389\n:prop-exp-time 175.000000\n:si HRC-S\n:requested-chip-count 0\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 N\n:ccds4 N\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:phase-constr-flag N\n:multi-tel-flag N\n:monitor N\n:monitor N\n)\n";
    private String expected_22700141 = "(\n:ao 22\n:observer Turner\n:prop-num 22700141\n:seq-nbr 999999\n:id 68241\n:name MCG-03-34-64\n:type GO\n:ra 200.602042\n:dec -16.728358\n:prop-exp-time 50.000000\n:si ACIS-S\n:requested-chip-count 4\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 Y\n:ccds2 Y\n:ccds3 Y\n:ccds4 Y\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:phase-constr-flag N\n:multi-tel-flag Y\n:multi-telescopes-interval 364.000000\n:monitor N\n:monitor N\n)\n";
    private String expected_23400045 = "(\n:ao 23\n:observer Hu\n:prop-num 23400045\n:seq-nbr 999999\n:id 70949\n:name CXOU J133001.0+471344\n:type GO\n:ra 202.504208\n:dec 47.228861\n:prop-exp-time 190.000000\n:si ACIS-S\n:requested-chip-count 1\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 Y\n:ccds4 N\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:phase-constr-flag Y\n:phase-epoch 2018:117:00:00:00.000\n:phase-period 038:10:42:29.952\n:phase-start 0.250000\n:phase-start-margin 0.050000\n:phase-end 0.500000\n:phase-end-margin 0.050000\n:phase-unique N\n:multi-tel-flag N\n:monitor N\n:monitor N\n)\n";
    private String expected_22700247 = "(\n:ao 22\n:observer Zoghbi\n:prop-num 22700247\n:seq-nbr 999999\n:id 68909\n:name NGC 5506\n:type GO\n:ra 213.312083\n:dec -3.207561\n:prop-exp-time 200.000000\n:si ACIS-S\n:requested-chip-count 4\n:optional-chip-count 1\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 O1\n:ccds2 Y\n:ccds3 Y\n:ccds4 Y\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:phase-constr-flag N\n:multi-tel-flag N\n:monitor Y\n:pre-min-lead 000:00:00:00.000\n:pre-max-lead 020:00:00:00.000\n:group-duration 200.000000\n:group-id NGC 5506_22700247\n)\n";
    private String expected_22200492 = "(\n:ao 22\n:observer BISWAS\n:prop-num 22200492\n:seq-nbr 999999\n:id 69736\n:name HD47129\n:type GO\n:ra 99.350167\n:dec 6.135383\n:prop-exp-time 60.000000\n:si ACIS-S\n:requested-chip-count 5\n:optional-chip-count 4\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 O1\n:ccds2 O2\n:ccds3 Y\n:ccds4 O3\n:ccds5 O4\n:uninterrupt Y\n:pointing_constraint N\n:phase-constr-flag N\n:multi-tel-flag N\n:monitor N\n:monitor Y\n:pre-min-lead-1 000:00:00:00.000\n:pre-max-lead-1 000:00:00:00.000\n:prop-exp-time-1 10.000000\n:pre-min-lead-2 006:00:00:00.000\n:pre-max-lead-2 007:00:00:00.000\n:prop-exp-time-2 10.000000\n:pre-min-lead-3 006:00:00:00.000\n:pre-max-lead-3 007:00:00:00.000\n:prop-exp-time-3 10.000000\n:pre-min-lead-4 014:00:00:00.000\n:pre-max-lead-4 015:00:00:00.000\n:prop-exp-time-4 10.000000\n:pre-min-lead-5 006:00:00:00.000\n:pre-max-lead-5 007:00:00:00.000\n:prop-exp-time-5 10.000000\n:pre-min-lead-6 006:00:00:00.000\n:pre-max-lead-6 007:00:00:00.000\n:prop-exp-time-6 10.000000\n)\n";
    private String expected_22700328 = "(\n:ao 22\n:observer Tremblay\n:prop-num 22700328\n:seq-nbr 999999\n:id 69135\n:name HE 1353-1917\n:type GO\n:ra 209.152958\n:dec -19.529128\n:prop-exp-time 200.000000\n:si HRC-I\n:requested-chip-count 0\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 N\n:ccds4 N\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:roll-flag-1 Y\n:roll-180-1 Y\n:roll-1 340.000000\n:roll-tolerance-1 60.000000\n:phase-constr-flag N\n:multi-tel-flag N\n:monitor N\n:monitor N\n)\n";
    private String expected_22400495 = "(\n:ao 22\n:observer Wijnands\n:prop-num 22400495\n:seq-nbr 999999\n:id 69753\n:name Quiescent source\n:type TOO\n:ra 0.000000\n:dec 0.000000\n:prop-exp-time 150.000000\n:si ACIS-S\n:requested-chip-count 1\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 Y\n:ccds4 N\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:window-constr-flag-1 Y\n:window-constr-start-1 2022:001:00:00:00.000\n:window-constr-stop-1 2022:365:00:00:00.000\n:window-constr-flag-2 Y\n:window-constr-start-2 2023:060:00:00:00.000\n:window-constr-stop-2 2023:365:00:00:00.000\n:phase-constr-flag N\n:multi-tel-flag N\n:monitor N\n:monitor N\n)\n";
    private String expected_w_split  = "(\n:ao 21\n:observer Bogdan\n:prop-num 21610537\n:seq-nbr 999999\n:id 67515\n:name H1821+643\n:type VLP\n:ra 275.488333\n:dec 64.343389\n:prop-exp-time 175.000000\n:si HRC-S\n:requested-chip-count 0\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 N\n:ccds4 N\n:ccds5 N\n:uninterrupt N\n:pointing_constraint N\n:phase-constr-flag N\n:multi-tel-flag N\n:monitor N\n:monitor N\n:split-interval 365.0\n)\n";

    private MPCatRecord  record;

    @Before
    public void setUp(){
        record = new MPCatRecord();
    }

    @After
    public void tearDown(){
        record = null;
    }

    @Test(expected=RuntimeException.class)
    public void test_setPosition_badFrame() {
        try{
            record.setPosition( 42.3601, 71.0589, "Geographic" );
        }catch( RuntimeException ex ){
            assertEquals( "Unsupported coordinate frame: Geographic", ex.getMessage() );
            throw ex;
        }
        fail();
    }

    @Test
    public void test_setPosition_sexagesimal() {
        // Using format loaded by the name resolver..
        String longitude = "10 45 03.55";
        String latitude  = "-59 41 03.97";
        String expected_lon = ":ra 161.264792";
        String expected_lat = ":dec -59.684436";
        String result;

        record.setPosition( longitude, latitude, "Equatorial" );
        result = record.toString();
        assertEquals( expected_lon, result.split("\n")[8]);
        assertEquals( expected_lat, result.split("\n")[9]);
    }

    @Test(expected=RuntimeException.class)
    public void test_setPosition_badCoords() {
        String longitude = "10-45-03.55";
        String latitude  = "-59-41-03.97";
        try{
            record.setPosition( longitude, latitude, "Equatorial" );
        }catch( RuntimeException ex ){
            assertEquals( "invalid format for longitude or latitude.", ex.getMessage() );
            throw ex;
        }
        fail();
    }

    @Test
    public void test_toString_default() {
        String result;

        result = record.toString();

        // verification
        assertEquals( expected_default, result );
    }

    @Test
    public void test_toString_21610537() {
        String result;

        this.init_case_21610537();
        result = record.toString();

        // verification
        assertEquals( expected_21610537, result );
    }

    @Test
    public void test_toString_23400045() {
        String result;

        // Phase epoch in AT1 format
        init_case_23400045();
        result = record.toString();

        // verification
        assertEquals( expected_23400045, result );

        // Phase epoch in MJD format
        init_case_23400045();
        record.setPhaseConstraint("58235.0000000",
                                  "038:10:42:29.952",
                                  0.250000,
                                  0.050000,
                                  0.500000,
                                  0.050000,
                                  false);
        result = record.toString();

        // verification
        assertEquals( expected_23400045, result );

    }

    @Test
    public void test_toString_22700141() {
        String result;

        this.init_case_22700141();
        result = record.toString();

        // verification
        assertEquals( expected_22700141, result );
    }

    @Test
    public void test_toString_22700247() {
        String result;

        this.init_case_22700247();
        result = record.toString();

        // verification
        assertEquals( expected_22700247, result );

        // check Group constraint Lead inputs as decimal days
        record.setGroupConstraint("NGC 5506_22700247",
                                  200.0,
                                  "0.000",
                                  "20.000");
        result = record.toString();

        // verification
        assertEquals( expected_22700247, result );
    }

    @Test
    public void test_toString_22200492() {
        String result;

        this.init_case_22200492();
        result = record.toString();

        // verification
        assertEquals( expected_22200492, result );
    }

    @Test
    public void test_toString_22700328() {
        String result;

        this.init_case_22700328();
        result = record.toString();

        // verification
        assertEquals( expected_22700328, result );
    }

    @Test
    public void test_toString_22400495() {
        String result;

        this.init_case_22400495();
        result = record.toString();

        // verification
        assertEquals( expected_22400495, result );
    }

    @Test
    public void test_toString_22400495_b() {
        String result;

        this.init_case_22400495_b();
        result = record.toString();

        // verification
        assertEquals( expected_22400495, result );
    }


    @Test
    public void test_toString_w_split() {
        String result;

        this.init_case_21610537();
        record.setSplitInterval("365");

        result = record.toString();

        // verification
        assertEquals( expected_w_split, result );
    }

    // TODO: with ECLIPTIC position

    /* ---------------------------------------------------------------------- */
    /* Private methods                                                        */
    /* ---------------------------------------------------------------------- */
    private void init_case_21610537(){
        /* Content extracted from RCC test file 21610537.txt */
        /*   - basic example with no constraints.            */
        record.setCycle( 21 );
        record.setObserver( "Bogdan" );
        record.setProposalNumber( 21610537 );
        record.setSequenceNumber( 999999 );
        record.setObsID( 67515 );
        record.setTarget( "H1821+643" );
        record.setObsType( "VLP" );
        record.setPosition( 275.488333, 64.343389, "Equatorial" );
        record.setExposureTime( 175.000000 );
        record.setInstrument( "HRC-S" );
        record.setChipCount( 0, 0 );
        record.setContinuous( false );
        record.setPointingConstraint( false );
    }
    private void init_case_23400045(){
        String[] chips = new String[10];
        Arrays.fill( chips, "N" );

        /* Content extracted from RCC test file 21610537.txt */
        /*   - with phase constraint                         */
        record.setCycle( "23" );
        record.setObserver( "Hu" );
        record.setProposalNumber( 23400045 );
        record.setSequenceNumber( 999999 );
        record.setObsID( 70949 );
        record.setTarget( "CXOU J133001.0+471344" );
        record.setObsType( "GO" );
        record.setPosition( 202.504208, 47.228861, "Equatorial" );
        record.setExposureTime( 190.000000 );
        record.setInstrument( "ACIS-S" );
        chips[7] = "Y";
        record.setChipSelection( chips );
        record.setContinuous( false );
        record.setPointingConstraint( false );
        record.setPhaseConstraint("2018:117:00:00:00.000",
                                  "038:10:42:29.952",
                                  0.250000,
                                  0.050000,
                                  0.500000,
                                  0.050000,
                                  false);
    }
    private void init_case_22700141(){
        String[] chips = new String[10];
        Arrays.fill( chips, "N" );

        /* Content extracted from RCC test file 22700141.txt */
        /*   - with multi-telescope selected                 */
        record.setCycle( "22" );
        record.setObserver( "Turner" );
        record.setProposalNumber( 22700141 );
        record.setSequenceNumber( 999999 );
        record.setObsID( 68241 );
        record.setTarget( "MCG-03-34-64" );
        record.setObsType( "GO" );
        record.setPosition( 200.602042, -16.728358, "Equatorial" );
        record.setExposureTime( 50.000000 );
        record.setInstrument( "ACIS-S" );
        chips[5] = "Y";
        chips[6] = "Y";
        chips[7] = "Y";
        chips[8] = "Y";
        record.setChipSelection( chips );
        record.setContinuous( false );
        record.setPointingConstraint( false );
        record.setMultiTelescope(364.000000);
    }
    private void init_case_22700247(){
        String[] chips = new String[10];
        Arrays.fill( chips, "N" );

        /* Content extracted from RCC test file 22700247.txt */
        /*   - with group constraint                         */
        record.setCycle( "22" );
        record.setObserver( "Zoghbi" );
        record.setProposalNumber( 22700247 );
        record.setSequenceNumber( 999999 );
        record.setObsID( 68909 );
        record.setTarget( "NGC 5506" );
        record.setObsType( "GO" );
        record.setPosition( 213.312083, -3.207561, "Equatorial" );
        record.setExposureTime( 200.000000 );
        record.setInstrument( "ACIS-S" );
        chips[5] = "O1";
        chips[6] = "Y";
        chips[7] = "Y";
        chips[8] = "Y";
        record.setChipSelection( chips );
        record.setContinuous( false );
        record.setPointingConstraint( false );
        record.setGroupConstraint("NGC 5506_22700247",
                                  200.0,
                                  "000:00:00:00.000",
                                  "020:00:00:00.000");
    }
    private void init_case_22200492(){
        String[] chips = new String[10];
        Arrays.fill( chips, "N" );

        /* Content extracted from RCC test file 22200492.txt */
        /*   - with monitor constraints                      */
        record.setCycle( "22" );
        record.setObserver( "BISWAS" );
        record.setProposalNumber( 22200492 );
        record.setSequenceNumber( 999999 );
        record.setObsID( 69736 );
        record.setTarget( "HD47129" );
        record.setObsType( "GO" );
        record.setPosition( 99.350167, 6.135383, "Equatorial" );
        record.setExposureTime( 60.000000 );
        record.setInstrument( "ACIS-S" );
        chips[5] = "O1";
        chips[6] = "O2";
        chips[7] = "Y";
        chips[8] = "O3";
        chips[9] = "O4";
        record.setChipSelection( chips );
        record.setContinuous( true );
        record.setPointingConstraint( false );
        record.addMonitorConstraint( "000:00:00:00.000", "000:00:00:00.000", 10. );
        record.addMonitorConstraint( "006:00:00:00.000", "007:00:00:00.000", 10. );
        record.addMonitorConstraint( "006:00:00:00.000", "007:00:00:00.000", 10. );
        record.addMonitorConstraint( "014:00:00:00.000", "015:00:00:00.000", 10. );
        record.addMonitorConstraint( "6.000", "7.000", 10. );
        record.addMonitorConstraint( "006:00:00:00.000", "007:00:00:00.000", 10. );
    }
    private void init_case_22700328(){
        String[] chips = new String[10];
        Arrays.fill( chips, "N" );

        /* Content extracted from RCC test file 22700328.txt */
        /*   - with roll constraints                         */
        record.setCycle( "22" );
        record.setObserver( "Tremblay" );
        record.setProposalNumber( 22700328 );
        record.setSequenceNumber( 999999 );
        record.setObsID( 69135 );
        record.setTarget( "HE 1353-1917" );
        record.setObsType( "GO" );
        record.setPosition( 209.152958, -19.529128, "Equatorial" );
        record.setExposureTime( 200.000000 );
        record.setInstrument( "HRC-I" );
        record.setChipSelection( chips );
        record.setContinuous( false );
        record.setPointingConstraint( false );
        record.addRollConstraint( true, true, 340.000000, 60.000000 );
    }
    private void init_case_22400495(){
        String[] chips = new String[10];
        Arrays.fill( chips, "N" );

        /* Content extracted from RCC test file 22400495.txt */
        /*   - with window constraints                       */
        record.setCycle( "22" );
        record.setObserver( "Wijnands" );
        record.setProposalNumber( 22400495 );
        record.setSequenceNumber( 999999 );
        record.setObsID( 69753 );
        record.setTarget( "Quiescent source" );
        record.setObsType( "TOO" );
        record.setPosition( 0.000000, 0.000000, "Equatorial" );
        record.setExposureTime( 150.000000 );
        record.setInstrument( "ACIS-S" );
        chips[7] = "Y";
        record.setChipSelection( chips );
        record.setContinuous( false );
        record.setPointingConstraint( false );
        record.addWindowConstraint( true, "2022:001:00:00:00.000", "2022:365:00:00:00.000" );
        record.addWindowConstraint( true, "2023:060:00:00:00.000", "2023:365:00:00:00.000" );
    }
    private void init_case_22400495_b(){
        String[] chips = new String[10];
        Arrays.fill( chips, "N" );

        /* Content extracted from RCC test file 22400495.txt */
        /*   - with window constraints                       */
        record.setCycle( "22" );
        record.setObserver( "Wijnands" );
        record.setProposalNumber( 22400495 );
        record.setSequenceNumber( 999999 );
        record.setObsID( 69753 );
        record.setTarget( "Quiescent source" );
        record.setObsType( "TOO" );
        record.setPosition( 0.000000, 0.000000, "Equatorial" );
        record.setExposureTime( 150.000000 );
        record.setInstrument( "ACIS-S" );
        chips[7] = "Y";
        record.setChipSelection( chips );
        record.setContinuous( false );
        record.setPointingConstraint( false );
        record.addWindowConstraint( true, "2022-01-01T00:00", "2022-12-31T00:00" );
        record.addWindowConstraint( true, "2023-03-01T00:00:00.000", "2023-12-31T00:00:00.000" );
    }

}
