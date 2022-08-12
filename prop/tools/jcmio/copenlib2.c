/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"

/*----------------------------------------------------------------------*/
/*
 * utn_fio_open_list
 *
 * List all open files
 * Uses:
 *  <stdio.h>: sprintf
 *  csproto.h: utn_cs_copy
 *  fileproto.h: utn_fio_file_name, utn_fio_get_type, utn_fio_file_status utn_fio_file_n
 *  msg
 */

void utn_fio_list( void ) 
{
 utn_fio_list_print( NULL );
}

void utn_fio_list_print( FioFileList f ) 
{
 long n;
 long file_no;
 Filename name;
 TextWord type;
 long status;
 TextBuf buf;
 TextWord eof;
 logical qopen;
 long pos, line, recl;
 long istatus;
 long cfd;

 FioFile file;

 if ( !f ) f = utn_fio_get_list();
 n = utn_fio_list_n( f );
 utn_fio_msg( " " );
 utn_fio_dashline( 40 );
 utn_fio_msg( " JCMLIB File IO System (FIO) Status " );
 utn_fio_dashline( 40 );
 utn_fio_msg( "  File  FD  Line  Cpos  Recl  IOS  EOF  Type        Name " );
 for ( file_no = 1; file_no <= n; file_no++ ) {
  file = utn_fio_list_get_file( f, file_no );
  utn_fio_file_get_name( file, name, FN_SIZE );
  utn_fio_file_get_type( file, type, WORD_SIZE );
  cfd = utn_fio_file_get_cfd( file );
  status = utn_fio_file_status( file, FIO_CHECK );
  qopen = status != UT_FILE_CLOSED;
  if ( qopen ) {
   line = utn_fio_file_get_lineno( file );
   pos  = utn_fio_file_get_pos( file );
   recl = utn_fio_file_get_recl( file );
   if ( status == UT_FILE_EOF ) {
    istatus = -1;
    utn_cs_copy( "EOF", eof );
   } else {
    istatus = 0;
    utn_cs_copy( " ", eof );
   }
   snprintf( buf, UT_TEXT_SIZE, "  %-6ld%-4ld%-6ld%-6ld%-6ld%-6ld%-4s%-12s%s",
    (long)file_no, (long)cfd, (long)line, (long)pos, (long)recl, (long)istatus, eof, type, name );
   utn_fio_msg( buf ); 
  }
 }
}




FioFile utn_fio_open_file( const char* filename, char* mode )
{
 FioFileList list = utn_fio_get_list();
 return utn_fio_list_open_file( list, filename, mode );
}

FioFile utn_fio_open_raw( void )
{
 return utn_fio_open_file( NULL, "CIO RAW" ); 
}

FioFile utn_fio_open_ar( const char* filename )
{
 return utn_fio_open_file( filename, "SEQ R" ); 
}

FioFile utn_fio_open_sr( const char* filename )
{ 
 return utn_fio_open_file( filename, "SIO R" );
}

FioFile utn_fio_open_sw( const char* filename )
{
 return utn_fio_open_file( filename, "SIO W" );
}

FioFile utn_fio_open_srw( const char* filename )
{
 return utn_fio_open_file( filename, "SIO RW" );
}

FioFile utn_fio_open_aw( const char* filename )
{
 return utn_fio_open_file( filename, "SEQ W" );
}

FioFile utn_fio_open_aa( const char* filename )
{
 return utn_fio_open_file( filename, "SEQ A" );
}

FioFile utn_fio_open_crw( const char* filename )
{
 return utn_fio_open_file( filename, "CIO RW" );
}

FioFile utn_fio_open_cr( const char* filename )
{
 return utn_fio_open_file( filename, "CIO R" );
}

FioFile utn_fio_open_cw( const char* filename )
{
 return utn_fio_open_file( filename, "CIO W" );
}


