/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

#include "jcmstate.h"


/*
 */
void utn_fio_list_free( FioFileList list )
{
 utn_stack_free( list->data );
 utn_fio_list_free_path( list );
 utn_fio_iostack_free( list->iofs );
 utn_fio_pager_free( list->pager );
 free( list );
}


FioFileList utn_fio_list_init( void )
{
 GenError error = NULL;
 integer i;
 FioFileList fiolist = NULL;
 FioFile stdfiles[3];
 GenStack drivers;
 GenStack files;
 long nfiles = 3;
 fiolist = calloc( 1, sizeof( struct FioFileList_s ));
 files = utn_stack_alloc();
 fiolist->data = files;
 drivers = utn_fio_std_drivers();
 fiolist->drivers = drivers;
/* Initialize the first few files */
 utn_fio_set_umask( fiolist, 0644 );
 stdfiles[0] = utn_fio_open_gen( drivers, NULL, "stdin",  "TTY IN", 0, &error );
 stdfiles[1] = utn_fio_open_gen( drivers, NULL, "stdout", "TTY OUT",0, &error );
 stdfiles[2] = utn_fio_open_gen( drivers, NULL, "stderr", "TTY ERR",0, &error );

 for ( i = 0; i < nfiles; i++ )
  utn_fio_list_add( files, stdfiles[i] );

/* Initialize remaining attributes */
 utn_fio_list_set_retry( fiolist, UT_FALSE );
 fiolist->pager = utn_fio_pager_init();
 fiolist->iofs =  utn_fio_iostack_init();
 utn_fio_iostack_reinit( fiolist, fiolist->iofs );
 utn_fio_list_set_path( fiolist, NULL );
 return fiolist;
}



/*
 *--------------------------------------------------------------------------------
 * RETRY
 *--------------------------------------------------------------------------------
 */


void utn_fio_list_set_retry( FioFileList fiolist, logical q )
{
 if ( fiolist ) fiolist->retry = q;
} 


logical utn_fio_list_get_retry( FioFileList fiolist )
{
 if ( fiolist )
  return fiolist->retry;
 return UT_FALSE;
}

/*
 *--------------------------------------------------------------------------------
 * Access to JCM FILE parameters
 *--------------------------------------------------------------------------------
 */
  


long utn_fio_list_n( FioFileList f )
{
 return( utn_stack_size( f->data ));
}

/*
 *  The value fid = 0 and file=NULL is used for stdin/stdout, and is valid data.
 */


FioFile utn_fio_list_get_file( FioFileList f, long fid )
{
 /* This routine uses utn_fio_file static storage */
 if ( fid > 0 ) {
  return( utn_stack_entry( f->data, fid ));
 } else {
  return( NULL );
 }
}


logical utn_fio_list_any_eof( FioFileList f, long mode )
{

 logical eof;
 long fid;
 FioFile file;
 integer n;
 GenStack files = f->data;
 eof = UT_FALSE;
 n = utn_stack_size( files );
 for ( fid = 1; fid <= n; fid++ ) {
  file = utn_stack_entry( files, fid );
  if ( file->status == UT_FILE_EOF ) {
   eof = UT_TRUE;
   if ( mode == 0 ) utn_fio_file_clear_eof( file );
  }
 }
 return( eof );
}


FioFile utn_fio_list_tty_in( FioFileList f )
{
 FioFile file;
 GenStack files = f->data;
 if ( !f ) return NULL;
 file = utn_stack_entry( files, FIO_TTY_IN );
 return file;
}

FioFile utn_fio_list_tty_out( FioFileList f )
{
 FioFile file;
 GenStack files = f->data;
 if ( !f ) return NULL;
 file = utn_stack_entry( files, FIO_TTY_OUT );
 return file;
}


FioFile utn_fio_list_tty_err( FioFileList f )
{
 FioFile file;
 GenStack files = f->data;
 if ( !f ) return NULL;
 file = utn_stack_entry( files, FIO_TTY_ERR );
 return file;
}



long utn_fio_list_find_free_fid( GenStack files )
{
 long fid;
 FioFile file;
 for ( fid = 1; fid <= files->n; fid++ )
 {
  file = files->data[fid-1];
  if ( file && file->status == UT_FILE_CLOSED ) return fid;
  if ( !file ) return fid;
 }
 return 0;
}

long utn_fio_list_add( GenStack files, FioFile file )
{
 long fid = utn_fio_list_find_free_fid( files );
 if ( fid == 0 )
  fid = utn_stack_push( files, file );
 else
  utn_stack_set( files, fid, file );
 if ( file )
  file->fid = fid;
 return fid;
}
