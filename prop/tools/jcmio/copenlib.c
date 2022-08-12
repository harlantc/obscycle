/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"
#include <string.h>


/*----------------------------------------------------------------------*/
/* 
 * utn_fio_read_line
 *
 * Read a line from an ASCII file.
 * Uses  stdin and type FILE*  from <stdio.h>
 * Uses fgets from <stdio.h>
 * Uses fileproto.h: utn_fio_file_fptr and utn_fio_set_eof
 * Uses csproto.h: utn_cs_null and utn_cs_remove_nl from csproto.h
 */



logical utn_fio_write_line( FioFile file, const char* buf )
{
 logical ios;
 if ( !file ) {
  ios = utn_fio_tty_write( buf );
 } else if ( buf == NULL ) {
  if( utn_fio_file_get_pos( file ) > 0 ) {
   ios = utn_fio_put_line_n( file, " ", UT_TRUE, UT_TRUE, 0 );
  } else {
   ios = UT_TRUE;
  }
 } else {
  ios = utn_fio_put_line_n( file, buf, UT_TRUE, UT_TRUE, 0 );
 }
 return( ios );
}

logical utn_fio_write_text_n( FioFile file, char* buf, integer n )
{
 logical ios;
 ios = utn_fio_put_line_n( file, buf, UT_FALSE, UT_FALSE, n );
 return ios;
}


logical utn_fio_write_text( FioFile file, char* buf )
{
 logical ios;
 if ( !file ) {
  ios = utn_fio_tty_writel( buf );
 } else {
  ios = utn_fio_put_line_n( file, buf, UT_FALSE, UT_FALSE, 0 );
 }
 return( ios );
}


long utn_fio_terminate_buf( long pos, const char* buf, char* buf2, logical strip, logical nl, long siz )
{
 long i;
 if ( strip ) {
  i = min_i( utn_cs_ends( buf ), siz-1 );
 } else {
  i = min_i( strlen( buf ), siz-1 );
 }
 utn_cs_copy_siz( buf, buf2, i );
 if( nl ) {
  buf2[i++] = '\n';
  pos = 0;
 } else {
  pos += i;
 }
 buf2[i] = '\0';
 return( pos );
}


logical utn_fio_read_line( FioFile file, char* line, long lmax )
{
 logical ios;
 if ( !file ) {
  ios = utn_fio_tty_read( line, lmax );   
 } else {
  ios = utn_fio_get_line( file, line, lmax );  
  utn_fio_set_status_pos( file, ios, 0 );
 }
 return( ios );
}



logical utn_fio_read_line_alloc( FioFile file, char** line, long* lmax )
{
 logical ios;
 if ( !file ) {
  ios = utn_fio_tty_read( *line, *lmax );   
 } else {
  ios = utn_fio_get_line_alloc( file, line, lmax, UT_TRUE );  
  utn_fio_set_status_pos( file, ios, 0 );
 }
 return( ios );
}

/* Interactively let the user try to type the filename correctly this time */

logical utn_fio_test_retry( FioFile tty_err, char* fullname )
{
 logical loop = UT_TRUE;
 TextBuf tmp;
    utn_fio_write_text( tty_err, "OPEN RETRY: Try again, full filename (Q to quit): " );
    utn_fio_get_line( NULL, fullname, FN_SIZE );
    if ( utn_cs_eq( fullname, "Q" ) || utn_cs_eq( fullname, "q" ) ) {
     loop = UT_FALSE;
     utn_cs_copy( " ", fullname );
     snprintf( tmp, UT_TEXT_SIZE, "OPEN RETRY: Abandoning attempt to open file %s", fullname );
     utn_fio_write_line( tty_err, tmp );
    }
 return loop;
}
