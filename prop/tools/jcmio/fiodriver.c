/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmstate.h"
#include "fiolist.h"
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>


void utn_fio_set_umask( FioFileList list, integer umask )
{
 list->umask = umask;
}

integer utn_fio_get_umask( FioFileList list )
{
 return list->umask;
}


FioDriver utn_fio_driver_select( FioFileList list, char* name )
{
 GenError error = NULL;
 GenStack dlist = list->drivers;
 return utn_fio_driver_search( dlist, name, &error );
}

FioFile utn_fio_alloc( void )
{
 FioFile file = calloc( 1, sizeof( FioFileObject ));
  utn_cs_copy( " ", file->name );
  utn_cs_copy( " ", file->type );
  file->status = UT_FILE_CLOSED;
  file->line = 0;
  file->pos = 0;
  file->recl = 0;
  file->stream = 0;
  file->fptr = (FILE*)NULL;
 return file;
}


FioFile utn_fio_create( FioDriver driver, char* name )
{

 FioFile file;
 file = utn_fio_alloc();
 file->driver = driver;
 utn_cs_copy( name, file->name );
 utn_fio_file_set_type( file, driver->name );
 return file;
}


FioDriver utn_fio_driver( FioFile file )
{
 return file->driver;
}


FioDriver utn_fio_driver_search( GenStack dlist, char* name, GenError* errorp )
{
 integer i, n;
 TextBuf mbuf;
 FioDriver fio = NULL;
 n = utn_stack_size( dlist );
 for ( i = 1; i <= n; i++ )
 {
  fio = utn_stack_entry( dlist, i );
  if ( utn_cs_eq( fio->name, name ))
   return fio;
 }
 /* Error: unknown mode */
 if ( n > 0 )
  fio = utn_stack_entry( dlist, 1 );   /* UNK driver */

 sprintf( mbuf, "Unknown file driver %s", name );
 *errorp = utn_error( mbuf );
 return fio;
}



GenStack utn_fio_std_drivers( void )
{
 FioDriver fio;
 FioDriver driver; /* driver definition */
 struct FioDriver_s drivers[] = {
  { "UNK",     UT_FALSE, "x", utn_fio_open_null,  utn_fio_file_close_null },
  { "TTY IN",  UT_TRUE,  " ", utn_fio_open_tty,  utn_fio_file_close_null },
  { "TTY OUT", UT_TRUE,  " ", utn_fio_open_tty,  utn_fio_file_close_null },
  { "TTY ERR", UT_TRUE,  " ", utn_fio_open_tty,  utn_fio_file_close_null },
  { "SEQ R",   UT_TRUE,  "r", utn_fio_open_seq,     utn_fio_file_close_seq },
  { "SEQ W",   UT_FALSE, "w", utn_fio_open_seq,     utn_fio_file_close_seq },
  { "SEQ A",   UT_TRUE,  "a", utn_fio_open_seq,     utn_fio_file_close_seq },
  { "SIO R",   UT_TRUE,  "r", utn_fio_open_seq,     utn_fio_file_close_seq },
  { "SIO W",   UT_FALSE, "w", utn_fio_open_seq,     utn_fio_file_close_seq },
  { "SIO RW",  UT_TRUE,  "r+", utn_fio_open_seq,     utn_fio_file_close_seq },
  { "CIO R",   UT_TRUE,  "cr", utn_fio_open_cio,     utn_fio_file_close_cio },
  { "CIO W",   UT_FALSE, "cw", utn_fio_open_cio,     utn_fio_file_close_cio },
  { "CIO RW",  UT_TRUE,  "crw",utn_fio_open_cio,     utn_fio_file_close_cio },
  { "CIO RAW", UT_FALSE, "crw",utn_fio_open_cio_raw, utn_fio_file_close_raw },
  { "MAP R",   UT_TRUE, "r",  utn_fio_open_map, utn_fio_file_close_map }
 };


 integer n = 15;
 integer i;
 GenStack dlist;
 dlist = utn_stack_alloc();
 for ( i = 0; i < n; i++ )
 {
  driver = &drivers[i];
  fio = utn_fio_driver_alloc( driver->name, driver->exist, driver->mode );
  fio->open = driver->open;
  fio->close = driver->close;
  utn_stack_push( dlist, (void*)fio );  
 }
 return dlist;
}


/* ---- */

FioDriver utn_fio_driver_alloc( char* name, logical exist, char* cmode )
{
 FioDriver fio = calloc( 1, sizeof ( struct FioDriver_s ));
 fio->name = utn_cs_dup( name );
 fio->exist = exist;
 utn_cs_copy_siz( cmode, fio->mode, 4 );
 return fio;
}


FioFile utn_fio_open_gen( GenStack drivers, NameList pathlist, char* filename, char* mode, integer umask, GenError* errorp )
{
 FioFile file = NULL;
 FioDriver fio;

 fio=  utn_fio_driver_search( drivers, mode, errorp );
 if ( fio && fio->open != NULL )
  file = fio->open( fio, pathlist, filename, umask, errorp );
 else
  *errorp = utn_error( "No driver open routine for file" );
 return file;
}

