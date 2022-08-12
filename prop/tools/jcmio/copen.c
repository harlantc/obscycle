/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"
#include "fio.h"

long utn_fio_file_status( FioFile file, long mode )
{
  long status = 0;
  if ( file )
  {
   status = file->status;
   if ( mode == FIO_CLEAR ) file->status = UT_FILE_OPEN;
  }
  return( status );
}



void utn_fio_set_status_pos( FioFile file, logical ios, long pos ) 
{
 if ( !file ) return;
 utn_fio_file_set_status( file, ios );
 if ( ios ) {
  file->line++;
  utn_fio_file_set_pos( file, pos );
 }
}

void utn_fio_file_set_status( FioFile file, logical status )
{
 if ( file )
  file->status = status ? UT_FILE_OPEN : UT_FILE_EOF;
}


void utn_fio_delta_pos( FioFile file, long pos )
{
 if ( file ) 
  file->pos += pos;
}

void utn_fio_file_rewind( FioFile file )
{
 if ( file->flags[FIO_FLAG_MAP] )
 {
  utn_fio_rewind_map( file );
 }
 else if ( !utn_fio_file_interactive( file ) )
  rewind( file->fptr );
 utn_fio_line_reset( file );
}

void utn_fio_line_reset( FioFile file )
{
 file->pos = 0;
 file->line= 0;
}


/*----------------------------------------------------------------------*/
/*
 *  utn_fio_file_clear
 *
 */


void utn_fio_file_clear( FioFile file )
{
/*
 * This routine uses the utn_fio_file structure definition.
 */
 if ( file ) 
 {
  file->status = UT_FILE_CLOSED;
  file->fptr = NULL;
  file->stream = 0;
  utn_fio_line_reset( file );
 }
}

long utn_fio_file_get_cfd( FioFile file )
{
 return( file->stream );
}


