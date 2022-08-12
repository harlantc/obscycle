/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include <sys/types.h>
#include <unistd.h>
#include <sys/stat.h>
#include "tpiolib.h"
#include "fio.h"
#include <string.h>



long utn_fio_file_get_lineno( FioFile file )
{
 return( file->line );
}


FILE* utn_fio_file_get_fptr( FioFile file )
{
/*
 * This routine uses the utn_fio_file structure definition.
 */
 if ( file )
  return( file->fptr );
 else 
  return( stdout );


}




/*----------------------------------------------------------------------*/
/*
 *  utn_fio_test_eof
 *
 *  Test if file status to EOF
 *
 */
logical utn_fio_file_test_eof( FioFile file )
{
 if ( file )
  return ( file->status == UT_FILE_EOF );
 else
  return ( UT_FALSE ); 
}

logical utn_fio_file_mode_eof( FioFile file, long mode )
{
 return ( utn_fio_file_status( file, mode ) == UT_FILE_EOF );
}

/*
 *--------------------------------------------------------------------------------
 */
/*
 *  Test whether the file exists.
 */
logical utn_fio_inquire( const char* name )
{
 struct stat stat_var;
 return( stat( name, &stat_var ) == 0 );
}

/*
 *--------------------------------------------------------------------------------
 */
/*----------------------------------------------------------------------*/
/*
 *  utn_fio_clear_eof
 *
 *  Set file status to Open
 *
 */


void utn_fio_file_clear_eof( FioFile file )
{
/*
 * Set EOF status
 */
 FILE* fptr;
 if ( file ) {
  file->status = UT_FILE_OPEN;
  fptr = file->fptr;
  if ( fptr != NULL ) clearerr( fptr );
 }
}
 
void utn_fio_file_set_pos( FioFile file, long pos )
{
 if ( file ) 
  file->pos = pos;
}

 
/*----------------------------------------------------------------------*/
/*
 * utn_fio_get_name
  * Return the name of the file
 *
 */

void utn_fio_file_get_name( FioFile file, char* name, const long maxlen )
{
/*
 * This routine uses the utn_fio_file structure definition.
 */
 if ( file ) {
  utn_cs_copy_siz( file->name, name, maxlen );
 } else {
  utn_cs_copy( " ", name );
 }
}

/*
 *--------------------------------------------------------------------------------
 */

long utn_fio_file_get_recl( FioFile file )
{
 if ( file ) {
  return( file->recl );
 } else {
  return( 0 );
 }
}
void utn_fio_file_set_recl( FioFile file, long recl )
{
 if ( file ) 
  file->recl = recl;
}

long utn_fio_file_fid( FioFile file )
{
 if ( file )
  return file->fid;
 else
  return 0;
}


logical utn_fio_file_interactive( FioFile file )
{
 logical q;
 q = !file || file->flags[FIO_FLAG_TTY];
 return q;
}

logical utn_fio_file_close( FioFile file )
{
 FioDriver fio = NULL;
 if ( file )
  fio = file->driver;
 if ( fio )
  return fio->close( file );
 else
  return UT_TRUE;
}


logical utn_fio_file_close_cio( FioFile file )
{
 integer fd;
 int fst = 0;
 if ( file )
 {
   fd = utn_fio_file_get_cfd( file );
   fst = close( fd );
   utn_fio_file_clear( file );
 }
 return ( fst == 0 );
}

logical utn_fio_file_close_null( FioFile file )
{
 utn_fio_file_clear( file );
 return UT_TRUE;
}


logical utn_fio_file_close_seq( FioFile file )
{
 int fst = 0;
 if ( file )
 {
   fst = fclose( file->fptr );
   utn_fio_file_clear( file );
 }
 return ( fst == 0 );
}

logical utn_fio_file_close_raw( FioFile file )
{
 integer fd;
 int fst = 0;
 if ( file )
 {
   fd = utn_fio_file_get_cfd( file );
   utn_cio_clear_raw( file->aux, fd );
   fst = close( fd );
   utn_fio_file_clear( file );
 }
 return ( fst == 0 );
}



void utn_fio_file_flush( FioFile file )
{
 FILE* fptr = NULL;
 if ( file )
  fptr = file->fptr;

 if ( !fptr )
  fptr = stdout;

 fflush( fptr );
}

void utn_fio_file_set_raw( FioFile file )
{
 if ( file )
 {
  file->flags[FIO_FLAG_RAW] = UT_TRUE;
  file->flags[FIO_FLAG_TTY] = UT_TRUE;
 }
}

logical utn_fio_file_raw( FioFile file )
{
 if ( file )
  return file->flags[FIO_FLAG_RAW];
 else
  return UT_FALSE;
}

