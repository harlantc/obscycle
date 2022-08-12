package info;

/* imports */
import java.util.List;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

import info.MPCatRecord;

/**
 * Class to store and manage MPCat records.
 *  - primary usage is Resource Cost Calculator Tool (RCCalc)
 */
public class MPCat
{
    private List<MPCatRecord> data = new ArrayList<MPCatRecord>();

    public MPCat(){
    }

    /**
     * Add MPCatRecord to stack.
     *
     * @param entry  Instance to add to stack.
     */
    public void addRecord( MPCatRecord entry ){
	data.add(entry);
    }

    /**
     * Removes all the records from the stack.
     *
     */
    public void clear(){
	data.clear();
    }

    /**
     * Get MPCatRecord from stack.
     *
     * @param index  Index of record to extract (0-based)
     * 
     * @return MPCatRecord  Instant to add to stack.
     */
    public MPCatRecord getRecord( Integer index ){
	return data.get(index);
    }

    /**
     * Get number of MPCat records in stack.
     *
     * @return int Number of entries in stack.
     */
    public int getNumRecords(){
	return this.data.size();
    }

    /**
     * Write MPCatRecord stack to file.
     *
     * @param filename  File name.
     *
     * @throws FileNotFoundException if unable to find/create specified file.
     */
    public void toFile( String filename ) throws FileNotFoundException {
	String content;
	try{
	    content = this.toString();
	}catch( NullPointerException ex ){
	    throw new NullPointerException("MPCat is empty, add records before writing.");
	}

	PrintWriter writer = new PrintWriter( filename );
	writer.write( content );
	writer.close();
    }

    /**
     * Get String representation of records.
     *
     * @return String  Formatted string representation of contained records.
     *
     * @throws NullPointerException if no MPCat contains no records.
     */
    public String toString(){
	StringBuilder sb;

	if ( data.isEmpty() ){
	    throw new NullPointerException("contains no records.");
	}

	sb = new StringBuilder();
	for ( MPCatRecord entry : data ){
	    sb.append(entry.toString());
	}
	
	return sb.toString();
    }
}