FioFile utn_fio_open_seq( FioDriver fio, NameList pathlist, char* filename, integer umask, GenError* errorp )
{
 GenError error = NULL;
 FioFile file;
 logical ok;
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
  file->fptr = fopen( file->path, fio->mode );
 if ( ok && file->fptr )
  {
   file->status = UT_FILE_OPEN;
   file->stream = fileno( file->fptr );
   file->flags[FIO_FLAG_CTTY] =  isatty( file->stream );
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

FioFile utn_fio_open_null( FioDriver fio, NameList pathlist, char* filename, integer umask, GenError* errorp )
{
 FioFile file = utn_fio_create( fio, filename );;

 if ( pathlist || umask ) *errorp = NULL;  /* suppress */

 file->path = NULL;
 return file;
}

FioFile utn_fio_open_tty( FioDriver fio, NameList pathlist, char* filename, integer umask, GenError* errorp )
{
 GenError error = NULL;
 char* names[] = { "stdin", "stdout", "stderr" };

 FioFile file;
 integer n = 3;
 integer i;

 if ( umask || pathlist ) { };  /* Suppress compiler; pathlist not used in this routine */

 file = utn_fio_create( fio, filename );
 file->path = NULL;
 file->flags[FIO_FLAG_TTY] = UT_TRUE;


 i = utn_ar_cmatch_c( file->name, names, n );
 if ( i >= 0 )
 {
/* The compiler complained when I tried to use stdin, stdout, stderr as array initializers */
   file->fptr = ( i == 0 ) ? stdin : ( i == 1 ) ? stdout : stderr;
   file->status = UT_FILE_OPEN;
   file->stream = fileno( file->fptr );
   file->flags[FIO_FLAG_CTTY] =  isatty( file->stream );
 } else {
   TextBuf mbuf;
   snprintf( mbuf, UT_TEXT_SIZE, "FIO OPEN: Failed to open TTY file %s", filename );
   error = utn_error( mbuf );
 } 
 *errorp = error;
 return file;
}

/* Open driver for CIO */

FioFile utn_fio_open_cio_raw( FioDriver fio, NameList pathlist, char* filename, integer umask, GenError* errorp )
{
 FioFile file;
 long fd;
 logical nocr = UT_FALSE;
 file = utn_fio_open_cio( fio, pathlist, filename, umask, errorp );
 if ( file && !(*errorp))
 {
  utn_fio_file_set_raw( file );
  fd = file->stream;
  file->aux = (void*) utn_fio_raw_init( fd, nocr );
 }
 return file;
}


FioFile utn_fio_open_cio( FioDriver fio, NameList pathlist, char* filename, integer umask, GenError* errorp )
{
 char* cmode = fio->mode;
 GenError error = NULL;
 long fd;
 long flags = 0;
 int mode = umask;
 FioFile file;

 file = utn_fio_create( fio, filename );
 file->path = utn_fio_expand_path( pathlist, filename, fio->exist );
 if ( !file->path ) 
 {
  TextBuf mbuf;
  snprintf( mbuf, UT_TEXT_SIZE, "File %s not found in path", filename );
  error = utn_error( mbuf );
 } else {

  if ( utn_cs_eq( cmode, "cw" ) ) 
  { 
   if ( utn_fio_inquire( file->path ))
    flags = O_WRONLY | O_TRUNC;
   else
    flags = O_CREAT | O_WRONLY | O_TRUNC;
  } else {
   flags = utn_cs_eq(cmode, "crw" ) ? O_RDWR : O_RDONLY;
  }

  fd = open( file->path, flags, mode );
  file->stream = fd;  
  if ( fd >= 0 )
  {
   file->status = UT_FILE_OPEN;
   file->flags[FIO_FLAG_CTTY] =  isatty( file->stream );
  } else {
   TextBuf mbuf;
   snprintf( mbuf, UT_TEXT_SIZE, "FIO OPEN: Failed to open file %s", file->path );
   error = utn_error( mbuf );
  } 
 }
 *errorp = error;
 return file;
}


FioFile utn_fio_list_open_file( FioFileList list, const char* filename, char* mode )
{
 logical retry;
 logical loop;
 Filename name;  /* buffer for interactively provided name */

 FioFile file = NULL;
 FioFile tty_err;
 GenStack drivers = list->drivers;
 GenStack files = list->data;
 NameList pathlist = list->pathlist;
 GenError error = NULL;
 integer umask = utn_fio_get_umask( list );
 if ( !list ) 
  list = utn_fio_get_list();

 tty_err = utn_fio_list_tty_err( list );
 retry = utn_fio_list_get_retry( list );

 if ( utn_cs_is_blank( filename )  || utn_cs_eq( filename, "*" ) ) {
  if ( !utn_cs_eq( mode, "CIO RAW") )
   return NULL;  
 }

 if ( utn_cs_eq( mode, "CIO RAW")&&  !utn_fio_have_raw( files )) 
 {
  error = utn_error("Failed to open raw mode: stdin is not tty" );
  return NULL;
 }

 if (! utn_cs_is_blank( filename ) ) {
  utn_cs_remove_quotes_and_trail( filename, name, FN_SIZE ); /* C has problems if the name has trailing spaces */
  loop = UT_TRUE;
  while ( !file && loop ) 
  {
   file = utn_fio_open_gen( drivers, pathlist, name, mode, umask, &error );
   if ( !file && retry )
    loop = utn_fio_test_retry( tty_err, name );   
   else
    loop = UT_FALSE;
  }
 }
 if ( file )
  utn_fio_list_add( files, file );
 
 return( file );
}