logical utn_fio_get_line_alloc_std( FioFile file, char** bufp, long* sizp, logical dynamic );
logical utn_fio_get_line_extend( FioFile file, char** bufp, long* sizp );
logical utn_fio_get_line1( FioFile file, char* buf, long siz, logical* nl );

/*
 *  Read a line using fgets. If no error, remove trailing newline.
 *  If error, return is the blank buffer. Return value is TRUE if 
 *  no error.
 */
logical utn_fio_get_line( FioFile file, char* buf, long siz )
{
 logical nl = 0;
 if ( file && file->flags[FIO_FLAG_RAW] )
  return utn_fio_read_line_raw( file, buf, siz );
 else if ( file && file->flags[FIO_FLAG_MAP] )
  return utn_fio_read_line_map( file, buf, siz );
 else
  return utn_fio_get_line1( file, buf, siz, &nl );
}




/* Read a line. Remove trailing \n if present and set nl accordingly. 
   Return false if file read failed 
 */
logical utn_fio_get_line1( FioFile file, char* buf, long siz, logical* nl )
{
  logical ios;
  FILE* fptr;
  long status;
  char* fst;
  if ( file ) 
   fptr = file->fptr;
  else
   fptr = stdin;
  fst = fgets( buf, siz, fptr );
  if ( feof( fptr ))
   status = UT_FILE_EOF;
  else if ( ferror( fptr ) )
   status = UT_FILE_ERR;
  else if ( !fst )
   status = UT_FILE_ERR;
  else
   status = UT_FILE_OPEN;
  if ( file )
   file->status = status; 
  ios = ( status == UT_FILE_OPEN );
  if ( ios )
   *nl = utn_cs_remove_nl( buf );
  else
  {
   *nl = UT_TRUE;  /* Consider error status to imply a newline */
   utn_cs_copy( " ",  buf );
  }
  return ios;
}

logical utn_fio_get_line_alloc( FioFile file, char** bufp, long* sizp, logical dynamic )
{
 if ( file && file->flags[FIO_FLAG_RAW] )
  return utn_fio_read_line_raw( file, *bufp, *sizp );
 else
  return utn_fio_get_line_alloc_std( file, bufp, sizp, dynamic );  
}

logical utn_fio_get_line_alloc_std( FioFile file, char** bufp, long* sizp, logical dynamic )
{
 logical nl = UT_FALSE;

 if ( dynamic )
  return utn_fio_get_line_extend( file, bufp, sizp );
 else
  return utn_fio_get_line1( file, *bufp, *sizp, &nl );
}

logical utn_fio_get_line_extend( FioFile file, char** bufp, long* sizp )
{
 logical ios;
 char* buf = *bufp;
 char* ptr = buf;
 long siz = *sizp;
 long nmax = siz;
 logical nl = UT_FALSE;

 while( !nl )
 {
  ios = utn_fio_get_line1( file, ptr, siz, &nl );
  if ( !nl ) {
/* Extend buffer and point to new extent */
   ptr = buf + nmax;
   siz = nmax;
   nmax *= 2;
   buf = realloc( buf, nmax );
   *bufp = buf;
   *sizp = nmax;
  } 
 }
 return( ios );
}


logical utn_fio_put_line_n( FioFile file, const char* buf, logical strip, logical nl, long size )
{
 const long siz = 1023;
 char buf2[1024];
 char* ptr;
 logical ios;
 long pos;
 FILE* fptr;
 long clen;
 if( !file || !file->fptr ) {
  fptr = stdout;
  pos = 0;
 } else {
  fptr = file->fptr; 
  pos = file->pos;
 }
 ios = UT_TRUE;
 ptr = (char*)buf;
 clen = size;
 if ( clen == 0 )
  clen = utn_cs_ends( buf );
 while( clen > siz && ios ) 
 {
  utn_cs_copy_siz( ptr, buf2, siz );
  ios = ( fputs( buf2, fptr ) >= 0 );
  ptr  += siz;
  clen -= siz;
 }
 pos = utn_fio_terminate_buf( pos, ptr, buf2, strip, nl, siz );
 ios = ( fputs( buf2, fptr ) >= 0 ); /* TRUE if good */
 if ( ios ) ios = ( ferror( fptr ) == 0 ) && ( feof( fptr ) == 0 );
 utn_fio_set_status_pos( file, ios, pos ); 
 return( ios );
}

/*
 *--------------------------------------------------------------------------------
 */

void utn_fio_file_set_type( FioFile file, char* type )
{
 if ( file ) 
  utn_cs_copy( type, file->type );
}

/*
 *--------------------------------------------------------------------------------
 */
