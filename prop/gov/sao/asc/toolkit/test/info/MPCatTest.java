//package info;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import info.MPCat;
import info.MPCatRecord;

public class MPCatTest {

    private String expected_basic = "(\n:ao 24\n:observer RCC\n:prop-num 99999999\n:seq-nbr 999999\n:id 99999\n:name \n:type GO\n:ra \n:dec \n:prop-exp-time 0.000000\n:si ACIS-S\n:requested-chip-count 4\n:optional-chip-count 0\n:ccdi0 N\n:ccdi1 N\n:ccdi2 N\n:ccdi3 N\n:ccds0 N\n:ccds1 N\n:ccds2 N\n:ccds3 N\n:ccds4 N\n:ccds5 N\n:uninterrupt Y\n:pointing_constraint N\n:phase-constr-flag N\n:multi-tel-flag N\n:monitor N\n:monitor N\n)\n";
    private MPCat  mpcat;

    @Before
    public void setUp(){
	mpcat = new MPCat();
    }

    @After
    public void tearDown(){
    }

    @Test
    public void test_clear(){

	/* execute on empty stack */
	mpcat.clear();
	assertEquals( 0, mpcat.getNumRecords() );

	/* add records to stack */
	mpcat.addRecord( new MPCatRecord() );
	mpcat.addRecord( new MPCatRecord() );
	mpcat.addRecord( new MPCatRecord() );
	mpcat.addRecord( new MPCatRecord() );
	assertEquals( 4, mpcat.getNumRecords() );

	/* clear the stack */
	mpcat.clear();
	assertEquals( 0, mpcat.getNumRecords() );
    }

    @Test
    public void test_getNumRecords() {
	MPCatRecord record;
	int result;

	/* before adding any records */
	result = mpcat.getNumRecords();
	assertEquals( 0, result );

	/* add records */
	record = new MPCatRecord();
	mpcat.addRecord( record );
	result = mpcat.getNumRecords();
	assertEquals( 1, result );

	record = new MPCatRecord();
	mpcat.addRecord( record );
	result = mpcat.getNumRecords();
	assertEquals( 2, result );

	/* remove records */
	//   - not implemented 
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void test_getRecord_empty() throws Exception {
	MPCatRecord result;
	try{
	    result = mpcat.getRecord(0);
	}catch( IndexOutOfBoundsException ex ){
	    assertEquals( "Index: 0, Size: 0", ex.getMessage() );
	    throw ex;
	}
	fail();
    }

    @Test(expected=IndexOutOfBoundsException.class)
    public void test_getRecord_bounds() throws Exception {
	MPCatRecord result;
	mpcat.addRecord( new MPCatRecord() );
	mpcat.addRecord( new MPCatRecord() );
	mpcat.addRecord( new MPCatRecord() );
	try{
	    result = mpcat.getRecord( 5 );
	}catch( IndexOutOfBoundsException ex ){
	    assertEquals( "Index: 5, Size: 3", ex.getMessage() );
	    throw ex;
	}
	fail();
    }

    @Test
    public void test_getRecord() {
	MPCatRecord record1 = new MPCatRecord();
	MPCatRecord record2 = new MPCatRecord();
	MPCatRecord record3 = new MPCatRecord();
	MPCatRecord result;

	/* add records */
	mpcat.addRecord( record1 );
	mpcat.addRecord( record2 );
	mpcat.addRecord( record3 );

	/* pull records and verify */
	result = mpcat.getRecord(0);
	assertEquals( record1, result );

	result = mpcat.getRecord(2);
	assertEquals( record3, result );

	/* remove records */
	//   - not implemented 
    }

    @Test(expected=NullPointerException.class)
    public void test_toString_empty() throws Exception {
	String result;
	try{
	    result = mpcat.toString();
	}catch( NullPointerException ex ){
	    assertEquals( "contains no records.", ex.getMessage() );
	    throw ex;
	}
	fail();
    }

    @Test
    public void test_toString_single() {
	MPCatRecord record;
	String result;

	record = new MPCatRecord();
	mpcat.addRecord( record );
	result = mpcat.toString();

	// verification
	assertEquals( expected_basic, result );
    }

    @Test
    public void test_toString_multi() {
	MPCatRecord record;
	String result;
	String expected = expected_basic + expected_basic;

	record = new MPCatRecord();
	mpcat.addRecord( record );
	mpcat.addRecord( record );
	result = mpcat.toString();

	// verification
	assertEquals( expected, result );
    }

    @Test(expected=NullPointerException.class)
    public void test_toFile_empty() throws IOException {
	String filename;

	/* create a temporary filename.. better method? */
	File fh = File.createTempFile("mpcat-",".txt");
	filename = fh.getAbsolutePath();
	fh.delete();

	/* write empty mpcat to file  */
	try{
	    mpcat.toFile( filename );
	}
	catch (NullPointerException ex){
	    assertEquals( "MPCat is empty, add records before writing.", ex.getMessage() );
	    throw ex;
	}
	fail();
    }

    @Test
    public void test_toFile_basic() throws IOException {
	String filename;
	MPCatRecord record = new MPCatRecord();
	this.init_case_21610537( record );
	mpcat.addRecord( record );

	/* create a temporary filename.. better method? */
	File fh = File.createTempFile("mpcat-",".txt");
	filename = fh.getAbsolutePath();
	fh.delete();

	/* write to file */
	mpcat.toFile( filename );

	//Compare with baseline in resources.
	String basename = getClass().getClassLoader().getResource("21610537.txt").getFile();
	String expected = get_file_content( basename );
	String result   = get_file_content( filename );
	assertEquals( expected, result );

	// Delete temp file.
	fh = new File( filename );
	fh.delete();
    }

    /* ---------------------------------------------------------------------- */
    /* Private methods                                                        */
    /* ---------------------------------------------------------------------- */
    private String get_file_content( String filename ) throws IOException {
	File fh = new File( filename );
	FileInputStream fis = new FileInputStream( fh );
	try( BufferedReader br = new BufferedReader( new InputStreamReader( fis, "UTF-8") ) )
	{
	    StringBuilder sb = new StringBuilder();
	    String line;
	    while (( line = br.readLine() ) != null ){
		sb.append( line );
		sb.append( "\n" );
	    }
	    return( sb.toString() );
	}
    }
    private void init_case_21610537( MPCatRecord record ){
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

}
