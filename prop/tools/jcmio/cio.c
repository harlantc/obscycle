/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include "tpiolib.h"
#include "fiolist.h"


/*
 *  These routines use the fcntl.h file access mechanism
 */

/* Generic replacement for open call with flags */
FioFile utn_fio_open_crm( FioFileList list, char* filename,  mode_t mode )
{
  FioFile file;
  utn_fio_set_umask( list, mode );
  file = utn_fio_open_file( filename, "CIO R" );
  utn_fio_set_umask( list, 0644 );
  return file;
}



FioFile utn_fio_open_cwm( FioFileList list, char* filename,  mode_t mode )
{
  FioFile file;
  utn_fio_set_umask( list, mode );
  file = utn_fio_open_file( filename, "CIO W" );
  utn_fio_set_umask( list, 0644 );
  return file;
}


/*
 *  Routines to read and write bytes from a CIO file.
 */

logical utn_fio_read_bytes( FioFile file, char* buf, long n, long* m )
{
 long fd;
 fd = utn_fio_file_get_cfd( file );
 *m= read( fd, buf, n );
 return( *m == n );
}

logical utn_fio_write_bytes( FioFile file, char* buf, long n, long* m )
{
 long fd;
 fd = utn_fio_file_get_cfd( file );
 *m= write( fd, buf, n );
 return( *m == n );
}

/*
 *  Wrap POSIX isatty
 */
logical utn_fio_file_tty( FioFile file )
{
 long fd;
 if ( file ) {
  fd = utn_fio_file_get_cfd( file );
  return( isatty( fd ) );
 } else {
  return( isatty( 0 ) );
 }
}

/*
 *  Open a raw mode file. Used for graphics cursors.
 */


logical utn_fio_have_raw( GenStack files )
{
 logical q;
 FioFile file = utn_stack_entry( files, FIO_TTY_IN );
 q = file->flags[FIO_FLAG_CTTY];
 return q;
}


/* Read a single byte. Usually used in raw mode */

long utn_fio_read_byte( FioFile file )
{
 logical status;
 char buf;
 long fd;
 long byte;
 int nr;
 fd = utn_fio_file_get_cfd( file );
 if ( file->resync )
  file->resync( file->resync_driver, fd );
 nr =  read( fd, &buf, 1 );
 status = ( nr == 1 );
 if ( status ) {
  byte  = (long) ( buf & UT_CHAR_MASK );
 } else {
  byte = -1;
 }
 return( byte );
}

void utn_fio_raw_set_resync( FioFile file, void (*resync)( void*, int ), void* driver )
{
 file->resync = resync;
 file->resync_driver = driver;
}

/*
 * Read a byte from a raw file. Used to get a cursor value.
 */
long utn_fio_read_raw( FioFile file )
{

 long c;

 if ( !file ) {
  c = UT_CHAR_ERR;
 } else {
  c = utn_fio_read_byte( file );
  if ( c < 0 ) { 
   utn_fio_file_close( file ); 
  } else if ( c == UT_CHAR_CTRLC ) {
   utn_fio_tmsg( "CTRL/C seen but ignored" );
  }
 }
 return( c );
}

/* Read a line terminated by CR */

logical utn_fio_read_line_raw( FioFile file, char* line, integer maxlen )
{
 char c = 0;
 integer i = 0;
 while ( c >= 0 && c != '\n' && i < maxlen )
 {
  c = utn_fio_read_byte( file );
  if ( c != '\n' )
  {
   line[i] = c;
   i++;
  }
  fputc( c, stdout );
  fflush( stdout );
 }
 line[i] = 0;
 return ( c >= 0 ); /* False on EOF */
}

logical utn_fio_tty( FioFile file )
{
 int fd;
 FILE* fid;
 logical q;
 if ( file )
  fid = file->fptr;
 else
  fid = stdin;

 fd = fileno( fid );
 q = isatty( fd );
 return q;
}