void utn_fio_file_get_type( FioFile file, char* type, const long maxlen )
{

/*
 * This routine uses the utn_fio_file structure definition.
 */

 if ( file ) {
  utn_cs_copy_siz( file->type, type, maxlen );
 } else {
  utn_cs_copy( " ", type );
 }
}

/*
 *--------------------------------------------------------------------------------
 */
long utn_fio_file_get_pos( FioFile file )
{
 if ( file )
  return( file->pos );
 else 
  return( 0 );
}


/*
 * Convert a char* to a bit pattern for posix access function
 */

logical utn_fio_posix_access( const char* name, const char* mode )
{
  logical retval;
  int xmode = 0;

  xmode |= ( strpbrk(mode, "r") != NULL ? R_OK 	: 0 );  /* Read perm */
  xmode |= ( strpbrk(mode, "w") != NULL ? W_OK 	: 0 );  /* Write perm */
  xmode |= ( strpbrk(mode, "x") != NULL ? X_OK	: 0 );  /* Execute perm */
  xmode |= ( strpbrk(mode, "f") != NULL ? F_OK 	: 0 );  /* Existence  */

  retval = access( name, xmode );
  return ( retval == 0 );
}	


mode_t utn_file_mode( char* name )
{
 mode_t mode;
 struct stat stat_var;
 int status = stat( name, &stat_var );
 if ( status == 0 )
  mode = stat_var.st_mode;
 else
  mode = 0;

 return mode;
}

long utn_fio_stat_type( const char* name )
{
 struct stat stat_var;
 int status;
 mode_t mode;


 if ( !name ) return( FIO_NOFILE );

 status = stat( name, &stat_var );
 if ( status == 0 ) {
/*  Is it world accessible? */
  mode = stat_var.st_mode;
  if ( S_ISREG( mode ) || S_ISLNK( mode ) )
  {
   if ( mode & 0111 ) return FIO_FILE_EXE;

   return FIO_FILE_STD;
  } else if ( S_ISDIR( mode )) {
   return FIO_FILE_DIR;
  } else if ( S_ISCHR( mode )) {
   return FIO_FILE_CDEV;
  } else if ( S_ISBLK( mode )) {
   return FIO_FILE_BDEV;
  } else if ( S_ISFIFO( mode )) {
   return FIO_FILE_FIFO;
#if 1
/* Not defined in POSIX... */
  } else if ( S_ISSOCK( mode )) {
   return FIO_FILE_SOCKET;
#endif
  }
 }
 return (FIO_NOFILE );
}

/*
 *   Test whether you have access permission to the file.
 */
long utn_fio_access( const char* name )
{
 struct stat stat_var;
 int status;
 uid_t uid, fuid;
 gid_t gid, fgid;
 mode_t mode;

 logical r, w;

 if ( !name ) return( FIO_NOFILE );

 status = stat( name, &stat_var );
 if ( status == 0 ) {
/*  Is it world accessible? */
  mode = stat_var.st_mode;

  r = ( mode & S_IROTH );
  w = ( mode & S_IWOTH );
  if ( !r || !w ) {
/* Is it group accessible for your group? */
   fgid = stat_var.st_gid;
   gid = getegid();
   if ( gid == fgid ) {
    r = r || ( mode & S_IRGRP );
    w = w || ( mode & S_IWGRP );
   }
   if ( !r || !w ) { 
/*  Is it accessible for your effective UID? */
     uid = geteuid();
     fuid= stat_var.st_uid;
     if ( uid == fuid ) {
      r =  r || ( mode & S_IRUSR );
      w =  w || ( mode & S_IWUSR );
     }   
   }
  }
/* OK, now we know whether you can read or write to it. */
  if ( r && w ) {
   return( FIO_READWRITE );
  } else if ( r ) {
   return( FIO_READONLY );
  } else {
   return( FIO_NOPERM );
  }
 } else {
  return( FIO_NOFILE );
 }
} 

long utn_file_stat_size(  char* name )
{
 struct stat st;
 if ( stat( name, &st ) == 0 )
  return st.st_size;

 return -1;
}


/* 
 * Clears special bits in current file creation mask with & 0777 
 * Returns the previous mask value. 
 */
long utn_file_set_mask( void )
{
  mode_t save_mask;
  mode_t new_mask; 
  save_mask = umask(0);
  new_mask  = umask(save_mask);
  return (long)save_mask;
}

logical utn_fio_open_fifo( char* path, mode_t mode )
{   
 int fail;
 mode_t mmode;
 logical ok;
 mmode = ( mode & 07777 ) | S_IFIFO;
 fail = mknod( path, mmode, 0 );
 ok = !fail;
 return ok;
} 
 

void utn_fio_file_free( FioFile file )
{
 free( file->path );
 free( file );
}
