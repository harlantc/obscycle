/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"

/* Routines added 1997 Jun 24 */

/*
 * Process each line of an ASCII file with the function 
 * process_line, except for blank lines and lines whose first
 * character is #. This function must have a single char*
 * argument which is the input line it will process.
 * It returns void. Input lines have tabs converted to spaces
 * before they are sent to process_line.
 * The idea of this routine is that many operations involve
 * processing ASCII files on a line-by-line basis, except for
 * some comment lines, so this code fragment is repeated
 * pretty often.
 */
void utn_fio_process_file( char* filename, void (*process_line)( char* ) )
{
 TextBuf line;
 char cmt;
 FioFile file;
 cmt = utn_esc_special_char( CMT_CHAR );
 file =  utn_fio_open_ar( filename );
 if ( !file) {
   TextBuf tmp;
   snprintf( tmp, UT_TEXT_SIZE, "Failed to open process-file %s", filename );
   utn_msg_d( UT_VERBOSE, tmp ); 
   return; 
 }
 while( utn_fio_read_line( file, line, TEXT_SIZE ) ) {
  utn_cs_detab( line );
  if ( !utn_cs_is_blank( line ) && line[0] != cmt ) {
   process_line( line );
  }
 }
 utn_fio_file_close( file );
}

/*
 * A variation on the previous function, with an extra void* argument
 * in both this call and the process_line function.
 * Typical example:
 *   process_file1( myfile, myfunction, mode );
 * ...
 *   void myfunction( char* mode, char* line );
 */
void utn_fio_process_file1( char* filename, void (*process_line)( void*, char* ), void* ptr )
{
 TextBuf line;
 FioFile file;
 char cmt;
 cmt = utn_esc_special_char( CMT_CHAR );
 file =  utn_fio_open_ar( filename );
 if ( !file) { 
   TextBuf tmp;
   snprintf( tmp, UT_TEXT_SIZE, "Failed to open process-file %s", filename );
   utn_msg_d( UT_VERBOSE, tmp ); 
   return;
 }
 while( utn_fio_read_line( file, line, TEXT_SIZE ) ) {
  utn_cs_detab( line );
  if ( !utn_cs_is_blank( line ) && line[0] != cmt ) {
   process_line( ptr, line );
  }
 }
 utn_fio_file_close( file );
}


