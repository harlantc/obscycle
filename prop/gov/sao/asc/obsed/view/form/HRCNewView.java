/*
  Copyrights:
 
  Copyright (c) 1998 Smithsonian Astrophysical Observatory
 
  Permission to use, copy, modify, distribute, and sell this software
  and its documentation for any purpose is hereby granted without fee,
  provided that the above copyright notice appear in all copies and
  that both that copyright notice and this permission notice appear in
  supporting docu- mentation, and that the name of the Smithsonian
  Astro- physical Observatory not be used in advertising or publicity
  pertaining to distribution of the software without specific, written
  prior permission.  The Smithsonian Astrophysical Observatory makes
  no representations about the suitability of this software for any
  purpose.  It is provided "as is" without express or implied
  warranty.  THE SMITHSONIAN INSTITUTION AND THE SMITHSONIAN ASTRO-
  PHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES WITH REGARD TO THIS
  SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANT- ABILITY AND
  FITNESS, IN NO EVENT SHALL THE SMITHSONIAN INSTITUTION AND/OR THE
  SMITHSONIAN ASTROPHYSICAL OBSERVATORY BE LIABLE FOR ANY SPECIAL,
  INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER
  RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
  CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
  CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

/******************************************************************************/

package gov.sao.asc.obsed.view.form;

/******************************************************************************/

import gov.sao.asc.obsed.Editor;
import gov.sao.asc.obsed.database.RecordInsertionException;
import gov.sao.asc.util.LogClient;
import javax.swing.JOptionPane;

/******************************************************************************/
/**
 * Shows the target table plus in a form view.
 *
 * @author Paul Michael Reilly
 *
 * @version %I%, %G%
 */

public class HRCNewView extends NewView 
{
  /****************************************************************************/
  /**
   * Construct a view which will be used to create a new HRC record.
   */

  public HRCNewView()
  {
    super();
  }

  /****************************************************************************/
  /**
   *  Insert a new record into the database.
   */

  public void newRecord()
  {
    System.out.println( "Inserting a new record into the hrcparam table." );

    // Get the number of existing rows in this table.
    int rowCount = model.getRowCount( "hrcparam" );

    try
    {
      // Insert a new record, or get the row number for the matching entry.
      int newRecordNumber = database.createNewHRCParamRecord( newKeyValue );

      // Assert: newRecordNumber != -1.
      if ( newRecordNumber < rowCount )
      {
	JOptionPane.showMessageDialog( this,
				       "This HRC parameter set already " + 
				       "exists as HRC ID " + 
				       newRecordNumber + ".",
				       "Table Insertion Error",
				       JOptionPane.INFORMATION_MESSAGE );
      }
    }
    catch ( RecordInsertionException exception )
    {
      String message = "HRC Param table insertion failed.";
      LogClient.logMessage( message );
      JOptionPane.showMessageDialog( this, message, 
				     "Table Insertion Error",
				     JOptionPane.INFORMATION_MESSAGE );
    }
  }

  /****************************************************************************/

}

/******************************************************************************/
