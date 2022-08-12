/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "tpiolib.h"
#include "fio.h"
FioFile utn_fio_map_ar( const char* filename )
{
 return utn_fio_open_file( filename, "MAP R" ); 
}

FioFile utn_fio_open_map( FioDriver fio, NameList pathlist, char* filename, integer umask, GenError* errorp )
{
 GenError error = NULL;
 FioFile file;
 logical ok;
 
 FileBuffer buffer;


 if ( umask ) { };  /* umask not used */
 file = utn_fio_create( fio, filename );
 file->path = utn_fio_expand_path( pathlist, filename, fio->exist );
 ok = ( file->path != NULL );
 if ( !ok )
 {
  TextBuf mbuf;
  snprintf( mbuf, UT_TEXT_SIZE, "File %s not found in path", filename );
  *errorp = utn_error( mbuf );
   utn_fio_file_free( file );
   return NULL;
 }
 if ( ok )
 {
  buffer = utn_fio_file_buffer_alloc( 0,0,0 );
  ok = utn_fio_file_buffer_read( filename, buffer );
  file->aux = (void*)buffer;
  file->flags[FIO_FLAG_MAP] = UT_TRUE;
 }
 if ( ok  )
  {
   file->status = UT_FILE_OPEN;
   file->stream = 0;
   file->flags[FIO_FLAG_CTTY] =  0;
  } else {
   TextBuf mbuf;
   snprintf( mbuf, UT_TEXT_SIZE, "FIO OPEN: Failed to open file %s", file->path );
   utn_fio_file_free( file );
   file = NULL;
   error = utn_error( mbuf );
  } 
 *errorp = error;
 return file;
}


logical utn_fio_file_close_map( FioFile file )
{
 int fst = 0;
 FileBuffer b;
 if ( file )
 {
/* Replace this with file buffer free */
   b = (FileBuffer)file->aux;
   utn_fio_file_buffer_free( b );
   utn_fio_file_clear( file );
 }
 return ( fst == 0 );
}

void utn_fio_rewind_map( FioFile file )
{
 FileBuffer b = (FileBuffer)file->aux; 
 utn_fio_file_buffer_seek( b, 1 );
}
  
logical utn_fio_read_line_map( FioFile file, char* buf, long siz )
{
 FileBuffer b = (FileBuffer)file->aux;
 return utn_fio_file_buffer_read_line( b, buf, siz );
}

logical utn_fio_seek_map( FioFile file, integer pos )
{
 FileBuffer b = (FileBuffer)file->aux; 
 return utn_fio_file_buffer_seek( b, pos );

}

